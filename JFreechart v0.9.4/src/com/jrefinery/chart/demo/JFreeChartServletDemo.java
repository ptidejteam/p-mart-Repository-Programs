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
 * --------------------------
 * JFreeChartServletDemo.java
 * --------------------------
 * (C) Copyright 2001, 2002, by Wolfgang Irler and Contributors.
 *
 * Original Author:  Wolfgang Irler;
 * Contributor(s):   David Gilbert;
 *
 * $Id: JFreeChartServletDemo.java,v 1.1 2007/10/10 21:48:02 vauchers Exp $
 *
 * Changes
 * -------
 * 03-Dec-2001 : Version 1, contributed by Wolfgang Irler (DG);
 * 10-Dec-2001 : Removed one demo dataset, replaced with call to DemoDatasetFactory class (DG);
 * 26-Jun-2002 : Updated imports (DG);
 * 11-Oct-2002 : Fixed errors reported by Checkstyle (DG);
 *
 */

package com.jrefinery.chart.demo;

import java.awt.Color;
import java.awt.GradientPaint;
import java.io.OutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.Calendar;
import java.util.GregorianCalendar;
import javax.servlet.ServletContext;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.jrefinery.chart.JFreeChart;
import com.jrefinery.chart.ChartFactory;
import com.jrefinery.chart.ChartUtilities;
import com.jrefinery.chart.Plot;
import com.jrefinery.chart.CategoryPlot;
import com.jrefinery.chart.XYPlot;
import com.jrefinery.chart.Axis;
import com.jrefinery.chart.HorizontalCategoryAxis;
import com.jrefinery.chart.NumberAxis;
import com.jrefinery.chart.VerticalNumberAxis;
import com.jrefinery.chart.data.PlotFit;
import com.jrefinery.chart.data.LinearPlotFitAlgorithm;
import com.jrefinery.chart.data.MovingAveragePlotFitAlgorithm;
import com.jrefinery.data.CategoryDataset;
import com.jrefinery.data.DefaultCategoryDataset;
import com.jrefinery.data.PieDataset;
import com.jrefinery.data.HighLowDataset;
import com.jrefinery.data.XYDataset;
import com.jrefinery.data.DefaultXYDataset;
import com.jrefinery.data.DatasetUtilities;

/**
 * A servlet demonstration, contributed by Wolfgang Irler.
 *
 * @author WI
 */
public class JFreeChartServletDemo extends HttpServlet {

    /**
     * Utility method to return a color.  Corresponds to the color selection in the
     * HTML form.
     *
     * @param color  the color index.
     *
     * @return a color.
     */
    protected Color getColor(int color) {

        switch (color % 11) {
            case 0: return Color.white;
            case 1: return Color.black;
            case 2: return Color.blue;
            case 3: return Color.green;
            case 4: return Color.red;
            case 5: return Color.yellow;
            case 6: return Color.gray;
            case 7 : return Color.orange;
            case 8: return Color.cyan;
            case 9: return Color.magenta;
            case 10: return Color.pink;
            default: return Color.white;
        }

    }

    /**
     * Creates and returns a sample category dataset for the demo charts.
     *
     * @return a sample category dataset.
     */
    public CategoryDataset createCategoryDataset() {

        Number[][] data = new Integer[][] {

            { new Integer(10), new Integer(4), new Integer(15), new Integer(14) },
            { new Integer(5), new Integer(7), new Integer(14), new Integer(3) },
            { new Integer(6), new Integer(17), new Integer(12), new Integer(7) },
            { new Integer(7), new Integer(15), new Integer(11), new Integer(0) },
            { new Integer(8), new Integer(6), new Integer(10), new Integer(9) },
            { new Integer(9), new Integer(8), new Integer(8), new Integer(6) },
            { new Integer(10), new Integer(9), new Integer(7), new Integer(7) },
            { new Integer(11), new Integer(13), new Integer(9), new Integer(9) },
            { new Integer(3), new Integer(7), new Integer(11), new Integer(10) }
        };

        return new DefaultCategoryDataset(data);

    }

