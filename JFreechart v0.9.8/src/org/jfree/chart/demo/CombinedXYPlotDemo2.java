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
 * ------------------------
 * CombinedXYPlotDemo2.java
 * ------------------------
 * (C) Copyright 2003 by Simba Management Limited.
 *
 * Original Author:  David Gilbert (for Simba Management Limited).
 * Contributor(s):   -;
 *
 * $Id $
 *
 * Changes
 * -------
 * 13-Jan-2003 : Version 1 (DG);
 *
 */

package org.jfree.chart.demo;

import java.awt.Font;

import org.jfree.chart.JFreeChart;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.annotations.XYTextAnnotation;
import org.jfree.chart.axis.HorizontalNumberAxis;
import org.jfree.chart.axis.VerticalNumberAxis;
import org.jfree.chart.plot.CombinedXYPlot;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.DefaultDrawingSupplier;
import org.jfree.chart.renderer.DrawingSupplier;
import org.jfree.chart.renderer.StandardXYItemRenderer;
import org.jfree.chart.renderer.XYItemRenderer;
import org.jfree.data.XYDataset;
import org.jfree.data.XYSeries;
import org.jfree.data.XYSeriesCollection;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;

/**
 * A demonstration application showing a time series chart overlaid with a vertical XY bar chart.
 *
 * @author David Gilbert
 */
public class CombinedXYPlotDemo2 extends ApplicationFrame {

    /**
     * Constructs a new demonstration application.
     *
     * @param title  the frame title.
     */
    public CombinedXYPlotDemo2(String title) {

        super(title);
        JFreeChart chart = createCombinedChart();
        ChartPanel panel = new ChartPanel(chart, true, true, true, false, true);
        panel.setPreferredSize(new java.awt.Dimension(500, 270));
        setContentPane(panel);

    }

    /**
     * Creates a combined XYPlot chart.
     *
     * @return the combined chart.
     */
    private JFreeChart createCombinedChart() {

        // create a parent plot...
        CombinedXYPlot plot = new CombinedXYPlot(new HorizontalNumberAxis("Domain"),
                                                 CombinedXYPlot.VERTICAL);

        // create a fill paint supplier to ensure all series use a unique color...
        DrawingSupplier supplier = new DefaultDrawingSupplier();
        
        // create subplot 1...
        XYDataset data1 = createDataset1();
        XYItemRenderer renderer1 = new StandardXYItemRenderer();
        renderer1.setDrawingSupplier(supplier);
        
        XYPlot subplot1 = new XYPlot(data1, null, new VerticalNumberAxis("Range 1"), renderer1);
        subplot1.addAnnotation(new XYTextAnnotation("Hello!",
                                                    new Font("SansSerif", Font.PLAIN, 9),
                                                    50.0, 10000.0));
        // create subplot 2...
        XYDataset data2 = createDataset2();
        XYPlot subplot2 = new XYPlot(data2, null, new VerticalNumberAxis("Range 2"));
        XYItemRenderer renderer2 = subplot2.getRenderer();
        renderer2.setDrawingSupplier(supplier);

        // add the subplots...
        plot.add(subplot1, 1);
        plot.add(subplot2, 1);

        // return a new chart containing the overlaid plot...
        return new JFreeChart("Combined XY Plot (Vertical Layout)",
                              JFreeChart.DEFAULT_TITLE_FONT, plot, true);

    }

    /**
     * Creates a sample dataset.
     *
     * @return Series 1.
     */
    private XYDataset createDataset1() {

        // create dataset 1...
        XYSeries series1 = new XYSeries("Series 1");
        series1.add(10.0, 12353.3);
        series1.add(20.0, 13734.4);
        series1.add(30.0, 14525.3);
        series1.add(40.0, 13984.3);
        series1.add(50.0, 12999.4);
        series1.add(60.0, 14274.3);
        series1.add(70.0, 15943.5);
        series1.add(80.0, 14845.3);
        series1.add(90.0, 14645.4);
        series1.add(100.0, 16234.6);
        series1.add(110.0, 17232.3);
        series1.add(120.0, 14232.2);
        series1.add(130.0, 13102.2);
        series1.add(140.0, 14230.2);
        series1.add(150.0, 11235.2);

        XYSeries series2 = new XYSeries("Series 2");
        series2.add(10.0, 15000.3);
        series2.add(20.0, 11000.4);
        series2.add(30.0, 17000.3);
        series2.add(40.0, 15000.3);
        series2.add(50.0, 14000.4);
        series2.add(60.0, 12000.3);
        series2.add(70.0, 11000.5);
        series2.add(80.0, 12000.3);
        series2.add(90.0, 13000.4);
        series2.add(100.0, 12000.6);
        series2.add(110.0, 13000.3);
        series2.add(120.0, 17000.2);
        series2.add(130.0, 18000.2);
        series2.add(140.0, 16000.2);
        series2.add(150.0, 17000.2);

        XYSeriesCollection collection = new XYSeriesCollection();
        collection.addSeries(series1);
        collection.addSeries(series2);
        return collection;

    }

    /**
     * Creates a sample dataset.
     *
     * @return Series 2.
     */
    private XYDataset createDataset2() {

        // create dataset 2...
        XYSeries series2 = new XYSeries("Series 3");

        series2.add(10.0, 16853.2);
        series2.add(20.0, 19642.3);
        series2.add(30.0, 18253.5);
        series2.add(40.0, 15352.3);
        series2.add(50.0, 13532.0);
        series2.add(100.0, 12635.3);
        series2.add(110.0, 13998.2);
        series2.add(120.0, 11943.2);
        series2.add(130.0, 16943.9);
        series2.add(140.0, 17843.2);
        series2.add(150.0, 16495.3);
        series2.add(160.0, 17943.6);
        series2.add(170.0, 18500.7);
        series2.add(180.0, 19595.9);

        return new XYSeriesCollection(series2);

    }

    /**
     * Starting point for the demonstration application.
     *
     * @param args  ignored.
     */
    public static void main(String[] args) {

        CombinedXYPlotDemo2 demo = new CombinedXYPlotDemo2("Combined XY Plot Demo");
        demo.pack();
        RefineryUtilities.centerFrameOnScreen(demo);
        demo.setVisible(true);

    }

}
