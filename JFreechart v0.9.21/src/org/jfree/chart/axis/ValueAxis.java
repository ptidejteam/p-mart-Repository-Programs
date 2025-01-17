/* ===========================================================
 * JFreeChart : a free chart library for the Java(tm) platform
 * ===========================================================
 *
 * (C) Copyright 2000-2004, by Object Refinery Limited and Contributors.
 *
 * Project Info:  http://www.jfree.org/jfreechart/index.html
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
 * [Java is a trademark or registered trademark of Sun Microsystems, Inc.
 * in the United States and other countries.]
 *
 * --------------
 * ValueAxis.java
 * --------------
 * (C) Copyright 2000-2004, by Object Refinery Limited and Contributors.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited);
 * Contributor(s):   Jonathan Nash;
 *                   Nicolas Brodu (for Astrium and EADS Corporate Research Center);
 *
 * $Id: ValueAxis.java,v 1.1 2007/10/10 19:50:17 vauchers Exp $
 *
 * Changes (from 18-Sep-2001)
 * --------------------------
 * 18-Sep-2001 : Added standard header and fixed DOS encoding problem (DG);
 * 23-Nov-2001 : Overhauled standard tick unit code (DG);
 * 04-Dec-2001 : Changed constructors to protected, and tidied up default values (DG);
 * 12-Dec-2001 : Fixed vertical gridlines bug (DG);
 * 16-Jan-2002 : Added an optional crosshair, based on the implementation by Jonathan Nash (DG);
 * 23-Jan-2002 : Moved the minimum and maximum values to here from NumberAxis, and changed the type
 *               from Number to double (DG);
 * 25-Feb-2002 : Added default value for autoRange. Changed autoAdjustRange from public to
 *               protected. Updated import statements (DG);
 * 23-Apr-2002 : Added setRange(...) method (DG);
 * 29-Apr-2002 : Added range adjustment methods (DG);
 * 13-Jun-2002 : Modified setCrosshairValue(...) to notify listeners only when the crosshairs are
 *               visible, to avoid unnecessary repaints, as suggested by Kees Kuip (DG);
 * 25-Jul-2002 : Moved lower and upper margin attributes from the NumberAxis class (DG);
 * 05-Sep-2002 : Updated constructor for changes in Axis class (DG);
 * 01-Oct-2002 : Fixed errors reported by Checkstyle (DG);
 * 04-Oct-2002 : Moved standardTickUnits from NumberAxis --> ValueAxis (DG);
 * 08-Nov-2002 : Moved to new package com.jrefinery.chart.axis (DG);
 * 19-Nov-2002 : Removed grid settings (now controlled by the plot) (DG);
 * 27-Nov-2002 : Moved the 'inverted' attributed from NumberAxis to ValueAxis (DG);
 * 03-Jan-2003 : Small fix to ensure auto-range minimum is observed immediately (DG);
 * 14-Jan-2003 : Changed autoRangeMinimumSize from Number --> double (DG);
 * 20-Jan-2003 : Replaced monolithic constructor (DG);
 * 26-Mar-2003 : Implemented Serializable (DG);
 * 09-May-2003 : Added AxisLocation parameter to translation methods (DG);
 * 13-Aug-2003 : Implemented Cloneable (DG);
 * 01-Sep-2003 : Fixed bug 793167 (setMaximumAxisValue exception) (DG);
 * 02-Sep-2003 : Fixed bug 795366 (zooming on inverted axes) (DG);
 * 08-Sep-2003 : Completed Serialization support (NB);
 * 08-Sep-2003 : Renamed get/setMinimumValue --> get/setLowerBound,
 *               and get/setMaximumValue --> get/setUpperBound (DG);
 * 27-Oct-2003 : Changed DEFAULT_AUTO_RANGE_MINIMUM_SIZE value - see bug ID 829606 (DG);
 * 07-Nov-2003 : Changes to tick mechanism (DG);
 * 06-Jan-2004 : Moved axis line attributes to Axis class (DG);
 * 21-Jan-2004 : Removed redundant axisLineVisible attribute.  Renamed translateJava2DToValue --> 
 *               java2DToValue, and translateValueToJava2D --> valueToJava2D (DG); 
 * 23-Jan-2004 : Fixed setAxisLinePaint() and setAxisLineStroke() which had no 
 *               effect (andreas.gawecki@coremedia.com);
 * 07-Apr-2004 : Changed text bounds calculation (DG);
 * 26-Apr-2004 : Added getter/setter methods for arrow shapes (DG);
 * 18-May-2004 : Added methods to set axis range *including* current margins (DG);
 * 02-Jun-2004 : Fixed bug in setRangeWithMargins() method (DG);
 *
 */

package org.jfree.chart.axis;

import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Polygon;
import java.awt.Shape;
import java.awt.font.LineMetrics;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Iterator;
import java.util.List;

import org.jfree.chart.event.AxisChangeEvent;
import org.jfree.chart.plot.Plot;
import org.jfree.data.Range;
import org.jfree.io.SerialUtilities;
import org.jfree.text.TextUtilities;
import org.jfree.ui.RectangleEdge;
import org.jfree.ui.RefineryUtilities;
import org.jfree.util.ObjectUtils;
import org.jfree.util.PublicCloneable;

/**
 * The base class for axes that display value data, where values are measured using the
 * <code>double</code> primitive.  The two key subclasses are {@link DateAxis} and 
 * {@link NumberAxis}.
 */
public abstract class ValueAxis extends Axis implements Cloneable, PublicCloneable, Serializable {

    /** The default axis range. */
    public static final Range DEFAULT_RANGE = new Range(0.0, 1.0);

    /** The default auto-range value. */
    public static final boolean DEFAULT_AUTO_RANGE = true;

    /** The default inverted flag setting. */
    public static final boolean DEFAULT_INVERTED = false;

    /** The default minimum auto range. */
    public static final double DEFAULT_AUTO_RANGE_MINIMUM_SIZE = 0.00000001;

    /** The default value for the lower margin (0.05 = 5%). */
    public static final double DEFAULT_LOWER_MARGIN = 0.05;