    /**
     * Returns a java.util.Date for the specified year, month and day.
     *
     * @param year  the year.
     * @param month  the month.
     * @param day  the day.
     *
     * @return the date.
     */
    private Date createDate(int year, int month, int day) {
        GregorianCalendar calendar = new GregorianCalendar(year, month, day);
        return calendar.getTime();
    }

    /**
     * Returns a java.util.Date for the specified year, month, day, hour and minute.
     *
     * @param year  the year.
     * @param month  the month.
     * @param day  the day.
     * @param hour  the hour.
     * @param minute  the minute.
     *
     * @return the date.
     */
    private Date createDateTime(int year, int month, int day, int hour, int minute) {
        GregorianCalendar calendar = new GregorianCalendar(year, month, day, hour, minute);
        return calendar.getTime();
    }

    /**
     * Creates and returns a XYDataset for the demo charts.
     *
     * @return a sample XY dateset.
     */
    public XYDataset createTestXYDataset() {

        Object[][][] data = new Object[][][] { {
            { createDateTime(2000, Calendar.OCTOBER, 18, 9, 5), new Double(10921.0) },
            { createDateTime(2000, Calendar.OCTOBER, 18, 10, 6), new Double(10886.7) },
            { createDateTime(2000, Calendar.OCTOBER, 18, 11, 6), new Double(10846.6) },
            { createDateTime(2000, Calendar.OCTOBER, 18, 12, 6), new Double(10843.7) },
            { createDateTime(2000, Calendar.OCTOBER, 18, 13, 6), new Double(10841.2) },
            { createDateTime(2000, Calendar.OCTOBER, 18, 14, 6), new Double(10830.7) },
            { createDateTime(2000, Calendar.OCTOBER, 18, 15, 6), new Double(10795.8) },
            { createDateTime(2000, Calendar.OCTOBER, 18, 16, 7), new Double(10733.8) }
        } };

        return new DefaultXYDataset(data);
    }


