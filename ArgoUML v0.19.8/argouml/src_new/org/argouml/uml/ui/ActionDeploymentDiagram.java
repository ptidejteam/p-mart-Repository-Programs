// $Id: ActionDeploymentDiagram.java,v 1.2 2006/03/02 05:07:09 vauchers Exp $
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

package org.argouml.uml.ui;

import org.apache.log4j.Logger;
import org.argouml.kernel.ProjectManager;
import org.argouml.model.Model;
import org.argouml.uml.diagram.DiagramFactory;
import org.argouml.uml.diagram.deployment.ui.UMLDeploymentDiagram;
import org.argouml.uml.diagram.ui.UMLDiagram;

/** 
 * Action to trigger creation of a deployment diagram.
 */
public class ActionDeploymentDiagram extends ActionAddDiagram {

    ////////////////////////////////////////////////////////////////
    // static variables

    private static final Logger LOG =
        Logger.getLogger(ActionDeploymentDiagram.class);

    ////////////////////////////////////////////////////////////////
    // constructors

    /**
     * Constructor.
     */
    public ActionDeploymentDiagram() {
        super("action.deployment-diagram");
    }

    ////////////////////////////////////////////////////////////////
    // main methods

    /**
     * @see org.argouml.uml.ui.ActionAddDiagram#createDiagram(Object)
     */
    public UMLDiagram createDiagram(Object handle) {
        // a deployment diagram shows something about the whole model
        // according to the uml spec
	handle = ProjectManager.getManager().getCurrentProject().getRoot();
        if (!Model.getFacade().isANamespace(handle)) {
            LOG.error("No namespace as argument");
            LOG.error(handle);
            throw new IllegalArgumentException(
					       "The argument " + handle
					       + "is not a namespace.");
        }
        return (UMLDiagram)DiagramFactory.getInstance().createDiagram(
                UMLDeploymentDiagram.class, 
                handle,
                null);
    }

    /**
     * @see org.argouml.uml.ui.ActionAddDiagram#isValidNamespace(Object)
     */
    public boolean isValidNamespace(Object handle) {
        // a deployment diagram shows something about the whole model
        // according to the uml spec
        handle = ProjectManager.getManager().getCurrentProject().getRoot();
        if (!Model.getFacade().isANamespace(handle)) {
            LOG.error("No namespace as argument");
            LOG.error(handle);
            throw new IllegalArgumentException(
					       "The argument " + handle
					       + "is not a namespace.");
        }
        // may only occur as child of the model or in a package
        return (
		handle == ProjectManager.getManager().getCurrentProject().getModel()
                || Model.getFacade().isAPackage(handle));
    }

} /* end class ActionDeploymentDiagram */
