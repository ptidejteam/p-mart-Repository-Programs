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
 * -----------------
 * ChartFactory.java
 * -----------------
 * (C) Copyright 2001-2003, by Object Refinery Limited and Contributors.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited);
 * Contributor(s):   Serge V. Grachov;
 *                   Joao Guilherme Del Valle;
 *                   Bill Kelemen;
 *                   Jon Iles;
 *                   Jelai Wang;
 *                   Richard Atkinson;
 *                   David Browning (for Australian Institute of Marine Science)
 *
 * $Id: ChartFactory.java,v 1.1 2007/10/10 19:19:00 vauchers Exp $
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
 * 06-Aug-2002 : Updated Javadoc comments (DG);
 * 21-Aug-2002 : Added createPieChart(CategoryDataset) method (DG);
 * 02-Oct-2002 : Fixed errors reported by Checkstyle (DG);
 * 09-Oct-2002 : Added methods including tooltips and URL flags (DG);
 * 06-Nov-2002 : Moved renderers into a separate package (DG);
 * 18-Nov-2002 : Changed CategoryDataset to TableDataset (DG);
 * 21-Mar-2003 : Incorporated HorizontalCategoryAxis3D, see bug id 685501 (DG);
 * 13-May-2003 : Merged some horizontal and vertical methods (DG);
 * 24-May-2003 : Added support for timeline in createHighLowChart (BK);
 * 07-Jul-2003 : Added createHistogram(...) method contributed by Jelai Wang (DG);
 * 27-Jul-2003 : Added createStackedAreaXYChart(...) method (RA);
 * 05-Aug-2003 : added new method createBoxAndWhiskerChart (DB);
 * 08-Sep-2003 : Changed ValueAxis API (DG);
 * 07-Oct-2003 : Added stepped area XY chart contributed by Matthias Rose (DG);
 * 06-Nov-2003 : Added createWaterfallChart(...) method (DG);
 *
 */

package org.jfree.chart;

import java.awt.Insets;
import java.text.DateFormat;
import java.text.NumberFormat;

import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryAxis3D;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.NumberAxis3D;
import org.jfree.chart.axis.Timeline;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.labels.CategoryItemLabelGenerator;
import org.jfree.chart.labels.HighLowToolTipGenerator;
import org.jfree.chart.labels.IntervalCategoryItemLabelGenerator;
import org.jfree.chart.labels.PieItemLabelGenerator;
import org.jfree.chart.labels.StandardCategoryItemLabelGenerator;
import org.jfree.chart.labels.StandardPieItemLabelGenerator;
import org.jfree.chart.labels.StandardXYToolTipGenerator;
import org.jfree.chart.labels.StandardXYZToolTipGenerator;
import org.jfree.chart.labels.TimeSeriesToolTipGenerator;
import org.jfree.chart.labels.XYToolTipGenerator;
import org.jfree.chart.labels.XYZToolTipGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.Pie3DPlot;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.AreaRenderer;
import org.jfree.chart.renderer.AreaXYRenderer;
import org.jfree.chart.renderer.BarRenderer;
import org.jfree.chart.renderer.BarRenderer3D;
import org.jfree.chart.renderer.CandlestickRenderer;
import org.jfree.chart.renderer.CategoryItemRenderer;
import org.jfree.chart.renderer.GanttRenderer;
import org.jfree.chart.renderer.HighLowRenderer;
import org.jfree.chart.renderer.ItemLabelAnchor;
import org.jfree.chart.renderer.ItemLabelPosition;
import org.jfree.chart.renderer.LineAndShapeRenderer;
import org.jfree.chart.renderer.SignalRenderer;
import org.jfree.chart.renderer.StackedAreaRenderer;
import org.jfree.chart.renderer.StackedAreaXYRenderer;
import org.jfree.chart.renderer.StackedBarRenderer;
import org.jfree.chart.renderer.StackedBarRenderer3D;
import org.jfree.chart.renderer.StandardXYItemRenderer;
import org.jfree.chart.renderer.WaterfallBarRenderer;
import org.jfree.chart.renderer.WindItemRenderer;
import org.jfree.chart.renderer.XYBarRenderer;
import org.jfree.chart.renderer.XYBoxAndWhiskerRenderer;
import org.jfree.chart.renderer.XYBubbleRenderer;
import org.jfree.chart.renderer.XYItemRenderer;
import org.jfree.chart.renderer.XYStepAreaRenderer;
import org.jfree.chart.renderer.XYStepRenderer;
import org.jfree.chart.urls.CategoryURLGenerator;
import org.jfree.chart.urls.PieURLGenerator;
import org.jfree.chart.urls.StandardCategoryURLGenerator;
import org.jfree.chart.urls.StandardPieURLGenerator;
import org.jfree.chart.urls.StandardXYURLGenerator;
import org.jfree.chart.urls.StandardXYZURLGenerator;
import org.jfree.chart.urls.XYURLGenerator;
import org.jfree.chart.urls.XYZURLGenerator;
import org.jfree.data.CategoryDataset;
import org.jfree.data.HighLowDataset;
import org.jfree.data.IntervalCategoryDataset;
import org.jfree.data.IntervalXYDataset;
import org.jfree.data.PieDataset;
import org.jfree.data.SignalsDataset;
import org.jfree.data.TableXYDataset;
import org.jfree.data.WindDataset;
import org.jfree.data.XYDataset;
import org.jfree.data.XYZDataset;
import org.jfree.data.statistics.BoxAndWhiskerXYDataset;
import org.jfree.ui.TextAnchor;

/**
 * A collection of utility methods for creating some standard charts with JFreeChart.
 *
 * @author David Gilbert
 */
public abstract class ChartFactory {

    /**
     * Creates a pie chart with default settings.
     *
     * @param title  the chart title.
     * @param data  the dataset for the chart.
     * @param legend  a flag specifying whether or not a legend is required.
     * @param tooltips  configure chart to generate tool tips?
     * @param urls  configure chart to generate URLs?
     *
     * @return a pie chart.
     */
    public static JFreeChart createPieChart(String title,
                                            PieDataset data,
                                            boolean legend,
                                            boolean tooltips,
                                            boolean urls) {

        PiePlot plot = new PiePlot(data);
        plot.setInsets(new Insets(0, 5, 5, 5));

        PieItemLabelGenerator labelGenerator = null;
        if (tooltips) {
            labelGenerator = new StandardPieItemLabelGenerator();
        }

        PieURLGenerator urlGenerator = null;
        if (urls) {
            urlGenerator = new StandardPieURLGenerator();
        }

        plot.setItemLabelGenerator(labelGenerator);
        plot.setURLGenerator(urlGenerator);

        JFreeChart chart = new JFreeChart(title, JFreeChart.DEFAULT_TITLE_FONT, plot, legend);

        return chart;

    }

