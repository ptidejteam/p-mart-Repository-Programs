/* ================================================================
 * JCommon : a general purpose, open source, class library for Java
 * ================================================================
 *
 * Project Info:  http://www.object-refinery.com/jcommon/index.html
 * Project Lead:  David Gilbert (david.gilbert@object-refinery.com);
 *
 * (C) Copyright 2000-2002, by Simba Management Limited.
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
 * DayTests.java
 * -------------
 * (C) Copyright 2001, 2002, by Simba Management Limited.
 *
 * Original Author:  David Gilbert (for Simba Management Limited);
 * Contributor(s):   -;
 *
 * $Id: DayTests.java,v 1.1 2007/10/10 19:52:22 vauchers Exp $
 *
 * Changes
 * -------
 * 15-Nov-2001 : Version 1 (DG);
 * 20-Mar-2002 : Added new tests for Day constructor and getStart() and getEnd() in different
 *               time zones (DG);
 * 26-Jun-2002 : Removed unnecessary imports (DG);
 *
 */

package com.jrefinery.data.junit;

import java.util.Date;
import java.util.TimeZone;
import junit.framework.*;
import com.jrefinery.data.*;
import com.jrefinery.date.*;

/**
 * Tests for the Day class.
 */
public class DayTests extends TestCase {

    /**
     * Returns the tests as a test suite.
     */
    public static Test suite() {
        return new TestSuite(DayTests.class);
    }

    /**
     * Constructs a new set of tests.
     *
     * @param The name of the tests.
     */
    public DayTests(String name) {
        super(name);
    }

    /**
     * Common test setup.
     */
    protected void setUp() {

    }

    /**
     * Test that a Day instance is equal to itself.
     *
     * SourceForge Bug ID: 558850.
     */
    public void testEqualsSelf() {
        Day day = new Day();
        this.assertTrue(day.equals(day));
    }

    public void testEquals() {
        Day day1 = new Day(29, SerialDate.MARCH, 2002);
        Day day2 = new Day(29, SerialDate.MARCH, 2002);
        this.assertTrue(day1.equals(day2));
    }

    /**
     * In GMT, the end of 29 Feb 2004 is java.util.Date(1,078,099,199,999L).  Use this to check the
     * day constructor.
     */
    public void testDateConstructor1() {

        TimeZone zone = TimeZone.getTimeZone("GMT");
        Day d1 = new Day(new Date(1078099199999L), zone);
        Day d2 = new Day(new Date(1078099200000L), zone);

        this.assertEquals(SerialDate.FEBRUARY, d1.getMonth());
        this.assertEquals(1078099199999L, d1.getEnd(zone));

        this.assertEquals(SerialDate.MARCH, d2.getMonth());
        this.assertEquals(1078099200000L, d2.getStart(zone));

    }

    /**
     * In Helsinki, the end of 29 Feb 2004 is java.util.Date(1,078,091,999,999L).  Use this to
     * check the Day constructor.
     */
    public void testDateConstructor2() {

        TimeZone zone = TimeZone.getTimeZone("Europe/Helsinki");
        Day d1 = new Day(new Date(1078091999999L), zone);
        Day d2 = new Day(new Date(1078092000000L), zone);

        this.assertEquals(SerialDate.FEBRUARY, d1.getMonth());
        this.assertEquals(1078091999999L, d1.getEnd(zone));

        this.assertEquals(SerialDate.MARCH, d2.getMonth());
        this.assertEquals(1078092000000L, d2.getStart(zone));

    }

    /**
     * Set up a day equal to 1 January 1900.  Request the previous day, it should be null.
     */
    public void test1Jan1900_previous() {

        Day jan1_1900 = new Day(1, SerialDate.JANUARY, 1900);
        Day previous = (Day)jan1_1900.previous();
        this.assertNull(previous);

    }

    /**
     * Set up a day equal to 1 January 1900.  Request the next day, it should be 2 January 1900.
     */
    public void test1Jan1900_next() {

        Day jan1_1900 = new Day(1, SerialDate.JANUARY, 1900);
        Day next = (Day)jan1_1900.next();
        this.assertEquals(2, next.getDayOfMonth());

    }

    /**
     * Set up a day equal to 31 December 9999.  Request the previous day, it should be 30 December
     * 9999.
     */
    public void test31Dec9999_previous() {

        Day dec31_9999 = new Day(31, SerialDate.DECEMBER, 9999);
        Day previous = (Day)dec31_9999.previous();
        this.assertEquals(30, previous.getDayOfMonth());

    }

    /**
     * Set up a day equal to 31 December 9999.  Request the next day, it should be null.
     */
    public void test31Dec9999_next() {

        Day dec31_9999 = new Day(31, SerialDate.DECEMBER, 9999);
        Day next = (Day)dec31_9999.next();
        this.assertNull(next);

    }

    /**
     * Test for date parsing.
     */
    public void testParseDay() {

        // test 1...
        Day d = Day.parseDay("31/12/2001");
        assertEquals(37256, d.getSerialDate().toSerial());

        // test 2...
        d = Day.parseDay("2001-12-31");
        assertEquals(37256, d.getSerialDate().toSerial());

    }

}
