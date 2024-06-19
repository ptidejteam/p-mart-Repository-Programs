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
 * AbstractRenderer.java
 * ---------------------
 * (C) Copyright 2002, 2003 by Object Refinery Limited.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited);
 * Contributor(s):   -;
 *
 * $Id: AbstractRenderer.java,v 1.1 2007/10/10 19:12:28 vauchers Exp $
 *
 * Changes:
 * --------
 * 22-Aug-2002 : Version 1, draws code out of AbstractXYItemRenderer to share with
 *               AbstractCategoryItemRenderer (DG);
 * 01-Oct-2002 : Fixed errors reported by Checkstyle (DG);
 * 06-Nov-2002 : Moved to the com.jrefinery.chart.renderer package (DG);
 * 21-Nov-2002 : Added a paint table for the renderer to use (DG);
 * 17-Jan-2003 : Moved plot classes into a separate package (DG);
 * 25-Mar-2003 : Implemented Serializable (DG);
 * 29-Apr-2003 : Added valueLabelFont and valueLabelPaint attributes, based on code from
 *               Arnaud Lelievre (DG);
 * 29-Jul-2003 : Amended code that doesn't compile with JDK 1.2.2 (DG);
 * 13-Aug-2003 : Implemented Cloneable (DG);
 * 
 */

package org.jfree.chart.renderer;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import org.jfree.chart.ChartRenderingInfo;
import org.jfree.chart.plot.DrawingSupplier;
import org.jfree.io.SerialUtilities;
import org.jfree.ui.TextAnchor;
import org.jfree.util.BooleanList;
import org.jfree.util.ObjectList;
import org.jfree.util.ObjectUtils;
import org.jfree.util.PaintList;
import org.jfree.util.ShapeList;
import org.jfree.util.ShapeUtils;

/**
 * Base class providing common services for renderers.
 *
 * @author David Gilbert
 */
public abstract class AbstractRenderer implements Cloneable, Serializable {

    /** The default paint. */
    public static final Paint DEFAULT_PAINT = Color.blue;

    /** The default outline paint. */
    public static final Paint DEFAULT_OUTLINE_PAINT = Color.gray;

    /** The default stroke. */
    public static final Stroke DEFAULT_STROKE = new BasicStroke(1.0f);

    /** The default outline stroke. */
    public static final Stroke DEFAULT_OUTLINE_STROKE = new BasicStroke(1.0f);

    /** The default shape. */
    public static final Shape DEFAULT_SHAPE = new Rectangle2D.Double(-3.0, -3.0, 6.0, 6.0);

    /** The default value label font. */
    public static final Font DEFAULT_VALUE_LABEL_FONT = new Font("SansSerif", Font.PLAIN, 10);

    /** The default value label pant. */
    public static final Paint DEFAULT_VALUE_LABEL_PAINT = Color.black;

    /** The paint for ALL series (optional). */
    private transient Paint paint;

    /** The paint list. */
    private PaintList paintList;

    /** The base paint. */
    private transient Paint basePaint;

    /** The outline paint for ALL series (optional). */
    private transient Paint outlinePaint;

    /** The outline paint list. */
    private PaintList outlinePaintList;

    /** The base outline paint. */
    private transient Paint baseOutlinePaint;

    /** The stroke for ALL series (optional). */
    private transient Stroke stroke;

    /** The stroke list. */
    private ObjectList strokeList;

    /** The base stroke. */
    private transient Stroke baseStroke;

    /** The outline stroke for ALL series (optional). */
    private transient Stroke outlineStroke;

    /** The outline stroke list. */
    private ObjectList outlineStrokeList;

    /** The base outline stroke. */
    private transient Stroke baseOutlineStroke;

    /** The shape for ALL series (optional). */
    private transient Shape shape;

    /** A shape list. */
    private ShapeList shapeList;

    /** The base shape. */
    private transient Shape baseShape;

    /** Visibility of the item labels for ALL series (optional). */
    private Boolean itemLabelsVisible;

    /** Visibility of the item labels PER series. */
    private BooleanList itemLabelsVisibleList;

    /** The base item labels visible. */
    private Boolean baseItemLabelsVisible;

    /** The item label font for ALL series (optional). */
    private Font itemLabelFont;

    /** The item label font list (one font per series). */
    private ObjectList itemLabelFontList;

    /** The base item label font. */
    private Font baseItemLabelFont;

    /** The item label paint for ALL series. */
    private transient Paint itemLabelPaint;

    /** The item label paint list (one paint per series). */
    private PaintList itemLabelPaintList;

    /** The base item label paint. */
    private transient Paint baseItemLabelPaint;

    /** The item label anchor. */
    private ItemLabelAnchor itemLabelAnchor;

    /** The item label anchor list (one anchor per series). */
    private ObjectList itemLabelAnchorList;

    /** The base item label anchor. */
    private ItemLabelAnchor baseItemLabelAnchor;

    /** The item label text anchor. */
    private TextAnchor itemLabelTextAnchor;

    /** The item label text anchor list (one anchor per series). */
    private ObjectList itemLabelTextAnchorList;

    /** The base item label text anchor. */
    private TextAnchor baseItemLabelTextAnchor;

    /** The rotation anchor. */
    private TextAnchor itemLabelRotationAnchor;

    /** The rotation anchor list (one anchor per series). */
    private ObjectList itemLabelRotationAnchorList;

