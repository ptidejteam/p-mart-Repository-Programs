/* ======================================
 * JFreeChart : a free Java chart library
 * ======================================
 *
 * Project Info:  http://www.jfree.org/jfreechart/index.html
 * Project Lead:  David Gilbert (david.gilbert@object-refinery.com);
 *
 * (C) Copyright 2000-2003, by Object Refinery Limited.
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
 * --------------------
 * TimeSeriesTests.java
 * --------------------
 * (C) Copyright 2001-2003, by Object Refinery Limited.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited);
 * Contributor(s):   -;
 *
 * $Id: TimeSeriesTests.java,v 1.1 2007/10/10 19:09:21 vauchers Exp $
 *
 * Changes
 * -------
 * 16-Nov-2001 : Version 1 (DG);
 * 17-Oct-2002 : Fixed errors reported by Checkstyle (DG);
 * 13-Mar-2003 : Added serialization test (DG);
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

import org.jfree.data.SeriesException;
import org.jfree.data.time.Day;
import org.jfree.data.time.RegularTimePeriod;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.Year;
import org.jfree.date.SerialDate;

/**
 * A collection of test cases for the {@link TimeSeries} class.
 *
 * @author David Gilbert
 */
public class TimeSeriesTests extends TestCase {

    /** A time series. */
    private TimeSeries seriesA;

    /** A time series. */
    private TimeSeries seriesB;

    /** A time series. */
    private TimeSeries seriesC;

    /**
     * Returns the tests as a test suite.
     *
     * @return the test suite.
     */
    public static Test suite() {
        return new TestSuite(TimeSeriesTests.class);
    }

    /**
     * Constructs a new set of tests.
     *
     * @param name  the name of the tests.
     */
    public TimeSeriesTests(String name) {
        super(name);
    }

    /**
     * Common test setup.
     */
    protected void setUp() {

        seriesA = new TimeSeries("Series A", Year.class);
        try {
            seriesA.add(new Year(2000), new Integer(102000));
            seriesA.add(new Year(2001), new Integer(102001));
            seriesA.add(new Year(2002), new Integer(102002));
            seriesA.add(new Year(2003), new Integer(102003));
            seriesA.add(new Year(2004), new Integer(102004));
            seriesA.add(new Year(2005), new Integer(102005));
        }
        catch (SeriesException e) {
            System.err.println("TimeSeriesTests.setUp(): problem creating series.");
        }

        seriesB = new TimeSeries("Series B", Year.class);
        try {
            seriesB.add(new Year(2006), new Integer(202006));
            seriesB.add(new Year(2007), new Integer(202007));
            seriesB.add(new Year(2008), new Integer(202008));
        }
        catch (SeriesException e) {
            System.err.println("TimeSeriesTests.setUp(): problem creating series.");
        }

        seriesC = new TimeSeries("Series C", Year.class);
        try {
            seriesC.add(new Year(1999), new Integer(301999));
            seriesC.add(new Year(2000), new Integer(302000));
            seriesC.add(new Year(2002), new Integer(302002));
        }
        catch (SeriesException e) {
            System.err.println("TimeSeriesTests.setUp(): problem creating series.");
        }

    }

    /**
     * Set up a quarter equal to Q1 1900.  Request the previous quarter, it should be null.
     */
    public void testClone() {

        TimeSeries series = new TimeSeries("Test Series");

        RegularTimePeriod jan1st2002 = new Day(1, SerialDate.JANUARY, 2002);
        try {
            series.add(jan1st2002, new Integer(42));
        }
        catch (SeriesException e) {
            System.err.println("TimeSeriesTests.testClone: problem adding to series.");
        }

        TimeSeries clone = (TimeSeries) series.clone();
        clone.setName("Clone Series");
        try {
            clone.update(jan1st2002, new Integer(10));
        }
        catch (SeriesException e) {
            System.err.println("TimeSeriesTests.testClone: problem updating series.");
        }

        int seriesValue = series.getValue(jan1st2002).intValue();
        int cloneValue = clone.getValue(jan1st2002).intValue();

        assertEquals(42, seriesValue);
        assertEquals(10, cloneValue);
        assertEquals("Test Series", series.getName());
        assertEquals("Clone Series", clone.getName());

    }

    /**
     * Add a value to series A for 1999.  It should be added at index 0.
     */
    public void testAddValue() {

        try {
            seriesA.add(new Year(1999), new Integer(1));
        }
        catch (SeriesException e) {
            System.err.println("TimeSeriesTests.testAddValue: problem adding to series.");
        }

        int value = seriesA.getValue(0).intValue();
        assertEquals(1, value);

    }

    /**
     * Tests the retrieval of values.
     */
    public void testGetValue() {

        Number value1 = seriesA.getValue(new Year(1999));
        assertNull(value1);
        int value2 = seriesA.getValue(new Year(2000)).intValue();
        assertEquals(102000, value2);

    }

    /**
     * Tests the deletion of values.
     */
    public void testDelete() {

        seriesA.delete(0, 0);
        assertEquals(5, seriesA.getItemCount());
        Number value = seriesA.getValue(new Year(2000));
        assertNull(value);

    }

    /**
     * Serialize an instance, restore it, and check for equality.
     */
    public void testSerialization() {

        TimeSeries s1 = new TimeSeries("A test", Year.class);
        s1.add(new Year(2000), 13.75);
        s1.add(new Year(2001), 11.90);
        s1.add(new Year(2002), null);
        s1.add(new Year(2005), 19.32);
        s1.add(new Year(2007), 16.89);
        TimeSeries s2 = null;

        try {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            ObjectOutput out = new ObjectOutputStream(buffer);
            out.writeObject(s1);
            out.close();

            ObjectInput in = new ObjectInputStream(new ByteArrayInputStream(buffer.toByteArray()));
            s2 = (TimeSeries) in.readObject();
            in.close();
        }
        catch (Exception e) {
            System.out.println(e.toString());
        }
        assertTrue(s1.equals(s2));

    }

    /**
     * Tests the equals method.
     */
    public void testEquals() {
        TimeSeries s1 = new TimeSeries("Time Series 1");
        TimeSeries s2 = new TimeSeries("Time Series 2");
        boolean b1 = s1.equals(s2);
        assertFalse("b1", b1);

        s2.setName("Time Series 1");
        boolean b2 = s1.equals(s2);
        assertTrue("b2", b2);

        RegularTimePeriod p1 = new Day();
        RegularTimePeriod p2 = p1.next();
        s1.add(p1, 100.0);
        s1.add(p2, 200.0);
        boolean b3 = s1.equals(s2);
        assertFalse("b3", b3);

        s2.add(p1, 100.0);
        s2.add(p2, 200.0);
        boolean b4 = s1.equals(s2);
        assertTrue("b4", b4);

        s1.setMaximumItemCount(100);
        boolean b5 = s1.equals(s2);
        assertFalse("b5", b5);

        s2.setMaximumItemCount(100);
        boolean b6 = s1.equals(s2);
        assertTrue("b6", b6);

        s1.setHistoryCount(100);
        boolean b7 = s1.equals(s2);
        assertFalse("b7", b7);

        s2.setHistoryCount(100);
        boolean b8 = s1.equals(s2);
        assertTrue("b8", b8);

    }

}
