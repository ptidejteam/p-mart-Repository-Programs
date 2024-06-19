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
 * -------------------------
 * CategoryItemRenderer.java
 * -------------------------
 *
 * (C) Copyright 2001-2004, by Object Refinery Limited and Contributors.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited);
 * Contributor(s):   Mark Watson (www.markwatson.com);
 *
 * $Id: CategoryItemRenderer.java,v 1.1 2007/10/10 19:29:18 vauchers Exp $
 *
 * Changes
 * -------
 * 23-Oct-2001 : Version 1 (DG);
 * 16-Jan-2002 : Renamed HorizontalCategoryItemRenderer.java --> CategoryItemRenderer.java (DG);
 * 05-Feb-2002 : Changed return type of the drawCategoryItem method from void to Shape, as part
 *               of the tooltips implementation (DG)
 *
 *               NOTE (30-May-2002) : this has subsequently been changed back to void, tooltips
 *               are now collected along with entities in ChartRenderingInfo (DG);
 *
 * 14-Mar-2002 : Added the initialise method, and changed all bar plots to use this renderer (DG);
 * 23-May-2002 : Added ChartRenderingInfo to the initialise method (DG);
 * 29-May-2002 : Added the getAxisArea(Rectangle2D) method (DG);
 * 06-Jun-2002 : Updated Javadoc comments (DG);
 * 26-Jun-2002 : Added range axis to the initialise method (DG);
 * 24-Sep-2002 : Added getLegendItem(...) method (DG);
 * 23-Oct-2002 : Added methods to get/setToolTipGenerator (DG);
 * 05-Nov-2002 : Replaced references to CategoryDataset with TableDataset (DG);
 * 06-Nov-2002 : Added the domain axis to the drawCategoryItem method.  Renamed
 *               drawCategoryItem(...) --> drawItem(...) (DG);
 * 20-Nov-2002 : Changed signature of drawItem(...) method to reflect use of TableDataset (DG);
 * 26-Nov-2002 : Replaced the isStacked() method with the getRangeType() method (DG);
 * 08-Jan-2003 : Changed getSeriesCount() --> getRowCount() and
 *               getCategoryCount() --> getColumnCount() (DG);
 * 09-Jan-2003 : Changed name of grid-line methods (DG);
 * 21-Jan-2003 : Merged TableDataset with CategoryDataset (DG);
 * 10-Apr-2003 : Changed CategoryDataset to KeyedValues2DDataset in drawItem(...) method (DG);
 * 29-Apr-2003 : Eliminated Renderer interface (DG);
 * 02-Sep-2003 : Fix for bug 790407 (DG);
 * 16-Sep-2003 : Changed ChartRenderingInfo --> PlotRenderingInfo (DG);
 * 20-Oct-2003 : Added setOutlinePaint(...) method (DG);
 * 06-Feb-2004 : Added missing methods, and moved deprecated methods (DG);
 * 19-Feb-2004 : Added extra setXXXLabelsVisible() methods (DG);
 *
 */

package org.jfree.chart.renderer;

import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.Rectangle2D;

import org.jfree.chart.LegendItem;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.event.RendererChangeEvent;  // for Javadocs
import org.jfree.chart.event.RendererChangeListener;
import org.jfree.chart.labels.CategoryItemLabelGenerator;
import org.jfree.chart.labels.ItemLabelAnchor;
import org.jfree.chart.labels.ItemLabelPosition;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.Marker;
import org.jfree.chart.plot.PlotRenderingInfo;
import org.jfree.chart.urls.CategoryURLGenerator;
import org.jfree.data.CategoryDataset;
import org.jfree.ui.TextAnchor;

/**
 * A plug-in object that is used by the {@link CategoryPlot} class to display individual data items
 * from a {@link CategoryDataset}.
 * <p>
 * This interface defines the methods that must be provided by all renderers.  If you are
 * implementing a custom renderer, you should consider extending the
 * {@link AbstractCategoryItemRenderer} class.
 *
 * @author David Gilbert
 */
public interface CategoryItemRenderer {

    /**
     * Returns the plot that the renderer has been assigned to.
     *
     * @return the plot.
     */
    public CategoryPlot getPlot();

