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
 * ---------------------
 * CyclicNumberAxis.java
 * ---------------------
 * (C) Copyright 2003, by Nicolas Brodu and Contributors.
 *
 * Original Author:  Nicolas Brodu;
 * Contributor(s): -
 *
 * $Id: CyclicNumberAxis.java,v 1.1 2007/10/10 19:21:57 vauchers Exp $
 *
 * Changes
 * -------
 * 19-Nov-2003 : Initial import to JFreeChart from the JSynoptic project (NB);
 * 
 */
package org.jfree.chart.axis;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Paint;
import java.awt.Stroke;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.NumberFormat;
import java.util.List;

import org.jfree.chart.plot.Plot;
import org.jfree.data.Range;
import org.jfree.io.SerialUtilities;
import org.jfree.ui.RectangleEdge;
import org.jfree.ui.TextAnchor;
import org.jfree.util.ObjectUtils;

/**
This class extends NumberAxis and handles cycling.
 
Traditional representation of data in the range x0..x1
<pre>
|-------------------------|
x0                       x1
</pre> 

Here, the range bounds are at the axis extremities.
With cyclic axis, however, the time is split in 
"cycles", or "time frames", or the same duration : the period.

A cycle axis cannot by definition handle a larger interval 
than the period : <pre>x1 - x0 >= period</pre>. Thus, at most a full 
period can be represented with such an axis.

The cycle bound is the number between x0 and x1 which marks 
the beginning of new time frame:
<pre>
|---------------------|----------------------------|
x0                   cb                           x1
<---previous cycle---><-------current cycle-------->
</pre>

It is actually a multiple of the period, plus optionally 
a start offset: <pre>cb = n * period + offset</pre>

Thus, by definition, two consecutive cycle bounds 
period apart, which is precisely why it is called a 
period.

The visual representation of a cyclic axis is like that:
<pre>
|----------------------------|---------------------|
cb                         x1|x0                  cb
<-------current cycle--------><---previous cycle--->
</pre>

The cycle bound is at the axis ends, then current 
cycle is shown, then the last cycle. When using 
dynamic data, the visual effect is the current cycle 
erases the last cycle as x grows. Then, the next cycle 
bound is reached, and the process starts over, erasing 
the previous cycle.

A Cyclic item renderer is provided to do exactly this.

 */
public class CyclicNumberAxis extends NumberAxis {

	protected double offset, period;
	
	protected boolean boundMappedToLastCycle;
	protected boolean advanceLineVisible;

	/** The default axis line stroke. */
	public static Stroke DEFAULT_ADVANCE_LINE_STROKE = new BasicStroke(1.0f);
    
	/** The default axis line paint. */
	public static final Paint DEFAULT_ADVANCE_LINE_PAINT = Color.gray;
	
	protected transient Stroke advanceLineStroke = DEFAULT_ADVANCE_LINE_STROKE;
	protected transient Paint advanceLinePaint = DEFAULT_ADVANCE_LINE_PAINT;
	
	private transient boolean internalMarkerWhenTicksOverlap;
	private transient Tick internalMarkerCycleBoundTick;
	
	/**
	 * The advance line is the line drawn at the limit of the current cycle, when erasing the previous cycle. 
	 * @return the advance line Paint.
	 */
	public Paint getAdvanceLinePaint() {
		return advanceLinePaint;
	}

	/**
	 * The advance line is the line drawn at the limit of the current cycle, when erasing the previous cycle. 
	 * @param advanceLinePaint The new advance line Paint to set.
	 */
	public void setAdvanceLinePaint(Paint advanceLinePaint) {
		this.advanceLinePaint = advanceLinePaint;
	}
	
	/**
	 * The advance line is the line drawn at the limit of the current cycle, when erasing the previous cycle. 
	 * @return the advance line Stroke.
	 */
	public Stroke getAdvanceLineStroke() {
		return advanceLineStroke;
	}
	/**
	 * The advance line is the line drawn at the limit of the current cycle, when erasing the previous cycle. 
	 * @param advanceLineStroke The advance line Stroke to set.
	 */
	public void setAdvanceLineStroke(Stroke advanceLineStroke) {
		this.advanceLineStroke = advanceLineStroke;
	}
	
