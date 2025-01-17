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
 * ----------------------------
 * IntervalCategoryDataset.java
 * ----------------------------
 * (C) Copyright 2002, 2003, by Eduard Martinescu and Contributors.
 *
 * Original Author:  Eduard Martinescu;
 * Contributor(s):   David Gilbert (for Simba Management Limited);
 *
 * $Id: IntervalCategoryDataset.java,v 1.1 2007/10/10 19:54:14 vauchers Exp $
 *
 * Changes
 * -------
 * 19-Mar-2002 : Version 1 contributed by Eduard Martinescu.  The interface name and method names
 *               have been renamed to be consistent with existing interfaces (DG);
 * 06-Jun-2002 : Updated Javadoc comments (DG);
 * 24-Oct-2002 : Categories and series are now indexed by int or Comparable, following changes
 *               made to the CategoryDataset interface (DG);
 *
 */

package com.jrefinery.data;

/**
 * A category dataset that defines a value range for each series/category combination.
 *
 * @author Eduard Martinescu
 */
public interface IntervalCategoryDataset extends CategoryDataset {

    /**
     * Returns the start value for the interval for a given series and category.
     *
     * @param series  the series (zero-based index).
     * @param category  the category (zero-based index).
     *
     * @return the start value (possibly null).
     */
    public Number getStartValue(int series, int category);

    /**
     * Returns the start value for the interval for a given series and category.
     *
     * @param series  the series key.
     * @param category  the category key.
     *
     * @return the start value (possibly null).
     */
    public Number getStartValue(Comparable series, Comparable category);

    /**
     * Returns the end value for the interval for a given series and category.
     *
     * @param series  the series (zero-based index).
     * @param category  the category (zero-based index).
     *
     * @return the end value (possibly null).
     */
    public Number getEndValue(int series, int category);

    /**
     * Returns the end value for the interval for a given series and category.
     *
     * @param series  the series key.
     * @param category  the category key.
     *
     * @return the end value (possibly null).
     */
    public Number getEndValue(Comparable series, Comparable category);

}
