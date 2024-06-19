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
 * CategoryAxis.java
 * -----------------
 * (C) Copyright 2000-2003, by Object Refinery Limited.
 *
 * Original Author:  David Gilbert;
 * Contributor(s):   -;
 *
 * $Id: CategoryAxis.java,v 1.1 2007/10/10 19:19:06 vauchers Exp $
 *
 * Changes (from 21-Aug-2001)
 * --------------------------
 * 21-Aug-2001 : Added standard header. Fixed DOS encoding problem (DG);
 * 18-Sep-2001 : Updated header (DG);
 * 04-Dec-2001 : Changed constructors to protected, and tidied up default values (DG);
 * 19-Apr-2002 : Updated import statements (DG);
 * 05-Sep-2002 : Updated constructor for changes in Axis class (DG);
 * 06-Nov-2002 : Moved margins from the CategoryPlot class (DG);
 * 08-Nov-2002 : Moved to new package com.jrefinery.chart.axis (DG);
 * 22-Jan-2002 : Removed monolithic constructor (DG);
 * 26-Mar-2003 : Implemented Serializable (DG);
 * 09-May-2003 : Merged HorizontalCategoryAxis and VerticalCategoryAxis into this class (DG);
 * 13-Aug-2003 : Implemented Cloneable (DG);
 * 29-Oct-2003 : Added workaround for font alignment in PDF output (DG);
 * 05-Nov-2003 : Fixed serialization bug (DG);
 *
 */

package org.jfree.chart.axis;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Iterator;
import java.util.List;

import org.jfree.chart.event.AxisChangeEvent;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.Plot;
import org.jfree.io.SerialUtilities;
import org.jfree.text.TextBlock;
import org.jfree.text.TextBlockAnchor;
import org.jfree.text.TextLine;
import org.jfree.ui.RectangleAnchor;
import org.jfree.ui.RectangleEdge;
import org.jfree.ui.RefineryUtilities;
import org.jfree.ui.TextAnchor;

/**
 * The base class for axes that display categories.
 *
 * @author David Gilbert
 */
public class CategoryAxis extends Axis implements Cloneable, Serializable {

    /** The default setting for rotated category labels. */
    public static final boolean DEFAULT_VERTICAL_CATEGORY_LABELS = false;

    /** The default margin for the axis (used for both lower and upper margins). */
    public static final double DEFAULT_AXIS_MARGIN = 0.05;

    /** The default margin between categories (a percentage of the overall axis length). */
    public static final double DEFAULT_CATEGORY_MARGIN = 0.20;

    /** The default axis line paint. */
    public static final Paint DEFAULT_AXIS_LINE_PAINT = Color.gray;

    /** The amount of space reserved at the start of the axis. */
    private double lowerMargin;

    /** The amount of space reserved at the end of the axis. */
    private double upperMargin;

    /** The amount of space reserved between categories. */
    private double categoryMargin;
    
    /** A flag that controls whether or not the axis line is visibile. */
    private boolean axisLineVisible = true;

    /** The paint used for the axis line. */
    private transient Paint axisLinePaint = DEFAULT_AXIS_LINE_PAINT;
    
    /** The stroke used for the axis line. */
    private transient Stroke axisLineStroke = new BasicStroke(1.0f);
    
    /** A flag that indicates whether the category labels should be rotated 90 degrees. */
    private boolean verticalCategoryLabels;  // need to replace this with rotation, so that it fits
                                             // with both horizontal and vertical axes

    /** Positioning info for category labels when the axis is at the top of the plot area. */
    private CategoryLabelPosition topCategoryLabelPosition;

    /** Positioning info for category labels when the axis is at the bottom of the plot area. */
    private CategoryLabelPosition bottomCategoryLabelPosition;

    /** Positioning info for category labels when the axis is at the left of the plot area. */
    private CategoryLabelPosition leftCategoryLabelPosition;

    /** Positioning info for category labels when the axis is at the right of the plot area. */
    private CategoryLabelPosition rightCategoryLabelPosition;
    
