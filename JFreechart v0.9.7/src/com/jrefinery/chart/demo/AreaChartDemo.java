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
 * ------------------
 * AreaChartDemo.java
 * ------------------
 * (C) Copyright 2002, 2003 by Simba Management Limited and Contributors.
 *
 * Original Author:  David Gilbert (for Simba Management Limited);
 * Contributor(s):   -;
 *
 * $Id: AreaChartDemo.java,v 1.1 2007/10/10 20:00:13 vauchers Exp $
 *
 * Changes
 * -------
 * 11-Jun-2002 : Version 1 (DG);
 * 25-Jun-2002 : Removed unnecessary imports (DG);
 * 10-Oct-2002 : Renamed AreaChartForCategoryDataDemo --> AreaChartDemo (DG);
 *
 */

package com.jrefinery.chart.demo;

import java.awt.Color;

import com.jrefinery.chart.ChartFactory;
import com.jrefinery.chart.ChartPanel;
import com.jrefinery.chart.JFreeChart;
import com.jrefinery.chart.StandardLegend;
import com.jrefinery.chart.TextTitle;
import com.jrefinery.chart.axis.HorizontalCategoryAxis;
import com.jrefinery.chart.axis.NumberAxis;
import com.jrefinery.chart.plot.CategoryPlot;
import com.jrefinery.data.CategoryDataset;
import com.jrefinery.data.DatasetUtilities;
import com.jrefinery.ui.ApplicationFrame;
import com.jrefinery.ui.RefineryUtilities;

/**
 * A simple demonstration application showing how to create an area chart using data from a
 * {@link CategoryDataset}.
 *
 * @author David Gilbert
 */
public class AreaChartDemo extends ApplicationFrame {

    /**
     * Creates a new demo application.
     *
     * @param title  the frame title.
     */
    public AreaChartDemo(String title) {

        super(title);

        // create a dataset...
        double[][] data = new double[][] {
            { 1.0, 4.0, 3.0, 5.0, 5.0, 7.0, 7.0, 8.0 },
            { 5.0, 7.0, 6.0, 8.0, 4.0, 4.0, 2.0, 1.0 },
            { 4.0, 3.0, 2.0, 3.0, 6.0, 3.0, 4.0, 3.0 }
        };

        CategoryDataset dataset = DatasetUtilities.createCategoryDataset("Series ", "Type ", data);

        // create the chart...
        JFreeChart chart = ChartFactory.createAreaChart("Area Chart",  // chart title
                                                        "Category",    // domain axis label
                                                        "Value",       // range axis label
                                                        dataset,       // data
                                                        true,          // include legend
                                                        true,          // tooltips
                                                        false          // urls
                                                        );

        // NOW DO SOME OPTIONAL CUSTOMISATION OF THE CHART...

        // set the background color for the chart...
        StandardLegend legend = (StandardLegend) chart.getLegend();
        legend.setAnchor(StandardLegend.EAST);
        
        chart.setBackgroundPaint(Color.yellow);
        TextTitle subtitle = new TextTitle("An area chart demonstration.");
        chart.addSubtitle(subtitle);
        
        CategoryPlot plot = chart.getCategoryPlot();
        plot.setOutlineStroke(null);
        plot.setForegroundAlpha(0.5f);
        plot.setValueLabelsVisible(true);
        plot.setDomainGridlinesVisible(true);
        plot.setRangeGridlinesVisible(true);
        
        HorizontalCategoryAxis domainAxis = (HorizontalCategoryAxis) plot.getDomainAxis();
        domainAxis.setVerticalCategoryLabels(true);
        domainAxis.setLowerMargin(0.0);
        domainAxis.setUpperMargin(0.0);

        NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
        rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
        // OPTIONAL CUSTOMISATION COMPLETED.

        // add the chart to a panel...
        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new java.awt.Dimension(500, 270));
        chartPanel.setEnforceFileExtensions(false);
        setContentPane(chartPanel);

    }

    /**
     * Starting point for the demonstration application.
     *
     * @param args  ignored.
     */
    public static void main(String[] args) {

        AreaChartDemo demo = new AreaChartDemo("Area Chart Demo");
        demo.pack();
        RefineryUtilities.centerFrameOnScreen(demo);
        demo.setVisible(true);

    }

}
