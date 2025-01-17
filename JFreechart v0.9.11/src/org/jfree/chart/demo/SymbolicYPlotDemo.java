/* ======================================
 * JFreeChart : a free Java chart library
 * ======================================
 *
 * Project Info:  http://www.jfree.org/jfreechart/index.html
 * Project Lead:  David Gilbert (david.gilbert@object-refinery.com);
 *
 * (C) Copyright 2000-2003, by Object Refinery Limited and Contributors.
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
 * ----------------------
 * SymbolicYPlotDemo.java
 * ----------------------
 * (C) Copyright 2002, 2003, by Anthony Boulestreau and Contributors.
 *
 * Original Author:  Anthony Boulestreau;
 * Contributor(s):   David Gilbert (for Object Refinery Limited);
 *
 * Changes
 * -------
 * 29-Mar-2002 : Version 1 (AB);
 * 23-Apr-2002 : Updated to reflect revisions in combined plot classes (DG);
 * 25-Jun-2002 : Removed unnecessary imports (DG);
 * 11-Oct-2002 : Fixed errors reported by Checkstyle (DG);
 *
 */

package org.jfree.chart.demo;

import java.awt.Color;
import java.awt.GradientPaint;

import javax.swing.JFrame;

import org.jfree.chart.ChartFrame;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.SymbolicAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.labels.SymbolicXYToolTipGenerator;
import org.jfree.chart.plot.CombinedDomainXYPlot;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.StandardXYItemRenderer;
import org.jfree.chart.renderer.XYItemRenderer;
import org.jfree.data.CombinedDataset;
import org.jfree.data.SubSeriesDataset;
import org.jfree.data.XYDataset;
import org.jfree.data.YisSymbolic;
import org.jfree.ui.RefineryUtilities;

/**
 * A demonstration application for the symbolic axis plots.
 *
 * @author Anthony Boulestreau
 */
public class SymbolicYPlotDemo {

    /**
     * Displays an XYPlot with Y symbolic data.
     *
     * @param frameTitle  the frame title.
     * @param data  the data.
     * @param chartTitle  the chart title.
     * @param xAxisLabel  the x-axis label.
     * @param yAxisLabel  the y-axis label.
     */
    private static void displayYSymbolic(String frameTitle,
                                         XYDataset data, String chartTitle,
                                         String xAxisLabel, String yAxisLabel) {

        JFreeChart chart = createYSymbolicPlot(chartTitle, xAxisLabel, yAxisLabel, data, true);
        chart.setBackgroundPaint(new GradientPaint(0, 0, Color.white, 1000, 0, Color.green));

        JFrame frame = new ChartFrame(frameTitle, chart);
        frame.pack();
        RefineryUtilities.positionFrameRandomly(frame);
        frame.show();

    }

    /**
     * Create and display an overlaid chart.
     *
     * @param frameTitle  the frame title.
     * @param data1  dataset1.
     * @param data2  dataset2.
     */
    private static void displayYSymbolicOverlaid(String frameTitle,
                                                 XYDataset data1, XYDataset data2) {

        String title = "Animals Overlaid";
        String xAxisLabel = "Miles";
        String yAxisLabel = "Animal";

        // combine the y symbolic values of the two data sets...
        String[] combinedYSymbolicValues
            = SampleYSymbolicDataset.combineYSymbolicDataset((YisSymbolic) data1,
                                                             (YisSymbolic) data2);

        // make master dataset...
        CombinedDataset data = new CombinedDataset();
        data.add(data1);
        data.add(data2);

        // decompose data...
        XYDataset series0 = new SubSeriesDataset(data, 0);
        XYDataset series1 = new SubSeriesDataset(data, 1);
        XYDataset series2 = new SubSeriesDataset(data, 2);
        XYDataset series3 = new SubSeriesDataset(data, 3);
        XYDataset series4 = new SubSeriesDataset(data, 4);
        XYDataset series5 = new SubSeriesDataset(data, 5);
        XYDataset series6 = new SubSeriesDataset(data, 6);
        XYDataset series7 = new SubSeriesDataset(data, 7);

        // create main plot...
        ValueAxis valueAxis = new NumberAxis(xAxisLabel);
        SymbolicAxis symbolicAxis = new SymbolicAxis(yAxisLabel, combinedYSymbolicValues);
        XYItemRenderer renderer = new StandardXYItemRenderer(StandardXYItemRenderer.SHAPES, null);
        XYPlot plot = new XYPlot(series0, valueAxis, symbolicAxis, renderer);
        plot.setSecondaryDataset(0, series1);
        plot.setSecondaryDataset(1, series2);
        plot.setSecondaryDataset(2, series3);
        plot.setSecondaryDataset(3, series4);
        plot.setSecondaryDataset(4, series5);
        plot.setSecondaryDataset(5, series6);
        plot.setSecondaryDataset(6, series7);

        // make the chart...
        JFreeChart chart = new JFreeChart(title, JFreeChart.DEFAULT_TITLE_FONT, plot, true);
        chart.setBackgroundPaint(new GradientPaint(0, 0, Color.white, 0, 1000, Color.blue));

        // and present it in a frame...
        JFrame frame = new ChartFrame(frameTitle, chart);
        frame.pack();
        RefineryUtilities.positionFrameRandomly(frame);
        frame.show();

    }

