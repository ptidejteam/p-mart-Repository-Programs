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
 * $Id: TimePeriodValuesCollection.java,v 1.1 2007/10/10 19:15:32 vauchers Exp $
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

    /** 
     * Useful constant for controlling the x-value returned for a time period. 
     * @deprecated Replaced by TimePeriodAnchor.START.
     */
    public static final int START = 0;

    /** 
     * Useful constant for controlling the x-value returned for a time period. 
     * @deprecated Replaced by TimePeriodAnchor.MIDDLE.
     */
    public static final int MIDDLE = 1;

    /** 
     * Useful constant for controlling the x-value returned for a time period. 
     * @deprecated Replaced by TimePeriodAnchor.END.
     */
    public static final int END = 2;

    /** Storage for the time series. */
    private List data;

    /** The position within a time period to return as the x-value (START, MIDDLE or END). */
    private TimePeriodAnchor xPosition;
    
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
        this((TimePeriodValues) null);
    }

    /**
     * Constructs an empty dataset, tied to a specific timezone.
     *
     * @param zone the timezone.
     * @deprecated The TimeZone is ignored, use an alternative constructor.
     */
    public TimePeriodValuesCollection(TimeZone zone) {
        this(null, zone);
    }

    /**
     * Constructs a dataset containing a single series.  Additional series can be added.
     *
     * @param series  the series.
     */
    public TimePeriodValuesCollection(TimePeriodValues series) {
        this.data = new java.util.ArrayList();
        this.xPosition = TimePeriodAnchor.MIDDLE;
        this.domainIsPointsInTime = true;
        if (series != null) {
            data.add(series);
            series.addChangeListener(this);
        }
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
     * 
     * @deprecated Use getXPosition().
     */
    public int getPosition() {
        TimePeriodAnchor anchor = getXPosition();
        if (anchor == TimePeriodAnchor.START) {
            return START;
        }
        else if (anchor == TimePeriodAnchor.MIDDLE) {
            return MIDDLE;
        }
        else if (anchor == TimePeriodAnchor.END) {
            return END;
        }
        return MIDDLE;
    }
    
    /**
     * Sets the position - this controls the x-value that is returned for a
     * particular time period.
     * <P>
     * Use the constants START, MIDDLE and END.
     *
     * @param position the position.
     * 
     * @deprecated Use setXPosition(...).
     */
    public void setPosition(int position) {
        if (position == START) {
            setXPosition(TimePeriodAnchor.START);
        }
        else if (position == MIDDLE) {
            setXPosition(TimePeriodAnchor.MIDDLE);
        }
        else if (position == END) {
            setXPosition(TimePeriodAnchor.END);
        }
    }

    /**
     * Returns the position of the X value within each time period.
     * 
     * @return The position.
     */
    public TimePeriodAnchor getXPosition() {
        return this.xPosition;
    }

    /**
     * Sets the position of the x axis within each time period.
     * 
     * @param position  the position.
     */
    public void setXPosition(TimePeriodAnchor position) {
        this.xPosition = position;
    }
    
    /**
     * Returns a flag that controls whether the domain is treated as 'points in time'.
     * <P>
     * This flag is used when determining the max and min values for the domain.  If true, then
     * only the x-values are considered for the max and min values.  If false, then the start and
     * end x-values will also be taken into consideration
     *
     * @return The flag.
     */
    public boolean getDomainIsPointsInTime() {
        return this.domainIsPointsInTime;
    }

    /**
     * Sets a flag that controls whether the domain is treated as 'points in time', or time
     * periods.
     *
     * @param flag  the new value of the flag.
     */
    public void setDomainIsPointsInTime(boolean flag) {
        this.domainIsPointsInTime = flag;
    }

    /**
     * Returns the number of series in the collection.
     *
     * @return The series count.
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
     * Adds a series to the collection.  A {@link org.jfree.data.DatasetChangeEvent} is 
     * sent to all registered listeners.
     *
     * @param series  the time series.
     */
    public void addSeries(TimePeriodValues series) {

        // check argument...
        if (series == null) {
            throw new IllegalArgumentException(
                "TimePeriodValuesCollection.addSeries(...): cannot add null series.");
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
                "TimePeriodValuesCollection.addSeries(...): cannot add null series.");
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
        if (this.xPosition == TimePeriodAnchor.START) {
            result = period.getStart().getTime();
        }
        else if (this.xPosition == TimePeriodAnchor.MIDDLE) {
            result = period.getStart().getTime() / 2 + period.getEnd().getTime() / 2;
        }
        else if (this.xPosition == TimePeriodAnchor.END) {
            result = period.getEnd().getTime();
        }
        else {
            throw new IllegalStateException("TimePeriodValuesCollection.getX(...).");
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
                TimePeriod start = series.getTimePeriod(series.getMinStartIndex());
                TimePeriod end = series.getTimePeriod(series.getMaxEndIndex());
                if (this.domainIsPointsInTime) {
                    if (this.xPosition == TimePeriodAnchor.START) {
                        TimePeriod maxStart = series.getTimePeriod(series.getMaxStartIndex());
                        temp = new Range(start.getStart().getTime(), maxStart.getStart().getTime());
                    }
                    else if (this.xPosition == TimePeriodAnchor.MIDDLE) {
                        TimePeriod minMiddle = series.getTimePeriod(series.getMinMiddleIndex());
                        long s1 = minMiddle.getStart().getTime();
                        long e1 = minMiddle.getEnd().getTime();
                        TimePeriod maxMiddle = series.getTimePeriod(series.getMaxMiddleIndex());
                        long s2 = maxMiddle.getStart().getTime();
                        long e2 = maxMiddle.getEnd().getTime();
                        temp = new Range(s1 + (e1 - s1) / 2, s2 + (e2 - s2) / 2);
                    }
                    else if (this.xPosition == TimePeriodAnchor.END) {
                        TimePeriod minEnd = series.getTimePeriod(series.getMinEndIndex());
                        temp = new Range(minEnd.getEnd().getTime(), end.getEnd().getTime());
                    }
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
