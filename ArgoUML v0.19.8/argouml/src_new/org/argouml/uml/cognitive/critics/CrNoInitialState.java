// $Id: CrNoInitialState.java,v 1.2 2006/03/02 05:07:02 vauchers Exp $
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

import java.util.Collection;
import java.util.Iterator;

import org.argouml.cognitive.Designer;
import org.argouml.model.Model;
import org.argouml.uml.cognitive.UMLDecision;

/**
 * A critic to detect whether the Compositestate attached to a
 * Statemachine has no initial state.
 *
 * @author jrobbins
 */
public class CrNoInitialState extends CrUML {

    /**
     * The constructor.
     *
     */
    public CrNoInitialState() {
        setupHeadAndDesc();
	addSupportedDecision(UMLDecision.STATE_MACHINES);
	addTrigger("substate");
    }

    /**
     * @see org.argouml.uml.cognitive.critics.CrUML#predicate2(
     * java.lang.Object, org.argouml.cognitive.Designer)
     */
    public boolean predicate2(Object dm, Designer dsgr) {
	if (!(Model.getFacade().isACompositeState(dm))) {
	    return NO_PROBLEM;
	}
	Object cs = /*(MCompositeState)*/ dm;

	// if this composite state is not attached to a statemachine
	// it is not the toplevel composite state.
	if (Model.getFacade().getStateMachine(cs) == null) {
	    return NO_PROBLEM;
	}
	Collection peers = Model.getFacade().getSubvertices(cs);
	int initialStateCount = 0;
	if (peers == null) {
	    return PROBLEM_FOUND;
	}
	int size = peers.size();
	for (Iterator iter = peers.iterator(); iter.hasNext();) {
	    Object sv = iter.next();
	    if (Model.getFacade().isAPseudostate(sv)
		&& (Model.getFacade().getKind(sv).equals(
                        Model.getPseudostateKind().getInitial()))) {
	        initialStateCount++;
	    }
	}
	if (initialStateCount == 0) {
	    return PROBLEM_FOUND;
	}
	return NO_PROBLEM;
    }

} /* end class CrNoInitialState */
