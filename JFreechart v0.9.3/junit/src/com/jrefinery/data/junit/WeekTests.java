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
 * ---------------
 * MonthTests.java
 * ---------------
 * (C) Copyright 2002, by Simba Management Limited.
 *
 * Original Author:  David Gilbert (for Simba Management Limited);
 * Contributor(s):   -;
 *
 * $Id: WeekTests.java,v 1.1 2007/10/10 19:52:22 vauchers Exp $
 *
 * Changes
 * -------
 * 05-Apr-2002 : Version 1 (DG);
 * 26-Jun-2002 : Removed unnecessary imports (DG);
 *
 */

package com.jrefinery.data.junit;

import junit.framework.*;
import com.jrefinery.data.*;

/**
 * Tests for the Week class.
 */
public class WeekTests extends TestCase {

    protected Week w1_1900;
    protected Week w2_1900;
    protected Week w51_9999;
    protected Week w52_9999;

    /**
     * Returns the tests as a test suite.
     */
    public static Test suite() {
        return new TestSuite(WeekTests.class);
    }

    /**
     * Constructs a new set of tests.
     * @param The name of the tests.
     */
    public WeekTests(String name) {
        super(name);
    }

    /**
     * Common test setup.
     */
    protected void setUp() {
        w1_1900 = new Week(1, 1900);
        w2_1900 = new Week(2, 1900);
        w51_9999 = new Week(51, 9999);
        w52_9999 = new Week(52, 9999);
    }

    public void testEquals() {
        Week w1 = new Week(1, 2002);
        Week w2 = new Week(1, 2002);
        this.assertTrue(w1.equals(w2));
    }

//    /**
//     * In GMT, the end of Feb 2000 is java.util.Date(951,868,799,999L).  Use this to check the
//     * Month constructor.
//     */
//    public void testDateConstructor1() {

//        TimeZone zone = TimeZone.getTimeZone("GMT");
//        Month m1 = new Month(new Date(951868799999L), zone);
//        Month m2 = new Month(new Date(951868800000L), zone);
//
//        this.assertEquals(SerialDate.FEBRUARY, m1.getMonth());
//        this.assertEquals(951868799999L, m1.getEnd(zone));

//        this.assertEquals(SerialDate.MARCH, m2.getMonth());
//        this.assertEquals(951868800000L, m2.getStart(zone));

//    }

//    /**
//     * In Auckland, the end of Feb 2000 is java.util.Date(951,821,999,999L).  Use this to check the
//     * Month constructor.
//     */
//    public void testDateConstructor2() {

//        TimeZone zone = TimeZone.getTimeZone("Pacific/Auckland");
//        Month m1 = new Month(new Date(951821999999L), zone);
//        Month m2 = new Month(new Date(951822000000L), zone);

//        this.assertEquals(SerialDate.FEBRUARY, m1.getMonth());
//        this.assertEquals(951821999999L, m1.getEnd(zone));

//        this.assertEquals(SerialDate.MARCH, m2.getMonth());
//        this.assertEquals(951822000000L, m2.getStart(zone));

//    }

    /**
     * Request the week before week 1, 1900: it should be null.
     */
    public void testW1_1900_previous() {
        Week previous = (Week)w1_1900.previous();
        this.assertNull(previous);
    }

    /**
     * Request the week after week 1, 1900: it should be week 2, 1900.
     */
    public void testW1_1900_next() {
        Week next = (Week)w1_1900.next();
        this.assertEquals(w2_1900, next);
    }

    /**
     * Request the week before w52, 9999: it should be week 51, 9999.
     */
    public void testW52_9999_previous() {
        Week previous = (Week)w52_9999.previous();
        this.assertEquals(w51_9999, previous);
    }

    /**
     * Request the week after w52, 9999: it should be null.
     */
    public void testW52_9999_next() {
        Week next = (Week)w52_9999.next();
        this.assertNull(next);
    }

//    /**
//     * Test the string parsing code...
//     */
//    public void testParseMonth() {
//
//        Month month = null;
//
//        // test 1...
//        try {
//            month = Month.parseMonth("1990-01");
//        }
//        catch (TimePeriodFormatException e) {
//            month = new Month(1, 1900);
//        }
//        this.assertEquals(1, month.getMonth());
//        this.assertEquals(1990, month.getYear().getYear());
//
//        // test 2...
//        try {
//            month = Month.parseMonth("02-1991");
//        }
//        catch (TimePeriodFormatException e) {
//            month = new Month(1, 1900);
//        }
//        this.assertEquals(2, month.getMonth());
//        this.assertEquals(1991, month.getYear().getYear());
//
//        // test 3...
//        try {
//            month = Month.parseMonth("March 1993");
//        }
//        catch (TimePeriodFormatException e) {
//            month = new Month(1, 1900);
//        }
//        this.assertEquals(3, month.getMonth());
//        this.assertEquals(1993, month.getYear().getYear());
//
//    }

}
