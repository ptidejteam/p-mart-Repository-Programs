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
 * ---------------------------------
 * AbstractXYItemLabelGenerator.java
 * ---------------------------------
 * (C) Copyright 2004, by Object Refinery Limited.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited);
 * Contributor(s):   -;
 *
 * $Id: AbstractXYItemLabelGenerator.java,v 1.1 2007/10/10 19:50:22 vauchers Exp $
 *
 * Changes
 * -------
 * 27-Feb-2004 : Version 1 (DG);
 * 12-May-2004 : Moved default tool tip format to StandardXYToolTipGenerator (DG);
 * 15-Jul-2004 : Switched getX() with getXValue() and getY() with getYValue() (DG);
 *
 */

package org.jfree.chart.labels;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.MessageFormat;
import java.text.NumberFormat;

import org.jfree.data.xy.XYDataset;
import org.jfree.util.ObjectUtils;

/**
 * A base class for creating item label generators.
 */
public class AbstractXYItemLabelGenerator implements Cloneable, Serializable {
    
    /** The item label format string. */
    private String formatString;
    
    /** A number formatter for the x value. */
    private NumberFormat xFormat;
    
    /** A date formatter for the x value. */
    private DateFormat xDateFormat;

    /** A formatter for the y value. */
    private NumberFormat yFormat;

    /** A date formatter for the y value. */
    private DateFormat yDateFormat;

    /**
     * Creates an item label generator using default number formatters.
     */
    protected AbstractXYItemLabelGenerator() {
        this("{2}", NumberFormat.getNumberInstance(), NumberFormat.getNumberInstance());
    }

    /**
     * Creates an item label generator using the specified number formatters.
     *
     * @param formatString  the item label format string (<code>null</code> not permitted).
     * @param xFormat  the format object for the x values (<code>null</code> not permitted).
     * @param yFormat  the format object for the y values (<code>null</code> not permitted).
     */
    protected AbstractXYItemLabelGenerator(String formatString,
                                           NumberFormat xFormat, 
                                           NumberFormat yFormat) {

        if (formatString == null) {
            throw new IllegalArgumentException("Null 'formatString' argument.");   
        }
        if (xFormat == null) {
            throw new IllegalArgumentException("Null 'xFormat' argument.");   
        }
        if (yFormat == null) {
            throw new IllegalArgumentException("Null 'yFormat' argument.");   
        }
        this.formatString = formatString;
        this.xFormat = xFormat;
        this.yFormat = yFormat;

    }

    /**
     * Creates an item label generator using the specified number formatters.
     *
     * @param formatString  the item label format string (<code>null</code> not permitted).
     * @param xFormat  the format object for the x values (<code>null</code> permitted).
     * @param yFormat  the format object for the y values (<code>null</code> not permitted).
     */
    protected AbstractXYItemLabelGenerator(String formatString,
                                           DateFormat xFormat, 
                                           NumberFormat yFormat) {

        this(formatString, NumberFormat.getInstance(), yFormat);
        this.xDateFormat = xFormat;
    
    }
    
    /**
     * Creates an item label generator using the specified number formatters.
     *
     * @param formatString  the item label format string (<code>null</code> not permitted).
     * @param xFormat  the format object for the x values (<code>null</code> permitted).
     * @param yFormat  the format object for the y values (<code>null</code> not permitted).
     */
    protected AbstractXYItemLabelGenerator(String formatString,
                                           DateFormat xFormat, 
                                           DateFormat yFormat) {

        this(formatString, NumberFormat.getInstance(), NumberFormat.getInstance());
        this.xDateFormat = xFormat;
        this.yDateFormat = yFormat;
    
    }
    
    /**
     * Returns the format string (this controls the overall structure of the label).
     * 
     * @return The format string (never <code>null</code>).
     */
    public String getFormatString() {
        return this.formatString;
    }
    
    /**
     * Returns the number formatter for the x-values.
     *
     * @return The number formatter (possibly <code>null</code>).
     */
    public NumberFormat getXFormat() {
        return this.xFormat;
    }

    /**
     * Returns the date formatter for the x-values.
     *
     * @return The date formatter (possibly <code>null</code>).
     */
    public DateFormat getXDateFormat() {
        return this.xDateFormat;
    }

    /**
     * Returns the number formatter for the y-values.
     *
     * @return the number formatter (possibly <code>null</code>).
     */
    public NumberFormat getYFormat() {
        return this.yFormat;
    }

    /**
     * Returns the date formatter for the y-values.
     *
     * @return The date formatter (possibly <code>null</code>).
     */
    public DateFormat getYDateFormat() {
        return this.yDateFormat;
    }

    /**
     * Generates a label string for an item in the dataset.
     *
     * @param dataset  the dataset (<code>null</code> not permitted).
     * @param series  the series (zero-based index).
     * @param item  the item (zero-based index).
     *
     * @return The label (possibly <code>null</code>).
     */
    public String generateLabelString(XYDataset dataset, int series, int item) {
        String result = null;    
        Object[] items = createItemArray(dataset, series, item);
        result = MessageFormat.format(this.formatString, items);
        return result;
    }

    /**
     * Creates the array of items that can be passed to the {@link MessageFormat} class
     * for creating labels.
     *
     * @param dataset  the dataset (<code>null</code> not permitted).
     * @param series  the series (zero-based index).
     * @param item  the item (zero-based index).
     *
     * @return The items (never <code>null</code>).
     */
    protected Object[] createItemArray(XYDataset dataset, int series, int item) {
        Object[] result = new Object[3];
        result[0] = dataset.getSeriesName(series);
        Number x = dataset.getX(series, item);
        if (this.xDateFormat != null) {
            result[1] = this.xDateFormat.format(x);   
        }
        else {
            result[1] = this.xFormat.format(x);
        }
        Number y = dataset.getY(series, item);
        if (this.yDateFormat != null) {
            result[2] = this.yDateFormat.format(y);   
        }
        else {
            result[2] = this.yFormat.format(y);
        }
        return result;
    }

    /**
     * Tests this object for equality with an arbitrary object.
     *
     * @param obj  the other object (<code>null</code> permitted).
     *
     * @return A boolean.
     */
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof AbstractXYItemLabelGenerator) {
            AbstractXYItemLabelGenerator generator = (AbstractXYItemLabelGenerator) obj;
            if (!this.formatString.equals(generator.formatString)) {
                return false;   
            }
            if (!ObjectUtils.equal(this.xFormat, generator.xFormat)) {
                return false;   
            }
            if (!ObjectUtils.equal(this.xDateFormat, generator.xDateFormat)) {
                return false;   
            }
            if (!ObjectUtils.equal(this.yFormat, generator.yFormat)) {
                return false;   
            }
            if (!ObjectUtils.equal(this.yDateFormat, generator.yDateFormat)) {
                return false;   
            }
            return true;
        }
        return false;
    }

    /**
     * Returns an independent copy of the generator.
     * 
     * @return A clone.
     * 
     * @throws CloneNotSupportedException if cloning is not supported.
     */
    public Object clone() throws CloneNotSupportedException {
        
        AbstractXYItemLabelGenerator clone = (AbstractXYItemLabelGenerator) super.clone();

        if (this.xFormat != null) {
            clone.xFormat = (NumberFormat) this.xFormat.clone();
        }
        
        if (this.yFormat != null) {
            clone.yFormat = (NumberFormat) this.yFormat.clone();
        }
        
        return clone;
        
    }
    
}
