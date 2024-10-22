/*******************************************************************************
 * Copyright (c) 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jdt.internal.debug.core;

import org.eclipse.debug.core.model.IDebugElement;
import org.eclipse.debug.core.model.IDebugTarget;
import org.eclipse.jdt.debug.core.IJavaDebugTarget;

/**
 * Controls preferences related to heap walking so that they are available in java 
 * debug model code, but can be updated through the UI.
 * 
 * @since 3.3
 */
public class HeapWalkingManager{
	
	private static HeapWalkingManager fgSingleton;
	
	/**
	 * Constructor. Intended to be called by getDefault()
	 */
	protected HeapWalkingManager() {}
	
	/**
	 * Returns whether the given parent object is a debug element with a debug target
	 * that supports retrieval of instance and reference information from the VM.
	 * 
	 * @param object the object to test, can be <code>null</code>
	 * @return whether the given object has a debug target that supports heap walking
	 */
	public static boolean supportsHeapWalking(Object object){
		if (object instanceof IDebugElement){
			IDebugTarget target = ((IDebugElement)object).getDebugTarget();
			if (target instanceof IJavaDebugTarget){
				return ((IJavaDebugTarget)target).supportsInstanceRetrieval();
			}
		}
		return false;
	}
	
	/**
	 * @return the default singleton instance of the manager
	 */
	public static HeapWalkingManager getDefault() {
		if (fgSingleton == null){
			fgSingleton = new HeapWalkingManager();
		}
		return fgSingleton;
	}
	
	/**
	 * @return preference dictating whether to display references as variables in variables view.
	 */
	public boolean isShowReferenceInVarView() {
		return JDIDebugPlugin.getDefault().getPluginPreferences().getBoolean(JDIDebugPlugin.PREF_SHOW_REFERENCES_IN_VAR_VIEW);
	}
	
	/**
	 * @return preference dictating the maximum number of references that should be displayed to the user 
	 */
	public int getAllReferencesMaxCount() {
		return JDIDebugPlugin.getDefault().getPluginPreferences().getInt(JDIDebugPlugin.PREF_ALL_REFERENCES_MAX_COUNT);
	}
	
	/**
	 * @return preference dictating the maximum number of instances that should be displayed to the user 
	 */
	public int getAllInstancesMaxCount() {
		return JDIDebugPlugin.getDefault().getPluginPreferences().getInt(JDIDebugPlugin.PREF_ALL_INSTANCES_MAX_COUNT);
	}
	
	/**
	 * Stores the passed vale in the preference store
	 * 
	 * @param value whether to display references as variables in the variables view
	 */
	public void setShowReferenceInVarView(boolean value) {
		JDIDebugPlugin.getDefault().getPluginPreferences().setValue(JDIDebugPlugin.PREF_SHOW_REFERENCES_IN_VAR_VIEW,value);
	}
	
	/**
	 * Stores the passed value in the preference store
	 * 
	 * @param max the maximum number of references that should be displayed to the user
	 */
	public void setAllReferencesMaxCount(int max){
		JDIDebugPlugin.getDefault().getPluginPreferences().setValue(JDIDebugPlugin.PREF_ALL_REFERENCES_MAX_COUNT, max);
	}
	
	/**
	 * Stores the passed value in the preference store
	 * 
	 * @param max the maximum number of instances that should be displayed to the user
	 */
	public void setAllInstancesMaxCount(int max){
		JDIDebugPlugin.getDefault().getPluginPreferences().setValue(JDIDebugPlugin.PREF_ALL_INSTANCES_MAX_COUNT, max);
	}
	
	/**
	 * Resets the preferences controlled by this manager to their default settings
	 */
	public void resetToDefaultSettings(){
		JDIDebugPlugin.getDefault().getPluginPreferences().setToDefault(JDIDebugPlugin.PREF_SHOW_REFERENCES_IN_VAR_VIEW);
		JDIDebugPlugin.getDefault().getPluginPreferences().setToDefault(JDIDebugPlugin.PREF_ALL_REFERENCES_MAX_COUNT);
		JDIDebugPlugin.getDefault().getPluginPreferences().setToDefault(JDIDebugPlugin.PREF_ALL_INSTANCES_MAX_COUNT);
	}
	
}
