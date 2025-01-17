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
 * -------------------
 * TimeSeriesDemo.java
 * -------------------
 * (C) Copyright 2002, by Simba Management Limited and Contributors.
 *
 * Original Author:  David Gilbert (for Simba Management Limited);
 * Contributor(s):   -;
 *
 * $Id: TimeSeriesDemo.java,v 1.1 2007/10/10 19:41:58 vauchers Exp $
 *
 * Changes
 * -------
 * 08-Apr-2002 : Version 1 (DG);
 * 25-Jun-2002 : Removed unnecessary import (DG);
 *
 */

package com.jrefinery.chart.demo;

import com.jrefinery.data.BasicTimeSeries;
import com.jrefinery.data.Quarter;
import com.jrefinery.data.TimeSeriesCollection;
import com.jrefinery.ui.ApplicationFrame;
import com.jrefinery.chart.JFreeChart;
import com.jrefinery.chart.ChartFactory;
import com.jrefinery.chart.ChartPanel;

/**
 * An example of a time series chart.
 */
public class TimeSeriesDemo extends ApplicationFrame {

    /** The time series. */
    protected BasicTimeSeries series;

    /**
     * A demonstration application showing a quarterly time series containing a null value.
     */
    public TimeSeriesDemo(String title) {

        super(title);
        this.series = new BasicTimeSeries("Quarterly Data", Quarter.class);
        this.series.add(new Quarter(1, 2001), 500.2);
        this.series.add(new Quarter(2, 2001), 694.1);
        this.series.add(new Quarter(3, 2001), 734.4);
        this.series.add(new Quarter(4, 2001), 453.2);
        this.series.add(new Quarter(1, 2002), 500.2);
        this.series.add(new Quarter(2, 2002), null);
        this.series.add(new Quarter(3, 2002), 734.4);
        this.series.add(new Quarter(4, 2002), 453.2);
        TimeSeriesCollection dataset = new TimeSeriesCollection(series);
        // create a title with Unicode characters (currency symbols in this case) to see if it works
        String chartTitle = "\u20A2\u20A2\u20A2\u20A3\u20A4\u20A5\u20A6\u20A7\u20A8\u20A9\u20AA";
        JFreeChart chart = ChartFactory.createTimeSeriesChart(chartTitle, "Time", "Value",
                                                              dataset, true);

        //chart.addTitle(new TextTitle(subtitle));
        chart.getXYPlot().addHorizontalLine(new Double(550));
        ChartPanel chartPanel = new ChartPanel(chart);
        this.setContentPane(chartPanel);

    }

    /**
     * Starting point for the demonstration application.
     */
    public static void main(String[] args) {

        TimeSeriesDemo demo = new TimeSeriesDemo("Time Series Demo 1");
        demo.pack();
        demo.setVisible(true);

    }

}
