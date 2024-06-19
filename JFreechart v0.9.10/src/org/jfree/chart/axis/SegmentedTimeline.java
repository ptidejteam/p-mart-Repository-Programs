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
 * -----------------------
 * SegmentedTimeline.java
 * -----------------------
 * (C) Copyright 2003, by Bill Kelemen and Contributors.
 *
 * Original Author:  Bill Kelemen;
 * Contributor(s):   ;
 *
 * $Id: SegmentedTimeline.java,v 1.1 2007/10/10 19:05:11 vauchers Exp $
 *
 * Changes
 * -------
 * 23-May-2003 : Version 1 (BK);
 */

package org.jfree.chart.axis;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;
import java.util.SimpleTimeZone;
import java.util.TimeZone;

/**
 * A {@link Timeline} that implements a "segmented" timeline with included, excluded and
 * exception segments.
 * <P>
 * A Timeline will present a series of values to be used for an axis. Each
 * Timeline must provide transformation methods between domain values and
 * timeline values.
 * <P>
 * A timeline can be used as parameter to a {@link org.jfree.chart.axis.DateAxis}
 * to define the values that this axis supports. This class implements a timeline formed by segments
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
 * <P>
 * In this class, a segment is defined as a unit of time of fixed length. Examples of segments
 * are: days, hours, minutes, etc. The size of a segment is defined as the number
 * of milliseconds in the segment. Some useful segment sizes are defined as constants
 * in this class: DAY_SEGMENT_SIZE, HOUR_SEGMENT_SIZE, FIFTEEN_MINUTE_SEGMENT_SIZE and
 * MINUTE_SEGMENT_SIZE.
 * <P>
 * Segments are group together to form a Segment Group. Each Segment Group will
 * contain a number of Segments included and a number of Segments excluded. This
 * Segment Group structure will repeat for the whole timeline.
 * <P>
 * For example, a working days SegmentedTimeline would be formed by a group of
 * 7 daily segments, where there are 5 included (Monday through Friday) and 2
 * excluded (Saturday and Sunday) segments.
 * <P>
 * Following is a diagram that explains the major attributes that define a segment.
 * Each box is one segment and must be of fixed length (ms, second, hour, day, etc).
 * <p>
 * <pre>
 * start time
 *   |
 *   v
 *   0  1  2  3  4  5  6  7  8  9 10 11 12 13 14 15 16 17 18 19 20 ...
 * +--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+...
 * |  |  |  |  |  |EE|EE|  |  |  |  |  |EE|EE|  |  |  |  |  |EE|EE|
 * +--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+...
 *  \____________/ \___/            \_/
 *        \/         |               |
 *     included   excluded        segment
 *     segments   segments         size
 *  \_________  _______/
 *            \/
 *       segment group
 * </pre>
 * Legend:<br>
 * &lt;space&gt; = Included segment<br>
 * EE      = Excluded segments in the base timeline<br>
 * <p>
 * In the example, the following segment attributes are presented:
 * <ul>
 * <li>segment size: the size of each segment in ms.
 * <li>start time: the start of the first segment of the first segment group to consider.
 * <li>included segments: the number of segments to include in the group.
 * <li>excluded segments: the number of segments to exclude in the group.
 * </ul>
 * <p>
 * Exception Segments are allowed. These exception segments are defined as
 * segments that would have been in the included segments of the Segment Group,
 * but should be excluded for special reasons. In the previous working days
 * SegmentedTimeline example, holidays would be considered exceptions.
 * <P>
 * Additionally the <code>startTime</code>, or start of the first Segment of the smallest
 * segment group needs to be defined. This startTime could be relative to January 1, 1970,
 * 00:00:00 GMT or any other date. This creates a point of reference to start counting Segment
 * Groups. For example, for the working days SegmentedTimeline, the <code>startTime</code>
 * could be 00:00:00 GMT of the first Monday after January 1, 1970. In this class, the constant
 * FIRST_MONDAY_AFTER_1900 refers to a reference poing of the first Monday of the last century.
 * <p>
 * A SegmentedTimeline can include a baseTimeline. This combination of timelines allows
 * the creation of more complex timelines. For example, in order to
 * implement a SegmentedTimeline for an intraday stock trading application, where
 * the trading period is defined as 9:00 AM through 4:00 PM Monday through Friday,
 * two SegmentedTimelines are used. The first one (the baseTimeline) would be a
 * a working day SegmentedTimeline (daily timeline Monday through Friday). On top
 * of this baseTimeline, a second one is defined that maps the 9:00 AM to 4:00 PM
 * period. Because the baseTimeline defines a timeline of Monday through
 * Friday, the resulting (combined) timeline will expose the period 9:00 AM through
 * 4:00 PM only on Monday through Friday, and will remove all other intermediate
 * intervals.
 * <P>
 * Two factory methods newMondayThroughFridayTimeline() and
 * newFifteenMinuteTimeline() are provided as examples to create special
 * SegmentdTimelines.
 *
 * @see org.jfree.chart.axis.DateAxis
 *
 * @author Bill Kelemen
 */
public class SegmentedTimeline implements Timeline, Serializable {

    ////////////////////////////////////////////////////////////////////////////
    // predetermined segments sizes
    ////////////////////////////////////////////////////////////////////////////

    /** Defines a day segment size in ms. */
    public static final long DAY_SEGMENT_SIZE = 24 * 60 * 60 * 1000;

    /** Defines a one hour segment size in ms. */
    public static final long HOUR_SEGMENT_SIZE = 60 * 60 * 1000;