	/**
	 * The advance line is the line drawn at the limit of the current cycle, when erasing the previous cycle. 
	 * @return true if the the advance line is visible.
	 */
	public boolean isAdvanceLineVisible() {
		return advanceLineVisible;
	}
	
	/**
	 * The advance line is the line drawn at the limit of the current cycle, when erasing the previous cycle. 
	 * @param advanceLineVisible The advance line will show if set to true
	 */
	public void setAdvanceLineVisible(boolean advanceLineVisible) {
		this.advanceLineVisible = advanceLineVisible;
	}
	
	/**
	 * The cycle bound can be associated either with the current or with the last cycle.
	 * It's up to the user's choice to decide which, as this is just a convention. 
	 * By default, the cycle bound is mapped to the current cycle.
	 * <br>
	 * Note that this has no effect on visual appearance, as the cycle bound is mapped successively for
	 * both axis ends. Use this function for correct results in translateValueToJava2D. 
	 *  
	 * @return true if the cycle bound is mapped to the last cycle, false if it is bound to the current cycle (default)
	 */
	public boolean isBoundMappedToLastCycle() {
		return boundMappedToLastCycle;
	}
	
	/**
	 * The cycle bound can be associated either with the current or with the last cycle.
	 * It's up to the user's choice to decide which, as this is just a convention. 
	 * By default, the cycle bound is mapped to the current cycle. 
	 * <br>
	 * Note that this has no effect on visual appearance, as the cycle bound is mapped successively for
	 * both axis ends. Use this function for correct results in translateValueToJava2D.
	 *  
	 * @param boundMappedToLastCycle Set it to true to map the cycle bound to the last cycle.
	 */
	public void setBoundMappedToLastCycle(boolean boundMappedToLastCycle) {
		this.boundMappedToLastCycle = boundMappedToLastCycle;
	}
	

	/** Creates a CycleNumberAxis with the given period */
	public CyclicNumberAxis(double period) {
		this(0,period);
	}

	/** Creates a CycleNumberAxis with the given period and offset */
	public CyclicNumberAxis(double period, double offset) {
		this(period,offset,null);
	}

	/** Creates a named CycleNumberAxis with the given period */
	public CyclicNumberAxis(double period, String label) {
		this(0, period, label);
	}
	
	/** Creates a named CycleNumberAxis with the given period and offset */
	public CyclicNumberAxis(double period, double offset, String label) {
		super(label);
		this.period = period;
		this.offset = offset;
		setFixedAutoRange(period);
		advanceLineVisible = true;
	}
		
	/* Parent's javadoc */
	protected void selectHorizontalAutoTickUnit(Graphics2D g2,
												Rectangle2D drawArea, Rectangle2D dataArea,
												RectangleEdge edge) {

		double tickLabelWidth = estimateMaximumTickLabelWidth(g2, getTickUnit());
		
		// Compute number of labels
		double n = getRange().getLength() * tickLabelWidth / dataArea.getWidth();

		setTickUnit( (NumberTickUnit)getStandardTickUnits().getCeilingTickUnit(n), false, false);
		
	 }

	/* Parent's javadoc */
	protected void selectVerticalAutoTickUnit(Graphics2D g2,
												Rectangle2D drawArea, Rectangle2D dataArea,
												RectangleEdge edge) {

		double tickLabelWidth = estimateMaximumTickLabelWidth(g2, getTickUnit());

		// Compute number of labels
		double n = getRange().getLength() * tickLabelWidth / dataArea.getHeight();

		setTickUnit( (NumberTickUnit)getStandardTickUnits().getCeilingTickUnit(n), false, false);
		
	 }

	/** A special Number tick that also hold information about the cycle bound mapping for this tick.
	 * This is especially useful for having a tick at each axis end with the cycle bound value.
	 * See also isBoundMappedToLastCycle()
	 */
	protected static class CycleBoundTick extends NumberTick {
		
		public boolean mapToLastCycle;
		
