/* ============================================
 * JFreeChart : a free Java chart class library
 * ============================================
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
 * --------------------------
 * DefaultHighLowDataset.java
 * --------------------------
 * (C) Copyright 2002, by Simba Management Limited.
 *
 * Original Author:  David Gilbert (for Simba Management Limited);
 * Contributor(s):   -;
 *
 * $Id: DefaultHighLowDataset.java,v 1.1 2007/10/10 19:52:14 vauchers Exp $
 *
 * Changes
 * -------
 * 21-Mar-2002 : Version 1 (DG);
 *
 */

package com.jrefinery.data;

import java.util.Date;

/**
 * A simple implementation of the HighLowDataset.
 */
public class DefaultHighLowDataset extends AbstractSeriesDataset
    implements HighLowDataset
{

    /** The series name. */
    protected String seriesName;

    /** Storage for the dates. */
    protected Date[] date;

    /** Storage for the high values. */
    protected Number[] high;

    /** Storage for the low values. */
    protected Number[] low;

    /** Storage for the open values. */
    protected Number[] open;

    /** Storage for the close values. */
    protected Number[] close;

    /** Storage for the volume values. */
    protected Number[] volume;

    /**
     * Constructs a new high/low/open/close dataset.
     * <p>
     * The current implementation allows only one series in the dataset.
     * This may be extended in a future version.
     *
     * @param seriesName    the name of the series.
     * @param date          the dates.
     * @param high          the high values.
     * @param low           the low values.
     * @param open          the open values.
     * @param close         the close values.
     * @param volume        the vloume values.
     */
    public DefaultHighLowDataset(String seriesName,
                                 Date[] date,
                                 double[] high, double[] low,
                                 double[] open, double[] close,
                                 double[] volume) {

        this.seriesName = seriesName;
        this.date = date;
        this.high = this.createNumberArray(high);
        this.low = this.createNumberArray(low);
        this.open = this.createNumberArray(open);
        this.close = this.createNumberArray(close);
        this.volume = this.createNumberArray(volume);;

    }

    /**
     * Returns the name of the series stored in this dataset.
     *
     * @param i     the index of the series. Currently ignored.
     * @return the name of this series.
     */
    public String getSeriesName(int i) {
        return this.seriesName;
    }

    /**
     * Returns the x-value for one item in a series.
     * <p>
     * The value returned is a Long object generated from the underlying Date
     * object.
     *
     * @param series    The series (zero-based index).
     * @param item      The item (zero-based index).
     * @return The x-value.
     */
    public Number getXValue(int series, int item) {
        return new Long(date[item].getTime());
    }

    /**
     * Returns the y-value for one item in a series.
     * <p>
     * This method (from the XYDataset interface) is mapped to the
     * getCloseValue(...) method.
     *
     * @param series    The series (zero-based index).
     * @param item      The item (zero-based index).
     * @return The y-value.
     */
    public Number getYValue(int series, int item) {
        return this.getCloseValue(series, item);
    }

    /**
     * Returns the high-value for one item in a series.
     *
     * @param series    The series (zero-based index).
     * @param item      The item (zero-based index).
     * @return The high-value.
     */
    public Number getHighValue(int series, int item) {
        return high[item];
    }

    /**
     * Returns the low-value for one item in a series.
     *
     * @param series    The series (zero-based index).
     * @param item      The item (zero-based index).
     * @return The low-value.
     */
    public Number getLowValue(int series, int item) {
        return low[item];
    }

    /**
     * Returns the open-value for one item in a series.
     *
     * @param series    The series (zero-based index).
     * @param item      The item (zero-based index).
     * @return The open-value.
     */
    public Number getOpenValue(int series, int item) {
        return open[item];
    }

    /**
     * Returns the close-value for one item in a series.
     *
     * @param series    The series (zero-based index).
     * @param item      The item (zero-based index).
     * @return The close-value.
     */
    public Number getCloseValue(int series, int item) {
        return close[item];
    }

    /**
     * Returns the volume-value for one item in a series.
     *
     * @param series    The series (zero-based index).
     * @param item      The item (zero-based index).
     * @return The volume-value.
     */
    public Number getVolumeValue(int series, int item) {
        return volume[item];
    }

    /**
     * Returns the number of series in the dataset.
     * <p>
     * This implementation only allows one series.
     *
     * @return The number of series.
     */
    public int getSeriesCount() {
        return 1;
    }

    /**
     * Returns the number of items in the specified series.
     * @param series    The index (zero-based) of the series;
     * @return The number of items in the specified series.
     */
    public int getItemCount(int series) {
        return date.length;
    }

    /**
     * Constructs an array of Number objects from an array of doubles.
     *
     * @param data  the double values to convert.
     *
     * @return data as array of Number.
     */
    public static Number[] createNumberArray(double[] data) {

        Number[] result = new Number[data.length];

        for (int i=0; i<data.length; i++) {
            result[i] = new Double(data[i]);
        }

        return result;

    }

}