    /** Defines a 15-minute segment size in ms. */
    public static final long FIFTEEN_MINUTE_SEGMENT_SIZE = 15 * 60 * 1000;

    /** Defines a one-minute segment size in ms. */
    public static final long MINUTE_SEGMENT_SIZE = 60 * 1000;

    ////////////////////////////////////////////////////////////////////////////
    // other constants
    ////////////////////////////////////////////////////////////////////////////

    /**
     * Utility constant that defines the startTime as the first monday after 1/1/1970.
     * This should be used when creating a SegmentedTimeline for Monday through
     * Friday. See static block below for calculation of this constant.
     */
    public static long FIRST_MONDAY_AFTER_1900;

    /**
     * Utility TimeZone object that has no DST and an offset equal to the default
     * TimeZone. This allows easy arithmetic between days as each one will have
     * equal size.
     */
    public static TimeZone NO_DST_TIME_ZONE;

    /**
     * This is the default time zone where the application is running. See getTime() below
     * where we make use of certain transformations between times in the default time zone and
     * the no-dst time zone used for our calculations.
     */
    public static TimeZone DEFAULT_TIME_ZONE = TimeZone.getDefault();

    /**
     * This will be a utility calendar that has no DST but is shifted relative to
     * the default time zone's offset.
     */
    private Calendar workingCalendarNoDST = new GregorianCalendar(NO_DST_TIME_ZONE);

    /**
     * This will be a utility calendar that used the default time zone.
     */
    private Calendar workingCalendar = Calendar.getInstance();

    ////////////////////////////////////////////////////////////////////////////
    // private attributes
    ////////////////////////////////////////////////////////////////////////////

    /** Segment size in ms. */
    private long segmentSize;

    /** Number of consecutive segments to include in a segment group. */
    private int segmentsIncluded;

    /** Number of consecutive segments to exclude in a segment group. */
    private int segmentsExcluded;

    /** Number of segments in a group (segmentsIncluded + segmentsExcluded). */
    private int segmentsGroup;

    /** Start of time reference from time zero (1/1/1970). This is the start of segment #0. */
    private long startTime;

    /** Consecutive ms in segmentsIncluded (segmentsIncluded * segmentSize). */
    private long segmentsIncludedSize;

    /** Consecutive ms in segmentsExcluded (segmentsExcluded * segmentSize). */
    private long segmentsExcludedSize;

    /** ms in a segment group (segmentsIncludedSize + segmentsExcludedSize). */
    private long segmentsGroupSize;

    /**
     * List of exception segments (exceptions segments that would otherwise be
     * included based on the periodic (included, excluded) grouping).
     */
    private ArrayList exceptionSegments = new ArrayList();

    /**
     * This base timeline is used to specify exceptions at a higher level. For example,
     * if we are a intraday timeline and want to exclude holidays, instead of having to
     * exclude all intraday segments for the holiday, segments from this base timeline
     * can be excluded. This baseTimeline is always optional and is only a convenience
     * method.
     * <p>
     * Additionally, all excluded segments from this baseTimeline will be considered
     * exceptions at this level.
     */
    private SegmentedTimeline baseTimeline;

    ////////////////////////////////////////////////////////////////////////////
    // static block
    ////////////////////////////////////////////////////////////////////////////

    static {
        // make a time zone with no DST for our Calendar calculations
        int offset = TimeZone.getDefault().getRawOffset();
        NO_DST_TIME_ZONE = new SimpleTimeZone(offset, "UTC-" + offset);

        // calculate midnight of first monday after 1/1/1900 relative to current locale
        Calendar cal = new GregorianCalendar(NO_DST_TIME_ZONE);
        cal.set(1900, 0, 1, 0, 0, 0);
        cal.set(Calendar.MILLISECOND, 0);
        while (cal.get(Calendar.DAY_OF_WEEK) != Calendar.MONDAY) {
            cal.add(Calendar.DATE, 1);
        }
        FIRST_MONDAY_AFTER_1900 = cal.getTime().getTime();
    }

    ////////////////////////////////////////////////////////////////////////////
    // constructors and factory methods
    ////////////////////////////////////////////////////////////////////////////

    /**
     * Constructs a new segmented timeline, optionaly using another segmented
     * timeline as its base. This chaning of SegmentedTimelines allows further
     * segmentation into smaller timelines.
     *
     * If a base
     *
     * @param segmentSize the size of a segment in ms. This time unit will be
     *        used to compute the included and excluded segments of the timeline.
     * @param segmentsIncluded Number of consecutive segments to include.
     * @param segmentsExcluded Number of consecutive segments to exclude.
     */
    public SegmentedTimeline(long segmentSize,
                             int segmentsIncluded,
                             int segmentsExcluded) {

        this.segmentSize = segmentSize;
        this.segmentsIncluded = segmentsIncluded;
        this.segmentsExcluded = segmentsExcluded;

        this.segmentsGroup = this.segmentsIncluded + this.segmentsExcluded;
        this.segmentsIncludedSize = this.segmentsIncluded * this.segmentSize;
        this.segmentsExcludedSize = this.segmentsExcluded * this.segmentSize;
        this.segmentsGroupSize = this.segmentsIncludedSize + this.segmentsExcludedSize;

    }

