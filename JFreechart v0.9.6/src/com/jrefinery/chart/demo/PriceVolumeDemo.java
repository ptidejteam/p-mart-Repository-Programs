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
 * --------------------
 * PriceVolumeDemo.java
 * --------------------
 * (C) Copyright 2002, 2003, by Simba Management Limited.
 *
 * Original Author:  David Gilbert (for Simba Management Limited).
 * Contributor(s):   -;
 *
 * $Id: PriceVolumeDemo.java,v 1.1 2007/10/10 19:57:52 vauchers Exp $
 *
 * Changes
 * -------
 * 28-Mar-2002 : Version 1 (DG);
 * 23-Apr-2002 : Modified to use new CombinedXYPlot class (DG);
 * 25-Jun-2002 : Removed unnecessary imports (DG);
 * 11-Oct-2002 : Fixed errors reported by Checkstyle (DG);
 * 21-Nov-2002 : Implemented with dual axes, and used sample data from Chicago Mercantile
 *               Exchange (http://www.cme.com) (DG);
 *
 */

package com.jrefinery.chart.demo;

import java.text.DecimalFormat;
import com.jrefinery.data.TimeSeries;
import com.jrefinery.data.TimeSeriesCollection;
import com.jrefinery.data.Day;
import com.jrefinery.data.XYDataset;
import com.jrefinery.data.IntervalXYDataset;
import com.jrefinery.date.SerialDate;
import com.jrefinery.chart.JFreeChart;
import com.jrefinery.chart.ChartFactory;
import com.jrefinery.chart.ChartPanel;
import com.jrefinery.chart.axis.NumberAxis;
import com.jrefinery.chart.axis.VerticalNumberAxis;
import com.jrefinery.chart.plot.XYPlot;
import com.jrefinery.chart.renderer.DefaultDrawingSupplier;
import com.jrefinery.chart.renderer.DrawingSupplier;
import com.jrefinery.chart.renderer.XYItemRenderer;
import com.jrefinery.chart.renderer.VerticalXYBarRenderer;
import com.jrefinery.chart.tooltips.TimeSeriesToolTipGenerator;
import com.jrefinery.ui.ApplicationFrame;
import com.jrefinery.ui.RefineryUtilities;

/**
 * A demonstration application showing how to create a price-volume chart.
 *
 * @author David Gilbert
 */
public class PriceVolumeDemo extends ApplicationFrame {

    /**
     * Constructs a new demonstration application.
     *
     * @param title  the frame title.
     */
    public PriceVolumeDemo(String title) {

        super(title);
        JFreeChart chart = createChart();
        ChartPanel panel = new ChartPanel(chart, true, true, true, false, true);
        panel.setPreferredSize(new java.awt.Dimension(500, 270));
        setContentPane(panel);

    }

    /**
     * Creates a chart.
     *
     * @return a chart.
     */
    private JFreeChart createChart() {

        DrawingSupplier supplier = new DefaultDrawingSupplier();
        
        XYDataset priceData = createPriceDataset();
        String title = "Eurodollar Futures Contract (MAR03)";
        JFreeChart chart = ChartFactory.createTimeSeriesChart(title, "Date", "Price",
                                                              priceData, true,
                                                              true,
                                                              false);
        XYPlot plot = chart.getXYPlot();
        NumberAxis rangeAxis1 = (NumberAxis) plot.getRangeAxis();
        rangeAxis1.setLowerMargin(0.40);  // to leave room for volume bars
        DecimalFormat format = new DecimalFormat("00.00");
        rangeAxis1.setNumberFormatOverride(format);

        XYItemRenderer renderer1 = plot.getRenderer();
        renderer1.setToolTipGenerator(new TimeSeriesToolTipGenerator("d-MMM-yyyy", "00.00"));
        renderer1.setDrawingSupplier(supplier);
        
        VerticalNumberAxis rangeAxis2 = new VerticalNumberAxis("Volume");
        rangeAxis2.setUpperMargin(1.00);  // to leave room for price line
        plot.setSecondaryRangeAxis(rangeAxis2);
        plot.setSecondaryDataset(createVolumeDataset());
        plot.setSecondaryRangeAxis(rangeAxis2);
        VerticalXYBarRenderer renderer2 = new VerticalXYBarRenderer(0.20);
        renderer2.setToolTipGenerator(new TimeSeriesToolTipGenerator("d-MMM-yyyy", "0,000.00"));
        renderer2.setDrawingSupplier(supplier);
        plot.setSecondaryRenderer(renderer2);
        return chart;

    }

    /**
     * Creates a sample dataset.
     *
     * @return A sample dataset.
     */
    private XYDataset createPriceDataset() {

        // create dataset 1...
        TimeSeries series1 = new TimeSeries("Price", Day.class);

        series1.add(new Day(2, SerialDate.JANUARY, 2002), 95.565);
        series1.add(new Day(3, SerialDate.JANUARY, 2002), 95.640);
        series1.add(new Day(4, SerialDate.JANUARY, 2002), 95.710);

        series1.add(new Day(7, SerialDate.JANUARY, 2002), 95.930);
        series1.add(new Day(8, SerialDate.JANUARY, 2002), 95.930);
        series1.add(new Day(9, SerialDate.JANUARY, 2002), 95.960);
        series1.add(new Day(10, SerialDate.JANUARY, 2002), 96.055);
        series1.add(new Day(11, SerialDate.JANUARY, 2002), 96.335);

        series1.add(new Day(14, SerialDate.JANUARY, 2002), 96.290);
        series1.add(new Day(15, SerialDate.JANUARY, 2002), 96.275);
        series1.add(new Day(16, SerialDate.JANUARY, 2002), 96.240);
        series1.add(new Day(17, SerialDate.JANUARY, 2002), 96.080);
        series1.add(new Day(18, SerialDate.JANUARY, 2002), 96.145);

        series1.add(new Day(22, SerialDate.JANUARY, 2002), 96.120);
        series1.add(new Day(23, SerialDate.JANUARY, 2002), 96.015);
        series1.add(new Day(24, SerialDate.JANUARY, 2002), 95.890);
        series1.add(new Day(25, SerialDate.JANUARY, 2002), 95.8650);

        series1.add(new Day(28, SerialDate.JANUARY, 2002), 95.880);
        series1.add(new Day(29, SerialDate.JANUARY, 2002), 96.050);
        series1.add(new Day(30, SerialDate.JANUARY, 2002), 96.065);
        series1.add(new Day(31, SerialDate.JANUARY, 2002), 95.910);
        series1.add(new Day(1, SerialDate.FEBRUARY, 2002), 96.015);

        series1.add(new Day(4, SerialDate.FEBRUARY, 2002), 96.140);
        series1.add(new Day(5, SerialDate.FEBRUARY, 2002), 96.195);
        series1.add(new Day(6, SerialDate.FEBRUARY, 2002), 96.245);
        series1.add(new Day(7, SerialDate.FEBRUARY, 2002), 96.220);
        series1.add(new Day(8, SerialDate.FEBRUARY, 2002), 96.280);

        series1.add(new Day(11, SerialDate.FEBRUARY, 2002), 96.265);
        series1.add(new Day(12, SerialDate.FEBRUARY, 2002), 96.160);
        series1.add(new Day(13, SerialDate.FEBRUARY, 2002), 96.120);
        series1.add(new Day(14, SerialDate.FEBRUARY, 2002), 96.125);
        series1.add(new Day(15, SerialDate.FEBRUARY, 2002), 96.265);

        series1.add(new Day(19, SerialDate.FEBRUARY, 2002), 96.290);
        series1.add(new Day(20, SerialDate.FEBRUARY, 2002), 96.275);
        series1.add(new Day(21, SerialDate.FEBRUARY, 2002), 96.280);
        series1.add(new Day(22, SerialDate.FEBRUARY, 2002), 96.305);

        series1.add(new Day(25, SerialDate.FEBRUARY, 2002), 96.265);
        series1.add(new Day(26, SerialDate.FEBRUARY, 2002), 96.185);
        series1.add(new Day(27, SerialDate.FEBRUARY, 2002), 96.305);
        series1.add(new Day(28, SerialDate.FEBRUARY, 2002), 96.215);
        series1.add(new Day(1, SerialDate.MARCH, 2002), 96.015);

        series1.add(new Day(4, SerialDate.MARCH, 2002), 95.970);
        series1.add(new Day(5, SerialDate.MARCH, 2002), 95.935);
        series1.add(new Day(6, SerialDate.MARCH, 2002), 95.935);
        series1.add(new Day(7, SerialDate.MARCH, 2002), 95.705);
        series1.add(new Day(8, SerialDate.MARCH, 2002), 95.4850);

        series1.add(new Day(11, SerialDate.MARCH, 2002), 95.505);
        series1.add(new Day(12, SerialDate.MARCH, 2002), 95.540);
        series1.add(new Day(13, SerialDate.MARCH, 2002), 95.675);
        series1.add(new Day(14, SerialDate.MARCH, 2002), 95.510);
        series1.add(new Day(15, SerialDate.MARCH, 2002), 95.500);

        series1.add(new Day(18, SerialDate.MARCH, 2002), 95.500);
        series1.add(new Day(19, SerialDate.MARCH, 2002), 95.535);
        series1.add(new Day(20, SerialDate.MARCH, 2002), 95.420);
        series1.add(new Day(21, SerialDate.MARCH, 2002), 95.400);
        series1.add(new Day(22, SerialDate.MARCH, 2002), 95.375);

        series1.add(new Day(25, SerialDate.MARCH, 2002), 95.350);
        series1.add(new Day(26, SerialDate.MARCH, 2002), 95.505);
        series1.add(new Day(27, SerialDate.MARCH, 2002), 95.550);
        series1.add(new Day(28, SerialDate.MARCH, 2002), 95.485);

        series1.add(new Day(1, SerialDate.APRIL, 2002), 95.485);
        series1.add(new Day(2, SerialDate.APRIL, 2002), 95.630);
        series1.add(new Day(3, SerialDate.APRIL, 2002), 95.735);
        series1.add(new Day(4, SerialDate.APRIL, 2002), 95.695);
        series1.add(new Day(5, SerialDate.APRIL, 2002), 95.810);

        series1.add(new Day(8, SerialDate.APRIL, 2002), 95.810);
        series1.add(new Day(9, SerialDate.APRIL, 2002), 95.865);
        series1.add(new Day(10, SerialDate.APRIL, 2002), 95.885);
        series1.add(new Day(11, SerialDate.APRIL, 2002), 95.900);
        series1.add(new Day(12, SerialDate.APRIL, 2002), 95.980);

        series1.add(new Day(15, SerialDate.APRIL, 2002), 96.035);
        series1.add(new Day(16, SerialDate.APRIL, 2002), 96.000);
        series1.add(new Day(17, SerialDate.APRIL, 2002), 96.035);
        series1.add(new Day(18, SerialDate.APRIL, 2002), 96.085);
        series1.add(new Day(19, SerialDate.APRIL, 2002), 96.0750);

        series1.add(new Day(22, SerialDate.APRIL, 2002), 96.105);
        series1.add(new Day(23, SerialDate.APRIL, 2002), 96.075);
        series1.add(new Day(24, SerialDate.APRIL, 2002), 96.210);
        series1.add(new Day(25, SerialDate.APRIL, 2002), 96.255);
        series1.add(new Day(26, SerialDate.APRIL, 2002), 96.310);

        series1.add(new Day(29, SerialDate.APRIL, 2002), 96.310);
        series1.add(new Day(30, SerialDate.APRIL, 2002), 96.325);
        series1.add(new Day(1, SerialDate.MAY, 2002), 96.345);
        series1.add(new Day(2, SerialDate.MAY, 2002), 96.285);
        series1.add(new Day(3, SerialDate.MAY, 2002), 96.385);

        series1.add(new Day(6, SerialDate.MAY, 2002), 96.380);
        series1.add(new Day(7, SerialDate.MAY, 2002), 96.485);
        series1.add(new Day(8, SerialDate.MAY, 2002), 96.230);
        series1.add(new Day(9, SerialDate.MAY, 2002), 96.310);
        series1.add(new Day(10, SerialDate.MAY, 2002), 96.445);

        series1.add(new Day(13, SerialDate.MAY, 2002), 96.355);
        series1.add(new Day(14, SerialDate.MAY, 2002), 96.180);
        series1.add(new Day(15, SerialDate.MAY, 2002), 96.240);
        series1.add(new Day(16, SerialDate.MAY, 2002), 96.325);
        series1.add(new Day(17, SerialDate.MAY, 2002), 96.200);

        series1.add(new Day(20, SerialDate.MAY, 2002), 96.305);
        series1.add(new Day(21, SerialDate.MAY, 2002), 96.385);
        series1.add(new Day(22, SerialDate.MAY, 2002), 96.445);
        series1.add(new Day(23, SerialDate.MAY, 2002), 96.385);
        series1.add(new Day(24, SerialDate.MAY, 2002), 96.390);

        series1.add(new Day(28, SerialDate.MAY, 2002), 96.390);
        series1.add(new Day(29, SerialDate.MAY, 2002), 96.475);
        series1.add(new Day(30, SerialDate.MAY, 2002), 96.555);
        series1.add(new Day(31, SerialDate.MAY, 2002), 96.500);

        series1.add(new Day(3, SerialDate.JUNE, 2002), 96.540);
        series1.add(new Day(4, SerialDate.JUNE, 2002), 96.605);
        series1.add(new Day(5, SerialDate.JUNE, 2002), 96.580);
        series1.add(new Day(6, SerialDate.JUNE, 2002), 96.610);
        series1.add(new Day(7, SerialDate.JUNE, 2002), 96.600);

        series1.add(new Day(10, SerialDate.JUNE, 2002), 96.615);
        series1.add(new Day(11, SerialDate.JUNE, 2002), 96.705);
        series1.add(new Day(12, SerialDate.JUNE, 2002), 96.750);
        series1.add(new Day(13, SerialDate.JUNE, 2002), 96.830);
        series1.add(new Day(14, SerialDate.JUNE, 2002), 96.965);

        series1.add(new Day(17, SerialDate.JUNE, 2002), 96.945);
        series1.add(new Day(18, SerialDate.JUNE, 2002), 96.990);
        series1.add(new Day(19, SerialDate.JUNE, 2002), 97.165);
        series1.add(new Day(20, SerialDate.JUNE, 2002), 97.030);
        series1.add(new Day(21, SerialDate.JUNE, 2002), 97.145);

        series1.add(new Day(24, SerialDate.JUNE, 2002), 97.120);
        series1.add(new Day(25, SerialDate.JUNE, 2002), 97.175);
        series1.add(new Day(26, SerialDate.JUNE, 2002), 97.365);
        series1.add(new Day(27, SerialDate.JUNE, 2002), 97.245);
        series1.add(new Day(28, SerialDate.JUNE, 2002), 97.245);

        series1.add(new Day(1, SerialDate.JULY, 2002), 97.290);
        series1.add(new Day(2, SerialDate.JULY, 2002), 97.380);
        series1.add(new Day(3, SerialDate.JULY, 2002), 97.380);

        series1.add(new Day(5, SerialDate.JULY, 2002), 97.220);

        series1.add(new Day(8, SerialDate.JULY, 2002), 97.325);
        series1.add(new Day(9, SerialDate.JULY, 2002), 97.455);
        series1.add(new Day(10, SerialDate.JULY, 2002), 97.580);
        series1.add(new Day(11, SerialDate.JULY, 2002), 97.605);
        series1.add(new Day(12, SerialDate.JULY, 2002), 97.690);

        series1.add(new Day(15, SerialDate.JULY, 2002), 97.730);
        series1.add(new Day(16, SerialDate.JULY, 2002), 97.580);
        series1.add(new Day(17, SerialDate.JULY, 2002), 97.640);
        series1.add(new Day(18, SerialDate.JULY, 2002), 97.680);
        series1.add(new Day(19, SerialDate.JULY, 2002), 97.715);

        series1.add(new Day(22, SerialDate.JULY, 2002), 97.815);
        series1.add(new Day(23, SerialDate.JULY, 2002), 97.875);
        series1.add(new Day(24, SerialDate.JULY, 2002), 97.835);
        series1.add(new Day(25, SerialDate.JULY, 2002), 97.925);
        series1.add(new Day(26, SerialDate.JULY, 2002), 97.960);

        series1.add(new Day(29, SerialDate.JULY, 2002), 97.745);
        series1.add(new Day(30, SerialDate.JULY, 2002), 97.710);
        series1.add(new Day(31, SerialDate.JULY, 2002), 97.930);
        series1.add(new Day(1, SerialDate.AUGUST, 2002), 98.000);
        series1.add(new Day(2, SerialDate.AUGUST, 2002), 98.170);

        series1.add(new Day(5, SerialDate.AUGUST, 2002), 98.225);
        series1.add(new Day(6, SerialDate.AUGUST, 2002), 98.115);
        series1.add(new Day(7, SerialDate.AUGUST, 2002), 98.265);
        series1.add(new Day(8, SerialDate.AUGUST, 2002), 98.180);
        series1.add(new Day(9, SerialDate.AUGUST, 2002), 98.185);

        series1.add(new Day(12, SerialDate.AUGUST, 2002), 98.150);
        series1.add(new Day(13, SerialDate.AUGUST, 2002), 98.290);
        series1.add(new Day(14, SerialDate.AUGUST, 2002), 98.155);
        series1.add(new Day(15, SerialDate.AUGUST, 2002), 98.075);
        series1.add(new Day(16, SerialDate.AUGUST, 2002), 98.000);

        series1.add(new Day(19, SerialDate.AUGUST, 2002), 98.040);
        series1.add(new Day(20, SerialDate.AUGUST, 2002), 98.135);
        series1.add(new Day(21, SerialDate.AUGUST, 2002), 98.110);
        series1.add(new Day(22, SerialDate.AUGUST, 2002), 98.005);
        series1.add(new Day(23, SerialDate.AUGUST, 2002), 98.055);

        series1.add(new Day(26, SerialDate.AUGUST, 2002), 98.065);
        series1.add(new Day(27, SerialDate.AUGUST, 2002), 97.980);
        series1.add(new Day(28, SerialDate.AUGUST, 2002), 98.035);
        series1.add(new Day(29, SerialDate.AUGUST, 2002), 98.095);
        series1.add(new Day(30, SerialDate.AUGUST, 2002), 98.060);

        series1.add(new Day(3, SerialDate.SEPTEMBER, 2002), 98.250);
        series1.add(new Day(4, SerialDate.SEPTEMBER, 2002), 98.245);
        series1.add(new Day(5, SerialDate.SEPTEMBER, 2002), 98.315);
        series1.add(new Day(6, SerialDate.SEPTEMBER, 2002), 98.170);

        series1.add(new Day(9, SerialDate.SEPTEMBER, 2002), 98.080);
        series1.add(new Day(10, SerialDate.SEPTEMBER, 2002), 98.090);
        series1.add(new Day(11, SerialDate.SEPTEMBER, 2002), 98.030);
        series1.add(new Day(12, SerialDate.SEPTEMBER, 2002), 98.105);
        series1.add(new Day(13, SerialDate.SEPTEMBER, 2002), 98.135);

        series1.add(new Day(16, SerialDate.SEPTEMBER, 2002), 98.115);
        series1.add(new Day(17, SerialDate.SEPTEMBER, 2002), 98.125);
        series1.add(new Day(18, SerialDate.SEPTEMBER, 2002), 98.130);
        series1.add(new Day(19, SerialDate.SEPTEMBER, 2002), 98.255);
        series1.add(new Day(20, SerialDate.SEPTEMBER, 2002), 98.255);

        series1.add(new Day(23, SerialDate.SEPTEMBER, 2002), 98.280);
        series1.add(new Day(24, SerialDate.SEPTEMBER, 2002), 98.310);
        series1.add(new Day(25, SerialDate.SEPTEMBER, 2002), 98.250);
        series1.add(new Day(26, SerialDate.SEPTEMBER, 2002), 98.300);
        series1.add(new Day(27, SerialDate.SEPTEMBER, 2002), 98.410);

        series1.add(new Day(30, SerialDate.SEPTEMBER, 2002), 98.495);
        series1.add(new Day(1, SerialDate.OCTOBER, 2002), 98.440);
        series1.add(new Day(2, SerialDate.OCTOBER, 2002), 98.440);
        series1.add(new Day(3, SerialDate.OCTOBER, 2002), 98.440);
        series1.add(new Day(4, SerialDate.OCTOBER, 2002), 98.380);

        series1.add(new Day(7, SerialDate.OCTOBER, 2002), 98.385);
        series1.add(new Day(8, SerialDate.OCTOBER, 2002), 98.340);
        series1.add(new Day(9, SerialDate.OCTOBER, 2002), 98.420);
        series1.add(new Day(10, SerialDate.OCTOBER, 2002), 98.375);
        series1.add(new Day(11, SerialDate.OCTOBER, 2002), 98.275);

        series1.add(new Day(14, SerialDate.OCTOBER, 2002), 98.275);
        series1.add(new Day(15, SerialDate.OCTOBER, 2002), 98.135);
        series1.add(new Day(16, SerialDate.OCTOBER, 2002), 98.165);
        series1.add(new Day(17, SerialDate.OCTOBER, 2002), 98.170);
        series1.add(new Day(18, SerialDate.OCTOBER, 2002), 98.165);

        series1.add(new Day(21, SerialDate.OCTOBER, 2002), 98.105);
        series1.add(new Day(22, SerialDate.OCTOBER, 2002), 98.125);
        series1.add(new Day(23, SerialDate.OCTOBER, 2002), 98.185);
        series1.add(new Day(24, SerialDate.OCTOBER, 2002), 98.245);
        series1.add(new Day(25, SerialDate.OCTOBER, 2002), 98.320);

        series1.add(new Day(28, SerialDate.OCTOBER, 2002), 98.420);
        series1.add(new Day(29, SerialDate.OCTOBER, 2002), 98.540);
        series1.add(new Day(30, SerialDate.OCTOBER, 2002), 98.545);
        series1.add(new Day(31, SerialDate.OCTOBER, 2002), 98.560);

        return new TimeSeriesCollection(series1);

    }

    /**
     * Creates a sample dataset.
     *
     * @return A sample dataset.
     */
    private IntervalXYDataset createVolumeDataset() {

        // create dataset 2...
        TimeSeries series1 = new TimeSeries("Volume", Day.class);

        series1.add(new Day(2, SerialDate.JANUARY, 2002), 41020);
        series1.add(new Day(3, SerialDate.JANUARY, 2002), 45586);
        series1.add(new Day(4, SerialDate.JANUARY, 2002), 81672);

        series1.add(new Day(7, SerialDate.JANUARY, 2002), 81975);
        series1.add(new Day(8, SerialDate.JANUARY, 2002), 79692);
        series1.add(new Day(9, SerialDate.JANUARY, 2002), 53187);
        series1.add(new Day(10, SerialDate.JANUARY, 2002), 87929);
        series1.add(new Day(11, SerialDate.JANUARY, 2002), 107047);

        series1.add(new Day(14, SerialDate.JANUARY, 2002), 86276);
        series1.add(new Day(15, SerialDate.JANUARY, 2002), 79005);
        series1.add(new Day(16, SerialDate.JANUARY, 2002), 80632);
        series1.add(new Day(17, SerialDate.JANUARY, 2002), 88797);
        series1.add(new Day(18, SerialDate.JANUARY, 2002), 57179);

        series1.add(new Day(22, SerialDate.JANUARY, 2002), 36611);
        series1.add(new Day(23, SerialDate.JANUARY, 2002), 57063);
        series1.add(new Day(24, SerialDate.JANUARY, 2002), 101938);
        series1.add(new Day(25, SerialDate.JANUARY, 2002), 87177);

        series1.add(new Day(28, SerialDate.JANUARY, 2002), 39831);
        series1.add(new Day(29, SerialDate.JANUARY, 2002), 67654);
        series1.add(new Day(30, SerialDate.JANUARY, 2002), 81162);
        series1.add(new Day(31, SerialDate.JANUARY, 2002), 64923);
        series1.add(new Day(1, SerialDate.FEBRUARY, 2002), 73481);

        series1.add(new Day(4, SerialDate.FEBRUARY, 2002), 54723);
        series1.add(new Day(5, SerialDate.FEBRUARY, 2002), 76708);
        series1.add(new Day(6, SerialDate.FEBRUARY, 2002), 81281);
        series1.add(new Day(7, SerialDate.FEBRUARY, 2002), 66553);
        series1.add(new Day(8, SerialDate.FEBRUARY, 2002), 53592);

        series1.add(new Day(11, SerialDate.FEBRUARY, 2002), 29410);
        series1.add(new Day(12, SerialDate.FEBRUARY, 2002), 60345);
        series1.add(new Day(13, SerialDate.FEBRUARY, 2002), 67339);
        series1.add(new Day(14, SerialDate.FEBRUARY, 2002), 40057);
        series1.add(new Day(15, SerialDate.FEBRUARY, 2002), 67865);

        series1.add(new Day(19, SerialDate.FEBRUARY, 2002), 58628);
        series1.add(new Day(20, SerialDate.FEBRUARY, 2002), 52109);
        series1.add(new Day(21, SerialDate.FEBRUARY, 2002), 50195);
        series1.add(new Day(22, SerialDate.FEBRUARY, 2002), 47806);

        series1.add(new Day(25, SerialDate.FEBRUARY, 2002), 31711);
        series1.add(new Day(26, SerialDate.FEBRUARY, 2002), 88328);
        series1.add(new Day(27, SerialDate.FEBRUARY, 2002), 95805);
        series1.add(new Day(28, SerialDate.FEBRUARY, 2002), 84035);
        series1.add(new Day(1, SerialDate.MARCH, 2002), 113584);

        series1.add(new Day(4, SerialDate.MARCH, 2002), 71872);
        series1.add(new Day(5, SerialDate.MARCH, 2002), 83016);
        series1.add(new Day(6, SerialDate.MARCH, 2002), 62273);
        series1.add(new Day(7, SerialDate.MARCH, 2002), 138508);
        series1.add(new Day(8, SerialDate.MARCH, 2002), 139428);

        series1.add(new Day(11, SerialDate.MARCH, 2002), 80232);
        series1.add(new Day(12, SerialDate.MARCH, 2002), 75693);
        series1.add(new Day(13, SerialDate.MARCH, 2002), 104068);
        series1.add(new Day(14, SerialDate.MARCH, 2002), 72171);
        series1.add(new Day(15, SerialDate.MARCH, 2002), 117262);

        series1.add(new Day(18, SerialDate.MARCH, 2002), 66048);
        series1.add(new Day(19, SerialDate.MARCH, 2002), 87079);
        series1.add(new Day(20, SerialDate.MARCH, 2002), 116084);
        series1.add(new Day(21, SerialDate.MARCH, 2002), 113206);
        series1.add(new Day(22, SerialDate.MARCH, 2002), 68326);

        series1.add(new Day(25, SerialDate.MARCH, 2002), 34340);
        series1.add(new Day(26, SerialDate.MARCH, 2002), 104413);
        series1.add(new Day(27, SerialDate.MARCH, 2002), 57277);
        series1.add(new Day(28, SerialDate.MARCH, 2002), 69936);

        series1.add(new Day(1, SerialDate.APRIL, 2002), 57282);
        series1.add(new Day(2, SerialDate.APRIL, 2002), 74686);
        series1.add(new Day(3, SerialDate.APRIL, 2002), 108601);
        series1.add(new Day(4, SerialDate.APRIL, 2002), 123381);
        series1.add(new Day(5, SerialDate.APRIL, 2002), 106691);

        series1.add(new Day(8, SerialDate.APRIL, 2002), 118535);
        series1.add(new Day(9, SerialDate.APRIL, 2002), 85577);
        series1.add(new Day(10, SerialDate.APRIL, 2002), 75441);
        series1.add(new Day(11, SerialDate.APRIL, 2002), 88845);
        series1.add(new Day(12, SerialDate.APRIL, 2002), 137141);

        series1.add(new Day(15, SerialDate.APRIL, 2002), 72518);
        series1.add(new Day(16, SerialDate.APRIL, 2002), 122100);
        series1.add(new Day(17, SerialDate.APRIL, 2002), 136419);
        series1.add(new Day(18, SerialDate.APRIL, 2002), 141338);
        series1.add(new Day(19, SerialDate.APRIL, 2002), 80274);

        series1.add(new Day(22, SerialDate.APRIL, 2002), 40449);
        series1.add(new Day(23, SerialDate.APRIL, 2002), 72292);
        series1.add(new Day(24, SerialDate.APRIL, 2002), 110644);
        series1.add(new Day(25, SerialDate.APRIL, 2002), 145142);
        series1.add(new Day(26, SerialDate.APRIL, 2002), 139573);

        series1.add(new Day(29, SerialDate.APRIL, 2002), 51509);
        series1.add(new Day(30, SerialDate.APRIL, 2002), 105782);
        series1.add(new Day(1, SerialDate.MAY, 2002), 170680);
        series1.add(new Day(2, SerialDate.MAY, 2002), 140800);
        series1.add(new Day(3, SerialDate.MAY, 2002), 170411);

        series1.add(new Day(6, SerialDate.MAY, 2002), 46172);
        series1.add(new Day(7, SerialDate.MAY, 2002), 137251);
        series1.add(new Day(8, SerialDate.MAY, 2002), 220626);
        series1.add(new Day(9, SerialDate.MAY, 2002), 175902);
        series1.add(new Day(10, SerialDate.MAY, 2002), 128807);

        series1.add(new Day(13, SerialDate.MAY, 2002), 78208);
        series1.add(new Day(14, SerialDate.MAY, 2002), 212048);
        series1.add(new Day(15, SerialDate.MAY, 2002), 145643);
        series1.add(new Day(16, SerialDate.MAY, 2002), 121520);
        series1.add(new Day(17, SerialDate.MAY, 2002), 147820);

        series1.add(new Day(20, SerialDate.MAY, 2002), 75969);
        series1.add(new Day(21, SerialDate.MAY, 2002), 118970);
        series1.add(new Day(22, SerialDate.MAY, 2002), 131013);
        series1.add(new Day(23, SerialDate.MAY, 2002), 141100);
        series1.add(new Day(24, SerialDate.MAY, 2002), 63606);

        series1.add(new Day(28, SerialDate.MAY, 2002), 78687);
        series1.add(new Day(29, SerialDate.MAY, 2002), 86743);
        series1.add(new Day(30, SerialDate.MAY, 2002), 164376);
        series1.add(new Day(31, SerialDate.MAY, 2002), 150108);

        series1.add(new Day(3, SerialDate.JUNE, 2002), 132363);
        series1.add(new Day(4, SerialDate.JUNE, 2002), 144902);
        series1.add(new Day(5, SerialDate.JUNE, 2002), 123834);
        series1.add(new Day(6, SerialDate.JUNE, 2002), 125004);
        series1.add(new Day(7, SerialDate.JUNE, 2002), 165049);

        series1.add(new Day(10, SerialDate.JUNE, 2002), 88069);
        series1.add(new Day(11, SerialDate.JUNE, 2002), 114146);
        series1.add(new Day(12, SerialDate.JUNE, 2002), 149992);
        series1.add(new Day(13, SerialDate.JUNE, 2002), 191261);
        series1.add(new Day(14, SerialDate.JUNE, 2002), 207444);

        series1.add(new Day(17, SerialDate.JUNE, 2002), 117081);
        series1.add(new Day(18, SerialDate.JUNE, 2002), 135924);
        series1.add(new Day(19, SerialDate.JUNE, 2002), 179654);
        series1.add(new Day(20, SerialDate.JUNE, 2002), 260936);
        series1.add(new Day(21, SerialDate.JUNE, 2002), 140283);

        series1.add(new Day(24, SerialDate.JUNE, 2002), 199052);
        series1.add(new Day(25, SerialDate.JUNE, 2002), 191804);
        series1.add(new Day(26, SerialDate.JUNE, 2002), 384936);
        series1.add(new Day(27, SerialDate.JUNE, 2002), 313065);
        series1.add(new Day(28, SerialDate.JUNE, 2002), 169963);

        series1.add(new Day(1, SerialDate.JULY, 2002), 109906);
        series1.add(new Day(2, SerialDate.JULY, 2002), 140644);
        series1.add(new Day(3, SerialDate.JULY, 2002), 150898);

        series1.add(new Day(5, SerialDate.JULY, 2002), 181355);

        series1.add(new Day(8, SerialDate.JULY, 2002), 155042);
        series1.add(new Day(9, SerialDate.JULY, 2002), 204305);
        series1.add(new Day(10, SerialDate.JULY, 2002), 300113);
        series1.add(new Day(11, SerialDate.JULY, 2002), 338948);
        series1.add(new Day(12, SerialDate.JULY, 2002), 281325);

        series1.add(new Day(15, SerialDate.JULY, 2002), 256101);
        series1.add(new Day(16, SerialDate.JULY, 2002), 348164);
        series1.add(new Day(17, SerialDate.JULY, 2002), 242995);
        series1.add(new Day(18, SerialDate.JULY, 2002), 200744);
        series1.add(new Day(19, SerialDate.JULY, 2002), 181071);

        series1.add(new Day(22, SerialDate.JULY, 2002), 163266);
        series1.add(new Day(23, SerialDate.JULY, 2002), 188508);
        series1.add(new Day(24, SerialDate.JULY, 2002), 308070);
        series1.add(new Day(25, SerialDate.JULY, 2002), 230901);
        series1.add(new Day(26, SerialDate.JULY, 2002), 162577);

        series1.add(new Day(29, SerialDate.JULY, 2002), 216318);
        series1.add(new Day(30, SerialDate.JULY, 2002), 280677);
        series1.add(new Day(31, SerialDate.JULY, 2002), 260236);
        series1.add(new Day(1, SerialDate.AUGUST, 2002), 242803);
        series1.add(new Day(2, SerialDate.AUGUST, 2002), 298490);

        series1.add(new Day(5, SerialDate.AUGUST, 2002), 182890);
        series1.add(new Day(6, SerialDate.AUGUST, 2002), 232273);
        series1.add(new Day(7, SerialDate.AUGUST, 2002), 253552);
        series1.add(new Day(8, SerialDate.AUGUST, 2002), 165365);
        series1.add(new Day(9, SerialDate.AUGUST, 2002), 160382);

        series1.add(new Day(12, SerialDate.AUGUST, 2002), 118030);
        series1.add(new Day(13, SerialDate.AUGUST, 2002), 208807);
        series1.add(new Day(14, SerialDate.AUGUST, 2002), 231599);
        series1.add(new Day(15, SerialDate.AUGUST, 2002), 343482);
        series1.add(new Day(16, SerialDate.AUGUST, 2002), 186116);

        series1.add(new Day(19, SerialDate.AUGUST, 2002), 96437);
        series1.add(new Day(20, SerialDate.AUGUST, 2002), 151735);
        series1.add(new Day(21, SerialDate.AUGUST, 2002), 167390);
        series1.add(new Day(22, SerialDate.AUGUST, 2002), 127184);
        series1.add(new Day(23, SerialDate.AUGUST, 2002), 80205);

        series1.add(new Day(26, SerialDate.AUGUST, 2002), 79893);
        series1.add(new Day(27, SerialDate.AUGUST, 2002), 201723);
        series1.add(new Day(28, SerialDate.AUGUST, 2002), 114001);
        series1.add(new Day(29, SerialDate.AUGUST, 2002), 188389);
        series1.add(new Day(30, SerialDate.AUGUST, 2002), 162801);

        series1.add(new Day(3, SerialDate.SEPTEMBER, 2002), 200951);
        series1.add(new Day(4, SerialDate.SEPTEMBER, 2002), 129229);
        series1.add(new Day(5, SerialDate.SEPTEMBER, 2002), 183348);
        series1.add(new Day(6, SerialDate.SEPTEMBER, 2002), 216722);

        series1.add(new Day(9, SerialDate.SEPTEMBER, 2002), 128575);
        series1.add(new Day(10, SerialDate.SEPTEMBER, 2002), 224714);
        series1.add(new Day(11, SerialDate.SEPTEMBER, 2002), 144224);
        series1.add(new Day(12, SerialDate.SEPTEMBER, 2002), 195721);
        series1.add(new Day(13, SerialDate.SEPTEMBER, 2002), 160724);

        series1.add(new Day(16, SerialDate.SEPTEMBER, 2002), 65473);
        series1.add(new Day(17, SerialDate.SEPTEMBER, 2002), 141274);
        series1.add(new Day(18, SerialDate.SEPTEMBER, 2002), 115084);
        series1.add(new Day(19, SerialDate.SEPTEMBER, 2002), 242106);
        series1.add(new Day(20, SerialDate.SEPTEMBER, 2002), 130034);

        series1.add(new Day(23, SerialDate.SEPTEMBER, 2002), 95215);
        series1.add(new Day(24, SerialDate.SEPTEMBER, 2002), 229288);
        series1.add(new Day(25, SerialDate.SEPTEMBER, 2002), 163672);
        series1.add(new Day(26, SerialDate.SEPTEMBER, 2002), 193573);
        series1.add(new Day(27, SerialDate.SEPTEMBER, 2002), 170741);

        series1.add(new Day(30, SerialDate.SEPTEMBER, 2002), 199615);
        series1.add(new Day(1, SerialDate.OCTOBER, 2002), 170771);
        series1.add(new Day(2, SerialDate.OCTOBER, 2002), 138498);
        series1.add(new Day(3, SerialDate.OCTOBER, 2002), 154774);
        series1.add(new Day(4, SerialDate.OCTOBER, 2002), 287154);

        series1.add(new Day(7, SerialDate.OCTOBER, 2002), 111762);
        series1.add(new Day(8, SerialDate.OCTOBER, 2002), 172535);
        series1.add(new Day(9, SerialDate.OCTOBER, 2002), 148339);
        series1.add(new Day(10, SerialDate.OCTOBER, 2002), 178796);
        series1.add(new Day(11, SerialDate.OCTOBER, 2002), 153499);

        series1.add(new Day(14, SerialDate.OCTOBER, 2002), 4589);
        series1.add(new Day(15, SerialDate.OCTOBER, 2002), 172088);
        series1.add(new Day(16, SerialDate.OCTOBER, 2002), 151267);
        series1.add(new Day(17, SerialDate.OCTOBER, 2002), 222680);
        series1.add(new Day(18, SerialDate.OCTOBER, 2002), 127019);

        series1.add(new Day(21, SerialDate.OCTOBER, 2002), 118226);
        series1.add(new Day(22, SerialDate.OCTOBER, 2002), 183031);
        series1.add(new Day(23, SerialDate.OCTOBER, 2002), 221005);
        series1.add(new Day(24, SerialDate.OCTOBER, 2002), 121333);
        series1.add(new Day(25, SerialDate.OCTOBER, 2002), 138179);

        series1.add(new Day(28, SerialDate.OCTOBER, 2002), 162012);
        series1.add(new Day(29, SerialDate.OCTOBER, 2002), 237355);
        series1.add(new Day(30, SerialDate.OCTOBER, 2002), 161650);
        series1.add(new Day(31, SerialDate.OCTOBER, 2002), 207569);

        return new TimeSeriesCollection(series1);

    }

    /**
     * Starting point for the price/volume chart demo application.
     *
     * @param args  ignored.
     */
    public static void main(String[] args) {

        PriceVolumeDemo demo = new PriceVolumeDemo("Price Volume Chart Demo");
        demo.pack();
        RefineryUtilities.centerFrameOnScreen(demo);
        demo.setVisible(true);

    }

}
