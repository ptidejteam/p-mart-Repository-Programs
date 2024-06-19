/* =======================================
 * JFreeChart : a Java Chart Class Library
 * =======================================
 * Version:         0.5.6;
 * Project Lead:    David Gilbert (david.gilbert@bigfoot.com);
 *
 * File:            HiLowPlot.java
 * Author:          Andrzej Porebski;
 * Contributor(s):  David Gilbert;
 *
 * (C) Copyright 2000, by Andrzej Porebski;
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
 * $Id: HiLowPlot.java,v 1.1 2007/10/10 18:52:17 vauchers Exp $
 *
 */

package com.jrefinery.chart;

import java.awt.*;
import java.awt.geom.*;
import java.util.*;

import com.jrefinery.chart.event.PlotChangeEvent;

/**
 * A Plot that displays data in the form of an XY plot, using data from any class that
 * implements the XYDataSource interface.
 * Constraints:
 * Each series in the XYDataSource must contain element count divisible by 4.
 * This is enforced so that the data can be fed in form of quadruplets with
 * each quadruplet specifying high, low, open, close values.
 *
 * @see Plot
 * @see XYDataSource
 */
public class HiLowPlot extends Plot implements HorizontalValuePlot, VerticalValuePlot {

  /**
   * If true, open and close marks are drawn.
   */
  private boolean drawOpenClose = true;

  /**
   * Standard constructor: returns an HiLowPlot with attributes specified by the caller.
   *
   * @param chart The chart that the plot belongs to;
   * @param horizontal The horizontal axis;
   * @param vertical The vertical axis.
   */
  public HiLowPlot(JFreeChart chart, Axis horizontal, Axis vertical)
    throws AxisNotCompatibleException
    {
      super(chart, horizontal, vertical);
    }

  /**
   * A convenience method that returns the data source for the plot, cast as an XYDataSource.
   */
  public XYDataSource getDataSource()
    {
      return (XYDataSource)chart.getDataSource();
    }

  /**
   * Returns true if this plot draws open/close marks.
   */
  public boolean getDrawOpenCloseTicks()
    {
      return drawOpenClose;
    }

  /**
   * Sets the property that tells this plot to draw open/close marks.
   * Once the value of this property is set, all listeners are notified
   * of the change.
   */
  public void setDrawOpenCloseTicks(boolean draw)
    {
      if (drawOpenClose != draw)
        {
          drawOpenClose = draw;
          notifyListeners(new PlotChangeEvent(this));
        }
    }

  /**
   * A convenience method that returns a reference to the horizontal axis cast as a
   * HorizontalValueAxis.
   */
  public ValueAxis getHorizontalValueAxis()
    {
      return (ValueAxis)horizontalAxis;
    }

  /**
   * A convenience method that returns a reference to the vertical axis cast as a
   * VerticalNumberAxis.
   */
  public ValueAxis getVerticalValueAxis()
    {
      return (ValueAxis)verticalAxis;
    }

  /**
   * Returns a list of HighLow elements that will fit inside the specified area.
   */
  private java.util.List getLines(Rectangle2D plotArea)
    {
      ArrayList elements = new ArrayList();
      XYDataSource data = getDataSource();
      if (data != null)
        {
          int seriesCount = data.getSeriesCount();

          for (int series=0; series<seriesCount; series++)
            {
              int itemCount = data.getItemCount(series);
              if (itemCount % 4 != 0)
                continue;

              for(int itemIndex = 0; itemIndex < itemCount; itemIndex+=4)
                {
                  Number x = data.getXValue(series, itemIndex);
                  Number yHigh  = data.getYValue(series,itemIndex);
                  Number yLow   = data.getYValue(series,itemIndex+1);
                  Number yOpen  = data.getYValue(series,itemIndex+2);
                  Number yClose = data.getYValue(series,itemIndex+3);

                  double xx = getHorizontalValueAxis().translatedValue(x, plotArea);
                  double yyHigh = getVerticalValueAxis().translatedValue(yHigh, plotArea);
                  double yyLow = getVerticalValueAxis().translatedValue(yLow, plotArea);
                  double yyOpen = getVerticalValueAxis().translatedValue(yOpen, plotArea);
                  double yyClose = getVerticalValueAxis().translatedValue(yClose, plotArea);

                  Paint p = chart.getSeriesPaint(series);
                  Stroke s = chart.getSeriesStroke(series);

                  elements.add(new HiLow(xx,yyHigh,yyLow,yyOpen,yyClose,s,p));
                }
            }
        }
      return elements;
    }

