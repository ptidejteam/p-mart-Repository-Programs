/* ===========================================================
 * JFreeChart : a free chart library for the Java(tm) platform
 * ===========================================================
 *
 * (C) Copyright 2000-2005, by Object Refinery Limited and Contributors.
 *
 * Project Info:  http://www.jfree.org/jfreechart/index.html
 *
 * This library is free software; you can redistribute it and/or modify it 
 * under the terms of the GNU Lesser General Public License as published by 
 * the Free Software Foundation; either version 2.1 of the License, or 
 * (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but 
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY 
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public 
 * License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, 
 * USA.  
 *
 * [Java is a trademark or registered trademark of Sun Microsystems, Inc. 
 * in the United States and other countries.]
 *
 * -------------
 * DateAxis.java
 * -------------
 * (C) Copyright 2000-2005, by Object Refinery Limited and Contributors.
 *
 * Original Author:  David Gilbert;
 * Contributor(s):   Jonathan Nash;
 *                   David Li;
 *                   Michael Rauch;
 *                   Bill Kelemen;
 *                   Pawel Pabis;
 *
 * $Id: DateAxis.java,v 1.1 2007/10/10 20:11:29 vauchers Exp $
 *
 * Changes (from 23-Jun-2001)
 * --------------------------
 * 23-Jun-2001 : Modified to work with null data source (DG);
 * 18-Sep-2001 : Updated header (DG);
 * 27-Nov-2001 : Changed constructors from public to protected, updated Javadoc 
 *               comments (DG);
 * 16-Jan-2002 : Added an optional crosshair, based on the implementation by 
 *               Jonathan Nash (DG);
 * 26-Feb-2002 : Updated import statements (DG);
 * 22-Apr-2002 : Added a setRange() method (DG);
 * 25-Jun-2002 : Removed redundant local variable (DG);
 * 25-Jul-2002 : Changed order of parameters in ValueAxis constructor (DG);
 * 21-Aug-2002 : The setTickUnit() method now turns off auto-tick unit 
 *               selection (fix for bug id 528885) (DG);
 * 05-Sep-2002 : Updated the constructors to reflect changes in the Axis 
 *               class (DG);
 * 18-Sep-2002 : Fixed errors reported by Checkstyle (DG);
 * 25-Sep-2002 : Added new setRange() methods, and deprecated 
 *               setAxisRange() (DG);
 * 04-Oct-2002 : Changed auto tick selection to parallel number axis 
 *               classes (DG);
 * 24-Oct-2002 : Added a date format override (DG);
 * 08-Nov-2002 : Moved to new package com.jrefinery.chart.axis (DG);
 * 14-Jan-2003 : Changed autoRangeMinimumSize from Number --> double, moved
 *               crosshair settings to the plot (DG);
 * 15-Jan-2003 : Removed anchor date (DG);
 * 20-Jan-2003 : Removed unnecessary constructors (DG);
 * 26-Mar-2003 : Implemented Serializable (DG);
 * 02-May-2003 : Added additional units to createStandardDateTickUnits() 
 *               method, as suggested by mhilpert in bug report 723187 (DG);
 * 13-May-2003 : Merged HorizontalDateAxis and VerticalDateAxis (DG);
 * 24-May-2003 : Added support for underlying timeline for 
 *               SegmentedTimeline (BK);
 * 16-Jul-2003 : Applied patch from Pawel Pabis to fix overlapping dates (DG);
 * 22-Jul-2003 : Applied patch from Pawel Pabis for monthly ticks (DG);
 * 25-Jul-2003 : Fixed bug 777561 and 777586 (DG);
 * 13-Aug-2003 : Implemented Cloneable and added equals() method (DG);
 * 02-Sep-2003 : Fixes for bug report 790506 (DG);
 * 04-Sep-2003 : Fixed tick label alignment when axis appears at the top (DG);
 * 10-Sep-2003 : Fixes for segmented timeline (DG);
 * 17-Sep-2003 : Fixed a layout bug when multiple domain axes are used (DG);
 * 29-Oct-2003 : Added workaround for font alignment in PDF output (DG);
 * 07-Nov-2003 : Modified to use new tick classes (DG);
 * 12-Nov-2003 : Modified tick labelling to use roll unit from DateTickUnit 
 *               when a calculated tick value is hidden (which can occur in 
 *               segmented date axes) (DG);
 * 24-Nov-2003 : Fixed some problems with the auto tick unit selection, and 
 *               fixed bug 846277 (labels missing for inverted axis) (DG);
 * 30-Dec-2003 : Fixed bug in refreshTicksHorizontal() when start of time unit 
 *               (ex. 1st of month) was hidden, causing infinite loop (BK);
 * 13-Jan-2004 : Fixed bug in previousStandardDate() method (fix by Richard 
 *               Wardle) (DG);
 * 21-Jan-2004 : Renamed translateJava2DToValue --> java2DToValue, and 
 *               translateValueToJava2D --> valueToJava2D (DG); 
 * 12-Mar-2004 : Fixed bug where date format override is ignored for vertical 
 *               axis (DG);
 * 16-Mar-2004 : Added plotState to draw() method (DG);
 * 07-Apr-2004 : Changed string width calculation (DG);
 * 21-Apr-2004 : Fixed bug in estimateMaximumTickLabelWidth() method (bug id 
 *               939148) (DG);
 * 11-Jan-2005 : Removed deprecated methods in preparation for 1.0.0 
 *               release (DG);
 * 13-Jan-2005 : Fixed bug (see 
 *               http://www.jfree.org/forum/viewtopic.php?t=11330) (DG);
 * 21-Apr-2005 : Replaced Insets with RectangleInsets, removed redundant 
 *               argument from selectAutoTickUnit() (DG);
 *
 */

package org.jfree.chart.axis;

import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.font.FontRenderContext;
import java.awt.font.LineMetrics;
import java.awt.geom.Rectangle2D;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import org.jfree.chart.event.AxisChangeEvent;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.plot.PlotRenderingInfo;
import org.jfree.chart.plot.ValueAxisPlot;
import org.jfree.data.Range;
import org.jfree.data.time.DateRange;
import org.jfree.data.time.Month;
import org.jfree.data.time.RegularTimePeriod;
import org.jfree.data.time.Year;
import org.jfree.ui.RectangleEdge;
import org.jfree.ui.RectangleInsets;
import org.jfree.ui.TextAnchor;
import org.jfree.util.ObjectUtilities;