    /** The default value for the upper margin (0.05 = 5%). */
    public static final double DEFAULT_UPPER_MARGIN = 0.05;

    /** The default lower bound for the axis. */
    public static final double DEFAULT_LOWER_BOUND = 0.0;

    /** The default upper bound for the axis. */
    public static final double DEFAULT_UPPER_BOUND = 1.0;

    /** The default auto-tick-unit-selection value. */
    public static final boolean DEFAULT_AUTO_TICK_UNIT_SELECTION = true;

    /** The maximum tick count. */
    public static final int MAXIMUM_TICK_COUNT = 500;
    
    /** A flag that controls whether an arrow is drawn at the positive end of the axis line. */
    private boolean positiveArrowVisible;
    
    /** A flag that controls whether an arrow is drawn at the negative end of the axis line. */
    private boolean negativeArrowVisible;
    
    /** The shape used for an up arrow. */
    private transient Shape upArrow;
    
    /** The shape used for a down arrow. */
    private transient Shape downArrow;
    
    /** The shape used for a left arrow. */
    private transient Shape leftArrow;
    
    /** The shape used for a right arrow. */
    private transient Shape rightArrow;
    
    /** A flag that affects the orientation of the values on the axis. */
    private boolean inverted;

    /** The axis range. */
    private Range range;

    /** Flag that indicates whether the axis automatically scales to fit the chart data. */
    private boolean autoRange;

    /** The minimum size for the 'auto' axis range (excluding margins). */
    private double autoRangeMinimumSize;

    /**
     * The upper margin percentage.  This indicates the amount by which the maximum axis value
     * exceeds the maximum data value (as a percentage of the range on the axis) when the axis
     * range is determined automatically.
     */
    private double upperMargin;

    /**
     * The lower margin.  This is a percentage that indicates the amount by
     * which the minimum axis value is "less than" the minimum data value when
     * the axis range is determined automatically.
     */
    private double lowerMargin;

    /**
     * If this value is positive, the amount is subtracted from the maximum
     * data value to determine the lower axis range.  This can be used to
     * provide a fixed "window" on dynamic data.
     */
    private double fixedAutoRange;

    /** Flag that indicates whether or not the tick unit is selected automatically. */
    private boolean autoTickUnitSelection;

    /** The standard tick units for the axis. */
    private TickUnitSource standardTickUnits;

    /** An index into an array of standard tick values. */
    private int autoTickIndex;
    
    /** A flag indicating whether or not tick labels are rotated to vertical. */
    private boolean verticalTickLabels;

    /**
     * Constructs a value axis.
     *
     * @param label  the axis label.
     * @param standardTickUnits  the source for standard tick units (<code>null</code> permitted).
     */
    protected ValueAxis(String label, TickUnitSource standardTickUnits) {

        super(label);

        this.positiveArrowVisible = false;
        this.negativeArrowVisible = false;

        this.range = DEFAULT_RANGE;
        this.autoRange = DEFAULT_AUTO_RANGE;

        this.inverted = DEFAULT_INVERTED;
        this.autoRangeMinimumSize = DEFAULT_AUTO_RANGE_MINIMUM_SIZE;

        this.lowerMargin = DEFAULT_LOWER_MARGIN;
        this.upperMargin = DEFAULT_UPPER_MARGIN;

        this.fixedAutoRange = 0.0;

        this.autoTickUnitSelection = DEFAULT_AUTO_TICK_UNIT_SELECTION;
        this.standardTickUnits = standardTickUnits;
        
        Polygon p1 = new Polygon();
        p1.addPoint(0, 0);
        p1.addPoint(-2, 2);
        p1.addPoint(2, 2);
        
        this.upArrow = p1;

        Polygon p2 = new Polygon();
        p2.addPoint(0, 0);
        p2.addPoint(-2, -2);
        p2.addPoint(2, -2);

        this.downArrow = p2;

        Polygon p3 = new Polygon();
        p3.addPoint(0, 0);
        p3.addPoint(-2, -2);
        p3.addPoint(-2, 2);
        
        this.rightArrow = p3;

        Polygon p4 = new Polygon();
        p4.addPoint(0, 0);
        p4.addPoint(2, -2);
        p4.addPoint(2, 2);

        this.leftArrow = p4;
        
        this.verticalTickLabels = false;
        
    }

    /**
     * Returns true if the tick labels should be rotated (to vertical), and false otherwise.
     *
     * @return <code>true</code> or <code>false</code>.
     */
    public boolean isVerticalTickLabels() {
        return this.verticalTickLabels;
    }

    /**
     * Sets the flag that controls whether the tick labels are displayed vertically (that is,
     * rotated 90 degrees from horizontal).
     * <P>
     * If the flag is changed, an {@link AxisChangeEvent} is sent to all registered listeners.
     *
     * @param flag  the flag.
     */
    public void setVerticalTickLabels(boolean flag) {
        if (this.verticalTickLabels != flag) {
            this.verticalTickLabels = flag;
            notifyListeners(new AxisChangeEvent(this));
        }
    }

    /**
     * Returns a flag that controls whether or not the axis line has an arrow drawn that 
     * points in the positive direction for the axis.
     * 
     * @return A boolean.
     */
    public boolean isPositiveArrowVisible() {
        return this.positiveArrowVisible;
    }
    
    /**
     * Sets a flag that controls whether or not the axis lines has an arrow drawn that
     * points in the positive direction for the axis, and sends an {@link AxisChangeEvent}
     * to all registered listeners.
     * 
     * @param visible  the flag.
     */
    public void setPositiveArrowVisible(boolean visible) {
        this.positiveArrowVisible = visible;
        notifyListeners(new AxisChangeEvent(this));
    }
    
    /**
     * Returns a flag that controls whether or not the axis line has an arrow drawn that 
     * points in the negative direction for the axis.
     * 
     * @return A boolean.
     */
    public boolean isNegativeArrowVisible() {
        return this.negativeArrowVisible;
    }
    
    /**
     * Sets a flag that controls whether or not the axis lines has an arrow drawn that
     * points in the negative direction for the axis, and sends an {@link AxisChangeEvent}
     * to all registered listeners.
     * 
     * @param visible  the flag.
     */
    public void setNegativeArrowVisible(boolean visible) {
        this.negativeArrowVisible = visible;
        notifyListeners(new AxisChangeEvent(this));
    }
    
