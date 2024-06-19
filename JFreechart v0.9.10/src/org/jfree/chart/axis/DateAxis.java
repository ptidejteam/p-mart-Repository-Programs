/* ======================================
 * JFreeChart : a free Java chart library
 * ======================================
 *
 * Project Info:  http://www.jfree.org/jfreechart/index.html
 * Project Lead:  David Gilbert (david.gilbert@object-refinery.com);
 *
 * (C) Copyright 2000-2003, by Object Refinery Limited and Contributors.
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
 * (C) Copyright 2000-2003, by Object Refinery Limited and Contributors.
 *
 * Original Author:  David Gilbert;
 * Contributor(s):   Jonathan Nash;
 *                   David Li;
 *                   Michael Rauch;
 *                   Bill Kelemen;
 *                   Pawel Pabis;
 *
 * $Id: DateAxis.java,v 1.1 2007/10/10 19:05:11 vauchers Exp $
 *
 * Changes (from 23-Jun-2001)
 * --------------------------
 * 23-Jun-2001 : Modified to work with null data source (DG);
 * 18-Sep-2001 : Updated header (DG);
 * 27-Nov-2001 : Changed constructors from public to protected, updated Javadoc comments (DG);
 * 16-Jan-2002 : Added an optional crosshair, based on the implementation by Jonathan Nash (DG);
 * 26-Feb-2002 : Updated import statements (DG);
 * 22-Apr-2002 : Added a setRange() method (DG);
 * 25-Jun-2002 : Removed redundant local variable (DG);
 * 25-Jul-2002 : Changed order of parameters in ValueAxis constructor (DG);
 * 21-Aug-2002 : The setTickUnit(...) method now turns off auto-tick unit selection (fix for
 *               bug id 528885) (DG);
 * 05-Sep-2002 : Updated the constructors to reflect changes in the Axis class (DG);
 * 18-Sep-2002 : Fixed errors reported by Checkstyle (DG);
 * 25-Sep-2002 : Added new setRange(...) methods, and deprecated setAxisRange(...) (DG);
 * 04-Oct-2002 : Changed auto tick selection to parallel number axis classes (DG);
 * 24-Oct-2002 : Added a date format override (DG);
 * 08-Nov-2002 : Moved to new package com.jrefinery.chart.axis (DG);
 * 14-Jan-2003 : Changed autoRangeMinimumSize from Number --> double, moved crosshair settings
 *               to the plot (DG);
 * 15-Jan-2003 : Removed anchor date (DG);
 * 20-Jan-2003 : Removed unnecessary constructors (DG);
 * 26-Mar-2003 : Implemented Serializable (DG);
 * 02-May-2003 : Added additional units to createStandardDateTickUnits() method, as suggested
 *               by mhilpert in bug report 723187 (DG);
 * 13-May-2003 : Merged HorizontalDateAxis and VerticalDateAxis (DG);
 * 24-May-2003 : Added support for underlying timeline for SegmentedTimeline (BK);
 * 16-Jul-2003 : Applied patch from Pawel Pabis to fix overlapping dates (DG);
 * 22-Jul-2003 : Applied patch from Pawel Pabis for monthly ticks (DG);
 *
 */

package org.jfree.chart.axis;

import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.font.FontRenderContext;
import java.awt.font.LineMetrics;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;

import org.jfree.chart.event.AxisChangeEvent;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.plot.ValueAxisPlot;
import org.jfree.data.DateRange;
import org.jfree.data.Range;
import org.jfree.ui.RectangleEdge;
import org.jfree.ui.RefineryUtilities;

/**
 * The base class for axes that display dates.
 * <P>
 * You will find it easier to understand how this axis works if you bear in mind that it really
 * displays/measures integer (or long) data, where the integers are milliseconds since midnight,
 * 1-Jan-1970.  When displaying tick labels, the millisecond values are converted back to dates
 * using a <code>DateFormat</code> instance.
 * <P>
 * You can also create a {@link org.jfree.chart.axis.Timeline} and supply in the
 * constructor to create an axis that only contains certain domain values. For example,
 * this allows you to create a Date axis that only contains working days.
 *
 * @author David Gilbert
 * @author Bill Kelemen
 */
public class DateAxis extends ValueAxis implements Serializable {

    /** The default axis range. */
    public static final DateRange DEFAULT_DATE_RANGE = new DateRange();

    /** The default minimum auto range size. */
    public static final double DEFAULT_AUTO_RANGE_MINIMUM_SIZE_IN_MILLISECONDS = 2.0;

    /** The default date tick unit. */
    public static final DateTickUnit DEFAULT_DATE_TICK_UNIT
        = new DateTickUnit(DateTickUnit.DAY, 1, new SimpleDateFormat());

    /** The default anchor date. */
    public static final Date DEFAULT_ANCHOR_DATE = new Date();

    /** The current tick unit. */
    private DateTickUnit tickUnit;

    /** The override date format. */
    private DateFormat dateFormatOverride;

//    /**
//     * The anchor date (needs to be synchronised with the anchorValue in the
//     * {@link ValueAxis} superclass, as this form is maintained for convenience only).
//     */
//    private Date anchorDate;
//
    /** Tick marks can be displayed at the start or the middle of the time period. */
    private DateTickMarkPosition tickMarkPosition = DateTickMarkPosition.MIDDLE;

    /** A timeline class of all Dates to use as default if none defined. */
    private static class DefaultTimeline implements Timeline, Serializable {

        /**
         * Converts a domain value into a timeline value.
         *
         * @param domainValue  the domain value.
         *
         * @return The timeline value.
         */
        public long toTimelineValue(long domainValue) {
            return (domainValue);
        }

        /**
         * Converts a domain value into a timeline value.
         *
         * @param domainValue  the domain value.
         *
         * @return The timeline value.
         */
        public long toTimelineValue(Date domainValue) {
            return (domainValue.getTime());
        }

