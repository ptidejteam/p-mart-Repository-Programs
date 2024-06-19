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
 * ----------------------
 * ChartPackageTests.java
 * ----------------------
 * (C) Copyright 2002, 2003, by Object Refinery Limited.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited);
 * Contributor(s):   -;
 *
 * $Id: ChartPackageTests.java,v 1.1 2007/10/10 19:21:59 vauchers Exp $
 *
 * Changes:
 * --------
 * 11-Jun-2002 : Version 1 (DG);
 * 17-Oct-2002 : Fixed errors reported by Checkstyle (DG);
 *
 */

package org.jfree.chart.junit;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * A collection of tests for the com.jrefinery.chart package.
 * <P>
 * These tests can be run using JUnit (http://www.junit.org).
 *
 * @author David Gilbert
 */
public class ChartPackageTests extends TestCase {

    /**
     * Returns a test suite to the JUnit test runner.
     *
     * @return the test suite.
     */
    public static Test suite() {
        TestSuite suite = new TestSuite("org.jfree.chart");
        suite.addTestSuite(JFreeChartTests.class);
        suite.addTestSuite(PieChartTests.class);
        suite.addTestSuite(BarChartTests.class);
        suite.addTestSuite(BarChart3DTests.class);
        suite.addTestSuite(MarkerTests.class);
        suite.addTestSuite(ScatterPlotTests.class);
        suite.addTestSuite(SpacerTests.class);
        suite.addTestSuite(StackedHorizontalBarChartTests.class);
        suite.addTestSuite(StackedVerticalBarChartTests.class);
        return suite;
    }

}
