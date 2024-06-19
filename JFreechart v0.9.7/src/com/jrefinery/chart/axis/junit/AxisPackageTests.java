/* ======================================
 * JFreeChart : a free Java chart library
 * ======================================
 *
 * Project Info:  http://www.object-refinery.com/jfreechart/index.html
 * Project Lead:  David Gilbert (david.gilbert@object-refinery.com);
 *
 * (C) Copyright 2000-2003, by Simba Management Limited and Contributors.
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
 * ---------------------
 * AxisPackageTests.java
 * ---------------------
 * (C) Copyright 2003, by Simba Management Limited.
 *
 * Original Author:  David Gilbert (for Simba Management Limited);
 * Contributor(s):   -;
 *
 * $Id: AxisPackageTests.java,v 1.1 2007/10/10 20:00:07 vauchers Exp $
 *
 * Changes:
 * --------
 * 26-Mar-2003 : Version 1 (DG);
 *
 */

package com.jrefinery.chart.axis.junit;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * A collection of tests for the com.jrefinery.chart.axis package.
 * <P>
 * These tests can be run using JUnit (http://www.junit.org).
 *
 * @author David Gilbert
 */
public class AxisPackageTests extends TestCase {

    /**
     * Returns a test suite to the JUnit test runner.
     *
     * @return the test suite.
     */
    public static Test suite() { 
        TestSuite suite = new TestSuite("com.jrefinery.chart.axis");
        suite.addTestSuite(DateTickUnitTests.class);
        suite.addTestSuite(HorizontalCategoryAxisTests.class);
        suite.addTestSuite(HorizontalCategoryAxis3DTests.class);
        suite.addTestSuite(HorizontalColorBarAxisTests.class);
        suite.addTestSuite(HorizontalDateAxisTests.class);
        suite.addTestSuite(HorizontalLogarithmicAxisTests.class);
        suite.addTestSuite(HorizontalLogarithmicColorBarAxisTests.class);
        suite.addTestSuite(HorizontalMarkerAxisBandTests.class);
        suite.addTestSuite(HorizontalNumberAxisTests.class);
        suite.addTestSuite(HorizontalNumberAxis3DTests.class);
        suite.addTestSuite(HorizontalSymbolicAxisTests.class);
        suite.addTestSuite(VerticalCategoryAxisTests.class);
        suite.addTestSuite(VerticalColorBarAxisTests.class);
        suite.addTestSuite(VerticalDateAxisTests.class);
        suite.addTestSuite(VerticalLogarithmicAxisTests.class);
        suite.addTestSuite(VerticalLogarithmicColorBarAxisTests.class);
        suite.addTestSuite(VerticalNumberAxisTests.class);
        suite.addTestSuite(VerticalNumberAxis3DTests.class);
        suite.addTestSuite(VerticalSymbolicAxisTests.class);
        return suite;
    }

    /**
     * Constructs the test suite.
     *
     * @param name  the suite name.
     */
    public AxisPackageTests(String name) {
        super(name);
    }

}

