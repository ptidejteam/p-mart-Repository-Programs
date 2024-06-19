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
 * ---------------
 * TimeSeries.java
 * ---------------
 * (C) Copyright 2001-2003, by Object Refinery Limited.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited);
 * Contributor(s):   Bryan Scott;
 *
 * $Id: TimeSeries.java,v 1.1 2007/10/10 19:29:24 vauchers Exp $
 *
 * Changes
 * -------
 * 11-Oct-2001 : Version 1 (DG);
 * 14-Nov-2001 : Added listener mechanism (DG);
 * 15-Nov-2001 : Updated argument checking and exceptions in add(...) method (DG);
 * 29-Nov-2001 : Added properties to describe the domain and range (DG);
 * 07-Dec-2001 : Renamed TimeSeries --> BasicTimeSeries (DG);
 * 01-Mar-2002 : Updated import statements (DG);
 * 28-Mar-2002 : Added a method add(TimePeriod, double) (DG);
 * 27-Aug-2002 : Changed return type of delete method to void (DG);
 * 04-Oct-2002 : Added itemCount and historyCount attributes, fixed errors reported by
 *               Checkstyle (DG);
 * 29-Oct-2002 : Added series change notification to addOrUpdate(...) method (DG);
 * 28-Jan-2003 : Changed name back to TimeSeries (DG);
 * 13-Mar-2003 : Moved to com.jrefinery.data.time package and implemented Serializable (DG);
 * 01-May-2003 : Updated equals(...) method (see bug report 727575) (DG);
 * 14-Aug-2003 : Added ageHistoryCountItems method (copied existing code for contents) made
 *               a method and added to addOrUpdate.  Made a public method to enable ageing
 *               against a specified time (eg now) as opposed to lastest time in series (BS);
 * 15-Oct-2003 : Added fix for setItemCount method - see bug report 804425.  Modified exception
 *               message in add(...) method to be more informative (DG);
 * 
 */

package org.jfree.data.time;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.jfree.data.Series;
import org.jfree.data.SeriesException;

/**
 * Represents a sequence of zero or more data items in the form (period, value).
 *
 * @author David Gilbert
 */
public class TimeSeries extends Series implements Cloneable, Serializable {

    /** Default value for the domain description. */
    protected static final String DEFAULT_DOMAIN_DESCRIPTION = "Time";

    /** Default value for the range description. */
    protected static final String DEFAULT_RANGE_DESCRIPTION = "Value";

    /** A description of the domain. */
    private String domain;

    /** A description of the range. */
    private String range;

    /** The type of period for the data. */
    protected Class timePeriodClass;

    /** The list of data pairs in the series. */
    protected List data;

    /** The maximum number of items for the series. */
    private int maximumItemCount;

    /** The history count. */
    private int historyCount;
    
    /**
     * Creates a new (empty) time series.
     * <P>
     * By default, a daily time series is created.  Use one of the other
     * constructors if you require a different time period.
     *
     * @param name  the name of the series.
     */
    public TimeSeries(String name) {

        this(name,
             DEFAULT_DOMAIN_DESCRIPTION,
             DEFAULT_RANGE_DESCRIPTION,
             Day.class);

    }

    /**
     * Creates a new  (empty) time series.
     *
     * @param name  the series name.
     * @param timePeriodClass  the type of time period.
     */
    public TimeSeries(String name, Class timePeriodClass) {

        this(name,
             DEFAULT_DOMAIN_DESCRIPTION,
             DEFAULT_RANGE_DESCRIPTION,
             timePeriodClass);

    }

    /**
     * Creates a new time series that contains no data.
     * <P>
     * Descriptions can be specified for the domain and range.  One situation
     * where this is helpful is when generating a chart for the time series -
     * axis labels can be taken from the domain and range description.
     *
     * @param name  the name of the series.
     * @param domain  the domain description.
     * @param range  the range description.
     * @param timePeriodClass  the type of time period.
     */
    public TimeSeries(String name, String domain, String range, Class timePeriodClass) {

        super(name);
        this.domain = domain;
        this.range = range;
        this.timePeriodClass = timePeriodClass;
        this.data = new java.util.ArrayList();
        this.maximumItemCount = Integer.MAX_VALUE;
        this.historyCount = 0;

    }

    /**
     * Returns the domain description.
     *
     * @return the domain description.
     */
    public String getDomainDescription() {
        return this.domain;
    }

