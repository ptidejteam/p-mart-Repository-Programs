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
 * ----------------------------
 * VerticalLogarithmicAxis.java
 * ----------------------------
 * (C) Copyright 2000-2002, by Simba Management Limited and Contributors.
 *
 * Original Author:  Michael Duffy;
 * Contributor(s):   David Gilbert (for Simba Management Limited);
 *                   Eric Thomas;
 *
 * $Id: VerticalLogarithmicAxis.java,v 1.1 2007/10/10 19:41:59 vauchers Exp $
 *
 * Changes
 * -------
 * 14-Mar-2002 : Version 1 contributed by Michael Duffy (DG);
 * 19-Apr-2002 : drawVerticalString(...) is now drawRotatedString(...) in RefineryUtilities (DG);
 * 23-Apr-2002 : Added a range property (DG);
 * 15-May-2002 : Modified to be able to deal with negative and zero values (via new
 *               'adjustedLog10()' method);  occurrences of "Math.log(10)" changed to "LOG10_VALUE";
 *               changed 'intValue()' to 'longValue()' in 'refreshTicks()' to fix label-text value
 *               out-of-range problem; removed 'draw()' method; added 'autoRangeMinimumSize' check;
 *               added 'log10TickLabelsFlag' parameter flag and implementation (ET);
 * 25-Jun-2002 : Removed redundant import (DG);
 *
 */

package com.jrefinery.chart;

import com.jrefinery.data.Range;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Stroke;
import java.awt.Paint;
import java.awt.Font;
import java.awt.geom.Rectangle2D;

/**
 * A vertical logarithmic value axis.  Display of negative values is supported.
 */
public class VerticalLogarithmicAxis extends VerticalNumberAxis  {

    /** Useful constant for log(10). */
    public static final double LOG10_VALUE = Math.log(10);

    /** ??? */
    protected final boolean log10TickLabelsFlag;

    /**
     * Constructs a vertical logarithmic axis, using default values where necessary.
     */
    public VerticalLogarithmicAxis() {

        this(null);

    }

    /**
     * Constructs a vertical logarithmic axis, using default values where necessary.
     *
     * @param label The axis label (null permitted).
     */
    public VerticalLogarithmicAxis(String label) {

        this(label,
             Axis.DEFAULT_AXIS_LABEL_FONT,
             1,
             10);

    }

    /**
     * Constructs a vertical logarithmic axis.
     *
     * @param label The axis label (null permitted).
     * @param labelFont The font for displaying the axis label.
     * @param minimumAxisValue The lowest value shown on the axis.
     * @param maximumAxisValue The highest value shown on the axis.
     */
    public VerticalLogarithmicAxis(String label,
                                   Font labelFont,
                                   double minimumAxisValue,
                                   double maximumAxisValue) {

        this(label,
             labelFont,
             Axis.DEFAULT_AXIS_LABEL_PAINT,
             Axis.DEFAULT_AXIS_LABEL_INSETS,
             true, // vertical axis label
             true, // tick labels visible
             Axis.DEFAULT_TICK_LABEL_FONT,
             Axis.DEFAULT_TICK_LABEL_PAINT,
             Axis.DEFAULT_TICK_LABEL_INSETS,
             true, // tick marks visible
             Axis.DEFAULT_TICK_STROKE,
             true, // auto range
             true, // auto range includes zero
             NumberAxis.DEFAULT_MINIMUM_AUTO_RANGE,
             minimumAxisValue,
             maximumAxisValue,
             false, // inverted
             true, // auto tick unit selection
             NumberAxis.DEFAULT_TICK_UNIT,
             true, // grid lines visible
             ValueAxis.DEFAULT_GRID_LINE_STROKE,
             ValueAxis.DEFAULT_GRID_LINE_PAINT,
             ValueAxis.DEFAULT_CROSSHAIR_VISIBLE,
             0.0,
             ValueAxis.DEFAULT_CROSSHAIR_STROKE,
             ValueAxis.DEFAULT_CROSSHAIR_PAINT,
             true);  //'log10tickLabelsFlag' enabled for "10^n" labels
    }

