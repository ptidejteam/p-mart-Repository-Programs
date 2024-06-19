/* ===============
 * JFreeChart Demo
 * ===============
 * Version:         0.5.6;
 * Project Lead:    David Gilbert (david.gilbert@bigfoot.com);
 *
 * File:            JFreeChartDemo.java
 * Author:          David Gilbert;
 * Contributor(s):  Andrzej Porebski, Matthew Wright;
 *
 * (C) Copyright 2000, Simba Management Limited;
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
 * $Id: JFreeChartDemo.java,v 1.1 2007/10/10 18:52:17 vauchers Exp $
 */

package com.jrefinery.chart.demo;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.awt.print.*;
import java.util.*;
import javax.swing.*;
import com.jrefinery.chart.*;
import com.jrefinery.chart.data.*;
import com.jrefinery.chart.ui.*;
import com.jrefinery.util.ui.*;

/**
 * The main frame in the chart demonstration application.
 */
public class JFreeChartDemo extends JFrame
                            implements ActionListener, Printable, WindowListener {

  /** A frame for displaying information about the system - this reference is kept to ensure that
      only one about screen is displayed at a time; */
  private AboutFrame aboutFrame;

  /** A frame for displaying information about the system - this reference is kept to ensure that
      only one system info screen is displayed at a time; */
  protected JFrame infoFrame;

  /** A tabbed pane for displaying sample charts; */
  private JTabbedPane tabbedPane;

  /** A reference to the chart being printed; */
  private JFreeChart printingChart;

  /**
   * Standard constructor: creates a frame containing a chart (uses the JFreeChart Class Library).
   */
  public JFreeChartDemo() {

    super("Chart Demo 0.5.6");

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

    JButton tempButtonRef;

    CategoryDataSource categoryData = createCategoryDataSource();

    JFreeChart chart = JFreeChart.createVerticalBarChart(categoryData);
    chart.setChartBackgroundPaint(new GradientPaint(0, 0, Color.white, 1000, 0, Color.red));
    //chart.setLegend(null);
    Plot bPlot = chart.getPlot();
    HorizontalCategoryAxis cAxis = (HorizontalCategoryAxis)bPlot.getAxis(Plot.HORIZONTAL_AXIS);
    cAxis.setVerticalCategoryLabels(true);
    JFreeChartPanel chartPanel1 = new JFreeChartPanel(chart);
    chartPanel1.setBorder(BorderFactory.createCompoundBorder(
                            BorderFactory.createEmptyBorder(4, 4, 4, 4),
                            BorderFactory.createLineBorder(Color.darkGray, 1)));


    // data = new SampleCategoryDataSource();
    categoryData = createCategoryDataSource();
    chart = JFreeChart.createHorizontalBarChart(categoryData);
    chart.setChartBackgroundPaint(new GradientPaint(0, 0, Color.white,0, 1000, Color.orange));
    JFreeChartPanel chartPanel2 = new JFreeChartPanel(chart);
    chartPanel2.setBorder(BorderFactory.createCompoundBorder(
                            BorderFactory.createEmptyBorder(4, 4, 4, 4),
                            BorderFactory.createLineBorder(Color.darkGray, 1)));

    // data = new SampleCategoryDataSource();
    categoryData = createCategoryDataSource();
    chart = JFreeChart.createLineChart(categoryData);
    chart.setChartBackgroundPaint(new GradientPaint(0, 0, Color.white,0, 1000, Color.yellow));
    JFreeChartPanel chartPanel3 = new JFreeChartPanel(chart);
    chartPanel3.setBorder(BorderFactory.createCompoundBorder(
                            BorderFactory.createEmptyBorder(4, 4, 4, 4),
                            BorderFactory.createLineBorder(Color.darkGray, 1)));

    XYDataSource xyData = new SampleXYDataSource();
    chart = JFreeChart.createXYChart(xyData);
    chart.setChartBackgroundPaint(new GradientPaint(0, 0, Color.white,0, 1000, Color.green));
    Plot xyPlot = chart.getPlot();
    NumberAxis hhAxis = (NumberAxis) xyPlot.getAxis(Plot.HORIZONTAL_AXIS);
    hhAxis.setAutoTickValue(false);
    hhAxis.setTickValue(new Double(3.0));
    JFreeChartPanel chartPanel4 = new JFreeChartPanel(chart);
    chartPanel4.setBorder(BorderFactory.createCompoundBorder(
                            BorderFactory.createEmptyBorder(4, 4, 4, 4),
                            BorderFactory.createLineBorder(Color.darkGray, 1)));

    XYDataSource xyData1 = createXYDataSource();
    //XYDataSource xyData2 = createTestXYDataSource();
    chart = JFreeChart.createTimeSeriesChart(xyData1);
    StandardTitle title = (StandardTitle)chart.getTitle();
    if (title==null) {
      title = new StandardTitle("Value of GBP", new Font("Arial", Font.BOLD, 12));
    }
    title.setTitle("Value of GBP");
    chart.setChartBackgroundPaint(new GradientPaint(0, 0, Color.white,0, 1000, Color.blue));
    Plot myPlot = chart.getPlot();
    Axis myVerticalAxis = myPlot.getAxis(Plot.VERTICAL_AXIS);
    myVerticalAxis.setLabel("USD per GBP");
    //DateAxis myHorizontalAxis = (DateAxis) myPlot.getAxis(Plot.HORIZONTAL_AXIS);
    //myHorizontalAxis.setAutoTickValue(false);
    //myHorizontalAxis.setAutoRange(false);
    //myHorizontalAxis.setMinimumDate(new GregorianCalendar(1999, 4, 1).getTime());
    //myHorizontalAxis.setMaximumDate(new GregorianCalendar(1999, 5, 1).getTime());
    //myHorizontalAxis.setAutoTickValue(false);
    //myHorizontalAxis.getTickLabelFormatter().applyPattern("d-MMM-y");
    //myHorizontalAxis.setTickUnit(new DateUnit(Calendar.DATE, 3));
    JFreeChartPanel chartPanel5 = new JFreeChartPanel(chart);
    chartPanel5.setBorder(BorderFactory.createCompoundBorder(
                            BorderFactory.createEmptyBorder(4, 4, 4, 4),
                            BorderFactory.createLineBorder(Color.darkGray, 1)));

    VerticalNumberAxis vnAxis = (VerticalNumberAxis)chart.getPlot().getAxis(Plot.VERTICAL_AXIS);
    //vnAxis.setMinimumAxisValue(new Double(10000.0));
    //vnAxis.setMaximumAxisValue(new Double(11000.0));
    //vnAxis.setAutoRange(false);
    //vnAxis.setAutoTickUnits(false);
    vnAxis.setAutoRangeIncludesZero(false);
    //vnAxis.setTickUnits(new Double(0.020));
    //vnAxis.getTickLabelFormatter().applyLocalizedPattern("0.0000");
    chart.setDataSource(xyData1);

    categoryData = createCategoryDataSource();
    chart = JFreeChart.createPieChart(categoryData);
    chart.setChartBackgroundPaint(new GradientPaint(0, 0, Color.white,0, 1000, Color.orange));
    JFreeChartPanel chartPanel6 = new JFreeChartPanel(chart);
    chartPanel6.setBorder(BorderFactory.createCompoundBorder(
                            BorderFactory.createEmptyBorder(4, 4, 4, 4),
                            BorderFactory.createLineBorder(Color.darkGray, 1)));

    // Added by Andrzej Porebski
    xyData = createHiLowDataSource();
    chart = JFreeChart.createHiLowChart(xyData);
    title = (StandardTitle)chart.getTitle();
    title.setTitle("HiLow Open/Close IBM");
    chart.setChartBackgroundPaint(new GradientPaint(0, 0, Color.white,0,
1000, Color.magenta));
    myPlot = chart.getPlot();
    myVerticalAxis = myPlot.getAxis(Plot.VERTICAL_AXIS);
    myVerticalAxis.setLabel("Price in ($) per share");
    JFreeChartPanel chartPanel7 = new JFreeChartPanel(chart);
    chartPanel7.setBorder(BorderFactory.createCompoundBorder(
                            BorderFactory.createEmptyBorder(4, 4, 4, 4),
                            BorderFactory.createLineBorder(Color.darkGray, 1)));

    /* start plot fit code */

    // moving avg
    XYDataSource xyData2 = createXYDataSource();
	MovingAveragePlotFitAlgorithm mavg = new MovingAveragePlotFitAlgorithm();
	mavg.setPeriod(30);
    PlotFit pf = new PlotFit(xyData2, mavg);
    xyData2 = pf.getFit();
    chart = JFreeChart.createTimeSeriesChart(xyData2);
    title = (StandardTitle)chart.getTitle();
    title.setTitle("30 day moving average of GBP");
    chart.setChartBackgroundPaint(new GradientPaint(0, 0, Color.white,0, 1000, Color.blue));
    myPlot = chart.getPlot();
    myVerticalAxis = myPlot.getAxis(Plot.VERTICAL_AXIS);
    myVerticalAxis.setLabel("USD per GBP");
    JFreeChartPanel chartPanel8 = new JFreeChartPanel(chart);
    chartPanel8.setBorder(BorderFactory.createCompoundBorder(
                            BorderFactory.createEmptyBorder(4, 4, 4, 4),
                            BorderFactory.createLineBorder(Color.darkGray, 1)));
	vnAxis = (VerticalNumberAxis)chart.getPlot().getAxis(Plot.VERTICAL_AXIS);
    vnAxis.setAutoRangeIncludesZero(false);
    chart.setDataSource(xyData2);

   	// linear fit
    XYDataSource xyData3 = createXYDataSource();
    pf = new PlotFit(xyData3, new LinearPlotFitAlgorithm());
    xyData3 = pf.getFit();
    chart = JFreeChart.createTimeSeriesChart(xyData3);
    title = (StandardTitle)chart.getTitle();
    title.setTitle("Linear Fit of GBP");
    chart.setChartBackgroundPaint(new GradientPaint(0, 0, Color.white,0, 1000, Color.blue));
    myPlot = chart.getPlot();
    myVerticalAxis = myPlot.getAxis(Plot.VERTICAL_AXIS);
    myVerticalAxis.setLabel("USD per GBP");
    JFreeChartPanel chartPanel9 = new JFreeChartPanel(chart);
    chartPanel9.setBorder(BorderFactory.createCompoundBorder(
                            BorderFactory.createEmptyBorder(4, 4, 4, 4),
                            BorderFactory.createLineBorder(Color.darkGray, 1)));
	vnAxis = (VerticalNumberAxis)chart.getPlot().getAxis(Plot.VERTICAL_AXIS);
    vnAxis.setAutoRangeIncludesZero(false);
    chart.setDataSource(xyData3);

/* end plot fit code */


    tabbedPane = new JTabbedPane();
    tabbedPane.addTab("Vertical Bar Chart", chartPanel1);
    tabbedPane.addTab("Horizontal Bar Chart", chartPanel2);
    tabbedPane.addTab("Line Chart", chartPanel3);
    tabbedPane.addTab("XY Plot", chartPanel4);
    tabbedPane.addTab("Time Series", chartPanel5);
    tabbedPane.addTab("Pie Plot", chartPanel6);
    tabbedPane.addTab("Hi-Low Plot", chartPanel7);
    tabbedPane.addTab("Time Series Moving Average", chartPanel8);
    tabbedPane.addTab("Time Series Linear Fit", chartPanel9);

    content.add(tabbedPane);

    setContentPane(content);

  }

  /**
   * Handles menu selections by passing control to an appropriate method.
   */
  public void actionPerformed(ActionEvent event) {
    String command = event.getActionCommand();
    if (command.equals("Print")) {
      attemptPrint();
    }
    else if (command.equals("Exit")) {
      attemptExit();
    }
    else if (command.equals("ChartProperties")) {
      attemptEditChartProperties();
    }
    else if (command.equals("Info")) {
      info();
    }
    else if (command.equals("About")) {
      about();
    }

  }

  /**
   * Handles the user request to print.
   */
  public void attemptPrint() {
    JFreeChartPanel selected = (JFreeChartPanel)(tabbedPane.getSelectedComponent());
    if (selected!=null) {
      printChart(selected.getChart());
    }
  }

  /**
   * Print a copy of the specified chart - let the user choose the page orientation.
   */
  public void printChart(JFreeChart chart) {

    PrinterJob pj = PrinterJob.getPrinterJob();
    pj.setPrintable(this);
    printingChart = chart;
    if (pj.printDialog()) {
      try {
        pj.print();
      }
      catch (PrinterException e) {
        JOptionPane.showMessageDialog(this, e);
      }
    }

  }

  /**
   * Supports the Printable interface by drawing a chart on a single page.
   */
  public int print(Graphics g, PageFormat pf, int pageIndex) {
    if (pageIndex!=0) return NO_SUCH_PAGE;
    Graphics2D g2 = (Graphics2D)g;
    double x = pf.getImageableX();
    double y = pf.getImageableY();
    double w = pf.getImageableWidth();
    double h = pf.getImageableHeight();
    printingChart.draw(g2, new Rectangle2D.Double(x, y, w, h));
    return PAGE_EXISTS;
  }

  /**
   * Creates and returns a menu-bar for the frame.
   */
  private JMenuBar createMenuBar() {

    // create the menus
    JMenuBar menuBar = new JMenuBar();

    // first the file menu
    JMenu fileMenu = new JMenu("File", true);
    fileMenu.setMnemonic('F');
    JMenuItem printItem = new JMenuItem("Print...", 'O');
    printItem.setActionCommand("Print");
    printItem.addActionListener(this);
    fileMenu.add(printItem);

    fileMenu.add(new JSeparator());

    JMenuItem exitItem = new JMenuItem("Exit", 'x');
    exitItem.setActionCommand("Exit");
    exitItem.addActionListener(this);
    fileMenu.add(exitItem);

    // then the edit menu
    JMenu editMenu = new JMenu("Edit");
    editMenu.setMnemonic('E');
    JMenuItem chartPropertiesItem = new JMenuItem("Chart Properties...", 'P');
    chartPropertiesItem.setActionCommand("ChartProperties");
    chartPropertiesItem.addActionListener(this);
    editMenu.add(chartPropertiesItem);

    // then the help menu
    JMenu helpMenu = new JMenu("Help");
    helpMenu.setMnemonic('H');

    JMenuItem infoItem = new JMenuItem("System Info...", 'S');
    infoItem.setActionCommand("Info");
    infoItem.addActionListener(this);
    helpMenu.add(infoItem);

    helpMenu.addSeparator();

    JMenuItem aboutItem = new JMenuItem("About...", 'A');
    aboutItem.setActionCommand("About");
    aboutItem.addActionListener(this);
    helpMenu.add(aboutItem);

    // finally, glue together the menu and return it
    menuBar.add(fileMenu);
    menuBar.add(editMenu);
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
   * Displays a dialog that allows the user to edit the properties for the current chart.
   */
  private void attemptEditChartProperties() {
    JFreeChart chart = getCurrentChart();
    ChartPropertyEditPanel panel = new ChartPropertyEditPanel(chart);
    int result = JOptionPane.showConfirmDialog(this, panel, "Chart Properties",
                   JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
    if (result==JOptionPane.OK_OPTION) {
      panel.setChartProperties(chart);
    }
  }

  /**
   * Returns the chart that is displayed in the selected tab.
   */
  private JFreeChart getCurrentChart() {
    JFreeChartPanel selected = (JFreeChartPanel)(tabbedPane.getSelectedComponent());
    if (selected!=null) {
      return selected.getChart();
    }
    return null;
  }

  /**
   * Displays the current system properties in a frame.
   */
  private void info() {
    if (infoFrame==null) {
      infoFrame = new SystemPropertiesFrame(true);
      infoFrame.addWindowListener(this);
      infoFrame.pack();
      Swing.positionFrameRandomly(infoFrame);
      infoFrame.show();
    }
    infoFrame.requestFocus();
  }

  /**
   * Displays information about the application.
   */
  private void about() {
    if (aboutFrame==null) {
      aboutFrame = new AboutFrame("About...");
      Swing.centerFrameOnScreen(aboutFrame);
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

    Swing.centerFrameOnScreen(f);

    // and show it...
    f.show();
  }

  /**
   * Creates and returns a category data source for the demo charts.
   */
  public CategoryDataSource createCategoryDataSource() {

    Number[][] data = new Integer[][]
      { { new Integer(10), new Integer(4), new Integer(15), new Integer(14) },
        { new Integer(5), new Integer(7), new Integer(14), new Integer(3) },
        { new Integer(6), new Integer(17), new Integer(12), new Integer(7) },
        { new Integer(7), new Integer(15), new Integer(11), new Integer(0) },
        { new Integer(8), new Integer(6), new Integer(10), new Integer(9) },
        { new Integer(9), new Integer(8), new Integer(8), new Integer(6) },
        { new Integer(10), new Integer(9), new Integer(7), new Integer(7) },
        { new Integer(11), new Integer(13), new Integer(9), new Integer(9) },
        { new Integer(3), new Integer(7), new Integer(11), new Integer(10) } };

    return new DefaultCategoryDataSource(data);

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
   * Creates and returns a XYDataSource for the demo charts.
   */
  public XYDataSource createXYDataSource() {

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

    return new DefaultXYDataSource(new String[] { "USD" }, data);
  }

  /**
   * Creates and returns a XYDataSource for the demo charts.
   */
  public XYDataSource createTestXYDataSource() {

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

    return new DefaultXYDataSource(data);
  }


  /**
   * Creates and returns a sample hi-low data source for the demo.  Added by Andrzej Porebski.
   */
  public XYDataSource createHiLowDataSource() {

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

    return new DefaultXYDataSource(new String[] { "IBM" }, data);

  }

  /**
   * Required for WindowListener interface, but not used by this class.
   */
  public void windowActivated(WindowEvent e) {}

  /**
   * Clears the reference to the print preview frames when they are closed.
   */
  public void windowClosed(WindowEvent e) {
    if (e.getWindow()==this.infoFrame) {
      infoFrame=null;
    }
    else if (e.getWindow()==this.aboutFrame) {
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