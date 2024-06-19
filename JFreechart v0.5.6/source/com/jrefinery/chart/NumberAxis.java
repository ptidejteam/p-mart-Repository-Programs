/* =======================================
 * JFreeChart : a Java Chart Class Library
 * =======================================
 * Version:         0.5.6;
 * Project Lead:    David Gilbert (david.gilbert@bigfoot.com);
 *
 * File:            NumberAxis.java
 * Author:          David Gilbert;
 * Contributor(s):  Laurence Vanhelsuwe;
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
 * $Id: NumberAxis.java,v 1.1 2007/10/10 18:52:16 vauchers Exp $
 */

package com.jrefinery.chart;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.text.*;
import javax.swing.*;
import com.jrefinery.chart.event.*;

/**
 * The base class for axes that display numerical data.
 * <P>
 * The 'auto tick value' mechanism is an adaptation of code suggested by Laurence Vanhelsuwe
 * (see LV's online book "Mastering JavaBeans" at http:www.lv.clara.co.uk/masbeans.html).
 * @see HorizontalNumberAxis
 * @see VerticalNumberAxis
 */
public abstract class NumberAxis extends ValueAxis {

  /** The lowest value showing on the axis. */
  protected Number minimumAxisValue;

  /** The highest value showing on the axis. */
  protected Number maximumAxisValue;

  /** A flag that indicates whether or not zero *must* be included when automatically determining
    * the axis range. */
  protected boolean autoRangeIncludesZero;

  /** The minimum size of a range that is determined automatically. */
  protected Number autoRangeMinimum;

  /** The current value (size) of one tick. */
  protected Number tickValue;

  /** A formatter for the tick labels. */
  protected DecimalFormat tickLabelFormatter;

  /**
   * Full constructor - initialises the attributes for a NumberAxis.  This is an abstract class,
   * subclasses include HorizontalValueAxis and VerticalValueAxis.
   * @param label The axis label;
   * @param labelFont The font for displaying the axis label;
   * @param labelPaint The paint used to draw the axis label;
   * @param labelInsets Determines the amount of blank space around the label;
   * @param showTickLabels Flag indicating whether or not tick labels are visible;
   * @param tickLabelFont The font used to display tick labels;
   * @param tickLabelPaint The paint used to draw tick labels;
   * @param tickLabelInsets Determines the amount of blank space around tick labels;
   * @param showTickMarks Flag indicating whether or not tick marks are visible;
   * @param tickMarkStroke The stroke used to draw tick marks (if visible);
   * @param autoRange Flag indicating whether or not the axis range is automatically determined to
   *                  fit the current data;
   * @param autoRangeIncludesZero A flag indicating whether the auto range must include zero;
   * @param autoRangeMinimum The minimum size for a range that is determined automatically;
   * @param minimumAxisValue The lowest value shown on the axis;
   * @param maximumAxisValue The highest value shown on the axis;
   * @param autoTickValue A flag indicating whether or not the tick value is automatically
   *                      calculated;
   * @param tickValue The value (or size) of one tick;
   * @param tickLabelFormatter The format object used to format tick labels;
   * @param showGridLines Flag indicating whether or not grid lines are visible for this axis;
   * @param gridStroke The Stroke used to display grid lines (if visible);
   * @param gridPaint The Paint used to display grid lines (if visible);
   */
  public NumberAxis(String label, Font labelFont, Paint labelPaint, Insets labelInsets,
           boolean showTickLabels, Font tickLabelFont, Paint tickLabelPaint, Insets tickLabelInsets,
           boolean showTickMarks, Stroke tickMarkStroke,
           boolean autoRange, boolean autoRangeIncludesZero, Number autoRangeMinimum,
           Number minimumAxisValue, Number maximumAxisValue,
           boolean autoTickValue, Number tickValue,
           DecimalFormat tickLabelFormatter,
           boolean showGridLines, Stroke gridStroke, Paint gridPaint) {

    super(label, labelFont, labelPaint, labelInsets,
          showTickLabels, tickLabelFont, tickLabelPaint, tickLabelInsets,
          showTickMarks, tickMarkStroke, autoRange,
          autoTickValue, showGridLines, gridStroke, gridPaint);

    this.autoRangeIncludesZero = autoRangeIncludesZero;
    this.autoRangeMinimum = autoRangeMinimum;
    this.minimumAxisValue = minimumAxisValue;
    this.maximumAxisValue = maximumAxisValue;
    this.tickValue = tickValue;
    this.tickLabelFormatter = tickLabelFormatter;

  }