    /**
     * Sets the plot that the renderer has been assigned to.
     * <P>
     * You shouldn't need to call this method yourself, the plot will do it for you when you
     * assign the renderer to the plot.
     *
     * @param plot  the plot.
     */
    public void setPlot(CategoryPlot plot);

    /**
     * Adds a change listener.
     * 
     * @param listener  the listener.
     */
    public void addChangeListener(RendererChangeListener listener);
    
    /**
     * Removes a change listener.
     * 
     * @param listener  the listener.
     */
    public void removeChangeListener(RendererChangeListener listener);
    
    /**
     * Returns the range type for the renderer.  The plot needs to know this information in order
     * to determine an appropriate axis range (when the axis auto-range calculation is on).
     * <P>
     * Two types are recognised:
     * <ul>
     *   <li><code>STANDARD</code> - data items are plotted individually, so the axis range should
     *     extend from the smallest value to the largest value;</li>
     * <li><code>STACKED</code> - data items are stacked on top of one another, so to determine
     *     the axis range, all the items in a series need to be summed together.</li>
     * </ul>
     *
     * If the data values are stacked, this affects the axis range required to
     * display all the data items.
     *
     * @return the range type (never <code>null</code>).
     */
    public RangeType getRangeType();

    /**
     * Initialises the renderer.  This method will be called before the first item is rendered,
     * giving the renderer an opportunity to initialise any state information it wants to maintain.
     * The renderer can do nothing if it chooses.
     *
     * @param g2  the graphics device.
     * @param dataArea  the area inside the axes.
     * @param plot  the plot.
     * @param index  the secondary index (<code>null</code> for primary renderer).
     * @param info  collects chart rendering information for return to caller.
     * 
     * @return A state object (maintains state information relevant to one chart drawing).
     */
    public CategoryItemRendererState initialise(Graphics2D g2,
                                                Rectangle2D dataArea,
                                                CategoryPlot plot,
                                                Integer index,
                                                PlotRenderingInfo info);
                           
    // PAINT
    
    /**
     * Returns the paint used to fill data items as they are drawn.
     *
     * @param row  the row (or series) index (zero-based).
     * @param column  the column (or category) index (zero-based).
     *
     * @return the paint (never <code>null</code>).
     */
    public Paint getItemPaint(int row, int column);

    /**
     * Returns the paint used to fill an item drawn by the renderer.
     *
     * @param series  the series index (zero-based).
     *
     * @return the paint (never <code>null</code>).
     */
    public Paint getSeriesPaint(int series);

    /**
     * Sets the paint to be used for ALL series, and sends a {@link RendererChangeEvent} to all
     * registered listeners.  If this is <code>null</code>, the renderer will use the paint for 
     * the series.
     * 
     * @param paint  the paint (<code>null</code> permitted).
     */
    public void setPaint(Paint paint);
    
    /**
     * Sets the paint used for a series and sends a {@link RendererChangeEvent} to all registered 
     * listeners.
     *
     * @param series  the series index (zero-based).
     * @param paint  the paint (<code>null</code> permitted).
     */
    public void setSeriesPaint(int series, Paint paint);
    
    /**
     * Returns the base paint.
     *
     * @return the base paint (never <code>null</code>).
     */
    public Paint getBasePaint();

    /**
     * Sets the base paint and sends a {@link RendererChangeEvent} to all registered listeners.
     *
     * @param paint  the paint (<code>null</code> not permitted).
     */
    public void setBasePaint(Paint paint);
    
    // OUTLINE PAINT
    
    /**
     * Returns the paint used to outline data items as they are drawn.
     *
     * @param row  the row (or series) index (zero-based).
     * @param column  the column (or category) index (zero-based).
     *
     * @return the paint (never <code>null</code>).
     */
    public Paint getItemOutlinePaint(int row, int column);

    /**
     * Returns the paint used to outline an item drawn by the renderer.
     *
     * @param series  the series (zero-based index).
     *
     * @return the paint (never <code>null</code>).
     */
    public Paint getSeriesOutlinePaint(int series);

    /**
     * Sets the paint used for a series outline and sends a {@link RendererChangeEvent} to
     * all registered listeners.
     *
     * @param series  the series index (zero-based).
     * @param paint  the paint (<code>null</code> permitted).
     */
    public void setSeriesOutlinePaint(int series, Paint paint);

    /**
     * Sets the outline paint for ALL series (optional).
     * 
     * @param paint  the paint (<code>null</code> permitted).
     */
    public void setOutlinePaint(Paint paint);
    
