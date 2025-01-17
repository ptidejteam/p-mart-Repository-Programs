// $Id: ToDoItem.java,v 1.2 2006/03/02 05:08:42 vauchers Exp $
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

package org.argouml.cognitive;

import java.io.Serializable;
import java.util.Enumeration;
import javax.swing.Icon;
import org.argouml.cognitive.critics.Critic;
import org.argouml.cognitive.ui.Wizard;
import org.argouml.cognitive.ui.WizardItem;
import org.argouml.model.Model;
import org.argouml.util.CollectionUtil;
import org.tigris.gef.base.Diagram;
import org.tigris.gef.presentation.Fig;
import org.tigris.gef.ui.Highlightable;



/**
 * This class defines the feedback items that can be placed on the
 * Designer's ToDoList.  The main point of a ToDoItem is to inform
 * the Designer of some problem or open design issue.  Additional
 * information in the ToDoItem helps put the designer in a mental
 * context suitable for resolving the issue: ToDoItem's are well tied
 * into the design and design process so that the Designer can see
 * which design material's are the subject of this ToDoItem, and which
 * Critic raised it.  The expert email address helps connect the
 * designer with the organizational context.  The more info URL helps
 * provide background knowledge of the domain. In the future
 * ToDoItems will include ties back to the design rationale log.
 * Also the run-time system needs to know who posted each ToDoItem so
 * that it can automatically remove it if it is no longer valid.
 */
public class ToDoItem implements Serializable, WizardItem {

    ////////////////////////////////////////////////////////////////
    // constants

    /**
     * The interruptive priority todoitem of 4 levels.
     */
    public static final int INTERRUPTIVE_PRIORITY = 9;

    /**
     * The high priority todoitem of 4 levels.
     */
    public static final int HIGH_PRIORITY = 1;

    /**
     * The medium priority todoitem of 4 levels.
     */
    public static final int MED_PRIORITY = 2;

    /**
     * The lowest priority todoitem of 4 levels.
     */
    public static final int LOW_PRIORITY = 3;

    ////////////////////////////////////////////////////////////////
    // instance variables

    /**
     * Who posted this item (e.g., a Critic, or the designer)?
     */
    private Poster thePoster;

    /**
     * One line description of issue.
     */
    private String theHeadline;

    /**
     * How important is this issue? Enough to interrupt the designer?
     */
    private int thePriority;

    /**
     * One paragraph description of the issue.
     */
    private String theDescription;

    /**
     * URL for background (textbook?) knowledge about the domain.
     */
    private String theMoreInfoURL;

    /**
     * Which part of the design this issue affects.<p>
     *
     * Each member is either a model element, a {@link Fig}, or
     * a {@link Diagram}.<p>
     *
     * This is set by the constructor and cannot change.
     */
    private ListSet theOffenders;

    private Icon theClarifier;

    private Wizard theWizard;

    ////////////////////////////////////////////////////////////////
    // constructors
    /**
     * The constructor.
     *
     * @param poster the poster
     * @param h the headline
     * @param p the priority
     * @param d the description
     * @param m the more info url
     * @param offs the offenders
     */
    public ToDoItem(Poster poster, String h, int p, String d, String m,
		    ListSet offs) {
        if (offs == null) {
            throw new IllegalArgumentException(
                    "A ListSet of offenders must be supplied.");
        }
        Object offender = CollectionUtil.getFirstItemOrNull(offs);
        if (offender != null
                && !Model.getFacade().isAModelElement(offender)
                && !(offender instanceof Fig)
                && !(offender instanceof Diagram)) {
            throw new IllegalArgumentException(
                    "The first offender must be a model element, "
                    + "a Fig or a Diagram");
        }

        if (offs.size() >= 2) {
            offender = offs.elementAt(1);
            if (!Model.getFacade().isAModelElement(offender)
                    && !(offender instanceof Fig)
                    && !(offender instanceof Diagram)) {
                throw new IllegalArgumentException(
                        "The second offender must be a model element, "
                        + "a Fig or a Diagram");
            }
        }
        // TODO: Why do we only care about checking the first
        // 2 offenders above?

        thePoster = poster;
        theHeadline = h;
        theOffenders = offs;
        thePriority = p;
        theDescription = d;
        theMoreInfoURL = m;
    }

    /**
     * The constructor.
     *
     * @param poster the poster
     * @param h the headline
     * @param p the priority
     * @param d the description
     * @param m the more info url
     */
    public ToDoItem(Poster poster, String h, int p, String d, String m) {
	thePoster = poster;
	theHeadline = h;
	theOffenders = new ListSet();
	thePriority = p;
	theDescription = d;
	theMoreInfoURL = m;
    }

