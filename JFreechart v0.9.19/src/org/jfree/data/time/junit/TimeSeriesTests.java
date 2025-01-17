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
 * --------------------
 * TimeSeriesTests.java
 * --------------------
 * (C) Copyright 2001-2003, by Object Refinery Limited.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited);
 * Contributor(s):   -;
 *
 * $Id: TimeSeriesTests.java,v 1.1 2007/10/10 19:34:48 vauchers Exp $
 *
 * Changes
 * -------
 * 16-Nov-2001 : Version 1 (DG);
 * 17-Oct-2002 : Fixed errors reported by Checkstyle (DG);
 * 13-Mar-2003 : Added serialization test (DG);
 * 15-Oct-2003 : Added test for setMaximumItemCount method (DG);
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

import org.jfree.data.SeriesChangeEvent;
import org.jfree.data.SeriesChangeListener;
import org.jfree.data.SeriesException;
import org.jfree.data.time.Day;
import org.jfree.data.time.Month;
import org.jfree.data.time.RegularTimePeriod;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesDataItem;
import org.jfree.data.time.Year;
import org.jfree.date.SerialDate;

/**
 * A collection of test cases for the {@link TimeSeries} class.
 */
public class TimeSeriesTests extends TestCase implements SeriesChangeListener {

    /** A time series. */
    private TimeSeries seriesA;

    /** A time series. */
    private TimeSeries seriesB;

    /** A time series. */
    private TimeSeries seriesC;

    /** A flag that indicates whether or not a change event was fired. */
    private boolean gotSeriesChangeEvent = false;
    
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
    public TimeSeriesTests(final String name) {
        super(name);
    }

    /**
     * Common test setup.
     */
    protected void setUp() {

        this.seriesA = new TimeSeries("Series A", Year.class);
        try {
            this.seriesA.add(new Year(2000), new Integer(102000));
            this.seriesA.add(new Year(2001), new Integer(102001));
            this.seriesA.add(new Year(2002), new Integer(102002));
            this.seriesA.add(new Year(2003), new Integer(102003));
            this.seriesA.add(new Year(2004), new Integer(102004));
            this.seriesA.add(new Year(2005), new Integer(102005));
        }
        catch (SeriesException e) {
            System.err.println("TimeSeriesTests.setUp(): problem creating series.");
        }

        this.seriesB = new TimeSeries("Series B", Year.class);
        try {
            this.seriesB.add(new Year(2006), new Integer(202006));
            this.seriesB.add(new Year(2007), new Integer(202007));
            this.seriesB.add(new Year(2008), new Integer(202008));
        }
        catch (SeriesException e) {
            System.err.println("TimeSeriesTests.setUp(): problem creating series.");
        }

        this.seriesC = new TimeSeries("Series C", Year.class);
        try {
            this.seriesC.add(new Year(1999), new Integer(301999));
            this.seriesC.add(new Year(2000), new Integer(302000));
            this.seriesC.add(new Year(2002), new Integer(302002));
        }
        catch (SeriesException e) {
            System.err.println("TimeSeriesTests.setUp(): problem creating series.");
        }

    }
    
    /**
     * Sets the flag to indicate that a {@link SeriesChangeEvent} has been received.
     * 
     * @param event  the event.
     */
    public void seriesChanged(SeriesChangeEvent event) {
        this.gotSeriesChangeEvent = true;   
    }

    /**
     * Problem that cloning works.
     */
    public void testClone() {

        final TimeSeries series = new TimeSeries("Test Series");

        final RegularTimePeriod jan1st2002 = new Day(1, SerialDate.JANUARY, 2002);
        try {
            series.add(jan1st2002, new Integer(42));
        }
        catch (SeriesException e) {
            System.err.println("TimeSeriesTests.testClone: problem adding to series.");
        }

        TimeSeries clone = null;
        try {
            clone = (TimeSeries) series.clone();
            clone.setName("Clone Series");
            try {
                clone.update(jan1st2002, new Integer(10));
            }
            catch (SeriesException e) {
                System.err.println("TimeSeriesTests.testClone: problem updating series.");
            }
        }
        catch (CloneNotSupportedException e) {
            assertTrue(false);  
        }

        final int seriesValue = series.getValue(jan1st2002).intValue();
        final int cloneValue = clone.getValue(jan1st2002).intValue();

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
            this.seriesA.add(new Year(1999), new Integer(1));
        }
        catch (SeriesException e) {
            System.err.println("TimeSeriesTests.testAddValue: problem adding to series.");
        }

