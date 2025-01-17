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
 * -------------------------
 * PieItemRendererState.java
 * -------------------------
 * (C) Copyright 2004, by Object Refinery Limited.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited);
 * Contributor(s):   -;
 *
 * $Id: PiePlotState.java,v 1.1 2007/10/10 19:29:15 vauchers Exp $
 *
 * Changes
 * -------
 * 06-Mar-2004 : Version 1 (DG);
 *
 */

package org.jfree.chart.plot;

import java.awt.geom.Rectangle2D;

import org.jfree.chart.renderer.RendererState;

/**
 * A renderer state.
 */
public class PiePlotState extends RendererState {

    /** The number of passes required by the renderer. */
    private int passesRequired;
    
    /** The total of the values in the dataset. */
    private double total;
    
    /** The latest angle. */
    private double latestAngle;
    
    /** The exploded pie area. */
    private Rectangle2D explodedPieArea;
   
    /** The pie area. */
    private Rectangle2D pieArea;
    
    /** The center of the pie in Java 2D coordinates. */
    private double pieCenterX;
   
    /** The center of the pie in Java 2D coordinates. */
    private double pieCenterY;
   
    /** The vertical link radius. */
    private double linkHRadius;
   
    /** The horizontal link radius. */
    private double linkWRadius;
    
    /** The vertical pie radius. */
    private double pieHRadius;
   
    /** The horizontal pie radius. */
    private double pieWRadius;
    
    /** The link area. */
    private Rectangle2D linkArea;

    /**
     * Creates a new object for recording temporary state information for a renderer.
     * 
     * @param info  the plot rendering info.
     */
    public PiePlotState(PlotRenderingInfo info) {
        super(info);
        this.passesRequired = 1;
        this.total = 0.0;
    }
    
    /**
     * Returns the number of passes required by the renderer.
     * 
     * @return the number of passes.
     */
    public int getPassesRequired() {
        return this.passesRequired;   
    }
    
    /**
     * Sets the number of passes required by the renderer.
     * 
     * @param passes  the passes.
     */
    public void setPassesRequired(int passes) {
        this.passesRequired = passes;   
    }
    
    /**
     * Returns the total of the values in the dataset.
     * 
     * @return the total.
     */
    public double getTotal() {
        return this.total;
    }
    
    /**
     * Sets the total.
     * 
     * @param total  the total.
     */
    public void setTotal(double total) {
        this.total = total;
    }
    
    /**
     * Returns the latest angle.
     * 
     * @return the latest angle.
     */
    public double getLatestAngle() {
        return this.latestAngle;   
    }
    
    /**
     * Sets the latest angle.
     * 
     * @param angle  the angle.
     */
    public void setLatestAngle(double angle) {
        this.latestAngle = angle;   
    }
    
    /**
     * Returns the pie area.
     * 
     * @return the pie area.
     */
    public Rectangle2D getPieArea() {
        return this.pieArea;   
    }
    
    /**
     * Sets the pie area.
     * 
     * @param area  the area.
     */
    public void setPieArea(Rectangle2D area) {
       this.pieArea = area;   
    }
    
    /**
     * Returns the exploded pie area.
     * 
     * @return the exploded pie area.
     */
    public Rectangle2D getExplodedPieArea() {
        return this.explodedPieArea;   
    }
    
    /**
     * Sets the exploded pie area.
     * 
     * @param area  the area.
     */
    public void setExplodedPieArea(Rectangle2D area) {
        this.explodedPieArea = area;   
    }
    
    /**
     * Returns the x-coordinate of the center of the pie chart.
     * 
     * @return the x-coordinate (in Java2D space).
     */
    public double getPieCenterX() {
        return this.pieCenterX;   
    }
    
    /**
     * Sets the x-coordinate of the center of the pie chart.
     * 
     * @param x  the x-coordinate (in Java2D space).
     */
    public void setPieCenterX(double x) {
        this.pieCenterX = x;   
    }
    
    /**
     * Returns the y-coordinate of the center of the pie chart.
     * 
     * @return the y-coordinate (in Java2D space).
     */
    public double getPieCenterY() {
        return this.pieCenterY;   
    }
    
    /**
     * Sets the y-coordinate of the center of the pie chart.
     * 
     * @param y  the y-coordinate (in Java2D space).
     */
    public void setPieCenterY(double y) {
        this.pieCenterY = y;   
    }
    
    /**
     * Returns the vertical link radius.
     * 
     * @return the radius.
     */
    public double getLinkHRadius() {
        return this.linkHRadius;   
    }
    
    /**
     * Sets the vertical link radius.
     * 
     * @param radius  the radius.
     */
    public void setLinkHRadius(double radius) {
        this.linkHRadius = radius;   
    }
    
    /**
     * Returns the horizontal link radius.
     * 
     * @return the radius.
     */
    public double getLinkWRadius() {
        return this.linkWRadius;   
    }
    
    /**
     * Sets the horizontal link radius.
     * 
     * @param radius  the radius.
     */
    public void setLinkWRadius(double radius) {
        this.linkWRadius = radius;   
    }

    /**
     * Returns the link area.
     * 
     * @return the link area.
     */
    public Rectangle2D getLinkArea() {
        return this.linkArea;   
    }
    
    /**
     * Sets the link area.
     * 
     * @param area  the area.
     */
    public void setLinkArea(Rectangle2D area) {
        this.linkArea = area;   
    }

    /**
     * Returns the vertical pie radius.
     * 
     * @return the radius.
     */
    public double getPieHRadius() {
        return this.pieHRadius;   
    }
    
    /**
     * Sets the vertical pie radius.
     * 
     * @param radius  the radius.
     */
    public void setPieHRadius(double radius) {
        this.pieHRadius = radius;   
    }
    
    /**
     * Returns the horizontal pie radius.
     * 
     * @return the radius.
     */
    public double getPieWRadius() {
        return this.pieWRadius;   
    }
    
    /**
     * Sets the horizontal pie radius.
     * 
     * @param radius  the radius.
     */
    public void setPieWRadius(double radius) {
        this.pieWRadius = radius;   
    }
   
}
