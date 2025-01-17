/* ===========================================================
 * JFreeChart : a free chart library for the Java(tm) platform
 * ===========================================================
 *
 * (C) Copyright 2000-2006, by Object Refinery Limited and Contributors.
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
 * ---------------------
 * XYTextAnnotation.java
 * ---------------------
 * (C) Copyright 2002-2006, by Object Refinery Limited.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited);
 * Contributor(s):   -;
 *
 * $Id: XYTextAnnotation.java,v 1.1 2007/10/10 20:30:12 vauchers Exp $
 *
 * Changes:
 * --------
 * 28-Aug-2002 : Version 1 (DG);
 * 07-Nov-2002 : Fixed errors reported by Checkstyle (DG);
 * 13-Jan-2003 : Reviewed Javadocs (DG);
 * 26-Mar-2003 : Implemented Serializable (DG);
 * 02-Jul-2003 : Added new text alignment and rotation options (DG);
 * 19-Aug-2003 : Implemented Cloneable (DG);
 * 17-Jan-2003 : Added fix for bug 878706, where the annotation is placed 
 *               incorrectly for a plot with horizontal orientation (thanks to
 *               Ed Yu for the fix) (DG);
 * 21-Jan-2004 : Update for renamed method in ValueAxis (DG);
 * ------------- JFREECHART 1.0.0 ---------------------------------------------
 * 26-Jan-2006 : Fixed equals() method (bug 1415480) (DG);
 *
 */

package org.jfree.chart.annotations;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.PlotRenderingInfo;
import org.jfree.chart.plot.XYPlot;
import org.jfree.io.SerialUtilities;
import org.jfree.text.TextUtilities;
import org.jfree.ui.RectangleEdge;
import org.jfree.ui.TextAnchor;
import org.jfree.util.PaintUtilities;
import org.jfree.util.PublicCloneable;

/**
 * A text annotation that can be placed at a particular (x, y) location on an 
 * {@link XYPlot}.
 */
