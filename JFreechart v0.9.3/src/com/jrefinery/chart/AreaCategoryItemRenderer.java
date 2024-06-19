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
 * -----------------------------
 * AreaCategoryItemRenderer.java
 * -----------------------------
 * (C) Copyright 2002, by Jon Iles.
 *
 * Original Author:  Jon Iles;
 * Contributor(s):   David Gilbert (for Simba Management Limited);
 *
 * $Id: AreaCategoryItemRenderer.java,v 1.1 2007/10/10 19:52:14 vauchers Exp $
 *
 * Changes:
 * --------
 * 21-May-2002 : Version 1, contributed by John Iles (DG);
 * 29-May-2002 : Now extends AbstractCategoryItemRenderer (DG);
 * 11-Jun-2002 : Updated Javadoc comments (DG);
 * 25-Jun-2002 : Removed unnecessary imports (DG);
 *
 */

package com.jrefinery.chart;

import com.jrefinery.data.CategoryDataset;
import com.jrefinery.data.Range;

import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.Polygon;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;

/**
 * A category item renderer that draws area charts.  You can use this renderer
 * with the VerticalCategoryPlot class.
 */
public class AreaCategoryItemRenderer extends AbstractCategoryItemRenderer {

	/**
	 * Draws a line (or some other marker) to indicate a certain value on the
	 * range axis.
	 *
	 * @param g2    The graphics device.
	 * @param plot      The plot.
	 * @param axis      The value axis.
	 * @param marker    The marker.
	 * @param axisDataArea      The area defined by the axes.
	 * @param dataClipRegion    The data clip region.
	 */
	public void drawRangeMarker(Graphics2D g2,
								CategoryPlot plot,
								ValueAxis axis,
								Marker marker,
								Rectangle2D axisDataArea,
								Shape dataClipRegion)
	{

		double value = marker.getValue();
		Range range = axis.getRange();
		if (!range.contains(value)) return;

		double y = axis.translateValueToJava2D(marker.getValue(), axisDataArea);
		Line2D line = new Line2D.Double(axisDataArea.getMinX(), y,
										axisDataArea.getMaxX(), y);
		g2.setPaint(marker.getOutlinePaint());
		g2.draw(line);

	}

	/**
	 * Draw a single data item.
	 *
	 * @param g2    The graphics device.
	 * @param dataArea      The data plot area.
	 * @param plot      The plot.
	 * @param axis      The range axis.
	 * @param data      The data.
	 * @param series    The series number (zero-based index).
	 * @param category      The category.
	 * @param categoryIndex     The category number (zero-based index).
	 * @param previousCategory      The previous category (will be null when
	 *      the first category is drawn).
	 */
	public void drawCategoryItem(Graphics2D g2,
								 Rectangle2D dataArea,
								 CategoryPlot plot,
								 ValueAxis axis,
								 CategoryDataset data,
								 int series,
								 Object category,
								 int categoryIndex,
								 Object previousCategory)
	{

		// plot non-null values...
		Number value = data.getValue(series, category);
		if (value!=null) {
			double x1 = plot.getCategoryCoordinate(categoryIndex, dataArea);
			double y1 = axis.translateValueToJava2D(value.doubleValue(),
				dataArea);

			g2.setPaint(plot.getSeriesPaint(series));
			g2.setStroke(plot.getSeriesStroke(series));

			if (previousCategory!=null) {
				Number previousValue = data.getValue(series, previousCategory);
				if (previousValue!=null) {
					double x0 = plot.getCategoryCoordinate(categoryIndex-1,
						dataArea);
					double y0 = axis.translateValueToJava2D(
						previousValue.doubleValue(), dataArea);
					double zeroInJava2D = plot.getRangeAxis()
						.translateValueToJava2D(0.0, dataArea);

					Polygon p = new Polygon ();
					p.addPoint((int)x0, (int)y0);
					p.addPoint((int)x1, (int)y1);
					p.addPoint((int)x1, (int)zeroInJava2D);
					p.addPoint((int)x0, (int)zeroInJava2D);

					g2.setPaint(plot.getSeriesPaint(series));
					g2.setStroke(plot.getSeriesStroke(series));
					g2.fill(p);
				}
			}
		}

	}

}
