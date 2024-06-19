package com.jrefinery.chart.demo;

import com.jrefinery.data.XYSeries;
import com.jrefinery.data.XYSeriesCollection;
import com.jrefinery.chart.ChartFactory;
import com.jrefinery.chart.JFreeChart;
import com.jrefinery.chart.JFreeChartFrame;
import com.jrefinery.chart.XYPlot;
import com.jrefinery.chart.ValueAxis;
import com.jrefinery.chart.XYItemRenderer;
import com.jrefinery.chart.StandardXYItemRenderer;

public class Second {

    public static void main(String[] args) {

        // create some data...
        XYSeries series1 = new XYSeries("Advisory Range");
        series1.add(new Integer(1200), new Integer(1));
        series1.add(new Integer(1500), new Integer(1));

        XYSeries series2 = new XYSeries("Normal Range");
        series2.add(new Integer(2000), new Integer(4));
        series2.add(new Integer(2300), new Integer(4));

        XYSeries series3 = new XYSeries("Recommended");
        series3.add(new Integer(2100), new Integer(2));

        XYSeries series4 = new XYSeries("Current");
        series4.add(new Integer(2400), new Integer(3));

        XYSeriesCollection data = new XYSeriesCollection();
        data.addSeries(series1);
        data.addSeries(series2);
        data.addSeries(series3);
        data.addSeries(series4);

        // create a chart...
        JFreeChart chart = ChartFactory.createXYChart("My Chart", "Calories", "Y", data, true);

        XYItemRenderer renderer = new StandardXYItemRenderer(StandardXYItemRenderer.SHAPES_AND_LINES);
        XYPlot plot = (XYPlot)chart.getPlot();
        plot.setXYItemRenderer(renderer);
        ValueAxis axis = plot.getRangeAxis();
        axis.setTickLabelsVisible(false);
        axis.setAxisRange(0.0, 5.0);

        // create and display a frame...
        JFreeChartFrame frame = new JFreeChartFrame("Test", chart);
        frame.pack();
        frame.setVisible(true);

    }

}