package org.jfree.chart.demo;

import org.jfree.data.XYSeries;

/*
 * Created on Jul 23, 2003
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
public class SeriesUpdater implements Runnable {

	public XYSeries series_;
	public static int nXPos_ = 0;
	public static int nYValue_ = 0;	
	
	
	public SeriesUpdater(XYSeries series) {
		series_=series;
	}
	
	private void updateDataAndAddDataToSeries() {
		nXPos_+=1;
		nYValue_+=20;
		if(nYValue_>60) {
			nYValue_=-30;
		}
		series_.add(nXPos_, nYValue_);
		
	}	

	public void run() {
		while (true) {
			try {
				Thread.sleep(2000);
				System.out.println("thread called");
				updateDataAndAddDataToSeries();	
				//data.setTranslate(data.getTranslate() + 0.25);
			}
			catch (Exception e) {
				// ignore
			}
	  }
	}
}
