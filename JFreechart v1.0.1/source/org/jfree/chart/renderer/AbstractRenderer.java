/* ===========================================================
 * JFreeChart : a free chart library for the Java(tm) platform
 * ===========================================================
 *
 * (C) Copyright 2000-2005, by Object Refinery Limited and Contributors.
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
 * AbstractRenderer.java
 * ---------------------
 * (C) Copyright 2002-2005, by Object Refinery Limited.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited);
 * Contributor(s):   Nicolas Brodu;
 *
 * $Id: AbstractRenderer.java,v 1.1 2007/10/10 20:17:20 vauchers Exp $
 *
 * Changes:
 * --------
 * 22-Aug-2002 : Version 1, draws code out of AbstractXYItemRenderer to share 
 *               with AbstractCategoryItemRenderer (DG);
 * 01-Oct-2002 : Fixed errors reported by Checkstyle (DG);
 * 06-Nov-2002 : Moved to the com.jrefinery.chart.renderer package (DG);
 * 21-Nov-2002 : Added a paint table for the renderer to use (DG);
 * 17-Jan-2003 : Moved plot classes into a separate package (DG);
 * 25-Mar-2003 : Implemented Serializable (DG);
 * 29-Apr-2003 : Added valueLabelFont and valueLabelPaint attributes, based on 
 *               code from Arnaud Lelievre (DG);
 * 29-Jul-2003 : Amended code that doesn't compile with JDK 1.2.2 (DG);
 * 13-Aug-2003 : Implemented Cloneable (DG);
 * 15-Sep-2003 : Fixed serialization (NB);
 * 17-Sep-2003 : Changed ChartRenderingInfo --> PlotRenderingInfo (DG);
 * 07-Oct-2003 : Moved PlotRenderingInfo into RendererState to allow for 
 *               multiple threads using a single renderer (DG);
 * 20-Oct-2003 : Added missing setOutlinePaint() method (DG);
 * 23-Oct-2003 : Split item label attributes into 'positive' and 'negative' 
 *               values (DG);
 * 26-Nov-2003 : Added methods to get the positive and negative item label 
 *               positions (DG);
 * 01-Mar-2004 : Modified readObject() method to prevent null pointer exceptions
 *               after deserialization (DG);
 * 19-Jul-2004 : Fixed bug in getItemLabelFont(int, int) method (DG);
 * 04-Oct-2004 : Updated equals() method, eliminated use of NumberUtils,
 *               renamed BooleanUtils --> BooleanUtilities, ShapeUtils -->
 *               ShapeUtilities (DG);
 * 15-Mar-2005 : Fixed serialization of baseFillPaint (DG);
 * 16-May-2005 : Base outline stroke should never be null (DG);
 * 01-Jun-2005 : Added hasListener() method for unit testing (DG);
 * 08-Jun-2005 : Fixed equals() method to handle GradientPaint (DG);
 * 
 */

package org.jfree.chart.renderer;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Arrays;
import java.util.EventListener;
import java.util.List;

import javax.swing.event.EventListenerList;

import org.jfree.chart.event.RendererChangeEvent;
import org.jfree.chart.event.RendererChangeListener;
import org.jfree.chart.labels.ItemLabelAnchor;
import org.jfree.chart.labels.ItemLabelPosition;
import org.jfree.chart.plot.DrawingSupplier;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.io.SerialUtilities;
import org.jfree.ui.TextAnchor;
import org.jfree.util.BooleanList;
import org.jfree.util.BooleanUtilities;
import org.jfree.util.ObjectList;
import org.jfree.util.ObjectUtilities;
import org.jfree.util.PaintList;
import org.jfree.util.PaintUtilities;
import org.jfree.util.ShapeList;
import org.jfree.util.ShapeUtilities;
import org.jfree.util.StrokeList;

/**
 * Base class providing common services for renderers.  Most methods that update
 * attributes of the renderer will fire a {@link RendererChangeEvent}, which 
 * normally means the plot that owns the renderer will receive notification that
 * the renderer has been changed (the plot will, in turn, notify the chart).
 */
public abstract class AbstractRenderer implements Cloneable, Serializable {

    /** For serialization. */
    private static final long serialVersionUID = -828267569428206075L;
    
    /** A useful constant. */
    public static final Double ZERO = new Double(0.0);
    
    /** The default paint. */
    public static final Paint DEFAULT_PAINT = Color.blue;

    /** The default outline paint. */
    public static final Paint DEFAULT_OUTLINE_PAINT = Color.gray;

    /** The default stroke. */
    public static final Stroke DEFAULT_STROKE = new BasicStroke(1.0f);

    /** The default outline stroke. */
    public static final Stroke DEFAULT_OUTLINE_STROKE = new BasicStroke(1.0f);

    /** The default shape. */
    public static final Shape DEFAULT_SHAPE 
        = new Rectangle2D.Double(-3.0, -3.0, 6.0, 6.0);

    /** The default value label font. */
    public static final Font DEFAULT_VALUE_LABEL_FONT 
        = new Font("SansSerif", Font.PLAIN, 10);

    /** The default value label paint. */
    public static final Paint DEFAULT_VALUE_LABEL_PAINT = Color.black;

    /** A flag that controls the visibility of ALL series. */
    private Boolean seriesVisible;
    
    /** A list of flags that controls whether or not each series is visible. */
    private BooleanList seriesVisibleList;

    /** The default visibility for each series. */
    private boolean baseSeriesVisible;
    
    /** A flag that controls the visibility of ALL series in the legend. */
    private Boolean seriesVisibleInLegend;
    
    /** 
     * A list of flags that controls whether or not each series is visible in 
     * the legend. 
     */
    private BooleanList seriesVisibleInLegendList;

    /** The default visibility for each series in the legend. */
    private boolean baseSeriesVisibleInLegend;
        
    /** The paint for ALL series (optional). */
    private transient Paint paint;

    /** The paint list. */
    private PaintList paintList;

    /** The base paint. */
    private transient Paint basePaint;

    /** The fill paint for ALL series (optional). */
    private transient Paint fillPaint;

    /** The fill paint list. */
    private PaintList fillPaintList;

    /** The base fill paint. */
    private transient Paint baseFillPaint;

    /** The outline paint for ALL series (optional). */
    private transient Paint outlinePaint;

    /** The outline paint list. */
    private PaintList outlinePaintList;

    /** The base outline paint. */
    private transient Paint baseOutlinePaint;

    /** The stroke for ALL series (optional). */
    private transient Stroke stroke;

    /** The stroke list. */
    private StrokeList strokeList;

    /** The base stroke. */
    private transient Stroke baseStroke;

    /** The outline stroke for ALL series (optional). */
    private transient Stroke outlineStroke;

    /** The outline stroke list. */
    private StrokeList outlineStrokeList;

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

    /** The positive item label position for ALL series (optional). */
    private ItemLabelPosition positiveItemLabelPosition;
    
    /** The positive item label position (per series). */
    private ObjectList positiveItemLabelPositionList;
    
    /** The fallback positive item label position. */
    private ItemLabelPosition basePositiveItemLabelPosition;
    
    /** The negative item label position for ALL series (optional). */
    private ItemLabelPosition negativeItemLabelPosition;
    
    /** The negative item label position (per series). */
    private ObjectList negativeItemLabelPositionList;
    
    /** The fallback negative item label position. */
    private ItemLabelPosition baseNegativeItemLabelPosition;

    /** The item label anchor offset. */
    private double itemLabelAnchorOffset = 2.0;

    /** 
     * A flag that controls whether or not entities are generated for 
     * ALL series (optional). 
     */
    private Boolean createEntities;

    /** 
     * Flags that control whether or not entities are generated for each 
     * series.  This will be overridden by 'createEntities'. 
     */
    private BooleanList createEntitiesList;

    /**
     * The default flag that controls whether or not entities are generated.
     * This flag is used when both the above flags return null. 
     */
    private boolean baseCreateEntities;
    
    /** Storage for registered change listeners. */
    private transient EventListenerList listenerList;

    /**
     * Default constructor.
     */
    public AbstractRenderer() {

        this.seriesVisible = null;
        this.seriesVisibleList = new BooleanList();
        this.baseSeriesVisible = true;
        
        this.seriesVisibleInLegend = null;
        this.seriesVisibleInLegendList = new BooleanList();
        this.baseSeriesVisibleInLegend = true;

        this.paint = null;
        this.paintList = new PaintList();
        this.basePaint = DEFAULT_PAINT;

        this.fillPaint = null;
        this.fillPaintList = new PaintList();
        this.baseFillPaint = Color.white;

        this.outlinePaint = null;
        this.outlinePaintList = new PaintList();
        this.baseOutlinePaint = DEFAULT_OUTLINE_PAINT;

        this.stroke = null;
        this.strokeList = new StrokeList();
        this.baseStroke = DEFAULT_STROKE;

        this.outlineStroke = null;
        this.outlineStrokeList = new StrokeList();
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

        this.positiveItemLabelPosition = null;
        this.positiveItemLabelPositionList = new ObjectList();
        this.basePositiveItemLabelPosition = new ItemLabelPosition(
            ItemLabelAnchor.OUTSIDE12, TextAnchor.BOTTOM_CENTER
        );
        
        this.negativeItemLabelPosition = null;
        this.negativeItemLabelPositionList = new ObjectList();
        this.baseNegativeItemLabelPosition = new ItemLabelPosition(
            ItemLabelAnchor.OUTSIDE6, TextAnchor.TOP_CENTER
        );

        this.createEntities = null;
        this.createEntitiesList = new BooleanList();
        this.baseCreateEntities = true;
        
        this.listenerList = new EventListenerList();

    }

    /**
     * Returns the drawing supplier from the plot.
     * 
     * @return The drawing supplier.
     */
    public abstract DrawingSupplier getDrawingSupplier();
    
    // SERIES VISIBLE (not yet respected by all renderers)

    /**
     * Returns a boolean that indicates whether or not the specified item 
     * should be drawn (this is typically used to hide an entire series).
     * 
     * @param series  the series index.
     * @param item  the item index.
     * 
     * @return A boolean.
     */
    public boolean getItemVisible(int series, int item) {
        return isSeriesVisible(series);
    }
    
