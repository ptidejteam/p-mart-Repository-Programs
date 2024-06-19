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
 * -----------------------
 * HorizontalDateAxis.java
 * -----------------------
 * (C) Copyright 2000-2002, by Simba Management Limited and Contributors.
 *
 * Original Author:  David Gilbert (for Simba Management Limited);
 * Contributor(s):   David Li;
 *                   Jonathan Nash;
 *
 * $Id: HorizontalDateAxis.java,v 1.1 2007/10/10 19:01:18 vauchers Exp $
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
 *
 */

package com.jrefinery.chart;

import java.awt.Graphics2D;
import java.awt.Font;
import java.awt.Paint;
import java.awt.Stroke;
import java.awt.Insets;
import java.awt.font.FontRenderContext;
import java.awt.font.LineMetrics;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import com.jrefinery.data.Range;
import com.jrefinery.data.DateRange;
import com.jrefinery.ui.RefineryUtilities;

/**
 * A horizontal axis that displays date values.  Used in XY plots where the x-values in the dataset
 * are interpreted as milliseconds, encoded in the same way as java.util.Date.
 *
 * @see XYPlot
 */
public class HorizontalDateAxis extends DateAxis implements HorizontalAxis {

    /** A flag indicating whether or not tick labels are drawn vertically. */
    protected boolean verticalTickLabels;

    /**
     * Constructs a HorizontalDateAxis, using default values where necessary.
     */
    public HorizontalDateAxis() {

        this(null,
             Axis.DEFAULT_AXIS_LABEL_FONT,
             Axis.DEFAULT_AXIS_LABEL_PAINT,
             Axis.DEFAULT_AXIS_LABEL_INSETS,
             true,  // tick labels visible
             Axis.DEFAULT_TICK_LABEL_FONT,
             Axis.DEFAULT_TICK_LABEL_PAINT,
             Axis.DEFAULT_TICK_LABEL_INSETS,
             true,  // vertical tick labels
             true,  // tick marks visible
             Axis.DEFAULT_TICK_STROKE,
             ValueAxis.DEFAULT_AUTO_RANGE,
             new DateRange(),
             true, // auto tick unit selection off
             new DateUnit(Calendar.DATE, 1),
             new SimpleDateFormat(),
             true,
             ValueAxis.DEFAULT_GRID_LINE_STROKE,
             ValueAxis.DEFAULT_GRID_LINE_PAINT,
             ValueAxis.DEFAULT_CROSSHAIR_VISIBLE,
             DateAxis.DEFAULT_CROSSHAIR_DATE,
             ValueAxis.DEFAULT_CROSSHAIR_STROKE,
             ValueAxis.DEFAULT_CROSSHAIR_PAINT);

    }

    /**
     * Constructs a horizontal date axis, using default values where necessary.
     *
     * @param label The axis label.
     */
    public HorizontalDateAxis(String label) {

        this(label,
             Axis.DEFAULT_AXIS_LABEL_FONT,
             Axis.DEFAULT_AXIS_LABEL_PAINT,
             Axis.DEFAULT_AXIS_LABEL_INSETS,
             true,  // tick labels visible
             Axis.DEFAULT_TICK_LABEL_FONT,
             Axis.DEFAULT_TICK_LABEL_PAINT,
             Axis.DEFAULT_TICK_LABEL_INSETS,
             false,  // vertical tick labels
             true,  // tick marks visible
             Axis.DEFAULT_TICK_STROKE,
             ValueAxis.DEFAULT_AUTO_RANGE,
             new DateRange(),
             true, // auto tick unit selection off
             new DateUnit(Calendar.DATE, 1),
             new SimpleDateFormat(),
             true,
             ValueAxis.DEFAULT_GRID_LINE_STROKE,
             ValueAxis.DEFAULT_GRID_LINE_PAINT,
             ValueAxis.DEFAULT_CROSSHAIR_VISIBLE,
             DateAxis.DEFAULT_CROSSHAIR_DATE,
             ValueAxis.DEFAULT_CROSSHAIR_STROKE,
             ValueAxis.DEFAULT_CROSSHAIR_PAINT);

    }

