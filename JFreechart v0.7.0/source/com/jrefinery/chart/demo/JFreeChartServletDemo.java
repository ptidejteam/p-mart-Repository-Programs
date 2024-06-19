/* ===============
 * JFreeChart Demo
 * ===============
 *
 * Project Info:  http://www.jrefinery.com/jfreechart;
 * Project Lead:  David Gilbert (david.gilbert@jrefinery.com);
 *
 * (C) Copyright 2000, 2001 by Simba Management Limited and Contributors.
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
 * --------------------------
 * JFreeChartServletDemo.java
 * --------------------------
 * (C) Copyright 2001 by Wolfgang Irler and Contributors.
 *
 * Original Author:  Wolfgang Irler;
 * Contributor(s):   David Gilbert;
 *
 * $Id: JFreeChartServletDemo.java,v 1.1 2007/10/10 18:54:39 vauchers Exp $
 *
 * Changes
 * -------
 * 03-Dec-2001 : Version 1, contributed by Wolfgang Irler (DG);
 * 10-Dec-2001 : Removed one demo dataset, replaced with call to DemoDatasetFactory class (DG);
 *
 */

package com.jrefinery.chart.demo;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.awt.image.*;
import java.io.*;
import java.util.*;
import javax.swing.*;

import javax.servlet.*;
import javax.servlet.http.*;

import com.sun.image.codec.jpeg.*;

import com.jrefinery.chart.*;
import com.jrefinery.chart.data.*;
import com.jrefinery.chart.ui.*;
import com.jrefinery.data.*;
import com.jrefinery.ui.*;


/**
 * A servlet demonstration, contributed by Wolfgang Irler.
 */
public class JFreeChartServletDemo extends HttpServlet {

    /**
     * Utility method to return a color.  Corresponds to the color selection in the
     * HTML form.
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
     * Creates and returns a category dataset for the demo charts.
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
     */
    private Date createDate(int year, int month, int day) {
        GregorianCalendar calendar = new GregorianCalendar(year, month, day);
        return calendar.getTime();
    }

    /**
     * Returns a java.util.Date for the specified year, month, day, hour and minute.
     */
    private Date createDateTime(int year, int month, int day, int hour, int minute) {
        GregorianCalendar calendar = new GregorianCalendar(year, month, day, hour, minute);
        return calendar.getTime();
    }

