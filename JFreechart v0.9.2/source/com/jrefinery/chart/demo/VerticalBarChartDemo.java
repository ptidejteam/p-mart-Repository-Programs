/* ===============
 * JFreeChart Demo
 * ===============
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
 * -------------------------
 * VerticalBarChartDemo.java
 * -------------------------
 * (C) Copyright 2002, by Simba Management Limited and Contributors.
 *
 * Original Author:  David Gilbert (for Simba Management Limited);
 * Contributor(s):   -;
 *
 * $Id: VerticalBarChartDemo.java,v 1.1 2007/10/10 19:41:58 vauchers Exp $
 *
 * Changes
 * -------
 * 11-Jun-2002 : Version 1 (DG);
 * 25-Jun-2002 : Removed redundant imports (DG);
 *
 */

package com.jrefinery.chart.demo;

import com.jrefinery.data.CategoryDataset;
import com.jrefinery.data.DefaultCategoryDataset;
import com.jrefinery.chart.JFreeChart;
import com.jrefinery.chart.ChartFactory;
import com.jrefinery.chart.ChartPanel;
import com.jrefinery.chart.CategoryPlot;
import com.jrefinery.chart.HorizontalCategoryAxis;
import com.jrefinery.chart.VerticalLogarithmicAxis;
import com.jrefinery.ui.ApplicationFrame;
import java.awt.Paint;
import java.awt.Color;

/**
 * A simple demonstration application showing how to create a vertical bar chart.
 */
public class VerticalBarChartDemo extends ApplicationFrame {

    /** The data. */
    protected CategoryDataset data;

    /**
     * Default constructor.
     */
    public VerticalBarChartDemo(String title) {

        super(title);

        // create a dataset...
        //double[][] data = new double[][] {
        //    { 1.0, 4.0, 3.0, 5.0, 5.0, 7.0, 7.0, 8.0 },
        //    { 5.0, 7.0, 6.0, 8.0, 4.0, 4.0, 2.0, 1.0 },
        //    { 4.0, 3.0, 2.0, 3.0, 6.0, 3.0, 4.0, 3.0 }
        //};

        double[][] data = new double[][] {
            { 100.0, -100.0, 100.0, 100.0, 100.0, 100.0, 100.0, 100.0 },
            { 100.0, 100.0, 100.0, 100.0, 100.0, 100.0, 100.0, 100.0 },
            { 100.0, 100.0, 100.0, 100.0, 100.0, 100.0, 100.0, 100.0 }
        };
        DefaultCategoryDataset dataset = new DefaultCategoryDataset(data);

        // set the series names...
        dataset.setSeriesName(0, "First");
        dataset.setSeriesName(1, "Second");
        dataset.setSeriesName(2, "Third");

        // set the category names...
        String[] categories = new String[] { "Type 1", "Type 2", "Type 3", "Type 4",
                                             "Type 5", "Type 6", "Type 7", "Type 8"  };
        dataset.setCategories(categories);

        // create the chart...
        JFreeChart chart = ChartFactory.createVerticalBarChart(
                                                     "Vertical Bar Chart",  // chart title
                                                     "Category",            // domain axis label
                                                     "Value",               // range axis label
                                                     dataset,               // data
                                                     true                   // include legend
                                                 );

        // NOW DO SOME OPTIONAL CUSTOMISATION OF THE CHART...

        // set the background color for the chart...
        chart.setBackgroundPaint(Color.yellow);

        // get a reference to the plot for further customisation...
        CategoryPlot plot = chart.getCategoryPlot();

        // set the color for each series...
        plot.setSeriesPaint(new Paint[] { Color.green, Color.orange, Color.red });

        // change the category labels to vertical...
        HorizontalCategoryAxis domainAxis = (HorizontalCategoryAxis)plot.getDomainAxis();
        domainAxis.setVerticalCategoryLabels(true);
        VerticalLogarithmicAxis rangeAxis = new VerticalLogarithmicAxis("Log(value)");
        plot.setRangeAxis(rangeAxis);
        // OPTIONAL CUSTOMISATION COMPLETED.

        // add the chart to a panel...
        ChartPanel chartPanel = new ChartPanel(chart);
        this.setContentPane(chartPanel);

    }

    /**
     * Starting point for the demonstration application.
     */
    public static void main(String[] args) {

        VerticalBarChartDemo demo = new VerticalBarChartDemo("Vertical Bar Chart Demo");
        demo.pack();
        demo.setVisible(true);

    }

}