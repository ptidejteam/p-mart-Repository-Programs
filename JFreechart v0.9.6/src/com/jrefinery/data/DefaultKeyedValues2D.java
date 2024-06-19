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
 * -------------------------
 * DefaultKeyedValues2D.java
 * -------------------------
 * (C) Copyright 2002, 2003, by Simba Management Limited.
 *
 * Original Author:  David Gilbert (for Simba Management Limited);
 * Contributor(s):   -;
 *
 * $Id: DefaultKeyedValues2D.java,v 1.1 2007/10/10 19:57:54 vauchers Exp $
 *
 * Changes
 * -------
 * 28-Oct-2002 : Version 1 (DG);
 * 21-Jan-2003 : Updated Javadocs (DG);
 *
 */

package com.jrefinery.data;

import java.util.List;
import java.util.Iterator;
import java.util.Collections;

/**
 * A data structure that stores zero, one or many values, where each value is associated with
 * two keys (a 'row' key and a 'column' key).
 *
 * @author David Gilbert
 */
public class DefaultKeyedValues2D implements KeyedValues2D {

    /** The row keys. */
    private List rowKeys;

    /** The column keys. */
    private List columnKeys;

    /** The row data. */
    private List rows;

    /**
     * Creates a new instance (initially empty).
     */
    public DefaultKeyedValues2D() {
        this.rowKeys = new java.util.ArrayList();
        this.columnKeys = new java.util.ArrayList();
        this.rows = new java.util.ArrayList();
    }

    /**
     * Returns the row count.
     *
     * @return the row count.
     */
    public int getRowCount() {
        return this.rowKeys.size();
    }

    /**
     * Returns the column count.
     *
     * @return the column count.
     */
    public int getColumnCount() {
        return this.columnKeys.size();
    }

    /**
     * Returns the value for a given row and column.
     *
     * @param row  the row index.
     * @param column  the column index.
     *
     * @return the value.
     */
    public Number getValue(int row, int column) {

        Number result = null;
        DefaultKeyedValues rowData = (DefaultKeyedValues) this.rows.get(row);
        if (rowData != null) {
            Comparable columnKey = (Comparable) this.columnKeys.get(column);
            if (columnKey != null) {
                result = rowData.getValue(columnKey);
            }
        }
        return result;

    }

    /**
     * Returns the key for a given row.
     *
     * @param row  the row index (zero based).
     *
     * @return the row index.
     */
    public Comparable getRowKey(int row) {
        return (Comparable) this.rowKeys.get(row);
    }

    /**
     * Returns the row index for a given key.
     *
     * @param key  the key.
     *
     * @return the row index.
     */
    public int getRowIndex(Comparable key) {
        return this.rowKeys.indexOf(key);
    }

    /**
     * Returns the row keys.
     *
     * @return the row keys.
     */
    public List getRowKeys() {
        return Collections.unmodifiableList(this.rowKeys);
    }

    /**
     * Returns the key for a given column.
     *
     * @param column  the column.
     *
     * @return the key.
     */
    public Comparable getColumnKey(int column) {
        return (Comparable) this.columnKeys.get(column);
    }

    /**
     * Returns the column index for a given key.
     *
     * @param key  the key.
     *
     * @return the column index.
     */
    public int getColumnIndex(Comparable key) {
        return this.columnKeys.indexOf(key);
    }

    /**
     * Returns the column keys.
     *
     * @return the column keys.
     */
    public List getColumnKeys() {
        return Collections.unmodifiableList(this.columnKeys);
    }

    /**
     * Returns the value for the given row and column keys.
     *
     * @param rowKey  the row key.
     * @param columnKey  the column key.
     *
     * @return the value.
     */
    public Number getValue(Comparable rowKey, Comparable columnKey) {

        Number result = null;
        int row = this.rowKeys.indexOf(rowKey);
        if (row >= 0) {
            DefaultKeyedValues rowData = (DefaultKeyedValues) this.rows.get(row);
            result = rowData.getValue(columnKey);
        }
        return result;

    }

    /**
     * Adds a value to the table.  Performs the same function as setValue(...).
     * 
     * @param value  the value.
     * @param rowKey  the row key.
     * @param columnKey  the column key.
     */
    public void addValue(Number value, Comparable rowKey, Comparable columnKey) {
        setValue(value, rowKey, columnKey);
    }

    /**
     * Adds or updates a value.
     * 
     * @param value  the value.
     * @param rowKey  the row key.
     * @param columnKey  the column key.
     */
    public void setValue(Number value, Comparable rowKey, Comparable columnKey) {

        DefaultKeyedValues row = null;
        int rowIndex = this.rowKeys.indexOf(rowKey);
        if (rowIndex >= 0) {
            row = (DefaultKeyedValues) this.rows.get(rowIndex);
        }
        else {
            this.rowKeys.add(rowKey);
            row = new DefaultKeyedValues();
            this.rows.add(row);
        }
        row.setValue(columnKey, value);
        int columnIndex = this.columnKeys.indexOf(columnKey);
        if (columnIndex < 0) {
            this.columnKeys.add(columnKey);
        }

    }

    /**
     * Removes a value.
     * 
     * @param rowKey  the row key.
     * @param columnKey  the column key.
     */
    public void removeValue(Comparable rowKey, Comparable columnKey) {
        setValue(null, rowKey, columnKey);
        // actually, a null value is different to a value that doesn't exist at all.
        // need to fix this code.
    }

    /**
     * Removes a row.
     * 
     * @param rowIndex  the row index.
     */
    public void removeRow(int rowIndex) {
        this.rowKeys.remove(rowIndex);
        this.rows.remove(rowIndex);
    }

    /**
     * Removes a row.
     * 
     * @param rowKey  the row key.
     */
    public void removeRow(Comparable rowKey) {
        removeRow(getRowIndex(rowKey));
    }

    /**
     * Removes a column.
     * 
     * @param columnIndex  the column index.
     */
    public void removeColumn(int columnIndex) {
        Comparable columnKey = getColumnKey(columnIndex);
        removeColumn(columnKey);
    }

    /**
     * Removes a column.
     * 
     * @param columnKey  the column key.
     */
    public void removeColumn(Comparable columnKey) {
        Iterator iterator = rows.iterator();
        while (iterator.hasNext()) {
            DefaultKeyedValues rowData = (DefaultKeyedValues) iterator.next();
            rowData.removeValue(columnKey);
        }
        this.columnKeys.remove(columnKey);
    }

}
