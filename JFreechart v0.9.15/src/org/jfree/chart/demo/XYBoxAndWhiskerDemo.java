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
 * ------------------------
 * XYBoxAndWhiskerDemo.java
 * ------------------------
 * (C) Copyright 2003, by David Browning and Contributors.
 *
 * Original Author:  David Browning;
 * Contributor(s):   David Gilbert (for Object Refinery Limited);
 *
 * $Id: XYBoxAndWhiskerDemo.java,v 1.1 2007/10/10 19:21:51 vauchers Exp $
 *
 * Changes
 * -------
 * 15-Jul-2003 : Version 1 (DB);
 * 27-Aug-2003 : Renamed BoxAndWhiskerDemo --> XYBoxAndWhiskerDemo (DG);
 *
 */

package org.jfree.chart.demo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.statistics.BoxAndWhiskerCalculator;
import org.jfree.data.statistics.BoxAndWhiskerXYDataset;
import org.jfree.data.statistics.DefaultBoxAndWhiskerXYDataset;
import org.jfree.date.DateUtilities;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;

/**
 * A demo showing a box and whisker chart.
 *
 * @author David David Browning
 */
public class XYBoxAndWhiskerDemo extends ApplicationFrame {

    /**
     * A demonstration application showing a box and whisker chart.
     *
     * @param title  the frame title.
     */
    public XYBoxAndWhiskerDemo(String title) {

        super(title);

        BoxAndWhiskerXYDataset dataset = createSampleDataset();
        JFreeChart chart = createChart(dataset);
        chart.getXYPlot().setOrientation(PlotOrientation.VERTICAL);
        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new java.awt.Dimension(600, 400));
        setContentPane(chartPanel);

    }

    /**
     * Creates a chart.
     * 
     * @param dataset  the dataset.
     * 
     * @return The dataset.
     */
    private JFreeChart createChart(BoxAndWhiskerXYDataset dataset) {
        
        JFreeChart chart = ChartFactory.createBoxAndWhiskerChart(
            "Box-and-Whisker Demo",
            "Time", 
            "Value",
            dataset, 
            true
        );
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
     * Creates a sample {@link BoxAndWhiskerDataset}.
     *
     * @return A sample dataset.
     */
    public static BoxAndWhiskerXYDataset createSampleDataset() {
        
        final int ENTITY_COUNT = 14;

        DefaultBoxAndWhiskerXYDataset dataset = new DefaultBoxAndWhiskerXYDataset("Test");
        for (int i = 0; i < ENTITY_COUNT; i++) {
            Date date = DateUtilities.createDate(2003, 7, i + 1, 12, 0);
            List values = new ArrayList();
            for (int j = 0; j < 10; j++) {
                values.add(new Double(10.0 + Math.random() * 10.0));
                values.add(new Double(13.0 + Math.random() * 4.0));
            }
            dataset.add(date, BoxAndWhiskerCalculator.calculateBoxAndWhiskerStatistics(values));
           
        }

        return dataset; 
    }

    /**
     * Starting point for the demonstration application.
     *
     * @param args  ignored.
     */
    public static void main(String[] args) {

        XYBoxAndWhiskerDemo demo = new XYBoxAndWhiskerDemo("Box-and-Whisker Demo");
        demo.pack();
        RefineryUtilities.centerFrameOnScreen(demo);
        demo.setVisible(true);

    }

}

