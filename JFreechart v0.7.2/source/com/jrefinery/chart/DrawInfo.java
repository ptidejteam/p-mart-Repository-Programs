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
 * -------------
 * DrawInfo.java
 * -------------
 * (C) Copyright 2002, by Simba Management Limited.
 *
 * Original Author:  David Gilbert (for Simba Management Limited);
 * Contributor(s):   -;
 *
 * $Id: DrawInfo.java,v 1.1 2007/10/10 18:56:25 vauchers Exp $
 *
 * Changes
 * -------
 * 22-Jan-2002 : Version 1 (DG);
 * 05-Feb-2002 : Added a new constructor, completed Javadoc comments (DG);
 *
 */

package com.jrefinery.chart;

import java.awt.geom.*;
import com.jrefinery.chart.tooltips.ToolTipsCollection;

/**
 * A structure for storing rendering information from one call to the JFreeChart.draw(...)
 * method.
 * <P>
 * An instance of the JFreeChart class can draw itself within an arbitrary rectangle on any
 * Graphics2D.  It is assumed that client code will sometimes render the same chart in more
 * than one view, so the JFreeChart instance does not retain any information about its
 * rendered dimensions.  This information can be useful sometimes, so you have the option to
 * collect the information at each call to JFreeChart.draw(...), by passing an instance of
 * this DrawInfo class.
 */
public class DrawInfo {

    /** The area in which the chart is drawn. */
    protected Rectangle2D chartArea;

    /** The area in which the plot and axes are drawn. */
    protected Rectangle2D plotArea;

    /** The area in which the data is plotted. */
    protected Rectangle2D dataArea;

    /** Tooltip information. */
    protected ToolTipsCollection tooltips;

    /**
     * Constructs a new DrawInfo structure.  By default, no tooltip info will be collected.
     */
    public DrawInfo() {
        this(null);
    }

    /**
     * Constructs a new DrawInfo structure
     */
    public DrawInfo(ToolTipsCollection tooltips) {

        chartArea = new Rectangle2D.Double();
        plotArea = new Rectangle2D.Double();
        dataArea = new Rectangle2D.Double();

        this.tooltips = tooltips;

    }

    /**
     * Returns the area in which the chart was drawn.
     * @return The area in which the chart was drawn.
     */
    public Rectangle2D getChartArea() {
        return this.chartArea;
    }

    /**
     * Sets the area in which the chart was drawn.
     * @param area The chart area.
     */
    public void setChartArea(Rectangle2D area) {
        chartArea.setRect(area);
    }

    /**
     * Returns the area in which the plot (and axes, if any) were drawn.
     * @return The area in which the plot (and axes, if any) were drawn.
     */
    public Rectangle2D getPlotArea() {
        return this.plotArea;
    }

    /**
     * Sets the area in which the plot and axes were drawn.
     * @param area The plot area.
     */
    public void setPlotArea(Rectangle2D area) {
        plotArea.setRect(area);
    }

    /**
     * Returns the area in which the data was plotted.
     * @return The area in which the data was plotted.
     */
    public Rectangle2D getDataArea() {
        return this.dataArea;
    }

    /**
     * Sets the area in which the data has been plotted.
     */
    public void setDataArea(Rectangle2D area) {
        dataArea.setRect(area);
    }

    /**
     * Returns a collection of tooltips.
     */
    public ToolTipsCollection getToolTipsCollection() {
        return this.tooltips;
    }

    /**
     * Sets the tooltips collection manager.
     * @param tooltips The tooltips collection manager.
     */
    public void setToolTipsCollection(ToolTipsCollection tooltips) {
        this.tooltips = tooltips;
    }

}