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
 * DefaultKeyedValues2DTests.java
 * ------------------------------
 * (C) Copyright 2003-2007 by Object Refinery Limited and Contributors.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited);
 * Contributor(s):   -;
 *
 * $Id: DefaultKeyedValues2DTests.java,v 1.1 2007/10/10 20:53:53 vauchers Exp $
 *
 * Changes
 * -------
 * 13-Mar-2003 : Version 1 (DG);
 * 15-Sep-2004 : Updated cloning test (DG);
 * 06-Oct-2005 : Added testEquals() (DG);
 * 18-Jan-2007 : Added testSparsePopulation() (DG);
 * 26-Feb-2007 : Added some basic tests (DG);
 * 30-Mar-2007 : Added a test for bug 1690654 (DG);
 *
 */

package org.jfree.data.junit;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.jfree.data.DefaultKeyedValues2D;
import org.jfree.data.UnknownKeyException;

/**
 * Tests for the {@link DefaultKeyedValues2D} class.
 */
public class DefaultKeyedValues2DTests extends TestCase {

    /**
     * Returns the tests as a test suite.
     *
     * @return The test suite.
     */
    public static Test suite() {
        return new TestSuite(DefaultKeyedValues2DTests.class);
    }
    
    /**
     * Constructs a new set of tests.
     *
     * @param name  the name of the tests.
     */
    public DefaultKeyedValues2DTests(String name) {
        super(name);
    }

    /**
     * Some checks for the getValue() method.
     */
    public void testGetValue() {
        DefaultKeyedValues2D d = new DefaultKeyedValues2D();
        d.addValue(new Double(1.0), "R1", "C1");
        assertEquals(new Double(1.0), d.getValue("R1", "C1"));
        boolean pass = false;
        try {
            d.getValue("XX", "C1");
        }
        catch (UnknownKeyException e) {
            pass = true;   
        }
        assertTrue(pass);
        
        pass = false;
        try {
            d.getValue("R1", "XX");
        }
        catch (UnknownKeyException e) {
            pass = true;   
        }
        assertTrue(pass);
    }
    
    /**
     * Some checks for the clone() method.
     */
    public void testCloning() {
        DefaultKeyedValues2D v1 = new DefaultKeyedValues2D();
        v1.setValue(new Integer(1), "V1", "C1");
        v1.setValue(null, "V2", "C1");
        v1.setValue(new Integer(3), "V3", "C2");
        DefaultKeyedValues2D v2 = null;
        try {
            v2 = (DefaultKeyedValues2D) v1.clone();
        }
        catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        assertTrue(v1 != v2);
        assertTrue(v1.getClass() == v2.getClass());
        assertTrue(v1.equals(v2));
        
        // check that clone is independent of the original
        v2.setValue(new Integer(2), "V2", "C1");
        assertFalse(v1.equals(v2));
    }
    
