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
 * -----------------------
 * DefaultKeyedValues.java
 * -----------------------
 * (C) Copyright 2002, 2003 by Simba Management Limited.
 *
 * Original Author:  David Gilbert (for Simba Management Limited);
 * Contributor(s):   -;
 *
 * $Id: DefaultKeyedValues.java,v 1.1 2007/10/10 19:54:14 vauchers Exp $
 *
 * Changes:
 * --------
 * 31-Oct-2002 : Version 1 (DG);
 *
 */

package com.jrefinery.data;

import java.util.List;
import java.util.Iterator;

/**
 * A collection of (key, value) pairs.
 * <P>
 * This class provides a default implementation of the {@link KeyedValues} interface.
 *
 * @author David Gilbert
 */
public class DefaultKeyedValues implements KeyedValues {

    /** Storage for the data. */
    private List data;

    /**
     * Creates a new collection (initially empty).
     */
    public DefaultKeyedValues() {
        this.data = new java.util.ArrayList();
    }

    /**
     * Returns the number of items (values) in the collection.
     *
     * @return the item count.
     */
    public int getItemCount() {
        return this.data.size();
    }

    /**
     * Returns a value.
     *
     * @param item  the item of interest (zero-based index).
     *
     * @return the value.
     */
    public Number getValue(int item) {

        Number result = null;
        KeyedValue kval = (KeyedValue) this.data.get(item);
        if (kval != null) {
            result = kval.getValue();
        }
        return result;

    }

    /**
     * Returns a key.
     *
     * @param index  the item index (zero-based).
     *
     * @return the row key.
     */
    public Comparable getKey(int index) {

        Comparable result = null;
        KeyedValue item = (KeyedValue) this.data.get(index);
        if (item != null) {
            result = item.getKey();
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
        int i = 0;
        Iterator iterator = this.data.iterator();
        while (iterator.hasNext()) {
            KeyedValue kv = (KeyedValue) iterator.next();
            if (kv.getKey().equals(key)) {
                result = i;
            }
            i++;
        }
        return result;

    }

    /**
     * Returns the keys.
     *
     * @return the keys.
     */
    public List getKeys() {

        List result = new java.util.ArrayList();
        Iterator iterator = this.data.iterator();
        while (iterator.hasNext()) {
            KeyedValue kv = (KeyedValue) iterator.next();
            result.add(kv.getKey());
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
        return getValue(getIndex(key));
    }

    /**
     * Adds a new value to the collection, or updates an existing value.
     * <P>
     * This is the same as the setValue(...) method.
     *
     * @param key  the key.
     * @param value  the value.
     */
    public void addValue(Comparable key, Number value) {
        setValue(key, value);
    }

    /**
     * Updates an existing value, or adds a new value to the collection.
     * <P>
     * This is the same as the addValue(...) method.
     *
     * @param key  the key.
     * @param value  the value.
     */
    public void setValue(Comparable key, Number value) {
        int keyIndex = getIndex(key);
        if (keyIndex >= 0) {
            DefaultKeyedValue kv = (DefaultKeyedValue) this.data.get(keyIndex);
            kv.setValue(value);
        }
        else {
            KeyedValue kv = new DefaultKeyedValue(key, value);
            this.data.add(kv);
        }
    }

    /**
     * Removes a value from the collection.
     *
     * @param index  the index of the item to remove.
     */
    public void removeValue(int index) {
        this.data.remove(index);
    }

    /**
     * Removes a value from the collection.
     *
     * @param key  the key of the item to remove.
     */
    public void removeValue(Comparable key) {
        removeValue(getIndex(key));
    }

}
