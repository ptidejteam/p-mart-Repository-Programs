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
 * -----------------
 * SymbolicAxis.java
 * -----------------
 * (C) Copyright 2002, 2003, by Object Refinery Limited and Contributors.
 *
 * Original Author:  Anthony Boulestreau;
 * Contributor(s):   David Gilbert (for Object Refinery Limited);
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
 * 14-May-2003 : Renamed HorizontalSymbolicAxis --> SymbolicAxis and merged in
 *               VerticalSymbolicAxis (DG);
 * 12-Aug-2003 : Fixed bug where refreshTicks(...) method has different signature to super 
 *               class (DG);
 * 29-Oct-2003 : Added workaround for font alignment in PDF output (DG);
 * 02-Nov-2003 : Added code to avoid overlapping labels (MR);
 * 07-Nov-2003 : Modified to use new tick classes (DG);
 *
 */

package org.jfree.chart.axis;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Paint;
import java.awt.geom.Rectangle2D;
import java.io.Serializable;
import java.text.NumberFormat;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.jfree.chart.event.AxisChangeEvent;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.plot.ValueAxisPlot;
import org.jfree.data.Range;
import org.jfree.ui.RectangleEdge;
import org.jfree.ui.TextAnchor;

/**
 * A standard linear value axis, for SYMBOLIC values.
 *
 * @author Anthony Boulestreau
 */
public class SymbolicAxis extends NumberAxis implements Serializable {

    /** The default symbolic grid line paint. */
    public static final Paint DEFAULT_SYMBOLIC_GRID_LINE_PAINT = new Color(232, 234, 232);

    /** The list of symbolic value to display instead of the numeric values. */
    private List symbolicValue;

    /** List of the symbolic grid lines shapes. */
    private List symbolicGridLineList = null;

    /** Color of the dark part of the symbolic grid line. **/
    private transient Paint symbolicGridPaint;

    /** Flag that indicates whether or not symbolic grid lines are visible. */
    private boolean symbolicGridLinesVisible;

