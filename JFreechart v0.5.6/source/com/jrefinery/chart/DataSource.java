/* =======================================
 * JFreeChart : a Java Chart Class Library
 * =======================================
 * Version:         0.5.6;
 * Project Lead:    David Gilbert (david.gilbert@bigfoot.com);
 *
 * File:            DataSource.java
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
 * $Id: DataSource.java,v 1.1 2007/10/10 18:52:17 vauchers Exp $
 */

package com.jrefinery.chart;

import com.jrefinery.chart.event.*;

/**
 * The base interface for data sources used by JFreeChart;
 * @see CategoryDataSource
 * @see XYDataSource
 */
public interface DataSource {

  /**
   * Returns the number of series in the data source;
   */
  public int getSeriesCount();

  /**
   * Returns the name of the specified series.
   * @param seriesIndex The index of the required series (zero-based);
   */
  public String getSeriesName(int seriesIndex);

  /**
   * Registers an object for notification of changes to the data source.
   * @param listener The object being registered;
   */
  public void addChangeListener(DataSourceChangeListener listener);

  /**
   * Unregisters an object for notification of changes to the data source.
   * @param listener The object being unregistered;
   */
  public void removeChangeListener(DataSourceChangeListener listener);

}
