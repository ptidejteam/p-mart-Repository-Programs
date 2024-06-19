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
 * ----------------------------------
 * BoxAndWhiskerToolTipGenerator.java
 * ----------------------------------
 * (C) Copyright 2003, by David Browning and Contributors.
 *
 * Original Author:  David Browning;
 * Contributor(s):   -;
 *
 * $Id: BoxAndWhiskerToolTipGenerator.java,v 1.1 2007/10/10 19:21:59 vauchers Exp $
 *
 * Changes
 * -------
 * 05-Aug-2003 : Version 1, contributed by David Browning (DG);
 * 13-Aug-2003 : Implemented Cloneable (DG);
 * 28-Aug-2003 : Updated for changes in dataset API (DG);
 *
 */

package org.jfree.chart.labels;

import java.io.Serializable;
import java.text.DateFormat;
import java.util.Date;

import org.jfree.data.XYDataset;
import org.jfree.data.statistics.BoxAndWhiskerXYDataset;

/**
 * A standard tooltip generator for plots that use data from a {@link BoxAndWhiskerXYDataset}.
 *
 * @author David Browning
 */
public class BoxAndWhiskerToolTipGenerator implements XYToolTipGenerator, 
                                                      Cloneable,
                                                      Serializable {

    /** The date formatter. */
    private DateFormat dateFormatter;

    /**
     * Creates a tool tip generator using the default date format.
     */
    public BoxAndWhiskerToolTipGenerator() {
        this(DateFormat.getInstance());
    }

    /**
     * Creates a tool tip generator using the supplied date formatter.
     *
     * @param formatter  the date formatter.
     */
    public BoxAndWhiskerToolTipGenerator(DateFormat formatter) {
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

        if (data instanceof BoxAndWhiskerXYDataset) {
            BoxAndWhiskerXYDataset d = (BoxAndWhiskerXYDataset) data;
            Number median = d.getMedianValue(series, item);
            Number max = d.getMaxRegularValue(series, item);
            Number min = d.getMinRegularValue(series, item);
            Number q1 = d.getQ1Value(series, item);
            Number q3 = d.getQ3Value(series, item);
            Number x = d.getXValue(series, item);

            result = d.getSeriesName(series);

            if (x != null) {
                Date date = new Date(x.longValue());
                result = result + " -> Date=" + dateFormatter.format(date);
                if (median != null) {
                    result = result + " Median=" + median.toString();
                }
                if (max != null) {
                    result = result + " Max=" + max.toString();
                }
                if (min != null) {
                    result = result + " Min=" + min.toString();
                }
                if (q1 != null) {
                    result = result + " Q1=" + q1.toString();
                }
                if (q3 != null) {
                    result = result + " Q3=" + q3.toString();
                }
            }

        }

        return result;

    }

    /**
     * Returns an independent copy of the generator.
     * 
     * @return A clone.
     * 
     * @throws CloneNotSupportedException if cloning is not supported.
     */
    public Object clone() throws CloneNotSupportedException {
        
        BoxAndWhiskerToolTipGenerator clone = (BoxAndWhiskerToolTipGenerator) super.clone();

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

        if (o instanceof BoxAndWhiskerToolTipGenerator) {
            BoxAndWhiskerToolTipGenerator generator = (BoxAndWhiskerToolTipGenerator) o;
            return this.dateFormatter.equals(generator.dateFormatter);
        }

        return false;

    }
}
