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
 * XYDataItemTests.java
 * --------------------
 * (C) Copyright 2003 by Object Refinery Limited and Contributors.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited);
 * Contributor(s):   -;
 *
 * $Id: XYDataItemTests.java,v 1.1 2007/10/10 19:25:32 vauchers Exp $
 *
 * Changes
 * -------
 * 23-Dec-2003 : Version 1 (DG);
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

import org.jfree.data.XYDataItem;

/**
 * Tests for the {@link XYDataItem} class.
 *
 * @author David Gilbert
 */
public class XYDataItemTests extends TestCase {

    /**
     * Returns the tests as a test suite.
     *
     * @return the test suite.
     */
    public static Test suite() {
        return new TestSuite(XYDataItemTests.class);
    }

    /**
     * Constructs a new set of tests.
     *
     * @param  name the name of the tests.
     */
    public XYDataItemTests(String name) {
        super(name);
    }

    /**
     * Confirm that the equals method can distinguish all the required fields.
     */
    public void testEquals() {
        
        XYDataItem i1 = new XYDataItem(1.0, 1.1);
        XYDataItem i2 = new XYDataItem(1.0, 1.1);
        assertTrue(i1.equals(i2));
        assertTrue(i2.equals(i1));

        i1.setY(new Double(9.9));
        assertFalse(i1.equals(i2));

        i2.setY(new Double(9.9));
        assertTrue(i1.equals(i2));

    }

    /**
     * Confirm that cloning works.
     */
    public void testCloning() {
        XYDataItem i1 = new XYDataItem(1.0, 1.1);
        XYDataItem i2 = null;
        try {
            i2 = (XYDataItem) i1.clone();
        }
        catch (CloneNotSupportedException e) {
            System.err.println("XYDataItemTests.testCloning: failed to clone.");
        }
        assertTrue(i1 != i2);
        assertTrue(i1.getClass() == i2.getClass());
        assertTrue(i1.equals(i2));
    }

    /**
     * Serialize an instance, restore it, and check for equality.
     */
    public void testSerialization() {

        XYDataItem i1 = new XYDataItem(1.0, 1.1);
        XYDataItem i2 = null;
        
        try {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            ObjectOutput out = new ObjectOutputStream(buffer);
            out.writeObject(i1);
            out.close();

            ObjectInput in = new ObjectInputStream(new ByteArrayInputStream(buffer.toByteArray()));
            i2 = (XYDataItem) in.readObject();
            in.close();
        }
        catch (Exception e) {
            System.out.println(e.toString());
        }
        assertEquals(i1, i2);

    }

}
