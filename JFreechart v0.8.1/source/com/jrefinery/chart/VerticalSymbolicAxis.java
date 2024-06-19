/* =======================================
 * JFreeChart : a Java Chart Class Library
 * =======================================
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
 * ---------------------------
 * VerticalSymbolicAxis.java
 * ---------------------------
 *
 * Original Author:  David Gilbert (for Simba Management Limited);
 * Contributor(s):   Anthony Boulestreau;
 *
 *
 * Changes (from 23-Jun-2001)
 * --------------------------
 * 29-Mar-2002 : First version (AB);
 *
 */

package com.jrefinery.chart;

import java.awt.*;
import java.awt.font.*;
import java.awt.geom.*;
import java.text.*;
import java.util.*;

import com.jrefinery.chart.event.*;
import com.jrefinery.data.*;


/**
 * A standard linear value axis, for SYMBOLIC values displayed vertically.
 *
 */
public class VerticalSymbolicAxis extends VerticalNumberAxis implements VerticalAxis {
	
    /** The default symbolic grid line paint. */
    public static final Paint DEFAULT_SYMBOLIC_GRID_LINE_PAINT = new Color(232, 234, 232);

	/** The list of symbolic value to display instead of the numeric values */
    protected java.util.List symbolicValue;

	/** Enable or not the zoom **/
	protected boolean ySymbolicZoomIsAccepted = false;

	/** List of the symbolic grid lines shapes */
	public java.util.List symbolicGridLineList = null;

	/** Color of the dark part of the symbolic grid line **/
	protected Paint symbolicGridPaint;

    /** Flag that indicates whether or not symbolic grid lines are visible. */
	protected boolean symbolicGridLinesVisible;


    /**
     * Constructs a vertical symbolic axis, using default values where necessary.
     * @param label The axis label (null permitted).
	 * @param sv The string values
     */
    public VerticalSymbolicAxis(String label, String[] sv) {

		this(label, sv,
				 Axis.DEFAULT_AXIS_LABEL_FONT,
				 true, //symbolic grid line visible
			     DEFAULT_SYMBOLIC_GRID_LINE_PAINT);

		this.autoRange = true;
    }

    /**
     * Constructs a vertical symbolic axis.
     * @param label The axis label (null permitted).
	 * @param sv The string values
     * @param labelFont The font for displaying the axis label.
     * @param minimumAxisValue The lowest value shown on the axis.
     * @param maximumAxisValue The highest value shown on the axis.
     */
    public VerticalSymbolicAxis(String label, String[] sv, Font labelFont, 
								boolean symbolicGridLinesVisible, Paint symbolicGridPaint) {

		this(label, sv,
				 labelFont,
				 Axis.DEFAULT_AXIS_LABEL_PAINT,
				 Axis.DEFAULT_AXIS_LABEL_INSETS,
				 true,
				 true, // tick labels visible
				 Axis.DEFAULT_TICK_LABEL_FONT,
				 Axis.DEFAULT_TICK_LABEL_PAINT,
				 Axis.DEFAULT_TICK_LABEL_INSETS,
				 true, // tick marks visible
				 Axis.DEFAULT_TICK_STROKE,
				 true, // auto range
				 false, // auto range includes zero
				 false, // auto range sticky zero
				 NumberAxis.DEFAULT_MINIMUM_AUTO_RANGE,
				 ValueAxis.DEFAULT_MINIMUM_AXIS_VALUE,
				 ValueAxis.DEFAULT_MAXIMUM_AXIS_VALUE,
				 false, // inverted
				 false, // auto tick unit
				 NumberAxis.DEFAULT_TICK_UNIT,
				 false, // grid lines visible
				 ValueAxis.DEFAULT_GRID_LINE_STROKE,
				 ValueAxis.DEFAULT_GRID_LINE_PAINT,
				 ValueAxis.DEFAULT_CROSSHAIR_VISIBLE,
				 0.0,  // crosshair value
				 ValueAxis.DEFAULT_CROSSHAIR_STROKE,
				 ValueAxis.DEFAULT_CROSSHAIR_PAINT,
				 symbolicGridLinesVisible,
			     symbolicGridPaint);
    }

