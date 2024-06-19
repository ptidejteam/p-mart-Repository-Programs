/* =======================================
 * JFreeChart : a Java Chart Class Library
 * =======================================
 *
 * Project Info:  http://www.jrefinery.com/jfreechart
 * Project Lead:  David Gilbert (david.gilbert@jrefinery.com);
 *
 * This file...
 * $Id: ChartChangeEvent.java,v 1.1 2007/10/10 18:53:20 vauchers Exp $
 *
 * Original Author:  David Gilbert;
 * Contributor(s):   -;
 *
 * (C) Copyright 2000, 2001, Simba Management Limited;
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
 * Changes (from 24-Aug-2001)
 * --------------------------
 * 24-Aug-2001 : Added standard source header. Fixed DOS encoding problem (DG);
 * 07-Nov-2001 : Updated e-mail address in header (DG);
 *               Change event type names (DG);
 *
 */

package com.jrefinery.chart.event;

import com.jrefinery.chart.*;

/**
 * A change event that encapsulates information about a change to a chart.
 */
public class ChartChangeEvent extends java.util.EventObject {

    /**
     * Event type indicating a general change to a chart (typically just requires a redraw).
     */
    public static final int GENERAL = 1;

    /**
     * Event type indicating that the chart has a new dataset.
     */
    public static final int NEW_DATASET = 2;

    /**
     * Event type indicating that the chart's data source has been modified.
     */
    public static final int UPDATED_DATASET = 3;

    /**
     * The type of event (GENERAL or DATA_SOURCE_REPLACED).
     */
    protected int type;

    /**
     * The chart that generated the event.
     */
    protected JFreeChart chart;

    /**
     * Standard constructor: returns a new ChartChangeEvent object, type GENERAL.
     * @param source The source of the event (could be the chart, a title, an axis etc.)
     */
    public ChartChangeEvent(Object source) {
        this(source, null, GENERAL);
    }

    /**
     * Standard constructor: returns a new ChartChangeEvent object, type GENERAL.
     * @param source The source of the event (could be the chart, a title, an axis etc.);
     * @param chart The chart that generated the event;
     */
    public ChartChangeEvent(Object source, JFreeChart chart) {
        this(source, chart, GENERAL);
    }

    /**
     * Full constructor: returns a new ChartChangeEvent object with the specified type.
     */
    public ChartChangeEvent(Object source, JFreeChart chart, int type) {
        super(source);
        this.chart = chart;
        this.type = type;
    }

    /**
     * Returns a reference to the chart that generated the change event.
     */
    public JFreeChart getChart() {
        return chart;
    }

    /**
     * Sets the chart that generated the change event.
     * @param chart The chart that generated the event.
     */
    public void setChart(JFreeChart chart) {
        this.chart = chart;
    }

    /**
     * Returns the event type.
     */
    public int getType() {
        return this.type;
    }

    /**
     * Sets the event type.
     * @param type The event type (GENERAL or DATA_SOURCE_REPLACED);
     */
    public void setType(int type) {
        this.type = type;
    }

}
