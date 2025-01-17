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
 * -----------------------------
 * DefaultKeyedValueDataset.java
 * -----------------------------
 * (C) Copyright 2003, 2004, by Object Refinery Limited.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited);
 * Contributor(s):   -;
 *
 * $Id $
 *
 * Changes
 * -------
 * 27-Mar-2003 : Version 1 (DG);
 * 18-Aug-2003 : Implemented Cloneable (DG);
 *
 */

package org.jfree.data;

import java.io.Serializable;

import org.jfree.util.ObjectUtils;

/**
 * A default implementation of the {@link KeyedValueDataset} interface.
 *
 * @author David Gilbert
 */
public class DefaultKeyedValueDataset extends AbstractDataset
                                      implements KeyedValueDataset, Serializable {

    /** Storage for the data. */
    private KeyedValue data;

    /**
     * Constructs a new dataset, initially empty.
     */
    public DefaultKeyedValueDataset() {
        this(null);
    }

    /**
     * Creates a new dataset with the specified initial value.
     *
     * @param key  the key.
     * @param value  the value (<code>null</code> permitted).
     */
    public DefaultKeyedValueDataset(Comparable key, Number value) {
        this(new DefaultKeyedValue(key, value));
    }

    /**
     * Creates a new dataset that uses the data from a {@link KeyedValue} instance.
     *
     * @param data  the data (<code>null</code> permitted).
     */
    public DefaultKeyedValueDataset(KeyedValue data) {
        this.data = data;
    }

    /**
     * Returns the key associated with the value, or <code>null</code> if the dataset has no 
     * data item.
     *
     * @return the key.
     */
    public Comparable getKey() {
        Comparable result = null;
        if (this.data != null) {
            result = this.data.getKey();
        }
        return result;
    }

    /**
     * Returns the value.
     *
     * @return the value (possibly <code>null</code>).
     */
    public Number getValue() {
        Number result = null;
        if (this.data != null) {
            result = this.data.getValue();
        }
        return result;
    }

    /**
     * Updates the value.
     *
     * @param value  the new value (<code>null</code> permitted).
     */
    public void updateValue(Number value) {
        if (this.data == null) {
            throw new RuntimeException("updateValue: can't update null.");
        }
        setValue(this.data.getKey(), value);
    }

    /**
     * Sets the value for the dataset and sends a {@link DatasetChangeEvent} to all registered 
     * listeners.
     *
     * @param key  the key.
     * @param value  the value (<code>null</code> permitted).
     */
    public void setValue(Comparable key, Number value) {
        this.data = new DefaultKeyedValue(key, value);
        notifyListeners(new DatasetChangeEvent(this, this));
    }

    /**
     * Tests this dataset for equality with an arbitrary object.
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

        if ((obj instanceof KeyedValueDataset) == false) {
            return false;
        }

        KeyedValueDataset kvd = (KeyedValueDataset) obj;
        if (this.data == null) {
            if (kvd.getKey() != null || kvd.getValue() != null) {
                return false;
            }
            return true;
        }
        
        if (ObjectUtils.equal(this.data.getKey(), kvd.getKey()) == false) {
            return false;
        }
        if (ObjectUtils.equal(this.data.getValue(), kvd.getValue()) == false) {
            return false;
        }
        return true;
    }

    /**
     * Returns a hash code.
     * 
     * @return a hash code.
     */
    public int hashCode() {
        return (this.data != null ? this.data.hashCode() : 0);
    }

    /**
     * Creates a clone of the dataset.
     * 
     * @return A clone.
     * 
     * @throws CloneNotSupportedException This class will not throw this exception, but subclasses 
     *         (if any) might.
     */
    public Object clone() throws CloneNotSupportedException {
        DefaultKeyedValueDataset clone = (DefaultKeyedValueDataset) super.clone();
        return clone;    
    }
    
}
