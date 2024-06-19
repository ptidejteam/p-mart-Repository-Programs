/* ===============
 * JFreeChart Demo
 * ===============
 *
 * Project Info:  http://www.object-refinery.com/jfreechart/index.html
 * Project Lead:  David Gilbert (david.gilbert@object-refinery.com);
 *
 * (C) Copyright 2000-2002, by Simba Management Limited and Contributors.
 *
 * This program is free software; you can redistribute it and/or modify it under the terms
 * of the GNU General Public License as published by the Free Software Foundation;
 * either version 2 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program;
 * if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston,
 * MA 02111-1307, USA.
 *
 * ---------------------
 * OverlaidPlotDemo.java
 * ---------------------
 * (C) Copyright 2002, by Simba Management Limited.
 *
 * Original Author:  David Gilbert (for Simba Management Limited).
 * Contributor(s):   -;
 *
 * $Id: PriceVolumeDemo.java,v 1.1 2007/10/10 19:02:36 vauchers Exp $
 *
 * Changes
 * -------
 * 28-Mar-2002 : Version 1 (DG);
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
import com.jrefinery.chart.JFreeChartPanel;
import com.jrefinery.chart.XYPlot;
import com.jrefinery.chart.VerticalXYBarRenderer;
import com.jrefinery.chart.HorizontalDateAxis;
import com.jrefinery.chart.ValueAxis;
import com.jrefinery.chart.NumberAxis;
import com.jrefinery.chart.VerticalNumberAxis;
import com.jrefinery.chart.combination.CombinedPlot;
import com.jrefinery.chart.combination.CombinedChart;

/**
 * A demonstration application showing a time series chart overlaid with a vertical XY bar chart.
 */
public class PriceVolumeDemo extends ApplicationFrame {

    /**
     * Constructs a new demonstration application.
     */
    public PriceVolumeDemo() {

        JFreeChart chart = createCombinedChart();
        JFreeChartPanel panel = new JFreeChartPanel(chart, true, true, true, false, true);
        this.setContentPane(panel);

    }

    /**
     * Creates an overlaid chart.
     */
    private JFreeChart createCombinedChart() {

        // create two sample datasets...
        XYDataset priceData = this.createPriceDataset();
        XYDataset volumeData = this.createVolumeDataset();

        // create a combined dataset...
        CombinedDataset data = new CombinedDataset();
        data.add(priceData);
        data.add(volumeData);

        // create a shared time axis...
        ValueAxis timeAxis = new HorizontalDateAxis("Date");
        timeAxis.setCrosshairVisible(false);

        // make a combined plot...
        CombinedPlot combinedPlot = new CombinedPlot(timeAxis, CombinedPlot.VERTICAL);

        // add the combinable charts to it...
        SeriesDataset s0 = new SubSeriesDataset(data, 0);
        NumberAxis valueAxis = new VerticalNumberAxis("Price");
        valueAxis.setCrosshairVisible(false);
        valueAxis.setAutoRangeIncludesZero(false);
        CombinedChart c0 = ChartFactory.createCombinableTimeSeriesChart(timeAxis, valueAxis, s0);
        combinedPlot.add(c0, 3);

        NumberAxis volumeAxis = new VerticalNumberAxis("Volume");
        volumeAxis.setCrosshairVisible(false);
        XYPlot plot1 = new XYPlot(timeAxis, volumeAxis, new VerticalXYBarRenderer(0.20));
        SeriesDataset s1 = new SubSeriesDataset(data, 1);
        CombinedChart c1 = ChartFactory.createCombinableChart(s1, plot1);
        combinedPlot.add(c1, 1);

        // perform final adjustments...
        combinedPlot.adjustPlots();

        // return a new chart containing the overlaid plot...
        return new JFreeChart(data, combinedPlot,
                              "Price / Volume Example", JFreeChart.DEFAULT_TITLE_FONT, true);

    }

