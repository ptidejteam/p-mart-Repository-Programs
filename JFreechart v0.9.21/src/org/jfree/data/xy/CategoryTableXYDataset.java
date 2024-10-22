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
 * ---------------------------
 * CategoryTableXYDataset.java
 * ---------------------------
 * (C) Copyright 2004, by Andreas Schroeder and Contributors.
 *
 * Original Author:  Andreas Schroeder;
 * Contributor(s):   David Gilbert (for Object Refinery Limited);
 *
 * $Id: CategoryTableXYDataset.java,v 1.1 2007/10/10 19:50:22 vauchers Exp $
 *
 * Changes
 * -------
 * 31-Mar-2004 : Version 1 (AS);
 * 05-May-2004 : Now extends AbstractIntervalXYDataset (DG);
 * 15-Jul-2004 : Switched interval access method names (DG);
 * 18-Aug-2004 : Moved from org.jfree.data --> org.jfree.data.xy (DG);
 *
 */

package org.jfree.data.xy;

import org.jfree.data.DefaultKeyedValues2D;
import org.jfree.data.DomainInfo;
import org.jfree.data.Range;
import org.jfree.data.general.DatasetChangeEvent;

/**
 * An implementation variant of the {@link TableXYDataset} where every series 
 * shares the same x-values (required for generating stacked area charts). 
 * This implementation uses a {@link DefaultKeyedValues2D} Object as backend 
 * implementation and is hence more "category oriented" than the {@link 
 * DefaultTableXYDataset} implementation.
 * <p>
 * This implementation provides no means to remove data items yet.
 * This is due to the lack of such facility in the DefaultKeyedValues2D class.
 * <p>
 * This class also implements the {@link IntervalXYDataset} interface, but this
 * implementation is provisional. 
 * 
 * @author Andreas Schroeder
 */
