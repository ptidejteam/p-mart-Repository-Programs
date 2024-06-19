/* ============================================
 * JFreeChart : a free Java chart class library
 * ============================================
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
 * ---------
 * Hour.java
 * ---------
 * (C) Copyright 2001, 2002, by Simba Management Limited.
 *
 * Original Author:  David Gilbert (for Simba Management Limited);
 * Contributor(s):   -;
 *
 * $Id: Hour.java,v 1.1 2007/10/10 19:52:14 vauchers Exp $
 *
 * Changes
 * -------
 * 11-Oct-2001 : Version 1 (DG);
 * 18-Dec-2001 : Changed order of parameters in constructor (DG);
 * 19-Dec-2001 : Added a new constructor as suggested by Paul English (DG);
 * 14-Feb-2002 : Fixed bug in Hour(Date) constructor (DG);
 * 26-Feb-2002 : Changed getStart(), getMiddle() and getEnd() methods to evaluate with reference
 *               to a particular time zone (DG);
 * 15-Mar-2002 : Changed API (DG);
 * 16-Apr-2002 : Fixed small time zone bug in constructor (DG);
 *
 */

package com.jrefinery.data;

import java.util.Date;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;

/**
 * Represents an hour in a specific day.
 * <P>
 * This class is immutable, which is a requirement for all TimePeriod
 * subclasses.
 */
public class Hour extends TimePeriod {

    /** Useful constant for the first hour in the day. */
    private static int FIRST_HOUR_IN_DAY = 0;

    /** Useful constant for the last hour in the day. */
    private static int LAST_HOUR_IN_DAY = 23;

    /** The day. */
    protected Day day;

    /** The hour. */
    protected int hour;

    /**
     * Constructs a new Hour, based on the system date/time.
     */
    public Hour() {

        this(new Date());

    }

    /**
     * Constructs a new Hour.
     *
     * @param hour      The hour (in the range 0 to 23).
     * @param day       The day.
     */
    public Hour(int hour, Day day) {

        this.hour = hour;
        this.day = day;

    }

    /**
     * Constructs a new Hour, based on the supplied date/time.
     *
     * @param time      The date-time.
     */
    public Hour(Date time) {

        this(time, TimePeriod.DEFAULT_TIME_ZONE);

    }

    /**
     * Constructs a new Hour, based on the supplied date/time evaluated in the
     * specified time zone.
     *
     * @param time      The date-time.
     * @param zone      The time zone.
     */
    public Hour(Date time, TimeZone zone) {

        Calendar calendar = Calendar.getInstance(zone);
        calendar.setTime(time);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        this.hour = hour;
        this.day = new Day(time, zone);

    }

    /**
     * Returns the hour.
     *
     * @return the hour.
     */
    public int getHour() {
        return this.hour;
    }

    /**
     * Returns the day in which this hour falls.
     * @return The day.
     */
    public Day getDay() {
        return this.day;
    }

    /**
     * Returns the year in which this hour falls.
     * @return The year.
     */
    public int getYear() {
        return this.day.getYear();
    }

    /**
     * Returns the month in which this hour falls.
     * @return The month.
     */
    public int getMonth() {
        return this.day.getMonth();
    }

    /**
     * Returns the day-of-the-month in which this hour falls.
     * @return The day-of-the-month.
     */
    public int getDayOfMonth() {
        return this.day.getDayOfMonth();
    }

    /**
     * Returns the hour preceding this one.
     *
     * @return the hour preceding this one.
     */
    public TimePeriod previous() {

        Hour result;
        if (this.hour != FIRST_HOUR_IN_DAY) {
            result = new Hour(hour-1, this.day);
        }
        else { // we are at the first hour in the day...
            Day prevDay = (Day)day.previous();
            if (prevDay!=null) result = new Hour(LAST_HOUR_IN_DAY, prevDay);
            else result = null;
        }
        return result;

    }

    /**
     * Returns the hour following this one.
     * @return the hour following this one.
     */
    public TimePeriod next() {

        Hour result;
        if (this.hour != LAST_HOUR_IN_DAY) {
            result = new Hour(hour+1, this.day);
        }
        else { // we are at the last hour in the day...
            Day nextDay = (Day)day.next();
            if (nextDay!=null) result = new Hour(FIRST_HOUR_IN_DAY, nextDay);
            else result = null;
        }
        return result;

    }

