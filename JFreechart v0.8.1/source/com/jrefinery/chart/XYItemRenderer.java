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
 * $Id: XYItemRenderer.java,v 1.1 2007/10/10 19:02:36 vauchers Exp $
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
 *
 */

package com.jrefinery.chart;

import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;
import java.beans.PropertyChangeListener;
import com.jrefinery.data.XYDataset;

/**
 * Interface for rendering the visual representation of a single (x, y) item on an XYPlot.
 */
public interface XYItemRenderer {

    /**
     * Initialises the renderer.  This method will be called before the first item is rendered,
     * giving the renderer an opportunity to initialise any state information it wants to
     * maintain.  The renderer can do nothing if it chooses.
     *
     * @param g2 The graphics device.
     * @param dataArea The area inside the axes.
     * @param plot The plot.
     * @param data The data.
     */
    public void initialise(Graphics2D g2,
                           Rectangle2D dataArea,
                           XYPlot plot,
                           XYDataset data);
    /**
     * Called for each item to be plotted.
     *
     * @param g2 The graphics device.
     * @param dataArea The area within which the data is being rendered.
     * @param drawInfo Collects drawing info.
     * @param plot The plot (can be used to obtain standard color information etc).
     * @param domainAxis The domain axis.
     * @param rangeAxis The range axis.
     * @param data The dataset.
     * @param series The series index.
     * @param item The item index.
     * @param translatedRangeZero Zero on the range axis (supplied so that, if it is required, it
     *        doesn't have to be calculated repeatedly).
     * @return A shape representing the area used to visually represent the data item.
     */
    public Shape drawItem(Graphics2D g2, Rectangle2D dataArea, DrawInfo info,
                          XYPlot plot, ValueAxis domainAxis, ValueAxis rangeAxis,
                          XYDataset data, int series, int item,
                          double translatedRangeZero, CrosshairInfo crosshairInfo);

    /**
     * Adds a property change listener to the renderer.
     *
     * @param listener The listener.
     */
    public void addPropertyChangeListener(PropertyChangeListener listener);

    /**
     * Removes a property change listener from the renderer.
     *
     * @param listener The listener.
     */
    public void removePropertyChangeListener(PropertyChangeListener listener);

}