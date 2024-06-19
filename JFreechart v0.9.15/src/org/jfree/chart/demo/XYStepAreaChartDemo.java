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
 * ------------------------
 * XYStepAreaChartDemo.java
 * ------------------------
 * (C) Copyright 2003 by Object Refinery Limited and Contributors.
 *
 * Original Author:  Matthias Rose (for Ablay & Fodi GmbH);
 * Contributor(s):   David Gilbert (for Object Refinery Limited);
 *
 * $Id: XYStepAreaChartDemo.java,v 1.1 2007/10/10 19:21:51 vauchers Exp $
 *
 * Changes
 * -------
 * 26-Sep-2003 : Copied XYAreaChartDemo --> XYStepAreaChartDemo
 *               and adapted to test XYStepAreaRenderer (MR);
 *
 */

package org.jfree.chart.demo;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.XYStepAreaRenderer;
import org.jfree.data.XYDataset;
import org.jfree.data.XYSeries;
import org.jfree.data.XYSeriesCollection;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;

/**
 * A simple demonstration application showing how to create a step area chart.
 *
 * @author Matthias Rose
 */
public class XYStepAreaChartDemo extends ApplicationFrame implements ActionListener {

    /** Vertical orientation. */
    private static final String ORIENT_VERT = "Plot vertical";
    
    /** Horizontal orientation. */
    private static final String ORIENT_HORIZ = "Plot horizontal";

    /** Test data. */
    private static final Object[][] TEST_DATA = {
        // domain values,   range values,    may be null?
        {new Integer(1), new Integer(500), Boolean.TRUE},
        {new Integer(2), new Integer(694)},
        {new Integer(3), new Integer(-734)},
        {new Integer(4), new Integer(453)},
        {new Integer(5), new Integer(500), Boolean.TRUE},
        {new Integer(6), new Integer(200)},
        {new Integer(7), new Integer(550), Boolean.TRUE},
        {new Integer(8), new Integer(-150), Boolean.TRUE},
        {new Integer(9), new Integer(232)},
        {new Integer(10), new Integer(734)},
        {new Integer(11), new Integer(400), Boolean.TRUE},
    };

    /** The chart panel. */
    private ChartPanel chartPanel;
    
    /** The data series. */
    private XYSeries xySeries;
    
    /** The null values checkbox. */
    private JCheckBox nullValuesCheckBox;

    /** The outline checkbox. */
    private JCheckBox outlineCheckBox;
    
    /** The range base text field. */
    private JTextField rangeBaseTextField;
    
    /** The orientation combobox. */
    private JComboBox orientationComboBox;  
    
    /**
     * Creates a new demo.
     *
     * @param title  the frame title.
     */
    public XYStepAreaChartDemo(String title) {

        super(title);

        xySeries = new XYSeries("Some data");
        for (int i = 0; i < TEST_DATA.length; i++) {
            xySeries.add((Integer) TEST_DATA[i][0], (Integer) TEST_DATA[i][1]);
        }

        XYSeriesCollection dataset = new XYSeriesCollection(xySeries);
        
        JFreeChart chart = createChart(dataset);
        
        chartPanel = new ChartPanel(chart);

        // allow zooming
        chartPanel.setHorizontalZoom(true);
        chartPanel.setVerticalZoom(true);
        
        // size
        chartPanel.setPreferredSize(new java.awt.Dimension(500, 270));

        // make stroke more striking
        Plot plot = chartPanel.getChart().getPlot();
        plot.setOutlineStroke(new BasicStroke(2));
        plot.setOutlinePaint(Color.magenta);

        // add some components to make options changable
        JPanel main = new JPanel(new BorderLayout());
        JPanel optionsPanel = new JPanel();
        
        String[] options = {ORIENT_VERT, ORIENT_HORIZ};
        orientationComboBox = new JComboBox(options);
        orientationComboBox.addActionListener(this);
        optionsPanel.add(orientationComboBox);

        outlineCheckBox = new JCheckBox("Outline");
        outlineCheckBox.addActionListener(this);
        optionsPanel.add(outlineCheckBox);

        optionsPanel.add(new JLabel("Base"));
        rangeBaseTextField = new JTextField("0", 5);
        rangeBaseTextField.addActionListener(this);
        optionsPanel.add(rangeBaseTextField);

        nullValuesCheckBox = new JCheckBox("NULL values");
        nullValuesCheckBox.addActionListener(this);
        optionsPanel.add(nullValuesCheckBox);

        main.add(optionsPanel, BorderLayout.SOUTH);
        main.add(chartPanel);
        setContentPane(main);
    }

