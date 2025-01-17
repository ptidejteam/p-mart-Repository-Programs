/* ======================================
 * JFreeChart : a free Java chart library
 * ======================================
 *
 * Project Info:  http://www.object-refinery.com/jcommon/index.html
 * Project Lead:  David Gilbert (david.gilbert@object-refinery.com);
 *
 * (C) Copyright 2000-2003, by Simba Management Limited.
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
 * --------------
 * HourTests.java
 * --------------
 * (C) Copyright 2002, 2003 by Simba Management Limited.
 *
 * Original Author:  David Gilbert (for Simba Management Limited);
 * Contributor(s):   -;
 *
 * $Id: HourTests.java,v 1.1 2007/10/10 19:54:28 vauchers Exp $
 *
 * Changes
 * -------
 * 29-Jan-2002 : Version 1 (DG);
 * 17-Oct-2002 : Fixed errors reported by Checkstyle (DG);
 *
 */

package com.jrefinery.data.junit;

import java.util.Date;
import java.util.TimeZone;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import com.jrefinery.data.Hour;
import com.jrefinery.data.Day;
import com.jrefinery.date.SerialDate;

/**
 * Tests for the {@link Hour} class.
 *
 * @author David Gilbert
 */
public class HourTests extends TestCase {

    /**
     * Returns the tests as a test suite.
     *
     * @return the test suite.
     */
    public static Test suite() {
        return new TestSuite(HourTests.class);
    }

    /**
     * Constructs a new set of tests.
     *
     * @param name  the name of the tests.
     */
    public HourTests(String name) {
        super(name);
    }

    /**
     * Common test setup.
     */
    protected void setUp() {

    }

    /**
     * Test that an Hour instance is equal to itself.
     *
     * SourceForge Bug ID: 558850.
     */
    public void testEqualsSelf() {
        Hour hour = new Hour();
        assertTrue(hour.equals(hour));
    }

    /**
     * Tests the equals method.
     */
    public void testEquals() {
        Hour hour1 = new Hour(15, new Day(29, SerialDate.MARCH, 2002));
        Hour hour2 = new Hour(15, new Day(29, SerialDate.MARCH, 2002));
        assertTrue(hour1.equals(hour2));
    }

    /**
     * In GMT, the 4pm on 21 Mar 2002 is java.util.Date(1,014,307,200,000L).  Use this to check the
     * hour constructor.
     */
    public void testDateConstructor1() {

        TimeZone zone = TimeZone.getTimeZone("GMT");
        Hour h1 = new Hour(new Date(1014307199999L), zone);
        Hour h2 = new Hour(new Date(1014307200000L), zone);

        assertEquals(15, h1.getHour());
        assertEquals(1014307199999L, h1.getLastMillisecond(zone));

        assertEquals(16, h2.getHour());
        assertEquals(1014307200000L, h2.getFirstMillisecond(zone));

    }

    /**
     * In Sydney, the 4pm on 21 Mar 2002 is java.util.Date(1,014,267,600,000L).
     * Use this to check the hour constructor.
     */
    public void testDateConstructor2() {

        TimeZone zone = TimeZone.getTimeZone("Australia/Sydney");
        Hour h1 = new Hour(new Date(1014267599999L), zone);
        Hour h2 = new Hour (new Date(1014267600000L), zone);

        assertEquals(15, h1.getHour());
        assertEquals(1014267599999L, h1.getLastMillisecond(zone));

        assertEquals(16, h2.getHour());
        assertEquals(1014267600000L, h2.getFirstMillisecond(zone));

    }

    /**
     * Set up an hour equal to hour zero, 1 January 1900.  Request the previous hour, it should be
     * null.
     */
    public void testFirstHourPrevious() {

        Hour first = new Hour(0, new Day(1, SerialDate.JANUARY, 1900));
        Hour previous = (Hour) first.previous();
        assertNull(previous);

    }

    /**
     * Set up an hour equal to hour zero, 1 January 1900.  Request the next hour, it should be
     * null.
     */
    public void testFirstHourNext() {

        Hour first = new Hour(0, new Day(1, SerialDate.JANUARY, 1900));
        Hour next = (Hour) first.next();
        assertEquals(1, next.getHour());
        assertEquals(1900, next.getYear());

    }

    /**
     * Set up an hour equal to hour zero, 1 January 1900.  Request the previous hour, it should be
     * null.
     */
    public void testLastHourPrevious() {

        Hour last = new Hour(23, new Day(31, SerialDate.DECEMBER, 9999));
        Hour previous = (Hour) last.previous();
        assertEquals(22, previous.getHour());
        assertEquals(9999, previous.getYear());

    }

    /**
     * Set up an hour equal to hour zero, 1 January 1900.  Request the next hour, it should be
     * null.
     */
    public void testLastHourNext() {

        Hour last = new Hour(23, new Day(31, SerialDate.DECEMBER, 9999));
        Hour next = (Hour) last.next();
        assertNull(next);

    }

    /**
     * Test for date parsing.
     */
    public void testParseHour() {

        // test 1...
        Hour h = Hour.parseHour("2002-01-29 13");
        assertEquals(13, h.getHour());

    }

}
