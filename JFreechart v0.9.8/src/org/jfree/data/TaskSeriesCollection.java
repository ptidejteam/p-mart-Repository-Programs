/* ======================================
 * JFreeChart : a free Java chart library
 * ======================================
 *
 * Project Info:  http://www.jfree.org/jfreechart/index.html
 * Project Lead:  David Gilbert (david.gilbert@object-refinery.com);
 *
 * (C) Copyright 2000-2003, by Simba Management Limited and Contributors.
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
 * -------------------------
 * TaskSeriesCollection.java
 * -------------------------
 * (C) Copyright 2002, 2003 by Simba Management Limited.
 *
 * Original Author:  David Gilbert (for Simba Management Limited);
 * Contributor(s):   -;
 *
 * $Id: TaskSeriesCollection.java,v 1.1 2007/10/10 20:03:14 vauchers Exp $
 *
 * Changes
 * -------
 * 06-Jun-2002 : Version 1 (DG);
 * 07-Oct-2002 : Fixed errors reported by Checkstyle (DG);
 * 24-Oct-2002 : Amendments for changes in CategoryDataset interface and CategoryToolTipGenerator
 *               interface (DG);
 * 10-Jan-2003 : Renamed GanttSeriesCollection --> TaskSeriesCollection (DG);
 *
 */

package org.jfree.data;

import java.util.List;
import java.util.Iterator;

/**
 * A collection of {@link TaskSeries} objects.
 * <P>
 * This class provides one implementation of the {@link MultiIntervalCategoryDataset} interface.
 *
 * @author David Gilbert
 */
