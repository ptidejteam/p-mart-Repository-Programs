/* =======================================
 * JFreeChart : a Java Chart Class Library
 * =======================================
 *
 * Project Info:  http://www.jrefinery.com/jfreechart
 * Project Lead:  David Gilbert (david.gilbert@jrefinery.com);
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
 * -------------
 * DateAxis.java
 * -------------
 * (C) Copyright 2000-2002, by Simba Management Limited and Contributors.
 *
 * Original Author:  David Gilbert;
 * Contributor(s):   Jonathan Nash;
 *
 * $Id: DateAxis.java,v 1.1 2007/10/10 18:57:06 vauchers Exp $
 *
 * Changes (from 23-Jun-2001)
 * --------------------------
 * 23-Jun-2001 : Modified to work with null data source (DG);
 * 18-Sep-2001 : Updated header (DG);
 * 27-Nov-2001 : Changed constructors from public to protected, updated Javadoc comments (DG);
 * 16-Jan-2002 : Added an optional crosshair, based on the implementation by Jonathan Nash (DG);
 */

package com.jrefinery.chart;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.text.*;
import java.util.*;
import javax.swing.*;
import com.jrefinery.chart.event.*;

/**
 * The base class for axes that display java.util.Date values.
 * @see HorizontalDateAxis
 */
public abstract class DateAxis extends ValueAxis {

    public static final Date DEFAULT_CROSSHAIR_DATE = null;

    /** The earliest date showing on the axis. */
    protected Date minimumDate;

    /** The latest date showing on the axis. */
    protected Date maximumDate;

    protected Date anchorDate = new Date();
    protected Date crosshairDate;

    /** The current tick unit. */
    protected DateUnit tickUnit;

    /** A formatter for the tick labels. */
    protected SimpleDateFormat tickLabelFormatter;

    /**
     * Constructs a date axis, using default values where necessary.
     * @param label The axis label.
     */
    public DateAxis(String label) {

	this(label,
             Axis.DEFAULT_AXIS_LABEL_FONT,
             Axis.DEFAULT_AXIS_LABEL_PAINT,
             Axis.DEFAULT_AXIS_LABEL_INSETS,
             true, // tick labels visible
             Axis.DEFAULT_TICK_LABEL_FONT,
             Axis.DEFAULT_TICK_LABEL_PAINT,
             Axis.DEFAULT_TICK_LABEL_INSETS,
             true, // tick marks visible
             Axis.DEFAULT_TICK_STROKE,
             true, // auto range
             null, // minimum date
             null, // maximum date
             true, // auto tick unit selection
             new DateUnit(Calendar.DATE, 1),
             new SimpleDateFormat(),
             true, // grid lines visible
             ValueAxis.DEFAULT_GRID_LINE_STROKE,
             ValueAxis.DEFAULT_GRID_LINE_PAINT,
             DEFAULT_CROSSHAIR_DATE,
             ValueAxis.DEFAULT_CROSSHAIR_STROKE,
             ValueAxis.DEFAULT_CROSSHAIR_PAINT);

    }

