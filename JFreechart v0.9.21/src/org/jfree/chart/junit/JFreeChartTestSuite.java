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
 * ------------------------
 * JFreeChartTestSuite.java
 * ------------------------
 * (C) Copyright 2002-2004, by Object Refinery Limited.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited);
 * Contributor(s):   -;
 *
 * $Id: JFreeChartTestSuite.java,v 1.1 2007/10/10 19:50:20 vauchers Exp $
 *
 * Changes:
 * --------
 * 11-Jun-2002 : Version 1 (DG);
 * 30-Sep-2002 : Added tests for com.jrefinery.data (DG);
 * 17-Oct-2002 : Fixed errors reported by Checkstyle (DG);
 * 13-Mar-2003 : Added tests for new com.jrefinery.data.time package (DG);
 * 17-Feb-2004 : Added tests for org.jfree.chart.title package (DG);
 * 20-May-2004 : Added tests for org.jfree.chart.entity package (DG);
 * 30-Jul-2004 : Added tests for org.jfree.data.gantt package (DG);
 * 23-Aug-2004 : Restructured org.jfree.data (DG);
 *
 */

package org.jfree.chart.junit;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.jfree.chart.annotations.junit.AnnotationsPackageTests;
import org.jfree.chart.axis.junit.AxisPackageTests;
import org.jfree.chart.entity.junit.EntityPackageTests;
import org.jfree.chart.labels.junit.LabelsPackageTests;
import org.jfree.chart.plot.junit.PlotPackageTests;
import org.jfree.chart.renderer.category.junit.RendererCategoryPackageTests;
import org.jfree.chart.renderer.junit.RendererPackageTests;
import org.jfree.chart.renderer.xy.junit.RendererXYPackageTests;
import org.jfree.chart.title.junit.TitlePackageTests;
import org.jfree.chart.ui.junit.ChartUIPackageTests;
import org.jfree.chart.urls.junit.UrlsPackageTests;
import org.jfree.data.category.junit.DataCategoryPackageTests;
import org.jfree.data.gantt.junit.DataGanttPackageTests;
import org.jfree.data.junit.DataPackageTests;
import org.jfree.data.statistics.junit.DataStatisticsPackageTests;
import org.jfree.data.time.junit.DataTimePackageTests;
import org.jfree.data.xy.junit.DataXYPackageTests;

/**
 * A test suite for the JFreeChart class library that can be run using
 * JUnit (<code>http://www.junit.org<code>).
 */
public class JFreeChartTestSuite extends TestCase {

    /**
     * Returns a test suite to the JUnit test runner.
     *
     * @return The test suite.
     */
    public static Test suite() {
        TestSuite suite = new TestSuite("JFreeChart");
        suite.addTest(ChartPackageTests.suite());
        suite.addTest(AnnotationsPackageTests.suite());
        suite.addTest(AxisPackageTests.suite());
        suite.addTest(EntityPackageTests.suite());
        suite.addTest(LabelsPackageTests.suite());
        suite.addTest(PlotPackageTests.suite());
        suite.addTest(RendererPackageTests.suite());
        suite.addTest(RendererCategoryPackageTests.suite());
        suite.addTest(RendererXYPackageTests.suite());
        suite.addTest(TitlePackageTests.suite());
        suite.addTest(ChartUIPackageTests.suite());
        suite.addTest(UrlsPackageTests.suite());
        suite.addTest(DataPackageTests.suite());
        suite.addTest(DataCategoryPackageTests.suite());
        suite.addTest(DataStatisticsPackageTests.suite());
        suite.addTest(DataTimePackageTests.suite());
        suite.addTest(DataXYPackageTests.suite());
        suite.addTest(DataGanttPackageTests.suite());
        return suite;
    }

}