    /**
     * Constructs a category axis, using default values where necessary.
     *
     * @param label  the axis label (<code>null</code> permitted).
     */
    public CategoryAxis(String label) {

        super(label);

        this.lowerMargin = DEFAULT_AXIS_MARGIN;
        this.upperMargin = DEFAULT_AXIS_MARGIN;
        this.categoryMargin = DEFAULT_CATEGORY_MARGIN;
        this.verticalCategoryLabels = DEFAULT_VERTICAL_CATEGORY_LABELS;
        
        setTickMarksVisible(false);  // not supported by this axis type yet
        
        this.topCategoryLabelPosition = new CategoryLabelPosition(
            RectangleAnchor.BOTTOM, TextBlockAnchor.BOTTOM_CENTER, TextAnchor.BOTTOM_CENTER, 0.0
        );
        this.bottomCategoryLabelPosition = new CategoryLabelPosition(
            RectangleAnchor.TOP, TextBlockAnchor.TOP_CENTER, TextAnchor.TOP_CENTER, 0.0
        );
        this.leftCategoryLabelPosition = new CategoryLabelPosition(
            RectangleAnchor.RIGHT, TextBlockAnchor.CENTER_RIGHT, TextAnchor.CENTER_RIGHT, 0.0
        );
        this.rightCategoryLabelPosition = new CategoryLabelPosition(
            RectangleAnchor.LEFT, TextBlockAnchor.CENTER_LEFT, TextAnchor.CENTER_LEFT, 0.0
        );
    }

    /**
     * Returns the lower margin for the axis.
     *
     * @return the margin.
     */
    public double getLowerMargin() {
        return this.lowerMargin;
    }

    /**
     * Sets the lower margin for the axis.  An {@link AxisChangeEvent} is sent to all registered
     * listeners.
     *
     * @param margin  the new margin.
     */
    public void setLowerMargin(double margin) {
        this.lowerMargin = margin;
        notifyListeners(new AxisChangeEvent(this));
    }

    /**
     * Returns the upper margin for the axis.
     *
     * @return the margin.
     */
    public double getUpperMargin() {
        return this.upperMargin;
    }

    /**
     * Sets the upper margin for the axis.  An {@link AxisChangeEvent} is sent to all registered
     * listeners.
     *
     * @param margin  the new margin.
     */
    public void setUpperMargin(double margin) {
        this.upperMargin = margin;
        notifyListeners(new AxisChangeEvent(this));
    }

    /**
     * Returns the category margin.
     *
     * @return the margin.
     */
    public double getCategoryMargin() {
        return this.categoryMargin;
    }

    /**
     * Sets the category margin.  An {@link AxisChangeEvent} is sent to all registered
     * listeners.
     *
     * @param margin  the new margin.
     */
    public void setCategoryMargin(double margin) {
        this.categoryMargin = margin;
        notifyListeners(new AxisChangeEvent(this));
    }

    /**
     * Returns a flag indicating whether the category labels are rotated to vertical.
     *
     * @return The flag.
     * 
     * @deprecated Use the get/setXXXCategoryLabelPosition methods.
     */
    public boolean isVerticalCategoryLabels() {
        return this.verticalCategoryLabels;
    }

