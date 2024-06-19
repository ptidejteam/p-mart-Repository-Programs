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
 * ---------------
 * XYDataItem.java
 * ---------------
 * (C) Copyright 2003, by Object Refinery Limited.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited);
 * Contributor(s):   -;
 *
 * $Id: XYDataItem.java,v 1.1 2007/10/10 19:19:00 vauchers Exp $
 *
 * Changes
 * -------
 * 05-Aug-2003 : Renamed XYDataPair --> XYDataItem (DG);
 *
 */

package org.jfree.data;

import java.io.Serializable;

/**
 * Represents one (x, y) data item for an xy-series.
 *
 * @author David Gilbert
 */
public class XYDataItem implements Cloneable, Comparable, Serializable {

    /** The x-value. */
    private Number x;

    /** The y-value. */
    private Number y;

    /**
     * Constructs a new data pair.
     *
     * @param x  the x-value.
     * @param y  the y-value.
     */
    public XYDataItem(Number x, Number y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Constructs a new data pair.
     *
     * @param x  the x-value.
     * @param y  the y-value.
     */
    public XYDataItem(double x, double y) {
        this(new Double(x), new Double(y));
    }

    /**
     * Returns the x-value.
     *
     * @return the x-value.
     */
    public Number getX() {
        return this.x;
    }

    /**
     * Returns the y-value.
     *
     * @return the y-value.
     */
    public Number getY() {
        return this.y;
    }

    /**
     * Sets the y-value for this data pair.
     * <P>
     * Note that there is no corresponding method to change the x-value.
     *
     * @param y  the new y-value.
     */
    public void setY(Number y) {
        this.y = y;
    }

    /**
     * Returns an integer indicating the order of this data pair object relative to another object.
     * <P>
     * For the order we consider only the x-value:
     * negative == "less-than", zero == "equal", positive == "greater-than".
     *
     * @param o1  the object being compared to.
     *
     * @return  an integer indicating the order of this data pair object
     *      relative to another object.
     */
    public int compareTo(Object o1) {

        int result;

        // CASE 1 : Comparing to another TimeSeriesDataPair object
        // -------------------------------------------------------
        if (o1 instanceof XYDataItem) {
            XYDataItem dataItem = (XYDataItem) o1;
            double compare = this.x.doubleValue() - dataItem.getX().doubleValue();
            if (compare > 0) {
                result = 1;
            }
            else {
                if (compare < 0) {
                    result = -1;
                }
                else {
                    result = 0;
                }
            }
        }

        // CASE 2 : Comparing to a general object
        // ---------------------------------------------
        else {
            // consider time periods to be ordered after general objects
            result = 1;
        }

        return result;

    }

    /**
     * Returns a clone of this XYDataPair.
     *
     * @return a clone of the data pair.
     */
    public Object clone() {

        Object clone = null;

        try {
            clone = super.clone();
        }
        catch (CloneNotSupportedException e) { // won't get here...
            System.err.println("XYDataPair.clone(): operation not supported.");
        }

        return clone;

    }

}
