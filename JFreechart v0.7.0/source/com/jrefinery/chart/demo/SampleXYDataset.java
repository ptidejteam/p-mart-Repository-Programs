/* ===============
 * JFreeChart Demo
 * ===============
 *
 * Project Info:  http://www.jrefinery.com/jfreechart
 * Project Lead:  David Gilbert (david.gilbert@jrefinery.com);
 *
 * (C) Copyright 2000, 2001, Simba Management Limited and Contributors;
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
 * --------------------
 * SampleXYDataset.java
 * --------------------
 * (C) Copyright 2000, 2001, Simba Management Limited;
 *
 * Original Author:  David Gilbert;
 * Contributor(s):   -;
 *
 * $Id: SampleXYDataset.java,v 1.1 2007/10/10 18:54:39 vauchers Exp $
 *
 * Changes (from 24-Aug-2001)
 * --------------------------
 * 24-Aug-2001 : Added standard source header. Fixed DOS encoding problem (DG);
 * 15-Oct-2001 : Parent class has changed package (DG);
 * 22-Oct-2001 : Renamed DataSource.java --> Dataset.java etc. (DG);
 *               Added translate factor, used for demonstrating dynamic chart (DG);
 * 07-Nov-2001 : Updated source header (DG);
 *
 */

package com.jrefinery.chart.demo;

import com.jrefinery.data.*;

/**
 * A dummy dataset for an XY plot.
 * <P>
 * Note that the aim of this class is to create a self-contained data source for demo purposes -
 * it is NOT intended to show how you should go about writing your own datasets.
 */
public class SampleXYDataset extends AbstractSeriesDataset implements XYDataset {

    // use the translate to change the data and demonstrate dynamic data changes
    private double translate;

    /**
     * Default constructor.
     */
    public SampleXYDataset() {
        this.translate = 0.0;
    }

    public double getTranslate() {
        return this.translate;
    }

    public void setTranslate(double translate) {
        this.translate = translate;
        this.notifyListeners(new DatasetChangeEvent(this));
    }

    /**
     * Returns the x-value for the specified series and item.  Series are numbered 0, 1, ...
     * @param series The index (zero-based) of the series;
     * @param item The index (zero-based) of the required item;
     * @return The x-value for the specified series and item.
     */
    public Number getXValue(int series, int item) {
        return new Double(-10.0+translate+(item*0.2));
    }

    /**
     * Returns the y-value for the specified series and item.  Series are numbered 0, 1, ...
     * @param series The index (zero-based) of the series;
     * @param item The index (zero-based) of the required item;
     * @return The y-value for the specified series and item.
     */
    public Number getYValue(int series, int item) {
        if (series==0) {
            return new Double(Math.cos(-10.0+translate+(item/10.0)));
        }
        else return new Double(2*(Math.sin(-10.0+translate+(item/10.0))));
    }

    /**
     * Returns the number of series in the data source.
     * @return The number of series in the data source.
     */
    public int getSeriesCount() {
        return 2;
    }

    /**
     * Returns the name of the series.
     * @param series The index (zero-based) of the series;
     * @return The name of the series.
     */
    public String getSeriesName(int series) {
        if (series==0) {
            return "y = cosine(x)";
        }
        else if (series==1) {
            return "y = 2*sine(x)";
        }
        else return "Error";
    }

    /**
     * Returns the number of items in the specified series.
     * @param series The index (zero-based) of the series;
     * @return The number of items in the specified series.
     */
    public int getItemCount(int series) {
        return 200;
    }

}







