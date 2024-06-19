/* =======================================
 * JFreeChart : a Java Chart Class Library
 * =======================================
 * Version:         0.5.6;
 * Project Lead:    David Gilbert (david.gilbert@bigfoot.com);
 *
 * File:            HorizontalNumberAxis.java
 * Author:          David Gilbert;
 * Contributor(s):  -;
 *
 * (C) Copyright 2000, Simba Management Limited;
 *
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation;
 * either version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this library;
 * if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston,
 * MA 02111-1307, USA.
 *
 * $Id: HorizontalNumberAxis.java,v 1.1 2007/10/10 18:52:16 vauchers Exp $
 */

package com.jrefinery.chart;

import java.awt.*;
import java.awt.font.*;
import java.awt.geom.*;
import java.text.*;
import java.util.*;

/**
 * A horizontal axis that displays numerical values.  Used in XY plots.
 * @see XYPlot
 */
public class HorizontalNumberAxis extends NumberAxis implements HorizontalAxis {

  /** A flag indicating whether or not tick labels are drawn vertically. */
  protected boolean verticalTickLabels;

  /**
   * Full constructor: returns a new HorizontalValueAxis with attributes as specified by the
   * caller. There are other constructors that use default values for some attributes.
   * @param label The axis label;
   * @param labelFont The font for displaying the axis label;
   * @param labelPaint The paint used to draw the axis label;
   * @param labelInsets The blank space around the axis label;
   * @param showTickLabels Flag indicating whether or not tick labels are visible;
   * @param tickLabelFont Font for displaying tick labels;
   * @param tickLabelPaint The paint used to display the tick labels;
   * @param tickLabelInsets The blank space around the tick labels;
   * @param verticalTickLabels A flag indicating whether or not tick labels are drawn vertically;
   * @param showTickMarks Flag indicating whether or not tick marks are visible;
   * @param tickMarkStroke The stroke used to draw tick marks (if visible);
   * @param autoRange Flag indicating whether or not the axis range is automatically determined to
   *                  fit the data;
   * @param autoRangeIncludesZero A flag indicating whether or not the axis range *must* include
   *                              zero;
   * @param autoRangeMinimum The smallest axis range permitted (avoids problems with a 'zero'
   *                         range);
   * @param minimumAxisValue The lowest value shown on the axis;
   * @param maximumAxisValue The highest value shown on the axis;
   * @param autoTickValue A flag indicating whether or not the tick value is automatically
   *                      calculated;
   * @param tickValue The tick value;
   * @param df The format object used to display tick labels;
   * @param showGridLines Flag indicating whether or not grid lines are visible for this axis;
   * @param gridStroke The Stroke used to display grid lines (if visible);
   * @param gridPaint The Paint used to display grid lines (if visible).
   */
  public HorizontalNumberAxis(String label, Font labelFont,
           Paint labelPaint, Insets labelInsets,
           boolean showTickLabels, Font tickLabelFont, Paint tickLabelPaint, Insets tickLabelInsets,
           boolean verticalTickLabels,
           boolean showTickMarks, Stroke tickMarkStroke,
           boolean autoRange, boolean autoRangeIncludesZero, Number autoRangeMinimum,
           Number minimumAxisValue, Number maximumAxisValue,
           boolean autoTickValue, Number tickValue, DecimalFormat df,
           boolean showGridLines, Stroke gridStroke, Paint gridPaint) {

    super(label, labelFont, labelPaint, labelInsets,
          showTickLabels, tickLabelFont, tickLabelPaint, tickLabelInsets,
          showTickMarks, tickMarkStroke,
          autoRange, autoRangeIncludesZero, autoRangeMinimum,
          minimumAxisValue, maximumAxisValue, autoTickValue, tickValue, df,
          showGridLines, gridStroke, gridPaint);

    this.verticalTickLabels = verticalTickLabels;

  }

  /**
   * Standard constructor: returns a HorizontalValueAxis with some default attributes.
   * @param label The axis label;
   * @param labelFont The font for displaying the axis label;
   * @param minimumAxisValue The lowest value shown on the axis;
   * @param maximumAxisValue The highest value shown on the axis;
   */
  public HorizontalNumberAxis(String label, Font labelFont,
                              Number minimumAxisValue, Number maximumAxisValue) {

    this(label, labelFont, Color.black, new Insets(4, 4, 4, 4),
         true, new Font("Arial", Font.PLAIN, 10), Color.black, new Insets(1, 2, 1, 2),
         true, true, new BasicStroke(1),
         false, true, new Double(0.000001), minimumAxisValue, maximumAxisValue,
         false, new Double(5.0),
         new DecimalFormat("0"),
         false, null, null);

  }

  /**
   * Standard constructor - builds a HorizontalNumberAxis with some default attributes.
   * @param label The axis label;
   */
  public HorizontalNumberAxis(String label) {
    super(label);
    this.verticalTickLabels = false;
  }

