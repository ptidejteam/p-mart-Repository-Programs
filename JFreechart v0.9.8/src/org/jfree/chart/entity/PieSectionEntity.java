/* ======================================
 * JFreeChart : a free Java chart library
 * ======================================
 *
 * Project Info:  http://www.jfree.org/jfreechart/index.html
 * Project Lead:  David Gilbert (david.gilbert@object-refinery.com);
 *
 * (C) Copyright 2000-2003, by Simba Management Limited and Contributors.
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
 * ---------------------
 * PieSectionEntity.java
 * ---------------------
 * (C) Copyright 2002, 2003, by Simba Management Limited.
 *
 * Original Author:  David Gilbert (for Simba Management Limited);
 * Contributor(s):   Richard Atkinson;
 *
 * $Id: PieSectionEntity.java,v 1.1 2007/10/10 20:03:26 vauchers Exp $
 *
 * Changes:
 * --------
 * 23-May-2002 : Version 1 (DG);
 * 12-Jun-2002 : Added Javadoc comments (DG);
 * 26-Jun-2002 : Added method to generate AREA tag for image map generation (DG);
 * 05-Aug-2002 : Added new constructor to populate URLText
 *               Moved getImageMapAreaTag() to ChartEntity (superclass) (RA);
 * 03-Oct-2002 : Fixed errors reported by Checkstyle (DG);
 * 07-Mar-2003 : Added pie index attribute, since the PiePlot class can create multiple
 *               pie plots within one chart.  Also renamed 'category' --> 'sectionKey' and changed 
 *               the class from Object --> Comparable (DG);
 *
 */

package org.jfree.chart.entity;

import java.awt.Shape;

/**
 * A chart entity that represents one section within a pie plot.
 *
 * @author David Gilbert
 */
public class PieSectionEntity extends ChartEntity {

    /** The pie index. */
    private int pieIndex;
    
    /** The section index. */
    private int sectionIndex;

    /** The section key. */
    private Comparable sectionKey;

    /**
     * Creates a new pie section entity.
     *
     * @param pieIndex  the pie index (zero-based).
     * @param sectionIndex  the section index (zero-based).
     * @param sectionKey  the section key.
     * @param area  the area.
     * @param toolTipText  the tool tip text.
     */
    public PieSectionEntity(int pieIndex, int sectionIndex, Comparable sectionKey, 
                            Shape area, String toolTipText) {
                                
        super(area, toolTipText);
        this.pieIndex = pieIndex;
        this.sectionIndex = sectionIndex;
        this.sectionKey = sectionKey;

    }

    /**
     * Creates a new pie section entity.
     *
     * @param pieIndex  the pie index (zero-based).
     * @param sectionIndex  the section index (zero-based).
     * @param sectionKey  the section key.
     * @param area  the area.
     * @param toolTipText  the tool tip text.
     * @param urlText  the URL text for HTML image maps.
     */
    public PieSectionEntity(int pieIndex, int sectionIndex, Comparable sectionKey, 
                            Shape area, String toolTipText, String urlText) {
                                
        super(area, toolTipText, urlText);
        this.pieIndex = pieIndex;
        this.sectionIndex = sectionIndex;
        this.sectionKey = sectionKey;

    }

    /**
     * Returns the pie index.
     * <p>
     * For a regular pie chart, the section index is 0.  For a pie chart containing
     * multiple pie plots, the pie index is the row or column index from which the pie data is 
     * extracted.
     * 
     * @return The pie index.
     */
    public int getPieIndex() {
        return this.pieIndex;
    }
    
    /**
     * Sets the pie index.
     * 
     * @param index  the new index value.
     */
    public void setPieIndex(int index) {
        this.pieIndex = index;
    }
    
    /**
     * Returns the section index.
     *
     * @return The section index.
     */
    public int getSectionIndex() {
        return this.sectionIndex;
    }

    /**
     * Sets the section index.
     *
     * @param index  the section index.
     */
    public void setSectionIndex(int index) {
        this.sectionIndex = index;
    }

    /**
     * Returns the section key.
     *
     * @return The section key.
     */
    public Comparable getSectionKey() {
        return this.sectionKey;
    }

    /**
     * Sets the section key.
     *
     * @param key  the section key.
     */
    public void setSectionKey(Comparable key) {
        this.sectionKey = key;
    }

    /**
     * Returns a string representing the entity.
     *
     * @return a string representing the entity.
     */
    public String toString() {
        return "PieSection: " + this.pieIndex + ", " + this.sectionIndex + "("
                              + this.sectionKey.toString() + ")";
    }

}
