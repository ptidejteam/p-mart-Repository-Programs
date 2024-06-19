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
 * ------------------------------
 * ChartRendererPackageTests.java
 * ------------------------------
 * (C) Copyright 2003, by Simba Management Limited.
 *
 * Original Author:  David Gilbert (for Simba Management Limited);
 * Contributor(s):   -;
 *
 * $Id: RendererPackageTests.java,v 1.1 2007/10/10 20:00:10 vauchers Exp $
 *
 * Changes:
 * --------
 * 21-Mar-2003 : Version 1 (DG);
 *
 */

package com.jrefinery.chart.renderer.junit;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * A collection of tests for the com.jrefinery.chart.renderer package.
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
        TestSuite suite = new TestSuite("com.jrefinery.chart.renderer");
        suite.addTestSuite(AreaRendererTests.class);
        suite.addTestSuite(AreaXYRendererTests.class);
        suite.addTestSuite(CandlestickRendererTests.class);
        suite.addTestSuite(ClusteredXYBarRendererTests.class);
        suite.addTestSuite(DefaultDrawingSupplierTests.class);
        suite.addTestSuite(HighLowRendererTests.class);
        suite.addTestSuite(HorizontalBarRendererTests.class);
        suite.addTestSuite(HorizontalBarRenderer3DTests.class);
        suite.addTestSuite(HorizontalIntervalBarRendererTests.class);
        suite.addTestSuite(PaintTableTests.class);
        suite.addTestSuite(ReverseXYItemRendererTests.class);
        suite.addTestSuite(ShapeTableTests.class);
        suite.addTestSuite(SignalRendererTests.class);
        suite.addTestSuite(StackedAreaRendererTests.class);
        suite.addTestSuite(StackedHorizontalBarRendererTests.class);
        suite.addTestSuite(StackedVerticalBarRendererTests.class);
        suite.addTestSuite(StackedVerticalBarRenderer3DTests.class);        
        suite.addTestSuite(StandardXYItemRendererTests.class);
        suite.addTestSuite(StrokeTableTests.class);
        suite.addTestSuite(VerticalBarRendererTests.class);
        suite.addTestSuite(VerticalBarRenderer3DTests.class);
        suite.addTestSuite(VerticalIntervalBarRendererTests.class);
        suite.addTestSuite(VerticalStatisticalBarRendererTests.class);
        suite.addTestSuite(VerticalXYBarRendererTests.class);
        suite.addTestSuite(WindItemRendererTests.class);
        suite.addTestSuite(XYBubbleRendererTests.class);
        suite.addTestSuite(XYDotRendererTests.class);
        suite.addTestSuite(XYStepRendererTests.class);
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
