// $Id: CrInstanceWithoutClassifier.java,v 1.2 2006/03/02 05:07:02 vauchers Exp $
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

package org.argouml.uml.cognitive.critics;

import java.util.Collection;
import java.util.Iterator;

import org.argouml.cognitive.Designer;
import org.argouml.cognitive.ListSet;
import org.argouml.cognitive.ToDoItem;
import org.argouml.model.Model;
import org.argouml.uml.cognitive.UMLDecision;
import org.argouml.uml.cognitive.UMLToDoItem;
import org.argouml.uml.diagram.deployment.ui.UMLDeploymentDiagram;
import org.argouml.uml.diagram.ui.FigNodeModelElement;

/**
 * A critic to detect when an object in a deployment-diagram
 * is not inside a component or a component-instance.
 *
 * @author 5eichler
 */
public class CrInstanceWithoutClassifier extends CrUML {

    /**
     * The constructor.
     */
    public CrInstanceWithoutClassifier() {
        setupHeadAndDesc();
	addSupportedDecision(UMLDecision.PATTERNS);
    }

    /**
     * @see org.argouml.uml.cognitive.critics.CrUML#predicate2(
     * java.lang.Object, org.argouml.cognitive.Designer)
     */
    public boolean predicate2(Object dm, Designer dsgr) {
	if (!(dm instanceof UMLDeploymentDiagram)) return NO_PROBLEM;
	UMLDeploymentDiagram dd = (UMLDeploymentDiagram) dm;
	ListSet offs = computeOffenders(dd);
	if (offs == null) return NO_PROBLEM;
	return PROBLEM_FOUND;
    }

    /**
     * @see org.argouml.cognitive.critics.Critic#toDoItem(
     * java.lang.Object, org.argouml.cognitive.Designer)
     */
    public ToDoItem toDoItem(Object dm, Designer dsgr) {
	UMLDeploymentDiagram dd = (UMLDeploymentDiagram) dm;
	ListSet offs = computeOffenders(dd);
	return new UMLToDoItem(this, offs, dsgr);
    }

    /**
     * @see org.argouml.cognitive.Poster#stillValid(
     * org.argouml.cognitive.ToDoItem, org.argouml.cognitive.Designer)
     */
    public boolean stillValid(ToDoItem i, Designer dsgr) {
	if (!isActive()) return false;
	ListSet offs = i.getOffenders();
	UMLDeploymentDiagram dd = (UMLDeploymentDiagram) offs.firstElement();
	//if (!predicate(dm, dsgr)) return false;
	ListSet newOffs = computeOffenders(dd);
	boolean res = offs.equals(newOffs);
	return res;
    }

    /**
     * If there are instances that have no classifiers they belong to
     * the returned vector-set is not null. Then in the vector-set
     * are the UMLDeploymentDiagram and all FigObjects, FigComponentInstances
     * and FigMNodeInstances with no classifier.
     *
     * @param dd the diagram to check
     * @return the set of offenders
     */
    public ListSet computeOffenders(UMLDeploymentDiagram dd) {
	Collection figs = dd.getLayer().getContents();
	ListSet offs = null;
        Iterator figIter = figs.iterator();
	while (figIter.hasNext()) {
	    Object obj = figIter.next();
	    if (!(obj instanceof FigNodeModelElement)) continue;
	    FigNodeModelElement figNodeModelElement = (FigNodeModelElement) obj;
	    if (figNodeModelElement != null
                && (Model.getFacade().isAInstance(
                        figNodeModelElement.getOwner()))) {
		Object instance = figNodeModelElement.getOwner();
		if (instance != null) {
		    Collection col = Model.getFacade().getClassifiers(instance);
		    if (col.size() > 0) {
		        continue;
		    }
		}
		if (offs == null) {
		    offs = new ListSet();
		    offs.addElement(dd);
		}
		offs.addElement(figNodeModelElement);
	    }
	}
	return offs;
    }

} /* end class CrInstanceWithoutClassifier.java */
