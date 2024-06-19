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
 * ------------------------------
 * ChartTooltipsPackageTests.java
 * ------------------------------
 * (C) Copyright 2003, by Object Refinery Limited.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited);
 * Contributor(s):   -;
 *
 * $Id: TooltipsPackageTests.java,v 1.1 2007/10/10 19:19:01 vauchers Exp $
 *
 * Changes:
 * --------
 * 21-Mar-2003 : Version 1 (DG);
 *
 */

package org.jfree.chart.labels.junit;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * A collection of tests for the <code>org.jfree.chart.tooltips</code> package.
 * <P>
 * These tests can be run using JUnit (http://www.junit.org).
 *
 * @author David Gilbert
 */
public class TooltipsPackageTests extends TestCase {

    /**
     * Returns a test suite to the JUnit test runner.
     *
     * @return the test suite.
     */
    public static Test suite() {
        TestSuite suite = new TestSuite("org.jfree.chart.tooltips");
        suite.addTestSuite(CustomXYToolTipGeneratorTests.class);
        suite.addTestSuite(HighLowToolTipGeneratorTests.class);
        suite.addTestSuite(IntervalCategoryToolTipGeneratorTests.class);
        suite.addTestSuite(StandardCategoryToolTipGeneratorTests.class);
        suite.addTestSuite(StandardContourToolTipGeneratorTests.class);
        suite.addTestSuite(StandardPieItemLabelGeneratorTests.class);
        suite.addTestSuite(StandardXYToolTipGeneratorTests.class);
        suite.addTestSuite(StandardXYZToolTipGeneratorTests.class);
        suite.addTestSuite(TimeSeriesToolTipGeneratorTests.class);
        return suite;
    }

    /**
     * Constructs the test suite.
     *
     * @param name  the suite name.
     */
    public TooltipsPackageTests(String name) {
        super(name);
    }

}
