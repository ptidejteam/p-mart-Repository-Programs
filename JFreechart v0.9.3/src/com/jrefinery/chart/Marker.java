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
 * -----------
 * Marker.java
 * -----------
 * (C) Copyright 2002, by Simba Management Limited.
 *
 * Original Author:  David Gilbert (for Simba Management Limited);
 * Contributor(s):   -;
 *
 * $Id: Marker.java,v 1.1 2007/10/10 19:52:15 vauchers Exp $
 *
 * Changes (since 2-Jul-2002)
 * --------------------------
 * 02-Jul-2002 : Added extra constructor, standard header and Javadoc comments (DG);
 * 20-Aug-2002 : Added the outline stroke attribute (DG);
 *
 */

package com.jrefinery.chart;

import java.awt.Stroke;
import java.awt.Paint;
import java.awt.Color;

/**
 * A constant value that is drawn on a chart as a marker, usually as a horizontal or a vertical
 * line.
 * <P>
 * In addition to a value, this class defines paint attributes to give some control over the
 * appearance of the marker.  The render can, however, override these settings if it chooses.
 * <P>
 * This class is immutable.
 */
public class Marker {

    /** The constant value. */
    protected double value;

    /** The outline paint. */
    protected Paint outlinePaint;

    /** The outline stroke. */
    protected Stroke outlineStroke;

    /** The paint. */
    protected Paint paint;

    /** The alpha transparency. */
    protected float alpha;

    /**
     * Constructs a new marker.
     *
     * @param value  The value.
     */
    public Marker(double value) {
        this(value, Color.gray, new java.awt.BasicStroke(0.5f), Color.red, 0.80f);
    }

    /**
     * Constructs a new marker.
     *
     * @param value  The value.
     * @param outlinePaint  The outline paint.
     * @param outlineStroke  The outline stroke.
     * @param paint  The paint.
     * @param alpha  The alpha transparency.
     */
    public Marker(double value, Paint outlinePaint, Stroke outlineStroke,
                  Paint paint, float alpha) {

        this.value = value;
        this.outlinePaint = outlinePaint;
        this.outlineStroke = outlineStroke;
        this.paint = paint;
        this.alpha = alpha;

    }

    /**
     * Returns the value.
     *
     * @return The value.
     */
    public double getValue() {
        return this.value;
    }

    /**
     * Returns the outline paint.
     *
     * @return The outline paint.
     */
    public Paint getOutlinePaint() {
        return this.outlinePaint;
    }

    /**
     * Returns the outline stroke.
     *
     * @return The outline stroke.
     */
    public Stroke getOutlineStroke() {
        return this.outlineStroke;
    }

    /**
     * Returns the paint.
     *
     * @return The paint.
     */
    public Paint getPaint() {
        return this.paint;
    }

    /**
     * Returns the alpha transparency.
     *
     * @return The alpha transparency.
     */
    public float getAlpha() {
        return this.alpha;
    }

}
