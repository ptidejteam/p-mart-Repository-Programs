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
 * TimeSeriesDemo5.java
 * --------------------
 * (C) Copyright 2001-2003, by Object Refinery Limited and Contributors.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited);
 * Contributor(s):   -;
 *
 * $Id: TimeSeriesDemo5.java,v 1.1 2007/10/10 19:09:08 vauchers Exp $
 *
 * Changes (from 24-Apr-2002)
 * --------------------------
 * 24-Apr-2002 : Added standard header (DG);
 * 10-Oct-2002 : Renamed JFreeChartDemo2 --> TimeSeriesDemo5 (DG);
 *
 */

package org.jfree.chart.demo;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartFrame;
import org.jfree.chart.JFreeChart;
import org.jfree.data.SeriesException;
import org.jfree.data.XYDataset;
import org.jfree.data.time.Day;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.ui.RefineryUtilities;

/**
 * A time series chart with 4000 data points, to get an idea of how JFreeChart performs with a
 * larger dataset.  You can see that it slows down significantly, so this needs to be worked on
 * (4000 points is not that many!).
 *
 * @author David Gilbert
 */
public class TimeSeriesDemo5 {

    /**
     * Starting point for the application.
     *
     * @param args  ignored.
     */
    public static void main(String[] args) {

        TimeSeries series = new TimeSeries("Random Data");

        Day current = new Day(1, 1, 1990);
        double value = 100.0;

        for (int i = 0; i < 4000; i++) {
            try {
                value = value + Math.random() - 0.5;
                series.add(current, new Double(value));
                current = (Day) current.next();
            }
            catch (SeriesException e) {
                System.err.println("Error adding to series");
            }
        }

        XYDataset data = new TimeSeriesCollection(series);

        JFreeChart chart = ChartFactory.createTimeSeriesChart("Test", "Day", "Value", data,
                                                              false, false, false);

        ChartFrame frame = new ChartFrame("\u20A2\u20A2\u20A2\u20A3\u20A4\u20A5\u20A6\u20A7\u20A8\u20A9\u20AA", chart);
        frame.pack();
        RefineryUtilities.positionFrameRandomly(frame);
        frame.setVisible(true);

    }

}