  /**
   * Returns a flag indicating whether the tick labels are drawn 'vertically'.
   */
  public boolean getVerticalTickLabels() {
    return this.verticalTickLabels;
  }

  /**
   * Sets the flag that determines whether the tick labels are drawn 'vertically'.
   * @param flag The new value of the flag;
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
    super.configure();
    if (isAutoRange()) {
      autoAdjustRange();
    }
  }

  /**
   * Translates the data value to the display coordinates (Java 2D User Space) of the chart.
   * @param dataValue The value to be plotted;
   * @param plotArea The plot area in Java 2D User Space;
   */
  public double translatedValue(Number dataValue, Rectangle2D plotArea) {
    double value = dataValue.doubleValue();
    double axisMin = minimumAxisValue.doubleValue();
    double axisMax = maximumAxisValue.doubleValue();
    double plotX = plotArea.getX();
    double plotMaxX = plotArea.getMaxX();
    return plotX + ((value - axisMin)/(axisMax - axisMin)) * (plotMaxX - plotX);
  }

  /**
   * Rescales the axis to ensure that all data is visible.
   */
  public void autoAdjustRange() {

    if (plot!=null) {
      if (plot instanceof HorizontalValuePlot) {
        HorizontalValuePlot hvp = (HorizontalValuePlot)plot;
        double upper = hvp.getMaximumHorizontalDataValue().doubleValue();
        double lower = hvp.getMinimumHorizontalDataValue().doubleValue();
        double range = upper-lower;

        double minRange = this.autoRangeMinimum.doubleValue();
        if (range<minRange) {
          upper = (upper+lower+minRange)/2;
          lower = (upper+lower-minRange)/2;
        }

        if (this.autoRangeIncludesZero()) {
          if (upper!=0.0) upper = Math.max(0.0, upper+0.05*range);
          if (lower!=0.0) lower = Math.min(0.0, lower-0.05*range);
        }
        else {
          if (upper!=0.0) upper = upper+0.05*range;
          if (lower!=0.0) lower = lower-0.05*range;
        }

        this.minimumAxisValue=new Double(lower);
        this.maximumAxisValue=new Double(upper);
      }
    }

  }

  /**
   * Creates a list of ticks ready for drawing.
   * @param g2 The graphics device;
   * @param drawArea The area within which the plot and axes should be drawn;
   * @param plotArea The area within which the plot should be drawn;
   */
  public void refreshTicks(Graphics2D g2, Rectangle2D drawArea, Rectangle2D plotArea) {

    g2.setFont(tickLabelFont);
    this.ticks.clear();
    if (this.autoTickValue) {
      calculateAutoTickValue(g2, drawArea, plotArea);
    }
    Number units = this.getTickValue();
    int count = this.calculateVisibleTickCount();
    Number lowestTickValue = this.calculateLowestVisibleTickValue();
    tickLabelFormatter = new DecimalFormat(tickLabelFormatter.toPattern());
    for (int i=0; i<count; i++) {
      Number currentTickValue = new Double(lowestTickValue.doubleValue()+(i*units.doubleValue()));
      double xx = this.translatedValue(currentTickValue, plotArea);
      String tickLabel = this.tickLabelFormatter.format(currentTickValue);
      Rectangle2D tickLabelBounds = tickLabelFont.getStringBounds(tickLabel,
                                      g2.getFontRenderContext());
      float x = 0.0f;
      float y = 0.0f;
      if (this.verticalTickLabels) {
        x = (float)(xx+tickLabelBounds.getHeight()/2);
        y = (float)(plotArea.getMaxY()+tickLabelInsets.top+tickLabelBounds.getWidth());
      }
      else {
        x = (float)(xx-tickLabelBounds.getWidth()/2);
        y = (float)(plotArea.getMaxY()+tickLabelInsets.top+tickLabelBounds.getHeight());
      }
      Tick tick = new Tick(currentTickValue, tickLabel, x, y);
      ticks.add(tick);
    }

  }