public class CategoryTableXYDataset extends AbstractIntervalXYDataset
                                    implements TableXYDataset, 
                                               IntervalXYDataset, 
                                               DomainInfo {
    
    /**
     * The backing data structure.
     */
    private DefaultKeyedValues2D values;
    
    /** A delegate for controlling the interval width. */
    private IntervalXYDelegate intervalDelegate;

    /**
     * Creates a new empty CategoryTableXYDataset.
     */
    public CategoryTableXYDataset() {
        this.values = new DefaultKeyedValues2D(true);
        this.intervalDelegate = new IntervalXYDelegate(this);
    }

    /**
     * Adds a data item to this dataset and sends a {@link DatasetChangeEvent} to all
     * registered listeners.
     * 
     * @param x  the x value.
     * @param y  the y value.
     * @param seriesName  the name of the series to add the data item.
     */
    public void add(double x, double y, String seriesName) {
        add(new Double(x), new Double(y), seriesName, true);
    }
    
    /**
     * Adds a data item to this dataset and, if requested, sends a {@link DatasetChangeEvent}
     * to all registered listeners.
     * 
     * @param x  the x value.
     * @param y  the y value.
     * @param seriesName  the name of the series to add the data item.
     * @param notify  notify listeners?
     */
    public void add(Number x, Number y, String seriesName, boolean notify) {
        this.values.addValue(y, (Comparable) x, seriesName);
        
        int series = this.values.getColumnIndex(seriesName);
        int item = this.values.getRowIndex((Comparable) x);
        this.intervalDelegate.itemAdded(series, item);
        if (notify) {
            fireDatasetChanged();
        }
    }

    /**
     * Removes a value from the dataset.
     * 
     * @param x  the x-value.
     * @param seriesName  the series name.
     */
    public void remove(double x, String seriesName) {
        remove(new Double(x), seriesName, true);
    }
    
    /**
     * Removes an item from the dataset.
     * 
     * @param x  the x-value.
     * @param seriesName  the series name.
     * @param notify  notify listeners?
     */
    public void remove(Number x, String seriesName, boolean notify) {
        this.values.removeValue((Comparable) x, seriesName);
        
        this.intervalDelegate.itemRemoved(x.doubleValue());
        
        if (notify) {
            fireDatasetChanged();
        }
    }


    /**
     * Returns the number of series in the collection.
     *
     * @return The series count.
     */
    public int getSeriesCount() {
        return this.values.getColumnCount();
    }

    /**
     * Returns the name of a series.
     *
     * @param series  the series index (zero-based).
     *
     * @return The name of a series.
     */
    public String getSeriesName(int series) {
        return this.values.getColumnKey(series).toString();
    }

    /**
     * Returns the number of x values in the dataset.
     *
     * @return The item count.
     */
    public int getItemCount() {
        return this.values.getRowCount();
    }

    /**
     * Returns the number of items in the specified series.
     * Returns the same as {@link CategoryTableXYDataset#getItemCount()}.
     *
     * @param series  the series index (zero-based).
     *
     * @return The item count.
     */
    public int getItemCount(int series) {
        return getItemCount();  // all series have the same number of items in this dataset
    }

    /**
     * Returns the x-value for the specified series and item.
     *
     * @param series  the series index (zero-based).
     * @param item  the item index (zero-based).
     *
     * @return The value.
     */
    public Number getX(int series, int item) {
        return (Number) this.values.getRowKey(item);
    }

    /**
     * Returns the starting X value for the specified series and item.
     *
     * @param series  the series index (zero-based).
     * @param item  the item index (zero-based).
     *
     * @return The starting X value.
     */
    public Number getStartX(int series, int item) {
        return this.intervalDelegate.getStartX(series, item);
    }

    /**
     * Returns the ending X value for the specified series and item.
     *
     * @param series  the series index (zero-based).
     * @param item  the item index (zero-based).
     *
     * @return The ending X value.
     */
    public Number getEndX(int series, int item) {
        return this.intervalDelegate.getEndX(series, item);
    }

    /**
     * Returns the y-value for the specified series and item.
     *
     * @param series  the series index (zero-based).
     * @param item  the item index (zero-based).
     *
     * @return The y value (possibly <code>null</code>).
     */
    public Number getY(int series, int item) {
        return this.values.getValue(item, series);
    }

    /**
     * Returns the starting Y value for the specified series and item.
     *
     * @param series  the series index (zero-based).
     * @param item  the item index (zero-based).
     *
     * @return The starting Y value.
     */
    public Number getStartY(int series, int item) {
        return getY(series, item);
    }

    /**
     * Returns the ending Y value for the specified series and item.
     *
     * @param series  the series index (zero-based).
     * @param item  the item index (zero-based).
     *
     * @return The ending Y value.
     */
    public Number getEndY(int series, int item) {
        return getY(series, item);
    }
    
    /**
     * Returns the range of values in the dataset's domain.
     * 
     * @return The range.
     */
    public Range getDomainRange() {
        return this.intervalDelegate.getDomainRange();
    }

    /**
     * Returns the maximum domain value.
     * 
     * @return The maximum domain value.
     */
    public Number getMaximumDomainValue() {
        return this.intervalDelegate.getMaximumDomainValue();
    }

    /**
     * Returns the minimum domain value.
     * 
     * @return The minimum domain value.
     */
    public Number getMinimumDomainValue() {
        return this.intervalDelegate.getMinimumDomainValue();
    }
    
    /**
     * Returns the interval position factor. 
     * 
     * @return The interval position factor.
     */
    public double getIntervalPositionFactor() {
        return this.intervalDelegate.getIntervalPositionFactor();
    }

    /**
     * Sets the interval position factor. Must be between 0.0 and 1.0 inclusive.
     * If the factor is 0.5, the gap is in the middle of the x values. If it
     * is lesser than 0.5, the gap is farther to the left and if greater than
     * 0.5 it gets farther to the right.
     *  
     * @param d  the new interval position factor.
     */
    public void setIntervalPositionFactor(double d) {
        this.intervalDelegate.setIntervalPositionFactor(d);
        fireDatasetChanged();
    }

    /**
     * Returns the full interval width. 
     * 
     * @return The interval width to use.
     */
    public double getIntervalWidth() {
        return this.intervalDelegate.getIntervalWidth();
    }

    /**
     * Sets the interval width manually. 
     * 
     * @param d  the new interval width.
     */
    public void setIntervalWidth(double d) {
        this.intervalDelegate.setIntervalWidth(d);
        fireDatasetChanged();
    }

    /**
     * Returns whether the interval width is automatically calculated or not.
     * 
     * @return whether the width is automatically calculated or not.
     */
    public boolean isAutoWidth() {
        return this.intervalDelegate.isAutoWidth();
    }

    /**
     * Sets the flag that indicates wether the interval width is automatically
     * calculated or not. 
     * 
     * @param b  the flag.
     */
    public void setAutoWidth(boolean b) {
        this.intervalDelegate.setAutoWidth(b);
        fireDatasetChanged();
    }
    
}
