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
package org.eclipse.jdt.internal.ui.packageview;

import org.eclipse.jface.viewers.ISelectionProvider;

import org.eclipse.jdt.internal.ui.dnd.BasicSelectionTransferDragAdapter;

public class SelectionTransferDragAdapter extends BasicSelectionTransferDragAdapter {
		
	public SelectionTransferDragAdapter(ISelectionProvider provider) {
		super(provider);
	}
}
