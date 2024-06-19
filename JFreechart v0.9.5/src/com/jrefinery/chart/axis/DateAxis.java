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
 * -------------
 * DateAxis.java
 * -------------
 * (C) Copyright 2000-2003, by Simba Management Limited and Contributors.
 *
 * Original Author:  David Gilbert;
 * Contributor(s):   Jonathan Nash;
 *
 * $Id: DateAxis.java,v 1.1 2007/10/10 19:54:25 vauchers Exp $
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
 *
 */

package com.jrefinery.chart.axis;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import com.jrefinery.chart.event.AxisChangeEvent;
import com.jrefinery.data.Range;
import com.jrefinery.data.DateRange;

/**
 * The base class for axes that display dates.
 * <P>
 * You will find it easier to understand how this axis works if you bear in mind that it really
 * displays/measures integer (or long) data, where the integers are milliseconds since midnight,
 * 1-Jan-1970.  When displaying tick labels, the millisecond values are converted back to dates
 * using a <code>DateFormat</code> instance.
 *
 * @see HorizontalDateAxis
 *
 * @author David Gilbert
 * 
 */
public abstract class DateAxis extends ValueAxis {

    /** The default axis range. */
    public static final DateRange DEFAULT_DATE_RANGE = new DateRange();
    
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
     * The anchor date (needs to be synchronised with the anchorValue in the
     * ValueAxis superclass, as this form is maintained for convenience only).
     */
    private Date anchorDate;

    /**
     * Creates a date axis.
     * 
     * @param label  the axis label.
     */
    protected DateAxis(String label) {
        
        super(label, DateAxis.createStandardDateTickUnits());

        setTickUnit(DateAxis.DEFAULT_DATE_TICK_UNIT, false, false);
        setAnchorDate(DateAxis.DEFAULT_ANCHOR_DATE);
        this.dateFormatOverride = null;
        
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
     * Returns the anchor date for the axis.
     *
     * @return the anchor date for the axis (possibly <code>null</code>).
     */
    public Date getAnchorDate() {
        return this.anchorDate;
    }

    /**
     * Sets the anchor date for the axis.
     *
     * @param anchorDate  the new anchor date (<code>null<code> permitted).
     */
    public void setAnchorDate(Date anchorDate) {

        this.anchorDate = anchorDate;
        double millis = (double) anchorDate.getTime();
        super.setAnchorValue(millis);

    }

    /**
     * Sets the anchor value.
     * <p>
     * This method keeps the anchorDate and anchorValue in synch.
     *
     * @param value  the new value.
     */
    public void setAnchorValue(double value) {
        long millis = (long) value;
        this.anchorDate.setTime(millis);
        super.setAnchorValue(value);
    }

    /**
     * Sets the upper and lower bounds for the axis.  Registered listeners are
     * notified of the change.
     * <P>
     * As a side-effect, the auto-range flag is set to false.
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

        setAutoRange(false, false);
        setRangeAttribute(range);
        notifyListeners(new AxisChangeEvent(this));

    }

    /**
     * Sets the axis range.
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
     * Sets the axis range.
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
     * Sets the minimum date visible on the axis.
     *
     * @param minimumDate  the new minimum date.
     */
    public void setMinimumDate(Date minimumDate) {

        setRangeAttribute(new DateRange(minimumDate, getMaximumDate()));
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
     * Sets the maximum date visible on the axis.
     *
     * @param maximumDate  the new maximum date.
     */
    public void setMaximumDate(Date maximumDate) {

        setRangeAttribute(new DateRange(getMinimumDate(), maximumDate));
        notifyListeners(new AxisChangeEvent(this));

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

            case (DateTickUnit.MILLISECOND) : {
                years = calendar.get(Calendar.YEAR);
                months = calendar.get(Calendar.MONTH);
                days = calendar.get(Calendar.DATE);
                hours = calendar.get(Calendar.HOUR_OF_DAY);
                minutes = calendar.get(Calendar.MINUTE);
                seconds = calendar.get(Calendar.SECOND);
                calendar.set(years, months, days, hours, minutes, seconds);
                calendar.set(Calendar.MILLISECOND, value);
                return calendar.getTime();
            }

            case (DateTickUnit.SECOND) : {
                years = calendar.get(Calendar.YEAR);
                months = calendar.get(Calendar.MONTH);
                days = calendar.get(Calendar.DATE);
                hours = calendar.get(Calendar.HOUR_OF_DAY);
                minutes = calendar.get(Calendar.MINUTE);
                calendar.clear(Calendar.MILLISECOND);
                calendar.set(years, months, days, hours, minutes, value);
                return calendar.getTime();
            }

            case (DateTickUnit.MINUTE) : {
                years = calendar.get(Calendar.YEAR);
                months = calendar.get(Calendar.MONTH);
                days = calendar.get(Calendar.DATE);
                hours = calendar.get(Calendar.HOUR_OF_DAY);
                calendar.clear(Calendar.MILLISECOND);
                calendar.set(years, months, days, hours, value, 0);
                return calendar.getTime();
            }

            case (DateTickUnit.HOUR) : {
                years = calendar.get(Calendar.YEAR);
                months = calendar.get(Calendar.MONTH);
                days = calendar.get(Calendar.DATE);
                calendar.clear(Calendar.MILLISECOND);
                calendar.set(years, months, days, value, 0, 0);
                return calendar.getTime();
            }

            case (DateTickUnit.DAY) : {
                years = calendar.get(Calendar.YEAR);
                months = calendar.get(Calendar.MONTH);
                calendar.clear(Calendar.MILLISECOND);
                calendar.set(years, months, value, 0, 0, 0);
                return calendar.getTime();
            }

            case (DateTickUnit.MONTH) : {
                years = calendar.get(Calendar.YEAR);
                calendar.clear(Calendar.MILLISECOND);
                calendar.set(years, value, 1, 0, 0, 0);
                return calendar.getTime();
            }

            case(DateTickUnit.YEAR) : {
                calendar.clear(Calendar.MILLISECOND);
                calendar.set(value, 0, 1, 0, 0, 0);
                return calendar.getTime();
            }

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

    //////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Sets the axis range.
     *
     * @param lower  the lower bound for the axis.
     * @param upper  the upper bound for the axis.
     *
     * @deprecated use setRange(double, double) method.
     */
    public void setAxisRange(double lower, double upper) {

        setRange(lower, upper);

    }

}
