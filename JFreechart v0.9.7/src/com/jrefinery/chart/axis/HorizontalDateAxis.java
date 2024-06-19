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
 * HorizontalDateAxis.java
 * -----------------------
 * (C) Copyright 2000-2003, by Simba Management Limited and Contributors.
 *
 * Original Author:  David Gilbert (for Simba Management Limited);
 * Contributor(s):   David Li;
 *                   Jonathan Nash;
 *
 * $Id: HorizontalDateAxis.java,v 1.1 2007/10/10 20:00:06 vauchers Exp $
 *
 * Changes (from 23-Jun-2001)
 * --------------------------
 * 23-Jun-2001 : Modified to work with null data source (DG);
 * 18-Sep-2001 : Updated header (DG);
 * 07-Nov-2001 : Updated configure() method (DG);
 * 30-Nov-2001 : Cleaned up default values in constructor (DG);
 * 12-Dec-2001 : Grid lines bug fix (DG);
 * 16-Jan-2002 : Added an optional crosshair, based on the implementation by Jonathan Nash (DG);
 * 20-Feb-2002 : Modified x-coordinate for vertical tick labels (DG);
 * 25-Feb-2002 : Updated import statements (DG);
 * 19-Apr-2002 : Added facility to set axis visibility on or off.  Also drawVerticalString(...) is
 *               now drawRotatedString(...) in RefineryUtilities (DG);
 * 22-Apr-2002 : Changed autoAdjustRange() from public to protected (DG);
 * 25-Jul-2002 : Changed the auto-range calculation to use the lower and upper margin percentages,
 *               which have been moved up one level from NumberAxis to ValueAxis (DG);
 * 05-Aug-2002 : Modified check for fit of tick labels to take into account the insets (DG);
 * 03-Sep-2002 : Added check for null label in reserveAxisArea method, suggested by Achilleus
 *               Mantzios (DG);
 * 05-Sep-2002 : Updated constructor to reflect changes in the Axis class, and changed the draw
 *               method to observe tickMarkPaint (DG);
 * 19-Sep-2002 : Fixed errors reported by Checkstyle (DG);
 * 04-Oct-2002 : Changed auto tick mechanism to parallel that used by the number axis classes (DG);
 * 24-Oct-2002 : Added date format override mechanism (DG);
 * 08-Nov-2002 : Moved to new package com.jrefinery.chart.axis (DG);
 * 18-Nov-2002 : Moved responsibility for drawing grid lines to the XYPlot class (DG);
 * 14-Jan-2003 : Changed autoRangeMinimumSize from Number --> double, and moved crosshair settings
 *               to the plot classes (DG);
 * 17-Jan-2003 : Moved plot classes to separate package (DG);
 * 20-Jan-2003 : Removed unnecessary constructors (DG);
 * 26-Mar-2003 : Implemented Serializable (DG);
 *
 */

package com.jrefinery.chart.axis;

import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.font.FontRenderContext;
import java.awt.font.LineMetrics;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.io.Serializable;
import java.text.DateFormat;
import java.util.Date;
import java.util.Iterator;

import com.jrefinery.chart.event.AxisChangeEvent;
import com.jrefinery.chart.plot.HorizontalValuePlot;
import com.jrefinery.chart.plot.Plot;
import com.jrefinery.data.DateRange;
import com.jrefinery.data.Range;
import com.jrefinery.ui.RefineryUtilities;

/**
 * A horizontal axis that displays dates.
 * <P>
 * This axis can be used with the {@link com.jrefinery.chart.plot.XYPlot} class for datasets where 
 * the x-values are numbers interpreted as milliseconds since midnight 1-Jan-1970 GMT (the 
 * encoding used by <code>java.util.Date<code>).
 * <P>
 * You can also use a <code>HorizontalDateAxis<code> as the range axis in a 
 * {@link com.jrefinery.chart.plot.HorizontalCategoryPlot}.
 *
 * @see com.jrefinery.chart.plot.XYPlot
 * @see com.jrefinery.chart.plot.HorizontalCategoryPlot
 *
 * @author David Gilbert
 */