    /**
     * Returns the base outline paint.
     *
     * @return the paint (never <code>null</code>).
     */
    public Paint getBaseOutlinePaint();

    /**
     * Sets the base outline paint and sends a {@link RendererChangeEvent} to all 
     * registered listeners.
     *
     * @param paint  the paint (<code>null</code> not permitted).
     */
    public void setBaseOutlinePaint(Paint paint);

    // STROKE
    
    /**
     * Returns the stroke used to draw data items.
     *
     * @param row  the row (or series) index (zero-based).
     * @param column  the column (or category) index (zero-based).
     *
     * @return the stroke (never <code>null</code>).
     */
    public Stroke getItemStroke(int row, int column);

    /**
     * Returns the stroke used to draw the items in a series.
     *
     * @param series  the series (zero-based index).
     *
     * @return the stroke (never <code>null</code>).
     */
    public Stroke getSeriesStroke(int series);
    
    /**
     * Sets the stroke for ALL series and sends a {@link RendererChangeEvent} to all
     * registered listeners.
     * 
     * @param stroke  the stroke (<code>null</code> permitted).
     */
    public void setStroke(Stroke stroke);

    /**
     * Sets the stroke used for a series and sends a {@link RendererChangeEvent} to 
     * all registered listeners.
     *
     * @param series  the series index (zero-based).
     * @param stroke  the stroke (<code>null</code> permitted).
     */
    public void setSeriesStroke(int series, Stroke stroke);

    /**
     * Returns the base stroke.
     *
     * @return the base stroke (never <code>null</code>).
     */
    public Stroke getBaseStroke();

    /**
     * Sets the base stroke.
     *
     * @param stroke  the stroke (<code>null</code> not permitted).
     */
    public void setBaseStroke(Stroke stroke);
    
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
     * @return the stroke (never <code>null</code>).
     */
    public Stroke getItemOutlineStroke(int row, int column);

    /**
     * Returns the stroke used to outline the items in a series.
     *
     * @param series  the series (zero-based index).
     *
     * @return the stroke (never <code>null</code>).
     */
    public Stroke getSeriesOutlineStroke(int series);

    /**
     * Sets the outline stroke for ALL series and sends a {@link RendererChangeEvent} to
     * all registered listeners.
     *
     * @param stroke  the stroke (<code>null</code> permitted).
     */
    public void setOutlineStroke(Stroke stroke);
    
    /**
     * Sets the outline stroke used for a series and sends a {@link RendererChangeEvent}
     * to all registered listeners.
     *
     * @param series  the series index (zero-based).
     * @param stroke  the stroke (<code>null</code> permitted).
     */
    public void setSeriesOutlineStroke(int series, Stroke stroke);
    
    /**
     * Returns the base outline stroke.
     *
     * @return the stroke (never <code>null</code>).
     */
    public Stroke getBaseOutlineStroke();

    /**
     * Sets the base outline stroke and sends a {@link RendererChangeEvent} to all 
     * registered listeners.
     *
     * @param stroke  the stroke (<code>null</code> not permitted).
     */
    public void setBaseOutlineStroke(Stroke stroke);
    
    // SHAPE
    
    /**
     * Returns a shape used to represent a data item.
     *
     * @param row  the row (or series) index (zero-based).
     * @param column  the column (or category) index (zero-based).
     *
     * @return the shape (never <code>null</code>).
     */
    public Shape getItemShape(int row, int column);

    /**
     * Returns a shape used to represent the items in a series.
     *
     * @param series  the series (zero-based index).
     *
     * @return the shape (never <code>null</code>).
     */
    public Shape getSeriesShape(int series);
    /**
     * Sets the shape for ALL series (optional) and sends a {@link RendererChangeEvent} 
     * to all registered listeners.
     * 
     * @param shape  the shape (<code>null</code> permitted).
     */
    public void setShape(Shape shape);
    
    /**
     * Sets the shape used for a series and sends a {@link RendererChangeEvent} to all 
     * registered listeners.
     *
     * @param series  the series index (zero-based).
     * @param shape  the shape (<code>null</code> permitted).
     */
    public void setSeriesShape(int series, Shape shape);
    
