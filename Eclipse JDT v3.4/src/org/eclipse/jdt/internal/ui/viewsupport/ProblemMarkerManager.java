/*******************************************************************************
 * Copyright (c) 2000, 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jdt.internal.ui.viewsupport;


import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.ListenerList;
import org.eclipse.core.runtime.Status;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IMarkerDelta;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;

import org.eclipse.swt.widgets.Display;

import org.eclipse.jface.text.source.AnnotationModelEvent;
import org.eclipse.jface.text.source.IAnnotationModel;
import org.eclipse.jface.text.source.IAnnotationModelListener;
import org.eclipse.jface.text.source.IAnnotationModelListenerExtension;

import org.eclipse.ui.progress.UIJob;

import org.eclipse.jdt.internal.ui.JavaPlugin;
import org.eclipse.jdt.internal.ui.JavaUIMessages;
import org.eclipse.jdt.internal.ui.javaeditor.CompilationUnitAnnotationModelEvent;
import org.eclipse.jdt.internal.ui.util.SWTUtil;

/**
 * Listens to resource deltas and filters for marker changes of type IMarker.PROBLEM
 * Viewers showing error ticks should register as listener to
 * this type.
 */
public class ProblemMarkerManager implements IResourceChangeListener, IAnnotationModelListener , IAnnotationModelListenerExtension {

	/**
	 * Visitors used to look if the element change delta contains a marker change.
	 */
	private static class ProjectErrorVisitor implements IResourceDeltaVisitor {

		private HashSet fChangedElements; 
		
		public ProjectErrorVisitor(HashSet changedElements) {
			fChangedElements= changedElements;
		}
			
		public boolean visit(IResourceDelta delta) throws CoreException {
			IResource res= delta.getResource();
			if (res instanceof IProject && delta.getKind() == IResourceDelta.CHANGED) {
				IProject project= (IProject) res;
				if (!project.isAccessible()) {
					// only track open Java projects
					return false;
				}
			}
			checkInvalidate(delta, res);
			return true;
		}
		
		private void checkInvalidate(IResourceDelta delta, IResource resource) {
			int kind= delta.getKind();
			if (kind == IResourceDelta.REMOVED || kind == IResourceDelta.ADDED || (kind == IResourceDelta.CHANGED && isErrorDelta(delta))) {
				// invalidate the resource and all parents
				while (resource.getType() != IResource.ROOT && fChangedElements.add(resource)) {
					resource= resource.getParent();
				}
			} 
		}	
		
		private boolean isErrorDelta(IResourceDelta delta) {	
			if ((delta.getFlags() & IResourceDelta.MARKERS) != 0) {
				IMarkerDelta[] markerDeltas= delta.getMarkerDeltas();
				for (int i= 0; i < markerDeltas.length; i++) {
					if (markerDeltas[i].isSubtypeOf(IMarker.PROBLEM)) {
						int kind= markerDeltas[i].getKind();
						if (kind == IResourceDelta.ADDED || kind == IResourceDelta.REMOVED)
							return true;
						int severity= markerDeltas[i].getAttribute(IMarker.SEVERITY, -1);
						int newSeverity= markerDeltas[i].getMarker().getAttribute(IMarker.SEVERITY, -1);
						if (newSeverity != severity)
							return true; 
					}
				}
			}
			return false;
		}
	}

	private ListenerList fListeners;
	
	private Set fResourcesWithMarkerChanges;
	private Set fResourcesWithAnnotationChanges;

	private UIJob fNotifierJob;
	
	public ProblemMarkerManager() {
		fListeners= new ListenerList();
		fResourcesWithMarkerChanges= new HashSet();
		fResourcesWithAnnotationChanges= new HashSet();
	}

