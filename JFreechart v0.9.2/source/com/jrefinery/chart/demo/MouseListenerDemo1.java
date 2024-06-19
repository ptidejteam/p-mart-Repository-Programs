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
 * $Id: MouseListenerDemo1.java,v 1.1 2007/10/10 19:41:58 vauchers Exp $
 *
 * Changes
 * -------
 * 24-May-2002 : Version 1 (DG);
 * 25-Jun-2002 : Removed unnecessary import (DG);
 *
 */

package com.jrefinery.chart.demo;

import java.awt.Color;
import com.jrefinery.data.DefaultPieDataset;
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
public class MouseListenerDemo1 extends ApplicationFrame implements ChartMouseListener {

    /**
     * Default constructor.
     */
    public MouseListenerDemo1(String title) {

        super(title);

        // create a dataset...
        DefaultPieDataset data = new DefaultPieDataset();
        data.setValue("Java", new Double(43.2));
        data.setValue("Visual Basic", new Double(0.0));
        data.setValue("C/C++", new Double(17.5));

        // create the chart...
        JFreeChart chart = ChartFactory.createPieChart("Pie Chart Demo 1",  // chart title
                                                       data,                // data
                                                       true                 // include legend
                                                       );

        // set the background color for the chart...
        chart.setBackgroundPaint(Color.yellow);

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

        MouseListenerDemo1 demo = new MouseListenerDemo1("Mouse Listener Demo");
        demo.pack();
        demo.setVisible(true);

    }

}