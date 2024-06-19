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
 * TableXYDataset.java
 * -------------------
 * (C) Copyright 2003 by Richard Atkinson.
 *
 * Original Author:  Richard Atkinson;
 * Contributor(s):   -;
 *
 * $Id: TableXYDataset.java,v 1.1 2007/10/10 19:09:12 vauchers Exp $
 *
 * Changes:
 * --------
 * 27-Jul-2003 : XYDataset that forces each series to have a value for every X-point
 *               which is essential for stacked XY area charts (RA);
 *
 */
package org.jfree.data;

import org.jfree.util.ObjectUtils;

import java.util.ArrayList;
import java.util.List;

public class TableXYDataset extends AbstractSeriesDataset implements XYDataset {
    private List data = null;
    private List xPoints = null;

    public TableXYDataset() {
        this.data = new ArrayList();
        this.xPoints = new ArrayList();
    }

    /**
     * Constructs a dataset and populates it with a single time series.
     *
     * @param series  the time series.
     */
    public TableXYDataset(XYSeries series) {

        this.data = new ArrayList();
        this.xPoints = new ArrayList();
        if (series != null) {
            this.updateXPoints(series);
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

        // add the series...
        this.updateXPoints(series);
        data.add(series);
        series.addChangeListener(this);
        fireDatasetChanged();

    }

    private void updateXPoints(XYSeries series) {
        ArrayList seriesXPoints = new ArrayList();
        for (int itemNo=0; itemNo<series.getItemCount(); itemNo++) {
            Number xValue = series.getXValue(itemNo);
            seriesXPoints.add(xValue);
            if (!xPoints.contains(xValue)) {
                xPoints.add(xValue);
                for (int seriesNo=0; seriesNo<this.data.size(); seriesNo++) {
                    XYSeries dataSeries = (XYSeries)data.get(seriesNo);
                    dataSeries.add(xValue, new Integer(0));
                }
            }
        }
        for (int itemNo=0; itemNo<xPoints.size(); itemNo++) {
            Number xPoint = (Number)this.xPoints.get(itemNo);
            if (!seriesXPoints.contains(xPoint)) {
                series.add(xPoint, new Integer(0));
            }
        }
    }

    /**
     * Returns the number of series in the collection.
     *
     * @return the number of series in the collection.
     */
    public int getSeriesCount() {
        return this.data.size();
    }

    public int getItemCount() {
        if (this.data == null ? true : this.data.size() == 0) {
            return 0;
        } else {
            return ((XYSeries)this.data.get(0)).getItemCount();
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

        if (obj instanceof TableXYDataset) {
            TableXYDataset c = (TableXYDataset) obj;
            return ObjectUtils.equalOrBothNull(this.data, c.data);
        }

        return false;

    }

    public static void main(String[] args) {
        TableXYDataset tableXYDataSet = new TableXYDataset();
        XYSeries series1 = new XYSeries("Series 1");
        series1.add(1,1);
        series1.add(2,1);
        series1.add(4,1);
        series1.add(5,1);
        XYSeries series2 = new XYSeries("Series 2");
        series2.add(2,2);
        series2.add(3,2);
        series2.add(4,2);
        series2.add(5,2);
        series2.add(6,2);

        tableXYDataSet.addSeries(series1);
        tableXYDataSet.addSeries(series2);

        for (int seriesNo = 0; seriesNo < tableXYDataSet.getSeriesCount(); seriesNo++) {
            XYSeries series = tableXYDataSet.getSeries(seriesNo);
            System.out.println(series.getName() + ", ItemCount = " + series.getItemCount());
            for (int itemNo = 0; itemNo < series.getItemCount(); itemNo++) {
                XYDataItem pair = series.getDataItem(itemNo);
                System.out.println("X = " + pair.getX() + ", Y = " + pair.getY());
            }
        }

        Range range = DatasetUtilities.getStackedRangeExtent(tableXYDataSet);
        System.out.println("Maximum = " + range.getUpperBound());
        System.out.println("Minimum = " + range.getLowerBound());

    }


}
