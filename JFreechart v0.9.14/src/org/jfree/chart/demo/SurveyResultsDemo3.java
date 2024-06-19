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
 * -----------------------
 * SurveyResultsDemo3.java
 * -----------------------
 * (C) Copyright 2003, by Object Refinery Limited and Contributors.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited);
 * Contributor(s):   -;
 *
 * $Id: SurveyResultsDemo3.java,v 1.1 2007/10/10 19:19:01 vauchers Exp $
 *
 * Changes
 * -------
 * 31-Oct-2003 : Version 1 (DG);
 *
 */
package org.jfree.chart.demo;

import java.awt.Color;
import java.awt.Font;
import java.text.DecimalFormat;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.TextTitle;
import org.jfree.chart.axis.CategoryLabelPosition;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.labels.StandardCategoryItemLabelGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.BarRenderer;
import org.jfree.chart.renderer.ItemLabelAnchor;
import org.jfree.chart.renderer.ItemLabelPosition;
import org.jfree.data.CategoryDataset;
import org.jfree.data.DefaultCategoryDataset;
import org.jfree.text.TextBlockAnchor;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RectangleAnchor;
import org.jfree.ui.RefineryUtilities;
import org.jfree.ui.TextAnchor;

/**
 * A chart showing...
 * 
 * @author David Gilbert
 */
public class SurveyResultsDemo3 extends ApplicationFrame {

    /**
     * Creates a new demo.
     *
     * @param title  the frame title.
     */
    public SurveyResultsDemo3(String title) {

        super(title);

        CategoryDataset dataset = createDataset();
        JFreeChart chart = createChart(dataset);
 
        // add the chart to a panel...
        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new java.awt.Dimension(300, 270));
        setContentPane(chartPanel);

    }
    
    /**
     * Creates a dataset.
     * 
     * @return The dataset.
     */
    private CategoryDataset createDataset() {
        
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        dataset.addValue(2.61, "Results", "Sm.");
        dataset.addValue(2.70, "Results", "Med.");
        dataset.addValue(2.90, "Results", "Lg.");
        dataset.addValue(2.74, "Results", "All");
        return dataset;

    }
    
    /**
     * Creates a chart.
     * 
     * @param dataset  the dataset.
     * 
     * @return The chart.
     */
    private JFreeChart createChart(CategoryDataset dataset) {

        JFreeChart chart = ChartFactory.createBarChart(
            null,                         // chart title
            null,                         // domain axis label
            null,                         // range axis label
            dataset,                      // data
            PlotOrientation.HORIZONTAL,
            false,                        // include legend
            true,
            false
        );
        
        chart.setBackgroundPaint(Color.white);
        chart.getPlot().setOutlinePaint(null);
        TextTitle title = new TextTitle("Figure 6 | Overall SEO Rating");
        title.setHorizontalAlignment(TextTitle.LEFT);
        title.setBackgroundPaint(Color.red);
        title.setPaint(Color.white);
        
        chart.setTitle(title);
        CategoryPlot plot = chart.getCategoryPlot();
        
        ValueAxis rangeAxis = plot.getRangeAxis();
        rangeAxis.setRange(0.0, 4.0);
        rangeAxis.setVisible(false);
        
        ExtendedCategoryAxis domainAxis = new ExtendedCategoryAxis(null);
        domainAxis.setTickLabelFont(new Font("SansSerif", Font.BOLD, 12));
        domainAxis.setCategoryMargin(0.30);        
        domainAxis.addSubLabel("Sm.", "(10)");
        domainAxis.addSubLabel("Med.", "(10)");
        domainAxis.addSubLabel("Lg.", "(10)");
        domainAxis.addSubLabel("All", "(10)");
        CategoryLabelPosition p = new CategoryLabelPosition( 
            RectangleAnchor.LEFT, TextBlockAnchor.CENTER_LEFT, TextAnchor.CENTER_LEFT, 0.0
        );
        domainAxis.setLeftCategoryLabelPosition(p);
        plot.setDomainAxis(domainAxis);
        
        BarRenderer renderer = (BarRenderer) plot.getRenderer();
        renderer.setSeriesPaint(0, new Color(0x9C, 0xA4, 0x4A));
        renderer.setBaseOutlineStroke(null);
        
        StandardCategoryItemLabelGenerator generator = new StandardCategoryItemLabelGenerator(
            new DecimalFormat("0.00")
        );
        renderer.setItemLabelGenerator(generator);
        renderer.setItemLabelsVisible(true);
        renderer.setItemLabelFont(new Font("SansSerif", Font.PLAIN, 18));
        ItemLabelPosition position = new ItemLabelPosition(
            ItemLabelAnchor.INSIDE3, TextAnchor.CENTER_RIGHT, TextAnchor.CENTER_RIGHT, 0.0    
        );
        renderer.setPositiveItemLabelPosition(position);
        renderer.setPositiveItemLabelPositionFallback(new ItemLabelPosition());
        
        return chart;
       
    }

    /**
     * Starting point for the demonstration application.
     *
     * @param args  ignored.
     */
    public static void main(String[] args) {

        SurveyResultsDemo3 demo = new SurveyResultsDemo3("Survey Results Demo 3");
        demo.pack();
        RefineryUtilities.centerFrameOnScreen(demo);
        demo.setVisible(true);

    }

}
