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
 * ------------------
 * DualAxisDemo3.java
 * ------------------
 * (C) Copyright 2002, 2003 by Simba Management Limited and Contributors.
 *
 * Original Author:  David Gilbert (for Simba Management Limited);
 * Contributor(s):   -;
 *
 * $Id: DualAxisDemo3.java,v 1.1 2007/10/10 19:57:52 vauchers Exp $
 *
 * Changes
 * -------
 * 19-Nov-2002 : Version 1 (DG);
 *
 */

package com.jrefinery.chart.demo;

import java.awt.Color;
import com.jrefinery.chart.JFreeChart;
import com.jrefinery.chart.Legend;
import com.jrefinery.chart.ChartFactory;
import com.jrefinery.chart.ChartPanel;
import com.jrefinery.chart.axis.Axis;
import com.jrefinery.chart.axis.ValueAxis;
import com.jrefinery.chart.axis.HorizontalNumberAxis;
import com.jrefinery.chart.plot.CategoryPlot;
import com.jrefinery.chart.renderer.CategoryItemRenderer;
import com.jrefinery.chart.renderer.DefaultDrawingSupplier;
import com.jrefinery.chart.renderer.HorizontalShapeRenderer;
import com.jrefinery.chart.renderer.DrawingSupplier;
import com.jrefinery.data.CategoryDataset;
import com.jrefinery.data.DefaultCategoryDataset;
import com.jrefinery.ui.ApplicationFrame;
import com.jrefinery.ui.RefineryUtilities;

/**
 * A simple demonstration application showing how to create a dual axis chart based on data
 * from two {@link TableDataset} instances.
 *
 * @author David Gilbert
 */
public class DualAxisDemo3 extends ApplicationFrame {

    /**
     * Creates a new demo instance.
     *
     * @param title  the frame title.
     */
    public DualAxisDemo3(String title) {

        super(title);

        CategoryDataset dataset1 = createDataset1();

        // create the chart...
        JFreeChart chart = ChartFactory.createHorizontalBarChart(
                                                     "Dual Axis Chart",  // chart title
                                                     "Category",         // domain axis label
                                                     "Value",            // range axis label
                                                     dataset1,           // data
                                                     true,               // include legend
                                                     true,
                                                     false
                                                 );

        // NOW DO SOME OPTIONAL CUSTOMISATION OF THE CHART...

        DrawingSupplier supplier = new DefaultDrawingSupplier();

        // set the background color for the chart...
        chart.setBackgroundPaint(Color.yellow);
        chart.getLegend().setAnchor(Legend.WEST);

        // get a reference to the plot for further customisation...
        CategoryPlot plot = chart.getCategoryPlot();
        plot.setDomainAxisLocation(Axis.RIGHT);
        plot.getRenderer().setDrawingSupplier(supplier);
        CategoryDataset dataset2 = createDataset2();
        ValueAxis axis2 = new HorizontalNumberAxis("Secondary");
        plot.setSecondaryRangeAxis(axis2);
        plot.setSecondaryDataset(dataset2);
        CategoryItemRenderer renderer2 = new HorizontalShapeRenderer();
        renderer2.setDrawingSupplier(supplier);
        plot.setSecondaryRenderer(renderer2);

        // OPTIONAL CUSTOMISATION COMPLETED.

        // add the chart to a panel...
        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new java.awt.Dimension(500, 270));
        setContentPane(chartPanel);

    }

    /**
     * Creates a sample dataset.
     * 
     * @return  The dataset.
     */
    private CategoryDataset createDataset1() {

        // row keys...
        String series1 = "First";
        String series2 = "Second";
        String series3 = "Third";

        // column keys...
        String category1 = "Category 1";
        String category2 = "Category 2";
        String category3 = "Category 3";
        String category4 = "Category 4";
        String category5 = "Category 5";
        String category6 = "Category 6";
        String category7 = "Category 7";
        String category8 = "Category 8";

        // create the dataset...
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        dataset.addValue(1.0, series1, category1);
        dataset.addValue(4.0, series1, category2);
        dataset.addValue(3.0, series1, category3);
        dataset.addValue(5.0, series1, category4);
        dataset.addValue(5.0, series1, category5);
        dataset.addValue(7.0, series1, category6);
        dataset.addValue(7.0, series1, category7);
        dataset.addValue(8.0, series1, category8);

        dataset.addValue(5.0, series2, category1);
        dataset.addValue(7.0, series2, category2);
        dataset.addValue(6.0, series2, category3);
        dataset.addValue(8.0, series2, category4);
        dataset.addValue(4.0, series2, category5);
        dataset.addValue(4.0, series2, category6);
        dataset.addValue(2.0, series2, category7);
        dataset.addValue(1.0, series2, category8);

        dataset.addValue(4.0, series3, category1);
        dataset.addValue(3.0, series3, category2);
        dataset.addValue(2.0, series3, category3);
        dataset.addValue(3.0, series3, category4);
        dataset.addValue(6.0, series3, category5);
        dataset.addValue(3.0, series3, category6);
        dataset.addValue(4.0, series3, category7);
        dataset.addValue(3.0, series3, category8);

        return dataset;

    }

    /**
     * Creates a sample dataset.
     * 
     * @return  The dataset.
     */
    private CategoryDataset createDataset2() {

        // row keys...
        String series1 = "Fourth";

        // column keys...
        String category1 = "Category 1";
        String category2 = "Category 2";
        String category3 = "Category 3";
        String category4 = "Category 4";
        String category5 = "Category 5";
        String category6 = "Category 6";
        String category7 = "Category 7";
        String category8 = "Category 8";

        // create the dataset...
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        dataset.addValue(15.0, series1, category1);
        dataset.addValue(24.0, series1, category2);
        dataset.addValue(31.0, series1, category3);
        dataset.addValue(25.0, series1, category4);
        dataset.addValue(56.0, series1, category5);
        dataset.addValue(37.0, series1, category6);
        dataset.addValue(77.0, series1, category7);
        dataset.addValue(18.0, series1, category8);

        return dataset;

    }

    /**
     * Starting point for the demonstration application.
     *
     * @param args  ignored.
     */
    public static void main(String[] args) {

        DualAxisDemo3 demo = new DualAxisDemo3("Dual Axis Demo 3");
        demo.pack();
        RefineryUtilities.centerFrameOnScreen(demo);
        demo.setVisible(true);

    }

}
