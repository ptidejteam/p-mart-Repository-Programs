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
 * -----------------
 * XYItemEntity.java
 * -----------------
 * (C) Copyright 2002-2004, by Object Refinery Limited.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited);
 * Contributor(s):   Richard Atkinson;
 *                   Christian W. Zuckschwerdt;
 *
 * $Id: XYItemEntity.java,v 1.1 2007/10/10 19:25:39 vauchers Exp $
 *
 * Changes:
 * --------
 * 23-May-2002 : Version 1 (DG);
 * 12-Jun-2002 : Added accessor methods and Javadoc comments (DG);
 * 26-Jun-2002 : Added getImageMapAreaTag() method (DG);
 * 05-Aug-2002 : Added new constructor to populate URLText
 *               Moved getImageMapAreaTag() to ChartEntity (superclass) (RA);
 * 03-Oct-2002 : Fixed errors reported by Checkstyle (DG);
 * 30-Jun-2003 : Added XYDataset reference (CZ);
 *
 */

package org.jfree.chart.entity;

import java.awt.Shape;

import org.jfree.data.XYDataset;

/**
 * A chart entity that represents one item within an {@link org.jfree.chart.plot.XYPlot}.
 *
 * @author David Gilbert
 */
public class XYItemEntity extends ChartEntity {

    /** The dataset. */
    private XYDataset dataset;
    
    /** The series. */
    private int series;

    /** The item. */
    private int item;

    /**
     * Creates a new entity.
     *
     * @param area  the area.
     * @param toolTipText  the tool tip text.
     * @param series  the series (zero-based index).
     * @param item  the item (zero-based index).
     * 
     * @deprecated Use alternative constructor.
     */
    public XYItemEntity(Shape area, String toolTipText, int series, int item) {
        super(area, toolTipText);
        this.series = series;
        this.item = item;
    }

    /**
     * Creates a new entity.
     *
     * @param area  the area.
     * @param toolTipText  the tool tip text.
     * @param urlText  the URL text for HTML image maps.
     * @param series  the series (zero-based index).
     * @param item  the item (zero-based index).
     * 
     * @deprecated Use alternative constructor.
     */
    public XYItemEntity(Shape area, String toolTipText, String urlText, int series, int item) {
        super(area, toolTipText, urlText);
        this.series = series;
        this.item = item;
    }

    /**
     * Creates a new entity.
     *
     * @param area  the area.
     * @param dataset  the dataset.
     * @param series  the series (zero-based index).
     * @param item  the item (zero-based index).
     * @param toolTipText  the tool tip text.
     * @param urlText  the URL text for HTML image maps.
     */
    public XYItemEntity(Shape area, 
                        XYDataset dataset, int series, int item,
                        String toolTipText, String urlText) {
        super(area, toolTipText, urlText);
        this.dataset = dataset;
        this.series = series;
        this.item = item;
    }

    /**
     * Returns the dataset this entity refers to.
     *
     * @return the dataset.
     */
    public XYDataset getDataset() {
        return this.dataset;
    }

    /**
     * Sets the dataset this entity refers to.
     *
     * @param dataset  the dataset.
     */
    public void setDataset(XYDataset dataset) {
        this.dataset = dataset;
    }

    /**
     * Returns the series index.
     *
     * @return the series index.
     */
    public int getSeries() {
        return this.series;
    }

    /**
     * Sets the series index.
     *
     * @param series the series index (zero-based).
     */
    public void setSeries(int series) {
        this.series = series;
    }

    /**
     * Returns the item index.
     *
     * @return the item index.
     */
    public int getItem() {
        return this.item;
    }

    /**
     * Sets the item index.
     *
     * @param item the item index (zero-based).
     */
    public void setItem(int item) {
        this.item = item;
    }

}
