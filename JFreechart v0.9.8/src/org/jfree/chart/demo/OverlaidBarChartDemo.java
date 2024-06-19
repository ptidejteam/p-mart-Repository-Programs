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
 * -------------------------
 * OverlaidBarChartDemo.java
 * -------------------------
 * (C) Copyright 2002, 2003, by Simba Management Limited.
 *
 * Original Author:  David Gilbert (for Simba Management Limited);
 * Contributor(s):   -;
 *
 * $Id: OverlaidBarChartDemo.java,v 1.1 2007/10/10 20:03:17 vauchers Exp $
 *
 * Changes
 * -------
 * 20-Sep-2002 : Version 1 (DG);
 * 11-Oct-2002 : Added tooltips, modified series colors, centered frame on screen (DG);
 * 11-Feb-2003 : Fixed bug where category labels were not showing on the axis (DG);
 *
 */

package org.jfree.chart.demo;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.HorizontalCategoryAxis;
import org.jfree.chart.plot.OverlaidVerticalCategoryPlot;
import org.jfree.chart.plot.VerticalCategoryPlot;
import org.jfree.chart.renderer.CategoryItemRenderer;
import org.jfree.chart.renderer.LineAndShapeRenderer;
import org.jfree.chart.renderer.VerticalBarRenderer;
import org.jfree.chart.tooltips.CategoryToolTipGenerator;
import org.jfree.chart.tooltips.StandardCategoryToolTipGenerator;
import org.jfree.data.CategoryDataset;
import org.jfree.data.DatasetUtilities;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;

/**
 * A simple demonstration application showing how to create a vertical bar chart overlaid
 * with a line chart.
 *
 * @author David Gilbert
 */
public class OverlaidBarChartDemo extends ApplicationFrame {

    /**
     * Default constructor.
     *
     * @param  title the frame title.
     */
    public OverlaidBarChartDemo(String title) {

        super(title);

        // create the first dataset...
        double[][] data1 = new double[][] {
            { 1.0, 4.0, 3.0, 5.0, 5.0, 7.0, 7.0, 8.0 },
            { 5.0, 7.0, 6.0, 8.0, 4.0, 4.0, 2.0, 1.0 }
        };
		CategoryDataset dataset1 = DatasetUtilities.createCategoryDataset("S", 
                                                                          "Category ", 
                                                                          data1);

        // create the first plot...
        CategoryToolTipGenerator tooltips = new StandardCategoryToolTipGenerator();
        CategoryItemRenderer renderer = new VerticalBarRenderer(tooltips, null);
        VerticalCategoryPlot plot1 = new VerticalCategoryPlot(dataset1, null, null, renderer);

        // create the second dataset...
        double[][] data2 = new double[][] {
            { 9.0, 7.0, 2.0, 6.0, 6.0, 9.0, 5.0, 4.0 }
        };
		CategoryDataset dataset2 = DatasetUtilities.createCategoryDataset("T", "Category", data2);

        // create the second plot...
        CategoryItemRenderer renderer2 = new LineAndShapeRenderer();
        VerticalCategoryPlot plot2 = new VerticalCategoryPlot(dataset2, null, null, renderer2);

        // create the overlaid plot...
        OverlaidVerticalCategoryPlot plot  = new OverlaidVerticalCategoryPlot("Category", 
                                                                              "Value"); 
        plot.add(plot1);
        plot.add(plot2);
        plot.setRangeGridlinesVisible(true);
        plot.setDomainGridlinesVisible(true);
        
        HorizontalCategoryAxis axis = (HorizontalCategoryAxis) plot.getDomainAxis();
        axis.setVerticalCategoryLabels(true);
       
        JFreeChart chart = new JFreeChart(plot);
        chart.setTitle("Overlaid Bar Chart");

        // add the chart to a panel...
        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new java.awt.Dimension(500, 270));
        setContentPane(chartPanel);

    }

    /**
     * Starting point for the demonstration application.
     *
     * @param args  ignored.
     */
    public static void main(String[] args) {

        OverlaidBarChartDemo demo = new OverlaidBarChartDemo("Overlaid Bar Chart Demo");
        demo.pack();
        RefineryUtilities.centerFrameOnScreen(demo);
        demo.setVisible(true);

    }

}