    /**
     * Constructs a date axis.
     * @param label The axis label.
     * @param labelFont The font for displaying the axis label.
     * @param labelPaint The paint used to draw the axis label.
     * @param labelInsets Determines the amount of blank space around the label.
     * @param tickLabelsVisible Flag indicating whether or not tick labels are visible.
     * @param tickLabelFont The font used to display tick labels.
     * @param tickLabelPaint The paint used to draw tick labels.
     * @param tickLabelInsets Determines the amount of blank space around tick labels.
     * @param tickMarksVisible Flag indicating whether or not tick marks are visible.
     * @param tickMarkStroke The stroke used to draw tick marks (if visible).
     * @param autoRange Flag indicating whether or not the axis range is automatically adjusted to
     *                  fit the data.
     * @param minimumDate The earliest date showing on the axis.
     * @param maximumDate The latest date showing on the axis.
     * @param autoTickUnitSelection A flag indicating whether or not the tick unit is automatically
     *                              selected.
     * @param tickUnit The tick unit.
     * @param gridLinesVisible Flag indicating whether or not grid lines are visible.
     * @param gridStroke The Stroke used to display grid lines (if visible).
     * @param gridPaint The Paint used to display grid lines (if visible).
     */
    protected DateAxis(String label,
                       Font labelFont, Paint labelPaint, Insets labelInsets,
		       boolean tickLabelsVisible,
                       Font tickLabelFont, Paint tickLabelPaint, Insets tickLabelInsets,
		       boolean tickMarksVisible, Stroke tickMarkStroke,
		       boolean autoRange, Date minimumDate, Date maximumDate,
		       boolean autoTickUnitSelection, DateUnit tickUnit,
                       SimpleDateFormat tickLabelFormatter,
		       boolean gridLinesVisible, Stroke gridStroke, Paint gridPaint,
                       Date crosshairDate, Stroke crosshairStroke, Paint crosshairPaint) {

	super(label, labelFont, labelPaint, labelInsets,
	      tickLabelsVisible, tickLabelFont, tickLabelPaint, tickLabelInsets,
	      tickMarksVisible, tickMarkStroke, autoRange,
	      autoTickUnitSelection, gridLinesVisible, gridStroke, gridPaint,
              0.0,
              crosshairStroke, crosshairPaint);

	this.minimumDate = minimumDate;
        if (minimumDate!=null) {
            this.minimumAxisValue = (double)minimumDate.getTime();
        }
	this.maximumDate = maximumDate;
        if (maximumDate!=null) {
            this.maximumAxisValue = (double)maximumDate.getTime();
        }
        this.crosshairDate = crosshairDate;

	this.tickUnit = tickUnit;
	this.tickLabelFormatter = tickLabelFormatter;
        this.anchorValue = (double)this.anchorDate.getTime();

    }

    /**
     * Returns the earliest date visible on the axis.
     * @return The earliest date visible on the axis.
     */
    public Date getMinimumDate() {
	return this.minimumDate;
    }

    /**
     * Sets the minimum date visible on the axis.
     * @param minimumDate The new minimum date.
     */
    public void setMinimumDate(Date minimumDate) {

	this.minimumDate = minimumDate;
        double millis = (double)minimumDate.getTime();
        this.setMinimumAxisValue(millis);

    }

    /**
     * Returns the latest date visible on the axis.
     * @return The latest date visible on the axis.
     */
    public Date getMaximumDate() {
	return this.maximumDate;
    }

    /**
     * Sets the maximum date visible on the axis.
     * @param maximumDate The new maximum date.
     */
    public void setMaximumDate(Date maximumDate) {

	this.maximumDate = maximumDate;
        double millis = (double)maximumDate.getTime();
        this.setMaximumAxisValue(millis);

    }

    public void setMinimumAxisValue(double value) {
	long millis = (long)value;
        this.minimumDate.setTime(millis);
        super.setMinimumAxisValue(value);
    }

    public void setMaximumAxisValue(double value) {
	long millis = (long)value;
        this.maximumDate.setTime(millis);
        super.setMaximumAxisValue(value);
    }

    public void setAnchorValue(double value) {
	long millis = (long)value;
        this.anchorDate.setTime(millis);
        super.setAnchorValue(value);
    }

    public void setAxisRange(double lower, double upper) {

	long lowerMillis = (long)lower;
	long upperMillis = (long)upper;
        this.minimumDate.setTime(lowerMillis);
        this.maximumDate.setTime(upperMillis);
        super.setAxisRange(lower, upper);
    }

    /**
     * Returns the crosshair date for the axis.
     * @return The crosshair date for the axis (possibly null).
     */
    public Date getCrosshairDate() {
	return this.crosshairDate;
    }

    /**
     * Sets the crosshair date for the axis.
     * @param maximumDate The new crosshair date (null permitted).
     */
    public void setCrosshairDate(Date crosshairDate) {

	this.crosshairDate = crosshairDate;
        double millis = (double)crosshairDate.getTime();
        this.setCrosshairValue(millis);

    }

    /**
     * Returns the anchor date for the axis.
     * @return The anchor date for the axis (possibly null).
     */
    public Date getAnchorDate() {
	return this.anchorDate;
    }

    /**
     * Sets the anchor date for the axis.
     * @param anchorDate The new anchor date (null permitted).
     */
    public void setAnchorDate(Date anchorDate) {

	this.anchorDate = anchorDate;
        double millis = (double)anchorDate.getTime();
        super.setAnchorValue(millis);

    }

