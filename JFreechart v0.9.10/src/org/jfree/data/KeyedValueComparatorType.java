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
 * -----------------------------
 * KeyedValueComparatorType.java
 * -----------------------------
 * (C) Copyright 2003 by Object Refinery Limited.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited);
 * Contributor(s):   -;
 *
 * $Id: KeyedValueComparatorType.java,v 1.1 2007/10/10 19:05:01 vauchers Exp $
 *
 * Changes:
 * --------
 * 05-Mar-2003 : Version 1 (DG);
 *
 */

package org.jfree.data;

/**
 * Uses to indicate the type of a {@link KeyedValueComparator} : 'by key' or 'by value'.
 *
 * @author David Gilbert
 */
public class KeyedValueComparatorType {

    /** An object representing 'by key' sorting. */
    public static final KeyedValueComparatorType BY_KEY
        = new KeyedValueComparatorType("KeyedValueComparatorType.BY_KEY");

    /** An object representing 'by value' sorting. */
    public static final KeyedValueComparatorType BY_VALUE
        = new KeyedValueComparatorType("KeyedValueComparatorType.BY_VALUE");

    /** The name. */
    private String name;

    /**
     * Private constructor.
     *
     * @param name  the name.
     */
    private KeyedValueComparatorType(String name) {
        this.name = name;
    }

    /**
     * Returns a string representing the object.
     *
     * @return The string.
     */
    public String toString() {
        return this.name;
    }

    /**
     * Returns <code>true</code> if this object is equal to the specified object, and
     * <code>false</code> otherwise.
     *
     * @param o  the other object.
     *
     * @return A boolean.
     */
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof KeyedValueComparatorType)) {
            return false;
        }

        final KeyedValueComparatorType type = (KeyedValueComparatorType) o;
        if (!this.name.equals(type.toString())) {
            return false;
        }

        return true;
    }

}