    /**
     * Factory method to create a Monday through Friday SegmentedTimeline.
     * <P>
     * The <code>startTime</code> of the resulting timeline will be midnight of the
     * firt Monday after 1/1/1900.
     *
     * @return A fully initialized SegmentedTimeline.
     */
    public static SegmentedTimeline newMondayThroughFridayTimeline() {
        SegmentedTimeline timeline = new SegmentedTimeline(DAY_SEGMENT_SIZE, 5, 2);
        timeline.setStartTime(FIRST_MONDAY_AFTER_1900);
        return (timeline);
    }

    /**
     * Factory method to create a 15-min, 9:00 AM thought 4:00 PM, Monday through
     * Friday SegmentedTimeline.
     * <P>
     * This timeline uses a segmentSize of FIFTEEN_MIN_SEGMENT_SIZE. The segment group
     * is defined as 28 included segments (9:00 AM through 4:00 PM) and 68 excluded
     * segments (4:00 PM through 9:00 AM the next day).
     * <P>
     * In order to exclude Saturdays and Sundays it uses a baseTimeline that only
     * includes Monday through Friday days.
     * <P>
     * The <code>startTime</code> of the resulting timeline will be 9:00 AM after
     * the startTime of the baseTimeline. This will correspond to 9:00 AM of the
     * firt Monday after 1/1/1900.
     *
     * @return A fully initialized SegmentedTimeline.
     */
    public static SegmentedTimeline newFifteenMinuteTimeline() {
        SegmentedTimeline timeline =
            new SegmentedTimeline(FIFTEEN_MINUTE_SEGMENT_SIZE, 28, 68);
        timeline.setStartTime(FIRST_MONDAY_AFTER_1900 + 36 * timeline.getSegmentSize());
        timeline.setBaseTimeline(newMondayThroughFridayTimeline());
        return (timeline);
    }

    ////////////////////////////////////////////////////////////////////////////
    // operations
    ////////////////////////////////////////////////////////////////////////////

    /**
     * Sets the startTime for the timeline. The startTime is the beginning of
     * Segment #0.
     *
     * @param startTime the start time to set
     */
    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    /**
     * Returns the startTime for the timeline. The startTime is the beginning of
     * Segment #0.
     * 
     * @return The start time.
     */
    public long getStartTime() {
        return (startTime);
    }

    /**
     * Returns the number of segments excluded per segment group.
     * 
     * @return The number of segments excluded.
     */
    public int getSegmentsExcluded() {
        return (segmentsExcluded);
    }

    /**
     * Returns the size in ms of the segments excluded per segment group.
     * 
     * @return The size in milliseconds.
     */
    public long getSegmentsExcludedSize() {
        return (segmentsExcludedSize);
    }

    /**
     * Returns the number of segments in a segment group. This will be equal to
     * segments included plus segments excluded.
     * 
     * @return The number of segments.
     */
    public int getSegmentsGroup() {
        return (segmentsGroup);
    }

    /**
     * Returns the size in ms of a segment group. This will be equal to
     * size of the segments included plus the size of the segments excluded.
     * 
     * @return The segment group size in milliseconds.
     */
    public long getSegmentsGroupSize() {
        return (segmentsGroupSize);
    }

    /**
     * Returns the number of segments incluced per segment group.
     * 
     * @return The number of segments.
     */
    public int getSegmentsIncluded() {
        return (segmentsIncluded);
    }

    /**
     * Returns the size in ms of the segments included per segment group.
     * 
     * @return The segment size in milliseconds.
     */
    public long getSegmentsIncludedSize() {
        return (segmentsIncludedSize);
    }

    /**
     * Returns the size of one segment in ms.
     * 
     * @return The segment size in milliseconds.
     */
    public long getSegmentSize() {
        return (segmentSize);
    }

    /**
     * Returns a list of all the exception segments. This list should not be
     * modified directly.
     * 
     * @return  The exception segments.
     */
    public ArrayList getExceptionSegments() {
        return (exceptionSegments);
    }

    /**
     * Sets the exception segments list.
     * 
     * @param exceptionSegments  the exception segments.
     */
    public void setExceptionSegments(ArrayList exceptionSegments) {
        this.exceptionSegments = exceptionSegments;
    }

    /**
     * Returns our baseTimeline, or null if none.
     * 
     * @return The base timeline.
     */
    public SegmentedTimeline getBaseTimeline() {
        return (baseTimeline);
    }

    /**
     * Sets the base timeline.
     * 
     * @param baseTimeline  the timeline.
     */
    public void setBaseTimeline(SegmentedTimeline baseTimeline) {

        // verify that baseTimeline is compatible with us
        if (baseTimeline != null) {
            if (baseTimeline.getSegmentSize() < segmentSize) {
                throw new IllegalArgumentException("baseTimeline.getSegmentSize() is smaller "
                                                   + "than segmentSize");
            } 
            else if (baseTimeline.getStartTime() > startTime) {
                throw new IllegalArgumentException("baseTimeline.getStartTime() is after than "
                                                   + "startTime");
            } 
            else if ((baseTimeline.getSegmentSize() % segmentSize) != 0) {
                throw new IllegalArgumentException(
                    "baseTimeline.getSegmentSize() is not multiple of segmentSize");
            } 
            else if (((startTime - baseTimeline.getStartTime()) % segmentSize) != 0) {
                throw new IllegalArgumentException(
                    "baseTimeline is not aligned");
            }
        }

        this.baseTimeline = baseTimeline;
    }

