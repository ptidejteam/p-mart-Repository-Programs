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
 * ------------------
 * ContourEntity.java
 * ------------------
 * (C) Copyright 2002, 2003, by David M. O'Donnell and Contributors.
 *
 * Original Author:  David M. O'Donnell;
 * Contributor(s):   David Gilbert (for Object Refinery Limited);
 *
 * $Id: ContourEntity.java,v 1.1 2007/10/10 19:19:01 vauchers Exp $
 *
 * Changes
 * -------
 * 26-Nov-2002 : Version 1 contributed by David M. O'Donnell (DG);
 *
 */

package org.jfree.chart.entity;

import java.awt.Shape;

/**
 * Represents an item on a contour chart.
 *
 * @author David M. O'Donnell
 */
public class ContourEntity extends ChartEntity {

    /** Holds the index into the dataset for this entity. */
    private int index = -1;

    /**
     * Constructor for ContourEntity.
     *
     * @param area  the area.
     * @param toolTipText  the tooltip text.
     */
    public ContourEntity(Shape area, String toolTipText) {
        super(area, toolTipText);
    }

    /**
     * Constructor for ContourEntity.
     *
     * @param area  the area.
     * @param toolTipText  the tooltip text.
     * @param urlText  the URL text.
     */
    public ContourEntity(Shape area, String toolTipText, String urlText) {
        super(area, toolTipText, urlText);
    }

    /**
     * Returns the index.
     *
     * @return The index.
     */
    public int getIndex() {
        return index;
    }

    /**
     * Sets the index.
     *
     * @param index  the index.
     */
    public void setIndex(int index) {
        this.index = index;
    }

}
