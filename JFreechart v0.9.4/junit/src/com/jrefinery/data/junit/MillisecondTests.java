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
 * ---------------------
 * MillisecondTests.java
 * ---------------------
 * (C) Copyright 2002, by Simba Management Limited.
 *
 * Original Author:  David Gilbert (for Simba Management Limited);
 * Contributor(s):   -;
 *
 * $Id: MillisecondTests.java,v 1.1 2007/10/10 21:48:09 vauchers Exp $
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
import com.jrefinery.data.Millisecond;
import com.jrefinery.data.Second;
import com.jrefinery.data.Minute;
import com.jrefinery.data.Hour;
import com.jrefinery.data.Day;
import com.jrefinery.date.SerialDate;

/**
 * Tests for the Millisecond class.
 *
 * @author DG
 */
public class MillisecondTests extends TestCase {

    /**
     * Returns the tests as a test suite.
     *
     * @return the test suite.
     */
    public static Test suite() {
        return new TestSuite(MillisecondTests.class);
    }

    /**
     * Constructs a new set of tests.
     *
     * @param name  the name of the tests.
     */
    public MillisecondTests(String name) {
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
        Millisecond millisecond = new Millisecond();
        assertTrue(millisecond.equals(millisecond));
    }

    /**
     * Tests the equals method.
     */
    public void testEquals() {
        Day day1 = new Day(29, SerialDate.MARCH, 2002);
        Hour hour1 = new Hour(15, day1);
        Minute minute1 = new Minute(15, hour1);
        Second second1 = new Second(34, minute1);
        Millisecond milli1 = new Millisecond(999, second1);
        Day day2 = new Day(29, SerialDate.MARCH, 2002);
        Hour hour2 = new Hour(15, day2);
        Minute minute2 = new Minute(15, hour2);
        Second second2 = new Second(34, minute2);
        Millisecond milli2 = new Millisecond(999, second2);
        assertTrue(milli1.equals(milli2));
    }

    /**
     * In GMT, the 4.55:59.123pm on 21 Mar 2002 is java.util.Date(1016729759123L).
     * Use this to check the Second constructor.
     */
    public void testDateConstructor1() {

        TimeZone zone = TimeZone.getTimeZone("GMT");
        Millisecond m1 = new Millisecond(new Date(1016729759122L), zone);
        Millisecond m2 = new Millisecond(new Date(1016729759123L), zone);

        assertEquals(122, m1.getMillisecond());
        assertEquals(1016729759122L, m1.getEnd(zone));

        assertEquals(123, m2.getMillisecond());
        assertEquals(1016729759123L, m2.getStart(zone));

    }

    /**
     * In Tallinn, the 4.55:59.123pm on 21 Mar 2002 is java.util.Date(1016722559123L).
     * Use this to check the Second constructor.
     */
    public void testDateConstructor2() {

        TimeZone zone = TimeZone.getTimeZone("Europe/Tallinn");
        Millisecond m1 = new Millisecond(new Date(1016722559122L), zone);
        Millisecond m2 = new Millisecond(new Date(1016722559123L), zone);

        assertEquals(122, m1.getMillisecond());
        assertEquals(1016722559122L, m1.getEnd(zone));

        assertEquals(123, m2.getMillisecond());
        assertEquals(1016722559123L, m2.getStart(zone));

    }

}
