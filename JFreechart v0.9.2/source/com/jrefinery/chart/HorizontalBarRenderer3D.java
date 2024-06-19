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
 * ----------------------------
 * HorizontalBarRenderer3D.java
 * ----------------------------
 * (C) Copyright 2002, by Tin Luu and Contributors.
 *
 * Original Author:  Tin Luu (based on VerticalBarRenderer3D.java);
 * Contributor(s):   David Gilbert (for Simba Management Limited);
 *
 * $Id: HorizontalBarRenderer3D.java,v 1.1 2007/10/10 19:41:58 vauchers Exp $
 *
 * Changes
 * -------
 * 15-May-2002 : Version 1, contributed by Tin Luu based on VerticalBarRenderer3D code (DG);
 * 13-Jun-2002 : Added check to make sure marker is visible before drawing it (DG);
 * 19-Jun-2002 : Added code to draw labels on bars (TL);
 * 26-Jun-2002 : Implemented bar clipping to avoid PRExceptions (DG);
 *
 */

package com.jrefinery.chart;

import com.jrefinery.chart.CategoryPlot;
import com.jrefinery.data.CategoryDataset;
import com.jrefinery.data.Range;
import com.jrefinery.chart.tooltips.CategoryToolTipGenerator;
import com.jrefinery.chart.tooltips.CategoryToolTipGenerator;
import com.jrefinery.chart.tooltips.StandardCategoryToolTipGenerator;
import java.awt.Graphics2D;
import java.awt.Font;
import java.awt.Shape;
import java.awt.Paint;
import java.awt.geom.Rectangle2D;
import java.awt.Color;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;

/**
 * A renderer that handles the drawing of 3D bars for a horizontal bar plot.
 */
public class HorizontalBarRenderer3D extends HorizontalBarRenderer {

    /** The default 3D effect. */
    private static final double DEFAULT_EFFECT3D = 10.0;

    /** The size of the 3D effect (in pixels). */
    protected double effect3d;

    /**
     * Default constructor.
     */
    public HorizontalBarRenderer3D() {
        this(new StandardCategoryToolTipGenerator(), DEFAULT_EFFECT3D);
    }

    /**
     * Constructs a new renderer.
     */
    public HorizontalBarRenderer3D(CategoryToolTipGenerator toolTipGenerator, double effect3d) {
        super(toolTipGenerator);
        this.effect3d = effect3d;
    }

    /**
     * Returns true to indicate that this renderer does allow for gaps between items.
     */
    public boolean hasItemGaps() {
        return true;
    }

    /**
     *  Returns the number of bar widths in each category (used to calculate the width of a
     *  single bar).
     *
     *  @param data The data.
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
     */
    public Rectangle2D getAxisArea(Rectangle2D plotArea) {
        return new Rectangle2D.Double(plotArea.getX(),
                                      plotArea.getY()+this.effect3d,
                                      plotArea.getWidth()-this.effect3d,
                                      plotArea.getHeight()-this.effect3d);
    }

    /**
     * Returns the clip region...usually returns the dataArea, but some charts (e.g. 3D) have
     * non rectangular clip regions.
     *
     * @param dataArea The data area.
     */
    public Shape getDataClipRegion(Rectangle2D dataArea) {

        GeneralPath result = new GeneralPath();
        result.moveTo((float)dataArea.getX(), (float)(dataArea.getMinY()+this.effect3d));
        result.lineTo((float)(dataArea.getX()+this.effect3d), (float)dataArea.getMinY());
        result.lineTo((float)dataArea.getMaxX(), (float)dataArea.getMinY());
        result.lineTo((float)dataArea.getMaxX(), (float)(dataArea.getMaxY()-this.effect3d));
        result.lineTo((float)(dataArea.getMaxX()-this.effect3d), (float)dataArea.getMaxY());
        result.lineTo((float)dataArea.getX(), (float)dataArea.getMaxY());
        result.closePath();

        return result;
    }

    /**
     * Draws the background for the plot.
     * <P>
     * For most charts, the axisDataArea and the dataClipArea are the same.  One case where they
     * are different is the 3D-effect bar charts...here the data clip area extends above and to
     * the right of the axisDataArea.
     *
     * @param g2 The graphics device.
     * @param plot The plot.
     * @param axisDataArea The area inside the axes.
     * @param dataClipArea The data clip area.
     */
    public void drawPlotBackground(Graphics2D g2,
                                   CategoryPlot plot,
                                   Rectangle2D axisDataArea, Shape dataClipRegion) {

        super.drawPlotBackground(g2, plot, axisDataArea, dataClipRegion);

        double x1 = axisDataArea.getX();
        double x2 = x1 + this.effect3d;
        double x3 = axisDataArea.getMaxX()+this.effect3d;

        double y1 = axisDataArea.getMaxY();
        double y2 = y1 - this.effect3d;
        double y3 = axisDataArea.getMinY()-this.effect3d;

        g2.setPaint(plot.getOutlinePaint());
        Line2D line = new Line2D.Double(x1, y1, x2, y2);
        g2.draw(line);
        line.setLine(x2, y2, x2, y3);
        g2.draw(line);
        line.setLine(x2, y2, x3, y2);
        g2.draw(line);

    }

