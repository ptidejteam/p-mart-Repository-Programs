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
 * $Id: VerticalLogarithmicAxis.java,v 1.1 2007/10/10 19:52:15 vauchers Exp $
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
 * 25-Jul-2002 : Changed order of parameters in ValueAxis constructor (DG);
 * 16-Jul-2002 : Implemented support for plotting positive values arbitrarily
 *               close to zero (added 'allowNegativesFlag' flag) (ET).
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
import java.text.DecimalFormat;

/**
 * A logarithmic value axis, for values displayed vertically.  Display
 * of positive values arbitrarily close to zero is supported, as well as
 * negative values (if 'allowNegativesFlag' flag set).
 */
public class VerticalLogarithmicAxis extends VerticalNumberAxis  {

    /** Useful constant for log(10). */
    public static final double LOG10_VALUE = Math.log(10);

    /** Smallest arbitrarily-close-to-zero value allowed. */
    public static final double SMALL_LOG_VALUE = 1e-100;

    /** Flag set true for "10^n"-style tick labels. */
    protected boolean log10TickLabelsFlag;

    /** Flag set true to allow negative values in data. */
    protected boolean allowNegativesFlag;

    /** Helper flag for log axis processing. */
    protected boolean smallLogFlag = false;

    /** Number formatter for generating numeric strings. */
    protected final DecimalFormat numberFormatterObj =
                                               new DecimalFormat("0.00000");


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

