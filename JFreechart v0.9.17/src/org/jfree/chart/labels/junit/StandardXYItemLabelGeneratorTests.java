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
 * ------------------------------------
 * StandardXYToolTipGeneratorTests.java
 * ------------------------------------
 * (C) Copyright 2003, 2004, by Object Refinery Limited and Contributors.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited);
 * Contributor(s):   -;
 *
 * $Id: StandardXYItemLabelGeneratorTests.java,v 1.1 2007/10/10 19:29:20 vauchers Exp $
 *
 * Changes
 * -------
 * 23-Mar-2003 : Version 1 (DG);
 * 26-Feb-2004 : Updates for new code (DG);
 *
 */

package org.jfree.chart.labels.junit;

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

import org.jfree.chart.labels.StandardXYItemLabelGenerator;

/**
 * Tests for the {@link StandardXYItemLabelGenerator} class.
 *
 * @author David Gilbert
 */
public class StandardXYItemLabelGeneratorTests extends TestCase {

    /**
     * Returns the tests as a test suite.
     *
     * @return the test suite.
     */
    public static Test suite() {
        return new TestSuite(StandardXYItemLabelGeneratorTests.class);
    }

    /**
     * Constructs a new set of tests.
     *
     * @param  name the name of the tests.
     */
    public StandardXYItemLabelGeneratorTests(String name) {
        super(name);
    }

    /**
     * A series of tests for the equals() method.
     */
    public void testEquals() {
        
        // standard test
        StandardXYItemLabelGenerator g1 = new StandardXYItemLabelGenerator();
        StandardXYItemLabelGenerator g2 = new StandardXYItemLabelGenerator();
        assertTrue(g1.equals(g2));
        assertTrue(g2.equals(g1));
        
        // tooltip format
        g1 = new StandardXYItemLabelGenerator(
            "{0} --> {1} {2}", "{2}", new DecimalFormat("0.0"), new DecimalFormat("0.0")
        );
        g2 = new StandardXYItemLabelGenerator(
            "{1} {2}", "{2}", new DecimalFormat("0.0"), new DecimalFormat("0.0")
        );
        assertFalse(g1.equals(g2));
        g2 = new StandardXYItemLabelGenerator(
            "{0} --> {1} {2}", "{2}", new DecimalFormat("0.0"), new DecimalFormat("0.0")
        );
        assertTrue(g1.equals(g2));

        // item label format
        g1 = new StandardXYItemLabelGenerator(
            "{0} --> {1} {2}", "{2}", new DecimalFormat("0.0"), new DecimalFormat("0.0")
        );
        g2 = new StandardXYItemLabelGenerator(
            "{0} --> {1} {2}", "{1} {2}", new DecimalFormat("0.0"), new DecimalFormat("0.0")
        );
        assertFalse(g1.equals(g2));
        g2 = new StandardXYItemLabelGenerator(
            "{0} --> {1} {2}", "{2}", new DecimalFormat("0.0"), new DecimalFormat("0.0")
        );
        assertTrue(g1.equals(g2));

        // X format
        g1 = new StandardXYItemLabelGenerator(
            "{0} --> {1} {2}", "{2}", new DecimalFormat("0.0"), new DecimalFormat("0.0")
        );
        g2 = new StandardXYItemLabelGenerator(
            "{0} --> {1} {2}", "{2}", new DecimalFormat("0.00"), new DecimalFormat("0.0")
        );
        assertFalse(g1.equals(g2));
        g2 = new StandardXYItemLabelGenerator(
            "{0} --> {1} {2}", "{2}", new DecimalFormat("0.0"), new DecimalFormat("0.0")
        );
        assertTrue(g1.equals(g2));

        // Y format
        g1 = new StandardXYItemLabelGenerator(
            "{0} --> {1} {2}", "{2}", new DecimalFormat("0.0"), new DecimalFormat("0.0")
        );
        g2 = new StandardXYItemLabelGenerator(
            "{0} --> {1} {2}", "{2}", new DecimalFormat("0.0"), new DecimalFormat("0.00")
        );
        assertFalse(g1.equals(g2));
        g2 = new StandardXYItemLabelGenerator(
            "{0} --> {1} {2}", "{2}", new DecimalFormat("0.0"), new DecimalFormat("0.0")
        );
        assertTrue(g1.equals(g2));
    }
    
    /**
     * Confirm that cloning works.
     */
    public void testCloning() {
        StandardXYItemLabelGenerator g1 = new StandardXYItemLabelGenerator();
        StandardXYItemLabelGenerator g2 = null;
        try {
            g2 = (StandardXYItemLabelGenerator) g1.clone();
        }
        catch (CloneNotSupportedException e) {
            System.err.println("Clone failed.");
        }
        assertTrue(g1 != g2);
        assertTrue(g1.getClass() == g2.getClass());
        assertTrue(g1.equals(g2));
    }

    /**
     * Serialize an instance, restore it, and check for equality.
     */
    public void testSerialization() {

        StandardXYItemLabelGenerator g1 = new StandardXYItemLabelGenerator();
        StandardXYItemLabelGenerator g2 = null;

        try {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            ObjectOutput out = new ObjectOutputStream(buffer);
            out.writeObject(g1);
            out.close();

            ObjectInput in = new ObjectInputStream(new ByteArrayInputStream(buffer.toByteArray()));
            g2 = (StandardXYItemLabelGenerator) in.readObject();
            in.close();
        }
        catch (Exception e) {
            System.out.println(e.toString());
        }
        assertEquals(g1, g2);

    }

}
