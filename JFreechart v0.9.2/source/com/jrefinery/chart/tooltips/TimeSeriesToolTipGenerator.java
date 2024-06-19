/* =======================================
 * JFreeChart : a Java Chart Class Library
 * =======================================
 *
 * Project Info:  http://www.object-refinery.com/jfreechart/index.html
 * Project Lead:  David Gilbert (david.gilbert@object-refinery.com);
 *
 * (C) Copyright 2000-2002, by Simba Management Limited and Contributors.
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
 * -------------------------------
 * TimeSeriesToolTipGenerator.java
 * -------------------------------
 * (C) Copyright 2001, 2002, by Simba Management Limited.
 *
 * Original Author:  David Gilbert (for Simba Management Limited);
 * Contributor(s):   -;
 *
 * $Id: TimeSeriesToolTipGenerator.java,v 1.1 2007/10/10 19:42:01 vauchers Exp $
 *
 * Changes (since 30-May-2002):
 * ----------------------------
 * 30-May-2002 : Added series name to tool tip (DG);
 *
 */

package com.jrefinery.chart.tooltips;

import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.text.NumberFormat;
import java.text.DecimalFormat;
import com.jrefinery.data.XYDataset;

/**
 * A standard tooltip generator for plots that use data from an XYDataset.
 */
public class TimeSeriesToolTipGenerator implements XYToolTipGenerator {

    /** A formatter for the time. */
    protected DateFormat domainFormat;

    /** A formatter for the value. */
    protected NumberFormat rangeFormat;

    /**
     * Default constructor.
     */
    public TimeSeriesToolTipGenerator() {

        this(DateFormat.getInstance(), NumberFormat.getNumberInstance());

    }

    public TimeSeriesToolTipGenerator(String dateFormat, String valueFormat) {
        this(new SimpleDateFormat(dateFormat), new DecimalFormat(valueFormat));
    }

    /**
     * Constructs a new tooltip generator using the specified number formats.
     *
     * @param domainFormat The format object for the dates.
     * @param rangeFormat The format object for the values.
     */
    public TimeSeriesToolTipGenerator(DateFormat domainFormat, NumberFormat rangeFormat) {

        this.domainFormat = domainFormat;
        this.rangeFormat = rangeFormat;

    }

    /**
     * Generates a tooltip text item for a particular item within a series.
     *
     * @param data The dataset.
     * @param series The series number (zero-based index).
     * @param item The item number (zero-based index).
     */
    public String generateToolTip(XYDataset data, int series, int item) {

        String result = data.getSeriesName(series)+": ";
        long x = data.getXValue(series, item).longValue();
        result = result+ "date = "+domainFormat.format(new Date(x));

        Number y = data.getYValue(series, item);
        if (y!=null) {
            result = result+", value = "+rangeFormat.format(y);
        }
        else {
            result = result+", value = null";
        }

        return result;
    }

}