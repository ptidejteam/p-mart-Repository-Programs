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
 * XYStepChartDemo.java
 * --------------------
 * (C) Copyright 2002, by Roger Studner and Contributors.
 *
 * Original Author:  Roger Studner;
 * Contributor(s):   David Gilbert;
 *
 * $Id: XYStepChartDemo.java,v 1.1 2007/10/10 19:41:58 vauchers Exp $
 *
 * Changes
 * -------
 * 13-May-2002 : Version 1, contributed by Roger Studner (DG);
 *
 */
package com.jrefinery.chart.demo;

import java.awt.Color;
import java.awt.Stroke;
import java.awt.BasicStroke;
import java.util.Date;
import java.util.Calendar;
import java.util.GregorianCalendar;
import com.jrefinery.data.XYDataset;
import com.jrefinery.data.DefaultXYDataset;
import com.jrefinery.chart.JFreeChart;
import com.jrefinery.chart.ChartFrame;
import com.jrefinery.chart.XYPlot;
import com.jrefinery.chart.DateAxis;
import com.jrefinery.chart.HorizontalDateAxis;
import com.jrefinery.chart.NumberAxis;
import com.jrefinery.chart.VerticalNumberAxis;
import com.jrefinery.chart.NumberTickUnit;
import com.jrefinery.chart.XYStepRenderer;
import com.jrefinery.ui.RefineryUtilities;

/**
 * A demonstration of the XYStepRenderer class.
 */
public class XYStepChartDemo {

    /** A frame for displaying the chart. */
    private ChartFrame frame = null;

    /**
     * Creates a stepped XY plot with default settings.
     * <P>
     * Move this to the ChartFactory class?
     *
     * @param title The chart title.
     * @param xAxisLabel A label for the X-axis.
     * @param yAxisLabel A label for the Y-axis.
     * @param data The dataset for the chart.
     * @param legend A flag specifying whether or not a legend is required.
     *
     * @return A chart.
     */
    public static JFreeChart createXYStepChart(String title,
                                               String xAxisLabel,
                                               String yAxisLabel,
                                               XYDataset data,
                                               boolean legend) {

        DateAxis xAxis = new HorizontalDateAxis(xAxisLabel);
        NumberAxis yAxis = new VerticalNumberAxis(yAxisLabel);

        xAxis.setCrosshairVisible(false);

        yAxis.setAutoTickUnitSelection(false);
        yAxis.setTickUnit(new NumberTickUnit(1.0, new java.text.DecimalFormat("0")));
        yAxis.setCrosshairVisible(false);

        XYPlot plot = new XYPlot(data, xAxis, yAxis);
        Stroke[] s = new BasicStroke[] {new BasicStroke((float)2.0), new BasicStroke((float)2.0)};
        plot.setSeriesStroke(s);
        plot.setXYItemRenderer(new XYStepRenderer());
        JFreeChart chart = new JFreeChart(title, JFreeChart.DEFAULT_TITLE_FONT, plot, legend);
        return chart;

    }

    /**
     * Displays an XYPlot in its own frame.
     */
    private void displayXYPlot() {

        if (frame==null) {

            // create a default chart based on some sample data...
            String title = "LCACs in use at given time";
            String xAxisLabel = "Time";
            String yAxisLabel = "Number of Transports";
            XYDataset data1 = createTestXYDataset();

            JFreeChart chart = this.createXYStepChart(title, xAxisLabel, yAxisLabel, data1, true);

            // then customise it a little...
            chart.setBackgroundPaint(new Color(216, 216, 216));

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
     * Returns a java.util.Date for the specified year, month, day, hour and minute.
     */
    private Date createDateTime(int year, int month, int day, int hour, int minute) {
        GregorianCalendar calendar = new GregorianCalendar(year, month, day, hour, minute);
        return calendar.getTime();
    }

    /**
     * Creates and returns a XYDataset for the demo charts.
     */
    public XYDataset createTestXYDataset() {

        String[] sNames = {"Plan 1", "Plan 2"};
        DefaultXYDataset d;
        Object[][][] data = new Object[][][]
        { {
            { createDateTime(2002, Calendar.FEBRUARY, 19, 8, 0), new Integer(0) },
            { createDateTime(2002, Calendar.FEBRUARY, 19, 8, 0), new Integer(2) },
            { createDateTime(2002, Calendar.FEBRUARY, 19, 9, 5), new Integer(4) },
            { createDateTime(2002, Calendar.FEBRUARY, 19, 10, 6), new Integer(4) },
            { createDateTime(2002, Calendar.FEBRUARY, 19, 11, 6), new Integer(5) },
            { createDateTime(2002, Calendar.FEBRUARY, 19, 12, 6), new Integer(3) },
            { createDateTime(2002, Calendar.FEBRUARY, 19, 13, 6), new Integer(6) },
            { createDateTime(2002, Calendar.FEBRUARY, 19, 14, 6), new Integer(6) },
            { createDateTime(2002, Calendar.FEBRUARY, 19, 15, 30), new Integer(2) },
            { createDateTime(2002, Calendar.FEBRUARY, 19, 16, 7), new Integer(0) }
          },
          {
            { createDateTime(2002, Calendar.FEBRUARY, 19, 8, 45), new Integer(0) },
            { createDateTime(2002, Calendar.FEBRUARY, 19, 8, 45), new Integer(1) },
            { createDateTime(2002, Calendar.FEBRUARY, 19, 9, 0), new Integer(6) },
            { createDateTime(2002, Calendar.FEBRUARY, 19, 10, 6), new Integer(2) },
            { createDateTime(2002, Calendar.FEBRUARY, 19, 10, 45), new Integer(4) },
            { createDateTime(2002, Calendar.FEBRUARY, 19, 12, 0), new Integer(7) },
            { createDateTime(2002, Calendar.FEBRUARY, 19, 13, 0), new Integer(5) },
            { createDateTime(2002, Calendar.FEBRUARY, 19, 14, 6), new Integer(4) },
            { createDateTime(2002, Calendar.FEBRUARY, 19, 15, 15), new Integer(4) },
            { createDateTime(2002, Calendar.FEBRUARY, 19, 16, 0), new Integer(0) },
          }
        };

        d = new DefaultXYDataset(data);
        d.setSeriesNames(sNames);
        return d;
    }



    /**
     * The starting point for the demonstration application.
     */
    public static void main(String[] args) {

        XYStepChartDemo demo = new XYStepChartDemo();
        demo.displayXYPlot();

    }

}