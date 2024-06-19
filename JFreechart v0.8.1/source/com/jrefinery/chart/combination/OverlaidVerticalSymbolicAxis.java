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
 * -----------------------------------
 * OverlaidVerticalSymbolicAxis.java
 * -----------------------------------
 *
 * Original Author:  Bill Kelemen;
 * Contributor(s):   David Gilbert (for Simba Management Limited), Anthony Boulestreau;
 *
 * $Id: OverlaidVerticalSymbolicAxis.java,v 1.1 2007/10/10 19:02:39 vauchers Exp $
 *
 * Changes:
 * --------
 * 29-Mar-2002: Version 1 (AB);
 *
 */

package com.jrefinery.chart.combination;

import java.awt.*;
import java.awt.font.*;
import java.awt.geom.*;
import java.util.*;
import com.jrefinery.chart.Plot;
import com.jrefinery.chart.VerticalAxis;
import com.jrefinery.chart.VerticalSymbolicAxis;

public class OverlaidVerticalSymbolicAxis extends CombinedVerticalSymbolicAxis {

    // list of all the CombinedVerticalSymbolicAxis we contain
    private java.util.List axes;

    private CombinedPlot plot;

    /**
     * Constructor.
     * @param plot CombinedPlot where this OverlaidVerticalSymbolicAxis will be
     *        contained.
     */
    public OverlaidVerticalSymbolicAxis(CombinedPlot plot) {

        super((VerticalSymbolicAxis)plot.getVerticalAxis(), false);
        this.plot = plot;
        this.axes = plot.getVerticalAxes();

        // validate type of axes and tell each axis that they are overlaid
        boolean oneVisible = false;
        Iterator iter = axes.iterator();
        while (iter.hasNext()) {
            Object axis = iter.next();
            if ((axis instanceof CombinedVerticalSymbolicAxis)) {
                CombinedVerticalSymbolicAxis combAxis = (CombinedVerticalSymbolicAxis)axis;
                oneVisible |= combAxis.isVisible();
                if (iter.hasNext() || oneVisible) {
                    combAxis.setGridLinesVisible(false);
					combAxis.setSymbolicGridLinesVisible(false);
                }
            } else {
                throw new IllegalArgumentException("Can not combine " + axis.getClass()
                                         + " into " + this.getClass() );
            }
        }
    }

    //////////////////////////////////////////////////////////////////////////////
    // From Axis
    //////////////////////////////////////////////////////////////////////////////

    /**
     * Does nothing.
     * @param g2 The graphics device;
     * @param drawArea The area within which the chart should be drawn;
     * @param plotArea The area within which the plot should be drawn (a subset of the drawArea).
     */
    public void draw(Graphics2D g2, Rectangle2D drawArea, Rectangle2D plotArea) {
    }

    //////////////////////////////////////////////////////////////////////////////
    // From HorizontalAxis
    //////////////////////////////////////////////////////////////////////////////

    /**
     * Returns the width required to draw the axis in the specified draw area. The
     * list of our axes is checked and the first non zero width is returned.
     * @param g2 The graphics device;
     * @param plot The plot that the axis belongs to;
     * @param drawArea The area within which the plot should be drawn;
     */
    public double reserveWidth(Graphics2D g2, Plot plot, Rectangle2D drawArea) {

        Iterator iter = axes.iterator();
        while (iter.hasNext()) {
            VerticalAxis axis = (VerticalAxis)iter.next();
            double width = axis.reserveWidth(g2, plot, drawArea);
            if (width != 0) {
                return width;
            }
        }
        return 0;
    }

    /**
     * Returns area in which the axis will be displayed. The list is our axes is
     * checked and the first non zero area is returned.
     * @param g2 The graphics device;
     * @param plot A reference to the plot;
     * @param drawArea The area within which the plot and axes should be drawn;
     * @param reservedWidth The space already reserved for the vertical axis;
     */
    public Rectangle2D reserveAxisArea(Graphics2D g2, Plot plot, Rectangle2D drawArea,
                                       double reservedWidth) {

        Rectangle2D empty = new Rectangle2D.Double();
        Iterator iter = axes.iterator();
        while (iter.hasNext()) {
            VerticalAxis axis = (VerticalAxis)iter.next();
            Rectangle2D area = axis.reserveAxisArea(g2, plot, drawArea, reservedWidth);
            if (!area.equals(empty)) {
                return area;
            }
        }
        return empty;

    }

    //////////////////////////////////////////////////////////////////////////////
    // Extra
    //////////////////////////////////////////////////////////////////////////////

    /**
     * Returns the AxisRange (min/max) of our Axis
     */
    public AxisRange getRange() {
        return plot.getRange(axes);
    }

    /**
     * Sets our AxisRange (min/max). This is done after a CombinedPlot has
     * has calculated the overall range of all CombinedAxis that share the same
     * Axis for all Plots. This makes all plots display the complete range of
     * their Datasets.
     */
    public void setRange(AxisRange range) {
        setAutoRange(false);
        plot.setRange(range, axes);
    }

}