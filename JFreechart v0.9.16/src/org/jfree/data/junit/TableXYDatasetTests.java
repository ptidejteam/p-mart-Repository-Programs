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
 * ------------------------
 * TableXYDatasetTests.java
 * ------------------------
 * (C) Copyright 2003 by Richard Atkinson and Contributors.
 *
 * Original Author:  Richard Atkinson;
 * Contributor(s):   -;
 *
 * $Id: TableXYDatasetTests.java,v 1.1 2007/10/10 19:25:32 vauchers Exp $
 *
 * Changes
 * -------
 * 11-Aug-2003 : Version 1 (RA);
 * 18-Aug-2003 : Added tests for event notification when removing and updating series (RA);
 * 22-Sep-2003 : Changed to recognise that empty values are now null rather than zero (RA);
 *
 */
package org.jfree.data.junit;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.jfree.data.DefaultTableXYDataset;
import org.jfree.data.XYSeries;

/**
 * Tests for {@link DefaultTableXYDataset}.
 *
 * @author Richard Atkinson
 */
public class TableXYDatasetTests extends TestCase {

    /**
     * Returns the tests as a test suite.
     *
     * @return the test suite.
     */
    public static Test suite() {
        return new TestSuite(TableXYDatasetTests.class);
    }

    /**
     * Constructs a new set of tests.
     *
     * @param  name the name of the tests.
     */
    public TableXYDatasetTests(String name) {
        super(name);
    }

    /**
     * Assorted tests.
     */
    public void testTableXYDataset() {
        
        DefaultTableXYDataset tableXYDataSet = new DefaultTableXYDataset();
        XYSeries series1 = new XYSeries("Series 1", false);
        series1.add(1, 1);
        series1.add(2, 1);
        series1.add(4, 1);
        series1.add(5, 1);
        
        XYSeries series2 = new XYSeries("Series 2", false);
        series2.add(2, 2);
        series2.add(3, 2);
        series2.add(4, 2);
        series2.add(5, 2);
        series2.add(6, 2);

        tableXYDataSet.addSeries(series1);
        tableXYDataSet.addSeries(series2);

        //  Test that there are 6 X points and some specific values
        assertEquals(6, tableXYDataSet.getItemCount());
        assertEquals(6, tableXYDataSet.getXValue(0, 5).intValue());
        assertEquals(null, tableXYDataSet.getYValue(0, 5));
        assertEquals(6, tableXYDataSet.getXValue(1, 5).intValue());
        assertEquals(2, tableXYDataSet.getYValue(1, 5).intValue());

        series2.add(7, 2);
        //  Test that there are now 7 X points in both series
        assertEquals(7, tableXYDataSet.getItemCount());
        assertEquals(null, tableXYDataSet.getYValue(0, 6));
        assertEquals(2, tableXYDataSet.getYValue(1, 6).intValue());

        //  Remove series 2
        tableXYDataSet.removeSeries(series1);
        //  Test that there are still 7 X points
        assertEquals(7, tableXYDataSet.getItemCount());

        //  Remove series 1 and add new series
        tableXYDataSet.removeSeries(series2);
        series1 = new XYSeries("Series 1", false);
        series1.add(1, 1);
        series1.add(2, 1);
        series1.add(4, 1);
        series1.add(5, 1);
        tableXYDataSet.addSeries(series1);

        //  Test that there are now 4 X points
        assertEquals(4, tableXYDataSet.getItemCount());

    }
}
