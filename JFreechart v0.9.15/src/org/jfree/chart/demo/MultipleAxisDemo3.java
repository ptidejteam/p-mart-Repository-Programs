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
 * ----------------------
 * MultipleAxisDemo3.java
 * ----------------------
 * (C) Copyright 2003 by Object Refinery Limited and Contributors.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited);
 * Contributor(s):   -;
 *
 * $Id: MultipleAxisDemo3.java,v 1.1 2007/10/10 19:21:51 vauchers Exp $
 *
 * Changes
 * -------
 * 16-Sep-2002 : Version 1 (DG);
 *
 */

package org.jfree.chart.demo;

import java.awt.Color;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.Spacer;
import org.jfree.chart.axis.AxisLocation;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.StandardXYItemRenderer;
import org.jfree.data.XYDataset;
import org.jfree.data.time.Minute;
import org.jfree.data.time.RegularTimePeriod;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;

/**
 * An example of....
 *
 * @author David Gilbert
 */
public class MultipleAxisDemo3 extends ApplicationFrame {

    /**
     * A demonstration application showing how to create a time series chart with muliple axes.
     *
     * @param title  the frame title.
     */
    public MultipleAxisDemo3(String title) {

        super(title);
        JFreeChart chart = createChart();
        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new java.awt.Dimension(600, 270));
        setContentPane(chartPanel);

    }

    /**
     * Creates the demo chart.
     * 
     * @return The chart.
     */
    private JFreeChart createChart() {

        XYDataset dataset1 = createDataset("Series 1", 100.0, new Minute(), 200);
        
        JFreeChart chart = ChartFactory.createTimeSeriesChart(
            "Multiple Axis Demo 2", 
            "Time of Day", 
            "Primary Range Axis",
            dataset1, 
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
        renderer.setPaint(Color.black);
       
        // DOMAIN AXIS 2
        NumberAxis xAxis2 = new NumberAxis("Domain Axis 2");
        xAxis2.setAutoRangeIncludesZero(false);
        plot.setSecondaryDomainAxis(0, xAxis2);
        plot.setSecondaryDomainAxisLocation(0, AxisLocation.BOTTOM_OR_LEFT);
       
        // DOMAIN AXIS 3
        NumberAxis xAxis3 = new NumberAxis("Domain Axis 3");
        xAxis2.setAutoRangeIncludesZero(false);
        plot.setSecondaryDomainAxis(1, xAxis3);
        plot.setSecondaryDomainAxisLocation(1, AxisLocation.BOTTOM_OR_LEFT);

        // RANGE AXIS 2
        NumberAxis yAxis2 = new NumberAxis("Range Axis 2");
        plot.setSecondaryRangeAxis(0, yAxis2);
        plot.setSecondaryRangeAxisLocation(0, AxisLocation.BOTTOM_OR_RIGHT);

        XYDataset dataset2 = createDataset("Series 2", 1000.0, new Minute(), 170);
        plot.setSecondaryDataset(0, dataset2);    
        plot.mapSecondaryDatasetToDomainAxis(0, new Integer(0));
        plot.mapSecondaryDatasetToRangeAxis(0, new Integer(0));    
        
        return chart;
        
    }
    
    // ****************************************************************************
    // * COMMERCIAL SUPPORT / JFREECHART DEVELOPER GUIDE                          *
    // * Please note that commercial support and documentation is available from: *
    // *                                                                          *
    // * http://www.object-refinery.com/jfreechart/support.html                   *
    // *                                                                          *
    // * This is not only a great service for developers, but is a VERY IMPORTANT *
    // * source of funding for the JFreeChart project.  Please support us so that *
    // * we can continue developing free software.                                *
    // ****************************************************************************

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
            period = period.next();
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

        MultipleAxisDemo3 demo = new MultipleAxisDemo3("Multiple Axis Demo 3");
        demo.pack();
        RefineryUtilities.centerFrameOnScreen(demo);
        demo.setVisible(true);

    }

}
