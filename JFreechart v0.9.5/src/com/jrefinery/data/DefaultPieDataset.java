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
 * ----------------------
 * DefaultPieDataset.java
 * ----------------------
 * (C) Copyright 2001-2003, by Simba Management Limited.
 *
 * Original Author:  David Gilbert (for Simba Management Limited);
 * Contributor(s):   Sam (oldman);
 *
 * $Id: DefaultPieDataset.java,v 1.1 2007/10/10 19:54:14 vauchers Exp $
 *
 * Changes
 * -------
 * 17-Nov-2001 : Version 1 (DG);
 * 22-Jan-2002 : Removed legend methods from dataset implementations (DG);
 * 07-Apr-2002 : Modified implementation to guarantee data sequence to remain in the order
 *               categories are added (oldman);
 * 23-Oct-2002 : Added getCategory(int) method and getItemCount() method, in line with changes
 *               to the PieDataset interface (DG);
 * 04-Feb-2003 : Changed underlying data storage to DefaultKeyedValues2D (DG);
 *
 */

package com.jrefinery.data;

import java.util.Collections;
import java.util.List;

/**
 * A default implementation of the {@link PieDataset} interface.
 *
 * @author David Gilbert
 */
public class DefaultPieDataset extends AbstractDataset implements PieDataset {

    /** Storage for the data. */
    private DefaultKeyedValues data;
    
    /**
     * Constructs a pie dataset, initially empty.
     */
    public DefaultPieDataset() {

        this.data = new DefaultKeyedValues();

    }

    /**
     * Returns the number of items in the dataset.
     *
     * @return the item count.
     */
    public int getItemCount() {
        return this.data.getItemCount();
    }

    /**
     * Returns the categories in the dataset.
     *
     * @return the categories in the dataset.
     */
    public List getKeys() {
        return Collections.unmodifiableList(this.data.getKeys());
    }

    /**
     * Returns the key for an item.
     *
     * @param item  the item index (zero-based).
     *
     * @return the category.
     */
    public Comparable getKey(int item) {

        Comparable result = null;
        if (getItemCount() > item) {
            result = this.data.getKey(item);
        }
        return result;

    }

    /**
     * Returns the index for a key.
     *
     * @param key  the key.
     *
     * @return the key index.
     */
    public int getIndex(Comparable key) {

        return this.data.getIndex(key);

    }

    /**
     * Returns a value.
     *
     * @param item  the value index.
     *
     * @return the value (possibly <code>null</code>).
     */
    public Number getValue(int item) {

        Number result = null;
        if (getItemCount() > item) {
            result = (Number) this.data.getValue(item);
        }
        return result;

    }

    /**
     * Returns the data value associated with a key.
     *
     * @param key  the key.
     *
     * @return The value (possibly <code>null</code>).
     */
    public Number getValue(Comparable key) {

        // check arguments...
        if (key == null) {
            throw new IllegalArgumentException("DefaultPieDataset: null key not allowed.");
        }

        // fetch the value...
        return this.data.getValue(key);

    }

    /**
     * Sets the data value for a key.
     *
     * @param key  the key.
     * @param value  the value.
     */
    public void setValue(Comparable key, Number value) {

        this.data.setValue(key, value);
        fireDatasetChanged();

    }
    
    /**
     * Sets the data value for a key.
     *
     * @param key  the key.
     * @param value  the value.
     */
    public void setValue(Comparable key, double value) {
    
        setValue(key, new Double(value));
    
    }

}
