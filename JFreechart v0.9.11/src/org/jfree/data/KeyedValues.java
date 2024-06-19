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
 * ----------------
 * KeyedValues.java
 * ----------------
 * (C) Copyright 2002, 2003, by Object Refinery Limited.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited);
 * Contributor(s):   -;
 *
 * $Id: KeyedValues.java,v 1.1 2007/10/10 19:09:12 vauchers Exp $
 *
 * Changes:
 * --------
 * 23-Oct-2002 : Version 1 (DG);
 *
 */

package org.jfree.data;

import java.util.List;

/**
 * A collection of values where each value is associated with a key.
 *
 * @see Values
 * @see DefaultKeyedValues
 *
 * @author David Gilbert
 */
public interface KeyedValues extends Values {

    /**
     * Returns the key associated with an item (value).
     *
     * @param index  the item index (zero-based).
     *
     * @return the key.
     */
    public Comparable getKey(int index);

    /**
     * Returns the index for a given key.
     *
     * @param key  the key.
     *
     * @return the index.
     */
    public int getIndex(Comparable key);

    /**
     * Returns the keys.
     *
     * @return the keys.
     */
    public List getKeys();

    /**
     * Returns the value (possibly <code>null</code>) for a given key.
     * <P>
     * If the key is not recognised, the method should return <code>null</code>.
     *
     * @param key  the key.
     *
     * @return the value.
     */
    public Number getValue(Comparable key);

}
