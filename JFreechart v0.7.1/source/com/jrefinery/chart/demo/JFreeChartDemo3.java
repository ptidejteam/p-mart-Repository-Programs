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

import java.awt.image.BufferedImage;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;

import com.jrefinery.data.DefaultPieDataset;
import com.jrefinery.chart.ChartFactory;
import com.jrefinery.chart.JFreeChart;
import com.jrefinery.chart.JFreeChartPanel;
import com.jrefinery.chart.JFreeChartFrame;

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