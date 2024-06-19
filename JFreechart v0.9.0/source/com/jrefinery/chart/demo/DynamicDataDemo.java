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
 * --------------------
 * DynamicDataDemo.java
 * --------------------
 * (C) Copyright 2002, by Simba Management Limited.
 *
 * Original Author:  David Gilbert (for Simba Management Limited).
 * Contributor(s):   -;
 *
 * $Id: DynamicDataDemo.java,v 1.1 2007/10/10 19:01:20 vauchers Exp $
 *
 * Changes
 * -------
 * 28-Mar-2002 : Version 1 (DG);
 *
 */

package com.jrefinery.chart.demo;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JPanel;
import javax.swing.JButton;
import com.jrefinery.data.BasicTimeSeries;
import com.jrefinery.data.TimeSeriesCollection;
import com.jrefinery.data.Millisecond;
import com.jrefinery.ui.ApplicationFrame;
import com.jrefinery.chart.ChartFactory;
import com.jrefinery.chart.JFreeChart;
import com.jrefinery.chart.XYPlot;
import com.jrefinery.chart.ValueAxis;
import com.jrefinery.chart.ChartPanel;

/**
 * A demonstration application showing a time series chart overlaid with a vertical XY bar chart.
 */
public class DynamicDataDemo extends ApplicationFrame implements ActionListener {

    protected BasicTimeSeries series;

    protected double lastValue = 100.0;

    /**
     * Constructs a new demonstration application.
     */
    public DynamicDataDemo(String title) {

        super(title);
        this.series = new BasicTimeSeries("Random Data", Millisecond.class);
        TimeSeriesCollection dataset = new TimeSeriesCollection(series);
        JFreeChart chart = ChartFactory.createTimeSeriesChart("Dynamic Data Demo", "Time", "Value",
                                                              dataset, true);
        XYPlot plot = chart.getXYPlot();
        ValueAxis axis = plot.getDomainAxis();
        axis.setAutoRange(true);
        axis.setFixedAutoRange(60000.0);  // 60 seconds

        axis = plot.getRangeAxis();
        axis.setRange(0.0, 200.0);

        JPanel content = new JPanel(new BorderLayout());

        ChartPanel chartPanel = new ChartPanel(chart);
        JButton button = new JButton("Add New Data Item");
        button.setActionCommand("ADD_DATA");
        button.addActionListener(this);
        content.add(chartPanel);
        content.add(button, BorderLayout.SOUTH);
        this.setContentPane(content);

    }

    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().equals("ADD_DATA")) {
            double factor = 0.9 + 0.2*Math.random();
            this.lastValue = lastValue*factor;
            Millisecond now = new Millisecond();
            System.out.println("Now = "+now.toString());
            this.series.add(new Millisecond(), lastValue);
        }
    }


    /**
     * Starting point for the demonstration application.
     */
    public static void main(String[] args) {

        DynamicDataDemo demo = new DynamicDataDemo("Dynamic Data Demo");
        demo.pack();
        demo.setVisible(true);

    }

}







