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
 * ----------------------
 * XYImageAnnotation.java
 * ----------------------
 * (C) Copyright 2003, 2004, by Object Refinery Limited.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited);
 * Contributor(s):   -;
 *
 * $Id: XYImageAnnotation.java,v 1.1 2007/10/10 19:39:36 vauchers Exp $
 *
 * Changes:
 * --------
 * 01-Dec-2003 : Version 1 (DG);
 * 21-Jan-2004 : Update for renamed method in ValueAxis (DG);
 *
 */

package org.jfree.chart.annotations;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.Rectangle2D;
import java.io.Serializable;

import org.jfree.chart.axis.AxisLocation;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.ui.RectangleEdge;
import org.jfree.util.ObjectUtils;

/**
 * An annotation that allows an image to be placed at some location on 
 * an {@link XYPlot}.
 *
 * @author David Gilbert
 */
public class XYImageAnnotation implements XYAnnotation, Cloneable, Serializable {

    /** The x-coordinate. */
    private double x;

    /** The y-coordinate. */
    private double y;

    /** The image. */
    private Image image;

    /**
     * Creates a new annotation to be displayed within the given area.
     *
     * @param x  the x-coordinate for the area.
     * @param y  the y-coordinate for the area.
     * @param image  the image (<code>null</code> not permitted).
     */
    public XYImageAnnotation(double x, double y, Image image) {
        if (image == null) {
            throw new IllegalArgumentException("Null 'image' argument.");      
        }
        this.x = x;
        this.y = y;
        this.image = image;
    }

    /**
     * Draws the annotation.
     *
     * @param g2  the graphics device.
     * @param plot  the plot.
     * @param dataArea  the data area.
     * @param domainAxis  the domain axis.
     * @param rangeAxis  the range axis.
     */
    public void draw(Graphics2D g2, XYPlot plot, Rectangle2D dataArea,
                     ValueAxis domainAxis, ValueAxis rangeAxis) {

        PlotOrientation orientation = plot.getOrientation();
        AxisLocation domainAxisLocation = plot.getDomainAxisLocation();
        AxisLocation rangeAxisLocation = plot.getRangeAxisLocation();
        RectangleEdge domainEdge = Plot.resolveDomainAxisLocation(domainAxisLocation, orientation);
        RectangleEdge rangeEdge = Plot.resolveRangeAxisLocation(rangeAxisLocation, orientation);
        float j2DX = (float) domainAxis.valueToJava2D(this.x, dataArea, domainEdge);
        float j2DY = (float) rangeAxis.valueToJava2D(this.y, dataArea, rangeEdge);
        float xx = j2DX - this.image.getWidth(null) / 2.0f;
        float yy = j2DY - this.image.getHeight(null) / 2.0f;
        g2.drawImage(this.image, (int) xx, (int) yy, null);

    }

    /**
     * Tests this object for equality with an arbitrary object.
     * 
     * @param object  the object to test against (<code>null</code> permitted).
     * 
     * @return a boolean.
     */
    public boolean equals(Object object) {
        
        if (object == null) {
            return false;
        }
        
        if (object == this) {
            return true;
        }
        
        if (object instanceof XYImageAnnotation) {
        
            XYImageAnnotation a = (XYImageAnnotation) object;
            boolean b0 = (this.x == a.x);
            boolean b1 = (this.y == a.y);
            boolean b2 = ObjectUtils.equal(this.image, a.image);
            return b0 && b1 && b2;
        }
        
        return false;
        
    }
    
    /**
     * Returns a clone of the annotation.
     * 
     * @return A clone.
     * 
     * @throws CloneNotSupportedException  if the annotation can't be cloned.
     */
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

}
