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
 * JFreeChartDemo3.java
 * --------------------
 * (C) Copyright 2001, 2002, by Simba Management Limited and Contributors.
 *
 * Original Author:  David Gilbert (for Simba Management Limited);
 * Contributor(s):   -;
 *
 * $Id: JFreeChartDemo3.java,v 1.1 2007/10/10 19:41:58 vauchers Exp $
 *
 * Changes (from 24-Apr-2002)
 * --------------------------
 * 24-Apr-2002 : Added standard header (DG);
 *
 */

package com.jrefinery.chart.demo;

import java.awt.image.BufferedImage;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;

import com.jrefinery.data.DefaultPieDataset;
import com.jrefinery.chart.ChartFactory;
import com.jrefinery.chart.JFreeChart;
import com.jrefinery.chart.ChartPanel;
import com.jrefinery.chart.ChartFrame;

public class JFreeChartDemo3 {

    private boolean finished;

    public JFreeChartDemo3() {

        finished = false;

        // create a dataset...
        DefaultPieDataset data = new DefaultPieDataset();
        data.setValue("One", new Double(10.3));
        data.setValue("Two", new Double(8.5));
        data.setValue("Three", new Double(3.9));
        data.setValue("Four", new Double(3.9));
        data.setValue("Five", new Double(3.9));
        data.setValue("Six", new Double(3.9));

        // create a pie chart...
        boolean withLegend = true;
        JFreeChart chart = ChartFactory.createPieChart("ToolTip Example", data, withLegend);

        BufferedImage image = new BufferedImage(400, 300, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2 = image.createGraphics();
        Rectangle2D chartArea = new Rectangle2D.Double(0, 0, 400, 300);

        TimerThread timer = new TimerThread(10000L, this);
        int count = 0;
        timer.start();
        while (!finished) {
            chart.draw(g2, chartArea, null);
            System.out.println("Charts drawn..."+count);
            if (!finished) count++;
        }
        System.out.println("DONE");

    }

    public void setFinished(boolean flag) {
        this.finished = flag;
    }

    public static void main(String[] args) {

        JFreeChartDemo3 app = new JFreeChartDemo3();

    }
}

class TimerThread extends Thread {

    private long millis;
    private JFreeChartDemo3 application;

    public TimerThread(long millis, JFreeChartDemo3 application) {
        this.millis = millis;
        this.application = application;
    }

    public void run() {
        try {
            sleep(millis);
            application.setFinished(true);
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

}