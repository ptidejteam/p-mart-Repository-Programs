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
 * -----------------
 * ChartFactory.java
 * -----------------
 * (C) Copyright 2001-2003, by Simba Management Limited and Contributors.
 *
 * Original Author:  David Gilbert (for Simba Management Limited);
 * Contributor(s):   Serge V. Grachov;
 *                   Joao Guilherme Del Valle;
 *                   Bill Kelemen;
 *                   Jon Iles;
 *
 * $Id: ChartFactory.java,v 1.1 2007/10/10 20:00:07 vauchers Exp $
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
 *
 */

package com.jrefinery.chart;

import java.awt.Insets;
import java.text.DateFormat;

import com.jrefinery.chart.axis.CategoryAxis;
import com.jrefinery.chart.axis.DateAxis;
import com.jrefinery.chart.axis.HorizontalCategoryAxis;
import com.jrefinery.chart.axis.HorizontalCategoryAxis3D;
import com.jrefinery.chart.axis.HorizontalDateAxis;
import com.jrefinery.chart.axis.HorizontalNumberAxis;
import com.jrefinery.chart.axis.HorizontalNumberAxis3D;
import com.jrefinery.chart.axis.NumberAxis;
import com.jrefinery.chart.axis.ValueAxis;
import com.jrefinery.chart.axis.VerticalCategoryAxis;
import com.jrefinery.chart.axis.VerticalNumberAxis;
import com.jrefinery.chart.axis.VerticalNumberAxis3D;
import com.jrefinery.chart.plot.HorizontalCategoryPlot;
import com.jrefinery.chart.plot.Pie3DPlot;
import com.jrefinery.chart.plot.PiePlot;
import com.jrefinery.chart.plot.Plot;
import com.jrefinery.chart.plot.VerticalCategoryPlot;
import com.jrefinery.chart.plot.XYPlot;
import com.jrefinery.chart.renderer.AreaRenderer;
import com.jrefinery.chart.renderer.AreaXYRenderer;
import com.jrefinery.chart.renderer.CandlestickRenderer;
import com.jrefinery.chart.renderer.CategoryItemRenderer;
import com.jrefinery.chart.renderer.HighLowRenderer;
import com.jrefinery.chart.renderer.HorizontalBarRenderer;
import com.jrefinery.chart.renderer.HorizontalBarRenderer3D;
import com.jrefinery.chart.renderer.HorizontalIntervalBarRenderer;
import com.jrefinery.chart.renderer.LineAndShapeRenderer;
import com.jrefinery.chart.renderer.SignalRenderer;
import com.jrefinery.chart.renderer.StackedAreaRenderer;
import com.jrefinery.chart.renderer.StackedHorizontalBarRenderer;
import com.jrefinery.chart.renderer.StackedVerticalBarRenderer;
import com.jrefinery.chart.renderer.StackedVerticalBarRenderer3D;
import com.jrefinery.chart.renderer.StandardXYItemRenderer;
import com.jrefinery.chart.renderer.VerticalBarRenderer;
import com.jrefinery.chart.renderer.VerticalBarRenderer3D;
import com.jrefinery.chart.renderer.VerticalXYBarRenderer;
import com.jrefinery.chart.renderer.WindItemRenderer;
import com.jrefinery.chart.renderer.XYBubbleRenderer;
import com.jrefinery.chart.renderer.XYItemRenderer;
import com.jrefinery.chart.renderer.XYStepRenderer;
import com.jrefinery.chart.tooltips.CategoryToolTipGenerator;
import com.jrefinery.chart.tooltips.HighLowToolTipGenerator;
import com.jrefinery.chart.tooltips.IntervalCategoryToolTipGenerator;
import com.jrefinery.chart.tooltips.PieToolTipGenerator;
import com.jrefinery.chart.tooltips.StandardCategoryToolTipGenerator;
import com.jrefinery.chart.tooltips.StandardPieToolTipGenerator;
import com.jrefinery.chart.tooltips.StandardXYToolTipGenerator;
import com.jrefinery.chart.tooltips.StandardXYZToolTipGenerator;
import com.jrefinery.chart.tooltips.TimeSeriesToolTipGenerator;
import com.jrefinery.chart.tooltips.XYToolTipGenerator;
import com.jrefinery.chart.tooltips.XYZToolTipGenerator;
import com.jrefinery.chart.urls.CategoryURLGenerator;
import com.jrefinery.chart.urls.PieURLGenerator;
import com.jrefinery.chart.urls.StandardCategoryURLGenerator;
import com.jrefinery.chart.urls.StandardPieURLGenerator;
import com.jrefinery.chart.urls.StandardXYURLGenerator;
import com.jrefinery.chart.urls.StandardXYZURLGenerator;
import com.jrefinery.chart.urls.XYURLGenerator;
import com.jrefinery.chart.urls.XYZURLGenerator;
import com.jrefinery.data.CategoryDataset;
import com.jrefinery.data.HighLowDataset;
import com.jrefinery.data.IntervalCategoryDataset;
import com.jrefinery.data.IntervalXYDataset;
import com.jrefinery.data.PieDataset;
import com.jrefinery.data.SignalsDataset;
import com.jrefinery.data.WindDataset;
import com.jrefinery.data.XYDataset;
import com.jrefinery.data.XYZDataset;

