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
 * ---------------------
 * HighLowChartDemo.java
 * ---------------------
 * (C) Copyright 2003, by Simba Management Limited and Contributors.
 *
 * Original Author:  David Gilbert (for Simba Management Limited);
 * Contributor(s):   -;
 *
 * $Id: HighLowChartDemo.java,v 1.1 2007/10/10 20:00:13 vauchers Exp $
 *
 * Changes
 * -------
 * 05-Feb-2003 : Version 1 (DG);
 *
 */

package com.jrefinery.chart.demo;

import com.jrefinery.chart.JFreeChart;
import com.jrefinery.chart.ChartFactory;
import com.jrefinery.chart.ChartPanel;
import com.jrefinery.chart.axis.DateAxis;
import com.jrefinery.data.HighLowDataset;
import com.jrefinery.ui.ApplicationFrame;
import com.jrefinery.ui.RefineryUtilities;

/**
 * A demo showing a high-low-open-close chart.
 *
 * @author David Gilbert
 */
public class HighLowChartDemo extends ApplicationFrame {

    /**
     * A demonstration application showing a high-low-open-close chart.
     *
     * @param title  the frame title.
     */
    public HighLowChartDemo(String title) {

        super(title);

        HighLowDataset dataset = DemoDatasetFactory.createHighLowDataset();
        JFreeChart chart = ChartFactory.createHighLowChart("High-Low-Open-Close Demo",
                                                           "Time", "Value",
                                                           dataset, true);
        DateAxis axis = (DateAxis) chart.getXYPlot().getDomainAxis();
        axis.setTickMarksAtStartOfUnit(false);
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

        HighLowChartDemo demo = new HighLowChartDemo("High-Low-Open-Close Demo");
        demo.pack();
        RefineryUtilities.centerFrameOnScreen(demo);
        demo.setVisible(true);

    }

}
