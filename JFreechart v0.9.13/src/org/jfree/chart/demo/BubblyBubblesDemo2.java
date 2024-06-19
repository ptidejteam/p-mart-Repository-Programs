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
 * BubblyBubblesDemo2.java
 * -----------------------
 * (C) Copyright 2003 by Barak Naveh and Contributors.
 *
 * Original Author:  Barak Naveh;;
 * Contributor(s):   David Gilbert (for Object Refinery Limited);
 *
 * $Id: BubblyBubblesDemo2.java,v 1.1 2007/10/10 19:15:25 vauchers Exp $
 *
 * Changes
 * -------
 * 10-Jul-2003 : Version 1 contributed by Barak Naveh (DG);
 *
 */
 
package org.jfree.chart.demo;

import java.awt.Color;
import java.awt.GradientPaint;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.MatrixSeriesCollection;
import org.jfree.data.NormalizedMatrixSeries;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;

/**
 * A demo that shows how matrix series can be used for charts that follow a
 * constantly changing grid input.
 *
 * @author Barak Naveh
 *
 * @since Jun 25, 2003
 */
public class BubblyBubblesDemo2 extends ApplicationFrame {
    
    /** The default size. */
    private static final int    SIZE  = 10;
 
    /** The default title. */
    private static final String TITLE = "Population count at grid locations";

    /**
     * The normalized matrix series is used here to represent a changing
     * population on a grid.
     */
    private NormalizedMatrixSeries m_series;

    /**
     * A demonstration application showing a bubble chart using matrix series.
     *
     * @param title the frame title.
     */
    public BubblyBubblesDemo2(String title) {
        super(title);

        m_series = createInitialSeries();

        MatrixSeriesCollection dataset = new MatrixSeriesCollection(m_series);

        JFreeChart chart = ChartFactory.createBubbleChart(
            TITLE, 
            "X", 
            "Y", 
            dataset, 
            PlotOrientation.VERTICAL, 
            true,
            true, 
            false 
);

        chart.setBackgroundPaint(new GradientPaint(0, 0, Color.white, 0, 1000, Color.yellow));

        XYPlot plot = chart.getXYPlot();
        plot.setForegroundAlpha(0.5f);

        NumberAxis domainAxis = (NumberAxis) plot.getDomainAxis();
        domainAxis.setLowerBound(-0.5);

        NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();

        // rangeAxis.setInverted(true);  // uncoment to reproduce a bug in jFreeChart
        rangeAxis.setLowerBound(-0.5);

        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setVerticalZoom(true);
        chartPanel.setHorizontalZoom(true);
        setContentPane(chartPanel);
    }

    /**
     * Starting point for the demonstration application.
     *
     * @param args ignored.
     */
    public static void main(String[] args) {
        BubblyBubblesDemo2 demo = new BubblyBubblesDemo2(TITLE);
        demo.pack();
        demo.setSize(800, 600);
        RefineryUtilities.centerFrameOnScreen(demo);
        demo.setVisible(true);

        Thread updater = demo.new UpdaterThread();
        updater.setDaemon(true);
        updater.start();
    }


    /**
     * Creates a series.
     * 
     * @return The series.
     */
    private NormalizedMatrixSeries createInitialSeries() {
        NormalizedMatrixSeries series = new NormalizedMatrixSeries("Sample Grid 1", SIZE, SIZE);

        // seed a few random bubbles
        for (int count = 0; count < SIZE * 3; count++) {
            int i = (int) (Math.random() * SIZE);
            int j = (int) (Math.random() * SIZE);

            double mij = Math.random() * 1;
            series.update(i, j, mij);
        }

        series.setScaleFactor(series.getItemCount());

        return series;
    }

    /**
     * Updater thread.
     */
    private class UpdaterThread extends Thread {
        /**
         * @see java.lang.Runnable#run()
         */
        public void run() {
            setPriority(MIN_PRIORITY); // be nice

            while (true) {
                int i = (int) (Math.random() * SIZE);
                int j = (int) (Math.random() * SIZE);

                double change = Math.random() * 3.0 - 1.0;

                m_series.update(i, j, m_series.get(i, j) + change);

                try {
                    sleep(50);
                }
                catch (InterruptedException e) {
                }
            }
        }
    }
}
