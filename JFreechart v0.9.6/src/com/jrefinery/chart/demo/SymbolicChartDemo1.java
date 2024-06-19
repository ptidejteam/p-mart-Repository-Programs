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
 * ----------------------
 * SymbolicChartDemo.java
 * ----------------------
 * (C) Copyright 2003, by Simba Management Limited and Contributors.
 *
 * Original Author:  David Gilbert (for Simba Management Limited);
 * Contributor(s):   -;
 *
 * $Id: SymbolicChartDemo1.java,v 1.1 2007/10/10 19:57:52 vauchers Exp $
 *
 * Changes
 * -------
 * 14-Feb-2003 : Version 1 (DG);
 *
 */

package com.jrefinery.chart.demo;

import java.text.SimpleDateFormat;

import com.jrefinery.chart.JFreeChart;
import com.jrefinery.chart.ChartFactory;
import com.jrefinery.chart.ChartPanel;
import com.jrefinery.chart.StandardLegend;
import com.jrefinery.chart.axis.DateAxis;
import com.jrefinery.chart.axis.HorizontalNumberAxis;
import com.jrefinery.chart.axis.HorizontalSymbolicAxis;
import com.jrefinery.chart.axis.ValueAxis;
import com.jrefinery.chart.axis.VerticalSymbolicAxis;
import com.jrefinery.chart.plot.XYPlot;
import com.jrefinery.chart.renderer.XYItemRenderer;
import com.jrefinery.chart.renderer.StandardXYItemRenderer;
import com.jrefinery.chart.tooltips.SymbolicXYToolTipGenerator;
import com.jrefinery.data.TimeSeries;
import com.jrefinery.data.Month;
import com.jrefinery.data.XYDataset;
import com.jrefinery.data.TimeSeriesCollection;
import com.jrefinery.data.XisSymbolic;
import com.jrefinery.data.YisSymbolic;
import com.jrefinery.ui.ApplicationFrame;
import com.jrefinery.ui.RefineryUtilities;

/**
 * An example of...
 *
 * @author David Gilbert
 */
public class SymbolicChartDemo1 extends ApplicationFrame {

    /**
     * A demonstration application.
     *
     * @param title  the frame title.
     */
    public SymbolicChartDemo1(String title) {

        super(title);

        // create a title...
        XYDataset dataset = createDataset();

        ValueAxis domainAxis = new HorizontalNumberAxis("X");
        VerticalSymbolicAxis symbolicAxis
            = new VerticalSymbolicAxis("Y", ((YisSymbolic) dataset).getYSymbolicValues());

        XYPlot plot = new XYPlot(dataset, domainAxis, symbolicAxis);
        XYItemRenderer renderer = new StandardXYItemRenderer(StandardXYItemRenderer.SHAPES,
                                                             new SymbolicXYToolTipGenerator());
        plot.setRenderer(renderer);
        JFreeChart chart = new JFreeChart(title, plot);
   
        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new java.awt.Dimension(500, 270));
        setContentPane(chartPanel);

    }

    /**
     * Creates a dataset, consisting of two series of monthly data.
     *
     * @return the dataset.
     */
    public XYDataset createDataset() {

        String[] sData = {"Giraffe", "Gazelle", "Zebra", "Gnu"};
        SampleYSymbolicDataset data
            = new SampleYSymbolicDataset("BY Sample", 40, sData, 4, 20,
                new String[] {"B Fall", "B Spring", "B Summer", "B Winter"});
        return data;

    }

    /**
     * Starting point for the demonstration application.
     *
     * @param args  ignored.
     */
    public static void main(String[] args) {

        SymbolicChartDemo1 demo = new SymbolicChartDemo1("Symbolic Chart Demo 1");
        demo.pack();
        RefineryUtilities.centerFrameOnScreen(demo);
        demo.setVisible(true);

    }

}

