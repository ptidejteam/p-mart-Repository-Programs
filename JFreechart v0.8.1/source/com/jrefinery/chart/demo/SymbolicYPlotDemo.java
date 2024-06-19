/* ===============
 * JFreeChart Demo
 * ===============
 *
 * Project Info:  http://www.object-refinery.com/jfreechart/index.html
 * Project Lead:  David Gilbert (david.gilbert@object-refinery.com);
 *
 * (C) Copyright 2000-2002, by Simba Management Limited and Contributors.
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
 * ---------------------------
 * SymbolicYPlotDemo.java
 * ---------------------------
 *
 * Original Author:  Anthony Boulestreau.
 * Contributor(s):   -;
 *
 *
 * Changes
 * -------
 * 29-Mar-2002 : Version 1 (AB);
 *
 */

package com.jrefinery.chart.demo;

import java.awt.*;
import javax.swing.*;
import java.lang.reflect.Array;

import com.jrefinery.data.*;
import com.jrefinery.chart.*;
import com.jrefinery.ui.*;
import com.jrefinery.chart.tooltips.*;
import com.jrefinery.chart.combination.*;

public class SymbolicYPlotDemo {

	/**
	 * Displays an XYPlot with Y symbolic data.
	 */
    private static void displayYSymbolic(XYDataset data, String title, String xAxisLabel, String yAxisLabel) {

		JFreeChart chart = createYSymbolicPlot(title, xAxisLabel, yAxisLabel, data, true);
		
		chart.setBackgroundPaint(new GradientPaint(0, 0, Color.white, 1000, 0, Color.green));

		JFrame ySymbolicFrame = new JFreeChartFrame("Y Symbolic", chart);
		ySymbolicFrame.pack();
		JRefineryUtilities.positionFrameRandomly(ySymbolicFrame);
		ySymbolicFrame.show();
    }


	/**
	 * Vertically combined sample1 and sample2 and display it.
	 */
    private static void displayYSymbolicCombinedVertically(SampleYSymbolicDataset data1, SampleYSymbolicDataset data2) {
		String title = "Animals Horizontally Combined";
		String xAxisLabel = "Miles";
		String yAxisLabel = "Animal";
		
		// make master Dataset as combination of the 2 dataset
		CombinedDataset data = new CombinedDataset();
		data.add(data1);
		data.add(data2);

		// decompose data into its height dataset series
		SeriesDataset series0 = new SubSeriesDataset(data, 0);
		SeriesDataset series1 = new SubSeriesDataset(data, 1);
		SeriesDataset series2 = new SubSeriesDataset(data, 2);
		SeriesDataset series3 = new SubSeriesDataset(data, 3);
		SeriesDataset series4 = new SubSeriesDataset(data, 4);
		SeriesDataset series5 = new SubSeriesDataset(data, 5);
		SeriesDataset series6 = new SubSeriesDataset(data, 6);
		SeriesDataset series7 = new SubSeriesDataset(data, 7);

		JFreeChart chart = null;

		try {
			// common horizontal and vertical axes
			ValueAxis valueAxis = new HorizontalNumberAxis(xAxisLabel);
			VerticalSymbolicAxis symbolicAxis1 = new VerticalSymbolicAxis(yAxisLabel, ((YisSymbolic)data1).getYSymbolicValues());
			VerticalSymbolicAxis symbolicAxis2 = new VerticalSymbolicAxis(yAxisLabel, ((YisSymbolic)data2).getYSymbolicValues());
			symbolicAxis1.setCrosshairVisible(false);
			symbolicAxis2.setCrosshairVisible(false);
			valueAxis.setCrosshairVisible(false);

			// make a vertically combined plot
			CombinedPlot combinedPlot = new CombinedPlot(valueAxis, CombinedPlot.VERTICAL);

			// add the sub-plots
			combinedPlot.add(createCombinableXYChart(valueAxis, symbolicAxis1, series0, 0));
			combinedPlot.add(createCombinableXYChart(valueAxis, symbolicAxis1, series1, 0));
			combinedPlot.add(createCombinableXYChart(valueAxis, symbolicAxis1, series2, 0));
			combinedPlot.add(createCombinableXYChart(valueAxis, symbolicAxis1, series3, 0));
			combinedPlot.add(createCombinableXYChart(valueAxis, symbolicAxis2, series4, 0));
			combinedPlot.add(createCombinableXYChart(valueAxis, symbolicAxis2, series5, 0));
			combinedPlot.add(createCombinableXYChart(valueAxis, symbolicAxis2, series6, 0));
			combinedPlot.add(createCombinableXYChart(valueAxis, symbolicAxis2, series7, 0));


			// call this method after all sub-plots have been added
			combinedPlot.adjustPlots();

			// make the top level JFreeChart object
			chart = new JFreeChart(data, combinedPlot, title, JFreeChart.DEFAULT_TITLE_FONT, true);
		}
		catch (AxisNotCompatibleException e) {
			// this won't happen unless you mess with the axis constructors above
			System.err.println("axis not compatible: " + e);
		}
		catch (PlotNotCompatibleException e) {
			// this won't happen unless you mess with the axis constructors above
			System.err.println("axis not compatible: " + e);
		}

		// then customise it a little...
		chart.setBackgroundPaint(new GradientPaint(0, 0, Color.white,0, 1000, Color.blue));
	
		// and present it in a frame...
		JFrame ySymbolicFrame = new JFreeChartFrame("XYPlot", chart);
		ySymbolicFrame.pack();
		JRefineryUtilities.positionFrameRandomly(ySymbolicFrame);
		ySymbolicFrame.show();
    }

