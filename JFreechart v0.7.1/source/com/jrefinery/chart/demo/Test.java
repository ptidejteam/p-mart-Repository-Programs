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

import javax.swing.*;

import com.jrefinery.chart.*;
import com.jrefinery.data.*;
import java.awt.*;

public class Test extends JFrame {

Long[][][] data = { { {new Long(10000044), new Long(0)}, {new Long(10000044), new Long(1)} } };

    public Test() {
        DefaultXYDataset source = new DefaultXYDataset(data);
        JFreeChart chart = ChartFactory.createTimeSeriesChart("Title", "Domain", "Range", source, true);
        JFreeChartPanel panel = new JFreeChartPanel(chart);
        this.getContentPane().add(panel, BorderLayout.CENTER);
    }

    public static void main(String[] args) {
        Test frame = new Test();
        frame.setVisible(true);
    }
}