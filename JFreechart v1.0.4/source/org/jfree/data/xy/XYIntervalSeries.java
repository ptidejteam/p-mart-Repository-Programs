/* ===========================================================
 * JFreeChart : a free chart library for the Java(tm) platform
 * ===========================================================
 *
 * (C) Copyright 2000-2006, by Object Refinery Limited and Contributors.
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
 * ---------------------
 * XYIntervalSeries.java
 * ---------------------
 * (C) Copyright 2006, by Object Refinery Limited.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited);
 * Contributor(s):   -;
 *
 * $Id: XYIntervalSeries.java,v 1.1 2007/10/10 20:46:49 vauchers Exp $
 *
 * Changes
 * -------
 * 20-Oct-2006 : Version 1 (DG);
 *
 */

package org.jfree.data.xy;

import org.jfree.data.ComparableObjectItem;
import org.jfree.data.ComparableObjectSeries;

/**
 * A list of (x, x-low, x-high, y, y-low, y-high) data items.
 *
 * @since 1.0.3
 *
 * @see XYIntervalSeriesCollection
 */
public class XYIntervalSeries extends ComparableObjectSeries {
    
    /**
     * Creates a new empty series.  By default, items added to the series will 
     * be sorted into ascending order by x-value, and duplicate x-values will 
     * be allowed (these defaults can be modified with another constructor).
     *
     * @param key  the series key (<code>null</code> not permitted).
     */
    public XYIntervalSeries(Comparable key) {
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
    public XYIntervalSeries(Comparable key, boolean autoSort, 
            boolean allowDuplicateXValues) {
        super(key, autoSort, allowDuplicateXValues);
    }
    
    /**
     * Adds a data item to the series.
     *
     * @param x  the x-value.
     * @param xLow  the lower bound of the x-interval.
     * @param xHigh  the upper bound of the x-interval.
     * @param y  the y-value.
     * @param yLow  the lower bound of the y-interval.
     * @param yHigh  the upper bound of the y-interval.
     */
    public void add(double x, double xLow, double xHigh, double y, double yLow, 
            double yHigh) {
        super.add(new XYIntervalDataItem(x, xLow, xHigh, y, yLow, yHigh), true);
    }
    
    /**
     * Returns the x-value for the specified item.
     *
     * @param index  the item index.
     *
     * @return The x-value (never <code>null</code>).
     */
    public Number getX(int index) {
        XYIntervalDataItem item = (XYIntervalDataItem) getDataItem(index);
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
        XYIntervalDataItem item = (XYIntervalDataItem) getDataItem(index);
        return item.getYValue();
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