    /**
     * Returns a boolean that indicates whether or not the specified series 
     * should be drawn.
     * 
     * @param series  the series index.
     * 
     * @return A boolean.
     */
    public boolean isSeriesVisible(int series) {
        boolean result = this.baseSeriesVisible;
        if (this.seriesVisible != null) {
            result = this.seriesVisible.booleanValue();   
        }
        else {
            Boolean b = this.seriesVisibleList.getBoolean(series);
            if (b != null) {
                result = b.booleanValue();   
            }
        }
        return result;
    }
    
    /**
     * Returns the flag that controls the visibility of ALL series.  This flag 
     * overrides the per series and default settings - you must set it to 
     * <code>null</code> if you want the other settings to apply.
     * 
     * @return The flag (possibly <code>null</code>).
     */
    public Boolean getSeriesVisible() {
        return this.seriesVisible;   
    }
    
    /**
     * Sets the flag that controls the visibility of ALL series and sends a 
     * {@link RendererChangeEvent} to all registered listeners.  This flag 
     * overrides the per series and default settings - you must set it to 
     * <code>null</code> if you want the other settings to apply.
     * 
     * @param visible  the flag (<code>null</code> permitted).
     */
    public void setSeriesVisible(Boolean visible) {
         setSeriesVisible(visible, true);
    }
    
    /**
     * Sets the flag that controls the visibility of ALL series and sends a 
     * {@link RendererChangeEvent} to all registered listeners.  This flag 
     * overrides the per series and default settings - you must set it to 
     * <code>null</code> if you want the other settings to apply.
     * 
     * @param visible  the flag (<code>null</code> permitted).
     * @param notify  notify listeners?
     */
    public void setSeriesVisible(Boolean visible, boolean notify) {
        this.seriesVisible = visible;   
        if (notify) {
            notifyListeners(new RendererChangeEvent(this));
        }
    }
    
    /**
     * Returns the flag that controls whether a series is visible.
     *
     * @param series  the series index (zero-based).
     *
     * @return The flag (possibly <code>null</code>).
     */
    public Boolean getSeriesVisible(int series) {
        return this.seriesVisibleList.getBoolean(series);
    }
    
    /**
     * Sets the flag that controls whether a series is visible and sends a 
     * {@link RendererChangeEvent} to all registered listeners.
     *
     * @param series  the series index (zero-based).
     * @param visible  the flag (<code>null</code> permitted).
     */
    public void setSeriesVisible(int series, Boolean visible) {
        setSeriesVisible(series, visible, true);
    }
    
    /**
     * Sets the flag that controls whether a series is visible and, if 
     * requested, sends a {@link RendererChangeEvent} to all registered 
     * listeners.
     * 
     * @param series  the series index.
     * @param visible  the flag (<code>null</code> permitted).
     * @param notify  notify listeners?
     */
    public void setSeriesVisible(int series, Boolean visible, boolean notify) {
        this.seriesVisibleList.setBoolean(series, visible);       
        if (notify) {
            notifyListeners(new RendererChangeEvent(this));
        }
    }

    /**
     * Returns the base visibility for all series.
     *
     * @return The base visibility.
     */
    public boolean getBaseSeriesVisible() {
        return this.baseSeriesVisible;
    }

    /**
     * Sets the base visibility and sends a {@link RendererChangeEvent} 
     * to all registered listeners.
     *
     * @param visible  the flag.
     */
    public void setBaseSeriesVisible(boolean visible) {
        // defer argument checking...
        setBaseSeriesVisible(visible, true);
    }
    
    /**
     * Sets the base visibility and, if requested, sends 
     * a {@link RendererChangeEvent} to all registered listeners.
     * 
     * @param visible  the visibility.
     * @param notify  notify listeners?
     */
    public void setBaseSeriesVisible(boolean visible, boolean notify) {
        this.baseSeriesVisible = visible;
        if (notify) {
            notifyListeners(new RendererChangeEvent(this));
        }
    }

    // SERIES VISIBLE IN LEGEND (not yet respected by all renderers)
    
    /**
     * Returns <code>true</code> if the series should be shown in the legend,
     * and <code>false</code> otherwise.
     * 
     * @param series  the series index.
     * 
     * @return A boolean.
     */
    public boolean isSeriesVisibleInLegend(int series) {
        boolean result = this.baseSeriesVisibleInLegend;
        if (this.seriesVisibleInLegend != null) {
            result = this.seriesVisibleInLegend.booleanValue();   
        }
        else {
            Boolean b = this.seriesVisibleInLegendList.getBoolean(series);
            if (b != null) {
                result = b.booleanValue();   
            }
        }
        return result;
    }
    
    /**
     * Returns the flag that controls the visibility of ALL series in the 
     * legend.  This flag overrides the per series and default settings - you 
     * must set it to <code>null</code> if you want the other settings to 
     * apply.
     * 
     * @return The flag (possibly <code>null</code>).
     */
    public Boolean getSeriesVisibleInLegend() {
        return this.seriesVisibleInLegend;   
    }
    
    /**
     * Sets the flag that controls the visibility of ALL series in the legend 
     * and sends a {@link RendererChangeEvent} to all registered listeners.  
     * This flag overrides the per series and default settings - you must set 
     * it to <code>null</code> if you want the other settings to apply.
     * 
     * @param visible  the flag (<code>null</code> permitted).
     */
    public void setSeriesVisibleInLegend(Boolean visible) {
         setSeriesVisibleInLegend(visible, true);
    }
    
    /**
     * Sets the flag that controls the visibility of ALL series in the legend 
     * and sends a {@link RendererChangeEvent} to all registered listeners.  
     * This flag overrides the per series and default settings - you must set 
     * it to <code>null</code> if you want the other settings to apply.
     * 
     * @param visible  the flag (<code>null</code> permitted).
     * @param notify  notify listeners?
     */
    public void setSeriesVisibleInLegend(Boolean visible, boolean notify) {
        this.seriesVisibleInLegend = visible;   
        if (notify) {
            notifyListeners(new RendererChangeEvent(this));
        }
    }
    
    /**
     * Returns the flag that controls whether a series is visible in the 
     * legend.  This method returns only the "per series" settings - to 
     * incorporate the override and base settings as well, you need to use the 
     * {@link #isSeriesVisibleInLegend(int)} method.
     *
     * @param series  the series index (zero-based).
     *
     * @return The flag (possibly <code>null</code>).
     */
    public Boolean getSeriesVisibleInLegend(int series) {
        return this.seriesVisibleInLegendList.getBoolean(series);
    }
    
    /**
     * Sets the flag that controls whether a series is visible in the legend 
     * and sends a {@link RendererChangeEvent} to all registered listeners.
     *
     * @param series  the series index (zero-based).
     * @param visible  the flag (<code>null</code> permitted).
     */
    public void setSeriesVisibleInLegend(int series, Boolean visible) {
        setSeriesVisibleInLegend(series, visible, true);
    }
    
    /**
     * Sets the flag that controls whether a series is visible in the legend
     * and, if requested, sends a {@link RendererChangeEvent} to all registered 
     * listeners.
     * 
     * @param series  the series index.
     * @param visible  the flag (<code>null</code> permitted).
     * @param notify  notify listeners?
     */
    public void setSeriesVisibleInLegend(int series, Boolean visible, 
                                         boolean notify) {
        this.seriesVisibleInLegendList.setBoolean(series, visible);       
        if (notify) {
            notifyListeners(new RendererChangeEvent(this));
        }
    }

    /**
     * Returns the base visibility in the legend for all series.
     *
     * @return The base visibility.
     */
    public boolean getBaseSeriesVisibleInLegend() {
        return this.baseSeriesVisibleInLegend;
    }

    /**
     * Sets the base visibility in the legend and sends a 
     * {@link RendererChangeEvent} to all registered listeners.
     *
     * @param visible  the flag.
     */
    public void setBaseSeriesVisibleInLegend(boolean visible) {
        // defer argument checking...
        setBaseSeriesVisibleInLegend(visible, true);
    }
    
    /**
     * Sets the base visibility in the legend and, if requested, sends 
     * a {@link RendererChangeEvent} to all registered listeners.
     * 
     * @param visible  the visibility.
     * @param notify  notify listeners?
     */
    public void setBaseSeriesVisibleInLegend(boolean visible, boolean notify) {
        this.baseSeriesVisibleInLegend = visible;
        if (notify) {
            notifyListeners(new RendererChangeEvent(this));
        }
    }

    // PAINT
    
    /**
     * Returns the paint used to fill data items as they are drawn.
     * <p>
     * The default implementation passes control to the 
     * <code>getSeriesPaint</code> method. You can override this method if you 
     * require different behaviour.
     *
     * @param row  the row (or series) index (zero-based).
     * @param column  the column (or category) index (zero-based).
     *
     * @return The paint (never <code>null</code>).
     */
    public Paint getItemPaint(int row, int column) {
        return getSeriesPaint(row);
    }

    /**
     * Returns the paint used to fill an item drawn by the renderer.
     *
     * @param series  the series index (zero-based).
     *
     * @return The paint (never <code>null</code>).
     */
    public Paint getSeriesPaint(int series) {

        // return the override, if there is one...
        if (this.paint != null) {
            return this.paint;
        }

        // otherwise look up the paint list
        Paint seriesPaint = this.paintList.getPaint(series);
        if (seriesPaint == null) {
            DrawingSupplier supplier = getDrawingSupplier();
            if (supplier != null) {
                seriesPaint = supplier.getNextPaint();
                this.paintList.setPaint(series, seriesPaint);
            }
            else {
                seriesPaint = this.basePaint;
            }
        }
        return seriesPaint;

    }

    /**
     * Sets the paint to be used for ALL series, and sends a 
     * {@link RendererChangeEvent} to all registered listeners.  If this is 
     * <code>null</code>, the renderer will use the paint for the series.
     * 
     * @param paint  the paint (<code>null</code> permitted).
     */
    public void setPaint(Paint paint) {
        setPaint(paint, true);
    }
    
    /**
     * Sets the paint to be used for all series and, if requested, sends a 
     * {@link RendererChangeEvent} to all registered listeners.
     * 
     * @param paint  the paint (<code>null</code> permitted).
     * @param notify  notify listeners?
     */
    public void setPaint(Paint paint, boolean notify) {
        this.paint = paint;
        if (notify) {
            notifyListeners(new RendererChangeEvent(this));
        }
    }
    
    /**
     * Sets the paint used for a series and sends a {@link RendererChangeEvent}
     * to all registered listeners.
     *
     * @param series  the series index (zero-based).
     * @param paint  the paint (<code>null</code> permitted).
     */
    public void setSeriesPaint(int series, Paint paint) {
        setSeriesPaint(series, paint, true);
    }
    
