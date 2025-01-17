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
 * ---------------------
 * MeterNeedleTests.java
 * ---------------------
 * (C) Copyright 2005, by Object Refinery Limited and Contributors.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited);
 * Contributor(s):   -;
 *
 * $Id: MeterNeedleTests.java,v 1.1 2007/10/10 20:22:58 vauchers Exp $
 *
 * Changes
 * -------
 * 08-Jun-2005 : Version 1 (DG);
 *
 */

package org.jfree.chart.needle.junit;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Stroke;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.jfree.chart.needle.LineNeedle;
import org.jfree.chart.needle.MeterNeedle;

/**
 * Tests for the {@link MeterNeedle} class.
 */
public class MeterNeedleTests extends TestCase {
    /**
     * Returns the tests as a test suite.
     *
     * @return The test suite.
     */
    public static Test suite() {
        return new TestSuite(MeterNeedleTests.class);
    }

    /**
     * Constructs a new set of tests.
     *
     * @param name  the name of the tests.
     */
    public MeterNeedleTests(String name) {
        super(name);
    }

    /**
     * Check that the equals() method can distinguish all fields.
     */
    public void testEquals() {
        MeterNeedle n1 = new LineNeedle();
        MeterNeedle n2 = new LineNeedle();
        assertTrue(n1.equals(n2));
        
        n1.setFillPaint(new GradientPaint(1.0f, 2.0f, Color.red, 3.0f, 4.0f, Color.blue));
        assertFalse(n1.equals(n2));
        n2.setFillPaint(new GradientPaint(1.0f, 2.0f, Color.red, 3.0f, 4.0f, Color.blue));
        assertTrue(n1.equals(n2));
        
        n1.setOutlinePaint(new GradientPaint(5.0f, 6.0f, Color.red, 7.0f, 8.0f, Color.blue));
        assertFalse(n1.equals(n2));
        n2.setOutlinePaint(new GradientPaint(5.0f, 6.0f, Color.red, 7.0f, 8.0f, Color.blue));
        assertTrue(n1.equals(n2));

        n1.setHighlightPaint(new GradientPaint(9.0f, 0.0f, Color.red, 1.0f, 2.0f, Color.blue));
        assertFalse(n1.equals(n2));
        n2.setHighlightPaint(new GradientPaint(9.0f, 0.0f, Color.red, 1.0f, 2.0f, Color.blue));
        assertTrue(n1.equals(n2));
        
        Stroke s = new BasicStroke(1.23f);
        n1.setOutlineStroke(s);
        assertFalse(n1.equals(n2));
        n2.setOutlineStroke(s);
        assertTrue(n1.equals(n2));
        
        n1.setRotateX(1.23);
        assertFalse(n1.equals(n2));
        n2.setRotateX(1.23);
        assertTrue(n1.equals(n2));
        
        n1.setRotateY(4.56);
        assertFalse(n1.equals(n2));
        n2.setRotateY(4.56);
        assertTrue(n1.equals(n2));
        
        n1.setSize(11);
        assertFalse(n1.equals(n2));
        n2.setSize(11);
        assertTrue(n1.equals(n2));
    }

}