		public CycleBoundTick(boolean mapToLastCycle, Number number, String label, float anchorX,
				float anchorY, TextAnchor textAnchor,
				TextAnchor rotationAnchor, double angle) {
			super(number, label, anchorX, anchorY, textAnchor, rotationAnchor,
					angle);
			this.mapToLastCycle = mapToLastCycle;
		}
	}
	
	/* (non-Javadoc)
	 * @see org.jfree.chart.axis.ValueAxis#calculateAnchorPoint(org.jfree.chart.axis.ValueTick, double, java.awt.geom.Rectangle2D, org.jfree.ui.RectangleEdge)
	 */
	protected float[] calculateAnchorPoint(ValueTick tick, double cursor, Rectangle2D dataArea, RectangleEdge edge) {
		if (tick instanceof CycleBoundTick) {
			boolean mapsav = boundMappedToLastCycle;
			boundMappedToLastCycle = ((CycleBoundTick)tick).mapToLastCycle;
			float[] ret = super.calculateAnchorPoint(tick, cursor, dataArea, edge);
			boundMappedToLastCycle = mapsav;
			return ret;
		}
		return super.calculateAnchorPoint(tick, cursor, dataArea, edge);
	}
	
	
	
	/* (non-Javadoc)
	 * @see org.jfree.chart.axis.DateAxis#refreshTicksHorizontal(java.awt.Graphics2D, double, java.awt.geom.Rectangle2D, java.awt.geom.Rectangle2D, org.jfree.ui.RectangleEdge)
	 */
	public List refreshHorizontalTicks(Graphics2D g2, double cursor, Rectangle2D plotArea, Rectangle2D dataArea, RectangleEdge edge) {

		List result = new java.util.ArrayList();

		Font tickLabelFont = getTickLabelFont();
		g2.setFont(tickLabelFont);
        
		if (isAutoTickUnitSelection()) {
			selectAutoTickUnit(g2, plotArea, dataArea, edge);
		}

		double unit = getTickUnit().getSize();
		double cycleBound = getCycleBound();
		double currentTickValue = Math.ceil(cycleBound / unit) * unit;
		double upperValue = getRange().getUpperBound();
		boolean cycled = false;

		boolean boundMapping = boundMappedToLastCycle; 
		boundMappedToLastCycle = false; 
		
		CycleBoundTick lastTick = null; 
		float lastX = 0.0f;

		if (upperValue==cycleBound) {
			currentTickValue = calculateLowestVisibleTickValue();
			cycled = true;
			boundMappedToLastCycle = true;
		}
		
		while (currentTickValue <= upperValue) {
			
			// Cycle when necessary
			boolean cyclenow = false;
			if ((currentTickValue + unit> upperValue) && !cycled) cyclenow = true;
			
			double xx = translateValueToJava2D(currentTickValue, dataArea, edge);
			String tickLabel;
			NumberFormat formatter = getNumberFormatOverride();
			if (formatter != null) {
				tickLabel = formatter.format(currentTickValue);
			}
			else {
				tickLabel = getTickUnit().valueToString(currentTickValue);
			}
			float x = (float) xx;
			float y = 0.0f;
			TextAnchor anchor = null;
			TextAnchor rotationAnchor = null;
			double angle = 0.0;
			Insets tickLabelInsets = getTickLabelInsets();
			if (isVerticalTickLabels()) {
				if (edge == RectangleEdge.TOP) {
					y = (float) (cursor - tickLabelInsets.right);
					angle = Math.PI / 2.0;
				}
				else {
					y = (float) (cursor + tickLabelInsets.right);
					angle = -Math.PI / 2.0;
				}
				anchor = TextAnchor.CENTER_RIGHT;
				// If tick overlap when cycling, update last tick too
				if ((lastTick!=null) && (lastX==x) && (currentTickValue!=cycleBound)) {
					anchor = isInverted() ? TextAnchor.TOP_RIGHT : TextAnchor.BOTTOM_RIGHT;
					result.remove(result.size()-1);
					result.add(new CycleBoundTick(boundMappedToLastCycle, lastTick.getNumber(), lastTick.getText(), x, y, anchor, anchor, lastTick.getAngle()));
					internalMarkerWhenTicksOverlap = true;
					anchor = isInverted() ? TextAnchor.BOTTOM_RIGHT : TextAnchor.TOP_RIGHT;
				}
				rotationAnchor = anchor;
			}
			else {
				if (edge == RectangleEdge.TOP) {
					y = (float) (cursor - tickLabelInsets.bottom);
					anchor = TextAnchor.BOTTOM_CENTER; 
					if ((lastTick!=null) && (lastX==x) && (currentTickValue!=cycleBound)) {
						anchor = isInverted() ? TextAnchor.BOTTOM_LEFT : TextAnchor.BOTTOM_RIGHT;
						result.remove(result.size()-1);
						result.add(new CycleBoundTick(boundMappedToLastCycle, lastTick.getNumber(), lastTick.getText(), x, y, anchor, anchor, lastTick.getAngle()));
						internalMarkerWhenTicksOverlap = true;
						anchor = isInverted() ? TextAnchor.BOTTOM_RIGHT : TextAnchor.BOTTOM_LEFT;
					}
					rotationAnchor = anchor;
				}
				else {
					y = (float) (cursor + tickLabelInsets.top);
					anchor = TextAnchor.TOP_CENTER; 
					if ((lastTick!=null) && (lastX==x) && (currentTickValue!=cycleBound)) {
						anchor = isInverted() ? TextAnchor.TOP_LEFT : TextAnchor.TOP_RIGHT;
						result.remove(result.size()-1);
						result.add(new CycleBoundTick(boundMappedToLastCycle, lastTick.getNumber(), lastTick.getText(), x, y, anchor, anchor, lastTick.getAngle()));
						internalMarkerWhenTicksOverlap = true;
						anchor = isInverted() ? TextAnchor.TOP_RIGHT :TextAnchor.TOP_LEFT;
					}
					rotationAnchor = anchor;
				}
			}

			CycleBoundTick tick = new CycleBoundTick(boundMappedToLastCycle, new Double(currentTickValue), tickLabel, x, y, anchor, rotationAnchor, angle);
			if (currentTickValue == cycleBound) internalMarkerCycleBoundTick = tick; 
			result.add(tick);
			lastTick = tick;
			lastX = x;
			
			currentTickValue += unit;
			
			if (cyclenow) {
				currentTickValue = calculateLowestVisibleTickValue();
				upperValue = cycleBound;
				cycled = true;
				boundMappedToLastCycle = true; 
			}

		}
		boundMappedToLastCycle = boundMapping; 
		return result;
		
	}

