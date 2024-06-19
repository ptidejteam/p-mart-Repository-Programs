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
 * --------------------
 * TimeSeriesDemo6.java
 * --------------------
 * (C) Copyright 2002, 2003, by Object Refinery Limited and Contributors.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited);
 * Contributor(s):   -;
 *
 * $Id: TimeSeriesDemo6.java,v 1.1 2007/10/10 19:19:02 vauchers Exp $
 *
 * Changes
 * -------
 * 08-Apr-2002 : Version 1 (DG);
 * 25-Jun-2002 : Removed unnecessary import (DG);
 *
 */

package org.jfree.chart.demo;

import java.text.SimpleDateFormat;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.XYDataset;
import org.jfree.data.time.Month;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;

/**
 * A time series chart with all zero data.  When the data range is zero, you may want to modify
 * the default behaviour of the range axis.
 *
 * @author David Gilbert
 */
public class TimeSeriesDemo6 extends ApplicationFrame {

    /**
     * A demonstration application showing how to create a simple time series chart.  This
     * example uses monthly data.
     *
     * @param title  the frame title.
     */
    public TimeSeriesDemo6(String title) {

        super(title);

        // create a title...
        String chartTitle = "Time Series Demo 6";
        XYDataset dataset = createDataset();

        JFreeChart chart = ChartFactory.createTimeSeriesChart(chartTitle, "Date", "Value",
                                                              dataset, true, true, false);

        XYPlot plot = chart.getXYPlot();
        DateAxis axis = (DateAxis) plot.getDomainAxis();
        axis.setDateFormatOverride(new SimpleDateFormat("MMM-yyyy"));
        ValueAxis rangeAxis = plot.getRangeAxis();
        rangeAxis.setAutoRangeMinimumSize(1.0);
        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new java.awt.Dimension(500, 270));
        setContentPane(chartPanel);

    }

    /**
     * Creates a dataset, consisting of two series of monthly data.
     *
     * @return the dataset.
     */
    public XYDataset createDataset() {

        TimeSeries s1 = new TimeSeries("Series 1", Month.class);
        s1.add(new Month(2, 2001), 0.0);
        s1.add(new Month(3, 2001), 0.0);
        s1.add(new Month(4, 2001), 0.0);
        s1.add(new Month(5, 2001), 0.0);
        s1.add(new Month(6, 2001), 0.0);
        s1.add(new Month(7, 2001), 0.0);
        s1.add(new Month(8, 2001), 0.0);
        s1.add(new Month(9, 2001), 0.0);
        s1.add(new Month(10, 2001), 0.0);
        s1.add(new Month(11, 2001), 0.0);
        s1.add(new Month(12, 2001), 0.0);
        s1.add(new Month(1, 2002), 0.0);
        s1.add(new Month(2, 2002), 0.0);
        s1.add(new Month(3, 2002), 0.0);
        s1.add(new Month(4, 2002), 0.0);
        s1.add(new Month(5, 2002), 0.0);
        s1.add(new Month(6, 2002), 0.0);
        s1.add(new Month(7, 2002), 0.0);

        TimeSeriesCollection dataset = new TimeSeriesCollection();
        dataset.addSeries(s1);

        return dataset;

    }

    /**
     * Starting point for the demonstration application.
     *
     * @param args  ignored.
     */
    public static void main(String[] args) {

        TimeSeriesDemo6 demo = new TimeSeriesDemo6("Time Series Demo 6");
        demo.pack();
        RefineryUtilities.centerFrameOnScreen(demo);
        demo.setVisible(true);

    }

}
