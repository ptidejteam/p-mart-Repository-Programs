/* =======================================
 * JFreeChart : a Java Chart Class Library
 * =======================================
 * Version:         0.5.6;
 * Project Lead:    David Gilbert (david.gilbert@bigfoot.com);
 *
 * File:            VerticalNumberAxis.java
 * Author:          David Gilbert;
 * Contributor(s):  David Li;
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
 * $Id: VerticalNumberAxis.java,v 1.1 2007/10/10 18:52:16 vauchers Exp $
 */

package com.jrefinery.chart;

import java.awt.*;
import java.awt.font.*;
import java.awt.geom.*;
import java.text.*;
import java.util.*;

import com.jrefinery.chart.event.*;

/**
 * A standard linear value axis, for values displayed vertically.
 */
public class VerticalNumberAxis extends NumberAxis implements VerticalAxis {

  /** A flag indicating whether or not the axis label is drawn vertically. */
  protected boolean verticalLabel;

  /**
   * Full constructor: returns a new VerticalValueAxis.
   * @param label The axis label;
   * @param labelFont The font for displaying the axis label;
   * @param labelPaint The paint used to draw the axis label;
   * @param labelInsets Determines the amount of blank space around the label;
   * @param verticalLabel Flag indicating whether or not the label is drawn vertically;
   * @param showTickLabels Flag indicating whether or not tick labels are visible;
   * @param tickLabelFont The font used to display tick labels;
   * @param tickLabelPaint The paint used to draw tick labels;
   * @param tickLabelInsets Determines the amount of blank space around tick labels;
   * @param showTickMarks Flag indicating whether or not tick marks are visible;
   * @param tickMarkStroke The stroke used to draw tick marks (if visible).
   * @param autoRange Flag indicating whether or not the axis is automatically scaled to fit the
   *                  data;
   * @param autoRangeIncludesZero - A flag indicating whether or not zero *must* be displayed on
   *                                axis;
   * @param autoRangeMinimum - the smallest automatic range allowed;
   * @param minimumAxisValue The lowest value shown on the axis;
   * @param maximumAxisValue The highest value shown on the axis;
   * @param autoTickValue A flag indicating whether or not the tick units are automatically calculated;
   * @param tickValue The tick units;
   * @param formatter The format object used to display tick labels;
   * @param showGridLines Flag indicating whether or not grid lines are visible for this axis;
   * @param gridStroke The Stroke used to display grid lines (if visible);
   * @param gridPaint The Paint used to display grid lines (if visible).
   */
  public VerticalNumberAxis(String label, Font labelFont, Paint labelPaint, Insets labelInsets,
           boolean verticalLabel,
           boolean showTickLabels, Font tickLabelFont, Paint tickLabelPaint, Insets tickLabelInsets,
           boolean showTickMarks, Stroke tickMarkStroke,
           boolean autoRange, boolean autoRangeIncludesZero, Number autoRangeMinimum,
           Number minimumAxisValue, Number maximumAxisValue,
           boolean autoTickValue, Number tickValue, DecimalFormat formatter,
           boolean showGridLines, Stroke gridStroke, Paint gridPaint) {

    super(label, labelFont, labelPaint, labelInsets, showTickLabels, tickLabelFont, tickLabelPaint,
          tickLabelInsets, showTickMarks, tickMarkStroke,
          autoRange, autoRangeIncludesZero, autoRangeMinimum,
          minimumAxisValue, maximumAxisValue, autoTickValue, tickValue,
          formatter, showGridLines, gridStroke, gridPaint);

    this.verticalLabel = verticalLabel;

  }

  /**
   * Standard constructor: returns a VerticalValueAxis with some default attributes.
   * @param label The axis label;
   * @param labelFont The font for displaying the axis label;
   * @param minimumAxisValue The lowest value shown on the axis;
   * @param maximumAxisValue The highest value shown on the axis;
   */
  public VerticalNumberAxis(String label, Font labelFont,
                            Number minimumAxisValue, Number maximumAxisValue) {

    this(label, labelFont, Color.black, new Insets(4, 4, 4, 4), true,
         true, new Font("Arial", Font.PLAIN, 10), Color.black, new Insets(2, 1, 2, 1),
         true, new BasicStroke(1), false, true, new Double(0.00001),
         minimumAxisValue, maximumAxisValue,
         true, new Double(5.0),
         new DecimalFormat("0"),
         false, new BasicStroke(1), Color.lightGray);

  }

  /**
   * Standard constructor - builds a VerticalValueAxis with mostly default attributes.
   * @param label The axis label;
   */
  public VerticalNumberAxis(String label) {
    super(label);
    this.verticalLabel = true;
  }

