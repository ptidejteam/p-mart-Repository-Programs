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
 * -----------------------------
 * DefaultKeyedValueDataset.java
 * -----------------------------
 * (C) Copyright 2003, by Simba Management Limited.
 *
 * Original Author:  David Gilbert (for Simba Management Limited);
 * Contributor(s):   -;
 *
 * $Id $
 *
 * Changes
 * -------
 * 27-Mar-2003 : Version 1 (DG);
 *
 */

package com.jrefinery.data;

import java.io.Serializable;

import com.jrefinery.util.ObjectUtils;

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
     * @param value  the value.
     */
    public DefaultKeyedValueDataset(Comparable key, Number value) {
        this(new DefaultKeyedValue(key, value));
    }
    
    /**
     * Creates a new dataset that uses the data from a {@link KeyedValue} instance.
     * 
     * @param data  the data.
     */
    public DefaultKeyedValueDataset(KeyedValue data) {
        
        this.data = data;

    }

    /**
     * Returns the key associated with the value.
     *
     * @return the key.
     */
    public Comparable getKey() {
        return this.data.getKey();
    }
    
    /**
     * Returns the value.
     *
     * @return the value.
     */
    public Number getValue() {
        return this.data.getValue();
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
     * Sets the value for the dataset.  After the change is made, a {@link DatasetChangeEvent} is 
     * sent to all registered listeners.
     * 
     * @param key  the key.
     * @param value  the value.
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
        
        if (obj instanceof KeyedValueDataset) {
            KeyedValueDataset kvd = (KeyedValueDataset) obj;
            boolean b0 = ObjectUtils.equalOrBothNull(this.data.getKey(), kvd.getKey());   
            boolean b1 = ObjectUtils.equalOrBothNull(this.data.getValue(), kvd.getValue());
            return b0 && b1;   
        }
        
        return false;
    }
     
}
