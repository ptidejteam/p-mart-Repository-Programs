// $Id: ActionNewTagDefinition.java,v 1.2 2006/03/02 05:09:04 vauchers Exp $
// Copyright (c) 2004-2005 The Regents of the University of California. All
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

package org.argouml.uml.ui.foundation.extension_mechanisms;

import java.awt.event.ActionEvent;

import javax.swing.Action;

import org.argouml.i18n.Translator;
import org.argouml.kernel.Project;
import org.argouml.kernel.ProjectManager;
import org.argouml.model.Model;
import org.argouml.ui.targetmanager.TargetManager;
import org.argouml.uml.ui.AbstractActionNewModelElement;
import org.tigris.gef.presentation.Fig;


/**
 * This action creates a new TagDefinition in the current Model or Stereotype or 
 * Package.
 *
 * @author rastaman@tigris.org
 */
public class ActionNewTagDefinition extends AbstractActionNewModelElement {

    /**
     * The constructor.
     */
    public ActionNewTagDefinition() {
        super("button.new-tagdefinition");
        putValue(Action.NAME, Translator.localize("button.new-tagdefinition"));
    }

    /**
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent e) {
        Object t = TargetManager.getInstance().getTarget();
        if (t instanceof Fig) t = ((Fig) t).getOwner();
        Object owner = null;
        Object namespace = null;
        if (Model.getFacade().isAStereotype(t)) {
            owner = t;
            namespace = Model.getFacade().getNamespace(owner);
        } else {
            if (Model.getFacade().isAPackage(t))
                namespace = t;
            else {
                Project p = ProjectManager.getManager().getCurrentProject();
                Object model = p.getModel();
                namespace = model;
            }            
        }
        Object newTagDefinition = Model.getExtensionMechanismsFactory()
            .buildTagDefinition(
                    (String) null,
                    owner,
                    namespace
            );
        TargetManager.getInstance().setTarget(newTagDefinition);
        super.actionPerformed(e);
    }
    
}
