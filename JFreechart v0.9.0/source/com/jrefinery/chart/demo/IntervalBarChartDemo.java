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
 * -------------------------
 * IntervalBarChartDemo.java
 * -------------------------
 * (C) Copyright 2002, by Jeremy Bowman.
 *
 * Original Author:  Jeremy Bowman.
 * Contributor(s):   -;
 *
 * $Id: IntervalBarChartDemo.java,v 1.1 2007/10/10 19:01:20 vauchers Exp $
 *
 * Changes
 * -------
 * 29-Apr-2002 : Version 1, contributed by Jeremy Bowman.  Name changed to
 *               IntervalBarChartDemo, and the chart is displayed in a frame rather than
 *               saved to a file (DG);
 *
 */

package com.jrefinery.chart.demo;

import com.jrefinery.chart.ChartFrame;
import com.jrefinery.chart.ChartUtilities;
import com.jrefinery.chart.HorizontalCategoryAxis;
import com.jrefinery.chart.JFreeChart;
import com.jrefinery.chart.NumberTickUnit;
import com.jrefinery.chart.VerticalCategoryPlot;
import com.jrefinery.chart.VerticalIntervalBarRenderer;
import com.jrefinery.chart.VerticalNumberAxis;
import com.jrefinery.data.DefaultIntervalCategoryDataset;

import java.awt.Color;
import java.awt.Font;
import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Locale;

public class IntervalBarChartDemo {

     private static final String[] categories = { "1", "3", "5", "10", "20" };
     private static Color[] dataColors = null;
     private static Font labelFont = null;
     private static Font titleFont = null;
     private JFreeChart chart = null;

     static {
          dataColors = new Color[1];
          dataColors[0] = new Color(51, 102, 153);
          labelFont = new Font("Helvetica", Font.PLAIN, 10);
          titleFont = new Font("Helvetica", Font.BOLD, 14);
     }

     public IntervalBarChartDemo() {
          DefaultIntervalCategoryDataset data = null;
          double[][] lows = { { -.0315, .0159, .0306, .0453, .0557 } };
          double[][] highs = { { .1931, .1457, .1310, .1163, .1059 } };
          data = new DefaultIntervalCategoryDataset(lows, highs);
          data.setCategories(categories);

          String title = "Strategie Sicherheit";
          String xTitle = "Zeitraum (in Jahren)";
          String yTitle = "Performance";
          HorizontalCategoryAxis xAxis = new HorizontalCategoryAxis(xTitle);
          xAxis.setLabelFont(titleFont);
          xAxis.setTickLabelFont(labelFont);
          xAxis.setTickMarksVisible(false);
          VerticalNumberAxis yAxis = new VerticalNumberAxis(yTitle);
          yAxis.setLabelFont(titleFont);
          yAxis.setTickLabelFont(labelFont);
          yAxis.setMinimumAxisValue(-0.2);
          yAxis.setMaximumAxisValue(0.40);
          DecimalFormat formatter = new DecimalFormat("0.##%");
          yAxis.setTickUnit(new NumberTickUnit(0.05, formatter));

          VerticalIntervalBarRenderer renderer = null;
          renderer = new VerticalIntervalBarRenderer();
          VerticalCategoryPlot plot = null;
          plot = new VerticalCategoryPlot(data, xAxis, yAxis, renderer);
          plot.setLabelFont(labelFont);
          plot.setLabelFormatString("0.##%");
          // Uncomment the following line for vertical plot labels
          //plot.setVerticalLabels(true);
          plot.setBackgroundPaint(Color.lightGray);
          plot.setOutlinePaint(Color.white);
          plot.setSeriesPaint(dataColors);
          chart = new JFreeChart(title, titleFont, plot, false);
          chart.setBackgroundPaint(Color.white);
     } // ctor

     public JFreeChart getChart() {
          return chart;
     } // getChart

     /**
      * For testing from the command line
      */
     public static void main(String[] args) {
          Locale.setDefault(Locale.GERMANY);
          IntervalBarChartDemo sample = new IntervalBarChartDemo();
          //File file = new File("sample.png");
          JFreeChart chart = sample.getChart();
          ChartFrame frame = new ChartFrame("Interval Bar Chart Demo", chart);
          frame.pack();
          frame.setVisible(true);
          //try {
          //     ChartUtilities.saveChartAsPNG(file, chart, 512, 385);
          //}
          //catch (IOException e) {
          //     System.err.println(e.getMessage());
          //     e.printStackTrace(System.err);
          //}
          //System.exit(0);
     }
}
