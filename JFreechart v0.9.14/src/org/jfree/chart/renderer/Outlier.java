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
 * Original Author:  David Browning (for Australian Institute of Marine Science);
 * Contributor(s):   David Gilbert (for Object Refinery Limited);
 *
 * $Id: Outlier.java,v 1.1 2007/10/10 19:19:08 vauchers Exp $
 *
 * Changes
 * -------
 * 05-Aug-2003 : Version 1, contributed by David Browning (DG);
 * 28-Aug-2003 : Minor tidy-up (DG);
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

    /** The xy coordinates of the bounding box containing the outlier ellipse. */
    private Point2D point;

    /** The radius of the ellipse */
    private double radius;

    /**
     * Constructs an outlier item consisting of a point and the radius of the outlier ellipse
     *
     * @param xCoord  the x coordinate of the point.
     * @param yCoord  the y coordinate of the point.
     * @param radius  the radius of the ellipse.
     */
    public Outlier(double xCoord, double yCoord, double radius) {
        this.point = new Point2D.Double(xCoord - radius, yCoord - radius);
        this.radius = radius;
    }

    /**
     * Returns the xy coordinates of the bounding box containing the outlier ellipse.
     *
     * @return The location of the outlier ellipse.
     */
    public Point2D getPoint() {
        return point;
    }

    /**
     * Sets the xy coordinates of the bounding box containing the outlier ellipse.
     *
     * @param point  the location.
     */
    public void setPoint(Point2D point) {
        this.point = point;
    }

    /**
     * Returns the x coordinate of the bounding box containing the outlier ellipse.
     *
     * @return The x coordinate.
     */
    public double getX() {
        return getPoint().getX();
    }

    /**
     * Returns the y coordinate of the bounding box containing the outlier ellipse.
     *
     * @return The y coordinate.
     */
    public double getY() {
        return getPoint().getY();
    }

    /**
     * Returns  the radius of the outlier ellipse.
     *
     * @return The radius.
     */
    public double getRadius() {
        return radius;
    }

    /**
     * Sets the radius of the outlier ellipse.
     *
     * @param radius  the new radius.
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
     */
    public int compareTo(Object o) {
        Outlier outlier = (Outlier) o;
        Point2D p1 = getPoint();
        Point2D p2 = outlier.getPoint();
        if (p1.equals(p2)) {
            return 0;
        } 
        else if ((p1.getX() < p2.getX()) || (p1.getY() < p2.getY())) {
            return -1;
        } 
        else {
            return 1;
        } 
    }

    /**
     * Returns a true if outlier is overlapped and false if it is not.
     * Overlapping is determined by the respective bounding boxes plus
     * a small margin.
     *
     * @param other  the other outlier.
     * 
     * @return A <code>boolean</code> indicating whether or not an overlap has occured.
     */
    public boolean overlaps(Outlier other) {
        return ((other.getX() >= this.getX() - (radius * 1.1)) 
                && (other.getX() <= this.getX() + (radius * 1.1)) 
                && (other.getY() >= this.getY() - (radius * 1.1)) 
                && (other.getY() <= this.getY() + (radius * 1.1)));
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
