/*
 * Created on 29-Jul-2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package org.jfree.chart.demo;

import java.awt.Dimension;

import javax.swing.JDesktopPane;
import javax.swing.JInternalFrame;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.DefaultCategoryDataset;
import org.jfree.data.XYDataset;
import org.jfree.data.time.Minute;
import org.jfree.data.time.RegularTimePeriod;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;

/**
 * @author dgilbert
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class InternalFrameDemo extends ApplicationFrame {

    public InternalFrameDemo(String title) {
        super(title);
        JDesktopPane desktopPane = new JDesktopPane();
        desktopPane.setPreferredSize(new Dimension(600, 400));
        JInternalFrame frame1 = createFrame1();
        desktopPane.add(frame1);
        frame1.pack();
        frame1.setVisible(true);
        JInternalFrame frame2 = createFrame2();
        desktopPane.add(frame2);
        frame2.pack();
        frame2.setLocation(100, 200);
        frame2.setVisible(true);
        getContentPane().add(desktopPane);
    }
    
    private JInternalFrame createFrame1() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        dataset.addValue(34.0, "Series 1", "Category 1");
        dataset.addValue(23.0, "Series 1", "Category 2");
        dataset.addValue(54.0, "Series 1", "Category 3");
        JFreeChart chart = ChartFactory.createBarChart(
            "Bar Chart", 
            "Category",
            "Series",
            dataset,
            PlotOrientation.VERTICAL,
            true,
            true,
            false
        );
        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new Dimension(200, 100));
        JInternalFrame frame = new JInternalFrame("Frame 1", true);
        frame.getContentPane().add(chartPanel);
        return frame;
        
    }
    
    private JInternalFrame createFrame2() {
        XYDataset dataset1 = createDataset("Series 1", 100.0, new Minute(), 200);
        
        JFreeChart chart = ChartFactory.createTimeSeriesChart(
            "Time Series Chart", 
            "Time of Day", 
            "Value",
            dataset1, 
            true, 
            true, 
            false
        );
        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new Dimension(200, 100));
        JInternalFrame frame = new JInternalFrame("Frame 2", true);
        frame.getContentPane().add(chartPanel);
        return frame;
    }
    
    /**
     * Creates a sample dataset.
     * 
     * @param name  the dataset name.
     * @param base  the starting value.
     * @param start  the starting period.
     * @param count  the number of values to generate.
     *
     * @return The dataset.
     */
    private XYDataset createDataset(String name, double base, RegularTimePeriod start, int count) {

        TimeSeries series = new TimeSeries(name, start.getClass());
        RegularTimePeriod period = start;
        double value = base;
        for (int i = 0; i < count; i++) {
            series.add(period, value);    
            period = period.next();
            value = value * (1 + (Math.random() - 0.495) / 10.0);
        }

        TimeSeriesCollection dataset = new TimeSeriesCollection();
        dataset.addSeries(series);

        return dataset;

    }

    public static void main(String[] args) {
        InternalFrameDemo demo = new InternalFrameDemo("Internal Frame Demo");
        demo.pack();
        RefineryUtilities.centerFrameOnScreen(demo);
        demo.setVisible(true);
        
    }

}
