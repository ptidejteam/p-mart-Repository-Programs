/* =======================================
 * JFreeChart : a Java Chart Class Library
 * =======================================
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
 * Contributor(s):   -;
 *
 * $Id: VerticalBarRenderer.java,v 1.1 2007/10/10 18:59:09 vauchers Exp $
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
 *
 */

package com.jrefinery.chart;

import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.Paint;
import java.awt.geom.Rectangle2D;
import com.jrefinery.data.CategoryDataset;

/**
 * A renderer that handles the drawing of bars for a vertical bar plot.
 */
public class VerticalBarRenderer extends BarRenderer implements CategoryItemRenderer {

    /**
     * Initialises the renderer.
     * <p>
     * This method gets called once at the start of the process of drawing a chart.
     *
     * @param g2 The graphics device.
     * @param dataArea The area in which the data is to be plotted.
     * @param plot The plot.
     * @param data The data.
     */
    public void initialise(Graphics2D g2,
                           Rectangle2D dataArea,
                           CategoryPlot plot,
                           CategoryDataset data) {

        this.calculateCategoryAndItemSpans(g2, dataArea, plot, data, dataArea.getWidth());

    }

    /**
     * Draws the bar for a single (series, category) data item.
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
    public Shape drawCategoryItem(Graphics2D g2, Rectangle2D dataArea,
                                  CategoryPlot plot, ValueAxis axis,
                                  CategoryDataset data, int series, Object category,
                                  int categoryIndex, Object previousCategory) {

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
            double translatedValue = axis.translateValueToJava2D(value.doubleValue(), dataArea);
            double rectY = Math.min(this.zeroInJava2D, translatedValue);

            // BAR WIDTH
            double rectWidth = itemWidth;

            // BAR HEIGHT
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
            result = bar;
        }

        return result;

    }

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

}