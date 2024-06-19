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
 * ------------------
 * PieChartTests.java
 * ------------------
 * (C) Copyright 2002, by Simba Management Limited.
 *
 * Original Author:  David Gilbert (for Simba Management Limited);
 * Contributor(s):   -;
 *
 * $Id: PieChartTests.java,v 1.1 2007/10/10 19:52:24 vauchers Exp $
 *
 * Changes:
 * --------
 * 11-Jun-2002 : Version 1 (DG);
 *
 */

package com.jrefinery.chart.junit;

import junit.framework.*;

import com.jrefinery.chart.JFreeChart;
import com.jrefinery.chart.ChartFactory;
import com.jrefinery.chart.event.ChartChangeEvent;
import com.jrefinery.chart.event.ChartChangeListener;
import com.jrefinery.data.DefaultPieDataset;

/**
 * Tests for a pie chart.
 */
public class PieChartTests extends TestCase {

    private JFreeChart pieChart;

    /**
     * Returns the tests as a test suite.
     */
    public static Test suite() {
        return new TestSuite(PieChartTests.class);
    }

    /**
     * Constructs a new set of tests.
     *
     * @param The name of the tests.
     */
    public PieChartTests(String name) {
        super(name);
    }

    /**
     * Common test setup.
     */
    protected void setUp() {

        this.pieChart = createPieChart();

    }

    /**
     * Using a regular pie chart, we replace the dataset with null.  Expect to receive notification
     * of a chart change event, and (of course) the dataset should be null.
     */
    public void testReplaceDatasetOnPieChart() {

        LocalListener l = new LocalListener();
        pieChart.addChangeListener(l);
        pieChart.getPlot().setDataset(null);
        this.assertEquals(true, l.flag);
        this.assertNull(pieChart.getPlot().getDataset());

    }

    private static JFreeChart createPieChart() {
        // create a dataset...
        DefaultPieDataset data = new DefaultPieDataset();
        data.setValue("Java", new Double(43.2));
        data.setValue("Visual Basic", new Double(0.0));
        data.setValue("C/C++", new Double(17.5));

        // create the chart...
        return ChartFactory.createPieChart("Pie Chart",  // chart title
                                               data,         // data
                                               true          // include legend
                                               );
    }

    static class LocalListener implements ChartChangeListener {
        boolean flag = false;
        public void chartChanged(ChartChangeEvent event) {
            flag = true;
        }
    }

    public static void main(String[] args) {
        // create a dataset...
        DefaultPieDataset data = new DefaultPieDataset();
        data.setValue("Java", new Double(43.2));
        data.setValue("Visual Basic", new Double(0.0));
        data.setValue("C/C++", new Double(17.5));

        // create the chart...
        JFreeChart chart = ChartFactory.createPieChart("Pie Chart",  // chart title
                                               data,         // data
                                               true          // include legend
                                               );
        chart.getPlot().setDataset(null);

    }

}
