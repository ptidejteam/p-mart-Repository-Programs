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
package org.eclipse.jdt.internal.ui.search;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.eclipse.core.runtime.preferences.InstanceScope;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.Rectangle;

import org.eclipse.jface.preference.PreferenceConverter;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.LabelProviderChangedEvent;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.jface.viewers.DelegatingStyledCellLabelProvider.IStyledLabelProvider;

import org.eclipse.ui.preferences.ScopedPreferenceStore;

import org.eclipse.search.ui.NewSearchUI;
import org.eclipse.search.ui.text.AbstractTextSearchResult;
import org.eclipse.search.ui.text.Match;

import org.eclipse.jdt.core.search.SearchMatch;

import org.eclipse.jdt.internal.corext.util.Messages;

import org.eclipse.jdt.ui.JavaElementImageDescriptor;
import org.eclipse.jdt.ui.JavaElementLabels;
import org.eclipse.jdt.ui.ProblemsLabelDecorator;
import org.eclipse.jdt.ui.search.IMatchPresentation;

import org.eclipse.jdt.internal.ui.JavaPlugin;
import org.eclipse.jdt.internal.ui.viewsupport.AppearanceAwareLabelProvider;
import org.eclipse.jdt.internal.ui.viewsupport.ColoringLabelProvider;
import org.eclipse.jdt.internal.ui.viewsupport.ImageImageDescriptor;
import org.eclipse.jdt.internal.ui.viewsupport.JavaElementImageProvider;

public abstract class SearchLabelProvider extends AppearanceAwareLabelProvider {
	
	public static final String PROPERTY_MATCH_COUNT= "org.eclipse.jdt.search.matchCount"; //$NON-NLS-1$

	// copied from SearchPreferencePage
	private static final String EMPHASIZE_POTENTIAL_MATCHES= "org.eclipse.search.potentialMatch.emphasize"; //$NON-NLS-1$
	private static final String POTENTIAL_MATCH_FG_COLOR= "org.eclipse.search.potentialMatch.fgColor"; //$NON-NLS-1$

	protected static final long DEFAULT_SEARCH_TEXTFLAGS= (DEFAULT_TEXTFLAGS | JavaElementLabels.P_COMPRESSED) & ~JavaElementLabels.M_APP_RETURNTYPE;
	protected static final int DEFAULT_SEARCH_IMAGEFLAGS= DEFAULT_IMAGEFLAGS;
	
	private Color fPotentialMatchFgColor;
	private Map fLabelProviderMap;
	
	protected JavaSearchResultPage fPage;

	private ScopedPreferenceStore fSearchPreferences;
	private IPropertyChangeListener fSearchPropertyListener;
	
	private Map fImages;

