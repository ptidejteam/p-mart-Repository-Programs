// $Id: ActionModifierLeaf.java,v 1.2 2006/03/02 05:07:58 vauchers Exp $
// Copyright (c) 2005 The Regents of the University of California. All
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

package org.argouml.uml.diagram.ui;

import org.argouml.model.Model;

class ActionModifierLeaf extends AbstractActionCheckBoxMenuItem {
    /**
     * The constructor.
     *
     * @param o the target
     */
    public ActionModifierLeaf(Object o) {
        super("checkbox.final-uc", NO_ICON);
        putValue("SELECTED", new Boolean(valueOfTarget(o)));
    }
    
    /**
     * @see org.argouml.uml.diagram.ui.FigNodeModelElement.AbstractActionCheckBoxMenuItem#toggleValueOfTarget(java.lang.Object)
     */
    void toggleValueOfTarget(Object t) {
        Model.getCoreHelper().setLeaf(t, !Model.getFacade().isLeaf(t));    
    }
    
    /**
     * @see org.argouml.uml.diagram.ui.FigNodeModelElement.AbstractActionCheckBoxMenuItem#valueOfTarget(java.lang.Object)
     */
    boolean valueOfTarget(Object t) {
        return Model.getFacade().isLeaf(t);
    }
}