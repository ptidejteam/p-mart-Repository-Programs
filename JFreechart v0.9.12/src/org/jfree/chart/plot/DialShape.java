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
 * --------------
 * DialShape.java
 * --------------
 * (C) Copyright 2003 by Object Refinery Limited.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited);
 * Contributor(s):   -;
 *
 * $Id: DialShape.java,v 1.1 2007/10/10 19:12:29 vauchers Exp $
 *
 * Changes:
 * --------
 * 20-Aug-2003 : Version 1 (DG);
 *
 */

package org.jfree.chart.plot;

import java.io.ObjectStreamException;
import java.io.Serializable;

/**
 * Used to indicate the background shape for a {@link org.jfree.chart.plot.MeterPlot}.
 *
 * @author David Gilbert
 */
public final class DialShape implements Serializable {

    /** Circle. */
    public static final DialShape CIRCLE = new DialShape("DialShape.CIRCLE");

    /** Chord. */
    public static final DialShape CHORD = new DialShape("DialShape.CHORD");

    /** Pie. */
    public static final DialShape PIE = new DialShape("DialShape.PIE");

    /** The name. */
    private String name;

    /**
     * Private constructor.
     *
     * @param name  the name.
     */
    private DialShape(String name) {
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
        if (!(o instanceof DialShape)) {
            return false;
        }

        final DialShape shape = (DialShape) o;
        if (!this.name.equals(shape.toString())) {
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
        if (this.equals(DialShape.CIRCLE)) {
            return DialShape.CIRCLE;
        }
        else if (this.equals(DialShape.CHORD)) {
            return DialShape.CHORD;
        }      
        else if (this.equals(DialShape.PIE)) {
            return DialShape.PIE;
        }      
        return null;
    }

}
