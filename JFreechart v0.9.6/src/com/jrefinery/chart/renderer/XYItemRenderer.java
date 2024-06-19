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
 * -------------------
 * XYItemRenderer.java
 * -------------------
 * (C) Copyright 2001-2003, by Simba Management Limited and Contributors.
 *
 * Original Author:  David Gilbert (for Simba Management Limited);
 * Contributor(s):   Mark Watson (www.markwatson.com);
 *                   Sylvain Vieujot;
 *
 * $Id: XYItemRenderer.java,v 1.1 2007/10/10 19:57:50 vauchers Exp $
 *
 * Changes
 * -------
 * 19-Oct-2001 : Version 1, based on code by Mark Watson (DG);
 * 22-Oct-2001 : Renamed DataSource.java --> Dataset.java etc. (DG);
 * 13-Dec-2001 : Changed return type of drawItem from void --> Shape.  The area returned can
 *               be used as the tooltip region.
 * 23-Jan-2002 : Added DrawInfo parameter to drawItem(...) method (DG);
 * 28-Mar-2002 : Added a property change listener mechanism.  Now renderers do not have to be
 *               immutable (DG);
 * 04-Apr-2002 : Added the initialise(...) method (DG);
 * 09-Apr-2002 : Removed the translated zero from the drawItem method, it can be calculated inside
 *               the initialise method if it is required.  Added a new getToolTipGenerator()
 *               method.  Changed the return type for drawItem() to void (DG);
 * 24-May-2002 : Added ChartRenderingInfo the initialise method API (DG);
 * 25-Jun-2002 : Removed redundant import (DG);
 * 20-Aug-2002 : Added get/setURLGenerator methods to interface (DG);
 * 02-Oct-2002 : Fixed errors reported by Checkstyle (DG);
 * 18-Nov-2002 : Added methods for drawing grid lines (DG);
 * 17-Jan-2003 : Moved plot classes into a separate package (DG);
 * 27-Jan-2003 : Added shape lookup table (DG);
 *
 */

package com.jrefinery.chart.renderer;

import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.Rectangle2D;
import java.beans.PropertyChangeListener;
import com.jrefinery.chart.ChartRenderingInfo;
import com.jrefinery.chart.Marker;
import com.jrefinery.chart.CrosshairInfo;
import com.jrefinery.chart.LegendItem;
import com.jrefinery.chart.axis.ValueAxis;
import com.jrefinery.chart.plot.Plot;
import com.jrefinery.chart.plot.XYPlot;
import com.jrefinery.chart.tooltips.XYToolTipGenerator;
import com.jrefinery.chart.urls.XYURLGenerator;
import com.jrefinery.data.XYDataset;

/**
 * Interface for rendering the visual representation of a single (x, y) item on an 
 * {@link XYPlot}.
 *
 * @author David Gilbert
 */
public interface XYItemRenderer extends Renderer {

    /**
     * Initialises the renderer.  This method will be called before the first
     * item is rendered, giving the renderer an opportunity to initialise any
     * state information it wants to maintain.  The renderer can do nothing if
     * it chooses.
     *
     * @param g2  the graphics device.
     * @param dataArea  the area inside the axes.
     * @param plot  the plot.
     * @param data  the data.
     * @param info  an optional info collection object to return data back
     *              to the caller.
     */
    public void initialise(Graphics2D g2,
                           Rectangle2D dataArea,
                           XYPlot plot,
                           XYDataset data,
                           ChartRenderingInfo info);

    /**
     * Sets the drawing supplier.
     * 
     * @param supplier  the supplier.
     */
    public void setDrawingSupplier(DrawingSupplier supplier);
    
    /**
     * Returns the tool tip generator for the renderer (possibly null).
     *
     * @return the tool tip generator.
     */
    public XYToolTipGenerator getToolTipGenerator();

    /**
     * Sets the tool tip generator for the renderer.
     *
     * @param toolTipGenerator  the tool tip generator (null permitted).
     */
    public void setToolTipGenerator(XYToolTipGenerator toolTipGenerator);

    /**
     * Returns the URL generator for HTML image maps.
     *
     * @return the URL generator (possibly null).
     */
    public XYURLGenerator getURLGenerator();

    /**
     * Sets the URL generator for HTML image maps.
     *
     * @param urlGenerator the URL generator (null permitted).
     */
    public void setURLGenerator(XYURLGenerator urlGenerator);

    /**
     * Adds a property change listener to the renderer.
     *
     * @param listener  the listener.
     */
    public void addPropertyChangeListener(PropertyChangeListener listener);