    /**
     * Creates a chart containing multiple pie charts, from a TableDataset.
     *
     * @param title  the chart title.
     * @param data  the dataset for the chart.
     * @param extractType  <code>PER_ROW</code> or <code>PER_COLUMN</code> (defined in
     *                     {@link PiePlot}).
     * @param legend  a flag specifying whether or not a legend is required.
     * @param tooltips  configure chart to generate tool tips?
     * @param urls  configure chart to generate URLs?
     *
     * @return a pie chart.
     */
    public static JFreeChart createPieChart(String title,
                                            CategoryDataset data,
                                            int extractType,
                                            boolean legend,
                                            boolean tooltips,
                                            boolean urls) {

        PiePlot plot = new PiePlot(data, extractType);
        plot.setInsets(new Insets(0, 5, 5, 5));

        PieItemLabelGenerator tooltipGenerator = null;
        if (tooltips) {
            tooltipGenerator = new StandardPieItemLabelGenerator();
        }

        PieURLGenerator urlGenerator = null;
        if (urls) {
            urlGenerator = new StandardPieURLGenerator();
        }

        plot.setItemLabelGenerator(tooltipGenerator);
        plot.setURLGenerator(urlGenerator);

        JFreeChart chart = new JFreeChart(title, JFreeChart.DEFAULT_TITLE_FONT, plot, legend);

        return chart;

    }

    /**
     * Creates a pie chart with default settings.
     *
     * @param title  the chart title.
     * @param data  the dataset for the chart.
     * @param legend  a flag specifying whether or not a legend is required.
     * @param tooltips  configure chart to generate tool tips?
     * @param urls  configure chart to generate URLs?
     *
     * @return a pie chart.
     */
    public static JFreeChart createPie3DChart(String title,
                                              PieDataset data,
                                              boolean legend,
                                              boolean tooltips,
                                              boolean urls) {

        Pie3DPlot plot = new Pie3DPlot(data);
        plot.setInsets(new Insets(0, 5, 5, 5));
        PieItemLabelGenerator tooltipGenerator = null;
        if (tooltips) {
            tooltipGenerator = new StandardPieItemLabelGenerator();
        }

        PieURLGenerator urlGenerator = null;
        if (urls) {
            urlGenerator = new StandardPieURLGenerator();
        }

        plot.setItemLabelGenerator(tooltipGenerator);
        plot.setURLGenerator(urlGenerator);
        JFreeChart chart = new JFreeChart(title, JFreeChart.DEFAULT_TITLE_FONT, plot, legend);

        return chart;

    }

    /**
     * Creates an area chart with default settings.
     *
     * @param title  the chart title.
     * @param categoryAxisLabel  the label for the category axis.
     * @param valueAxisLabel  the label for the value axis.
     * @param data  the dataset for the chart.
     * @param orientation  the plot orientation.
     * @param legend  a flag specifying whether or not a legend is required.
     * @param tooltips  configure chart to generate tool tips?
     * @param urls  configure chart to generate URLs?
     *
     * @return an area chart.
     */
    public static JFreeChart createAreaChart(String title,
                                             String categoryAxisLabel,
                                             String valueAxisLabel,
                                             CategoryDataset data,
                                             PlotOrientation orientation,
                                             boolean legend,
                                             boolean tooltips,
                                             boolean urls) {

        CategoryAxis categoryAxis = new CategoryAxis(categoryAxisLabel);
        categoryAxis.setCategoryMargin(0.0);

        ValueAxis valueAxis = new NumberAxis(valueAxisLabel);

        AreaRenderer renderer = new AreaRenderer();
        if (tooltips) {
            renderer.setItemLabelGenerator(new StandardCategoryItemLabelGenerator());
        }
        if (urls) {
            renderer.setItemURLGenerator(new StandardCategoryURLGenerator());
        }

        CategoryPlot plot = new CategoryPlot(data, categoryAxis, valueAxis, renderer);
        plot.setOrientation(orientation);
        JFreeChart chart = new JFreeChart(title, JFreeChart.DEFAULT_TITLE_FONT, plot, legend);

        return chart;

    }

    /**
     * Creates an area chart with default settings.
     *
     * @param title  the chart title.
     * @param categoryAxisLabel  the label for the category axis.
     * @param valueAxisLabel  the label for the value axis.
     * @param data  the dataset for the chart.
     * @param orientation  the plot orientation (horizontal or vertical).
     * @param legend  a flag specifying whether or not a legend is required.
     * @param tooltips  configure chart to generate tool tips?
     * @param urls  configure chart to generate URLs?
     *
     * @return an area chart.
     */
    public static JFreeChart createStackedAreaChart(String title,
                                                    String categoryAxisLabel,
                                                    String valueAxisLabel,
                                                    CategoryDataset data,
                                                    PlotOrientation orientation,
                                                    boolean legend,
                                                    boolean tooltips,
                                                    boolean urls) {

        CategoryAxis categoryAxis = new CategoryAxis(categoryAxisLabel);
        ValueAxis valueAxis = new NumberAxis(valueAxisLabel);

        StackedAreaRenderer renderer = new StackedAreaRenderer();
        if (tooltips) {
            renderer.setItemLabelGenerator(new StandardCategoryItemLabelGenerator());
        }
        if (urls) {
            renderer.setItemURLGenerator(new StandardCategoryURLGenerator());
        }

        CategoryPlot plot = new CategoryPlot(data, categoryAxis, valueAxis, renderer);
        plot.setOrientation(orientation);
        JFreeChart chart = new JFreeChart(title, JFreeChart.DEFAULT_TITLE_FONT, plot, legend);

        return chart;

    }