    /**
     * Translates a value relative to the domain value (all Dates) into a value
     * relative to the segmented timeline. The values relative to the segmented timeline
     * are all consecutives starting at zero at the startTime.
     *
     * @param domainValue Value relative to the domain.
     * 
     * @return The timeline value.
     */
    public long toTimelineValue(long domainValue) {
        long shiftedSegmentedValue = domainValue - startTime;
        long x = shiftedSegmentedValue % segmentsGroupSize;
        long y = shiftedSegmentedValue / segmentsGroupSize;

        long wholeExceptionsBeforeDomainValue =
            getExceptionSegmentCount(startTime, domainValue - 1);

        long partialTimeInException = 0;
        Segment segment = getSegment(domainValue);
        if (segment.inExceptionSegments()) {
            partialTimeInException = domainValue - segment.getSegmentStart();
        }

        long value = (x < segmentsIncludedSize ? segmentsIncludedSize * y + x 
                      : segmentsIncludedSize * (y + 1)) 
                      - wholeExceptionsBeforeDomainValue * segmentSize - partialTimeInException;

        return (value);
    }

    /**
     * Translates a value relative to the domain value (all Dates) into a value
     * relative to the segmented timeline. The values relative to the segmented timeline
     * are all consecutives starting at zero at the startTime.
     *
     * @param dateDomainValue date relative to the domain.
     * 
     * @return The timeline value.
     */
    public long toTimelineValue(Date dateDomainValue) {
        return (toTimelineValue(getTime(dateDomainValue)));
    }

    /**
     * Translates a value relative to the timeline into a domain value.
     *
     * @param timelineValue  the timeline value.
     * 
     * @return The domain value.
     */
    public long toDomainValue(long timelineValue) {
        // calculate the result as if no exceptions
        Segment result = new Segment(startTime + timelineValue 
            + ((timelineValue / segmentsIncludedSize)) * segmentsExcludedSize);
        long lastIndex = startTime;

        // adjust result for any exceptions in the result calculated
        while (lastIndex <= result.segmentStart) {

            // skip all whole exception segments in the range
            long exceptionSegmentCount;
            while ((exceptionSegmentCount =
                     getExceptionSegmentCount(lastIndex, result.index - 1)) > 0) {
                lastIndex = result.segmentStart;
                // move forward exceptionSegmentCount segments skipping excluded segments
                for (int i = 0; i < exceptionSegmentCount; i++) {
                    do {
                        result.inc();
                    }
                    while (result.inExcludeSegments());
                }
            }
            lastIndex = result.segmentStart;

            // skip exception or excluded segments we may fall on
            while (result.inExceptionSegments() || result.inExcludeSegments()) {
                result.inc();
                lastIndex += segmentSize;
            }

            lastIndex++;
        }

        return (result.index);
    }

    /**
     * Returns true if a value is contained in the timeline.
     * @param domainValue value to verify
     * @return true if value is contained in the timeline
     */
    public boolean containsDomainValue(long domainValue) {
        Segment segment = getSegment(domainValue);
        return (segment.inIncludeSegments());
    }

    /**
     * Returns true if a value is contained in the timeline.
     * @param dateDomainValue date to verify
     * @return true if value is contained in the timeline
     */
    public boolean containsDomainValue(Date dateDomainValue) {
        return (containsDomainValue(getTime(dateDomainValue)));
    }

    /**
     * Returns true if a range of values are contained in the timeline. This is
     * implemented verifying that all segments are in the range.
     *
     * @param domainValueStart start of the range to verify
     * @param domainValueEnd end of the range to verify
     * @return true if the range is contained in the timeline
     */
    public boolean containsDomainRange(long domainValueStart, long domainValueEnd) {
        if (domainValueEnd < domainValueStart) {
            throw new IllegalArgumentException("domainValueEnd (" + domainValueEnd
                + ") < domainValueStart (" + domainValueStart + ")");
        }
        Segment segment = getSegment(domainValueStart);
        boolean contains = true;
        do {
            contains = (segment.inIncludeSegments());
            if (segment.contains(domainValueEnd)) {
                break;
            } 
            else {
                segment.inc();
            }
        } 
        while (contains);
        return (contains);
    }

    /**
     * Returns true if a range of values are contained in the timeline. This is
     * implemented verifying that all segments are in the range.
     *
     * @param dateDomainValueStart start of the range to verify
     * @param dateDomainValueEnd end of the range to verify
     * @return true if the range is contained in the timeline
     */
    public boolean containsDomainRange(Date dateDomainValueStart, Date dateDomainValueEnd) {
        return (containsDomainRange(getTime(dateDomainValueStart), getTime(dateDomainValueEnd)));
    }

    /**
     * Adds a segment as an exception. An exception segment is defined as a segment
     * to exclude from what would otherwise be considered a valid segment of the timeline.
     * An exception segment can not be contained inside an already excluded segment.
     * If so, no action will occure (the proposed exception segment will be discarted).
     * <p>
     * The segment is identified by a domainValue into any part of the segment.
     * Therefore the segmentStart <= domainValue <= segmentEnd.
     *
     * @param domainValue domain value to teat as an exception
     */
    public void addException(long domainValue) {
        addException(new Segment(domainValue));
    }

    /**
     * Adds a segment range as an exception. An exception segment is defined as a segment
     * to exclude from what would otherwise be considered a valid segment of the timeline.
     * An exception segment can not be contained inside an already excluded segment.
     * If so, no action will occure (the proposed exception segment will be discarted).
     * <p>
     * The segment range is identified by the domainValue that begins a valid segment and ends
     * with the domainValue that ends a valid segment. Therefore the range will contain all
     * segments whose segmentStart <= domainValue and segmentEnd <= toDomainValue.
     *
     * @param fromDomainValue start of domain range to teat as an exception
     * @param toDomainValue end of domain range to teat as an exception
     */
    public void addException(long fromDomainValue, long toDomainValue) {
        addException(new SegmentRange(fromDomainValue, toDomainValue));
    }

