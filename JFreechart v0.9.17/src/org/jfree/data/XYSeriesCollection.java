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
 * -----------------------
 * XYSeriesCollection.java
 * -----------------------
 * (C) Copyright 2001-2004, by Object Refinery Limited and Contributors.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited);
 * Contributor(s):   Aaron Metzger;
 *
 * $Id: XYSeriesCollection.java,v 1.1 2007/10/10 19:29:13 vauchers Exp $
 *
 * Changes
 * -------
 * 15-Nov-2001 : Version 1 (DG);
 * 03-Apr-2002 : Added change listener code (DG);
 * 29-Apr-2002 : Added removeSeries, removeAllSeries methods (ARM);
 * 07-Oct-2002 : Fixed errors reported by Checkstyle (DG);
 * 26-Mar-2003 : Implemented Serializable (DG);
 * 04-Aug-2003 : Added getSeries() method (DG);
 *
 */

package org.jfree.data;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

import org.jfree.util.ObjectUtils;

/**
 * Represents a collection of {@link XYSeries} objects that can be used as a dataset.
 *
 * @author David Gilbert
 */
public class XYSeriesCollection extends AbstractSeriesDataset
                                implements IntervalXYDataset, Serializable {

    /** The series that are included in the collection. */
    private List data;
    
    /** The interval width (used to calculate the start and end x-values). */
    private double intervalWidth;
    
    /** The interval position factor (used to position the start and end x-values). */
    private double intervalPositionFactor;

    /**
     * Constructs an empty dataset.
     */
    public XYSeriesCollection() {
        this(null);
    }

    /**
     * Constructs a dataset and populates it with a single time series.
     *
     * @param series  the time series (<code>null</code> ignored).
     */
    public XYSeriesCollection(XYSeries series) {
        this.data = new java.util.ArrayList();
        if (series != null) {
            this.data.add(series);
            series.addChangeListener(this);
        }
        this.intervalWidth = 1.0;
        this.intervalPositionFactor = 0.5;
    }
    
    /**
     * Returns the interval width. This is used to calculate the start and end x-values, if 
     * they are used.
     * 
     * @return The interval width.
     */
    public double getIntervalWidth() {
        return this.intervalWidth;
    }
    
    /**
     * Sets the interval width.
     * 
     * @param width  the width.
     */
    public void setIntervalWidth(double width) {
        this.intervalWidth = width;
        fireDatasetChanged();
    }

    /**
     * Returns the interval position factor.  
     * 
     * @return The interval position factor.
     */
    public double getIntervalPositionFactor() {
        return this.intervalPositionFactor;
    }
    
    /**
     * Sets the interval position factor. This controls where the x-value is in relation to
     * the interval surrounding the x-value (0.0 means the x-value will be positioned at the start,
     * 0.5 in the middle, and 1.0 at the end).
     * 
     * @param factor  the factor.
     */
    public void setIntervalPositionFactor(double factor) {
        this.intervalPositionFactor = factor;
        fireDatasetChanged();
    }
    
    /**
     * Adds a series to the collection and sends a {@link DatasetChangeEvent} to all
     * registered listeners.
     *
     * @param series  the series (<code>null</code> not permitted).
     */
    public void addSeries(XYSeries series) {

        // check arguments...
        if (series == null) {
            throw new IllegalArgumentException(
                "XYSeriesCollection.addSeries(...): cannot add null series.");
        }

        // add the series...
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
     * Returns a list of all the series in the collection.  
     * 
     * @return The list (which is unmodifiable).
     */
    public List getSeries() {
        return Collections.unmodifiableList(this.data);
    }

    /**
     * Returns a series.
     *
     * @param series  the series (zero-based index).
     *
     * @return The series.
     */
    public XYSeries getSeries(int series) {

        // check arguments...
        if ((series < 0) || (series > getSeriesCount())) {
            throw new IllegalArgumentException(
                "XYSeriesCollection.getSeries(...): index outside valid range.");
        }

        // fetch the series...
        XYSeries ts = (XYSeries) this.data.get(series);
        return ts;

    }

    /**
     * Returns the name of a series.
     *
     * @param series  the series (zero-based index).
     *
     * @return the name of a series.
     */
    public String getSeriesName(int series) {

        // check arguments...delegated

        // fetch the result...
        return getSeries(series).getName();

    }

    /**
     * Returns the number of items in the specified series.
     *
     * @param series  the series (zero-based index).
     *
     * @return the number of items in the specified series.
     */
    public int getItemCount(int series) {

        // check arguments...delegated

        // fetch the result...
        return getSeries(series).getItemCount();

    }

    /**
     * Returns the x-value for the specified series and item.
     *
     * @param series  the series (zero-based index).
     * @param item  the item (zero-based index).
     *
     * @return the x-value for the specified series and item.
     */
    public Number getXValue(int series, int item) {

        XYSeries ts = (XYSeries) this.data.get(series);
        XYDataItem xyItem = ts.getDataItem(item);
        return xyItem.getX();

    }

    /**
     * Returns the starting X value for the specified series and item.
     *
     * @param series  the series (zero-based index).
     * @param item  the item (zero-based index).
     *
     * @return The starting X value.
     */
    public Number getStartXValue(int series, int item) {
        Number startX = null;
        Number x = getXValue(series, item);
        if (x != null) {
            startX = new Double(
                x.doubleValue() - (this.intervalPositionFactor * this.intervalWidth)
            );
        }
        return startX;
    }

    /**
     * Returns the ending X value for the specified series and item.
     *
     * @param series  the series (zero-based index).
     * @param item  the item (zero-based index).
     *
     * @return The ending X value.
     */
    public Number getEndXValue(int series, int item) {
        Number endX = null;
        Number x = getXValue(series, item);
        if (x != null) {
            endX = new Double(
                x.doubleValue() + (1.0 - this.intervalPositionFactor) * this.intervalWidth
            );
        }
        return endX;
    }

    /**
     * Returns the y-value for the specified series and item.
     *
     * @param series  the series (zero-based index).
     * @param index  the index of the item of interest (zero-based).
     *
     * @return the y-value for the specified series and item.
     */
    public Number getYValue(int series, int index) {

        XYSeries ts = (XYSeries) this.data.get(series);
        XYDataItem xyItem = ts.getDataItem(index);
        return xyItem.getY();

    }

    /**
     * Returns the starting Y value for the specified series and item.
     *
     * @param series  the series (zero-based index).
     * @param item  the item (zero-based index).
     *
     * @return The starting Y value.
     */
    public Number getStartYValue(int series, int item) {
        return getYValue(series, item);
    }

    /**
     * Returns the ending Y value for the specified series and item.
     *
     * @param series  the series (zero-based index).
     * @param item  the item (zero-based index).
     *
     * @return The ending Y value.
     */
    public Number getEndYValue(int series, int item) {
        return getYValue(series, item);
    }

    /**
     * Removes all the series from the collection.
     * <P>
     * Notifies all registered listeners that the dataset has changed.
     */
    public void removeAllSeries() {


        // Unregister the collection as a change listener to each series in the collection.
        for (int i = 0; i < this.data.size(); i++) {
          XYSeries series = (XYSeries) this.data.get(i);
          series.removeChangeListener(this);
        }

        // Remove all the series from the collection and notify listeners.
        this.data.clear();
        fireDatasetChanged();

    }

    /**
     * Removes a series from the collection.
     * <P>
     * Notifies all registered listeners that the dataset has changed.
     *
     * @param series  the series.
     */
    public void removeSeries(XYSeries series) {

        // check arguments...
        if (series == null) {
            throw new IllegalArgumentException(
                "XYSeriesCollection.removeSeries(...): cannot remove null series.");
        }

        // remove the series...
        if (this.data.contains(series)) {
            series.removeChangeListener(this);
            this.data.remove(series);
            fireDatasetChanged();
        }

    }

    /**
     * Removes a series from the collection.
     * <P>
     * Notifies all registered listeners that the dataset has changed.
     *
     * @param series  the series (zero based index).
     */
    public void removeSeries(int series) {

        // check arguments...
        if ((series < 0) || (series > getSeriesCount())) {
            throw new IllegalArgumentException(
                "XYSeriesCollection.removeSeries(...): index outside valid range.");
        }

        // fetch the series, remove the change listener, then remove the series.
        XYSeries ts = (XYSeries) this.data.get(series);
        ts.removeChangeListener(this);
        this.data.remove(series);
        fireDatasetChanged();

    }

    /**
     * Tests this collection for equality with an arbitrary object.
     *
     * @param obj  the object.
     *
     * @return A boolean.
     */
    public boolean equals(Object obj) {

        if (obj == null) {
            return false;
        }

        if (obj == this) {
            return true;
        }

        if (obj instanceof XYSeriesCollection) {
            XYSeriesCollection c = (XYSeriesCollection) obj;
            return ObjectUtils.equal(this.data, c.data);
        }

        return false;
    }

    /**
     * Returns a hash code.
     * 
     * @return a hash code.
     */
    public int hashCode() {
        return (this.data != null ? this.data.hashCode() : 0);
    }
}