    /**
     * Creates a bar chart.
     *
     * @param title  the chart title.
     * @param categoryAxisLabel  the label for the category axis.
     * @param valueAxisLabel  the label for the value axis.
     * @param data  the dataset for the chart.
     * @param orientation  the plot orientation (horizontal or vertical).
     * @param legend  a flag specifying whether or not a legend is required.
     * @param tooltips  configure chart to generate tool tips?
     * @param urls  configure chart to generate URLs?
     *
     * @return A bar chart.
     */
    public static JFreeChart createBarChart(String title,
                                            String categoryAxisLabel,
                                            String valueAxisLabel,
                                            CategoryDataset data,
                                            PlotOrientation orientation,
                                            boolean legend,
                                            boolean tooltips,
                                            boolean urls) {

        CategoryAxis categoryAxis = new CategoryAxis(categoryAxisLabel);
        ValueAxis valueAxis = new NumberAxis(valueAxisLabel);

        BarRenderer renderer = new BarRenderer();
        if (orientation == PlotOrientation.HORIZONTAL) {
            ItemLabelPosition position1 = new ItemLabelPosition(
                ItemLabelAnchor.OUTSIDE3, TextAnchor.CENTER_LEFT, TextAnchor.CENTER, 0.0
            );
            renderer.setPositiveItemLabelPosition(position1);
            ItemLabelPosition position2 = new ItemLabelPosition(
                ItemLabelAnchor.OUTSIDE9, TextAnchor.CENTER_RIGHT, TextAnchor.CENTER, 0.0
            );
            renderer.setNegativeItemLabelPosition(position2);
         }
        else if (orientation == PlotOrientation.VERTICAL) {
            ItemLabelPosition position1 = new ItemLabelPosition(
                ItemLabelAnchor.OUTSIDE12, TextAnchor.BOTTOM_CENTER, TextAnchor.CENTER, 0.0
            );
            renderer.setPositiveItemLabelPosition(position1);
            ItemLabelPosition position2 = new ItemLabelPosition(
                ItemLabelAnchor.OUTSIDE6, TextAnchor.TOP_CENTER, TextAnchor.CENTER, 0.0
            );
            renderer.setNegativeItemLabelPosition(position2);
        }
        if (tooltips) {
            renderer.setItemLabelGenerator(new StandardCategoryItemLabelGenerator());
        }
        if (urls) {
            renderer.setItemURLGenerator(new StandardCategoryURLGenerator());
        }

        CategoryPlot plot = new CategoryPlot(data, categoryAxis, valueAxis, renderer);
        plot.setOrientation(orientation);
        JFreeChart chart = new JFreeChart(title, JFreeChart.DEFAULT_TITLE_FONT, plot, legend);

        return chart;

    }

    /**
     * Creates a stacked bar chart with default settings.
     *
     * @param title  the chart title.
     * @param domainAxisLabel  the label for the category axis.
     * @param rangeAxisLabel  the label for the value axis.
     * @param data  the dataset for the chart.
     * @param orientation  the orientation of the chart (horizontal or vertical).
     * @param legend  a flag specifying whether or not a legend is required.
     * @param tooltips  configure chart to generate tool tips?
     * @param urls  configure chart to generate URLs?
     *
     * @return a stacked bar chart.
     */
    public static JFreeChart createStackedBarChart(String title,
                                                   String domainAxisLabel,
                                                   String rangeAxisLabel,
                                                   CategoryDataset data,
                                                   PlotOrientation orientation,
                                                   boolean legend,
                                                   boolean tooltips,
                                                   boolean urls) {

        // create axes...
        CategoryAxis categoryAxis = new CategoryAxis(domainAxisLabel);
        ValueAxis valueAxis = new NumberAxis(rangeAxisLabel);

        // create the renderer...
        StackedBarRenderer renderer = new StackedBarRenderer();
        if (tooltips) {
            renderer.setItemLabelGenerator(new StandardCategoryItemLabelGenerator());
        }
        if (urls) {
            renderer.setItemURLGenerator(new StandardCategoryURLGenerator());
        }

        CategoryPlot plot = new CategoryPlot(data, categoryAxis, valueAxis, renderer);
        plot.setOrientation(orientation);
        JFreeChart chart = new JFreeChart(title, JFreeChart.DEFAULT_TITLE_FONT, plot, legend);

        return chart;

    }

    /**
     * Creates a bar chart with a 3D effect.
     *
     * @param title  the chart title.
     * @param categoryAxisLabel  the label for the category axis.
     * @param valueAxisLabel  the label for the value axis.
     * @param data  the dataset for the chart.
     * @param orientation  the plot orientation (horizontal or vertical).
     * @param legend  a flag specifying whether or not a legend is required.
     * @param tooltips  configure chart to generate tool tips?
     * @param urls  configure chart to generate URLs?
     *
     * @return a 3D-effect bar chart.
     */
    public static JFreeChart createBarChart3D(String title,
                                              String categoryAxisLabel,
                                              String valueAxisLabel,
                                              CategoryDataset data,
                                              PlotOrientation orientation,
                                              boolean legend,
                                              boolean tooltips,
                                              boolean urls) {

        CategoryAxis categoryAxis = new CategoryAxis3D(categoryAxisLabel);
        ValueAxis valueAxis = new NumberAxis3D(valueAxisLabel);

        BarRenderer3D renderer = new BarRenderer3D();
        if (tooltips) {
            renderer.setItemLabelGenerator(new StandardCategoryItemLabelGenerator());
        }
        if (urls) {
            renderer.setItemURLGenerator(new StandardCategoryURLGenerator());
        }

        CategoryPlot plot = new CategoryPlot(data, categoryAxis, valueAxis, renderer);
        plot.setOrientation(orientation);
        plot.setForegroundAlpha(0.75f);

        JFreeChart chart = new JFreeChart(title, JFreeChart.DEFAULT_TITLE_FONT, plot, legend);

        return chart;

    }

    /**
     * Creates a stacked bar chart with a 3D effect and default settings.
     *
     * @param title  the chart title.
     * @param categoryAxisLabel  the label for the category axis.
     * @param valueAxisLabel  the label for the value axis.
     * @param data  the dataset for the chart.
     * @param orientation  the orientation (horizontal or vertical).
     * @param legend  a flag specifying whether or not a legend is required.
     * @param tooltips  configure chart to generate tool tips?
     * @param urls  configure chart to generate URLs?
     *
     * @return a stacked vertical bar chart.
     */
    public static JFreeChart createStackedBarChart3D(String title,
                                                     String categoryAxisLabel,
                                                     String valueAxisLabel,
                                                     CategoryDataset data,
                                                     PlotOrientation orientation,
                                                     boolean legend,
                                                     boolean tooltips,
                                                     boolean urls) {

        // create the axes...
        CategoryAxis categoryAxis = new CategoryAxis3D(categoryAxisLabel);
        ValueAxis valueAxis = new NumberAxis3D(valueAxisLabel);

        // create the renderer...
        CategoryItemLabelGenerator toolTipGenerator = null;
        if (tooltips) {
            toolTipGenerator = new StandardCategoryItemLabelGenerator();
        }
        CategoryURLGenerator urlGenerator = null;
        if (urls) {
            urlGenerator = new StandardCategoryURLGenerator();
        }
        CategoryItemRenderer renderer = new StackedBarRenderer3D();
        renderer.setItemLabelGenerator(toolTipGenerator);
        renderer.setItemURLGenerator(urlGenerator);

        // create the plot...
        CategoryPlot plot = new CategoryPlot(data, categoryAxis, valueAxis, renderer);
        plot.setOrientation(orientation);

        // create the chart...
        JFreeChart chart = new JFreeChart(title, JFreeChart.DEFAULT_TITLE_FONT, plot, legend);

        return chart;

    }