    /**
     * Draws a vertical line across the chart to represent the marker.
     *
     * @param g2 The graphics device.
     * @param plot The plot.
     * @param axis The value axis.
     * @param marker The marker line.
     * @param axisDataArea The axis data area.
     * @param dataClipRegion The data clip region.
     */
    public void drawRangeMarker(Graphics2D g2,
                                CategoryPlot plot, ValueAxis axis, Marker marker,
                                Rectangle2D axisDataArea, Shape dataClipRegion) {

        double value = marker.getValue();
        Range range = axis.getRange();
        if (!range.contains(value)) return;

        float x = (float)axis.translateValueToJava2D(marker.getValue(), axisDataArea);
        float y = (float)axisDataArea.getMaxY();
        GeneralPath path = new GeneralPath();
        path.moveTo(x, y);
        path.lineTo((float)(x+effect3d), y-(float)effect3d);
        path.lineTo((float)(x+effect3d), (float)(axisDataArea.getMinY()-effect3d));
        path.lineTo(x, (float)axisDataArea.getMinY());
        path.closePath();
        g2.setPaint(marker.getPaint());
        g2.fill(path);
        g2.setPaint(marker.getOutlinePaint());
        g2.draw(path);

    }

    /**
     * Draws the bar for a single (series, category) data item.
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
                                 CategoryPlot plot,
                                 ValueAxis axis,
                                 CategoryDataset data, int series,
                                 Object category, int categoryIndex,
                                 Object previousCategory) {

        // first check the value we are plotting...
        Number dataValue = data.getValue(series, category);
        if (dataValue!=null) {

            // X
            double value = dataValue.doubleValue();
            double base = 0.0;

            if (this.upperClip<=0.0) {  // cases 1, 2, 3 and 4
                if (value>=this.upperClip) return; // bar is not visible
                base = this.upperClip;
                if (value<=this.lowerClip) {
                    value = this.lowerClip;
                }
            }
            else if (this.lowerClip<=0.0) { // cases 5, 6, 7 and 8
               if (value>=this.upperClip) value=this.upperClip;
               else if (value<=this.lowerClip) value=this.lowerClip;
            }
            else { // cases 9, 10, 11 and 12
               if (value<=this.lowerClip) return; // bar is not visible
               base = this.lowerClip;
               if (value>=this.upperClip) {
                   value=this.upperClip;
               }
            }

            double transX1 = axis.translateValueToJava2D(base, dataArea);
            double transX2 = axis.translateValueToJava2D(value, dataArea);
            double rectX = Math.min(transX1, transX2);
            double rectWidth = Math.abs(transX2-transX1);

            // Y
            double rectY = dataArea.getY() + dataArea.getHeight()*plot.getIntroGapPercent();

            int categories = data.getCategoryCount();
            int seriesCount = data.getSeriesCount();
            if (categories>1) {
                rectY = rectY
                        // bars in completed categories
                        + (categoryIndex*categorySpan/categories)
                        // gaps between completed categories
                        + (categoryIndex*categoryGapSpan/(categories-1))
                        // bars+gaps completed in current category
                        + (series*itemSpan/(categories*seriesCount));
                if (seriesCount>1) {
                    rectY = rectY
                            + (series*itemGapSpan/(categories*(seriesCount-1)));
                }
            }
            else {
                rectY = rectY
                        // bars+gaps completed in current category;
                        + (series*itemSpan/(categories*seriesCount));
                if (seriesCount>1) {
                    rectY = rectY
                            + (series*itemGapSpan/(categories*(seriesCount-1)));
                }
            }

            // HEIGHT
            double rectHeight = itemWidth;

            // DRAW THE BAR...
            Rectangle2D bar = new Rectangle2D.Double(rectX, rectY, rectWidth, rectHeight);
            Paint seriesPaint = plot.getSeriesPaint(series);
            g2.setPaint(seriesPaint);
            g2.fill(bar);

            GeneralPath bar3dRight = null;
            GeneralPath bar3dTop = null;
            double effect3d = 0.00;
            HorizontalAxis hAxis = (HorizontalAxis)plot.getRangeAxis();
            if (rectWidth != 0 && hAxis instanceof HorizontalNumberAxis3D) {
                effect3d = ((HorizontalNumberAxis3D) hAxis).getEffect3d();
                bar3dRight = new GeneralPath();

                bar3dRight.moveTo((float)(rectX+rectWidth), (float)rectY);
                bar3dRight.lineTo((float)(rectX+rectWidth), (float)(rectY+rectHeight));
                bar3dRight.lineTo((float)(rectX+rectWidth+effect3d),
                                  (float)(rectY+rectHeight-effect3d));
                bar3dRight.lineTo((float)(rectX+rectWidth+effect3d), (float)(rectY-effect3d));

                if (seriesPaint instanceof Color) {
                    g2.setPaint( ((Color) seriesPaint)/*.darker()*/);
                }
                g2.fill(bar3dRight);

                bar3dTop = new GeneralPath();

                bar3dTop.moveTo( (float) rectX, (float) rectY);
                bar3dTop.lineTo((float) (rectX+effect3d), (float) (rectY-effect3d));
                bar3dTop.lineTo((float) (rectX+rectWidth+effect3d), (float) (rectY-effect3d));
                bar3dTop.lineTo((float) (rectX+rectWidth), (float) (rectY) );

                if (seriesPaint instanceof Color) {
                    g2.setPaint( ((Color) seriesPaint).darker()); //.brighter());
                }
                g2.fill(bar3dTop);
            }

            if (itemWidth>BAR_OUTLINE_WIDTH_THRESHOLD) {
                g2.setStroke(plot.getSeriesStroke(series));
                g2.setPaint(plot.getSeriesOutlinePaint(series));
                g2.draw(bar);
                if (bar3dRight != null) {
                    g2.draw(bar3dRight);
                }
                if (bar3dTop != null) {
                    g2.draw(bar3dTop);
                }

                if (plot.getLabelsVisible()) {
                    Font labelFont = plot.getLabelFont();
                    g2.setFont(labelFont);
                    Paint paint = plot.getLabelPaint();
                    g2.setPaint(paint);
                    g2.drawString(String.valueOf(dataValue), (int)(rectX + rectWidth * 0.90),
                                                             (int)rectY - 15);
                }
            }

        }

    }

}
