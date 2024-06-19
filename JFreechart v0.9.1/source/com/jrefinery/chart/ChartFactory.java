/* =======================================
 * JFreeChart : a Java Chart Class Library
 * =======================================
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
 * -----------------
 * ChartFactory.java
 * -----------------
 * (C) Copyright 2001, 2002, by Simba Management Limited and Contributors.
 *
 * Original Author:  David Gilbert (for Simba Management Limited);
 * Contributor(s):   Serge V. Grachov;
 *                   Joao Guilherme Del Valle;
 *                   Bill Kelemen;
 *                   Jon Iles;
 *
 * $Id: ChartFactory.java,v 1.1 2007/10/10 19:02:26 vauchers Exp $
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
 * 12-Dec-2001 : Added createCandlestickChart(...) method (DG);
 * 13-Dec-2001 : Updated methods for charts with new renderers (DG);
 * 08-Jan-2002 : Added import for com.jrefinery.chart.combination.CombinedChart (DG);
 * 31-Jan-2002 : Changed the createCombinableVerticalXYBarChart(...) method to use renderer (DG);
 * 06-Feb-2002 : Added new method createWindPlot(...) (DG);
 * 23-Apr-2002 : Updates to the chart and plot constructor API (DG);
 * 21-May-2002 : Added new method createAreaChart(...) (JI);
 * 06-Jun-2002 : Added new method createGanttChart(...) (DG);
 * 11-Jun-2002 : Renamed createHorizontalStackedBarChart() --> createStackedHorizontalBarChart() for
 *               consistency (DG);
 *
 */

package com.jrefinery.chart;

import java.awt.Insets;
import com.jrefinery.data.PieDataset;
import com.jrefinery.data.CategoryDataset;
import com.jrefinery.data.IntervalCategoryDataset;
import com.jrefinery.data.XYDataset;
import com.jrefinery.data.IntervalXYDataset;
import com.jrefinery.data.HighLowDataset;
import com.jrefinery.data.WindDataset;
import com.jrefinery.data.SignalsDataset;
import com.jrefinery.chart.tooltips.StandardXYToolTipGenerator;
import com.jrefinery.chart.tooltips.TimeSeriesToolTipGenerator;
import com.jrefinery.chart.tooltips.HighLowToolTipGenerator;

/**
 * Factory class for creating ready-made charts.
 */
public class ChartFactory {

    /**
     * Creates a vertical bar chart with default settings.
     *
     * @param title The chart title.
     * @param categoryAxisLabel The label for the category axis.
     * @param valueAxisLabel The label for the value axis.
     * @param data The dataset for the chart.
     * @param legend A flag specifying whether or not a legend is required.
     */
    public static JFreeChart createVerticalBarChart(String title,
                                                    String categoryAxisLabel, String valueAxisLabel,
                                                    CategoryDataset data, boolean legend) {

        CategoryAxis categoryAxis = new HorizontalCategoryAxis(categoryAxisLabel);
        ValueAxis valueAxis = new VerticalNumberAxis(valueAxisLabel);
        CategoryItemRenderer renderer = new VerticalBarRenderer();
        Plot plot = new VerticalCategoryPlot(data, categoryAxis, valueAxis, renderer);
        JFreeChart chart = new JFreeChart(title, JFreeChart.DEFAULT_TITLE_FONT, plot, legend);

        return chart;

    }

    /**
     * Creates a vertical 3D-effect bar chart with default settings.
     * <P>
     * Added by Serge V. Grachov.
     *
     * @param title The chart title.
     * @param categoryAxisLabel The label for the category axis.
     * @param valueAxisLabel The label for the value axis.
     * @param data The dataset for the chart.
     * @param legend A flag specifying whether or not a legend is required.
     */
    public static JFreeChart createVerticalBarChart3D(String title, String categoryAxisLabel,
                                                    String valueAxisLabel, CategoryDataset data,
                                                    boolean legend) {

        CategoryAxis categoryAxis = new HorizontalCategoryAxis(categoryAxisLabel);
        ValueAxis valueAxis = new VerticalNumberAxis3D(valueAxisLabel);
        CategoryItemRenderer renderer = new VerticalBarRenderer3D();
        Plot plot = new VerticalCategoryPlot(data, categoryAxis, valueAxis, renderer);
        JFreeChart chart = new JFreeChart(title, JFreeChart.DEFAULT_TITLE_FONT, plot, legend);

        return chart;

    }