    /**
     * Returns the base shape.
     *
     * @return the shape (never <code>null</code>).
     */
    public Shape getBaseShape();

    /**
     * Sets the base shape and sends a {@link RendererChangeEvent} to all 
     * registered listeners.
     *
     * @param shape  the shape (<code>null</code> not permitted).
     */
    public void setBaseShape(Shape shape);
    
    // ITEM LABELS VISIBLE 
    
    /**
     * Returns <code>true</code> if an item label is visible, and <code>false</code> otherwise.
     * 
     * @param row  the row index (zero-based).
     * @param column  the column index (zero-based).
     * 
     * @return a boolean.
     */
    public boolean isItemLabelVisible(int row, int column);
    
    /**
     * Returns <code>true</code> if the item labels for a series are visible, and 
     * <code>false</code> otherwise.
     * 
     * @param series  the series index (zero-based).
     * 
     * @return a boolean.
     */    
    public boolean isSeriesItemLabelsVisible(int series);
    
    /**
     * Sets a flag that controls whether or not the item labels for ALL series are visible.
     * 
     * @param visible  the flag.
     */
    public void setItemLabelsVisible(boolean visible);

    /**
     * Sets a flag that controls whether or not the item labels for ALL series are visible.
     * 
     * @param visible  the flag (<code>null</code> permitted).
     */
    public void setItemLabelsVisible(Boolean visible);

    /**
     * Sets the visibility of item labels for ALL series and, if requested, sends a 
     * {@link RendererChangeEvent} to all registered listeners.
     * 
     * @param visible  a flag that controls whether or not the item labels are visible 
     *                 (<code>null</code> permitted).
     * @param notify  a flag that controls whether or not listeners are notified.
     */
    public void setItemLabelsVisible(Boolean visible, boolean notify);

    /**
     * Sets a flag that controls the visibility of the item labels for a series.
     * 
     * @param series  the series index (zero-based).
     * @param visible  the flag.
     */
    public void setSeriesItemLabelsVisible(int series, boolean visible);
    
    /**
     * Sets a flag that controls the visibility of the item labels for a series.
     * 
     * @param series  the series index (zero-based).
     * @param visible  the flag (<code>null</code> permitted).
     */
    public void setSeriesItemLabelsVisible(int series, Boolean visible);
    
    /**
     * Sets the visibility of item labels for a series and, if requested, sends a 
     * {@link RendererChangeEvent} to all registered listeners.
     * 
     * @param series  the series index (zero-based).
     * @param visible  the visible flag.
     * @param notify  a flag that controls whether or not listeners are notified.
     */
    public void setSeriesItemLabelsVisible(int series, Boolean visible, boolean notify);
    
    /**
     * Returns the base setting for item label visibility.
     * 
     * @return A flag (possibly <code>null</code>).
     */
    public Boolean getBaseItemLabelsVisible();
    
    /**
     * Sets the base flag that controls whether or not item labels are visible.
     * 
     * @param visible  the flag.
     */
    public void setBaseItemLabelsVisible(boolean visible);
    
    /**
     * Sets the base setting for item label visibility.
     * 
     * @param visible  the flag (<code>null</code> permitted).
     */
    public void setBaseItemLabelsVisible(Boolean visible);
    
    /**
     * Sets the base visibility for item labels and, if requested, sends a 
     * {@link RendererChangeEvent} to all registered listeners.
     * 
     * @param visible  the visibility flag.
     * @param notify  a flag that controls whether or not listeners are notified.
     */
    public void setBaseItemLabelsVisible(Boolean visible, boolean notify);
    
    // LABEL GENERATOR
    
    /**
     * Returns the label generator for an item.
     *
     * @param series  the series index (zero-based).
     * @param item  the item index (zero-based).
     *
     * @return the item label generator.
     */
    public CategoryItemLabelGenerator getLabelGenerator(int series, int item);

    /**
     * Returns the item label generator for a series.
     *
     * @param series  the series index (zero-based).
     *
     * @return the label generator.
     */
    public CategoryItemLabelGenerator getSeriesLabelGenerator(int series);

    /**
     * Sets the item label generator for ALL series. 
     * 
     * @param generator  the generator.
     */
    public void setLabelGenerator(CategoryItemLabelGenerator generator);
    
    /**
     * Sets the item label generator for a series.
     *
     * @param series  the series index (zero-based).
     * @param generator  the generator.
     */
    public void setSeriesLabelGenerator(int series, CategoryItemLabelGenerator generator);

