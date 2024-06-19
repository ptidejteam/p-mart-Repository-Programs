/* ======================================
 * JFreeChart : a free Java chart library
 * ======================================
 *
 * Project Info:  http://www.object-refinery.com/jfreechart/index.html
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
 * -----------------------
 * VerticalNumberAxis.java
 * -----------------------
 * (C) Copyright 2000-2003, by Simba Management Limited and Contributors.
 *
 * Original Author:  David Gilbert (for Simba Management Limited);
 * Contributor(s):   David Li;
 *                   Jonathan Nash;
 *                   Richard Atkinson;
 *
 * $Id: VerticalNumberAxis.java,v 1.1 2007/10/10 19:57:50 vauchers Exp $
 *
 * Changes (from 23-Jun-2001)
 * --------------------------
 * 23-Jun-2001 : Modified to work with null data source (DG);
 * 18-Sep-2001 : Updated header and fixed DOS encoding problem (DG);
 * 07-Nov-2001 : Updated configure() method.  Replaced some hard-coded defaults. (DG);
 * 12-Dec-2001 : Minor change due to grid lines bug fix (DG);
 * 08-Jan-2002 : Added flag to allow axis to be inverted (DG);
 * 16-Jan-2002 : Added an optional crosshair, based on the implementation by Jonathan Nash (DG);
 * 25-Feb-2002 : Changed autoAdjustRange() from public to protected, and modified the calculation
 *               to take into account the new autoRangeStickyZero flag.  Updated constructors for
 *               new flag.  Updated import statements (DG);
 * 06-Mar-2002 : Changed labelDrawnVertical --> verticalLabel to be consistent with other
 *               vertical axes (DG);
 * 22-Apr-2002 : drawVerticalString(...) is now drawRotatedString(...) in RefineryUtilities.
 *               Simplified autoAdjustRange() method (DG);
 * 01-May-2002 : Updated for changes in TickUnit class (DG);
 * 25-Jun-2002 : Fixed bug in auto range calculation...when auto range includes zero, the upper
 *               and lower margins were being calculated on the range not including zero (DG);
 * 25-Jul-2002 : Changed order of parameters in ValueAxis constructor (DG);
 * 06-Aug-2002 : Modified draw method to not draw axis label if label is empty String (RA);
 * 05-Sep-2002 : Updated constructors to reflect changes in the Axis class, and changed draw method
 *               to observe tickMarkPaint (DG);
 * 22-Sep-2002 : Fixed errors reported by Checkstyle (DG);
 * 16-Oct-2002 : Changed calculation for vertical position of tick labels (DG);
 * 24-Oct-2002 : Added number format override mechanism (DG);
 * 08-Nov-2002 : Moved to new package com.jrefinery.chart.axis (DG);
 * 18-Nov-2002 : Moved responsibility for drawing grid lines to the XYPlot class (DG);
 * 19-Nov-2002 : Removed grid settings (now controlled by the plot) (DG);
 * 03-Jan-2002 : Fixed small problem in the auto-range minimum (DG);
 * 14-Jan-2003 : Changed autoRangeMinimumSize from Number --> double, and moved crosshair settings
 *               to the plot classes (DG);
 * 20-Jan-2003 : Removed unnecessary constructors (DG);
 *
 */

package com.jrefinery.chart.axis;

import java.awt.Font;
import java.awt.Insets;
import java.awt.Graphics2D;
import java.awt.font.FontRenderContext;
import java.awt.font.LineMetrics;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.text.NumberFormat;
import java.util.Iterator;
import com.jrefinery.chart.event.AxisChangeEvent;
import com.jrefinery.chart.plot.Plot;
import com.jrefinery.chart.plot.VerticalValuePlot;
import com.jrefinery.data.Range;

/**
 * A standard linear value axis, for values displayed vertically.
 * <P>
 * Note that bug 4273469 on the Java Developer Connection talks about why the
 * grid lines don't always line up with the tick marks precisely.
 *
 * @author David Gilbert
 */
