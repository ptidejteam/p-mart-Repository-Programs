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
 * Pie3DChartDemo1.java
 * --------------------
 * (C) Copyright 2002, 2003, by Object Refinery Limited and Contributors.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited);
 * Contributor(s):   -;
 *
 * $Id: Pie3DChartDemo1.java,v 1.1 2007/10/10 19:09:08 vauchers Exp $
 *
 * Changes
 * -------
 * 19-Jun-2002 : Version 1 (DG);
 * 31-Jul-2002 : Updated with changes to Pie3DPlot class (DG);
 * 11-Oct-2002 : Fixed errors reported by Checkstyle (DG);
 *
 */

package org.jfree.chart.demo;

import java.awt.Color;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.Pie3DPlot;
import org.jfree.data.DefaultPieDataset;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;

/**
 * A simple demonstration application showing how to create a pie chart using data from a
 * {@link DefaultPieDataset}.
 *
 * @author David Gilbert
 */
public class Pie3DChartDemo1 extends ApplicationFrame {

    /**
     * Creates a new demo.
     *
     * @param title  the frame title.
     */
    public Pie3DChartDemo1(String title) {

        super(title);

        // create a dataset...
        DefaultPieDataset data = new DefaultPieDataset();
        data.setValue("Java", new Double(43.2));
        data.setValue("Visual Basic", new Double(10.0));
        data.setValue("C/C++", new Double(17.5));
        data.setValue("PHP", new Double(32.5));
        data.setValue("Perl", new Double(0.0));

        // create the chart...
        JFreeChart chart = ChartFactory.createPie3DChart("Pie Chart 3D Demo 1",  // chart title
                                                         data,                // data
                                                         true,                // include legend
                                                         true,
                                                         false
                                                         );

        // set the background color for the chart...
        chart.setBackgroundPaint(Color.yellow);
        Pie3DPlot plot = (Pie3DPlot) chart.getPlot();
        plot.setStartAngle(270);
        plot.setDirection(Pie3DPlot.CLOCKWISE);
        plot.setForegroundAlpha(0.5f);
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

        Pie3DChartDemo1 demo = new Pie3DChartDemo1("Pie Chart 3D Demo 1");
        demo.pack();
        RefineryUtilities.centerFrameOnScreen(demo);
        demo.setVisible(true);

    }

}
