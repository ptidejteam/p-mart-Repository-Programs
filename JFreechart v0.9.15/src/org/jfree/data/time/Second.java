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
 * -----------
 * Second.java
 * -----------
 * (C) Copyright 2001-2003, by Object Refinery Limited.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited);
 * Contributor(s):   -;
 *
 * $Id: Second.java,v 1.1 2007/10/10 19:21:58 vauchers Exp $
 *
 * Changes
 * -------
 * 11-Oct-2001 : Version 1 (DG);
 * 18-Dec-2001 : Changed order of parameters in constructor (DG);
 * 19-Dec-2001 : Added a new constructor as suggested by Paul English (DG);
 * 14-Feb-2002 : Fixed bug in Second(Date) constructor, and changed start of range to zero from
 *               one (DG);
 * 26-Feb-2002 : Changed getStart(), getMiddle() and getEnd() methods to evaluate with reference
 *               to a particular time zone (DG);
 * 13-Mar-2002 : Added parseSecond() method (DG);
 * 10-Sep-2002 : Added getSerialIndex() method (DG);
 * 07-Oct-2002 : Fixed errors reported by Checkstyle (DG);
 * 10-Jan-2003 : Changed base class and method names (DG);
 * 05-Mar-2003 : Fixed bug in getLastMillisecond(...) picked up in JUnit tests (DG);
 * 13-Mar-2003 : Moved to com.jrefinery.data.time package and implemented Serializable (DG);
 * 21-Oct-2003 : Added hashCode() method (DG);
 *
 */

package org.jfree.data.time;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * Represents a second in a particular day.
 * <P>
 * This class is immutable, which is a requirement for all {@link RegularTimePeriod} subclasses.
 *
 * @author David Gilbert
 *
 */
public class Second extends RegularTimePeriod implements Serializable {

    /** Useful constant for the first second in a day. */
    public static final int FIRST_SECOND_IN_MINUTE = 0;

    /** Useful constant for the last second in a day. */
    public static final int LAST_SECOND_IN_MINUTE = 59;

    /** The day. */
    private Minute minute;

    /** The second. */
    private int second;

    /**
     * Constructs a new Second, based on the system date/time.
     */
    public Second() {

        this(new Date());

    }

    /**
     * Constructs a new Second.
     *
     * @param second  the second (0 to 24*60*60-1).
     * @param minute  the minute.
     */
    public Second(int second, Minute minute) {

        this.minute = minute;
        this.second = second;

    }

    /**
     * Creates a new second.
     * 
     * @param second  the second (0-59).
     * @param minute  the minute (0-59).
     * @param hour  the hour (0-23).
     * @param day  the day (1-31).
     * @param month  the month (1-12).
     * @param year  the year (1900-9999).
     */
    public Second(int second, int minute, int hour, int day, int month, int year) {
        this(second, new Minute(minute, hour, day, month, year));    
    }
    
    /**
     * Constructs a second.
     *
     * @param time  the time.
     */
    public Second(Date time) {
        this(time, RegularTimePeriod.DEFAULT_TIME_ZONE);
    }

    /**
     * Creates a new second based on the supplied time and time zone.
     *
     * @param time  the instant in time.
     * @param zone  the time zone.
     */
    public Second(Date time, TimeZone zone) {

        this.minute = new Minute(time, zone);

        Calendar calendar = Calendar.getInstance(zone);
        calendar.setTime(time);
        this.second = calendar.get(Calendar.SECOND);

    }

    /**
     * Returns the second.
     *
     * @return the second.
     */
    public int getSecond() {
        return this.second;
    }

    /**
     * Returns the minute.
     *
     * @return the minute.
     */
    public Minute getMinute() {
        return this.minute;
    }

    /**
     * Returns the second preceding this one.
     *
     * @return the second preceding this one.
     */
    public RegularTimePeriod previous() {

        Second result = null;

        if (this.second != FIRST_SECOND_IN_MINUTE) {
            result = new Second(second - 1, this.minute);
        }
        else {
            Minute previous = (Minute) this.minute.previous();
            if (previous != null) {
                result = new Second(LAST_SECOND_IN_MINUTE, previous);
            }
        }

        return result;

    }