    /**
     * Returns the base item label generator.
     *
     * @return The base item label generator.
     */
    public CategoryItemLabelGenerator getBaseLabelGenerator();

    /**
     * Sets the base item label generator.
     *
     * @param generator  the base item label generator.
     */
    public void setBaseLabelGenerator(CategoryItemLabelGenerator generator);

    // TOOL TIP GENERATOR
    
    /**
     * Returns the tool tip generator for an item.
     *
     * @param series  the series index (zero-based).
     * @param item  the item index (zero-based).
     *
     * @return the generator.
     */
    public CategoryItemLabelGenerator getToolTipGenerator(int series, int item);

    /**
     * Returns the tool tip generator for a series.
     *
     * @param series  the series index (zero-based).
     *
     * @return the tool tip generator.
     */
    public CategoryItemLabelGenerator getSeriesToolTipGenerator(int series);

    /**
     * Sets the tool tip generator for ALL series. 
     * 
     * @param generator  the generator.
     */
    public void setToolTipGenerator(CategoryItemLabelGenerator generator);
    
    /**
     * Sets the tool tip generator for a series.
     *
     * @param series  the series index (zero-based).
     * @param generator  the generator.
     */
    public void setSeriesToolTipGenerator(int series, CategoryItemLabelGenerator generator);

    /**
     * Returns the base tool tip generator.
     *
     * @return the base tool tip generator.
     */
    public CategoryItemLabelGenerator getBaseToolTipGenerator();

    /**
     * Sets the base tool tip generator.
     *
     * @param generator  the generator.
     */
    public void setBaseToolTipGenerator(CategoryItemLabelGenerator generator);


    // ITEM LABEL FONT
    
    /**
     * Returns the font that should be used for a specific item label.
     *
     * @param series  the series index (zero-based).
     * @param item  the item index (zero-based).
     *
     * @return the item label font.
     */
    public Font getItemLabelFont(int series, int item);

    /**
     * Returns the item label font for a series.
     *
     * @param series  the series index (zero-based).
     *
     * @return the font.
     */
    public Font getSeriesItemLabelFont(int series);

    /**
     * Sets the item label font for ALL series and sends a {@link RendererChangeEvent} to all
     * registered listeners.  You can set this to <code>null</code> if you prefer to set the font
     * on a per series basis.
     * 
     * @param font  the font (<code>null</code> permitted).
     */
    public void setItemLabelFont(Font font);
    
    /**
     * Sets the item label font for a series and sends a {@link RendererChangeEvent} to all
     * registered listeners.  
     * 
     * @param series  the series index (zero-based).
     * @param font  the font (<code>null</code> permitted).
     */
    public void setSeriesItemLabelFont(int series, Font font);

    /**
     * Returns the base item label font.  This font is used as the fallback if the series font is
     * <code>null</code>
     * 
     * @return the font (<code>never</code> null).
     */
    public Font getBaseItemLabelFont();

    /**
     * Sets the base item label font and sends a {@link RendererChangeEvent} to all
     * registered listeners.  
     * 
     * @param font  the font (<code>null</code> not permitted).
     */
    public void setBaseItemLabelFont(Font font);
    
    // POSITIVE ITEM LABEL POSITION...

    /**
     * Returns the item label position for positive values.
     * 
     * @param row  the row index (zero-based).
     * @param column  the column index (zero-based).
     * 
     * @return the item label position (never <code>null</code>).
     */
    public ItemLabelPosition getPositiveItemLabelPosition(int row, int column);

    /**
     * Returns the item label position for positive values in ALL series.
     * 
     * @return the item label position (possibly <code>null</code>).
     */
    public ItemLabelPosition getPositiveItemLabelPosition();

    /**
     * Sets the item label position for positive values in ALL series, and sends a 
     * {@link RendererChangeEvent} to all registered listeners.  You need to set this to
     * <code>null</code> to expose the settings for individual series.
     * 
     * @param position  the position (<code>null</code> permitted).
     */
    public void setPositiveItemLabelPosition(ItemLabelPosition position);
    