    /**
     * Creates a line chart with default settings.
     *
     * @param title  the chart title.
     * @param categoryAxisLabel  the label for the category axis.
     * @param valueAxisLabel  the label for the value axis.
     * @param data  the dataset for the chart.
     * @param orientation  the chart orientation (horizontal or vertical).
     * @param legend  a flag specifying whether or not a legend is required.
     * @param tooltips  configure chart to generate tool tips?
     * @param urls  configure chart to generate URLs?
     *
     * @return a line chart.
     */
    public static JFreeChart createLineChart(String title,
                                             String categoryAxisLabel,
                                             String valueAxisLabel,
                                             CategoryDataset data,
                                             PlotOrientation orientation,
                                             boolean legend,
                                             boolean tooltips,
                                             boolean urls) {

        CategoryAxis categoryAxis = new CategoryAxis(categoryAxisLabel);
        ValueAxis valueAxis = new NumberAxis(valueAxisLabel);

        LineAndShapeRenderer renderer = new LineAndShapeRenderer();
        renderer.setDrawLines(true);
        renderer.setDrawShapes(false);
        if (tooltips) {
            renderer.setItemLabelGenerator(new StandardCategoryItemLabelGenerator());
        }
        if (urls) {
            renderer.setItemURLGenerator(new StandardCategoryURLGenerator());
        }
        CategoryPlot plot = new CategoryPlot(data, categoryAxis, valueAxis, renderer);
        plot.setOrientation(orientation);
        JFreeChart chart = new JFreeChart(title, JFreeChart.DEFAULT_TITLE_FONT, plot, legend);

        return chart;

    }

    /**
     * Creates a Gantt chart using the supplied attributes plus default values where required.
     *
     * @param title  the chart title.
     * @param categoryAxisLabel  the label for the category axis.
     * @param dateAxisLabel  the label for the date axis.
     * @param data  the dataset for the chart.
     * @param legend  a flag specifying whether or not a legend is required.
     * @param tooltips  configure chart to generate tool tips?
     * @param urls  configure chart to generate URLs?
     *
     * @return A Gantt chart.
     */
    public static JFreeChart createGanttChart(String title,
                                              String categoryAxisLabel,
                                              String dateAxisLabel,
                                              IntervalCategoryDataset data,
                                              boolean legend,
                                              boolean tooltips,
                                              boolean urls) {

        CategoryAxis categoryAxis = new CategoryAxis(categoryAxisLabel);
        DateAxis dateAxis = new DateAxis(dateAxisLabel);

        CategoryItemLabelGenerator toolTipGenerator = null;
        if (tooltips) {
            toolTipGenerator = new IntervalCategoryItemLabelGenerator(DateFormat.getDateInstance());
        }

        CategoryURLGenerator urlGenerator = null;
        if (urls) {
            urlGenerator = new StandardCategoryURLGenerator();
        }

        CategoryItemRenderer renderer = new GanttRenderer();
        renderer.setItemLabelGenerator(toolTipGenerator);
        renderer.setItemURLGenerator(urlGenerator);
        
        CategoryPlot plot = new CategoryPlot(data, categoryAxis, dateAxis, renderer);
        plot.setOrientation(PlotOrientation.HORIZONTAL);
        JFreeChart chart = new JFreeChart(title, JFreeChart.DEFAULT_TITLE_FONT, plot, legend);

        return chart;

    }

    /**
     * Creates a waterfall chart.
     *
     * @param title  the chart title.
     * @param categoryAxisLabel  the label for the category axis.
     * @param valueAxisLabel  the label for the value axis.
     * @param data  the dataset for the chart.
     * @param orientation  the plot orientation (horizontal or vertical).
     * @param legend  a flag specifying whether or not a legend is required.
     * @param tooltips  configure chart to generate tool tips?
     * @param urls  configure chart to generate URLs?
     *
     * @return A bar chart.
     */
    public static JFreeChart createWaterfallChart(String title,
                                                  String categoryAxisLabel,
                                                  String valueAxisLabel,
                                                  CategoryDataset data,
                                                  PlotOrientation orientation,
                                                  boolean legend,
                                                  boolean tooltips,
                                                  boolean urls) {

        CategoryAxis categoryAxis = new CategoryAxis(categoryAxisLabel);
        categoryAxis.setCategoryMargin(0.0);
        
        ValueAxis valueAxis = new NumberAxis(valueAxisLabel);

        WaterfallBarRenderer renderer = new WaterfallBarRenderer();
        if (orientation == PlotOrientation.HORIZONTAL) {
            ItemLabelPosition position = new ItemLabelPosition(
                ItemLabelAnchor.CENTER, TextAnchor.CENTER, TextAnchor.CENTER, Math.PI / 2.0
            );
            renderer.setPositiveItemLabelPosition(position);
            renderer.setNegativeItemLabelPosition(position);
         }
        else if (orientation == PlotOrientation.VERTICAL) {
            ItemLabelPosition position = new ItemLabelPosition(
                ItemLabelAnchor.CENTER, TextAnchor.CENTER, TextAnchor.CENTER, 0.0
            );
            renderer.setPositiveItemLabelPosition(position);
            renderer.setNegativeItemLabelPosition(position);
        }
        if (tooltips) {
            StandardCategoryItemLabelGenerator generator = new StandardCategoryItemLabelGenerator(
                NumberFormat.getInstance(), true
            );
            renderer.setItemLabelGenerator(generator);
        }
        if (urls) {
            renderer.setItemURLGenerator(new StandardCategoryURLGenerator());
        }

        CategoryPlot plot = new CategoryPlot(data, categoryAxis, valueAxis, renderer);
        plot.setOrientation(orientation);
        JFreeChart chart = new JFreeChart(title, JFreeChart.DEFAULT_TITLE_FONT, plot, legend);

        return chart;

    }

