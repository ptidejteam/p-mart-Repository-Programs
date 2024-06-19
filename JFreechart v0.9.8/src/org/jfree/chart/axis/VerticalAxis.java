/* ======================================
 * JFreeChart : a free Java chart library
 * ======================================
 *
 * Project Info:  http://www.jfree.org/jfreechart/index.html
 * Project Lead:  David Gilbert (david.gilbert@object-refinery.com);
 *
 * (C) Copyright 2000-2003, by Simba Management Limited and Contributors.
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
 * -----------------
 * VerticalAxis.java
 * -----------------
 * (C) Copyright 2000-2003, by Simba Management Limited.

 * Original Author:  David Gilbert (for Simba Management Limited);
 * Contributor(s):   -;
 *
 * $Id: VerticalAxis.java,v 1.1 2007/10/10 20:03:22 vauchers Exp $
 *
 * Changes (from 18-Sep-2001)
 * --------------------------
 * 18-Sep-2001 : Added standard header and fixed DOS encoding problem (DG);
 * 25-Feb-2002 : Updated Javadoc comments (DG);
 * 01-Oct-2002 : Fixed errors reported by Checkstyle (DG);
 * 08-Nov-2002 : Moved to new package com.jrefinery.chart.axis (DG);
 * 17-Jan-2003 : Moved plot classes to separate package (DG);
 *
 */

package org.jfree.chart.axis;

import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;

import org.jfree.chart.plot.Plot;

/**
 * An interface that must be supported by all vertical axes for layout purposes.
 * 
 * @see org.jfree.chart.axis.HorizontalAxis
 *
 * @author David Gilbert
 */
public interface VerticalAxis {

    /**
     * Returns the width of the axis.
     *
     * @param g2  the graphics device.
     * @param plot  the plot that the axis belongs to.
     * @param drawArea  the area within which the plot should be drawn.
     * @param location  the axis location.
     *
     * @return the estimated width required to draw the axis.
     */
    public double reserveWidth(Graphics2D g2, Plot plot, Rectangle2D drawArea, int location);

    /**
     * Returns the width of the axis, assuming that the horizontal axis has already reserved
     * the specified height.
     *
     * @param g2  the graphics device.
     * @param plot  the plot that the axis belongs to.
     * @param drawArea  the area within which the plot should be drawn.
     * @param location  the axis location.
     * @param reservedHeight  the height reserved by the horizontal axis.
     * @param horizontalAxisLocation  the horizontal axis location.
     *
     * @return the estimated width required to draw the axis.
     */
    public double reserveWidth(Graphics2D g2, Plot plot, Rectangle2D drawArea, int location,
                               double reservedHeight, int horizontalAxisLocation);

}
