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
 * ---------------------------
 * DefaultDrawingSupplier.java
 * ---------------------------
 * (C) Copyright 2003, by Object Refinery Limited.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited);
 * Contributor(s):   Jeremy Bowman;
 *
 * $Id: DefaultDrawingSupplier.java,v 1.1 2007/10/10 19:22:00 vauchers Exp $
 *
 * Changes
 * -------
 * 16-Jan-2003 : Version 1 (DG);
 * 17-Jan-2003 : Added stroke method, renamed DefaultPaintSupplier --> DefaultDrawingSupplier (DG)
 * 27-Jan-2003 : Incorporated code from SeriesShapeFactory, originally contributed by
 *               Jeremy Bowman (DG);
 * 25-Mar-2003 : Implemented Serializable (DG);
 * 20-Aug-2003 : Implemented Cloneable and PublicCloneable (DG);
 *
 */

 package org.jfree.chart.plot;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Paint;
import java.awt.Polygon;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Arrays;

import org.jfree.chart.ChartColor;
import org.jfree.io.SerialUtilities;
import org.jfree.util.PublicCloneable;

/**
 * A default implementation of the {@link DrawingSupplier} interface.
 *
 * @author David Gilbert
 */
public class DefaultDrawingSupplier implements DrawingSupplier, 
                                               Cloneable, 
                                               PublicCloneable, 
                                               Serializable {

    /** The default fill paint sequence. */
    public static final Paint[] DEFAULT_PAINT_SEQUENCE = ChartColor.createDefaultPaintArray();

    /** The default outline paint sequence. */
    public static final Paint[] DEFAULT_OUTLINE_PAINT_SEQUENCE = new Paint[] {
                                    Color.lightGray
                                };

    /** The default stroke sequence. */
    public static final Stroke[] DEFAULT_STROKE_SEQUENCE = new Stroke[] {
                                    new BasicStroke(1.0f,
                                                    BasicStroke.CAP_SQUARE,
                                                    BasicStroke.JOIN_BEVEL)
                                };

    /** The default outline stroke sequence. */
    public static final Stroke[] DEFAULT_OUTLINE_STROKE_SEQUENCE = new Stroke[] {
                                    new BasicStroke(1.0f,
                                                    BasicStroke.CAP_SQUARE,
                                                    BasicStroke.JOIN_BEVEL)
                                };

    /** The default shape sequence. */
    public static final Shape[] DEFAULT_SHAPE_SEQUENCE = createStandardSeriesShapes();

    /** The paint sequence. */
    private transient Paint[] paintSequence;

    /** The current paint index. */
    private int paintIndex;

    /** The outline paint sequence. */
    private transient Paint[] outlinePaintSequence;

    /** The current outline paint index. */
    private int outlinePaintIndex;

    /** The stroke sequence. */
    private transient Stroke[] strokeSequence;

    /** The current stroke index. */
    private int strokeIndex;

    /** The outline stroke sequence. */
    private transient Stroke[] outlineStrokeSequence;

    /** The current outline stroke index. */
    private int outlineStrokeIndex;

    /** The shape sequence. */
    private transient Shape[] shapeSequence;

    /** The current shape index. */
    private int shapeIndex;

    /**
     * Creates a new supplier, with default sequences for fill paint, outline paint, stroke and
     * shapes.
     */
    public DefaultDrawingSupplier() {

        this(DEFAULT_PAINT_SEQUENCE,
             DEFAULT_OUTLINE_PAINT_SEQUENCE,
             DEFAULT_STROKE_SEQUENCE,
             DEFAULT_OUTLINE_STROKE_SEQUENCE,
             DEFAULT_SHAPE_SEQUENCE);

    }

    /**
     * Creates a new supplier.
     *
     * @param paintSequence  the fill paint sequence.
     * @param outlinePaintSequence  the outline paint sequence.
     * @param strokeSequence  the stroke sequence.
     * @param outlineStrokeSequence  the outline stroke sequence.
     * @param shapeSequence  the shape sequence.
     */
    public DefaultDrawingSupplier(Paint[] paintSequence,
                                  Paint[] outlinePaintSequence,
                                  Stroke[] strokeSequence,
                                  Stroke[] outlineStrokeSequence,
                                  Shape[] shapeSequence) {

        this.paintSequence = paintSequence;
        this.outlinePaintSequence = outlinePaintSequence;
        this.strokeSequence = strokeSequence;
        this.outlineStrokeSequence = outlineStrokeSequence;
        this.shapeSequence = shapeSequence;

    }

    /**
     * Returns the next paint in the sequence.
     *
     * @return The paint.
     */
    public Paint getNextPaint() {
        Paint result = paintSequence[paintIndex % paintSequence.length];
        paintIndex++;
        return result;
    }

    /**
     * Returns the next outline paint in the sequence.
     *
     * @return The paint.
     */
    public Paint getNextOutlinePaint() {
        Paint result = outlinePaintSequence[outlinePaintIndex % outlinePaintSequence.length];
        outlinePaintIndex++;
        return result;
    }

    /**
     * Returns the next stroke in the sequence.
     *
     * @return The stroke.
     */
    public Stroke getNextStroke() {
        Stroke result = strokeSequence[strokeIndex % strokeSequence.length];
        strokeIndex++;
        return result;
    }

    /**
     * Returns the next outline stroke in the sequence.
     *
     * @return The stroke.
     */
    public Stroke getNextOutlineStroke() {
        Stroke result = outlineStrokeSequence[outlineStrokeIndex % outlineStrokeSequence.length];
        outlineStrokeIndex++;
        return result;
    }

    /**
     * Returns the next shape in the sequence.
     *
     * @return The shape.
     */
    public Shape getNextShape() {
        Shape result = shapeSequence[shapeIndex % shapeSequence.length];
        shapeIndex++;
        return result;
    }

    /**
     * Creates an array of standard shapes to display for the items in series on charts.
     *
     * @return The array of shapes.
     */
    public static Shape[] createStandardSeriesShapes() {

        Shape[] result = new Shape[10];

        double size = 6.0;
        double delta = size / 2.0;
        int[] xpoints = null;
        int[] ypoints = null;

        // square
        result[0] = new Rectangle2D.Double(-delta, -delta, size, size);
        // circle
        result[1] = new Ellipse2D.Double(-delta, -delta, size, size);

        // up-pointing triangle
        xpoints = intArray(0.0, delta, -delta);
        ypoints = intArray(-delta, delta, delta);
        result[2] = new Polygon(xpoints, ypoints, 3);

        // diamond
        xpoints = intArray(0.0, delta, 0.0, -delta);
        ypoints = intArray(-delta, 0.0, delta, 0.0);
        result[3] = new Polygon(xpoints, ypoints, 4);

        // horizontal rectangle
        result[4] = new Rectangle2D.Double(-delta, -delta / 2, size, size / 2);

        // down-pointing triangle
        xpoints = intArray(-delta, +delta, 0.0);
        ypoints = intArray(-delta, -delta, delta);
        result[5] = new Polygon(xpoints, ypoints, 3);

        // horizontal ellipse
        result[6] = new Ellipse2D.Double(-delta, -delta / 2, size, size / 2);

        // right-pointing triangle
        xpoints = intArray(-delta, delta, -delta);
        ypoints = intArray(-delta, 0.0, delta);
        result[7] = new Polygon(xpoints, ypoints, 3);

        // vertical rectangle
        result[8] = new Rectangle2D.Double(-delta / 2, -delta, size / 2, size);

        // left-pointing triangle
        xpoints = intArray(-delta, delta, delta);
        ypoints = intArray(0.0, -delta, +delta);
        result[9] = new Polygon(xpoints, ypoints, 3);

        return result;

    }

    /**
     * Tests this object for equality with another object.
     *
     * @param obj  the other object.
     *
     * @return A boolean.
     */
    public boolean equals(Object obj) {

        if (obj == null) {
            return false;
        }

        if (obj == this) {
            return true;
        }

        if (obj instanceof DefaultDrawingSupplier) {
            DefaultDrawingSupplier supplier = (DefaultDrawingSupplier) obj;

            boolean b0 = Arrays.equals(this.paintSequence, supplier.paintSequence);

            boolean b1 = (this.paintIndex == supplier.paintIndex);

            boolean b2 = Arrays.equals(this.outlinePaintSequence, supplier.outlinePaintSequence);

            boolean b3 = (this.outlinePaintIndex == supplier.outlinePaintIndex);

            boolean b4 = Arrays.equals(this.strokeSequence, supplier.strokeSequence);

            boolean b5 = (this.strokeIndex == supplier.strokeIndex);

            boolean b6 = Arrays.equals(this.outlineStrokeSequence, supplier.outlineStrokeSequence);

            boolean b7 = (this.outlineStrokeIndex == supplier.outlineStrokeIndex);

            boolean b8 = true; // something is going wrong here?
            // Arrays.equals(this.shapeSequence, supplier.shapeSequence);

            boolean b9 = (this.shapeIndex == supplier.shapeIndex);

            return b0 && b1 && b2 && b3 && b4 && b5 && b6 && b7 && b8 && b9;

        }

        return false;

    }

    /**
     * Handles serialization.
     *
     * @param stream  the output stream.
     *
     * @throws IOException if there is an I/O problem.
     */
    private void writeObject(ObjectOutputStream stream) throws IOException {
        stream.defaultWriteObject();

        int paintCount = this.paintSequence.length;
        stream.writeInt(paintCount);
        for (int i = 0; i < paintCount; i++) {
            SerialUtilities.writePaint(this.paintSequence[i], stream);
        }

        int outlinePaintCount = this.outlinePaintSequence.length;
        stream.writeInt(outlinePaintCount);
        for (int i = 0; i < outlinePaintCount; i++) {
            SerialUtilities.writePaint(this.outlinePaintSequence[i], stream);
        }

        int strokeCount = this.strokeSequence.length;
        stream.writeInt(strokeCount);
        for (int i = 0; i < strokeCount; i++) {
            SerialUtilities.writeStroke(this.strokeSequence[i], stream);
        }

        int outlineStrokeCount = this.outlineStrokeSequence.length;
        stream.writeInt(outlineStrokeCount);
        for (int i = 0; i < outlineStrokeCount; i++) {
            SerialUtilities.writeStroke(this.outlineStrokeSequence[i], stream);
        }

        int shapeCount = this.shapeSequence.length;
        stream.writeInt(shapeCount);
        for (int i = 0; i < shapeCount; i++) {
            SerialUtilities.writeShape(this.shapeSequence[i], stream);
        }

    }

    /**
     * Restores a serialized object.
     *
     * @param stream  the input stream.
     *
     * @throws IOException if there is an I/O problem.
     * @throws ClassNotFoundException if there is a problem loading a class.
     */
    private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
        stream.defaultReadObject();

        int paintCount = stream.readInt();
        this.paintSequence = new Paint[paintCount];
        for (int i = 0; i < paintCount; i++) {
            this.paintSequence[i] = SerialUtilities.readPaint(stream);
        }

        int outlinePaintCount = stream.readInt();
        this.outlinePaintSequence = new Paint[outlinePaintCount];
        for (int i = 0; i < outlinePaintCount; i++) {
            this.outlinePaintSequence[i] = SerialUtilities.readPaint(stream);
        }

        int strokeCount = stream.readInt();
        this.strokeSequence = new Stroke[strokeCount];
        for (int i = 0; i < strokeCount; i++) {
            this.strokeSequence[i] = SerialUtilities.readStroke(stream);
        }

        int outlineStrokeCount = stream.readInt();
        this.outlineStrokeSequence = new Stroke[outlineStrokeCount];
        for (int i = 0; i < outlineStrokeCount; i++) {
            this.outlineStrokeSequence[i] = SerialUtilities.readStroke(stream);
        }

        int shapeCount = stream.readInt();
        this.shapeSequence = new Shape[shapeCount];
        for (int i = 0; i < shapeCount; i++) {
            this.shapeSequence[i] = SerialUtilities.readShape(stream);
        }

    }

    /**
     * Helper method to avoid lots of explicit casts in getShape().  Returns
     * an array containing the provided doubles cast to ints.
     *
     * @param a  x
     * @param b  y
     * @param c  z
     *
     * @return int[3] with converted params.
     */
    private static int[] intArray(double a, double b, double c) {
        return new int[] {(int) a, (int) b, (int) c};
    }

    /**
     * Helper method to avoid lots of explicit casts in getShape().  Returns
     * an array containing the provided doubles cast to ints.
     *
     * @param a  x
     * @param b  y
     * @param c  z
     * @param d  t
     *
     * @return int[3] with converted params.
     */
    private static int[] intArray(double a, double b, double c, double d) {
        return new int[] {(int) a, (int) b, (int) c, (int) d};
    }

    /**
     * Returns a clone.
     * 
     * @return A clone.
     * 
     * @throws CloneNotSupportedException if a component of the supplier does not support cloning.
     */
    public Object clone() throws CloneNotSupportedException {
        DefaultDrawingSupplier clone = (DefaultDrawingSupplier) super.clone();
        //public static final Paint[] DEFAULT_PAINT_SEQUENCE = ChartColor.createDefaultPaintArray();
        //public static final Paint[] DEFAULT_OUTLINE_PAINT_SEQUENCE 
        //public static final Stroke[] DEFAULT_STROKE_SEQUENCE 
        //public static final Stroke[] DEFAULT_OUTLINE_STROKE_SEQUENCE
        //public static final Shape[] DEFAULT_SHAPE_SEQUENCE = createStandardSeriesShapes();
        //private transient Paint[] paintSequence;
        //private int paintIndex;
        //private transient Paint[] outlinePaintSequence;
        //private int outlinePaintIndex;
        // private transient Stroke[] strokeSequence;
        //private int strokeIndex;
        //private transient Stroke[] outlineStrokeSequence;
        //private int outlineStrokeIndex;
        //private transient Shape[] shapeSequence;
        //clone.shapeSequence = 
        //private int shapeIndex <-- primitive       
        return clone;
    }
}
