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
 * DefaultBoxAndWhiskerDataset.java
 * --------------------------------
 * (C) Copyright 2003, by David Browning and Contributors.
 *
 * Original Author:  David Browning;
 * Contributor(s):   -;
 *
 * $Id: DefaultBoxAndWhiskerDataset.java,v 1.1 2007/10/10 19:09:12 vauchers Exp $
 *
 * Changes
 * -------
 * 05-Aug-2003 : Version 1, contributed by David Browning (DG);
 *
 */

package org.jfree.data;

import java.util.Date;
import java.text.DecimalFormat;

/**
 * A simple implementation of the {@link BoxAndWhiskerDataset}.
 *
 * @author David Browning
 */
public class DefaultBoxAndWhiskerDataset extends AbstractSeriesDataset implements BoxAndWhiskerDataset {

    /** The series name. */
    private String seriesName;

    /** Storage for the dates. */
    private Date[] date;

    /** Storage for the max values. */
    private Number[] max;

    /** Storage for the min values. */
    private Number[] min;

    /** Storage for the average values. */
    private Number[] average;

    /** Storage for the median values. */
    private Number[] median;

    /** Storage for the q1Median values. */
    private Number[] q1Median;

    /** Storage for the q3Median values. */
    private Number[] q3Median;

    /** Storage for the replicateCount values. */
    private Number[] replicateCount;

    /** Storage for the outlier values. */
    private Number[][] outliers;

    /**
     * Constructs a new high/low/open/close dataset.
     * <p>
     * The current implementation allows only one series in the dataset.
     * This may be extended in a future version.
     *
     * @param seriesName  the name of the series.
     * @param date  the dates.
     * @param high  the high values.
     * @param low  the low values.
     * @param open  the open values.
     * @param close  the close values.
     * @param volume  the volume values.
     */
    public DefaultBoxAndWhiskerDataset(String seriesName,
                                       Date[] date,
                                       double[] max, double[] min,
                                       double[] average, double[] median,
                                       double[] q1Median, double[] q3Median,
                                       double[][] outliers,
                                       double[] replicateCount) {

        this.seriesName = seriesName;
        this.date = date;
        this.max = createNumberArray(max);
        this.min = createNumberArray(min);
        this.average = createNumberArray(average);
        this.median = createNumberArray(median);
        this.q1Median = createNumberArray(q1Median);
        this.q3Median = createNumberArray(q3Median);
        this.replicateCount = createNumberArray(replicateCount);

        // create outliers double array
        this.outliers = new Number[outliers.length][];
        for (int i = 0; i < outliers.length; i++ ) {
            this.outliers[i] = DefaultBoxAndWhiskerDataset.createNumberArray(outliers[i]);
        }

    }

    /**
     * Returns the name of the series stored in this dataset.
     *
     * @param i  the index of the series. Currently ignored.
     * @return the name of this series.
     */
    public String getSeriesName(int i) {
        return this.seriesName;
    }

    /**
     * Returns the x-value for one item in a series.
     * <p>
     * The value returned is a Long object generated from the underlying Date object.
     *
     * @param series  the series (zero-based index).
     * @param item  the item (zero-based index).
     *
     * @return the x-value.
     */
    public Number getXValue(int series, int item) {
        return new Long(date[item].getTime());
    }

    /**
     * Returns the x-value for one item in a series, as a Date.
     * <p>
     * This method is provided for convenience only.
     *
     * @param series  the series (zero-based index).
     * @param item  the item (zero-based index).
     *
     * @return the x-value as a Date.
     */
    public Date getXDate(int series, int item) {
        return date[item];
    }

    /**
     * Returns the y-value for one item in a series.
     * <p>
     * This method (from the XYDataset interface) is mapped to the getMaxOutlierValue(...) method.
     *
     * @param series  the series (zero-based index).
     * @param item  the item (zero-based index).
     *
     * @return the y-value.
     */
    public Number getYValue(int series, int item) {
        return new Double(this.getMaxOutlierValue(series, item).doubleValue() * 1.05);  // adds a small amount of space...
    }

    private Number getMaxOutlierValue(int series, int item) {
        Number result = new Double(0.0);
        for (int i = 0; i < outliers[item].length; i++) {
            if (result.doubleValue() < outliers[item][i].doubleValue()) {
                result = outliers[item][i];
            }
        }
        return result;
    }

