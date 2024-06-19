/* ======================================
 * JFreeChart : a free Java chart library
 * ======================================
 *
 * Project Info:  http://www.jfree.org/jfreechart/index.html
 * Project Lead:  David Gilbert (david.gilbert@object-refinery.com);
 *
 * (C) Copyright 2000-2003, by Object Refinery Limited and Contributors.
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
 * -------------------------
 * BoxAndWhiskerDataset.java
 * -------------------------
 * (C) Copyright 2003, by David Browning and Contributors.
 *
 * Original Author:  David Browning;
 * Contributor(s):   -;
 *
 * $Id: BoxAndWhiskerDataset.java,v 1.1 2007/10/10 19:09:12 vauchers Exp $
 *
 * Changes
 * -------
 * 05-Aug-2003 : Version 1, contributed by David Browning (DG);
 *
 */

package org.jfree.data;

/**
 * An interface that defines data in the form of (x, max, min, average, median) tuples.
 * <P>
 * Example: JFreeChart uses this interface to obtain data for AIMS max-min-average-median plots.
 *
 * @author David Browning
 */
public interface BoxAndWhiskerDataset extends XYDataset {

    /**
     * Returns the max-value for the specified series and item.
     *
     * @param series  the series (zero-based index).
     * @param item  the item (zero-based index).
     *
     * @return the max-value for the specified series and item.
     */
    public Number getMaxValue(int series, int item);

    /**
     * Returns the min-value for the specified series and item.
     *
     * @param series  the series (zero-based index).
     * @param item  the item (zero-based index).
     *
     * @return the min-value for the specified series and item.
     */
    public Number getMinValue(int series, int item);

    /**
     * Returns the average-value for the specified series and item.
     *
     * @param series  the series (zero-based index).
     * @param item  the item (zero-based index).
     *
     * @return the average-value for the specified series and item.
     */
    public Number getAverageValue(int series, int item);

    /**
     * Returns the median-value for the specified series and item.
     *
     * @param series  the series (zero-based index).
     * @param item  the item (zero-based index).
     *
     * @return the median-value for the specified series and item.
     */
    public Number getMedianValue(int series, int item);

    /**
     * Returns the Q1 median-value for the specified series and item.
     *
     * @param series  the series (zero-based index).
     * @param item  the item (zero-based index).
     *
     * @return the Q1 median-value for the specified series and item.
     */
    public Number getQ1MedianValue(int series, int item);

    /**
     * Returns the Q3 median-value for the specified series and item.
     *
     * @param series  the series (zero-based index).
     * @param item  the item (zero-based index).
     *
     * @return the Q3 median-value for the specified series and item.
     */
    public Number getQ3MedianValue(int series, int item);

    /**
     * Returns the number of replicates for the specified series and item.
     *
     * @param series  the series (zero-based index).
     * @param item  the item (zero-based index).
     *
     * @return the number of replicates for the specified series and item.
     */
    public Number getReplicateCount(int series, int item);

    /**
     * Returns an array of outliers for the specified series and item.
     *
     * @param series  the series (zero-based index).
     * @param item  the item (zero-based index).
     *
     * @return the array of outliers for the specified series and item.
     */
    public Number[] getOutliersArray(int series, int item);

    /**
     * Creates a String representing an item in the dataset.
     * 
     * @param series  the series index.
     * @param item  the item index.
     * 
     * @return A string.
     */
    public String makeString(int series, int item);

}
