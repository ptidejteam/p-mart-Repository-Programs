/*******************************************************************************
 * Copyright (c) 2000, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Jesper Kamstrup Linnet (eclipse@kamstrup-linnet.dk) - initial API and implementation 
 * 			(report 36180: Callers/Callees view)
 *   Stephan Herrmann (stephan@cs.tu-berlin.de):
 *          - bug 75800: [call hierarchy] should allow searches for fields
 *******************************************************************************/
package org.eclipse.jdt.internal.ui.callhierarchy;

import org.eclipse.core.runtime.Assert;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;

import org.eclipse.ui.PlatformUI;

import org.eclipse.jdt.core.IMember;

import org.eclipse.jdt.internal.corext.util.Messages;

import org.eclipse.jdt.ui.JavaElementLabels;

import org.eclipse.jdt.internal.ui.IJavaHelpContextIds;
import org.eclipse.jdt.internal.ui.viewsupport.JavaElementImageProvider;

class HistoryAction extends Action {
	
	private static long LABEL_FLAGS= JavaElementLabels.ALL_POST_QUALIFIED | JavaElementLabels.M_PARAMETER_TYPES | JavaElementLabels.M_APP_RETURNTYPE | JavaElementLabels.T_TYPE_PARAMETERS;
	private static long LABEL_MULTI_FLAGS= 0L;
	
	private CallHierarchyViewPart fView;
	private IMember[] fMembers;

	public HistoryAction(CallHierarchyViewPart viewPart, IMember[] members) {
		super("", AS_RADIO_BUTTON); //$NON-NLS-1$
		fView= viewPart;
		fMembers= members;

		String elementName= getElementLabel(members);
		setText(elementName);
		setImageDescriptor(getImageDescriptor(members));

		setDescription(Messages.format(CallHierarchyMessages.HistoryAction_description, elementName));
		setToolTipText(Messages.format(CallHierarchyMessages.HistoryAction_tooltip, elementName));

		PlatformUI.getWorkbench().getHelpSystem().setHelp(this, IJavaHelpContextIds.CALL_HIERARCHY_HISTORY_ACTION);
	}

	private static ImageDescriptor getImageDescriptor(IMember[] members) {
		if (members.length == 1) {
			JavaElementImageProvider imageProvider= new JavaElementImageProvider();
			ImageDescriptor desc= imageProvider.getBaseImageDescriptor(members[0], 0);
			imageProvider.dispose();
			return desc;
		} else {
			return null;
		}
	}

	/*
	 * @see Action#run()
	 */
	public void run() {
		fView.gotoHistoryEntry(fMembers);
	}

	static String getElementLabel(IMember[] members) {
		switch (members.length) {
        	case 0:
        		Assert.isTrue(false);
        		return null;
        		
        	case 1:
        		return JavaElementLabels.getElementLabel(members[0], LABEL_FLAGS);
        	
        	case 2:
        		return Messages.format(CallHierarchyMessages.HistoryAction_inputElements_2,
        				new String[] { getShortLabel(members[0]), getShortLabel(members[1]) });
        		
        	case 3:
        		return Messages.format(CallHierarchyMessages.HistoryAction_inputElements_3,
        				new String[] { getShortLabel(members[0]), getShortLabel(members[1]), getShortLabel(members[2]) });
        		
        	default:
        		return Messages.format(CallHierarchyMessages.HistoryAction_inputElements_more,
        				new String[] { getShortLabel(members[0]), getShortLabel(members[1]), getShortLabel(members[2]) });
		} 
    }

	private static String getShortLabel(IMember member) {
		return JavaElementLabels.getElementLabel(member, LABEL_MULTI_FLAGS);
	}
}