    /**
     * Returns a shape that can be displayed as an arrow pointing upwards at the end of 
     * an axis line.
     * 
     * @return A shape (never <code>null</code>).
     */
    public Shape getUpArrow() {
        return this.upArrow;   
    }
    
    /**
     * Sets the shape that can be displayed as an arrow pointing upwards at the end of an
     * axis line and sends an {@link AxisChangeEvent} to all registered listeners.
     * 
     * @param arrow  the arrow shape (<code>null</code> not permitted).
     */
    public void setUpArrow(Shape arrow) {
        if (arrow == null) {
            throw new IllegalArgumentException("Null 'arrow' argument.");   
        }
        this.upArrow = arrow;
        notifyListeners(new AxisChangeEvent(this));
    }
    
    /**
     * Returns a shape that can be displayed as an arrow pointing downwards at the end of 
     * an axis line.
     * 
     * @return A shape (never <code>null</code>).
     */
    public Shape getDownArrow() {
        return this.downArrow;   
    }
    
    /**
     * Sets the shape that can be displayed as an arrow pointing downwards at the end of an
     * axis line and sends an {@link AxisChangeEvent} to all registered listeners.
     * 
     * @param arrow  the arrow shape (<code>null</code> not permitted).
     */
    public void setDownArrow(Shape arrow) {
        if (arrow == null) {
            throw new IllegalArgumentException("Null 'arrow' argument.");   
        }
        this.downArrow = arrow;
        notifyListeners(new AxisChangeEvent(this));
    }
    
    /**
     * Returns a shape that can be displayed as an arrow pointing left at the end of 
     * an axis line.
     * 
     * @return A shape (never <code>null</code>).
     */
    public Shape getLeftArrow() {
        return this.leftArrow;   
    }
    
    /**
     * Sets the shape that can be displayed as an arrow pointing left at the end of an
     * axis line and sends an {@link AxisChangeEvent} to all registered listeners.
     * 
     * @param arrow  the arrow shape (<code>null</code> not permitted).
     */
    public void setLeftArrow(Shape arrow) {
        if (arrow == null) {
            throw new IllegalArgumentException("Null 'arrow' argument.");   
        }
        this.leftArrow = arrow;
        notifyListeners(new AxisChangeEvent(this));
    }
    
    /**
     * Returns a shape that can be displayed as an arrow pointing right at the end of 
     * an axis line.
     * 
     * @return A shape (never <code>null</code>).
     */
    public Shape getRightArrow() {
        return this.rightArrow;   
    }
    
    /**
     * Sets the shape that can be displayed as an arrow pointing rightwards at the end of an
     * axis line and sends an {@link AxisChangeEvent} to all registered listeners.
     * 
     * @param arrow  the arrow shape (<code>null</code> not permitted).
     */
    public void setRightArrow(Shape arrow) {
        if (arrow == null) {
            throw new IllegalArgumentException("Null 'arrow' argument.");   
        }
        this.rightArrow = arrow;
        notifyListeners(new AxisChangeEvent(this));
    }
    
    /**
     * Draws an axis line at the current cursor position and edge.
     * 
     * @param g2  the graphics device.
     * @param cursor  the cursor position.
     * @param dataArea  the data area.
     * @param edge  the edge.
     */
    protected void drawAxisLine(Graphics2D g2, double cursor,
                                Rectangle2D dataArea, RectangleEdge edge) {
        Line2D axisLine = null;
        if (edge == RectangleEdge.TOP) {
            axisLine = new Line2D.Double(dataArea.getX(), cursor, dataArea.getMaxX(), cursor);  
        }
        else if (edge == RectangleEdge.BOTTOM) {
            axisLine = new Line2D.Double(dataArea.getX(), cursor, dataArea.getMaxX(), cursor);  
        }
        else if (edge == RectangleEdge.LEFT) {
            axisLine = new Line2D.Double(cursor, dataArea.getY(), cursor, dataArea.getMaxY());  
        }
        else if (edge == RectangleEdge.RIGHT) {
            axisLine = new Line2D.Double(cursor, dataArea.getY(), cursor, dataArea.getMaxY());  
        }
        g2.setPaint(getAxisLinePaint());
        g2.setStroke(getAxisLineStroke());
        g2.draw(axisLine);
        
        boolean drawUpOrRight = false;  
        boolean drawDownOrLeft = false;
        if (this.positiveArrowVisible) {
            if (this.inverted) {
                drawDownOrLeft = true;   
            }
            else {
                drawUpOrRight = true;   
            }
        }
        if (this.negativeArrowVisible) {
            if (this.inverted) {
                drawUpOrRight = true;   
            }
            else {
                drawDownOrLeft = true;   
            }
        }
        if (drawUpOrRight) {
            double x = 0.0;
            double y = 0.0;
            Shape arrow = null;
            if (edge == RectangleEdge.TOP || edge == RectangleEdge.BOTTOM) {
                x = dataArea.getMaxX();
                y = cursor;
                arrow = this.rightArrow; 
            }
            else if (edge == RectangleEdge.LEFT || edge == RectangleEdge.RIGHT) {
                x = cursor;
                y = dataArea.getMinY();
                arrow = this.upArrow; 
            }

            // draw the arrow...
            AffineTransform transformer = new AffineTransform();
            transformer.setToTranslation(x, y);
            Shape shape = transformer.createTransformedShape(arrow);
            g2.fill(shape);
            g2.draw(shape);
        }
        
        if (drawDownOrLeft) {
            double x = 0.0;
            double y = 0.0;
            Shape arrow = null;
            if (edge == RectangleEdge.TOP || edge == RectangleEdge.BOTTOM) {
                x = dataArea.getMinX();
                y = cursor;
                arrow = this.leftArrow; 
            }
            else if (edge == RectangleEdge.LEFT || edge == RectangleEdge.RIGHT) {
                x = cursor;
                y = dataArea.getMaxY();
                arrow = this.downArrow; 
            }

            // draw the arrow...
            AffineTransform transformer = new AffineTransform();
            transformer.setToTranslation(x, y);
            Shape shape = transformer.createTransformedShape(arrow);
            g2.fill(shape);
            g2.draw(shape);
        }
        
    }
    