	public SearchLabelProvider(JavaSearchResultPage page) {
		super(DEFAULT_SEARCH_TEXTFLAGS, DEFAULT_SEARCH_IMAGEFLAGS);
		addLabelDecorator(new ProblemsLabelDecorator(null));
		
		fPage= page;
		fLabelProviderMap= new HashMap(5);
		
		fSearchPreferences= new ScopedPreferenceStore(new InstanceScope(), NewSearchUI.PLUGIN_ID);
		fSearchPropertyListener= new IPropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent event) {
				doSearchPropertyChange(event);
			}
		};
		fSearchPreferences.addPropertyChangeListener(fSearchPropertyListener);
		
		fImages= null;
	}
	
	final void doSearchPropertyChange(PropertyChangeEvent event) {
		if (fPotentialMatchFgColor == null)
			return;
		if (POTENTIAL_MATCH_FG_COLOR.equals(event.getProperty()) || EMPHASIZE_POTENTIAL_MATCHES.equals(event.getProperty())) {
			fPotentialMatchFgColor.dispose();
			fPotentialMatchFgColor= null;
			LabelProviderChangedEvent lpEvent= new LabelProviderChangedEvent(SearchLabelProvider.this, null); // refresh all
			fireLabelProviderChanged(lpEvent);
		}
	}

	public Color getForeground(Object element) {
		if (arePotentialMatchesEmphasized()) {
			if (getNumberOfPotentialMatches(element) > 0)
				return getForegroundColor();
		}
		return super.getForeground(element);
	}
	
	private Color getForegroundColor() {
		if (fPotentialMatchFgColor == null) {
			fPotentialMatchFgColor= new Color(JavaPlugin.getActiveWorkbenchShell().getDisplay(), getPotentialMatchForegroundColor());
		}
		return fPotentialMatchFgColor;
	}

	protected final int getNumberOfPotentialMatches(Object element) {
		int res= 0;
		AbstractTextSearchResult result= fPage.getInput();
		if (result != null) {
			Match[] matches= result.getMatches(element);
			for (int i = 0; i < matches.length; i++) {
				if ((matches[i]) instanceof JavaElementMatch) {
					if (((JavaElementMatch)matches[i]).getAccuracy() == SearchMatch.A_INACCURATE)
						res++;
				}
			}
		}
		return res;
	}
		
	protected final StyledString getColoredLabelWithCounts(Object element, StyledString coloredName) {
		String name= coloredName.getString();
		String decorated= getLabelWithCounts(element, name);
		if (decorated.length() > name.length()) {
			ColoringLabelProvider.decorateStyledString(coloredName, decorated, StyledString.COUNTER_STYLER);
		}
		return coloredName;
	}
	
	protected final String getLabelWithCounts(Object element, String elementName) {
		int matchCount= fPage.getDisplayedMatchCount(element);
		int potentialCount= getNumberOfPotentialMatches(element);
		
		if (matchCount < 2) {
			if (matchCount == 1 && hasChildren(element)) {
				if (potentialCount > 0)
					return Messages.format(SearchMessages.SearchLabelProvider_potential_singular, elementName); 
				return Messages.format(SearchMessages.SearchLabelProvider_exact_singular, elementName); 
			}
			if (potentialCount > 0)
				return Messages.format(SearchMessages.SearchLabelProvider_potential_noCount, elementName); 
			return Messages.format(SearchMessages.SearchLabelProvider_exact_noCount, elementName); 
		} else {
			int exactCount= matchCount - potentialCount;
			
			if (potentialCount > 0 && exactCount > 0) {
				String[] args= new String[] { elementName, String.valueOf(matchCount), String.valueOf(exactCount), String.valueOf(potentialCount) };
				return Messages.format(SearchMessages.SearchLabelProvider_exact_and_potential_plural, args); 
			} else if (exactCount == 0) {
				String[] args= new String[] { elementName, String.valueOf(matchCount) };
				return Messages.format(SearchMessages.SearchLabelProvider_potential_plural, args); 
			}
			String[] args= new String[] { elementName, String.valueOf(matchCount) };
			return Messages.format(SearchMessages.SearchLabelProvider_exact_plural, args); 
		}
	}
	
	/**
	 * Returns <code>true</code> if the given element has children
	 * @param elem the element
	 * @return returns <code>true</code> if the given element has children
	 */
	protected boolean hasChildren(Object elem) {
		return false;
	}
	
	public void dispose() {
		if (fPotentialMatchFgColor != null) {
			fPotentialMatchFgColor.dispose();
			fPotentialMatchFgColor= null;
		}
		fSearchPreferences.removePropertyChangeListener(fSearchPropertyListener);
		for (Iterator labelProviders = fLabelProviderMap.values().iterator(); labelProviders.hasNext();) {
			ILabelProvider labelProvider = (ILabelProvider) labelProviders.next();
			labelProvider.dispose();
		}
		if (fImages != null) {
			for (Iterator iterator= fImages.values().iterator(); iterator.hasNext();) {
				Image image= (Image) iterator.next();
				image.dispose();
			}
			fImages= null;
		}
		
		fSearchPreferences= null;
		fSearchPropertyListener= null;
		fLabelProviderMap.clear();
		
		super.dispose();
	}
	
	public void addListener(ILabelProviderListener listener) {
		super.addListener(listener);
		for (Iterator labelProviders = fLabelProviderMap.values().iterator(); labelProviders.hasNext();) {
			ILabelProvider labelProvider = (ILabelProvider) labelProviders.next();
			labelProvider.addListener(listener);
		}
	}

	public boolean isLabelProperty(Object element, String property) {
		if (PROPERTY_MATCH_COUNT.equals(property))
			return true;
		return getLabelProvider(element).isLabelProperty(element, property);
	}

	public void removeListener(ILabelProviderListener listener) {
		super.removeListener(listener);
		for (Iterator labelProviders = fLabelProviderMap.values().iterator(); labelProviders.hasNext();) {
			ILabelProvider labelProvider = (ILabelProvider) labelProviders.next();
			labelProvider.removeListener(listener);
		}
	}

	protected String getParticipantText(Object element) {
		ILabelProvider labelProvider= getLabelProvider(element);
		if (labelProvider != null)
			return labelProvider.getText(element);
		return ""; //$NON-NLS-1$
	
	}

	protected StyledString getStyledParticipantText(Object element) {
		ILabelProvider labelProvider= getLabelProvider(element);
		if (labelProvider instanceof IStyledLabelProvider)
			return ((IStyledLabelProvider) labelProvider).getStyledText(element);
		if (labelProvider != null)
			return new StyledString(labelProvider.getText(element));
		return new StyledString();
	}

	protected Image getParticipantImage(Object element) {
		ILabelProvider lp= getLabelProvider(element);
		if (lp == null)
			return null;
		Image image= lp.getImage(element);
		if (image == null || true) {
			return image;
		}
		Rectangle bounds= image.getBounds();
		Point expectedWidth= JavaElementImageProvider.BIG_SIZE;
		if (bounds.height == expectedWidth.y && bounds.width == expectedWidth.x) {
			return image;
		}
		if (bounds.height > expectedWidth.y || bounds.width > expectedWidth.x) {
			return null; // too big
		}
		return getSizeCorrectedImage(image, expectedWidth);
	}

	private Image getSizeCorrectedImage(Image image, Point expectedWidth) {
		if (fImages != null) {
			Image newImage= (Image) fImages.get(image);
			if (newImage != null) {
				return newImage;
			}
		} else {
			fImages= new HashMap();
		}
		ImageImageDescriptor desc= new ImageImageDescriptor(image);
		Image newImage= (new JavaElementImageDescriptor(desc, 0, expectedWidth)).createImage();
		fImages.put(image, newImage);
		return newImage;
	}

	private ILabelProvider getLabelProvider(Object element) {
		AbstractTextSearchResult input= fPage.getInput();
		if (!(input instanceof JavaSearchResult))
			return null;
		
		IMatchPresentation participant= ((JavaSearchResult) input).getSearchParticpant(element);
		if (participant == null)
			return null;
		
		ILabelProvider lp= (ILabelProvider) fLabelProviderMap.get(participant);
		if (lp == null) {
			lp= participant.createLabelProvider();
			fLabelProviderMap.put(participant, lp);
			
			Object[] listeners= fListeners.getListeners();
			for (int i= 0; i < listeners.length; i++) {
				lp.addListener((ILabelProviderListener) listeners[i]);
			}
		}
		return lp;
	}
	
	private boolean arePotentialMatchesEmphasized() {
		return fSearchPreferences.getBoolean(EMPHASIZE_POTENTIAL_MATCHES);
	}

	private RGB getPotentialMatchForegroundColor() {
		return PreferenceConverter.getColor(fSearchPreferences, POTENTIAL_MATCH_FG_COLOR);
	}
}
