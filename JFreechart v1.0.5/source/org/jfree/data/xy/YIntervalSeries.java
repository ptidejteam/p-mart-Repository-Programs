/* ===========================================================
 * JFreeChart : a free chart library for the Java(tm) platform
 * ===========================================================
 *
 * (C) Copyright 2000-2007, by Object Refinery Limited and Contributors.
 *
 * Project Info:  http://www.jfree.org/jfreechart/index.html
 *
 * This library is free software; you can redistribute it and/or modify it 
 * under the terms of the GNU Lesser General Public License as published by 
 * the Free Software Foundation; either version 2.1 of the License, or 
 * (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but 
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY 
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public 
 * License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, 
 * USA.  
 *
 * [Java is a trademark or registered trademark of Sun Microsystems, Inc. 
 * in the United States and other countries.]
 *
 * --------------------
 * YIntervalSeries.java
 * --------------------
 * (C) Copyright 2006, 2007, by Object Refinery Limited.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited);
 * Contributor(s):   -;
 *
 * $Id: YIntervalSeries.java,v 1.1 2007/10/10 20:38:28 vauchers Exp $
 *
 * Changes
 * -------
 * 20-Oct-2006 : Version 1 (DG);
 * 20-Feb-2007 : Added getYHighValue() and getYLowValue() methods (DG);
 *
 */

package org.jfree.data.xy;

import org.jfree.data.ComparableObjectItem;
import org.jfree.data.ComparableObjectSeries;

/**
 * A list of (x, y, y-low, y-high) data items.
 *
 * @since 1.0.3
 *
 * @see YIntervalSeriesCollection
 */
public class YIntervalSeries extends ComparableObjectSeries {
    
    /**
     * Creates a new empty series.  By default, items added to the series will 
     * be sorted into ascending order by x-value, and duplicate x-values will 
     * be allowed (these defaults can be modified with another constructor.
     *
     * @param key  the series key (<code>null</code> not permitted).
     */
    public YIntervalSeries(Comparable key) {
        this(key, true, true);
    }
    
    /**
     * Constructs a new xy-series that contains no data.  You can specify 
     * whether or not duplicate x-values are allowed for the series.
     *
     * @param key  the series key (<code>null</code> not permitted).
     * @param autoSort  a flag that controls whether or not the items in the 
     *                  series are sorted.
     * @param allowDuplicateXValues  a flag that controls whether duplicate 
     *                               x-values are allowed.
     */
    public YIntervalSeries(Comparable key, boolean autoSort, 
            boolean allowDuplicateXValues) {
        super(key, autoSort, allowDuplicateXValues);
    }
    
    /**
     * Adds a data item to the series.
     *
     * @param x  the x-value.
     * @param y  the y-value.
     * @param yLow  the lower bound of the y-interval.
     * @param yHigh  the upper bound of the y-interval.
     */
    public void add(double x, double y, double yLow, double yHigh) {
        super.add(new YIntervalDataItem(x, y, yLow, yHigh), true);
    }
    
    /**
     * Returns the x-value for the specified item.
     *
     * @param index  the item index.
     *
     * @return The x-value (never <code>null</code>).
     */
    public Number getX(int index) {
        YIntervalDataItem item = (YIntervalDataItem) getDataItem(index);
        return item.getX();
    }
    
    /**
     * Returns the y-value for the specified item.
     *
     * @param index  the item index.
     *
     * @return The y-value.
     */
    public double getYValue(int index) {
        YIntervalDataItem item = (YIntervalDataItem) getDataItem(index);
        return item.getYValue();
    }
    
    /**
     * Returns the lower bound of the Y-interval for the specified item in the
     * series.
     * 
     * @param index  the item index.
     * 
     * @return The lower bound of the Y-interval.
     * 
     * @since 1.0.5
     */
    public double getYLowValue(int index) {
        YIntervalDataItem item = (YIntervalDataItem) getDataItem(index);
        return item.getYLowValue();
    }
    
    /**
     * Returns the upper bound of the y-interval for the specified item in the
     * series.
     * 
     * @param index  the item index.
     * 
     * @return The upper bound of the y-interval.
     * 
     * @since 1.0.5
     */
    public double getYHighValue(int index) {
        YIntervalDataItem item = (YIntervalDataItem) getDataItem(index);
        return item.getYHighValue();
    }

    /**
     * Returns the data item at the specified index.
     * 
     * @param index  the item index.
     * 
     * @return The data item.
     */
    public ComparableObjectItem getDataItem(int index) {
        return super.getDataItem(index);
    }

}
