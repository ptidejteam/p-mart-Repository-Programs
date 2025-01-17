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
 * -----------------------------
 * XYPointerAnnotationTests.java
 * -----------------------------
 * (C) Copyright 2003 by Object Refinery Limited and Contributors.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited);
 * Contributor(s):   -;
 *
 * $Id: XYPointerAnnotationTests.java,v 1.1 2007/10/10 19:15:35 vauchers Exp $
 *
 * Changes
 * -------
 * 19-Aug-2003 : Version 1 (DG);
 *
 */

package org.jfree.chart.annotations.junit;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.jfree.chart.annotations.XYPointerAnnotation;

/**
 * Tests for the {@link XYPointerAnnotation} class.
 *
 * @author David Gilbert
 */
public class XYPointerAnnotationTests extends TestCase {

    /**
     * Returns the tests as a test suite.
     *
     * @return the test suite.
     */
    public static Test suite() {
        return new TestSuite(XYPointerAnnotationTests.class);
    }

    /**
     * Constructs a new set of tests.
     *
     * @param  name the name of the tests.
     */
    public XYPointerAnnotationTests(String name) {
        super(name);
    }

    /**
     * Confirm that the equals method can distinguish all the required fields.
     */
    public void testEquals() {
        
        XYPointerAnnotation a1 = new XYPointerAnnotation("Label", 10.0, 20.0, Math.PI);
        XYPointerAnnotation a2 = new XYPointerAnnotation("Label", 10.0, 20.0, Math.PI);
        boolean b = a1.equals(a2);
        assertTrue(a1.equals(a2));
      
    }

    /**
     * Confirm that cloning works.
     */
    public void testCloning() {

        XYPointerAnnotation a1 = new XYPointerAnnotation("Label", 10.0, 20.0, Math.PI);
        XYPointerAnnotation a2 = null;
        try {
            a2 = (XYPointerAnnotation) a1.clone();
        }
        catch (CloneNotSupportedException e) {
            System.err.println("XYPointerAnnotationTests.testCloning: failed to clone.");
        }
        assertTrue(a1 != a2);
        assertTrue(a1.getClass() == a2.getClass());
        assertTrue(a1.equals(a2));
    }

    /**
     * Serialize an instance, restore it, and check for equality.
     */
    public void testSerialization() {

        XYPointerAnnotation a1 = new XYPointerAnnotation("Label", 10.0, 20.0, Math.PI);
        XYPointerAnnotation a2 = null;

        try {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            ObjectOutput out = new ObjectOutputStream(buffer);
            out.writeObject(a1);
            out.close();

            ObjectInput in = new ObjectInputStream(new ByteArrayInputStream(buffer.toByteArray()));
            a2 = (XYPointerAnnotation) in.readObject();
            in.close();
        }
        catch (Exception e) {
            System.out.println(e.toString());
        }
        assertEquals(a1, a2);

    }

}
