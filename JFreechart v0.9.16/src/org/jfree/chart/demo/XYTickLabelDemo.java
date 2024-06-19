/* ===========================================================
 * JFreeChart : a free chart library for the Java(tm) platform
 * ===========================================================
 *
 * (C) Copyright 2000-2004, by Object Refinery Limited and Contributors.
 *
 * Project Info:  http://www.jfree.org/jfreechart/index.html
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
 * [Java is a trademark or registered trademark of Sun Microsystems, Inc. 
 * in the United States and other countries.]
 *
 * --------------------
 * XYTickLabelDemo.java
 * --------------------
 * (C) Copyright 2003, 2004, by Object Refinery Limited and Contributors.
 *
 * Original Author:  Matthias Rose;
 * Contributor(s):   -;
 *
 * $Id: XYTickLabelDemo.java,v 1.1 2007/10/10 19:25:26 vauchers Exp $
 *
 * Changes
 * -------
 * 15-Jul-2002 : Version 1 (MR);
 *
 */


package org.jfree.chart.demo;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.Spacer;
import org.jfree.chart.axis.AxisLocation;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.SymbolicAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.StandardXYItemRenderer;
import org.jfree.data.XYSeries;
import org.jfree.data.XYSeriesCollection;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;

/**
 * An example which shows some bugs with tick labels in version 0.9.13
 *
 * @author Matthias Rose
 */
public class XYTickLabelDemo extends ApplicationFrame implements ActionListener {

    private static int DEFAULT_FONT_SIZE = 13; // causes some overlapping 

    /** the chart */
    JFreeChart chart;

    /** Tick labels vertical? */
    private JCheckBox verticalTickLabelsCheckBox;

    /** Plot horizontal? */
    private JCheckBox horizontalPlotCheckBox;

    /** SymbolicAxes? */
    private JCheckBox symbolicAxesCheckBox;

    /** Tick labels font size entry field */
    private JTextField fontSizeTextField;

    /**
     * A demonstration application showing some bugs with tick labels in version 0.9.13
     *
     * @param title  the frame title.
     */
    public XYTickLabelDemo(String title) {

        super(title);
        chart = createChart();
        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new java.awt.Dimension(600, 270));

        JPanel mainPanel = new JPanel(new BorderLayout());
        setContentPane(mainPanel);
        mainPanel.add(chartPanel);

        JPanel optionsPanel = new JPanel();
        mainPanel.add(optionsPanel, BorderLayout.SOUTH);
        
        symbolicAxesCheckBox = new JCheckBox("Symbolic axes");
        symbolicAxesCheckBox.addActionListener(this);
        optionsPanel.add(symbolicAxesCheckBox);

        verticalTickLabelsCheckBox = new JCheckBox("Tick labels vertical");
        verticalTickLabelsCheckBox.addActionListener(this);
        optionsPanel.add(verticalTickLabelsCheckBox);

        fontSizeTextField = new JTextField(3);
        fontSizeTextField.addActionListener(this);
        optionsPanel.add(new JLabel("Font size:"));
        optionsPanel.add(fontSizeTextField);
        ValueAxis axis = chart.getXYPlot().getDomainAxis();
        fontSizeTextField.setText(DEFAULT_FONT_SIZE+"");

