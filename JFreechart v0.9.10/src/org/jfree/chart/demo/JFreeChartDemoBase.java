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
 * -----------------------
 * JFreeChartDemoBase.java
 * -----------------------
 * (C) Copyright 2000-2003, by Object Refinery Limited and Contributors.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited);
 * Contributor(s):   Andrzej Porebski;
 *                   Matthew Wright;
 *                   Serge V. Grachov;
 *                   Bill Kelemen;
 *                   Achilleus Mantzios;
 *                   Bryan Scott;
 *
 * $Id: JFreeChartDemoBase.java,v 1.1 2007/10/10 19:04:55 vauchers Exp $
 *
 * Changes
 * -------
 * 27-Jul-2002 : Created (BRS);
 * 10-Oct-2002 : Fixed errors reported by Checkstyle (DG);
 *
 */

package org.jfree.chart.demo;

import java.awt.Color;
import java.awt.Font;
import java.awt.GradientPaint;
import java.lang.reflect.Method;
import java.util.ResourceBundle;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.Legend;
import org.jfree.chart.Spacer;
import org.jfree.chart.TextTitle;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.LogarithmicAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.labels.HighLowToolTipGenerator;
import org.jfree.chart.labels.TimeSeriesToolTipGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.CombinedDomainXYPlot;
import org.jfree.chart.plot.CombinedRangeXYPlot;
import org.jfree.chart.plot.CompassPlot;
import org.jfree.chart.plot.MeterPlot;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.ThermometerPlot;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.HighLowRenderer;
import org.jfree.chart.renderer.StandardXYItemRenderer;
import org.jfree.chart.renderer.XYBarRenderer;
import org.jfree.chart.renderer.XYItemRenderer;
import org.jfree.data.CategoryDataset;
import org.jfree.data.CombinedDataset;
import org.jfree.data.DatasetUtilities;
import org.jfree.data.DefaultMeterDataset;
import org.jfree.data.DefaultValueDataset;
import org.jfree.data.HighLowDataset;
import org.jfree.data.IntervalCategoryDataset;
import org.jfree.data.MovingAverage;
import org.jfree.data.PieDataset;
import org.jfree.data.SignalsDataset;
import org.jfree.data.SubSeriesDataset;
import org.jfree.data.WindDataset;
import org.jfree.data.XYDataset;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;

/**
 * A simple class that allows the swing and servlet chart demonstrations
 * to share chart generating code.
 *
 * If you would like to add a chart to the swing and/or servlet demo do so here.
 *
 * @author Bryan Scott
 * @author David Gilbert
 */
public class JFreeChartDemoBase {

    /**
     * CHART_COMMANDS holds information on charts that can be created
     * Format is
     *   Name, Creation Method, Resource file prefix
     *
     * Steps To add a chart
     * 1) Create a createChart method which returns a JFreeChart
     * 2) Append details to CHART_COMMANDS
     * 3) Append details to DemoResources
     */
    public static final String[][] CHART_COMMANDS = {
        {"HORIZONTAL_BAR_CHART", "createHorizontalBarChart", "chart1"},
        {"HORIZONTAL_STACKED_BAR_CHART", "createStackedHorizontalBarChart", "chart2"},
        {"VERTICAL_BAR_CHART", "createVerticalBarChart", "chart3"},
        {"VERTICAL_3D_BAR_CHART", "createVertical3DBarChart", "chart4"},
        {"VERTICAL_STACKED_BAR_CHART", "createVerticalStackedBarChart", "chart5"},
        {"VERTICAL_STACKED_3D_BAR_CHART", "createVerticalStacked3DBarChart", "chart6"},
        {"PIE_CHART_1", "createPieChartOne", "chart7"},
        {"PIE_CHART_2", "createPieChartTwo", "chart8"},
        {"XY_PLOT", "createXYPlot", "chart9"},
        {"TIME_SERIES_1_CHART", "createTimeSeries1Chart", "chart10"},
        {"TIME_SERIES_2_CHART", "createTimeSeries2Chart", "chart11"},
        {"TIME_SERIES_WITH_MA_CHART", "createTimeSeriesWithMAChart", "chart12"},
        {"HIGH_LOW_CHART", "createHighLowChart", "chart13"},
        {"CANDLESTICK_CHART", "createCandlestickChart", "chart14"},
        {"SIGNAL_CHART", "createSignalChart", "chart15"},
        {"WIND_PLOT", "createWindPlot", "chart16"},
        {"SCATTER_PLOT", "createScatterPlot", "chart17"},
        {"LINE_CHART", "createLineChart", "chart18"},
        {"VERTICAL_XY_BAR_CHART", "createVerticalXYBarChart", "chart19"},
        {"XY_PLOT_NULL", "createNullXYPlot", "chart20"},
        {"XY_PLOT_ZERO", "createXYPlotZeroData", "chart21"},
        {"TIME_SERIES_CHART_SCROLL", "createTimeSeriesChartInScrollPane", "chart22"},
        {"SINGLE_SERIES_BAR_CHART", "createSingleSeriesBarChart", "chart23"},
        {"DYNAMIC_CHART", "createDynamicXYChart", "chart24"},
        {"OVERLAID_CHART", "createOverlaidChart", "chart25"},
        {"HORIZONTALLY_COMBINED_CHART", "createHorizontallyCombinedChart", "chart26"},
        {"VERTICALLY_COMBINED_CHART", "createVerticallyCombinedChart", "chart27"},
        {"COMBINED_OVERLAID_CHART", "createCombinedAndOverlaidChart1", "chart28"},
        {"COMBINED_OVERLAID_DYNAMIC_CHART", "createCombinedAndOverlaidDynamicXYChart", "chart29"},
        {"THERMOMETER_CHART", "createThermometerChart", "chart30"},
        {"METER_CHART", "createMeterChartCircle", "chart31"},
        {"GANTT_CHART", "createGanttChart", "chart32"},
        {"METER_CHART2", "createMeterChartPie", "chart33"},
        {"METER_CHART3", "createMeterChartChord", "chart34"},
        {"COMPASS_CHART", "createCompassChart", "chart35"},
    };

    /** Base class name for localised resources. */
    public static final String BASE_RESOURCE_CLASS
        = "org.jfree.chart.demo.resources.DemoResources";

    /** Localised resources. */
    private ResourceBundle resources;

    /** An array of charts. */
    private JFreeChart[] charts = new JFreeChart[CHART_COMMANDS.length];

    /**
     * Default constructor.
     */
    public JFreeChartDemoBase() {
        this.resources = ResourceBundle.getBundle(BASE_RESOURCE_CLASS);
    }

