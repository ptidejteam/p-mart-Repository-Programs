/* ======================================
 * JFreeChart : a free Java chart library
 * ======================================
 *
 * Project Info:  http://www.object-refinery.com/jfreechart/index.html
 * Project Lead:  David Gilbert (david.gilbert@object-refinery.com);
 *
 * (C) Copyright 2000-2003, by Simba Management Limited and Contributors.
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
 * ------------------
 * MovingAverage.java
 * ------------------
 * (C) Copyright 2003, by Simba Management Limited.
 *
 * Original Author:  David Gilbert (for Simba Management Limited);
 * Contributor(s):   -;
 *
 * $Id: MovingAverage.java,v 1.1 2007/10/10 19:57:54 vauchers Exp $
 *
 * Changes
 * -------
 * 28-Jan-2003 : Version 1 (DG);
 *
 */

package com.jrefinery.data;

/**
 * A utility class for calculating moving averages of time series data.
 * 
 * @author David Gilbert 
 */
public class MovingAverage {

    /**
     * Creates a new {@link TimeSeries} containing moving average values for the given series.
     * <p>
     * If the series is empty (contains zero items), the result is an empty series.
     * 
     * @param source  the source series.
     * @param name  the name of the new series.
     * @param periodCount  the number of periods used in the average calculation.
     * @param skip  the number of initial periods to skip.
     * 
     * @return The moving average series.
     */
    public static TimeSeries createMovingAverage(TimeSeries source, 
                                                 String name, 
                                                 int periodCount,
                                                 int skip) {
        
        // check arguments
        if (source == null) {
            throw new IllegalArgumentException("MovingAverage.createMovingAverage(...) : "
                + "null source.");
        }
        
        if (periodCount < 1) {
            throw new IllegalArgumentException("MovingAverage.createMovingAverage(...) : "
                + "periodCount must be greater than or equal to 1.");
            
        }
        
        TimeSeries result = new TimeSeries(name, source.getTimePeriodClass());
        
        if (source.getItemCount() > 0) {

            // if the initial averaging period is to be excluded, then calculate the index of the
            // first data item to have an average calculated...
            long firstSerial = source.getDataPair(0).getPeriod().getSerialIndex() + skip;
       
            for (int i = source.getItemCount() - 1; i >= 0; i--) {
    
                // get the current data item...            
                TimeSeriesDataPair current = source.getDataPair(i);
                RegularTimePeriod period = current.getPeriod();
                long serial = period.getSerialIndex();
                Number value = current.getValue();
            
                if (serial >= firstSerial) {
                    // work out the average for the earlier values...
                    int n = 0;
                    double sum = 0.0;
                    long serialLimit = period.getSerialIndex() - periodCount;
                    int offset = 0;
                    boolean finished = false;
            
                    while ((offset < periodCount) && (!finished)) {
                        if ((i - offset) >= 0) {
                            TimeSeriesDataPair item = source.getDataPair(i - offset);
                            RegularTimePeriod p = item.getPeriod();
                            Number v = item.getValue();
                            long currentIndex = p.getSerialIndex();
                            if (currentIndex > serialLimit) {
                                if (v != null) {
                                    sum = sum + v.doubleValue();
                                    n = n + 1;
                                }
                            }
                            else {
                                finished = true;
                            }
                        }
                        offset = offset + 1;
                    }
                    if (n > 0) {
                        result.add(period, sum / n);
                    }
                    else {
                        result.add(period, null);
                    }
                }
            
            } 
        }       
        
        return result;
        
    }

    /**
     * Creates a new {@link XYDataset} containing the moving averages of each series in the 
     * <code>source</code> dataset.
     * 
     * @param source  the source dataset.
     * @param suffix  the string to append to source series names to create target series names.
     * @param periodMilliseconds  the averaging period (in milliseconds).
     * @param skipMilliseconds  the length of the initial skip period.
     * 
     * @return The dataset.
     */  
    public static XYDataset createMovingAverage(XYDataset source, String suffix,
                                                long periodMilliseconds, long skipMilliseconds) {
    
        XYSeriesCollection result = new XYSeriesCollection();
        
        for (int i = 0; i < source.getSeriesCount(); i++) {
            XYSeries s = createMovingAverage(source, i, source.getSeriesName(i) + suffix, 
                                             periodMilliseconds, skipMilliseconds);
            result.addSeries(s);
        }
        
        return result;            
    
    }

    /**
     * Creates a new {@link XYDataset} containing the moving averages of one series in the 
     * <code>source</code> dataset.
     * 
     * @param source  the source dataset.
     * @param series  the series index (zero based).
     * @param name  the name for the new series.
     * @param periodMilliseconds  the averaging period (in milliseconds).
     * @param skipMilliseconds  the length of the initial skip period.
     * 
     * @return The dataset.
     */  
    public static XYSeries createMovingAverage(XYDataset source, int series, String name,
                                               long periodMilliseconds, long skipMilliseconds) {

        // check arguments
        if (source == null) {
            throw new IllegalArgumentException("MovingAverage.createMovingAverage(...) : "
                + "null source (XYDataset).");
        }
        
        if (periodMilliseconds < 1) {
            throw new IllegalArgumentException("MovingAverage.createMovingAverage(...) : "
                + "periodMilliseconds must be greater than or equal to 1.");
            
        }
        
        if (skipMilliseconds < 0) {
            throw new IllegalArgumentException("MovingAverage.createMovingAverage(...) : "
                + "skipMilliseconds must be greater than or equal to 0.");
            
        }
        
        XYSeries result = new XYSeries(name);
        
        if (source.getItemCount(series) > 0) {

            // if the initial averaging period is to be excluded, then calculate the index of the
            // first data item to have an average calculated...
            double first = source.getXValue(series, 0).doubleValue() + skipMilliseconds;
       
            for (int i = source.getItemCount(series) - 1; i >= 0; i--) {
    
                // get the current data item...            
                double x = source.getXValue(series, i).doubleValue();
                double y = source.getYValue(series, i).doubleValue();
            
                if (x >= first) {
                    // work out the average for the earlier values...
                    int n = 0;
                    double sum = 0.0;
                    double limit = x - periodMilliseconds;
                    int offset = 0;
                    boolean finished = false;
            
                    while (!finished) {
                        if ((i - offset) >= 0) {
                            double xx = source.getXValue(series, i - offset).doubleValue();
                            Number yy = source.getYValue(series, i - offset);
                            if (xx > limit) {
                                if (yy != null) {
                                    sum = sum + yy.doubleValue();
                                    n = n + 1;
                                }
                            }
                            else {
                                finished = true;
                            }
                        }
                        offset = offset + 1;
                    }
                    if (n > 0) {
                        result.add(x, sum / n);
                    }
                    else {
                        result.add(x, null);
                    }
                }
            
            } 
        }       
        
        return result;
                                                        
    }

}