    /**
     * Sets the paint used for a series and, if requested, sends a 
     * {@link RendererChangeEvent} to all registered listeners.
     * 
     * @param series  the series index.
     * @param paint  the paint (<code>null</code> permitted).
     * @param notify  notify listeners?
     */
    public void setSeriesPaint(int series, Paint paint, boolean notify) {
        this.paintList.setPaint(series, paint);       
        if (notify) {
            notifyListeners(new RendererChangeEvent(this));
        }
    }

    /**
     * Returns the base paint.
     *
     * @return The base paint (never <code>null</code>).
     */
    public Paint getBasePaint() {
        return this.basePaint;
    }

    /**
     * Sets the base paint and sends a {@link RendererChangeEvent} to all 
     * registered listeners.
     *
     * @param paint  the paint (<code>null</code> not permitted).
     */
    public void setBasePaint(Paint paint) {
        // defer argument checking...
        setBasePaint(paint, true);
    }
    
    /**
     * Sets the base paint and, if requested, sends a 
     * {@link RendererChangeEvent} to all registered listeners.
     * 
     * @param paint  the paint (<code>null</code> not permitted).
     * @param notify  notify listeners?
     */
    public void setBasePaint(Paint paint, boolean notify) {
        this.basePaint = paint;
        if (notify) {
            notifyListeners(new RendererChangeEvent(this));
        }
    }

    //// FILL PAINT //////////////////////////////////////////////////////////
    
    /**
     * Returns the paint used to fill data items as they are drawn.  The 
     * default implementation passes control to the 
     * {@link #getSeriesFillPaint(int)} method - you can override this method 
     * if you require different behaviour.
     *
     * @param row  the row (or series) index (zero-based).
     * @param column  the column (or category) index (zero-based).
     *
     * @return The paint (never <code>null</code>).
     */
    public Paint getItemFillPaint(int row, int column) {
        return getSeriesFillPaint(row);
    }

    /**
     * Returns the paint used to fill an item drawn by the renderer.
     *
     * @param series  the series (zero-based index).
     *
     * @return The paint (never <code>null</code>).
     */
    public Paint getSeriesFillPaint(int series) {

        // return the override, if there is one...
        if (this.fillPaint != null) {
            return this.fillPaint;
        }

        // otherwise look up the paint table
        Paint seriesFillPaint = this.fillPaintList.getPaint(series);
        if (seriesFillPaint == null) {
            seriesFillPaint = this.baseFillPaint;
        }
        return seriesFillPaint;

    }

    /**
     * Sets the paint used for a series fill and sends a 
     * {@link RendererChangeEvent} to all registered listeners.
     *
     * @param series  the series index (zero-based).
     * @param paint  the paint (<code>null</code> permitted).
     */
    public void setSeriesFillPaint(int series, Paint paint) {
        setSeriesFillPaint(series, paint, true);
    }

    /**
     * Sets the paint used to fill a series and, if requested, 
     * sends a {@link RendererChangeEvent} to all registered listeners.
     * 
     * @param series  the series index (zero-based).
     * @param paint  the paint (<code>null</code> permitted).
     * @param notify  notify listeners?
     */    
    public void setSeriesFillPaint(int series, Paint paint, boolean notify) {
        this.fillPaintList.setPaint(series, paint);
        if (notify) {
            notifyListeners(new RendererChangeEvent(this));
        }
    }

    /**
     * Sets the fill paint for ALL series (optional).
     * 
     * @param paint  the paint (<code>null</code> permitted).
     */
    public void setFillPaint(Paint paint) {
        setFillPaint(paint, true);
    }

    /**
     * Sets the fill paint for ALL series and, if requested, sends a 
     * {@link RendererChangeEvent} to all registered listeners.
     * 
     * @param paint  the paint (<code>null</code> permitted).
     * @param notify  notify listeners?
     */
    public void setFillPaint(Paint paint, boolean notify) {
        this.fillPaint = paint;
        if (notify) {
            notifyListeners(new RendererChangeEvent(this));
        }
    }
    
    /**
     * Returns the base fill paint.
     *
     * @return The paint (never <code>null</code>).
     */
    public Paint getBaseFillPaint() {
        return this.baseFillPaint;
    }

    /**
     * Sets the base fill paint and sends a {@link RendererChangeEvent} to 
     * all registered listeners.
     *
     * @param paint  the paint (<code>null</code> not permitted).
     */
    public void setBaseFillPaint(Paint paint) {
        // defer argument checking...
        setBaseFillPaint(paint, true);
    }
    
    /**
     * Sets the base fill paint and, if requested, sends a 
     * {@link RendererChangeEvent} to all registered listeners.
     * 
     * @param paint  the paint (<code>null</code> not permitted).
     * @param notify  notify listeners?
     */
    public void setBaseFillPaint(Paint paint, boolean notify) {
        if (paint == null) {
            throw new IllegalArgumentException("Null 'paint' argument.");   
        }
        this.baseFillPaint = paint;
        if (notify) {
            notifyListeners(new RendererChangeEvent(this));
        }
    }

    // OUTLINE PAINT //////////////////////////////////////////////////////////
    
    /**
     * Returns the paint used to outline data items as they are drawn.
     * <p>
     * The default implementation passes control to the getSeriesOutlinePaint 
     * method.  You can override this method if you require different behaviour.
     *
     * @param row  the row (or series) index (zero-based).
     * @param column  the column (or category) index (zero-based).
     *
     * @return The paint (never <code>null</code>).
     */
    public Paint getItemOutlinePaint(int row, int column) {
        return getSeriesOutlinePaint(row);
    }

    /**
     * Returns the paint used to outline an item drawn by the renderer.
     *
     * @param series  the series (zero-based index).
     *
     * @return The paint (never <code>null</code>).
     */
    public Paint getSeriesOutlinePaint(int series) {

        // return the override, if there is one...
        if (this.outlinePaint != null) {
            return this.outlinePaint;
        }

        // otherwise look up the paint table
        Paint seriesOutlinePaint = this.outlinePaintList.getPaint(series);
        if (seriesOutlinePaint == null) {
            DrawingSupplier supplier = getDrawingSupplier();
            if (supplier != null) {
                seriesOutlinePaint = supplier.getNextOutlinePaint();
                this.outlinePaintList.setPaint(series, seriesOutlinePaint);
            }
            else {
                seriesOutlinePaint = this.baseOutlinePaint;
            }
        }
        return seriesOutlinePaint;

    }

    /**
     * Sets the paint used for a series outline and sends a 
     * {@link RendererChangeEvent} to all registered listeners.
     *
     * @param series  the series index (zero-based).
     * @param paint  the paint (<code>null</code> permitted).
     */
    public void setSeriesOutlinePaint(int series, Paint paint) {
        setSeriesOutlinePaint(series, paint, true);
    }

    /**
     * Sets the paint used to draw the outline for a series and, if requested, 
     * sends a {@link RendererChangeEvent} to all registered listeners.
     * 
     * @param series  the series index (zero-based).
     * @param paint  the paint (<code>null</code> permitted).
     * @param notify  notify listeners?
     */    
    public void setSeriesOutlinePaint(int series, Paint paint, boolean notify) {
        this.outlinePaintList.setPaint(series, paint);
        if (notify) {
            notifyListeners(new RendererChangeEvent(this));
        }
    }

    /**
     * Sets the outline paint for ALL series (optional).
     * 
     * @param paint  the paint (<code>null</code> permitted).
     */
    public void setOutlinePaint(Paint paint) {
        setOutlinePaint(paint, true);
    }

    /**
     * Sets the outline paint for ALL series and, if requested, sends a 
     * {@link RendererChangeEvent} to all registered listeners.
     * 
     * @param paint  the paint (<code>null</code> permitted).
     * @param notify  notify listeners?
     */
    public void setOutlinePaint(Paint paint, boolean notify) {
        this.outlinePaint = paint;
        if (notify) {
            notifyListeners(new RendererChangeEvent(this));
        }
    }
    
    /**
     * Returns the base outline paint.
     *
     * @return The paint (never <code>null</code>).
     */
    public Paint getBaseOutlinePaint() {
        return this.baseOutlinePaint;
    }

    /**
     * Sets the base outline paint and sends a {@link RendererChangeEvent} to 
     * all registered listeners.
     *
     * @param paint  the paint (<code>null</code> not permitted).
     */
    public void setBaseOutlinePaint(Paint paint) {
        // defer argument checking...
        setBaseOutlinePaint(paint, true);
    }
    
    /**
     * Sets the base outline paint and, if requested, sends a 
     * {@link RendererChangeEvent} to all registered listeners.
     * 
     * @param paint  the paint (<code>null</code> not permitted).
     * @param notify  notify listeners?
     */
    public void setBaseOutlinePaint(Paint paint, boolean notify) {
        if (paint == null) {
            throw new IllegalArgumentException("Null 'paint' argument.");   
        }
        this.baseOutlinePaint = paint;
        if (notify) {
            notifyListeners(new RendererChangeEvent(this));
        }
    }

    // STROKE
    
    /**
     * Returns the stroke used to draw data items.
     * <p>
     * The default implementation passes control to the getSeriesStroke method.
     * You can override this method if you require different behaviour.
     *
     * @param row  the row (or series) index (zero-based).
     * @param column  the column (or category) index (zero-based).
     *
     * @return The stroke (never <code>null</code>).
     */
    public Stroke getItemStroke(int row, int column) {
        return getSeriesStroke(row);
    }

    /**
     * Returns the stroke used to draw the items in a series.
     *
     * @param series  the series (zero-based index).
     *
     * @return The stroke (never <code>null</code>).
     */
    public Stroke getSeriesStroke(int series) {

        // return the override, if there is one...
        if (this.stroke != null) {
            return this.stroke;
        }

        // otherwise look up the paint table
        Stroke result = this.strokeList.getStroke(series);
        if (result == null) {
            DrawingSupplier supplier = getDrawingSupplier();
            if (supplier != null) {
                result = supplier.getNextStroke();
                this.strokeList.setStroke(series, result);
            }
            else {
                result = this.baseStroke;
            }
        }
        return result;

    }
    
    /**
     * Sets the stroke for ALL series and sends a {@link RendererChangeEvent} 
     * to all registered listeners.
     * 
     * @param stroke  the stroke (<code>null</code> permitted).
     */
    public void setStroke(Stroke stroke) {
        setStroke(stroke, true);
    }
    