    /**
     * Serialize an instance, restore it, and check for equality.
     */
    public void testSerialization() {

        DefaultKeyedValues2D kv2D1 = new DefaultKeyedValues2D();
        kv2D1.addValue(new Double(234.2), "Row1", "Col1");
        kv2D1.addValue(null, "Row1", "Col2");
        kv2D1.addValue(new Double(345.9), "Row2", "Col1");
        kv2D1.addValue(new Double(452.7), "Row2", "Col2");

        DefaultKeyedValues2D kv2D2 = null;

        try {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            ObjectOutput out = new ObjectOutputStream(buffer);
            out.writeObject(kv2D1);
            out.close();

            ObjectInput in = new ObjectInputStream(
                    new ByteArrayInputStream(buffer.toByteArray()));
            kv2D2 = (DefaultKeyedValues2D) in.readObject();
            in.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        assertEquals(kv2D1, kv2D2);

    }
    
    /**
     * Some checks for the equals() method.
     */
    public void testEquals() {
        DefaultKeyedValues2D d1 = new DefaultKeyedValues2D();
        DefaultKeyedValues2D d2 = new DefaultKeyedValues2D();
        assertTrue(d1.equals(d2));
        assertTrue(d2.equals(d1));
        
        d1.addValue(new Double(1.0), new Double(2.0), "S1");
        assertFalse(d1.equals(d2));
        d2.addValue(new Double(1.0), new Double(2.0), "S1");
        assertTrue(d1.equals(d2));
    }
    
    /**
     * Populates a data structure with sparse entries, then checks that
     * the unspecified entries return null.
     */
    public void testSparsePopulation() {
        DefaultKeyedValues2D d = new DefaultKeyedValues2D();
        d.addValue(new Integer(11), "R1", "C1");
        d.addValue(new Integer(22), "R2", "C2");
        
        assertEquals(new Integer(11), d.getValue("R1", "C1"));
        assertNull(d.getValue("R1", "C2"));
        assertEquals(new Integer(22), d.getValue("R2", "C2"));
        assertNull(d.getValue("R2", "C1"));
    }
    
    /**
     * Some basic checks for the getRowCount() method.
     */
    public void testRowCount() {
        DefaultKeyedValues2D d = new DefaultKeyedValues2D();
        assertEquals(0, d.getRowCount());
        d.addValue(new Double(1.0), "R1", "C1");
        assertEquals(1, d.getRowCount());
        d.addValue(new Double(2.0), "R2", "C1");
        assertEquals(2, d.getRowCount());
    }

    /**
     * Some basic checks for the getColumnCount() method.
     */
    public void testColumnCount() {
        DefaultKeyedValues2D d = new DefaultKeyedValues2D();
        assertEquals(0, d.getColumnCount());
        d.addValue(new Double(1.0), "R1", "C1");
        assertEquals(1, d.getColumnCount());
        d.addValue(new Double(2.0), "R1", "C2");
        assertEquals(2, d.getColumnCount());
    }
    
    private static final double EPSILON = 0.0000000001;
    
    /**
     * Some basic checks for the getValue(int, int) method.
     */
    public void testGetValue2() {
        DefaultKeyedValues2D d = new DefaultKeyedValues2D();
        boolean pass = false;
        try {
            d.getValue(0, 0);
        }
        catch (IndexOutOfBoundsException e) {
            pass = true;
        }
        assertTrue(pass);
        d.addValue(new Double(1.0), "R1", "C1");
        assertEquals(1.0, d.getValue(0, 0).doubleValue(), EPSILON);
        d.addValue(new Double(2.0), "R2", "C2");
        assertEquals(2.0, d.getValue(1, 1).doubleValue(), EPSILON);
        assertNull(d.getValue(1, 0));
        assertNull(d.getValue(0, 1));
        
        pass = false;
        try {
            d.getValue(2, 0);
        }
        catch (IndexOutOfBoundsException e) {
            pass = true;
        }
        assertTrue(pass);
    }
    
    /**
     * Some basic checks for the getRowKey() method.
     */
    public void testGetRowKey() {
        DefaultKeyedValues2D d = new DefaultKeyedValues2D();
        boolean pass = false;
        try {
            d.getRowKey(0);
        }
        catch (IndexOutOfBoundsException e) {
            pass = true;
        }
        assertTrue(pass);
        d.addValue(new Double(1.0), "R1", "C1");
        d.addValue(new Double(1.0), "R2", "C1");
        assertEquals("R1", d.getRowKey(0));
        assertEquals("R2", d.getRowKey(1));
        
        // check sorted rows
        d = new DefaultKeyedValues2D(true);
        d.addValue(new Double(1.0), "R1", "C1");
        assertEquals("R1", d.getRowKey(0));
        d.addValue(new Double(0.0), "R0", "C1");
        assertEquals("R0", d.getRowKey(0));
        assertEquals("R1", d.getRowKey(1));
    }
    
    /**
     * Some basic checks for the getColumnKey() method.
     */
    public void testGetColumnKey() {
        DefaultKeyedValues2D d = new DefaultKeyedValues2D();
        boolean pass = false;
        try {
            d.getColumnKey(0);
        }
        catch (IndexOutOfBoundsException e) {
            pass = true;
        }
        assertTrue(pass);
        d.addValue(new Double(1.0), "R1", "C1");
        d.addValue(new Double(1.0), "R1", "C2");
        assertEquals("C1", d.getColumnKey(0));
        assertEquals("C2", d.getColumnKey(1));
    }
    
    /**
     * Some basic checks for the removeValue() method.
     */
    public void testRemoveValue() {
        DefaultKeyedValues2D d = new DefaultKeyedValues2D();
        d.removeValue("R1", "C1");
        d.addValue(new Double(1.0), "R1", "C1");
        d.removeValue("R1", "C1");
        assertEquals(0, d.getRowCount());
        assertEquals(0, d.getColumnCount());
        
        d.addValue(new Double(1.0), "R1", "C1");
        d.addValue(new Double(2.0), "R2", "C1");
        d.removeValue("R1", "C1");
        assertEquals(new Double(2.0), d.getValue(0, 0));
    }
    
    /**
     * A test for bug 1690654.
     */
    public void testRemoveValueBug1690654() {
        DefaultKeyedValues2D d = new DefaultKeyedValues2D();
        d.addValue(new Double(1.0), "R1", "C1");
        d.addValue(new Double(2.0), "R2", "C2");
        assertEquals(2, d.getColumnCount());
        assertEquals(2, d.getRowCount());
        d.removeValue("R2", "C2");
        assertEquals(1, d.getColumnCount());
        assertEquals(1, d.getRowCount());
        assertEquals(new Double(1.0), d.getValue(0, 0));
    }
    
    /**
     * Some basic checks for the removeRow() method.
     */
    public void testRemoveRow() {
        DefaultKeyedValues2D d = new DefaultKeyedValues2D();
        boolean pass = false;
        try {
            d.removeRow(0);
        }
        catch (IndexOutOfBoundsException e) {
            pass = true;
        }
        assertTrue(pass);        
    }

}
