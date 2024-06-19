/* =======================================
 * JFreeChart : a Java Chart Class Library
 * =======================================
 *
 * Project Info:  http://www.object-refinery.com/jfreechart
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
 * ------------------
 * CrosshairInfo.java
 * ------------------
 * (C) Copyright 2002, by Simba Management Limited.
 *
 * Original Author:  David Gilbert (for Simba Management Limited);
 * Contributor(s):   -;
 *
 * $Id: CrosshairInfo.java,v 1.1 2007/10/10 18:57:57 vauchers Exp $
 *
 * Changes
 * -------
 * 24-Jan-2002 : Version 1 (DG);
 * 05-Mar-2002 : Added Javadoc comments (DG);
 *
 */

package com.jrefinery.chart;

/**
 * Maintains information about crosshairs on a plot.
 */
public class CrosshairInfo {

    /** The x-value for the anchor point. */
    protected double anchorX;

    /** The y-value for the anchor point. */
    protected double anchorY;

    /** The x-value for the crosshair point. */
    protected double crosshairX;

    /** The y-value for the crosshair point. */
    protected double crosshairY;

    /** The smallest distance so far between the anchor point and a data point. */
    protected double distance;

    /**
     * Default constructor.
     */
    public CrosshairInfo() {
    }

    /**
     * Sets the distance.
     */
    public void setCrosshairDistance(double distance) {
        this.distance = distance;
    }

    /**
     * Evaluates a data point and if it is the closest to the anchor point it becomes the new
     * crosshair point.
     * <P>
     * To understand this method, you need to know the context in which it will be called.  An
     * instance of this class is passed to an XYItemRenderer as each data point is plotted.  As the
     * point is plotted, it is passed to this method to see if it should be the new crosshair point.
     */
    public void updateCrosshairPoint(double candidateX, double candidateY) {

        double d = (candidateX-anchorX)*(candidateX-anchorX) +
                   (candidateY-anchorY)*(candidateY-anchorY);
        if (d < distance) {
            crosshairX = candidateX;
            crosshairY = candidateY;
            distance = d;
        }

    }

    /**
     * Evaluates an x-value and if it is the closest to the anchor point it becomes the new
     * crosshair point.
     * <P>
     * Used in cases where only the x-axis is numerical.
     */
    public void updateCrosshairX(double candidateX) {

        double d = Math.abs(candidateX-anchorX);
        if (d < distance) {
            crosshairX = candidateX;
            distance = d;
        }

    }

    /**
     * Evaluates a y-value and if it is the closest to the anchor point it becomes the new
     * crosshair point.
     * <P>
     * Used in cases where only the y-axis is numerical.
     */
    public void updateCrosshairY(double candidateY) {

        double d = Math.abs(candidateY-anchorY);
        if (d < distance) {
            crosshairY = candidateY;
            distance = d;
        }

    }

    /**
     * Sets the x-value for the anchor point.
     */
    public void setAnchorX(double x) {
        this.anchorX = x;
    }

    /**
     * Sets the y-value for the anchor point.
     */
    public void setAnchorY(double y) {
        this.anchorY = y;
    }

    /**
     * Sets the x-value for the crosshair point.
     */
    public double getCrosshairX() {
        return this.crosshairX;
    }

    /**
     * Sets the y-value for the crosshair point.
     */
    public double getCrosshairY() {
        return this.crosshairY;
    }

}