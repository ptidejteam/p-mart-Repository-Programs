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
 * CategoryAxis3DTests.java
 * ------------------------
 * (C) Copyright 2003, 2004, by Object Refinery Limited and Contributors.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited);
 * Contributor(s):   -;
 *
 * $Id: CategoryAxis3DTests.java,v 1.1 2007/10/10 19:34:42 vauchers Exp $
 *
 * Changes
 * -------
 * 18-Mar-2003 : Version 1 (DG);
 *
 */

package org.jfree.chart.axis.junit;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.jfree.chart.axis.CategoryAxis3D;

/**
 * Tests for the {@link CategoryAxis3D} class.
 *
 */
public class CategoryAxis3DTests extends TestCase {

    /**
     * Returns the tests as a test suite.
     *
     * @return the test suite.
     */
    public static Test suite() {
        return new TestSuite(CategoryAxis3DTests.class);
    }

    /**
     * Constructs a new set of tests.
     *
     * @param  name the name of the tests.
     */
    public CategoryAxis3DTests(String name) {
        super(name);
    }

    /**
     * Confirm that cloning works.
     */
    public void testCloning() {
        CategoryAxis3D a1 = new CategoryAxis3D("Test");
        CategoryAxis3D a2 = null;
        try {
            a2 = (CategoryAxis3D) a1.clone();
        }
        catch (CloneNotSupportedException e) {
            System.err.println("CategoryAxis3DTests.testCloning: failed to clone.");
        }
        assertTrue(a1 != a2);
        assertTrue(a1.getClass() == a2.getClass());
        assertTrue(a1.equals(a2));
    }

    /**
     * Serialize an instance, restore it, and check for equality.
     */
    public void testSerialization() {

        CategoryAxis3D a1 = new CategoryAxis3D("Test Axis");
        CategoryAxis3D a2 = null;

        try {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            ObjectOutput out = new ObjectOutputStream(buffer);
            out.writeObject(a1);
            out.close();

            ObjectInput in = new ObjectInputStream(new ByteArrayInputStream(buffer.toByteArray()));
            a2 = (CategoryAxis3D) in.readObject();
            in.close();
        }
        catch (Exception e) {
            System.out.println(e.toString());
        }
        assertEquals(a1, a2);

    }

}
