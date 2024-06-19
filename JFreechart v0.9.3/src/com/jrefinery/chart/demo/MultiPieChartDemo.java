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
 * ----------------------
 * MultiPieChartDemo.java
 * ----------------------
 * (C) Copyright 2002, by Simba Management Limited and Contributors.
 *
 * Original Author:  David Gilbert (for Simba Management Limited);
 * Contributor(s):   -;
 *
 * $Id: MultiPieChartDemo.java,v 1.1 2007/10/10 19:52:20 vauchers Exp $
 *
 * Changes
 * -------
 * 21-Aug-2002 : Version 1 (DG);
 *
 */

package com.jrefinery.chart.demo;

import com.jrefinery.data.CategoryDataset;
import com.jrefinery.data.DefaultCategoryDataset;
import com.jrefinery.chart.JFreeChart;
import com.jrefinery.chart.ChartFactory;
import com.jrefinery.chart.ChartPanel;
import com.jrefinery.chart.PiePlot;
import com.jrefinery.ui.ApplicationFrame;
import java.awt.Paint;
import java.awt.Color;

/**
 * A simple demonstration application showing how to create a chart consisting of multiple
 * pie charts.
 */
public class MultiPieChartDemo extends ApplicationFrame {

    /** The data. */
    protected CategoryDataset data;

    /**
     * Default constructor.
     */
    public MultiPieChartDemo(String title) {

        super(title);

        // create a dataset...
        double[][] data = new double[][] {
            { 3.0, 4.0, 3.0, 5.0 },
            { 5.0, 7.0, 6.0, 8.0 },
            { 5.0, 7.0, 3.0, 8.0 },
            { 2.0, 3.0, 2.0, 3.0 }
        };

        DefaultCategoryDataset dataset = new DefaultCategoryDataset(data);

        // set the series names...
        dataset.setSeriesName(0, "First");
        dataset.setSeriesName(1, "Second");
        dataset.setSeriesName(2, "Third");
        dataset.setSeriesName(3, "Fourth");

        // set the category names...
        String[] categories = new String[] { "Category 1", "Category 2", "Category 3",
                                             "Category 4"  };
        dataset.setCategories(categories);

        // create the chart...
        JFreeChart chart = ChartFactory.createPieChart("Multi Pie Chart",  // chart title
                                                       dataset,            // data
                                                       true                // include legend
                                                       );

        // NOW DO SOME OPTIONAL CUSTOMISATION OF THE CHART...

        // set the background color for the chart...
        chart.setBackgroundPaint(Color.yellow);

        // OPTIONAL CUSTOMISATION COMPLETED.

        // add the chart to a panel...
        ChartPanel chartPanel = new ChartPanel(chart);
        this.setContentPane(chartPanel);

    }

    /**
     * Starting point for the demonstration application.
     */
    public static void main(String[] args) {

        MultiPieChartDemo demo = new MultiPieChartDemo("Multi Pie Chart Demo");
        demo.pack();
        demo.setVisible(true);

    }

}
