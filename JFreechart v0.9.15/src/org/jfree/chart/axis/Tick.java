/* ======================================
 * JFreeChart : a free Java chart library
 * ======================================
 *
 * Project Info:  http://www.jfree.org/jfreechart/index.html
 * Project Lead:  David Gilbert (david.gilbert@object-refinery.com);
 *
 * (C) Copyright 2000-2003, by Object Refinery Limited and Contributors.
 *
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation;
 * either version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this
 * library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330,
 * Boston, MA 02111-1307, USA.
 *
 * ---------
 * Tick.java
 * ---------
 * (C) Copyright 2000-2003, by Object Refinery Limited.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited);
 * Contributor(s):   Nicolas Brodu;
 *
 * $Id: Tick.java,v 1.1 2007/10/10 19:21:57 vauchers Exp $
 *
 * Changes (from 18-Sep-2001)
 * --------------------------
 * 18-Sep-2001 : Added standard header and fixed DOS encoding problem (DG);
 * 26-Sep-2002 : Fixed errors reported by Checkstyle (DG);
 * 08-Nov-2002 : Moved to new package com.jrefinery.chart.axis (DG);
 * 26-Mar-2003 : Implemented Serializable (DG);
 * 12-Sep-2003 : Implemented Cloneable (NB);
 * 07-Nov-2003 : Added subclasses for particular types of ticks (DG);
 *
 */

package org.jfree.chart.axis;

import java.io.Serializable;

import org.jfree.ui.TextAnchor;

/**
 * Represents the dimensions of a tick on an axis (used during the process of
 * drawing a chart, but not retained).
 *
 * @author David Gilbert
 */
public abstract class Tick implements Serializable, Cloneable {

    /** A text version of the tick value. */
    private String text;

    /** The text anchor for the tick label. */
    private TextAnchor textAnchor;
    
    /** The rotation anchor for the tick label. */
    private TextAnchor rotationAnchor;
        
    /** The rotation angle. */
    private double angle;
    
    /**
     * Creates a new tick.
     *
     * @param text  the formatted version of the tick value.
     * @param textAnchor  the text anchor.
     * @param rotationAnchor  the rotation anchor.
     * @param angle  the angle. 
     */
    public Tick(String text, TextAnchor textAnchor, TextAnchor rotationAnchor, double angle) {
                    
        this.text = text;
        this.textAnchor = textAnchor;
        this.rotationAnchor = rotationAnchor;
        this.angle = angle;
        
    }

    /**
     * Returns the text version of the tick value.
     *
     * @return the formatted version of the tick value;
     */
    public String getText() {
        return text;
    }

    /**
     * Returns the text anchor.
     * 
     * @return The text anchor.
     */
    public TextAnchor getTextAnchor() {
        return this.textAnchor;
    }

    /**
     * Returns the rotation anchor.
     * 
     * @return The rotation anchor.
     */    
    public TextAnchor getRotationAnchor() {
        return this.rotationAnchor;
    }
    
    /**
     * Returns the angle.
     * 
     * @return The angle.
     */
    public double getAngle() {
        return this.angle;
    }

    /** 
     * Clone the object values too if possible
     * @see java.lang.Object#clone()
     */
    public Object clone() throws CloneNotSupportedException {
        Tick clone = (Tick) super.clone();
        return clone;
    }

    /**
     * Returns a string representation of the tick.
     * 
     * @return a string.
     */
    public String toString() {
        return this.text;
    }
}
