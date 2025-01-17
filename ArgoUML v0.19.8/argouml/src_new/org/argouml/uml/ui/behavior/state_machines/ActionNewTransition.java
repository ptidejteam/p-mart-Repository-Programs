// $Id: ActionNewTransition.java,v 1.2 2006/03/02 05:07:52 vauchers Exp $
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

// $header$
package org.argouml.uml.ui.behavior.state_machines;

import java.awt.event.ActionEvent;

import org.argouml.model.Model;
import org.argouml.uml.ui.AbstractActionNewModelElement;

/**
 * Action to create a new transition, either
 * an internal transition or a transition
 * between two states.
 * @since Dec 15, 2002
 * @author jaap.branderhorst@xs4all.nl
 */
public class ActionNewTransition extends AbstractActionNewModelElement {

    /**
     * Key used for storing the source of the transition.
     * If this value is not set,
     * the action assumes that an internal transition should be constructed.
     */
    public static final String SOURCE = "source";

     /**
     * Key used for storing the destination of the transition.
     * If this value is not set,
     * the action assumes that an internal transition should be constructed.
     */
    public static final String DESTINATION = "destination";


    private static final ActionNewTransition SINGLETON =
        new ActionNewTransition();

    /**
     * Constructor for ActionNewTransition.
     */
    protected ActionNewTransition() {
        super();
    }

    /**
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent e) {
        super.actionPerformed(e);
        if (getValue(SOURCE) == null || getValue(DESTINATION) == null) {
            Model.getStateMachinesFactory()
                .buildInternalTransition(getTarget());
        } else {
            Model.getStateMachinesFactory()
                .buildTransition(getValue(SOURCE), getValue(DESTINATION));
        }
    }

    /**
     * @return Returns the SINGLETON.
     */
    public static ActionNewTransition getInstance() {
        return SINGLETON;
    }

}
