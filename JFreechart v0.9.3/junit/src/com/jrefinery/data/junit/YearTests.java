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
 * --------------
 * YearTests.java
 * --------------
 * (C) Copyright 2001, 2002, by Simba Management Limited.
 *
 * Original Author:  David Gilbert (for Simba Management Limited);
 * Contributor(s):   -;
 *
 * $Id: YearTests.java,v 1.1 2007/10/10 19:52:22 vauchers Exp $
 *
 * Changes
 * -------
 * 16-Nov-2001 : Version 1 (DG);
 * 19-Mar-2002 : Added tests for constructor that uses java.util.Date to ensure it is
 *               consistent with the getStart() and getEnd() methods (DG);
 *
 */

package com.jrefinery.data.junit;

import java.util.Date;
import java.util.TimeZone;
import junit.framework.*;
import com.jrefinery.data.*;

/**
 * Tests for the Year class.
 */
public class YearTests extends TestCase {

    /**
     * Returns the tests as a test suite.
     */
    public static Test suite() {
        return new TestSuite(YearTests.class);
    }

    /**
     * Constructs a new set of tests.
     * @param The name of the tests.
     */
    public YearTests(String name) {
        super(name);
    }

    /**
     * Common test setup.
     */
    protected void setUp() {

    }

    /**
     * Test that a Year instance is equal to itself.
     *
     * SourceForge Bug ID: 558850.
     */
    public void testEqualsSelf() {
        Year year = new Year();
        this.assertTrue(year.equals(year));
    }

    public void testEquals() {
        Year year1 = new Year(2002);
        Year year2 = new Year(2002);
        this.assertTrue(year1.equals(year2));
    }

    /**
     * In GMT, the end of 2001 is java.util.Date(1009843199999L).  Use this to check the
     * year constructor.
     */
    public void testDateConstructor1() {

        TimeZone zone = TimeZone.getTimeZone("GMT");
        Year y1 = new Year(new Date(1009843199999L), zone);
        Year y2 = new Year(new Date(1009843200000L), zone);

        this.assertEquals(2001, y1.getYear());
        this.assertEquals(1009843199999L, y1.getEnd(zone));

        this.assertEquals(2002, y2.getYear());
        this.assertEquals(1009843200000L, y2.getStart(zone));

    }

    /**
     * In Los Angeles, the end of 2001 is java.util.Date(1009871999999L).  Use this to check the
     * year constructor.
     */
    public void testDateConstructor2() {

        TimeZone zone = TimeZone.getTimeZone("America/Los_Angeles");
        Year y1 = new Year(new Date(1009871999999L), zone);
        Year y2 = new Year(new Date(1009872000000L), zone);

        this.assertEquals(2001, y1.getYear());
        this.assertEquals(1009871999999L, y1.getEnd(zone));

        this.assertEquals(2002, y2.getYear());
        this.assertEquals(1009872000000L, y2.getStart(zone));

    }

    /**
     * Set up a year equal to 1900.  Request the previous year, it should be null.
     */
    public void test1900_previous() {
        Year current = new Year(1900);
        Year previous = (Year)current.previous();
        this.assertNull(previous);
    }

    /**
     * Set up a year equal to 1900.  Request the next year, it should be 1901.
     */
    public void test1900_next() {
        Year current = new Year(1900);
        Year next = (Year)current.next();
        this.assertEquals(1901, next.getYear());
    }

    /**
     * Set up a year equal to 9999.  Request the previous year, it should be 9998.
     */
    public void test9999_previous() {
        Year current = new Year(9999);
        Year previous = (Year)current.previous();
        this.assertEquals(9998, previous.getYear());
    }

    /**
     * Set up a year equal to 9999.  Request the next year, it should be null.
     */
    public void test9999_next() {
        Year current = new Year(9999);
        Year next = (Year)current.next();
        this.assertNull(next);
    }

    public void testParseYear() {

        Year year = null;

        // test 1...
        try {
            year = Year.parseYear("2000");
        }
        catch (TimePeriodFormatException e) {
            year = new Year(1900);
        }
        this.assertEquals(2000, year.getYear());

        // test 2...
        try {
            year = Year.parseYear(" 2001 ");
        }
        catch (TimePeriodFormatException e) {
            year = new Year(1900);
        }
        this.assertEquals(2001, year.getYear());

        // test 3...
        try {
            year = Year.parseYear("99");
        }
        catch (TimePeriodFormatException e) {
            year = new Year(1900);
        }
        this.assertEquals(1900, year.getYear());

    }

}