    /**
     * Tests the equality of this object against an arbitrary Object.
     * <P>
     * This method will return true ONLY if the object is an Hour object
     * representing the same hour as this instance.
     *
     * @param object    the object to compare.
     *
     * @return <code>true</code> if the hour and day value of the object
     *      is the same as this.
     */
    public boolean equals(Object object) {
        if (object instanceof Hour) {
            Hour h = (Hour)object;
            return ((this.hour==h.getHour()) && (this.day.equals(h.getDay())));
        }
        else
            return false;
    }

    /**
     * Returns an integer indicating the order of this Hour object relative to
     * the specified
     * object: negative == before, zero == same, positive == after.
     *
     * @param o1    the object to compare.
     *
     * @return negative == before, zero == same, positive == after.
     */
    public int compareTo(Object o1) {

        int result;

        // CASE 1 : Comparing to another Hour object
        // -----------------------------------------
        if (o1 instanceof Hour) {
            Hour h = (Hour)o1;
            result = this.getDay().compareTo(h.getDay());
            if (result == 0) result = this.hour - h.getHour();
        }

        // CASE 2 : Comparing to another TimePeriod object
        // -----------------------------------------------
        else if (o1 instanceof TimePeriod) {
            // more difficult case - evaluate later...
            result = 0;
        }

        // CASE 3 : Comparing to a non-TimePeriod object
        // ---------------------------------------------
        else
            // consider time periods to be ordered after general objects
            result = 1;

        return result;

    }


    public long getStart(Calendar calendar) {

        int year = this.day.getYear();
        int month = this.day.getMonth()-1;
        int day = this.day.getDayOfMonth();

        calendar.set(year, month, day, hour, 0, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        return calendar.getTime().getTime();

    }

    public long getEnd(Calendar calendar) {

        int year = this.day.getYear();
        int month = this.day.getMonth()-1;
        int day = this.day.getDayOfMonth();

        calendar.set(year, month, day, hour, 59, 59);
        calendar.set(Calendar.MILLISECOND, 999);

        return calendar.getTime().getTime();

    }

    /**
     * Creates an Hour instance by parsing a string.  The string is assumed to
     * be in the format "YYYY-MM-DD HH", perhaps with leading or trailing
     * whitespace.
     *
     * @param s     the hour string to parse.
     * @return <code>null</code> if the string is not parseable, the hour
     *      otherwise.
     */
    public static Hour parseHour(String s) {

        Hour result = null;
        s = s.trim();

        String daystr = s.substring(0, Math.min(10, s.length()));
        Day day = Day.parseDay(daystr);
        if (day!=null) {
            String hourstr = s.substring(Math.min(daystr.length()+1,
                s.length()), s.length());
            hourstr = hourstr.trim();
            int hour = Integer.parseInt(hourstr);
            // if the hour is 0 - 23 then create an hour
            if ((hour>=FIRST_HOUR_IN_DAY) && (hour<=LAST_HOUR_IN_DAY)) {
                result = new Hour(hour, day);
            }
        }

        return result;

    }

    /**
     * Test code - please ignore.
     *
     * @param args  no args honored.
     */
    public static void main(String[] args) {

        String[] ids = TimeZone.getAvailableIDs();

        for (int i=0; i<ids.length; i++) {
            System.out.println(ids[i]);
        }

        TimeZone zone = TimeZone.getTimeZone("Australia/Sydney");
        Calendar calendar = new GregorianCalendar(zone);
        calendar.clear();
        calendar.set(2002, 1, 21, 16, 0, 0);
        System.out.println("4pm on 21-Mar-2002 in Australia/Sydney: "
            + calendar.getTime().getTime());

        Date time = new Date(1014307200000L);
        calendar.setTime(time);
        Hour hour = new Hour(time);
        System.out.println("Hour = "+hour.toString());
        System.out.println("Start = "+hour.getStart(zone));
        System.out.println("End = "+hour.getEnd(zone));
        System.out.println("Offset = "+zone.getRawOffset());

    }

}