    /**
     * Create and display a multi XY plot with horizontal layout.
     *
     * @param frameTitle  the frame title.
     * @param data1  dataset1.
     * @param data2  dataset2.
     */
    private static void displayYSymbolicCombinedHorizontally(String frameTitle,
                                                             SampleYSymbolicDataset data1,
                                                             SampleYSymbolicDataset data2) {

        String title = "Animals Horizontally Combined";
        String xAxisLabel = "Miles";
        String yAxisLabel = null;

        // combine the y symbolic values of the two data sets
        String[] combinedYSymbolicValues
            = SampleYSymbolicDataset.combineYSymbolicDataset((YisSymbolic) data1,
                                                             (YisSymbolic) data2);

        // make master dataset...
        CombinedDataset data = new CombinedDataset();
        data.add(data1);
        data.add(data2);

        // decompose data...
        XYDataset series0 = new SubSeriesDataset(data, 0);
        XYDataset series1 = new SubSeriesDataset(data, 1);
        XYDataset series2 = new SubSeriesDataset(data, 2);
        XYDataset series3 = new SubSeriesDataset(data, 3);
        XYDataset series4 = new SubSeriesDataset(data, 4);
        XYDataset series5 = new SubSeriesDataset(data, 5);
        XYDataset series6 = new SubSeriesDataset(data, 6);
        XYDataset series7 = new SubSeriesDataset(data, 7);

        // create axes...
        ValueAxis valueAxis1 = new NumberAxis(xAxisLabel);
        ValueAxis valueAxis2 = new NumberAxis(xAxisLabel);
        SymbolicAxis symbolicAxis = new SymbolicAxis(yAxisLabel, combinedYSymbolicValues);

        // make a horizontally combined plot
        CombinedDomainXYPlot mainPlot = new CombinedDomainXYPlot(symbolicAxis);

        // add the sub-plots
        XYItemRenderer renderer = new StandardXYItemRenderer(StandardXYItemRenderer.SHAPES, null);
        XYPlot subplot0 = new XYPlot(series0, valueAxis1, null, renderer);
 //       subplot0.setSeriesPaint(0, Color.red);
        XYPlot subplot1 = new XYPlot(series1, valueAxis1, null, renderer);
 //       subplot1.setSeriesPaint(0, Color.blue);
        XYPlot subplot2 = new XYPlot(series2, valueAxis1, null, renderer);
 //       subplot2.setSeriesPaint(0, Color.green);
        XYPlot subplot3 = new XYPlot(series3, valueAxis1, null, renderer);
  //      subplot3.setSeriesPaint(0, Color.yellow);
        XYPlot subplot4 = new XYPlot(series4, valueAxis2, null, renderer);
 //       subplot4.setSeriesPaint(0, Color.gray);
        XYPlot subplot5 = new XYPlot(series5, valueAxis2, null, renderer);
 //       subplot5.setSeriesPaint(0, Color.orange);
        XYPlot subplot6 = new XYPlot(series6, valueAxis2, null, renderer);
 //       subplot6.setSeriesPaint(0, Color.magenta);
        XYPlot subplot7 = new XYPlot(series7, valueAxis2, null, renderer);
 //       subplot7.setSeriesPaint(0, Color.cyan);

        mainPlot.add(subplot0, 1);
        mainPlot.add(subplot1, 1);
        mainPlot.add(subplot2, 1);
        mainPlot.add(subplot3, 1);
        mainPlot.add(subplot4, 1);
        mainPlot.add(subplot5, 1);
        mainPlot.add(subplot6, 1);
        mainPlot.add(subplot7, 1);

        // make the top level JFreeChart object
        JFreeChart chart = new JFreeChart(title, JFreeChart.DEFAULT_TITLE_FONT, mainPlot, true);

        // then customise it a little...
        chart.setBackgroundPaint(new GradientPaint(0, 0, Color.white, 0, 1000, Color.blue));

        // and present it in a frame...
        JFrame ySymbolicFrame = new ChartFrame(frameTitle, chart);
        ySymbolicFrame.pack();
        RefineryUtilities.positionFrameRandomly(ySymbolicFrame);
        ySymbolicFrame.show();

    }