    /**
     * Creates a stacked vertical bar chart with default settings.
     *
     * @param title The chart title.
     * @param categoryAxisLabel The label for the category axis.
     * @param valueAxisLabel The label for the value axis.
     * @param data The dataset for the chart.
     * @param legend A flag specifying whether or not a legend is required.
     */
    public static JFreeChart createStackedVerticalBarChart(String title, String categoryAxisLabel,
                                                       String valueAxisLabel, CategoryDataset data,
                                                       boolean legend) {

        CategoryAxis categoryAxis = new HorizontalCategoryAxis(categoryAxisLabel);
        ValueAxis valueAxis = new VerticalNumberAxis(valueAxisLabel);
        CategoryItemRenderer renderer = new StackedVerticalBarRenderer();
        Plot plot = new VerticalCategoryPlot(data, categoryAxis, valueAxis, renderer);
        JFreeChart chart = new JFreeChart(title, JFreeChart.DEFAULT_TITLE_FONT, plot, legend);
        return chart;

    }

    /**
     * Creates a stacked vertical bar chart with default settings.
     *
     * @param title The chart title.
     * @param categoryAxisLabel The label for the category axis.
     * @param valueAxisLabel The label for the value axis.
     * @param data The dataset for the chart.
     * @param legend A flag specifying whether or not a legend is required.
     */
    public static JFreeChart createStackedVerticalBarChart3D(String title, String categoryAxisLabel,
                                                        String valueAxisLabel, CategoryDataset data,
                                                        boolean legend) {

        CategoryAxis categoryAxis = new HorizontalCategoryAxis(categoryAxisLabel);
        ValueAxis valueAxis = new VerticalNumberAxis3D(valueAxisLabel);
        CategoryItemRenderer renderer = new StackedVerticalBarRenderer3D();
        Plot plot = new VerticalCategoryPlot(data, categoryAxis, valueAxis, renderer);
        // the insets here are a workaround for the fact that the plot area is no longer a
        // rectangle, so it is overlapping the title.  To be fixed...
        plot.setInsets(new Insets(20, 2, 2, 2));
        JFreeChart chart = new JFreeChart(title, JFreeChart.DEFAULT_TITLE_FONT, plot, legend);
        return chart;

    }

    /**
     * Creates a horizontal bar chart with default settings.
     *
     * @param title The chart title.
     * @param domainAxisLabel The label for the category axis.
     * @param rangeAxisLabel The label for the value axis.
     * @param data The dataset for the chart.
     * @param legend A flag specifying whether or not a legend is required.
     *
     * @return A horizontal bar chart.
     */
    public static JFreeChart createHorizontalBarChart(String title,
                                                      String domainAxisLabel,
                                                      String rangeAxisLabel,
                                                      CategoryDataset data,
                                                      boolean legend) {

        CategoryAxis domainAxis = new VerticalCategoryAxis(domainAxisLabel);
        ValueAxis rangeAxis = new HorizontalNumberAxis(rangeAxisLabel);
        CategoryItemRenderer renderer = new HorizontalBarRenderer();
        Plot plot = new HorizontalCategoryPlot(data, domainAxis, rangeAxis, renderer);
        JFreeChart chart = new JFreeChart(title, JFreeChart.DEFAULT_TITLE_FONT, plot, legend);
        return chart;

    }

    /**
     * Creates a horizontal 3D-effect bar chart with default settings.
     *
     * @param title The chart title.
     * @param categoryAxisLabel The label for the category axis.
     * @param valueAxisLabel The label for the value axis.
     * @param data The dataset for the chart.
     * @param legend A flag specifying whether or not a legend is required.
     */
    public static JFreeChart createHorizontalBarChart3D(String title,
                                                        String categoryAxisLabel,
                                                        String valueAxisLabel,
                                                        CategoryDataset data,
                                                        boolean legend) {

        CategoryAxis categoryAxis = new VerticalCategoryAxis(categoryAxisLabel);
        ValueAxis valueAxis = new HorizontalNumberAxis3D(valueAxisLabel);
        CategoryItemRenderer renderer = new HorizontalBarRenderer3D();
        Plot plot = new HorizontalCategoryPlot(data, categoryAxis, valueAxis, renderer);
        plot.setForegroundAlpha(0.75f);
        JFreeChart chart = new JFreeChart(title, JFreeChart.DEFAULT_TITLE_FONT, plot, legend);

        return chart;

    }