    /**
     * Sets the flag that determines whether the category labels are rotated to vertical.
     *
     * @param flag  the flag.
     * 
     * @deprecated Use the get/setXXXCategoryLabelPosition methods.
     */
    public void setVerticalCategoryLabels(boolean flag) {

        if (this.verticalCategoryLabels != flag) {
            this.verticalCategoryLabels = flag;
            if (flag) {
                double angle1 = Math.PI / 2.0;
                double angle2 = -angle1;   

                this.topCategoryLabelPosition = new CategoryLabelPosition(
                    this.topCategoryLabelPosition.getCategoryAnchor(),
                    TextBlockAnchor.CENTER_RIGHT,
                    TextAnchor.CENTER_RIGHT,
                    angle1
                );
            
                this.bottomCategoryLabelPosition = new CategoryLabelPosition(
                    this.bottomCategoryLabelPosition.getCategoryAnchor(),
                    TextBlockAnchor.CENTER_RIGHT,
                    TextAnchor.CENTER_RIGHT,
                    angle2
                );
            
                this.leftCategoryLabelPosition = new CategoryLabelPosition(
                    this.leftCategoryLabelPosition.getCategoryAnchor(),
                    TextBlockAnchor.CENTER_RIGHT,
                    this.leftCategoryLabelPosition.getRotationAnchor(),
                    angle1
                );
            
                this.rightCategoryLabelPosition = new CategoryLabelPosition(
                    this.rightCategoryLabelPosition.getCategoryAnchor(),
                    TextBlockAnchor.CENTER_LEFT,
                    this.rightCategoryLabelPosition.getRotationAnchor(),
                    angle2
                );
            }
            else {
                this.topCategoryLabelPosition = new CategoryLabelPosition(
                    this.topCategoryLabelPosition.getCategoryAnchor(),
                    this.topCategoryLabelPosition.getLabelAnchor(),
                    this.topCategoryLabelPosition.getRotationAnchor(),
                    0.0
                );
            
                this.bottomCategoryLabelPosition = new CategoryLabelPosition(
                    this.bottomCategoryLabelPosition.getCategoryAnchor(),
                    this.bottomCategoryLabelPosition.getLabelAnchor(),
                    this.bottomCategoryLabelPosition.getRotationAnchor(),
                    0.0
                );
            
                this.leftCategoryLabelPosition = new CategoryLabelPosition(
                    this.leftCategoryLabelPosition.getCategoryAnchor(),
                    this.leftCategoryLabelPosition.getLabelAnchor(),
                    this.leftCategoryLabelPosition.getRotationAnchor(),
                    0.0
                );
            
                this.rightCategoryLabelPosition = new CategoryLabelPosition(
                    this.rightCategoryLabelPosition.getCategoryAnchor(),
                    this.rightCategoryLabelPosition.getLabelAnchor(),
                    this.rightCategoryLabelPosition.getRotationAnchor(),
                    0.0
                );
            }
            notifyListeners(new AxisChangeEvent(this));
        }

    }

    /**
     * Returns the flag that determines whether the category labels are to be
     * skipped to avoid overlapping.
     *
     * @return The flag.
     * 
     * @deprecated No longer supported.
     */
    public boolean getSkipCategoryLabelsToFit() {
        return false;
    }

    /**
     * Sets the flag that determines whether the category labels are to be
     * skipped to avoid overlapping.
     *
     * @param flag  the new value of the flag.
     * 
     * @deprecated No longer supported.
     */
    public void setSkipCategoryLabelsToFit(boolean flag) {

    }

    /**
     * A flag that controls whether or not the axis line is drawn.
     * 
     * @return A boolean.
     */
    public boolean isAxisLineVisible() {
        return this.axisLineVisible;
    }
    
    /**
     * Sets a flag that controls whether or not the axis line is visible.
     * 
     * @param visible  the flag.
     */
    public void setAxisLineVisible(boolean visible) {
        this.axisLineVisible = visible;
        notifyListeners(new AxisChangeEvent(this));
    }
    
    /**
     * Returns the category label positioning info that applies when the axis is displayed
     * at the top of the plot area.
     * 
     * @return the position info.
     */
    public CategoryLabelPosition getTopCategoryLabelPosition() {
        return this.topCategoryLabelPosition;
    }
    
    /**
     * Sets the position info that applies when the axis is displayed at the top of the 
     * plot area.
     * 
     * @param position  the position info.
     */
    public void setTopCategoryLabelPosition(CategoryLabelPosition position) {
        this.topCategoryLabelPosition = position;
    }

    /**
     * Returns the category label positioning info that applies when the axis is displayed
     * at the bottom of the plot area.
     * 
     * @return the position info.
     */
    public CategoryLabelPosition getBottomCategoryLabelPosition() {
        return this.bottomCategoryLabelPosition;
    }
    
    /**
     * Sets the position info that applies when the axis is displayed at the bottom of the 
     * plot area.
     * 
     * @param position  the position info.
     */
    public void setBottomCategoryLabelPosition(CategoryLabelPosition position) {
        this.bottomCategoryLabelPosition = position;
    }

    /**
     * Returns the category label positioning info that applies when the axis is displayed
     * at the left of the plot area.
     * 
     * @return the position info.
     */
    public CategoryLabelPosition getLeftCategoryLabelPosition() {
        return this.leftCategoryLabelPosition;
    }
    
    /**
     * Sets the position info that applies when the axis is displayed at the left of the 
     * plot area.
     * 
     * @param position  the position info.
     */
    public void setLeftCategoryLabelPosition(CategoryLabelPosition position) {
        this.leftCategoryLabelPosition = position;
    }

