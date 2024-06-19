/* =======================================
 * JFreeChart : a Java Chart Class Library
 * =======================================
 *
 * Project Info:  http://www.object-refinery.com/jfreechart
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
 * ---------------------------------
 * CombinedHorizontalNumberAxis.java
 * ---------------------------------
 * (C) Copyright 2001, 2002, by Bill Kelemen.
 *
 * Original Author:  Bill Kelemen;
 * Contributor(s):   -;
 *
 * $Id: CombinedHorizontalNumberAxis.java,v 1.1 2007/10/10 18:57:57 vauchers Exp $
 *
 * Changes:
 * --------
 * 06-Dec-2001 : Version 1 (BK);
 * 12-Dec-2001 : Minor change due to grid lines bug fix (DG);
 * 08-Jan-2002 : Moved to new package com.jrefinery.chart.combination (DG);
 * 15-Feb-2002 : Added code to set crosshair visibility in constructor (DG);
 * 25-Feb-2002 : Updated constructor for new autoRangeStickyZero flag (DG);
 *
 */

package com.jrefinery.chart.combination;

import java.awt.Graphics2D;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.util.Iterator;
import com.jrefinery.chart.Axis;
import com.jrefinery.chart.HorizontalNumberAxis;
import com.jrefinery.chart.Plot;
import com.jrefinery.chart.Tick;

/**
 * A combined horizontal number axis used internally by CombinedPlot.
 * Depending on its attributes can displsy or not the axis and its labels.
 * Implements all the rest of the calculations.
 *
 * @see CombinedPlot
 * @author Bill Kelemen (bill@kelemen-usa.com)
 */
public class CombinedHorizontalNumberAxis extends HorizontalNumberAxis implements CombinableAxis {

    /** The parent axis. */
    private HorizontalNumberAxis parentAxis = null;

    /** Flag controlling the visibility of the axis. */
    private boolean visible = false;

    /** To assure all combined axis use the same height. */
    private double reserveHeight = 0;

    /**
     * Constructs a blank horizontal number axis.
     * @param axis Parent HorizontalNumberAxis to take as reference.
     * @param display Indicates if the axis is visible
     */
    public CombinedHorizontalNumberAxis(HorizontalNumberAxis axis, boolean visible) {

        super(axis.getLabel(),
              axis.getLabelFont(),
              axis.getLabelPaint(),
              axis.getLabelInsets(),
              axis.isTickLabelsVisible(),
              axis.getTickLabelFont(),
              axis.getTickLabelPaint(),
              axis.getTickLabelInsets(),
              axis.getVerticalTickLabels(),
              axis.isTickMarksVisible(),
              axis.getTickMarkStroke(),
              axis.isAutoRange(),
              axis.autoRangeIncludesZero(),
              axis.autoRangeStickyZero(),
              axis.getAutoRangeMinimumSize(),
              axis.getMinimumAxisValue(),
              axis.getMaximumAxisValue(),
              axis.isInverted(),
              axis.isAutoTickUnitSelection(),
              axis.getTickUnit(),
              axis.isGridLinesVisible(),
              axis.getGridStroke(),
              axis.getGridPaint(),
              axis.getCrosshairValue(),
              axis.getCrosshairStroke(),
              axis.getCrosshairPaint());

        this.parentAxis = axis;
        this.visible = visible;
        this.setCrosshairVisible(axis.isCrosshairVisible());

    }

    //////////////////////////////////////////////////////////////////////////////
    // Methods froom Axis
    //////////////////////////////////////////////////////////////////////////////

