/*******************************************************************************
 * Copyright (c) 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jdt.internal.debug.ui.variables;

import org.eclipse.debug.internal.ui.elements.adapters.VariableColumnPresentation;
import org.eclipse.jdt.debug.ui.IJavaDebugUIConstants;

/**
 * @since 3.2
 *
 */
public class JavaVariableColumnPresentation extends VariableColumnPresentation {
	/**
	 * Constant identifier for the Java variable column presentation.
	 */
	public final static String JAVA_VARIABLE_COLUMN_PRESENTATION = IJavaDebugUIConstants.PLUGIN_ID + ".VARIALBE_COLUMN_PRESENTATION";  //$NON-NLS-1$
	/**
	 * Default column identifiers
	 */
	public final static String COLUMN_INSTANCE_ID = JAVA_VARIABLE_COLUMN_PRESENTATION + ".COL_INSTANCE_ID"; //$NON-NLS-1$
	
	/**
	 * Column ids
	 */
	private static String[] fgAllColumns = null;
	
	/* (non-Javadoc)
	 * @see org.eclipse.debug.internal.ui.elements.adapters.VariableColumnPresentation#getAvailableColumns()
	 */
	public String[] getAvailableColumns() {
		if (fgAllColumns == null) {
			String[] basic = super.getAvailableColumns();
			fgAllColumns = new String[basic.length + 1];
			System.arraycopy(basic, 0, fgAllColumns, 0, basic.length);
			fgAllColumns[basic.length] = COLUMN_INSTANCE_ID;
		}
		return fgAllColumns;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.debug.internal.ui.elements.adapters.VariableColumnPresentation#getHeader(java.lang.String)
	 */
	public String getHeader(String id) {
		if (COLUMN_INSTANCE_ID.equals(id)) {
			return VariableMessages.JavaVariableColumnPresentation_0;
		}
		return super.getHeader(id);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.debug.internal.ui.elements.adapters.VariableColumnPresentation#getId()
	 */
	public String getId() {
		return JAVA_VARIABLE_COLUMN_PRESENTATION;
	}
	
	
}