/**
 * The base class for axes that display dates.  You will find it easier to 
 * understand how this axis works if you bear in mind that it really 
 * displays/measures integer (or long) data, where the integers are 
 * milliseconds since midnight, 1-Jan-1970.  When displaying tick labels, the 
 * millisecond values are converted back to dates using a 
 * <code>DateFormat</code> instance.
 * <P>
 * You can also create a {@link org.jfree.chart.axis.Timeline} and supply in 
 * the constructor to create an axis that only contains certain domain values. 
 * For example, this allows you to create a date axis that only contains 
 * working days.
 */
public class DateAxis extends ValueAxis implements Cloneable, Serializable {

    /** For serialization. */
    private static final long serialVersionUID = -1013460999649007604L;
    
    /** The default axis range. */
    public static final DateRange DEFAULT_DATE_RANGE = new DateRange();

    /** The default minimum auto range size. */
    public static final double 
        DEFAULT_AUTO_RANGE_MINIMUM_SIZE_IN_MILLISECONDS = 2.0;

    /** The default date tick unit. */
    public static final DateTickUnit DEFAULT_DATE_TICK_UNIT
        = new DateTickUnit(DateTickUnit.DAY, 1, new SimpleDateFormat());

    /** The default anchor date. */
    public static final Date DEFAULT_ANCHOR_DATE = new Date();

    /** The current tick unit. */
    private DateTickUnit tickUnit;

    /** The override date format. */
    private DateFormat dateFormatOverride;

    /** 
     * Tick marks can be displayed at the start or the middle of the time 
     * period. 
     */
    private DateTickMarkPosition tickMarkPosition = DateTickMarkPosition.START;

    /**
     * A timeline that includes all milliseconds (as defined by 
     * <code>java.util.Date</code>) in the real time line.
     */
    private static class DefaultTimeline implements Timeline, Serializable {

        /**
         * Converts a millisecond into a timeline value.
         *
         * @param millisecond  the millisecond.
         *
         * @return The timeline value.
         */
        public long toTimelineValue(long millisecond) {
            return millisecond;
        }

        /**
         * Converts a date into a timeline value.
         *
         * @param date  the domain value.
         *
         * @return The timeline value.
         */
        public long toTimelineValue(Date date) {
            return date.getTime();
        }

        /**
         * Converts a timeline value into a millisecond (as encoded by 
         * <code>java.util.Date</code>).
         *
         * @param value  the value.
         *
         * @return The millisecond.
         */
        public long toMillisecond(long value) {
            return value;
        }

        /**
         * Returns <code>true</code> if the timeline includes the specified 
         * domain value.
         *
         * @param millisecond  the millisecond.
         *
         * @return <code>true</code>.
         */
        public boolean containsDomainValue(long millisecond) {
            return true;
        }

        /**
         * Returns <code>true</code> if the timeline includes the specified 
         * domain value.
         *
         * @param date  the date.
         *
         * @return <code>true</code>.
         */
        public boolean containsDomainValue(Date date) {
            return true;
        }

        /**
         * Returns <code>true</code> if the timeline includes the specified 
         * domain value range.
         *
         * @param from  the start value.
         * @param to  the end value.
         *
         * @return <code>true</code>.
         */
        public boolean containsDomainRange(long from, long to) {
            return true;
        }

        /**
         * Returns <code>true</code> if the timeline includes the specified 
         * domain value range.
         *
         * @param from  the start date.
         * @param to  the end date.
         *
         * @return <code>true</code>.
         */
        public boolean containsDomainRange(Date from, Date to) {
            return true;
        }

        /**
         * Tests an object for equality with this instance.
         *
         * @param object  the object.
         *
         * @return A boolean.
         */
        public boolean equals(Object object) {

            if (object == null) {
                return false;
            }

            if (object == this) {
                return true;
            }

            if (object instanceof DefaultTimeline) {
                return true;
            }

            return false;

        }
    }

    /** A static default timeline shared by all standard DateAxis */
    private static final Timeline DEFAULT_TIMELINE = new DefaultTimeline();

    /** The time zone for the axis. */
    private TimeZone timeZone;
    
    /** Our underlying timeline. */
    private Timeline timeline;

    /**
     * Creates a date axis with no label.
     */
    public DateAxis() {
        this(null);
    }

    /**
     * Creates a date axis with the specified label.
     *
     * @param label  the axis label (<code>null</code> permitted).
     */
    public DateAxis(String label) {
        this(label, TimeZone.getDefault());
    }

    /**
     * Creates a date axis. A timeline is specified for the axis. This allows 
     * special transformations to occur between a domain of values and the 
     * values included in the axis.
     *
     * @see org.jfree.chart.axis.SegmentedTimeline
     *
     * @param label  the axis label (<code>null</code> permitted).
     * @param zone  the time zone.
     */
    public DateAxis(String label, TimeZone zone) {
        super(label, DateAxis.createStandardDateTickUnits(zone));
        setTickUnit(DateAxis.DEFAULT_DATE_TICK_UNIT, false, false);
        setAutoRangeMinimumSize(
            DEFAULT_AUTO_RANGE_MINIMUM_SIZE_IN_MILLISECONDS
        );
        setRange(DEFAULT_DATE_RANGE, false, false);
        this.dateFormatOverride = null;
        this.timeZone = zone;
        this.timeline = DEFAULT_TIMELINE;
    }

    /**
     * Returns the underlying timeline used by this axis.
     *
     * @return The timeline.
     */
    public Timeline getTimeline() {
        return this.timeline;
    }

    /**
     * Sets the underlying timeline to use for this axis.
     * <P>
     * If the timeline is changed, an {@link AxisChangeEvent} is sent to all
     * registered listeners.
     *
     * @param timeline  the timeline.
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
     * @return The tick unit (possibly <code>null</code>).
     */
    public DateTickUnit getTickUnit() {
        return this.tickUnit;
    }

