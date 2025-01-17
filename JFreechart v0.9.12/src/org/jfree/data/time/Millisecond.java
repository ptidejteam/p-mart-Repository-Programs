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
 * ----------------
 * Millisecond.java
 * ----------------
 * (C) Copyright 2001-2003, by Object Refinery Limited.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited);
 * Contributor(s):   -;
 *
 * $Id: Millisecond.java,v 1.1 2007/10/10 19:12:26 vauchers Exp $
 *
 * Changes
 * -------
 * 11-Oct-2001 : Version 1 (DG);
 * 19-Dec-2001 : Added new constructors as suggested by Paul English (DG);
 * 26-Feb-2002 : Added new getStart(...) and getEnd(...) methods (DG);
 * 29-Mar-2002 : Fixed bug in getStart(...), getEnd(...) and compareTo(...) methods (DG);
 * 10-Sep-2002 : Added getSerialIndex() method (DG);
 * 07-Oct-2002 : Fixed errors reported by Checkstyle (DG);
 * 10-Jan-2003 : Changed base class and method names (DG);
 * 13-Mar-2003 : Moved to com.jrefinery.data.time package and implemented Serializable (DG);
 *
 */

package org.jfree.data.time;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * Represents a millisecond.
 * <P>
 * This class is immutable, which is a requirement for all {@link RegularTimePeriod} subclasses.
 *
 * @author David Gilbert
 */
public class Millisecond extends RegularTimePeriod implements Serializable {

    /** A constant for the first millisecond in a second. */
    public static final int FIRST_MILLISECOND_IN_SECOND = 0;

    /** A constant for the last millisecond in a second. */
    public static final int LAST_MILLISECOND_IN_SECOND = 999;

    /** The millisecond. */
    private int millisecond;

    /** The second. */
    private Second second;

    /**
     * Constructs a millisecond based on the current system time.
     */
    public Millisecond() {
        this(new Date());
    }

    /**
     * Constructs a millisecond.
     *
     * @param millisecond  the millisecond (0-999).
     * @param second  the second.
     */
    public Millisecond(int millisecond, Second second) {

        this.millisecond = millisecond;
        this.second = second;

    }

    /**
     * Constructs a millisecond.
     *
     * @param time  the time.
     */
    public Millisecond(Date time) {
        this(time, RegularTimePeriod.DEFAULT_TIME_ZONE);
    }

    /**
     * Creates a millisecond.
     *
     * @param time  the instant in time.
     * @param zone  the time zone.
     */
    public Millisecond(Date time, TimeZone zone) {

        this.second = new Second(time, zone);

        Calendar calendar = Calendar.getInstance(zone);
        calendar.setTime(time);
        this.millisecond = calendar.get(Calendar.MILLISECOND);

    }

    /**
     * Returns the second.
     *
     * @return The second.
     */
    public Second getSecond() {
        return this.second;
    }

    /**
     * Returns the millisecond.
     *
     * @return the millisecond.
     */
    public long getMillisecond() {
        return this.millisecond;
    }

    /**
     * Returns the millisecond preceding this one.
     *
     * @return the millisecond preceding this one.
     */
    public RegularTimePeriod previous() {

        RegularTimePeriod result = null;

        if (this.millisecond != FIRST_MILLISECOND_IN_SECOND) {
            result = new Millisecond(this.millisecond - 1, this.second);
        }
        else {
            Second previous = (Second) this.second.previous();
            if (previous != null) {
                result = new Millisecond(LAST_MILLISECOND_IN_SECOND, previous);
            }
        }
        return result;

    }

    /**
     * Returns the millisecond following this one.
     *
     * @return the millisecond following this one.
     */
    public RegularTimePeriod next() {

        RegularTimePeriod result = null;

        if (this.millisecond != LAST_MILLISECOND_IN_SECOND) {
            result = new Millisecond(this.millisecond + 1, this.second);
        }
        else {
            Second next = (Second) this.second.next();
            if (next != null) {
                result = new Millisecond(FIRST_MILLISECOND_IN_SECOND, next);
            }
        }
        return result;

    }

    /**
     * Returns a serial index number for the millisecond.
     *
     * @return The serial index number.
     */
    public long getSerialIndex() {
        return this.second.getSerialIndex() * 1000L + this.millisecond;
    }

    /**
     * Tests the equality of this object against an arbitrary Object.
     * <P>
     * This method will return true ONLY if the object is a Millisecond object
     * representing the same millisecond as this instance.
     *
     * @param object  the object to compare
     *
     * @return <code>true</code> if milliseconds and seconds of this and object
     *      are the same.
     */
    public boolean equals(Object object) {

        if (object instanceof Millisecond) {
            Millisecond m = (Millisecond) object;
            return ((this.millisecond == m.getMillisecond())
                    && (this.second.equals(m.getSecond())));
        }
        else {
            return false;
        }

    }

    /**
     * Returns an integer indicating the order of this Millisecond object
     * relative to the specified object:
     *
     * negative == before, zero == same, positive == after.
     *
     * @param o1  the object to compare
     *
     * @return negative == before, zero == same, positive == after.
     */
    public int compareTo(Object o1) {

        int result;
        long difference;

        // CASE 1 : Comparing to another Second object
        // -------------------------------------------
        if (o1 instanceof Millisecond) {
            Millisecond ms = (Millisecond) o1;
            difference = getFirstMillisecond() - ms.getFirstMillisecond();
            if (difference > 0) {
                result = 1;
            }
            else {
                if (difference < 0) {
                    result = -1;
                }
                else {
                    result = 0;
                }
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
     * Returns the first millisecond of the time period.
     *
     * @return the first millisecond of the time period.
     */
    public long getFirstMillisecond() {
        return this.second.getFirstMillisecond() + this.millisecond;
    }

    /**
     * Returns the first millisecond of the time period.
     *
     * @param calendar  the calendar.
     *
     * @return the first millisecond of the time period.
     */
    public long getFirstMillisecond(Calendar calendar) {

        return this.second.getFirstMillisecond(calendar) + this.millisecond;

    }

    /**
     * Returns the last millisecond of the time period.
     *
     * @return the last millisecond of the time period.
     */
    public long getLastMillisecond() {
        return this.second.getFirstMillisecond() + millisecond;
    }

    /**
     * Returns the last millisecond of the time period.
     *
     * @param calendar  the calendar.
     *
     * @return the last millisecond of the time period.
     */
    public long getLastMillisecond(Calendar calendar) {

        return this.second.getFirstMillisecond(calendar) + this.millisecond;

    }

}