    /**
     * Adds a segment as an exception. An exception segment is defined as a segment
     * to exclude from what would otherwise be considered a valid segment of the timeline.
     * An exception segment can not be contained inside an already excluded segment.
     * If so, no action will occure (the proposed exception segment will be discarted).<p>
     *
     * The segment is identified by a Date into any part of the segment.
     *
     * @param exceptionDate Date into the segment to exclude
     */
    public void addException(Date exceptionDate) {
        addException(getTime(exceptionDate));
    }

    /**
     * Adds a list of dates as segment exceptions. Each exception segment is definied as a segment
     * to exclude from what would otherwise be considered a valid segment of the timeline.
     * An exception segment can not be contained inside an already excluded segment.
     * If so, no action will occure (the proposed exception segment will be discarted).<p>
     *
     * The segment is identified by a Date into any part of the segment.
     *
     * @param exceptionList List of Date objects that identify the segments to exclude
     */
    public void addExceptions(List exceptionList) {
        for (Iterator iter = exceptionList.iterator(); iter.hasNext();) {
            addException((Date) iter.next());
        }
    }

    /**
     * Adds a segment as an exception. An exception segment is defined as a segment
     * to exclude from what would otherwise be considered a valid segment of the timeline.
     * An exception segment can not be contained inside an already excluded segment.
     * This is verified inside this method, and if so, no action will occure (the proposed
     * exception segment will be discarted).
     *
     * @param segment Segment to exclude
     */
    private void addException(Segment segment) {
         if (segment.inIncludeSegments()) {
             int p = binarySearchExceptionSegments(segment);
             exceptionSegments.add(-(p + 1), segment);
         }
    }

    /**
     * Adds a segment relative to the baseTimeline as an exception. Because a base segment is
     * normally larger than our segments, this may add one or more segment ranges to the
     * exception list.
     * <p>
     * An exception segment is defined as a segment
     * to exclude from what would otherwise be considered a valid segment of the timeline.
     * An exception segment can not be contained inside an already excluded segment.
     * If so, no action will occure (the proposed exception segment will be discarted).
     * <p>
     * The segment is identified by a domainValue into any part of the baseTimeline segment.
     *
     * @param domainValue domain value to teat as a baseTimeline exception
     */
    public void addBaseTimelineException(long domainValue) {

        Segment baseSegment = baseTimeline.getSegment(domainValue);
        if (baseSegment.inIncludeSegments()) {

            // cycle through all the segments contained in the BaseTimeline exception segment
            Segment segment = getSegment(baseSegment.getSegmentStart());
            while (segment.getSegmentStart() <= baseSegment.getSegmentEnd()) {
                if (segment.inIncludeSegments()) {

                    // find all consecutive included segments
                    long fromDomainValue = segment.getSegmentStart();
                    long toDomainValue;
                    do {
                        toDomainValue = segment.getSegmentEnd();
                        segment.inc();
                    }
                    while (segment.inIncludeSegments());

                    // add the interval as an exception
                    addException(fromDomainValue, toDomainValue);

                }
                else {
                    // this is not one of our included segment, skip it
                    segment.inc();
                }
            }
        }
    }

    /**
     * Adds a segment relative to the baseTimeline as an exception. An exception segment is 
     * defined as a segment to exclude from what would otherwise be considered a valid segment 
     * of the timeline.
     * An exception segment can not be contained inside an already excluded segment.
     * If so, no action will occure (the proposed exception segment will be discarted).
     * <p>
     * The segment is identified by a domainValue into any part of the segment.
     * Therefore the segmentStart <= domainValue <= segmentEnd.
     *
     * @param date date domain value to teat as a baseTimeline exception
     */
    public void addBaseTimelineException(Date date) {
        addBaseTimelineException(getTime(date));
    }

    /**
     * Adds all excluded segments from the BaseTimeline as exceptions to our timeline. This allows
     * us to combine two timelines for more complex calculations.
     *
     * @param fromBaseDomainValue Start of the range where exclusions will be extracted.
     * @param toBaseDomainValue End of the range to process.
     */
    public void addBaseTimelineExclusions(long fromBaseDomainValue, long toBaseDomainValue) {

        // find first excluded base segment starting fromDomainValue
        Segment baseSegment = baseTimeline.getSegment(fromBaseDomainValue);
        while (baseSegment.getSegmentStart() <= toBaseDomainValue 
               && !baseSegment.inExcludeSegments()) {
                   
            baseSegment.inc();
            
        }

        // cycle over all the base segments groups in the range
        while (baseSegment.getSegmentStart() <= toBaseDomainValue) {

            long baseExclusionRangeEnd = baseSegment.getSegmentStart() 
                 + baseTimeline.getSegmentsExcluded() * baseTimeline.getSegmentSize() - 1;

            // cycle through all the segments contained in the base exclusion area
            Segment segment = getSegment(baseSegment.getSegmentStart());
            while (segment.getSegmentStart() <= baseExclusionRangeEnd) {
                if (segment.inIncludeSegments()) {

                    // find all consecutive included segments
                    long fromDomainValue = segment.getSegmentStart();
                    long toDomainValue;
                    do {
                        toDomainValue = segment.getSegmentEnd();
                        segment.inc();
                    }
                    while (segment.inIncludeSegments());

                    // add the interval as an exception
                    addException(new BaseTimelineSegmentRange(fromDomainValue, toDomainValue));
                }
                else {
                    // this is not one of our included segment, skip it
                    segment.inc();
                }
            }

            // go to next base segment group
            baseSegment.inc(baseTimeline.getSegmentsGroup());
        }
    }