    /**
     * If the axis is not visible, just draws grid lines if needed, but no
     * horizontal axis labels.
     * @param g2 The graphics device;
     * @param drawArea The area within which the chart should be drawn;
     * @param plotArea The area within which the plot should be drawn (a subset of the drawArea);
     */
    public void draw(Graphics2D g2, Rectangle2D drawArea, Rectangle2D plotArea) {

        if (visible) {
            this.parentAxis.draw(g2, drawArea, plotArea);
        }
        else {
            if (gridLinesVisible) {
                refreshTicks(g2, drawArea, plotArea);
                Iterator iterator = ticks.iterator();
                while (iterator.hasNext()) {
                    Tick tick = (Tick)iterator.next();
                    float xx = (float)translateValueToJava2D(tick.getNumericalValue(), plotArea);
                    g2.setStroke(gridStroke);
                    g2.setPaint(gridPaint);
                    Line2D gridline = new Line2D.Float(xx, (float)plotArea.getMaxY(), xx,
                                                           (float)plotArea.getMinY());
                    g2.draw(gridline);
                }
            }
        }
    }

    //////////////////////////////////////////////////////////////////////////////
    // From HorizontalAxis
    //////////////////////////////////////////////////////////////////////////////

    /**
     * Returns the height required to draw the axis in the specified draw area. If
     * the axis is not visible, returns zero.
     * @param g2 The graphics device;
     * @param plot The plot that the axis belongs to;
     * @param drawArea The area within which the plot should be drawn;
     */
    public double reserveHeight(Graphics2D g2, Plot plot, Rectangle2D drawArea) {

        if (!visible) {
            return 0;
        }
        else if (reserveHeight > 0) {
            return reserveHeight;
        }
        else {
            return this.parentAxis.reserveHeight(g2, plot, drawArea);
        }

    }

    /**
     * Returns area in which the axis will be displayed. If the axis is not visible
     * returns a zero size rectangle.
     * @param g2 The graphics device;
     * @param plot A reference to the plot;
     * @param drawArea The area within which the plot and axes should be drawn;
     * @param reservedWidth The space already reserved for the vertical axis;
     */
    public Rectangle2D reserveAxisArea(Graphics2D g2, Plot plot, Rectangle2D drawArea,
                                       double reservedWidth) {

        if (visible) {
            return this.parentAxis.reserveAxisArea(g2, plot, drawArea, reservedWidth);
        }
        else {
            return new Rectangle2D.Double();
        }

    }

    //////////////////////////////////////////////////////////////////////////////
    // From CombinedAxis
    //////////////////////////////////////////////////////////////////////////////

    /**
     * Returns our parent axis.
     */
    public Axis getParentAxis() {

        return this.parentAxis;

    }

    /**
     * Returns the AxisRange (min/max) of our Axis
     */
    public AxisRange getRange() {
        autoAdjustRange();
        return (new NumberAxisRange(new Double(getMinimumAxisValue()),
                                    new Double(getMaximumAxisValue())));
    }

    /**
     * Sets our AxisRange (min/max). This is done after a CombinedPlot has
     * has calculated the overall range of all CombinedAxis that share the same
     * Axis for all Plots. This makes all plots display the complete range of
     * their Datasets.
     */
    public void setRange(AxisRange range) {

        setAutoRange(false);
        Number min = (Number)range.getMin();
        Number max = (Number)range.getMax();
        setMinimumAxisValue(min.doubleValue());
        setMaximumAxisValue(max.doubleValue());
        if (visible) {
            HorizontalNumberAxis axis = (HorizontalNumberAxis)getParentAxis();
            axis.setAutoRange(false);
            axis.setMinimumAxisValue(min.doubleValue());
            axis.setMaximumAxisValue(max.doubleValue());
        }
    }

    /**
     * Sets the visible flag on or off for this combined axis. A visible axis will
     * display the axis title, ticks and legend depending on the parent's
     * attributes. An invisible axis will not display anything. If the invisible
     * axis isContainer(), then it occupies space on the graphic device.
     */
    public void setVisible(boolean flag) {
        visible = flag;
    }

    /**
     * Is this axis visible? Is is drawn?
     */
    public boolean isVisible() {
        return visible;
    }

    /**
     * The CombinedPlot will calculate the maximim of all reserveWidth or reserveHeight
     * depending on the type of CombinedPlot and inform all CombinedXXXXXAxis to store
     * this value.
     * @param dimension If the axis is vertical, this is width. If axis is
     *        horizontal, then this is height
     */
    public void setReserveDimension(double dimension) {
        this.reserveHeight = dimension;
    }

}
