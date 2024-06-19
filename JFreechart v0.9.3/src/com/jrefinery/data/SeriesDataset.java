/* ============================================
 * JFreeChart : a free Java chart class library
 * ============================================
 *
 * Project Info:  http://www.object-refinery.com/jfreechart/index.html
 * Project Lead:  David Gilbert (david.gilbert@object-refinery.com);
 *
 * (C) Copyright 2000-2002, by Simba Management Limited and Contributors.
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
 * ------------------
 * SeriesDataset.java
 * ------------------
 * (C) Copyright 2000-2002, by Simba Management Limited.
 *
 * Original Author:  David Gilbert (for Simba Management Limited);
 * Contributor(s):   -;
 *
 * $Id: SeriesDataset.java,v 1.1 2007/10/10 19:52:14 vauchers Exp $
 *
 * Changes
 * -------
 * 17-Nov-2001 : Version 1 (DG);
 *
 */

package com.jrefinery.data;

/**
 * The interface for a dataset consisting of one or many series of data.
 * @see CategoryDataset
 * @see IntervalXYDataset
 * @see IntervalXYZDataset
 * @see XYDataset
 * @see XYZDataset
 */
public interface SeriesDataset extends Dataset {

    /**
     * Returns the number of series in the dataset.
     * @return The number of series in the dataset.
     */
    public int getSeriesCount();

    /**
     * Returns the name of a series.
     * @param series    The series (zero-based index).
     * @return the name of the series.
     */
    public String getSeriesName(int series);

}
