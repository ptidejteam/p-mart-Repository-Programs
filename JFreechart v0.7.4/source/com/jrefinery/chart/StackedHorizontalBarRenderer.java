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
 * ---------------------------------
 * StackedHorizontalBarRenderer.java
 * ---------------------------------
 * (C) Copyright 2001, 2002, by Simba Management Limited.
 *
 * Original Author:  David Gilbert (for Simba Management Limited);
 * Contributor(s):   -;
 *
 * $Id: StackedHorizontalBarRenderer.java,v 1.1 2007/10/10 18:57:57 vauchers Exp $
 *
 * Changes
 * -------
 * 22-Oct-2001 : Version 1 (DG);
 *               Renamed DataSource.java --> Dataset.java etc. (DG);
 * 23-Oct-2001 : Changed intro and trail gaps on bar plots to use percentage of available space
 *               rather than a fixed number of units (DG);
 * 15-Nov-2001 : Modified to allow for null data values (DG);
 * 13-Dec-2001 : Initial tooltip implementation (DG);
 * 15-Feb-2002 : Added isStacked() method (DG);
 *
 */

package com.jrefinery.chart;

import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.Paint;
import java.awt.geom.Rectangle2D;
import com.jrefinery.data.CategoryDataset;

/**
 * A renderer that draws horizontal bars that are "stacked" on top of one another.
 */
public class StackedHorizontalBarRenderer extends HorizontalBarRenderer {

    /**
     * Constructs a new StackedHorizontalBarRenderer.
     */
    public StackedHorizontalBarRenderer() {
    }

    /**
     * Returns true, to indicate that this renderer stacks values.  This affects the axis range
     * required to display all values.
     */
    public boolean isStacked() {

        return true;

    }

    /**
     * Returns a flag (always false for this renderer) to indicate whether or not there are
     * gaps between items in the plot.
     */
    public boolean hasItemGaps() {
        return false;
    }

    /**
     * Returns the number of "bar widths" per category.
     * <P>
     * For this style of rendering, there is only one bar per category.
     */
    public int barWidthsPerCategory(CategoryDataset data) {
        return 1;
    }

    /**
     * Draws a bar for a specific item.
     * @param g2 The graphics device.
     * @param dataArea The plot area.
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
    public Shape drawBar(Graphics2D g2, Rectangle2D dataArea,
                         BarPlot plot, ValueAxis valueAxis, CategoryDataset data,
                         int series, Object category, int categoryIndex,
                         double translatedZero, double itemWidth,
                         double categorySpan, double categoryGapSpan,
                         double itemSpan, double itemGapSpan) {

        Shape result = null;

        // RECT X
        double positiveBase = 0.0;
        double negativeBase = 0.0;
        for (int i=0; i<series; i++) {
            Number v = data.getValue(i, category);
            if (v!=null) {
                double d = v.doubleValue();
                if (d>0) positiveBase = positiveBase+d;
                else negativeBase = negativeBase+d;
            }
        }


        Number value = data.getValue(series, category);
        if (value!=null) {
            double xx = value.doubleValue();
            double translatedBase;
            double translatedValue;
            double rectX;

            if (xx>0) {
                translatedBase = valueAxis.translateValueToJava2D(positiveBase, dataArea);
                translatedValue = valueAxis.translateValueToJava2D(positiveBase+xx, dataArea);
                rectX = Math.min(translatedBase, translatedValue);
            }
            else {
                translatedBase = valueAxis.translateValueToJava2D(negativeBase, dataArea);
                translatedValue = valueAxis.translateValueToJava2D(negativeBase+xx, dataArea);
                rectX = Math.min(translatedBase, translatedValue);
            }

            // Y
            double rectY = dataArea.getY()
                               // intro gap
                               + dataArea.getHeight()*plot.getIntroGapPercent()
                               // bars in completed categories
                               + (categoryIndex*categorySpan/data.getCategoryCount());
            if (data.getCategoryCount()>1) {
                // add gaps between completed categories
                rectY = rectY + (categoryIndex*categoryGapSpan/(data.getCategoryCount()-1));
            }

            // RECT WIDTH
            double rectWidth = Math.abs(translatedValue-translatedBase);
            // Supplied as a parameter as it is constant

            // rect HEIGHT
            double rectHeight = itemWidth;

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