    /**
     * Sets the stroke for ALL series and, if requested, sends a 
     * {@link RendererChangeEvent} to all registered listeners.
     * 
     * @param stroke  the stroke (<code>null</code> permitted).
     * @param notify  notify listeners?
     */
    public void setStroke(Stroke stroke, boolean notify) {
        this.stroke = stroke;
        if (notify) {
            notifyListeners(new RendererChangeEvent(this));
        }
    }    

    /**
     * Sets the stroke used for a series and sends a {@link RendererChangeEvent}
     * to all registered listeners.
     *
     * @param series  the series index (zero-based).
     * @param stroke  the stroke (<code>null</code> permitted).
     */
    public void setSeriesStroke(int series, Stroke stroke) {
        setSeriesStroke(series, stroke, true);
    }
    
    /**
     * Sets the stroke for a series and, if requested, sends a 
     * {@link RendererChangeEvent} to all registered listeners.
     * 
     * @param series  the series index (zero-based).
     * @param stroke  the stroke (<code>null</code> permitted).
     * @param notify  notify listeners?
     */
    public void setSeriesStroke(int series, Stroke stroke, boolean notify) {
        this.strokeList.setStroke(series, stroke);
        if (notify) {
            notifyListeners(new RendererChangeEvent(this));
        }
    }    

    /**
     * Returns the base stroke.
     *
     * @return The base stroke (never <code>null</code>).
     */
    public Stroke getBaseStroke() {
        return this.baseStroke;
    }

    /**
     * Sets the base stroke.
     *
     * @param stroke  the stroke (<code>null</code> not permitted).
     */
    public void setBaseStroke(Stroke stroke) {
        // defer argument checking...
        setBaseStroke(stroke, true);
    }

    /**
     * Sets the base stroke and, if requested, sends a 
     * {@link RendererChangeEvent} to all registered listeners.
     * 
     * @param stroke  the stroke (<code>null</code> not permitted).
     * @param notify  notify listeners?
     */
    public void setBaseStroke(Stroke stroke, boolean notify) {
        if (stroke == null) {
            throw new IllegalArgumentException("Null 'stroke' argument.");   
        }
        this.baseStroke = stroke;
        if (notify) {
            notifyListeners(new RendererChangeEvent(this));
        }
    }    

    // OUTLINE STROKE 
    
    /**
     * Returns the stroke used to outline data items.  The default 
     * implementation passes control to the {@link #getSeriesOutlineStroke(int)}
     * method. You can override this method if you require different behaviour.
     *
     * @param row  the row (or series) index (zero-based).
     * @param column  the column (or category) index (zero-based).
     *
     * @return The stroke (never <code>null</code>).
     */
    public Stroke getItemOutlineStroke(int row, int column) {
        return getSeriesOutlineStroke(row);
    }

    /**
     * Returns the stroke used to outline the items in a series.
     *
     * @param series  the series (zero-based index).
     *
     * @return The stroke (never <code>null</code>).
     */
    public Stroke getSeriesOutlineStroke(int series) {

        // return the override, if there is one...
        if (this.outlineStroke != null) {
            return this.outlineStroke;
        }

        // otherwise look up the stroke table
        Stroke result = this.outlineStrokeList.getStroke(series);
        if (result == null) {
            DrawingSupplier supplier = getDrawingSupplier();
            if (supplier != null) {
                result = supplier.getNextOutlineStroke();
                this.outlineStrokeList.setStroke(series, result);
            }
            else {
                result = this.baseOutlineStroke;
            }
        }
        return result;

    }

    /**
     * Sets the outline stroke for ALL series and sends a 
     * {@link RendererChangeEvent} to all registered listeners.
     *
     * @param stroke  the stroke (<code>null</code> permitted).
     */
    public void setOutlineStroke(Stroke stroke) {
        setOutlineStroke(stroke, true);
    }

    /**
     * Sets the outline stroke for ALL series and, if requested, sends a 
     * {@link RendererChangeEvent} to all registered listeners.
     * 
     * @param stroke  the stroke (<code>null</code> permitted).
     * @param notify  notify listeners?
     */
    public void setOutlineStroke(Stroke stroke, boolean notify) {
        this.outlineStroke = stroke;
        if (notify) {
            notifyListeners(new RendererChangeEvent(this));
        }
    }
    
    /**
     * Sets the outline stroke used for a series and sends a 
     * {@link RendererChangeEvent} to all registered listeners.
     *
     * @param series  the series index (zero-based).
     * @param stroke  the stroke (<code>null</code> permitted).
     */
    public void setSeriesOutlineStroke(int series, Stroke stroke) {
        setSeriesOutlineStroke(series, stroke, true);
    }

    /**
     * Sets the outline stroke for a series and, if requested, sends a 
     * {@link RendererChangeEvent} to all registered listeners.
     * 
     * @param series  the series index.
     * @param stroke  the stroke (<code>null</code> permitted).
     * @param notify  notify listeners?
     */
    public void setSeriesOutlineStroke(int series, Stroke stroke, 
                                       boolean notify) {
        this.outlineStrokeList.setStroke(series, stroke);
        if (notify) {
            notifyListeners(new RendererChangeEvent(this));
        }
    }
    
    /**
     * Returns the base outline stroke.
     *
     * @return The stroke (never <code>null</code>).
     */
    public Stroke getBaseOutlineStroke() {
        return this.baseOutlineStroke;
    }

    /**
     * Sets the base outline stroke and sends a {@link RendererChangeEvent} to 
     * all registered listeners.
     *
     * @param stroke  the stroke (<code>null</code> not permitted).
     */
    public void setBaseOutlineStroke(Stroke stroke) {
        setBaseOutlineStroke(stroke, true);
    }

    /**
     * Sets the base outline stroke and, if requested, sends a 
     * {@link RendererChangeEvent} to all registered listeners.
     * 
     * @param stroke  the stroke (<code>null</code> not permitted).
     * @param notify  a flag that controls whether or not listeners are 
     *                notified.
     */
    public void setBaseOutlineStroke(Stroke stroke, boolean notify) {
        if (stroke == null) {
            throw new IllegalArgumentException("Null 'stroke' argument.");
        }
        this.baseOutlineStroke = stroke;
        if (notify) {
            notifyListeners(new RendererChangeEvent(this));
        }
    }
    
    // SHAPE
    
    /**
     * Returns a shape used to represent a data item.
     * <p>
     * The default implementation passes control to the getSeriesShape method.
     * You can override this method if you require different behaviour.
     *
     * @param row  the row (or series) index (zero-based).
     * @param column  the column (or category) index (zero-based).
     *
     * @return The shape (never <code>null</code>).
     */
    public Shape getItemShape(int row, int column) {
        return getSeriesShape(row);
    }

    /**
     * Returns a shape used to represent the items in a series.
     *
     * @param series  the series (zero-based index).
     *
     * @return The shape (never <code>null</code>).
     */
    public Shape getSeriesShape(int series) {

        // return the override, if there is one...
        if (this.shape != null) {
            return this.shape;
        }

        // otherwise look up the shape list
        Shape result = this.shapeList.getShape(series);
        if (result == null) {
            DrawingSupplier supplier = getDrawingSupplier();
            if (supplier != null) {
                result = supplier.getNextShape();
                this.shapeList.setShape(series, result);
            }
            else {
                result = this.baseShape;
            }
        }
        return result;

    }

    /**
     * Sets the shape for ALL series (optional) and sends a 
     * {@link RendererChangeEvent} to all registered listeners.
     * 
     * @param shape  the shape (<code>null</code> permitted).
     */
    public void setShape(Shape shape) {
        setShape(shape, true);
    }
    
    /**
     * Sets the shape for ALL series and, if requested, sends a 
     * {@link RendererChangeEvent} to all registered listeners.
     * 
     * @param shape  the shape (<code>null</code> permitted).
     * @param notify  notify listeners?
     */
    public void setShape(Shape shape, boolean notify) {
        this.shape = shape;
        if (notify) {
            notifyListeners(new RendererChangeEvent(this));
        }
    }
    
    /**
     * Sets the shape used for a series and sends a {@link RendererChangeEvent} 
     * to all registered listeners.
     *
     * @param series  the series index (zero-based).
     * @param shape  the shape (<code>null</code> permitted).
     */
    public void setSeriesShape(int series, Shape shape) {
        setSeriesShape(series, shape, true);
    }

    /**
     * Sets the shape for a series and, if requested, sends a 
     * {@link RendererChangeEvent} to all registered listeners.
     * 
     * @param series  the series index (zero based).
     * @param shape  the shape (<code>null</code> permitted).
     * @param notify  notify listeners?
     */
    public void setSeriesShape(int series, Shape shape, boolean notify) {
        this.shapeList.setShape(series, shape);
        if (notify) {
            notifyListeners(new RendererChangeEvent(this));
        }
    }
    
    /**
     * Returns the base shape.
     *
     * @return The shape (never <code>null</code>).
     */
    public Shape getBaseShape() {
        return this.baseShape;
    }

    /**
     * Sets the base shape and sends a {@link RendererChangeEvent} to all 
     * registered listeners.
     *
     * @param shape  the shape (<code>null</code> not permitted).
     */
    public void setBaseShape(Shape shape) {
        // defer argument checking...
        setBaseShape(shape, true);
    }

    /**
     * Sets the base shape and, if requested, sends a 
     * {@link RendererChangeEvent} to all registered listeners.
     * 
     * @param shape  the shape (<code>null</code> not permitted). 
     * @param notify  notify listeners?
     */
    public void setBaseShape(Shape shape, boolean notify) {
        if (shape == null) {
            throw new IllegalArgumentException("Null 'shape' argument."); 
        }
        this.baseShape = shape;
        if (notify) {
            notifyListeners(new RendererChangeEvent(this));
        }
    }
    
    // ITEM LABEL VISIBILITY...

    /**
     * Returns <code>true</code> if an item label is visible, and 
     * <code>false</code> otherwise.
     * 
     * @param row  the row index (zero-based).
     * @param column  the column index (zero-based).
     * 
     * @return A boolean.
     */
    public boolean isItemLabelVisible(int row, int column) {
        return isSeriesItemLabelsVisible(row);
    }

