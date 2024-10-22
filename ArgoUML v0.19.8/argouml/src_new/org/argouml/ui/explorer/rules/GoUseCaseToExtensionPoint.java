// $Id: GoUseCaseToExtensionPoint.java,v 1.2 2006/03/02 05:07:36 vauchers Exp $
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

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.argouml.i18n.Translator;
import org.argouml.model.Model;

/**
 * Rule for Use Case->Extension Point.
 * Provides a rule to display extension points in the navigation pane.<p>
 *
 * @author  16 Apr 2002. Jeremy Bennett (mail@jeremybennett.com).
 */
public class GoUseCaseToExtensionPoint extends AbstractPerspectiveRule {
    /**
     * Give a name to this rule.<p>
     *
     * @return  The name of the rule ("<code>Use Case->Extension
     *          Point</code>").
     */
    public String getRuleName() {
        return Translator.localize ("misc.use-case.extension-point");
    }

    /**
     * @see org.argouml.ui.explorer.rules.PerspectiveRule#getChildren(java.lang.Object)
     */
    public Collection getChildren(Object parent) {
        if (Model.getFacade().isAUseCase(parent)) {
            return Model.getFacade().getExtensionPoints(parent);
        }
        return null;
    }

    /**
     * @see org.argouml.ui.explorer.rules.PerspectiveRule#getDependencies(java.lang.Object)
     */
    public Set getDependencies(Object parent) {
        if (Model.getFacade().isAUseCase(parent)) {
	    Set set = new HashSet();
	    set.add(parent);
	    return set;
	}
	return null;
    }
}  /* End of class GoUseCaseToExtensionPoint */