	public List refreshVerticalTicks(Graphics2D g2, double cursor,Rectangle2D plotArea, Rectangle2D dataArea, RectangleEdge edge) {
		List result = new java.util.ArrayList();
		result.clear();

		Font tickLabelFont = getTickLabelFont();
		g2.setFont(tickLabelFont);
		if (isAutoTickUnitSelection()) {
			selectAutoTickUnit(g2, plotArea, dataArea, edge);
		}

		double unit = getTickUnit().getSize();
		double cycleBound = getCycleBound();
		double currentTickValue = Math.ceil(cycleBound / unit) * unit;
		double upperValue = getRange().getUpperBound();
		boolean cycled = false;

		boolean boundMapping = boundMappedToLastCycle; 
		boundMappedToLastCycle = true; 

		NumberTick lastTick = null;
		float lastY = 0.0f;

		if (upperValue==cycleBound) {
			currentTickValue = calculateLowestVisibleTickValue();
			cycled = true;
			boundMappedToLastCycle = true;
		}
		
		while (currentTickValue <= upperValue) {
			
			// Cycle when necessary
			boolean cyclenow = false;
			if ((currentTickValue + unit> upperValue) && !cycled) cyclenow = true;

			double yy = translateValueToJava2D(currentTickValue, dataArea, edge);
			String tickLabel;
			NumberFormat formatter = getNumberFormatOverride();
			if (formatter != null) {
				tickLabel = formatter.format(currentTickValue);
			}
			else {
				tickLabel = getTickUnit().valueToString(currentTickValue);
			}

			float x = 0.0f;
			float y = (float) yy;
			TextAnchor anchor = null;
			TextAnchor rotationAnchor = null;
			double angle = 0.0;
			if (isVerticalTickLabels()) {

				if (edge == RectangleEdge.LEFT) {
					x = (float) (cursor - getTickLabelInsets().right);  
					anchor = TextAnchor.BOTTOM_CENTER; 
					if ((lastTick!=null) && (lastY==y) && (currentTickValue!=cycleBound)) {
						anchor = isInverted() ? TextAnchor.BOTTOM_LEFT : TextAnchor.BOTTOM_RIGHT;
						result.remove(result.size()-1);
						result.add(new CycleBoundTick(boundMappedToLastCycle, lastTick.getNumber(), lastTick.getText(), x, y, anchor, anchor, lastTick.getAngle()));
						internalMarkerWhenTicksOverlap = true;
						anchor = isInverted() ? TextAnchor.BOTTOM_RIGHT : TextAnchor.BOTTOM_LEFT;
					}
					rotationAnchor = anchor;
					angle = -Math.PI / 2.0;
				}
				else {
					x = (float) (cursor + getTickLabelInsets().left);
					anchor = TextAnchor.BOTTOM_CENTER; 
					if ((lastTick!=null) && (lastY==y) && (currentTickValue!=cycleBound)) {
						anchor = isInverted() ? TextAnchor.BOTTOM_RIGHT : TextAnchor.BOTTOM_LEFT;
						result.remove(result.size()-1);
						result.add(new CycleBoundTick(boundMappedToLastCycle, lastTick.getNumber(), lastTick.getText(), x, y, anchor, anchor, lastTick.getAngle()));
						internalMarkerWhenTicksOverlap = true;
						anchor = isInverted() ? TextAnchor.BOTTOM_LEFT : TextAnchor.BOTTOM_RIGHT;
					}
					rotationAnchor = anchor;
					angle = Math.PI / 2.0;
				}
			}
			else {
				if (edge == RectangleEdge.LEFT) {
					x = (float) (cursor - getTickLabelInsets().right);
					anchor = TextAnchor.CENTER_RIGHT; 
					if ((lastTick!=null) && (lastY==y) && (currentTickValue!=cycleBound)) {
						anchor = isInverted() ? TextAnchor.BOTTOM_RIGHT : TextAnchor.TOP_RIGHT;
						result.remove(result.size()-1);
						result.add(new CycleBoundTick(boundMappedToLastCycle, lastTick.getNumber(), lastTick.getText(), x, y, anchor, anchor, lastTick.getAngle()));
						internalMarkerWhenTicksOverlap = true;
						anchor = isInverted() ? TextAnchor.TOP_RIGHT : TextAnchor.BOTTOM_RIGHT;
					}
					rotationAnchor = anchor;
				}
				else {
					x = (float) (cursor + getTickLabelInsets().left);
					anchor = TextAnchor.CENTER_LEFT; 
					if ((lastTick!=null) && (lastY==y) && (currentTickValue!=cycleBound)) {
						anchor = isInverted() ? TextAnchor.BOTTOM_LEFT : TextAnchor.TOP_LEFT;
						result.remove(result.size()-1);
						result.add(new CycleBoundTick(boundMappedToLastCycle, lastTick.getNumber(), lastTick.getText(), x, y, anchor, anchor, lastTick.getAngle()));
						internalMarkerWhenTicksOverlap = true;
						anchor = isInverted() ? TextAnchor.TOP_LEFT : TextAnchor.BOTTOM_LEFT;
					}
					rotationAnchor = anchor;
				}
			}

			CycleBoundTick tick = new CycleBoundTick(boundMappedToLastCycle, new Double(currentTickValue), tickLabel, x, y, anchor, rotationAnchor, angle);
			if (currentTickValue == cycleBound) internalMarkerCycleBoundTick = tick; 
			result.add(tick);
			lastTick = tick;
			lastY = y;
			
			if (currentTickValue == cycleBound) internalMarkerCycleBoundTick = tick;

			currentTickValue += unit;
			
			if (cyclenow) {
				currentTickValue = calculateLowestVisibleTickValue();
				upperValue = cycleBound;
				cycled = true;
				boundMappedToLastCycle = false; 
			}

		}
		boundMappedToLastCycle = boundMapping; 
		return result;
	}
	
	
	/* (non-Javadoc)
	 * @see org.jfree.chart.axis.ValueAxis#translateJava2DToValue(double, java.awt.geom.Rectangle2D, org.jfree.ui.RectangleEdge)
	 */
	public double translateJava2DToValue(double java2DValue, Rectangle2D dataArea, RectangleEdge edge) {
		Range range = getRange();
		
		double vmax = range.getUpperBound();
		double vp = getCycleBound();

		double jmin = 0.0;
		double jmax = 0.0;
		if (RectangleEdge.isTopOrBottom(edge)) {
			jmin = dataArea.getMinX();
			jmax = dataArea.getMaxX();
		}
		else if (RectangleEdge.isLeftOrRight(edge)) {
			jmin = dataArea.getMaxY();
			jmax = dataArea.getMinY();
		}
		
		if (isInverted()) {
			double jbreak = jmax - (vmax - vp) * (jmax - jmin) / period;
			if (java2DValue >= jbreak) { 
				return vp + (jmax - java2DValue) * period / (jmax - jmin);
			} else {
				return vp - (java2DValue - jmin) * period / (jmax - jmin);
			}
		}
		else {
			double jbreak = (vmax - vp) * (jmax - jmin) / period + jmin;
			if (java2DValue <= jbreak) { 
				return vp + (java2DValue - jmin) * period / (jmax - jmin);
			} else {
				return vp - (jmax - java2DValue) * period / (jmax - jmin);
			}
		}
	}
	