    /**
     * Returns <code>true</code> if the item labels for a series are visible, 
     * and <code>false</code> otherwise.
     * 
     * @param series  the series index (zero-based).
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
        setItemLabelsVisible(BooleanUtilities.valueOf(visible));
        // The following alternative is only supported in JDK 1.4 - we support 
        // JDK 1.2.2
        // setItemLabelsVisible(Boolean.valueOf(visible));
    }
    
    /**
     * Sets the visibility of the item labels for ALL series (optional).
     * 
     * @param visible  the flag (<code>null</code> permitted).
     */
    public void setItemLabelsVisible(Boolean visible) {
        setItemLabelsVisible(visible, true);
    }
    
    /**
     * Sets the visibility of item labels for ALL series and, if requested, 
     * sends a {@link RendererChangeEvent} to all registered listeners.
     * 
     * @param visible  a flag that controls whether or not the item labels are 
     *                 visible (<code>null</code> permitted).
     * @param notify  a flag that controls whether or not listeners are 
     *                notified.
     */
    public void setItemLabelsVisible(Boolean visible, boolean notify) {
        this.itemLabelsVisible = visible;
        if (notify) {
            notifyListeners(new RendererChangeEvent(this));
        }
    }

    /**
     * Sets a flag that controls the visibility of the item labels for a series.
     * 
     * @param series  the series index (zero-based).
     * @param visible  the flag.
     */
    public void setSeriesItemLabelsVisible(int series, boolean visible) {
        setSeriesItemLabelsVisible(series, BooleanUtilities.valueOf(visible));
    }
    
    /**
     * Sets the visibility of the item labels for a series.
     * 
     * @param series  the series index (zero-based).
     * @param visible  the flag (<code>null</code> permitted).
     */
    public void setSeriesItemLabelsVisible(int series, Boolean visible) {
        setSeriesItemLabelsVisible(series, visible, true);
    }

    /**
     * Sets the visibility of item labels for a series and, if requested, sends 
     * a {@link RendererChangeEvent} to all registered listeners.
     * 
     * @param series  the series index (zero-based).
     * @param visible  the visible flag.
     * @param notify  a flag that controls whether or not listeners are 
     *                notified.
     */
    public void setSeriesItemLabelsVisible(int series, Boolean visible, 
                                           boolean notify) {
        this.itemLabelsVisibleList.setBoolean(series, visible);
        if (notify) {
            notifyListeners(new RendererChangeEvent(this));
        }
    }

    /**
     * Returns the base setting for item label visibility.
     * 
     * @return A flag (possibly <code>null</code>).
     */
    public Boolean getBaseItemLabelsVisible() {
        return this.baseItemLabelsVisible;
    }

    /**
     * Sets the base flag that controls whether or not item labels are visible.
     * 
     * @param visible  the flag.
     */
    public void setBaseItemLabelsVisible(boolean visible) {
        setBaseItemLabelsVisible(BooleanUtilities.valueOf(visible));
    }
    
    /**
     * Sets the base setting for item label visibility.
     * 
     * @param visible  the flag (<code>null</code> permitted).
     */
    public void setBaseItemLabelsVisible(Boolean visible) {
        setBaseItemLabelsVisible(visible, true);
    }

    /**
     * Sets the base visibility for item labels and, if requested, sends a 
     * {@link RendererChangeEvent} to all registered listeners.
     * 
     * @param visible  the visibility flag.
     * @param notify  a flag that controls whether or not listeners are 
     *                notified.
     */
    public void setBaseItemLabelsVisible(Boolean visible, boolean notify) {
        this.baseItemLabelsVisible = visible;
        if (notify) {
            notifyListeners(new RendererChangeEvent(this));
        }
    }

    //// ITEM LABEL FONT //////////////////////////////////////////////////////

    /**
     * Returns the font for an item label.
     * 
     * @param row  the row index (zero-based).
     * @param column  the column index (zero-based).
     * 
     * @return The font (never <code>null</code>).
     */
    public Font getItemLabelFont(int row, int column) {
        Font result = this.itemLabelFont;
        if (result == null) {
            result = getSeriesItemLabelFont(row);
            if (result == null) {
                result = this.baseItemLabelFont;   
            }
        }
        return result;
    }

    /**
     * Returns the font used for all item labels.  This may be 
     * <code>null</code>, in which case the per series font settings will apply.
     * 
     * @return The font (possibly <code>null</code>).
     */
    public Font getItemLabelFont() {
        return this.itemLabelFont;   
    }
    
    /**
     * Sets the item label font for ALL series and sends a 
     * {@link RendererChangeEvent} to all registered listeners.  You can set 
     * this to <code>null</code> if you prefer to set the font on a per series 
     * basis.
     * 
     * @param font  the font (<code>null</code> permitted).
     */
    public void setItemLabelFont(Font font) {
        setItemLabelFont(font, true);
    }
    
    /**
     * Sets the item label font for ALL series and, if requested, sends a 
     * {@link RendererChangeEvent} to all registered listeners.
     * 
     * @param font  the font (<code>null</code> permitted).
     * @param notify  a flag that controls whether or not listeners are 
     *                notified.
     */ 
    public void setItemLabelFont(Font font, boolean notify) {
        this.itemLabelFont = font;
        if (notify) {
            notifyListeners(new RendererChangeEvent(this));
        }
    }

    /**
     * Returns the font for all the item labels in a series.
     * 
     * @param series  the series index (zero-based).
     * 
     * @return The font (possibly <code>null</code>).
     */
    public Font getSeriesItemLabelFont(int series) {
        return (Font) this.itemLabelFontList.get(series);
    }

    /**
     * Sets the item label font for a series and sends a 
     * {@link RendererChangeEvent} to all registered listeners.  
     * 
     * @param series  the series index (zero-based).
     * @param font  the font (<code>null</code> permitted).
     */
    public void setSeriesItemLabelFont(int series, Font font) {
        setSeriesItemLabelFont(series, font, true);
    }

    /**
     * Sets the item label font for a series and, if requested, sends a 
     * {@link RendererChangeEvent} to all registered listeners.
     * 
     * @param series  the series index (zero based).
     * @param font  the font (<code>null</code> permitted).
     * @param notify  a flag that controls whether or not listeners are 
     *                notified.
     */
    public void setSeriesItemLabelFont(int series, Font font, boolean notify) {
        this.itemLabelFontList.set(series, font);
        if (notify) {
            notifyListeners(new RendererChangeEvent(this));
        }
    }
    
    /**
     * Returns the base item label font (this is used when no other font 
     * setting is available).
     * 
     * @return The font (<code>never</code> null).
     */
    public Font getBaseItemLabelFont() {
        return this.baseItemLabelFont;
    }

    /**
     * Sets the base item label font and sends a {@link RendererChangeEvent} to 
     * all registered listeners.  
     * 
     * @param font  the font (<code>null</code> not permitted).
     */
    public void setBaseItemLabelFont(Font font) {
        if (font == null) {
            throw new IllegalArgumentException("Null 'font' argument.");
        }
        setBaseItemLabelFont(font, true);
    }

    /**
     * Sets the base item label font and, if requested, sends a 
     * {@link RendererChangeEvent} to all registered listeners.
     * 
     * @param font  the font (<code>null</code> not permitted).
     * @param notify  a flag that controls whether or not listeners are 
     *                notified.
     */
    public void setBaseItemLabelFont(Font font, boolean notify) {
        this.baseItemLabelFont = font;
        if (notify) {
            notifyListeners(new RendererChangeEvent(this));
        }
    }

    //// ITEM LABEL PAINT  ////////////////////////////////////////////////////

    /**
     * Returns the paint used to draw an item label.
     * 
     * @param row  the row index (zero based).
     * @param column  the column index (zero based).
     * 
     * @return The paint (never <code>null</code>).
     */
    public Paint getItemLabelPaint(int row, int column) {
        Paint result = this.itemLabelPaint;
        if (result == null) {
            result = getSeriesItemLabelPaint(row);
            if (result == null) {
                result = this.baseItemLabelPaint;   
            }
        }
        return result;
    }
    
    /**
     * Returns the paint used for all item labels.  This may be 
     * <code>null</code>, in which case the per series paint settings will 
     * apply.
     * 
     * @return The paint (possibly <code>null</code>).
     */
    public Paint getItemLabelPaint() {
        return this.itemLabelPaint;   
    }

    /**
     * Sets the item label paint for ALL series and sends a 
     * {@link RendererChangeEvent} to all registered listeners.
     * 
     * @param paint  the paint (<code>null</code> permitted).
     */
    public void setItemLabelPaint(Paint paint) {
        setItemLabelPaint(paint, true);
    }

    /**
     * Sets the item label paint for ALL series and, if requested, sends a 
     * {@link RendererChangeEvent} to all registered listeners.
     * 
     * @param paint  the paint.
     * @param notify  a flag that controls whether or not listeners are 
     *                notified.
     */
    public void setItemLabelPaint(Paint paint, boolean notify) {
        this.itemLabelPaint = paint;
        if (notify) {
            notifyListeners(new RendererChangeEvent(this));
        }
    }
    
    /**
     * Returns the paint used to draw the item labels for a series.
     * 
     * @param series  the series index (zero based).
     * 
     * @return The paint (possibly <code>null<code>).
     */
    public Paint getSeriesItemLabelPaint(int series) {
        return this.itemLabelPaintList.getPaint(series);
    }

    /**
     * Sets the item label paint for a series and sends a 
     * {@link RendererChangeEvent} to all registered listeners.
     * 
     * @param series  the series (zero based index).
     * @param paint  the paint (<code>null</code> permitted).
     */
    public void setSeriesItemLabelPaint(int series, Paint paint) {
        setSeriesItemLabelPaint(series, paint, true);
    }
    
    /**
     * Sets the item label paint for a series and, if requested, sends a 
     * {@link RendererChangeEvent} to all registered listeners.
     * 
     * @param series  the series index (zero based).
     * @param paint  the paint (<code>null</code> permitted).
     * @param notify  a flag that controls whether or not listeners are 
     *                notified.
     */
    public void setSeriesItemLabelPaint(int series, Paint paint, 
                                        boolean notify) {
        this.itemLabelPaintList.setPaint(series, paint);
        if (notify) {
            notifyListeners(new RendererChangeEvent(this));
        }
    }
    
    /**
     * Returns the base item label paint.
     * 
     * @return The paint (never <code>null<code>).
     */
    public Paint getBaseItemLabelPaint() {
        return this.baseItemLabelPaint;
    }

    /**
     * Sets the base item label paint and sends a {@link RendererChangeEvent} 
     * to all registered listeners.
     * 
     * @param paint  the paint (<code>null</code> not permitted).
     */
    public void setBaseItemLabelPaint(Paint paint) {
        // defer argument checking...
        setBaseItemLabelPaint(paint, true);
    }

