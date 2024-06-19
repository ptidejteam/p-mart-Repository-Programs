/* =======================================
 * JFreeChart : a Java Chart Class Library
 * =======================================
 * Version:         0.5.6;
 * Project Lead:    David Gilbert (david.gilbert@bigfoot.com);
 *
 * File:            Plot.java
 * Author:          David Gilbert;
 * Contributor(s):  -;
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
 * $Id: Plot.java,v 1.1 2007/10/10 18:52:16 vauchers Exp $
 */

package com.jrefinery.chart;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import javax.swing.*;
import com.jrefinery.chart.event.*;

/**
 * A 'plot' is a class that controls the visual representation of data - all the different types
 * of plots share a common structure that is defined by this base class.
 */
public abstract class Plot implements AxisChangeListener,
                                      ChartChangeListener {

  /** Useful constant representing zero. */
  public static final Integer ZERO = new Integer(0);

  /** Useful constant for specifying the horizontal axis. */
  public static final int HORIZONTAL_AXIS = 0;

  /** Useful constant for specifying the vertical axis. */
  public static final int VERTICAL_AXIS = 1;

  /** The chart that the plot belongs to. */
  protected JFreeChart chart;

  /** The vertical axis for the plot. */
  protected Axis verticalAxis;

  /** The horizontal axis for the plot. */
  protected Axis horizontalAxis;

  /** Amount of blank space around the plot area. */
  protected Insets insets;

  /** The Paint used to fill the plot background. */
  protected Paint backgroundPaint;

  /** The Stroke used to draw an outline around the plot. */
  protected Stroke outlineStroke;

  /** The Paint used to draw an outline around the plot. */
  protected Paint outlinePaint;

  /** Storage for registered change listeners. */
  protected java.util.List listeners;

  /**
   * Full constructor: returns a new Plot.
   * @param chart The chart that the plot belongs to;
   * @param horizontalAxis The horizontal axis for the plot;
   * @param verticalAxis The vertical axis for the plot;
   * @param insets Amount of blank space around the plot area;
   * @param background The Paint used to fill the plot background;
   * @param outlineStroke The Stroke used to draw an outline around the plot;
   * @param outlinePaint Storage for registered change listeners.
   */
  public Plot(JFreeChart chart, Axis horizontalAxis, Axis verticalAxis,
              Insets insets, Paint background, Stroke outlineStroke, Paint outlinePaint)
              throws AxisNotCompatibleException {

    this.chart = chart;

    this.horizontalAxis=horizontalAxis;
    horizontalAxis.setPlot(this);
    horizontalAxis.addChangeListener(this);

    this.verticalAxis=verticalAxis;
    verticalAxis.setPlot(this);
    verticalAxis.addChangeListener(this);

    this.insets = insets;
    this.backgroundPaint = background;
    this.outlineStroke = outlineStroke;
    this.outlinePaint = outlinePaint;

    this.listeners = new java.util.ArrayList();

  }

  /**
   * Standard constructor: returns a new Plot.
   * @param chart The chart that the plot belongs to;
   * @param horizontal The horizontal axis for the plot;
   * @param vertical The vertical axis for the plot.
   */
  public Plot(JFreeChart chart, Axis horizontal, Axis vertical)
           throws AxisNotCompatibleException {
    this(chart, horizontal, vertical,
         new Insets(2, 2, 2, 2), Color.white, new BasicStroke(1), Color.gray);
  }

  /**
   * Returns a reference to the chart that this plot belongs to.
   */
  public JFreeChart getChart() {
    return chart;
  }

  /**
   * Returns the specified axis.
   * @param select Determines the axis returned (use the constants HORIZONTAL_AXIS and
   *               VERTICAL_AXIS);
   * @see Plot#getHorizontalAxis
   * @see Plot#getVerticalAxis
   */
  public Axis getAxis(int select) {
    switch (select) {
      case HORIZONTAL_AXIS : return horizontalAxis;
      case VERTICAL_AXIS : return verticalAxis;
      default: return null;
    }
  }

  /**
   * Returns a reference to the horizontal axis.
   * @see Plot#getAxis
   */
  public HorizontalAxis getHorizontalAxis() {
    return (HorizontalAxis)horizontalAxis;
  }

  /**
   * Returns a reference to the vertical axis.
   * @see Plot#getAxis
   */
  public VerticalAxis getVerticalAxis() {
    return (VerticalAxis)verticalAxis;
  }

  /**
   * Returns the insets for the plot area.
   */
  public Insets getInsets() {
    return this.insets;
  }

  /**
   * Sets the insets for the plot and notifies registered listeners that the plot has been
   * modified.
   * @param insets The new insets.
   */
  public void setInsets(Insets insets) {
    this.insets = insets;
    notifyListeners(new PlotChangeEvent(this));
  }

  /**
   * Returns the background color of the plot area.
   */
  public Paint getBackgroundPaint() {
    return this.backgroundPaint;
  }

  /**
   * Sets the background color of the plot area, and notifies registered listeners that the
   * Plot has been modified.
   * @param paint The new background Paint.
   */
  public void setBackgroundPaint(Paint paint) {
    this.backgroundPaint = paint;
    notifyListeners(new PlotChangeEvent(this));
  }

  /**
   * Returns the Stroke used to outline the plot area.
   */
  public Stroke getOutlineStroke() {
    return this.outlineStroke;
  }

  /**
   * Sets the Stroke used to outline the plot area, and notifies registered listeners that the
   * plot has been modified.
   * @param stroke The new outline stroke.
   */
  public void setOutlineStroke(Stroke stroke) {
    this.outlineStroke = stroke;
    notifyListeners(new PlotChangeEvent(this));
  }

  /**
   * Returns the paint used to color the outline of the plot area.
   */
  public Paint getOutlinePaint() {
    return this.outlinePaint;
  }

  /**
   * Sets the color of the outline of the plot area, and notifies registered listeners that the
   * Plot has been modified.
   * @param paint The new outline paint.
   */
  public void setOutlinePaint(Paint paint) {
    this.outlinePaint = paint;
    notifyListeners(new PlotChangeEvent(this));
  }

  /**
   * Sets a reference back to the chart that this plot belongs to - configures the axes
   * according to the chart's data source.
   * @param chart The chart that the plot belongs to.
   */
  public void setChart(JFreeChart chart) {

    // if we are replacing an existing chart, then is the plot a registered listener...
    this.chart = chart;

    // setting the chart also means a new data source, so the axes may need adjusting...
    verticalAxis.configure();
    horizontalAxis.configure();
  }

  /**
   * Sets the vertical axis for the plot (this must be compatible with the plot type or an
   * exception is thrown).
   * @param axis The new axis.
   */
  public void setVerticalAxis(Axis axis) throws AxisNotCompatibleException {

    if (isCompatibleVerticalAxis(axis)) {

      // if we are replacing an existing axis, then chances are the plot is registered as an
      // AxisChangeListener so remove it...
      if (this.verticalAxis!=null) {
        this.verticalAxis.removeChangeListener(this);
      }
      // set the new axis and register the plot for changes...
      this.verticalAxis = axis;
      this.verticalAxis.addChangeListener(this);
    }
    else throw new AxisNotCompatibleException("Vertical axis not compatible with plot type.");

  }

  /**
   * Sets the horizontal axis for the plot (this must be compatible with the plot type or an
   * exception is thrown).
   * @param axis The new axis.
   */
  public void setHorizontalAxis(Axis axis) throws AxisNotCompatibleException {
    if (isCompatibleHorizontalAxis(axis)) {

      // if we are replacing an existing axis, then chances are the plot is registered as an
      // AxisChangeListener so remove it...
      if (this.horizontalAxis!=null) {
        this.horizontalAxis.removeChangeListener(this);
      }
      // set the new axis and register the plot for changes...
      this.horizontalAxis = axis;
      axis.addChangeListener(this);
    }
    else throw new AxisNotCompatibleException("Horizontal axis not compatible with plot type.");
  }

  /**
   * Notifies all registered listeners that the plot has been modified.
   * @param event Information about the change event.
   */
  public void notifyListeners(PlotChangeEvent event) {
    java.util.Iterator iterator = listeners.iterator();
    while (iterator.hasNext()) {
      PlotChangeListener listener = (PlotChangeListener)iterator.next();
      listener.plotChanged(event);
    }
  }

  /**
   * Registers an object for notification of changes to the plot.
   * @param listener The object to be registered.
   */
  public void addChangeListener(PlotChangeListener listener) {
    listeners.add(listener);
  }

  /**
   * Unregisters an object for notification of changes to the plot.
   * @param listener The object to be unregistered.
   */
  public void removeChangeListener(PlotChangeListener listener) {
    listeners.remove(listener);
  }

  /**
   * Checks the compatibility of a horizontal axis, returning true if the axis is compatible with
   * the plot, and false otherwise.
   * @param axis The horizontal axis.
   */
  public abstract boolean isCompatibleHorizontalAxis(Axis axis);

  /**
   * Checks the compatibility of a vertical axis, returning true if the axis is compatible with
   * the plot, and false otherwise.
   * @param axis The vertical axis;
   */
  public abstract boolean isCompatibleVerticalAxis(Axis axis);

  /**
   * Draws the plot on a Java 2D graphics device (such as the screen or a printer).
   * @param g2 The graphics device;
   * @param drawArea The area within which the plot should be drawn;
   */
  public abstract void draw(Graphics2D g2, Rectangle2D drawArea);

  /**
   * Draw the plot outline and background.
   * @param g2 The graphics device;
   * @param drawArea The area within which the plot should be drawn;
   */
  public void drawOutlineAndBackground(Graphics2D g2, Rectangle2D area) {

    if (backgroundPaint!=null) {
      g2.setPaint(backgroundPaint);
      g2.fill(area);
    }

    if ((outlineStroke!=null) && (outlinePaint!=null)) {
      g2.setStroke(outlineStroke);
      g2.setPaint(outlinePaint);
      g2.draw(area);
    }

  }

  /**
   * Receives notification of a change to one of the plot's axes.
   * @param event Information about the event (not used here).
   */
  public void axisChanged(AxisChangeEvent event) {
    notifyListeners(new PlotChangeEvent(this));
  }

  /**
   * Received notification of a change to the chart.  We are interested to know when the
   * data-source is replaced, so that we can adjust the axes if necessary;
   */
  public void chartChanged(ChartChangeEvent event) {

    if (event.getType()==ChartChangeEvent.DATA_SOURCE_REPLACED) {
      verticalAxis.configure();
      horizontalAxis.configure();
    }

    if (event.getType()==ChartChangeEvent.DATA_SOURCE_MODIFIED) {
      verticalAxis.configure();
      horizontalAxis.configure();
    }

  }

  /**
   * Returns a short string describing the plot type.
   */
  public abstract String getPlotType();

}