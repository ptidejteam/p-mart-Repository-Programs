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
 * ---------------------------------------
 * StandardCategoryItemLabelGenerator.java
 * ---------------------------------------
 * (C) Copyright 2001-2004, by Object Refinery Limited.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited);
 * Contributor(s):   Richard Atkinson;
 *
 * $Id: StandardCategoryItemLabelGenerator.java,v 1.1 2007/10/10 19:39:13 vauchers Exp $
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
 * 13-Aug-2003 : Implemented Cloneable (DG);
 * 05-Nov-2003 : Added a flag to control whether or not the series name is included in the
 *               tooltip text (DG);
 * 12-Feb-2004 : Implemented PublicCloneable (DG);
 *
 */

package org.jfree.chart.labels;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.NumberFormat;

import org.jfree.data.CategoryDataset;
import org.jfree.util.ObjectUtils;
import org.jfree.util.PublicCloneable;

/**
 * A label generator that can be assigned to a {@link org.jfree.chart.renderer.CategoryItemRenderer}
 * and used for the creation of item labels and tooltips.
 */
public class StandardCategoryItemLabelGenerator implements CategoryItemLabelGenerator,
                                                           CategoryToolTipGenerator,
                                                           PublicCloneable,
                                                           Cloneable, 
                                                           Serializable {

    /** The number formatter. */
    private NumberFormat numberFormat;

    /** The date formatter. */
    private DateFormat dateFormat;
    
    /** A flag that controls whether or not the series name is included in the tooltip text. */
    private boolean showSeriesNameInToolTips;

    /**
     * Creates a new item label generator with a default number formatter.
     */
    public StandardCategoryItemLabelGenerator() {
        this(NumberFormat.getInstance());
    }

    /**
     * Creates a new item label generator with the specified number formatter.
     *
     * @param formatter  the number formatter (<code>null</code> not permitted).
     */
    public StandardCategoryItemLabelGenerator(NumberFormat formatter) {
        this(formatter, true);
    }
    
    /**
     * Creates a label generator with the specified number formatter.
     *
     * @param formatter  the number formatter (<code>null</code> not permitted).
     * @param showSeriesNameInToolTips  a flag that controls whether or not the series name is 
     *                                  included in tooltips.
     */
    public StandardCategoryItemLabelGenerator(NumberFormat formatter, 
                                              boolean showSeriesNameInToolTips) {
        
        if (formatter == null) {
            throw new IllegalArgumentException("Null 'formatter' argument.");
        }

        this.numberFormat = formatter;
        this.dateFormat = null;
        this.showSeriesNameInToolTips = showSeriesNameInToolTips;

    }

    /**
     * Creates a label generator with the specified date formatter.
     *
     * @param formatter  the date formatter (<code>null</code> not permitted).
     */
    public StandardCategoryItemLabelGenerator(DateFormat formatter) {
        this(formatter, true);
    }
    
    /**
     * Creates a label generator with the specified date formatter.
     *
     * @param formatter  the date formatter (<code>null</code> not permitted).
     * @param showSeriesNameInToolTips  a flag that controls whether or not the series name is 
     *                                  included in tooltips.
     */
    public StandardCategoryItemLabelGenerator(DateFormat formatter, 
                                              boolean showSeriesNameInToolTips) {

        if (formatter == null) {
            throw new IllegalArgumentException("Null 'formatter' argument.");
        }

        this.numberFormat = null;
        this.dateFormat = formatter;
        this.showSeriesNameInToolTips = showSeriesNameInToolTips;
    
    }

    /**
     * Returns the number formatter.
     *
     * @return the number formatter (possibly <code>null</code>).
     */
    public NumberFormat getNumberFormat() {
        return this.numberFormat;
    }

    /**
     * Returns the date formatter.
     *
     * @return the date formatter (possibly <code>null</code>).
     */
    public DateFormat getDateFormat() {
        return this.dateFormat;
    }

    /**
     * Generates the tooltip text for the specified item.
     *
     * @param dataset  the dataset (<code>null</code> not permitted).
     * @param series  the series index (zero-based).
     * @param category  the category index (zero-based).
     *
     * @return the tooltip text (possibly <code>null</code>).
     */
    public String generateToolTip(CategoryDataset dataset, int series, int category) {

        if (dataset == null) {
            throw new IllegalArgumentException("Null 'dataset' argument.");
        }
        
        String result = null;
        Number value = dataset.getValue(series, category);
        if (value != null) {
            if (this.showSeriesNameInToolTips) {
                Object seriesName = dataset.getRowKey(series);
                if (seriesName != null) {
                    result = seriesName.toString() + ", ";
                }
            }
            Object categoryKey = dataset.getColumnKey(category);
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
     * Generates a label for the specified item.
     *
     * @param dataset  the dataset (<code>null</code> not permitted).
     * @param series  the series index (zero-based).
     * @param category  the category index (zero-based).
     *
     * @return the label (possibly <code>null</code>).
     */
    public String generateItemLabel(CategoryDataset dataset, int series, int category) {

        if (dataset == null) {
            throw new IllegalArgumentException("Null 'dataset' argument.");
        }

        String result = null;
        Number value = dataset.getValue(series, category);
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
     * Tests this object for equality with an arbitrary object.
     *
     * @param o  the other object (<code>null</code> permitted).
     *
     * @return a boolean.
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
            boolean b0 = ObjectUtils.equal(this.dateFormat, generator.dateFormat);
            boolean b1 = ObjectUtils.equal(this.numberFormat, generator.numberFormat);
            return b0 && b1;
        }

        return false;

    }
    
    /**
     * Returns an independent copy of the generator.
     * 
     * @return a clone.
     * 
     * @throws CloneNotSupportedException  should not happen.
     */
    public Object clone() throws CloneNotSupportedException {
        
        StandardCategoryItemLabelGenerator clone 
            = (StandardCategoryItemLabelGenerator) super.clone();
        
        if (this.numberFormat != null) {
            clone.numberFormat = (NumberFormat) this.numberFormat.clone();
        } 
        
        if (this.dateFormat != null) {
            clone.dateFormat = (DateFormat) this.dateFormat.clone();
        } 
        
        return clone;
        
    }

}
