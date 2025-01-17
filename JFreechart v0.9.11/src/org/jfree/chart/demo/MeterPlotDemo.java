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
 * MeterPlotDemo.java
 * ------------------
 * (C) Copyright 2002, 2003, by Hari and Contributors.
 *
 * Original Author:  Hari;
 * Contributor(s):   David Gilbert (for Object Refinery Limited);
 *
 * $Id: MeterPlotDemo.java,v 1.1 2007/10/10 19:09:08 vauchers Exp $
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
import org.jfree.chart.plot.MeterPlot;
import org.jfree.data.DefaultMeterDataset;
import org.jfree.ui.RefineryUtilities;

/**
 * A meter chart demonstration application.
 *
 * @author Hari
 */
public class MeterPlotDemo {

    /**
     * Displays a meter chart.
     *
     * @param value  the value.
     * @param type  the type.
     */
    void displayMeterChart(double value, int type) {

        JFreeChart chart = null;

        String title = "Meter Chart";
        DefaultMeterDataset data = new DefaultMeterDataset(new Double(20), new Double(140),
                                                           new Double(value), "Units");
        data.setNormalRange(new Double(70), new Double(100));
        data.setWarningRange(new Double(100), new Double(120));
        data.setCriticalRange(new Double(120), new Double(140));
        //data.setBorderType(MeterDataset.CRITICAL_DATA);

        MeterPlot plot = new MeterPlot(data);
        plot.setDialType(type);
        plot.setNeedlePaint(Color.white);
        plot.setTickLabelFont(new Font("SansSerif", Font.BOLD, 9));

        plot.setInsets(new Insets(5, 5, 5, 5));
        chart = new JFreeChart(title, JFreeChart.DEFAULT_TITLE_FONT, plot, false);

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
            System.err.println("Type:  0 = DIALTYPE_PIE");
            System.err.println("Type:  1 = DIALTYPE_CIRCLE");
            System.err.println("Type:  2 = DIALTYPE_CHORD");
        }

        MeterPlotDemo h = new MeterPlotDemo();
        double val = 85;
        int type = MeterPlot.DIALTYPE_CIRCLE;
        if (args.length > 0) {
            type = Integer.parseInt(args[0]);
        }
        if (args.length > 1) {
            val = new Double(args[1]).doubleValue();
        }
        h.displayMeterChart(val, type);

    }

}