    /**
     * Constructs a symbolic axis, using default attribute values where necessary.
     *
     * @param label  the axis label (null permitted).
     * @param sv  the list of symbolic values to display instead of the numeric value.
     */
    public SymbolicAxis(String label, String[] sv) {

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

//    /**
//     * Redefinition of setAnchoredRange for the symbolicvalues.
//     *
//     * @param range  the new range.
//     */
//    public void setAnchoredRange(double range) {
//
//        if (zoomIsAccepted) {
//            //compute the corresponding integer corresponding to the anchor
//            //position
//            double anchor = Math.rint(getAnchorValue());
//            double min = Math.rint(anchor - range / 2) - 0.5;
//            double max = Math.rint(anchor + range / 2) + 0.5;
//            if (min < -0.5) {
//                min = -0.5;
//            }
//            if (max > symbolicValue.size() - 0.5) {
//                max = symbolicValue.size() - 0.5;
//            }
//            setRange(min, max);
//        }
//
//    }

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
     * @param cursor  the cursor location.
     * @param plotArea  the area within which the plot and axes should be drawn.
     * @param dataArea  the area within which the data should be drawn (a subset of the plotArea).
     * @param edge  the axis location.
     * 
     * @return The new cursor location.
     */
    public AxisState draw(Graphics2D g2, double cursor,
                         Rectangle2D plotArea, Rectangle2D dataArea, RectangleEdge edge) {

        AxisState info = new AxisState(cursor);
        if (isVisible()) {
            info = super.draw(g2, cursor, plotArea, dataArea, edge);
        }
        if (symbolicGridLinesVisible) {
            drawSymbolicGridLines(g2, plotArea, dataArea, edge, info.getTicks());
        }
        return info;

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
     * @param edge  the axis location.
     * @param ticks  the ticks.
     */
    public void drawSymbolicGridLines(Graphics2D g2,
                                      Rectangle2D plotArea, 
                                      Rectangle2D dataArea,
                                      RectangleEdge edge, 
                                      List ticks) {

        if (RectangleEdge.isTopOrBottom(edge)) {
            drawSymbolicGridLinesHorizontal(g2, plotArea, dataArea, true, ticks);
        }
        else if (RectangleEdge.isLeftOrRight(edge)) {
            drawSymbolicGridLinesVertical(g2, plotArea, dataArea, true, ticks);
        }
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
     * @param ticks  the ticks.
     */
    public void drawSymbolicGridLinesHorizontal(Graphics2D g2,
                                                Rectangle2D plotArea, 
                                                Rectangle2D dataArea,
                                                boolean firstGridLineIsDark, 
                                                List ticks) {

        this.symbolicGridLineList = new Vector(ticks.size());
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

        Iterator iterator = ticks.iterator();
        ValueTick tick;
        Rectangle2D symbolicGridLine;
        while (iterator.hasNext()) {
            tick = (ValueTick) iterator.next();
            xx1 = translateValueToJava2D(tick.getValue() - 0.5d, dataArea,
                                         RectangleEdge.BOTTOM);
            xx2 = translateValueToJava2D(tick.getValue() + 0.5d, dataArea,
                                         RectangleEdge.BOTTOM);
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

        if (plot instanceof ValueAxisPlot) {

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

            setRange(new Range(lower, upper), false, false);

        }

    }

    /**
     * Calculates the positions of the tick labels for the axis, storing the results in the
     * tick label list (ready for drawing).
     *
     * @param g2  the graphics device.
     * @param cursor  the cursor.
     * @param plotArea  the area in which the plot (inlcuding axes) should be drawn.
     * @param dataArea  the area in which the data should be drawn.
     * @param edge  the location of the axis.
     * 
     * @return A list of ticks.
     */
    public List refreshTicks(Graphics2D g2, double cursor,
                             Rectangle2D plotArea, Rectangle2D dataArea,
                             RectangleEdge edge) {

        List ticks = null;
        if (RectangleEdge.isTopOrBottom(edge)) {
            ticks = refreshTicksHorizontal(g2, cursor, plotArea, dataArea, edge);
        }
        else if (RectangleEdge.isLeftOrRight(edge)) {
            ticks = refreshTicksVertical(g2, cursor, plotArea, dataArea, edge);
        }
        return ticks;

    }


    /**
     * Calculates the positions of the tick labels for the axis, storing the results in the
     * tick label list (ready for drawing).
     *
     * @param g2  the graphics device.
     * @param cursor  the cursor position for drawing the axis.
     * @param plotArea  the area in which the plot (inlcuding axes) should be drawn.
     * @param dataArea  the area in which the data should be drawn.
     * @param edge  the location of the axis.
     * 
     * @return The ticks.
     */
    public List refreshTicksHorizontal(Graphics2D g2, double cursor,
                                       Rectangle2D plotArea, Rectangle2D dataArea,
                                       RectangleEdge edge) {

        List ticks = new java.util.ArrayList();

        Font tickLabelFont = getTickLabelFont();
        g2.setFont(tickLabelFont);

        double size = getTickUnit().getSize();
        int count = calculateVisibleTickCount();
        double lowestTickValue = calculateLowestVisibleTickValue();

                double previousDrawnTickLabelPos = 0.0;         
                double previousDrawnTickLabelLength = 0.0;              

        if (count <= ValueAxis.MAXIMUM_TICK_COUNT) {
            for (int i = 0; i < count; i++) {
                double currentTickValue = lowestTickValue + (i * size);
                double xx = translateValueToJava2D(currentTickValue, dataArea, edge);
                String tickLabel;
                NumberFormat formatter = getNumberFormatOverride();
                if (formatter != null) {
                    tickLabel = formatter.format(currentTickValue);
                }
                else {
                    tickLabel = valueToString(currentTickValue);
                }
                
                // avoid to draw overlapping tick labels
                Rectangle2D bounds = tickLabelFont.getStringBounds(tickLabel,
                g2.getFontRenderContext());
                double tickLabelLength = isVerticalTickLabels() 
                                         ? bounds.getHeight() : bounds.getWidth();
                boolean tickLabelsOverlapping = false;
                if (i > 0) {
                    double avgTickLabelLength = (previousDrawnTickLabelLength + tickLabelLength) / 2.0;
                    if (Math.abs(xx - previousDrawnTickLabelPos) < avgTickLabelLength) {
                        tickLabelsOverlapping = true;
                    }
                }
                if (tickLabelsOverlapping) {
                    tickLabel = ""; // don't draw this tick label
                }
                else {
                    // remember these values for next comparison
                    previousDrawnTickLabelPos = xx;
                    previousDrawnTickLabelLength = tickLabelLength;         
                } 
                
                float x = (float) xx;
                float y = 0.0f;
                TextAnchor anchor = null;
                TextAnchor rotationAnchor = null;
                double angle = 0.0;
                Insets tickLabelInsets = getTickLabelInsets();
                if (isVerticalTickLabels()) {
                    anchor = TextAnchor.CENTER_RIGHT;
                    rotationAnchor = TextAnchor.CENTER_RIGHT;
                    if (edge == RectangleEdge.TOP) {
                        y = (float) (cursor - tickLabelInsets.right);
                        angle = Math.PI / 2.0;
                    }
                    else {
                        y = (float) (cursor + tickLabelInsets.right);
                        angle = -Math.PI / 2.0;
                    }
                }
                else {
                    if (edge == RectangleEdge.TOP) {
                        y = (float) (cursor - tickLabelInsets.bottom);
                        anchor = TextAnchor.BOTTOM_CENTER;
                        rotationAnchor = TextAnchor.BOTTOM_CENTER;
                    }
                    else {
                        y = (float) (cursor + tickLabelInsets.top);
                        anchor = TextAnchor.TOP_CENTER;
                        rotationAnchor = TextAnchor.TOP_CENTER;
                    }
                }
                Tick tick = new NumberTick(new Double(currentTickValue), tickLabel, x, y, 
                                           anchor, rotationAnchor, angle);
                ticks.add(tick);
            }
        }
        return ticks;

    }

    /**
     * Calculates the positions of the tick labels for the axis, storing the results in the
     * tick label list (ready for drawing).
     *
     * @param g2  the graphics device.
     * @param cursor  the cursor position for drawing the axis.
     * @param plotArea  the area in which the plot and the axes should be drawn.
     * @param dataArea  the area in which the plot should be drawn.
     * @param edge  the location of the axis.
     * 
     * @return The ticks.
     */
    public List refreshTicksVertical(Graphics2D g2, double cursor,
                                     Rectangle2D plotArea, Rectangle2D dataArea,
                                     RectangleEdge edge) {

        List ticks = new java.util.ArrayList();

        Font tickLabelFont = getTickLabelFont();
        g2.setFont(tickLabelFont);

        double size = getTickUnit().getSize();
        int count = calculateVisibleTickCount();
        double lowestTickValue = calculateLowestVisibleTickValue();

        double previousDrawnTickLabelPos = 0.0;         
        double previousDrawnTickLabelLength = 0.0;              

        if (count <= ValueAxis.MAXIMUM_TICK_COUNT) {
            for (int i = 0; i < count; i++) {
                double currentTickValue = lowestTickValue + (i * size);
                double yy = translateValueToJava2D(currentTickValue, dataArea, edge);
                String tickLabel;
                NumberFormat formatter = getNumberFormatOverride();
                if (formatter != null) {
                    tickLabel = formatter.format(currentTickValue);
                }
                else {
                    tickLabel = valueToString(currentTickValue);
                }

                // avoid to draw overlapping tick labels
                Rectangle2D bounds = tickLabelFont.getStringBounds(tickLabel,
                                                                   g2.getFontRenderContext());
                double tickLabelLength = isVerticalTickLabels() 
                                         ? bounds.getWidth() : bounds.getHeight();
                boolean tickLabelsOverlapping = false;
                if (i > 0) {
                    double avgTickLabelLength = 
                        (previousDrawnTickLabelLength + tickLabelLength) / 2.0;
                    if (Math.abs(yy - previousDrawnTickLabelPos) < avgTickLabelLength) {
                        tickLabelsOverlapping = true;    
                    }
                    if (tickLabelsOverlapping) {
                        tickLabel = ""; // don't draw this tick label
                    }
                    else {
                        // remember these values for next comparison
                        previousDrawnTickLabelPos = yy;
                        previousDrawnTickLabelLength = tickLabelLength;         
                    } 
                }
                float x = 0.0f;
                float y = (float) yy;
                TextAnchor anchor = null;
                TextAnchor rotationAnchor = null;
                double angle = 0.0;
                if (isVerticalTickLabels()) {
                    anchor = TextAnchor.BOTTOM_CENTER;
                    rotationAnchor = TextAnchor.BOTTOM_CENTER;
                    if (edge == RectangleEdge.LEFT) {
                        x = (float) (cursor - getTickLabelInsets().bottom);
                        angle = -Math.PI / 2.0;
                    }
                    else {
                        x = (float) (cursor + getTickLabelInsets().bottom);
                        angle = Math.PI / 2.0;
                    }                    
                }
                else {
                    if (edge == RectangleEdge.LEFT) {
                        x = (float) (cursor - getTickLabelInsets().right);
                        anchor = TextAnchor.CENTER_RIGHT;
                        rotationAnchor = TextAnchor.CENTER_RIGHT;
                    }
                    else {
                        x = (float) (cursor + getTickLabelInsets().left);
                        anchor = TextAnchor.CENTER_LEFT;
                        rotationAnchor = TextAnchor.CENTER_LEFT;
                    }
                }
                Tick tick = new NumberTick(new Double(currentTickValue), tickLabel, x, y, 
                                           anchor, rotationAnchor, angle);
                ticks.add(tick);
            }
        }
        return ticks;
        
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
     * @param drawArea  the area within which the chart should be drawn.
     * @param plotArea  the area within which the plot should be drawn (a
     *                  subset of the drawArea).
     * @param firstGridLineIsDark   True: the first symbolic grid line take the
     *      color of <CODE>symbolicGridPaint<CODE>.
     *      False: the first symbolic grid line is white.
     * @param ticks  a list of ticks.
     */
    public void drawSymbolicGridLinesVertical(Graphics2D g2, Rectangle2D drawArea,
                                              Rectangle2D plotArea, boolean firstGridLineIsDark,
                                              List ticks) {

        this.symbolicGridLineList = new Vector(ticks.size());
        boolean currentGridLineIsDark = firstGridLineIsDark;
        double xx = plotArea.getX();
        double yy1, yy2;

        //gets the outline stroke width of the plot
        double outlineStrokeWidth;
        if (getPlot().getOutlineStroke() != null) {
            outlineStrokeWidth = ((BasicStroke) getPlot().getOutlineStroke()).getLineWidth();
        }
        else {
            outlineStrokeWidth = 1d;
        }

        Iterator iterator = ticks.iterator();
        ValueTick tick;
        Rectangle2D symbolicGridLine;
        while (iterator.hasNext()) {
            tick = (ValueTick) iterator.next();
            yy1 = translateValueToJava2D(tick.getValue() + 0.5d, plotArea,
                                         RectangleEdge.LEFT);
            yy2 = translateValueToJava2D(tick.getValue() - 0.5d, plotArea,
                                         RectangleEdge.LEFT);
            if (currentGridLineIsDark) {
                g2.setPaint(Color.white);
                g2.setXORMode((Color) getSymbolicGridPaint());
            }
            else {
                g2.setPaint(Color.white);
                g2.setXORMode(Color.white);
            }
            symbolicGridLine = new Rectangle2D.Double(xx + outlineStrokeWidth,
                yy1, plotArea.getMaxX() - xx - outlineStrokeWidth, yy2 - yy1);
            g2.fill(symbolicGridLine);
            symbolicGridLineList.add(symbolicGridLine);
            currentGridLineIsDark = !currentGridLineIsDark;
        }
        g2.setPaintMode();
    }

}