    /**
     * Sets the tick unit for the axis.  The auto-tick-unit-selection flag is 
     * set to <code>false</code>, and registered listeners are notified that 
     * the axis has been changed.
     *
     * @param unit  the tick unit.
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
    public void setTickUnit(DateTickUnit unit, boolean notify, 
                            boolean turnOffAutoSelection) {

        this.tickUnit = unit;
        if (turnOffAutoSelection) {
            setAutoTickUnitSelection(false, false);
        }
        if (notify) {
            notifyListeners(new AxisChangeEvent(this));
        }

    }

    /**
     * Returns the date format override.  If this is non-null, then it will be
     * used to format the dates on the axis.
     *
     * @return The formatter (possibly <code>null</code>).
     */
    public DateFormat getDateFormatOverride() {
        return this.dateFormatOverride;
    }

    /**
     * Sets the date format override.  If this is non-null, then it will be 
     * used to format the dates on the axis.
     *
     * @param formatter  the date formatter (<code>null</code> permitted).
     */
    public void setDateFormatOverride(DateFormat formatter) {
        this.dateFormatOverride = formatter;
        notifyListeners(new AxisChangeEvent(this));
    }

    /**
     * Sets the upper and lower bounds for the axis and sends an 
     * {@link AxisChangeEvent} to all registered listeners.  As a side-effect, 
     * the auto-range flag is set to false.
     *
     * @param range  the new range (<code>null</code> not permitted).
     */
    public void setRange(Range range) {
        setRange(range, true, true);
    }

    /**
     * Sets the range for the axis, if requested, sends an 
     * {@link AxisChangeEvent} to all registered listeners.  As a side-effect, 
     * the auto-range flag is set to <code>false</code> (optional).
     *
     * @param range  the range (<code>null</code> not permitted).
     * @param turnOffAutoRange  a flag that controls whether or not the auto 
     *                          range is turned off.
     * @param notify  a flag that controls whether or not listeners are 
     *                notified.
     */
    public void setRange(Range range, boolean turnOffAutoRange, 
                         boolean notify) {
        if (range == null) {
            throw new IllegalArgumentException("Null 'range' argument.");
        }
        // usually the range will be a DateRange, but if it isn't do a 
        // conversion...
        if (!(range instanceof DateRange)) {
            range = new DateRange(range);
        }
        super.setRange(range, turnOffAutoRange, notify);
    }

    /**
     * Sets the axis range and sends an {@link AxisChangeEvent} to all 
     * registered listeners.
     *
     * @param lower  the lower bound for the axis.
     * @param upper  the upper bound for the axis.
     */
    public void setRange(Date lower, Date upper) {
        if (lower.getTime() >= upper.getTime()) {
            throw new IllegalArgumentException("Requires 'lower' < 'upper'.");
        }
        setRange(new DateRange(lower, upper));
    }

    /**
     * Sets the axis range and sends an {@link AxisChangeEvent} to all 
     * registered listeners.
     *
     * @param lower  the lower bound for the axis.
     * @param upper  the upper bound for the axis.
     */
    public void setRange(double lower, double upper) {
        if (lower >= upper) {
            throw new IllegalArgumentException("Requires 'lower' < 'upper'.");
        }
        setRange(new DateRange(lower, upper));
    }

    /**
     * Returns the earliest date visible on the axis.
     *
     * @return The date.
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
     * Sets the minimum date visible on the axis and sends an 
     * {@link AxisChangeEvent} to all registered listeners.
     *
     * @param date  the date (<code>null</code> not permitted).
     */
    public void setMinimumDate(Date date) {
        setRange(new DateRange(date, getMaximumDate()), true, false);
        notifyListeners(new AxisChangeEvent(this));
    }

    /**
     * Returns the latest date visible on the axis.
     *
     * @return The date.
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
     * Sets the maximum date visible on the axis.  An {@link AxisChangeEvent} 
     * is sent to all registered listeners.
     *
     * @param maximumDate  the date (<code>null</code> not permitted).
     */
    public void setMaximumDate(Date maximumDate) {
        setRange(new DateRange(getMinimumDate(), maximumDate), true, false);
        notifyListeners(new AxisChangeEvent(this));
    }

    /**
     * Returns the tick mark position (start, middle or end of the time period).
     *
     * @return The position (never <code>null</code>).
     */
    public DateTickMarkPosition getTickMarkPosition() {
        return this.tickMarkPosition;
    }