    /**
     * Creates a line chart (based on an {@link XYDataset}) with default settings.
     *
     * @param title  the chart title.
     * @param xAxisLabel  a label for the X-axis.
     * @param yAxisLabel  a label for the Y-axis.
     * @param dataset  the dataset for the chart.
     * @param orientation  the plot orientation (horizontal or vertical).
     * @param legend  a flag specifying whether or not a legend is required.
     * @param tooltips  configure chart to generate tool tips?
     * @param urls  configure chart to generate URLs?
     *
     * @return The chart.
     * 
     * @deprecated Use createXYLineChart(...).
     */
    public static JFreeChart createLineXYChart(String title,
                                               String xAxisLabel,
                                               String yAxisLabel,
                                               XYDataset dataset,
                                               PlotOrientation orientation,
                                               boolean legend,
                                               boolean tooltips,
                                               boolean urls) {

        return createXYLineChart(
            title, xAxisLabel, yAxisLabel, dataset, orientation, legend, tooltips, urls
        );

    }

    /**
     * Creates a line chart (based on an {@link XYDataset}) with default settings.
     *
     * @param title  the chart title.
     * @param xAxisLabel  a label for the X-axis.
     * @param yAxisLabel  a label for the Y-axis.
     * @param dataset  the dataset for the chart.
     * @param orientation  the plot orientation (horizontal or vertical).
     * @param legend  a flag specifying whether or not a legend is required.
     * @param tooltips  configure chart to generate tool tips?
     * @param urls  configure chart to generate URLs?
     *
     * @return The chart.
     */
    public static JFreeChart createXYLineChart(String title,
                                               String xAxisLabel,
                                               String yAxisLabel,
                                               XYDataset dataset,
                                               PlotOrientation orientation,
                                               boolean legend,
                                               boolean tooltips,
                                               boolean urls) {

        NumberAxis xAxis = new NumberAxis(xAxisLabel);
        xAxis.setAutoRangeIncludesZero(false);
        NumberAxis yAxis = new NumberAxis(yAxisLabel);
        XYItemRenderer renderer = new StandardXYItemRenderer(StandardXYItemRenderer.LINES);
        XYPlot plot = new XYPlot(dataset, xAxis, yAxis, renderer);
        plot.setOrientation(orientation);
        if (tooltips) {
            renderer.setToolTipGenerator(new StandardXYToolTipGenerator());
        }
        if (urls) {
            renderer.setURLGenerator(new StandardXYURLGenerator());
        }

        JFreeChart chart = new JFreeChart(title, JFreeChart.DEFAULT_TITLE_FONT, plot, legend);

        return chart;

    }

    /**
     * Creates an area chart using an {@link XYDataset}.
     *
     * @param title  the chart title.
     * @param xAxisLabel  a label for the X-axis.
     * @param yAxisLabel  a label for the Y-axis.
     * @param data  the dataset for the chart.
     * @param orientation  the plot orientation (horizontal or vertical).
     * @param legend  a flag specifying whether or not a legend is required.
     * @param tooltips  configure chart to generate tool tips?
     * @param urls  configure chart to generate URLs?
     *
     * @return an XY area chart.
     * 
     * @deprecated Use createXYAreaChart(...).
     */
    public static JFreeChart createAreaXYChart(String title,
                                               String xAxisLabel,
                                               String yAxisLabel,
                                               XYDataset data,
                                               PlotOrientation orientation,
                                               boolean legend,
                                               boolean tooltips,
                                               boolean urls) {

        return createXYAreaChart(
            title, xAxisLabel, yAxisLabel, data, orientation, legend, tooltips, urls
        );

    }

    /**
     * Creates an area chart using an {@link XYDataset}.
     *
     * @param title  the chart title.
     * @param xAxisLabel  a label for the X-axis.
     * @param yAxisLabel  a label for the Y-axis.
     * @param data  the dataset for the chart.
     * @param orientation  the plot orientation (horizontal or vertical).
     * @param legend  a flag specifying whether or not a legend is required.
     * @param tooltips  configure chart to generate tool tips?
     * @param urls  configure chart to generate URLs?
     *
     * @return an XY area chart.
     */
    public static JFreeChart createXYAreaChart(String title,
                                               String xAxisLabel,
                                               String yAxisLabel,
                                               XYDataset data,
                                               PlotOrientation orientation,
                                               boolean legend,
                                               boolean tooltips,
                                               boolean urls) {

        NumberAxis xAxis = new NumberAxis(xAxisLabel);
        xAxis.setAutoRangeIncludesZero(false);
        NumberAxis yAxis = new NumberAxis(yAxisLabel);
        XYPlot plot = new XYPlot(data, xAxis, yAxis, null);
        plot.setOrientation(orientation);
        
        XYToolTipGenerator toolTipGenerator = null;
        if (tooltips) {
            toolTipGenerator = new StandardXYToolTipGenerator();
        }

        XYURLGenerator urlGenerator = null;
        if (urls) {
            urlGenerator = new StandardXYURLGenerator();
        }

        plot.setRenderer(new AreaXYRenderer(AreaXYRenderer.AREA, toolTipGenerator, urlGenerator));

        JFreeChart chart = new JFreeChart(title, JFreeChart.DEFAULT_TITLE_FONT, plot, legend);

        return chart;

    }

    /**
     * Creates a scatter plot with default settings.
     *
     * @param title  the chart title.
     * @param xAxisLabel  a label for the X-axis.
     * @param yAxisLabel  a label for the Y-axis.
     * @param data  the dataset for the chart.
     * @param orientation  the plot orientation (horizontal or vertical).
     * @param legend  a flag specifying whether or not a legend is required.
     * @param tooltips  configure chart to generate tool tips?
     * @param urls  configure chart to generate URLs?
     *
     * @return a scatter plot.
     */
    public static JFreeChart createScatterPlot(String title,
                                               String xAxisLabel,
                                               String yAxisLabel,
                                               XYDataset data,
                                               PlotOrientation orientation,
                                               boolean legend,
                                               boolean tooltips,
                                               boolean urls) {

        NumberAxis xAxis = new NumberAxis(xAxisLabel);
        xAxis.setAutoRangeIncludesZero(false);
        NumberAxis yAxis = new NumberAxis(yAxisLabel);
        yAxis.setAutoRangeIncludesZero(false);

        XYPlot plot = new XYPlot(data, xAxis, yAxis, null);

        XYToolTipGenerator toolTipGenerator = null;
        if (tooltips) {
            toolTipGenerator = new StandardXYToolTipGenerator();
        }

        XYURLGenerator urlGenerator = null;
        if (urls) {
            urlGenerator = new StandardXYURLGenerator();
        }
        StandardXYItemRenderer renderer = new StandardXYItemRenderer(StandardXYItemRenderer.SHAPES,
                                                                     toolTipGenerator,
                                                                     urlGenerator);
        renderer.setShapesFilled(Boolean.TRUE);
        plot.setRenderer(renderer);
        plot.setOrientation(orientation);

        JFreeChart chart = new JFreeChart(title, JFreeChart.DEFAULT_TITLE_FONT, plot, legend);

        return chart;

    }

