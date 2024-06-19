/* ===============
 * JFreeChart Demo
 * ===============
 *
 * Project Info:  http://www.object-refinery.com/jfreechart
 * Project Lead:  David Gilbert (david.gilbert@jrefinery.com);
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
 */

package com.jrefinery.chart.demo;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.awt.print.*;
import java.io.*;
import java.util.*;
import javax.swing.*;
import com.jrefinery.data.*;
import com.jrefinery.layout.*;
import com.jrefinery.chart.*;
import com.jrefinery.chart.combination.*;
import com.jrefinery.chart.demo.*;
import com.jrefinery.chart.data.*;
import com.jrefinery.data.*;
import com.jrefinery.date.*;
import com.jrefinery.chart.ui.*;
import com.jrefinery.ui.*;

/**
* The main frame in the chart demonstration application.
*/
public class TestChart {

private void displayVerticallyCombinedChart( boolean type) {

    JFrame verticallyCombinedChartFrame = null;
    JFreeChart chart = null;

    // create a default chart based on some sample data...
    String title = "Vertically Combined Chart";
    String xAxisLabel = "Date";
    String[] yAxisLabel = { "CCY per GBP", "Pounds", "IBM", "Bars" };
    String yAxisLabelAlternate = "ALL";
    int[] weight = { 1, 1, 1 }; // control vertical space allocated to each sub-plot

    if (type) {
        // calculate Time Series and Moving Average Dataset
        XYDataset ds1 = createTimeSeriesCollection2();
        XYDataset ds2 = createTimeSeriesCollection3();
        XYDataset ds3 = createTimeSeriesCollection4();

        // create master dataset
        CombinedDataset data = new CombinedDataset();
        data.add(ds1);
        data.add(ds2);

        // test SubSeriesDataset and CombinedDataset operations

        // decompose data into its two dataset series
        SeriesDataset series0 = new SubSeriesDataset(data, 0);
        SeriesDataset series1 = new SubSeriesDataset(data, 1);

        // compose datasets for each sub-plot
        CombinedDataset data0 = new CombinedDataset(new SeriesDataset[] {series0} );
        CombinedDataset data1 = new CombinedDataset(new SeriesDataset[] {series1} );

        // this code could probably go later in the ChartFactory class

        Dataset[] dss = new Dataset[] { ds1, ds2, };

        try {
            // make one vertical axis for each sub-plot
            NumberAxis[] valueAxis = new NumberAxis[2];
            for (int i=0; i<valueAxis.length; i++) {
                valueAxis[i] = new VerticalNumberAxis(yAxisLabel[i]);
                if(dss[i] != null) {
                    Number min = DatasetUtilities.getMinimumRangeValue( dss[i]);
                    Number max = DatasetUtilities.getMaximumRangeValue( dss[i]);
                    System.err.println( "Min: " + min + ", Max: " + max);
                    valueAxis[i].setAutoRange( false);
                    valueAxis[i].setMinimumAxisValue( min.doubleValue());
                    valueAxis[i].setMaximumAxisValue( max.doubleValue());
                } else {
                    valueAxis[i].setAutoRange( true);
                    valueAxis[i].setAutoRangeIncludesZero( true);
            }
        }

        // make one shared horizontal axis
        HorizontalDateAxis timeAxis0 = new HorizontalDateAxis(xAxisLabel);

        // make a vertically CombinedPlot that will contain the sub-plots
        CombinedPlot combinedPlot = new CombinedPlot(timeAxis0, CombinedPlot.VERTICAL);

        CombinedChart chartToCombine;

        chartToCombine = ChartFactory.createCombinableTimeSeriesChart(timeAxis0, valueAxis[0], data0);
        combinedPlot.add(chartToCombine, weight[0]);

        chartToCombine = ChartFactory.createCombinableTimeSeriesChart(timeAxis0, valueAxis[1], data1);
        combinedPlot.add(chartToCombine, weight[1]);

        // this should be called after all sub-plots have been added
        combinedPlot.adjustPlots();

        // now make the top level JFreeChart that contains the CombinedPlot
        chart = new JFreeChart(data, combinedPlot, title, JFreeChart.DEFAULT_TITLE_FONT, true);

    }
    catch (AxisNotCompatibleException e) {
        // this won't happen unless you mess with the axis constructors above
        System.err.println("axis not compatible.");
        e.printStackTrace();
    }
    catch (PlotNotCompatibleException e) {
        // this won't happen unless you mess with the axis constructors above
        System.err.println("axis not compatible.");
    }

    // then customise it a little...
    TextTitle subtitle = new TextTitle("Four Combined Plots: XY, TimeSeries, HighLow and VerticalXYBar",
    new Font("SansSerif", Font.BOLD, 12));
    chart.addTitle(subtitle);

    } else {
        XYDataset data = createTimeSeriesCollection1();
        chart = ChartFactory.createTimeSeriesChart(title, xAxisLabel, yAxisLabelAlternate, data,
                true);

        // then customise it a little...
        TextTitle subtitle = new TextTitle("Value of GBP in JPY", new Font("SansSerif", Font.BOLD, 12));
        chart.addTitle(subtitle);
        Plot plot = chart.getPlot();
    }

    // and present it in a frame...
    verticallyCombinedChartFrame = new JFreeChartFrame("Vertically Combined Chart", chart);
    verticallyCombinedChartFrame.pack();
    JRefineryUtilities.positionFrameRandomly(verticallyCombinedChartFrame);
    verticallyCombinedChartFrame.show();

}



public static TimeSeriesCollection createTimeSeriesCollection1() {

    TimeSeriesCollection data = new TimeSeriesCollection();
    data.addSeries(createJPYTimeSeries());
    data.addSeries(createUSDTimeSeries());
    data.addSeries(createEURTimeSeries());
    return data;

}

public static TimeSeriesCollection createTimeSeriesCollection2() {

    TimeSeriesCollection data = new TimeSeriesCollection();
    data.addSeries(createJPYTimeSeries());
    return data;

}

/**
* Creates a time series collection containing USD/GBP and EUR/GBP exchange rates.
*/
public static TimeSeriesCollection createTimeSeriesCollection3() {

    TimeSeriesCollection collection = new TimeSeriesCollection();
    collection.addSeries(createUSDTimeSeries());
    return collection;

}

public static TimeSeriesCollection createTimeSeriesCollection4() {

    TimeSeriesCollection collection = new TimeSeriesCollection();
    collection.addSeries(createEURTimeSeries());
    return collection;

}

public static BasicTimeSeries createUSDTimeSeries() {

    BasicTimeSeries t1 = new BasicTimeSeries("USD/GBP");
    try {
        t1.add(new Day(2, SerialDate.JANUARY, 2001), new Double(14956));
        t1.add(new Day(3, SerialDate.JANUARY, 2001), new Double(15047));
        t1.add(new Day(4, SerialDate.JANUARY, 2001), new Double(14931));
        t1.add(new Day(5, SerialDate.JANUARY, 2001), new Double(14955));
        t1.add(new Day(8, SerialDate.JANUARY, 2001), new Double(14994));
        t1.add(new Day(9, SerialDate.JANUARY, 2001), new Double(14911));
        t1.add(new Day(10, SerialDate.JANUARY, 2001), new Double(14903));
        t1.add(new Day(11, SerialDate.JANUARY, 2001), new Double(14947));
        t1.add(new Day(12, SerialDate.JANUARY, 2001), new Double(14784));
        t1.add(new Day(15, SerialDate.JANUARY, 2001), new Double(14787));
        t1.add(new Day(16, SerialDate.JANUARY, 2001), new Double(14702));
        t1.add(new Day(17, SerialDate.JANUARY, 2001), new Double(14729));
        t1.add(new Day(18, SerialDate.JANUARY, 2001), new Double(14760));
        t1.add(new Day(19, SerialDate.JANUARY, 2001), new Double(14685));
        t1.add(new Day(22, SerialDate.JANUARY, 2001), new Double(14609));
        t1.add(new Day(23, SerialDate.JANUARY, 2001), new Double(14709));
        t1.add(new Day(24, SerialDate.JANUARY, 2001), new Double(14576));
        t1.add(new Day(25, SerialDate.JANUARY, 2001), new Double(14589));
        t1.add(new Day(26, SerialDate.JANUARY, 2001), new Double(14568));
        t1.add(new Day(29, SerialDate.JANUARY, 2001), new Double(14566));
        t1.add(new Day(30, SerialDate.JANUARY, 2001), new Double(14604));
        t1.add(new Day(31, SerialDate.JANUARY, 2001), new Double(14616));
    }
    catch (Exception e) {
        System.err.println(e.getMessage());
    }
    return t1;
}

/**
* Returns a time series of the daily EUR/GBP exchange rates in 2001 (to date), for use in
* the JFreeChart demonstration application.
* <P>
* You wouldn't normally create a time series in this way. Typically, values would
* be read from a database.
*
*/
public static BasicTimeSeries createEURTimeSeries() {

    BasicTimeSeries t1 = new BasicTimeSeries("EUR/GBP");
    try {
        t1.add(new Day(2, SerialDate.JANUARY, 2001), new Double(15788));
        t1.add(new Day(3, SerialDate.JANUARY, 2001), new Double(15913));
        t1.add(new Day(4, SerialDate.JANUARY, 2001), new Double(15807));
        t1.add(new Day(5, SerialDate.JANUARY, 2001), new Double(15711));
        t1.add(new Day(8, SerialDate.JANUARY, 2001), new Double(15778));
        t1.add(new Day(9, SerialDate.JANUARY, 2001), new Double(15851));
        t1.add(new Day(10, SerialDate.JANUARY, 2001), new Double(15846));
        t1.add(new Day(11, SerialDate.JANUARY, 2001), new Double(15727));
        t1.add(new Day(12, SerialDate.JANUARY, 2001), new Double(15585));
        t1.add(new Day(15, SerialDate.JANUARY, 2001), new Double(15694));
        t1.add(new Day(16, SerialDate.JANUARY, 2001), new Double(15629));
        t1.add(new Day(17, SerialDate.JANUARY, 2001), new Double(15831));
        t1.add(new Day(18, SerialDate.JANUARY, 2001), new Double(15624));
        t1.add(new Day(19, SerialDate.JANUARY, 2001), new Double(15694));
        t1.add(new Day(22, SerialDate.JANUARY, 2001), new Double(15615));
        t1.add(new Day(23, SerialDate.JANUARY, 2001), new Double(15656));
        t1.add(new Day(24, SerialDate.JANUARY, 2001), new Double(15795));
        t1.add(new Day(25, SerialDate.JANUARY, 2001), new Double(15852));
        t1.add(new Day(26, SerialDate.JANUARY, 2001), new Double(15797));
        t1.add(new Day(29, SerialDate.JANUARY, 2001), new Double(15862));
        t1.add(new Day(30, SerialDate.JANUARY, 2001), new Double(15803));
        t1.add(new Day(31, SerialDate.JANUARY, 2001), new Double(15714));
    }
    catch (Exception e) {
        System.err.println(e.getMessage());
    }
    return t1;
}

/**
* Returns a time series of the daily EUR/GBP exchange rates in 2001 (to date), for use in
* the JFreeChart demonstration application.
* <P>
* You wouldn't normally create a time series in this way. Typically, values would
* be read from a database.
*
*/
public static BasicTimeSeries createJPYTimeSeries() {

    BasicTimeSeries t1 = new BasicTimeSeries("JPY/GBP Exchange Rate");
    try {
        t1.add(new Day(2, SerialDate.MARCH, 2001), new Double(17112));
        t1.add(new Day(3, SerialDate.MARCH, 2001), new Double(17276));
        t1.add(new Day(4, SerialDate.MARCH, 2001), new Double(17285));
        t1.add(new Day(5, SerialDate.MARCH, 2001), new Double(17323));
        t1.add(new Day(8, SerialDate.MARCH, 2001), new Double(17453));
        t1.add(new Day(9, SerialDate.MARCH, 2001), new Double(17386));
        t1.add(new Day(10, SerialDate.MARCH, 2001), new Double(17323));
        t1.add(new Day(11, SerialDate.MARCH, 2001), new Double(17519));
        t1.add(new Day(12, SerialDate.MARCH, 2001), new Double(17442));
        t1.add(new Day(15, SerialDate.MARCH, 2001), new Double(17583));
        t1.add(new Day(16, SerialDate.MARCH, 2001), new Double(17319));
        t1.add(new Day(17, SerialDate.MARCH, 2001), new Double(17305));
        t1.add(new Day(18, SerialDate.MARCH, 2001), new Double(17475));
        t1.add(new Day(19, SerialDate.MARCH, 2001), new Double(17238));
        t1.add(new Day(22, SerialDate.MARCH, 2001), new Double(17016));
        t1.add(new Day(23, SerialDate.MARCH, 2001), new Double(17236));
        t1.add(new Day(24, SerialDate.MARCH, 2001), new Double(17254));
        t1.add(new Day(25, SerialDate.MARCH, 2001), new Double(17015));
        t1.add(new Day(26, SerialDate.MARCH, 2001), new Double(17028));
        t1.add(new Day(29, SerialDate.MARCH, 2001), new Double(17011));
        t1.add(new Day(30, SerialDate.MARCH, 2001), new Double(17095));
        t1.add(new Day(31, SerialDate.MARCH, 2001), new Double(16910));
    } catch (Exception e) {
        System.err.println(e.getMessage());
    }
    return t1;
}

public static void main(String[] args) {

    TestChart h = new TestChart();
    h.displayVerticallyCombinedChart(args.length==0);

}

}