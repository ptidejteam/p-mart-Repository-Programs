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
 * ---------------------
 * XYLineAnnotation.java
 * ---------------------
 * (C) Copyright 2003 by Object Refinery Limited.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited);
 * Contributor(s):   -;
 *
 * $Id: XYLineAnnotation.java,v 1.1 2007/10/10 19:19:10 vauchers Exp $
 *
 * Changes:
 * --------
 * 02-Apr-2003 : Version 1 (DG);
 * 19-Aug-2003 : Added equals method, implemented Cloneable, and applied serialization fixes (DG);
 *
 */

package org.jfree.chart.annotations;

import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Stroke;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.io.SerialUtilities;
import org.jfree.ui.RectangleEdge;
import org.jfree.util.ObjectUtils;

/**
 * A simple line annotation that can be placed on an {@link XYPlot}.
 *
 * @author David Gilbert
 */
public class XYLineAnnotation implements XYAnnotation, Cloneable, Serializable {

    /** The x-coordinate. */
    private double x1;

    /** The y-coordinate. */
    private double y1;

    /** The x-coordinate. */
    private double x2;

    /** The y-coordinate. */
    private double y2;

    /** The line stroke. */
    private transient Stroke stroke;

    /** The line color. */
    private transient Paint paint;


    /**
     * Creates a new annotation to be displayed at the given coordinates.
     *
     * @param x1  the x-coordinate for the start of the line.
     * @param y1  the y-coordinate for the start of the line.
     * @param x2  the x-coordinate for the end of the line.
     * @param y2  the y-coordinate for the end of the line.
     * @param stroke  the line stroke.
     * @param paint  the line color.
     */
    public XYLineAnnotation(double x1, double y1, double x2, double y2,
                            Stroke stroke, Paint paint) {

        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;
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
        float j2DX1 = (float) domainAxis.translateValueToJava2D(this.x1, dataArea, domainEdge);
        float j2DY1 = (float) rangeAxis.translateValueToJava2D(this.y1, dataArea, rangeEdge);
        float j2DX2 = (float) domainAxis.translateValueToJava2D(this.x2, dataArea, domainEdge);
        float j2DY2 = (float) rangeAxis.translateValueToJava2D(this.y2, dataArea, rangeEdge);

        g2.setPaint(this.paint);
        g2.setStroke(this.stroke);
        Line2D line = new Line2D.Float(j2DX1, j2DY1, j2DX2, j2DY2);
        g2.draw(line);

    }

    /**
     * Tests this annotation for equality with an object.
     * 
     * @param object  the object to test against.
     * 
     * @return <code>true</code> or <code>false</code>.
     */
    public boolean equals(Object object) {
        
        if (object == null) {
            return false;
        }
        
        if (object == this) {
            return true;
        }
        
        if (object instanceof XYLineAnnotation) {
        
            XYLineAnnotation a = (XYLineAnnotation) object;
            boolean b0 = (this.x1 == a.x1);
            boolean b1 = (this.y1 == a.y1);
            boolean b2 = (this.x2 == a.x2);
            boolean b3 = (this.y2 == a.y2);
            boolean b4 = ObjectUtils.equal(this.paint, a.paint);
            boolean b5 = ObjectUtils.equal(this.stroke, a.stroke);
            return b0 && b1 && b2 && b3 && b4 && b5;
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
    
    /**
     * Provides serialization support.
     *
     * @param stream  the output stream.
     *
     * @throws IOException  if there is an I/O error.
     */
    private void writeObject(ObjectOutputStream stream) throws IOException {

        stream.defaultWriteObject();
        SerialUtilities.writePaint(this.paint, stream);
        SerialUtilities.writeStroke(this.stroke, stream);

    }

    /**
     * Provides serialization support.
     *
     * @param stream  the input stream.
     *
     * @throws IOException  if there is an I/O error.
     * @throws ClassNotFoundException  if there is a classpath problem.
     */
    private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {

        stream.defaultReadObject();
        this.paint = SerialUtilities.readPaint(stream);
        this.stroke = SerialUtilities.readStroke(stream);

    }

}