        final int value = this.seriesA.getValue(0).intValue();
        assertEquals(1, value);

    }

    /**
     * Tests the retrieval of values.
     */
    public void testGetValue() {

        final Number value1 = this.seriesA.getValue(new Year(1999));
        assertNull(value1);
        final int value2 = this.seriesA.getValue(new Year(2000)).intValue();
        assertEquals(102000, value2);

    }

    /**
     * Tests the deletion of values.
     */
    public void testDelete() {
        this.seriesA.delete(0, 0);
        assertEquals(5, this.seriesA.getItemCount());
        final Number value = this.seriesA.getValue(new Year(2000));
        assertNull(value);
    }

    /**
     * Basic tests for the delete() method.
     */
    public void testDelete2() {
        TimeSeries s1 = new TimeSeries("Series", Year.class);    
        s1.add(new Year(2000), 13.75);
        s1.add(new Year(2001), 11.90);
        s1.add(new Year(2002), null);
        s1.addChangeListener(this);
        this.gotSeriesChangeEvent = false;
        s1.delete(new Year(2001));
        assertTrue(this.gotSeriesChangeEvent);
        assertEquals(2, s1.getItemCount());
        assertEquals(null, s1.getValue(new Year(2001)));
    }
    
    /**
     * Serialize an instance, restore it, and check for equality.
     */
    public void testSerialization() {

        final TimeSeries s1 = new TimeSeries("A test", Year.class);
        s1.add(new Year(2000), 13.75);
        s1.add(new Year(2001), 11.90);
        s1.add(new Year(2002), null);
        s1.add(new Year(2005), 19.32);
        s1.add(new Year(2007), 16.89);
        TimeSeries s2 = null;

        try {
            final ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            final ObjectOutput out = new ObjectOutputStream(buffer);
            out.writeObject(s1);
            out.close();

            final ObjectInput in = new ObjectInputStream(
                new ByteArrayInputStream(buffer.toByteArray())
            );
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
        final TimeSeries s1 = new TimeSeries("Time Series 1");
        final TimeSeries s2 = new TimeSeries("Time Series 2");
        final boolean b1 = s1.equals(s2);
        assertFalse("b1", b1);

        s2.setName("Time Series 1");
        final boolean b2 = s1.equals(s2);
        assertTrue("b2", b2);

        final RegularTimePeriod p1 = new Day();
        final RegularTimePeriod p2 = p1.next();
        s1.add(p1, 100.0);
        s1.add(p2, 200.0);
        final boolean b3 = s1.equals(s2);
        assertFalse("b3", b3);

        s2.add(p1, 100.0);
        s2.add(p2, 200.0);
        final boolean b4 = s1.equals(s2);
        assertTrue("b4", b4);

        s1.setMaximumItemCount(100);
        final boolean b5 = s1.equals(s2);
        assertFalse("b5", b5);

        s2.setMaximumItemCount(100);
        final boolean b6 = s1.equals(s2);
        assertTrue("b6", b6);

        s1.setHistoryCount(100);
        final boolean b7 = s1.equals(s2);
        assertFalse("b7", b7);

        s2.setHistoryCount(100);
        final boolean b8 = s1.equals(s2);
        assertTrue("b8", b8);

    }
    
    /**
     * Some tests to ensure that the createCopy(RegularTimePeriod, RegularTimePeriod) method
     * is functioning correctly.
     */
    public void testCreateCopy1() {
        
        final TimeSeries series = new TimeSeries("Series", Month.class);
        series.add(new Month(SerialDate.JANUARY, 2003), 45.0);
        series.add(new Month(SerialDate.FEBRUARY, 2003), 55.0);
        series.add(new Month(SerialDate.JUNE, 2003), 35.0);
        series.add(new Month(SerialDate.NOVEMBER, 2003), 85.0);
        series.add(new Month(SerialDate.DECEMBER, 2003), 75.0);
        
        try {
            // copy a range before the start of the series data...
            final TimeSeries result1 = series.createCopy(new Month(SerialDate.NOVEMBER, 2002),
                                                   new Month(SerialDate.DECEMBER, 2002));
            assertEquals(0, result1.getItemCount());
        
            // copy a range that includes only the first item in the series...
            final TimeSeries result2 = series.createCopy(new Month(SerialDate.NOVEMBER, 2002),
                                                   new Month(SerialDate.JANUARY, 2003));
            assertEquals(1, result2.getItemCount());
        
            // copy a range that begins before and ends in the middle of the series...
            final TimeSeries result3 = series.createCopy(new Month(SerialDate.NOVEMBER, 2002),
                                                   new Month(SerialDate.APRIL, 2003));
            assertEquals(2, result3.getItemCount());
        
            final TimeSeries result4 = series.createCopy(new Month(SerialDate.NOVEMBER, 2002),
                                                   new Month(SerialDate.DECEMBER, 2003));
            assertEquals(5, result4.getItemCount());
                                                                                        
            final TimeSeries result5 = series.createCopy(new Month(SerialDate.NOVEMBER, 2002),
                                                   new Month(SerialDate.MARCH, 2004));
            assertEquals(5, result5.getItemCount());
        
            final TimeSeries result6 = series.createCopy(new Month(SerialDate.JANUARY, 2003),
                                                   new Month(SerialDate.JANUARY, 2003));
            assertEquals(1, result6.getItemCount());

            final TimeSeries result7 = series.createCopy(new Month(SerialDate.JANUARY, 2003),
                                                   new Month(SerialDate.APRIL, 2003));
            assertEquals(2, result7.getItemCount());

            final TimeSeries result8 = series.createCopy(new Month(SerialDate.JANUARY, 2003),
                                                   new Month(SerialDate.DECEMBER, 2003));
            assertEquals(5, result8.getItemCount());

            final TimeSeries result9 = series.createCopy(new Month(SerialDate.JANUARY, 2003),
                                                   new Month(SerialDate.MARCH, 2004));
            assertEquals(5, result9.getItemCount());
        
            final TimeSeries result10 = series.createCopy(new Month(SerialDate.MAY, 2003),
                                                    new Month(SerialDate.DECEMBER, 2003));
            assertEquals(3, result10.getItemCount());

            final TimeSeries result11 = series.createCopy(new Month(SerialDate.MAY, 2003),
                                                    new Month(SerialDate.MARCH, 2004));
            assertEquals(3, result11.getItemCount());

            final TimeSeries result12 = series.createCopy(new Month(SerialDate.DECEMBER, 2003),
                                                    new Month(SerialDate.DECEMBER, 2003));
            assertEquals(1, result12.getItemCount());
    
            final TimeSeries result13 = series.createCopy(new Month(SerialDate.DECEMBER, 2003),
                                                    new Month(SerialDate.MARCH, 2004));
            assertEquals(1, result13.getItemCount());

            final TimeSeries result14 = series.createCopy(new Month(SerialDate.JANUARY, 2004),
                                                    new Month(SerialDate.MARCH, 2004));
            assertEquals(0, result14.getItemCount());
        }
        catch (CloneNotSupportedException e) {
            assertTrue(false);
        }

    }
    
    /**
     * Problem the setMaximumItemCount method to ensure that it removes items from the series
     * if necessary.
     */
    public void testSetMaximumItemCount() {

        final TimeSeries s1 = new TimeSeries("S1", Year.class);
        s1.add(new Year(2000), 13.75);
        s1.add(new Year(2001), 11.90);
        s1.add(new Year(2002), null);
        s1.add(new Year(2005), 19.32);
        s1.add(new Year(2007), 16.89);

        assertTrue(s1.getItemCount() == 5);
        s1.setMaximumItemCount(3);
        assertTrue(s1.getItemCount() == 3);
        final TimeSeriesDataItem item = s1.getDataItem(0);
        assertTrue(item.getPeriod().equals(new Year(2002)));

    }



}
