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
 * -------------------------------------
 * StandardCategoryToolTipGenerator.java
 * -------------------------------------
 * (C) Copyright 2001-2003, by Object Refinery Limited.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited);
 * Contributor(s):   Richard Atkinson;
 *
 * $Id: StandardCategoryItemLabelGenerator.java,v 1.1 2007/10/10 19:09:17 vauchers Exp $
 *
 * Changes
 * -------
 * 13-Dec-2001 : Version 1 (DG);
 * 16-Jan-2002 : Completed Javadocs (DG);
 * 29-Aug-2002 : Changed to format numbers using default locale (RA);
 * 26-Sep-2002 : Fixed errors reported by Checkstyle (DG);
 * 10-Oct-2002 : Modified to handle dates also (DG);
 * 05-Nov-2002 : Based dataset is now TableDataset not CategoryDataset (DG);
 * 10-Apr-2003 : Replaced CategoryDataset with KeyedValues2DDataset (DG);
 *
 */

package org.jfree.chart.labels;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.NumberFormat;

import org.jfree.data.CategoryDataset;

/**
 * A standard tooltip generator for plots that use data from a {@link CategoryDataset}.
 *
 * @author David Gilbert
 */
public class StandardCategoryItemLabelGenerator implements CategoryItemLabelGenerator,
                                                           Serializable {

    /** The number formatter. */
    private NumberFormat numberFormat;

    /** The date formatter. */
    private DateFormat dateFormat;

    /**
     * Creates a new tool tip generator with a default number formatter.
     */
    public StandardCategoryItemLabelGenerator() {
        this(NumberFormat.getInstance());
    }

    /**
     * Creates a tool tip generator with the specified number formatter.
     *
     * @param formatter  the number formatter.
     */
    public StandardCategoryItemLabelGenerator(NumberFormat formatter) {
        this.numberFormat = formatter;
        this.dateFormat = null;
    }

    /**
     * Creates a tool tip generator with the specified date formatter.
     *
     * @param formatter  the date formatter.
     */
    public StandardCategoryItemLabelGenerator(DateFormat formatter) {
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
    public String generateToolTip(CategoryDataset data, int series, int category) {

        String result = null;
        Number value = data.getValue(series, category);
        if (value != null) {
            Object seriesName = data.getRowKey(series);
            if (seriesName != null) {
                result = seriesName.toString() + ", ";
            }
            Object categoryKey = data.getColumnKey(category);
            String categoryName = categoryKey.toString();
            String valueString = null;
            if (this.numberFormat != null) {
                valueString = this.numberFormat.format(value);
            }
            else if (this.dateFormat != null) {
                valueString = this.dateFormat.format(value);
            }
            if (result != null) {
                result += categoryName + " = " + valueString;
            }
            else {
                result = categoryName + " = " + valueString;
            }
        }

        return result;

    }

    /**
     * Generates a value label for an item in the dataset.
     *
     * @param data  the dataset.
     * @param series  the series index (zero-based).
     * @param category  the category index (zero-based).
     *
     * @return The value label.
     */
    public String generateItemLabel(CategoryDataset data, int series, int category) {
        String result = null;
        Number value = data.getValue(series, category);
        if (value != null) {
            if (this.numberFormat != null) {
                result = this.numberFormat.format(value);
            }
            else if (this.dateFormat != null) {
                result = this.dateFormat.format(value);
            }
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

        if (o instanceof StandardCategoryItemLabelGenerator) {
            StandardCategoryItemLabelGenerator generator = (StandardCategoryItemLabelGenerator) o;
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
