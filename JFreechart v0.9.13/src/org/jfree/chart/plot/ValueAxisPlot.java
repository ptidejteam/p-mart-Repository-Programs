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
 * ------------------
 * ValueAxisPlot.java
 * ------------------
 *
 * (C) Copyright 2003, by Object Refinery Limited and Contributors.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited);
 * Contributor(s):   -;
 *
 * Changes
 * -------
 * 07-May-2003 : Version 1 (DG);
 *
 */

package org.jfree.chart.plot;

import org.jfree.chart.axis.ValueAxis;
import org.jfree.data.Range;

/**
 * An interface that is implemented by plots that use a {@link ValueAxis}, providing a standard
 * method to find the current data range.
 *
 * @author David Gilbert
 */
public interface ValueAxisPlot {

    /**
     * Returns the data range that should apply for the specified axis.
     *
     * @param axis  the axis.
     *
     * @return The data range.
     */
    public Range getDataRange(ValueAxis axis);

    /**
     * Multiplies the range on the horizontal axis/axes by the specified factor.
     *
     * @param factor  the zoom factor.
     */
    public void zoomHorizontalAxes(double factor);

    /**
     * Zooms in on the horizontal axes.
     * 
     * @param lowerPercent  the new lower bound.
     * @param upperPercent  the new upper bound.
     */
    public void zoomHorizontalAxes(double lowerPercent, double upperPercent);

    /**
     * Multiplies the range on the vertical axis/axes by the specified factor.
     *
     * @param factor  the zoom factor.
     */
    public void zoomVerticalAxes(double factor);

    /**
     * Zooms in on the vertical axes.
     * 
     * @param lowerPercent  the new lower bound.
     * @param upperPercent  the new upper bound.
     */
    public void zoomVerticalAxes(double lowerPercent, double upperPercent);

}
