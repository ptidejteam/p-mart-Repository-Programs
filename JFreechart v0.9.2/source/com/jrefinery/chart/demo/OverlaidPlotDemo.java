/* ===============
 * JFreeChart Demo
 * ===============
 *
 * Project Info:  http://www.object-refinery.com/jfreechart/index.html
 * Project Lead:  David Gilbert (david.gilbert@object-refinery.com);
 *
 * (C) Copyright 2000-2002, by Simba Management Limited and Contributors.
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
 * ---------------------
 * OverlaidPlotDemo.java
 * ---------------------
 * (C) Copyright 2002, by Simba Management Limited.
 *
 * Original Author:  David Gilbert (for Simba Management Limited).
 * Contributor(s):   -;
 *
 * $Id: OverlaidPlotDemo.java,v 1.1 2007/10/10 19:41:58 vauchers Exp $
 *
 * Changes
 * -------
 * 28-Mar-2002 : Version 1 (DG);
 * 23-Apr-2002 : Modified to use new OverlaidXYPlot class (DG);
 * 31-May-2002 : Changed plot background color to yellow, to check that it works (DG);
 *
 */

package com.jrefinery.chart.demo;

import com.jrefinery.data.BasicTimeSeries;
import com.jrefinery.data.TimeSeriesCollection;
import com.jrefinery.data.Day;
import com.jrefinery.data.XYDataset;
import com.jrefinery.data.IntervalXYDataset;
import com.jrefinery.data.CombinedDataset;
import com.jrefinery.data.SeriesDataset;
import com.jrefinery.data.SubSeriesDataset;
import com.jrefinery.date.SerialDate;
import com.jrefinery.ui.ApplicationFrame;
import com.jrefinery.chart.ChartFactory;
import com.jrefinery.chart.JFreeChart;
import com.jrefinery.chart.ChartPanel;
import com.jrefinery.chart.XYPlot;
import com.jrefinery.chart.XYItemRenderer;
import com.jrefinery.chart.StandardXYItemRenderer;
import com.jrefinery.chart.VerticalXYBarRenderer;
import com.jrefinery.chart.HorizontalDateAxis;
import com.jrefinery.chart.ValueAxis;
import com.jrefinery.chart.NumberAxis;
import com.jrefinery.chart.VerticalNumberAxis;
import com.jrefinery.chart.OverlaidXYPlot;
import com.jrefinery.chart.tooltips.TimeSeriesToolTipGenerator;

import java.awt.Color;

/**
 * A demonstration application showing a time series chart overlaid with a vertical XY bar chart.
 */
public class OverlaidPlotDemo extends ApplicationFrame {

    /**
     * Constructs a new demonstration application.
     */
    public OverlaidPlotDemo(String title) {

        super(title);
        JFreeChart chart = createOverlaidChart();
        chart.getPlot().setBackgroundPaint(Color.yellow);
        ChartPanel panel = new ChartPanel(chart, true, true, true, true, true);
        this.setContentPane(panel);

    }

    /**
     * Creates an overlaid chart.
     */
    private JFreeChart createOverlaidChart() {

        // make an overlaid plot...
        OverlaidXYPlot plot = new OverlaidXYPlot("Date", "Value");
        plot.setDomainAxis(new HorizontalDateAxis("Date"));

        // add subplot 1...
        IntervalXYDataset data1 = this.createDataset1();
        XYItemRenderer renderer1 = new VerticalXYBarRenderer(0.20);
        renderer1.setToolTipGenerator(new TimeSeriesToolTipGenerator("d-MMM-yyyy", "0.00"));
        XYPlot subplot1 = new XYPlot(data1, null, null, renderer1);
        plot.add(subplot1);

        // add subplot 2...
        XYDataset data2 = this.createDataset2();
        XYItemRenderer renderer2 = new StandardXYItemRenderer();
        renderer2.setToolTipGenerator(new TimeSeriesToolTipGenerator("d-MMM-yyyy", "0.00"));
        XYPlot subplot2 = new XYPlot(data2, null, null, renderer2);
        plot.add(subplot2);

        // return a new chart containing the overlaid plot...
        return new JFreeChart("Overlaid Plot Example", JFreeChart.DEFAULT_TITLE_FONT, plot, true);

    }

    /**
     * Creates a sample dataset.
     */
    private IntervalXYDataset createDataset1() {

        // create dataset 1...
        BasicTimeSeries series1 = new BasicTimeSeries("Series 1", Day.class);
        series1.add(new Day(1, SerialDate.MARCH, 2002), 12353.3);
        series1.add(new Day(2, SerialDate.MARCH, 2002), 13734.4);
        series1.add(new Day(3, SerialDate.MARCH, 2002), 14525.3);
        series1.add(new Day(4, SerialDate.MARCH, 2002), 13984.3);
        series1.add(new Day(5, SerialDate.MARCH, 2002), 12999.4);
        series1.add(new Day(6, SerialDate.MARCH, 2002), 14274.3);
        series1.add(new Day(7, SerialDate.MARCH, 2002), 15943.5);
        series1.add(new Day(8, SerialDate.MARCH, 2002), 14845.3);
        series1.add(new Day(9, SerialDate.MARCH, 2002), 14645.4);
        series1.add(new Day(10, SerialDate.MARCH, 2002), 16234.6);
        series1.add(new Day(11, SerialDate.MARCH, 2002), 17232.3);
        series1.add(new Day(12, SerialDate.MARCH, 2002), 14232.2);
        series1.add(new Day(13, SerialDate.MARCH, 2002), 13102.2);
        series1.add(new Day(14, SerialDate.MARCH, 2002), 14230.2);
        series1.add(new Day(15, SerialDate.MARCH, 2002), 11235.2);

        return new TimeSeriesCollection(series1);

    }

    /**
     * Creates a sample dataset.
     */
    private XYDataset createDataset2() {

        // create dataset 2...
        BasicTimeSeries series2 = new BasicTimeSeries("Series 2", Day.class);

        series2.add(new Day(3, SerialDate.MARCH, 2002), 16853.2);
        series2.add(new Day(4, SerialDate.MARCH, 2002), 19642.3);
        series2.add(new Day(5, SerialDate.MARCH, 2002), 18253.5);
        series2.add(new Day(6, SerialDate.MARCH, 2002), 15352.3);
        series2.add(new Day(7, SerialDate.MARCH, 2002), 13532.0);
        series2.add(new Day(8, SerialDate.MARCH, 2002), 12635.3);
        series2.add(new Day(9, SerialDate.MARCH, 2002), 13998.2);
        series2.add(new Day(10, SerialDate.MARCH, 2002), 11943.2);
        series2.add(new Day(11, SerialDate.MARCH, 2002), 16943.9);
        series2.add(new Day(12, SerialDate.MARCH, 2002), 17843.2);
        series2.add(new Day(13, SerialDate.MARCH, 2002), 16495.3);
        series2.add(new Day(14, SerialDate.MARCH, 2002), 17943.6);
        series2.add(new Day(15, SerialDate.MARCH, 2002), 18500.7);
        series2.add(new Day(16, SerialDate.MARCH, 2002), 19595.9);

        return new TimeSeriesCollection(series2);

    }

    /**
     * Starting point for the demonstration application.
     */
    public static void main(String[] args) {

        OverlaidPlotDemo demo = new OverlaidPlotDemo("Overlaid Plot Demo");
        demo.pack();
        demo.setVisible(true);

    }

}