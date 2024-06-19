/* ======================================
 * JFreeChart : a free Java chart library
 * ======================================
 *
 * Project Info:  http://www.jfree.org/jfreechart/index.html
 * Project Lead:  David Gilbert (david.gilbert@object-refinery.com);
 *
 * (C) Copyright 2000-2003, by Object Refinery Limited and Contributors.
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
 * ---------------------------
 * CyclicXYItemRenderer.java
 * ---------------------------
 * (C) Copyright 2003, by Nicolas Brodu and Contributors.
 *
 * Original Author:  Nicolas Brodu;
 * Contributor(s): -
 *
 * $Id: CyclicXYItemRenderer.java,v 1.1 2007/10/10 19:21:54 vauchers Exp $
 *
 * Changes
 * -------
 * 19-Nov-2003 : Initial import to JFreeChart from the JSynoptic project (NB);
 * 
 */

package org.jfree.chart.renderer;

import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;

import org.jfree.chart.CrosshairInfo;
import org.jfree.chart.axis.CyclicNumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.labels.XYToolTipGenerator;
import org.jfree.chart.plot.PlotRenderingInfo;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.urls.XYURLGenerator;
import org.jfree.data.DatasetChangeListener;
import org.jfree.data.DatasetGroup;
import org.jfree.data.XYDataset;

/**
 * The Cyclic XY item renderer is specially designed to handle cyclic axis. 
 * While the standard renderer would draw a line across the plot when a cycling occurs, the cyclic
 * renderer splits the line at each cycle end instead. This is done by interpolating new points at 
 * cycle boundary. Thus, correct appearance is restored. 
 * 
 * The Cyclic XY item renderer works exactly like a standard XY item renderer with non-cyclic axis. 
 *
 * @author Nicolas Brodu
 */
public class CyclicXYItemRenderer extends StandardXYItemRenderer {

	/** Overloaded contructor. See StandardXYItemRenderer */
	public CyclicXYItemRenderer() {
		super();
	}

	/** Overloaded contructor. See StandardXYItemRenderer */
	public CyclicXYItemRenderer(int type) {
		super(type);
	}

	/** Overloaded contructor. See StandardXYItemRenderer */
	public CyclicXYItemRenderer(int type, XYToolTipGenerator toolTipGenerator) {
		super(type, toolTipGenerator);
	}