    /**
     * Creates and returns a XYDataset for the demo charts.
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
     */
    public HighLowDataset createHighLowDataset() {

        Object[][][] data = new Object[][][] { {
            { createDate(1999, Calendar.JANUARY,4), new Double(47) },
            { createDate(1999, Calendar.JANUARY,4), new Double(33) },
            { createDate(1999, Calendar.JANUARY,4), new Double(35) },
            { createDate(1999, Calendar.JANUARY,4), new Double(33) },

            { createDate(1999, Calendar.JANUARY,5), new Double(47) },
            { createDate(1999, Calendar.JANUARY,5), new Double(32) },
            { createDate(1999, Calendar.JANUARY,5), new Double(41) },
            { createDate(1999, Calendar.JANUARY,5), new Double(37) },

            { createDate(1999, Calendar.JANUARY,6), new Double(49) },
            { createDate(1999, Calendar.JANUARY,6), new Double(43) },
            { createDate(1999, Calendar.JANUARY,6), new Double(46) },
            { createDate(1999, Calendar.JANUARY,6), new Double(48) },

            { createDate(1999, Calendar.JANUARY,7), new Double(51) },
            { createDate(1999, Calendar.JANUARY,7), new Double(39) },
            { createDate(1999, Calendar.JANUARY,7), new Double(40) },
            { createDate(1999, Calendar.JANUARY,7), new Double(47) },

            { createDate(1999, Calendar.JANUARY,8), new Double(60) },
            { createDate(1999, Calendar.JANUARY,8), new Double(40) },
            { createDate(1999, Calendar.JANUARY,8), new Double(46) },
            { createDate(1999, Calendar.JANUARY,8), new Double(53) },

            { createDate(1999, Calendar.JANUARY,9), new Double(62) },
            { createDate(1999, Calendar.JANUARY,9), new Double(55) },
            { createDate(1999, Calendar.JANUARY,9), new Double(57) },
            { createDate(1999, Calendar.JANUARY,9), new Double(61) },

            { createDate(1999, Calendar.JANUARY,10), new Double(65) },
            { createDate(1999, Calendar.JANUARY,10), new Double(56) },
            { createDate(1999, Calendar.JANUARY,10), new Double(62) },
            { createDate(1999, Calendar.JANUARY,10), new Double(59) },

            { createDate(1999, Calendar.JANUARY,11), new Double(55) },
            { createDate(1999, Calendar.JANUARY,11), new Double(43) },
            { createDate(1999, Calendar.JANUARY,11), new Double(45) },
            { createDate(1999, Calendar.JANUARY,11), new Double(47) },

            { createDate(1999, Calendar.JANUARY,12), new Double(54) },
            { createDate(1999, Calendar.JANUARY,12), new Double(33) },
            { createDate(1999, Calendar.JANUARY,12), new Double(40) },
            { createDate(1999, Calendar.JANUARY,12), new Double(51) },

            { createDate(1999, Calendar.JANUARY,13), new Double(58) },
            { createDate(1999, Calendar.JANUARY,13), new Double(42) },
            { createDate(1999, Calendar.JANUARY,13), new Double(44) },
            { createDate(1999, Calendar.JANUARY,13), new Double(57) },

            { createDate(1999, Calendar.JANUARY,14), new Double(54) },
            { createDate(1999, Calendar.JANUARY,14), new Double(38) },
            { createDate(1999, Calendar.JANUARY,14), new Double(43) },
            { createDate(1999, Calendar.JANUARY,14), new Double(52) },

            { createDate(1999, Calendar.JANUARY,15), new Double(48) },
            { createDate(1999, Calendar.JANUARY,15), new Double(41) },
            { createDate(1999, Calendar.JANUARY,15), new Double(44) },
            { createDate(1999, Calendar.JANUARY,15), new Double(41) },

            { createDate(1999, Calendar.JANUARY,17), new Double(60) },
            { createDate(1999, Calendar.JANUARY,17), new Double(30) },
            { createDate(1999, Calendar.JANUARY,17), new Double(34) },
            { createDate(1999, Calendar.JANUARY,17), new Double(44) },

            { createDate(1999, Calendar.JANUARY,18), new Double(58) },
            { createDate(1999, Calendar.JANUARY,18), new Double(44) },
            { createDate(1999, Calendar.JANUARY,18), new Double(54) },
            { createDate(1999, Calendar.JANUARY,18), new Double(56) },

            { createDate(1999, Calendar.JANUARY,19), new Double(54) },
            { createDate(1999, Calendar.JANUARY,19), new Double(32) },
            { createDate(1999, Calendar.JANUARY,19), new Double(42) },
            { createDate(1999, Calendar.JANUARY,19), new Double(53) },

            { createDate(1999, Calendar.JANUARY,20), new Double(53) },
            { createDate(1999, Calendar.JANUARY,20), new Double(39) },
            { createDate(1999, Calendar.JANUARY,20), new Double(50) },
            { createDate(1999, Calendar.JANUARY,20), new Double(49) },

            { createDate(1999, Calendar.JANUARY,21), new Double(47) },
            { createDate(1999, Calendar.JANUARY,21), new Double(38) },
            { createDate(1999, Calendar.JANUARY,21), new Double(41) },
            { createDate(1999, Calendar.JANUARY,21), new Double(40) },

            { createDate(1999, Calendar.JANUARY,22), new Double(55) },
            { createDate(1999, Calendar.JANUARY,22), new Double(37) },
            { createDate(1999, Calendar.JANUARY,22), new Double(43) },
            { createDate(1999, Calendar.JANUARY,22), new Double(45) },

            { createDate(1999, Calendar.JANUARY,23), new Double(54) },
            { createDate(1999, Calendar.JANUARY,23), new Double(42) },
            { createDate(1999, Calendar.JANUARY,23), new Double(50) },
            { createDate(1999, Calendar.JANUARY,23), new Double(42) },

            { createDate(1999, Calendar.JANUARY,24), new Double(48) },
            { createDate(1999, Calendar.JANUARY,24), new Double(37) },
            { createDate(1999, Calendar.JANUARY,24), new Double(37) },
            { createDate(1999, Calendar.JANUARY,24), new Double(47) },

            { createDate(1999, Calendar.JANUARY,25), new Double(58) },
            { createDate(1999, Calendar.JANUARY,25), new Double(33) },
            { createDate(1999, Calendar.JANUARY,25), new Double(39) },
            { createDate(1999, Calendar.JANUARY,25), new Double(41) },

            { createDate(1999, Calendar.JANUARY,26), new Double(47) },
            { createDate(1999, Calendar.JANUARY,26), new Double(31) },
            { createDate(1999, Calendar.JANUARY,26), new Double(36) },
            { createDate(1999, Calendar.JANUARY,26), new Double(41) },

            { createDate(1999, Calendar.JANUARY,27), new Double(58) },
            { createDate(1999, Calendar.JANUARY,27), new Double(44) },
            { createDate(1999, Calendar.JANUARY,27), new Double(49) },
            { createDate(1999, Calendar.JANUARY,27), new Double(44) },

            { createDate(1999, Calendar.JANUARY,28), new Double(46) },
            { createDate(1999, Calendar.JANUARY,28), new Double(41) },
            { createDate(1999, Calendar.JANUARY,28), new Double(43) },
            { createDate(1999, Calendar.JANUARY,28), new Double(44) },

            { createDate(1999, Calendar.JANUARY,29), new Double(56) },
            { createDate(1999, Calendar.JANUARY,29), new Double(39) },
            { createDate(1999, Calendar.JANUARY,29), new Double(39) },
            { createDate(1999, Calendar.JANUARY,29), new Double(51) },

            { createDate(1999, Calendar.JANUARY,30), new Double(56) },
            { createDate(1999, Calendar.JANUARY,30), new Double(39) },
            { createDate(1999, Calendar.JANUARY,30), new Double(47) },
            { createDate(1999, Calendar.JANUARY,30), new Double(49) },

            { createDate(1999, Calendar.JANUARY,31), new Double(53) },
            { createDate(1999, Calendar.JANUARY,31), new Double(39) },
            { createDate(1999, Calendar.JANUARY,31), new Double(52) },
            { createDate(1999, Calendar.JANUARY,31), new Double(47) },

            { createDate(1999, Calendar.FEBRUARY,1), new Double(51) },
            { createDate(1999, Calendar.FEBRUARY,1), new Double(30) },
            { createDate(1999, Calendar.FEBRUARY,1), new Double(45) },
            { createDate(1999, Calendar.FEBRUARY,1), new Double(47) },

            { createDate(1999, Calendar.FEBRUARY,2), new Double(47) },
            { createDate(1999, Calendar.FEBRUARY,2), new Double(30) },
            { createDate(1999, Calendar.FEBRUARY,2), new Double(34) },
            { createDate(1999, Calendar.FEBRUARY,2), new Double(46) },

            { createDate(1999, Calendar.FEBRUARY,3), new Double(57) },
            { createDate(1999, Calendar.FEBRUARY,3), new Double(37) },
            { createDate(1999, Calendar.FEBRUARY,3), new Double(44) },
            { createDate(1999, Calendar.FEBRUARY,3), new Double(56) },

            { createDate(1999, Calendar.FEBRUARY,4), new Double(49) },
            { createDate(1999, Calendar.FEBRUARY,4), new Double(40) },
            { createDate(1999, Calendar.FEBRUARY,4), new Double(47) },
            { createDate(1999, Calendar.FEBRUARY,4), new Double(44) },

            { createDate(1999, Calendar.FEBRUARY,5), new Double(46) },
            { createDate(1999, Calendar.FEBRUARY,5), new Double(38) },
            { createDate(1999, Calendar.FEBRUARY,5), new Double(43) },
            { createDate(1999, Calendar.FEBRUARY,5), new Double(40) },

            { createDate(1999, Calendar.FEBRUARY,6), new Double(55) },
            { createDate(1999, Calendar.FEBRUARY,6), new Double(38) },
            { createDate(1999, Calendar.FEBRUARY,6), new Double(39) },
            { createDate(1999, Calendar.FEBRUARY,6), new Double(53) },

            { createDate(1999, Calendar.FEBRUARY,7), new Double(50) },
            { createDate(1999, Calendar.FEBRUARY,7), new Double(33) },
            { createDate(1999, Calendar.FEBRUARY,7), new Double(37) },
            { createDate(1999, Calendar.FEBRUARY,7), new Double(37) },

            { createDate(1999, Calendar.FEBRUARY,8), new Double(59) },
            { createDate(1999, Calendar.FEBRUARY,8), new Double(34) },
            { createDate(1999, Calendar.FEBRUARY,8), new Double(57) },
            { createDate(1999, Calendar.FEBRUARY,8), new Double(43) },

            { createDate(1999, Calendar.FEBRUARY,9), new Double(48) },
            { createDate(1999, Calendar.FEBRUARY,9), new Double(39) },
            { createDate(1999, Calendar.FEBRUARY,9), new Double(46) },
            { createDate(1999, Calendar.FEBRUARY,9), new Double(47) },

            { createDate(1999, Calendar.FEBRUARY,10), new Double(55) },
            { createDate(1999, Calendar.FEBRUARY,10), new Double(30) },
            { createDate(1999, Calendar.FEBRUARY,10), new Double(37) },
            { createDate(1999, Calendar.FEBRUARY,10), new Double(30) },

            { createDate(1999, Calendar.FEBRUARY,11), new Double(60) },
            { createDate(1999, Calendar.FEBRUARY,11), new Double(32) },
            { createDate(1999, Calendar.FEBRUARY,11), new Double(56) },
            { createDate(1999, Calendar.FEBRUARY,11), new Double(36) },

            { createDate(1999, Calendar.FEBRUARY,12), new Double(56) },
            { createDate(1999, Calendar.FEBRUARY,12), new Double(42) },
            { createDate(1999, Calendar.FEBRUARY,12), new Double(53) },
            { createDate(1999, Calendar.FEBRUARY,12), new Double(54) },

            { createDate(1999, Calendar.FEBRUARY,13), new Double(49) },
            { createDate(1999, Calendar.FEBRUARY,13), new Double(42) },
            { createDate(1999, Calendar.FEBRUARY,13), new Double(45) },
            { createDate(1999, Calendar.FEBRUARY,13), new Double(42) },

            { createDate(1999, Calendar.FEBRUARY,14), new Double(55) },
            { createDate(1999, Calendar.FEBRUARY,14), new Double(42) },
            { createDate(1999, Calendar.FEBRUARY,14), new Double(47) },
            { createDate(1999, Calendar.FEBRUARY,14), new Double(54) },

            { createDate(1999, Calendar.FEBRUARY,15), new Double(49) },
            { createDate(1999, Calendar.FEBRUARY,15), new Double(35) },
            { createDate(1999, Calendar.FEBRUARY,15), new Double(38) },
            { createDate(1999, Calendar.FEBRUARY,15), new Double(35) },

            { createDate(1999, Calendar.FEBRUARY,16), new Double(47) },
            { createDate(1999, Calendar.FEBRUARY,16), new Double(38) },
            { createDate(1999, Calendar.FEBRUARY,16), new Double(43) },
            { createDate(1999, Calendar.FEBRUARY,16), new Double(42) },

            { createDate(1999, Calendar.FEBRUARY,17), new Double(53) },
            { createDate(1999, Calendar.FEBRUARY,17), new Double(42) },
            { createDate(1999, Calendar.FEBRUARY,17), new Double(47) },
            { createDate(1999, Calendar.FEBRUARY,17), new Double(48) },

            { createDate(1999, Calendar.FEBRUARY,18), new Double(47) },
            { createDate(1999, Calendar.FEBRUARY,18), new Double(44) },
            { createDate(1999, Calendar.FEBRUARY,18), new Double(46) },
            { createDate(1999, Calendar.FEBRUARY,18), new Double(44) },

            { createDate(1999, Calendar.FEBRUARY,19), new Double(46) },
            { createDate(1999, Calendar.FEBRUARY,19), new Double(40) },
            { createDate(1999, Calendar.FEBRUARY,19), new Double(43) },
            { createDate(1999, Calendar.FEBRUARY,19), new Double(44) },

            { createDate(1999, Calendar.FEBRUARY,20), new Double(48) },
            { createDate(1999, Calendar.FEBRUARY,20), new Double(41) },
            { createDate(1999, Calendar.FEBRUARY,20), new Double(46) },
            { createDate(1999, Calendar.FEBRUARY,20), new Double(41) } }
        };

        return null;  // broken, needs fixing...
        //return new DefaultXYDataset(new String[] { "IBM" }, data);

    }



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
                chart.setChartBackgroundPaint(new GradientPaint(0, 0, getColor( initGradColor ), 1000, 0, getColor( finalGradColor )));
                Plot bPlot = chart.getPlot();
                HorizontalCategoryAxis cAxis = (HorizontalCategoryAxis)bPlot.getAxis(Plot.HORIZONTAL_AXIS);
                cAxis.setVerticalCategoryLabels(true);
                return chart;

