/* ===============
 * JFreeChart Demo
 * ===============
 *
 * Project Info:  http://www.object-refinery.com/jfreechart/index.html
 * Project Lead:  David Gilbert (david.gilbert@object-refinery.com);
 *
 * (C) Copyright 2000-2002, by Simba Management Limited and Contributors.
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
 * JFreeChartDemo.java
 * -------------------
 * (C) Copyright 2000-2002, by Simba Management Limited and Contributors.
 *
 * Original Author:  David Gilbert (for Simba Management Limited);
 * Contributor(s):   Andrzej Porebski;
 *                   Matthew Wright;
 *                   Serge V. Grachov;
 *                   Bill Kelemen;
 *                   Achilleus Mantzios;
 *
 * $Id: JFreeChartDemo.java,v 1.1 2007/10/10 19:01:20 vauchers Exp $
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
 * 12-Dec-2001 : Added Candlestick chart (DG);
 * 23-Jan-2002 : Added a test chart for single series bar charts (DG);
 * 06-Feb-2002 : Added sample wind plot (DG);
 * 15-Mar-2002 : Now using ResourceBundle to fetch strings and other items displayed to the
 *               user.  This will allow for localisation (DG);
 * 09-Apr-2002 : Changed horizontal bar chart to use integer tick units (DG);
 * 19-Apr-2002 : Renamed JRefineryUtilities-->RefineryUtilities (DG);
 *
 */

package com.jrefinery.chart.demo;

import java.awt.Dimension;
import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.Paint;
import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.event.WindowAdapter;
import java.util.Date;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.ResourceBundle;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JScrollPane;
import javax.swing.JOptionPane;
import javax.swing.BorderFactory;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.ImageIcon;
import javax.swing.JTextArea;
import javax.swing.JButton;
import com.jrefinery.data.*;
import com.jrefinery.chart.*;
import com.jrefinery.chart.data.PlotFit;
import com.jrefinery.chart.data.MovingAveragePlotFitAlgorithm;
import com.jrefinery.layout.LCBLayout;
import com.jrefinery.ui.RefineryUtilities;
import com.jrefinery.ui.about.AboutFrame;
import com.jrefinery.chart.tooltips.HighLowToolTipGenerator;
import com.jrefinery.chart.tooltips.TimeSeriesToolTipGenerator;

/**
 * The main frame in the chart demonstration application.
 */
