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
 * -----------------------------
 * StackedAreaRendererTests.java
 * -----------------------------
 * (C) Copyright 2003, 2004, by Object Refinery Limited and Contributors.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited);
 * Contributor(s):   -;
 *
 * $Id: StackedAreaRendererTests.java,v 1.1 2007/10/10 19:25:33 vauchers Exp $
 *
 * Changes
 * -------
 * 25-Mar-2003 : Version 1 (DG);
 *
 */

package org.jfree.chart.renderer.junit;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.jfree.chart.renderer.StackedAreaRenderer;

/**
 * Tests for the {@link StackedAreaRendererTests} class.
 *
 * @author David Gilbert
 */
public class StackedAreaRendererTests extends TestCase {

    /**
     * Returns the tests as a test suite.
     *
     * @return the test suite.
     */
    public static Test suite() {
        return new TestSuite(StackedAreaRendererTests.class);
    }

    /**
     * Constructs a new set of tests.
     *
     * @param  name the name of the tests.
     */
    public StackedAreaRendererTests(String name) {
        super(name);
    }

    /**
     * Problem that the equals(...) method distinguishes all fields.
     */
    public void testEquals() {
        StackedAreaRenderer r1 = new StackedAreaRenderer();
        StackedAreaRenderer r2 = new StackedAreaRenderer();
        assertEquals(r1, r2);
    }

    /**
     * Two objects that are equal are required to return the same hashCode. 
     */
    public void testHashcode() {
        StackedAreaRenderer r1 = new StackedAreaRenderer();
        StackedAreaRenderer r2 = new StackedAreaRenderer();
        assertTrue(r1.equals(r2));
        int h1 = r1.hashCode();
        int h2 = r2.hashCode();
        assertEquals(h1, h2);
    }
    
    /**
     * Confirm that cloning works.
     */
    public void testCloning() {
        StackedAreaRenderer r1 = new StackedAreaRenderer();
        StackedAreaRenderer r2 = null;
        try {
            r2 = (StackedAreaRenderer) r1.clone();
        }
        catch (CloneNotSupportedException e) {
            System.err.println("StackedAreaRendererTests.testCloning: failed to clone.");
        }
        assertTrue(r1 != r2);
        assertTrue(r1.getClass() == r2.getClass());
        assertTrue(r1.equals(r2));
        
    }

    /**
     * Serialize an instance, restore it, and check for equality.
     */
    public void testSerialization() {

        StackedAreaRenderer r1 = new StackedAreaRenderer();
        StackedAreaRenderer r2 = null;

        try {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            ObjectOutput out = new ObjectOutputStream(buffer);
            out.writeObject(r1);
            out.close();

            ObjectInput in = new ObjectInputStream(new ByteArrayInputStream(buffer.toByteArray()));
            r2 = (StackedAreaRenderer) in.readObject();
            in.close();
        }
        catch (Exception e) {
            System.out.println(e.toString());
        }
        assertEquals(r1, r2);

    }

}