    /**
     * Returns the tick unit for the axis.
     * @return The tick unit for the axis.
     */
    public DateUnit getTickUnit() {
	return tickUnit;
    }

    /**
     * Sets the tick unit for the axis.
     * @param unit The new date unit.
     */
    public void setTickUnit(DateUnit unit) {

	this.tickUnit = unit;
	this.notifyListeners(new AxisChangeEvent(this));

    }

    /**
     * Returns the formatter for the tick labels.
     * @return The formatter for the tick labels.
     */
    public SimpleDateFormat getTickLabelFormatter() {
	return tickLabelFormatter;
    }

    /**
     * Calculates the value of the lowest visible tick on the axis.
     * @return The value of the lowest visible tick on the axis.
     */
    public Date calculateLowestVisibleTickValue(DateUnit unit) {
	return this.nextStandardDate(minimumDate, unit.getField(), unit.getCount());
    }

    /**
     * Calculates the value of the highest visible tick on the axis.
     * @return The value of the highest visible tick on the axis.
     */
    public Date calculateHighestVisibleTickValue(DateUnit unit) {
	return this.previousStandardDate(maximumDate, unit.getField(), unit.getCount());
    }

    /**
     * Returns the previous "standard" date (based on the specified field and units).
     */
    protected Date previousStandardDate(Date date, int field, int units) {
	int milliseconds;
	int seconds;
	int minutes;
	int hours;
	int days;
	int months;
	int years;
	Calendar calendar = Calendar.getInstance();
	calendar.setTime(date);
	int current = calendar.get(field);
	int value = units*(current/units);
	switch (field) {

	case(Calendar.MILLISECOND) : {
            years = calendar.get(Calendar.YEAR);
            months = calendar.get(Calendar.MONTH);
            days = calendar.get(Calendar.DATE);
            hours = calendar.get(Calendar.HOUR_OF_DAY);
            minutes = calendar.get(Calendar.MINUTE);
            seconds = calendar.get(Calendar.SECOND);
            calendar.set(years, months, days, hours, minutes, seconds);
            calendar.set(Calendar.MILLISECOND, value);
            return calendar.getTime(); 	}

	case(Calendar.SECOND)      : {
	    years = calendar.get(Calendar.YEAR);
	    months = calendar.get(Calendar.MONTH);
	    days = calendar.get(Calendar.DATE);
	    hours = calendar.get(Calendar.HOUR_OF_DAY);
	    minutes = calendar.get(Calendar.MINUTE);
	    calendar.clear(Calendar.MILLISECOND);
	    calendar.set(years, months, days, hours, minutes, value);
	    return calendar.getTime();
	}

	case(Calendar.MINUTE)      : {
	    years = calendar.get(Calendar.YEAR);
	    months = calendar.get(Calendar.MONTH);
	    days = calendar.get(Calendar.DATE);
	    hours = calendar.get(Calendar.HOUR_OF_DAY);
	    calendar.clear(Calendar.MILLISECOND);
	    calendar.set(years, months, days, hours, value, 0);
	    return calendar.getTime();
	}

	case(Calendar.HOUR_OF_DAY)        : {
	    years = calendar.get(Calendar.YEAR);
	    months = calendar.get(Calendar.MONTH);
	    days = calendar.get(Calendar.DATE);
	    calendar.clear(Calendar.MILLISECOND);
	    calendar.set(years, months, days, value, 0, 0);
	    return calendar.getTime();
	}

	case(Calendar.DATE)        : {
	    years = calendar.get(Calendar.YEAR);
	    months = calendar.get(Calendar.MONTH);
	    calendar.clear(Calendar.MILLISECOND);
	    calendar.set(years, months, value, 0, 0, 0);
	    return calendar.getTime();
	}

	case(Calendar.MONTH)       : {
	    years = calendar.get(Calendar.YEAR);
	    calendar.clear(Calendar.MILLISECOND);
	    calendar.set(years, value, 1, 0, 0, 0);
	    return calendar.getTime();
	}

	case(Calendar.YEAR)        : {
	    calendar.clear(Calendar.MILLISECOND);
	    calendar.set(value, 0, 1, 0, 0, 0);
	    return calendar.getTime();
	}

	default: return null;

	}
    }