    /**
     * Creates a stacked horizontal bar chart with default settings.
     *
     * @param title The chart title.
     * @param domainAxisLabel The label for the category axis.
     * @param rangeAxisLabel The label for the value axis.
     * @param data The dataset for the chart.
     * @param legend A flag specifying whether or not a legend is required.
     *
     * @return A stacked horizontal bar chart.
     */
    public static JFreeChart createStackedHorizontalBarChart(String title,
                                                             String domainAxisLabel,
                                                             String rangeAxisLabel,
                                                             CategoryDataset data,
                                                             boolean legend) {

        CategoryAxis domainAxis = new VerticalCategoryAxis(domainAxisLabel);
        ValueAxis rangeAxis = new HorizontalNumberAxis(rangeAxisLabel);
        CategoryItemRenderer renderer = new StackedHorizontalBarRenderer();
        Plot plot = new HorizontalCategoryPlot(data, domainAxis, rangeAxis, renderer);
        JFreeChart chart = new JFreeChart(title, JFreeChart.DEFAULT_TITLE_FONT, plot, legend);
        return chart;

    }

    /**
     * Creates a line chart with default settings.
     *
     * @param title The chart title.
     * @param categoryAxisLabel The label for the category axis.
     * @param valueAxisLabel The label for the value axis.
     * @param data The dataset for the chart.
     * @param legend A flag specifying whether or not a legend is required.
     */
    public static JFreeChart createLineChart(String title, String categoryAxisLabel,
                                                 String valueAxisLabel, CategoryDataset data,
                                                 boolean legend) {

        CategoryAxis categoryAxis = new HorizontalCategoryAxis(categoryAxisLabel);
        ValueAxis valueAxis = new VerticalNumberAxis(valueAxisLabel);
        CategoryItemRenderer renderer = new LineAndShapeRenderer();
        Plot plot = new VerticalCategoryPlot(data, categoryAxis, valueAxis, renderer);
        JFreeChart chart = new JFreeChart(title, JFreeChart.DEFAULT_TITLE_FONT, plot, legend);
        return chart;

    }

    /**
     * Creates an area chart with default settings.
     *
     * @param title The chart title.
     * @param categoryAxisLabel The label for the category axis.
     * @param valueAxisLabel The label for the value axis.
     * @param data The dataset for the chart.
     * @param legend A flag specifying whether or not a legend is required.
     */
    public static JFreeChart createAreaChart(String title,
                                             String categoryAxisLabel,
                                             String valueAxisLabel,
                                             CategoryDataset data,
                                             boolean legend) {

        CategoryAxis categoryAxis = new HorizontalCategoryAxis(categoryAxisLabel);
        ValueAxis valueAxis = new VerticalNumberAxis(valueAxisLabel);
        CategoryItemRenderer renderer = new AreaCategoryItemRenderer();
        Plot plot = new VerticalCategoryPlot(data, categoryAxis, valueAxis, renderer);
        JFreeChart chart = new JFreeChart(title, JFreeChart.DEFAULT_TITLE_FONT, plot, legend);
        return chart;

    }

    /**
     * Creates a Gantt chart with default settings.
     *
     * @param title The chart title.
     * @param categoryAxisLabel The label for the category axis.
     * @param dateAxisLabel The label for the date axis.
     * @param data The dataset for the chart.
     * @param legend A flag specifying whether or not a legend is required.
     */
    public static JFreeChart createGanttChart(String title,
                                              String categoryAxisLabel,
                                              String dateAxisLabel,
                                              IntervalCategoryDataset data,
                                              boolean legend) {

        CategoryAxis categoryAxis = new VerticalCategoryAxis(categoryAxisLabel);
        DateAxis dateAxis = new HorizontalDateAxis(dateAxisLabel);
        CategoryItemRenderer renderer = new HorizontalIntervalBarRenderer();
        Plot plot = new HorizontalCategoryPlot(data, categoryAxis, dateAxis, renderer);
        JFreeChart chart = new JFreeChart(title, JFreeChart.DEFAULT_TITLE_FONT, plot, legend);
        return chart;

    }

