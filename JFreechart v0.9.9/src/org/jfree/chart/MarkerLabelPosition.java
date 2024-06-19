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
 * MarkerLabelPosition.java
 * ------------------------
 * (C) Copyright 2003 by Object Refinery Limited.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited);
 * Contributor(s):   -;
 *
 * $Id: MarkerLabelPosition.java,v 1.1 2007/10/10 20:07:37 vauchers Exp $
 *
 * Changes:
 * --------
 * 21-May-2003 (DG);
 */

package org.jfree.chart;

import java.io.Serializable;

/**
 * Used to indicate the position of a label relative to a marker).
 *
 * @author David Gilbert
 */
public class MarkerLabelPosition implements Serializable {

    /** Top/left. */
    public static final MarkerLabelPosition TOP_LEFT
        = new MarkerLabelPosition("MarkerLabelPosition.TOP_LEFT");

    /** Top/right. */
    public static final MarkerLabelPosition TOP_RIGHT
        = new MarkerLabelPosition("MarkerLabelPosition.TOP_RIGHT");

    /** Bottom/left. */
    public static final MarkerLabelPosition BOTTOM_LEFT
        = new MarkerLabelPosition("MarkerLabelPosition.BOTTOM_LEFT");

    /** Bottom/right. */
    public static final MarkerLabelPosition BOTTOM_RIGHT
        = new MarkerLabelPosition("MarkerLabelPosition.BOTTOM_RIGHT");

    /** The name. */
    private String name;

    /**
     * Private constructor.
     *
     * @param name  the name.
     */
    private MarkerLabelPosition(String name) {
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
        if (!(o instanceof MarkerLabelPosition)) {
            return false;
        }

        final MarkerLabelPosition order = (MarkerLabelPosition) o;
        if (!this.name.equals(order.toString())) {
            return false;
        }

        return true;

    }

}