    /**
     * Constructs a HorizontalDateAxis, using default values where necessary.
     *
     * @param label The axis label.
     * @param labelFont The font for displaying the axis label.
     * @param range The axis range.
     */
    public HorizontalDateAxis(String label, Font labelFont,
                              Range range) {

        this(label,
             labelFont,
             Axis.DEFAULT_AXIS_LABEL_PAINT,
             Axis.DEFAULT_AXIS_LABEL_INSETS,
             true,  // tick labels visible
             Axis.DEFAULT_TICK_LABEL_FONT,
             Axis.DEFAULT_TICK_LABEL_PAINT,
             Axis.DEFAULT_TICK_LABEL_INSETS,
             true,  // vertical tick labels
             true,  // tick marks visible
             Axis.DEFAULT_TICK_STROKE,
             ValueAxis.DEFAULT_AUTO_RANGE,
             range,
             true, // auto tick unit selection off
             new DateUnit(Calendar.DATE, 1),
             new SimpleDateFormat(),
             true,
             ValueAxis.DEFAULT_GRID_LINE_STROKE,
             ValueAxis.DEFAULT_GRID_LINE_PAINT,
             ValueAxis.DEFAULT_CROSSHAIR_VISIBLE,
             DateAxis.DEFAULT_CROSSHAIR_DATE,
             ValueAxis.DEFAULT_CROSSHAIR_STROKE,
             ValueAxis.DEFAULT_CROSSHAIR_PAINT);

    }

    /**
     * Constructs a HorizontalDateAxis.
     *
     * @param label The axis label.
     * @param labelFont The font for displaying the axis label.
     * @param labelPaint The paint used to draw the axis label.
     * @param labelInsets The blank space around the axis label.
     * @param showTickLabels Flag indicating whether or not tick labels are visible.
     * @param tickLabelFont Font for displaying tick labels.
     * @param tickLabelPaint The paint used to display the tick labels.
     * @param tickLabelInsets The blank space around the tick labels.
     * @param verticalTickLabels A flag indicating whether or not tick labels are drawn vertically.
     * @param showTickMarks Flag indicating whether or not tick marks are visible.
     * @param tickMarkStroke The stroke used to draw tick marks (if visible).
     * @param autoRange Flag indicating whether or not the axis is automatically scaled to fit the
     *                  data.
     * @param range The axis range.
     * @param autoUnits A flag indicating whether or not the tick units are automatically
     *                  selected.
     * @param tickUnits The tick units.
     * @param tickLabelFormatter The format object used to display tick labels.
     * @param gridVisible Flag indicating whether or not grid lines are visible for this axis.
     * @param gridStroke The Stroke used to display grid lines (if visible).
     * @param gridPaint The Paint used to display grid lines (if visible).
     * @param crosshairDate The date at which to draw the crosshair line (null permitted).
     * @param crosshairStroke The pen/brush used to draw the data line.
     * @param crosshairPaint The color used to draw the data line.
     */
    public HorizontalDateAxis(String label,
                              Font labelFont, Paint labelPaint, Insets labelInsets,
                              boolean showTickLabels,
                              Font tickLabelFont, Paint tickLabelPaint, Insets tickLabelInsets,
                              boolean verticalTickLabels,
                              boolean showTickMarks, Stroke tickMarkStroke,
                              boolean autoRange, Range range,
                              boolean autoUnits,
                              DateUnit tickUnits, SimpleDateFormat tickLabelFormatter,
                              boolean gridVisible, Stroke gridStroke, Paint gridPaint,
                              boolean crosshairVisible, Date crosshairDate,
                              Stroke crosshairStroke, Paint crosshairPaint) {

        super(label,
              labelFont, labelPaint, labelInsets,
              showTickLabels,
              tickLabelFont, tickLabelPaint, tickLabelInsets,
              showTickMarks, tickMarkStroke,
              autoRange, range,
              autoUnits, tickUnits, tickLabelFormatter,
              gridVisible, gridStroke, gridPaint,
              crosshairVisible, crosshairDate, crosshairStroke, crosshairPaint);

        this.verticalTickLabels = verticalTickLabels;

    }

