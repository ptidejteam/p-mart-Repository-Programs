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
 * ------------------------------
 * HighLowItemLabelGenerator.java
 * ------------------------------
 * (C) Copyright 2001-2004, by Object Refinery Limited.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited);
 * Contributor(s):   -;
 *
 * $Id: HighLowItemLabelGenerator.java,v 1.1 2007/10/10 19:29:21 vauchers Exp $
 *
 * Changes
 * -------
 * 13-Dec-2001 : Version 1 (DG);
 * 16-Jan-2002 : Completed Javadocs (DG);
 * 23-Apr-2002 : Added date to the tooltip string (DG);
 * 26-Sep-2002 : Fixed errors reported by Checkstyle (DG);
 * 21-Mar-2003 : Implemented Serializable (DG);
 * 13-Aug-2003 : Implemented Cloneable (DG);
 * 17-Nov-2003 : Implemented PublicCloneable (DG);
 * 25-Feb-2004 : Renamed XYToolTipGenerator --> XYItemLabelGenerator (DG);
 *
 */

package org.jfree.chart.labels;

import java.io.Serializable;
import java.text.DateFormat;
import java.util.Date;

import org.jfree.data.HighLowDataset;
import org.jfree.data.XYDataset;
import org.jfree.util.PublicCloneable;

/**
 * A standard item label generator for plots that use data from a {@link HighLowDataset}.
 *
 * @author David Gilbert
 */
public class HighLowItemLabelGenerator implements XYItemLabelGenerator, 
                                                  Cloneable, 
                                                  PublicCloneable,
                                                  Serializable {

    /** The date formatter. */
    private DateFormat dateFormatter;

    /**
     * Creates an item label generator using the default date format.
     */
    public HighLowItemLabelGenerator() {
        this(DateFormat.getInstance());
    }

    /**
     * Creates a tool tip generator using the supplied date formatter.
     *
     * @param formatter  the date formatter.
     */
    public HighLowItemLabelGenerator(DateFormat formatter) {
        this.dateFormatter = formatter;
    }

    /**
     * Generates a tooltip text item for a particular item within a series.
     *
     * @param data  the dataset.
     * @param series  the series (zero-based index).
     * @param item  the item (zero-based index).
     *
     * @return the tooltip text.
     */
    public String generateToolTip(XYDataset data, int series, int item) {

        String result = null;

        if (data instanceof HighLowDataset) {
            HighLowDataset d = (HighLowDataset) data;
            Number high = d.getHighValue(series, item);
            Number low = d.getLowValue(series, item);
            Number open = d.getOpenValue(series, item);
            Number close = d.getCloseValue(series, item);
            Number x = d.getXValue(series, item);

            result = d.getSeriesName(series);

            if (x != null) {
                Date date = new Date(x.longValue());
                result = result + "--> Date=" + dateFormatter.format(date);
                if (high != null) {
                    result = result + " High=" + high.toString();
                }
                if (low != null) {
                    result = result + " Low=" + low.toString();
                }
                if (open != null) {
                    result = result + " Open=" + open.toString();
                }
                if (close != null) {
                    result = result + " Close=" + close.toString();
                }
            }

        }

        return result;

    }

    /**
     * Generates a label for the specified item. The label is typically a formatted version of 
     * the data value, but any text can be used.
     *
     * @param dataset  the dataset (<code>null</code> not permitted).
     * @param series  the series index (zero-based).
     * @param category  the category index (zero-based).
     *
     * @return the label (possibly <code>null</code>).
     */
    public String generateItemLabel(XYDataset dataset, int series, int category) {
        return null;  //TODO: implement this method properly
    }

    /**
     * Returns an independent copy of the generator.
     * 
     * @return A clone.
     * 
     * @throws CloneNotSupportedException if cloning is not supported.
     */
    public Object clone() throws CloneNotSupportedException {
        
        HighLowItemLabelGenerator clone = (HighLowItemLabelGenerator) super.clone();

        if (this.dateFormatter != null) {
            clone.dateFormatter = (DateFormat) this.dateFormatter.clone();
        }
        
        return clone;
        
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

        if (o instanceof HighLowItemLabelGenerator) {
            HighLowItemLabelGenerator generator = (HighLowItemLabelGenerator) o;
            return this.dateFormatter.equals(generator.dateFormatter);
        }

        return false;

    }
}
