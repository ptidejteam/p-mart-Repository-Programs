package org.jfree.chart.demo;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.XYDataset;
import org.jfree.data.XYSeries;
import org.jfree.data.XYSeriesCollection;

/*
 * Created on Jul 21, 2003
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */

/**
 * @author administrator
 *
 * To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public class SimpleTestingJFreeChart {

	public static XYSeries series_ = new XYSeries("Cost");
	public static XYDataset xyDataset_ = new XYSeriesCollection(series_);
	public static int nXPos_ = 0;
	public static int nYValue_ = 0;	

	public static void main(String[] args) {
		testJFreeChartXYChart1();
	}
	
	private static void testJFreeChartXYChart1() {
		
		series_.setMaximumItemCount(100);

		SeriesUpdater seriesUpdater = new SeriesUpdater(series_);		
		Thread thread = new Thread(seriesUpdater);
		thread.start();
		
		ChartPanel chartPanelAreaXY = makeAreaXYChart(xyDataset_);
		ChartPanel chartPanelLineXY = makeLineXYChart(xyDataset_);
		
		JFrame frame = new JFrame();
		//frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE); // make sure we exit the app when the window is closed
		setupJFrameUsingGridBagLayout(frame, chartPanelLineXY, chartPanelAreaXY);							
							
		final int frameWidth = 300;
		final int frameHeight = 300;
		frame.setSize(frameWidth, frameHeight);
		frame.setVisible(true);		
	}
	

	public static void addNewDataToSeries() {
		series_.add(19.0, 55.0);
		series_.add(200.22, 130.5);
		
		// show how many items we're trying to display
		final int nNbrItems =series_.getItemCount(); 
		System.out.println(nNbrItems);
//		for(int n = 0; n < nNbrItems; ++n) {
//			XYDataPair xyDataPair = series_.getDataPair(n);
//			System.out.print("["+xyDataPair.getX()+","+xyDataPair.getY()+"], ");
//		}
//		System.out.println();
	}
	

	public static void setupJFrameUsingGridBagLayout(JFrame frame, ChartPanel chartPanelLineXY, ChartPanel chartPanelAreaXY) {
		frame.getContentPane().setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		c.insets = new Insets(5,5,5,5);

		// add a button that'll update the dataset with
		// each press
		JButton updateButton = new JButton();
		updateButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.out.println("pressed");
				addNewDataToSeries();		
			}
		});
		
		updateButton.setText("Add Data To Chart");
		c.gridx=0;
		c.gridy=0;
		c.gridwidth=1;
		c.gridheight=1;
		c.weightx=0.0;
		c.weighty=0.0;
		frame.getContentPane().add(updateButton, c);
		
	
		c.gridx=0;
		c.gridy=1;
		c.gridwidth=1;
		c.gridheight=1;
		c.weightx=1.0;
		c.weighty=1.0;
		frame.getContentPane().add(chartPanelLineXY,c);
		
		c.gridx=0;
		c.gridy=2;
		c.gridwidth=1;
		c.gridheight=1;
		c.weightx=1.0;
		c.weighty=1.0;
		frame.getContentPane().add(chartPanelAreaXY, c);
				
	}

	
	public static ChartPanel makeLineXYChart(XYDataset xyDataset) {
		JFreeChart chart = ChartFactory.createXYLineChart
							 ("Schedule Cost Over Time",  // Title
							  "Time",           // X-Axis label
							  "Cost",           // Y-Axis label
							  xyDataset,          // Dataset
                              PlotOrientation.VERTICAL,
							  true ,               // Show legend, 
							  true, false
							 );

		// set a paint colour, alpha blend background directly into the white/green blend
		chart.setBackgroundPaint(new GradientPaint(0, 0, Color.white, 0, 1000, Color.blue));
		XYPlot plot = (XYPlot) chart.getPlot();
		plot.setBackgroundAlpha(0.0f);

		ChartPanel chartPanel = createChartPanel(chart);

		return chartPanel;		
	}

	public static ChartPanel makeAreaXYChart(XYDataset xyDataset) {
		JFreeChart chart = ChartFactory.createXYAreaChart
							 ("Schedule Cost Over Time",  // Title
							  "Time",           // X-Axis label
							  "Cost",           // Y-Axis label
							  xyDataset,          // Dataset
                              PlotOrientation.VERTICAL,
							  true ,               // Show legend, 
							  true, false
							 );

		// set a background image and alpha blending					
		//chart.setBackgroundImageAlpha(0.5f);// doesn't do much, as there is nothing in background		 
		chart.setBackgroundImage(JFreeChart.INFO.getLogo());
		XYPlot plot = (XYPlot) chart.getPlot();
		plot.setBackgroundAlpha(0.65f);

		ChartPanel chartPanel = createChartPanel(chart);

		return chartPanel;		
	}

	private static ChartPanel createChartPanel(JFreeChart chart) {
		ChartPanel chartPanel = new ChartPanel(chart);
		chartPanel.setPreferredSize(new java.awt.Dimension(200, 200));
		chartPanel.setEnforceFileExtensions(false);
		return chartPanel;
	}


}
