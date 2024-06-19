/* =======================================
 * JFreeChart : a Java Chart Class Library
 * =======================================
 *
 * Project Info:  http://www.jrefinery.com/jfreechart;
 * Project Lead:  David Gilbert (david.gilbert@jrefinery.com);
 *
 * This file...
 * $Id: NumberAxis.java,v 1.1 2007/10/10 18:53:20 vauchers Exp $
 *
 * Original Author:  David Gilbert;
 * Contributor(s):   Laurence Vanhelsuwe;
 *
 * (C) Copyright 2000, 2001, Simba Management Limited;
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
 * Changes (from 18-Sep-2001)
 * --------------------------
 * 18-Sep-2001 : Added standard header and fixed DOS encoding problem (DG);
 * 22-Sep-2001 : Changed setMinimumAxisValue(...) and setMaximumAxisValue(...) so that they
 *               clear the autoRange flag (DG);
 *
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

    /** The default minimum axis value. */
    public static final Number DEFAULT_MINIMUM_AXIS_VALUE = new Double(0.0);

    /** The default maximum axis value. */
    public static final Number DEFAULT_MAXIMUM_AXIS_VALUE = new Double(1.0);

    /** The default value for the upper margin. */
    public static final double DEFAULT_UPPER_MARGIN = 0.05;

    /** The default value for the lower margin. */
    public static final double DEFAULT_LOWER_MARGIN = 0.05;

    /** The default minimum auto range. */
    public static final Number DEFAULT_MINIMUM_AUTO_RANGE = new Double(0.0000001);

    /** The default tick unit. */
    public static final NumberTickUnit DEFAULT_TICK_UNIT
                                       = new NumberTickUnit(new Double(1.0),
                                                            new DecimalFormat("0"));

    /** The lowest value showing on the axis. */
    protected Number minimumAxisValue;

    /** The highest value showing on the axis. */
    protected Number maximumAxisValue;

    /** A flag that indicates whether or not zero *must* be included when automatically determining
     * the axis range. */
    protected boolean autoRangeIncludesZero;

    /** The minimum size of a range that is determined automatically. */
    protected Number autoRangeMinimumSize;

    /** The upper margin.  This is a percentage that indicates the amount by which the maximum
        axis value exceeds the maximum data value when the axis range is determined
        automatically. */
    protected double upperMargin;

    /** The lower margin.  This is a percentage that indicates the amount by which the minimum
        axis value is "less than" the minimum data value when the axis range is determined
        automatically. */
    protected double lowerMargin;

//    /** The current value (size) of one tick. */
//    protected Number tickValue;

