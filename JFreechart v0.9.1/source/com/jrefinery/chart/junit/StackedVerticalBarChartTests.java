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
 * ---------------------------------
 * StackedVerticalBarChartTests.java
 * ---------------------------------
 * (C) Copyright 2002, by Simba Management Limited.
 *
 * Original Author:  David Gilbert (for Simba Management Limited);
 * Contributor(s):   -;
 *
 * $Id: StackedVerticalBarChartTests.java,v 1.1 2007/10/10 19:02:28 vauchers Exp $
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
import com.jrefinery.chart.ValueAxis;
import com.jrefinery.chart.event.ChartChangeEvent;
import com.jrefinery.chart.event.ChartChangeListener;
import com.jrefinery.data.CategoryDataset;
import com.jrefinery.data.DefaultCategoryDataset;
import com.jrefinery.data.DefaultPieDataset;
import com.jrefinery.data.Range;

import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

/**
 * Tests for a stacked vertical bar chart.
 */
public class StackedVerticalBarChartTests extends TestCase {

    private JFreeChart chart;

    /**
     * Returns the tests as a test suite.
     */
    public static Test suite() {
        return new TestSuite(StackedVerticalBarChartTests.class);
    }

    /**
     * Constructs a new set of tests.
     *
     * @param The name of the tests.
     */
    public StackedVerticalBarChartTests(String name) {
        super(name);
    }

    /**
     * Common test setup.
     */
    protected void setUp() {

        this.chart = createChart();

    }

    /**
     * Draws the chart with a null info object to make sure that no exceptions are thrown (a
     * problem that was occurring at one point).
     */
    public void testDrawWithNullInfo() {

        boolean success = false;
        try {

            BufferedImage image = new BufferedImage(200 , 100, BufferedImage.TYPE_INT_RGB);
            Graphics2D g2 = image.createGraphics();
            chart.draw(g2, new Rectangle2D.Double(0, 0, 200, 100), null);
            g2.dispose();
            success = true;
        }
        catch (Exception e) {
          success = false;
        }

        this.assertTrue(success);

    }

    public void testReplaceDataset() {

        // create a dataset...
        Number[][] data = new Integer[][]
            { { new Integer(-30), new Integer(-20) },
              { new Integer(-10), new Integer(10) },
              { new Integer(20), new Integer(30) }
            };

        CategoryDataset newData = new DefaultCategoryDataset(data);

        LocalListener l = new LocalListener();
        chart.addChangeListener(l);
        chart.getPlot().setDataset(newData);
        this.assertEquals(true, l.flag);
        ValueAxis axis = chart.getCategoryPlot().getRangeAxis();
        Range range = axis.getRange();
        this.assertTrue("Expecting the lower bound of the range to be around -30."+
                        range.getLowerBound(),
                        range.getLowerBound()<=-30);
        this.assertTrue("Expecting the upper bound of the range to be around 30:"+
                        range.getUpperBound(),
                        range.getUpperBound()>=30);

    }

    /**
     * Create a horizontal bar chart with sample data in the range -3 to +3.
     */
    private static JFreeChart createChart() {

        // create a dataset...
        Number[][] data = new Integer[][]
            { { new Integer(-3), new Integer(-2) },
              { new Integer(-1), new Integer(1) },
              { new Integer(2), new Integer(3) }
            };

        CategoryDataset dataset = new DefaultCategoryDataset(data);

        // create the chart...
        return ChartFactory.createStackedVerticalBarChart(
                                                "Stacked Vertical Bar Chart",  // chart title
                                                "Domain", "Range",
                                                dataset,         // data
                                                true          // include legend
                                            );

    }

    static class LocalListener implements ChartChangeListener {
        boolean flag = false;
        public void chartChanged(ChartChangeEvent event) {
            flag = true;
        }
    }

}