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
 * -------------------------
 * LegendRenderingOrder.java
 * -------------------------
 * (C) Copyright 2000-2004, by Object Refinery Limited and Contributors.
 *
 * Original Author:  Angel;
 * Contributor(s):   -;
 *
 * $Id: LegendRenderingOrder.java,v 1.1 2007/10/10 19:29:12 vauchers Exp $
 *
 * Changes
 * -------
 * 26-Mar-2004 : Version 1 (DG);
 * 
 */

package org.jfree.chart;

import java.io.ObjectStreamException;
import java.io.Serializable;

/**
 * Represents the order for rendering legend items.
 */
public final class LegendRenderingOrder implements Serializable {

    /** In order. */
    public static final LegendRenderingOrder STANDARD = new LegendRenderingOrder(
        "LegendRenderingOrder.STANDARD"
    );

    /** In reverse order. */
    public static final LegendRenderingOrder REVERSE = new LegendRenderingOrder(
        "LegendRenderingOrder.REVERSE"
    );

    /** The name. */
    private String name;

    /**
     * Private constructor.
     *
     * @param name  the name.
     */
    private LegendRenderingOrder(String name) {
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
        if (!(o instanceof LegendRenderingOrder)) {
            return false;
        }

        final LegendRenderingOrder order = (LegendRenderingOrder) o;
        if (!this.name.equals(order.toString())) {
            return false;
        }

        return true;

    }
   
    /**
     * Ensures that serialization returns the unique instances.
     *
     * @return The object.
     *
     * @throws ObjectStreamException if there is a problem.
     */
    private Object readResolve() throws ObjectStreamException {
        if (this.equals(LegendRenderingOrder.STANDARD)) {
            return LegendRenderingOrder.STANDARD;
        }
        else if (this.equals(LegendRenderingOrder.REVERSE)) {
            return LegendRenderingOrder.REVERSE;
        }     
        return null;
    }

}