    /**
     * Creates a bubble chart with default settings.
     *
     * @param title  the chart title.
     * @param xAxisLabel  a label for the X-axis.
     * @param yAxisLabel  a label for the Y-axis.
     * @param data  the dataset for the chart.
     * @param orientation  the orientation (horizontal or vertical).
     * @param legend  a flag specifying whether or not a legend is required.
     * @param tooltips  configure chart to generate tool tips?
     * @param urls  configure chart to generate URLs?
     *
     * @return a scatter plot.
     */
    public static JFreeChart createBubbleChart(String title,
                                               String xAxisLabel,
                                               String yAxisLabel,
                                               XYZDataset data,
                                               PlotOrientation orientation,
                                               boolean legend,
                                               boolean tooltips,
                                               boolean urls) {

        NumberAxis xAxis = new NumberAxis(xAxisLabel);
        xAxis.setAutoRangeIncludesZero(false);
        NumberAxis yAxis = new NumberAxis(yAxisLabel);
        yAxis.setAutoRangeIncludesZero(false);

        XYPlot plot = new XYPlot(data, xAxis, yAxis, null);

        XYZToolTipGenerator toolTipGenerator = null;
        if (tooltips) {
            toolTipGenerator = new StandardXYZToolTipGenerator();
        }

        XYZURLGenerator urlGenerator = null;
        if (urls) {
            urlGenerator = new StandardXYZURLGenerator();
        }

        XYItemRenderer renderer = new XYBubbleRenderer(XYBubbleRenderer.SCALE_ON_RANGE_AXIS);
        renderer.setToolTipGenerator(toolTipGenerator);
        renderer.setURLGenerator(urlGenerator);
        plot.setRenderer(renderer);
        plot.setOrientation(orientation);

        JFreeChart chart = new JFreeChart(title, JFreeChart.DEFAULT_TITLE_FONT, plot, legend);

        return chart;

    }

    /**
     * Creates a wind plot with default settings.
     *
     * @param title  the chart title.
     * @param xAxisLabel  a label for the x-axis.
     * @param yAxisLabel  a label for the y-axis.
     * @param data  the dataset for the chart.
     * @param legend  a flag that controls whether or not a legend is created.
     * @param tooltips  configure chart to generate tool tips?
     * @param urls  configure chart to generate URLs?
     *
     * @return a wind plot.
     *
     */
    public static JFreeChart createWindPlot(String title,
                                            String xAxisLabel,
                                            String yAxisLabel,
                                            WindDataset data,
                                            boolean legend,
                                            boolean tooltips,
                                            boolean urls) {

        ValueAxis xAxis = new DateAxis(xAxisLabel);
        ValueAxis yAxis = new NumberAxis(yAxisLabel);
        yAxis.setRange(-12.0, 12.0);

        XYToolTipGenerator tooltipGenerator = null;
        if (tooltips) {
            tooltipGenerator = new StandardXYToolTipGenerator();
        }

        XYURLGenerator urlGenerator = null;
        if (urls) {
            urlGenerator = new StandardXYURLGenerator();
        }

        XYPlot plot = new XYPlot(data, xAxis, yAxis, null);
        WindItemRenderer renderer = new WindItemRenderer();
        renderer.setToolTipGenerator(tooltipGenerator);
        renderer.setURLGenerator(urlGenerator);
        plot.setRenderer(renderer);
        JFreeChart chart = new JFreeChart(title, JFreeChart.DEFAULT_TITLE_FONT, plot, legend);

        return chart;

    }

    /**
     * Creates and returns a time series chart.
     * <P>
     * A time series chart is an XYPlot with a date axis (horizontal) and a number axis (vertical),
     * and each data item is connected with a line.
     * <P>
     * Note that you can supply a TimeSeriesCollection to this method, as it implements the
     * XYDataset interface.
     *
     * @param title  the chart title.
     * @param timeAxisLabel  a label for the time axis.
     * @param valueAxisLabel  a label for the value axis.
     * @param data  the dataset for the chart.
     * @param legend  a flag specifying whether or not a legend is required.
     * @param tooltips  configure chart to generate tool tips?
     * @param urls  configure chart to generate URLs?
     *
     * @return a time series chart.
     */
    public static JFreeChart createTimeSeriesChart(String title,
                                                   String timeAxisLabel,
                                                   String valueAxisLabel,
                                                   XYDataset data,
                                                   boolean legend,
                                                   boolean tooltips,
                                                   boolean urls) {

        ValueAxis timeAxis = new DateAxis(timeAxisLabel);
        timeAxis.setLowerMargin(0.02);  // reduce the default margins on the time axis
        timeAxis.setUpperMargin(0.02);
        NumberAxis valueAxis = new NumberAxis(valueAxisLabel);
        valueAxis.setAutoRangeIncludesZero(false);  // override default
        XYPlot plot = new XYPlot(data, timeAxis, valueAxis, null);

        XYToolTipGenerator tooltipGenerator = null;
        if (tooltips) {
            tooltipGenerator = new TimeSeriesToolTipGenerator();
        }

        XYURLGenerator urlGenerator = null;
        if (urls) {
            urlGenerator = new StandardXYURLGenerator();
        }

        plot.setRenderer(new StandardXYItemRenderer(StandardXYItemRenderer.LINES,
                                                    tooltipGenerator, urlGenerator));
        JFreeChart chart = new JFreeChart(title, JFreeChart.DEFAULT_TITLE_FONT, plot, legend);

        return chart;

    }

    /**
     * Creates and returns a default instance of an XY bar chart.
     *
     * @param title  the chart title.
     * @param xAxisLabel  a label for the X-axis.
     * @param yAxisLabel  a label for the Y-axis.
     * @param data  the dataset for the chart.
     * @param orientation  the orientation (horizontal or vertical).
     * @param legend  a flag specifying whether or not a legend is required.
     * @param tooltips  configure chart to generate tool tips?
     * @param urls  configure chart to generate URLs?
     *
     * @return an XY bar chart.
     */
    public static JFreeChart createXYBarChart(String title,
                                              String xAxisLabel,
                                              String yAxisLabel,
                                              IntervalXYDataset data,
                                              PlotOrientation orientation,
                                              boolean legend,
                                              boolean tooltips,
                                              boolean urls) {

        DateAxis dateAxis = new DateAxis(xAxisLabel);
        ValueAxis valueAxis = new NumberAxis(yAxisLabel);

        XYToolTipGenerator tooltipGenerator = null;
        if (tooltips) {
            tooltipGenerator = new StandardXYToolTipGenerator();
        }

        XYURLGenerator urlGenerator = null;
        if (urls) {
            urlGenerator = new StandardXYURLGenerator();
        }
        
        XYBarRenderer renderer = new XYBarRenderer();
        renderer.setToolTipGenerator(tooltipGenerator);
        renderer.setURLGenerator(urlGenerator);
        
        XYPlot plot = new XYPlot(data, dateAxis, valueAxis, renderer);
        plot.setOrientation(orientation);
        
        JFreeChart chart = new JFreeChart(title, JFreeChart.DEFAULT_TITLE_FONT, plot, legend);

        return chart;

    }

