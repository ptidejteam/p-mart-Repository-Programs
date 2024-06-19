/* =======================================
 * JFreeChart : a Java Chart Class Library
 * =======================================
 * Version:         0.5.6;
 * Project Lead:    David Gilbert (david.gilbert@bigfoot.com);
 *
 * File:            BarPlot.java
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
 * $Id: BarPlot.java,v 1.1 2007/10/10 18:52:17 vauchers Exp $
 */

package com.jrefinery.chart;

import java.awt.*;
import java.awt.geom.*;
import java.util.*;
import com.jrefinery.chart.event.*;

/**
 * A Plot that displays data in the form of a bar chart, using data from any class that
 * implements the CategoryDataSource interface.
 * @see Plot
 * @see CategoryDataSource
 */
public abstract class BarPlot extends Plot implements CategoryPlot {

  /** The gap before the first bar in the plot. */
  protected int introGap;

  /** The gap after the last bar in the plot. */
  protected int trailGap;

  /** The gap between the last bar in one category and the first bar in the next category. */
  protected int categoryGap;

  /** The gap between bars within the same category. */
  protected int seriesGap;

  /**
   * Standard constructor: returns a BarPlot with attributes specified by the caller.
   * @param chart The chart that the plot belongs to;
   * @param horizontal The horizontal axis;
   * @param vertical The vertical axis;
   * @param introGap The gap before the first bar in the plot;
   * @param trailGap The gap after the last bar in the plot;
   * @param categoryGap The gap between the last bar in one category and the first bar in the next
   *                    category;
   * @param seriesGap The gap between bars within the same category;
   */
  public BarPlot(JFreeChart chart,
                 Axis horizontal, Axis vertical, Insets insets,
                 int introGap, int trailGap, int categoryGap, int seriesGap)
                 throws AxisNotCompatibleException {

    super(chart, horizontal, vertical);
    this.insets = insets;
    this.introGap = introGap;
    this.trailGap = trailGap;
    this.categoryGap = categoryGap;
    this.seriesGap = seriesGap;
  }

  /**
   * Standard constructor: returns a bar plot with the specified axes...other attributes take
   * default values.
   */
  public BarPlot(JFreeChart chart, Axis horizontalAxis, Axis verticalAxis)
         throws AxisNotCompatibleException {
    this(chart, horizontalAxis, verticalAxis, new Insets(2, 2, 2, 2), 5, 5, 6, 0);
  }

  /**
   * A convenience method that returns the data source for the plot, cast as a
   * CategoryDataSource.
   */
  public CategoryDataSource getDataSource() {
    return (CategoryDataSource)chart.getDataSource();
  }

  /**
   * Sets the vertical axis for the plot.  This method should throw an exception if the axis
   * doesn't implement the required interfaces.
   * @param vAxis The new vertical axis.
   */
  public void setVerticalAxis(Axis vAxis) throws AxisNotCompatibleException {
    // check that the axis implements the required interface (if not raise an exception);
    super.setVerticalAxis(vAxis);
  }

  /**
   * Sets the horizontal axis for the plot.  This method should throw an exception if the axis
   * doesn't implement the required interfaces.
   * @param hAxis The new horizontal axis.
   */
  public void setHorizontalAxis(Axis hAxis) throws AxisNotCompatibleException {
    // check that the axis implements the required interface (if not raise an exception);
    super.setHorizontalAxis(hAxis);
  }

  /**
   * A convenience method that returns a list of the categories in the data source.
   */
  public java.util.List getCategories() {
    return getDataSource().getCategories();
  }

  /**
   * Returns the gap before the first bar on the chart, measured in Java 2D User Space units.
   */
  public int getIntroGap() {
    return introGap;
  }

  /**
   * Sets the gap before the first bar on the chart, and notifies registered listeners that the
   * plot has been modified.
   * @param gap The new gap value.
   */
  public void setIntroGap(int gap) {
    this.introGap = gap;
    notifyListeners(new PlotChangeEvent(this));
  }

  /**
   * Returns the gap following the last bar on the chart, measured in Java 2D User Space units.
   */
  public int getTrailGap() {
    return trailGap;
  }

  /**
   * Sets the gap after the last bar on the chart, and notifies registered listeners that the plot
   * has been modified.
   * @param gap The new gap value.
   */
  public void setTrailGap(int gap) {
    this.trailGap = gap;
    notifyListeners(new PlotChangeEvent(this));
  }

  /**
   * Returns the gap between the last bar in one category and the first bar in the next
   * category, measured in Java 2D User Space units.
   */
  public int getCategoryGap() {
    return categoryGap;
  }

  /**
   * Sets the gap between the last bar in one category and the first bar in the
   * next category, and notifies registered listeners that the plot has been modified.
   * @param gap The new gap value.
   */
  public void setCategoryGap(int gap) {
    this.categoryGap = gap;
    notifyListeners(new PlotChangeEvent(this));
  }

  /**
   * Returns the gap between one bar and the next within the same category, measured in Java 2D
   * User Space units.
   */
  public int getSeriesGap() {
    return seriesGap;
  }

  /**
   * Sets the gap between one bar and the next within the same category, and notifies registered
   * listeners that the plot has been modified.
   * @param gap The new gap value.
   */
  public void setSeriesGap(int gap) {
    this.seriesGap = gap;
    notifyListeners(new PlotChangeEvent(this));
  }

}