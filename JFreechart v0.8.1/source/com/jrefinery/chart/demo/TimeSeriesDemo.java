package com.jrefinery.chart.demo;

import com.jrefinery.data.BasicTimeSeries;
import com.jrefinery.data.Quarter;
import com.jrefinery.data.TimeSeriesCollection;
import com.jrefinery.ui.ApplicationFrame;
import com.jrefinery.chart.JFreeChart;
import com.jrefinery.chart.ChartFactory;
import com.jrefinery.chart.JFreeChartPanel;

public class TimeSeriesDemo extends ApplicationFrame {

    protected BasicTimeSeries series;

    /**
     * A demonstration application showing a quarterly time series containing a null value.
     */
    public TimeSeriesDemo() {

        this.series = new BasicTimeSeries("Quarterly Data", Quarter.class);
        this.series.add(new Quarter(1, 2001), 500.2);
        this.series.add(new Quarter(2, 2001), 694.1);
        this.series.add(new Quarter(3, 2001), 734.4);
        this.series.add(new Quarter(4, 2001), 453.2);
        this.series.add(new Quarter(1, 2002), 500.2);
        this.series.add(new Quarter(2, 2002), null);
        this.series.add(new Quarter(3, 2002), 734.4);
        this.series.add(new Quarter(4, 2002), 453.2);
        TimeSeriesCollection dataset = new TimeSeriesCollection(series);
        JFreeChart chart = ChartFactory.createTimeSeriesChart("Time Series Demo", "Time", "Value",
                                                              dataset, true);

        JFreeChartPanel chartPanel = new JFreeChartPanel(chart);
        this.setContentPane(chartPanel);

    }



    /**
     * Starting point for the demonstration application.
     */
    public static void main(String[] args) {

        TimeSeriesDemo demo = new TimeSeriesDemo();
        demo.pack();
        demo.setVisible(true);

    }

}
