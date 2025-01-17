// $Id: UMLLinkConnectionListModel.java,v 1.2 2006/03/02 05:08:48 vauchers Exp $
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

package org.argouml.uml.ui.behavior.common_behavior;

import java.util.ArrayList;
import java.util.List;

import org.argouml.model.Model;
import org.argouml.uml.ui.UMLModelElementOrderedListModel2;

/**
 *
 */
public class UMLLinkConnectionListModel
    extends UMLModelElementOrderedListModel2 {

    /**
     * Constructor for UMLInstanceLinkEndListModel.
     */
    public UMLLinkConnectionListModel() {
        super("linkEnd");
    }

    /**
     * @see org.argouml.uml.ui.UMLModelElementListModel2#buildModelList()
     */
    protected void buildModelList() {
        if (getTarget() != null) {
            setAllElements(Model.getFacade().getConnections(getTarget()));
        }
    }

    /**
     * @see org.argouml.uml.ui.UMLModelElementListModel2#isValidElement(Object)
     */
    protected boolean isValidElement(Object/*MBase*/ element) {
        return Model.getFacade().getConnections(getTarget()).contains(element);
    }

    /**
     * @see org.argouml.uml.ui.UMLModelElementOrderedListModel2#swap(int, int)
     */
    public void swap(int index1, int index2) {
        Object link = getTarget();
        List c = new ArrayList(Model.getFacade().getConnections(link));
        Object mem1 = c.get(index1);
        Object mem2 = c.get(index2);
        c.set(index1, mem2);
        c.set(index2, mem1);
        Model.getCoreHelper().setConnections(link, c);
        buildModelList();
    }
}
