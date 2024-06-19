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
 * -------------------------------
 * OverlaidVerticalNumberAxis.java
 * -------------------------------
 * (C) Copyright 2001, 2002, by Bill Kelemen.
 *
 * Original Author:  Bill Kelemen;
 * Contributor(s):   -;
 *
 * $Id: OverlaidVerticalNumberAxis.java,v 1.1 2007/10/10 19:02:39 vauchers Exp $
 *
 * Changes:
 * --------
 * 06-Dec-2001 : Version 1 (BK);
 * 12-Dec-2001 : Minor change due to grid lines bug fix (DG);
 * 08-Jan-2002 : Moved to new package com.jrefinery.chart.combination (DG);
 * 06-Mar-2002 : Updated import statements (DG);
 *
 */

package com.jrefinery.chart.combination;

import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.util.List;
import java.util.Iterator;
import com.jrefinery.chart.Plot;
import com.jrefinery.chart.VerticalAxis;
import com.jrefinery.chart.VerticalNumberAxis;

public class OverlaidVerticalNumberAxis extends CombinedVerticalNumberAxis {

    // list of all the CombinedVerticalNumberAxis we contain
    private List axes;

    private CombinedPlot plot;

    /**
     * Constructor.
     * @param plot CombinedPlot where this OverlaidVerticalNumberAxis will be
     *        contained.
     */
    public OverlaidVerticalNumberAxis(CombinedPlot plot) {

        super((VerticalNumberAxis)plot.getVerticalAxis(), false);
        this.plot = plot;
        this.axes = plot.getVerticalAxes();

        // validate type of axes and tell each axis that they are overlaid
        boolean oneVisible = false;
        Iterator iterator = axes.iterator();
        while (iterator.hasNext()) {
            Object axis = iterator.next();
            if ((axis instanceof CombinedVerticalNumberAxis)) {
                CombinedVerticalNumberAxis combAxis = (CombinedVerticalNumberAxis)axis;
                oneVisible |= combAxis.isVisible();
                if (iterator.hasNext() || oneVisible) {
                    combAxis.setGridLinesVisible(false);

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