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
 * $Id: AbstractRenderer.java,v 1.1 2007/10/10 20:07:35 vauchers Exp $
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
import org.jfree.chart.plot.Plot;
import org.jfree.io.SerialUtilities;
import org.jfree.ui.TextAnchor;
import org.jfree.util.ObjectUtils;

/**
 * Base class providing common services for renderers.
 *
 * @author David Gilbert
 */
public abstract class AbstractRenderer implements Serializable {

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

    /** The plot that the renderer is assigned to. */
    private transient Plot plot;

    /** The paint for ALL series (optional). */
    private transient Paint paint;

    /** The paint table. */
    private PaintTable paintTable;

    /** The default paint. */
    private transient Paint defaultPaint;

    /** The outline paint for ALL series (optional). */
    private transient Paint outlinePaint;

    /** The outline paint table. */
    private PaintTable outlinePaintTable;

    /** The default outline paint. */
    private transient Paint defaultOutlinePaint;

    /** The stroke for ALL series (optional). */
    private transient Stroke stroke;

    /** The stroke table. */
    private StrokeTable strokeTable;

    /** The default stroke. */
    private transient Stroke defaultStroke;

    /** The outline stroke for ALL series (optional). */
    private transient Stroke outlineStroke;

    /** The outline stroke table. */
    private StrokeTable outlineStrokeTable;

    /** The default outline stroke. */
    private transient Stroke defaultOutlineStroke;

    /** The shape for ALL series (optional). */
    private transient Shape shape;

    /** A shape table. */
    private ShapeTable shapeTable;

    /** The default shape. */
    private transient Shape defaultShape;

    /** Visibility of the item labels for ALL series (optional). */
    private Boolean itemLabelsVisible;

    /** Visibility of the item labels PER series. */
    private BooleanTable itemLabelsVisibleTable;

    /** The default item labels visible. */
    private Boolean defaultItemLabelsVisible;

    /** The item label font for ALL series (optional). */
    private Font itemLabelFont;

    /** The item label font table (one font per series). */
    private FontTable itemLabelFontTable;

    /** The default item label font. */
    private Font defaultItemLabelFont;

    /** The item label paint for ALL series. */
    private transient Paint itemLabelPaint;

    /** The item label paint table (one paint per series). */
    private PaintTable itemLabelPaintTable;

    /** The default item label paint. */
    private transient Paint defaultItemLabelPaint;

    /** The item label anchor. */
    private ItemLabelAnchor itemLabelAnchor;

    /** The item label anchor table (one anchor per series). */
    private ItemLabelAnchorTable itemLabelAnchorTable;

    /** The default item label anchor. */
    private ItemLabelAnchor defaultItemLabelAnchor;

    /** The item label text anchor. */
    private TextAnchor itemLabelTextAnchor;

    /** The item label text anchor table (one anchor per series). */
    private TextAnchorTable itemLabelTextAnchorTable;

    /** The default item label text anchor. */
    private TextAnchor defaultItemLabelTextAnchor;

    /** The rotation anchor. */
    private TextAnchor itemLabelRotationAnchor;

    /** The rotation anchor table (one anchor per series). */
    private TextAnchorTable itemLabelRotationAnchorTable;

    /** The default rotation anchor. */
    private TextAnchor defaultItemLabelRotationAnchor;

    /** The angle. */
    private Number itemLabelAngle;

    /** The angle table (one angle per series). */
    private NumberTable itemLabelAngleTable;

    /** The default angle. */
    private Number defaultItemLabelAngle;

    /** A temporary reference to chart rendering info (may be <code>null</code>). */
    private transient ChartRenderingInfo info;

    /** Support class for the property change listener mechanism. */
    private transient PropertyChangeSupport listeners;

