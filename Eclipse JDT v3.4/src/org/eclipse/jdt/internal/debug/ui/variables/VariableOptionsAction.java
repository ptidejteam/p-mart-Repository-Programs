/*******************************************************************************
 * Copyright (c) 2004, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial implementation
 *******************************************************************************/
package org.eclipse.jdt.internal.debug.ui.variables;

import org.eclipse.core.runtime.Preferences.IPropertyChangeListener;
import org.eclipse.core.runtime.Preferences.PropertyChangeEvent;
import org.eclipse.debug.ui.IDebugView;
import org.eclipse.jdt.internal.debug.ui.SWTFactory;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;

/**
 * Action which opens preference settings for Java variables.
 */
public class VariableOptionsAction implements IViewActionDelegate, IPropertyChangeListener {
	
	private IViewPart fPart;

    /* (non-Javadoc)
     * @see org.eclipse.ui.IViewActionDelegate#init(org.eclipse.ui.IViewPart)
     */
    public void init(IViewPart view) {
    	fPart = view;
    }

    /* (non-Javadoc)
     * @see org.eclipse.ui.IActionDelegate#run(org.eclipse.jface.action.IAction)
     */
    public void run(IAction action) {
    	SWTFactory.showPreferencePage("org.eclipse.jdt.debug.ui.JavaDetailFormattersPreferencePage",  //$NON-NLS-1$
    			new String[] {"org.eclipse.jdt.debug.ui.JavaDetailFormattersPreferencePage", //$NON-NLS-1$
    							"org.eclipse.jdt.debug.ui.JavaLogicalStructuresPreferencePage",  //$NON-NLS-1$
    							"org.eclipse.jdt.debug.ui.heapWalking",  //$NON-NLS-1$
    							"org.eclipse.jdt.debug.ui.JavaPrimitivesPreferencePage"}); //$NON-NLS-1$
    }

    /* (non-Javadoc)
     * @see org.eclipse.ui.IActionDelegate#selectionChanged(org.eclipse.jface.action.IAction, org.eclipse.jface.viewers.ISelection)
     */
    public void selectionChanged(IAction action, ISelection selection) {
    }

	/* (non-Javadoc)
	 * @see org.eclipse.core.runtime.Preferences.IPropertyChangeListener#propertyChange(org.eclipse.core.runtime.Preferences.PropertyChangeEvent)
	 */
	public void propertyChange(PropertyChangeEvent event) {
		if (fPart instanceof IDebugView) {
			IDebugView view = (IDebugView) fPart;
			view.getViewer().refresh();
		}
		
	}
    
}