    /**
     * Returns true if the tick labels should be rotated to vertical, and false for standard
     * horizontal labels.
     *
     * @return A flag indicating the orientation of the tick labels.
     */
    public boolean getVerticalTickLabels() {
        return this.verticalTickLabels;
    }

    /**
     * Sets the flag that determines the orientation of the tick labels.  Registered listeners are
     * notified that the axis has been changed.
     *
     * @param flag The flag.
     */
    public void setVerticalTickLabels(boolean flag) {
        this.verticalTickLabels = flag;
        this.notifyListeners(new com.jrefinery.chart.event.AxisChangeEvent(this));
    }

    /**
     * Configures the axis to work with the specified plot.  If the axis has auto-scaling, then sets
     * the maximum and minimum values.
     */
    public void configure() {
        if (isAutoRange()) {
            autoAdjustRange();
        }
    }

    /**
     * Translates a date to Java2D coordinates, based on the range displayed by this axis for the
     * specified data area.
     *
     * @param date The date.
     * @param dataArea The rectangle (in Java2D space) where the data is to be plotted.
     *
     * @return The horizontal coordinate corresponding to the supplied date.
     */
    public double translateDateToJava2D(Date date, Rectangle2D dataArea) {

        double value = (double)date.getTime();
        double axisMin = range.getLowerBound();
        double axisMax = range.getUpperBound();
        double plotX = dataArea.getX();
        double plotMaxX = dataArea.getMaxX();
        return plotX + ((value - axisMin)/(axisMax - axisMin)) * (plotMaxX - plotX);

    }

    /**
     * Translates the data value to the display coordinates (Java 2D User Space) of the chart.
     *
     * @param date The date to be plotted.
     * @param dataArea The rectangle (in Java2D space) where the data is to be plotted.
     *
     * @return The horizontal coordinate corresponding to the supplied data value.
     */
    public double translateValueToJava2D(double value, Rectangle2D dataArea) {

        double axisMin = range.getLowerBound();
        double axisMax = range.getUpperBound() ;
        double plotX = dataArea.getX();
        double plotMaxX = dataArea.getMaxX();
        return plotX + ((value - axisMin)/(axisMax - axisMin)) * (plotMaxX - plotX);

    }

    /**
     * Translates the Java2D (horizontal) coordinate back to the corresponding data value.
     *
     * @param java2DValue The coordinate in Java2D space.
     * @param dataArea The rectangle (in Java2D space) where the data is to be plotted.
     *
     * @return The data value corresponding to the Java2D coordinate.
     */
    public double translateJava2DtoValue(float java2DValue, Rectangle2D dataArea) {

        double axisMin = range.getLowerBound();
        double axisMax = range.getUpperBound();
        double plotX = dataArea.getX();
        double plotMaxX = dataArea.getMaxX();
        double result = axisMin + ((java2DValue - plotX)/(plotMaxX - plotX)*(axisMax - axisMin));
        return result;

    }

    /**
     * Rescales the axis to ensure that all data is visible.
     */
    protected void autoAdjustRange() {

        if (plot==null) return;  // no plot, no data

        if (plot instanceof HorizontalValuePlot) {
            HorizontalValuePlot hvp = (HorizontalValuePlot)plot;

            Range r = hvp.getHorizontalDataRange();
            if (r==null) r=new DateRange();

            long upper = (long)r.getUpperBound();
            long lower;
            if (this.fixedAutoRange>0.0) {
                lower = upper - (long)fixedAutoRange;
            }
            else {
                lower = (long)r.getLowerBound();
                long range = upper-lower;
                upper = upper+(range/20);
                lower = lower-(range/20);
            }
            this.range = new DateRange(new Date(lower), new Date(upper));
        }

    }

