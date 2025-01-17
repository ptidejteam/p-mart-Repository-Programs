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
 * ----------------------
 * SymbolicChartDemo.java
 * ----------------------
 * (C) Copyright 2003, by Object Refinery Limited and Contributors.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited);
 * Contributor(s):   -;
 *
 * $Id: SymbolicChartDemo1.java,v 1.1 2007/10/10 19:12:16 vauchers Exp $
 *
 * Changes
 * -------
 * 14-Feb-2003 : Version 1 (DG);
 *
 */

package org.jfree.chart.demo;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.SymbolicAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.labels.SymbolicXYToolTipGenerator;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.StandardXYItemRenderer;
import org.jfree.chart.renderer.XYItemRenderer;
import org.jfree.data.XYDataset;
import org.jfree.data.YisSymbolic;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;

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

        ValueAxis domainAxis = new NumberAxis("X");
        SymbolicAxis symbolicAxis
            = new SymbolicAxis("Y", ((YisSymbolic) dataset).getYSymbolicValues());

        XYPlot plot = new XYPlot(dataset, domainAxis, symbolicAxis, null);
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

