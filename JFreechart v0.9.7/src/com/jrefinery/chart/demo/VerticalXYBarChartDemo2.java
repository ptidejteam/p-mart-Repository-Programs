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
 * ----------------------------
 * VerticalXYBarChartDemo2.java
 * ----------------------------
 * (C) Copyright 2003, by Simba Management Limited and Contributors.
 *
 * Original Author:  David Gilbert (for Simba Management Limited);
 * Contributor(s):   -;
 *
 * $Id: VerticalXYBarChartDemo2.java,v 1.1 2007/10/10 20:00:13 vauchers Exp $
 *
 * Changes
 * -------
 * 20-Jun-2002 : Version 1 (DG);
 * 02-Jul-2002 : Removed unnecessary imports (DG);
 * 24-Aug-2002 : Set preferred size for ChartPanel (DG);
 * 11-Oct-2002 : Fixed issues reported by Checkstyle (DG);
 *
 */

package com.jrefinery.chart.demo;

import com.jrefinery.chart.ChartFactory;
import com.jrefinery.chart.ChartPanel;
import com.jrefinery.chart.JFreeChart;
import com.jrefinery.chart.axis.HorizontalDateAxis;
import com.jrefinery.chart.plot.XYPlot;
import com.jrefinery.chart.renderer.ClusteredXYBarRenderer;
import com.jrefinery.data.time.Day;
import com.jrefinery.data.time.TimeSeries;
import com.jrefinery.data.time.TimeSeriesCollection;
import com.jrefinery.ui.ApplicationFrame;
import com.jrefinery.ui.RefineryUtilities;

/**
 * A simple demonstration application showing how to create a vertical bar chart.
 *
 * @author David Gilbert
 */
public class VerticalXYBarChartDemo2 extends ApplicationFrame {

    /**
     * Constructs the demo application.
     *
     * @param title  the frame title.
     */
    public VerticalXYBarChartDemo2(String title) {

        super(title);

        // create a dataset...
        TimeSeries series1 = new TimeSeries("Series 1", Day.class);
        series1.add(new Day(1, 1, 2003), 54.3);
        series1.add(new Day(2, 1, 2003), 20.3);
        series1.add(new Day(3, 1, 2003), 43.4);
        series1.add(new Day(4, 1, 2003), -12.0);
        
        TimeSeries series2 = new TimeSeries("Series 2", Day.class);
        series2.add(new Day(1, 1, 2003), 8.0);
        series2.add(new Day(2, 1, 2003), 16.0);
        series2.add(new Day(3, 1, 2003), 21.0);
        series2.add(new Day(4, 1, 2003), 5.0);
        
        TimeSeriesCollection data = new TimeSeriesCollection();
        data.setDomainIsPointsInTime(false);
        data.addSeries(series1);
        data.addSeries(series2);
        
        // create the chart...
        JFreeChart chart = ChartFactory.createVerticalXYBarChart(
                                                     "Sample",  // chart title
                                                     "X",       // domain axis label
                                                     "Y",       // range axis label
                                                     data,      // data
                                                     true,      // include legend
                                                     true,
                                                     false
                                                 );

        // NOW DO SOME OPTIONAL CUSTOMISATION OF THE CHART...
        XYPlot plot = chart.getXYPlot();
        plot.setDomainAxis(new HorizontalDateAxis("Date"));
        plot.setRenderer(new ClusteredXYBarRenderer());
        // OPTIONAL CUSTOMISATION COMPLETED.

        // add the chart to a panel...
        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new java.awt.Dimension(500, 300));
        setContentPane(chartPanel);

    }

    /**
     * Starting point for the demonstration application.
     *
     * @param args  ignored.
     */
    public static void main(String[] args) {

        VerticalXYBarChartDemo2 demo = new VerticalXYBarChartDemo2("Vertical XY Bar Chart Demo 2");
        demo.pack();
        RefineryUtilities.centerFrameOnScreen(demo);
        demo.setVisible(true);

    }

}
