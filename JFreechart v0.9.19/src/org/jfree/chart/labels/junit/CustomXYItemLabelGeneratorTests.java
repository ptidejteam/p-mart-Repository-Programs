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
 * ----------------------------------
 * CustomXYToolTipGeneratorTests.java
 * ----------------------------------
 * (C) Copyright 2003 by Object Refinery Limited and Contributors.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited);
 * Contributor(s):   -;
 *
 * $Id: CustomXYItemLabelGeneratorTests.java,v 1.1 2007/10/10 19:34:52 vauchers Exp $
 *
 * Changes
 * -------
 * 23-Mar-2003 : Version 1 (DG);
 * 13-Aug-2003 : Added cloning tests (DG);
 *
 */

package org.jfree.chart.labels.junit;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.util.List;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.jfree.chart.labels.CustomXYToolTipGenerator;

/**
 * Tests for the {@link CustomXYToolTipGenerator} class.
 *
 */
public class CustomXYItemLabelGeneratorTests extends TestCase {

    /**
     * Returns the tests as a test suite.
     *
     * @return the test suite.
     */
    public static Test suite() {
        return new TestSuite(CustomXYItemLabelGeneratorTests.class);
    }

    /**
     * Constructs a new set of tests.
     *
     * @param  name the name of the tests.
     */
    public CustomXYItemLabelGeneratorTests(String name) {
        super(name);
    }

    /**
     * Confirm that cloning works.
     */
    public void testCloning() {
        CustomXYToolTipGenerator g1 = new CustomXYToolTipGenerator();
        CustomXYToolTipGenerator g2 = null;
        try {
            g2 = (CustomXYToolTipGenerator) g1.clone();
        }
        catch (CloneNotSupportedException e) {
            System.err.println("CustomXYItemLabelGenerator.testCloning: failed to clone.");
        }
        assertTrue(g1 != g2);
        assertTrue(g1.getClass() == g2.getClass());
        assertTrue(g1.equals(g2));
    }

    /**
     * Serialize an instance, restore it, and check for equality.
     */
    public void testSerialization() {

        List t1 = new java.util.ArrayList();
        t1.add("Tooltip A1");
        t1.add("Tooltip A2");
        t1.add("Tooltip A3");

        List t2 = new java.util.ArrayList();
        t2.add("Tooltip B1");
        t2.add("Tooltip B2");
        t2.add("Tooltip B3");

        CustomXYToolTipGenerator g1 = new CustomXYToolTipGenerator();
        g1.addToolTipSeries(t1);
        g1.addToolTipSeries(t2);
        CustomXYToolTipGenerator g2 = null;

        try {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            ObjectOutput out = new ObjectOutputStream(buffer);
            out.writeObject(g1);
            out.close();

            ObjectInput in = new ObjectInputStream(new ByteArrayInputStream(buffer.toByteArray()));
            g2 = (CustomXYToolTipGenerator) in.readObject();
            in.close();
        }
        catch (Exception e) {
            System.out.println(e.toString());
        }
        assertEquals(g1, g2);

    }

}