	/* (non-Javadoc)
	 * @see org.jfree.chart.axis.ValueAxis#translateValueToJava2D(double, java.awt.geom.Rectangle2D, org.jfree.ui.RectangleEdge)
	 */
	public double translateValueToJava2D(double value, Rectangle2D dataArea,RectangleEdge edge) {
		Range range = getRange();
		
		double vmin = range.getLowerBound();
		double vmax = range.getUpperBound();
		double vp = getCycleBound();

		if ((value<vmin) || (value>vmax)) return Double.NaN;
		
		
		double jmin = 0.0;
		double jmax = 0.0;
		if (RectangleEdge.isTopOrBottom(edge)) {
			jmin = dataArea.getMinX();
			jmax = dataArea.getMaxX();
		}
		else if (RectangleEdge.isLeftOrRight(edge)) {
			jmax = dataArea.getMinY();
			jmin = dataArea.getMaxY();
		}

		if (isInverted()) {
			if (value == vp) {
				return boundMappedToLastCycle ? jmin : jmax; 
			}
			else if (value >vp) {
				return jmax - (value - vp) * (jmax - jmin) / period;
			} else {
				return jmin + (vp - value) * (jmax - jmin) / period;
			}
		}
		else {
			if (value == vp) {
				return boundMappedToLastCycle ? jmax : jmin; 
			}
			else if (value >=vp) {
				return jmin + (value - vp) * (jmax - jmin) / period;
			} else {
				return jmax - (vp - value) * (jmax - jmin) / period;
			}
		}
	}
	
// Range functions
	