	/**
	 * Horizontally combined sample1 and sample2 and display it.
	 */
    private static void displayYSymbolicCombinedHorizontally(SampleYSymbolicDataset data1, SampleYSymbolicDataset data2) {
		String title = "Animals Horizontally Combined";
		String xAxisLabel = "Miles";
		String yAxisLabel = "Animal";

		//Combine the y symbolic values of the two data sets
		String[] combinedYSymbolicValues = SampleYSymbolicDataset.combineYSymbolicDataset((YisSymbolic)data1, (YisSymbolic)data2);

		// make master Dataset as combination of the 2 dataset
		CombinedDataset data = new CombinedDataset();
		data.add(data1);
		data.add(data2);

		// decompose data into its height dataset series
		SeriesDataset series0 = new SubSeriesDataset(data, 0);
		SeriesDataset series1 = new SubSeriesDataset(data, 1);
		SeriesDataset series2 = new SubSeriesDataset(data, 2);
		SeriesDataset series3 = new SubSeriesDataset(data, 3);
		SeriesDataset series4 = new SubSeriesDataset(data, 4);
		SeriesDataset series5 = new SubSeriesDataset(data, 5);
		SeriesDataset series6 = new SubSeriesDataset(data, 6);
		SeriesDataset series7 = new SubSeriesDataset(data, 7);

		JFreeChart chart = null;

		try {
			// common horizontal and vertical axes
			ValueAxis valueAxis1 = new HorizontalNumberAxis(xAxisLabel);
			ValueAxis valueAxis2 = new HorizontalNumberAxis(xAxisLabel);
			VerticalSymbolicAxis symbolicAxis = new VerticalSymbolicAxis(yAxisLabel, combinedYSymbolicValues);
			symbolicAxis.setCrosshairVisible(false);
			valueAxis1.setCrosshairVisible(false);
			valueAxis2.setCrosshairVisible(false);

			// make a horizontally combined plot
			CombinedPlot combinedPlot = new CombinedPlot(symbolicAxis, CombinedPlot.HORIZONTAL);

			// add the sub-plots
			combinedPlot.add(createCombinableXYChart(valueAxis1, symbolicAxis, series0, 0));
			combinedPlot.add(createCombinableXYChart(valueAxis1, symbolicAxis, series1, 0));
			combinedPlot.add(createCombinableXYChart(valueAxis1, symbolicAxis, series2, 0));
			combinedPlot.add(createCombinableXYChart(valueAxis1, symbolicAxis, series3, 0));
			combinedPlot.add(createCombinableXYChart(valueAxis2, symbolicAxis, series4, 0));
			combinedPlot.add(createCombinableXYChart(valueAxis2, symbolicAxis, series5, 0));
			combinedPlot.add(createCombinableXYChart(valueAxis2, symbolicAxis, series6, 0));
			combinedPlot.add(createCombinableXYChart(valueAxis2, symbolicAxis, series7, 0));

			// call this method after all sub-plots have been added
			combinedPlot.adjustPlots();

			// make the top level JFreeChart object
			chart = new JFreeChart(data, combinedPlot, title, JFreeChart.DEFAULT_TITLE_FONT, true);
		}
		catch (AxisNotCompatibleException e) {
			// this won't happen unless you mess with the axis constructors above
			System.err.println("axis not compatible: " + e);
		}
		catch (PlotNotCompatibleException e) {
			// this won't happen unless you mess with the axis constructors above
			System.err.println("axis not compatible: " + e);
		}

		// then customise it a little...
		chart.setBackgroundPaint(new GradientPaint(0, 0, Color.white,0, 1000, Color.blue));
	
		// and present it in a frame...
		JFrame ySymbolicFrame = new JFreeChartFrame("XYPlot", chart);
		ySymbolicFrame.pack();
		JRefineryUtilities.positionFrameRandomly(ySymbolicFrame);
		ySymbolicFrame.show();
    }

