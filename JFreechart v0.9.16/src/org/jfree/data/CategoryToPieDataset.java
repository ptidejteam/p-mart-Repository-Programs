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
 * -------------------------
 * CategoryToPieDataset.java
 * -------------------------
 * (C) Copyright 2003, 2004, by Object Refinery Limited.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited);
 * Contributor(s):   Christian W. Zuckschwerdt;
 *
 * $Id: CategoryToPieDataset.java,v 1.1 2007/10/10 19:25:30 vauchers Exp $
 *
 * Changes
 * -------
 * 23-Jan-2003 : Version 1 (DG);
 * 30-Jul-2003 : Pass through DatasetChangeEvent (CZ);
 *
 */

package org.jfree.data;

import java.util.List;

/**
 * A {@link PieDataset} implementation that obtains its data from one row or column of a
 * {@link CategoryDataset}.
 *
 * @author David Gilbert
 */
public class CategoryToPieDataset extends AbstractDataset 
                                  implements PieDataset, DatasetChangeListener {

    /** A constant indicating that data should be extracted from a row. */
    public static final int ROW = 0;

    /** A constant indicating that data should be extracted from a column. */
    public static final int COLUMN = 1;

    /** The source. */
    private CategoryDataset source;

    /** The extract type. */
    private int extract;

    /** The row or column index. */
    private int index;

    /**
     * An adaptor class that converts any {@link CategoryDataset} into a {@link PieDataset}, by
     * taking the values from a single row or column.
     *
     * @param source  the source dataset.
     * @param extract  ROW or COLUMN.
     * @param index  the row or column index.
     */
    public CategoryToPieDataset(CategoryDataset source, int extract, int index) {
        this.source = source;
        this.source.addChangeListener(this);
        this.extract = extract;
        this.index = index;
    }

    /**
     * Returns the number of items (values) in the collection.
     *
     * @return the item count.
     */
    public int getItemCount() {

        int result = 0;
        switch (this.extract) {
            case (ROW) :
                result = source.getColumnCount();
                break;
            case (COLUMN) :
                result = source.getRowCount();
                break;
            default : // error
        }
        return result;
    }

    /**
     * Returns a value.
     *
     * @param item  the item index (zero-based).
     *
     * @return the value.
     */
    public Number getValue(int item) {

        Number result = null;
        switch (this.extract) {
            case (ROW) :
                result = source.getValue(this.index, item);
                break;
            case (COLUMN) :
                result = source.getValue(item, this.index);
                break;
            default : // error
        }
        return result;

    }

    /**
     * Returns a key.
     *
     * @param index  the item index (zero-based).
     *
     * @return the key.
     */
    public Comparable getKey(int index) {

        Comparable result = null;
        switch (this.extract) {
            case (ROW) :
                result = source.getColumnKey(index);
                break;
            case (COLUMN) :
                result = source.getRowKey(index);
                break;
            default : // error
        }
        return result;

    }

    /**
     * Returns the index for a given key.
     *
     * @param key  the key.
     *
     * @return the index.
     */
    public int getIndex(Comparable key) {

        int result = -1;
        switch (this.extract) {
            case (ROW) :
                result = source.getColumnIndex(key);
                break;
            case (COLUMN) :
                result = source.getRowIndex(key);
                break;
            default : // error
        }
        return result;

    }

    /**
     * Returns the keys.
     *
     * @return the keys.
     */
    public List getKeys() {

        List result = null;
        switch (this.extract) {
            case (ROW) :
                result = source.getColumnKeys();
                break;
            case (COLUMN) :
                result = source.getRowKeys();
                break;
            default : // error
        }
        return result;

    }

    /**
     * Returns the value (possibly null) for a given key.
     * <P>
     * If the key is not recognised, the method should return null.
     *
     * @param key  the key.
     *
     * @return the value.
     */
    public Number getValue(Comparable key) {

        Number result = null;
        int keyIndex = getIndex(key);
        switch (this.extract) {
            case (ROW) :
                result = source.getValue(this.index, keyIndex);
                break;
            case (COLUMN) :
                result = source.getValue(keyIndex, this.index);
                break;
            default : // error
        }
        return result;

    }
    
    /**
     * Passes the {@link DatasetChangeEvent} through.
     * 
     * @param event  the event.
     */
    public void datasetChanged (DatasetChangeEvent event) {
        fireDatasetChanged ();
    }
    
}