    /**
     * Sets the positive item label position for ALL series and (if requested) sends a 
     * {@link RendererChangeEvent} to all registered listeners.
     * 
     * @param position  the position (<code>null</code> permitted).
     * @param notify  notify registered listeners?
     */
    public void setPositiveItemLabelPosition(ItemLabelPosition position, boolean notify);

    /**
     * Returns the item label position for all positive values in a series.
     * 
     * @param series  the series index (zero-based).
     * 
     * @return The item label position.
     */
    public ItemLabelPosition getSeriesPositiveItemLabelPosition(int series);
    
    /**
     * Sets the item label position for all positive values in a series and sends a 
     * {@link RendererChangeEvent} to all registered listeners.
     * 
     * @param series  the series index (zero-based).
     * @param position  the position (<code>null</code> permitted).
     */
    public void setSeriesPositiveItemLabelPosition(int series, ItemLabelPosition position);

    /**
     * Sets the item label position for all positive values in a series and (if requested) sends a 
     * {@link RendererChangeEvent} to all registered listeners.
     * 
     * @param series  the series index (zero-based).
     * @param position  the position (<code>null</code> permitted).
     * @param notify  notify registered listeners?
     */
    public void setSeriesPositiveItemLabelPosition(int series, ItemLabelPosition position, 
        boolean notify);

    /**
     * Returns the base positive item label position.
     * 
     * @return The position.
     */
    public ItemLabelPosition getBasePositiveItemLabelPosition();

    /**
     * Sets the base positive item label position.
     * 
     * @param position  the position.
     */
    public void setBasePositiveItemLabelPosition(ItemLabelPosition position);
    
    /**
     * Sets the base positive item label position and, if requested, sends a 
     * {@link RendererChangeEvent} to all registered listeners.
     * 
     * @param position  the position.
     * @param notify  notify registered listeners?
     */
    public void setBasePositiveItemLabelPosition(ItemLabelPosition position, boolean notify);
    
    
    // NEGATIVE ITEM LABEL POSITION...

    /**
     * Returns the item label position for negative values.  This method can be overridden to 
     * provide customisation of the item label position for individual data items.
     * 
     * @param row  the row index (zero-based).
     * @param column  the column (zero-based).
     * 
     * @return the item label position.
     */
    public ItemLabelPosition getNegativeItemLabelPosition(int row, int column);

    /**
     * Returns the item label position for negative values in ALL series.
     * 
     * @return the item label position (possibly <code>null</code>).
     */
    public ItemLabelPosition getNegativeItemLabelPosition();

    /**
     * Sets the item label position for negative values in ALL series, and sends a 
     * {@link RendererChangeEvent} to all registered listeners.  You need to set this to
     * <code>null</code> to expose the settings for individual series.
     * 
     * @param position  the position (<code>null</code> permitted).
     */
    public void setNegativeItemLabelPosition(ItemLabelPosition position);
    
    /**
     * Sets the item label position for negative values in ALL series and (if requested) sends 
     * a {@link RendererChangeEvent} to all registered listeners.  
     * 
     * @param position  the position (<code>null</code> permitted).
     * @param notify  notify registered listeners?
     */
    public void setNegativeItemLabelPosition(ItemLabelPosition position, boolean notify);

    /**
     * Returns the item label position for all negative values in a series.
     * 
     * @param series  the series index (zero-based).
     * 
     * @return The item label position.
     */
    public ItemLabelPosition getSeriesNegativeItemLabelPosition(int series);

    /**
     * Sets the item label position for negative values in a series and sends a 
     * {@link RendererChangeEvent} to all registered listeners.
     * 
     * @param series  the series index (zero-based).
     * @param position  the position (<code>null</code> permitted).
     */
    public void setSeriesNegativeItemLabelPosition(int series, ItemLabelPosition position);

    /**
     * Sets the item label position for negative values in a series and (if requested) sends a.
     * {@link RendererChangeEvent} to all registered listeners.
     * 
     * @param series  the series index (zero-based).
     * @param position  the position (<code>null</code> permitted).
     * @param notify  notify registered listeners?
     */
    public void setSeriesNegativeItemLabelPosition(int series, ItemLabelPosition position, 
        boolean notify);

    /**
     * Returns the base item label position for negative values.
     * 
     * @return The position.
     */
    public ItemLabelPosition getBaseNegativeItemLabelPosition();

