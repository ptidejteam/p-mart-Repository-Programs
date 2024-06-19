/* =======================================
 * JFreeChart : a Java Chart Class Library
 * =======================================
 * Version:         0.5.6;
 * Project Lead:    David Gilbert (david.gilbert@bigfoot.com);
 *
 * File:            DefaultXYDataSource.java
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
 * $Id: DefaultXYDataSource.java,v 1.1 2007/10/10 18:52:16 vauchers Exp $
 */

package com.jrefinery.chart;

import java.util.*;

/**
 * A convenience class that provides a default implementation of the XYDataSource interface.
 * The standard constructor accepts data in a two dimensional array where the first dimension is
 * the series, and the second dimension is the category.
 */
public class DefaultXYDataSource extends AbstractDataSource
                                 implements XYDataSource {

  /** A list of series names. */
  protected List seriesNames;

  /** A list of Lists containing the data for each series. */
  protected List allSeriesData;

  /**
   * Default constructor - builds an empty DefaultXYDataSource.
   */
  public DefaultXYDataSource() {
    seriesNames = new ArrayList();
    allSeriesData = new ArrayList();
  }

  /**
   * Standard constructor - builds a DefaultXYDataSource with the specified data.  The dimensions
   * of the data array are [series][item][x=0, y=1]. The x-values should be Number or Date
   * objects, the y-values should be Number objects.  Any other types are interpreted as zero. The
   * data will be sorted so that the x-values are ascending.
   */
  public DefaultXYDataSource(Object[][][] data) {
    this(seriesNameListFromDataArray(data), data);
  }

  /**
   * Standard constructor - builds a DefaultXYDataSource with the specified data and series
   * names.
   */
  public DefaultXYDataSource(String[] seriesNames, Object[][][] data) {
    this(Arrays.asList(seriesNames), data);
  }

  /**
   * Standard constructor - builds a DefaultXYDataSource with the specified data and series
   * names.
   */
  public DefaultXYDataSource(List seriesNames, Object[][][] data) {

    this.seriesNames = seriesNames;

    int seriesCount = data.length;

    allSeriesData = new ArrayList(seriesCount);

    for (int seriesIndex=0; seriesIndex<seriesCount; seriesIndex++) {
      List oneSeriesData = new ArrayList();
      int maxItemCount = data[seriesIndex].length;
      for (int itemIndex=0; itemIndex<maxItemCount; itemIndex++) {
        Object xObject = data[seriesIndex][itemIndex][0];
        if (xObject!=null) {
          Number xNumber = null;
          if (xObject instanceof Number) {
            xNumber = (Number)xObject;
          }
          else if (xObject instanceof Date) {
            Date xDate = (Date)xObject;
            xNumber = new Long(xDate.getTime());
          }
          else xNumber = new Integer(0);
          Number yNumber = (Number)data[seriesIndex][itemIndex][1];
          oneSeriesData.add(new XYDataItem(xNumber, yNumber));
        }
      }
      Collections.sort(oneSeriesData);
      allSeriesData.add(seriesIndex, oneSeriesData);
    }

  }

  /**
   * Returns the number of series.
   */
  public int getSeriesCount() {
    return allSeriesData.size();
  }

  /**
   * Returns the number of series.
   */
  public int getItemCount(int seriesIndex) {
    List oneSeriesData = (List)allSeriesData.get(seriesIndex);
    return oneSeriesData.size();
  }

  /**
   * Returns the name of the specified series.
   * @param seriesIndex The index of the required series (zero-based).
   */
  public String getSeriesName(int seriesIndex) {
    return seriesNames.get(seriesIndex).toString();
  }

  /**
   * Returns the data value for the specified series (zero-based index) and category.  Supports the
   * CategoryDataSource interface.
   */
  public Number getXValue(int seriesIndex, int itemIndex) {
    List oneSeriesData = (List)allSeriesData.get(seriesIndex);
    XYDataItem item = (XYDataItem)oneSeriesData.get(itemIndex);
    return item.x;
  }

  /**
   *
   */
  public Number getYValue(int seriesIndex, int itemIndex) {
    List oneSeriesData = (List)allSeriesData.get(seriesIndex);
    XYDataItem item = (XYDataItem)oneSeriesData.get(itemIndex);
    return item.y;
  }

  /**
   * Returns a List of String objects that can be used as series names.
   */
  public static List seriesNameListFromDataArray(Object[][] data) {

    int seriesCount = data.length;
    List seriesNameList = new ArrayList(seriesCount);
    for (int i=0; i<seriesCount; i++) {
      seriesNameList.add("Series "+(i+1));
    }
    return seriesNameList;

  }

}

class XYDataItem implements Comparable {

  public Number x;
  public Number y;

  public XYDataItem(Number x, Number y) {
    this.x = x;
    this.y = y;
  }

  public int compareTo(Object object) {
    if (object instanceof XYDataItem) {
      XYDataItem item = (XYDataItem)object;
      if (this.x.doubleValue()>item.x.doubleValue()) {
        return 1;
      }
      else if (this.x.equals(item.x)) {
        return 0;
      }
      else return -1;
    }
    else throw new ClassCastException("XYDataItem.compareTo(error)");
  }

}