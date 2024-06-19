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
 * ------------------
 * CrosshairInfo.java
 * ------------------
 * (C) Copyright 2002, by Simba Management Limited.
 *
 * Original Author:  David Gilbert (for Simba Management Limited);
 * Contributor(s):   -;
 *
 * $Id: CrosshairInfo.java,v 1.1 2007/10/10 18:56:25 vauchers Exp $
 *
 * Changes
 * -------
 * 24-Jan-2002 : Version 1 (DG);
 *
 */

package com.jrefinery.chart;

public class CrosshairInfo {

    protected double anchorX;

    protected double anchorY;

    protected double crosshairX;

    protected double crosshairY;

    protected double distance;

    public CrosshairInfo() {
    }

    public void setCrosshairDistance(double distance) {
        this.distance = distance;
    }

    public void updateCrosshairPoint(double candidateX, double candidateY) {

        double d = (candidateX-anchorX)*(candidateX-anchorX) +
                   (candidateY-anchorY)*(candidateY-anchorY);
        if (d < distance) {
            crosshairX = candidateX;
            crosshairY = candidateY;
            distance = d;
        }

    }

    public void updateCrosshairX(double candidateX) {

        double d = Math.abs(candidateX-anchorX);
        if (d < distance) {
            crosshairX = candidateX;
            distance = d;
        }

    }

    public void updateCrosshairY(double candidateY) {

        double d = Math.abs(candidateY-anchorY);
        if (d < distance) {
            crosshairY = candidateY;
            distance = d;
        }

    }

    public void setAnchorX(double x) {
        this.anchorX = x;
    }

    public void setAnchorY(double y) {
        this.anchorY = y;
    }

    public double getCrosshairX() {
        return this.crosshairX;
    }

    public double getCrosshairY() {
        return this.crosshairY;
    }
}