    /**
     * Default constructor.
     */
    public AbstractRenderer() {

        this.paint = null;
        this.paintTable = new PaintTable();
        this.defaultPaint = DEFAULT_PAINT;

        this.outlinePaint = null;
        this.outlinePaintTable = new PaintTable();
        this.defaultOutlinePaint = DEFAULT_OUTLINE_PAINT;

        this.stroke = null;
        this.strokeTable = new StrokeTable();
        this.defaultStroke = DEFAULT_STROKE;

        this.outlineStroke = null;
        this.outlineStrokeTable = new StrokeTable();
        this.defaultOutlineStroke = DEFAULT_OUTLINE_STROKE;

        this.shape = null;
        this.shapeTable = new ShapeTable();
        this.defaultShape = DEFAULT_SHAPE;

        this.itemLabelsVisible = null;
        this.itemLabelsVisibleTable = new BooleanTable();
        this.defaultItemLabelsVisible = Boolean.FALSE;

        this.itemLabelFont = null;
        this.itemLabelFontTable = new FontTable();
        this.defaultItemLabelFont = new Font("SansSerif", Font.PLAIN, 10);

        this.itemLabelPaint = null;
        this.itemLabelPaintTable = new PaintTable();
        this.defaultItemLabelPaint = Color.black;

        this.itemLabelAnchor = null;
        this.itemLabelAnchorTable = new ItemLabelAnchorTable();
        this.defaultItemLabelAnchor = ItemLabelAnchor.OUTSIDE3;

        this.itemLabelTextAnchor = null;
        this.itemLabelTextAnchorTable = new TextAnchorTable();
        this.defaultItemLabelTextAnchor = TextAnchor.CENTER_LEFT;

        this.itemLabelRotationAnchor = null;
        this.itemLabelRotationAnchorTable = new TextAnchorTable();
        this.defaultItemLabelRotationAnchor = TextAnchor.CENTER_LEFT;

        this.itemLabelAngle = null;
        this.itemLabelAngleTable = new NumberTable();
        this.defaultItemLabelAngle = new Double(0.0);

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
     * @param dataset  the dataset index (zero-based).
     * @param series  the series index (zero-based).
     *
     * @return  The paint.
     */
    public Paint getSeriesPaint(int series) {

        // return the override, if there is one...
        if (this.paint != null) {
            return this.paint;
        }

        // otherwise look up the paint table
        Paint paint = this.paintTable.getPaint(0, series);
        if (paint == null) {
            DrawingSupplier supplier = getDrawingSupplier();
            if (supplier != null) {
                paint = supplier.getNextPaint();
                this.paintTable.setPaint(0, series, paint);
            }
            else {
                paint = this.defaultPaint;
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
        this.paintTable.setPaint(0, series, paint);
    }

    /**
     * Returns the default paint.
     *
     * @return The default paint.
     */
    public Paint getDefaultPaint() {
        return this.defaultPaint;
    }

    /**
     * Sets the default paint.
     * <p>
     * In most cases, the renderer's paint table will be active and so this default value will
     * not be used.
     *
     * @param paint  the paint.
     */
    public void setDefaultPaint(Paint paint) {
        this.defaultPaint = paint;
    }

    // OUTLINE PAINT
    
    /**
     * Returns the paint used to outline data items as they are drawn.
     * <p>
     * The default implementation passes control to the getSeriesOutlinePaint method.  You can
     * override this method if you require different behaviour.
     *
     * @param dataset  the dataset index (zero-based).
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
     * @param dataset  the dataset (zero-based index).
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
        Paint paint = this.outlinePaintTable.getPaint(0, series);
        if (paint == null) {
            DrawingSupplier supplier = getDrawingSupplier();
            if (supplier != null) {
                paint = supplier.getNextOutlinePaint();
                this.outlinePaintTable.setPaint(0, series, paint);
            }
            else {
                paint = this.defaultOutlinePaint;
            }
        }
        return paint;

    }

    /**
     * Sets the paint used for a series outline.
     *
     * @param dataset  the dataset index (zero-based).
     * @param series  the series index (zero-based).
     * @param paint  the paint.
     */
    public void setSeriesOutlinePaint(int series, Paint paint) {
        this.outlinePaintTable.setPaint(0, series, paint);
    }

    /**
     * Returns the default outline paint.
     *
     * @return The default outline paint.
     */
    public Paint getDefaultOutlinePaint() {
        return this.defaultOutlinePaint;
    }

    /**
     * Sets the default outline paint.
     *
     * @param paint  the paint.
     */
    public void setDefaultOutlinePaint(Paint paint) {
        this.defaultOutlinePaint = paint;
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
        Stroke stroke = this.strokeTable.getStroke(0, series);
        if (stroke == null) {
            DrawingSupplier supplier = getDrawingSupplier();
            if (supplier != null) {
                stroke = supplier.getNextStroke();
                this.strokeTable.setStroke(0, series, stroke);
            }
            else {
                stroke = this.defaultStroke;
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
     * @param dataset  the dataset index (zero-based).
     * @param series  the series index (zero-based).
     * @param stroke  the stroke.
     */
    public void setSeriesStroke(int series, Stroke stroke) {
        this.strokeTable.setStroke(0, series, stroke);
    }

    /**
     * Returns the default stroke.
     *
     * @return The default stroke.
     */
    public Stroke getDefaultStroke() {
        return this.defaultStroke;
    }

    /**
     * Sets the default stroke.
     *
     * @param stroke  the stroke.
     */
    public void setDefaultStroke(Stroke stroke) {
        this.defaultStroke = stroke;
    }

    // OUTLINE STROKE 
    
    /**
     * Returns the stroke used to outline data items.
     * <p>
     * The default implementation passes control to the getSeriesOutlineStroke method.
     * You can override this method if you require different behaviour.
     *
     * @param dataset  the dataset index (zero-based).
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
     * @param dataset  the dataset (zero-based index).
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
        Stroke stroke = this.outlineStrokeTable.getStroke(0, series);
        if (stroke == null) {
            DrawingSupplier supplier = getDrawingSupplier();
            if (supplier != null) {
                stroke = supplier.getNextStroke();
                this.strokeTable.setStroke(0, series, stroke);
            }
            else {
                stroke = this.defaultOutlineStroke;
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
     * @param dataset  the dataset index (zero-based).
     * @param series  the series index (zero-based).
     * @param stroke  the stroke.
     */
    public void setSeriesOutlineStroke(int series, Stroke stroke) {
        this.outlineStrokeTable.setStroke(0, series, stroke);
    }

    /**
     * Returns the default outline stroke.
     *
     * @return The default outline stroke.
     */
    public Stroke getDefaultOutlineStroke() {
        return this.defaultOutlineStroke;
    }

    /**
     * Sets the default outline stroke.
     *
     * @param stroke  the default outline stroke.
     */
    public void setDefaultOutlineStroke(Stroke stroke) {
        this.defaultOutlineStroke = stroke;
    }

    // SHAPE
    
    /**
     * Returns a shape used to represent a data item.
     * <p>
     * The default implementation passes control to the getSeriesShape method.  You can override
     * this method if you require different behaviour.
     *
     * @param dataset  the dataset index (zero-based).
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

        // otherwise look up the shape table
        Shape shape = this.shapeTable.getShape(0, series);
        if (shape == null) {
            DrawingSupplier supplier = getDrawingSupplier();
            if (supplier != null) {
                shape = supplier.getNextShape();
                this.shapeTable.setShape(0, series, shape);
            }
            else {
                shape = this.defaultShape;
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
     * @param dataset  the dataset index (zero-based).
     * @param series  the series index (zero-based).
     * @param shape  the shape.
     */
    public void setSeriesShape(int series, Shape shape) {
        this.shapeTable.setShape(0, series, shape);
    }

    /**
     * Returns the default shape.
     *
     * @return The default shape.
     */
    public Shape getDefaultShape() {
        return this.defaultShape;
    }

    /**
     * Sets the default shape.
     *
     * @param shape  the shape.
     */
    public void setDefaultShape(Shape shape) {
        this.defaultShape = shape;
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
        Boolean b = this.itemLabelsVisibleTable.getBoolean(0, series);
        if (b == null) {
            b = this.defaultItemLabelsVisible;
        }
        if (b == null) {
            b = Boolean.FALSE;
        }
        return b.booleanValue();

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
        this.itemLabelsVisibleTable.setBoolean(0, series, visible);
    }

    /**
     * Returns the default setting for item label visibility.
     * 
     * @return A flag.
     */
    public Boolean getDefaultItemLabelsVisible() {
        return this.defaultItemLabelsVisible;
    }

    /**
     * Sets the default setting for item label visibility.
     * 
     * @param visible  the flag.
     */
    public void setDefaultItemLabelsVisible(Boolean visible) {
        this.defaultItemLabelsVisible = visible;
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
        Font font = this.itemLabelFontTable.getFont(0, series);
        if (font == null) {
            font = this.defaultItemLabelFont;
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
        this.itemLabelFontTable.setFont(0, series, font);
    }

    /**
     * Returns the default item label font.
     * 
     * @return The default item label font.
     */
    public Font getDefaultItemLabelFont() {
        return this.defaultItemLabelFont;
    }

    /**
     * Sets the default item label font.
     * 
     * @param font  the font.
     */
    public void setDefaultItemLabelFont(Font font) {
        this.defaultItemLabelFont = font;
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
        Paint paint = this.itemLabelPaintTable.getPaint(0, series);
        if (paint == null) {
            paint = this.defaultItemLabelPaint;
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
        this.itemLabelPaintTable.setPaint(0, series, paint);
    }

    /**
     * Returns the default item label paint.
     * 
     * @return The default item label paint.
     */
    public Paint getDefaultItemLabelPaint() {
        return this.defaultItemLabelPaint;
    }

    /**
     * Sets the default item label paint.
     * 
     * @param paint  the paint.
     */
    public void setDefaultItemLabelPaint(Paint paint) {
        this.defaultItemLabelPaint = paint;
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
        ItemLabelAnchor anchor = this.itemLabelAnchorTable.getAnchor(0, series);
        if (anchor == null) {
            anchor = this.defaultItemLabelAnchor;
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
        this.itemLabelAnchorTable.setAnchor(0, series, anchor);
    }

    /**
     * Returns the default item label anchor.
     * 
     * @return The anchor point.
     */
    public ItemLabelAnchor getDefaultItemLabelAnchor() {
        return this.defaultItemLabelAnchor;
    }

    /**
     * Sets the default item label anchor.
     * 
     * @param anchor  the anchor.
     */
    public void setDefaultItemLabelAnchor(ItemLabelAnchor anchor) {
        this.defaultItemLabelAnchor = anchor;
    }

    // TEXT ANCHOR...

    /**
     * Returns the text anchor for an item label.  This is a point relative to the label that
     * will be aligned with another anchor point that is relative to the data item.
     * 
     * @param row  the row.
     * @param row  the column.
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
        TextAnchor anchor = this.itemLabelTextAnchorTable.getAnchor(0, series);
        if (anchor == null) {
            anchor = this.defaultItemLabelTextAnchor;
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
        this.itemLabelTextAnchorTable.setAnchor(0, series, anchor);
    }

    /**
     * Returns the default item label text anchor.
     * 
     * @return The text anchor.
     */
    public TextAnchor getDefaultItemLabelTextAnchor() {
        return this.defaultItemLabelTextAnchor;
    }

    /**
     * Sets the default item label text anchor.
     * 
     * @param anchor  the anchor.
     */
    public void setDefaultItemLabelTextAnchor(TextAnchor anchor) {
        this.defaultItemLabelTextAnchor = anchor;
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
        TextAnchor anchor = this.itemLabelRotationAnchorTable.getAnchor(0, series);
        if (anchor == null) {
            anchor = this.defaultItemLabelRotationAnchor;
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
        this.itemLabelRotationAnchorTable.setAnchor(0, series, anchor);
    }

    /**
     * Returns the default item label rotation anchor point.
     * 
     * @return The anchor point.
     */
    public TextAnchor getDefaultItemLabelRotationAnchor() {
        return this.defaultItemLabelRotationAnchor;
    }

    /**
     * Sets the default item label rotation anchor point.
     * 
     * @param anchor  the anchor point.
     */
    public void setDefaultItemLabelRotationAnchor(TextAnchor anchor) {
        this.defaultItemLabelRotationAnchor = anchor;
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
        Number angle = this.itemLabelAngleTable.getNumber(0, series);
        if (angle == null) {
            angle = this.defaultItemLabelAngle;
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
        this.itemLabelAngleTable.setNumber(0, series, angle);
    }

    /**
     * Returns the default item label angle.
     * 
     * @return The angle.
     */
    public Number getDefaultItemLabelAngle() {
        return this.defaultItemLabelAngle;
    }

    /**
     * Sets the default item label angle.
     * 
     * @param angle  the angle.
     */
    public void setDefaultAngle(Number angle) {
        this.defaultItemLabelAngle = angle;
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

            boolean b0 = true;
            //ObjectUtils.equalOrBothNull(this.plot, renderer.plot);

            boolean b1 = true;
            //ObjectUtils.equalOrBothNull(this.supplier, renderer.supplier);

            boolean b2 = ObjectUtils.equalOrBothNull(this.defaultPaint, renderer.defaultPaint);

            boolean b4 = ObjectUtils.equalOrBothNull(this.paintTable, renderer.paintTable);

            boolean b5 = ObjectUtils.equalOrBothNull(this.defaultOutlinePaint,
                                                     renderer.defaultOutlinePaint);

            boolean b7 = ObjectUtils.equalOrBothNull(this.outlinePaintTable,
                                                     renderer.outlinePaintTable);

            boolean b8 = ObjectUtils.equalOrBothNull(this.defaultStroke, renderer.defaultStroke);

            boolean b10 = ObjectUtils.equalOrBothNull(this.strokeTable, renderer.strokeTable);

            boolean b11 = ObjectUtils.equalOrBothNull(this.defaultOutlineStroke,
                                                      renderer.defaultOutlineStroke);

            boolean b13 = ObjectUtils.equalOrBothNull(this.outlineStrokeTable,
                                                      renderer.outlineStrokeTable);

            boolean b14 = ObjectUtils.equalOrBothNull(this.defaultShape, renderer.defaultShape);

            boolean b16 = ObjectUtils.equalOrBothNull(this.shapeTable, renderer.shapeTable);

            //boolean b17 = ObjectUtils.equalOrBothNull(this.valueLabelFont, renderer.valueLabelFont);
//            boolean b18 = ObjectUtils.equalOrBothNull(
//                this.valueLabelPaint, renderer.valueLabelPaint
//            );

            return b0 && b1 && b2 && b4 && b5 && b7 && b8
                      && b10 && b11 && b13 && b14 && b16;
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
        SerialUtilities.writePaint(this.defaultPaint, stream);
        SerialUtilities.writePaint(this.defaultOutlinePaint, stream);
        SerialUtilities.writeStroke(this.defaultStroke, stream);
        SerialUtilities.writeStroke(this.defaultOutlineStroke, stream);
        SerialUtilities.writeShape(this.defaultShape, stream);
        //SerialUtilities.writePaint(this.valueLabelPaint, stream);

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
        this.defaultPaint = SerialUtilities.readPaint(stream);
        this.defaultOutlinePaint = SerialUtilities.readPaint(stream);
        this.defaultStroke = SerialUtilities.readStroke(stream);
        this.defaultOutlineStroke = SerialUtilities.readStroke(stream);
        this.defaultShape = SerialUtilities.readShape(stream);
        //this.valueLabelPaint = SerialUtilities.readPaint(stream);

    }

}
