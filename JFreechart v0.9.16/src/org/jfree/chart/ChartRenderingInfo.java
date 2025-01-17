/* ===========================================================
 * JFreeChart : a free chart library for the Java(tm) platform
 * ===========================================================
 *
 * (C) Copyright 2000-2004, by Object Refinery Limited and Contributors.
 *
 * Project Info:  http://www.jfree.org/jfreechart/index.html
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
 * [Java is a trademark or registered trademark of Sun Microsystems, Inc. 
 * in the United States and other countries.]
 *
 * -----------------------
 * ChartRenderingInfo.java
 * -----------------------
 * (C) Copyright 2002-2004, by Object Refinery Limited.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited);
 * Contributor(s):   -;
 *
 * $Id: ChartRenderingInfo.java,v 1.1 2007/10/10 19:25:38 vauchers Exp $
 *
 * Changes
 * -------
 * 22-Jan-2002 : Version 1 (DG);
 * 05-Feb-2002 : Added a new constructor, completed Javadoc comments (DG);
 * 05-Mar-2002 : Added a clear() method (DG);
 * 23-May-2002 : Renamed DrawInfo --> ChartRenderingInfo (DG);
 * 26-Sep-2002 : Fixed errors reported by Checkstyle (DG);
 * 17-Sep-2003 : Added PlotRenderingInfo (DG);
 *
 */

package org.jfree.chart;

import java.awt.geom.Rectangle2D;

import org.jfree.chart.entity.EntityCollection;
import org.jfree.chart.entity.StandardEntityCollection;
import org.jfree.chart.plot.PlotRenderingInfo;

/**
 * A structure for storing rendering information from one call to the
 * JFreeChart.draw(...) method.
 * <P>
 * An instance of the {@link JFreeChart} class can draw itself within an arbitrary
 * rectangle on any Graphics2D.  It is assumed that client code will sometimes
 * render the same chart in more than one view, so the {@link JFreeChart} instance does
 * not retain any information about its rendered dimensions.  This information
 * can be useful sometimes, so you have the option to collect the information
 * at each call to <code>JFreeChart.draw(...)</code>, by passing an instance of this
 * <code>ChartRenderingInfo</code> class.
 *
 * @author David Gilbert
 */
public class ChartRenderingInfo {

    /** The area in which the chart is drawn. */
    private Rectangle2D chartArea;

    /** Rendering info for the chart's plot (and subplots, if any). */
    private PlotRenderingInfo plotInfo;
    
    /** The area in which the plot and axes are drawn. */
    private Rectangle2D plotArea;

    /** The area in which the data is plotted. */
    //private Rectangle2D dataArea;

    /** 
     * Storage for the chart entities.  Since retaining entity information for charts with a
     * large number of data points consumes a lot of memory, it is intended that you can set
     * this to <code>null</code> to prevent the information being collected.
     */
    private EntityCollection entities;

    /**
     * Constructs a new ChartRenderingInfo structure that can be used to collect information
     * about the dimensions of a rendered chart.
     */
    public ChartRenderingInfo() {
        this(new StandardEntityCollection());
    }

    /**
     * Constructs a new ChartRenderingInfo structure.
     * <P>
     * If an entity collection is supplied, it will be populated with information about the
     * entities in a chart.  If it is null, no entity information (including tool tips) will
     * be collected.
     *
     * @param entities  an entity collection (null permitted).
     */
    public ChartRenderingInfo(EntityCollection entities) {

        this.chartArea = new Rectangle2D.Double();
        this.plotArea = new Rectangle2D.Double();
        //this.dataArea = new Rectangle2D.Double();
    
        this.plotInfo = new PlotRenderingInfo(this);
    
        this.entities = entities;

    }

    /**
     * Returns the area in which the chart was drawn.
     *
     * @return the area in which the chart was drawn.
     */
    public Rectangle2D getChartArea() {
        return this.chartArea;
    }

    /**
     * Sets the area in which the chart was drawn.
     *
     * @param area  the chart area.
     */
    public void setChartArea(Rectangle2D area) {
        chartArea.setRect(area);
    }

    /**
     * Returns the area in which the plot (and axes, if any) were drawn.
     *
     * @return the plot area.
     */
    public Rectangle2D getPlotArea() {
        return this.plotArea;
    }

    /**
     * Sets the area in which the plot and axes were drawn.
     *
     * @param area  the plot area.
     */
    public void setPlotArea(Rectangle2D area) {
        plotArea.setRect(area);
    }

    /**
     * Returns the collection of entities maintained by this instance.
     *
     * @return The entity collection (possibly <code>null</code>.
     */
    public EntityCollection getEntityCollection() {
        return this.entities;
    }

    /**
     * Sets the entity collection.
     *
     * @param entities  the entity collection (<code>null</code> permitted).
     */
    public void setEntityCollection(EntityCollection entities) {
        this.entities = entities;
    }

    /**
     * Clears the information recorded by this object.
     */
    public void clear() {

        this.chartArea.setRect(0.0, 0.0, 0.0, 0.0);
        this.plotArea.setRect(0.0, 0.0, 0.0, 0.0);
        this.plotInfo = new PlotRenderingInfo(this);
        if (this.entities != null) {
            this.entities.clear();
        }

    }
  
    /**
     * Returns the rendering info for the chart's plot.
     * 
     * @return The rendering info for the plot.
     */  
    public PlotRenderingInfo getPlotInfo() {
        return this.plotInfo;
    }

}
