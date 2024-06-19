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
 * StandardEntityCollection.java
 * -----------------------------
 * (C) Copyright 2001-2004, by Object Refinery Limited.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited);
 * Contributor(s):   -;
 *
 * $Id: StandardEntityCollection.java,v 1.1 2007/10/10 19:29:20 vauchers Exp $
 *
 * Changes
 * -------
 * 23-May-2002 : Version 1 (DG);
 * 26-Jun-2002 : Added iterator() method (DG);
 * 03-Oct-2002 : Fixed errors reported by Checkstyle (DG);
 *
 */

package org.jfree.chart.entity;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;

/**
 * A standard implementation of the {@link EntityCollection} interface.
 *
 * @author David Gilbert
 */
public class StandardEntityCollection implements EntityCollection {

    /** Storage for the entities. */
    private Collection entities;

    /**
     * Constructs a new entity collection (initially empty).
     */
    public StandardEntityCollection() {
        this.entities = new java.util.ArrayList();
    }

    /**
     * Clears the entities.
     */
    public void clear() {
        this.entities.clear();
    }

    /**
     * Adds an entity.
     *
     * @param entity  the entity.
     */
    public void addEntity(ChartEntity entity) {
        this.entities.add(entity);
    }
    
    /**
     * Adds all the entities from the specified collection.
     * 
     * @param collection  the collection of entities.
     */
    public void addEntities(EntityCollection collection) {
        this.entities.addAll(collection.getEntities());
    }

    /**
     * Returns an entity for the specified coordinates.
     *
     * @param x  the x coordinate.
     * @param y  the y coordinate.
     *
     * @return the entity.
     */
    public ChartEntity getEntity(double x, double y) {

        ChartEntity result = null;

        Iterator iterator = this.entities.iterator();
        while (iterator.hasNext()) {
            ChartEntity entity = (ChartEntity) iterator.next();
            if (entity.getArea().contains(x, y)) {
                result = entity;
            }
        }

        return result;
    }

    /**
     * Returns the entities in an unmodifiable collection.
     * 
     * @return The entities.
     */
    public Collection getEntities() {
        return Collections.unmodifiableCollection(this.entities);
    }
    
    /**
     * Returns an iterator for the entities in the collection.
     *
     * @return an iterator.
     */
    public Iterator iterator() {
        return this.entities.iterator();
    }

}
