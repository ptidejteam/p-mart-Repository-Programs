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
 * ------------------------
 * VerticalBarRenderer.java
 * ------------------------
 * (C) Copyright 2000-2002, by Simba Management Limited.
 *
 * Original Author:  David Gilbert (for Simba Management Limited);
 * Contributor(s):   Tin Luu;
 *                   Richard Atkinson;
 *
 * $Id: VerticalBarRenderer.java,v 1.1 2007/10/10 19:52:14 vauchers Exp $
 *
 * Changes
 * -------
 * 19-Oct-2001 : Version 1 (DG);
 * 22-Oct-2001 : Renamed DataSource.java --> Dataset.java etc. (DG);
 * 23-Oct-2001 : Changed intro and trail gaps on bar plots to use percentage of available space
 *               rather than a fixed number of units (DG);
 * 31-Oct-2001 : Debug for gaps (DG);
 * 15-Nov-2001 : Modified to allow for null data values (DG);
 * 13-Dec-2001 : Changed drawBar(...) method to return a Shape (that can be used for tooltips) (DG);
 * 15-Feb-2002 : Added isStacked() method to allow the plot to alter the method of finding the
 *               minimum and maximum data values (DG);
 * 14-Mar-2002 : Modified to implement the CategoryItemRenderer interface (DG);
 * 24-May-2002 : Incorporated tooltips into chart entities (DG);
 * 11-Jun-2002 : Added check for (permitted) null info object, bug and fix reported by David
 *               Basten.  Also updated Javadocs. (DG);
 * 19-Jun-2002 : Added code to draw labels on bars (TL);
 * 26-Jun-2002 : Added range axis to initialise method, and implemented our own bar clipping to
 *               avoid PRExceptions (DG);
 * 05-Aug-2002 : Small modification to drawCategoryItem method to support URLs for HTML image
 *               maps (RA);
 * 08-Aug-2002 : Applied fixed in bug id 592218 (DG);
 * 20-Aug-2002 : Updated drawRangeMarker method (DG);
 *
 */

package com.jrefinery.chart;

import java.awt.Graphics2D;
import java.awt.Font;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.Paint;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.text.NumberFormat;
import com.jrefinery.chart.entity.EntityCollection;
import com.jrefinery.chart.entity.CategoryItemEntity;
import com.jrefinery.chart.tooltips.CategoryToolTipGenerator;
import com.jrefinery.chart.tooltips.StandardCategoryToolTipGenerator;
import com.jrefinery.data.CategoryDataset;
import com.jrefinery.data.Range;

/**
 * A renderer that handles the drawing of bars for a vertical bar plot.
 */
public class VerticalBarRenderer extends BarRenderer implements CategoryItemRenderer {

    /**
     * Default constructor, creates a renderer with a standard tool tip
     * generator.
     */
    public VerticalBarRenderer() {
        this(new StandardCategoryToolTipGenerator());
    }

    /**
     * Constructs a renderer with a specific tool tip generator.
     *
     * @param toolTipGenerator The tool tip generator.
     */
    public VerticalBarRenderer(CategoryToolTipGenerator toolTipGenerator) {
        super(toolTipGenerator);
    }

    /**
     * Initialises the renderer.
     * <p>
     * This method gets called once at the start of the process of drawing a
     * chart.
     *
     * @param g2 The graphics device.
     * @param dataArea The data area.
     * @param plot The plot.
     * @param axis The range axis.
     * @param data The data.
     * @param info Optional information collection.
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
     * Draws a marker for the range axis.
     * <P>
     * A marker is a constant value, usually represented by a line.
     *
     * @param g2 The graphics device.
     * @param plot The plot.
     * @param axis The range axis.
     * @param marker The marker to be drawn.
     * @param axisDataArea The area inside the axes.
     * @param dataClipRegion The data area.
     */
    public void drawRangeMarker(Graphics2D g2,
                                CategoryPlot plot,
                                ValueAxis axis,
                                Marker marker,
                                Rectangle2D axisDataArea,
                                Shape dataClipRegion) {

        double value = marker.getValue();
        Range range = axis.getRange();

        if (!range.contains(value)) return;

        double y = axis.translateValueToJava2D(marker.getValue(), axisDataArea);
        Line2D line = new Line2D.Double(axisDataArea.getMinX(), y,
                                        axisDataArea.getMaxX(), y);
        Paint paint = marker.getOutlinePaint();
        Stroke stroke = marker.getOutlineStroke();
        g2.setPaint(paint!=null ? paint : Plot.DEFAULT_OUTLINE_PAINT);
        g2.setStroke(stroke!=null ? stroke : Plot.DEFAULT_OUTLINE_STROKE);
        g2.draw(line);

    }

