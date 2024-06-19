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
 * ---------------------------
 * MinMaxCategoryPlotDemo.java
 * ---------------------------
 * (C) Copyright 2002, 2003, by Object Refinery Limited and Contributors.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited);
 * Contributor(s):   -;
 *
 * $Id: MinMaxCategoryPlotDemo.java,v 1.1 2007/10/10 19:19:02 vauchers Exp $
 *
 * Changes
 * -------
 * 08-Aug-2002 : Demo for a renderer contributed by Tomer Peretz (DG);
 * 11-Oct-2002 : Fixed errors reported by Checkstyle (DG);
 *
 */

package org.jfree.chart.demo;

import java.awt.Color;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.MinMaxCategoryRenderer;
import org.jfree.data.DefaultCategoryDataset;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;

/**
 * A simple demonstration application showing how to create a min/max category plot.
 *
 * @author David Gilbert
 */
public class MinMaxCategoryPlotDemo extends ApplicationFrame {

    /**
     * Creates a new demo.
     *
     * @param title  the frame title.
     */
    public MinMaxCategoryPlotDemo(String title) {

        super(title);

        // create a dataset...
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        dataset.addValue(1.0, "First", "Category 1");
        dataset.addValue(4.0, "First", "Category 2");
        dataset.addValue(3.0, "First", "Category 3");
        dataset.addValue(5.0, "First", "Category 4");
        dataset.addValue(5.0, "First", "Category 5");
        dataset.addValue(7.0, "First", "Category 6");
        dataset.addValue(7.0, "First", "Category 7");
        dataset.addValue(8.0, "First", "Category 8");
        dataset.addValue(5.0, "Second", "Category 1");
        dataset.addValue(7.0, "Second", "Category 2");
        dataset.addValue(6.0, "Second", "Category 3");
        dataset.addValue(8.0, "Second", "Category 4");
        dataset.addValue(4.0, "Second", "Category 5");
        dataset.addValue(4.0, "Second", "Category 6");
        dataset.addValue(2.0, "Second", "Category 7");
        dataset.addValue(1.0, "Second", "Category 8");
        dataset.addValue(4.0, "Third", "Category 1");
        dataset.addValue(3.0, "Third", "Category 2");
        dataset.addValue(2.0, "Third", "Category 3");
        dataset.addValue(3.0, "Third", "Category 4");
        dataset.addValue(6.0, "Third", "Category 5");
        dataset.addValue(3.0, "Third", "Category 6");
        dataset.addValue(4.0, "Third", "Category 7");
        dataset.addValue(3.0, "Third", "Category 8");

        // create the chart...
        JFreeChart chart = ChartFactory.createBarChart(
            "Min/Max Category Plot",  // chart title
            "Category",               // domain axis label
            "Value",                  // range axis label
            dataset,                  // data
            PlotOrientation.VERTICAL,
            true,                     // include legend
            true,                     // tooltips
            false                     // urls
        );

        // NOW DO SOME OPTIONAL CUSTOMISATION OF THE CHART...

        // set the background color for the chart...
        chart.setBackgroundPaint(Color.yellow);

        // get a reference to the plot for further customisation...
        CategoryPlot plot = chart.getCategoryPlot();
        plot.setRenderer(new MinMaxCategoryRenderer());
        // OPTIONAL CUSTOMISATION COMPLETED.

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

        MinMaxCategoryPlotDemo demo = new MinMaxCategoryPlotDemo("Min/Max Category Chart Demo");
        demo.pack();
        RefineryUtilities.centerFrameOnScreen(demo);
        demo.setVisible(true);

    }

}
