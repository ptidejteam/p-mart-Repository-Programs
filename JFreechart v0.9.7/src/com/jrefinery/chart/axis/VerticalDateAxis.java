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
 * ---------------------
 * VerticalDateAxis.java
 * ---------------------
 * (C) Copyright 2002, 2003 by Michael Rauch and Contributors.
 *
 * Original Author:  Michael Rauch;
 * Contributor(s):   David Gilbert (for Simba Management Limited);
 *
 * $Id: VerticalDateAxis.java,v 1.1 2007/10/10 20:00:06 vauchers Exp $
 *
 * Changes
 * -------
 * 27-Nov-2002 : Version 1 (based on HorizontalDateAxis), contributed by Michael Rauch.
 *               Modified for axis location and other recent changes (DG);
 * 14-Jan-2003 : Changed autoRangeMinimumSize from Number --> double (DG);
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
import java.util.Date;
import java.util.Iterator;

import com.jrefinery.chart.event.AxisChangeEvent;
import com.jrefinery.chart.plot.Plot;
import com.jrefinery.chart.plot.VerticalValuePlot;
import com.jrefinery.data.DateRange;
import com.jrefinery.data.Range;

/**
 * A vertical axis that displays dates.
 *
 * @author Michael Rauch
 */
public class VerticalDateAxis extends DateAxis implements VerticalAxis, Serializable {

    /**
     * A flag indicating whether or not the axis label is drawn vertically.
     */
    private boolean verticalLabel;

    /**
     * Constructs a new date axis, using default attribute values where necessary.
     *
     * @param label  the axis label (<code>null</code> permitted).
     */
    public VerticalDateAxis(String label) {

        super(label);
        
        this.verticalLabel = true;

    }

    /**
     * Returns a flag indicating whether or not the axis label is drawn vertically.
     *
     * @return <code>true</code> or <code>false</code>.
     */
    public boolean isVerticalLabel() {
        return this.verticalLabel;
    }

    /**
     * Sets a flag indicating whether or not the axis label is drawn vertically.
     * <P>
     * If the flag is changed, an {@link AxisChangeEvent} is sent to all registered listeners.
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
     * Translates a date to Java2D coordinates, based on the range displayed by
     * this axis for the specified data area.
     *
     * @param date  the date.
     * @param dataArea  the rectangle (in Java2D space) where the data is to be plotted.
     *
     * @return the vertical coordinate corresponding to the supplied date.
     */
    public double translateDateToJava2D(Date date, Rectangle2D dataArea) {
        return translateValueToJava2D((double) date.getTime(), dataArea);
    }

    /**
     * Translates the data value to the display coordinates (Java 2D User Space)
     * of the chart.
     *
     * @param value  the date to be plotted.
     * @param dataArea  the rectangle (in Java2D space) where the data is to be plotted.
     *
     * @return the vertical coordinate corresponding to the supplied data value.
     */
    public double translateValueToJava2D(double value, Rectangle2D dataArea) {
        Range range = getRange();
        double axisMin = range.getLowerBound();
        double axisMax = range.getUpperBound();
        double minY = dataArea.getMinY();
        double maxY = dataArea.getMaxY();
        if (isInverted()) {
          return minY + (((value - axisMin) / (axisMax - axisMin)) * (maxY - minY));
        }
        else {
            return maxY - (((value - axisMin) / (axisMax - axisMin)) * (maxY - minY));
        }
    }

