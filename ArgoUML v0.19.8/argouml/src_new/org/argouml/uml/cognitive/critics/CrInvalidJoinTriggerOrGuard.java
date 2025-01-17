// $Id: CrInvalidJoinTriggerOrGuard.java,v 1.2 2006/03/02 05:07:02 vauchers Exp $
// Copyright (c) 2003-2005 The Regents of the University of California. All
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
import org.argouml.model.Model;
import org.argouml.uml.cognitive.UMLDecision;

/**
 * UML 1.5 Well-formedness rule [2] for Transition.
 *
 * @author pepargouml@yahoo.es
 */
public class CrInvalidJoinTriggerOrGuard extends CrUML {

    /**
     * The constructor.
     */
    public CrInvalidJoinTriggerOrGuard() {
        setupHeadAndDesc();
        addSupportedDecision(UMLDecision.STATE_MACHINES);
        addTrigger("trigger");
        addTrigger("guard");
    }

    /**
     * @see org.argouml.uml.cognitive.critics.CrUML#predicate2(java.lang.Object,
     * org.argouml.cognitive.Designer)
     */
    public boolean predicate2(Object dm, Designer dsgr) {
        if (!(Model.getFacade().isATransition(dm))) return NO_PROBLEM;
        Object tr = /*(MTransition)*/ dm;
        Object/*MEvent*/ t = Model.getFacade().getTrigger(tr);
        Object g = Model.getFacade().getGuard(tr);
        Object dv = Model.getFacade().getTarget(tr);
        if (!(Model.getFacade().isAPseudostate(dv))) return NO_PROBLEM;
		
		// WFR Transitions, OMG UML 1.3
        Object k = Model.getFacade().getPseudostateKind(dv);
        if (!Model.getFacade().
                equalsPseudostateKind(k,
                        Model.getPseudostateKind().getJoin()))
            return NO_PROBLEM;
		
        boolean hasTrigger =
                (t != null && Model.getFacade().getName(t) != null
                && Model.getFacade().getName(t).length() > 0);
        if (hasTrigger) return PROBLEM_FOUND;
        boolean noGuard = (g == null
                || Model.getFacade().getExpression(g) == null
                || Model.getFacade().getBody(Model.getFacade()
                        .getExpression(g)) == null
                || Model.getFacade().getBody(Model.getFacade()
                        .getExpression(g)).toString().length() == 0);
        if (!noGuard) return PROBLEM_FOUND;
        return NO_PROBLEM;
    }

} /* end class CrInvalidJoinTriggerOrGuard */