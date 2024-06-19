/* =======================================
 * JFreeChart : a Java Chart Class Library
 * =======================================
 *
 * Project Info:  http://www.jrefinery.com/jfreechart
 * Project Lead:  David Gilbert (david.gilbert@jrefinery.com);
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
 * ---------
 * Line.java
 * ---------
 * (C) Copyright 2000-2002, by Simba Management Limited.
 *
 * Original Author:  David Gilbert (for Simba Management Limited);
 * Contributor(s):   -;
 *
 * $Id: Line.java,v 1.1 2007/10/10 18:55:43 vauchers Exp $
 *
 * Changes (from 18-Sep-2001)
 * --------------------------
 * 18-Sep-2001 : Added standard header (DG);
 *
 */

package com.jrefinery.chart;

import java.awt.*;
import java.awt.geom.*;

/**
 * Represents one line in the line plot.
 */
public class Line {

    private Line2D line;    // the position of the line
    private Stroke stroke;
    private Paint paint;

    /**
     * Standard constructor, with default values for the colors.
     */
    public Line(double x1, double y1, double x2, double y2) {
	this(x1, y1, x2, y2, new BasicStroke(), Color.blue);
    }

    /**
     * Standard constructor.
     */
    public Line(double x1, double y1, double x2, double y2, Stroke stroke, Paint paint) {
	this.line = new Line2D.Double(x1, y1, x2, y2);
	this.stroke = stroke;
	this.paint = paint;
    }

    /**
     * Returns the line.
     */
    public Line2D getLine() {
	return line;
    }

    /**
     * Returns the Stroke object used to draw the line.
     */
    public Stroke getStroke() {
	return stroke;
    }

    /**
     * Returns the Paint object used to color the line.
     */
    public Paint getPaint() {
	return paint;
    }

}
