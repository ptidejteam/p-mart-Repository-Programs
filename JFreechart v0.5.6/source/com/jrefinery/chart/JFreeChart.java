/* =======================================
 * JFreeChart : a Java Chart Class Library
 * =======================================
 * Version:         0.5.6;
 * Project Lead:    David Gilbert (david.gilbert@bigfoot.com);
 *
 * File:            JFreeChart.java
 * Author:          David Gilbert;
 * Contributor(s):  Andrzej Porebski, David Li;
 *
 * (C) Copyright 2000, Simba Management Limited;
 *
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation;
 * either version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this library;
 * if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston,
 * MA 02111-1307, USA.
 *
 * $Id: JFreeChart.java,v 1.1 2007/10/10 18:52:16 vauchers Exp $
 */

package com.jrefinery.chart;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.text.*;
import java.util.*;
import javax.swing.*;
import com.jrefinery.chart.event.*;

/**
 * A chart class implemented using the Java 2D APIs.  The current version supports bar charts,
 * line charts, pie charts and xy plots (including time series data).
 * <P>
 * JFreeChart coordinates several objects to achieve its aim of being able to draw a chart
 * on a Java 2D graphics device: a Title, a Legend, a Plot and a DataSource (the Plot in turn
 * manages a horizontal axis and a vertical axis).
 * <P>
 * You should use JFreeChartPanel to display a chart in a GUI.
 * @see JFreeChartPanel
 * @see Title
 * @see Legend
 * @see Plot
 * @see DataSource
 */
