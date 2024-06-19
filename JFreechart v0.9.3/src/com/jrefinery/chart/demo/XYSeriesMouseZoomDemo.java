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
 * --------------------------
 * XYSeriesMouseZoomDemo.java
 * --------------------------
 * (C) Copyright 2002, by Simba Management Limited and Contributors.
 *
 * Original Author:  David Gilbert (for Simba Management Limited);
 * Contributor(s):   Viktor Rajewski;
 *
 *
 * Changes
 * -------
 * 12-Aug-2002 : Version 1, based on XYSeriesDemo (VR);
 *
 */

package com.jrefinery.chart.demo;

import java.awt.Paint;
import java.awt.Color;
import java.awt.event.*;
import javax.swing.JPanel;
import javax.swing.JCheckBox;
import com.jrefinery.data.XYSeries;
import com.jrefinery.data.XYSeriesCollection;
import com.jrefinery.ui.ApplicationFrame;
import com.jrefinery.chart.JFreeChart;
import com.jrefinery.chart.ChartFactory;
import com.jrefinery.chart.ChartPanel;
import com.jrefinery.chart.demo.SampleXYDataset;

public class XYSeriesMouseZoomDemo extends ApplicationFrame {

    protected XYSeries series;
    ChartPanel chartPanel;
    JCheckBox xzoom ;
    JCheckBox yzoom ;

    /**
     * A demonstration application showing a quarterly time series containing a null value.
     */
    public XYSeriesMouseZoomDemo(String title) {

        super(title);
        SampleXYDataset data = new SampleXYDataset();
        JFreeChart chart = ChartFactory.createXYChart("XY Series Mouse Zoom Demo", "X", "Y", data, true);

        chart.getPlot().setSeriesPaint(new Paint[] { Color.blue });
        chartPanel = new ChartPanel(chart);
          chartPanel.setHorizontalZoom(false);
          chartPanel.setVerticalZoom(false);
          chartPanel.setHorizontalAxisTrace(false);
          chartPanel.setVerticalAxisTrace(false);
          chartPanel.setFillZoomRectangle(true);

          JPanel pan = new JPanel();
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
          pan.add(checkpanel);
          pan.add(chartPanel);

          this.setContentPane(pan);

    }

    /**
     * Starting point for the demonstration application.
     */
    public static void main(String[] args) {

        XYSeriesMouseZoomDemo demo = new XYSeriesMouseZoomDemo("XY Series Mouse Zoom Demo");
        demo.pack();
        demo.setVisible(true);

    }

     class CheckListener implements ItemListener {
        public void itemStateChanged(ItemEvent e) {
             Object source = e.getItemSelectable();
             if(source == xzoom) {
                 if(e.getStateChange() == ItemEvent.DESELECTED)
                 {
                    chartPanel.setHorizontalZoom(false);
                    chartPanel.setHorizontalAxisTrace(false);
                    chartPanel.repaint();
                 }
                 else
                 {
                    chartPanel.setHorizontalZoom(true);
                    chartPanel.setHorizontalAxisTrace(true);
                 }
             }
             else if(source == yzoom) {
                 if(e.getStateChange() == ItemEvent.DESELECTED)
                 {
                    chartPanel.setVerticalZoom(false);
                    chartPanel.setVerticalAxisTrace(false);
                    chartPanel.repaint();
                 }
                 else
                 {
                    chartPanel.setVerticalZoom(true);
                    chartPanel.setVerticalAxisTrace(true);
                 }
             }
       }
    }

}
