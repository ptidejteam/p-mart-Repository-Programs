/* ============================================
 * JFreeChart : a free Java chart class library
 * ============================================
 *
 * Project Info:  http://www.object-refinery.com/jfreechart/index.html
 * Project Lead:  David Gilbert (david.gilbert@object-refinery.com);
 *
 * (C) Copyright 2000-2002, by Simba Management Limited and Contributors.
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
 * ChartEntity.java
 * ----------------
 * (C) Copyright 2002, by Simba Management Limited.
 *
 * Original Author:  David Gilbert (for Simba Management Limited);
 * Contributor(s):   -;
 *
 * $Id: ChartEntity.java,v 1.1 2007/10/10 19:42:01 vauchers Exp $
 *
 * Changes:
 * --------
 * 23-May-2002 : Version 1 (DG);
 * 12-Jun-2002 : Added Javadoc comments (DG);
 * 26-Jun-2002 : Added methods for image maps (DG);
 *
 */

package com.jrefinery.chart.entity;

import java.awt.Shape;
import java.awt.geom.Rectangle2D;
import java.awt.geom.PathIterator;

/**
 * A class that captures information about some component of a chart (a bar, line etc).
 */
public class ChartEntity {

    /** The area occupied by the entity (in Java 2D space). */
    protected Shape area;

    /** The tool tip text for the entity. */
    protected String toolTipText;

    /**
     * Creates a new entity.
     *
     * @param area The area.
     * @param toolTipText The tool tip text (if any).
     */
    public ChartEntity(Shape area, String toolTipText) {
        this.area = area;
        this.toolTipText = toolTipText;
    }

    /**
     * Returns the area occupied by the entity (in Java 2D space).
     *
     * @return The area.
     */
    public Shape getArea() {
        return this.area;
    }

    /**
     * Sets the area for the entity.
     * <P>
     * This class conveys information about chart entities back to a client.  Setting this
     * area doesn't change the entity (which has already been drawn).
     *
     * @param area The area.
     */
    public void setArea(Shape area) {
        this.area = area;
    }

    /**
     * Returns the tool tip text for the entity.
     *
     * @return The tool tip text (if any).
     */
    public String getToolTipText() {
        return this.toolTipText;
    }

    /**
     * Sets the tool tip text.
     *
     * @param text The text.
     */
    public void setToolTipText(String text) {
        this.toolTipText = toolTipText;
    }

    /**
     * Returns a string describing the entity area.  This string is intended for use in an AREA
     * tag when generating an image map.
     *
     * @return The shape type.
     */
    public String getShapeType() {
        if (this.area instanceof Rectangle2D) {
            return "RECT";
        }
        else {
            return "POLY";
        }
    }

    public String getShapeCoords() {
        if (this.area instanceof Rectangle2D) {
            return getRectCoords((Rectangle2D)this.area);
        }
        else {
            return getPolyCoords(this.area);
        }
    }

    /**
     * Returns a string containing the coordinates (x1, y1, x2, y2) for a given rectangle.  This
     * string is intended for use in an image map.
     *
     * @param rectangle The rectangle.
     */
    private String getRectCoords(Rectangle2D rectangle) {
        int x1 = (int)rectangle.getX();
        int y1 = (int)rectangle.getY();
        int x2 = x1+(int)rectangle.getWidth();
        int y2 = y1+(int)rectangle.getHeight();
        return x1+","+y1+","+x2+","+y2;
    }

    /**
     * Returns a string containing the coordinates for a given shape.  This
     * string is intended for use in an image map.
     *
     * @param shape The shape.
     */
    private String getPolyCoords(Shape shape) {
        String result = "";
        boolean first = true;
        float[] coords = new float[6];
        PathIterator pi = shape.getPathIterator(null, 1.0);
        while (pi.isDone()==false) {
            pi.currentSegment(coords);
            if (first) {
                first = false;
                result = result+(int)coords[0]+","+(int)coords[1];
            }
            else {
                result = result+","+(int)coords[0]+","+(int)coords[1];
            }
            pi.next();
        }
        return result;
    }

}