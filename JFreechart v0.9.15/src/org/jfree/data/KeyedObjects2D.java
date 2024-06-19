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
 * ------------------
 * KeyedObject2D.java
 * ------------------
 * (C) Copyright 2003, by Object Refinery Limited.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited);
 * Contributor(s):   -;
 *
 * $Id: KeyedObjects2D.java,v 1.1 2007/10/10 19:21:49 vauchers Exp $
 *
 * Changes
 * -------
 * 05-Feb-2003 : Version 1 (DG);
 *
 */

package org.jfree.data;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * A data structure that stores zero, one or many objects, where each object is associated with
 * two keys (a 'row' key and a 'column' key).
 *
 * @author David Gilbert
 */
public class KeyedObjects2D {

    /** The row keys. */
    private List rowKeys;

    /** The column keys. */
    private List columnKeys;

    /** The row data. */
    private List rows;

    /**
     * Creates a new instance (initially empty).
     */
    public KeyedObjects2D() {
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
     * Returns the object for a given row and column.
     *
     * @param row  the row index.
     * @param column  the column index.
     *
     * @return the object.
     */
    public Object getObject(int row, int column) {

        Object result = null;
        KeyedObjects rowData = (KeyedObjects) this.rows.get(row);
        if (rowData != null) {
            Comparable columnKey = (Comparable) this.columnKeys.get(column);
            if (columnKey != null) {
                result = rowData.getObject(columnKey);
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
     * Returns the object for the given row and column keys.
     *
     * @param rowKey  the row key.
     * @param columnKey  the column key.
     *
     * @return the object.
     */
    public Object getObject(Comparable rowKey, Comparable columnKey) {

        Object result = null;
        int row = this.rowKeys.indexOf(rowKey);
        if (row >= 0) {
            KeyedObjects rowData = (KeyedObjects) this.rows.get(row);
            result = rowData.getObject(columnKey);
        }
        return result;

    }

    /**
     * Adds an object to the table.  Performs the same function as setObject(...).
     *
     * @param object  the object.
     * @param rowKey  the row key.
     * @param columnKey  the column key.
     */
    public void addObject(Object object, Comparable rowKey, Comparable columnKey) {
        setObject(object, rowKey, columnKey);
    }

    /**
     * Adds or updates an object.
     *
     * @param object  the object.
     * @param rowKey  the row key.
     * @param columnKey  the column key.
     */
    public void setObject(Object object, Comparable rowKey, Comparable columnKey) {

        KeyedObjects row = null;
        int rowIndex = this.rowKeys.indexOf(rowKey);
        if (rowIndex >= 0) {
            row = (KeyedObjects) this.rows.get(rowIndex);
        }
        else {
            this.rowKeys.add(rowKey);
            row = new KeyedObjects();
            this.rows.add(row);
        }
        row.setObject(columnKey, object);
        int columnIndex = this.columnKeys.indexOf(columnKey);
        if (columnIndex < 0) {
            this.columnKeys.add(columnKey);
        }

    }

    /**
     * Removes an object.
     *
     * @param rowKey  the row key.
     * @param columnKey  the column key.
     */
    public void removeObject(Comparable rowKey, Comparable columnKey) {
        setObject(null, rowKey, columnKey);
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
            KeyedObjects rowData = (KeyedObjects) iterator.next();
            rowData.removeValue(columnKey);
        }
        this.columnKeys.remove(columnKey);
    }

}