    /**
     * Returns the max-value for one item in a series.
     *
     * @param series  the series (zero-based index).
     * @param item  the item (zero-based index).
     *
     * @return the max-value.
     */
    public Number getMaxValue(int series, int item) {
        return max[item];
    }

    /**
     * Returns the min-value for one item in a series.
     *
     * @param series  the series (zero-based index).
     * @param item  the item (zero-based index).
     *
     * @return  the min-value.
     */
    public Number getMinValue(int series, int item) {
        return min[item];
    }

    /**
     * Returns the average-value for one item in a series.
     *
     * @param series  the series (zero-based index).
     * @param item  the item (zero-based index).
     *
     * @return the average-value.
     */
    public Number getAverageValue(int series, int item) {
        return average[item];
    }

    /**
     * Returns the median-value for one item in a series.
     *
     * @param series  the series (zero-based index).
     * @param item  the item (zero-based index).
     *
     * @return the median-value.
     */
    public Number getMedianValue(int series, int item) {
        return median[item];
    }

    /**
     * Returns the q1Median-value for one item in a series.
     *
     * @param series  the series (zero-based index).
     * @param item  the item (zero-based index).
     *
     * @return the q1Median-value.
     */
    public Number getQ1MedianValue(int series, int item) {
        return q1Median[item];
    }

    /**
     * Returns the q3Median-value for one item in a series.
     *
     * @param series  the series (zero-based index).
     * @param item  the item (zero-based index).
     *
     * @return the q3Median-value.
     */
    public Number getQ3MedianValue(int series, int item) {
        return q3Median[item];
    }

    /**
     * Returns the outlier-values for one item in a series.
     *
     * @param series  the series (zero-based index).
     * @param item  the item (zero-based index).
     *
     * @return the outlier-values.
     */
    public Number[] getOutliersArray(int series, int item) {
        return outliers[item];
    }

    /**
     * Returns the replicate count for one item in a series.
     *
     * @param series  the series (zero-based index).
     * @param item  the item (zero-based index).
     *
     * @return the replicate count.
     */
    public Number getReplicateCount(int series, int item) {
        return replicateCount[item];
    }

    /**
     * Returns the number of series in the dataset.
     * <p>
     * This implementation only allows one series.
     *
     * @return the number of series.
     */
    public int getSeriesCount() {
        return 1;
    }

    /**
     * Returns the number of items in the specified series.
     *
     * @param series  the index (zero-based) of the series.
     *
     * @return  the number of items in the specified series.
     */
    public int getItemCount(int series) {
        return date.length;
    }

    public String makeString(int series, int item) {
        DecimalFormat format=new DecimalFormat("#0.00;#0.00");
        Number[] outliers = this.getOutliersArray(series, item);
        String s = "";
        for (int i =0; i < outliers.length; i++) {
            s += format.format(outliers[i]) + " ";
        }

        String maxString = "Mx = " + format.format(this.getMaxValue(series, item).doubleValue());
        String minString = ", Mn = " + format.format(this.getMinValue(series, item).doubleValue());
        String avgString = ", Av = " + format.format(this.getAverageValue(series, item).doubleValue());
        String medString = ", Md = " + format.format(this.getMedianValue(series, item).doubleValue());
        String q1MString = ", Q1 = " + format.format(this.getQ1MedianValue(series, item).doubleValue());
        String q3MString = ", Q3 = " + format.format(this.getQ3MedianValue(series, item).doubleValue());
        String repString = ", RC = " + format.format(this.getReplicateCount(series, item).doubleValue());
        String outString = ", Ol = [" + s + "]";
        return maxString + minString + avgString + medString + q1MString + q3MString + repString + outString;
    }

    /**
     * Constructs an array of Number objects from an array of doubles.
     *
     * @param data  the double values to convert.
     *
     * @return data as array of Number.
     */
    public static Number[] createNumberArray(double[] data) {

        Number[] result = new Number[0];

        if (data != null) {
            result = new Number[data.length];

            for (int i = 0; i < data.length; i++) {
                result[i] = new Double(data[i]);
            }
        }

        return result;

    }

}
