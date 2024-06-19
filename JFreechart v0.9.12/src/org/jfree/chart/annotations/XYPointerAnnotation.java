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
 * ------------------------
 * XYPointerAnnotation.java
 * ------------------------
 * (C) Copyright 2003 by Object Refinery Limited.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited);
 * Contributor(s):   -;
 *
 * $Id: XYPointerAnnotation.java,v 1.1 2007/10/10 19:12:33 vauchers Exp $
 *
 * Changes:
 * --------
 * 21-May-2003 : Version 1 (DG);
 * 10-Jun-2003 : Changed BoundsAnchor to TextAnchor (DG);
 * 02-Jul-2003 : Added accessor methods and simplified constructor (DG);
 * 19-Aug-2003 : Implemented Cloneable (DG);
 *
 */

package org.jfree.chart.annotations;

import java.awt.Graphics2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.io.Serializable;

import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.ui.RectangleEdge;
import org.jfree.ui.RefineryUtilities;

/**
 * An arrow and label that can be placed on an {@link org.jfree.chart.plot.XYPlot}.  The arrow is
 * drawn at a user-definable angle so that it points towards the (x, y) location for the 
 * annotation.  
 * <p>
 * The arrow length (and its offset from the (x, y) location) is controlled by the
 * tip radius and the base radius attributes.  Imagine two circles around the (x, y) coordinate: 
 * the inner circle defined by the tip radius, and the outer circle defined by the base radius.  
 * Now, draw the arrow starting at some point on the outer circle (the point is determined by the
 * angle), with the arrow tip being drawn at a corresponding point on the inner circle.
 * <p>
 * See the <code>MarkerDemo1.java</code> source file in the JFreeChart distribution for an example.
 *
 * @author David Gilbert
 */