    /**
     * Returns the category label positioning info that applies when the axis is displayed
     * at the right of the plot area.
     * 
     * @return the position info.
     */
    public CategoryLabelPosition getRightCategoryLabelPosition() {
        return this.rightCategoryLabelPosition;
    }
    
    /**
     * Sets the position info that applies when the axis is displayed at the right of the 
     * plot area.
     * 
     * @param position  the position info.
     */
    public void setRightCategoryLabelPosition(CategoryLabelPosition position) {
        this.rightCategoryLabelPosition = position;
    }

    /**
     * Returns the Java 2D coordinate for a category.
     * 
     * @param anchor  the anchor point.
     * @param category  the category index.
     * @param categoryCount  the category count.
     * @param area  the data area.
     * @param edge  the location of the axis.
     * 
     * @return The coordinate.
     */
    public double getCategoryJava2DCoordinate(CategoryAnchor anchor, 
                                              int category, int categoryCount, Rectangle2D area,
                                              RectangleEdge edge) {
    
        double result = 0.0;
        if (anchor == CategoryAnchor.START) {
            result = getCategoryStart(category, categoryCount, area, edge);
        }
        else if (anchor == CategoryAnchor.MIDDLE) {
            result = getCategoryMiddle(category, categoryCount, area, edge);
        }
        else if (anchor == CategoryAnchor.END) {
            result = getCategoryEnd(category, categoryCount, area, edge);
        }
        return result;
                                                      
    }
                                              
    /**
     * Returns the starting coordinate for the specified category.
     *
     * @param category  the category.
     * @param categoryCount  the number of categories.
     * @param area  the data area.
     * @param edge  the axis location.
     *
     * @return the coordinate.
     */
    public double getCategoryStart(int category, int categoryCount, Rectangle2D area,
                                   RectangleEdge edge) {

        double result = 0.0;
        if ((edge == RectangleEdge.TOP) || (edge == RectangleEdge.BOTTOM)) {
            result = area.getX() + area.getWidth() * getLowerMargin();
        }
        else if ((edge == RectangleEdge.LEFT) || (edge == RectangleEdge.RIGHT)) {
            result = area.getMinY() + area.getHeight() * getLowerMargin();
        }

        double categorySize = calculateCategorySize(categoryCount, area, edge);
        double categoryGapWidth = calculateCategoryGapSize(categoryCount, area, edge);

        result = result + category * (categorySize + categoryGapWidth);

        return result;
    }

    /**
     * Returns the middle coordinate for the specified category.
     *
     * @param category  the category.
     * @param categoryCount  the number of categories.
     * @param area  the data area.
     * @param edge  the axis location.
     *
     * @return the coordinate.
     */
    public double getCategoryMiddle(int category, int categoryCount, Rectangle2D area,
                                    RectangleEdge edge) {

        return getCategoryStart(category, categoryCount, area, edge)
               + calculateCategorySize(categoryCount, area, edge) / 2;

    }

    /**
     * Returns the end coordinate for the specified category.
     *
     * @param category  the category.
     * @param categoryCount  the number of categories.
     * @param area  the data area.
     * @param edge  the axis location.
     *
     * @return the coordinate.
     */
    public double getCategoryEnd(int category, int categoryCount, Rectangle2D area,
                                 RectangleEdge edge) {

        return getCategoryStart(category, categoryCount, area, edge)
               + calculateCategorySize(categoryCount, area, edge);

    }

    /**
     * Calculates the size (width or height, depending on the location of the axis) of a category.
     *
     * @param categoryCount  the number of categories.
     * @param area  the area within which the categories will be drawn.
     * @param edge  the axis location.
     *
     * @return the category size.
     */
    protected double calculateCategorySize(int categoryCount, Rectangle2D area,
                                           RectangleEdge edge) {

        double result = 0.0;
        double available = 0.0;

        if ((edge == RectangleEdge.TOP) || (edge == RectangleEdge.BOTTOM)) {
            available = area.getWidth();
        }
        else if ((edge == RectangleEdge.LEFT) || (edge == RectangleEdge.RIGHT)) {
            available = area.getHeight();
        }
        if (categoryCount > 1) {
            result = available * (1 - getLowerMargin() - getUpperMargin() - getCategoryMargin());
            result = result / categoryCount;
        }
        else {
            result = available * (1 - getLowerMargin() - getUpperMargin());
        }
        return result;

    }

