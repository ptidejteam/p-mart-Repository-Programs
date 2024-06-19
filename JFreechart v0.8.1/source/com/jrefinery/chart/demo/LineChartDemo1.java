package com.jrefinery.chart.demo;

import java.awt.Paint;
import java.awt.Color;
import java.awt.Stroke;
import java.awt.BasicStroke;
import com.jrefinery.data.CategoryDataset;
import com.jrefinery.data.DefaultCategoryDataset;
import com.jrefinery.ui.ApplicationFrame;
import com.jrefinery.chart.JFreeChart;
import com.jrefinery.chart.ChartFactory;
import com.jrefinery.chart.JFreeChartPanel;
import com.jrefinery.chart.CategoryPlot;
import com.jrefinery.chart.NumberAxis;
import com.jrefinery.chart.TickUnits;

/**
 * A simple demonstration application showing how to create a line chart using data from a
 * CategoryDataset.
 */
public class LineChartDemo1 extends ApplicationFrame {

    /** The data. */
    protected CategoryDataset data;

    /**
     * Default constructor.
     */
    public LineChartDemo1() {

        // create a dataset...
        double[][] data = new double[][] {
            { 1.0, 4.0, 3.0, 5.0, 5.0, 7.0, 7.0, 8.0 },
            { 5.0, 7.0, 6.0, 8.0, 4.0, 4.0, 2.0, 1.0 },
            { 4.0, 3.0, 2.0, 3.0, 6.0, 3.0, 4.0, 3.0 }
        };

        DefaultCategoryDataset dataset = new DefaultCategoryDataset(data);

        // set the series names...
        String[] seriesNames = new String[] { "First", "Second", "Third" };
        dataset.setSeriesNames(seriesNames);

        // set the category names...
        String[] categories = new String[] { "Type 1", "Type 2", "Type 3", "Type 4",
                                             "Type 5", "Type 6", "Type 7", "Type 8"  };
        dataset.setCategories(categories);

        // create the chart...
        JFreeChart chart = ChartFactory.createLineChart("Line Chart Demo 1",  // chart title
                                                        "Category",           // domain axis label
                                                        "Value",              // range axis label
                                                        dataset,              // data
                                                        true                  // include legend
                                                        );

        // NOW DO SOME OPTIONAL CUSTOMISATION OF THE CHART...

        // set the background color for the chart...
        chart.setBackgroundPaint(Color.yellow);

        // get a reference to the plot for further customisation...
        CategoryPlot plot = chart.getCategoryPlot();

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

        LineChartDemo1 demo = new LineChartDemo1();
        demo.pack();
        demo.setVisible(true);

    }

}
