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
 * ---------------------------
 * StackedAreaXYChartDemo.java
 * ---------------------------
 * (C) Copyright 2003 by Richard Atkinson.
 *
 * Original Author:  Richard Atkinson;
 * Contributor(s):   -;
 *
 * $Id: StackedAreaXYChartDemo.java,v 1.1 2007/10/10 19:21:51 vauchers Exp $
 *
 * Changes:
 * --------
 * 27-Jul-2003 : Initial version (RA);
 * 22-Sep-2003 : Updated to use new DefaultTableXYDataset (RA);
 *
 */
package org.jfree.chart.demo;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.geom.Ellipse2D;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.labels.TimeSeriesToolTipGenerator;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.AreaXYRenderer;
import org.jfree.chart.renderer.StackedAreaXYRenderer;
import org.jfree.data.DefaultTableXYDataset;
import org.jfree.data.TableXYDataset;
import org.jfree.data.XYSeries;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;

/**
 * A simple demonstration application showing how to create a
 * stacked area XY chart.
 *
 */
public class StackedAreaXYChartDemo extends ApplicationFrame {

    /**
     * Creates a new demo.
     *
     * @param title  the frame title.
     */
    public StackedAreaXYChartDemo(String title) {

        super(title);

        SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy", Locale.UK);
        XYSeries series1 = new XYSeries("Series 1", false);
        XYSeries series2 = new XYSeries("Series 2", false);
        XYSeries series3 = new XYSeries("Series 3", false);
        try {
            series1.add(sdf.parse("03-Jul-2003").getTime(), 115);
            series1.add(sdf.parse("04-Jul-2003").getTime(), 120);
            series1.add(sdf.parse("07-Jul-2003").getTime(), 125);
            series1.add(sdf.parse("08-Jul-2003").getTime(), 160);
            series1.add(sdf.parse("09-Jul-2003").getTime(), 175);
            series1.add(sdf.parse("10-Jul-2003").getTime(), 140);
            series1.add(sdf.parse("11-Jul-2003").getTime(), 145);
            series1.add(sdf.parse("14-Jul-2003").getTime(), 150);
            series1.add(sdf.parse("15-Jul-2003").getTime(), 155);
            series1.add(sdf.parse("16-Jul-2003").getTime(), 160);
            series1.add(sdf.parse("17-Jul-2003").getTime(), 165);
            series1.add(sdf.parse("18-Jul-2003").getTime(), 170);

            series2.add(sdf.parse("30-Jun-2003").getTime(), 50);
            series2.add(sdf.parse("01-Jul-2003").getTime(), 60);
            series2.add(sdf.parse("02-Jul-2003").getTime(), 70);
            series2.add(sdf.parse("03-Jul-2003").getTime(), 80);
            series2.add(sdf.parse("04-Jul-2003").getTime(), 90);
            series2.add(sdf.parse("07-Jul-2003").getTime(), 100);
            series2.add(sdf.parse("08-Jul-2003").getTime(), 110);
            series2.add(sdf.parse("09-Jul-2003").getTime(), 120);
            series2.add(sdf.parse("10-Jul-2003").getTime(), 130);
            series2.add(sdf.parse("11-Jul-2003").getTime(), 140);
            series2.add(sdf.parse("14-Jul-2003").getTime(), 150);
            series2.add(sdf.parse("15-Jul-2003").getTime(), 160);
            series2.add(sdf.parse("16-Jul-2003").getTime(), 170);
            series2.add(sdf.parse("17-Jul-2003").getTime(), 180);
            series2.add(sdf.parse("18-Jul-2003").getTime(), 190);

            series3.add(sdf.parse("30-Jun-2003").getTime(), 100);
            series3.add(sdf.parse("01-Jul-2003").getTime(), 120);
            series3.add(sdf.parse("02-Jul-2003").getTime(), 110);
            series3.add(sdf.parse("03-Jul-2003").getTime(), 120);
            series3.add(sdf.parse("04-Jul-2003").getTime(), 130);
            series3.add(sdf.parse("07-Jul-2003").getTime(), 135);
            series3.add(sdf.parse("08-Jul-2003").getTime(), 140);
            series3.add(sdf.parse("09-Jul-2003").getTime(), 155);
            series3.add(sdf.parse("10-Jul-2003").getTime(), 130);
            series3.add(sdf.parse("11-Jul-2003").getTime(), 135);
            series3.add(sdf.parse("14-Jul-2003").getTime(), 140);
            series3.add(sdf.parse("15-Jul-2003").getTime(), 165);
            series3.add(sdf.parse("16-Jul-2003").getTime(), 170);
            series3.add(sdf.parse("17-Jul-2003").getTime(), 165);
            series3.add(sdf.parse("18-Jul-2003").getTime(), 140);
        } catch (ParseException e) {
            //  Not likely, given that the dates are hard-coded
            e.printStackTrace();
        }
        DefaultTableXYDataset dataset = new DefaultTableXYDataset(series1);
        dataset.addSeries(series2);
        dataset.addSeries(series3);

        JFreeChart chart = createChart(dataset);

        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new java.awt.Dimension(500, 270));
        setContentPane(chartPanel);

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
     * Creates a chart.
     *
     * @param dataset  the dataset.
     *
     * @return A chart.
     */
    private JFreeChart createChart(TableXYDataset dataset) {

        SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy", Locale.UK);
        TimeSeriesToolTipGenerator ttg = new TimeSeriesToolTipGenerator(
                sdf, NumberFormat.getInstance());
        DateAxis xAxis = new DateAxis("Domain (X)");
        xAxis.setLowerMargin(0.0);
        xAxis.setUpperMargin(0.0);

        NumberAxis yAxis = new NumberAxis("Range (Y)");
        yAxis.setAutoRangeIncludesZero(true);
        StackedAreaXYRenderer renderer =
                new StackedAreaXYRenderer(AreaXYRenderer.AREA_AND_SHAPES, ttg, null);
        renderer.setOutline(true);
        renderer.setSeriesPaint(0, new Color(255, 255, 206));
        renderer.setSeriesPaint(1, new Color(206, 230, 255));
        renderer.setSeriesPaint(2, new Color(255, 230, 230));
        renderer.setShapePaint(Color.gray);
        renderer.setShapeStroke(new BasicStroke(0.5f));
        renderer.setShape(new Ellipse2D.Double(-3, -3, 6, 6));
        XYPlot plot = new XYPlot(dataset, xAxis, yAxis, renderer);

        //  Reconfigure Y-Axis so the auto-range knows that the data is stacked
        yAxis.configure();

        JFreeChart chart = new JFreeChart(null, JFreeChart.DEFAULT_TITLE_FONT, plot, true);

        return chart;
    }

    /**
     * Starting point for the demonstration application.
     *
     * @param args  ignored.
     */
    public static void main(String[] args) {
        StackedAreaXYChartDemo demo = new StackedAreaXYChartDemo("Stacked Area XY Chart Demo");
        demo.pack();
        RefineryUtilities.centerFrameOnScreen(demo);
        demo.setVisible(true);
    }

}
