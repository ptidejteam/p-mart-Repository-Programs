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
 * -------------------------------
 * TimePeriodValuesCollection.java
 * -------------------------------
 * (C) Copyright 2003, by Object Refinery Limited.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited);
 * Contributor(s):   -;
 *
 * $Id: TimePeriodValuesCollection.java,v 1.1 2007/10/10 20:07:31 vauchers Exp $
 *
 * Changes
 * -------
 * 22-Apr-2003 : Version 1 (DG);
 *
 */

package org.jfree.data.time;

import java.io.Serializable;
import java.util.Iterator;
import java.util.List;
import java.util.TimeZone;

import org.jfree.data.AbstractSeriesDataset;
import org.jfree.data.DomainInfo;
import org.jfree.data.IntervalXYDataset;
import org.jfree.data.Range;

/**
 * A collection of {@link TimePeriodValues} objects.
 * <P>
 * This class implements the {@link org.jfree.data.XYDataset} interface, as well as the
 * extended {@link IntervalXYDataset} interface.  This makes it a convenient dataset for use with
 * the {@link org.jfree.chart.plot.XYPlot} class.
 *
 * @author David Gilbert
 */
public class TimePeriodValuesCollection extends AbstractSeriesDataset
                                        implements IntervalXYDataset,
                                                   DomainInfo,
                                                   Serializable {

    /** Useful constant for controlling the x-value returned for a time period. */
    public static final int START = 0;

    /** Useful constant for controlling the x-value returned for a time period. */
    public static final int MIDDLE = 1;

    /** Useful constant for controlling the x-value returned for a time period. */
    public static final int END = 2;

    /** Storage for the time series. */
    private List data;

    /** The position within a time period to return as the x-value (START, MIDDLE or END). */
    private int position;

    /**
     * A flag that indicates that the domain is 'points in time'.  If this flag is true, only
     * the x-value is used to determine the range of values in the domain, the start and end
     * x-values are ignored.
     */
    private boolean domainIsPointsInTime;

    /**
     * Constructs an empty dataset, tied to the default timezone.
     */
    public TimePeriodValuesCollection() {
        this(null, TimeZone.getDefault());
    }

    /**
     * Constructs an empty dataset, tied to a specific timezone.
     *
     * @param zone the timezone.
     */
    public TimePeriodValuesCollection(TimeZone zone) {
        this(null, zone);
    }

    /**
     * Constructs a dataset containing a single series (more can be added),
     * tied to the default timezone.
     *
     * @param series the series.
     */
    public TimePeriodValuesCollection(TimePeriodValues series) {
      this.data = new java.util.ArrayList();
      if (series != null) {
          data.add(series);
          series.addChangeListener(this);
      }
      this.position = MIDDLE;
      this.domainIsPointsInTime = true;
    }

    /**
     * Constructs a dataset containing a single series (more can be added),
     * tied to a specific timezone.
     *
     * @param series the series.
     * @param zone the timezone.
     * @deprecated TimeZone parameter is not used.
     */
    public TimePeriodValuesCollection(TimePeriodValues series, TimeZone zone) {
      this(series);
    }

    /**
     * Returns the position of the x-value returned for a time period (START,
     * MIDDLE, or END).
     *
     * @return the position.
     */
    public int getPosition() {
        return this.position;
    }

    /**
     * Sets the position - this controls the x-value that is returned for a
     * particular time period.
     * <P>
     * Use the constants START, MIDDLE and END.
     *
     * @param position the position.
     */
    public void setPosition(int position) {
        this.position = position;
    }

    /**
     * Returns a flag that controls whether the domain is treated as 'points in time'.
     * <P>
     * This flag is used when determining the max and min values for the domain.  If true, then
     * only the x-values are considered for the max and min values.  If false, then the start and
     * end x-values will also be taken into consideration
     *
     * @return the flag.
     */
    public boolean getDomainIsPointsInTime() {
        return this.domainIsPointsInTime;
    }

    /**
     * Sets a flag that controls whether the domain is treated as 'points in time', or time
     * periods.
     *
     * @param flag The new value of the flag.
     */
    public void setDomainIsPointsInTime(boolean flag) {
        this.domainIsPointsInTime = flag;
    }

    /**
     * Returns the number of series in the collection.
     *
     * @return the series count.
     */
    public int getSeriesCount() {
        return this.data.size();
    }

    /**
     * Returns a series.
     *
     * @param series The index of the series (zero-based).
     *
     * @return the series.
     */
    public TimePeriodValues getSeries(int series) {

        // check arguments...
        if ((series < 0) || (series > getSeriesCount())) {
            throw new IllegalArgumentException(
                "TimePeriodValuesCollection.getSeries(...): index outside valid range.");
        }

        // fetch the series...
        TimePeriodValues ts = (TimePeriodValues) data.get(series);
        return ts;

    }

    /**
     * Returns the name of a series.
     * <P>
     * This method is provided for convenience.
     *
     * @param series The index of the series (zero-based).
     *
     * @return the name of a series.
     */
    public String getSeriesName(int series) {

        // check arguments...delegated
        // fetch the series name...
        return getSeries(series).getName();

    }

    /**
     * Adds a series to the collection.
     * <P>
     * Notifies all registered listeners that the dataset has changed.
     *
     * @param series the time series.
     */
    public void addSeries(TimePeriodValues series) {

        // check argument...
        if (series == null) {
            throw new IllegalArgumentException(
                "TimeSeriesDataset.addSeries(...): cannot add null series.");
        }

        // add the series...
        data.add(series);
        series.addChangeListener(this);
        fireDatasetChanged();

    }

    /**
     * Removes the specified series from the collection.
     *
     * @param series  the series to remove.
     */
    public void removeSeries(TimePeriodValues series) {

        // check argument...
        if (series == null) {
            throw new IllegalArgumentException(
                "TimeSeriesDataset.addSeries(...): cannot add null series.");
        }

        // remove the series...
        data.remove(series);
        series.removeChangeListener(this);
        fireDatasetChanged();

    }

    /**
     * Removes a series from the collection.
     *
     * @param index  the series index (zero-based).
     */
    public void removeSeries(int index) {

        TimePeriodValues series = getSeries(index);
        if (series != null) {
            removeSeries(series);
        }

    }

    /**
     * Returns the number of items in the specified series.
     * <P>
     * This method is provided for convenience.
     *
     * @param series The index of the series of interest (zero-based).
     *
     * @return the number of items in the specified series.
     */
    public int getItemCount(int series) {

        return getSeries(series).getItemCount();

    }

    /**
     * Returns the x-value for the specified series and item.
     *
     * @param series The series (zero-based index).
     * @param item The item (zero-based index).
     *
     * @return the x-value for the specified series and item.
     */
    public Number getXValue(int series, int item) {

        TimePeriodValues ts = (TimePeriodValues) data.get(series);
        TimePeriodValue dp = ts.getDataItem(item);
        TimePeriod period = dp.getPeriod();

        return new Long(getX(period));

    }

    /**
     * Returns the x-value for a time period.
     *
     * @param period  the time period.
     *
     * @return the x-value.
     */
    private long getX(TimePeriod period) {

        long result = 0L;
        switch (position) {
            case (START) :
                result = period.getStart().getTime();
                break;
            case (MIDDLE) :
                result = period.getStart().getTime() / 2 + period.getEnd().getTime() / 2;
                break;
            case (END) :
                result = period.getEnd().getTime();
                break;
            default:
                result = period.getStart().getTime() / 2 + period.getEnd().getTime() / 2;
        }
        return result;

    }

    /**
     * Returns the starting X value for the specified series and item.
     *
     * @param series The series (zero-based index).
     * @param item The item (zero-based index).
     *
     * @return the starting X value for the specified series and item.
     */
    public Number getStartXValue(int series, int item) {

        TimePeriodValues ts = (TimePeriodValues) data.get(series);
        TimePeriodValue dp = ts.getDataItem(item);
        return new Long(dp.getPeriod().getStart().getTime());

    }

    /**
     * Returns the ending X value for the specified series and item.
     *
     * @param series The series (zero-based index).
     * @param item  The item (zero-based index).
     *
     * @return the ending X value for the specified series and item.
     */
    public Number getEndXValue(int series, int item) {

        TimePeriodValues ts = (TimePeriodValues) data.get(series);
        TimePeriodValue dp = ts.getDataItem(item);
        return new Long(dp.getPeriod().getEnd().getTime());

    }

    /**
     * Returns the y-value for the specified series and item.
     *
     * @param series The series (zero-based index).
     * @param item The item (zero-based index).
     *
     * @return the y-value for the specified series and item.
     */
    public Number getYValue(int series, int item) {

        TimePeriodValues ts = (TimePeriodValues) data.get(series);
        TimePeriodValue dp = ts.getDataItem(item);
        return dp.getValue();

    }

    /**
     * Returns the starting Y value for the specified series and item.
     *
     * @param series The series (zero-based index).
     * @param item The item (zero-based index).
     *
     * @return the starting Y value for the specified series and item.
     */
    public Number getStartYValue(int series, int item) {
        return getYValue(series, item);
    }

    /**
     * Returns the ending Y value for the specified series and item.
     *
     * @param series The series (zero-based index).
     * @param item The item (zero-based index).
     *
     * @return the ending Y value for the specified series and item.
     */
    public Number getEndYValue(int series, int item) {
        return getYValue(series, item);
    }

    /**
     * Returns the minimum value in the dataset (or null if all the values in
     * the domain are null).
     *
     * @return the minimum value.
     */
    public Number getMinimumDomainValue() {

        Range r = getDomainRange();
        return new Double(r.getLowerBound());

    }

    /**
     * Returns the maximum value in the dataset (or null if all the values in
     * the domain are null).
     *
     * @return the maximum value.
     */
    public Number getMaximumDomainValue() {

        Range r = getDomainRange();
        return new Double(r.getUpperBound());

    }

    /**
     * Returns the range of the values in the series domain.
     *
     * @return the range.
     */
    public Range getDomainRange() {

        Range result = null;
        Range temp = null;
        Iterator iterator = data.iterator();
        while (iterator.hasNext()) {
            TimePeriodValues series = (TimePeriodValues) iterator.next();
            int count = series.getItemCount();
            if (count > 0) {
                TimePeriod start = series.getTimePeriod(0);
                TimePeriod end = series.getTimePeriod(count - 1);
                if (this.domainIsPointsInTime) {
                    temp = new Range(getX(start), getX(end));
                }
                else {
                    temp = new Range(start.getStart().getTime(), end.getEnd().getTime());
                }
                result = Range.combine(result, temp);
            }
        }

        return result;

    }

}