    /** The base rotation anchor. */
    private TextAnchor baseItemLabelRotationAnchor;

    /** The angle. */
    private Number itemLabelAngle;

    /** The angle list (one angle per series). */
    private ObjectList itemLabelAngleList;

    /** The base angle. */
    private Number baseItemLabelAngle;

    /** A temporary reference to chart rendering info (may be <code>null</code>). */
    private transient ChartRenderingInfo info;

    /** Support class for the property change listener mechanism. */
    private transient PropertyChangeSupport listeners;

    /**
     * Default constructor.
     */
    public AbstractRenderer() {

        this.paint = null;
        this.paintList = new PaintList();
        this.basePaint = DEFAULT_PAINT;

        this.outlinePaint = null;
        this.outlinePaintList = new PaintList();
        this.baseOutlinePaint = DEFAULT_OUTLINE_PAINT;

        this.stroke = null;
        this.strokeList = new ObjectList();
        this.baseStroke = DEFAULT_STROKE;

        this.outlineStroke = null;
        this.outlineStrokeList = new ObjectList();
        this.baseOutlineStroke = DEFAULT_OUTLINE_STROKE;

        this.shape = null;
        this.shapeList = new ShapeList();
        this.baseShape = DEFAULT_SHAPE;

        this.itemLabelsVisible = null;
        this.itemLabelsVisibleList = new BooleanList();
        this.baseItemLabelsVisible = Boolean.FALSE;

        this.itemLabelFont = null;
        this.itemLabelFontList = new ObjectList();
        this.baseItemLabelFont = new Font("SansSerif", Font.PLAIN, 10);

        this.itemLabelPaint = null;
        this.itemLabelPaintList = new PaintList();
        this.baseItemLabelPaint = Color.black;

        this.itemLabelAnchor = null;
        this.itemLabelAnchorList = new ObjectList();
        this.baseItemLabelAnchor = ItemLabelAnchor.OUTSIDE12;

        this.itemLabelTextAnchor = null;
        this.itemLabelTextAnchorList = new ObjectList();
        this.baseItemLabelTextAnchor = TextAnchor.BOTTOM_CENTER;

        this.itemLabelRotationAnchor = null;
        this.itemLabelRotationAnchorList = new ObjectList();
        this.baseItemLabelRotationAnchor = TextAnchor.CENTER;

        this.itemLabelAngle = null;
        this.itemLabelAngleList = new ObjectList();
        this.baseItemLabelAngle = new Double(0.0);

        this.info = null;
        this.listeners = new PropertyChangeSupport(this);

    }

    /**
     * Returns the chart rendering info.
     *
     * @return the chart rendering info.
     */
    public ChartRenderingInfo getInfo() {
        return this.info;
    }

    /**
     * Sets the chart rendering info.
     *
     * @param info  the chart rendering info.
     */
    public void setInfo(ChartRenderingInfo info) {
        this.info = info;
    }

    /**
     * Returns the drawing supplier from the plot.
     * 
     * @return The drawing supplier.
     */
    public abstract DrawingSupplier getDrawingSupplier();
    
    // PAINT
    
    /**
     * Returns the paint used to fill data items as they are drawn.
     * <p>
     * The default implementation passes control to the <code>getSeriesPaint</code> method.
     * You can override this method if you require different behaviour.
     *
     * @param row  the row (or series) index (zero-based).
     * @param column  the column (or category) index (zero-based).
     *
     * @return  The paint.
     */
    public Paint getItemPaint(int row, int column) {
        return getSeriesPaint(row);
    }

    /**
     * Returns the color used to fill an item drawn by the renderer.
     *
     * @param series  the series index (zero-based).
     *
     * @return  The paint.
     */
    public Paint getSeriesPaint(int series) {

        // return the override, if there is one...
        if (this.paint != null) {
            return this.paint;
        }

        // otherwise look up the paint list
        Paint paint = (Paint) this.paintList.getPaint(series);
        if (paint == null) {
            DrawingSupplier supplier = getDrawingSupplier();
            if (supplier != null) {
                paint = supplier.getNextPaint();
                this.paintList.setPaint(series, paint);
            }
            else {
                paint = this.basePaint;
            }
        }
        return paint;

    }

    /**
     * Sets the paint to be used for ALL series.  Most of the time, you will want to leave this
     * set to <code>null</code> so that the paint lookup table is used instead.
     * 
     * @param paint  the paint (<code>null</code> permitted).
     */
    public void setPaint(Paint paint) {
        this.paint = paint;
    }
    
    /**
     * Sets the paint used for a series.
     *
     * @param series  the series index (zero-based).
     * @param paint  the paint.
     */
    public void setSeriesPaint(int series, Paint paint) {
        this.paintList.setPaint(series, paint);
    }

    /**
     * Returns the base paint.
     *
     * @return The base paint.
     */
    public Paint getBasePaint() {
        return this.basePaint;
    }

    /**
     * Sets the base paint.
     * <p>
     * In most cases, the renderer's paint table will be active and so this default value will
     * not be used.
     *
     * @param paint  the paint.
     */
    public void setBasePaint(Paint paint) {
        this.basePaint = paint;
    }

    // OUTLINE PAINT
    
