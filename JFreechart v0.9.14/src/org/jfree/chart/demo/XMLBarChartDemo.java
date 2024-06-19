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
 * XMLBarChartDemo.java
 * --------------------
 * (C) Copyright 2002, 2003, by Object Refinery Limited and Contributors.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited);
 * Contributor(s):   -;
 *
 * $Id: XMLBarChartDemo.java,v 1.1 2007/10/10 19:19:01 vauchers Exp $
 *
 * Changes
 * -------
 * 28-Oct-2002 : Version 1 (DG);
 *
 */

package org.jfree.chart.demo;

import java.awt.Color;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.CategoryDataset;
import org.jfree.data.xml.DatasetReader;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;

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
        URL url = getClass().getResource("/org/jfree/chart/demo/categorydata.xml");

        try {
            InputStream in = url.openStream();
            dataset = DatasetReader.readCategoryDatasetFromXML(in);
        }
        catch (IOException ioe) {
            System.out.println(ioe.getMessage());
        }

        // create the chart...
        JFreeChart chart = ChartFactory.createBarChart("Bar Chart",  // chart title
                                                       "Domain", "Range",
                                                       dataset,      // data
                                                       PlotOrientation.VERTICAL,
                                                       true,         // include legend
                                                       true,
                                                       false);

        // set the background color for the chart...
        chart.setBackgroundPaint(Color.yellow);

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
