/* ===========================================================
 * JFreeChart : a free chart library for the Java(tm) platform
 * ===========================================================
 *
 * (C) Copyright 2000-2007, by Object Refinery Limited and Contributors.
 *
 * Project Info:  http://www.jfree.org/jfreechart/index.html
 *
 * This library is free software; you can redistribute it and/or modify it 
 * under the terms of the GNU Lesser General Public License as published by 
 * the Free Software Foundation; either version 2.1 of the License, or 
 * (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but 
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY 
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public 
 * License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, 
 * USA.  
 *
 * [Java is a trademark or registered trademark of Sun Microsystems, Inc. 
 * in the United States and other countries.]
 *
 * ------------------------------
 * TimeSeriesCollectionTests.java
 * ------------------------------
 * (C) Copyright 2003-2007, by Object Refinery Limited.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited);
 * Contributor(s):   -;
 *
 * $Id: TimeSeriesCollectionTests.java,v 1.1 2007/10/10 20:52:50 vauchers Exp $
 *
 * Changes
 * -------
 * 01-May-2003 : Version 1 (DG);
 * 04-Dec-2003 : Added a test for the getSurroundingItems() method (DG);
 * 08-May-2007 : Added testIndexOf() method (DG);
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
import org.jfree.data.time.RegularTimePeriod;
import org.jfree.data.time.TimePeriodAnchor;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;

/**
 * A collection of test cases for the {@link TimeSeriesCollection} class.
 */
public class TimeSeriesCollectionTests extends TestCase {

    /**
     * Returns the tests as a test suite.
     *
     * @return The test suite.
     */
    public static Test suite() {
        return new TestSuite(TimeSeriesCollectionTests.class);
    }

    /**
     * Constructs a new set of tests.
     *
     * @param name  the name of the tests.
     */
    public TimeSeriesCollectionTests(String name) {
        super(name);
    }

    /**
     * Some tests for the equals() method.
     */
    public void testEquals() {

        TimeSeriesCollection c1 = new TimeSeriesCollection();
        TimeSeriesCollection c2 = new TimeSeriesCollection();

        TimeSeries s1 = new TimeSeries("Series 1");
        TimeSeries s2 = new TimeSeries("Series 2");

        // newly created collections should be equal
        boolean b1 = c1.equals(c2);
        assertTrue("b1", b1);

        // add series to collection 1, should be not equal
        c1.addSeries(s1);
        c1.addSeries(s2);
        boolean b2 = c1.equals(c2);
        assertFalse("b2", b2);

        // now add the same series to collection 2 to make them equal again...
        c2.addSeries(s1);
        c2.addSeries(s2);
        boolean b3 = c1.equals(c2);
        assertTrue("b3", b3);

        // now remove series 2 from collection 2
        c2.removeSeries(s2);
        boolean b4 = c1.equals(c2);
        assertFalse("b4", b4);

        // now remove series 2 from collection 1 to make them equal again
        c1.removeSeries(s2);
        boolean b5 = c1.equals(c2);
        assertTrue("b5", b5);
    }

    /**
     * Tests the remove series method.
     */
    public void testRemoveSeries() {

        TimeSeriesCollection c1 = new TimeSeriesCollection();

        TimeSeries s1 = new TimeSeries("Series 1");
        TimeSeries s2 = new TimeSeries("Series 2");
        TimeSeries s3 = new TimeSeries("Series 3");
        TimeSeries s4 = new TimeSeries("Series 4");

        c1.addSeries(s1);
        c1.addSeries(s2);
        c1.addSeries(s3);
        c1.addSeries(s4);

        c1.removeSeries(s3);

        TimeSeries s = c1.getSeries(2);
        boolean b1 = s.equals(s4);
        assertTrue(b1);

    }
    
