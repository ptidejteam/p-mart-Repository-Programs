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
 * -----------------------------
 * SimpleIntervalXYDataset2.java
 * -----------------------------
 * (C) Copyright 2003, 2004, by Object Refinery Limited and Contributors.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited);
 * Contributor(s):   -;
 *
 * $Id: SimpleIntervalXYDataset2.java,v 1.1 2007/10/10 19:29:07 vauchers Exp $
 *
 * Changes
 * -------
 * 27-jUN-2003 : Version 1 (DG);
 *
 */

package org.jfree.chart.demo;

import org.jfree.data.AbstractDataset;
import org.jfree.data.DatasetChangeListener;
import org.jfree.data.IntervalXYDataset;

/**
 * A quick and dirty sample dataset.
 *
 * @author David Gilbert
 */
public class SimpleIntervalXYDataset2 extends AbstractDataset implements IntervalXYDataset {

    /** The start values. */
    private Double[] yStart;
    
    /** The end values. */
    private Double[] yEnd = new Double[3];

    /** The x values. */
    private Double[] x = new Double[3];

    /**
     * Creates a new dataset.
     * 
     * @param itemCount  the number of items to generate.
     */
    public SimpleIntervalXYDataset2(int itemCount) {

        this.x = new Double[itemCount];
        this.yStart = new Double[itemCount];
        this.yEnd = new Double[itemCount];
        
        double base = 100;
        for (int i = 1; i <= itemCount; i++) {
            this.x[i - 1] = new Double(i);
            base = base * (1 + (Math.random() / 10 - 0.05));
            this.yStart[i - 1] = new Double(base);
            this.yEnd[i - 1] = new Double(this.yStart[i - 1].doubleValue() + Math.random() * 30);
        }
    }

    /**
     * Returns the number of series in the dataset.
     *
     * @return the number of series in the dataset.
     */
    public int getSeriesCount() {
        return 1;
    }

    /**
     * Returns the name of a series.
     *
     * @param series the series (zero-based index).
     *
     * @return the series name.
     */
    public String getSeriesName(int series) {
        return "Series 1";
    }

    /**
     * Returns the number of items in a series.
     *
     * @param series the series (zero-based index).
     *
     * @return the number of items within a series.
     */
    public int getItemCount(int series) {
        return this.x.length;
    }

    /**
     * Returns the x-value for an item within a series.
     * <P>
     * The implementation is responsible for ensuring that the x-values are presented in ascending
     * order.
     *
     * @param series  the series (zero-based index).
     * @param item  the item (zero-based index).
     *
     * @return  the x-value for an item within a series.
     */
    public Number getXValue(int series, int item) {
        return this.x[item];
    }

    /**
     * Returns the y-value for an item within a series.
     *
     * @param series  the series (zero-based index).
     * @param item  the item (zero-based index).
     *
     * @return the y-value for an item within a series.
     */
    public Number getYValue(int series, int item) {
        return this.yEnd[item];
    }

    /**
     * Returns the starting X value for the specified series and item.
     *
     * @param series  the series (zero-based index).
     * @param item  the item within a series (zero-based index).
     *
     * @return the start x value.
     */
    public Number getStartXValue(int series, int item) {
        return this.x[item];
    }

    /**
     * Returns the ending X value for the specified series and item.
     *
     * @param series  the series (zero-based index).
     * @param item  the item within a series (zero-based index).
     *
     * @return the end x value.
     */
    public Number getEndXValue(int series, int item) {
        return this.x[item];
    }

    /**
     * Returns the starting Y value for the specified series and item.
     *
     * @param series  the series (zero-based index).
     * @param item  the item within a series (zero-based index).
     *
     * @return the start y value.
     */
    public Number getStartYValue(int series, int item) {
        return this.yStart[item];
    }

    /**
     * Returns the ending Y value for the specified series and item.
     *
     * @param series  the series (zero-based index).
     * @param item  the item within a series (zero-based index).
     *
     * @return the end y value.
     */
    public Number getEndYValue(int series, int item) {
        return this.yEnd[item];
    }

    /**
     * Registers an object for notification of changes to the dataset.
     *
     * @param listener  the object to register.
     */
    public void addChangeListener(DatasetChangeListener listener) {
        // ignored
    }

    /**
     * Deregisters an object for notification of changes to the dataset.
     *
     * @param listener  the object to deregister.
     */
    public void removeChangeListener(DatasetChangeListener listener) {
        // ignored
    }

}
