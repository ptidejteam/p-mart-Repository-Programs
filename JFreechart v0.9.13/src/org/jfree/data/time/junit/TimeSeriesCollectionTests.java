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
 * ------------------------------
 * TimeSeriesCollectionTests.java
 * ------------------------------
 * (C) Copyright 2003, by Object Refinery Limited.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited);
 * Contributor(s):   -;
 *
 * $Id: TimeSeriesCollectionTests.java,v 1.1 2007/10/10 19:15:26 vauchers Exp $
 *
 * Changes
 * -------
 * 01-May-2003 : Version 1 (DG);
 *
 */

package org.jfree.data.time.junit;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;

/**
 * A collection of test cases for the {@link TimeSeriesCollection} class.
 *
 * @author David Gilbert
 */
public class TimeSeriesCollectionTests extends TestCase {

    /**
     * Returns the tests as a test suite.
     *
     * @return the test suite.
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
     * Some tests for the equals(...) method.
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
}
