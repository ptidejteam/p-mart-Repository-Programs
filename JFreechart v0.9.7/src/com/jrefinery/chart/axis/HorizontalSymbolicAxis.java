/* ======================================
 * JFreeChart : a free Java chart library
 * ======================================
 *
 * Project Info:  http://www.object-refinery.com/jfreechart/index.html
 * Project Lead:  David Gilbert (david.gilbert@object-refinery.com);
 *
 * (C) Copyright 2000-2003, by Simba Management Limited and Contributors.
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
 * HorizontalSymbolicAxis.java
 * ---------------------------
 * (C) Copyright 2002, 2003, by Simba Management Limited and Contributors.
 *
 * Original Author:  Anthony Boulestreau;
 * Contributor(s):   David Gilbert (for Simba Management Limited);
 *
 *
 * Changes (from 23-Jun-2001)
 * --------------------------
 * 29-Mar-2002 : First version (AB);
 * 19-Apr-2002 : Updated formatting and import statements (DG);
 * 21-Jun-2002 : Make change to use the class TickUnit - remove valueToString(...) method and
 *               add SymbolicTickUnit (AB);
 * 25-Jun-2002 : Removed redundant code (DG);
 * 25-Jul-2002 : Changed order of parameters in ValueAxis constructor (DG);
 * 05-Sep-2002 : Updated constructor to reflect changes in the Axis class (DG);
 * 08-Nov-2002 : Moved to new package com.jrefinery.chart.axis (DG);
 * 14-Feb-2003 : Added back missing constructor code (DG);
 * 26-Mar-2003 : Implemented Serializable (DG);
 *
 */

package com.jrefinery.chart.axis;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Paint;
import java.awt.font.FontRenderContext;
import java.awt.font.LineMetrics;
import java.awt.geom.Rectangle2D;
import java.io.Serializable;
import java.text.NumberFormat;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import com.jrefinery.chart.event.AxisChangeEvent;
import com.jrefinery.chart.plot.HorizontalValuePlot;
import com.jrefinery.chart.plot.Plot;
import com.jrefinery.data.Range;

/**
 * A standard linear value axis, for SYMBOLIC values displayed horizontally.
 *
 * @author Anthony Boulestreau
 */
