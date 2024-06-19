/* ===========================================================
 * JFreeChart : a free chart library for the Java(tm) platform
 * ===========================================================
 *
 * (C) Copyright 2000-2004, by Object Refinery Limited and Contributors.
 *
 * Project Info:  http://www.jfree.org/jfreechart/index.html
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
 * [Java is a trademark or registered trademark of Sun Microsystems, Inc. 
 * in the United States and other countries.]
 *
 * ----------
 * Range.java
 * ----------
 * (C) Copyright 2002-2004, by Object Refinery Limited and Contributors.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited);
 * Contributor(s):   Chuanhao Chiu;
 *                   Bill Kelemen; 
 *                   Nicolas Brodu;
 *
 * $Id: Range.java,v 1.1 2007/10/10 19:29:13 vauchers Exp $
 *
 * Changes (from 23-Jun-2001)
 * --------------------------
 * 22-Apr-2002 : Version 1, loosely based by code by Bill Kelemen (DG);
 * 30-Apr-2002 : Added getLength() and getCentralValue() methods.  Changed argument check in
 *               constructor (DG);
 * 13-Jun-2002 : Added contains(double) method (DG);
 * 22-Aug-2002 : Added fix to combine method where both ranges are null, thanks to Chuanhao Chiu
 *               for reporting and fixing this (DG);
 * 07-Oct-2002 : Fixed errors reported by Checkstyle (DG);
 * 26-Mar-2003 : Implemented Serializable (DG);
 * 14-Aug-2003 : Added equals(...) method (DG);
 * 27-Aug-2003 : Added toString(...) method (BK);
 * 11-Sep-2003 : Added Clone Support (NB);
 * 23-Sep-2003 : Fixed Checkstyle issues (DG);
 * 25-Sep-2003 : Oops, Range immutable, clone not necessary (NB)
 *
 */

package org.jfree.data;

import java.io.Serializable;

/**
 * Represents the visible range for an axis.
 * <p>
 * Instances of this class are immutable.
 *
 * @author David Gilbert
 */
public strictfp class Range implements Serializable {

    /** The lower bound of the range. */
    private double lower;

    /** The upper bound of the range. */
    private double upper;

    /**
     * Created a new range.
     *
     * @param lower  the lower bound.
     * @param upper  the upper bound.
     */
    public Range(double lower, double upper) {

        if (lower > upper) {
            throw new IllegalArgumentException("Range(double, double): require lower<=upper.");
        }

        this.lower = lower;
        this.upper = upper;

    }

    /**
     * Returns the lower bound for the range.
     *
     * @return the lower bound.
     */
    public double getLowerBound() {
        return this.lower;
    }

    /**
     * Returns the upper bound for the range.
     *
     * @return the upper bound.
     */
    public double getUpperBound() {
        return this.upper;
    }

    /**
     * Returns the length of the range.
     *
     * @return the length.
     */
    public double getLength() {
        return this.upper - this.lower;
    }

    /**
     * Returns the central value for the range.
     *
     * @return the central value.
     */
    public double getCentralValue() {
        return this.lower / 2 + this.upper / 2;
    }

    /**
     * Returns true if the range contains the specified value.
     *
     * @param value  the value to lookup.
     *
     * @return <code>true</code> if the range contains the specified value.
     */
    public boolean contains(double value) {
        return (value >= this.lower && value <= this.upper);
    }

    /**
     * Creates a new range by combining two existing ranges.
     * <P>
     * Note that:
     * <ul>
     *   <li>either range can be null, in which case the other range is returned;</li>
     *   <li>if both ranges are null the return value is null.</li>
     * </ul>
     *
     * @param range1  the first range.
     * @param range2  the second range.
     *
     * @return a new range.
     */
    public static Range combine(Range range1, Range range2) {

        if (range1 == null) {
            return range2;
        }
        else {
            if (range2 == null) {
                return range1;
            }
            else {
                double l = Math.min(range1.getLowerBound(), range2.getLowerBound());
                double u = Math.max(range1.getUpperBound(), range2.getUpperBound());
                return new Range(l, u);
            }
        }
    }

    /**
     * Tests this object for equality with another object.
     *
     * @param object  the object to test against.
     *
     * @return <code>true</code> or <code>false</code>.
     */
    public boolean equals(Object object) {

        if (object == null) {
            return false;
        }

        if (object == this) {
            return true;
        }

        if ((object instanceof Range) == false) {
            return false;
        }

        // TODO : does this approximate equality make sense? 
        Range range = (Range) object;
        if ((Math.abs(this.lower - range.lower) < 0.000001) == false) {
            return false;
        }
        if ((Math.abs(this.upper - range.upper) < 0.000001) == false) {
            return false;
        }

        return true;

    }

    /**
     * Returns a hash code.
     * 
     * @return a hash code.
     */
    public int hashCode() {
        int result;
        long temp;
        temp = Double.doubleToLongBits(this.lower);
        result = (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(this.upper);
        result = 29 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }

    /**
     * Returns a string representation of this Range.
     *
     * @return a String "Range[lower,upper]" where lower=lower range and upper=upper range.
     */
    public String toString() {
        return ("Range[" + this.lower + "," + this.upper + "]");
    }

}
