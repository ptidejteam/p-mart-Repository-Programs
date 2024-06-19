/* =======================================
 * JFreeChart : a Java Chart Class Library
 * =======================================
 *
 * Project Info:  http://www.jrefinery.com/jfreechart;
 * Project Lead:  David Gilbert (david.gilbert@jrefinery.com);
 *
 * This file...
 * $Id: ChartFactory.java,v 1.1 2007/10/10 18:54:39 vauchers Exp $
 *
 * Original Author:  David Gilbert;
 * Contributor(s):   Serge V. Grachov;
 *                   Joao Guilherme Del Valle;
 *                   Bill Kelemen;
 *
 * (C) Copyright 2001 Simba Management Limited;
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
 * Changes
 * -------
 * 19-Oct-2001 : Version 1, most methods transferred from JFreeChart.java (DG);
 * 22-Oct-2001 : Added methods to create stacked bar charts (DG);
 *               Renamed DataSource.java --> Dataset.java etc. (DG);
 * 31-Oct-2001 : Added 3D-effect vertical bar and stacked-bar charts, contributed by
 *               Serge V. Grachov (DG);
 * 07-Nov-2001 : Added a flag to control whether or not a legend is added to the chart (DG);
 * 17-Nov-2001 : For pie chart, changed dataset from CategoryDataset to PieDataset (DG);
 * 30-Nov-2001 : Removed try/catch handlers from chart creation, as the exception are now
 *               RuntimeExceptions, as suggested by Joao Guilherme Del Valle (DG);
 * 06-Dec-2001 : Added createCombinableXXXXXCharts methods (BK);
 *
 */

package com.jrefinery.chart;

import java.awt.*;
import com.jrefinery.data.*;

/**
 * Factory class for creating ready-made charts.
 */
public class ChartFactory {

    /**
     * Creates a vertical bar chart with default settings.
     * @param title The chart title.
     * @param categoryAxisLabel The label for the category axis.
     * @param valueAxisLabel The label for the value axis.
     * @param data The dataset for the chart.
     * @param legend A flag specifying whether or not a legend is required.
     */
    public static JFreeChart createVerticalBarChart(String title,
                                                    String categoryAxisLabel, String valueAxisLabel,
                                                    CategoryDataset data, boolean legend) {

        Axis categoryAxis = new HorizontalCategoryAxis(categoryAxisLabel);
        Axis valueAxis = new VerticalNumberAxis(valueAxisLabel);
        Plot plot = new VerticalBarPlot(categoryAxis, valueAxis);
        JFreeChart chart = new JFreeChart(data, plot, title, JFreeChart.DEFAULT_TITLE_FONT, legend);

        return chart;

    }

    /**
     * Creates a vertical 3D-effect bar chart with default settings.
     * <P>
     * Added by Serge V. Grachov.
     * @param title The chart title.
     * @param categoryAxisLabel The label for the category axis.
     * @param valueAxisLabel The label for the value axis.
     * @param data The dataset for the chart.
     * @param legend A flag specifying whether or not a legend is required.
     */
    public static JFreeChart createVerticalBarChart3D(String title, String categoryAxisLabel,
                                                    String valueAxisLabel, CategoryDataset data,
                                                    boolean legend) {

        Axis categoryAxis = new HorizontalCategoryAxis(categoryAxisLabel);
        Axis valueAxis = new VerticalNumberAxis3D(valueAxisLabel);
        VerticalBarPlot plot = new VerticalBarPlot3D(categoryAxis, valueAxis);
        // the insets here are a workaround for the fact that the plot area is no longer a
        // rectangle, so it is overlapping the title.  To be fixed...
        plot.setInsets(new Insets(20, 2, 2, 2));
        plot.setRenderer(new VerticalBarRenderer3D());
        JFreeChart chart = new JFreeChart(data, plot, title, JFreeChart.DEFAULT_TITLE_FONT, legend);

        return chart;

    }

    /**
     * Creates a stacked vertical bar chart with default settings.  This is still experimental at
     * this point!
     * @param title The chart title.
     * @param categoryAxisLabel The label for the category axis.
     * @param valueAxisLabel The label for the value axis.
     * @param data The dataset for the chart.
     * @param legend A flag specifying whether or not a legend is required.
     */
    public static JFreeChart createStackedVerticalBarChart(String title, String categoryAxisLabel,
                                                       String valueAxisLabel, CategoryDataset data,
                                                       boolean legend) {

        Axis categoryAxis = new HorizontalCategoryAxis(categoryAxisLabel);
        Axis valueAxis = new VerticalNumberAxis(valueAxisLabel);
        VerticalBarPlot plot = new VerticalBarPlot(categoryAxis, valueAxis);
        plot.setRenderer(new StackedVerticalBarRenderer());
        JFreeChart chart = new JFreeChart(data, plot, title, JFreeChart.DEFAULT_TITLE_FONT, legend);
        return chart;

    }

