/* =======================================
 * JFreeChart : a Java Chart Class Library
 * =======================================
 * Version:         0.5.6;
 * Project Lead:    David Gilbert (david.gilbert@bigfoot.com);
 *
 * File:            XYDataSource.java
 * Author:          David Gilbert;
 * Contributor(s):  -;
 *
 * (C) Copyright 2000, Simba Management Limited;
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
 * $Id: XYDataSource.java,v 1.1 2007/10/10 18:52:16 vauchers Exp $
 */

package com.jrefinery.chart;

/**
 * The interface through which JFreeChart obtains data in the form of (x, y) pairs - used for
 * XY plots.
 */
public interface XYDataSource extends DataSource {

  /**
   * Returns the x-value for the specified series and item.  The implementation is responsible for
   * ensuring that the x-values are presented in ascending order.
   * @param seriesIndex The index of the series of interest (zero-based);
   * @param itemIndex The index of the item of interest (zero-based).
   */
  public Number getXValue(int seriesIndex, int itemIndex);

  /**
   * Returns the y-value for the specified series and item.
   * @param seriesIndex The index of the series of interest (zero-based);
   * @param itemIndex The index of the item of interest (zero-based).
   */
  public Number getYValue(int seriesIndex, int itemIndex);

  /**
   * Returns the number of items in the specified series.
   * @param seriesIndex The index of the series of interest (zero-based).
   */
  public int getItemCount(int seriesIndex);

}
