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
 * -------------------
 * MeterChartDemo.java
 * -------------------
 * (C) Copyright 2002, 2003, by Hari and Contributors.
 *
 * Original Author:  Hari;
 * Contributor(s):   David Gilbert (for Object Refinery Limited);
 *
 * $Id: MeterChartDemo.java,v 1.1 2007/10/10 19:15:24 vauchers Exp $
 *
 * Changes
 * -------
 * 08-Apr-2002 : Version 1, contributed by Hari (DG);
 * 19-Apr-2002 : Renamed JRefineryUtilities-->RefineryUtilities (DG);
 * 25-Jun-2002 : Removed unnecessary imports (DG);
 * 11-Oct-2002 : Fixed errors reported by Checkstyle (DG);
 *
 */

package org.jfree.chart.demo;

import java.awt.Color;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Insets;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;

import org.jfree.chart.ChartFrame;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.MeterLegend;
import org.jfree.chart.plot.DialShape;
import org.jfree.chart.plot.MeterPlot;
import org.jfree.data.DefaultValueDataset;
import org.jfree.data.Range;
import org.jfree.ui.RefineryUtilities;

/**
 * A meter chart demonstration application.
 *
 * @author Hari
 */
public class MeterChartDemo {

    /**
     * Displays a meter chart.
     *
     * @param value  the value.
     * @param shape  the dial shape.
     */
    void displayMeterChart(double value, DialShape shape) {

        DefaultValueDataset data = new DefaultValueDataset(75.0);
        MeterPlot plot = new MeterPlot(data);
        plot.setUnits("Degrees");
        plot.setRange(new Range(20.0, 140.0));
        plot.setNormalRange(new Range(70.0, 100.0));
        plot.setWarningRange(new Range(100.0, 120.0));
        plot.setCriticalRange(new Range(120.0, 140.0));
        
        plot.setDialShape(shape);
        plot.setNeedlePaint(Color.white);
        plot.setTickLabelFont(new Font("SansSerif", Font.BOLD, 9));

        plot.setInsets(new Insets(5, 5, 5, 5));
        JFreeChart chart = new JFreeChart(
            "Meter Chart", 
            JFreeChart.DEFAULT_TITLE_FONT, 
            plot, 
            false
        );

        MeterLegend legend = new MeterLegend(chart, "Sample Meter");
        chart.setLegend(legend);

        chart.setBackgroundPaint(new GradientPaint(0, 0, Color.white, 0, 1000, Color.blue));

        JFrame chartFrame = new ChartFrame("Meter Chart", chart);
        chartFrame.addWindowListener(new WindowAdapter() {
          /**
           * Invoked when a window is in the process of being closed.
           * The close operation can be overridden at this point.
           */
          public void windowClosing(WindowEvent e)
          {
            System.exit(0);
          }
        });
        chartFrame.pack();
        RefineryUtilities.positionFrameRandomly(chartFrame);
        chartFrame.setSize(250, 250);
        chartFrame.show();

    }

    /**
     * Starting point for the meter plot demonstration application.
     *
     * @param args  used to specify the type and value.
     */
    public static void main(String[] args) {

        if (args.length == 0) {
            System.err.println("Usage: java TestMeter <type> <value>");
            System.err.println("Type:  0 = PIE");
            System.err.println("Type:  1 = CIRCLE");
            System.err.println("Type:  2 = CHORD");
        }
        MeterChartDemo h = new MeterChartDemo();
        double val = 85;
        DialShape dialShape = DialShape.CIRCLE;
        if (args.length > 0) {
            int type = Integer.parseInt(args[0]);
            if (type == 0) {
                dialShape = DialShape.PIE;
            }
            else if (type == 1) {
                dialShape = DialShape.CIRCLE;
            }
            else if (type == 0) {
                dialShape = DialShape.CHORD;
            }
        }
        if (args.length > 1) {
            val = new Double(args[1]).doubleValue();
        }
        h.displayMeterChart(val, dialShape);

    }

}