    /**
     * Calculates the anchor point for a tick label.
     * 
     * @param tick  the tick.
     * @param cursor  the cursor.
     * @param dataArea  the data area.
     * @param edge  the edge on which the axis is drawn.
     * 
     * @return  the x and y coordinates of the anchor point.
     */
    protected float[] calculateAnchorPoint(ValueTick tick, 
                                           double cursor, 
                                           Rectangle2D dataArea, 
                                           RectangleEdge edge) {
    
        float[] result = new float[2];
        if (edge == RectangleEdge.TOP) {
            result[0] = (float) this.valueToJava2D(tick.getValue(), dataArea, edge);
            result[1] = (float) (cursor - getTickLabelInsets().bottom - 2.0);    
        }
        else if (edge == RectangleEdge.BOTTOM) {
            result[0] = (float) this.valueToJava2D(tick.getValue(), dataArea, edge);
            result[1] = (float) (cursor + getTickLabelInsets().top + 2.0);                
        }
        else if (edge == RectangleEdge.LEFT) {
            result[0] = (float) (cursor - getTickLabelInsets().left - 2.0);    
            result[1] = (float) this.valueToJava2D(tick.getValue(), dataArea, edge);
        }
        else if (edge == RectangleEdge.RIGHT) {
            result[0] = (float) (cursor + getTickLabelInsets().right + 2.0);    
            result[1] = (float) this.valueToJava2D(tick.getValue(), dataArea, edge);
        }
        return result;
    }
    
