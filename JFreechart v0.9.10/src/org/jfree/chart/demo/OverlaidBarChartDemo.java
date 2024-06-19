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
 * -------------------------
 * OverlaidBarChartDemo.java
 * -------------------------
 * (C) Copyright 2002, 2003, by Object Refinery Limited.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited);
 * Contributor(s):   -;
 *
 * $Id: OverlaidBarChartDemo.java,v 1.1 2007/10/10 19:04:55 vauchers Exp $
 *
 * Changes
 * -------
 * 20-Sep-2002 : Version 1 (DG);
 * 11-Oct-2002 : Added tooltips, modified series colors, centered frame on screen (DG);
 * 11-Feb-2003 : Fixed bug where category labels were not showing on the axis (DG);
 *
 */

package org.jfree.chart.demo;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.StandardLegend;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.labels.CategoryItemLabelGenerator;
import org.jfree.chart.labels.StandardCategoryItemLabelGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.BarRenderer;
import org.jfree.chart.renderer.CategoryItemRenderer;
import org.jfree.chart.renderer.LineAndShapeRenderer;
import org.jfree.data.CategoryDataset;
import org.jfree.data.DatasetUtilities;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;

/**
 * A simple demonstration application showing how to create a bar chart overlaid
 * with a line chart.  From version 0.9.10 onwards, this no longer uses the 
 * <code>OverlaidCategoryPlot</code> class as this has been removed.  Instrad, multiple datasets
 * and renderers are added to the standard {@link CategoryPlot} class.
 *
 * @author David Gilbert
 */
public class OverlaidBarChartDemo extends ApplicationFrame {

    /**
     * Default constructor.
     *
     * @param  title the frame title.
     */
    public OverlaidBarChartDemo(String title) {

        super(title);

        // create the first dataset...
        double[][] data1 = new double[][] {
            {1.0, 4.0, 3.0, 5.0, 5.0, 7.0, 7.0, 8.0},
            {5.0, 7.0, 6.0, 8.0, 4.0, 4.0, 2.0, 1.0}
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
        plot.setRenderer(new BarRenderer());
        
        CategoryAxis domainAxis = new CategoryAxis("Category");
        domainAxis.setVerticalCategoryLabels(true);
        plot.setDomainAxis(new CategoryAxis("Category"));
        plot.setRangeAxis(new NumberAxis("Value"));

        plot.setOrientation(PlotOrientation.VERTICAL);
        plot.setRangeGridlinesVisible(true);
        plot.setDomainGridlinesVisible(true);

        ValueAxis rangeAxis2 = new NumberAxis("Axis 2");
        plot.setSecondaryRangeAxis(0, rangeAxis2);

        // create the second dataset...
        double[][] data2 = new double[][] {
            {9.0, 7.0, 2.0, 6.0, 6.0, 9.0, 5.0, 4.0}
        };
        CategoryDataset dataset2 = DatasetUtilities.createCategoryDataset("T", "Category", data2);

        // create the second plot...
        CategoryItemRenderer renderer2 = new LineAndShapeRenderer();
        plot.setSecondaryDataset(0, dataset2);
        plot.setSecondaryRenderer(0, renderer2);

        // create the third dataset...
        double[][] data3 = new double[][] {
            {94.0, 75.0, 22.0, 74.0, 83.0, 9.0, 23.0, 98.0}
        };
        CategoryDataset dataset3 = DatasetUtilities.createCategoryDataset("R", "Category", data3);
        plot.setSecondaryDataset(1, dataset3);
        plot.setSecondaryRenderer(1, new LineAndShapeRenderer());

        JFreeChart chart = new JFreeChart(plot);
        chart.setTitle("Overlaid Bar Chart");
        chart.setLegend(new StandardLegend(chart));

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

        OverlaidBarChartDemo demo = new OverlaidBarChartDemo("Overlaid Bar Chart Demo");
        demo.pack();
        RefineryUtilities.centerFrameOnScreen(demo);
        demo.setVisible(true);

    }

}