    /**
     * The constructor.
     *
     * @param c the poster (critic)
     * @param dm A single offender.
     * @param dsgr the designer
     */
    public ToDoItem(Critic c, Object dm, Designer dsgr) {
        if (!Model.getFacade().isAModelElement(dm)
                && !(dm instanceof Fig)
                && !(dm instanceof Diagram)) {
            throw new IllegalArgumentException(
                    "The offender must be a model element, "
                    + "a Fig or a Diagram");
        }

        thePoster = c;
        theHeadline = c.getHeadline(dm, dsgr);
        theOffenders = new ListSet(dm);
        thePriority = c.getPriority(theOffenders, dsgr);
        theDescription = c.getDescription(theOffenders, dsgr);
        theMoreInfoURL = c.getMoreInfoURL(theOffenders, dsgr);
        theWizard = c.makeWizard(this);
    }

    /**
     * The constructor.
     *
     * @param c the poster (critic)
     * @param offs the offenders
     * @param dsgr the designer
     */
    public ToDoItem(Critic c, ListSet offs, Designer dsgr) {
        if (offs == null) {
            throw new IllegalArgumentException(
                    "A ListSet of offenders must be supplied.");
        }
        Object offender = CollectionUtil.getFirstItemOrNull(offs);
        if (offender != null
                && !Model.getFacade().isAModelElement(offender)
                && !(offender instanceof Fig)
                && !(offender instanceof Diagram)) {
            throw new IllegalArgumentException(
                    "The first offender must be a model element, "
                    + "a Fig or a Diagram");
        }

        if (offs.size() >= 2) {
            offender = offs.elementAt(1);
            if (!Model.getFacade().isAModelElement(offender)
                    && !(offender instanceof Fig)
                    && !(offender instanceof Diagram)) {
                throw new IllegalArgumentException(
                        "The second offender must be a model element, "
                        + "a Fig or a Diagram");
            }
        }
        // TODO: Why do we only care about checking the first
        // 2 offenders above?

        thePoster = c;
        theHeadline = c.getHeadline(offs, dsgr);
        theOffenders = offs;
        thePriority = c.getPriority(theOffenders, dsgr);
        theDescription = c.getDescription(theOffenders, dsgr);
        theMoreInfoURL = c.getMoreInfoURL(theOffenders, dsgr);
        theWizard = c.makeWizard(this);
    }



    /**
     * The constructor.
     *
     * @param c the poster (critic)
     */
    public ToDoItem(Critic c) {
	thePoster = c;
	theHeadline = c.getHeadline();
	theOffenders = new ListSet();
	thePriority = c.getPriority(null, null);
	theDescription = c.getDescription(null, null);
	theMoreInfoURL = c.getMoreInfoURL(null, null);
	theWizard = c.makeWizard(this);
    }


    // Cached expansions
    private String cachedExpandedHeadline;
    private String cachedExpandedDescription;

    ////////////////////////////////////////////////////////////////
    // accessors

    /**
     * @return the headline
     */
    public String getHeadline() {
	if (cachedExpandedHeadline == null) {
	    cachedExpandedHeadline =
	        thePoster.expand(theHeadline, theOffenders);
	}
	return cachedExpandedHeadline;
    }

    /**
     * @param h the headline
     */
    public void setHeadline(String h) {
	theHeadline = h;
	cachedExpandedHeadline = null;
    }

    /**
     * @return the description
     */
    public String getDescription() {
	if (cachedExpandedDescription == null) {
	    cachedExpandedDescription =
		thePoster.expand(theDescription, theOffenders);
	}
	return cachedExpandedDescription;
    }

    /**
     * @param d the description
     */
    public void setDescription(String d) {
	theDescription = d;
	cachedExpandedDescription = null;
    }

    /**
     * @return the more-info-url
     */
    public String getMoreInfoURL() { return theMoreInfoURL; }

    /**
     * @param m the more-info-url
     */
    public void setMoreInfoURL(String m) { theMoreInfoURL = m; }

    /**
     * @return the priority
     */
    public int getPriority() { return thePriority; }

    /**
     * @param p the priority
     */
    public void setPriority(int p) { thePriority = p; }

    /**
     * @return the wizard progress. An integer between 0 and 100,
     *         shows percent done.
     */
    public int getProgress() {
	if (theWizard != null) {
	    return theWizard.getProgress();
	}
	return 0;
    }

    /**
     * Reply a Set of design material's that are the subject of this ToDoItem.
     *
     * @return the offenders
     */
    public ListSet getOffenders() {
        assert theOffenders != null;
        assert theOffenders.size() <= 0
        	|| Model.getFacade().isAModelElement(theOffenders.elementAt(0))
        	|| theOffenders.elementAt(0) instanceof Fig
        	|| theOffenders.elementAt(0) instanceof Diagram;
        return theOffenders;
    }

    /**
     * Set the designmatial that is subject of this ToDoItem.
     *
     * @param offenders the offenders
     */
    public void setOffenders(ListSet offenders) {
        theOffenders = offenders;
    }

    /**
     * Reply the Critic or Designer that posted this ToDoItem.
     *
     * @return the poster
     */
    public Poster getPoster() { return thePoster; }

    /**
     * Find the email address of the poster.
     *
     * @return the email address
     */
    public String getExpertEmail() { return thePoster.getExpertEmail(); }