  /**
   * Default constructor.
   */
  public VerticalNumberAxis() {
    super(null);
    this.verticalLabel = true;
  }

  /**
   * Returns a flag that indicates whether or not the axis label is drawn 'vertically'.
   */
    public boolean getVerticalLabel() {
    return this.verticalLabel;
  }

  /**
   * Sets the flag that controls whether or not the axis label is drawn 'vertically'.
   * @param flag The new value of the flag;
   */
  public void setVerticalLabel(boolean flag) {
    this.verticalLabel = flag;
    this.notifyListeners(new AxisChangeEvent(this));
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
   * @param plotArea The plot area in Java 2D User Space.
   */
  public double translatedValue(Number dataValue, Rectangle2D plotArea) {
    double value = dataValue.doubleValue();
    double axisMin = minimumAxisValue.doubleValue();
    double axisMax = maximumAxisValue.doubleValue();

    double maxY = plotArea.getMaxY();
    double minY = plotArea.getMinY();
    return maxY - (((value - axisMin)/(axisMax - axisMin)) * (maxY - minY));
  }

  /**
   * Rescales the axis to ensure that all data is visible.
   */
  public void autoAdjustRange() {

    if (plot!=null) {
      if (plot instanceof VerticalValuePlot) {
        VerticalValuePlot vvp = (VerticalValuePlot)plot;
        double upper = vvp.getMaximumVerticalDataValue().doubleValue();
        double lower = vvp.getMinimumVerticalDataValue().doubleValue();
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
   * Calculates the positions of the tick labels for the axis, storing the results in the
   * tick label list (ready for drawing).
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
      double yy = this.translatedValue(currentTickValue, plotArea);
      String tickLabel = this.tickLabelFormatter.format(currentTickValue);
      Rectangle2D tickLabelBounds = tickLabelFont.getStringBounds(tickLabel, g2.getFontRenderContext());
      float x = (float)(plotArea.getX()-tickLabelBounds.getWidth()-tickLabelInsets.left-tickLabelInsets.right);
      float y = (float)(yy+(tickLabelBounds.getHeight()/2));
      Tick tick = new Tick(currentTickValue, tickLabel, x, y);
      ticks.add(tick);
    }

  }

  /**
   * Draws the plot on a Java 2D graphics device (such as the screen or a printer).
   * @param g2 The graphics device;
   * @param drawArea The area within which the chart should be drawn.
   * @param plotArea The area within which the plot should be drawn (a subset of the drawArea).
   */
  public void draw(Graphics2D g2, Rectangle2D drawArea, Rectangle2D plotArea) {

    // draw the axis label
    if (this.label!=null) {
      g2.setFont(labelFont);
      g2.setPaint(labelPaint);

      Rectangle2D labelBounds = labelFont.getStringBounds(label, g2.getFontRenderContext());
      if (verticalLabel) {
        double xx = drawArea.getX()+labelInsets.left+labelBounds.getHeight();
        double yy = plotArea.getY()+plotArea.getHeight()/2+(labelBounds.getWidth()/2);
        drawVerticalString(label, g2, (float)xx, (float)yy);
      }
      else {
        double xx = drawArea.getX()+labelInsets.left;
        double yy = drawArea.getY()+drawArea.getHeight()/2-labelBounds.getHeight()/2;
        g2.drawString(label, (float)xx, (float)yy);
      }
    }

    // draw the tick labels and marks and gridlines
    this.refreshTicks(g2, drawArea, plotArea);
    double xx = plotArea.getX();
    g2.setFont(tickLabelFont);

    Iterator iterator = ticks.iterator();
    while (iterator.hasNext()) {
      Tick tick = (Tick)iterator.next();
      float yy = (float)this.translatedValue(tick.getNumericalValue(), plotArea);
      if (showTickLabels) {
        g2.setPaint(this.tickLabelPaint);
        g2.drawString(tick.getText(), tick.getX(), tick.getY());
      }
      if (showTickMarks) {
        g2.setStroke(this.getTickMarkStroke());
        Line2D mark = new Line2D.Double(plotArea.getX()-2, yy,
                                        plotArea.getX()+2, yy);
        g2.draw(mark);
      }
      if (showGridLines) {
        g2.setStroke(gridStroke);
        g2.setPaint(gridPaint);
        Line2D gridline = new Line2D.Double(xx, yy,
                                            plotArea.getMaxX(), yy);
        g2.draw(gridline);
      }
    }

  }

  /**
   * Returns the width required to draw the axis in the specified draw area.
   * @param g2 The graphics device;
   * @param plot A reference to the plot;
   * @param drawArea The area within which the plot should be drawn.
   */
  public double reserveWidth(Graphics2D g2, Plot plot, Rectangle2D drawArea) {

    // calculate the width of the axis label...
    double labelWidth = 0.0;
    if (label!=null) {
      Rectangle2D labelBounds = labelFont.getStringBounds(label, g2.getFontRenderContext());
      labelWidth = labelInsets.left+labelInsets.right;
      if (this.verticalLabel) {
        labelWidth = labelWidth + labelBounds.getHeight();  // assume width == height before rotation
      }
      else {
        labelWidth = labelWidth + labelBounds.getWidth();
      }
    }

    // calculate the width required for the tick labels (if visible);
    double tickLabelWidth = tickLabelInsets.left+tickLabelInsets.right;
    if (showTickLabels) {
      this.refreshTicks(g2, drawArea, drawArea);
      tickLabelWidth = tickLabelWidth+getMaxTickLabelWidth(g2, drawArea);
    }
    return labelWidth+tickLabelWidth;

  }

  /**
   * Returns area in which the axis will be displayed.
   * @param g2 The graphics device;
   * @param plot A reference to the plot;
   * @param drawArea The area in which the plot and axes should be drawn;
   * @param reservedHeight The height reserved for the horizontal axis;
   */
  public Rectangle2D reserveAxisArea(Graphics2D g2, Plot plot, Rectangle2D drawArea,
                                     double reservedHeight) {

    // calculate the width of the axis label...
    double labelWidth = 0.0;
    if (label!=null) {
      Rectangle2D labelBounds = labelFont.getStringBounds(label, g2.getFontRenderContext());
      labelWidth = labelInsets.left+labelInsets.right;
      if (this.verticalLabel) {
        labelWidth = labelWidth + labelBounds.getHeight();  // assume width == height before rotation
      }
      else {
        labelWidth = labelWidth + labelBounds.getWidth();
      }
    }

    // calculate the width of the tick labels
    double tickLabelWidth = tickLabelInsets.left+tickLabelInsets.right;
    if (showTickLabels) {
      Rectangle2D approximatePlotArea = new Rectangle2D.Double(drawArea.getX(), drawArea.getY(),
                                              drawArea.getWidth(),
                                              drawArea.getHeight()-reservedHeight);
      this.refreshTicks(g2, drawArea, approximatePlotArea);
      tickLabelWidth = tickLabelWidth+getMaxTickLabelWidth(g2, approximatePlotArea);
    }

    return new Rectangle2D.Double(drawArea.getX(), drawArea.getY(), labelWidth+tickLabelWidth,
                                  drawArea.getHeight()-reservedHeight);

  }

  /**
   * Selects an appropriate tick value for the axis.  The strategy is to display as many ticks as
   * possible (selected from an array of 'standard' tick units) without the labels overlapping.
   * @param g2 The graphics device;
   * @param drawArea The area in which the plot and axes should be drawn;
   * @param plotArea The area in which the plot should be drawn;
   */
  private void calculateAutoTickValue(Graphics2D g2, Rectangle2D drawArea, Rectangle2D plotArea) {

    if (autoTickValue) {
      indexTickUnits();
      double zero = this.translatedValue(Plot.ZERO, plotArea);
      int fitsIndex = autoTickIndex;
      int fitsNotIndex = autoTickIndex;
      if (tickLabelsFit(this.translatedValue(tickValue, plotArea)-zero, g2)) {

	if (fitsIndex>0) {
          fitsNotIndex = fitsIndex-1;
          while (tickLabelsFit(this.translatedValue(autoTickValues[fitsNotIndex],
                                                    plotArea)-zero, g2)) {
            fitsIndex = fitsNotIndex;
            fitsNotIndex=fitsNotIndex-1;
            if (fitsIndex==0) break;
          }
        }
      }
      else {
        fitsNotIndex = autoTickIndex;
        fitsIndex = fitsNotIndex+1;
        if (fitsIndex!=autoTickValues.length) {
          while (!tickLabelsFit(translatedValue(autoTickValues[fitsIndex], plotArea)-zero, g2)) {
            fitsNotIndex = fitsIndex;
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
   * Determines whether or not the labels for the specified tick value will fit.
   * @param unit The tick value;
   * @param g2 The graphics device;
   */
  private boolean tickLabelsFit(double unit, Graphics2D g2) {
    FontRenderContext frc = g2.getFontRenderContext();
    return ((Math.abs(unit))>tickLabelFont.getLineMetrics("XYZxyz", frc).getHeight());
  }

}










