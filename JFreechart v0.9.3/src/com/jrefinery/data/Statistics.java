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
 * ---------------
 * Statistics.java
 * ---------------
 * (C) Copyright 2000, 2001, by Matthew Wright and Contributors.
 *
 * Original Author:  Matthew Wright;
 * Contributor(s):   David Gilbert (for Simba Management Limited);
 *
 * $Id: Statistics.java,v 1.1 2007/10/10 19:52:14 vauchers Exp $
 *
 * Changes (from 08-Nov-2001)
 * --------------------------
 * 08-Nov-2001 : Added standard header and tidied Javadoc comments (DG);
 *               Moved from JFreeChart to package com.jrefinery.data.* in JCommon class
 *               library (DG);
 * 24-Jun-2002 : Removed unnecessary local variable (DG);
 *
 */

package com.jrefinery.data;

/**
 *  A utility class that provides some simple statistical functions.
 */
public class Statistics {

    /**
     * Returns the average of a set of numbers.
     * @param data      The data.
     * @return the average of a set of numbers.
     */
    public static double getAverage(Number[] data) {
        double sum = 0.0;
        int counter = 0;
        for(; counter < data.length; counter++) {
            sum = sum + data[counter].doubleValue();
        }
        return (sum/counter);
    }

    /**
     * Returns the standard deviation of a set of numbers.
     * @param data The data.
     *
     * @return the standard deviation of a set of numbers.
     */
    public static double getStdDev(Number[] data) {
        double avg = getAverage(data);
        double sum = 0.0;
        int counter = 0;
        double diff = 0.0;

        for(; counter < data.length; counter++) {
            diff = data[counter].doubleValue() - avg;
            sum = sum + diff * diff;
        }
        return Math.sqrt(sum/(counter - 1));
    }

    /**
     * Fits a straight line to a set of (x, y) data, returning the slope and
     * intercept.
     * @param x_data    The x-data.
     * @param y_data    The y-data.
     * @return A double array with the intercept in [0] and the slope in [1].
     */
    public static double[] getLinearFit(Number[] x_data, Number[] y_data) {

        // check arguments...
        if(x_data.length != y_data.length) {
            throw new IllegalArgumentException(
                "Statistics.getLinearFit(...): array lengths must be equal.");
        }

        double[] result = new double[2];
        // slope
        result[1] = getSlope(x_data, y_data);
        // intercept
        result[0] = getAverage(y_data) - result[1] * getAverage(x_data);

        return result;

    }

    /**
     * Finds the slope of a regression line using least squares.
     * @param x_data    An array of Numbers (the x values).
     * @param y_data    An array of Numbers (the y values).
     * @return The slope.
     */
    public static double getSlope(Number[] x_data, Number[] y_data) {

        // check arguments...
        if(x_data.length != y_data.length) {
            throw new IllegalArgumentException(
                "Statistics.getSlope(...): array lengths must be equal.");
        }

        // ********* stat function for linear slope ********
        // y = a + bx
        // a = ybar - b * xbar
        //     sum(x * y) - (sum (x) * sum(y)) / n
        // b = ------------------------------------
        //     sum (x^2) - (sum(x)^2 / n
        // *************************************************

        // sum of x, x^2, x * y, y
        double sx = 0.0, sxx = 0.0, sxy = 0.0, sy = 0.0;
        int counter;
        for(counter = 0; counter < x_data.length; counter++) {
            sx = sx + x_data[counter].doubleValue();
            sxx = sxx + Math.pow(x_data[counter].doubleValue(), 2);
            sxy = sxy + y_data[counter].doubleValue()
                * x_data[counter].doubleValue();
            sy = sy + y_data[counter].doubleValue();
        }
        return (sxy - (sx * sy)/counter)/ (sxx - (sx * sx) /counter);

    }

    /**
     * Calculates the correlation between two datasets.
     * @param data1     The first dataset.
     * @param data2     The second dataset.
     *
     * @return the correlation between two datasets.
     */
    public static double getCorrelation(Number[] data1, Number[] data2) {

        // check arguments...
        if(data1.length != data2.length) {
            throw new IllegalArgumentException(
                "Statistics.getCorrelation(...): array lengths must be equal.");
        }

        double xavg = 0, yavg = 0;
        double xstd = 0, ystd = 0;
        int counter = 0;

        // copy to a local variable
        Number[] x_data = new Double[data1.length];
        Number[] y_data = new Double[data2.length];
        for(int i = 0; i < data1.length; i++) {
            x_data[i] = new Double(data1[i].doubleValue());
        }
        for(int i = 0; i < data2.length; i++) {
            y_data[i] = new Double(data2[i].doubleValue());
        }

        // get averages and standard deviations for calculations
        xavg = getAverage(x_data);
        yavg = getAverage(y_data);
        xstd = getStdDev(x_data);
        ystd = getStdDev(y_data);

        // convert to standard units
        for(; counter < x_data.length; counter++) {
            x_data[counter] =
                new Double((x_data[counter].doubleValue() - xavg)/xstd);
            y_data[counter] =
                new Double((y_data[counter].doubleValue() - yavg)/ystd);
        }

        // get the product of the standard units
        for(counter = 0; counter < x_data.length; counter++) {
            x_data[counter] = new Double(x_data[counter].doubleValue()
                                         * y_data[counter].doubleValue());
        }

        return getAverage(x_data);

    }

    /**
     * Returns a data set for a moving average on the data set passed in.
     * @param x_data An array of the x data.
     * @param y_data An array of the y data.
     * @param period The number of data points to average
     * @return A double[][] the length of the data set in the first dimension,
	 *           with two doubles for x and y in the second dimension
     */
    public static double[][] getMovingAverage(Number[] x_data, Number[] y_data, int period) {

        // check arguments...
        if(x_data.length != y_data.length) {
            throw new IllegalArgumentException(
                "Statistics.getMovingAverage(...): array lengths must be equal.");
        }

        if(period > x_data.length) {
            throw new IllegalArgumentException(
                "Statistics.getMovingAverage(...): "
                + "period can't be longer than dataset.");
        }

        double[][] result = new double[x_data.length - period][2];
        for(int i = 0; i < result.length; i++) {
            result[i][0] = x_data[i + period].doubleValue();
            // holds the moving average sum
            double sum = 0.0;
            for(int j = 0; j < period; j++) {
                sum += y_data[i + j].doubleValue();
            }
            sum = sum/period;
            result[i][1] = sum;
        }
        return result;

    }

}
