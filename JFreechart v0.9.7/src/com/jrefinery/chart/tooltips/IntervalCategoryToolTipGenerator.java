/* ======================================
 * JFreeChart : a free Java chart library
 * ======================================
 *
 * Project Info:  http://www.object-refinery.com/jfreechart/index.html
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
 * -------------------------------------
 * IntervalCategoryToolTipGenerator.java
 * -------------------------------------
 * (C) Copyright 2002, 2003, by Simba Management Limited.
 *
 * Original Author:  David Gilbert (for Simba Management Limited);
 * Contributor(s):   -;
 *
 * $Id: IntervalCategoryToolTipGenerator.java,v 1.1 2007/10/10 20:00:13 vauchers Exp $
 *
 * Changes
 * -------
 * 10-Oct-2002 : Version 1 (DG);
 * 24-Oct-2002 : Amendments for changes in CategoryDataset interface and CategoryToolTipGenerator
 *               interface (DG);
 * 05-Nov-2002 : The base dataset is now TableDataset not CategoryDataset (DG);
 * 21-Mar-2003 : Implemented Serializable (DG);
 * 10-Apr-2003 : Changed CategoryDataset to KeyedValues2DDataset (DG);
 *
 */

package com.jrefinery.chart.tooltips;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.NumberFormat;

import com.jrefinery.data.IntervalCategoryDataset;
import com.jrefinery.data.KeyedValues2DDataset;

/**
 * A tooltip generator for plots that use data from an IntervalCategoryDataset.
 *
 * @author David Gilbert
 */
public class IntervalCategoryToolTipGenerator implements CategoryToolTipGenerator, Serializable {

    /** The number formatter. */
    private NumberFormat numberFormat;

    /** The date formatter. */
    private DateFormat dateFormat;

    /**
     * Creates a new tool tip generator with a default number formatter.
     */
    public IntervalCategoryToolTipGenerator() {
        this(NumberFormat.getInstance());
    }

    /**
     * Creates a tool tip generator with the specified number formatter.
     *
     * @param formatter  the number formatter.
     */
    public IntervalCategoryToolTipGenerator(NumberFormat formatter) {
        this.numberFormat = formatter;
        this.dateFormat = null;
    }

    /**
     * Creates a tool tip generator with the specified date formatter.
     *
     * @param formatter  the date formatter.
     */
    public IntervalCategoryToolTipGenerator(DateFormat formatter) {
        this.numberFormat = null;
        this.dateFormat = formatter;
    }

    /**
     * Returns the number formatter.
     *
     * @return the number formatter.
     */
    public NumberFormat getNumberFormat() {
        return this.numberFormat;
    }

    /**
     * Returns the date formatter.
     *
     * @return the date formatter.
     */
    public DateFormat getDateFormat() {
        return this.dateFormat;
    }

    /**
     * Generates a tooltip text item for a particular category within a series.
     *
     * @param data  the dataset.
     * @param series  the series index (zero-based).
     * @param category  the category index (zero-based).
     *
     * @return the tooltip text or <code>null</code> if value is <code>null</code>.
     */
    public String generateToolTip(KeyedValues2DDataset data, int series, int category) {

        Number value = data.getValue(series, category);
        Number start = value;
        Number end = value;
        if (data instanceof IntervalCategoryDataset) {
            IntervalCategoryDataset icd = (IntervalCategoryDataset) data;
            start = icd.getStartValue(series, category);
            end = icd.getEndValue(series, category);
        }

        String result = "";
        if ((start != null) && (end != null)) {
            Object seriesName = data.getRowKey(series);
            if (seriesName != null) {
                result += seriesName.toString() + ", ";
            }
            String categoryName = data.getColumnKey(category).toString();
            String startString = null;
            String endString = null;
            if (this.numberFormat != null) {
                startString = this.numberFormat.format(start);
                endString = this.numberFormat.format(end);
            }
            else if (this.dateFormat != null) {
                startString = this.dateFormat.format(start);
                endString = this.dateFormat.format(end);
            }
            result += categoryName + " : " + startString + " to " + endString;
        }

        return result;

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
        
        if (o instanceof IntervalCategoryToolTipGenerator) {
            IntervalCategoryToolTipGenerator generator = (IntervalCategoryToolTipGenerator) o;
            if (this.dateFormat != null) {
                return this.dateFormat.equals(generator.dateFormat);
            }
            else {
                return this.numberFormat.equals(generator.numberFormat);
            }
        }
        
        return false;
        
    }

}