public class VerticalNumberAxis extends NumberAxis implements VerticalAxis {

    /** The default setting for the vertical-label flag. */
    public static final boolean DEFAULT_VERTICAL_LABEL = true;
    
    /** A flag indicating whether or not the axis label is drawn vertically. */
    private boolean verticalLabel;

    /**
     * Constructs a vertical number axis, using default attribute values where necessary.
     *
     * @param label  the axis label (null permitted).
     */
    public VerticalNumberAxis(String label) {

        super(label);

        this.verticalLabel = DEFAULT_VERTICAL_LABEL;

    }

    /**
     * Returns a flag indicating whether or not the axis label is drawn vertically.
     *
     * @return  the flag.
     */
    public boolean isVerticalLabel() {
        return this.verticalLabel;
    }

    /**
     * Sets a flag indicating whether or not the axis label is drawn vertically.
     * If the setting is changed, registered listeners are notified that the
     * axis has changed.
     *
     * @param flag  the new flag.
     */
    public void setVerticalLabel(boolean flag) {

        if (this.verticalLabel != flag) {
            this.verticalLabel = flag;
            notifyListeners(new AxisChangeEvent(this));
        }

    }

    /**
     * Configures the axis to work with the specified plot.  If the axis has
     * auto-scaling, then sets the maximum and minimum values.
     */
    public void configure() {

        if (isAutoRange()) {
            autoAdjustRange();
        }

    }

    /**
     * Converts a data value to a coordinate in Java2D space, assuming that the
     * axis runs along one edge of the specified dataArea.
     * <p>
     * Note that it is possible for the coordinate to fall outside the plotArea.
     *
     * @param value  the data value.
     * @param dataArea  the area for plotting the data.
     *
     * @return the Java2D coordinate.
     */
    public double translateValueToJava2D(double value, Rectangle2D dataArea) {

        Range range = getRange();
        double axisMin = range.getLowerBound();
        double axisMax = range.getUpperBound();

        double maxY = dataArea.getMaxY();
        double minY = dataArea.getMinY();

        if (isInverted()) {
            return minY + (((value - axisMin) / (axisMax - axisMin)) * (maxY - minY));
        }
        else {
            return maxY - (((value - axisMin) / (axisMax - axisMin)) * (maxY - minY));
        }

    }

    /**
     * Converts a coordinate in Java2D space to the corresponding data value,
     * assuming that the axis runs along one edge of the specified dataArea.
     *
     * @param java2DValue  the coordinate in Java2D space.
     * @param dataArea  the area in which the data is plotted.
     *
     * @return the data value.
     */
    public double translateJava2DtoValue(float java2DValue, Rectangle2D dataArea) {

        Range range = getRange();
        double axisMin = range.getLowerBound();
        double axisMax = range.getUpperBound();
        double plotY = dataArea.getY();
        double plotMaxY = dataArea.getMaxY();

        if (isInverted()) {
            return axisMin + (java2DValue - plotY) / (plotMaxY - plotY) * (axisMax - axisMin);
        }
        else {
            return axisMax - (java2DValue - plotY) / (plotMaxY - plotY) * (axisMax - axisMin);
        }

    }

