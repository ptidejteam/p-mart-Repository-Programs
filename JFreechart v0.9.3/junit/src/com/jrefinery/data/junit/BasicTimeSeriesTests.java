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
 * -------------------------
 * BasicTimeSeriesTests.java
 * -------------------------
 * (C) Copyright 2001, 2002, by Simba Management Limited.
 *
 * Original Author:  David Gilbert (for Simba Management Limited);
 * Contributor(s):   -;
 *
 * $Id: BasicTimeSeriesTests.java,v 1.1 2007/10/10 19:52:22 vauchers Exp $
 *
 * Changes
 * -------
 * 16-Nov-2001 : Version 1 (DG);
 *
 */

package com.jrefinery.data.junit;

import junit.framework.*;
import com.jrefinery.data.*;
import com.jrefinery.date.*;

/**
 * An incomplete collection of test cases for the BasicTimeSeries class.
 */
public class BasicTimeSeriesTests extends TestCase {

    protected BasicTimeSeries seriesA;
    protected BasicTimeSeries seriesB;
    protected BasicTimeSeries seriesC;

    /**
     * Returns the tests as a test suite.
     */
    public static Test suite() {
        return new TestSuite(BasicTimeSeriesTests.class);
    }

    /**
     * Constructs a new set of tests.
     * @param The name of the tests.
     */
    public BasicTimeSeriesTests(String name) {
        super(name);
    }

    /**
     * Common test setup.
     */
    protected void setUp() {

        seriesA = new BasicTimeSeries("Series A", Year.class);
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

        seriesB = new BasicTimeSeries("Series B", Year.class);
        try {
            seriesB.add(new Year(2006), new Integer(202006));
            seriesB.add(new Year(2007), new Integer(202007));
            seriesB.add(new Year(2008), new Integer(202008));
        }
        catch (SeriesException e) {
            System.err.println("TimeSeriesTests.setUp(): problem creating series.");
        }

        seriesC = new BasicTimeSeries("Series C", Year.class);
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

        BasicTimeSeries series = new BasicTimeSeries("Test Series");

        TimePeriod Jan1_2002 = new Day(1, SerialDate.JANUARY, 2002);
        try {
            series.add(Jan1_2002, new Integer(42));
        }
        catch (SeriesException e) {
            System.err.println("TimeSeriesTests.testClone: problem adding to series.");
        }

        BasicTimeSeries clone = (BasicTimeSeries)series.clone();
        clone.setName("Clone Series");
        try {
            clone.update(Jan1_2002, new Integer(10));
        }
        catch (SeriesException e) {
            System.err.println("TimeSeriesTests.testClone: problem updating series.");
        }

        int seriesValue = series.getValue(Jan1_2002).intValue();
        int cloneValue = clone.getValue(Jan1_2002).intValue();

        this.assertEquals(42, seriesValue);
        this.assertEquals(10, cloneValue);
        this.assertEquals("Test Series", series.getName());
        this.assertEquals("Clone Series", clone.getName());

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
        this.assertEquals(1, value);

    }

    /**
     * Tests the retrieval of values.
     */
    public void testGetValue() {

        Number value1 = seriesA.getValue(new Year(1999));
        this.assertNull(value1);
        int value2 = seriesA.getValue(new Year(2000)).intValue();
        this.assertEquals(102000, value2);

    }

    /**
     * Tests the deletion of values.
     */
    public void testDelete() {

        seriesA.delete(0, 0);
        this.assertEquals(5, seriesA.getItemCount());
        Number value = seriesA.getValue(new Year(2000));
        this.assertNull(value);

    }

}