    /**
     * Returns the first "standard" date (based on the specified field and units).
     */
    protected Date nextStandardDate(Date date, int field, int units) {
	Date previous = previousStandardDate(date, field, units);
	Calendar calendar = Calendar.getInstance();
	calendar.setTime(previous);
	calendar.add(field, units);
	return calendar.getTime();
    }

    /**
     * Returns the index of the largest tick unit that will fit within the axis range.
     */
    protected int findAxisMagnitudeIndex() {

	long axisMagnitude = this.maximumDate.getTime()-this.minimumDate.getTime();
	int index = 0;
	while(index<standardTickUnitMagnitudes.length-1) {
	    if (axisMagnitude<standardTickUnitMagnitudes[index]) break;
	    index++;
	}
	return Math.max(0, index-1);

     }

    /**
     * The approximate magnitude of each of the standard tick units.  This array is used to find
     * an index into the standardTickUnits array.
     */
    protected static long[] standardTickUnitMagnitudes = {
	1L, 5L, 10L, 50L, 100L, 500L,
	1000L, 5*1000L, 10*1000L, 30*1000L,
	60*1000L, 5*60*1000L, 10*60*1000L, 30*60*1000L,
	60*60*1000L, 6*60*60*1000L, 12*60*60*1000L,
	24*60*60*1000L, 7*24*60*60*1000L,
	30*24*60*60*1000L, 90*24*60*60*1000L, 180*24*60*60*1000L,
	365*24*60*60*1000L, 5*365*24*60*60*1000L, 10*365*24*60*60*1000L,
	25*365*24*60*60*1000L, 50*365*24*60*60*1000L, 100*365*24*60*60*1000L
    };

    /**
     * An array of Calendar fields that will be used for automatic tick generation.
     */
    protected static int[][] standardTickUnits = {
	{ Calendar.MILLISECOND, 1 },
	{ Calendar.MILLISECOND, 5 },
	{ Calendar.MILLISECOND, 10 },
	{ Calendar.MILLISECOND, 50 },
	{ Calendar.MILLISECOND, 100 },
	{ Calendar.MILLISECOND, 500 },
	{ Calendar.SECOND, 1 },
	{ Calendar.SECOND, 5 },
	{ Calendar.SECOND, 10 },
	{ Calendar.SECOND, 30 },
	{ Calendar.MINUTE, 1 },
	{ Calendar.MINUTE, 5 },
	{ Calendar.MINUTE, 10 },
	{ Calendar.MINUTE, 30 },
	{ Calendar.HOUR_OF_DAY, 1 },
	{ Calendar.HOUR_OF_DAY, 6 },
	{ Calendar.HOUR_OF_DAY, 12 },
	{ Calendar.DATE, 1 },
	{ Calendar.DATE, 7 },
	{ Calendar.MONTH, 1 },
	{ Calendar.MONTH, 3 },
	{ Calendar.MONTH, 6 },
	{ Calendar.YEAR, 1 },
	{ Calendar.YEAR, 5 },
	{ Calendar.YEAR, 10 },
	{ Calendar.YEAR, 25 },
	{ Calendar.YEAR, 50 },
	{ Calendar.YEAR, 100 }
    };

    /**
     * An array of strings, corresponding to the tickValues array, and used to create a
     * DateFormat object for displaying tick values.
     */
    protected static String[] standardTickFormats = {
	"HH:mm:ss.SSS",
	"HH:mm:ss.SSS",
	"HH:mm:ss.SSS",
	"HH:mm:ss.SSS",
	"HH:mm:ss.SSS",
	"HH:mm:ss.SSS",
	"HH:mm:ss",
	"HH:mm:ss",
	"HH:mm:ss",
	"HH:mm:ss",
	"HH:mm",
	"HH:mm",
	"HH:mm",
	"HH:mm",
	"HH:mm",
	"d-MMM, H:mm",
	"d-MMM, H:mm",
	"d-MMM-yyyy",
	"d-MMM-yyyy",
	"MMM-yyyy",
	"MMM-yyyy",
	"MMM-yyyy",
	"yyyy",
	"yyyy",
	"yyyy",
	"yyyy",
	"yyyy",
	"yyyy",
    };

}
