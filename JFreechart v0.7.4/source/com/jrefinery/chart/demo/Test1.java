package com.jrefinery.chart.demo;

import com.jrefinery.data.DefaultCategoryDataset;
import com.jrefinery.chart.ChartFactory;
import com.jrefinery.chart.JFreeChart;
import com.jrefinery.chart.JFreeChartFrame;
import com.jrefinery.chart.Plot;
import com.jrefinery.chart.HorizontalDateAxis;

public class Test1 {

    public Test1() {
    }

    public JFreeChart createTimeSeriesChart() {

        JFreeChart chart = ChartFactory.createTimeSeriesChart("Price Chart",
                                                              "Time",
                                                              "Price",
                                                              DemoDatasetFactory.createTimeSeriesCollection1(),
                                                              true);
        Plot plot = chart.getPlot();
        plot.setHorizontalAxis(new HorizontalDateAxis());

        return chart;

    }

    public static void main(String args[]) throws Throwable {

       JFreeChart chart = new Test1().createTimeSeriesChart();
       JFreeChartFrame frame = new JFreeChartFrame("Title", chart);
       frame.pack();
       frame.setVisible(true);

    }

}