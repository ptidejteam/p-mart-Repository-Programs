/* ===========================================================
 * JFreeChart : a free chart library for the Java(tm) platform
 * ===========================================================
 *
 * (C) Copyright 2000-2007, by Object Refinery Limited and Contributors.
 *
 * Project Info:  http://www.jfree.org/jfreechart/index.html
 *
 * This library is free software; you can redistribute it and/or modify it 
 * under the terms of the GNU Lesser General Public License as published by 
 * the Free Software Foundation; either version 2.1 of the License, or 
 * (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but 
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY 
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public 
 * License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, 
 * USA.  
 *
 * [Java is a trademark or registered trademark of Sun Microsystems, Inc. 
 * in the United States and other countries.]
 *
 * ----------------------
 * StandardDialRange.java
 * ----------------------
 * (C) Copyright 2006, 2007, by Object Refinery Limited.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited);
 * Contributor(s):   -;
 *
 * $Id: StandardDialRange.java,v 1.1 2007/10/10 20:38:38 vauchers Exp $
 *
 * Changes
 * -------
 * 03-Nov-2006 : Version 1 (DG);
 * 08-Mar-2007 : Fix in hashCode() (DG);
 * 
 */

package org.jfree.experimental.chart.plot.dial;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.geom.Arc2D;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import org.jfree.chart.HashUtilities;
import org.jfree.io.SerialUtilities;
import org.jfree.util.PaintUtilities;
import org.jfree.util.PublicCloneable;

/**
 * A layer that draws a range highlight on a dial plot.
 */
