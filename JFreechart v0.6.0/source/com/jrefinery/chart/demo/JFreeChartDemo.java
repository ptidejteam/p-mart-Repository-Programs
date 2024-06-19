/* ===============
 * JFreeChart Demo
 * ===============
 *
 * Project Info:  http://www.jrefinery.com/jfreechart;
 * Project Lead:  David Gilbert (david.gilbert@jrefinery.com);
 *
 * This file...
 * $Id: JFreeChartDemo.java,v 1.1 2007/10/10 18:53:20 vauchers Exp $
 *
 * Original Author:  David Gilbert;
 * Contributor(s):   Andrzej Porebski;
 *                   Matthew Wright;
 *                   Serge V. Grachov;
 *
 * (C) Copyright 2000, 2001 by Simba Management Limited;
 *
 * This program is free software; you can redistribute it and/or modify it under the terms
 * of the GNU General Public License as published by the Free Software Foundation;
 * either version 2 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program;
 * if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston,
 * MA 02111-1307, USA.
 *
 * Changes (from 22-Jun-2001)
 * --------------------------
 * 22-Jun-2001 : Modified to use new title code (DG);
 * 23-Jun-2001 : Added null data source chart (DG);
 * 24-Aug-2001 : Fixed DOS encoding problem (DG);
 * 15-Oct-2001 : Data source classes moved to com.jrefinery.data.* (DG);
 * 19-Oct-2001 : Implemented new ChartFactory class (DG);
 * 22-Oct-2001 : Added panes for stacked bar charts and a scatter plot (DG);
 *               Renamed DataSource.java --> Dataset.java etc. (DG);
 * 31-Oct-2001 : Added some negative values to the sample CategoryDataset (DG);
 *               Added 3D-effect bar plots by Serge V. Grachov (DG);
 * 07-Nov-2001 : Separated the JCommon Class Library classes, JFreeChart now requires
 *               jcommon.jar (DG);
 *               New flag in ChartFactory to control whether or not a legend is added to the
 *               chart (DG);
 * 15-Nov-2001 : Changed TimeSeriesDataset to TimeSeriesCollection (DG);
 * 17-Nov-2001 : For pie chart, changed dataset from CategoryDataset to PieDataset (DG);
 * 26-Nov-2001 : Moved property editing, saving and printing to the JFreeChartPanel class (DG);
 *
 */

package com.jrefinery.chart.demo;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.awt.print.*;
import java.io.*;
import java.util.*;
import javax.swing.*;
import com.jrefinery.data.*;
import com.jrefinery.layout.*;
import com.jrefinery.chart.*;
import com.jrefinery.chart.data.*;
import com.jrefinery.chart.ui.*;
import com.jrefinery.ui.*;

/**
 * The main frame in the chart demonstration application.
 */
