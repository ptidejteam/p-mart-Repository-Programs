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
 * SurveyResultsDemo.java
 * ----------------------
 * (C) Copyright 2003, by Object Refinery Limited and Contributors.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited);
 * Contributor(s):   -;
 *
 * $Id: SurveyResultsDemo.java,v 1.1 2007/10/10 19:09:08 vauchers Exp $
 *
 * Changes
 * -------
 * 04-Jul-2003 : Version 1 (DG);
 *
 */
package org.jfree.chart.demo;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.TextTitle;
import org.jfree.chart.annotations.CategoryTextAnnotation;
import org.jfree.chart.axis.CategoryAnchor;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.CategoryItemRenderer;
import org.jfree.chart.renderer.ItemLabelAnchor;
import org.jfree.data.CategoryDataset;
import org.jfree.data.DefaultCategoryDataset;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;
import org.jfree.ui.TextAnchor;

/**
 * A horizontal bar chart showing survey results.
 * 
 * @author David Gilbert
 */
public class SurveyResultsDemo extends ApplicationFrame {

    /**
     * Creates a new demo.
     *
     * @param title  the frame title.
     */
    public SurveyResultsDemo(String title) {

        super(title);

        CategoryDataset dataset = createDataset();
        JFreeChart chart = createChart(dataset);
 
        // add the chart to a panel...
        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new java.awt.Dimension(700, 600));
        setContentPane(chartPanel);

    }
    
    /**
     * Creates a dataset.
     * 
     * @return The dataset.
     */
    private CategoryDataset createDataset() {
        
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        dataset.addValue(2.01, "Results", "Category 1");
        dataset.addValue(2.02, "Results", "Category 2");
        dataset.addValue(2.00, "Results", "Category 3");
        dataset.addValue(1.97, "Results", "Category 4");
        dataset.addValue(1.44, "Results", "Category 5");
        dataset.addValue(1.49, "Results", "Category 6");
        dataset.addValue(1.49, "Results", "Category 7");
        dataset.addValue(1.48, "Results", "Category 8");
        dataset.addValue(4.26, "Results", "Category 9");
        dataset.addValue(4.08, "Results", "Category 10");
        dataset.addValue(4.03, "Results", "Category 11");
        dataset.addValue(3.92, "Results", "Category 12");
        dataset.addValue(3.99, "Results", "Category 13");
        dataset.addValue(2.23, "Results", "Category 14");
        dataset.addValue(2.60, "Results", "Overall");
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
        TextTitle title = new TextTitle("Figure 7 | I. Resources - The site offers users relevant, "
                                        + "informative and educational resources");
        title.setHorizontalAlignment(TextTitle.LEFT);
        title.setBackgroundPaint(Color.red);
        title.setPaint(Color.white);
        
        chart.setTitle(title);
        CategoryPlot plot = chart.getCategoryPlot();
        plot.setDomainGridlinesVisible(true);
        plot.setDomainGridlinePosition(CategoryAnchor.END);
        plot.setDomainGridlineStroke(new BasicStroke(0.5f));
        plot.setDomainGridlinePaint(Color.black);
        plot.clearRangeMarkers();
        
        CategoryAxis domainAxis = plot.getDomainAxis();
        domainAxis.setVisible(false);
        domainAxis.setCategoryMargin(0.50);
        
        plot.getRangeAxis().setVisible(false);
        
        CategoryItemRenderer renderer = plot.getRenderer();
        renderer.setSeriesPaint(0, new Color(0x9C, 0xA4, 0x4A));
        renderer.setOutlineStroke(null);
        renderer.setBaseOutlineStroke(null);
        
        renderer.setItemLabelsVisible(Boolean.TRUE);
        renderer.setItemLabelAnchor(ItemLabelAnchor.INSIDE12);
        renderer.setItemLabelTextAnchor(TextAnchor.CENTER_RIGHT);
        renderer.setItemLabelFont(new Font("SansSerif", Font.BOLD, 10));
        
        CategoryTextAnnotation a1 = new CategoryTextAnnotation("1. White papers are available.", 
            "Category 1", 0.0);
        a1.setFont(new Font("SansSerif", Font.BOLD, 12));
        a1.setTextAnchor(TextAnchor.BOTTOM_LEFT);
        a1.setCategoryAnchor(CategoryAnchor.START);
        plot.addAnnotation(a1);
        
        CategoryTextAnnotation a2 = new CategoryTextAnnotation("2. White papers enhance users "
            + "understanding of the firm and its expertise.", "Category 2", 0.0);
        a2.setFont(new Font("SansSerif", Font.PLAIN, 12));
        a2.setTextAnchor(TextAnchor.BOTTOM_LEFT);
        a2.setCategoryAnchor(CategoryAnchor.START);
        plot.addAnnotation(a2);

        CategoryTextAnnotation a3 = new CategoryTextAnnotation("3. White papers are relevant to "
            + "the firm's prospects and clients.", "Category 3", 0.0);
        a3.setFont(new Font("SansSerif", Font.PLAIN, 12));
        a3.setTextAnchor(TextAnchor.BOTTOM_LEFT);
        a3.setCategoryAnchor(CategoryAnchor.START);
        plot.addAnnotation(a3);

        CategoryTextAnnotation a4 = new CategoryTextAnnotation("4. White papers are relevant to "
            + "the firm's positioning.", "Category 4", 0.0);
        a4.setFont(new Font("SansSerif", Font.PLAIN, 12));
        a4.setTextAnchor(TextAnchor.BOTTOM_LEFT);
        a4.setCategoryAnchor(CategoryAnchor.START);
        plot.addAnnotation(a4);

        CategoryTextAnnotation a5 = new CategoryTextAnnotation("5. Case studies are available.", 
            "Category 5", 0.0);
        a5.setFont(new Font("SansSerif", Font.BOLD, 12));
        a5.setTextAnchor(TextAnchor.BOTTOM_LEFT);
        a5.setCategoryAnchor(CategoryAnchor.START);
        plot.addAnnotation(a5);
        
        CategoryTextAnnotation a6 = new CategoryTextAnnotation("6. Case studies enhance users "
            + "understanding of the firm and its expertise.", "Category 6", 0.0);
        a6.setFont(new Font("SansSerif", Font.PLAIN, 12));
        a6.setTextAnchor(TextAnchor.BOTTOM_LEFT);
        a6.setCategoryAnchor(CategoryAnchor.START);
        plot.addAnnotation(a6);

        CategoryTextAnnotation a7 = new CategoryTextAnnotation("7. Case studies are relevant to "
            + "the firm's prospects and clients.", "Category 7", 0.0);
        a7.setFont(new Font("SansSerif", Font.PLAIN, 12));
        a7.setTextAnchor(TextAnchor.BOTTOM_LEFT);
        a7.setCategoryAnchor(CategoryAnchor.START);
        plot.addAnnotation(a7);

        CategoryTextAnnotation a8 = new CategoryTextAnnotation("8. White papers are relevant to "
            + "the firm's positioning.", "Category 8", 0.0);
        a8.setFont(new Font("SansSerif", Font.PLAIN, 12));
        a8.setTextAnchor(TextAnchor.BOTTOM_LEFT);
        a8.setCategoryAnchor(CategoryAnchor.START);
        plot.addAnnotation(a8);

        CategoryTextAnnotation a9 = new CategoryTextAnnotation("9. Case studies are available.", 
            "Category 9", 0.0);
        a9.setFont(new Font("SansSerif", Font.BOLD, 12));
        a9.setTextAnchor(TextAnchor.BOTTOM_LEFT);
        a9.setCategoryAnchor(CategoryAnchor.START);
        plot.addAnnotation(a9);
        
        CategoryTextAnnotation a10 = new CategoryTextAnnotation("10. Case studies enhance users "
            + "understanding of the firm and its expertise.", "Category 10", 0.0);
        a10.setFont(new Font("SansSerif", Font.PLAIN, 12));
        a10.setTextAnchor(TextAnchor.BOTTOM_LEFT);
        a10.setCategoryAnchor(CategoryAnchor.START);
        plot.addAnnotation(a10);

        CategoryTextAnnotation a11 = new CategoryTextAnnotation("11. Case studies are relevant "
            + "to the firm's prospects and clients.", "Category 11", 0.0);
        a11.setFont(new Font("SansSerif", Font.PLAIN, 12));
        a11.setTextAnchor(TextAnchor.BOTTOM_LEFT);
        a11.setCategoryAnchor(CategoryAnchor.START);
        plot.addAnnotation(a11);

        CategoryTextAnnotation a12 = new CategoryTextAnnotation("12. White papers are relevant "
            + "to the firm's positioning.", "Category 12", 0.0);
        a12.setFont(new Font("SansSerif", Font.PLAIN, 12));
        a12.setTextAnchor(TextAnchor.BOTTOM_LEFT);
        a12.setCategoryAnchor(CategoryAnchor.START);
        plot.addAnnotation(a12);

        CategoryTextAnnotation a13 = new CategoryTextAnnotation("13. Users can easily access "
            + "resources based on viewer interest.", "Category 13", 0.0);
        a13.setFont(new Font("SansSerif", Font.BOLD, 12));
        a13.setTextAnchor(TextAnchor.BOTTOM_LEFT);
        a13.setCategoryAnchor(CategoryAnchor.START);
        plot.addAnnotation(a13);

        CategoryTextAnnotation a14 = new CategoryTextAnnotation("14. Access to additional "
            + "hyperlinks enhances users's ability to find relevant information.", 
            "Category 14", 0.0);
        a14.setFont(new Font("SansSerif", Font.BOLD, 12));
        a14.setTextAnchor(TextAnchor.BOTTOM_LEFT);
        a14.setCategoryAnchor(CategoryAnchor.START);
        plot.addAnnotation(a14);

        CategoryTextAnnotation a15 = new CategoryTextAnnotation("15. OVERALL EFFECTIVENESS.", 
            "Overall", 0.0);
        a15.setFont(new Font("SansSerif", Font.BOLD, 12));
        a15.setTextAnchor(TextAnchor.BOTTOM_LEFT);
        a15.setCategoryAnchor(CategoryAnchor.START);
        plot.addAnnotation(a15);

        return chart;
       
    }

    /**
     * Starting point for the demonstration application.
     *
     * @param args  ignored.
     */
    public static void main(String[] args) {

        SurveyResultsDemo demo = new SurveyResultsDemo("Survey Results Demo");
        demo.pack();
        RefineryUtilities.centerFrameOnScreen(demo);
        demo.setVisible(true);

    }


}
