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
 * BarRendererTests.java
 * ---------------------
 * (C) Copyright 2003-2005, by Object Refinery Limited and Contributors.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited);
 * Contributor(s):   -;
 *
 * $Id: BarRendererTests.java,v 1.1 2007/10/10 20:11:30 vauchers Exp $
 *
 * Changes
 * -------
 * 25-Mar-2003 : Version 1 (DG);
 * 19-Aug-2003 : Renamed HorizontalBarRendererTests --> BarRendererTests (DG);
 * 22-Oct-2003 : Added hashCode test (DG);
 * 18-May-2005 : Added field to equals() test (DG);
 * 22-Sep-2005 : Renamed getMaxBarWidth() --> getMaximumBarWidth() (DG);
 *
 */

package org.jfree.chart.renderer.category.junit;

import java.awt.Color;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.jfree.chart.labels.ItemLabelAnchor;
import org.jfree.chart.labels.ItemLabelPosition;
import org.jfree.chart.labels.StandardCategoryItemLabelGenerator;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.junit.RendererChangeDetector;
import org.jfree.ui.GradientPaintTransformType;
import org.jfree.ui.StandardGradientPaintTransformer;
import org.jfree.ui.TextAnchor;

/**
 * Tests for the {@link BarRenderer} class.
 */
public class BarRendererTests extends TestCase {

    /**
     * Returns the tests as a test suite.
     *
     * @return The test suite.
     */
    public static Test suite() {
        return new TestSuite(BarRendererTests.class);
    }

    /**
     * Constructs a new set of tests.
     *
     * @param name  the name of the tests.
     */
    public BarRendererTests(String name) {
        super(name);
    }

    /**
     * Test that the equals() method distinguishes all fields.
     */
    public void testEquals() {
        BarRenderer r1 = new BarRenderer();
        BarRenderer r2 = new BarRenderer();
        assertTrue(r1.equals(r2));
        assertTrue(r2.equals(r1));
        
        // base value
        r1.setBase(0.123);
        assertFalse(r1.equals(r2));
        r2.setBase(0.123);
        assertTrue(r1.equals(r2));
        
        // itemMargin
        r1.setItemMargin(0.22);
        assertFalse(r1.equals(r2));
        r2.setItemMargin(0.22);
        assertTrue(r1.equals(r2));
        
        // drawBarOutline
        r1.setDrawBarOutline(!r1.isDrawBarOutline());
        assertFalse(r1.equals(r2));
        r2.setDrawBarOutline(!r2.isDrawBarOutline());
        assertTrue(r1.equals(r2));
        
        // maximumBarWidth
        r1.setMaximumBarWidth(0.11);
        assertFalse(r1.equals(r2));
        r2.setMaximumBarWidth(0.11);
        assertTrue(r1.equals(r2));
        
        // minimumBarLength
        r1.setMinimumBarLength(0.04);
        assertFalse(r1.equals(r2));
        r2.setMinimumBarLength(0.04);
        assertTrue(r1.equals(r2));
        
        // gradientPaintTransformer
        r1.setGradientPaintTransformer(
            new StandardGradientPaintTransformer(
                GradientPaintTransformType.CENTER_VERTICAL
            )
        );
        assertFalse(r1.equals(r2));
        r2.setGradientPaintTransformer(
            new StandardGradientPaintTransformer(
                GradientPaintTransformType.CENTER_VERTICAL
            )
        );
        assertTrue(r1.equals(r2));
        
        // positiveItemLabelPositionFallback
        r1.setPositiveItemLabelPositionFallback(
            new ItemLabelPosition(ItemLabelAnchor.INSIDE1, TextAnchor.CENTER)
        );
        assertFalse(r1.equals(r2));
        r2.setPositiveItemLabelPositionFallback(
            new ItemLabelPosition(ItemLabelAnchor.INSIDE1, TextAnchor.CENTER)
        );
        assertTrue(r1.equals(r2));

        // negativeItemLabelPositionFallback
        r1.setNegativeItemLabelPositionFallback(
            new ItemLabelPosition(ItemLabelAnchor.INSIDE1, TextAnchor.CENTER)
        );
        assertFalse(r1.equals(r2));
        r2.setNegativeItemLabelPositionFallback(
            new ItemLabelPosition(ItemLabelAnchor.INSIDE1, TextAnchor.CENTER)
        );
        assertTrue(r1.equals(r2));

    }

    /**
     * Two objects that are equal are required to return the same hashCode. 
     */
    public void testHashcode() {
        BarRenderer r1 = new BarRenderer();
        BarRenderer r2 = new BarRenderer();
        assertTrue(r1.equals(r2));
        int h1 = r1.hashCode();
        int h2 = r2.hashCode();
        assertEquals(h1, h2);
    }
    
    /**
     * Confirm that cloning works.
     */
    public void testCloning() {
        BarRenderer r1 = new BarRenderer();
        r1.setItemLabelGenerator(new StandardCategoryItemLabelGenerator());
        BarRenderer r2 = null;
        try {
            r2 = (BarRenderer) r1.clone();
        }
        catch (CloneNotSupportedException e) {
            System.err.println("Failed to clone.");
        }
        assertTrue(r1 != r2);
        assertTrue(r1.getClass() == r2.getClass());
        assertTrue(r1.equals(r2));
    }

    /**
     * Serialize an instance, restore it, and check for equality.
     */
    public void testSerialization() {

        BarRenderer r1 = new BarRenderer();
        BarRenderer r2 = null;

        try {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            ObjectOutput out = new ObjectOutputStream(buffer);
            out.writeObject(r1);
            out.close();

            ObjectInput in = new ObjectInputStream(
                new ByteArrayInputStream(buffer.toByteArray())
            );
            r2 = (BarRenderer) in.readObject();
            in.close();
        }
        catch (Exception e) {
            System.out.println(e.toString());
        }
        assertEquals(r1, r2);

    }
    
    /**
     * Tests each setter method to ensure that it sends an event notification.
     */
    public void testEventNotification() {
        
        RendererChangeDetector detector = new RendererChangeDetector();
        BarRenderer r1 = new BarRenderer();
        r1.addChangeListener(detector);
        
        detector.setNotified(false);
        r1.setPaint(Color.red);
        assertTrue(detector.getNotified());

    }

}
