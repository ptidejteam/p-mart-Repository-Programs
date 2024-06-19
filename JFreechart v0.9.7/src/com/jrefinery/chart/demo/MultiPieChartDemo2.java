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
 * MultiPieChartDemo2.java
 * -----------------------
 * (C) Copyright 2003 by Simba Management Limited and Contributors.
 *
 * Original Author:  David Gilbert (for Simba Management Limited);
 * Contributor(s):   -;
 *
 * $Id: MultiPieChartDemo2.java,v 1.1 2007/10/10 20:00:13 vauchers Exp $
 *
 * Changes
 * -------
 * 23-Jan-2003 : Version 1 (DG);
 *
 */

package com.jrefinery.chart.demo;

import java.awt.Color;

import com.jrefinery.data.DatasetUtilities;
import com.jrefinery.data.CategoryDataset;
import com.jrefinery.chart.JFreeChart;
import com.jrefinery.chart.ChartFactory;
import com.jrefinery.chart.ChartPanel;
import com.jrefinery.chart.plot.PiePlot;
import com.jrefinery.ui.ApplicationFrame;
import com.jrefinery.ui.RefineryUtilities;

/**
 * Identical to MultiPieChartDemo, but pie charts are produced for each COLUMN rather than ROW.
 *
 * @author David Gilbert
 */
public class MultiPieChartDemo2 extends ApplicationFrame {

    /**
     * Creates a new demo.
     *
     * @param title  the frame title.
     */
    public MultiPieChartDemo2(String title) {

        super(title);

        // create a dataset...
        double[][] data = new double[][] {
            { 3.0, 4.0, 3.0, 5.0 },
            { 5.0, 7.0, 6.0, 8.0 },
            { 5.0, 7.0, 3.0, 8.0 },
            { 1.0, 2.0, 3.0, 4.0 },
            { 2.0, 3.0, 2.0, 3.0 }
        };

        CategoryDataset dataset = DatasetUtilities.createCategoryDataset("Region ", 
                                                                         "Sales/Q", 
                                                                         data);

        // create the chart...
        JFreeChart chart = ChartFactory.createPieChart("Multi Pie Chart 2",  // chart title
                                                       dataset,              // data
                                                       PiePlot.PER_COLUMN,
                                                       true,                 // include legend
                                                       true,
                                                       false
                                                       );

        // NOW DO SOME OPTIONAL CUSTOMISATION OF THE CHART...

        chart.setBackgroundPaint(Color.yellow);
        PiePlot plot = (PiePlot) chart.getPlot();
        plot.setSectionLabelType(PiePlot.VALUE_AND_PERCENT_LABELS);
        
        // OPTIONAL CUSTOMISATION COMPLETED.

        // add the chart to a panel...
        ChartPanel chartPanel = new ChartPanel(chart, true, true, true, false, true);
        chartPanel.setPreferredSize(new java.awt.Dimension(600, 380));
        setContentPane(chartPanel);

    }

    /**
     * Starting point for the demonstration application.
     *
     * @param args  ignored.
     */
    public static void main(String[] args) {

        MultiPieChartDemo2 demo = new MultiPieChartDemo2("Multi Pie Chart Demo 2");
        demo.pack();
        RefineryUtilities.centerFrameOnScreen(demo);
        demo.setVisible(true);

    }

}
