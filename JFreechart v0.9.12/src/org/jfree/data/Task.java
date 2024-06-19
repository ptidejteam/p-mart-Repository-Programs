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
 * You should have received a copy of the GNU Lesser General Public License along with this
 * library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330,
 * Boston, MA 02111-1307, USA.
 *
 * ---------
 * Task.java
 * ---------
 * (C) Copyright 2003 by Object Refinery Limited.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited);
 * Contributor(s):   -;
 *
 * $Id: Task.java,v 1.1 2007/10/10 19:12:18 vauchers Exp $
 *
 * Changes
 * -------
 * 10-Jan-2003 : Version 1 (DG);
 *
 */

package org.jfree.data;

import java.util.List;

import org.jfree.data.time.TimePeriod;

/**
 * A simple representation of a task.  The task has a description and a duration.  You can add
 * sub-tasks to the task.
 *
 * @author David Gilbert
 */
public class Task {

    /** The task description. */
    private String description;

    /** The time period for the task (estimated or actual). */
    private TimePeriod duration;

    /** Storage for the sub-tasks (if any). */
    private List subtasks;

    /**
     * Creates a new task.
     *
     * @param description  the task description.
     * @param duration  the task duration.
     */
    public Task(String description, TimePeriod duration) {
        this.description = description;
        this.duration = duration;
        this.subtasks = new java.util.ArrayList();
    }

    /**
     * Returns the task description.
     *
     * @return The task description.
     */
    public String getDescription() {
        return this.description;
    }

    /**
     * Sets the task description.
     *
     * @param description  the new description.
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Returns the duration (actual or estimated) of the task.
     *
     * @return The task duration.
     */
    public TimePeriod getDuration() {
        return this.duration;
    }

    /**
     * Sets the task duration (actual or estimated).
     *
     * @param duration  the duration.
     */
    public void setDuration(TimePeriod duration) {
        this.duration = duration;
    }

    /**
     * Adds a sub-task to the task.
     *
     * @param subtask  the subtask.
     */
    public void addSubtask(Task subtask) {
        this.subtasks.add(subtask);
    }

    /**
     * Removes a sub-task from the task.
     *
     * @param subtask  the subtask.
     */
    public void removeSubtask(Task subtask) {
        this.subtasks.remove(subtask);
    }

    /**
     * Returns the sub-task count.
     *
     * @return The sub-task count.
     */
    public int getSubtaskCount() {
        return this.subtasks.size();
    }

    /**
     * Returns a sub-task.
     *
     * @param index  the index.
     *
     * @return The sub-task.
     */
    public Task getSubtask(int index) {
        return (Task) this.subtasks.get(index);
    }

}
