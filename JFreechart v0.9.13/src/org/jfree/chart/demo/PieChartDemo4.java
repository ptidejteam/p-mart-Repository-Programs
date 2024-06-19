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
 * ------------------
 * PieChartDemo4.java
 * ------------------
 * (C) Copyright 2003, by Object Refinery Limited and Contributors.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited);
 * Contributor(s):   -;
 *
 * $Id: PieChartDemo4.java,v 1.1 2007/10/10 19:15:25 vauchers Exp $
 *
 * Changes
 * -------
 * 11-Feb-2003 : Version 1 (DG);
 *
 */

package org.jfree.chart.demo;

import java.awt.Color;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.labels.StandardPieItemLabelGenerator;
import org.jfree.chart.plot.PiePlot;
import org.jfree.data.DefaultPieDataset;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;

/**
 * A simple demonstration application showing how to create a pie chart using data from a
 * {@link DefaultPieDataset}.
 *
 * @author David Gilbert
 */
public class PieChartDemo4 extends ApplicationFrame {

    /**
     * Default constructor.
     *
     * @param title  the frame title.
     */
    public PieChartDemo4(String title) {

        super(title);

        // create a dataset...
        DefaultPieDataset data = new DefaultPieDataset();
        data.setValue("One", new Double(43.2));
        data.setValue("Two", new Double(10.0));
        data.setValue("Three", new Double(27.5));
        data.setValue("Four", new Double(17.5));
        data.setValue("Five", new Double(11.0));
        data.setValue("Six", new Double(19.4));
        data.setValue("Seven", new Double(19.4));
        data.setValue("Eight", new Double(19.4));
        data.setValue("Nine", new Double(9.4));
        data.setValue("Ten", new Double(19.4));
        data.setValue("Eleven", new Double(9.4));
        data.setValue("Twelve", new Double(9.4));
        data.setValue("Thirteen", new Double(9.4));
        data.setValue("Fourteen", new Double(9.4));

        // create the chart...
        JFreeChart chart = ChartFactory.createPieChart(
            "Pie Chart Demo 4",  // chart title
            data,                // data
            true,                // include legend
            true,
            false
        );

        // set the background color for the chart...
        chart.setBackgroundPaint(Color.yellow);
        PiePlot plot = (PiePlot) chart.getPlot();
        plot.setSectionLabelType(PiePlot.NAME_AND_PERCENT_LABELS);
        plot.setNoDataMessage("No data available");
        plot.setItemLabelGenerator(new StandardPieItemLabelGenerator());
        plot.setSectionPaint(0, new Color(0xCC, 0xCC, 0xFF));
        plot.setSectionPaint(1, new Color(0xFF, 0xCC, 0xCC));
        plot.setSectionPaint(2, new Color(0xCC, 0xFF, 0xCC));
        plot.setSectionPaint(3, new Color(0xFF, 0x99, 0x99));
        plot.setSectionPaint(4, new Color(0x99, 0xFF, 0x99));
        plot.setSectionPaint(5, new Color(0x99, 0x99, 0xFF));

        plot.setSectionOutlinePaint(null);
        plot.setSectionOutlinePaintListAutoFill(false);

        // add the chart to a panel...
        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new java.awt.Dimension(500, 270));
        setContentPane(chartPanel);

    }

    /**
     * Starting point for the demonstration application.
     *
     * @param args  ignored.
     */
    public static void main(String[] args) {

        PieChartDemo4 demo = new PieChartDemo4("Pie Chart Demo 4");
        demo.pack();
        RefineryUtilities.centerFrameOnScreen(demo);
        demo.setVisible(true);

    }

}