    /**
     * Returns the paint used to outline data items as they are drawn.
     * <p>
     * The default implementation passes control to the getSeriesOutlinePaint method.  You can
     * override this method if you require different behaviour.
     *
     * @param row  the row (or series) index (zero-based).
     * @param column  the column (or category) index (zero-based).
     *
     * @return  The paint.
     */
    public Paint getItemOutlinePaint(int row, int column) {
        return getSeriesOutlinePaint(row);
    }

    /**
     * Returns the color used to outline an item drawn by the renderer.
     *
     * @param series  the series (zero-based index).
     *
     * @return  The paint.
     */
    public Paint getSeriesOutlinePaint(int series) {

        // return the override, if there is one...
        if (this.outlinePaint != null) {
            return this.outlinePaint;
        }

        // otherwise look up the paint table
        Paint paint = (Paint) this.outlinePaintList.getPaint(series);
        if (paint == null) {
            DrawingSupplier supplier = getDrawingSupplier();
            if (supplier != null) {
                paint = supplier.getNextOutlinePaint();
                this.outlinePaintList.setPaint(series, paint);
            }
            else {
                paint = this.baseOutlinePaint;
            }
        }
        return paint;

    }

    /**
     * Sets the paint used for a series outline.
     *
     * @param series  the series index (zero-based).
     * @param paint  the paint.
     */
    public void setSeriesOutlinePaint(int series, Paint paint) {
        this.outlinePaintList.setPaint(series, paint);
    }

    /**
     * Returns the base outline paint.
     *
     * @return The base outline paint.
     */
    public Paint getBaseOutlinePaint() {
        return this.baseOutlinePaint;
    }

    /**
     * Sets the base outline paint.
     *
     * @param paint  the paint.
     */
    public void setBaseOutlinePaint(Paint paint) {
        this.baseOutlinePaint = paint;
    }

    // STROKE
    
    /**
     * Returns the stroke used to draw data items.
     * <p>
     * The default implementation passes control to the getSeriesStroke method.  You can override
     * this method if you require different behaviour.
     *
     * @param row  the row (or series) index (zero-based).
     * @param column  the column (or category) index (zero-based).
     *
     * @return The stroke.
     */
    public Stroke getItemStroke(int row, int column) {
        return getSeriesStroke(row);
    }

    /**
     * Returns the stroke used to draw the items in a series.
     *
     * @param series  the series (zero-based index).
     *
     * @return  The stroke.
     */
    public Stroke getSeriesStroke(int series) {

        // return the override, if there is one...
        if (this.stroke != null) {
            return this.stroke;
        }

        // otherwise look up the paint table
        Stroke stroke = (Stroke) this.strokeList.get(series);
        if (stroke == null) {
            DrawingSupplier supplier = getDrawingSupplier();
            if (supplier != null) {
                stroke = supplier.getNextStroke();
                this.strokeList.set(series, stroke);
            }
            else {
                stroke = this.baseStroke;
            }
        }
        return stroke;

    }
    
    /**
     * Sets the stroke for ALL series (optional).
     * 
     * @param stroke  the stroke (<code>null</code> permitted).
     */
    public void setStroke(Stroke stroke) {
        this.stroke = stroke;
    }

    /**
     * Sets the stroke used for a series.
     *
     * @param series  the series index (zero-based).
     * @param stroke  the stroke.
     */
    public void setSeriesStroke(int series, Stroke stroke) {
        this.strokeList.set(series, stroke);
    }

    /**
     * Returns the base stroke.
     *
     * @return The base stroke.
     */
    public Stroke getBaseStroke() {
        return this.baseStroke;
    }

    /**
     * Sets the base stroke.
     *
     * @param stroke  the stroke.
     */
    public void setBaseStroke(Stroke stroke) {
        this.baseStroke = stroke;
    }

    // OUTLINE STROKE 
    
    /**
     * Returns the stroke used to outline data items.
     * <p>
     * The default implementation passes control to the getSeriesOutlineStroke method.
     * You can override this method if you require different behaviour.
     *
     * @param row  the row (or series) index (zero-based).
     * @param column  the column (or category) index (zero-based).
     *
     * @return The stroke.
     */
    public Stroke getItemOutlineStroke(int row, int column) {
        return getSeriesOutlineStroke(row);
    }

    /**
     * Returns the stroke used to outline the items in a series.
     *
     * @param series  the series (zero-based index).
     *
     * @return  The stroke.
     */
    public Stroke getSeriesOutlineStroke(int series) {

        // return the override, if there is one...
        if (this.outlineStroke != null) {
            return this.outlineStroke;
        }

        // otherwise look up the stroke table
        Stroke stroke = (Stroke) this.outlineStrokeList.get(series);
        if (stroke == null) {
            DrawingSupplier supplier = getDrawingSupplier();
            if (supplier != null) {
                stroke = supplier.getNextStroke();
                this.strokeList.set(series, stroke);
            }
            else {
                stroke = this.baseOutlineStroke;
            }
        }
        return stroke;

    }

    /**
     * Sets the outline stroke.
     *
     * @param stroke  the outline stroke.
     */
    public void setOutlineStroke(Stroke stroke) {
        this.outlineStroke = stroke;
    }

    /**
     * Sets the outline stroke used for a series.
     *
     * @param series  the series index (zero-based).
     * @param stroke  the stroke.
     */
    public void setSeriesOutlineStroke(int series, Stroke stroke) {
        this.outlineStrokeList.set(series, stroke);
    }

    /**
     * Returns the base outline stroke.
     *
     * @return The base outline stroke.
     */
    public Stroke getBaseOutlineStroke() {
        return this.baseOutlineStroke;
    }

