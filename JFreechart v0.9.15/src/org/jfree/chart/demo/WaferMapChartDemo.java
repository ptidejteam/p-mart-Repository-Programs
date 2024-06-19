package org.jfree.chart.demo;

/*
 * Created on Nov 8, 2003
 * Author: Robert Redburn
 */

import java.awt.Color;
import java.awt.Font;
import java.awt.GradientPaint;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.Legend;
import org.jfree.chart.TextTitle;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.WaferMapDataset;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;

/**
 * 
 */
public class WaferMapChartDemo extends ApplicationFrame {

	public static void main(String[] args) {
		WaferMapChartDemo demo = new WaferMapChartDemo("Wafer Map Demo");
		demo.pack();
		RefineryUtilities.centerFrameOnScreen(demo);
		demo.setVisible(true);
	}

    public WaferMapChartDemo(String title) {
	    super(title);
		WaferMapDataset dataset = waferdata();
		JFreeChart chart = ChartFactory.createWaferMapChart(
            "Wafer Map Demo", // title
		    dataset, // wafermapdataset
		    PlotOrientation.VERTICAL, // vertical = notchdown
		    true, // legend                       // horizontal = notchright
		    false, // tooltips
	        false); // urls
		Legend legend = chart.getLegend();
		legend.setAnchor(Legend.EAST);
		chart.setBackgroundPaint(
			new GradientPaint(0, 0, Color.white, 0, 1000, Color.blue));

		TextTitle copyright =
			new TextTitle(
				"JFreeChart WaferMapPlot",
				new Font("SansSerif", Font.PLAIN, 9));
		copyright.setPosition(TextTitle.BOTTOM);
		copyright.setHorizontalAlignment(TextTitle.RIGHT);
		chart.addSubtitle(copyright);

		ChartPanel chartPanel = new ChartPanel(chart);
		chartPanel.setPreferredSize(new java.awt.Dimension(500, 400));
		setContentPane(chartPanel);
	}

	/**
	 * generates a WaferMapDataset
	 * 
	 * @return
	 */
	private WaferMapDataset waferdata() {
		WaferMapDataset data = new WaferMapDataset(30, 20);
		data.addValue(1, 5, 14); // (value, chipx, chipy)
		data.addValue(2, 5, 13);
		data.addValue(3, 5, 12);
		data.addValue(4, 5, 11);
		data.addValue(5, 5, 10);
		data.addValue(6, 5, 9);
		data.addValue(7, 5, 8);
		data.addValue(8, 5, 7);
		data.addValue(9, 5, 6);
		data.addValue(10, 6, 10);
		data.addValue(11, 7, 10);
		data.addValue(12, 8, 10);
		data.addValue(13, 9, 10);
		data.addValue(14, 10, 10);
		data.addValue(15, 11, 10);
		data.addValue(16, 11, 11);
		data.addValue(17, 11, 12);
		data.addValue(18, 11, 13);
		data.addValue(19, 11, 14);
		data.addValue(20, 11, 9);
		data.addValue(21, 11, 8);
		data.addValue(22, 11, 7);
		data.addValue(23, 11, 6);

		data.addValue(6, 16, 6);
		data.addValue(6, 17, 6);
		data.addValue(6, 18, 6);
		data.addValue(6, 19, 6);
		data.addValue(6, 20, 6);
		data.addValue(6, 21, 6);
		data.addValue(6, 22, 6);
		data.addValue(3, 19, 7);
		data.addValue(3, 19, 8);
		data.addValue(3, 19, 9);
		data.addValue(3, 19, 10);
		data.addValue(3, 19, 11);
		data.addValue(3, 19, 12);
		data.addValue(3, 19, 13);
		data.addValue(4, 19, 14);
		data.addValue(4, 18, 14);
		data.addValue(4, 17, 14);
		data.addValue(4, 16, 14);
		data.addValue(4, 20, 14);
		data.addValue(4, 21, 14);
		data.addValue(4, 22, 14);
		return data;
	} // end method waferdata

} // end class wafermapchartdemo
