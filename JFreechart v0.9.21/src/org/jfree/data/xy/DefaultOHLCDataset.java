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
 * -----------------------
 * DefaultOHLCDataset.java
 * -----------------------
 * (C) Copyright 2003, 2004, by Object Refinery Limited.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited);
 * Contributor(s):   -;
 *
 * $Id: DefaultOHLCDataset.java,v 1.1 2007/10/10 19:50:22 vauchers Exp $
 *
 * Changes
 * -------
 * 03-Dec-2003 : Version 1 (DG);
 * 05-May-2004 : Now extends AbstractXYDataset (DG);
 * 15-Jul-2004 : Switched getX() with getXValue() and getY() with getYValue() (DG);
 *
 */

package org.jfree.data.xy;

import java.util.Arrays;
import java.util.Date;

/**
 * A simple implementation of the {@link OHLCDataset} interface.  This implementation supports
 * only one series.
 */
public class DefaultOHLCDataset extends AbstractXYDataset implements OHLCDataset {

    /** The series name. */
    private String name;
    
    /** Storage for the data items. */
    private OHLCDataItem[] data;
    
    /**
     * Creates a new dataset.
     * 
     * @param name  the series name.
     * @param data  the data items.
     */
    public DefaultOHLCDataset(String name, OHLCDataItem[] data) {
        this.name = name;
        this.data = data;
    }
    
    /**
     * Returns the series name. 
     * 
     * @param series  the series index (ignored).
     * 
     * @return The series name.
     */
    public String getSeriesName(int series) {
        return this.name;
    }

    /**
     * Returns the x-value for a data item.
     * 
     * @param series  the series index (ignored).
     * @param item  the item index (zero-based).
     * 
     * @return The x-value.
     */
    public Number getX(int series, int item) {
        return new Long(this.data[item].getDate().getTime());
    }

    /**
     * Returns the x-value for a data item as a date.
     * 
     * @param series  the series index (ignored).
     * @param item  the item index (zero-based).
     * 
     * @return The x-value as a date.
     */
    public Date getXDate(int series, int item) {
        return this.data[item].getDate();
    }

    /**
     * Returns the y-value.
     * 
     * @param series  the series index (ignored).
     * @param item  the item index (zero-based).
     * 
     * @return the y value.
     */
    public Number getY(int series, int item) {
        return getClose(series, item);
    }

    /**
     * Returns the high value.
     * 
     * @param series  the series index (ignored).
     * @param item  the item index (zero-based).
     * 
     * @return the high value.
     */
    public Number getHigh(int series, int item) {
        return this.data[item].getHigh();
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
     * Returns the low value.
     * 
     * @param series  the series index (ignored).
     * @param item  the item index (zero-based).
     * 
     * @return the low value.
     */
    public Number getLow(int series, int item) {
        return this.data[item].getLow();
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
     * Returns the open value.
     * 
     * @param series  the series index (ignored).
     * @param item  the item index (zero-based).
     * 
     * @return the open value.
     */
    public Number getOpen(int series, int item) {
        return this.data[item].getOpen();
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
     * Returns the close value.
     * 
     * @param series  the series index (ignored).
     * @param item  the item index (zero-based).
     * 
     * @return the close value.
     */
    public Number getClose(int series, int item) {
        return this.data[item].getClose();
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
     * Returns the trading volume.
     * 
     * @param series  the series index (ignored).
     * @param item  the item index (zero-based).
     * 
     * @return the trading volume.
     */
    public Number getVolume(int series, int item) {
        return this.data[item].getVolume();
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

    /**
     * Returns the series count.
     * 
     * @return 1.
     */
    public int getSeriesCount() {
        return 1;
    }

    /**
     * Returns the item count for the specified series.
     * 
     * @param series  the series index (ignored).
     * 
     * @return The item count.
     */
    public int getItemCount(int series) {
        return this.data.length;
    }
   
    /**
     * Sorts the data into ascending order by date.
     */
    public void sortDataByDate() {
        Arrays.sort(this.data);    
    }

}
