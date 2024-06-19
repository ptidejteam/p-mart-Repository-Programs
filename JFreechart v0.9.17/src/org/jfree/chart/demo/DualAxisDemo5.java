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
 * ------------------
 * DualAxisDemo5.java
 * ------------------
 * (C) Copyright 2002-2004, by Object Refinery Limited and Contributors.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited);
 * Contributor(s):   -;
 *
 * $Id: DualAxisDemo5.java,v 1.1 2007/10/10 19:29:07 vauchers Exp $
 *
 * Changes
 * -------
 * 19-Sep-2003 : Version 1 (DG);
 * 06-Feb-2004 : Modified to correct legend (DG);
 *
 */

package org.jfree.chart.demo;

import java.awt.Color;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.Legend;
import org.jfree.chart.LegendItem;
import org.jfree.chart.LegendItemCollection;
import org.jfree.chart.axis.AxisLocation;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.renderer.BarRenderer;
import org.jfree.chart.renderer.CategoryItemRenderer;
import org.jfree.data.CategoryDataset;
import org.jfree.data.DefaultCategoryDataset;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;

/**
 * This demo shows how to create a dual axis bar chart.  A workaround is used because the
 * {@link BarRenderer} and {@link CategoryAxis} classes will overlap the bars for the two 
 * datasets - to get around this, an an additional series (containing 'null' values) is added 
 * to each dataset, and the getLegendItems() method in the plot is overridden.
 *
 * @author David Gilbert
 */
public class DualAxisDemo5 extends ApplicationFrame {

    /**
     * Creates a new demo instance.
     *
     * @param title  the frame title.
     */
    public DualAxisDemo5(String title) {
        super(title);
        CategoryDataset dataset1 = createDataset1();
        CategoryDataset dataset2 = createDataset2();
        JFreeChart chart = createChart(dataset1, dataset2);
        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new java.awt.Dimension(500, 270));
        setContentPane(chartPanel);
    }

    // ****************************************************************************
    // * JFREECHART DEVELOPER GUIDE                                               *
    // * The JFreeChart Developer Guide, written by David Gilbert, is available   *
    // * to purchase from Object Refinery Limited:                                *
    // *                                                                          *
    // * http://www.object-refinery.com/jfreechart/guide.html                     *
    // *                                                                          *
    // * Sales are used to provide funding for the JFreeChart project - please    * 
    // * support us so that we can continue developing free software.             *
    // ****************************************************************************
    
    /**
     * Creates a sample dataset.
     *
     * @return  The dataset.
     */
    private CategoryDataset createDataset1() {

        // row keys...
        String series1 = "Series 1";
        String series2 = "Dummy 1";

        // column keys...
        String category1 = "Category 1";
        String category2 = "Category 2";
        String category3 = "Category 3";
        String category4 = "Category 4";

        // create the dataset...
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        dataset.addValue(1.0, series1, category1);
        dataset.addValue(4.0, series1, category2);
        dataset.addValue(3.0, series1, category3);
        dataset.addValue(5.0, series1, category4);

        dataset.addValue(null, series2, category1);
        dataset.addValue(null, series2, category2);
        dataset.addValue(null, series2, category3);
        dataset.addValue(null, series2, category4);

        return dataset;

    }

    /**
     * Creates a sample dataset.
     *
     * @return  The dataset.
     */
    private CategoryDataset createDataset2() {

        // row keys...
        String series1 = "Dummy 2";
        String series2 = "Series 2";

        // column keys...
        String category1 = "Category 1";
        String category2 = "Category 2";
        String category3 = "Category 3";
        String category4 = "Category 4";

        // create the dataset...
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        dataset.addValue(null, series1, category1);
        dataset.addValue(null, series1, category2);
        dataset.addValue(null, series1, category3);
        dataset.addValue(null, series1, category4);

        dataset.addValue(75.0, series2, category1);
        dataset.addValue(87.0, series2, category2);
        dataset.addValue(96.0, series2, category3);
        dataset.addValue(68.0, series2, category4);

        return dataset;

    }

    /**
     * Creates a chart.
     * 
     * @param dataset1  the first dataset.
     * @param dataset2  the second dataset.
     * 
     * @return A chart.
     */
    private JFreeChart createChart(CategoryDataset dataset1, CategoryDataset dataset2) {

        CategoryAxis domainAxis = new CategoryAxis("Category");
        NumberAxis rangeAxis = new NumberAxis("Value");
        BarRenderer renderer1 = new BarRenderer();
        CategoryPlot plot = new CategoryPlot(dataset1, domainAxis, rangeAxis, renderer1) {
            
            /**
             * Override the getLegendItems() method to handle special case.
             *
             * @return the legend items.
             */
            public LegendItemCollection getLegendItems() {

                LegendItemCollection result = new LegendItemCollection();

                CategoryDataset data = getDataset();
                if (data != null) {
                    CategoryItemRenderer r = getRenderer();
                    if (r != null) {
                        LegendItem item = r.getLegendItem(0, 0);
                        result.add(item);
                    }
                }

                // the JDK 1.2.2 compiler complained about the name of this
                // variable 
                CategoryDataset dset2 = getSecondaryDataset(0);
                if (dset2 != null) {
                    CategoryItemRenderer renderer2 = getSecondaryRenderer(0);
                    if (renderer2 != null) {
                        LegendItem item = renderer2.getLegendItem(1, 1);
                        result.add(item);
                    }
                }

                return result;

            }
            
        };
        
        JFreeChart chart = new JFreeChart("Dual Axis Bar Chart", plot);
        chart.setBackgroundPaint(Color.white);
        chart.getLegend().setAnchor(Legend.SOUTH);
        plot.setBackgroundPaint(new Color(0xEE, 0xEE, 0xFF));
        plot.setDomainAxisLocation(AxisLocation.BOTTOM_OR_RIGHT);
        plot.setSecondaryDataset(0, dataset2);
        plot.mapSecondaryDatasetToRangeAxis(0, new Integer(0));
        ValueAxis axis2 = new NumberAxis("Secondary");
        plot.setSecondaryRangeAxis(0, axis2);
        plot.setSecondaryRangeAxisLocation(0, AxisLocation.BOTTOM_OR_RIGHT);
        BarRenderer renderer2 = new BarRenderer();
        plot.setSecondaryRenderer(0, renderer2);
        
        return chart;
    }
    
    /**
     * Starting point for the demonstration application.
     *
     * @param args  ignored.
     */
    public static void main(String[] args) {

        DualAxisDemo5 demo = new DualAxisDemo5("Dual Axis Demo 5");
        demo.pack();
        RefineryUtilities.centerFrameOnScreen(demo);
        demo.setVisible(true);

    }

}
