/* ======================================
 * JFreeChart : a free Java chart library
 * ======================================
 *
 * Project Info:  http://www.jfree.org/jfreechart/index.html
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
 * -----------------------------
 * HorizontalBarChart3DDemo.java
 * -----------------------------
 * (C) Copyright 2002, 2003, by Simba Management Limited and Contributors.
 *
 * Original Author:  David Gilbert (for Simba Management Limited);
 * Contributor(s):   -;
 *
 * $Id: HorizontalBarChart3DDemo.java,v 1.1 2007/10/10 20:03:18 vauchers Exp $
 *
 * Changes
 * -------
 * 29-May-2002 : Version 1 (DG);
 * 10-Oct-2002 : Fixed errors reported by Checkstyle (DG);
 * 05-Nov-2002 : Renamed HorizontalBarChart3DDemo.java (DG);
 *
 */

package org.jfree.chart.demo;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.CategoryDataset;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;

/**
 * A simple demonstration application showing how to create a horizontal 3D bar chart using data
 * from a {@link TableDataset}.
 *
 * @author David Gilbert
 */
public class HorizontalBarChart3DDemo extends ApplicationFrame {

    /**
     * Creates a new demo.
     *
     * @param title  the frame title.
     */
    public HorizontalBarChart3DDemo(String title) {

        super(title);
		CategoryDataset dataset = DemoDatasetFactory.createCategoryDataset();

        // create the chart...
        JFreeChart chart = ChartFactory.createHorizontalBarChart3D(
                                                  "Horizontal Bar 3D Demo",  // chart title
                                                  "Category",                // domain axis label
                                                  "Value",                   // range axis label
                                                  dataset,                   // data
                                                  true,                      // include legend
                                                  true,                      // tooltips
                                                  false                      // urls
                                              );

        //chart.getCategoryPlot().getDomainAxis().setVisible(false);
        //chart.getCategoryPlot().getRangeAxis().setVisible(false);
        
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

        HorizontalBarChart3DDemo demo
            = new HorizontalBarChart3DDemo("Horizontal 3D Bar Chart Demo");
        demo.pack();
        RefineryUtilities.centerFrameOnScreen(demo);
        demo.setVisible(true);

    }

}
