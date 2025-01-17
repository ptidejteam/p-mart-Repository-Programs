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
 * $Id: SubSeriesDataset.java,v 1.1 2007/10/10 19:50:17 vauchers Exp $
 *
 * Changes
 * -------
 * 06-Dec-2001 : Version 1 (BK);
 * 05-Feb-2002 : Added SignalsDataset (and small change to HighLowDataset interface) as requested
 *               by Sylvain Vieujot (DG);
 * 28-Feb-2002 : Fixed bug: missing map[series] in IntervalXYDataset and SignalsDataset
 *               methods (BK);
 * 07-Oct-2002 : Fixed errors reported by Checkstyle (DG);
 * 06-May-2004 : Now extends AbstractIntervalXYDataset (DG);
 * 15-Jul-2004 : Switched getX() with getXValue() and getY() with getYValue() (DG);
 *
 */

package org.jfree.data.general;

import org.jfree.data.xy.AbstractIntervalXYDataset;
import org.jfree.data.xy.OHLCDataset;
import org.jfree.data.xy.IntervalXYDataset;
import org.jfree.data.xy.SignalsDataset;
import org.jfree.data.xy.XYDataset;

/**
 * This class will create a dataset with one or more series from another
 * {@link SeriesDataset}. 
 *
 * @author Bill Kelemen (bill@kelemen-usa.com)
 */