    /**
     * Creates a pie chart with default settings.
     *
     * @param title The chart title.
     * @param data The dataset for the chart.
     * @param legend A flag specifying whether or not a legend is required.
     */
    public static JFreeChart createPieChart(String title, PieDataset data, boolean legend) {

        Plot plot = new PiePlot(data);
        plot.setInsets(new Insets(0, 5, 5, 5));
        JFreeChart chart = new JFreeChart(title, JFreeChart.DEFAULT_TITLE_FONT, plot, legend);
        return chart;

    }

    /**
     * Creates an XY (line) plot with default settings.
     *
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
        NumberAxis yAxis = new VerticalNumberAxis(yAxisLabel);
        XYPlot plot = new XYPlot(data, xAxis, yAxis);
        plot.setXYItemRenderer(new StandardXYItemRenderer(StandardXYItemRenderer.LINES,
                                                          new StandardXYToolTipGenerator()));
        JFreeChart chart = new JFreeChart(title, JFreeChart.DEFAULT_TITLE_FONT, plot, legend);
        return chart;

    }

    /**
     * Creates an XY area plot.
     *
     * @param title The chart title.
     * @param xAxisLabel A label for the X-axis.
     * @param yAxisLabel A label for the Y-axis.
     * @param data The dataset for the chart.
     * @param legend A flag specifying whether or not a legend is required.
     */
    public static JFreeChart createAreaXYChart(String title, String xAxisLabel, String yAxisLabel,
                                               XYDataset data, boolean legend) {

        NumberAxis xAxis = new HorizontalNumberAxis(xAxisLabel);
        xAxis.setAutoRangeIncludesZero(false);
        NumberAxis yAxis = new VerticalNumberAxis(yAxisLabel);
        XYPlot plot = new XYPlot(data, xAxis, yAxis);
        plot.setXYItemRenderer(new AreaXYItemRenderer());
        JFreeChart chart = new JFreeChart(title, JFreeChart.DEFAULT_TITLE_FONT, plot, legend);
        return chart;

    }

    /**
     * Creates a scatter plot with default settings.
     *
     * @param title The chart title.
     * @param xAxisLabel A label for the X-axis.
     * @param yAxisLabel A label for the Y-axis.
     * @param data The dataset for the chart.
     * @param legend A flag specifying whether or not a legend is required.
     */
    public static JFreeChart createScatterPlot(String title, String xAxisLabel, String yAxisLabel,
                                               XYDataset data, boolean legend) {

        ValueAxis xAxis = new HorizontalNumberAxis(xAxisLabel);
        ValueAxis yAxis = new VerticalNumberAxis(yAxisLabel);
        XYPlot plot = new XYPlot(data, xAxis, yAxis);
        plot.setXYItemRenderer(new StandardXYItemRenderer(StandardXYItemRenderer.SHAPES,
                                                          new StandardXYToolTipGenerator()));
        JFreeChart chart = new JFreeChart(title, JFreeChart.DEFAULT_TITLE_FONT, plot, legend);
        return chart;

    }

    public static JFreeChart createWindPlot(String title, String xAxisLabel, String yAxisLabel,
                                               WindDataset data, boolean legend) {

        ValueAxis xAxis = new HorizontalDateAxis(xAxisLabel);
        ValueAxis yAxis = new VerticalNumberAxis(yAxisLabel);
        yAxis.setMaximumAxisValue(12.0);
        yAxis.setMinimumAxisValue(-12.0);
        XYPlot plot = new XYPlot(data, xAxis, yAxis);
        plot.setXYItemRenderer(new WindItemRenderer());
        JFreeChart chart = new JFreeChart(title, JFreeChart.DEFAULT_TITLE_FONT, plot, legend);
        return chart;

    }