    /**
     * Creates and returns a default instance of a high-low-open-close chart.
     *
     * @param title  the chart title.
     * @param timeAxisLabel  a label for the time axis.
     * @param valueAxisLabel  a label for the value axis.
     * @param data  the dataset for the chart.
     * @param legend  a flag specifying whether or not a legend is required.
     *
     * @return a high-low-open-close chart.
     */
    public static JFreeChart createHighLowChart(String title,
                                                String timeAxisLabel,
                                                String valueAxisLabel,
                                                HighLowDataset data,
                                                boolean legend) {

        ValueAxis timeAxis = new DateAxis(timeAxisLabel);
        NumberAxis valueAxis = new NumberAxis(valueAxisLabel);
        HighLowRenderer renderer = new HighLowRenderer();
        renderer.setToolTipGenerator(new HighLowToolTipGenerator());
        XYPlot plot = new XYPlot(data, timeAxis, valueAxis, renderer);
        JFreeChart chart = new JFreeChart(title, JFreeChart.DEFAULT_TITLE_FONT, plot, legend);

        return chart;

    }

    /**
     * Creates and returns a default instance of a high-low-open-close chart with
     * a special timeline. This timeline can be a {@link org.jfree.chart.axis.SegmentedTimeline} 
     * such as the Monday trough Friday timeline that will remove Saturdays and Sundays from
     * the axis.
     *
     * @param title  the chart title.
     * @param timeAxisLabel  a label for the time axis.
     * @param valueAxisLabel  a label for the value axis.
     * @param data  the dataset for the chart.
     * @param timeline  the timeline.
     * @param legend  a flag specifying whether or not a legend is required.
     *
     * @return a high-low-open-close chart.
     */
    public static JFreeChart createHighLowChart(String title,
                                                String timeAxisLabel,
                                                String valueAxisLabel,
                                                HighLowDataset data,
                                                Timeline timeline,
                                                boolean legend) {

        ValueAxis timeAxis = new DateAxis(timeAxisLabel, timeline);
        NumberAxis valueAxis = new NumberAxis(valueAxisLabel);
        HighLowRenderer renderer = new HighLowRenderer();
        renderer.setToolTipGenerator(new HighLowToolTipGenerator());
        XYPlot plot = new XYPlot(data, timeAxis, valueAxis, renderer);
        JFreeChart chart = new JFreeChart(title, JFreeChart.DEFAULT_TITLE_FONT, plot, legend);

        return chart;

    }

    /**
     * Creates and returns a default instance of a candlesticks chart.
     *
     * @param title  the chart title.
     * @param timeAxisLabel  a label for the time axis.
     * @param valueAxisLabel  a label for the value axis.
     * @param data  the dataset for the chart.
     * @param legend  a flag specifying whether or not a legend is required.
     *
     * @return a candlestick chart.
     */
    public static JFreeChart createCandlestickChart(String title,
                                                    String timeAxisLabel,
                                                    String valueAxisLabel,
                                                    HighLowDataset data,
                                                    boolean legend) {

        ValueAxis timeAxis = new DateAxis(timeAxisLabel);
        NumberAxis valueAxis = new NumberAxis(valueAxisLabel);
        XYPlot plot = new XYPlot(data, timeAxis, valueAxis, null);
        plot.setRenderer(new CandlestickRenderer());
        JFreeChart chart = new JFreeChart(title, JFreeChart.DEFAULT_TITLE_FONT, plot, legend);

        return chart;

    }

    /**
     * Creates and returns a default instance of a signal chart.
     *
     * @param title  the chart title.
     * @param timeAxisLabel  a label for the time axis.
     * @param valueAxisLabel  a label for the value axis.
     * @param data  the dataset for the chart.
     * @param legend  a flag specifying whether or not a legend is required.
     *
     * @return a signal chart.
     */
    public static JFreeChart createSignalChart(String title,
                                               String timeAxisLabel,
                                               String valueAxisLabel,
                                               SignalsDataset data,
                                               boolean legend) {

        ValueAxis timeAxis = new DateAxis(timeAxisLabel);
        NumberAxis valueAxis = new NumberAxis(valueAxisLabel);
        XYPlot plot = new XYPlot(data, timeAxis, valueAxis, null);
        plot.setRenderer(new SignalRenderer());
        JFreeChart chart = new JFreeChart(title, JFreeChart.DEFAULT_TITLE_FONT, plot, legend);

        return chart;

    }

    /**
     * Creates a stepped XY plot with default settings.
     *
     * @param title  the chart title.
     * @param xAxisLabel  a label for the X-axis.
     * @param yAxisLabel  a label for the Y-axis.
     * @param data  the dataset for the chart.
     * @param orientation  the plot orientation (horizontal or vertical).
     * @param legend  a flag specifying whether or not a legend is required.
     * @param tooltips  configure chart to generate tool tips?
     * @param urls  configure chart to generate URLs?
     *
     * @return A chart.
     */
    public static JFreeChart createXYStepChart(String title,
                                               String xAxisLabel,
                                               String yAxisLabel,
                                               XYDataset data,
                                               PlotOrientation orientation,
                                               boolean legend,
                                               boolean tooltips,
                                               boolean urls) {

        DateAxis xAxis = new DateAxis(xAxisLabel);
        NumberAxis yAxis = new NumberAxis(yAxisLabel);
        yAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());

        XYToolTipGenerator tooltipGenerator = null;
        if (tooltips) {
            tooltipGenerator = new StandardXYToolTipGenerator();
        }

        XYURLGenerator urlGenerator = null;
        if (urls) {
            urlGenerator = new StandardXYURLGenerator();
        }
        XYItemRenderer renderer = new XYStepRenderer(tooltipGenerator, urlGenerator);

