/* =======================================
 * JFreeChart : a Java Chart Class Library
 * =======================================
 *
 * Project Info:  http://www.jrefinery.com/jfreechart
 * Project Lead:  David Gilbert (david.gilbert@jrefinery.com);
 *
 * (C) Copyright 2000-2002, by Simba Management Limited and Contributors.
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
 * ------------
 * PlotFit.java
 * ------------
 * (C) Copyright 2001, 2002, by Matthew Wright and Contributors.
 *
 * Original Author:  Matthew Wright;
 * Contributor(s):   David Gilbert (for Simba Management Limited);
 *
 * $Id: PlotFit.java,v 1.1 2007/10/10 18:57:09 vauchers Exp $
 *
 * Changes (from 15-Oct-2001)
 * --------------------------
 * 15-Oct-2001 : Data source classes in new package com.jrefinery.data.* (DG);
 * 22-Oct-2001 : Renamed DataSource.java --> Dataset.java etc. (DG);
 * 08-Nov-2001 : Removed redundant import statements, tidied up Javadoc comments (DG);
 *
 */

package com.jrefinery.chart.data;

import com.jrefinery.data.*;

/**
 * Manages the creation of a new dataset based on an existing XYDataset, according to a pluggable
 * algorithm.
 */
public class PlotFit {

    /** The underlying dataset. */
    protected XYDataset dataset;

    /** The algorithm. */
    protected PlotFitAlgorithm alg;

    /**
     * Standard constructor.
     * @param data The underlying dataset.
     * @param alg The algorithm.
     */
    public PlotFit(XYDataset data, PlotFitAlgorithm alg) {
        this.dataset = data;
        this.alg = alg;
    }

    /**
     * Sets the underlying dataset.
     * @param data The underlying dataset.
     */
    public void setXYDataset(XYDataset data) {
        this.dataset = data;
    }

    /**
     * Sets the algorithm used to generate the new dataset.
     * @param alg The algorithm.
     */
    public void setPlotFitAlgorithm(PlotFitAlgorithm alg) {
        this.alg = alg;
    }

    /**
     * Returns a three-dimensional array based on algorithm calculations.  Used to create a new
     * dataset.
     * Matthew Wright:  implements what I'm doing in code now... not the best way to do this?
     */
    public Object[][][] getResults() {

        /* set up our algorithm */
        alg.setXYDataset(dataset);

        /* make a data container big enough to hold it all */
        int arraysize = 0;
        int seriescount = dataset.getSeriesCount();
        for(int i = 0; i < seriescount; i++) {
            if(dataset.getItemCount(i) > arraysize) {
                arraysize = dataset.getItemCount(i);
            }
        }

        // we'll apply the plot fit to all of the series for now
        Object[][][] newdata = new Object[seriescount * 2][arraysize][2];

        /* copy in the series to the first half */
        for(int i = 0; i < seriescount; i++) {
            for(int j = 0; j < dataset.getItemCount(i); j++) {
                Number x = dataset.getXValue(i,j);
                newdata[i][j][0] = x;
                newdata[i][j][1] = dataset.getYValue(i,j);
                Number y = alg.getY(i, x);
                /*
                 * only want to set data for non-null algorithm fits.
                 * This allows things like moving average plots, or partial
                 * plots to return null and not get NPEs when the chart is
                 * created
                 */
                if(y != null) {
                    newdata[i + seriescount][j][0] = x;
                    newdata[i + seriescount][j][1] = y;
                }
                else {
                    newdata[i + seriescount][j][0] = null;
                    newdata[i + seriescount][j][1] = null;
	        }
            }
        }
        return newdata;
    }

    /**
     * Constructs and returns a new dataset based on applying an algorithm to an underlying
     * dataset.
     */
    public XYDataset getFit() {
        int seriescount = dataset.getSeriesCount();
        String[] seriesnames = new String[seriescount * 2];
        for(int i = 0; i < seriescount; i++) {
            seriesnames[i] = dataset.getSeriesName(i);
            seriesnames[i + seriescount] = dataset.getSeriesName(i) + " " + alg.getName();
        }

        return new DefaultXYDataset(seriesnames, getResults());
    }

}
