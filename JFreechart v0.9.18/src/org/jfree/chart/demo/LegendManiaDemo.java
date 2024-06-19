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
 * --------------------
 * LegendManiaDemo.java
 * --------------------
 * (C) Copyright 2004, by Barak Naveh and Contributors.
 *
 * Original Author:  Barak Naveh;
 * Contributor(s):   -;
 *
 * $Id: LegendManiaDemo.java,v 1.1 2007/10/10 19:39:16 vauchers Exp $
 *
 * Changes
 * -------
 * 26-Mar-2004 : Version 1 contributed by Barak Naveh (BN);
 * 27-Mar-2004 : Added showing off round corners of bounding box (BN);
 *
 */

package org.jfree.chart.demo;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Paint;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.Legend;
import org.jfree.chart.StandardLegend;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.BarRenderer;
import org.jfree.data.CategoryDataset;
import org.jfree.data.DefaultCategoryDataset;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;

/**
 * A demo that shows legend positions and legend bounding box options.
 *
 * @author Barak Naveh
 *
 * @since March 26, 2004
 */
public class LegendManiaDemo extends ApplicationFrame {

    private final static String CHART_TITLE = "Legend Mania Demo";
    private static final Paint BACKGROUND_PAINT = new Color(255, 240, 240);
    
    /** The chart of this demo */
    JFreeChart m_chart;
    
    /**
     * A demo application that shows legend positions and legend bounding box 
     * options.
     *
     * @param title the frame title.
     */
    public LegendManiaDemo(String title) {
        super(title);
        CategoryDataset dataset = createDataset();
        m_chart = createChart(dataset);
        ChartPanel chartPanel = new ChartPanel(m_chart);
        chartPanel.setPreferredSize(new Dimension(500, 270));
        setContentPane(chartPanel);
    }

    /**
     * Returns a sample dataset.
     * 
     * @return The dataset.
     */
    private CategoryDataset createDataset() {
        
        // row keys...
        String series1 = "Birds";
        String series2 = "Beas";
        String series3 = "Kangaroos";

        // column keys...
        String category1 = "Shopping";
        String category2 = "Socializing";
        String category3 = "Sex";
        String category4 = "TV Watching";

        // create the dataset...
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        dataset.addValue(1.0, series1, category1);
        dataset.addValue(4.0, series1, category2);
        dataset.addValue(3.0, series1, category3);
        dataset.addValue(5.0, series1, category4);

        dataset.addValue(5.0, series2, category1);
        dataset.addValue(7.0, series2, category2);
        dataset.addValue(6.0, series2, category3);
        dataset.addValue(8.0, series2, category4);

        dataset.addValue(4.0, series3, category1);
        dataset.addValue(3.0, series3, category2);
        dataset.addValue(2.0, series3, category3);
        dataset.addValue(3.0, series3, category4);
        
        return dataset;
    }
    
    /**
     * Creates a sample chart.
     * 
     * @param dataset  the dataset.
     * 
     * @return The chart.
     */
    private JFreeChart createChart(CategoryDataset dataset) {
        
        // create the chart...
        JFreeChart chart = ChartFactory.createBarChart(
            CHART_TITLE,                    // chart title
            "Activity",               // domain axis label
            "Rate",                  // range axis label
            dataset,                  // data
            PlotOrientation.VERTICAL, // orientation
            true,                     // include legend
            true,                     // tooltips?
            false                     // URLs?
        );

        // NOW DO SOME OPTIONAL CUSTOMISATION OF THE CHART...

        // set the background color for the chart...
        chart.setBackgroundPaint(new Color(255, 255, 180));

        // get a reference to the plot for further customisation...
        CategoryPlot plot = chart.getCategoryPlot();
        plot.setBackgroundPaint(BACKGROUND_PAINT);
        plot.setDomainGridlinePaint(Color.white);
        plot.setRangeGridlinePaint(Color.white);

        // set the range axis to display integers only...
        NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
        rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());

        // disable bar outlines...
        BarRenderer renderer = (BarRenderer) plot.getRenderer();
        renderer.setDrawBarOutline(false);
        
        CategoryAxis domainAxis = plot.getDomainAxis();
        domainAxis.setCategoryLabelPositions(
            CategoryLabelPositions.createUpRotationLabelPositions(Math.PI / 6.0));
        
        StandardLegend legend = (StandardLegend)chart.getLegend();
        legend.setBackgroundPaint(Color.orange);
        legend.setOutlinePaint(Color.orange);
        // OPTIONAL CUSTOMISATION COMPLETED.
        
        return chart;
        
    }
    
    /**
     * Starting point for the demonstration application.
     *
     * @param args ignored.
     */
    public static void main(String[] args) {
        LegendManiaDemo demo = new LegendManiaDemo(CHART_TITLE);
        demo.pack();
        demo.setSize(800, 600);
        RefineryUtilities.centerFrameOnScreen(demo);
        demo.setVisible(true);

        Thread updater = demo.new UpdaterThread();
        updater.setDaemon(true);
        updater.start();
    }

    /**
     * A thread for updating the legend position in a loop.
     */
    private class UpdaterThread extends Thread {
        
        /**
         * @see java.lang.Runnable#run()
         */
        public void run() {
            int[] anchors = {
                    Legend.NORTH_NORTHWEST,
                    Legend.NORTH,
                    Legend.NORTH_NORTHEAST,
                    Legend.EAST_NORTHEAST,
                    Legend.EAST,
                    Legend.EAST_SOUTHEAST,
                    Legend.SOUTH_SOUTHEAST,
                    Legend.SOUTH,
                    Legend.SOUTH_SOUTHWEST,
                    Legend.WEST_SOUTHWEST,
                    Legend.WEST,
                    Legend.WEST_NORTHWEST
            };
            
            String[] anchorNames = {
                    "NORTH_NORTHWEST",
                    "NORTH",
                    "NORTH_NORTHEAST",
                    "EAST_NORTHEAST",
                    "EAST",
                    "EAST_SOUTHEAST",
                    "SOUTH_SOUTHEAST",
                    "SOUTH",
                    "SOUTH_SOUTHWEST",
                    "WEST_SOUTHWEST",
                    "WEST",
                    "WEST_NORTHWEST"
            };
            
            setPriority(MIN_PRIORITY); // be nice
            StandardLegend legend = (StandardLegend)m_chart.getLegend();
            
            int i = 0;
            while (true) {
                // set the next anchor point 
                legend.setTitle(anchorNames[i]);
                legend.setAnchor(anchors[i]);
                i = (i + 1) % anchors.length;

                // set rectangular corners of bounding box and wait for a second
                legend.setBoundingBoxArcHeight(0);
                legend.setBoundingBoxArcWidth(0);
                
                try {
                    sleep(1000);
                }
                catch (InterruptedException e) {}

                // set round corners of bounding box and wait for a second
                legend.setBoundingBoxArcHeight(10);
                legend.setBoundingBoxArcWidth(10);
                
                try {
                    sleep(1000);
                }
                catch (InterruptedException e) {}
            }
        }
    }
}



