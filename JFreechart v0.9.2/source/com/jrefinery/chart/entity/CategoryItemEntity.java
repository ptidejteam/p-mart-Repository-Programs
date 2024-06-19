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
 * -----------------------
 * CategoryItemEntity.java
 * -----------------------
 * (C) Copyright 2002, by Simba Management Limited.
 *
 * Original Author:  David Gilbert (for Simba Management Limited);
 * Contributor(s):   -;
 *
 * $Id: CategoryItemEntity.java,v 1.1 2007/10/10 19:42:01 vauchers Exp $
 *
 * Changes:
 * --------
 * 23-May-2002 : Version 1 (DG);
 * 12-Jun-2002 : Added Javadoc comments (DG);
 * 26-Jun-2002 : Added getImageMapAreaTag() method (DG);
 *
 */

package com.jrefinery.chart.entity;

import java.awt.Shape;

/**
 * A chart entity that represents one item within a category plot.
 */
public class CategoryItemEntity extends ChartEntity {

    /** The series (zero-based index). */
    protected int series;

    /** The category. */
    protected Object category;

    /** The category index. */
    protected int categoryIndex;

    /**
     * Creates a new category item entity.
     *
     * @param area The area.
     * @param toolTipText The tool tip text.
     * @param series The series (zero-based index).
     * @param category The category.
     */
    public CategoryItemEntity(Shape area, String toolTipText,
                              int series, Object category, int categoryIndex) {
        super(area, toolTipText);
        this.series = series;
        this.category = category;
        this.categoryIndex = categoryIndex;
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
     * Returns the category.
     *
     * @return The category.
     */
    public Object getCategory() {
        return this.category;
    }

    /**
     * Sets the category.
     *
     * @param category The category.
     */
    public void setCategory(Object category) {
        this.category = category;
    }

    /**
     * Returns the category index.
     *
     * @return The category index.
     */
    public int getCategoryIndex() {
        return this.categoryIndex;
    }

    /**
     * Sets the category index.
     *
     * @param index The category index.
     */
    public void setCategoryIndex(int index) {
        this.categoryIndex = index;
    }

    /**
     * Returns a string representing this object.
     *
     * @return A string representing this object.
     */
    public String toString() {
        return "Category Item: series="+series+", category="+category.toString();
    }

    /**
     * Returns an AREA tag for this entity.  The tag is intended for use within an image map.
     *
     * @param hrefPrefix A prefix for the HREF link.
     */
    public String getImageMapAreaTag(String hrefPrefix) {
        String tag = "<AREA SHAPE=\""+getShapeType()+
                     "\" COORDS=\""+getShapeCoords()+
                     "\" HREF=\""+hrefPrefix+"categoryitem-series-"+this.series+"-category-"+this.categoryIndex+".html\">";
        return tag;
    }

}