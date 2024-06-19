/* =======================================
 * JFreeChart : a Java Chart Class Library
 * =======================================
 *
 * Project Info:  http://www.jrefinery.com/jfreechart;
 * Project Lead:  David Gilbert (david.gilbert@jrefinery.com);
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
 * Contributor(s):   -;
 *
 * $Id: VerticalBarRenderer.java,v 1.1 2007/10/10 18:55:43 vauchers Exp $
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
 *
 */

package com.jrefinery.chart;

import java.awt.*;
import java.awt.geom.*;
import com.jrefinery.data.*;

/**
 * Plug-in class that handles the drawing of bars on a vertical bar plot.
 */
public class VerticalBarRenderer {

    /** Constant that controls the minimum width before a bar has an outline drawn. */
    private static final double BAR_OUTLINE_WIDTH_THRESHOLD = 3.0;

    /**
     * Returns true, since for this renderer there are gaps between the items in one category.
     */
    public boolean hasItemGaps() {
        return true;
    }

    /**
     * Returns the number of bar-widths displayed in each category.  For this renderer, there is one
     * bar per series, so we return the number of series.
     */
    public int barWidthsPerCategory(CategoryDataset data) {
        return data.getSeriesCount();
    }

    /**
     * Handles the rendering of a single bar.
     * @param g2
     * @param dataArea
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
     * @return A shape representing the area in which the bar is drawn (one use for this is
     *         supporting tooltips).
     */
    public Shape drawBar(Graphics2D g2, Rectangle2D dataArea, BarPlot plot, ValueAxis valueAxis,
                         CategoryDataset data, int series, Object category, int categoryIndex,
                         double translatedZero, double itemWidth,
                         double categorySpan, double categoryGapSpan,
                         double itemSpan, double itemGapSpan) {

        Shape result = null;

        // first check the value we are plotting...
        Number value = data.getValue(series, category);
        if (value!=null) {

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
            double translatedValue = valueAxis.translateValueToJava2D(value.doubleValue(), dataArea);
            double rectY = Math.min(translatedZero, translatedValue);

            // BAR WIDTH
            double rectWidth = itemWidth;

            // BAR HEIGHT
            double rectHeight = Math.abs(translatedValue-translatedZero);

            Rectangle2D bar = new Rectangle2D.Double(rectX, rectY, rectWidth, rectHeight);
            Paint seriesPaint = plot.getSeriesPaint(series);
            g2.setPaint(seriesPaint);
            g2.fill(bar);
            if (itemWidth>3) {
                g2.setStroke(plot.getSeriesStroke(series));
                g2.setPaint(plot.getSeriesOutlinePaint(series));
                g2.draw(bar);
            }
            result = bar;
        }

        return result;

    }

}