    /**
     * Creates a stacked vertical bar chart with default settings.  This is still experimental at
     * this point!
     * <P>
     * Added by Serge V. Grachov.
     * @param title The chart title.
     * @param categoryAxisLabel The label for the category axis.
     * @param valueAxisLabel The label for the value axis.
     * @param data The dataset for the chart.
     * @param legend A flag specifying whether or not a legend is required.
     */
    public static JFreeChart createStackedVerticalBarChart3D(String title, String categoryAxisLabel,
                                                        String valueAxisLabel, CategoryDataset data,
                                                        boolean legend) {

        Axis categoryAxis = new HorizontalCategoryAxis(categoryAxisLabel);
        Axis valueAxis = new VerticalNumberAxis3D(valueAxisLabel);
        VerticalBarPlot plot = new VerticalBarPlot3D(categoryAxis, valueAxis);
        // the insets here are a workaround for the fact that the plot area is no longer a
        // rectangle, so it is overlapping the title.  To be fixed...
        plot.setInsets(new Insets(20, 2, 2, 2));
        plot.setRenderer(new StackedVerticalBarRenderer3D());
        JFreeChart chart = new JFreeChart(data, plot, title, JFreeChart.DEFAULT_TITLE_FONT, legend);
        return chart;

    }

    /**
     * Creates a horizontal bar chart with default settings.
     * @param title The chart title.
     * @param categoryAxisLabel The label for the category axis.
     * @param valueAxisLabel The label for the value axis.
     * @param data The dataset for the chart.
     * @param legend A flag specifying whether or not a legend is required.
     */
    public static JFreeChart createHorizontalBarChart(String title, String categoryAxisLabel,
                                                      String valueAxisLabel, CategoryDataset data,
                                                      boolean legend) {

        Axis categoryAxis = new VerticalCategoryAxis(categoryAxisLabel);
        Axis valueAxis = new HorizontalNumberAxis(valueAxisLabel);
        Plot plot = new HorizontalBarPlot(valueAxis, categoryAxis);
        JFreeChart chart = new JFreeChart(data, plot, title, JFreeChart.DEFAULT_TITLE_FONT, legend);
        return chart;

    }

    /**
     * Creates a stacked horizontal bar chart with default settings.  This is still experimental at
     * this point!
     * @param title The chart title.
     * @param categoryAxisLabel The label for the category axis.
     * @param valueAxisLabel The label for the value axis.
     * @param data The dataset for the chart.
     * @param legend A flag specifying whether or not a legend is required.
     */
    public static JFreeChart createStackedHorizontalBarChart(String title, String categoryAxisLabel,
                                                      String valueAxisLabel, CategoryDataset data,
                                                      boolean legend) {

        Axis categoryAxis = new VerticalCategoryAxis(categoryAxisLabel);
        Axis valueAxis = new HorizontalNumberAxis(valueAxisLabel);
        HorizontalBarPlot plot = new HorizontalBarPlot(valueAxis, categoryAxis);
        plot.setRenderer(new StackedHorizontalBarRenderer());
        JFreeChart chart = new JFreeChart(data, plot, title, JFreeChart.DEFAULT_TITLE_FONT, legend);
        return chart;

    }

    /**
     * Creates a line chart with default settings.
     * @param title The chart title.
     * @param categoryAxisLabel The label for the category axis.
     * @param valueAxisLabel The label for the value axis.
     * @param data The dataset for the chart.
     * @param legend A flag specifying whether or not a legend is required.
     */
    public static JFreeChart createLineChart(String title, String categoryAxisLabel,
                                                 String valueAxisLabel, CategoryDataset data,
                                                 boolean legend) {

        Axis categoryAxis = new HorizontalCategoryAxis(categoryAxisLabel);
        Axis valueAxis = new VerticalNumberAxis(valueAxisLabel);
        Plot plot = new LinePlot(categoryAxis, valueAxis);
        JFreeChart chart = new JFreeChart(data, plot, title, JFreeChart.DEFAULT_TITLE_FONT, legend);
        return chart;

    }

    /**
     * Creates a pie chart with default settings.
     * @param title The chart title.
     * @param data The dataset for the chart.
     * @param legend A flag specifying whether or not a legend is required.
     */
    public static JFreeChart createPieChart(String title, PieDataset data, boolean legend) {

        Plot plot = new PiePlot();
        JFreeChart chart = new JFreeChart(data, plot, title, JFreeChart.DEFAULT_TITLE_FONT, legend);
        return chart;

    }

