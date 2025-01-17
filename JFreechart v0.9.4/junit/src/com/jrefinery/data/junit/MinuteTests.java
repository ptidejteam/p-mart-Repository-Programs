/* ======================================
 * JFreeChart : a free Java chart library
 * ======================================
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
 * ----------------
 * MinuteTests.java
 * ----------------
 * (C) Copyright 2002, by Simba Management Limited.
 *
 * Original Author:  David Gilbert (for Simba Management Limited);
 * Contributor(s):   -;
 *
 * $Id: MinuteTests.java,v 1.1 2007/10/10 21:48:09 vauchers Exp $
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
import com.jrefinery.data.Minute;
import com.jrefinery.data.Hour;
import com.jrefinery.data.Day;
import com.jrefinery.date.SerialDate;

/**
 * Tests for the Minute class.
 *
 * @author DG
 */
public class MinuteTests extends TestCase {

    /**
     * Returns the tests as a test suite.
     *
     * @return the test suite.
     */
    public static Test suite() {
        return new TestSuite(MinuteTests.class);
    }

    /**
     * Constructs a new set of tests.
     *
     * @param name  the name of the tests.
     */
    public MinuteTests(String name) {
        super(name);
    }

    /**
     * Common test setup.
     */
    protected void setUp() {

    }

    /**
     * Test that a Minute instance is equal to itself.
     *
     * SourceForge Bug ID: 558850.
     */
    public void testEqualsSelf() {
        Minute minute = new Minute();
        assertTrue(minute.equals(minute));
    }

    /**
     * Tests the equals method.
     */
    public void testEquals() {
        Day day1 = new Day(29, SerialDate.MARCH, 2002);
        Hour hour1 = new Hour(15, day1);
        Minute minute1 = new Minute(15, hour1);
        Day day2 = new Day(29, SerialDate.MARCH, 2002);
        Hour hour2 = new Hour(15, day2);
        Minute minute2 = new Minute(15, hour2);
        assertTrue(minute1.equals(minute2));
    }

    /**
     * In GMT, the 4.55pm on 21 Mar 2002 is java.util.Date(1016729700000L).
     * Use this to check the Minute constructor.
     */
    public void testDateConstructor1() {

        TimeZone zone = TimeZone.getTimeZone("GMT");
        Minute m1 = new Minute(new Date(1016729699999L), zone);
        Minute m2 = new Minute(new Date(1016729700000L), zone);

        assertEquals(54, m1.getMinute());
        assertEquals(1016729699999L, m1.getEnd(zone));

        assertEquals(55, m2.getMinute());
        assertEquals(1016729700000L, m2.getStart(zone));

    }

    /**
     * In Singapore, the 4.55pm on 21 Mar 2002 is java.util.Date(1,014,281,700,000L).
     * Use this to check the Minute constructor.
     */
    public void testDateConstructor2() {

        TimeZone zone = TimeZone.getTimeZone("Asia/Singapore");
        Minute m1 = new Minute(new Date(1016700899999L), zone);
        Minute m2 = new Minute(new Date(1016700900000L), zone);

        assertEquals(54, m1.getMinute());
        assertEquals(1016700899999L, m1.getEnd(zone));

        assertEquals(55, m2.getMinute());
        assertEquals(1016700900000L, m2.getStart(zone));

    }

}
