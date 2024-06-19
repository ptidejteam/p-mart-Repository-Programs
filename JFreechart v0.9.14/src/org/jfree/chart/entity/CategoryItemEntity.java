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
 * -----------------------
 * CategoryItemEntity.java
 * -----------------------
 * (C) Copyright 2002, 2003, by Object Refinery Limited and Contributors.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited);
 * Contributor(s):   Richard Atkinson;
 *                   Christian W. Zuckschwerdt;
 *
 * $Id: CategoryItemEntity.java,v 1.1 2007/10/10 19:19:01 vauchers Exp $
 *
 * Changes:
 * --------
 * 23-May-2002 : Version 1 (DG);
 * 12-Jun-2002 : Added Javadoc comments (DG);
 * 26-Jun-2002 : Added getImageMapAreaTag() method (DG);
 * 05-Aug-2002 : Added new constructor to populate URLText
 *               Moved getImageMapAreaTag() to ChartEntity (superclass) (RA);
 * 03-Oct-2002 : Fixed errors reported by Checkstyle (DG);
 * 30-Jul-2003 : Added CategoryDataset reference (CZ);
 *
 */

package org.jfree.chart.entity;

import java.awt.Shape;

import org.jfree.data.CategoryDataset;

/**
 * A chart entity that represents one item within a category plot.
 *
 * @author David Gilbert
 */
public class CategoryItemEntity extends ChartEntity {

    /** The dataset. */
    private CategoryDataset dataset;
    
    /** The series (zero-based index). */
    private int series;

    /** The category. */
    private Object category;

    /** The category index. */
    private int categoryIndex;

    /**
     * Creates a new category item entity.
     *
     * @param area  the area.
     * @param toolTipText  the tool tip text.
     * @param series  the series (zero-based index).
     * @param category  the category.
     * @param categoryIndex  the index of the category.
     * 
     * @deprecated Use alternative constructor.
     * 
     */
    public CategoryItemEntity(Shape area, String toolTipText,
                              int series, Object category, int categoryIndex) {

        super(area, toolTipText);
        this.series = series;
        this.category = category;
        this.categoryIndex = categoryIndex;

    }

    /**
     * Creates a new category item entity.
     *
     * @param area  the area.
     * @param toolTipText  the tool tip text.
     * @param urlText  the URL text for HTML image maps.
     * @param series  the series (zero-based index).
     * @param category  the category.
     * @param categoryIndex  the category index.
     * 
     * @deprecated Use alternative constructor.
     */
    public CategoryItemEntity(Shape area, String toolTipText, String urlText,
                              int series, Object category, int categoryIndex) {

        super(area, toolTipText, urlText);
        this.series = series;
        this.category = category;
        this.categoryIndex = categoryIndex;

    }

    /**
     * Creates a new category item entity.
     *
     * @param area  the area.
     * @param toolTipText  the tool tip text.
     * @param urlText  the URL text for HTML image maps.
     * @param dataset  the dataset.
     * @param series  the series (zero-based index).
     * @param category  the category.
     * @param categoryIndex  the category index.
     */
    public CategoryItemEntity(Shape area, String toolTipText, String urlText,
                              CategoryDataset dataset,
                              int series, Object category, int categoryIndex) {

        super(area, toolTipText, urlText);
        this.dataset = dataset;
        this.series = series;
        this.category = category;
        this.categoryIndex = categoryIndex;

    }

    /**
     * Returns the datset this entity refers to.
     *
     * @return the dataset.
     */
    public CategoryDataset getDataset() {
        return this.dataset; 
    }

    /**
     * Sets the datset this entity refers to.
     *
     * @param dataset  the dataset.
     */
    public void setDataset(CategoryDataset dataset) {
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
     * @param series  the series index (zero-based).
     */
    public void setSeries(int series) {
        this.series = series;
    }

    /**
     * Returns the category.
     *
     * @return the category.
     */
    public Object getCategory() {
        return this.category;
    }

    /**
     * Sets the category.
     *
     * @param category  the category.
     */
    public void setCategory(Object category) {
        this.category = category;
    }

    /**
     * Returns the category index.
     *
     * @return the category index.
     */
    public int getCategoryIndex() {
        return this.categoryIndex;
    }

    /**
     * Sets the category index.
     *
     * @param index  the category index.
     */
    public void setCategoryIndex(int index) {
        this.categoryIndex = index;
    }

    /**
     * Returns a string representing this object.
     *
     * @return a string representing this object.
     */
    public String toString() {
        return "Category Item: series=" + series + ", category=" + category.toString();
    }

}