public class JFreeChart implements DataSourceChangeListener,
                                   TitleChangeListener,
                                   LegendChangeListener,
                                   PlotChangeListener {

  /** The chart title. */
  protected Title title;

  /** The chart legend. */
  protected Legend legend;

  /** The source of the data to be displayed in the chart. */
  protected DataSource data;

  /** Draws the visual representation of the data. */
  protected Plot plot;

  /** Flag that determines whether or not the chart is drawn with anti-aliasing. */
  protected boolean antialias;

  /** Paint used to draw the background of the chart. */
  protected Paint chartBackgroundPaint;

  /** Paint objects used to color each series in the chart. */
  protected Paint[] seriesPaint;

  /** Stroke objects used to draw each series in the chart. */
  protected Stroke[] seriesStroke;

  /** Paint objects used to draw the outline of each series in the chart. */
  protected Paint[] seriesOutlinePaint;

  /** Stroke objects used to draw the outline of each series in the chart. */
  protected Stroke[] seriesOutlineStroke;

  /** Storage for registered change listeners. */
  protected java.util.List listeners;

  /**
   * Standard constructor: returns a JFreeChart.  There are static 'factory' methods that will
   * return a ready-made chart.
   * @param title The chart title;
   * @param font The font for displaying the chart title;
   * @param data The data source for the chart;
   * @param plot The class that controls the visual representation of the data;
   */
  public JFreeChart(String title, Font font, DataSource data, Plot plot) {
    listeners = new ArrayList();

    if (title!=null) {
      this.title = Title.createInstance(title, font);
      this.title.addChangeListener(this);
    }

    this.legend = Legend.createInstance(this);
    this.legend.addChangeListener(this);
    this.data = data;
    this.plot = plot;
    plot.setChart(this);

    // the chart listens for changes in the plot...
    plot.addChangeListener(this);

    // ...and vice versa...
    this.addChangeListener(plot);

    this.antialias = true;
    this.chartBackgroundPaint = Color.lightGray;
    this.seriesPaint = new Paint[] {Color.red, Color.blue, Color.green, Color.yellow, Color.cyan,
                                    Color.magenta, Color.orange, Color.pink, Color.lightGray};
    this.seriesStroke = new Stroke[] { new BasicStroke(1) };
    this.seriesOutlinePaint = new Paint[] { Color.gray };
    this.seriesOutlineStroke = new Stroke[] { new BasicStroke(1) };
  }

  /**
   * Returns the chart title.
   */
  public Title getTitle() {
    return title;
  }

  /**
   * Sets the chart title, and notifies registered listeners that the chart has been modified.
   * @param title The new  chart title.
   */
  public void setTitle(Title title) {
    this.title = title;
    if (title!=null) {
      title.addChangeListener(this);
    }
    fireChartChanged();
  }

  /**
   * A convenience method that sets the text of the chart title (this will trigger a chart change
   * event).
   * @param text The new title text;
   */
  public void setTitle(String text) {
    if (this.title==null) {
      this.setTitle(Title.createInstance(text, new Font("Arial", Font.BOLD, 14)));
    }
    else this.title.setTitle(text);
  }

  /**
   * Returns the current chart legend (possibly null);
   */
  public Legend getLegend() {
    return legend;
  }

  /**
   * Sets the chart legend, and notifies registered listeners that the chart has been modified.
   * @param legend The new chart legend (can be null);
   */
  public void setLegend(Legend legend) {
    this.legend = legend;
    if (legend!=null) {
      legend.addChangeListener(this);
    }
    fireChartChanged();
  }

  /**
   * Returns the current plot.
   */
  public Plot getPlot() {
    return this.plot;
  }

  /**
   * Returns the current data source;
   */
  public DataSource getDataSource() {
    return data;
  }

  /**
   * Sets the data source for the chart, and notifies registered listeners that the chart has
   * been modified.
   * @param data The new data source;
   */
  public void setDataSource(DataSource data) {

    // if an existing data source is being replaced, the chart should no longer be registered as a
    // change listener...
    if (this.data!=null) {
      this.data.removeChangeListener(this);
    }

    this.data = data;
    ChartChangeEvent event = new ChartChangeEvent(data, this, ChartChangeEvent.DATA_SOURCE_REPLACED);
    notifyListeners(event);

  }

  /**
   * Returns the current status of the anti-alias flag;
   */
  public boolean getAntiAlias() {
    return antialias;
  }

  /**
   * Sets antialiasing on or off.
   */
  public void setAntiAlias(boolean flag) {
    this.antialias = flag;
    fireChartChanged();
  }

  /**
   * Returns the Paint used to fill the chart background.
   */
  public Paint getChartBackgroundPaint() {
    return chartBackgroundPaint;
  }

  /**
   * Sets the Paint used to fill the chart background, and notifies registered listeners that the
   * chart has been modified.
   * @param paint The new background paint;
   */
  public void setChartBackgroundPaint(Paint paint) {
    this.chartBackgroundPaint = paint;
    fireChartChanged();
  }

  /**
   * Sets the paint used to color any shapes representing series, and notifies registered
   * listeners that the chart has been modified.
   * @param paint An array of Paint objects used to color series;
   */
  public void setSeriesPaint(Paint[] paint) {
    this.seriesPaint = paint;
    fireChartChanged();
  }

  /**
   * Sets the stroke used to draw any shapes representing series, and notifies registered
   * listeners that the chart has been modified.
   * @param stroke An array of Stroke objects used to draw series;
   */
  public void setSeriesStroke(Stroke[] stroke) {
    this.seriesStroke = stroke;
    fireChartChanged();
  }

  /**
   * Sets the paint used to outline any shapes representing series, and notifies registered
   * listeners that the chart has been modified.
   * @param paint An array of Paint objects for drawing the outline of series shapes;
   */
  public void setSeriesOutlinePaint(Paint[] paint) {
    this.seriesOutlinePaint = paint;
    fireChartChanged();
  }

  /**
   * Sets the stroke used to draw any shapes representing series, and notifies registered
   * listeners that the chart has been modified.
   * @param stroke An array of Stroke objects;
   */
  public void setSeriesOutlineStroke(Stroke[] stroke) {
    this.seriesOutlineStroke = stroke;
    fireChartChanged();
  }

  /**
   * Returns the Paint used to color any shapes for the specified series.
   * @param index The index of the series of interest (zero-based);
   */
  public Paint getSeriesPaint(int index) {
    return seriesPaint[index % seriesPaint.length];
  }

  /**
   * Returns the Stroke used to draw any shapes for the specified series.
   * @param index The index of the series of interest (zero-based);
   */
  public Stroke getSeriesStroke(int index) {
    return seriesStroke[index % seriesStroke.length];
  }

  /**
   * Returns the Paint used to outline any shapes for the specified series.
   * @param index The index of the series of interest (zero-based);
   */
  public Paint getSeriesOutlinePaint(int index) {
    return seriesOutlinePaint[index % seriesOutlinePaint.length];
  }

  /**
   * Returns the Stroke used to outline any shapes for the specified series.
   * @param index The index of the series of interest (zero-based);
   */
  public Stroke getSeriesOutlineStroke(int index) {
    return seriesOutlineStroke[index % seriesOutlinePaint.length];
  }

  /**
   * Draws the chart on a Java 2D graphics device (such as the screen or a printer).  This method
   * is the focus of the entire JFreeChart API.
   * @param g2 The graphics device;
   * @param chartArea The area within which the chart should be drawn;
   */
  public void draw(Graphics2D g2, Rectangle2D chartArea) {

    if (antialias) {
      g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    }
    else {
      g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
    }

    g2.setPaint(chartBackgroundPaint);
    g2.fill(chartArea);

    // draw the title
    Rectangle2D titleArea = null;
    if (title!=null) {
      titleArea = title.draw(g2, chartArea);
    }
    else {
      titleArea = new Rectangle2D.Double(chartArea.getX(), chartArea.getY(), 0, 0);
    }

    // calculate the non-title area - we assume that the title is using up the area at the top
    // of the chart area (because that's what StandardTitle currently does) but later we'll
    // have to test for TOP, BOTTOM, LEFT or RIGHT....
    Rectangle2D nonTitleArea = new Rectangle2D.Double(chartArea.getX(),
                                                      chartArea.getY()+titleArea.getHeight(),
                                                      chartArea.getWidth(),
                                                      chartArea.getHeight()-titleArea.getHeight());

    // draw the legend
    Rectangle2D legendArea = null;
    if (legend!=null) {
      legendArea = legend.draw(g2, nonTitleArea);
    }
    else {
      legendArea = new Rectangle2D.Double(0, 0, 0, 0);
    }

    // calculate the draw area - we assume that the legend is using up the area at the right
    // of the nonTitleArea (because that's what StandardLegend currently does) but later we'll
    // have to test for TOP, BOTTOM, LEFT or RIGHT...
    Rectangle2D drawArea = new Rectangle2D.Double(nonTitleArea.getX(),
                                                  nonTitleArea.getY(),
                                                  nonTitleArea.getWidth()-legendArea.getWidth(),
                                                  nonTitleArea.getHeight());

    // draw the plot (axes and data visualisation)
    plot.draw(g2, drawArea);

  }

  /**
   * Notifies all registered listeners that the chart has been modified.
   */
  public void fireChartChanged() {
    ChartChangeEvent event = new ChartChangeEvent(this);
    notifyListeners(event);
  }

  /**
   * Registers an object for notification of changes to the chart.
   * @param listener The object being registered;
   */
  public void addChangeListener(ChartChangeListener listener) {
    listeners.add(listener);
  }

  /**
   * Unregisters an object for notification of changes to the chart.
   * @param listener The object being unregistered.
   */
  public void removeChangeListener(ChartChangeListener listener) {
    listeners.remove(listener);
  }

  /**
   * Notifies all registered listeners that the chart has been modified.
   * @param event Contains information about the event that triggered the notification;
   */
  public void notifyListeners(ChartChangeEvent event) {
    Iterator iterator = listeners.iterator();
    while (iterator.hasNext()) {
      ChartChangeListener listener = (ChartChangeListener)iterator.next();
      listener.chartChanged(event);
    }
  }

  /**
   * Receives notification of a change to the plot's data source.
   * @param event Information about the event (not used here);
   */
  public void dataSourceChanged(DataSourceChangeEvent event) {

    ChartChangeEvent newEvent = new ChartChangeEvent(event, this,
                                                     ChartChangeEvent.DATA_SOURCE_MODIFIED);
    notifyListeners(event);

  }

  /**
   * Receives notification that the chart title has changed, and passes this on to registered
   * listeners.
   * @param event Information about the chart title change;
   */
  public void titleChanged(TitleChangeEvent event) {
    event.setChart(this);
    notifyListeners(event);
  }

  /**
   * Receives notification that the chart legend has changed, and passes this on to registered
   * listeners.
   * @param event Information about the chart legend change;
   */
  public void legendChanged(LegendChangeEvent event) {
    event.setChart(this);
    notifyListeners(event);
  }

  /**
   * Receives notification that the plot has changed, and passes this on to registered listeners.
   * @param event Information about the plot change;
   */
  public void plotChanged(PlotChangeEvent event) {
    event.setChart(this);
    notifyListeners(event);
  }

  //***********************************************************************************************

  // *** STATIC METHODS TO CONSTRUCT READY-MADE CHARTS ***

  /**
   * Creates and returns a default instance of a bar chart based on the specified data source.
   * @param data The data source for the chart;
   */
  public static JFreeChart createVerticalBarChart(DataSource data) {

    Axis categoryAxis = new HorizontalCategoryAxis("Category");
    Axis valueAxis = new VerticalNumberAxis("Value");

    try {
      Plot barPlot = new VerticalBarPlot(null, categoryAxis, valueAxis);
      return new JFreeChart("Vertical Bar Chart",
                            new Font("Arial", Font.BOLD, 24), data, barPlot);
    }
    catch (AxisNotCompatibleException e) {  // work on this later...
      return null;
    }

  }

  /**
   * Creates and returns a default instance of a bar chart based on the specified data source.
   * @param data The data source for the chart;
   */
  public static JFreeChart createHorizontalBarChart(DataSource data) {

    Axis categoryAxis = new VerticalCategoryAxis("Category");
    Axis valueAxis = new HorizontalNumberAxis("Value");

    try {
      Plot barPlot = new HorizontalBarPlot(null, valueAxis, categoryAxis);
      return new JFreeChart("Horizontal Bar Chart",
                            new Font("Arial", Font.BOLD, 24), data, barPlot);
    }
    catch (AxisNotCompatibleException e) {  // work on this later...
      return null;
    }

  }

  /**
   * Creates and returns a default instance of a line chart based on the specified data source.
   * @param data The data source for the chart;
   */
  public static JFreeChart createLineChart(DataSource data) {

    Axis categoryAxis = new HorizontalCategoryAxis("Category");
    Axis valueAxis = new VerticalNumberAxis("Value");

    try {
      Plot lplot = new LinePlot(null, categoryAxis, valueAxis);
      return new JFreeChart("Line Chart", new Font("Arial", Font.BOLD, 24), data, lplot);
    }
    catch (AxisNotCompatibleException e) {  // work on this later...
      return null;
    }
  }

  /**
   * Creates and returns a default instance of a pie chart based on the specified data source.
   * @param data The data source for the chart;
   */
  public static JFreeChart createPieChart(CategoryDataSource data) {
    try {
      Plot piePlot = new PiePlot(null);
      return new JFreeChart("Pie Chart", new Font("Arial", Font.BOLD, 24), data, piePlot);
    }
    catch (AxisNotCompatibleException e) {
      return null;
    }
  }

  /**
   * Creates and returns a default instance of an XY chart based on the specified data source.
   * @param data The data source for the chart;
   */
  public static JFreeChart createXYChart(XYDataSource data) {

    Axis xAxis = new HorizontalNumberAxis("X");
    Axis yAxis = new VerticalNumberAxis("Y");

    try {
      Plot xyPlot = new XYPlot(null, xAxis, yAxis);
      return new JFreeChart("XY Plot", new Font("Arial", Font.BOLD, 24), data, xyPlot);
    }
    catch (AxisNotCompatibleException e) {  // work on this later...
      return null;
    }
  }

  /**
   * Creates and returns a default instance of an XY chart based on the specified data source.
   * @param data The data source for the chart;
   */
  public static JFreeChart createTimeSeriesChart(XYDataSource data) {

    Axis xAxis = new HorizontalDateAxis();
    Axis yAxis = new VerticalNumberAxis();

    try {
      Plot xyPlot = new XYPlot(null, xAxis, yAxis);
      return new JFreeChart("Time Series", new Font("Arial", Font.BOLD, 24), data, xyPlot);
    }
    catch (AxisNotCompatibleException e) {   // work on this later...
      return null;
    }
  }

  /**
   * Creates and returns a default instance of an Hi/Low chart based on the specified data source.
   * <P>
   * Added by Andrzej Porebski.
   * @param data The data source for the chart.
   */
  public static JFreeChart createHiLowChart(XYDataSource data) {
    Axis xAxis = new HorizontalDateAxis("Date");
    NumberAxis yAxis = new VerticalNumberAxis("Stock price in ($) per Share");

    try {
      HiLowPlot hlPlot = new HiLowPlot(null, xAxis, yAxis);
      JFreeChart chart = new JFreeChart("HiLow Plot", new Font("Arial", Font.BOLD, 12), data,
                                        hlPlot);
      yAxis.configure();
      yAxis.setAutoRange(false);
      yAxis.setMinimumAxisValue(new Double(0));
      return chart;
    }
    catch (AxisNotCompatibleException e) {
      return null;
    }
  }

}