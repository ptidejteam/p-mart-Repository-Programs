// $Id: CrMissingOperName.java,v 1.2 2006/03/02 05:07:02 vauchers Exp $
// Copyright (c) 1996-2005 The Regents of the University of California. All
// Rights Reserved. Permission to use, copy, modify, and distribute this
// software and its documentation without fee, and without a written
// agreement is hereby granted, provided that the above copyright notice
// and this paragraph appear in all copies.  This software program and
// documentation are copyrighted by The Regents of the University of
// California. The software program and documentation are supplied "AS
// IS", without any accompanying services from The Regents. The Regents
// does not warrant that the operation of the program will be
// uninterrupted or error-free. The end-user understands that the program
// was developed for research purposes and is advised not to rely
// exclusively on the program for any reason.  IN NO EVENT SHALL THE
// UNIVERSITY OF CALIFORNIA BE LIABLE TO ANY PARTY FOR DIRECT, INDIRECT,
// SPECIAL, INCIDENTAL, OR CONSEQUENTIAL DAMAGES, INCLUDING LOST PROFITS,
// ARISING OUT OF THE USE OF THIS SOFTWARE AND ITS DOCUMENTATION, EVEN IF
// THE UNIVERSITY OF CALIFORNIA HAS BEEN ADVISED OF THE POSSIBILITY OF
// SUCH DAMAGE. THE UNIVERSITY OF CALIFORNIA SPECIFICALLY DISCLAIMS ANY
// WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
// MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE. THE SOFTWARE
// PROVIDED HEREUNDER IS ON AN "AS IS" BASIS, AND THE UNIVERSITY OF
// CALIFORNIA HAS NO OBLIGATIONS TO PROVIDE MAINTENANCE, SUPPORT,
// UPDATES, ENHANCEMENTS, OR MODIFICATIONS.

package org.argouml.uml.cognitive.critics;

import org.argouml.cognitive.Designer;
import org.argouml.cognitive.ToDoItem;
import org.argouml.cognitive.critics.Critic;
import org.argouml.cognitive.ui.Wizard;
import org.argouml.model.Model;
import org.argouml.uml.cognitive.UMLDecision;

/**
 * A critic to detect whether an operation has a name.
 */
public class CrMissingOperName extends CrUML {

    /**
     * The constructor.
     */
    public CrMissingOperName() {
        setupHeadAndDesc();
	addSupportedDecision(UMLDecision.NAMING);
	setKnowledgeTypes(Critic.KT_SYNTAX);
	addTrigger("name");
    }

    /**
     * @see org.argouml.uml.cognitive.critics.CrUML#predicate2(
     * java.lang.Object, org.argouml.cognitive.Designer)
     */
    public boolean predicate2(Object dm, Designer dsgr) {
	if (!(Model.getFacade().isAOperation(dm))) return NO_PROBLEM;
	Object oper = /*(MOperation)*/ dm;
	String myName = Model.getFacade().getName(oper);
	if (myName == null || myName.equals("")) return PROBLEM_FOUND;
	if (myName.length() == 0) return PROBLEM_FOUND;
	return NO_PROBLEM;
    }

    /**
     * @see org.argouml.cognitive.critics.Critic#initWizard(
     *         org.argouml.cognitive.ui.Wizard)
     */
    public void initWizard(Wizard w) {
	if (w instanceof WizMEName) {
	    ToDoItem item = (ToDoItem) w.getToDoItem();
	    Object me = /*(MModelElement)*/ item.getOffenders().elementAt(0);
	    String ins = "Set the name of this attribute.";
	    String sug = "AttributeName";
	    if (Model.getFacade().isAOperation(me)) {
		Object a = /*(MOperation)*/ me;
		int count = 1;
		if (Model.getFacade().getOwner(a) != null)
		    count = Model.getFacade().getFeatures(
		            Model.getFacade().getOwner(a)).size();
		sug = "oper" + (count + 1);
	    }
	    ((WizMEName) w).setInstructions(ins);
	    ((WizMEName) w).setSuggestion(sug);
	}
    }

    /**
     * @see org.argouml.cognitive.critics.Critic#getWizardClass(org.argouml.cognitive.ToDoItem)
     */
    public Class getWizardClass(ToDoItem item) { return WizMEName.class; }

} /* end class CrMissingOperName */