	/**
     * Constructs a new VerticalNumberAxis.
     * @param label The axis label.
     * @param labelFont The font for displaying the axis label.
     * @param labelPaint The paint used to draw the axis label.
     * @param labelInsets Determines the amount of blank space around the label.
     * @param labelDrawnVertical Flag indicating whether or not the label is drawn vertically.
     * @param tickLabelsVisible Flag indicating whether or not tick labels are visible.
     * @param tickLabelFont The font used to display tick labels.
     * @param tickLabelPaint The paint used to draw tick labels.
     * @param tickLabelInsets Determines the amount of blank space around tick labels.
     * @param tickMarksVisible Flag indicating whether or not tick marks are visible.
     * @param tickMarkStroke The stroke used to draw tick marks (if visible).
     * @param autoRange Flag indicating whether or not the axis is automatically scaled to fit the
     *                  data.
     * @param autoRangeIncludesZero A flag indicating whether or not zero *must* be displayed on
     *                              axis.
     * @param autoRangeMinimum The smallest automatic range allowed.
     * @param minimumAxisValue The lowest value shown on the axis.
     * @param maximumAxisValue The highest value shown on the axis.
     * @param inverted A flag indicating whether the axis is normal or inverted (inverted means
     *                 running from positive to negative).
     * @param autoTickUnitSelection A flag indicating whether or not the tick units are
     *                              selected automatically.
     * @param tickUnit The tick unit.
     * @param gridLinesVisible Flag indicating whether or not grid lines are visible for this axis.
     * @param gridStroke The pen/brush used to display grid lines (if visible).
     * @param gridPaint The color used to display grid lines (if visible).
     * @param crosshairValue The value at which to draw an optional crosshair (null permitted).
     * @param crosshairStroke The pen/brush used to draw the crosshair.
     * @param crosshairPaint The color used to draw the crosshair.
     */
    public VerticalSymbolicAxis(String label, String[] sv,
                              Font labelFont, Paint labelPaint, Insets labelInsets,
			      boolean labelDrawnVertical,
			      boolean tickLabelsVisible, Font tickLabelFont, Paint tickLabelPaint,
                              Insets tickLabelInsets,
			      boolean tickMarksVisible, Stroke tickMarkStroke,
			      boolean autoRange, boolean autoRangeIncludesZero, boolean autoRangeStickyZero,
                              Number autoRangeMinimum,
			      double minimumAxisValue, double maximumAxisValue,
                              boolean inverted,
			      boolean autoTickUnitSelection,
                              NumberTickUnit tickUnit,
 			      boolean gridLinesVisible, Stroke gridStroke, Paint gridPaint,
                   boolean crosshairVisible, double crosshairValue, Stroke crosshairStroke, Paint crosshairPaint,
					boolean symbolicGridLinesVisible, Paint symbolicGridPaint) {

	super(label,
              labelFont, labelPaint, labelInsets,
				labelDrawnVertical,
              tickLabelsVisible,
              tickLabelFont, tickLabelPaint, tickLabelInsets,
              tickMarksVisible,
              tickMarkStroke,
	      autoRange, autoRangeIncludesZero, autoRangeStickyZero, autoRangeMinimum,
	      minimumAxisValue, maximumAxisValue,
              inverted,
              autoTickUnitSelection, tickUnit,
              gridLinesVisible, gridStroke, gridPaint, crosshairVisible,
              crosshairValue, crosshairStroke, crosshairPaint);

	//initialization of symbolic value
	this.symbolicValue = Arrays.asList(sv);
	this.symbolicGridLinesVisible = symbolicGridLinesVisible;
	this.symbolicGridPaint = symbolicGridPaint;

    }
	
	/**
	 * Returns the list of the symbolic values to display.
	 *
	 * @returns list of symbolic values.
	 */
	 public String[] getSymbolicValue() {
		String[] strToReturn = new String[symbolicValue.size()];
		strToReturn = (String[])symbolicValue.toArray(strToReturn);
		return strToReturn;
	 }
	
