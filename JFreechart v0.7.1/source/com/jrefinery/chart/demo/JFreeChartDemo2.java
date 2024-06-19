/* ===============
 * JFreeChart Demo
 * ===============
 *
 * Project Info:  http://www.jrefinery.com/jfreechart;
 * Project Lead:  David Gilbert (david.gilbert@jrefinery.com);
 *
 * (C) Copyright 2000-2002, by Simba Management Limited and Contributors.
 *
 * This program is free software; you can redistribute it and/or modify it under the terms
 * of the GNU General Public License as published by the Free Software Foundation;
 * either version 2 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program;
 * if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston,
 * MA 02111-1307, USA.
 *
 */

package com.jrefinery.chart.demo;

import com.jrefinery.chart.*;
import com.jrefinery.data.*;
import com.jrefinery.date.*;

public class JFreeChartDemo2 {

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
        JFreeChartFrame frame = new JFreeChartFrame("Test", chart);
        frame.pack();
        frame.setVisible(true);

    }

}