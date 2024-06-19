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
 * ---------------------
 * TimeSeriesDemo11.java
 * ---------------------
 * (C) Copyright 2003 by Object Refinery Limited and Contributors.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited);
 * Contributor(s):   -;
 *
 * $Id: TimeSeriesDemo11.java,v 1.1 2007/10/10 19:12:16 vauchers Exp $
 *
 * Changes
 * -------
 * 12-Aug-2003 : Version 1 (DG);
 *
 */

package org.jfree.chart.demo;

import java.awt.Color;
import java.awt.GridLayout;

import javax.swing.JPanel;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.Spacer;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.StandardXYItemRenderer;
import org.jfree.data.XYDataset;
import org.jfree.data.time.Day;
import org.jfree.data.time.RegularTimePeriod;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.date.SerialDate;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;

/**
 * An example of....
 *
 * @author David Gilbert
 */
public class TimeSeriesDemo11 extends ApplicationFrame {

    /**
     * A demonstration application showing how to...
     *
     * @param title  the frame title.
     */
    public TimeSeriesDemo11(String title) {

        super(title);
        JPanel panel = new JPanel(new GridLayout(2, 2));
        panel.setPreferredSize(new java.awt.Dimension(800, 600));
        
        Day today = new Day();
        XYDataset dataset = createDataset("Series 1", 100.0, today, 365);

        JFreeChart chart1 = createChart("Chart 1 : 1 Year", dataset);
        ChartPanel chartPanel1 = new ChartPanel(chart1);
        panel.add(chartPanel1);
        
        JFreeChart chart2 = createChart("Chart 2 : 6 Months", dataset);
        SerialDate t = today.getSerialDate();
        SerialDate t6m = SerialDate.addMonths(-6, t);
        Day sixMonthsAgo = new Day(t6m);
        DateAxis axis2 = (DateAxis) chart2.getXYPlot().getDomainAxis();
        axis2.setRange(sixMonthsAgo.getStart(), today.getEnd());
        ChartPanel chartPanel2 = new ChartPanel(chart2);
        panel.add(chartPanel2);
        
        JFreeChart chart3 = createChart("Chart 3 : 3 Months", dataset);
        SerialDate t3m = SerialDate.addMonths(-3, t);
        Day threeMonthsAgo = new Day(t3m);
        DateAxis axis3 = (DateAxis) chart3.getXYPlot().getDomainAxis();
        axis3.setRange(threeMonthsAgo.getStart(), today.getEnd());
        ChartPanel chartPanel3 = new ChartPanel(chart3);
        panel.add(chartPanel3);
        
        JFreeChart chart4 = createChart("Chart 4 : 1 Month", dataset);
        SerialDate t1m = SerialDate.addMonths(-1, t);
        Day oneMonthsAgo = new Day(t1m);
        DateAxis axis4 = (DateAxis) chart4.getXYPlot().getDomainAxis();
        axis4.setRange(oneMonthsAgo.getStart(), today.getEnd());
        ChartPanel chartPanel4 = new ChartPanel(chart4);
        panel.add(chartPanel4);
        
        setContentPane(panel);

    }

    /**
     * Creates the demo chart.
     * 
     * @param title  the title.
     * @param dataset  the dataset.
     * 
     * @return The chart.
     */
    private JFreeChart createChart(String title, XYDataset dataset) {

        
        JFreeChart chart = ChartFactory.createTimeSeriesChart(
            title, 
            "Date", 
            "Price",
            dataset, 
            true, 
            true, 
            false
        );

        chart.setBackgroundPaint(Color.white);
        XYPlot plot = chart.getXYPlot();
        plot.setOrientation(PlotOrientation.VERTICAL);
        plot.setBackgroundPaint(Color.lightGray);
        plot.setDomainGridlinePaint(Color.white);
        plot.setRangeGridlinePaint(Color.white);
        plot.setAxisOffset(new Spacer(Spacer.ABSOLUTE, 5.0, 5.0, 5.0, 5.0));
        
        StandardXYItemRenderer renderer = (StandardXYItemRenderer) plot.getRenderer();
        renderer.setPaint(Color.blue);
               
        return chart;
        
    }
    
    /**
     * Creates a sample dataset.
     * 
     * @param name  the dataset name.
     * @param base  the starting value.
     * @param start  the starting period.
     * @param count  the number of values to generate.
     *
     * @return The dataset.
     */
    private XYDataset createDataset(String name, double base, RegularTimePeriod start, int count) {

        TimeSeries series = new TimeSeries(name, start.getClass());
        RegularTimePeriod period = start;
        double value = base;
        for (int i = 0; i < count; i++) {
            series.add(period, value);    
            period = period.previous();
            value = value * (1 + (Math.random() - 0.495) / 10.0);
        }

        TimeSeriesCollection dataset = new TimeSeriesCollection();
        dataset.addSeries(series);

        return dataset;

    }

    /**
     * Starting point for the demonstration application.
     *
     * @param args  ignored.
     */
    public static void main(String[] args) {

        TimeSeriesDemo11 demo = new TimeSeriesDemo11("Time Series Demo 11");
        demo.pack();
        RefineryUtilities.centerFrameOnScreen(demo);
        demo.setVisible(true);

    }

}