            case 2:
                chart = ChartFactory.createHorizontalBarChart("Horizontal Bar Chart",
                                                              "Categories",
                                                              "Values",
                                                              categoryData, true);
                chart.setChartBackgroundPaint(new GradientPaint(0, 0, getColor( initGradColor ), 1000, 0, getColor( finalGradColor )));
                return chart;

            case 3:
                chart = ChartFactory.createLineChart("Line Chart",
                                                     "Categories",
                                                     "Values",
                                                     categoryData, true);
                chart.setChartBackgroundPaint(new GradientPaint(0, 0, getColor( initGradColor ), 1000, 0, getColor( finalGradColor )));
                return chart;

            case 4:
                XYDataset xyData = new SampleXYDataset();
                chart = ChartFactory.createXYChart("XY Plot",
                                                   "X",
                                                   "Y",
                                                   xyData, true);
                chart.setChartBackgroundPaint(new GradientPaint(0, 0, getColor( initGradColor ), 1000, 0, getColor( finalGradColor )));
                Plot xyPlot = chart.getPlot();
                //NumberAxis hhAxis = (NumberAxis) xyPlot.getAxis(Plot.HORIZONTAL_AXIS);
                //hhAxis.setAutoTickValue(false);
                //hhAxis.setTickValue(new Double(3.0));
                return chart;

