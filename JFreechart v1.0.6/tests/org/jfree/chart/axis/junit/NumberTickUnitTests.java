/* ===========================================================
 * JFreeChart : a free chart library for the Java(tm) platform
 * ===========================================================
 *
 * (C) Copyright 2000-2005, by Object Refinery Limited and Contributors.
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
 * ------------------------
 * NumberTickUnitTests.java
 * ------------------------
 * (C) Copyright 2005, by Object Refinery Limited and Contributors.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited);
 * Contributor(s):   -;
 *
 * $Id: NumberTickUnitTests.java,v 1.1 2007/10/10 20:53:15 vauchers Exp $
 *
 * Changes
 * -------
 * 5-Jul-2005 : Version 1 (DG);
 *
 */

package org.jfree.chart.axis.junit;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.text.DecimalFormat;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.jfree.chart.axis.NumberTickUnit;

/**
 * Some tests for the {@link NumberTickUnit} class.
 */
public class NumberTickUnitTests extends TestCase {

    /**
     * Returns the tests as a test suite.
     *
     * @return The test suite.
     */
    public static Test suite() {
        return new TestSuite(NumberTickUnitTests.class);
    }

    /**
     * Constructs a new set of tests.
     *
     * @param name  the name of the tests.
     */
    public NumberTickUnitTests(String name) {
        super(name);
    }

    /**
     * Confirm that the equals method can distinguish all the required fields.
     */
    public void testEquals() {
        NumberTickUnit t1 = new NumberTickUnit(1.23, new DecimalFormat("0.00"));
        NumberTickUnit t2 = new NumberTickUnit(1.23, new DecimalFormat("0.00"));
        assertTrue(t1.equals(t2));
        assertTrue(t2.equals(t1));
        
        t1 = new NumberTickUnit(3.21, new DecimalFormat("0.00"));
        assertFalse(t1.equals(t2));
        t2 = new NumberTickUnit(3.21, new DecimalFormat("0.00"));
        assertTrue(t1.equals(t2));
        
        t1 = new NumberTickUnit(3.21, new DecimalFormat("0.000"));
        assertFalse(t1.equals(t2));
        t2 = new NumberTickUnit(3.21, new DecimalFormat("0.000"));
        assertTrue(t1.equals(t2));
    }

    /**
     * Two objects that are equal are required to return the same hashCode. 
     */
    public void testHashCode() {
        NumberTickUnit t1 = new NumberTickUnit(1.23, new DecimalFormat("0.00"));
        NumberTickUnit t2 = new NumberTickUnit(1.23, new DecimalFormat("0.00"));
        int h1 = t1.hashCode();
        int h2 = t2.hashCode();
        assertEquals(h1, h2);
    }

    /**
     * This is an immutable class so it doesn't need to be cloneable.
     */
    public void testCloning() {
        NumberTickUnit t1 = new NumberTickUnit(1.23, new DecimalFormat("0.00"));
        assertFalse(t1 instanceof Cloneable);
    }

    /**
     * Serialize an instance, restore it, and check for equality.
     */
    public void testSerialization() {
        NumberTickUnit t1 = new NumberTickUnit(1.23, new DecimalFormat("0.00"));
        NumberTickUnit t2 = null;
        try {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            ObjectOutput out = new ObjectOutputStream(buffer);
            out.writeObject(t1);
            out.close();

            ObjectInput in = new ObjectInputStream(
                new ByteArrayInputStream(buffer.toByteArray())
            );
            t2 = (NumberTickUnit) in.readObject();
            in.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        assertEquals(t1, t2);
    }

}
