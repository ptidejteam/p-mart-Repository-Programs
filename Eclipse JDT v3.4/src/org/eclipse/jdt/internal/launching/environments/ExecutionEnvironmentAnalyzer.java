/*******************************************************************************
 * Copyright (c) 2006, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jdt.internal.launching.environments;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.internal.launching.EEVMInstall;
import org.eclipse.jdt.internal.launching.EEVMType;
import org.eclipse.jdt.launching.IVMInstall;
import org.eclipse.jdt.launching.IVMInstall2;
import org.eclipse.jdt.launching.IVMInstall3;
import org.eclipse.jdt.launching.JavaRuntime;
import org.eclipse.jdt.launching.environments.CompatibleEnvironment;
import org.eclipse.jdt.launching.environments.IExecutionEnvironment;
import org.eclipse.jdt.launching.environments.IExecutionEnvironmentAnalyzerDelegate;
import org.eclipse.jdt.launching.environments.IExecutionEnvironmentsManager;
/**
 * Environment analyzer for standard execution environments.
 * 
 * @since 3.3
 */
public class ExecutionEnvironmentAnalyzer implements IExecutionEnvironmentAnalyzerDelegate {
	
	private static final String JavaSE_1_6 = "JavaSE-1.6"; //$NON-NLS-1$
	private static final String J2SE_1_5 = "J2SE-1.5"; //$NON-NLS-1$
	private static final String J2SE_1_4 = "J2SE-1.4"; //$NON-NLS-1$
	private static final String J2SE_1_3 = "J2SE-1.3"; //$NON-NLS-1$
	private static final String J2SE_1_2 = "J2SE-1.2"; //$NON-NLS-1$
	private static final String JRE_1_1 = "JRE-1.1"; //$NON-NLS-1$
	
	private static final String CDC_FOUNDATION_1_1 = "CDC-1.1/Foundation-1.1"; //$NON-NLS-1$
	private static final String CDC_FOUNDATION_1_0 = "CDC-1.0/Foundation-1.0"; //$NON-NLS-1$
	
	private static final String OSGI_MINIMUM_1_0 = "OSGi/Minimum-1.0"; //$NON-NLS-1$
	private static final String OSGI_MINIMUM_1_1 = "OSGi/Minimum-1.1"; //$NON-NLS-1$
	
	private static final String JAVA_SPEC_VERSION = "java.specification.version"; //$NON-NLS-1$
	private static final String JAVA_SPEC_NAME = "java.specification.name"; //$NON-NLS-1$
	private static final String JAVA_VERSION = "java.version"; //$NON-NLS-1$
	
	private static final String[] VM_PROPERTIES = {JAVA_SPEC_NAME, JAVA_SPEC_VERSION, JAVA_VERSION};
	private static final String FOUNDATION = "foundation"; //$NON-NLS-1$
	private static final Map mappings = new HashMap();

