/* ===========================================================
 * JFreeChart : a free chart library for the Java(tm) platform
 * ===========================================================
 *
 * (C) Copyright 2000-2006, by Object Refinery Limited and Contributors.
 *
 * Project Info:  http://www.jfree.org/jfreechart/index.html
 *
 * This library is free software; you can redistribute it and/or modify it 
 * under the terms of the GNU Lesser General Public License as published by 
 * the Free Software Foundation; either version 2.1 of the License, or 
 * (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but 
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY 
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public 
 * License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, 
 * USA.  
 *
 * [Java is a trademark or registered trademark of Sun Microsystems, Inc. 
 * in the United States and other countries.]
 *
 * --------------
 * StrokeMap.java
 * --------------
 * (C) Copyright 2006, by Object Refinery Limited.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited);
 * Contributor(s):   -;
 *
 * $Id: StrokeMap.java,v 1.1 2007/10/10 20:38:45 vauchers Exp $
 *
 * Changes:
 * --------
 * 27-Sep-2006 : Version 1 (DG);
 *
 */

package org.jfree.chart;

import java.awt.Stroke;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.jfree.io.SerialUtilities;
import org.jfree.util.ObjectUtilities;

/**
 * A storage structure that maps <code>Comparable</code> instances with
 * <code>Stroke</code> instances.  
 * <br><br>
 * To support cloning and serialization, you should only use keys that are 
 * cloneable and serializable.  Special handling for the <code>Stroke</code>
 * instances is included in this class.
 * 
 * @since 1.0.3
 */
public class StrokeMap implements Cloneable, Serializable {

    /** Storage for the keys and values. */
    private transient Map store;
    
    /**
     * Creates a new (empty) map.
     */
    public StrokeMap() {
        this.store = new TreeMap();    
    }
    
    /**
     * Returns the stroke associated with the specified key, or 
     * <code>null</code>.
     * 
     * @param key  the key (<code>null</code> not permitted).
     * 
     * @return The stroke, or <code>null</code>.
     * 
     * @throws IllegalArgumentException if <code>key</code> is 
     *     <code>null</code>.
     */
    public Stroke getStroke(Comparable key) {
        if (key == null) {
            throw new IllegalArgumentException("Null 'key' argument.");
        }
        return (Stroke) this.store.get(key);
    }
    
    /**
     * Returns <code>true</code> if the map contains the specified key, and
     * <code>false</code> otherwise.
     * 
     * @param key  the key.
     * 
     * @return <code>true</code> if the map contains the specified key, and
     * <code>false</code> otherwise.
     */
    public boolean containsKey(Comparable key) {
        return this.store.containsKey(key);
    }
    
    /**
     * Adds a mapping between the specified <code>key</code> and 
     * <code>stroke</code> values.
     * 
     * @param key  the key (<code>null</code> not permitted).
     * @param stroke  the stroke.
     */
    public void put(Comparable key, Stroke stroke) {
        if (key == null) { 
            throw new IllegalArgumentException("Null 'key' argument.");
        }
        this.store.put(key, stroke);
    }
    
    /**
     * Resets the map to empty.
     */
    public void clear() {
        this.store.clear();
    }
    
    /**
     * Tests this map for equality with an arbitrary object.
     * 
     * @param obj  the object (<code>null</code> permitted).
     * 
     * @return A boolean.
     */
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof StrokeMap)) {
            return false;
        }
        StrokeMap that = (StrokeMap) obj;
        if (this.store.size() != that.store.size()) {
            return false;
        }
        Set keys = this.store.keySet();
        Iterator iterator = keys.iterator();
        while (iterator.hasNext()) {
            Comparable key = (Comparable) iterator.next();
            Stroke s1 = getStroke(key);
            Stroke s2 = that.getStroke(key);
            if (!ObjectUtilities.equal(s1, s2)) {
                return false;
            }
        }
        return true;
    }
    
    /**
     * Returns a clone of this <code>StrokeMap</code>.
     * 
     * @return A clone of this instance.
     * 
     * @throws CloneNotSupportedException if any key is not cloneable.
     */
    public Object clone() throws CloneNotSupportedException {
        // TODO: I think we need to make sure the keys are actually cloned,
        // whereas the stroke instances are always immutable so they're OK
        return super.clone();
    }
    
    /**
     * Provides serialization support.
     *
     * @param stream  the output stream.
     *
     * @throws IOException  if there is an I/O error.
     */
    private void writeObject(ObjectOutputStream stream) throws IOException {
        stream.defaultWriteObject();
        stream.writeInt(this.store.size());
        Set keys = this.store.keySet();
        Iterator iterator = keys.iterator();
        while (iterator.hasNext()) {
            Comparable key = (Comparable) iterator.next();
            stream.writeObject(key);
            Stroke stroke = getStroke(key);
            SerialUtilities.writeStroke(stroke, stream);
        }
    }

    /**
     * Provides serialization support.
     *
     * @param stream  the input stream.
     *
     * @throws IOException  if there is an I/O error.
     * @throws ClassNotFoundException  if there is a classpath problem.
     */
    private void readObject(ObjectInputStream stream) 
            throws IOException, ClassNotFoundException {
        stream.defaultReadObject();
        this.store = new TreeMap();
        int keyCount = stream.readInt();
        for (int i = 0; i < keyCount; i++) {
            Comparable key = (Comparable) stream.readObject();
            Stroke stroke = SerialUtilities.readStroke(stream);
            this.store.put(key, stroke);
        }
    }
    
}
