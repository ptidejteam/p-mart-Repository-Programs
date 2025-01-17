/* ===========================================================
 * JFreeChart : a free chart library for the Java(tm) platform
 * ===========================================================
 *
 * (C) Copyright 2000-2004, by Object Refinery Limited and Contributors.
 *
 * Project Info:  http://www.jfree.org/jfreechart/index.html
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
 * [Java is a trademark or registered trademark of Sun Microsystems, Inc. 
 * in the United States and other countries.]
 *
 * ------------------------------
 * ChartRendererPackageTests.java
 * ------------------------------
 * (C) Copyright 2003, 2004, by Object Refinery Limited.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited);
 * Contributor(s):   -;
 *
 * $Id: RendererPackageTests.java,v 1.1 2007/10/10 19:29:15 vauchers Exp $
 *
 * Changes:
 * --------
 * 21-Mar-2003 : Version 1 (DG);
 * 22-Oct-2003 : Added BoxAndWhiskerRendererTests (DG);
 *
 */

package org.jfree.chart.renderer.junit;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * A collection of tests for the org.jfree.chart.renderer package.
 * <P>
 * These tests can be run using JUnit (http://www.junit.org).
 *
 * @author David Gilbert
 */
public class RendererPackageTests extends TestCase {

    /**
     * Returns a test suite to the JUnit test runner.
     *
     * @return the test suite.
     */
    public static Test suite() {
        TestSuite suite = new TestSuite("org.jfree.chart.renderer");
        suite.addTestSuite(AbstractRendererTests.class);
        suite.addTestSuite(AbstractCategoryItemRendererTests.class);
        suite.addTestSuite(AreaRendererTests.class);
        suite.addTestSuite(XYAreaRendererTests.class);
        suite.addTestSuite(BarRendererTests.class);
        suite.addTestSuite(BarRenderer3DTests.class);
        suite.addTestSuite(BoxAndWhiskerRendererTests.class);
        suite.addTestSuite(CandlestickRendererTests.class);
        suite.addTestSuite(ClusteredXYBarRendererTests.class);
        suite.addTestSuite(DefaultCategoryItemRendererTests.class);
        suite.addTestSuite(DefaultDrawingSupplierTests.class);
        suite.addTestSuite(GanttRendererTests.class);
        suite.addTestSuite(HighLowRendererTests.class);
        suite.addTestSuite(IntervalBarRendererTests.class);
        suite.addTestSuite(LayeredBarRendererTests.class);
        suite.addTestSuite(LineAndShapeRendererTests.class);
        suite.addTestSuite(MinMaxCategoryRendererTests.class);
        suite.addTestSuite(SignalRendererTests.class);
        suite.addTestSuite(StackedAreaRendererTests.class);
        suite.addTestSuite(StackedAreaXYRendererTests.class);
        suite.addTestSuite(StackedBarRendererTests.class);
        suite.addTestSuite(StackedBarRenderer3DTests.class);
        suite.addTestSuite(StandardXYItemRendererTests.class);
        suite.addTestSuite(StatisticalBarRendererTests.class);
        suite.addTestSuite(WaterfallBarRendererTests.class);
        suite.addTestSuite(WindItemRendererTests.class);
        suite.addTestSuite(XYBarRendererTests.class);
        suite.addTestSuite(XYBoxAndWhiskerRendererTests.class);
        suite.addTestSuite(XYBubbleRendererTests.class);
        suite.addTestSuite(XYDifferenceRendererTests.class);
        suite.addTestSuite(XYDotRendererTests.class);
        suite.addTestSuite(XYLineAndShapeRendererTests.class);
        suite.addTestSuite(XYStepRendererTests.class);
        suite.addTestSuite(XYStepAreaRendererTests.class);
        suite.addTestSuite(YIntervalRendererTests.class);
        return suite;
    }

    /**
     * Constructs the test suite.
     *
     * @param name  the suite name.
     */
    public RendererPackageTests(String name) {
        super(name);
    }

}