    /**
     * Recalculates the ticks for the date axis.
     *
     * @param g2 The graphics device.
     * @param drawArea The area in which the axes and data are to be drawn.
     * @param plotArea The area in which the data is to be drawn.
     */
    public void refreshTicks(Graphics2D g2, Rectangle2D drawArea, Rectangle2D plotArea) {

        this.ticks.clear();

        g2.setFont(tickLabelFont);
        FontRenderContext frc = g2.getFontRenderContext();

        if (this.autoTickUnitSelection) {
            calculateAutoTickUnits(g2, drawArea, plotArea);
        }

        Rectangle2D labelBounds = null;
        Date tickDate = calculateLowestVisibleTickValue(tickUnit);
        Date upperDate = this.getMaximumDate();
        while (tickDate.before(upperDate)) {
            // work out the value, label and position
            double xx = this.translateDateToJava2D(tickDate, plotArea);
            String tickLabel = this.tickLabelFormatter.format(tickDate);
            labelBounds = tickLabelFont.getStringBounds(tickLabel, g2.getFontRenderContext());
            LineMetrics metrics = tickLabelFont.getLineMetrics(tickLabel, frc);
            float x = 0.0f;
            float y = 0.0f;
            if (this.verticalTickLabels) {
                x = (float)(xx+labelBounds.getHeight()/2-metrics.getDescent());
                y = (float)(plotArea.getMaxY()+tickLabelInsets.top+labelBounds.getWidth());
            }
            else {
                x = (float)(xx-labelBounds.getWidth()/2);
                y = (float)(plotArea.getMaxY()+tickLabelInsets.top+labelBounds.getHeight());
            }
            Tick tick = new Tick(tickDate, tickLabel, x, y);
            ticks.add(tick);
            tickDate = this.tickUnit.addToDate(tickDate);
        }

    }

    /**
     * Draws the plot on a Java 2D graphics device (such as the screen or a printer).
     *
     * @param g2 The graphics device.
     * @param drawArea The area within which the chart should be drawn.
     * @param plotArea The area within which the plot should be drawn (a subset of the drawArea).
     */
    public void draw(Graphics2D g2, Rectangle2D drawArea, Rectangle2D plotArea) {

        if (!visible) return;

        // draw the axis label
        if (this.label!=null) {
            g2.setFont(labelFont);
            g2.setPaint(labelPaint);
            FontRenderContext frc = g2.getFontRenderContext();
            Rectangle2D labelBounds = labelFont.getStringBounds(label, frc);
            LineMetrics lm = labelFont.getLineMetrics(label, frc);
            float labelx = (float)(plotArea.getX()+plotArea.getWidth()/2-labelBounds.getWidth()/2);
            float labely = (float)(drawArea.getMaxY()-labelInsets.bottom
                                   -lm.getDescent()-lm.getLeading());
            g2.drawString(label, labelx, labely);
        }

        // draw the tick labels and marks
        this.refreshTicks(g2, drawArea, plotArea);
        float maxY = (float)plotArea.getMaxY();
        g2.setFont(getTickLabelFont());

        Iterator iterator = ticks.iterator();
        while (iterator.hasNext()) {
            Tick tick = (Tick)iterator.next();
            float xx = (float)this.translateValueToJava2D(tick.getNumericalValue(), plotArea);

            if (tickLabelsVisible) {
                g2.setPaint(this.tickLabelPaint);
                if (this.verticalTickLabels) {
                    RefineryUtilities.drawRotatedString(tick.getText(), g2,
                                                        tick.getX(), tick.getY(), -Math.PI/2);
                }
                else {
                    g2.drawString(tick.getText(), tick.getX(), tick.getY());
                }
            }

            if (tickMarksVisible) {
                g2.setStroke(this.getTickMarkStroke());
                Line2D mark = new Line2D.Float(xx, maxY-2, xx, maxY+2);
                g2.draw(mark);
            }

            if (gridLinesVisible) {
                g2.setStroke(gridStroke);
                g2.setPaint(gridPaint);
                Line2D gridline = new Line2D.Float(xx, (float)plotArea.getMaxY(), xx,
                                                   (float)plotArea.getMinY());
                g2.draw(gridline);
            }

        }

    }

