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
 * -----------------
 * AxisLocation.java
 * -----------------
 * (C) Copyright 2003 by Object Refinery Limited.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited);
 * Contributor(s):   -;
 *
 * $Id: AxisLocation.java,v 1.1 2007/10/10 20:07:40 vauchers Exp $
 *
 * Changes:
 * --------
 * 02-May-2003 : Version 1 (DG);
 * 03-Jul-2003 : Added isTopOrBottom(...) and isLeftOrRight(...) methods (DG);
 *
 */

package org.jfree.chart.axis;

import java.io.ObjectStreamException;
import java.io.Serializable;

/**
 * Used to indicate the location of an axis on a 2D plot.
 *
 * @author David Gilbert
 */
public final class AxisLocation implements Serializable {

    /** Axis at the top. */
    public static final AxisLocation TOP = new AxisLocation("AxisLocation.TOP");

    /** Axis at the bottom. */
    public static final AxisLocation BOTTOM = new AxisLocation("AxisLocation.BOTTOM");

    /** Axis at the left. */
    public static final AxisLocation LEFT = new AxisLocation("AxisLocation.LEFT");

    /** Axis at the right. */
    public static final AxisLocation RIGHT = new AxisLocation("AxisLocation.RIGHT");


    /** The name. */
    private String name;

    /**
     * Private constructor.
     *
     * @param name  the name.
     */
    private AxisLocation(String name) {
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
        if (!(o instanceof AxisLocation)) {
            return false;
        }

        final AxisLocation orientation = (AxisLocation) o;
        if (!this.name.equals(orientation.toString())) {
            return false;
        }

        return true;

    }

    /**
     * Returns <code>true</code> if the location is <code>TOP</code> or <code>BOTTOM</code>, and 
     * <code>false</code> otherwise.
     * 
     * @param location  the location.
     * 
     * @return A boolean.
     */
    public static boolean isTopOrBottom(AxisLocation location) {
        return (location == AxisLocation.TOP || location == AxisLocation.BOTTOM);    
    }
    
    /**
     * Returns <code>true</code> if the location is <code>LEFT</code> or <code>RIGHT</code>, and 
     * <code>false</code> otherwise.
     * 
     * @param location  the location.
     * 
     * @return A boolean.
     */
    public static boolean isLeftOrRight(AxisLocation location) {
        return (location == AxisLocation.LEFT || location == AxisLocation.RIGHT);    
    }
    
    /**
     * Ensures that serialization returns the unique instances.
     * 
     * @return The object.
     * 
     * @throws ObjectStreamException if there is a problem.
     */
    private Object readResolve() throws ObjectStreamException {
        if (this.equals(AxisLocation.TOP)) {
            return AxisLocation.TOP;
        }
        else if (this.equals(AxisLocation.BOTTOM)) {
            return AxisLocation.BOTTOM;
        }    
        else if (this.equals(AxisLocation.LEFT)) {
            return AxisLocation.LEFT;
        }    
        else if (this.equals(AxisLocation.RIGHT)) {
            return AxisLocation.RIGHT;
        }
        return null;
    }
    
}
