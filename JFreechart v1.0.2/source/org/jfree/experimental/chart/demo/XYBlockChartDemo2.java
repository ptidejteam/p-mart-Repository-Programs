/* ----------------------
 * XYBlockChartDemo2.java
 * ----------------------
 * (C) Copyright 2006, by Object Refinery Limited.
 *
 */

package org.jfree.experimental.chart.demo;

import java.awt.Color;

import javax.swing.JPanel;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.time.Day;
import org.jfree.data.time.RegularTimePeriod;
import org.jfree.data.xy.DefaultXYZDataset;
import org.jfree.data.xy.XYZDataset;
import org.jfree.experimental.chart.renderer.LookupPaintScale;
import org.jfree.experimental.chart.renderer.xy.XYBlockRenderer;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RectangleAnchor;
import org.jfree.ui.RefineryUtilities;

/**
 * A simple demonstration application showing the experimental XYBlockRenderer 
 * in action.  TODO: The chart needs a legend to explain the colors displayed.
 */
public class XYBlockChartDemo2 extends ApplicationFrame {

    /**
     * Constructs the demo application.
     *
     * @param title  the frame title.
     */
    public XYBlockChartDemo2(String title) {
        super(title);
        JPanel chartPanel = createDemoPanel();
        chartPanel.setPreferredSize(new java.awt.Dimension(500, 270));
        setContentPane(chartPanel);
    }
    
    private static JFreeChart createChart(XYZDataset dataset) {
        DateAxis xAxis = new DateAxis("Date");
        xAxis.setLowerMargin(0.0);
        xAxis.setUpperMargin(0.0);
        NumberAxis yAxis = new NumberAxis("Hour");
        yAxis.setUpperMargin(0.0);
        yAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
        XYBlockRenderer renderer = new XYBlockRenderer();
        renderer.setBlockWidth(1000.0 * 60.0 * 60.0 * 24.0);
        renderer.setBlockAnchor(RectangleAnchor.BOTTOM_LEFT);
        LookupPaintScale paintScale = new LookupPaintScale();
        paintScale.add(new Double(1.0), Color.red);
        paintScale.add(new Double(2.0), Color.green);
        paintScale.add(new Double(3.0), Color.blue);        
        paintScale.add(new Double(4.0), Color.yellow);
        renderer.setPaintScale(paintScale);
        XYPlot plot = new XYPlot(dataset, xAxis, yAxis, renderer);
        plot.setOrientation(PlotOrientation.HORIZONTAL);
        plot.setBackgroundPaint(Color.lightGray);
        plot.setRangeGridlinePaint(Color.white);
        JFreeChart chart = new JFreeChart("XYBlockChartDemo2", plot);
        chart.removeLegend();
        chart.setBackgroundPaint(Color.white);
        return chart;
    }
    
    /**
     * Creates a sample dataset.
     * 
     * @return A sample dataset.
     */
    private static XYZDataset createDataset() {
        double[] xvalues = new double[2400];    
        double[] yvalues = new double[2400];    
        double[] zvalues = new double[2400];
        RegularTimePeriod t = new Day();
        for (int days = 0; days < 100; days++) {
            double value = 1.0;
            for (int hour = 0; hour < 24; hour++) {
                if (Math.random() < 0.1) {
                    value = Math.random() * 4.0;
                }
                xvalues[days * 24 + hour] = t.getFirstMillisecond();
                yvalues[days * 24 + hour] = hour;
                zvalues[days * 24 + hour] = value;
            }
            t = t.next();
        }
        DefaultXYZDataset dataset = new DefaultXYZDataset();
        dataset.addSeries("Series 1", 
                new double[][] { xvalues, yvalues, zvalues });
        return dataset;
    }
    
    /**
     * Creates a panel for the demo.
     *  
     * @return A panel.
     */
    public static JPanel createDemoPanel() {
        return new ChartPanel(createChart(createDataset()));
    }
    
    /**
     * Starting point for the demonstration application.
     *
     * @param args  ignored.
     */
    public static void main(String[] args) {
        XYBlockChartDemo2 demo = new XYBlockChartDemo2("Block Chart Demo 2");
        demo.pack();
        RefineryUtilities.centerFrameOnScreen(demo);
        demo.setVisible(true);
    }

}
