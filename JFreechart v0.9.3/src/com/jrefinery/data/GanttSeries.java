/* ============================================
 * JFreeChart : a free Java chart class library
 * ============================================
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
 * ----------------
 * GanttSeries.java
 * -----------------
 * (C) Copyright 2002, by Simba Management Limited.
 *
 * Original Author:  David Gilbert (for Simba Management Limited);
 * Contributor(s):   -;
 *
 * $Id: GanttSeries.java,v 1.1 2007/10/10 19:52:14 vauchers Exp $
 *
 * Changes
 * -------
 * 06-Jun-2002 : Version 1 (DG);
 *
 */

package com.jrefinery.data;

import java.util.List;

/**
 * A very basic representation of a list of tasks and time allocations.
 * <P>
 * This class is used as a building block for the GanttSeriesCollection class
 * that implements the IntervalCategoryDataset that, in turn, can be used to
 * construct simple Gantt charts.
 * <P>
 * This class is incomplete.  It implements the bare minimum required to get
 * a simple demo running.
 *
 */
public class GanttSeries extends Series {

    /** A list of tasks. */
    protected List tasks;

    /** A list of time allocations corresponding to the above tasks. */
    protected List times;

    /**
     * Constructs a new series with the specified name.
     *
     * @param name      The series name.
     */
    public GanttSeries(String name) {
        super(name);
        tasks = new java.util.ArrayList();
        times = new java.util.ArrayList();
    }

    /**
     * Returns the number of items in the series.
     *
     * @return The item count.
     */
    public int getItemCount() {
        return tasks.size();
    }

    /**
     * Returns the time allocation for a task.
     *
     * @param task      The task.
     * @return the time allocation for a task.
     */
    public TimeAllocation getTimeAllocation(Object task) {
        int index = tasks.indexOf(task);
        return (TimeAllocation)times.get(index);
    }

    /**
     * Adds a time allocation for a task.
     * <P>
     * The task can be represented by an arbitrary Object.
     *
     * @param task      The task.
     * @param allocation    The time allocation.
     */
    public void add(Object task, TimeAllocation allocation) {
        tasks.add(task);
        times.add(allocation);
    }

}
