/* =======================================
 * JFreeChart : a Java Chart Class Library
 * =======================================
 *
 * Project Info:  http://www.jrefinery.com/jfreechart
 * Project Lead:  David Gilbert (david.gilbert@jrefinery.com);
 *
 * This file...
 * $Id: MovingAveragePlotFitAlgorithm.java,v 1.1 2007/10/10 18:54:40 vauchers Exp $
 *
 * Original Author:  Matthew Wright;
 * Contributor(s):   David Gilbert;
 *
 * (C) Copyright 2001, Simba Management Limited;
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
 * Changes (from 15-Oct-2001)
 * --------------------------
 * 15-Oct-2001 : Data source classes in new package com.jrefinery.data.* (DG);
 * 22-Oct-2001 : Renamed DataSource.java --> Dataset.java etc. (DG);
 * 08-Nov-2001 : Removed redundant import statements, tidied up Javadoc comments (DG);
 *
 */

package com.jrefinery.chart.data;

import java.util.Vector;
import com.jrefinery.data.*;

/**
 * Calculates a moving average for an XYDataset.
 */
public class MovingAveragePlotFitAlgorithm implements PlotFitAlgorithm {

    /** The underlying dataset. */
    private XYDataset dataset;

    /** The moving average period. */
    private int period = 5;

    /** ?? */
    private Vector plots;

    /**
     * @return the name that you want to see in the legend.
     */
    public String getName() {
        return "Moving Average";
    }

    /**
     * Sets the period for this moving average algorithm.
     * @param period The number of points to include in the average.
     */
    public void setPeriod(int period) {
        this.period = period;
    }

    /**
     * @param ds The underlying XYDataset.
     */
    public void setXYDataset(XYDataset ds) {

        this.dataset = ds;

        /*
         * build the x and y data arrays to be passed to the
         * statistics class to get a linear fit and store them
         * for each dataset in the datasets Vector
         */
        Vector datasets = new Vector();
        for(int i = 0; i < ds.getSeriesCount(); i++) {
            int seriessize = ds.getItemCount(i);
            Number[] x_data = new Number[seriessize];
            Number[] y_data = new Number[seriessize];
            for(int j = 0; j < seriessize; j++) {
                x_data[j] = ds.getXValue(i,j);
                y_data[j] = ds.getYValue(i,j);
            }
            Vector pair = new Vector();
            pair.addElement(x_data);
            pair.addElement(y_data);
            datasets.addElement(pair);
        }
        plots = new Vector();
        for(int j = 0; j < datasets.size(); j++) {
            Vector pair = (Vector)datasets.elementAt(j);
            Number[] x_data = (Number[])pair.elementAt(0);
            Number[] y_data = (Number[])pair.elementAt(1);
            plots.addElement(new ArrayHolder(Statistics.getMovingAverage(x_data, y_data, period)));
        }

    }

    /**
     * Returns the y-value for any x-value.
     * @param x The x-value.
     * @param series The series.
     * @return The y-value
     */
    public Number getY(int series, Number x) {

        /*
         * for a moving average, this returns a number if there is a match
         * for that y and series, otherwise, it returns a null reference
         */
        double[][] mavg = ((ArrayHolder)plots.elementAt(series)).getArray();
        for(int j = 0; j < mavg.length; j++) {

            /* if the x matches up, we have a moving average point for this x */
            if(mavg[j][0] == x.doubleValue()) {
                return new Double(mavg[j][1]);
            }
        }
        /* if we don't return null */
        return null;
    }

}

/**
 * A utility class to hold the moving average arrays in a Vector.
 */
class ArrayHolder {

    private double[][] array;

    ArrayHolder(double[][] array) {
        this.array = array;
    }

    public double[][] getArray() {
        return array;
    }

}