    /**
     * Sets the domain description.
     * <P>
     * A property change event is fired, and an undoable edit is posted.
     *
     * @param description  the new description.
     */
    public void setDomainDescription(String description) {

        String old = this.domain;
        this.domain = description;
        firePropertyChange("Domain", old, description);

    }

    /**
     * Returns the range description.
     *
     * @return the range description.
     */
    public String getRangeDescription() {
        return this.range;
    }

    /**
     * Sets the range description.
     * <P>
     * Registered listeners are notified of the change.
     *
     * @param description  the new description.
     */
    public void setRangeDescription(String description) {

        String old = this.range;
        this.range = description;
        firePropertyChange("Range", old, description);

    }

    /**
     * Returns the number of items in the series.
     *
     * @return the item count.
     */
    public int getItemCount() {
        return this.data.size();
    }

    /**
     * Returns the list of data items for the series (the list contains {@link TimeSeriesDataItem}
     * objects and is unmodifiable).
     *
     * @return The list of data items.
     */
    public List getItems() {
        return Collections.unmodifiableList(this.data);
    }

    /**
     * Returns the maximum number of items that will be retained in the series.
     * <P>
     * The default value is <code>Integer.MAX_VALUE</code>).
     *
     * @return The maximum item count.
     */
    public int getMaximumItemCount() {
        return this.maximumItemCount;
    }

    /**
     * Sets the maximum number of items that will be retained in the series.
     * <P>
     * If you add a new item to the series such that the number of items will exceed the
     * maximum item count, then the FIRST element in the series is automatically removed,
     * ensuring that the maximum item count is not exceeded.
     *
     * @param maximum  the maximum.
     */
    public void setMaximumItemCount(int maximum) {
        this.maximumItemCount = maximum;
        while (this.data.size() > this.maximumItemCount) {
            this.data.remove(0);
        }
    }

    /**
     * Returns the history count for the series.
     *
     * @return the history count.
     */
    public int getHistoryCount() {
        return this.historyCount;
    }

    /**
     * Sets the number of time units in the 'history' for the series.
     * <P>
     * This provides one mechanism for automatically dropping old data from the time series.
     * For example, if a series contains daily data, you might set the history count to 30.
     * Then, when you add a new data item, all data items more than 30 days older than the latest
     * value are automatically dropped from the series.
     *
     * @param periods  the number of time periods.
     */
    public void setHistoryCount(int periods) {
        this.historyCount = periods;
    }

    /**
     * Returns the time period class for this series.
     * <p>
     * Only one time period class can be used within a single series (enforced).
     * If you add a data item with a Year for the time period, then all
     * subsequent data items must also have a Year for the time period.
     *
     * @return the time period class for this series (null if the series is empty).
     */
    public Class getTimePeriodClass() {
        return this.timePeriodClass;
    }

    /**
     * Returns one data pair for the series.
     *
     * @param index  the item index (zero-based).
     *
     * @return one data pair for the series.
     * @deprecated Use getDataItem(int).
     */
    public TimeSeriesDataItem getDataPair(int index) {
        return getDataItem(index);
    }

    /**
     * Returns one data item for the series.
     *
     * @param index  the item index (zero-based).
     *
     * @return One data item for the series.
     */
    public TimeSeriesDataItem getDataItem(int index) {
        return (TimeSeriesDataItem) this.data.get(index);
    }

    /**
     * Returns the data pair for a specific period.
     *
     * @param period  the period of interest.
     *
     * @return the data pair matching the specified period (or null if there is no match).
     * @deprecated Use getDataItem(RegularTimePeriod).
     *
     */
    public TimeSeriesDataItem getDataPair(RegularTimePeriod period) {
        return getDataItem(period);
    }

    /**
     * Returns the data item for a specific period.
     *
     * @param period  the period of interest (<code>null</code> not allowed).
     *
     * @return The data item matching the specified period (or <code>null</code> if there is
     *         no match).
     *
     */
    public TimeSeriesDataItem getDataItem(RegularTimePeriod period) {

        // check arguments...
        if (period == null) {
            throw new IllegalArgumentException(
                "TimeSeries.getDataItem(...): null time period not allowed.");
        }

        // fetch the value...
        TimeSeriesDataItem dummy = new TimeSeriesDataItem(period, new Integer(0));
        int index = Collections.binarySearch(this.data, dummy);
        if (index >= 0) {
            return (TimeSeriesDataItem) this.data.get(index);
        }
        else {
            return null;
        }

    }

