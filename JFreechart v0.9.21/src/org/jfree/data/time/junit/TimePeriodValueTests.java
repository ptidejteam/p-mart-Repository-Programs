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
 * -------------------------
 * TimePeriodValueTests.java
 * -------------------------
 * (C) Copyright 2003 by Object Refinery Limited.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited);
 * Contributor(s):   -;
 *
 * $Id: TimePeriodValueTests.java,v 1.1 2007/10/10 19:50:27 vauchers Exp $
 *
 * Changes
 * -------
 * 30-Jul-2003 : Version 1 (DG);
 *
 */

package org.jfree.data.time.junit;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.jfree.data.time.Day;
import org.jfree.data.time.TimePeriodValue;

/**
 * Tests for the {@link TimePeriodValue} class.
 */
public class TimePeriodValueTests extends TestCase {

    /**
     * Returns the tests as a test suite.
     *
     * @return The test suite.
     */
    public static Test suite() {
        return new TestSuite(TimePeriodValueTests.class);
    }

    /**
     * Constructs a new set of tests.
     *
     * @param name  the name of the tests.
     */
    public TimePeriodValueTests(final String name) {
        super(name);
    }

    /**
     * Common test setup.
     */
    protected void setUp() {
        // no setup
    }

    /**
     * Test that an instance is equal to itself.
     */
    public void testEqualsSelf() {
        final TimePeriodValue tpv = new TimePeriodValue(new Day(), 55.75);
        assertTrue(tpv.equals(tpv));
    }

    /**
     * Tests the equals() method.
     */
    public void testEquals() {
        final TimePeriodValue tpv1 = new TimePeriodValue(new Day(30, 7, 2003), 55.75);
        final TimePeriodValue tpv2 = new TimePeriodValue(new Day(30, 7, 2003), 55.75);
        assertTrue(tpv1.equals(tpv2));
        assertTrue(tpv2.equals(tpv1));
    }

    /**
     * Serialize an instance, restore it, and check for equality.
     */
    public void testSerialization() {

        final TimePeriodValue tpv1 = new TimePeriodValue(new Day(30, 7, 2003), 55.75);
        TimePeriodValue tpv2 = null;

        try {
            final ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            final ObjectOutput out = new ObjectOutputStream(buffer);
            out.writeObject(tpv1);
            out.close();

            final ObjectInput in = new ObjectInputStream(
                new ByteArrayInputStream(buffer.toByteArray())
            );
            tpv2 = (TimePeriodValue) in.readObject();
            in.close();
        }
        catch (Exception e) {
            System.out.println(e.toString());
        }
        assertEquals(tpv1, tpv2);

    }

}
