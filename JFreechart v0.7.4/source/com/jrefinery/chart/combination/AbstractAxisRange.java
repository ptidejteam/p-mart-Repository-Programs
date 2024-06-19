/* =======================================
 * JFreeChart : a Java Chart Class Library
 * =======================================
 *
 * Project Info:  http://www.object-refinery.com/jfreechart
 * Project Lead:  David Gilbert (david.gilbert@jrefinery.com);
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
 * ----------------------
 * AbstractAxisRange.java
 * ----------------------
 * (C) Copyright 2001, 2002, by Bill Kelemen.
 *
 * Original Author:  Bill Kelemen;
 * Contributor(s):   -;
 *
 * $Id: AbstractAxisRange.java,v 1.1 2007/10/10 18:57:57 vauchers Exp $
 *
 * Changes:
 * --------
 * 06-Dec-2001 : Version 1 (BK);
 * 13-Dec-2001 : Standardised formatting (DG);
 * 08-Jan-2002 : Moved to new package com.jrefinery.chart.combination (DG);
 *
 */

package com.jrefinery.chart.combination;

/**
 * Abstract implementation of AxisRange interface. AxisRange is an interface
 * used by CombinedPlot to represent the min/max range of an axis. This allows
 * general algorithms to operate on dates and numerical ranges.
 *
 * @author Bill Kelemen (bill@kelemen-usa.com)
 */
public abstract class AbstractAxisRange implements AxisRange {

    private Object min;
    private Object max;

    /**
     * Creates an AbstractAxisRange object.
     *
     * @param min Minimum value
     * @param max Maximum value
     */
    public AbstractAxisRange(Object min, Object max) {
        this.min = min;
        this.max = max;
    }

    /**
     * Returns the min of the range.
     */
    public Object getMin() {
        return min;
    }

    /**
     * Returns the max of the range.
     */
    public Object getMax() {
        return max;
    }

    /**
     * Combines this with range. The result will be a range that contains both
     * this and range.
     *
     * @param range Range to combine with this.
     */
    public void combine(AxisRange range) {
        Object otherMin = range.getMin();
        Object otherMax = range.getMax();
        if (before(otherMin, min)) {
            min = otherMin;
        }
        if (after(otherMax, max)) {
            max = otherMax;
        }
    }

    /**
     * Returns true if o1 is before o2. This abstract method needs to be implemented
     * by sub-classes.
     *
     * @param o1 Object #1 to compare.
     * @param o2 Object #2 to compare.
     */
    protected abstract boolean before(Object o1, Object o2);

    /**
     * Returns true if o1 is after o2.
     *
     * @param o1 Object #1 to compare.
     * @param o2 Object #2 to compare.
     */
    protected boolean after(Object o1, Object o2) {
        return (!o1.equals(o2) && !before(o1, o2));
    }

    /**
     * Returns a string representing the object.
     */
    public String toString() {
        return (this.getClass() + "[" + min + ":" + max + "]");
    }

}