	/**
	 * Overlaid sample1 and sample2 and display it.
	 */
    private static void displayYSymbolicOverlaid(XYDataset data1, XYDataset data2) {
		String title = "Animals Overlaid";
		String xAxisLabel = "Miles";
		String yAxisLabel = "Animal";

		//Combine the y symbolic values of the two data sets
		String[] combinedYSymbolicValues = SampleYSymbolicDataset.combineYSymbolicDataset((YisSymbolic)data1, (YisSymbolic)data2);

		// make master Dataset as combination of the 2 dataset
		CombinedDataset data = new CombinedDataset();
		data.add(data1);
		data.add(data2);

		// decompose data into its four dataset series
		SeriesDataset series0 = new SubSeriesDataset(data, 0);
		SeriesDataset series1 = new SubSeriesDataset(data, 1);
		SeriesDataset series2 = new SubSeriesDataset(data, 2);
		SeriesDataset series3 = new SubSeriesDataset(data, 3);
		SeriesDataset series4 = new SubSeriesDataset(data, 4);
		SeriesDataset series5 = new SubSeriesDataset(data, 5);
		SeriesDataset series6 = new SubSeriesDataset(data, 6);
		SeriesDataset series7 = new SubSeriesDataset(data, 7);

		JFreeChart chart = null;

		try {
			// common horizontal and vertical axes
			ValueAxis valueAxis = new HorizontalNumberAxis(xAxisLabel);
			VerticalSymbolicAxis symbolicAxis = new VerticalSymbolicAxis(yAxisLabel, combinedYSymbolicValues);
			symbolicAxis.setCrosshairVisible(false);
			valueAxis.setCrosshairVisible(false);

			// make an overlaid CombinedPlot
			OverlaidPlot overlaidPlot = new OverlaidPlot(valueAxis, symbolicAxis);

			// add the sub-plots
			overlaidPlot.add(createCombinableXYChart(valueAxis, symbolicAxis, series0, 0));
			overlaidPlot.add(createCombinableXYChart(valueAxis, symbolicAxis, series1, 0));
			overlaidPlot.add(createCombinableXYChart(valueAxis, symbolicAxis, series2, 0));
			overlaidPlot.add(createCombinableXYChart(valueAxis, symbolicAxis, series3, 0));
			overlaidPlot.add(createCombinableXYChart(valueAxis, symbolicAxis, series4, 0));
			overlaidPlot.add(createCombinableXYChart(valueAxis, symbolicAxis, series5, 0));
			overlaidPlot.add(createCombinableXYChart(valueAxis, symbolicAxis, series6, 0));
			overlaidPlot.add(createCombinableXYChart(valueAxis, symbolicAxis, series7, 0));

			// call this method after all sub-plots have been added
			overlaidPlot.adjustPlots();

			// make the top level JFreeChart object
			chart = new JFreeChart(data, overlaidPlot, title, JFreeChart.DEFAULT_TITLE_FONT, true);
		}
		catch (AxisNotCompatibleException e) {
			// this won't happen unless you mess with the axis constructors above
			System.err.println("axis not compatible: " + e);
		}
		catch (PlotNotCompatibleException e) {
			// this won't happen unless you mess with the axis constructors above
			System.err.println("axis not compatible: " + e);
		}

		// then customise it a little...
		chart.setBackgroundPaint(new GradientPaint(0, 0, Color.white,0, 1000, Color.blue));
	
		// and present it in a frame...
		JFrame ySymbolicFrame = new JFreeChartFrame("XYPlot", chart);
		ySymbolicFrame.pack();
		JRefineryUtilities.positionFrameRandomly(ySymbolicFrame);
		ySymbolicFrame.show();
    }