    /**
     * Creates and returns a time series chart.  A time series chart is an XYPlot with a date
     * axis (horizontal) and a number axis (vertical), and each data item is connected with a line.
     * <P>
     * Note that you can supply a TimeSeriesDataset to this method as it is a subclass of
     * XYDataset.
     *
     * @param title The chart title.
     * @param timeAxisLabel A label for the time axis.
     * @param valueAxisLabel A label for the value axis.
     * @param data The dataset for the chart.
     * @param legend A flag specifying whether or not a legend is required.
     */
    public static JFreeChart createTimeSeriesChart(String title, String timeAxisLabel,
                                                   String valueAxisLabel, XYDataset data,
                                                   boolean legend) {

        ValueAxis timeAxis = new HorizontalDateAxis(timeAxisLabel);
        NumberAxis valueAxis = new VerticalNumberAxis(valueAxisLabel);
        valueAxis.setAutoRangeIncludesZero(false);  // override default
        XYPlot plot = new XYPlot(data, timeAxis, valueAxis);
        plot.setXYItemRenderer(new StandardXYItemRenderer(StandardXYItemRenderer.LINES,
                               new TimeSeriesToolTipGenerator()));
        JFreeChart chart = new JFreeChart(title, JFreeChart.DEFAULT_TITLE_FONT, plot, legend);
        return chart;

    }

    /**
     * Creates and returns a default instance of a VerticalXYBarChart based on the specified
     * dataset.
     *
     * @param title The chart title.
     * @param xAxisLabel A label for the X-axis.
     * @param yAxisLabel A label for the Y-axis.
     * @param data The dataset for the chart.
     * @param legend A flag specifying whether or not a legend is required.
     */
    public static JFreeChart createVerticalXYBarChart(String title, String xAxisLabel,
                                                      String yAxisLabel, IntervalXYDataset data,
                                                      boolean legend) {

        DateAxis dateAxis = new HorizontalDateAxis(xAxisLabel);
        ValueAxis valueAxis = new VerticalNumberAxis(yAxisLabel);
        XYPlot plot = new XYPlot(data, dateAxis, valueAxis);
        plot.setXYItemRenderer(new VerticalXYBarRenderer());
        JFreeChart chart = new JFreeChart(title, JFreeChart.DEFAULT_TITLE_FONT, plot, legend);
        return chart;

    }

    /**
     * Creates and returns a default instance of a high-low-open-close chart based on the specified
     * dataset.
     *
     * @param title The chart title.
     * @param timeAxisLabel A label for the time axis.
     * @param valueAxisLabel A label for the value axis.
     * @param data The dataset for the chart.
     * @param legend A flag specifying whether or not a legend is required.
     */
    public static JFreeChart createHighLowChart(String title, String timeAxisLabel,
                                                String valueAxisLabel, HighLowDataset data,
                                                boolean legend) {

        ValueAxis timeAxis = new HorizontalDateAxis(timeAxisLabel);
        NumberAxis valueAxis = new VerticalNumberAxis(valueAxisLabel);
        XYPlot plot = new XYPlot(data, timeAxis, valueAxis);
        plot.setXYItemRenderer(new HighLowRenderer(new HighLowToolTipGenerator()));
        JFreeChart chart = new JFreeChart(title, JFreeChart.DEFAULT_TITLE_FONT, plot, legend);
        return chart;

    }

    /**
     * Creates and returns a default instance of a candlesticks chart based on the specified
     * dataset.
     *
     * @param title The chart title.
     * @param timeAxisLabel A label for the time axis.
     * @param valueAxisLabel A label for the value axis.
     * @param data The dataset for the chart.
     * @param legend A flag specifying whether or not a legend is required.
     */
    public static JFreeChart createCandlestickChart(String title, String timeAxisLabel,
                                                    String valueAxisLabel, HighLowDataset data,
                                                    boolean legend) {

        ValueAxis timeAxis = new HorizontalDateAxis(timeAxisLabel);
        NumberAxis valueAxis = new VerticalNumberAxis(valueAxisLabel);
        XYPlot plot = new XYPlot(data, timeAxis, valueAxis);
        plot.setXYItemRenderer(new CandlestickRenderer());
        JFreeChart chart = new JFreeChart(title, JFreeChart.DEFAULT_TITLE_FONT, plot, legend);
        return chart;

    }