    /**
     * Returns the time period at the specified index.
     *
     * @param index  the index of the data pair.
     *
     * @return the time period at the specified index.
     */
    public RegularTimePeriod getTimePeriod(int index) {
        return getDataPair(index).getPeriod();
    }

    /**
     * Returns a time period that would be the next in sequence on the end of
     * the time series.
     *
     * @return the next time period.
     */
    public RegularTimePeriod getNextTimePeriod() {

        RegularTimePeriod last = getTimePeriod(getItemCount() - 1);
        return last.next();

    }

    /**
     * Returns a collection of all the time periods in the time series.
     *
     * @return a collection of all the time periods.
     */
    public Collection getTimePeriods() {

        Collection result = new java.util.ArrayList();

        for (int i = 0; i < getItemCount(); i++) {
            result.add(getTimePeriod(i));
        }

        return result;

    }

    /**
     * Returns a collection of time periods in the specified series, but not in
     * this series, and therefore unique to the specified series.
     *
     * @param series  the series to check against this one.
     *
     * @return the series minus 'this'.
     */
    public Collection getTimePeriodsUniqueToOtherSeries(TimeSeries series) {

        Collection result = new java.util.ArrayList();

        for (int i = 0; i < series.getItemCount(); i++) {
            RegularTimePeriod period = series.getTimePeriod(i);
            int index = getIndex(period);
            if (index < 0) {
                result.add(period);
            }

        }

        return result;

    }

    /**
     * Returns the index for the item (if any) that corresponds to a time period.
     *
     * @param period  the time period (<code>null</code> not permitted).
     *
     * @return The index.
     */
    public int getIndex(RegularTimePeriod period) {

        // check argument...
        if (period == null) {
            throw new IllegalArgumentException("TimeSeries.getIndex(...) : null not permitted.");
        }
        
        // fetch the value...
        TimeSeriesDataItem dummy = new TimeSeriesDataItem(period, new Integer(0));
        int index = Collections.binarySearch(this.data, dummy);
        return index;

    }

    /**
     * Returns the value at the specified index.
     *
     * @param index  index of a value.
     *
     * @return the value at the specified index.
     */
    public Number getValue(int index) {
        return getDataPair(index).getValue();
    }

    /**
     * Returns the value for a time period.
     *
     * @param period  time period to lookup.
     *
     * @return the value or <code>null</code> if the time period is not in the series.
     */
    public Number getValue(RegularTimePeriod period) {

        int index = getIndex(period);
        if (index >= 0) {
            return getValue(index);
        }
        else {
            return null;
        }

    }

    /**
     * Adds a data item to the series.
     *
     * @param pair  the (timeperiod, value) pair.
     *
     * @throws SeriesException if there is a problem adding the data.
     */
    public void add(TimeSeriesDataItem pair) throws SeriesException {

        // check arguments...
        if (pair == null) {
            throw new IllegalArgumentException("TimeSeries.add(...): null item not allowed.");
        }

        if (!pair.getPeriod().getClass().equals(this.timePeriodClass)) {
            String message = "TimeSeries.add(): you are trying to add data where the time ";
            message = message + "period class is " + pair.getPeriod().getClass().getName() + ", ";
            message = message + "but the TimeSeries is expecting an instance of "
                              + this.timePeriodClass.getName() + ".";
            throw new SeriesException(message);
        }


        // make the change (if it's not a duplicate time period)...
        int index = Collections.binarySearch(this.data, pair);
        if (index < 0) {
            this.data.add(-index - 1, pair);

            // check if this addition will exceed the maximum item count...
            if (getItemCount() > this.maximumItemCount) {
                this.data.remove(0);
            }

            // check if there are any values earlier than specified by the history count...
            ageHistoryCountItems();
            fireSeriesChanged();
        }
        else {
            String message = "TimeSeries.add(...): you are attempting to add an observation for ";
            message = message + "the time period " + pair.getPeriod().toString() + " but the ";
            message = message + "series already contains an observation for that time period.  ";
            message = message + "Duplicates are not permitted.";
            throw new SeriesException(message);
        }

    }

    /**
     * Adds a new data item to the series.
     *
     * @param period  the time period.
     * @param value  the value.
     *
     * @throws SeriesException if there is a problem adding the data.
     */
    public void add(RegularTimePeriod period, double value) throws SeriesException {

        TimeSeriesDataItem pair = new TimeSeriesDataItem(period, value);
        add(pair);

    }