        /**
         * Converts a timeline value into a domain value.
         *
         * @param value  the value.
         *
         * @return The domain value.
         */
        public long toDomainValue(long value) {
            return (value);
        }

        /**
         * Returns <code>true</code> if the timeline includes the specified domain value.
         *
         * @param value  the value.
         *
         * @return <code>true</code>.
         */
        public boolean containsDomainValue(long value) {
            return (true);
        }

        /**
         * Returns <code>true</code> if the timeline includes the specified domain value.
         *
         * @param date  the date.
         *
         * @return <code>true</code>.
         */
        public boolean containsDomainValue(Date date) {
            return (true);
        }

        /**
         * Returns <code>true</code> if the timeline includes the specified domain value range.
         *
         * @param from  the start value.
         * @param to  the end value.
         *
         * @return <code>true</code>.
         */
        public boolean containsDomainRange(long from, long to) {
            return (true);
        }

        /**
         * Returns <code>true</code> if the timeline includes the specified domain value range.
         *
         * @param from  the start date.
         * @param to  the end date.
         *
         * @return <code>true</code>.
         */
        public boolean containsDomainRange(Date from, Date to) {
            return (true);
        }
    };

    /** A static default timeline shared by all standard DateAxis */
    private static final Timeline DEFAULT_TIMELINE = new DefaultTimeline();

    /** Our underlying timeline. */
    private Timeline timeline;

    /**
     * Default constructor.
     */
    public DateAxis() {
        this(null);    
    }
    
    /**
     * Creates a date axis.
     *
     * @param label  the axis label (<code>null</code> permitted).
     */
    public DateAxis(String label) {
        this(label, DEFAULT_TIMELINE);
    }

    /**
     * Creates a date axis. A timeline is specified for the axis. This allows special
     * transformations to occure between a domain of values and the values included
     * in the axis.
     *
     * @see org.jfree.chart.axis.SegmentedTimeline
     *
     * @param label  the axis label (<code>null</code> permitted).
     * @param timeline  the underlying timeline to use for the axis.
     */
    public DateAxis(String label, Timeline timeline) {

        super(label, DateAxis.createStandardDateTickUnits());

        setTickUnit(DateAxis.DEFAULT_DATE_TICK_UNIT, false, false);
       // setAnchorDate(DateAxis.DEFAULT_ANCHOR_DATE);
        setAutoRangeMinimumSize(DEFAULT_AUTO_RANGE_MINIMUM_SIZE_IN_MILLISECONDS);
        setRange(DEFAULT_DATE_RANGE, false, false);
        this.dateFormatOverride = null;
        this.timeline = timeline;

    }

    /**
     * Returns the underlying timeline used by this axis.
     *
     * @return The timeline.
     */
    public Timeline getTimeline() {
        return (timeline);
    }

    /**
     * Sets the underlying timeline to use for this axis.
     * <P>
     * If the timeline is changed, an {@link AxisChangeEvent} is sent to all
     * registered listeners.\
     *
     * @param timeline The timeline to set.
     */
    public void setTimeline(Timeline timeline) {
        if (this.timeline != timeline) {
            this.timeline = timeline;
            notifyListeners(new AxisChangeEvent(this));
        }
    }

    /**
     * Returns the tick unit for the axis.
     *
     * @return the tick unit for the axis.
     */
    public DateTickUnit getTickUnit() {
        return tickUnit;
    }

    /**
     * Sets the tick unit for the axis.  The auto-tick-unit-selection flag is set to
     * <code>false</code>, and registered listeners are notified that the axis has been changed.
     *
     * @param unit  the new tick unit.
     */
    public void setTickUnit(DateTickUnit unit) {
        setTickUnit(unit, true, true);
    }

    /**
     * Sets the tick unit attribute without any other side effects.
     *
     * @param unit  the new tick unit.
     * @param notify  notify registered listeners?
     * @param turnOffAutoSelection  turn off auto selection?
     */
    public void setTickUnit(DateTickUnit unit, boolean notify, boolean turnOffAutoSelection) {

        this.tickUnit = unit;
        if (turnOffAutoSelection) {
            setAutoTickUnitSelection(false, false);
        }
        if (notify) {
            notifyListeners(new AxisChangeEvent(this));
        }

    }

    /**
     * Returns the date format override.  If this is non-null, then it will be used to format
     * the dates on the axis.
     *
     * @return the date format override.
     */
    public DateFormat getDateFormatOverride() {
        return this.dateFormatOverride;
    }

    /**
     * Sets the date format override.  If this is non-null, then it will be used to format
     * the dates on the axis.
     *
     * @param formatter  the date formatter (<code>null</code> permitted).
     */
    public void setDateFormatOverride(DateFormat formatter) {
        this.dateFormatOverride = formatter;
        notifyListeners(new AxisChangeEvent(this));
    }

    /**
     * Sets the upper and lower bounds for the axis.  An {@link AxisChangeEvent} is sent to all
     * registered listeners.  As a side-effect, the auto-range flag is set to false.
     *
     * @param range  the new range.
     */
    public void setRange(Range range) {

        // check arguments...
        if (range == null) {
            throw new IllegalArgumentException("DateAxis.setRange(...): null not permitted.");
        }

        // usually the range will be a DateRange, but if it isn't do a conversion...
        if (!(range instanceof DateRange)) {
            range = new DateRange(range);
        }

        setRange(range, true, true);
        notifyListeners(new AxisChangeEvent(this));

    }

    /**
     * Sets the axis range.  An {@link AxisChangeEvent} is sent to all registered listeners.
     *
     * @param lower  the lower bound for the axis.
     * @param upper  the upper bound for the axis.
     */
    public void setRange(Date lower, Date upper) {

        // check arguments...
        if (lower.getTime() >= upper.getTime()) {
            throw new IllegalArgumentException("DateAxis.setRange(...): lower not before upper.");
        }

        // make the change...
        setRange(new DateRange(lower, upper));

    }

