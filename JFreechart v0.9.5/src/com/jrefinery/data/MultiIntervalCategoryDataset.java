/* ======================================
 * JFreeChart : a free Java chart library
 * ======================================
 *
 * Project Info:  http://www.object-refinery.com/jfreechart/index.html
 * Project Lead:  David Gilbert (david.gilbert@object-refinery.com);
 *
 * (C) Copyright 2000-2003, by Simba Management Limited and Contributors.
 *
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation;
 * either version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this
 * library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330,
 * Boston, MA 02111-1307, USA.
 *
 * ------------------------------
 * MultiIntervalTableDataset.java
 * ------------------------------
 * (C) Copyright 2002, 2003 by Simba Management Limited.
 *
 * Original Author:  David Gilbert (for Simba Management Limited);
 * Contributor(s):   -;
 *
 * $Id: MultiIntervalCategoryDataset.java,v 1.1 2007/10/10 19:54:14 vauchers Exp $
 *
 * Changes
 * -------
 * 10-Jan-2003 : Version 1 (DG);
 *
 */

 package com.jrefinery.data;

/**
 * An extension of the {@link IntervalCategoryDataset} interface that adds support for multiple
 * sub-intervals.
 *
 * @author David Gilbert
 *
 */
public interface MultiIntervalCategoryDataset extends IntervalCategoryDataset {

    /**
     * Returns the number of sub-intervals for a given item.
     *
     * @param row  the row index (zero-based).
     * @param column  the column index (zero-based).
     *
     * @return the sub-interval count.
     */
    public int getSubIntervalCount(int row, int column);

    /**
     * Returns the number of sub-intervals for a given item.
     *
     * @param rowKey  the row key.
     * @param columnKey  the column key.
     *
     * @return the sub-interval count.
     */
    public int getSubIntervalCount(Comparable rowKey, Comparable columnKey);

    /**
     * Returns the start value of a sub-interval for a given item.
     *
     * @param row  the row index (zero-based).
     * @param column  the column index (zero-based).
     * @param subinterval  the sub-interval index (zero-based).
     *
     * @return the start value (possibly <code>null</code>).
     */
    public Number getStartValue(int row, int column, int subinterval);

    /**
     * Returns the start value of a sub-interval for a given item.
     *
     * @param rowKey  the row key.
     * @param columnKey  the column key.
     * @param subinterval  the sub-interval.
     *
     * @return the start value (possibly <code>null</code>).
     */
    public Number getStartValue(Comparable rowKey, Comparable columnKey, int subinterval);

    /**
     * Returns the end value of a sub-interval for a given item.
     *
     * @param row  the row index (zero-based).
     * @param column  the column index (zero-based).
     * @param subinterval  the sub-interval.
     *
     * @return the end value (possibly <code>null</code>).
     */
    public Number getEndValue(int row, int column, int subinterval);

    /**
     * Returns the end value of a sub-interval for a given item.
     *
     * @param rowKey  the row key.
     * @param columnKey  the column key.
     * @param subinterval  the sub-interval.
     *
     * @return the end value (possibly <code>null</code>).
     */
    public Number getEndValue(Comparable rowKey, Comparable columnKey, int subinterval);

}