        this(label, Axis.DEFAULT_AXIS_LABEL_FONT, 1, 10);

    }

    /**
     * Constructs a vertical logarithmic axis.
     *
     * @param label The axis label (null permitted).
     * @param labelFont The font for displaying the axis label.
     * @param lowerBound The lowest value shown on the axis.
     * @param upperBound The highest value shown on the axis.
     */
    public VerticalLogarithmicAxis(String label,
                                   Font labelFont,
                                   double lowerBound,
                                   double upperBound)
    {

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
             ValueAxis.DEFAULT_AUTO_RANGE_MINIMUM_SIZE,
             true, // auto range includes zero
             lowerBound,
             upperBound,
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
             true,      //'log10tickLabelsFlag' enabled for "10^n" labels
             false);    //'allowNegativesFlag' set false for no values < 0
    }

    /**
     * Constructs a vertical number axis.
     *
     * @param label The axis label (null permitted).
     * @param labelFont The font for displaying the axis label.
     * @param labelPaint The paint used to draw the axis label.
     * @param labelInsets Determines the amount of blank space around the label.
     * @param labelDrawnVertical Flag indicating whether or not the label is drawn vertically.
     * @param tickLabelsVisible Flag indicating whether or not the tick labels are visible.
     * @param tickLabelFont The font used to display tick labels.
     * @param tickLabelPaint The paint used to draw tick labels.
     * @param tickLabelInsets Determines the amount of blank space around tick labels.
     * @param tickMarksVisible Flag indicating whether or not tick marks are visible.
     * @param tickMarkStroke The stroke used to draw tick marks (if visible).
     * @param autoRange Flag indicating whether or not the axis is automatically scaled to fit the
     *        data.
     * @param autoRangeMinimumSize The smallest range allowed when the axis range is calculated to
     *        fit the data.
     * @param autoRangeIncludesZero A flag indicating whether or not zero *must* be displayed on
     *        axis.
     * @param lowerBound The lowest value shown on the axis.
     * @param upperBound The highest value shown on the axis.
     * @param inverted A flag indicating whether the axis is normal or inverted (inverted means
     *        running from positive to negative).
     * @param autoTickUnitSelection A flag indicating whether or not the tick units are selected
     *        automatically.
     * @param tickUnit The tick unit.
     * @param gridLinesVisible Flag indicating whether or not grid lines are visible for this axis.
     * @param gridStroke The pen/brush used to display grid lines (if visible).
     * @param gridPaint The color used to display grid lines (if visible).
     * @param crosshairVisible Whether to show a crosshair.
     * @param crosshairValue The value at which to draw an optional crosshair (null permitted).
     * @param crosshairStroke The pen/brush used to draw the crosshair.
     * @param crosshairPaint The color used to draw the crosshair.
     * @param log10TickLabelsFlag true for "10^n"-style tick labels,
     *        <code>false</code> for normal numeric tick labels.
     */
    public VerticalLogarithmicAxis(String label,
        Font labelFont, Paint labelPaint, Insets labelInsets,
        boolean labelDrawnVertical,
        boolean tickLabelsVisible, Font tickLabelFont, Paint tickLabelPaint,
        Insets tickLabelInsets,
        boolean tickMarksVisible, Stroke tickMarkStroke,
        boolean autoRange,
        Number autoRangeMinimumSize,
        boolean autoRangeIncludesZero,
        double lowerBound, double upperBound,
        boolean inverted,
        boolean autoTickUnitSelection,
        NumberTickUnit tickUnit,
        boolean gridLinesVisible, Stroke gridStroke, Paint gridPaint,
        boolean crosshairVisible, double crosshairValue,
        Stroke crosshairStroke, Paint crosshairPaint,
        boolean log10TickLabelsFlag,
        boolean allowNegativesFlag) {

        super(label,
              labelFont, labelPaint, labelInsets,
              labelDrawnVertical,
              tickLabelsVisible,
              tickLabelFont, tickLabelPaint, tickLabelInsets,
              tickMarksVisible,
              tickMarkStroke,
              autoRange,
              autoRangeMinimumSize,
              autoRangeIncludesZero, false,
              lowerBound, upperBound,
              inverted,
              autoTickUnitSelection, tickUnit,
              gridLinesVisible, gridStroke, gridPaint,
              crosshairVisible, crosshairValue, crosshairStroke, crosshairPaint);
        this.log10TickLabelsFlag = log10TickLabelsFlag;    //save flags
        this.allowNegativesFlag = allowNegativesFlag;
        if(!autoRange)                 //if not auto-ranging then
          setupSmallLogFlag();         //setup flag based on bounds values
    }

    /**
     * Sets the 'log10TickLabelsFlag' flag; true for "10^n"-style tick
     * labels, false for regular numeric labels.
     */
    public void setLog10TickLabelsFlag(boolean flgVal)
    {
      log10TickLabelsFlag = flgVal;
    }

    /**
     * Returns the 'log10TickLabelsFlag' flag; true for "10^n"-style tick
     * labels, false for regular numeric labels.
     */
    public boolean getLog10TickLabelsFlag()
    {
      return log10TickLabelsFlag;
    }

    /**
     * Sets the 'allowNegativesFlag' flag; true to allow negative values
     * in data, false to be able to plot positive values arbitrarily close
     * to zero.
     */
    public void setAllowNegativesFlag(boolean flgVal)
    {
      allowNegativesFlag = flgVal;
    }

    /**
     * Returns the 'allowNegativesFlag' flag; true to allow negative values
     * in data, false to be able to plot positive values arbitrarily close
     * to zero.
     */
    public boolean getAllowNegativesFlag()
    {
      return allowNegativesFlag;
    }

    /**
     * Overridden version that calls original and then sets up flag for
     * log axis processing.
     */
    public void setRange(Range range)
    {
      super.setRange(range);      //call parent mathod
      setupSmallLogFlag();        //setup flag based on bounds values
    }

    /**
     * Sets up flag for log axis processing.
     */
    protected void setupSmallLogFlag()
    {
              //set flag true if negative values not allowed and the
              // lower bound is between 0 and 10:
      final double lowerVal = range.getLowerBound();
      smallLogFlag = (!allowNegativesFlag && lowerVal < 10.0 &&
                                                            lowerVal > 0.0);
    }

    /**
     * Converts a data value to a coordinate in Java2D space, assuming that the axis runs along
     * one edge of the specified plot area.
     * <p>
     * Note that it is possible for the coordinate to fall outside the dataArea.
     *
     * @param value The data value.
     * @param dataArea The area for plotting the data.
     *
     * @return The Java2D coordinate.
     */
    public double translateValueToJava2D(double value, Rectangle2D dataArea) {

        double axisMin = switchedLog10(range.getLowerBound());
        double axisMax = switchedLog10(range.getUpperBound());

        double maxY = dataArea.getMaxY();
        double minY = dataArea.getMinY();

        value = switchedLog10(value);

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

        double axisMin = switchedLog10(range.getLowerBound());
        double axisMax = switchedLog10(range.getUpperBound());

        double plotY = dataArea.getY();
        double plotMaxY = dataArea.getMaxY();

        if (inverted) {
            return axisMin+Math.pow(10, ((java2DValue-plotY)/(plotMaxY-plotY))*(axisMax - axisMin));
        }
        else {
            return axisMax-Math.pow(10, ((java2DValue-plotY)/(plotMaxY-plotY))*(axisMax - axisMin));
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
            if (r==null) {
                r = new Range(DEFAULT_LOWER_BOUND, DEFAULT_UPPER_BOUND);
            }

            double lower = computeLogFloor(r.getLowerBound());

            if(!allowNegativesFlag && lower >= 0.0 &&
                                                    lower < SMALL_LOG_VALUE)
            {      //negatives not allowed and lower range bound is zero
              lower = r.getLowerBound();    //use data range bound instead
            }

//            double upper = computeLogCeil(r.getUpperBound());
            double upper = r.getUpperBound();
            if(!allowNegativesFlag && upper < 1.0 && upper > 0.0 &&
                                                                lower > 0.0)
            {      //negatives not allowed and upper bound between 0 & 1
                        //round up to nearest significant digit for bound:
                                                 //get negative exponent:
              double expVal = Math.log(upper) / LOG10_VALUE;
              expVal = Math.ceil(-expVal+0.001); //get positive exponent
              expVal = Math.pow(10,expVal);      //create multiplier value
                        //multiply, round up, and divide for bound value:
              upper = (expVal > 0.0) ? Math.ceil(upper*expVal)/expVal :
                                                           Math.ceil(upper);
            }
            else   //negatives allowed or upper bound not between 0 & 1
              upper = computeLogCeil(upper);  //use nearest log value

            // ensure the autorange is at least <minRange> in size...
            double minRange = this.autoRangeMinimumSize.doubleValue();
            if (upper-lower < minRange)
            {
              upper = (upper+lower+minRange) / 2;
              lower = (upper+lower-minRange) / 2;
            }

            this.range = new Range(lower, upper);

            setupSmallLogFlag();       //setup flag based on bounds values
        }
    }

    /**
     * Returns the smallest (closest to negative infinity) double value that is
     * not less than the argument, is equal to a mathematical integer and
     * satisfying the condition that log base 10 of the value is an integer
     * (i.e., the value returned will be a power of 10: 1, 10, 100, 1000, etc.).
     *
     * @param upper a double value above which a ceiling will be calcualted.
     *
     * @return 10<sup>N</sup> with N .. { 1 ... }
     */
    private double computeLogCeil(double upper) {

        double logCeil;
        if(allowNegativesFlag)
        {     //negative values are allowed
          if(upper > 10.0)
          {   //parameter value is > 10
            // The Math.log() function is based on e not 10.
            logCeil = Math.log(upper) / LOG10_VALUE;
            logCeil = Math.ceil(logCeil);
            logCeil = Math.pow(10, logCeil);
          }
          else if(upper < -10.0)
          {   //parameter value is < -10
                     //calculate log using positive value:
            logCeil = Math.log(-upper) / LOG10_VALUE;
                     //calculate ceil using negative value:
            logCeil = Math.ceil(-logCeil);
                     //calculate power using positive value; then negate
            logCeil = -Math.pow(10, -logCeil);
          }
          else       //parameter value is -10 > val < 10
            logCeil = Math.ceil(upper);     //use as-is
        }
        else
        {     //negative values not allowed
          if(upper > 0.0)
          {     //parameter value is > 0
            // The Math.log() function is based on e not 10.
            logCeil = Math.log(upper) / LOG10_VALUE;
            logCeil = Math.ceil(logCeil);
            logCeil = Math.pow(10, logCeil);
          }
          else     //parameter value is <= 0
            logCeil = Math.ceil(upper);     //use as-is
        }
        return logCeil;
    }

    /**
     * Returns the largest (closest to positive infinity) double value that is
     * not greater than the argument, is equal to a mathematical integer and
     * satisfying the condition that log base 10 of the value is an integer
     * (i.e., the value returned will be a power of 10: 1, 10, 100, 1000, etc.).
     *
     * @param lower a double value below which a floor will be calcualted.
     *
     * @return 10<sup>N</sup> with N .. { 1 ... }
     */
    private double computeLogFloor(double lower) {

        double logFloor;
        if(allowNegativesFlag)
        {     //negative values are allowed
          if(lower > 10.0)
          {   //parameter value is > 10
            // The Math.log() function is based on e not 10.
            logFloor = Math.log(lower) / LOG10_VALUE;
            logFloor = Math.floor(logFloor);
            logFloor = Math.pow(10, logFloor);
          }
          else if(lower < -10.0)
          {   //parameter value is < -10
                   //calculate log using positive value:
            logFloor = Math.log(-lower) / LOG10_VALUE;
                   //calculate floor using negative value:
            logFloor = Math.floor(-logFloor);
                   //calculate power using positive value; then negate
            logFloor = -Math.pow(10, -logFloor);
          }
          else     //parameter value is -10 > val < 10
            logFloor = Math.floor(lower);   //use as-is
        }
        else
        {     //negative values not allowed
          if(lower > 0.0)
          {   //parameter value is > 0
            // The Math.log() function is based on e not 10.
            logFloor = Math.log(lower) / LOG10_VALUE;
            logFloor = Math.floor(logFloor);
            logFloor = Math.pow(10, logFloor);
          }
          else     //parameter value is <= 0
            logFloor = Math.floor(lower);   //use as-is
        }
        return logFloor;
    }

    /**
     * Calculates the positions of the tick labels for the axis, storing the
     * results in the tick label list (ready for drawing).
     *
     * @param g2 The graphics device.
     * @param plotArea The area in which the plot and the axes should be drawn.
     * @param dataArea The area in which the plot should be drawn.
     */
    public void refreshTicks(Graphics2D g2, Rectangle2D plotArea, Rectangle2D dataArea) {

        ticks.clear();

        //get lower bound value:
        double lowerBoundVal = range.getLowerBound();
        //if small log values and lower bound value too small
        // then set to a small value (don't allow <= 0):
        if(smallLogFlag && lowerBoundVal < SMALL_LOG_VALUE)
            lowerBoundVal = SMALL_LOG_VALUE;
        //get upper bound value
        final double upperBoundVal = range.getUpperBound();

        //get log10 version of lower bound and round to integer:
        final int iBegCount = (int)Math.rint(switchedLog10(lowerBoundVal));
        //get log10 version of upper bound and round to integer:
        final int iEndCount = (int)Math.rint(switchedLog10(upperBoundVal));

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
                if(smallLogFlag)
                {    //small log values in use
                    tickVal = Math.pow(10,i) + (Math.pow(10,i)*j);
                    if(j == 0)
                    {  //first tick of group; create label text
                        if(log10TickLabelsFlag)       //if flag then
                            tickLabel = "10^" + i;      //create "log10"-type label
                        else
                        {    //not "log10"-type label
                            if(i >= 0)   //if positive exponent then make integer
                                tickLabel =  Long.toString((long)Math.rint(tickVal));
                            else
                            {  //negative exponent; create fractional value
                        //set exact number of fractional digits to be shown:
                                numberFormatterObj.setMaximumFractionDigits(-i);
                                            //create tick label:
                                tickLabel = numberFormatterObj.format(tickVal);
                            }
                        }
                    }
                    else   //not first tick to be displayed
                        tickLabel = "";     //no tick label
                }
                else
                { //not small log values in use; allow for values <= 0
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
                }

                if (tickVal > upperBoundVal)
                    return;     //if past highest data value then exit method

                      //get Y-position for tick:
                double yy = this.translateValueToJava2D(tickVal, dataArea);
                      //get bounds for tick label:
                Rectangle2D tickLabelBounds = tickLabelFont.getStringBounds(
                                    tickLabel, g2.getFontRenderContext());
                      //get X-position for tick label:
                float x = (float)(dataArea.getX()
                            - tickLabelBounds.getWidth()
                            - tickLabelInsets.left
                            - tickLabelInsets.right);
                      //get Y-position for tick label:
                float y = (float)(yy + (tickLabelBounds.getHeight()/3));

                      //create tick object and add to list:
            ticks.add(new Tick(new Double(tickVal), tickLabel, x, y));
            }
        }
    }

    /**
     * Returns the log10 value, depending on if values between 0 and
     * 1 are being plotted.
     */
    protected double switchedLog10(double val)
    {
      return smallLogFlag ? Math.log(val)/LOG10_VALUE : adjustedLog10(val);
    }

    /**
     * Returns an adjusted log10 value for graphing purposes.  The first
     * adjustment is that negative values are changed to positive during
     * the calculations, and then the answer is negated at the end.  The
     * second is that, for values less than 10, an increasingly large
     * (0 to 1) scaling factor is added such that at 0 the value is
     * adjusted to 1, resulting in a returned result of 0.
     *
     * @param val the value.
     * @return the adjusted value.
     */
    public double adjustedLog10(double val)
    {
      final boolean negFlag;
      if(negFlag = (val < 0.0))
        val = -val;          //if negative then set flag and make positive
      if (val<10.0)                   //if < 10 then
        val += (10.0-val) / 10;        //increase so 0 translates to 0
              //return value; negate if original value was negative:
      return negFlag
          ? -(Math.log(val) / LOG10_VALUE)
          : (Math.log(val) / LOG10_VALUE);
    }

}