    /**
     * Returns the number of exception segments wholy contained in the
     * (fromDomainValue, toDomainValue) interval.
     *
     * @param fromDomainValue Beginning of the interval
     * @param toDomainValue End of interval
     * @return Number of exception segments contained in the interval.
     */
    public long getExceptionSegmentCount(long fromDomainValue, long toDomainValue) {
        if (toDomainValue < fromDomainValue) {
            return (0);
        }

        int n = 0;
        for (Iterator iter = exceptionSegments.iterator(); iter.hasNext();) {
            Segment segment = (Segment) iter.next();
            Segment intersection = segment.intersect(fromDomainValue, toDomainValue);
            if (intersection != null) {
                n += intersection.getSegmentCount();
            }
        }

        return (n);
    }

    /**
     * Returns a segment that contains a domainValue. If the domainValue is not contained
     * in the timeline (because it is not contained in the baseTimeline),
     * a Segment that contains <code>index + segmentSize*m</code> will be returned for the
     * smallest <code>m</code> possible.

     * @param domainValue index into the segment
     * @return a Segment that contains index, or the next possible Segment.
     */
    public Segment getSegment(long domainValue) {
        return (new Segment(domainValue));
    }

    /**
     * Returns a segment that contains a date. For accurate calculations,
     * the calendar should use TIME_ZONE for its calculation (or any other similar time zone).
     *
     * If the date is not contained in the timeline (because it is not contained in the 
     * baseTimeline), a Segment that contains <code>date + segmentSize*m</code> will be returned 
     * for the smallest <code>m</code> possible.
     *
     * @param date date into the segment
     * @return a Segment that contains date, or the next possible Segment.
     */
    public Segment getSegment(Date date) {
        return (getSegment(getTime(date)));
    }

    /**
     * Convenient method to test equality in two object taking into account nulls.
     * @param o first object to compare
     * @param p second object to compare
     * @return true if both objects are equal or both null, false otherwise.
     */
    private boolean equals(Object o, Object p) {
        return (o == p || ((o != null) && o.equals(p)));
    }

    /**
     * Returns true if we are equal to the parameter
     * @param o Object to verify with us
     * @return true or false
     */
    public boolean equals(Object o) {
        if (o instanceof SegmentedTimeline) {
            SegmentedTimeline other = (SegmentedTimeline) o;
            return (segmentSize == other.getSegmentSize() 
                    && segmentsIncluded == other.getSegmentsIncluded() 
                    && segmentsExcluded == other.getSegmentsExcluded() 
                    && startTime == other.getStartTime() 
                    && equals(exceptionSegments, other.getExceptionSegments()));
        } 
        else {
            return (false);
        }
    }

    /**
     * Preforms a binary serach in the exceptionSegments sorted array. This array can contain
     * Segments or SegmentRange objects.
     *
     * @param  segment the key to be searched for.
     * 
     * @return index of the search segment, if it is contained in the list;
     *         otherwise, <tt>(-(<i>insertion point</i>) - 1)</tt>.  The
     *         <i>insertion point</i> is defined as the point at which the
     *         segment would be inserted into the list: the index of the first
     *         element greater than the key, or <tt>list.size()</tt>, if all
     *         elements in the list are less than the specified segment.  Note
     *         that this guarantees that the return value will be &gt;= 0 if
     *         and only if the key is found.
     */
    private int binarySearchExceptionSegments(Segment segment) {
        int low = 0;
        int high = exceptionSegments.size() - 1;

        while (low <= high) {
            int mid = (low + high) / 2;
            Segment midSegment = (Segment) exceptionSegments.get(mid);

            // first test for equality (contains or contained)
            if (segment.contains(midSegment) || midSegment.contains(segment)) {
                return (mid);
            }

            if (midSegment.before(segment)) {
                low = mid + 1;
            } 
            else if (midSegment.after(segment)) {
                high = mid - 1;
            } 
            else {
                throw new IllegalStateException("Invalid condition.");
            }
        }
        return -(low + 1);  // key not found
    }

    /**
     * Special method that handles conversion between the Default Time Zone and a UTC time zone
     * with no DST. This is needed so all days have the same size. This method is the prefered
     * way of converting a Data into milliseconds for usage in this class.
     *
     * @param date Date to convert to long.
     * 
     * @return The milliseconds.
     */
    public long getTime(Date date) {
        workingCalendar.setTime(date);
        workingCalendarNoDST.set(workingCalendar.get(Calendar.YEAR),
                                 workingCalendar.get(Calendar.MONTH),
                                 workingCalendar.get(Calendar.DATE),
                                 workingCalendar.get(Calendar.HOUR_OF_DAY),
                                 workingCalendar.get(Calendar.MINUTE),
                                 workingCalendar.get(Calendar.SECOND));
        workingCalendarNoDST.set(Calendar.MILLISECOND, workingCalendar.get(Calendar.MILLISECOND));
        return (workingCalendarNoDST.getTime().getTime());
    }

    /** 
     * Converts a millisecond value into a {@link Date} object.
     * 
     * @param value  the millisecond value.
     * 
     * @return The date.
     */
    public Date getDate(long value) {
        workingCalendarNoDST.setTime(new Date(value));
        return (workingCalendarNoDST.getTime());
    }

