/* =======================================
 * JFreeChart : a Java Chart Class Library
 * =======================================
 * Version:         0.5.6;
 * Project Lead:    David Gilbert (david.gilbert@bigfoot.com);
 *
 * File:            StandardLegend.java
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
 * $Id: StandardLegend.java,v 1.1 2007/10/10 18:52:16 vauchers Exp $
 */

package com.jrefinery.chart;

import java.awt.*;
import java.awt.event.*;
import java.awt.font.*;
import java.awt.geom.*;
import javax.swing.*;
import com.jrefinery.chart.event.*;

/**
 * A chart legend shows the names and visual representations of the series that are plotted in a
 * chart. In the current implementation, the legend is shown to the right of the plot area.
 * In future implementations, there is likely to more flexibility regarding the placement relative
 * to the chart.
 */
public class StandardLegend extends Legend {

  /** The blank space inside the legend box. */
  protected int innerGap;

  /** The Stroke used to draw the outline of the legend. */
  protected Stroke outlineStroke;

  /** The Paint used to draw the outline of the legend. */
  protected Paint outlinePaint;

  /** The Paint used to draw the background of the legend. */
  protected Paint backgroundPaint;

  /** The Font used to display the series names. */
  protected Font seriesFont;

  /** the Paint used to display the series names. */
  protected Paint seriesPaint;

  /**
   * Default constructor: returns a StandardLegend with default appearance.
   * @param chart The chart that the legend belongs to.
   */
  public StandardLegend(JFreeChart chart) {
    this(chart, 3, 2, Color.white, new BasicStroke(), Color.gray,
         new Font("Arial", Font.PLAIN, 12), Color.black);
  }

  /**
   * Full constructor.
   */
  public StandardLegend(JFreeChart chart,
                        int outerGap, int innerGap,
                        Paint backgroundPaint,
                        Stroke outlineStroke, Paint outlinePaint,
                        Font seriesFont, Paint seriesPaint) {
    super(chart, outerGap);
    this.innerGap = innerGap;
    this.backgroundPaint = backgroundPaint;
    this.outlineStroke = outlineStroke;
    this.outlinePaint = outlinePaint;
    this.seriesFont = seriesFont;
    this.seriesPaint = seriesPaint;
  }

  /**
   * Returns the background paint for the legend.
   */
  public Paint getBackgroundPaint() {
    return this.backgroundPaint;
  }

  /**
   * Sets the background color of the legend and notifies registered listeners that the legend
   * has been modified.
   * @param paint The new background Paint;
   */
  public void setBackgroundPaint(Paint paint) {
    this.backgroundPaint = paint;
    notifyListeners(new LegendChangeEvent(this));
  }

  /**
   * Returns the outline stroke for the legend.
   */
  public Stroke getOutlineStroke() {
    return this.outlineStroke;
  }

  /**
   * Sets the outline stroke for the legend and notifies registered listeners that the legend has
   * been modified.
   * @param stroke The new outline Stroke;
   */
  public void setOutlineStroke(Stroke stroke) {
    this.outlineStroke = stroke;
    notifyListeners(new LegendChangeEvent(this));
  }

  /**
   * Returns the outline paint for the legend;
   */
  public Paint getOutlinePaint() {
    return this.outlinePaint;
  }

  /**
   * Sets the outline paint for the legend and notifies registered listeners that the legend has
   * been modified.
   * @param paint The new outline Paint;
   */
  public void setOutlinePaint(Paint paint) {
    this.outlinePaint = paint;
    notifyListeners(new LegendChangeEvent(this));
  }

  /**
   * Returns the series label font for the legend.
   */
  public Font getSeriesFont() {
    return this.seriesFont;
  }

  /**
   * Sets the series font for the legend and notifies registered listeners that the legend has
   * been modified.
   * @param font The new series Font;
   */
  public void setSeriesFont(Font font) {
    this.seriesFont = font;
    notifyListeners(new LegendChangeEvent(this));
  }

