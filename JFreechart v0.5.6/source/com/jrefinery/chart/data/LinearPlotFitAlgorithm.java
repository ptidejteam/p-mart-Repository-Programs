/* =======================================
 * JFreeChart : a Java Chart Class Library
 * =======================================
 * Version:         0.5.6;
 * Project Lead:    David Gilbert (david.gilbert@bigfoot.com);
 *
 * File:            LinearPlotFitAlgorithm.java
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
 * $Id: LinearPlotFitAlgorithm.java,v 1.1 2007/10/10 18:52:17 vauchers Exp $
 *
 */
 
package com.jrefinery.chart.data;

import java.util.Vector;
import com.jrefinery.chart.*;

/**
 * A linear plot fit algorithm contributed by Matthew Wright.
 */
public class LinearPlotFitAlgorithm implements PlotFitAlgorithm {

  private XYDataSource datasource;
  
  private double[][] linear_fit;

  /** @return the name that you want to see in the legend. */
  public String getName() { return "Linear Fit"; }

  /**
   * @param ds the XYDataSource for this PlotFit 
   */
  public void setXYDataSource(XYDataSource ds) {
    this.datasource = datasource;

    /*
     *	build the x and y data arrays to be passed to the
     *	statistics class to get a linear fit and store them
     *	for each dataset in the datasets Vector
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
    
    // put in the linear fit array
    linear_fit = new double[datasets.size()][2];
    for(int i = 0; i < datasets.size(); i++) {
      Vector pair = (Vector)datasets.elementAt(i);
      linear_fit[i] = Statistics.getLinearFit((Number[])pair.elementAt(0), (Number[])pair.elementAt(1));
    }
  }

  /** for a given x, must return a y
   *	@param x the x value
   *	@param i the series
   *	@return the y value	
   */
  public Number getY(int i, Number x) {
   /*
    *	for a linear fit, this will return the y for the formula
    *			y = a + bx
    *	These are in the private variable linear_fit
    * 		a = linear_fit[i][0]
    *		b = linear_fit[i][1]
    */
    return new Double(linear_fit[i][0] + linear_fit[i][1] * x.doubleValue());

  }

}