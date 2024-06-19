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
 * ---------------------
 * AbstractRenderer.java
 * ---------------------
 * (C) Copyright 2002, 2003 by Simba Management Limited.
 *
 * Original Author:  David Gilbert (for Simba Management Limited);
 * Contributor(s):   -;
 *
 * $Id: AbstractRenderer.java,v 1.1 2007/10/10 19:57:50 vauchers Exp $
 *
 * Changes:
 * --------
 * 22-Aug-2002 : Version 1, draws code out of AbstractXYItemRenderer to share with
 *               AbstractCategoryItemRenderer (DG);
 * 01-Oct-2002 : Fixed errors reported by Checkstyle (DG);
 * 06-Nov-2002 : Moved to the com.jrefinery.chart.renderer package (DG);
 * 21-Nov-2002 : Added a paint table for the renderer to use (DG);
 * 17-Jan-2003 : Moved plot classes into a separate package (DG);
 */

package com.jrefinery.chart.renderer;

import java.awt.BasicStroke;
import java.awt.Paint;
import java.awt.Color;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.beans.PropertyChangeSupport;
import java.beans.PropertyChangeListener;
import com.jrefinery.chart.ChartRenderingInfo;
import com.jrefinery.chart.plot.Plot;

/**
 * Base class providing common services for renderers.
 *
 * @author David Gilbert
 */
public class AbstractRenderer implements Renderer {

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
    
    /** The plot that the renderer is assigned to. */
    private Plot plot;

    /** The drawing supplier. */
    private DrawingSupplier supplier;
    
    /** The default paint. */
    private Paint defaultPaint;
        
    /** A flag that controls whether the paint table is active. */
    private boolean paintTableActive;

    /** The paint table. */
    private PaintTable paintTable;

    /** The default outline paint. */
    private Paint defaultOutlinePaint;
    
    /** A flag that controls whether the outline paint table is active. */
    private boolean outlinePaintTableActive;

    /** The outline paint table. */
    private PaintTable outlinePaintTable;
    
    /** The default stroke. */
    private Stroke defaultStroke;

    /** A flag that controls whether the stroke table is active. */
    private boolean strokeTableActive;
        
    /** The stroke table. */
    private StrokeTable strokeTable;

    /** The default outline stroke. */
    private Stroke defaultOutlineStroke;
    
    /** A flag that controls whether the outline stroke table is active. */
    private boolean outlineStrokeTableActive;
    
    /** The outline stroke table. */
    private StrokeTable outlineStrokeTable;

    /** The default shape. */
    private Shape defaultShape;
    
    /** A flag that controls whether the shape table is active. */
    private boolean shapeTableActive;
        
    /** A shape table. */
    private ShapeTable shapeTable;
    
    /** A temporary reference to chart rendering info (may be <code>null</code>). */
    private ChartRenderingInfo info;

    /** Support class for the property change listener mechanism. */
    private PropertyChangeSupport listeners;

    /**
     * Default constructor.
     */
    public AbstractRenderer() {
        
        this.plot = null;
        this.supplier = new DefaultDrawingSupplier();
        
        this.paintTableActive = true;
        this.paintTable = new PaintTable();
        this.defaultPaint = DEFAULT_PAINT;
        
        this.outlinePaintTableActive = false;
        this.outlinePaintTable = null;
        this.defaultOutlinePaint = DEFAULT_OUTLINE_PAINT;
        
        this.strokeTableActive = false;
        this.strokeTable = null;
        this.defaultStroke = DEFAULT_STROKE;
        
        this.outlineStrokeTableActive = false;
        this.outlineStrokeTable = null;
        this.defaultOutlineStroke = DEFAULT_OUTLINE_STROKE;
        
        this.shapeTableActive = true;
        this.shapeTable = new ShapeTable();
        this.defaultShape = DEFAULT_SHAPE;

        this.info = null;
        this.listeners = new PropertyChangeSupport(this);

    }

    /**
     * Returns the plot that this renderer has been assigned to.
     *
     * @return the plot.
     */
    public Plot getPlot() {
        return this.plot;
    }

