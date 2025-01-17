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
 * --------------------------------
 * StandardXYZToolTipGenerator.java
 * --------------------------------
 * (C) Copyright 2003, by Object Refinery Limited.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited);
 * Contributor(s):   -;
 *
 * $Id: StandardXYZToolTipGenerator.java,v 1.1 2007/10/10 19:19:12 vauchers Exp $
 *
 * Changes
 * -------
 * 03-Feb-2003 : Version 1 (DG);
 *
 */

package org.jfree.chart.labels;

import java.text.NumberFormat;

import org.jfree.data.XYZDataset;

/**
 * A standard tool tip generator for use with {@link XYZDataset} data.
 *
 * @author David Gilbert
 */
public class StandardXYZToolTipGenerator extends StandardXYToolTipGenerator
                                         implements XYZToolTipGenerator {

    /** A formatter for the z value. */
    private NumberFormat zFormat;

    /**
     * Creates a tool tip generator using default number formatters.
     */
    public StandardXYZToolTipGenerator() {

        this(NumberFormat.getNumberInstance(),
             NumberFormat.getNumberInstance(),
             NumberFormat.getNumberInstance());

    }

    /**
     * Cnstructs a tool tip generator using the specified number formatters.
     *
     * @param xFormat  the format object for the x values.
     * @param yFormat  the format object for the y values.
     * @param zFormat  the format object for the z values.
     */
    public StandardXYZToolTipGenerator(NumberFormat xFormat,
                                       NumberFormat yFormat,
                                       NumberFormat zFormat) {

        super(xFormat, yFormat);
        this.zFormat = zFormat;

    }

    /**
     * Returns the number formatter for the z-values.
     *
     * @return the number formatter for the z-values.
     */
    public NumberFormat getZFormat() {
        return this.zFormat;
    }

    /**
     * Generates a tool tip text item for a particular item within a series.
     *
     * @param data  the dataset.
     * @param series  the series index (zero-based).
     * @param item  the item index (zero-based).
     *
     * @return the tooltip text.
     */
    public String generateToolTip(XYZDataset data, int series, int item) {

        String result = data.getSeriesName(series) + ": ";
        Number x = data.getXValue(series, item);
        result = result + "x: " + getXFormat().format(x);

        Number y = data.getYValue(series, item);
        result = result + "y: " + getYFormat().format(y);

        Number z = data.getZValue(series, item);
        if (z != null) {
            result = result + ", z: " + zFormat.format(z);
        }
        else {
            result = result + ", z: null";
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

        if (o instanceof StandardXYZToolTipGenerator) {
            StandardXYZToolTipGenerator generator = (StandardXYZToolTipGenerator) o;
            return (getXFormat().equals(generator.getXFormat())
                    && getYFormat().equals(generator.getYFormat())
                    && getZFormat().equals(generator.getZFormat()));
        }
        return false;

    }

}
