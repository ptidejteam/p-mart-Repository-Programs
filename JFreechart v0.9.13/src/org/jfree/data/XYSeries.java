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
 * -------------
 * XYSeries.java
 * -------------
 * (C) Copyright 2001-2003, Object Refinery Limited and Contributors.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited);
 * Contributor(s):   Aaron Metzger;
 *                   Jonathan Gabbai;
 *                   Richard Atkinson;
 *
 * $Id: XYSeries.java,v 1.1 2007/10/10 19:15:28 vauchers Exp $
 *
 * Changes
 * -------
 * 15-Nov-2001 : Version 1 (DG);
 * 03-Apr-2002 : Added an add(double, double) method (DG);
 * 29-Apr-2002 : Added a clear() method (ARM);
 * 06-Jun-2002 : Updated Javadoc comments (DG);
 * 29-Aug-2002 : Modified to give user control over whether or not duplicate x-values are
 *               allowed (DG);
 * 07-Oct-2002 : Fixed errors reported by Checkstyle (DG);
 * 11-Nov-2002 : Added maximum item count, code contributed by Jonathan Gabbai (DG);
 * 26-Mar-2003 : Implemented Serializable (DG);
 * 04-Aug-2003 : Added getItems() method (DG);
 * 15-Aug-2003 : Changed 'data' from private to protected, added new add(...) methods with a
 *               'notify' argument (DG);
 * 22-Sep-2003 : Added getAllowDuplicateXValues() method (RA);
 *
 */

package org.jfree.data;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

import org.jfree.util.ObjectUtils;

/**
 * Represents a sequence of zero or more data items in the form (x, y).
 *
 * @author David Gilbert
 */
public class XYSeries extends Series implements Serializable {

    /** Storage for the data items in the series. */
    protected List data;

    // In version 0.9.12, in response to several developer requests, I changed the 'data' attribute 
    // from 'private' to 'protected', so that others can make subclasses that work directly with 
    // the underlying data structure.

    /** The maximum number of items for the series. */
    private int maximumItemCount = Integer.MAX_VALUE;

    /** A flag that controls whether or not duplicate x-values are allowed. */
    private boolean allowDuplicateXValues;

    /**
     * Constructs a new xy-series that contains no data.
     * <p>
     * By default, duplicate x-values will be allowed for the series.
     *
     * @param name  the series name.
     */
    public XYSeries(String name) {
        this(name, true);
    }

    /**
     * Constructs a new xy-series that contains no data.  You can specify whether or not
     * duplicate x-values are allowed for the series.
     *
     * @param name  the series name.
     * @param allowDuplicateXValues  a flag that controls whether duplicate x-values are allowed.
     */
    public XYSeries(String name, boolean allowDuplicateXValues) {
        super(name);
        this.allowDuplicateXValues = allowDuplicateXValues;
        this.data = new java.util.ArrayList();
    }

    /**
     * Returns the number of items in the series.
     *
     * @return The item count.
     */
    public int getItemCount() {
        return data.size();
    }

    /**
     * Returns whether duplicate X-Values are allowed.
     *
     * @return Whether they are.
     */
    public boolean getAllowDuplicateXValues() {
        return this.allowDuplicateXValues;
    }

    /**
     * Returns the list of data items for the series (the list contains {@link XYDataItem} 
     * objects and is unmodifiable).
     * 
     * @return The list of data items.
     */
    public List getItems() {
        return Collections.unmodifiableList(this.data);    
    }
    
    /**
     * Returns the maximum number of items that will be retained in the series.
     * <P>
     * The default value is <code>Integer.MAX_VALUE</code>).
     *
     * @return the maximum item count.
     */
    public int getMaximumItemCount() {
        return this.maximumItemCount;
    }

    /**
     * Sets the maximum number of items that will be retained in the series.
     * <P>
     * If you add a new item to the series such that the number of items will exceed the
     * maximum item count, then the FIRST element in the series is automatically removed,
     * ensuring that the maximum item count is not exceeded.
     *
     * @param maximum  the maximum.
     */
    public void setMaximumItemCount(int maximum) {
        this.maximumItemCount = maximum;
    }

    /**
     * Adds a data item to the series. A {@link SeriesChangeEvent} is sent to all registered 
     * listeners.
     *
     * @param item  the (x, y) item.
     *
     * @throws SeriesException if there is a problem adding the data.
     */
    public void add(XYDataItem item) throws SeriesException {
        add(item, true);
    }
    
    /**
     * Adds a data item to the series.  If requested (via <code>notify</code>), a
     * {@link SeriesChangeEvent} is sent to all registered listeners.
     *
     * @param item  the (x, y) item.
     * @param notify  a flag that controls whether or not a {@link SeriesChangeEvent} is sent to 
     *                all registered listeners.
     *
     * @throws SeriesException if there is a problem adding the data.
     */
    public void add(XYDataItem item, boolean notify) throws SeriesException {

        // check arguments...
        if (item == null) {
            throw new IllegalArgumentException("XYSeries.add(...): null item not allowed.");
        }

        // make the change (if it's not a duplicate x-value)...
        boolean changed = false;
        int index = Collections.binarySearch(data, item);
        if (index < 0) {
            data.add(-index - 1, item);
         
            // check if this addition will exceed the maximum item count...
            if (getItemCount() > this.maximumItemCount) {
                this.data.remove(0);
            }
            changed = true;
        }
        else {
            if (allowDuplicateXValues == true) {
                data.add(index, item);
                if (getItemCount() > this.maximumItemCount) {
                    this.data.remove(0);
                }
                changed = true;
            }
            else {
                throw new SeriesException("XYSeries.add(...): x-value already exists.");
            }
        }
        
        if (notify && changed) {
            fireSeriesChanged();
        }

    }

