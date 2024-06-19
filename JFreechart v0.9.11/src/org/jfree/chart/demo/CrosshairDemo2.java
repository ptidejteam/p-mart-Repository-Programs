/* ======================================
 * JFreeChart : a free Java chart library
 * ======================================
 *
 * Project Info:  http://www.jfree.org/jfreechart/index.html
 * Project Lead:  David Gilbert (david.gilbert@object-refinery.com);
 *
 * (C) Copyright 2000-2003, by Object Refinery Limited and Contributors.
 *
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation;
 * either version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this
 * library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330,
 * Boston, MA 02111-1307, USA.
 *
 * -------------------
 * CrosshairDemo2.java
 * -------------------
 * (C) Copyright 2003 by Object Refinery Limited and Contributors.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited);
 * Contributor(s):   -;
 *
 * $Id: CrosshairDemo2.java,v 1.1 2007/10/10 19:09:08 vauchers Exp $
 *
 * Changes
 * -------
 * 07-Aug-2002 : Version 1 (DG);
 *
 */

package org.jfree.chart.demo;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.Border;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.Spacer;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.event.ChartChangeEvent;
import org.jfree.chart.event.ChartChangeListener;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.DefaultXYItemRenderer;
import org.jfree.chart.renderer.StandardXYItemRenderer;
import org.jfree.data.XYDataset;
import org.jfree.data.time.Minute;
import org.jfree.data.time.RegularTimePeriod;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.time.TimeSeriesDataItem;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.DateCellRenderer;
import org.jfree.ui.NumberCellRenderer;
import org.jfree.ui.RefineryUtilities;

/**
 * An example of....
 *
 * @author David Gilbert
 */
public class CrosshairDemo2 extends ApplicationFrame implements ChartChangeListener {

    private static final int SERIES_COUNT = 4;
    
    private TimeSeriesCollection[] datasets;
    
    private TimeSeries[] series;
    
    private ChartPanel chartPanel;
    
    private DemoTableModel model;
    
    /**
     * A demonstration application showing how to...
     *
     * @param title  the frame title.
     */
    public CrosshairDemo2(String title) {

        super(title);
        
        this.datasets = new TimeSeriesCollection[SERIES_COUNT];
        this.series = new TimeSeries[SERIES_COUNT];
        
        JPanel content = new JPanel(new BorderLayout());
        
        JFreeChart chart = createChart();
        this.chartPanel = new ChartPanel(chart);
        this.chartPanel.setPreferredSize(new java.awt.Dimension(600, 270));
        this.chartPanel.setHorizontalZoom(true);
        this.chartPanel.setVerticalZoom(true);
        Border border = BorderFactory.createCompoundBorder(
            BorderFactory.createEmptyBorder(4, 4, 4, 4),
            BorderFactory.createEtchedBorder()
        );
        this.chartPanel.setBorder(border);
        content.add(this.chartPanel);
        
        JPanel dashboard = new JPanel(new BorderLayout());
        dashboard.setPreferredSize(new Dimension(400, 120));
        dashboard.setBorder(BorderFactory.createEmptyBorder(0, 4, 4, 4));
        
        this.model = new DemoTableModel(SERIES_COUNT);
        for (int row = 0; row < SERIES_COUNT; row++) {
        
            if (row == 0) {
                this.model.setValueAt(chart.getXYPlot().getDataset().getSeriesName(0), row, 0);
            }
            else {
                this.model.setValueAt(chart.getXYPlot().getSecondaryDataset(row - 1).getSeriesName(0), row, 0);                
            }
            this.model.setValueAt(new Double("0.00"), row, 1);
            this.model.setValueAt(new Double("0.00"), row, 2);
            this.model.setValueAt(new Double("0.00"), row, 3);
            this.model.setValueAt(new Double("0.00"), row, 4);
            this.model.setValueAt(new Double("0.00"), row, 5);
            this.model.setValueAt(new Double("0.00"), row, 6);
        
        }
        JTable table = new JTable(this.model);
        TableCellRenderer renderer1 = new DateCellRenderer(new SimpleDateFormat("HH:mm:ss"));
        TableCellRenderer renderer2 = new NumberCellRenderer();
        table.getColumnModel().getColumn(1).setCellRenderer(renderer1);
        table.getColumnModel().getColumn(2).setCellRenderer(renderer2);
        table.getColumnModel().getColumn(3).setCellRenderer(renderer1);
        table.getColumnModel().getColumn(4).setCellRenderer(renderer2);
        table.getColumnModel().getColumn(5).setCellRenderer(renderer1);
        table.getColumnModel().getColumn(6).setCellRenderer(renderer2);
        dashboard.add(new JScrollPane(table));
        content.add(dashboard, BorderLayout.SOUTH);
        
        setContentPane(content);

    }