    /**
     * Creates and returns a default instance of a signal chart based on the specified dataset.
     *
     * @param title The chart title.
     * @param timeAxisLabel A label for the time axis.
     * @param valueAxisLabel A label for the value axis.
     * @param data The dataset for the chart.
     * @param legend A flag specifying whether or not a legend is required.
     */
    public static JFreeChart createSignalChart(String title, String timeAxisLabel,
                                               String valueAxisLabel, SignalsDataset data,
                                               boolean legend) {

        ValueAxis timeAxis = new HorizontalDateAxis(timeAxisLabel);
        NumberAxis valueAxis = new VerticalNumberAxis(valueAxisLabel);
        XYPlot plot = new XYPlot(data, timeAxis, valueAxis);
        plot.setXYItemRenderer(new SignalRenderer());
        JFreeChart chart = new JFreeChart(title, JFreeChart.DEFAULT_TITLE_FONT, plot, legend);
        return chart;

    }

    ////////////////////////////////////////////////////////////////////////////
    // Factory methods for Combinable objects
    ////////////////////////////////////////////////////////////////////////////

//    /**
//     * Creates a combinable XY (line) plot with default settings.
//     * @author Bill Kelemen.
//     * @param horizontal The horizontal axis
//     * @param vertical The vertical axis
//     * @param data The dataset for the chart.
//     */
//    public static CombinedChart createCombinableXYChart(ValueAxis horizontal, ValueAxis vertical,
//                                                        XYDataset data) {
//        XYPlot plot = new XYPlot(data, horizontal, vertical);
//        plot.setXYItemRenderer(new StandardXYItemRenderer(StandardXYItemRenderer.LINES, null));
//        return createCombinableChart(plot);
//
//    }

//    /**
//     * Creates and returns a combinable time series chart.  A time series chart is an XYPlot with a
//     * date axis (horizontal) and a number axis (vertical), and each data item is connected with a
//     * line.
//     * <P>
//     * @param horizontal The horizontal axis.
//     * @param vertical The vertical axis.
//     * @param data The dataset for the chart.
//     * @author Bill Kelemen.
//     */
//    public static CombinedChart createCombinableTimeSeriesChart(ValueAxis horizontal,
//                                                                ValueAxis vertical,
//                                                                XYDataset data) {
//
//            XYPlot plot = new XYPlot(data, horizontal, vertical);
//            plot.setXYItemRenderer(new StandardXYItemRenderer(StandardXYItemRenderer.LINES, null));
//            return createCombinableChart(plot);

//    }

//    /**
//     * Creates and returns a default instance of a high-low-open-close combinable chart based on
//     * the specified dataset.
//     * <P>
//     * @author Bill Kelemen.
//     * @param horizontal The horizontal axis
//     * @param vertical The vertical axis
//     * @param data The dataset for the chart.
//     */
//    public static CombinedChart createCombinableHighLowChart(ValueAxis horizontal,
//                                                             ValueAxis vertical,
//                                                             XYDataset data) {
//
//            XYPlot plot = new XYPlot(data, horizontal, vertical);
//            plot.setXYItemRenderer(new HighLowRenderer());
//            return createCombinableChart(plot);
//
//    }
//

//    /**
//     * Creates and returns a default instance of a VerticalXYBar combinable chart based on the
//     * specified dataset.
//     * <P>
//     * @author Bill Kelemen.
//     * @param horizontal The horizontal axis
//     * @param vertical The vertical axis
//     * @param data The dataset for the chart.
//     */
//    public static CombinedChart createCombinableVerticalXYBarChart(ValueAxis horizontal,
//                                                                   ValueAxis vertical,
//                                                                   XYDataset data) {
//
//        XYPlot plot = new XYPlot(data, horizontal, vertical);
//        plot.setXYItemRenderer(new VerticalXYBarRenderer());
//        return createCombinableChart(plot);
//
//    }


//    /**
//     * Creates and returns a CombinedChart.
//     * <P>
//     * @author Bill Kelemen.
//     * @param data The dataset.
//     * @param plot The plot.
//     */
//    public static CombinedChart createCombinableChart(Plot plot) {
//        CombinedChart chart = new CombinedChart(plot);
//        chart.setBackgroundPaint(null);
//        return chart;
//    }

}