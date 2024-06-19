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
 * --------------------
 * TimeSeriesDemo9.java
 * --------------------
 * (C) Copyright 2003, by Simba Management Limited and Contributors.
 *
 * Original Author:  David Gilbert (for Simba Management Limited);
 * Contributor(s):   -;
 *
 * $Id: TimeSeriesDemo9.java,v 1.1 2007/10/10 20:00:13 vauchers Exp $
 *
 * Changes
 * -------
 * 11-Feb-2003 : Version 1 (DG);
 *
 */

package com.jrefinery.chart.demo;

import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Rectangle2D;

import com.jrefinery.chart.ChartFactory;
import com.jrefinery.chart.ChartPanel;
import com.jrefinery.chart.JFreeChart;
import com.jrefinery.chart.StandardLegend;
import com.jrefinery.chart.plot.XYPlot;
import com.jrefinery.chart.renderer.StandardXYItemRenderer;
import com.jrefinery.chart.renderer.XYItemRenderer;
import com.jrefinery.data.XYDataset;
import com.jrefinery.data.time.Day;
import com.jrefinery.data.time.TimeSeries;
import com.jrefinery.data.time.TimeSeriesCollection;
import com.jrefinery.ui.ApplicationFrame;
import com.jrefinery.ui.RefineryUtilities;

/**
 * An example of a time series chart. 
 *
 * @author David Gilbert
 */
public class TimeSeriesDemo9 extends ApplicationFrame {

    /**
     * A demonstration application showing how to create a simple time series chart.  This
     * example uses monthly data.
     *
     * @param title  the frame title.
     */
    public TimeSeriesDemo9(String title) {

        super(title);

        // create a title...
        String chartTitle = "Test";
        XYDataset dataset = createDataset();

        JFreeChart chart = ChartFactory.createTimeSeriesChart(chartTitle, "Date", "Price Per Unit",
                                                              dataset, 
                                                              true,
                                                              true,
                                                              false);
                                                              
        StandardLegend sl = (StandardLegend) chart.getLegend();
        sl.setDisplaySeriesShapes(true);
                                                              
        XYPlot plot = chart.getXYPlot();
        XYItemRenderer r = plot.getRenderer();
        if (r instanceof StandardXYItemRenderer) {
            StandardXYItemRenderer renderer = (StandardXYItemRenderer) r;
            renderer.setPlotShapes(true);
            renderer.setDefaultShapeFilled(true);
            renderer.setSeriesShape(0, new Ellipse2D.Double(-3.0, -3.0, 6.0, 6.0));
            renderer.setSeriesShape(1, new Rectangle2D.Double(-3.0, -3.0, 6.0, 6.0));
            GeneralPath s2 = new GeneralPath();
            s2.moveTo(0.0f, -3.0f);
            s2.lineTo(3.0f, 3.0f);
            s2.lineTo(-3.0f, 3.0f);
            s2.closePath();
            renderer.setSeriesShape(2, s2);
            GeneralPath s3 = new GeneralPath();
            s3.moveTo(-1.0f, -3.0f);
            s3.lineTo(1.0f, -3.0f);
            s3.lineTo(1.0f, -1.0f);
            s3.lineTo(3.0f, -1.0f);
            s3.lineTo(3.0f, 1.0f);
            s3.lineTo(1.0f, 1.0f);
            s3.lineTo(1.0f, 3.0f);
            s3.lineTo(-1.0f, 3.0f);
            s3.lineTo(-1.0f, 1.0f);
            s3.lineTo(-3.0f, 1.0f);
            s3.lineTo(-3.0f, -1.0f);
            s3.lineTo(-1.0f, -1.0f);
            s3.closePath();
            renderer.setSeriesShape(3, s3);
        }
        
        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new java.awt.Dimension(500, 270));
        setContentPane(chartPanel);

    }

    /**
     * Creates a sample dataset.
     * 
     * @return The dataset.
     */
    public XYDataset createDataset() {
    
        TimeSeriesCollection dataset = new TimeSeriesCollection();
        for (int i = 0; i < 4; i++) {
            dataset.addSeries(createTimeSeries(i, 10));
        }
        return dataset;
    
    }
    
    /**
     * Creates a time series containing random daily data.
     *
     * @param series  the series index.
     * @param count  the number of items for the series.
     * 
     * @return the dataset.
     */
    public TimeSeries createTimeSeries(int series, int count) {

        TimeSeries result = new TimeSeries("Series " + series , Day.class);
        
        Day start = new Day();
        for (int i = 0; i < count; i++) {
            result.add(start, Math.random());
            start = (Day) start.next();
        }

        return result;

    }

    /**
     * Starting point for the demonstration application.
     *
     * @param args  ignored.
     */
    public static void main(String[] args) {

        TimeSeriesDemo9 demo = new TimeSeriesDemo9("Time Series Demo 9");
        demo.pack();
        RefineryUtilities.centerFrameOnScreen(demo);
        demo.setVisible(true);

    }

}
