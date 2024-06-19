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
 * -------------------
 * LineChartDemo3.java
 * -------------------
 * (C) Copyright 2003, by Simba Management Limited and Contributors.
 *
 * Original Author:  David Gilbert (for Simba Management Limited);
 * Contributor(s):   -;
 *
 * $Id: LineChartDemo3.java,v 1.1 2007/10/10 20:00:13 vauchers Exp $
 *
 * Changes
 * -------
 * 27-Jan-2003 : Version 1 (DG);
 *
 */

package com.jrefinery.chart.demo;

import com.jrefinery.data.XYSeriesCollection;
import com.jrefinery.data.XYSeries;
import com.jrefinery.ui.ApplicationFrame;
import com.jrefinery.chart.JFreeChart;
import com.jrefinery.chart.ChartFactory;
import com.jrefinery.chart.ChartPanel;
import com.jrefinery.chart.axis.NumberAxis;
import com.jrefinery.chart.plot.XYPlot;
import com.jrefinery.chart.renderer.StandardXYItemRenderer;
import com.jrefinery.ui.RefineryUtilities;

/**
 * This line chart demo shows many series, each displaying a different shape.
 *
 * @author David Gilbert
 */
public class LineChartDemo3 extends ApplicationFrame {

    /**
     * Creates a new demo.
     *
     * @param title  the frame title.
     */
    public LineChartDemo3(String title) {

        super(title);

        // create a dataset...
        XYSeriesCollection dataset = new XYSeriesCollection();
        for (int i = 0; i < 10; i++) {
            XYSeries series = new XYSeries("S" + i);
            for (int j = 0; j < 10; j++) {
                series.add(j, Math.random() * 100);
            }
            dataset.addSeries(series);
        }

        // create the chart...
        JFreeChart chart = ChartFactory.createLineXYChart("Line Chart Demo 3",  // chart title
                                                          "X",                  // x axis label
                                                          "Y",                  // y axis label
                                                          dataset,              // data
                                                          true,                 // include legend
                                                          true,                 // tooltips
                                                          false                 // urls
                                                          );

        // NOW DO SOME OPTIONAL CUSTOMISATION OF THE CHART...

        // get a reference to the plot for further customisation...
        XYPlot plot = chart.getXYPlot();
        StandardXYItemRenderer renderer = (StandardXYItemRenderer) plot.getRenderer();
        renderer.setPlotShapes(true);
        renderer.setDefaultShapeFilled(true);

        // change the auto tick unit selection to integer units only...
        NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
        rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());

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

        LineChartDemo3 demo = new LineChartDemo3("Line Chart Demo 3");
        demo.pack();
        RefineryUtilities.centerFrameOnScreen(demo);
        demo.setVisible(true);

    }

}
