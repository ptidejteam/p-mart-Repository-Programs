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
 * ---------------------------------
 * BoxAndWhiskerCalculatorTests.java
 * ---------------------------------
 * (C) Copyright 2003 by Object Refinery Limited and Contributors.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited);
 * Contributor(s):   -;
 *
 * $Id: BoxAndWhiskerCalculatorTests.java,v 1.1 2007/10/10 19:12:30 vauchers Exp $
 *
 * Changes
 * -------
 * 28-Aug-2003 : Version 1 (DG);
 *
 */

package org.jfree.data.statistics.junit;

import java.util.ArrayList;
import java.util.List;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.jfree.data.statistics.BoxAndWhiskerCalculator;

/**
 * Tests for the {@link BoxAndWhiskerCalculator} class.
 *
 * @author David Gilbert
 */
public class BoxAndWhiskerCalculatorTests extends TestCase {

    /**
     * Returns the tests as a test suite.
     *
     * @return the test suite.
     */
    public static Test suite() {
        return new TestSuite(BoxAndWhiskerCalculatorTests.class);
    }

    /**
     * Constructs a new set of tests.
     *
     * @param  name the name of the tests.
     */
    public BoxAndWhiskerCalculatorTests(String name) {
        super(name);
    }

    /**
     * Test the median calculation.
     */
    public void testMedian1() {
        List values = new ArrayList();
        values.add(new Double(1.0));
        double median = BoxAndWhiskerCalculator.calculateMedian(values);
        assertTrue(equal(1.0, median));
    }

    /**
     * Test the median calculation.
     */
    public void testMedian2() {
        List values = new ArrayList();
        values.add(new Double(1.0));
        values.add(new Double(2.0));
        double median = BoxAndWhiskerCalculator.calculateMedian(values);
        assertTrue(equal(1.5, median));
    }

    /**
     * Tests two doubles for "equality".
     * 
     * @param d1  the first number.
     * @param d2  the secound number.
     * 
     * @return A boolean.
     */
    private boolean equal(double d1, double d2) {
        return Math.abs(d1 - d2) < 0.0000000001;
    }
}