public class StandardDialRange extends AbstractDialLayer implements DialLayer, 
        Cloneable, PublicCloneable, Serializable {
    
    /** The minimum data value for the scale. */
    private double lowerBound;
    
    /** The maximum data value for the scale. */
    private double upperBound;
    
    /**
     * The increment (in data units) for each section. 
     */
    private double increment;

    /**
     * The paint used to draw the range highlight.  This field is transient
     * because it requires special handling for serialization.
     */
    private transient Paint paint;
    
    /** 
     * The factor (in the range 0.0 to 1.0) that determines the inside limit
     * of the range highlight.
     */
    private double innerRadius;

    /**
     * The factor (in the range 0.0 to 1.0) that determines the outside limit 
     * of the range highlight.
     */
    private double outerRadius;
    
    /** 
     * Creates a new instance of <code>StandardDialRange</code>.
     */
    public StandardDialRange() {
        this(0.0, 100.0, Color.white);
    }
    
    /** 
     * Creates a new instance of <code>StandardDialRange</code>.
     *
     * @param lower  the lower bound.
     * @param upper  the upper bound.
     * @param paint  the paint (<code>null</code> not permitted).
     */
    public StandardDialRange(double lower, double upper, Paint paint) {
        if (paint == null) {
            throw new IllegalArgumentException("Null 'paint' argument.");
        }
        this.lowerBound = lower;
        this.upperBound = upper;
        this.paint = paint;
    }
    
    /**
     * Returns the lower bound (a data value) of the dial range.
     * 
     * @return The lower bound of the dial range.
     * 
     * @see #setLowerBound(double)
     */
    public double getLowerBound() {
        return this.lowerBound;
    }
    
    /**
     * Sets the lower bound of the dial range.
     * 
     * @param bound  the lower bound.
     * 
     * @see #getLowerBound()
     */
    public void setLowerBound(double bound) {
        // FIXME: check
        this.lowerBound = bound;
        notifyListeners(new DialLayerChangeEvent(this));
    }
    
    /**
     * Returns the upper bound of the dial range.
     * 
     * @return The upper bound.
     * 
     * @see #setUpperBound(double)
     */
    public double getUpperBound() {
        return this.upperBound;
    }
    
    /**
     * Sets the upper bound of the dial range.
     * 
     * @param bound  the upper bound.
     * 
     * @see #getUpperBound()
     */
    public void setUpperBound(double bound) {
        // FIXME: check
        this.upperBound = bound;
        notifyListeners(new DialLayerChangeEvent(this));
    }
    
    /**
     * Returns the increment between tick marks.
     * 
     * @return The increment.
     * 
     * @see #setIncrement(double)
     */
    public double getIncrement() {
        return this.increment;
    }
    
    /**
     * Sets the increment.
     * 
     * @param increment  the increment.
     * 
     * @see #getIncrement()
     */
    public void setIncrement(double increment) {
        // FIXME: check
        this.increment = increment;
        notifyListeners(new DialLayerChangeEvent(this));
    }
    
    /**
     * Returns the paint used to highlight the range.
     * 
     * @return The paint (never <code>null</code>).
     * 
     * @see #setPaint(Paint)
     */
    public Paint getPaint() {
        return this.paint;
    }
    
    /**
     * Sets the paint used to highlight the range.
     * 
     * @param paint  the paint (<code>null</code> not permitted).
     * 
     * @see #getPaint()
     */
    public void setPaint(Paint paint) {
        if (paint == null) {
            throw new IllegalArgumentException("Null 'paint' argument.");
        }
        this.paint = paint;
        notifyListeners(new DialLayerChangeEvent(this));
    }
    
    /**
     * Returns the inner radius.
     * 
     * @return The inner radius.
     * 
     * @see #setInnerRadius(double)
     */
    public double getInnerRadius() {
        return this.innerRadius;
    }
    
    /**
     * Sets the inner radius.
     * 
     * @param radius  the radius.
     * 
     * @see #getInnerRadius()
     */
    public void setInnerRadius(double radius) {
        this.innerRadius = radius;
        notifyListeners(new DialLayerChangeEvent(this));
    }
    
    /**
     * Returns the outer radius.
     * 
     * @return The outer radius.
     * 
     * @see #setOuterRadius(double)
     */
    public double getOuterRadius() {
        return this.outerRadius;
    }
    
    /**
     * Sets the outer radius.
     * 
     * @param radius  the radius.
     * 
     * @see #getOuterRadius()
     */
    public void setOuterRadius(double radius) {
        this.outerRadius = radius;
        notifyListeners(new DialLayerChangeEvent(this));
    }
    
    /**
     * Returns <code>true</code> to indicate that this layer should be 
     * clipped within the dial window. 
     * 
     * @return <code>true</code>.
     */
    public boolean isClippedToWindow() {
        return true;
    }
    
    /**
     * Draws the range.
     * 
     * @param g2  the graphics target.
     * @param plot  the plot.
     * @param frame  the dial's reference frame (in Java2D space).
     * @param view  the dial's view rectangle (in Java2D space).
     */
    public void draw(Graphics2D g2, DialPlot plot, Rectangle2D frame, 
            Rectangle2D view) {
        
        Rectangle2D arcRectInner = DialPlot.rectangleByRadius(frame, 
                this.innerRadius, this.innerRadius);
        Rectangle2D arcRectOuter = DialPlot.rectangleByRadius(frame, 
                this.outerRadius, this.outerRadius);
        //double range = this.upperBound - this.lowerBound;
        
        DialScale scale = plot.getScaleForDataset(0);
        double angleMin = scale.valueToAngle(this.lowerBound);
        double angleMax = scale.valueToAngle(this.upperBound);

        Arc2D arcInner = new Arc2D.Double(arcRectInner, angleMin, 
                angleMax - angleMin, Arc2D.OPEN);
        Arc2D arcOuter = new Arc2D.Double(arcRectOuter, angleMax, 
                angleMin - angleMax, Arc2D.OPEN);
        
        g2.setPaint(this.paint);
        g2.setStroke(new BasicStroke(2.0f));
        g2.draw(arcInner);
        g2.draw(arcOuter);
    }
    
    /**
     * Tests this instance for equality with an arbitrary object.
     * 
     * @param obj  the object (<code>null</code> permitted).
     * 
     * @return A boolean.
     */
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof StandardDialRange)) {
            return false;
        }
        StandardDialRange that = (StandardDialRange) obj;
        if (this.lowerBound != that.lowerBound) {
            return false;
        }
        if (this.upperBound != that.upperBound) {
            return false;
        }
        if (this.increment != that.increment) {
            return false;
        }
        if (!PaintUtilities.equal(this.paint, that.paint)) {
            return false;
        }
        if (this.innerRadius != that.innerRadius) {
            return false;
        }
        if (this.outerRadius != that.outerRadius) {
            return false;
        }
        return true; 
    }

    /**
     * Returns a hash code for this instance.
     * 
     * @return The hash code.
     */
    public int hashCode() {
        int result = 193;
        long temp = Double.doubleToLongBits(this.increment);
        result = 37 * result + (int) (temp ^ (temp >>> 32));        
        temp = Double.doubleToLongBits(this.lowerBound);
        result = 37 * result + (int) (temp ^ (temp >>> 32));        
        temp = Double.doubleToLongBits(this.upperBound);
        result = 37 * result + (int) (temp ^ (temp >>> 32));        
        temp = Double.doubleToLongBits(this.innerRadius);
        result = 37 * result + (int) (temp ^ (temp >>> 32));        
        temp = Double.doubleToLongBits(this.outerRadius);
        result = 37 * result + (int) (temp ^ (temp >>> 32));        
        result = 37 * result + HashUtilities.hashCodeForPaint(this.paint);
        return result;
    }
    
    /**
     * Returns a clone of this instance.
     * 
     * @return A clone.
     * 
     * @throws CloneNotSupportedException if any of the attributes of this 
     *     instance cannot be cloned.
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
    }

    /**
     * Provides serialization support.
     *
     * @param stream  the input stream.
     *
     * @throws IOException  if there is an I/O error.
     * @throws ClassNotFoundException  if there is a classpath problem.
     */
    private void readObject(ObjectInputStream stream) 
            throws IOException, ClassNotFoundException {
        stream.defaultReadObject();
        this.paint = SerialUtilities.readPaint(stream);
    }

}
