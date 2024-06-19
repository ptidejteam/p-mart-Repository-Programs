/* ===============
 * JFreeChart Demo
 * ===============
 *
 * Project Info:  http://www.jrefinery.com/jfreechart;
 * Project Lead:  David Gilbert (david.gilbert@jrefinery.com);
 *
 * (C) Copyright 2000, 2001 by Simba Management Limited and Contributors;
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
 * -------------------
 * JFreeChartDemo.java
 * -------------------
 * (C) Copyright 2000, 2001 by Simba Management Limited and Contributors;
 *
 * Original Author:  David Gilbert (for Simba Management Limited);
 * Contributor(s):   Andrzej Porebski;
 *                   Matthew Wright;
 *                   Serge V. Grachov;
 *                   Bill Kelemen;
 *
 * $Id: JFreeChartDemo.java,v 1.1 2007/10/10 18:54:39 vauchers Exp $
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
 * 05-Dec-2001 : Added combined charts contributed by Bill Kelemen (DG);
 * 10-Dec-2001 : Updated exchange rate demo data, and included a demo chart that shows multiple
 *               time series together on one chart.  Removed some redundant code (DG);
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
    public static final Dimension PREFERRED_SIZE = new Dimension(780, 400);

    /** A list of contributors. */
    protected java.util.List contributors;

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
    private JFreeChartFrame timeSeries1ChartFrame;

    /** A frame for displaying a time series chart. */
    private JFreeChartFrame timeSeries2ChartFrame;

    /** A frame for displaying a time series chart with a moving average. */
    private JFreeChartFrame timeSeriesWithMAChartFrame;

    /** A frame for displaying a chart in a scroll pane. */
    private JFreeChartFrame timeSeriesChartScrollFrame;

    /** A frame for displaying a high/low/open/close chart. */
    private JFreeChartFrame highLowChartFrame;

    /** A frame for displaying a dynamic XY plot chart. */
    private JFreeChartFrame dynamicXYChartFrame;

    /** A frame for displaying a horizontally Combined plot chart. */
    private JFreeChartFrame horizontallyCombinedChartFrame;

    /** A frame for displaying a vertically Combined plot chart. */
    private JFreeChartFrame verticallyCombinedChartFrame;

    /** A frame for displaying a Combined plot chart. */
    private JFreeChartFrame combinedOverlaidChartFrame1;

    /** A frame for displaying a Combined plot chart. */
    private JFreeChartFrame overlaidChartFrame;

    /** A frame for displaying a Combined and Overlaid Dynamic chart. */
    private JFreeChartFrame combinedAndOverlaidDynamicXYChartFrame;

    /** A frame for displaying information about the application. */
    private AboutFrame aboutFrame;

    /** A tabbed pane for displaying sample charts; */
    private JTabbedPane tabbedPane;

    /**
     * Constructs a demonstration application for the JFreeChart Class Library.
     */
    public JFreeChartDemo() {

        super("JFreeChart "+JFreeChart.VERSION+" Demo");

        // The list of contributors, in no particular order, is displayed in the about frame...
        contributors = new ArrayList();
        contributors.add(new Contributor("David Gilbert", "david.gilbert@jrefinery.com"));
        contributors.add(new Contributor("Andrzej Porebski", "-"));
        contributors.add(new Contributor("Bill Kelemen", "-"));
        contributors.add(new Contributor("David Berry", "-"));
        contributors.add(new Contributor("Matthew Wright", "-"));
        contributors.add(new Contributor("David Li", "-"));
        contributors.add(new Contributor("Sylvain Vieujot", "-"));
        contributors.add(new Contributor("Serge V. Grachov", "-"));
        contributors.add(new Contributor("Joao Guilherme Del Valle", "-"));
        contributors.add(new Contributor("Mark Watson", "www.markwatson.com"));
        contributors.add(new Contributor("Søren Caspersen", "-"));
        contributors.add(new Contributor("Laurence Vanhelsuwe", "-"));
        contributors.add(new Contributor("Martin Cordova", "-"));
        contributors.add(new Contributor("Wolfgang Irler", "-"));
        contributors.add(new Contributor("Craig MacFarlane", "-"));

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

        JPanel combinedPanel = new JPanel(new LCBLayout(20));
        combinedPanel.setPreferredSize(new Dimension(360, 20));
        combinedPanel.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));

        barPanel.add(JRefineryUtilities.createJLabel("Horizontal Bar Chart: ", font));
        barPanel.add(new DescriptionPanel(new JTextArea("Displays horizontal bars, representing data from a CategoryDataset.")));
        JButton b1 = JRefineryUtilities.createJButton("Display", font);
        b1.setActionCommand("HORIZONTAL_BAR_CHART");
        b1.addActionListener(this);
        barPanel.add(b1);


        barPanel.add(JRefineryUtilities.createJLabel("Horizontal Stacked Bar Chart: ", font));
        barPanel.add(new DescriptionPanel(new JTextArea("Displays stacked horizontal bars, representing data from a CategoryDataset.")));
        JButton b2 = JRefineryUtilities.createJButton("Display", font);
        b2.setActionCommand("HORIZONTAL_STACKED_BAR_CHART");
        b2.addActionListener(this);
        barPanel.add(b2);

        barPanel.add(JRefineryUtilities.createJLabel("Vertical Bar Chart: ", font));
        barPanel.add(new DescriptionPanel(new JTextArea("Displays vertical bars, representing data from a CategoryDataset.")));
        JButton b3 = JRefineryUtilities.createJButton("Display", font);
        b3.setActionCommand("VERTICAL_BAR_CHART");
        b3.addActionListener(this);
        barPanel.add(b3);

        barPanel.add(JRefineryUtilities.createJLabel("Vertical 3D Bar Chart: ", font));
        barPanel.add(new DescriptionPanel(new JTextArea("Displays stacked vertical bars with a 3D effect, representing data from a CategoryDataset.")));
        JButton b6 = JRefineryUtilities.createJButton("Display", font);
        b6.setActionCommand("VERTICAL_3D_BAR_CHART");
        b6.addActionListener(this);
        barPanel.add(b6);

        barPanel.add(JRefineryUtilities.createJLabel("Vertical Stacked Bar Chart: ", font));
        barPanel.add(new DescriptionPanel(new JTextArea("Displays stacked vertical bars, representing data from a CategoryDataset.")));
        JButton b4 = JRefineryUtilities.createJButton("Display", font);
        b4.setActionCommand("VERTICAL_STACKED_BAR_CHART");
        b4.addActionListener(this);
        barPanel.add(b4);

        barPanel.add(JRefineryUtilities.createJLabel("Vertical Stacked 3D Bar Chart: ", font));
        barPanel.add(new DescriptionPanel(new JTextArea("Displays stacked vertical bars with a 3D effect, representing data from a CategoryDataset.")));
        JButton b5 = JRefineryUtilities.createJButton("Display", font);
        b5.setActionCommand("VERTICAL_STACKED_3D_BAR_CHART");
        b5.addActionListener(this);
        barPanel.add(b5);

        otherPanel.add(JRefineryUtilities.createJLabel("Line Chart: ", font));
        otherPanel.add(new DescriptionPanel(new JTextArea("A chart displaying lines and or shapes, representing data in a CategoryDataset.")));
        JButton b8 = JRefineryUtilities.createJButton("Display", font);
        b8.setActionCommand("LINE_CHART");
        b8.addActionListener(this);
        otherPanel.add(b8);

        piePanel.add(JRefineryUtilities.createJLabel("Pie Chart: ", font));
        piePanel.add(new DescriptionPanel(new JTextArea("A pie chart showing one section exploded.")));
        JButton b9 = JRefineryUtilities.createJButton("Display", font);
        b9.setActionCommand("PIE_CHART_1");
        b9.addActionListener(this);
        piePanel.add(b9);

        piePanel.add(JRefineryUtilities.createJLabel("Pie Chart 2: ", font));
        piePanel.add(new DescriptionPanel(new JTextArea("A pie chart showing percentage labels.")));
        JButton b10 = JRefineryUtilities.createJButton("Display", font);
        b10.setActionCommand("PIE_CHART_2");
        b10.addActionListener(this);
        piePanel.add(b10);

        otherPanel.add(JRefineryUtilities.createJLabel("Scatter Plot: ", font));
        otherPanel.add(new DescriptionPanel(new JTextArea("A scatter plot, based on data from an XYDataset.")));
        JButton b11 = JRefineryUtilities.createJButton("Display", font);
        b11.setActionCommand("SCATTER_PLOT");
        b11.addActionListener(this);
        otherPanel.add(b11);

        xyPanel.add(JRefineryUtilities.createJLabel("XY Plot: ", font));
        xyPanel.add(new DescriptionPanel(new JTextArea("A line chart, based on data from an XYDataset.")));
        JButton b12 = JRefineryUtilities.createJButton("Display", font);
        b12.setActionCommand("XY_PLOT");
        b12.addActionListener(this);
        xyPanel.add(b12);

        testPanel.add(JRefineryUtilities.createJLabel("Null Data: ", font));
        testPanel.add(new DescriptionPanel(new JTextArea("A chart with a null dataset.")));
        JButton b13 = JRefineryUtilities.createJButton("Display", font);
        b13.setActionCommand("XY_PLOT_NULL");
        b13.addActionListener(this);
        testPanel.add(b13);

        testPanel.add(JRefineryUtilities.createJLabel("Zero Data: ", font));
        testPanel.add(new DescriptionPanel(new JTextArea("A chart with a dataset containing zero series..")));
        JButton b14 = JRefineryUtilities.createJButton("Display", font);
        b14.setActionCommand("XY_PLOT_ZERO");
        b14.addActionListener(this);
        testPanel.add(b14);

        timeSeriesPanel.add(JRefineryUtilities.createJLabel("Time Series 1: ", font));
        timeSeriesPanel.add(new DescriptionPanel(new JTextArea("A time series chart, based on data from an XYDataset.")));
        JButton b15 = JRefineryUtilities.createJButton("Display", font);
        b15.setActionCommand("TIME_SERIES_1_CHART");
        b15.addActionListener(this);
        timeSeriesPanel.add(b15);

        timeSeriesPanel.add(JRefineryUtilities.createJLabel("Time Series 2: ", font));
        timeSeriesPanel.add(new DescriptionPanel(new JTextArea("A time series chart, based on data from an XYDataset.")));
        JButton b17 = JRefineryUtilities.createJButton("Display", font);
        b17.setActionCommand("TIME_SERIES_2_CHART");
        b17.addActionListener(this);
        timeSeriesPanel.add(b17);

        timeSeriesPanel.add(JRefineryUtilities.createJLabel("Time Series 3: ", font));
        timeSeriesPanel.add(new DescriptionPanel(new JTextArea("A time series chart with a moving average, based on data from an XYDataset.")));
        JButton b16 = JRefineryUtilities.createJButton("Display", font);
        b16.setActionCommand("TIME_SERIES_WITH_MA_CHART");
        b16.addActionListener(this);
        timeSeriesPanel.add(b16);

        testPanel.add(JRefineryUtilities.createJLabel("Chart in JScrollPane: ", font));
        testPanel.add(new DescriptionPanel(new JTextArea("A chart embedded in a JScrollPane.")));
        JButton b18 = JRefineryUtilities.createJButton("Display", font);
        b18.setActionCommand("TIME_SERIES_CHART_SCROLL");
        b18.addActionListener(this);
        testPanel.add(b18);

        timeSeriesPanel.add(JRefineryUtilities.createJLabel("High/Low/Open/Close Chart: ", font));
        timeSeriesPanel.add(new DescriptionPanel(new JTextArea("A high/low/open/close chart based on data in HighLowDataset.")));
        JButton b19 = JRefineryUtilities.createJButton("Display", font);
        b19.setActionCommand("HIGH_LOW_CHART");
        b19.addActionListener(this);
        timeSeriesPanel.add(b19);

        otherPanel.add(JRefineryUtilities.createJLabel("Vertical XY Bar Chart: ", font));
        otherPanel.add(new DescriptionPanel(new JTextArea("A chart showing vertical bars, based on data in an IntervalXYDataset.")));
        JButton b7 = JRefineryUtilities.createJButton("Display", font);
        b7.setActionCommand("VERTICAL_XY_BAR_CHART");
        b7.addActionListener(this);
        otherPanel.add(b7);

        testPanel.add(JRefineryUtilities.createJLabel("Dynamic Chart: ", font));
        testPanel.add(new DescriptionPanel(new JTextArea("A dynamic chart, to test the event notification mechanism.")));
        JButton b20 = JRefineryUtilities.createJButton("Display", font);
        b20.setActionCommand("DYNAMIC_CHART");
        b20.addActionListener(this);
        testPanel.add(b20);

        // Combined Charts...

        combinedPanel.add(JRefineryUtilities.createJLabel("Overlaid Chart: ", font));
        combinedPanel.add(new DescriptionPanel(new JTextArea("Displays an overlaid chart with a HighLow and a Moving Average TimeSeries plots.")));
        JButton b28 = JRefineryUtilities.createJButton("Display", font);
        b28.setActionCommand("OVERLAID_CHART");
        b28.addActionListener(this);
        combinedPanel.add(b28);

        combinedPanel.add(JRefineryUtilities.createJLabel("Vertically Combined Chart:", font));
        combinedPanel.add(new DescriptionPanel(new JTextArea("Displays a vertically combined chart of XY, TimeSeries, HighLow and a VerticalXYBar plots.")));
        JButton b26 = JRefineryUtilities.createJButton("Display", font);
        b26.setActionCommand("VERTICALLY_COMBINED_CHART");
        b26.addActionListener(this);
        combinedPanel.add(b26);

        combinedPanel.add(JRefineryUtilities.createJLabel("Horizontally Combined Chart:", font));
        combinedPanel.add(new DescriptionPanel(new JTextArea("Displays a horizontally combined chart of XY, TimeSeries and VerticalXYBar plots.")));
        JButton b25 = JRefineryUtilities.createJButton("Display", font);
        b25.setActionCommand("HORIZONTALLY_COMBINED_CHART");
        b25.addActionListener(this);
        combinedPanel.add(b25);

        combinedPanel.add(JRefineryUtilities.createJLabel("Combined and Overlaid Chart:", font));
        combinedPanel.add(new DescriptionPanel(new JTextArea("A combined chart of a XY, overlaid TimeSeries and an overlaid HighLow & TimeSeries plots.")));
        JButton b27 = JRefineryUtilities.createJButton("Display", font);
        b27.setActionCommand("COMBINED_OVERLAID_CHART");
        b27.addActionListener(this);
        combinedPanel.add(b27);

        combinedPanel.add(JRefineryUtilities.createJLabel("Combined and Overlaid Dynamic Chart:", font));
        combinedPanel.add(new DescriptionPanel(new JTextArea("Displays a dynamic combined and  overlaid chart, to test the event notification mechanism.")));
        JButton b29 = JRefineryUtilities.createJButton("Display", font);
        b29.setActionCommand("COMBINED_OVERLAID_DYNAMIC_CHART");
        b29.addActionListener(this);
        combinedPanel.add(b29);

        tabs.add("Bar Charts", new JScrollPane(barPanel));
        tabs.add("Pie Charts", new JScrollPane(piePanel));
        tabs.add("XY Charts", new JScrollPane(xyPanel));
        tabs.add("Time Series Charts", new JScrollPane(timeSeriesPanel));
        tabs.add("Other Charts", new JScrollPane(otherPanel));
        tabs.add("Test Charts", new JScrollPane(testPanel));
        tabs.add("Combined Charts", new JScrollPane(combinedPanel));

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
        else if (command.equals("TIME_SERIES_1_CHART")) {
            displayTimeSeries1Chart();
        }
        else if (command.equals("TIME_SERIES_2_CHART")) {
            displayTimeSeries2Chart();
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
        else if (command.equals("HORIZONTALLY_COMBINED_CHART")) {
            displayHorizontallyCombinedChart();
        }
        else if (command.equals("VERTICALLY_COMBINED_CHART")) {
            displayVerticallyCombinedChart();
        }
        else if (command.equals("COMBINED_OVERLAID_CHART")) {
            displayCombinedAndOverlaidChart1();
        }
        else if (command.equals("OVERLAID_CHART")) {
            displayOverlaidChart();
        }
        else if (command.equals("COMBINED_OVERLAID_DYNAMIC_CHART")) {
            displayCombinedAndOverlaidDynamicXYChart();
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
            JRefineryUtilities.positionFrameRandomly(dynamicXYChartFrame);
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
            JRefineryUtilities.positionFrameRandomly(verticalBarChartFrame);
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
            JRefineryUtilities.positionFrameRandomly(verticalStackedBarChartFrame);
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
            JRefineryUtilities.positionFrameRandomly(verticalStacked3DBarChartFrame);
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
            TimeSeriesCollection data = DemoDatasetFactory.createTimeSeriesCollection1();
            JFreeChart chart = ChartFactory.createVerticalXYBarChart(title, xAxisLabel, yAxisLabel,
                                                                     data, true);


            // then customise it a little...
            chart.setChartBackgroundPaint(new GradientPaint(0, 0, Color.white, 1000, 0, Color.blue));

            // and present it in a panel...
            verticalXYBarChartFrame = new JFreeChartFrame("Vertical XY Bar Chart", chart);
            verticalXYBarChartFrame.pack();
            JRefineryUtilities.positionFrameRandomly(verticalXYBarChartFrame);
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
            JRefineryUtilities.positionFrameRandomly(vertical3DBarChartFrame);
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
            JRefineryUtilities.positionFrameRandomly(horizontalBarChartFrame);
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
            JRefineryUtilities.positionFrameRandomly(horizontalStackedBarChartFrame);
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
            JRefineryUtilities.positionFrameRandomly(lineChartFrame);
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
            JRefineryUtilities.positionFrameRandomly(pieChartOneFrame);
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
            JRefineryUtilities.positionFrameRandomly(pieChartTwoFrame);
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
            JRefineryUtilities.positionFrameRandomly(xyPlotFrame);
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
            JRefineryUtilities.positionFrameRandomly(xyPlotZeroDataFrame);
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
            JRefineryUtilities.positionFrameRandomly(scatterPlotFrame);
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
            JRefineryUtilities.positionFrameRandomly(xyPlotNullDataFrame);
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
    private void displayTimeSeries1Chart() {

        if (this.timeSeries1ChartFrame==null) {

            // create a default chart based on some sample data...
            String title = "Time Series Chart";
            String xAxisLabel = "Date";
            String yAxisLabel = "CCY per GBP";
            XYDataset data = DemoDatasetFactory.createTimeSeriesCollection3();
            JFreeChart chart = ChartFactory.createTimeSeriesChart(title, xAxisLabel, yAxisLabel, data,
                                                              true);

            // then customise it a little...
            TextTitle subtitle = new TextTitle("Value of GBP in JPY", new Font("SansSerif", Font.BOLD, 12));
            chart.addTitle(subtitle);
            chart.setChartBackgroundPaint(new GradientPaint(0, 0, Color.white,0, 1000, Color.blue));
            Plot plot = chart.getPlot();

            // and present it in a frame...
            timeSeries1ChartFrame = new JFreeChartFrame("Time Series Chart", chart);
            timeSeries1ChartFrame.pack();
            JRefineryUtilities.positionFrameRandomly(timeSeries1ChartFrame);
            timeSeries1ChartFrame.setVisible(true);

        }
        else {
            timeSeries1ChartFrame.setVisible(true);
            timeSeries1ChartFrame.requestFocus();
        }

    }

    /**
     * Displays a vertical bar chart in its own frame.
     */
    private void displayTimeSeries2Chart() {

        if (this.timeSeries2ChartFrame==null) {

            // create a default chart based on some sample data...
            String title = "Time Series Chart";
            String xAxisLabel = "Date";
            String yAxisLabel = "CCY per GBP";
            XYDataset data = DemoDatasetFactory.createTimeSeriesCollection2();
            JFreeChart chart = ChartFactory.createTimeSeriesChart(title, xAxisLabel, yAxisLabel, data,
                                                              true);

            // then customise it a little...
            TextTitle subtitle = new TextTitle("Value of GBP", new Font("SansSerif", Font.BOLD, 12));
            chart.addTitle(subtitle);
            chart.setChartBackgroundPaint(new GradientPaint(0, 0, Color.white,0, 1000, Color.blue));
            Plot plot = chart.getPlot();

            // and present it in a frame...
            timeSeries2ChartFrame = new JFreeChartFrame("Time Series Chart", chart);
            timeSeries2ChartFrame.pack();
            JRefineryUtilities.positionFrameRandomly(timeSeries2ChartFrame);
            timeSeries2ChartFrame.setVisible(true);

        }
        else {
            timeSeries2ChartFrame.setVisible(true);
            timeSeries2ChartFrame.requestFocus();
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
            String valueAxisLabel = "CCY per GBP";
            XYDataset data = DemoDatasetFactory.createTimeSeriesCollection2();
            MovingAveragePlotFitAlgorithm mavg = new MovingAveragePlotFitAlgorithm();
	    mavg.setPeriod(30);
            PlotFit pf = new PlotFit(data, mavg);
            data = pf.getFit();
            JFreeChart chart = ChartFactory.createTimeSeriesChart(title, timeAxisLabel, valueAxisLabel,
                                                              data, true);

            // then customise it a little...
            TextTitle subtitle = new TextTitle("30 day moving average of GBP", new Font("SansSerif", Font.BOLD, 12));
            chart.addTitle(subtitle);
            chart.setChartBackgroundPaint(new GradientPaint(0, 0, Color.white,0, 1000, Color.blue));


            // and present it in a frame...
            timeSeriesWithMAChartFrame = new JFreeChartFrame("Time Series Chart", chart);
            timeSeriesWithMAChartFrame.pack();
            JRefineryUtilities.positionFrameRandomly(timeSeriesWithMAChartFrame);
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
            TextTitle subtitle = new TextTitle("IBM Stock Price", new Font("SansSerif", Font.BOLD, 12));
            chart.addTitle(subtitle);
            chart.setChartBackgroundPaint(new GradientPaint(0, 0, Color.white, 0, 1000, Color.magenta));


            // and present it in a frame...
            highLowChartFrame = new JFreeChartFrame("High/Low/Open/Close Chart", chart);
            highLowChartFrame.pack();
            JRefineryUtilities.positionFrameRandomly(highLowChartFrame);
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
            String yAxisLabel = "CCY per GBP";
            XYDataset data = DemoDatasetFactory.createTimeSeriesCollection2();
            JFreeChart chart = ChartFactory.createTimeSeriesChart(title, xAxisLabel, yAxisLabel, data,
                                                              true);

            // then customise it a little...
            TextTitle subtitle = new TextTitle("Value of GBP", new Font("SansSerif", Font.BOLD, 12));
            chart.addTitle(subtitle);
            chart.setChartBackgroundPaint(new GradientPaint(0, 0, Color.white,0, 1000, Color.gray));
            Plot plot = chart.getPlot();

            // and present it in a frame...
            timeSeriesChartScrollFrame = new JFreeChartFrame("Time Series Chart", chart, true);
            timeSeriesChartScrollFrame.pack();
            JRefineryUtilities.positionFrameRandomly(timeSeriesChartScrollFrame);
            timeSeriesChartScrollFrame.show();

        }
        else {
            timeSeriesChartScrollFrame.show();
            timeSeriesChartScrollFrame.requestFocus();
        }

    }

    /**
     * Displays a horizontally combined plot in its own frame.
     */
    private void displayHorizontallyCombinedChart() {

        if (this.horizontallyCombinedChartFrame==null) {

            // create a default chart based on some sample data...
            String title = "Horizontally Combined Chart";
            String[] xAxisLabel = { "Date1", "Date2", "Date3" };
            String yAxisLabel = "CCY per GBP";;
            int[] weight = { 1, 1, 1 }; // control horizontal space assigned to each chart

            // calculate Time Series and Moving Average Dataset
            MovingAveragePlotFitAlgorithm mavg = new MovingAveragePlotFitAlgorithm();
	    mavg.setPeriod(30);
            PlotFit pf = new PlotFit(DemoDatasetFactory.createTimeSeriesCollection2(), mavg);
            XYDataset tempDataset = pf.getFit();

            // create master dataset
            CombinedDataset data = new CombinedDataset();
            data.add(tempDataset);                // time series + MA

            // test SubSeriesDataset and CombinedDataset operations

            // decompose data into its two dataset series
            SeriesDataset series0 = new SubSeriesDataset(data, 0);
            SeriesDataset series1 = new SubSeriesDataset(data, 1);

            // this code could probably go later in the ChartFactory class

            JFreeChart chart = null;

            try {
                // make a horizintal axis for each sub-plot
                Axis[] timeAxis = new HorizontalDateAxis[3];
                for (int i=0; i<timeAxis.length; i++) {
                  timeAxis[i] = new HorizontalDateAxis(xAxisLabel[i]);
                }

                // make a common vertical axis for all the sub-plots
                NumberAxis valueAxis = new VerticalNumberAxis(yAxisLabel);
                valueAxis.setAutoRangeIncludesZero(false);  // override default

                // make a horizontally combined plot
                CombinedPlot combinedPlot = new CombinedPlot(valueAxis, CombinedPlot.HORIZONTAL);
                CombinedChart chartToCombine;

                // add a XY plot
                chartToCombine = ChartFactory.createCombinableXYChart(timeAxis[0], valueAxis, series0);
                combinedPlot.add(chartToCombine, weight[0]);

                // add a TimeSeries plot
                chartToCombine = ChartFactory.createCombinableTimeSeriesChart(timeAxis[1], valueAxis, data);
                combinedPlot.add(chartToCombine, weight[1]);

                // add a VerticalXYBar plot
                chartToCombine = ChartFactory.createCombinableVerticalXYBarChart(timeAxis[2], valueAxis, series0);
                combinedPlot.add(chartToCombine, weight[2]);

                // call this method after all sub-plots have been added
                combinedPlot.adjustPlots();

                // now make tht top level JFreeChart
                chart = new JFreeChart(data, combinedPlot, title, JFreeChart.DEFAULT_TITLE_FONT, true);
            }
            catch (AxisNotCompatibleException e) {
                // this won't happen unless you mess with the axis constructors above
                System.err.println("axis not compatible.");
            }
            catch (PlotNotCompatibleException e) {
                // this won't happen unless you mess with the axis constructors above
                System.err.println("plot not compatible.");
            }

            // then customise it a little...
            TextTitle subtitle = new TextTitle("Charts combined: XY, TimeSeries and VerticalXYBar plots",
                                               new Font("SansSerif", Font.BOLD, 12));
            chart.addTitle(subtitle);
            chart.setChartBackgroundPaint(new GradientPaint(0, 0, Color.white,0, 1000, Color.blue));

            // and present it in a frame...
            horizontallyCombinedChartFrame = new JFreeChartFrame("Combined Chart #1", chart);
            horizontallyCombinedChartFrame.pack();
            JRefineryUtilities.positionFrameRandomly(horizontallyCombinedChartFrame);
            horizontallyCombinedChartFrame.show();

        }
        else {
            horizontallyCombinedChartFrame.show();
            horizontallyCombinedChartFrame.requestFocus();
        }

    }

    /**
     * Displays a vertically combined plot in its own frame. This chart displays
     * a XYPlot, TimeSeriesPlot, HighLowPlot and VerticalXYBarPlot together.
     */
    private void displayVerticallyCombinedChart() {

        if (this.verticallyCombinedChartFrame==null) {

            // create a default chart based on some sample data...
            String title = "Vertically Combined Chart";
            String xAxisLabel = "Date";
            String[] yAxisLabel = { "CCY per GBP", "Pounds", "IBM", "Bars" };
            int[] weight = { 1, 1, 1, 1 }; // control vertical space allocated to each sub-plot

            // calculate Time Series and Moving Average Dataset
            MovingAveragePlotFitAlgorithm mavg = new MovingAveragePlotFitAlgorithm();
	    mavg.setPeriod(30);
            PlotFit pf = new PlotFit(DemoDatasetFactory.createTimeSeriesCollection2(), mavg);
            XYDataset tempDataset = pf.getFit();

            // create master dataset
            CombinedDataset data = new CombinedDataset();
            data.add(tempDataset);                // time series + MA
            data.add(new SampleHighLowDataset()); // high-low data

            // test SubSeriesDataset and CombinedDataset operations

            // decompose data into its two dataset series
            SeriesDataset series0 = new SubSeriesDataset(data, 0);
            SeriesDataset series1 = new SubSeriesDataset(data, 1);
            SeriesDataset series2 = new SubSeriesDataset(data, 2);

            // compose datasets for each sub-plot
            CombinedDataset data0 = new CombinedDataset(new SeriesDataset[] {series0} );
            CombinedDataset data1 = new CombinedDataset(new SeriesDataset[] {series0, series1} );
            CombinedDataset data2 = new CombinedDataset(new SeriesDataset[] {series2} );

            // this code could probably go later in the ChartFactory class

            JFreeChart chart = null;

            try {
                // make one shared horizintal axis
                Axis timeAxis = new HorizontalDateAxis(xAxisLabel);

                // make one vertical axis for each sub-plot
                NumberAxis[] valueAxis = new NumberAxis[4];
                for (int i=0; i<valueAxis.length; i++) {
                  valueAxis[i] = new VerticalNumberAxis(yAxisLabel[i]);
                  if (i != 2) {
                    valueAxis[i].setAutoRangeIncludesZero(false);  // just for demo
                  }
                }

                // make a vertically CombinedPlot that will contain the sub-plots
                CombinedPlot combinedPlot = new CombinedPlot(timeAxis, CombinedPlot.VERTICAL);
                CombinedChart chartToCombine;

                // add a XY chart
                chartToCombine = ChartFactory.createCombinableXYChart(timeAxis, valueAxis[0], data0);
                combinedPlot.add(chartToCombine, weight[0]);

                // add a Time Series chart
                chartToCombine = ChartFactory.createCombinableTimeSeriesChart(timeAxis, valueAxis[1], data1);
                combinedPlot.add(chartToCombine, weight[1]);

                // add a High-Low chart
                chartToCombine = ChartFactory.createCombinableHighLowChart(timeAxis, valueAxis[2], data2);
                combinedPlot.add(chartToCombine, weight[2]);

                // add a VerticalXYBar chart
                chartToCombine = ChartFactory.createCombinableVerticalXYBarChart(timeAxis, valueAxis[3], data0);
                combinedPlot.add(chartToCombine, weight[3]);

                // this should be called after all sub-plots have been added
                combinedPlot.adjustPlots();

                // now make the top level JFreeChart that contains the CombinedPlot
                chart = new JFreeChart(data, combinedPlot, title, JFreeChart.DEFAULT_TITLE_FONT, true);
            }
            catch (AxisNotCompatibleException e) {
                // this won't happen unless you mess with the axis constructors above
                System.err.println("axis not compatible.");
            }
            catch (PlotNotCompatibleException e) {
                // this won't happen unless you mess with the axis constructors above
                System.err.println("axis not compatible.");
            }

            // then customise it a little...
            TextTitle subtitle = new TextTitle("Four Combined Plots: XY, TimeSeries, HighLow and VerticalXYBar",
                                               new Font("SansSerif", Font.BOLD, 12));
            chart.addTitle(subtitle);
            chart.setChartBackgroundPaint(new GradientPaint(0, 0, Color.white,0, 1000, Color.blue));

            // and present it in a frame...
            verticallyCombinedChartFrame = new JFreeChartFrame("Vertically Combined Chart", chart);
            verticallyCombinedChartFrame.pack();
            JRefineryUtilities.positionFrameRandomly(verticallyCombinedChartFrame);
            verticallyCombinedChartFrame.show();
        }
        else {
            verticallyCombinedChartFrame.show();
            verticallyCombinedChartFrame.requestFocus();
        }
    }

    /**
     * Displays a combined and overlaid plot in its own frame.
     */
    private void displayCombinedAndOverlaidChart1() {

        if (this.combinedOverlaidChartFrame1==null) {

            // create a default chart based on some sample data...
            String title = "Combined Overlaid Chart";
            String xAxisLabel = "Date";
            String[] yAxisLabel = { "CCY per GBP", "Pounds", "IBM" };
            int[] weight = { 1, 2, 2 };

            HighLowDataset highLowData = new SampleHighLowDataset();
            XYDataset timeSeriesData = DemoDatasetFactory.createTimeSeriesCollection2();

            // calculate Moving Average of High-Low Dataset
            MovingAveragePlotFitAlgorithm mavg = new MovingAveragePlotFitAlgorithm();
	    mavg.setPeriod(5);
            PlotFit pf = new PlotFit(highLowData, mavg);
            XYDataset highLowMAData = pf.getFit();

            // calculate Moving Average of Time Series
            mavg = new MovingAveragePlotFitAlgorithm();
	    mavg.setPeriod(30);
            pf = new PlotFit(timeSeriesData, mavg);
            XYDataset timeSeriesMAData = pf.getFit();

            // create master Dataset
            CombinedDataset data = new CombinedDataset();
            data.add(timeSeriesData);         // time series
            data.add(timeSeriesMAData, 1);    // time series MA (series #1 of dataset)
            data.add(highLowData);            // high-low series
            data.add(highLowMAData, 1);       // high-low MA (series #1 of dataset)

            // test XYSubDataset and CombinedDataset operations

            // decompose data into its two dataset series
            SeriesDataset series0 = new SubSeriesDataset(data, 0); // time series
            SeriesDataset series1 = new SubSeriesDataset(data, 1); // time series MA
            SeriesDataset series2 = new SubSeriesDataset(data, 2); // high-low series
            SeriesDataset series3 = new SubSeriesDataset(data, 3); // high-low MA

            // compose datasets for each sub-plot
            CombinedDataset data0 = new CombinedDataset(new SeriesDataset[] {series0} );
            CombinedDataset data1 = new CombinedDataset(new SeriesDataset[] {series0, series1} );
            CombinedDataset data2 = new CombinedDataset(new SeriesDataset[] {series2, series3} );

            // this code could probably go later in the ChartFactory class

            JFreeChart chart = null;
            int n = 3;    // number of combined (vertically laidout) charts

            try {
                // common time axis
                Axis timeAxis = new HorizontalDateAxis(xAxisLabel);

                // make one vertical axis for each (vertical) chart
                NumberAxis[] valueAxis = new NumberAxis[3];
                for (int i=0; i<valueAxis.length; i++) {
                  valueAxis[i] = new VerticalNumberAxis(yAxisLabel[i]);
                  if (i <= 1) {
                    valueAxis[i].setAutoRangeIncludesZero(false);  // override default
                  }
                }

                // create CombinedPlot...
                CombinedPlot combinedPlot = new CombinedPlot(timeAxis, CombinedPlot.VERTICAL);
                OverlaidPlot overlaidPlot;
                CombinedChart chartToCombine;

                // simple XY chart
                chartToCombine = ChartFactory.createCombinableXYChart(timeAxis, valueAxis[0], data0);
                combinedPlot.add(chartToCombine, weight[0]);

                // two TimeSeriesCharts overlaid (share both axes)
                overlaidPlot = new OverlaidPlot(timeAxis, valueAxis[1]);
                overlaidPlot.add(ChartFactory.createCombinableTimeSeriesChart(timeAxis, valueAxis[1], series0));
                overlaidPlot.add(ChartFactory.createCombinableTimeSeriesChart(timeAxis, valueAxis[1], series1));
                chartToCombine = ChartFactory.createCombinableChart(data, overlaidPlot);
                combinedPlot.add(chartToCombine, weight[1]);

                // a HighLowChart and a TimeSeriesChart overlaid (share both axes)
                overlaidPlot = new OverlaidPlot(timeAxis, valueAxis[2]);
                overlaidPlot.add(ChartFactory.createCombinableHighLowChart(timeAxis, valueAxis[2], series2));
                overlaidPlot.add(ChartFactory.createCombinableTimeSeriesChart(timeAxis, valueAxis[2], series3));
                chartToCombine = ChartFactory.createCombinableChart(data, overlaidPlot);
                combinedPlot.add(chartToCombine, weight[2]);

                // call this method after all sub-plots have been added
                combinedPlot.adjustPlots();

                // now create the master JFreeChart object
                chart = new JFreeChart(data, combinedPlot, title, JFreeChart.DEFAULT_TITLE_FONT, true);
            }
            catch (AxisNotCompatibleException e) {
                // this won't happen unless you mess with the axis constructors above
                System.err.println("axis not compatible.");
            }
            catch (PlotNotCompatibleException e) {
                // this won't happen unless you mess with the axis constructors above
                System.err.println("axis not compatible.");
            }

            // then customise it a little...
            TextTitle subtitle = new TextTitle("Combined and Overlaid Charts: XY, Overlaid[two TimeSeries], Overlaid[HighLow and TimeSeries]",
                                               new Font("SansSerif", Font.BOLD, 12));
            chart.addTitle(subtitle);
            chart.setChartBackgroundPaint(new GradientPaint(0, 0, Color.white,0, 1000, Color.blue));

            // and present it in a frame...
            combinedOverlaidChartFrame1 = new JFreeChartFrame("Combined Chart #2", chart);
            combinedOverlaidChartFrame1.pack();
            JRefineryUtilities.positionFrameRandomly(combinedOverlaidChartFrame1);
            combinedOverlaidChartFrame1.show();

        }
        else {
            combinedOverlaidChartFrame1.show();
            combinedOverlaidChartFrame1.requestFocus();
        }

    }

    /**
     * Displays a combined and overlaid plot in its own frame.
     */
    private void displayOverlaidChart() {

        if (this.overlaidChartFrame==null) {

            // create a default chart based on some sample data...
            String title = "Overlaid Chart";
            String xAxisLabel = "Date";
            String yAxisLabel = "IBM";

            // calculate High-Low and Moving Average Dataset
            HighLowDataset highLowData = new SampleHighLowDataset();
            MovingAveragePlotFitAlgorithm mavg = new MovingAveragePlotFitAlgorithm();
	    mavg.setPeriod(5);
            PlotFit pf = new PlotFit(highLowData, mavg);
            XYDataset maData = pf.getFit();

            // make master Dataset as combination of highLowData and the MA
            CombinedDataset data = new CombinedDataset();
            data.add(highLowData);
            data.add(new SubSeriesDataset(maData, 1));    // extract MA series from maData as demo

            // decompose data into its two dataset series
            SeriesDataset series0 = new SubSeriesDataset(data, 0); // high-low series
            SeriesDataset series1 = new SubSeriesDataset(data, 1); // MA data

            // this code could probably go later in the ChartFactory class

            JFreeChart chart = null;

            try {
                // common horizontal and vertical axes
                Axis timeAxis = new HorizontalDateAxis(xAxisLabel);
                NumberAxis valueAxis = new VerticalNumberAxis(yAxisLabel);

                // make an overlaid CombinedPlot
                CombinedPlot overlaidPlot = new CombinedPlot(timeAxis, valueAxis);

                // add the sub-plots
                overlaidPlot.add(ChartFactory.createCombinableHighLowChart(timeAxis, valueAxis, series0));    // high-low
                overlaidPlot.add(ChartFactory.createCombinableTimeSeriesChart(timeAxis, valueAxis, series1)); // MA

                // call this method after all sub-plots have been added
                overlaidPlot.adjustPlots();

                // make the top level JFreeChart object
                chart = new JFreeChart(data, overlaidPlot, title, JFreeChart.DEFAULT_TITLE_FONT, true);
            }
            catch (AxisNotCompatibleException e) {
                // this won't happen unless you mess with the axis constructors above
                System.err.println("axis not compatible.");
            }
            catch (PlotNotCompatibleException e) {
                // this won't happen unless you mess with the axis constructors above
                System.err.println("axis not compatible.");
            }

            // then customise it a little...
            TextTitle subtitle = new TextTitle("Overlaid Chart (HighLow and TimeSeries plots)",
                                               new Font("SansSerif", Font.BOLD, 12));
            chart.addTitle(subtitle);
            chart.setChartBackgroundPaint(new GradientPaint(0, 0, Color.white,0, 1000, Color.blue));

            // and present it in a frame...
            overlaidChartFrame = new JFreeChartFrame("Overlaid Chart: HighLow and TimeSeries plots", chart);
            overlaidChartFrame.pack();
            JRefineryUtilities.positionFrameRandomly(overlaidChartFrame);
            overlaidChartFrame.show();

        }
        else {
            overlaidChartFrame.show();
            overlaidChartFrame.requestFocus();
        }

    }


    /**
     * Displays an XY chart that is periodically updated by a background thread.  This is to
     * demonstrate the event notification system that automatically updates charts as required.
     */
    private void displayCombinedAndOverlaidDynamicXYChart() {
        if (combinedAndOverlaidDynamicXYChartFrame==null) {

            // chart title and axis labels...
            String title = "Combined and Overlaid Dynamic Chart";
            String xAxisLabel = "X";
            String[] yAxisLabel = { "Y", "Y", "Y", "Y" };

            // setup sample base 2-series dataset
            SampleXYDataset data = new SampleXYDataset();

            // create some SubSeriesDatasets and CombinedDatasets to test events
            SubSeriesDataset series0 = new SubSeriesDataset(data, 0);
            SubSeriesDataset series1 = new SubSeriesDataset(data, 1);

            CombinedDataset combinedData = new CombinedDataset();
            combinedData.add(series0);
            combinedData.add(series1);

            // create common time axis
            HorizontalNumberAxis timeAxis = new HorizontalNumberAxis(xAxisLabel);
            timeAxis.setTickMarksVisible(true);
            timeAxis.setAutoRangeIncludesZero(false);

            // make one vertical axis for each (vertical) chart
            NumberAxis[] valueAxis = new NumberAxis[4];
            for (int i=0; i<valueAxis.length; i++) {
              valueAxis[i] = new VerticalNumberAxis(yAxisLabel[i]);
              valueAxis[i].setAutoRangeIncludesZero(false);
            }

            // create some combined and overlaid charts
            JFreeChart chart = null;
            try {
                CombinedPlot combinedPlot = new CombinedPlot(timeAxis, CombinedPlot.VERTICAL);
                OverlaidPlot overlaidPlot;
                CombinedChart chartToCombine;

                // add first simple XY chart
                chartToCombine = ChartFactory.createCombinableXYChart(timeAxis, valueAxis[0], series0);
                combinedPlot.add(chartToCombine);

                // add second simple XY chart
                chartToCombine = ChartFactory.createCombinableXYChart(timeAxis, valueAxis[0], series1);
                combinedPlot.add(chartToCombine);

                // add two overlaid XY charts (share both axes)
                overlaidPlot = new OverlaidPlot(timeAxis, valueAxis[1]);
                overlaidPlot.add(ChartFactory.createCombinableXYChart(timeAxis, valueAxis[1], series0));
                overlaidPlot.add(ChartFactory.createCombinableXYChart(timeAxis, valueAxis[1], series1));
                chartToCombine = ChartFactory.createCombinableChart(combinedData, overlaidPlot);
                combinedPlot.add(chartToCombine);

                // add one XY chart with both series data
                chartToCombine = ChartFactory.createCombinableXYChart(timeAxis, valueAxis[2], combinedData);
                combinedPlot.add(chartToCombine);

                combinedPlot.adjustPlots();

                chart = new JFreeChart(data, combinedPlot, title, JFreeChart.DEFAULT_TITLE_FONT, true);
            }
            catch (AxisNotCompatibleException e) {
                // this won't happen unless you mess with the axis constructors above
                System.err.println("axis not compatible.");
            }
            catch (PlotNotCompatibleException e) {
                // this won't happen unless you mess with the axis constructors above
                System.err.println("axis not compatible.");
            }

            // display the combined and overlaid dynamic charts
            combinedAndOverlaidDynamicXYChartFrame = new JFreeChartFrame("Dynamic Combined and Overlaid Chart", chart);

            // then customise it a little...
            TextTitle subtitle = new TextTitle("Plots: XY[series 0], XY[series 1], Overlaid[XY[series 0] and XY[series 1]] and XY[both series]",
                                               new Font("SansSerif", Font.BOLD, 12));
            chart.addTitle(subtitle);
            chart.setChartBackgroundPaint(new GradientPaint(0, 0, Color.white,0, 1000, Color.cyan));

            // show the frame
            combinedAndOverlaidDynamicXYChartFrame.pack();
            JRefineryUtilities.positionFrameRandomly(combinedAndOverlaidDynamicXYChartFrame);
            combinedAndOverlaidDynamicXYChartFrame.show();

            // setup thread to update base Dataset
            SampleXYDatasetThread update = new SampleXYDatasetThread(data);
            Thread thread = new Thread(update);
            thread.start();
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
                                        "(C)opyright 2000, 2001, Simba Management Limited and Contributors",
                                        Licences.LGPL, this.contributors);
            aboutFrame.pack();
            JRefineryUtilities.centerFrameOnScreen(aboutFrame);
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

        JRefineryUtilities.centerFrameOnScreen(f);

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
