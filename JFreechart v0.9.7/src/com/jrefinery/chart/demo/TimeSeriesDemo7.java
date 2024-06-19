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
 * --------------------
 * TimeSeriesDemo7.java
 * --------------------
 * (C) Copyright 2003, by Simba Management Limited and Contributors.
 *
 * Original Author:  David Gilbert (for Simba Management Limited);
 * Contributor(s):   -;
 *
 * $Id: TimeSeriesDemo7.java,v 1.1 2007/10/10 20:00:13 vauchers Exp $
 *
 * Changes
 * -------
 * 05-Feb-2003 : Version 1 (DG);
 *
 */

package com.jrefinery.chart.demo;

import com.jrefinery.chart.JFreeChart;
import com.jrefinery.chart.ChartFactory;
import com.jrefinery.chart.ChartPanel;
import com.jrefinery.chart.axis.ValueAxis;
import com.jrefinery.chart.plot.XYPlot;
import com.jrefinery.data.XYDataset;
import com.jrefinery.data.time.TimeSeriesCollection;
import com.jrefinery.ui.ApplicationFrame;
import com.jrefinery.ui.RefineryUtilities;

/**
 * A time series chart.
 *
 * @author David Gilbert
 */
public class TimeSeriesDemo7 extends ApplicationFrame {

    /**
     * A demonstration application showing how to create a simple time series chart.
     *
     * @param title  the frame title.
     */
    public TimeSeriesDemo7(String title) {

        super(title);

        // create a title...
        String chartTitle = "Time Series Demo";
        XYDataset dataset = new TimeSeriesCollection(DemoDatasetFactory.createEURTimeSeries());

        JFreeChart chart = ChartFactory.createTimeSeriesChart(chartTitle, "Date", "Value",
                                                              dataset, true, true, false);

        XYPlot plot = chart.getXYPlot();
        ValueAxis rangeAxis = plot.getRangeAxis();
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

        TimeSeriesDemo7 demo = new TimeSeriesDemo7("Time Series Demo 7");
        demo.pack();
        RefineryUtilities.centerFrameOnScreen(demo);
        demo.setVisible(true);

    }

}