public class HorizontalDateAxis extends DateAxis 
                                implements HorizontalAxis, Serializable {

    /** A flag indicating whether or not tick labels are drawn vertically. */
    private boolean verticalTickLabels;

    /**
     * Constructs a new date axis, using default attribute values where necessary.
     *
     * @param label  the axis label (<code>null</code> permitted).
     */
    public HorizontalDateAxis(String label) {

        super(label);
        
        this.verticalTickLabels = false;

    }

    /**
     * Returns true if the tick labels should be rotated to vertical, and false
     * for standard horizontal labels.
     *
     * @return <code>true</code> or <code>false</code>.
     */
    public boolean getVerticalTickLabels() {
        return this.verticalTickLabels;
    }

    /**
     * Sets the flag that controls whether the tick labels are displayed vertically (that is,
     * rotated 90 degrees from horizontal).
     * <P>
     * If the flag is changed, an {@link AxisChangeEvent} is sent to all registered listeners.
     *
     * @param flag  the flag.
     */
    public void setVerticalTickLabels(boolean flag) {
        if (this.verticalTickLabels != flag) {
            this.verticalTickLabels = flag;
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
     * Translates a date to Java2D coordinates, based on the range displayed by
     * this axis for the specified data area.
     *
     * @param date  the date.
     * @param dataArea  the rectangle (in Java2D space) where the data is to be plotted.
     *
     * @return the horizontal coordinate corresponding to the supplied date.
     */
    public double translateDateToJava2D(Date date, Rectangle2D dataArea) {

        Range range = getRange();
        double value = (double) date.getTime();
        double axisMin = range.getLowerBound();
        double axisMax = range.getUpperBound();
        double plotX = dataArea.getX();
        double plotMaxX = dataArea.getMaxX();
        return plotX + ((value - axisMin) / (axisMax - axisMin)) * (plotMaxX - plotX);

    }

    /**
     * Translates the data value to the display coordinates (Java 2D User Space)
     * of the chart.
     *
     * @param value  the date to be plotted.
     * @param dataArea  the rectangle (in Java2D space) where the data is to be plotted.
     *
     * @return the horizontal coordinate corresponding to the supplied data value.
     */
    public double translateValueToJava2D(double value, Rectangle2D dataArea) {

        Range range = getRange();
        double axisMin = range.getLowerBound();
        double axisMax = range.getUpperBound();
        double plotX = dataArea.getX();
        double plotMaxX = dataArea.getMaxX();
        return plotX + ((value - axisMin) / (axisMax - axisMin)) * (plotMaxX - plotX);

    }

    /**
     * Translates the Java2D (horizontal) coordinate back to the corresponding
     * data value.
     *
     * @param java2DValue  the coordinate in Java2D space.
     * @param dataArea  the rectangle (in Java2D space) where the data is to be plotted.
     *
     * @return the data value corresponding to the Java2D coordinate.
     */
    public double translateJava2DtoValue(float java2DValue, Rectangle2D dataArea) {

        Range range = getRange();
        double axisMin = range.getLowerBound();
        double axisMax = range.getUpperBound();
        double plotX = dataArea.getX();
        double plotMaxX = dataArea.getMaxX();
        double result = axisMin
                        + ((java2DValue - plotX) / (plotMaxX - plotX) * (axisMax - axisMin));
        return result;

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
                r = new DateRange();
            }

            long upper = (long) r.getUpperBound();
            long lower;
            long fixedAutoRange = (long) getFixedAutoRange();
            if (fixedAutoRange > 0.0) {
                lower = upper - fixedAutoRange;
            }
            else {
                lower = (long) r.getLowerBound();
                double range = upper - lower;
                long minRange = (long) getAutoRangeMinimumSize();
                if (range < minRange) {
                    long expand = (long) (minRange - range) / 2;
                    upper = upper + expand;
                    lower = lower - expand;
                }
                upper = upper + (long) (range * getUpperMargin());
                lower = lower - (long) (range * getLowerMargin());
            }
            setRangeAttribute(new DateRange(new Date(lower), new Date(upper)));
        }

    }

    /**
     * Recalculates the ticks for the date axis.
     *
     * @param g2  the graphics device.
     * @param plotArea  the area in which the axes and data are to be drawn.
     * @param dataArea  the area in which the data is to be drawn.
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

        Rectangle2D labelBounds = null;
        DateTickUnit tickUnit = getTickUnit();
        Date tickDate = calculateLowestVisibleTickValue(tickUnit);
        Date upperDate = getMaximumDate();
        while (tickDate.before(upperDate)) {
            // work out the value, label and position
            double xx = translateDateToJava2D(tickDate, dataArea);
            String tickLabel;
            DateFormat formatter = getDateFormatOverride();
            if (formatter != null) {
                tickLabel = formatter.format(tickDate);
            }
            else {
                tickLabel = tickUnit.dateToString(tickDate);
            }
            FontRenderContext frc = g2.getFontRenderContext();
            Rectangle2D tickLabelBounds = tickLabelFont.getStringBounds(tickLabel, frc);
            LineMetrics metrics = tickLabelFont.getLineMetrics(tickLabel, frc);
            float x = 0.0f;
            float y = 0.0f;
            Insets tickLabelInsets = getTickLabelInsets();
            if (this.verticalTickLabels) {
                x = (float) (xx + tickLabelBounds.getHeight() / 2 - metrics.getDescent());
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
                    y = (float) (dataArea.getMinY() - tickLabelInsets.bottom);
                }
                else {
                    y = (float) (dataArea.getMaxY() + tickLabelInsets.top
                                                    + tickLabelBounds.getHeight());
                }
            }
            Tick tick = new Tick(tickDate, tickLabel, x, y);
            getTicks().add(tick);
            tickDate = tickUnit.addToDate(tickDate);
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

        if (!isVisible()) {
            return;
        }

        double y = 0.0;
        if (location == TOP) {
            y = plotArea.getMinY();
        }
        else {
            y = plotArea.getMaxY();
        }

        // draw the axis label
        String label = getLabel();
        if (label != null) {
            Font labelFont = getLabelFont();
            Insets labelInsets = getLabelInsets();
            g2.setFont(labelFont);
            g2.setPaint(getLabelPaint());
            FontRenderContext frc = g2.getFontRenderContext();
            Rectangle2D labelBounds = labelFont.getStringBounds(label, frc);
            LineMetrics lm = labelFont.getLineMetrics(label, frc);
            if (location == TOP) {
                y = y + labelInsets.top + lm.getAscent();
            }
            else {
                y = y - labelInsets.bottom - lm.getDescent() - lm.getLeading();
            }
            float labelx = (float) (dataArea.getX() + dataArea.getWidth() / 2
                                                    - labelBounds.getWidth() / 2);
            float labely = (float) y;
            g2.drawString(label, labelx, labely);
            if (location == TOP) {
                y = y + lm.getDescent() + lm.getLeading() + labelInsets.bottom;
            }
            else {
                y = y - labelInsets.bottom - labelInsets.top - lm.getHeight();
            }
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
     * @return the height.
     */
    public double reserveHeight(Graphics2D g2, Plot plot, Rectangle2D drawArea, int location) {

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

        // calculate the height required for the tick labels (if visible)
        Insets tickLabelInsets = getTickLabelInsets();
        double tickLabelHeight = tickLabelInsets.top + tickLabelInsets.bottom;
        if (isTickLabelsVisible()) {
            g2.setFont(getTickLabelFont());
            refreshTicks(g2, drawArea, drawArea, location);
            tickLabelHeight = tickLabelHeight
                              + getMaxTickLabelHeight(g2, drawArea, this.verticalTickLabels);
        }
        return labelHeight + tickLabelHeight;

    }

    /**
     * Returns area in which the axis will be displayed.
     *
     * @param g2  the graphics device.
     * @param plot  the plot.
     * @param drawArea  the drawing area.
     * @param location  the location of the axis.
     * @param reservedWidth  the width already reserved for the vertical axis.
     * @param verticalAxisLocation  the location of the vertical axis.
     *
     *
     * @return the area to reserve for the horizontal axis.
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
            labelHeight = metrics.getHeight();
            Insets labelInsets = getLabelInsets();
            if (labelInsets != null) {
                labelHeight += labelInsets.top + labelInsets.bottom;
            }
        }

        // calculate the height required for the tick labels (if visible)
        Insets tickLabelInsets = getTickLabelInsets();
        double tickLabelHeight = tickLabelInsets.top + tickLabelInsets.bottom;
        if (isTickLabelsVisible()) {
            g2.setFont(getTickLabelFont());
            refreshTicks(g2, drawArea, drawArea, location);
            tickLabelHeight += getMaxTickLabelHeight(g2, drawArea, this.verticalTickLabels);
        }

        return labelHeight + tickLabelHeight;

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

        DateTickUnit unit2 = (DateTickUnit) tickUnits.getCeilingTickUnit(guess);
        double x2 = translateValueToJava2D(unit2.getSize(), dataArea);
        double unit2Width = Math.abs(x2 - zero);

        tickLabelWidth = estimateMaximumTickLabelWidth(g2, unit2);
        if (tickLabelWidth > unit2Width) {
            unit2 = (DateTickUnit) tickUnits.getLargerTickUnit(unit2);
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
    private double estimateMaximumTickLabelWidth(Graphics2D g2, DateTickUnit tickUnit) {

        Insets tickLabelInsets = getTickLabelInsets();
        double result = tickLabelInsets.left + tickLabelInsets.right;

        FontRenderContext frc = g2.getFontRenderContext();
        Font tickLabelFont = getTickLabelFont();
        if (this.verticalTickLabels) {
            // all tick labels have the same width (equal to the height of the font)...
            result += tickLabelFont.getStringBounds("1-Jan-2002", frc).getHeight();
        }
        else {
            // look at lower and upper bounds...
            DateRange range = (DateRange) getRange();
            Date lower = range.getLowerDate();
            Date upper = range.getUpperDate();
            String lowerStr = tickUnit.dateToString(lower);
            String upperStr = tickUnit.dateToString(upper);
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
     * @param drawArea  the drawing area.
     * @param vertical  a flag indicating whether or not the tick labels are rotated to vertical.
     *
     * @return the maximum tick label height.
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
     * Checks the compatibility of a plot with this type of axis, returning true if the plot is
     * compatible and false otherwise.
     * <p>
     * The HorizontalDateAxis class required the plot to implement the HorizontalValuePlot
     * interface.
     *
     * @param plot  the plot.
     *
     * @return a boolean indicating whether the plot is compatible with the axis.
     */
    protected boolean isCompatiblePlot(Plot plot) {
        return (plot instanceof HorizontalValuePlot);
    }

}
