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
 * -------------
 * Renderer.java
 * -------------
 * (C) Copyright 2003, by Simba Management Limited and Contributors.
 *
 * Original Author:  David Gilbert (for Simba Management Limited);
 * Contributor(s):   Jonathan Nash;
 *
 * $Id: Renderer.java,v 1.1 2007/10/10 19:57:50 vauchers Exp $
 *
 * Changes
 * -------
 * 25-Jan-2003 : Version 1 (DG);
 *
 */
 
package com.jrefinery.chart.renderer;

import java.awt.Paint;
import java.awt.Shape;
import java.awt.Stroke;

import com.jrefinery.chart.plot.Plot;

/**
 * The base interface for renderers.
 * 
 * @author David Gilbert
 */
public interface Renderer {
    
    /**
     * Returns the plot that the renderer has been assigned to.
     *
     * @return the plot.
     */
    public Plot getPlot();

    /**
     * Sets the plot that the renderer has been assigned to.
     * <P>
     * You shouldn't need to call this method yourself, the plot will do it for you when you
     * assign the renderer to the plot.
     *
     * @param plot  the plot.
     */
    public void setPlot(Plot plot);

    /** 
     * Returns the drawing supplier for the renderer.
     * 
     * @return The drawing supplier.
     */
    public DrawingSupplier getDrawingSupplier();
    
    /**
     * Sets the drawing supplier for the renderer.  The drawing supplier is responsible for 
     * supplying a limitless (possibly repeating) sequence of Paint, Stroke and Shape objects that
     * the renderer can use to populate its tables.  The supplier can be shared among multiple 
     * renderers.
     * 
     * @param supplier  the new supplier.
     */
    public void setDrawingSupplier(DrawingSupplier supplier);
    
    /**
     * Returns the default paint.
     * 
     * @return The default paint.
     */
    public Paint getDefaultPaint();
    
    /**
     * Sets the default paint.
     * <p>
     * In most cases, the renderer's paint table will be active and so this default value will
     * not be used.
     * 
     * @param paint  the paint.
     */
    public void setDefaultPaint(Paint paint);
    
    /**
     * Returns <code>true</code> if the paint table is being used, and <code>false</code> otherwise.
     * 
     * @return The flag.
     */
    public boolean isPaintTableActive();
    
    /**
     * Sets the flag that controls whether the paint table is used or not.  If it is not active,
     * the <i>default-paint</i> attribute will be used instead.
     * 
     * @param flag  the flag.
     */
    public void setPaintTableActive(boolean flag);
    
    /**
     * Sets the paint for a series in the primary dataset.
     * 
     * @param series  the series index (zero-based).
     * @param paint  the paint.
     */
    public void setSeriesPaint(int series, Paint paint);
    
    /**
     * Sets the paint for a series.
     *
     * @param dataset  the dataset index (zero-based).
     * @param series  the series index (zero-based).
     * @param paint  the paint.
     */ 
    public void setSeriesPaint(int dataset, int series, Paint paint);

    /**
     * Returns the default outline paint.
     * 
     * @return The default outline paint.
     */
    public Paint getDefaultOutlinePaint();
    
    /**
     * Sets the default outline paint.
     * 
     * @param paint  the paint.
     */
    public void setDefaultOutlinePaint(Paint paint);
    
    /**
     * Returns <code>true</code> if the outline paint table is being used, and <code>false</code>
     * otherwise.
     * 
     * @return The flag.
     */
    public boolean isOutlinePaintTableActive();
    
    /**
     * Sets the flag that controls whether the outline paint table is used or not.  If it is not 
     * active, the <i>default-outline-paint</i> attribute will be used instead.
     * 
     * @param flag  the flag.
     */
    public void setOutlinePaintTableActive(boolean flag);
    
    /**
     * Returns the default stroke.
     * 
     * @return The default stroke.
     */
    public Stroke getDefaultStroke();
    
    /**
     * Sets the default stroke.
     * 
     * @param stroke  the stroke.
     */
    public void setDefaultStroke(Stroke stroke);
    
    /**
     * Returns <code>true</code> if the stroke table is being used, and <code>false</code>
     * otherwise.
     * 
     * @return The flag.
     */
    public boolean isStrokeTableActive();
    
    /**
     * Sets the flag that controls whether the stroke table is used or not.  If it is not active,
     * the <i>default-stroke</i> attribute will be used instead.
     * 
     * @param flag  the flag.
     */
    public void setStrokeTableActive(boolean flag);
    
    /**
     * Returns the default outline stroke.
     * 
     * @return The default outline stroke.
     */
    public Stroke getDefaultOutlineStroke();
    
    /**
     * Sets the default outline stroke.
     * 
     * @param stroke  the default outline stroke.
     */
    public void setDefaultOutlineStroke(Stroke stroke);
    
    /**
     * Sets the stroke used for a series (in the primary dataset). 
     * 
     * @param series  the series.
     * @param stroke  the stroke.
     */
    public void setSeriesStroke(int series, Stroke stroke);
    
    /**
     * Sets the stroke used for a series.
     * 
     * @param dataset  the dataset index (zero-based).
     * @param series  the series index (zero-based).
     * @param stroke  the stroke.
     */
    public void setSeriesStroke(int dataset, int series, Stroke stroke);

    /**
     * Returns <code>true</code> if the outline stroke table is being used, and <code>false</code>
     * otherwise.
     * 
     * @return The flag.
     */
    public boolean isOutlineStrokeTableActive();
    
    /**
     * Sets the flag that controls whether the outline stroke table is used or not.  If it is not 
     * active, the <i>default-outline-stroke</i> attribute will be used instead.
     * 
     * @param flag  the flag.
     */
    public void setOutlineStrokeTableActive(boolean flag);
    
    /**
     * Returns the default shape.
     * 
     * @return The default shape.
     */
    public Shape getDefaultShape();
    
    /**
     * Sets the default shape.
     * 
     * @param shape  the shape.
     */
    public void setDefaultShape(Shape shape);
    
    /**
     * Returns <code>true</code> if the shape table is being used, and <code>false</code>
     * otherwise.
     * 
     * @return The flag.
     */
    public boolean isShapeTableActive();
    
    /**
     * Sets the flag that controls whether the shape table is used or not.  If it is not 
     * active, the <i>default-shape</i> attribute will be used instead.
     * 
     * @param flag  the flag.
     */
    public void setShapeTableActive(boolean flag);
    
}
