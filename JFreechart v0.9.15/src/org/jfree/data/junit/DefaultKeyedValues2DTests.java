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
 * ------------------------------
 * DefaultKeyedValues2DTests.java
 * ------------------------------
 * (C) Copyright 2003 by Object Refinery Limited and Contributors.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited);
 * Contributor(s):   -;
 *
 * $Id: DefaultKeyedValues2DTests.java,v 1.1 2007/10/10 19:22:02 vauchers Exp $
 *
 * Changes
 * -------
 * 13-Mar-2003 : Version 1 (DG);
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

/**
 * Tests for the {@link DefaultKeyedValue} class.
 *
 * @author David Gilbert
 */
public class DefaultKeyedValues2DTests extends TestCase {

    /**
     * Returns the tests as a test suite.
     *
     * @return the test suite.
     */
    public static Test suite() {
        return new TestSuite(DefaultKeyedValues2DTests.class);
    }

    /**
     * Constructs a new set of tests.
     *
     * @param  name the name of the tests.
     */
    public DefaultKeyedValues2DTests(String name) {
        super(name);
    }

    /**
     * Confirm that cloning works.
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
            System.err.println("DefaultKeyedValues2DTests.testCloning: failed to clone.");
        }
        assertTrue(v1 != v2);
        assertTrue(v1.getClass() == v2.getClass());
        assertTrue(v1.equals(v2));
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

            ObjectInput in = new ObjectInputStream(new ByteArrayInputStream(buffer.toByteArray()));
            kv2D2 = (DefaultKeyedValues2D) in.readObject();
            in.close();
        }
        catch (Exception e) {
            System.out.println(e.toString());
        }
        assertEquals(kv2D1, kv2D2);

    }

}
