/* =======================================
 * JFreeChart : a Java Chart Class Library
 * =======================================
 *
 * Project Info:  http://www.object-refinery.com/jfreechart/index.html
 * Project Lead:  David Gilbert (david.gilbert@object-refinery.com);
 *
 * (C) Copyright 2000-2002, by Simba Management Limited and Contributors.
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
 * (C) Copyright 2001, 2002, by Simba Management Limited and Contributors.
 *
 * Original Author:  David Gilbert (for Simba Management Limited);
 * Contributor(s):   Mark Watson (www.markwatson.com);
 *                   Sylvain Vieujot;
 *
 * $Id: XYItemRenderer.java,v 1.1 2007/10/10 19:52:14 vauchers Exp $
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
 *
 */

package com.jrefinery.chart;

import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.beans.PropertyChangeListener;
import com.jrefinery.data.XYDataset;
import com.jrefinery.chart.tooltips.XYToolTipGenerator;
import com.jrefinery.chart.urls.XYURLGenerator;

/**
 * Interface for rendering the visual representation of a single (x, y) item on an XYPlot.
 */
public interface XYItemRenderer {

    /**
     * Initialises the renderer.  This method will be called before the first
     * item is rendered, giving the renderer an opportunity to initialise any
     * state information it wants to maintain.  The renderer can do nothing if
     * it chooses.
     *
     * @param g2  The graphics device.
     * @param dataArea  The area inside the axes.
     * @param plot  The plot.
     * @param data  The data.
     * @param info  An optional info collection object to return data back
     *              to the caller.
     */
    public void initialise(Graphics2D g2,
                           Rectangle2D dataArea,
                           XYPlot plot,
                           XYDataset data,
                           ChartRenderingInfo info);

    /**
     * Returns the tool tip generator for the renderer (possibly null).
     *
     * @return The tool tip generator.
     */
    public XYToolTipGenerator getToolTipGenerator();

    /**
     * Sets the tool tip generator for the renderer.
     *
     * @param toolTipGenerator  The tool tip generator (null permitted).
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
     * @param urlGenerator The URL generator (null permitted).
     */
    public void setURLGenerator(XYURLGenerator urlGenerator);

    /**
     * Adds a property change listener to the renderer.
     *
     * @param listener  The listener.
     */
    public void addPropertyChangeListener(PropertyChangeListener listener);

    /**
     * Removes a property change listener from the renderer.
     *
     * @param listener  The listener.
     */
    public void removePropertyChangeListener(PropertyChangeListener listener);

    /**
     * Called for each item to be plotted.
     *
     * @param g2  The graphics device.
     * @param dataArea  The area within which the data is being rendered.
     * @param info  Collects drawing info.
     * @param plot  The plot (can be used to obtain standard color information etc).
     * @param domainAxis  The domain axis.
     * @param rangeAxis  The range axis.
     * @param data  The dataset.
     * @param series  The series index.
     * @param item  The item index.
     * @param crosshairInfo  Collects information about crosshairs.
     */
    public void drawItem(Graphics2D g2,
                         Rectangle2D dataArea,
                         ChartRenderingInfo info,
                         XYPlot plot,
                         ValueAxis domainAxis,
                         ValueAxis rangeAxis,
                         XYDataset data,
                         int series,
                         int item,
                         CrosshairInfo crosshairInfo);

    /**
     * Draws a vertical line on the chart to represent a 'range marker'.
     *
     * @param g2  The graphics device.
     * @param plot  The plot.
     * @param axis  The value axis.
     * @param marker  The marker line.
     * @param dataArea  The axis data area.
     */
    public void drawDomainMarker(Graphics2D g2,
                                 XYPlot plot,
                                 ValueAxis axis,
                                 Marker marker,
                                 Rectangle2D dataArea);

    /**
     * Draws a horizontal line across the chart to represent a 'range marker'.
     *
     * @param g2  The graphics device.
     * @param plot  The plot.
     * @param axis  The value axis.
     * @param marker  The marker line.
     * @param dataArea  The axis data area.
     */
    public void drawRangeMarker(Graphics2D g2,
                                XYPlot plot,
                                ValueAxis axis,
                                Marker marker,
                                Rectangle2D dataArea);

}