        XYPlot plot = new XYPlot(data, xAxis, yAxis, null);
        plot.setRenderer(renderer);
        plot.setOrientation(orientation);
        plot.setDomainCrosshairVisible(false);
        plot.setRangeCrosshairVisible(false);
        JFreeChart chart = new JFreeChart(title, JFreeChart.DEFAULT_TITLE_FONT, plot, legend);
        return chart;

    }
    
    /**
     * Creates a filled stepped XY plot with default settings.
     *
     * @param title  the chart title.
     * @param xAxisLabel  a label for the X-axis.
     * @param yAxisLabel  a label for the Y-axis.
     * @param data  the dataset for the chart.
     * @param orientation  the plot orientation (horizontal or vertical).
     * @param legend  a flag specifying whether or not a legend is required.
     * @param tooltips  configure chart to generate tool tips?
     * @param urls  configure chart to generate URLs?
     *
     * @return A chart.
     */
    public static JFreeChart createXYStepAreaChart(String title,
                                                   String xAxisLabel,
                                                   String yAxisLabel,
                                                   XYDataset data,
                                                   PlotOrientation orientation,
                                                   boolean legend,
                                                   boolean tooltips,
                                                   boolean urls) {

        NumberAxis xAxis = new NumberAxis(xAxisLabel);
        xAxis.setAutoRangeIncludesZero(false);
        NumberAxis yAxis = new NumberAxis(yAxisLabel);

        XYToolTipGenerator tooltipGenerator = null;
        if (tooltips) {
            tooltipGenerator = new StandardXYToolTipGenerator();
        }

        XYURLGenerator urlGenerator = null;
        if (urls) {
            urlGenerator = new StandardXYURLGenerator();
        }
        XYItemRenderer renderer = new XYStepAreaRenderer(XYStepAreaRenderer.AREA_AND_SHAPES, 
                                                         tooltipGenerator, urlGenerator);

        XYPlot plot = new XYPlot(data, xAxis, yAxis, null);
        plot.setRenderer(renderer);
        plot.setOrientation(orientation);
        plot.setDomainCrosshairVisible(false);
        plot.setRangeCrosshairVisible(false);
        JFreeChart chart = new JFreeChart(title, JFreeChart.DEFAULT_TITLE_FONT, plot, legend);
        return chart;
    }
    
    /**
     * Creates a histogram.
     * 
     * @param title  the chart title.
     * @param xAxisLabel  the x axis label.
     * @param yAxisLabel  the y axis label.
     * @param dataset  the dataset.
     * @param orientation  the orientation (horizontal or vertical).
     * @param legend  create a legend?
     * @param tooltips  display tooltips?
     * @param urls  generate URLs?
     * 
     * @return The chart.
     */
    public static JFreeChart createHistogram(String title, 
                                             String xAxisLabel, 
                                             String yAxisLabel, 
                                             IntervalXYDataset dataset, 
                                             PlotOrientation orientation,
                                             boolean legend, 
                                             boolean tooltips, 
                                             boolean urls) {

        ValueAxis xAxis = new NumberAxis(xAxisLabel);
        ValueAxis yAxis = new NumberAxis(yAxisLabel);
        
        XYToolTipGenerator tooltipGenerator = null;
        if (tooltips) {
            tooltipGenerator = new StandardXYToolTipGenerator();
        }

        XYURLGenerator urlGenerator = null;
        if (urls) {
            urlGenerator = new StandardXYURLGenerator();
        }
        XYItemRenderer renderer = new XYBarRenderer();
        renderer.setToolTipGenerator(tooltipGenerator);
        renderer.setURLGenerator(urlGenerator);
        
        XYPlot plot = new XYPlot(dataset, xAxis, yAxis, null);
        plot.setRenderer(renderer);
        plot.setOrientation(orientation);
        
        JFreeChart chart = new JFreeChart(title, JFreeChart.DEFAULT_TITLE_FONT, plot, legend);

        return chart;
    }

    /**
     * Creates a stacked XY area plot.
     *
     * @param title  the chart title.
     * @param xAxisLabel  a label for the X-axis.
     * @param yAxisLabel  a label for the Y-axis.
     * @param data  the dataset for the chart.
     * @param orientation  the plot orientation (horizontal or vertical).
     * @param legend  a flag specifying whether or not a legend is required.
     * @param tooltips  configure chart to generate tool tips?
     * @param urls  configure chart to generate URLs?
     *
     * @return an XY area chart.
     */
    public static JFreeChart createStackedAreaXYChart(String title,
                                                      String xAxisLabel,
                                                      String yAxisLabel,
                                                      TableXYDataset data,
                                                      PlotOrientation orientation,
                                                      boolean legend,
                                                      boolean tooltips,
                                                      boolean urls) {

        NumberAxis xAxis = new NumberAxis(xAxisLabel);
        xAxis.setAutoRangeIncludesZero(false);
        NumberAxis yAxis = new NumberAxis(yAxisLabel);
        XYToolTipGenerator toolTipGenerator = null;
        if (tooltips) {
            toolTipGenerator = new StandardXYToolTipGenerator();
        }

        XYURLGenerator urlGenerator = null;
        if (urls) {
            urlGenerator = new StandardXYURLGenerator();
        }
        StackedAreaXYRenderer renderer = new StackedAreaXYRenderer(AreaXYRenderer.AREA,
                                                                   toolTipGenerator,
                                                                   urlGenerator);
        renderer.setOutline(true);
        XYPlot plot = new XYPlot(data, xAxis, yAxis, renderer);
        plot.setOrientation(orientation);

        JFreeChart chart = new JFreeChart(title, JFreeChart.DEFAULT_TITLE_FONT, plot, legend);

        return chart;

    }
    
    /**
     * Creates and returns a default instance of a box and whisker chart.
     *
     * @param title  the chart title.
     * @param timeAxisLabel  a label for the time axis.
     * @param valueAxisLabel  a label for the value axis.
     * @param data  the dataset for the chart.
     * @param legend  a flag specifying whether or not a legend is required.
     *
     * @return a box and whisker chart.
     */
    public static JFreeChart createBoxAndWhiskerChart(String title,
                                                String timeAxisLabel,
                                                String valueAxisLabel,
                                                BoxAndWhiskerXYDataset data,
                                                boolean legend) {

        ValueAxis timeAxis = new DateAxis(timeAxisLabel);
        NumberAxis valueAxis = new NumberAxis(valueAxisLabel);
        valueAxis.setAutoRangeIncludesZero(false);
        XYBoxAndWhiskerRenderer renderer = new XYBoxAndWhiskerRenderer(10.0);
        XYPlot plot = new XYPlot(data, timeAxis, valueAxis, renderer);
        JFreeChart chart = new JFreeChart(title, JFreeChart.DEFAULT_TITLE_FONT, plot, legend);

        return chart;

    }


}
