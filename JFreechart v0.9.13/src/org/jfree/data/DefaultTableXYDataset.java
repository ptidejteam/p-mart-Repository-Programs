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
 * -------------------
 * DefaultTableXYDataset.java
 * -------------------
 * (C) Copyright 2003 by Richard Atkinson.
 *
 * Original Author:  Richard Atkinson;
 * Contributor(s):   -;
 *
 * $Id: DefaultTableXYDataset.java,v 1.1 2007/10/10 19:15:28 vauchers Exp $
 *
 * Changes:
 * --------
 * 27-Jul-2003 : XYDataset that forces each series to have a value for every X-point
 *               which is essential for stacked XY area charts (RA);
 * 18-Aug-2003 : Fixed event notification when removing and updating series (RA);
 * 22-Sep-2003 : Functionality moved from TableXYDataset to DefaultTableXYDataset (RA);
 *
 */
package org.jfree.data;

import java.util.ArrayList;
import java.util.List;

import org.jfree.util.ObjectUtils;

/**
 * An {@link XYDataset} where every series shares the same x-values (required for
 * generating stacked area charts).
 * 
 * @author Richard Atkinson
 */
public class DefaultTableXYDataset extends AbstractSeriesDataset implements TableXYDataset {
    
    /** Storage for the data. */
    private List data = null;
    
    /** Storage for the x values. */
    private List xPoints = null;
    
    /** A flag that controls whether or not events are propogated. */
    private boolean propagateEvents = true;

    /**
     * Creates a new empty dataset.
     */
    public DefaultTableXYDataset() {
        this.data = new ArrayList();
        this.xPoints = new ArrayList();
    }

    /**
     * Constructs a dataset and populates it with a single time series.
     *
     * @param series  the time series.
     */
    public DefaultTableXYDataset(XYSeries series) {

        this.data = new ArrayList();
        this.xPoints = new ArrayList();
        if (series != null) {
            if (series.getAllowDuplicateXValues()) {
                throw new IllegalArgumentException("Cannot accept XY Series that allow duplicate values.  Use XYSeries(seriesName, false) constructor.");
            }
            updateXPoints(series);
            data.add(series);
            series.addChangeListener(this);
        }

    }

    /**
     * Adds a series to the collection.
     * <P>
     * Notifies all registered listeners that the dataset has changed.
     *
     * @param series  the series.
     */
    public void addSeries(XYSeries series) {

        // check arguments...
        if (series == null) {
            throw new IllegalArgumentException(
                "XYSeriesCollection.addSeries(...): cannot add null series.");
        }
        if (series.getAllowDuplicateXValues()) {
            throw new IllegalArgumentException("Cannot accept XY Series that allow duplicate values.  Use XYSeries(seriesName, false) constructor.");
        }

        // add the series...
        this.updateXPoints(series);
        data.add(series);
        series.addChangeListener(this);
        fireDatasetChanged();

    }

    /**
     * Updates XPoints for a particular series.
     *
     * @param series  the series.
     */
    private void updateXPoints(XYSeries series) {
        ArrayList seriesXPoints = new ArrayList();
        boolean savedState = this.propagateEvents;
        this.propagateEvents = false;
        for (int itemNo = 0; itemNo < series.getItemCount(); itemNo++) {
            Number xValue = series.getXValue(itemNo);
            seriesXPoints.add(xValue);
            if (!xPoints.contains(xValue)) {
                xPoints.add(xValue);
                for (int seriesNo = 0; seriesNo < this.data.size(); seriesNo++) {
                    XYSeries dataSeries = (XYSeries) data.get(seriesNo);
                    if (!dataSeries.equals(series)) {
                        dataSeries.add(xValue, null);
                    } 
                }
            }
        }
        for (int itemNo = 0; itemNo < xPoints.size(); itemNo++) {
            Number xPoint = (Number) this.xPoints.get(itemNo);
            if (!seriesXPoints.contains(xPoint)) {
                series.add(xPoint, null);
            }
        }
        this.propagateEvents = savedState;
    }

    /**
     * Updates XPoints for all series.
     */
    public void updateXPoints() {
        this.propagateEvents = false;
        for (int seriesNo = 0; seriesNo < data.size(); seriesNo++) {
            updateXPoints((XYSeries) data.get(seriesNo));
        }
        this.propagateEvents = true;
    }

    /**
     * Returns the number of series in the collection.
     *
     * @return the number of series in the collection.
     */
    public int getSeriesCount() {
        return this.data.size();
    }

    /**
     * Returns the number of X points in the Dataset.
     *
     * @return the number of X points in the Dataset
     */
    public int getItemCount() {
        if (this.xPoints == null) {
            return 0;
        } 
        else {
            return this.xPoints.size();
        }
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
        XYSeries ts = (XYSeries) data.get(series);
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

        XYSeries ts = (XYSeries) data.get(series);
        XYDataItem dataItem = ts.getDataItem(item);
        return dataItem.getX();

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

        XYSeries ts = (XYSeries) data.get(series);
        XYDataItem dataItem = ts.getDataItem(index);
        return dataItem.getY();

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
        data.clear();
        xPoints.clear();
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
        if (data.contains(series)) {
            series.removeChangeListener(this);
            data.remove(series);
            if (data.size() == 0) xPoints.clear();
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
        XYSeries ts = (XYSeries) data.get(series);
        ts.removeChangeListener(this);
        data.remove(series);
        if (data.size() == 0) xPoints.clear();
        fireDatasetChanged();

    }

    /**
     * Called when a series belonging to the dataset changes.
     *
     * @param event  information about the change.
     */
    public void seriesChanged(SeriesChangeEvent event) {
        if (this.propagateEvents) {
            updateXPoints();
            fireDatasetChanged();
        }
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

        if (obj instanceof DefaultTableXYDataset) {
            DefaultTableXYDataset c = (DefaultTableXYDataset) obj;
            return ObjectUtils.equal(this.data, c.data);
        }

        return false;

    }

}
