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
 * -----------
 * Marker.java
 * -----------
 * (C) Copyright 2002, 2003, by Object Refinery Limited.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited);
 * Contributor(s):   Nicolas Brodu;
 *
 * $Id: Marker.java,v 1.1 2007/10/10 19:21:54 vauchers Exp $
 *
 * Changes (since 2-Jul-2002)
 * --------------------------
 * 02-Jul-2002 : Added extra constructor, standard header and Javadoc comments (DG);
 * 20-Aug-2002 : Added the outline stroke attribute (DG);
 * 02-Oct-2002 : Fixed errors reported by Checkstyle (DG);
 * 16-Oct-2002 : Added new constructor (DG);
 * 26-Mar-2003 : Implemented Serializable (DG);
 * 21-May-2003 : Added labels (DG);
 * 11-Sep-2003 : Implemented Cloneable (NB);
 * 05-Nov-2003 : Added checks to ensure some attributes are never null (DG);
 *
 */

package org.jfree.chart;

import java.awt.Color;
import java.awt.Font;
import java.awt.Paint;
import java.awt.Stroke;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import org.jfree.io.SerialUtilities;
import org.jfree.util.ObjectUtils;

/**
 * A constant value that is drawn on a chart as a marker, usually as a horizontal or a vertical
 * line.
 * <P>
 * In addition to a value, this class defines paint attributes to give some control over the
 * appearance of the marker.  The renderer can, however, override these settings if it chooses.
 *
 * @author David Gilbert
 */
public class Marker implements Serializable, Cloneable {

    /** The constant value. */
    private double value;

    /** The outline paint. */
    private transient Paint outlinePaint;

    /** The outline stroke. */
    private transient Stroke outlineStroke;

    /** The paint. */
    private transient Paint paint;

    /** The alpha transparency. */
    private float alpha;

    /** The label. */
    private String label = null;

    /** The label font. */
    private Font labelFont;

    /** The label paint. */
    private transient Paint labelPaint;

    /** The label position. */
    private MarkerLabelPosition labelPosition = MarkerLabelPosition.TOP_LEFT;

    /**
     * Constructs a new marker.
     *
     * @param value  the value.
     */
    public Marker(double value) {
        this(value, Color.gray, new java.awt.BasicStroke(0.5f), Color.gray, 0.80f);
    }

    /**
     * Constructs a new marker.
     *
     * @param value  the value.
     * @param outlinePaint  the paint.
     */
    public Marker(double value, Paint outlinePaint) {
        this(value, outlinePaint, new java.awt.BasicStroke(0.5f), Color.red, 0.80f);
    }

    /**
     * Constructs a new marker.
     *
     * @param value  the value.
     * @param outlinePaint  the outline paint (<code>null</code> not permitted).
     * @param outlineStroke  the outline stroke (<code>null</code> not permitted).
     * @param paint  the paint (<code>null</code> not permitted).
     * @param alpha  the alpha transparency.
     */
    public Marker(double value, Paint outlinePaint, Stroke outlineStroke,
                  Paint paint, float alpha) {

        // check arguments...
        if (outlinePaint == null) {
            throw new IllegalArgumentException("Marker(...) : null outlinePaint not permitted.");
        }
        if (outlineStroke == null) {
            throw new IllegalArgumentException("Marker(...) : null outlineStroke not permitted.");
        }
        if (paint == null) {
            throw new IllegalArgumentException("Marker(...) : null paint not permitted.");
        }
        
        this.value = value;
        this.outlinePaint = outlinePaint;
        this.outlineStroke = outlineStroke;
        this.paint = paint;
        this.alpha = alpha;
        
        this.labelFont = new Font("SansSerif", Font.PLAIN, 9);
        this.labelPaint = Color.black;
        
    }

    /**
     * Returns the value.
     *
     * @return the value.
     */
    public double getValue() {
        return this.value;
    }

    /**
     * Returns the outline paint.
     *
     * @return the outline paint.
     */
    public Paint getOutlinePaint() {
        return this.outlinePaint;
    }

    /**
     * Returns the outline stroke.
     *
     * @return the outline stroke.
     */
    public Stroke getOutlineStroke() {
        return this.outlineStroke;
    }

    /**
     * Returns the paint.
     *
     * @return the paint.
     */
    public Paint getPaint() {
        return this.paint;
    }

    /**
     * Returns the alpha transparency.
     *
     * @return the alpha transparency.
     */
    public float getAlpha() {
        return this.alpha;
    }

    /**
     * Returns the label (if <code>null</code> no label is displayed).
     *
     * @return The label (possibly <code>null</code>).
     */
    public String getLabel() {
        return this.label;
    }

    /**
     * Sets the label (if <code>null</code> no label is displayed).
     *
     * @param label  the label (<code>null</code> permitted).
     */
    public void setLabel(String label) {
        this.label = label;
    }

    /**
     * Returns the label font.
     *
     * @return the label font (never <code>null</code>).
     */
    public Font getLabelFont() {
        return this.labelFont;
    }

    /**
     * Sets the label font.
     *
     * @param font  the font (<code>null</code> not permitted).
     */
    public void setLabelFont(Font font) {
        if (paint == null) {
            throw new IllegalArgumentException("Marker.setLabelFont(...): null not permitted.");
        }
        this.labelFont = font;
    }

    /**
     * Returns the label paint.
     *
     * @return the label paint (never </code>null</code>).
     */
    public Paint getLabelPaint() {
        return this.labelPaint;
    }

    /**
     * Sets the label paint.
     *
     * @param paint  the paint (<code>null</code> not permitted).
     */
    public void setLabelPaint(Paint paint) {
        if (paint == null) {
            throw new IllegalArgumentException("Marker.setLabelPaint(...): null not permitted.");
        }
        this.labelPaint = paint;
    }

    /**
     * Returns the label position.
     *
     * @return The label position.
     */
    public MarkerLabelPosition getLabelPosition() {
        return this.labelPosition;
    }

    /**
     * Sets the label position.
     *
     * @param position  the position.
     */
    public void setLabelPosition(MarkerLabelPosition position) {
        this.labelPosition = position;
    }

    /**
     * Tests an object for equality with this instance.
     * 
     * @param object  the object to test.
     * 
     * @return A boolean.
     */
    public boolean equals(Object object) {
        
        if (object == null) {
            return false;
        }
        
        if (object == this) {
            return true;
        }
        
        if (object instanceof Marker) {
            Marker marker = (Marker) object;
            boolean b0 = (this.value == marker.value);
            boolean b1 = ObjectUtils.equal(this.outlinePaint, marker.outlinePaint);
            boolean b2 = ObjectUtils.equal(this.outlineStroke, marker.outlineStroke);
            boolean b3 = ObjectUtils.equal(this.paint, marker.paint);
            boolean b4 = (this.alpha == marker.alpha);
            boolean b5 = ObjectUtils.equal(this.label, marker.label);
            boolean b6 = ObjectUtils.equal(this.labelFont, marker.labelFont);
            boolean b7 = ObjectUtils.equal(this.labelPaint, marker.labelPaint);
            boolean b8 = (this.labelPosition == marker.labelPosition);

            return b0 && b1 && b2 && b3 && b4 && b5 && b6 && b7 && b8;
        }
        
        return false;
            
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
        SerialUtilities.writePaint(this.outlinePaint, stream);
        SerialUtilities.writeStroke(this.outlineStroke, stream);
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
    private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {

        stream.defaultReadObject();
        this.outlinePaint = SerialUtilities.readPaint(stream);
        this.outlineStroke = SerialUtilities.readStroke(stream);
        this.paint = SerialUtilities.readPaint(stream);

    }

}
