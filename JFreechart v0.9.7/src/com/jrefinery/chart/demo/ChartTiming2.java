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
 * -----------------
 * ChartTiming2.java
 * -----------------
 * (C) Copyright 2002, 2003, by Simba Management Limited and Contributors.
 *
 * Original Author:  David Gilbert (for Simba Management Limited);
 * Contributor(s):   -;
 *
 * $Id: ChartTiming2.java,v 1.1 2007/10/10 20:00:13 vauchers Exp $
 *
 * Changes
 * -------
 * 29-Oct-2002 : Version 1 (DG);
 *
 */

package com.jrefinery.chart.demo;

import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import javax.swing.Timer;
import com.jrefinery.chart.ChartFactory;
import com.jrefinery.chart.JFreeChart;
import com.jrefinery.chart.plot.XYPlot;
import com.jrefinery.chart.renderer.XYDotRenderer;
import com.jrefinery.data.XYDataset;

/**
 * Draws a scatter plot over and over for 10 seconds.  Reports on how many redraws were achieved.
 *
 * @author David Gilbert
 */
public class ChartTiming2 implements ActionListener {

    /** A flag that indicates when time is up. */
    private boolean finished;

    /**
     * Creates a new application.
     */
    public ChartTiming2() {

        this.finished = false;

        // create a dataset...
        XYDataset data = new SampleXYDataset2(1, 1440);

        // create a scatter chart...
        boolean withLegend = true;
        JFreeChart chart = ChartFactory.createScatterPlot("Scatter plot timing", "X", "Y",
                                                          data, withLegend, false, false);

        XYPlot plot = chart.getXYPlot();
        plot.setRenderer(new XYDotRenderer());

        BufferedImage image = new BufferedImage(400, 300, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2 = image.createGraphics();
        Rectangle2D chartArea = new Rectangle2D.Double(0, 0, 400, 300);

        // set up the timer...
        Timer timer = new Timer(10000, this);
        timer.setRepeats(false);
        int count = 0;
        timer.start();
        while (!finished) {
            chart.draw(g2, chartArea, null);
            System.out.println("Charts drawn..." + count);
            if (!finished) {
                count++;
            }
        }
        System.out.println("DONE");

    }

    /**
     * Receives notification of action events (in this case, from the Timer).
     *
     * @param event  the event.
     */
    public void actionPerformed(ActionEvent event) {
        this.finished = true;
    }

    /**
     * Starting point for the application.
     *
     * @param args  ignored.
     */
    public static void main(String[] args) {

        ChartTiming2 app = new ChartTiming2();

    }

}