public class XYPointerAnnotation extends XYTextAnnotation 
                                 implements XYAnnotation, Cloneable, Serializable {

    /** The default tip radius (in Java2D units). */
    public static final double DEFAULT_TIP_RADIUS = 10.0;
    
    /** The default base radius (in Java2D units). */
    public static final double DEFAULT_BASE_RADIUS = 30.0;
    
    /** The default label offset (in Java2D units). */
    public static final double DEFAULT_LABEL_OFFSET = 3.0;
    
    /** The default arrow length (in Java2D units). */
    public static final double DEFAULT_ARROW_LENGTH = 5.0;

    /** The default arrow width (in Java2D units). */
    public static final double DEFAULT_ARROW_WIDTH = 3.0;
    
    /** The angle of the arrow's line (in radians). */
    private double angle;

    /** The radius from the (x, y) point to the tip of the arrow (in Java2D units). */
    private double tipRadius;

    /** The radius from the (x, y) point to the start of the arrow line (in Java2D units). */
    private double baseRadius;

    /** The length of the arrow head (in Java2D units). */
    private double arrowLength;

    /** The arrow width (in Java2D units, per side). */
    private double arrowWidth;

    /** The radius from the base point to the anchor point for the label. */
    private double labelOffset;

    /**
     * Creates a new label and arrow annotation.
     *
     * @param label  the label.
     * @param x  the x-coordinate (measured against the chart's domain axis).
     * @param y  the y-coordinate (measured against the chart's range axis).
     * @param angle  the angle of the arrow's line (in radians).
     */
    public XYPointerAnnotation(String label, double x, double y, double angle) {

        super(label, x, y);
        this.angle = angle;
        this.tipRadius = DEFAULT_TIP_RADIUS;
        this.baseRadius = DEFAULT_BASE_RADIUS;
        this.arrowLength = DEFAULT_ARROW_LENGTH;
        this.arrowWidth = DEFAULT_ARROW_WIDTH;
        this.labelOffset = DEFAULT_LABEL_OFFSET;

    }
    
    /**
     * Returns the tip radius.
     * 
     * @return The tip radius.
     */
    public double getTipRadius() {
        return this.tipRadius;
    }
    
    /**
     * Sets the tip radius.
     * 
     * @param radius  the radius.
     */
    public void setTipRadius(double radius) {
        this.tipRadius = radius;
    }
    
    /**
     * Sets the base radius.
     * 
     * @return The base radius.
     */
    public double getBaseRadius() {
        return this.baseRadius;
    }
    
    /**
     * Sets the base radius.
     * 
     * @param radius The radius.
     */
    public void setBaseRadius(double radius) {
        this.baseRadius = radius;
    }

    /**
     * Sets the label offset.
     * 
     * @return The label offset.
     */
    public double getLabelOffset() {
        return this.labelOffset;
    }
    
    /**
     * Sets the label offset (from the arrow base, continuing in a straight line, in Java2D units).
     * 
     * @param offset  the offset.
     */
    public void setLabelOffset(double offset) {
        this.labelOffset = offset;
    }
    
    /**
     * Returns the arrow length.
     * 
     * @return The arrow length.
     */
    public double getArrowLength() {
        return this.arrowLength;
    }
    
    /**
     * Sets the arrow length.
     * 
     * @param length  the length.
     */
    public void setArrowLength(double length) {
        this.arrowLength = length;
    }

    /**
     * Returns the arrow width.
     * 
     * @return The arrow width.
     */
    public double getArrowWidth() {
        return this.arrowWidth;
    }
    
    /**
     * Sets the arrow width.
     * 
     * @param width  the width.
     */
    public void setArrowWidth(double width) {
        this.arrowWidth = width;
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
        double j2DX = domainAxis.translateValueToJava2D(getX(), dataArea, domainEdge);
        double j2DY = rangeAxis.translateValueToJava2D(getY(), dataArea, rangeEdge);

        double startX = j2DX + Math.cos(this.angle) * this.baseRadius;
        double startY = j2DY + Math.sin(this.angle) * this.baseRadius;

        double endX = j2DX + Math.cos(this.angle) * this.tipRadius;
        double endY = j2DY + Math.sin(this.angle) * this.tipRadius;

        double arrowBaseX = endX + Math.cos(this.angle) * this.arrowLength;
        double arrowBaseY = endY + Math.sin(this.angle) * this.arrowLength;

        double arrowLeftX = arrowBaseX + Math.cos(this.angle + Math.PI / 2.0) * this.arrowWidth;
        double arrowLeftY = arrowBaseY + Math.sin(this.angle + Math.PI / 2.0) * this.arrowWidth;

        double arrowRightX = arrowBaseX - Math.cos(this.angle + Math.PI / 2.0) * this.arrowWidth;
        double arrowRightY = arrowBaseY - Math.sin(this.angle + Math.PI / 2.0) * this.arrowWidth;

        GeneralPath arrow = new GeneralPath();
        arrow.moveTo((float) endX, (float) endY);
        arrow.lineTo((float) arrowLeftX, (float) arrowLeftY);
        arrow.lineTo((float) arrowRightX, (float) arrowRightY);
        arrow.closePath();

        Line2D line = new Line2D.Double(startX, startY, endX, endY);
        g2.draw(line);
        g2.fill(arrow);

        double labelX = j2DX + Math.cos(this.angle) * (this.baseRadius + this.labelOffset);
        double labelY = j2DY + Math.sin(this.angle) * (this.baseRadius + this.labelOffset);
        g2.setFont(getFont());
        g2.setPaint(getPaint());
        RefineryUtilities.drawAlignedString(
            getText(), 
            g2, 
            (float) labelX, 
            (float) labelY,
            getTextAnchor()
        );

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
        
        if (object instanceof XYPointerAnnotation) {
        
            XYPointerAnnotation a = (XYPointerAnnotation) object;
            boolean b0 = (this.angle == a.angle);
            boolean b1 = (this.tipRadius == a.tipRadius);
            boolean b2 = (this.baseRadius == a.baseRadius);
            boolean b3 = (this.arrowLength == a.arrowLength);
            boolean b4 = (this.arrowWidth == a.arrowWidth);
            boolean b5 = (this.labelOffset == a.labelOffset);
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

}