            case 5:
                XYDataset xyData1 = DemoDatasetFactory.createTimeSeriesCollection3();
                //XYDataSource xyData2 = createTestXYDataSource();
                chart = ChartFactory.createTimeSeriesChart("Time Series Chart",
                                                           "Date",
                                                           "USD per GBP",
                                                           xyData1, true);
                //StandardTitle title = (StandardTitle)chart.getTitle();
                //if (title==null) {
                //    title = new StandardTitle("Value of GBP", new Font("Arial", Font.BOLD, 12));
               // }
                //title.setTitle("Value of GBP");
                chart.setChartBackgroundPaint(new GradientPaint(0, 0, getColor( initGradColor ), 1000, 0, getColor( finalGradColor )));
                //Plot myPlot = chart.getPlot();
                //Axis myVerticalAxis = myPlot.getAxis(Plot.VERTICAL_AXIS);
                //myVerticalAxis.setLabel("USD per GBP");
                //DateAxis myHorizontalAxis = (DateAxis) myPlot.getAxis(Plot.HORIZONTAL_AXIS);
                //myHorizontalAxis.setAutoTickValue(false);
                //myHorizontalAxis.setAutoRange(false);
                //myHorizontalAxis.setMinimumDate(new GregorianCalendar(1999, 4, 1).getTime());
                //myHorizontalAxis.setMaximumDate(new GregorianCalendar(1999, 5, 1).getTime());
                //myHorizontalAxis.setAutoTickValue(false);
                //myHorizontalAxis.getTickLabelFormatter().applyPattern("d-MMM-y");
                //myHorizontalAxis.setTickUnit(new DateUnit(Calendar.DATE, 3));