    /**
     * Creates and returns a sample high-low dataset for the demo.  Added by Andrzej Porebski.
     *
     * @return a sample high low dataset.
     */
    public HighLowDataset createHighLowDataset() {

        Object[][][] data = new Object[][][] { {
            { createDate(1999, Calendar.JANUARY, 4), new Double(47) },
            { createDate(1999, Calendar.JANUARY, 4), new Double(33) },
            { createDate(1999, Calendar.JANUARY, 4), new Double(35) },
            { createDate(1999, Calendar.JANUARY, 4), new Double(33) },

            { createDate(1999, Calendar.JANUARY, 5), new Double(47) },
            { createDate(1999, Calendar.JANUARY, 5), new Double(32) },
            { createDate(1999, Calendar.JANUARY, 5), new Double(41) },
            { createDate(1999, Calendar.JANUARY, 5), new Double(37) },

            { createDate(1999, Calendar.JANUARY, 6), new Double(49) },
            { createDate(1999, Calendar.JANUARY, 6), new Double(43) },
            { createDate(1999, Calendar.JANUARY, 6), new Double(46) },
            { createDate(1999, Calendar.JANUARY, 6), new Double(48) },

            { createDate(1999, Calendar.JANUARY, 7), new Double(51) },
            { createDate(1999, Calendar.JANUARY, 7), new Double(39) },
            { createDate(1999, Calendar.JANUARY, 7), new Double(40) },
            { createDate(1999, Calendar.JANUARY, 7), new Double(47) },

            { createDate(1999, Calendar.JANUARY, 8), new Double(60) },
            { createDate(1999, Calendar.JANUARY, 8), new Double(40) },
            { createDate(1999, Calendar.JANUARY, 8), new Double(46) },
            { createDate(1999, Calendar.JANUARY, 8), new Double(53) },

            { createDate(1999, Calendar.JANUARY, 9), new Double(62) },
            { createDate(1999, Calendar.JANUARY, 9), new Double(55) },
            { createDate(1999, Calendar.JANUARY, 9), new Double(57) },
            { createDate(1999, Calendar.JANUARY, 9), new Double(61) },

            { createDate(1999, Calendar.JANUARY, 10), new Double(65) },
            { createDate(1999, Calendar.JANUARY, 10), new Double(56) },
            { createDate(1999, Calendar.JANUARY, 10), new Double(62) },
            { createDate(1999, Calendar.JANUARY, 10), new Double(59) },

            { createDate(1999, Calendar.JANUARY, 11), new Double(55) },
            { createDate(1999, Calendar.JANUARY, 11), new Double(43) },
            { createDate(1999, Calendar.JANUARY, 11), new Double(45) },
            { createDate(1999, Calendar.JANUARY, 11), new Double(47) },

            { createDate(1999, Calendar.JANUARY, 12), new Double(54) },
            { createDate(1999, Calendar.JANUARY, 12), new Double(33) },
            { createDate(1999, Calendar.JANUARY, 12), new Double(40) },
            { createDate(1999, Calendar.JANUARY, 12), new Double(51) },

            { createDate(1999, Calendar.JANUARY, 13), new Double(58) },
            { createDate(1999, Calendar.JANUARY, 13), new Double(42) },
            { createDate(1999, Calendar.JANUARY, 13), new Double(44) },
            { createDate(1999, Calendar.JANUARY, 13), new Double(57) },

            { createDate(1999, Calendar.JANUARY, 14), new Double(54) },
            { createDate(1999, Calendar.JANUARY, 14), new Double(38) },
            { createDate(1999, Calendar.JANUARY, 14), new Double(43) },
            { createDate(1999, Calendar.JANUARY, 14), new Double(52) },

            { createDate(1999, Calendar.JANUARY, 15), new Double(48) },
            { createDate(1999, Calendar.JANUARY, 15), new Double(41) },
            { createDate(1999, Calendar.JANUARY, 15), new Double(44) },
            { createDate(1999, Calendar.JANUARY, 15), new Double(41) },

            { createDate(1999, Calendar.JANUARY, 17), new Double(60) },
            { createDate(1999, Calendar.JANUARY, 17), new Double(30) },
            { createDate(1999, Calendar.JANUARY, 17), new Double(34) },
            { createDate(1999, Calendar.JANUARY, 17), new Double(44) },

            { createDate(1999, Calendar.JANUARY, 18), new Double(58) },
            { createDate(1999, Calendar.JANUARY, 18), new Double(44) },
            { createDate(1999, Calendar.JANUARY, 18), new Double(54) },
            { createDate(1999, Calendar.JANUARY, 18), new Double(56) },

            { createDate(1999, Calendar.JANUARY, 19), new Double(54) },
            { createDate(1999, Calendar.JANUARY, 19), new Double(32) },
            { createDate(1999, Calendar.JANUARY, 19), new Double(42) },
            { createDate(1999, Calendar.JANUARY, 19), new Double(53) },

            { createDate(1999, Calendar.JANUARY, 20), new Double(53) },
            { createDate(1999, Calendar.JANUARY, 20), new Double(39) },
            { createDate(1999, Calendar.JANUARY, 20), new Double(50) },
            { createDate(1999, Calendar.JANUARY, 20), new Double(49) },

            { createDate(1999, Calendar.JANUARY, 21), new Double(47) },
            { createDate(1999, Calendar.JANUARY, 21), new Double(38) },
            { createDate(1999, Calendar.JANUARY, 21), new Double(41) },
            { createDate(1999, Calendar.JANUARY, 21), new Double(40) },

            { createDate(1999, Calendar.JANUARY, 22), new Double(55) },
            { createDate(1999, Calendar.JANUARY, 22), new Double(37) },
            { createDate(1999, Calendar.JANUARY, 22), new Double(43) },
            { createDate(1999, Calendar.JANUARY, 22), new Double(45) },

            { createDate(1999, Calendar.JANUARY, 23), new Double(54) },
            { createDate(1999, Calendar.JANUARY, 23), new Double(42) },
            { createDate(1999, Calendar.JANUARY, 23), new Double(50) },
            { createDate(1999, Calendar.JANUARY, 23), new Double(42) },

            { createDate(1999, Calendar.JANUARY, 24), new Double(48) },
            { createDate(1999, Calendar.JANUARY, 24), new Double(37) },
            { createDate(1999, Calendar.JANUARY, 24), new Double(37) },
            { createDate(1999, Calendar.JANUARY, 24), new Double(47) },

            { createDate(1999, Calendar.JANUARY, 25), new Double(58) },
            { createDate(1999, Calendar.JANUARY, 25), new Double(33) },
            { createDate(1999, Calendar.JANUARY, 25), new Double(39) },
            { createDate(1999, Calendar.JANUARY, 25), new Double(41) },

            { createDate(1999, Calendar.JANUARY, 26), new Double(47) },
            { createDate(1999, Calendar.JANUARY, 26), new Double(31) },
            { createDate(1999, Calendar.JANUARY, 26), new Double(36) },
            { createDate(1999, Calendar.JANUARY, 26), new Double(41) },

            { createDate(1999, Calendar.JANUARY, 27), new Double(58) },
            { createDate(1999, Calendar.JANUARY, 27), new Double(44) },
            { createDate(1999, Calendar.JANUARY, 27), new Double(49) },
            { createDate(1999, Calendar.JANUARY, 27), new Double(44) },

            { createDate(1999, Calendar.JANUARY, 28), new Double(46) },
            { createDate(1999, Calendar.JANUARY, 28), new Double(41) },
            { createDate(1999, Calendar.JANUARY, 28), new Double(43) },
            { createDate(1999, Calendar.JANUARY, 28), new Double(44) },

            { createDate(1999, Calendar.JANUARY, 29), new Double(56) },
            { createDate(1999, Calendar.JANUARY, 29), new Double(39) },
            { createDate(1999, Calendar.JANUARY, 29), new Double(39) },
            { createDate(1999, Calendar.JANUARY, 29), new Double(51) },

            { createDate(1999, Calendar.JANUARY, 30), new Double(56) },
            { createDate(1999, Calendar.JANUARY, 30), new Double(39) },
            { createDate(1999, Calendar.JANUARY, 30), new Double(47) },
            { createDate(1999, Calendar.JANUARY, 30), new Double(49) },

            { createDate(1999, Calendar.JANUARY, 31), new Double(53) },
            { createDate(1999, Calendar.JANUARY, 31), new Double(39) },
            { createDate(1999, Calendar.JANUARY, 31), new Double(52) },
            { createDate(1999, Calendar.JANUARY, 31), new Double(47) },

            { createDate(1999, Calendar.FEBRUARY, 1), new Double(51) },
            { createDate(1999, Calendar.FEBRUARY, 1), new Double(30) },
            { createDate(1999, Calendar.FEBRUARY, 1), new Double(45) },
            { createDate(1999, Calendar.FEBRUARY, 1), new Double(47) },

            { createDate(1999, Calendar.FEBRUARY, 2), new Double(47) },
            { createDate(1999, Calendar.FEBRUARY, 2), new Double(30) },
            { createDate(1999, Calendar.FEBRUARY, 2), new Double(34) },
            { createDate(1999, Calendar.FEBRUARY, 2), new Double(46) },

            { createDate(1999, Calendar.FEBRUARY, 3), new Double(57) },
            { createDate(1999, Calendar.FEBRUARY, 3), new Double(37) },
            { createDate(1999, Calendar.FEBRUARY, 3), new Double(44) },
            { createDate(1999, Calendar.FEBRUARY, 3), new Double(56) },

            { createDate(1999, Calendar.FEBRUARY, 4), new Double(49) },
            { createDate(1999, Calendar.FEBRUARY, 4), new Double(40) },
            { createDate(1999, Calendar.FEBRUARY, 4), new Double(47) },
            { createDate(1999, Calendar.FEBRUARY, 4), new Double(44) },

            { createDate(1999, Calendar.FEBRUARY, 5), new Double(46) },
            { createDate(1999, Calendar.FEBRUARY, 5), new Double(38) },
            { createDate(1999, Calendar.FEBRUARY, 5), new Double(43) },
            { createDate(1999, Calendar.FEBRUARY, 5), new Double(40) },

            { createDate(1999, Calendar.FEBRUARY, 6), new Double(55) },
            { createDate(1999, Calendar.FEBRUARY, 6), new Double(38) },
            { createDate(1999, Calendar.FEBRUARY, 6), new Double(39) },
            { createDate(1999, Calendar.FEBRUARY, 6), new Double(53) },

            { createDate(1999, Calendar.FEBRUARY, 7), new Double(50) },
            { createDate(1999, Calendar.FEBRUARY, 7), new Double(33) },
            { createDate(1999, Calendar.FEBRUARY, 7), new Double(37) },
            { createDate(1999, Calendar.FEBRUARY, 7), new Double(37) },

            { createDate(1999, Calendar.FEBRUARY, 8), new Double(59) },
            { createDate(1999, Calendar.FEBRUARY, 8), new Double(34) },
            { createDate(1999, Calendar.FEBRUARY, 8), new Double(57) },
            { createDate(1999, Calendar.FEBRUARY, 8), new Double(43) },

            { createDate(1999, Calendar.FEBRUARY, 9), new Double(48) },
            { createDate(1999, Calendar.FEBRUARY, 9), new Double(39) },
            { createDate(1999, Calendar.FEBRUARY, 9), new Double(46) },
            { createDate(1999, Calendar.FEBRUARY, 9), new Double(47) },

            { createDate(1999, Calendar.FEBRUARY, 10), new Double(55) },
            { createDate(1999, Calendar.FEBRUARY, 10), new Double(30) },
            { createDate(1999, Calendar.FEBRUARY, 10), new Double(37) },
            { createDate(1999, Calendar.FEBRUARY, 10), new Double(30) },

            { createDate(1999, Calendar.FEBRUARY, 11), new Double(60) },
            { createDate(1999, Calendar.FEBRUARY, 11), new Double(32) },
            { createDate(1999, Calendar.FEBRUARY, 11), new Double(56) },
            { createDate(1999, Calendar.FEBRUARY, 11), new Double(36) },

            { createDate(1999, Calendar.FEBRUARY, 12), new Double(56) },
            { createDate(1999, Calendar.FEBRUARY, 12), new Double(42) },
            { createDate(1999, Calendar.FEBRUARY, 12), new Double(53) },
            { createDate(1999, Calendar.FEBRUARY, 12), new Double(54) },

            { createDate(1999, Calendar.FEBRUARY, 13), new Double(49) },
            { createDate(1999, Calendar.FEBRUARY, 13), new Double(42) },
            { createDate(1999, Calendar.FEBRUARY, 13), new Double(45) },
            { createDate(1999, Calendar.FEBRUARY, 13), new Double(42) },

            { createDate(1999, Calendar.FEBRUARY, 14), new Double(55) },
            { createDate(1999, Calendar.FEBRUARY, 14), new Double(42) },
            { createDate(1999, Calendar.FEBRUARY, 14), new Double(47) },
            { createDate(1999, Calendar.FEBRUARY, 14), new Double(54) },

            { createDate(1999, Calendar.FEBRUARY, 15), new Double(49) },
            { createDate(1999, Calendar.FEBRUARY, 15), new Double(35) },
            { createDate(1999, Calendar.FEBRUARY, 15), new Double(38) },
            { createDate(1999, Calendar.FEBRUARY, 15), new Double(35) },

            { createDate(1999, Calendar.FEBRUARY, 16), new Double(47) },
            { createDate(1999, Calendar.FEBRUARY, 16), new Double(38) },
            { createDate(1999, Calendar.FEBRUARY, 16), new Double(43) },
            { createDate(1999, Calendar.FEBRUARY, 16), new Double(42) },

            { createDate(1999, Calendar.FEBRUARY, 17), new Double(53) },
            { createDate(1999, Calendar.FEBRUARY, 17), new Double(42) },
            { createDate(1999, Calendar.FEBRUARY, 17), new Double(47) },
            { createDate(1999, Calendar.FEBRUARY, 17), new Double(48) },

            { createDate(1999, Calendar.FEBRUARY, 18), new Double(47) },
            { createDate(1999, Calendar.FEBRUARY, 18), new Double(44) },
            { createDate(1999, Calendar.FEBRUARY, 18), new Double(46) },
            { createDate(1999, Calendar.FEBRUARY, 18), new Double(44) },

            { createDate(1999, Calendar.FEBRUARY, 19), new Double(46) },
            { createDate(1999, Calendar.FEBRUARY, 19), new Double(40) },
            { createDate(1999, Calendar.FEBRUARY, 19), new Double(43) },
            { createDate(1999, Calendar.FEBRUARY, 19), new Double(44) },

            { createDate(1999, Calendar.FEBRUARY, 20), new Double(48) },
            { createDate(1999, Calendar.FEBRUARY, 20), new Double(41) },
            { createDate(1999, Calendar.FEBRUARY, 20), new Double(46) },
            { createDate(1999, Calendar.FEBRUARY, 20), new Double(41) } }
        };

        return null;  // broken, needs fixing...
        //return new DefaultXYDataset(new String[] { "IBM" }, data);

    }

