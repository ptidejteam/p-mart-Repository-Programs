package com.jrefinery.chart.demo;

import java.awt.Color;
import java.awt.Paint;
import java.awt.Stroke;
import java.awt.Stroke;
import com.jrefinery.chart.JFreeChart;
import com.jrefinery.chart.ChartFactory;
import com.jrefinery.chart.ChartPanel;
import com.jrefinery.chart.CategoryPlot;
import com.jrefinery.chart.HorizontalDateAxis;
import com.jrefinery.chart.HorizontalShapeRenderer;
import com.jrefinery.data.CategoryDataset;
import com.jrefinery.data.DefaultCategoryDataset;
import com.jrefinery.data.Day;
import com.jrefinery.date.SerialDate;
import com.jrefinery.ui.ApplicationFrame;
import com.jrefinery.ui.RefineryUtilities;

public class EventFrequencyDemo extends ApplicationFrame {

    /** The data. */
    protected CategoryDataset data;

    /**
     * Default constructor.
     */
    public EventFrequencyDemo(String title) {

        super(title);

        // create a dataset...
        Number[][] data = new Number[3][4];
        DefaultCategoryDataset dataset = new DefaultCategoryDataset(data);

        // set the series names...
        String[] seriesNames = new String[] { "Add", "Change", "Delete" };
        dataset.setSeriesNames(seriesNames);

        // set the category names...
        String[] categories = new String[] { "Requirement 1", "Requirement 2",
                                             "Requirement 3", "Requirement 4" };
        dataset.setCategories(categories);

        // initialise the data...
        Day d1 = new Day(12, SerialDate.JUNE, 2002);
        Day d2 = new Day(14, SerialDate.JUNE, 2002);
        Day d3 = new Day(15, SerialDate.JUNE, 2002);
        Day d4 = new Day(10, SerialDate.JULY, 2002);
        Day d5 = new Day(20, SerialDate.JULY, 2002);
        Day d6 = new Day(22, SerialDate.AUGUST, 2002);

        dataset.setValue(0, "Requirement 1", new Long(d1.getMiddle()));
        dataset.setValue(0, "Requirement 2", new Long(d1.getMiddle()));
        dataset.setValue(0, "Requirement 3", new Long(d2.getMiddle()));
        dataset.setValue(1, "Requirement 1", new Long(d3.getMiddle()));
        dataset.setValue(1, "Requirement 3", new Long(d4.getMiddle()));
        dataset.setValue(2, "Requirement 2", new Long(d5.getMiddle()));
        dataset.setValue(0, "Requirement 4", new Long(d6.getMiddle()));

        // create the chart...
        JFreeChart chart = ChartFactory.createHorizontalBarChart("Event Frequency Demo",  // chart title
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
        plot.setRangeAxis(new HorizontalDateAxis("Date"));
        plot.setRenderer(new HorizontalShapeRenderer());

        // set the color for each series...
        plot.setSeriesPaint(new Paint[] { Color.green, Color.orange, Color.red });

        // OPTIONAL CUSTOMISATION COMPLETED.

        // add the chart to a panel...
        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new java.awt.Dimension(500, 270));
        this.setContentPane(chartPanel);

    }

    /**
     * Starting point for the demonstration application.
     */
    public static void main(String[] args) {

        EventFrequencyDemo demo = new EventFrequencyDemo("Event Frequency Demo");
        demo.pack();
        RefineryUtilities.centerFrameOnScreen(demo);
        demo.setVisible(true);

    }

}