        XYPlot plot = chart.getXYPlot(); 
        Font ft = axis.getTickLabelFont();
        ft = ft.deriveFont((float) DEFAULT_FONT_SIZE);
        plot.getDomainAxis().setTickLabelFont(ft);
        plot.getRangeAxis().setTickLabelFont(ft);
        plot.getSecondaryDomainAxis(0).setTickLabelFont(ft);
        plot.getSecondaryRangeAxis(0).setTickLabelFont(ft);

        
        horizontalPlotCheckBox = new JCheckBox("Plot horizontal");
        horizontalPlotCheckBox.addActionListener(this);
        optionsPanel.add(horizontalPlotCheckBox);       
    }

    /**
     * When a checkbox is changed ...
     * 
     * @param event  the event.
     */
    public void actionPerformed(ActionEvent event)
    {
        ValueAxis[] axes = new ValueAxis[4];
        XYPlot plot = chart.getXYPlot(); 
        axes[0] = plot.getDomainAxis();
        axes[1] = plot.getRangeAxis();
        axes[2] = plot.getSecondaryDomainAxis(0);
        axes[3] = plot.getSecondaryRangeAxis(0);

        Object source = event.getSource();
        
        if(source == symbolicAxesCheckBox) {

            boolean val = symbolicAxesCheckBox.isSelected();
        
            for(int i = 0; i < axes.length; i++)
            {
                ValueAxis axis = axes[i];
                String label = axis.getLabel(); 
                int maxTick = (int) axis.getUpperBound();
                String[] tickLabels = new String[maxTick];
                Font ft = axis.getTickLabelFont();
                for(int itk = 0; itk <  maxTick; itk++)
                    tickLabels[itk] = "Label " + itk;
                axis = val ? new SymbolicAxis(label, tickLabels) : new NumberAxis(label);  
                axis.setTickLabelFont(ft);
                axes[i] = axis;
            }
            plot.setDomainAxis(axes[0]);
            plot.setRangeAxis(axes[1]);
            plot.setSecondaryDomainAxis(0, axes[2]);
            plot.setSecondaryRangeAxis(0, axes[3]);     

        }
        
        if(source == symbolicAxesCheckBox || source == verticalTickLabelsCheckBox) {
            boolean val = verticalTickLabelsCheckBox.isSelected();
                        
            for(int i = 0; i < axes.length; i++)
                axes[i].setVerticalTickLabels(val);
            
        } else if (source == symbolicAxesCheckBox || source == horizontalPlotCheckBox) {
            
            PlotOrientation val = horizontalPlotCheckBox.isSelected() ?
            PlotOrientation.HORIZONTAL : PlotOrientation.VERTICAL;
            chart.getXYPlot().setOrientation(val);
            
        } else if (source == symbolicAxesCheckBox || source == fontSizeTextField) {
            String s = fontSizeTextField.getText();
            if(s.length() > 0) {
                float sz = Float.parseFloat(s);
                for(int i = 0; i < axes.length; i++) {
                    ValueAxis axis = axes[i];
                    Font ft = axis.getTickLabelFont();
                    ft = ft.deriveFont(sz);
                    axis.setTickLabelFont(ft);
                }
            }
        }       
    }

    /**
     * Creates the demo chart.
     * 
     * @return The chart.
     */
    private JFreeChart createChart() {

        // create some sample data
        
        XYSeries series1 = new XYSeries("Something");
        series1.add(0.0, 30.0);
        series1.add(1.0, 10.0);
        series1.add(2.0, 40.0);
        series1.add(3.0, 30.0);
        series1.add(4.0, 50.0);
        series1.add(5.0, 50.0);
        series1.add(6.0, 70.0);
        series1.add(7.0, 70.0);
        series1.add(8.0, 80.0);

        XYSeriesCollection dataset1 = new XYSeriesCollection();
        dataset1.addSeries(series1);
  
        XYSeries series2 = new XYSeries("Something else");
        series2.add(0.0, 5.0);
        series2.add(1.0, 4.0);
        series2.add(2.0, 1.0);
        series2.add(3.0, 5.0);
        series2.add(4.0, 0.0);

        XYSeriesCollection dataset2 = new XYSeriesCollection();
        dataset2.addSeries(series2);
  
        // create the chart
        
        JFreeChart chart = ChartFactory.createXYLineChart(
            "Tick Label Demo", 
            "Domain Axis 1",
            "Range Axis 1",
            dataset1, 
            PlotOrientation.VERTICAL,
            false, 
            true, 
            false
        );

        chart.setBackgroundPaint(Color.white);
        XYPlot plot = chart.getXYPlot();
        plot.setOrientation(PlotOrientation.VERTICAL);
        plot.setBackgroundPaint(Color.lightGray);
        plot.setDomainGridlinePaint(Color.white);
        plot.setRangeGridlinePaint(Color.white);
        plot.setAxisOffset(new Spacer(Spacer.ABSOLUTE, 5.0, 5.0, 5.0, 5.0));
        
        StandardXYItemRenderer renderer = (StandardXYItemRenderer) plot.getRenderer();
        renderer.setPaint(Color.black);
       
        // DOMAIN AXIS 2
        NumberAxis xAxis2 = new NumberAxis("Domain Axis 2");
        xAxis2.setAutoRangeIncludesZero(false);
        plot.setSecondaryDomainAxis(0, xAxis2);
       
        // RANGE AXIS 2
        DateAxis yAxis1 = new DateAxis("Range Axis 1");
        plot.setRangeAxis(yAxis1);
        
        DateAxis yAxis2 = new DateAxis("Range Axis 2");
        plot.setSecondaryRangeAxis(0, yAxis2);
        plot.setSecondaryRangeAxisLocation(0, AxisLocation.BOTTOM_OR_RIGHT);

        plot.setSecondaryDataset(0, dataset2);    
        plot.mapSecondaryDatasetToDomainAxis(0, new Integer(0));
        plot.mapSecondaryDatasetToRangeAxis(0, new Integer(0));    
        
        return chart;        
    }

    /**
     * Starting point for the demonstration application.
     *
     * @param args  ignored.
     */
    public static void main(String[] args) {

        XYTickLabelDemo demo = new XYTickLabelDemo("Tick Label Demo");
        demo.pack();
        RefineryUtilities.centerFrameOnScreen(demo);
        demo.setVisible(true);

    }
}

