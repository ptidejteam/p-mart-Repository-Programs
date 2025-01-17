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
 * Contributor(s):   -;
 *
 * $Id: Tick.java,v 1.1 2007/10/10 19:09:14 vauchers Exp $
 *
 * Changes (from 18-Sep-2001)
 * --------------------------
 * 18-Sep-2001 : Added standard header and fixed DOS encoding problem (DG);
 * 26-Sep-2002 : Fixed errors reported by Checkstyle (DG);
 * 08-Nov-2002 : Moved to new package com.jrefinery.chart.axis (DG);
 * 26-Mar-2003 : Implemented Serializable (DG);
 *
 */

package org.jfree.chart.axis;

import java.io.Serializable;
import java.util.Date;

/**
 * Represents the dimensions of a tick on an axis (used during the process of
 * drawing a chart, but not retained).
 *
 * @author David Gilbert
 */
public class Tick implements Serializable {

    /** The tick value. */
    private Object value;

    /** A text version of the tick value. */
    private String text;

    /** The x-coordinate of the tick label. */
    private float x;

    /** The y-coordinate of the tick label. */
    private float y;

    /**
     * Standard constructor: creates a Tick with the specified properties.
     *
     * @param value  the tick value.
     * @param text  the formatted version of the tick value.
     * @param x  the x-coordinate of the tick label.
     * @param y  the y-coordinate of the tick label.
     */
    public Tick(Object value, String text, float x, float y) {
        this.value = value;
        this.text = text;
        this.x = x;
        this.y = y;
    }

    /**
     * Standard constructor: creates a Tick with the specified properties.
     *
     * @param text  the formatted version of the tick value.
     * @param x  the x-coordinate of the tick.
     * @param y  the y-coordinate of the tick.
     */
    public Tick(String text, float x, float y) {
        this(text, text, x, y);
    }

    /**
     * Returns the numerical value of the tick, or null if the value is not a number.
     *
     * @return the tick value.
     */
    public double getNumericalValue() {

        if (value instanceof Number) {
            Number v = (Number) value;
            return v.doubleValue();
        }
        else if (value instanceof Date) {
            Date d = (Date) value;
            return (double) d.getTime();
        }
        else {
            return 0.0;
        }

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
     * Returns the x-coordinate of the tick label.
     *
     * @return the x-coordinate of the tick label.
     */
    public float getX() {
        return x;
    }

    /**
     * Returns the y-coordinate of the tick label.
     *
     * @return the y-coordinate of the tick label.
     */
    public float getY() {
        return y;
    }

}
