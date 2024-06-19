/* ======================================
 * JFreeChart : a free Java chart library
 * ======================================
 *
 * Project Info:  http://www.jfree.org/jfreechart/index.html
 * Project Lead:  David Gilbert (david.gilbert@object-refinery.com);
 *
 * (C) Copyright 2000-2002, by Object Refinery Limited and Contributors.
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
 * -------------------
 * IntervalMarker.java
 * -------------------
 * (C) Copyright 2002, 2003, by Object Refinery Limited.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited);
 * Contributor(s):   -;
 *
 * $Id: IntervalMarker.java,v 1.1 2007/10/10 19:19:00 vauchers Exp $
 *
 * Changes (since 20-Aug-2002)
 * --------------------------
 * 20-Aug-2002 : Added stroke to constructor in Marker class (DG);
 * 02-Oct-2002 : Fixed errors reported by Checkstyle (DG);
 * 26-Mar-2003 : Implemented Serializable (DG);
 *
 */

package org.jfree.chart;

import java.awt.Color;
import java.awt.Paint;
import java.awt.Stroke;
import java.io.Serializable;

/**
 * Represents an interval to be highlighted in some way.
 *
 * @author David Gilbert
 */
public class IntervalMarker extends Marker implements Cloneable, Serializable {

    /** The start value. */
    private double startValue;

    /** The end value. */
    private double endValue;

    /** The label. */
    private String label;

    /**
     * Constructs an interval marker.
     *
     * @param start  the start of the interval.
     * @param end  the end of the interval.
     */
    public IntervalMarker(double start, double end) {

        this(start, end, null, Color.gray, new java.awt.BasicStroke(0.5f), Color.blue, 0.8f);
    }

    /**
     * Constructs an interval marker.
     *
     * @param start  the start of the interval.
     * @param end  the end of the interval.
     * @param label  the interval label (null permitted).
     * @param outlinePaint  the outline paint.
     * @param outlineStroke  the outline stroke.
     * @param paint  the fill paint.
     * @param alpha  the alpha transparency.
     */
    public IntervalMarker(double start, double end, String label,
                          Paint outlinePaint, Stroke outlineStroke, Paint paint, float alpha) {

        super((start + end) / 2, outlinePaint, outlineStroke, paint, alpha);
        this.startValue = start;
        this.endValue = end;
        this.label = label;
    }

    /**
     * Returns the start value for the interval.
     *
     * @return The start value.
     */
    public double getStartValue() {
        return this.startValue;
    }

    /**
     * Returns the end value for the interval.
     *
     * @return The end value.
     */
    public double getEndValue() {
        return this.endValue;
    }

    /**
     * Returns the label for the interval (possibly null).
     *
     * @return The label.
     */
    public String getLabel() {
        return this.label;
    }
    
    /**
     * Returns a clone of the marker.
     * 
     * @return A clone.
     * 
     * @throws CloneNotSupportedException Not thrown by this class, but the exception is declared 
     *         for the use of subclasses.
     */
    public Object clone() throws CloneNotSupportedException {   
        return super.clone();   
    }
        

}
