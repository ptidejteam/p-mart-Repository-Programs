// $Id: GoOperationToSequenceDiagram.java,v 1.2 2006/03/02 05:07:36 vauchers Exp $
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

package org.argouml.ui.explorer.rules;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.argouml.i18n.Translator;
import org.argouml.kernel.Project;
import org.argouml.kernel.ProjectManager;
import org.argouml.model.Model;
import org.argouml.ui.ArgoDiagram;
import org.argouml.uml.diagram.sequence.SequenceDiagramGraphModel;
import org.argouml.uml.diagram.sequence.ui.UMLSequenceDiagram;

/**
 * Rule for Operation->Sequence diagram.
 * Go rule from represented operation to sequence diagram representing it
 * @author : jaap.branderhorst@xs4all.nl
 */
public class GoOperationToSequenceDiagram extends AbstractPerspectiveRule {

    /**
     * @see org.argouml.ui.explorer.rules.PerspectiveRule#getRuleName()
     */
    public String getRuleName() {
        return Translator.localize ("misc.operation.sequence-diagram");
    }

    /**
     * @see org.argouml.ui.explorer.rules.PerspectiveRule#getChildren(java.lang.Object)
     */
    public Collection getChildren(Object parent) {
        if (Model.getFacade().isAOperation(parent)) {
            Collection col = Model.getFacade().getCollaborations(parent);
            List ret = new ArrayList();
            Project p = ProjectManager.getManager().getCurrentProject();
            Iterator it = p.getDiagrams().iterator();
            while (it.hasNext()) {
                ArgoDiagram diagram = (ArgoDiagram) it.next();
                if (diagram instanceof UMLSequenceDiagram
                    && col.contains(
                        (
                            (SequenceDiagramGraphModel)
                                ((UMLSequenceDiagram) diagram)
                            .getGraphModel())
                            .getCollaboration())) {
                    ret.add(diagram);
                }

            }
            return ret;
        }
        return null;
    }

    /**
     * @see org.argouml.ui.explorer.rules.PerspectiveRule#getDependencies(java.lang.Object)
     */
    public Set getDependencies(Object parent) {
        // TODO: What?
	return null;
    }
}