//    /** A formatter for the tick labels. */
//    protected DecimalFormat tickLabelFormatter;

    /** The tick unit for the axis. */
    protected NumberTickUnit tickUnit;

    /** The standard tick units for the axis. */
    protected TickUnits standardTickUnits;

    /**
     * Constructs a number axis.
     * @param label The axis label.
     * @param labelFont The font for displaying the axis label.
     * @param labelPaint The paint used to display the axis label.
     * @param labelInsets The amount of blank space around the axis label.
     * @param tickLabelsVisible Flag indicating whether or not the tick labels are visible.
     * @param tickLabelFont The font used to display the tick labels.
     * @param tickLabelPaint The paint used to draw the tick labels.
     * @param tickLabelInsets The amount of blank space around the tick labels.
     * @param tickMarksVisible Flag indicating whether or not tick marks are visible;
     * @param tickMarkStroke The stroke used to draw the tick marks (if visible);
     * @param autoRange Flag indicating whether or not the axis range is automatically determined.
     * @param autoRangeIncludesZero A flag indicating whether the auto range must include zero.
     * @param autoRangeMinimumSize The minimum size for the auto range.
     * @param minimumAxisValue The lowest value shown on the axis.
     * @param maximumAxisValue The highest value shown on the axis.
     * @param autoTickUnitSelection A flag indicating whether or not the tick value is automatically
     *                              selected.
     * @param tickUnit The tick unit for the axis.
     * @param gridLinesVisible Flag indicating whether or not grid lines are visible.
     * @param gridStroke The pen/brush used to display grid lines (if visible).
     * @param gridPaint The color used to display grid lines (if visible).
     */
    protected NumberAxis(String label, Font labelFont, Paint labelPaint, Insets labelInsets,
		      boolean tickLabelsVisible, Font tickLabelFont, Paint tickLabelPaint,
                      Insets tickLabelInsets,
		      boolean tickMarksVisible, Stroke tickMarkStroke,
		      boolean autoRange, boolean autoRangeIncludesZero, Number autoRangeMinimumSize,
		      Number minimumAxisValue, Number maximumAxisValue,
		      boolean autoTickUnitSelection, NumberTickUnit tickUnit,
                      /* Number tickValue, DecimalFormat tickLabelFormatter, */
		      boolean gridLinesVisible, Stroke gridStroke, Paint gridPaint) {

	super(label,
              labelFont, labelPaint, labelInsets,
	      tickLabelsVisible,
              tickLabelFont, tickLabelPaint, tickLabelInsets,
	      tickMarksVisible, tickMarkStroke,
              autoRange, autoTickUnitSelection,
              gridLinesVisible, gridStroke, gridPaint);

        // check arguments...
        if (minimumAxisValue.doubleValue()>=maximumAxisValue.doubleValue()) {
            throw new IllegalArgumentException("NumberAxis(...): minimum axis value must be less "
                                               +"than maximum axis value.");
        }

        if (!autoRange) {
            if (minimumAxisValue == null) {
                throw new IllegalArgumentException("NumberAxis(...): minimum axis value must be  "
                                                   +"specified if auto range calculation is off.");

            }
            if (maximumAxisValue == null) {
                throw new IllegalArgumentException("NumberAxis(...): maximum axis value must be  "
                                                   +"specified if auto range calculation is off.");

            }
        }

        if (autoRangeMinimumSize==null) {
            throw new IllegalArgumentException("NumberAxis(...): autoRangeMinimum cannot be null.");
        }

        // do the initialisation...
	this.autoRangeIncludesZero = autoRangeIncludesZero;
	this.autoRangeMinimumSize = autoRangeMinimumSize;

	this.minimumAxisValue = minimumAxisValue;
	this.maximumAxisValue = maximumAxisValue;

	this.tickUnit = tickUnit;

        this.upperMargin = DEFAULT_UPPER_MARGIN;
        this.lowerMargin = DEFAULT_LOWER_MARGIN;

        this.standardTickUnits = createStandardTickUnits();

    }

    /**
     * Constructs a number axis, using default values where necessary.
     * @param label The axis label.
     */
    protected NumberAxis(String label) {

	this(label,
             Axis.DEFAULT_AXIS_LABEL_FONT,
             Axis.DEFAULT_AXIS_LABEL_PAINT,
             new Insets(2, 2, 2, 2), // label insets
             true, // tick labels visible
             Axis.DEFAULT_TICK_LABEL_FONT,
             Axis.DEFAULT_TICK_LABEL_PAINT,
             new Insets(1, 1, 1, 1), // tick label insets
             true, // tick marks visible
             Axis.DEFAULT_TICK_STROKE,
             true, // auto range
             true, // auto range includes zero
             NumberAxis.DEFAULT_MINIMUM_AUTO_RANGE,
             NumberAxis.DEFAULT_MINIMUM_AXIS_VALUE,
             NumberAxis.DEFAULT_MAXIMUM_AXIS_VALUE,
             true, // auto tick unit
             NumberAxis.DEFAULT_TICK_UNIT,
             true, // grid lines visible
             ValueAxis.DEFAULT_GRID_LINE_STROKE,
             ValueAxis.DEFAULT_GRID_LINE_PAINT);

    }

    /**
     * Returns the minimum value for the axis.
     * @return The minimum value for the axis.
     */
    public Number getMinimumAxisValue() {
	return minimumAxisValue;
    }

    /**
     * Sets the minimum value for the axis.
     * <P>
     * Registered listeners are notified that the axis has been modified.
     * @param value The new minimum.
     */
    public void setMinimumAxisValue(Number value) {

        // check argument...
        if (value==null) {
            throw new IllegalArgumentException("NumberAxis.setMinimumAxisValue(Number): "
                                               +"null not permitted.");
        }

        // make the change...
	if (!value.equals(this.minimumAxisValue)) {
	    this.minimumAxisValue = value;
            this.autoRange = false;
            notifyListeners(new AxisChangeEvent(this));
        }

    }

    /**
     * Returns the maximum value for the axis.
     */
    public Number getMaximumAxisValue() {
	return maximumAxisValue;
    }

    /**
     * Sets the maximum value for the axis.
     * <P>
     * Registered listeners are notified that the axis has been modified.
     * @param value The new maximum.
     */
    public void setMaximumAxisValue(Number value) {

        // check argument...
        if (value==null) {
            throw new IllegalArgumentException("NumberAxis.setMinimumAxisValue(Number): "
                                               +"null not permitted.");
        }

        // make the change...
	if (!value.equals(this.maximumAxisValue)) {
	    this.maximumAxisValue = value;
            this.autoRange = false;
            notifyListeners(new AxisChangeEvent(this));
        }

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
    public Number getAutoRangeMinimumSize() {
	return this.autoRangeMinimumSize;
    }

    /**
     * Sets the minimum size of the automatic axis range.
     * @param minimum The new minimum.
     */
    public void setAutoRangeMinimumSize(Number size) {

        // check argument...
        if (size==null) {
            throw new IllegalArgumentException("NumberAxis.setAutoRangeMinimumSize(Number): "
                                               +"null not permitted.");
        }

        // make the change...
	if (autoRangeMinimumSize.doubleValue()!=size.doubleValue()) {
	    this.autoRangeMinimumSize = size;
	    notifyListeners(new AxisChangeEvent(this));
	}

    }

    /**
     * Returns the margin (as a percentage of the range) by which the maximum axis value exceeds
     * the maximum data value.
     */
    public double getUpperMargin() {
        return this.upperMargin;
    }

    /**
     * Sets the upper margin.
     * @param margin The new margin;
     */
    public void setUpperMargin(double margin) {
        this.upperMargin = margin;
        notifyListeners(new AxisChangeEvent(this));
    }

    /**
     * Returns the margin (as a percentage of the range) by which the minimum axis value is less
     * than the minimum data value.
     */
    public double getLowerMargin() {
        return this.lowerMargin;
    }

    /**
     * Sets the lower margin.
     * @param margin The new margin;
     */
    public void setLowerMargin(double margin) {
        this.lowerMargin = margin;
        notifyListeners(new AxisChangeEvent(this));
    }

    public NumberTickUnit getTickUnit() {
        return this.tickUnit;
    }

    /**
     * Converts a value to a string, using the current format for the tick labels on the axis.
     */
    public String valueToString(Number value) {

        // is there an override format?
        return this.tickUnit.formatter.format(value.doubleValue());

    }

//    /**
//     * Returns the current tick value (or size) for the axis.
//     */
//    public Number getTickValue() {
//	return tickValue;
//    }

//    /**
//     * Sets the tick value (or size) and notifies registered listeners that the axis has been
//     * modified.
//     * @param value The new tick value or size;
//     */
//    public void setTickValue(Number value) {
//	this.tickValue = value;
//	notifyListeners(new AxisChangeEvent(this));
//    }

//    /**
//     * Returns the DecimalFormat object used to format tick labels.
//     */
//    public DecimalFormat getTickLabelFormatter() {
//	return tickLabelFormatter;
//    }

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
     * Calculates the value of the lowest visible tick on the axis.
     * @return The value of the lowest visible tick on the axis.
     */
    public double calculateLowestVisibleTickValue() {

	double min = minimumAxisValue.doubleValue();
	double unit = getTickUnit().getValue().doubleValue();
	double index = Math.ceil(min/unit);
	return index*unit;

    }

    /**
     * Calculates the value of the highest visible tick on the axis.
     * @return The value of the highest visible tick on the axis.
     */
    public double calculateHighestVisibleTickValue() {

	double max = maximumAxisValue.doubleValue();
	double unit = getTickUnit().getValue().doubleValue();
	double index = Math.floor(max/unit);
	return index*unit;

    }

    /**
     * Calculates the number of visible ticks.
     * @return The number of visible ticks on the axis.
     */
    public int calculateVisibleTickCount() {

	double low = minimumAxisValue.doubleValue();
	double high = maximumAxisValue.doubleValue();
	double unit = getTickUnit().getValue().doubleValue();
	return (int)(Math.floor(high/unit)-Math.ceil(low/unit)+1);

    }

    /**
     * Matches the current tick unit to a STANDARD tick unit.
     * <P>
     * Three things are updated:  (1) the tickValue, (2) the autoTickIndex and (3) the tick label
     * format.
     */
//    protected void matchTickUnitToStandardUnit() {
//
//	int j = 0;
//	boolean found = false;
//	for (j=0; j<autoTickValues.length; j++) {
//	    if (getTickUnit().getValue().doubleValue()<autoTickValues[j].doubleValue()) {
//		found = true;
//		break;
//	    }
//	}
//	if (found) {
//	    autoTickIndex = j;
//	}
//	else {
//	    autoTickIndex = autoTickValues.length-1;
//	}
//
//	tickValue = autoTickValues[autoTickIndex];
//	tickLabelFormatter.applyPattern(autoTickFormats[autoTickIndex]);
//
//    }

    private TickUnits createStandardTickUnits() {

        TickUnits units = new TickUnits();

        units.add(new NumberTickUnit(new Double(0.0000001),  new DecimalFormat("0.0000000")));
        units.add(new NumberTickUnit(new Double(0.000001),   new DecimalFormat("0.000000")));
        units.add(new NumberTickUnit(new Double(0.00001),    new DecimalFormat("0.00000")));
        units.add(new NumberTickUnit(new Double(0.0001),     new DecimalFormat("0.0000")));
        units.add(new NumberTickUnit(new Double(0.001),      new DecimalFormat("0.000")));
        units.add(new NumberTickUnit(new Double(0.01),       new DecimalFormat("0.00")));
        units.add(new NumberTickUnit(new Double(0.1),        new DecimalFormat("0.0")));
        units.add(new NumberTickUnit(new Long(1L),           new DecimalFormat("0")));
        units.add(new NumberTickUnit(new Long(10L),          new DecimalFormat("0")));
        units.add(new NumberTickUnit(new Long(100L),         new DecimalFormat("0")));
        units.add(new NumberTickUnit(new Long(1000L),        new DecimalFormat("#,##0")));
        units.add(new NumberTickUnit(new Long(10000L),       new DecimalFormat("#,##0")));
        units.add(new NumberTickUnit(new Long(100000L),      new DecimalFormat("#,##0")));
        units.add(new NumberTickUnit(new Long(1000000L),     new DecimalFormat("#,###,##0")));
        units.add(new NumberTickUnit(new Long(10000000L),    new DecimalFormat("#,###,##0")));
        units.add(new NumberTickUnit(new Long(100000000L),   new DecimalFormat("#,###,##0")));
        units.add(new NumberTickUnit(new Long(1000000000L),  new DecimalFormat("#,###,###,##0")));

        units.add(new NumberTickUnit(new Double(0.00000025), new DecimalFormat("0.00000000")));
        units.add(new NumberTickUnit(new Double(0.0000025),  new DecimalFormat("0.0000000")));
        units.add(new NumberTickUnit(new Double(0.000025),   new DecimalFormat("0.000000")));
        units.add(new NumberTickUnit(new Double(0.00025),    new DecimalFormat("0.00000")));
        units.add(new NumberTickUnit(new Double(0.0025),     new DecimalFormat("0.0000")));
        units.add(new NumberTickUnit(new Double(0.025),      new DecimalFormat("0.000")));
        units.add(new NumberTickUnit(new Double(0.25),       new DecimalFormat("0.00")));
        units.add(new NumberTickUnit(new Double(2.5),        new DecimalFormat("0.0")));
        units.add(new NumberTickUnit(new Long(25L),          new DecimalFormat("0")));
        units.add(new NumberTickUnit(new Long(250L),         new DecimalFormat("0")));
        units.add(new NumberTickUnit(new Long(2500L),        new DecimalFormat("#,##0")));
        units.add(new NumberTickUnit(new Long(25000L),       new DecimalFormat("#,##0")));
        units.add(new NumberTickUnit(new Long(250000L),      new DecimalFormat("#,##0")));
        units.add(new NumberTickUnit(new Long(2500000L),     new DecimalFormat("#,###,##0")));
        units.add(new NumberTickUnit(new Long(25000000L),    new DecimalFormat("#,###,##0")));
        units.add(new NumberTickUnit(new Long(250000000L),   new DecimalFormat("#,###,##0")));
        units.add(new NumberTickUnit(new Long(2500000000L),  new DecimalFormat("#,###,###,##0")));

        units.add(new NumberTickUnit(new Double(0.0000005),  new DecimalFormat("0.0000000")));
        units.add(new NumberTickUnit(new Double(0.000005),   new DecimalFormat("0.000000")));
        units.add(new NumberTickUnit(new Double(0.00005),    new DecimalFormat("0.00000")));
        units.add(new NumberTickUnit(new Double(0.0005),     new DecimalFormat("0.0000")));
        units.add(new NumberTickUnit(new Double(0.005),      new DecimalFormat("0.000")));
        units.add(new NumberTickUnit(new Double(0.05),       new DecimalFormat("0.00")));
        units.add(new NumberTickUnit(new Double(0.5),        new DecimalFormat("0.0")));
        units.add(new NumberTickUnit(new Long(5L),           new DecimalFormat("0")));
        units.add(new NumberTickUnit(new Long(50L),          new DecimalFormat("0")));
        units.add(new NumberTickUnit(new Long(500L),         new DecimalFormat("0")));
        units.add(new NumberTickUnit(new Long(5000L),        new DecimalFormat("#,##0")));
        units.add(new NumberTickUnit(new Long(50000L),       new DecimalFormat("#,##0")));
        units.add(new NumberTickUnit(new Long(500000L),      new DecimalFormat("#,##0")));
        units.add(new NumberTickUnit(new Long(5000000L),     new DecimalFormat("#,###,##0")));
        units.add(new NumberTickUnit(new Long(50000000L),    new DecimalFormat("#,###,##0")));
        units.add(new NumberTickUnit(new Long(500000000L),   new DecimalFormat("#,###,##0")));
        units.add(new NumberTickUnit(new Long(5000000000L),  new DecimalFormat("#,###,###,##0")));

        return units;

    }

//    /**
//     * An array of standard tick sizes used when automatically determining the tick value.  These
//     * values can be changed to whatever you require, as long as: (1) the values remain in
//     * ascending order; and (2) you update the corresponding array of standard tick formats (see
//     * below).
//     */
//    protected static Number[] autoTickValues = {
//	new Double(0.0000001),  new Double(0.00000025),   new Double(0.0000005),
//	new Double(0.000001),   new Double(0.0000025),    new Double(0.000005),
//	new Double(0.00001),    new Double(0.000025),     new Double(0.00005),
//	new Double(0.0001),     new Double(0.00025),      new Double(0.0005),
//	new Double(0.001),      new Double(0.0025),       new Double(0.005),
//	new Double(0.01),       new Double(0.025),        new Double(0.05),
//	new Double(0.1),        new Double(0.25),         new Double(0.5),
//	new Long(1),            new Double(2.5),          new Long(5),
//	new Long(10),           new Long(25),             new Long(50),
//	new Long(100),          new Long(250),            new Long(500),
//	new Long(1000),         new Long(2500),           new Long(5000),
//	new Long(10000),        new Long(25000),          new Long(50000),
//	new Long(100000),       new Long(250000),         new Long(500000),
//	new Long(1000000),      new Long(2500000),        new Long(5000000),
//	new Long(10000000),     new Long(25000000),       new Long(50000000),
//	new Long(100000000),    new Long(250000000),      new Long(500000000),
//	new Long(1000000000),   new Long(2500000000L),    new Long(5000000000L)
  //  };

    /**
     * An array of format strings, corresponding to the standardTickValues array, and used to create
     * a NumberFormat object for displaying tick values.
     */
  //  protected static String[] autoTickFormats = {
//	"0.0000000",     "0.00000000",    "0.0000000",
//	"0.000000",      "0.0000000",     "0.000000",
//	"0.00000",       "0.000000",      "0.00000",
//	"0.0000",        "0.00000",       "0.0000",
//	"0.000",         "0.0000",        "0.000",
//	"0.00",          "0.000",         "0.00",
//	"0.0",           "0.00",          "0.0",
//	"0",             "0.0",           "0",
//	"0",             "0",             "0",
//	"0",             "0",             "0",
//	"#,##0",         "#,##0",         "#,##0",
//	"#,##0",         "#,##0",         "#,##0",
//	"#,##0",         "#,##0",         "#,##0",
//	"#,###,##0",     "#,###,##0",     "#,###,##0",
///	"#,###,##0",     "#,###,##0",     "#,###,##0",
//	"#,###,##0",     "#,###,##0",     "#,###,##0",
//	"#,###,###,##0", "#,###,###,##0", "#,###,###,##0"
//    };

}
