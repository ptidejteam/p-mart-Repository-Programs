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
 * ----------------
 * KeyedObject.java
 * ----------------
 * (C) Copyright 2003, by Simba Management Limited.
 *
 * Original Author:  David Gilbert (for Simba Management Limited);
 * Contributor(s):   -;
 *
 * $Id: KeyedObject.java,v 1.1 2007/10/10 19:54:14 vauchers Exp $
 *
 * Changes:
 * --------
 * 05-Feb-2003 : Version 1 (DG);
 *
 */

package com.jrefinery.data;

/**
 * A (key, object) pair.
 * 
 * @author David Gilbert
 */
public class KeyedObject {

    /** The key. */
    private Comparable key;

    /** The object. */
    private Object object;

    /**
     * Creates a new (key, object) pair.
     *
     * @param key  the key.
     * @param object  the object.
     */
    public KeyedObject(Comparable key, Object object) {
        this.key = key;
        this.object = object;
    }

    /**
     * Returns the key.
     *
     * @return the key.
     */
    public Comparable getKey() {
        return this.key;
    }

    /**
     * Returns the object.
     *
     * @return the object.
     */
    public Object getObject() {
        return this.object;
    }

    /**
     * Sets the object.
     *
     * @param object  the object.
     */
    public void setObject(Object object) {
        this.object = object;
    }

}
