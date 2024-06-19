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
 * --------------------------
 * TimePeriodValuesDemo2.java
 * --------------------------
 * (C) Copyright 2003, by Object Refinery Limited and Contributors.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited);
 * Contributor(s):   -;
 *
 * $Id: TimePeriodValuesDemo2.java,v 1.1 2007/10/10 19:15:24 vauchers Exp $
 *
 * Changes
 * -------
 * 30-Jul-2002 : Version 1 (DG);
 *
 */

package org.jfree.chart.demo;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.XYBarRenderer;
import org.jfree.chart.renderer.XYItemRenderer;
import org.jfree.data.XYDataset;
import org.jfree.data.time.Day;
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
public class TimePeriodValuesDemo2 extends ApplicationFrame {

    /**
     * A demonstration application showing how to....
     *
     * @param title  the frame title.
     */
    public TimePeriodValuesDemo2(String title) {

        super(title);

        XYDataset data1 = createDataset();
        XYItemRenderer renderer1 = new XYBarRenderer();
        
        DateAxis domainAxis = new DateAxis("Date");
        ValueAxis rangeAxis = new NumberAxis("Value");
        
        XYPlot plot = new XYPlot(data1, domainAxis, rangeAxis, renderer1);

        JFreeChart chart = new JFreeChart("Time Period Values Demo", plot);
        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new java.awt.Dimension(500, 270));
        chartPanel.setMouseZoomable(true, false);
        setContentPane(chartPanel);

    }

    /**
     * Creates a dataset, consisting of two series of monthly data.
     *
     * @return the dataset.
     */
    public XYDataset createDataset() {

        TimePeriodValues s1 = new TimePeriodValues("Series 1");
        Day d1 = new Day();
        Day d2 = (Day) d1.next();
        Day d3 = (Day) d2.next();
        Day d4 = (Day) d3.next();
        Day d5 = (Day) d4.next();
        Day d6 = (Day) d5.next();
        Day d7 = (Day) d6.next();
        
        s1.add(new SimpleTimePeriod(d6.getStart(), d6.getEnd()), 74.95);
        s1.add(new SimpleTimePeriod(d1.getStart(), d2.getEnd()), 55.75);
        s1.add(new SimpleTimePeriod(d7.getStart(), d7.getEnd()), 90.45);
        s1.add(new SimpleTimePeriod(d3.getStart(), d5.getEnd()), 105.75);

        TimePeriodValuesCollection dataset = new TimePeriodValuesCollection();
        dataset.addSeries(s1);
        dataset.setDomainIsPointsInTime(false);

        return dataset;

    }

    /**
     * Starting point for the demonstration application.
     *
     * @param args  ignored.
     */
    public static void main(String[] args) {

        TimePeriodValuesDemo2 demo = new TimePeriodValuesDemo2("Time Period Values Demo 2");
        demo.pack();
        RefineryUtilities.centerFrameOnScreen(demo);
        demo.setVisible(true);

    }

}
