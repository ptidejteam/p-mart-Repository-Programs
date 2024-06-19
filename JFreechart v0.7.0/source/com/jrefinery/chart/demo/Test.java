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