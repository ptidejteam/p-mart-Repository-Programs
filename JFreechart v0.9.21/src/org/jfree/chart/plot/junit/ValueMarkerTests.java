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
 * ---------------------
 * ValueMarkerTests.java
 * ---------------------
 * (C) Copyright 2003, 2004, by Object Refinery Limited and Contributors.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited);
 * Contributor(s):   -;
 *
 * $Id: ValueMarkerTests.java,v 1.1 2007/10/10 19:50:27 vauchers Exp $
 *
 * Changes
 * -------
 * 18-Aug-2003 : Version 1 (DG);
 * 14-Jun-2004 : Renamed MarkerTests --> ValueMarkerTests (DG);
 *
 */

package org.jfree.chart.plot.junit;

import java.awt.BasicStroke;
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
import org.jfree.ui.RectangleAnchor;
import org.jfree.ui.RectangleInsets;
import org.jfree.ui.TextAnchor;
import org.jfree.util.UnitType;

/**
 * Tests for the {@link ValueMarker} class.
 */
public class ValueMarkerTests extends TestCase {

    /**
     * Returns the tests as a test suite.
     *
     * @return The test suite.
     */
    public static Test suite() {
        return new TestSuite(ValueMarkerTests.class);
    }

    /**
     * Constructs a new set of tests.
     *
     * @param name  the name of the tests.
     */
    public ValueMarkerTests(String name) {
        super(name);
    }

    /**
     * Confirm that the equals method can distinguish all the required fields.
     */
    public void testEquals() {
        
        Marker m1 = new ValueMarker(45.0);
        Marker m2 = new ValueMarker(45.0);
        assertTrue(m1.equals(m2));
        assertTrue(m2.equals(m1));
        
        m1.setPaint(Color.red);
        assertFalse(m1.equals(m2));
        m2.setPaint(Color.red);
        assertTrue(m1.equals(m2));
        
        BasicStroke stroke = new BasicStroke(2.2f);
        m1.setStroke(stroke);
        assertFalse(m1.equals(m2));
        m2.setStroke(stroke);
        assertTrue(m1.equals(m2));
        
        m1.setOutlinePaint(Color.red);
        assertFalse(m1.equals(m2));
        m2.setOutlinePaint(Color.red);
        assertTrue(m1.equals(m2));
        
        m1.setOutlineStroke(stroke);
        assertFalse(m1.equals(m2));
        m2.setOutlineStroke(stroke);
        assertTrue(m1.equals(m2));

        m1.setAlpha(0.1f);
        assertFalse(m1.equals(m2));
        m2.setAlpha(0.1f);
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

        m1.setLabelAnchor(RectangleAnchor.TOP_RIGHT);
        assertFalse(m1.equals(m2));
        m2.setLabelAnchor(RectangleAnchor.TOP_RIGHT);
        assertTrue(m1.equals(m2));
        
        m1.setLabelTextAnchor(TextAnchor.BASELINE_RIGHT);
        assertFalse(m1.equals(m2));
        m2.setLabelTextAnchor(TextAnchor.BASELINE_RIGHT);
        assertTrue(m1.equals(m2));
        
        m1.setLabelOffset(new RectangleInsets(UnitType.ABSOLUTE, 10.0, 10.0, 10.0, 10.0));
        assertFalse(m1.equals(m2));
        m2.setLabelOffset(new RectangleInsets(UnitType.ABSOLUTE, 10.0, 10.0, 10.0, 10.0));
        assertTrue(m1.equals(m2));
    }
        
    /**
     * Confirm that cloning works.
     */
    public void testCloning() {
        ValueMarker m1 = new ValueMarker(25.0);
        ValueMarker m2 = null;
        try {
            m2 = (ValueMarker) m1.clone();
        }
        catch (CloneNotSupportedException e) {
            System.err.println("Failed to clone.");
        }
        assertTrue(m1 != m2);
        assertTrue(m1.getClass() == m2.getClass());
        assertTrue(m1.equals(m2));
    }

   /**
     * Serialize an instance, restore it, and check for equality.
     */
    public void testSerialization() {

        ValueMarker m1 = new ValueMarker(25.0);
        ValueMarker m2 = null;

        try {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            ObjectOutput out = new ObjectOutputStream(buffer);
            out.writeObject(m1);
            out.close();

            ObjectInput in = new ObjectInputStream(new ByteArrayInputStream(buffer.toByteArray()));
            m2 = (ValueMarker) in.readObject();
            in.close();
        }
        catch (Exception e) {
            System.out.println(e.toString());
        }
        boolean b = m1.equals(m2);
        assertTrue(b);

    }

}
