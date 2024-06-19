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
 * -----------------------
 * EventFrequencyDemo.java
 * -----------------------
 * (C) Copyright 2002, 2003, by Simba Management Limited and Contributors.
 *
 * Original Author:  David Gilbert (for Simba Management Limited);
 * Contributor(s):   -;
 *
 * $Id: EventFrequencyDemo.java,v 1.1 2007/10/10 20:00:13 vauchers Exp $
 *
 * Changes (from 10-Oct-2002)
 * --------------------------
 * 10-Oct-2002 : Added standard header and Javadocs (DG);
 * 11-Feb-2003 : Fixed 0.9.5 bug (DG);
 *
 */

package com.jrefinery.chart.demo;

import java.awt.Color;
import java.text.DateFormat;

import com.jrefinery.chart.ChartFactory;
import com.jrefinery.chart.ChartPanel;
import com.jrefinery.chart.JFreeChart;
import com.jrefinery.chart.StandardLegend;
import com.jrefinery.chart.axis.HorizontalDateAxis;
import com.jrefinery.chart.plot.CategoryPlot;
import com.jrefinery.chart.renderer.HorizontalShapeRenderer;
import com.jrefinery.chart.tooltips.CategoryToolTipGenerator;
import com.jrefinery.chart.tooltips.StandardCategoryToolTipGenerator;
import com.jrefinery.data.DefaultCategoryDataset;
import com.jrefinery.data.time.Day;
import com.jrefinery.date.SerialDate;
import com.jrefinery.ui.ApplicationFrame;
import com.jrefinery.ui.RefineryUtilities;

/**
 * A demo application showing how to display category data against a date axis.
 *
 * @author David Gilbert
 */
public class EventFrequencyDemo extends ApplicationFrame {

    /**
     * Creates a new demo.
     *
     * @param title  the frame title.
     */
    public EventFrequencyDemo(String title) {

        super(title);

        // create a dataset...
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        
        // initialise the data...
        Day d1 = new Day(12, SerialDate.JUNE, 2002);
        Day d2 = new Day(14, SerialDate.JUNE, 2002);
        Day d3 = new Day(15, SerialDate.JUNE, 2002);
        Day d4 = new Day(10, SerialDate.JULY, 2002);
        Day d5 = new Day(20, SerialDate.JULY, 2002);
        Day d6 = new Day(22, SerialDate.AUGUST, 2002);

        dataset.setValue(new Long(d1.getMiddleMillisecond()), "Series 1", "Requirement 1");
        dataset.setValue(new Long(d1.getMiddleMillisecond()), "Series 1", "Requirement 2");
        dataset.setValue(new Long(d2.getMiddleMillisecond()), "Series 1", "Requirement 3");
        dataset.setValue(new Long(d3.getMiddleMillisecond()), "Series 2", "Requirement 1");
        dataset.setValue(new Long(d4.getMiddleMillisecond()), "Series 2", "Requirement 3");
        dataset.setValue(new Long(d5.getMiddleMillisecond()), "Series 3", "Requirement 2");
        dataset.setValue(new Long(d6.getMiddleMillisecond()), "Series 1", "Requirement 4");

        // create the chart...
        JFreeChart chart = ChartFactory.createHorizontalBarChart(
                                            "Event Frequency Demo",  // title
                                            "Category",              // domain axis label
                                            "Value",                 // range axis label
                                            dataset,                 // data
                                            true,                    // include legend
                                            true,                    // tooltips
                                            false                    // URLs
                                        );

        // NOW DO SOME OPTIONAL CUSTOMISATION OF THE CHART...

        // set the background color for the chart...
        chart.setBackgroundPaint(new Color(0xFF, 0xFF, 0xCC));
        
        StandardLegend legend = (StandardLegend) chart.getLegend();
        legend.setDisplaySeriesShapes(true);

        // get a reference to the plot for further customisation...
        CategoryPlot plot = chart.getCategoryPlot();
        plot.setRangeAxis(new HorizontalDateAxis("Date"));
        CategoryToolTipGenerator tooltips
            = new StandardCategoryToolTipGenerator(DateFormat.getDateInstance());
        plot.setRenderer(new HorizontalShapeRenderer(HorizontalShapeRenderer.SHAPES,
                                                     HorizontalShapeRenderer.TOP,
                                                     tooltips,
                                                     null));

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

        EventFrequencyDemo demo = new EventFrequencyDemo("Event Frequency Demo");
        demo.pack();
        RefineryUtilities.centerFrameOnScreen(demo);
        demo.setVisible(true);
        
    }

}
