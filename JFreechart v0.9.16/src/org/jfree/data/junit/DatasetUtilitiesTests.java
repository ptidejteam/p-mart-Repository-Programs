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
 * --------------------------
 * DatasetUtilitiesTests.java
 * --------------------------
 * (C) Copyright 2003 by Object Refinery Limited and Contributors.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited);
 * Contributor(s):   -;
 *
 * $Id: DatasetUtilitiesTests.java,v 1.1 2007/10/10 19:25:32 vauchers Exp $
 *
 * Changes
 * -------
 * 18-Sep-2003 : Version 1 (DG);
 *
 */

package org.jfree.data.junit;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.jfree.data.CategoryDataset;
import org.jfree.data.DatasetUtilities;
import org.jfree.data.DefaultCategoryDataset;
import org.jfree.data.Range;
import org.jfree.util.NumberUtils;

/**
 * Tests for the {@link DatasetUtilities} class.
 *
 * @author David Gilbert
 */
public class DatasetUtilitiesTests extends TestCase {

    /**
     * Returns the tests as a test suite.
     *
     * @return the test suite.
     */
    public static Test suite() {
        return new TestSuite(DatasetUtilitiesTests.class);
    }

    /**
     * Constructs a new set of tests.
     *
     * @param  name the name of the tests.
     */
    public DatasetUtilitiesTests(String name) {
        super(name);
    }

    /**
     * A quick test of the min and max range value methods.
     */
    public void testMinMaxRange() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        dataset.addValue(100.0, "Series 1", "Type 1");
        dataset.addValue(101.1, "Series 1", "Type 2");
        Number min = DatasetUtilities.getMinimumRangeValue(dataset);
        assertTrue(min.doubleValue() < 100.1);
        Number max = DatasetUtilities.getMaximumRangeValue(dataset);
        assertTrue(max.doubleValue() > 101.0);
    }

    /**
     * A test to reproduce bug report 803660.
     */
    public void test803660() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        dataset.addValue(100.0, "Series 1", "Type 1");
        dataset.addValue(101.1, "Series 1", "Type 2");
        Number n = DatasetUtilities.getMaximumRangeValue(dataset);
        assertTrue(n.doubleValue() > 101.0);
        
    }
    
    /**
     * A simple test for the cumulative range calculation.  The sequence of "cumulative" values
     * are considered to be { 0.0, 10.0, 25.0, 18.0 } so the range should be 0.0 -> 25.0.
     */
    public void testCumulativeRange1() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        dataset.addValue(10.0, "Series 1", "Start");
        dataset.addValue(15.0, "Series 1", "Delta 1");
        dataset.addValue(-7.0, "Series 1", "Delta 2");
        Range range = DatasetUtilities.getCumulativeRangeExtent(dataset);
        assertTrue(NumberUtils.equal(range.getLowerBound(), 0.0));
        assertTrue(NumberUtils.equal(range.getUpperBound(), 25.0));
    }
    
    /**
     * A further test for the cumulative range calculation.
     */
    public void testCumulativeRange2() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        dataset.addValue(-21.4, "Series 1", "Start Value");
        dataset.addValue(11.57, "Series 1", "Delta 1");
        dataset.addValue(3.51, "Series 1", "Delta 2");
        dataset.addValue(-12.36, "Series 1", "Delta 3");
        dataset.addValue(3.39, "Series 1", "Delta 4");
        dataset.addValue(38.68, "Series 1", "Delta 5");
        dataset.addValue(-43.31, "Series 1", "Delta 6");
        dataset.addValue(-29.59, "Series 1", "Delta 7");
        dataset.addValue(35.30, "Series 1", "Delta 8");
        dataset.addValue(5.0, "Series 1", "Delta 9");
        Range range = DatasetUtilities.getCumulativeRangeExtent(dataset);
        assertTrue(NumberUtils.equal(range.getLowerBound(), -49.51));
        assertTrue(NumberUtils.equal(range.getUpperBound(), 23.39));
    }
    
    /**
     * Test the creation of a dataset from an array.
     */
    public void testCreateCategoryDataset1() {
        String[] rowKeys = { "R1", "R2", "R3" };
        String[] columnKeys = { "C1", "C2" };
        double[][] data = new double[3][];
        data[0] = new double[] { 1.1, 1.2 };
        data[1] = new double[] { 2.1, 2.2 };
        data[2] = new double[] { 3.1, 3.2 };
        CategoryDataset dataset = DatasetUtilities.createCategoryDataset(
                rowKeys, columnKeys, data
        );
        assertTrue(dataset.getRowCount() == 3);
        assertTrue(dataset.getColumnCount() == 2);
    }

    /**
     * Test the creation of a dataset from an array.  This time is should fail because
     * the array dimensions are around the wrong way.
     */
    public void testCreateCategoryDataset2() {
        boolean pass = false;
        String[] rowKeys = { "R1", "R2", "R3" };
        String[] columnKeys = { "C1", "C2" };
        double[][] data = new double[2][];
        data[0] = new double[] { 1.1, 1.2, 1.3 };
        data[1] = new double[] { 2.1, 2.2, 2.3 };
        CategoryDataset dataset = null;
        try {
            dataset = DatasetUtilities.createCategoryDataset(
               rowKeys, columnKeys, data
            );
        }
        catch (IllegalArgumentException e) {
            pass = true;  // got it!
        }
        assertTrue(pass);
        assertTrue(dataset == null);
    }

}
