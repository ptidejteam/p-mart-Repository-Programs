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
 * --------------------
 * XMLBarChartDemo.java
 * --------------------
 * (C) Copyright 2002, 2003, by Simba Management Limited and Contributors.
 *
 * Original Author:  David Gilbert (for Simba Management Limited);
 * Contributor(s):   -;
 *
 * $Id: XMLBarChartDemo.java,v 1.1 2007/10/10 19:57:52 vauchers Exp $
 *
 * Changes
 * -------
 * 28-Oct-2002 : Version 1 (DG);
 *
 */

package com.jrefinery.chart.demo;

import java.awt.Color;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import com.jrefinery.chart.JFreeChart;
import com.jrefinery.chart.ChartFactory;
import com.jrefinery.chart.ChartPanel;
import com.jrefinery.chart.plot.CategoryPlot;
import com.jrefinery.data.CategoryDataset;
import com.jrefinery.data.xml.DatasetReader;
import com.jrefinery.ui.ApplicationFrame;
import com.jrefinery.ui.RefineryUtilities;

/**
 * A simple demonstration application showing how to create a bar chart using data from an
 * XML data file.
 *
 * @author David Gilbert
 */
public class XMLBarChartDemo extends ApplicationFrame {

    /**
     * Default constructor.
     *
     * @param title  the frame title.
     */
    public XMLBarChartDemo(String title) {

        super(title);

        // create a dataset...
        CategoryDataset dataset = null;
        URL url = getClass().getResource("/com/jrefinery/chart/demo/categorydata.xml");

        try {
            InputStream in = url.openStream();
            dataset = DatasetReader.readCategoryDatasetFromXML(in);
        }
        catch (IOException ioe) {
            System.out.println(ioe.getMessage());
        }

        // create the chart...
        JFreeChart chart = ChartFactory.createVerticalBarChart("Bar Chart",  // chart title
                                                               "Domain", "Range",
                                                               dataset,      // data
                                                               true,         // include legend
                                                               true,
                                                               false
                                                               );

        // set the background color for the chart...
        chart.setBackgroundPaint(Color.yellow);
        CategoryPlot plot = chart.getCategoryPlot();

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

        XMLBarChartDemo demo = new XMLBarChartDemo("XML Bar Chart Demo");
        demo.pack();
        RefineryUtilities.centerFrameOnScreen(demo);
        demo.setVisible(true);

    }

}
