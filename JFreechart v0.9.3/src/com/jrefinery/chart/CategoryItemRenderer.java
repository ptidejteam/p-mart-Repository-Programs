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
 * -------------------------
 * CategoryItemRenderer.java
 * -------------------------
 *
 * (C) Copyright 2001, 2002, by Simba Management Limited and Contributors.
 *
 * Original Author:  David Gilbert (for Simba Management Limited);
 * Contributor(s):   Mark Watson (www.markwatson.com);
 *
 * $Id: CategoryItemRenderer.java,v 1.1 2007/10/10 19:52:14 vauchers Exp $
 *
 * Changes
 * -------
 * 23-Oct-2001 : Version 1 (DG);
 * 16-Jan-2002 : Renamed HorizontalCategoryItemRenderer.java --> CategoryItemRenderer.java (DG);
 * 05-Feb-2002 : Changed return type of the drawCategoryItem method from void to Shape, as part
 *               of the tooltips implementation (DG)
 *
 *               NOTE (30-May-2002) : this has subsequently been changed back to void, tooltips
 *               are now collected along with entities in ChartRenderingInfo (DG);
 *
 * 14-Mar-2002 : Added the initialise method, and changed all bar plots to use this renderer (DG);
 * 23-May-2002 : Added ChartRenderingInfo to the initialise method (DG);
 * 29-May-2002 : Added the getAxisArea(Rectangle2D) method (DG);
 * 06-Jun-2002 : Updated Javadoc comments (DG);
 * 26-Jun-2002 : Added range axis to the initialise method (DG);
 *
 */

package com.jrefinery.chart;

import com.jrefinery.data.CategoryDataset;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;

/**
 * Defines the interface for a category item renderer.
 */
public interface CategoryItemRenderer {

	/**
	 * Initialises the renderer.  This method will be called before the first
	 * item is rendered, giving the renderer an opportunity to initialise any
	 * state information it wants to maintain.  The renderer can do nothing if
	 * it chooses.
	 *
	 * @param g2    The graphics device.
	 * @param dataArea      The area inside the axes.
	 * @param plot      The plot.
	 * @param axis      The range axis.
	 * @param data      The data.
	 * @param info      Collects chart rendering information for return to
	 *      caller.
	 */
	public void initialise(Graphics2D g2,
		Rectangle2D dataArea,
		CategoryPlot plot,
		ValueAxis axis,
		CategoryDataset data,
		ChartRenderingInfo info);

	/**
	 * Returns true if the data values are stacked, and false otherwise.
	 * <p>
	 * If the data values are stacked, this affects the axis range required to
	 * display all the data items.
	 *
	 * @return A flag indicating whether or not the data values are stacked.
	 */
	public boolean isStacked();

	/**
	 * Returns the area that the axes must fit into.  Often this is the same as
	 * the plotArea, but sometimes a smaller region should be used (for example,
	 * the 3D charts require the axes to use less space in order to leave room
	 * for the 'depth' part of the chart).
	 *
	 * @param plotArea      The data plot area.
	 *
	 * @return the area that the axes must fit into.
	 */
	public Rectangle2D getAxisArea(Rectangle2D plotArea);

	/**
	 * Returns the clip region... usually returns the dataArea, but some charts
	 * (e.g. 3D) have non rectangular clip regions.
	 *
	 * @param dataArea      The area inside the axes.
	 *
	 * @return the clip region.
	 */
	public Shape getDataClipRegion(Rectangle2D dataArea);

	/**
	 * Draws the background for the plot.
	 * <P>
	 * For most charts, the axisDataArea and the dataClipArea are the same.
	 * One case where they are different is the 3D-effect bar charts... here
	 * the data clip area extends above and to the right of the axisDataArea.
	 *
	 * @param g2    The graphics device.
	 * @param plot      The plot.
	 * @param axisDataArea      The area inside the axes.
	 * @param dataClipArea      The data clip area.
	 */
	public void drawPlotBackground(Graphics2D g2,
		CategoryPlot plot,
		Rectangle2D axisDataArea, Shape dataClipArea);

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
		CategoryPlot plot, ValueAxis axis, Marker marker,
		Rectangle2D axisDataArea, Shape dataClipRegion);

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
	public void drawCategoryItem(Graphics2D g2, Rectangle2D dataArea,
		CategoryPlot plot, ValueAxis axis,
		CategoryDataset data, int series, Object category,
		int categoryIndex, Object previousCategory);

}
