/* ===========================================================
 * JFreeChart : a free chart library for the Java(tm) platform
 * ===========================================================
 *
 * (C) Copyright 2000-2006, by Object Refinery Limited and Contributors.
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
 * --------------------------
 * XYIntervalSeriesTests.java
 * --------------------------
 * (C) Copyright 2006, by Object Refinery Limited and Contributors.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited);
 * Contributor(s):   -;
 *
 * $Id: XYIntervalSeriesTests.java,v 1.1 2007/10/10 20:29:55 vauchers Exp $
 *
 * Changes
 * -------
 * 20-Oct-2006 : Version 1, based on XYSeriesTests (DG);
 *
 */

package org.jfree.data.xy.junit;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.jfree.data.xy.XYIntervalSeries;

/**
 * Tests for the {@link XYIntervalSeries} class.
 */
public class XYIntervalSeriesTests extends TestCase {

    /**
     * Returns the tests as a test suite.
     *
     * @return The test suite.
     */
    public static Test suite() {
        return new TestSuite(XYIntervalSeriesTests.class);
    }

    /**
     * Constructs a new set of tests.
     *
     * @param name  the name of the tests.
     */
    public XYIntervalSeriesTests(String name) {
        super(name);
    }

    /**
     * Confirm that the equals method can distinguish all the required fields.
     */
    public void testEquals() {
        
        XYIntervalSeries s1 = new XYIntervalSeries("s1");
        XYIntervalSeries s2 = new XYIntervalSeries("s1");
        assertTrue(s1.equals(s2));
        
        // seriesKey
        s1 = new XYIntervalSeries("s2");
        assertFalse(s1.equals(s2));
        s2 = new XYIntervalSeries("s2");
        assertTrue(s1.equals(s2));
        
        // autoSort
        s1 = new XYIntervalSeries("s2", false, true);
        assertFalse(s1.equals(s2));
        s2 = new XYIntervalSeries("s2", false, true);
        assertTrue(s1.equals(s2));
        
        // allowDuplicateValues
        s1 = new XYIntervalSeries("s2", false, false);
        assertFalse(s1.equals(s2));
        s2 = new XYIntervalSeries("s2", false, false);
        assertTrue(s1.equals(s2));
        
        // add a value
        s1.add(1.0, 0.5, 1.5, 2.0, 1.9, 2.1);
        assertFalse(s1.equals(s2));
        s2.add(1.0, 0.5, 1.5, 2.0, 1.9, 2.1);
        assertTrue(s2.equals(s1));

        // add another value
        s1.add(2.0, 0.5, 1.5, 2.0, 1.9, 2.1);
        assertFalse(s1.equals(s2));
        s2.add(2.0, 0.5, 1.5, 2.0, 1.9, 2.1);
        assertTrue(s2.equals(s1));

        // remove a value
        s1.remove(new Double(1.0));
        assertFalse(s1.equals(s2));
        s2.remove(new Double(1.0));
        assertTrue(s2.equals(s1));
        
    }

    /**
     * Confirm that cloning works.
     */
    public void testCloning() {
        XYIntervalSeries s1 = new XYIntervalSeries("s1");
        s1.add(1.0, 0.5, 1.5, 2.0, 1.9, 2.01);
        XYIntervalSeries s2 = null;
        try {
            s2 = (XYIntervalSeries) s1.clone();
        }
        catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        assertTrue(s1 != s2);
        assertTrue(s1.getClass() == s2.getClass());
        assertTrue(s1.equals(s2));
    }