/**
 * A collection of utility methods for creating some standard charts with JFreeChart.
 *
 * @author David Gilbert
 */
public class ChartFactory {

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

        PieToolTipGenerator tooltipGenerator = null;
        if (tooltips) {
            tooltipGenerator = new StandardPieToolTipGenerator();
        }

        PieURLGenerator urlGenerator = null;
        if (urls) {
            urlGenerator = new StandardPieURLGenerator();
        }

        plot.setToolTipGenerator(tooltipGenerator);
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

        PieToolTipGenerator tooltipGenerator = null;
        if (tooltips) {
            tooltipGenerator = new StandardPieToolTipGenerator();
        }

        PieURLGenerator urlGenerator = null;
        if (urls) {
            urlGenerator = new StandardPieURLGenerator();
        }

        plot.setToolTipGenerator(tooltipGenerator);
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
        PieToolTipGenerator tooltipGenerator = null;
        if (tooltips) {
            tooltipGenerator = new StandardPieToolTipGenerator();
        }

        PieURLGenerator urlGenerator = null;
        if (urls) {
            urlGenerator = new StandardPieURLGenerator();
        }

        plot.setToolTipGenerator(tooltipGenerator);
        plot.setURLGenerator(urlGenerator);
        JFreeChart chart = new JFreeChart(title, JFreeChart.DEFAULT_TITLE_FONT, plot, legend);

