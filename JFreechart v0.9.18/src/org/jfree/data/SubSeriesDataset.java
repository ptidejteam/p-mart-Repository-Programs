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
 * SubseriesDataset.java
 * ---------------------
 * (C) Copyright 2001-2004, by Bill Kelemen and Contributors.
 *
 * Original Author:  Bill Kelemen;
 * Contributor(s):   Sylvain Vieujot;
 *                   David Gilbert (for Object Refinery Limited);
 *
 * $Id: SubSeriesDataset.java,v 1.1 2007/10/10 19:39:25 vauchers Exp $
 *
 * Changes
 * -------
 * 06-Dec-2001 : Version 1 (BK);
 * 05-Feb-2002 : Added SignalsDataset (and small change to HighLowDataset interface) as requested
 *               by Sylvain Vieujot (DG);
 * 28-Feb-2002 : Fixed bug: missing map[series] in IntervalXYDataset and SignalsDataset
 *               methods (BK);
 * 07-Oct-2002 : Fixed errors reported by Checkstyle (DG);
 *
 */

package org.jfree.data;

/**
 * This class will create a dataset with one or more series from another
 * {@link SeriesDataset}. 
 *
 * @author Bill Kelemen (bill@kelemen-usa.com)
 */
public class SubSeriesDataset extends AbstractSeriesDataset
                              implements HighLowDataset, SignalsDataset, IntervalXYDataset,
                                         CombinationDataset {

    /** The parent dataset. */
    private SeriesDataset parent = null;

    /** Storage for map. */
    private int[] map;  // maps our series into our parent's

    /**
     * Creates a SubSeriesDataset using one or more series from <code>parent</code>.
     * The series to use are passed as an array of int.
     *
     * @param parent  underlying dataset
     * @param map  int[] of series from parent to include in this Dataset
     */
    public SubSeriesDataset(SeriesDataset parent, int[] map) {
        this.parent = parent;
        this.map = map;
    }

    /**
     * Creates a SubSeriesDataset using one series from <code>parent</code>.
     * The series to is passed as an int.
     *
     * @param parent  underlying dataset
     * @param series  series from parent to include in this Dataset
     */
    public SubSeriesDataset(SeriesDataset parent, int series) {
        this(parent, new int[] {series});
    }

    ///////////////////////////////////////////////////////////////////////////
    // From HighLowDataset
    ///////////////////////////////////////////////////////////////////////////

    /**
     * Returns the high-value for the specified series and item.
     * <p>
     * Note: throws <code>ClassCastException</code> if the series if not from a 
     * {@link HighLowDataset}.
     *
     * @param series  the index of the series of interest (zero-based).
     * @param item  the index of the item of interest (zero-based).
     *
     * @return the high-value for the specified series and item.
     */
    public Number getHighValue(int series, int item) {
        return ((HighLowDataset) this.parent).getHighValue(this.map[series], item);
    }

    /**
     * Returns the low-value for the specified series and item.
     * <p>
     * Note: throws <code>ClassCastException</code> if the series if not from a 
     * {@link HighLowDataset}.
     *
     * @param series  the index of the series of interest (zero-based).
     * @param item  the index of the item of interest (zero-based).
     *
     * @return the low-value for the specified series and item.
     */
    public Number getLowValue(int series, int item) {
        return ((HighLowDataset) this.parent).getLowValue(this.map[series], item);
    }

    /**
     * Returns the open-value for the specified series and item.
     * <p>
     * Note: throws <code>ClassCastException</code> if the series if not from a 
     * {@link HighLowDataset}.
     *
     * @param series  the index of the series of interest (zero-based).
     * @param item  the index of the item of interest (zero-based).
     *
     * @return the open-value for the specified series and item.
     */
    public Number getOpenValue(int series, int item) {
        return ((HighLowDataset) this.parent).getOpenValue(this.map[series], item);
    }

    /**
     * Returns the close-value for the specified series and item.
     * <p>
     * Note: throws <code>ClassCastException</code> if the series if not from a 
     * {@link HighLowDataset}.
     *
     * @param series  the index of the series of interest (zero-based).
     * @param item  the index of the item of interest (zero-based).
     *
     * @return the close-value for the specified series and item.
     */
    public Number getCloseValue(int series, int item) {
        return ((HighLowDataset) this.parent).getCloseValue(this.map[series], item);
    }

    /**
     * Returns the volume.
     * <p>
     * Note: throws <code>ClassCastException</code> if the series if not from a 
     * {@link HighLowDataset}.
     *
     * @param series  the series (zero based index).
     * @param item  the item (zero based index).
     *
     * @return the volume.
     */
    public Number getVolumeValue(int series, int item) {
        return ((HighLowDataset) this.parent).getVolumeValue(this.map[series], item);
    }


    ///////////////////////////////////////////////////////////////////////////
    // From XYDataset
    ///////////////////////////////////////////////////////////////////////////

    /**
     * Returns the X-value for the specified series and item.
     * <p>
     * Note: throws <code>ClassCastException</code> if the series if not from a 
     * {@link XYDataset}.
     *
     * @param series  the index of the series of interest (zero-based);
     * @param item  the index of the item of interest (zero-based).
     *
     * @return the X-value for the specified series and item.
     */
    public Number getXValue(int series, int item) {
        return ((XYDataset) this.parent).getXValue(this.map[series], item);
    }

    /**
     * Returns the Y-value for the specified series and item.
     * <p>
     * Note: throws <code>ClassCastException</code> if the series if not from a 
     * {@link XYDataset}.
     *
     * @param series  the index of the series of interest (zero-based).
     * @param item  the index of the item of interest (zero-based).
     *
     * @return the Y-value for the specified series and item.
     */
    public Number getYValue(int series, int item) {
        return ((XYDataset) this.parent).getYValue(this.map[series], item);
    }

    /**
     * Returns the number of items in a series.
     * <p>
     * Note: throws <code>ClassCastException</code> if the series if not from a 
     * {@link XYDataset}.
     *
     * @param series  the index of the series of interest (zero-based).
     *
     * @return the number of items in a series.
     */
    public int getItemCount(int series) {
        return ((XYDataset) this.parent).getItemCount(this.map[series]);
    }

    ///////////////////////////////////////////////////////////////////////////
    // From SeriesDataset
    ///////////////////////////////////////////////////////////////////////////

    /**
     * Returns the number of series in the dataset.
     *
     * @return the number of series in the dataset.
     */
    public int getSeriesCount() {
        return this.map.length;
    }

    /**
     * Returns the name of a series.
     *
     * @param series  the series (zero-based index).
     *
     * @return the name of a series.
     */
    public String getSeriesName(int series) {
        return this.parent.getSeriesName(this.map[series]);
    }

    ///////////////////////////////////////////////////////////////////////////
    // From IntervalXYDataset
    ///////////////////////////////////////////////////////////////////////////

    /**
     * Returns the starting X value for the specified series and item.
     *
     * @param series  the index of the series of interest (zero-based).
     * @param item  the index of the item of interest (zero-based).
     *
     * @return the starting X value for the specified series and item.
     */
    public Number getStartXValue(int series, int item) {
        if (this.parent instanceof IntervalXYDataset) {
            return ((IntervalXYDataset) this.parent).getStartXValue(this.map[series], item);
        }
        else {
            return getXValue(series, item);
        }
    }

    /**
     * Returns the ending X value for the specified series and item.
     *
     * @param series  the index of the series of interest (zero-based).
     * @param item  the index of the item of interest (zero-based).
     *
     * @return the ending X value for the specified series and item.
     */
    public Number getEndXValue(int series, int item) {
        if (this.parent instanceof IntervalXYDataset) {
            return ((IntervalXYDataset) this.parent).getEndXValue(this.map[series], item);
        }
        else {
            return getXValue(series, item);
        }
    }

    /**
     * Returns the starting Y value for the specified series and item.
     *
     * @param series  the index of the series of interest (zero-based).
     * @param item  the index of the item of interest (zero-based).
     *
     * @return the starting Y value for the specified series and item.
     */
    public Number getStartYValue(int series, int item) {
        if (this.parent instanceof IntervalXYDataset) {
            return ((IntervalXYDataset) this.parent).getStartYValue(this.map[series], item);
        }
        else {
            return getYValue(series, item);
        }
    }

    /**
     * Returns the ending Y value for the specified series and item.
     *
     * @param series  the index of the series of interest (zero-based).
     * @param item  the index of the item of interest (zero-based).
     *
     * @return the ending Y value for the specified series and item.
     */
    public Number getEndYValue(int series, int item) {
        if (this.parent instanceof IntervalXYDataset) {
            return ((IntervalXYDataset) this.parent).getEndYValue(this.map[series], item);
        }
        else {
            return getYValue(series, item);
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    // From SignalsDataset
    ///////////////////////////////////////////////////////////////////////////

    /**
     * Returns the type.
     *
     * @param series  the series (zero based index).
     * @param item  the item (zero based index).
     *
     * @return the type.
     */
    public int getType(int series, int item) {
        if (this.parent instanceof SignalsDataset) {
            return ((SignalsDataset) this.parent).getType(this.map[series], item);
        }
        else {
            return getYValue(series, item).intValue();
        }
    }

    /**
     * Returns the level.
     *
     * @param series  the series (zero based index).
     * @param item  the item (zero based index).
     *
     * @return the level.
     */
    public double getLevel(int series, int item) {
        if (this.parent instanceof SignalsDataset) {
            return ((SignalsDataset) this.parent).getLevel(this.map[series], item);
        }
        else {
            return getYValue(series, item).doubleValue();
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    // New methods from CombinationDataset
    ///////////////////////////////////////////////////////////////////////////

    /**
     * Returns the parent Dataset of this combination.
     *
     * @return the parent Dataset of this combination.
     */
    public SeriesDataset getParent() {
        return this.parent;
    }

    /**
     * Returns a map or indirect indexing form our series into parent's series.
     *
     * @return a map or indirect indexing form our series into parent's series.
     */
    public int[] getMap() {
        return (int[]) this.map.clone();
    }

}
