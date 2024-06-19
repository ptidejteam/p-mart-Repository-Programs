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
 * --------------
 * DateRange.java
 * --------------
 * (C) Copyright 2002, 2003, by Object Refinery Limited and Contributors.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited);
 * Contributor(s):   Bill Kelemen;
 *
 * $Id: DateRange.java,v 1.1 2007/10/10 19:05:01 vauchers Exp $
 *
 * Changes (from 23-Jun-2001)
 * --------------------------
 * 22-Apr-2002 : Version 1 based on code by Bill Kelemen (DG);
 * 07-Oct-2002 : Fixed errors reported by Checkstyle (DG);
 *
 */

package org.jfree.data;

import java.util.Date;

/**
 * A range specified in terms of two <code>java.util.Date</code> objects.
 * <p>
 * Instances of this class are immutable.
 *
 * @author David Gilbert
 * @author Bill Kelemen
 */
public class DateRange extends Range {

    /** The lower bound for the range. */
    private Date lowerDate;

    /** The upper bound for the range. */
    private Date upperDate;

    /**
     * Default constructor.
     */
    public DateRange() {

        this(new Date(0), new Date(1));

    }

    /**
     * Constructs a new range.
     *
     * @param lower  the lower bound.
     * @param upper  the upper bound.
     */
    public DateRange(Date lower, Date upper) {

        super(lower.getTime(), upper.getTime());
        this.lowerDate = lower;
        this.upperDate = upper;

    }

    /**
     * Constructs a new range.
     *
     * @param lower  the lower (oldest) date.
     * @param upper  the upper (most recent) date.
     */
    public DateRange(double lower, double upper) {

        super(lower, upper);
        this.lowerDate = new Date((long) lower);
        this.upperDate = new Date((long) upper);


    }

    /**
     * Constructs a new range based on another range.
     * <P>
     * The other range may not be a {@link DateRange}.  If it is not, the upper
     * and lower bounds are evaluated as milliseconds since midnight
     * GMT, 1-Jan-1970.
     *
     * @param other  the other range.
     */
    public DateRange(Range other) {

        this(other.getLowerBound(), other.getUpperBound());

    }

    /**
     * Returns the lower bound for the range.
     *
     * @return the lower bound for the range.
     */
    public Date getLowerDate() {
        return this.lowerDate;
    }

    /**
     * Returns the upper bound for the range.
     *
     * @return the upper bound for the range.
     */
    public Date getUpperDate() {
        return this.upperDate;
    }

}