    /**
     * Constructs a vertical number axis.
     *
     * @param label The axis label.
     * @param labelFont The font for displaying the axis label.
     * @param labelPaint The paint used to draw the axis label.
     * @param labelInsets Determines the amount of blank space around the label.
     * @param labelDrawnVertical Flag indicating whether or not the label is drawn vertically.
     * @param tickMarksVisible Flag indicating whether or not tick labels are visible.
     * @param tickLabelFont The font used to display tick labels.
     * @param tickLabelPaint The paint used to draw tick labels.
     * @param tickLabelInsets Determines the amount of blank space around tick labels.
     * @param showTickMarks Flag indicating whether or not tick marks are visible.
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
     * @param showGridLines Flag indicating whether or not grid lines are visible for this axis.
     * @param gridStroke The pen/brush used to display grid lines (if visible).
     * @param gridPaint The color used to display grid lines (if visible).
     * @param crosshairValue The value at which to draw an optional crosshair (null permitted).
     * @param crosshairStroke The pen/brush used to draw the crosshair.
     * @param crosshairPaint The color used to draw the crosshair.
     * @param log10TickLabelsFlag true for "10^n"-style tick labels, false
     * for normal numeric tick labels.
     */
    public VerticalLogarithmicAxis(String label,
                                   Font labelFont, Paint labelPaint, Insets labelInsets,
                                   boolean labelDrawnVertical,
                                   boolean tickLabelsVisible, Font tickLabelFont, Paint tickLabelPaint,
                                   Insets tickLabelInsets,
                                   boolean tickMarksVisible, Stroke tickMarkStroke,
                                   boolean autoRange, boolean autoRangeIncludesZero,
                                   Number autoRangeMinimum,
                                   double minimumAxisValue, double maximumAxisValue,
                                   boolean inverted,
                                   boolean autoTickUnitSelection,
                                   NumberTickUnit tickUnit,
                                   boolean gridLinesVisible, Stroke gridStroke, Paint gridPaint,
                                   boolean crosshairVisible, double crosshairValue,
                                   Stroke crosshairStroke, Paint crosshairPaint,
                                   boolean log10TickLabelsFlag) {

        super(label,
              labelFont, labelPaint, labelInsets,
              labelDrawnVertical,
              tickLabelsVisible,
              tickLabelFont, tickLabelPaint, tickLabelInsets,
              tickMarksVisible,
              tickMarkStroke,
              autoRange, autoRangeIncludesZero, false, autoRangeMinimum,
              minimumAxisValue, maximumAxisValue,
              inverted,
              autoTickUnitSelection, tickUnit,
              gridLinesVisible, gridStroke, gridPaint,
              crosshairVisible, crosshairValue, crosshairStroke, crosshairPaint);
        this.log10TickLabelsFlag = log10TickLabelsFlag;
    }

    /**
     * Converts a data value to a coordinate in Java2D space, assuming that the axis runs along
     * one edge of the specified plot area.
     * <p>
     * Note that it is possible for the coordinate to fall outside the dataArea.
     *
     * @param dataValue The data value.
     * @param dataArea The area for plotting the data.
     *
     * @return The Java2D coordinate.
     */
    public double translateValueToJava2D(double value, Rectangle2D dataArea) {

        double axisMin = adjustedLog10(range.getLowerBound());
        double axisMax = adjustedLog10(range.getUpperBound());

        double maxY = dataArea.getMaxY();
        double minY = dataArea.getMinY();

//        System.out.print("translateValueToJava2D(" + value + "):  ");

        value = adjustedLog10(value);

//        System.out.println("axisMin=" + axisMin + ", axisMax=" + axisMax +
//                 ", minY=" + minY + ", maxY=" + maxY + ", value=" + value +
//                                                  ", retVal=" + (inverted ?
//                         (minY + (((value - axisMin)/(axisMax - axisMin)) *
//                                                          (maxY - minY))) :
//                         (maxY - (((value - axisMin)/(axisMax - axisMin)) *
//                                                         (maxY - minY)))));

        if (inverted) {
            return minY + (((value - axisMin)/(axisMax - axisMin)) * (maxY - minY));
        }
        else {
            return maxY - (((value - axisMin)/(axisMax - axisMin)) * (maxY - minY));
        }

    }

