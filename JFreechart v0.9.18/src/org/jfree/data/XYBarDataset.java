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
 * -----------------
 * XYBarDataset.java
 * -----------------
 * (C) Copyright 2004, by Object Refinery Limited and Contributors.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited);
 * Contributor(s):   -;
 *
 * $Id: XYBarDataset.java,v 1.1 2007/10/10 19:39:25 vauchers Exp $
 *
 * Changes
 * -------
 * 02-Mar-2004 : Version 1 (DG);
 *
 */

package org.jfree.data;

/**
 * A dataset wrapper class that converts a standard {@link XYDataset} into an
 * {@link IntervalXYDataset} suitable for use in creating XY bar charts.
 */
public class XYBarDataset extends AbstractDataset
                          implements IntervalXYDataset, DatasetChangeListener {
    
    /** The underlying dataset. */
    private XYDataset underlying;
    
    /** The bar width. */
    private double barWidth;
    
    /**
     * Creates a new dataset.
     * 
     * @param underlying  the underlying dataset.
     * @param barWidth  the width of the bars.
     */
    public XYBarDataset(XYDataset underlying, double barWidth) {
        this.underlying = underlying;   
        this.underlying.addChangeListener(this);
        this.barWidth = barWidth;
    }

    /**
     * Returns the number of series in the dataset.
     *
     * @return the series count.
     */
    public int getSeriesCount() {
        return this.underlying.getSeriesCount();   
    }

    /**
     * Returns the name of a series.
     *
     * @param series  the series (zero-based index).
     *
     * @return the name of the series.
     */
    public String getSeriesName(int series) {
        return this.underlying.getSeriesName(series);   
    }
    
    /**
     * Returns the number of items in a series.
     *
     * @param series  the series (zero-based index).
     *
     * @return the number of items within the series.
     */
    public int getItemCount(int series) {
        return this.underlying.getItemCount(series);   
    }

    /**
     * Returns the x-value for an item within a series.  The x-values may or may not be returned
     * in ascending order, that is up to the class implementing the interface.
     *
     * @param series  the series (zero-based index).
     * @param item  the item (zero-based index).
     *
     * @return the x-value.
     */
    public Number getXValue(int series, int item) {
        return this.underlying.getXValue(series, item);   
    }

    /**
     * Returns the y-value for an item within a series.
     *
     * @param series  the series (zero-based index).
     * @param item  the item (zero-based index).
     *
     * @return the y-value (possibly <code>null</code>).
     */
    public Number getYValue(int series, int item) {
        return this.underlying.getYValue(series, item);   
    }

    /**
     * Returns the starting X value for the specified series and item.
     *
     * @param series  the series (zero-based index).
     * @param item  the item within a series (zero-based index).
     *
     * @return the starting X value for the specified series and item.
     */
    public Number getStartXValue(int series, int item) {
        Number result = null;
        Number xnum = this.underlying.getXValue(series, item);
        if (xnum != null) {
             result = new Double(xnum.doubleValue() - this.barWidth / 2.0);   
        }
        return result;   
    }

    /**
     * Returns the ending X value for the specified series and item.
     *
     * @param series  the series (zero-based index).
     * @param item  the item within a series (zero-based index).
     *
     * @return the ending X value for the specified series and item.
     */
    public Number getEndXValue(int series, int item) {
        Number result = null;
        Number xnum = this.underlying.getXValue(series, item);
        if (xnum != null) {
             result = new Double(xnum.doubleValue() + this.barWidth / 2.0);   
        }
        return result;   
    }

    /**
     * Returns the starting Y value for the specified series and item.
     *
     * @param series  the series (zero-based index).
     * @param item  the item within a series (zero-based index).
     *
     * @return starting Y value for the specified series and item.
     */
    public Number getStartYValue(int series, int item) {
        return this.underlying.getYValue(series, item);   
    }

    /**
     * Returns the ending Y value for the specified series and item.
     *
     * @param series  the series (zero-based index).
     * @param item  the item within a series (zero-based index).
     *
     * @return the ending Y value for the specified series and item.
     */
    public Number getEndYValue(int series, int item) {
        return this.underlying.getYValue(series, item);   
    }

    /**
     * Receives notification of an dataset change event.
     *
     * @param event  information about the event.
     */
    public void datasetChanged(DatasetChangeEvent event) {
        this.notifyListeners(event);
    }

}