    /**
     * Sets the base outline stroke.
     *
     * @param stroke  the base outline stroke.
     */
    public void setBaseOutlineStroke(Stroke stroke) {
        this.baseOutlineStroke = stroke;
    }

    // SHAPE
    
    /**
     * Returns a shape used to represent a data item.
     * <p>
     * The default implementation passes control to the getSeriesShape method.  You can override
     * this method if you require different behaviour.
     *
     * @param row  the row (or series) index (zero-based).
     * @param column  the column (or category) index (zero-based).
     *
     * @return The shape.
     */
    public Shape getItemShape(int row, int column) {
        return getSeriesShape(row);
    }

    /**
     * Returns a shape used to represent the items in a series.
     *
     * @param series  the series (zero-based index).
     *
     * @return  The shape.
     */
    public Shape getSeriesShape(int series) {

        // return the override, if there is one...
        if (this.shape != null) {
            return this.shape;
        }

        // otherwise look up the shape list
        Shape shape = this.shapeList.getShape(series);
        if (shape == null) {
            DrawingSupplier supplier = getDrawingSupplier();
            if (supplier != null) {
                shape = supplier.getNextShape();
                this.shapeList.setShape(series, shape);
            }
            else {
                shape = this.baseShape;
            }
        }
        return shape;

    }

    /**
     * Sets the shape for ALL series (optional).
     * 
     * @param shape  the shape (<code>null</code> permitted).
     */
    public void setShape(Shape shape) {
        this.shape = shape;    
    }
    
    /**
     * Sets the shape used for a series.
     *
     * @param series  the series index (zero-based).
     * @param shape  the shape.
     */
    public void setSeriesShape(int series, Shape shape) {
        this.shapeList.setShape(series, shape);
    }

    /**
     * Returns the base shape.
     *
     * @return The base shape.
     */
    public Shape getBaseShape() {
        return this.baseShape;
    }

    /**
     * Sets the base shape.
     *
     * @param shape  the shape.
     */
    public void setBaseShape(Shape shape) {
        this.baseShape = shape;
    }

    /**
     * Creates and returns a translated version of a shape.
     *
     * @param shape  the base shape.
     * @param translateX  the x translation.
     * @param translateY  the y translation.
     *
     * @return The shape.
     */
    protected synchronized Shape createTransformedShape(Shape shape,
                                                        double translateX, double translateY) {

        AffineTransform transformer = new AffineTransform();
        transformer.setToTranslation(translateX, translateY);
        return transformer.createTransformedShape(shape);

    }

    // ITEM LABEL VISIBILITY...

    /**
     * Returns <code>true</code> if an item label is visible, and <code>false</code> otherwise.
     * 
     * @param row  the row.
     * @param column  the column.
     * 
     * @return A boolean.
     */
    public boolean isItemLabelVisible(int row, int column) {
        return isSeriesItemLabelsVisible(row);
    }

    /**
     * Returns <code>true</code> if the item labels for a series area visible, and 
     * <code>false</code> otherwise.
     * 
     * @param series  the series.
     * 
     * @return A boolean.
     */    
    public boolean isSeriesItemLabelsVisible(int series) {

        // return the override, if there is one...
        if (this.itemLabelsVisible != null) {
            return this.itemLabelsVisible.booleanValue();
        }

        // otherwise look up the boolean table
        Boolean b = this.itemLabelsVisibleList.getBoolean(series);
        if (b == null) {
            b = this.baseItemLabelsVisible;
        }
        if (b == null) {
            b = Boolean.FALSE;
        }
        return b.booleanValue();

    }
    
    /**
     * Sets the visibility of the item labels for ALL series.
     * 
     * @param visible  the flag.
     */
    public void setItemLabelsVisible(boolean visible) {        
        if (visible) {
            setItemLabelsVisible(Boolean.TRUE);
        }
        else {
            setItemLabelsVisible(Boolean.FALSE);
        }
        // The following alternative is not supported in JDK 1.2.2:
        // setItemLabelsVisible(Boolean.valueOf(visible));
    }
    
    /**
     * Sets the visibility of the item labels for ALL series (optional).
     * 
     * @param visible  the flag (<code>null</code> permitted).
     */
    public void setItemLabelsVisible(Boolean visible) {
        this.itemLabelsVisible = visible;
    }

    /**
     * Sets the visibility of the item labels for a series.
     * 
     * @param series  the series.
     * @param visible  the flag.
     */
    public void setSeriesItemLabelsVisible(int series, Boolean visible) {
        this.itemLabelsVisibleList.setBoolean(series, visible);
    }

    /**
     * Returns the base setting for item label visibility.
     * 
     * @return A flag.
     */
    public Boolean getBaseItemLabelsVisible() {
        return this.baseItemLabelsVisible;
    }

    /**
     * Sets the base setting for item label visibility.
     * 
     * @param visible  the flag.
     */
    public void setBaseItemLabelsVisible(Boolean visible) {
        this.baseItemLabelsVisible = visible;
    }

    // ITEM LABEL FONT...

    /**
     * Returns the font for an item label.
     * 
     * @param row  the row.
     * @param column  the column.
     * 
     * @return The font.
     */
    public Font getItemLabelFont(int row, int column) {
        return getSeriesItemLabelFont(row);
    }