    /**
     * Returns a chart.
     *
     * @param type  the chart type.
     * @param initGradColor  the first color for the gradient.
     * @param finalGradColor  the final color for the gradient.
     *
     * @return the chart.
     */
    protected JFreeChart createChart(int type, int initGradColor, int finalGradColor) {

        CategoryDataset categoryData = createCategoryDataset();
        JFreeChart chart;

        try {

        switch (type) {
            case 1:
                chart = ChartFactory.createVerticalBarChart("Vertical Bar Chart",
                                                            "Categories",
                                                            "Values",
                                                            categoryData, true);
                chart.setBackgroundPaint(new GradientPaint(0, 0, getColor(initGradColor),
                                                           1000, 0, getColor(finalGradColor)));
                CategoryPlot plot = chart.getCategoryPlot();
                HorizontalCategoryAxis axis = (HorizontalCategoryAxis) plot.getDomainAxis();
                axis.setVerticalCategoryLabels(true);
                return chart;

            case 2:
                chart = ChartFactory.createHorizontalBarChart("Horizontal Bar Chart",
                                                              "Categories",
                                                              "Values",
                                                              categoryData, true);
                chart.setBackgroundPaint(new GradientPaint(0, 0, getColor(initGradColor),
                                                           1000, 0, getColor(finalGradColor)));
                return chart;

            case 3:
                chart = ChartFactory.createLineChart("Line Chart",
                                                     "Categories",
                                                     "Values",
                                                     categoryData, true);
                chart.setBackgroundPaint(new GradientPaint(0, 0, getColor(initGradColor),
                                                           1000, 0, getColor(finalGradColor)));
                return chart;

            case 4:
                XYDataset xyData = new SampleXYDataset();
                chart = ChartFactory.createLineXYChart("XY Plot",
                                                       "X",
                                                       "Y",
                                                       xyData, true);
                chart.setBackgroundPaint(new GradientPaint(0, 0, getColor(initGradColor),
                                                           1000, 0, getColor(finalGradColor)));
                Plot xyPlot = chart.getPlot();
                return chart;

            case 5:
                XYDataset xyData1 = DemoDatasetFactory.createTimeSeriesCollection3();
                chart = ChartFactory.createTimeSeriesChart("Time Series Chart",
                                                           "Date",
                                                           "USD per GBP",
                                                           xyData1, true);
                chart.setBackgroundPaint(new GradientPaint(0, 0, getColor(initGradColor),
                                                           1000, 0, getColor(finalGradColor)));

                XYPlot plot5 = chart.getXYPlot();
                VerticalNumberAxis axis5 = (VerticalNumberAxis) plot5.getRangeAxis();
                axis5.setAutoRangeIncludesZero(false);
                plot5.setDataset(xyData1);
                return chart;

            case 6:
                categoryData = createCategoryDataset();
                PieDataset pieData = DatasetUtilities.createPieDataset(categoryData, 0);
                chart = ChartFactory.createPieChart("Pie Chart", pieData, true);
                chart.setBackgroundPaint(new GradientPaint(0, 0, getColor(initGradColor),
                                                           1000, 0, getColor(finalGradColor)));
                return chart;

            case 7:
                HighLowDataset data7 = createHighLowDataset();
                chart = ChartFactory.createHighLowChart("High-Low-Open-Close IBM",
                                                        "Date",
                                                        "Price",
                                                        data7, true);

                chart.setBackgroundPaint(new GradientPaint(0, 0, getColor(initGradColor),
                                                           1000, 0, getColor(finalGradColor)));
                XYPlot plot7 = chart.getXYPlot();
                Axis axis7 = plot7.getRangeAxis();
                axis7.setLabel("Price in ($) per share");
                return chart;

            case 8:
                // moving avg
                XYDataset xyData2 = DemoDatasetFactory.createTimeSeriesCollection3();
                MovingAveragePlotFitAlgorithm mavg = new MovingAveragePlotFitAlgorithm();
                mavg.setPeriod(30);
                PlotFit pf = new PlotFit(xyData2, mavg);
                xyData2 = pf.getFit();
                chart = ChartFactory.createTimeSeriesChart("Moving Average", "Date", "Value",
                                                           xyData2, true);
                //title = (StandardTitle)chart.getTitle();
                //title.setTitle("30 day moving average of GBP");
                chart.setBackgroundPaint(new GradientPaint(0, 0, getColor(initGradColor),
                                                           1000, 0, getColor(finalGradColor)));
                XYPlot plot8 = chart.getXYPlot();
                NumberAxis axis8 = (NumberAxis) plot8.getRangeAxis();
                axis8.setLabel("USD per GBP");
                axis8.setAutoRangeIncludesZero(false);
                return chart;

            case 9:
                // linear fit
                XYDataset data9 = DemoDatasetFactory.createTimeSeriesCollection2();
                pf = new PlotFit(data9, new LinearPlotFitAlgorithm());
                data9 = pf.getFit();
                chart = ChartFactory.createTimeSeriesChart("Linear Fit", "Date", "Value",
                                                           data9, true);
                XYPlot plot9 = chart.getXYPlot();
                NumberAxis axis9 = (NumberAxis) plot9.getRangeAxis();
                axis9.setLabel("USD per GBP");
                axis9.setAutoRangeIncludesZero(false);

                //chart.getPlot().setDataset(xyData3);
                return chart;

            default:
                return null;

        }

        }
        catch (Exception e) {
            return null;
        }

    }

