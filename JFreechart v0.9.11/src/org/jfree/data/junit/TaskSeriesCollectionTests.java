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
 * ------------------------------
 * TaskSeriesCollectionTests.java
 * ------------------------------
 * (C) Copyright 2003 by Object Refinery Limited and Contributors.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited);
 * Contributor(s):   -;
 *
 * $Id: TaskSeriesCollectionTests.java,v 1.1 2007/10/10 19:09:17 vauchers Exp $
 *
 * Changes
 * -------
 * 10-Apr-2003 : Version 1 (DG);
 *
 */

package org.jfree.data.junit;

import java.util.Date;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.jfree.data.Task;
import org.jfree.data.TaskSeries;
import org.jfree.data.TaskSeriesCollection;
import org.jfree.data.time.SimpleTimePeriod;

/**
 * Tests for the {@link TaskSeriesCollection} class.
 *
 * @author David Gilbert
 */
public class TaskSeriesCollectionTests extends TestCase {

    /**
     * Returns the tests as a test suite.
     *
     * @return the test suite.
     */
    public static Test suite() {
        return new TestSuite(TaskSeriesCollectionTests.class);
    }

    /**
     * Constructs a new set of tests.
     *
     * @param  name the name of the tests.
     */
    public TaskSeriesCollectionTests(String name) {
        super(name);
    }

    /**
     * A test for bug report 697153.
     */
    public void test697153() {

        TaskSeries s1 = new TaskSeries("S1");
        s1.add(new Task("Task 1", new SimpleTimePeriod(new Date(), new Date())));
        s1.add(new Task("Task 2", new SimpleTimePeriod(new Date(), new Date())));
        s1.add(new Task("Task 3", new SimpleTimePeriod(new Date(), new Date())));

        TaskSeries s2 = new TaskSeries("S2");
        s2.add(new Task("Task 2", new SimpleTimePeriod(new Date(), new Date())));
        s2.add(new Task("Task 3", new SimpleTimePeriod(new Date(), new Date())));
        s2.add(new Task("Task 4", new SimpleTimePeriod(new Date(), new Date())));

        TaskSeriesCollection tsc = new TaskSeriesCollection();
        tsc.add(s1);
        tsc.add(s2);

        s1.removeAll();

        int taskCount = tsc.getColumnCount();

        assertEquals(3, taskCount);

    }

}
