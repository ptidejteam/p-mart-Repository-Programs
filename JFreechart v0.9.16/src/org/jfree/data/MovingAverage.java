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
 * ------------------
 * MovingAverage.java
 * ------------------
 * (C) Copyright 2003, 2004, by Object Refinery Limited.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited);
 * Contributor(s):   Benoit Xhenseval;
 *
 * $Id: MovingAverage.java,v 1.1 2007/10/10 19:25:30 vauchers Exp $
 *
 * Changes
 * -------
 * 28-Jan-2003 : Version 1 (DG);
 * 10-Mar-2003 : Added createPointMovingAverage(...) method contributed by Benoit Xhenseval (DG);
 * 01-Aug-2003 : Added new method for TimeSeriesCollection, and fixed bug in XYDataset method (DG);
 *
 */

package org.jfree.data;

import org.jfree.data.time.RegularTimePeriod;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.time.TimeSeriesDataItem;

/**
 * A utility class for calculating moving averages of time series data.
 *
 * @author David Gilbert
 */
public class MovingAverage {

    /**
     * Creates a new {@link TimeSeriesCollection} containing a moving average series for each
     * series in the source collection.
     * 
     * @param source  the source collection.
     * @param suffix  the suffix added to each source series name to create the corresponding 
     *                moving average series name.
     * @param periodCount  the number of periods in the moving average calculation.
     * @param skip  the number of initial periods to skip.
     * 
     * @return A collection of moving average time series.
     */
    public static TimeSeriesCollection createMovingAverage(TimeSeriesCollection source, 
                                                           String suffix,
                                                           int periodCount, 
                                                           int skip) {
    
        // check arguments
        if (source == null) {
            throw new IllegalArgumentException(
                "MovingAverage.createMovingAverage(...) : null source."
            );
        }

        if (periodCount < 1) {
            throw new IllegalArgumentException("MovingAverage.createMovingAverage(...) : "
                + "periodCount must be greater than or equal to 1.");
        }

        TimeSeriesCollection result = new TimeSeriesCollection();
        
        for (int i = 0; i < source.getSeriesCount(); i++) {
            TimeSeries sourceSeries = source.getSeries(i);
            TimeSeries maSeries = createMovingAverage(sourceSeries, sourceSeries.getName() + suffix,
                                                      periodCount, skip);
            result.addSeries(maSeries);       
        }
        
        return result;
        
    }
    
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
            throw new IllegalArgumentException(
                "MovingAverage.createMovingAverage(...) : null source."
            );
        }

        if (periodCount < 1) {
            throw new IllegalArgumentException("MovingAverage.createMovingAverage(...) : "
                + "periodCount must be greater than or equal to 1.");

        }

        TimeSeries result = new TimeSeries(name, source.getTimePeriodClass());

        if (source.getItemCount() > 0) {

            // if the initial averaging period is to be excluded, then calculate the index of the
            // first data item to have an average calculated...
            long firstSerial = source.getDataItem(0).getPeriod().getSerialIndex() + skip;

            for (int i = source.getItemCount() - 1; i >= 0; i--) {

                // get the current data item...
                TimeSeriesDataItem current = source.getDataItem(i);
                RegularTimePeriod period = current.getPeriod();
                long serial = period.getSerialIndex();

                if (serial >= firstSerial) {
                    // work out the average for the earlier values...
                    int n = 0;
                    double sum = 0.0;
                    long serialLimit = period.getSerialIndex() - periodCount;
                    int offset = 0;
                    boolean finished = false;

                    while ((offset < periodCount) && (!finished)) {
                        if ((i - offset) >= 0) {
                            TimeSeriesDataItem item = source.getDataItem(i - offset);
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
     * Creates a new {@link TimeSeries} containing moving average values for the given series,
     * calculated by number of points (irrespective of the 'age' of those points).
     * <p>
     * If the series is empty (contains zero items), the result is an empty series.
     * <p>
     * Developed by Benoit Xhenseval (www.ObjectLab.co.uk).
     *
     * @param source  the source series.
     * @param name  the name of the new series.
     * @param pointCount  the number of POINTS used in the average calculation (not periods!)
     *
     * @return The moving average series.
     */
    public static TimeSeries createPointMovingAverage(TimeSeries source,
                                                      String name, int pointCount) {

        // check arguments
        if (source == null) {
            throw new IllegalArgumentException("MovingAverage.createMovingAverage(...) : "
                + "null source.");
        }

        if (pointCount < 2) {
            throw new IllegalArgumentException("MovingAverage.createMovingAverage(...) : "
                + "periodCount must be greater than or equal to 2.");

        }

        TimeSeries result = new TimeSeries(name, source.getTimePeriodClass());
        double rollingSumForPeriod = 0.0;
        for (int i = 0; i < source.getItemCount(); i++) {
            // get the current data item...
            TimeSeriesDataItem current = source.getDataItem(i);
            RegularTimePeriod period = current.getPeriod();
            rollingSumForPeriod += current.getValue().doubleValue();

            if (i > pointCount - 1) {
                // remove the point i-periodCount out of the rolling sum.
                TimeSeriesDataItem startOfMovingAvg = source.getDataItem(i - pointCount);
                rollingSumForPeriod -= startOfMovingAvg.getValue().doubleValue();
                result.add(period, rollingSumForPeriod / pointCount);
            }
            else if (i == pointCount - 1) {
                result.add(period, rollingSumForPeriod / pointCount);
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
     * @param period  the averaging period.
     * @param skip  the length of the initial skip period.
     *
     * @return The dataset.
     */
    public static XYDataset createMovingAverage(XYDataset source, String suffix,
                                                long period, long skip) {

        return createMovingAverage(source, suffix, (double) period, (double) skip);
        
    }


    /**
     * Creates a new {@link XYDataset} containing the moving averages of each series in the
     * <code>source</code> dataset.
     *
     * @param source  the source dataset.
     * @param suffix  the string to append to source series names to create target series names.
     * @param period  the averaging period.
     * @param skip  the length of the initial skip period.
     *
     * @return The dataset.
     */
    public static XYDataset createMovingAverage(XYDataset source, String suffix,
                                                double period, double skip) {

        // check arguments
        if (source == null) {
            throw new IllegalArgumentException(
                "MovingAverage.createMovingAverage(...) : null source (XYDataset)."
            );
        }
        
        XYSeriesCollection result = new XYSeriesCollection();

        for (int i = 0; i < source.getSeriesCount(); i++) {
            XYSeries s = createMovingAverage(source, i, source.getSeriesName(i) + suffix,
                                             period, skip);
            result.addSeries(s);
        }

        return result;

    }

    /**
     * Creates a new {@link XYSeries} containing the moving averages of one series in the
     * <code>source</code> dataset.
     *
     * @param source  the source dataset.
     * @param series  the series index (zero based).
     * @param name  the name for the new series.
     * @param period  the averaging period.
     * @param skip  the length of the initial skip period.
     *
     * @return The dataset.
     * 
     * @deprecated Use similar method with 'double' parameters.
     */
    public static XYSeries createMovingAverage(XYDataset source, int series, String name,
                                               long period, long skip) {
        return createMovingAverage(source, series, name, (double) period, (double) skip);
    }

    /**
     * Creates a new {@link XYSeries} containing the moving averages of one series in the
     * <code>source</code> dataset.
     *
     * @param source  the source dataset.
     * @param series  the series index (zero based).
     * @param name  the name for the new series.
     * @param period  the averaging period.
     * @param skip  the length of the initial skip period.
     *
     * @return The dataset.
     */
    public static XYSeries createMovingAverage(XYDataset source, int series, String name,
                                               double period, double skip) {

                                               
        // check arguments
        if (source == null) {
            throw new IllegalArgumentException("MovingAverage.createMovingAverage(...) : "
                + "null source (XYDataset).");
        }

        if (period < Double.MIN_VALUE) {
            throw new IllegalArgumentException("MovingAverage.createMovingAverage(...) : "
                + "period must be positive.");

        }

        if (skip < 0.0) {
            throw new IllegalArgumentException("MovingAverage.createMovingAverage(...) : "
                + "skip must be >= 0.0.");

        }

        XYSeries result = new XYSeries(name);

        if (source.getItemCount(series) > 0) {

            // if the initial averaging period is to be excluded, then calculate the lowest 
            // x-value to have an average calculated...
            double first = source.getXValue(series, 0).doubleValue() + skip;

            for (int i = source.getItemCount(series) - 1; i >= 0; i--) {

                // get the current data item...
                double x = source.getXValue(series, i).doubleValue();

                if (x >= first) {
                    // work out the average for the earlier values...
                    int n = 0;
                    double sum = 0.0;
                    double limit = x - period;
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
                        else {
                            finished = true;
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
