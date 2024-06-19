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
 * -----------
 * Legend.java
 * -----------
 * (C) Copyright 2000-2002, by Simba Management Limited and Contributors.
 *
 * Original Author:  David Gilbert (for Simba Management Limited);
 * Contributor(s):   Andrzej Porebski;
 *
 * $Id: Legend.java,v 1.1 2007/10/10 19:02:36 vauchers Exp $
 *
 * Changes (from 20-Jun-2001)
 * --------------------------
 * 20-Jun-2001 : Modifications submitted by Andrzej Porebski for legend placement;
 * 18-Sep-2001 : Updated header and fixed DOS encoding problem (DG);
 * 07-Nov-2001 : Tidied up Javadoc comments (DG);
 * 06-Mar-2002 : Updated import statements (DG);
 *
 */

package com.jrefinery.chart;

import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.util.List;
import java.util.ArrayList;
import com.jrefinery.chart.event.LegendChangeEvent;
import com.jrefinery.chart.event.LegendChangeListener;

/**
 * A chart legend shows the names and visual representations of the series that are plotted in a
 * chart.
 * @see StandardLegend
 */
public abstract class Legend {

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
    protected int _anchor = SOUTH;

    /** A reference to the chart that the legend belongs to (used for access to the dataset). */
    protected JFreeChart chart;

    /** The amount of blank space around the legend. */
    protected int outerGap;

    /** Storage for registered change listeners. */
    protected List listeners;

    /**
     * Static factory method that returns a concrete subclass of Legend.
     * @param chart The chart that the legend belongs to.
     */
    public static Legend createInstance(JFreeChart chart) {
	return new StandardLegend(chart);
    }

    /**
     * Default constructor: returns a new legend.
     * @param chart The chart that the legend belongs to.
     * @param outerGap The blank space around the legend.
     */
    public Legend(JFreeChart chart, int outerGap) {
	this.chart = chart;
	this.outerGap = outerGap;
	this.listeners = new ArrayList();
    }

    /**
     * Draws the legend on a Java 2D graphics device (such as the screen or a printer).
     * @param g2 The graphics device.
     * @param drawArea The area within which the legend should be drawn.
     * @return The area remaining after the legend has drawn itself.
     */
    public abstract Rectangle2D draw(Graphics2D g2, Rectangle2D nonTitleArea);

    /**
     * Registers an object for notification of changes to the legend.
     * @param listener The object that is being registered.
     */
    public void addChangeListener(LegendChangeListener listener) {
	listeners.add(listener);
    }

    /**
     * Deregisters an object for notification of changes to the legend.
     * @param listener The object that is being deregistered.
     */
    public void removeChangeListener(LegendChangeListener listener) {
	listeners.remove(listener);
    }

    /**
     * Notifies all registered listeners that the chart legend has changed in some way.
     * @param event An object that contains information about the change to the legend.
     */
    protected void notifyListeners(LegendChangeEvent event) {
	java.util.Iterator iterator = listeners.iterator();
	while (iterator.hasNext()) {
	    LegendChangeListener listener = (LegendChangeListener)iterator.next();
	    listener.legendChanged(event);
	}
    }

    /**
     * Returns the current anchor of this legend.
     * <P>
     * The default anchor for this legend is SOUTH.
     * @return current anchor value
     */
    public int getAnchor() {
        return _anchor;
    }

    /**
     * Sets the current anchor of this legend.
     * <P>
     * The anchor can be one of: NORTH, SOUTH, EAST, WEST.  If a valid anchor value is provided,
     * the current anchor is set and an update event is triggered. Otherwise, no change is made.
     * @param anchor new anchor value
     */
    public void setAnchor(int anchor) {
         switch(anchor) {
            case NORTH:
            case SOUTH:
            case WEST:
            case EAST:
                _anchor = anchor;
                notifyListeners(new LegendChangeEvent(this));
                break;
            default:
        }
    }

}
