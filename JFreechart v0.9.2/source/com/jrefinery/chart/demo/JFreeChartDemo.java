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
 *                   Bryan Scott;
 *
 * $Id: JFreeChartDemo.java,v 1.1 2007/10/10 19:41:58 vauchers Exp $
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
 * 11-Jun-2002 : Changed createHorizontalStackedBarChart() --> createStackedHorizontalBarChart() for
 *               consistency (DG);
 * 25-Jun-2002 : Removed redundant code (DG);
 *
 */

package com.jrefinery.chart.demo;

import com.jrefinery.data.*;
import com.jrefinery.chart.*;
import com.jrefinery.chart.data.PlotFit;
import com.jrefinery.chart.data.MovingAveragePlotFitAlgorithm;
import com.jrefinery.layout.LCBLayout;
import com.jrefinery.ui.RefineryUtilities;
import com.jrefinery.ui.about.AboutFrame;
import com.jrefinery.chart.tooltips.HighLowToolTipGenerator;
import com.jrefinery.chart.tooltips.TimeSeriesToolTipGenerator;
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
import java.lang.reflect.Method;
import java.util.ResourceBundle;
import java.util.Vector;
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

/**
 * The main frame in the chart demonstration application.
 */
public class JFreeChartDemo extends JFrame
                            implements ActionListener, WindowListener {


    /** Exit action command. */
    public static final String EXIT_COMMAND = "EXIT";

    /** About action command. */
    public static final String ABOUT_COMMAND = "ABOUT";

    /**
     * CHART_COMMANDS holds information on charts that can be created
     * Format is
     *   Name, Creation Method, Resource file prefix
     *
     * Steps To add a chart
     * 1) Create a createChart method which returns a JFreeChart
     * 2) Append details to CHART_COMMANDS
     * 3) Append details to DemoResources
     */
    public static final String[][] CHART_COMMANDS = {
        {"HORIZONTAL_BAR_CHART", "createHorizontalBarChart", "chart1"},
        {"HORIZONTAL_STACKED_BAR_CHART", "createStackedHorizontalBarChart", "chart2"},
        {"VERTICAL_BAR_CHART","createVerticalBarChart", "chart3"},
        {"VERTICAL_3D_BAR_CHART","createVertical3DBarChart", "chart4"},
        {"VERTICAL_STACKED_BAR_CHART","createVerticalStackedBarChart", "chart5"},
        {"VERTICAL_STACKED_3D_BAR_CHART","createVerticalStacked3DBarChart", "chart6"},
        {"PIE_CHART_1","createPieChartOne", "chart7"},
        {"PIE_CHART_2","createPieChartTwo", "chart8"},
        {"XY_PLOT","createXYPlot", "chart9"},
        {"TIME_SERIES_1_CHART","createTimeSeries1Chart", "chart10"},
        {"TIME_SERIES_2_CHART","createTimeSeries2Chart", "chart11"},
        {"TIME_SERIES_WITH_MA_CHART","createTimeSeriesWithMAChart", "chart12"},
        {"HIGH_LOW_CHART","createHighLowChart", "chart13"},
        {"CANDLESTICK_CHART","createCandlestickChart", "chart14"},
        {"SIGNAL_CHART","createSignalChart", "chart15"},
        {"WIND_PLOT","createWindPlot", "chart16"},
        {"SCATTER_PLOT","createScatterPlot", "chart17"},
        {"LINE_CHART","createLineChart", "chart18"},
        {"VERTICAL_XY_BAR_CHART","createVerticalXYBarChart", "chart19"},
        {"XY_PLOT_NULL","createNullXYPlot", "chart20"},
        {"XY_PLOT_ZERO","createXYPlotZeroData", "chart21"},
        {"TIME_SERIES_CHART_SCROLL","createTimeSeriesChartInScrollPane", "chart22"},
        {"SINGLE_SERIES_BAR_CHART","createSingleSeriesBarChart", "chart23"},
        {"DYNAMIC_CHART","createDynamicXYChart", "chart24"},
        {"OVERLAID_CHART","createOverlaidChart", "chart25"},
        {"VERTICALLY_COMBINED_CHART","createVerticallyCombinedChart", "chart26"},
        {"HORIZONTALLY_COMBINED_CHART","createHorizontallyCombinedChart", "chart27"},
        {"COMBINED_OVERLAID_CHART","createCombinedAndOverlaidChart1", "chart28"},
        {"COMBINED_OVERLAID_DYNAMIC_CHART","createCombinedAndOverlaidDynamicXYChart", "chart29"},
        {"THERMOMETER_CHART","createThermometerChart", "chart30"},
        {"METER_CHART","createMeterChart", "chart31"},
    };

    private JFreeChart[] chart = new JFreeChart[CHART_COMMANDS.length];

    private ChartFrame[] frame = new ChartFrame[CHART_COMMANDS.length];

    private JPanel[] panels = null;

    /** The preferred size for the frame. */
    public static final Dimension PREFERRED_SIZE = new Dimension(780, 400);

    /** A frame for displaying information about the application. */
    private AboutFrame aboutFrame;

    /** A tabbed pane for displaying sample charts; */
    private JTabbedPane tabbedPane;

    private ResourceBundle resources;

    /**
     * Constructs a demonstration application for the JFreeChart Class Library.
     */
    public JFreeChartDemo() {
        super(JFreeChart.INFO.getName()+" "+JFreeChart.INFO.getVersion()+" Demo");
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
     *
     * @return The preferred size.
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
        else {
            /// Loop through available commands to find index to current command.
            int chartnum = -1;
            int i = CHART_COMMANDS.length;
            while (i > 0) {
                --i;
                if (command.equals(CHART_COMMANDS[i][0])) {
                    chartnum = i;
                    i = 0;
                }
            }

            /// check our index is valid
            if ((chartnum >= 0) && (chartnum < frame.length)) {
                /// Check we have not already created chart.
                if (frame[chartnum] == null) {
                    // setup the chart.
                    getChart(chartnum);

                    // present it in a frame...
                    String str = resources.getString(CHART_COMMANDS[chartnum][2]+".title");
                    frame[chartnum] = new ChartFrame(str, chart[chartnum]);
                    frame[chartnum].pack();
                    RefineryUtilities.positionFrameRandomly(frame[chartnum]);

                    /// Set panel to zoomable if required
                    try {
                        str = resources.getString(CHART_COMMANDS[chartnum][2]+".zoom");
                        if ((str != null) && (str.toLowerCase().equals("true"))) {
                            ChartPanel panel =  frame[chartnum].getChartPanel();
                            panel.setMouseZoomable(true);
                            panel.setHorizontalAxisTrace(true);
                            panel.setVerticalAxisTrace(true);
                        }
                    }
                    catch (Exception ex) {
                        System.out.println(ex.toString());
                    }

                    frame[chartnum].show();

                }
                else {
                    frame[chartnum].show();
                    frame[chartnum].requestFocus();
                }
            }
        }
    }

    public JFreeChart getChart(int i) {
        if ((i < 0) && (i >= chart.length))
            i = 0;

        if (chart[i] == null) {
            /// Utilise reflection to invoke method to create new chart if required.
            try {
                Method method = getClass().getDeclaredMethod(CHART_COMMANDS[i][1], null);
                chart[i] = (JFreeChart) method.invoke(this, null);
            }
            catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return chart[i];
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
     * Create a horizontal bar chart.
     */
    public JFreeChart createHorizontalBarChart() {

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

        return chart;

    }

    /**
     * Create a stacked horizontal bar chart.
     */
    public JFreeChart createStackedHorizontalBarChart() {

        // create a default chart based on some sample data...
        String title = resources.getString("bar.horizontal-stacked.title");
        String domain = resources.getString("bar.horizontal-stacked.domain");
        String range = resources.getString("bar.horizontal-stacked.range");

        CategoryDataset data = DemoDatasetFactory.createCategoryDataset();
        JFreeChart chart = ChartFactory.createStackedHorizontalBarChart(title, domain, range,
            data, true);

        // then customise it a little...
        chart.setBackgroundPaint(new GradientPaint(0, 0, Color.white, 1000, 0, Color.blue));
        return chart;

    }

    /**
     * Creates a vertical bar chart.
     */
    public JFreeChart createVerticalBarChart() {

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
        return chart;

    }

    /**
     * Creates a vertical 3D bar chart.
     */
    public JFreeChart createVertical3DBarChart() {

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
        return chart;

    }

    /**
     * Displays a vertical bar chart in its own frame.
     */
    public JFreeChart createVerticalStackedBarChart() {

        // create a default chart based on some sample data...
        String title = resources.getString("bar.vertical-stacked.title");
        String domain = resources.getString("bar.vertical-stacked.domain");
        String range = resources.getString("bar.vertical-stacked.range");

        CategoryDataset data = DemoDatasetFactory.createCategoryDataset();
        JFreeChart chart = ChartFactory.createStackedVerticalBarChart(title, domain, range,
            data, true);

        // then customise it a little...
        chart.setBackgroundPaint(new GradientPaint(0, 0, Color.white, 1000, 0, Color.red));
        return chart;

    }

    /**
     * Displays a vertical bar chart in its own frame.
     */
    public JFreeChart createVerticalStacked3DBarChart() {

        // create a default chart based on some sample data...
        String title = resources.getString("bar.vertical-stacked3D.title");
        String domain = resources.getString("bar.vertical-stacked3D.domain");
        String range = resources.getString("bar.vertical-stacked3D.range");
        CategoryDataset data = DemoDatasetFactory.createCategoryDataset();
        JFreeChart chart = ChartFactory.createStackedVerticalBarChart3D(title, domain, range,
            data, true);

        // then customise it a little...
        chart.setBackgroundPaint(new GradientPaint(0, 0, Color.white, 1000, 0, Color.red));
        return chart;

    }

    /**
     * Displays pie chart one in its own frame.
     */
    public JFreeChart createPieChartOne() {

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
        return chart;

    }

    /**
     * Displays pie chart two in its own frame.
     */
    public JFreeChart createPieChartTwo() {

        ImageIcon icon = new javax.swing.ImageIcon(JFreeChart.class.getResource("gorilla.jpg"));
        Image bgimage = icon.getImage();
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
        return chart;

    }

    /**
     * Displays an XYPlot in its own frame.
     */
    public JFreeChart createXYPlot() {

        // create a default chart based on some sample data...
        String title = resources.getString("xyplot.sample1.title");
        String domain = resources.getString("xyplot.sample1.domain");
        String range = resources.getString("xyplot.sample1.range");
        XYDataset data = DemoDatasetFactory.createSampleXYDataset();
        JFreeChart chart = ChartFactory.createXYChart(title, domain, range, data, true);

        // then customise it a little...
        chart.setBackgroundPaint(new GradientPaint(0, 0, Color.white, 0, 1000, Color.green));
        return chart;

    }

    /**
     * Displays a vertical bar chart in its own frame.
     */
    public JFreeChart createTimeSeries1Chart() {

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
        return chart;

    }

    /**
     * Displays a vertical bar chart in its own frame.
     */
    public JFreeChart createTimeSeries2Chart() {

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
        return chart;

    }

    /**
     * Displays a vertical bar chart in its own frame.
     */
    public JFreeChart createTimeSeriesWithMAChart() {

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
        return chart;

    }

    /**
     * Displays a vertical bar chart in its own frame.
     */
    public JFreeChart createHighLowChart() {

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
        return chart;

    }

    /**
     * Creates a candlestick chart.
     */
    public JFreeChart createCandlestickChart() {

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
        return chart;

    }

    /**
     * Creates a signal chart.
     */
    public JFreeChart createSignalChart() {

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
        return chart;

    }

    /**
     * Creates a signal chart.
     */
    public JFreeChart createThermometerChart() {

        // create a default chart based on some sample data...
        String title = resources.getString("meter.thermo.title");
        String subtitleStr = resources.getString("meter.thermo.subtitle");
        String units = resources.getString("meter.thermo.units");

        DefaultMeterDataset data = DemoDatasetFactory.createMeterDataset();
        data.setValue(new Double(34.0));
        ThermometerPlot plot = new ThermometerPlot(data);
        plot.setUnits(units);
        JFreeChart chart = new JFreeChart(title, JFreeChart.DEFAULT_TITLE_FONT,
                                           plot, false);

        // then customise it a little...
        TextTitle subtitle = new TextTitle(subtitleStr, new Font("SansSerif", Font.BOLD, 12));
        chart.addTitle(subtitle);
        chart.setBackgroundPaint(new GradientPaint(0, 0, Color.white, 0, 1000, Color.blue));
        return chart;

    }

    public JFreeChart createMeterChart() {

        // create a default chart based on some sample data...
        String title = resources.getString("meter.meter.title");
        String subtitleStr = resources.getString("meter.meter.subtitle");
        String units = resources.getString("meter.meter.units");
        DefaultMeterDataset data = DemoDatasetFactory.createMeterDataset();

        data.setUnits(units);
        Plot meterplot = new MeterPlot(data);
        JFreeChart chart = new JFreeChart(title, JFreeChart.DEFAULT_TITLE_FONT,
                                          meterplot, false);

        // then customise it a little...
        TextTitle subtitle = new TextTitle(subtitleStr, new Font("SansSerif", Font.BOLD, 12));
        chart.addTitle(subtitle);
        chart.setBackgroundPaint(new GradientPaint(0, 0, Color.white, 0, 1000, Color.blue));
        return chart;
    }

    /**
     * Displays a wind plot in its own frame.
     */
    public JFreeChart createWindPlot() {

        // create a default chart based on some sample data...
        String title = resources.getString("other.wind.title");
        String domain = resources.getString("other.wind.domain");
        String range = resources.getString("other.wind.range");
        WindDataset data = DemoDatasetFactory.createWindDataset1();
        JFreeChart chart = ChartFactory.createWindPlot(title, domain, range,
            data, true);

        // then customise it a little...
        chart.setBackgroundPaint(new GradientPaint(0, 0, Color.white, 1000, 0, Color.green));
        return chart;

    }

    /**
     * Displays a scatter plot in its own frame.  Crosshairs are switched on for demo purposes.
     */
    public JFreeChart createScatterPlot() {

        // create a default chart based on some sample data...
        String title = resources.getString("other.scatter.title");
        String domain = resources.getString("other.scatter.domain");
        String range = resources.getString("other.scatter.range");
        XYDataset data = new SampleXYDataset2();
        JFreeChart chart = ChartFactory.createScatterPlot(title, domain, range, data, true);

        // then customise it a little...
        chart.setBackgroundPaint(new GradientPaint(0, 0, Color.white, 1000, 0, Color.green));

        XYPlot plot = (XYPlot)chart.getPlot();
        ValueAxis domainAxis = plot.getDomainAxis();
        domainAxis.setCrosshairVisible(true);
        NumberAxis rangeAxis = (NumberAxis)plot.getRangeAxis();
        rangeAxis.setCrosshairVisible(true);
        rangeAxis.setAutoRangeIncludesZero(false);
        return chart;

    }

    /**
     * Displays a line chart in its own frame.
     */
    public JFreeChart createLineChart() {

        // create a default chart based on some sample data...
        String title = resources.getString("other.line.title");
        String domain = resources.getString("other.line.domain");
        String range = resources.getString("other.line.range");
        CategoryDataset data = DemoDatasetFactory.createCategoryDataset();
        JFreeChart chart = ChartFactory.createLineChart(title, domain, range, data, true);

        // then customise it a little...
        ImageIcon icon = new javax.swing.ImageIcon(JFreeChart.class.getResource("gorilla.jpg"));
        chart.setBackgroundImage(icon.getImage());
        chart.setBackgroundPaint(new GradientPaint(0, 0, Color.white, 0, 1000, Color.green));

        CategoryPlot plot = (CategoryPlot)chart.getPlot();
        plot.setBackgroundAlpha(0.65f);
        HorizontalCategoryAxis axis = (HorizontalCategoryAxis)plot.getDomainAxis();
        axis.setVerticalCategoryLabels(true);
        return chart;
    }

    /**
     * Displays a vertical bar chart in its own frame.
     */
    public JFreeChart createVerticalXYBarChart() {

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
        return chart;
    }

    /**
     * Displays a vertical bar chart in its own frame.
     */
    public JFreeChart createNullXYPlot() {

        // create a default chart based on some sample data...
        String title = resources.getString("test.null.title");
        String domain = resources.getString("test.null.domain");
        String range = resources.getString("test.null.range");
        XYDataset data = null;
        JFreeChart chart = ChartFactory.createXYChart(title, domain, range, data, true);

        // then customise it a little...
        chart.setBackgroundPaint(new GradientPaint(0, 0, Color.white, 1000, 0, Color.red));
        return chart;

    }

    /**
     * Creates an XY plot with an empty dataset.
     */
    public JFreeChart createXYPlotZeroData() {

        // create a default chart based on some sample data...
        String title = resources.getString("test.zero.title");
        String domain = resources.getString("test.zero.domain");
        String range = resources.getString("test.zero.range");
        XYDataset data = new EmptyXYDataset();
        JFreeChart chart = ChartFactory.createXYChart(title, domain, range, data, true);

        // then customise it a little...
        chart.setBackgroundPaint(new GradientPaint(0, 0, Color.white, 1000, 0, Color.red));
        return chart;
    }

    /**
     * Displays a vertical bar chart in its own frame.
     */
    public JFreeChart createTimeSeriesChartInScrollPane() {

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
        return chart;

    }

    /**
     * Creates a bar chart with just one series.
     */
    public JFreeChart createSingleSeriesBarChart() {

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
        return chart;

    }

    /**
     * Displays an XY chart that is periodically updated by a background thread.  This is to
     * demonstrate the event notification system that automatically updates charts as required.
     */
    public JFreeChart createDynamicXYChart() {

        String title = resources.getString("test.dynamic.title");
        String domain = resources.getString("test.dynamic.domain");
        String range = resources.getString("test.dynamic.range");

        SampleXYDataset data = new SampleXYDataset();
        JFreeChart chart = ChartFactory.createXYChart(title, domain, range, data, true);
        SampleXYDatasetThread update = new SampleXYDatasetThread(data);

        Thread thread = new Thread(update);
        thread.start();

        return chart;

    }

    /**
     * Displays a combined and overlaid plot in its own frame.
     */
    public JFreeChart createOverlaidChart() {

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

        return chart;

    }

    /**
     * Creates a horizontally combined chart.
     */
    public JFreeChart createHorizontallyCombinedChart() {

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

        JFreeChart chart = null;

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
        return chart;

    }

    /**
     * Creates a vertically combined chart.
     */
    public JFreeChart createVerticallyCombinedChart() {

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
        return chart;

    }

    /**
     * Creates a combined and overlaid chart.
     */
    public JFreeChart createCombinedAndOverlaidChart1() {

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

        int n = 3;    // number of combined (vertically laidout) charts

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
        return chart;

    }

    /**
     * Displays an XY chart that is periodically updated by a background thread.  This is to
     * demonstrate the event notification system that automatically updates charts as required.
     */
    public JFreeChart createCombinedAndOverlaidDynamicXYChart() {

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

        // then customise it a little...
        TextTitle subtitle = new TextTitle(subtitleStr, new Font("SansSerif", Font.BOLD, 12));
        chart.addTitle(subtitle);
        chart.setBackgroundPaint(new GradientPaint(0, 0, Color.white, 0, 1000, Color.cyan));

        // setup thread to update base Dataset
        SampleXYDatasetThread update = new SampleXYDatasetThread(data);
        Thread thread = new Thread(update);
        thread.start();

        return chart;

    }

    /**
     * Displays information about the application.
     */
    private void about() {

        String title = this.resources.getString("about.title");
        String versionLabel = this.resources.getString("about.version.label");
        if (aboutFrame==null) {
            aboutFrame = new AboutFrame(title, JFreeChart.INFO);
            aboutFrame.pack();
            aboutFrame.setSize(700, 350);
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

        Font font = new Font("Dialog", Font.PLAIN, 12);
        JTabbedPane tabs = new JTabbedPane();

        int tab = 1;
        Vector titles = new Vector(0);
        String[] tabTitles;
        String title = null;

        while (tab > 0) {
            try {
                title = resources.getString("tabs." + tab);
                if (title != null)
                    titles.add(title);
                else
                    tab = -1;
                ++tab;
            }
            catch (Exception ex) {
                tab = -1;
            }
        }

        if (titles.size() == 0) {
            titles.add("Default");
        }

        tab = titles.size();
        panels = new JPanel[tab];
        tabTitles = new String[tab];

        --tab;
        for (; tab >= 0; --tab) {
            title = titles.get(tab).toString();
            tabTitles[tab] = title;
        }
        titles.removeAllElements();


        for (int i = 0; i < tabTitles.length; ++i) {
            panels[i] = new JPanel();
            panels[i].setLayout(new LCBLayout(20));
            panels[i].setPreferredSize(new Dimension(360, 20));
            panels[i].setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
            tabs.add(tabTitles[i], new JScrollPane(panels[i]));
        }

        String description;
        String buttonText = resources.getString("charts.display");
        JButton b1;

        // Load the CHARTS ...
        for (int i = 0; i <= CHART_COMMANDS.length-1; ++i) {
            title = resources.getString(CHART_COMMANDS[i][2]+".title");
            description = resources.getString(CHART_COMMANDS[i][2]+".description");
            try {
                tab = Integer.parseInt(resources.getString(CHART_COMMANDS[i][2]+".tab"));
                --tab;
            }
            catch (Exception ex) {
                System.err.println("Demo : Error retrieving tab identifier for chart " + CHART_COMMANDS[i][2]);
                System.err.println("Demo : Error = " + ex.getMessage());
                tab = 0;
            }
            if ((tab < 0) || (tab >= panels.length))
                tab = 0;

            System.out.println("Demo : adding " + CHART_COMMANDS[i][0] + " to panel " + tab);
            panels[tab].add(RefineryUtilities.createJLabel(title, font));
            panels[tab].add(new DescriptionPanel(new JTextArea(description)));
            b1 = RefineryUtilities.createJButton(buttonText, font);
            b1.setActionCommand(CHART_COMMANDS[i][0]);
            b1.addActionListener(this);
            panels[tab].add(b1);
        }

        return tabs;

    }

}