        return chart;

    }

    /**
     * Creates a vertical bar chart with default settings.
     *
     * @param title  the chart title.
     * @param categoryAxisLabel  the label for the category axis.
     * @param valueAxisLabel  the label for the value axis.
     * @param data  the dataset for the chart.
     * @param legend  a flag specifying whether or not a legend is required.
     * @param tooltips  configure chart to generate tool tips?
     * @param urls  configure chart to generate URLs?
     *
     * @return a vertical bar chart.
     */
    public static JFreeChart createVerticalBarChart(String title,
                                                    String categoryAxisLabel,
                                                    String valueAxisLabel,
                                                    CategoryDataset data,
                                                    boolean legend,
                                                    boolean tooltips,
                                                    boolean urls) {

        CategoryAxis categoryAxis = new HorizontalCategoryAxis(categoryAxisLabel);
        ValueAxis valueAxis = new VerticalNumberAxis(valueAxisLabel);

        CategoryToolTipGenerator tooltipsGenerator = null;
        if (tooltips) {
            tooltipsGenerator = new StandardCategoryToolTipGenerator();
        }

        CategoryURLGenerator urlGenerator = null;
        if (urls) {
            urlGenerator = new StandardCategoryURLGenerator();
        }

        CategoryItemRenderer renderer = new VerticalBarRenderer(tooltipsGenerator,
                                                                urlGenerator);
        Plot plot = new VerticalCategoryPlot(data, categoryAxis, valueAxis, renderer);
        JFreeChart chart = new JFreeChart(title, JFreeChart.DEFAULT_TITLE_FONT, plot, legend);

        return chart;

    }
                                                      
    /**
     * Creates a vertical 3D-effect bar chart with default settings.
     *
     * @param title  the chart title.
     * @param categoryAxisLabel  the label for the category axis.
     * @param valueAxisLabel  the label for the value axis.
     * @param data  the dataset for the chart.
     * @param legend  a flag specifying whether or not a legend is required.
     * @param tooltips  configure chart to generate tool tips?
     * @param urls  configure chart to generate URLs?
     *
     * @return a vertical 3D-effect bar chart.
     */
    public static JFreeChart createVerticalBarChart3D(String title,
                                                      String categoryAxisLabel,
                                                      String valueAxisLabel,
                                                      CategoryDataset data,
                                                      boolean legend,
                                                      boolean tooltips,
                                                      boolean urls) {

        CategoryAxis categoryAxis = new HorizontalCategoryAxis3D(categoryAxisLabel);
        ValueAxis valueAxis = new VerticalNumberAxis3D(valueAxisLabel);

        CategoryToolTipGenerator toolTipGenerator = null;
        if (tooltips) {
            toolTipGenerator = new StandardCategoryToolTipGenerator();
        }        
        CategoryURLGenerator urlGenerator = null;
        if (urls) {
            urlGenerator = new StandardCategoryURLGenerator();
        }
        CategoryItemRenderer renderer = new VerticalBarRenderer3D(toolTipGenerator,
                                                                  urlGenerator);

        Plot plot = new VerticalCategoryPlot(data, categoryAxis, valueAxis, renderer);

        JFreeChart chart = new JFreeChart(title, JFreeChart.DEFAULT_TITLE_FONT, plot, legend);

        return chart;

    }
    
    /**
     * Creates a stacked vertical bar chart with default settings.
     *
     * @param title  the chart title.
     * @param domainAxisLabel  the label for the category axis.
     * @param rangeAxisLabel  the label for the value axis.
     * @param data   the dataset for the chart.
     * @param legend  a flag specifying whether or not a legend is required.
     * @param tooltips  configure chart to generate tool tips?
     * @param urls  configure chart to generate URLs?
     *
     * @return The chart.
     */
    public static JFreeChart createStackedVerticalBarChart(String title,
                                                           String domainAxisLabel,
                                                           String rangeAxisLabel,
                                                           CategoryDataset data,
                                                           boolean legend,
                                                           boolean tooltips,
                                                           boolean urls) {
        
        // create axes...
        CategoryAxis categoryAxis = new HorizontalCategoryAxis(domainAxisLabel);
        ValueAxis valueAxis = new VerticalNumberAxis(rangeAxisLabel);
        
        // create renderer...
        CategoryToolTipGenerator toolTipGenerator = null;
        if (tooltips) {
            toolTipGenerator = new StandardCategoryToolTipGenerator();
        }
        CategoryURLGenerator urlGenerator = null;
        if (urls) {
            urlGenerator = new StandardCategoryURLGenerator();
        }
        CategoryItemRenderer renderer = new StackedVerticalBarRenderer(toolTipGenerator,
                                                                       urlGenerator);
        
        // create plot...
        Plot plot = new VerticalCategoryPlot(data, categoryAxis, valueAxis, renderer);
        
        // create chart...
        JFreeChart chart = new JFreeChart(title, JFreeChart.DEFAULT_TITLE_FONT, plot, legend);

        return chart;
            
    }
                                                             
    /**
     * Creates a stacked vertical bar chart with default settings.
     *
     * @param title  the chart title.
     * @param categoryAxisLabel  the label for the category axis.
     * @param valueAxisLabel  the label for the value axis.
     * @param data  the dataset for the chart.
     * @param legend  a flag specifying whether or not a legend is required.
     * @param tooltips  configure chart to generate tool tips?
     * @param urls  configure chart to generate URLs?
     *
     * @return a stacked vertical bar chart.
     */
    public static JFreeChart createStackedVerticalBarChart3D(String title,
                                                             String categoryAxisLabel, 
                                                             String valueAxisLabel, 
                                                             CategoryDataset data, 
                                                             boolean legend,
                                                             boolean tooltips,
                                                             boolean urls) {

        // create the axes...
        CategoryAxis categoryAxis = new HorizontalCategoryAxis(categoryAxisLabel);
        ValueAxis valueAxis = new VerticalNumberAxis3D(valueAxisLabel);
        
        // create the renderer...
        CategoryToolTipGenerator toolTipGenerator = null;
        if (tooltips) {
            toolTipGenerator = new StandardCategoryToolTipGenerator();
        }
        CategoryURLGenerator urlGenerator = null;
        if (urls) {
            urlGenerator = new StandardCategoryURLGenerator();
        }
        CategoryItemRenderer renderer = new StackedVerticalBarRenderer3D(toolTipGenerator,
                                                                         urlGenerator);
                                                                         
        // create the plot...
        Plot plot = new VerticalCategoryPlot(data, categoryAxis, valueAxis, renderer);
        
        // create the chart...
        JFreeChart chart = new JFreeChart(title, JFreeChart.DEFAULT_TITLE_FONT, plot, legend);

        return chart;

    }

    /**
     * Creates a horizontal bar chart with default settings.
     *
     * @param title  the chart title.
     * @param domainAxisLabel  the label for the category axis.
     * @param rangeAxisLabel  the label for the value axis.
     * @param data  the dataset for the chart.
     * @param legend  a flag specifying whether or not a legend is required.
     * @param tooltips  configure chart to generate tool tips?
     * @param urls  configure chart to generate URLs?
     *
     * @return a horizontal bar chart.
     */
    public static JFreeChart createHorizontalBarChart(String title,
                                                      String domainAxisLabel,
                                                      String rangeAxisLabel,
                                                      CategoryDataset data,
                                                      boolean legend,
                                                      boolean tooltips,
                                                      boolean urls) {

        CategoryAxis domainAxis = new VerticalCategoryAxis(domainAxisLabel);
        ValueAxis rangeAxis = new HorizontalNumberAxis(rangeAxisLabel);

        CategoryToolTipGenerator tooltipGenerator = null;
        if (tooltips) {
            tooltipGenerator = new StandardCategoryToolTipGenerator();
        }

        CategoryURLGenerator urlGenerator = null;
        if (urls) {
            urlGenerator = new StandardCategoryURLGenerator();
        }

        CategoryItemRenderer renderer = new HorizontalBarRenderer(tooltipGenerator,
                                                                  urlGenerator);
        Plot plot = new HorizontalCategoryPlot(data, domainAxis, rangeAxis, renderer);
        JFreeChart chart = new JFreeChart(title, JFreeChart.DEFAULT_TITLE_FONT, plot, legend);

        return chart;

    }

    /**
     * Creates a horizontal 3D-effect bar chart with default settings.
     *
     * @param title  the chart title.
     * @param categoryAxisLabel  the label for the category axis.
     * @param valueAxisLabel  the label for the value axis.
     * @param data  the dataset for the chart.
     * @param legend  a flag specifying whether or not a legend is required.
     * @param tooltips  configure chart to generate tool tips?
     * @param urls  configure chart to generate URLs?
     *
     * @return a horizontal 3D-effect bar chart.
     */
    public static JFreeChart createHorizontalBarChart3D(String title,
                                                        String categoryAxisLabel,
                                                        String valueAxisLabel,
                                                        CategoryDataset data,
                                                        boolean legend,
                                                        boolean tooltips,
                                                        boolean urls) {

        CategoryAxis categoryAxis = new VerticalCategoryAxis(categoryAxisLabel);
        ValueAxis valueAxis = new HorizontalNumberAxis3D(valueAxisLabel);

        CategoryToolTipGenerator tooltipGenerator = null;
        if (tooltips) {
            tooltipGenerator = new StandardCategoryToolTipGenerator();
        }

        CategoryURLGenerator urlGenerator = null;
        if (urls) {
            urlGenerator = new StandardCategoryURLGenerator();
        }

        CategoryItemRenderer renderer
            = new HorizontalBarRenderer3D(HorizontalBarRenderer3D.DEFAULT_X_OFFSET,
                                          HorizontalBarRenderer3D.DEFAULT_Y_OFFSET,
                                          tooltipGenerator,
                                          urlGenerator);
        Plot plot = new HorizontalCategoryPlot(data, categoryAxis, valueAxis, renderer);
        plot.setForegroundAlpha(0.75f);
        JFreeChart chart = new JFreeChart(title, JFreeChart.DEFAULT_TITLE_FONT, plot, legend);

        return chart;

    }
   
    /**
     * Creates a stacked horizontal bar chart with default settings.
     *
     * @param title  the chart title.
     * @param domainAxisLabel  the label for the category axis.
     * @param rangeAxisLabel  the label for the value axis.
     * @param data  the dataset for the chart.
     * @param legend  a flag specifying whether or not a legend is required.
     * @param tooltips  configure chart to generate tool tips?
     * @param urls  configure chart to generate URLs?
     *
     * @return a stacked horizontal bar chart.
     */
    public static JFreeChart createStackedHorizontalBarChart(String title,
                                                             String domainAxisLabel, 
                                                             String rangeAxisLabel, 
                                                             CategoryDataset data, 
                                                             boolean legend,
                                                             boolean tooltips,
                                                             boolean urls) {

        // create axes...
        CategoryAxis domainAxis = new VerticalCategoryAxis(domainAxisLabel);
        ValueAxis rangeAxis = new HorizontalNumberAxis(rangeAxisLabel);
        
        // create the renderer...
        CategoryToolTipGenerator toolTipGenerator = null;
        if (tooltips) {
            toolTipGenerator = new StandardCategoryToolTipGenerator();
        }
        CategoryURLGenerator urlGenerator = null;
        if (urls) {
            urlGenerator = new StandardCategoryURLGenerator();
        }
        CategoryItemRenderer renderer = new StackedHorizontalBarRenderer(toolTipGenerator,
                                                                         urlGenerator);
                                                                         
                               
        Plot plot = new HorizontalCategoryPlot(data, domainAxis, rangeAxis, renderer);
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
                                             boolean legend,
                                             boolean tooltips,
                                             boolean urls) {

        CategoryAxis categoryAxis = new HorizontalCategoryAxis(categoryAxisLabel);
        ValueAxis valueAxis = new VerticalNumberAxis(valueAxisLabel);

        CategoryToolTipGenerator tooltipGenerator = null;
        if (tooltips) {
            tooltipGenerator = new StandardCategoryToolTipGenerator();
        }

        CategoryURLGenerator urlGenerator = null;
        if (urls) {
            urlGenerator = new StandardCategoryURLGenerator();
        }

        CategoryItemRenderer renderer = new LineAndShapeRenderer(LineAndShapeRenderer.LINES,
                                                                 LineAndShapeRenderer.TOP,
                                                                 tooltipGenerator, urlGenerator);
        Plot plot = new VerticalCategoryPlot(data, categoryAxis, valueAxis, renderer);
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
                                             boolean legend,
                                             boolean tooltips,
                                             boolean urls) {

        CategoryAxis domainAxis = new HorizontalCategoryAxis(categoryAxisLabel);
        domainAxis.setCategoryMargin(0.0);

        ValueAxis rangeAxis = new VerticalNumberAxis(valueAxisLabel);

        CategoryToolTipGenerator tooltipGenerator = null;
        if (tooltips) {
            tooltipGenerator = new StandardCategoryToolTipGenerator();
        }

        CategoryURLGenerator urlGenerator = null;
        if (urls) {
            urlGenerator = new StandardCategoryURLGenerator();
        }

        CategoryItemRenderer renderer = new AreaRenderer(tooltipGenerator, urlGenerator);
        Plot plot = new VerticalCategoryPlot(data, domainAxis, rangeAxis, renderer);
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
                                                    boolean legend,
                                                    boolean tooltips,
                                                    boolean urls) {

        CategoryAxis categoryAxis = new HorizontalCategoryAxis(categoryAxisLabel);
        ValueAxis valueAxis = new VerticalNumberAxis(valueAxisLabel);

        CategoryToolTipGenerator tooltipGenerator = null;
        if (tooltips) {
            tooltipGenerator = new StandardCategoryToolTipGenerator();
        }

        CategoryURLGenerator urlGenerator = null;
        if (urls) {
            urlGenerator = new StandardCategoryURLGenerator();
        }

        CategoryItemRenderer renderer = new StackedAreaRenderer(tooltipGenerator,
                                                                urlGenerator);
        Plot plot = new VerticalCategoryPlot(data, categoryAxis, valueAxis, renderer);
        JFreeChart chart = new JFreeChart(title, JFreeChart.DEFAULT_TITLE_FONT, plot, legend);

        return chart;

    }

    /**
     * Creates a Gantt chart using the supplied attributes plus default values where required.
     * <p>
     * The chart uses the following classes amongst others:
     * <ul>
     * <li>{@link HorizontalCategoryPlot}</li> - to coordinate drawing the data;
     * <li>{@link VerticalCategoryAxis}</li> - for the domain axis;
     * <li>{@link HorizontalDateAxis}</li> - for the range axis;
     * <li>{@link HorizontalIntervalBarRenderer}</li> - to draw the data items;
     * </ul>
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

        CategoryAxis categoryAxis = new VerticalCategoryAxis(categoryAxisLabel);
        DateAxis dateAxis = new HorizontalDateAxis(dateAxisLabel);

        CategoryToolTipGenerator toolTipGenerator = null;
        if (tooltips) {
            toolTipGenerator = new IntervalCategoryToolTipGenerator(DateFormat.getDateInstance());
        }

        CategoryURLGenerator urlGenerator = null;
        if (urls) {
            urlGenerator = new StandardCategoryURLGenerator();
        }

        CategoryItemRenderer renderer = new HorizontalIntervalBarRenderer(toolTipGenerator,
                                                                          urlGenerator);
        Plot plot = new HorizontalCategoryPlot(data, categoryAxis, dateAxis, renderer);
        JFreeChart chart = new JFreeChart(title, JFreeChart.DEFAULT_TITLE_FONT, plot, legend);

        return chart;

    }

    /**
     * Creates a line chart (based on an XYDataset) with default settings.
     *
     * @param title  the chart title.
     * @param xAxisLabel  a label for the X-axis.
     * @param yAxisLabel  a label for the Y-axis.
     * @param data  the dataset for the chart.
     * @param legend  a flag specifying whether or not a legend is required.
     * @param tooltips  configure chart to generate tool tips?
     * @param urls  configure chart to generate URLs?
     *
     * @return the chart.
     */
    public static JFreeChart createLineXYChart(String title,
                                               String xAxisLabel,
                                               String yAxisLabel,
                                               XYDataset data,
                                               boolean legend,
                                               boolean tooltips,
                                               boolean urls) {

        NumberAxis xAxis = new HorizontalNumberAxis(xAxisLabel);
        xAxis.setAutoRangeIncludesZero(false);
        NumberAxis yAxis = new VerticalNumberAxis(yAxisLabel);
        XYPlot plot = new XYPlot(data, xAxis, yAxis);

        XYToolTipGenerator tooltipGenerator = null;
        if (tooltips) {
            tooltipGenerator = new StandardXYToolTipGenerator();
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
     * Creates an XY area plot.
     *
     * @param title  the chart title.
     * @param xAxisLabel  a label for the X-axis.
     * @param yAxisLabel  a label for the Y-axis.
     * @param data  the dataset for the chart.
     * @param legend  a flag specifying whether or not a legend is required.
     * @param tooltips  configure chart to generate tool tips?
     * @param urls  configure chart to generate URLs?
     *
     * @return an XY area chart.
     */
    public static JFreeChart createAreaXYChart(String title,
                                               String xAxisLabel,
                                               String yAxisLabel,
                                               XYDataset data,
                                               boolean legend,
                                               boolean tooltips,
                                               boolean urls) {

        NumberAxis xAxis = new HorizontalNumberAxis(xAxisLabel);
        xAxis.setAutoRangeIncludesZero(false);
        NumberAxis yAxis = new VerticalNumberAxis(yAxisLabel);
        XYPlot plot = new XYPlot(data, xAxis, yAxis);

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
                                               boolean legend,
                                               boolean tooltips,
                                               boolean urls) {

        NumberAxis xAxis = new HorizontalNumberAxis(xAxisLabel);
        xAxis.setAutoRangeIncludesZero(false);
        NumberAxis yAxis = new VerticalNumberAxis(yAxisLabel);
        yAxis.setAutoRangeIncludesZero(false);

        XYPlot plot = new XYPlot(data, xAxis, yAxis);
        
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
        renderer.setDefaultShapeFilled(true);
        plot.setRenderer(renderer);
                                                   
                                                    
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
                                               boolean legend,
                                               boolean tooltips,
                                               boolean urls) {

        NumberAxis xAxis = new HorizontalNumberAxis(xAxisLabel);
        xAxis.setAutoRangeIncludesZero(false);
        NumberAxis yAxis = new VerticalNumberAxis(yAxisLabel);
        yAxis.setAutoRangeIncludesZero(false);

        XYPlot plot = new XYPlot(data, xAxis, yAxis);
        
        XYZToolTipGenerator toolTipGenerator = null;
        if (tooltips) {
            toolTipGenerator = new StandardXYZToolTipGenerator();
        }

        XYZURLGenerator urlGenerator = null;
        if (urls) {
            urlGenerator = new StandardXYZURLGenerator();
        }
        
        XYItemRenderer renderer = new XYBubbleRenderer(XYBubbleRenderer.SCALE_ON_RANGE_AXIS,
                                                       toolTipGenerator, urlGenerator);
        plot.setRenderer(renderer);
                                        
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

        ValueAxis xAxis = new HorizontalDateAxis(xAxisLabel);
        ValueAxis yAxis = new VerticalNumberAxis(yAxisLabel);
        yAxis.setMaximumAxisValue(12.0);
        yAxis.setMinimumAxisValue(-12.0);

        XYToolTipGenerator tooltipGenerator = null;
        if (tooltips) {
            tooltipGenerator = new StandardXYToolTipGenerator();
        }

        XYURLGenerator urlGenerator = null;
        if (urls) {
            urlGenerator = new StandardXYURLGenerator();
        }

        XYPlot plot = new XYPlot(data, xAxis, yAxis);
        plot.setRenderer(new WindItemRenderer(tooltipGenerator, urlGenerator));
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

        ValueAxis timeAxis = new HorizontalDateAxis(timeAxisLabel);
        timeAxis.setLowerMargin(0.02);  // reduce the default margins on the time axis
        timeAxis.setUpperMargin(0.02);
        NumberAxis valueAxis = new VerticalNumberAxis(valueAxisLabel);
        valueAxis.setAutoRangeIncludesZero(false);  // override default
        XYPlot plot = new XYPlot(data, timeAxis, valueAxis);
        
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
     * Creates and returns a default instance of a vertical XY bar chart.
     *
     * @param title  the chart title.
     * @param xAxisLabel  a label for the X-axis.
     * @param yAxisLabel  a label for the Y-axis.
     * @param data  the dataset for the chart.
     * @param legend  a flag specifying whether or not a legend is required.
     * @param tooltips  configure chart to generate tool tips?
     * @param urls  configure chart to generate URLs?
     *
     * @return a vertical XY bar chart.
     */
    public static JFreeChart createVerticalXYBarChart(String title,
                                                      String xAxisLabel, 
                                                      String yAxisLabel, 
                                                      IntervalXYDataset data, 
                                                      boolean legend,
                                                      boolean tooltips,
                                                      boolean urls) {

        DateAxis dateAxis = new HorizontalDateAxis(xAxisLabel);
        ValueAxis valueAxis = new VerticalNumberAxis(yAxisLabel);
        
        XYToolTipGenerator tooltipGenerator = null;
        if (tooltips) {
            tooltipGenerator = new StandardXYToolTipGenerator();
        }

        XYURLGenerator urlGenerator = null;
        if (urls) {
            urlGenerator = new StandardXYURLGenerator();
        }
        XYItemRenderer renderer = new VerticalXYBarRenderer(0.0,  tooltipGenerator, urlGenerator);
        
        XYPlot plot = new XYPlot(data, dateAxis, valueAxis);
        plot.setRenderer(renderer);
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

        ValueAxis timeAxis = new HorizontalDateAxis(timeAxisLabel);
        NumberAxis valueAxis = new VerticalNumberAxis(valueAxisLabel);
        XYPlot plot = new XYPlot(data, timeAxis, valueAxis);
        plot.setRenderer(new HighLowRenderer(new HighLowToolTipGenerator()));
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

        ValueAxis timeAxis = new HorizontalDateAxis(timeAxisLabel);
        NumberAxis valueAxis = new VerticalNumberAxis(valueAxisLabel);
        XYPlot plot = new XYPlot(data, timeAxis, valueAxis);
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

        ValueAxis timeAxis = new HorizontalDateAxis(timeAxisLabel);
        NumberAxis valueAxis = new VerticalNumberAxis(valueAxisLabel);
        XYPlot plot = new XYPlot(data, timeAxis, valueAxis);
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
                                               boolean legend,
                                               boolean tooltips,
                                               boolean urls) {

        DateAxis xAxis = new HorizontalDateAxis(xAxisLabel);
        NumberAxis yAxis = new VerticalNumberAxis(yAxisLabel);
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
        
        XYPlot plot = new XYPlot(data, xAxis, yAxis);
        plot.setRenderer(renderer);
        plot.setDomainCrosshairVisible(false);
        plot.setRangeCrosshairVisible(false);
        JFreeChart chart = new JFreeChart(title, JFreeChart.DEFAULT_TITLE_FONT, plot, legend);
        return chart;

    }

}
