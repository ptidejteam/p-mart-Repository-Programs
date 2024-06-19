/* =======================================
 * JFreeChart : a Java Chart Class Library
 * =======================================
 *
 * Project Info:  http://www.jrefinery.com/jfreechart;
 * Project Lead:  David Gilbert (david.gilbert@jrefinery.com);
 *
 * This file...
 * $Id: StackedHorizontalBarRenderer.java,v 1.1 2007/10/10 18:54:39 vauchers Exp $
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
 * Changes
 * -------
 * 22-Oct-2001 : Version 1 (DG);
 *               Renamed DataSource.java --> Dataset.java etc. (DG);
 * 23-Oct-2001 : Changed intro and trail gaps on bar plots to use percentage of available space
 *               rather than a fixed number of units (DG);
 * 15-Nov-2001 : Modified to allow for null data values (DG);
 *
 */

package com.jrefinery.chart;

import java.awt.*;
import java.awt.geom.*;
import com.jrefinery.data.*;

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
     */
    public void drawBar(Graphics2D g2, Rectangle2D plotArea,
                        BarPlot plot, ValueAxis valueAxis, CategoryDataset data,
                        int series, Object category, int categoryIndex,
                        double translatedZero, double itemWidth,
                        double categorySpan, double categoryGapSpan,
                        double itemSpan, double itemGapSpan) {

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
                translatedBase = valueAxis.translatedValue(new Double(positiveBase), plotArea);
                translatedValue = valueAxis.translatedValue(new Double(positiveBase+xx), plotArea);
                rectX = Math.min(translatedBase, translatedValue);
            }
            else {
                translatedBase = valueAxis.translatedValue(new Double(negativeBase), plotArea);
                translatedValue = valueAxis.translatedValue(new Double(negativeBase+xx), plotArea);
                rectX = Math.min(translatedBase, translatedValue);
            }

            // Y
            double rectY = plotArea.getY()
                               // intro gap
                               + plotArea.getHeight()*plot.getIntroGapPercent()
                               // bars in completed categories
                               + (categoryIndex*categorySpan/data.getCategoryCount())
                               // gaps between completed categories
                               + (categoryIndex*categoryGapSpan/(data.getCategoryCount()-1));

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
        }
    }

}