    /**
     * Converts a coordinate in Java2D space to the corresponding data value, assuming that the
     * axis runs along one edge of the specified dataArea.
     *
     * @param java2DValue The coordinate in Java2D space.
     * @param dataArea The area in which the data is plotted.
     *
     * @return The data value.
     */
    public double translateJava2DtoValue(float java2DValue, Rectangle2D dataArea) {

        double axisMin = adjustedLog10(range.getLowerBound());
        double axisMax = adjustedLog10(range.getUpperBound());

        double plotY = dataArea.getY();
        double plotMaxY = dataArea.getMaxY();

        if (inverted) {
            return axisMin + Math.pow(10, ((java2DValue-plotY)/(plotMaxY-plotY))*(axisMax - axisMin));
        }
        else {
            return axisMax - Math.pow(10, ((java2DValue-plotY)/(plotMaxY-plotY))*(axisMax - axisMin));
        }
    }

    /**
     * Rescales the axis to ensure that all data is visible.
     */
    public void autoAdjustRange() {

        if (plot==null) return;  // no plot, no data.

        if (plot instanceof VerticalValuePlot) {
            VerticalValuePlot vvp = (VerticalValuePlot)plot;

            Range r = vvp.getVerticalDataRange();
            if (r == null) r = new Range(DEFAULT_MINIMUM_AXIS_VALUE, DEFAULT_MAXIMUM_AXIS_VALUE);

            double upper = computeLogCeil(r.getUpperBound());
            double lower = computeLogFloor(r.getLowerBound());

            // ensure the autorange is at least <minRange> in size...
            double minRange = this.autoRangeMinimumSize.doubleValue();
            if (upper-lower < minRange)
            {
              upper = (upper+lower+minRange) / 2;
              lower = (upper+lower-minRange) / 2;
            }

            this.range = new Range(lower, upper);
        }

    }

    /**
     * Returns the smallest (closest to negative infinity) double value that is
     * not less than the argument, is equal to a mathematical integer and
     * satisfying the condition that log base 10 of the value is an integer
     * (i.e., the value returned will be a power of 10: 1, 10, 100, 1000, etc.).
     *
     * @param lower  a double value above which a ceiling will be calcualted.
     */
    private double computeLogCeil(double upper) {

        double logCeil;
        if(upper > 10.0)
        {     //parameter value is > 10
          // The Math.log() funtion is based on e not 10.
          logCeil = Math.log(upper) / LOG10_VALUE;
          logCeil = Math.ceil(logCeil);
          logCeil = Math.pow(10, logCeil);
        }
        else if(upper < -10.0)
        {     //parameter value is < -10
                   //calculate log using positive value:
          logCeil = Math.log(-upper) / LOG10_VALUE;
                   //calculate ceil using negative value:
          logCeil = Math.ceil(-logCeil);
                   //calculate power using positive value; then negate
          logCeil = -Math.pow(10, -logCeil);
        }
        else       //parameter value is -10 > val < 10
          logCeil = Math.ceil(upper);       //use as-is
        return logCeil;
    }

    /**
     * Returns the largest (closest to positive infinity) double value that is
     * not greater than the argument, is equal to a mathematical integer and
     * satisfying the condition that log base 10 of the value is an integer
     * (i.e., the value returned will be a power of 10: 1, 10, 100, 1000, etc.).
     *
     * @param lower  a double value below which a floor will be calcualted.
     */
    private double computeLogFloor(double lower) {

        double logFloor;
        if(lower > 10.0)
        {     //parameter value is > 10
          // The Math.log() funtion is based on e not 10.
          logFloor = Math.log(lower) / LOG10_VALUE;
          logFloor = Math.floor(logFloor);
          logFloor = Math.pow(10, logFloor);
        }
        else if(lower < -10.0)
        {     //parameter value is < -10
                   //calculate log using positive value:
          logFloor = Math.log(-lower) / LOG10_VALUE;
                   //calculate floor using negative value:
          logFloor = Math.floor(-logFloor);
                   //calculate power using positive value; then negate
          logFloor = -Math.pow(10, -logFloor);
        }
        else       //parameter value is -10 > val < 10
          logFloor = Math.floor(lower);     //use as-is
        return logFloor;          //return zero
    }