                VerticalNumberAxis vnAxis = (VerticalNumberAxis)chart.getPlot().getAxis(Plot.VERTICAL_AXIS);
                //vnAxis.setMinimumAxisValue(new Double(10000.0));
                //vnAxis.setMaximumAxisValue(new Double(11000.0));
                //vnAxis.setAutoRange(false);
                //vnAxis.setAutoTickUnits(false);
                vnAxis.setAutoRangeIncludesZero(false);
                //vnAxis.setTickUnits(new Double(0.020));
                //vnAxis.getTickLabelFormatter().applyLocalizedPattern("0.0000");
                chart.setDataset(xyData1);
                return chart;

            case 6:
                categoryData = createCategoryDataset();
                PieDataset pieData = Datasets.createPieDataset(categoryData, 0);
                chart = ChartFactory.createPieChart("Pie Chart", pieData, true);
                chart.setChartBackgroundPaint(new GradientPaint(0, 0, getColor( initGradColor ), 1000, 0, getColor( finalGradColor )));
                return chart;

            case 7:
                // Added by Andrzej Porebski
                HighLowDataset hlData = createHighLowDataset();
                chart = ChartFactory.createHighLowChart("High-Low-Open-Close IBM",
                                                        "Date",
                                                        "Price",
                                                        hlData, true);
                //title = (StandardTitle)chart.getTitle();
                //title.setTitle("High-Low-Open-Close IBM");
                chart.setChartBackgroundPaint(new GradientPaint(0, 0, getColor( initGradColor ), 1000, 0, getColor( finalGradColor )));
                Plot myPlot = chart.getPlot();
                Axis myVerticalAxis = myPlot.getAxis(Plot.VERTICAL_AXIS);
                myVerticalAxis.setLabel("Price in ($) per share");
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
                chart.setChartBackgroundPaint(new GradientPaint(0, 0, getColor( initGradColor ), 1000, 0, getColor( finalGradColor )));
                myPlot = chart.getPlot();
                myVerticalAxis = myPlot.getAxis(Plot.VERTICAL_AXIS);
                myVerticalAxis.setLabel("USD per GBP");
	        vnAxis = (VerticalNumberAxis)chart.getPlot().getAxis(Plot.VERTICAL_AXIS);
                vnAxis.setAutoRangeIncludesZero(false);
                chart.setDataset(xyData2);
                return chart;

