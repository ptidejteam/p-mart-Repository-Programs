/* ===========================================================
 * JFreeChart : a free chart library for the Java(tm) platform
 * ===========================================================
 *
 * (C) Copyright 2000-2004, by Object Refinery Limited and Contributors.
 *
 * Project Info:  http://www.jfree.org/jfreechart/index.html
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
 * [Java is a trademark or registered trademark of Sun Microsystems, Inc. 
 * in the United States and other countries.]
 *
 * ---------------------
 * SampleXYZDataset.java
 * ---------------------
 * (C) Copyright 2003, 2004, by Object Refinery Limited and Contributors.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited);
 * Contributor(s):   -;
 *
 * $Id: SampleXYZDataset.java,v 1.1 2007/10/10 19:25:26 vauchers Exp $
 *
 * Changes
 * -------
 * 28-Jan-2003 : Version 1 (DG);
 *
 */

package org.jfree.chart.demo;

import org.jfree.data.AbstractSeriesDataset;
import org.jfree.data.XYZDataset;

/**
 * A quick-and-dirty implementation of the {@link XYZDataset interface}.  Hard-coded and not useful
 * beyond the demo.
 *
 * @author David Gilbert
 */
public class SampleXYZDataset extends AbstractSeriesDataset implements XYZDataset {

    /** The x values. */
    private double[] xVal = {2.1, 2.375625, 2.375625, 2.232928726, 2.232928726, 1.860415253,
                             1.840842668, 1.905415253, 2.336029412, 3.8};

    /** The y values. */
    private double[] yVal = {14.168, 11.156, 10.089, 8.884, 8.719, 8.466, 5.489,
                             4.107, 4.101, 25};

    /** The z values. */
    private double[] zVal = {2.45, 2.791285714, 2.791285714, 2.2125, 2.2125, 2.22, 2.1, 2.22,
                             1.64875, 4};

    /**
     * Returns the number of series in the dataset.
     *
     * @return the series count.
     */
    public int getSeriesCount() {
        return 1;
    }

    /**
     * Returns the name of a series.
     *
     * @param series  the series (zero-based index).
     *
     * @return the name of the series.
     */
    public String getSeriesName(int series) {
        return "Series 1";
    }

    /**
     * Returns the number of items in a series.
     *
     * @param series  the series (zero-based index).
     *
     * @return the number of items within the series.
     */
    public int getItemCount(int series) {
        return xVal.length;
    }

    /**
     * Returns the x-value for an item within a series.
     * <P>
     * The implementation is responsible for ensuring that the x-values are
     * presented in ascending order.
     *
     * @param series  the series (zero-based index).
     * @param item  the item (zero-based index).
     *
     * @return the x-value.
     */
    public Number getXValue(int series, int item) {
        return new Double(xVal[item]);
    }

    /**
     * Returns the y-value for an item within a series.
     *
     * @param series  the series (zero-based index).
     * @param item  the item (zero-based index).
     *
     * @return the y-value.
     */
    public Number getYValue(int series, int item) {
        return new Double(yVal[item]);
    }

    /**
     * Returns the z-value for the specified series and item.
     *
     * @param series  the series (zero-based index).
     * @param item  the item (zero-based index).
     *
     * @return the z-value for the specified series and item.
     */
    public Number getZValue(int series, int item) {
        return new Double(zVal[item]);
    }

}
