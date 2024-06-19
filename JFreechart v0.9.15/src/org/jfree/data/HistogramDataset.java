/* ======================================
 * JFreeChart : a free Java chart library
 * ======================================
 *
 * Project Info:  http://www.jfree.org/jfreechart/index.html
 * Project Lead:  David Gilbert (david.gilbert@object-refinery.com);
 *
 * (C) Copyright 2000-2003, by Object Refinery Limited and Contributors.
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
 * ---------------------
 * HistogramDataset.java
 * ---------------------
 * (C) Copyright 2003, by Jelai Wang and Contributors.
 *
 * Original Author:  Jelai Wang (jelaiw AT mindspring.com);
 * Contributor(s):   David Gilbert (for Object Refinery Limited);
 *
 * $Id: HistogramDataset.java,v 1.1 2007/10/10 19:21:49 vauchers Exp $
 *
 * Changes
 * -------
 * 06-Jul-2003 : Version 1, contributed by Jelai Wang (DG);
 * 07-Jul-2003 : Changed package and added Javadocs (DG);
 * 15-Oct-2003 : Updated Javadocs and removed array sorting (JW);
 * 
 */

package org.jfree.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A dataset that can be used for creating histograms.
 * <p>
 * See the <code>HistogramDemo.java</code> file in the JFreeChart distribution for an example.
 * 
 * @author Jelai Wang, jelaiw AT mindspring.com
 */
public class HistogramDataset extends AbstractDataset implements IntervalXYDataset {

    /** A constant to indicate a frequency histogram, also the default. */
    public final static HistogramType FREQUENCY = new HistogramType();
    
    /** A constant to indicate a relative frequency histogram. */ 
    public static HistogramType RELATIVE_FREQUENCY = new HistogramType();
    
    /** A constant to indicate a histogram where the total area is scaled to 1. */
    public static HistogramType SCALE_AREA_TO_1 = new HistogramType();  

    /** A list of maps. */
    private List list = new ArrayList();
    
    /** The histogram type. */
    private HistogramType type = FREQUENCY;

    /**
     * Sets the histogram type.
     * 
     * @param type  the type (<code>null</code> not permitted).
     */
    public void setType(HistogramType type) {
        if (type == null) {
            throw new IllegalArgumentException(
                "HistogramDataset.setType(...):  null not permitted.");
        }
        this.type = type;   
    }

    /**
     * Returns the histogram type. 
     * 
     * @return The type.
     */
    public HistogramType getType() { 
        return type; 
    }

    /**
     * Adds a series.
     * 
     * @param name  the series name.
     * @param values the values (<code>null</code> not permitted).
     * @param numberOfBins  the number of bins (must be at least 1).
     */
    public void addSeries(String name, double[] values, int numberOfBins) {
        if (values == null) {
            throw new IllegalArgumentException(
                "HistogramDataset.addSeries(...): 'values' argument must not be null."
            );
        }
        else if (numberOfBins < 1) {
            throw new IllegalArgumentException(
                "HistogramDataset.addSeries(...): number of bins must be at least 1"
            );
        }
        // work out bin strategy
        double minimum = getMinimum(values);
        double maximum = getMaximum(values);
        double binWidth = (maximum - minimum) / numberOfBins;
        // set up the bins
        double tmp = minimum;
        HistogramBin[] bins = new HistogramBin[numberOfBins];
        for (int i = 0; i < bins.length; i++) {
            HistogramBin bin = new HistogramBin(tmp, tmp + binWidth);
            tmp = tmp + binWidth;
            bins[i] = bin;
        }
        // fill the bins
        for (int i = 0; i < values.length; i++) {
            for (int j = 0; j < bins.length; j++) {
                if (values[i] >= bins[j].getStartBoundary()
                    && values[i] <= bins[j].getEndBoundary()) { 
                    // note the greedy <=
                    bins[j].incrementCount();
                    break; // break out of inner loop
                }
            }
        }
        // generic map for each series
        Map map = new HashMap();
        map.put("name", name);
        map.put("bins", bins);
        map.put("values.length", new Integer(values.length));
        map.put("bin width", new Double(binWidth));
        list.add(map);
    }

    /**
     * Returns the minimum value from an array.
     * 
     * @param values  the values.
     * 
     * @return  The minimum value.
     */
    private double getMinimum(double[] values) {
        if (values == null || values.length < 1) {
            throw new IllegalArgumentException();
        }

        double min = Double.MAX_VALUE;
        for (int i = 0; i < values.length; i++) {
            if (values[i] < min) {
                min = values[i];
            }
        }
        return min;
    }

