// $Id: ActionAddMessage.java,v 1.2 2006/03/02 05:07:58 vauchers Exp $
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

package org.argouml.uml.diagram.ui;

import java.awt.event.ActionEvent;

import org.argouml.model.Model;
import org.argouml.ui.targetmanager.TargetManager;
import org.argouml.uml.ui.UMLAction;
import org.tigris.gef.base.Editor;
import org.tigris.gef.base.Globals;
import org.tigris.gef.base.Layer;
import org.tigris.gef.graph.GraphModel;
import org.tigris.gef.graph.GraphNodeRenderer;
import org.tigris.gef.presentation.FigNode;

/**
 * Action to add a message.
 * @stereotype singleton
 */
public class ActionAddMessage extends UMLAction {

    ////////////////////////////////////////////////////////////////
    // static variables

    private static ActionAddMessage singleton = new ActionAddMessage();


    ////////////////////////////////////////////////////////////////
    // constructors

    /**
     * The constructor.
     */
    private ActionAddMessage() {
        super("action.add-message", true, HAS_ICON);
    }


    ////////////////////////////////////////////////////////////////
    // main methods

    /**
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent ae) {
    	Object target =  TargetManager.getInstance().getModelTarget();

    	if (!(Model.getFacade().isAAssociationRole(target))
	    && Model.getFacade().isACollaboration(Model.getFacade()
                .getNamespace(target))) {
    	    return;
    	}
        // So, the target is a MAssociationRole
    	this.addMessage(target);
        super.actionPerformed(ae);
    }

    /**
     * Add a message to an associationRole: it builds it using the
     * Factory method and then it creates the Fig and adds it to the
     * diagram.
     *
     * @param associationrole the associationRole to which the new message
     *                        must be added
     */
    private void addMessage(Object associationrole) {
        Object collaboration = Model.getFacade().getNamespace(associationrole);
        Object message =
            Model.getCollaborationsFactory()
            	.buildMessage(collaboration, associationrole);
        Editor e = Globals.curEditor();
        GraphModel gm = e.getGraphModel();
        Layer lay = e.getLayerManager().getActiveLayer();
        GraphNodeRenderer gr = e.getGraphNodeRenderer();
        FigNode figMsg = gr.getFigNodeFor(gm, lay, message, null);
        ((FigMessage) figMsg).addPathItemToFigAssociationRole(lay);
        
        gm.getNodes().add(message); /*MVW This is not the correct way, 
        * but it allows connecting a CommentEdge to it! 
        * See e.g. ActionAddNote for the correct way.
        * Testcase:
        * 1. Select the message.
        * 2. Click the Comment tool.
        * */
        
        TargetManager.getInstance().setTarget(message);
    }

    /**
     * @see org.argouml.uml.ui.UMLAction#shouldBeEnabled()
     */
    public boolean shouldBeEnabled() {
	Object target =  TargetManager.getInstance().getModelTarget();
	return super.shouldBeEnabled()
	    && Model.getFacade().isAAssociationRole(target);
    }

    /**
     * @return Returns the singleton.
     */
    public static ActionAddMessage getSingleton() {
        return singleton;
    }
}  /* end class ActionAddMessage */