    /**
     * Returns a chart.
     *
     * @param i  the chart index.
     *
     * @return a chart.
     */
    public JFreeChart getChart(int i) {

        if ((i < 0) && (i >= charts.length)) {
            i = 0;
        }

        if (charts[i] == null) {
            /// Utilise reflection to invoke method to create new chart if required.
            try {
                Method method = getClass().getDeclaredMethod(CHART_COMMANDS[i][1], null);
                charts[i] = (JFreeChart) method.invoke(this, null);
            }
            catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return charts[i];
    }

    /**
     * This makes the resources bundle available.  Basically an optimisation so
     * the demo servlet can access the same resource file.
     * @return the resources bundle.
     */
    public ResourceBundle getResources() {
      return this.resources;
    }

    /**
     * Create a horizontal bar chart.
     *
     * @return a horizontal bar chart.
     */
    public JFreeChart createHorizontalBarChart() {

        // create a default chart based on some sample data...
        String title = resources.getString("bar.horizontal.title");
        String domain = resources.getString("bar.horizontal.domain");
        String range = resources.getString("bar.horizontal.range");

        CategoryDataset data = DemoDatasetFactory.createCategoryDataset();
        JFreeChart chart = ChartFactory.createBarChart(title, domain, range, data,
                                                       PlotOrientation.HORIZONTAL,
                                                       true,
                                                       true,
                                                       false);

        // then customise it a little...
        chart.getLegend().setAnchor(Legend.EAST);
        chart.setBackgroundPaint(new GradientPaint(0, 0, Color.white, 0, 1000, Color.orange));
        CategoryPlot plot = chart.getCategoryPlot();
        plot.setRangeCrosshairVisible(false);
        NumberAxis axis = (NumberAxis) plot.getRangeAxis();
        axis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
        axis.setInverted(true);

        return chart;

    }

    /**
     * Creates and returns a sample stacked horizontal bar chart.
     *
     * @return a sample stacked horizontal bar chart.
     */
    public JFreeChart createStackedHorizontalBarChart() {

        // create a default chart based on some sample data...
        String title = resources.getString("bar.horizontal-stacked.title");
        String domain = resources.getString("bar.horizontal-stacked.domain");
        String range = resources.getString("bar.horizontal-stacked.range");

        CategoryDataset data = DemoDatasetFactory.createCategoryDataset();
        JFreeChart chart = ChartFactory.createStackedBarChart(title, domain, range,
                                                              data,
                                                              PlotOrientation.HORIZONTAL,
                                                              true,
                                                              true,
                                                              false);

        // then customise it a little...
        chart.setBackgroundPaint(new GradientPaint(0, 0, Color.white, 1000, 0, Color.blue));
        return chart;

    }

    /**
     * Creates and returns a sample vertical bar chart.
     *
     * @return a sample vertical bar chart.
     */
    public JFreeChart createVerticalBarChart() {

        String title = resources.getString("bar.vertical.title");
        String domain = resources.getString("bar.vertical.domain");
        String range = resources.getString("bar.vertical.range");

        CategoryDataset data = DemoDatasetFactory.createCategoryDataset();
        JFreeChart chart = ChartFactory.createBarChart(title, domain, range, data,
                                                       PlotOrientation.VERTICAL,
                                                       true,
                                                       true,
                                                       false);

        // then customise it a little...
        chart.setBackgroundPaint(new GradientPaint(0, 0, Color.white, 1000, 0, Color.red));
        CategoryPlot plot = (CategoryPlot) chart.getPlot();
        plot.setForegroundAlpha(0.9f);
        NumberAxis verticalAxis = (NumberAxis) plot.getRangeAxis();
        verticalAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
        return chart;
    }

    /**
     * Creates and returns a sample vertical 3D bar chart.
     *
     * @return a sample vertical 3D bar chart.
     */
    public JFreeChart createVertical3DBarChart() {

        // create a default chart based on some sample data...
        String title = resources.getString("bar.vertical3D.title");
        String domain = resources.getString("bar.vertical3D.domain");
        String range = resources.getString("bar.vertical3D.range");

        CategoryDataset data = DemoDatasetFactory.createCategoryDataset();
        JFreeChart chart = ChartFactory.createBarChart3D(
            title,
            domain,
            range, data,
            PlotOrientation.VERTICAL,
            true,
            true,
            false
        );

        // then customise it a little...
        chart.setBackgroundPaint(new GradientPaint(0, 0, Color.white, 1000, 0, Color.blue));
        CategoryPlot plot = (CategoryPlot) chart.getPlot();
        plot.setForegroundAlpha(0.75f);
        return chart;

    }

    /**
     * Creates and returns a sample stacked vertical bar chart.
     *
     * @return a sample stacked vertical bar chart.
     */
    public JFreeChart createVerticalStackedBarChart() {

        // create a default chart based on some sample data...
        String title = resources.getString("bar.vertical-stacked.title");
        String domain = resources.getString("bar.vertical-stacked.domain");
        String range = resources.getString("bar.vertical-stacked.range");

        CategoryDataset data = DemoDatasetFactory.createCategoryDataset();
        JFreeChart chart
            = ChartFactory.createStackedBarChart(title, domain, range, data,
                                                 PlotOrientation.VERTICAL,
                                                 true,
                                                 true,
                                                 false);

        // then customise it a little...
        chart.setBackgroundPaint(new GradientPaint(0, 0, Color.white, 1000, 0, Color.red));
        return chart;

    }

    /**
     * Creates and returns a sample stacked vertical 3D bar chart.
     *
     * @return a sample stacked vertical 3D bar chart.
     */
    public JFreeChart createVerticalStacked3DBarChart() {

        // create a default chart based on some sample data...
        String title = resources.getString("bar.vertical-stacked3D.title");
        String domain = resources.getString("bar.vertical-stacked3D.domain");
        String range = resources.getString("bar.vertical-stacked3D.range");
        CategoryDataset data = DemoDatasetFactory.createCategoryDataset();
        JFreeChart chart = ChartFactory.createStackedBarChart3D(title, domain, range, data,
                                                                PlotOrientation.VERTICAL,
                                                                true, true, false);

        // then customise it a little...
        chart.setBackgroundPaint(new GradientPaint(0, 0, Color.white, 1000, 0, Color.red));
        return chart;

    }

    /**
     * Creates and returns a sample pie chart.
     *
     * @return a sample pie chart.
     */
    public JFreeChart createPieChartOne() {

        // create a default chart based on some sample data...
        String title = resources.getString("pie.pie1.title");
        CategoryDataset data = DemoDatasetFactory.createCategoryDataset();
        PieDataset extracted = DatasetUtilities.createPieDatasetForRow(data, 0);
        JFreeChart chart = ChartFactory.createPieChart(title, extracted, true, true, false);

        // then customise it a little...
        chart.setBackgroundPaint(new GradientPaint(0, 0, Color.white, 0, 1000, Color.orange));
        PiePlot plot = (PiePlot) chart.getPlot();
        plot.setCircular(false);
        // make section 1 explode by 100%...
        plot.setRadius(0.60);
        plot.setExplodePercent(1, 1.00);
        return chart;

    }

    /**
     * Creates and returns a sample pie chart.
     *
     * @return a sample pie chart.
     */
    public JFreeChart createPieChartTwo() {

        // create a default chart based on some sample data...
        String title = resources.getString("pie.pie2.title");
        CategoryDataset data = DemoDatasetFactory.createCategoryDataset();
        Comparable category = (Comparable) data.getColumnKeys().get(1);
        PieDataset extracted = DatasetUtilities.createPieDatasetForColumn(data, category);
        JFreeChart chart = ChartFactory.createPieChart(title, extracted, true, true, false);

        // then customise it a little...
        chart.setBackgroundPaint(Color.lightGray);
        PiePlot pie = (PiePlot) chart.getPlot();
        pie.setSectionLabelType(PiePlot.NAME_AND_PERCENT_LABELS);
        pie.setBackgroundImage(JFreeChart.INFO.getLogo());
        pie.setBackgroundPaint(Color.white);
        pie.setBackgroundAlpha(0.6f);
        pie.setForegroundAlpha(0.75f);
        return chart;

    }

    /**
     * Creates and returns a sample XY plot.
     *
     * @return a sample XY plot.
     */
    public JFreeChart createXYPlot() {

        // create a default chart based on some sample data...
        String title = resources.getString("xyplot.sample1.title");
        String domain = resources.getString("xyplot.sample1.domain");
        String range = resources.getString("xyplot.sample1.range");
        XYDataset data = DemoDatasetFactory.createSampleXYDataset();
        JFreeChart chart = ChartFactory.createLineXYChart(title, 
                                                          domain, range, data, 
                                                          PlotOrientation.VERTICAL,
                                                          true,
                                                          true,
                                                          false);

        // then customise it a little...
        chart.setBackgroundPaint(new GradientPaint(0, 0, Color.white, 0, 1000, Color.green));
        return chart;

    }

    /**
     * Creates and returns a sample time series chart.
     *
     * @return a sample time series chart.
     */
    public JFreeChart createTimeSeries1Chart() {

        // create a default chart based on some sample data...
        String title = resources.getString("timeseries.sample1.title");
        String subtitle = resources.getString("timeseries.sample1.subtitle");
        String domain = resources.getString("timeseries.sample1.domain");
        String range = resources.getString("timeseries.sample1.range");
        String copyrightStr = resources.getString("timeseries.sample1.copyright");
        XYDataset data = DemoDatasetFactory.createTimeSeriesCollection3();
        JFreeChart chart = ChartFactory.createTimeSeriesChart(title, domain, range, data,
                                                              true,
                                                              true,
                                                              false);

        // then customise it a little...
        TextTitle title2 = new TextTitle(subtitle, new Font("SansSerif", Font.PLAIN, 12));
        title2.setSpacer(new Spacer(Spacer.RELATIVE, 0.05, 0.05, 0.05, 0.0));
        chart.addSubtitle(title2);

        TextTitle copyright = new TextTitle(copyrightStr, new Font("SansSerif", Font.PLAIN, 9));
        copyright.setPosition(TextTitle.BOTTOM);
        copyright.setHorizontalAlignment(TextTitle.RIGHT);
        chart.addSubtitle(copyright);

        chart.setBackgroundPaint(new GradientPaint(0, 0, Color.white, 0, 1000, Color.blue));
        XYPlot plot = chart.getXYPlot();
        DateAxis axis = (DateAxis) plot.getDomainAxis();
        axis.setVerticalTickLabels(true);
        return chart;

    }

    /**
     * Creates and returns a sample time series chart.
     *
     * @return a sample time series chart.
     */
    public JFreeChart createTimeSeries2Chart() {

        // create a default chart based on some sample data...
        String title = resources.getString("timeseries.sample2.title");
        String subtitleStr = resources.getString("timeseries.sample2.subtitle");
        String domain = resources.getString("timeseries.sample2.domain");
        String range = resources.getString("timeseries.sample2.range");
        XYDataset data = DemoDatasetFactory.createTimeSeriesCollection4();
        JFreeChart chart = ChartFactory.createTimeSeriesChart(title, domain, range, data,
                                                              true, true, false);

        // then customise it a little...
        TextTitle subtitle = new TextTitle(subtitleStr, new Font("SansSerif", Font.BOLD, 12));
        chart.addSubtitle(subtitle);
        chart.setBackgroundPaint(new GradientPaint(0, 0, Color.white, 0, 1000, Color.blue));
        XYPlot plot = chart.getXYPlot();
        LogarithmicAxis rangeAxis = new LogarithmicAxis(range);
        plot.setRangeAxis(rangeAxis);
        return chart;

    }

    /**
     * Creates and returns a sample time series chart.
     *
     * @return a sample time series chart.
     */
    public JFreeChart createTimeSeriesWithMAChart() {

        // create a default chart based on some sample data...
        String title = resources.getString("timeseries.sample3.title");
        String domain = resources.getString("timeseries.sample3.domain");
        String range = resources.getString("timeseries.sample3.range");
        String subtitleStr = resources.getString("timeseries.sample3.subtitle");
        TimeSeries jpy = DemoDatasetFactory.createJPYTimeSeries();
        TimeSeries mav = MovingAverage.createMovingAverage(jpy, "30 Day Moving Average", 30, 30);
        TimeSeriesCollection dataset = new TimeSeriesCollection();
        dataset.addSeries(jpy);
        dataset.addSeries(mav);
        JFreeChart chart = ChartFactory.createTimeSeriesChart(title, domain, range, dataset,
                                                              true,
                                                              true,
                                                              false);

        // then customise it a little...
        TextTitle subtitle = new TextTitle(subtitleStr, new Font("SansSerif", Font.BOLD, 12));
        chart.addSubtitle(subtitle);
        chart.setBackgroundPaint(new GradientPaint(0, 0, Color.white, 0, 1000, Color.blue));
        return chart;

    }

    /**
     * Displays a vertical bar chart in its own frame.
     *
     * @return a high low chart.
     */
    public JFreeChart createHighLowChart() {

        // create a default chart based on some sample data...
        String title = resources.getString("timeseries.highlow.title");
        String domain = resources.getString("timeseries.highlow.domain");
        String range = resources.getString("timeseries.highlow.range");
        String subtitleStr = resources.getString("timeseries.highlow.subtitle");
        HighLowDataset data = DemoDatasetFactory.createHighLowDataset();
        JFreeChart chart = ChartFactory.createHighLowChart(title, domain, range, data, true);

        // then customise it a little...
        TextTitle subtitle = new TextTitle(subtitleStr, new Font("SansSerif", Font.BOLD, 12));
        chart.addSubtitle(subtitle);
        chart.setBackgroundPaint(new GradientPaint(0, 0, Color.white, 0, 1000, Color.magenta));
        return chart;

    }

    /**
     * Creates a candlestick chart.
     *
     * @return a candlestick chart.
     */
    public JFreeChart createCandlestickChart() {

      // create a default chart based on some sample data...
      String title = resources.getString("timeseries.candlestick.title");
      String domain = resources.getString("timeseries.candlestick.domain");
      String range = resources.getString("timeseries.candlestick.range");
      String subtitleStr = resources.getString("timeseries.candlestick.subtitle");
      HighLowDataset data = DemoDatasetFactory.createHighLowDataset();
      JFreeChart chart = ChartFactory.createCandlestickChart(title, domain, range, data, false);

      // then customise it a little...
      TextTitle subtitle = new TextTitle(subtitleStr, new Font("SansSerif", Font.BOLD, 12));
      chart.addSubtitle(subtitle);
      chart.setBackgroundPaint(new GradientPaint(0, 0, Color.white, 0, 1000, Color.green));
      return chart;

    }

    /**
     * Creates and returns a sample signal chart.
     *
     * @return a sample chart.
     */
    public JFreeChart createSignalChart() {

        // create a default chart based on some sample data...
        String title = resources.getString("timeseries.signal.title");
        String domain = resources.getString("timeseries.signal.domain");
        String range = resources.getString("timeseries.signal.range");
        String subtitleStr = resources.getString("timeseries.signal.subtitle");
        SignalsDataset data = DemoDatasetFactory.createSampleSignalDataset();
        JFreeChart chart = ChartFactory.createSignalChart(title, domain, range, data, true);

        // then customise it a little...
        TextTitle subtitle = new TextTitle(subtitleStr, new Font("SansSerif", Font.BOLD, 12));
        chart.addSubtitle(subtitle);
        chart.setBackgroundPaint(new GradientPaint(0, 0, Color.white, 0, 1000, Color.blue));
        return chart;

    }

    /**
     * Creates and returns a sample thermometer chart.
     *
     * @return a sample thermometer chart.
     */
    public JFreeChart createThermometerChart() {

        // create a default chart based on some sample data...
        String title = resources.getString("meter.thermo.title");
        String subtitleStr = resources.getString("meter.thermo.subtitle");
        String units = resources.getString("meter.thermo.units");

        DefaultValueDataset data = new DefaultValueDataset(new Double(34.0));
        ThermometerPlot plot = new ThermometerPlot(data);
        plot.setUnits(units);
        JFreeChart chart = new JFreeChart(title, JFreeChart.DEFAULT_TITLE_FONT, plot, false);

        // then customise it a little...
        TextTitle subtitle = new TextTitle(subtitleStr, new Font("SansSerif", Font.BOLD, 12));
        chart.addSubtitle(subtitle);
        chart.setBackgroundPaint(new GradientPaint(0, 0, Color.white, 0, 1000, Color.blue));
        return chart;

    }

    /**
     * Creates and returns a sample meter chart.
     *
     * @return a meter chart.
     */
    public JFreeChart createMeterChartCircle() {

        // create a default chart based on some sample data...
        String title = resources.getString("meter.meter.title");
        String subtitleStr = resources.getString("meter.meter.subtitle");
        String units = resources.getString("meter.meter.units");
        DefaultMeterDataset data = DemoDatasetFactory.createMeterDataset();

        data.setUnits(units);
        MeterPlot plot = new MeterPlot(data);
        plot.setMeterAngle(270);
        plot.setDialType(1);
        JFreeChart chart = new JFreeChart(title, JFreeChart.DEFAULT_TITLE_FONT,
                                          plot, false);

        // then customise it a little...
        TextTitle subtitle = new TextTitle(subtitleStr, new Font("SansSerif", Font.BOLD, 12));
        chart.addSubtitle(subtitle);
        chart.setBackgroundPaint(new GradientPaint(0, 0, Color.white, 0, 1000, Color.blue));
        return chart;
    }

    /**
     * Creates and returns a sample meter chart.
     *
     * @return a meter chart.
     */
    public JFreeChart createMeterChartPie() {

        // create a default chart based on some sample data...
        String title = resources.getString("meter.meter.title");
        String subtitleStr = resources.getString("meter.meter.subtitle");
        String units = resources.getString("meter.meter.units");
        DefaultMeterDataset data = DemoDatasetFactory.createMeterDataset();

        data.setUnits(units);
        MeterPlot plot = new MeterPlot(data);
        plot.setMeterAngle(270);
        plot.setDialType(0);
        JFreeChart chart = new JFreeChart(title, JFreeChart.DEFAULT_TITLE_FONT,
                                          plot, false);

        // then customise it a little...
        TextTitle subtitle = new TextTitle(subtitleStr, new Font("SansSerif", Font.BOLD, 12));
        chart.addSubtitle(subtitle);
        chart.setBackgroundPaint(new GradientPaint(0, 0, Color.white, 0, 1000, Color.blue));
        return chart;
    }

    /**
     * Creates and returns a sample meter chart.
     *
     * @return the meter chart.
     */
    public JFreeChart createMeterChartChord() {

        // create a default chart based on some sample data...
        String title = resources.getString("meter.meter.title");
        String subtitleStr = resources.getString("meter.meter.subtitle");
        String units = resources.getString("meter.meter.units");
        DefaultMeterDataset data = DemoDatasetFactory.createMeterDataset();

        data.setUnits(units);
        MeterPlot plot = new MeterPlot(data);
        plot.setMeterAngle(270);
        plot.setDialType(2);
        JFreeChart chart = new JFreeChart(title, JFreeChart.DEFAULT_TITLE_FONT,
                                          plot, false);

        // then customise it a little...
        TextTitle subtitle = new TextTitle(subtitleStr, new Font("SansSerif", Font.BOLD, 12));
        chart.addSubtitle(subtitle);
        chart.setBackgroundPaint(new GradientPaint(0, 0, Color.white, 0, 1000, Color.blue));
        return chart;
    }

    /**
     * Creates a compass chart.
     *
     * @return a compass chart.
     */
    public JFreeChart createCompassChart() {

        // create a default chart based on some sample data...
        String title = resources.getString("meter.compass.title");
        String subtitleStr = resources.getString("meter.compass.subtitle");
        DefaultValueDataset data = new DefaultValueDataset(new Double(45.0));

        Plot plot = new CompassPlot(data);
        JFreeChart chart = new JFreeChart(title, JFreeChart.DEFAULT_TITLE_FONT,
                                          plot, false);

        // then customise it a little...
        TextTitle subtitle = new TextTitle(subtitleStr, new Font("SansSerif", Font.BOLD, 12));
        chart.addSubtitle(subtitle);
        chart.setBackgroundPaint(new GradientPaint(0, 0, Color.white, 0, 1000, Color.blue));
        return chart;
    }

    /**
     * Creates and returns a sample wind plot.
     *
     * @return a sample wind plot.
     */
    public JFreeChart createWindPlot() {

        // create a default chart based on some sample data...
        String title = resources.getString("other.wind.title");
        String domain = resources.getString("other.wind.domain");
        String range = resources.getString("other.wind.range");
        WindDataset data = DemoDatasetFactory.createWindDataset1();
        JFreeChart chart = ChartFactory.createWindPlot(title, domain, range, data,
                                                       true,
                                                       false,
                                                       false);

        // then customise it a little...
        chart.setBackgroundPaint(new GradientPaint(0, 0, Color.white, 1000, 0, Color.green));
        return chart;

    }

    /**
     * Creates and returns a sample scatter plot.
     *
     * @return a sample scatter plot.
     */
    public JFreeChart createScatterPlot() {

        // create a default chart based on some sample data...
        String title = resources.getString("other.scatter.title");
        String domain = resources.getString("other.scatter.domain");
        String range = resources.getString("other.scatter.range");
        XYDataset data = new SampleXYDataset2();
        JFreeChart chart = ChartFactory.createScatterPlot(
            title, 
            domain, 
            range, 
            data,
            PlotOrientation.VERTICAL, 
            true,
            true,  // tooltips
            false  // urls
        );

        // then customise it a little...
        chart.setBackgroundPaint(new GradientPaint(0, 0, Color.white, 1000, 0, Color.green));

        XYPlot plot = chart.getXYPlot();
        plot.setDomainCrosshairVisible(true);
        plot.setRangeCrosshairVisible(true);
        NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
        rangeAxis.setAutoRangeIncludesZero(false);
        return chart;

    }

    /**
     * Creates and returns a sample line chart.
     *
     * @return a line chart.
     */
    public JFreeChart createLineChart() {

        // create a default chart based on some sample data...
        String title = resources.getString("other.line.title");
        String domain = resources.getString("other.line.domain");
        String range = resources.getString("other.line.range");
        CategoryDataset data = DemoDatasetFactory.createCategoryDataset();
        JFreeChart chart = ChartFactory.createLineChart(title, domain, range, data,
                                                        PlotOrientation.VERTICAL,
                                                        true,
                                                        true,
                                                        false);

        // then customise it a little...
        chart.setBackgroundImage(JFreeChart.INFO.getLogo());
        chart.setBackgroundPaint(new GradientPaint(0, 0, Color.white, 0, 1000, Color.green));

        CategoryPlot plot = (CategoryPlot) chart.getPlot();
        plot.setBackgroundAlpha(0.65f);
        CategoryAxis axis = plot.getDomainAxis();
        axis.setVerticalCategoryLabels(true);
        return chart;
    }

    /**
     * Creates and returns a sample vertical XY bar chart.
     *
     * @return a sample vertical XY bar chart.
     */
    public JFreeChart createVerticalXYBarChart() {

        // create a default chart based on some sample data...
        String title = resources.getString("other.xybar.title");
        String domain = resources.getString("other.xybar.domain");
        String range = resources.getString("other.xybar.range");
        TimeSeriesCollection data = DemoDatasetFactory.createTimeSeriesCollection1();
        data.setDomainIsPointsInTime(false);
        JFreeChart chart = ChartFactory.createXYBarChart(
            title, 
            domain, 
            range, 
            data,
            PlotOrientation.VERTICAL,
            true,
            false,
            false
        );

        // then customise it a little...
        chart.setBackgroundPaint(new GradientPaint(0, 0, Color.white, 1000, 0, Color.blue));

        XYItemRenderer renderer = chart.getXYPlot().getRenderer();
        renderer.setToolTipGenerator(new TimeSeriesToolTipGenerator());
        return chart;
    }

    /**
     * Creates and returns a sample XY chart with null data.
     *
     * @return a chart.
     */
    public JFreeChart createNullXYPlot() {

        // create a default chart based on some sample data...
        String title = resources.getString("test.null.title");
        String domain = resources.getString("test.null.domain");
        String range = resources.getString("test.null.range");
        XYDataset data = null;
        JFreeChart chart = ChartFactory.createLineXYChart(title, domain, range, data,
                                                          PlotOrientation.VERTICAL,
                                                          true,
                                                          true,
                                                          false);

        // then customise it a little...
        chart.setBackgroundPaint(new GradientPaint(0, 0, Color.white, 1000, 0, Color.red));
        return chart;

    }

    /**
     * Creates a sample XY plot with an empty dataset.
     *
     * @return a sample XY plot with an empty dataset.
     */
    public JFreeChart createXYPlotZeroData() {

        // create a default chart based on some sample data...
        String title = resources.getString("test.zero.title");
        String domain = resources.getString("test.zero.domain");
        String range = resources.getString("test.zero.range");
        XYDataset data = new EmptyXYDataset();
        JFreeChart chart = ChartFactory.createLineXYChart(title, domain, range, data,
                                                          PlotOrientation.VERTICAL,
                                                          true,
                                                          true,
                                                          false);

        // then customise it a little...
        chart.setBackgroundPaint(new GradientPaint(0, 0, Color.white, 1000, 0, Color.red));
        return chart;
    }

    /**
     * Creates and returns a sample time series chart that will be displayed in a scroll pane.
     *
     * @return a sample time series chart.
     */
    public JFreeChart createTimeSeriesChartInScrollPane() {

        // create a default chart based on some sample data...
        String title = resources.getString("test.scroll.title");
        String domain = resources.getString("test.scroll.domain");
        String range = resources.getString("test.scroll.range");
        String subtitleStr = resources.getString("test.scroll.subtitle");
        XYDataset data = DemoDatasetFactory.createTimeSeriesCollection2();
        JFreeChart chart = ChartFactory.createTimeSeriesChart(title, domain, range, data,
                                                              true,
                                                              true,
                                                              false);

        // then customise it a little...
        TextTitle subtitle = new TextTitle(subtitleStr, new Font("SansSerif", Font.BOLD, 12));
        chart.addSubtitle(subtitle);
        chart.setBackgroundPaint(new GradientPaint(0, 0, Color.white, 0, 1000, Color.gray));
        return chart;

    }

    /**
     * Creates and returns a sample bar chart with just one series.
     *
     * @return a sample bar chart.
     */
    public JFreeChart createSingleSeriesBarChart() {

        // create a default chart based on some sample data...
        String title = resources.getString("test.single.title");
        String domain = resources.getString("test.single.domain");
        String range = resources.getString("test.single.range");
        String subtitle1Str = resources.getString("test.single.subtitle1");
        String subtitle2Str = resources.getString("test.single.subtitle2");

        CategoryDataset data = DemoDatasetFactory.createSingleSeriesCategoryDataset();

        JFreeChart chart = ChartFactory.createBarChart(title, domain, range, data,
                                                       PlotOrientation.HORIZONTAL,
                                                       true,
                                                       true,
                                                       false);
        chart.addSubtitle(new TextTitle(subtitle1Str));
        chart.addSubtitle(new TextTitle(subtitle2Str));
        chart.setBackgroundPaint(new GradientPaint(0, 0, Color.white, 1000, 0, Color.red));
        return chart;

    }

    /**
     * Displays an XY chart that is periodically updated by a background thread.  This is to
     * demonstrate the event notification system that automatically updates charts as required.
     *
     * @return a chart.
     */
    public JFreeChart createDynamicXYChart() {

        String title = resources.getString("test.dynamic.title");
        String domain = resources.getString("test.dynamic.domain");
        String range = resources.getString("test.dynamic.range");

        SampleXYDataset data = new SampleXYDataset();
        JFreeChart chart = ChartFactory.createLineXYChart(title, domain, range, data,
                                                          PlotOrientation.VERTICAL,
                                                          true,
                                                          true,
                                                          false);
        SampleXYDatasetThread update = new SampleXYDatasetThread(data);

        Thread thread = new Thread(update);
        thread.start();

        return chart;

    }

    /**
     * Creates and returns a sample overlaid chart.
     * <P>
     * Note:  with the introduction of multiple secondary datasets in JFreeChart version 0.9.10,
     * the overlaid chart facility has been removed.  You can achieve the same results using
     * a regular XYPlot with multiple datasets.
     *
     * @return an overlaid chart.
     */
    public JFreeChart createOverlaidChart() {

        // create a default chart based on some sample data...
        String title = this.resources.getString("combined.overlaid.title");
        String subtitleStr = this.resources.getString("combined.overlaid.subtitle");
        String domainAxisLabel = this.resources.getString("combined.overlaid.domain");
        String rangeAxisLabel = this.resources.getString("combined.overlaid.range");

        // create high-low and moving average dataset
        HighLowDataset highLowData = DemoDatasetFactory.createHighLowDataset();

        // make an overlaid plot
        ValueAxis domainAxis = new DateAxis(domainAxisLabel);
        NumberAxis rangeAxis = new NumberAxis(rangeAxisLabel);
        rangeAxis.setAutoRangeIncludesZero(false);
        XYItemRenderer renderer1 = new HighLowRenderer(new HighLowToolTipGenerator());

        XYPlot plot = new XYPlot(highLowData, domainAxis, rangeAxis, renderer1);

        // overlay a moving average dataset
        XYDataset maData = MovingAverage.createMovingAverage(
            highLowData, 
            " (Moving Average)",
            5 * 24 * 60 * 60 * 1000L,
            5 * 24 * 60 * 60 * 1000L
        );
        plot.setSecondaryDataset(0, maData);
        XYItemRenderer renderer2 = new StandardXYItemRenderer();
        renderer2.setToolTipGenerator(new TimeSeriesToolTipGenerator("d-MMM-yyyy", "0.00"));
        plot.setSecondaryRenderer(0, renderer2);

        // make the top level JFreeChart object
        JFreeChart chart = new JFreeChart(title, JFreeChart.DEFAULT_TITLE_FONT, plot, true);

        // then customise it a little...
        TextTitle subtitle = new TextTitle(subtitleStr, new Font("SansSerif", Font.BOLD, 12));
        chart.addSubtitle(subtitle);
        chart.setBackgroundPaint(new GradientPaint(0, 0, Color.white, 0, 1000, Color.blue));

        return chart;

    }

    /**
     * Creates a horizontally combined chart.
     *
     * @return a horizontally combined chart.
     */
    public JFreeChart createHorizontallyCombinedChart() {

        // create a default chart based on some sample data...
        String title = this.resources.getString("combined.horizontal.title");
        String subtitleStr = this.resources.getString("combined.horizontal.subtitle");
        String[] domains = this.resources.getStringArray("combined.horizontal.domains");
        String rangeAxisLabel = this.resources.getString("combined.horizontal.range");

        TimeSeriesCollection dataset0 = new TimeSeriesCollection();
        TimeSeries eur = DemoDatasetFactory.createEURTimeSeries();
        dataset0.addSeries(eur);

        TimeSeriesCollection dataset1 = new TimeSeriesCollection();
        TimeSeries mav = MovingAverage.createMovingAverage(eur, "EUR/GBP (30 Day MA)", 30, 30);
        dataset1.addSeries(eur);
        dataset1.addSeries(mav);

        TimeSeriesCollection dataset2 = new TimeSeriesCollection();
        dataset2.addSeries(eur);

        // make a combined range plot
        NumberAxis valueAxis = new NumberAxis(rangeAxisLabel);
        valueAxis.setAutoRangeIncludesZero(false);  // override default
        CombinedRangeXYPlot parent = new CombinedRangeXYPlot(valueAxis);
        parent.setRenderer(new StandardXYItemRenderer());

        // add subplots
        int[] weight = {1, 1, 1}; // controls space assigned to each subplot

        // add subplot 1...
        XYPlot subplot1 = new XYPlot(dataset0, new DateAxis(domains[0]), null, 
                                     new StandardXYItemRenderer());
        parent.add(subplot1, weight[0]);

        // add subplot 2...
        XYPlot subplot2 = new XYPlot(dataset1, new DateAxis(domains[1]), null, 
                                     new StandardXYItemRenderer());
        parent.add(subplot2, weight[1]);

        // add subplot 3...
        XYPlot subplot3 = new XYPlot(dataset2, new DateAxis(domains[2]),
                                     null, new XYBarRenderer(0.20));
        parent.add(subplot3, weight[2]);

        // now make the top level JFreeChart
        JFreeChart chart = new JFreeChart(title, JFreeChart.DEFAULT_TITLE_FONT, parent, true);

        // then customise it a little...
        TextTitle subtitle = new TextTitle(subtitleStr, new Font("SansSerif", Font.BOLD, 12));
        chart.addSubtitle(subtitle);
        chart.setBackgroundPaint(new GradientPaint(0, 0, Color.white, 0, 1000, Color.blue));
        return chart;

    }

    /**
     * Creates and returns a sample vertically combined chart.
     *
     * @return a sample vertically combined chart.
     */
    public JFreeChart createVerticallyCombinedChart() {

        // create a default chart based on some sample data...
        String title = this.resources.getString("combined.vertical.title");
        String subtitleStr = this.resources.getString("combined.vertical.subtitle");
        String domain = this.resources.getString("combined.vertical.domain");
        String[] ranges = this.resources.getStringArray("combined.vertical.ranges");


        TimeSeriesCollection dataset0 = new TimeSeriesCollection();
        TimeSeries eur = DemoDatasetFactory.createEURTimeSeries();
        dataset0.addSeries(eur);

        TimeSeriesCollection dataset1 = new TimeSeriesCollection();
        TimeSeries jpy = DemoDatasetFactory.createJPYTimeSeries();
        TimeSeries mav = MovingAverage.createMovingAverage(jpy, "JPY/GBP (30 Day MA)", 30, 30);
        dataset1.addSeries(jpy);
        dataset1.addSeries(mav);

        XYDataset dataset2 = DemoDatasetFactory.createHighLowDataset();

        TimeSeriesCollection dataset3 = new TimeSeriesCollection();
        dataset3.addSeries(eur);

        // make one shared horizontal axis
        ValueAxis timeAxis = new DateAxis(domain);

        // make a vertically CombinedPlot that will contain the sub-plots
        CombinedDomainXYPlot multiPlot = new CombinedDomainXYPlot(timeAxis);

        int[] weight = {1, 1, 1, 1}; // control vertical space allocated to each sub-plot

        // add subplot1...
        XYPlot subplot1 = new XYPlot(dataset0, null, new NumberAxis(ranges[0]), 
                                     new StandardXYItemRenderer());
        NumberAxis range1 = (NumberAxis) subplot1.getRangeAxis();
        range1.setTickLabelFont(new Font("Monospaced", Font.PLAIN, 7));
        range1.setLabelFont(new Font("SansSerif", Font.PLAIN, 8));
        range1.setAutoRangeIncludesZero(false);
        multiPlot.add(subplot1, weight[0]);

        // add subplot2...
        XYPlot subplot2 = new XYPlot(dataset1, null, new NumberAxis(ranges[1]), 
                                     new StandardXYItemRenderer());
        NumberAxis range2 = (NumberAxis) subplot2.getRangeAxis();
        range2.setTickLabelFont(new Font("Monospaced", Font.PLAIN, 7));
        range2.setLabelFont(new Font("SansSerif", Font.PLAIN, 8));
        range2.setAutoRangeIncludesZero(false);
        multiPlot.add(subplot2, weight[1]);

        // add subplot3...
        XYPlot subplot3 = new XYPlot(dataset2, null, new NumberAxis(ranges[2]), null);
        XYItemRenderer renderer3 = new HighLowRenderer();
        subplot3.setRenderer(renderer3);
        NumberAxis range3 = (NumberAxis) subplot3.getRangeAxis();
        range3.setTickLabelFont(new Font("Monospaced", Font.PLAIN, 7));
        range3.setLabelFont(new Font("SansSerif", Font.PLAIN, 8));
        range3.setAutoRangeIncludesZero(false);
        multiPlot.add(subplot3, weight[2]);

        // add subplot4...
        XYPlot subplot4 = new XYPlot(dataset3, null, new NumberAxis(ranges[3]), null);
        XYItemRenderer renderer4 = new XYBarRenderer();
        subplot4.setRenderer(renderer4);
        NumberAxis range4 = (NumberAxis) subplot4.getRangeAxis();
        range4.setTickLabelFont(new Font("Monospaced", Font.PLAIN, 7));
        range4.setLabelFont(new Font("SansSerif", Font.PLAIN, 8));
        range4.setAutoRangeIncludesZero(false);
        multiPlot.add(subplot4, weight[3]);

        // now make the top level JFreeChart that contains the CombinedPlot
        JFreeChart chart = new JFreeChart(title, JFreeChart.DEFAULT_TITLE_FONT, multiPlot, true);

        // then customise it a little...
        TextTitle subtitle = new TextTitle(subtitleStr, new Font("SansSerif", Font.BOLD, 12));
        chart.addSubtitle(subtitle);
        chart.setBackgroundPaint(new GradientPaint(0, 0, Color.white, 0, 1000, Color.blue));
        return chart;

    }

    /**
     * Creates a combined and overlaid chart.
     * <p>
     * Note: from version 0.9.10, the overlaid chart is no longer supported (you can achieve
     * the same result using a regular XYPlot with multiple datasets and renderers).
     *
     * @return a combined and overlaid chart.
     */
    public JFreeChart createCombinedAndOverlaidChart1() {

        // create a default chart based on some sample data...
        String title = this.resources.getString("combined.combined-overlaid.title");
        String subtitleStr = this.resources.getString("combined.combined-overlaid.subtitle");
        String domain = this.resources.getString("combined.combined-overlaid.domain");
        String[] ranges = this.resources.getStringArray("combined.combined-overlaid.ranges");

        TimeSeries jpy = DemoDatasetFactory.createJPYTimeSeries();
        TimeSeries mav = MovingAverage.createMovingAverage(jpy, "30 Day Moving Average", 30, 30);

        TimeSeriesCollection dataset0 = new TimeSeriesCollection();
        dataset0.addSeries(jpy);

        TimeSeriesCollection dataset1 = new TimeSeriesCollection();
        dataset1.addSeries(jpy);
        dataset1.addSeries(mav);

        HighLowDataset highLowDataset = DemoDatasetFactory.createHighLowDataset();
        XYDataset highLowDatasetMA = MovingAverage.createMovingAverage(
            highLowDataset, 
            " (MA)",
            5 * 24 * 60 * 60 * 1000L, 
            5 * 24 * 60 * 60 * 1000L
        );

        // make one vertical axis for each (vertical) chart
        NumberAxis[] valueAxis = new NumberAxis[3];
        for (int i = 0; i < valueAxis.length; i++) {
            valueAxis[i] = new NumberAxis(ranges[i]);
            if (i <= 1) {
                valueAxis[i].setAutoRangeIncludesZero(false);  // override default
            }
        }

        // create CombinedPlot...
        CombinedDomainXYPlot parent = new CombinedDomainXYPlot(new DateAxis(domain));

        int[] weight = {1, 2, 2};

        // add subplot1...
        XYItemRenderer renderer1 = new StandardXYItemRenderer();
        XYPlot subplot1 = new XYPlot(dataset0, null, new NumberAxis(ranges[0]), renderer1);
        NumberAxis axis1 = (NumberAxis) subplot1.getRangeAxis();
        axis1.setTickLabelFont(new Font("Monospaced", Font.PLAIN, 7));
        axis1.setLabelFont(new Font("SansSerif", Font.PLAIN, 8));
        axis1.setAutoRangeIncludesZero(false);
        parent.add(subplot1, weight[0]);

        // add subplot2 (an overlaid plot)...
        XYPlot subplot2 = new XYPlot(dataset0, null, new NumberAxis(ranges[1]), 
                                     new StandardXYItemRenderer());
        NumberAxis axis2 = (NumberAxis) subplot2.getRangeAxis();
        axis2.setTickLabelFont(new Font("Monospaced", Font.PLAIN, 7));
        axis2.setLabelFont(new Font("SansSerif", Font.PLAIN, 8));
        axis2.setAutoRangeIncludesZero(false);
        subplot2.setSecondaryDataset(0, dataset1);
        subplot2.setSecondaryRenderer(0, new StandardXYItemRenderer());

        parent.add(subplot2, weight[1]);

        // add subplot3 (an overlaid plot)...
        XYItemRenderer renderer3 = new HighLowRenderer();
        XYPlot subplot3 = new XYPlot(highLowDataset, null, new NumberAxis(ranges[2]), renderer3);
        NumberAxis axis3 = (NumberAxis) subplot3.getRangeAxis();
        axis3.setTickLabelFont(new Font("Monospaced", Font.PLAIN, 7));
        axis3.setLabelFont(new Font("SansSerif", Font.PLAIN, 8));
        axis3.setAutoRangeIncludesZero(false);
        subplot3.setSecondaryDataset(0, highLowDatasetMA);
        subplot3.setSecondaryRenderer(0, new StandardXYItemRenderer());

        parent.add(subplot3, weight[2]);

        // now create the master JFreeChart object
        JFreeChart chart = new JFreeChart(
            title,
            new Font("SansSerif", Font.BOLD, 12),
            parent, 
            true
        );

        // then customise it a little...
        TextTitle subtitle = new TextTitle(subtitleStr, new Font("SansSerif", Font.BOLD, 10));
        chart.addSubtitle(subtitle);
        chart.setBackgroundPaint(new GradientPaint(0, 0, Color.white, 0, 1000, Color.blue));
        return chart;

    }

    /**
     * Displays an XY chart that is periodically updated by a background thread.  This is to
     * demonstrate the event notification system that automatically updates charts as required.
     *
     * @return a chart.
     */
    public JFreeChart createCombinedAndOverlaidDynamicXYChart() {

        // chart title and axis labels...
        String title = this.resources.getString("combined.dynamic.title");
        String subtitleStr = this.resources.getString("combined.dynamic.subtitle");
        String domainAxisLabel = this.resources.getString("combined.dynamic.domain");
        String[] ranges = this.resources.getStringArray("combined.dynamic.ranges");

        // setup sample base 2-series dataset
        SampleXYDataset data = new SampleXYDataset();

        // create some SubSeriesDatasets and CombinedDatasets to test events
        XYDataset series0 = new SubSeriesDataset(data, 0);
        XYDataset series1 = new SubSeriesDataset(data, 1);

        CombinedDataset combinedData = new CombinedDataset();
        combinedData.add(series0);
        combinedData.add(series1);

        // create common time axis
        NumberAxis timeAxis = new NumberAxis(domainAxisLabel);
        timeAxis.setTickMarksVisible(true);
        timeAxis.setAutoRangeIncludesZero(false);

        // make one vertical axis for each (vertical) chart
        NumberAxis[] valueAxis = new NumberAxis[4];
        for (int i = 0; i < valueAxis.length; i++) {
            valueAxis[i] = new NumberAxis(ranges[i]);
            valueAxis[i].setAutoRangeIncludesZero(false);
        }

        CombinedDomainXYPlot plot = new CombinedDomainXYPlot(timeAxis);

        // add subplot1...
        XYItemRenderer renderer0 = new StandardXYItemRenderer();
        XYPlot subplot0 = new XYPlot(series0, null, valueAxis[0], renderer0);
        plot.add(subplot0, 1);

        // add subplot2...
        XYItemRenderer renderer1 = new StandardXYItemRenderer();
        XYPlot subplot1 = new XYPlot(series1, null, valueAxis[1], renderer1);
        plot.add(subplot1, 1);

        // add subplot3...
        XYPlot subplot2 = new XYPlot(series0, null, valueAxis[2], new StandardXYItemRenderer());
        subplot2.setSecondaryDataset(0, series1);
        subplot2.setSecondaryRenderer(0, new StandardXYItemRenderer());
        plot.add(subplot2, 1);

        // add subplot4...
        XYItemRenderer renderer3 = new StandardXYItemRenderer();
        XYPlot subplot3 = new XYPlot(data, null, valueAxis[3], renderer3);
        plot.add(subplot3, 1);

        JFreeChart chart = new JFreeChart(title, JFreeChart.DEFAULT_TITLE_FONT, plot, true);

        // then customise it a little...
        TextTitle subtitle = new TextTitle(subtitleStr, new Font("SansSerif", Font.BOLD, 12));
        chart.addSubtitle(subtitle);
        chart.setBackgroundPaint(new GradientPaint(0, 0, Color.white, 0, 1000, Color.cyan));

        // setup thread to update base Dataset
        SampleXYDatasetThread update = new SampleXYDatasetThread(data);
        Thread thread = new Thread(update);
        thread.start();

        return chart;

    }

    /**
     * Creates a gantt chart.
     *
     * @return a gantt chart.
     */
    public JFreeChart createGanttChart() {

        String title = resources.getString("gantt.task.title");
        String domain = resources.getString("gantt.task.domain");
        String range = resources.getString("gantt.task.range");

        IntervalCategoryDataset data = DemoDatasetFactory.createGanttDataset1();

        JFreeChart chart = ChartFactory.createGanttChart(title, domain, range, data,
                                                         true,
                                                         true,
                                                         false);

        // then customise it a little...
        chart.setBackgroundPaint(new GradientPaint(0, 0, Color.white, 1000, 0, Color.blue));
        return chart;

    }

}
