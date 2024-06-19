package com.jrefinery.chart.demo;

import java.awt.Paint;
import java.awt.Color;
import java.awt.Stroke;
import java.awt.BasicStroke;
import com.jrefinery.data.XYDataset;
import com.jrefinery.data.XYSeriesCollection;
import com.jrefinery.data.XYSeries;
import com.jrefinery.ui.ApplicationFrame;
import com.jrefinery.chart.JFreeChart;
import com.jrefinery.chart.ChartFactory;
import com.jrefinery.chart.JFreeChartPanel;
import com.jrefinery.chart.XYPlot;
import com.jrefinery.chart.NumberAxis;
import com.jrefinery.chart.TickUnits;

/**
 * A simple demonstration application showing how to create a line chart using data from an
 * XYDataset.
 */
public class LineChartDemo2 extends ApplicationFrame {

    /** The data. */
    protected XYDataset data;

    /**
     * Default constructor.
     */
    public LineChartDemo2() {

        // create a dataset...
        double[][] data = new double[][] {
            { 1.0, 4.0, 3.0, 5.0, 5.0, 7.0, 7.0, 8.0 },
            { 5.0, 7.0, 6.0, 8.0, 4.0, 4.0, 2.0, 1.0 },
            { 4.0, 3.0, 2.0, 3.0, 6.0, 3.0, 4.0, 3.0 }
        };

        XYSeries series1 = new XYSeries("First");
        series1.add(1.0, 1.0);
        series1.add(2.0, 4.0);
        series1.add(3.0, 3.0);
        series1.add(4.0, 5.0);
        series1.add(5.0, 5.0);
        series1.add(6.0, 7.0);
        series1.add(7.0, 7.0);
        series1.add(8.0, 8.0);

        XYSeries series2 = new XYSeries("Second");
        series2.add(1.0, 5.0);
        series2.add(2.0, 7.0);
        series2.add(3.0, 6.0);
        series2.add(4.0, 8.0);
        series2.add(5.0, 4.0);
        series2.add(6.0, 4.0);
        series2.add(7.0, 2.0);
        series2.add(8.0, 1.0);

        XYSeries series3 = new XYSeries("Third");
        series3.add(3.0, 4.0);
        series3.add(4.0, 3.0);
        series3.add(5.0, 2.0);
        series3.add(6.0, 3.0);
        series3.add(7.0, 6.0);
        series3.add(8.0, 3.0);
        series3.add(9.0, 4.0);
        series3.add(10.0, 3.0);

        XYSeriesCollection dataset = new XYSeriesCollection();
        dataset.addSeries(series1);
        dataset.addSeries(series2);
        dataset.addSeries(series3);

        // create the chart...
        JFreeChart chart = ChartFactory.createXYChart("Line Chart Demo 2",  // chart title
                                                      "X",                  // domain axis label
                                                      "Y",                  // range axis label
                                                      dataset,              // data
                                                      true                  // include legend
                                                      );

        // NOW DO SOME OPTIONAL CUSTOMISATION OF THE CHART...

        // set the background color for the chart...
        chart.setBackgroundPaint(Color.orange);

        // get a reference to the plot for further customisation...
        XYPlot plot = chart.getXYPlot();

        // set the color for each series...
        plot.setSeriesPaint(new Paint[] { Color.green, Color.orange, Color.red });

        // set the stroke for each series...
        Stroke[] seriesStrokeArray = new Stroke[3];
        seriesStrokeArray[0] = new BasicStroke(2.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND,
                                               1.0f, new float[] { 10.0f, 6.0f }, 0.0f);
        seriesStrokeArray[1] = new BasicStroke(2.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND,
                                               1.0f, new float[] { 6.0f, 6.0f }, 0.0f);
        seriesStrokeArray[2] = new BasicStroke(2.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND,
                                               1.0f, new float[] { 2.0f, 6.0f }, 0.0f);
        plot.setSeriesStroke(seriesStrokeArray);

        // change the auto tick unit selection to integer units only...
        NumberAxis rangeAxis = (NumberAxis)plot.getRangeAxis();
        rangeAxis.setStandardTickUnits(TickUnits.createIntegerTickUnits());

        // OPTIONAL CUSTOMISATION COMPLETED.

        // add the chart to a panel...
        JFreeChartPanel chartPanel = new JFreeChartPanel(chart);
        this.setContentPane(chartPanel);

    }

    /**
     * Starting point for the demonstration application.
     */
    public static void main(String[] args) {

        LineChartDemo2 demo = new LineChartDemo2();
        demo.pack();
        demo.setVisible(true);

    }

}