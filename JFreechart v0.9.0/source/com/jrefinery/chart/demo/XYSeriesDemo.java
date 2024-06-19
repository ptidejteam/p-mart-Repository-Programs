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
 * -----------------
 * XYSeriesDemo.java
 * -----------------
 * (C) Copyright 2002, by Simba Management Limited and Contributors.
 *
 * Original Author:  David Gilbert (for Simba Management Limited);
 * Contributor(s):   -;
 *
 * $Id: XYSeriesDemo.java,v 1.1 2007/10/10 19:01:20 vauchers Exp $
 *
 * Changes
 * -------
 * 08-Apr-2002 : Version 1 (DG);
 *
 */

package com.jrefinery.chart.demo;

import java.awt.Paint;
import java.awt.Color;
import com.jrefinery.data.XYSeries;
import com.jrefinery.data.XYSeriesCollection;
import com.jrefinery.ui.ApplicationFrame;
import com.jrefinery.chart.JFreeChart;
import com.jrefinery.chart.ChartFactory;
import com.jrefinery.chart.ChartPanel;

public class XYSeriesDemo extends ApplicationFrame {

    protected XYSeries series;

    /**
     * A demonstration application showing a quarterly time series containing a null value.
     */
    public XYSeriesDemo(String title) {

        super(title);
        this.series = new XYSeries("Random Data");
        this.series.add(1.0, 500.2);
        this.series.add(5.0, 694.1);
        this.series.add(12.5, 734.4);
        this.series.add(17.3, 453.2);
        this.series.add(21.2, 500.2);
        this.series.add(new Double(21.9), null);  // different method required to get null in
        this.series.add(25.6, 734.4);
        this.series.add(30.0, 453.2);
        XYSeriesCollection data = new XYSeriesCollection(series);
        JFreeChart chart = ChartFactory.createXYChart("XY Series Demo", "X", "Y", data, true);

        chart.getPlot().setSeriesPaint(new Paint[] { Color.blue });
        ChartPanel chartPanel = new ChartPanel(chart);
        this.setContentPane(chartPanel);

    }

    /**
     * Starting point for the demonstration application.
     */
    public static void main(String[] args) {

        XYSeriesDemo demo = new XYSeriesDemo("XY Series Demo");
        demo.pack();
        demo.setVisible(true);

    }

}