	/**
	 * Returns the symbolic grid line color. 
	 *
	 * @returns the grid line color.
	 */
	public Paint getSymbolicGridPaint() {
		return symbolicGridPaint;
	}
	
	/**
	 * Returns <CODE>true</CODE> if the symbolic grid lines are showing, and false otherwise.
	 *
	 * @returns True if the symbolic grid lines are showing, and false otherwise.
	 */
	public boolean isGridLinesVisible() {
		return symbolicGridLinesVisible;
	}

	/**
     * Sets the visibility of the symbolic grid lines and notifies registered listeners that the axis has
     * been modified.
	 *
     * @param flag The new setting.
     */
	public void setSymbolicGridLinesVisible(boolean flag) {
		if (symbolicGridLinesVisible!=flag) {
            symbolicGridLinesVisible = flag;
			notifyListeners(new AxisChangeEvent(this));
        }
	}

	/**
     * Converts a value to a string, using the list of symbolic values.
	 *
	 * @returns the symbolic value.
     */
    public String valueToString(double value) {
		String strToReturn;
		try {
			strToReturn = (String)this.symbolicValue.get((int)value);
		}
		catch (IndexOutOfBoundsException  ex) {
			strToReturn =  new String("");
		}		
        return strToReturn;
    }

   /**
    * Redefinition of setAnchoredRange for the symbolicvalues.
	*/
    public void setAnchoredRange(double range) {
		if (ySymbolicZoomIsAccepted) {
			double anchor = Math.rint(this.anchorValue); //compute the corresponding integer corresponding to the anchor position
			double min = Math.rint(anchor - range/2) - 0.5;
			double max = Math.rint(anchor + range/2) + 0.5;
			if (min < -0.5) min = -0.5;
			if (max > symbolicValue.size()-0.5) max = symbolicValue.size()-0.5;
			this.setAxisRange(min, max);	
		}
    }

    /**
     * This operation is not supported by the symbolic values
     * @param g2 The graphics device;
     * @param drawArea The area in which the plot and axes should be drawn;
     * @param plotArea The area in which the plot should be drawn;
     */
    private void selectAutoTickUnit(Graphics2D g2, Rectangle2D drawArea, Rectangle2D plotArea) {
        throw new UnsupportedOperationException();
    }
	
	/**
     * Draws the plot on a Java 2D graphics device (such as the screen or a printer).
     * @param g2 The graphics device;
     * @param drawArea The area within which the chart should be drawn.
     * @param plotArea The area within which the plot should be drawn (a subset of the drawArea).
     */
    public void draw(Graphics2D g2, Rectangle2D drawArea, Rectangle2D plotArea) {
		super.draw(g2, drawArea, plotArea);
		if (symbolicGridLinesVisible) {
			drawSymbolicGridLines(g2, drawArea, plotArea);	
		}
	}

	/**
     * Draws the symbolic grid lines.<P>
	 * The colors are consecutively the color specified by <CODE>symbolicGridPaint<CODE> (<CODE>DEFAULT_SYMBOLIC_GRID_LINE_PAINT</CODE> by default) and white.
	 *
     * @param g2 The graphics device;
     * @param drawArea The area within which the chart should be drawn.
     * @param plotArea The area within which the plot should be drawn (a subset of the drawArea).
     */
	public void drawSymbolicGridLines(Graphics2D g2, Rectangle2D drawArea, Rectangle2D plotArea) {
		this.drawSymbolicGridLines(g2, drawArea, plotArea, true);
	}