    /** Servlet context. */
    private ServletContext context = null;

    /**
     * Override init() to set up data used by invocations of this servlet.
     *
     * @param config  configuration info.
     *
     * @throws ServletException if there is a problem.
     */
    public void init(ServletConfig config) throws ServletException {
        super.init(config);

        // save servlet context
        context = config.getServletContext();
    }

    /**
     * Basic servlet method, answers requests fromt the browser.
     *
     * @param request HTTPServletRequest
     * @param response HTTPServletResponse
     *
     * @throws ServletException if there is a servlet problem.
     * @throws IOException if there is an I/O problem.
     */
    public void doGet(HttpServletRequest request,
                      HttpServletResponse response) throws ServletException, IOException {

        response.setContentType("image/jpeg");
        int type = 1;
        try {
            type = Integer.parseInt(request.getParameter("type"));
        }
        catch (Exception e) {
            // suppress
        }

        int initGradColor = 0;
        int finalGradColor = 0;
        try {
            initGradColor = Integer.parseInt(request.getParameter("initGradColor"));
            finalGradColor = Integer.parseInt(request.getParameter("finalGradColor"));
        }
        catch (Exception e) {
            // suppress
        }

        JFreeChart chart = createChart(type, initGradColor, finalGradColor);

        int width = 400;
        int height = 300;
        try {
            width = Integer.parseInt(request.getParameter("width"));
            height = Integer.parseInt(request.getParameter("height"));
        }
        catch (Exception e) {
            // suppress
        }

        OutputStream out = response.getOutputStream();
        ChartUtilities.writeChartAsJPEG(out, chart, width, height);
        out.close();
    }

}