	/*
	 * @see IResourceChangeListener#resourceChanged
	 */	
	public void resourceChanged(IResourceChangeEvent event) {
		HashSet changedElements= new HashSet();
		
		try {
			IResourceDelta delta= event.getDelta();
			if (delta != null)
				delta.accept(new ProjectErrorVisitor(changedElements));
		} catch (CoreException e) {
			JavaPlugin.log(e.getStatus());
		}

		if (!changedElements.isEmpty()) {
			boolean hasChanges= false;
			synchronized (this) {
				if (fResourcesWithMarkerChanges.isEmpty()) {
					fResourcesWithMarkerChanges= changedElements;
					hasChanges= true;
				} else {
					hasChanges= fResourcesWithMarkerChanges.addAll(changedElements);
				}
			}
			if (hasChanges) {
				fireChanges();
			}
		}
	}
	
	/* (non-Javadoc)
	 * @see IAnnotationModelListener#modelChanged(IAnnotationModel)
	 */
	public void modelChanged(IAnnotationModel model) {
		// no action
	}

	/* (non-Javadoc)
	 * @see IAnnotationModelListenerExtension#modelChanged(AnnotationModelEvent)
	 */
	public void modelChanged(AnnotationModelEvent event) {
		if (event instanceof CompilationUnitAnnotationModelEvent) {
			CompilationUnitAnnotationModelEvent cuEvent= (CompilationUnitAnnotationModelEvent) event;
			if (cuEvent.includesProblemMarkerAnnotationChanges()) {
				boolean hasChanges= false;
				synchronized (this) {
					IResource changedResource= cuEvent.getUnderlyingResource();
					hasChanges= fResourcesWithAnnotationChanges.add(changedResource);
				}
				if (hasChanges) {
					fireChanges();
				}
			}
		}
	}	
	
	
	/**
	 * Adds a listener for problem marker changes.
	 * @param listener the listener to add
	 */
	public void addListener(IProblemChangedListener listener) {
		if (fListeners.isEmpty()) { 
			JavaPlugin.getWorkspace().addResourceChangeListener(this);
			JavaPlugin.getDefault().getCompilationUnitDocumentProvider().addGlobalAnnotationModelListener(this);
		}
		fListeners.add(listener);
	}

	/**
	 * Removes a <code>IProblemChangedListener</code>.
	 * @param listener the listener to remove
	 */	
	public void removeListener(IProblemChangedListener listener) {
		fListeners.remove(listener);
		if (fListeners.isEmpty()) {
			JavaPlugin.getWorkspace().removeResourceChangeListener(this);
			JavaPlugin.getDefault().getCompilationUnitDocumentProvider().removeGlobalAnnotationModelListener(this);
		}
	}
	
	private void fireChanges() {
		Display display= SWTUtil.getStandardDisplay();
		if (display != null && !display.isDisposed()) {
			postAsyncUpdate(display);
		}	
	}
	
	private void postAsyncUpdate(final Display display) {
		if (fNotifierJob == null) {
			fNotifierJob= new UIJob(display, JavaUIMessages.ProblemMarkerManager_problem_marker_update_job_description) {
				public IStatus runInUIThread(IProgressMonitor monitor) {
					runPendingUpdates();
					return Status.OK_STATUS;
				}
			};
			fNotifierJob.setSystem(true);
		}
		fNotifierJob.schedule();
	}
	
	/**
	 * Notify all IProblemChangedListener. Must be called in the display thread.
	 */
	private void runPendingUpdates() {
		IResource[] markerResources= null;
		IResource[] annotationResources= null;
		synchronized (this) {
			if (!fResourcesWithMarkerChanges.isEmpty()) {
				markerResources= (IResource[]) fResourcesWithMarkerChanges.toArray(new IResource[fResourcesWithMarkerChanges.size()]);
				fResourcesWithMarkerChanges.clear();
			}
			if (!fResourcesWithAnnotationChanges.isEmpty()) {
				annotationResources= (IResource[]) fResourcesWithAnnotationChanges.toArray(new IResource[fResourcesWithAnnotationChanges.size()]);
				fResourcesWithAnnotationChanges.clear();
			}
		}
		Object[] listeners= fListeners.getListeners();
		for (int i= 0; i < listeners.length; i++) {
			IProblemChangedListener curr= (IProblemChangedListener) listeners[i];
			if (markerResources != null) {
				curr.problemsChanged(markerResources, true);
			}
			if (annotationResources != null) {
				curr.problemsChanged(annotationResources, false);
			}
		}
	}

}