  /**
   * Returns the series paint for the legend.
   */
  public Paint getSeriesPaint() {
    return this.seriesPaint;
  }

  /**
   * Sets the series label Paint for the legend and notifies registered listeners that the legend
   * has been modified.
   * @param paint The new outline Paint;
   */
  public void setSeriesPaint(Paint paint) {
    this.seriesPaint = paint;
    notifyListeners(new LegendChangeEvent(this));
  }

  /**
   * Draws the legend on a Java 2D graphics device (such as the screen or a printer).
   * @param g2 The graphics device;
   * @param nonTitleArea The area within which the legend (and plot) should be drawn;
   * @return The area used by the legend;
   */
  public Rectangle2D draw(Graphics2D g2, Rectangle2D nonTitleArea) {

    int seriesCount = chart.getDataSource().getSeriesCount();

    // work out the widest label, and the line height and leading
    float maxWidth = 0.0f;
    FontRenderContext frc = g2.getFontRenderContext();
    LineMetrics lm = seriesFont.getLineMetrics("Sample", frc);
    for (int seriesIndex=0; seriesIndex<seriesCount; seriesIndex++) {
      String seriesName = chart.getDataSource().getSeriesName(seriesIndex);
      Rectangle2D seriesBounds = seriesFont.getStringBounds(seriesName, frc);
      if (seriesBounds.getWidth()>maxWidth) {
        maxWidth = (float)seriesBounds.getWidth();
      }
    }

    float legendWidth = 2*(outerGap+innerGap)+maxWidth+1.30f*lm.getHeight();
    float legendHeight = 2*(outerGap+innerGap)+seriesCount*lm.getHeight();
    float legendY = (float)nonTitleArea.getY()+(float)(nonTitleArea.getHeight()/2)-
                    (legendHeight/2);
    Rectangle2D legendArea = new Rectangle2D.Float((float)nonTitleArea.getMaxX()-legendWidth,
                                                   legendY, legendWidth, legendHeight);
    Rectangle2D innerLegendArea = new Rectangle2D.Double(legendArea.getX()+outerGap,
                                                        legendArea.getY()+outerGap,
                                                        legendArea.getWidth()-2*outerGap,
                                                        legendArea.getHeight()-2*outerGap);
    g2.setPaint(backgroundPaint);
    g2.fill(innerLegendArea);
    g2.setPaint(outlinePaint);
    g2.setStroke(outlineStroke);
    g2.draw(innerLegendArea);

    g2.setFont(seriesFont);
    for (int seriesIndex=0; seriesIndex<seriesCount; seriesIndex++) {
      String seriesName = chart.getDataSource().getSeriesName(seriesIndex);
      float xx = (float)(innerLegendArea.getX()+innerGap+1.15f*lm.getHeight());
      float yy = (float)(innerLegendArea.getY()+innerGap+
                        ((float)(seriesIndex+1)*lm.getHeight())-lm.getLeading()-lm.getDescent());
      g2.setPaint(seriesPaint);
      g2.drawString(seriesName, xx, yy);

      Rectangle2D box = getLegendBox(seriesIndex, seriesCount, lm.getHeight(),
                                     innerLegendArea);
      g2.setPaint(chart.getSeriesPaint(seriesIndex));
      g2.fill(box);

    }

    return legendArea;
  }

  /**
   * Returns a box that will be positioned next to the name of the specified series within the
   * legend area.  The box will be square and 65% of the height of a line.
   */
  private Rectangle2D getLegendBox(int series, int seriesCount, float textHeight,
                                   Rectangle2D innerLegendArea) {

    float boxHeightAndWidth = textHeight*0.70f;
    float xx = (float)innerLegendArea.getX()+innerGap+0.15f*textHeight;
    float yy = (float)innerLegendArea.getY()+innerGap+(series+0.15f)*textHeight;
    return new Rectangle2D.Float(xx, yy, boxHeightAndWidth, boxHeightAndWidth);

  }

}