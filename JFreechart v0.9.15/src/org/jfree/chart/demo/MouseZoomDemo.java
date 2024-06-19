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
 * MouseZoomDemo.java
 * ------------------
 * (C) Copyright 2002, by Viktor Rajewski and Contributors.
 *
 * Original Author:  Viktor Rajewski;
 * Contributor(s):   David Gilbert (for Object Refinery Limited);
 *
 *
 * Changes
 * -------
 * 12-Aug-2002 : Version 1, based on XYSeriesDemo (VR);
 * 11-Oct-2002 : Renamed XYSeriesMouseZoomDemo --> MouseZoomDemo, altered layout, and fixed errors
 *               reported by Checkstyle (DG);
 *
 */

package org.jfree.chart.demo;

import java.awt.BorderLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JCheckBox;
import javax.swing.JPanel;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;

/**
 * A simple demo showing mouse zooming.
 *
 * @author Viktor Rajewski
 */
public class MouseZoomDemo extends ApplicationFrame {

    /** The chart panel. */
    private ChartPanel chartPanel;

    /** X zoom. */
    private JCheckBox xzoom;

    /** Y zoom. */
    private JCheckBox yzoom;

    /**
     * A demonstration of mouse zooming.
     *
     * @param title  the frame title.
     */
    public MouseZoomDemo(String title) {

        super(title);
        SampleXYDataset data = new SampleXYDataset();
        JFreeChart chart = ChartFactory.createXYLineChart(
            "Mouse Zoom Demo",
            "X", 
            "Y", 
            data, 
            PlotOrientation.VERTICAL,
            true, 
            true, 
            false
        );

        chartPanel = new ChartPanel(chart);
        chartPanel.setHorizontalZoom(false);
        chartPanel.setVerticalZoom(false);
        chartPanel.setHorizontalAxisTrace(false);
        chartPanel.setVerticalAxisTrace(false);
        chartPanel.setFillZoomRectangle(true);
        chartPanel.setPreferredSize(new java.awt.Dimension(500, 270));

        JPanel main = new JPanel(new BorderLayout());
        JPanel checkpanel = new JPanel();
        xzoom = new JCheckBox("Horizontal Mouse Zooming");
        xzoom.setSelected(false);
        yzoom = new JCheckBox("Vertical Mouse Zooming");
        yzoom.setSelected(false);
        CheckListener clisten = new CheckListener();
        xzoom.addItemListener(clisten);
        yzoom.addItemListener(clisten);
        checkpanel.add(xzoom);
        checkpanel.add(yzoom);
        main.add(checkpanel, BorderLayout.SOUTH);
        main.add(chartPanel);
        setContentPane(main);

    }

    // ****************************************************************************
    // * COMMERCIAL SUPPORT / JFREECHART DEVELOPER GUIDE                          *
    // * Please note that commercial support and documentation is available from: *
    // *                                                                          *
    // * http://www.object-refinery.com/jfreechart/support.html                   *
    // *                                                                          *
    // * This is not only a great service for developers, but is a VERY IMPORTANT *
    // * source of funding for the JFreeChart project.  Please support us so that *
    // * we can continue developing free software.                                *
    // ****************************************************************************

    /**
     * Starting point for the demonstration application.
     *
     * @param args  ignored.
     */
    public static void main(String[] args) {

        MouseZoomDemo demo = new MouseZoomDemo("Mouse Zoom Demo");
        demo.pack();
        RefineryUtilities.centerFrameOnScreen(demo);
        demo.setVisible(true);

    }

    /**
     * An item listener.
     *
     * @author VR
     */
    class CheckListener implements ItemListener {

        /**
         * Receives change events.
         *
         * @param e  the event.
         */
        public void itemStateChanged(ItemEvent e) {
            Object source = e.getItemSelectable();
            if (source == xzoom) {
                if (e.getStateChange() == ItemEvent.DESELECTED) {
                    chartPanel.setHorizontalZoom(false);
                    chartPanel.setHorizontalAxisTrace(false);
                    chartPanel.repaint();
                }
                else {
                    chartPanel.setHorizontalZoom(true);
                    chartPanel.setHorizontalAxisTrace(true);
                }
            }
            else if (source == yzoom) {
                if (e.getStateChange() == ItemEvent.DESELECTED) {
                    chartPanel.setVerticalZoom(false);
                    chartPanel.setVerticalAxisTrace(false);
                    chartPanel.repaint();
                }
                else {
                    chartPanel.setVerticalZoom(true);
                    chartPanel.setVerticalAxisTrace(true);
                }
            }
       }
    }

}