	/**
	 * Creates a XY graph with symbolic value on Y axis.
	 */
	public static JFreeChart createYSymbolicPlot(String title, String xAxisLabel,
                                                   String yAxisLabel, XYDataset data,
                                                   boolean legend) {
		ValueAxis valueAxis = new HorizontalNumberAxis(xAxisLabel);
        VerticalSymbolicAxis symbolicAxis = new VerticalSymbolicAxis(yAxisLabel, ((YisSymbolic)data).getYSymbolicValues());

		XYPlot plot = new XYPlot(valueAxis, symbolicAxis);
        plot.setXYItemRenderer(new StandardXYItemRenderer(StandardXYItemRenderer.SHAPES));
		plot.setToolTipGenerator(new SymbolicXYToolTipGenerator());
        JFreeChart chart = new JFreeChart(data, plot, title, JFreeChart.DEFAULT_TITLE_FONT, legend);
        return chart;
	}

	/**
	 *	Creates a combinated XY graph.
	 */
	public static CombinedChart createCombinableXYChart(ValueAxis horizontal, ValueAxis vertical,
                                                        Dataset data, int shapeType) {
        XYPlot plot = new XYPlot(horizontal, vertical);
        plot.setXYItemRenderer(new StandardXYItemRenderer(StandardXYItemRenderer.SHAPES));
        return ChartFactory.createCombinableChart(data, plot);
	}

	public static SampleYSymbolicDataset createYSymbolicSample1() {
		String[] sData = {"Lion", "Elephant", "Monkey", "Hippopotamus","Giraffe"};
		SampleYSymbolicDataset ySymbolicData = new SampleYSymbolicDataset("AY Sample", 20, sData, 4, 20, new String[] {"A Fall","A Spring","A Summer","A Winter"});
		return ySymbolicData;
	}

	public static SampleYSymbolicDataset createYSymbolicSample2() {
		String[] sData = {"Giraffe", "Gazelle", "Zebra", "Gnu"};
		SampleYSymbolicDataset ySymbolicData = new SampleYSymbolicDataset("BY Sample", 40, sData, 4, 20, new String[] {"B Fall","B Spring","B Summer","B Winter"});
		return ySymbolicData;
	}

	public static void main(String[] args) 	{

		SampleYSymbolicDataset s1 = createYSymbolicSample1();
		SampleYSymbolicDataset s2 = createYSymbolicSample2();

		displayYSymbolic(s1, "Animal A", "Miles", "Animal");
		displayYSymbolic(s2, "Animal B", "Miles", "Animal");
		displayYSymbolicCombinedVertically((SampleYSymbolicDataset)s1.clone(), (SampleYSymbolicDataset)s2.clone());
		displayYSymbolicCombinedHorizontally((SampleYSymbolicDataset)s1.clone(), (SampleYSymbolicDataset)s2.clone());
		displayYSymbolicOverlaid((SampleYSymbolicDataset)s1.clone(), (SampleYSymbolicDataset)s2.clone());
	}

}
