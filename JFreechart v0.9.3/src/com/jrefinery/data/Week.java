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
 * Week.java
 * ---------
 * (C) Copyright 2001, 2002, by Simba Management Limited.
 *
 * Original Author:  David Gilbert (for Simba Management Limited);
 * Contributor(s):   -;
 *
 * $Id: Week.java,v 1.1 2007/10/10 19:52:14 vauchers Exp $
 *
 * Changes
 * -------
 * 11-Oct-2001 : Version 1 (DG);
 * 18-Dec-2001 : Changed order of parameters in constructor (DG);
 * 19-Dec-2001 : Added a new constructor as suggested by Paul English (DG);
 * 29-Jan-2002 : Worked on the parseWeek(...) method (DG);
 * 13-Feb-2002 : Fixed bug in Week(Date) constructor (DG);
 * 26-Feb-2002 : Changed getStart(), getMiddle() and getEnd() methods to evaluate with reference
 *               to a particular time zone (DG);
 * 05-Apr-2002 : Reinstated this class to the JCommon library (DG);
 * 24-Jun-2002 : Removed unnecessary main method (DG);
 *
 */

package com.jrefinery.data;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import com.jrefinery.date.SerialDate;

/**
 * Represents a week within a particular year.
 * <P>
 * This class is immutable, which is a requirement for all TimePeriod
 * subclasses.
 */
public class Week extends TimePeriod {

    /** Constant for the first week in the year. */
    public static int FIRST_WEEK_IN_YEAR = 1;

    /** Constant for the last week in the year. */
    public static int LAST_WEEK_IN_YEAR = 52;

//    /** The number of milliseconds per week. */
//    private static long MILLISECONDS_PER_WEEK = 7 * 24 * 60 * 60 * 1000;

    /** The year in which the week falls. */
    protected Year year;

    /** The week (1-52). */
    protected int week;

    /**
     * Constructs a Week, based on the system date/time.
     */
    public Week() {

        this(new Date());

    }

    /**
     * Constructs a time period representing the week in the specified year.
     *
     * @param week      The week (1 to 52).
     * @param year      The year (1900 to 9999).
     */
    public Week(int week, int year) {

        this(week, new Year(year));

    }

    /**
     * Constructs a time period representing the week in the specified year.
     *
     * @param week      The week (1 to 52).
     * @param year      The year (1900 to 9999).
     */
    public Week(int week, Year year) {

        if ((week<1) && (week>52)) {
            throw new IllegalArgumentException(
                "Week(...): week outside valid range.");
        }

        this.week = week;
        this.year = year;

    }

    /**
     * Constructs a time period representing a week.
     *
     * @param time   The time.
     */
    public Week(Date time) {

        this(time, TimePeriod.DEFAULT_TIME_ZONE);

    }

    /**
     * Constructs a Week, based on a date/time and a time zone.
     *
     * @param time      The date/time.
     * @param zone      The time zone.
     */
    public Week(Date time, TimeZone zone) {

        Calendar calendar = Calendar.getInstance(zone);
        calendar.setTime(time);
        this.week = Math.min(calendar.get(Calendar.WEEK_OF_YEAR), 52);
        this.year = new Year(calendar.get(Calendar.YEAR));

    }

    /**
     * Returns the year in which the week falls.
     *
     * @return The year.
     */
    public Year getYear() {
        return this.year;
    }

    /**
     * Returns the year in which the week falls, as an integer value.
     *
     * @return The year.
     */
    public int getYearValue() {
        return this.year.getYear();
    }

    /**
     * Returns the week.
     *
     * @return The week.
     */
    public int getWeek() {
        return this.week;
    }

    /**
     * Returns the week preceding this one.
     *
     * @return The preceding week.
     */
    public TimePeriod previous() {

        Week result;
        if (this.week!=FIRST_WEEK_IN_YEAR) {
            result = new Week(week-1, year);
        }
        else {
            Year prevYear = (Year)year.previous();
            if (prevYear!=null) result = new Week(LAST_WEEK_IN_YEAR, prevYear);
            else result = null;
        }
        return result;

    }

    /**
     * Returns the week following this one.
     *
     * @return The following week.
     */
    public TimePeriod next() {

        Week result;
        if (week != LAST_WEEK_IN_YEAR) {
            result = new Week(week+1, year);
        }
        else {
            Year nextYear = (Year)year.next();
            if (nextYear!=null) result = new Week(FIRST_WEEK_IN_YEAR, nextYear);
            else result = null;
        }
        return result;

    }

    /**
     * Returns a string representing the week (e.g. "Week 9, 2002").
     * <P>
     * To do: look at internationalisation.
     * @return A string representing the week.
     */
    public String toString() {
        return "Week "+week+", "+year;
    }

    /**
     * Tests the equality of this Week object to an arbitrary object.  Returns
     * true if the target is a Week instance representing the same week as this
     * object.  In all other cases, returns false.
     * @param obj The object.
     *
     * @return <code>true</code> if week and year of this and object are the
     *      same.
     */
    public boolean equals(Object obj) {

        if (obj!=null) {
            if (obj instanceof Week) {
                Week target = (Week)obj;
                return ((week==target.getWeek())
                    && (year.equals(target.getYear())));
            }
            else return false;
        }
        else return false;

    }