    /**
     * Sets the tick mark position (start, middle or end of the time period) 
     * and sends an {@link AxisChangeEvent} to all registered listeners.
     *
     * @param position  the position (<code>null</code> not permitted).
     */
    public void setTickMarkPosition(DateTickMarkPosition position) {
        if (position == null) {
            throw new IllegalArgumentException("Null 'position' argument.");
        }
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
     * Returns <code>true</code> if the axis hides this value, and 
     * <code>false</code> otherwise.
     *
     * @param millis  the data value.
     *
     * @return A value.
     */
    public boolean isHiddenValue(long millis) {
        return (!this.timeline.containsDomainValue(new Date(millis)));
    }

    /**
     * Translates the data value to the display coordinates (Java 2D User Space)
     * of the chart.
     *
     * @param value  the date to be plotted.
     * @param area  the rectangle (in Java2D space) where the data is to be 
     *              plotted.
     * @param edge  the axis location.
     *
     * @return The coordinate corresponding to the supplied data value.
     */
    public double valueToJava2D(double value, Rectangle2D area, 
                                RectangleEdge edge) {
        
        value = this.timeline.toTimelineValue((long) value);

        DateRange range = (DateRange) getRange();
        double axisMin = this.timeline.toTimelineValue(range.getLowerDate());
        double axisMax = this.timeline.toTimelineValue(range.getUpperDate());
        double result = 0.0;
        if (RectangleEdge.isTopOrBottom(edge)) {
            double minX = area.getX();
            double maxX = area.getMaxX();
            if (isInverted()) {
                result = maxX + ((value - axisMin) / (axisMax - axisMin)) 
                         * (minX - maxX);
            }
            else {
                result = minX + ((value - axisMin) / (axisMax - axisMin)) 
                         * (maxX - minX);
            }
        }
        else if (RectangleEdge.isLeftOrRight(edge)) {
            double minY = area.getMinY();
            double maxY = area.getMaxY();
            if (isInverted()) {
                result = minY + (((value - axisMin) / (axisMax - axisMin)) 
                         * (maxY - minY));
            }
            else {
                result = maxY - (((value - axisMin) / (axisMax - axisMin)) 
                         * (maxY - minY));
            }
        }
        return result;

    }

    /**
     * Translates a date to Java2D coordinates, based on the range displayed by
     * this axis for the specified data area.
     *
     * @param date  the date.
     * @param area  the rectangle (in Java2D space) where the data is to be
     *              plotted.
     * @param edge  the axis location.
     *
     * @return The coordinate corresponding to the supplied date.
     */
    public double dateToJava2D(Date date, Rectangle2D area, 
                               RectangleEdge edge) {  
        double value = date.getTime();
        return valueToJava2D(value, area, edge);
    }

    /**
     * Translates a Java2D coordinate into the corresponding data value.  To 
     * perform this translation, you need to know the area used for plotting 
     * data, and which edge the axis is located on.
     *
     * @param java2DValue  the coordinate in Java2D space.
     * @param area  the rectangle (in Java2D space) where the data is to be 
     *              plotted.
     * @param edge  the axis location.
     *
     * @return A data value.
     */
    public double java2DToValue(double java2DValue, Rectangle2D area, 
                                RectangleEdge edge) {
        
        DateRange range = (DateRange) getRange();
        double axisMin = this.timeline.toTimelineValue(range.getLowerDate());
        double axisMax = this.timeline.toTimelineValue(range.getUpperDate());

        double min = 0.0;
        double max = 0.0;
        if (RectangleEdge.isTopOrBottom(edge)) {
            min = area.getX();
            max = area.getMaxX();
        }
        else if (RectangleEdge.isLeftOrRight(edge)) {
            min = area.getMaxY();
            max = area.getY();
        }

        double result;
        if (isInverted()) {
             result = axisMax - ((java2DValue - min) / (max - min) 
                      * (axisMax - axisMin));
        }
        else {
             result = axisMin + ((java2DValue - min) / (max - min) 
                      * (axisMax - axisMin));
        }

        return this.timeline.toMillisecond((long) result); 
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
     * @return The value of the highest visible tick on the axis.
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
     * @return The previous "standard" date.
     */
    protected Date previousStandardDate(Date date, DateTickUnit unit) {

        int milliseconds;
        int seconds;
        int minutes;
        int hours;
        int days;
        int months;
        int years;

        Calendar calendar = Calendar.getInstance(this.timeZone);
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
                // long result = calendar.getTimeInMillis();  
                    // won't work with JDK 1.3
                long result = calendar.getTime().getTime();
                if (result > date.getTime()) {
                    calendar.set(years, months, value - 1, hours, 0, 0);
                }
                return calendar.getTime();

            case (DateTickUnit.MONTH) :
                years = calendar.get(Calendar.YEAR);
                calendar.clear(Calendar.MILLISECOND);
                calendar.set(years, value, 1, 0, 0, 0);
                Month month = new Month(calendar.getTime());
                Date standardDate = calculateDateForPosition(
                    month, this.tickMarkPosition
                );
                long millis = standardDate.getTime();
                if (millis > date.getTime()) {
                    month = (Month) month.previous();
                    standardDate = calculateDateForPosition(
                        month, this.tickMarkPosition
                    );
                }
                return standardDate;

            case(DateTickUnit.YEAR) :
                if (this.tickMarkPosition == DateTickMarkPosition.START) {
                    months = 0;
                    days = 1;
                }
                else if (this.tickMarkPosition == DateTickMarkPosition.MIDDLE) {
                    months = 6;
                    days = 1;
                }
                else {
                    months = 11;
                    days = 31;
                }
                calendar.clear(Calendar.MILLISECOND);
                calendar.set(value, months, days, 0, 0, 0);
                return calendar.getTime();

            default: return null;

        }

    }

    /**
     * Returns a {@link java.util.Date} corresponding to the specified position
     * within a {@link RegularTimePeriod}.
     *
     * @param period  the period.
     * @param position  the position (<code>null</code> not permitted).
     *
     * @return A date.
     */
    private Date calculateDateForPosition(RegularTimePeriod period, 
                                          DateTickMarkPosition position) {
        
        if (position == null) {
            throw new IllegalArgumentException("Null 'position' argument.");   
        }
        Date result = null;
        if (position == DateTickMarkPosition.START) {
            result = new Date(period.getFirstMillisecond());
        }
        else if (position == DateTickMarkPosition.MIDDLE) {
            result = new Date(period.getMiddleMillisecond());
        }
        else if (position == DateTickMarkPosition.END) {
            result = new Date(period.getLastMillisecond());
        }
        return result;

    }

    /**
     * Returns the first "standard" date (based on the specified field and 
     * units).
     *
     * @param date  the reference date.
     * @param unit  the date tick unit.
     *
     * @return The next "standard" date.
     */
    protected Date nextStandardDate(Date date, DateTickUnit unit) {

        Date previous = previousStandardDate(date, unit);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(previous);
        calendar.add(unit.getCalendarField(), unit.getCount());
        return calendar.getTime();

    }

    /**
     * Returns a collection of standard date tick units that uses the default 
     * time zone.  This collection will be used by default, but you are free 
     * to create your own collection if you want to (see the 
     * {@link ValueAxis#setStandardTickUnits(TickUnitSource)} method inherited 
     * from the {@link ValueAxis} class).
     *
     * @return A collection of standard date tick units.
     */
    public static TickUnitSource createStandardDateTickUnits() {
        return createStandardDateTickUnits(TimeZone.getDefault());
    }

    /**
     * Returns a collection of standard date tick units.  This collection will 
     * be used by default, but you are free to create your own collection if 
     * you want to (see the 
     * {@link ValueAxis#setStandardTickUnits(TickUnitSource)} method inherited 
     * from the {@link ValueAxis} class).
     *
     * @param zone  the time zone (<code>null</code> not permitted).
     * 
     * @return A collection of standard date tick units.
     */
    public static TickUnitSource createStandardDateTickUnits(TimeZone zone) {

        if (zone == null) {
            throw new IllegalArgumentException("Null 'zone' argument.");
        }
        TickUnits units = new TickUnits();

        // date formatters
        DateFormat f1 = new SimpleDateFormat("HH:mm:ss.SSS");
        DateFormat f2 = new SimpleDateFormat("HH:mm:ss");
        DateFormat f3 = new SimpleDateFormat("HH:mm");
        DateFormat f4 = new SimpleDateFormat("d-MMM, HH:mm");
        DateFormat f5 = new SimpleDateFormat("d-MMM");
        DateFormat f6 = new SimpleDateFormat("MMM-yyyy");
        DateFormat f7 = new SimpleDateFormat("yyyy");
        
        f1.setTimeZone(zone);
        f2.setTimeZone(zone);
        f3.setTimeZone(zone);
        f4.setTimeZone(zone);
        f5.setTimeZone(zone);
        f6.setTimeZone(zone);
        f7.setTimeZone(zone);
        
        // milliseconds
        units.add(new DateTickUnit(DateTickUnit.MILLISECOND, 1, f1));
        units.add(
            new DateTickUnit(
                DateTickUnit.MILLISECOND, 5, DateTickUnit.MILLISECOND, 1, f1
            )
        );
        units.add(
            new DateTickUnit(
                DateTickUnit.MILLISECOND, 10, DateTickUnit.MILLISECOND, 1, f1
            )
        );
        units.add(
            new DateTickUnit(
                DateTickUnit.MILLISECOND, 25, DateTickUnit.MILLISECOND, 5, f1
            )
        );
        units.add(
            new DateTickUnit(
                DateTickUnit.MILLISECOND, 50, DateTickUnit.MILLISECOND, 10, f1
            )
        );
        units.add(
            new DateTickUnit(
                DateTickUnit.MILLISECOND, 100, DateTickUnit.MILLISECOND, 10, f1
            )
        );
        units.add(
            new DateTickUnit(
                DateTickUnit.MILLISECOND, 250, DateTickUnit.MILLISECOND, 10, f1
            )
        );
        units.add(
            new DateTickUnit(
                DateTickUnit.MILLISECOND, 500, DateTickUnit.MILLISECOND, 50, f1
            )
        );

        // seconds
        units.add(
            new DateTickUnit(
                DateTickUnit.SECOND, 1, DateTickUnit.MILLISECOND, 50, f2
            )
        );
        units.add(
            new DateTickUnit(
                DateTickUnit.SECOND, 5, DateTickUnit.SECOND, 1, f2
            )
        );
        units.add(
            new DateTickUnit(
                DateTickUnit.SECOND, 10, DateTickUnit.SECOND, 1, f2
            )
        );
        units.add(
            new DateTickUnit(
                DateTickUnit.SECOND, 30, DateTickUnit.SECOND, 5, f2
            )
        );

        // minutes
        units.add(
            new DateTickUnit(DateTickUnit.MINUTE, 1, DateTickUnit.SECOND, 5, f3)
        );
        units.add(
            new DateTickUnit(
                DateTickUnit.MINUTE, 2, DateTickUnit.SECOND, 10, f3
            )
        );
        units.add(
            new DateTickUnit(DateTickUnit.MINUTE, 5, DateTickUnit.MINUTE, 1, f3)
        );
        units.add(
            new DateTickUnit(
                DateTickUnit.MINUTE, 10, DateTickUnit.MINUTE, 1, f3
            )
        );
        units.add(
            new DateTickUnit(
                DateTickUnit.MINUTE, 15, DateTickUnit.MINUTE, 5, f3
            )
        );
        units.add(
            new DateTickUnit(
                DateTickUnit.MINUTE, 20, DateTickUnit.MINUTE, 5, f3
            )
        );
        units.add(
            new DateTickUnit(
                DateTickUnit.MINUTE, 30, DateTickUnit.MINUTE, 5, f3
            )
        );

        // hours
        units.add(
            new DateTickUnit(DateTickUnit.HOUR, 1, DateTickUnit.MINUTE, 5, f3)
        );
        units.add(
            new DateTickUnit(DateTickUnit.HOUR, 2, DateTickUnit.MINUTE, 10, f3)
        );
        units.add(
            new DateTickUnit(DateTickUnit.HOUR, 4, DateTickUnit.MINUTE, 30, f3)
        );
        units.add(
            new DateTickUnit(DateTickUnit.HOUR, 6, DateTickUnit.HOUR, 1, f3)
        );
        units.add(
            new DateTickUnit(DateTickUnit.HOUR, 12, DateTickUnit.HOUR, 1, f4)
        );

        // days
        units.add(
            new DateTickUnit(DateTickUnit.DAY, 1, DateTickUnit.HOUR, 1, f5)
        );
        units.add(
            new DateTickUnit(DateTickUnit.DAY, 2, DateTickUnit.HOUR, 1, f5)
        );
        units.add(
            new DateTickUnit(DateTickUnit.DAY, 7, DateTickUnit.DAY, 1, f5)
        );
        units.add(
            new DateTickUnit(DateTickUnit.DAY, 15, DateTickUnit.DAY, 1, f5)
        );

        // months
        units.add(
            new DateTickUnit(DateTickUnit.MONTH, 1, DateTickUnit.DAY, 1, f6)
        );
        units.add(
            new DateTickUnit(DateTickUnit.MONTH, 2, DateTickUnit.DAY, 1, f6)
        );
        units.add(
            new DateTickUnit(DateTickUnit.MONTH, 3, DateTickUnit.MONTH, 1, f6)
        );
        units.add(
            new DateTickUnit(DateTickUnit.MONTH, 4,  DateTickUnit.MONTH, 1, f6)
        );
        units.add(
            new DateTickUnit(DateTickUnit.MONTH, 6,  DateTickUnit.MONTH, 1, f6)
        );

        // years
        units.add(
            new DateTickUnit(DateTickUnit.YEAR, 1,  DateTickUnit.MONTH, 1, f7)
        );
        units.add(
            new DateTickUnit(DateTickUnit.YEAR, 2,  DateTickUnit.MONTH, 3, f7)
        );
        units.add(
            new DateTickUnit(DateTickUnit.YEAR, 5,  DateTickUnit.YEAR, 1, f7)
        );
        units.add(
            new DateTickUnit(DateTickUnit.YEAR, 10,  DateTickUnit.YEAR, 1, f7)
        );
        units.add(
            new DateTickUnit(DateTickUnit.YEAR, 25, DateTickUnit.YEAR, 5, f7)
        );
        units.add(
            new DateTickUnit(DateTickUnit.YEAR, 50, DateTickUnit.YEAR, 10, f7)
        );
        units.add(
            new DateTickUnit(DateTickUnit.YEAR, 100, DateTickUnit.YEAR, 20, f7)
        );

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
                if (this.timeline instanceof SegmentedTimeline) { 
                    //Timeline hasn't method getStartTime()
                    r = new DateRange(
                        ((SegmentedTimeline) this.timeline).getStartTime(),
                        ((SegmentedTimeline) this.timeline).getStartTime() + 1
                    );
                } 
                else {
                    r = new DateRange();
                }
            }

            long upper = this.timeline.toTimelineValue(
                (long) r.getUpperBound()
            );
            long lower;
            long fixedAutoRange = (long) getFixedAutoRange();
            if (fixedAutoRange > 0.0) {
                lower = upper - fixedAutoRange;
            }
            else {
                lower = this.timeline.toTimelineValue((long) r.getLowerBound());
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

            upper = this.timeline.toMillisecond(upper);
            lower = this.timeline.toMillisecond(lower);
            DateRange dr = new DateRange(new Date(lower), new Date(upper));
            setRange(dr, false, false);
        }

    }

