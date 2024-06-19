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
 * AreaXYChartDemo.java
 * --------------------
 * (C) Copyright 2002, 2003 by Object Refinery Limited and Contributors.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited);
 * Contributor(s):   -;
 *
 * $Id: AreaXYChartDemo.java,v 1.1 2007/10/10 20:07:29 vauchers Exp $
 *
 * Changes
 * -------
 * 03-Apr-2002 : Version 1 (DG);
 * 09-Oct-2002 : Fixed errors reported by Checkstyle (DG);
 * 10-Oct-2002 : Renamed AreaChartDemo --> AreaXYChartDemo (DG);
 * 10-Jun-2003 : Modified to handle plot orientation (DG);
 *
 */

package org.jfree.chart.demo;

import java.awt.Color;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.XYDataset;
import org.jfree.data.XYSeries;
import org.jfree.data.XYSeriesCollection;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;

/**
 * A simple demonstration application showing how to create an area chart.
 *
 * @author David Gilbert
 */
public class AreaXYChartDemo extends ApplicationFrame {

    /**
     * Creates a new demo.
     *
     * @param title  the frame title.
     */
    public AreaXYChartDemo(String title) {

        super(title);

        XYSeries series1 = new XYSeries("Random 1");
        series1.add(new Integer(1), new Double(500.2));
        series1.add(new Integer(2), new Double(694.1));
        series1.add(new Integer(3), new Double(-734.4));
        series1.add(new Integer(4), new Double(453.2));
        series1.add(new Integer(5), new Double(500.2));
        series1.add(new Integer(6), new Double(300.7));
        series1.add(new Integer(7), new Double(734.4));
        series1.add(new Integer(8), new Double(453.2));

        XYSeries series2 = new XYSeries("Random 2");
        series2.add(new Integer(1), new Double(700.2));
        series2.add(new Integer(2), new Double(534.1));
        series2.add(new Integer(3), new Double(323.4));
        series2.add(new Integer(4), new Double(125.2));
        series2.add(new Integer(5), new Double(653.2));
        series2.add(new Integer(6), new Double(432.7));
        series2.add(new Integer(7), new Double(564.4));
        series2.add(new Integer(8), new Double(322.2));

        XYSeriesCollection dataset = new XYSeriesCollection(series1);
        dataset.addSeries(series2);
        
        JFreeChart chart = createChart(dataset);
        
        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new java.awt.Dimension(500, 270));
        setContentPane(chartPanel);

    }

    /**
     * Creates a chart.
     * 
     * @param dataset  the dataset.
     * 
     * @return A chart.
     */
    private JFreeChart createChart(XYDataset dataset) {
            
        JFreeChart chart = ChartFactory.createAreaXYChart(
            "Area XY Chart Demo",
            "Domain (X)", "Range (Y)",
            dataset,
            PlotOrientation.VERTICAL,
            true,  // legend
            true,  // tool tips
            false  // URLs
        );
        
        XYPlot plot = chart.getXYPlot();
        plot.setOutlinePaint(Color.black);
        plot.setForegroundAlpha(0.65f);
        
        ValueAxis domainAxis = plot.getDomainAxis();
        domainAxis.setTickMarkPaint(Color.black);
        domainAxis.setLowerMargin(0.0);
        domainAxis.setUpperMargin(0.0);
        
        ValueAxis rangeAxis = plot.getRangeAxis();
        rangeAxis.setTickMarkPaint(Color.black);
        
        return chart;
        
    }
    
    /**
     * Starting point for the demonstration application.
     *
     * @param args  ignored.
     */
    public static void main(String[] args) {

        AreaXYChartDemo demo = new AreaXYChartDemo("Area XY Chart Demo");
        demo.pack();
        RefineryUtilities.centerFrameOnScreen(demo);
        demo.setVisible(true);

    }

}
