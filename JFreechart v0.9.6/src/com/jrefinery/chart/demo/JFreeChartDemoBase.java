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
 * -----------------------
 * JFreeChartDemoBase.java
 * -----------------------
 * (C) Copyright 2000-2003, by Simba Management Limited and Contributors.
 *
 * Original Author:  David Gilbert (for Simba Management Limited);
 * Contributor(s):   Andrzej Porebski;
 *                   Matthew Wright;
 *                   Serge V. Grachov;
 *                   Bill Kelemen;
 *                   Achilleus Mantzios;
 *                   Bryan Scott;
 *
 * $Id: JFreeChartDemoBase.java,v 1.1 2007/10/10 19:57:52 vauchers Exp $
 *
 * Changes
 * -------
 * 27-Jul-2002 : Created (BRS);
 * 10-Oct-2002 : Fixed errors reported by Checkstyle (DG);
 *
 */

package com.jrefinery.chart.demo;

import java.awt.Font;
import java.awt.Color;
import java.awt.GradientPaint;
import java.lang.reflect.Method;
import java.util.ResourceBundle;
import com.jrefinery.chart.JFreeChart;
import com.jrefinery.chart.ChartFactory;
import com.jrefinery.chart.TextTitle;
import com.jrefinery.chart.Spacer;
import com.jrefinery.chart.axis.ValueAxis;
import com.jrefinery.chart.axis.HorizontalCategoryAxis;
import com.jrefinery.chart.axis.NumberAxis;
import com.jrefinery.chart.axis.HorizontalNumberAxis;
import com.jrefinery.chart.axis.VerticalNumberAxis;
import com.jrefinery.chart.axis.VerticalLogarithmicAxis;
import com.jrefinery.chart.axis.HorizontalDateAxis;
//import com.jrefinery.chart.data.PlotFit;
//import com.jrefinery.chart.data.MovingAveragePlotFitAlgorithm;
import com.jrefinery.chart.plot.Plot;
import com.jrefinery.chart.plot.CategoryPlot;
import com.jrefinery.chart.plot.CombinedXYPlot;
import com.jrefinery.chart.plot.CompassPlot;
import com.jrefinery.chart.plot.MeterPlot;
import com.jrefinery.chart.plot.OverlaidXYPlot;
import com.jrefinery.chart.plot.PiePlot;
import com.jrefinery.chart.plot.ThermometerPlot;
import com.jrefinery.chart.plot.XYPlot;
import com.jrefinery.chart.renderer.DefaultDrawingSupplier;
import com.jrefinery.chart.renderer.DrawingSupplier;
import com.jrefinery.chart.renderer.StandardXYItemRenderer;
import com.jrefinery.chart.renderer.XYItemRenderer;
import com.jrefinery.chart.renderer.VerticalXYBarRenderer;
import com.jrefinery.chart.renderer.HighLowRenderer;
import com.jrefinery.chart.tooltips.HighLowToolTipGenerator;
import com.jrefinery.chart.tooltips.TimeSeriesToolTipGenerator;
import com.jrefinery.data.DatasetUtilities;
import com.jrefinery.data.MovingAverage;
import com.jrefinery.data.PieDataset;
import com.jrefinery.data.CategoryDataset;
import com.jrefinery.data.IntervalCategoryDataset;
import com.jrefinery.data.TimeSeries;
import com.jrefinery.data.TimeSeriesCollection;
import com.jrefinery.data.XYDataset;
import com.jrefinery.data.HighLowDataset;
import com.jrefinery.data.SignalsDataset;
import com.jrefinery.data.WindDataset;
import com.jrefinery.data.SubSeriesDataset;
import com.jrefinery.data.CombinedDataset;
import com.jrefinery.data.DefaultMeterDataset;

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
        { "HORIZONTAL_BAR_CHART", "createHorizontalBarChart", "chart1"},
        { "HORIZONTAL_STACKED_BAR_CHART", "createStackedHorizontalBarChart", "chart2"},
        { "VERTICAL_BAR_CHART", "createVerticalBarChart", "chart3"},
        { "VERTICAL_3D_BAR_CHART", "createVertical3DBarChart", "chart4"},
        { "VERTICAL_STACKED_BAR_CHART", "createVerticalStackedBarChart", "chart5"},
        { "VERTICAL_STACKED_3D_BAR_CHART", "createVerticalStacked3DBarChart", "chart6"},
        { "PIE_CHART_1", "createPieChartOne", "chart7"},
        { "PIE_CHART_2", "createPieChartTwo", "chart8"},
        { "XY_PLOT", "createXYPlot", "chart9"},
        { "TIME_SERIES_1_CHART", "createTimeSeries1Chart", "chart10"},
        { "TIME_SERIES_2_CHART", "createTimeSeries2Chart", "chart11"},
        { "TIME_SERIES_WITH_MA_CHART", "createTimeSeriesWithMAChart", "chart12"},
        { "HIGH_LOW_CHART", "createHighLowChart", "chart13"},
        { "CANDLESTICK_CHART", "createCandlestickChart", "chart14"},
        { "SIGNAL_CHART", "createSignalChart", "chart15"},
        { "WIND_PLOT", "createWindPlot", "chart16"},
        { "SCATTER_PLOT", "createScatterPlot", "chart17"},
        { "LINE_CHART", "createLineChart", "chart18"},
        { "VERTICAL_XY_BAR_CHART", "createVerticalXYBarChart", "chart19"},
        { "XY_PLOT_NULL", "createNullXYPlot", "chart20"},
        { "XY_PLOT_ZERO", "createXYPlotZeroData", "chart21"},
        { "TIME_SERIES_CHART_SCROLL", "createTimeSeriesChartInScrollPane", "chart22"},
        { "SINGLE_SERIES_BAR_CHART", "createSingleSeriesBarChart", "chart23"},
        { "DYNAMIC_CHART", "createDynamicXYChart", "chart24"},
        { "OVERLAID_CHART", "createOverlaidChart", "chart25"},
        { "HORIZONTALLY_COMBINED_CHART", "createHorizontallyCombinedChart", "chart26"},
        { "VERTICALLY_COMBINED_CHART", "createVerticallyCombinedChart", "chart27"},
        { "COMBINED_OVERLAID_CHART", "createCombinedAndOverlaidChart1", "chart28"},
        { "COMBINED_OVERLAID_DYNAMIC_CHART", "createCombinedAndOverlaidDynamicXYChart", "chart29"},
        { "THERMOMETER_CHART", "createThermometerChart", "chart30"},
        { "METER_CHART", "createMeterChartCircle", "chart31"},
        { "GANTT_CHART", "createGanttChart", "chart32"},
        { "METER_CHART2", "createMeterChartPie", "chart33"},
        { "METER_CHART3", "createMeterChartChord", "chart34"},
        { "COMPASS_CHART", "createCompassChart", "chart35"},
    };

    /** Base class name for localised resources. */
    public static final String BASE_RESOURCE_CLASS
        = "com.jrefinery.chart.demo.resources.DemoResources";

    /** Localised resources. */
    private ResourceBundle resources;

    /** An array of charts. */
    private JFreeChart[] chart = new JFreeChart[CHART_COMMANDS.length];

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

        if ((i < 0) && (i >= chart.length)) {
            i = 0;
        }

        if (chart[i] == null) {
            /// Utilise reflection to invoke method to create new chart if required.
            try {
                Method method = getClass().getDeclaredMethod(CHART_COMMANDS[i][1], null);
                chart[i] = (JFreeChart) method.invoke(this, null);
            }
            catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return chart[i];
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
        JFreeChart chart = ChartFactory.createHorizontalBarChart(title, domain, range, data, 
                                                                 true,
                                                                 true,
                                                                 false);

        // then customise it a little...
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
        JFreeChart chart = ChartFactory.createStackedHorizontalBarChart(title, domain, range,
                                                                        data, 
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
        JFreeChart chart = ChartFactory.createVerticalBarChart(title, domain, range, data, 
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
        JFreeChart chart = ChartFactory.createVerticalBarChart3D(title, domain, range, data,
                                                                 true,
                                                                 true,
                                                                 false);

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
            = ChartFactory.createStackedVerticalBarChart(title, domain, range, data, 
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
        JFreeChart chart
            = ChartFactory.createStackedVerticalBarChart3D(title, domain, range, data,
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
        JFreeChart chart = ChartFactory.createLineXYChart(title, domain, range, data, true, 
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
        HorizontalDateAxis axis = (HorizontalDateAxis) plot.getDomainAxis();
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
        VerticalLogarithmicAxis vla = new VerticalLogarithmicAxis(range);
        plot.setRangeAxis(vla);
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

        DefaultMeterDataset data = DemoDatasetFactory.createMeterDataset();
        data.setValue(new Double(34.0));
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
        DefaultMeterDataset data = DemoDatasetFactory.createMeterDataset();

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
        JFreeChart chart = ChartFactory.createScatterPlot(title, domain, range, data, true, 
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
                                                        true,
                                                        true,
                                                        false);

        // then customise it a little...
        chart.setBackgroundImage(JFreeChart.INFO.getLogo());
        chart.setBackgroundPaint(new GradientPaint(0, 0, Color.white, 0, 1000, Color.green));

        CategoryPlot plot = (CategoryPlot) chart.getPlot();
        plot.setBackgroundAlpha(0.65f);
        HorizontalCategoryAxis axis = (HorizontalCategoryAxis) plot.getDomainAxis();
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
        JFreeChart chart = ChartFactory.createVerticalXYBarChart(title, domain, range, data,
                                                                 true,
                                                                 false,
                                                                 false);

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

        JFreeChart chart = ChartFactory.createHorizontalBarChart(title, domain, range, data,
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
     *
     * @return an overlaid chart.
     */
    public JFreeChart createOverlaidChart() {

        // create a default chart based on some sample data...
        String title = this.resources.getString("combined.overlaid.title");
        String subtitleStr = this.resources.getString("combined.overlaid.subtitle");
        String domain = this.resources.getString("combined.overlaid.domain");
        String range = this.resources.getString("combined.overlaid.range");

        // create high-low and moving average dataset
        HighLowDataset highLowData = DemoDatasetFactory.createHighLowDataset();
        XYDataset maData = MovingAverage.createMovingAverage(highLowData, " (Moving Average)", 
                                     5 * 24 * 60 * 60 * 1000L, 
                                     5 * 24 * 60 * 60 * 1000L);
                                     
        // create a drawing supplier to control colors...
        DrawingSupplier supplier = new DefaultDrawingSupplier();
        
        // make an overlaid plot
        OverlaidXYPlot plot = new OverlaidXYPlot("Date", "Price");
        plot.setDomainAxis(new HorizontalDateAxis("Date"));
        NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
        rangeAxis.setAutoRangeIncludesZero(false);
        
        // create and add subplot 1...
        XYItemRenderer renderer1 = new HighLowRenderer(new HighLowToolTipGenerator());
        renderer1.setDrawingSupplier(supplier);
        XYPlot subplot1 = new XYPlot(highLowData, null, null, renderer1);

        plot.add(subplot1);

        //XYDataset data2 = new SubSeriesDataset(maData, 1); // MA data
        XYPlot subplot2 = new XYPlot(maData, null, null);
        XYItemRenderer renderer2 = new StandardXYItemRenderer();
        renderer2.setDrawingSupplier(supplier);
        renderer2.setToolTipGenerator(new TimeSeriesToolTipGenerator("d-MMM-yyyy", "0.00"));
        subplot2.setRenderer(renderer2);
        plot.add(subplot2);

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
        String range = this.resources.getString("combined.horizontal.range");

        TimeSeriesCollection dataset0 = new TimeSeriesCollection();
        TimeSeries eur = DemoDatasetFactory.createEURTimeSeries();
        dataset0.addSeries(eur);

        TimeSeriesCollection dataset1 = new TimeSeriesCollection();
//        TimeSeries usd = DemoDatasetFactory.createUSDTimeSeries();
        TimeSeries mav = MovingAverage.createMovingAverage(eur, "EUR/GBP (30 Day MA)", 30, 30);
        dataset1.addSeries(eur);
        dataset1.addSeries(mav);

        TimeSeriesCollection dataset2 = new TimeSeriesCollection();
        //TimeSeries gbp = DemoDatasetFactory.createGBPTimeSeries();
        dataset2.addSeries(eur);

        JFreeChart chart = null;

        // make a common vertical axis for all the sub-plots
        NumberAxis valueAxis = new VerticalNumberAxis(range);
        valueAxis.setAutoRangeIncludesZero(false);  // override default

        // make a horizontally combined plot
        CombinedXYPlot multiPlot = new CombinedXYPlot(valueAxis, CombinedXYPlot.HORIZONTAL);
        DrawingSupplier supplier = new DefaultDrawingSupplier();
        
        int[] weight = { 1, 1, 1 }; // control horizontal space assigned to each subplot

        // add subplot 1...
        XYPlot subplot1 = new XYPlot(dataset0, new HorizontalDateAxis("Date"), null);
        subplot1.getRenderer().setDrawingSupplier(supplier);
        multiPlot.add(subplot1, weight[0]);

        // add subplot 2...
        XYPlot subplot2 = new XYPlot(dataset1, new HorizontalDateAxis("Date"), null);
        subplot2.getRenderer().setDrawingSupplier(supplier);
        multiPlot.add(subplot2, weight[1]);

        // add subplot 3...
        XYPlot subplot3 = new XYPlot(dataset2, new HorizontalDateAxis("Date"),
                                     null, new VerticalXYBarRenderer(0.20));
        subplot3.getRenderer().setDrawingSupplier(supplier);
        multiPlot.add(subplot3, weight[2]);

        // now make the top level JFreeChart
        chart = new JFreeChart(title, JFreeChart.DEFAULT_TITLE_FONT, multiPlot, true);

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
        TimeSeries usd = DemoDatasetFactory.createUSDTimeSeries();
        dataset3.addSeries(eur);

        // make one shared horizontal axis
        ValueAxis timeAxis = new HorizontalDateAxis(domain);

        // make a vertically CombinedPlot that will contain the sub-plots
        CombinedXYPlot multiPlot = new CombinedXYPlot(timeAxis, CombinedXYPlot.VERTICAL);

        int[] weight = { 1, 1, 1, 1 }; // control vertical space allocated to each sub-plot
        DrawingSupplier supplier = new DefaultDrawingSupplier();
        
        // add subplot1...
        XYPlot subplot1 = new XYPlot(dataset0, null, new VerticalNumberAxis("Value"));
        NumberAxis range1 = (NumberAxis) subplot1.getRangeAxis();
        range1.setTickLabelFont(new Font("Monospaced", Font.PLAIN, 7));
        range1.setLabelFont(new Font("SansSerif", Font.PLAIN, 8));
        range1.setAutoRangeIncludesZero(false);
        subplot1.getRenderer().setDrawingSupplier(supplier);
        multiPlot.add(subplot1, weight[0]);

        // add subplot2...
        XYPlot subplot2 = new XYPlot(dataset1, null, new VerticalNumberAxis("Value"));
        NumberAxis range2 = (NumberAxis) subplot2.getRangeAxis();
        range2.setTickLabelFont(new Font("Monospaced", Font.PLAIN, 7));
        range2.setLabelFont(new Font("SansSerif", Font.PLAIN, 8));
        range2.setAutoRangeIncludesZero(false);
        subplot2.getRenderer().setDrawingSupplier(supplier);
        multiPlot.add(subplot2, weight[1]);

        // add subplot3...
        XYPlot subplot3 = new XYPlot(dataset2, null, new VerticalNumberAxis("Value"));
        XYItemRenderer renderer3 = new HighLowRenderer();
        subplot3.setRenderer(renderer3);
        renderer3.setDrawingSupplier(supplier);
        NumberAxis range3 = (NumberAxis) subplot3.getRangeAxis();
        range3.setTickLabelFont(new Font("Monospaced", Font.PLAIN, 7));
        range3.setLabelFont(new Font("SansSerif", Font.PLAIN, 8));
        range3.setAutoRangeIncludesZero(false);
        multiPlot.add(subplot3, weight[2]);

        // add subplot4...
        XYPlot subplot4 = new XYPlot(dataset3, null, new VerticalNumberAxis("Value"));
        XYItemRenderer renderer4 = new VerticalXYBarRenderer();
        renderer4.setDrawingSupplier(supplier);
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
        XYDataset highLowDatasetMA = MovingAverage.createMovingAverage(highLowDataset, " (MA)",
                             5 * 24 * 60 * 60 * 1000L, 5 * 24 * 60 * 60 * 1000L);

        int n = 3;    // number of combined (vertically laidout) charts

        // make one vertical axis for each (vertical) chart
        NumberAxis[] valueAxis = new NumberAxis[3];
        for (int i = 0; i < valueAxis.length; i++) {
            valueAxis[i] = new VerticalNumberAxis(ranges[i]);
            if (i <= 1) {
                valueAxis[i].setAutoRangeIncludesZero(false);  // override default
            }
        }

        // create CombinedPlot...
        CombinedXYPlot multiPlot = new CombinedXYPlot(new HorizontalDateAxis(domain),
                                                      CombinedXYPlot.VERTICAL);
                                                      
        DrawingSupplier supplier = new DefaultDrawingSupplier();

        int[] weight = { 1, 2, 2 };

        // add subplot1...
        XYPlot subplot1 = new XYPlot(dataset0, null, new VerticalNumberAxis(ranges[0]));
        NumberAxis axis1 = (NumberAxis) subplot1.getRangeAxis();
        axis1.setTickLabelFont(new Font("Monospaced", Font.PLAIN, 7));
        axis1.setLabelFont(new Font("SansSerif", Font.PLAIN, 8));
        axis1.setAutoRangeIncludesZero(false);
        subplot1.getRenderer().setDrawingSupplier(supplier);
        multiPlot.add(subplot1, weight[0]);

        // add subplot2 (an overlaid plot)...
        OverlaidXYPlot subplot2 = new OverlaidXYPlot(null, new VerticalNumberAxis(ranges[1]));
        NumberAxis axis2 = (NumberAxis) subplot2.getRangeAxis();
        axis2.setTickLabelFont(new Font("Monospaced", Font.PLAIN, 7));
        axis2.setLabelFont(new Font("SansSerif", Font.PLAIN, 8));
        axis2.setAutoRangeIncludesZero(false);
        XYPlot p1 = new XYPlot(dataset0, null, null);
        p1.getRenderer().setDrawingSupplier(supplier);
        subplot2.add(p1);
        XYPlot p2 = new XYPlot(dataset1, null, null);
        p2.getRenderer().setDrawingSupplier(supplier);
        subplot2.add(p2);

        multiPlot.add(subplot2, weight[1]);

        // add subplot3 (an overlaid plot)...
        OverlaidXYPlot subplot3 = new OverlaidXYPlot(null, new VerticalNumberAxis(ranges[2]));
        NumberAxis axis3 = (NumberAxis) subplot3.getRangeAxis();
        axis3.setTickLabelFont(new Font("Monospaced", Font.PLAIN, 7));
        axis3.setLabelFont(new Font("SansSerif", Font.PLAIN, 8));
        axis3.setAutoRangeIncludesZero(false);

        XYItemRenderer renderer3 = new HighLowRenderer();
        renderer3.setDrawingSupplier(supplier);
        XYPlot p3 = new XYPlot(highLowDataset, null, null, renderer3);
        subplot3.add(p3);
        XYPlot p4 = new XYPlot(highLowDatasetMA, null, null);
        p4.getRenderer().setDrawingSupplier(supplier);
        subplot3.add(p4);

        multiPlot.add(subplot3, weight[2]);

        // now create the master JFreeChart object
        JFreeChart chart = new JFreeChart(title,
                                          new Font("SansSerif", Font.BOLD, 12),
                                          multiPlot, true);

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
        String domain = this.resources.getString("combined.dynamic.domain");
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
        NumberAxis timeAxis = new HorizontalNumberAxis(domain);
        timeAxis.setTickMarksVisible(true);
        timeAxis.setAutoRangeIncludesZero(false);
        //timeAxis.setCrosshairVisible(false);

        // make one vertical axis for each (vertical) chart
        NumberAxis[] valueAxis = new NumberAxis[4];
        for (int i = 0; i < valueAxis.length; i++) {
            valueAxis[i] = new VerticalNumberAxis(ranges[i]);
            valueAxis[i].setAutoRangeIncludesZero(false);
        //    valueAxis[i].setCrosshairVisible(false);
        }

        CombinedXYPlot plot = new CombinedXYPlot(timeAxis, CombinedXYPlot.VERTICAL);
        DrawingSupplier supplier = new DefaultDrawingSupplier();

        // add subplot1...
        XYPlot subplot0 = new XYPlot(series0, null, valueAxis[0]);
        subplot0.getRenderer().setDrawingSupplier(supplier);
        plot.add(subplot0, 1);

        // add subplot2...
        XYPlot subplot1 = new XYPlot(series1, null, valueAxis[1]);
        subplot1.getRenderer().setDrawingSupplier(supplier);
        plot.add(subplot1, 1);

        // add subplot3...
        OverlaidXYPlot subplot2 = new OverlaidXYPlot(null, valueAxis[2]);

        // add two overlaid XY charts (share both axes)
        XYPlot p1 = new XYPlot(series0, null, null);
        p1.getRenderer().setDrawingSupplier(supplier);
        subplot2.add(p1);
        XYPlot p2 = new XYPlot(series1, null, null);
        p2.getRenderer().setDrawingSupplier(supplier);
        subplot2.add(p2);
        plot.add(subplot2, 1);

        // add subplot4...
        XYPlot subplot3 = new XYPlot(data, null, valueAxis[3]);
        subplot3.getRenderer().setDrawingSupplier(supplier);
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
