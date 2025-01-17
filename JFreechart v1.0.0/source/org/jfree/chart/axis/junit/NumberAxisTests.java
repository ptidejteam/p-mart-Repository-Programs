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
 * --------------------
 * NumberAxisTests.java
 * --------------------
 * (C) Copyright 2003-2005, by Object Refinery Limited and Contributors.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited);
 * Contributor(s):   -;
 *
 * $Id: NumberAxisTests.java,v 1.1 2007/10/10 20:11:17 vauchers Exp $
 *
 * Changes
 * -------
 * 26-Mar-2003 : Version 1 (DG);
 * 14-Aug-2003 : Added tests for equals() method (DG);
 * 05-Oct-2004 : Added tests to pick up a bug in the auto-range calculation for
 *               a domain axis on an XYPlot using an XYSeriesCollection (DG);
 * 07-Jan-2005 : Added test for hashCode() (DG):
 *
 */

package org.jfree.chart.axis.junit;

import java.awt.geom.Rectangle2D;
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

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.NumberTickUnit;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.RectangleEdge;

/**
 * Tests for the {@link NumberAxis} class.
 */
public class NumberAxisTests extends TestCase {

    /**
     * Returns the tests as a test suite.
     *
     * @return The test suite.
     */
    public static Test suite() {
        return new TestSuite(NumberAxisTests.class);
    }

    /**
     * Constructs a new set of tests.
     *
     * @param name  the name of the tests.
     */
    public NumberAxisTests(String name) {
        super(name);
    }
    
    /**
     * Confirm that cloning works.
     */
    public void testCloning() {
        NumberAxis a1 = new NumberAxis("Test");
        NumberAxis a2 = null;
        try {
            a2 = (NumberAxis) a1.clone();
        }
        catch (CloneNotSupportedException e) {
            System.err.println("Failed to clone.");
        }
        assertTrue(a1 != a2);
        assertTrue(a1.getClass() == a2.getClass());
        assertTrue(a1.equals(a2));
    }

    /**
     * Confirm that the equals method can distinguish all the required fields.
     */
    public void testEquals() {
        
        NumberAxis a1 = new NumberAxis("Test");
        NumberAxis a2 = new NumberAxis("Test");
        assertTrue(a1.equals(a2));
        
        //private boolean autoRangeIncludesZero;
        a1.setAutoRangeIncludesZero(false);
        assertFalse(a1.equals(a2));
        a2.setAutoRangeIncludesZero(false);
        assertTrue(a1.equals(a2));

        //private boolean autoRangeStickyZero;
        a1.setAutoRangeStickyZero(false);
        assertFalse(a1.equals(a2));
        a2.setAutoRangeStickyZero(false);
        assertTrue(a1.equals(a2));

        //private NumberTickUnit tickUnit;
        a1.setTickUnit(new NumberTickUnit(25.0));
        assertFalse(a1.equals(a2));
        a2.setTickUnit(new NumberTickUnit(25.0));
        assertTrue(a1.equals(a2));

        //private NumberFormat numberFormatOverride;
        a1.setNumberFormatOverride(new DecimalFormat("0.00"));
        assertFalse(a1.equals(a2));
        a2.setNumberFormatOverride(new DecimalFormat("0.00"));
        assertTrue(a1.equals(a2));

    }

    /**
     * Two objects that are equal are required to return the same hashCode. 
     */
    public void testHashCode() {
        NumberAxis a1 = new NumberAxis("Test");
        NumberAxis a2 = new NumberAxis("Test");
        assertTrue(a1.equals(a2));
        int h1 = a1.hashCode();
        int h2 = a2.hashCode();
        assertEquals(h1, h2);
    }

    private static final double EPSILON = 0.0000001;
    
    /**
     * Test the translation of Java2D values to data values.
     */
    public void testTranslateJava2DToValue() {
        NumberAxis axis = new NumberAxis();
        axis.setRange(50.0, 100.0); 
        Rectangle2D dataArea = new Rectangle2D.Double(10.0, 50.0, 400.0, 300.0);
        double y1 = axis.java2DToValue(75.0, dataArea, RectangleEdge.LEFT);  
        assertEquals(y1, 95.8333333, EPSILON); 
        double y2 = axis.java2DToValue(75.0, dataArea, RectangleEdge.RIGHT);   
        assertEquals(y2, 95.8333333, EPSILON); 
        double x1 = axis.java2DToValue(75.0, dataArea, RectangleEdge.TOP);   
        assertEquals(x1, 58.125, EPSILON); 
        double x2 = axis.java2DToValue(75.0, dataArea, RectangleEdge.BOTTOM);   
        assertEquals(x2, 58.125, EPSILON); 
        axis.setInverted(true);
        double y3 = axis.java2DToValue(75.0, dataArea, RectangleEdge.LEFT);  
        assertEquals(y3, 54.1666667, EPSILON); 
        double y4 = axis.java2DToValue(75.0, dataArea, RectangleEdge.RIGHT);   
        assertEquals(y4, 54.1666667, EPSILON); 
        double x3 = axis.java2DToValue(75.0, dataArea, RectangleEdge.TOP);   
        assertEquals(x3, 91.875, EPSILON); 
        double x4 = axis.java2DToValue(75.0, dataArea, RectangleEdge.BOTTOM);   
        assertEquals(x4, 91.875, EPSILON); 
        
    }
    