    /**
     * Internal class to represent a valid Segment valid for this timeline. A segment
     * is valid on a timeline if it is part of its incluced, excluded or exception
     * segments.
     * <p>
     * Each segment will know its segment number, segmentStart, segmentEnd and
     * index inside the segment.
     */
    public class Segment implements Comparable, Cloneable, Serializable {

        /** The segment number. */
        protected long segmentNumber;
        
        /** The segment start. */
        protected long segmentStart;
        
        /** The segment end. */
        protected long segmentEnd;
        
        /** The index. */
        protected long index;

        /**
         * Protected constructor only used by sub-classes.
         */
        protected Segment() {
        }

        /**
         * Creates a segment based on an index inside the segment
         * 
         * @param index index inside the segment
         */
        protected Segment(long index) {
            this.segmentNumber = getSegmentNumber(index);
            this.segmentStart = startTime + segmentNumber * segmentSize;
            this.segmentEnd = segmentStart + segmentSize - 1;
            this.index = index;
        }

        /**
         * Returns the segment number of an index.
         * 
         * @param index index inside the segment
         * 
         * @return The segment number.
         */
        public long getSegmentNumber(long index) {
            return ((index - startTime) / segmentSize);
        }

        /**
         * Returns the segment number of this segment. Segments start at 0.
         * 
         * @return The segment number.
         */
        public long getSegmentNumber() {
            return (segmentNumber);
        }

        /**
         * Returns always one (the number of segments contained in this segment).
         * 
         * @return The segment count (always 1 for this class).
         */
        public long getSegmentCount() {
            return (1);
        }

        /**
         * Gets the start of this segment in ms.
         * 
         * @return The segment start.
         */
        public long getSegmentStart() {
            return (segmentStart);
        }

        /**
         * Gets the end of this segment in ms.
         * 
         * @return The segment end.
         */
        public long getSegmentEnd() {
            return (segmentEnd);
        }

        /**
         * Gets the index in this segment. Index will always be between the segmentStart
         * and segmentEnd.
         * 
         * @return The index.
         */
        public long getIndex() {
            return (index);
        }

        /**
         * Gets a Date that represents the index of this segment.
         * 
         * @return The date.
         */
        public Date getDate() {
            return (SegmentedTimeline.this.getDate(index));
        }

        /**
         * Returns true if an index is contained in this segment.
         * @param index Index to verify
         * @return true if index is contained in the segment.
         */
        public boolean contains(long index) {
            return (segmentStart <= index && index <= segmentEnd);
        }

        /**
         * Returns true if an interval is contained in this segment.
         * 
         * @param from Begining of interval
         * @param to End of interval
         * 
         * @return true if interval is contained in the segment.
         */
        public boolean contains(long from, long to) {
            return (segmentStart <= from && to <= segmentEnd);
        }

        /**
         * Returns true if a segment is contained in this segment.
         * 
         * @param segment The segment to test for inclusion
         * 
         * @return true if the segment is contained in this segment.
         */
        public boolean contains(Segment segment) {
            return (contains(segment.getSegmentStart(), segment.getSegmentEnd()));
        }

        /**
         * Returns true if this segment is contained in an interval.
         * 
         * @param from Begining of interval
         * @param to End of interval
         * 
         * @return true this segment is contained in the interval
         */
        public boolean contained(long from, long to) {
            return (from <= segmentStart && segmentEnd <= to);
        }

        /**
         * Returns a segment that is the intersection of this segment and the interval.
         * @param from Begining of interval
         * @param to End of interval
         * @return true this segment is contained in the interval
         */
        public Segment intersect(long from, long to) {
            if (from <= segmentStart && segmentEnd <= to) {
                return (this);
            } 
            else {
                return (null);
            }
        }

        /**
         * Returns true if we are before another segment.
         * 
         * @param other The other segment to compare with us
         * 
         * @return true if we are before other
         */
        public boolean before(Segment other) {
            return (this.segmentEnd < other.getSegmentStart());
        }

        /**
         * Returns true if we are after another segment.
         * 
         * @param other The other segment to compare with us
         * 
         * @return true if we are after other
         */
        public boolean after(Segment other) {
            return (this.segmentStart > other.getSegmentEnd());
        }

        /**
         * Returns true if we are equal to another segment.
         * 
         * @param o The other segment to compare with us
         * 
         * @return true if we are the same segment
         */
        public boolean equals(Object o) {
            if (o instanceof Segment) {
                Segment other = (Segment) o;
                return (this.segmentNumber == other.getSegmentNumber() 
                        && this.segmentStart == other.getSegmentStart() 
                        && this.segmentEnd == other.getSegmentEnd() 
                        && this.index == other.getIndex());
            }
            else {
                return false;
            }
        }

        /**
         * Returns a copy of ourselves or null if there was an exception during
         * cloning.
         * 
         * @return a copy of ourselves.
         */
        public Segment copy() {
            try {
                return ((Segment) this.clone());
            } 
            catch (CloneNotSupportedException e) {
                return (null);
            }
        }

        /**
         * Will compare this Segment with another Segment (from Comparable interface).
         *
         * @param object The other Segment to compare with
         * 
         * @return -1: this < object, 0: this.equal(object) and +1: this > object
         */
        public int compareTo(Object object) {
            Segment other = (Segment) object;
            if (this.before(other)) {
                return -1;
            } 
            else if (this.after(other)) {
                return +1;
            } 
            else {
                return 0;
            }
        }