    /**
     * Selects an appropriate tick value for the axis.  The strategy is to
     * display as many ticks as possible (selected from an array of 'standard'
     * tick units) without the labels overlapping.
     *
     * @param g2  the graphics device.
     * @param dataArea  the area defined by the axes.
     * @param edge  the axis location.
     */
    protected void selectAutoTickUnit(Graphics2D g2, 
                                      Rectangle2D dataArea,
                                      RectangleEdge edge) {

        if (RectangleEdge.isTopOrBottom(edge)) {
            selectHorizontalAutoTickUnit(g2, dataArea, edge);
        }
        else if (RectangleEdge.isLeftOrRight(edge)) {
            selectVerticalAutoTickUnit(g2, dataArea, edge);
        }

    }

    /**
     * Selects an appropriate tick size for the axis.  The strategy is to
     * display as many ticks as possible (selected from a collection of 
     * 'standard' tick units) without the labels overlapping.
     *
     * @param g2  the graphics device.
     * @param dataArea  the area defined by the axes.
     * @param edge  the axis location.
     */
    protected void selectHorizontalAutoTickUnit(Graphics2D g2, 
                                                Rectangle2D dataArea, 
                                                RectangleEdge edge) {

        long shift = 0;
        if (this.timeline instanceof SegmentedTimeline) {
            shift = ((SegmentedTimeline) this.timeline).getStartTime();
        }
        double zero = valueToJava2D(shift + 0.0, dataArea, edge);
        double tickLabelWidth 
            = estimateMaximumTickLabelWidth(g2, getTickUnit());

        // start with the current tick unit...
        TickUnitSource tickUnits = getStandardTickUnits();
        TickUnit unit1 = tickUnits.getCeilingTickUnit(getTickUnit());
        double x1 = valueToJava2D(shift + unit1.getSize(), dataArea, edge);
        double unit1Width = Math.abs(x1 - zero);

        // then extrapolate...
        double guess = (tickLabelWidth / unit1Width) * unit1.getSize();
        DateTickUnit unit2 = (DateTickUnit) tickUnits.getCeilingTickUnit(guess);
        double x2 = valueToJava2D(shift + unit2.getSize(), dataArea, edge);
        double unit2Width = Math.abs(x2 - zero);
        tickLabelWidth = estimateMaximumTickLabelWidth(g2, unit2);
        if (tickLabelWidth > unit2Width) {
            unit2 = (DateTickUnit) tickUnits.getLargerTickUnit(unit2);
        }
        setTickUnit(unit2, false, false);
    }
    