    /**
     * Translates the Java2D (vertical) coordinate back to the corresponding
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
        double plotY = dataArea.getY();
        double plotMaxY = dataArea.getMaxY();
        if (isInverted()) {
             return axisMin
                    + ((java2DValue - plotY) / (plotMaxY - plotY) * (axisMax - axisMin));
        }
        else {
             return axisMax
                    - ((java2DValue - plotY) / (plotMaxY - plotY) * (axisMax - axisMin));
        }
    }

    /**
     * Rescales the axis to ensure that all data is visible.
     */
    protected void autoAdjustRange() {

        Plot plot = getPlot();

        if (plot == null) {
            return; // no plot, no data
        }

        if (plot instanceof VerticalValuePlot) {
            VerticalValuePlot vvp = (VerticalValuePlot) plot;
            Range r = vvp.getVerticalDataRange(this);
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

        FontRenderContext frc = g2.getFontRenderContext();
        if (isAutoTickUnitSelection()) {
            selectAutoTickUnit(g2, plotArea, dataArea);
        }
        Rectangle2D labelBounds = null;
        DateTickUnit tickUnit = getTickUnit();
        Date tickDate = calculateLowestVisibleTickValue(tickUnit);
        Date upperDate = calculateHighestVisibleTickValue(tickUnit);
        while (tickDate.before(upperDate)) {

            // work out the value, label and position
            double yy = translateDateToJava2D(tickDate, dataArea);
            String tickLabel = tickUnit.dateToString(tickDate);
            labelBounds = tickLabelFont.getStringBounds(tickLabel, g2.getFontRenderContext());
            LineMetrics metrics = tickLabelFont.getLineMetrics(tickLabel, frc);
            float x;
            if (location == LEFT) {
                x = (float) (dataArea.getX()
                             - labelBounds.getWidth() - getTickLabelInsets().right);
            }
            else {
                x = (float) (dataArea.getMaxX() + getTickLabelInsets().left);
            }
            float y = (float) (yy + (metrics.getAscent() / 2));
            Tick tick = new Tick(tickDate, tickLabel, x, y);
            getTicks().add(tick);
            tickDate = tickUnit.addToDate(tickDate);
        }
    }

    /**
     * Draws the axis on a Java 2D graphics device (such as the screen or a printer).
     *
     * @param g2  the graphics device.
     * @param plotArea  the area within which the chart should be drawn.
     * @param dataArea  the area within which the plot should be drawn (a subset of the drawArea).
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
            float yy =  (float) translateValueToJava2D(tick.getNumericalValue(), dataArea);
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
     * @param drawArea  the area in which the plot and axes should be drawn.
     * @param plotArea  the area in which the plot should be drawn.
     */
    protected void selectAutoTickUnit(Graphics2D g2, Rectangle2D drawArea, Rectangle2D plotArea) {

        // calculate the tick label height...
        FontRenderContext frc = g2.getFontRenderContext();
        double tickLabelHeight = getTickLabelFont().getLineMetrics("123",
            frc).getHeight() + getTickLabelInsets().top + getTickLabelInsets().bottom;

        // now find the smallest tick unit that will accommodate the labels...
        double zero = translateValueToJava2D(0.0, plotArea);

        // start with the current tick unit...
        TickUnits tickUnits = getStandardTickUnits();
        DateTickUnit candidate1 = (DateTickUnit) tickUnits.getCeilingTickUnit(getTickUnit());
        double y = translateValueToJava2D(candidate1.getSize(), plotArea);
        double unitHeight = Math.abs(y - zero);

        // then extrapolate...
        int bestguess = (int) ((tickLabelHeight / unitHeight) * candidate1.getSize());
        TickUnit guess = new NumberTickUnit(bestguess, null);
        DateTickUnit candidate2 = (DateTickUnit) tickUnits.getCeilingTickUnit(guess);

        setTickUnit(candidate2, false, false);
    }

    /**
     * Checks the compatibility of a plot with this type of axis, returning true if the plot is
     * compatible and false otherwise.
     * <p>
     * The VerticalDateAxis class required the plot to implement the VerticalValuePlot interface.
     *
     * @param plot  the plot.
     *
     * @return a boolean indicating whether the plot is compatible with the axis.
     */
    protected boolean isCompatiblePlot(Plot plot) {
        return (plot instanceof VerticalValuePlot);
    }

}
