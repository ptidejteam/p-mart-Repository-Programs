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
 * ----------------------------
 * HighLowToolTipGenerator.java
 * ----------------------------
 * (C) Copyright 2001, 2002, by Simba Management Limited.
 *
 * Original Author:  David Gilbert (for Simba Management Limited);
 * Contributor(s):   -;
 *
 * $Id: HighLowToolTipGenerator.java,v 1.1 2007/10/10 19:01:21 vauchers Exp $
 *
 * Changes
 * -------
 * 13-Dec-2001 : Version 1 (DG);
 * 16-Jan-2002 : Completed Javadocs (DG);
 * 23-Apr-2002 : Added date to the tooltip string (DG);
 *
 */

package com.jrefinery.chart.tooltips;

import java.text.DateFormat;
import java.util.Date;
import com.jrefinery.data.HighLowDataset;
import com.jrefinery.data.XYDataset;

/**
 * A standard tooltip generator for plots that use data from a HighLowDataset.
 */
public class HighLowToolTipGenerator implements XYToolTipGenerator {

    protected DateFormat dateFormatter;

    public HighLowToolTipGenerator() {
        this.dateFormatter = DateFormat.getInstance();
    }

    /**
     * Generates a tooltip text item for a particular item within a series.
     *
     * @param data The dataset.
     * @param series The series number (zero-based index).
     * @param item The item number (zero-based index).
     */
    public String generateToolTip(XYDataset data, int series, int item) {

        String result = null;

        if (data instanceof HighLowDataset) {
            HighLowDataset d = (HighLowDataset)data;
            Number high = d.getHighValue(series, item);
            Number low = d.getLowValue(series, item);
            Number open = d.getOpenValue(series, item);
            Number close = d.getCloseValue(series, item);
            Number x = d.getXValue(series, item);

            result = d.getSeriesName(series);
            if (x!=null) {
                Date date = new Date(x.longValue());
                result = result+"--> Date="+dateFormatter.format(date);
            }
            if (high!=null) result = result + " High="+high.toString();
            if (low!=null) result = result +" Low="+low.toString();
            if (open!=null) result = result +" Open="+open.toString();
            if (close!=null) result = result +" Close="+close.toString();

        }

        return result;

    }


}