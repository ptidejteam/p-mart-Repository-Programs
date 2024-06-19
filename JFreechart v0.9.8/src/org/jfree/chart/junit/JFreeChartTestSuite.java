/* ======================================
 * JFreeChart : a free Java chart library
 * ======================================
 *
 * Project Info:  http://www.jfree.org/jfreechart/index.html
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
 * ------------------------
 * JFreeChartTestSuite.java
 * ------------------------
 * (C) Copyright 2002, 2003, by Simba Management Limited.
 *
 * Original Author:  David Gilbert (for Simba Management Limited);
 * Contributor(s):   -;
 *
 * $Id: JFreeChartTestSuite.java,v 1.1 2007/10/10 20:03:22 vauchers Exp $
 *
 * Changes:
 * --------
 * 11-Jun-2002 : Version 1 (DG);
 * 30-Sep-2002 : Added tests for com.jrefinery.data (DG);
 * 17-Oct-2002 : Fixed errors reported by Checkstyle (DG);
 * 13-Mar-2003 : Added tests for new com.jrefinery.data.time package (DG);
 *
 */

package org.jfree.chart.junit;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.jfree.chart.axis.junit.AxisPackageTests;
import org.jfree.chart.plot.junit.PlotPackageTests;
import org.jfree.chart.renderer.junit.RendererPackageTests;
import org.jfree.chart.urls.junit.UrlsPackageTests;
import org.jfree.data.junit.DataPackageTests;
import org.jfree.data.time.junit.DataTimePackageTests;

/**
 * A test suite for the JFreeChart class library that can be run using
 * JUnit (<code>http://www.junit.org<code>).
 *
 * @author David Gilbert
 */
public class JFreeChartTestSuite extends TestCase {

    /**
     * Returns a test suite to the JUnit test runner.
     *
     * @return the test suite.
     */
    public static Test suite() {
        TestSuite suite = new TestSuite("JFreeChart");
        suite.addTest(ChartPackageTests.suite());
        suite.addTest(AxisPackageTests.suite());
        suite.addTest(PlotPackageTests.suite());
        suite.addTest(RendererPackageTests.suite());
        // sv Test suite was removed since it refers to an incorrect class
//        suite.addTest(TooltipsPackageTests.suite());
        suite.addTest(UrlsPackageTests.suite());
        suite.addTest(DataPackageTests.suite());
        suite.addTest(DataTimePackageTests.suite());
        return suite;
    }

    /**
     * Constructs the test suite.
     *
     * @param name  the test suite name.
     */
    public JFreeChartTestSuite(String name) {
        super(name);
    }

}
