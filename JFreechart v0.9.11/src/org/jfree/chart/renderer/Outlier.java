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
 * ------------
 * Outlier.java
 * ------------
 * (C) Copyright 2003, by David Browning and Contributors.
 *
 * Original Author:  David Browning;
 * Contributor(s):   -;
 *
 * $Id: Outlier.java,v 1.1 2007/10/10 19:09:11 vauchers Exp $
 *
 * Changes
 * -------
 * 05-Aug-2003 : Version 1, contributed by David Browning (DG);
 *
 */
 
package org.jfree.chart.renderer;

import java.awt.geom.Point2D;

/**
 * Represents one outlier in the box and whisker plot.
 * <P>
 * All the coordinates in this class are in Java2D space.
 *
 * @author David Browning
 */
public class Outlier implements Comparable {

    /* A <code>Point2D</code> representing the xy coordinates of the
     * bounding box containing the outlier ellipse
     */
    private Point2D.Double point;

    /* a <code>double</code> being the radius of the ellipse */
    private double radius;

    /**
     * Constructs an outlier item consisting of a point and the radius of the outlier ellipse
     *
     * @param xCoord  a <code>double</code> being the x coordinate of the point.
     * @param yCoord  a <code>double</code> being the y coordinate of the point.
     * @param radius  a <code>double</code> being the radius of the ellipse.
     */
    public Outlier(double xCoord, double yCoord, double radius) {
        this.point = new Point2D.Double(xCoord - radius, yCoord - radius);
        this.radius = radius;
    }

    /**
     * Returns a <code>Point2D</code> representing the xy coordinates of the
     * bounding box containing the outlier ellipse.
     *
     * @return the <code>Point2D</code>.
     */
    public Point2D.Double getPoint() {
        return point;
    }

    /**
     * Sets the <code>Point2D</code> representing the xy coordinates of the
     * bounding box containing the outlier ellipse.
     *
     * @param point  the new <code>Point2D</code> point.
     */
    public void setPoint(Point2D.Double point) {
        this.point = point;
    }

    /**
     * Returns a <code>double</code> representing the x coordinate of the
     * bounding box containing the outlier ellipse.
     *
     * @return the <code>double</code> being the x coordinate.
     */
    public double getX() {
        return this.getPoint().getX();
    }

    /**
     * Returns a <code>double</code> representing the y coordinate of the
     * bounding box containing the outlier ellipse.
     *
     * @return the <code>double</code> being the y coordinate.
     */
    public double getY() {
        return this.getPoint().getY();
    }

    /**
     * Returns  the radius of the outlier ellipse.
     *
     * @return the <code>double</code> being the radius.
     */
    public double getRadius() {
        return radius;
    }

    /**
     * Sets the radius of the outlier ellipse.
     *
     * @param radius  the <code>double</code> representing the new radius.
     */
    public void setRadius(double radius) {
        this.radius = radius;
    }

    /**
     * Compares this object with the specified object for order, based on
     * the outlier's point.
     *
     * @param   o the Object to be compared.
     * @return  a negative integer, zero, or a positive integer as this object
     *      is less than, equal to, or greater than the specified object.
     *
     * @throws ClassCastException if the specified object's type prevents it
     *         from being compared to this Object.
     */
    public int compareTo(Object o) {
        Outlier outlier = (Outlier)o;
        Point2D.Double p1 = this.getPoint();
        Point2D.Double p2 = outlier.getPoint();
        if (p1.equals(p2)) return 0;
        else if ((p1.getX() < p2.getX()) || (p1.getY() < p2.getY())) return -1;
        else return 1;
    }

    /**
     * Returns a true if outlier is overlapped and false if it is not.
     * Overlapping is determined by the respective bounding boxes plus
     * a small margin.
     *
     * @return  a <code>boolean</code> indicating whether or not an overlap has occured.
     */
    public boolean overlaps(Object o) {
        if (o instanceof Outlier) {
            Outlier outlier = (Outlier)o;
            return ((outlier.getX() >= this.getX() - (radius * 1.1)) &&
                (outlier.getX() <= this.getX() + (radius * 1.1)) &&
                (outlier.getY() >= this.getY() - (radius * 1.1)) &&
                (outlier.getY() <= this.getY() + (radius * 1.1)));
        } 
        else {
            new NotOutlierException("Not an outlier!");
            return false;
        }
    }

    /**
     * Returns a textual representation of the outlier.
     *
     * @return  a <code>String</code> representing the outlier.
     */
    public String toString() {
        return "{" + this.getX() + "," + this.getY() + "}";
    }

}
