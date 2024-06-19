/* =======================================
 * JFreeChart : a Java Chart Class Library
 * =======================================
 *
 * Project Info:  http://www.jrefinery.com/jfreechart;
 * Project Lead:  David Gilbert (david.gilbert@jrefinery.com);
 *
 * This file...
 * $Id: HorizontalBarRenderer.java,v 1.1 2007/10/10 18:54:39 vauchers Exp $
 *
 * Original Author:  David Gilbert;
 * Contributor(s):   -;
 *
 * (C) Copyright 2001 Simba Management Limited;
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
 * Change History:
 * ---------------
 * 22-Oct-2001 : Initial implementation (DG);
 *               Renamed DataSource.java --> Dataset.java etc. (DG);
 * 23-Oct-2001 : Changed intro and trail gaps on bar plots to use percentage of available space
 *               rather than a fixed number of units (DG);
 * 31-Oct-2001 : Debug for gaps (DG);
 * 15-Nov-2001 : Modified to allow for null values (DG);
 *
 */

package com.jrefinery.chart;

import java.awt.*;
import java.awt.geom.*;
import com.jrefinery.data.*;

/**
 * Plug-in class that handles the drawing of bars on a horizontal bar plot.
 */
public class HorizontalBarRenderer {

    /** Constant that controls the minimum width before a bar has an outline drawn. */
    private static final double BAR_OUTLINE_WIDTH_THRESHOLD = 3.0;

    /**
     * Returns true, since for this renderer there are gaps between the items in one category.
     */
    public boolean hasItemGaps() {
        return true;
    }

    /**
     *  This renderer shows each series within a category as a separate bar (as opposed to a
     *  stacked bar renderer).
     *  @param data The data being plotted.
     */
    public int barWidthsPerCategory(CategoryDataset data) {
        return data.getSeriesCount();
    }

    /**
     * Draws the bar for a single (series, category) data item.
     * @param g2
     * @param plotArea
     * @param plot
     * @param valueAxis
     * @param data
     * @param series
     * @param category
     * @param categoryIndex
     * @param translatedZero
     * @param itemWidth
     * @param categorySpan
     * @param categoryGapSpan
     * @param itemSpan
     * @param itemGapSpan
     */
    public void drawBar(Graphics2D g2, Rectangle2D plotArea, BarPlot plot, ValueAxis valueAxis,
                        CategoryDataset data, int series, Object category, int categoryIndex,
                        double translatedZero, double itemWidth,
                        double categorySpan, double categoryGapSpan,
                        double itemSpan, double itemGapSpan) {

        // first check the value we are plotting...
        Number value = data.getValue(series, category);
        if (value!=null) {

            // X
            double translatedValue = valueAxis.translatedValue(value, plotArea);
            double rectX = Math.min(translatedZero, translatedValue);

            // Y
            double rectY = plotArea.getY() + plotArea.getHeight()*plot.getIntroGapPercent();

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

            // WIDTH
            double rectWidth = Math.abs(translatedValue-translatedZero);

            // HEIGHT
            double rectHeight = itemWidth;

            // DRAW THE BAR...
            Rectangle2D bar = new Rectangle2D.Double(rectX, rectY, rectWidth, rectHeight);
            Paint seriesPaint = plot.getSeriesPaint(series);
            g2.setPaint(seriesPaint);
            g2.fill(bar);
            if (itemWidth>BAR_OUTLINE_WIDTH_THRESHOLD) {
                g2.setStroke(plot.getSeriesStroke(series));
                g2.setPaint(plot.getSeriesOutlinePaint(series));
                g2.draw(bar);
            }
        }

    }

}