// $Id: GoClassToNavigableClass.java,v 1.2 2006/03/02 05:07:36 vauchers Exp $
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
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.argouml.i18n.Translator;
import org.argouml.model.Model;

/**
 * Rule for Class->Navigable Class.
 *
 */
public class GoClassToNavigableClass extends AbstractPerspectiveRule {

    /**
     * @see org.argouml.ui.explorer.rules.PerspectiveRule#getRuleName()
     */
    public String getRuleName() {
        return Translator.localize ("misc.class.navigable-class");
    }

    /**
     * @see org.argouml.ui.explorer.rules.PerspectiveRule#getChildren(java.lang.Object)
     */
    public Collection getChildren(Object parent) {
        if (!Model.getFacade().isAClass(parent)) {
            return null;
        }

        List childClasses = new ArrayList();

        Collection ends = Model.getFacade().getAssociationEnds(parent);
        if (ends == null) {
            return null;
        }

        Iterator it = ends.iterator();
        while (it.hasNext()) {
            Object ae = /*(MAssociationEnd)*/ it.next();
            Object asc = Model.getFacade().getAssociation(ae);
            Collection allEnds = Model.getFacade().getConnections(asc);

            Object otherEnd = null;
            Iterator endIt = allEnds.iterator();
            if (endIt.hasNext()) {
                otherEnd = /*(MAssociationEnd)*/ endIt.next();
                if (ae != otherEnd && endIt.hasNext()) {
                    otherEnd = /*(MAssociationEnd)*/ endIt.next();
                    if (ae != otherEnd) {
                        otherEnd = null;
                    }
                }
            }

            if (otherEnd == null) {
                continue;
            }
            if (!Model.getFacade().isNavigable(otherEnd)) {
                continue;
            }
            if (childClasses.contains(Model.getFacade().getType(otherEnd))) {
                continue;
            }
            childClasses.add(Model.getFacade().getType(otherEnd));
            // TODO: handle n-way Associations
        }

        return childClasses;
    }

    /**
     * @see org.argouml.ui.explorer.rules.PerspectiveRule#getDependencies(java.lang.Object)
     */
    public Set getDependencies(Object parent) {
        if (Model.getFacade().isAClass(parent)) {
	    Set set = new HashSet();
	    set.add(parent);
	    return set;
	}
	return null;
    }
}
