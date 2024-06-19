/* =======================================
 * JFreeChart : a Java Chart Class Library
 * =======================================
 * Version:         0.5.6;
 * Project Lead:    David Gilbert (david.gilbert@bigfoot.com);
 *
 * File:            DefaultCategoryDataSource.java
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
 * $Id: DefaultCategoryDataSource.java,v 1.1 2007/10/10 18:52:16 vauchers Exp $
 */

package com.jrefinery.chart;

import java.util.*;

/**
 * A convenience class that provides a default implementation of the CategoryDataSource interface.
 * The standard constructor accepts data in a two dimensional array where the first dimension is
 * the series, and the second dimension is the category.
 */
public class DefaultCategoryDataSource extends AbstractDataSource
                                       implements CategoryDataSource {

  /** A list of series names. */
  protected List seriesNames;

  /** A list of data categories. */
  protected List categories;

  /** A list of Lists containing the data for each series. */
  protected List seriesData;

  /**
   * Default constructor: builds an empty DefaultCategoryDataSource.
   */
  public DefaultCategoryDataSource() {
    seriesNames = new ArrayList();
    categories = new ArrayList();
    seriesData = new ArrayList();
  }

  /**
   * Standard constructor: builds a DefaultCategoryDataSource with the specified data.  Series and
   * category names are automatically generated.
   */
  public DefaultCategoryDataSource(Number[][] data) {
    this(seriesNameListFromDataArray(data), categoryNameListFromDataArray(data), data);
  }

  /**
   * Standard constructor: builds a DefaultCategoryDataSource with the specified series names and
   * data.  Category names are generated automatically ("Category 1", "Category 2", etc).
   */
  public DefaultCategoryDataSource(String[] seriesNames, Number[][] data) {
    this(Arrays.asList(seriesNames), data);
  }

  /**
   * Standard constructor: builds a DefaultCategoryDataSource with the specified series names and
   * data.  Category names are generated automatically ("Category 1", "Category 2", etc).
   */
  public DefaultCategoryDataSource(List seriesNames, Number[][] data) {
    this(seriesNames, categoryNameListFromDataArray(data), data);
  }

  /**
   * Full constructor: builds a DefaultCategoryDataSource with the specified series names,
   * categories and data.
   */
  public DefaultCategoryDataSource(String[] seriesNames, Object[] categories, Number[][] data) {
    this(Arrays.asList(seriesNames), Arrays.asList(categories), data);
  }

  /**
   * Full constructor: builds a DefaultCategoryDataSource with the specified series names,
   * categories and data.
   */
  public DefaultCategoryDataSource(List seriesNames, List categories, Number[][] data) {
    this.seriesNames = seriesNames;
    this.categories = categories;

    // check that the size of the data array matches the seriesNames and categories
    int seriesCount = data.length;
    int categoryCount = data[0].length;

    seriesData = new ArrayList(seriesCount);
    for (int seriesIndex=0; seriesIndex<seriesCount; seriesIndex++) {
      List dataList = new ArrayList(categoryCount);
      for (int categoryIndex=0; categoryIndex<categoryCount; categoryIndex++) {
        dataList.add(data[seriesIndex][categoryIndex]);
      }
      seriesData.add(seriesIndex, dataList);
    }

  }

  /**
   * Returns the number of series.
   */
  public int getSeriesCount() {
    return seriesData.size();
  }

  /**
   * Returns the name of the specified series.
   * @param seriesIndex The index of the required series (zero-based).
   */
  public String getSeriesName(int seriesIndex) {
    return seriesNames.get(seriesIndex).toString();
  }

  /**
   * Sets the names of the series in the data source.
   */
  public void setSeriesNames(String[] seriesNames) {
    this.seriesNames = Arrays.asList(seriesNames);
    fireDataSourceChanged();
  }

  /**
   * Returns the number of categories in the data source.  Supports the CategoryDataSource
   * interface.
   */
  public int getCategoryCount() {
    return categories.size();
  }

  /**
   * Returns a list of the categories in the data source.  Supports the CategoryDataSource
   * interface.
   */
  public List getCategories() {
    return categories;
  }

  /**
   *
   */
  public void setCategories(List categories) {
    this.categories = categories;
    fireDataSourceChanged();
  }

  /**
   *
   */
  public void setCategories(Object[] categories) {
    setCategories(Arrays.asList(categories));
  }

  /**
   * Returns the data value for the specified series (zero-based index) and category.  Supports the
   * CategoryDataSource interface.
   */
  public Number getValue(int seriesIndex, Object category) {
    List data = (List)seriesData.get(seriesIndex);
    int categoryIndex = categories.indexOf(category);
    return (Number)data.get(categoryIndex);
  }

  /**
   * A utility method that returns a list of series names ("Series 1", "Series 2" etc.).  The
   * number of series matches the size of the data array.  String objects are used to
   * represent the series names.  This method is called by constructors that do not specify
   * series names.
   */
  public static List seriesNameListFromDataArray(Number[][] data) {

    int seriesCount = data.length;
    List seriesNameList = new ArrayList(seriesCount);
    for (int i=0; i<seriesCount; i++) {
      seriesNameList.add("Series "+(i+1));
    }
    return seriesNameList;

  }

  /**
   * A utility method that returns a list of categories ("Category 1", "Category 2" etc.).  The
   * number of categories matches the size of the data array.  String objects are used to
   * represent the categories.  This method is called by constructors that do not specify
   * category names.
   */
  public static List categoryNameListFromDataArray(Number[][] data) {

    int categoryCount = data[0].length;
    List categoryNameList = new ArrayList(categoryCount);
    for (int i=0; i<categoryCount; i++) {
      categoryNameList.add("Category "+(i+1));
    }
    return categoryNameList;

  }

}