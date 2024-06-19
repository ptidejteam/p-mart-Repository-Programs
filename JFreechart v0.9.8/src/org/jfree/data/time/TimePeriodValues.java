/* ======================================
 * JFreeChart : a free Java chart library
 * ======================================
 *
 * Project Info:  http://www.jfree.org/jfreechart/index.html
 * Project Lead:  David Gilbert (david.gilbert@object-refinery.com);
 *
 * (C) Copyright 2000-2003, by Simba Management Limited and Contributors.  All rights reserved.
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
 * TimePeriodValues.java
 * ---------------------
 * (C) Copyright 2003, by Simba Management Limited.
 *
 * Original Author:  David Gilbert (for Simba Management Limited);
 * Contributor(s):   -;
 *
 * $Id: TimePeriodValues.java,v 1.1 2007/10/10 20:03:22 vauchers Exp $
 *
 * Changes
 * -------
 * 22-Apr-2003 : Version 1 (DG);
 * 
 */

package org.jfree.data.time;

import java.io.Serializable;
import java.util.List;

import org.jfree.data.Series;
import org.jfree.data.SeriesException;

public class TimePeriodValues extends Series implements Serializable {
	
	/** Default value for the domain description. */
	protected static final String DEFAULT_DOMAIN_DESCRIPTION = "Time";

	/** Default value for the range description. */
	protected static final String DEFAULT_RANGE_DESCRIPTION = "Value";

	/** A description of the domain. */
	private String domain;

	/** A description of the range. */
	private String range;

	/** The list of data pairs in the series. */
	private List data;

	/**
	 * Creates a new (empty) collection of time period values.
	 *
	 * @param name  the name of the series.
	 */
	public TimePeriodValues(String name) {

		this(name,
			 DEFAULT_DOMAIN_DESCRIPTION,
			 DEFAULT_RANGE_DESCRIPTION);

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
	public TimePeriodValues(String name, String domain, String range) {

		super(name);
		this.domain = domain;
		this.range = range;
		data = new java.util.ArrayList();

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
		return data.size();
	}

	/**
	 * Returns one data item for the series.
	 *
	 * @param index  the item index (zero-based).
	 *
	 * @return one data item for the series.
	 */
	public TimePeriodValue getDataItem(int index) {

		return (TimePeriodValue) data.get(index);

	}

	/**
	 * Returns the time period at the specified index.
	 *
	 * @param index  the index of the data pair.
	 *
	 * @return the time period at the specified index.
	 */
	public TimePeriod getTimePeriod(int index) {
		return getDataItem(index).getPeriod();
	}

	/**
	 * Returns the value at the specified index.
	 *
	 * @param index  index of a value.
	 *
	 * @return the value at the specified index.
	 */
	public Number getValue(int index) {
		return getDataItem(index).getValue();
	}

	/**
	 * Adds a data item to the series.
	 *
	 * @param pair  the (timeperiod, value) pair.
	 *
	 * @throws SeriesException if there is a problem adding the data.
	 */
	public void add(TimePeriodValue item) throws SeriesException {

		// check arguments...
		if (item == null) {
			throw new IllegalArgumentException("TimePeriodValues.add(...): null item not allowed.");
		}

		// make the change
		this.data.add(item);

	}

	/**
	 * Adds a new data item to the series.
	 *
	 * @param period  the time period.
	 * @param value  the value.
	 *
	 * @throws SeriesException if there is a problem adding the data.
	 */
	public void add(TimePeriod period, double value) throws SeriesException {

		TimePeriodValue item = new TimePeriodValue(period, value);
		add(item);

	}

	/**
	 * Adds a new data item to the series.
	 *
	 * @param period  the time period.
	 * @param value  the value.
	 *
	 * @throws SeriesException if there is a problem adding the data.
	 */
	public void add(TimePeriod period, Number value) throws SeriesException {

		TimePeriodValue item = new TimePeriodValue(period, value);
		add(item);

	}

	/**
	 * Updates (changes) the value of a data item.
	 *
	 * @param index  the index of the data item to update.
	 * @param value  the new value.
	 */
	public void update(int index, Number value) {

		TimePeriodValue item = getDataItem(index);
		item.setValue(value);
		fireSeriesChanged();

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

}
