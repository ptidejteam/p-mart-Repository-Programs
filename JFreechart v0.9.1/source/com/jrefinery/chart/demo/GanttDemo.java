/* ===============
 * JFreeChart Demo
 * ===============
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
 * --------------
 * GanttDemo.java
 * --------------
 * (C) Copyright 2002, by Simba Management Limited and Contributors.
 *
 * Original Author:  David Gilbert (for Simba Management Limited);
 * Contributor(s):   -;
 *
 * $Id: GanttDemo.java,v 1.1 2007/10/10 19:02:28 vauchers Exp $
 *
 * Changes
 * -------
 * 06-Jun-2002 : Version 1 (DG);
 *
 */

package com.jrefinery.chart.demo;

import com.jrefinery.chart.JFreeChart;
import com.jrefinery.chart.ChartFactory;
import com.jrefinery.chart.ChartPanel;
import com.jrefinery.data.IntervalCategoryDataset;
import com.jrefinery.data.GanttSeriesCollection;
import com.jrefinery.data.GanttSeries;
import com.jrefinery.data.TimeAllocation;
import com.jrefinery.ui.ApplicationFrame;
import java.util.Calendar;
import java.util.Date;

/**
 * A simple demonstration application showing how to create a Gantt chart.
 * <P>
 * This demo is intended to show the conceptual approach rather than being a polished
 * implementation.
 *
 */
public class GanttDemo extends ApplicationFrame {

    /** The data. */
    protected IntervalCategoryDataset data;

    /**
     * Default constructor.
     */
    public GanttDemo(String title) {

        super(title);

        IntervalCategoryDataset dataset = createDataset();

        // create the chart...
        JFreeChart chart = ChartFactory.createGanttChart("Gantt Chart Demo",  // chart title
                                                         "Task",              // domain axis label
                                                         "Date",              // range axis label
                                                         dataset,              // data
                                                         true                  // include legend
                                                         );

        // add the chart to a panel...
        ChartPanel chartPanel = new ChartPanel(chart);
        this.setContentPane(chartPanel);

    }

    /**
     * Creates a sample dataset.  This would not normally be hard-coded in this way.
     */
    private IntervalCategoryDataset createDataset() {

        // tasks...
        Object task1 = new String("Write Proposal");
        Object task2 = new String("Obtain Approval");
        Object task3 = new String("Requirements Analysis");
        Object task4 = new String("Design Phase");
        Object task5 = new String("Design Signoff");
        Object task6 = new String("Alpha Implementation");
        Object task7 = new String("Design Review");
        Object task8 = new String("Revised Design Signoff");
        Object task9 = new String("Beta Implementation");
        Object task10 = new String("Testing");
        Object task11 = new String("Final Implementation");
        Object task12 = new String("Signoff");

        GanttSeries s1 = new GanttSeries("Scheduled");
        s1.add(task1,  new TimeAllocation(date( 1,  Calendar.APRIL,    2001), date(5,  Calendar.APRIL,     2001)));
        s1.add(task2,  new TimeAllocation(date( 9,  Calendar.APRIL,    2001), date(9,  Calendar.APRIL,     2001)));
        s1.add(task3,  new TimeAllocation(date(10,  Calendar.APRIL,    2001), date(5,  Calendar.MAY,       2001)));
        s1.add(task4,  new TimeAllocation(date( 6,  Calendar.MAY,      2001), date(30, Calendar.MAY,       2001)));
        s1.add(task5,  new TimeAllocation(date( 2,  Calendar.JUNE,     2001), date(2,  Calendar.JUNE,      2001)));
        s1.add(task6,  new TimeAllocation(date( 3,  Calendar.JUNE,     2001), date(31, Calendar.JULY,      2001)));
        s1.add(task7,  new TimeAllocation(date( 1,  Calendar.AUGUST,   2001), date(8,  Calendar.AUGUST,    2001)));
        s1.add(task8,  new TimeAllocation(date(10,  Calendar.AUGUST,   2001), date(10, Calendar.AUGUST,    2001)));
        s1.add(task9,  new TimeAllocation(date(12,  Calendar.AUGUST,   2001), date(12, Calendar.SEPTEMBER, 2001)));
        s1.add(task10, new TimeAllocation(date(13, Calendar.SEPTEMBER, 2001), date(31, Calendar.OCTOBER,   2001)));
        s1.add(task11, new TimeAllocation(date(1,  Calendar.NOVEMBER,  2001), date(15, Calendar.NOVEMBER,  2001)));
        s1.add(task12, new TimeAllocation(date(28, Calendar.NOVEMBER,  2001), date(30, Calendar.NOVEMBER,  2001)));

        GanttSeries s2 = new GanttSeries("Actual");
        s2.add(task1,  new TimeAllocation(date( 1, Calendar.APRIL,     2001), date( 5, Calendar.APRIL,     2001)));
        s2.add(task2,  new TimeAllocation(date( 9, Calendar.APRIL,     2001), date( 9, Calendar.APRIL,     2001)));
        s2.add(task3,  new TimeAllocation(date(10, Calendar.APRIL,     2001), date(15, Calendar.MAY,       2001)));
        s2.add(task4,  new TimeAllocation(date(15, Calendar.MAY,       2001), date(17, Calendar.JUNE,      2001)));
        s2.add(task5,  new TimeAllocation(date(30, Calendar.JUNE,      2001), date(30, Calendar.JUNE,      2001)));
        s2.add(task6,  new TimeAllocation(date( 1, Calendar.JULY,      2001), date(12, Calendar.SEPTEMBER, 2001)));
        s2.add(task7,  new TimeAllocation(date(12, Calendar.SEPTEMBER, 2001), date(22, Calendar.SEPTEMBER, 2001)));
        s2.add(task8,  new TimeAllocation(date(25, Calendar.SEPTEMBER, 2001), date(27, Calendar.SEPTEMBER, 2001)));
        s2.add(task9,  new TimeAllocation(date(27, Calendar.SEPTEMBER, 2001), date(30, Calendar.OCTOBER,   2001)));
        s2.add(task10, new TimeAllocation(date(31, Calendar.OCTOBER,   2001), date(17, Calendar.NOVEMBER,  2001)));
        s2.add(task11, new TimeAllocation(date(18, Calendar.NOVEMBER,  2001), date( 5, Calendar.DECEMBER,  2001)));
        s2.add(task12, new TimeAllocation(date(10, Calendar.DECEMBER,  2001), date(11, Calendar.DECEMBER,  2001)));

        GanttSeriesCollection collection = new GanttSeriesCollection();
        collection.add(s1);
        collection.add(s2);

        return collection;

    }

    /**
     * Utility class for creating Date objects.
     */
    private Date date(int day, int month, int year) {

        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day);
        Date result = calendar.getTime();
        return result;

    }

    /**
     * Starting point for the demonstration application.
     */
    public static void main(String[] args) {

        GanttDemo demo = new GanttDemo("Gantt Chart Demo");
        demo.pack();
        demo.setVisible(true);

    }

}