    /**
     * Adds a new data item to the series.
     *
     * @param period  the time period.
     * @param value  the value.
     *
     * @throws SeriesException if there is a problem adding the data.
     */
    public void add(RegularTimePeriod period, Number value) throws SeriesException {

        TimeSeriesDataItem pair = new TimeSeriesDataItem(period, value);
        add(pair);

    }

    /**
     * Updates (changes) the value for a time period.  Ignores the update if
     * the period does not exist.
     *
     * @param period  the period to update.
     * @param value  the new value.
     *
     * @throws SeriesException if there is a problem adding the data.
     */
    public void update(RegularTimePeriod period, Number value) throws SeriesException {

        TimeSeriesDataItem temp = new TimeSeriesDataItem(period, value);
        int index = Collections.binarySearch(this.data, temp);
        if (index >= 0) {
            TimeSeriesDataItem pair = (TimeSeriesDataItem) this.data.get(index);
            pair.setValue(value);
            fireSeriesChanged();
        }
        else {
            throw new SeriesException("TimeSeries.update(TimePeriod, Number): "
                                      + "period does not exist.");
        }

    }

    /**
     * Updates (changes) the value of a data pair.
     *
     * @param index  the index of the data pair to update.
     * @param value  the new value.
     */
    public void update(int index, Number value) {

        TimeSeriesDataItem pair = getDataPair(index);
        pair.setValue(value);
        fireSeriesChanged();

    }

    /**
     * Adds or updates data from one series to another.  Returns another series
     * containing the values that were overwritten.
     *
     * @param series  the series to merge with this.
     *
     * @return series containing the values that were overwritten.
     */
    public TimeSeries addAndOrUpdate(TimeSeries series) {

        TimeSeries overwritten = new TimeSeries("Overwritten values from: " + getName());

        for (int i = 0; i < series.getItemCount(); i++) {
            TimeSeriesDataItem pair = series.getDataPair(i);
            TimeSeriesDataItem oldPair = addOrUpdate(pair.getPeriod(), pair.getValue());
            if (oldPair != null) {
                try {
                    overwritten.add(oldPair);
                }
                catch (SeriesException e) {  // should not get here...
                    System.err.println("TimeSeries.addAndOrUpdate(series): "
                                       + "unable to add data to overwritten series.");
                }
            }
        }
        return overwritten;
    }

    /**
     * Adds or updates the times series.
     *
     * @param period  the time period to add/update.
     * @param value  the new value.
     *
     * @return a copy of the overwritten data pair (or null).
     */
    public TimeSeriesDataItem addOrUpdate(RegularTimePeriod period, Number value) {

        TimeSeriesDataItem overwritten = null;

        TimeSeriesDataItem key = new TimeSeriesDataItem(period, value);
        int index = Collections.binarySearch(this.data, key);
        if (index >= 0) {
            TimeSeriesDataItem existing = (TimeSeriesDataItem) this.data.get(index);
            overwritten = (TimeSeriesDataItem) existing.clone();
            existing.setValue(value);
            ageHistoryCountItems();
            fireSeriesChanged();
        }
        else {
            this.data.add(-index - 1, new TimeSeriesDataItem(period, value));
            ageHistoryCountItems();
            fireSeriesChanged();
        }
        return overwritten;

    }

    /**
     * age items in the series.  Ensure that the timespan from the youngest to the oldest record
     * in the series does not exceed history count.  oldest items will be removed if required
     */
    public void ageHistoryCountItems() {
      // check if there are any values earlier than specified by the history count...
      if ((getItemCount() > 1) && (this.historyCount > 0)) {
        long latest = getTimePeriod(getItemCount() - 1).getSerialIndex();
        while ((latest - getTimePeriod(0).getSerialIndex()) >= this.historyCount) {
          this.data.remove(0);
        }
      }
    }

    /**
     * age items in the series.  Ensure that the timespan from the supplied time to the
     * oldest record in the series does not exceed history count.  oldest items will be
     * removed if required
     *
     * @param latest the time to be compared against when aging data.
     */
    public void ageHistoryCountItems(long latest) {
      // check if there are any values earlier than specified by the history count...
      if ((getItemCount() > 1) && (this.historyCount > 0)) {
        while ((latest - getTimePeriod(0).getSerialIndex()) >= this.historyCount) {
          this.data.remove(0);
        }
      }
    }

    /**
     * Deletes data for the given time period.
     *
     * @param period  period to delete.
     */
    public void delete(RegularTimePeriod period) {
        int index = getIndex(period);
        this.data.remove(index);
    }

