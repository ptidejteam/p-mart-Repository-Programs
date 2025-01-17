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
 * --------------------------
 * HorizontalBarRenderer.java
 * --------------------------
 * (C) Copyright 2001, 2002, by Simba Management Limited.
 *
 * Original Author:  David Gilbert (for Simba Management Limited);
 * Contributor(s):   Tin Luu;
 *                   Richard Atkinson;
 *
 * $Id: HorizontalBarRenderer.java,v 1.1 2007/10/10 19:52:15 vauchers Exp $
 *
 * Changes
 * -------
 * 22-Oct-2001 : Version 1 (DG);
 *               Renamed DataSource.java --> Dataset.java etc. (DG);
 * 23-Oct-2001 : Changed intro and trail gaps on bar plots to use percentage of available space
 *               rather than a fixed number of units (DG);
 * 31-Oct-2001 : Debug for gaps (DG);
 * 15-Nov-2001 : Modified to allow for null values (DG);
 * 13-Dec-2001 : Changed drawBar(...) method to return a Shape (that can be used for tooltips) (DG);
 * 16-Jan-2002 : Updated Javadoc comments (DG);
 * 15-Feb-2002 : Added isStacked() method to allow the plot to alter the method of finding the
 *               minimum and maximum data values (DG);
 * 14-Mar-2002 : Modified this class to implement the CategoryItemRenderer interface (DG);
 * 24-May-2002 : Incorporated tooltips into chart entities (DG);
 * 13-Jun-2002 : Added check to make sure marker is visible before drawing it (DG);
 * 19-Jun-2002 : Added code to draw labels on bars (TL);
 * 26-Jun-2002 : Added axis to initialise method, and implemented bar clipping to avoid
 *               PRExceptions (DG);
 * 05-Aug-2002 : Small modification to drawCategoryItem method to support URLs for HTML image
 *               maps (RA);
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
import com.jrefinery.chart.CategoryPlot;
import com.jrefinery.chart.entity.EntityCollection;
import com.jrefinery.chart.entity.CategoryItemEntity;
import com.jrefinery.chart.tooltips.CategoryToolTipGenerator;
import com.jrefinery.chart.tooltips.StandardCategoryToolTipGenerator;
import com.jrefinery.data.CategoryDataset;
import com.jrefinery.data.Range;

/**
 * A renderer that handles the drawing of bars for a horizontal bar plot.
 */
public class HorizontalBarRenderer extends BarRenderer implements CategoryItemRenderer {

    /**
     * Constructs a new renderer.  A default tool tip generator is used.
     */
    public HorizontalBarRenderer() {
        this(new StandardCategoryToolTipGenerator());
    }

    /**
     * Constructs a new renderer with a specific tool tip generator.
     *
     * @param toolTipGenerator  The tool tip generator.
     */
    public HorizontalBarRenderer(CategoryToolTipGenerator toolTipGenerator) {
        super(toolTipGenerator);
    }

    /**
     * Initialises the renderer.
     * <p>
     * This method gets called once at the start of the process of drawing a
     * chart.
     *
     * @param g2  The graphics device.
     * @param dataArea  The area in which the data is to be plotted.
     * @param plot  The plot.
     * @param axis  The value axis.
     * @param data  The data.
     * @param info  Collects chart rendering information for return to caller.
     *
     */
    public void initialise(Graphics2D g2,
                           Rectangle2D dataArea,
                           CategoryPlot plot,
                           ValueAxis axis,
                           CategoryDataset data,
                           ChartRenderingInfo info) {

        super.initialise(g2, dataArea, plot, axis, data, info);
        this.calculateCategoryAndItemSpans(g2, dataArea, plot, data,
            dataArea.getHeight());

    }