    /**
     * Sets the base item label position for negative values and sends a 
     * {@link RendererChangeEvent} to all registered listeners.
     * 
     * @param position  the position.
     */
    public void setBaseNegativeItemLabelPosition(ItemLabelPosition position);
    
    /**
     * Sets the base negative item label position and, if requested, sends a 
     * {@link RendererChangeEvent} to all registered listeners.
     * 
     * @param position  the position.
     * @param notify  notify registered listeners?
     */
    public void setBaseNegativeItemLabelPosition(ItemLabelPosition position, boolean notify);
    
    // ITEM URL GENERATOR
    
    /**
     * Returns the URL generator for an item.
     *
     * @param series  the series index (zero-based).
     * @param item  the item index (zero-based).
     *
     * @return The item URL generator.
     */
    public CategoryURLGenerator getItemURLGenerator(int series, int item);

    /**
     * Returns the item URL generator for a series.
     *
     * @param series  the series index (zero-based).
     *
     * @return The URL generator.
     */
    public CategoryURLGenerator getSeriesItemURLGenerator(int series);

    /**
     * Sets the item URL generator for ALL series. 
     * 
     * @param generator  the generator.
     */
    public void setItemURLGenerator(CategoryURLGenerator generator);
    
    /**
     * Sets the item URL generator for a series.
     *
     * @param series  the series index (zero-based).
     * @param generator  the generator.
     */
    public void setSeriesItemURLGenerator(int series, CategoryURLGenerator generator);

    /**
     * Returns the base item URL generator.
     *
     * @return The item URL generator.
     */
    public CategoryURLGenerator getBaseItemURLGenerator();

    /**
     * Sets the base item URL generator.
     *
     * @param generator  the item URL generator.
     */
    public void setBaseItemURLGenerator(CategoryURLGenerator generator);

    /**
     * Returns a legend item for a series.
     *
     * @param datasetIndex  the dataset index (zero-based).
     * @param series  the series (zero-based index).
     *
     * @return the legend item.
     */
    public LegendItem getLegendItem(int datasetIndex, int series);

    /**
     * Draws a background for the data area.
     *
     * @param g2  the graphics device.
     * @param plot  the plot.
     * @param dataArea  the data area.
     */
    public void drawBackground(Graphics2D g2,
                               CategoryPlot plot,
                               Rectangle2D dataArea);

    /**
     * Draws an outline for the data area.
     *
     * @param g2  the graphics device.
     * @param plot  the plot.
     * @param dataArea  the data area.
     */
    public void drawOutline(Graphics2D g2,
                            CategoryPlot plot,
                            Rectangle2D dataArea);

    /**
     * Draws a single data item.
     *
     * @param g2  the graphics device.
     * @param state  state information for one chart.
     * @param dataArea  the data plot area.
     * @param plot  the plot.
     * @param domainAxis  the domain axis.
     * @param rangeAxis  the range axis.
     * @param data  the data.
     * @param row  the row index (zero-based).
     * @param column  the column index (zero-based).
     */
    public void drawItem(Graphics2D g2,
                         CategoryItemRendererState state,
                         Rectangle2D dataArea,
                         CategoryPlot plot,
                         CategoryAxis domainAxis,
                         ValueAxis rangeAxis,
                         CategoryDataset data,
                         int row,
                         int column);

    /**
     * Draws a grid line against the domain axis.
     *
     * @param g2  the graphics device.
     * @param plot  the plot.
     * @param dataArea  the area for plotting data (not yet adjusted for any 3D effect).
     * @param value  the value.
     */
    public void drawDomainGridline(Graphics2D g2,
                                   CategoryPlot plot,
                                   Rectangle2D dataArea,
                                   double value);

    /**
     * Draws a grid line against the range axis.
     *
     * @param g2  the graphics device.
     * @param plot  the plot.
     * @param axis  the value axis.
     * @param dataArea  the area for plotting data (not yet adjusted for any 3D effect).
     * @param value  the value.
     */
    public void drawRangeGridline(Graphics2D g2,
                                  CategoryPlot plot,
                                  ValueAxis axis,
                                  Rectangle2D dataArea,
                                  double value);

    /**
     * Draws a line (or some other marker) to indicate a particular value on the range axis.
     *
     * @param g2  the graphics device.
     * @param plot  the plot.
     * @param axis  the value axis.
     * @param marker  the marker.
     * @param dataArea  the area for plotting data (not including 3D effect).
     */
    public void drawRangeMarker(Graphics2D g2,
                                CategoryPlot plot,
                                ValueAxis axis,
                                Marker marker,
                                Rectangle2D dataArea);
    
