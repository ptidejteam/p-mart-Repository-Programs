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
 * -----------------
 * QuarterTests.java
 * -----------------
 * (C) Copyright 2001, 2002, by Simba Management Limited.
 *
 * Original Author:  David Gilbert (for Simba Management Limited);
 * Contributor(s):   -;
 *
 * $Id: QuarterTests.java,v 1.1 2007/10/10 19:52:22 vauchers Exp $
 *
 * Changes
 * -------
 * 16-Nov-2001 : Version 1 (DG);
 *
 */

package com.jrefinery.data.junit;

import java.util.Date;
import java.util.TimeZone;
import junit.framework.*;
import com.jrefinery.data.*;

/**
 * Tests for the Quarter class.
 */
public class QuarterTests extends TestCase {

    protected Quarter q1_1900;
    protected Quarter q2_1900;
    protected Quarter q3_9999;
    protected Quarter q4_9999;

    /**
     * Returns the tests as a test suite.
     */
    public static Test suite() {
        return new TestSuite(QuarterTests.class);
    }

    /**
     * Constructs a new set of tests.
     * @param The name of the tests.
     */
    public QuarterTests(String name) {
        super(name);
    }

    /**
     * Common test setup.
     */
    protected void setUp() {
        q1_1900 = new Quarter(1, 1900);
        q2_1900 = new Quarter(2, 1900);
        q3_9999 = new Quarter(3, 9999);
        q4_9999 = new Quarter(4, 9999);
    }

    /**
     * Test that a Quarter instance is equal to itself.
     *
     * SourceForge Bug ID: 558850.
     */
    public void testEqualsSelf() {
        Quarter quarter = new Quarter();
        this.assertTrue(quarter.equals(quarter));
    }

    public void testEquals() {
        Quarter q1 = new Quarter(2, 2002);
        Quarter q2 = new Quarter(2, 2002);
        this.assertTrue(q1.equals(q2));
    }

    /**
     * In GMT, the end of Q1 2002 is java.util.Date(1017619199999L).  Use this to check the
     * quarter constructor.
     */
    public void testDateConstructor1() {

        TimeZone zone = TimeZone.getTimeZone("GMT");
        Quarter q1 = new Quarter(new Date(1017619199999L), zone);
        Quarter q2 = new Quarter(new Date(1017619200000L), zone);

        this.assertEquals(1, q1.getQuarter());
        this.assertEquals(1017619199999L, q1.getEnd(zone));

        this.assertEquals(2, q2.getQuarter());
        this.assertEquals(1017619200000L, q2.getStart(zone));

    }

    /**
     * In Istanbul, the end of Q1 2002 is java.util.Date(1017608399999L).  Use this to check the
     * quarter constructor.
     */
    public void testDateConstructor2() {

        TimeZone zone = TimeZone.getTimeZone("Europe/Istanbul");
        Quarter q1 = new Quarter(new Date(1017608399999L), zone);
        Quarter q2 = new Quarter(new Date(1017608400000L), zone);

        this.assertEquals(1, q1.getQuarter());
        this.assertEquals(1017608399999L, q1.getEnd(zone));

        this.assertEquals(2, q2.getQuarter());
        this.assertEquals(1017608400000L, q2.getStart(zone));

    }

    /**
     * Set up a quarter equal to Q1 1900.  Request the previous quarter, it should be null.
     */
    public void testQ1_1900_previous() {
        Quarter previous = (Quarter)q1_1900.previous();
        this.assertNull(previous);
    }

    /**
     * Set up a quarter equal to Q1 1900.  Request the next quarter, it should be Q2 1900.
     */
    public void testQ1_1900_next() {
        Quarter next = (Quarter)q1_1900.next();
        this.assertEquals(q2_1900, next);
    }

    /**
     * Set up a quarter equal to Q4 9999.  Request the previous quarter, it should be Q3 9999.
     */
    public void testQ4_9999_previous() {
        Quarter previous = (Quarter)q4_9999.previous();
        this.assertEquals(q3_9999, previous);
    }

    /**
     * Set up a quarter equal to Q4 9999.  Request the next quarter, it should be null.
     */
    public void testQ4_9999_next() {
        Quarter next = (Quarter)q4_9999.next();
        this.assertNull(next);
    }

    /**
     * Test the string parsing code...
     */
    public void testParseQuarter() {

        Quarter quarter = null;

        // test 1...
        try {
            quarter = Quarter.parseQuarter("Q1-2000");
        }
        catch (TimePeriodFormatException e) {
            quarter = new Quarter(1, 1900);
        }
        this.assertEquals(1, quarter.getQuarter());
        this.assertEquals(2000, quarter.getYear().getYear());

        // test 2...
        try {
            quarter = Quarter.parseQuarter("2001-Q2");
        }
        catch (TimePeriodFormatException e) {
            quarter = new Quarter(1, 1900);
        }
        this.assertEquals(2, quarter.getQuarter());
        this.assertEquals(2001, quarter.getYear().getYear());

        // test 3...
        try {
            quarter = Quarter.parseQuarter("Q3, 2002");
        }
        catch (TimePeriodFormatException e) {
            quarter = new Quarter(1, 1900);
        }
        this.assertEquals(3, quarter.getQuarter());
        this.assertEquals(2002, quarter.getYear().getYear());

    }

}