    /**
     * Calculates the size (width or height, depending on the location of the axis) of a category
     * gap.
     *
     * @param categoryCount  the number of categories.
     * @param area  the area within which the categories will be drawn.
     * @param edge  the axis location.
     *
     * @return the category gap width.
     */
    protected double calculateCategoryGapSize(int categoryCount, Rectangle2D area,
                                              RectangleEdge edge) {

        double result = 0.0;
        double available = 0.0;

        if ((edge == RectangleEdge.TOP) || (edge == RectangleEdge.BOTTOM)) {
            available = area.getWidth();
        }
        else if ((edge == RectangleEdge.LEFT) || (edge == RectangleEdge.RIGHT)) {
            available = area.getHeight();
        }

        if (categoryCount > 1) {
            result = available * getCategoryMargin() / (categoryCount - 1);
        }

        return result;

    }

    /**
     * Estimates the space required for the axis, given a specific drawing area, without any
     * information about the space required for the range axis/axes.
     *
     * @param g2  the graphics device (used to obtain font information).
     * @param plot  the plot that the axis belongs to.
     * @param plotArea  the area within which the axis should be drawn.
     * @param edge  the axis location (top or bottom).
     * @param space  the space already reserved.
     *
     * @return the estimated height required for the axis.
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

        // calculate the max size of the tick labels (if visible)...
        double tickLabelHeight = 0.0;
        double tickLabelWidth = 0.0;
        if (isTickLabelsVisible()) {
            g2.setFont(getTickLabelFont());
            AxisState state = new AxisState();
            refreshTicks(g2, state, plotArea, plotArea, edge);
            //CategoryPlot categoryPlot = (CategoryPlot) plot;
            if (edge == RectangleEdge.TOP) {
                tickLabelHeight = state.getMax();
            }
            else if (edge == RectangleEdge.BOTTOM) {
                tickLabelHeight = state.getMax();
            }
            else if (edge == RectangleEdge.LEFT) {
                tickLabelWidth = state.getMax(); 
            }
            else if (edge == RectangleEdge.RIGHT) {
                tickLabelWidth = state.getMax(); 
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
     * Configures the axis against the current plot.  Nothing required in this class.
     */
    public void configure() {
    }

    /**
     * Draws the axis on a Java 2D graphics device (such as the screen or a printer).
     *
     * @param g2  the graphics device.
     * @param cursor  the cursor location.
     * @param plotArea  the area within which the axis should be drawn.
     * @param dataArea  the area within which the plot is being drawn.
     * @param edge  the location of the axis.
     * 
     * @return The new cursor location.
     */
    public AxisState draw(Graphics2D g2, double cursor, 
                          Rectangle2D plotArea, Rectangle2D dataArea,
                          RectangleEdge edge) {
        
        // if the axis is not visible, don't draw it...
        if (!isVisible()) {
            return new AxisState(cursor);
        }

        if (isAxisLineVisible()) {
            drawAxisLine(g2, cursor, dataArea, edge);
        }

        // draw the category labels and axis label
        AxisState state = new AxisState(cursor);
        state = drawCategoryLabels(g2, plotArea, dataArea, edge, state);
        state = drawLabel(getLabel(), g2, plotArea, dataArea, edge, state);
    
        return state;

    }

