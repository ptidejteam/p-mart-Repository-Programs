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
 * -----------------------
 * ChartRenderingInfo.java
 * -----------------------
 * (C) Copyright 2002, by Simba Management Limited.
 *
 * Original Author:  David Gilbert (for Simba Management Limited);
 * Contributor(s):   -;
 *
 * $Id: ChartRenderingInfo.java,v 1.1 2007/10/10 19:52:14 vauchers Exp $
 *
 * Changes
 * -------
 * 22-Jan-2002 : Version 1 (DG);
 * 05-Feb-2002 : Added a new constructor, completed Javadoc comments (DG);
 * 05-Mar-2002 : Added a clear() method (DG);
 * 23-May-2002 : Renamed DrawInfo --> ChartRenderingInfo (DG);
 *
 */

package com.jrefinery.chart;

import com.jrefinery.chart.entity.EntityCollection;
import com.jrefinery.chart.entity.StandardEntityCollection;
import java.awt.geom.Rectangle2D;

/**
 * A structure for storing rendering information from one call to the
 * JFreeChart.draw(...) method.
 * <P>
 * An instance of the JFreeChart class can draw itself within an arbitrary
 * rectangle on any Graphics2D.  It is assumed that client code will sometimes
 * render the same chart in more than one view, so the JFreeChart instance does
 * not retain any information about its rendered dimensions.  This information
 * can be useful sometimes, so you have the option to collect the information
 * at each call to JFreeChart.draw(...), by passing an instance of this
 * ChartRenderingInfo class.
 */
public class ChartRenderingInfo {

	/** The area in which the chart is drawn. */
	protected Rectangle2D chartArea;

	/** The area in which the plot and axes are drawn. */
	protected Rectangle2D plotArea;

	/** The area in which the data is plotted. */
	protected Rectangle2D dataArea;

	/** Storage for the chart entities. */
	protected EntityCollection entities;

	protected boolean generateToolTips;

	/**
	 * Constructs a new ChartRenderingInfo structure.  By default, no tooltip
	 * info will be collected.
	 */
	public ChartRenderingInfo() {
		this(null);
	}

	/**
	 * Constructs a new ChartRenderingInfo structure.
	 * <P>
	 * If an entity collection is supplied, it will be populated with
	 * information about the entities in a chart.
	 *
	 * @param entities      An entity collection (optional).
	 */
	public ChartRenderingInfo(EntityCollection entities) {

		chartArea = new Rectangle2D.Double();
		plotArea = new Rectangle2D.Double();
		dataArea = new Rectangle2D.Double();

		this.entities = entities;
		this.generateToolTips = true;

	}

	/**
	 * Returns the area in which the chart was drawn.
	 *
	 * @return The area in which the chart was drawn.
	 */
	public Rectangle2D getChartArea() {
		return this.chartArea;
	}

	/**
	 * Sets the area in which the chart was drawn.
	 *
	 * @param area The chart area.
	 */
	public void setChartArea(Rectangle2D area) {
		chartArea.setRect(area);
	}

	/**
	 * Returns the area in which the plot (and axes, if any) were drawn.
	 *
	 * @return The plot area.
	 */
	public Rectangle2D getPlotArea() {
		return this.plotArea;
	}

	/**
	 * Sets the area in which the plot and axes were drawn.
	 *
	 * @param area      The plot area.
	 */
	public void setPlotArea(Rectangle2D area) {
		plotArea.setRect(area);
	}

	/**
	 * Returns the area in which the data was plotted.
	 *
	 * @return The data area.
	 */
	public Rectangle2D getDataArea() {
		return this.dataArea;
	}

	/**
	 * Sets the area in which the data has been plotted.
	 *
	 * @param area      The data area.
	 */
	public void setDataArea(Rectangle2D area) {
		dataArea.setRect(area);
	}

	/**
	 * Returns a collection of entities.
	 *
	 * @return The entity collection.
	 */
	public EntityCollection getEntityCollection() {
		return this.entities;
	}

	/**
	 * Sets the entity collection.
	 *
	 * @param entities The entity collection.
	 */
	public void setEntityCollection(EntityCollection entities) {
		this.entities = entities;
	}

	/**
	 * Returns a flag that indicates whether or not tool tips should be
	 * generated.
	 *
	 * @return The generate tool tips flag.
	 */
	public boolean isGenerateToolTips() {
		return this.generateToolTips;
	}

	/**
	 * Sets a flag that controls whether or not tool tips are generated.
	 *
	 * @param flag      The flag.
	 */
	public void setGenerateToolTips(boolean flag) {
		this.generateToolTips = flag;
		if (flag) {
			this.entities = new StandardEntityCollection();
		}
		else {
			this.entities = null;
		}

	}

	/**
	 * Clears the information recorded by this object.
	 */
	public void clear() {

		this.chartArea.setRect(0.0, 0.0, 0.0, 0.0);
		this.plotArea.setRect(0.0, 0.0, 0.0, 0.0);
		this.dataArea.setRect(0.0, 0.0, 0.0, 0.0);
		if (this.entities!=null) {
			this.entities.clear();
		}

	}

}
