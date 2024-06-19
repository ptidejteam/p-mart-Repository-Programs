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
 * ---------------
 * MonthTests.java
 * ---------------
 * (C) Copyright 2002, 2003 by Simba Management Limited.
 *
 * Original Author:  David Gilbert (for Simba Management Limited);
 * Contributor(s):   -;
 *
 * $Id: WeekTests.java,v 1.1 2007/10/10 19:54:28 vauchers Exp $
 *
 * Changes
 * -------
 * 05-Apr-2002 : Version 1 (DG);
 * 26-Jun-2002 : Removed unnecessary imports (DG);
 * 17-Oct-2002 : Fixed errors reported by Checkstyle (DG);
 *
 */

package com.jrefinery.data.junit;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import com.jrefinery.data.Week;

/**
 * Tests for the {@link Week} class.
 *
 * @author David Gilbert
 */
public class WeekTests extends TestCase {

    /** A week. */
    private Week w1Y1900;

    /** A week. */
    private Week w2Y1900;

    /** A week. */
    private Week w51Y9999;

    /** A week. */
    private Week w52Y9999;

    /**
     * Returns the tests as a test suite.
     *
     * @return the test suite.
     */
    public static Test suite() {
        return new TestSuite(WeekTests.class);
    }

    /**
     * Constructs a new set of tests.
     *
     * @param name  the name of the tests.
     */
    public WeekTests(String name) {
        super(name);
    }

    /**
     * Common test setup.
     */
    protected void setUp() {
        w1Y1900 = new Week(1, 1900);
        w2Y1900 = new Week(2, 1900);
        w51Y9999 = new Week(51, 9999);
        w52Y9999 = new Week(52, 9999);
    }

    /**
     * Tests the equals method.
     */
    public void testEquals() {
        Week w1 = new Week(1, 2002);
        Week w2 = new Week(1, 2002);
        assertTrue(w1.equals(w2));
    }

    /**
     * Request the week before week 1, 1900: it should be null.
     */
    public void testW1Y1900Previous() {
        Week previous = (Week) w1Y1900.previous();
        assertNull(previous);
    }

    /**
     * Request the week after week 1, 1900: it should be week 2, 1900.
     */
    public void testW1Y1900Next() {
        Week next = (Week) w1Y1900.next();
        assertEquals(w2Y1900, next);
    }

    /**
     * Request the week before w52, 9999: it should be week 51, 9999.
     */
    public void testW52Y9999Previous() {
        Week previous = (Week) w52Y9999.previous();
        assertEquals(w51Y9999, previous);
    }

    /**
     * Request the week after w52, 9999: it should be null.
     */
    public void testW52Y9999Next() {
        Week next = (Week) w52Y9999.next();
        assertNull(next);
    }

}