    /**
     * Draws the category labels when the axis is 'horizontal'.
     *
     * @param g2  the graphics device.
     * @param plotArea  the plot area.
     * @param dataArea  the area inside the axes.
     * @param edge  the axis location.
     * @param state  the axis state.
     * 
     * @return The revised axis state.
     */
    protected AxisState drawCategoryLabels(Graphics2D g2,
                                           Rectangle2D plotArea,
                                           Rectangle2D dataArea,
                                           RectangleEdge edge,
                                           AxisState state) {

        if (state == null) {
            throw new IllegalArgumentException("null state not permitted.");
        }

        if (isTickLabelsVisible()) {
            g2.setFont(getTickLabelFont());
            g2.setPaint(getTickLabelPaint());
            List ticks = refreshTicks(g2, state, plotArea, dataArea, edge);
            state.setTicks(ticks);        
            // need to get the max height or width during the refreshTicks call...
            
            int categoryIndex = 0;
            Iterator iterator = ticks.iterator();
            while (iterator.hasNext()) {
                
                CategoryTick tick = (CategoryTick) iterator.next();
                g2.setPaint(getTickLabelPaint());

                CategoryLabelPosition position = null;
                double x0 = 0.0;
                double x1 = 0.0;
                double y0 = 0.0;
                double y1 = 0.0;
                if (edge == RectangleEdge.TOP) {
                    x0 = getCategoryStart(categoryIndex, ticks.size(), dataArea, edge);
                    x1 = getCategoryEnd(categoryIndex, ticks.size(), dataArea, edge);
                    y1 = state.getCursor() - 2.0;
                    y0 = y1 - state.getMax();
                    position = this.topCategoryLabelPosition;
                }
                else if (edge == RectangleEdge.BOTTOM) {
                    x0 = getCategoryStart(categoryIndex, ticks.size(), dataArea, edge);
                    x1 = getCategoryEnd(categoryIndex, ticks.size(), dataArea, edge); 
                    y0 = state.getCursor() + 2.0;                   
                    y1 = y0 + state.getMax();
                    position = this.bottomCategoryLabelPosition;
                }
                else if (edge == RectangleEdge.LEFT) {
                    y0 = getCategoryStart(categoryIndex, ticks.size(), dataArea, edge);
                    y1 = getCategoryEnd(categoryIndex, ticks.size(), dataArea, edge);                    
                    x1 = state.getCursor() - 2.0;
                    x0 = x1 - state.getMax();
                    position = this.leftCategoryLabelPosition;
                }
                else if (edge == RectangleEdge.RIGHT) {
                    y0 = getCategoryStart(categoryIndex, ticks.size(), dataArea, edge);
                    y1 = getCategoryEnd(categoryIndex, ticks.size(), dataArea, edge);                    
                    x0 = state.getCursor() + 2.0;
                    x1 = x0 - state.getMax();
                    position = this.rightCategoryLabelPosition;
                }
                Rectangle2D area = new Rectangle2D.Double(x0, y0, (x1 - x0), (y1 - y0));
                double[] anchorPoint = RectangleAnchor.coordinates(area, position.getCategoryAnchor());
                TextBlock block = tick.getLabel();
                block.draw(g2, 
                           (float) anchorPoint[0], (float) anchorPoint[1], position.getLabelAnchor(), 
                           (float) anchorPoint[0], (float) anchorPoint[1], position.getAngle());
                categoryIndex++;
            }

            if (edge.equals(RectangleEdge.TOP)) {
                double h = state.getMax();
                state.cursorUp(h);
            }
            else if (edge.equals(RectangleEdge.BOTTOM)) {
                double h = state.getMax();
                state.cursorDown(h);
            }
            else if (edge == RectangleEdge.LEFT) {
                double w = state.getMax();
                state.cursorLeft(w);
            }
            else if (edge == RectangleEdge.RIGHT) {
                double w = state.getMax();
                state.cursorRight(w);
            }
        }
        return state;
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
        g2.setPaint(this.axisLinePaint);
        g2.setStroke(this.axisLineStroke);
        g2.draw(axisLine);
        
    }

    /**
     * Creates a temporary list of ticks that can be used when drawing the axis.
     *
     * @param g2  the graphics device (used to get font measurements).
     * @param state  the axis state.
     * @param plotArea  the area where the plot and axes will be drawn.
     * @param dataArea  the area inside the axes.
     * @param edge  the location of the axis.
     * 
     * @return A list of ticks.
     */
    public List refreshTicks(Graphics2D g2, 
                             AxisState state,
                             Rectangle2D plotArea, 
                             Rectangle2D dataArea,
                             RectangleEdge edge) {

        List ticks = new java.util.ArrayList();
        CategoryPlot plot = (CategoryPlot) getPlot();
        List categories = plot.getCategories();
        double max = 0.0;
                
        if (categories != null) {
            int categoryIndex = 0;
            Iterator iterator = categories.iterator();
            while (iterator.hasNext()) {
                Comparable category = (Comparable) iterator.next();
                TextBlock label = createLabel(category, 0.0, edge);
                CategoryLabelPosition position = null;
                if (edge == RectangleEdge.TOP) {
                    position = this.topCategoryLabelPosition;
                    max = Math.max(max, calculateTextBlockHeight(label, position, g2));
                }
                else if (edge == RectangleEdge.BOTTOM) {
                    position = this.bottomCategoryLabelPosition;
                    max = Math.max(max, calculateTextBlockHeight(label, position, g2));
                }
                else if (edge == RectangleEdge.LEFT) {
                    position = this.leftCategoryLabelPosition;
                    max = Math.max(max, calculateTextBlockWidth(label, position, g2));
                }
                else if (edge == RectangleEdge.RIGHT) {
                    position = this.rightCategoryLabelPosition;
                    max = Math.max(max, calculateTextBlockWidth(label, position, g2));
                }
                Tick tick = new CategoryTick(category, 
                                             label,
                                             position.getLabelAnchor(), 
                                             position.getRotationAnchor(), 
                                             position.getAngle());
                ticks.add(tick);
                categoryIndex = categoryIndex + 1;
            }
        }
        state.setMax(max);
        return ticks;
        
    }

