/* ======================================
 * JFreeChart : a free Java chart library
 * ======================================
 *
 * Project Info:  http://www.object-refinery.com/jfreechart/index.html
 * Project Lead:  David Gilbert (david.gilbert@object-refinery.com);
 *
 * (C) Copyright 2000-2003, by Simba Management Limited and Contributors.
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
 * (C) Copyright 2003, by Simba Management Limited.
 *
 * Original Author:  David Gilbert (for Simba Management Limited);
 * Contributor(s):   Jeremy Bowman;
 *
 * $Id: DefaultDrawingSupplier.java,v 1.1 2007/10/10 19:54:20 vauchers Exp $
 *
 * Changes
 * -------
 * 16-Jan-2003 : Version 1 (DG);
 * 17-Jan-2003 : Added stroke method, renamed DefaultPaintSupplier --> DefaultDrawingSupplier (DG)
 * 27-Jan-2003 : Incorporated code from SeriesShapeFactory, originally contributed by 
 *               Jeremy Bowman (DG);
 */
 
 package com.jrefinery.chart.renderer;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Paint;
import java.awt.Polygon;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;

import com.jrefinery.chart.ChartColor;

/**
 * A default implementation of the {@link DrawingSupplier} interface.
 * 
 * @author David Gilbert
 */
public class DefaultDrawingSupplier implements DrawingSupplier {
    
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
    private Paint[] paintSequence;
    
    /** The current paint index. */
    private int paintIndex;
    
    /** The outline paint sequence. */
    private Paint[] outlinePaintSequence;
    
    /** The current outline paint index. */
    private int outlinePaintIndex;

    /** The stroke sequence. */
    private Stroke[] strokeSequence;
    
    /** The current stroke index. */
    private int strokeIndex;
    
    /** The outline stroke sequence. */
    private Stroke[] outlineStrokeSequence;
    
    /** The current outline stroke index. */
    private int outlineStrokeIndex;
    
    /** The shape sequence. */
    private Shape[] shapeSequence;
    
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

//        default:
//            // Vertical ellipse
//            return new Ellipse2D.Double(x - delta / 2, y - delta, scale / 2, scale);

        return result;
        
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
        return new int[] { (int) a, (int) b, (int) c };
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
        return new int[] { (int) a, (int) b, (int) c, (int) d };
    }
 
}