public class XYTextAnnotation extends AbstractXYAnnotation
                              implements Cloneable, PublicCloneable, 
                                         Serializable {

    /** For serialization. */
    private static final long serialVersionUID = -2946063342782506328L;
    
    /** The default font. */
    public static final Font DEFAULT_FONT 
        = new Font("SansSerif", Font.PLAIN, 10);

    /** The default paint. */
    public static final Paint DEFAULT_PAINT = Color.black;
    
    /** The default text anchor. */
    public static final TextAnchor DEFAULT_TEXT_ANCHOR = TextAnchor.CENTER;

    /** The default rotation anchor. */    
    public static final TextAnchor DEFAULT_ROTATION_ANCHOR = TextAnchor.CENTER;
    
    /** The default rotation angle. */
    public static final double DEFAULT_ROTATION_ANGLE = 0.0;

    /** The text. */
    private String text;

    /** The font. */
    private Font font;

    /** The paint. */
    private transient Paint paint;
    
    /** The x-coordinate. */
    private double x;

    /** The y-coordinate. */
    private double y;

    /** The text anchor (to be aligned with (x, y)). */
    private TextAnchor textAnchor;
    
    /** The rotation anchor. */
    private TextAnchor rotationAnchor;
    
    /** The rotation angle. */
    private double rotationAngle;
    
    /**
     * Creates a new annotation to be displayed at the given coordinates.  The
     * coordinates are specified in data space (they will be converted to 
     * Java2D space for display).
     *
     * @param text  the text (<code>null</code> not permitted).
     * @param x  the x-coordinate (in data space).
     * @param y  the y-coordinate (in data space).
     */
    public XYTextAnnotation(String text, double x, double y) {
        if (text == null) {
            throw new IllegalArgumentException("Null 'text' argument.");
        }
        this.text = text;
        this.font = DEFAULT_FONT;
        this.paint = DEFAULT_PAINT;
        this.x = x;
        this.y = y;
        this.textAnchor = DEFAULT_TEXT_ANCHOR;
        this.rotationAnchor = DEFAULT_ROTATION_ANCHOR;
        this.rotationAngle = DEFAULT_ROTATION_ANGLE;
    }
    
    /**
     * Returns the text for the annotation.
     *
     * @return The text (never <code>null</code>).
     */
    public String getText() {
        return this.text;
    }

    /**
     * Sets the text for the annotation.
     * 
     * @param text  the text (<code>null</code> not permitted).
     */
    public void setText(String text) {
        this.text = text;
    }
    
    /**
     * Returns the font for the annotation.
     *
     * @return The font.
     */
    public Font getFont() {
        return this.font;
    }

    /**
     * Sets the font for the annotation.
     * 
     * @param font  the font.
     */
    public void setFont(Font font) {
        this.font = font;
    }
    
    /**
     * Returns the paint for the annotation.
     *
     * @return The paint.
     */
    public Paint getPaint() {
        return this.paint;
    }
    
    /**
     * Sets the paint for the annotation.
     * 
     * @param paint  the paint.
     */
    public void setPaint(Paint paint) {
        this.paint = paint;
    }

    /**
     * Returns the text anchor.
     * 
     * @return The text anchor.
     */
    public TextAnchor getTextAnchor() {
        return this.textAnchor;
    }
    
    /**
     * Sets the text anchor (the point on the text bounding rectangle that is 
     * aligned to the (x, y) coordinate of the annotation).
     * 
     * @param anchor  the anchor point.
     */
    public void setTextAnchor(TextAnchor anchor) {
        this.textAnchor = anchor;
    }
    
    /**
     * Returns the rotation anchor.
     * 
     * @return The rotation anchor point.
     */
    public TextAnchor getRotationAnchor() {
        return this.rotationAnchor;
    }
    
    /**
     * Sets the rotation anchor point.
     * 
     * @param anchor  the anchor.
     */
    public void setRotationAnchor(TextAnchor anchor) {
        this.rotationAnchor = anchor;    
    }
    
    /**
     * Returns the rotation angle.
     * 
     * @return The rotation angle.
     */
    public double getRotationAngle() {
        return this.rotationAngle; 
    }
    
    /**
     * Sets the rotation angle.
     * <p>
     * The angle is measured clockwise in radians.
     * 
     * @param angle  the angle (in radians).
     */
    public void setRotationAngle(double angle) {
        this.rotationAngle = angle;    
    }
    
    /**
     * Returns the x coordinate for the text anchor point (measured against the
     * domain axis).
     * 
     * @return The x coordinate (in data space).
     */
    public double getX() {
        return this.x;
    }
    
    /**
     * Sets the x coordinate for the text anchor point (measured against the 
     * domain axis).
     * 
     * @param x  the x coordinate (in data space).
     */
    public void setX(double x) {
        this.x = x;
    }
    
    /**
     * Returns the y coordinate for the text anchor point (measured against the
     * range axis).
     * 
     * @return The y coordinate (in data space).
     */
    public double getY() {
        return this.y;
    }
    
    /**
     * Sets the y coordinate for the text anchor point (measured against the
     * range axis).
     * 
     * @param y  the y coordinate.
     */
    public void setY(double y) {
        this.y = y;
    }    

    /**
     * Draws the annotation.
     *
     * @param g2  the graphics device.
     * @param plot  the plot.
     * @param dataArea  the data area.
     * @param domainAxis  the domain axis.
     * @param rangeAxis  the range axis.
     * @param rendererIndex  the renderer index.
     * @param info  an optional info object that will be populated with
     *              entity information.
     */
    public void draw(Graphics2D g2, XYPlot plot, Rectangle2D dataArea,
                     ValueAxis domainAxis, ValueAxis rangeAxis, 
                     int rendererIndex,
                     PlotRenderingInfo info) {

        PlotOrientation orientation = plot.getOrientation();
        RectangleEdge domainEdge = Plot.resolveDomainAxisLocation(
            plot.getDomainAxisLocation(), orientation
        );
        RectangleEdge rangeEdge = Plot.resolveRangeAxisLocation(
            plot.getRangeAxisLocation(), orientation
        );

        float anchorX = (float) domainAxis.valueToJava2D(
            this.x, dataArea, domainEdge
        );
        float anchorY = (float) rangeAxis.valueToJava2D(
            this.y, dataArea, rangeEdge
        );

        if (orientation == PlotOrientation.HORIZONTAL) {
            float tempAnchor = anchorX;
            anchorX = anchorY;
            anchorY = tempAnchor;
        }
        
        g2.setFont(getFont());
        g2.setPaint(getPaint());
        TextUtilities.drawRotatedString(
            getText(), 
            g2,
            anchorX, 
            anchorY,
            getTextAnchor(),
            getRotationAngle(),
            getRotationAnchor()
        );
        Shape hotspot = TextUtilities.calculateRotatedStringBounds(
            getText(), 
            g2,
            anchorX, 
            anchorY,
            getTextAnchor(),
            getRotationAngle(),
            getRotationAnchor()
        );
        
        String toolTip = getToolTipText();
        String url = getURL();
        if (toolTip != null || url != null) {
            addEntity(info, hotspot, rendererIndex, toolTip, url);
        }

    }
    
    /**
     * Tests this annotation for equality with an arbitrary object.
     * 
     * @param obj  the object (<code>null</code> permitted).
     * 
     * @return A boolean.
     */
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;   
        }
        if (!(obj instanceof XYTextAnnotation)) {
            return false;   
        }
        if (!super.equals(obj)) {
            return false;
        }
        XYTextAnnotation that = (XYTextAnnotation) obj;
        if (!this.text.equals(that.text)) {
            return false;   
        }
        if (this.x != that.x) {
            return false;
        }
        if (this.y != that.y) {
            return false;
        }
        if (!this.font.equals(that.font)) {
            return false;   
        }
        if (!PaintUtilities.equal(this.paint, that.paint)) {
            return false;   
        }
        if (!this.rotationAnchor.equals(that.rotationAnchor)) {
            return false;   
        }
        if (this.rotationAngle != that.rotationAngle) {
            return false;   
        }
        if (!this.textAnchor.equals(that.textAnchor)) {
            return false;   
        }
        return true;   
    }
    
    /**
     * Returns a hash code for the object.
     * 
     * @return A hash code.
     */
    public int hashCode() {
        // TODO: implement this properly.
        return this.text.hashCode();   
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