    /**
     * Returns the height required to draw the axis in the specified draw area.
     *
     * @param g2 The graphics device.
     * @param plot The plot that the axis belongs to.
     * @param drawArea The area within which the plot should be drawn.
     *
     * @return The height.
     */
    public double reserveHeight(Graphics2D g2, Plot plot, Rectangle2D drawArea) {

        if (!visible) return 0.0;

        // calculate the height of the axis label...
        double labelHeight = 0.0;
        if (label!=null) {
            LineMetrics metrics = labelFont.getLineMetrics(label, g2.getFontRenderContext());
            labelHeight = this.labelInsets.top+metrics.getHeight()+this.labelInsets.bottom;
        }

        // calculate the height required for the tick labels (if visible);
        double tickLabelHeight = tickLabelInsets.top+tickLabelInsets.bottom;
        if (tickLabelsVisible) {
            g2.setFont(tickLabelFont);
            this.refreshTicks(g2, drawArea, drawArea);
            tickLabelHeight = tickLabelHeight+getMaxTickLabelHeight(g2, drawArea,
                                                                    this.verticalTickLabels);
        }
        return labelHeight+tickLabelHeight;

    }

    /**
     * Returns area in which the axis will be displayed.
     *
     * @param g2 The graphics device.
     * @param plot The plot.
     * @param drawArea The drawing area.
     * @param reservedWidth The width already reserved for the vertical axis.
     *
     * @return The area to reserve for the horizontal axis.
     */
    public Rectangle2D reserveAxisArea(Graphics2D g2, Plot plot, Rectangle2D drawArea,
                                       double reservedWidth) {

        if (!visible) {
            return new Rectangle2D.Double(drawArea.getX(), drawArea.getMaxY(),
                                          drawArea.getWidth()-reservedWidth,
                                          0.0);

        }
        // calculate the height of the axis label...
        LineMetrics metrics = labelFont.getLineMetrics(label, g2.getFontRenderContext());
        double labelHeight = this.labelInsets.top+metrics.getHeight()+this.labelInsets.bottom;

        // calculate the height required for the tick labels (if visible);
        double tickLabelHeight = tickLabelInsets.top+tickLabelInsets.bottom;
        if (tickLabelsVisible) {
            g2.setFont(tickLabelFont);
            this.refreshTicks(g2, drawArea, drawArea);
            tickLabelHeight = tickLabelHeight+getMaxTickLabelHeight(g2, drawArea,
                                                                    this.verticalTickLabels);
        }

        return new Rectangle2D.Double(drawArea.getX(), drawArea.getMaxY(),
                                      drawArea.getWidth()-reservedWidth,
                                      labelHeight+tickLabelHeight);

    }

