/* ======================================
 * JFreeChart : a free Java chart library
 * ======================================
 *
 * Project Info:  http://www.jfree.org/jfreechart/index.html
 * Project Lead:  David Gilbert (david.gilbert@object-refinery.com);
 *
 * (C) Copyright 2000-2003, by Object Refinery Limited and Contributors.
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
 * ----------------------
 * PlotRenderingInfo.java
 * ----------------------
 * (C) Copyright 2003, by Object Refinery Limited.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited);
 * Contributor(s):   -;
 *
 * $Id: PlotRenderingInfo.java,v 1.1 2007/10/10 19:19:05 vauchers Exp $
 *
 * Changes
 * -------
 * 16-Sep-2003 : Version 1 (DG);
 * 23-Sep-2003 : Added Javadocs (DG);
 *
 */
 
package org.jfree.chart.plot;

import java.awt.geom.Rectangle2D;
import java.util.List;

import org.jfree.chart.ChartRenderingInfo;


/**
 * Stores information about the dimensions of a plot and its subplots.
 * 
 * @author David Gilbert
 */
public class PlotRenderingInfo {

    /** The owner of this info. */
    private ChartRenderingInfo owner;
    
    /** The plot that generated the info. */
    //private Plot source;
    
    /** The plot area. */
    private Rectangle2D plotArea;
    
    /** The data area. */
    private Rectangle2D dataArea;
    
    /** Storage for the plot rendering info objects belonging to the subplots. */
    private List subplotInfo;
    
    /** The entities. */
    //private EntityCollection entities;  
      
    /**
     * Default constructor.
     * 
     * @param owner  the owner.
     */
    public PlotRenderingInfo(ChartRenderingInfo owner) {
        this.owner = owner;        
        this.dataArea = new Rectangle2D.Double();
        this.subplotInfo = new java.util.ArrayList();    
    }
    
    /**
     * Returns the owner.
     * 
     * @return The owner.
     */
    public ChartRenderingInfo getOwner() {
        return this.owner;
    }
    
    /**
     * Returns the plot area (in Java2D space).
     * 
     * @return The plot area.
     */
    public Rectangle2D getPlotArea() {
        return this.plotArea;
    }
    
    /**
     * Sets the plot area.
     * 
     * @param area  the plot area (in Java2D space)
     */
    public void setPlotArea(Rectangle2D area) {
        this.plotArea = area;
    }
    
    /**
     * Returns the plot's data area (in Java2D space).
     * 
     * @return The data area.
     */
    public Rectangle2D getDataArea() {
        return this.dataArea;
    }
    
    /**
     * Sets the data area.
     * 
     * @param area  the data area (in Java2D space).
     */
    public void setDataArea(Rectangle2D area) {
        this.dataArea = area;
    }
    
    /**
     * Adds the info for a subplot.
     * 
     * @param info  the subplot info.
     */
    public void addSubplotInfo(PlotRenderingInfo info) {
        this.subplotInfo.add(info);    
    }
    
    /**
     * Returns the info for a subplot.
     * 
     * @param index  the subplot index.
     * 
     * @return The info.
     */
    public PlotRenderingInfo getSubplotInfo(int index) {
        return (PlotRenderingInfo) this.subplotInfo.get(index);
    }
    
}