    /**
     * Test the getSurroundingItems() method to ensure it is returning the 
     * values we expect.
     */
    public void testGetSurroundingItems() {
        
        TimeSeries series = new TimeSeries("Series 1", Day.class);
        TimeSeriesCollection collection = new TimeSeriesCollection(series);
        collection.setXPosition(TimePeriodAnchor.MIDDLE);
        
        // for a series with no data, we expect {-1, -1}...
        int[] result = collection.getSurroundingItems(0, 1000L);
        assertTrue(result[0] == -1);
        assertTrue(result[1] == -1);
        
        // now test with a single value in the series...
        Day today = new Day();
        long start1 = today.getFirstMillisecond();
        long middle1 = today.getMiddleMillisecond();
        long end1 = today.getLastMillisecond();
        
        series.add(today, 99.9);
        result = collection.getSurroundingItems(0, start1);
        assertTrue(result[0] == -1);
        assertTrue(result[1] == 0);
        
        result = collection.getSurroundingItems(0, middle1);
        assertTrue(result[0] == 0);
        assertTrue(result[1] == 0);
        
        result = collection.getSurroundingItems(0, end1);
        assertTrue(result[0] == 0);
        assertTrue(result[1] == -1);
        
        // now add a second value to the series...
        Day tomorrow = (Day) today.next();
        long start2 = tomorrow.getFirstMillisecond();
        long middle2 = tomorrow.getMiddleMillisecond();
        long end2 = tomorrow.getLastMillisecond();
        
        series.add(tomorrow, 199.9);
        result = collection.getSurroundingItems(0, start2);
        assertTrue(result[0] == 0);
        assertTrue(result[1] == 1);
        
        result = collection.getSurroundingItems(0, middle2);
        assertTrue(result[0] == 1);
        assertTrue(result[1] == 1);
        
        result = collection.getSurroundingItems(0, end2);
        assertTrue(result[0] == 1);
        assertTrue(result[1] == -1);
        
        // now add a third value to the series...
        Day yesterday = (Day) today.previous();
        long start3 = yesterday.getFirstMillisecond();
        long middle3 = yesterday.getMiddleMillisecond();
        long end3 = yesterday.getLastMillisecond();
        
        series.add(yesterday, 1.23);
        result = collection.getSurroundingItems(0, start3);
        assertTrue(result[0] == -1);
        assertTrue(result[1] == 0);
        
        result = collection.getSurroundingItems(0, middle3);
        assertTrue(result[0] == 0);
        assertTrue(result[1] == 0);
        
        result = collection.getSurroundingItems(0, end3);
        assertTrue(result[0] == 0);
        assertTrue(result[1] == 1);
        
    }
    
    /**
     * Serialize an instance, restore it, and check for equality.
     */
    public void testSerialization() {

        TimeSeriesCollection c1 = new TimeSeriesCollection(createSeries());
        TimeSeriesCollection c2 = null;

        try {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            ObjectOutput out = new ObjectOutputStream(buffer);
            out.writeObject(c1);
            out.close();

            ObjectInput in = new ObjectInputStream(
                new ByteArrayInputStream(buffer.toByteArray())
            );
            c2 = (TimeSeriesCollection) in.readObject();
            in.close();
        }
        catch (Exception e) {
            System.out.println(e.toString());
        }
        assertEquals(c1, c2);

    }

    /**
     * Creates a time series for testing.
     * 
     * @return A time series.
     */
    private TimeSeries createSeries() {
        RegularTimePeriod t = new Day();
        TimeSeries series = new TimeSeries("Test");
        series.add(t, 1.0);
        t = t.next();
        series.add(t, 2.0);
        t = t.next();
        series.add(t, null);
        t = t.next();
        series.add(t, 4.0);
        return series;
    }
    
    /**
     * A test for bug report 1170825.
     */
    public void test1170825() {
        TimeSeries s1 = new TimeSeries("Series1");
        TimeSeriesCollection dataset = new TimeSeriesCollection();
        dataset.addSeries(s1);
        try {
            /* TimeSeries s = */ dataset.getSeries(1);
        }
        catch (IllegalArgumentException e) {
            // correct outcome
        }
        catch (IndexOutOfBoundsException e) {
            assertTrue(false);  // wrong outcome
        }        
    }

    /**
     * Some tests for the indexOf() method.
     */
    public void testIndexOf() {
        TimeSeries s1 = new TimeSeries("S1");
        TimeSeries s2 = new TimeSeries("S2");
        TimeSeriesCollection dataset = new TimeSeriesCollection();
        assertEquals(-1, dataset.indexOf(s1));
        assertEquals(-1, dataset.indexOf(s2));
        
        dataset.addSeries(s1);
        assertEquals(0, dataset.indexOf(s1));
        assertEquals(-1, dataset.indexOf(s2));
        
        dataset.addSeries(s2);
        assertEquals(0, dataset.indexOf(s1));
        assertEquals(1, dataset.indexOf(s2));
        
        dataset.removeSeries(s1);
        assertEquals(-1, dataset.indexOf(s1));
        assertEquals(0, dataset.indexOf(s2));
        
        TimeSeries s2b = new TimeSeries("S2");
        assertEquals(0, dataset.indexOf(s2b));
    }
    
}
