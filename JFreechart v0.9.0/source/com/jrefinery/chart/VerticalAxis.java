/* =======================================
 * JFreeChart : a Java Chart Class Library
 * =======================================
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
 * -----------------
 * VerticalAxis.java
 * -----------------
 * (C) Copyright 2000-2002, by Simba Management Limited.

 * Original Author:  David Gilbert (for Simba Management Limited);
 * Contributor(s):   -;
 *
 * $Id: VerticalAxis.java,v 1.1 2007/10/10 19:01:18 vauchers Exp $
 *
 * Changes (from 18-Sep-2001)
 * --------------------------
 * 18-Sep-2001 : Added standard header and fixed DOS encoding problem (DG);
 * 25-Feb-2002 : Updated Javadoc comments (DG);
 *
 */

package com.jrefinery.chart;

import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;

/**
 * An interface that must be supported by all vertical axes for layout purposes.
 */
public interface VerticalAxis {

    /**
     * Estimates the area required to draw the axis, assuming that the horizontal axis has already
     * reserved the specified height.
     * @param g2 The graphics device.
     * @param plot The plot that the axis belongs to.
     * @param drawArea The area within which the plot should be drawn.
     * @param reservedHeight The height reserved by the horizontal axis.
     */
    public Rectangle2D reserveAxisArea(Graphics2D g2, Plot plot, Rectangle2D drawArea,
				       double reservedHeight);

    /**
     * Estimates the width required to draw the axis.
     * @param g2 The graphics device.
     * @param plot The plot that the axis belongs to.
     * @param drawArea The area within which the plot should be drawn.
     */
    public double reserveWidth(Graphics2D g2, Plot plot, Rectangle2D drawArea);

}