            case 9:
   	        // linear fit
                XYDataset xyData3 = DemoDatasetFactory.createTimeSeriesCollection2();
                pf = new PlotFit(xyData3, new LinearPlotFitAlgorithm());
                xyData3 = pf.getFit();
                chart = ChartFactory.createTimeSeriesChart("Linear Fit", "Date", "Value",
                                                           xyData3, true);
                //title = (StandardTitle)chart.getTitle();
                //title.setTitle("Linear Fit of GBP");
                //chart.setChartBackgroundPaint(new GradientPaint(0, 0, getColor( initGradColor ), 1000, 0, getColor( finalGradColor )));
                myPlot = chart.getPlot();
                myVerticalAxis = myPlot.getAxis(Plot.VERTICAL_AXIS);
                myVerticalAxis.setLabel("USD per GBP");

	        vnAxis = (VerticalNumberAxis)chart.getPlot().getAxis(Plot.VERTICAL_AXIS);
                vnAxis.setAutoRangeIncludesZero(false);
                chart.setDataset(xyData3);
                return chart;

            default:
                return null;

        }

        }
        catch (Exception e) {
            return null;
        }

    }


    ServletContext context = null;

    /**
     * Override init() to set up data used by invocations of this servlet.
     */
    public void init(ServletConfig config) throws ServletException {
        super.init(config);

        // save servlet context
        context = config.getServletContext();
    }

    /**
     * Basic servlet method, answers requests fromt the browser.
     * @param request HTTPServletRequest
     * @param response HTTPServletResponse
     */
    public void doGet(HttpServletRequest request,
                     HttpServletResponse response) throws ServletException, IOException {

        response.setContentType("image/jpeg");
        int type = 1;
        try {
            type = Integer.parseInt( request.getParameter( "type" ) );
        }
        catch (Exception e) {
        }

        int  initGradColor= 0;
        int  finalGradColor= 0;
        try {
            initGradColor = Integer.parseInt( request.getParameter( "initGradColor" ) );
            finalGradColor = Integer.parseInt( request.getParameter( "finalGradColor" ) );
        }
        catch (Exception e) {
        }

        JFreeChart chart = createChart( type, initGradColor, finalGradColor );

        int width = 400;
        int height = 300;
        try {
            width = Integer.parseInt( request.getParameter( "width" ) );
            height = Integer.parseInt( request.getParameter( "height" ) );
        }
        catch (Exception e) {
        }

        //BufferedImage img = draw( chart, width, height );
        OutputStream out = response.getOutputStream();
//        BufferedImage image = chart.createBufferedImage(width, height);
//        JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(out);
//        JPEGEncodeParam param = encoder.getDefaultJPEGEncodeParam(image);
//        param.setQuality(1.0f, true);
//        encoder.encode(image, param);
        ChartUtilities.writeChartAsJPEG(out, chart, width, height);
        out.close();
    }

//    protected BufferedImage draw(JFreeChart chart, int width, int height) {
//
//        BufferedImage img = new BufferedImage(width , height,
//                                              BufferedImage.TYPE_INT_RGB);
//        Graphics2D g2 = img.createGraphics();
//        chart.draw(g2, new Rectangle2D.Double(0, 0, width, height));
//        g2.dispose();
//        return img;
//    }

}