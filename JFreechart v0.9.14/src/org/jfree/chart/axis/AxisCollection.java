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
 * -------------------
 * AxisCollection.java
 * -------------------
 * (C) Copyright 2003 by Object Refinery Limited.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited);
 * Contributor(s):   -;
 *
 * $Id: AxisCollection.java,v 1.1 2007/10/10 19:19:06 vauchers Exp $
 *
 * Changes
 * -------
 * 03-Nov-2003 : Added standard header (DG);
 *
 */

package org.jfree.chart.axis;

import java.util.List;

import org.jfree.ui.RectangleEdge;

/**
 * A collection of axes that have been assigned to the TOP, BOTTOM, LEFT or RIGHT of a chart.
 */
public class AxisCollection {
    
    /** The axes that need to be drawn at the top of the plot area. */
    private List axesAtTop;
    
    /** The axes that need to be drawn at the bottom of the plot area. */
    private List axesAtBottom;
    
    /** The axes that need to be drawn at the left of the plot area. */
    private List axesAtLeft;
    
    /** The axes that need to be drawn at the right of the plot area. */
    private List axesAtRight;
   
    /**
     * Creates a new empty collection.
     */ 
    public AxisCollection() {
        this.axesAtTop = new java.util.ArrayList();
        this.axesAtBottom = new java.util.ArrayList();
        this.axesAtLeft = new java.util.ArrayList();
        this.axesAtRight = new java.util.ArrayList();
    }
    
    /**
     * Returns a list of the axes (if any) that need to be drawn at the top of the plot area.
     * 
     * @return A list of axes.
     */
    public List getAxesAtTop() {
        return this.axesAtTop;
    }
    
   /**
    * Returns a list of the axes (if any) that need to be drawn at the bottom of the plot area.
    * 
    * @return A list of axes.
    */
   public List getAxesAtBottom() {
        return this.axesAtBottom;
    }
    
    /**
     * Returns a list of the axes (if any) that need to be drawn at the left of the plot area.
     * 
     * @return A list of axes.
     */
    public List getAxesAtLeft() {
        return this.axesAtLeft;
    }
    
    /**
    * Returns a list of the axes (if any) that need to be drawn at the right of the plot area.
    * 
    * @return A list of axes.
    */
    public List getAxesAtRight() {
        return this.axesAtRight;    
    }
    
    /**
     * Adds an axis to the collection.
     * 
     * @param axis  the axis.
     * @param edge  the edge of the plot that the axis should be drawn on.
     */
    public void add(Axis axis, RectangleEdge edge) {
        if (edge == RectangleEdge.TOP) {
            this.axesAtTop.add(axis);
        }
        else if (edge == RectangleEdge.BOTTOM) {
            this.axesAtBottom.add(axis);
        }
        else if (edge == RectangleEdge.LEFT) {
            this.axesAtLeft.add(axis);
        }
        else if (edge == RectangleEdge.RIGHT) {
            this.axesAtRight.add(axis);
        }
    }

}