  /**
   * Checks the compatibility of a horizontal axis, returning true if the axis is compatible with
   * the plot, and false otherwise.
   * @param axis The horizontal axis.
   */
  public boolean isCompatibleHorizontalAxis(Axis axis)
    {
      if (axis instanceof HorizontalNumberAxis)
        {
          return true;
        }
      else if (axis instanceof HorizontalDateAxis)
        {
          return true;
        }
      else
        return false;
    }

  /**
   * Checks the compatibility of a vertical axis, returning true
   * if the axis is compatible with the plot, and false otherwise.
   * The vertical axis for this plot must be an instance of
   * VerticalNumberAxis.
   * @param axis The vertical axis.
   */
  public boolean isCompatibleVerticalAxis(Axis axis)
    {
      if (axis instanceof VerticalNumberAxis)
        return true;
      else
        return false;
    }

  /**
   * Draws the plot on a Java 2D graphics device (such as the screen or a printer).
   * @param g2 The graphics device;
   * @param drawArea The area within which the plot should be drawn.
   */
  public void draw(Graphics2D g2, Rectangle2D drawArea)
    {

      if (insets!=null)
        {
          drawArea = new Rectangle2D.Double(drawArea.getX()+insets.left,
                                            drawArea.getY()+insets.top,
                                            drawArea.getWidth()-insets.left-insets.right,
                                            drawArea.getHeight()-insets.top-insets.bottom);
        }

      // we can cast the axes because HiLowPlot enforces support of these interfaces
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

      this.horizontalAxis.draw(g2, drawArea, plotArea);
      this.verticalAxis.draw(g2, drawArea, plotArea);

      Shape originalClip = g2.getClip();
      g2.setClip(plotArea);

      java.util.List lines = getLines(plotArea);   // area should be remaining area only
      for (int i=0; i<lines.size(); i++)
        {
          HiLow l = (HiLow)lines.get(i);
          g2.setPaint(l.getPaint());
          g2.setStroke(l.getStroke());
          g2.draw(l.getLine());
          if (getDrawOpenCloseTicks())
            {
              g2.draw(l.getOpenTickLine());
              g2.draw(l.getCloseTickLine());
            }
        }

      g2.setClip(originalClip);
    }

  /**
   * Returns the plot type as a string. This implementation returns "HiLow Plot".
   */
  public String getPlotType()
    {
      return "HiLow Plot";
    }

  /**
   * Returns the minimum value in the domain, since this is plotted against the horizontal axis for
   * an HiLowPlot.
   */
  public Number getMinimumHorizontalDataValue()
    {

      DataSource data = this.getChart().getDataSource();
      if (data!=null)
        {
          return DataSources.getMinimumDomainValue(data);
        }
      else return null;
    }

  /**
   * Returns the maximum value in the domain, since this is plotted
   * against the horizontal axis for
   * an HiLowPlot.
   */
  public Number getMaximumHorizontalDataValue()
    {

      DataSource data = this.getChart().getDataSource();
      if (data!=null)
        {
          return DataSources.getMaximumDomainValue(data);
        }
      else
        return null;
    }

  /**
   * Returns the minimum value in the range, since this is plotted against the vertical axis for
   * an HiLowPlot.
   */
  public Number getMinimumVerticalDataValue()
    {

      DataSource data = this.getChart().getDataSource();
      if (data!=null)
        {
          return DataSources.getMinimumRangeValue(data);
        }
      else return null;
    }

  /**
   * Returns the maximum value in the range, since this is plotted against the vertical axis for
   * an HiLowPlot.
   */
  public Number getMaximumVerticalDataValue()
    {
      DataSource data = this.getChart().getDataSource();
      if (data!=null)
        {
          return DataSources.getMaximumRangeValue(data);
        }
      else
        return null;
    }
}