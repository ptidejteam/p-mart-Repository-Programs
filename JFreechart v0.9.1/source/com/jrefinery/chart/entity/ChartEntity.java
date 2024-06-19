/* =======================================
 * JFreeChart : a Java Chart Class Library
 * =======================================
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
 * $Id: ChartEntity.java,v 1.1 2007/10/10 19:02:30 vauchers Exp $
 *
 * Changes:
 * --------
 * 23-May-2002 : Version 1 (DG);
 * 12-Jun-2002 : Added Javadoc comments (DG);
 *
 */

package com.jrefinery.chart.entity;

import java.awt.Shape;

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

}