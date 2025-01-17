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
 * --------------------------
 * DatasetRenderingOrder.java
 * --------------------------
 * (C) Copyright 2003, 2004, by Object Refinery Limited.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited);
 * Contributor(s):   -;
 *
 * $Id: DatasetRenderingOrder.java,v 1.1 2007/10/10 19:50:23 vauchers Exp $
 *
 * Changes:
 * --------
 * 02-May-2003 : Version 1 (DG);
 * 02-Jun-2004 : Changed 'STANDARD' --> 'FORWARD' (DG);
 *
 */

package org.jfree.chart.plot;

import java.io.ObjectStreamException;
import java.io.Serializable;

/**
 * Defines the tokens that indicate the rendering order for datasets in a 
 * {@link org.jfree.chart.plot.CategoryPlot} or an {@link org.jfree.chart.plot.XYPlot}.
 */
public final class DatasetRenderingOrder implements Serializable {

    /** Render datasets in the order 0, 1, 2, ..., N-1, where N is the number of datasets. */
    public static final DatasetRenderingOrder FORWARD
        = new DatasetRenderingOrder("DatasetRenderingOrder.FORWARD");

    /** Render datasets in the order N-1, N-2, ..., 2, 1, 0, where N is the number of datasets. */
    public static final DatasetRenderingOrder REVERSE
        = new DatasetRenderingOrder("DatasetRenderingOrder.REVERSE");

    /** The name. */
    private String name;

    /**
     * Private constructor.
     *
     * @param name  the name.
     */
    private DatasetRenderingOrder(String name) {
        this.name = name;
    }

    /**
     * Returns a string representing the object.
     *
     * @return The string (never <code>null</code>).
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
        if (!(o instanceof DatasetRenderingOrder)) {
            return false;
        }

        final DatasetRenderingOrder order = (DatasetRenderingOrder) o;
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
        if (this.equals(DatasetRenderingOrder.FORWARD)) {
            return DatasetRenderingOrder.FORWARD;
        }
        else if (this.equals(DatasetRenderingOrder.REVERSE)) {
            return DatasetRenderingOrder.REVERSE;
        }      
        return null;
    }

}
