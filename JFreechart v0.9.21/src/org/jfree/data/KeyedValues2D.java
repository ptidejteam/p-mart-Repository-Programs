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
 * ------------------
 * KeyedValues2D.java
 * ------------------
 * (C) Copyright 2002-2004, by Object Refinery Limited.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited);
 * Contributor(s):   -;
 *
 * $Id: KeyedValues2D.java,v 1.1 2007/10/10 19:50:29 vauchers Exp $
 *
 * Changes
 * -------
 * 28-Oct-2002 : Version 1 (DG);
 *
 */

package org.jfree.data;

import java.util.List;


/**
 * An extension of the {@link Values2D} interface where a unique key is associated with the row
 * and column indices.
 */
public interface KeyedValues2D extends Values2D {

    /**
     * Returns the row key for a given index.
     *
     * @param row  the row index (zero-based).
     *
     * @return The row key.
     */
    public Comparable getRowKey(int row);

    /**
     * Returns the row index for a given key.
     *
     * @param key  the row key.
     *
     * @return The row index.
     */
    public int getRowIndex(Comparable key);

    /**
     * Returns the row keys.
     *
     * @return The keys.
     */
    public List getRowKeys();

    /**
     * Returns the column key for a given index.
     *
     * @param column  the column index (zero-based).
     *
     * @return The column key.
     */
    public Comparable getColumnKey(int column);

    /**
     * Returns the column index for a given key.
     *
     * @param key  the column key.
     *
     * @return The column index.
     */
    public int getColumnIndex(Comparable key);

    /**
     * Returns the column keys.
     *
     * @return The keys.
     */
    public List getColumnKeys();

    /**
     * Returns the value for a pair of keys.  This method should return <code>null</code> if
     * either of the keys is not found.
     *
     * @param rowKey  the row key.
     * @param columnKey  the column key.
     *
     * @return The value.
     */
    public Number getValue(Comparable rowKey, Comparable columnKey);

}
