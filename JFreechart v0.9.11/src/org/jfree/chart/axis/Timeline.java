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
 * -------------
 * Timeline.java
 * -------------
 * (C) Copyright 2000-2003, by Object Refinery Limited and Contributors.
 *
 * Original Author:  Bill Kelemen
 * Contributor(s):   ;
 *
 * $Id: Timeline.java,v 1.1 2007/10/10 19:09:14 vauchers Exp $
 *
 * Changes
 * -------
 * 23-May-2003 : Version 1 (BK);
 * 
 */

package org.jfree.chart.axis;

import java.util.Date;

/**
 * An interface that defines the contract for a Timeline.
 * <P>
 * A Timeline will present a series of values to be used for an axis. Each
 * Timeline must provide transformation methods between domain values and
 * timeline values. In theory many transformations are possible. This interface
 * has been implemented completelly in {@link org.jfree.chart.axis.SegmentedTimeline}.
 * <P>
 * A timeline can be used as parameter to a {@link org.jfree.chart.axis.DateAxis}
 * to define the values that this axis supports. As an example, the {@link
 * org.jfree.chart.axis.SegmentedTimeline} implements a timeline formed by segments
 * of equal length (ex. days, hours, minutes) where some segments can be included
 * in the timeline and others excluded. Therefore timelines like "working days" or
 * "working hours" can be created where non-working days or non-working hours respectively can
 * be removed from the timeline, and therefore from the axis. This creates a smooth
 * plot with equal separation between all included segments.
 * <P>
 * Because Timelines were created mainly for Date related axis, values are
 * represented as longs instead of doubles. In this case, the domain value is
 * just the number of milliseconds since January 1, 1970, 00:00:00 GMT as defined
 * by the getTime() method of {@link java.util.Date}.
 *
 * @see org.jfree.chart.axis.SegmentedTimeline
 * @see org.jfree.chart.axis.DateAxis
 *
 * @author Bill Kelemen
 */
public interface Timeline {

    /**
     * Translates a value relative to some domain into a value on this timeline.
     *
     * @param domainValue the domain value
     * @return a timeline value
     */
    public long toTimelineValue(long domainValue);

    /**
     * Translates a date relative to some domain into a value on this timeline.
     *
     * @param dateDomainValue the domain value
     * @return a timeline value
     */
    public long toTimelineValue(Date dateDomainValue);

    /**
     * Translates a value relative to this timeline into a domain value. The domain
     * value obtained by this method is not always the same domain value that
     * could have been supplied to translateDomainValueToTimelineValue(domainValue).
     * This is because the original tranformation may not be complete reversable.
     *
     * @see org.jfree.chart.axis.SegmentedTimeline
     *
     * @param timelineValue a timeline value
     * @return a domain value
     */
    public long toDomainValue(long timelineValue);

    /**
     * Returns true if a value is contained in the timeline values.
     *
     * @param domainValue value to verify
     * @return true if value is contained in the timeline or false otherwise.
     */
    public boolean containsDomainValue(long domainValue);

    /**
     * Returns true if a date is contained in the timeline values.
     *
     * @param dateDomainValue date to verify
     * @return true if value is contained in the timeline or false otherwise.
     */
    public boolean containsDomainValue(Date dateDomainValue);

    /**
     * Returns true if a range of values are contained in the timeline.
     *
     * @param fromDomainValue start of the range to verify
     * @param toDomainValue end of the range to verify
     * @return true if the range is contained in the timeline or false otherwise
     */
    public boolean containsDomainRange(long fromDomainValue, long toDomainValue);

    /**
     * Returns true if a range of dates are contained in the timeline.
     *
     * @param fromDateDomainValue start of the range to verify
     * @param toDateDomainValue end of the range to verify
     * @return true if the range is contained in the timeline or false otherwise
     */
    public boolean containsDomainRange(Date fromDateDomainValue, Date toDateDomainValue);

}
