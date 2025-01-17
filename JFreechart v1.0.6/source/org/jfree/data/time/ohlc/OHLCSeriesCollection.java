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
 * -------------------------
 * OHLCSeriesCollection.java
 * -------------------------
 * (C) Copyright 2006, by Object Refinery Limited.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited);
 * Contributor(s):   -;
 *
 * $Id: OHLCSeriesCollection.java,v 1.1 2007/10/10 20:53:56 vauchers Exp $
 *
 * Changes
 * -------
 * 04-Dec-2006 : Version 1 (DG);
 *
 */

package org.jfree.data.time.ohlc;

import java.io.Serializable;
import java.util.List;

import org.jfree.data.general.DatasetChangeEvent;
import org.jfree.data.time.RegularTimePeriod;
import org.jfree.data.time.TimePeriodAnchor;
import org.jfree.data.xy.AbstractXYDataset;
import org.jfree.data.xy.OHLCDataset;
import org.jfree.util.ObjectUtilities;

/**
 * A collection of {@link OHLCSeries} objects.
 *
 * @since 1.0.4
 *
 * @see OHLCSeries
 */
public class OHLCSeriesCollection extends AbstractXYDataset
                                implements OHLCDataset, Serializable {

    /** Storage for the data series. */
    private List data;
    
    private TimePeriodAnchor xPosition = TimePeriodAnchor.MIDDLE;
    
    /** 
     * Creates a new instance of <code>OHLCSeriesCollection</code>. 
     */
    public OHLCSeriesCollection() {
        this.data = new java.util.ArrayList();
    }

    /**
     * Adds a series to the collection and sends a {@link DatasetChangeEvent} 
     * to all registered listeners.
     *
     * @param series  the series (<code>null</code> not permitted).
     */
    public void addSeries(OHLCSeries series) {
        if (series == null) {
            throw new IllegalArgumentException("Null 'series' argument.");
        }
        this.data.add(series);
        series.addChangeListener(this);
        fireDatasetChanged();
    }

    /**
     * Returns the number of series in the collection.
     *
     * @return The series count.
     */
    public int getSeriesCount() {
        return this.data.size();
    }

    /**
     * Returns a series from the collection.
     *
     * @param series  the series index (zero-based).
     *
     * @return The series.
     * 
     * @throws IllegalArgumentException if <code>series</code> is not in the
     *     range <code>0</code> to <code>getSeriesCount() - 1</code>.
     */
    public OHLCSeries getSeries(int series) {
        if ((series < 0) || (series >= getSeriesCount())) {
            throw new IllegalArgumentException("Series index out of bounds");
        }
        return (OHLCSeries) this.data.get(series);
    }

    /**
     * Returns the key for a series.
     *
     * @param series  the series index (in the range <code>0</code> to 
     *     <code>getSeriesCount() - 1</code>).
     *
     * @return The key for a series.
     * 
     * @throws IllegalArgumentException if <code>series</code> is not in the
     *     specified range.
     */
    public Comparable getSeriesKey(int series) {
        // defer argument checking
        return getSeries(series).getKey();
    }

    /**
     * Returns the number of items in the specified series.
     *
     * @param series  the series (zero-based index).
     *
     * @return The item count.
     * 
     * @throws IllegalArgumentException if <code>series</code> is not in the
     *     range <code>0</code> to <code>getSeriesCount() - 1</code>.
     */
    public int getItemCount(int series) {
        // defer argument checking
        return getSeries(series).getItemCount();
    }

    /**
     * Returns the x-value for a time period.
     *
     * @param period  the time period (<code>null</code> not permitted).
     *
     * @return The x-value.
     */
    protected synchronized long getX(RegularTimePeriod period) {
        long result = 0L;
        if (this.xPosition == TimePeriodAnchor.START) {
            result = period.getFirstMillisecond();
        }
        else if (this.xPosition == TimePeriodAnchor.MIDDLE) {
            result = period.getMiddleMillisecond();
        }
        else if (this.xPosition == TimePeriodAnchor.END) {
            result = period.getLastMillisecond(); 
        }
        return result;
    }

    /**
     * Returns the x-value for an item within a series.
     *
     * @param series  the series index.
     * @param item  the item index.
     *
     * @return The x-value.
     */
    public double getXValue(int series, int item) {
        OHLCSeries s = (OHLCSeries) this.data.get(series);
        OHLCItem di = (OHLCItem) s.getDataItem(item);
        RegularTimePeriod period = di.getPeriod();
        return getX(period);
    }

    /**
     * Returns the x-value for an item within a series.
     *
     * @param series  the series index.
     * @param item  the item index.
     *
     * @return The x-value.
     */
    public Number getX(int series, int item) {
        return new Double(getXValue(series, item));
    }

    /**
     * Returns the y-value for an item within a series.
     *
     * @param series  the series index.
     * @param item  the item index.
     *
     * @return The y-value.
     */
    public Number getY(int series, int item) {
        OHLCSeries s = (OHLCSeries) this.data.get(series);
        OHLCItem di = (OHLCItem) s.getDataItem(item);
        return new Double(di.getYValue());
    }

    /**
     * Returns the open-value for an item within a series.
     *
     * @param series  the series index.
     * @param item  the item index.
     *
     * @return The open-value.
     */
    public double getOpenValue(int series, int item) {
        OHLCSeries s = (OHLCSeries) this.data.get(series);
        OHLCItem di = (OHLCItem) s.getDataItem(item);
        return di.getOpenValue();
    }
    
    /**
     * Returns the open-value for an item within a series.
     *
     * @param series  the series index.
     * @param item  the item index.
     *
     * @return The open-value.
     */
    public Number getOpen(int series, int item) {
        return new Double(getOpenValue(series, item));
    }
    
    /**
     * Returns the close-value for an item within a series.
     *
     * @param series  the series index.
     * @param item  the item index.
     *
     * @return The close-value.
     */
    public double getCloseValue(int series, int item) {
        OHLCSeries s = (OHLCSeries) this.data.get(series);
        OHLCItem di = (OHLCItem) s.getDataItem(item);
        return di.getCloseValue();
    }
    
    /**
     * Returns the close-value for an item within a series.
     *
     * @param series  the series index.
     * @param item  the item index.
     *
     * @return The close-value.
     */
    public Number getClose(int series, int item) {
        return new Double(getCloseValue(series, item));
    }
    
    /**
     * Returns the high-value for an item within a series.
     *
     * @param series  the series index.
     * @param item  the item index.
     *
     * @return The high-value.
     */
    public double getHighValue(int series, int item) {
        OHLCSeries s = (OHLCSeries) this.data.get(series);
        OHLCItem di = (OHLCItem) s.getDataItem(item);
        return di.getHighValue();
    }
    
    /**
     * Returns the high-value for an item within a series.
     *
     * @param series  the series index.
     * @param item  the item index.
     *
     * @return The high-value.
     */
    public Number getHigh(int series, int item) {
        return new Double(getHighValue(series, item));
    }
    
    /**
     * Returns the low-value for an item within a series.
     *
     * @param series  the series index.
     * @param item  the item index.
     *
     * @return The low-value.
     */
    public double getLowValue(int series, int item) {
        OHLCSeries s = (OHLCSeries) this.data.get(series);
        OHLCItem di = (OHLCItem) s.getDataItem(item);
        return di.getLowValue();
    }
    
    /**
     * Returns the low-value for an item within a series.
     *
     * @param series  the series index.
     * @param item  the item index.
     *
     * @return The low-value.
     */
    public Number getLow(int series, int item) {
        return new Double(getLowValue(series, item));
    }
    
    public Number getVolume(int series, int item) {
        return null;
    }
    
    public double getVolumeValue(int series, int item) {
        return Double.NaN;
    }
    
    /**
     * Tests this instance for equality with an arbitrary object.
     *
     * @param obj  the object (<code>null</code> permitted).
     *
     * @return A boolean. 
     */
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof OHLCSeriesCollection)) {
            return false;
        }
        OHLCSeriesCollection that = (OHLCSeriesCollection) obj;
        return ObjectUtilities.equal(this.data, that.data);
    }
    
    /**
     * Returns a clone of this instance.
     * 
     * @return A clone.
     * 
     * @throws CloneNotSupportedException if there is a problem.
     */
    public Object clone() throws CloneNotSupportedException {
        OHLCSeriesCollection clone 
                = (OHLCSeriesCollection) super.clone();
        clone.data = (List) ObjectUtilities.deepClone(this.data);
        return clone;
    }
    
}