	static {
		// table where the key is the EE and the value is an array of EEs that it is a super-set of
		mappings.put(CDC_FOUNDATION_1_0, new String[] {OSGI_MINIMUM_1_0});
		mappings.put(CDC_FOUNDATION_1_1, new String[] {CDC_FOUNDATION_1_0, OSGI_MINIMUM_1_1});
		mappings.put(OSGI_MINIMUM_1_1, new String[] {OSGI_MINIMUM_1_0});
		mappings.put(J2SE_1_2, new String[] {JRE_1_1});
		mappings.put(J2SE_1_3, new String[] {J2SE_1_2, CDC_FOUNDATION_1_0, OSGI_MINIMUM_1_0});
		mappings.put(J2SE_1_4, new String[] {J2SE_1_3, CDC_FOUNDATION_1_1, OSGI_MINIMUM_1_1});
		mappings.put(J2SE_1_5, new String[] {J2SE_1_4});
		mappings.put(JavaSE_1_6, new String[] {J2SE_1_5});
	}
	public CompatibleEnvironment[] analyze(IVMInstall vm, IProgressMonitor monitor) throws CoreException {
		ArrayList result = new ArrayList();
		if (!(vm instanceof IVMInstall2))
			return new CompatibleEnvironment[0];
		IVMInstall2 vm2 = (IVMInstall2) vm;
		List types = null;
		if (EEVMType.ID_EE_VM_TYPE.equals(vm.getVMInstallType().getId())) {
			String eeId = ((EEVMInstall)vm).getAttribute(EEVMInstall.ATTR_EXECUTION_ENVIRONMENT_ID);
			if (eeId != null) {
				types = getTypes(eeId);
			}
		}
		boolean strictMatch = true;
		if (types == null) {
			String javaVersion = vm2.getJavaVersion();
			if (javaVersion == null) {
				// We have a contributed VM type. Check to see if its a foundation VM, if we can.
				if ((vm instanceof IVMInstall3) && isFoundation1_0((IVMInstall3) vm)) 
					types = getTypes(CDC_FOUNDATION_1_0);
				else if ((vm instanceof IVMInstall3) && isFoundation1_1((IVMInstall3) vm)) 
					types = getTypes(CDC_FOUNDATION_1_1);
			} else {
				if (javaVersion.startsWith("1.7")) { //$NON-NLS-1$
					types = getTypes(JavaSE_1_6); // there is no 1.7 EE defined yet
					strictMatch = false; // 1.7 is not a strict match for 1.6
				} else if (javaVersion.startsWith("1.6")) //$NON-NLS-1$
					types = getTypes(JavaSE_1_6);
				else if (javaVersion.startsWith("1.5")) //$NON-NLS-1$
					types = getTypes(J2SE_1_5);
				else if (javaVersion.startsWith("1.4")) //$NON-NLS-1$
					types = getTypes(J2SE_1_4);
				else if (javaVersion.startsWith("1.3")) //$NON-NLS-1$
					types = getTypes(J2SE_1_3);
				else if (javaVersion.startsWith("1.2")) //$NON-NLS-1$
					types = getTypes(J2SE_1_2);
				else if (javaVersion.startsWith("1.1")) { //$NON-NLS-1$
					if ((vm instanceof IVMInstall3) && isFoundation1_1((IVMInstall3) vm))
						types = getTypes(CDC_FOUNDATION_1_1);
					else
						types = getTypes(JRE_1_1);
				} else if (javaVersion.startsWith("1.0")) { //$NON-NLS-1$
					if ((vm instanceof IVMInstall3) && isFoundation1_0((IVMInstall3) vm)) 
						types = getTypes(CDC_FOUNDATION_1_0);
				}
			}
		}

		if (types != null) {
			for (int i=0; i < types.size(); i++)
				addEnvironment(result, (String) types.get(i), strictMatch && i ==0);
		}
		return (CompatibleEnvironment[])result.toArray(new CompatibleEnvironment[result.size()]);
	}

	/*
	 * Check a couple of known system properties for the word "foundation".
	 */
	private boolean isFoundation(Map properties) {
		for (int i=0; i < VM_PROPERTIES.length; i++) {
			String value = (String) properties.get(VM_PROPERTIES[i]);
			if (value == null)
				continue;
			for (StringTokenizer tokenizer = new StringTokenizer(value); tokenizer.hasMoreTokens(); )
				if (FOUNDATION.equalsIgnoreCase(tokenizer.nextToken()))
					return true;
		}
		return false;
	}

	private boolean isFoundation1_0(IVMInstall3 vm) throws CoreException {
		Map map = vm.evaluateSystemProperties(VM_PROPERTIES, null);
		return isFoundation(map) ? "1.0".equals(map.get(JAVA_SPEC_VERSION)) : false; //$NON-NLS-1$
	}

	private boolean isFoundation1_1(IVMInstall3 vm) throws CoreException {
		Map map = vm.evaluateSystemProperties(VM_PROPERTIES, null);
		return isFoundation(map) ? "1.1".equals(map.get(JAVA_SPEC_VERSION)) : false; //$NON-NLS-1$
	}

	private void addEnvironment(ArrayList result, String id, boolean strict) {
		IExecutionEnvironmentsManager manager = JavaRuntime.getExecutionEnvironmentsManager();
		IExecutionEnvironment env = manager.getEnvironment(id);
		if (env != null)
			result.add(new CompatibleEnvironment(env, strict));
	}
	
	// first entry in the list is the perfect match
	private List getTypes(String type) {
		List result = new ArrayList();
		result.add(type);
		String[] values = (String[]) mappings.get(type);
		if (values != null) {
			for (int i=0; i<values.length; i++)
				result.addAll(getTypes(values[i]));
		}
		return result;
	}

}
