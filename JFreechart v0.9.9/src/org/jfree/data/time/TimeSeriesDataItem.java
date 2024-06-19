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
 * -----------------------
 * TimeSeriesDataItem.java
 * -----------------------
 * (C) Copyright 2001-2003, by Object Refinery Limited.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited);
 * Contributor(s):   -;
 *
 * $Id: TimeSeriesDataItem.java,v 1.1 2007/10/10 20:07:31 vauchers Exp $
 *
 * Changes
 * -------
 * 11-Oct-2001 : Version 1 (DG);
 * 15-Nov-2001 : Updated Javadoc comments (DG);
 * 29-Nov-2001 : Added cloning (DG);
 * 24-Jun-2002 : Removed unnecessary import (DG);
 * 07-Oct-2002 : Fixed errors reported by Checkstyle (DG);
 * 13-Mar-2003 : Renamed TimeSeriesDataPair --> TimeSeriesDataItem, moved to
 *               com.jrefinery.data.time package, implemented Serializable (DG)
 */

package org.jfree.data.time;

import java.io.Serializable;

/**
 * Represents one data item in a time series.
 * <P>
 * The time period can be any of the following:
 * <ul>
 * <li>Year</li>
 * <li>Quarter</li>
 * <li>Month</li>
 * <li>Week</li>
 * <li>Day</li>
 * <li>Hour</li>
 * <li>Minute</li>
 * <li>Second</li>
 * <li>Millisecond</li>
 * <li>FixedMillisecond</li>
 * </ul>
 *
 * The time period is an immutable property of the data item.  Data items will
 * often be sorted within a list, and allowing the time period to be changed
 * could destroy the sort order.
 * <P>
 * Implements the <code>Comparable</code> interface so that standard Java sorting can be
 * used to keep the data items in order.
 *
 * @author David Gilbert
 */
public class TimeSeriesDataItem implements Cloneable, Comparable, Serializable {

    /** The time period. */
    private RegularTimePeriod period;

    /** The value associated with the time period. */
    private Number value;

    /**
     * Constructs a new data pair.
     *
     * @param period  the time period.
     * @param value  the value associated with the time period.
     */
    public TimeSeriesDataItem(RegularTimePeriod period, Number value) {

        this.period = period;
        this.value = value;

    }

    /**
     * Constructs a new data pair.
     *
     * @param period  the time period.
     * @param value  the value associated with the time period.
     */
    public TimeSeriesDataItem(RegularTimePeriod period, double value) {

        this(period, new Double(value));

    }

    /**
     * Clones the data pair.
     * <P>
     * Notes:
     * --> no need to clone the period or value since they are immutable classes.
     *
     * @return a clone of this data pair.
     */
    public Object clone() {

        Object clone = null;

        try {
            clone = super.clone();
        }
        catch (CloneNotSupportedException e) { // won't get here...
            System.err.println("TimeSeriesDataPair.clone(): operation not supported.");
        }

        return clone;

    }

    /**
     * Returns the time period.
     *
     * @return the time period.
     */
    public RegularTimePeriod getPeriod() {
        return this.period;
    }

    /**
     * Returns the value.
     *
     * @return the value.
     */
    public Number getValue() {
        return this.value;
    }

    /**
     * Sets the value for this data pair.
     *
     * @param value  the new value.
     */
    public void setValue(Number value) {
        this.value = value;
    }

    /**
     * Tests this object for equality with the target object.
     *
     * @param target  the other object.
     *
     * @return A boolean.
     */
    public boolean equals(Object target) {

        boolean result = false;

        if (this == target) {
            result = true;
        }
        else {
            if (target instanceof TimeSeriesDataItem) {
                TimeSeriesDataItem item = (TimeSeriesDataItem) target;
                Number value = item.getValue();
                boolean sameValues;
                if (this.value == null) {
                    sameValues = (value == null);
                }
                else {
                    sameValues = this.value.equals(value);
                }
                result = this.period.equals(item.getPeriod()) && sameValues;
            }
        }

        return result;

    }

    /**
     * Returns an integer indicating the order of this data pair object
     * relative to another object.
     * <P>
     * For the order we consider only the timing:
     * negative == before, zero == same, positive == after.
     *
     * @param o1  The object being compared to.
     *
     * @return  An integer indicating the order of the data pair object relative to another object.
     */
    public int compareTo(Object o1) {

        int result;

        // CASE 1 : Comparing to another TimeSeriesDataPair object
        // -------------------------------------------------------
        if (o1 instanceof TimeSeriesDataItem) {
            TimeSeriesDataItem datapair = (TimeSeriesDataItem) o1;
            result = getPeriod().compareTo(datapair.getPeriod());
        }

        // CASE 2 : Comparing to a general object
        // ---------------------------------------------
        else {
            // consider time periods to be ordered after general objects
            result = 1;
        }

        return result;

    }

}
