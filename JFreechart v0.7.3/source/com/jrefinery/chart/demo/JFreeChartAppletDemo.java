/* ===============
 * JFreeChart Demo
 * ===============
 *
 * Project Info:  http://www.jrefinery.com/jfreechart;
 * Project Lead:  David Gilbert (david.gilbert@jrefinery.com);
 *
 * (C) Copyright 2000-2002, by Simba Management Limited and Contributors.
 *
 * This program is free software; you can redistribute it and/or modify it under the terms
 * of the GNU General Public License as published by the Free Software Foundation;
 * either version 2 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program;
 * if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston,
 * MA 02111-1307, USA.
 *
 * -------------------------
 * JFreeChartAppletDemo.java
 * -------------------------
 * (C) Copyright 2002, by Simba Management Limited.
 *
 * Original Author:  David Gilbert (for Simba Management Limited);
 * Contributor(s):   -;
 *
 * $Id: JFreeChartAppletDemo.java,v 1.1 2007/10/10 18:57:07 vauchers Exp $
 *
 * Changes
 * -------
 * 11-Feb-2002 : Version 1 (DG);
 *
 */

package com.jrefinery.chart.demo;

import javax.swing.JApplet;
import javax.swing.JTabbedPane;
import com.jrefinery.data.CategoryDataset;
import com.jrefinery.data.XYDataset;
import com.jrefinery.chart.JFreeChart;
import com.jrefinery.chart.JFreeChartPanel;
import com.jrefinery.chart.ChartFactory;

/**
 * A simple applet containing two sample charts in a JTabbedPane.
 */
public class JFreeChartAppletDemo extends JApplet {

    /**
     * Constructs the demo applet.
     */
    public JFreeChartAppletDemo() {

        JTabbedPane tabs = new JTabbedPane();

        XYDataset data1 = DemoDatasetFactory.createTimeSeriesCollection1();
        JFreeChart chart1 = ChartFactory.createTimeSeriesChart("Time Series", "Date", "Rate",
                                                               data1, true);
        JFreeChartPanel panel1 = new JFreeChartPanel(chart1, 400, 300, 200, 100,
                                                     true, false, false, false, true, true);
        tabs.add("Chart 1", panel1);

        CategoryDataset data2 = DemoDatasetFactory.createCategoryDataset();
        JFreeChart chart2 = ChartFactory.createHorizontalBarChart("Bar Chart", "Categories", "Value",
                                                                  data2, true);
        JFreeChartPanel panel2 = new JFreeChartPanel(chart2, 400, 300, 200, 100,
                                                     true, false, false, false, true, true);
        tabs.add("Chart 2", panel2);

        this.getContentPane().add(tabs);

    }

}