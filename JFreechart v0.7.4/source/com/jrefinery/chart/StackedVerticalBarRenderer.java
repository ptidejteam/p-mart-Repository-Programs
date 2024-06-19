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
 * -------------------------------
 * StackedVerticalBarRenderer.java
 * -------------------------------
 * (C) Copyright 2000-2002, by Simba Management Limited.
 *
 * Original Author:  David Gilbert (for Simba Management Limited);
 * Contributor(s):   -;
 *
 * $Id: StackedVerticalBarRenderer.java,v 1.1 2007/10/10 18:57:57 vauchers Exp $
 *
 * Changes
 * -------
 * 19-Oct-2001 : Version 1 (DG);
 * 22-Oct-2001 : Renamed DataSource.java --> Dataset.java etc. (DG);
 * 23-Oct-2001 : Changed intro and trail gaps on bar plots to use percentage of available space
 *               rather than a fixed number of units (DG);
 * 15-Nov-2001 : Modified to allow for null data values (DG);
 * 22-Nov-2001 : Modified to allow for negative data values (DG);
 * 13-Dec-2001 : Added tooltips (DG);
 * 16-Jan-2002 : Fixed bug for single category datasets (DG);
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
 * A bar renderer that draws stacked bars for a vertical bar plot.
 * <P>
 * Still experimental at this stage.  The main problem is that setting an auto range for the axis
 * is now a more difficult problem...so use a fixed axis range for now.
 *
 */
public class StackedVerticalBarRenderer extends VerticalBarRenderer {

    /**
     * Constructs a new renderer.
     */
    public StackedVerticalBarRenderer() {
    }

    /**
     * Returns true, to indicate that this renderer stacks values.  This affects the axis range
     * required to display all values.
     */
    public boolean isStacked() {

        return true;

    }

    /**
     * Returns the number of "bar widths" per category.
     * <P>
     * There is just one bar-width per category for this renderer, since all the bars are stacked
     * on top of one another.
     * @param data The dataset (ignored).
     *
     */
    public int barWidthsPerCategory(CategoryDataset data) {
        return 1;
    }

    /**
     * Returns true if there are gaps between the items, and false otherwise.
     * <P>
     * In this case, there are no gaps between items, since all the items are stacked on top of
     * one another.
     * @return True if there are gaps between the items, and false otherwise.
     */
    public boolean hasItemGaps() {
        return false;
    }

    /**
     * Draws one bar.
     */
    public Shape drawBar(Graphics2D g2, Rectangle2D dataArea, BarPlot plot, ValueAxis valueAxis,
                         CategoryDataset data, int series, Object category, int categoryIndex,
                         double translatedZero, double itemWidth,
                         double categorySpan, double categoryGapSpan,
                         double itemSpan, double itemGapSpan) {

        Shape result = null;

        Paint seriesPaint = plot.getSeriesPaint(series);
        Paint seriesOutlinePaint = plot.getSeriesOutlinePaint(series);

        // BAR X
        double rectX = dataArea.getX()
                           // intro gap
                           + dataArea.getWidth()*plot.getIntroGapPercent()
                           // bars in completed categories
                           + categoryIndex*categorySpan/data.getCategoryCount();
        if (data.getCategoryCount()>1) {
            // gaps between completed categories
            rectX = rectX+categoryIndex*categoryGapSpan/(data.getCategoryCount()-1);
        }

        // BAR Y
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
            double barY;
            if (xx>0) {
                translatedBase = valueAxis.translateValueToJava2D(positiveBase, dataArea);
                translatedValue = valueAxis.translateValueToJava2D(positiveBase+xx, dataArea);
                barY = Math.min(translatedBase, translatedValue);
            }
            else {
                translatedBase = valueAxis.translateValueToJava2D(negativeBase, dataArea);
                translatedValue = valueAxis.translateValueToJava2D(negativeBase+xx, dataArea);
                barY = Math.min(translatedBase, translatedValue);
            }

            // BAR WIDTH
            double rectWidth = itemWidth;

            // BAR HEIGHT
            double barHeight = Math.abs(translatedValue-translatedBase);

            Rectangle2D bar = new Rectangle2D.Double(rectX, barY, rectWidth, barHeight);
            g2.setPaint(seriesPaint);
            g2.fill(bar);
            result = bar;
            if (rectWidth>3) {
                g2.setStroke(plot.getSeriesStroke(series));
                g2.setPaint(seriesOutlinePaint);
                g2.draw(bar);
            }
        }

        return result;

    }

}