public class HorizontalSymbolicAxis extends HorizontalNumberAxis 
                                    implements HorizontalAxis, Serializable {

    /** The default symbolic grid line paint. */
    public static final Paint DEFAULT_SYMBOLIC_GRID_LINE_PAINT = new Color(232, 234, 232);

    /** The list of symbolic value to display instead of the numeric values. */
    private List symbolicValue;

    /** Enable or not the zoom. **/
    private boolean xSymbolicZoomIsAccepted = false;

    /** List of the symbolic grid lines shapes. */
    private List symbolicGridLineList = null;

    /** Color of the dark part of the symbolic grid line. **/
    private transient Paint symbolicGridPaint;

    /** Flag that indicates whether or not symbolic grid lines are visible. */
    private boolean symbolicGridLinesVisible;

    /**
     * Constructs a horizontal symbolic axis, using default attribute values where necessary.
     *
     * @param label  the axis label (null permitted).
     * @param sv  the list of symbolic values to display instead of the numeric value.
     */
    public HorizontalSymbolicAxis(String label, String[] sv) {

        super(label);

        //initialization of symbolic value
        this.symbolicValue = Arrays.asList(sv);
        this.symbolicGridLinesVisible = true;
        this.symbolicGridPaint = DEFAULT_SYMBOLIC_GRID_LINE_PAINT;
        
        setAutoTickUnitSelection(false, false);
        setAutoRangeStickyZero(false);
        
    }
                                  
    /**
     * Returns the list of the symbolic values to display.
     *
     * @return list of symbolic values.
     */
    public String[] getSymbolicValue() {

        String[] strToReturn = new String[symbolicValue.size()];
        strToReturn = (String[]) symbolicValue.toArray(strToReturn);
        return strToReturn;
    }

    /**
     * Returns the symbolic grid line color.
     *
     * @return the grid line color.
     */
    public Paint getSymbolicGridPaint() {
        return symbolicGridPaint;
    }

    /**
     * Returns <CODE>true</CODE> if the symbolic grid lines are showing, and
     * false otherwise.
     *
     * @return true if the symbolic grid lines are showing, and false otherwise.
     */
    public boolean isGridLinesVisible() {
        return symbolicGridLinesVisible;
    }

    /**
     * Sets the visibility of the symbolic grid lines and notifies registered
     * listeners that the axis has been modified.
     *
     * @param flag  the new setting.
     */
    public void setSymbolicGridLinesVisible(boolean flag) {

        if (symbolicGridLinesVisible != flag) {
            symbolicGridLinesVisible = flag;
            notifyListeners(new AxisChangeEvent(this));
        }
    }

    /**
     * Redefinition of setAnchoredRange for the symbolicvalues.
     *
     * @param range  the new range.
     */
    public void setAnchoredRange(double range) {

        if (xSymbolicZoomIsAccepted) {
            //compute the corresponding integer corresponding to the anchor
            //position
            double anchor = Math.rint(getAnchorValue());
            double min = Math.rint(anchor - range / 2) - 0.5;
            double max = Math.rint(anchor + range / 2) + 0.5;
            if (min < -0.5) {
                min = -0.5;
            }
            if (max > symbolicValue.size() - 0.5) {
                max = symbolicValue.size() - 0.5;
            }
            setRange(min, max);
        }

    }

    /**
     * This operation is not supported by the symbolic values.
     *
     * @param g2  the graphics device.
     * @param drawArea  the area in which the plot and axes should be drawn.
     * @param plotArea  the area in which the plot should be drawn.
     */
    protected void selectAutoTickUnit(Graphics2D g2, Rectangle2D drawArea, Rectangle2D plotArea) {
        throw new UnsupportedOperationException();
    }

    /**
     * Draws the axis on a Java 2D graphics device (such as the screen or a printer).
     *
     * @param g2  the graphics device.
     * @param plotArea  the area within which the plot and axes should be drawn.
     * @param dataArea  the area within which the data should be drawn (a subset of the plotArea).
     * @param location  the axis location.
     */
    public void draw(Graphics2D g2, Rectangle2D plotArea, Rectangle2D dataArea, int location) {

        if (isVisible()) {
            super.draw(g2, plotArea, dataArea, location);
        }
        if (symbolicGridLinesVisible) {
            drawSymbolicGridLines(g2, plotArea, dataArea);
        }

    }

    /**
     * Draws the symbolic grid lines.
     * <P>
     * The colors are consecutively the color specified by
     * <CODE>symbolicGridPaint<CODE>
     * (<CODE>DEFAULT_SYMBOLIC_GRID_LINE_PAINT</CODE> by default) and white.
     *
     * @param g2  the graphics device.
     * @param plotArea  the area within which the chart should be drawn.
     * @param dataArea  the area within which the plot should be drawn (a subset of the drawArea).
     */
    public void drawSymbolicGridLines(Graphics2D g2, Rectangle2D plotArea, Rectangle2D dataArea) {
        drawSymbolicGridLines(g2, plotArea, dataArea, true);
    }

    /**
     * Draws the symbolic grid lines.
     * <P>
     * The colors are consecutively the color specified by
     * <CODE>symbolicGridPaint<CODE>
     * (<CODE>DEFAULT_SYMBOLIC_GRID_LINE_PAINT</CODE> by default) and white.
     * or if <CODE>firstGridLineIsDark</CODE> is <CODE>true</CODE> white and
     * the color specified by <CODE>symbolicGridPaint<CODE>.
     *
     * @param g2  the graphics device.
     * @param plotArea  the area within which the chart should be drawn.
     * @param dataArea  the area within which the plot should be drawn
     *                  (a subset of the drawArea).
     * @param firstGridLineIsDark  True: the first symbolic grid line take the
     *                             color of <CODE>symbolicGridPaint<CODE>.
     *                             False: the first symbolic grid line is white.
     */
    public void drawSymbolicGridLines(Graphics2D g2,
                                      Rectangle2D plotArea, Rectangle2D dataArea,
                                      boolean firstGridLineIsDark) {

        this.symbolicGridLineList = new Vector(getTicks().size());
        boolean currentGridLineIsDark = firstGridLineIsDark;
        double yy = dataArea.getY();
        double xx1, xx2;

        //gets the outline stroke width of the plot
        double outlineStrokeWidth;
        if (getPlot().getOutlineStroke() !=  null) {
            outlineStrokeWidth = ((BasicStroke) getPlot().getOutlineStroke()).getLineWidth();
        }
        else {
            outlineStrokeWidth = 1d;
        }

        Iterator iterator = getTicks().iterator();
        Tick tick;
        Rectangle2D symbolicGridLine;
        while (iterator.hasNext()) {
            tick = (Tick) iterator.next();
            xx1 = translateValueToJava2D(tick.getNumericalValue() - 0.5d, dataArea);
            xx2 = translateValueToJava2D(tick.getNumericalValue() + 0.5d, dataArea);
            if (currentGridLineIsDark) {
                g2.setPaint(Color.white);
                g2.setXORMode((Color) symbolicGridPaint);
            }
            else {
                g2.setPaint(Color.white);
                g2.setXORMode(Color.white);
            }
            symbolicGridLine =
                new Rectangle2D.Double(xx1,
                                       yy + outlineStrokeWidth, xx2 - xx1,
                                       dataArea.getMaxY() - yy - outlineStrokeWidth);
            g2.fill(symbolicGridLine);
            symbolicGridLineList.add(symbolicGridLine);
            currentGridLineIsDark = !currentGridLineIsDark;
        }
        g2.setPaintMode();
    }

    /**
     * Get the symbolic grid line corresponding to the specified position.
     *
     * @param position  position of the grid line, startinf from 0.
     *
     * @return the symbolic grid line corresponding to the specified position.
     */
    public Rectangle2D.Double getSymbolicGridLine(int position) {

        if (symbolicGridLineList != null) {
            return (Rectangle2D.Double) symbolicGridLineList.get(position);
        }
        else {
            return null;
        }

    }

    /**
     * Rescales the axis to ensure that all data is visible.
     */
    protected void autoAdjustRange() {

        Plot plot = getPlot();
        if (plot == null) {
            return;  // no plot, no data
        }

        if (plot instanceof HorizontalValuePlot) {

            //ensure that all the symbolic value are displayed
            double upper = symbolicValue.size() - 1;
            double lower = 0;
            double range = upper - lower;

            // ensure the autorange is at least <minRange> in size...
            double minRange = getAutoRangeMinimumSize();
            if (range < minRange) {
                upper = (upper + lower + minRange) / 2;
                lower = (upper + lower - minRange) / 2;
            }

            //this ensure that the symbolic grid lines will be displayed
            //correctly.
            double upperMargin = 0.5;
            double lowerMargin = 0.5;

            if (autoRangeIncludesZero()) {
                if (autoRangeStickyZero()) {
                    if (upper <= 0.0) {
                        upper = 0.0;
                    }
                    else {
                        upper = upper + upperMargin;
                    }
                    if (lower >= 0.0) {
                        lower = 0.0;
                    }
                    else {
                        lower = lower - lowerMargin;
                    }
                }
                else {
                    upper = Math.max(0.0, upper + upperMargin);
                    lower = Math.min(0.0, lower - lowerMargin);
                }
            }
            else {
                if (autoRangeStickyZero()) {
                    if (upper <= 0.0) {
                        upper = Math.min(0.0, upper + upperMargin);
                    }
                    else {
                        upper = upper + upperMargin * range;
                    }
                    if (lower >= 0.0) {
                        lower = Math.max(0.0, lower - lowerMargin);
                    }
                    else {
                        lower = lower - lowerMargin;
                    }
                }
                else {
                    upper = upper + upperMargin;
                    lower = lower - lowerMargin;
                }
            }

            setRangeAttribute(new Range(lower, upper));

        }

    }
    
    /**
     * Calculates the positions of the tick labels for the axis, storing the results in the
     * tick label list (ready for drawing).
     *
     * @param g2  the graphics device.
     * @param plotArea  the area in which the plot (inlcuding axes) should be drawn.
     * @param dataArea  the area in which the data should be drawn.
     * @param location  the location of the axis.
     */
    public void refreshTicks(Graphics2D g2,
                             Rectangle2D plotArea, Rectangle2D dataArea,
                             int location) {

        getTicks().clear();

        Font tickLabelFont = getTickLabelFont();
        g2.setFont(tickLabelFont);
        FontRenderContext frc = g2.getFontRenderContext();

        //if (isAutoTickUnitSelection()) {
        //    selectAutoTickUnit(g2, plotArea, dataArea);
        //}

        double size = getTickUnit().getSize();
        int count = calculateVisibleTickCount();
        double lowestTickValue = calculateLowestVisibleTickValue();

        if (count <= ValueAxis.MAXIMUM_TICK_COUNT) {
            for (int i = 0; i < count; i++) {
                double currentTickValue = lowestTickValue + (i * size);
                double xx = translateValueToJava2D(currentTickValue, dataArea);
                String tickLabel;
                NumberFormat formatter = getNumberFormatOverride();
                if (formatter != null) {
                    tickLabel = formatter.format(currentTickValue);
                }
                else {
                    tickLabel = valueToString(currentTickValue);
                }
                Rectangle2D tickLabelBounds = tickLabelFont.getStringBounds(tickLabel, frc);
                LineMetrics metrics = tickLabelFont.getLineMetrics(tickLabel, frc);
                float x = 0.0f;
                float y = 0.0f;
                Insets tickLabelInsets = getTickLabelInsets();
                if (isVerticalTickLabels()) {
                    x = (float) (xx + tickLabelBounds.getHeight() / 2);
                    if (location == TOP) {
                        y = (float) (dataArea.getMinY() - tickLabelInsets.bottom
                                                        - tickLabelBounds.getWidth());
                    }
                    else {
                        y = (float) (dataArea.getMaxY() + tickLabelInsets.top
                                                        + tickLabelBounds.getWidth());
                    }
                }
                else {
                    x = (float) (xx - tickLabelBounds.getWidth() / 2);
                    if (location == TOP) {
                        y = (float) (dataArea.getMinY() - tickLabelInsets.bottom
                                                        - metrics.getLeading()
                                                        - metrics.getDescent());
                    }
                    else {
                        y = (float) (dataArea.getMaxY() + tickLabelInsets.top
                                                        + tickLabelBounds.getHeight());
                    }
                }
                Tick tick = new Tick(new Double(currentTickValue), tickLabel, x, y);
                getTicks().add(tick);
            }
        }

    }

    /**
     * Converts a value to a string, using the list of symbolic values.
     *
     * @param value  value to convert.
     *
     * @return the symbolic value.
     */
    public String valueToString(double value) {

        String strToReturn;
        try {
            strToReturn = (String) this.symbolicValue.get((int) value);
        }
        catch (IndexOutOfBoundsException  ex) {
            strToReturn =  new String("");
        }
        return strToReturn;
    }

}