    /**
     * Returns the font for all the item labels in a series.
     * 
     * @param series  the series.
     * 
     * @return The font.
     */
    public Font getSeriesItemLabelFont(int series) {

        // return the override, if there is one...
        if (this.itemLabelFont != null) {
            return this.itemLabelFont;
        }

        // otherwise look up the font table
        Font font = (Font) this.itemLabelFontList.get(series);
        if (font == null) {
            font = this.baseItemLabelFont;
        }
        return font;

    }

    /**
     * Sets the item label font for ALL series (optional).
     * 
     * @param font  the font.
     */
    public void setItemLabelFont(Font font) {
        this.itemLabelFont = font;
    }

    /**
     * Sets the item label font for a series.
     * 
     * @param series  the series.
     * @param font  the font.
     */
    public void setSeriesItemLabelFont(int series, Font font) {
        this.itemLabelFontList.set(series, font);
    }

    /**
     * Returns the base item label font.
     * 
     * @return The base item label font.
     */
    public Font getBaseItemLabelFont() {
        return this.baseItemLabelFont;
    }

    /**
     * Sets the base item label font.
     * 
     * @param font  the font.
     */
    public void setBaseItemLabelFont(Font font) {
        this.baseItemLabelFont = font;
    }

    // ITEM LABEL PAINT...

    /**
     * Returns the paint used to draw an item label.
     * 
     * @param row  the row index (zero based).
     * @param column  the column index (zero based).
     * 
     * @return The paint.
     */
    public Paint getItemLabelPaint(int row, int column) {
        return getSeriesItemLabelPaint(row);
    }

    /**
     * Returns the paint used to draw the item labels for a series.
     * 
     * @param series  the series index (zero based).
     * 
     * @return The paint.
     */
    public Paint getSeriesItemLabelPaint(int series) {

        // return the override, if there is one...
        if (this.itemLabelPaint != null) {
            return this.itemLabelPaint;
        }

        // otherwise look up the paint table
        Paint paint = (Paint) this.itemLabelPaintList.getPaint(series);
        if (paint == null) {
            paint = this.baseItemLabelPaint;
        }
        return paint;

    }

    /**
     * Sets the item label paint for ALL series.
     * 
     * @param paint  the paint (<code>null</code> permitted).
     */
    public void setItemLabelPaint(Paint paint) {
        this.itemLabelPaint = paint;
    }

    /**
     * Sets the item label paint for a series.
     * 
     * @param series  the series (zero based index).
     * @param paint  the paint (<code>null</code> permitted).
     */
    public void setSeriesItemLabelPaint(int series, Paint paint) {
        this.itemLabelPaintList.setPaint(series, paint);
    }

    /**
     * Returns the base item label paint.
     * 
     * @return The base item label paint.
     */
    public Paint getBaseItemLabelPaint() {
        return this.baseItemLabelPaint;
    }

    /**
     * Sets the base item label paint.
     * 
     * @param paint  the paint.
     */
    public void setBaseItemLabelPaint(Paint paint) {
        this.baseItemLabelPaint = paint;
    }

    // ITEM LABEL ANCHOR...

    /**
     * Returns the item label anchor.
     * 
     * @param row  the row.
     * @param column  the column.
     * 
     * @return The item label anchor.
     */
    public ItemLabelAnchor getItemLabelAnchor(int row, int column) {
        return getSeriesItemLabelAnchor(row);
    }

    /**
     * Returns the item label anchor for all labels in a series.
     * 
     * @param series  the series.
     * 
     * @return The anchor point.
     */
    public ItemLabelAnchor getSeriesItemLabelAnchor(int series) {

        // return the override, if there is one...
        if (this.itemLabelAnchor != null) {
            return this.itemLabelAnchor;
        }

        // otherwise look up the anchor table
        ItemLabelAnchor anchor = (ItemLabelAnchor) this.itemLabelAnchorList.get(series);
        if (anchor == null) {
            anchor = this.baseItemLabelAnchor;
        }
        return anchor;

    }

    /**
     * Sets the item label anchor.
     * 
     * @param anchor  the anchor.
     */
    public void setItemLabelAnchor(ItemLabelAnchor anchor) {
        this.itemLabelAnchor = anchor;
    }

    /**
     * Sets the series item label anchor.
     * 
     * @param series  the series.
     * @param anchor  the anchor.
     */
    public void setSeriesItemLabelAnchor(int series, ItemLabelAnchor anchor) {
        this.itemLabelAnchorList.set(series, anchor);
    }

    /**
     * Returns the base item label anchor.
     * 
     * @return The anchor point.
     */
    public ItemLabelAnchor getBaseItemLabelAnchor() {
        return this.baseItemLabelAnchor;
    }

    /**
     * Sets the base item label anchor.
     * 
     * @param anchor  the anchor.
     */
    public void setBaseItemLabelAnchor(ItemLabelAnchor anchor) {
        this.baseItemLabelAnchor = anchor;
    }

    // TEXT ANCHOR...

    /**
     * Returns the text anchor for an item label.  This is a point relative to the label that
     * will be aligned with another anchor point that is relative to the data item.
     * 
     * @param row  the row.
     * @param column  the column.
     * 
     * @return The text anchor.
     */
    public TextAnchor getItemLabelTextAnchor(int row, int column) {
        return getSeriesItemLabelTextAnchor(row);
    }