    /**
     * Draws a vertical line across the chart to represent the marker.
     *
     * @param g2  The graphics device.
     * @param plot  The plot.
     * @param axis  The value axis.
     * @param marker  The marker line.
     * @param axisDataArea  The axis data area.
     * @param dataClipRegion  The data clip region.
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

        double x = axis.translateValueToJava2D(marker.getValue(), axisDataArea);
        Line2D line = new Line2D.Double(x, axisDataArea.getMinY(),
                                        x, axisDataArea.getMaxY());
        Paint paint = marker.getOutlinePaint();
        Stroke stroke = marker.getOutlineStroke();
        g2.setPaint(paint!=null ? paint : Plot.DEFAULT_OUTLINE_PAINT);
        g2.setStroke(stroke!=null ? stroke : Plot.DEFAULT_OUTLINE_STROKE);
        g2.draw(line);

    }

    /**
     * Draws the bar for a single (series, category) data item.
     *
     * @param g2  The graphics device.
     * @param dataArea  The data area.
     * @param plot  The plot.
     * @param axis  The range axis.
     * @param data  The data.
     * @param series  The series number (zero-based index).
     * @param category  The category.
     * @param categoryIndex  The category number (zero-based index).
     * @param previousCategory  The previous category.
     */
    public void drawCategoryItem(Graphics2D g2,
                                 Rectangle2D dataArea,
                                 CategoryPlot plot,
                                 ValueAxis axis,
                                 CategoryDataset data, int series,
                                 Object category, int categoryIndex,
                                 Object previousCategory) {

        // plot non-null values...
        Number dataValue = data.getValue(series, category);
        if (dataValue!=null) {

            // X
            double value = dataValue.doubleValue();
            double base = 0.0;

            if (this.upperClip<=0.0) {  // cases 1, 2, 3 and 4
                if (value>=this.upperClip)
                    return; // bar is not visible
                base = this.upperClip;
                if (value<=this.lowerClip) {
                    value = this.lowerClip;
                }
            }
            else if (this.lowerClip<=0.0) { // cases 5, 6, 7 and 8
                if (value>=this.upperClip)
                    value=this.upperClip;
                else if (value<=this.lowerClip)
                    value=this.lowerClip;
            }
            else { // cases 9, 10, 11 and 12
                if (value<=this.lowerClip)
                    return; // bar is not visible
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
            double rectY = dataArea.getY()
                + dataArea.getHeight()*plot.getIntroGapPercent();

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

            // DRAW THE BAR...
            double rectHeight = itemWidth;
            Rectangle2D bar =
                new Rectangle2D.Double(rectX, rectY, rectWidth, rectHeight);
            Paint seriesPaint = plot.getSeriesPaint(series);
            g2.setPaint(seriesPaint);
            g2.fill(bar);
            if (itemWidth>BAR_OUTLINE_WIDTH_THRESHOLD) {
                g2.setStroke(plot.getSeriesStroke(series));
                g2.setPaint(plot.getSeriesOutlinePaint(series));
                g2.draw(bar);
                if (plot.getLabelsVisible()) {
                    Font labelFont = plot.getLabelFont();
                    g2.setFont(labelFont);
                    Paint paint = plot.getLabelPaint();
                    g2.setPaint(paint);
                    g2.drawString(String.valueOf(dataValue),
                        (int)(rectX + rectWidth * 0.90), (int)rectY - 5);
                }
            }

            // collect entity and tool tip information...
            if (this.info!=null) {
                EntityCollection entities = this.info.getEntityCollection();
                if (entities!=null) {
                    String tip="";
                    if (this.toolTipGenerator!=null) {
                        tip = this.toolTipGenerator
                            .generateToolTip(data, series, category);
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
     * Returns true to indicate that this renderer does allow for gaps between
     * items.
     *
     * @return <code>true</code> if this renderer allows gaps between items.
     */
    public boolean hasItemGaps() {
        return true;
    }

    /**
     * Returns the number of bar widths in each category (used to calculate
     * the width of a single bar).
     *
     * @param data     The data.
     *
     * @return the number of bar widths in each category.
     */
    public int barWidthsPerCategory(CategoryDataset data) {
        return data.getSeriesCount();
    }

}
