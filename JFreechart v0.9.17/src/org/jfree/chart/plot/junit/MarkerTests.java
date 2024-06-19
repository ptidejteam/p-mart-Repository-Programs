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
 * ----------------
 * MarkerTests.java
 * ----------------
 * (C) Copyright 2003, 2004, by Object Refinery Limited and Contributors.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited);
 * Contributor(s):   -;
 *
 * $Id: MarkerTests.java,v 1.1 2007/10/10 19:29:17 vauchers Exp $
 *
 * Changes
 * -------
 * 18-Aug-2003 : Version 1 (DG);
 *
 */

package org.jfree.chart.plot.junit;

import java.awt.Color;
import java.awt.Font;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.jfree.chart.plot.Marker;
import org.jfree.chart.plot.ValueMarker;
import org.jfree.ui.Spacer;

/**
 * Tests for the {@link Marker} class.
 *
 * @author David Gilbert
 */
public class MarkerTests extends TestCase {

    /**
     * Returns the tests as a test suite.
     *
     * @return the test suite.
     */
    public static Test suite() {
        return new TestSuite(MarkerTests.class);
    }

    /**
     * Constructs a new set of tests.
     *
     * @param  name the name of the tests.
     */
    public MarkerTests(String name) {
        super(name);
    }

    /**
     * Confirm that the equals method can distinguish all the required fields.
     */
    public void testEquals() {
        
        Marker m1 = new ValueMarker(45.0);
        Marker m2 = new ValueMarker(45.0);
        assertTrue(m1.equals(m2));
        
        m1.setLabel("New Label");
        assertFalse(m1.equals(m2));
        m2.setLabel("New Label");
        assertTrue(m1.equals(m2));
        
        m1.setLabelFont(new Font("SansSerif", Font.PLAIN, 10));
        assertFalse(m1.equals(m2));
        m2.setLabelFont(new Font("SansSerif", Font.PLAIN, 10));
        assertTrue(m1.equals(m2));

        m1.setLabelPaint(Color.red);
        assertFalse(m1.equals(m2));
        m2.setLabelPaint(Color.red);
        assertTrue(m1.equals(m2));

    }
        
    /**
     * Serialize an instance, restore it, and check for equality.
     */
    public void testSerialization() {

        Spacer s1 = new Spacer(Spacer.ABSOLUTE, 0.05, 0.05, 0.05, 0.05);
        Spacer s2 = null;

        try {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            ObjectOutput out = new ObjectOutputStream(buffer);
            out.writeObject(s1);
            out.close();

            ObjectInput in = new ObjectInputStream(new ByteArrayInputStream(buffer.toByteArray()));
            s2 = (Spacer) in.readObject();
            in.close();
        }
        catch (Exception e) {
            System.out.println(e.toString());
        }
        boolean b = s1.equals(s2);
        assertTrue(b);

    }

}
