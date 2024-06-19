/* ===============
 * JFreeChart Demo
 * ===============
 *
 * Project Info:  http://www.jrefinery.com/jfreechart
 * Project Lead:  David Gilbert (david.gilbert@jrefinery.com);
 *
 * This file...
 * $Id: SampleXYDataset2.java,v 1.1 2007/10/10 18:53:20 vauchers Exp $
 *
 * Original Author:  David Gilbert;
 * Contributor(s):   -;
 *
 * (C) Copyright 2001, Simba Management Limited;
 *
 * This program is free software; you can redistribute it and/or modify it under the terms
 * of the GNU General Public License as published by the Free Software Foundation;
 * either version 2 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program;
 * if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston,
 * MA 02111-1307, USA.
 *
 * Change History:
 * ---------------
 * 22-Oct-2001 : Initial implementation (DG);
 *               Renamed DataSource.java --> Dataset.java etc. (DG);
 * 07-Nov-2001 : Updated source header (DG);
 *
 */

package com.jrefinery.chart.demo;

import com.jrefinery.data.*;

/**
 * Random data for a scatter plot demo.
 * <P>
 * Note that the aim of this class is to create a self-contained data source for demo purposes -
 * it is NOT intended to show how you should go about writing your own data sources.
 */
public class SampleXYDataset2 extends AbstractSeriesDataset implements XYDataset {

    private static final int SERIES_COUNT = 2;
    private static final int ITEM_COUNT = 100;
    private static final double RANGE = 200;

    private Double[][] xValues = new Double[SERIES_COUNT][ITEM_COUNT];
    private Double[][] yValues = new Double[SERIES_COUNT][ITEM_COUNT];

    /**
     * Default constructor.
     */
    public SampleXYDataset2() {

        for (int series=0; series<SERIES_COUNT; series++) {
            for (int item=0; item<ITEM_COUNT; item++) {
                double x = (Math.random()-0.5) * RANGE;
                xValues[series][item] = new Double(x);
                yValues[series][item] = new Double((Math.random() - 0.5) * x * x);
            }
        }

    }

    /**
     * Returns the x-value for the specified series and item.  Series are numbered 0, 1, ...
     * @param series The index (zero-based) of the series;
     * @param item The index (zero-based) of the required item;
     * @return The x-value for the specified series and item.
     */
    public Number getXValue(int series, int item) {
        return xValues[series][item];
    }

    /**
     * Returns the y-value for the specified series and item.  Series are numbered 0, 1, ...
     * @param series The index (zero-based) of the series;
     * @param item The index (zero-based) of the required item;
     * @return The y-value for the specified series and item.
     */
    public Number getYValue(int series, int item) {
        return yValues[series][item];
    }

    /**
     * Returns the number of series in the data source.
     * @return The number of series in the data source.
     */
    public int getSeriesCount() {
        return SERIES_COUNT;
    }

    /**
     * Returns the name of the series.
     * @param series The index (zero-based) of the series;
     * @return The name of the series.
     */
    public String getSeriesName(int series) {
        return "Sample "+series;
    }

    /**
     * Returns the number of items in the specified series.
     * @param series The index (zero-based) of the series;
     * @return The number of items in the specified series.
     */
    public int getItemCount(int series) {
        return ITEM_COUNT;
    }

}
