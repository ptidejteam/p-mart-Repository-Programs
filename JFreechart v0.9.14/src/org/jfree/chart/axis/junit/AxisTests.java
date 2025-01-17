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
 * --------------
 * AxisTests.java
 * --------------
 * (C) Copyright 2003 by Object Refinery Limited and Contributors.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited);
 * Contributor(s):   -;
 *
 * $Id: AxisTests.java,v 1.1 2007/10/10 19:19:11 vauchers Exp $
 *
 * Changes
 * -------
 * 13-Aug-2003 : Version 1 (DG);
 *
 */

package org.jfree.chart.axis.junit;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Insets;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.jfree.chart.axis.CategoryAxis;

/**
 * Tests for the {@link Axis} class.
 *
 * @author David Gilbert
 */
public class AxisTests extends TestCase {

    /**
     * Returns the tests as a test suite.
     *
     * @return the test suite.
     */
    public static Test suite() {
        return new TestSuite(AxisTests.class);
    }

    /**
     * Constructs a new set of tests.
     *
     * @param  name the name of the tests.
     */
    public AxisTests(String name) {
        super(name);
    }

    /**
     * Confirm that cloning works.
     */
    public void testCloning() {
        CategoryAxis a1 = new CategoryAxis("Test");
        CategoryAxis a2 = null;
        try {
            a2 = (CategoryAxis) a1.clone();
        }
        catch (CloneNotSupportedException e) {
            System.err.println("CategoryAxisTests.testCloning: failed to clone.");
        }
        assertTrue(a1 != a2);
        assertTrue(a1.getClass() == a2.getClass());
        assertTrue(a1.equals(a2));
    }

    /**
     * Confirm that the equals method can distinguish all the required fields.
     */
    public void testEquals() {
        
        CategoryAxis a1 = new CategoryAxis("Test");
        CategoryAxis a2 = new CategoryAxis("Test");
        assertTrue(a1.equals(a2));
        
        // visible flag...
        a1.setVisible(false);
        assertFalse(a1.equals(a2));
        a2.setVisible(false);
        assertTrue(a1.equals(a2));
                
        // label...
        a1.setLabel("New Label");
        assertFalse(a1.equals(a2));
        a2.setLabel("New Label");
        assertTrue(a1.equals(a2));

        // label font...
        a1.setLabelFont(new Font("Dialog", Font.PLAIN, 8));
        assertFalse(a1.equals(a2));
        a2.setLabelFont(new Font("Dialog", Font.PLAIN, 8));
        assertTrue(a1.equals(a2));

        // label paint...
        a1.setLabelPaint(Color.blue);
        assertFalse(a1.equals(a2));
        a2.setLabelPaint(Color.blue);
        assertTrue(a1.equals(a2));

        // label insets...
        a1.setLabelInsets(new Insets(10, 10, 10, 10));
        assertFalse(a1.equals(a2));
        a2.setLabelInsets(new Insets(10, 10, 10, 10));
        assertTrue(a1.equals(a2));

        // label angle...
        a1.setLabelAngle(1.23);
        assertFalse(a1.equals(a2));
        a2.setLabelAngle(1.23);
        assertTrue(a1.equals(a2));

        // tick labels visible flag...
        a1.setTickLabelsVisible(false);
        assertFalse(a1.equals(a2));
        a2.setTickLabelsVisible(false);
        assertTrue(a1.equals(a2));
                
        // tick label font...
        a1.setTickLabelFont(new Font("Dialog", Font.PLAIN, 12));
        assertFalse(a1.equals(a2));
        a2.setTickLabelFont(new Font("Dialog", Font.PLAIN, 12));
        assertTrue(a1.equals(a2));

        // tick label paint...
        a1.setTickLabelPaint(Color.red);
        assertFalse(a1.equals(a2));
        a2.setTickLabelPaint(Color.red);
        assertTrue(a1.equals(a2));

        // tick label insets...
        a1.setTickLabelInsets(new Insets(10, 10, 10, 10));
        assertFalse(a1.equals(a2));
        a2.setTickLabelInsets(new Insets(10, 10, 10, 10));
        assertTrue(a1.equals(a2));

        // tick marks visible flag...
        a1.setTickMarksVisible(true);
        assertFalse(a1.equals(a2));
        a2.setTickMarksVisible(true);
        assertTrue(a1.equals(a2));
                
        // tick mark inside length...
        a1.setTickMarkInsideLength(1.23f);
        assertFalse(a1.equals(a2));
        a2.setTickMarkInsideLength(1.23f);
        assertTrue(a1.equals(a2));

        // tick mark outside length...
        a1.setTickMarkOutsideLength(1.23f);
        assertFalse(a1.equals(a2));
        a2.setTickMarkOutsideLength(1.23f);
        assertTrue(a1.equals(a2));

        // tick mark stroke...
        a1.setTickMarkStroke(new BasicStroke(2.0f));
        assertFalse(a1.equals(a2));
        a2.setTickMarkStroke(new BasicStroke(2.0f));
        assertTrue(a1.equals(a2));

        // tick mark paint...
        a1.setTickMarkPaint(Color.green);
        assertFalse(a1.equals(a2));
        a2.setTickMarkPaint(Color.green);
        assertTrue(a1.equals(a2));

        // tick mark outside length...
        a1.setFixedDimension(3.21f);
        assertFalse(a1.equals(a2));
        a2.setFixedDimension(3.21f);
        assertTrue(a1.equals(a2));

    }

}