public class TaskSeriesCollection extends AbstractSeriesDataset
                                  implements MultiIntervalCategoryDataset {

    /** Storage for aggregate task keys (the task description is used as the key). */
    private List keys;

    /** Storage for the series. */
    private List data;

    /**
     * Default constructor.
     */
    public TaskSeriesCollection() {
        this.keys = new java.util.ArrayList();
        this.data = new java.util.ArrayList();
    }

    /**
     * Returns the name of a series.
     *
     * @param series  the series index (zero-based).
     *
     * @return The name of a series.
     */
    public String getSeriesName(int series) {
        TaskSeries ts = (TaskSeries) data.get(series);
        return ts.getName();
    }

    /**
     * Returns the number of series in the collection.
     *
     * @return the series count.
     */
    public int getSeriesCount() {
        return getRowCount();
    }

    /**
     * Returns the number of rows (series) in the collection.
     *
     * @return the series count.
     */
    public int getRowCount() {
        return data.size();
    }

    /**
     * Returns the number of column in the dataset.
     *
     * @return The column count.
     */
    public int getColumnCount() {
        return keys.size();
    }

    /**
     * Returns the row keys.  In this case, each series is a key.
     *
     * @return The row keys.
     */
    public List getRowKeys() {
        return this.data;
    }

    /**
     * Returns a list of the column keys in the dataset.
     *
     * @return the category list.
     */
    public List getColumnKeys() {
        return this.keys;
    }

    /**
     * Returns a column key.
     *
     * @param item  the index.
     * 
     * @return The column key.
     */
    public Comparable getColumnKey(int item) {
        return (Comparable) this.keys.get(item);
    }

    /**
     * Returns the column index for a column key.
     *
     * @param columnKey  the columnKey.
     *
     * @return the column index.
     */
    public int getColumnIndex(Comparable columnKey) {
        return this.keys.indexOf(columnKey);
    }

    /**
     * Returns the row index for the given row key.
     *
     * @param rowKey  the row key.
     *
     * @return The index.
     */
    public int getRowIndex(Comparable rowKey) {
        return this.data.indexOf(rowKey);
    }

    /**
     * Returns the key for a row.
     *
     * @param index  the row index (zero-based).
     *
     * @return The key.
     */
    public Comparable getRowKey(int index) {
        TaskSeries series = (TaskSeries) this.data.get(index);
        return series.getName();
    }

    /**
     * Adds a series to the dataset.
     *
     * @param series  the series.
     */
    public void add(TaskSeries series) {
        
        // check arguments...
        if (series == null) {
            throw new IllegalArgumentException(
                "XYSeriesCollection.addSeries(...): cannot add null series.");
        }

        this.data.add(series);
        series.addChangeListener(this);
        
        // look for any keys that we don't already know about...
        Iterator iterator = series.getTasks().iterator();
        while (iterator.hasNext()) {
            Task task = (Task) iterator.next();
            String key = task.getDescription();
            int index = this.keys.indexOf(key);
            if (index < 0) {
                this.keys.add(key);
            }
        }

        fireDatasetChanged();
        
    }

    /**
     * Removes a series from the collection.
     * <P>
     * Notifies all registered listeners that the dataset has changed.
     *
     * @param series  the series (zero based index).
     */
    public void remove(int series) {

        // check arguments...
        if ((series < 0) || (series > getSeriesCount())) {
            throw new IllegalArgumentException(
                "TaskSeriesCollection.remove(...): index outside valid range.");
        }

        // fetch the series, remove the change listener, then remove the series.
        TaskSeries ts = (TaskSeries) data.get(series);
        ts.removeChangeListener(this);
        data.remove(series);
        fireDatasetChanged();

    }

    /**
     * Removes a series from the collection.
     * <P>
     * Notifies all registered listeners that the dataset has changed.
     *
     * @param series  the series.
     */
    public void remove(TaskSeries series) {

        // check arguments...
        if (series == null) {
            throw new IllegalArgumentException(
                "TaskSeriesCollection.remove(...): cannot remove null series.");
        }

        // remove the series...
        if (this.data.contains(series)) {
            series.removeChangeListener(this);
            this.data.remove(series);
            fireDatasetChanged();
        }

    }

    /**
     * Removes all the series from the collection.
     * <P>
     * Notifies all registered listeners that the dataset has changed.
     */
    public void removeAll() {

        // deregister the collection as a change listener to each series in the collection.
        Iterator iterator = this.data.iterator();
        while (iterator.hasNext()) {
            TaskSeries series = (TaskSeries) iterator.next();
            series.removeChangeListener(this);
        }
        
        // remove all the series from the collection and notify listeners.
        data.clear();
        fireDatasetChanged();

    }

    /**
     * Returns the value for an item.
     *
     * @param rowKey  the row key.
     * @param columnKey  the column key.
     *
     * @return The item value.
     */
    public Number getValue(Comparable rowKey, Comparable columnKey) {
        int row = getRowIndex(rowKey);
        int column = getColumnIndex(columnKey);
        return getValue(row, column);
    }

    /**
    /**
     * Returns the value for a task.
     *
     * @param row  the row index (zero-based).
     * @param column  the column index (zero-based).
     *
     * @return the start value.
     */
    public Number getValue(int row, int column) {
        return getStartValue(row, column);
    }

    /**
     * Returns the start value for a task.
     *
     * @param rowKey  the series.
     * @param columnKey  the category.
     *
     * @return the start value.
     */
    public Number getStartValue(Comparable rowKey, Comparable columnKey) {
        int row = getRowIndex(rowKey);
        int column = getColumnIndex(columnKey);
        return getStartValue(row, column);
    }

    /**
     * Returns the start value for a task.
     *
     * @param row  the row index (zero-based).
     * @param column  the column index (zero-based).
     *
     * @return the start value.
     */
    public Number getStartValue (int row, int column) {

        Number result = null;
        TaskSeries series = (TaskSeries) this.data.get(row);
        int tasks = series.getItemCount();
        if (column <= tasks) {
            Task task = series.get(column);
            result = new Long(task.getDuration().getStart().getTime());
        }
        return result;

    }

    /**
     * Returns the end value for a task.
     *
     * @param rowKey  the series.
     * @param columnKey  the category.
     *
     * @return the end value.
     */
    public Number getEndValue (Comparable rowKey, Comparable columnKey) {
        int row = getRowIndex(rowKey);
        int column = getColumnIndex(columnKey);
        return getEndValue(row, column);
    }

    /**
     * Returns the end value for a task.
     *
     * @param row  the row index (zero-based).
     * @param column  the column index (zero-based).
     *
     * @return the end value.
     */
    public Number getEndValue (int row, int column) {

        Number result = null;
        TaskSeries series = (TaskSeries) this.data.get(row);
        int tasks = series.getItemCount();
        if (column <= tasks) {
            Task task = series.get(column);
            result = new Long(task.getDuration().getEnd().getTime());
        }
        return result;

    }

    /**
     * Returns the number of sub-intervals for a given item.
     *
     * @param row  the row index (zero-based).
     * @param column  the column index (zero-based).
     *
     * @return the sub-interval count.
     */
    public int getSubIntervalCount(int row, int column) {

        int result = 0;
        TaskSeries series = (TaskSeries) this.data.get(row);
        int tasks = series.getItemCount();
        if (column <= tasks) {
            Task task = series.get(column);
            result = task.getSubtaskCount();
        }
        return result;

    }

    /**
     * Returns the number of sub-intervals for a given item.
     *
     * @param rowKey  the row key.
     * @param columnKey  the column key.
     *
     * @return the sub-interval count.
     */
    public int getSubIntervalCount(Comparable rowKey, Comparable columnKey) {

        int row = getRowIndex(rowKey);
        int column = getColumnIndex(columnKey);
        return getSubIntervalCount(row, column);

    }

    /**
     * Returns the start value of a sub-interval for a given item.
     *
     * @param row  the row index (zero-based).
     * @param column  the column index (zero-based).
     * @param subinterval  the sub-interval index (zero-based).
     *
     * @return the start value (possibly <code>null</code>).
     */
    public Number getStartValue(int row, int column, int subinterval) {

        Number result = null;
        TaskSeries series = (TaskSeries) this.data.get(row);
        int tasks = series.getItemCount();
        if (column <= tasks) {
            Task task = series.get(column);
            Task subtask = task.getSubtask(subinterval);
            result = new Long(subtask.getDuration().getStart().getTime());
        }
        return result;

    }

    /**
     * Returns the start value of a sub-interval for a given item.
     *
     * @param rowKey  the row key.
     * @param columnKey  the column key.
     * @param subinterval  the subinterval.
     *
     * @return the start value (possibly <code>null</code>).
     */
    public Number getStartValue(Comparable rowKey, Comparable columnKey, int subinterval) {

        int row = getRowIndex(rowKey);
        int column = getColumnIndex(columnKey);
        return getStartValue(row, column, subinterval);

    }

    /**
     * Returns the end value of a sub-interval for a given item.
     *
     * @param row  the row index (zero-based).
     * @param column  the column index (zero-based).
     * @param subinterval  the subinterval.
     *
     * @return the end value (possibly <code>null</code>).
     */
    public Number getEndValue(int row, int column, int subinterval) {

        Number result = null;
        TaskSeries series = (TaskSeries) this.data.get(row);
        int tasks = series.getItemCount();
        if (column <= tasks) {
            Task task = series.get(column);
            Task subtask = task.getSubtask(subinterval);
            result = new Long(subtask.getDuration().getEnd().getTime());
        }
        return result;

    }

    /**
     * Returns the end value of a sub-interval for a given item.
     *
     * @param rowKey  the row key.
     * @param columnKey  the column key.
     * @param subinterval  the subinterval.
     *
     * @return the end value (possibly <code>null</code>).
     */
    public Number getEndValue(Comparable rowKey, Comparable columnKey, int subinterval) {

        int row = getRowIndex(rowKey);
        int column = getColumnIndex(columnKey);
        return getStartValue(row, column, subinterval);

    }
    
    /**
     * Called when a series belonging to the dataset changes.
     *
     * @param event  information about the change.
     */
    public void seriesChanged(SeriesChangeEvent event) {
        refreshKeys();
        fireDatasetChanged();
    }
    
    private void refreshKeys() {
        
        this.keys.clear();
        for (int i = 0; i < getSeriesCount(); i++) {
            TaskSeries series = (TaskSeries) this.data.get(i);
            // look for any keys that we don't already know about...
            Iterator iterator = series.getTasks().iterator();
            while (iterator.hasNext()) {
                Task task = (Task) iterator.next();
                String key = task.getDescription();
                int index = this.keys.indexOf(key);
                if (index < 0) {
                    this.keys.add(key);
                }
            }
        }

    }

}
