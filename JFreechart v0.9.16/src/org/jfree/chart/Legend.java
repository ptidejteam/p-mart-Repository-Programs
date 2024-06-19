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
 * -----------
 * Legend.java
 * -----------
 * (C) Copyright 2000-2004, by Object Refinery Limited and Contributors.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited);
 * Contributor(s):   Andrzej Porebski;
 *                   Jim Moore;
 *                   Nicolas Brodu;
 *
 * $Id: Legend.java,v 1.1 2007/10/10 19:25:38 vauchers Exp $
 *
 * Changes (from 20-Jun-2001)
 * --------------------------
 * 20-Jun-2001 : Modifications submitted by Andrzej Porebski for legend placement;
 * 18-Sep-2001 : Updated header and fixed DOS encoding problem (DG);
 * 07-Nov-2001 : Tidied up Javadoc comments (DG);
 * 06-Mar-2002 : Updated import statements (DG);
 * 20-Jun-2002 : Added outlineKeyBoxes attribute suggested by Jim Moore (DG);
 * 14-Oct-2002 : Changed listener storage structure (DG);
 * 14-Jan-2003 : Changed constructor to protected, moved outer-gap to subclass (DG);
 * 27-Mar-2003 : Implemented Serializable (DG);
 * 05-Jun-2003 : Added ChartRenderingInfo parameter to draw(...) method (DG);
 * 11-Sep-2003 : Cloning support
 *
 */

package org.jfree.chart;

import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import javax.swing.event.EventListenerList;

import org.jfree.chart.event.LegendChangeEvent;
import org.jfree.chart.event.LegendChangeListener;

/**
 * A chart legend shows the names and visual representations of the series that
 * are plotted in a chart.
 *
 * @see StandardLegend
 *
 * @author David Gilbert
 */
public abstract class Legend implements Serializable, Cloneable {

    /** Constant anchor value for legend position WEST. */
    public static final int WEST = 0x00;

    /** Constant anchor value for legend position NORTH. */
    public static final int NORTH = 0x01;

    /** Constant anchor value for legend position EAST. */
    public static final int EAST = 0x02;

    /** Constant anchor value for legend position SOUTH. */
    public static final int SOUTH = 0x03;

    /** Internal value indicating the bit holding the value of interest in the anchor value. */
    protected static final int INVERTED = 1 << 1;

    /** Internal value indicating the bit holding the value of interest in the anchor value. */
    protected static final int HORIZONTAL = 1 << 0;

    /** The current location anchor of the legend. */
    private int anchor = SOUTH;

    /**
     * A reference to the chart that the legend belongs to
     * (used for access to the dataset).
     *  <!-- use registerChart() instead -->
     */
    private JFreeChart chart;

    /** Storage for registered change listeners. */
    private transient EventListenerList listenerList;

    /**
     * Static factory method that returns a concrete subclass of Legend.
     *
     * @param chart  the chart that the legend belongs to.
     *
     * @return a StandardLegend.
     */
    public static Legend createInstance(JFreeChart chart) {
        return new StandardLegend();
    }

    /**
     * Default constructor.
     */
    public Legend() {
        this.listenerList = new EventListenerList();
    }

    /**
     * Creates a new legend.
     *
     * @param chart  the chart that the legend belongs to.
     * @deprecated use the default constructor instead and let JFreeChart manage
     * the chart reference
     */
    protected Legend(JFreeChart chart) {
        this();
        this.chart = chart;
    }

    /**
     * Returns the chart that this legend belongs to.
     *
     * @return the chart.
     */
    public JFreeChart getChart() {
        return this.chart;
    }

    /**
     * Internal maintenace method to update the reference to the central
     * JFreeChart object.
     *
     * @param chart the chart, may be null, if the legend gets removed from
     * the chart.
     */
    protected void registerChart (JFreeChart chart)
    {
        this.chart = chart;
    }

    /**
     * Draws the legend on a Java 2D graphics device (such as the screen or a printer).
     *
     * @param g2  the graphics device.
     * @param available  the area within which the legend (and plot) should be drawn.
     * @param info  a carrier for returning information about the entities in the legend.
     *
     * @return the area remaining after the legend has drawn itself.
     */
    public abstract Rectangle2D draw(Graphics2D g2, Rectangle2D available, ChartRenderingInfo info);

    /**
     * Registers an object for notification of changes to the legend.
     *
     * @param listener  the object that is being registered.
     */
    public void addChangeListener(LegendChangeListener listener) {
        this.listenerList.add(LegendChangeListener.class, listener);
    }

    /**
     * Deregisters an object for notification of changes to the legend.
     *
     * @param listener  the object that is being deregistered.
     */
    public void removeChangeListener(LegendChangeListener listener) {
        this.listenerList.remove(LegendChangeListener.class, listener);
    }

    /**
     * Notifies all registered listeners that the chart legend has changed in some way.
     *
     * @param event  information about the change to the legend.
     */
    protected void notifyListeners(LegendChangeEvent event) {

        Object[] listeners = this.listenerList.getListenerList();
        for (int i = listeners.length - 2; i >= 0; i -= 2) {
            if (listeners[i] == LegendChangeListener.class) {
                ((LegendChangeListener) listeners[i + 1]).legendChanged(event);
            }
        }

    }

    /**
     * Returns the current anchor of this legend.
     * <p>
     * The default anchor for this legend is <code>SOUTH</code>.
     *
     * @return the current anchor.
     */
    public int getAnchor() {
        return this.anchor;
    }

    /**
     * Sets the current anchor of this legend.
     * <P>
     * The anchor can be one of: <code>NORTH</code>, <code>SOUTH</code>, <code>EAST</code>,
     * <code>WEST</code>.  If a valid anchor value is provided, the current anchor is set and an
     * update event is triggered. Otherwise, no change is made.
     *
     * @param anchor  the new anchor value.
     */
    public void setAnchor(int anchor) {
        switch (anchor) {
            case NORTH:
            case SOUTH:
            case WEST:
            case EAST:
                this.anchor = anchor;
                notifyListeners(new LegendChangeEvent(this));
                break;
            default:
        }
    }

    /**
     * Tests this legend for equality with another object.
     *
     * @param obj  the object.
     *
     * @return <code>true</code> or <code>false</code>.
     */
    public boolean equals(Object obj) {

        if (obj == null) {
            return false;
        }

        if (obj == this) {
            return true;
        }

        if (obj instanceof Legend) {
            Legend l = (Legend) obj;
            return (this.anchor == l.anchor);
        }

        return false;

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
    }

    /**
     * Provides serialization support.
     *
     * @param stream  the input stream.
     *
     * @throws IOException  if there is an I/O error.
     * @throws ClassNotFoundException  if there is a classpath problem.
     */
    private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
        stream.defaultReadObject();
        this.listenerList = new EventListenerList();  // todo: make sure this is populated.
    }

    /**
     * Clones the legend, and takes care of listeners.
     * Note: the cloned legend refer to the same chart as the original one.
     * JFreeChart clone() takes care of setting the references correctly.
     *
     * @return A clone.
     *
     * @throws CloneNotSupportedException if the object cannot be cloned.
     */
    protected Object clone() throws CloneNotSupportedException {
        Legend ret = (Legend) super.clone();
        this.listenerList = new EventListenerList();
        return ret;
    }

}