	/** Overloaded contructor. See StandardXYItemRenderer */
	public CyclicXYItemRenderer(int type, XYToolTipGenerator toolTipGenerator,
			XYURLGenerator urlGenerator) {
		super(type, toolTipGenerator, urlGenerator);
	}

	
	/** Draws the visual representation of a single data item.
	 * When using cyclic axis, do not draw a line from right to left when cycling as would a 
	 * standard XY item renderer, but instead draw a line from the previous point to the cycle bound 
	 * in the last cycle, and a line from the cycle bound to current point in the current cycle.  
	 * @see org.jfree.chart.renderer.XYItemRenderer#drawItem(java.awt.Graphics2D, org.jfree.chart.renderer.XYItemRendererState, java.awt.geom.Rectangle2D, org.jfree.chart.plot.PlotRenderingInfo, org.jfree.chart.plot.XYPlot, org.jfree.chart.axis.ValueAxis, org.jfree.chart.axis.ValueAxis, org.jfree.data.XYDataset, int, int, org.jfree.chart.CrosshairInfo, int)
	 */
	public void drawItem(Graphics2D g2, XYItemRendererState state,
			Rectangle2D dataArea, PlotRenderingInfo info, XYPlot plot,
			ValueAxis domainAxis, ValueAxis rangeAxis, XYDataset dataset,
			int series, int item, CrosshairInfo crosshairInfo, int pass) {

		if ((!getPlotLines()) || ((!(domainAxis instanceof CyclicNumberAxis)) && (!(rangeAxis instanceof CyclicNumberAxis))) || (item<=0)) {
			super.drawItem(g2, state, dataArea, info, plot, domainAxis, rangeAxis, dataset, series, item, crosshairInfo, pass);
			return;
		}

		// get the previous data point...
		Number xn = dataset.getXValue(series, item - 1);
		Number yn = dataset.getYValue(series, item - 1);
		// If null, don't draw line => then delegate to parent
		if (yn == null || xn == null) {
			super.drawItem(g2, state, dataArea, info, plot, domainAxis, rangeAxis, dataset, series, item, crosshairInfo, pass);
			return;
		}
		double[] x = new double[2];
		double[] y = new double[2];
		x[0] = xn.doubleValue();
		y[0] = yn.doubleValue();
		
		// get the data point...
		xn = dataset.getXValue(series, item);
		yn = dataset.getYValue(series, item);
		// If null, don't draw line at all
		if (yn == null || xn == null) {
			return;
		}
		x[1] = xn.doubleValue();
		y[1] = yn.doubleValue();

		// Now split the segment as needed
		double xcycleBound = Double.NaN;
		double ycycleBound = Double.NaN;
		boolean xBoundMapping = false, yBoundMapping = false;
		CyclicNumberAxis cnax = null, cnay = null;

		if (domainAxis instanceof CyclicNumberAxis) {
			cnax = (CyclicNumberAxis)domainAxis;
			xcycleBound = cnax.getCycleBound();
			xBoundMapping = cnax.isBoundMappedToLastCycle();
			// If the segment must be splitted, insert a new point
			// Strict test forces to have real segments (not 2 equal points) and avoids division by 0 
			if ((x[0]!=x[1]) && ((xcycleBound>=x[0]) && (xcycleBound<=x[1]) ||  (xcycleBound>=x[1]) && (xcycleBound<=x[0]))) {
				double[] nx = new double[3];
				double[] ny = new double[3];
				nx[0] = x[0]; nx[2] = x[1]; ny[0] = y[0]; ny[2] = y[1];
				nx[1] = xcycleBound;
				ny[1] = (y[1] - y[0]) * (xcycleBound - x[0]) / (x[1] - x[0]) + y[0];
				x = nx; y = ny;
			}
		}

		if (rangeAxis instanceof CyclicNumberAxis) {
			cnay = (CyclicNumberAxis)rangeAxis;
			ycycleBound = cnay.getCycleBound();
			yBoundMapping = cnay.isBoundMappedToLastCycle();
			// The split may occur in either x splitted segments, if any, but not in both
			if ((y[0]!=y[1]) && ((ycycleBound>=y[0]) && (ycycleBound<=y[1]) ||  (ycycleBound>=y[1]) && (ycycleBound<=y[0]))) {
				double[] nx = new double[x.length+1];
				double[] ny = new double[y.length+1];
				nx[0] = x[0]; nx[2] = x[1]; ny[0] = y[0]; ny[2] = y[1];
				ny[1] = ycycleBound;
				nx[1] = (x[1] - x[0]) * (ycycleBound - y[0]) / (y[1] - y[0]) + x[0];
				if (x.length==3) { 
					nx[3] = x[2]; ny[3] = y[2]; 
				}
				x = nx; y = ny;
			}
			else if ((x.length==3) && (y[1]!=y[2]) && ((ycycleBound>=y[1]) && (ycycleBound<=y[2]) ||  (ycycleBound>=y[2]) && (ycycleBound<=y[1]))) {
				double[] nx = new double[4];
				double[] ny = new double[4];
				nx[0] = x[0]; nx[1] = x[1]; nx[3] = x[2]; 
				ny[0] = y[0]; ny[1] = y[1]; ny[3] = y[2];
				ny[2] = ycycleBound;
				nx[2] = (x[2] - x[1]) * (ycycleBound - y[1]) / (y[2] - y[1]) + x[1];
				x = nx; y = ny;
			}
		}
		
		// If the line is not wrapping, then parent is OK
		if (x.length==2) {
			super.drawItem(g2, state, dataArea, info, plot, domainAxis, rangeAxis, dataset, series, item, crosshairInfo, pass);
			return;
		}

		OverwriteDataSet newset = new OverwriteDataSet(x,y,dataset);

		if (cnax!=null) {
			if (xcycleBound==x[0]) cnax.setBoundMappedToLastCycle(x[1] <= xcycleBound);
			if (xcycleBound==x[1]) cnax.setBoundMappedToLastCycle(x[0] <= xcycleBound);
		}
		if (cnay!=null) {
			if (ycycleBound==y[0]) cnay.setBoundMappedToLastCycle(y[1] <= ycycleBound);
			if (ycycleBound==y[1]) cnay.setBoundMappedToLastCycle(y[0] <= ycycleBound);
		}
		super.drawItem(g2, state, dataArea, info, plot, domainAxis, rangeAxis, newset, series, 1, crosshairInfo, pass);

		if (cnax!=null) {
			if (xcycleBound==x[1]) cnax.setBoundMappedToLastCycle(x[2] <= xcycleBound);
			if (xcycleBound==x[2]) cnax.setBoundMappedToLastCycle(x[1] <= xcycleBound);
		}
		if (cnay!=null) {
			if (ycycleBound==y[1]) cnay.setBoundMappedToLastCycle(y[2] <= ycycleBound);
			if (ycycleBound==y[2]) cnay.setBoundMappedToLastCycle(y[1] <= ycycleBound);
		}
		super.drawItem(g2, state, dataArea, info, plot, domainAxis, rangeAxis, newset, series, 2, crosshairInfo, pass);

		if (x.length==4) {
			if (cnax!=null) {
				if (xcycleBound==x[2]) cnax.setBoundMappedToLastCycle(x[3] <= xcycleBound);
				if (xcycleBound==x[3]) cnax.setBoundMappedToLastCycle(x[2] <= xcycleBound);
			}
			if (cnay!=null) {
				if (ycycleBound==y[2]) cnay.setBoundMappedToLastCycle(y[3] <= ycycleBound);
				if (ycycleBound==y[3]) cnay.setBoundMappedToLastCycle(y[2] <= ycycleBound);
			}
			super.drawItem(g2, state, dataArea, info, plot, domainAxis, rangeAxis, newset, series, 3, crosshairInfo, pass);
		}
		
		if (cnax!=null) cnax.setBoundMappedToLastCycle(xBoundMapping);
		if (cnay!=null) cnay.setBoundMappedToLastCycle(yBoundMapping);
	}

