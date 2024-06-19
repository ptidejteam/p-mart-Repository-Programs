/* =======================================
 * JFreeChart : a Java Chart Class Library
 * =======================================
 * Version:         0.5.6;
 * Project Lead:    David Gilbert (david.gilbert@bigfoot.com);
 *
 * File:            MovingAveragePlotFitAlgorithm
 * Author:          Matthew Wright
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
 * $Id: MovingAveragePlotFitAlgorithm.java,v 1.1 2007/10/10 18:52:17 vauchers Exp $
 *
 */

package com.jrefinery.chart.data;

import java.util.Vector;
import com.jrefinery.chart.*;

public class MovingAveragePlotFitAlgorithm implements PlotFitAlgorithm {

  private XYDataSource datasource;
  private int period = 5;
  private Vector plots;

  /** @return the name that you want to see in the legend. */
  public String getName() { return "Moving Average"; }

  /**
   * sets the period for this moving average algorithm
   * @param period the number of points to include in the average
   */
  public void setPeriod(int period) {
    this.period = period;
  }

  /**
   *	@param ds 	the XYDataSource for this PlotFit
   */
  public void setXYDataSource(XYDataSource ds) {
    this.datasource = datasource;

    /*
     * build the x and y data arrays to be passed to the
     * statistics class to get a linear fit and store them
     * for each dataset in the datasets Vector
     */
    Vector datasets = new Vector();
    for(int i = 0; i < ds.getSeriesCount(); i++) {
      int seriessize = ds.getItemCount(i);
      Number[] x_data = new Number[seriessize];
      Number[] y_data = new Number[seriessize];
      for(int j = 0; j < seriessize; j++) {
        x_data[j] = ds.getXValue(i,j);
        y_data[j] = ds.getYValue(i,j);
      }
      Vector pair = new Vector();
      pair.addElement(x_data);
      pair.addElement(y_data);
      datasets.addElement(pair);
    }
    plots = new Vector();
    for(int j = 0; j < datasets.size(); j++) {
      Vector pair = (Vector)datasets.elementAt(j);
      Number[] x_data = (Number[])pair.elementAt(0);
      Number[] y_data = (Number[])pair.elementAt(1);
      plots.addElement(new ArrayHolder(Statistics.getMovingAvg(x_data, y_data, period)));
    }
  }

  /**
   * for a given x, must return a y
   * @param x the x value
   * @param i the series
   * @return the y value
   */
  public Number getY(int i, Number x) {

    /*
     * for a moving average, this returns a number if there is a match
     * for that y and series, otherwise, it returns a null reference
     */
    double[][] mavg = ((ArrayHolder)plots.elementAt(i)).getArray();
    for(int j = 0; j < mavg.length; j++) {

      /* if the x matches up, we have a moving average point for this x */
      if(mavg[j][0] == x.doubleValue()) {
        return new Double(mavg[j][1]);
      }
    }
    /* if we don't return null */
    return null;
  }

}

/* a utility class to hold the moving average arrays in a Vector */
class ArrayHolder {
  private double[][] array;

  ArrayHolder(double[][] array) {
    this.array = array;
  }

  public double[][] getArray() {
    return array;
  }

}