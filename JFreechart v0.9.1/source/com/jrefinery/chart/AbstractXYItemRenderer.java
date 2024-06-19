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
 * ---------------------------
 * AbstractXYItemRenderer.java
 * ---------------------------
 * (C) Copyright 2002, by Simba Management Limited.
 *
 * Original Author:  David Gilbert (for Simba Management Limited);
 * Contributor(s):   -;
 *
 * $Id: AbstractXYItemRenderer.java,v 1.1 2007/10/10 19:02:26 vauchers Exp $
 *
 * Changes:
 * --------
 * 15-Mar-2002 : Version 1 (DG);
 * 09-Apr-2002 : Added a getToolTipGenerator() method reflecting the change in the XYItemRenderer
 *               interface (DG);
 */

package com.jrefinery.chart;

import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.beans.PropertyChangeSupport;
import java.beans.PropertyChangeListener;
import com.jrefinery.data.XYDataset;
import com.jrefinery.chart.tooltips.XYToolTipGenerator;

/**
 * A base class that can be used to create new XYItemRenderer implementations.
 */
public abstract class AbstractXYItemRenderer implements XYItemRenderer {

    /** An object (optional) that collects information about the chart structure. */
    protected ChartRenderingInfo info;

    /** The tool tip generator. */
    protected XYToolTipGenerator toolTipGenerator;

    /** Support class for the property change listener mechanism. */
    protected PropertyChangeSupport listeners;

    /**
     * Default constructor.
     */
    protected AbstractXYItemRenderer() {
        this(null);
    }

    /**
     * Creates a renderer with the specified tooltip generator.  Storage is allocation for property
     * change listeners.
     *
     * @param tooltipGenerator The tooltip generator (null permitted).
     */
    protected AbstractXYItemRenderer(XYToolTipGenerator toolTipGenerator) {

        this.toolTipGenerator = toolTipGenerator;
        this.listeners = new PropertyChangeSupport(this);

    }

    /**
     * Initialises the renderer.  This method will be called before the first item is rendered,
     * giving the renderer an opportunity to initialise any state information it wants to
     * maintain.  The renderer can do nothing if it chooses.
     *
     * @param g2 The graphics device.
     * @param dataArea The area inside the axes.
     * @param plot The plot.
     * @param data The data.
     * @param info An optional info collection object to return data back to the caller.
     */
    public void initialise(Graphics2D g2,
                           Rectangle2D dataArea,
                           XYPlot plot,
                           XYDataset data,
                           ChartRenderingInfo info) {

        this.info = info;

    }

    /**
     * Returns the tool tip generator.
     */
    public XYToolTipGenerator getToolTipGenerator() {
        return this.toolTipGenerator;
    }

    /**
     * Sets the tool tip generator.
     *
     * @param toolTipGenerator The tool tip generator.
     */
    public void setToolTipGenerator(XYToolTipGenerator toolTipGenerator) {
        this.toolTipGenerator = toolTipGenerator;
    }

    /**
     * Adds a property change listener to the renderer.
     *
     * @param listener The listener.
     */
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        listeners.addPropertyChangeListener(listener);
    }

    /**
     * Removes a property change listener from the renderer.
     *
     * @param listener The listener.
     */
    public void removePropertyChangeListener(PropertyChangeListener listener) {
        listeners.removePropertyChangeListener(listener);
    }

    /**
     * Notifies registered listeners that a property of the renderer has changed.
     *
     * @param propertyName The name of the property.
     * @param oldValue The old value.
     * @param newValue The new value.
     */
    protected void firePropertyChanged(String propertyName, Object oldValue, Object newValue) {
        listeners.firePropertyChange(propertyName, oldValue, newValue);
    }

}