    /**
     * Deletes data from start until end index (end inclusive).
     *
     * @param start  the index of the first period to delete.
     * @param end  the index of the last period to delete.
     */
    public void delete(int start, int end) {

        for (int i = 0; i <= (end - start); i++) {
            this.data.remove(start);
        }
        fireSeriesChanged();

    }

    /**
     * Returns a clone of the time series.
     * <P>
     * Notes:
     * <ul>
     *   <li>no need to clone the domain and range descriptions, since String object is 
     *     immutable;</li>
     *   <li>we pass over to the more general method clone(start, end).</li>
     * </ul>
     *
     * @return a clone of the time series.
     * 
     * @throws CloneNotSupportedException not thrown by this class, but subclasses may differ.
     */
    public Object clone() throws CloneNotSupportedException {

        Object clone = createCopy(0, getItemCount() - 1);
        return clone;

    }

    /**
     * Creates a new timeseries by copying a subset of the data in this time
     * series.
     *
     * @param start  the index of the first time period to copy.
     * @param end  the index of the last time period to copy.
     *
     * @return a series containing a copy of this times series from start until end.
     * 
     * @throws CloneNotSupportedException if there is a cloning problem.
     */
    public TimeSeries createCopy(int start, int end) throws CloneNotSupportedException {

        TimeSeries copy = (TimeSeries) super.clone();

        copy.data = new java.util.ArrayList();
        if (this.data.size() > 0) {
            for (int index = start; index <= end; index++) {
                TimeSeriesDataItem item = (TimeSeriesDataItem) this.data.get(index);
                TimeSeriesDataItem clone = (TimeSeriesDataItem) item.clone();
                try {
                    copy.add(clone);
                }
                catch (SeriesException e) {
                    System.err.println("TimeSeries.createCopy(): unable to add cloned data item.");
                }
            }
        }

        return copy;

    }

    /**
     * Creates a new timeseries by copying a subset of the data in this time series.
     *
     * @param start  the first time period to copy.
     * @param end  the last time period to copy.
     *
     * @return a time series containing a copy of this time series from start until end.
     * 
     * @throws CloneNotSupportedException if there is a cloning problem.
     */
    public TimeSeries createCopy(RegularTimePeriod start, RegularTimePeriod end) 
        throws CloneNotSupportedException {

        int startIndex = getIndex(start);
        if (startIndex < 0) {
            startIndex = -(startIndex + 1);
        }
        int endIndex = getIndex(end);
        if (endIndex < 0) {               // end period is not in original series
            endIndex = -(endIndex + 1);  // this gives first item AFTER end period
            endIndex = endIndex - 1;      // so this gives last item BEFORE end period
        }
        
        TimeSeries result = createCopy(startIndex, endIndex);        
        
        return result;

    }

    /**
     * Tests the series for equality with another object.
     *
     * @param object  the object to test against (<code>null</code> permitted).
     *
     * @return <code>true</code> or <code>false</code>.
     */
    public boolean equals(Object object) {

        if (object == null) {
            return false;
        }

        if (object == this) {
            return true;
        }

        if (super.equals(object) == false) {
            return false;
        }

        if (object instanceof TimeSeries == false) {
            return false;
        }

        TimeSeries s = (TimeSeries) object;
        if (getDomainDescription().equals(s.getDomainDescription()) == false) {
            return false;
        }

        if (getRangeDescription().equals(s.getRangeDescription()) == false) {
            return false;
        }

        if (getClass().equals(s.getClass()) == false) {
            return false;
        }

        if (getHistoryCount() != s.getHistoryCount()) {
            return false;
        }

        if (getMaximumItemCount() != s.getMaximumItemCount()) {
            return false;
        }

        int count = getItemCount();
        if (count != s.getItemCount()) {
            return false;
        }
        for (int i = 0; i < count; i++) {
            if (getDataItem(i).equals(s.getDataItem(i)) == false) {
                return false;
            }
        }
        return true;
    }

    /**
     * Returns a hash code value for the object.
     *
     * @return the hashcode
     */
    public int hashCode() {
        int result;
        result = (this.domain != null ? this.domain.hashCode() : 0);
        result = 29 * result + (this.range != null ? this.range.hashCode() : 0);
        result = 29 * result + (this.timePeriodClass != null ? this.timePeriodClass.hashCode() : 0);
        result = 29 * result + this.data.hashCode();
        result = 29 * result + this.maximumItemCount;
        result = 29 * result + this.historyCount;
        return result;
    }
}
