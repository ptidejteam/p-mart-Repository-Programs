// $Id: ActionVisibilityPublic.java,v 1.2 2006/03/02 05:07:58 vauchers Exp $
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

class ActionVisibilityPublic extends AbstractActionRadioMenuItem {
    /**
     * The constructor.
     *
     * @param o the target
     * @param element TODO:
     */
    public ActionVisibilityPublic(Object o) {
        super("checkbox.visibility.public-uc", NO_ICON);
        putValue("SELECTED", new Boolean(
            Model.getVisibilityKind().getPublic()
                .equals(valueOfTarget(o))));
    }
    
    /**
     * @see org.argouml.uml.diagram.ui.FigNodeModelElement.AbstractActionRadioMenuItem#toggleValueOfTarget(java.lang.Object)
     */
    void toggleValueOfTarget(Object t) {
        Model.getCoreHelper().setVisibility(t,
            Model.getVisibilityKind().getPublic());
    }
        
    /**
     * Make use of the default visibility, which is public...
     * TODO: centralise this knowledge.
     * 
     * @see org.argouml.uml.diagram.ui.FigNodeModelElement.AbstractActionRadioMenuItem#valueOfTarget(java.lang.Object)
     */
    Object valueOfTarget(Object t) {
        Object v = Model.getFacade().getVisibility(t);
        return v == null ? Model.getVisibilityKind().getPublic() : v;
    }
}