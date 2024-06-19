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
 * ---------------
 * LegendItem.java
 * ---------------
 * (C) Copyright 2000-2004, by Object Refinery Limited and Contributors.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited);
 * Contributor(s):   Andrzej Porebski;
 *                   David Li;
 *                   Wolfgang Irler;
 *                   Luke Quinane;
 *
 * $Id: LegendItem.java,v 1.1 2007/10/10 19:34:49 vauchers Exp $
 *
 * Changes (from 2-Oct-2002)
 * -------------------------
 * 02-Oct-2002 : Fixed errors reported by Checkstyle (DG);
 * 17-Jan-2003 : Dropped outlineStroke attribute (DG);
 * 08-Oct-2003 : Applied patch for displaying series line style, contributed by Luke Quinane (DG);
 * 21-Jan-2004 : Added the shapeFilled flag (DG);
 * 
 */

package org.jfree.chart;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.Rectangle2D;

/**
 * A legend item.  Records all the properties of a legend item, but is not concerned about the 
 * display location.  Instances of this class are immutable.
 *
 */
public class LegendItem {

    /** The label. */
    private String label;
    
    /** The description (not currently used). */
    private String description;

    /** The shape. */
    private Shape shape;
    
    /** A flag that controls whether or not the shape is filled. */
    private boolean shapeFilled;

    /** The paint. */
    private Paint paint;

    /** The stroke. */
    private Stroke stroke;
    
    /** The outline paint. */
    private Paint outlinePaint;
    
    /** The outline stroke. */
    private Stroke outlineStroke;

    /**
     * Creates a new legend item.
     * 
     * @param label  the label.
     * @param paint  the fill paint.
     */
    public LegendItem(String label, Paint paint) {
        this(
            label, label, new Rectangle2D.Double(-4.0, -4.0, 8.0, 8.0), 
            true, paint, new BasicStroke(0.5f), Color.lightGray, new BasicStroke(0.5f)
        );   
    }
    /**
     * Creates a new legend item.
     *
     * @param label  the label.
     * @param description  the description (not currently used).
     * @param shape  the shape.
     * @param shapeFilled  a flag that controls whether or not the shape is filled.
     * @param paint  the paint.
     * @param stroke  the stroke.
     * @param outlinePaint  the outline paint.
     * @param outlineStroke  the outline stroke.
     */
    public LegendItem(String label,
                      String description,
                      Shape shape,
                      boolean shapeFilled,
                      Paint paint, 
                      Stroke stroke,
                      Paint outlinePaint,
                      Stroke outlineStroke) {
        
        this.label = label;
        this.description = description;
        this.shape = shape;
        this.shapeFilled = shapeFilled;
        this.paint = paint;
        this.stroke = stroke;
        this.outlinePaint = outlinePaint;
        this.outlineStroke = outlineStroke;

    }

    /**
     * Returns the label.
     *
     * @return the label.
     */
    public String getLabel() {
        return this.label;
    }

    /**
     * Returns the shape used to label the series represented by this legend item.
     *
     * @return the shape.
     */
    public Shape getShape() {
        return this.shape;
    }
    
    /**
     * Returns a flag that controls whether or not the shape is filled.
     * 
     * @return a boolean.
     */
    public boolean isShapeFilled() {
        return this.shapeFilled;
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
     * Returns the stroke used to render the shape for this series.
     *
     * @return the stroke.
     */
    public Stroke getStroke() {
        return this.stroke;
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
    
    //// DEPRECATED CODE //////////////////////////////////////////////////////////////////////////
    
    /**
     * Creates a new legend item.
     *
     * @param label  the label.
     * @param description  the description (not used).
     * @param shape  the shape.
     * @param paint  the paint.
     * @param outlinePaint  the outline paint.
     * @param stroke  the stroke.
     * 
     * @deprecated Use the other constructor.
     */
    public LegendItem(String label,
            String description,
            Shape shape,
            Paint paint, 
            Paint outlinePaint,
            Stroke stroke) {
        this(label, description, shape, true, paint, outlinePaint, stroke);
    }
    
    /**
     * Creates a new legend item.
     *
     * @param label  the label.
     * @param description  the description (not used).
     * @param shape  the shape.
     * @param shapeFilled  a flag that controls whether or not the shape is filled.
     * @param paint  the paint.
     * @param outlinePaint  the outline paint.
     * @param stroke  the stroke.
     * 
     * @deprecated Use other constructor.
     */
    public LegendItem(String label,
            String description,
            Shape shape,
            boolean shapeFilled,
            Paint paint, Paint outlinePaint,
            Stroke stroke) {
        
        this.label = label;
        this.description = description;
        this.shape = shape;
        this.shapeFilled = shapeFilled;
        this.paint = paint;
        this.outlinePaint = outlinePaint;
        this.stroke = stroke;

    }

}
