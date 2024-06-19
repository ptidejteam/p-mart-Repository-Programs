/* ======================================
 * JFreeChart : a free Java chart library
 * ======================================
 *
 * Project Info:  http://www.object-refinery.com/jfreechart/index.html
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
 * ---------------------------
 * DefaultCategoryDataset.java
 * ---------------------------
 * (C) Copyright 2002, 2003, by Simba Management Limited.
 *
 * Original Author:  David Gilbert (for Simba Management Limited);
 * Contributor(s):   -;
 *
 * $Id: DefaultCategoryDataset.java,v 1.1 2007/10/10 19:54:14 vauchers Exp $
 *
 * Changes
 * -------
 * 21-Jan-2003 : Added standard header, and renamed DefaultCategoryDataset (DG);
 *
 */
package com.jrefinery.data;

import java.util.List;

/**
 * A default implementation of the {@link CategoryDataset} interface.
 * 
 * @author David Gilbert
 */
public class DefaultCategoryDataset extends AbstractDataset
                                    implements CategoryDataset {

    /** A storage structure for the data. */
    private DefaultKeyedValues2D data;

    /**
     * Creates a new (empty) dataset.
     */
    public DefaultCategoryDataset() {
        this.data = new DefaultKeyedValues2D();
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
     * Returns a value from the table.
     *
     * @param row  the row index (zero-based).
     * @param column  the column index (zero-based).
     *
     * @return the value (possibly null).
     */
    public Number getValue(int row, int column) {
        return this.data.getValue(row, column);
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
     * Returns the row keys.
     *
     * @return the keys.
     */
    public List getRowKeys() {
        return this.data.getRowKeys();
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
     * Returns the column keys.
     *
     * @return the keys.
     */
    public List getColumnKeys() {
        return this.data.getColumnKeys();
    }

    /**
     * Returns the value for a pair of keys.
     * <P>
     * This method should return <code>null</code> if either of the keys is not found.
     *
     * @param rowKey  the row key.
     * @param columnKey  the column key.
     *
     * @return the value.
     */
    public Number getValue(Comparable rowKey, Comparable columnKey) {
        return this.data.getValue(rowKey, columnKey);
    }

    /**
     * Adds a value to the table.  Performs the same function as setValue(...).
     * 
     * @param value  the value.
     * @param rowKey  the row key.
     * @param columnKey  the column key.
     */
    public void addValue(Number value, Comparable rowKey, Comparable columnKey) {
        this.data.addValue(value, rowKey, columnKey);
        fireDatasetChanged();
    }

    /**
     * Adds a value to the table.
     * 
     * @param value  the value.
     * @param rowKey  the row key.
     * @param columnKey  the column key.
     */
    public void addValue(double value, Comparable rowKey, Comparable columnKey) {
        this.addValue(new Double(value), rowKey, columnKey);
    }

    /**
     * Adds or updates a value in the table.
     * 
     * @param value  the value.
     * @param rowKey  the row key.
     * @param columnKey  the column key.
     */
    public void setValue(Number value, Comparable rowKey, Comparable columnKey) {
        this.data.setValue(value, rowKey, columnKey);
        fireDatasetChanged();
    }

    /**
     * Adds or updates a value in the table.
     * 
     * @param value  the value.
     * @param rowKey  the row key.
     * @param columnKey  the column key.
     */
    public void setValue(double value, Comparable rowKey, Comparable columnKey) {
        this.setValue(new Double(value), rowKey, columnKey);
    }

    /**
     * Removes a value from the dataset.
     * 
     * @param rowKey  the row key.
     * @param columnKey  the column key.
     */
    public void removeValue(Comparable rowKey, Comparable columnKey) {
        this.data.removeValue(rowKey, columnKey);
        fireDatasetChanged();
    }

    /**
     * Removes a row from the dataset.
     * 
     * @param rowIndex  the row index.
     */
    public void removeRow(int rowIndex) {
        this.data.removeRow(rowIndex);
        fireDatasetChanged();
    }

    /**
     * Removes a row from the dataset.
     * 
     * @param rowKey  the row key.
     */
    public void removeRow(Comparable rowKey) {
        this.data.removeRow(rowKey);
        fireDatasetChanged();
    }

    /**
     * Removes a column from the dataset.
     * 
     * @param columnIndex  the column index.
     */
    public void removeColumn(int columnIndex) {
        this.data.removeColumn(columnIndex);
        fireDatasetChanged();
    }

    /**
     * Removes a column from the dataset.
     * 
     * @param columnKey  the column key.
     */
    public void removeColumn(Comparable columnKey) {
        this.data.removeColumn(columnKey);
        fireDatasetChanged();
    }

}