public class JFreeChartDemo extends JFrame
                            implements ActionListener, WindowListener {


    //Constants representing action commands...
    public static final String EXIT_COMMAND = "EXIT";
    public static final String ABOUT_COMMAND = "ABOUT";

    public static final String CHART_1_COMMAND = "HORIZONTAL_BAR_CHART";
    public static final String CHART_2_COMMAND = "HORIZONTAL_STACKED_BAR_CHART";
    public static final String CHART_3_COMMAND = "VERTICAL_BAR_CHART";
    public static final String CHART_4_COMMAND = "VERTICAL_3D_BAR_CHART";
    public static final String CHART_5_COMMAND = "VERTICAL_STACKED_BAR_CHART";
    public static final String CHART_6_COMMAND = "VERTICAL_STACKED_3D_BAR_CHART";
    public static final String CHART_7_COMMAND = "PIE_CHART_1";
    public static final String CHART_8_COMMAND = "PIE_CHART_2";
    public static final String CHART_9_COMMAND = "XY_PLOT";
    public static final String CHART_10_COMMAND = "TIME_SERIES_1_CHART";
    public static final String CHART_11_COMMAND = "TIME_SERIES_2_CHART";
    public static final String CHART_12_COMMAND = "TIME_SERIES_WITH_MA_CHART";
    public static final String CHART_13_COMMAND = "HIGH_LOW_CHART";
    public static final String CHART_14_COMMAND = "CANDLESTICK_CHART";
    public static final String CHART_15_COMMAND = "SIGNAL_CHART";
    public static final String CHART_16_COMMAND = "WIND_PLOT";
    public static final String CHART_17_COMMAND = "SCATTER_PLOT";
    public static final String CHART_18_COMMAND = "LINE_CHART";
    public static final String CHART_19_COMMAND = "VERTICAL_XY_BAR_CHART";
    public static final String CHART_20_COMMAND = "XY_PLOT_NULL";
    public static final String CHART_21_COMMAND = "XY_PLOT_ZERO";
    public static final String CHART_22_COMMAND = "TIME_SERIES_CHART_SCROLL";
    public static final String CHART_23_COMMAND = "SINGLE_SERIES_BAR_CHART";
    public static final String CHART_24_COMMAND = "DYNAMIC_CHART";
    public static final String CHART_25_COMMAND = "OVERLAID_CHART";
    public static final String CHART_26_COMMAND = "VERTICALLY_COMBINED_CHART";
    public static final String CHART_27_COMMAND = "HORIZONTALLY_COMBINED_CHART";
    public static final String CHART_28_COMMAND = "COMBINED_OVERLAID_CHART";
    public static final String CHART_29_COMMAND = "COMBINED_OVERLAID_DYNAMIC_CHART";

    /** The preferred size for the frame. */
    public static final Dimension PREFERRED_SIZE = new Dimension(780, 400);

    /** A frame for displaying a horizontal bar chart. */
    private ChartFrame horizontalBarChartFrame;

    /** A frame for displaying a horizontal stacked bar chart. */
    private ChartFrame horizontalStackedBarChartFrame;

    /** A frame for displaying a vertical bar chart. */
    private ChartFrame verticalBarChartFrame;

    /** A frame for displaying a vertical stacked bar chart. */
    private ChartFrame verticalStackedBarChartFrame;

    /** A frame for displaying a vertical 3D bar chart. */
    private ChartFrame vertical3DBarChartFrame;

    /** A frame for displaying a vertical stacked 3D bar chart. */
    private ChartFrame verticalStacked3DBarChartFrame;

    /** A frame for displaying a vertical XY bar chart. */
    private ChartFrame verticalXYBarChartFrame;

    /** A frame for displaying a line chart. */
    private ChartFrame lineChartFrame;

    /** A frame for displaying a pie chart. */
    private ChartFrame pieChartOneFrame;

    /** A frame for displaying a pie chart. */
    private ChartFrame pieChartTwoFrame;

    /** A frame for displaying a scatter plot chart. */
    private ChartFrame scatterPlotFrame;

    /** A frame for displaying a wind plot. */
    private ChartFrame windPlotFrame;

    /** A frame for displaying an XY plot chart. */
    private ChartFrame xyPlotFrame;

    /** A frame for displaying a chart with null data. */
    private ChartFrame xyPlotNullDataFrame;

    /** A frame for displaying a chart with zero data series. */
    private ChartFrame xyPlotZeroDataFrame;

    /** A frame for displaying a time series chart. */
    private ChartFrame timeSeries1ChartFrame;

    /** A frame for displaying a time series chart. */
    private ChartFrame timeSeries2ChartFrame;

    /** A frame for displaying a time series chart with a moving average. */
    private ChartFrame timeSeriesWithMAChartFrame;

    /** A frame for displaying a chart in a scroll pane. */
    private ChartFrame timeSeriesChartScrollFrame;

    /** A frame for displaying a high/low/open/close chart. */
    private ChartFrame highLowChartFrame;

    /** A frame for displaying a candlestick chart. */
    private ChartFrame candlestickChartFrame;

    /** A frame for displaying a signal chart. */
    private ChartFrame signalChartFrame;

    /** A frame for displaying a dynamic XY plot chart. */
    private ChartFrame dynamicXYChartFrame;

    private ChartFrame singleSeriesBarChartFrame;

    /** A frame for displaying a horizontally Combined plot chart. */
    private ChartFrame horizontallyCombinedChartFrame;

    /** A frame for displaying a vertically Combined plot chart. */
    private ChartFrame verticallyCombinedChartFrame;

    /** A frame for displaying a Combined plot chart. */
    private ChartFrame combinedOverlaidChartFrame1;

    /** A frame for displaying a Combined plot chart. */
    private ChartFrame overlaidChartFrame;

    /** A frame for displaying a Combined and Overlaid Dynamic chart. */
    private ChartFrame combinedAndOverlaidDynamicXYChartFrame;

    /** A frame for displaying information about the application. */
    private AboutFrame aboutFrame;

    /** A tabbed pane for displaying sample charts; */
    private JTabbedPane tabbedPane;

    private ResourceBundle resources;

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

        // get a locale-specific resource bundle...
        String baseResourceClass = "com.jrefinery.chart.demo.resources.DemoResources";
        this.resources = ResourceBundle.getBundle(baseResourceClass);

        // set up the menu
        JMenuBar menuBar = createMenuBar(resources);
        setJMenuBar(menuBar);

        JPanel content = new JPanel(new BorderLayout());
        content.add(createTabbedPane(resources));
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
     * Handles menu selections by passing control to an appropriate method.
     */
    public void actionPerformed(ActionEvent event) {

        String command = event.getActionCommand();
        if (command.equals(EXIT_COMMAND)) {
            attemptExit();
        }
        else if (command.equals(ABOUT_COMMAND)) {
            about();
        }
        else if (command.equals(CHART_1_COMMAND)) {
            displayHorizontalBarChart();
        }
        else if (command.equals(CHART_2_COMMAND)) {
            displayHorizontalStackedBarChart();
        }
        else if (command.equals(CHART_3_COMMAND)) {
            displayVerticalBarChart();
        }
        else if (command.equals(CHART_4_COMMAND)) {
            displayVertical3DBarChart();
        }
        else if (command.equals(CHART_5_COMMAND)) {
            displayVerticalStackedBarChart();
        }
        else if (command.equals(CHART_6_COMMAND)) {
            displayVerticalStacked3DBarChart();
        }
        else if (command.equals(CHART_7_COMMAND)) {
            displayPieChartOne();
        }
        else if (command.equals(CHART_8_COMMAND)) {
            displayPieChartTwo();
        }
        else if (command.equals(CHART_9_COMMAND)) {
            displayXYPlot();
        }
        else if (command.equals(CHART_10_COMMAND)) {
            displayTimeSeries1Chart();
        }
        else if (command.equals(CHART_11_COMMAND)) {
            displayTimeSeries2Chart();
        }
        else if (command.equals(CHART_12_COMMAND)) {
            displayTimeSeriesWithMAChart();
        }
        else if (command.equals(CHART_13_COMMAND)) {
            displayHighLowChart();
        }
        else if (command.equals(CHART_14_COMMAND)) {
            displayCandlestickChart();
        }
        else if (command.equals(CHART_15_COMMAND)) {
            displaySignalChart();
        }
        else if (command.equals(CHART_16_COMMAND)) {
            displayWindPlot();
        }
        else if (command.equals(CHART_17_COMMAND)) {
            displayScatterPlot();
        }
        else if (command.equals(CHART_18_COMMAND)) {
            displayLineChart();
        }
        else if (command.equals(CHART_19_COMMAND)) {
            displayVerticalXYBarChart();
        }
        else if (command.equals(CHART_20_COMMAND)) {
            displayNullXYPlot();
        }
        else if (command.equals(CHART_21_COMMAND)) {
            displayXYPlotZeroData();
        }
        else if (command.equals(CHART_22_COMMAND)) {
            displayTimeSeriesChartInScrollPane();
        }
        else if (command.equals(CHART_23_COMMAND)) {
            displaySingleSeriesBarChart();
        }
        else if (command.equals(CHART_24_COMMAND)) {
            displayDynamicXYChart();
        }
        else if (command.equals(CHART_25_COMMAND)) {
            displayOverlaidChart();
        }
        else if (command.equals(CHART_26_COMMAND)) {
            displayHorizontallyCombinedChart();
        }
        else if (command.equals(CHART_27_COMMAND)) {
            displayVerticallyCombinedChart();
        }
        else if (command.equals(CHART_28_COMMAND)) {
            displayCombinedAndOverlaidChart1();
        }
        else if (command.equals(CHART_29_COMMAND)) {
            displayCombinedAndOverlaidDynamicXYChart();
        }
    }

    /**
     * Exits the application, but only if the user agrees.
     */
    private void attemptExit() {

        String title = resources.getString("dialog.exit.title");
        String message = resources.getString("dialog.exit.message");
        int result = JOptionPane.showConfirmDialog(this, message, title,
                                                   JOptionPane.YES_NO_OPTION,
                                                   JOptionPane.QUESTION_MESSAGE);
        if (result==JOptionPane.YES_OPTION) {
            dispose();
            System.exit(0);
        }

    }

    /**
     * Displays a horizontal bar chart in its own frame.
     */
    private void displayHorizontalBarChart() {

        if (horizontalBarChartFrame==null) {

            // create a default chart based on some sample data...
            String title = resources.getString("bar.horizontal.title");
            String domain = resources.getString("bar.horizontal.domain");
            String range = resources.getString("bar.horizontal.range");

            CategoryDataset data = DemoDatasetFactory.createCategoryDataset();
            JFreeChart chart = ChartFactory.createHorizontalBarChart(title, domain, range,
                                                                     data, true);

            // then customise it a little...
            chart.setBackgroundPaint(new GradientPaint(0, 0, Color.white, 0, 1000, Color.orange));
            CategoryPlot plot = chart.getCategoryPlot();
            NumberAxis axis = (NumberAxis)plot.getRangeAxis();
            axis.setStandardTickUnits(TickUnits.createIntegerTickUnits());
            axis.setCrosshairVisible(false);
            axis.setInverted(true);

            // and present it in a frame...
            horizontalBarChartFrame = new ChartFrame(title, chart);
            horizontalBarChartFrame.pack();
            RefineryUtilities.positionFrameRandomly(horizontalBarChartFrame);
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
            String title = resources.getString("bar.horizontal-stacked.title");
            String domain = resources.getString("bar.horizontal-stacked.domain");
            String range = resources.getString("bar.horizontal-stacked.range");

            CategoryDataset data = DemoDatasetFactory.createCategoryDataset();
            JFreeChart chart = ChartFactory.createHorizontalStackedBarChart(title, domain, range,
                                                                            data, true);

            // then customise it a little...
            chart.setBackgroundPaint(new GradientPaint(0, 0, Color.white, 1000, 0, Color.blue));
            CategoryPlot plot = chart.getCategoryPlot();

            // and present it in a frame...
            horizontalStackedBarChartFrame = new ChartFrame(title, chart);
            horizontalStackedBarChartFrame.pack();
            RefineryUtilities.positionFrameRandomly(horizontalStackedBarChartFrame);
            horizontalStackedBarChartFrame.show();

        }
        else {
            horizontalStackedBarChartFrame.show();
            horizontalStackedBarChartFrame.requestFocus();
        }

    }

    /**
     * Displays a vertical bar chart in its own frame.
     */
    private void displayVerticalBarChart() {

        if (verticalBarChartFrame==null) {

            String title = resources.getString("bar.vertical.title");
            String domain = resources.getString("bar.vertical.domain");
            String range = resources.getString("bar.vertical.range");

            CategoryDataset data = DemoDatasetFactory.createCategoryDataset();
            JFreeChart chart = ChartFactory.createVerticalBarChart(title, domain, range,
                                                                   data, true);

            // then customise it a little...
            chart.setBackgroundPaint(new GradientPaint(0, 0, Color.white, 1000, 0, Color.red));
            CategoryPlot plot = (CategoryPlot)chart.getPlot();
            plot.setForegroundAlpha(0.9f);
            NumberAxis verticalAxis = (NumberAxis)plot.getRangeAxis();
            verticalAxis.setStandardTickUnits(TickUnits.createIntegerTickUnits());

            // and present it in a panel...
            verticalBarChartFrame = new ChartFrame(title, chart);
            //verticalBarChartFrame.getChartPanel().setToolTipGeneration(false);
            verticalBarChartFrame.pack();
            RefineryUtilities.positionFrameRandomly(verticalBarChartFrame);
            verticalBarChartFrame.show();

        }
        else {
            verticalBarChartFrame.show();
            verticalBarChartFrame.requestFocus();
        }

    }

    /**
     * Displays a vertical 3D bar chart in its own frame.
     */
    private void displayVertical3DBarChart() {

        if (vertical3DBarChartFrame==null) {

            // create a default chart based on some sample data...
            String title = resources.getString("bar.vertical3D.title");
            String domain = resources.getString("bar.vertical3D.domain");
            String range = resources.getString("bar.vertical3D.range");

            CategoryDataset data = DemoDatasetFactory.createCategoryDataset();
            JFreeChart chart = ChartFactory.createVerticalBarChart3D(title, domain, range,
                                                                     data, true);

            // then customise it a little...
            chart.setBackgroundPaint(new GradientPaint(0, 0, Color.white, 1000, 0, Color.blue));
            CategoryPlot plot = (CategoryPlot)chart.getPlot();
            plot.setForegroundAlpha(0.75f);
            NumberAxis axis = (NumberAxis)plot.getRangeAxis();

            // and present it in a panel...
            vertical3DBarChartFrame = new ChartFrame("Vertical 3D Bar Chart", chart);
            vertical3DBarChartFrame.pack();
            RefineryUtilities.positionFrameRandomly(vertical3DBarChartFrame);
            vertical3DBarChartFrame.show();

        }
        else {
            vertical3DBarChartFrame.show();
            vertical3DBarChartFrame.requestFocus();
        }

    }

    /**
     * Displays a vertical bar chart in its own frame.
     */
    private void displayVerticalStackedBarChart() {

        if (verticalStackedBarChartFrame==null) {

            // create a default chart based on some sample data...
            String title = resources.getString("bar.vertical-stacked.title");
            String domain = resources.getString("bar.vertical-stacked.domain");
            String range = resources.getString("bar.vertical-stacked.range");

            CategoryDataset data = DemoDatasetFactory.createCategoryDataset();
            JFreeChart chart = ChartFactory.createStackedVerticalBarChart(title, domain, range,
                                                                          data, true);

            // then customise it a little...
            chart.setBackgroundPaint(new GradientPaint(0, 0, Color.white, 1000, 0, Color.red));
            Plot plot = chart.getPlot();

            // and present it in a panel...
            verticalStackedBarChartFrame = new ChartFrame(title, chart);
            verticalStackedBarChartFrame.pack();
            RefineryUtilities.positionFrameRandomly(verticalStackedBarChartFrame);
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
            String title = resources.getString("bar.vertical-stacked3D.title");
            String domain = resources.getString("bar.vertical-stacked3D.domain");
            String range = resources.getString("bar.vertical-stacked3D.range");
            CategoryDataset data = DemoDatasetFactory.createCategoryDataset();
            JFreeChart chart = ChartFactory.createStackedVerticalBarChart3D(title, domain, range,
                                                                            data, true);

            // then customise it a little...
            chart.setBackgroundPaint(new GradientPaint(0, 0, Color.white, 1000, 0, Color.red));
            Plot plot = chart.getPlot();

            // and present it in a panel...
            verticalStacked3DBarChartFrame = new ChartFrame(title, chart);
            verticalStacked3DBarChartFrame.pack();
            RefineryUtilities.positionFrameRandomly(verticalStacked3DBarChartFrame);
            verticalStacked3DBarChartFrame.show();

        }
        else {
            verticalStacked3DBarChartFrame.show();
            verticalStacked3DBarChartFrame.requestFocus();
        }

    }

    /**
     * Displays pie chart one in its own frame.
     */
    private void displayPieChartOne() {

        if (pieChartOneFrame==null) {

            // create a default chart based on some sample data...
            String title = resources.getString("pie.pie1.title");
            CategoryDataset data = DemoDatasetFactory.createCategoryDataset();
            PieDataset extracted = DatasetUtilities.createPieDataset(data, 0);
            JFreeChart chart = ChartFactory.createPieChart(title, extracted, true);

            // then customise it a little...
            chart.setBackgroundPaint(new GradientPaint(0, 0, Color.white, 0, 1000, Color.orange));
            PiePlot plot = (PiePlot)chart.getPlot();
            //plot.setOutlineStroke(null);
            plot.setCircular(false);
            // make section 1 explode by 100%...
            plot.setRadiusPercent(0.60);
            plot.setExplodePercent(1, 1.00);

            // and present it in a frame...
            pieChartOneFrame = new ChartFrame(title, chart);
            pieChartOneFrame.pack();
            RefineryUtilities.positionFrameRandomly(pieChartOneFrame);
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

        ImageIcon icon = new javax.swing.ImageIcon(JFreeChartDemo.class.getResource("gorilla.jpg"));
        Image bgimage = icon.getImage();

        if (pieChartTwoFrame==null) {

            // create a default chart based on some sample data...
            String title = resources.getString("pie.pie2.title");
            CategoryDataset data = DemoDatasetFactory.createCategoryDataset();
            Object category = data.getCategories().get(1);
            PieDataset extracted = DatasetUtilities.createPieDataset(data, category);
            JFreeChart chart = ChartFactory.createPieChart(title, extracted, true);

            // then customise it a little...
            //chart.setLegend(null);
            chart.setBackgroundPaint(Color.lightGray);
            PiePlot pie = (PiePlot)chart.getPlot();
            pie.setSectionLabelType(PiePlot.NAME_AND_PERCENT_LABELS);
            pie.setBackgroundImage(bgimage);
            pie.setBackgroundPaint(Color.white);
            pie.setBackgroundAlpha(0.6f);
            pie.setForegroundAlpha(0.75f);
            // and present it in a frame...
            pieChartTwoFrame = new ChartFrame(title, chart);
            pieChartTwoFrame.pack();
            RefineryUtilities.positionFrameRandomly(pieChartTwoFrame);
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
            String title = resources.getString("xyplot.sample1.title");
            String domain = resources.getString("xyplot.sample1.domain");
            String range = resources.getString("xyplot.sample1.range");
            XYDataset data = DemoDatasetFactory.createSampleXYDataset();
            JFreeChart chart = ChartFactory.createXYChart(title, domain, range, data, true);

            // then customise it a little...
            chart.setBackgroundPaint(new GradientPaint(0, 0, Color.white, 0, 1000, Color.green));
            //chart.getXYPlot().getDomainAxis().setVisible(false);
            //chart.getXYPlot().getRangeAxis().setVisible(false);
            // and present it in a frame...
            xyPlotFrame = new ChartFrame(title, chart);
            ChartPanel panel = xyPlotFrame.getChartPanel();
            panel.setMouseZoomable(true);
            panel.setHorizontalAxisTrace(true);
            panel.setVerticalAxisTrace(true);
            xyPlotFrame.pack();
            RefineryUtilities.positionFrameRandomly(xyPlotFrame);
            xyPlotFrame.show();

        }
        else {
            xyPlotFrame.show();
            xyPlotFrame.requestFocus();
        }

    }

    /**
     * Displays a vertical bar chart in its own frame.
     */
    private void displayTimeSeries1Chart() {

        if (this.timeSeries1ChartFrame==null) {

            // create a default chart based on some sample data...
            String title = resources.getString("timeseries.sample1.title");
            String subtitle = resources.getString("timeseries.sample1.subtitle");
            String domain = resources.getString("timeseries.sample1.domain");
            String range = resources.getString("timeseries.sample1.range");
            String copyrightStr = resources.getString("timeseries.sample1.copyright");
            XYDataset data = DemoDatasetFactory.createTimeSeriesCollection3();
            JFreeChart chart = ChartFactory.createTimeSeriesChart(title, domain, range,
                                                                  data, true);

            // then customise it a little...
            TextTitle title2 = new TextTitle(subtitle, new Font("SansSerif", Font.PLAIN, 12));
            title2.setSpacer(new Spacer(Spacer.RELATIVE, 0.05, 0.05, 0.05, 0.0));
            chart.addTitle(title2);

            TextTitle copyright = new TextTitle(copyrightStr, new Font("SansSerif", Font.PLAIN, 9));
            copyright.setPosition(TextTitle.BOTTOM);
            copyright.setHorizontalAlignment(TextTitle.RIGHT);
            chart.addTitle(copyright);

            chart.setBackgroundPaint(new GradientPaint(0, 0, Color.white, 0, 1000, Color.blue));
            XYPlot plot = chart.getXYPlot();
            HorizontalDateAxis axis = (HorizontalDateAxis)plot.getDomainAxis();
            axis.setVerticalTickLabels(true);

            // and present it in a frame...
            timeSeries1ChartFrame = new ChartFrame(title, chart);
            timeSeries1ChartFrame.pack();
            RefineryUtilities.positionFrameRandomly(timeSeries1ChartFrame);
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
            String title = resources.getString("timeseries.sample2.title");
            String subtitleStr = resources.getString("timeseries.sample2.subtitle");
            String domain = resources.getString("timeseries.sample2.domain");
            String range = resources.getString("timeseries.sample2.range");
            XYDataset data = DemoDatasetFactory.createTimeSeriesCollection4();
            JFreeChart chart = ChartFactory.createTimeSeriesChart(title, domain, range,
                                                                  data, true);

            // then customise it a little...
            TextTitle subtitle = new TextTitle(subtitleStr, new Font("SansSerif", Font.BOLD, 12));
            chart.addTitle(subtitle);
            chart.setBackgroundPaint(new GradientPaint(0, 0, Color.white, 0, 1000, Color.blue));
            Plot plot = chart.getPlot();

            VerticalLogarithmicAxis vla = new VerticalLogarithmicAxis(range);
            chart.getXYPlot().setRangeAxis(vla);

            // and present it in a frame...
            timeSeries2ChartFrame = new ChartFrame(title, chart);
            timeSeries2ChartFrame.pack();
            RefineryUtilities.positionFrameRandomly(timeSeries2ChartFrame);
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
            String title = resources.getString("timeseries.sample3.title");
            String domain = resources.getString("timeseries.sample3.domain");
            String range = resources.getString("timeseries.sample3.range");
            String subtitleStr = resources.getString("timeseries.sample3.subtitle");
            XYDataset data = DemoDatasetFactory.createTimeSeriesCollection2();
            MovingAveragePlotFitAlgorithm mavg = new MovingAveragePlotFitAlgorithm();
            mavg.setPeriod(30);
            PlotFit pf = new PlotFit(data, mavg);
            data = pf.getFit();
            JFreeChart chart = ChartFactory.createTimeSeriesChart(title, domain, range,
                                                                  data, true);

            // then customise it a little...
            TextTitle subtitle = new TextTitle(subtitleStr, new Font("SansSerif", Font.BOLD, 12));
            chart.addTitle(subtitle);
            chart.setBackgroundPaint(new GradientPaint(0, 0, Color.white,0, 1000, Color.blue));


            // and present it in a frame...
            timeSeriesWithMAChartFrame = new ChartFrame(title, chart);
            timeSeriesWithMAChartFrame.pack();
            RefineryUtilities.positionFrameRandomly(timeSeriesWithMAChartFrame);
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
            String title = resources.getString("timeseries.highlow.title");
            String domain = resources.getString("timeseries.highlow.domain");
            String range = resources.getString("timeseries.highlow.range");
            String subtitleStr = resources.getString("timeseries.highlow.subtitle");
            HighLowDataset data = DemoDatasetFactory.createSampleHighLowDataset();
            JFreeChart chart = ChartFactory.createHighLowChart(title, domain, range,
                                                               data, true);

            // then customise it a little...
            TextTitle subtitle = new TextTitle(subtitleStr, new Font("SansSerif", Font.BOLD, 12));
            chart.addTitle(subtitle);
            chart.setBackgroundPaint(new GradientPaint(0, 0, Color.white, 0, 1000, Color.magenta));


            // and present it in a frame...
            highLowChartFrame = new ChartFrame(title, chart);
            highLowChartFrame.pack();
            RefineryUtilities.positionFrameRandomly(highLowChartFrame);
            highLowChartFrame.show();

        }
        else {
            highLowChartFrame.show();
            highLowChartFrame.requestFocus();
        }

    }

    /**
     * Displays a candlestick chart in its own frame.
     */
    private void displayCandlestickChart() {

        if (this.candlestickChartFrame==null) {

            // create a default chart based on some sample data...
            String title = resources.getString("timeseries.candlestick.title");
            String domain = resources.getString("timeseries.candlestick.domain");
            String range = resources.getString("timeseries.candlestick.range");
            String subtitleStr = resources.getString("timeseries.candlestick.subtitle");
            HighLowDataset data = DemoDatasetFactory.createSampleHighLowDataset();
            JFreeChart chart = ChartFactory.createCandlestickChart(title, domain, range, data,
                                                                   false);

            chart.getPlot().setSeriesPaint(new Paint[] { Color.blue });

            // then customise it a little...
            TextTitle subtitle = new TextTitle(subtitleStr, new Font("SansSerif", Font.BOLD, 12));
            chart.addTitle(subtitle);
            chart.setBackgroundPaint(new GradientPaint(0, 0, Color.white, 0, 1000, Color.green));


            // and present it in a frame...
            candlestickChartFrame = new ChartFrame(title, chart);
            candlestickChartFrame.pack();
            RefineryUtilities.positionFrameRandomly(candlestickChartFrame);
            candlestickChartFrame.setVisible(true);

        }
        else {
            candlestickChartFrame.setVisible(true);
            candlestickChartFrame.requestFocus();
        }

    }

    /**
     * Displays a signal chart in its own frame.
     */
    private void displaySignalChart() {

        if (this.signalChartFrame==null) {

            // create a default chart based on some sample data...
            String title = resources.getString("timeseries.signal.title");
            String domain = resources.getString("timeseries.signal.domain");
            String range = resources.getString("timeseries.signal.range");
            String subtitleStr = resources.getString("timeseries.signal.subtitle");
            SignalsDataset data = DemoDatasetFactory.createSampleSignalDataset();
            JFreeChart chart = ChartFactory.createSignalChart(title, domain, range,
                                                              data, true);

            // then customise it a little...
            TextTitle subtitle = new TextTitle(subtitleStr, new Font("SansSerif", Font.BOLD, 12));
            chart.addTitle(subtitle);
            chart.setBackgroundPaint(new GradientPaint(0, 0, Color.white, 0, 1000, Color.blue));

            // and present it in a frame...
            signalChartFrame = new ChartFrame(title, chart);
            signalChartFrame.pack();
            RefineryUtilities.positionFrameRandomly(signalChartFrame);
            signalChartFrame.setVisible(true);

        }
        else {
            signalChartFrame.setVisible(true);
            signalChartFrame.requestFocus();
        }

    }

    /**
     * Displays a wind plot in its own frame.
     */
    private void displayWindPlot() {

        if (windPlotFrame==null) {

            // create a default chart based on some sample data...
            String title = resources.getString("other.wind.title");
            String domain = resources.getString("other.wind.domain");
            String range = resources.getString("other.wind.range");
            WindDataset data = DemoDatasetFactory.createWindDataset1();
            JFreeChart chart = ChartFactory.createWindPlot(title, domain, range,
                                                           data, true);

            // then customise it a little...
            chart.setBackgroundPaint(new GradientPaint(0, 0, Color.white, 1000, 0, Color.green));

            // and present it in a frame...
            windPlotFrame = new ChartFrame(title, chart);
            windPlotFrame.pack();
            RefineryUtilities.positionFrameRandomly(windPlotFrame);
            windPlotFrame.show();

        }
        else {
            windPlotFrame.show();
            windPlotFrame.requestFocus();
        }

    }

    /**
     * Displays a scatter plot in its own frame.
     */
    private void displayScatterPlot() {

        if (scatterPlotFrame==null) {

            // create a default chart based on some sample data...
            String title = resources.getString("other.scatter.title");
            String domain = resources.getString("other.scatter.domain");
            String range = resources.getString("other.scatter.range");
            XYDataset data = new SampleXYDataset2();
            JFreeChart chart = ChartFactory.createScatterPlot(title, domain, range, data, true);

            // then customise it a little...
            chart.setBackgroundPaint(new GradientPaint(0, 0, Color.white, 1000, 0, Color.green));

            XYPlot plot = (XYPlot)chart.getPlot();
            NumberAxis axis = (NumberAxis)plot.getRangeAxis();
            axis.setAutoRangeIncludesZero(false);

            // and present it in a frame...
            scatterPlotFrame = new ChartFrame(title, chart);
            scatterPlotFrame.pack();
            RefineryUtilities.positionFrameRandomly(scatterPlotFrame);
            scatterPlotFrame.show();

        }
        else {
            scatterPlotFrame.show();
            scatterPlotFrame.requestFocus();
        }

    }

    /**
     * Displays a line chart in its own frame.
     */
    private void displayLineChart() {

        if (lineChartFrame==null) {

            // create a default chart based on some sample data...
            String title = resources.getString("other.line.title");
            String domain = resources.getString("other.line.domain");
            String range = resources.getString("other.line.range");
            CategoryDataset data = DemoDatasetFactory.createCategoryDataset();
            JFreeChart chart = ChartFactory.createLineChart(title, domain, range, data, true);

            // then customise it a little...
            ImageIcon icon = new javax.swing.ImageIcon(JFreeChartDemo.class.getResource("gorilla.jpg"));
            chart.setBackgroundImage(icon.getImage());
            chart.setBackgroundPaint(new GradientPaint(0, 0, Color.white, 0, 1000, Color.green));

            CategoryPlot plot = (CategoryPlot)chart.getPlot();
            plot.setBackgroundAlpha(0.65f);
            HorizontalCategoryAxis axis = (HorizontalCategoryAxis)plot.getDomainAxis();
            axis.setVerticalCategoryLabels(true);

            // and present it in a frame...
            lineChartFrame = new ChartFrame(title, chart);
            lineChartFrame.pack();
            RefineryUtilities.positionFrameRandomly(lineChartFrame);
            lineChartFrame.show();

        }
        else {
            lineChartFrame.show();
            lineChartFrame.requestFocus();
        }

    }

    /**
     * Displays a vertical bar chart in its own frame.
     */
    private void displayVerticalXYBarChart() {

        if (verticalXYBarChartFrame==null) {

            // create a default chart based on some sample data...
            String title = resources.getString("other.xybar.title");
            String domain = resources.getString("other.xybar.domain");
            String range = resources.getString("other.xybar.range");
            IntervalXYDataset data = DemoDatasetFactory.createTimeSeriesCollection1();
            JFreeChart chart = ChartFactory.createVerticalXYBarChart(title, domain, range,
                                                                     data, true);

            // then customise it a little...
            chart.setBackgroundPaint(new GradientPaint(0, 0, Color.white, 1000, 0, Color.blue));

            XYItemRenderer renderer = chart.getXYPlot().getItemRenderer();
            renderer.setToolTipGenerator(new TimeSeriesToolTipGenerator());

            // and present it in a panel...
            verticalXYBarChartFrame = new ChartFrame(title, chart);
            verticalXYBarChartFrame.pack();
            RefineryUtilities.positionFrameRandomly(verticalXYBarChartFrame);
            verticalXYBarChartFrame.show();

        }
        else {
            verticalXYBarChartFrame.show();
            verticalXYBarChartFrame.requestFocus();
        }

    }

    /**
     * Displays a vertical bar chart in its own frame.
     */
    private void displayNullXYPlot() {

        if (this.xyPlotNullDataFrame==null) {

            // create a default chart based on some sample data...
            String title = resources.getString("test.null.title");
            String domain = resources.getString("test.null.domain");
            String range = resources.getString("test.null.range");
            XYDataset data = null;
            JFreeChart chart = ChartFactory.createXYChart(title, domain, range, data, true);

            // then customise it a little...
            chart.setBackgroundPaint(new GradientPaint(0, 0, Color.white, 1000, 0, Color.red));

            // and present it in a panel...
            xyPlotNullDataFrame = new ChartFrame(title, chart);
            xyPlotNullDataFrame.pack();
            RefineryUtilities.positionFrameRandomly(xyPlotNullDataFrame);
            xyPlotNullDataFrame.show();

        }
        else {
            xyPlotNullDataFrame.show();
            xyPlotNullDataFrame.requestFocus();
        }

    }

    /**
     * Displays an XYPlot in its own frame.
     */
    private void displayXYPlotZeroData() {

        if (xyPlotZeroDataFrame==null) {

            // create a default chart based on some sample data...
            String title = resources.getString("test.zero.title");
            String domain = resources.getString("test.zero.domain");
            String range = resources.getString("test.zero.range");
            XYDataset data = new EmptyXYDataset();
            JFreeChart chart = ChartFactory.createXYChart(title, domain, range, data, true);

            // then customise it a little...
            chart.setBackgroundPaint(new GradientPaint(0, 0, Color.white, 1000, 0, Color.red));

            // and present it in a frame...
            xyPlotZeroDataFrame = new ChartFrame(title, chart);
            xyPlotZeroDataFrame.pack();
            RefineryUtilities.positionFrameRandomly(xyPlotZeroDataFrame);
            xyPlotZeroDataFrame.show();

        }
        else {
            xyPlotZeroDataFrame.show();
            xyPlotZeroDataFrame.requestFocus();
        }

    }

    /**
     * Displays a vertical bar chart in its own frame.
     */
    private void displayTimeSeriesChartInScrollPane() {

        if (this.timeSeriesChartScrollFrame==null) {

            // create a default chart based on some sample data...
            String title = resources.getString("test.scroll.title");
            String domain = resources.getString("test.scroll.domain");
            String range = resources.getString("test.scroll.range");
            String subtitleStr = resources.getString("test.scroll.subtitle");
            XYDataset data = DemoDatasetFactory.createTimeSeriesCollection2();
            JFreeChart chart = ChartFactory.createTimeSeriesChart(title, domain, range, data, true);

            // then customise it a little...
            TextTitle subtitle = new TextTitle(subtitleStr, new Font("SansSerif", Font.BOLD, 12));
            chart.addTitle(subtitle);
            chart.setBackgroundPaint(new GradientPaint(0, 0, Color.white,0, 1000, Color.gray));
            Plot plot = chart.getPlot();

            // and present it in a frame...
            timeSeriesChartScrollFrame = new ChartFrame(title, chart, true);
            timeSeriesChartScrollFrame.pack();
            RefineryUtilities.positionFrameRandomly(timeSeriesChartScrollFrame);
            timeSeriesChartScrollFrame.show();

        }
        else {
            timeSeriesChartScrollFrame.show();
            timeSeriesChartScrollFrame.requestFocus();
        }

    }

    /**
     * Displays a horizontal bar chart in its own frame.
     */
    private void displaySingleSeriesBarChart() {

        if (this.singleSeriesBarChartFrame==null) {

            // create a default chart based on some sample data...
            String title = resources.getString("test.single.title");
            String domain = resources.getString("test.single.domain");
            String range = resources.getString("test.single.range");
            String subtitle1Str = resources.getString("test.single.subtitle1");
            String subtitle2Str = resources.getString("test.single.subtitle2");

            CategoryDataset data = DemoDatasetFactory.createSingleSeriesCategoryDataset();

            JFreeChart chart = ChartFactory.createHorizontalBarChart(title, domain, range,
                                                                     data, true);

            chart.addTitle(new TextTitle(subtitle1Str));
            chart.addTitle(new TextTitle(subtitle2Str));
            // then customise it a little...
            chart.setBackgroundPaint(new GradientPaint(0, 0, Color.white, 1000, 0, Color.red));

            // and present it in a panel...
            singleSeriesBarChartFrame = new ChartFrame(title, chart);
            ChartPanel panel = singleSeriesBarChartFrame.getChartPanel();
            panel.setBorder(BorderFactory.createCompoundBorder(
                                BorderFactory.createEmptyBorder(3, 3, 3, 3),
                                BorderFactory.createCompoundBorder(
                                    BorderFactory.createEtchedBorder(),
                                    BorderFactory.createEmptyBorder(2, 2, 2, 2))));
            singleSeriesBarChartFrame.pack();
            RefineryUtilities.positionFrameRandomly(singleSeriesBarChartFrame);
            singleSeriesBarChartFrame.setVisible(true);

        }
        else {
            singleSeriesBarChartFrame.show();
            xyPlotNullDataFrame.requestFocus();
        }

    }

    /**
     * Displays an XY chart that is periodically updated by a background thread.  This is to
     * demonstrate the event notification system that automatically updates charts as required.
     */
    private void displayDynamicXYChart() {

        if (dynamicXYChartFrame==null) {

            String title = resources.getString("test.dynamic.title");
            String domain = resources.getString("test.dynamic.domain");
            String range = resources.getString("test.dynamic.range");

            SampleXYDataset data = new SampleXYDataset();
            JFreeChart chart = ChartFactory.createXYChart(title, domain, range, data, true);
            SampleXYDatasetThread update = new SampleXYDatasetThread(data);
            dynamicXYChartFrame = new ChartFrame(title, chart);
            dynamicXYChartFrame.pack();
            RefineryUtilities.positionFrameRandomly(dynamicXYChartFrame);
            dynamicXYChartFrame.show();
            Thread thread = new Thread(update);
            thread.start();
        }

    }

    /**
     * Displays a combined and overlaid plot in its own frame.
     */
    private void displayOverlaidChart() {

        if (this.overlaidChartFrame==null) {

            // create a default chart based on some sample data...
            String title = this.resources.getString("combined.overlaid.title");
            String subtitleStr = this.resources.getString("combined.overlaid.subtitle");
            String domain = this.resources.getString("combined.overlaid.domain");
            String range = this.resources.getString("combined.overlaid.range");

            // create high-low and moving average dataset
            HighLowDataset highLowData = DemoDatasetFactory.createSampleHighLowDataset();
            MovingAveragePlotFitAlgorithm mavg = new MovingAveragePlotFitAlgorithm();
            mavg.setPeriod(5);
            PlotFit pf = new PlotFit(highLowData, mavg);
            XYDataset maData = pf.getFit();

            // make an overlaid CombinedPlot
            OverlaidXYPlot overlaidPlot = new OverlaidXYPlot("Date", "Price");
            overlaidPlot.setDomainAxis(new HorizontalDateAxis("Date"));

            // create and add subplot 1...
            XYItemRenderer renderer1 = new HighLowRenderer(new HighLowToolTipGenerator());
            XYPlot subplot1 = new XYPlot(highLowData, null, null, renderer1);
            overlaidPlot.add(subplot1);

            XYDataset data2 = new SubSeriesDataset(maData, 1); // MA data
            XYPlot subplot2 = new XYPlot(data2, null, null);
            subplot2.getItemRenderer().setToolTipGenerator(new TimeSeriesToolTipGenerator("d-MMM-yyyy", "0.00"));
            overlaidPlot.add(subplot2);

            // make the top level JFreeChart object
            JFreeChart chart = new JFreeChart(title, JFreeChart.DEFAULT_TITLE_FONT,
                                              overlaidPlot, true);

            // then customise it a little...
            TextTitle subtitle = new TextTitle(subtitleStr, new Font("SansSerif", Font.BOLD, 12));
            chart.addTitle(subtitle);
            chart.setBackgroundPaint(new GradientPaint(0, 0, Color.white, 0, 1000, Color.blue));

            // and present it in a frame...
            overlaidChartFrame = new ChartFrame(title, chart);
            overlaidChartFrame.pack();
            RefineryUtilities.positionFrameRandomly(overlaidChartFrame);
            overlaidChartFrame.show();

        }
        else {
            overlaidChartFrame.show();
            overlaidChartFrame.requestFocus();
        }

    }

    /**
     * Displays a horizontally combined plot in its own frame.
     */
    private void displayHorizontallyCombinedChart() {

        if (this.horizontallyCombinedChartFrame==null) {

            // create a default chart based on some sample data...
            String title = this.resources.getString("combined.horizontal.title");
            String subtitleStr = this.resources.getString("combined.horizontal.subtitle");
            String[] domains = this.resources.getStringArray("combined.horizontal.domains");
            String range = this.resources.getString("combined.horizontal.range");

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
            XYDataset series0 = new SubSeriesDataset(data, 0);
            XYDataset series1 = new SubSeriesDataset(data, 1);

            // this code could probably go later in the ChartFactory class

            JFreeChart chart = null;

            // make a horizontal axis for each sub-plot
            //ValueAxis[] timeAxis = new HorizontalDateAxis[3];
            //for (int i=0; i<timeAxis.length; i++) {
            //    timeAxis[i] = new HorizontalDateAxis(domains[i]);
            //    timeAxis[i].setCrosshairVisible(false);
            //}

            // make a common vertical axis for all the sub-plots
            NumberAxis valueAxis = new VerticalNumberAxis(range);
            valueAxis.setAutoRangeIncludesZero(false);  // override default
            valueAxis.setCrosshairVisible(false);

            // make a horizontally combined plot
            CombinedXYPlot multiPlot = new CombinedXYPlot(valueAxis, CombinedXYPlot.HORIZONTAL);

            int[] weight = { 1, 1, 1 }; // control horizontal space assigned to each subplot

            // add subplot 1...
            XYPlot subplot1 = new XYPlot(series0, new HorizontalDateAxis("Date"), null);
            multiPlot.add(subplot1, weight[0]);

            // add subplot 2...
            XYPlot subplot2 = new XYPlot(data, new HorizontalDateAxis("Date"), null);
            multiPlot.add(subplot2, weight[1]);

            // add subplot 3...
            XYPlot subplot3 = new XYPlot(series0, new HorizontalDateAxis("Date"), null, new VerticalXYBarRenderer(0.20));
            //chartToCombine = ChartFactory.createCombinableVerticalXYBarChart(timeAxis[2], valueAxis, series0);
            multiPlot.add(subplot3, weight[2]);

            // call this method after all sub-plots have been added
            //combinedPlot.adjustPlots();

            // now make tht top level JFreeChart
            chart = new JFreeChart(title, JFreeChart.DEFAULT_TITLE_FONT, multiPlot, true);

            // then customise it a little...
            TextTitle subtitle = new TextTitle(subtitleStr, new Font("SansSerif", Font.BOLD, 12));
            chart.addTitle(subtitle);
            chart.setBackgroundPaint(new GradientPaint(0, 0, Color.white,0, 1000, Color.blue));

            // and present it in a frame...
            horizontallyCombinedChartFrame = new ChartFrame(title, chart);
            horizontallyCombinedChartFrame.pack();
            RefineryUtilities.positionFrameRandomly(horizontallyCombinedChartFrame);
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
            String title = this.resources.getString("combined.vertical.title");
            String subtitleStr = this.resources.getString("combined.vertical.subtitle");
            String domain = this.resources.getString("combined.vertical.domain");
            String[] ranges = this.resources.getStringArray("combined.vertical.ranges");


            // calculate Time Series and Moving Average Dataset
            MovingAveragePlotFitAlgorithm mavg = new MovingAveragePlotFitAlgorithm();
            mavg.setPeriod(30);
            PlotFit pf = new PlotFit(DemoDatasetFactory.createTimeSeriesCollection2(), mavg);
            XYDataset tempDataset = pf.getFit();

            // create master dataset
            CombinedDataset data = new CombinedDataset();
            data.add(tempDataset);                // time series + MA
            data.add(DemoDatasetFactory.createSampleHighLowDataset()); // high-low data

            // test SubSeriesDataset and CombinedDataset operations

            // decompose data into its two dataset series
            SeriesDataset series0 = new SubSeriesDataset(data, 0);
            SeriesDataset series1 = new SubSeriesDataset(data, 1);
            SeriesDataset series2 = new SubSeriesDataset(data, 2);

            // compose datasets for each sub-plot
            CombinedDataset data0 = new CombinedDataset(new SeriesDataset[] {series0} );
            CombinedDataset data1 = new CombinedDataset(new SeriesDataset[] {series0, series1} );
            CombinedDataset data2 = new CombinedDataset(new SeriesDataset[] {series2} );

            // make one shared horizontal axis
            ValueAxis timeAxis = new HorizontalDateAxis(domain);
            timeAxis.setCrosshairVisible(false);

            // make a vertically CombinedPlot that will contain the sub-plots
            CombinedXYPlot multiPlot = new CombinedXYPlot(timeAxis, CombinedXYPlot.VERTICAL);

            int[] weight = { 1, 1, 1, 1 }; // control vertical space allocated to each sub-plot

            // add subplot1...
            XYPlot subplot1 = new XYPlot(data0, null, new VerticalNumberAxis("Value"));
            NumberAxis range1 = (NumberAxis)subplot1.getRangeAxis();
            range1.setAutoRangeIncludesZero(false);
            multiPlot.add(subplot1, weight[0]);

            // add subplot2...
            XYPlot subplot2 = new XYPlot(data1, null, new VerticalNumberAxis("Value"));
            NumberAxis range2 = (NumberAxis)subplot2.getRangeAxis();
            range2.setAutoRangeIncludesZero(false);
            multiPlot.add(subplot2, weight[1]);

            // add subplot3...
            XYPlot subplot3 = new XYPlot(data2, null, new VerticalNumberAxis("Value"));
            XYItemRenderer renderer3 = new HighLowRenderer();
            subplot3.setXYItemRenderer(renderer3);
            NumberAxis range3 = (NumberAxis)subplot3.getRangeAxis();
            range3.setAutoRangeIncludesZero(false);
            multiPlot.add(subplot3, weight[2]);

            // add subplot4...
            XYPlot subplot4 = new XYPlot(data0, null, new VerticalNumberAxis("Value"));
            XYItemRenderer renderer4 = new VerticalXYBarRenderer();
            subplot4.setXYItemRenderer(renderer4);
            NumberAxis range4 = (NumberAxis)subplot4.getRangeAxis();
            range4.setAutoRangeIncludesZero(false);
            multiPlot.add(subplot4, weight[3]);

            // now make the top level JFreeChart that contains the CombinedPlot
            JFreeChart chart = new JFreeChart(title, JFreeChart.DEFAULT_TITLE_FONT, multiPlot, true);

            // then customise it a little...
            TextTitle subtitle = new TextTitle(subtitleStr, new Font("SansSerif", Font.BOLD, 12));
            chart.addTitle(subtitle);
            chart.setBackgroundPaint(new GradientPaint(0, 0, Color.white,0, 1000, Color.blue));

            // and present it in a frame...
            verticallyCombinedChartFrame = new ChartFrame(title, chart);
            verticallyCombinedChartFrame.pack();
            RefineryUtilities.positionFrameRandomly(verticallyCombinedChartFrame);
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
            String title = this.resources.getString("combined.combined-overlaid.title");
            String subtitleStr = this.resources.getString("combined.combined-overlaid.subtitle");
            String domain = this.resources.getString("combined.combined-overlaid.domain");
            String[] ranges = this.resources.getStringArray("combined.combined-overlaid.ranges");


            HighLowDataset highLowData = DemoDatasetFactory.createSampleHighLowDataset();
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
            XYDataset series0 = new SubSeriesDataset(data, 0); // time series
            XYDataset series1 = new SubSeriesDataset(data, 1); // time series MA
            XYDataset series2 = new SubSeriesDataset(data, 2); // high-low series
            XYDataset series3 = new SubSeriesDataset(data, 3); // high-low MA

            // compose datasets for each sub-plot
            CombinedDataset data0 = new CombinedDataset(new SeriesDataset[] {series0} );
            CombinedDataset data1 = new CombinedDataset(new SeriesDataset[] {series0, series1} );
            CombinedDataset data2 = new CombinedDataset(new SeriesDataset[] {series2, series3} );

            // this code could probably go later in the ChartFactory class

            //chart = null;
            int n = 3;    // number of combined (vertically laidout) charts

            // common time axis
            //ValueAxis timeAxis = ;
           // timeAxis.setCrosshairVisible(false);

            // make one vertical axis for each (vertical) chart
            NumberAxis[] valueAxis = new NumberAxis[3];
            for (int i=0; i<valueAxis.length; i++) {
                valueAxis[i] = new VerticalNumberAxis(ranges[i]);
                valueAxis[i].setCrosshairVisible(false);
                if (i <= 1) {
                    valueAxis[i].setAutoRangeIncludesZero(false);  // override default
                }
            }

            // create CombinedPlot...
            CombinedXYPlot multiPlot = new CombinedXYPlot(new HorizontalDateAxis(domain),
                                                          CombinedXYPlot.VERTICAL);

            int[] weight = { 1, 2, 2 };

            // add subplot1...
            XYPlot subplot1 = new XYPlot(data0, null, new VerticalNumberAxis(ranges[0]));
            NumberAxis axis1 = (NumberAxis)subplot1.getRangeAxis();
            axis1.setTickLabelFont(new Font("Monospaced", Font.PLAIN, 7));
            axis1.setLabelFont(new Font("SansSerif", Font.PLAIN, 8));
            axis1.setAutoRangeIncludesZero(false);
            multiPlot.add(subplot1, weight[0]);

            // add subplot2 (an overlaid plot)...
            OverlaidXYPlot subplot2 = new OverlaidXYPlot(null, new VerticalNumberAxis(ranges[1]));
            NumberAxis axis2 = (NumberAxis)subplot2.getRangeAxis();
            axis2.setTickLabelFont(new Font("Monospaced", Font.PLAIN, 7));
            axis2.setLabelFont(new Font("SansSerif", Font.PLAIN, 8));
            axis2.setAutoRangeIncludesZero(false);
            XYPlot p1 = new XYPlot(series0, null, null);
            subplot2.add(p1);
            XYPlot p2 = new XYPlot(series1, null, null);
            subplot2.add(p2);

            multiPlot.add(subplot2, weight[1]);

            // add subplot3 (an overlaid plot)...
            OverlaidXYPlot subplot3 = new OverlaidXYPlot(null, new VerticalNumberAxis(ranges[2]));
            NumberAxis axis3 = (NumberAxis)subplot3.getRangeAxis();
            axis3.setTickLabelFont(new Font("Monospaced", Font.PLAIN, 7));
            axis3.setLabelFont(new Font("SansSerif", Font.PLAIN, 8));
            axis3.setAutoRangeIncludesZero(false);

            XYItemRenderer renderer3 = new HighLowRenderer();
            XYPlot p3 = new XYPlot(series2, null, null, renderer3);
            subplot3.add(p3);
            XYPlot p4 = new XYPlot(series3, null, null);
            subplot3.add(p4);

            multiPlot.add(subplot3, weight[2]);

            // now create the master JFreeChart object
            JFreeChart chart = new JFreeChart(title, new Font("SansSerif", Font.BOLD, 12), multiPlot, true);

            // then customise it a little...
            TextTitle subtitle = new TextTitle(subtitleStr, new Font("SansSerif", Font.BOLD, 10));
            chart.addTitle(subtitle);
            chart.setBackgroundPaint(new GradientPaint(0, 0, Color.white,0, 1000, Color.blue));

            // and present it in a frame...
            combinedOverlaidChartFrame1 = new ChartFrame(title, chart);
            combinedOverlaidChartFrame1.pack();
            RefineryUtilities.positionFrameRandomly(combinedOverlaidChartFrame1);
            combinedOverlaidChartFrame1.show();

        }
        else {
            combinedOverlaidChartFrame1.show();
            combinedOverlaidChartFrame1.requestFocus();
        }

    }


    /**
     * Displays an XY chart that is periodically updated by a background thread.  This is to
     * demonstrate the event notification system that automatically updates charts as required.
     */
    private void displayCombinedAndOverlaidDynamicXYChart() {

        if (combinedAndOverlaidDynamicXYChartFrame==null) {

            // chart title and axis labels...
            String title = this.resources.getString("combined.dynamic.title");
            String subtitleStr = this.resources.getString("combined.dynamic.subtitle");
            String domain = this.resources.getString("combined.dynamic.domain");
            String[] ranges = this.resources.getStringArray("combined.dynamic.ranges");

            // setup sample base 2-series dataset
            SampleXYDataset data = new SampleXYDataset();

            // create some SubSeriesDatasets and CombinedDatasets to test events
            XYDataset series0 = new SubSeriesDataset(data, 0);
            XYDataset series1 = new SubSeriesDataset(data, 1);

            CombinedDataset combinedData = new CombinedDataset();
            combinedData.add(series0);
            combinedData.add(series1);

            // create common time axis
            NumberAxis timeAxis = new HorizontalNumberAxis(domain);
            timeAxis.setTickMarksVisible(true);
            timeAxis.setAutoRangeIncludesZero(false);
            timeAxis.setCrosshairVisible(false);

            // make one vertical axis for each (vertical) chart
            NumberAxis[] valueAxis = new NumberAxis[4];
            for (int i=0; i<valueAxis.length; i++) {
                valueAxis[i] = new VerticalNumberAxis(ranges[i]);
                valueAxis[i].setAutoRangeIncludesZero(false);
                valueAxis[i].setCrosshairVisible(false);
            }

            CombinedXYPlot plot = new CombinedXYPlot(timeAxis, CombinedXYPlot.VERTICAL);

            // add subplot1...
            XYPlot subplot0 = new XYPlot(series0, null, valueAxis[0]);
            plot.add(subplot0, 1);

            // add subplot2...
            XYPlot subplot1 = new XYPlot(series1, null, valueAxis[1]);
            plot.add(subplot1, 1);

            // add subplot3...
            OverlaidXYPlot subplot2 = new OverlaidXYPlot(null, valueAxis[2]);

            // add two overlaid XY charts (share both axes)
            XYPlot p1 = new XYPlot(series0, null, null);
            subplot2.add(p1);
            XYPlot p2 = new XYPlot(series1, null, null);
            subplot2.add(p2);
            plot.add(subplot2, 1);

            // add subplot4...
            XYPlot subplot3 = new XYPlot(data, null, valueAxis[3]);
            plot.add(subplot3, 1);
            //combinedPlot.adjustPlots();

            JFreeChart chart = new JFreeChart(title, JFreeChart.DEFAULT_TITLE_FONT, plot, true);

            // display the combined and overlaid dynamic charts
            combinedAndOverlaidDynamicXYChartFrame = new ChartFrame(title, chart);

            // then customise it a little...
            TextTitle subtitle = new TextTitle(subtitleStr, new Font("SansSerif", Font.BOLD, 12));
            chart.addTitle(subtitle);
            chart.setBackgroundPaint(new GradientPaint(0, 0, Color.white, 0, 1000, Color.cyan));

            // show the frame
            combinedAndOverlaidDynamicXYChartFrame.pack();
            RefineryUtilities.positionFrameRandomly(combinedAndOverlaidDynamicXYChartFrame);
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

        String title = this.resources.getString("about.title");
        String versionLabel = this.resources.getString("about.version.label");
        if (aboutFrame==null) {
            aboutFrame = new AboutFrame(title,
                                        JFreeChart.NAME,
                                        versionLabel+" "+JFreeChart.VERSION,
                                        JFreeChart.INFO,
                                        JFreeChart.COPYRIGHT,
                                        JFreeChart.LICENCE,
                                        JFreeChart.CONTRIBUTORS,
                                        JFreeChart.LIBRARIES);
            aboutFrame.pack();
            RefineryUtilities.centerFrameOnScreen(aboutFrame);
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
        RefineryUtilities.centerFrameOnScreen(f);
        f.setVisible(true);
    }

    /**
     * Required for WindowListener interface, but not used by this class.
     */
    public void windowActivated(WindowEvent e) {}

    /**
     * Clears the reference to the print preview frames when they are closed.
     */
    public void windowClosed(WindowEvent e) {

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

    /**
     * Creates a menubar.
     */
    private JMenuBar createMenuBar(ResourceBundle resources) {

        // create the menus
        JMenuBar menuBar = new JMenuBar();

        String label;
        Character mnemonic;

        // first the file menu
        label = resources.getString("menu.file");
        mnemonic = (Character)resources.getObject("menu.file.mnemonic");
        JMenu fileMenu = new JMenu(label, true);
        fileMenu.setMnemonic(mnemonic.charValue());

        label = resources.getString("menu.file.exit");
        mnemonic = (Character)resources.getObject("menu.file.exit.mnemonic");
        JMenuItem exitItem = new JMenuItem(label, mnemonic.charValue());
        exitItem.setActionCommand(EXIT_COMMAND);
        exitItem.addActionListener(this);
        fileMenu.add(exitItem);

        // then the help menu
        label = resources.getString("menu.help");
        mnemonic = (Character)resources.getObject("menu.help.mnemonic");
        JMenu helpMenu = new JMenu(label);
        helpMenu.setMnemonic(mnemonic.charValue());

        label = resources.getString("menu.help.about");
        mnemonic = (Character)resources.getObject("menu.help.about.mnemonic");
        JMenuItem aboutItem = new JMenuItem(label, mnemonic.charValue());
        aboutItem.setActionCommand(ABOUT_COMMAND);
        aboutItem.addActionListener(this);
        helpMenu.add(aboutItem);

        // finally, glue together the menu and return it
        menuBar.add(fileMenu);
        menuBar.add(helpMenu);

        return menuBar;

    }

    /**
     * Creates a tabbed pane containing descriptions of the demo charts.
     */
    private JTabbedPane createTabbedPane(ResourceBundle resources) {

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

        String title;
        String description;
        String buttonText = resources.getString("charts.display");

        // DEMO CHART 1...
        title = resources.getString("chart1.title");
        description = resources.getString("chart1.description");
        barPanel.add(RefineryUtilities.createJLabel(title, font));
        barPanel.add(new DescriptionPanel(new JTextArea(description)));
        JButton b1 = RefineryUtilities.createJButton(buttonText, font);
        b1.setActionCommand(CHART_1_COMMAND);
        b1.addActionListener(this);
        barPanel.add(b1);

        // DEMO CHART 2...
        title = resources.getString("chart2.title");
        description = resources.getString("chart2.description");
        barPanel.add(RefineryUtilities.createJLabel(title, font));
        barPanel.add(new DescriptionPanel(new JTextArea(description)));
        JButton b2 = RefineryUtilities.createJButton(buttonText, font);
        b2.setActionCommand(CHART_2_COMMAND);
        b2.addActionListener(this);
        barPanel.add(b2);

        // DEMO CHART 3...
        title = resources.getString("chart3.title");
        description = resources.getString("chart3.description");
        barPanel.add(RefineryUtilities.createJLabel(title, font));
        barPanel.add(new DescriptionPanel(new JTextArea(description)));
        JButton b3 = RefineryUtilities.createJButton(buttonText, font);
        b3.setActionCommand(CHART_3_COMMAND);
        b3.addActionListener(this);
        barPanel.add(b3);

        // DEMO CHART 4...
        title = resources.getString("chart4.title");
        description = resources.getString("chart4.description");
        barPanel.add(RefineryUtilities.createJLabel(title, font));
        barPanel.add(new DescriptionPanel(new JTextArea(description)));
        JButton b4 = RefineryUtilities.createJButton(buttonText, font);
        b4.setActionCommand(CHART_4_COMMAND);
        b4.addActionListener(this);
        barPanel.add(b4);

        // DEMO CHART 5...
        title = resources.getString("chart5.title");
        description = resources.getString("chart5.description");
        barPanel.add(RefineryUtilities.createJLabel(title, font));
        barPanel.add(new DescriptionPanel(new JTextArea(description)));
        JButton b5 = RefineryUtilities.createJButton(buttonText, font);
        b5.setActionCommand(CHART_5_COMMAND);
        b5.addActionListener(this);
        barPanel.add(b5);

        // DEMO CHART 6...
        title = resources.getString("chart6.title");
        description = resources.getString("chart6.description");
        barPanel.add(RefineryUtilities.createJLabel(title, font));
        barPanel.add(new DescriptionPanel(new JTextArea(description)));
        JButton b6 = RefineryUtilities.createJButton(buttonText, font);
        b6.setActionCommand(CHART_6_COMMAND);
        b6.addActionListener(this);
        barPanel.add(b6);

        // DEMO CHART 7...
        title = resources.getString("chart7.title");
        description = resources.getString("chart7.description");
        piePanel.add(RefineryUtilities.createJLabel(title, font));
        piePanel.add(new DescriptionPanel(new JTextArea(description)));
        JButton b7 = RefineryUtilities.createJButton(buttonText, font);
        b7.setActionCommand(CHART_7_COMMAND);
        b7.addActionListener(this);
        piePanel.add(b7);

        // DEMO CHART 8...
        title = resources.getString("chart8.title");
        description = resources.getString("chart8.description");
        piePanel.add(RefineryUtilities.createJLabel(title, font));
        piePanel.add(new DescriptionPanel(new JTextArea(description)));
        JButton b8 = RefineryUtilities.createJButton(buttonText, font);
        b8.setActionCommand(CHART_8_COMMAND);
        b8.addActionListener(this);
        piePanel.add(b8);

        // DEMO CHART 9...
        title = resources.getString("chart9.title");
        description = resources.getString("chart9.description");
        xyPanel.add(RefineryUtilities.createJLabel(title, font));
        xyPanel.add(new DescriptionPanel(new JTextArea(description)));
        JButton b9 = RefineryUtilities.createJButton(buttonText, font);
        b9.setActionCommand(CHART_9_COMMAND);
        b9.addActionListener(this);
        xyPanel.add(b9);

        // DEMO CHART 10...
        title = resources.getString("chart10.title");
        description = resources.getString("chart10.description");
        timeSeriesPanel.add(RefineryUtilities.createJLabel(title, font));
        timeSeriesPanel.add(new DescriptionPanel(new JTextArea(description)));
        JButton b10 = RefineryUtilities.createJButton(buttonText, font);
        b10.setActionCommand(CHART_10_COMMAND);
        b10.addActionListener(this);
        timeSeriesPanel.add(b10);

        // DEMO CHART 11...
        title = resources.getString("chart11.title");
        description = resources.getString("chart11.description");
        timeSeriesPanel.add(RefineryUtilities.createJLabel(title, font));
        timeSeriesPanel.add(new DescriptionPanel(new JTextArea(description)));
        JButton b11 = RefineryUtilities.createJButton(buttonText, font);
        b11.setActionCommand(CHART_11_COMMAND);
        b11.addActionListener(this);
        timeSeriesPanel.add(b11);

        // DEMO CHART 12...
        title = resources.getString("chart12.title");
        description = resources.getString("chart12.description");
        timeSeriesPanel.add(RefineryUtilities.createJLabel(title, font));
        timeSeriesPanel.add(new DescriptionPanel(new JTextArea(description)));
        JButton b12 = RefineryUtilities.createJButton(buttonText, font);
        b12.setActionCommand(CHART_12_COMMAND);
        b12.addActionListener(this);
        timeSeriesPanel.add(b12);

        // DEMO CHART 13...
        title = resources.getString("chart13.title");
        description = resources.getString("chart13.description");
        timeSeriesPanel.add(RefineryUtilities.createJLabel(title, font));
        timeSeriesPanel.add(new DescriptionPanel(new JTextArea(description)));
        JButton b13 = RefineryUtilities.createJButton(buttonText, font);
        b13.setActionCommand(CHART_13_COMMAND);
        b13.addActionListener(this);
        timeSeriesPanel.add(b13);

        // DEMO CHART 14...
        title = resources.getString("chart14.title");
        description = resources.getString("chart14.description");
        timeSeriesPanel.add(RefineryUtilities.createJLabel(title, font));
        timeSeriesPanel.add(new DescriptionPanel(new JTextArea(description)));
        JButton b14 = RefineryUtilities.createJButton(buttonText, font);
        b14.setActionCommand(CHART_14_COMMAND);
        b14.addActionListener(this);
        timeSeriesPanel.add(b14);

        // DEMO CHART 15...
        title = resources.getString("chart15.title");
        description = resources.getString("chart15.description");
        timeSeriesPanel.add(RefineryUtilities.createJLabel(title, font));
        timeSeriesPanel.add(new DescriptionPanel(new JTextArea(description)));
        JButton b15 = RefineryUtilities.createJButton(buttonText, font);
        b15.setActionCommand(CHART_15_COMMAND);
        b15.addActionListener(this);
        timeSeriesPanel.add(b15);

        // DEMO CHART 16...
        title = resources.getString("chart16.title");
        description = resources.getString("chart16.description");
        otherPanel.add(RefineryUtilities.createJLabel(title, font));
        otherPanel.add(new DescriptionPanel(new JTextArea(description)));
        JButton b16 = RefineryUtilities.createJButton(buttonText, font);
        b16.setActionCommand(CHART_16_COMMAND);
        b16.addActionListener(this);
        otherPanel.add(b16);

        // DEMO CHART 17...
        title = resources.getString("chart17.title");
        description = resources.getString("chart17.description");
        otherPanel.add(RefineryUtilities.createJLabel(title, font));
        otherPanel.add(new DescriptionPanel(new JTextArea(description)));
        JButton b17 = RefineryUtilities.createJButton(buttonText, font);
        b17.setActionCommand(CHART_17_COMMAND);
        b17.addActionListener(this);
        otherPanel.add(b17);

        // DEMO CHART 18...
        title = resources.getString("chart18.title");
        description = resources.getString("chart18.description");
        otherPanel.add(RefineryUtilities.createJLabel(title, font));
        otherPanel.add(new DescriptionPanel(new JTextArea(description)));
        JButton b18 = RefineryUtilities.createJButton(buttonText, font);
        b18.setActionCommand(CHART_18_COMMAND);
        b18.addActionListener(this);
        otherPanel.add(b18);

        // DEMO CHART 19...
        title = resources.getString("chart19.title");
        description = resources.getString("chart19.description");
        otherPanel.add(RefineryUtilities.createJLabel(title, font));
        otherPanel.add(new DescriptionPanel(new JTextArea(description)));
        JButton b19 = RefineryUtilities.createJButton(buttonText, font);
        b19.setActionCommand(CHART_19_COMMAND);
        b19.addActionListener(this);
        otherPanel.add(b19);

        // DEMO CHART 20...
        title = resources.getString("chart20.title");
        description = resources.getString("chart20.description");
        testPanel.add(RefineryUtilities.createJLabel(title, font));
        testPanel.add(new DescriptionPanel(new JTextArea(description)));
        JButton b20 = RefineryUtilities.createJButton(buttonText, font);
        b20.setActionCommand(CHART_20_COMMAND);
        b20.addActionListener(this);
        testPanel.add(b20);

        // DEMO CHART 21...
        title = resources.getString("chart21.title");
        description = resources.getString("chart21.description");
        testPanel.add(RefineryUtilities.createJLabel(title, font));
        testPanel.add(new DescriptionPanel(new JTextArea(description)));
        JButton b21 = RefineryUtilities.createJButton(buttonText, font);
        b21.setActionCommand(CHART_21_COMMAND);
        b21.addActionListener(this);
        testPanel.add(b21);

        // DEMO CHART 22...
        title = resources.getString("chart22.title");
        description = resources.getString("chart22.description");
        testPanel.add(RefineryUtilities.createJLabel(title, font));
        testPanel.add(new DescriptionPanel(new JTextArea(description)));
        JButton b22 = RefineryUtilities.createJButton(buttonText, font);
        b22.setActionCommand(CHART_22_COMMAND);
        b22.addActionListener(this);
        testPanel.add(b22);

        // DEMO CHART 23...
        title = resources.getString("chart23.title");
        description = resources.getString("chart23.description");
        testPanel.add(RefineryUtilities.createJLabel(title, font));
        testPanel.add(new DescriptionPanel(new JTextArea(description)));
        JButton b23 = RefineryUtilities.createJButton(buttonText, font);
        b23.setActionCommand(CHART_23_COMMAND);
        b23.addActionListener(this);
        testPanel.add(b23);

        // DEMO CHART 24...
        title = resources.getString("chart24.title");
        description = resources.getString("chart24.description");
        testPanel.add(RefineryUtilities.createJLabel(title, font));
        testPanel.add(new DescriptionPanel(new JTextArea(description)));
        JButton b24 = RefineryUtilities.createJButton(buttonText, font);
        b24.setActionCommand(CHART_24_COMMAND);
        b24.addActionListener(this);
        testPanel.add(b24);

        // DEMO CHART 25...
        title = resources.getString("chart25.title");
        description = resources.getString("chart25.description");
        combinedPanel.add(RefineryUtilities.createJLabel(title, font));
        combinedPanel.add(new DescriptionPanel(new JTextArea(description)));
        JButton b25 = RefineryUtilities.createJButton(buttonText, font);
        b25.setActionCommand(CHART_25_COMMAND);
        b25.addActionListener(this);
        combinedPanel.add(b25);

        // DEMO CHART 26...
        title = resources.getString("chart26.title");
        description = resources.getString("chart26.description");
        combinedPanel.add(RefineryUtilities.createJLabel(title, font));
        combinedPanel.add(new DescriptionPanel(new JTextArea(description)));
        JButton b26 = RefineryUtilities.createJButton(buttonText, font);
        b26.setActionCommand(CHART_26_COMMAND);
        b26.addActionListener(this);
        combinedPanel.add(b26);

        // DEMO CHART 27...
        title = resources.getString("chart27.title");
        description = resources.getString("chart27.description");
        combinedPanel.add(RefineryUtilities.createJLabel(title, font));
        combinedPanel.add(new DescriptionPanel(new JTextArea(description)));
        JButton b27 = RefineryUtilities.createJButton(buttonText, font);
        b27.setActionCommand(CHART_27_COMMAND);
        b27.addActionListener(this);
        combinedPanel.add(b27);

        // DEMO CHART 28...
        title = resources.getString("chart28.title");
        description = resources.getString("chart28.description");
        combinedPanel.add(RefineryUtilities.createJLabel(title, font));
        combinedPanel.add(new DescriptionPanel(new JTextArea(description)));
        JButton b28 = RefineryUtilities.createJButton(buttonText, font);
        b28.setActionCommand(CHART_28_COMMAND);
        b28.addActionListener(this);
        combinedPanel.add(b28);

        // DEMO CHART 29...
        title = resources.getString("chart29.title");
        description = resources.getString("chart29.description");
        combinedPanel.add(RefineryUtilities.createJLabel(title, font));
        combinedPanel.add(new DescriptionPanel(new JTextArea(description)));
        JButton b29 = RefineryUtilities.createJButton(buttonText, font);
        b29.setActionCommand(CHART_29_COMMAND);
        b29.addActionListener(this);
        combinedPanel.add(b29);

        String title1 = resources.getString("tab.bar");
        String title2 = resources.getString("tab.pie");
        String title3 = resources.getString("tab.xy");
        String title4 = resources.getString("tab.time");
        String title5 = resources.getString("tab.other");
        String title6 = resources.getString("tab.test");
        String title7 = resources.getString("tab.combined");

        tabs.add(title1, new JScrollPane(barPanel));
        tabs.add(title2, new JScrollPane(piePanel));
        tabs.add(title3, new JScrollPane(xyPanel));
        tabs.add(title4, new JScrollPane(timeSeriesPanel));
        tabs.add(title5, new JScrollPane(otherPanel));
        tabs.add(title6, new JScrollPane(testPanel));
        tabs.add(title7, new JScrollPane(combinedPanel));

        return tabs;

    }

}
