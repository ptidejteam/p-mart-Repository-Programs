/* ============================================
 * JFreeChart : a free Java chart class library
 * ============================================
 *
 * Project Info:  http://www.object-refinery.com/jfreechart/index.html
 * Project Lead:  David Gilbert (david.gilbert@object-refinery.com);
 *
 * (C) Copyright 2000-2002, by Simba Management Limited and Contributors.
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
 * (C) Copyright 2002, by Simba Management Limited.
 *
 * Original Author:  David Gilbert (for Simba Management Limited);
 * Contributor(s):   -;
 *
 * $Id: IntervalMarker.java,v 1.1 2007/10/10 19:52:14 vauchers Exp $
 *
 * Changes (since 20-Aug-2002)
 * --------------------------
 * 20-Aug-2002 : Added stroke to constructor in Marker class (DG);
 *
 */

package com.jrefinery.chart;

import java.awt.Paint;
import java.awt.Color;
import java.awt.Stroke;

/**
 * Represents an interval to be highlighted in some way.
 */
public class IntervalMarker extends Marker {

    /** The start value. */
    protected double startValue;

    /** The end value. */
    protected double endValue;

    /** The label. */
    protected String label;

    /**
     * Constructs an interval marker.
     *
     * @param start The start of the interval.
     * @param end The end of the interval.
     */
    public IntervalMarker(double start, double end) {

        this(start, end, null, Color.gray, new java.awt.BasicStroke(0.5f), Color.blue, 0.8f);
    }

    /**
     * Constructs an interval marker.
     *
     * @param start The start of the interval.
     * @param end The end of the interval.
     * @param label The interval label (null permitted).
     * @param outlinePaint The outline paint.
     * @param outlineStroke The outline stroke.
     * @param paint The fill paint.
     * @param alpha The alpha transparency.
     */
    public IntervalMarker(double start, double end, String label,
                          Paint outlinePaint, Stroke outlineStroke, Paint paint, float alpha) {

        super((start+end)/2, outlinePaint, outlineStroke, paint, alpha);
        this.startValue = start;
        this.endValue = end;
        this.label = label;
    }

    /**
     * Returns the start value for the interval.
     */
    public double getStartValue() {
        return this.startValue;
    }

    /**
     * Returns the end value for the interval.
     */
    public double getEndValue() {
        return this.endValue;
    }

    /**
     * Returns the label for the interval (possibly null).
     */
    public String getLabel() {
        return this.label;
    }

}
