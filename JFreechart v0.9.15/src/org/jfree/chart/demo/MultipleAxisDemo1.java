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
 * MultipleAxisDemo1.java
 * ----------------------
 * (C) Copyright 2003 by Object Refinery Limited and Contributors.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited);
 * Contributor(s):   -;
 *
 * $Id: MultipleAxisDemo1.java,v 1.1 2007/10/10 19:21:51 vauchers Exp $
 *
 * Changes
 * -------
 * 15-Jul-2002 : Version 1 (DG);
 *
 */

package org.jfree.chart.demo;

import java.awt.Color;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.Spacer;
import org.jfree.chart.TextTitle;
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
public class MultipleAxisDemo1 extends ApplicationFrame {

    /**
     * A demonstration application showing how to create a time series chart with muliple axes.
     *
     * @param title  the frame title.
     */
    public MultipleAxisDemo1(String title) {

        super(title);
        JFreeChart chart = createChart();
        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new java.awt.Dimension(600, 270));
        chartPanel.setHorizontalZoom(true);
        chartPanel.setVerticalZoom(true);
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
            "Multiple Axis Demo 1", 
            "Time of Day", 
            "Primary Range Axis",
            dataset1, 
            true, 
            true, 
            false
        );

        chart.setBackgroundPaint(Color.white);
        chart.addSubtitle(new TextTitle("Four datasets and four range axes."));  
        XYPlot plot = chart.getXYPlot();
        plot.setOrientation(PlotOrientation.VERTICAL);
        plot.setBackgroundPaint(Color.lightGray);
        plot.setDomainGridlinePaint(Color.white);
        plot.setRangeGridlinePaint(Color.white);
        
        plot.setAxisOffset(new Spacer(Spacer.ABSOLUTE, 5.0, 5.0, 5.0, 5.0));
        
        StandardXYItemRenderer renderer = (StandardXYItemRenderer) plot.getRenderer();
        renderer.setPaint(Color.black);
       
        // AXIS 2
        NumberAxis axis2 = new NumberAxis("Range Axis 2");
        axis2.setAutoRangeIncludesZero(false);
        axis2.setLabelPaint(Color.red);
        axis2.setTickLabelPaint(Color.red);
        plot.setSecondaryRangeAxis(0, axis2);
        plot.setSecondaryRangeAxisLocation(0, AxisLocation.BOTTOM_OR_LEFT);

        XYDataset dataset2 = createDataset("Series 2", 1000.0, new Minute(), 170);
        plot.setSecondaryDataset(0, dataset2);
        plot.mapSecondaryDatasetToRangeAxis(0, new Integer(0));
        plot.setSecondaryRenderer(0, new StandardXYItemRenderer());
        plot.getSecondaryRenderer(0).setSeriesPaint(0, Color.red);
        
        // AXIS 3
        NumberAxis axis3 = new NumberAxis("Range Axis 3");
        axis3.setLabelPaint(Color.blue);
        axis3.setTickLabelPaint(Color.blue);
        plot.setSecondaryRangeAxis(1, axis3);

        XYDataset dataset3 = createDataset("Series 3", 10000.0, new Minute(), 170);
        plot.setSecondaryDataset(1, dataset3);
        plot.mapSecondaryDatasetToRangeAxis(1, new Integer(1));
        
        plot.setSecondaryRenderer(1, new StandardXYItemRenderer());
        plot.getSecondaryRenderer(1).setSeriesPaint(0, Color.blue);

        // AXIS 4        
        NumberAxis axis4 = new NumberAxis("Range Axis 4");
        axis4.setLabelPaint(Color.green);
        axis4.setTickLabelPaint(Color.green);
        plot.setSecondaryRangeAxis(2, axis4);
        
        XYDataset dataset4 = createDataset("Series 4", 25.0, new Minute(), 200);
        plot.setSecondaryDataset(2, dataset4);
        plot.mapSecondaryDatasetToRangeAxis(2, new Integer(2));
        
        plot.setSecondaryRenderer(2, new StandardXYItemRenderer());
        plot.getSecondaryRenderer(2).setSeriesPaint(0, Color.green);        
                
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

        MultipleAxisDemo1 demo = new MultipleAxisDemo1("Multiple Axis Demo 1");
        demo.pack();
        RefineryUtilities.centerFrameOnScreen(demo);
        demo.setVisible(true);

    }

}