    /**
     * Sets the base item label paint and, if requested, sends a 
     * {@link RendererChangeEvent} to all registered listeners..
     * 
     * @param paint  the paint (<code>null</code> not permitted).
     * @param notify  a flag that controls whether or not listeners are 
     *                notified.
     */
    public void setBaseItemLabelPaint(Paint paint, boolean notify) {
        if (paint == null) {
            throw new IllegalArgumentException("Null 'paint' argument.");   
        }
        this.baseItemLabelPaint = paint;
        if (notify) {
            notifyListeners(new RendererChangeEvent(this));
        }
    }
    
    // POSITIVE ITEM LABEL POSITION...

    /**
     * Returns the item label position for positive values.
     * 
     * @param row  the row index (zero-based).
     * @param column  the column index (zero-based).
     * 
     * @return The item label position (never <code>null</code>).
     */
    public ItemLabelPosition getPositiveItemLabelPosition(int row, int column) {
        return getSeriesPositiveItemLabelPosition(row);
    }

    /**
     * Returns the item label position for positive values in ALL series.
     * 
     * @return The item label position (possibly <code>null</code>).
     */
    public ItemLabelPosition getPositiveItemLabelPosition() {
        return this.positiveItemLabelPosition;
    }

    /**
     * Sets the item label position for positive values in ALL series, and 
     * sends a {@link RendererChangeEvent} to all registered listeners.  You 
     * need to set this to <code>null</code> to expose the settings for 
     * individual series.
     * 
     * @param position  the position (<code>null</code> permitted).
     */
    public void setPositiveItemLabelPosition(ItemLabelPosition position) {
        setPositiveItemLabelPosition(position, true);
    }
    
    /**
     * Sets the positive item label position for ALL series and (if requested) 
     * sends a {@link RendererChangeEvent} to all registered listeners.
     * 
     * @param position  the position (<code>null</code> permitted).
     * @param notify  notify registered listeners?
     */
    public void setPositiveItemLabelPosition(ItemLabelPosition position, 
                                             boolean notify) {
        this.positiveItemLabelPosition = position;
        if (notify) {
            notifyListeners(new RendererChangeEvent(this));
        }
    }

    /**
     * Returns the item label position for all positive values in a series.
     * 
     * @param series  the series index (zero-based).
     * 
     * @return The item label position (never <code>null</code>).
     */
    public ItemLabelPosition getSeriesPositiveItemLabelPosition(int series) {

        // return the override, if there is one...
        if (this.positiveItemLabelPosition != null) {
            return this.positiveItemLabelPosition;
        }

        // otherwise look up the position table
        ItemLabelPosition position = (ItemLabelPosition) 
            this.positiveItemLabelPositionList.get(series);
        if (position == null) {
            position = this.basePositiveItemLabelPosition;
        }
        return position;

    }
    
    /**
     * Sets the item label position for all positive values in a series and 
     * sends a {@link RendererChangeEvent} to all registered listeners.
     * 
     * @param series  the series index (zero-based).
     * @param position  the position (<code>null</code> permitted).
     */
    public void setSeriesPositiveItemLabelPosition(int series, 
                                                   ItemLabelPosition position) {
        setSeriesPositiveItemLabelPosition(series, position, true);
    }

    /**
     * Sets the item label position for all positive values in a series and (if
     * requested) sends a {@link RendererChangeEvent} to all registered 
     * listeners.
     * 
     * @param series  the series index (zero-based).
     * @param position  the position (<code>null</code> permitted).
     * @param notify  notify registered listeners?
     */
    public void setSeriesPositiveItemLabelPosition(int series, 
                                                   ItemLabelPosition position, 
                                                   boolean notify) {
        this.positiveItemLabelPositionList.set(series, position);
        if (notify) {
            notifyListeners(new RendererChangeEvent(this));
        }
    }

    /**
     * Returns the base positive item label position.
     * 
     * @return The position (never <code>null</code>).
     */
    public ItemLabelPosition getBasePositiveItemLabelPosition() {
        return this.basePositiveItemLabelPosition;
    }

    /**
     * Sets the base positive item label position.
     * 
     * @param position  the position (<code>null</code> not permitted).
     */
    public void setBasePositiveItemLabelPosition(ItemLabelPosition position) {
        // defer argument checking...
        setBasePositiveItemLabelPosition(position, true);
    }
    
    /**
     * Sets the base positive item label position and, if requested, sends a 
     * {@link RendererChangeEvent} to all registered listeners.
     * 
     * @param position  the position (<code>null</code> not permitted).
     * @param notify  notify registered listeners?
     */
    public void setBasePositiveItemLabelPosition(ItemLabelPosition position, 
                                                 boolean notify) {
        if (position == null) {
            throw new IllegalArgumentException("Null 'position' argument.");   
        }
        this.basePositiveItemLabelPosition = position;
        if (notify) {
            notifyListeners(new RendererChangeEvent(this));
        }
    }

    // NEGATIVE ITEM LABEL POSITION...

    /**
     * Returns the item label position for negative values.  This method can be 
     * overridden to provide customisation of the item label position for 
     * individual data items.
     * 
     * @param row  the row index (zero-based).
     * @param column  the column (zero-based).
     * 
     * @return The item label position (never <code>null</code>).
     */
    public ItemLabelPosition getNegativeItemLabelPosition(int row, int column) {
        return getSeriesNegativeItemLabelPosition(row);
    }

    /**
     * Returns the item label position for negative values in ALL series.
     * 
     * @return The item label position (possibly <code>null</code>).
     */
    public ItemLabelPosition getNegativeItemLabelPosition() {
        return this.negativeItemLabelPosition;
    }

    /**
     * Sets the item label position for negative values in ALL series, and 
     * sends a {@link RendererChangeEvent} to all registered listeners.  You 
     * need to set this to <code>null</code> to expose the settings for 
     * individual series.
     * 
     * @param position  the position (<code>null</code> permitted).
     */
    public void setNegativeItemLabelPosition(ItemLabelPosition position) {
        setNegativeItemLabelPosition(position, true);
    }
    
    /**
     * Sets the item label position for negative values in ALL series and (if 
     * requested) sends a {@link RendererChangeEvent} to all registered 
     * listeners.  
     * 
     * @param position  the position (<code>null</code> permitted).
     * @param notify  notify registered listeners?
     */
    public void setNegativeItemLabelPosition(ItemLabelPosition position, 
                                             boolean notify) {
        this.negativeItemLabelPosition = position;
        if (notify) {
            notifyListeners(new RendererChangeEvent(this));
        }
    }

    /**
     * Returns the item label position for all negative values in a series.
     * 
     * @param series  the series index (zero-based).
     * 
     * @return The item label position (never <code>null</code>).
     */
    public ItemLabelPosition getSeriesNegativeItemLabelPosition(int series) {

        // return the override, if there is one...
        if (this.negativeItemLabelPosition != null) {
            return this.negativeItemLabelPosition;
        }

        // otherwise look up the position list
        ItemLabelPosition position = (ItemLabelPosition) 
            this.negativeItemLabelPositionList.get(series);
        if (position == null) {
            position = this.baseNegativeItemLabelPosition;
        }
        return position;

    }

    /**
     * Sets the item label position for negative values in a series and sends a 
     * {@link RendererChangeEvent} to all registered listeners.
     * 
     * @param series  the series index (zero-based).
     * @param position  the position (<code>null</code> permitted).
     */
    public void setSeriesNegativeItemLabelPosition(int series, 
                                                   ItemLabelPosition position) {
        setSeriesNegativeItemLabelPosition(series, position, true);
    }

    /**
     * Sets the item label position for negative values in a series and (if 
     * requested) sends a {@link RendererChangeEvent} to all registered 
     * listeners.
     * 
     * @param series  the series index (zero-based).
     * @param position  the position (<code>null</code> permitted).
     * @param notify  notify registered listeners?
     */
    public void setSeriesNegativeItemLabelPosition(int series, 
                                                   ItemLabelPosition position, 
                                                   boolean notify) {
        this.negativeItemLabelPositionList.set(series, position);
        if (notify) {
            notifyListeners(new RendererChangeEvent(this));
        }
    }

    /**
     * Returns the base item label position for negative values.
     * 
     * @return The position (never <code>null</code>).
     */
    public ItemLabelPosition getBaseNegativeItemLabelPosition() {
        return this.baseNegativeItemLabelPosition;
    }

    /**
     * Sets the base item label position for negative values and sends a 
     * {@link RendererChangeEvent} to all registered listeners.
     * 
     * @param position  the position (<code>null</code> not permitted).
     */
    public void setBaseNegativeItemLabelPosition(ItemLabelPosition position) {
        setBaseNegativeItemLabelPosition(position, true);
    }
    
    /**
     * Sets the base negative item label position and, if requested, sends a 
     * {@link RendererChangeEvent} to all registered listeners.
     * 
     * @param position  the position (<code>null</code> not permitted).
     * @param notify  notify registered listeners?
     */
    public void setBaseNegativeItemLabelPosition(ItemLabelPosition position, 
                                                 boolean notify) {
        if (position == null) {
            throw new IllegalArgumentException("Null 'position' argument.");   
        }
        this.baseNegativeItemLabelPosition = position;
        if (notify) {
            notifyListeners(new RendererChangeEvent(this));
        }
    }

    /**
     * Returns the item label anchor offset.
     *
     * @return The offset.
     */
    public double getItemLabelAnchorOffset() {
        return this.itemLabelAnchorOffset;
    }

    /**
     * Sets the item label anchor offset.
     *
     * @param offset  the offset.
     */
    public void setItemLabelAnchorOffset(double offset) {
        this.itemLabelAnchorOffset = offset;
        notifyListeners(new RendererChangeEvent(this));
    }

    /**
     * Returns a boolean that indicates whether or not the specified item 
     * should have a chart entity created for it.
     * 
     * @param series  the series index.
     * @param item  the item index.
     * 
     * @return A boolean.
     */
    public boolean getItemCreateEntity(int series, int item) {
        if (this.createEntities != null) {
            return this.createEntities.booleanValue();
        }
        else {
            Boolean b = getSeriesCreateEntities(series);
            if (b != null) {
                return b.booleanValue();
            }
            else {
                return this.baseCreateEntities;
            }
        }
    }
    
