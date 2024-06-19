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
 * -----------------
 * XYItemEntity.java
 * -----------------
 * (C) Copyright 2002, by Simba Management Limited.
 *
 * Original Author:  David Gilbert (for Simba Management Limited);
 * Contributor(s):   -;
 *
 * $Id: XYItemEntity.java,v 1.1 2007/10/10 19:02:30 vauchers Exp $
 *
 * Changes:
 * --------
 * 23-May-2002 : Version 1 (DG);
 * 12-Jun-2002 : Added accessor methods and Javadoc comments (DG);
 *
 */

package com.jrefinery.chart.entity;

import java.awt.Shape;

/**
 * A chart entity that represents one item within an XY plot.
 */
public class XYItemEntity extends ChartEntity {

    /** The series. */
    protected int series;

    /** The item. */
    protected int item;

    /**
     * Creates a new entity.
     *
     * @param area The area.
     * @param toolTipText The tool tip text.
     * @param series The series (zero-based index).
     * @param item The item (zero-based index).
     */
    public XYItemEntity(Shape area, String tooltipText, int series, int item) {
        super(area, tooltipText);
        this.series = series;
        this.item = item;
    }

    /**
     * Returns the series index.
     *
     * @return The series index.
     */
    public int getSeries() {
        return this.series;
    }

    /**
     * Sets the series index.
     *
     * @param series The series index (zero-based).
     */
    public void setSeries(int series) {
        this.series = series;
    }

    /**
     * Returns the item index.
     *
     * @return The item index.
     */
    public int getItem() {
        return this.item;
    }

    /**
     * Sets the item index.
     *
     * @param item The item index (zero-based).
     */
    public void setItem(int item) {
        this.item = item;
    }

}