    /**
     * Returns the maximum value from an array.
     * 
     * @param values  the values.
     * 
     * @return The maximum value.
     */
    private double getMaximum(double[] values) {
        if (values == null || values.length < 1) {
            throw new IllegalArgumentException();
        }

        double max = -Double.MAX_VALUE;
        for (int i = 0; i < values.length; i++) {
            if (values[i] > max) {
                max = values[i];
            }
        }
        return max;
    }

    /**
     * Returns the bins for a series.
     * 
     * @param series  the series index.
     * 
     * @return An array of bins.
     */
    HistogramBin[] getBins(int series) { 
        Map map = (Map) list.get(series);
        return (HistogramBin[]) map.get("bins"); 
    }

    /**
     * Returns the total number of observations for a series.
     * 
     * @param series  the series index.
     * 
     * @return The total.
     */
    private int getTotal(int series) {
        Map map = (Map) list.get(series);
        return ((Integer) map.get("values.length")).intValue(); 
    }

    /**
     * Returns the bin width for a series.
     * 
     * @param series  the series index (zero based).
     * 
     * @return The bin width.
     */
    private double getBinWidth(int series) {
        Map map = (Map) list.get(series);
        return ((Double) map.get("bin width")).doubleValue(); 
    }

    /**
     * Returns the number of series in the dataset.
     * 
     * @return The series count.
     */
    public int getSeriesCount() { 
        return list.size(); 
    }
    
    /**
     * Returns the name for a series.
     * 
     * @param series  the series index (zero based).
     * 
     * @return The series name.
     */
    public String getSeriesName(int series) { 
        Map map = (Map) list.get(series);
        return (String) map.get("name"); 
    }

    /**
     * Returns the number of data items for a series.
     * 
     * @param series  the series index (zero based).
     * 
     * @return The item count.
     */
    public int getItemCount(int series) { 
        return getBins(series).length; 
    }

    /**
     * Returns the X value for a bin. 
     * <p>
     * This value won't be used for plotting histograms, since the renderer will ignore it.
     * But other renderers can use it (for example, you could use the dataset to create a line
     * chart).
     * 
     * @param series  the series index (zero based).
     * @param item  the item index (zero based).
     * 
     * @return The start value.
     */
    public Number getXValue(int series, int item) { 
        HistogramBin[] bins = getBins(series);
        HistogramBin bin = bins[item];
        double x = (bin.getStartBoundary() + bin.getEndBoundary()) / 2.;
        return new Double(x);
    }

    /**
     * Returns the Y value for a bin.
     * 
     * @param series  the series index (zero based).
     * @param item  the item index (zero based).
     * 
     * @return The Y value.
     */
    public Number getYValue(int series, int item) { 
        HistogramBin[] bins = getBins(series);
        double total = getTotal(series);
        double binWidth = getBinWidth(series);

        if (type == FREQUENCY) {
            return new Double(bins[item].getCount());
        }
        else if (type == RELATIVE_FREQUENCY) {
            return new Double(bins[item].getCount() / total);
        }
        else if (type == SCALE_AREA_TO_1) {
            return new Double(bins[item].getCount() / (binWidth * total));
        }
        else { // pretty sure this shouldn't ever happen
            throw new IllegalStateException();
        }
    }

    /**
     * Returns the start value for a bin.
     * 
     * @param series  the series index (zero based).
     * @param item  the item index (zero based).
     * 
     * @return The start value.
     */
    public Number getStartXValue(int series, int item) {
        HistogramBin[] bins = getBins(series);
        return new Double(bins[item].getStartBoundary());
    }

    /**
     * Returns the end value for a bin.
     * 
     * @param series  the series index (zero based).
     * @param item  the item index (zero based).
     * 
     * @return The end value.
     */
    public Number getEndXValue(int series, int item) {
        HistogramBin[] bins = getBins(series);
        return new Double(bins[item].getEndBoundary());
    }

    /**
     * Returns the Y value for a bin.
     * 
     * @param series  the series index (zero based).
     * @param item  the item index (zero based).
     * 
     * @return The Y value.
     */
    public Number getStartYValue(int series, int item) {
        //HistogramBin[] bins = getBins(series);
        return getYValue(series, item);
    }

    /**
     * Returns the Y value for a bin.
     * 
     * @param series  the series index (zero based).
     * @param item  the item index (zero based).
     * 
     * @return The Y value.
     */    
    public Number getEndYValue(int series, int item) {
        //HistogramBin[] bins = getBins(series);
        return getYValue(series, item);
    }

    /**
     * A class for creating constants to represent the histogram type.  See Bloch's enum tip in
     * 'Effective Java'
     */
    private static class HistogramType { 
        /** 
         * Creates a new type.
         */
        private HistogramType() {
        } 
    }

}
