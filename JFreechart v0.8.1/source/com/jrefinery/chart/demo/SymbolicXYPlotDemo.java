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
 * SymbolicXYPlotDemo.java
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

public class SymbolicXYPlotDemo {

	/**
	 * Displays an XYPlot with Y symbolic data.
	 */
    private static void displayXYSymbolic(XYDataset data, String title, String xAxisLabel, String yAxisLabel) {
		JFreeChart chart = createXYSymbolicPlot(title, xAxisLabel, yAxisLabel, data, true);

		chart.setBackgroundPaint(new GradientPaint(0, 0, Color.white, 1000, 0, Color.green));

		JFrame ySymbolicFrame = new JFreeChartFrame("XYPlot", chart);
		ySymbolicFrame.pack();
		JRefineryUtilities.positionFrameRandomly(ySymbolicFrame);
		ySymbolicFrame.show();
    }

	/**
	 * Vertically combined sample1 and sample2 and display it.
	 */
    private static void displayXYSymbolicCombinedVertically(XYDataset data1, XYDataset data2) {
		String title = "Pollutant Vertically Combined";
		String xAxisLabel = "Contamination and Type";
		String yAxisLabel = "Pollutant";
		
		//Combine the x symbolic values of the two data sets
		String[] combinedXSymbolicValues = SampleXYSymbolicDataset.combineXSymbolicDataset((XisSymbolic)data1, (XisSymbolic)data2);
		
		// make master Dataset as combination of the 2 dataset
		CombinedDataset data = new CombinedDataset();
		data.add(data1);
		data.add(data2);

		// decompose data into its four dataset series
		SeriesDataset series0 = new SubSeriesDataset(data, 0);
		SeriesDataset series1 = new SubSeriesDataset(data, 1);

		JFreeChart chart = null;

		try {
			// common horizontal and vertical axes
			HorizontalSymbolicAxis hsymbolicAxis = new HorizontalSymbolicAxis(xAxisLabel, combinedXSymbolicValues);
			VerticalSymbolicAxis vsymbolicAxis1 = new VerticalSymbolicAxis(yAxisLabel, ((YisSymbolic)data1).getYSymbolicValues());
			VerticalSymbolicAxis vsymbolicAxis2 = new VerticalSymbolicAxis(yAxisLabel, ((YisSymbolic)data2).getYSymbolicValues());
			vsymbolicAxis1.setCrosshairVisible(false);
			vsymbolicAxis2.setCrosshairVisible(false);
			hsymbolicAxis.setCrosshairVisible(false);

			// make a vertically combined plot
			CombinedPlot combinedPlot = new CombinedPlot(hsymbolicAxis, CombinedPlot.VERTICAL);

			// add the sub-plots
			combinedPlot.add(createCombinableXYChart(hsymbolicAxis, vsymbolicAxis1, series0, 0));
			combinedPlot.add(createCombinableXYChart(hsymbolicAxis, vsymbolicAxis2, series1, 0));

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
	 * Displays an horizontally combined XYPlot with X and Y symbolic data.
	 */
    private static void displayXYSymbolicCombinedHorizontally(XYDataset data1, XYDataset data2) {
		String title = "Pollutant Horizontally Combined";
		String x1AxisLabel = "Contamination";
		String x2AxisLabel = "Type";
		String yAxisLabel = "Pollutant";

		//Combine the y symbolic values of the two data sets
		String[] combinedYSymbolicValues = SampleXYSymbolicDataset.combineYSymbolicDataset((YisSymbolic)data1, (YisSymbolic)data2);

		// make master Dataset as combination of the 2 dataset
		CombinedDataset data = new CombinedDataset();
		data.add(data1);
		data.add(data2);

		// decompose data into its four dataset series
		SeriesDataset series0 = new SubSeriesDataset(data, 0);
		SeriesDataset series1 = new SubSeriesDataset(data, 1);

		JFreeChart chart = null;

		try {
			// common horizontal and vertical axes
			HorizontalSymbolicAxis hsymbolicAxis1 = new HorizontalSymbolicAxis(x1AxisLabel, ((XisSymbolic)data1).getXSymbolicValues());
			HorizontalSymbolicAxis hsymbolicAxis2 = new HorizontalSymbolicAxis(x2AxisLabel, ((XisSymbolic)data2).getXSymbolicValues());
			VerticalSymbolicAxis symbolicAxis = new VerticalSymbolicAxis(yAxisLabel, combinedYSymbolicValues);
			symbolicAxis.setCrosshairVisible(false);
			hsymbolicAxis1.setCrosshairVisible(false);
			hsymbolicAxis2.setCrosshairVisible(false);

			// make a horizontally combined plot
			CombinedPlot combinedPlot = new CombinedPlot(symbolicAxis, CombinedPlot.HORIZONTAL);

			// add the sub-plots
			combinedPlot.add(createCombinableXYChart(hsymbolicAxis1, symbolicAxis, series0, 0));
			combinedPlot.add(createCombinableXYChart(hsymbolicAxis2, symbolicAxis, series1, 0));

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
	 * Displays an overlaid XYPlot with X and Y symbolic data.
	 */
    private static void displayXYSymbolicOverlaid(XYDataset data1, XYDataset data2) {
		String title = "Pollutant Overlaided";
		String xAxisLabel = "Contamination and Type";
		String yAxisLabel = "Pollutant";

		//Combine the x symbolic values of the two data sets
		String[] combinedXSymbolicValues = SampleXYSymbolicDataset.combineXSymbolicDataset((XisSymbolic)data1, (XisSymbolic)data2);

		//Combine the y symbolic values of the two data sets
		String[] combinedYSymbolicValues = SampleXYSymbolicDataset.combineYSymbolicDataset((YisSymbolic)data1, (YisSymbolic)data2);

		// make master Dataset as combination of the 2 dataset
		CombinedDataset data = new CombinedDataset();
		data.add(data1);
		data.add(data2);

		// decompose data into its four dataset series
		SeriesDataset series0 = new SubSeriesDataset(data, 0);
		SeriesDataset series1 = new SubSeriesDataset(data, 1);

		JFreeChart chart = null;

		try {
			// common horizontal and vertical axes
			HorizontalSymbolicAxis hsymbolicAxis = new HorizontalSymbolicAxis(xAxisLabel, combinedXSymbolicValues);
			VerticalSymbolicAxis vsymbolicAxis = new VerticalSymbolicAxis(yAxisLabel, combinedYSymbolicValues);
			hsymbolicAxis.setCrosshairVisible(false);
			vsymbolicAxis.setCrosshairVisible(false);

			// make an overlaid CombinedPlot
			OverlaidPlot overlaidPlot = new OverlaidPlot(hsymbolicAxis, vsymbolicAxis);

			// add the sub-plots
			overlaidPlot.add(createCombinableXYChart(hsymbolicAxis, vsymbolicAxis, series0, 0));
			overlaidPlot.add(createCombinableXYChart(hsymbolicAxis, vsymbolicAxis, series1, 0));

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
	 * Creates a XY graph with symbolic value on X and Y axis.
	 */
	public static JFreeChart createXYSymbolicPlot(String title, String xAxisLabel,
                                                   String yAxisLabel, XYDataset data,
                                                   boolean legend) {
		HorizontalSymbolicAxis xSymbolicAxis = new HorizontalSymbolicAxis(xAxisLabel, ((XisSymbolic)data).getXSymbolicValues());
        VerticalSymbolicAxis ySymbolicAxis = new VerticalSymbolicAxis(yAxisLabel, ((YisSymbolic)data).getYSymbolicValues());

		XYPlot plot = new XYPlot(xSymbolicAxis, ySymbolicAxis);
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
		SampleYSymbolicDataset ySymbolicData = new SampleYSymbolicDataset("AY Sample", 20, sData, 4, 20, new String[] {"Fall","Spring","Summer","Winter"});
		return ySymbolicData;
	}

	public static SampleYSymbolicDataset createYSymbolicSample2() {
		String[] sData = {"Giraffe", "Gazelle", "Zebra", "Gnu"};
		SampleYSymbolicDataset ySymbolicData = new SampleYSymbolicDataset("BY Sample", 40, sData, 4, 10, new String[] {"Fall","Spring","Summer","Winter"});
		return ySymbolicData;
	}

	public static SampleXYSymbolicDataset createXYSymbolicSample1(){
		String[] xsData = {"Atmosphere", "Continental Ecosystem", "Limnic Ecosystem", "Marine Ecosystem"};
		String[] ysData = {"Ionizing radiations", "Thermic pollutants", "Hydrocarbon", "Synthetic materials","Pesticides", "Detergent", 
							"Synthetic organic materials", "Sulphur", "Nitrate", "Phosphate", "Heavy metals", "Fluors", "Aerosols",
							"Dead organic materials", "Pathogen micro-organisms"};
		int[][] xd = {{0,0,0,0,0,0,0,0,1,1,1,1,1,1,1,1,1,1,1,2,2,2,2,2,2,2,2,2,2,2,2,3,3,3,3,3,3,3,3,3,3,3}};
		int[][] yd = {{0,2,3,7,10,11,12,14,0,2,3,4,7,8,9,10,11,12,14,0,1,2,3,4,5,7,8,9,10,13,14,0,1,2,3,4,5,8,9,10,13,14}};
		Integer[][] xData = (Integer[][])toArray(xd);
		Integer[][] yData = (Integer[][])toArray(yd);
		SampleXYSymbolicDataset xySymbolicData = new SampleXYSymbolicDataset("AXY Sample", xData, yData, xsData, ysData, new String[] {"A"});
		return xySymbolicData;
	}

	public static SampleXYSymbolicDataset createXYSymbolicSample2(){
		String[] xsData = {"Physic pollutant", "Chemical pollutant", "Biological pollutant"};
		String[] ysData = {"Ionizing radiations", "Thermic pollutants", "Hydrocarbon", "Synthetic materials","Pesticides", "Detergent", 
							"Synthetic organic materials", "Sulphur", "Nitrate", "Phosphate", "Heavy metals", "Fluors", "Aerosols",
							"Dead organic materials", "Pathogen micro-organisms"};
		int[][] xd = {{0,0,1,1,1,1,1,1,1,1,1,1,1,2,2}};
		int[][] yd = {{0,1,2,3,4,5,6,7,8,9,10,11,12,13,14}};
		Integer[][] xData = (Integer[][])toArray(xd);
		Integer[][] yData = (Integer[][])toArray(yd);
		SampleXYSymbolicDataset xySymbolicData = new SampleXYSymbolicDataset("BXY Sample", xData, yData, xsData, ysData, new String[] {"B"});
		return xySymbolicData;
	}

	public static void main(String[] args) 	{
		SampleXYSymbolicDataset s1 = createXYSymbolicSample1();
		SampleXYSymbolicDataset s2 = createXYSymbolicSample2();

		displayXYSymbolic(s1, "Pollutant", "contamination", "pollutant");
		displayXYSymbolic(s2, "Pollutant", "type", "pollutant");
		displayXYSymbolicCombinedVertically((SampleXYSymbolicDataset)s1.clone(), (SampleXYSymbolicDataset)s2.clone());
		displayXYSymbolicCombinedHorizontally((SampleXYSymbolicDataset)s1.clone(), (SampleXYSymbolicDataset)s2.clone());
		displayXYSymbolicOverlaid((SampleXYSymbolicDataset)s1.clone(), (SampleXYSymbolicDataset)s2.clone());
	}

	/**
	 *	Transform an primitive array to an object array
	 */
	private static Object toArray(Object arr) {

		if (arr == null) {
			return arr;
		}

		Class cls = arr.getClass();
		if (!cls.isArray()) {
			return arr;
		}

		Class compType = cls.getComponentType();
		int dim = 1;
		while (!compType.isPrimitive()) {	
			if (!compType.isArray()) return arr;
			else {
				dim++;
				compType=compType.getComponentType();
			}
		}

		int[] length = new int[dim];
		length[0] = Array.getLength(arr);
		Object[] newarr = null;
	
		try {
			if (compType.equals(Integer.TYPE)) newarr = (Object[])Array.newInstance(Class.forName("java.lang.Integer"), length);
			else if (compType.equals(Double.TYPE)) newarr = (Object[])Array.newInstance(Class.forName("java.lang.Double"), length);
			else if (compType.equals(Long.TYPE)) newarr = (Object[])Array.newInstance(Class.forName("java.lang.Long"), length);
			else if (compType.equals(Float.TYPE)) newarr = (Object[])Array.newInstance(Class.forName("java.lang.Float"), length);
			else if (compType.equals(Short.TYPE)) newarr = (Object[])Array.newInstance(Class.forName("java.lang.Short"), length);
			else if (compType.equals(Byte.TYPE)) newarr = (Object[])Array.newInstance(Class.forName("java.lang.Byte"), length);
			else if (compType.equals(Character.TYPE)) newarr = (Object[])Array.newInstance(Class.forName("java.lang.Character"), length);
			else if (compType.equals(Boolean.TYPE)) newarr = (Object[])Array.newInstance(Class.forName("java.lang.Boolean"), length);
		}
		catch (ClassNotFoundException ex) {
			System.out.println(ex);
		}

		for (int i = 0; i < length[0]; i++) {
			if (dim!=1) {
				newarr[i] = toArray(Array.get(arr, i));				
			} else {
				newarr[i] = Array.get(arr, i);
			}
		}
		return newarr;
     }
}