    /**
     * Selects an appropriate tick size for the axis.  The strategy is to
     * display as many ticks as possible (selected from a collection of 
     * 'standard' tick units) without the labels overlapping.
     *
     * @param g2  the graphics device.
     * @param dataArea  the area in which the plot should be drawn.
     * @param edge  the axis location.
     */
    protected void selectVerticalAutoTickUnit(Graphics2D g2,
                                              Rectangle2D dataArea,
                                              RectangleEdge edge) {

        // start with the current tick unit...
        TickUnitSource tickUnits = getStandardTickUnits();
        double zero = valueToJava2D(0.0, dataArea, edge);

        // start with a unit that is at least 1/10th of the axis length
        double estimate1 = getRange().getLength() / 10.0;
        DateTickUnit candidate1 
            = (DateTickUnit) tickUnits.getCeilingTickUnit(estimate1);
        double labelHeight1 = estimateMaximumTickLabelHeight(g2, candidate1);
        double y1 = valueToJava2D(candidate1.getSize(), dataArea, edge);
        double candidate1UnitHeight = Math.abs(y1 - zero);

        // now extrapolate based on label height and unit height...
        double estimate2 
            = (labelHeight1 / candidate1UnitHeight) * candidate1.getSize();
        DateTickUnit candidate2 
            = (DateTickUnit) tickUnits.getCeilingTickUnit(estimate2);
        double labelHeight2 = estimateMaximumTickLabelHeight(g2, candidate2);
        double y2 = valueToJava2D(candidate2.getSize(), dataArea, edge);
        double unit2Height = Math.abs(y2 - zero);

       // make final selection...
       DateTickUnit finalUnit;
       if (labelHeight2 < unit2Height) {
           finalUnit = candidate2;
       }
       else {
           finalUnit = (DateTickUnit) tickUnits.getLargerTickUnit(candidate2);
       }
       setTickUnit(finalUnit, false, false);

    }

