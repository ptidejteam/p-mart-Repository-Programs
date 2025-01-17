/*******************************************************************************
 * Copyright (c) 2000, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/

package org.eclipse.jdt.internal.ui.refactoring;

import org.eclipse.jface.preference.IPreferenceStore;

import org.eclipse.jdt.internal.ui.JavaPlugin;

import org.eclipse.jdt.ui.PreferenceConstants;

public class RefactoringSavePreferences {

	public static final String PREF_SAVE_ALL_EDITORS= PreferenceConstants.REFACTOR_SAVE_ALL_EDITORS;
	
	public static boolean getSaveAllEditors() {
		IPreferenceStore store= JavaPlugin.getDefault().getPreferenceStore();
		return store.getBoolean(PREF_SAVE_ALL_EDITORS);
	}
	
	public static void setSaveAllEditors(boolean save) {
		IPreferenceStore store= JavaPlugin.getDefault().getPreferenceStore();
		store.setValue(PREF_SAVE_ALL_EDITORS, save);
	}	
}