	/** A dataset to hold the interpolated points when drawing new lines */
	protected static class OverwriteDataSet implements XYDataset {
		
		protected XYDataset delegateSet;
		Double [] x, y;
		
		public OverwriteDataSet(double [] x, double[] y, XYDataset delegateSet) {
			this.delegateSet = delegateSet;
			this.x = new Double[x.length]; this.y = new Double[y.length];
			for (int i=0; i<x.length; ++i) { 
				this.x[i] = new Double(x[i]);
				this.y[i] = new Double(y[i]);
			}
		}

		public int getItemCount(int series) {
			return x.length;
		}

		public Number getXValue(int series, int item) {
			return x[item];
		}

		public Number getYValue(int series, int item) {
			return y[item];
		}

		public int getSeriesCount() {
			return delegateSet.getSeriesCount();
		}

		public String getSeriesName(int series) {
			return delegateSet.getSeriesName(series);
		}

		public void addChangeListener(DatasetChangeListener listener) {
			// unused in parent
		}

		public void removeChangeListener(DatasetChangeListener listener) {
			// unused in parent
		}

		public DatasetGroup getGroup() {
			// unused but must return something, so while we are at it...
			return delegateSet.getGroup();
		}

		public void setGroup(DatasetGroup group) {
			// unused in parent
		}
		
	}
	
}