    /**
     * Creates a sample dataset.
     */
    private XYDataset createPriceDataset() {

        // create dataset 1...
        BasicTimeSeries series1 = new BasicTimeSeries("Price", Day.class);
        series1.add(new Day(1, SerialDate.MARCH, 2002), 12353.3);
        series1.add(new Day(4, SerialDate.MARCH, 2002), 13984.3);
        series1.add(new Day(5, SerialDate.MARCH, 2002), 12999.4);
        series1.add(new Day(6, SerialDate.MARCH, 2002), 14274.3);
        series1.add(new Day(7, SerialDate.MARCH, 2002), 15943.5);
        series1.add(new Day(8, SerialDate.MARCH, 2002), 14845.3);
        series1.add(new Day(11, SerialDate.MARCH, 2002), 17232.3);
        series1.add(new Day(12, SerialDate.MARCH, 2002), 14232.2);
        series1.add(new Day(13, SerialDate.MARCH, 2002), 13102.2);
        series1.add(new Day(14, SerialDate.MARCH, 2002), 14230.2);
        series1.add(new Day(15, SerialDate.MARCH, 2002), 11435.2);
        series1.add(new Day(18, SerialDate.MARCH, 2002), 14525.3);
        series1.add(new Day(19, SerialDate.MARCH, 2002), 13984.3);
        series1.add(new Day(20, SerialDate.MARCH, 2002), 12999.4);
        series1.add(new Day(21, SerialDate.MARCH, 2002), 14274.3);
        series1.add(new Day(22, SerialDate.MARCH, 2002), 15943.5);
        series1.add(new Day(25, SerialDate.MARCH, 2002), 16234.6);
        series1.add(new Day(26, SerialDate.MARCH, 2002), 17232.3);
        series1.add(new Day(27, SerialDate.MARCH, 2002), 14232.2);
        series1.add(new Day(28, SerialDate.MARCH, 2002), 13102.2);

        return new TimeSeriesCollection(series1);

    }

    /**
     * Creates a sample dataset.
     */
    private IntervalXYDataset createVolumeDataset() {

        // create dataset 2...
        BasicTimeSeries series2 = new BasicTimeSeries("Volume", Day.class);

        series2.add(new Day(1, SerialDate.MARCH, 2002), 500);
        series2.add(new Day(4, SerialDate.MARCH, 2002), 100);
        series2.add(new Day(5, SerialDate.MARCH, 2002), 350);
        series2.add(new Day(6, SerialDate.MARCH, 2002), 975);
        series2.add(new Day(7, SerialDate.MARCH, 2002), 675);
        series2.add(new Day(8, SerialDate.MARCH, 2002), 525);
        series2.add(new Day(11, SerialDate.MARCH, 2002), 675);
        series2.add(new Day(12, SerialDate.MARCH, 2002), 700);
        series2.add(new Day(13, SerialDate.MARCH, 2002), 250);
        series2.add(new Day(14, SerialDate.MARCH, 2002), 225);
        series2.add(new Day(15, SerialDate.MARCH, 2002), 425);
        series2.add(new Day(18, SerialDate.MARCH, 2002), 600);
        series2.add(new Day(19, SerialDate.MARCH, 2002), 300);
        series2.add(new Day(20, SerialDate.MARCH, 2002), 325);
        series2.add(new Day(21, SerialDate.MARCH, 2002), 925);
        series2.add(new Day(22, SerialDate.MARCH, 2002), 525);
        series2.add(new Day(25, SerialDate.MARCH, 2002), 775);
        series2.add(new Day(26, SerialDate.MARCH, 2002), 725);
        series2.add(new Day(27, SerialDate.MARCH, 2002), 125);
        series2.add(new Day(28, SerialDate.MARCH, 2002), 150);

        return new TimeSeriesCollection(series2);

    }

    /**
     * Starting point for the demonstration application.
     */
    public static void main(String[] args) {

        PriceVolumeDemo demo = new PriceVolumeDemo();
        demo.pack();
        demo.setVisible(true);

    }

}
