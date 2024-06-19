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
 * ------------------
 * XYSeriesDemo2.java
 * ------------------
 * (C) Copyright 2002, 2003, by Simba Management Limited and Contributors.
 *
 * Original Author:  David Gilbert (for Simba Management Limited);
 * Contributor(s):   -;
 *
 * $Id: XYSeriesDemo2.java,v 1.1 2007/10/10 20:03:17 vauchers Exp $
 *
 * Changes
 * -------
 * 01-Oct-2002 : Version 1 (DG);
 *
 */

package org.jfree.chart.demo;

import org.jfree.data.XYSeries;
import org.jfree.data.XYSeriesCollection;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;

/**
 * Demo for {@link XYSeries}, where all the y values are the same.
 *
 * @author David Gilbert
 */
public class XYSeriesDemo2 extends ApplicationFrame {

    /**
     * A demonstration application showing an {@link XYSeries} where all the y-values are the same.
     *
     * @param title  the frame title.
     */
    public XYSeriesDemo2(String title) {

        super(title);
        XYSeries series = new XYSeries("Flat Data");
        series.add(1.0, 100.0);
        series.add(5.0, 100.0);
        series.add(4.0, 100.0);
        series.add(12.5, 100.0);
        series.add(17.3, 100.0);
        series.add(21.2, 100.0);
        series.add(21.9, 100.0);
        series.add(25.6, 100.0);
        series.add(30.0, 100.0);
        XYSeriesCollection data = new XYSeriesCollection(series);
        JFreeChart chart = ChartFactory.createLineXYChart("XY Series Demo 2",
                                                          "X", "Y", data, 
                                                          true,
                                                          true,
                                                          false);

        XYPlot plot = (XYPlot) chart.getPlot();
        NumberAxis axis = (NumberAxis) plot.getRangeAxis();
        axis.setAutoRangeIncludesZero(false);
        axis.setAutoRangeMinimumSize(1.0);
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

        XYSeriesDemo2 demo = new XYSeriesDemo2("XY Series Demo 2");
        demo.pack();
        RefineryUtilities.centerFrameOnScreen(demo);
        demo.setVisible(true);

    }

}
