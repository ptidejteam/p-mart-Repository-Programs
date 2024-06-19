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
 * --------------------------------------
 * DefaultStatisticalCategoryDataset.java
 * --------------------------------------
 * (C) Copyright 2002, 2003, by Pascal Collet.
 *
 * Original Author:  Pascal Collet;
 * Contributor(s):   David Gilbert (for Object Refinery Limited);
 *
 * $Id: DefaultStatisticalCategoryDataset.java,v 1.1 2007/10/10 19:15:28 vauchers Exp $
 *
 * Changes
 * -------
 * 21-Aug-2002 : Version 1, contributed by Pascal Collet (DG);
 * 07-Oct-2002 : Fixed errors reported by Checkstyle (DG);
 * 05-Feb-2003 : Revised implementation to use KeyedObjects2D (DG);
 *
 */

package org.jfree.data;

import java.util.List;

/**
 * A convenience class that provides a default implementation of the
 * {@link StatisticalCategoryDataset} interface.
 * <p>
 * The standard constructor accepts data in a two dimensional array where the
 * first dimension is the series, and the second dimension is the category.
 *
 * @author Pascal Collet
 */
public class DefaultStatisticalCategoryDataset extends AbstractDataset
                                               implements StatisticalCategoryDataset,
                                                          RangeInfo {

    /** Storage for the data. */
    private KeyedObjects2D data;

    /** The minimum range value. */
    private Number minimumRangeValue;

    /** The maximum range value. */
    private Number maximumRangeValue;

    /** The range of values. */
    private Range valueRange;

    /**
     * Creates a new dataset.
     */
    public DefaultStatisticalCategoryDataset() {

        this.data = new KeyedObjects2D();
        this.minimumRangeValue = new Double(0.0);
        this.maximumRangeValue = new Double(0.0);
        this.valueRange = new Range(0.0, 0.0);

    }

    /**
     * Returns the mean value for an item.
     *
     * @param row  the row index (zero-based).
     * @param column  the column index (zero-based).
     *
     * @return the mean value.
     */
    public Number getMeanValue (int row, int column) {

        Number result = null;
        MeanAndStandardDeviation masd = (MeanAndStandardDeviation) this.data.getObject(row, column);
        if (masd != null) {
            result = masd.getMean();
        }
        return result;

    }

    /**
     * Returns the value for an item.
     *
     * @param row  the row index.
     * @param column  the column index.
     *
     * @return the value.
     */
    public Number getValue (int row, int column) {
        return getMeanValue(row, column);
    }

    /**
     * Returns the value for an item.
     *
     * @param rowKey  the row key.
     * @param columnKey  the columnKey.
     *
     * @return the value.
     */
    public Number getValue (Comparable rowKey, Comparable columnKey) {
        return getMeanValue(rowKey, columnKey);
    }

    /**
     * Returns the mean value for an item.
     *
     * @param rowKey  the row key.
     * @param columnKey  the columnKey.
     *
     * @return the mean value.
     */
    public Number getMeanValue (Comparable rowKey, Comparable columnKey) {

        Number result = null;
        MeanAndStandardDeviation masd
            = (MeanAndStandardDeviation) this.data.getObject(rowKey, columnKey);
        if (masd != null) {
            result = masd.getMean();
        }
        return result;

    }

    /**
     * Returns the standard deviation value for an item.
     *
     * @param row  the row index (zero-based).
     * @param column  the column index (zero-based).
     *
     * @return the standard deviation.
     */
    public Number getStdDevValue (int row, int column) {

        Number result = null;
        MeanAndStandardDeviation masd = (MeanAndStandardDeviation) this.data.getObject(row, column);
        if (masd != null) {
            result = masd.getStandardDeviation();
        }
        return result;

    }

    /**
     * Returns the standard deviation value for an item.
     *
     * @param rowKey  the row key.
     * @param columnKey  the columnKey.
     *
     * @return the standard deviation.
     */
    public Number getStdDevValue (Comparable rowKey, Comparable columnKey) {

        Number result = null;
        MeanAndStandardDeviation masd
            = (MeanAndStandardDeviation) this.data.getObject(rowKey, columnKey);
        if (masd != null) {
            result = masd.getStandardDeviation();
        }
        return result;

    }

    /**
     * Returns the column index for a given key.
     *
     * @param key  the column key.
     *
     * @return the column index.
     */
    public int getColumnIndex(Comparable key) {
        return this.data.getColumnIndex(key);
    }

    /**
     * Returns a column key.
     *
     * @param column  the column index (zero-based).
     *
     * @return the column key.
     */
    public Comparable getColumnKey(int column) {
        return this.data.getColumnKey(column);
    }

    /**
     * Returns the column keys.
     *
     * @return the keys.
     */
    public List getColumnKeys() {
        return this.data.getColumnKeys();
    }

    /**
     * Returns the row index for a given key.
     *
     * @param key  the row key.
     *
     * @return the row index.
     */
    public int getRowIndex(Comparable key) {
        return this.data.getRowIndex(key);
    }

    /**
     * Returns a row key.
     *
     * @param row  the row index (zero-based).
     *
     * @return the row key.
     */
    public Comparable getRowKey(int row) {
        return this.data.getRowKey(row);
    }

    /**
     * Returns the row keys.
     *
     * @return the keys.
     */
    public List getRowKeys() {
        return this.data.getRowKeys();
    }

    /**
     * Returns the number of rows in the table.
     *
     * @return the row count.
     */
    public int getRowCount() {
        return this.data.getRowCount();
    }

    /**
     * Returns the number of columns in the table.
     *
     * @return the column count.
     */
    public int getColumnCount() {
        return this.data.getColumnCount();
    }

    /**
     * Adds a mean and standard deviation to the table.
     *
     * @param mean  the mean.
     * @param standardDeviation  the standard deviation.
     * @param rowKey  the row key.
     * @param columnKey  the column key.
     */
    public void add(double mean, double standardDeviation,
                    Comparable rowKey, Comparable columnKey) {

        MeanAndStandardDeviation item = new MeanAndStandardDeviation(new Double(mean),
                                                                     new Double(standardDeviation));

        this.data.addObject(item, rowKey, columnKey);
        if ((mean + standardDeviation) > this.maximumRangeValue.doubleValue()) {
            this.maximumRangeValue = new Double(mean + standardDeviation);
            this.valueRange = new Range(this.minimumRangeValue.doubleValue(),
                                        this.maximumRangeValue.doubleValue());
        }
        if ((mean - standardDeviation) < this.minimumRangeValue.doubleValue()) {
            this.minimumRangeValue = new Double(mean - standardDeviation);
            this.valueRange = new Range(this.minimumRangeValue.doubleValue(),
                                        this.maximumRangeValue.doubleValue());
        }

        fireDatasetChanged();

    }

    /**
     * Returns the minimum value in the dataset's range (or null if all the
     * values in the range are null).
     *
     * @return the minimum value.
     */
    public Number getMinimumRangeValue() {
        return this.minimumRangeValue;
    }

    /**
     * Returns the maximum value in the dataset's range (or null if all the
     * values in the range are null).
     *
     * @return the maximum value.
     */
    public Number getMaximumRangeValue() {
        return this.maximumRangeValue;
    }

    /**
     * Returns the range of the values in this dataset's range.
     *
     * @return the range.
     */
    public Range getValueRange() {
        return this.valueRange;
    }

}
