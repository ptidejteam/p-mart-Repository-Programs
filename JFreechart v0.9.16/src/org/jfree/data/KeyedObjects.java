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
 * -----------------
 * KeyedObjects.java
 * -----------------
 * (C) Copyright 2003, 2004, by Object Refinery Limited.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited);
 * Contributor(s):   -;
 *
 * $Id: KeyedObjects.java,v 1.1 2007/10/10 19:25:30 vauchers Exp $
 *
 * Changes:
 * --------
 * 31-Oct-2002 : Version 1 (DG);
 *
 */

package org.jfree.data;

import java.util.Iterator;
import java.util.List;

/**
 * A collection of (key, object) pairs.
 *
 * @author David Gilbert
 */
public class KeyedObjects {

    /** Storage for the data. */
    private List data;

    /**
     * Creates a new collection (initially empty).
     */
    public KeyedObjects() {
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
     * Returns an object.
     *
     * @param item  the item index (zero-based).
     *
     * @return The object.
     */
    public Object getObject(int item) {

        Object result = null;
        KeyedObject kobj = (KeyedObject) this.data.get(item);
        if (kobj != null) {
            result = kobj.getObject();
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
        KeyedObject item = (KeyedObject) this.data.get(index);
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
            KeyedObject ko = (KeyedObject) iterator.next();
            if (ko.getKey().equals(key)) {
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
            KeyedObject ko = (KeyedObject) iterator.next();
            result.add(ko.getKey());
        }
        return result;

    }

    /**
     * Returns the object (possibly null) for a given key.
     * <P>
     * If the key is not recognised, the method should return null.
     *
     * @param key  the key.
     *
     * @return the object.
     */
    public Object getObject(Comparable key) {
        return getObject(getIndex(key));
    }

    /**
     * Adds a new object to the collection, or overwrites an existing object.
     * <P>
     * This is the same as the setObject(...) method.
     *
     * @param key  the key.
     * @param object  the object.
     */
    public void addObject(Comparable key, Object object) {
        setObject(key, object);
    }

    /**
     * Replaces an existing object, or adds a new object to the collection.
     * <P>
     * This is the same as the addObject(...) method.
     *
     * @param key  the key.
     * @param object  the object.
     */
    public void setObject(Comparable key, Object object) {
        int keyIndex = getIndex(key);
        if (keyIndex >= 0) {
            KeyedObject ko = (KeyedObject) this.data.get(keyIndex);
            ko.setObject(object);
        }
        else {
            KeyedObject ko = new KeyedObject(key, object);
            this.data.add(ko);
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
