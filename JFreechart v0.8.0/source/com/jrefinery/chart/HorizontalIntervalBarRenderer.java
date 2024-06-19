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
 * ----------------------------------
 * HorizontalIntervalBarRenderer.java
 * ----------------------------------
 * (C) Copyright 2000-2002, by Simba Management Limited and Contributors.
 *
 * Original Author:  David Gilbert (for Simba Management Limited);
 * Contributor(s):   -;
 *
 * $Id: HorizontalIntervalBarRenderer.java,v 1.1 2007/10/10 18:59:08 vauchers Exp $
 *
 * Changes
 * -------
 * 21-Mar-2002 : Version 1 (DG);
 *
 */

package com.jrefinery.chart;

import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.Paint;
import java.awt.geom.Rectangle2D;
import com.jrefinery.data.CategoryDataset;
import com.jrefinery.data.IntervalCategoryDataset;

public class HorizontalIntervalBarRenderer extends BarRenderer implements CategoryItemRenderer {

    public HorizontalIntervalBarRenderer() {
    }

    /**
     * Initialises the renderer.  This method will be called before the first item is rendered,
     * giving the renderer an opportunity to initialise any state information it wants to
     * maintain.
     */
    public void initialise(Graphics2D g2,
                           Rectangle2D dataArea,
                           CategoryPlot plot,
                           CategoryDataset data) {

    }

    /**
     * Returns true, since for this renderer there are gaps between the items in one category.
     */
    public boolean hasItemGaps() {
        return false;
    }

    /**
     *  This renderer shows each series within a category as a separate bar (as opposed to a
     *  stacked bar renderer).
     *  @param data The data.
     */
    public int barWidthsPerCategory(CategoryDataset data) {
        return 2;
    }

    /**
     * Draws the bar for a single (series, category) data item.
     *
     */
    public Shape drawCategoryItem(Graphics2D g2, Rectangle2D dataArea,
                                  CategoryPlot plot, ValueAxis axis,
                                  CategoryDataset data, int series, Object category,
                                  int categoryIndex, Object previousCategory) {

        Shape result = null;

        IntervalCategoryDataset intervalData = (IntervalCategoryDataset)data;

	// X0
	Number value0 = intervalData.getStartValue(series,category);
	double translatedValue0 = axis.translateValueToJava2D(value0.doubleValue(), dataArea);

	// X1
	Number value1 = intervalData.getEndValue(series, category);
	double translatedValue1 = axis.translateValueToJava2D(value1.doubleValue(), dataArea);

	if (translatedValue1 < translatedValue0)
	{
	  double temp = translatedValue1;
	  translatedValue1 = translatedValue0;
	  translatedValue0 = temp;
	}

	// Y
	double rectY = dataArea.getY()
		       // intro gap
		       + dataArea.getHeight()*plot.getIntroGapPercent()
		       // bars in completed categories
		       + (categoryIndex*categorySpan/data.getCategoryCount())
		       // gaps between completed categories
		       + (categoryIndex*categoryGapSpan/(data.getCategoryCount()-1))
		       // bars+gaps completed in current category
		       + (series*itemSpan/(data.getCategoryCount()*data.getSeriesCount()));
	//+ (series*itemGapSpan/(data.getCategoryCount()*(data.getSeriesCount()-1)));
	// WIDTH
	double rectWidth = Math.abs(translatedValue1-translatedValue0);

	// HEIGHT
	double rectHeight = itemWidth;

	// DRAW THE BAR...
	Rectangle2D bar = new Rectangle2D.Double(translatedValue0, rectY, rectWidth, rectHeight);

	Paint seriesPaint = plot.getSeriesPaint(series);
	g2.setPaint(seriesPaint);
	g2.fill(bar);
	if (itemWidth>BAR_OUTLINE_WIDTH_THRESHOLD) {
	  g2.setStroke(plot.getSeriesStroke(series));
	  g2.setPaint(plot.getSeriesOutlinePaint(series));
	  g2.draw(bar);
	}
	result = bar;
        return result;
    }


}