    // ****************************************************************************
    // * COMMERCIAL SUPPORT / JFREECHART DEVELOPER GUIDE                          *
    // * Please note that commercial support and documentation is available from: *
    // *                                                                          *
    // * http://www.object-refinery.com/jfreechart/support.html                   *
    // *                                                                          *
    // * This is not only a great service for developers, but is a VERY IMPORTANT *
    // * source of funding for the JFreeChart project.  Please support us so that *
    // * we can continue developing free software.                                *
    // ****************************************************************************

    /**
     * Creates a chart.
     * 
     * @param dataset  the dataset.
     * 
     * @return A chart.
     */
    private JFreeChart createChart(XYDataset dataset) {
            
        JFreeChart chart = ChartFactory.createXYStepAreaChart(
            "XY Step Area Chart Demo",
            "Domain (X)", "Range (Y)",
            dataset,
            PlotOrientation.VERTICAL,
            true,  // legend
            true,  // tool tips
            false  // URLs
        );
        
        // color
        XYPlot plot = chart.getXYPlot();
        plot.getRenderer().setSeriesPaint(0, Color.green);

        // fill shapes      
        XYStepAreaRenderer rend = (XYStepAreaRenderer) plot.getRenderer();
        rend.setShapesFilled(true);

        return chart;        
    }

    /**
     * Change options according to settings.
     * 
     * @param evt  the event.
     */
    public void actionPerformed(ActionEvent evt) {

        Object source = evt.getSource();

        if (source == nullValuesCheckBox) {
            
            boolean withNulls = nullValuesCheckBox.isSelected();
            for (int i = 0; i < TEST_DATA.length; i++) {
                Integer yVal = (Integer) TEST_DATA[i][1];
                if (withNulls && TEST_DATA[i].length > 2) {
                    yVal = null;
                }
                xySeries.getDataItem(i).setY(yVal);
            }
            
        } 
        else if (source == outlineCheckBox) {

            XYPlot plot = (XYPlot) chartPanel.getChart().getPlot();
            ((XYStepAreaRenderer) plot.getRenderer()).setOutline(outlineCheckBox.isSelected());
            
        } 
        else if (source == rangeBaseTextField) {
            
            double val = Double.parseDouble(rangeBaseTextField.getText());
            XYPlot plot = (XYPlot) chartPanel.getChart().getPlot();
            XYStepAreaRenderer rend = (XYStepAreaRenderer) plot.getRenderer();
            rend.setRangeBase(val);     

        } 
        else if (source == orientationComboBox) {

            XYPlot plot = (XYPlot) chartPanel.getChart().getPlot();
            if (orientationComboBox.getSelectedItem() == ORIENT_HORIZ) {
                plot.setOrientation(PlotOrientation.HORIZONTAL);        
            }    
            else if (orientationComboBox.getSelectedItem() == ORIENT_VERT) {
                plot.setOrientation(PlotOrientation.VERTICAL);          
            }
        }

        chartPanel.repaint();
    }

    /**
     * Starting point for the demonstration application.
     *
     * @param args  ignored.
     */
    public static void main(String[] args) {
        XYStepAreaChartDemo demo = new XYStepAreaChartDemo("Step Area XY Chart Demo");
        demo.pack();
        RefineryUtilities.centerFrameOnScreen(demo);
        demo.setVisible(true);
    }
}
