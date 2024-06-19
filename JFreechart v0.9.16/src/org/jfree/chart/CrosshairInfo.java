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
 * ------------------
 * CrosshairInfo.java
 * ------------------
 * (C) Copyright 2002-2004, by Object Refinery Limited.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited);
 * Contributor(s):   -;
 *
 * $Id: CrosshairInfo.java,v 1.1 2007/10/10 19:25:38 vauchers Exp $
 *
 * Changes
 * -------
 * 24-Jan-2002 : Version 1 (DG);
 * 05-Mar-2002 : Added Javadoc comments (DG);
 * 26-Sep-2002 : Fixed errors reported by Checkstyle (DG);
 * 19-Sep-2003 : Modified crosshair distance calculation (DG);
 * 04-Dec-2003 : Crosshair anchor point now stored outside chart since it is
                 dependent on the display target (DG);
 *
 */

package org.jfree.chart;

import java.awt.geom.Point2D;

/**
 * Maintains information about crosshairs on a plot.
 *
 * @author David Gilbert
 */
public class CrosshairInfo {

    /** A flag that controls whether the distance is calculated in data space or Java2D space. */
    private boolean calculateDistanceInDataSpace = false;

    /** The x-value for the anchor point. */
    private double anchorX;

    /** The y-value for the anchor point. */
    private double anchorY;
    
    /** The anchor point in Java2D space - if null, don't update crosshair. */
    private Point2D anchor;
    
    /** The x-value for the crosshair point. */
    private double crosshairX;

    /** The y-value for the crosshair point. */
    private double crosshairY;

    /** The smallest distance so far between the anchor point and a data point. */
    private double distance;

    /**
     * Default constructor.
     */
    public CrosshairInfo() {
    }

    /**
     * Creates a new info object.
     * 
     * @param calculateDistanceInDataSpace  a flag that controls whether the distance is calculated
     *                                      in data space or Java2D space.
     */
    public CrosshairInfo(boolean calculateDistanceInDataSpace) {
        this.calculateDistanceInDataSpace = calculateDistanceInDataSpace;
    }

    /**
     * Sets the distance.
     *
     * @param distance  the distance.
     */
    public void setCrosshairDistance(double distance) {
        this.distance = distance;
    }

    /**
     * Evaluates a data point and if it is the closest to the anchor point it
     * becomes the new crosshair point.
     * <P>
     * To understand this method, you need to know the context in which it will
     * be called.  An instance of this class is passed to an XYItemRenderer as
     * each data point is plotted.  As the point is plotted, it is passed to
     * this method to see if it should be the new crosshair point.
     *
     * @param dataX  x position of candidate for the new crosshair point.
     * @param dataY  y position of candidate for the new crosshair point.
     * @param viewX  x in Java2D space.
     * @param viewY  y in Java2D space.
     */
    public void updateCrosshairPoint(double dataX, double dataY, double viewX, double viewY) {

        if (this.anchor != null) {
            double d = 0.0;
            if (this.calculateDistanceInDataSpace) {
                d = (dataX - this.anchorX) * (dataX - this.anchorX)
                  + (dataY - this.anchorY) * (dataY - this.anchorY);
            }
            else {
                double xx = this.anchor.getX();
                double yy = this.anchor.getY();
                d = (viewX - xx) * (viewX - xx)
                  + (viewY - yy) * (viewY - yy);            
            }

            if (d < distance) {
                this.crosshairX = dataX;
                this.crosshairY = dataY;
                this.distance = d;
            }
        }

    }

    /**
     * Evaluates an x-value and if it is the closest to the anchor point it
     * becomes the new crosshair point.
     * <P>
     * Used in cases where only the x-axis is numerical.
     *
     * @param candidateX  x position of the candidate for the new crosshair point.
     */
    public void updateCrosshairX(double candidateX) {

        double d = Math.abs(candidateX - anchorX);
        if (d < distance) {
            crosshairX = candidateX;
            distance = d;
        }

    }

    /**
     * Evaluates a y-value and if it is the closest to the anchor point it
     * becomes the new crosshair point.
     * <P>
     * Used in cases where only the y-axis is numerical.
     *
     * @param candidateY  y position of the candidate for the new crosshair point.
     */
    public void updateCrosshairY(double candidateY) {

        double d = Math.abs(candidateY - anchorY);
        if (d < distance) {
            crosshairY = candidateY;
            distance = d;
        }

    }

    /**
     * Set the x-value for the anchor point.
     *
     * @param x  the x position.
     */
    public void setAnchorX(double x) {
        this.anchorX = x;
        this.crosshairX = x;
    }

    /**
     * Set the y-value for the anchor point.
     *
     * @param y  the y position.
     */
    public void setAnchorY(double y) {
        this.anchorY = y;
        this.crosshairY = y;
    }

    /** 
     * Sets the anchor point.
     * 
     * @param anchor  the anchor point.
     */
    public void setAnchor(Point2D anchor) {
        this.anchor = anchor;
    }
    
    /**
     * Get the x-value for the crosshair point.
     *
     * @return the x position of the crosshair point.
     */
    public double getCrosshairX() {
        return this.crosshairX;
    }
    
    /**
     * Sets the x coordinate for the crosshair.
     * 
     * @param x  the coordinate.
     */
    public void setCrosshairX(double x) {
        this.crosshairX = x;
    }

    /**
     * Get the y-value for the crosshair point.
     *
     * @return the y position of the crosshair point.
     */
    public double getCrosshairY() {
        return this.crosshairY;
    }

    /**
     * Sets the y coordinate for the crosshair.
     * 
     * @param y  the y coordinate.
     */
    public void setCrosshairY(double y) {
        this.crosshairY = y;
    }


}
