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
 * ---------------
 * TaskSeries.java
 * ---------------
 * (C) Copyright 2002, 2003 by Simba Management Limited.
 *
 * Original Author:  David Gilbert (for Simba Management Limited);
 * Contributor(s):   -;
 *
 * $Id: TaskSeries.java,v 1.1 2007/10/10 20:00:04 vauchers Exp $
 *
 * Changes
 * -------
 * 06-Jun-2002 : Version 1 (DG);
 * 07-Oct-2002 : Fixed errors reported by Checkstyle (DG);
 * 24-Oct-2002 : Added methods to get TimeAllocation by task index (DG);
 * 10-Jan-2003 : Renamed GanttSeries --> TaskSeries (DG);
 *
 */

package com.jrefinery.data;

import java.util.Collections;
import java.util.List;

/**
 * A series that contains zero, one or many {@link Task} objects.
 * <P>
 * This class is used as a building block for the {@link TaskSeriesCollection} class that 
 * implements the {@link MultiIntervalCategoryDataset} that, in turn, can be used to construct 
 * basic Gantt charts.
 *
 * @author David Gilbert 
 */
public class TaskSeries extends Series {

    /** Storage for the tasks in the series. */
    private List tasks;
    
    /**
     * Constructs a new series with the specified name.
     *
     * @param name  the series name.
     */
    public TaskSeries(String name) {
        super(name);
        this.tasks = new java.util.ArrayList();
    }

    /**
     * Adds a task to the series.
     *
     * @param task  the task.
     */
    public void add(Task task) {
        tasks.add(task);
        fireSeriesChanged();
    }

    /**
     * Removes a task from the series.
     * 
     * @param task  the task.
     */
    public void remove(Task task) {
        tasks.remove(task);
        fireSeriesChanged();
    }
    
    public void removeAll() {
        this.tasks.clear();
        fireSeriesChanged();
    }
    
    /**
     * Returns the tasks.
     *
     * @return the tasks.
     */
    public List getTasks() {
        return Collections.unmodifiableList(this.tasks);
    }
    
    /**
     * Returns a task from the series.
     * 
     * @param index  the task index (zero-based).
     * 
     * @return the task.
     */
    public Task get(int index) {
        return (Task) tasks.get(index);
    }

    /**
     * Returns the number of items in the series.
     *
     * @return the item count.
     */
    public int getItemCount() {
        return tasks.size();
    }

}
