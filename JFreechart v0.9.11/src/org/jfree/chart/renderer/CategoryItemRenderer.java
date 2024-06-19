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
 * -------------------------
 * CategoryItemRenderer.java
 * -------------------------
 *
 * (C) Copyright 2001-2003, by Object Refinery Limited and Contributors.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited);
 * Contributor(s):   Mark Watson (www.markwatson.com);
 *
 * $Id: CategoryItemRenderer.java,v 1.1 2007/10/10 19:09:11 vauchers Exp $
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
 *
 */

package org.jfree.chart.renderer;

import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.Rectangle2D;

import org.jfree.chart.ChartRenderingInfo;
import org.jfree.chart.LegendItem;
import org.jfree.chart.Marker;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.labels.CategoryItemLabelGenerator;
import org.jfree.chart.plot.CategoryPlot;
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
     * @return a flag indicating whether or not the data values are stacked.
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
     * @param info  collects chart rendering information for return to caller.
     */
    public void initialise(Graphics2D g2,
                           Rectangle2D dataArea,
                           CategoryPlot plot,
                           ChartRenderingInfo info);

    /**
     * Returns the base paint.
     *
     * @return The base paint.
     */
    public Paint getBasePaint();

    /**
     * Sets the base paint.
     * <p>
     * In most cases, the renderer's paint table will be active and so this default value will
     * not be used.
     *
     * @param paint  the paint.
     */
    public void setBasePaint(Paint paint);

    /**
     * Returns the paint used to fill an item.
     *
     * @param series  the series index (zero-based).
     * @param category  the category index (zero-based).
     *
     * @return The paint.
     */
    public Paint getItemPaint(int series, int category);

    /**
     * Returns the paint used to fill shapes for this renderer.
     *
     * @param series  the series (zero-based index).
     *
     * @return the paint.
     */
    public Paint getSeriesPaint(int series);

    /**
     * Sets the paint for a series in the primary dataset.
     *
     * @param series  the series index (zero-based).
     * @param paint  the paint.
     */
    public void setSeriesPaint(int series, Paint paint);

    /**
     * Returns the base outline paint.
     *
     * @return The base outline paint.
     */
    public Paint getBaseOutlinePaint();

    /**
     * Sets the base outline paint.
     *
     * @param paint  the paint.
     */
    public void setBaseOutlinePaint(Paint paint);

    /**
     * Returns the outline paint for an item.
     *
     * @param series  the series index (zero-based).
     * @param item  the item index (zero-based).
     *
     * @return The paint.
     */
    public Paint getItemOutlinePaint(int series, int item);

    /**
     * Returns the outline paint for a series.
     *
     * @param series  the series index (zero-based).
     *
     * @return The paint.
     */
    public Paint getSeriesOutlinePaint(int series);

    /**
     * Sets the outline paint for a series in the primary dataset.
     *
     * @param series  the series index (zero-based).
     * @param paint  the paint.
     */
    public void setSeriesOutlinePaint(int series, Paint paint);

    // STROKE 
    
    /**
     * Returns the stroke for an item.
     *
     * @param series  the series index (zero-based).
     * @param item  the item index (zero-based).
     *
     * @return The stroke.
     */
    public Stroke getItemStroke(int series, int item);

    /**
     * Returns the stroke for a series.
     *
     * @param series  the series index (zero-based).
     *
     * @return The stroke.
     */
    public Stroke getSeriesStroke(int series);

    /**
     * Sets the stroke for ALL series (optional).
     *
     * @param stroke  the stroke.
     */
    public void setStroke(Stroke stroke);
    
    /**
     * Sets the stroke for a series in the primary dataset.
     *
     * @param series  the series index (zero-based).
     * @param stroke  the stroke.
     */
    public void setSeriesStroke(int series, Stroke stroke);

    /**
     * Returns the base stroke.
     *
     * @return The stroke.
     */
    public Stroke getBaseStroke();

    /**
     * Sets the base stroke.
     *
     * @param stroke  the stroke.
     */
    public void setBaseStroke(Stroke stroke);

    // OUTLINE STROKE
    
    /**
     * Returns the outline stroke for an item.
     *
     * @param series  the series index (zero-based).
     * @param item  the item index (zero-based).
     *
     * @return The stroke.
     */
    public Stroke getItemOutlineStroke(int series, int item);

    /**
     * Returns the outline stroke for a series.
     *
     * @param series  the series index (zero-based).
     *
     * @return The stroke.
     */
    public Stroke getSeriesOutlineStroke(int series);

    /**
     * Sets the outline stroke for ALL series. 
     * 
     * @param stroke  the stroke.
     */
    public void setOutlineStroke(Stroke stroke);
    
    /**
     * Sets the outline stroke for a series in the primary dataset.
     *
     * @param series  the series index (zero-based).
     * @param stroke  the stroke.
     */
    public void setSeriesOutlineStroke(int series, Stroke stroke);

    /**
     * Returns the base outline stroke.
     *
     * @return The outline stroke.
     */
    public Stroke getBaseOutlineStroke();

    /**
     * Sets the base outline stroke.
     *
     * @param stroke  the outline stroke.
     */
    public void setBaseOutlineStroke(Stroke stroke);

    // SHAPE
    
    /**
     * Returns the shape for an item.
     *
     * @param series  the series index (zero-based).
     * @param item  the item index (zero-based).
     *
     * @return The shape.
     */
    public Shape getItemShape(int series, int item);

    /**
     * Returns the shape for a series.
     *
     * @param series  the series index (zero-based).
     *
     * @return The shape.
     */
    public Shape getSeriesShape(int series);

    /**
     * Sets the shape for a series in the primary dataset.
     *
     * @param series  the series index (zero-based).
     * @param shape  the shape.
     */
    public void setSeriesShape(int series, Shape shape);

    /**
     * Returns the base shape.
     *
     * @return The shape.
     */
    public Shape getBaseShape();

    /**
     * Sets the base shape.
     *
     * @param shape  the shape.
     */
    public void setBaseShape(Shape shape);

    // ITEM LABEL VISIBLE 
    
    /**
     * Sets a flag that controls whether or not the item labels for ALL series are visible.
     * 
     * @param visible  the flag (<code>null</code> permitted).
     */
    public void setItemLabelsVisible(Boolean visible);

    /**
     * Sets a flag that controls whether or not the item labels for ALL series are visible.
     * 
     * @param visible  the flag (<code>null</code> permitted).
     */
    public void setItemLabelsVisible(boolean visible);

    // ITEM LABEL GENERATOR
    
    /**
     * Returns the item label generator for an item.
     *
     * @param series  the series index (zero-based).
     * @param item  the item index (zero-based).
     *
     * @return The item label generator.
     */
    public CategoryItemLabelGenerator getItemLabelGenerator(int series, int item);

    /**
     * Returns the item label generator for a series.
     *
     * @param series  the series index (zero-based).
     *
     * @return The label generator.
     */
    public CategoryItemLabelGenerator getSeriesItemLabelGenerator(int series);

    /**
     * Sets the item label generator for ALL series. 
     * 
     * @param generator  the generator.
     */
    public void setItemLabelGenerator(CategoryItemLabelGenerator generator);
    
    /**
     * Sets the item label generator for a series.
     *
     * @param series  the series index (zero-based).
     * @param generator  the generator.
     */
    public void setSeriesItemLabelGenerator(int series, CategoryItemLabelGenerator generator);

    /**
     * Returns the base item label generator.
     *
     * @return The base item label generator.
     */
    public CategoryItemLabelGenerator getBaseItemLabelGenerator();

    /**
     * Sets the base item label generator.
     *
     * @param generator  the base item label generator.
     */
    public void setBaseItemLabelGenerator(CategoryItemLabelGenerator generator);

    // ITEM LABEL ANCHOR
    
    /**
     * Returns the item label anchor for an item.
     *
     * @param series  the series index (zero-based).
     * @param item  the item index (zero-based).
     *
     * @return The item label anchor.
     */
    public ItemLabelAnchor getItemLabelAnchor(int series, int item);

    /**
     * Returns the item label anchor for a series.
     *
     * @param series  the series index (zero-based).
     *
     * @return The anchor.
     */
    public ItemLabelAnchor getSeriesItemLabelAnchor(int series);

    /**
     * Sets the item label anchor for ALL series. 
     * 
     * @param anchor  the anchor.
     */
    public void setItemLabelAnchor(ItemLabelAnchor anchor);
    
    /**
     * Sets the item label anchor for a series.
     *
     * @param series  the series index (zero-based).
     * @param anchor  the anchor.
     */
    public void setSeriesItemLabelAnchor(int series, ItemLabelAnchor anchor);

    /**
     * Returns the base item label anchor.
     *
     * @return The item label anchor.
     */
    public ItemLabelAnchor getBaseItemLabelAnchor();

    /**
     * Sets the base item label anchor.
     *
     * @param anchor  the base item label anchor.
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
     */
    public TextAnchor getItemLabelTextAnchor(int series, int item);

    /**
     * Returns the item label text anchor for a series.
     *
     * @param series  the series index (zero-based).
     *
     * @return The anchor.
     */
    public TextAnchor getSeriesItemLabelTextAnchor(int series);

    /**
     * Sets the item label text anchor for ALL series. 
     * 
     * @param anchor  the anchor.
     */
    public void setItemLabelTextAnchor(TextAnchor anchor);
    
    /**
     * Sets the item label text anchor for a series.
     *
     * @param series  the series index (zero-based).
     * @param anchor  the anchor.
     */
    public void setSeriesItemLabelTextAnchor(int series, TextAnchor anchor);

    /**
     * Returns the base item label text anchor.
     *
     * @return The item label text anchor.
     */
    public TextAnchor getBaseItemLabelTextAnchor();

    /**
     * Sets the base item label text anchor.
     *
     * @param anchor  the item label text anchor.
     */
    public void setBaseItemLabelTextAnchor(TextAnchor anchor);

    // ITEM LABEL FONT
    
    /**
     * Returns the item label font for an item.
     *
     * @param series  the series index (zero-based).
     * @param item  the item index (zero-based).
     *
     * @return The item label font.
     */
    public Font getItemLabelFont(int series, int item);

    /**
     * Returns the item label font for a series.
     *
     * @param series  the series index (zero-based).
     *
     * @return The font.
     */
    public Font getSeriesItemLabelFont(int series);

    /**
     * Sets the item label font for ALL series. 
     * 
     * @param font  the font.
     */
    public void setItemLabelFont(Font font);
    
    /**
     * Sets the item label font for a series.
     *
     * @param series  the series index (zero-based).
     * @param font  the font.
     */
    public void setSeriesItemLabelFont(int series, Font font);

    /**
     * Returns the base item label font.
     *
     * @return The item label font.
     */
    public Font getBaseItemLabelFont();

    /**
     * Sets the base item label font.
     *
     * @param font  the item label font.
     */
    public void setBaseItemLabelFont(Font font);

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
     * @param dataArea  the data plot area.
     * @param plot  the plot.
     * @param domainAxis  the domain axis.
     * @param rangeAxis  the range axis.
     * @param data  the data.
     * @param row  the row index (zero-based).
     * @param column  the column index (zero-based).
     */
    public void drawItem(Graphics2D g2,
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

}