    /**
     * Displays a vertically combined symbolic plot.
     *
     * @param frameTitle  the frame title.
     * @param data1  dataset 1.
     * @param data2  dataset 2.
     */
    private static void displayYSymbolicCombinedVertically(String frameTitle,
                                                           SampleYSymbolicDataset data1,
                                                           SampleYSymbolicDataset data2) {

        String title = "Animals Vertically Combined";
        String xAxisLabel = "Miles";
        String yAxisLabel = null;

        // create master dataset...
        CombinedDataset data = new CombinedDataset();
        data.add(data1);
        data.add(data2);

        // decompose data...
        XYDataset series0 = new SubSeriesDataset(data, 0);
        XYDataset series1 = new SubSeriesDataset(data, 1);
        XYDataset series2 = new SubSeriesDataset(data, 2);
        XYDataset series3 = new SubSeriesDataset(data, 3);
        XYDataset series4 = new SubSeriesDataset(data, 4);
        XYDataset series5 = new SubSeriesDataset(data, 5);
        XYDataset series6 = new SubSeriesDataset(data, 6);
        XYDataset series7 = new SubSeriesDataset(data, 7);

        // common horizontal and vertical axes
        ValueAxis valueAxis = new NumberAxis(xAxisLabel);
        SymbolicAxis symbolicAxis1
            = new SymbolicAxis(yAxisLabel, ((YisSymbolic) data1).getYSymbolicValues());
        SymbolicAxis symbolicAxis2
            = new SymbolicAxis(yAxisLabel, ((YisSymbolic) data2).getYSymbolicValues());

        // create the main plot...
        CombinedDomainXYPlot mainPlot = new CombinedDomainXYPlot(valueAxis);

        // and the sub-plots...
        XYItemRenderer renderer = new StandardXYItemRenderer(StandardXYItemRenderer.SHAPES, null);
        XYPlot subplot0 = new XYPlot(series0, null, symbolicAxis1, renderer);
 //       subplot0.setSeriesPaint(0, Color.red);
        XYPlot subplot1 = new XYPlot(series1, null, symbolicAxis1, renderer);
 //       subplot1.setSeriesPaint(0, Color.blue);
        XYPlot subplot2 = new XYPlot(series2, null, symbolicAxis1, renderer);
 //       subplot2.setSeriesPaint(0, Color.green);
        XYPlot subplot3 = new XYPlot(series3, null, symbolicAxis1, renderer);
 //       subplot3.setSeriesPaint(0, Color.yellow);
        XYPlot subplot4 = new XYPlot(series4, null, symbolicAxis2, renderer);
 //       subplot4.setSeriesPaint(0, Color.gray);
        XYPlot subplot5 = new XYPlot(series5, null, symbolicAxis2, renderer);
 //       subplot5.setSeriesPaint(0, Color.orange);
        XYPlot subplot6 = new XYPlot(series6, null, symbolicAxis2, renderer);
 //       subplot6.setSeriesPaint(0, Color.magenta);
        XYPlot subplot7 = new XYPlot(series7, null, symbolicAxis2, renderer);
 //       subplot7.setSeriesPaint(0, Color.cyan);

        // add the subplots to the main plot...
        mainPlot.add(subplot0, 1);
        mainPlot.add(subplot1, 1);
        mainPlot.add(subplot2, 1);
        mainPlot.add(subplot3, 1);
        mainPlot.add(subplot4, 1);
        mainPlot.add(subplot5, 1);
        mainPlot.add(subplot6, 1);
        mainPlot.add(subplot7, 1);

        // construct the chart...
        JFreeChart chart = new JFreeChart(title, JFreeChart.DEFAULT_TITLE_FONT, mainPlot, true);
        chart.setBackgroundPaint(new GradientPaint(0, 0, Color.white, 0, 1000, Color.blue));

        // and present it in a frame...
        JFrame frame = new ChartFrame(frameTitle, chart);
        frame.pack();
        RefineryUtilities.positionFrameRandomly(frame);
        frame.show();

    }

