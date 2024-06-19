/* ======================================
 * JFreeChart : a free Java chart library
 * ======================================
 *
 * Project Info:  http://www.jfree.org/jfreechart/index.html
 * Project Lead:  David Gilbert (david.gilbert@object-refinery.com);
 *
 * (C) Copyright 2003, by Object Refinery Limited and Contributors.
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
 * TimePeriodValuesDemo.java
 * -------------------------
 * (C) Copyright 2003, by Object Refinery Limited and Contributors.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited);
 * Contributor(s):   -;
 *
 * $Id: TimePeriodValuesDemo.java,v 1.1 2007/10/10 19:21:51 vauchers Exp $
 *
 * Changes
 * -------
 * 08-Apr-2002 : Version 1 (DG);
 * 25-Jun-2002 : Removed unnecessary import (DG);
 *
 */

package org.jfree.chart.demo;

import java.text.SimpleDateFormat;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.DateTickUnit;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.StandardXYItemRenderer;
import org.jfree.chart.renderer.XYBarRenderer;
import org.jfree.chart.renderer.XYItemRenderer;
import org.jfree.data.XYDataset;
import org.jfree.data.time.Day;
import org.jfree.data.time.Hour;
import org.jfree.data.time.Minute;
import org.jfree.data.time.SimpleTimePeriod;
import org.jfree.data.time.TimePeriodValues;
import org.jfree.data.time.TimePeriodValuesCollection;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;

/**
 * An example of....
 *
 * @author David Gilbert
 */
public class TimePeriodValuesDemo extends ApplicationFrame {

    /**
     * A demonstration application showing how to....
     *
     * @param title  the frame title.
     */
    public TimePeriodValuesDemo(String title) {

        super(title);

        XYDataset data1 = createDataset1();
        XYItemRenderer renderer1 = new XYBarRenderer();
        
        DateAxis domainAxis = new DateAxis("Date");
        domainAxis.setVerticalTickLabels(true);
        domainAxis.setTickUnit(new DateTickUnit(DateTickUnit.HOUR, 1));
        domainAxis.setDateFormatOverride(new SimpleDateFormat("hh:mm"));
        domainAxis.setLowerMargin(0.01);
        domainAxis.setUpperMargin(0.01);
        ValueAxis rangeAxis = new NumberAxis("Value");
        
        XYPlot plot = new XYPlot(data1, domainAxis, rangeAxis, renderer1);

        XYDataset data2 = createDataset2();
        StandardXYItemRenderer renderer2
            = new StandardXYItemRenderer(StandardXYItemRenderer.SHAPES_AND_LINES);
        renderer2.setShapesFilled(true);
        
        plot.setSecondaryDataset(0, data2);
        plot.setSecondaryRenderer(0, renderer2);
        //plot.mapSecondaryDatasetToRangeAxis(0, null);

        JFreeChart chart = new JFreeChart("Supply and Demand", plot);
        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new java.awt.Dimension(500, 270));
        chartPanel.setMouseZoomable(true, false);
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
     * Creates a dataset, consisting of two series of monthly data.
     *
     * @return the dataset.
     */
    public XYDataset createDataset1() {

        TimePeriodValues s1 = new TimePeriodValues("Supply");
        TimePeriodValues s2 = new TimePeriodValues("Demand");
        Day today = new Day();
        for (int i = 0; i < 24; i++) {
            Minute m0 = new Minute(0, new Hour(i, today));
            Minute m1 = new Minute(15, new Hour(i, today));
            Minute m2 = new Minute(30, new Hour(i, today));
            Minute m3 = new Minute(45, new Hour(i, today));
            Minute m4 = new Minute(0, new Hour(i + 1, today));
            s1.add(new SimpleTimePeriod(m0.getStart(), m1.getStart()), Math.random());
            s2.add(new SimpleTimePeriod(m1.getStart(), m2.getStart()), Math.random());
            s1.add(new SimpleTimePeriod(m2.getStart(), m3.getStart()), Math.random());
            s2.add(new SimpleTimePeriod(m3.getStart(), m4.getStart()), Math.random());
        }

        TimePeriodValuesCollection dataset = new TimePeriodValuesCollection();
        dataset.addSeries(s1);
        dataset.addSeries(s2);

        return dataset;

    }

    /**
     * Creates a dataset, consisting of two series of monthly data.
     *
     * @return the dataset.
     */
    public XYDataset createDataset2() {

        TimePeriodValues s1 = new TimePeriodValues("WebCOINS");
        Day today = new Day();
        for (int i = 0; i < 24; i++) {
            Minute m0 = new Minute(0, new Hour(i, today));
            Minute m1 = new Minute(30, new Hour(i, today));
            Minute m2 = new Minute(0, new Hour(i + 1, today));
            s1.add(new SimpleTimePeriod(m0.getStart(), m1.getStart()), Math.random() * 2.0);
            s1.add(new SimpleTimePeriod(m1.getStart(), m2.getStart()), Math.random() * 2.0);
        }

        TimePeriodValuesCollection dataset = new TimePeriodValuesCollection();
        dataset.addSeries(s1);

        return dataset;

    }

    /**
     * Starting point for the demonstration application.
     *
     * @param args  ignored.
     */
    public static void main(String[] args) {

        TimePeriodValuesDemo demo = new TimePeriodValuesDemo("Time Period Values Demo 1");
        demo.pack();
        RefineryUtilities.centerFrameOnScreen(demo);
        demo.setVisible(true);

    }

}