	/* (non-Javadoc)
	 * @see org.jfree.chart.axis.ValueAxis#centerRange(double)
	 */
	public void centerRange(double value) {
		setRange(value - period / 2.0, value + period / 2.0);
	}

	/** This function is nearly useless since the auto range is fixed for this class to the period
	 * The period is extended if necessary to fit the minimum size
	 * @see org.jfree.chart.axis.ValueAxis#setAutoRangeMinimumSize(double, boolean)
	 */
	public void setAutoRangeMinimumSize(double size, boolean notify) {
		if (size>period) period = size;
		super.setAutoRangeMinimumSize(size, notify);
	}

	/** The auto range is fixed for this class to the period by default. 
	 * This function will thus set a new period
	 * @see org.jfree.chart.axis.ValueAxis#setFixedAutoRange(double)
	 */
	public void setFixedAutoRange(double length) {
		period = length;
		super.setFixedAutoRange(length);
	}

	/** Sets a new axis range. The period is extended to fit the range size, if necessary.
	 * @see org.jfree.chart.axis.ValueAxis#setRange(org.jfree.data.Range, boolean, boolean)
	 */
	public void setRange(Range range, boolean turnOffAutoRange, boolean notify) {
		double size = range.getUpperBound() - range.getLowerBound();
		if (size > period) period = size;
		super.setRange(range, turnOffAutoRange, notify);
	}
	
