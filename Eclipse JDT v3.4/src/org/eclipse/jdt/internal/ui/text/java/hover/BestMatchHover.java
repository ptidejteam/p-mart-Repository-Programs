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
package org.eclipse.jdt.internal.ui.text.java.hover;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.text.IInformationControlCreator;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextHover;
import org.eclipse.jface.text.ITextHoverExtension;
import org.eclipse.jface.text.ITextHoverExtension2;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.information.IInformationProviderExtension2;

import org.eclipse.ui.IEditorPart;

import org.eclipse.jdt.ui.PreferenceConstants;
import org.eclipse.jdt.ui.text.java.hover.IJavaEditorTextHover;

import org.eclipse.jdt.internal.ui.JavaPlugin;


/**
 * Caution: this implementation is a layer breaker and contains some "shortcuts"
 */
public class BestMatchHover extends AbstractJavaEditorTextHover {

	private List fTextHoverSpecifications;
	private List fInstantiatedTextHovers;
	private ITextHover fBestHover;

	public BestMatchHover() {
		installTextHovers();
	}

	public BestMatchHover(IEditorPart editor) {
		this();
		setEditor(editor);
	}

	/**
	 * Installs all text hovers.
	 */
	private void installTextHovers() {

		// initialize lists - indicates that the initialization happened
		fTextHoverSpecifications= new ArrayList(2);
		fInstantiatedTextHovers= new ArrayList(2);

		// populate list
		JavaEditorTextHoverDescriptor[] hoverDescs= JavaPlugin.getDefault().getJavaEditorTextHoverDescriptors();
		for (int i= 0; i < hoverDescs.length; i++) {
			// ensure that we don't add ourselves to the list
			if (!PreferenceConstants.ID_BESTMATCH_HOVER.equals(hoverDescs[i].getId()))
				fTextHoverSpecifications.add(hoverDescs[i]);
		}
	}

	private void checkTextHovers() {
		if (fTextHoverSpecifications.size() == 0)
			return;

		for (Iterator iterator= new ArrayList(fTextHoverSpecifications).iterator(); iterator.hasNext(); ) {
			JavaEditorTextHoverDescriptor spec= (JavaEditorTextHoverDescriptor) iterator.next();

			IJavaEditorTextHover hover= spec.createTextHover();
			if (hover != null) {
				hover.setEditor(getEditor());
				addTextHover(hover);
				fTextHoverSpecifications.remove(spec);
			}
		}
	}

	protected void addTextHover(ITextHover hover) {
		if (!fInstantiatedTextHovers.contains(hover))
			fInstantiatedTextHovers.add(hover);
	}

	/*
	 * @see ITextHover#getHoverInfo(ITextViewer, IRegion)
	 */
	public String getHoverInfo(ITextViewer textViewer, IRegion hoverRegion) {

		checkTextHovers();
		fBestHover= null;

		if (fInstantiatedTextHovers == null)
			return null;

		for (Iterator iterator= fInstantiatedTextHovers.iterator(); iterator.hasNext(); ) {
			ITextHover hover= (ITextHover)iterator.next();

			String s= hover.getHoverInfo(textViewer, hoverRegion);
			if (s != null && s.trim().length() > 0) {
				fBestHover= hover;
				return s;
			}
		}

		return null;
	}

	/*
	 * @see org.eclipse.jface.text.ITextHoverExtension2#getHoverInfo2(org.eclipse.jface.text.ITextViewer, org.eclipse.jface.text.IRegion)
	 */
	public Object getHoverInfo2(ITextViewer textViewer, IRegion hoverRegion) {
		
		checkTextHovers();
		fBestHover= null;
		
		if (fInstantiatedTextHovers == null)
			return null;
		
		for (Iterator iterator= fInstantiatedTextHovers.iterator(); iterator.hasNext(); ) {
			ITextHover hover= (ITextHover)iterator.next();
			
			if (hover instanceof ITextHoverExtension2) {
				Object info= ((ITextHoverExtension2) hover).getHoverInfo2(textViewer, hoverRegion);
				if (info != null) {
					fBestHover= hover;
					return info;
				}
			} else {
				String s= hover.getHoverInfo(textViewer, hoverRegion);
				if (s != null && s.trim().length() > 0) {
					fBestHover= hover;
					return s;
				}
			}
		}
		
		return null;
	}
	
	/*
	 * @see org.eclipse.jface.text.ITextHoverExtension#getHoverControlCreator()
	 * @since 3.0
	 */
	public IInformationControlCreator getHoverControlCreator() {
		if (fBestHover instanceof ITextHoverExtension)
			return ((ITextHoverExtension)fBestHover).getHoverControlCreator();

		return null;
	}

	/*
	 * @see org.eclipse.jface.text.information.IInformationProviderExtension2#getInformationPresenterControlCreator()
	 * @since 3.0
	 */
	public IInformationControlCreator getInformationPresenterControlCreator() {
		if (fBestHover instanceof IInformationProviderExtension2) // this is wrong, but left here for backwards compatibility
			return ((IInformationProviderExtension2)fBestHover).getInformationPresenterControlCreator();

		return null;
	}
}
