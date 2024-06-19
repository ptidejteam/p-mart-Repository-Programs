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
 * ----------------------------------
 * StackedHorizontalBarChartDemo.java
 * ----------------------------------
 * (C) Copyright 2002, 2003, by Simba Management Limited and Contributors.
 *
 * Original Author:  David Gilbert (for Simba Management Limited);
 * Contributor(s):   -;
 *
 * $Id: StackedHorizontalBarChartDemo.java,v 1.1 2007/10/10 19:57:52 vauchers Exp $
 *
 * Changes
 * -------
 * 07-Nov-2002 : Version 1 (DG);
 *
 */

package com.jrefinery.chart.demo;

import com.jrefinery.chart.JFreeChart;
import com.jrefinery.chart.ChartFactory;
import com.jrefinery.chart.ChartPanel;
import com.jrefinery.chart.plot.CategoryPlot;
import com.jrefinery.data.CategoryDataset;
import com.jrefinery.ui.ApplicationFrame;
import com.jrefinery.ui.RefineryUtilities;

/**
 * A simple demonstration application showing how to create a stacked horizontal bar chart
 * using data from a {@link CategoryDataset}.
 *
 * @author David Gilbert
 */
public class StackedHorizontalBarChartDemo extends ApplicationFrame {

    /**
     * Creates a new demo.
     *
     * @param title  the frame title.
     */
    public StackedHorizontalBarChartDemo(String title) {

        super(title);
        CategoryDataset dataset = DemoDatasetFactory.createCategoryDataset();

        // create the chart...
        JFreeChart chart = ChartFactory.createStackedHorizontalBarChart(
                                                  "Stacked Horizontal Bar Chart Demo",
                                                  "Category",      // domain axis label
                                                  "Value",         // range axis label
                                                  dataset,         // data
                                                  true,            // include legend
                                                  true,            // tooltips
                                                  false            // urls
                                              );

        CategoryPlot plot = chart.getCategoryPlot();

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

        StackedHorizontalBarChartDemo demo
            = new StackedHorizontalBarChartDemo("Stacked Horizontal Bar Chart Demo");
        demo.pack();
        RefineryUtilities.centerFrameOnScreen(demo);
        demo.setVisible(true);

    }

}
