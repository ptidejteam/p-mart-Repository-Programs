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
 * $Id: TimeSeries.java,v 1.1 2007/10/10 19:46:29 vauchers Exp $
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
 * 13-Apr-2004 : Added clear() method (DG);
 * 21-May-2004 : Added an extra addOrUpdate() method (DG);
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

    /** The list of data items in the series. */
    protected List data;

    /** The maximum number of items for the series. */
    private int maximumItemCount;

    /** The history count. */
    private int historyCount;
    
    /**
     * Creates a new (empty) time series.  By default, a daily time series is created.  
     * Use one of the other constructors if you require a different time period.
     *
     * @param name  the series name (<code>null</code> not permitted).
     */
    public TimeSeries(final String name) {
        this(name, DEFAULT_DOMAIN_DESCRIPTION, DEFAULT_RANGE_DESCRIPTION, Day.class);
    }

    /**
     * Creates a new  (empty) time series.
     *
     * @param name  the series name (<code>null</code> not permitted).
     * @param timePeriodClass  the type of time period (<code>null</code> not permitted).
     */
    public TimeSeries(final String name, final Class timePeriodClass) {
        this(name, DEFAULT_DOMAIN_DESCRIPTION, DEFAULT_RANGE_DESCRIPTION, timePeriodClass);
    }

    /**
     * Creates a new time series that contains no data.
     * <P>
     * Descriptions can be specified for the domain and range.  One situation
     * where this is helpful is when generating a chart for the time series -
     * axis labels can be taken from the domain and range description.
     *
     * @param name  the name of the series (<code>null</code> not permitted).
     * @param domain  the domain description (<code>null</code> permitted).
     * @param range  the range description (<code>null</code> permitted).
     * @param timePeriodClass  the type of time period (<code>null</code> not permitted).
     */
    public TimeSeries(final String name, final String domain, final String range, 
                      final Class timePeriodClass) {

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
     * @return The domain description (possibly <code>null</code>).
     */
    public String getDomainDescription() {
        return this.domain;
    }

    /**
     * Sets the domain description.
     * <P>
     * A property change event is fired, and an undoable edit is posted.
     *
     * @param description  the description (<code>null</code> permitted).
     */
    public void setDomainDescription(final String description) {
        final String old = this.domain;
        this.domain = description;
        firePropertyChange("Domain", old, description);
    }

    /**
     * Returns the range description.
     *
     * @return The range description (possibly <code>null</code>).
     */
    public String getRangeDescription() {
        return this.range;
    }

    /**
     * Sets the range description and fires a property change event for the 'Range' 
     * property.
     *
     * @param description  the description (<code>null</code> permitted).
     */
    public void setRangeDescription(final String description) {
        final String old = this.range;
        this.range = description;
        firePropertyChange("Range", old, description);
    }

    /**
     * Returns the number of items in the series.
     *
     * @return The item count.
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
    public void setMaximumItemCount(final int maximum) {
        this.maximumItemCount = maximum;
        while (this.data.size() > this.maximumItemCount) {
            this.data.remove(0);
        }
    }

    /**
     * Returns the history count for the series.
     *
     * @return The history count.
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
    public void setHistoryCount(final int periods) {
        this.historyCount = periods;
    }

    /**
     * Returns the time period class for this series.
     * <p>
     * Only one time period class can be used within a single series (enforced).
     * If you add a data item with a {@link Year} for the time period, then all
     * subsequent data items must also have a {@link Year} for the time period.
     *
     * @return The time period class (never <code>null</code>).
     */
    public Class getTimePeriodClass() {
        return this.timePeriodClass;
    }

    /**
     * Returns a data item for the series.
     *
     * @param index  the item index (zero-based).
     *
     * @return The data item.
     */
    public TimeSeriesDataItem getDataItem(final int index) {
        return (TimeSeriesDataItem) this.data.get(index);
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
    public TimeSeriesDataItem getDataItem(final RegularTimePeriod period) {

        // check arguments...
        if (period == null) {
            throw new IllegalArgumentException("Null 'period' argument");
        }

        // fetch the value...
        final TimeSeriesDataItem dummy = new TimeSeriesDataItem(period, new Integer(0));
        final int index = Collections.binarySearch(this.data, dummy);
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
     * @param index  the index of the data item.
     *
     * @return The time period.
     */
    public RegularTimePeriod getTimePeriod(final int index) {
        return getDataItem(index).getPeriod();
    }

    /**
     * Returns a time period that would be the next in sequence on the end of
     * the time series.
     *
     * @return The next time period.
     */
    public RegularTimePeriod getNextTimePeriod() {
        final RegularTimePeriod last = getTimePeriod(getItemCount() - 1);
        return last.next();
    }

    /**
     * Returns a collection of all the time periods in the time series.
     *
     * @return A collection of all the time periods.
     */
    public Collection getTimePeriods() {
        final Collection result = new java.util.ArrayList();
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
     * @return The unique time periods.
     */
    public Collection getTimePeriodsUniqueToOtherSeries(final TimeSeries series) {

        final Collection result = new java.util.ArrayList();

        for (int i = 0; i < series.getItemCount(); i++) {
            final RegularTimePeriod period = series.getTimePeriod(i);
            final int index = getIndex(period);
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
    public int getIndex(final RegularTimePeriod period) {

        // check argument...
        if (period == null) {
            throw new IllegalArgumentException("Null 'period' argument.");
        }
        
        // fetch the value...
        final TimeSeriesDataItem dummy = new TimeSeriesDataItem(period, new Integer(0));
        final int index = Collections.binarySearch(this.data, dummy);
        return index;

    }

    /**
     * Returns the value at the specified index.
     *
     * @param index  index of a value.
     *
     * @return The value (possibly <code>null</code>).
     */
    public Number getValue(final int index) {
        return getDataItem(index).getValue();
    }

    /**
     * Returns the value for a time period.  If there is no data item with the specified period,
     * this method will return <code>null</code>.
     *
     * @param period  time period (<code>null</code> not permitted).
     *
     * @return The value (possibly <code>null</code>).
     */
    public Number getValue(final RegularTimePeriod period) {

        final int index = getIndex(period);
        if (index >= 0) {
            return getValue(index);
        }
        else {
            return null;
        }

    }

    /**
     * Adds a data item to the series and sends a {@link org.jfree.data.SeriesChangeEvent} 
     * to all registerd listeners.
     *
     * @param item  the (timeperiod, value) pair (<code>null</code> not permitted).
     */
    public void add(final TimeSeriesDataItem item) {
        if (item == null) {
            throw new IllegalArgumentException("Null 'item' argument.");
        }
        if (!item.getPeriod().getClass().equals(this.timePeriodClass)) {
            String message = "TimeSeries.add(): you are trying to add data where the time ";
            message = message + "period class is " + item.getPeriod().getClass().getName() + ", ";
            message = message + "but the TimeSeries is expecting an instance of "
                              + this.timePeriodClass.getName() + ".";
            throw new SeriesException(message);
        }


        // make the change (if it's not a duplicate time period)...
        final int index = Collections.binarySearch(this.data, item);
        if (index < 0) {
            this.data.add(-index - 1, item);

            // check if this addition will exceed the maximum item count...
            if (getItemCount() > this.maximumItemCount) {
                this.data.remove(0);
            }

            // check if there are any values earlier than specified by the history count...
            ageHistoryCountItems();
            fireSeriesChanged();
        }
        else {
            String message = "TimeSeries.add(): you are attempting to add an observation for ";
            message += "the time period " + item.getPeriod().toString() + " but the ";
            message +=  "series already contains an observation for that time period.  ";
            message +=  "Duplicates are not permitted.  Try using the addOrUpdate() method.";
            throw new SeriesException(message);
        }

    }

    /**
     * Adds a new data item to the series and sends a {@link org.jfree.data.SeriesChangeEvent} 
     * to all registerd listeners.
     *
     * @param period  the time period (<code>null</code> not permitted).
     * @param value  the value.
     */
    public void add(final RegularTimePeriod period, final double value) {
        // defer argument checking...
        final TimeSeriesDataItem item = new TimeSeriesDataItem(period, value);
        add(item);
    }

    /**
     * Adds a new data item to the series and sends a {@link org.jfree.data.SeriesChangeEvent} 
     * to all registerd listeners.
     *
     * @param period  the time period (<code>null</code> not permitted).
     * @param value  the value (<code>null</code> permitted).
     */
    public void add(final RegularTimePeriod period, final Number value) {
        // defer argument checking...
        final TimeSeriesDataItem item = new TimeSeriesDataItem(period, value);
        add(item);
    }

    /**
     * Updates (changes) the value for a time period.  Throws a {@link SeriesException} if
     * the period does not exist.
     *
     * @param period  the period (<code>null</code> not permitted).
     * @param value  the value (<code>null</code> permitted).
     */
    public void update(final RegularTimePeriod period, final Number value) {
        final TimeSeriesDataItem temp = new TimeSeriesDataItem(period, value);
        final int index = Collections.binarySearch(this.data, temp);
        if (index >= 0) {
            final TimeSeriesDataItem pair = (TimeSeriesDataItem) this.data.get(index);
            pair.setValue(value);
            fireSeriesChanged();
        }
        else {
            throw new SeriesException(
                "TimeSeries.update(TimePeriod, Number):  period does not exist."
            );
        }

    }

    /**
     * Updates (changes) the value of a data item.
     *
     * @param index  the index of the data item.
     * @param value  the new value (<code>null</code> permitted).
     */
    public void update(final int index, final Number value) {
        final TimeSeriesDataItem item = getDataItem(index);
        item.setValue(value);
        fireSeriesChanged();
    }

    /**
     * Adds or updates data from one series to another.  Returns another series
     * containing the values that were overwritten.
     *
     * @param series  the series to merge with this.
     *
     * @return A series containing the values that were overwritten.
     */
    public TimeSeries addAndOrUpdate(final TimeSeries series) {

        final TimeSeries overwritten = new TimeSeries("Overwritten values from: " + getName());

        for (int i = 0; i < series.getItemCount(); i++) {
            final TimeSeriesDataItem pair = series.getDataItem(i);
            final TimeSeriesDataItem oldPair = addOrUpdate(pair.getPeriod(), pair.getValue());
            if (oldPair != null) {
                try {
                    overwritten.add(oldPair);
                }
                catch (SeriesException e) {  // should not get here...
                    System.err.println(
                        "TimeSeries.addAndOrUpdate(series): "
                        + "unable to add data to overwritten series."
                    );
                }
            }
        }
        return overwritten;
    }

    /**
     * Adds or updates an item in the times series and sends a 
     * {@link org.jfree.data.SeriesChangeEvent} to all registered listenrs.
     *
     * @param period  the time period to add/update (<code>null</code> not permitted).
     * @param value  the new value.
     *
     * @return A copy of the overwritten data item, or <code>null</code> if no item 
     *         was overwritten.
     */
    public TimeSeriesDataItem addOrUpdate(final RegularTimePeriod period, final double value) {
        return this.addOrUpdate(period, new Double(value));    
    }
    
    /**
     * Adds or updates an item in the times series and sends a 
     * {@link org.jfree.data.SeriesChangeEvent} to all registered listenrs.
     *
     * @param period  the time period to add/update (<code>null</code> not permitted).
     * @param value  the new value (<code>null</code> permitted).
     *
     * @return A copy of the overwritten data item, or <code>null</code> if no item 
     *         was overwritten.
     */
    public TimeSeriesDataItem addOrUpdate(final RegularTimePeriod period, final Number value) {

        if (period == null) {
            throw new IllegalArgumentException("Null 'period' argument.");   
        }
        TimeSeriesDataItem overwritten = null;

        final TimeSeriesDataItem key = new TimeSeriesDataItem(period, value);
        final int index = Collections.binarySearch(this.data, key);
        if (index >= 0) {
            final TimeSeriesDataItem existing = (TimeSeriesDataItem) this.data.get(index);
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
     * Age items in the series.  Ensure that the timespan from the youngest to the oldest record
     * in the series does not exceed history count.  oldest items will be removed if required.
     */
    public void ageHistoryCountItems() {
        // check if there are any values earlier than specified by the history count...
        if ((getItemCount() > 1) && (this.historyCount > 0)) {
            final long latest = getTimePeriod(getItemCount() - 1).getSerialIndex();
            while ((latest - getTimePeriod(0).getSerialIndex()) >= this.historyCount) {
                this.data.remove(0);
            }
        }
    }

    /**
     * Age items in the series.  Ensure that the timespan from the supplied time to the
     * oldest record in the series does not exceed history count.  oldest items will be
     * removed if required.
     *
     * @param latest  the time to be compared against when aging data.
     */
    public void ageHistoryCountItems(final long latest) {
        // check if there are any values earlier than specified by the history count...
        if ((getItemCount() > 1) && (this.historyCount > 0)) {
            while ((latest - getTimePeriod(0).getSerialIndex()) >= this.historyCount) {
                this.data.remove(0);
            }
        }
    }

    /**
     * Removes all data items from the series and sends a {@link org.jfree.data.SeriesChangeEvent}
     * to all registered listeners.
     */
    public void clear() {
        if (this.data.size() > 0) {
            this.data.clear();
            fireSeriesChanged();
        }
    }

    /**
     * Deletes the data item for the given time period and sends 
     * a {@link org.jfree.data.SeriesChangeEvent} to all registered listeners.
     *
     * @param period  the period of the item to delete (<code>null</code> not permitted).
     */
    public void delete(final RegularTimePeriod period) {
        final int index = getIndex(period);
        this.data.remove(index);
        fireSeriesChanged();
    }

    /**
     * Deletes data from start until end index (end inclusive).
     *
     * @param start  the index of the first period to delete.
     * @param end  the index of the last period to delete.
     */
    public void delete(final int start, final int end) {
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
     * @return A clone of the time series.
     * 
     * @throws CloneNotSupportedException not thrown by this class, but subclasses may differ.
     */
    public Object clone() throws CloneNotSupportedException {
        final Object clone = createCopy(0, getItemCount() - 1);
        return clone;
    }

    /**
     * Creates a new timeseries by copying a subset of the data in this time
     * series.
     *
     * @param start  the index of the first time period to copy.
     * @param end  the index of the last time period to copy.
     *
     * @return A series containing a copy of this times series from start until end.
     * 
     * @throws CloneNotSupportedException if there is a cloning problem.
     */
    public TimeSeries createCopy(final int start, final int end) throws CloneNotSupportedException {

        final TimeSeries copy = (TimeSeries) super.clone();

        copy.data = new java.util.ArrayList();
        if (this.data.size() > 0) {
            for (int index = start; index <= end; index++) {
                final TimeSeriesDataItem item = (TimeSeriesDataItem) this.data.get(index);
                final TimeSeriesDataItem clone = (TimeSeriesDataItem) item.clone();
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
    public TimeSeries createCopy(final RegularTimePeriod start, final RegularTimePeriod end)
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
        
        final TimeSeries result = createCopy(startIndex, endIndex);
        
        return result;

    }

    /**
     * Tests the series for equality with an arbitrary object.
     *
     * @param object  the object to test against (<code>null</code> permitted).
     *
     * @return A boolean.
     */
    public boolean equals(final Object object) {
        if (object == this) {
            return true;
        }
        if (!(object instanceof TimeSeries) || !super.equals(object)) {
            return false;
        }
        final TimeSeries s = (TimeSeries) object;
        if (!getDomainDescription().equals(s.getDomainDescription())) {
            return false;
        }

        if (!getRangeDescription().equals(s.getRangeDescription())) {
            return false;
        }

        if (!getClass().equals(s.getClass())) {
            return false;
        }

        if (getHistoryCount() != s.getHistoryCount()) {
            return false;
        }

        if (getMaximumItemCount() != s.getMaximumItemCount()) {
            return false;
        }

        final int count = getItemCount();
        if (count != s.getItemCount()) {
            return false;
        }
        for (int i = 0; i < count; i++) {
            if (!getDataItem(i).equals(s.getDataItem(i))) {
                return false;
            }
        }
        return true;
    }

    /**
     * Returns a hash code value for the object.
     *
     * @return The hashcode
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
    
    //// DEPRECATED CODE //////////////////////////////////////////////////////////////////////////
    
    /**
     * Returns one data pair for the series.
     *
     * @param index  the item index (zero-based).
     *
     * @return one data pair for the series.
     * @deprecated Use getDataItem(int).
     */
    public TimeSeriesDataItem getDataPair(final int index) {
        return getDataItem(index);
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
    public TimeSeriesDataItem getDataPair(final RegularTimePeriod period) {
        return getDataItem(period);
    }


}
