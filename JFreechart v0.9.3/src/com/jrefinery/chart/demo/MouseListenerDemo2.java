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
 * -----------------------
 * MouseListenerDemo1.java
 * -----------------------
 * (C) Copyright 2002, by Simba Management Limited and Contributors.
 *
 * Original Author:  David Gilbert (for Simba Management Limited);
 * Contributor(s):   -;
 *
 * $Id: MouseListenerDemo2.java,v 1.1 2007/10/10 19:52:20 vauchers Exp $
 *
 * Changes
 * -------
 * 24-May-2002 : Version 1 (DG);
 * 25-Jun-2002 : Removed unnecessary import (DG);
 *
 */

package com.jrefinery.chart.demo;

import java.awt.Color;
import com.jrefinery.data.CategoryDataset;
import com.jrefinery.ui.ApplicationFrame;
import com.jrefinery.chart.JFreeChart;
import com.jrefinery.chart.ChartFactory;
import com.jrefinery.chart.ChartPanel;
import com.jrefinery.chart.ChartMouseListener;
import com.jrefinery.chart.ChartMouseEvent;
import com.jrefinery.chart.entity.ChartEntity;

/**
 * A simple demonstration application showing how to create a pie chart using data from a
 * DefaultPieDataset.
 */
public class MouseListenerDemo2 extends ApplicationFrame implements ChartMouseListener {

    /**
     * Default constructor.
     */
    public MouseListenerDemo2(String title) {

        super(title);
        CategoryDataset data = DemoDatasetFactory.createCategoryDataset();
        JFreeChart chart = ChartFactory.createVerticalBarChart("Test", "Category", "Value",
                                                               data, true);

        // set the background color for the chart...
        chart.setBackgroundPaint(Color.orange);

        // add the chart to a panel...
        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.addChartMouseListener(this);
        this.setContentPane(chartPanel);

    }

    public void chartMouseClicked(ChartMouseEvent event) {
        ChartEntity entity = event.getEntity();
        if (entity!=null) {
            System.out.println(entity.toString());
        }
        else {
            System.out.println("Null entity.");
        }
    }

    public void chartMouseMoved(ChartMouseEvent event) {
        int x = event.getTrigger().getX();
        int y = event.getTrigger().getY();
        ChartEntity entity = event.getEntity();
        if (entity!=null) {
            System.out.println("Location: "+x+", "+y+": "+entity.toString());
        }
        else {
            System.out.println("Location: "+x+", "+y+": null entity.");
        }
    }

    /**
     * Starting point for the demonstration application.
     */
    public static void main(String[] args) {

        MouseListenerDemo2 demo = new MouseListenerDemo2("Mouse Listener Demo 2");
        demo.pack();
        demo.setVisible(true);

    }

}