    /**
     * Adds a data item to the series.
     *
     * @param x  the x value.
     * @param y  the y value.
     *
     * @throws SeriesException if there is a problem adding the data.
     */
    public void add(double x, double y) throws SeriesException {
        add(new Double(x), new Double(y), true);
    }

    /**
     * Adds a data item to the series.
     *
     * @param x  the x value.
     * @param y  the y value.
     * @param notify  a flag that controls whether or not a {@link SeriesChangeEvent} is sent to 
     *                all registered listeners.
     *
     * @throws SeriesException if there is a problem adding the data.
     */
    public void add(double x, double y, boolean notify) throws SeriesException {
        add(new Double(x), new Double(y), notify);
    }

    /**
     * Adds a data item to the series.
     * <P>
     * The unusual pairing of parameter types is to make it easier to add null y-values.
     *
     * @param x  the x value.
     * @param y  the y value.
     *
     * @throws SeriesException if there is a problem adding the data.
     */
    public void add(double x, Number y) throws SeriesException {
        add(new Double(x), y);
    }

    /**
     * Adds a data item to the series.
     * <P>
     * The unusual pairing of parameter types is to make it easier to add null y-values.
     *
     * @param x  the x value.
     * @param y  the y value.
     * @param notify  a flag that controls whether or not a {@link SeriesChangeEvent} is sent to 
     *                all registered listeners.
     *
     * @throws SeriesException if there is a problem adding the data.
     */
    public void add(double x, Number y, boolean notify) throws SeriesException {
        add(new Double(x), y, notify);
    }

    /**
     * Adds new data to the series.
     * <P>
     * Throws an exception if the x-value is a duplicate AND the allowDuplicateXValues flag is
     * false.
     *
     * @param x  the x-value.
     * @param y  the y-value.
     *
     * @throws SeriesException if there is a problem adding the data.
     */
    public void add(Number x, Number y) throws SeriesException {
        add(x, y, true);
    }
    
    /**
     * Adds new data to the series.
     * <P>
     * Throws an exception if the x-value is a duplicate AND the allowDuplicateXValues flag is
     * false.
     *
     * @param x  the x-value.
     * @param y  the y-value.
     * @param notify  a flag the controls whether or not a {@link SeriesChangeEvent} is sent to 
     *                all registered listeners.
     *
     * @throws SeriesException if there is a problem adding the data.
     */
    public void add(Number x, Number y, boolean notify) throws SeriesException {
        XYDataItem item = new XYDataItem(x, y);
        add(item, notify);
    }

    /**
     * Deletes a range of items from the series.
     *
     * @param start  the start index (zero-based).
     * @param end  the end index (zero-based).
     */
    public void delete(int start, int end) {
        for (int i = start; i <= end; i++) {
            data.remove(start);
        }
        fireSeriesChanged();
    }

    /**
     * Removes all data pairs from the series.
     */
    public void clear() {

        if (data.size() > 0) {
            data.clear();
            fireSeriesChanged();
        }

    }

    /**
     * Return the data pair with the specified index.
     *
     * @param index  The index.
     *
     * @return The data pair with the specified index.
     * @deprecated Use getDataItem(index).
     */
    public XYDataPair getDataPair(int index) {
        return (XYDataPair) data.get(index);
    }

    /**
     * Return the data item with the specified index.
     *
     * @param index  The index.
     *
     * @return The data item with the specified index.
     */
    public XYDataItem getDataItem(int index) {
        return (XYDataItem) data.get(index);
    }

    /**
     * Returns the x-value at the specified index.
     *
     * @param index  The index.
     *
     * @return The x-value.
     */
    public Number getXValue(int index) {
        return getDataItem(index).getX();
    }

    /**
     * Returns the y-value at the specified index.
     *
     * @param index  The index.
     *
     * @return The y-value.
     */
    public Number getYValue(int index) {
        return getDataItem(index).getY();
    }

    /**
     * Updates the value of an item in the series.
     *
     * @param index  The item (zero based index).
     * @param y  The new value.
     */
    public void update(int index, Number y) {
        XYDataItem item = getDataItem(index);
        item.setY(y);
        fireSeriesChanged();
    }

    /**
     * Returns a clone of the series.
     *
     * @return a clone of the time series.
     */
    public Object clone() {

        Object clone = createCopy(0, getItemCount() - 1);
        return clone;

    }

    /**
     * Creates a new series by copying a subset of the data in this time series.
     *
     * @param start  The index of the first item to copy.
     * @param end  The index of the last item to copy.
     *
     * @return A series containing a copy of this series from start until end.
     */
    public XYSeries createCopy(int start, int end) {

        XYSeries copy = (XYSeries) super.clone();
        //copy.listeners = new java.util.ArrayList();
        //copy.propertyChangeSupport = new PropertyChangeSupport(copy);

        copy.data = new java.util.ArrayList();
        if (data.size() > 0) {
            for (int index = start; index <= end; index++) {
                XYDataItem item = (XYDataItem) this.data.get(index);
                XYDataItem clone = (XYDataItem) item.clone();
                try {
                    copy.add(clone);
                }
                catch (SeriesException e) {
                    System.err.println("XYSeries.createCopy(): unable to add cloned data pair.");
                }
            }
        }

        return copy;

    }

    /**
     * Tests this series for equality with an arbitrary object.
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

        if (obj instanceof XYSeries) {
            XYSeries s = (XYSeries) obj;
            boolean b0 = ObjectUtils.equal(this.data, s.data);
            boolean b1 = (this.maximumItemCount == s.maximumItemCount);
            boolean b2 = (this.allowDuplicateXValues == s.allowDuplicateXValues);
            return b0 && b1 && b2;
        }

        return false;

    }

}