    /**
     * Returns the flag that controls whether or not chart entities are created 
     * for the items in ALL series.  This flag overrides the per series and 
     * default settings - you must set it to <code>null</code> if you want the
     * other settings to apply.
     * 
     * @return The flag (possibly <code>null</code>).
     */
    public Boolean getCreateEntities() {
        return this.createEntities;  
    }
    
    /**
     * Sets the flag that controls whether or not chart entities are created 
     * for the items in ALL series, and sends a {@link RendererChangeEvent} to 
     * all registered listeners.  This flag overrides the per series and 
     * default settings - you must set it to <code>null</code> if you want the
     * other settings to apply.
     * 
     * @param create  the flag (<code>null</code> permitted).
     */
    public void setCreateEntities(Boolean create) {
         setCreateEntities(create, true);
    }
    
    /**
     * Sets the flag that controls whether or not chart entities are created 
     * for the items in ALL series, and sends a {@link RendererChangeEvent} to 
     * all registered listeners.  This flag overrides the per series and 
     * default settings - you must set it to <code>null</code> if you want the
     * other settings to apply.
     * 
     * @param create  the flag (<code>null</code> permitted).
     * @param notify  notify listeners?
     */
    public void setCreateEntities(Boolean create, boolean notify) {
        this.createEntities = create;   
        if (notify) {
            notifyListeners(new RendererChangeEvent(this));
        }
    }
    
    /**
     * Returns the flag that controls whether entities are created for a
     * series.
     *
     * @param series  the series index (zero-based).
     *
     * @return The flag (possibly <code>null</code>).
     */
    public Boolean getSeriesCreateEntities(int series) {
        return this.createEntitiesList.getBoolean(series);
    }
    
    /**
     * Sets the flag that controls whether entities are created for a series,
     * and sends a {@link RendererChangeEvent} to all registered listeners.
     *
     * @param series  the series index (zero-based).
     * @param create  the flag (<code>null</code> permitted).
     */
    public void setSeriesCreateEntities(int series, Boolean create) {
        setSeriesCreateEntities(series, create, true);
    }
    
    /**
     * Sets the flag that controls whether entities are created for a series
     * and, if requested, sends a {@link RendererChangeEvent} to all registered 
     * listeners.
     * 
     * @param series  the series index.
     * @param create  the flag (<code>null</code> permitted).
     * @param notify  notify listeners?
     */
    public void setSeriesCreateEntities(int series, Boolean create, 
                                        boolean notify) {
        this.createEntitiesList.setBoolean(series, create);       
        if (notify) {
            notifyListeners(new RendererChangeEvent(this));
        }
    }

    /**
     * Returns the base visibility for all series.
     *
     * @return The base visibility.
     */
    public boolean getBaseCreateEntities() {
        return this.baseCreateEntities;
    }

    /**
     * Sets the base flag that controls whether entities are created
     * for a series, and sends a {@link RendererChangeEvent} 
     * to all registered listeners.
     *
     * @param create  the flag.
     */
    public void setBaseCreateEntities(boolean create) {
        // defer argument checking...
        setBaseCreateEntities(create, true);
    }
    
    /**
     * Sets the base flag that controls whether entities are created and, 
     * if requested, sends a {@link RendererChangeEvent} to all registered 
     * listeners.
     * 
     * @param create  the visibility.
     * @param notify  notify listeners?
     */
    public void setBaseCreateEntities(boolean create, boolean notify) {
        this.baseCreateEntities = create;
        if (notify) {
            notifyListeners(new RendererChangeEvent(this));
        }
    }

    /** The adjacent offset. */
    private static final double ADJ = Math.cos(Math.PI / 6.0);
    
    /** The opposite offset. */
    private static final double OPP = Math.sin(Math.PI / 6.0);
    
    /**
     * Calculates the item label anchor point.
     *
     * @param anchor  the anchor.
     * @param x  the x coordinate.
     * @param y  the y coordinate.
     * @param orientation  the plot orientation.
     *
     * @return The anchor point (never <code>null</code>).
     */
    protected Point2D calculateLabelAnchorPoint(ItemLabelAnchor anchor,
            double x, double y, PlotOrientation orientation) {
        Point2D result = null;
        if (anchor == ItemLabelAnchor.CENTER) {
            result = new Point2D.Double(x, y);
        }
        else if (anchor == ItemLabelAnchor.INSIDE1) {
            result = new Point2D.Double(x + OPP * this.itemLabelAnchorOffset, 
                    y - ADJ * this.itemLabelAnchorOffset);
        }
        else if (anchor == ItemLabelAnchor.INSIDE2) {
            result = new Point2D.Double(x + ADJ * this.itemLabelAnchorOffset, 
                    y - OPP * this.itemLabelAnchorOffset);
        }
        else if (anchor == ItemLabelAnchor.INSIDE3) {
            result = new Point2D.Double(x + this.itemLabelAnchorOffset, y);
        }
        else if (anchor == ItemLabelAnchor.INSIDE4) {
            result = new Point2D.Double(x + ADJ * this.itemLabelAnchorOffset, 
                    y + OPP * this.itemLabelAnchorOffset);
        }
        else if (anchor == ItemLabelAnchor.INSIDE5) {
            result = new Point2D.Double(x + OPP * this.itemLabelAnchorOffset, 
                    y + ADJ * this.itemLabelAnchorOffset);
        }
        else if (anchor == ItemLabelAnchor.INSIDE6) {
            result = new Point2D.Double(x, y + this.itemLabelAnchorOffset);
        }
        else if (anchor == ItemLabelAnchor.INSIDE7) {
            result = new Point2D.Double(x - OPP * this.itemLabelAnchorOffset, 
                    y + ADJ * this.itemLabelAnchorOffset);
        }
        else if (anchor == ItemLabelAnchor.INSIDE8) {
            result = new Point2D.Double(x - ADJ * this.itemLabelAnchorOffset, 
                    y + OPP * this.itemLabelAnchorOffset);
        }
        else if (anchor == ItemLabelAnchor.INSIDE9) {
            result = new Point2D.Double(x - this.itemLabelAnchorOffset, y);
        }
        else if (anchor == ItemLabelAnchor.INSIDE10) {
            result = new Point2D.Double(x - ADJ * this.itemLabelAnchorOffset, 
                    y - OPP * this.itemLabelAnchorOffset);
        }
        else if (anchor == ItemLabelAnchor.INSIDE11) {
            result = new Point2D.Double(x - OPP * this.itemLabelAnchorOffset, 
                    y - ADJ * this.itemLabelAnchorOffset);
        }
        else if (anchor == ItemLabelAnchor.INSIDE12) {
            result = new Point2D.Double(x, y - this.itemLabelAnchorOffset);
        }
        else if (anchor == ItemLabelAnchor.OUTSIDE1) {
            result = new Point2D.Double(
                    x + 2.0 * OPP * this.itemLabelAnchorOffset, 
                    y - 2.0 * ADJ * this.itemLabelAnchorOffset);
        }
        else if (anchor == ItemLabelAnchor.OUTSIDE2) {
            result = new Point2D.Double(
                    x + 2.0 * ADJ * this.itemLabelAnchorOffset, 
                    y - 2.0 * OPP * this.itemLabelAnchorOffset);
        }
        else if (anchor == ItemLabelAnchor.OUTSIDE3) {
            result = new Point2D.Double(x + 2.0 * this.itemLabelAnchorOffset, 
                    y);
        }
        else if (anchor == ItemLabelAnchor.OUTSIDE4) {
            result = new Point2D.Double(
                    x + 2.0 * ADJ * this.itemLabelAnchorOffset, 
                    y + 2.0 * OPP * this.itemLabelAnchorOffset);
        }
        else if (anchor == ItemLabelAnchor.OUTSIDE5) {
            result = new Point2D.Double(
                    x + 2.0 * OPP * this.itemLabelAnchorOffset, 
                    y + 2.0 * ADJ * this.itemLabelAnchorOffset);
        }
        else if (anchor == ItemLabelAnchor.OUTSIDE6) {
            result = new Point2D.Double(x, 
                    y + 2.0 * this.itemLabelAnchorOffset);
        }
        else if (anchor == ItemLabelAnchor.OUTSIDE7) {
            result = new Point2D.Double(
                    x - 2.0 * OPP * this.itemLabelAnchorOffset, 
                    y + 2.0 * ADJ * this.itemLabelAnchorOffset);
        }
        else if (anchor == ItemLabelAnchor.OUTSIDE8) {
            result = new Point2D.Double(
                    x - 2.0 * ADJ * this.itemLabelAnchorOffset, 
                    y + 2.0 * OPP * this.itemLabelAnchorOffset);
        }
        else if (anchor == ItemLabelAnchor.OUTSIDE9) {
            result = new Point2D.Double(x - 2.0 * this.itemLabelAnchorOffset, 
                    y);
        }
        else if (anchor == ItemLabelAnchor.OUTSIDE10) {
            result = new Point2D.Double(
                    x - 2.0 * ADJ * this.itemLabelAnchorOffset, 
                    y - 2.0 * OPP * this.itemLabelAnchorOffset);
        }
        else if (anchor == ItemLabelAnchor.OUTSIDE11) {
            result = new Point2D.Double(
                x - 2.0 * OPP * this.itemLabelAnchorOffset, 
                y - 2.0 * ADJ * this.itemLabelAnchorOffset);
        }
        else if (anchor == ItemLabelAnchor.OUTSIDE12) {
            result = new Point2D.Double(x, 
                    y - 2.0 * this.itemLabelAnchorOffset);
        }
        return result;
    }
    
    /**
     * Registers an object to receive notification of changes to the renderer.
     *
     * @param listener  the listener (<code>null</code> not permitted).
     */
    public void addChangeListener(RendererChangeListener listener) {
        if (listener == null) {
            throw new IllegalArgumentException("Null 'listener' argument.");   
        }
        this.listenerList.add(RendererChangeListener.class, listener);
    }

    /**
     * Deregisters an object so that it no longer receives 
     * notification of changes to the renderer.
     *
     * @param listener  the object (<code>null</code> not permitted).
     */
    public void removeChangeListener(RendererChangeListener listener) {
        if (listener == null) {
            throw new IllegalArgumentException("Null 'listener' argument.");   
        }
        this.listenerList.remove(RendererChangeListener.class, listener);
    }

    /**
     * Returns <code>true</code> if the specified object is registered with
     * the dataset as a listener.  Most applications won't need to call this 
     * method, it exists mainly for use by unit testing code.
     * 
     * @param listener  the listener.
     * 
     * @return A boolean.
     */
    public boolean hasListener(EventListener listener) {
        List list = Arrays.asList(this.listenerList.getListenerList());
        return list.contains(listener);
    }
    
