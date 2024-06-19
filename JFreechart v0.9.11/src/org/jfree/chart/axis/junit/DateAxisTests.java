/* ======================================
 * JFreeChart : a free Java chart library
 * ======================================
 *
 * Project Info:  http://www.jfree.org/jfreechart/index.html
 * Project Lead:  David Gilbert (david.gilbert@object-refinery.com);
 *
 * (C) Copyright 2000-2003, by Object Refinery Limited and Contributors.
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
 * ------------------
 * DateAxisTests.java
 * ------------------
 * (C) Copyright 2003 by Object Refinery Limited and Contributors.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited);
 * Contributor(s):   -;
 *
 * $Id: DateAxisTests.java,v 1.1 2007/10/10 19:09:23 vauchers Exp $
 *
 * Changes
 * -------
 * 22-Apr-2003 : Version 1 (DG);
 *
 */

package org.jfree.chart.axis.junit;

import java.awt.geom.Rectangle2D;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.util.Calendar;
import java.util.Date;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.jfree.chart.axis.DateAxis;
import org.jfree.data.DateRange;
import org.jfree.ui.RectangleEdge;

/**
 * Tests for the {@link DateAxis} class.
 *
 * @author David Gilbert
 */
public class DateAxisTests extends TestCase {

    /**
     * Returns the tests as a test suite.
     *
     * @return the test suite.
     */
    public static Test suite() {
        return new TestSuite(DateAxisTests.class);
    }

    /**
     * Constructs a new set of tests.
     *
     * @param  name the name of the tests.
     */
    public DateAxisTests(String name) {
        super(name);
    }

    /**
     * Test that the setRange(...) method works.
     */
    public void testSetRange() {

        DateAxis axis = new DateAxis("Test Axis");
        Calendar calendar = Calendar.getInstance();
        calendar.set(1999, Calendar.JANUARY, 3);
        Date d1 = calendar.getTime();
        calendar.set(1999, Calendar.JANUARY, 31);
        Date d2 = calendar.getTime();
        axis.setRange(d1, d2);

        DateRange range = (DateRange) axis.getRange();
        assertEquals(d1, range.getLowerDate());
        assertEquals(d2, range.getUpperDate());

    }

    /**
     * Test that the setMaximumDate(...) method works.
     */
    public void testSetMaximumDate() {

        DateAxis axis = new DateAxis("Test Axis");
        Date date = new Date();
        axis.setMaximumDate(date);
        assertEquals(date, axis.getMaximumDate());

    }

    /**
     * Test that the setMinimumDate(...) method works.
     */
    public void testSetMinimumDate() {

        DateAxis axis = new DateAxis("Test Axis");
        Date d1 = new Date();
        Date d2 = new Date(d1.getTime() + 1);
        axis.setMaximumDate(d2);
        axis.setMinimumDate(d1);
        assertEquals(d1, axis.getMinimumDate());

    }
    
    /**
     * Tests two doubles for 'near enough' equality.
     * 
     * @param d1  number 1.
     * @param d2  number 2.
     * @param tolerance  maximum tolerance.
     * 
     * @return A boolean.
     */
    private boolean same(double d1, double d2, double tolerance) {
        return (Math.abs(d1 - d2) < tolerance);
    }
    
    /**
     * Test the translation of Java2D values to data values.
     */
    public void testTranslateJava2DToValue() {
        DateAxis axis = new DateAxis();
        axis.setRange(50.0, 100.0); 
        Rectangle2D dataArea = new Rectangle2D.Double(10.0, 50.0, 400.0, 300.0);
        double y1 = axis.translateJava2DtoValue(75.0f, dataArea, RectangleEdge.LEFT);  
        assertTrue(same(y1, 95.8333333, 1.0)); 
        double y2 = axis.translateJava2DtoValue(75.0f, dataArea, RectangleEdge.RIGHT);   
        assertTrue(same(y2, 95.8333333, 1.0)); 
        double x1 = axis.translateJava2DtoValue(75.0f, dataArea, RectangleEdge.TOP);   
        assertTrue(same(x1, 58.125, 1.0)); 
        double x2 = axis.translateJava2DtoValue(75.0f, dataArea, RectangleEdge.BOTTOM);   
        assertTrue(same(x2, 58.125, 1.0)); 
        axis.setInverted(true);
        double y3 = axis.translateJava2DtoValue(75.0f, dataArea, RectangleEdge.LEFT);  
        assertTrue(same(y3, 54.1666667, 1.0)); 
        double y4 = axis.translateJava2DtoValue(75.0f, dataArea, RectangleEdge.RIGHT);   
        assertTrue(same(y4, 54.1666667, 1.0)); 
        double x3 = axis.translateJava2DtoValue(75.0f, dataArea, RectangleEdge.TOP);   
        assertTrue(same(x3, 91.875, 1.0)); 
        double x4 = axis.translateJava2DtoValue(75.0f, dataArea, RectangleEdge.BOTTOM);   
        assertTrue(same(x4, 91.875, 1.0)); 
        
    }
    
    /**
     * Serialize an instance, restore it, and check for equality.
     */
    public void testSerialization() {

        DateAxis a1 = new DateAxis("Test Axis");
        DateAxis a2 = null;

        try {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            ObjectOutput out = new ObjectOutputStream(buffer);
            out.writeObject(a1);
            out.close();

            ObjectInput in = new ObjectInputStream(new ByteArrayInputStream(buffer.toByteArray()));
            a2 = (DateAxis) in.readObject();
            in.close();
        }
        catch (Exception e) {
            System.out.println(e.toString());
        }
        assertEquals(a1, a2);

    }

}