    /**
     * Creates an XY (line) plot with default settings.
     * @param title The chart title.
     * @param xAxisLabel A label for the X-axis.
     * @param yAxisLabel A label for the Y-axis.
     * @param data The dataset for the chart.
     * @param legend A flag specifying whether or not a legend is required.
     */
    public static JFreeChart createXYChart(String title, String xAxisLabel, String yAxisLabel,
                                               XYDataset data, boolean legend) {

        NumberAxis xAxis = new HorizontalNumberAxis(xAxisLabel);
        xAxis.setAutoRangeIncludesZero(false);
        Axis yAxis = new VerticalNumberAxis(yAxisLabel);
        XYPlot plot = new XYPlot(xAxis, yAxis);
        plot.setXYItemRenderer(new StandardXYItemRenderer(StandardXYItemRenderer.LINES));
        JFreeChart chart = new JFreeChart(data, plot, title, JFreeChart.DEFAULT_TITLE_FONT, legend);
        return chart;

    }

    /**
     * Creates a scatter plot with default settings.
     * @param title The chart title.
     * @param xAxisLabel A label for the X-axis.
     * @param yAxisLabel A label for the Y-axis.
     * @param data The dataset for the chart.
     * @param legend A flag specifying whether or not a legend is required.
     */
    public static JFreeChart createScatterPlot(String title, String xAxisLabel, String yAxisLabel,
                                               XYDataset data, boolean legend) {

        Axis xAxis = new HorizontalNumberAxis(xAxisLabel);
        Axis yAxis = new VerticalNumberAxis(yAxisLabel);
        XYPlot plot = new XYPlot(xAxis, yAxis);
        plot.setXYItemRenderer(new StandardXYItemRenderer(StandardXYItemRenderer.SHAPES));
        JFreeChart chart = new JFreeChart(data, plot, title, JFreeChart.DEFAULT_TITLE_FONT, legend);
        return chart;

    }

    /**
     * Creates and returns a time series chart.  A time series chart is an XYPlot with a date
     * axis (horizontal) and a number axis (vertical), and each data item is connected with a line.
     * <P>
     * Note that you can supply a TimeSeriesDataset to this method as it is a subclass of
     * XYDataset.
     * @param title The chart title.
     * @param timeAxisLabel A label for the time axis.
     * @param valueAxisLabel A label for the value axis.
     * @param data The dataset for the chart.
     * @param legend A flag specifying whether or not a legend is required.
     */
    public static JFreeChart createTimeSeriesChart(String title, String timeAxisLabel,
                                                   String valueAxisLabel, XYDataset data,
                                                   boolean legend) {

        Axis timeAxis = new HorizontalDateAxis(timeAxisLabel);
        NumberAxis valueAxis = new VerticalNumberAxis(valueAxisLabel);
        valueAxis.setAutoRangeIncludesZero(false);  // override default
        XYPlot plot = new XYPlot(timeAxis, valueAxis);
        plot.setXYItemRenderer(new StandardXYItemRenderer(StandardXYItemRenderer.LINES));
        JFreeChart chart = new JFreeChart(data, plot, title, JFreeChart.DEFAULT_TITLE_FONT, legend);
        return chart;

    }

    /**
     * Creates and returns a default instance of a VerticalXYBarChart based on the specified
     * dataset.
     * @param title The chart title.
     * @param xAxisLabel A label for the X-axis.
     * @param yAxisLabel A label for the Y-axis.
     * @param data The dataset for the chart.
     * @param legend A flag specifying whether or not a legend is required.
     */
    public static JFreeChart createVerticalXYBarChart(String title, String xAxisLabel,
                                                      String yAxisLabel, IntervalXYDataset data,
                                                      boolean legend) {

        HorizontalDateAxis timeAxis = new HorizontalDateAxis(xAxisLabel);
        Axis valueAxis = new VerticalNumberAxis(yAxisLabel);
        Plot plot = new VerticalXYBarPlot(timeAxis, valueAxis, new Insets(0,0,0,0), Color.white,
        new BasicStroke(), Color.gray);
        JFreeChart chart = new JFreeChart(data, plot, title, JFreeChart.DEFAULT_TITLE_FONT, legend);
        return chart;

    }

    /**
     * Creates and returns a default instance of a high-low-open-close chart based on the specified
     * dataset.
     * <P>
     * Added by Andrzej Porebski.  Amended by David Gilbert.
     * @param title The chart title.
     * @param timeAxisLabel A label for the time axis.
     * @param valueAxisLabel A label for the value axis.
     * @param data The dataset for the chart.
     * @param legend A flag specifying whether or not a legend is required.
     */
    public static JFreeChart createHighLowChart(String title, String timeAxisLabel,
                                                String valueAxisLabel, HighLowDataset data,
                                                boolean legend) {

        Axis timeAxis = new HorizontalDateAxis(timeAxisLabel);
        NumberAxis valueAxis = new VerticalNumberAxis(valueAxisLabel);
        HighLowPlot plot = new HighLowPlot(timeAxis, valueAxis);
        JFreeChart chart = new JFreeChart(data, plot, title, JFreeChart.DEFAULT_TITLE_FONT, legend);
        return chart;

    }