  /**
   * Draws the plot on a Java 2D graphics device (such as the screen or a printer).
   * @param g2 The graphics device;
   * @param drawArea The area within which the chart should be drawn;
   * @param plotArea The area within which the plot should be drawn (a subset of the drawArea);
   */
  public void draw(Graphics2D g2, Rectangle2D drawArea, Rectangle2D plotArea) {

    // draw the axis label
    g2.setFont(labelFont);
    g2.setPaint(labelPaint);
    FontRenderContext frc = g2.getFontRenderContext();
    Rectangle2D labelBounds = labelFont.getStringBounds(label, frc);
    LineMetrics lm = labelFont.getLineMetrics(label, frc);
    float labelx = (float)(plotArea.getX()+plotArea.getWidth()/2-labelBounds.getWidth()/2);
    float labely = (float)(drawArea.getMaxY()-labelInsets.bottom-lm.getDescent()-lm.getLeading());
    g2.drawString(label, labelx, labely);

    // draw the tick labels and marks
    this.refreshTicks(g2, drawArea, plotArea);

    float maxY = (float)plotArea.getMaxY();
    g2.setFont(getTickLabelFont());

    Iterator iterator = ticks.iterator();
    while (iterator.hasNext()) {
      Tick tick = (Tick)iterator.next();
      float xx = (float)this.translatedValue(tick.getNumericalValue(), plotArea);
      if (showTickLabels) {
        g2.setPaint(this.tickLabelPaint);
        if (this.verticalTickLabels) {
          drawVerticalString(tick.getText(), g2, tick.getX(), tick.getY());
        }
        else {
          g2.drawString(tick.getText(), tick.getX(), tick.getY());
        }
      }
      if (showTickMarks) {
        g2.setStroke(this.getTickMarkStroke());
        Line2D mark = new Line2D.Float(xx, maxY-2, xx, maxY+2);
        g2.draw(mark);
      }
      if (showGridLines) {
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
   * @param g2 The graphics device;
   * @param plot The plot that the axis belongs to;
   * @param drawArea The area within which the plot should be drawn;
   */
  public double reserveHeight(Graphics2D g2, Plot plot, Rectangle2D drawArea) {

    // calculate the height of the axis label...
    LineMetrics metrics = labelFont.getLineMetrics(label, g2.getFontRenderContext());
    double labelHeight = this.labelInsets.top+metrics.getHeight()+this.labelInsets.bottom;

    // calculate the height required for the tick labels (if visible);
    double tickLabelHeight = tickLabelInsets.top+tickLabelInsets.bottom;
    if (showTickLabels) {
      g2.setFont(tickLabelFont);
      this.refreshTicks(g2, drawArea, drawArea);
      tickLabelHeight = tickLabelHeight+getMaxTickLabelHeight(g2, drawArea,
                                                              this.verticalTickLabels);
    }
    return labelHeight+tickLabelHeight;

  }

  /**
   * Returns area in which the axis will be displayed.
   * @param g2 The graphics device;
   * @param plot A reference to the plot;
   * @param drawArea The area within which the plot and axes should be drawn;
   * @param reservedWidth The space already reserved for the vertical axis;
   */
  public Rectangle2D reserveAxisArea(Graphics2D g2, Plot plot, Rectangle2D drawArea,
                                     double reservedWidth) {

    // calculate the height of the axis label...
    LineMetrics metrics = labelFont.getLineMetrics(label, g2.getFontRenderContext());
    double labelHeight = this.labelInsets.top+metrics.getHeight()+this.labelInsets.bottom;

    // calculate the height required for the tick labels (if visible);
    double tickLabelHeight = tickLabelInsets.top+tickLabelInsets.bottom;
    if (showTickLabels) {
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
   * Works out the standard tick value to use.
   * @param g2 The graphics device;
   * @param drawArea The area within which the plot and axes should be drawn;
   * @param plotArea The area within which the plot should be drawn;
   */
  private void calculateAutoTickValue(Graphics2D g2, Rectangle2D drawArea, Rectangle2D plotArea) {

    if (autoTickValue) {
      indexTickUnits();
      double zero = this.translatedValue(Plot.ZERO, plotArea);
      int fitsIndex = autoTickIndex;
      int fitsNotIndex = autoTickIndex;
      if (tickLabelsFit(this.translatedValue(tickValue, plotArea)-zero, g2)) {
        fitsNotIndex = fitsIndex-1;
        while (tickLabelsFit(this.translatedValue(autoTickValues[fitsNotIndex], plotArea)-zero, g2)) {
          fitsIndex = fitsNotIndex;
          if (fitsIndex==0) break;
          fitsNotIndex=fitsNotIndex-1;
        }
      }
      else {
        fitsNotIndex = autoTickIndex;
        fitsIndex = fitsNotIndex+1;
        if (fitsIndex!=autoTickValues.length) {
          while (!tickLabelsFit(translatedValue(autoTickValues[fitsIndex], plotArea)-zero, g2)) {
            fitsNotIndex = fitsIndex;
            if (fitsNotIndex>=autoTickValues.length-1) break;
              fitsIndex = fitsIndex+1;
          }
        }
        else {
          fitsIndex = fitsNotIndex;
        }
      }
      tickValue = autoTickValues[fitsIndex];
      tickLabelFormatter.applyPattern(autoTickFormats[fitsIndex]);
    }

  }

  /**
   * Returns true if the tick labels fit, and false otherwise.
   * @param unit The tick value;
   * @param g2 The graphics device;
   */
  private boolean tickLabelsFit(double unit, Graphics2D g2) {
    FontRenderContext frc = g2.getFontRenderContext();
    return ((Math.abs(unit))>g2.getFont().getStringBounds("10.1234", frc).getWidth());
  }

  /**
   * A utility method for determining the height of the tallest tick label.
   * @param g2 The graphics device;
   * @param drawArea The area within which the plot and axes should be drawn;
   * @param vertical A flag that indicates whether or not the tick labels are 'vertical';
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

}
