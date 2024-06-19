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
 * -------------------------
 * HorizontalNumberAxis.java
 * -------------------------
 * (C) Copyright 2000-2003, by Simba Management Limited and Contributors.
 *
 * Original Author:  David Gilbert (for Simba Management Limited);
 * Contributor(s):   Jonathan Nash;
 *
 * $Id: HorizontalNumberAxis.java,v 1.1 2007/10/10 19:54:25 vauchers Exp $
 *
 * Changes (from 23-Jun-2001)
 * --------------------------
 * 23-Jun-2001 : Modified to work with null data source (DG);
 * 18-Sep-2001 : Updated header and fixed DOS encoding problem (DG);
 * 07-Nov-2001 : Updated configure() method (DG);
 * 21-Nov-2001 : Fixed bug on default axis range (DG);
 * 23-Nov-2001 : Overhauled auto tick unit code for all axes (DG);
 * 12-Dec-2001 : Minor change due to grid lines bug fix (DG);
 * 08-Jan-2002 : Added flag to allow axis to be inverted (DG);
 * 16-Jan-2002 : Added an optional crosshair, based on the implementation by Jonathan Nash (DG);
 * 23-Jan-2002 : Fixed bugs causing null pointer exceptions when axis label is null (DG);
 * 25-Feb-2002 : Changed autoAdjustRange() from public to protected, and altered the calculation
 *               to take into account the new autoRangeStickyZero flag (DG);
 * 26-Feb-2002 : Fixed bug 523032 (an incomplete implementation) preventing inverted flag from
 *               having any effect on a HorizontalNumberAxis (DG);
 * 19-Apr-2002 : Added facility to set axis visibility on or off.  Also drawVerticalString(...) is
 *               now drawRotatedString(...) in RefineryUtilities (DG);
 * 01-May-2002 : Improved auto tick unit selection to prevent labels overlapping (DG);
 * 19-Jun-2002 : Added an optional marker band (DG);
 * 25-Jul-2002 : Changed order of parameters in ValueAxis constructor (DG);
 * 27-Aug-2002 : Changed auto-range calculation to observe fixedAutoRange setting, as suggested
 *               by Bob Orchard (DG);
 * 03-Sep-2002 : Added maximum tick count check (DG);
 * 05-Sep-2002 : Updated constructors to reflect changes in the Axis class, and changed draw
 *               method to observe tickMarkPaint (DG);
 * 24-Oct-2002 : Added number format override mechanism (DG);
 * 08-Nov-2002 : Moved to new package com.jrefinery.chart.axis (DG);
 * 18-Nov-2002 : Moved responsibility for drawing grid lines to the XYPlot class (DG);
 * 19-Nov-2002 : Amended for drawing at the top or the bottom of the plot.
 *               Removed the grid settings (now controlled by the plot) (DG);
 * 14-Jan-2003 : Changed autoRangeMinimumSize from Number --> double, and moved crosshair settings
 *               to the plot classes (DG);
 * 17-Jan-2003 : Moved plot classes to separate package (DG);
 *
 */

package com.jrefinery.chart.axis;

import java.awt.Graphics2D;
import java.awt.Font;
import java.awt.Insets;
import java.awt.font.FontRenderContext;
import java.awt.font.LineMetrics;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Line2D;
import java.text.NumberFormat;
import java.util.Iterator;
import com.jrefinery.chart.event.AxisChangeEvent;
import com.jrefinery.chart.plot.Plot;
import com.jrefinery.chart.plot.HorizontalValuePlot;
import com.jrefinery.data.Range;
import com.jrefinery.ui.RefineryUtilities;

/**
 * A horizontal axis that displays numerical values.
 *
 * @see com.jrefinery.chart.plot.HorizontalCategoryPlot
 * @see com.jrefinery.chart.plot.XYPlot
 *
 * @author David Gilbert
 */
public class HorizontalNumberAxis extends NumberAxis implements HorizontalAxis {

    /** The default setting for the vertical tick labels flag. */
    public static final boolean DEFAULT_VERTICAL_TICK_LABELS = false;
    
    /** A flag indicating whether or not tick labels are drawn vertically. */
    private boolean verticalTickLabels;

    /** An optional band for marking regions on the axis. */
    private HorizontalMarkerAxisBand markerBand;