    /**
     * Sets the axis minimum and maximum values so that all the data is visible.
     * <P>
     * You can control the range calculation in several ways.  First, you can
     * define upper and lower margins as a percentage of the data range (the
     * default is a 5% margin for each). Second, you can set a flag that forces
     * the range to include zero.  Finally, you can set another flag, the
     * 'sticky zero' flag, that only affects the range when zero falls within
     * the axis margins.  When this happens, the margin is truncated so that
     * zero is the upper or lower limit for the axis.
     */
    protected void autoAdjustRange() {

        Plot plot = getPlot();
        if (plot == null) {
            return;  // no plot, no data
        }

        if (plot instanceof VerticalValuePlot) {

            VerticalValuePlot vvp = (VerticalValuePlot) plot;
            Range r = vvp.getVerticalDataRange(this);
            if (r == null) {
                r = new Range(DEFAULT_LOWER_BOUND, DEFAULT_UPPER_BOUND);
            }
            double lower = r.getLowerBound();
            double upper = r.getUpperBound();
            double range = upper - lower;

            // ensure the autorange is at least <minRange> in size...
            double minRange = getAutoRangeMinimumSize();
            if (range < minRange) {
                double expand = (minRange - range) / 2;
                upper = upper + expand;
                lower = lower - expand;
            }

            if (autoRangeIncludesZero()) {
                if (autoRangeStickyZero()) {
                    if (upper <= 0.0) {
                        upper = 0.0;
                    }
                    else {
                        upper = upper + getUpperMargin() * (upper - Math.min(lower, 0.0));
                    }
                    if (lower >= 0.0) {
                        lower = 0.0;
                    }
                    else {
                        lower = lower - getLowerMargin() * (upper - lower);
                    }
                }
                else {
                    range = Math.max(0.0, upper) - Math.min(0.0, lower);
                    upper = Math.max(0.0, upper + getUpperMargin() * range);
                    lower = Math.min(0.0, lower - getLowerMargin() * range);
                }
            }
            else {
                if (autoRangeStickyZero()) {
                    if (upper <= 0.0) {
                        upper = Math.min(0.0, upper + getUpperMargin() * range);
                    }
                    else {
                        upper = upper + getUpperMargin() * range;
                    }
                    if (lower >= 0.0) {
                        lower = Math.max(0.0, lower - getLowerMargin() * range);
                    }
                    else {
                        lower = lower - getLowerMargin() * range;
                    }
                }
                else {
                    upper = upper + getUpperMargin() * range;
                    lower = lower - getLowerMargin() * range;
                }
            }

            setRangeAttribute(new Range(lower, upper));
        }

    }

    /**
     * Draws the plot on a Java 2D graphics device (such as the screen or a printer).
     *
     * @param g2  the graphics device.
     * @param plotArea  the area within which the plot and axes should be drawn.
     * @param dataArea  the area within which the data should be drawn.
     * @param location  the axis location.
     */
    public void draw(Graphics2D g2, Rectangle2D plotArea, Rectangle2D dataArea, int location) {

        // if the axis is not visible, don't draw it...
        if (!isVisible()) {
            return;
        }

        // draw the axis label...
        drawVerticalLabel(getLabel(), isVerticalLabel(), g2, plotArea, dataArea, location);

        // draw the tick labels and marks...
        refreshTicks(g2, plotArea, dataArea, location);
        double xx;
        double ll;
        double rr;
        if (location == LEFT) {
            xx = dataArea.getMinX();
            ll = getTickMarkOutsideLength();
            rr = getTickMarkInsideLength();
        }
        else {
            xx = dataArea.getMaxX();
            ll = getTickMarkInsideLength();
            rr = getTickMarkOutsideLength();
        }
        g2.setFont(getTickLabelFont());

        Iterator iterator = getTicks().iterator();
        while (iterator.hasNext()) {
            Tick tick = (Tick) iterator.next();
            float yy = (float) translateValueToJava2D(tick.getNumericalValue(), dataArea);
            if (isTickLabelsVisible()) {
                g2.setPaint(getTickLabelPaint());
                g2.drawString(tick.getText(), tick.getX(), tick.getY());
            }

            if (isTickMarksVisible()) {
                g2.setStroke(getTickMarkStroke());
                g2.setPaint(getTickMarkPaint());
                Line2D mark = new Line2D.Double(xx - ll, yy, xx + rr, yy);
                g2.draw(mark);
            }
        }

    }

