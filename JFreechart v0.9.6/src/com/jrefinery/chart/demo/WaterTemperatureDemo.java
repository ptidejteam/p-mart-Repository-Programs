/* ======================================
 * JFreeChart : a free Java chart library
 * ======================================
 *
 * Project Info:  http://www.object-refinery.com/jfreechart/index.html
 * Project Lead:  David Gilbert (david.gilbert@object-refinery.com);
 *
 * (C) Copyright 2000-2003, by Simba Management Limited and Contributors.
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
 * -------------------------
 * WaterTemperatureDemo.java
 * -------------------------
 * (C) Copyright 2003, by Simba Management Limited.
 *
 * Original Author:  David Gilbert (for Simba Management Limited);
 * Contributor(s):   -;
 *
 * $Id: WaterTemperatureDemo.java,v 1.1 2007/10/10 19:57:52 vauchers Exp $
 *
 * Changes
 * -------
 * 17-Jan-2003 : Version 1 (DG);
 * 
 */

package com.jrefinery.chart.demo;

import com.jrefinery.data.XYDataset;
import com.jrefinery.data.XYSeries;
import com.jrefinery.data.XYSeriesCollection;
import com.jrefinery.chart.JFreeChart;
import com.jrefinery.chart.ChartPanel;
import com.jrefinery.chart.axis.HorizontalNumberAxis;
import com.jrefinery.chart.axis.NumberAxis;
import com.jrefinery.chart.axis.VerticalNumberAxis;
import com.jrefinery.chart.plot.XYPlot;
import com.jrefinery.chart.renderer.ReverseXYItemRenderer;
import com.jrefinery.chart.renderer.XYItemRenderer;
import com.jrefinery.ui.ApplicationFrame;
import com.jrefinery.ui.RefineryUtilities;

/**
 * This demo shows the use of the {@link ReverseXYItemRenderer} to plot water temperature at 
 * various depths.
 *
 * @author David Gilbert
 */
public class WaterTemperatureDemo extends ApplicationFrame {

    /**
     * A demonstration application showing an XY series containing a null value.
     *
     * @param title  the frame title.
     */
    public WaterTemperatureDemo(String title) {

        super(title);
        XYDataset dataset = createDataset();
        
        HorizontalNumberAxis axis1 = new HorizontalNumberAxis("Temperature");
        axis1.setRange(-0.55, -0.15);

        VerticalNumberAxis axis2 = new VerticalNumberAxis("Depth");
        axis2.setInverted(true);
        axis2.setRange(0.0, 35.0);
        axis2.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
        
        XYItemRenderer renderer = new ReverseXYItemRenderer();
       
        XYPlot plot = new XYPlot(dataset, axis1, axis2, renderer);
        JFreeChart chart = new JFreeChart("Water Temperature By Depth", plot);

        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new java.awt.Dimension(500, 270));
        setContentPane(chartPanel);

    }

    /**
     * Creates a sample dataset.
     * 
     * @return The dataset.
     */
    private XYDataset createDataset() {
    
        XYSeries series = new XYSeries("Zone 1");
        series.add(1.0, -0.5);
        series.add(5.0, -0.5);
        series.add(10.0, -0.4);
        series.add(15.0, -0.4);
        series.add(20.0, -0.3);
        series.add(25.0, -0.3);
        series.add(30.0, -0.2);
        series.add(35.0, -0.2);
        
        return new XYSeriesCollection(series);
        
    }    
    
    /**
     * Starting point for the demonstration application.
     *
     * @param args  ignored.
     */
    public static void main(String[] args) {

        WaterTemperatureDemo demo = new WaterTemperatureDemo("Water Temperature Demo");
        demo.pack();
        RefineryUtilities.centerFrameOnScreen(demo);
        demo.setVisible(true);

    }

}
