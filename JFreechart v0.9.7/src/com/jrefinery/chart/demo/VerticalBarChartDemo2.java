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
 * --------------------------
 * VerticalBarChartDemo2.java
 * --------------------------
 * (C) Copyright 2002, 2003, by Simba Management Limited and Contributors.
 *
 * Original Author:  David Gilbert (for Simba Management Limited);
 * Contributor(s):   -;
 *
 * $Id: VerticalBarChartDemo2.java,v 1.1 2007/10/10 20:00:13 vauchers Exp $
 *
 * Changes
 * -------
 * 22-Aug-2002 : Version 1 (DG);
 *
 */

package com.jrefinery.chart.demo;

import java.awt.Paint;
import java.awt.Color;

import com.jrefinery.chart.JFreeChart;
import com.jrefinery.chart.ChartFactory;
import com.jrefinery.chart.ChartPanel;
import com.jrefinery.chart.plot.CategoryPlot;
import com.jrefinery.chart.axis.ValueAxis;
import com.jrefinery.chart.axis.NumberAxis;
import com.jrefinery.chart.axis.HorizontalCategoryAxis;
import com.jrefinery.chart.renderer.CategoryItemRenderer;
import com.jrefinery.chart.renderer.VerticalBarRenderer;
import com.jrefinery.data.DatasetUtilities;
import com.jrefinery.data.CategoryDataset;
import com.jrefinery.ui.ApplicationFrame;
import com.jrefinery.ui.RefineryUtilities;

/**
 * A bar chart with just one series.  In this special case, it is possible to use multiple colors
 * for the items within the series.
 *
 * @author David Gilbert
 */
public class VerticalBarChartDemo2 extends ApplicationFrame {
    
    /**
     * A custom renderer that returns a different color for each item in a single series.
     */
    class CustomRenderer extends VerticalBarRenderer {
    
        /** The colors. */
        private Paint[] colors;
        
        /**
         * Creates a new renderer.
         * 
         * @param colors  the colors.
         */
        public CustomRenderer(Paint[] colors) {
            this.colors = colors;
        }
        
        /**
         * Returns the paint for an item.  Overrides the default behaviour inherited from
         * AbstractRenderer.
         * 
         * @param dataset  the dataset index.
         * @param row  the series.
         * @param column  the category.
         * 
         * @return The item color.
         */
        public Paint getItemPaint(int dataset, int row, int column) {
            return colors[column % colors.length];
        }
    }

    /**
     * Creates a new demo.
     *
     * @param title  the frame title.
     */
    public VerticalBarChartDemo2(String title) {

        super(title);

        // create a dataset...
        double[][] data = new double[][] {
            { 4.0, 3.0, 2.0, 3.0, 6.0, 3.0, 4.0, 3.0 }
        };

        CategoryDataset dataset = DatasetUtilities.createCategoryDataset("Series ", 
                                                                         "Category ", 
                                                                         data);

        // create the chart...
        JFreeChart chart = ChartFactory.createVerticalBarChart(
                                                     "Vertical Bar Chart",  // chart title
                                                     "Category",            // domain axis label
                                                     "Value",               // range axis label
                                                     dataset,               // data
                                                     false,                 // include legend
                                                     true,
                                                     false
                                                 );

        // NOW DO SOME OPTIONAL CUSTOMISATION OF THE CHART...

        // set the background color for the chart...
        chart.setBackgroundPaint(Color.lightGray);

        // get a reference to the plot for further customisation...
        CategoryPlot plot = chart.getCategoryPlot();
        plot.setNoDataMessage("NO DATA!");
        //plot.setBackgroundImage(JFreeChart.INFO.getLogo());
        plot.setValueLabelsVisible(true);
        
        CategoryItemRenderer renderer = new CustomRenderer(
            new Paint[] { Color.red, Color.blue, Color.green,
                          Color.yellow, Color.orange, Color.cyan,
                          Color.magenta, Color.blue });
        plot.setRenderer(renderer);                  
                          
        // change the category labels to vertical...
        HorizontalCategoryAxis domainAxis = (HorizontalCategoryAxis) plot.getDomainAxis();
        domainAxis.setSkipCategoryLabelsToFit(true);

        // change the margin at the top of the range axis...
        ValueAxis rangeAxis = plot.getRangeAxis();
        rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
        rangeAxis.setUpperMargin(0.10);

        // OPTIONAL CUSTOMISATION COMPLETED.

        // add the chart to a panel...
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

        VerticalBarChartDemo2 demo = new VerticalBarChartDemo2("Vertical Bar Chart Demo 2");
        demo.pack();
        RefineryUtilities.centerFrameOnScreen(demo);
        demo.setVisible(true);

    }

}
