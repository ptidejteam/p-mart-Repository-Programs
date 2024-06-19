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
 * ----------------
 * MarkerDemo1.java
 * ----------------
 * (C) Copyright 2003, by Object Refinery Limited and Contributors.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited);
 * Contributor(s):   -;
 *
 * $Id: MarkerDemo1.java,v 1.1 2007/10/10 19:15:25 vauchers Exp $
 *
 * Changes
 * -------
 * 21-May-2003 : Version 1 (DG);
 *
 */

package org.jfree.chart.demo;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.Legend;
import org.jfree.chart.Marker;
import org.jfree.chart.MarkerLabelPosition;
import org.jfree.chart.annotations.XYAnnotation;
import org.jfree.chart.annotations.XYDrawableAnnotation;
import org.jfree.chart.annotations.XYPointerAnnotation;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.labels.TimeSeriesToolTipGenerator;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.XYDataset;
import org.jfree.data.time.Day;
import org.jfree.data.time.Hour;
import org.jfree.data.time.Minute;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;
import org.jfree.ui.TextAnchor;

/**
 * A demo application.
 *
 * @author David Gilbert
 */
public class MarkerDemo1 extends ApplicationFrame {

    /**
     * Creates a new instance.
     *
     * @param title  the frame title.
     */
    public MarkerDemo1(String title) {

        super(title);
        XYDataset data = createDataset();
        JFreeChart chart = createChart(data);
        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new java.awt.Dimension(500, 270));
        chartPanel.setVerticalZoom(true);
        chartPanel.setHorizontalZoom(true);
        setContentPane(chartPanel);

    }

    /**
     * Creates a sample chart.
     *
     * @param data  the sample data.
     *
     * @return A configured chart.
     */
    private JFreeChart createChart(XYDataset data) {

        JFreeChart chart = ChartFactory.createScatterPlot(
            "Marker Demo 1",
            "X",
            "Y", 
            data, 
            PlotOrientation.VERTICAL,
            true, 
            true, 
            false
        );
        chart.getLegend().setAnchor(Legend.EAST);

        // customise...
        XYPlot plot = chart.getXYPlot();

        // need a time series tool tip generator...
        plot.getRenderer().setToolTipGenerator(new TimeSeriesToolTipGenerator());

        // set axis margins to allow space for marker labels...
        DateAxis domainAxis = new DateAxis("Time");
        domainAxis.setUpperMargin(0.50);
        plot.setDomainAxis(domainAxis);

        ValueAxis rangeAxis = plot.getRangeAxis();
        rangeAxis.setUpperMargin(0.30);
        rangeAxis.setLowerMargin(0.50);

        // add a labelled marker for the bid start price...
        Marker start = new Marker(200.0, Color.green);
        start.setLabel("Bid Start Price");
        start.setLabelPosition(MarkerLabelPosition.TOP_RIGHT);
        plot.addRangeMarker(start);

        // add a labelled marker for the target price...
        Marker target = new Marker(175.0, Color.red);
        target.setLabel("Target Price");
        target.setLabelPosition(MarkerLabelPosition.TOP_RIGHT);
        plot.addRangeMarker(target);

        // add a labelled marker for the original closing time...
        Hour hour = new Hour(2, new Day(22, 5, 2003));
        double millis = (double) hour.getFirstMillisecond();
        Marker originalEnd = new Marker(millis, Color.orange);
        originalEnd.setLabel("Original Close (02:00)");
        plot.addDomainMarker(originalEnd);

        // add a labelled marker for the current closing time...
        Minute min = new Minute(15, hour);
        millis = (double) min.getFirstMillisecond();
        Marker currentEnd = new Marker(millis, Color.red);
        currentEnd.setLabel("Close Date (02:15)");
        currentEnd.setLabelPosition(MarkerLabelPosition.TOP_RIGHT);
        plot.addDomainMarker(currentEnd);

        // label the best bid with an arrow and label...
        Hour h = new Hour(2, new Day(22, 5, 2003));
        Minute m = new Minute(10, h);
        millis = (double) m.getFirstMillisecond();
        CircleDrawer cd = new CircleDrawer(Color.red, new BasicStroke(1.0f), null);
        XYAnnotation bestBid = new XYDrawableAnnotation(millis, 163.0, 11, 11, cd);
        plot.addAnnotation(bestBid);
        XYPointerAnnotation pointer = new XYPointerAnnotation("Best Bid", millis, 163.0,
                                                              3.0 * Math.PI / 4.0);
        pointer.setBaseRadius(35.0);
        pointer.setTipRadius(10.0);
        pointer.setFont(new Font("SansSerif", Font.PLAIN, 9));
        pointer.setPaint(Color.blue);
        pointer.setTextAnchor(TextAnchor.HALF_ASCENT_RIGHT);
        plot.addAnnotation(pointer);

        return chart;

    }

    /**
     * Returns a sample dataset.
     *
     * @return A sample dataset.
     */
    private XYDataset createDataset() {

        TimeSeriesCollection result = new TimeSeriesCollection();
        result.addSeries(createSupplier1Bids());
        result.addSeries(createSupplier2Bids());
        return result;

    }

    /**
     * Returns a sample data series (for supplier 1).
     *
     * @return A sample data series.
     */
    private TimeSeries createSupplier1Bids() {

        Hour hour = new Hour(1, new Day(22, 5, 2003));

        TimeSeries series1 = new TimeSeries("Supplier 1", Minute.class);
        series1.add(new Minute(13, hour), 200.0);
        series1.add(new Minute(14, hour), 195.0);
        series1.add(new Minute(45, hour), 190.0);
        series1.add(new Minute(46, hour), 188.0);
        series1.add(new Minute(47, hour), 185.0);
        series1.add(new Minute(52, hour), 180.0);

        return series1;

    }

    /**
     * Returns a sample data series (for supplier 2).
     *
     * @return A sample data series.
     */
    private TimeSeries createSupplier2Bids() {

        Hour hour1 = new Hour(1, new Day(22, 5, 2003));
        Hour hour2 = (Hour) hour1.next();

        TimeSeries series2 = new TimeSeries("Supplier 2", Minute.class);
        series2.add(new Minute(25, hour1), 185.0);
        series2.add(new Minute(0, hour2), 175.0);
        series2.add(new Minute(5, hour2), 170.0);
        series2.add(new Minute(6, hour2), 168.0);
        series2.add(new Minute(9, hour2), 165.0);
        series2.add(new Minute(10, hour2), 163.0);

        return series2;

    }

    /**
     * Starting point for the demo application.
     *
     * @param args  ignored.
     */
    public static void main(String[] args) {

        MarkerDemo1 demo = new MarkerDemo1("Marker Demo 1");
        demo.pack();
        RefineryUtilities.centerFrameOnScreen(demo);
        demo.setVisible(true);

    }

}
