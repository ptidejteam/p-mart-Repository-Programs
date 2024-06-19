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
 * CrosshairDemo1.java
 * -------------------
 * (C) Copyright 2003 by Object Refinery Limited and Contributors.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited);
 * Contributor(s):   -;
 *
 * $Id: CrosshairDemo1.java,v 1.1 2007/10/10 19:04:55 vauchers Exp $
 *
 * Changes
 * -------
 * 24-Jul-2002 : Version 1 (DG);
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
import org.jfree.chart.event.ChartChangeEvent;
import org.jfree.chart.event.ChartChangeListener;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
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
public class CrosshairDemo1 extends ApplicationFrame implements ChartChangeListener {

    private TimeSeries series;
    
    private ChartPanel chartPanel;
    
    private DemoTableModel model;
    
    /**
     * A demonstration application showing how to...
     *
     * @param title  the frame title.
     */
    public CrosshairDemo1(String title) {

        super(title);
        
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
        dashboard.setPreferredSize(new Dimension(400, 60));
        dashboard.setBorder(BorderFactory.createEmptyBorder(0, 4, 4, 4));
        
        this.model = new DemoTableModel(3);
        this.model.setValueAt(chart.getXYPlot().getDataset().getSeriesName(0), 0, 0);
        this.model.setValueAt(new Double("0.00"), 0, 1);
        this.model.setValueAt(new Double("0.00"), 0, 2);
        this.model.setValueAt(new Double("0.00"), 0, 3);
        this.model.setValueAt(new Double("0.00"), 0, 4);
        this.model.setValueAt(new Double("0.00"), 0, 5);
        this.model.setValueAt(new Double("0.00"), 0, 6);
        JTable table = new JTable(this.model);
        TableCellRenderer renderer1 = new DateCellRenderer(new SimpleDateFormat("HH:mm"));
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

        XYDataset dataset1 = createDataset("Random 1", 100.0, new Minute(), 200);
        
        JFreeChart chart = ChartFactory.createTimeSeriesChart(
            "Crosshair Demo 1", 
            "Time of Day", 
            "Value",
            dataset1, 
            true, 
            true, 
            false
        );

        chart.addChangeListener(this);
        chart.setBackgroundPaint(Color.white);
        XYPlot plot = chart.getXYPlot();
        plot.setOrientation(PlotOrientation.VERTICAL);
        plot.setBackgroundPaint(Color.lightGray);
        plot.setDomainGridlinePaint(Color.white);
        plot.setRangeGridlinePaint(Color.white);
        plot.setAxisOffset(new Spacer(Spacer.ABSOLUTE, 5.0, 5.0, 5.0, 5.0));
        
        plot.setDomainCrosshairVisible(true);
        plot.setRangeCrosshairVisible(true);
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
    private XYDataset createDataset(String name, double base, RegularTimePeriod start, int count) {

        this.series = new TimeSeries(name, start.getClass());
        RegularTimePeriod period = start;
        double value = base;
        for (int i = 0; i < count; i++) {
            this.series.add(period, value);    
            period = period.next();
            value = value * (1 + (Math.random() - 0.495) / 10.0);
        }

        TimeSeriesCollection dataset = new TimeSeriesCollection();
        dataset.addSeries(this.series);
        dataset.setPosition(TimeSeriesCollection.MIDDLE);

        return dataset;

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
                this.model.setValueAt(new Long(millis), 0, 1);
                int itemIndex = this.series.getIndex(new Minute(new Date(millis)));
                if (itemIndex >= 0) {
                    TimeSeriesDataItem item = this.series.getDataPair(Math.min(199, Math.max(0, itemIndex)));
                    TimeSeriesDataItem prevItem = this.series.getDataPair(Math.max(0, itemIndex - 1));
                    TimeSeriesDataItem nextItem = this.series.getDataPair(Math.min(199, itemIndex + 1));
                    long x = item.getPeriod().getMiddleMillisecond();      
                    double y = item.getValue().doubleValue();          
                    long prevX = prevItem.getPeriod().getMiddleMillisecond();      
                    double prevY = prevItem.getValue().doubleValue();          
                    long nextX = nextItem.getPeriod().getMiddleMillisecond();
                    double nextY = nextItem.getValue().doubleValue();
                    this.model.setValueAt(new Long(x), 0, 1);
                    this.model.setValueAt(new Double(y), 0, 2);
                    this.model.setValueAt(new Long(prevX), 0, 3);
                    this.model.setValueAt(new Double(prevY), 0, 4);
                    this.model.setValueAt(new Long(nextX), 0, 5);
                    this.model.setValueAt(new Double(nextY), 0, 6);
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

        CrosshairDemo1 demo = new CrosshairDemo1("Crosshair Demo 1");
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
            return 1;
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
