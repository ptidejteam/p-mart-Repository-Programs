// $Id: ActionAddAllClassesFromModel.java,v 1.2 2006/03/02 05:07:58 vauchers Exp $
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

package org.argouml.uml.diagram.ui;

import java.awt.event.ActionEvent;
import java.util.Iterator;

import org.argouml.model.Model;
import org.argouml.uml.diagram.static_structure.ui.UMLClassDiagram;
import org.argouml.uml.reveng.DiagramInterface;
import org.argouml.uml.ui.UMLAction;
import org.tigris.gef.base.Globals;

/**
 * ActionAddAllClassesFromModel enables pasting of an existing node into a
 * Diagram.
 *
 * @author Timothy M. Lebo (Oct 2003)
 * Smart Information Flow Technologies.
 */
public class ActionAddAllClassesFromModel extends UMLAction {

    // Instance Variables
    private String tabName;
    private Object object;

    /**
     * Constructor
     *
     * @param name the name of the action
     * @param o the Diagram
     */
    public ActionAddAllClassesFromModel(String name, Object o) {
        super(name, true, NO_ICON);
        tabName = name;
        object = o;
    }

    /**
     * shouldBeEnabled
     *
     * Returns true if this popup menu item should be enabled, false
     * if it should be grayed out.
     *
     * @author Timothy M. Lebo (Oct 2003)
     * Smart Information Flow Technologies.
     *
     * @see org.argouml.uml.ui.UMLAction#shouldBeEnabled()
     */
    public boolean shouldBeEnabled() {
	return object instanceof UMLClassDiagram;
    }

    /**
     * actionPerformed
     *
     * Finds all of the classes within the same namespace as the
     * UMLClassDiagram that was given to me in my constructor and adds
     * them to the UMLClassDiagram.
     *
     * @param ae - the ActionEvent
     *
     * @author Timothy M. Lebo (Oct 2003)
     * Smart Information Flow Technologies.
     */
    public void actionPerformed(ActionEvent ae) {

	if (object instanceof UMLClassDiagram) {

	    // Use DiagramInterface to add classes to diagram
	    DiagramInterface diagram =
		new DiagramInterface(Globals.curEditor());
	    diagram.setCurrentDiagram((UMLClassDiagram) object);

	    Object namespace = ((UMLClassDiagram) object).getNamespace();
	    Iterator elements =
		Model.getFacade().getOwnedElements(namespace).iterator();
	    while (elements.hasNext()) {
		Object element = elements.next();
		if (Model.getFacade().isAClass(element) 
		    && !Model.getFacade().isAAssociationClass(element)) {
		    diagram.addClass(element, false);
		}
	    }
	}
    }

} // ActionAddAllClassesFromModel