    /**
     * Creates a XY graph with symbolic value on Y axis.
     *
     * @param title  the chart title.
     * @param xAxisLabel  the x-axis label.
     * @param yAxisLabel  the y-axis label.
     * @param data  the data.
     * @param legend  a flag controlling whether or not the legend is created for the chart.
     *
     * @return the chart.
     */
    public static JFreeChart createYSymbolicPlot(String title, String xAxisLabel,
                                                 String yAxisLabel, XYDataset data,
                                                 boolean legend) {

        ValueAxis valueAxis = new NumberAxis(xAxisLabel);
        SymbolicAxis symbolicAxis
            = new SymbolicAxis(yAxisLabel, ((YisSymbolic) data).getYSymbolicValues());

        XYPlot plot = new XYPlot(data, valueAxis, symbolicAxis, null);
        XYItemRenderer renderer = new StandardXYItemRenderer(StandardXYItemRenderer.SHAPES,
                                                             new SymbolicXYToolTipGenerator());
        plot.setRenderer(renderer);
        JFreeChart chart = new JFreeChart(title, JFreeChart.DEFAULT_TITLE_FONT, plot, legend);
        return chart;

    }

    /**
     * Creates a sample symbolic dataset.
     *
     * @return the dataset.
     */
    public static SampleYSymbolicDataset createYSymbolicSample1() {

        String[] sData = {"Lion", "Elephant", "Monkey", "Hippopotamus", "Giraffe"};
        SampleYSymbolicDataset data
            = new SampleYSymbolicDataset("AY Sample", 20, sData, 4, 20,
                new String[] {"A Fall", "A Spring", "A Summer", "A Winter"});
        return data;

    }

    /**
     * Creates a sample symbolic dataset.
     *
     * @return The dataset.
     */
    public static SampleYSymbolicDataset createYSymbolicSample2() {

        String[] sData = {"Giraffe", "Gazelle", "Zebra", "Gnu"};
        SampleYSymbolicDataset data
            = new SampleYSymbolicDataset("BY Sample", 40, sData, 4, 20,
                new String[] {"B Fall", "B Spring", "B Summer", "B Winter"});
        return data;

    }

    /**
     * The starting point for the demonstration application.
     *
     * @param args  ignored.
     */
    public static void main(String[] args) {

        SampleYSymbolicDataset s1 = createYSymbolicSample1();
        SampleYSymbolicDataset s2 = createYSymbolicSample2();

        displayYSymbolic("Example 1", s1, "Animal A", "Miles", "Animal");

        displayYSymbolic("Example 2", s2, "Animal B", "Miles", "Animal");

        displayYSymbolicCombinedHorizontally("Example 3", (SampleYSymbolicDataset) s1.clone(),
                                                          (SampleYSymbolicDataset) s2.clone());

        displayYSymbolicCombinedVertically("Example 4", (SampleYSymbolicDataset) s1.clone(),
                                                        (SampleYSymbolicDataset) s2.clone());

        displayYSymbolicOverlaid("Example 5", (SampleYSymbolicDataset) s1.clone(),
                                              (SampleYSymbolicDataset) s2.clone());
    }

}
