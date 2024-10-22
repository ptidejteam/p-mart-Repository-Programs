// $Id: ActionSetMetaClass.java,v 1.2 2006/03/02 05:09:04 vauchers Exp $
// Copyright (c) 2003-2005 The Regents of the University of California. All
// Rights Reserved. Permission to use, copy, modify, and distribute this
// software and its documentation without fee, and without a written
// agreement is hereby granted, provided that the above copyright notice
// and this paragraph appear in all copies. This software program and
// documentation are copyrighted by The Regents of the University of
// California. The software program and documentation are supplied "AS
// IS", without any accompanying services from The Regents. The Regents
// does not warrant that the operation of the program will be
// uninterrupted or error-free. The end-user understands that the program
// was developed for research purposes and is advised not to rely
// exclusively on the program for any reason. IN NO EVENT SHALL THE
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

package org.argouml.uml.ui.foundation.extension_mechanisms;

import java.awt.event.ActionEvent;

import org.argouml.model.Model;
import org.argouml.uml.ui.UMLAction;
import org.argouml.uml.ui.UMLComboBox2;

/**
 *
 * @author mkl
 *
 */
public class ActionSetMetaClass extends UMLAction {

    /**
     * The Singleton.
     */
    public static final ActionSetMetaClass SINGLETON =
	new ActionSetMetaClass();

    /**
     * Constructor.
     */
    public ActionSetMetaClass() {
        super("Set", HAS_ICON);
    }

    /**
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent e) {
        super.actionPerformed(e);
        Object source = e.getSource();
        Object newBase = null;
        Object oldBase = null;
        Object stereo = null;
        if (source instanceof UMLComboBox2) {
            UMLComboBox2 combo = (UMLComboBox2) source;
            newBase = combo.getSelectedItem();
            Object o = combo.getTarget();
            if (Model.getFacade().isAStereotype(o)) {
                stereo = /* (String) */o;
                o = combo.getSelectedItem();

                newBase = /* (MUseCase) */o;
                oldBase = Model.getFacade().getBaseClass(stereo);
                if (newBase != oldBase) {
                    Model.getExtensionMechanismsHelper().setBaseClass(
                            stereo,
                            newBase);
                } else {
                    if (o != null && o.equals("")) {
                        Model.getExtensionMechanismsHelper().setBaseClass(
                                stereo,
                                "ModelElement");
                    }
                }
            }
        }
    }
}