	/**
	 * The cycle bound is defined as the higest value x such that "offset + period * i = x", with i 
	 * and integer and x &lt; range.getUpperBound()
	 * This is the value which is at both ends of the axis :  x...up|low...x
	 * The values from x to up are the valued in the current cycle.
	 * The values from low to x are the valued in the previous cycle.
	 * */
	public double getCycleBound() {
		return Math.floor( (getRange().getUpperBound() - offset) / period) * period + offset;
	}
	
	/**
	 * The cycle bound is a multiple of the period, plus optionally a start offset.
     * <P>
     * <pre>cb = n * period + offset</pre><br>
	 * 
     * @return the current offset.
     * 
     * @see #getCycleBound()
	 */
	public double getOffset() {
		return offset;
	}
	
	/**
	 * The cycle bound is a multiple of the period, plus optionally a start offset.
     * <P>
     * <pre>cb = n * period + offset</pre><br>
	 * 
     * @param offset The offset to set.
     *
     * @see #getCycleBound() 
	 */
	public void setOffset(double offset) {
		this.offset = offset;
	}
	
	/**
	 * The cycle bound is a multiple of the period, plus optionally a start offset.
     * <P>
     * <pre>cb = n * period + offset</pre><br>
	 * 
     * @return the current period.
     * 
     * @see #getCycleBound()
	 */
	public double getPeriod() {
		return period;
	}
	
	/**
	 * The cycle bound is a multiple of the period, plus optionally a start offset.
     * <P>
     * <pre>cb = n * period + offset</pre><br>
	 * 
     * @param period The period to set.
     * 
     * @see #getCycleBound()
	 */
	public void setPeriod(double period) {
		this.period = period;
	}
	
	/* (non-Javadoc)
	 * @see org.jfree.chart.axis.ValueAxis#drawTickMarksAndLabels(java.awt.Graphics2D, double, java.awt.geom.Rectangle2D, java.awt.geom.Rectangle2D, org.jfree.ui.RectangleEdge)
	 */
	protected AxisState drawTickMarksAndLabels(Graphics2D g2, double cursor, Rectangle2D plotArea, Rectangle2D dataArea, RectangleEdge edge) {
		internalMarkerWhenTicksOverlap = false;
		AxisState ret = super.drawTickMarksAndLabels(g2, cursor, plotArea, dataArea, edge);
		
		// continue and separate the labels only if necessary
		if (!internalMarkerWhenTicksOverlap) return ret;
		
		double ol = getTickMarkOutsideLength();
		FontMetrics fm = g2.getFontMetrics(getTickLabelFont());
		
		if (this.isVerticalTickLabels()) ol = fm.getMaxAdvance(); 
		else ol = fm.getHeight();
		
		double il = 0;
		if (isTickMarksVisible()) {
			float xx = (float) translateValueToJava2D(getRange().getUpperBound(), dataArea, edge);
			Line2D mark = null;
			g2.setStroke(getTickMarkStroke());
			g2.setPaint(getTickMarkPaint());
			if (edge == RectangleEdge.LEFT) {
				mark = new Line2D.Double(cursor - ol, xx, cursor + il, xx);
			}
			else if (edge == RectangleEdge.RIGHT) {
				mark = new Line2D.Double(cursor + ol, xx, cursor - il, xx);
			}
			else if (edge == RectangleEdge.TOP) {
				mark = new Line2D.Double(xx, cursor - ol, xx, cursor + il);
			}
			else if (edge == RectangleEdge.BOTTOM) {
				mark = new Line2D.Double(xx, cursor + ol, xx, cursor - il);
			}
			g2.draw(mark);
		}
		return ret;
	}
	
	
	public AxisState draw(Graphics2D g2, double cursor, Rectangle2D plotArea, Rectangle2D dataArea, RectangleEdge edge) {
		AxisState ret = super.draw(g2, cursor, plotArea, dataArea, edge);
		if (isAdvanceLineVisible()) {
			double xx = translateValueToJava2D(getRange().getUpperBound(), dataArea, edge);
			Line2D mark = null;
			g2.setStroke(getAdvanceLineStroke());
			g2.setPaint(getAdvanceLinePaint());
			if (edge == RectangleEdge.LEFT) {
				mark = new Line2D.Double(cursor, xx, cursor +dataArea.getWidth(), xx);
			}
			else if (edge == RectangleEdge.RIGHT) {
				mark = new Line2D.Double(cursor-dataArea.getWidth(), xx, cursor, xx);
			}
			else if (edge == RectangleEdge.TOP) {
				mark = new Line2D.Double(xx, cursor + dataArea.getHeight(), xx, cursor);
			}
			else if (edge == RectangleEdge.BOTTOM) {
				mark = new Line2D.Double(xx, cursor, xx, cursor -dataArea.getHeight());
			}
			g2.draw(mark);
		}
		return ret;
	}