    /**
     * Determines an appropriate tick value for the axis...
     *
     * @param g2 The graphics device.
     * @param drawArea The drawing area.
     * @param plotArea The plotting area.
     */
    private void calculateAutoTickUnits(Graphics2D g2, Rectangle2D drawArea, Rectangle2D plotArea) {

        // find the index of the largest standard tick magnitude that fits into the axis range
        int index = this.findAxisMagnitudeIndex();
        boolean labelsFit = true;
        while (labelsFit && index>0) {
            index=index-1;
            labelsFit = tickLabelsFit(index, g2, drawArea, plotArea);
        }

        if (labelsFit) {
            this.autoTickIndex=index;
        }
        else {
            this.autoTickIndex=Math.min(index+1, this.standardTickUnitMagnitudes.length);
        }

        this.tickLabelFormatter.applyPattern(this.standardTickFormats[autoTickIndex]);
        this.tickUnit = new DateUnit(this.standardTickUnits[autoTickIndex][0],
                                     this.standardTickUnits[autoTickIndex][1]);

        // there are two special cases to handle
        // (1) the highest index doesn't fit, but there is no "next one up" to use;
        // (2) the lowest index DOES fit, so we should use it rather than the next one up
        // otherwise, step up one index and use it
    }

    /**
     * Determines whether or not the tick labels fit given the available space.
     *
     * @param index Index into the standard tick unit arrays.
     * @param g2 The graphics device.
     * @param drawArea The drawing area.
     * @param plotArea The plotting area.
     *
     * @return A boolean indicating whether or not the tick labels fit (don't overlap).
     */
    private boolean tickLabelsFit(int index,
                                  Graphics2D g2, Rectangle2D drawArea, Rectangle2D plotArea) {

        // generate one label at a time until all are done OR there is an overlap (so fit==FALSE)
        SimpleDateFormat dateFormatter = new SimpleDateFormat(standardTickFormats[index]);
        DateUnit units = new DateUnit(this.standardTickUnits[index][0],
                                      this.standardTickUnits[index][1]);
        double lastLabelExtent = Double.NEGATIVE_INFINITY;
        double labelExtent;
        boolean labelsFit = true;
        Date tickDate = this.calculateLowestVisibleTickValue(units);
        Date upperDate = this.getMaximumDate();
        while (tickDate.before(upperDate) && labelsFit) {
            double xx = this.translateDateToJava2D(tickDate, plotArea);
            String tickLabel = dateFormatter.format(tickDate);
            Rectangle2D tickLabelBounds = tickLabelFont.getStringBounds(tickLabel,
                                                                        g2.getFontRenderContext());
            if (this.verticalTickLabels) {
                labelExtent = xx-(tickLabelBounds.getHeight()/2);
                if (labelExtent<lastLabelExtent) labelsFit = false;
                lastLabelExtent = xx+(tickLabelBounds.getHeight()/2);
            }
            else {
                labelExtent = xx-(tickLabelBounds.getWidth()/2);
                if (labelExtent<lastLabelExtent) labelsFit = false;
                lastLabelExtent = xx+(tickLabelBounds.getWidth()/2);
            }
            tickDate = units.addToDate(tickDate);
        }

        return labelsFit;

    }

    /**
     * A utility method for determining the height of the tallest tick label.
     *
     * @param g2 The graphics device.
     * @param drawArea The drawing area.
     * @param vertical A flag indicating whether or not the tick labels are rotated to vertical.
     *
     * @return The maximum tick label height.
     */
    private double getMaxTickLabelHeight(Graphics2D g2, Rectangle2D drawArea, boolean vertical) {

        Font font = getTickLabelFont();
        g2.setFont(font);
        FontRenderContext frc = g2.getFontRenderContext();
        double maxHeight = 0.0;
        if (vertical) {
            Iterator iterator = this.ticks.iterator();
            while (iterator.hasNext()) {
                Tick tick = (Tick)iterator.next();
                Rectangle2D labelBounds = font.getStringBounds(tick.getText(), frc);
                if (labelBounds.getWidth()>maxHeight) {
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
     * Returns true if the specified plot is compatible with the axis, and false otherwise.
     * <p>
     * The HorizontalDateAxis class expects the plot to implement the HorizontalValuePlot interface.
     *
     * @param plot The plot.
     *
     * @return A flag indicating whether or not the plot is compatible with the axis.
     */
    protected boolean isCompatiblePlot(Plot plot) {
        if (plot instanceof HorizontalValuePlot) return true;
        else return false;
    }

}
