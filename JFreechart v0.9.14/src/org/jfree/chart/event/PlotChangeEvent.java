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
 * You should have received a copy of the GNU Lesser General Public License along with this library;
 * if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston,
 * MA 02111-1307, USA.
 *
 * --------------------
 * PlotChangeEvent.java
 * --------------------
 * (C) Copyright 2000-2003, by Object Refinery Limited.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited);
 * Contributor(s):   -;
 *
 * $Id: PlotChangeEvent.java,v 1.1 2007/10/10 19:19:10 vauchers Exp $
 *
 * Changes (from 24-Aug-2001)
 * --------------------------
 * 24-Aug-2001 : Added standard source header. Fixed DOS encoding problem (DG);
 * 07-Nov-2001 : Updated header (DG);
 * 09-Oct-2002 : Fixed errors reported by Checkstyle (DG);
 * 17-Jan-2003 : Moved plot classes to a separate package (DG);
 *
 */

package org.jfree.chart.event;

import org.jfree.chart.plot.Plot;

/**
 * An event that can be forwarded to any {@link org.jfree.chart.event.PlotChangeListener} to
 * signal a change to a plot.
 *
 * @author David Gilbert
 */
public class PlotChangeEvent extends ChartChangeEvent {

    /** The plot that generated the event. */
    private Plot plot;

    /**
     * Creates a new PlotChangeEvent.
     *
     * @param plot  the plot that generated the event.
     */
    public PlotChangeEvent(Plot plot) {
        super(plot);
        this.plot = plot;
    }

    /**
     * Returns the plot that generated the event.
     *
     * @return the plot that generated the event.
     */
    public Plot getPlot() {
        return this.plot;
    }

}
