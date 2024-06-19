/* =======================================
 * JFreeChart : a Java Chart Class Library
 * =======================================
 *
 * Project Info:  http://www.jrefinery.com/jfreechart
 * Project Lead:  David Gilbert (david.gilbert@jrefinery.com);
 *
 * This file...
 * $Id: Bar.java,v 1.1 2007/10/10 18:53:20 vauchers Exp $
 *
 * Original Author:  David Gilbert;
 * Contributor(s):   -;
 *
 * (C) Copyright 2000, 2001, Simba Management Limited;
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
 * Changes (from 21-Aug-2001)
 * --------------------------
 * 21-Aug-2001 : Added standard header.  Fixed DOS encoding problem (DG);
 * 18-Sep-2001 : Updated e-mail address in header (DG);
 *
 */

package com.jrefinery.chart;

import java.awt.*;
import java.awt.geom.*;

/**
 * Represents all the visual characteristics of a single bar in a BarPlot.
 * @see BarPlot
 */
public class Bar {

    /** A rectangle defining the area of the bar. */
    protected Rectangle2D area;

    /** The Stroke used to draw the outline of the bar. */
    protected Stroke outlineStroke;

    /** The Paint used to draw the outline of the bar. */
    protected Paint outlinePaint;

    /** The Paint used to draw the interior of the bar. */
    protected Paint fillPaint;

    /**
     * Full constructor: returns a new Bar object with attributes as defined by the caller.
     * @param x The x-coordinate of the bar;
     * @param y The y-coordinate of the bar;
     * @param width The width of the bar;
     * @param height The height of the bar;
     * @param outlineStroke The Stroke used to draw the outline of the bar;
     * @param outlinePaint The Paint used to draw the outline of the bar;
     * @param fillPaint The Paint used to draw the interior of the bar.
     */
    public Bar(double x, double y, double width, double height,
	       Stroke outlineStroke, Paint outlinePaint, Paint fillPaint)
    {
	this.area = new Rectangle2D.Double(x, y, width, height);
	this.outlineStroke = outlineStroke;
	this.outlinePaint = outlinePaint;
	this.fillPaint = fillPaint;
    }

    /**
     * Standard constructor: returns a new Bar object, with some default attributes.
     * @param x The x-coordinate of the bar;
     * @param y The y-coordinate of the bar;
     * @param width The width of the bar;
     * @param height The height of the bar;
     */
    public Bar(double x, double y, double width, double height) {
	this(x, y, width, height, new BasicStroke(), Color.gray, Color.green);
    }

    /**
     * Returns the rectangle that is the outline of the bar.
     * @return The outline of the bar;
     */
    public Rectangle2D getArea() {
	return area;
    }

    /**
     * Returns the Stroke object used to draw the outline of the bar.
     * @return The Stroke used to draw the outline of the bar.
     */
    public Stroke getOutlineStroke() {
	return outlineStroke;
    }

    /**
     * Returns the Paint object used to draw the outline of the bar.
     * @return The Paint used to draw the outline of the bar.
     */
    public Paint getOutlinePaint() {
	return outlinePaint;
    }

    /**
     * Returns the Paint object used to fill the bar.
     * @return The Paint used to fill the bar.
     */
    public Paint getFillPaint() {
	return fillPaint;
    }

    /**
     * Returns the width of the bar.
     * @return The width of the bar.
     */
    public double getWidth() {
	return area.getWidth();
    }

}
