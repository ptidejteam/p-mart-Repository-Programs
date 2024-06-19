/* ======================================
 * JFreeChart : a free Java chart library
 * ======================================
 *
 * Project Info:  http://www.jfree.org/jfreechart/index.html
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
 * --------------------
 * ParetoChartDemo.java
 * --------------------
 * (C) Copyright 2003, by Simba Management Limited.
 *
 * Original Author:  David Gilbert (for Simba Management Limited).
 * Contributor(s):   -;
 *
 * $Id: ParetoChartDemo.java,v 1.1 2007/10/10 20:03:18 vauchers Exp $
 *
 * Changes
 * -------
 * 05-Mar-2003 : Version 1 (DG);
 *
 */

package org.jfree.chart.demo;

import java.awt.Color;
import java.text.NumberFormat;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.TextTitle;
import org.jfree.chart.axis.HorizontalCategoryAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.VerticalNumberAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.renderer.DefaultDrawingSupplier;
import org.jfree.chart.renderer.DrawingSupplier;
import org.jfree.chart.renderer.LineAndShapeRenderer;
import org.jfree.data.CategoryDataset;
import org.jfree.data.DataUtilities;
import org.jfree.data.DatasetUtilities;
import org.jfree.data.DefaultKeyedValues;
import org.jfree.data.KeyedValues;
import org.jfree.data.SortOrder;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;

/**
 * A demo showing the creation of a pareto chart.
 * 
 * @author David Gilbert
 */
public class ParetoChartDemo extends ApplicationFrame {

    /**
     * Creates a new demo instance.
     *
     * @param title  the frame title.
     */
    public ParetoChartDemo(String title) {

        super(title);

        DefaultKeyedValues data = new DefaultKeyedValues();
        data.addValue("C", new Integer(4843));
        data.addValue("C++", new Integer(2098));
        data.addValue("C#", new Integer(26));
        data.addValue("Java", new Integer(1901));
        data.addValue("Perl", new Integer(2507));
        data.addValue("PHP", new Integer(1689));
        data.addValue("Python", new Integer(948));
        data.addValue("Ruby", new Integer(100));
        data.addValue("SQL", new Integer(263));
        data.addValue("Unix Shell", new Integer(485));

        data.sortByValues(SortOrder.DESCENDING);
        KeyedValues cumulative = DataUtilities.getCumulativePercentages(data);
		CategoryDataset dataset = DatasetUtilities.createCategoryDataset("Languages", data);
        
        // create the chart...
        JFreeChart chart = ChartFactory.createVerticalBarChart(
                                                     "Freshmeat Software Projects",  // chart title
                                                     "Language",            // domain axis label
                                                     "Projects",            // range axis label
                                                     dataset,               // data
                                                     true,                  // include legend
                                                     true,
                                                     false
                                                 );

        // NOW DO SOME OPTIONAL CUSTOMISATION OF THE CHART...
        chart.addSubtitle(new TextTitle("By Programming Language"));
        chart.addSubtitle(new TextTitle("As at 5 March 2003"));
        DrawingSupplier supplier = new DefaultDrawingSupplier();

        // set the background color for the chart...
        chart.setBackgroundPaint(new Color(0xBBBBDD));

        // get a reference to the plot for further customisation...
        CategoryPlot plot = chart.getCategoryPlot();
        plot.getRenderer().setDrawingSupplier(supplier);
        
        // skip some labels if they overlap...
        HorizontalCategoryAxis domainAxis = (HorizontalCategoryAxis) plot.getDomainAxis();
        domainAxis.setLowerMargin(0.02);
        domainAxis.setUpperMargin(0.02);
        //domainAxis.setSkipCategoryLabelsToFit(true);

        // set the range axis to display integers only...
        NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
        rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());

        LineAndShapeRenderer renderer2 = new LineAndShapeRenderer();
        renderer2.setDrawingSupplier(supplier);
        
		CategoryDataset dataset2 = DatasetUtilities.createCategoryDataset("Cumulative", cumulative);
        NumberAxis axis2 = new VerticalNumberAxis("Percent");
        axis2.setNumberFormatOverride(NumberFormat.getPercentInstance());
        plot.setSecondaryRangeAxis(axis2);
        plot.setSecondaryDataset(dataset2);
        plot.setSecondaryRenderer(renderer2);

        // OPTIONAL CUSTOMISATION COMPLETED.

        // add the chart to a panel...
        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new java.awt.Dimension(550, 270));
        setContentPane(chartPanel);

    }

    /**
     * Starting point for the demonstration application.
     *
     * @param args  ignored.
     */
    public static void main(String[] args) {

        ParetoChartDemo demo = new ParetoChartDemo("Pareto Chart Demo");
        demo.pack();
        RefineryUtilities.centerFrameOnScreen(demo);
        demo.setVisible(true);

    }


}