    /**
     * Calculates the positions of the tick labels for the axis, storing the results in the
     * tick label list (ready for drawing).
     *
     * @param g2 The graphics device.
     * @param drawArea The area in which the plot and the axes should be drawn.
     * @param plotArea The area in which the plot should be drawn.
     */
    public void refreshTicks(Graphics2D g2, Rectangle2D drawArea, Rectangle2D plotArea) {

        ticks.clear();

        final int iBegCount = (int)Math.rint(adjustedLog10(
                                                    range.getLowerBound()));
        final int iEndCount = (int)Math.rint(adjustedLog10(
                                                    range.getUpperBound()));

        double tickVal;
        String tickLabel;
        boolean zeroTickFlag = false;
        for (int i = iBegCount; i <= iEndCount; i++)
        {     //for each tick with a label to be displayed
            int jEndCount = 10;
            if (i == iEndCount) {
                jEndCount = 1;
            }

            for (int j = 0; j < jEndCount; j++)
            {      //for each tick to be displayed
                if(zeroTickFlag)       //if did zero tick last iter then
                  --j;                 //decrement to do 1.0 tick now
                tickVal = (i >= 0) ? Math.pow(10,i) + (Math.pow(10,i)*j) :
                                 -(Math.pow(10,-i) - (Math.pow(10,-i-1)*j));
                if(j == 0)
                {  //first tick of group
                  if(!zeroTickFlag)
                  {     //did not do zero tick last iteration
                    if(i > iBegCount && i < iEndCount &&
                                             Math.abs(tickVal-1.0) < 0.0001)
                    {   //not first or last tick on graph and value is 1.0
                      tickVal = 0.0;        //change value to 0.0
                      zeroTickFlag = true;  //indicate zero tick
                      tickLabel = "0";      //create label for tick
                    }
                    else
                    {   //first or last tick on graph or value is 1.0
                        //create label for tick ("log10" label if flag):
                      tickLabel = log10TickLabelsFlag ?
                                        (((i<0)?"-":"")+"10^"+Math.abs(i)) :
                                    Long.toString((long)Math.rint(tickVal));
                    }
                  }
                  else
                  {     //did zero tick last iteration
                    tickLabel = "";         //no label
                    zeroTickFlag = false;   //clear flag
                  }
                }
                else
                {       //not first tick of group
                  tickLabel = "";           //no label
                  zeroTickFlag = false;     //make sure flag cleared
                }
                        //create 'Number' holder for tick value:
                Number currentTickValue = new Double(tickVal);
                        //get Y-position for tick:
                double yy = this.translateValueToJava2D(tickVal, plotArea);
                        //get bounds for tick label:
                Rectangle2D tickLabelBounds = tickLabelFont.getStringBounds(
                                      tickLabel, g2.getFontRenderContext());
                        //get X-position for tick label:
                float x = (float)(plotArea.getX()
                                  - tickLabelBounds.getWidth()
                                  - tickLabelInsets.left
                                  - tickLabelInsets.right);
                        //get Y-position for tick label:
                float y = (float)(yy + (tickLabelBounds.getHeight()/3));

//                System.out.println("tick(" + currentTickValue + ", \"" +
//                                         tickLabel + "\", " + x + ", " + y);

                        //create tick object and add to list:
                ticks.add(new Tick(currentTickValue, tickLabel, x, y));
            }
        }
    }

    /**
     * Returns an adjusted log10 value for graphing purposes.  The first
     * adjustment is that negative values are changed to positive during
     * the calculations, and then the answer is negated at the end.  The
     * second is that, for values less than 10, an increasingly large
     * (0 to 1) scaling factor is added such that at 0 the value is
     * adjusted to 1, resulting in a returned result of 0.
     */
    public double adjustedLog10(double val)
    {
      final boolean negFlag;
      if(negFlag = (val < 0.0))
        val = -val;          //if negative then set flag and make positive
      if(val < 10.0)                   //if < 10 then
        val += (10.0-val) / 10;        //increase so 0 translates to 0
              //return value; negate if original value was negative:
      return negFlag ? -(Math.log(val) / LOG10_VALUE) :
                                              (Math.log(val) / LOG10_VALUE);
    }

}
