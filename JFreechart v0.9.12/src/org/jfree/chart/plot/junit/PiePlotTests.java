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
 * -----------------
 * PiePlotTests.java
 * -----------------
 * (C) Copyright 2003 by Object Refinery Limited and Contributors.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited);
 * Contributor(s):   -;
 *
 * $Id: PiePlotTests.java,v 1.1 2007/10/10 19:12:26 vauchers Exp $
 *
 * Changes
 * -------
 * 18-Mar-2003 : Version 1 (DG);
 *
 */

package org.jfree.chart.plot.junit;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Stroke;
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

import org.jfree.chart.labels.StandardPieItemLabelGenerator;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.urls.StandardPieURLGenerator;
import org.jfree.data.DefaultPieDataset;
import org.jfree.util.Rotation;

/**
 * Tests for the {@link PiePlot} class.
 *
 * @author David Gilbert
 */
public class PiePlotTests extends TestCase {

    /**
     * Returns the tests as a test suite.
     *
     * @return the test suite.
     */
    public static Test suite() {
        return new TestSuite(PiePlotTests.class);
    }

    /**
     * Constructs a new set of tests.
     *
     * @param  name the name of the tests.
     */
    public PiePlotTests(String name) {
        super(name);
    }

    /**
     * Test the equals method.
     */
    public void testEquals() {
        PiePlot plot1 = new PiePlot();
        PiePlot plot2 = new PiePlot();
        DefaultPieDataset dataset = new DefaultPieDataset();
        dataset.setValue("Section 0", 15.5);
        dataset.setValue("Section 1", 15.5);
        dataset.setValue("Section 2", 15.5);
        plot1.setDataset(dataset);
        plot2.setDataset(dataset);
        assertTrue(plot1.equals(plot2));    
        
        // interiorGap...
        plot1.setInteriorGap(0.15);
        assertFalse(plot1.equals(plot2));
        plot2.setInteriorGap(0.15);
        assertTrue(plot1.equals(plot2));

        // circular
        plot1.setCircular(false);
        assertFalse(plot1.equals(plot2));
        plot2.setCircular(false);
        assertTrue(plot1.equals(plot2));
        
        // radius
        plot1.setRadius(0.85);
        assertFalse(plot1.equals(plot2));
        plot2.setRadius(0.85);
        assertTrue(plot1.equals(plot2));
        
        // startAngle
        plot1.setStartAngle(Math.PI);
        assertFalse(plot1.equals(plot2));
        plot2.setStartAngle(Math.PI);
        assertTrue(plot1.equals(plot2));
        
        // direction
        plot1.setDirection(Rotation.ANTICLOCKWISE);
        assertFalse(plot1.equals(plot2));
        plot2.setDirection(Rotation.ANTICLOCKWISE);
        assertTrue(plot1.equals(plot2));
        
        // sectionLabelType
        plot1.setSectionLabelType(PiePlot.NAME_AND_PERCENT_LABELS);
        assertFalse(plot1.equals(plot2));
        plot2.setSectionLabelType(PiePlot.NAME_AND_PERCENT_LABELS);
        assertTrue(plot1.equals(plot2));
        
        // sectionLabelFont
        plot1.setSectionLabelFont(new Font("Serif", Font.PLAIN, 18));
        assertFalse(plot1.equals(plot2));
        plot2.setSectionLabelFont(new Font("Serif", Font.PLAIN, 18));
        assertTrue(plot1.equals(plot2));
       
        // sectionLabelPaint
        plot1.setSectionLabelPaint(Color.red);
        assertFalse(plot1.equals(plot2));
        plot2.setSectionLabelPaint(Color.red);
        assertTrue(plot1.equals(plot2));
       
        // sectionLabelGap
        plot1.setSectionLabelGap(0.11);
        assertFalse(plot1.equals(plot2));
        plot2.setSectionLabelGap(0.11);
        assertTrue(plot1.equals(plot2));
        
        // explodePercentages
        plot1.setExplodePercent(2, 0.15);
        assertFalse(plot1.equals(plot2));
        plot2.setExplodePercent(2, 0.15);
        assertTrue(plot1.equals(plot2));

        // valueFormatter
        plot1.setValueFormat(new DecimalFormat("0.0"));
        assertFalse(plot1.equals(plot2));
        plot2.setValueFormat(new DecimalFormat("0.0"));
        assertTrue(plot1.equals(plot2));
        
        // percentFormatter
        plot1.setPercentFormat(new DecimalFormat("0.0%"));
        assertFalse(plot1.equals(plot2));
        plot2.setPercentFormat(new DecimalFormat("0.0%"));
        assertTrue(plot1.equals(plot2));

        // itemLabelGenerator
        plot1.setItemLabelGenerator(new StandardPieItemLabelGenerator(new DecimalFormat("0.0")));
        assertFalse(plot1.equals(plot2));
        plot2.setItemLabelGenerator(new StandardPieItemLabelGenerator(new DecimalFormat("0.0")));
        assertTrue(plot1.equals(plot2));

        // urlGenerator
        plot1.setURLGenerator(new StandardPieURLGenerator());
        assertFalse(plot1.equals(plot2));
        plot2.setURLGenerator(new StandardPieURLGenerator());
        assertTrue(plot1.equals(plot2));

        // showSeriesLabels
        plot1.setShowSeriesLabels(false);
        assertFalse(plot1.equals(plot2));
        plot2.setShowSeriesLabels(false);
        assertTrue(plot1.equals(plot2));

        // seriesLabelFont
        plot1.setSeriesLabelFont(new Font("Serif", Font.PLAIN, 18));
        assertFalse(plot1.equals(plot2));
        plot2.setSeriesLabelFont(new Font("Serif", Font.PLAIN, 18));
        assertTrue(plot1.equals(plot2));
       
        // sectionLabelPaint
        plot1.setSeriesLabelPaint(Color.red);
        assertFalse(plot1.equals(plot2));
        plot2.setSeriesLabelPaint(Color.red);
        assertTrue(plot1.equals(plot2));
        
        // sectionPaintList
        plot1.setSectionPaint(2, Color.red);      
        assertFalse(plot1.equals(plot2));
        plot2.setSectionPaint(2, Color.red);  
        assertTrue(plot1.equals(plot2));
        
        // sectionPaint
        plot1.setSectionPaint(Color.white);      
        assertFalse(plot1.equals(plot2));
        plot2.setSectionPaint(Color.white);     
        assertTrue(plot1.equals(plot2));
                
        // sectionOutlinePaint
        plot1.setSectionOutlinePaint(Color.white);      
        assertFalse(plot1.equals(plot2));
        plot2.setSectionOutlinePaint(Color.white);     
        assertTrue(plot1.equals(plot2));

        // sectionOutlinePaintList
        plot1.setSectionOutlinePaint(2, Color.red);      
        assertFalse(plot1.equals(plot2));
        plot2.setSectionOutlinePaint(2, Color.red);  
        assertTrue(plot1.equals(plot2));
        
        // sectionOutlineStroke
        Stroke stroke = new BasicStroke(2.5f);
        plot1.setSectionOutlineStroke(stroke);      
        assertFalse(plot1.equals(plot2));
        plot2.setSectionOutlineStroke(stroke);     
        assertTrue(plot1.equals(plot2));
        
        // outlineStrokeList
        plot1.setSectionOutlineStroke(2, stroke);      
        assertFalse(plot1.equals(plot2));
        plot2.setSectionOutlineStroke(2, stroke);  
        assertTrue(plot1.equals(plot2));
        
        // extractType
        plot1.setMinimumArcAngleToDraw(1.0);
        assertFalse(plot1.equals(plot2));
        plot2.setMinimumArcAngleToDraw(1.0);  
        assertTrue(plot1.equals(plot2));
    }

    /**
     * Confirm that cloning works.
     */
    public void testCloning() {
        PiePlot p1 = new PiePlot();
        PiePlot p2 = null;
        try {
            p2 = (PiePlot) p1.clone();
        }
        catch (CloneNotSupportedException e) {
            e.printStackTrace();
            System.err.println("PiePlotTests.testCloning: failed to clone.");
        }
        assertTrue(p1 != p2);
        assertTrue(p1.getClass() == p2.getClass());
        assertTrue(p1.equals(p2));
    }

    /**
     * Serialize an instance, restore it, and check for equality.
     */
    public void testSerialization() {

        PiePlot p1 = new PiePlot(null);
        PiePlot p2 = null;

        try {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            ObjectOutput out = new ObjectOutputStream(buffer);
            out.writeObject(p1);
            out.close();

            ObjectInput in = new ObjectInputStream(new ByteArrayInputStream(buffer.toByteArray()));
            p2 = (PiePlot) in.readObject();
            in.close();
        }
        catch (Exception e) {
            System.out.println(e.toString());
        }
        assertEquals(p1, p2);

    }

}
