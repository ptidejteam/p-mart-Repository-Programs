/* ===========================================================
 * JFreeChart : a free chart library for the Java(tm) platform
 * ===========================================================
 *
 * (C) Copyright 2000-2007, by Object Refinery Limited and Contributors.
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
 * -------------------
 * VectorDataItem.java
 * -------------------
 * (C) Copyright 2007, by Object Refinery Limited.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited);
 * Contributor(s):   -;
 *
 * $Id: VectorDataItem.java,v 1.1 2007/10/10 20:46:59 vauchers Exp $
 *
 * Changes
 * -------
 * 30-Jan-2007 : Version 1 (DG);
 *
 */

package org.jfree.experimental.data.xy;

import org.jfree.data.ComparableObjectItem;

/**
 * An item representing data in the form (x, y, deltaX, deltaY).
 */
public class VectorDataItem extends ComparableObjectItem {

    /** 
     * Creates a new instance of <code>YIntervalItem</code>.
     *
     * @param x  the x-value.
     * @param y  the y-value.
     * @param deltaX  the vector x.
     * @param deltaY  the vector y.
     */
    public VectorDataItem(double x, double y, double deltaX, double deltaY) {
        super(new XYCoordinate(x, y), new Vector(deltaX, deltaY));
    }
    
    /**
     * Returns the x-value.
     *
     * @return The x-value (never <code>null</code>).
     */
    public double getXValue() {
        XYCoordinate xy = (XYCoordinate) getComparable();
        return xy.getX();
    }
    
    /**
     * Returns the y-value.
     *
     * @return The y-value.
     */
    public double getYValue() {
        XYCoordinate xy = (XYCoordinate) getComparable();
        return xy.getY();
    }
    
    /**
     * Returns the x-component for the vector.
     * 
     * @return The x-component.
     */
    public double getDeltaX() {
        Vector vi = (Vector) getObject();
        if (vi != null) {
            return vi.getX();
        }
        else {
            return Double.NaN;
        }
    }
    
    /**
     * Returns the y-component for the vector.
     * 
     * @return The y-component.
     */
    public double getDeltaY() {
        Vector vi = (Vector) getObject();
        if (vi != null) {
            return vi.getY();
        }
        else {
            return Double.NaN;
        }
    }
    
}
