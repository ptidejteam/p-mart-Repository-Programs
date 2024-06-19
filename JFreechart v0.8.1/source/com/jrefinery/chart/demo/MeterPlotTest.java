package com.jrefinery.chart.demo;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.awt.print.*;
import java.io.*;
import java.util.*;
import javax.swing.*;
import com.jrefinery.chart.demo.*;
import com.jrefinery.data.*;
import com.jrefinery.layout.*;
import com.jrefinery.chart.*;
import com.jrefinery.chart.combination.*;
import com.jrefinery.chart.demo.*;
import com.jrefinery.chart.data.*;
import com.jrefinery.data.*;
import com.jrefinery.date.*;
import com.jrefinery.chart.ui.*;
import com.jrefinery.ui.*;

/**
 * The main frame in the chart demonstration application.
 */
public class MeterPlotTest {

    void displayMeterChart(double value) {

        JFreeChart chart = null;

        String title = "Meter Chart";
        DefaultMeterDataset data = new DefaultMeterDataset(new Double(23), new Double(140),
                                                           new Double(value) );
        data.setWarningRange(new Double(70), new Double(85));
        data.setCriticalRange( new Double(85), new Double(100));
        MeterPlot plot = new MeterPlot();

        plot.setInsets(new Insets(5, 5, 5, 5));
        chart = new JFreeChart(data, plot, title, JFreeChart.DEFAULT_TITLE_FONT, false);

        chart.setBackgroundPaint(new GradientPaint(0, 0, Color.white, 0, 1000, Color.orange));

        JFrame chartFrame = new JFreeChartFrame("Meter Chart", chart);
        chartFrame.pack();
        JRefineryUtilities.positionFrameRandomly(chartFrame);
        chartFrame.setSize(250, 250);
        chartFrame.show();

   }

    /**
     * Starting point for the Meter Plot demonstration application.
     */
    public static void main(String[] args) {

      MeterPlotTest h = new MeterPlotTest();
      double val = 50;
      if (args.length > 0) {
          val = new Double(args[0]).doubleValue();
      }
      h.displayMeterChart(val);

    }

}
