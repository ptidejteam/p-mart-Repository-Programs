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
 * XYLogAxesDemo.java
 * ------------------
 * (C) Copyright 2002, 2003, by Simba Management Limited and Contributors.
 *
 * Original Author:  David Gilbert (for Simba Management Limited);
 * Contributor(s):   Clemens;
 *
 * $Id: XYLogAxesDemo.java,v 1.1 2007/10/10 19:54:11 vauchers Exp $
 *
 * Changes
 * -------
 * 31-Jul-2002 : Version 1 (DG);
 * 11-Oct-2002 : Fixed issues reported by Checkstyle (DG);
 * 31-Jan-2003 : Replaced DefaultXYDataset with XYSeriesCollection (DG);
 *
 */
package com.jrefinery.chart.demo;

import java.awt.Color;
import com.jrefinery.data.XYSeries;
import com.jrefinery.data.XYSeriesCollection;
import com.jrefinery.ui.ApplicationFrame;
import com.jrefinery.chart.JFreeChart;
import com.jrefinery.chart.ChartFactory;
import com.jrefinery.chart.ChartPanel;
import com.jrefinery.chart.axis.HorizontalLogarithmicAxis;
import com.jrefinery.chart.axis.VerticalLogarithmicAxis;
import com.jrefinery.chart.plot.XYPlot;
import com.jrefinery.ui.RefineryUtilities;

/**
 * A demo showing the use of log axes.
 *
 * @author David Gilbert
 */
public class XYLogAxesDemo extends ApplicationFrame {

    /**
     * Creates a new demo.
     *
     * @param title  the frame title.
     */
    public XYLogAxesDemo(String title) {

        super(title);

        Object[][][] data = new Object[3][50][2];
        XYSeries s1 = new XYSeries("Series 1");
        XYSeries s2 = new XYSeries("Series 2");
        XYSeries s3 = new XYSeries("Series 3");
        
        for (int i = 1; i <= 50; i++) {
            s1.add(i, 1000 * Math.pow(i, -2));
            s2.add(i, 1000 * Math.pow(i, -3));
            s3.add(i, 1000 * Math.pow(i, -4));
        }
        
        XYSeriesCollection dataset = new XYSeriesCollection();
        dataset.addSeries(s1);
        dataset.addSeries(s2);
        dataset.addSeries(s3);

        JFreeChart chart = ChartFactory.createLineXYChart("Log Axis Demo", // chart title
                                                          "Category",      // domain axis label
                                                          "Value",         // range axis label
                                                          dataset,         // data
                                                          true,            // include legend
                                                          true,
                                                          false
                                                          );


        XYPlot plot = chart.getXYPlot();
        VerticalLogarithmicAxis v = new VerticalLogarithmicAxis("Log(y)");
        HorizontalLogarithmicAxis h = new HorizontalLogarithmicAxis("Log(x)");
        plot.setRangeAxis(v);
        plot.setDomainAxis(h);
        chart.setBackgroundPaint(Color.white);
        plot.setOutlinePaint(Color.black);
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

        XYLogAxesDemo demo = new XYLogAxesDemo("XY Log Axes Demo");
        demo.pack();
        RefineryUtilities.centerFrameOnScreen(demo);
        demo.setVisible(true);

    }

}