  /**
   * Standard constructor - builds a NumberAxis with mostly default attributes.
   * @param label The axis label;
   */
  public NumberAxis(String label) {
    super(label);
    this.autoRangeIncludesZero = true;
    this.autoRangeMinimum = this.autoTickValues[0];
    this.minimumAxisValue = null;  // the max and min will be set automatically
    this.maximumAxisValue = null;
    this.tickValue = new Double(1.0);
    this.tickLabelFormatter = new DecimalFormat("0");
  }

  /**
   * Returns the flag that indicates whether or not the automatic axis range (if indeed it is
   * determined automatically) is forced to include zero.
   */
  public boolean autoRangeIncludesZero() {
    return this.autoRangeIncludesZero;
  }

  /**
   * Sets the flag that indicates whether or not the automatic axis range is forced to include
   * zero.
   * @param flag The new value of the flag;
   */
  public void setAutoRangeIncludesZero(boolean flag) {
    if (autoRangeIncludesZero!=flag) {
      this.autoRangeIncludesZero = flag;
      notifyListeners(new AxisChangeEvent(this));
    }
  }

  /**
   * Returns the minimum size of the automatic axis range (if indeed it is determined
   * automatically).
   */
  public Number getAutoRangeMinimum() {
    return this.autoRangeMinimum;
  }

  /**
   * Sets the minimum size of the automatic axis range.
   * @param minimum The new minimum axis range;
   */
  public void setAutoRangeMinimum(Number minimum) {
    if (autoRangeMinimum.doubleValue()!=minimum.doubleValue()) {
      this.autoRangeMinimum = minimum;
      notifyListeners(new AxisChangeEvent(this));
    }
  }

  /**
   * Returns the minimum value for the axis.
   */
  public Number getMinimumAxisValue() {
    return minimumAxisValue;
  }

  /**
   * Sets the minimum value for the axis and notifies registered listeners that the axis has been
   * modified.
   * @param value The new minimum value; */
  public void setMinimumAxisValue(Number value) {
    this.minimumAxisValue = value;
    notifyListeners(new AxisChangeEvent(this));
  }

  /**
   * Returns the maximum value for the axis.
   */
  public Number getMaximumAxisValue() {
    return maximumAxisValue;
  }

  /**
   * Sets the maximum value for the axis and notifies registered listeners that the axis has been
   * modified.
   * @param value The new maximum value;
   */
  public void setMaximumAxisValue(Number value) {
    this.maximumAxisValue = value;
    notifyListeners(new AxisChangeEvent(this));
  }

  /**
   * Returns the current tick value (or size) for the axis.
   */
  public Number getTickValue() {
    return tickValue;
  }

  /**
   * Sets the tick value (or size) and notifies registered listeners that the axis has been
   * modified.
   * @param value The new tick value or size;
   */
  public void setTickValue(Number value) {
    this.tickValue = value;
    notifyListeners(new AxisChangeEvent(this));
  }

  /**
   * Returns the DecimalFormat object used to format tick labels.
   */
  public DecimalFormat getTickLabelFormatter() {
    return tickLabelFormatter;
  }

  /**
   * Automatically determines the maximum and minimum values on the axis to 'fit' the data.
   */
  public abstract void autoAdjustRange();

  /**
   * Converts a value from the data source to a Java2D user-space co-ordinate relative to the
   * specified plotArea.  The coordinate will be an x-value for horizontal axes and a y-value
   * for vertical axes (refer to the subclass).
   * <p>
   * Note that it is possible for the coordinate to fall outside the plotArea.  One problem that
   * this introduces is that Java2D does not clearly define the maximum or minimum supported
   * co-ordinates.
   * @param dataValue The data value;
   * @param plotArea The area within which the data is being plotted;
   */
  public abstract double translatedValue(Number dataValue, Rectangle2D plotArea);

