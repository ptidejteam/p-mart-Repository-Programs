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
package org.eclipse.jdt.internal.corext.refactoring.code.flow;



class GenericConditionalFlowInfo extends FlowInfo {
	
	public GenericConditionalFlowInfo() {
		super(UNDEFINED);
	}
	
	public void merge(FlowInfo info, FlowContext context) {
		if (info == null)
			return;
			
		mergeConditional(info, context);
	}
	
	public void mergeAccessMode(FlowInfo info, FlowContext context) {
		if (info == null)
			return;
			
		mergeAccessModeConditional(info, context);
	}
}