    /**
     * Sets the axis range.  An {@link AxisChangeEvent} is sent to all registered listeners.
     *
     * @param lower  the lower bound for the axis.
     * @param upper  the upper bound for the axis.
     */
    public void setRange(double lower, double upper) {

        // check arguments...
        if (lower >= upper) {
            throw new IllegalArgumentException("DateAxis.setRange(...): lower >= upper.");
        }

        // make the change...
        setRange(new DateRange(lower, upper));

    }

    /**
     * Returns the earliest date visible on the axis.
     *
     * @return the earliest date visible on the axis.
     */
    public Date getMinimumDate() {

        Date result = null;

        Range range = getRange();
        if (range instanceof DateRange) {
            DateRange r = (DateRange) range;
            result = r.getLowerDate();
        }
        else {
            result = new Date((long) range.getLowerBound());
        }

        return result;

    }

    /**
     * Sets the minimum date visible on the axis.  An {@link AxisChangeEvent} is sent to all
     * registered listeners.
     *
     * @param minimumDate  the new minimum date.
     */
    public void setMinimumDate(Date minimumDate) {

        setRange(new DateRange(minimumDate, getMaximumDate()), true, false);
        notifyListeners(new AxisChangeEvent(this));

    }

    /**
     * Returns the latest date visible on the axis.
     *
     * @return the latest date visible on the axis.
     */
    public Date getMaximumDate() {

        Date result = null;

        Range range = getRange();
        if (range instanceof DateRange) {
            DateRange r = (DateRange) range;
            result = r.getUpperDate();
        }
        else {
            result = new Date((long) range.getUpperBound());
        }

        return result;

    }

    /**
     * Sets the maximum date visible on the axis.  An {@link AxisChangeEvent} is sent to all
     * registered listeners.
     *
     * @param maximumDate  the new maximum date.
     */
    public void setMaximumDate(Date maximumDate) {

        setRange(new DateRange(getMinimumDate(), maximumDate), true, false);
        notifyListeners(new AxisChangeEvent(this));

    }

    /**
     * Returns the tick mark position (start, middle or end of the time period).
     *
     * @return The position.
     */
    public DateTickMarkPosition getTickMarkPosition() {
        return this.tickMarkPosition;
    }