  /**
   * Calculates and returns the value of the lowest visible tick on the axis.
   */
  public Number calculateLowestVisibleTickValue() {
    double m = minimumAxisValue.doubleValue();
    double u = getTickValue().doubleValue();
    double index = Math.ceil(m/u);
    return new Double(index*u);
  }

  /**
   * Calculates and returns the value of the highest visible tick on the axis.
   */
  public Number calculateHighestVisibleTickValue() {
    double m = maximumAxisValue.doubleValue();
    double u = getTickValue().doubleValue();
    double index = Math.floor(m/u);
    return new Double(index*u);
  }

  /**
   * Calculates and returns the number of visible ticks.
   */
  public int calculateVisibleTickCount() {
    double low = minimumAxisValue.doubleValue();
    double high = maximumAxisValue.doubleValue();
    double u = getTickValue().doubleValue();
    return (int)(Math.floor(high/u)-Math.ceil(low/u)+1);
  }

  /**
   * Scans the tickValues array to find the first entry that is greater than the current tick
   * value for the axis.  Records the index (in autoTickIndex) and sets the tickValue and
   * NumberFormat for the axis.
   */
  public void indexTickUnits() {
    int  j = 0;
    boolean found = false;
    for (j=0; j<autoTickValues.length; j++) {
      if (getTickValue().doubleValue()<autoTickValues[j].doubleValue()) {
        found = true;
        break;
      }
    }
    if (found) {
      autoTickIndex = j;
    }
    else {
      autoTickIndex = autoTickValues.length-1;
    }
    tickValue = autoTickValues[autoTickIndex];
    tickLabelFormatter.applyPattern(autoTickFormats[autoTickIndex]);
  }

  /**
   * An array of standard tick sizes used when automatically determining the tick value.  These
   * values can be changed to whatever you require, as long as: (1) the values remain in
   * ascending order; and (2) you update the corresponding array of standard tick formats (see
   * below).
   */
  protected static Number[] autoTickValues = {
    new Double(0.0000001),  new Double(0.00000025),   new Double(0.0000005),
    new Double(0.000001),   new Double(0.0000025),    new Double(0.000005),
    new Double(0.00001),    new Double(0.000025),     new Double(0.00005),
    new Double(0.0001),     new Double(0.00025),      new Double(0.0005),
    new Double(0.001),      new Double(0.0025),       new Double(0.005),
    new Double(0.01),       new Double(0.025),        new Double(0.05),
    new Double(0.1),        new Double(0.25),         new Double(0.5),
    new Long(1),            new Double(2.5),          new Long(5),
    new Long(10),           new Long(25),             new Long(50),
    new Long(100),          new Long(250),            new Long(500),
    new Long(1000),         new Long(2500),           new Long(5000),
    new Long(10000),        new Long(25000),          new Long(50000),
    new Long(100000),       new Long(250000),         new Long(500000),
    new Long(1000000),      new Long(2500000),        new Long(5000000),
    new Long(10000000),     new Long(25000000),       new Long(50000000),
    new Long(100000000),    new Long(250000000),      new Long(500000000),
    new Long(1000000000),   new Long(2500000000L),    new Long(5000000000L)
  };

  /**
   * An array of format strings, corresponding to the standardTickValues array, and used to create
   * a NumberFormat object for displaying tick values.
   */
  protected static String[] autoTickFormats = {
    "0.0000000",     "0.00000000",    "0.0000000",
    "0.000000",      "0.0000000",     "0.000000",
    "0.00000",       "0.000000",      "0.00000",
    "0.0000",        "0.00000",       "0.0000",
    "0.000",         "0.0000",        "0.000",
    "0.00",          "0.000",         "0.00",
    "0.0",           "0.00",          "0.0",
    "0",             "0.0",           "0",
    "0",             "0",             "0",
    "0",             "0",             "0",
    "#,##0",         "#,##0",         "#,##0",
    "#,##0",         "#,##0",         "#,##0",
    "#,##0",         "#,##0",         "#,##0",
    "#,###,##0",     "#,###,##0",     "#,###,##0",
    "#,###,##0",     "#,###,##0",     "#,###,##0",
    "#,###,##0",     "#,###,##0",     "#,###,##0",
    "#,###,###,##0", "#,###,###,##0", "#,###,###,##0"
  };

}
