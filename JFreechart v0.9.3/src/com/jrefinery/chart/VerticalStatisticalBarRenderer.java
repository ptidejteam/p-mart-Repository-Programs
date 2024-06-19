/* ============================================
 * JFreeChart : a free Java chart class library
 * ============================================
 *
 * Project Info:  http://www.object-refinery.com/jfreechart/index.html
 * Project Lead:  David Gilbert (david.gilbert@object-refinery.com);
 *
 * (C) Copyright 2000-2002, by Simba Management Limited and Contributors.
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
 * -----------------------------------
 * VerticalStatisticalBarRenderer.java
 * -----------------------------------
 * (C) Copyright 2002, by Pascal Collet.
 *
 * Original Author:  Pascal Collet;
 * Contributor(s):   -;
 *
 * $Id: VerticalStatisticalBarRenderer.java,v 1.1 2007/10/10 19:52:15 vauchers Exp $
 *
 * Changes
 * -------
 * 21-Aug-2002 : Version 1, contributed by Pascal Collet (DG);
 *
 */

package com.jrefinery.chart;

import com.jrefinery.chart.*;

import com.jrefinery.chart.tooltips.CategoryToolTipGenerator;
import com.jrefinery.chart.tooltips.StandardCategoryToolTipGenerator;
import com.jrefinery.data.CategoryDataset;
import com.jrefinery.data.StatisticalCategoryDataset;
import com.jrefinery.ui.RefineryUtilities;

import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.Paint;
import java.awt.font.FontRenderContext;
import java.awt.font.LineMetrics;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.text.NumberFormat;

/**
 * A renderer that handles the drawing of bars for a vertical bar plot where
 * each bar has a mean value and a standard deviation vertical line.
 *
 */
public class VerticalStatisticalBarRenderer extends BarRenderer implements CategoryItemRenderer {

  /**
   * Default constructor
   */
  public VerticalStatisticalBarRenderer() {
    this(new StandardCategoryToolTipGenerator());
  }

  /**
   * Constructor with tooltip generator
   *
   * @param toolTipGenerator the tooltip generator
   */
  public VerticalStatisticalBarRenderer(CategoryToolTipGenerator toolTipGenerator) {
    super(toolTipGenerator);
  }

  /**
   * Initialises the renderer.
   * <p>
   * This method gets called once at the start of the process of drawing a chart.
   *
   * @param g2 The graphics device.
   * @param dataArea The area in which the data is to be plotted.
   * @param plot The plot.
   * @param axis The axis.
   * @param data The data.
   * @param info Collects chart rendering information for return to caller.
   *
   */
  public void initialise(Graphics2D g2,
                         Rectangle2D dataArea,
                         CategoryPlot plot,
                         ValueAxis axis,
                         CategoryDataset data,
                         ChartRenderingInfo info) {

    super.initialise(g2, dataArea, plot, axis, data, info);
    this.calculateCategoryAndItemSpans(g2, dataArea, plot, data, dataArea.getWidth());

  }


  /**
   * Draws a line (or some other marker) to indicate a certain value on the range axis.
   *
   * @param g2 The graphics device.
   * @param plot The plot.
   * @param axis The value axis.
   * @param marker The marker.
   * @param axisDataArea The area defined by the axes.
   * @param dataClipRegion The data clip region.
   */
  public void drawRangeMarker(Graphics2D g2,
                              CategoryPlot plot, ValueAxis axis, Marker marker,
                              Rectangle2D axisDataArea, Shape dataClipRegion) {

    double y = axis.translateValueToJava2D(marker.getValue(), axisDataArea);
    Line2D line = new Line2D.Double(axisDataArea.getMinX(), y,
                                    axisDataArea.getMaxX(), y);
    g2.setPaint(marker.getOutlinePaint());
    g2.draw(line);

  }

