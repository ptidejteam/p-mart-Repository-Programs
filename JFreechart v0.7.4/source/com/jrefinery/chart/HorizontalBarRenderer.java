/* =======================================
 * JFreeChart : a Java Chart Class Library
 * =======================================
 *
 * Project Info:  http://www.object-refinery.com/jfreechart
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
 * --------------------------
 * HorizontalBarRenderer.java
 * --------------------------
 * (C) Copyright 2001, 2002, by Simba Management Limited.
 *
 * Original Author:  David Gilbert (for Simba Management Limited);
 * Contributor(s):   -;
 *
 * $Id: HorizontalBarRenderer.java,v 1.1 2007/10/10 18:57:57 vauchers Exp $
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
 *
 */

package com.jrefinery.chart;

import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.Paint;
import java.awt.geom.Rectangle2D;
import com.jrefinery.data.CategoryDataset;

/**
 * Plug-in class that handles the drawing of bars on a horizontal bar plot.
 */
public class HorizontalBarRenderer {

    /** Constant that controls the minimum width before a bar has an outline drawn. */
    private static final double BAR_OUTLINE_WIDTH_THRESHOLD = 3.0;

    /**
     * Returns a flag indicating whether or not the renderer stacks values within each category.
     * This has an effect on the minimum and maximum values required for the axis to show all the
     * data values.
     * <P>
     * Subclasses should override this method as necessary.
     */
    public boolean isStacked() {

        return false;

    }

    /**
     * Returns true, since for this renderer there are gaps between the items in one category.
     */
    public boolean hasItemGaps() {
        return true;
    }

    /**
     *  This renderer shows each series within a category as a separate bar (as opposed to a
     *  stacked bar renderer).
     *  @param data The data.
     */
    public int barWidthsPerCategory(CategoryDataset data) {
        return data.getSeriesCount();
    }

    /**
     * Draws the bar for a single (series, category) data item.
     * @param g2 The graphics device.
     * @param dataArea The data area.
     * @param plot The plot.
     * @param valueAxis The range axis.
     * @param data The data.
     * @param series The series number (zero-based index).
     * @param category The category.
     * @param categoryIndex The category number (zero-based index).
     * @param zeroToJava2D  The data value zero translated into Java2D space.
     * @param itemWidth The width of one bar.
     * @param categorySpan The width of all items in one category.
     * @param categoryGapSpan The width of all category gaps.
     * @param itemSpan The width of all items.
     * @param itemGapSpan The width of all item gaps.
     */
    public Shape drawBar(Graphics2D g2, Rectangle2D dataArea, BarPlot plot, ValueAxis valueAxis,
                         CategoryDataset data, int series, Object category, int categoryIndex,
                         double zeroToJava2D, double itemWidth,
                         double categorySpan, double categoryGapSpan,
                         double itemSpan, double itemGapSpan) {

        Shape result = null;

        // first check the value we are plotting...
        Number value = data.getValue(series, category);
        if (value!=null) {

            // X
            double translatedValue = valueAxis.translateValueToJava2D(value.doubleValue(), dataArea);
            double rectX = Math.min(zeroToJava2D, translatedValue);

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

            // WIDTH
            double rectWidth = Math.abs(translatedValue-zeroToJava2D);

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
            result = bar;
        }

        return result;

    }

}