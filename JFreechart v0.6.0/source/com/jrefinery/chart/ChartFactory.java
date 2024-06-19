/* =======================================
 * JFreeChart : a Java Chart Class Library
 * =======================================
 *
 * Project Info:  http://www.jrefinery.com/jfreechart;
 * Project Lead:  David Gilbert (david.gilbert@jrefinery.com);
 *
 * This file...
 * $Id: ChartFactory.java,v 1.1 2007/10/10 18:53:20 vauchers Exp $
 *
 * Original Author:  David Gilbert;
 * Contributor(s):   Serge V. Grachov;
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
 * 19-Oct-2001 : Initial implementation - most methods transferred from JFreeChart.java (DG);
 * 22-Oct-2001 : Added methods to create stacked bar charts (DG);
 *               Renamed DataSource.java --> Dataset.java etc. (DG);
 * 31-Oct-2001 : Added 3D-effect vertical bar and stacked-bar charts, contributed by
 *               Serge V. Grachov (DG);
 * 07-Nov-2001 : Added a flag to control whether or not a legend is added to the chart (DG);
 * 17-Nov-2001 : For pie chart, changed dataset from CategoryDataset to PieDataset (DG);
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
    public static JFreeChart createVerticalBarChart(String title, String categoryAxisLabel,
                                                    String valueAxisLabel, CategoryDataset data,
                                                    boolean legend) {

        JFreeChart chart = null;

        try {
            Axis categoryAxis = new HorizontalCategoryAxis(categoryAxisLabel);
            Axis valueAxis = new VerticalNumberAxis(valueAxisLabel);
    	    Plot plot = new VerticalBarPlot(categoryAxis, valueAxis);
            chart = new JFreeChart(data, plot, title, JFreeChart.DEFAULT_TITLE_FONT, legend);
        }
	catch (AxisNotCompatibleException e) {
            // this won't happen unless you mess with the axis constructors above
	    System.err.println("ChartFactory.createVerticalBarChart(...) : axis not compatible.");
        }
	catch (PlotNotCompatibleException e) {
            // this won't happen unless you mess with the axis constructors above
	    System.err.println("ChartFactory.createVerticalBarChart(...) : plot not compatible.");
        }

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

        JFreeChart chart = null;

        try {
            Axis categoryAxis = new HorizontalCategoryAxis(categoryAxisLabel);
            Axis valueAxis = new VerticalNumberAxis3D(valueAxisLabel);
    	    VerticalBarPlot plot = new VerticalBarPlot3D(categoryAxis, valueAxis);
            // the insets here are a workaround for the fact that the plot area is no longer a
            // rectangle, so it is overlapping the title.  To be fixed...
            plot.setInsets(new Insets(20, 2, 2, 2));
            plot.setRenderer(new VerticalBarRenderer3D());
            chart = new JFreeChart(data, plot, title, JFreeChart.DEFAULT_TITLE_FONT, legend);
        }
	catch (AxisNotCompatibleException e) {
            // this won't happen unless you mess with the axis constructors above
	    System.err.println("ChartFactory.createVerticalBarChart3D(...) : axis not compatible.");
        }
	catch (PlotNotCompatibleException e) {
            // this won't happen unless you mess with the axis constructors above
	    System.err.println("ChartFactory.createVerticalBarChart3D(...) : plot not compatible.");
        }

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

        JFreeChart chart = null;

        try {
            Axis categoryAxis = new HorizontalCategoryAxis(categoryAxisLabel);
            Axis valueAxis = new VerticalNumberAxis(valueAxisLabel);
    	    VerticalBarPlot plot = new VerticalBarPlot(categoryAxis, valueAxis);
            plot.setRenderer(new StackedVerticalBarRenderer());
            chart = new JFreeChart(data, plot, title, JFreeChart.DEFAULT_TITLE_FONT, legend);
        }
	catch (AxisNotCompatibleException e) {
            // this won't happen unless you mess with the axis constructors above
	    System.err.println(
                "ChartFactory.createStackedVerticalBarChart(...) : axis not compatible.");
        }
	catch (PlotNotCompatibleException e) {
            // this won't happen unless you mess with the axis constructors above
	    System.err.println(
                "ChartFactory.createStackedVerticalBarChart(...) : plot not compatible.");
        }

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

        JFreeChart chart = null;

        try {
            Axis categoryAxis = new HorizontalCategoryAxis(categoryAxisLabel);
            Axis valueAxis = new VerticalNumberAxis3D(valueAxisLabel);
    	    VerticalBarPlot plot = new VerticalBarPlot3D(categoryAxis, valueAxis);
            // the insets here are a workaround for the fact that the plot area is no longer a
            // rectangle, so it is overlapping the title.  To be fixed...
            plot.setInsets(new Insets(20, 2, 2, 2));
            plot.setRenderer(new StackedVerticalBarRenderer3D());
            chart = new JFreeChart(data, plot, title, JFreeChart.DEFAULT_TITLE_FONT, legend);
        }
	catch (AxisNotCompatibleException e) {
            // this won't happen unless you mess with the axis constructors above
	    System.err.println(
                "ChartFactory.createStackedVerticalBarChart3D(...) : axis not compatible.");
        }
	catch (PlotNotCompatibleException e) {
            // this won't happen unless you mess with the axis constructors above
	    System.err.println(
                "ChartFactory.createStackedVerticalBarChart3D(...) : plot not compatible.");
        }

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

        JFreeChart chart = null;

        try {
	    Axis categoryAxis = new VerticalCategoryAxis(categoryAxisLabel);
	    Axis valueAxis = new HorizontalNumberAxis(valueAxisLabel);
	    Plot plot = new HorizontalBarPlot(valueAxis, categoryAxis);
	    chart = new JFreeChart(data, plot, title, JFreeChart.DEFAULT_TITLE_FONT, legend);
	}
	catch (AxisNotCompatibleException e) {
            // this won't happen unless you mess with the axis constructors above
	    System.err.println("ChartFactory.createHorizontalBarChart(...) : axis not compatible.");
        }
	catch (PlotNotCompatibleException e) {
            // this won't happen unless you mess with the axis constructors above
	    System.err.println("ChartFactory.createHorizontalBarChart(...) : plot not compatible.");
        }

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

        JFreeChart chart = null;

        try {
            Axis categoryAxis = new VerticalCategoryAxis(categoryAxisLabel);
            Axis valueAxis = new HorizontalNumberAxis(valueAxisLabel);
    	    HorizontalBarPlot plot = new HorizontalBarPlot(valueAxis, categoryAxis);
            plot.setRenderer(new StackedHorizontalBarRenderer());
            chart = new JFreeChart(data, plot, title, JFreeChart.DEFAULT_TITLE_FONT, legend);
        }
	catch (AxisNotCompatibleException e) {
            // this won't happen unless you mess with the axis constructors above
	    System.err.println(
                "ChartFactory.createStackedHorizontalBarChart(...) : axis not compatible.");
        }
	catch (PlotNotCompatibleException e) {
            // this won't happen unless you mess with the axis constructors above
	    System.err.println(
                "ChartFactory.createStackedHorizontalBarChart(...) : plot not compatible.");
        }

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

        JFreeChart chart = null;

	try {
    	    Axis categoryAxis = new HorizontalCategoryAxis(categoryAxisLabel);
	    Axis valueAxis = new VerticalNumberAxis(valueAxisLabel);
	    Plot plot = new LinePlot(categoryAxis, valueAxis);
	    chart = new JFreeChart(data, plot, title, JFreeChart.DEFAULT_TITLE_FONT, legend);
	}
	catch (AxisNotCompatibleException e) {
            // this won't happen unless you mess with the axis constructors above
	    System.err.println("ChartFactory.createLineChart(...) : axis not compatible.");
	}
	catch (PlotNotCompatibleException e) {
            // this won't happen unless you mess with the axis constructors above
	    System.err.println("ChartFactory.createLineChart(...) : plot not compatible.");
	}

        return chart;

    }

    /**
     * Creates a pie chart with default settings.
     * @param title The chart title.
     * @param data The dataset for the chart.
     * @param legend A flag specifying whether or not a legend is required.
     */
    public static JFreeChart createPieChart(String title, PieDataset data, boolean legend) {

        JFreeChart chart = null;

        try {
	    Plot plot = new PiePlot();
	    chart = new JFreeChart(data, plot, title, JFreeChart.DEFAULT_TITLE_FONT, legend);
	}
	catch (AxisNotCompatibleException e) {
            // can't see how this could happen for a pie chart
	    System.err.println("ChartFactory.createPieChart(...) : axis not compatible.");
	}
        catch (PlotNotCompatibleException e) {
            // can't see how this could happen for a pie chart
	    System.err.println("ChartFactory.createPieChart(...) : plot not compatible.");
	}

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

        JFreeChart chart = null;

	try {
            NumberAxis xAxis = new HorizontalNumberAxis(xAxisLabel);
            xAxis.setAutoRangeIncludesZero(false);
	    Axis yAxis = new VerticalNumberAxis(yAxisLabel);
            XYPlot plot = new XYPlot(xAxis, yAxis);
            plot.setXYItemRenderer(new StandardXYItemRenderer(StandardXYItemRenderer.LINES));
	    chart = new JFreeChart(data, plot, title, JFreeChart.DEFAULT_TITLE_FONT, legend);
	}
	catch (AxisNotCompatibleException e) {
            // this won't happen unless you mess with the axis constructors above
	    System.err.println("ChartFactory.createXYChart(...) : axis not compatible.");
	}
	catch (PlotNotCompatibleException e) {
            // this won't happen unless you mess with the axis constructors above
	    System.err.println("ChartFactory.createXYChart(...) : plot not compatible.");
	}

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

        JFreeChart chart = null;

	try {
            Axis xAxis = new HorizontalNumberAxis(xAxisLabel);
	    Axis yAxis = new VerticalNumberAxis(yAxisLabel);
            XYPlot plot = new XYPlot(xAxis, yAxis);
            plot.setXYItemRenderer(new StandardXYItemRenderer(StandardXYItemRenderer.SHAPES));
	    chart = new JFreeChart(data, plot, title, JFreeChart.DEFAULT_TITLE_FONT, legend);
	}
	catch (AxisNotCompatibleException e) {
            // this won't happen unless you mess with the axis constructors above
	    System.err.println("ChartFactory.createScatterPlot(...) : axis not compatible.");
	}
	catch (PlotNotCompatibleException e) {
            // this won't happen unless you mess with the axis constructors above
	    System.err.println("ChartFactory.createScatterPlot(...) : plot not compatible.");
	}

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

        JFreeChart chart = null;

	try {
    	    Axis timeAxis = new HorizontalDateAxis(timeAxisLabel);
	    NumberAxis valueAxis = new VerticalNumberAxis(valueAxisLabel);
            valueAxis.setAutoRangeIncludesZero(false);  // override default
	    XYPlot plot = new XYPlot(timeAxis, valueAxis);
            plot.setXYItemRenderer(new StandardXYItemRenderer(StandardXYItemRenderer.LINES));
	    chart = new JFreeChart(data, plot, title, JFreeChart.DEFAULT_TITLE_FONT, legend);
	}
	catch (AxisNotCompatibleException e) {
            // this won't happen unless you mess with the axis constructors above
	    System.err.println("ChartFactory.createTimeSeriesChart(...) : axis not compatible.");
	}
	catch (PlotNotCompatibleException e) {
            // this won't happen unless you mess with the axis constructors above
	    System.err.println("ChartFactory.createTimeSeriesChart(...) : axis not compatible.");
	}

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

        JFreeChart chart = null;

	try {
	    HorizontalDateAxis timeAxis = new HorizontalDateAxis(xAxisLabel);
	    Axis valueAxis = new VerticalNumberAxis(yAxisLabel);
	    Plot plot = new VerticalXYBarPlot(timeAxis, valueAxis, new Insets(0,0,0,0), Color.white,
            new BasicStroke(), Color.gray);
	    chart = new JFreeChart(data, plot, title, JFreeChart.DEFAULT_TITLE_FONT, legend);
	}
	catch (AxisNotCompatibleException e) {
            // this won't happen unless you mess with the axis constructors above
	    System.err.println("ChartFactory.createVerticalXYBarChart(...) : axis not compatible.");
	}
	catch (PlotNotCompatibleException e) {
            // this won't happen unless you mess with the axis constructors above
	    System.err.println("ChartFactory.createVerticalXYBarChart(...) : plot not compatible.");
	}

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

        JFreeChart chart = null;

	try {
	    Axis timeAxis = new HorizontalDateAxis(timeAxisLabel);
	    NumberAxis valueAxis = new VerticalNumberAxis(valueAxisLabel);
	    HighLowPlot plot = new HighLowPlot(timeAxis, valueAxis);
	    chart = new JFreeChart(data, plot, title, JFreeChart.DEFAULT_TITLE_FONT, legend);
	}
	catch (AxisNotCompatibleException e) {
            // this won't happen unless you mess with the axis constructors above
	    System.err.println("ChartFactory.createHighLowChart(...) : axis not compatible.");
	}
	catch (PlotNotCompatibleException e) {
            // this won't happen unless you mess with the axis constructors above
	    System.err.println("ChartFactory.createHighLowChart(...) : plot not compatible.");
	}

        return chart;

    }

}