	/**
	 * Reserve some space on each axis side because we draw a centered label at each extremity. 
	 */
	public AxisSpace reserveSpace(Graphics2D g2, Plot plot, Rectangle2D plotArea, RectangleEdge edge, AxisSpace space) {
		internalMarkerCycleBoundTick = null;
		AxisSpace ret = super.reserveSpace(g2, plot, plotArea, edge, space);
		if (internalMarkerCycleBoundTick==null) return ret;

		FontMetrics fm = g2.getFontMetrics(getTickLabelFont());
		Rectangle2D r = fm.getStringBounds(internalMarkerCycleBoundTick.getText(),g2);

		if (RectangleEdge.isTopOrBottom(edge)) {
			if (isVerticalTickLabels()) space.add(r.getHeight() / 2, RectangleEdge.RIGHT);
			else space.add(r.getWidth() / 2, RectangleEdge.RIGHT);
		}
		else if (RectangleEdge.isLeftOrRight(edge)) {
			if (isVerticalTickLabels()) space.add(r.getWidth() / 2, RectangleEdge.TOP);
			else space.add(r.getHeight() / 2, RectangleEdge.TOP);
		}
		
		return ret;
	}

	/**
	 * Provides serialization support.
	 *
	 * @param stream  the output stream.
	 *
	 * @throws IOException  if there is an I/O error.
	 */
	private void writeObject(ObjectOutputStream stream) throws IOException {
	
		stream.defaultWriteObject();
		SerialUtilities.writePaint(this.advanceLinePaint, stream);
		SerialUtilities.writeStroke(this.advanceLineStroke, stream);
	
	}
	
	/**
	 * Provides serialization support.
	 *
	 * @param stream  the input stream.
	 *
	 * @throws IOException  if there is an I/O error.
	 * @throws ClassNotFoundException  if there is a classpath problem.
	 */
	private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
	
		stream.defaultReadObject();
		this.advanceLinePaint = SerialUtilities.readPaint(stream);
		this.advanceLineStroke = SerialUtilities.readStroke(stream);
	
	}
	 
	
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object object) {
        
		if (object == null) {
			return false;
		}
        
		if (object == this) {
			return true;
		}
        
		if (object instanceof CyclicNumberAxis) {
			if (super.equals(object)) {
                
				CyclicNumberAxis axis = (CyclicNumberAxis) object;
                
				boolean b0 = period == axis.period;
				boolean b1 = offset == axis.offset;
				boolean b2 = ObjectUtils.equal(this.advanceLinePaint, axis.advanceLinePaint);
				boolean b3 = ObjectUtils.equal(this.advanceLineStroke, axis.advanceLineStroke);
				boolean b4 = advanceLineVisible == axis.advanceLineVisible;
				boolean b5 = boundMappedToLastCycle == axis.boundMappedToLastCycle;

				return b0 && b1 && b2 && b3 && b4 && b5;
                
			}
		}
        
		return false;
	}
}