    /**
     * Serialize an instance, restore it, and check for equality.
     */
    public void testSerialization() {

        XYIntervalSeries s1 = new XYIntervalSeries("s1");
        s1.add(1.0, 0.5, 1.5, 2.0, 1.9, 2.1);
        XYIntervalSeries s2 = null;
        
        try {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            ObjectOutput out = new ObjectOutputStream(buffer);
            out.writeObject(s1);
            out.close();

            ObjectInput in = new ObjectInputStream(
                    new ByteArrayInputStream(buffer.toByteArray()));
            s2 = (XYIntervalSeries) in.readObject();
            in.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        assertEquals(s1, s2);

    }
    
    /**
     * Simple test for the indexOf() method.
     */
    public void testIndexOf() {
        XYIntervalSeries s1 = new XYIntervalSeries("Series 1");
        s1.add(1.0, 1.0, 1.0, 2.0, 1.9, 2.1);
        s1.add(2.0, 2.0, 2.0, 3.0, 2.9, 3.1);
        s1.add(3.0, 3.0, 3.0, 4.0, 3.9, 4.1);
        assertEquals(0, s1.indexOf(new Double(1.0)));
    }
    
    /**
     * A check for the indexOf() method for an unsorted series.
     */
    public void testIndexOf2() {
        XYIntervalSeries s1 = new XYIntervalSeries("Series 1", false, true);
        s1.add(1.0, 1.0, 1.0, 2.0, 1.9, 2.1);
        s1.add(3.0, 3.0, 3.0, 3.0, 2.9, 3.1);
        s1.add(2.0, 2.0, 2.0, 2.0, 1.9, 2.1);
        assertEquals(0, s1.indexOf(new Double(1.0)));        
        assertEquals(1, s1.indexOf(new Double(3.0)));        
        assertEquals(2, s1.indexOf(new Double(2.0)));        
    }

    /**
     * Simple test for the remove() method.
     */
    public void testRemove() {
        XYIntervalSeries s1 = new XYIntervalSeries("Series 1");
        s1.add(1.0, 1.0, 1.0, 2.0, 1.9, 2.1);
        s1.add(2.0, 2.0, 2.0, 2.0, 1.9, 2.1);
        s1.add(3.0, 3.0, 3.0, 3.0, 2.9, 3.1);
        assertEquals(3, s1.getItemCount());
        
        s1.remove(new Double(2.0));
        assertEquals(new Double(3.0), s1.getX(1));
        
        s1.remove(new Double(1.0));
        assertEquals(new Double(3.0), s1.getX(0));    
    }

    private static final double EPSILON = 0.0000000001;
    
    /**
     * When items are added with duplicate x-values, we expect them to remain 
     * in the order they were added.
     */
    public void testAdditionOfDuplicateXValues() {
        XYIntervalSeries s1 = new XYIntervalSeries("Series 1");
        s1.add(1.0, 1.0, 1.0, 1.0, 1.0, 1.0);
        s1.add(2.0, 2.0, 2.0, 2.0, 2.0, 2.0);
        s1.add(2.0, 3.0, 3.0, 3.0, 3.0, 3.0);
        s1.add(2.0, 4.0, 4.0, 4.0, 4.0, 4.0);
        s1.add(3.0, 5.0, 5.0, 5.0, 5.0, 5.0);
        assertEquals(1.0, s1.getYValue(0), EPSILON);
        assertEquals(2.0, s1.getYValue(1), EPSILON);
        assertEquals(3.0, s1.getYValue(2), EPSILON);
        assertEquals(4.0, s1.getYValue(3), EPSILON);
        assertEquals(5.0, s1.getYValue(4), EPSILON);
    }
    
    /**
     * Some checks for the add() method for an UNSORTED series.
     */
    public void testAdd() {
        XYIntervalSeries series = new XYIntervalSeries("Series", false, true);
        series.add(5.0, 5.50, 5.50, 5.50, 5.50, 5.50);
        series.add(5.1, 5.51, 5.51, 5.51, 5.51, 5.51);
        series.add(6.0, 6.6, 6.6, 6.6, 6.6, 6.6);
        series.add(3.0, 3.3, 3.3, 3.3, 3.3, 3.3);
        series.add(4.0, 4.4, 4.4, 4.4, 4.4, 4.4);
        series.add(2.0, 2.2, 2.2, 2.2, 2.2, 2.2);
        series.add(1.0, 1.1, 1.1, 1.1, 1.1, 1.1);
        assertEquals(5.5, series.getYValue(0), EPSILON);
        assertEquals(5.51, series.getYValue(1), EPSILON);
        assertEquals(6.6, series.getYValue(2), EPSILON);
        assertEquals(3.3, series.getYValue(3), EPSILON);
        assertEquals(4.4, series.getYValue(4), EPSILON);
        assertEquals(2.2, series.getYValue(5), EPSILON);
        assertEquals(1.1, series.getYValue(6), EPSILON);
    }
    
    /**
     * A simple check that the maximumItemCount attribute is working.
     */
    public void testSetMaximumItemCount() {
        XYIntervalSeries s1 = new XYIntervalSeries("S1");
        assertEquals(Integer.MAX_VALUE, s1.getMaximumItemCount());
        s1.setMaximumItemCount(2);
        assertEquals(2, s1.getMaximumItemCount());
        s1.add(1.0, 1.1, 1.1, 1.1, 1.1, 1.1);
        s1.add(2.0, 2.2, 2.2, 2.2, 2.2, 2.2);
        s1.add(3.0, 3.3, 3.3, 3.3, 3.3, 3.3);
        assertEquals(2.0, s1.getX(0).doubleValue(), EPSILON);
        assertEquals(3.0, s1.getX(1).doubleValue(), EPSILON);
    }
    
    /**
     * Check that the maximum item count can be applied retrospectively.
     */
    public void testSetMaximumItemCount2() {
        XYIntervalSeries s1 = new XYIntervalSeries("S1");
        s1.add(1.0, 1.1, 1.1, 1.1, 1.1, 1.1);
        s1.add(2.0, 2.2, 2.2, 2.2, 2.2, 2.2);
        s1.add(3.0, 3.3, 3.3, 3.3, 2.2, 2.2);
        s1.setMaximumItemCount(2);
        assertEquals(2.0, s1.getX(0).doubleValue(), EPSILON);
        assertEquals(3.0, s1.getX(1).doubleValue(), EPSILON);
    }
    
}
