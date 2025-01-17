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
 * ------------------------
 * DefaultValueDataset.java
 * ------------------------
 * (C) Copyright 2003, by Object Refinery Limited.
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
 * A default implementation of the {@link ValueDataset} interface.
 *
 * @author David Gilbert
 */
public class DefaultValueDataset extends AbstractDataset
                                 implements ValueDataset, Cloneable, Serializable {

    /** The value. */
    private Number value;

    /**
     * Constructs a new dataset, initially empty.
     */
    public DefaultValueDataset() {

        this(null);

    }
    
    /**
     * Creates a new dataset.
     * 
     * @param value  the value.
     */
    public DefaultValueDataset(double value) {
        this(new Double(value));
    }

    /**
     * Creates a new dataset.
     *
     * @param value  the initial value.
     */
    public DefaultValueDataset(Number value) {
        this.value = value;
    }

    /**
     * Returns the value.
     *
     * @return the value.
     */
    public Number getValue() {
        return this.value;
    }

    /**
     * Sets the value.  A {@link DatasetChangeEvent} is sent to all registered listeners.
     *
     * @param value  the new value.
     */
    public void setValue(Number value) {
        this.value = value;
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

        if (obj instanceof ValueDataset) {
            ValueDataset vd = (ValueDataset) obj;
            return ObjectUtils.equal(this.value, vd.getValue());
        }

        return false;
    }

}