    /**
     * Serialize an instance, restore it, and check for equality.
     */
    public void testSerialization() {

        NumberAxis a1 = new NumberAxis("Test Axis");
        NumberAxis a2 = null;

        try {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            ObjectOutput out = new ObjectOutputStream(buffer);
            out.writeObject(a1);
            out.close();

            ObjectInput in = new ObjectInputStream(
                new ByteArrayInputStream(buffer.toByteArray())
            );
            a2 = (NumberAxis) in.readObject();
            in.close();
        }
        catch (Exception e) {
            System.out.println(e.toString());
        }
        assertEquals(a1, a2);

    }

    /**
     * A simple test for the auto-range calculation looking at a
     * NumberAxis used as the range axis for a CategoryPlot.
     */
    public void testAutoRange1() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        dataset.setValue(100.0, "Row 1", "Column 1");
        dataset.setValue(200.0, "Row 1", "Column 2");
        JFreeChart chart = ChartFactory.createBarChart(
            "Test", 
            "Categories",
            "Value",
            dataset,
            PlotOrientation.VERTICAL,
            false, 
            false,
            false
        );
        CategoryPlot plot = (CategoryPlot) chart.getPlot();
        NumberAxis axis = (NumberAxis) plot.getRangeAxis();
        assertEquals(axis.getLowerBound(), 0.0, EPSILON);    
        assertEquals(axis.getUpperBound(), 210.0, EPSILON);    
    }
    
    /**
     * A simple test for the auto-range calculation looking at a
     * NumberAxis used as the range axis for a CategoryPlot.  In this
     * case, the 'autoRangeIncludesZero' flag is set to false.
     */
    public void testAutoRange2() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        dataset.setValue(100.0, "Row 1", "Column 1");
        dataset.setValue(200.0, "Row 1", "Column 2");
        JFreeChart chart = ChartFactory.createBarChart(
            "Test", 
            "Categories",
            "Value",
            dataset,
            PlotOrientation.VERTICAL,
            false, 
            false,
            false
        );
        CategoryPlot plot = (CategoryPlot) chart.getPlot();
        NumberAxis axis = (NumberAxis) plot.getRangeAxis();
        axis.setAutoRangeIncludesZero(false);
        assertEquals(axis.getLowerBound(), 95.0, EPSILON);    
        assertEquals(axis.getUpperBound(), 205.0, EPSILON);    
    }
    
    /**
     * A simple test for the auto-range calculation looking at a
     * NumberAxis used as the range axis for a CategoryPlot.  In this
     * case, the 'autoRangeIncludesZero' flag is set to false AND the
     * original dataset is replaced with a new dataset.
     */
    public void testAutoRange3() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        dataset.setValue(100.0, "Row 1", "Column 1");
        dataset.setValue(200.0, "Row 1", "Column 2");
        JFreeChart chart = ChartFactory.createBarChart(
            "Test", 
            "Categories",
            "Value",
            dataset,
            PlotOrientation.VERTICAL,
            false, 
            false,
            false
        );
        CategoryPlot plot = (CategoryPlot) chart.getPlot();
        NumberAxis axis = (NumberAxis) plot.getRangeAxis();
        axis.setAutoRangeIncludesZero(false);
        assertEquals(axis.getLowerBound(), 95.0, EPSILON);    
        assertEquals(axis.getUpperBound(), 205.0, EPSILON);    
        
        // now replacing the dataset should update the axis range...
        DefaultCategoryDataset dataset2 = new DefaultCategoryDataset();
        dataset2.setValue(900.0, "Row 1", "Column 1");
        dataset2.setValue(1000.0, "Row 1", "Column 2");
        plot.setDataset(dataset2);
        assertEquals(axis.getLowerBound(), 895.0, EPSILON);    
        assertEquals(axis.getUpperBound(), 1005.0, EPSILON);    
    }
    
    /**
     * Checks that the auto-range for the domain axis on an XYPlot is
     * working as expected.
     */
    public void testXYAutoRange1() {
        XYSeries series = new XYSeries("Series 1");
        series.add(1.0, 1.0);
        series.add(2.0, 2.0);
        series.add(3.0, 3.0);
        XYSeriesCollection dataset = new XYSeriesCollection();
        dataset.addSeries(series);
        JFreeChart chart = ChartFactory.createScatterPlot(
            "Test", 
            "X",
            "Y",
            dataset,
            PlotOrientation.VERTICAL,
            false, 
            false,
            false
        );
        XYPlot plot = (XYPlot) chart.getPlot();
        NumberAxis axis = (NumberAxis) plot.getDomainAxis();
        axis.setAutoRangeIncludesZero(false);
        assertEquals(0.9, axis.getLowerBound(), EPSILON);    
        assertEquals(3.1, axis.getUpperBound(), EPSILON);    
    }
    
    /**
     * Checks that the auto-range for the range axis on an XYPlot is
     * working as expected.
     */
    public void testXYAutoRange2() {
        XYSeries series = new XYSeries("Series 1");
        series.add(1.0, 1.0);
        series.add(2.0, 2.0);
        series.add(3.0, 3.0);
        XYSeriesCollection dataset = new XYSeriesCollection();
        dataset.addSeries(series);
        JFreeChart chart = ChartFactory.createScatterPlot(
            "Test", 
            "X",
            "Y",
            dataset,
            PlotOrientation.VERTICAL,
            false, 
            false,
            false
        );
        XYPlot plot = (XYPlot) chart.getPlot();
        NumberAxis axis = (NumberAxis) plot.getRangeAxis();
        axis.setAutoRangeIncludesZero(false);
        assertEquals(0.9, axis.getLowerBound(), EPSILON);    
        assertEquals(3.1, axis.getUpperBound(), EPSILON);    
    }

}
