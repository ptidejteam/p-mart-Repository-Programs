/* -----------------------
 * TimeSeriesBarDemo1.java
 * -----------------------
 * (C) Copyright 2006, 2009, by Object Refinery Limited.
 *
 */

package tutorial;

import java.awt.Color;
import java.awt.Dimension;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.time.Month;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;

/**
 * A simple demonstration application showing how to create a bar chart.
 */
public class TimeSeriesBarDemo2 extends ApplicationFrame {

    /**
     * Creates a new demo instance.
     *
     * @param title  the frame title.
     */
    public TimeSeriesBarDemo2(String title) {
        super(title);
        TimeSeries s1 = new TimeSeries("Sunshine Hours");
        s1.add(new Month(1, 2005), 56.4);
        s1.add(new Month(2, 2005), 72.0);
        s1.add(new Month(3, 2005), 79.5);
        s1.add(new Month(4, 2005), 146.9);
        s1.add(new Month(5, 2005), 216.6);
        s1.add(new Month(6, 2005), 190.7);
        s1.add(new Month(7, 2005), 178.7);
        s1.add(new Month(8, 2005), 210.6);
        s1.add(new Month(9, 2005), 150.6);
        s1.add(new Month(10, 2005), 81.1);
        s1.add(new Month(11, 2005), 90.9);
        s1.add(new Month(12, 2005), 57.0);
        TimeSeriesCollection dataset = new TimeSeriesCollection();
        dataset.addSeries(s1);
        JFreeChart chart = ChartFactory.createXYBarChart(
            "Sunshine Hours - England & Wales",         // chart title
            "Date",               // domain axis label
            true,
            "Hours",                  // range axis label
            dataset,                  // data
            PlotOrientation.VERTICAL, // orientation
            true,                     // include legend
            true,                     // tooltips?
            false                     // URLs?
        );

        /** Demo 2 */
        chart.setBackgroundPaint(Color.white);
        XYPlot plot = (XYPlot) chart.getPlot();
        plot.setBackgroundPaint(Color.lightGray);
        plot.setDomainGridlinePaint(Color.white);
        plot.setRangeGridlinePaint(Color.white);
        /** Demo 2 */

        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new Dimension(500, 270));
        setContentPane(chartPanel);
    }

    /**
     * Starting point for the demonstration application.
     *
     * @param args  ignored.
     */
    public static void main(String[] args) {
        TimeSeriesBarDemo2 demo = new TimeSeriesBarDemo2(
                "Time Series Bar Demo 2");
        demo.pack();
        RefineryUtilities.centerFrameOnScreen(demo);
        demo.setVisible(true);
    }

}
