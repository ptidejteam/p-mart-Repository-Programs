/* =======================================
 * JFreeChart : a Java Chart Class Library
 * =======================================
 * Version:         0.5.6;
 * Project Lead:    David Gilbert (david.gilbert@bigfoot.com);
 *
 * File:            LinePlot.java
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
 * $Id: LinePlot.java,v 1.1 2007/10/10 18:52:16 vauchers Exp $
 */

package com.jrefinery.chart;

import java.awt.*;
import java.awt.geom.*;
import java.util.*;

/**
 * A Plot that displays data in the form of a line chart, using data from any class that
 * implements the CategoryDataSource interface.
 * @see Plot
 * @see CategoryDataSource
 */
public class LinePlot extends Plot implements CategoryPlot, VerticalValuePlot {

  /**
   * Standard constructor: returns a LinePlot with attributes specified by the caller.
   * @param chart The chart that the plot belongs to;
   * @param horizontal The horizontal axis;
   * @param vertical The vertical axis;
   */
  public LinePlot(JFreeChart chart, Axis horizontal, Axis vertical)
           throws AxisNotCompatibleException {
    super(chart, horizontal, vertical);
  }

  /**
   * A convenience method that returns the data source for the plot, cast as a
   * CategoryDataSource.
   */
  public CategoryDataSource getDataSource() {
    return (CategoryDataSource)chart.getDataSource();
  }

  /**
   * A convenience method that returns a reference to the horizontal axis cast as a
   * CategoryAxis.
   */
  public CategoryAxis getCategoryAxis() {
    return (CategoryAxis)horizontalAxis;
  }

  /**
   * A convenience method that returns a reference to the vertical axis cast as a
   * VerticalNumberAxis.
   */
  public VerticalNumberAxis getValueAxis() {
    return (VerticalNumberAxis)verticalAxis;
  }

  /**
   * A convenience method that returns a list of the categories in the data source.
   */
  public java.util.List getCategories() {
    return getDataSource().getCategories();
  }

  /**
   * Returns the x-coordinate (in Java 2D User Space) of the center of the specified category.
   * @param category The index of the category of interest (first category index = 0);
   * @param area The region within which the plot will be drawn.
   */
  public double getCategoryCoordinate(int category, Rectangle2D area) {
    int categoryCount = getDataSource().getCategoryCount();
    return area.getX()+(category+1)*(area.getWidth()/(categoryCount+1));
  }

  /**
   * Returns a list of lines that will fit inside the specified area.
   */
  private java.util.List getLines(Rectangle2D plotArea) {

    java.util.List lines = new ArrayList();
    CategoryDataSource data = getDataSource();
    if (data!=null) {
      // series, category counts
      int categoryCount = data.getCategoryCount();
      int seriesCount = data.getSeriesCount();

      for (int i=0; i<seriesCount; i++) {
        Point2D prev = null;
        int categoryIndex = 0;
        Iterator iterator = data.getCategories().iterator();
        while (iterator.hasNext()) {
          Object category = iterator.next();
          Number value = data.getValue(i, category);
          Paint p = chart.getSeriesPaint(i);
          Stroke s = chart.getSeriesStroke(i);
          double x = getCategoryCoordinate(categoryIndex, plotArea);
          Point2D current = new Point2D.Double(x, getValueAxis().translatedValue(value, plotArea));
          if (prev!=null) {
            lines.add(new Line(prev.getX(), prev.getY(), current.getX(), current.getY(), s, p));
          }
          prev = current;
          categoryIndex++;
        }
        prev = null;
      }
    }
    return lines;
  }

  /**
   * Checks the compatibility of a horizontal axis, returning true if the axis is compatible with
   * the plot, and false otherwise.
   * @param axis The horizontal axis.
   */
  public boolean isCompatibleHorizontalAxis(Axis axis) {
    if (axis instanceof CategoryAxis) {
      return true;
    }
    else return false;
  }

  /**
   * Checks the compatibility of a vertical axis, returning true if the axis is compatible with
   * the plot, and false otherwise.
   * @param axis The vertical axis;
   */
  public boolean isCompatibleVerticalAxis(Axis axis) {
    if (axis instanceof VerticalNumberAxis) {
      return true;
    }
    else return false;
  }

  /**
   * Draws the plot on a Java 2D graphics device (such as the screen or a printer).
   * @param g2 The graphics device;
   * @param drawArea The area within which the plot should be drawn;
   */
  public void draw(Graphics2D g2, Rectangle2D drawArea) {

    if (insets!=null) {
      drawArea = new Rectangle2D.Double(drawArea.getX()+insets.left,
                                        drawArea.getY()+insets.top,
                                        drawArea.getWidth()-insets.left-insets.right,
                                        drawArea.getHeight()-insets.top-insets.bottom);
    }

    // we can cast the axes because LinePlot enforces support of these interfaces
    HorizontalAxis ha = getHorizontalAxis();
    VerticalAxis va = getVerticalAxis();

    double h = ha.reserveHeight(g2, this, drawArea);
    Rectangle2D vAxisArea = va.reserveAxisArea(g2, this, drawArea, h);

    // compute the plot area
    Rectangle2D plotArea = new Rectangle2D.Double(drawArea.getX()+vAxisArea.getWidth(),
                                                  drawArea.getY(),
                                                  drawArea.getWidth()-vAxisArea.getWidth(),
                                                  drawArea.getHeight()-h);

    drawOutlineAndBackground(g2, plotArea);

    // draw the axes
    getCategoryAxis().draw(g2, drawArea, plotArea);
    getValueAxis().draw(g2, drawArea, plotArea);

    Shape originalClip=g2.getClip();
    g2.setClip(plotArea);

    // draw the lines
    java.util.List lines = getLines(plotArea);   // area should be remaining area only
    for (int i=0; i<lines.size(); i++) {
      Line l = (Line)lines.get(i);
      g2.setPaint(l.getPaint());
      g2.setStroke(l.getStroke());
      g2.draw(l.getLine());
    }

    g2.setClip(originalClip);

  }

  /**
   * Returns a short string describing the plot type;
   */
  public String getPlotType() {
    return "Line Plot";
  }

  /**
   * Returns the minimum value in the range, since this is plotted against the vertical axis for
   * BarPlot.
   */
  public Number getMinimumVerticalDataValue() {

    DataSource data = this.getChart().getDataSource();
    if (data!=null) {
      return DataSources.getMinimumRangeValue(data);
    }
    else return null;

  }

  /**
   * Returns the maximum value in either the domain or the range, whichever is displayed against
   * the vertical axis for the particular type of plot implementing this interface.
   */
  public Number getMaximumVerticalDataValue() {

    DataSource data = this.getChart().getDataSource();
    if (data!=null) {
      return DataSources.getMaximumRangeValue(data);
    }
    else return null;
  }

}