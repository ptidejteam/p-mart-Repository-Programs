/*******************************************************************************
 * Copyright (c) 2000, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jdt.internal.ui.compare;

import org.eclipse.swt.widgets.Composite;

import org.eclipse.compare.CompareConfiguration;
import org.eclipse.compare.IViewerCreator;

import org.eclipse.jface.viewers.Viewer;

/**
 * A factory object for the {@link org.eclipse.jdt.internal.ui.compare.PropertiesFileMergeViewer}.
 * This indirection is necessary because only objects with a default
 * constructor can be created via an extension point
 * (this precludes Viewers).
 * 
 * @since 3.1
 */
public class PropertiesFileMergeViewerCreator implements IViewerCreator {

	public Viewer createViewer(Composite parent, CompareConfiguration mp) {
		return new PropertiesFileMergeViewer(parent, mp);
	}
}
