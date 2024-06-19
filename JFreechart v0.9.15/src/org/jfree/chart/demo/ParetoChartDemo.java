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
 * --------------------
 * ParetoChartDemo.java
 * --------------------
 * (C) Copyright 2003, by Object Refinery Limited.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited).
 * Contributor(s):   -;
 *
 * $Id: ParetoChartDemo.java,v 1.1 2007/10/10 19:21:51 vauchers Exp $
 *
 * Changes
 * -------
 * 05-Mar-2003 : Version 1 (DG);
 * 27-Aug-2003 : Moved SortOrder from org.jfree.data --> org.jfree.util (DG);
 *
 */

package org.jfree.chart.demo;

import java.awt.Color;
import java.text.NumberFormat;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.TextTitle;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.DatasetRenderingOrder;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.LineAndShapeRenderer;
import org.jfree.data.CategoryDataset;
import org.jfree.data.DataUtilities;
import org.jfree.data.DatasetUtilities;
import org.jfree.data.DefaultKeyedValues;
import org.jfree.data.KeyedValues;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;
import org.jfree.util.SortOrder;

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
        JFreeChart chart = ChartFactory.createBarChart(
            "Freshmeat Software Projects",  // chart title
            "Language",                     // domain axis label
            "Projects",                     // range axis label
            dataset,                        // data
            PlotOrientation.VERTICAL,
            true,                           // include legend
            true,
            false
        );

        // NOW DO SOME OPTIONAL CUSTOMISATION OF THE CHART...
        chart.addSubtitle(new TextTitle("By Programming Language"));
        chart.addSubtitle(new TextTitle("As at 5 March 2003"));

        // set the background color for the chart...
        chart.setBackgroundPaint(new Color(0xBBBBDD));

        // get a reference to the plot for further customisation...
        CategoryPlot plot = chart.getCategoryPlot();

        CategoryAxis domainAxis = plot.getDomainAxis();
        domainAxis.setLowerMargin(0.02);
        domainAxis.setUpperMargin(0.02);

        // set the range axis to display integers only...
        NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
        rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());

        LineAndShapeRenderer renderer2 = new LineAndShapeRenderer();

        CategoryDataset dataset2 = DatasetUtilities.createCategoryDataset("Cumulative", cumulative);
        NumberAxis axis2 = new NumberAxis("Percent");
        axis2.setNumberFormatOverride(NumberFormat.getPercentInstance());
        plot.setSecondaryRangeAxis(0, axis2);
        plot.setSecondaryDataset(0, dataset2);
        plot.setSecondaryRenderer(0, renderer2);
        plot.mapSecondaryDatasetToRangeAxis(0, new Integer(0));

        plot.setDatasetRenderingOrder(DatasetRenderingOrder.REVERSE);
        // OPTIONAL CUSTOMISATION COMPLETED.

        // add the chart to a panel...
        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new java.awt.Dimension(550, 270));
        setContentPane(chartPanel);

    }

    // ****************************************************************************
    // * COMMERCIAL SUPPORT / JFREECHART DEVELOPER GUIDE                          *
    // * Please note that commercial support and documentation is available from: *
    // *                                                                          *
    // * http://www.object-refinery.com/jfreechart/support.html                   *
    // *                                                                          *
    // * This is not only a great service for developers, but is a VERY IMPORTANT *
    // * source of funding for the JFreeChart project.  Please support us so that *
    // * we can continue developing free software.                                *
    // ****************************************************************************

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