    /**
     * Sets the plot that this renderer has been assigned to.
     *
     * @param plot  the plot.
     */
    public void setPlot(Plot plot) {
        this.plot = plot;
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
     * Returns the drawing supplier for the renderer.
     * 
     * @return The drawing supplier.
     */
    public DrawingSupplier getDrawingSupplier() {
        return this.supplier;
    }

    /**
     * Sets the drawing supplier for the renderer.  The drawing supplier is responsible for 
     * supplying a limitless (possibly repeating) sequence of <code>Paint</code>, 
     * <code>Stroke</code> and <code>Shape</code> objects that the renderer can use to populate 
     * its tables.  The supplier can be shared among multiple renderers.
     * 
     * @param supplier  the new supplier.
     */
    public void setDrawingSupplier(DrawingSupplier supplier) {
        this.supplier = supplier;
    }

    /**
     * Returns <code>true</code> if the paint table is being used, and <code>false</code> otherwise.
     * 
     * @return The flag.
     */
    public boolean isPaintTableActive() {
        return this.paintTableActive;
    }
    
    /**
     * Sets the flag that controls whether the paint table is used or not.  If it is not active,
     * the <i>default-paint</i> attribute will be used instead.
     * 
     * @param active  the flag.
     */
    public void setPaintTableActive(boolean active) {
        this.paintTableActive = active;
        if (active) {
            if (this.paintTable == null) {
                this.paintTable = new PaintTable();
            }
        }
    }
    
    /**
     * Returns the paint used to fill data items as they are drawn.
     * <p>
     * The default implementation passes control to the <code>getSeriesPaint</code> method.  
     * You can override this method if you require different behaviour.
     * 
     * @param dataset  the dataset index (zero-based).
     * @param row  the row (or series) index (zero-based).
     * @param column  the column (or category) index (zero-based).
     * 
     * @return  The paint.
     */
    public Paint getItemPaint(int dataset, int row, int column) {
        return getSeriesPaint(dataset, row);
    }
    
    /**
     * Returns the color used to fill an item drawn by the renderer.
     *
     * @param dataset  the dataset index (zero-based).
     * @param series  the series index (zero-based).
     *
     * @return  The paint.
     */
    public Paint getSeriesPaint(int dataset, int series) {

        Paint result = this.defaultPaint;

        if (this.paintTableActive) {
            result = this.paintTable.getPaint(dataset, series);
            if (result == null) {
                result = this.supplier.getNextPaint();
                this.paintTable.setPaint(dataset, series, result);    
            }
        }

        return result;

    }

    /**
     * Sets the paint used for a series (in the primary dataset). 
     * 
     * @param series  the series.
     * @param paint  the paint.
     */
    public void setSeriesPaint(int series, Paint paint) {
        setSeriesPaint(0, series, paint);
    }
    
    /**
     * Sets the paint used for a series.
     * 
     * @param dataset  the dataset index (zero-based).
     * @param series  the series index (zero-based).
     * @param paint  the paint.
     */
    public void setSeriesPaint(int dataset, int series, Paint paint) {
        setPaintTableActive(true);
        this.paintTable.setPaint(dataset, series, paint);
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

    /**
     * Returns <code>true</code> if the outline paint table is being used, and <code>false</code>
     * otherwise.
     * 
     * @return The flag.
     */
    public boolean isOutlinePaintTableActive() {
        return this.outlinePaintTableActive;
    }
    
    /**
     * Sets the flag that controls whether the outline paint table is used or not.  If it is not 
     * active, the <i>default-outline-paint</i> attribute will be used instead.
     * 
     * @param active  the flag.
     */
    public void setOutlinePaintTableActive(boolean active) {
        this.outlinePaintTableActive = active;
        if (active) {
            if (this.outlinePaintTable == null) {
                this.outlinePaintTable = new PaintTable();
            }
        }
    }
    
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
    public Paint getItemOutlinePaint(int dataset, int row, int column) {
        return getSeriesOutlinePaint(dataset, row);
    }
    
    /**
     * Returns the color used to outline an item drawn by the renderer.
     *
     * @param dataset  the dataset (zero-based index).
     * @param series  the series (zero-based index).
     *
     * @return  The paint.
     */
    public Paint getSeriesOutlinePaint(int dataset, int series) {

        Paint result = defaultOutlinePaint;

        if (this.outlinePaintTable != null) {
            result = this.outlinePaintTable.getPaint(dataset, series);
        }

        return result;

    }

    /**
     * Sets the paint used for a series outline (in the primary dataset). 
     * 
     * @param series  the series.
     * @param paint  the paint.
     */
    public void setSeriesOutlinePaint(int series, Paint paint) {
        setSeriesOutlinePaint(0, series, paint);
    }
    
    /**
     * Sets the paint used for a series outline.
     * 
     * @param dataset  the dataset index (zero-based).
     * @param series  the series index (zero-based).
     * @param paint  the paint.
     */
    public void setSeriesOutlinePaint(int dataset, int series, Paint paint) {
        setOutlinePaintTableActive(true);
        this.outlinePaintTable.setPaint(dataset, series, paint);
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

    /**
     * Returns <code>true</code> if the stroke table is being used, and <code>false</code>
     * otherwise.
     * 
     * @return The flag.
     */
    public boolean isStrokeTableActive() {
        return this.strokeTableActive;
    }
    
    /**
     * Sets the flag that controls whether the stroke table is used or not.  If it is not active,
     * the <i>default-stroke</i> attribute will be used instead.
     * 
     * @param active  the flag.
     */
    public void setStrokeTableActive(boolean active) {
        this.strokeTableActive = active;
        if (active) {
            if (this.strokeTable == null) {
                this.strokeTable = new StrokeTable();
            }
        }
    }
    
    /**
     * Returns the stroke used to draw data items.
     * <p>
     * The default implementation passes control to the getSeriesStroke method.  You can override
     * this method if you require different behaviour.
     * 
     * @param dataset  the dataset index (zero-based).
     * @param row  the row (or series) index (zero-based).
     * @param column  the column (or category) index (zero-based).
     * 
     * @return The stroke.
     */
    public Stroke getItemStroke(int dataset, int row, int column) {
        return getSeriesStroke(dataset, row);
    }
    
    /**
     * Returns the stroke used to draw the items in a series.
     *
     * @param dataset  the dataset (zero-based index).
     * @param series  the series (zero-based index).
     *
     * @return  The stroke.
     */
    public Stroke getSeriesStroke(int dataset, int series) {

        Stroke result = this.defaultStroke;

        if (this.strokeTable != null) {
            result = this.strokeTable.getStroke(dataset, series);
        }

        return result;

    }

    /**
     * Sets the stroke used for a series (in the primary dataset). 
     * 
     * @param series  the series.
     * @param stroke  the stroke.
     */
    public void setSeriesStroke(int series, Stroke stroke) {
        setSeriesStroke(0, series, stroke);
    }
    
    /**
     * Sets the stroke used for a series.
     * 
     * @param dataset  the dataset index (zero-based).
     * @param series  the series index (zero-based).
     * @param stroke  the stroke.
     */
    public void setSeriesStroke(int dataset, int series, Stroke stroke) {
        setStrokeTableActive(true);
        this.strokeTable.setStroke(dataset, series, stroke);
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
    
    /**
     * Returns <code>true</code> if the outline stroke table is being used, and <code>false</code>
     * otherwise.
     * 
     * @return The flag.
     */
    public boolean isOutlineStrokeTableActive() {
        return this.outlineStrokeTableActive;
    }
    
    /**
     * Sets the flag that controls whether the outline stroke table is used or not.  If it is not 
     * active, the <i>default-outline-stroke</i> attribute will be used instead.
     * 
     * @param active  the flag.
     */
    public void setOutlineStrokeTableActive(boolean active) {
        this.outlineStrokeTableActive = active;
        if (active) { 
            if (this.outlineStrokeTable == null) {
                this.outlineStrokeTable = new StrokeTable();
            }
        }
    }
    
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
    public Stroke getItemOutlineStroke(int dataset, int row, int column) {
        return getSeriesOutlineStroke(dataset, row);
    }
    
    /**
     * Returns the stroke used to outline the items in a series.
     *
     * @param dataset  the dataset (zero-based index).
     * @param series  the series (zero-based index).
     *
     * @return  The stroke.
     */
    public Stroke getSeriesOutlineStroke(int dataset, int series) {

        Stroke result = this.defaultOutlineStroke;

        if (this.outlineStrokeTable != null) {
            result = this.outlineStrokeTable.getStroke(dataset, series);
        }

        return result;

    }

    /**
     * Sets the outline stroke used for a series (in the primary dataset). 
     * 
     * @param series  the series.
     * @param stroke  the stroke.
     */
    public void setSeriesOutlineStroke(int series, Stroke stroke) {
        setSeriesOutlineStroke(0, series, stroke);
    }
    
    /**
     * Sets the outline stroke used for a series.
     * 
     * @param dataset  the dataset index (zero-based).
     * @param series  the series index (zero-based).
     * @param stroke  the stroke.
     */
    public void setSeriesOutlineStroke(int dataset, int series, Stroke stroke) {
        setOutlineStrokeTableActive(true);
        this.outlineStrokeTable.setStroke(dataset, series, stroke);
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
                    
    /**
     * Returns <code>true</code> if the shape table is being used, and <code>false</code>
     * otherwise.
     * 
     * @return The flag.
     */
    public boolean isShapeTableActive() {
        return this.shapeTableActive;
    }
    
    /**
     * Sets the flag that controls whether the shape table is used or not.  If it is not 
     * active, the <i>default-shape</i> attribute will be used instead.
     * 
     * @param active  the flag.
     */
    public void setShapeTableActive(boolean active) {
        this.shapeTableActive = active;
    }

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
    public Shape getItemShape(int dataset, int row, int column) {
        return getSeriesShape(dataset, row);
    }
    
    /**
     * Returns a shape used to represent the items in a series.
     *
     * @param dataset  the dataset (zero-based index).
     * @param series  the series (zero-based index).
     *
     * @return  The shape.
     */
    public Shape getSeriesShape(int dataset, int series) {

        Shape result = defaultShape;

        if (this.shapeTable != null) {
            result = this.shapeTable.getShape(dataset, series);
        }

        return result;

    }

    /**
     * Sets the shape used for a series (in the primary dataset). 
     * 
     * @param series  the series.
     * @param shape  the shape.
     */
    public void setSeriesShape(int series, Shape shape) {
        setSeriesShape(0, series, shape);
    }
    
    /**
     * Sets the shape used for a series.
     * 
     * @param dataset  the dataset index (zero-based).
     * @param series  the series index (zero-based).
     * @param shape  the shape.
     */
    public void setSeriesShape(int dataset, int series, Shape shape) {
        setShapeTableActive(true);
        this.shapeTable.setShape(dataset, series, shape);
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

}