    /**
     * Returns the second following this one.
     *
     * @return the second following this one.
     */
    public RegularTimePeriod next() {

        Second result = null;

        if (this.second != LAST_SECOND_IN_MINUTE) {
            result = new Second(second + 1, this.minute);
        }
        else {
            Minute next = (Minute) this.minute.next();
            if (next != null) {
                result = new Second(FIRST_SECOND_IN_MINUTE, next);
            }
        }

        return result;

    }

    /**
     * Returns a serial index number for the minute.
     *
     * @return the serial index number.
     */
    public long getSerialIndex() {
        return this.minute.getSerialIndex() * 60L + this.second;
    }

    /**
     * Returns the first millisecond of the minute.
     *
     * @param calendar  the calendar/timezone.
     *
     * @return  the first millisecond.
     */
    public long getFirstMillisecond(Calendar calendar) {
        return this.minute.getFirstMillisecond(calendar) + second * 1000L;
    }

    /**
     * Returns the last millisecond of the second.
     *
     * @param calendar  the calendar/timezone.
     *
     * @return the last millisecond.
     */
    public long getLastMillisecond(Calendar calendar) {
        return this.minute.getFirstMillisecond(calendar) + second * 1000L + 999L;
    }

    /**
     * Tests the equality of this object against an arbitrary Object.
     * <P>
     * This method will return true ONLY if the object is a Second object
     * representing the same second as this instance.
     *
     * @param object  the object to compare.
     *
     * @return <code>true</code> if second and minute of this and the object are the same.
     */
    public boolean equals(Object object) {
        if (object instanceof Second) {
            Second s = (Second) object;
            return ((this.second == s.getSecond()) && (this.minute.equals(s.getMinute())));
        }
        else {
            return false;
        }
    }

    /**
     * Returns a hash code for this object instance.
     * <p>
     * The approach described by Joshua Bloch in "Effective Java" has been used here:
     * <p>
     * <code>http://developer.java.sun.com/developer/Books/effectivejava/Chapter3.pdf</code>
     * 
     * @return A hash code.
     */
    public int hashCode() {
        int result = 17;
        result = 37 * result + this.second;
        result = 37 * result + this.minute.hashCode();
        return result;
    }

    /**
     * Returns an integer indicating the order of this Second object relative
     * to the specified
     * object: negative == before, zero == same, positive == after.
     *
     * @param o1  the object to compare.
     *
     * @return negative == before, zero == same, positive == after.
     */
    public int compareTo(Object o1) {

        int result;

        // CASE 1 : Comparing to another Second object
        // -------------------------------------------
        if (o1 instanceof Second) {
            Second s = (Second) o1;
            result = this.minute.compareTo(s.minute);
            if (result == 0) {
                result = this.second - s.second;
            }
        }

        // CASE 2 : Comparing to another TimePeriod object
        // -----------------------------------------------
        else if (o1 instanceof RegularTimePeriod) {
            // more difficult case - evaluate later...
            result = 0;
        }

        // CASE 3 : Comparing to a non-TimePeriod object
        // ---------------------------------------------
        else {
            // consider time periods to be ordered after general objects
            result = 1;
        }

        return result;

    }

    /**
     * Creates a Minute instance by parsing a string.  The string is assumed to
     * be in the format "YYYY-MM-DD HH:MM:SS", perhaps with leading or trailing
     * whitespace.
     *
     * @param s  the string to parse.
     *
     * @return <code>null</code> if the string is not parseable, the Second
     *      otherwise.
     */
    public static Second parseSecond(String s) {

        Second result = null;
        s = s.trim();

        String daystr = s.substring(0, Math.min(10, s.length()));
        Day day = Day.parseDay(daystr);
        if (day != null) {
            String hmsstr = s.substring(Math.min(daystr.length() + 1, s.length()),
                                        s.length());
            hmsstr = hmsstr.trim();

            int l = hmsstr.length();
            String hourstr = hmsstr.substring(0, Math.min(2, l));
            String minstr = hmsstr.substring(Math.min(3, l), Math.min(5, l));
            String secstr = hmsstr.substring(Math.min(6, l), Math.min(8, l));
            int hour = Integer.parseInt(hourstr);

            if ((hour >= 0) && (hour <= 23)) {

                int minute = Integer.parseInt(minstr);
                if ((minute >= 0) && (minute <= 59)) {

                    Minute m = new Minute(minute, new Hour(hour, day));
                    int second = Integer.parseInt(secstr);
                    if ((second >= 0) && (second <= 59)) {
                        result = new Second(second, m);
                    }
                }
            }
        }

        return result;

    }

}
