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
 * ------------------
 * HistogramDemo.java
 * ------------------
 * (C) Copyright 2003, by Jelai Wang and Contributors.
 *
 * Original Author:  Jelai Wang (jelaiw AT mindspring.com);
 * Contributor(s):   David Gilbert (for Object Refinery Limited);
 *
 * $Id: HistogramDemo.java,v 1.1 2007/10/10 19:04:55 vauchers Exp $
 *
 * Changes
 * -------
 * 06-Jul-2003 : Version 1, contributed by Jelai Wang (DG);
 * 07-Jul-2003 : Modified to display chart on screen, consistent with the other JFreeChart demo
 *               applications (DG);
 *
 */

package org.jfree.chart.demo;

import java.io.IOException;
import java.util.Random;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.HistogramDataset;
import org.jfree.data.IntervalXYDataset;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;

/**
 * A demo of the {@link HistogramDataset} class.
 * 
 * @author Jelai Wang, jelaiw AT mindspring.com
 */
public class HistogramDemo extends ApplicationFrame {
    
    /** For generating random numbers. */ 
    static Random random = new Random();

    /**
     * Creates a new demo.
     * 
     * @param title  the frame title.
     */
    public HistogramDemo(String title) {
        super(title);    
        IntervalXYDataset dataset = createDataset();
        JFreeChart chart = createChart(dataset);
        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new java.awt.Dimension(500, 270));
        chartPanel.setMouseZoomable(true, false);
        setContentPane(chartPanel);
    }
    
    /**
     * Creates a sample {@link HistogramDataset}.
     * 
     * @return The dataset.
     */
    private IntervalXYDataset createDataset() {
        HistogramDataset dataset = new HistogramDataset();
        dataset.setType(HistogramDataset.RELATIVE_FREQUENCY);
        dataset.addSeries("H1", gaussianData(1000, 3.0), 20);
        dataset.addSeries("H0", gaussianData(1000, 0), 20);   
        return dataset;     
    }
    
    /**
     * Creates a chart.
     * 
     * @param dataset  a dataset.
     * 
     * @return The chart.
     */
    private JFreeChart createChart(IntervalXYDataset dataset) {
        JFreeChart chart = ChartFactory.createHistogram(
            "Histogram Demo", 
            null, 
            null, 
            dataset, 
            PlotOrientation.VERTICAL, 
            true, 
            false, 
            false
        );
        chart.getXYPlot().setForegroundAlpha(0.75f);
        return chart;
    }
    
    /**
     * Generates an array of sample data.
     * 
     * @param size  the array size.
     * @param shift  the shift from zero.
     * 
     * @return The array of sample data.
     */
    private static double[] gaussianData(int size, double shift) {
        double[] d = new double[size];
        for (int i = 0; i < d.length; i++) {
            d[i] = random.nextGaussian() + shift;
        }
        return d;
    }
    
    /**
     * The starting point for the demo.
     * 
     * @param args  ignored.
     * 
     * @throws IOException  if there is a problem saving the file.
     */
    public static void main(String[] args) throws IOException {
        
        HistogramDemo demo = new HistogramDemo("Histogram Demo");
        demo.pack();
        RefineryUtilities.centerFrameOnScreen(demo);
        demo.setVisible(true);
        
    }

}
