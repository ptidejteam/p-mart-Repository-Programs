/* ===========================================================
 * JFreeChart : a free chart library for the Java(tm) platform
 * ===========================================================
 *
 * (C) Copyright 2000-2004, by Object Refinery Limited and Contributors.
 *
 * Project Info:  http://www.jfree.org/jfreechart/index.html
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
 * [Java is a trademark or registered trademark of Sun Microsystems, Inc. 
 * in the United States and other countries.]
 *
 * --------------------------
 * OverlaidBarChartDemo2.java
 * --------------------------
 * (C) Copyright 2004, by Object Refinery Limited.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited);
 * Contributor(s):   -;
 *
 * $Id: OverlaidBarChartDemo2.java,v 1.1 2007/10/10 19:25:26 vauchers Exp $
 *
 * Changes
 * -------
 * 09-Jan-2004 : Version 1 (DG);
 *
 */

package org.jfree.chart.demo;

import java.awt.BasicStroke;
import java.awt.Color;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.StandardLegend;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.labels.CategoryItemLabelGenerator;
import org.jfree.chart.labels.StandardCategoryItemLabelGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.DatasetRenderingOrder;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.BarRenderer;
import org.jfree.chart.renderer.CategoryItemRenderer;
import org.jfree.chart.renderer.LevelRenderer;
import org.jfree.data.CategoryDataset;
import org.jfree.data.DatasetUtilities;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;

/**
 * Another demo of an overlaid bar chart.
 *
 * @author David Gilbert
 */
public class OverlaidBarChartDemo2 extends ApplicationFrame {

    /**
     * Default constructor.
     *
     * @param  title the frame title.
     */
    public OverlaidBarChartDemo2(String title) {

        super(title);
        JFreeChart chart = createChart();
        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new java.awt.Dimension(500, 270));
        setContentPane(chartPanel);

    }
    
    /**
     * Creates a sample chart.
     * 
     * @return A sample chart.
     */
    private JFreeChart createChart() {
        
        // create the first dataset...
        double[][] data1 = new double[][] {
            {1.0, 4.0, 3.0, 5.0, 5.0},
            {5.0, 7.0, 6.0, 8.0, 4.0}
        };
        CategoryDataset dataset1 = DatasetUtilities.createCategoryDataset(
            "S", "Category ", data1
        );

        // create the first plot...
        CategoryItemLabelGenerator tooltips = new StandardCategoryItemLabelGenerator();
        CategoryItemRenderer renderer = new BarRenderer();
        
        renderer.setItemLabelGenerator(tooltips);
        CategoryPlot plot = new CategoryPlot();
        plot.setDataset(dataset1);
        plot.setRenderer(renderer);
        
        plot.setDomainAxis(new CategoryAxis("Category"));
        plot.setRangeAxis(new NumberAxis("Value"));

        plot.setOrientation(PlotOrientation.VERTICAL);
        plot.setRangeGridlinesVisible(true);
        plot.setDomainGridlinesVisible(true);

        // create the second dataset and renderer...
        double[][] data2 = new double[][] {
            {6.0, 7.0, 2.0, 6.0, 6.0},
            {4.0, 2.0, 1.0, 3.0, 2.0}
        };
        CategoryDataset dataset2 = DatasetUtilities.createCategoryDataset("Prior ", "Category", data2);
        CategoryItemRenderer renderer2 = new LevelRenderer();
        renderer2.setSeriesStroke(0, new BasicStroke(2.0f));
        renderer2.setSeriesStroke(1, new BasicStroke(2.0f));
        plot.setSecondaryDataset(0, dataset2);
        plot.setSecondaryRenderer(0, renderer2);

        plot.setDatasetRenderingOrder(DatasetRenderingOrder.REVERSE);
        plot.setBackgroundPaint(Color.lightGray);
        plot.setRangeGridlinePaint(Color.white);
        
        JFreeChart chart = new JFreeChart(plot);
        chart.setTitle("Overlaid Bar Chart");
        chart.setLegend(new StandardLegend());
        chart.setBackgroundPaint(Color.white);
        return chart;
    }

    // ****************************************************************************
    // * JFREECHART DEVELOPER GUIDE                                               *
    // * The JFreeChart Developer Guide, written by David Gilbert, is available   *
    // * to purchase from Object Refinery Limited:                                *
    // *                                                                          *
    // * http://www.object-refinery.com/jfreechart/guide.html                     *
    // *                                                                          *
    // * Sales are used to provide funding for the JFreeChart project - please    * 
    // * support us so that we can continue developing free software.             *                                             *
    // ****************************************************************************
    
    /**
     * Starting point for the demonstration application.
     *
     * @param args  ignored.
     */
    public static void main(String[] args) {

        OverlaidBarChartDemo2 demo = new OverlaidBarChartDemo2("Overlaid Bar Chart Demo 2");
        demo.pack();
        RefineryUtilities.centerFrameOnScreen(demo);
        demo.setVisible(true);

    }

}