    /**
     * Returns an integer indicating the order of this Week object relative to
     * the specified object:
     * negative == before, zero == same, positive == after.
     * @param o1    object to compare.
     * @return negative == before, zero == same, positive == after.
     */
    public int compareTo(Object o1) {

        int result;

        // CASE 1 : Comparing to another Week object
        // --------------------------------------------
        if (o1 instanceof Week) {
            Week w = (Week)o1;
            result = this.year.getYear() - w.getYear().getYear();
            if (result == 0) result = this.week - w.getWeek();
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

    /**
     * Returns the first millisecond of the week, evaluated using the supplied
     * calendar (which determines the time zone).
     *
     * @param calendar      The calendar.
     * @return The first millisecond of the week.
     */
    public long getStart(Calendar calendar) {

        SerialDate jan1 = SerialDate.createInstance(1, 1, year.getYear());
        SerialDate startOfWeek = SerialDate.addDays((this.week-1)*7, jan1);
        Day first = new Day(startOfWeek);
        return first.getStart(calendar);

    }

    /**
     * Returns the last millisecond of the week, evaluated using the supplied
     * calendar (which determines the time zone).
     *
     * @param calendar The calendar.
     * @return The last millisecond of the week.
     */
    public long getEnd(Calendar calendar) {

        if (this.week==Week.LAST_WEEK_IN_YEAR) {
            return this.year.getEnd(calendar);
        }
        else {
            SerialDate jan1 = SerialDate.createInstance(1, 1, year.getYear());
            SerialDate endOfWeek = SerialDate.addDays((this.week)*7-1, jan1);
            Day last = new Day(endOfWeek);
            return last.getEnd(calendar);
        }

    }

    /**
     * Parses the string argument as a week.
     * <P>
     * This method is required to accept the format "YYYY-Wnn".  It will also
     * accept "Wnn-YYYY". Anything else, at the moment, is a bonus.
     *
     * @param s     string to parse
     * @return <code>null</code> if the string is not parseable, the week
     *      otherwise.
     * @throws TimePeriodFormatException
     */
    public static Week parseWeek(String s) throws TimePeriodFormatException {

        Week result = null;
        if (s!=null) {

            // trim whitespace from either end of the string
            s = s.trim();

            int i = Week.findSeparator(s);
            if (i!=-1) {
                String s1 = s.substring(0, i).trim();
                String s2 = s.substring(i+1, s.length()).trim();

                Year year = Week.evaluateAsYear(s1);
                int week;
                if (year!=null) {
                    week = Week.stringToWeek(s2);
                    if (week==-1) {
                        throw new TimePeriodFormatException(
                            "Week.parseWeek(String): can't evaluate the week.");
                    }
                    result = new Week(week, year);
                }
                else {
                    year = Week.evaluateAsYear(s2);
                    if (year!=null) {
                        week = Week.stringToWeek(s1);
                        if (week==-1) {
                            throw new TimePeriodFormatException(
                                "Week.parseWeek(String): can't "
                                + "evaluate the week.");
                        }
                        result = new Week(week, year);
                    }
                    else {
                        throw new TimePeriodFormatException(
                            "Week.parseWeek(String): can't evaluate the year.");
                    }
                }

            }
            else {
                throw new TimePeriodFormatException(
                    "Week.parseWeek(String): could not find separator.");
            }

        }
        return result;

    }

    /**
     * Finds the first occurrence of ' ', '-', ',' or '.'
     *
     * @param s     the string to parse.
     *
     * @return <code>-1</code> if none of the characters was found, the
     *      index of the first occurrence otherwise.
     */
    private static int findSeparator(String s) {

        int result = -1;
        result = s.indexOf('-');
        if (result==-1) {
            result = s.indexOf(',');
        }
        if (result==-1) {
            result = s.indexOf(' ');
        }
        if (result==-1) {
            result = s.indexOf('.');
        }
        return result;
    }

    /**
     * Creates a year from a string, or returns null (format exceptions
     * suppressed).
     *
     * @param s     string to parse.
     * @return <ocde>null</code> if the string is not parseable, the year
     *      otherwise.
     */
    private static Year evaluateAsYear(String s) {

        Year result = null;
        try {
            result = Year.parseYear(s);
        }
        catch (TimePeriodFormatException e) {
            // suppress
        }
        return result;

    }

    /**
     * Converts a string to a week.
     * @param s     the string to parse.
     * @return <code>-1</code> if the string does not contain a week number,
     *      the number of the week otherwise.
     */
    private static int stringToWeek(String s) {

        int result = -1;
        s = s.replace('W', ' ');
        s = s.trim();
        try {
            result = Integer.parseInt(s);
            if ((result<1) || (result>52)) {
                result = -1;
            }
        }
        catch(NumberFormatException e) {
            // suppress
        }
        return result;

    }

}