  /**
   * Draws the bar with its standard deviation line range for a single
   * (series, category) data item.
   *
   * @param g2 The graphics device.
   * @param dataArea The data area.
   * @param plot The plot.
   * @param axis The range axis.
   * @param data The data.
   * @param series The series number (zero-based index).
   * @param category The category.
   * @param categoryIndex The category number (zero-based index).
   * @param previousCategory  The previous category.
   */
  public void drawCategoryItem(Graphics2D g2, Rectangle2D dataArea,
                               CategoryPlot plot, ValueAxis axis,
                               CategoryDataset data, int series, Object category,
                               int categoryIndex, Object previousCategory) {


    // defensive check
    if(!(data instanceof StatisticalCategoryDataset)) {
      throw new IllegalArgumentException("VerticalStatisticalBarRenderer::drawCategoryItem() - the data"
          +" should be of type StatisticalCategoryDataSet only");
    }
    StatisticalCategoryDataset statData = (StatisticalCategoryDataset)data;

    // BAR X
    double rectX = dataArea.getX()+dataArea.getWidth()*plot.getIntroGapPercent();

    int categories = data.getCategoryCount();
    int seriesCount = data.getSeriesCount();
    if (categories>1) {
      rectX = rectX
      // bars in completed categories
            + categoryIndex*(categorySpan/categories)
            // gaps between completed categories
            + (categoryIndex*(categoryGapSpan/(categories-1))
            // bars+gaps completed in current category
            + (series*itemSpan/(categories*seriesCount)));
      if (seriesCount>1) {
        rectX = rectX
              + (series*itemGapSpan/(categories*(seriesCount-1)));
      }
    }
    else {
      rectX = rectX
      // bars+gaps completed in current category
            + (series*itemSpan/(categories*seriesCount));
      if (seriesCount>1) {
        rectX = rectX
              + (series*itemGapSpan/(categories*(seriesCount-1)));
      }
    }

    // BAR Y
    Number meanValue = statData.getMeanValue(series, category);
    double translatedValue = axis.translateValueToJava2D(meanValue.doubleValue(), dataArea);
    double rectY = Math.min(this.zeroInJava2D, translatedValue);

    double rectWidth = itemWidth;
    double rectHeight = Math.abs(translatedValue-this.zeroInJava2D);

    Rectangle2D bar = new Rectangle2D.Double(rectX, rectY, rectWidth, rectHeight);
    Paint seriesPaint = plot.getSeriesPaint(series);
    g2.setPaint(seriesPaint);
    g2.fill(bar);
    if (itemWidth>3) {
      g2.setStroke(plot.getSeriesStroke(series));
      g2.setPaint(plot.getSeriesOutlinePaint(series));
      g2.draw(bar);
    }

    // Standard deviation lines
    double valueDelta = statData.getStdDevValue(series,category).doubleValue();
    double highVal = axis.translateValueToJava2D(meanValue.doubleValue()+valueDelta, dataArea);
    double lowVal = axis.translateValueToJava2D(meanValue.doubleValue()-valueDelta, dataArea);

    Line2D line = null;
    line = new Line2D.Double(rectX+rectWidth/2.0d, lowVal,
                             rectX+rectWidth/2.0d, highVal);
    g2.draw(line);
    line = new Line2D.Double(rectX+rectWidth/2.0d-5.0d, highVal,
                             rectX+rectWidth/2.0d+5.0d, highVal);
    g2.draw(line);
    line = new Line2D.Double(rectX+rectWidth/2.0d-5.0d, lowVal,
                             rectX+rectWidth/2.0d+5.0d, lowVal);
    g2.draw(line);
  }


  /**
   * Returns true, since for this renderer there are gaps between the items
   * in one category.
   */
  public boolean hasItemGaps() {
    return true;
  }

  /**
   * Returns the number of bar-widths displayed in each category.  For this
   * renderer, there is one bar per series, so we return the number of
   * series.
   */
  public int barWidthsPerCategory(CategoryDataset data) {
    return data.getSeriesCount();
  }


  /**
   * Returns the area that the axes (and date) must fit into.
   * <P>
   * Often this is the same as the plotArea, but sometimes a smaller region should be used
   * (for example, the 3D charts require the axes to use less space in order to leave room
   * for the 'depth' part of the chart).
   *
   * @param plotArea The plot area.
   */
  public Rectangle2D getAxisArea(Rectangle2D plotArea) {
    return plotArea;
  }


}
