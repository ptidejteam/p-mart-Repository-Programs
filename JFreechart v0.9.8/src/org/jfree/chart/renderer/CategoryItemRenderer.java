/* ======================================
 * JFreeChart : a free Java chart library
 * ======================================
 *
 * Project Info:  http://www.jfree.org/jfreechart/index.html
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
 * -------------------------
 * CategoryItemRenderer.java
 * -------------------------
 *
 * (C) Copyright 2001-2003, by Simba Management Limited and Contributors.
 *
 * Original Author:  David Gilbert (for Simba Management Limited);
 * Contributor(s):   Mark Watson (www.markwatson.com);
 *
 * $Id: CategoryItemRenderer.java,v 1.1 2007/10/10 20:03:12 vauchers Exp $
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
 *
 */

package org.jfree.chart.renderer;

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
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.tooltips.CategoryToolTipGenerator;
import org.jfree.data.CategoryDataset;

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
public interface CategoryItemRenderer extends Renderer {

    /** A constant for the range type (STANDARD). */
    public static final int STANDARD = 0;

    /** A constant for the range type (STACKED). */
    public static final int STACKED = 1;

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
    public int getRangeType();

    /**
     * Returns the tool tip generator.
     *
     * @return the tool tip generator (possibly <code>null</code>).
     */
    public CategoryToolTipGenerator getToolTipGenerator();

    /**
     * Sets the tool tip generator.
     * <P>
     * Classes that implement this interface are not required to support tool tip generation, 
     * although it is recommended.  If the renderer does not support tooltips, this method should 
     * throw an <code>UnsupportedOperationException</code>.
     *
     * @param generator  the tool tip generator (<code>null</code> permitted).
     */
    public void setToolTipGenerator(CategoryToolTipGenerator generator);
    
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
     * Returns the paint used to fill shapes for this renderer.
     *
     * @param dataset  the dataset (zero-based index).
     * @param series  the series (zero-based index).
     *
     * @return the paint.
     */
    public Paint getSeriesPaint(int dataset, int series);

    /**
     * Returns the paint used to fill an item.
     * <p>
     * A plot can have more than one dataset, the dataset index indicates which one is being
     * used.
     * 
     * @param dataset  the dataset index (zero-based).
     * @param series  the series index (zero-based).
     * @param category  the category index (zero-based).
     * 
     * @return The paint.
     */
    public Paint getItemPaint(int dataset, int series, int category);
    
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
     * @param dataset  the dataset index (zero-based).
     * @param row  the row index (zero-based).
     * @param column  the column index (zero-based).
     */
    public void drawItem(Graphics2D g2,
                         Rectangle2D dataArea,
                         CategoryPlot plot,
                         CategoryAxis domainAxis,
                         ValueAxis rangeAxis,
                         CategoryDataset data,
                         int dataset,
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

    /**
     * Returns the outline paint for an item. 
     * 
     * @param dataset  the dataset index (zero-based).
     * @param series  the series index (zero-based).
     * @param item  the item index (zero-based).
     * 
     * @return The paint.
     */
    public Paint getItemOutlinePaint(int dataset, int series, int item);
    
    /**
     * Returns the outline paint for a series. 
     * 
     * @param dataset  the dataset index (zero-based).
     * @param series  the series index (zero-based).

     * 
     * @return The paint.
     */
    public Paint getSeriesOutlinePaint(int dataset, int series);
        
    /**
     * Returns the stroke for an item. 
     * 
     * @param dataset  the dataset index (zero-based).
     * @param series  the series index (zero-based).
     * @param item  the item index (zero-based).
     * 
     * @return The stroke.
     */
    public Stroke getItemStroke(int dataset, int series, int item);
    
    /**
     * Returns the stroke for a series. 
     * 
     * @param dataset  the dataset index (zero-based).
     * @param series  the series index (zero-based).

     * 
     * @return The stroke.
     */
    public Stroke getSeriesStroke(int dataset, int series);
    
    /**
     * Returns the shape for an item. 
     * 
     * @param dataset  the dataset index (zero-based).
     * @param series  the series index (zero-based).
     * @param item  the item index (zero-based).
     * 
     * @return The shape.
     */
    public Shape getItemShape(int dataset, int series, int item);
    
    /**
     * Returns the shape for a series. 
     * 
     * @param dataset  the dataset index (zero-based).
     * @param series  the series index (zero-based).

     * 
     * @return The shape.
     */
    public Shape getSeriesShape(int dataset, int series);
    
}
