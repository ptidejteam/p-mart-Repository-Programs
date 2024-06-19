/* =======================================
 * JFreeChart : a Java Chart Class Library
 * =======================================
 * Version:         0.5.6;
 * Project Lead:    David Gilbert (david.gilbert@bigfoot.com);
 *
 * File:            CategoryDataSource.java
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
 * $Id: CategoryDataSource.java,v 1.1 2007/10/10 18:52:16 vauchers Exp $
 */

package com.jrefinery.chart;

import java.util.*;

/**
 * The interface through which JFreeChart obtains categorical data - used for bar charts and line
 * charts;
 * <P>
 * The categories are represented by any Java Object, with the category label being provided by
 * the toString() method.
 */
public interface CategoryDataSource extends DataSource {

  /**
   * Returns the value for the specified series (zero-based index) and category.
   * @param series The series index (zero-based);
   * @param category The category;
   */
  public Number getValue(int seriesIndex, Object category);

  /**
   * Returns a list of the categories in the data source.
   */
  public List getCategories();

  /**
   * Returns the number of categories in the data source.
   */
  public int getCategoryCount();

}