    /**
     * Estimates the maximum width of the tick labels, assuming the specified 
     * tick unit is used.
     * <P>
     * Rather than computing the string bounds of every tick on the axis, we
     * just look at two values: the lower bound and the upper bound for the 
     * axis.  These two values will usually be representative.
     *
     * @param g2  the graphics device.
     * @param unit  the tick unit to use for calculation.
     *
     * @return The estimated maximum width of the tick labels.
     */
    private double estimateMaximumTickLabelWidth(Graphics2D g2, 
                                                 DateTickUnit unit) {

        RectangleInsets tickLabelInsets = getTickLabelInsets();
        double result = tickLabelInsets.getLeft() + tickLabelInsets.getRight();

        Font tickLabelFont = getTickLabelFont();
        FontRenderContext frc = g2.getFontRenderContext();
        LineMetrics lm = tickLabelFont.getLineMetrics("ABCxyz", frc);
        if (isVerticalTickLabels()) {
            // all tick labels have the same width (equal to the height of 
            // the font)...
            result += lm.getHeight();
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
                lowerStr = unit.dateToString(lower);
                upperStr = unit.dateToString(upper);
            }
            FontMetrics fm = g2.getFontMetrics(tickLabelFont);
            double w1 = fm.stringWidth(lowerStr);
            double w2 = fm.stringWidth(upperStr);
            result += Math.max(w1, w2);
        }

