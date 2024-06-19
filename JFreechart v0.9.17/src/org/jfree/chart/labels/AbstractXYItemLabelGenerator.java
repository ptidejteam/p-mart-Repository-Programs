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
 * $Id: AbstractXYItemLabelGenerator.java,v 1.1 2007/10/10 19:29:21 vauchers Exp $
 *
 * Changes
 * -------
 * 27-Feb-2004 : Version 1 (DG);
 *
 */

package org.jfree.chart.labels;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.MessageFormat;
import java.text.NumberFormat;
import java.util.Date;

import org.jfree.data.XYDataset;
import org.jfree.util.ObjectUtils;

/**
 * A base class for creating item label generators.
 */
public class AbstractXYItemLabelGenerator implements Cloneable, Serializable {
    
    /** The default tooltip format. */
    public static final String DEFAULT_TOOLTIP_FORMAT = "{0}: ({1}, {2})";

    /** The default item label format. */
    public static final String DEFAULT_ITEM_LABEL_FORMAT = "{2}";

    /** The tooltip format string. */
    private String toolTipFormat;
    
    /** The item label format string. */
    private String itemLabelFormat;
    
    /** A number formatter for the x value. */
    private NumberFormat xFormat;
    
    /** A date formatter for the x value. */
    private DateFormat xDateFormat;

    /** A formatter for the y value. */
    private NumberFormat yFormat;

    /**
     * Creates an item label generator using default number formatters.
     */
    public AbstractXYItemLabelGenerator() {
        this(
            DEFAULT_TOOLTIP_FORMAT, DEFAULT_ITEM_LABEL_FORMAT, 
            NumberFormat.getNumberInstance(), NumberFormat.getNumberInstance()
        );
    }

    /**
     * Creates an item label generator using the specified number formatters.
     *
     * @param toolTipFormat  the tooltip format string (<code>null</code> not permitted).
     * @param itemLabelFormat  the item label format string (<code>null</code> not permitted).
     * @param xFormat  the format object for the x values (<code>null</code> not permitted).
     * @param yFormat  the format object for the y values (<code>null</code> not permitted).
     */
    public AbstractXYItemLabelGenerator(String toolTipFormat,
                                        String itemLabelFormat,
                                        NumberFormat xFormat, 
                                        NumberFormat yFormat) {

        if (toolTipFormat == null) {
            throw new IllegalArgumentException("Null 'toolTipFormat' argument.");   
        }
        if (itemLabelFormat == null) {
            throw new IllegalArgumentException("Null 'itemLabelFormat' argument.");   
        }
        if (xFormat == null) {
            throw new IllegalArgumentException("Null 'xFormat' argument.");   
        }
        if (yFormat == null) {
            throw new IllegalArgumentException("Null 'yFormat' argument.");   
        }
        this.toolTipFormat = toolTipFormat;
        this.itemLabelFormat = itemLabelFormat;
        this.xFormat = xFormat;
        this.yFormat = yFormat;

    }

    /**
     * Creates an item label generator using the specified number formatters.
     *
     * @param toolTipFormat  the tooltip format string (<code>null</code> not permitted).
     * @param itemLabelFormat  the item label format string (<code>null</code> not permitted).
     * @param xFormat  the format object for the x values (<code>null</code> permitted).
     * @param yFormat  the format object for the y values (<code>null</code> not permitted).
     */
    public AbstractXYItemLabelGenerator(String toolTipFormat,
                                        String itemLabelFormat,
                                        DateFormat xFormat, 
                                        NumberFormat yFormat) {

        this(toolTipFormat, itemLabelFormat, NumberFormat.getInstance(), yFormat);
        this.xDateFormat = xFormat;
    
    }
    
    /**
     * Returns the number formatter for the x-values.
     *
     * @return the number formatter (never <code>null</code>).
     */
    public NumberFormat getXFormat() {
        return this.xFormat;
    }

    /**
     * Returns the date formatter for the x-values.
     *
     * @return the date formatter (possibly <code>null</code>).
     */
    public DateFormat getXDateFormat() {
        return this.xDateFormat;
    }

    /**
     * Returns the number formatter for the y-values.
     *
     * @return the number formatter (never <code>null</code>).
     */
    public NumberFormat getYFormat() {
        return this.yFormat;
    }

    /**
     * Generates the tooltip text for a data item.
     *
     * @param dataset  the dataset (<code>null</code> permitted).
     * @param series  the series (zero-based index).
     * @param item  the item (zero-based index).
     *
     * @return the tooltip text (possibly <code>null</code>).
     */
    public String generateToolTip(XYDataset dataset, int series, int item) {

        String result = null;    
        if (dataset != null) {
            Object[] items = createItemArray(dataset, series, item);
            result = MessageFormat.format(this.toolTipFormat, items);
        }
        return result;

    }

    /**
     * Generates the item label text for a data item.
     *
     * @param dataset  the dataset (<code>null</code> permitted).
     * @param series  the series (zero-based index).
     * @param item  the item (zero-based index).
     *
     * @return the item label text (possibly <code>null</code>).
     */
    public String generateItemLabel(XYDataset dataset, int series, int item) {

        String result = null;    
        if (dataset != null) {
            Object[] items = createItemArray(dataset, series, item);
            result = MessageFormat.format(this.itemLabelFormat, items);
        }
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
     * @return the items (never <code>null</code>).
     */
    protected Object[] createItemArray(XYDataset dataset, int series, int item) {
        Object[] result = new Object[3];
        result[0] = dataset.getSeriesName(series);
        Number x = dataset.getXValue(series, item);
        if (this.xDateFormat != null) {
            result[1] = this.xDateFormat.format(new Date(x.longValue()));   
        }
        else {
            result[1] = this.xFormat.format(x);
        }
        Number y = dataset.getYValue(series, item);
        result[2] = this.yFormat.format(y);
        return result;
    }

    /**
     * Tests this object for equality with an arbitrary object.
     *
     * @param obj  the other object (<code>null</code> permitted).
     *
     * @return a boolean.
     */
    public boolean equals(Object obj) {

        if (obj == null) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        if (obj instanceof AbstractXYItemLabelGenerator) {
            AbstractXYItemLabelGenerator generator = (AbstractXYItemLabelGenerator) obj;
            if (!this.toolTipFormat.equals(generator.toolTipFormat)) {
                return false;   
            }
            if (!this.itemLabelFormat.equals(generator.itemLabelFormat)) {
                return false;   
            }
            if (!this.xFormat.equals(generator.xFormat)) {
                return false;   
            }
            if (!ObjectUtils.equal(this.xDateFormat, generator.xDateFormat)) {
                return false;   
            }
            if (!this.yFormat.equals(generator.yFormat)) {
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
