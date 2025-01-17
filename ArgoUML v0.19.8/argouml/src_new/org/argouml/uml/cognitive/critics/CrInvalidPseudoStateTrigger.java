// $Id: CrInvalidPseudoStateTrigger.java,v 1.2 2006/03/02 05:07:02 vauchers Exp $
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
 * UML 1.5 Well-formedness rule [5] for Transition.
 *
 * @author pepargouml@yahoo.es
 */
public class CrInvalidPseudoStateTrigger extends CrUML {

    /**
     * The constructor.
     */
    public CrInvalidPseudoStateTrigger() {
        setupHeadAndDesc();
        addSupportedDecision(UMLDecision.STATE_MACHINES);
        addTrigger("trigger");
    }

    /**
     * @see org.argouml.uml.cognitive.critics.CrUML#predicate2(java.lang.Object,
     * org.argouml.cognitive.Designer)
     */
    public boolean predicate2(Object dm, Designer dsgr) {
        if (!(Model.getFacade().isATransition(dm))) return NO_PROBLEM;
        Object tr = /*(MTransition)*/ dm;
        Object t = Model.getFacade().getTrigger(tr);
        Object sv = Model.getFacade().getSource(tr);
        if (!(Model.getFacade().isAPseudostate(sv))) return NO_PROBLEM;
        Object k = Model.getFacade().getPseudostateKind(sv);
        //Forks have their own outgoing transitions critic
        if (Model.getFacade().
                equalsPseudostateKind(k,
                        Model.getPseudostateKind().getFork()))
            return NO_PROBLEM;
        boolean hasTrigger =
                (t != null && Model.getFacade().getName(t) != null
                && Model.getFacade().getName(t).length() > 0);
        if (hasTrigger) return PROBLEM_FOUND;
        return NO_PROBLEM;
    }

} /* end class CrInvalidPseudoStateTrigger */