    /**
     * Sets the tick mark position (start, middle or end of the time period).  An
     * {@link AxisChangeEvent} is sent to all registered listeners.
     *
     * @param position  the new position.
     */
    public void setTickMarkPosition(DateTickMarkPosition position) {
        this.tickMarkPosition = position;
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
     * Translates the data value to the display coordinates (Java 2D User Space)
     * of the chart.
     *
     * @param value  the date to be plotted.
     * @param dataArea  the rectangle (in Java2D space) where the data is to be plotted.
     * @param edge  the axis location.
     *
     * @return the coordinate corresponding to the supplied data value.
     */
    public double translateValueToJava2D(double value, Rectangle2D dataArea,
                                         RectangleEdge edge) {

        value = timeline.toTimelineValue((long) value);

        DateRange range = (DateRange) getRange();
        double axisMin = timeline.toTimelineValue(range.getLowerDate());
        double axisMax = timeline.toTimelineValue(range.getUpperDate());

        double result = 0.0;
        if (RectangleEdge.isTopOrBottom(edge)) {
            double minX = dataArea.getX();
            double maxX = dataArea.getMaxX();
            if (isInverted()) {
                result = maxX + ((value - axisMin) / (axisMax - axisMin)) * (minX - maxX);
            }
            else {
                result = minX + ((value - axisMin) / (axisMax - axisMin)) * (maxX - minX);
            }
        }
        else if (RectangleEdge.isLeftOrRight(edge)) {
            double minY = dataArea.getMinY();
            double maxY = dataArea.getMaxY();
            if (isInverted()) {
                result = minY + (((value - axisMin) / (axisMax - axisMin)) * (maxY - minY));
            }
            else {
                result = maxY - (((value - axisMin) / (axisMax - axisMin)) * (maxY - minY));
            }
        }
        return result;

    }

    /**
     * Translates a date to Java2D coordinates, based on the range displayed by
     * this axis for the specified data area.
     *
     * @param date  the date.
     * @param dataArea  the rectangle (in Java2D space) where the data is to be plotted.
     * @param edge  the axis location.
     *
     * @return the coordinate corresponding to the supplied date.
     */
    public double translateDateToJava2D(Date date, Rectangle2D dataArea, RectangleEdge edge) {

        double value = (double) date.getTime();
        return translateValueToJava2D(value, dataArea, edge);

    }

    /**
     * Translates the Java2D (vertical) coordinate back to the corresponding
     * data value.
     *
     * @param java2DValue  the coordinate in Java2D space.
     * @param dataArea  the rectangle (in Java2D space) where the data is to be plotted.
     * @param edge  the axis location.
     *
     * @return the data value corresponding to the Java2D coordinate.
     */
    public double translateJava2DtoValue(float java2DValue,
                                         Rectangle2D dataArea, RectangleEdge edge) {

        DateRange range = (DateRange) getRange();
        double axisMin = timeline.toTimelineValue(range.getLowerDate());
        double axisMax = timeline.toTimelineValue(range.getUpperDate());

        double min = 0.0;
        double max = 0.0;
        if (RectangleEdge.isTopOrBottom(edge)) {
            min = dataArea.getX();
            max = dataArea.getMaxX();
        }
        else if (RectangleEdge.isLeftOrRight(edge)) {
            min = dataArea.getMaxY();
            max = dataArea.getY();
        }

        double result;
        if (isInverted()) {
             result = axisMax - ((java2DValue - min) / (max - min) * (axisMax - axisMin));
        }
        else {
             result = axisMin + ((java2DValue - min) / (max - min) * (axisMax - axisMin));
        }

        result = timeline.toTimelineValue((long) result);
        return (result);
    }

    /**
     * Calculates the value of the lowest visible tick on the axis.
     *
     * @param unit  date unit to use.
     *
     * @return The value of the lowest visible tick on the axis.
     */
    public Date calculateLowestVisibleTickValue(DateTickUnit unit) {

        return nextStandardDate(getMinimumDate(), unit);

    }

    /**
     * Calculates the value of the highest visible tick on the axis.
     *
     * @param unit  date unit to use.
     *
     * @return the value of the highest visible tick on the axis.
     */
    public Date calculateHighestVisibleTickValue(DateTickUnit unit) {

        return previousStandardDate(getMaximumDate(), unit);

    }

    /**
     * Returns the previous "standard" date, for a given date and tick unit.
     *
     * @param date  the reference date.
     * @param unit  the tick unit.
     *
     * @return the previous "standard" date.
     */
    protected Date previousStandardDate(Date date, DateTickUnit unit) {

        int milliseconds;
        int seconds;
        int minutes;
        int hours;
        int days;
        int months;
        int years;

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int count = unit.getCount();
        int current = calendar.get(unit.getCalendarField());
        int value = count * (current / count);

        switch (unit.getUnit()) {

            case (DateTickUnit.MILLISECOND) :
                years = calendar.get(Calendar.YEAR);
                months = calendar.get(Calendar.MONTH);
                days = calendar.get(Calendar.DATE);
                hours = calendar.get(Calendar.HOUR_OF_DAY);
                minutes = calendar.get(Calendar.MINUTE);
                seconds = calendar.get(Calendar.SECOND);
                calendar.set(years, months, days, hours, minutes, seconds);
                calendar.set(Calendar.MILLISECOND, value);
                return calendar.getTime();

            case (DateTickUnit.SECOND) :
                years = calendar.get(Calendar.YEAR);
                months = calendar.get(Calendar.MONTH);
                days = calendar.get(Calendar.DATE);
                hours = calendar.get(Calendar.HOUR_OF_DAY);
                minutes = calendar.get(Calendar.MINUTE);
                if (this.tickMarkPosition == DateTickMarkPosition.START) {
                    milliseconds = 0;
                }
                else if (this.tickMarkPosition == DateTickMarkPosition.MIDDLE) {
                    milliseconds = 500;
                }
                else {
                    milliseconds = 999;
                }
                calendar.set(Calendar.MILLISECOND, milliseconds);
                calendar.set(years, months, days, hours, minutes, value);
                return calendar.getTime();

            case (DateTickUnit.MINUTE) :
                years = calendar.get(Calendar.YEAR);
                months = calendar.get(Calendar.MONTH);
                days = calendar.get(Calendar.DATE);
                hours = calendar.get(Calendar.HOUR_OF_DAY);
                if (this.tickMarkPosition == DateTickMarkPosition.START) {
                    seconds = 0;
                }
                else if (this.tickMarkPosition == DateTickMarkPosition.MIDDLE) {
                    seconds = 30;
                }
                else {
                    seconds = 59;
                }
                calendar.clear(Calendar.MILLISECOND);
                calendar.set(years, months, days, hours, value, seconds);
                return calendar.getTime();

            case (DateTickUnit.HOUR) :
                years = calendar.get(Calendar.YEAR);
                months = calendar.get(Calendar.MONTH);
                days = calendar.get(Calendar.DATE);
                if (this.tickMarkPosition == DateTickMarkPosition.START) {
                    minutes = 0;
                    seconds = 0;
                }
                else if (this.tickMarkPosition == DateTickMarkPosition.MIDDLE) {
                    minutes = 30;
                    seconds = 0;
                }
                else {
                    minutes = 59;
                    seconds = 59;
                }
                calendar.clear(Calendar.MILLISECOND);
                calendar.set(years, months, days, value, minutes, seconds);
                return calendar.getTime();

            case (DateTickUnit.DAY) :
                years = calendar.get(Calendar.YEAR);
                months = calendar.get(Calendar.MONTH);
                if (this.tickMarkPosition == DateTickMarkPosition.START) {
                    hours = 0;
                    minutes = 0;
                    seconds = 0;
                }
                else if (this.tickMarkPosition == DateTickMarkPosition.MIDDLE) {
                    hours = 12;
                    minutes = 0;
                    seconds = 0;
                }
                else {
                    hours = 23;
                    minutes = 59;
                    seconds = 59;
                }
                calendar.clear(Calendar.MILLISECOND);
                calendar.set(years, months, value, hours, 0, 0);
                return calendar.getTime();

            case (DateTickUnit.MONTH) :
                years = calendar.get(Calendar.YEAR);
                if (this.tickMarkPosition == DateTickMarkPosition.START) {
                    days = 1;
                }
                else if (this.tickMarkPosition == DateTickMarkPosition.MIDDLE) {
                    days = 15;
                }
                else {
                    days = 30;
                }
                calendar.clear(Calendar.MILLISECOND);
                calendar.set(years, value, days, 0, 0, 0);
                return calendar.getTime();

            case(DateTickUnit.YEAR) :
                if (this.tickMarkPosition == DateTickMarkPosition.START) {
                    months = 1;
                }
                else if (this.tickMarkPosition == DateTickMarkPosition.MIDDLE) {
                    months = 7;
                }
                else {
                    months = 12;
                }
                calendar.clear(Calendar.MILLISECOND);
                calendar.set(value, 0, months, 0, 0, 0);
                return calendar.getTime();

            default: return null;

        }

    }

    /**
     * Returns the first "standard" date (based on the specified field and units).
     *
     * @param date  the reference date.
     * @param unit  the date tick unit.
     *
     * @return the next "standard" date.
     */
    protected Date nextStandardDate(Date date, DateTickUnit unit) {

        Date previous = previousStandardDate(date, unit);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(previous);
        calendar.add(unit.getCalendarField(), unit.getCount());
        return calendar.getTime();

    }

    /**
     * Returns a collection of standard date tick units.  This collection will be used by default,
     * but you are free to create your own collection if you want to (see the
     * setStandardTickUnits(...) method inherited from the ValueAxis class).
     *
     * @return a collection of standard date tick units.
     */
    public static TickUnits createStandardDateTickUnits() {

        TickUnits units = new TickUnits();

        // milliseconds
        units.add(new DateTickUnit(DateTickUnit.MILLISECOND, 1,
                                   new SimpleDateFormat("HH:mm:ss.SSS")));
        units.add(new DateTickUnit(DateTickUnit.MILLISECOND, 5,
                                   new SimpleDateFormat("HH:mm:ss.SSS")));
        units.add(new DateTickUnit(DateTickUnit.MILLISECOND, 10,
                                   new SimpleDateFormat("HH:mm:ss.SSS")));
        units.add(new DateTickUnit(DateTickUnit.MILLISECOND, 25,
                                   new SimpleDateFormat("HH:mm:ss.SSS")));
        units.add(new DateTickUnit(DateTickUnit.MILLISECOND, 50,
                                   new SimpleDateFormat("HH:mm:ss.SSS")));
        units.add(new DateTickUnit(DateTickUnit.MILLISECOND, 100,
                                   new SimpleDateFormat("HH:mm:ss.SSS")));
        units.add(new DateTickUnit(DateTickUnit.MILLISECOND, 250,
                                   new SimpleDateFormat("HH:mm:ss.SSS")));
        units.add(new DateTickUnit(DateTickUnit.MILLISECOND, 500,
                                   new SimpleDateFormat("HH:mm:ss.SSS")));

        // seconds
        units.add(new DateTickUnit(DateTickUnit.SECOND, 1, new SimpleDateFormat("HH:mm:ss")));
        units.add(new DateTickUnit(DateTickUnit.SECOND, 5, new SimpleDateFormat("HH:mm:ss")));
        units.add(new DateTickUnit(DateTickUnit.SECOND, 10, new SimpleDateFormat("HH:mm:ss")));
        units.add(new DateTickUnit(DateTickUnit.SECOND, 30, new SimpleDateFormat("HH:mm:ss")));

        // minutes
        units.add(new DateTickUnit(DateTickUnit.MINUTE, 1, new SimpleDateFormat("HH:mm")));
        units.add(new DateTickUnit(DateTickUnit.MINUTE, 2, new SimpleDateFormat("HH:mm")));
        units.add(new DateTickUnit(DateTickUnit.MINUTE, 5, new SimpleDateFormat("HH:mm")));
        units.add(new DateTickUnit(DateTickUnit.MINUTE, 10, new SimpleDateFormat("HH:mm")));
        units.add(new DateTickUnit(DateTickUnit.MINUTE, 15, new SimpleDateFormat("HH:mm")));
        units.add(new DateTickUnit(DateTickUnit.MINUTE, 20, new SimpleDateFormat("HH:mm")));
        units.add(new DateTickUnit(DateTickUnit.MINUTE, 30, new SimpleDateFormat("HH:mm")));

        // hours
        units.add(new DateTickUnit(DateTickUnit.HOUR, 1, new SimpleDateFormat("HH:mm")));
        units.add(new DateTickUnit(DateTickUnit.HOUR, 2, new SimpleDateFormat("HH:mm")));
        units.add(new DateTickUnit(DateTickUnit.HOUR, 4, new SimpleDateFormat("HH:mm")));
        units.add(new DateTickUnit(DateTickUnit.HOUR, 6, new SimpleDateFormat("HH:mm")));
        units.add(new DateTickUnit(DateTickUnit.HOUR, 12, new SimpleDateFormat("d-MMM, HH:mm")));

        // days
        units.add(new DateTickUnit(DateTickUnit.DAY, 1, new SimpleDateFormat("d-MMM")));
        units.add(new DateTickUnit(DateTickUnit.DAY, 2, new SimpleDateFormat("d-MMM")));
        units.add(new DateTickUnit(DateTickUnit.DAY, 7, new SimpleDateFormat("d-MMM")));
        units.add(new DateTickUnit(DateTickUnit.DAY, 15, new SimpleDateFormat("d-MMM")));

        // months
        units.add(new DateTickUnit(DateTickUnit.MONTH, 1, new SimpleDateFormat("MMM-yyyy")));
        units.add(new DateTickUnit(DateTickUnit.MONTH, 2, new SimpleDateFormat("MMM-yyyy")));
        units.add(new DateTickUnit(DateTickUnit.MONTH, 3, new SimpleDateFormat("MMM-yyyy")));
        units.add(new DateTickUnit(DateTickUnit.MONTH, 4, new SimpleDateFormat("MMM-yyyy")));
        units.add(new DateTickUnit(DateTickUnit.MONTH, 6, new SimpleDateFormat("MMM-yyyy")));

        // years
        units.add(new DateTickUnit(DateTickUnit.YEAR, 1, new SimpleDateFormat("yyyy")));
        units.add(new DateTickUnit(DateTickUnit.YEAR, 2, new SimpleDateFormat("yyyy")));
        units.add(new DateTickUnit(DateTickUnit.YEAR, 5, new SimpleDateFormat("yyyy")));
        units.add(new DateTickUnit(DateTickUnit.YEAR, 10, new SimpleDateFormat("yyyy")));
        units.add(new DateTickUnit(DateTickUnit.YEAR, 25, new SimpleDateFormat("yyyy")));
        units.add(new DateTickUnit(DateTickUnit.YEAR, 50, new SimpleDateFormat("yyyy")));
        units.add(new DateTickUnit(DateTickUnit.YEAR, 100, new SimpleDateFormat("yyyy")));

        return units;

    }

    /**
     * Rescales the axis to ensure that all data is visible.
     */
    protected void autoAdjustRange() {

        Plot plot = getPlot();

        if (plot == null) {
            return;  // no plot, no data
        }

        if (plot instanceof ValueAxisPlot) {
            ValueAxisPlot vap = (ValueAxisPlot) plot;

            Range r = vap.getDataRange(this);
            if (r == null) {
                r = new DateRange();
            }

            long upper = timeline.toTimelineValue((long) r.getUpperBound());
            long lower;
            long fixedAutoRange = (long) getFixedAutoRange();
            if (fixedAutoRange > 0.0) {
                lower = upper - fixedAutoRange;
            }
            else {
                lower = timeline.toTimelineValue((long) r.getLowerBound());
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

            upper = timeline.toDomainValue(upper);
            lower = timeline.toDomainValue(lower);

            setRange(new DateRange(new Date(lower), new Date(upper)), false, false);
        }

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
        return (plot instanceof ValueAxisPlot);
    }

    /**
     * Returns the space required to draw the axis.
     *
     * @param g2  the graphics device.
     * @param plot  the plot that the axis belongs to.
     * @param plotArea  the area within which the plot should be drawn.
     * @param edge  the axis location (top or bottom).
     * @param space  the space already reserved.
     *
     * @return The space.
     */
    public AxisSpace reserveSpace(Graphics2D g2, Plot plot, Rectangle2D plotArea, 
                                  RectangleEdge edge, AxisSpace space) {

        // create a new space object if one wasn't supplied...
        if (space == null) {
            space = new AxisSpace();
        }
        this.reservedForAxisLabel = 0.0;
        this.reservedForTickLabels = 0.0;
        
        // if the axis is not visible, no additional space is required...
        if (!isVisible()) {
            return space;
        }

        // if the axis has a fixed dimension, return it...
        double dimension = getFixedDimension();
        if (dimension > 0.0) {
            space.ensureAtLeast(dimension, edge);
        }

        // calculate the max size of the tick labels (if visible)...
        double tickLabelHeight = 0.0;
        double tickLabelWidth = 0.0;
        if (isTickLabelsVisible()) {
            g2.setFont(getTickLabelFont());
            refreshTicks(g2, 0.0, plotArea, plotArea, edge);
            Insets tickLabelInsets = getTickLabelInsets();
            if (RectangleEdge.isTopOrBottom(edge)) {
                tickLabelHeight = tickLabelInsets.top + tickLabelInsets.bottom;
                tickLabelHeight += getMaxTickLabelHeight(g2, plotArea, isVerticalTickLabels());
                this.reservedForTickLabels = tickLabelHeight;
            }
            else if (RectangleEdge.isLeftOrRight(edge)) {
                tickLabelWidth = tickLabelInsets.left + tickLabelInsets.right;
                tickLabelWidth += getMaxTickLabelWidth(g2, plotArea);
                this.reservedForTickLabels = tickLabelWidth;
            }
        }

        // get the axis label size and update the space object...
        Rectangle2D labelEnclosure = getLabelEnclosure(g2, edge);
        double labelHeight = 0.0;
        double labelWidth = 0.0;
        if (RectangleEdge.isTopOrBottom(edge)) {
            labelHeight = labelEnclosure.getHeight();
            space.ensureAtLeast(labelHeight + tickLabelHeight, edge);
        }
        else if (RectangleEdge.isLeftOrRight(edge)) {
            labelWidth = labelEnclosure.getWidth();
            space.ensureAtLeast(labelWidth + tickLabelWidth, edge);
        }

        return space;

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
     * Selects an appropriate tick value for the axis.  The strategy is to
     * display as many ticks as possible (selected from an array of 'standard'
     * tick units) without the labels overlapping.
     *
     * @param g2  the graphics device.
     * @param drawArea  the area in which the plot and axes should be drawn.
     * @param dataArea  the area defined by the axes.
     * @param edge  the axis location.
     */
    protected void selectAutoTickUnit(Graphics2D g2, Rectangle2D drawArea, Rectangle2D dataArea,
                                      RectangleEdge edge) {

        if (RectangleEdge.isTopOrBottom(edge)) {
            selectHorizontalAutoTickUnit(g2, drawArea, dataArea, edge);
        }
        else if (RectangleEdge.isLeftOrRight(edge)) {
            selectVerticalAutoTickUnit(g2, drawArea, dataArea, edge);
        }

    }

    /**
     * Selects an appropriate tick value for the axis.  The strategy is to
     * display as many ticks as possible (selected from an array of 'standard'
     * tick units) without the labels overlapping.
     *
     * @param g2  the graphics device.
     * @param drawArea  the area in which the plot and axes should be drawn.
     * @param dataArea  the area defined by the axes.
     * @param edge  the axis location.
     */
    protected void selectHorizontalAutoTickUnit(Graphics2D g2, Rectangle2D drawArea,
                                                Rectangle2D dataArea, RectangleEdge edge) {

        double zero = translateValueToJava2D(0.0, dataArea, edge);
        double tickLabelWidth = estimateMaximumTickLabelWidth(g2, getTickUnit());

        // start with the current tick unit...
        TickUnits tickUnits = getStandardTickUnits();
        TickUnit unit1 = tickUnits.getCeilingTickUnit(getTickUnit());
        double x1 = translateValueToJava2D(unit1.getSize(), dataArea, edge);
        double unit1Width = Math.abs(x1 - zero);

        // then extrapolate...
        double guess = (tickLabelWidth / unit1Width) * unit1.getSize();

        DateTickUnit unit2 = (DateTickUnit) tickUnits.getCeilingTickUnit(guess);
        double x2 = translateValueToJava2D(unit2.getSize(), dataArea, edge);
        double unit2Width = Math.abs(x2 - zero);

        tickLabelWidth = estimateMaximumTickLabelWidth(g2, unit2);
        if (tickLabelWidth > unit2Width) {
            unit2 = (DateTickUnit) tickUnits.getLargerTickUnit(unit2);
        }

        setTickUnit(unit2, false, false);

    }

    /**
     * Selects an appropriate tick value for the axis.  The strategy is to
     * display as many ticks as possible (selected from an array of 'standard'
     * tick units) without the labels overlapping.
     *
     * @param g2  the graphics device.
     * @param drawArea  the area in which the plot and axes should be drawn.
     * @param plotArea  the area in which the plot should be drawn.
     * @param edge  the axis location.
     */
    protected void selectVerticalAutoTickUnit(Graphics2D g2, Rectangle2D drawArea,
                                              Rectangle2D plotArea, RectangleEdge edge) {

        // calculate the tick label height...
        FontRenderContext frc = g2.getFontRenderContext();
        double tickLabelHeight = getTickLabelFont().getLineMetrics("123",
            frc).getHeight() + getTickLabelInsets().top + getTickLabelInsets().bottom;

        // now find the smallest tick unit that will accommodate the labels...
        double zero = translateValueToJava2D(0.0, plotArea, edge);

        // start with the current tick unit...
        TickUnits tickUnits = getStandardTickUnits();
        DateTickUnit candidate1 = (DateTickUnit) tickUnits.getCeilingTickUnit(getTickUnit());
        double y = translateValueToJava2D(candidate1.getSize(), plotArea, edge);
        double unitHeight = Math.abs(y - zero);

        // then extrapolate...
        int bestguess = (int) ((tickLabelHeight / unitHeight) * candidate1.getSize());
        TickUnit guess = new NumberTickUnit(bestguess, null);
        DateTickUnit candidate2 = (DateTickUnit) tickUnits.getCeilingTickUnit(guess);

        setTickUnit(candidate2, false, false);
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
        if (isVerticalTickLabels()) {
            // all tick labels have the same width (equal to the height of the font)...
            result += tickLabelFont.getStringBounds("1-Jan-2002", frc).getHeight();
        }
        else {
            // look at lower and upper bounds...
            DateRange range = (DateRange) getRange();
            Date lower = range.getLowerDate();
            Date upper = range.getUpperDate();
            String lowerStr = null;
            String upperStr = null;
            DateFormat formatter = getDateFormatOverride();
            if (formatter != null) {
                lowerStr = formatter.format(lower);
                upperStr = formatter.format(upper);
            }
            else {
                lowerStr = tickUnit.dateToString(lower);
                upperStr = tickUnit.dateToString(upper);
            }
            double w1 = tickLabelFont.getStringBounds(lowerStr, frc).getWidth();
            double w2 = tickLabelFont.getStringBounds(upperStr, frc).getWidth();
            result += Math.max(w1, w2);
        }

        return result;

    }

    /**
     * Calculates the positions of the tick labels for the axis, storing the results in the
     * tick label list (ready for drawing).
     *
     * @param g2  the graphics device.
     * @param cursor  the cursor location.
     * @param plotArea  the area in which the plot and the axes should be drawn.
     * @param dataArea  the area in which the plot should be drawn.
     * @param edge  the location of the axis.
     *
     */
    public void refreshTicks(Graphics2D g2, double cursor,
                             Rectangle2D plotArea, Rectangle2D dataArea,
                             RectangleEdge edge) {

        if (RectangleEdge.isTopOrBottom(edge)) {
            refreshTicksHorizontal(g2, cursor, plotArea, dataArea, edge);
        }
        else if (RectangleEdge.isLeftOrRight(edge)) {
            refreshTicksVertical(g2, cursor, plotArea, dataArea, edge);
        }

    }

    /**
     * Recalculates the ticks for the date axis.
     *
     * @param g2  the graphics device.
     * @param cursor  the cursor location.
     * @param plotArea  the area in which the axes and data are to be drawn.
     * @param dataArea  the area in which the data is to be drawn.
     * @param edge  the location of the axis.
     *
     */
    public void refreshTicksHorizontal(Graphics2D g2, double cursor,
                                       Rectangle2D plotArea, Rectangle2D dataArea,
                                       RectangleEdge edge) {

        getTicks().clear();

        Font tickLabelFont = getTickLabelFont();
        g2.setFont(tickLabelFont);

        if (isAutoTickUnitSelection()) {
            selectAutoTickUnit(g2, plotArea, dataArea, edge);
        }

        DateTickUnit unit = getTickUnit();
        Date tickDate = calculateLowestVisibleTickValue(unit);
        Date upperDate = getMaximumDate();
        float lastX = Float.MIN_VALUE;
        while (tickDate.before(upperDate)) {
            // work out the value, label and position
            double xx = translateDateToJava2D(tickDate, dataArea, edge);
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
            if (isVerticalTickLabels()) {
                x = (float) (xx + tickLabelBounds.getHeight() / 2 - metrics.getDescent());
                if (edge == RectangleEdge.TOP) {
                    y = (float) (cursor - tickLabelInsets.bottom - tickLabelBounds.getWidth());
                }
                else {
                    y = (float) (cursor + tickLabelInsets.top + tickLabelBounds.getWidth());
                }
            }
            else {
                x = (float) (xx - tickLabelBounds.getWidth() / 2);
                if (edge == RectangleEdge.TOP) {
                    y = (float) (cursor - tickLabelInsets.bottom);
                }
                else {
                    y = (float) (cursor + tickLabelInsets.top + tickLabelBounds.getHeight());
                }
            }

            // Prevent overwriting a label on top of a previous one. This can occure if we're
            // using a SegmentedTimeline as more than one Date can map to the same timeline value.
            if (x > lastX) {
                Tick tick = new Tick(tickDate, tickLabel, x, y);
                getTicks().add(tick);
                if (isVerticalTickLabels()) {
                    lastX = x + (float) tickLabelBounds.getHeight();
                } 
                else {
                    lastX = x + (float) tickLabelBounds.getWidth();
                }
            }

            tickDate = unit.addToDate(tickDate);
        }

    }

    /**
     * Recalculates the ticks for the date axis.
     *
     * @param g2  the graphics device.
     * @param cursor  the cursor location.
     * @param plotArea  the area in which the plot and the axes should be drawn.
     * @param dataArea  the area in which the plot should be drawn.
     * @param edge  the location of the axis.
     *
     */
    public void refreshTicksVertical(Graphics2D g2, double cursor,
                             Rectangle2D plotArea, Rectangle2D dataArea,
                             RectangleEdge edge) {

        getTicks().clear();

        Font tickLabelFont = getTickLabelFont();
        g2.setFont(tickLabelFont);

        FontRenderContext frc = g2.getFontRenderContext();
        if (isAutoTickUnitSelection()) {
            selectAutoTickUnit(g2, plotArea, dataArea, edge);
        }
        Rectangle2D labelBounds = null;
        DateTickUnit unit = getTickUnit();
        Date tickDate = calculateLowestVisibleTickValue(unit);
        Date upperDate = calculateHighestVisibleTickValue(unit);
        while (tickDate.before(upperDate)) {

            // work out the value, label and position
            double yy = translateDateToJava2D(tickDate, dataArea, edge);
            String tickLabel = tickUnit.dateToString(tickDate);
            labelBounds = tickLabelFont.getStringBounds(tickLabel, g2.getFontRenderContext());
            LineMetrics metrics = tickLabelFont.getLineMetrics(tickLabel, frc);
            float x;
            if (edge == RectangleEdge.LEFT) {
                x = (float) (cursor - labelBounds.getWidth() - getTickLabelInsets().right);
            }
            else {
                x = (float) (cursor + getTickLabelInsets().left);
            }
            float y = (float) (yy + (metrics.getAscent() / 2));
            Tick tick = new Tick(tickDate, tickLabel, x, y);
            getTicks().add(tick);
            tickDate = unit.addToDate(tickDate);
        }
    }

    /**
     * Draws the axis on a Java 2D graphics device (such as the screen or a printer).
     *
     * @param g2  the graphics device.
     * @param cursor  the cursor location.
     * @param plotArea  the area within which the axes and data should be drawn.
     * @param dataArea  the area within which the data should be drawn.
     * @param edge  the location of the axis.
     * 
     * @return The new cursor location.
     */
    public double draw(Graphics2D g2, double cursor,
                       Rectangle2D plotArea, Rectangle2D dataArea, RectangleEdge edge) {

        // if the axis is not visible, don't draw it...
        if (!isVisible()) {
            return 0.0;
        }

        // draw the tick marks and labels...
        double used1 = drawTickMarksAndLabels(g2, cursor, plotArea, dataArea, edge);
        if (edge == RectangleEdge.TOP || edge == RectangleEdge.LEFT) {
            cursor = cursor - used1;
        }
        else if (edge == RectangleEdge.BOTTOM || edge == RectangleEdge.RIGHT) {
            cursor = cursor + used1;
        }

        // draw the axis label...
        double used2 = drawLabel(getLabel(), g2, cursor, plotArea, dataArea, edge);

        return used1 + used2;

    }

    /**
     * Draws the axis line, tick marks and tick mark labels.
     * 
     * @param g2  the graphics device.
     * @param cursor  the cursor.
     * @param plotArea  the plot area.
     * @param dataArea  the data area.
     * @param edge  the edge that the axis is aligned with.
     * 
     * @return The width or height used to draw the axis.
     */
    protected double drawTickMarksAndLabels(Graphics2D g2, double cursor,
                                            Rectangle2D plotArea,
                                            Rectangle2D dataArea, RectangleEdge edge) {
                                              
        if (isAxisLineVisible()) {
            drawAxisLine(g2, cursor, dataArea, edge);
        }

        double ol = getTickMarkOutsideLength();
        double il = getTickMarkInsideLength();

        refreshTicks(g2, cursor, plotArea, dataArea, edge);
        g2.setFont(getTickLabelFont());
        Iterator iterator = getTicks().iterator();
        while (iterator.hasNext()) {
            Tick tick = (Tick) iterator.next();
            float xx = (float) translateValueToJava2D(tick.getNumericalValue(), dataArea, edge);
            if (isTickLabelsVisible()) {
                g2.setPaint(getTickLabelPaint());
                if (isVerticalTickLabels()) {
                    RefineryUtilities.drawRotatedString(tick.getText(), g2,
                                                        tick.getX(), tick.getY(), -Math.PI / 2);
                }
                else {
                    g2.drawString(tick.getText(), tick.getX(), tick.getY());
                }
            }

            if (isTickMarksVisible()) {
                Line2D mark = null;
                g2.setStroke(getTickMarkStroke());
                g2.setPaint(getTickMarkPaint());
                if (edge == RectangleEdge.LEFT) {
                    mark = new Line2D.Double(cursor - ol, xx, cursor + il, xx);
                }
                else if (edge == RectangleEdge.RIGHT) {
                    mark = new Line2D.Double(cursor + ol, xx, cursor - il, xx);
                }
                else if (edge == RectangleEdge.TOP) {
                    mark = new Line2D.Double(xx, cursor - ol, xx, cursor + il);
                }
                else if (edge == RectangleEdge.BOTTOM) {
                    mark = new Line2D.Double(xx, cursor + ol, xx, cursor - il);
                }
                g2.draw(mark);
            }
        }
        return this.reservedForTickLabels;
    }
}