    /**
     * Creates the demo chart.
     * 
     * @return The chart.
     */
    private JFreeChart createChart() {
        
        JFreeChart chart = ChartFactory.createTimeSeriesChart(
            "Crosshair Demo 2", 
            "Time of Day", 
            "Value",
            null, 
            true, 
            true, 
            false
        );

        XYDataset[] datasets = new XYDataset[SERIES_COUNT];
        for (int i = 0; i < SERIES_COUNT; i++) {
            datasets[i] = createDataset(i, "Series " + i, 100.0 + i * 200.0, new Minute(), 200);
            if (i == 0) {
                chart.getXYPlot().setDataset(datasets[i]);
            }
            else {
                XYPlot plot = chart.getXYPlot();
                plot.setSecondaryDataset(i - 1, datasets[i]);
                plot.setSecondaryRangeAxis(i - 1, new NumberAxis("Axis " + (i + 1)));
                plot.mapSecondaryDatasetToRangeAxis(i - 1, new Integer(i - 1));
                plot.setSecondaryRenderer(i - 1, new DefaultXYItemRenderer());
            }
        }
        chart.addChangeListener(this);
        chart.setBackgroundPaint(Color.white);
        XYPlot plot = chart.getXYPlot();
        plot.setOrientation(PlotOrientation.VERTICAL);
        plot.setBackgroundPaint(Color.lightGray);
        plot.setDomainGridlinePaint(Color.white);
        plot.setRangeGridlinePaint(Color.white);
        plot.setAxisOffset(new Spacer(Spacer.ABSOLUTE, 5.0, 5.0, 5.0, 5.0));
        
        plot.setDomainCrosshairVisible(true);
        plot.setDomainCrosshairLockedOnData(false);
        plot.setRangeCrosshairVisible(false);
        StandardXYItemRenderer renderer = (StandardXYItemRenderer) plot.getRenderer();
        renderer.setPaint(Color.black);
                       
        return chart;
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
    private XYDataset createDataset(int index, String name,
                                    double base, RegularTimePeriod start, int count) {

        this.series[index] = new TimeSeries(name, start.getClass());
        RegularTimePeriod period = start;
        double value = base;
        for (int i = 0; i < count; i++) {
            this.series[index].add(period, value);    
            period = period.next();
            value = value * (1 + (Math.random() - 0.495) / 10.0);
        }

        this.datasets[index] = new TimeSeriesCollection();
        datasets[index].addSeries(this.series[index]);

        return datasets[index];

    }

    public void chartChanged(ChartChangeEvent event) {
        if (this.chartPanel != null) {
            JFreeChart chart = this.chartPanel.getChart();
            if (chart != null) {
                XYPlot plot = chart.getXYPlot();
                XYDataset dataset = plot.getDataset();
                String seriesName = dataset.getSeriesName(0);
                double xx = plot.getDomainCrosshairValue();
                this.model.setValueAt(seriesName, 0, 0);
                long millis = (long) xx;
                for (int row = 0; row < SERIES_COUNT; row++) {
                    this.model.setValueAt(new Long(millis), row, 1);
                    int[] bounds = this.datasets[row].getSurroundingItems(0, millis);
                    System.out.println("Bounds: " + bounds[0] + ", " + bounds[1]);
                    //int itemIndex = this.series[row].getIndex(new Minute(new Date(millis)));
                    long prevX = 0;
                    long nextX = 0;
                    double prevY = 0.0;
                    double nextY = 0.0;
                    if (bounds[0] >= 0) {
                        TimeSeriesDataItem prevItem = this.series[row].getDataItem(bounds[0]);
                        prevX = prevItem.getPeriod().getMiddleMillisecond();      
                        Number y = prevItem.getValue();
                        if (y != null) {
                            prevY = y.doubleValue();  
                            this.model.setValueAt(new Double(prevY), row, 4);
                        }  
                        else {     
                            this.model.setValueAt(null, row, 4);
                        }
                        this.model.setValueAt(new Long(prevX), row, 3);
                    }
                    else {
                        this.model.setValueAt(new Double(0.00), row, 4);
                        this.model.setValueAt(new Double(this.chartPanel.getChart().getXYPlot().getDomainAxis().getRange().getLowerBound()), row, 3);                        
                    }
                    if (bounds[1] >= 0) {
                        TimeSeriesDataItem nextItem = this.series[row].getDataItem(bounds[1]);
                        nextX = nextItem.getPeriod().getMiddleMillisecond();
                        Number y = nextItem.getValue();
                        if (y != null) {
                            nextY = y.doubleValue();
                            this.model.setValueAt(new Double(nextY), row, 6);                            
                        }
                        else {     
                            this.model.setValueAt(null, row, 6);
                        }
                        this.model.setValueAt(new Long(nextX), row, 5);
                    }
                    else {
                        this.model.setValueAt(new Double(0.00), row, 6);
                        this.model.setValueAt(new Double(this.chartPanel.getChart().getXYPlot().getDomainAxis().getRange().getUpperBound()), row, 5);                        
                    }
                    double interpolatedY = 0.0;
                    if ((nextX - prevX) > 0) {
                        interpolatedY = prevY + (((double)millis - (double)prevX) / ((double)nextX - (double)prevX)) * (nextY - prevY); 
                    }
                    else {
                        interpolatedY = prevY;
                    }
                    this.model.setValueAt(new Double(interpolatedY), row, 2);
                }
            }
        }
    }
    
    /**
     * Starting point for the demonstration application.
     *
     * @param args  ignored.
     */
    public static void main(String[] args) {

        CrosshairDemo2 demo = new CrosshairDemo2("Crosshair Demo 2");
        demo.pack();
        RefineryUtilities.centerFrameOnScreen(demo);
        demo.setVisible(true);

    }

    class DemoTableModel extends AbstractTableModel implements TableModel {
    
        private Object[][] data;
        
        public DemoTableModel(int rows) {
            this.data = new Object[rows][7];
        }
     
        public int getColumnCount() {
            return 7;
        }
        
        public int getRowCount() {
            return data.length;
        }
        
        public Object getValueAt(int row, int column) {
            return this.data[row][column];
        }
        
        public void setValueAt(Object value, int row, int column) {
            this.data[row][column] = value;
            fireTableDataChanged();
        }
        
        public String getColumnName(int column) {
            switch(column) {
                case 0 : return "Series Name:";
                case 1 : return "X:";
                case 2 : return "Y:";
                case 3 : return "X (prev)";
                case 4 : return "Y (prev):";
                case 5 : return "X (next):";
                case 6 : return "Y (next):";
            }
            return null;
        }
        
    }
}
