/*******************************************************************************
 * Copyright (c) 2000, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jdt.internal.ui.fix;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;

import org.eclipse.jdt.core.dom.CompilationUnit;

import org.eclipse.jdt.internal.corext.fix.CleanUpConstants;
import org.eclipse.jdt.internal.corext.fix.ExpressionsFix;
import org.eclipse.jdt.internal.corext.fix.IFix;

public class ExpressionsCleanUp extends AbstractCleanUp {
		
	public ExpressionsCleanUp(Map options) {
		super(options);
	}
	
	public ExpressionsCleanUp() {
		super();
	}
	
	/**
	 * {@inheritDoc}
	 */
	public CleanUpRequirements getRequirements() {
		return new CleanUpRequirements(requireAST(), false, null);
	}
	
	private boolean requireAST() {
		boolean usePrentheses= isEnabled(CleanUpConstants.EXPRESSIONS_USE_PARENTHESES);
		if (!usePrentheses)
			return false;
		
		return isEnabled(CleanUpConstants.EXPRESSIONS_USE_PARENTHESES_ALWAYS) ||
		       isEnabled(CleanUpConstants.EXPRESSIONS_USE_PARENTHESES_NEVER);
	}
	
	/**
	 * {@inheritDoc}
	 */
	public IFix createFix(CleanUpContext context) throws CoreException {
		CompilationUnit compilationUnit= context.getAST();
		if (compilationUnit == null)
			return null;
		
		boolean usePrentheses= isEnabled(CleanUpConstants.EXPRESSIONS_USE_PARENTHESES);
		if (!usePrentheses)
			return null;
		
		return ExpressionsFix.createCleanUp(compilationUnit, 
				isEnabled(CleanUpConstants.EXPRESSIONS_USE_PARENTHESES_ALWAYS),
				isEnabled(CleanUpConstants.EXPRESSIONS_USE_PARENTHESES_NEVER));
	}
		
	/**
	 * {@inheritDoc}
	 */
	public String[] getDescriptions() {
		List result= new ArrayList();
		if (isEnabled(CleanUpConstants.EXPRESSIONS_USE_PARENTHESES) && isEnabled(CleanUpConstants.EXPRESSIONS_USE_PARENTHESES_ALWAYS)) 
			result.add(MultiFixMessages.ExpressionsCleanUp_addParanoiac_description);
		
		if (isEnabled(CleanUpConstants.EXPRESSIONS_USE_PARENTHESES) && isEnabled(CleanUpConstants.EXPRESSIONS_USE_PARENTHESES_NEVER)) 
			result.add(MultiFixMessages.ExpressionsCleanUp_removeUnnecessary_description);
		
		return (String[])result.toArray(new String[result.size()]);
	}
	
	/**
	 * {@inheritDoc}
	 */
	public String getPreview() {
		StringBuffer buf= new StringBuffer();
		
		if (isEnabled(CleanUpConstants.EXPRESSIONS_USE_PARENTHESES) && isEnabled(CleanUpConstants.EXPRESSIONS_USE_PARENTHESES_ALWAYS)) {
			buf.append("boolean b= (((i > 0) && (i < 10)) || (i == 50));\n"); //$NON-NLS-1$
		} else if (isEnabled(CleanUpConstants.EXPRESSIONS_USE_PARENTHESES) && isEnabled(CleanUpConstants.EXPRESSIONS_USE_PARENTHESES_NEVER)) {
			buf.append("boolean b= i > 0 && i < 10 || i == 50;\n"); //$NON-NLS-1$
		} else {
			buf.append("boolean b= (i > 0 && i < 10 || i == 50);\n"); //$NON-NLS-1$
		}
		
		return buf.toString();
	}

}
