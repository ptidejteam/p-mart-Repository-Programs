/* =======================================
 * JFreeChart : a Java Chart Class Library
 * =======================================
 *
 * Project Info:  http://www.jrefinery.com/jfreechart;
 * Project Lead:  David Gilbert (david.gilbert@jrefinery.com);
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
 * -------------------------------
 * CombinedVerticalNumberAxis.java
 * -------------------------------
 * $Id: CombinedVerticalNumberAxis.java,v 1.1 2007/10/10 18:54:39 vauchers Exp $
 *
 * Original Author:  Bill Kelemen;
 * Contributor(s):   -;
 *
 * (C) Copyright 2001, Bill Kelemen;
 *
 * Changes:
 * --------
 * 06-Dec-2001 : Version 1 (BK);
 *
 */

package com.jrefinery.chart;

import java.awt.*;
import java.awt.font.*;
import java.awt.geom.*;
import java.util.*;

/**
 * A combined vertical number axis combines one or more VerticalNumberAxes and
 * aligns them for use in a CombinedPlot. This is needed to align properly all
 * charts so that all vertical axis have the same width.
 *
 * @see CombinedPlot
 * @author Bill Kelemen (bill@kelemen-usa.com)
 */
public class CombinedVerticalNumberAxis extends VerticalNumberAxis implements CombinableAxis {

  private VerticalNumberAxis axis = null;

  private boolean visible = true;

  // to assure all combined axis use the same width
  private double reserveWidth = 0;

  /**
   * Constructs a visible combined vertical number axis.
   * @param axis Parent VerticalNumberAxis to take as reference.
   */
  public CombinedVerticalNumberAxis(VerticalNumberAxis axis) {
    this(axis, true);
  }

  /**
   * Constructs a combined vertical number axis.
   * @param axis Parent VerticalNumberAxis to take as reference.
   * @param visible Is this axis visible?
   */
  public CombinedVerticalNumberAxis(VerticalNumberAxis axis, boolean visible) {
    super(axis.getLabel(),
          axis.getLabelFont(),
          axis.getLabelPaint(),
          axis.getLabelInsets(),
          axis.isLabelDrawnVertical(),
          axis.isTickLabelsVisible(),
          axis.getTickLabelFont(),
          axis.getTickLabelPaint(),
          axis.getTickLabelInsets(),
          axis.isTickMarksVisible(),
          axis.getTickMarkStroke(),
          axis.isAutoRange(),
          axis.autoRangeIncludesZero(),
          axis.getAutoRangeMinimumSize(),
          axis.getMinimumAxisValue(),
          axis.getMaximumAxisValue(),
          axis.isAutoTickUnit(),
          axis.getTickUnit(),
          axis.isShowGridLines(),
          axis.getGridStroke(),
          axis.getGridPaint());
    this.axis = axis;
    this.visible = visible;
  }

  //////////////////////////////////////////////////////////////////////////////
  // Methods from VerticalNumberAxis
  //////////////////////////////////////////////////////////////////////////////

  /**
   * Draws the plot on a Java 2D graphics device (such as the screen or a printer).
   * @param g2 The graphics device;
   * @param drawArea The area within which the chart should be drawn.
   * @param plotArea The area within which the plot should be drawn (a subset of the drawArea).
   */
  public void draw(Graphics2D g2, Rectangle2D drawArea, Rectangle2D plotArea) {
    if (visible) {
      axis.draw(g2, drawArea, plotArea);
    } else if (showGridLines) {
      refreshTicks(g2, drawArea, plotArea);
      g2.setStroke(gridStroke);
      g2.setPaint(gridPaint);
      double xx = plotArea.getX();
      Iterator iterator = ticks.iterator();
      while (iterator.hasNext()) {
        Tick tick = (Tick)iterator.next();
        float yy = (float)this.translatedValue(tick.getNumericalValue(), plotArea);
        Line2D gridline = new Line2D.Double(xx, yy, plotArea.getMaxX(), yy);
        g2.draw(gridline);
      }
    }
  }

  /**
   * The CombinedPlot will calculate the maximim of all reserveWidth or reserveHeight
   * depending on the type of CombinedPlot and inform all CombinedXXXXXAxis to store
   * this value.
   * @param dimension If the axis is vertical, this is width. If axis is
   *        horizontal, then this is height
   */
  public void setReserveDimension(double dimension) {
    this.reserveWidth = dimension;
  }

  /**
   * Returns the width required to draw the biggest axis of all the combined
   * vertical axis in the specified draw area. If the width was set via
   * setReserveWidth, then this value is returned instead of a calculation.
   *
   * @param g2 The graphics device;
   * @param plot A reference to the plot;
   * @param drawArea The area within which the plot should be drawn.
   */
  public double reserveWidth(Graphics2D g2, Plot plot, Rectangle2D drawArea) {
    if (!visible) {
      return 0;
    } else if (reserveWidth > 0) {
      return reserveWidth;
    } else {
      return axis.reserveWidth(g2, plot, drawArea);
    }
  }

  /**
   * Returns area in which the axis will be displayed.
   * @param g2 The graphics device;
   * @param plot A reference to the plot;
   * @param drawArea The area in which the plot and axes should be drawn;
   * @param reservedHeight The height reserved for the horizontal axis;
   */
  public Rectangle2D reserveAxisArea(Graphics2D g2, Plot plot, Rectangle2D drawArea,
                                     double reservedHeight) {
    return new Rectangle2D.Double(drawArea.getX(),
                                  drawArea.getY(),
                                  reserveWidth(g2, plot, drawArea),
                                  drawArea.getHeight()-reservedHeight);
  }

  //////////////////////////////////////////////////////////////////////////////
  // From CombinedAxis
  //////////////////////////////////////////////////////////////////////////////

  /**
   * Returns our parent axis.
   */
  public Axis getParentAxis() {
    return axis;
  }

  /**
   * Returns the AxisRange (min/max) of our Axis
   */
  public AxisRange getRange() {
    autoAdjustRange();
    return (new NumberAxisRange(getMinimumAxisValue(), getMaximumAxisValue()));
  }

  /**
   * Sets our AxisRange (min/max). This is done after a CombinedPlot has
   * has calculated the overall range of all CombinedAxis that share the same
   * Axis for all Plots. This makes all plots display the complete range of
   * their Datasets.
   */
  public void setRange(AxisRange range) {
    setAutoRange(false);
    setMinimumAxisValue((Number)range.getMin());
    setMaximumAxisValue((Number)range.getMax());
    if (visible) {
      VerticalNumberAxis axis = (VerticalNumberAxis)getParentAxis();
      axis.setAutoRange(false);
      axis.setMinimumAxisValue((Number)range.getMin());
      axis.setMaximumAxisValue((Number)range.getMax());
    }
  }

  /**
   * Sets the visible flag on or off for this combined axis. A visible axis will
   * display the axis title, ticks and legend depending on the parent's
   * attributes. An invisible axis will not display anything. If the invisible
   * axis isContainer(), then it occupies space on the graphic device.
   */
  public void setVisible(boolean flag) {
    visible = flag;
  }

  /**
   * Is this axis visible? Is is drawn?
   */
  public boolean isVisible() {
    return visible;
  }

}