    ////////////////////////////////////////////////////////////////////////////
    // Factory methogs for Combinable objects
    ////////////////////////////////////////////////////////////////////////////

    /**
     * Creates a combinable XY (line) plot with default settings.
     * @author Bill Kelemen.
     * @param horizontal The horizontal axis
     * @param vertical The vertical axis
     * @param data The dataset for the chart.
     */
    public static CombinedChart createCombinableXYChart(Axis horizontal, Axis vertical, Dataset data) {
	try {
            XYPlot plot = new XYPlot(horizontal, vertical);
            plot.setXYItemRenderer(new StandardXYItemRenderer(StandardXYItemRenderer.LINES));
            return createCombinableChart(data, plot);
	}
	catch (AxisNotCompatibleException e) {
            // this won't happen unless you mess with the axis constructors above
	    System.err.println("ChartFactory.createCombinableXYChart(...) : axis not compatible.");
	}
	catch (PlotNotCompatibleException e) {
            // this won't happen unless you mess with the axis constructors above
	    System.err.println("ChartFactory.createCombinableXYChart(...) : plot not compatible.");
	}
        return null;
    }

    /**
     * Creates and returns a combinable time series chart.  A time series chart is an XYPlot with a date
     * axis (horizontal) and a number axis (vertical), and each data item is connected with a line.
     * <P>
     * @author Bill Kelemen.
     * @param horizontal The horizontal axis
     * @param vertical The vertical axis
     * @param data The dataset for the chart.
     */
    public static CombinedChart createCombinableTimeSeriesChart(Axis horizontal, Axis vertical, Dataset data) {
	try {
	    XYPlot plot = new XYPlot(horizontal, vertical);
            plot.setXYItemRenderer(new StandardXYItemRenderer(StandardXYItemRenderer.LINES));
            return createCombinableChart(data, plot);
	}
	catch (AxisNotCompatibleException e) {
            // this won't happen unless you mess with the axis constructors above
	    System.err.println("ChartFactory.createCombinableTimeSeriesChart(...) : axis not compatible.");
	}
	catch (PlotNotCompatibleException e) {
            // this won't happen unless you mess with the axis constructors above
	    System.err.println("ChartFactory.createCombinableTimeSeriesChart(...) : plot not compatible.");
	}
        return null;
    }

    /**
     * Creates and returns a default instance of a high-low-open-close combinable chart based on the specified
     * dataset.
     * <P>
     * @author Bill Kelemen.
     * @param horizontal The horizontal axis
     * @param vertical The vertical axis
     * @param data The dataset for the chart.
     */
    public static CombinedChart createCombinableHighLowChart(Axis horizontal, Axis vertical, Dataset data) {
	try {
	    HighLowPlot plot = new HighLowPlot(horizontal, vertical);
            return createCombinableChart(data, plot);
	}
	catch (AxisNotCompatibleException e) {
            // this won't happen unless you mess with the axis constructors above
	    System.err.println("ChartFactory.createCombinableHighLowChart(...) : axis not compatible.");
	}
	catch (PlotNotCompatibleException e) {
            // this won't happen unless you mess with the axis constructors above
	    System.err.println("ChartFactory.createCombinableHighLowChart(...) : plot not compatible.");
	}
        return null;
    }


    /**
     * Creates and returns a default instance of a VerticalXYBar combinable chart based on the specified
     * dataset.
     * <P>
     * @author Bill Kelemen.
     * @param horizontal The horizontal axis
     * @param vertical The vertical axis
     * @param data The dataset for the chart.
     */
    public static CombinedChart createCombinableVerticalXYBarChart(Axis horizontal, Axis vertical, Dataset data) {
	try {
            VerticalXYBarPlot plot = new VerticalXYBarPlot(horizontal, vertical);
            return createCombinableChart(data, plot);
	}
	catch (AxisNotCompatibleException e) {
            // this won't happen unless you mess with the axis constructors above
	    System.err.println("ChartFactory.createCombinableVerticalXYBarChart(...) : axis not compatible.");
	}
	catch (PlotNotCompatibleException e) {
            // this won't happen unless you mess with the axis constructors above
	    System.err.println("ChartFactory.createCombinableVerticalXYBarChart(...) : plot not compatible.");
	}
        return null;
    }


    /**
     * Creates and returns a Combinable Chart.
     * <P>
     * @author Bill Kelemen.
     * @param data Dataset to use for the chart
     * @param plot Plot to use for the chart
     */
    public static CombinedChart createCombinableChart(Dataset data, Plot plot) {
        CombinedChart chart = new CombinedChart(data, plot);
        chart.setChartBackgroundPaint(null);
        return chart;
    }

}