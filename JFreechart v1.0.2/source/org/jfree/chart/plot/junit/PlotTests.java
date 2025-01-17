/* ===========================================================
 * JFreeChart : a free chart library for the Java(tm) platform
 * ===========================================================
 *
 * (C) Copyright 2000-2006, by Object Refinery Limited and Contributors.
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
 * --------------
 * PlotTests.java
 * --------------
 * (C) Copyright 2005, 2006, by Object Refinery Limited and Contributors.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited);
 * Contributor(s):   -;
 *
 * $Id: PlotTests.java,v 1.1 2007/10/10 20:22:58 vauchers Exp $
 *
 * Changes
 * -------
 * 06-Jun-2005 : Version 1 (DG);
 * 30-Jun-2006 : Extended equals() test to cover new field (DG);
 *
 */

package org.jfree.chart.plot.junit;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Paint;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.Stroke;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.DefaultDrawingSupplier;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.plot.Plot;
import org.jfree.ui.Align;
import org.jfree.ui.RectangleInsets;

/**
 * Some tests for the {@link Plot} class.
 */
public class PlotTests extends TestCase {

    /**
     * Returns the tests as a test suite.
     *
     * @return The test suite.
     */
    public static Test suite() {
        return new TestSuite(PlotTests.class);
    }

    /**
     * Constructs a new set of tests.
     *
     * @param name  the name of the tests.
     */
    public PlotTests(String name) {
        super(name);
    }

    /**
     * Check that the equals() method can distinguish all fields (note that 
     * the dataset is NOT considered in the equals() method).
     */
    public void testEquals() {
        PiePlot plot1 = new PiePlot();
        PiePlot plot2 = new PiePlot();
        assertTrue(plot1.equals(plot2));    
        assertTrue(plot2.equals(plot1));

        // noDataMessage
        plot1.setNoDataMessage("No data XYZ");
        assertFalse(plot1.equals(plot2));
        plot2.setNoDataMessage("No data XYZ");
        assertTrue(plot1.equals(plot2));
        
        // noDataMessageFont
        plot1.setNoDataMessageFont(new Font("SansSerif", Font.PLAIN, 13));
        assertFalse(plot1.equals(plot2));
        plot2.setNoDataMessageFont(new Font("SansSerif", Font.PLAIN, 13));
        assertTrue(plot1.equals(plot2));
        
        // noDataMessagePaint
        plot1.setNoDataMessagePaint(new GradientPaint(1.0f, 2.0f, Color.red, 
                3.0f, 4.0f, Color.blue));
        assertFalse(plot1.equals(plot2));
        plot2.setNoDataMessagePaint(new GradientPaint(1.0f, 2.0f, Color.red, 
                3.0f, 4.0f, Color.blue));
        assertTrue(plot1.equals(plot2));
        
        // insets
        plot1.setInsets(new RectangleInsets(1.0, 2.0, 3.0, 4.0));
        assertFalse(plot1.equals(plot2));
        plot2.setInsets(new RectangleInsets(1.0, 2.0, 3.0, 4.0));
        assertTrue(plot1.equals(plot2));
        
        // outlineStroke
        BasicStroke s = new BasicStroke(1.23f);
        plot1.setOutlineStroke(s);
        assertFalse(plot1.equals(plot2));
        plot2.setOutlineStroke(s);
        assertTrue(plot1.equals(plot2));
        
        // outlinePaint
        plot1.setOutlinePaint(new GradientPaint(1.0f, 2.0f, Color.yellow, 
                3.0f, 4.0f, Color.green));
        assertFalse(plot1.equals(plot2));
        plot2.setOutlinePaint(new GradientPaint(1.0f, 2.0f, Color.yellow, 
                3.0f, 4.0f, Color.green));
        assertTrue(plot1.equals(plot2));
        
        // backgroundPaint
        plot1.setBackgroundPaint(new GradientPaint(1.0f, 2.0f, Color.cyan, 
                3.0f, 4.0f, Color.green));
        assertFalse(plot1.equals(plot2));
        plot2.setBackgroundPaint(new GradientPaint(1.0f, 2.0f, Color.cyan, 
                3.0f, 4.0f, Color.green));
        assertTrue(plot1.equals(plot2));
        
        // backgroundImage
        plot1.setBackgroundImage(JFreeChart.INFO.getLogo());
        assertFalse(plot1.equals(plot2));
        plot2.setBackgroundImage(JFreeChart.INFO.getLogo());
        assertTrue(plot1.equals(plot2));
        
        // backgroundImageAlignment
        plot1.setBackgroundImageAlignment(Align.BOTTOM_RIGHT);
        assertFalse(plot1.equals(plot2));
        plot2.setBackgroundImageAlignment(Align.BOTTOM_RIGHT);
        assertTrue(plot1.equals(plot2));
        
        // backgroundImageAlpha
        plot1.setBackgroundImageAlpha(0.77f);
        assertFalse(plot1.equals(plot2));
        plot2.setBackgroundImageAlpha(0.77f);
        assertTrue(plot1.equals(plot2));
        
        // foregroundAlpha
        plot1.setForegroundAlpha(0.99f);
        assertFalse(plot1.equals(plot2));
        plot2.setForegroundAlpha(0.99f);
        assertTrue(plot1.equals(plot2));
        
        // backgroundAlpha
        plot1.setBackgroundAlpha(0.99f);
        assertFalse(plot1.equals(plot2));
        plot2.setBackgroundAlpha(0.99f);
        assertTrue(plot1.equals(plot2));
        
        // drawingSupplier
        plot1.setDrawingSupplier(new DefaultDrawingSupplier(
            new Paint[] {Color.blue}, new Paint[] {Color.red}, 
            new Stroke[] {new BasicStroke(1.1f)}, 
            new Stroke[] {new BasicStroke(9.9f)}, 
            new Shape[] {new Rectangle(1, 2, 3, 4)}));
        assertFalse(plot1.equals(plot2));
        plot2.setDrawingSupplier(new DefaultDrawingSupplier(
            new Paint[] {Color.blue}, new Paint[] {Color.red}, 
            new Stroke[] {new BasicStroke(1.1f)}, 
            new Stroke[] {new BasicStroke(9.9f)}, 
            new Shape[] {new Rectangle(1, 2, 3, 4)}));
        assertTrue(plot1.equals(plot2));
    }
    
}