public class SubSeriesDataset extends AbstractIntervalXYDataset
                              implements OHLCDataset, SignalsDataset, IntervalXYDataset,
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
    public SubSeriesDataset(final SeriesDataset parent, final int[] map) {
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
    public SubSeriesDataset(final SeriesDataset parent, final int series) {
        this(parent, new int[] {series});
    }

    ///////////////////////////////////////////////////////////////////////////
    // From HighLowDataset
    ///////////////////////////////////////////////////////////////////////////

    /**
     * Returns the high-value for the specified series and item.
     * <p>
     * Note: throws <code>ClassCastException</code> if the series if not from a 
     * {@link OHLCDataset}.
     *
     * @param series  the index of the series of interest (zero-based).
     * @param item  the index of the item of interest (zero-based).
     *
     * @return the high-value for the specified series and item.
     */
    public Number getHigh(int series, int item) {
        return ((OHLCDataset) this.parent).getHigh(this.map[series], item);
    }

    /**
     * Returns the high-value (as a double primitive) for an item within a series.
     * 
     * @param series  the series (zero-based index).
     * @param item  the item (zero-based index).
     * 
     * @return The high-value.
     */
    public double getHighValue(int series, int item) {
        double result = Double.NaN;
        Number high = getHigh(series, item);
        if (high != null) {
            result = high.doubleValue();   
        }
        return result;   
    }

    /**
     * Returns the low-value for the specified series and item.
     * <p>
     * Note: throws <code>ClassCastException</code> if the series if not from a 
     * {@link OHLCDataset}.
     *
     * @param series  the index of the series of interest (zero-based).
     * @param item  the index of the item of interest (zero-based).
     *
     * @return the low-value for the specified series and item.
     */
    public Number getLow(int series, int item) {
        return ((OHLCDataset) this.parent).getLow(this.map[series], item);
    }

    /**
     * Returns the low-value (as a double primitive) for an item within a series.
     * 
     * @param series  the series (zero-based index).
     * @param item  the item (zero-based index).
     * 
     * @return The low-value.
     */
    public double getLowValue(int series, int item) {
        double result = Double.NaN;
        Number low = getLow(series, item);
        if (low != null) {
            result = low.doubleValue();   
        }
        return result;   
    }

    /**
     * Returns the open-value for the specified series and item.
     * <p>
     * Note: throws <code>ClassCastException</code> if the series if not from a 
     * {@link OHLCDataset}.
     *
     * @param series  the index of the series of interest (zero-based).
     * @param item  the index of the item of interest (zero-based).
     *
     * @return the open-value for the specified series and item.
     */
    public Number getOpen(int series, int item) {
        return ((OHLCDataset) this.parent).getOpen(this.map[series], item);
    }

    /**
     * Returns the open-value (as a double primitive) for an item within a series.
     * 
     * @param series  the series (zero-based index).
     * @param item  the item (zero-based index).
     * 
     * @return The open-value.
     */
    public double getOpenValue(int series, int item) {
        double result = Double.NaN;
        Number open = getOpen(series, item);
        if (open != null) {
            result = open.doubleValue();   
        }
        return result;   
    }

    /**
     * Returns the close-value for the specified series and item.
     * <p>
     * Note: throws <code>ClassCastException</code> if the series if not from a 
     * {@link OHLCDataset}.
     *
     * @param series  the index of the series of interest (zero-based).
     * @param item  the index of the item of interest (zero-based).
     *
     * @return the close-value for the specified series and item.
     */
    public Number getClose(int series, int item) {
        return ((OHLCDataset) this.parent).getClose(this.map[series], item);
    }

    /**
     * Returns the close-value (as a double primitive) for an item within a series.
     * 
     * @param series  the series (zero-based index).
     * @param item  the item (zero-based index).
     * 
     * @return The close-value.
     */
    public double getCloseValue(int series, int item) {
        double result = Double.NaN;
        Number close = getClose(series, item);
        if (close != null) {
            result = close.doubleValue();   
        }
        return result;   
    }

    /**
     * Returns the volume.
     * <p>
     * Note: throws <code>ClassCastException</code> if the series if not from a 
     * {@link OHLCDataset}.
     *
     * @param series  the series (zero based index).
     * @param item  the item (zero based index).
     *
     * @return the volume.
     */
    public Number getVolume(int series, int item) {
        return ((OHLCDataset) this.parent).getVolume(this.map[series], item);
    }

    /**
     * Returns the volume-value (as a double primitive) for an item within a series.
     * 
     * @param series  the series (zero-based index).
     * @param item  the item (zero-based index).
     * 
     * @return The volume-value.
     */
    public double getVolumeValue(int series, int item) {
        double result = Double.NaN;
        Number volume = getVolume(series, item);
        if (volume != null) {
            result = volume.doubleValue();   
        }
        return result;   
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
    public Number getX(int series, int item) {
        return ((XYDataset) this.parent).getX(this.map[series], item);
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
    public Number getY(int series, int item) {
        return ((XYDataset) this.parent).getY(this.map[series], item);
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
    public int getItemCount(final int series) {
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
    public String getSeriesName(final int series) {
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
    public Number getStartX(int series, int item) {
        if (this.parent instanceof IntervalXYDataset) {
            return ((IntervalXYDataset) this.parent).getStartX(this.map[series], item);
        }
        else {
            return getX(series, item);
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
    public Number getEndX(int series, int item) {
        if (this.parent instanceof IntervalXYDataset) {
            return ((IntervalXYDataset) this.parent).getEndX(this.map[series], item);
        }
        else {
            return getX(series, item);
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
    public Number getStartY(int series, int item) {
        if (this.parent instanceof IntervalXYDataset) {
            return ((IntervalXYDataset) this.parent).getStartY(this.map[series], item);
        }
        else {
            return getY(series, item);
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
    public Number getEndY(int series,  int item) {
        if (this.parent instanceof IntervalXYDataset) {
            return ((IntervalXYDataset) this.parent).getEndY(this.map[series], item);
        }
        else {
            return getY(series, item);
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
    public int getType(final int series, final int item) {
        if (this.parent instanceof SignalsDataset) {
            return ((SignalsDataset) this.parent).getType(this.map[series], item);
        }
        else {
            return getY(series, item).intValue();
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
    public double getLevel(final int series, final int item) {
        if (this.parent instanceof SignalsDataset) {
            return ((SignalsDataset) this.parent).getLevel(this.map[series], item);
        }
        else {
            return getYValue(series, item);
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