    /**
     * Returns the width required to draw the axis in the specified draw area.
     *
     * @param g2  the graphics device.
     * @param plot  a reference to the plot;
     * @param drawArea  the area within which the plot should be drawn.
     * @param location  the axis location.
     *
     * @return  width required to draw the axis.
     */
    public double reserveWidth(Graphics2D g2, Plot plot, Rectangle2D drawArea, int location) {

        // if the axis is not visible, the width is zero...
        if (!isVisible()) {
            return 0.0;
        }

        // if the axis has a fixed dimension, return it...
        if (getFixedDimension() > 0.0) {
            return getFixedDimension();
        }

        // calculate the width of the axis label...
        double labelWidth = 0.0;
        String label = getLabel();
        if (label != null) {
            Rectangle2D labelBounds
                = getLabelFont().getStringBounds(label, g2.getFontRenderContext());
            Insets labelInsets = getLabelInsets();
            labelWidth = labelInsets.left + labelInsets.right;
            if (this.verticalLabel) {
                // assume width == height before rotation
                labelWidth = labelWidth + labelBounds.getHeight();
            }
            else {
                labelWidth = labelWidth + labelBounds.getWidth();
            }
        }

        // calculate the width required for the tick labels (if visible)
        Insets tickLabelInsets = getTickLabelInsets();
        double tickLabelWidth = tickLabelInsets.left + tickLabelInsets.right;
        if (isTickLabelsVisible()) {
            refreshTicks(g2, drawArea, drawArea, location);
            tickLabelWidth = tickLabelWidth + getMaxTickLabelWidth(g2, drawArea);
        }
        return labelWidth + tickLabelWidth;

    }

    /**
     * Returns area in which the axis will be displayed.
     *
     * @param g2  the graphics device.
     * @param plot  a reference to the plot.
     * @param plotArea  the area in which the plot and axes should be drawn.
     * @param location  the axis location.
     * @param reservedHeight  the height reserved for the horizontal axis.
     * @param horizontalAxisLocation  the horizontal axis location.
     *
     * @return  area in which the axis will be displayed.
     */
    public double reserveWidth(Graphics2D g2, Plot plot, Rectangle2D plotArea, int location,
                               double reservedHeight, int horizontalAxisLocation) {

        // if the axis is not visible, the width is zero...
        if (!isVisible()) {
            return 0.0;
        }

        // if the axis has a fixed dimension, return it...
        if (getFixedDimension() > 0.0) {
            return getFixedDimension();
        }

        // calculate the width of the axis label...
        double labelWidth = 0.0;
        String label = getLabel();
        if (label != null) {
            Rectangle2D labelBounds
                = getLabelFont().getStringBounds(label, g2.getFontRenderContext());
            Insets labelInsets = getLabelInsets();
            labelWidth = labelInsets.left + labelInsets.right;
            if (this.verticalLabel) {
                // assume width == height before rotation
                labelWidth = labelWidth + labelBounds.getHeight();
            }
            else {
                labelWidth = labelWidth + labelBounds.getWidth();
            }
        }

        // calculate the width of the tick labels
        Insets tickLabelInsets = getTickLabelInsets();
        double tickLabelWidth = tickLabelInsets.left + tickLabelInsets.right;
        if (isTickLabelsVisible()) {
            Rectangle2D approximateDataArea =
                new Rectangle2D.Double(plotArea.getX(), plotArea.getY(),
                                       plotArea.getWidth(),
                                       plotArea.getHeight() - reservedHeight);
            refreshTicks(g2, plotArea, approximateDataArea, location);
            tickLabelWidth = tickLabelWidth + getMaxTickLabelWidth(g2, approximateDataArea);
        }

        return labelWidth + tickLabelWidth;

    }