        /**
         * Returns true if we are an included segment and we are not an exception.
         * 
         * @return <code>true</code> or <code>false</code>.
         */
        public boolean inIncludeSegments() {
            if (getSegmentNumberRelativeToGroup() < segmentsIncluded) {
                return (!inExceptionSegments());
            } 
            else {
                return (false);
            }
        }

        /**
         * Returns true if we are an excluded segment.
         * 
         * @return <code>true</code> or <code>false</code>.
         */
        public boolean inExcludeSegments() {
            return (getSegmentNumberRelativeToGroup() >= segmentsIncluded);
        } 

        /**
         * Calculate the segment number relative to the segment group. This will be a number between
         * 0 and segmentsGroup-1. This value is calculated from the segmentNumber. Special care is
         * taken for negative segmentNumbers.
         * 
         * @return The segment number.
         */
        private long getSegmentNumberRelativeToGroup() {
            long p = (segmentNumber % segmentsGroup);
            if (p < 0) {
                p += segmentsGroup;
            }
            return (p);
        }

        /**
         * Returns true if we are an exception segment. This is implemented via
         * a binary search on the exceptionSegments sorted list.
         *
         * If the segment is not listed as an exception in our list and we have
         * a baseTimeline, a check is performed to see if the segment is inside
         * an excluded segment from our base. If so, it is also considered an
         * exception.
         *
         * @return true if we are an exception segment.
         */
        public boolean inExceptionSegments() {
            return (binarySearchExceptionSegments(this) >= 0);
        }

        /**
         * Increments the internal attributes of this segment by a number of
         * segments.
         *
         * @param n Number of segments to increment.
         */
        public void inc(long n) {
            segmentNumber += n;
            long m = n * segmentSize;
            segmentStart += m;
            segmentEnd += m;
            index += m;
        }

        /**
         * Increments the internal attributes of this segment by one segment.
         * The exact time incremented is segmentSize.
         */
        public void inc() {
            inc(1);
        } 

        /**
         * Moves the index of this segment to the beginning if the segment.
         */
        public void moveIndexToStart() {
            index = segmentStart;
        }

        /**
         * Moves the index of this segment to the end of the segment.
         */
        public void moveIndexToEnd() {
            index = segmentEnd;
        }

    }

    /**
     * Private internal class to represent a range of segments. This class is mainly used to
     * store in one object a range of exception segments. This optimizes certain timelines
     * that use a small segment size (like an intraday timeline) allowing them to express a day
     * exception as one SegmentRange instead of multi Segments.
     */
    protected class SegmentRange extends Segment { 

        /** The number of segments in the range. */
        private long segmentCount; 

        /**
         * Creates a SegmentRange between a start and end domain values.
         * @param fromDomainValue start of the range
         * @param toDomainValue en of the range
         */
        public SegmentRange(long fromDomainValue, long toDomainValue) {

            Segment start = getSegment(fromDomainValue);
            Segment end = getSegment(toDomainValue);
            if (start.getSegmentStart() != fromDomainValue 
                || end.getSegmentEnd() != toDomainValue) {
                throw new IllegalArgumentException("Invalid Segment Range ["
                    + fromDomainValue + "," + toDomainValue + "]");
            }

            this.index = fromDomainValue;
            this.segmentNumber = getSegmentNumber(index);
            this.segmentStart = fromDomainValue;
            this.segmentEnd = toDomainValue;
            this.segmentCount = (end.getSegmentNumber() - start.getSegmentNumber() + 1);
        }

        /**
         * Returns the number of segments contained in this range.
         * 
         * @return The segment count.
         */
        public long getSegmentCount() {
            return this.segmentCount;
        }

        /**
         * Returns a segment that is the intersection of this segment and the interval.
         * 
         * @param from Begining of interval
         * @param to End of interval
         * 
         * @return true this segment is contained in the interval
         */
        public Segment intersect(long from, long to) {
            long start = Math.max(from, segmentStart);
            long end = Math.min(to, segmentEnd);
            if (start <= end) {
                return (new SegmentRange(start, end));
            } 
            else {
                return (null);
            }
        }

        /**
         * Returns true if all Segments of this SegmentRenge are an included segment and are
         * not an exception.
         * 
         * @return <code>true</code> or </code>false</code>.
         */
        public boolean inIncludeSegments() {
            for (Segment segment = getSegment(segmentStart);
                segment.getSegmentStart() < segmentEnd;
                segment.inc()) {
                if (!segment.inIncludeSegments()) {
                    return (false);
                }
            }
            return (true);
        }

        /**
         * Returns true if we are an excluded segment.
         * 
         * @return <code>true</code> or </code>false</code>.
         */
        public boolean inExcludeSegments() {
            for (Segment segment = getSegment(segmentStart);
                segment.getSegmentStart() < segmentEnd;
                segment.inc()) {
                if (!segment.inExceptionSegments()) {
                    return (false);
                }
            }
            return (true);
        }

        /**
         * Not implemented for SegmentRange. Always throws IllegalArgumentException.
         *
         * @param n Number of segments to increment.
         */
        public void inc(long n) {
            throw new IllegalArgumentException("Not implemented in SegmentRange");
        }

    }

    /**
     * Special Segment Range that came from the BaseTimeline.
     */
    protected class BaseTimelineSegmentRange extends SegmentRange {

        /**
         * Constructor.
         * 
         * @param fromDomainValue  the start value.
         * @param toDomainValue  the end value.
         */
        public BaseTimelineSegmentRange(long fromDomainValue, long toDomainValue) {
            super(fromDomainValue, toDomainValue);
        }
       
    }

}
