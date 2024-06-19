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
 * --------------------
 * ChartMouseEvent.java
 * --------------------
 * (C) Copyright 2002, by Simba Management Limited and Contributors.
 *
 * Original Author:  David Gilbert (for Simba Management Limited);
 * Contributor(s):   Alex Weber;
 *
 * $Id: ChartMouseEvent.java,v 1.1 2007/10/10 19:02:26 vauchers Exp $
 *
 * Changes
 * -------
 * 27-May-2002 : Version 1, incorporating code and ideas by Alex Weber (DG);
 * 13-Jun-2002 : Added Javadoc comments (DG);
 *
 */

package com.jrefinery.chart;

import java.awt.event.MouseEvent;
import com.jrefinery.chart.entity.ChartEntity;

/**
 * A mouse event for a chart that is displayed in a ChartPanel.
 */
public class ChartMouseEvent {

    /** The Java mouse event that triggered this event. */
    protected MouseEvent trigger;

    /** The chart entity (if any). */
    protected ChartEntity entity;

    /**
     * Constructs a new event.
     *
     * @param trigger The mouse event that triggered this event.
     * @param entity The chart entity (if any) under the mouse point.
     */
    public ChartMouseEvent(MouseEvent trigger, ChartEntity entity) {
        this.trigger = trigger;
        this.entity = entity;
    }

    /**
     * Returns the mouse event that triggered this event.
     *
     * @return The event.
     */
    public MouseEvent getTrigger() {
        return this.trigger;
    }

    /**
     * Returns the chart entity (if any) under the mouse point.
     *
     * @return The chart entity.
     */
    public ChartEntity getEntity() {
        return this.entity;
    }

}