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
 * TimeSeriesURLGenerator.java
 * -------------------------------
 * (C) Copyright 2002, by Richard Atkinson and contributors.
 *
 * Original Author:  Richard Atkinson;
 * Contributor(s):   -;
 *
 * $Id: TimeSeriesURLGenerator.java,v 1.1 2007/10/10 19:52:22 vauchers Exp $
 *
 * Changes:
 * ----------------------------
 * 29-Aug-2002 : Initial version (RA);
 *
 */
package com.jrefinery.chart.urls;

import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import com.jrefinery.data.XYDataset;

public class TimeSeriesURLGenerator implements XYURLGenerator {
    /** A formatter for the date. */
    protected DateFormat dateFormat = DateFormat.getInstance();
    /** Prefix to the URL */
    protected String prefix = "index.html";
    /** Name to use to identify the series */
    protected String seriesParameterName = "series";
    /** Name to use to identify the item */
    protected String itemParameterName = "item";

    /**
     * Blank constructor
     */
    public TimeSeriesURLGenerator() {
    }

    /**
     * Construct TimeSeriesURLGenerator overriding defaults
     * @param dDateFormat A formatter for the date
     * @param sPrefix The prefix of the URL
     * @param sSeriesParameterName The name of the series parameter in the URL
     * @param sItemParameterName The name of the item parameter in the URL
     */
    public TimeSeriesURLGenerator(DateFormat dDateFormat, String sPrefix,
                                  String sSeriesParameterName, String sItemParameterName) {
        this.dateFormat = dDateFormat;
        this.prefix = sPrefix;
        this.seriesParameterName = sSeriesParameterName;
        this.itemParameterName = sItemParameterName;
    }

    /**
     * Generates a URL for a particular item within a series.
     *
     * @param dataset The dataset.
     * @param series The series number (zero-based index).
     * @param item The item number (zero-based index).
     * @return The generated URL
     */
    public String generateURL(XYDataset dataset, int series, int item) {
        String result = this.prefix;
        boolean firstParameter = result.indexOf("?") == -1;
        String seriesName = dataset.getSeriesName(series);
        if (seriesName != null) {
            result += firstParameter ? "?" : "&";
            result += this.seriesParameterName + "=" + seriesName;
            firstParameter = false;
        }

        long x = dataset.getXValue(series, item).longValue();
        String xValue = this.dateFormat.format(new Date(x));
        result += firstParameter ? "?" : "&";
        result += this.itemParameterName + "=" + xValue;

        return result;
    }


}
