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
 * JFreeChartDemo2.java
 * --------------------
 * (C) Copyright 2001, 2002, by Simba Management Limited and Contributors.
 *
 * Original Author:  David Gilbert (for Simba Management Limited);
 * Contributor(s):   -;
 *
 * $Id: JFreeChartDemo2.java,v 1.1 2007/10/10 19:52:20 vauchers Exp $
 *
 * Changes (from 24-Apr-2002)
 * --------------------------
 * 24-Apr-2002 : Added standard header (DG);
 *
 */

package com.jrefinery.chart.demo;

import com.jrefinery.chart.*;
import com.jrefinery.data.*;
import com.jrefinery.date.*;

/**
 * A time series chart with 4000 data points, to get an idea of how JFreeChart performs with a
 * larger dataset.
 */
public class JFreeChartDemo2 {

    /**
     * Starting point for the application.
     */
    public static void main(String[] args) {

        BasicTimeSeries series = new BasicTimeSeries("Random Data");

        Day current = new Day(1, 1, 1990);
        double value = 100.0;

        for (int i=0; i<4000; i++) {
            try {
                value = value+Math.random()-0.5;
                series.add(current, new Double(value));
                current = (Day)current.next();
            }
            catch (SeriesException e) {
                System.err.println("Error adding to series");
            }
        }

        XYDataset data = new TimeSeriesCollection(series);

        JFreeChart chart = ChartFactory.createTimeSeriesChart("Test", "Day", "Value", data, false);
        ChartFrame frame = new ChartFrame("Test", chart);
        frame.pack();
        frame.setVisible(true);

    }

}
