// $Id: PriorityNode.java,v 1.2 2006/03/02 05:07:09 vauchers Exp $
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

package org.argouml.cognitive.ui;

import java.util.Vector;
import org.argouml.cognitive.ToDoItem;
import org.argouml.cognitive.Translator;

/**
 * This class represents a "priority". Which is a classification for critics.
 *
 */
public class PriorityNode {

    // Private members.
    private static final String HIGH =
        Translator.localize("misc.level.high");
    private static final String MEDIUM =
        Translator.localize("misc.level.medium");
    private static final String LOW =
        Translator.localize("misc.level.low");

    ////////////////////////////////////////////////////////////////
    // static variables and methods
    private static Vector priorities = null;


    ////////////////////////////////////////////////////////////////
    // instance variables

    private String name;
    private int priority;

    ////////////////////////////////////////////////////////////////
    // contrsuctors

    /**
     * The constructor.
     *
     * @param n the name of this priority
     * @param pri the priority number
     */
    public PriorityNode(String n, int pri) {
	name = n;
	priority = pri;
    }

    /**
     * @return the list of all the priorities
     */
    public static Vector getPriorities() {
        if (priorities == null) {
            priorities = new Vector();
            priorities.addElement(new PriorityNode(HIGH,
                    ToDoItem.HIGH_PRIORITY));
            priorities.addElement(new PriorityNode(MEDIUM,
                    ToDoItem.MED_PRIORITY));
            priorities.addElement(new PriorityNode(LOW,
                    ToDoItem.LOW_PRIORITY));
        }
    return priorities;
    }


    ////////////////////////////////////////////////////////////////
    // accessors

    /**
     * @return the name
     */
    public String getName() { return name; }

    /**
     * @return the priority
     */
    public int getPriority() { return priority; }

    /**
     * @see java.lang.Object#toString()
     */
    public String toString() { return getName(); }

} /* end class PriorityNode */