    /**
     * Removes a property change listener from the renderer.
     *
     * @param listener  the listener.
     */
    public void removePropertyChangeListener(PropertyChangeListener listener);
    
    
    /**
     * Returns the paint used to fill an item.
     * 
     * @param dataset  the dataset index (zero-based).
     * @param series  the series index (zero-based).
     * @param item  the item index (zero-based).
     * 
     * @return The paint.
     */
    public Paint getItemPaint(int dataset, int series, int item);
    
    /**
     * Returns the paint used to fill items in a series.
     * 
     * @param dataset  the dataset index (zero-based).
     * @param series  the series index (zero-based).
     * 
     * @return The paint.
     */
    public Paint getSeriesPaint(int dataset, int series);

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
     * Returns the paint used to outline an item.
     * 
     * @param dataset  the dataset index (zero-based).
     * @param series  the series index (zero-based).
     * @param item  the item index (zero-based).
     * 
     * @return The paint.
     */
    public Paint getItemOutlinePaint(int dataset, int series, int item);
    
    /**
     * Returns the paint used to outline items in a series.
     * 
     * @param dataset  the dataset index (zero-based).
     * @param series  the series index (zero-based).
     * 
     * @return The paint.
     */
    public Paint getSeriesOutlinePaint(int dataset, int series);
        
    /**
     * Returns the stroke used to draw an item.
     * 
     * @param dataset  the dataset index (zero-based).
     * @param series  the series index (zero-based).
     * @param item  the item index (zero-based).
     * 
     * @return The stroke.
     */
    public Stroke getItemStroke(int dataset, int series, int item);
    
    /**
     * Returns the stroke used to draw items in a series.
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
    
    /**
     * Called for each item to be plotted.
     *
     * @param g2  the graphics device.
     * @param dataArea  the area within which the data is being rendered.
     * @param info  collects drawing info.
     * @param plot  the plot (can be used to obtain standard color information etc).
     * @param domainAxis  the domain axis.
     * @param rangeAxis  the range axis.
     * @param dataset  the dataset.
     * @param datasetIndex  the dataset index (zero-based).
     * @param series  the series index (zero-based).
     * @param item  the item index (zero-based).
     * @param crosshairInfo  collects information about crosshairs.
     */
    public void drawItem(Graphics2D g2,
                         Rectangle2D dataArea,
                         ChartRenderingInfo info,
                         XYPlot plot,
                         ValueAxis domainAxis,
                         ValueAxis rangeAxis,
                         XYDataset dataset,
                         int datasetIndex,
                         int series,
                         int item,
                         CrosshairInfo crosshairInfo);

    /**
     * Returns a legend item for a series from a dataset.
     *
     * @param datasetIndex  the dataset index.
     * @param series  the series (zero-based index).
     *
     * @return the legend item.
     */
    public LegendItem getLegendItem(int datasetIndex, int series);

    /**
     * Draws a grid line against the domain axis.
     *
     * @param g2  the graphics device.
     * @param plot  the plot.
     * @param axis  the value axis.
     * @param dataArea  the area for plotting data (not yet adjusted for any 3D effect).
     * @param value  the value.
     */
    public void drawDomainGridLine(Graphics2D g2,
                                   XYPlot plot,
                                   ValueAxis axis,
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
    public void drawRangeGridLine(Graphics2D g2,
                                  XYPlot plot,
                                  ValueAxis axis,
                                  Rectangle2D dataArea,
                                  double value);

    /**
     * Draws a vertical line on the chart to represent a 'range marker'.
     *
     * @param g2  the graphics device.
     * @param plot  the plot.
     * @param axis  the value axis.
     * @param marker  the marker line.
     * @param dataArea  the axis data area.
     */
    public void drawDomainMarker(Graphics2D g2,
                                 XYPlot plot,
                                 ValueAxis axis,
                                 Marker marker,
                                 Rectangle2D dataArea);

    /**
     * Draws a horizontal line across the chart to represent a 'range marker'.
     *
     * @param g2  the graphics device.
     * @param plot  the plot.
     * @param axis  the value axis.
     * @param marker  the marker line.
     * @param dataArea  the axis data area.
     */
    public void drawRangeMarker(Graphics2D g2,
                                XYPlot plot,
                                ValueAxis axis,
                                Marker marker,
                                Rectangle2D dataArea);

    /**
     * Returns the plot that this renderer has been assigned to.
     *
     * @return the plot.
     */
    public Plot getPlot();

    /**
     * Sets the plot that this renderer is assigned to.
     * <P>
     * This method will be called by the plot class...you do not need to call it yourself.
     *
     * @param plot  the plot.
     */
    public void setPlot(Plot plot);

}