public class JFreeChartDemo extends JFrame
                            implements ActionListener, WindowListener {

    /** The preferred size for the frame. */
    public static final Dimension PREFERRED_SIZE = new Dimension(600, 400);

    /** A frame for displaying a horizontal bar chart. */
    private JFreeChartFrame horizontalBarChartFrame;

    /** A frame for displaying a horizontal stacked bar chart. */
    private JFreeChartFrame horizontalStackedBarChartFrame;

    /** A frame for displaying a vertical bar chart. */
    private JFreeChartFrame verticalBarChartFrame;

    /** A frame for displaying a vertical stacked bar chart. */
    private JFreeChartFrame verticalStackedBarChartFrame;

    /** A frame for displaying a vertical 3D bar chart. */
    private JFreeChartFrame vertical3DBarChartFrame;

    /** A frame for displaying a vertical stacked 3D bar chart. */
    private JFreeChartFrame verticalStacked3DBarChartFrame;

    /** A frame for displaying a vertical XY bar chart. */
    private JFreeChartFrame verticalXYBarChartFrame;

    /** A frame for displaying a line chart. */
    private JFreeChartFrame lineChartFrame;

    /** A frame for displaying a pie chart. */
    private JFreeChartFrame pieChartOneFrame;

    /** A frame for displaying a pie chart. */
    private JFreeChartFrame pieChartTwoFrame;

    /** A frame for displaying a scatter plot chart. */
    private JFreeChartFrame scatterPlotFrame;

    /** A frame for displaying an XY plot chart. */
    private JFreeChartFrame xyPlotFrame;

    /** A frame for displaying a chart with null data. */
    private JFreeChartFrame xyPlotNullDataFrame;

    /** A frame for displaying a chart with zero data series. */
    private JFreeChartFrame xyPlotZeroDataFrame;

    /** A frame for displaying a time series chart. */
    private JFreeChartFrame timeSeriesChartFrame;

    /** A frame for displaying a time series chart with a moving average. */
    private JFreeChartFrame timeSeriesWithMAChartFrame;

    /** A frame for displaying a chart in a scroll pane. */
    private JFreeChartFrame timeSeriesChartScrollFrame;

    /** A frame for displaying a high/low/open/close chart. */
    private JFreeChartFrame highLowChartFrame;

    /** A frame for displaying a dynamic XY plot chart. */
    private JFreeChartFrame dynamicXYChartFrame;

    /** A frame for displaying information about the application. */
    private AboutFrame aboutFrame;

    /** A tabbed pane for displaying sample charts; */
    private JTabbedPane tabbedPane;

    /**
     * Constructs a demonstration application for the JFreeChart Class Library.
     */
    public JFreeChartDemo() {

        super("JFreeChart "+JFreeChart.VERSION+" Demo");

        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                dispose();
                System.exit(0);
            }
        });

        // set up the menu
        JMenuBar menuBar = createMenuBar();
        setJMenuBar(menuBar);

        JPanel content = new JPanel(new BorderLayout());
        content.add(createTabbedPane());
        setContentPane(content);

    }

    /**
     * Returns the preferred size for the frame.
     * @return The preferred size for the frame.
     */
    public Dimension getPreferredSize() {
        return PREFERRED_SIZE;
    }

    /**
     * Creates a tabbed pane containing descriptions of the demo charts.
     */
    private JTabbedPane createTabbedPane() {

        JTabbedPane tabs = new JTabbedPane();
        Font font = new Font("Dialog", Font.PLAIN, 12);

        JPanel barPanel = new JPanel(new LCBLayout(20));
        barPanel.setPreferredSize(new Dimension(360, 20));
        barPanel.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));

        JPanel piePanel = new JPanel(new LCBLayout(20));
        piePanel.setPreferredSize(new Dimension(360, 20));
        piePanel.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));

        JPanel xyPanel = new JPanel(new LCBLayout(20));
        xyPanel.setPreferredSize(new Dimension(360, 20));
        xyPanel.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));

        JPanel timeSeriesPanel = new JPanel(new LCBLayout(20));
        timeSeriesPanel.setPreferredSize(new Dimension(360, 20));
        timeSeriesPanel.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));

        JPanel otherPanel = new JPanel(new LCBLayout(20));
        otherPanel.setPreferredSize(new Dimension(360, 20));
        otherPanel.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));

        JPanel testPanel = new JPanel(new LCBLayout(20));
        testPanel.setPreferredSize(new Dimension(360, 20));
        testPanel.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));

        barPanel.add(SwingRefinery.createJLabel("Horizontal Bar Chart: ", font));
        barPanel.add(new DescriptionPanel(new JTextArea("Displays horizontal bars, representing data from a CategoryDataset.")));
        JButton b1 = SwingRefinery.createJButton("Display", font);
        b1.setActionCommand("HORIZONTAL_BAR_CHART");
        b1.addActionListener(this);
        barPanel.add(b1);


        barPanel.add(SwingRefinery.createJLabel("Horizontal Stacked Bar Chart: ", font));
        barPanel.add(new DescriptionPanel(new JTextArea("Displays stacked horizontal bars, representing data from a CategoryDataset.")));
        JButton b2 = SwingRefinery.createJButton("Display", font);
        b2.setActionCommand("HORIZONTAL_STACKED_BAR_CHART");
        b2.addActionListener(this);
        barPanel.add(b2);

        barPanel.add(SwingRefinery.createJLabel("Vertical Bar Chart: ", font));
        barPanel.add(new DescriptionPanel(new JTextArea("Displays vertical bars, representing data from a CategoryDataset.")));
        JButton b3 = SwingRefinery.createJButton("Display", font);
        b3.setActionCommand("VERTICAL_BAR_CHART");
        b3.addActionListener(this);
        barPanel.add(b3);

        barPanel.add(SwingRefinery.createJLabel("Vertical 3D Bar Chart: ", font));
        barPanel.add(new DescriptionPanel(new JTextArea("Displays stacked vertical bars with a 3D effect, representing data from a CategoryDataset.")));
        JButton b6 = SwingRefinery.createJButton("Display", font);
        b6.setActionCommand("VERTICAL_3D_BAR_CHART");
        b6.addActionListener(this);
        barPanel.add(b6);

        barPanel.add(SwingRefinery.createJLabel("Vertical Stacked Bar Chart: ", font));
        barPanel.add(new DescriptionPanel(new JTextArea("Displays stacked vertical bars, representing data from a CategoryDataset.")));
        JButton b4 = SwingRefinery.createJButton("Display", font);
        b4.setActionCommand("VERTICAL_STACKED_BAR_CHART");
        b4.addActionListener(this);
        barPanel.add(b4);

        barPanel.add(SwingRefinery.createJLabel("Vertical Stacked 3D Bar Chart: ", font));
        barPanel.add(new DescriptionPanel(new JTextArea("Displays stacked vertical bars with a 3D effect, representing data from a CategoryDataset.")));
        JButton b5 = SwingRefinery.createJButton("Display", font);
        b5.setActionCommand("VERTICAL_STACKED_3D_BAR_CHART");
        b5.addActionListener(this);
        barPanel.add(b5);

        otherPanel.add(SwingRefinery.createJLabel("Line Chart: ", font));
        otherPanel.add(new DescriptionPanel(new JTextArea("A chart displaying lines and or shapes, representing data in a CategoryDataset.")));
        JButton b8 = SwingRefinery.createJButton("Display", font);
        b8.setActionCommand("LINE_CHART");
        b8.addActionListener(this);
        otherPanel.add(b8);

        piePanel.add(SwingRefinery.createJLabel("Pie Chart: ", font));
        piePanel.add(new DescriptionPanel(new JTextArea("A pie chart showing one section exploded.")));
        JButton b9 = SwingRefinery.createJButton("Display", font);
        b9.setActionCommand("PIE_CHART_1");
        b9.addActionListener(this);
        piePanel.add(b9);

        piePanel.add(SwingRefinery.createJLabel("Pie Chart 2: ", font));
        piePanel.add(new DescriptionPanel(new JTextArea("A pie chart showing percentage labels.")));
        JButton b10 = SwingRefinery.createJButton("Display", font);
        b10.setActionCommand("PIE_CHART_2");
        b10.addActionListener(this);
        piePanel.add(b10);

        otherPanel.add(SwingRefinery.createJLabel("Scatter Plot: ", font));
        otherPanel.add(new DescriptionPanel(new JTextArea("A scatter plot, based on data from an XYDataset.")));
        JButton b11 = SwingRefinery.createJButton("Display", font);
        b11.setActionCommand("SCATTER_PLOT");
        b11.addActionListener(this);
        otherPanel.add(b11);

        xyPanel.add(SwingRefinery.createJLabel("XY Plot: ", font));
        xyPanel.add(new DescriptionPanel(new JTextArea("A line chart, based on data from an XYDataset.")));
        JButton b12 = SwingRefinery.createJButton("Display", font);
        b12.setActionCommand("XY_PLOT");
        b12.addActionListener(this);
        xyPanel.add(b12);

        testPanel.add(SwingRefinery.createJLabel("Null Data: ", font));
        testPanel.add(new DescriptionPanel(new JTextArea("A chart with a null dataset.")));
        JButton b13 = SwingRefinery.createJButton("Display", font);
        b13.setActionCommand("XY_PLOT_NULL");
        b13.addActionListener(this);
        testPanel.add(b13);

        testPanel.add(SwingRefinery.createJLabel("Zero Data: ", font));
        testPanel.add(new DescriptionPanel(new JTextArea("A chart with a dataset containing zero series..")));
        JButton b14 = SwingRefinery.createJButton("Display", font);
        b14.setActionCommand("XY_PLOT_ZERO");
        b14.addActionListener(this);
        testPanel.add(b14);

        timeSeriesPanel.add(SwingRefinery.createJLabel("Time Series Chart: ", font));
        timeSeriesPanel.add(new DescriptionPanel(new JTextArea("A time series chart, based on data from an XYDataset.")));
        JButton b15 = SwingRefinery.createJButton("Display", font);
        b15.setActionCommand("TIME_SERIES_CHART");
        b15.addActionListener(this);
        timeSeriesPanel.add(b15);

        timeSeriesPanel.add(SwingRefinery.createJLabel("Time Series 2: ", font));
        timeSeriesPanel.add(new DescriptionPanel(new JTextArea("A time series chart with a moving average, based on data from an XYDataset.")));
        JButton b16 = SwingRefinery.createJButton("Display", font);
        b16.setActionCommand("TIME_SERIES_WITH_MA_CHART");
        b16.addActionListener(this);
        timeSeriesPanel.add(b16);

        testPanel.add(SwingRefinery.createJLabel("Chart in JScrollPane: ", font));
        testPanel.add(new DescriptionPanel(new JTextArea("A chart embedded in a JScrollPane.")));
        JButton b18 = SwingRefinery.createJButton("Display", font);
        b18.setActionCommand("TIME_SERIES_CHART_SCROLL");
        b18.addActionListener(this);
        testPanel.add(b18);

        timeSeriesPanel.add(SwingRefinery.createJLabel("High/Low/Open/Close Chart: ", font));
        timeSeriesPanel.add(new DescriptionPanel(new JTextArea("A high/low/open/close chart based on data in HighLowDataset.")));
        JButton b19 = SwingRefinery.createJButton("Display", font);
        b19.setActionCommand("HIGH_LOW_CHART");
        b19.addActionListener(this);
        timeSeriesPanel.add(b19);

        otherPanel.add(SwingRefinery.createJLabel("Vertical XY Bar Chart: ", font));
        otherPanel.add(new DescriptionPanel(new JTextArea("A chart showing vertical bars, based on data in an IntervalXYDataset.")));
        JButton b7 = SwingRefinery.createJButton("Display", font);
        b7.setActionCommand("VERTICAL_XY_BAR_CHART");
        b7.addActionListener(this);
        otherPanel.add(b7);

        testPanel.add(SwingRefinery.createJLabel("Dynamic Chart: ", font));
        testPanel.add(new DescriptionPanel(new JTextArea("A dynamic chart, to test the event notification mechanism.")));
        JButton b20 = SwingRefinery.createJButton("Display", font);
        b20.setActionCommand("DYNAMIC_CHART");
        b20.addActionListener(this);
        testPanel.add(b20);

        tabs.add("Bar Charts", new JScrollPane(barPanel));
        tabs.add("Pie Charts", new JScrollPane(piePanel));
        tabs.add("XY Charts", new JScrollPane(xyPanel));
        tabs.add("Time Series Charts", new JScrollPane(timeSeriesPanel));
        tabs.add("Other Charts", new JScrollPane(otherPanel));
        tabs.add("Test Charts", new JScrollPane(testPanel));
        return tabs;

    }



    /**
     * Handles menu selections by passing control to an appropriate method.
     */
    public void actionPerformed(ActionEvent event) {
        String command = event.getActionCommand();
        if (command.equals("exitItem")) {
            attemptExit();
        }
        else if (command.equals("DYNAMIC_CHART")) {
            displayDynamicXYChart();
        }
        else if (command.equals("aboutItem")) {
            about();
        }
        else if (command.equals("VERTICAL_BAR_CHART")) {
            displayVerticalBarChart();
        }
        else if (command.equals("VERTICAL_STACKED_BAR_CHART")) {
            displayVerticalStackedBarChart();
        }
        else if (command.equals("VERTICAL_XY_BAR_CHART")) {
            displayVerticalXYBarChart();
        }
        else if (command.equals("VERTICAL_3D_BAR_CHART")) {
            displayVertical3DBarChart();
        }
        else if (command.equals("VERTICAL_STACKED_3D_BAR_CHART")) {
            displayVerticalStacked3DBarChart();
        }
        else if (command.equals("HORIZONTAL_BAR_CHART")) {
            displayHorizontalBarChart();
        }
        else if (command.equals("HORIZONTAL_STACKED_BAR_CHART")) {
            displayHorizontalStackedBarChart();
        }
        else if (command.equals("LINE_CHART")) {
            displayLineChart();
        }
        else if (command.equals("PIE_CHART_1")) {
            displayPieChartOne();
        }
        else if (command.equals("PIE_CHART_2")) {
            displayPieChartTwo();
        }
        else if (command.equals("XY_PLOT")) {
            displayXYPlot();
        }
        else if (command.equals("SCATTER_PLOT")) {
            displayScatterPlot();
        }
        else if (command.equals("TIME_SERIES_CHART")) {
            displayTimeSeriesChart();
        }
        else if (command.equals("TIME_SERIES_WITH_MA_CHART")) {
            displayTimeSeriesWithMAChart();
        }
        else if (command.equals("TIME_SERIES_CHART_SCROLL")) {
            displayTimeSeriesChartInScrollPane();
        }
        else if (command.equals("HIGH_LOW_CHART")) {
            displayHighLowChart();
        }
        else if (command.equals("XY_PLOT_NULL")) {
            displayNullXYPlot();
        }
        else if (command.equals("XY_PLOT_ZERO")) {
            displayXYPlotZeroData();
        }
    }


    /**
     * Creates a menubar.
     */
    private JMenuBar createMenuBar() {

        // create the menus
        JMenuBar menuBar = new JMenuBar();

        // first the file menu
        JMenu fileMenu = new JMenu("File", true);
        fileMenu.setMnemonic('F');


        JMenuItem exitItem = new JMenuItem("Exit", 'x');
        exitItem.setActionCommand("exitItem");
        exitItem.addActionListener(this);
        fileMenu.add(exitItem);

        // then the help menu
        JMenu helpMenu = new JMenu("Help");
        helpMenu.setMnemonic('H');

        JMenuItem aboutItem = new JMenuItem("About...", 'A');
        aboutItem.setActionCommand("aboutItem");
        aboutItem.addActionListener(this);
        helpMenu.add(aboutItem);

        // finally, glue together the menu and return it
        menuBar.add(fileMenu);
        menuBar.add(helpMenu);

        return menuBar;

    }

    /**
     * Exits the application, but only if the user agrees.
     */
    private void attemptExit() {
        int result = JOptionPane.showConfirmDialog(this,
                       "Are you sure you want to exit?", "Confirmation...",
                       JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (result==JOptionPane.YES_OPTION) {
            dispose();
            System.exit(0);
        }
    }

    /**
     * Displays an XY chart that is periodically updated by a background thread.  This is to
     * demonstrate the event notification system that automatically updates charts as required.
     */
    private void displayDynamicXYChart() {

        if (dynamicXYChartFrame==null) {

            SampleXYDataset data = new SampleXYDataset();
            JFreeChart chart = ChartFactory.createXYChart("Dynamic XY Chart", "X", "Y", data, true);
            SampleXYDatasetThread update = new SampleXYDatasetThread(data);
            dynamicXYChartFrame = new JFreeChartFrame("Dynamic Chart", chart);
            dynamicXYChartFrame.pack();
            SwingRefinery.positionFrameRandomly(dynamicXYChartFrame);
            dynamicXYChartFrame.show();
            Thread thread = new Thread(update);
            thread.start();
        }

    }

    /**
     * Displays a vertical bar chart in its own frame.
     */
    private void displayVerticalBarChart() {

        if (verticalBarChartFrame==null) {

            CategoryDataset categoryData = createCategoryDataset();
            JFreeChart chart = ChartFactory.createVerticalBarChart("Vertical Bar Chart",
                                   "Categories", "Values", categoryData, true);

            // then customise it a little...
            chart.setChartBackgroundPaint(new GradientPaint(0, 0, Color.white, 1000, 0, Color.red));
            Plot plot = chart.getPlot();
            HorizontalCategoryAxis hAxis = (HorizontalCategoryAxis)plot.getAxis(Plot.HORIZONTAL_AXIS);
                hAxis.setVerticalCategoryLabels(true);

            // and present it in a panel...
            verticalBarChartFrame = new JFreeChartFrame("Vertical Bar Chart", chart);
            verticalBarChartFrame.pack();
            SwingRefinery.positionFrameRandomly(verticalBarChartFrame);
            verticalBarChartFrame.show();

        }
        else {
            verticalBarChartFrame.show();
            verticalBarChartFrame.requestFocus();
        }

    }

    /**
     * Displays a vertical bar chart in its own frame.
     */
    private void displayVerticalStackedBarChart() {

        if (verticalStackedBarChartFrame==null) {

            // create a default chart based on some sample data...
            String title = "Vertical Stacked Bar Chart";
            String categoryAxisLabel = "Categories";
            String valueAxisLabel = "Values";
            CategoryDataset categoryData = createCategoryDataset();
            JFreeChart chart = ChartFactory.createStackedVerticalBarChart(title, categoryAxisLabel,
                                                               valueAxisLabel, categoryData, true);

            // then customise it a little...
            chart.setChartBackgroundPaint(new GradientPaint(0, 0, Color.white, 1000, 0, Color.red));
            Plot plot = chart.getPlot();
            VerticalNumberAxis valueAxis = (VerticalNumberAxis)plot.getAxis(Plot.VERTICAL_AXIS);
            valueAxis.setMinimumAxisValue(new Double(-32.0));
            valueAxis.setMaximumAxisValue(new Double(85.0));

            // and present it in a panel...
            verticalStackedBarChartFrame = new JFreeChartFrame("Vertical Stacked Bar Chart", chart);
            verticalStackedBarChartFrame.pack();
            SwingRefinery.positionFrameRandomly(verticalStackedBarChartFrame);
            verticalStackedBarChartFrame.show();

        }
        else {
            verticalStackedBarChartFrame.show();
            verticalStackedBarChartFrame.requestFocus();
        }

    }

    /**
     * Displays a vertical bar chart in its own frame.
     */
    private void displayVerticalStacked3DBarChart() {

        if (verticalStacked3DBarChartFrame==null) {

            // create a default chart based on some sample data...
            String title = "Vertical Stacked 3D Bar Chart";
            String categoryAxisLabel = "Categories";
            String valueAxisLabel = "Values";
            CategoryDataset categoryData = createCategoryDataset();
            JFreeChart chart = ChartFactory.createStackedVerticalBarChart3D(title, categoryAxisLabel,
                                                               valueAxisLabel, categoryData, true);

            // then customise it a little...
            chart.setChartBackgroundPaint(new GradientPaint(0, 0, Color.white, 1000, 0, Color.red));
            Plot plot = chart.getPlot();
            VerticalNumberAxis valueAxis = (VerticalNumberAxis)plot.getAxis(Plot.VERTICAL_AXIS);
            //valueAxis.setAutoRange(false);
            valueAxis.setMinimumAxisValue(new Double(-32.0));
            valueAxis.setMaximumAxisValue(new Double(85.0));

            // and present it in a panel...
            verticalStacked3DBarChartFrame = new JFreeChartFrame("Vertical Stacked 3D Bar Chart", chart);
            verticalStacked3DBarChartFrame.pack();
            SwingRefinery.positionFrameRandomly(verticalStacked3DBarChartFrame);
            verticalStacked3DBarChartFrame.show();

        }
        else {
            verticalStacked3DBarChartFrame.show();
            verticalStacked3DBarChartFrame.requestFocus();
        }

    }

    /**
     * Displays a vertical bar chart in its own frame.
     */
    private void displayVerticalXYBarChart() {

        if (verticalXYBarChartFrame==null) {

            // create a default chart based on some sample data...
            String title = "Time Series Bar Chart";
            String xAxisLabel = "X Axis";
            String yAxisLabel = "Y Axis";
            TimeSeriesCollection data = createTestTimeSeriesCollection1();
            JFreeChart chart = ChartFactory.createVerticalXYBarChart(title, xAxisLabel, yAxisLabel,
                                                                     data, true);


            // then customise it a little...
            chart.setChartBackgroundPaint(new GradientPaint(0, 0, Color.white, 1000, 0, Color.blue));

            // and present it in a panel...
            verticalXYBarChartFrame = new JFreeChartFrame("Vertical XY Bar Chart", chart);
            verticalXYBarChartFrame.pack();
            SwingRefinery.positionFrameRandomly(verticalXYBarChartFrame);
            verticalXYBarChartFrame.show();

        }
        else {
            verticalXYBarChartFrame.show();
            verticalXYBarChartFrame.requestFocus();
        }

    }

    /**
     * Displays a vertical 3D bar chart in its own frame.
     */
    private void displayVertical3DBarChart() {

        if (vertical3DBarChartFrame==null) {

        // create a default chart based on some sample data...
        String title = "Vertical Bar Chart (3D Effect)";
        String categoryAxisLabel = "Categories";
        String valueAxisLabel = "Values";
        CategoryDataset categoryData = createCategoryDataset();
        JFreeChart chart = ChartFactory.createVerticalBarChart3D(title, categoryAxisLabel,
                                                               valueAxisLabel, categoryData, true);

        // then customise it a little...
        chart.setChartBackgroundPaint(new GradientPaint(0, 0, Color.white, 1000, 0, Color.blue));
        Plot plot = chart.getPlot();

            // and present it in a panel...
            vertical3DBarChartFrame = new JFreeChartFrame("Vertical 3D Bar Chart", chart);
            vertical3DBarChartFrame.pack();
            SwingRefinery.positionFrameRandomly(vertical3DBarChartFrame);
            vertical3DBarChartFrame.show();

        }
        else {
            vertical3DBarChartFrame.show();
            vertical3DBarChartFrame.requestFocus();
        }

    }

    /**
     * Displays a horizontal bar chart in its own frame.
     */
    private void displayHorizontalBarChart() {

        if (horizontalBarChartFrame==null) {

            // create a default chart based on some sample data...
            String title = "Horizontal Bar Chart";
            String categoryAxisLabel = "Categories";
            String valueAxisLabel = "Values";
            CategoryDataset categoryData = createCategoryDataset();
            JFreeChart chart = ChartFactory.createHorizontalBarChart(title, categoryAxisLabel,
                                                                 valueAxisLabel, categoryData,
                                                                 true);

            // then customise it a little...
            chart.setChartBackgroundPaint(new GradientPaint(0, 0, Color.white,0, 1000, Color.orange));

            // and present it in a frame...
            horizontalBarChartFrame = new JFreeChartFrame("Horizontal Bar Chart", chart);
            horizontalBarChartFrame.pack();
            SwingRefinery.positionFrameRandomly(horizontalBarChartFrame);
            horizontalBarChartFrame.show();

        }
        else {
            horizontalBarChartFrame.show();
            horizontalBarChartFrame.requestFocus();
        }

    }

    /**
     * Displays a horizontal bar chart in its own frame.
     */
    private void displayHorizontalStackedBarChart() {

        if (horizontalStackedBarChartFrame==null) {

            // create a default chart based on some sample data...
            String title = "Horizontal Stacked Bar Chart";
            String categoryAxisLabel = "Categories";
            String valueAxisLabel = "Values";
            CategoryDataset categoryData = createCategoryDataset();
            JFreeChart chart = ChartFactory.createStackedHorizontalBarChart(title, categoryAxisLabel,
                                                               valueAxisLabel, categoryData, true);

            // then customise it a little...
            chart.setChartBackgroundPaint(new GradientPaint(0, 0, Color.white, 1000, 0, Color.blue));
            Plot plot = chart.getPlot();
            HorizontalNumberAxis valueAxis = (HorizontalNumberAxis)plot.getAxis(Plot.HORIZONTAL_AXIS);
            valueAxis.setMinimumAxisValue(new Double(-32.0));
            valueAxis.setMaximumAxisValue(new Double(85.0));

            // and present it in a frame...
            horizontalStackedBarChartFrame = new JFreeChartFrame("Horizontal Bar Chart", chart);
            horizontalStackedBarChartFrame.pack();
            SwingRefinery.positionFrameRandomly(horizontalStackedBarChartFrame);
            horizontalStackedBarChartFrame.show();

        }
        else {
            horizontalStackedBarChartFrame.show();
            horizontalStackedBarChartFrame.requestFocus();
        }

    }

    /**
     * Displays a line chart in its own frame.
     */
    private void displayLineChart() {

        if (lineChartFrame==null) {

            // create a default chart based on some sample data...
            String title = "Line Chart";
            String categoryAxisLabel = "Categories";
            String valueAxisLabel = "Values";
            CategoryDataset data = createCategoryDataset();
            JFreeChart chart = ChartFactory.createLineChart(title, categoryAxisLabel, valueAxisLabel,
                                                        data, true);

            // then customise it a little...
            chart.setChartBackgroundPaint(new GradientPaint(0, 0, Color.white,0, 1000, Color.yellow));

            // and present it in a frame...
            lineChartFrame = new JFreeChartFrame("Line Chart", chart);
            lineChartFrame.pack();
            SwingRefinery.positionFrameRandomly(lineChartFrame);
            lineChartFrame.show();

        }
        else {
            lineChartFrame.show();
            lineChartFrame.requestFocus();
        }

    }

    /**
     * Displays pie chart one in its own frame.
     */
    private void displayPieChartOne() {

        if (pieChartOneFrame==null) {

            // create a default chart based on some sample data...
            String title = "Pie Chart";
            CategoryDataset data = createCategoryDataset();
            PieDataset extracted = Datasets.createPieDataset(data, 0);
            JFreeChart chart = ChartFactory.createPieChart(title, extracted, true);

            // then customise it a little...
            chart.setChartBackgroundPaint(new GradientPaint(0, 0, Color.white, 0, 1000, Color.orange));
            PiePlot plot = (PiePlot)chart.getPlot();
            plot.setCircular(false);
            // make section 1 explode by 100%...
            plot.setRadiusPercent(0.60);
            plot.setExplodePercent(1, 1.00);

            // and present it in a frame...
            pieChartOneFrame = new JFreeChartFrame("Pie Chart 1", chart);
            pieChartOneFrame.pack();
            SwingRefinery.positionFrameRandomly(pieChartOneFrame);
            pieChartOneFrame.show();

        }
        else {
            pieChartOneFrame.show();
            pieChartOneFrame.requestFocus();
        }

    }

    /**
     * Displays pie chart two in its own frame.
     */
    private void displayPieChartTwo() {

        if (pieChartTwoFrame==null) {

            // create a default chart based on some sample data...
            String title = "Pie Chart";
            CategoryDataset data = createCategoryDataset();
            PieDataset extracted = Datasets.createPieDataset(data, "Category 2");
            JFreeChart chart = ChartFactory.createPieChart(title, extracted, true);

            // then customise it a little...
            chart.setLegend(null);
            chart.setChartBackgroundPaint(new GradientPaint(0, 0, Color.white, 0, 1000, Color.orange));
            PiePlot pie = (PiePlot)chart.getPlot();
            pie.setSectionLabelType(PiePlot.NAME_AND_PERCENT_LABELS);

            // and present it in a frame...
            pieChartTwoFrame = new JFreeChartFrame("Pie Chart 2", chart);
            pieChartTwoFrame.pack();
            SwingRefinery.positionFrameRandomly(pieChartTwoFrame);
            pieChartTwoFrame.show();

        }
        else {
            pieChartTwoFrame.show();
            pieChartTwoFrame.requestFocus();
        }

    }


    /**
     * Displays an XYPlot in its own frame.
     */
    private void displayXYPlot() {

        if (xyPlotFrame==null) {

            // create a default chart based on some sample data...
            String title = "XY Plot";
            String xAxisLabel = "X Axis";
            String yAxisLabel = "Y Axis";
            XYDataset data = new SampleXYDataset();
            JFreeChart chart = ChartFactory.createXYChart(title, xAxisLabel, yAxisLabel, data, true);

            // then customise it a little...
            chart.setChartBackgroundPaint(new GradientPaint(0, 0, Color.white,0, 1000, Color.green));

            // and present it in a frame...
            xyPlotFrame = new JFreeChartFrame("XYPlot", chart);
            xyPlotFrame.pack();
            SwingRefinery.positionFrameRandomly(xyPlotFrame);
            xyPlotFrame.show();

        }
        else {
            xyPlotFrame.show();
            xyPlotFrame.requestFocus();
        }

    }

    /**
     * Displays an XYPlot in its own frame.
     */
    private void displayXYPlotZeroData() {

        if (xyPlotZeroDataFrame==null) {

            // create a default chart based on some sample data...
            String title = "XY Plot (zero series)";
            String xAxisLabel = "X Axis";
            String yAxisLabel = "Y Axis";
            XYDataset data = new EmptyXYDataset();
            JFreeChart chart = ChartFactory.createXYChart(title, xAxisLabel, yAxisLabel, data, true);

            // then customise it a little...
            chart.setChartBackgroundPaint(new GradientPaint(0, 0, Color.white, 1000, 0, Color.red));

            // and present it in a frame...
            xyPlotZeroDataFrame = new JFreeChartFrame("XYPlot", chart);
            xyPlotZeroDataFrame.pack();
            SwingRefinery.positionFrameRandomly(xyPlotZeroDataFrame);
            xyPlotZeroDataFrame.show();

        }
        else {
            xyPlotZeroDataFrame.show();
            xyPlotZeroDataFrame.requestFocus();
        }

    }

    /**
     * Displays a scatter plot in its own frame.
     */
    private void displayScatterPlot() {

        if (scatterPlotFrame==null) {

            // create a default chart based on some sample data...
            String title = "Scatter Plot";
            String xAxisLabel = "X Axis";
            String yAxisLabel = "Y Axis";
            XYDataset scatterData = new SampleXYDataset2();
            JFreeChart chart = ChartFactory.createScatterPlot(title, xAxisLabel,
                                                          yAxisLabel, scatterData, true);

            // then customise it a little...
            chart.setChartBackgroundPaint(new GradientPaint(0, 0, Color.white, 1000, 0, Color.green));

            // and present it in a frame...
            scatterPlotFrame = new JFreeChartFrame("XYPlot", chart);
            scatterPlotFrame.pack();
            SwingRefinery.positionFrameRandomly(scatterPlotFrame);
            scatterPlotFrame.show();

        }
        else {
            scatterPlotFrame.show();
            scatterPlotFrame.requestFocus();
        }

    }

    /**
     * Displays a vertical bar chart in its own frame.
     */
    private void displayNullXYPlot() {

        if (this.xyPlotNullDataFrame==null) {

            // create a default chart based on some sample data...
            String title = "XY Plot (null data)";
            String xAxisLabel = "X Axis";
            String yAxisLabel = "Y Axis";
            XYDataset data = null;
            JFreeChart chart = ChartFactory.createXYChart(title, xAxisLabel, yAxisLabel, data, true);

            // then customise it a little...
            chart.setChartBackgroundPaint(new GradientPaint(0, 0, Color.white, 1000, 0, Color.red));

            // and present it in a panel...
            xyPlotNullDataFrame = new JFreeChartFrame("XY Plot with NULL data", chart);
            xyPlotNullDataFrame.pack();
            SwingRefinery.positionFrameRandomly(xyPlotNullDataFrame);
            xyPlotNullDataFrame.show();

        }
        else {
            xyPlotNullDataFrame.show();
            xyPlotNullDataFrame.requestFocus();
        }

    }

    /**
     * Displays a vertical bar chart in its own frame.
     */
    private void displayTimeSeriesChart() {

        if (this.timeSeriesChartFrame==null) {

            // create a default chart based on some sample data...
            String title = "Time Series Chart";
            String xAxisLabel = "Date";
            String yAxisLabel = "USD per GBP";
            XYDataset data = createXYDataset();
            JFreeChart chart = ChartFactory.createTimeSeriesChart(title, xAxisLabel, yAxisLabel, data,
                                                              true);

            // then customise it a little...
            TextTitle subtitle = new TextTitle("Value of GBP", new Font("Arial", Font.BOLD, 12));
            chart.addTitle(subtitle);
            chart.setChartBackgroundPaint(new GradientPaint(0, 0, Color.white,0, 1000, Color.blue));
            Plot plot = chart.getPlot();

            // and present it in a frame...
            timeSeriesChartFrame = new JFreeChartFrame("Time Series Chart", chart);
            timeSeriesChartFrame.pack();
            SwingRefinery.positionFrameRandomly(timeSeriesChartFrame);
            timeSeriesChartFrame.show();

        }
        else {
            timeSeriesChartFrame.show();
            timeSeriesChartFrame.requestFocus();
        }

    }

    /**
     * Displays a vertical bar chart in its own frame.
     */
    private void displayTimeSeriesWithMAChart() {

        if (this.timeSeriesWithMAChartFrame==null) {

            // create a default chart based on some sample data...
            String title = "Moving Average";
            String timeAxisLabel = "Date";
            String valueAxisLabel = "USD per GBP";
            XYDataset data = createXYDataset();
            MovingAveragePlotFitAlgorithm mavg = new MovingAveragePlotFitAlgorithm();
	    mavg.setPeriod(30);
            PlotFit pf = new PlotFit(data, mavg);
            data = pf.getFit();
            JFreeChart chart = ChartFactory.createTimeSeriesChart(title, timeAxisLabel, valueAxisLabel,
                                                              data, true);

            // then customise it a little...
            TextTitle subtitle = new TextTitle("30 day moving average of GBP", new Font("Arial", Font.BOLD, 12));
            chart.addTitle(subtitle);
            chart.setChartBackgroundPaint(new GradientPaint(0, 0, Color.white,0, 1000, Color.blue));


            // and present it in a frame...
            timeSeriesWithMAChartFrame = new JFreeChartFrame("Time Series Chart", chart);
            timeSeriesWithMAChartFrame.pack();
            SwingRefinery.positionFrameRandomly(timeSeriesWithMAChartFrame);
            timeSeriesWithMAChartFrame.show();

        }
        else {
            timeSeriesWithMAChartFrame.show();
            timeSeriesWithMAChartFrame.requestFocus();
        }

    }

    /**
     * Displays a vertical bar chart in its own frame.
     */
    private void displayHighLowChart() {

        if (this.highLowChartFrame==null) {

            // create a default chart based on some sample data...
            String title = "High-Low/Open-Close Chart";
            String timeAxisLabel = "Date";
            String valueAxisLabel = "Price ($ per share)";
            HighLowDataset data = new SampleHighLowDataset();
            JFreeChart chart = ChartFactory.createHighLowChart(title, timeAxisLabel, valueAxisLabel,
                                                           data, true);

            // then customise it a little...
            TextTitle subtitle = new TextTitle("IBM Stock Price", new Font("Arial", Font.BOLD, 12));
            chart.addTitle(subtitle);
            chart.setChartBackgroundPaint(new GradientPaint(0, 0, Color.white, 0, 1000, Color.magenta));


            // and present it in a frame...
            highLowChartFrame = new JFreeChartFrame("High/Low/Open/Close Chart", chart);
            highLowChartFrame.pack();
            SwingRefinery.positionFrameRandomly(highLowChartFrame);
            highLowChartFrame.show();

        }
        else {
            highLowChartFrame.show();
            highLowChartFrame.requestFocus();
        }

    }

    /**
     * Displays a vertical bar chart in its own frame.
     */
    private void displayTimeSeriesChartInScrollPane() {

        if (this.timeSeriesChartScrollFrame==null) {

            // create a default chart based on some sample data...
            String title = "Time Series Chart";
            String xAxisLabel = "Date";
            String yAxisLabel = "USD per GBP";
            XYDataset data = createXYDataset();
            JFreeChart chart = ChartFactory.createTimeSeriesChart(title, xAxisLabel, yAxisLabel, data,
                                                              true);

            // then customise it a little...
            TextTitle subtitle = new TextTitle("Value of GBP", new Font("Arial", Font.BOLD, 12));
            chart.addTitle(subtitle);
            chart.setChartBackgroundPaint(new GradientPaint(0, 0, Color.white,0, 1000, Color.gray));
            Plot plot = chart.getPlot();

            // and present it in a frame...
            timeSeriesChartScrollFrame = new JFreeChartFrame("Time Series Chart", chart, true);
            timeSeriesChartScrollFrame.pack();
            SwingRefinery.positionFrameRandomly(timeSeriesChartScrollFrame);
            timeSeriesChartScrollFrame.show();

        }
        else {
            timeSeriesChartScrollFrame.show();
            timeSeriesChartScrollFrame.requestFocus();
        }

    }

    /**
     * Displays information about the application.
     */
    private void about() {

        if (aboutFrame==null) {
            aboutFrame = new AboutFrame("About...",
                                        "JFreeChart",
                                        "Version "+JFreeChart.VERSION,
                                        "http://www.jrefinery.com/jfreechart",
                                        Licences.LGPL);
            aboutFrame.pack();
            SwingRefinery.centerFrameOnScreen(aboutFrame);
        }
        aboutFrame.show();
        aboutFrame.requestFocus();

    }

    /**
     * The starting point for the demonstration application.
     */
    public static void main(String[] args) {
        JFreeChartDemo f = new JFreeChartDemo();
        f.pack();

        SwingRefinery.centerFrameOnScreen(f);

        // and show it...
        f.show();
    }

    /**
     * Creates and returns a category dataset for the demo charts.
     */
    public CategoryDataset createCategoryDataset() {

        Number[][] data = new Integer[][]
            { { new Integer(10), new Integer(4), new Integer(15), new Integer(14) },
              { new Integer(-5), new Integer(-7), new Integer(14), new Integer(-3) },
              { new Integer(6), new Integer(17), new Integer(-12), new Integer(7) },
              { new Integer(7), new Integer(15), new Integer(11), new Integer(0) },
              { new Integer(-8), new Integer(-6), new Integer(10), new Integer(-9) },
              { new Integer(9), new Integer(8), null, new Integer(6) },
              { new Integer(-10), new Integer(9), new Integer(7), new Integer(7) },
              { new Integer(11), new Integer(13), new Integer(9), new Integer(9) },
              { new Integer(-3), new Integer(7), new Integer(11), new Integer(-10) } };

        return new DefaultCategoryDataset(data);

    }

    /**
     * Creates and returns a category dataset with JUST ONE CATEGORY for the demo charts.
     */
    public CategoryDataset createSingleCategoryDataset() {

        Number[][] data = new Integer[][]
            { { new Integer(10) },
              { new Integer(-5) },
              { new Integer(6) },
              { new Integer(7) },
              { new Integer(-8) },
              { new Integer(9) },
              { new Integer(-10) },
              { new Integer(11) },
              { new Integer(-3) } };

        return new DefaultCategoryDataset(data);

    }

    /**
     * Creates and returns a category dataset for the demo charts.
     */
    public CategoryDataset createSingleSeriesCategoryDataset() {

        Number[][] data = new Integer[][]
            { { new Integer(10), new Integer(-4), new Integer(15), new Integer(14) } };

        return new DefaultCategoryDataset(data);

    }

    /**
     * Returns a java.util.Date for the specified year, month and day.
     */
    private Date createDate(int year, int month, int day) {
        GregorianCalendar calendar = new GregorianCalendar(year, month, day);
        return calendar.getTime();
    }

    /**
     * Returns a java.util.Date for the specified year, month, day, hour and minute.
     */
    private Date createDateTime(int year, int month, int day, int hour, int minute) {
        GregorianCalendar calendar = new GregorianCalendar(year, month, day, hour, minute);
        return calendar.getTime();
    }

    /**
     * Creates and returns a XYDataset for the demo charts.
     */
    public XYDataset createXYDataset() {

        GregorianCalendar calendar = new GregorianCalendar(1999, Calendar.JANUARY, 4);

        Object[][][] data = new Object[][][] { {
            { createDate(1999, Calendar.JANUARY,4), new Double(1.6581) },
            { createDate(1999, Calendar.JANUARY,5), new Double(1.6566) },
            { createDate(1999, Calendar.JANUARY,6), new Double(1.6547) },
            { createDate(1999, Calendar.JANUARY,7), new Double(1.6495) },
            { createDate(1999, Calendar.JANUARY,8), new Double(1.6405) },
            { createDate(1999, Calendar.JANUARY,11), new Double(1.6375) },
            { createDate(1999, Calendar.JANUARY,12), new Double(1.6308) },
            { createDate(1999, Calendar.JANUARY,13), new Double(1.6493) },
            { createDate(1999, Calendar.JANUARY,14), new Double(1.6530) },
            { createDate(1999, Calendar.JANUARY,15), new Double(1.6500) },
            { createDate(1999, Calendar.JANUARY,19), new Double(1.6550) },
            { createDate(1999, Calendar.JANUARY,20), new Double(1.6467) },
            { createDate(1999, Calendar.JANUARY,21), new Double(1.6516) },
            { createDate(1999, Calendar.JANUARY,22), new Double(1.6560) },
            { createDate(1999, Calendar.JANUARY,25), new Double(1.6563) },
            { createDate(1999, Calendar.JANUARY,26), new Double(1.6585) },
            { createDate(1999, Calendar.JANUARY,27), new Double(1.6485) },
            { createDate(1999, Calendar.JANUARY,28), new Double(1.6470) },
            { createDate(1999, Calendar.JANUARY,29), new Double(1.6457) },
            { createDate(1999, Calendar.FEBRUARY,1), new Double(1.6410) },
            { createDate(1999, Calendar.FEBRUARY,2), new Double(1.6430) },
            { createDate(1999, Calendar.FEBRUARY,3), new Double(1.6375) },
            { createDate(1999, Calendar.FEBRUARY,4), new Double(1.6395) },
            { createDate(1999, Calendar.FEBRUARY,5), new Double(1.6370) },
            { createDate(1999, Calendar.FEBRUARY,8), new Double(1.6380) },
            { createDate(1999, Calendar.FEBRUARY,9), new Double(1.6355) },
            { createDate(1999, Calendar.FEBRUARY,10), new Double(1.6290) },
            { createDate(1999, Calendar.FEBRUARY,11), new Double(1.6244) },
            { createDate(1999, Calendar.FEBRUARY,12), new Double(1.6311) },
            { createDate(1999, Calendar.FEBRUARY,16), new Double(1.6317) },
            { createDate(1999, Calendar.FEBRUARY,17), new Double(1.6338) },
            { createDate(1999, Calendar.FEBRUARY,18), new Double(1.6338) },
            { createDate(1999, Calendar.FEBRUARY,19), new Double(1.6255) },
            { createDate(1999, Calendar.FEBRUARY,22), new Double(1.6238) },
            { createDate(1999, Calendar.FEBRUARY,23), new Double(1.6138) },
            { createDate(1999, Calendar.FEBRUARY,24), new Double(1.5980) },
            { createDate(1999, Calendar.FEBRUARY,25), new Double(1.6060) },
            { createDate(1999, Calendar.FEBRUARY,26), new Double(1.6027) },
            { createDate(1999, Calendar.MARCH,1), new Double(1.6058) },
            { createDate(1999, Calendar.MARCH,2), new Double(1.6135) },
            { createDate(1999, Calendar.MARCH,3), new Double(1.6134) },
            { createDate(1999, Calendar.MARCH,4), new Double(1.6065) },
            { createDate(1999, Calendar.MARCH,5), new Double(1.6073) },
            { createDate(1999, Calendar.MARCH,8), new Double(1.6085) },
            { createDate(1999, Calendar.MARCH,9), new Double(1.6148) },
            { createDate(1999, Calendar.MARCH,10), new Double(1.6267) },
            { createDate(1999, Calendar.MARCH,11), new Double(1.6270) },
            { createDate(1999, Calendar.MARCH,12), new Double(1.6335) },
            { createDate(1999, Calendar.MARCH,15), new Double(1.6223) },
            { createDate(1999, Calendar.MARCH,16), new Double(1.6240) },
            { createDate(1999, Calendar.MARCH,17), new Double(1.6302) },
            { createDate(1999, Calendar.MARCH,18), new Double(1.6290) },
            { createDate(1999, Calendar.MARCH,19), new Double(1.6303) },
            { createDate(1999, Calendar.MARCH,22), new Double(1.6280) },
            { createDate(1999, Calendar.MARCH,23), new Double(1.6372) },
            { createDate(1999, Calendar.MARCH,24), new Double(1.6410) },
            { createDate(1999, Calendar.MARCH,25), new Double(1.6314) },
            { createDate(1999, Calendar.MARCH,26), new Double(1.6205) },
            { createDate(1999, Calendar.MARCH,29), new Double(1.6140) },
            { createDate(1999, Calendar.MARCH,30), new Double(1.6115) },
            { createDate(1999, Calendar.MARCH,31), new Double(1.6140) },
            { createDate(1999, Calendar.APRIL,1), new Double(1.6063) },
            { createDate(1999, Calendar.APRIL,2), new Double(1.6023) },
            { createDate(1999, Calendar.APRIL,5), new Double(1.6015) },
            { createDate(1999, Calendar.APRIL,6), new Double(1.5920) },
            { createDate(1999, Calendar.APRIL,7), new Double(1.5975) },
            { createDate(1999, Calendar.APRIL,8), new Double(1.6083) },
            { createDate(1999, Calendar.APRIL,9), new Double(1.6068) },
            { createDate(1999, Calendar.APRIL,12), new Double(1.6127) },
            { createDate(1999, Calendar.APRIL,13), new Double(1.6135) },
            { createDate(1999, Calendar.APRIL,14), new Double(1.6124) },
            { createDate(1999, Calendar.APRIL,15), new Double(1.6103) },
            { createDate(1999, Calendar.APRIL,16), new Double(1.6112) },
            { createDate(1999, Calendar.APRIL,19), new Double(1.6075) },
            { createDate(1999, Calendar.APRIL,20), new Double(1.6135) },
            { createDate(1999, Calendar.APRIL,21), new Double(1.6074) },
            { createDate(1999, Calendar.APRIL,22), new Double(1.6117) },
            { createDate(1999, Calendar.APRIL,23), new Double(1.6170) },
            { createDate(1999, Calendar.APRIL,26), new Double(1.6113) },
            { createDate(1999, Calendar.APRIL,27), new Double(1.6177) },
            { createDate(1999, Calendar.APRIL,28), new Double(1.6148) },
            { createDate(1999, Calendar.APRIL,29), new Double(1.6105) },
            { createDate(1999, Calendar.APRIL,30), new Double(1.6085) },
            { createDate(1999, Calendar.MAY,3), new Double(1.6083) },
            { createDate(1999, Calendar.MAY,4), new Double(1.6210) },
            { createDate(1999, Calendar.MAY,5), new Double(1.6337) },
            { createDate(1999, Calendar.MAY,6), new Double(1.6377) },
            { createDate(1999, Calendar.MAY,7), new Double(1.6350) },
            { createDate(1999, Calendar.MAY,10), new Double(1.6300) },
            { createDate(1999, Calendar.MAY,11), new Double(1.6215) },
            { createDate(1999, Calendar.MAY,12), new Double(1.6193) },
            { createDate(1999, Calendar.MAY,13), new Double(1.6190) },
            { createDate(1999, Calendar.MAY,14), new Double(1.6175) },
            { createDate(1999, Calendar.MAY,17), new Double(1.6193) },
            { createDate(1999, Calendar.MAY,18), new Double(1.6203) },
            { createDate(1999, Calendar.MAY,19), new Double(1.6175) },
            { createDate(1999, Calendar.MAY,20), new Double(1.6122) },
            { createDate(1999, Calendar.MAY,21), new Double(1.6020) },
            { createDate(1999, Calendar.MAY,24), new Double(1.5978) },
            { createDate(1999, Calendar.MAY,25), new Double(1.6021) },
            { createDate(1999, Calendar.MAY,26), new Double(1.5963) },
            { createDate(1999, Calendar.MAY,27), new Double(1.5957) },
            { createDate(1999, Calendar.MAY,28), new Double(1.6020) },
            { createDate(1999, Calendar.JUNE,1), new Double(1.6150) },
            { createDate(1999, Calendar.JUNE,2), new Double(1.6075) },
            { createDate(1999, Calendar.JUNE,3), new Double(1.6055) },
            { createDate(1999, Calendar.JUNE,4), new Double(1.6074) },
            { createDate(1999, Calendar.JUNE,7), new Double(1.6010) },
            { createDate(1999, Calendar.JUNE,8), new Double(1.6050) },
            { createDate(1999, Calendar.JUNE,9), new Double(1.6001) },
            { createDate(1999, Calendar.JUNE,10), new Double(1.6002) },
            { createDate(1999, Calendar.JUNE,11), new Double(1.6085) },
            { createDate(1999, Calendar.JUNE,14), new Double(1.6075) },
            { createDate(1999, Calendar.JUNE,15), new Double(1.5950) },
            { createDate(1999, Calendar.JUNE,16), new Double(1.5862) },
            { createDate(1999, Calendar.JUNE,17), new Double(1.5925) },
            { createDate(1999, Calendar.JUNE,18), new Double(1.5924) },
            { createDate(1999, Calendar.JUNE,21), new Double(1.5890) },
            { createDate(1999, Calendar.JUNE,22), new Double(1.5895) },
            { createDate(1999, Calendar.JUNE,23), new Double(1.5787) },
            { createDate(1999, Calendar.JUNE,24), new Double(1.5833) },
            { createDate(1999, Calendar.JUNE,25), new Double(1.5893) },
            { createDate(1999, Calendar.JUNE,28), new Double(1.5822) },
            { createDate(1999, Calendar.JUNE,29), new Double(1.5781) },
            { createDate(1999, Calendar.JUNE,30), new Double(1.5765) },
            { createDate(1999, Calendar.JULY,1), new Double(1.5765) },
            { createDate(1999, Calendar.JULY,2), new Double(1.5792) },
            { createDate(1999, Calendar.JULY,6), new Double(1.5688) },
            { createDate(1999, Calendar.JULY,7), new Double(1.5602) },
            { createDate(1999, Calendar.JULY,8), new Double(1.5577) },
            { createDate(1999, Calendar.JULY,9), new Double(1.5515) },
            { createDate(1999, Calendar.JULY,12), new Double(1.5558) },
            { createDate(1999, Calendar.JULY,13), new Double(1.5566) },
            { createDate(1999, Calendar.JULY,14), new Double(1.5635) },
            { createDate(1999, Calendar.JULY,15), new Double(1.5665) },
            { createDate(1999, Calendar.JULY,16), new Double(1.5622) },
            { createDate(1999, Calendar.JULY,19), new Double(1.5630) },
            { createDate(1999, Calendar.JULY,20), new Double(1.5700) },
            { createDate(1999, Calendar.JULY,21), new Double(1.5766) },
            { createDate(1999, Calendar.JULY,22), new Double(1.5835) },
            { createDate(1999, Calendar.JULY,23), new Double(1.5780) },
            { createDate(1999, Calendar.JULY,26), new Double(1.5905) },
            { createDate(1999, Calendar.JULY,27), new Double(1.5895) },
            { createDate(1999, Calendar.JULY,28), new Double(1.5935) },
            { createDate(1999, Calendar.JULY,29), new Double(1.6130) },
            { createDate(1999, Calendar.JULY,30), new Double(1.6207) },
            { createDate(1999, Calendar.AUGUST,2), new Double(1.6145) },
            { createDate(1999, Calendar.AUGUST,3), new Double(1.6185) },
            { createDate(1999, Calendar.AUGUST,4), new Double(1.6200) },
            { createDate(1999, Calendar.AUGUST,5), new Double(1.6198) },
            { createDate(1999, Calendar.AUGUST,6), new Double(1.6147) },
            { createDate(1999, Calendar.AUGUST,9), new Double(1.6000) },
            { createDate(1999, Calendar.AUGUST,10), new Double(1.6180) },
            { createDate(1999, Calendar.AUGUST,11), new Double(1.6107) },
            { createDate(1999, Calendar.AUGUST,12), new Double(1.6090) },
            { createDate(1999, Calendar.AUGUST,13), new Double(1.6057) },
            { createDate(1999, Calendar.AUGUST,16), new Double(1.6011) },
            { createDate(1999, Calendar.AUGUST,17), new Double(1.6036) },
            { createDate(1999, Calendar.AUGUST,18), new Double(1.6004) },
            { createDate(1999, Calendar.AUGUST,19), new Double(1.6180) },
            { createDate(1999, Calendar.AUGUST,20), new Double(1.6133) },
            { createDate(1999, Calendar.AUGUST,23), new Double(1.6076) },
            { createDate(1999, Calendar.AUGUST,24), new Double(1.5946) },
            { createDate(1999, Calendar.AUGUST,25), new Double(1.5865) },
            { createDate(1999, Calendar.AUGUST,26), new Double(1.5876) },
            { createDate(1999, Calendar.AUGUST,27), new Double(1.5885) },
            { createDate(1999, Calendar.AUGUST,30), new Double(1.5875) },
            { createDate(1999, Calendar.AUGUST,31), new Double(1.6086) },
            { createDate(1999, Calendar.SEPTEMBER,1), new Double(1.6023) },
            { createDate(1999, Calendar.SEPTEMBER,2), new Double(1.6091) },
            { createDate(1999, Calendar.SEPTEMBER,3), new Double(1.6028) },
            { createDate(1999, Calendar.SEPTEMBER,7), new Double(1.6057) },
            { createDate(1999, Calendar.SEPTEMBER,8), new Double(1.6185) },
            { createDate(1999, Calendar.SEPTEMBER,9), new Double(1.6332) },
            { createDate(1999, Calendar.SEPTEMBER,10), new Double(1.6183) },
            { createDate(1999, Calendar.SEPTEMBER,13), new Double(1.6090) },
            { createDate(1999, Calendar.SEPTEMBER,14), new Double(1.6085) },
            { createDate(1999, Calendar.SEPTEMBER,15), new Double(1.6110) },
            { createDate(1999, Calendar.SEPTEMBER,16), new Double(1.6250) },
            { createDate(1999, Calendar.SEPTEMBER,17), new Double(1.6222) },
            { createDate(1999, Calendar.SEPTEMBER,20), new Double(1.6230) },
            { createDate(1999, Calendar.SEPTEMBER,21), new Double(1.6310) },
            { createDate(1999, Calendar.SEPTEMBER,22), new Double(1.6363) },
            { createDate(1999, Calendar.SEPTEMBER,23), new Double(1.6375) },
            { createDate(1999, Calendar.SEPTEMBER,24), new Double(1.6428) },
            { createDate(1999, Calendar.SEPTEMBER,27), new Double(1.6455) },
            { createDate(1999, Calendar.SEPTEMBER,28), new Double(1.6485) },
            { createDate(1999, Calendar.SEPTEMBER,29), new Double(1.6425) },
            { createDate(1999, Calendar.SEPTEMBER,30), new Double(1.6457) },
            { createDate(1999, Calendar.OCTOBER,1), new Double(1.6550) },
            { createDate(1999, Calendar.OCTOBER,4), new Double(1.6555) },
            { createDate(1999, Calendar.OCTOBER,5), new Double(1.6525) },
            { createDate(1999, Calendar.OCTOBER,6), new Double(1.6560) },
            { createDate(1999, Calendar.OCTOBER,7), new Double(1.6520) },
            { createDate(1999, Calendar.OCTOBER,8), new Double(1.6520) },
            { createDate(1999, Calendar.OCTOBER,12), new Double(1.6535) },
            { createDate(1999, Calendar.OCTOBER,13), new Double(1.6532) },
            { createDate(1999, Calendar.OCTOBER,14), new Double(1.6575) },
            { createDate(1999, Calendar.OCTOBER,15), new Double(1.6684) },
            { createDate(1999, Calendar.OCTOBER,18), new Double(1.6720) },
            { createDate(1999, Calendar.OCTOBER,19), new Double(1.6708) },
            { createDate(1999, Calendar.OCTOBER,20), new Double(1.6643) },
            { createDate(1999, Calendar.OCTOBER,21), new Double(1.6765) },
            { createDate(1999, Calendar.OCTOBER,22), new Double(1.6584) },
            { createDate(1999, Calendar.OCTOBER,25), new Double(1.6631) },
            { createDate(1999, Calendar.OCTOBER,26), new Double(1.6525) },
            { createDate(1999, Calendar.OCTOBER,27), new Double(1.6477) },
            { createDate(1999, Calendar.OCTOBER,28), new Double(1.6400) },
            { createDate(1999, Calendar.OCTOBER,29), new Double(1.6425) },
            { createDate(1999, Calendar.NOVEMBER,1), new Double(1.6404) },
            { createDate(1999, Calendar.NOVEMBER,2), new Double(1.6445) },
            { createDate(1999, Calendar.NOVEMBER,3), new Double(1.6440) },
            { createDate(1999, Calendar.NOVEMBER,4), new Double(1.6375) },
            { createDate(1999, Calendar.NOVEMBER,5), new Double(1.6205) },
            { createDate(1999, Calendar.NOVEMBER,8), new Double(1.6150) },
            { createDate(1999, Calendar.NOVEMBER,9), new Double(1.6210) },
            { createDate(1999, Calendar.NOVEMBER,10), new Double(1.6250) },
            { createDate(1999, Calendar.NOVEMBER,12), new Double(1.6131) },
            { createDate(1999, Calendar.NOVEMBER,15), new Double(1.6230) },
            { createDate(1999, Calendar.NOVEMBER,16), new Double(1.6164) },
            { createDate(1999, Calendar.NOVEMBER,17), new Double(1.6231) },
            { createDate(1999, Calendar.NOVEMBER,18), new Double(1.6150) },
            { createDate(1999, Calendar.NOVEMBER,19), new Double(1.6160) },
            { createDate(1999, Calendar.NOVEMBER,22), new Double(1.6205) },
            { createDate(1999, Calendar.NOVEMBER,23), new Double(1.6236) },
            { createDate(1999, Calendar.NOVEMBER,24), new Double(1.6090) },
            { createDate(1999, Calendar.NOVEMBER,26), new Double(1.6062) },
            { createDate(1999, Calendar.NOVEMBER,29), new Double(1.6024) },
            { createDate(1999, Calendar.NOVEMBER,30), new Double(1.5935) },
            { createDate(1999, Calendar.DECEMBER,1), new Double(1.5960) },
            { createDate(1999, Calendar.DECEMBER,2), new Double(1.5972) },
            { createDate(1999, Calendar.DECEMBER,3), new Double(1.6015) },
            { createDate(1999, Calendar.DECEMBER,6), new Double(1.6230) },
            { createDate(1999, Calendar.DECEMBER,7), new Double(1.6233) },
            { createDate(1999, Calendar.DECEMBER,8), new Double(1.6255) },
            { createDate(1999, Calendar.DECEMBER,9), new Double(1.6230) },
            { createDate(1999, Calendar.DECEMBER,10), new Double(1.6225) },
            { createDate(1999, Calendar.DECEMBER,13), new Double(1.6232) },
            { createDate(1999, Calendar.DECEMBER,14), new Double(1.6110) },
            { createDate(1999, Calendar.DECEMBER,15), new Double(1.6085) },
            { createDate(1999, Calendar.DECEMBER,16), new Double(1.6123) },
            { createDate(1999, Calendar.DECEMBER,17), new Double(1.6070) },
            { createDate(1999, Calendar.DECEMBER,20), new Double(1.6035) },
            { createDate(1999, Calendar.DECEMBER,21), new Double(1.6103) },
            { createDate(1999, Calendar.DECEMBER,22), new Double(1.6060) },
            { createDate(1999, Calendar.DECEMBER,23), new Double(1.6161) },
            { createDate(1999, Calendar.DECEMBER,24), new Double(1.6154) },
            { createDate(1999, Calendar.DECEMBER,27), new Double(1.6173) },
            { createDate(1999, Calendar.DECEMBER,28), new Double(1.6165) },
            { createDate(1999, Calendar.DECEMBER,29), new Double(1.6168) },
            { createDate(1999, Calendar.DECEMBER,30), new Double(1.6130) },
            { createDate(1999, Calendar.DECEMBER,31), new Double(1.6150) },
            { createDate(2000, Calendar.JANUARY,3), new Double(1.6270) },
            { createDate(2000, Calendar.JANUARY,4), new Double(1.6370) },
            { createDate(2000, Calendar.JANUARY,5), new Double(1.6415) },
            { createDate(2000, Calendar.JANUARY,6), new Double(1.6475) },
            { createDate(2000, Calendar.JANUARY,7), new Double(1.6384) },
            { createDate(2000, Calendar.JANUARY,10), new Double(1.6374) },
            { createDate(2000, Calendar.JANUARY,11), new Double(1.6480) },
            { createDate(2000, Calendar.JANUARY,12), new Double(1.6465) },
            { createDate(2000, Calendar.JANUARY,13), new Double(1.6482) },
            { createDate(2000, Calendar.JANUARY,14), new Double(1.6353) },
            { createDate(2000, Calendar.JANUARY,18), new Double(1.6380) },
            { createDate(2000, Calendar.JANUARY,19), new Double(1.6438) },
            { createDate(2000, Calendar.JANUARY,20), new Double(1.6538) },
            { createDate(2000, Calendar.JANUARY,21), new Double(1.6504) },
            { createDate(2000, Calendar.JANUARY,24), new Double(1.6520) },
            { createDate(2000, Calendar.JANUARY,25), new Double(1.6482) },
            { createDate(2000, Calendar.JANUARY,26), new Double(1.6395) },
            { createDate(2000, Calendar.JANUARY,27), new Double(1.6363) },
            { createDate(2000, Calendar.JANUARY,28), new Double(1.6210) },
            { createDate(2000, Calendar.JANUARY,31), new Double(1.6182) },
            { createDate(2000, Calendar.FEBRUARY,1), new Double(1.6150) },
            { createDate(2000, Calendar.FEBRUARY,2), new Double(1.6060) },
            { createDate(2000, Calendar.FEBRUARY,3), new Double(1.6025) },
            { createDate(2000, Calendar.FEBRUARY,4), new Double(1.5915) },
            { createDate(2000, Calendar.FEBRUARY,7), new Double(1.5917) },
            { createDate(2000, Calendar.FEBRUARY,8), new Double(1.6105) },
            { createDate(2000, Calendar.FEBRUARY,9), new Double(1.6115) },
            { createDate(2000, Calendar.FEBRUARY,10), new Double(1.6057) },
            { createDate(2000, Calendar.FEBRUARY,11), new Double(1.5923) },
            { createDate(2000, Calendar.FEBRUARY,14), new Double(1.5890) },
            { createDate(2000, Calendar.FEBRUARY,15), new Double(1.5950) },
            { createDate(2000, Calendar.FEBRUARY,16), new Double(1.6040) },
            { createDate(2000, Calendar.FEBRUARY,17), new Double(1.6050) },
            { createDate(2000, Calendar.FEBRUARY,18), new Double(1.5984) },
            { createDate(2000, Calendar.FEBRUARY,22), new Double(1.6165) },
            { createDate(2000, Calendar.FEBRUARY,23), new Double(1.6047) },
            { createDate(2000, Calendar.FEBRUARY,24), new Double(1.5981) },
            { createDate(2000, Calendar.FEBRUARY,25), new Double(1.5908) },
            { createDate(2000, Calendar.FEBRUARY,28), new Double(1.5935) },
            { createDate(2000, Calendar.FEBRUARY,29), new Double(1.5780) },
            { createDate(2000, Calendar.MARCH,1), new Double(1.5849) },
            { createDate(2000, Calendar.MARCH,2), new Double(1.5765) },
            { createDate(2000, Calendar.MARCH,3), new Double(1.5810) },
            { createDate(2000, Calendar.MARCH,6), new Double(1.5732) },
            { createDate(2000, Calendar.MARCH,7), new Double(1.5772) },
            { createDate(2000, Calendar.MARCH,8), new Double(1.5855) },
            { createDate(2000, Calendar.MARCH,9), new Double(1.5815) },
            { createDate(2000, Calendar.MARCH,10), new Double(1.5793) },
            { createDate(2000, Calendar.MARCH,13), new Double(1.5782) },
            { createDate(2000, Calendar.MARCH,14), new Double(1.5725) },
            { createDate(2000, Calendar.MARCH,15), new Double(1.5730) },
            { createDate(2000, Calendar.MARCH,16), new Double(1.5747) },
            { createDate(2000, Calendar.MARCH,17), new Double(1.5705) } } };

        return new DefaultXYDataset(new String[] { "USD" }, data);
    }

    /**
     * Creates and returns a XYDataset for the demo charts.
     */
    public XYDataset createTestXYDataset() {

        Object[][][] data = new Object[][][] { {
            { createDateTime(2000, Calendar.OCTOBER, 18, 9, 5), new Double(10921.0) },
            { createDateTime(2000, Calendar.OCTOBER, 18, 10, 6), new Double(10886.7) },
            { createDateTime(2000, Calendar.OCTOBER, 18, 11, 6), new Double(10846.6) },
            { createDateTime(2000, Calendar.OCTOBER, 18, 12, 6), new Double(10843.7) },
            { createDateTime(2000, Calendar.OCTOBER, 18, 13, 6), new Double(10841.2) },
            { createDateTime(2000, Calendar.OCTOBER, 18, 14, 6), new Double(10830.7) },
            { createDateTime(2000, Calendar.OCTOBER, 18, 15, 6), new Double(10795.8) },
            { createDateTime(2000, Calendar.OCTOBER, 18, 16, 7), new Double(10733.8) }
        } };

        return new DefaultXYDataset(data);
    }

    public TimeSeriesCollection createTestTimeSeriesCollection1() {

        TimeSeries t1 = new TimeSeries("Annual");
        try {
            t1.add(new Year(1990), new Double(50.1));
            t1.add(new Year(1991), new Double(12.3));
            t1.add(new Year(1992), new Double(23.9));
            t1.add(new Year(1993), new Double(83.4));
            t1.add(new Year(1994), new Double(-34.7));
            t1.add(new Year(1995), new Double(76.5));
            t1.add(new Year(1996), new Double(10.0));
            t1.add(new Year(1997), new Double(-14.7));
            t1.add(new Year(1998), new Double(43.9));
            t1.add(new Year(1999), new Double(49.6));
            t1.add(new Year(2000), new Double(37.2));
            t1.add(new Year(2001), new Double(17.1));
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
        }

        return new TimeSeriesCollection(t1);

    }


    /**
     * Creates and returns a sample high-low dataset for the demo.  Added by Andrzej Porebski.
     */
    public XYDataset createHighLowDataset() {

        Object[][][] data = new Object[][][] { {
            { createDate(1999, Calendar.JANUARY,4), new Double(47) },
            { createDate(1999, Calendar.JANUARY,4), new Double(33) },
            { createDate(1999, Calendar.JANUARY,4), new Double(35) },
            { createDate(1999, Calendar.JANUARY,4), new Double(33) },

            { createDate(1999, Calendar.JANUARY,5), new Double(47) },
            { createDate(1999, Calendar.JANUARY,5), new Double(32) },
            { createDate(1999, Calendar.JANUARY,5), new Double(41) },
            { createDate(1999, Calendar.JANUARY,5), new Double(37) },

            { createDate(1999, Calendar.JANUARY,6), new Double(49) },
            { createDate(1999, Calendar.JANUARY,6), new Double(43) },
            { createDate(1999, Calendar.JANUARY,6), new Double(46) },
            { createDate(1999, Calendar.JANUARY,6), new Double(48) },

            { createDate(1999, Calendar.JANUARY,7), new Double(51) },
            { createDate(1999, Calendar.JANUARY,7), new Double(39) },
            { createDate(1999, Calendar.JANUARY,7), new Double(40) },
            { createDate(1999, Calendar.JANUARY,7), new Double(47) },

            { createDate(1999, Calendar.JANUARY,8), new Double(60) },
            { createDate(1999, Calendar.JANUARY,8), new Double(40) },
            { createDate(1999, Calendar.JANUARY,8), new Double(46) },
            { createDate(1999, Calendar.JANUARY,8), new Double(53) },

            { createDate(1999, Calendar.JANUARY,9), new Double(62) },
            { createDate(1999, Calendar.JANUARY,9), new Double(55) },
            { createDate(1999, Calendar.JANUARY,9), new Double(57) },
            { createDate(1999, Calendar.JANUARY,9), new Double(61) },

            { createDate(1999, Calendar.JANUARY,10), new Double(65) },
            { createDate(1999, Calendar.JANUARY,10), new Double(56) },
            { createDate(1999, Calendar.JANUARY,10), new Double(62) },
            { createDate(1999, Calendar.JANUARY,10), new Double(59) },

            { createDate(1999, Calendar.JANUARY,11), new Double(55) },
            { createDate(1999, Calendar.JANUARY,11), new Double(43) },
            { createDate(1999, Calendar.JANUARY,11), new Double(45) },
            { createDate(1999, Calendar.JANUARY,11), new Double(47) },

            { createDate(1999, Calendar.JANUARY,12), new Double(54) },
            { createDate(1999, Calendar.JANUARY,12), new Double(33) },
            { createDate(1999, Calendar.JANUARY,12), new Double(40) },
            { createDate(1999, Calendar.JANUARY,12), new Double(51) },

            { createDate(1999, Calendar.JANUARY,13), new Double(58) },
            { createDate(1999, Calendar.JANUARY,13), new Double(42) },
            { createDate(1999, Calendar.JANUARY,13), new Double(44) },
            { createDate(1999, Calendar.JANUARY,13), new Double(57) },

            { createDate(1999, Calendar.JANUARY,14), new Double(54) },
            { createDate(1999, Calendar.JANUARY,14), new Double(38) },
            { createDate(1999, Calendar.JANUARY,14), new Double(43) },
            { createDate(1999, Calendar.JANUARY,14), new Double(52) },

            { createDate(1999, Calendar.JANUARY,15), new Double(48) },
            { createDate(1999, Calendar.JANUARY,15), new Double(41) },
            { createDate(1999, Calendar.JANUARY,15), new Double(44) },
            { createDate(1999, Calendar.JANUARY,15), new Double(41) },

            { createDate(1999, Calendar.JANUARY,17), new Double(60) },
            { createDate(1999, Calendar.JANUARY,17), new Double(30) },
            { createDate(1999, Calendar.JANUARY,17), new Double(34) },
            { createDate(1999, Calendar.JANUARY,17), new Double(44) },

            { createDate(1999, Calendar.JANUARY,18), new Double(58) },
            { createDate(1999, Calendar.JANUARY,18), new Double(44) },
            { createDate(1999, Calendar.JANUARY,18), new Double(54) },
            { createDate(1999, Calendar.JANUARY,18), new Double(56) },

            { createDate(1999, Calendar.JANUARY,19), new Double(54) },
            { createDate(1999, Calendar.JANUARY,19), new Double(32) },
            { createDate(1999, Calendar.JANUARY,19), new Double(42) },
            { createDate(1999, Calendar.JANUARY,19), new Double(53) },

            { createDate(1999, Calendar.JANUARY,20), new Double(53) },
            { createDate(1999, Calendar.JANUARY,20), new Double(39) },
            { createDate(1999, Calendar.JANUARY,20), new Double(50) },
            { createDate(1999, Calendar.JANUARY,20), new Double(49) },

            { createDate(1999, Calendar.JANUARY,21), new Double(47) },
            { createDate(1999, Calendar.JANUARY,21), new Double(38) },
            { createDate(1999, Calendar.JANUARY,21), new Double(41) },
            { createDate(1999, Calendar.JANUARY,21), new Double(40) },

            { createDate(1999, Calendar.JANUARY,22), new Double(55) },
            { createDate(1999, Calendar.JANUARY,22), new Double(37) },
            { createDate(1999, Calendar.JANUARY,22), new Double(43) },
            { createDate(1999, Calendar.JANUARY,22), new Double(45) },

            { createDate(1999, Calendar.JANUARY,23), new Double(54) },
            { createDate(1999, Calendar.JANUARY,23), new Double(42) },
            { createDate(1999, Calendar.JANUARY,23), new Double(50) },
            { createDate(1999, Calendar.JANUARY,23), new Double(42) },

            { createDate(1999, Calendar.JANUARY,24), new Double(48) },
            { createDate(1999, Calendar.JANUARY,24), new Double(37) },
            { createDate(1999, Calendar.JANUARY,24), new Double(37) },
            { createDate(1999, Calendar.JANUARY,24), new Double(47) },

            { createDate(1999, Calendar.JANUARY,25), new Double(58) },
            { createDate(1999, Calendar.JANUARY,25), new Double(33) },
            { createDate(1999, Calendar.JANUARY,25), new Double(39) },
            { createDate(1999, Calendar.JANUARY,25), new Double(41) },

            { createDate(1999, Calendar.JANUARY,26), new Double(47) },
            { createDate(1999, Calendar.JANUARY,26), new Double(31) },
            { createDate(1999, Calendar.JANUARY,26), new Double(36) },
            { createDate(1999, Calendar.JANUARY,26), new Double(41) },

            { createDate(1999, Calendar.JANUARY,27), new Double(58) },
            { createDate(1999, Calendar.JANUARY,27), new Double(44) },
            { createDate(1999, Calendar.JANUARY,27), new Double(49) },
            { createDate(1999, Calendar.JANUARY,27), new Double(44) },

            { createDate(1999, Calendar.JANUARY,28), new Double(46) },
            { createDate(1999, Calendar.JANUARY,28), new Double(41) },
            { createDate(1999, Calendar.JANUARY,28), new Double(43) },
            { createDate(1999, Calendar.JANUARY,28), new Double(44) },

            { createDate(1999, Calendar.JANUARY,29), new Double(56) },
            { createDate(1999, Calendar.JANUARY,29), new Double(39) },
            { createDate(1999, Calendar.JANUARY,29), new Double(39) },
            { createDate(1999, Calendar.JANUARY,29), new Double(51) },

            { createDate(1999, Calendar.JANUARY,30), new Double(56) },
            { createDate(1999, Calendar.JANUARY,30), new Double(39) },
            { createDate(1999, Calendar.JANUARY,30), new Double(47) },
            { createDate(1999, Calendar.JANUARY,30), new Double(49) },

            { createDate(1999, Calendar.JANUARY,31), new Double(53) },
            { createDate(1999, Calendar.JANUARY,31), new Double(39) },
            { createDate(1999, Calendar.JANUARY,31), new Double(52) },
            { createDate(1999, Calendar.JANUARY,31), new Double(47) },

            { createDate(1999, Calendar.FEBRUARY,1), new Double(51) },
            { createDate(1999, Calendar.FEBRUARY,1), new Double(30) },
            { createDate(1999, Calendar.FEBRUARY,1), new Double(45) },
            { createDate(1999, Calendar.FEBRUARY,1), new Double(47) },

            { createDate(1999, Calendar.FEBRUARY,2), new Double(47) },
            { createDate(1999, Calendar.FEBRUARY,2), new Double(30) },
            { createDate(1999, Calendar.FEBRUARY,2), new Double(34) },
            { createDate(1999, Calendar.FEBRUARY,2), new Double(46) },

            { createDate(1999, Calendar.FEBRUARY,3), new Double(57) },
            { createDate(1999, Calendar.FEBRUARY,3), new Double(37) },
            { createDate(1999, Calendar.FEBRUARY,3), new Double(44) },
            { createDate(1999, Calendar.FEBRUARY,3), new Double(56) },

            { createDate(1999, Calendar.FEBRUARY,4), new Double(49) },
            { createDate(1999, Calendar.FEBRUARY,4), new Double(40) },
            { createDate(1999, Calendar.FEBRUARY,4), new Double(47) },
            { createDate(1999, Calendar.FEBRUARY,4), new Double(44) },

            { createDate(1999, Calendar.FEBRUARY,5), new Double(46) },
            { createDate(1999, Calendar.FEBRUARY,5), new Double(38) },
            { createDate(1999, Calendar.FEBRUARY,5), new Double(43) },
            { createDate(1999, Calendar.FEBRUARY,5), new Double(40) },

            { createDate(1999, Calendar.FEBRUARY,6), new Double(55) },
            { createDate(1999, Calendar.FEBRUARY,6), new Double(38) },
            { createDate(1999, Calendar.FEBRUARY,6), new Double(39) },
            { createDate(1999, Calendar.FEBRUARY,6), new Double(53) },

            { createDate(1999, Calendar.FEBRUARY,7), new Double(50) },
            { createDate(1999, Calendar.FEBRUARY,7), new Double(33) },
            { createDate(1999, Calendar.FEBRUARY,7), new Double(37) },
            { createDate(1999, Calendar.FEBRUARY,7), new Double(37) },

            { createDate(1999, Calendar.FEBRUARY,8), new Double(59) },
            { createDate(1999, Calendar.FEBRUARY,8), new Double(34) },
            { createDate(1999, Calendar.FEBRUARY,8), new Double(57) },
            { createDate(1999, Calendar.FEBRUARY,8), new Double(43) },

            { createDate(1999, Calendar.FEBRUARY,9), new Double(48) },
            { createDate(1999, Calendar.FEBRUARY,9), new Double(39) },
            { createDate(1999, Calendar.FEBRUARY,9), new Double(46) },
            { createDate(1999, Calendar.FEBRUARY,9), new Double(47) },

            { createDate(1999, Calendar.FEBRUARY,10), new Double(55) },
            { createDate(1999, Calendar.FEBRUARY,10), new Double(30) },
            { createDate(1999, Calendar.FEBRUARY,10), new Double(37) },
            { createDate(1999, Calendar.FEBRUARY,10), new Double(30) },

            { createDate(1999, Calendar.FEBRUARY,11), new Double(60) },
            { createDate(1999, Calendar.FEBRUARY,11), new Double(32) },
            { createDate(1999, Calendar.FEBRUARY,11), new Double(56) },
            { createDate(1999, Calendar.FEBRUARY,11), new Double(36) },

            { createDate(1999, Calendar.FEBRUARY,12), new Double(56) },
            { createDate(1999, Calendar.FEBRUARY,12), new Double(42) },
            { createDate(1999, Calendar.FEBRUARY,12), new Double(53) },
            { createDate(1999, Calendar.FEBRUARY,12), new Double(54) },

            { createDate(1999, Calendar.FEBRUARY,13), new Double(49) },
            { createDate(1999, Calendar.FEBRUARY,13), new Double(42) },
            { createDate(1999, Calendar.FEBRUARY,13), new Double(45) },
            { createDate(1999, Calendar.FEBRUARY,13), new Double(42) },

            { createDate(1999, Calendar.FEBRUARY,14), new Double(55) },
            { createDate(1999, Calendar.FEBRUARY,14), new Double(42) },
            { createDate(1999, Calendar.FEBRUARY,14), new Double(47) },
            { createDate(1999, Calendar.FEBRUARY,14), new Double(54) },

            { createDate(1999, Calendar.FEBRUARY,15), new Double(49) },
            { createDate(1999, Calendar.FEBRUARY,15), new Double(35) },
            { createDate(1999, Calendar.FEBRUARY,15), new Double(38) },
            { createDate(1999, Calendar.FEBRUARY,15), new Double(35) },

            { createDate(1999, Calendar.FEBRUARY,16), new Double(47) },
            { createDate(1999, Calendar.FEBRUARY,16), new Double(38) },
            { createDate(1999, Calendar.FEBRUARY,16), new Double(43) },
            { createDate(1999, Calendar.FEBRUARY,16), new Double(42) },

            { createDate(1999, Calendar.FEBRUARY,17), new Double(53) },
            { createDate(1999, Calendar.FEBRUARY,17), new Double(42) },
            { createDate(1999, Calendar.FEBRUARY,17), new Double(47) },
            { createDate(1999, Calendar.FEBRUARY,17), new Double(48) },

            { createDate(1999, Calendar.FEBRUARY,18), new Double(47) },
            { createDate(1999, Calendar.FEBRUARY,18), new Double(44) },
            { createDate(1999, Calendar.FEBRUARY,18), new Double(46) },
            { createDate(1999, Calendar.FEBRUARY,18), new Double(44) },

            { createDate(1999, Calendar.FEBRUARY,19), new Double(46) },
            { createDate(1999, Calendar.FEBRUARY,19), new Double(40) },
            { createDate(1999, Calendar.FEBRUARY,19), new Double(43) },
            { createDate(1999, Calendar.FEBRUARY,19), new Double(44) },

            { createDate(1999, Calendar.FEBRUARY,20), new Double(48) },
            { createDate(1999, Calendar.FEBRUARY,20), new Double(41) },
            { createDate(1999, Calendar.FEBRUARY,20), new Double(46) },
            { createDate(1999, Calendar.FEBRUARY,20), new Double(41) } }
        };

        return new DefaultXYDataset(new String[] { "IBM" }, data);

    }

    /**
     * Required for WindowListener interface, but not used by this class.
     */
    public void windowActivated(WindowEvent e) {}

    /**
     * Clears the reference to the print preview frames when they are closed.
     */
    public void windowClosed(WindowEvent e) {

        //if (e.getWindow()==this.infoFrame) {
        //    infoFrame=null;
        //}
        //else
        if (e.getWindow()==this.aboutFrame) {
            aboutFrame=null;
        }

    }

    /**
     * Required for WindowListener interface, but not used by this class.
     */
    public void windowClosing(WindowEvent e) { }

    /**
     * Required for WindowListener interface, but not used by this class.
     */
    public void windowDeactivated(WindowEvent e) {}

    /**
     * Required for WindowListener interface, but not used by this class.
     */
    public void windowDeiconified(WindowEvent e) {}

    /**
     * Required for WindowListener interface, but not used by this class.
     */
    public void windowIconified(WindowEvent e) {}

    /**
     * Required for WindowListener interface, but not used by this class.
     */
    public void windowOpened(WindowEvent e) {}

}
