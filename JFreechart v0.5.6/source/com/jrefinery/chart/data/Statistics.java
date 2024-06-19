/* =======================================
 * JFreeChart : a Java Chart Class Library
 * =======================================
 * Version:         0.5.6;
 * Project Lead:    David Gilbert (david.gilbert@bigfoot.com);
 *
 * File:            Statistics.java
 * Author:          Matthew Wright;
 * Contributor(s):  David Gilbert;
 *
 * (C) Copyright 2000, by Matthew Wright;
 *
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation;
 * either version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this library;
 * if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston,
 * MA 02111-1307, USA.
 *
 * $Id: Statistics.java,v 1.1 2007/10/10 18:52:17 vauchers Exp $
 *
 */

package com.jrefinery.chart.data;

/**
 *  A Utility class that provides some simple statistics functions.  Used
 *  for curve fits and plots.
 */
public class Statistics {

  static double getAvg(Number[] series) {
    double sum = 0.0;
    int counter = 0;
    for(; counter < series.length; counter++) {
      sum = sum + series[counter].doubleValue();
    }
    return (sum/counter);
  }

  static double getStdDev(Number[] series) {
    double avg = getAvg(series);
    double sum = 0.0;
    int counter = 0;
    double diff = 0.0;

    for(; counter < series.length; counter++) {
    diff = series[counter].doubleValue() - avg;
    sum = sum + diff * diff;
    }

    return Math.sqrt(sum/(counter - 1));
  }

  /**
   * finds the slope of the linear line fit to this dataset
   * @param x_data the X data
   * @param y_data the Y data
   *  @return a double array with the intercept in [0] and the slope in [1]
   */
  static double[] getLinearFit(Number[] x_data, Number[] y_data) {
    /* catch problems w/ data first */
    if(x_data.length != y_data.length) {
      throw new IllegalArgumentException("array lengths in getLinearFit must be equal");
    }
    double[] answer = new double[2];
    // get slope
    answer[1] = getSlope(x_data, y_data);
    // get intercept
    answer[0] = getAvg(y_data) - answer[1] * getAvg(x_data);
    return answer;
  }

  /**
   * finds the slope of a line using least squares
   * @param x_data an Array of Numbers (the x values) that will be used in the calcs
   * @param y_data an Array of Numbers (the y values)
   * @returns the slope as a double
   */
  static double getSlope(Number[] x_data, Number[] y_data) {
    /* catch problems w/ data first */
    if(x_data.length != y_data.length) {
      throw new IllegalArgumentException("array lengths in getSlope must be equal");
    }

    /********* stat function for linear slope ********
      y = a + bx
      a = ybar - b * xbar
          sum(x * y) - (sum (x) * sum(y)) / n
      b = ------------------------------------
          sum (x^2) - (sum(x)^2 / n
     ***************************************************/
     // sum of x, x^2, x * y, y
    double sx = 0.0, sxx = 0.0, sxy = 0.0, sy = 0.0, slope = 0.0;
    int counter;
    for(counter = 0; counter < x_data.length; counter++) {
      sx = sx + x_data[counter].doubleValue();
      sxx = sxx + Math.pow(x_data[counter].doubleValue(), 2);
      sxy = sxy + y_data[counter].doubleValue() * x_data[counter].doubleValue();
      sy = sy + y_data[counter].doubleValue();
    }
    return (sxy - (sx * sy)/counter)/ (sxx - (sx * sx) /counter);
  }

  /** finds the correlation between two datasets */
  static double getCorrelation(Number[] data1, Number[] data2) {

    /* catch problems w/ data first */
    if(data1.length != data2.length) {
      throw new IllegalArgumentException("array lengths in getMovingAvg must be equal");
    }

    double xavg = 0, yavg = 0;
    double xstd = 0, ystd = 0;
    int counter = 0;

    /* copy to a local variable */
    Number[] x_data = new Double[data1.length];
    Number[] y_data = new Double[data2.length];
    for(int i = 0; i < data1.length; i++) {
      x_data[i] = new Double(data1[i].doubleValue());
    }
    for(int i = 0; i < data2.length; i++) {
      y_data[i] = new Double(data2[i].doubleValue());
    }

    /* get averages and standard deviations for calculations */
    xavg = getAvg(x_data);
    yavg = getAvg(y_data);
    xstd = getStdDev(x_data);
    ystd = getStdDev(y_data);

    /* convert to standard units */
    for(; counter < x_data.length; counter++) {
      x_data[counter] = new Double((x_data[counter].doubleValue() - xavg)/xstd);
      y_data[counter] = new Double((y_data[counter].doubleValue() - yavg)/ystd);
    }

    /* get the product of the standard units */
    for(counter = 0; counter < x_data.length; counter++) {
      x_data[counter] = new Double(x_data[counter].doubleValue() * y_data[counter].doubleValue());
    }

    return getAvg(x_data);
  }

  /**
   * returns a data set for a moving average on the data set passed in.
   * @param x_data an array of the x data
   * @param y_data an array of the y data
   * @param period, the number of data points to average
   * @return a double[][] the length of the data set in the first dimension,
   *		with two doubles for x and y in the second dimension
   */
  static double[][] getMovingAvg(Number[] x_data, Number[] y_data, int period) {
    /* catch problems w/ data first */
    if(x_data.length != y_data.length) {
      throw new IllegalArgumentException("array lengths in getMovingAvg must be equal");
    }
    if(period > x_data.length) {
      throw new IllegalArgumentException("period can't be longer than dataset in getMovingAvg");
    }

    double[][] answer = new double[x_data.length - period][2];
    for(int i = 0; i < answer.length; i++) {
      answer[i][0] = x_data[i + period].doubleValue();
      // holds the moving average sum
      double sum = 0.0;
      for(int j = 0; j < period; j++) {
        sum += y_data[i + j].doubleValue();
      }
      sum = sum/period;
      answer[i][1] = sum;
    }
    return answer;
  }

}