    ////////  DEPRECATED METHODS //////////////////////////////////////////////////////////////////

    // ITEM LABEL ANCHOR
    
    /**
     * Returns the item label anchor for an item.
     *
     * @param series  the series index (zero-based).
     * @param item  the item index (zero-based).
     *
     * @return The item label anchor.
     * 
     * @deprecated Use getPositiveItemLabelPosition() or getNegativeItemLabelPosition().
     */
    public ItemLabelAnchor getItemLabelAnchor(int series, int item);

    /**
     * Returns the item label anchor for a series.
     *
     * @param series  the series index (zero-based).
     *
     * @return The anchor.
     * 
     * @deprecated Use getSeriesPositiveItemLabelPosition() or getSeriesNegativeItemLabelPosition().
     */
    public ItemLabelAnchor getSeriesItemLabelAnchor(int series);

    /**
     * Sets the item label anchor for ALL series. 
     * 
     * @param anchor  the anchor.
     * 
     * @deprecated Use setPositiveItemLabelPosition() or setNegativeItemLabelPosition().
     */
    public void setItemLabelAnchor(ItemLabelAnchor anchor);
    
    /**
     * Sets the item label anchor for a series.
     *
     * @param series  the series index (zero-based).
     * @param anchor  the anchor.
     * 
     * @deprecated Use setSeriesPositiveItemLabelPosition() or setSeriesNegativeItemLabelPosition().
     */
    public void setSeriesItemLabelAnchor(int series, ItemLabelAnchor anchor);

    /**
     * Returns the base item label anchor.
     *
     * @return The item label anchor.
     * 
     * @deprecated Use getBasePositiveItemLabelPosition() or getBaseNegativeItemLabelPosition().
     */
    public ItemLabelAnchor getBaseItemLabelAnchor();

    /**
     * Sets the base item label anchor.
     *
     * @param anchor  the base item label anchor.
     * 
     * @deprecated Use setBasePositiveItemLabelPosition() or setBaseNegativeItemLabelPosition().
     */
    public void setBaseItemLabelAnchor(ItemLabelAnchor anchor);

    // ITEM LABEL TEXT ANCHOR
    
    /**
     * Returns the item label text anchor for an item.
     *
     * @param series  the series index (zero-based).
     * @param item  the item index (zero-based).
     *
     * @return The item label text anchor.
     * 
     * @deprecated Use getPositiveItemLabelPosition() or getNegativeItemLabelPosition().
     */
    public TextAnchor getItemLabelTextAnchor(int series, int item);

    /**
     * Returns the item label text anchor for a series.
     *
     * @param series  the series index (zero-based).
     *
     * @return The anchor.
     * 
     * @deprecated Use getSeriesPositiveItemLabelPosition() or getSeriesNegativeItemLabelPosition().
     */
    public TextAnchor getSeriesItemLabelTextAnchor(int series);

    /**
     * Sets the item label text anchor for ALL series. 
     * 
     * @param anchor  the anchor.
     * 
     * @deprecated Use setPositiveItemLabelPosition() or setNegativeItemLabelPosition().
     */
    public void setItemLabelTextAnchor(TextAnchor anchor);
    
    /**
     * Sets the item label text anchor for a series.
     *
     * @param series  the series index (zero-based).
     * @param anchor  the anchor.
     * 
     * @deprecated Use setSeriesPositiveItemLabelPosition() or setSeriesNegativeItemLabelPosition().
     */
    public void setSeriesItemLabelTextAnchor(int series, TextAnchor anchor);

    /**
     * Returns the base item label text anchor.
     *
     * @return The item label text anchor.
     * 
     * @deprecated Use setBasePositiveItemLabelPosition() or setBaseNegativeItemLabelPosition().
     */
    public TextAnchor getBaseItemLabelTextAnchor();

    /**
     * Sets the base item label text anchor.
     *
     * @param anchor  the item label text anchor.
     * 
     * @deprecated Use setBasePositiveItemLabelPosition() or setBaseNegativeItemLabelPosition().
     */
    public void setBaseItemLabelTextAnchor(TextAnchor anchor);

}