    /**
     * Returns the text anchor for all item labels in a series.
     * 
     * @param series  the series.
     * 
     * @return The text anchor.
     */
    public TextAnchor getSeriesItemLabelTextAnchor(int series) {

        // return the override, if there is one...
        if (this.itemLabelTextAnchor != null) {
            return this.itemLabelTextAnchor;
        }

        // otherwise look up the anchor table
        TextAnchor anchor = (TextAnchor) this.itemLabelTextAnchorList.get(series);
        if (anchor == null) {
            anchor = this.baseItemLabelTextAnchor;
        }
        return anchor;

    }

    /**
     * Sets the item label text anchor for ALL series (optional).
     * 
     * @param anchor  the anchor (<code>null</code> permitted).
     */
    public void setItemLabelTextAnchor(TextAnchor anchor) {
        this.itemLabelTextAnchor = anchor;
    }

    /**
     * Sets the item label text anchor for a series.
     * 
     * @param series  the series.
     * @param anchor  the anchor.
     */
    public void setSeriesItemLabelTextAnchor(int series, TextAnchor anchor) {
        this.itemLabelTextAnchorList.set(series, anchor);
    }

    /**
     * Returns the base item label text anchor.
     * 
     * @return The text anchor.
     */
    public TextAnchor getBaseItemLabelTextAnchor() {
        return this.baseItemLabelTextAnchor;
    }

    /**
     * Sets the default item label text anchor.
     * 
     * @param anchor  the anchor.
     */
    public void setBaseItemLabelTextAnchor(TextAnchor anchor) {
        this.baseItemLabelTextAnchor = anchor;
    }

    // ROTATION ANCHOR...

    /**
     * Returns the rotation anchor for an item label.
     * 
     * @param row  the row.
     * @param column  the column.
     * 
     * @return The rotation anchor.
     */
    public TextAnchor getItemLabelRotationAnchor(int row, int column) {
        return getSeriesItemLabelRotationAnchor(row);
    }

    /**
     * Returns the rotation anchor for all item labels in a series.
     * 
     * @param series  the series.
     * 
     * @return The rotation anchor.
     */
    public TextAnchor getSeriesItemLabelRotationAnchor(int series) {

        // return the override, if there is one...
        if (this.itemLabelRotationAnchor != null) {
            return this.itemLabelRotationAnchor;
        }

        // otherwise look up the anchor table
        TextAnchor anchor = (TextAnchor) this.itemLabelRotationAnchorList.get(series);
        if (anchor == null) {
            anchor = this.baseItemLabelRotationAnchor;
        }
        return anchor;

    }
    
    /**
     * Sets the rotation anchor for the item labels in ALL series.
     * 
     * @param anchor  the anchor (<code>null</code> permitted).
     */
    public void setItemLabelRotationAnchor(TextAnchor anchor) {
        this.itemLabelRotationAnchor = anchor;
    }

    /**
     * Sets the item label rotation anchor point for a series.
     * 
     * @param series  the series.
     * @param anchor  the anchor point.
     */
    public void setSeriesItemLabelRotationAnchor(int series, TextAnchor anchor) {
        this.itemLabelRotationAnchorList.set(series, anchor);
    }

    /**
     * Returns the base item label rotation anchor point.
     * 
     * @return The anchor point.
     */
    public TextAnchor getBaseItemLabelRotationAnchor() {
        return this.baseItemLabelRotationAnchor;
    }

    /**
     * Sets the base item label rotation anchor point.
     * 
     * @param anchor  the anchor point.
     */
    public void setBaseItemLabelRotationAnchor(TextAnchor anchor) {
        this.baseItemLabelRotationAnchor = anchor;
    }

    // ANGLE...

    /**
     * Returns the angle for an item label.
     * 
     * @param row  the row.
     * @param column  the column.
     * 
     * @return The angle (in radians).
     */
    public Number getItemLabelAngle(int row, int column) {
        return getSeriesItemLabelAngle(row);
    }

    /**
     * Returns the angle for all the item labels in a series.
     * 
     * @param series  the series.
     * 
     * @return The angle (in radians).
     */
    public Number getSeriesItemLabelAngle(int series) {

        // return the override, if there is one...
        if (this.itemLabelAngle != null) {
            return this.itemLabelAngle;
        }

        // otherwise look up the angle table
        Number angle = (Number) this.itemLabelAngleList.get(series);
        if (angle == null) {
            angle = this.baseItemLabelAngle;
        }
        return angle;

    }

    /**
     * Sets the angle for the item labels in ALL series (optional).
     * 
     * @param angle  the angle (<code>null</code> permitted).
     */
    public void setItemLabelAngle(Number angle) {
        this.itemLabelAngle = angle;
    }

    /**
     * Sets the angle for all item labels in a series.
     * 
     * @param series  the series.
     * @param angle  the angle.
     */
    public void setSeriesAngle(int series, Number angle) {
        this.itemLabelAngleList.set(series, angle);
    }

    /**
     * Returns the base item label angle.
     * 
     * @return The angle.
     */
    public Number getBaseItemLabelAngle() {
        return this.baseItemLabelAngle;
    }

    /**
     * Sets the base item label angle.
     * 
     * @param angle  the angle.
     */
    public void setBaseAngle(Number angle) {
        this.baseItemLabelAngle = angle;
    }