    /**
     * Constructs a horizontal number axis, using default values where necessary.
     *
     * @param label  the axis label (<code>null</code> permitted).
     */
    public HorizontalNumberAxis(String label) {

        super(label);

        this.verticalTickLabels = DEFAULT_VERTICAL_TICK_LABELS;
        this.markerBand = null;
        
    }

    /**
     * Returns a flag indicating whether the tick labels are drawn 'vertically'.
     *
     * @return The flag.
     */
    public boolean isVerticalTickLabels() {
        return this.verticalTickLabels;
    }

    /**
     * Sets the flag that determines whether the tick labels are drawn 'vertically'.
     *
     * @param flag  the new value of the flag.
     */
    public void setVerticalTickLabels(boolean flag) {
        this.verticalTickLabels = flag;
        notifyListeners(new AxisChangeEvent(this));
    }

    /**
     * Returns the (optional) marker band for the axis.
     *
     * @return The marker band (possibly <code>null</code>).
     */
    public HorizontalMarkerAxisBand getMarkerBand() {
        return this.markerBand;
    }

    /**
     * Sets the marker band for the axis.
     * <P>
     * The marker band is optional, leave it set to <code>null</code> if you don't require it.
     *
     * @param band the new band (<code>null<code> permitted).
     */
    public void setMarkerBand(HorizontalMarkerAxisBand band) {
        this.markerBand = band;
        notifyListeners(new AxisChangeEvent(this));
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
     * @return The Java2D coordinate.
     */
    public double translateValueToJava2D(double value, Rectangle2D dataArea) {

        Range range = getRange();
        double axisMin = range.getLowerBound();
        double axisMax = range.getUpperBound();
        double minX = dataArea.getX();
        double maxX = dataArea.getMaxX();
        if (isInverted()) {
            return maxX - ((value - axisMin) / (axisMax - axisMin)) * (maxX - minX);
        }
        else {
            return minX + ((value - axisMin) / (axisMax - axisMin)) * (maxX - minX);
        }

    }

    /**
     * Converts a coordinate in Java2D space to the corresponding data value,
     * assuming that the axis runs along one edge of the specified dataArea.
     *
     * @param java2DValue  the coordinate in Java2D space.
     * @param dataArea  the area in which the data is plotted.
     *
     * @return The data value.
     */
    public double translateJava2DtoValue(float java2DValue, Rectangle2D dataArea) {

        Range range = getRange();
        double axisMin = range.getLowerBound();
        double axisMax = range.getUpperBound();
        double plotX = dataArea.getX();
        double plotMaxX = dataArea.getMaxX();
        if (isInverted()) {
            return axisMax - (java2DValue - plotX) / (plotMaxX - plotX) * (axisMax - axisMin);
        }
        else {
            return axisMin + (java2DValue - plotX) / (plotMaxX - plotX) * (axisMax - axisMin);
        }

    }

    /**
     * Rescales the axis to ensure that all data is visible.
     */
    protected void autoAdjustRange() {

        Plot plot = getPlot();
        if (plot == null) {
            return;  // no plot, no data
        }

        if (plot instanceof HorizontalValuePlot) {
            HorizontalValuePlot hvp = (HorizontalValuePlot) plot;

            Range r = hvp.getHorizontalDataRange(this);
            if (r == null) {
                r = new Range(DEFAULT_LOWER_BOUND, DEFAULT_UPPER_BOUND);
            }
            double upper = r.getUpperBound();
            double lower = r.getLowerBound();
            double range = upper - lower;

            // if fixed auto range, then derive lower bound...
            double fixedAutoRange = getFixedAutoRange();
            if (fixedAutoRange > 0.0) {
                lower = upper - fixedAutoRange;
            }
            else {
                // ensure the autorange is at least <minRange> in size...
                double minRange = getAutoRangeMinimumSize();
                if (range < minRange) {
                    upper = (upper + lower + minRange) / 2;
                    lower = (upper + lower - minRange) / 2;
                }


                if (autoRangeIncludesZero()) {
                    if (autoRangeStickyZero()) {
                        if (upper <= 0.0) {
                            upper = 0.0;
                        }
                        else {
                            upper = upper + getUpperMargin() * range;
                        }
                        if (lower >= 0.0) {
                            lower = 0.0;
                        }
                        else {
                            lower = lower - getLowerMargin() * range;
                        }
                    }
                    else {
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
            }

            setRangeAttribute(new Range(lower, upper));
        }

    }

    /**
     * Calculates the positions of the tick labels for the axis, storing the results in the
     * tick label list (ready for drawing).
     *
     * @param g2  the graphics device.
     * @param plotArea  the area in which the plot (inlcuding axes) should be drawn.
     * @param dataArea  the area in which the data should be drawn.
     * @param location  the location of the axis.
     */
    public void refreshTicks(Graphics2D g2,
                             Rectangle2D plotArea, Rectangle2D dataArea,
                             int location) {

        getTicks().clear();

        Font tickLabelFont = getTickLabelFont();
        g2.setFont(tickLabelFont);
        FontRenderContext frc = g2.getFontRenderContext();

        if (isAutoTickUnitSelection()) {
            selectAutoTickUnit(g2, plotArea, dataArea);
        }

        double size = getTickUnit().getSize();
        int count = calculateVisibleTickCount();
        double lowestTickValue = calculateLowestVisibleTickValue();

        if (count <= ValueAxis.MAXIMUM_TICK_COUNT) {
            for (int i = 0; i < count; i++) {
                double currentTickValue = lowestTickValue + (i * size);
                double xx = translateValueToJava2D(currentTickValue, dataArea);
                String tickLabel;
                NumberFormat formatter = getNumberFormatOverride();
                if (formatter != null) {
                    tickLabel = formatter.format(currentTickValue);
                }
                else {
                    tickLabel = getTickUnit().valueToString(currentTickValue);
                }
                Rectangle2D tickLabelBounds = tickLabelFont.getStringBounds(tickLabel, frc);
                LineMetrics metrics = tickLabelFont.getLineMetrics(tickLabel, frc);
                float x = 0.0f;
                float y = 0.0f;
                Insets tickLabelInsets = getTickLabelInsets();
                if (isVerticalTickLabels()) {
                    x = (float) (xx + tickLabelBounds.getHeight() / 2);
                    if (location == TOP) {
                        y = (float) (dataArea.getMinY() - tickLabelInsets.bottom
                                                        - tickLabelBounds.getWidth());
                    }
                    else {
                        y = (float) (dataArea.getMaxY() + tickLabelInsets.top
                                                        + tickLabelBounds.getWidth());
                    }
                }
                else {
                    x = (float) (xx - tickLabelBounds.getWidth() / 2);
                    if (location == TOP) {
                        y = (float) (dataArea.getMinY() - tickLabelInsets.bottom
                                                        - metrics.getLeading()
                                                        - metrics.getDescent());
                    }
                    else {
                        y = (float) (dataArea.getMaxY() + tickLabelInsets.top
                                                        + tickLabelBounds.getHeight());
                    }
                }
                Tick tick = new Tick(new Double(currentTickValue), tickLabel, x, y);
                getTicks().add(tick);
            }
        }

    }

    /**
     * Draws the axis on a Java 2D graphics device (such as the screen or a printer).
     *
     * @param g2  the graphics device.
     * @param plotArea  the area within which the axes and data should be drawn.
     * @param dataArea  the area within which the data should be drawn.
     * @param location  the location of the axis.
     */
    public void draw(Graphics2D g2, Rectangle2D plotArea, Rectangle2D dataArea, int location) {

        // if the axis is not visible, don't draw it...
        if (!isVisible()) {
            return;
        }

        // use a cursor to track the vertical level of the items that need drawing...
        double cursorY = 0.0;
        if (location == TOP) {
            cursorY = plotArea.getMinY();
        }
        else {
            cursorY = plotArea.getMaxY();
        }

        // draw the axis label...
        cursorY = drawHorizontalLabel(getLabel(), g2, plotArea, dataArea, location, cursorY);

        // draw the marker band (if there is one)...
        if (markerBand != null) {
            if (location == BOTTOM) {
                cursorY = cursorY - markerBand.getHeight(g2);
            }
            markerBand.draw(g2, plotArea, dataArea, 0, cursorY);
        }

        // draw the tick labels and marks and gridlines
        refreshTicks(g2, plotArea, dataArea, location);

        double yy;
        double uu;
        double dd;
        if (location == TOP) {
            yy = dataArea.getMinY();
            uu = getTickMarkOutsideLength();
            dd = getTickMarkInsideLength();
        }
        else {
            yy = dataArea.getMaxY();
            uu = getTickMarkInsideLength();
            dd = getTickMarkOutsideLength();
        }
        g2.setFont(getTickLabelFont());

        Iterator iterator = getTicks().iterator();
        while (iterator.hasNext()) {
            Tick tick = (Tick) iterator.next();
            float xx = (float) translateValueToJava2D(tick.getNumericalValue(), dataArea);
            if (isTickLabelsVisible()) {
                g2.setPaint(getTickLabelPaint());
                if (this.verticalTickLabels) {
                    RefineryUtilities.drawRotatedString(tick.getText(), g2,
                                                        tick.getX(), tick.getY(), -Math.PI / 2);
                }
                else {
                    g2.drawString(tick.getText(), tick.getX(), tick.getY());
                }
            }

            if (isTickMarksVisible()) {
                g2.setStroke(getTickMarkStroke());
                g2.setPaint(getTickMarkPaint());
                Line2D mark = new Line2D.Double(xx, yy - uu, xx, yy + dd);
                g2.draw(mark);
            }
        }

    }

    /**
     * Returns the height required to draw the axis in the specified draw area.
     *
     * @param g2  the graphics device.
     * @param plot  the plot that the axis belongs to.
     * @param drawArea  the area within which the plot should be drawn.
     * @param location  the axis location (top or bottom).
     *
     * @return the height required to draw the axis in the specified draw area.
     */
    public double reserveHeight(Graphics2D g2, Plot plot, Rectangle2D drawArea, int location) {

        if (!isVisible()) {
            return 0.0;
        }

        // calculate the height of the axis label...
        double labelHeight = 0.0;
        String label = getLabel();
        if (label != null) {
            LineMetrics metrics
                = getLabelFont().getLineMetrics(label, g2.getFontRenderContext());
            Insets labelInsets = getLabelInsets();
            labelHeight = labelInsets.top + metrics.getHeight() + labelInsets.bottom;
        }

        // calculate the height of the marker band (if there is one)...
        double markerBandHeight = 0.0;
        if (markerBand != null) {
            markerBandHeight = markerBand.getHeight(g2);
        }

        // calculate the height required for the tick labels (if visible)
        Insets tickLabelInsets = getTickLabelInsets();
        double tickLabelHeight = tickLabelInsets.top + tickLabelInsets.bottom;
        if (isTickLabelsVisible()) {
            g2.setFont(getTickLabelFont());
            refreshTicks(g2, drawArea, drawArea, location);
            tickLabelHeight = tickLabelHeight
                              + getMaxTickLabelHeight(g2, drawArea, this.verticalTickLabels);
        }
        return labelHeight + markerBandHeight + tickLabelHeight;

    }

    /**
     * Returns area in which the axis will be displayed.
     *
     * @param g2  the graphics device.
     * @param plot  a reference to the plot.
     * @param drawArea  the area within which the plot and axes should be drawn.
     * @param location  the location of the axis.
     * @param reservedWidth  the space already reserved for the vertical axis.
     * @param verticalAxisLocation  the location of the vertical axis.
     *
     * @return area in which the axis will be displayed.
     */
    public double reserveHeight(Graphics2D g2, Plot plot, Rectangle2D drawArea, int location,
                                double reservedWidth, int verticalAxisLocation) {

        if (!isVisible()) {
            return 0.0;
        }

        // calculate the height of the axis label...
        double labelHeight = 0.0;
        String label = getLabel();
        if (label != null) {
            LineMetrics metrics = getLabelFont().getLineMetrics(label, g2.getFontRenderContext());
            Insets labelInsets = getLabelInsets();
            labelHeight = labelInsets.top + metrics.getHeight() + labelInsets.bottom;
        }

        // calculate the height of the marker band (if there is one)...
        double markerBandHeight = 0.0;
        if (markerBand != null) {
            markerBandHeight = markerBand.getHeight(g2);
        }

        // calculate the height required for the tick labels (if visible)
        Insets tickLabelInsets = getTickLabelInsets();
        double tickLabelHeight = tickLabelInsets.top + tickLabelInsets.bottom;
        if (isTickLabelsVisible()) {
            g2.setFont(getTickLabelFont());
            refreshTicks(g2, drawArea, drawArea, location);
            tickLabelHeight = tickLabelHeight
                              + getMaxTickLabelHeight(g2, drawArea, this.verticalTickLabels);
        }
        return labelHeight + markerBandHeight + tickLabelHeight;

    }

    /**
     * Selects an appropriate tick value for the axis.  The strategy is to
     * display as many ticks as possible (selected from an array of 'standard'
     * tick units) without the labels overlapping.
     *
     * @param g2  the graphics device.
     * @param drawArea  the area in which the plot and axes should be drawn.
     * @param dataArea  the area defined by the axes.
     */
    protected void selectAutoTickUnit(Graphics2D g2, Rectangle2D drawArea, Rectangle2D dataArea) {

        double zero = translateValueToJava2D(0.0, dataArea);
        double tickLabelWidth = estimateMaximumTickLabelWidth(g2, getTickUnit());

        // start with the current tick unit...
        TickUnits tickUnits = getStandardTickUnits();
        TickUnit unit1 = tickUnits.getCeilingTickUnit(getTickUnit());
        double x1 = translateValueToJava2D(unit1.getSize(), dataArea);
        double unit1Width = Math.abs(x1 - zero);

        // then extrapolate...
        double guess = (tickLabelWidth / unit1Width) * unit1.getSize();

        NumberTickUnit unit2 = (NumberTickUnit) tickUnits.getCeilingTickUnit(guess);
        double x2 = translateValueToJava2D(unit2.getSize(), dataArea);
        double unit2Width = Math.abs(x2 - zero);

        tickLabelWidth = estimateMaximumTickLabelWidth(g2, unit2);
        if (tickLabelWidth > unit2Width) {
            unit2 = (NumberTickUnit) tickUnits.getLargerTickUnit(unit2);
        }

        setTickUnit(unit2, false, false);

    }

    /**
     * Estimates the maximum width of the tick labels, assuming the specified tick unit is used.
     * <P>
     * Rather than computing the string bounds of every tick on the axis, we just look at two
     * values: the lower bound and the upper bound for the axis.  These two values will usually
     * be representative.
     *
     * @param g2  the graphics device.
     * @param tickUnit  the tick unit to use for calculation.
     *
     * @return the estimated maximum width of the tick labels.
     */
    private double estimateMaximumTickLabelWidth(Graphics2D g2, TickUnit tickUnit) {

        Insets tickLabelInsets = getTickLabelInsets();
        double result = tickLabelInsets.left + tickLabelInsets.right;

        Font tickLabelFont = getTickLabelFont();
        FontRenderContext frc = g2.getFontRenderContext();
        if (this.verticalTickLabels) {
            // all tick labels have the same width (equal to the height of the font)...
            result += tickLabelFont.getStringBounds("0", frc).getHeight();
        }
        else {
            // look at lower and upper bounds...
            Range range = getRange();
            double lower = range.getLowerBound();
            double upper = range.getUpperBound();
            String lowerStr = tickUnit.valueToString(lower);
            String upperStr = tickUnit.valueToString(upper);
            double w1 = tickLabelFont.getStringBounds(lowerStr, frc).getWidth();
            double w2 = tickLabelFont.getStringBounds(upperStr, frc).getWidth();
            result += Math.max(w1, w2);
        }

        return result;

    }

    /**
     * A utility method for determining the height of the tallest tick label.
     *
     * @param g2  the graphics device.
     * @param drawArea  the area within which the plot and axes should be drawn.
     * @param vertical  a flag that indicates whether or not the tick labels are 'vertical'.
     *
     * @return the height of the tallest tick label.
     */
    private double getMaxTickLabelHeight(Graphics2D g2, Rectangle2D drawArea, boolean vertical) {
        Font font = getTickLabelFont();
        g2.setFont(font);
        FontRenderContext frc = g2.getFontRenderContext();
        double maxHeight = 0.0;
        if (vertical) {
            Iterator iterator = getTicks().iterator();
            while (iterator.hasNext()) {
                Tick tick = (Tick) iterator.next();
                Rectangle2D labelBounds = font.getStringBounds(tick.getText(), frc);
                if (labelBounds.getWidth() > maxHeight) {
                    maxHeight = labelBounds.getWidth();
                }
            }
        }
        else {
            LineMetrics metrics = font.getLineMetrics("Sample", frc);
            maxHeight = metrics.getHeight();
        }
        return maxHeight;
    }

    /**
     * Returns true if a plot is compatible with the axis, and false otherwise.
     * <P>
     * For this axis, the requirement is that the plot implements the HorizontalValuePlot
     * interface.
     *
     * @param plot  the plot.
     *
     * @return <code>true</code> if the plot is compatible with the axis.
     */
    protected boolean isCompatiblePlot(Plot plot) {
        if (plot instanceof HorizontalValuePlot) {
            return true;
        }
        else {
            return false;
        }
    }

}