    /**
     * Return a clarifier object that can graphical highlight this
     * error in a design diagram. Return a clarifier for this todoitem,
     * if not found by the poster, or null.
     *
     * @return an Icon or null if none found.
     */
    public Icon getClarifier() {
	if (theClarifier != null) {
	    return theClarifier;
	}
	Icon posterClarifier = thePoster.getClarifier();
	if (posterClarifier != null) {
	    return posterClarifier;
	}
	return null;
    }

    /**
     * @return the wizard
     */
    public Wizard getWizard() { return theWizard; }

    /**
     * @param type the knowledgetype
     * @return true if the poster contains the given knowledgetype
     */
    public boolean containsKnowledgeType(String type) {
	return getPoster().containsKnowledgeType(type);
    }

    /**
     * @param d the decision
     * @return true if the decision is supported by the poster
     */
    public boolean supports(Decision d) {
	return getPoster().supports(d);
    }

    /**
     * @param g the given goal
     * @return true if the poster supports the given goal
     */
    public boolean supports(Goal g) {
	return getPoster().supports(g);
    }

    /**
     * @see java.lang.Object#hashCode()
     */
    public int hashCode() {
        int code = 0;

        code += getHeadline().hashCode();
        if (getPoster() != null) {
            code += getPoster().hashCode();
        }
        // The VectorSet.hashCode() doesn't exist so this will not work.
        // if (getOffenders() != null) {
        //     code += getOffenders().hashCode();
        // }
        return code;
    }

    /**
     * Is this item a copy?
     *
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals(Object o) {
	if (!(o instanceof ToDoItem)) {
	    return false;
	}
	ToDoItem i = (ToDoItem) o;
	if (!getHeadline().equals(i.getHeadline())) {
	    return false;
	}
	if (!(getPoster() == (i.getPoster()))) {
	    return false;
	}

	// For some reason VectorSet.equals() allocates a lot of memory, well
	// some memory at least. Lets try to avoid that when not needed by
	// invoking this test only when the two previous tests are not decisive.
	if (!getOffenders().equals(i.getOffenders())) {
	    return false;
	}
	return true;
    }

    ////////////////////////////////////////////////////////////////
    // user interface

    /**
     * When a ToDoItem is selected in the UiToDoList window, highlight
     * the "offending" design material's.
     */
    public void select() {
	Enumeration offs = getOffenders().elements();
	while (offs.hasMoreElements()) {
	    Object dm = offs.nextElement();
	    if (dm instanceof Highlightable) {
	        ((Highlightable) dm).setHighlight(true);
	    }
	}
    }

    /**
     * When a ToDoItem is deselected in the UiToDoList window,
     * unhighlight the "offending" design material's.
     */
    public void deselect() {
	Enumeration offs = getOffenders().elements();
	while (offs.hasMoreElements()) {
	    Object dm =  offs.nextElement();
	    if (dm instanceof Highlightable) {
	        ((Highlightable) dm).setHighlight(false);
	    }
	}
    }

    /**
     * The user has double-clicked or otherwise indicated that they
     * want to do something active with this item. By default, just
     * re-select it, subclasses may choose to do more (e.g., navigate to
     * the offending item if it is not visible).
     */
    public void action() { deselect(); select(); }

    /**
     * Notify the user interface that this ToDoItem has
     * changed. Currently, this is used to update the progress bar.
     */
    public void changed() {
	ToDoList list = Designer.theDesigner().getToDoList();
	list.fireToDoItemChanged(this);
    }


    ////////////////////////////////////////////////////////////////
    // issue resolutions

    /**
     * Some problems can be automatically fixed, ask the Critic to do
     * it if it can. <p>
     */
    public void fixIt() { thePoster.fixIt(this, null); }

    /**
     * Some problems can be automatically fixed, ask the Critic to do
     * it if it can.
     *
     * @return true if the critic can automatically fix the problem
     */
    public boolean canFixIt() { return thePoster.canFixIt(this); }

    /**
     * TODO: this is not done yet. Eventually this will also
     * feed the rational log.
     */
    //   public void resolve(Object reason) {
    //     ToDoList list = Designer.theDesigner().getToDoList();
    //     list.resolve(this, reason);
    //   }

    /**
     * Reply true iff this ToDoItem should be kept on the Designer's
     * ToDoList. This should return false if the poster has been
     * deactivated, or if it can be determined that the problem that
     * raised this issue is no longer present.
     *
     * @param d the given designer
     * @return true if the todoitem is still valid
     */
    public boolean stillValid(Designer d) {
	if (thePoster == null) {
	    return true;
	}
	if (theWizard != null && theWizard.isStarted()
	        && !theWizard.isFinished()) {
	    return true;
	}
	return thePoster.stillValid(this, d);
    }

    /**
     * Reply a string for debugging.
     *
     * @see java.lang.Object#toString()
     */
    public String toString() {
	return this.getClass().getName()
	    + "(" + getHeadline() + ") on " + getOffenders().toString();
    }

} /* end class ToDoItem */





