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
 *  ---------------------
 *  XYShapeAnnotation.java
 *  ---------------------
 * (C) Copyright 2003, 2004, by Ondax, Inc. and Contributors.
 *
 * Original Author:  Greg Steckman (for Ondax, Inc.);
 * Contributor(s):   -;
 *
 * $Id: XYShapeAnnotation.java,v 1.1 2007/10/10 19:25:38 vauchers Exp $
 *
 * Changes:
 * --------
 * 15-Aug-2003 : Version 1, adapted from org.jfree.chart.annotations.XYLineAnnotation (GS);
 *
 */
 
package org.jfree.chart.annotations;

import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.io.Serializable;

import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.ui.RectangleEdge;

/**
 * A simple <code>Shape</code> annotation that can be placed on an {@link XYPlot}.
 *
 * @author Greg Steckman
 */
public class XYShapeAnnotation implements XYAnnotation, Serializable {
    
    /** The Shape to draw. */
    private Shape shape;

    /** The shape's stroke. */
    private Stroke stroke;

    /** The shape's color. */
    private Paint paint;

    /**
     * Creates a new annotation to be displayed.
     *
     * @param shape  the shape.
     * @param stroke  the shape stroke.
     * @param paint  the shape color.
     */
    public XYShapeAnnotation(Shape shape, Stroke stroke, Paint paint) {
        this.shape = shape;
        this.stroke = stroke;
        this.paint = paint;
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
        RectangleEdge domainEdge = Plot.resolveDomainAxisLocation(plot.getDomainAxisLocation(),
                                                                  orientation);
        RectangleEdge rangeEdge = Plot.resolveRangeAxisLocation(plot.getRangeAxisLocation(),
                                                                orientation);

        //compute transform matrix elements via sample points. Assume no rotation or shear.
        // x-axis translation
        double m02 = domainAxis.translateValueToJava2D(0, dataArea, domainEdge); 
        // y-axis translation
        double m12 = rangeAxis.translateValueToJava2D(0, dataArea, rangeEdge);
        // x-axis scale 
        double m00 = domainAxis.translateValueToJava2D(1, dataArea, domainEdge) - m02; 
        // y-axis scale
        double m11 = rangeAxis.translateValueToJava2D(1, dataArea, rangeEdge) - m12; 

        //create transform & transform shape
        AffineTransform newTransform = new AffineTransform(m00, 0, 0, m11, m02, m12);
        Shape newShape = newTransform.createTransformedShape(shape);

        g2.setPaint(this.paint);
        g2.setStroke(this.stroke);
        g2.draw(newShape);
    }
    
}