    /**
     * Selects an appropriate tick value for the axis.  The strategy is to
     * display as many ticks as possible (selected from an array of 'standard'
     * tick units) without the labels overlapping.
     *
     * @param g2  the graphics device.
     * @param plotArea  the area in which the plot and axes should be drawn.
     * @param dataArea  the area in which the plot should be drawn.
     */
    protected void selectAutoTickUnit(Graphics2D g2, Rectangle2D plotArea, Rectangle2D dataArea) {

        // calculate the tick label height...
        FontRenderContext frc = g2.getFontRenderContext();
        Insets tickLabelInsets = getTickLabelInsets();
        double tickLabelHeight = getTickLabelFont().getLineMetrics("123", frc).getHeight()
                                 + tickLabelInsets.top + tickLabelInsets.bottom;

        // now find the smallest tick unit that will accommodate the labels...
        double zero = translateValueToJava2D(0.0, dataArea);

        // start with the current tick unit...
        TickUnits tickUnits = getStandardTickUnits();
        NumberTickUnit candidate1 = (NumberTickUnit) tickUnits.getCeilingTickUnit(getTickUnit());
        double y = translateValueToJava2D(candidate1.getSize(), dataArea);
        double unitHeight = Math.abs(y - zero);

        // then extrapolate...
        double bestguess = (tickLabelHeight / unitHeight) * candidate1.getSize();
        NumberTickUnit guess = new NumberTickUnit(bestguess, null);
        NumberTickUnit candidate2 = (NumberTickUnit) tickUnits.getCeilingTickUnit(guess);

        setTickUnit(candidate2, false, false);

    }

    /**
     * Calculates the positions of the tick labels for the axis, storing the results in the
     * tick label list (ready for drawing).
     *
     * @param g2  the graphics device.
     * @param plotArea  the area in which the plot and the axes should be drawn.
     * @param dataArea  the area in which the plot should be drawn.
     * @param location  the location of the axis.
     *
     */
    public void refreshTicks(Graphics2D g2,
                             Rectangle2D plotArea, Rectangle2D dataArea,
                             int location) {

        getTicks().clear();

        Font tickLabelFont = getTickLabelFont();
        g2.setFont(tickLabelFont);

        if (isAutoTickUnitSelection()) {
            selectAutoTickUnit(g2, plotArea, dataArea);
        }

        double size = getTickUnit().getSize();
        int count = calculateVisibleTickCount();
        double lowestTickValue = calculateLowestVisibleTickValue();

        if (count <= ValueAxis.MAXIMUM_TICK_COUNT) {
            for (int i = 0; i < count; i++) {
                double currentTickValue = lowestTickValue + (i * size);
                double yy = translateValueToJava2D(currentTickValue, dataArea);
                String tickLabel;
                NumberFormat formatter = getNumberFormatOverride();
                if (formatter != null) {
                    tickLabel = formatter.format(currentTickValue);
                }
                else {
                    tickLabel = getTickUnit().valueToString(currentTickValue);
                }
                FontRenderContext frc = g2.getFontRenderContext();
                Rectangle2D tickLabelBounds = tickLabelFont.getStringBounds(tickLabel, frc);
                LineMetrics lm = tickLabelFont.getLineMetrics(tickLabel, frc);
                float x;
                if (location == LEFT) {
                    x = (float) (dataArea.getX()
                                 - tickLabelBounds.getWidth() - getTickLabelInsets().right);
                }
                else {
                    x = (float) (dataArea.getMaxX() + getTickLabelInsets().left);
                }
                float y = (float) (yy + (lm.getAscent() / 2));
                Tick tick = new Tick(new Double(currentTickValue), tickLabel, x, y);
                getTicks().add(tick);
            }
        }

    }

    /**
     * Returns true if the specified plot is compatible with the axis.
     * <P>
     * This class (VerticalNumberAxis) requires that the plot implements the
     * VerticalValuePlot interface.
     *
     * @param plot  the plot.
     *
     * @return <code>true</code> if the specified plot is compatible with the axis.
     */
    protected boolean isCompatiblePlot(Plot plot) {

        if (plot instanceof VerticalValuePlot) {
            return true;
        }
        else {
            return false;
        }
    }

}
