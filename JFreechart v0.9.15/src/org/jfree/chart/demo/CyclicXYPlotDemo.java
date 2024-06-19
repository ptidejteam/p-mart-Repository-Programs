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
 * CyclicXYPlot.java
 * ------------------
 * (C) Copyright 2002, 2003, by Object Refinery Limited and Contributors.
 *
 * Original Author:  Nicolas Brodu
 * Contributor(s):   -;
 *
 * $Id: CyclicXYPlotDemo.java,v 1.1 2007/10/10 19:21:51 vauchers Exp $
 *
 * Changes
 * -------
 * 20-Nov-2003 : Creation Date (NB)
 *
 */

package org.jfree.chart.demo;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.Timer;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CyclicNumberAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.CyclicXYItemRenderer;
import org.jfree.data.XYSeries;
import org.jfree.data.XYSeriesCollection;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;

/**
 * Demo for an XY plot, with a cyclic axis and renderer  
 *
 * @author Nicolas Brodu
 */
public class CyclicXYPlotDemo extends ApplicationFrame implements ActionListener {

	XYSeries series;
	long x = 0;
	double y = 50;
	Timer timer;
	
    /**
     * A demonstration application showing an XY plot, with a cyclic axis and renderer
     *
     * @param title  the frame title.
     */
    public CyclicXYPlotDemo(String title) {

        super(title);

        series = new XYSeries("Random Data");
        series.setMaximumItemCount(50); // Only 50 items are visible at the same time. Keep more as a mean to test this.
        XYSeriesCollection data = new XYSeriesCollection(series);
        
        JFreeChart chart = ChartFactory.createXYLineChart(
            "Cyclic XY Plot Demo",
            "X", 
            "Y", 
            data,
            PlotOrientation.VERTICAL,
            true,
            true,
            false
        );

		XYPlot plot = chart.getXYPlot();
		plot.setDomainAxis(new CyclicNumberAxis(10,0));
		plot.setRenderer(new CyclicXYItemRenderer());

		NumberAxis axis = (NumberAxis) plot.getRangeAxis();
        axis.setAutoRangeIncludesZero(false);
        axis.setAutoRangeMinimumSize(1.0);

        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new java.awt.Dimension(400, 300));
		JPanel content = new JPanel(new BorderLayout());
		content.add(chartPanel,BorderLayout.CENTER);

		JButton button1 = new JButton("Start");
		button1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				timer.start();
			}
		});
        
		JButton button2 = new JButton("Stop");
		button2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				timer.stop();
			}
		});

		JButton button3 = new JButton("Step by step");
		button3.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				CyclicXYPlotDemo.this.actionPerformed(null);
			}
		});

		JPanel buttonPanel = new JPanel(new FlowLayout());
		buttonPanel.add(button1);
		buttonPanel.add(button2);
		buttonPanel.add(button3);
        
		content.add(buttonPanel, BorderLayout.SOUTH);
		setContentPane(content);

		timer = new Timer(200, this);            
    }

    /**
     * Starting point for the demonstration application.
     *
     * @param args  ignored.
     */
    public static void main(String[] args) {

        CyclicXYPlotDemo demo = new CyclicXYPlotDemo("Cyclic XY Plot Demo");
        demo.pack();
        RefineryUtilities.centerFrameOnScreen(demo);
        demo.setVisible(true);

		//.start(); // Calls ourselves each half of a second
    }

	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent e) {
		double delta = Math.random()*10 - 5;
		if (delta==-5.0) delta = 0; // balance chances
		y += delta;
		series.add(x++ / 4.0, y);
	}

}
