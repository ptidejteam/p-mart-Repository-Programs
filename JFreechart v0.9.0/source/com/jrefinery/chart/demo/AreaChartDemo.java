/* ===============
 * JFreeChart Demo
 * ===============
 *
 * Project Info:  http://www.object-refinery.com/jfreechart/index.html
 * Project Lead:  David Gilbert (david.gilbert@object-refinery.com);
 *
 * (C) Copyright 2000-2002, by Simba Management Limited and Contributors.
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
 * AreaChartDemo.java
 * ------------------
 * (C) Copyright 2002, by Simba Management Limited and Contributors.
 *
 * Original Author:  David Gilbert (for Simba Management Limited);
 * Contributor(s):   -;
 *
 * $Id: AreaChartDemo.java,v 1.1 2007/10/10 19:01:20 vauchers Exp $
 *
 * Changes
 * -------
 * 03-Apr-2002 : Version 1 (DG);
 *
 */

package com.jrefinery.chart.demo;

import com.jrefinery.chart.JFreeChart;
import com.jrefinery.chart.ChartFactory;
import com.jrefinery.chart.ChartPanel;
import com.jrefinery.chart.XYPlot;
import com.jrefinery.chart.XYItemRenderer;
import com.jrefinery.chart.tooltips.StandardXYToolTipGenerator;
import com.jrefinery.data.XYSeries;
import com.jrefinery.data.XYSeriesCollection;
import com.jrefinery.ui.ApplicationFrame;

/**
 * A simple demonstration application showing how to create an area chart.
 */
public class AreaChartDemo extends ApplicationFrame {

    protected XYSeries series1;
    protected XYSeries series2;

    /**
     * Default constructor.
     */
    public AreaChartDemo(String title) {

        super(title);
        this.series1 = new XYSeries("Random 1");
        this.series1.add(new Integer(1), new Double(500.2));
        this.series1.add(new Integer(2), new Double(694.1));
        this.series1.add(new Integer(3), new Double(-734.4));
        this.series1.add(new Integer(4), new Double(453.2));
        this.series1.add(new Integer(5), new Double(500.2));
        this.series1.add(new Integer(6), new Double(300.7));
        this.series1.add(new Integer(7), new Double(734.4));
        this.series1.add(new Integer(8), new Double(453.2));

        this.series2 = new XYSeries("Random 2");
        this.series2.add(new Integer(1), new Double(700.2));
        this.series2.add(new Integer(2), new Double(534.1));
        this.series2.add(new Integer(3), new Double(323.4));
        this.series2.add(new Integer(4), new Double(125.2));
        this.series2.add(new Integer(5), new Double(653.2));
        this.series2.add(new Integer(6), new Double(432.7));
        this.series2.add(new Integer(7), new Double(564.4));
        this.series2.add(new Integer(8), new Double(322.2));

        XYSeriesCollection dataset = new XYSeriesCollection(series1);
        dataset.addSeries(series2);
        JFreeChart chart = ChartFactory.createAreaXYChart("Area Chart Demo",
                                                          "Time", "Value",
                                                          dataset, true);
        XYPlot plot = chart.getXYPlot();
        plot.setForegroundAlpha(0.5f);

        XYItemRenderer renderer = plot.getItemRenderer();
        renderer.setToolTipGenerator(new StandardXYToolTipGenerator());
        ChartPanel chartPanel = new ChartPanel(chart);
        this.setContentPane(chartPanel);

    }

    /**
     * Starting point for the demonstration application.
     */
    public static void main(String[] args) {

        AreaChartDemo demo = new AreaChartDemo("Area Chart Demo");
        demo.pack();
        demo.setVisible(true);

    }

}