    /**
     * Draws the axis line, tick marks and tick mark labels.
     * 
     * @param g2  the graphics device.
     * @param cursor  the cursor.
     * @param plotArea  the plot area.
     * @param dataArea  the data area.
     * @param edge  the edge that the axis is aligned with.
     * 
     * @return The width or height used to draw the axis.
     */
    protected AxisState drawTickMarksAndLabels(Graphics2D g2, 
                                               double cursor,
                                               Rectangle2D plotArea,
                                               Rectangle2D dataArea, 
                                               RectangleEdge edge) {
                                              
        AxisState state = new AxisState(cursor);

        if (isAxisLineVisible()) {
            drawAxisLine(g2, cursor, dataArea, edge);
        }

        double ol = getTickMarkOutsideLength();
        double il = getTickMarkInsideLength();

        List ticks = refreshTicks(g2, state, plotArea, dataArea, edge);
        state.setTicks(ticks);
        g2.setFont(getTickLabelFont());
        Iterator iterator = ticks.iterator();
        while (iterator.hasNext()) {
            ValueTick tick = (ValueTick) iterator.next();
            if (isTickLabelsVisible()) {
                g2.setPaint(getTickLabelPaint());
                float[] anchorPoint = calculateAnchorPoint(tick, cursor, dataArea, edge);
                RefineryUtilities.drawRotatedString(
                    tick.getText(), g2, 
                    anchorPoint[0], anchorPoint[1],
                    tick.getTextAnchor(), 
                    tick.getRotationAnchor(),
                    tick.getAngle()
                );
            }

            if (isTickMarksVisible()) {
                float xx = (float) translateValueToJava2D(tick.getValue(), dataArea, edge);
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
        }
        
        // need to work out the space used by the tick labels...
        // so we can update the cursor...
        double used = 0.0;
        if (isTickLabelsVisible()) {
            if (edge == RectangleEdge.LEFT) {
                used += findMaximumTickLabelWidth(ticks, g2, plotArea, isVerticalTickLabels());  
                state.cursorLeft(used);      
            }
            else if (edge == RectangleEdge.RIGHT) {
                used = findMaximumTickLabelWidth(ticks, g2, plotArea, isVerticalTickLabels());
                state.cursorRight(used);      
            }
            else if (edge == RectangleEdge.TOP) {
                used = findMaximumTickLabelHeight(ticks, g2, plotArea, isVerticalTickLabels());
                state.cursorUp(used);
            }
            else if (edge == RectangleEdge.BOTTOM) {
                used = findMaximumTickLabelHeight(ticks, g2, plotArea, isVerticalTickLabels());
                state.cursorDown(used);
            }
        }
       
        return state;
    }
    
    /**
     * Returns the space required to draw the axis.
     *
     * @param g2  the graphics device.
     * @param plot  the plot that the axis belongs to.
     * @param plotArea  the area within which the plot should be drawn.
     * @param edge  the axis location.
     * @param space  the space already reserved (for other axes).
     *
     * @return The space required to draw the axis (including pre-reserved space).
     */
    public AxisSpace reserveSpace(Graphics2D g2, Plot plot, Rectangle2D plotArea, 
                                  RectangleEdge edge, AxisSpace space) {

        // create a new space object if one wasn't supplied...
        if (space == null) {
            space = new AxisSpace();
        }
        
        // if the axis is not visible, no additional space is required...
        if (!isVisible()) {
            return space;
        }

        // if the axis has a fixed dimension, return it...
        double dimension = getFixedDimension();
        if (dimension > 0.0) {
            space.ensureAtLeast(dimension, edge);
        }

        // calculate the max size of the tick labels (if visible)...
        double tickLabelHeight = 0.0;
        double tickLabelWidth = 0.0;
        if (isTickLabelsVisible()) {
            g2.setFont(getTickLabelFont());
            List ticks = refreshTicks(g2, new AxisState(), plotArea, plotArea, edge);
            if (RectangleEdge.isTopOrBottom(edge)) {
                tickLabelHeight = findMaximumTickLabelHeight(
                    ticks, g2, plotArea, isVerticalTickLabels()
                );
            }
            else if (RectangleEdge.isLeftOrRight(edge)) {
                tickLabelWidth = findMaximumTickLabelWidth(
                    ticks, g2, plotArea, isVerticalTickLabels()
                );
            }
        }

        // get the axis label size and update the space object...
        Rectangle2D labelEnclosure = getLabelEnclosure(g2, edge);
        double labelHeight = 0.0;
        double labelWidth = 0.0;
        if (RectangleEdge.isTopOrBottom(edge)) {
            labelHeight = labelEnclosure.getHeight();
            space.add(labelHeight + tickLabelHeight, edge);
        }
        else if (RectangleEdge.isLeftOrRight(edge)) {
            labelWidth = labelEnclosure.getWidth();
            space.add(labelWidth + tickLabelWidth, edge);
        }

        return space;

    }

    /**
     * A utility method for determining the height of the tallest tick label.
     *
     * @param ticks  the ticks.
     * @param g2  the graphics device.
     * @param drawArea  the area within which the plot and axes should be drawn.
     * @param vertical  a flag that indicates whether or not the tick labels are 'vertical'.
     *
     * @return the height of the tallest tick label.
     */
    protected double findMaximumTickLabelHeight(List ticks,
                                                Graphics2D g2, 
                                                Rectangle2D drawArea, 
                                                boolean vertical) {
                                                    
        Insets insets = getTickLabelInsets();
        Font font = getTickLabelFont();
        double maxHeight = 0.0;
        if (vertical) {
            FontMetrics fm = g2.getFontMetrics(font);
            Iterator iterator = ticks.iterator();
            while (iterator.hasNext()) {
                Tick tick = (Tick) iterator.next();
                Rectangle2D labelBounds = TextUtilities.getTextBounds(tick.getText(), g2, fm);
                if (labelBounds.getWidth() + insets.top + insets.bottom > maxHeight) {
                    maxHeight = labelBounds.getWidth() + insets.top + insets.bottom;
                }
            }
        }
        else {
            LineMetrics metrics = font.getLineMetrics("ABCxyz", g2.getFontRenderContext());
            maxHeight = metrics.getHeight() + insets.top + insets.bottom;
        }
        return maxHeight;
        
    }

    /**
     * A utility method for determining the width of the widest tick label.
     *
     * @param ticks  the ticks.
     * @param g2  the graphics device.
     * @param drawArea  the area within which the plot and axes should be drawn.
     * @param vertical  a flag that indicates whether or not the tick labels are 'vertical'.
     *
     * @return the width of the tallest tick label.
     */
    protected double findMaximumTickLabelWidth(List ticks, 
                                               Graphics2D g2, 
                                               Rectangle2D drawArea, 
                                               boolean vertical) {
                                                   
        Insets insets = getTickLabelInsets();
        Font font = getTickLabelFont();
        double maxWidth = 0.0;
        if (!vertical) {
            FontMetrics fm = g2.getFontMetrics(font);
            Iterator iterator = ticks.iterator();
            while (iterator.hasNext()) {
                Tick tick = (Tick) iterator.next();
                Rectangle2D labelBounds = TextUtilities.getTextBounds(tick.getText(), g2, fm);
                if (labelBounds.getWidth() + insets.left + insets.right > maxWidth) {
                    maxWidth = labelBounds.getWidth() + insets.left + insets.right;
                }
            }
        }
        else {
            LineMetrics metrics = font.getLineMetrics("ABCxyz", g2.getFontRenderContext());
            maxWidth = metrics.getHeight() + insets.top + insets.bottom;
        }
        return maxWidth;
        
    }

    /**
     * Returns a flag that controls the direction of values on the axis.
     * <P>
     * For a regular axis, values increase from left to right (for a horizontal
     * axis) and bottom to top (for a vertical axis).  When the axis is
     * 'inverted', the values increase in the opposite direction.
     *
     * @return the flag.
     */
    public boolean isInverted() {
        return this.inverted;
    }

    /**
     * Sets a flag that controls the direction of values on the axis, and
     * notifies registered listeners that the axis has changed.
     *
     * @param flag  the flag.
     */
    public void setInverted(boolean flag) {

        if (this.inverted != flag) {
            this.inverted = flag;
            notifyListeners(new AxisChangeEvent(this));
        }

    }

    /**
     * Returns the flag that controls whether or not the axis range is automatically
     * adjusted to fit the data values.
     *
     * @return The flag.
     */
    public boolean isAutoRange() {
        return this.autoRange;
    }

    /**
     * Sets a flag that determines whether or not the axis range is
     * automatically adjusted to fit the data, and notifies registered
     * listeners that the axis has been modified.
     *
     * @param auto  the new value of the flag.
     */
    public void setAutoRange(boolean auto) {
        setAutoRange(auto, true);
    }

    /**
     * Sets the auto range attribute.  If the <code>notify</code> flag is set, an
     * {@link AxisChangeEvent} is sent to registered listeners.
     *
     * @param auto  the flag.
     * @param notify  notify listeners?
     */
    protected void setAutoRange(boolean auto, boolean notify) {
        if (this.autoRange != auto) {
            this.autoRange = auto;
            if (this.autoRange) {
                autoAdjustRange();
            }
            if (notify) {
                notifyListeners(new AxisChangeEvent(this));
            }
        }
    }

    /**
     * Returns the minimum size allowed for the axis range when it is automatically calculated.
     *
     * @return the minimum range.
     */
    public double getAutoRangeMinimumSize() {
        return this.autoRangeMinimumSize;
    }

    /**
     * Sets the auto range minimum size and sends an {@link AxisChangeEvent} to all registered
     * listeners.
     *
     * @param size  the size.
     */
    public void setAutoRangeMinimumSize(double size) {
        setAutoRangeMinimumSize(size, true);
    }

    /**
     * Sets the minimum size allowed for the axis range when it is automatically calculated.
     * <p>
     * If requested, an {@link AxisChangeEvent} is forwarded to all registered listeners.
     *
     * @param size  the new minimum.
     * @param notify  notify listeners?
     */
    public void setAutoRangeMinimumSize(double size, boolean notify) {

        // check argument...
        if (size <= 0.0) {
            throw new IllegalArgumentException(
                "NumberAxis.setAutoRangeMinimumSize(double): must be > 0.0.");
        }

        // make the change...
        if (this.autoRangeMinimumSize != size) {
            this.autoRangeMinimumSize = size;
            if (this.autoRange) {
                autoAdjustRange();
            }
            if (notify) {
                notifyListeners(new AxisChangeEvent(this));
            }
        }

    }

    /**
     * Returns the lower margin for the axis, expressed as a percentage of the axis range.  This
     * controls the space added to the lower end of the axis when the axis range is automatically
     * calculated (it is ignored when the axis range is set explicitly). The default value is 
     * 0.05 (five percent).
     *
     * @return The lower margin.
     */
    public double getLowerMargin() {
        return this.lowerMargin;
    }

    /**
     * Sets the lower margin for the axis (as a percentage of the axis range) and sends an
     * {@link AxisChangeEvent} to all registered listeners.  This margin is added only when the 
     * axis range is auto-calculated - if you set the axis range manually, the margin is ignored.
     *
     * @param margin  the margin percentage (for example, 0.05 is five percent).
     */
    public void setLowerMargin(double margin) {
        this.lowerMargin = margin;
        if (isAutoRange()) {
            autoAdjustRange();
        }
        notifyListeners(new AxisChangeEvent(this));
    }

    /**
     * Returns the upper margin for the axis, expressed as a percentage of the axis range.  This
     * controls the space added to the lower end of the axis when the axis range is automatically
     * calculated (it is ignored when the axis range is set explicitly). The default value is 
     * 0.05 (five percent).
     *
     * @return The upper margin.
     */
    public double getUpperMargin() {
        return this.upperMargin;
    }

    /**
     * Sets the upper margin for the axis (as a percentage of the axis range) and sends an
     * {@link AxisChangeEvent} to all registered listeners.  This margin is added only when the 
     * axis range is auto-calculated - if you set the axis range manually, the margin is ignored.
     *
     * @param margin  the margin percentage (for example, 0.05 is five percent).
     */
    public void setUpperMargin(double margin) {
        this.upperMargin = margin;
        if (isAutoRange()) {
            autoAdjustRange();
        }
        notifyListeners(new AxisChangeEvent(this));
    }

    /**
     * Returns the fixed auto range.
     *
     * @return the length.
     */
    public double getFixedAutoRange() {
        return this.fixedAutoRange;
    }

    /**
     * Sets the fixed auto range for the axis.
     *
     * @param length  the range length.
     */
    public void setFixedAutoRange(double length) {

        this.fixedAutoRange = length;
        notifyListeners(new AxisChangeEvent(this));

    }

    /**
     * Returns the lower bound of the axis range.
     *
     * @return The lower bound.
     */
    public double getLowerBound() {
        return this.range.getLowerBound();
    }

    /**
     * Sets the lower bound for the axis range.  An {@link AxisChangeEvent} is sent
     * to all registered listeners.
     *
     * @param min  the new minimum.
     */
    public void setLowerBound(double min) {
        if (this.range.getUpperBound() > min) {
            setRange(new Range(min, this.range.getUpperBound()));            
        }
        else {
            setRange(new Range(min, min + 1.0));                        
        }
    }

    /**
     * Returns the upper bound for the axis range.
     *
     * @return The upper bound.
     */
    public double getUpperBound() {
        return this.range.getUpperBound();
    }

    /**
     * Sets the upper bound for the axis range.  An {@link AxisChangeEvent} is sent
     * to all registered listeners.
     *
     * @param max  the new maximum.
     */
    public void setUpperBound(double max) {

        if (this.range.getLowerBound() < max) {
            setRange(new Range(this.range.getLowerBound(), max));
        }
        else {
            setRange(max - 1.0, max);
        }

    }

    /**
     * Returns the range for the axis.
     *
     * @return The axis range (never <code>null</code>).
     */
    public Range getRange() {
        return this.range;
    }

    /**
     * Sets the range attribute and sends an {@link AxisChangeEvent} to all registered
     * listeners.  As a side-effect, the auto-range flag is set to <code>false</code>.
     *
     * @param range  the range (<code>null</code> not permitted).
     */
    public void setRange(Range range) {
        // defer argument checking
        setRange(range, true, true);
    }

    /**
     * Sets the range for the axis, if requested, sends an {@link AxisChangeEvent} to all 
     * registered listeners.  As a side-effect, the auto-range flag is set to <code>false</code>
     * (optional).
     *
     * @param range  the range (<code>null</code> not permitted).
     * @param turnOffAutoRange  a flag that controls whether or not the auto range is turned off.
     * @param notify  a flag that controls whether or not listeners are notified.
     */
    public void setRange(Range range, boolean turnOffAutoRange, boolean notify) {
        if (range == null) {
            throw new IllegalArgumentException("Null 'range' argument.");
        }
        if (turnOffAutoRange) {
            this.autoRange = false;
        }
        this.range = range;
        if (notify) {
            notifyListeners(new AxisChangeEvent(this));
        }
    }

    /**
     * Sets the axis range and sends an {@link AxisChangeEvent} to all registered
     * listeners.  As a side-effect, the auto-range flag is set to <code>false</code>.
     *
     * @param lower  the lower axis limit.
     * @param upper  the upper axis limit.
     */
    public void setRange(double lower, double upper) {
        setRange(new Range(lower, upper));
    }
    
    /**
     * Sets the range for the axis (after first adding the current margins to the specified range)
     * and sends an {@link AxisChangeEvent} to all registered listeners.
     * 
     * @param range  the range (<code>null</code> not permitted).
     */
    public void setRangeWithMargins(Range range) {
        setRangeWithMargins(range, true, true);
    }

    /**
     * Sets the range for the axis after first adding the current margins to the range and, if 
     * requested, sends an {@link AxisChangeEvent} to all registered listeners.  As a side-effect,
     * the auto-range flag is set to <code>false</code> (optional).
     *
     * @param range  the range (excluding margins, <code>null</code> not permitted).
     * @param turnOffAutoRange  a flag that controls whether or not the auto range is turned off.
     * @param notify  a flag that controls whether or not listeners are notified.
     */
    public void setRangeWithMargins(Range range, boolean turnOffAutoRange, boolean notify) {
        if (range == null) {
            throw new IllegalArgumentException("Null 'range' argument.");
        }
        setRange(Range.expand(range, getLowerMargin(), getUpperMargin()), turnOffAutoRange, notify);
    }

    /**
     * Sets the axis range (after first adding the current margins to the range) and sends an 
     * {@link AxisChangeEvent} to all registered listeners.  As a side-effect, the auto-range 
     * flag is set to <code>false</code>.
     *
     * @param lower  the lower axis limit.
     * @param upper  the upper axis limit.
     */
    public void setRangeWithMargins(double lower, double upper) {
        setRangeWithMargins(new Range(lower, upper));
    }
    
    /**
     * Sets the axis range, where the new range is 'size' in length, and centered on 'value'.
     *
     * @param value  the central value.
     * @param length  the range length.
     */
    public void setRangeAboutValue(double value, double length) {
        setRange(new Range(value - length / 2, value + length / 2));
    }

    /**
     * Returns a flag indicating whether or not the tick unit is automatically
     * selected from a range of standard tick units.
     *
     * @return a flag indicating whether or not the tick unit is automatically selected.
     */
    public boolean isAutoTickUnitSelection() {
        return this.autoTickUnitSelection;
    }

    /**
     * Sets a flag indicating whether or not the tick unit is automatically
     * selected from a range of standard tick units.  If the flag is changed, registered
     * listeners are notified that the chart has changed.
     *
     * @param flag  the new value of the flag.
     */
    public void setAutoTickUnitSelection(boolean flag) {
        setAutoTickUnitSelection(flag, true);
    }

    /**
     * Sets a flag indicating whether or not the tick unit is automatically
     * selected from a range of standard tick units.
     *
     * @param flag  the new value of the flag.
     * @param notify  notify listeners?
     */
    public void setAutoTickUnitSelection(boolean flag, boolean notify) {

        if (this.autoTickUnitSelection != flag) {
            this.autoTickUnitSelection = flag;
            if (notify) {
                notifyListeners(new AxisChangeEvent(this));
            }
        }
    }

    /**
     * Returns the source for obtaining standard tick units for the axis.
     *
     * @return the source (possibly <code>null</code>).
     */
    public TickUnitSource getStandardTickUnits() {
        return this.standardTickUnits;
    }

    /**
     * Sets the source for obtaining standard tick units for the axis and sends an 
     * {@link AxisChangeEvent} to all registered listeners.  The axis will try to select the
     * smallest tick unit from the source that does not cause the tick labels to overlap (see 
     * also the {@link #setAutoTickUnitSelection(boolean)} method.
     *
     * @param source  the source for standard tick units (<code>null</code> permitted).
     */
    public void setStandardTickUnits(TickUnitSource source) {
        this.standardTickUnits = source;
        notifyListeners(new AxisChangeEvent(this));
    }
    
    /**
     * Converts a data value to a coordinate in Java2D space, assuming that the
     * axis runs along one edge of the specified dataArea.
     * <p>
     * Note that it is possible for the coordinate to fall outside the area.
     *
     * @param value  the data value.
     * @param area  the area for plotting the data.
     * @param edge  the edge along which the axis lies.
     *
     * @return the Java2D coordinate.
     */
    public abstract double valueToJava2D(double value, Rectangle2D area, RectangleEdge edge);
    
    /**
     * Converts a length in data coordinates into the corresponding length in Java2D
     * coordinates.
     * 
     * @param length  the length.
     * @param area  the plot area.
     * @param edge  the edge along which the axis lies.
     * 
     * @return The length in Java2D coordinates.
     */
    public double lengthToJava2D(double length, Rectangle2D area, RectangleEdge edge) {
        double zero = valueToJava2D(0.0, area, edge);
        double l = valueToJava2D(length, area, edge);
        return Math.abs(l - zero);
    }

    /**
     * Converts a coordinate in Java2D space to the corresponding data value,
     * assuming that the axis runs along one edge of the specified dataArea.
     *
     * @param java2DValue  the coordinate in Java2D space.
     * @param area  the area in which the data is plotted.
     * @param edge  the edge along which the axis lies.
     *
     * @return the data value.
     */
    public abstract double java2DToValue(double java2DValue,
                                         Rectangle2D area,
                                         RectangleEdge edge);

    /**
     * Automatically sets the axis range to fit the range of values in the dataset.
     * Sometimes this can depend on the renderer used as well (for example, the
     * renderer may "stack" values, requiring an axis range greater than otherwise
     * necessary).
     */
    protected abstract void autoAdjustRange();

    /**
     * Centers the axis range about the specified value and sends an {@link AxisChangeEvent}
     * to all registered listeners.
     *
     * @param value  the center value.
     */
    public void centerRange(double value) {

        double central = this.range.getCentralValue();
        Range adjusted = new Range(
            this.range.getLowerBound() + value - central,
            this.range.getUpperBound() + value - central
        );
        setRange(adjusted);

    }

    /**
     * Increases or decreases the axis range by the specified percentage about the
     * central value and sends an {@link AxisChangeEvent} to all registered listeners.
     * <P>
     * To double the length of the axis range, use 200% (2.0).
     * To halve the length of the axis range, use 50% (0.5).
     *
     * @param percent  the resize factor.
     */
    public void resizeRange(double percent) {
        resizeRange(percent, this.range.getCentralValue());
    }

    /**
     * Increases or decreases the axis range by the specified percentage about the
     * specified anchor value and sends an {@link AxisChangeEvent} to all registered 
     * listeners.
     * <P>
     * To double the length of the axis range, use 200% (2.0).
     * To halve the length of the axis range, use 50% (0.5).
     *
     * @param percent  the resize factor.
     * @param anchorValue  the new central value after the resize.
     */
    public void resizeRange(double percent, double anchorValue) {

        if (percent > 0.0) {
            double halfLength = this.range.getLength() * percent / 2;
            Range adjusted = new Range(anchorValue - halfLength, anchorValue + halfLength);
            setRange(adjusted);
        }
        else {
            setAutoRange(true);
        }

    }
    
    /**
     * Zooms in on the current range.
     * 
     * @param lowerPercent  the new lower bound.
     * @param upperPercent  the new upper bound.
     */
    public void zoomRange(double lowerPercent, double upperPercent) {
        double start = this.range.getLowerBound();
        double length = this.range.getLength();
        Range adjusted = null;
        if (isInverted()) {
            adjusted = new Range(start + (length * (1 - upperPercent)), 
                                 start + (length * (1 - lowerPercent)));            
        }
        else {
            adjusted = new Range(start + length * lowerPercent, start + length * upperPercent);
        }
        setRange(adjusted);
    }

    /**
     * Returns the auto tick index.
     *
     * @return the auto tick index.
     */
    protected int getAutoTickIndex() {
        return this.autoTickIndex;
    }

    /**
     * Sets the auto tick index.
     *
     * @param index  the new value.
     */
    protected void setAutoTickIndex(int index) {
        this.autoTickIndex = index;
    }

    /**
     * Tests the axis for equality with an arbitrary object.
     *
     * @param obj  the object (<code>null</code> permitted).
     *
     * @return <code>true</code> or <code>false</code>.
     */
    public boolean equals(Object obj) {

        if (obj == null) {
            return false;
        }

        if (obj == this) {
            return true;
        }

        if (obj instanceof ValueAxis) {
            ValueAxis axis = (ValueAxis) obj;

        
            if (super.equals(obj)) {
                boolean b1 = (this.positiveArrowVisible == axis.positiveArrowVisible);
                boolean b2 = (this.negativeArrowVisible == axis.negativeArrowVisible);
                boolean b3 = (this.inverted == axis.inverted);
                boolean b4 = ObjectUtils.equal(this.range, axis.range);
                boolean b5 = (this.autoRange == axis.autoRange);
                boolean b6 = (this.autoRangeMinimumSize == axis.autoRangeMinimumSize);
                boolean b7 = (Math.abs(this.upperMargin - axis.upperMargin) < 0.000001);
                boolean b8 = (Math.abs(this.lowerMargin - axis.lowerMargin) < 0.000001);
                boolean b9 = (Math.abs(this.fixedAutoRange - axis.fixedAutoRange) < 0.000001);
                boolean b10 = (this.autoTickUnitSelection == axis.autoTickUnitSelection);
                boolean b11 = ObjectUtils.equal(this.standardTickUnits, axis.standardTickUnits);
                boolean b12 = (this.verticalTickLabels == axis.verticalTickLabels);
                return b1 && b2 && b3 && b4 && b5 && b6 && b7 && b8 && b9 && b10 && b11 && b12;
            }
            else {
                return false;
            }

        }

        return false;

    }
    
    /**
     * Returns a clone of the object.
     * 
     * @return A clone.
     * 
     * @throws CloneNotSupportedException if some component of the axis does not support cloning.
     */
    public Object clone() throws CloneNotSupportedException {
        ValueAxis clone = (ValueAxis) super.clone();
        return clone;
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
        SerialUtilities.writeShape(this.upArrow, stream);
        SerialUtilities.writeShape(this.downArrow, stream);
        SerialUtilities.writeShape(this.leftArrow, stream);
        SerialUtilities.writeShape(this.rightArrow, stream);

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
        this.upArrow = SerialUtilities.readShape(stream);
        this.downArrow = SerialUtilities.readShape(stream);
        this.leftArrow = SerialUtilities.readShape(stream);
        this.rightArrow = SerialUtilities.readShape(stream);

    }
    
    //// DEPRECATED METHODS ///////////////////////////////////////////////////////////////////////
    
    /**
     * Returns the minimum value for the axis.
     *
     * @return the minimum value for the axis.
     * 
     * @deprecated Use getLowerBound().
     */
    public double getMinimumAxisValue() {
        return getLowerBound();
    }

    /**
     * Sets the minimum value for the axis.
     * <P>
     * Registered listeners are notified that the axis has been modified.
     *
     * @param min  the new minimum.
     * 
     * @deprecated Use setLowerBound(...).
     */
    public void setMinimumAxisValue(double min) {
        setLowerBound(min);
    }

    /**
     * Returns the maximum value for the axis.
     *
     * @return the maximum value.
     * 
     * @deprecated Use getUpperBound().
     */
    public double getMaximumAxisValue() {
        return getUpperBound();
    }

    /**
     * Sets the maximum value for the axis.
     * <P>
     * Registered listeners are notified that the axis has been modified.
     *
     * @param max  the new maximum.
     * 
     * @deprecated Use setUpperBound(...).
     */
    public void setMaximumAxisValue(double max) {
        setUpperBound(max);
    }
    
    /**
     * Converts a data value to a coordinate in Java2D space, assuming that the
     * axis runs along one edge of the specified dataArea.
     * <p>
     * Note that it is possible for the coordinate to fall outside the plotArea.
     *
     * @param value  the data value.
     * @param area  the area for plotting the data.
     * @param edge  the edge along which the axis lies.
     *
     * @return The Java2D coordinate.
     * 
     * @deprecated Use valueToJava2D instead.
     */
    public double translateValueToJava2D(double value,
            Rectangle2D area,
            RectangleEdge edge) {
        return valueToJava2D(value, area, edge);   
    }
    
    /**
     * Converts a coordinate in Java2D space to the corresponding data value,
     * assuming that the axis runs along one edge of the specified dataArea.
     *
     * @param java2DValue  the coordinate in Java2D space.
     * @param dataArea  the area in which the data is plotted.
     * @param edge  the edge along which the axis lies.
     *
     * @return the data value.
     * 
     * @deprecated Use translateJava2DToValue(double, ...).
     */
    public double translateJava2DtoValue(float java2DValue,
            Rectangle2D dataArea,
            RectangleEdge edge) {
        
        return translateJava2DToValue(java2DValue, dataArea, edge);
        
    }
    
    /**
     * Converts a coordinate in Java2D space to the corresponding data value,
     * assuming that the axis runs along one edge of the specified area.
     *
     * @param java2DValue  the coordinate in Java2D space.
     * @param area  the area in which the data is plotted.
     * @param edge  the edge along which the axis lies.
     *
     * @return the data value.
     * 
     * @deprecated Use java2DToValue instead.
     */
    public double translateJava2DToValue(double java2DValue,
                                         Rectangle2D area,
                                         RectangleEdge edge) {
        return java2DToValue(java2DValue, area, edge);   
    }
    
}
