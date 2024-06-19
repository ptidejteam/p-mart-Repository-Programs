/* ===============
 * JFreeChart Demo
 * ===============
 * Version:         0.5.6;
 * Project Lead:    David Gilbert (david.gilbert@bigfoot.com);
 *
 * File:            SampleXYDataSource.java
 * Author:          David Gilbert;
 * Contributor(s):  -;
 *
 * (C) Copyright 2000, Simba Management Limited;
 *
 * This program is free software; you can redistribute it and/or modify it under the terms
 * of the GNU General Public License as published by the Free Software Foundation;
 * either version 2 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program;
 * if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston,
 * MA 02111-1307, USA.
 *
 * $Id: SampleXYDataSource.java,v 1.1 2007/10/10 18:52:17 vauchers Exp $
 */

package com.jrefinery.chart.demo;

import com.jrefinery.chart.*;

/**
 * A dummy data source for an XY plot.
 * <P>
 * Note that the aim of this class is to create a self-contained data source for demo purposes -
 * it is NOT intended to show how you should go about writing your own data sources.
 */
public class SampleXYDataSource extends AbstractDataSource
                                implements XYDataSource {

  /**
   * Default constructor.
   */
  public SampleXYDataSource() {

  }

  /**
   * Returns the x-value for the specified series and item.  Series are numbered 0, 1, ...
   * @param series The index (zero-based) of the series;
   * @param item The index (zero-based) of the required item;
   * @return The x-value for the specified series and item.
   */
  public Number getXValue(int series, int item) {
    return new Double(-10.0+(item*0.2));
  }

  /**
   * Returns the y-value for the specified series and item.  Series are numbered 0, 1, ...
   * @param series The index (zero-based) of the series;
   * @param item The index (zero-based) of the required item;
   * @return The y-value for the specified series and item.
   */
  public Number getYValue(int series, int item) {
    if (series==0) {
      return new Double(Math.cos(-10.0+(item/10.0)));
    }
    else return new Double(2*(Math.sin(-10.0+(item/10.0))));
  }

  /**
   * Returns the number of series in the data source.
   * @return The number of series in the data source.
   */
  public int getSeriesCount() {
    return 2;
  }

  /**
   * Returns the name of the series.
   * @param series The index (zero-based) of the series;
   * @return The name of the series.
   */
  public String getSeriesName(int series) {
    if (series==0) {
      return "y = cosine(x)";
    }
    else if (series==1) {
      return "y = 2*sine(x)";
    }
    else return "Error";
  }

  /**
   * Returns the number of items in the specified series.
   * @param series The index (zero-based) of the series;
   * @return The number of items in the specified series.
   */
  public int getItemCount(int series) {
    return 200;
  }

}