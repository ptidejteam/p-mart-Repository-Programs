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
 * -------------
 * DayTests.java
 * -------------
 * (C) Copyright 2001-2003, by Object Refinery Limited.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited);
 * Contributor(s):   -;
 *
 * $Id: DayTests.java,v 1.1 2007/10/10 19:50:27 vauchers Exp $
 *
 * Changes
 * -------
 * 15-Nov-2001 : Version 1 (DG);
 * 20-Mar-2002 : Added new tests for Day constructor and getStart() and getEnd() in different
 *               time zones (DG);
 * 26-Jun-2002 : Removed unnecessary imports (DG);
 * 17-Oct-2002 : Fixed errors reported by Checkstyle (DG);
 * 13-Mar-2003 : Added serialization test (DG);
 * 21-Oct-2003 : Added hashCode test (DG);
 *
 */

package org.jfree.data.time.junit;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.jfree.data.time.Day;
import org.jfree.date.SerialDate;

/**
 * Tests for the {@link Day} class.
 *
 */
public class DayTests extends TestCase {

    /**
     * Returns the tests as a test suite.
     *
     * @return the test suite.
     */
    public static Test suite() {
        return new TestSuite(DayTests.class);
    }

    /**
     * Constructs a new set of tests.
     *
     * @param name  the name of the tests.
     */
    public DayTests(final String name) {
        super(name);
    }

    /**
     * Common test setup.
     */
    protected void setUp() {
        // no setup required
    }

    /**
     * Problem that a Day instance is equal to itself.
     *
     * SourceForge Bug ID: 558850.
     */
    public void testEqualsSelf() {
        final Day day = new Day();
        assertTrue(day.equals(day));
    }

    /**
     * Tests the equals method.
     */
    public void testEquals() {
        final Day day1 = new Day(29, SerialDate.MARCH, 2002);
        final Day day2 = new Day(29, SerialDate.MARCH, 2002);
        assertTrue(day1.equals(day2));
    }

    /**
     * In GMT, the end of 29 Feb 2004 is java.util.Date(1,078,099,199,999L).  Use this to check the
     * day constructor.
     */
    public void testDateConstructor1() {

        final TimeZone zone = TimeZone.getTimeZone("GMT");
        final Day d1 = new Day(new Date(1078099199999L), zone);
        final Day d2 = new Day(new Date(1078099200000L), zone);

        assertEquals(SerialDate.FEBRUARY, d1.getMonth());
        assertEquals(1078099199999L, d1.getLastMillisecond(zone));

        assertEquals(SerialDate.MARCH, d2.getMonth());
        assertEquals(1078099200000L, d2.getFirstMillisecond(zone));

    }

    /**
     * In Helsinki, the end of 29 Feb 2004 is java.util.Date(1,078,091,999,999L).  Use this to
     * check the Day constructor.
     */
    public void testDateConstructor2() {

        final TimeZone zone = TimeZone.getTimeZone("Europe/Helsinki");
        final Day d1 = new Day(new Date(1078091999999L), zone);
        final Day d2 = new Day(new Date(1078092000000L), zone);

        assertEquals(SerialDate.FEBRUARY, d1.getMonth());
        assertEquals(1078091999999L, d1.getLastMillisecond(zone));

        assertEquals(SerialDate.MARCH, d2.getMonth());
        assertEquals(1078092000000L, d2.getFirstMillisecond(zone));

    }

    /**
     * Set up a day equal to 1 January 1900.  Request the previous day, it should be null.
     */
    public void test1Jan1900Previous() {

        final Day jan1st1900 = new Day(1, SerialDate.JANUARY, 1900);
        final Day previous = (Day) jan1st1900.previous();
        assertNull(previous);

    }

    /**
     * Set up a day equal to 1 January 1900.  Request the next day, it should be 2 January 1900.
     */
    public void test1Jan1900Next() {

        final Day jan1st1900 = new Day(1, SerialDate.JANUARY, 1900);
        final Day next = (Day) jan1st1900.next();
        assertEquals(2, next.getDayOfMonth());

    }

    /**
     * Set up a day equal to 31 December 9999.  Request the previous day, it should be 30 December
     * 9999.
     */
    public void test31Dec9999Previous() {

        final Day dec31st9999 = new Day(31, SerialDate.DECEMBER, 9999);
        final Day previous = (Day) dec31st9999.previous();
        assertEquals(30, previous.getDayOfMonth());

    }

    /**
     * Set up a day equal to 31 December 9999.  Request the next day, it should be null.
     */
    public void test31Dec9999Next() {

        final Day dec31st9999 = new Day(31, SerialDate.DECEMBER, 9999);
        final Day next = (Day) dec31st9999.next();
        assertNull(next);

    }

    /**
     * Problem for date parsing.
     * <p>
     * This test works only correct if the short pattern of the date
     * format is "dd/MM/yyyy". If not, this test will result in a
     * false negative.
     * 
     * @throws ParseException on parsing errors.
     */
    public void testParseDay() throws ParseException {

        final GregorianCalendar gc = new GregorianCalendar(2001, 12, 31);
        final SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
        final Date reference = format.parse("31/12/2001");
        if (reference.equals(gc.getTime())) {
            // test 1...
            final Day d = Day.parseDay("31/12/2001");
            assertEquals(37256, d.getSerialDate().toSerial());
        }

        // test 2...
        final Day d = Day.parseDay("2001-12-31");
        assertEquals(37256, d.getSerialDate().toSerial());

    }

    /**
     * Serialize an instance, restore it, and check for equality.
     */
    public void testSerialization() {

        final Day d1 = new Day(15, 4, 2000);
        Day d2 = null;

        try {
            final ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            final ObjectOutput out = new ObjectOutputStream(buffer);
            out.writeObject(d1);
            out.close();

            final ObjectInput in = new ObjectInputStream(
                new ByteArrayInputStream(buffer.toByteArray())
            );
            d2 = (Day) in.readObject();
            in.close();
        }
        catch (Exception e) {
            System.out.println(e.toString());
        }
        assertEquals(d1, d2);

    }
    
    /**
     * Two objects that are equal are required to return the same hashCode. 
     */
    public void testHashcode() {
        final Day d1 = new Day(1, 2, 2003);
        final Day d2 = new Day(1, 2, 2003);
        assertTrue(d1.equals(d2));
        final int h1 = d1.hashCode();
        final int h2 = d2.hashCode();
        assertEquals(h1, h2);
    }

}
