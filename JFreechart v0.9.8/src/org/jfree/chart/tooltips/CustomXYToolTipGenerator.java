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
 * -----------------------------
 * CustomXYToolTipGenerator.java
 * -----------------------------
 * (C) Copyright 2002, 2003, by Richard Atkinson and Contributors.
 *
 * Original Author:  Richard Atkinson (richard_c_atkinson@ntlworld.com);
 * Contributor(s):   David Gilbert (for Simba Management Limited);
 *
 * $Id: CustomXYToolTipGenerator.java,v 1.1 2007/10/10 20:03:26 vauchers Exp $
 *
 * Changes:
 * --------
 * 05-Aug-2002 : Version 1, contributed by Richard Atkinson (RA);
 * 26-Sep-2002 : Fixed errors reported by Checkstyle (DG);
 * 21-Mar-2003 : Implemented Serializable (DG);
 *
 */

package org.jfree.chart.tooltips;

import java.io.Serializable;
import java.util.List;

import org.jfree.data.XYDataset;

/**
 * A tool tip generator that stores custom tooltips. The dataset passed into the generateToolTip 
 * method is ignored.
 *
 * @author Richard Atkinson
 */
public class CustomXYToolTipGenerator implements XYToolTipGenerator, Serializable {

    /** Storage for the tooltip lists. */
    private List toolTipSeries = new java.util.ArrayList();

    /**
     * Default constructor.
     */
    public CustomXYToolTipGenerator() {
        super();
    }

    /**
     * Returns the number of tool tip lists stored by the renderer.
     * 
     * @return The list count.
     */
    public int getListCount() {
        return this.toolTipSeries.size();
    }
    
    /**
     * Returns the number of tool tips in a given list.
     * 
     * @param list  the list index (zero based).
     * 
     * @return The tooltip count.
     */
    public int getToolTipCount(int list) {
        
        int result = 0;
        List tooltips = (List) this.toolTipSeries.get(list);
        if (tooltips != null) {
            result = tooltips.size();
        }
        return result;
    }

    /**
     * Returns the tool tip text for an item.
     * 
     * @param series  the series index.
     * @param item  the item index.
     * 
     * @return The tool tip text.
     */    
    public String getToolTipText(int series, int item) {

        String result = null;
        
        if (series < getListCount()) {
            List tooltips = (List) this.toolTipSeries.get(series);
            if (tooltips != null) {
                if (item < tooltips.size()) {
                    result = (String) tooltips.get(item);
                }
            }
        }
        
        return result;
    }
    
    /**
     * Adds a list of tooltips for a series.
     *
     * @param toolTips  the list of tool tips.
     */
    public void addToolTipSeries(List toolTips) {
        this.toolTipSeries.add(toolTips);
    }

    /**
     * Generates a tool tip text item for a particular item within a series.
     *
     * @param data  the dataset (ignored in this implementation).
     * @param series  the series (zero-based index).
     * @param item  the item (zero-based index).
     *
     * @return the tooltip text.
     */
    public String generateToolTip(XYDataset data, int series, int item) {

        return getToolTipText(series, item);

    }
    
    /**
     * Tests if this object is equal to another.
     * 
     * @param o  the other object.
     * 
     * @return A boolean.
     */
    public boolean equals(Object o) {
    
        if (o == null) {
            return false;
        }
        if (o == this) {
            return true;
        }
        
        if (o instanceof CustomXYToolTipGenerator) {
            CustomXYToolTipGenerator generator = (CustomXYToolTipGenerator) o;
            boolean result = true;
            for (int series = 0; series < getListCount(); series++) {
                for (int item = 0; item < getToolTipCount(series); item++) {
                    String t1 = getToolTipText(series, item);
                    String t2 = generator.getToolTipText(series, item);
                    if (t1 != null) {
                        result = result && t1.equals(t2);
                    }
                    else {
                        result = result && (t2 == null);
                    }
                }
            }
            return result;
        }
        
        return false;
        
    }

}
