/*******************************************************************************
 * Copyright (c) 2000, 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Jesper Kamstrup Linnet (eclipse@kamstrup-linnet.dk) - initial API and implementation 
 *          (report 36180: Callers/Callees view)
 *   Stephan Herrmann (stephan@cs.tu-berlin.de):
 *          - bug 206949: [call hierarchy] filter field accesses (only write or only read)
 *******************************************************************************/
package org.eclipse.jdt.internal.corext.callhierarchy;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubProgressMonitor;

import org.eclipse.jdt.core.Flags;
import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IInitializer;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.ISourceRange;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.search.IJavaSearchConstants;
import org.eclipse.jdt.core.search.IJavaSearchScope;
import org.eclipse.jdt.core.search.SearchEngine;
import org.eclipse.jdt.core.search.SearchParticipant;
import org.eclipse.jdt.core.search.SearchPattern;

import org.eclipse.jdt.internal.corext.util.JdtFlags;
import org.eclipse.jdt.internal.corext.util.SearchUtils;

import org.eclipse.jdt.internal.ui.JavaPlugin;

class CallerMethodWrapper extends MethodWrapper {
    public CallerMethodWrapper(MethodWrapper parent, MethodCall methodCall) {
        super(parent, methodCall);
    }

    protected IJavaSearchScope getSearchScope() {
        return CallHierarchy.getDefault().getSearchScope();
    }

    protected String getTaskName() {
        return CallHierarchyMessages.CallerMethodWrapper_taskname; 
    }

    /* (non-Javadoc)
	 * @see org.eclipse.jdt.internal.corext.callhierarchy.MethodWrapper#createMethodWrapper(org.eclipse.jdt.internal.corext.callhierarchy.MethodCall)
	 */
	protected MethodWrapper createMethodWrapper(MethodCall methodCall) {
        return new CallerMethodWrapper(this, methodCall);
    }

	/*
	 * @see org.eclipse.jdt.internal.corext.callhierarchy.MethodWrapper#canHaveChildren()
	 */
	public boolean canHaveChildren() {
		IMember member= getMember();
		if (member instanceof IField)
			return getLevel() == 1;
		return member instanceof IMethod || member instanceof IType;
	}
	
	/**
	 * @return The result of the search for children
	 * @see org.eclipse.jdt.internal.corext.callhierarchy.MethodWrapper#findChildren(org.eclipse.core.runtime.IProgressMonitor)
	 */
	protected Map findChildren(IProgressMonitor progressMonitor) {
		try {

			IProgressMonitor monitor= new SubProgressMonitor(progressMonitor, 95, SubProgressMonitor.SUPPRESS_SUBTASK_LABEL);

			checkCanceled(progressMonitor);

			IMember member= getMember();
			SearchPattern pattern= null;
			IType type= null;
			if (member instanceof IType) {
				type= (IType) member;
			} else if (member instanceof IInitializer && ! Flags.isStatic(member.getFlags())) {
				type= (IType) member.getParent();
			}
			if (type != null) {
				if (! type.isAnonymous()) {
					pattern= SearchPattern.createPattern(type.getFullyQualifiedName('.'), 
							IJavaSearchConstants.CONSTRUCTOR, 
							IJavaSearchConstants.REFERENCES, 
							SearchUtils.GENERICS_AGNOSTIC_MATCH_RULE);
				} else {
					CallSearchResultCollector resultCollector= new CallSearchResultCollector();
					IJavaElement parent= type.getParent();
					if (parent instanceof IMember) {
						IMember parentMember= (IMember) parent;
						ISourceRange nameRange= type.getNameRange();
						int start= nameRange != null ? nameRange.getOffset() : -1;
						int len= nameRange != null ? nameRange.getLength() : 0;
						resultCollector.addMember(type, parentMember, start, start + len);
						return resultCollector.getCallers();
					}
				}
			}
			if (pattern == null) {
				int limitTo= IJavaSearchConstants.REFERENCES;
				if (member.getElementType() == IJavaElement.FIELD)
					limitTo= getFieldSearchMode();
				pattern= SearchPattern.createPattern(member, limitTo, SearchUtils.GENERICS_AGNOSTIC_MATCH_RULE);
			}
			if (pattern == null) { // e.g. for initializers
				return new HashMap(0);
			}
			
			SearchEngine searchEngine= new SearchEngine();
			MethodReferencesSearchRequestor searchRequestor= new MethodReferencesSearchRequestor();
			IJavaSearchScope defaultSearchScope= getSearchScope();
			boolean isWorkspaceScope= SearchEngine.createWorkspaceScope().equals(defaultSearchScope);
			IJavaSearchScope searchScope= isWorkspaceScope ? getAccurateSearchScope(defaultSearchScope, member) : defaultSearchScope;
			searchEngine.search(pattern, new SearchParticipant[] { SearchEngine.getDefaultSearchParticipant() }, searchScope, searchRequestor,
					monitor);
			return searchRequestor.getCallers();
			
		} catch (CoreException e) {
			JavaPlugin.log(e);
			return new HashMap(0);
		}
	}

	private IJavaSearchScope getAccurateSearchScope(IJavaSearchScope defaultSearchScope, IMember member) throws JavaModelException {
		if (! JdtFlags.isPrivate(member))
			return defaultSearchScope;
		
		if (member.getCompilationUnit() != null) {
			return SearchEngine.createJavaSearchScope(new IJavaElement[] { member.getCompilationUnit() });
		} else if (member.getClassFile() != null) {
			// member could be called from an inner class-> search
			// package fragment (see also bug 109053):
			return SearchEngine.createJavaSearchScope(new IJavaElement[] { member.getAncestor(IJavaElement.PACKAGE_FRAGMENT) });
		} else {
			return defaultSearchScope;
		}
	}

}