    /**
     * Draws the bar for a single (series, category) data item.
     *
     * @param g2 The graphics device.
     * @param dataArea  The data area.
     * @param plot The plot.
     * @param axis The range axis.
     * @param data The data.
     * @param series The series number (zero-based index).
     * @param category The category.
     * @param categoryIndex The category number (zero-based index).
     * @param previousCategory The previous category.
     */
    public void drawCategoryItem(Graphics2D g2,
                                 Rectangle2D dataArea,
                                 CategoryPlot plot,
                                 ValueAxis axis,
                                 CategoryDataset data,
                                 int series,
                                 Object category,
                                 int categoryIndex,
                                 Object previousCategory) {

        // plot non-null values...
        Number dataValue = data.getValue(series, category);
        if (dataValue!=null) {

            // BAR X
            double rectX = dataArea.getX() + dataArea.getWidth()*plot.getIntroGapPercent();

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
                    rectX = rectX + (series*itemGapSpan/(categories*(seriesCount-1)));
                }
            }
            else {
                rectX = rectX
                        // bars+gaps completed in current category
                        + (series*itemSpan/(categories*seriesCount));
                if (seriesCount>1) {
                    rectX = rectX + (series*itemGapSpan/(categories*(seriesCount-1)));
                }
            }

            // BAR Y
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
               if (value<=this.lowerClip)
                    return; // bar is not visible
               base = this.lowerClip;
               if (value>=this.upperClip) {
                   value=this.upperClip;
               }
            }

            double transY1 = axis.translateValueToJava2D(base, dataArea);
            double transY2 = axis.translateValueToJava2D(value, dataArea);
            double rectY = Math.min(transY2, transY1);

            double rectWidth = itemWidth;
            double rectHeight = Math.abs(transY2-transY1);

            Rectangle2D bar = new Rectangle2D.Double(rectX, rectY, rectWidth, rectHeight);
            Paint itemPaint = null;
            if (!this.useCategoriesPaint) {
                itemPaint = plot.getSeriesPaint(series);
            }
            else {
                itemPaint = getCategoryPaint(categoryIndex);
            }
            g2.setPaint(itemPaint);
            g2.fill(bar);
            if (itemWidth>3) {
                g2.setStroke(plot.getSeriesStroke(series));
                g2.setPaint(plot.getSeriesOutlinePaint(series));
                g2.draw(bar);
                if (plot.getLabelsVisible()) {
                    Font labelFont = plot.getLabelFont();
                    g2.setFont(labelFont);
                    Paint paint = plot.getLabelPaint();
                    g2.setPaint(paint);
                    NumberFormat formatter = plot.getLabelFormatter();
                    String s = formatter.format(dataValue);
                    java.awt.FontMetrics fm = g2.getFontMetrics();
                    int ix = (int)((itemWidth - fm.stringWidth( s )) / 2);
                    // Center above bar
                    g2.drawString( s, (int)(rectX+ix), (int)(rectY - 5));
                }
            }

            // collect entity and tool tip information...
            if (this.info!=null) {
                EntityCollection entities = this.info.getEntityCollection();
                if (entities!=null) {
                    String tip="";
                    if (this.toolTipGenerator!=null) {
                        tip = this.toolTipGenerator.generateToolTip(data, series, category);
                    }
                    String url = null;
                    if (this.urlGenerator != null)
                        url = this.urlGenerator.generateURL(data, series, category);
                    CategoryItemEntity entity = new CategoryItemEntity(bar, tip,
                        url, series, category, categoryIndex);
                    entities.addEntity(entity);
                }
            }

        }

    }

    /**
     * Returns true, since for this renderer there are gaps between the items
     * in one category.
     *
     * @return <code>true</code>.
     */
    public boolean hasItemGaps() {
        return true;
    }

    /**
     * Returns the number of bar-widths displayed in each category.  For this
     * renderer, there is one bar per series, so we return the number of series.
     * <P>
     * This number is used to calculate the width of a single bar.
     *
     * @param data The dataset.
     *
     * @return The number of bar widths across each category.
     */
    public int barWidthsPerCategory(CategoryDataset data) {
        return data.getSeriesCount();
    }

}
