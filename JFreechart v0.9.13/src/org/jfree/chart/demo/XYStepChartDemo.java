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
 * --------------------
 * XYStepChartDemo.java
 * --------------------
 * (C) Copyright 2002, 2003, by Roger Studner and Contributors.
 *
 * Original Author:  Roger Studner;
 * Contributor(s):   David Gilbert (for Object Refinery Limited);
 *
 * $Id: XYStepChartDemo.java,v 1.1 2007/10/10 19:15:24 vauchers Exp $
 *
 * Changes
 * -------
 * 13-May-2002 : Version 1, contributed by Roger Studner (DG);
 * 11-Oct-2002 : Moved create method to ChartFactory class, and fixed issues reported by
 *               Checkstyle (DG);
 *
 */
package org.jfree.chart.demo;

import java.awt.BasicStroke;
import java.awt.Color;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartFrame;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.XYDataset;
import org.jfree.ui.RefineryUtilities;

/**
 * A demonstration of the XYStepRenderer class.
 *
 * @author Roger Studner
 */
public class XYStepChartDemo {

    /** A frame for displaying the chart. */
    private ChartFrame frame = null;

    /**
     * Displays a sample chart in its own frame.
     */
    private void displayChart() {

        if (frame == null) {

            // create a default chart based on some sample data...
            String title = "LCACs in use at given time";
            String xAxisLabel = "Time";
            String yAxisLabel = "Number of Transports";

            XYDataset data = DemoDatasetFactory.createStepXYDataset();

            JFreeChart chart = ChartFactory.createXYStepChart(
                title,
                xAxisLabel, yAxisLabel,
                data,
                PlotOrientation.VERTICAL,
                true,   // legend
                true,   // tooltips
                false   // urls
            );

            // then customise it a little...
            chart.setBackgroundPaint(new Color(216, 216, 216));
            XYPlot plot = chart.getXYPlot();
            plot.getRenderer().setSeriesStroke(0, new BasicStroke(2.0f));
            plot.getRenderer().setSeriesStroke(1, new BasicStroke(2.0f));
            
            // and present it in a frame...
            frame = new ChartFrame("Plan Comparison", chart);
            frame.pack();
            RefineryUtilities.positionFrameRandomly(frame);
            frame.show();

        }
        else {
            frame.show();
            frame.requestFocus();
        }

    }

    /**
     * The starting point for the demonstration application.
     *
     * @param args  ignored.
     */
    public static void main(String[] args) {

        XYStepChartDemo demo = new XYStepChartDemo();
        demo.displayChart();

    }

}