    /**
     * Creates a label.
     *
     * @param category  the category.
     * @param width  the available width. 
     * @param edge  the edge on which the axis appears.
     *
     * @return a label.
     */
    protected TextBlock createLabel(Comparable category, double width, RectangleEdge edge) {
        TextBlock label = new TextBlock();
        TextLine line = new TextLine(category.toString(), getTickLabelFont());
        label.addLine(line);   
        return label; 
    }
    
    /**
     * A utility method for determining the width of a text block.
     *
     * @param block  the text block.
     * @param position  the position.
     * @param g2  the graphics device.
     *
     * @return the height of the tallest tick label.
     */
    protected double calculateTextBlockWidth(TextBlock block, 
                                             CategoryLabelPosition position, 
                                             Graphics2D g2) {
                                                    
        Insets insets = getTickLabelInsets();
        Dimension size = block.calculateDimensions(g2);
        Rectangle2D box = new Rectangle2D.Double(0.0, 0.0, size.getWidth(), size.getHeight());
        Shape rotatedBox = RefineryUtilities.rotateShape(
            box, g2, position.getAngle(), 0.0f, 0.0f
        );
        double w = rotatedBox.getBounds2D().getWidth() + insets.top + insets.bottom;
        return w;
        
    }

    /**
     * A utility method for determining the height of a text block.
     *
     * @param block  the text block.
     * @param position  the label position.
     * @param g2  the graphics device.
     *
     * @return the height.
     */
    protected double calculateTextBlockHeight(TextBlock block, 
                                              CategoryLabelPosition position, 
                                              Graphics2D g2) {
                                                    
        Insets insets = getTickLabelInsets();
        Dimension size = block.calculateDimensions(g2);
        Rectangle2D box = new Rectangle2D.Double(0.0, 0.0, size.getWidth(), size.getHeight());
        Shape rotatedBox = RefineryUtilities.rotateShape(
            box, g2, position.getAngle(), 0.0f, 0.0f
        );
        double h = rotatedBox.getBounds2D().getHeight() + insets.top + insets.bottom;
        return h;
        
    }

    /**
     * Creates a clone of the axis.
     * 
     * @return A clone.
     * 
     * @throws CloneNotSupportedException if some component of the axis does not support cloning.
     */
    public Object clone() throws CloneNotSupportedException {
    
        Object clone = super.clone();
        return clone;
            
    }
    
    /**
     * Tests this axis for equality with another object.
     *
     * @param obj  the object.
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

        if (obj instanceof CategoryAxis) {
            CategoryAxis axis = (CategoryAxis) obj;
            if (super.equals(obj)) {
                boolean b0 = (this.lowerMargin == axis.lowerMargin);
                boolean b1 = (this.upperMargin == axis.upperMargin);
                boolean b2 = (this.categoryMargin == axis.categoryMargin);
                boolean b3 = (this.verticalCategoryLabels == axis.verticalCategoryLabels);
                
                return b0 && b1 && b2 && b3;
            }
        }

        return false;

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
        SerialUtilities.writePaint(this.axisLinePaint, stream);
        SerialUtilities.writeStroke(this.axisLineStroke, stream);
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
        this.axisLinePaint = SerialUtilities.readPaint(stream);
        this.axisLineStroke = SerialUtilities.readStroke(stream);

    }

}
