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
 * ------------------
 * XYLogAxesDemo.java
 * ------------------
 * (C) Copyright 2002, by Simba Management Limited and Contributors.
 *
 * Original Author:  David Gilbert (for Simba Management Limited);
 * Contributor(s):   Clemens;
 *
 * $Id: XYLogAxesDemo.java,v 1.1 2007/10/10 19:52:20 vauchers Exp $
 *
 * Changes
 * -------
 * 31-Jul-2002 : Version 1 (DG);
 *
 */
package com.jrefinery.chart.demo;

import java.awt.Paint;
import java.awt.Color;
import com.jrefinery.data.XYDataset;
import com.jrefinery.data.DefaultXYDataset;
import com.jrefinery.ui.ApplicationFrame;
import com.jrefinery.chart.JFreeChart;
import com.jrefinery.chart.ChartFactory;
import com.jrefinery.chart.ChartPanel;
import com.jrefinery.chart.XYPlot;
import com.jrefinery.chart.HorizontalLogarithmicAxis;
import com.jrefinery.chart.VerticalLogarithmicAxis;
import com.jrefinery.chart.Axis;
import com.jrefinery.chart.ValueAxis;
import com.jrefinery.chart.NumberAxis;

public class XYLogAxesDemo extends ApplicationFrame {

    public XYLogAxesDemo(String title) {

        super(title);

        Object[][][] data = new Object[3][50][2];
        for(int i=1; i<=50; i++) {
            data[0][i-1][0] = new Double(i);
            data[0][i-1][1] = new Double(1000 * Math.pow(i, -2)); // 1/x^2
            data[1][i-1][0] = new Double(i);
            data[1][i-1][1] = new Double(1000 * Math.pow(i, -3)); // 1/x^3
            data[2][i-1][0] = new Double(i);
            data[2][i-1][1] = new Double(1000 * Math.pow(i, -4)); // 1/x^4
            System.out.println(data[0][i-1][0] + ";" + data[0][i-1][1] + ";" + data[1][i-1][0] + ";" + data[1][i-1][1] + ";" + data[2][i-1][0] + ";" + data[2][i-1][1]);
        }

        XYDataset dataset = new DefaultXYDataset(data);

        JFreeChart chart = ChartFactory.createXYChart("Log Axis Demo", // chart title
                                                      "Category", // domain axis label
                                                      "Value", // range axis label
                                                      dataset, // data
                                                      true // include legend
                                                      );


        XYPlot plot = chart.getXYPlot();
        VerticalLogarithmicAxis v = new VerticalLogarithmicAxis("Log(y)");
        HorizontalLogarithmicAxis h = new HorizontalLogarithmicAxis("Log(x)");
        plot.setRangeAxis(v);
        plot.setDomainAxis(h);
        chart.setBackgroundPaint(Color.white);
        plot.setSeriesPaint(new Paint[] { Color.green, Color.orange, Color.red });
        plot.setOutlinePaint(Color.blue);
        ChartPanel chartPanel = new ChartPanel(chart);
        this.setContentPane(chartPanel);

    }

    /**
     * Starting point for the demonstration application.
     */
    public static void main(String[] args) {

        XYLogAxesDemo demo = new XYLogAxesDemo("XY Log Axes Demo");
        demo.pack();
        demo.setVisible(true);

    }

}
