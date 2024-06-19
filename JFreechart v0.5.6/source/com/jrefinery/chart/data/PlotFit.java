/* =======================================
 * JFreeChart : a Java Chart Class Library
 * =======================================
 * Version:         0.5.6;
 * Project Lead:    David Gilbert (david.gilbert@bigfoot.com);
 *
 * File:            PlotFit.java
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
 * $Id: PlotFit.java,v 1.1 2007/10/10 18:52:17 vauchers Exp $
 *
 */

package com.jrefinery.chart.data;


import com.jrefinery.chart.*;
import com.jrefinery.chart.ui.*;
import com.jrefinery.util.ui.*;

public class PlotFit {

  protected XYDataSource datasource;

  // just one algorithm per PlotFit right now but can change it
  protected PlotFitAlgorithm alg;

  public PlotFit(XYDataSource datasource, PlotFitAlgorithm alg) {
    this.datasource = datasource;
    this.alg = alg;
  }

  public void setXYDataSource(XYDataSource datasource) {
    this.datasource = datasource;
  }

  public void setPlotFitAlgorithm(PlotFitAlgorithm alg) {
    this.alg = alg;
  }

  /** implements what I'm doing in code now... not the best
   *	way to do this?
   */
  public Object[][][] getResults() {
    /* set up our algorithm */
    alg.setXYDataSource(datasource);

    /* make a data container big enough to hold it all */
    int arraysize = 0;
    int seriescount = datasource.getSeriesCount();
    for(int i = 0; i < seriescount; i++) {
      if(datasource.getItemCount(i) > arraysize) {
        arraysize = datasource.getItemCount(i);
      }
    }
    // we'll apply the plot fit to all of the series for now
    Object[][][] newdata = new Object[seriescount * 2][arraysize][2];
    /* copy in the series to the first half */
    for(int i = 0; i < seriescount; i++) {
      for(int j = 0; j < datasource.getItemCount(i); j++) {
        Number x = datasource.getXValue(i,j);
        newdata[i][j][0] = x;
        newdata[i][j][1] = datasource.getYValue(i,j);
        Number y = alg.getY(i, x);
        /*
         * only want to set data for non-null algorithm fits.
         * This allows things like moving average plots, or partial
         * plots to return null and not get NPEs when the chart is
         * created
         */
        //System.out.println("At [" + i + "," + j + "] the values are [" + x + "," + y + "]");
        if(y != null) {
          newdata[i + seriescount][j][0] = x;
          newdata[i + seriescount][j][1] = y;
        }
        else {
          newdata[i + seriescount][j][0] = null;
          newdata[i + seriescount][j][1] = null;
	}
      }
    }
    return newdata;
  }

  public XYDataSource getFit() {
    int seriescount = datasource.getSeriesCount();
    String[] seriesnames = new String[seriescount * 2];
    for(int i = 0; i < seriescount; i++) {
      seriesnames[i] = datasource.getSeriesName(i);
      seriesnames[i + seriescount] = datasource.getSeriesName(i) + " " + alg.getName();
    }

    return new DefaultXYDataSource(seriesnames, getResults());
  }

}