        return result;

    }

    /**
     * Estimates the maximum width of the tick labels, assuming the specified 
     * tick unit is used.
     * <P>
     * Rather than computing the string bounds of every tick on the axis, we 
     * just look at two values: the lower bound and the upper bound for the 
     * axis.  These two values will usually be representative.
     *
     * @param g2  the graphics device.
     * @param unit  the tick unit to use for calculation.
     *
     * @return The estimated maximum width of the tick labels.
     */
    private double estimateMaximumTickLabelHeight(Graphics2D g2, 
                                                  DateTickUnit unit) {

        RectangleInsets tickLabelInsets = getTickLabelInsets();
        double result = tickLabelInsets.getTop() + tickLabelInsets.getBottom();

        Font tickLabelFont = getTickLabelFont();
        FontRenderContext frc = g2.getFontRenderContext();
        LineMetrics lm = tickLabelFont.getLineMetrics("ABCxyz", frc);
        if (!isVerticalTickLabels()) {
            // all tick labels have the same width (equal to the height of 
            // the font)...
            result += lm.getHeight();
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
                lowerStr = unit.dateToString(lower);
                upperStr = unit.dateToString(upper);
            }
            FontMetrics fm = g2.getFontMetrics(tickLabelFont);
            double w1 = fm.stringWidth(lowerStr);
            double w2 = fm.stringWidth(upperStr);
            result += Math.max(w1, w2);
        }

        return result;

    }

    /**
     * Calculates the positions of the tick labels for the axis, storing the 
     * results in the tick label list (ready for drawing).
     *
     * @param g2  the graphics device.
     * @param state  the axis state.
     * @param dataArea  the area in which the plot should be drawn.
     * @param edge  the location of the axis.
     *
     * @return A list of ticks.
     */
    public List refreshTicks(Graphics2D g2,
                             AxisState state,
                             Rectangle2D dataArea,
                             RectangleEdge edge) {

        List result = null;
        if (RectangleEdge.isTopOrBottom(edge)) {
            result = refreshTicksHorizontal(g2, dataArea, edge);
        }
        else if (RectangleEdge.isLeftOrRight(edge)) {
            result = refreshTicksVertical(g2, dataArea, edge);
        }
        return result;

    }

    /**
     * Recalculates the ticks for the date axis.
     *
     * @param g2  the graphics device.
     * @param dataArea  the area in which the data is to be drawn.
     * @param edge  the location of the axis.
     *
     * @return A list of ticks.
     */
    protected List refreshTicksHorizontal(Graphics2D g2,
                                          Rectangle2D dataArea,
                                          RectangleEdge edge) {

        List result = new java.util.ArrayList();

        Font tickLabelFont = getTickLabelFont();
        g2.setFont(tickLabelFont);

        if (isAutoTickUnitSelection()) {
            selectAutoTickUnit(g2, dataArea, edge);
        }

        DateTickUnit unit = getTickUnit();
        Date tickDate = calculateLowestVisibleTickValue(unit);
        Date upperDate = getMaximumDate();
        // float lastX = Float.MIN_VALUE;
        while (tickDate.before(upperDate)) {

            if (!isHiddenValue(tickDate.getTime())) {
                // work out the value, label and position
                String tickLabel;
                DateFormat formatter = getDateFormatOverride();
                if (formatter != null) {
                    tickLabel = formatter.format(tickDate);
                }
                else {
                    tickLabel = this.tickUnit.dateToString(tickDate);
                }
                TextAnchor anchor = null;
                TextAnchor rotationAnchor = null;
                double angle = 0.0;
                if (isVerticalTickLabels()) {
                    anchor = TextAnchor.CENTER_RIGHT;
                    rotationAnchor = TextAnchor.CENTER_RIGHT;
                    if (edge == RectangleEdge.TOP) {
                        angle = Math.PI / 2.0;
                    }
                    else {
                        angle = -Math.PI / 2.0;
                    }
                }
                else {
                    if (edge == RectangleEdge.TOP) {
                        anchor = TextAnchor.BOTTOM_CENTER;
                        rotationAnchor = TextAnchor.BOTTOM_CENTER;
                    }
                    else {
                        anchor = TextAnchor.TOP_CENTER;
                        rotationAnchor = TextAnchor.TOP_CENTER;
                    }
                }

                Tick tick = new DateTick(
                    tickDate, tickLabel, anchor, rotationAnchor, angle
                );
                result.add(tick);
                tickDate = unit.addToDate(tickDate);
            }
            else {
                tickDate = unit.rollDate(tickDate);
                continue;
            }

            // could add a flag to make the following correction optional...
            switch (unit.getUnit()) {

                case (DateTickUnit.MILLISECOND) :
                case (DateTickUnit.SECOND) :
                case (DateTickUnit.MINUTE) :
                case (DateTickUnit.HOUR) :
                case (DateTickUnit.DAY) :
                    break;
                case (DateTickUnit.MONTH) :
                    tickDate = calculateDateForPosition(
                        new Month(tickDate), this.tickMarkPosition
                    );
                    break;
                case(DateTickUnit.YEAR) :
                    tickDate = calculateDateForPosition(
                        new Year(tickDate), this.tickMarkPosition
                    );
                    break;

                default: break;

            }

        }
        return result;

    }

    /**
     * Recalculates the ticks for the date axis.
     *
     * @param g2  the graphics device.
     * @param dataArea  the area in which the plot should be drawn.
     * @param edge  the location of the axis.
     *
     * @return A list of ticks.
     */
    protected List refreshTicksVertical(Graphics2D g2,
                                        Rectangle2D dataArea,
                                        RectangleEdge edge) {

        List result = new java.util.ArrayList();

        Font tickLabelFont = getTickLabelFont();
        g2.setFont(tickLabelFont);

        if (isAutoTickUnitSelection()) {
            selectAutoTickUnit(g2, dataArea, edge);
        }
        DateTickUnit unit = getTickUnit();
        Date tickDate = calculateLowestVisibleTickValue(unit);
        //Date upperDate = calculateHighestVisibleTickValue(unit);
        Date upperDate = getMaximumDate();
        while (tickDate.before(upperDate)) {

            if (!isHiddenValue(tickDate.getTime())) {
                // work out the value, label and position
                String tickLabel;
                DateFormat formatter = getDateFormatOverride();
                if (formatter != null) {
                    tickLabel = formatter.format(tickDate);
                }
                else {
                    tickLabel = this.tickUnit.dateToString(tickDate);
                }
                TextAnchor anchor = null;
                TextAnchor rotationAnchor = null;
                double angle = 0.0;
                if (isVerticalTickLabels()) {
                    anchor = TextAnchor.BOTTOM_CENTER;
                    rotationAnchor = TextAnchor.BOTTOM_CENTER;
                    if (edge == RectangleEdge.LEFT) {
                        angle = -Math.PI / 2.0;
                    }
                    else {
                        angle = Math.PI / 2.0;
                    }
                }
                else {
                    if (edge == RectangleEdge.LEFT) {
                        anchor = TextAnchor.CENTER_RIGHT;
                        rotationAnchor = TextAnchor.CENTER_RIGHT;
                    }
                    else {
                        anchor = TextAnchor.CENTER_LEFT;
                        rotationAnchor = TextAnchor.CENTER_LEFT;
                    }
                }

                Tick tick = new DateTick(
                    tickDate, tickLabel, anchor, rotationAnchor, angle
                );
                result.add(tick);
                tickDate = unit.addToDate(tickDate);
            }
            else {
                tickDate = unit.rollDate(tickDate);
            }
        }
        return result;
    }

    /**
     * Draws the axis on a Java 2D graphics device (such as the screen or a 
     * printer).
     *
     * @param g2  the graphics device (<code>null</code> not permitted).
     * @param cursor  the cursor location.
     * @param plotArea  the area within which the axes and data should be 
     *                  drawn (<code>null</code> not permitted).
     * @param dataArea  the area within which the data should be drawn 
     *                  (<code>null</code> not permitted).
     * @param edge  the location of the axis (<code>null</code> not permitted).
     * @param plotState  collects information about the plot 
     *                   (<code>null</code> permitted).
     *
     * @return The axis state (never <code>null</code>).
     */
    public AxisState draw(Graphics2D g2, 
                          double cursor,
                          Rectangle2D plotArea, 
                          Rectangle2D dataArea, 
                          RectangleEdge edge,
                          PlotRenderingInfo plotState) {

        // if the axis is not visible, don't draw it...
        if (!isVisible()) {
            AxisState state = new AxisState(cursor);
            // even though the axis is not visible, we need to refresh ticks in
            // case the grid is being drawn...
            List ticks = refreshTicks(g2, state, dataArea, edge);
            state.setTicks(ticks);
            return state;
        }

        // draw the tick marks and labels...
        AxisState state = drawTickMarksAndLabels(
            g2, cursor, plotArea, dataArea, edge
        );

        // draw the axis label (note that 'state' is passed in *and* 
        // returned)...
        state = drawLabel(getLabel(), g2, plotArea, dataArea, edge, state);

        return state;

    }

    /**
     * Zooms in on the current range.
     *
     * @param lowerPercent  the new lower bound.
     * @param upperPercent  the new upper bound.
     */
    public void zoomRange(double lowerPercent, double upperPercent) {
        double start = this.timeline.toTimelineValue(
            (long) getRange().getLowerBound()
        );
        double length = (this.timeline.toTimelineValue(
                (long) getRange().getUpperBound()) 
                - this.timeline.toTimelineValue(
                    (long) getRange().getLowerBound()
        ));
        Range adjusted = null;
        if (isInverted()) {
            adjusted = new DateRange(
                this.timeline.toMillisecond(
                    (long) (start + (length * (1 - upperPercent)))
                ),
                this.timeline.toMillisecond(
                    (long) (start + (length * (1 - lowerPercent)))
                )
            );
        }
        else {
            adjusted = new DateRange(this.timeline.toMillisecond(
                (long) (start + length * lowerPercent)),
                this.timeline.toMillisecond(
                    (long) (start + length * upperPercent)
                )
            );
        }
        setRange(adjusted);
    } 
    
    /**
     * Tests an object for equality with this instance.
     *
     * @param obj  the object to test.
     *
     * @return A boolean.
     */
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof DateAxis)) {
            return false;
        }
        DateAxis that = (DateAxis) obj;
        if (!ObjectUtilities.equal(this.tickUnit, that.tickUnit)) {
            return false;
        }
        if (!ObjectUtilities.equal(
            this.dateFormatOverride, that.dateFormatOverride)
        ) {
            return false;
        }
        if (!ObjectUtilities.equal(
            this.tickMarkPosition, that.tickMarkPosition
        )) {
            return false;
        }
        if (!ObjectUtilities.equal(this.timeline, that.timeline)) {
            return false;
        }
        return true;
    }

    /**
     * Returns a hash code for this object.
     * 
     * @return A hash code.
     */
    public int hashCode() {
        if (getLabel() != null) {
            return getLabel().hashCode();
        }
        else {
            return 0;
        }
    }

    /**
     * Returns a clone of the object.
     *
     * @return A clone.
     *
     * @throws CloneNotSupportedException if some component of the axis does 
     *         not support cloning.
     */
    public Object clone() throws CloneNotSupportedException {

        DateAxis clone = (DateAxis) super.clone();

        // 'dateTickUnit' is immutable : no need to clone
        if (this.dateFormatOverride != null) {
            clone.dateFormatOverride 
                = (DateFormat) this.dateFormatOverride.clone();
        }
        // 'tickMarkPosition' is immutable : no need to clone

        return clone;

    }
            
}
