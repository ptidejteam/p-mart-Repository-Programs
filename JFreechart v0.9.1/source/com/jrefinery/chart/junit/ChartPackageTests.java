/* =======================================
 * JFreeChart : a Java Chart Class Library
 * =======================================
 *
 * Project Info:  http://www.object-refinery.com/jfreechart/index.html
 * Project Lead:  David Gilbert (david.gilbert@object-refinery.com);
 *
 * (C) Copyright 2000-2002, by Simba Management Limited and Contributors.
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
 * ----------------------
 * ChartPackageTests.java
 * ----------------------
 * (C) Copyright 2002, by Simba Management Limited.
 *
 * Original Author:  David Gilbert (for Simba Management Limited);
 * Contributor(s):   -;
 *
 * $Id: ChartPackageTests.java,v 1.1 2007/10/10 19:02:28 vauchers Exp $
 *
 * Changes:
 * --------
 * 11-Jun-2002 : Version 1 (DG);
 *
 */

package com.jrefinery.chart.junit;

import junit.framework.*;

/**
 * An incomplete collection of tests for the com.jrefinery.chart package.
 * <P>
 * These tests can be run using JUnit (http://www.junit.org).
 */
public class ChartPackageTests extends TestCase {

    /**
     * Returns a test suite to the JUnit test runner.
     */
    public static Test suite() {
        TestSuite suite = new TestSuite("com.jrefinery.chart");
        suite.addTestSuite(JFreeChartTests.class);
        suite.addTestSuite(PieChartTests.class);
        suite.addTestSuite(HorizontalBarChartTests.class);
        suite.addTestSuite(HorizontalBarChart3DTests.class);
        suite.addTestSuite(ScatterPlotTests.class);
        suite.addTestSuite(StackedHorizontalBarChartTests.class);
        suite.addTestSuite(StackedVerticalBarChartTests.class);
        suite.addTestSuite(VerticalBarChartTests.class);
        suite.addTestSuite(VerticalBarChart3DTests.class);
        return suite;
    }

    /**
     * Constructs the test suite.
     */
    public ChartPackageTests(String name) {
        super(name);
    }

}