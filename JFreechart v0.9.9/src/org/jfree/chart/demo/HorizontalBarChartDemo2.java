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
 * ----------------------------
 * HorizontalBarChartDemo2.java
 * ----------------------------
 * (C) Copyright 2002, 2003, by Object Refinery Limited and Contributors.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited);
 * Contributor(s):   -;
 *
 * $Id: HorizontalBarChartDemo2.java,v 1.1 2007/10/10 20:07:29 vauchers Exp $
 *
 * Changes
 * -------
 * 11-Jun-2002 : Version 1 (DG);
 * 25-Jun-2002 : Removed unnecessary imports (DG);
 * 09-Oct-2002 : Added frame centering (DG);
 * 14-Nov-2002 : Renamed HorizontalBarChartDemo --> HorizontalBarChartDemo2 (DG);
 *
 */

package org.jfree.chart.demo;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.IntervalMarker;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.MarkerAxisBand;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.CategoryDataset;
import org.jfree.data.DatasetUtilities;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;

/**
 * A simple demonstration application showing how to create a horizontal bar chart.
 *
 * @author David Gilbert
 */
public class HorizontalBarChartDemo2 extends ApplicationFrame {

    /**
     * Creates a new demo instance.
     *
     * @param title  the frame title.
     */
    public HorizontalBarChartDemo2(String title) {

        super(title);

        // create a dataset...
        double[][] data = new double[][] {
            { 1.0, 43.0, 35.0, 58.0, 54.0, 77.0, 71.0, 89.0 },
            { 54.0, 75.0, 63.0, 83.0, 43.0, 46.0, 27.0, 13.0 },
            { 41.0, 33.0, 22.0, 34.0, 62.0, 32.0, 42.0, 34.0 }
        };

        CategoryDataset dataset = DatasetUtilities.createCategoryDataset("Series ",
                                                                         "Factor ",
                                                                         data);

        // create the chart...
        JFreeChart chart = ChartFactory.createBarChart("Horizontal Bar Chart",  // chart title
                                                       "Category",              // domain axis label
                                                       "Score (%)",             // range axis label
                                                       dataset,                 // data
                                                       PlotOrientation.HORIZONTAL,
                                                       true,                    // include legend
                                                       true,
                                                       false);

        // NOW DO SOME OPTIONAL CUSTOMISATION OF THE CHART...

        chart.setBackgroundPaint(Color.lightGray);

        // get a reference to the plot for further customisation...
        CategoryPlot plot = chart.getCategoryPlot();

        plot.getRenderer().setSeriesPaint(0, new Color(0, 0, 255));
        plot.getRenderer().setSeriesPaint(1, new Color(75, 75, 255));
        plot.getRenderer().setSeriesPaint(2, new Color(150, 150, 255));

        // change the auto tick unit selection to integer units only...
        NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
        rangeAxis.setRange(0.0, 100.0);
        rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());

        NumberAxis hna = (NumberAxis) rangeAxis;
        MarkerAxisBand band = new MarkerAxisBand(hna, 2.0, 2.0, 2.0, 2.0,
                                        new Font("SansSerif", Font.PLAIN, 9));

        IntervalMarker m1 = new IntervalMarker(0.0, 33.0, "Low", Color.gray,
                                               new BasicStroke(0.5f), Color.green, 0.75f);
        IntervalMarker m2 = new IntervalMarker(33.0, 66.0, "Medium", Color.gray,
                                               new BasicStroke(0.5f), Color.orange, 0.75f);
        IntervalMarker m3 = new IntervalMarker(66.0, 100.0, "High", Color.gray,
                                               new BasicStroke(0.5f), Color.red, 0.75f);
        band.addMarker(m1);
        band.addMarker(m2);
        band.addMarker(m3);
        hna.setMarkerBand(band);
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

        HorizontalBarChartDemo2 demo = new HorizontalBarChartDemo2("Horizontal Bar Chart Demo");
        demo.pack();
        RefineryUtilities.centerFrameOnScreen(demo);
        demo.setVisible(true);

    }

}
