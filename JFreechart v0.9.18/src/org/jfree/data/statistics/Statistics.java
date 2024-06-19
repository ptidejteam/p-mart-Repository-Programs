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
 * ---------------
 * Statistics.java
 * ---------------
 * (C) Copyright 2000-2003, by Matthew Wright and Contributors.
 *
 * Original Author:  Matthew Wright;
 * Contributor(s):   David Gilbert (for Object Refinery Limited);
 *
 * $Id: Statistics.java,v 1.1 2007/10/10 19:39:16 vauchers Exp $
 *
 * Changes (from 08-Nov-2001)
 * --------------------------
 * 08-Nov-2001 : Added standard header and tidied Javadoc comments (DG);
 *               Moved from JFreeChart to package com.jrefinery.data.* in JCommon class
 *               library (DG);
 * 24-Jun-2002 : Removed unnecessary local variable (DG);
 * 07-Oct-2002 : Fixed errors reported by Checkstyle (DG);
 *
 */

package org.jfree.data.statistics;

/**
 * A utility class that provides some simple statistical functions.
 *
 * @author Matthew Wright
 */
public class Statistics {

    /**
     * Returns the average of a set of numbers.
     *
     * @param data  the data.
     *
     * @return the average of a set of numbers.
     */
    public static double getAverage(Number[] data) {
        double sum = 0.0;
        int counter = 0;
        for (; counter < data.length; counter++) {
            sum = sum + data[counter].doubleValue();
        }
        return (sum / counter);
    }

    /**
     * Returns the standard deviation of a set of numbers.
     *
     * @param data  the data.
     *
     * @return the standard deviation of a set of numbers.
     */
    public static double getStdDev(Number[] data) {
        double avg = getAverage(data);
        double sum = 0.0;

        for (int counter = 0; counter < data.length; counter++) {
            double diff = data[counter].doubleValue() - avg;
            sum = sum + diff * diff;
        }
        return Math.sqrt(sum / (data.length - 1));
    }

    /**
     * Fits a straight line to a set of (x, y) data, returning the slope and
     * intercept.
     *
     * @param xData  the x-data.
     * @param yData  the y-data.
     *
     * @return a double array with the intercept in [0] and the slope in [1].
     */
    public static double[] getLinearFit(Number[] xData, Number[] yData) {

        // check arguments...
        if (xData.length != yData.length) {
            throw new IllegalArgumentException(
                "Statistics.getLinearFit(...): array lengths must be equal.");
        }

        double[] result = new double[2];
        // slope
        result[1] = getSlope(xData, yData);
        // intercept
        result[0] = getAverage(yData) - result[1] * getAverage(xData);

        return result;

    }

    /**
     * Finds the slope of a regression line using least squares.
     *
     * @param xData  an array of Numbers (the x values).
     * @param yData  an array of Numbers (the y values).
     *
     * @return the slope.
     */
    public static double getSlope(Number[] xData, Number[] yData) {

        // check arguments...
        if (xData.length != yData.length) {
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
        for (counter = 0; counter < xData.length; counter++) {
            sx = sx + xData[counter].doubleValue();
            sxx = sxx + Math.pow(xData[counter].doubleValue(), 2);
            sxy = sxy + yData[counter].doubleValue() * xData[counter].doubleValue();
            sy = sy + yData[counter].doubleValue();
        }
        return (sxy - (sx * sy) / counter) / (sxx - (sx * sx) / counter);

    }

    /**
     * Calculates the correlation between two datasets.  Both arrays should contain the same number
     * of items.  Null values are treated as zero.
     * <P>
     * Information about the correlation calculation was obtained from:
     * 
     * http://trochim.human.cornell.edu/kb/statcorr.htm
     * 
     * @param data1  the first dataset.
     * @param data2  the second dataset.
     * 
     * @return The correlation.
     */
    public static double getCorrelation(Number[] data1, Number[] data2) {
        if (data1 == null) {
            throw new IllegalArgumentException("Null 'data1' argument.");
        }
        if (data2 == null) {
            throw new IllegalArgumentException("Null 'data2' argument.");
        }
        if (data1.length != data2.length) {
            throw new IllegalArgumentException("'data1' and 'data2' arrays must have same length.");   
        }
        int n = data1.length;
        double sumX = 0.0;
        double sumY = 0.0;
        double sumX2 = 0.0;
        double sumY2 = 0.0;
        double sumXY = 0.0;
        for (int i = 0; i < n; i++) {
            double x = 0.0;
            if (data1[i] != null) {
                x = data1[i].doubleValue();   
            }
            double y = 0.0;
            if (data2[i] != null) {
                y = data2[i].doubleValue();   
            }
            sumX = sumX + x;
            sumY = sumY + y;
            sumXY = sumXY + (x * y);
            sumX2 = sumX2 + (x * x);
            sumY2 = sumY2 + (y * y);
        }
        return (n * sumXY - sumX * sumY) 
            / Math.pow((n * sumX2 - sumX * sumX) * (n * sumY2 - sumY * sumY), 0.5);      
    }

    /**
     * Returns a data set for a moving average on the data set passed in.
     *
     * @param xData  an array of the x data.
     * @param yData  an array of the y data.
     * @param period  the number of data points to average
     *
     * @return a double[][] the length of the data set in the first dimension,
     *         with two doubles for x and y in the second dimension
     */
    public static double[][] getMovingAverage(Number[] xData, Number[] yData, int period) {

        // check arguments...
        if (xData.length != yData.length) {
            throw new IllegalArgumentException(
                "Statistics.getMovingAverage(...): array lengths must be equal.");
        }

        if (period > xData.length) {
            throw new IllegalArgumentException(
                "Statistics.getMovingAverage(...): period can't be longer than dataset.");
        }

        double[][] result = new double[xData.length - period][2];
        for (int i = 0; i < result.length; i++) {
            result[i][0] = xData[i + period].doubleValue();
            // holds the moving average sum
            double sum = 0.0;
            for (int j = 0; j < period; j++) {
                sum += yData[i + j].doubleValue();
            }
            sum = sum / period;
            result[i][1] = sum;
        }
        return result;

    }

}
