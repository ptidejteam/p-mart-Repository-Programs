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
 * CombinedHorizontalDateAxis.java
 * -------------------------------
 * (C) Copyright 2001, 2002, by Bill Kelemen and Contributors.
 *
 * Original Author:  Bill Kelemen;
 * Contributor(s):   David Gilbert (for Simba Management Limited);
 *                   Jonathan Nash;
 *
 * $Id: CombinedHorizontalDateAxis.java,v 1.1 2007/10/10 18:59:10 vauchers Exp $
 *
 * Changes:
 * --------
 * 06-Dec-2001 : Version 1 (BK);
 * 12-Dec-2001 : Minor change due to grid lines bug fix (DG);
 * 08-Jan-2002 : Moved to new package com.jrefinery.chart.combination (DG);
 * 16-Jan-2002 : Added an optional crosshair, based on the implementation by Jonathan Nash (DG);
 * 15-Feb-2002 : Added code to set crosshair visibility in constructor (DG);
 * 25-Feb-2002 : Updated import statements (DG);
 *
 */

package com.jrefinery.chart.combination;

import java.awt.Graphics2D;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.util.Date;
import java.util.Iterator;
import com.jrefinery.chart.Axis;
import com.jrefinery.chart.HorizontalDateAxis;
import com.jrefinery.chart.Plot;
import com.jrefinery.chart.Tick;

/**
 * A combined horizontal date axis used internally by CombinedPlot.  Depending on its attributes
 * can display or not the axis and its labels.  Implements all the rest of the calculations.
 *
 * @see CombinedPlot
 * @author Bill Kelemen (bill@kelemen-usa.com)
 */
public class CombinedHorizontalDateAxis extends HorizontalDateAxis implements CombinableAxis {

    /** The parent axis. */
    private HorizontalDateAxis parentAxis = null;

    /** Flag controlling the visibility of the axis. */
    private boolean visible = false;

    /** To assure all combined axis use the same height. */
    private double reserveHeight = 0;

    /**
     * Constructs a CombinedHorizontalDateAxis, based on a HorizontalDateAxis.
     *
     * @param axis The parent axis.
     * @param visible Flag controlling the visibility of the combined axis.
     */
    public CombinedHorizontalDateAxis(HorizontalDateAxis axis, boolean visible) {

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
              axis.getMinimumDate(),
              axis.getMaximumDate(),
              axis.isAutoTickUnitSelection(),
              axis.getTickUnit(),
              axis.getTickLabelFormatter(),
              axis.isGridLinesVisible(),
              axis.getGridStroke(),
              axis.getGridPaint(),
              axis.isCrosshairVisible(),
              axis.getCrosshairDate(),
              axis.getCrosshairStroke(),
              axis.getCrosshairPaint());

        this.parentAxis = axis;
        this.visible = visible;
        this.setCrosshairVisible(axis.isCrosshairVisible());

    }

    //////////////////////////////////////////////////////////////////////////////
    // From Axis
    //////////////////////////////////////////////////////////////////////////////

    /**
     * If axis is not visible, just draws grid lines if needed, but no horizontal date axis labels.
     * @param g2 The graphics device.
     * @param drawArea The area within which the chart should be drawn.
     * @param plotArea The area within which the plot should be drawn (a subset of the drawArea).
     */
    public void draw(Graphics2D g2, Rectangle2D drawArea, Rectangle2D plotArea) {

        if (visible) {
            parentAxis.draw(g2, drawArea, plotArea);
        }
        else if (gridLinesVisible) {
            refreshTicks(g2, drawArea, plotArea);
            g2.setStroke(gridStroke);
            g2.setPaint(gridPaint);
            Iterator iterator = ticks.iterator();
            while (iterator.hasNext()) {
                Tick tick = (Tick)iterator.next();
                float xx = (float)translateValueToJava2D(tick.getNumericalValue(), plotArea);
                Line2D gridline = new Line2D.Float(xx, (float)plotArea.getMaxY(),
                                                   xx, (float)plotArea.getMinY());
                g2.draw(gridline);
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
            return parentAxis.reserveHeight(g2, plot, drawArea);
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
            return parentAxis.reserveAxisArea(g2, plot, drawArea, reservedWidth);
        }
        else {
            return new Rectangle2D.Double();
        }
    }

    //////////////////////////////////////////////////////////////////////////////
    // Extra
    //////////////////////////////////////////////////////////////////////////////

    /**
     * Returns the parent axis.
     */
    public Axis getParentAxis() {
        return this.parentAxis;
    }

    /**
     * Returns the range (minimum and maximum) for this axis.
     */
    public AxisRange getRange() {

        DateAxisRange result = null;

        if (visible) {
            parentAxis.autoAdjustRange();
            result = new DateAxisRange(parentAxis.getMinimumDate(), parentAxis.getMaximumDate());
        }
        else {
            autoAdjustRange();
            result = new DateAxisRange(getMinimumDate(), getMaximumDate());
        }

        return result;
    }

    /**
     * Sets the axis range (minimum and maximum). This is done after a CombinedPlot has calculated
     * the overall range of all CombinedAxis that share the same Axis for all Plots.  This makes
     * all plots display the complete range of their Datasets.
     */
    public void setRange(AxisRange range) {

        setAutoRange(false);
        setMinimumDate((Date)range.getMin());
        setMaximumDate((Date)range.getMax());
        if (visible) {
            parentAxis.setAutoRange(false);
            parentAxis.setMinimumDate((Date)range.getMin());
            parentAxis.setMaximumDate((Date)range.getMax());
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