    /**
     * Notifies all registered listeners that the renderer has been modified.
     *
     * @param event  information about the change event.
     */
    public void notifyListeners(RendererChangeEvent event) {

        Object[] ls = this.listenerList.getListenerList();
        for (int i = ls.length - 2; i >= 0; i -= 2) {
            if (ls[i] == RendererChangeListener.class) {
                ((RendererChangeListener) ls[i + 1]).rendererChanged(event);
            }
        }

    }

    /**
     * Tests this renderer for equality with another object.
     *
     * @param obj  the object (<code>null</code> permitted).
     *
     * @return <code>true</code> or <code>false</code>.
     */
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof AbstractRenderer)) {
            return false;
        }
        AbstractRenderer that = (AbstractRenderer) obj;
        if (!ObjectUtilities.equal(this.seriesVisible, that.seriesVisible)) {
            return false;   
        }
        if (!this.seriesVisibleList.equals(that.seriesVisibleList)) {
            return false;   
        }
        if (this.baseSeriesVisible != that.baseSeriesVisible) {
            return false;   
        }
        if (!ObjectUtilities.equal(this.seriesVisibleInLegend, 
                that.seriesVisibleInLegend)) {
            return false;   
        }
        if (!this.seriesVisibleInLegendList.equals(
                that.seriesVisibleInLegendList)) {
            return false;   
        }
        if (this.baseSeriesVisibleInLegend != that.baseSeriesVisibleInLegend) {
            return false;   
        }
        if (!PaintUtilities.equal(this.paint, that.paint)) {
            return false;
        }
        if (!ObjectUtilities.equal(this.paintList, that.paintList)) {
            return false;
        }
        if (!PaintUtilities.equal(this.basePaint, that.basePaint)) {
            return false;
        }
        if (!PaintUtilities.equal(this.fillPaint, that.fillPaint)) {
            return false;
        }
        if (!ObjectUtilities.equal(this.fillPaintList, that.fillPaintList)) {
            return false;
        }
        if (!PaintUtilities.equal(this.baseFillPaint, that.baseFillPaint)) {
            return false;
        }
        if (!PaintUtilities.equal(this.outlinePaint, that.outlinePaint)) {
            return false;
        }
        if (!ObjectUtilities.equal(this.outlinePaintList,
                that.outlinePaintList)) {
            return false;
        }
        if (!PaintUtilities.equal(this.baseOutlinePaint, 
                that.baseOutlinePaint)) {
            return false;
        }
        if (!ObjectUtilities.equal(this.stroke, that.stroke)) {
            return false;
        }
        if (!ObjectUtilities.equal(this.strokeList, that.strokeList)) {
            return false;
        }
        if (!ObjectUtilities.equal(this.baseStroke, that.baseStroke)) {
            return false;
        }
        if (!ObjectUtilities.equal(this.outlineStroke, that.outlineStroke)) {
            return false;
        }
        if (!ObjectUtilities.equal(
            this.outlineStrokeList, that.outlineStrokeList
        )) {
            return false;
        }
        if (!ObjectUtilities.equal(
            this.baseOutlineStroke, that.baseOutlineStroke)
        ) {
            return false;
        }
        if (!ObjectUtilities.equal(this.shape, that.shape)) {
            return false;
        }
        if (!ObjectUtilities.equal(this.shapeList, that.shapeList)) {
            return false;
        }
        if (!ObjectUtilities.equal(this.baseShape, that.baseShape)) {
            return false;
        }
        if (!ObjectUtilities.equal(
            this.itemLabelsVisible, that.itemLabelsVisible
        )) {
            return false;
        }
        if (!ObjectUtilities.equal(
            this.itemLabelsVisibleList, that.itemLabelsVisibleList)
        ) {
            return false;
        }
        if (!ObjectUtilities.equal(
            this.baseItemLabelsVisible, that.baseItemLabelsVisible
        )) {
            return false;
        }
        if (!ObjectUtilities.equal(this.itemLabelFont, that.itemLabelFont)) {
            return false;
        }
        if (!ObjectUtilities.equal(
            this.itemLabelFontList, that.itemLabelFontList
        )) {
            return false;
        }
        if (!ObjectUtilities.equal(
            this.baseItemLabelFont, that.baseItemLabelFont
        )) {
            return false;
        }
 
        if (!PaintUtilities.equal(this.itemLabelPaint, that.itemLabelPaint)) {
            return false;
        }
        if (!ObjectUtilities.equal(
            this.itemLabelPaintList, that.itemLabelPaintList
        )) {
            return false;
        }
        if (!PaintUtilities.equal(
            this.baseItemLabelPaint, that.baseItemLabelPaint
        )) {
            return false;
        }

        if (!ObjectUtilities.equal(
                this.positiveItemLabelPosition, that.positiveItemLabelPosition
            )) {
            return false;
        }
        if (!ObjectUtilities.equal(
                this.positiveItemLabelPositionList, 
                that.positiveItemLabelPositionList
            )) {
            return false;
        }
        if (!ObjectUtilities.equal(
                this.basePositiveItemLabelPosition, 
                that.basePositiveItemLabelPosition
            )) {
            return false;
        }

        if (!ObjectUtilities.equal(
                this.negativeItemLabelPosition, that.negativeItemLabelPosition
            )) {
            return false;
        }
        if (!ObjectUtilities.equal(
                this.negativeItemLabelPositionList, 
                that.negativeItemLabelPositionList
            )) {
            return false;
        }
        if (!ObjectUtilities.equal(
                this.baseNegativeItemLabelPosition, 
                that.baseNegativeItemLabelPosition
            )) {
            return false;
        }
        if (this.itemLabelAnchorOffset != that.itemLabelAnchorOffset) {
            return false;
        }
        if (!ObjectUtilities.equal(this.createEntities, that.createEntities)) {
            return false;   
        }
        if (!ObjectUtilities.equal(this.createEntitiesList, 
                that.createEntitiesList)) {
            return false;   
        }
        if (this.baseCreateEntities != that.baseCreateEntities) {
            return false;   
        }
        return true;
    }
    
    /**
     * Returns a hashcode for the renderer.
     * 
     * @return The hashcode.
     */
    public int hashCode() {
        int result = 193;   
        result = 37 * result + ObjectUtilities.hashCode(this.stroke);     
        result = 37 * result + ObjectUtilities.hashCode(this.baseStroke);    
        result = 37 * result + ObjectUtilities.hashCode(this.outlineStroke);
        result = 37 * result + ObjectUtilities.hashCode(this.baseOutlineStroke);
        return result;
    }
    
    /**
     * Returns an independent copy of the renderer.
     * 
     * @return A clone.
     * 
     * @throws CloneNotSupportedException if some component of the renderer 
     *         does not support cloning.
     */
    protected Object clone() throws CloneNotSupportedException {
        AbstractRenderer clone = (AbstractRenderer) super.clone();
        
        // 'paint' : immutable, no need to clone reference
        if (this.paintList != null) {
            clone.paintList = (PaintList) this.paintList.clone();
        }
        // 'basePaint' : immutable, no need to clone reference
        
        if (this.fillPaintList != null) {
            clone.fillPaintList = (PaintList) this.fillPaintList.clone();
        }
        // 'outlinePaint' : immutable, no need to clone reference
        if (this.outlinePaintList != null) {
            clone.outlinePaintList = (PaintList) this.outlinePaintList.clone();
        }
        // 'baseOutlinePaint' : immutable, no need to clone reference
        
        // 'stroke' : immutable, no need to clone reference
        if (this.strokeList != null) {
            clone.strokeList = (StrokeList) this.strokeList.clone();
        }
        // 'baseStroke' : immutable, no need to clone reference
        
        // 'outlineStroke' : immutable, no need to clone reference
        if (this.outlineStrokeList != null) {
            clone.outlineStrokeList 
                = (StrokeList) this.outlineStrokeList.clone();
        }
        // 'baseOutlineStroke' : immutable, no need to clone reference
        
        if (this.shape != null) {
            clone.shape = ShapeUtilities.clone(this.shape);
        }
        if (this.baseShape != null) {
            clone.baseShape = ShapeUtilities.clone(this.baseShape);
        }
        
        // 'itemLabelsVisible' : immutable, no need to clone reference
        if (this.itemLabelsVisibleList != null) {
            clone.itemLabelsVisibleList 
                = (BooleanList) this.itemLabelsVisibleList.clone();
        }
        // 'basePaint' : immutable, no need to clone reference
        
        // 'itemLabelFont' : immutable, no need to clone reference
        if (this.itemLabelFontList != null) {
            clone.itemLabelFontList 
                = (ObjectList) this.itemLabelFontList.clone();
        }
        // 'baseItemLabelFont' : immutable, no need to clone reference

        // 'itemLabelPaint' : immutable, no need to clone reference
        if (this.itemLabelPaintList != null) {
            clone.itemLabelPaintList 
                = (PaintList) this.itemLabelPaintList.clone();
        }
        // 'baseItemLabelPaint' : immutable, no need to clone reference
        
        // 'postiveItemLabelAnchor' : immutable, no need to clone reference
        if (this.positiveItemLabelPositionList != null) {
            clone.positiveItemLabelPositionList 
                = (ObjectList) this.positiveItemLabelPositionList.clone();
        }
        // 'baseItemLabelAnchor' : immutable, no need to clone reference

        // 'negativeItemLabelAnchor' : immutable, no need to clone reference
        if (this.negativeItemLabelPositionList != null) {
            clone.negativeItemLabelPositionList 
                = (ObjectList) this.negativeItemLabelPositionList.clone();
        }
        // 'baseNegativeItemLabelAnchor' : immutable, no need to clone reference
        
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
        SerialUtilities.writePaint(this.fillPaint, stream);
        SerialUtilities.writePaint(this.baseFillPaint, stream);
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
    private void readObject(ObjectInputStream stream) 
        throws IOException, ClassNotFoundException {

        stream.defaultReadObject();
        this.paint = SerialUtilities.readPaint(stream);
        this.basePaint = SerialUtilities.readPaint(stream);
        this.fillPaint = SerialUtilities.readPaint(stream);
        this.baseFillPaint = SerialUtilities.readPaint(stream);
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
        
        // listeners are not restored automatically, but storage must be 
        // provided...
        this.listenerList = new EventListenerList();

    }

}