    /**
     * Adds a property change listener to the renderer.
     *
     * @param listener  the listener.
     */
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        listeners.addPropertyChangeListener(listener);
    }

    /**
     * Removes a property change listener from the renderer.
     *
     * @param listener  the listener.
     */
    public void removePropertyChangeListener(PropertyChangeListener listener) {
        listeners.removePropertyChangeListener(listener);
    }

    /**
     * Notifies registered listeners that a property of the renderer has changed.
     *
     * @param propertyName  the name of the property.
     * @param oldValue  the old value.
     * @param newValue  the new value.
     */
    protected void firePropertyChanged(String propertyName, Object oldValue, Object newValue) {
        listeners.firePropertyChange(propertyName, oldValue, newValue);
    }

    /**
     * Tests this renderer for equality with another object.
     *
     * @param obj  the object.
     *
     * @return <code>true</code> or <code>false</code>.
     */
    public boolean equals(Object obj) {

        if (obj == null) {
            return false;
        }

        if (obj == this) {
            return true;
        }

        if (obj instanceof AbstractRenderer) {
            AbstractRenderer renderer = (AbstractRenderer) obj;

            boolean b2 = ObjectUtils.equal(this.paint, renderer.paint);
            boolean b3 = ObjectUtils.equal(this.paintList, renderer.paintList);
            boolean b4 = ObjectUtils.equal(this.basePaint, renderer.basePaint);

            boolean b5 = ObjectUtils.equal(this.outlinePaint, renderer.outlinePaint);
            boolean b6 = ObjectUtils.equal(this.outlinePaintList, renderer.outlinePaintList);
            boolean b7 = ObjectUtils.equal(this.baseOutlinePaint, renderer.baseOutlinePaint);

            boolean b8 = ObjectUtils.equal(this.stroke, renderer.stroke);
            boolean b9 = ObjectUtils.equal(this.strokeList, renderer.strokeList);
            boolean b10 = ObjectUtils.equal(this.baseStroke, renderer.baseStroke);

            boolean b11 = ObjectUtils.equal(this.outlineStroke, renderer.outlineStroke);
            boolean b12 = ObjectUtils.equal(this.outlineStrokeList, renderer.outlineStrokeList);
            boolean b13 = ObjectUtils.equal(this.baseOutlineStroke, renderer.baseOutlineStroke);

            boolean b35 = ObjectUtils.equal(this.shape, renderer.shape);
            boolean b36 = ObjectUtils.equal(this.shapeList, renderer.shapeList);
            boolean b37 = ObjectUtils.equal(this.baseShape, renderer.baseShape);

            boolean b14 = ObjectUtils.equal(this.itemLabelsVisible, renderer.itemLabelsVisible);
            boolean b15 = ObjectUtils.equal(this.itemLabelsVisibleList, 
                                            renderer.itemLabelsVisibleList);
            boolean b16 = ObjectUtils.equal(this.baseItemLabelsVisible, 
                                            renderer.baseItemLabelsVisible);

            boolean b17 = ObjectUtils.equal(this.itemLabelFont, renderer.itemLabelFont);
            boolean b18 = ObjectUtils.equal(this.itemLabelFontList, renderer.itemLabelFontList);
            boolean b19 = ObjectUtils.equal(this.baseItemLabelFont, renderer.baseItemLabelFont);
 
            boolean b20 = ObjectUtils.equal(this.itemLabelPaint, renderer.itemLabelPaint);
            boolean b21 = ObjectUtils.equal(this.itemLabelPaintList, renderer.itemLabelPaintList);
            boolean b22 = ObjectUtils.equal(this.baseItemLabelPaint, renderer.baseItemLabelPaint);

            boolean b23 = ObjectUtils.equal(this.itemLabelAnchor, renderer.itemLabelAnchor);
            boolean b24 = ObjectUtils.equal(this.itemLabelAnchorList, renderer.itemLabelAnchorList);
            boolean b25 = ObjectUtils.equal(this.baseItemLabelAnchor, renderer.baseItemLabelAnchor);

            boolean b26 = ObjectUtils.equal(this.itemLabelTextAnchor, renderer.itemLabelTextAnchor);
            boolean b27 = ObjectUtils.equal(this.itemLabelTextAnchorList, 
                                            renderer.itemLabelTextAnchorList);
            boolean b28 = ObjectUtils.equal(this.baseItemLabelTextAnchor, 
                                            renderer.baseItemLabelTextAnchor);

            boolean b29 = ObjectUtils.equal(this.itemLabelRotationAnchor, 
                                            renderer.itemLabelRotationAnchor);
            boolean b30 = ObjectUtils.equal(this.itemLabelRotationAnchorList, 
                                            renderer.itemLabelRotationAnchorList);
            boolean b31 = ObjectUtils.equal(this.baseItemLabelRotationAnchor, 
                                            renderer.baseItemLabelRotationAnchor);

            boolean b32 = ObjectUtils.equal(this.itemLabelAngle, renderer.itemLabelAngle);
            boolean b33 = ObjectUtils.equal(this.itemLabelAngleList, renderer.itemLabelAngleList);
            boolean b34 = ObjectUtils.equal(this.baseItemLabelAngle, renderer.baseItemLabelAngle);

            return b2 && b3 && b4 
                && b5 && b6 && b7 
                && b8 && b9 && b10 
                && b11 && b12 && b13 
                && b14 && b15 && b16 
                && b17 && b18 && b19 
                && b20 && b21 && b22 
                && b23 && b24 && b25 
                && b26 && b27 && b28 
                && b29 && b30 && b31 
                && b32 && b33 && b34 
                && b35 && b36 && b37;
        }

        return false;

    }
    
    /**
     * Returns an independent copy of the renderer.
     * 
     * @return A clone.
     * 
     * @throws CloneNotSupportedException if some component of the renderer does not support 
     *         cloning.
     */
    protected Object clone() throws CloneNotSupportedException {
        AbstractRenderer clone = (AbstractRenderer) super.clone();
        
        // 'paint' : immutable, no need to clone reference
        if (this.paintList != null) {
            clone.paintList = (PaintList) this.paintList.clone();
        }
        // 'basePaint' : immutable, no need to clone reference
        
        // 'outlinePaint' : immutable, no need to clone reference
        if (this.outlinePaintList != null) {
            clone.outlinePaintList = (PaintList) this.outlinePaintList.clone();
        }
        // 'baseOutlinePaint' : immutable, no need to clone reference
        
        // 'stroke' : immutable, no need to clone reference
        if (this.strokeList != null) {
            clone.strokeList = (ObjectList) this.strokeList.clone();
        }
        // 'baseStroke' : immutable, no need to clone reference
        
        // 'outlineStroke' : immutable, no need to clone reference
        if (this.outlineStrokeList != null) {
            clone.outlineStrokeList = (ObjectList) this.outlineStrokeList.clone();
        }
        // 'baseOutlineStroke' : immutable, no need to clone reference
        
        if (this.shape != null) {
            clone.shape = ShapeUtils.clone(this.shape);
        }
        if (this.shapeList != null) {
            clone.strokeList = (ObjectList) this.strokeList.clone();
        }
        if (this.baseShape != null) {
            clone.baseShape = ShapeUtils.clone(this.baseShape);
        }
        
        // 'itemLabelsVisible' : immutable, no need to clone reference
        if (this.itemLabelsVisibleList != null) {
            clone.itemLabelsVisibleList = (BooleanList) this.itemLabelsVisibleList.clone();
        }
        // 'basePaint' : immutable, no need to clone reference
        
        // 'itemLabelFont' : immutable, no need to clone reference
        if (this.itemLabelFontList != null) {
            clone.itemLabelFontList = (ObjectList) this.itemLabelFontList.clone();
        }
        // 'baseItemLabelFont' : immutable, no need to clone reference

        // 'itemLabelPaint' : immutable, no need to clone reference
        if (this.itemLabelPaintList != null) {
            clone.itemLabelPaintList = (PaintList) this.itemLabelPaintList.clone();
        }
        // 'baseItemLabelPaint' : immutable, no need to clone reference
        
        // 'itemLabelAnchor' : immutable, no need to clone reference
        if (this.itemLabelAnchorList != null) {
            clone.itemLabelAnchorList = (ObjectList) this.itemLabelAnchorList.clone();
        }
        // 'baseItemLabelAnchor' : immutable, no need to clone reference

        // 'itemLabelTextAnchor' : immutable, no need to clone reference
        if (this.itemLabelTextAnchorList != null) {
            clone.itemLabelTextAnchorList = (ObjectList) this.itemLabelTextAnchorList.clone();
        }
        // 'baseItemLabelTextAnchor' : immutable, no need to clone reference

        // 'itemLabelRotationAnchor' : immutable, no need to clone reference
        if (this.itemLabelRotationAnchorList != null) {
            clone.itemLabelRotationAnchorList 
                = (ObjectList) this.itemLabelRotationAnchorList.clone();
        }
        // 'baseItemLabelRotationAnchor' : immutable, no need to clone reference

        // 'itemLabelAngle' : immutable, no need to clone reference
        if (this.itemLabelAngleList != null) {
            clone.itemLabelAngleList = (ObjectList) this.itemLabelAngleList.clone();
        }
        // 'baseItemLabelFont' : immutable, no need to clone reference

        return clone;
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
        SerialUtilities.writePaint(this.basePaint, stream);
        SerialUtilities.writePaint(this.outlinePaint, stream);
        SerialUtilities.writePaint(this.baseOutlinePaint, stream);
        SerialUtilities.writeStroke(this.stroke, stream);
        SerialUtilities.writeStroke(this.baseStroke, stream);
        SerialUtilities.writeStroke(this.outlineStroke, stream);
        SerialUtilities.writeStroke(this.baseOutlineStroke, stream);
        SerialUtilities.writeShape(this.shape, stream);
        SerialUtilities.writeShape(this.baseShape, stream);
        SerialUtilities.writePaint(this.itemLabelPaint, stream);
        SerialUtilities.writePaint(this.baseItemLabelPaint, stream);

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
        this.basePaint = SerialUtilities.readPaint(stream);
        this.outlinePaint = SerialUtilities.readPaint(stream);
        this.baseOutlinePaint = SerialUtilities.readPaint(stream);
        this.stroke = SerialUtilities.readStroke(stream);
        this.baseStroke = SerialUtilities.readStroke(stream);
        this.outlineStroke = SerialUtilities.readStroke(stream);
        this.baseOutlineStroke = SerialUtilities.readStroke(stream);
        this.shape = SerialUtilities.readShape(stream);
        this.baseShape = SerialUtilities.readShape(stream);
        this.itemLabelPaint = SerialUtilities.readPaint(stream);
        this.baseItemLabelPaint = SerialUtilities.readPaint(stream);

    }

}