	/**
     * Draws the symbolic grid lines.<P>
	 * The colors are consecutively the color specified by <CODE>symbolicGridPaint<CODE> (<CODE>DEFAULT_SYMBOLIC_GRID_LINE_PAINT</CODE> by default) and white.
	 * or if <CODE>firstGridLineIsDark</CODE> is <CODE>true</CODE> white and the color specified by <CODE>symbolicGridPaint<CODE>.
     * @param g2 The graphics device;
     * @param drawArea The area within which the chart should be drawn.
     * @param plotArea The area within which the plot should be drawn (a subset of the drawArea).
	 * @param firstGridLineIsDark True: the first symbolic grid line take the color of <CODE>symbolicGridPaint<CODE>.
	 *							  False: the first symbolic grid line is white. 
     */
	public void drawSymbolicGridLines(Graphics2D g2, Rectangle2D drawArea, Rectangle2D plotArea, boolean firstGridLineIsDark) {

		this.symbolicGridLineList = new Vector(ticks.size());
		boolean currentGridLineIsDark = firstGridLineIsDark;
		double xx = plotArea.getX();
		double yy1, yy2;
		
		//gets the outline stroke width of the plot
		double outlineStrokeWidth;
		if (plot.outlineStroke!= null) {
			outlineStrokeWidth = ((BasicStroke)plot.outlineStroke).getLineWidth();
		} else {
			outlineStrokeWidth = 1d;
		}
		
		Iterator iterator = ticks.iterator();
		Tick tick;
		Rectangle2D symbolicGridLine;
		while (iterator.hasNext()) {
			tick = (Tick)iterator.next();
			yy1 = this.translateValueToJava2D(tick.getNumericalValue()+0.5d, plotArea);
			yy2 = this.translateValueToJava2D(tick.getNumericalValue()-0.5d, plotArea);
			if (currentGridLineIsDark) {
				g2.setPaint(Color.white);
				g2.setXORMode((Color)symbolicGridPaint);
			} else {
				g2.setPaint(Color.white);
				g2.setXORMode(Color.white);
			}
			symbolicGridLine = new Rectangle2D.Double(xx + outlineStrokeWidth, yy1, plotArea.getMaxX() - xx - outlineStrokeWidth, yy2-yy1);					
			g2.fill(symbolicGridLine);	
			symbolicGridLineList.add(symbolicGridLine);
			currentGridLineIsDark=!currentGridLineIsDark;
		}
		g2.setPaintMode();
	}

	/**
	 *	Get the symbolic grid line corresponding to the specified position.
	 *	@param position position of the grid line, startinf from 0
	 */
	public Rectangle2D.Double getSymbolicGridLine(int position) {
		if (symbolicGridLineList!= null) {
			return (Rectangle2D.Double)symbolicGridLineList.get(position);			
		} else {
			return null;
		}

	}

	/**
     * Rescales the axis to ensure that all data is visible.
     */
	protected void autoAdjustRange() {

		if (plot!=null) {

			if (plot instanceof VerticalValuePlot) {
			VerticalValuePlot vvp = (VerticalValuePlot)plot;
			
			//ensure that all the symbolic value are displayed
			double upper = symbolicValue.size()-1;
			double lower = 0;

			double range = upper-lower;

			// ensure the autorange is at least <minRange> in size...
			double minRange = this.autoRangeMinimumSize.doubleValue();
			if (range<minRange) {
				upper = (upper+lower+minRange)/2;
				lower = (upper+lower-minRange)/2;
			}

			//this ensure that the symbolic grid lines will be displayed correctly.
			double upperMargin = 0.5;
			double lowerMargin = 0.5;

			if (this.autoRangeIncludesZero) {
						if (this.autoRangeStickyZero) {
					if (upper<=0.0) {
								upper = 0.0;
							}
							else {
								upper = upper+upperMargin;
							}
					if (lower>=0.0) {
								lower = 0.0;
							}
							else {
								lower = lower-lowerMargin;
							}
						}
						else {
					upper = Math.max(0.0, upper+upperMargin);
					lower = Math.min(0.0, lower-lowerMargin);
						}
			}
			else {
						if (this.autoRangeStickyZero) {
					if (upper<=0.0) {
								upper = Math.min(0.0, upper+upperMargin);
							}
							else {
								upper = upper+upperMargin;
							}
					if (lower>=0.0) {
								lower = Math.max(0.0, lower-lowerMargin);
							}
							else {
								lower = lower-lowerMargin;
							}
						}
						else {
							upper = upper+upperMargin;
					lower = lower-lowerMargin;
						}
					}
			this.minimumAxisValue=lower;
			this.maximumAxisValue=upper;
			}
        }

    }


}
