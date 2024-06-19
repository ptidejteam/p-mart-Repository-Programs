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
 * ---------------------------
 * VerticalXYBarChartDemo.java
 * ---------------------------
 * (C) Copyright 2002, by Simba Management Limited and Contributors.
 *
 * Original Author:  David Gilbert (for Simba Management Limited);
 * Contributor(s):   -;
 *
 * $Id: VerticalXYBarChartDemo.java,v 1.1 2007/10/10 19:41:58 vauchers Exp $
 *
 * Changes
 * -------
 * 20-Jun-2002 : Version 1 (DG);
 *
 */

package com.jrefinery.chart.demo;

import com.jrefinery.data.IntervalXYDataset;
import com.jrefinery.ui.ApplicationFrame;
import com.jrefinery.chart.JFreeChart;
import com.jrefinery.chart.ChartFactory;
import com.jrefinery.chart.ChartPanel;
import com.jrefinery.chart.XYPlot;
import com.jrefinery.chart.HorizontalNumberAxis;

import java.awt.Paint;
import java.awt.Color;
import java.awt.Stroke;
import java.awt.BasicStroke;

/**
 * A simple demonstration application showing how to create a vertical bar chart.
 */
public class VerticalXYBarChartDemo extends ApplicationFrame {

    /** The data. */
    protected IntervalXYDataset data;

    /**
     * Default constructor.
     */
    public VerticalXYBarChartDemo(String title) {

        super(title);

        // create a dataset...
        IntervalXYDataset dataset = new SimpleIntervalXYDataset();

        // create the chart...
        JFreeChart chart = ChartFactory.createVerticalXYBarChart(
                                                     "Sample",  // chart title
                                                     "X",            // domain axis label
                                                     "Y",               // range axis label
                                                     dataset,               // data
                                                     true                   // include legend
                                                 );

        // NOW DO SOME OPTIONAL CUSTOMISATION OF THE CHART...
        XYPlot plot = chart.getXYPlot();
        plot.setDomainAxis(new HorizontalNumberAxis("X"));
        // OPTIONAL CUSTOMISATION COMPLETED.

        // add the chart to a panel...
        ChartPanel chartPanel = new ChartPanel(chart);
        this.setContentPane(chartPanel);

    }

    /**
     * Starting point for the demonstration application.
     */
    public static void main(String[] args) {

        VerticalXYBarChartDemo demo = new VerticalXYBarChartDemo("Vertical XY Bar Chart Demo");
        demo.pack();
        demo.setVisible(true);

    }

}