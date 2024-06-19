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
 * $Id: CategoryAxis.java,v 1.1 2007/10/10 19:15:30 vauchers Exp $
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
 *
 */

package org.jfree.chart.axis;

import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.font.FontRenderContext;
import java.awt.font.LineBreakMeasurer;
import java.awt.font.LineMetrics;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.io.Serializable;
import java.text.AttributedCharacterIterator;
import java.text.AttributedString;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.jfree.chart.event.AxisChangeEvent;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.Plot;
import org.jfree.ui.RectangleEdge;
import org.jfree.ui.RefineryUtilities;

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

    /** The amount of space reserved at the start of the axis. */
    private double lowerMargin;

    /** The amount of space reserved at the end of the axis. */
    private double upperMargin;

    /** The amount of space reserved between categories. */
    private double categoryMargin;

    /** A flag that indicates whether the category labels should be rotated 90 degrees. */
    private boolean verticalCategoryLabels;  // need to replace this with rotation, so that it fits
                                             // with both horizontal and vertical axes

    /** A flag that controls whether to skip category labels to avoid overlapping. */
    private boolean skipCategoryLabelsToFit;

    /** Tick line count */
    private int maxTickLineCount;

    /** Temporary list of categories. */
    private List categories;
    
    /** A temporary record of the amount of space reserved for the tick labels. */
    protected double reservedForTickLabels;
    
    /** A temporary record of the amount of space reserved for the axis label. */
    protected double reservedForAxisLabel;
    
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
        this.setTickMarksVisible(false);  // not supported by this axis type yet
        this.categories = new java.util.ArrayList();
        
        this.reservedForAxisLabel = 0.0;
        this.reservedForTickLabels = 0.0;

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
     */
    public boolean isVerticalCategoryLabels() {
        return this.verticalCategoryLabels;
    }

    /**
     * Sets the flag that determines whether the category labels are rotated to vertical.
     *
     * @param flag  the flag.
     */
    public void setVerticalCategoryLabels(boolean flag) {

        if (this.verticalCategoryLabels != flag) {
            this.verticalCategoryLabels = flag;
            notifyListeners(new AxisChangeEvent(this));
        }

    }

    /**
     * Returns the flag that determines whether the category labels are to be
     * skipped to avoid overlapping.
     *
     * @return The flag.
     */
    public boolean getSkipCategoryLabelsToFit() {
        return this.skipCategoryLabelsToFit;
    }

    /**
     * Sets the flag that determines whether the category labels are to be
     * skipped to avoid overlapping.
     *
     * @param flag  the new value of the flag.
     */
    public void setSkipCategoryLabelsToFit(boolean flag) {

        if (this.skipCategoryLabelsToFit != flag) {
            this.skipCategoryLabelsToFit = flag;
            notifyListeners(new AxisChangeEvent(this));
        }

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
            refreshTicks(g2, 0.0, plotArea, plotArea, edge);
            Insets tickLabelInsets = getTickLabelInsets();
            if (RectangleEdge.isTopOrBottom(edge)) {
                tickLabelHeight = tickLabelInsets.top + tickLabelInsets.bottom;
                tickLabelHeight += getMaxTickLabelHeight(g2, plotArea, isVerticalCategoryLabels());
                this.reservedForTickLabels = tickLabelHeight;
            }
            else if (RectangleEdge.isLeftOrRight(edge)) {
                tickLabelWidth = tickLabelInsets.left + tickLabelInsets.right;
                tickLabelWidth += getMaxTickLabelWidth(g2, plotArea);
                this.reservedForTickLabels = tickLabelWidth;
            }
        }
        
        // get the axis label size and update the space object...
        Rectangle2D labelEnclosure = getLabelEnclosure(g2, edge);
        double labelHeight = 0.0;
        double labelWidth = 0.0;
        if (RectangleEdge.isTopOrBottom(edge)) {
            labelHeight = labelEnclosure.getHeight();
            space.ensureAtLeast(labelHeight + tickLabelHeight, edge);
            this.reservedForAxisLabel = labelHeight;
        }
        else if (RectangleEdge.isLeftOrRight(edge)) {
            labelWidth = labelEnclosure.getWidth();
            space.ensureAtLeast(labelWidth + tickLabelWidth, edge);
            this.reservedForAxisLabel = labelWidth;
        }

        return space;

    }

    /**
     * Configures the axis against the current plot.  Nothing required in this class.
     */
    public void configure() {
        CategoryPlot plot = (CategoryPlot) this.getPlot();
        this.categories = plot.getCategories();
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
    public double draw(Graphics2D g2, double cursor, 
                       Rectangle2D plotArea, Rectangle2D dataArea,
                       RectangleEdge edge) {

        // if the axis is not visible, don't draw it...
        if (!isVisible()) {
            return 0.0;
        }

        // draw the category labels
        double used1 = 0.0;
        if (edge == RectangleEdge.TOP || edge == RectangleEdge.BOTTOM) {
            used1 = drawHorizontalCategoryLabels(g2, cursor, plotArea, dataArea, edge);
        }
        else if (edge == RectangleEdge.LEFT || edge == RectangleEdge.RIGHT) {
            used1 = drawVerticalCategoryLabels(g2, cursor, plotArea, dataArea, edge);
        }
        if (edge == RectangleEdge.TOP || edge == RectangleEdge.LEFT) {
            cursor = cursor - used1;
        }
        else if (edge == RectangleEdge.BOTTOM || edge == RectangleEdge.RIGHT) {
            cursor = cursor + used1;
        }

        // draw the axis label...
        double used2 = drawLabel(getLabel(), g2, cursor, plotArea, dataArea, edge);

    
        return used1 + used2;

    }

    /**
     * Draws the category labels when the axis is 'horizontal'.
     *
     * @param g2  the graphics device.
     * @param cursor  the cursor location.
     * @param plotArea  the plot area.
     * @param dataArea  the area inside the axes.
     * @param edge  the axis location.
     * 
     * @return The new cursor location.
     */
    protected double drawHorizontalCategoryLabels(Graphics2D g2, double cursor,
                                                  Rectangle2D plotArea,
                                                  Rectangle2D dataArea,
                                                  RectangleEdge edge) {

        if (isTickLabelsVisible()) {
            Font tickLabelFont = getTickLabelFont();
            g2.setFont(tickLabelFont);
            g2.setPaint(getTickLabelPaint());
            refreshTicks(g2, cursor, plotArea, dataArea, edge);
            Insets tickLabelInsets = getTickLabelInsets();            
            double tickLabelHeight = tickLabelInsets.top + tickLabelInsets.bottom;
            tickLabelHeight += getMaxTickLabelHeight(g2, plotArea, isVerticalCategoryLabels());
            this.reservedForTickLabels = tickLabelHeight;
            
            Iterator iterator = getTicks().iterator();
            while (iterator.hasNext()) {
                Object obj = iterator.next();
                if (obj instanceof Tick) {
                    Tick tick = (Tick) obj;
                    if (isVerticalCategoryLabels()) {
                        RefineryUtilities.drawRotatedString(tick.getText(), g2,
                                                            tick.getX(), tick.getY(),
                                                            -Math.PI / 2);
                    }
                    else {
                        g2.drawString(tick.getText(), tick.getX(), tick.getY());
                    }
                }
                else {
                    Tick[] ts = (Tick[]) obj;
                    for (int i = 0; i < ts.length; i++) {
                        g2.drawString(ts[i].getText(), ts[i].getX(), ts[i].getY());
                    }
                }
            }
        }
        return this.reservedForTickLabels;

    }

    /**
     * Draws the category labels when the axis is 'vertical'.
     *
     * @param g2  the graphics device.
     * @param cursor  the cursor location.
     * @param plotArea  the plot area.
     * @param dataArea  the area inside the axes.
     * @param edge  the axis location.
     * 
     * @return The new cursor location.
     */
    protected double drawVerticalCategoryLabels(Graphics2D g2, double cursor,
                                                Rectangle2D plotArea,
                                                Rectangle2D dataArea,
                                                RectangleEdge edge) {

        if (isTickLabelsVisible()) {
            g2.setFont(getTickLabelFont());
            g2.setPaint(getTickLabelPaint());
            refreshTicks(g2, cursor, plotArea, dataArea, edge);
            Iterator iterator = getTicks().iterator();
            while (iterator.hasNext()) {
                Tick tick = (Tick) iterator.next();
                g2.drawString(tick.getText(), tick.getX(), tick.getY());
            }
        }
        return this.reservedForTickLabels;

    }

    /**
     * Returns <code>true</code> if the specified plot is compatible with the axis, and 
     * <code>false</code> otherwise.
     *
     * @param plot  the plot.
     *
     * @return A boolean.
     */
    protected boolean isCompatiblePlot(Plot plot) {

        return (plot instanceof CategoryPlot);

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
                boolean b4 = (this.skipCategoryLabelsToFit == axis.skipCategoryLabelsToFit);
                
                return b0 && b1 && b2 && b3 && b4;
            }
        }

        return false;

    }

    /**
     * Creates a temporary list of ticks that can be used when drawing the axis.
     *
     * @param g2  the graphics device (used to get font measurements).
     * @param cursor  the cursor location.
     * @param plotArea  the area where the plot and axes will be drawn.
     * @param dataArea  the area inside the axes.
     * @param edge  the location of the axis.
     */
    public void refreshTicks(Graphics2D g2, double cursor,
                             Rectangle2D plotArea, Rectangle2D dataArea,
                             RectangleEdge edge) {

        if (edge == RectangleEdge.TOP || edge == RectangleEdge.BOTTOM) {
            refreshTicksHorizontal(g2, cursor, plotArea, dataArea, edge);
        }
        else if (edge == RectangleEdge.LEFT || edge == RectangleEdge.RIGHT) {
            refreshTicksVertical(g2, cursor, plotArea, dataArea, edge);
        }
    }

    /**
     * Creates a temporary list of ticks that can be used when drawing the axis.
     *
     * @param g2  the graphics device (used to get font measurements).
     * @param cursor  the cursor location.
     * @param drawArea  the area where the plot and axes will be drawn.
     * @param plotArea  the area inside the axes.
     * @param edge  the location of the axis.
     */
    public void refreshTicksVertical(Graphics2D g2, double cursor,
                                     Rectangle2D drawArea, Rectangle2D plotArea,
                                     RectangleEdge edge) {

        getTicks().clear();
        CategoryPlot plot = (CategoryPlot) getPlot();
        List categories = plot.getCategories();
        if (categories != null) {
            Font font = getTickLabelFont();
            g2.setFont(font);
            FontRenderContext frc = g2.getFontRenderContext();
            int categoryIndex = 0;
            float xx = 0.0f;
            float yy = 0.0f;
            Iterator iterator = categories.iterator();
            while (iterator.hasNext()) {
                Object category = iterator.next();
                String label = category.toString();
                Rectangle2D labelBounds = font.getStringBounds(label, frc);
                LineMetrics metrics = font.getLineMetrics(label, frc);

                if (edge == RectangleEdge.LEFT) {
                    xx = (float) (cursor - getTickLabelInsets().right - labelBounds.getWidth());
                }
                else {
                    xx = (float) (cursor + getTickLabelInsets().left);
                }
                yy = (float) (getCategoryMiddle(categoryIndex,
                                                categories.size(),
                                                plotArea, edge)
                                                - metrics.getStrikethroughOffset() + 0.5f);
                Tick tick = new Tick(category, label, xx, yy);
                getTicks().add(tick);
                categoryIndex = categoryIndex + 1;
            }
        }
    }

    /**
     * Creates a temporary list of ticks that can be used when drawing the axis.
     *
     * @param g2  the graphics device (used to get font measurements).
     * @param cursor  the cursor location.
     * @param plotArea  the area where the plot and axes will be drawn.
     * @param dataArea  the area inside the axes.
     * @param edge  the location of the axis.
     */
    public void refreshTicksHorizontal(Graphics2D g2, double cursor,
                                       Rectangle2D plotArea, Rectangle2D dataArea,
                                       RectangleEdge edge) {

        this.maxTickLineCount = 1;
        getTicks().clear();
        CategoryPlot categoryPlot = (CategoryPlot) getPlot();
        List categories = categoryPlot.getCategories();
        if (categories != null) {
            Font font = getTickLabelFont();
            g2.setFont(font);
            FontRenderContext frc = g2.getFontRenderContext();
            int categorySkip = 0;
            int categoryIndex = 0;
            float maxWidth = (float) (dataArea.getWidth() / categories.size() * 0.9f);
            float xx = 0.0f;
            float yy = 0.0f;
            Iterator iterator = categories.iterator();
            while (iterator.hasNext()) {
                Object category = iterator.next();

                if (categorySkip != 0) {
                    ++categoryIndex;
                    --categorySkip;
                    continue;
                }

                String label = category.toString();
                Rectangle2D labelBounds = font.getStringBounds(label, frc);
                LineMetrics metrics = font.getLineMetrics(label, frc);
                float catX = (float) getCategoryMiddle(categoryIndex,
                                                       categories.size(),
                                                       dataArea, edge);
                if (isVerticalCategoryLabels()) {
                    xx = (float) (catX + labelBounds.getHeight() / 2 - metrics.getDescent());
                    if (edge == RectangleEdge.TOP) {
                        yy = (float) (cursor - getTickLabelInsets().bottom);
                                                        // - labelBounds.getWidth());
                    }
                    else {
                        yy = (float) (cursor + getTickLabelInsets().top + labelBounds.getWidth());
                    }
                    getTicks().add(new Tick(category, label, xx, yy));
                    if (this.skipCategoryLabelsToFit) {
                        categorySkip = (int) ((labelBounds.getHeight() - maxWidth / 2)
                                             / maxWidth) + 1;
                    }
                }
                else if (labelBounds.getWidth() > maxWidth) {
                    if (this.skipCategoryLabelsToFit) {
                        xx = (float) (catX - maxWidth / 2);
                        if (edge == RectangleEdge.TOP) {
                            yy = (float) (dataArea.getMinY() - getTickLabelInsets().bottom
                                                             - metrics.getDescent()
                                                             - metrics.getLeading());
                        }
                        else {
                            yy = (float) (dataArea.getMaxY() + getTickLabelInsets().top
                                                             + metrics.getHeight()
                                                             - metrics.getDescent());
                        }
                        getTicks().add(new Tick(category, label, xx, yy));
                        categorySkip = (int) ((labelBounds.getWidth() - maxWidth / 2)
                                             / maxWidth) + 1;
                    }
                    else {
                        String[] labels = breakLine(label, (int) maxWidth, frc);
                        Tick[] ts = new Tick[labels.length];
                        for (int i = 0; i < labels.length; i++) {
                            labelBounds = font.getStringBounds(labels[i], frc);
                            xx = (float) (catX - labelBounds.getWidth() / 2);
                            if (edge == RectangleEdge.TOP) {
                                yy = (float) (dataArea.getMinY() - getTickLabelInsets().bottom
                                              - (labels.length - i) * metrics.getHeight()
                                              + metrics.getAscent());
                            }
                            else {
                                yy = (float) (dataArea.getMaxY() + getTickLabelInsets().top
                                                                 + (i + 1) * (metrics.getHeight())
                                                                 - metrics.getDescent());
                            }
                            ts[i] = new Tick(category, labels[i], xx, yy);
                        }
                        if (labels.length > this.maxTickLineCount) {
                            this.maxTickLineCount = labels.length;
                        }
                        getTicks().add(ts);
                    }
                }
                else {
                    xx = (float) (catX - labelBounds.getWidth() / 2);
                    if (edge == RectangleEdge.TOP) {
                        yy = (float) (dataArea.getMinY() - getTickLabelInsets().bottom
                                                         - metrics.getLeading()
                                                         - metrics.getDescent());
                    }
                    else {
                        yy = (float) (dataArea.getMaxY() + getTickLabelInsets().top
                                                         + metrics.getHeight()
                                                         - metrics.getDescent());
                    }
                    getTicks().add(new Tick(category, label, xx, yy));
                }
                categoryIndex = categoryIndex + 1;
            }
        }

    }

    /**
     * A utility method for determining the height of the tallest tick label.
     *
     * @param g2  the graphics device.
     * @param drawArea  the drawing area.
     * @param vertical  a flag indicating whether the tick labels are drawn vertically.
     *
     * @return The maximum tick label height.
     */
    protected double getMaxTickLabelHeight(Graphics2D g2, Rectangle2D drawArea, boolean vertical) {
        Font font = getTickLabelFont();
        g2.setFont(font);
        FontRenderContext frc = g2.getFontRenderContext();
        double maxHeight = 0.0;
        if (vertical) {
            Iterator iterator = getTicks().iterator();
            while (iterator.hasNext()) {
                Tick tick = (Tick) iterator.next();
                Rectangle2D labelBounds = font.getStringBounds(tick.getText(), frc);
                if (labelBounds.getWidth() > maxHeight) {
                    maxHeight = labelBounds.getWidth();
                }
            }
        }
        else {
            LineMetrics metrics = font.getLineMetrics("Sample", frc);
            maxHeight = (metrics.getHeight() * this.maxTickLineCount);
//                        - (metrics.getDescent() * (this.maxTickLineCount - 1));
        }
        return maxHeight;
    }

    /**
     * Returns the maximum width of the ticks in the working list (that is set
     * up by refreshTicks()).
     *
     * @param g2  the graphics device.
     * @param plotArea  the area within which the plot is to be drawn.
     *
     * @return the maximum width of the ticks in the working list.
     */
    protected double getMaxTickLabelWidth(Graphics2D g2, Rectangle2D plotArea) {

        double maxWidth = 0.0;
        Font font = getTickLabelFont();
        FontRenderContext frc = g2.getFontRenderContext();

        Iterator iterator = getTicks().iterator();
        while (iterator.hasNext()) {
            Object obj = iterator.next();
            if (obj instanceof Tick) {
                Tick tick = (Tick) obj;
                Rectangle2D labelBounds = font.getStringBounds(tick.getText(), frc);
                if (labelBounds.getWidth() > maxWidth) {
                    maxWidth = labelBounds.getWidth();
                }
            }
            else {
                Tick[] ts = (Tick[]) obj;
                for (int i = 0; i < ts.length; i++) {
                    Rectangle2D labelBounds = font.getStringBounds(ts[i].getText(), frc);
                    if (labelBounds.getWidth() > maxWidth) {
                        maxWidth = labelBounds.getWidth();
                    }
                }

            }
        }
        return maxWidth;

    }

    /**
     * Breaks a line
     *
     * @param text  string at break
     * @param areaWidth  width of tick area
     * @param frc  current Font Renderer Context
     *
     * @return array of breaked strings
     */
    private String[] breakLine(String text, int areaWidth, FontRenderContext frc) {

        ArrayList textList = new ArrayList(5);

        int currWidth = areaWidth;
        AttributedString as = new AttributedString(text, getTickLabelFont().getAttributes());
        AttributedCharacterIterator aci = as.getIterator();
        AffineTransform affine = new AffineTransform();
        for (;;) {
            LineBreakMeasurer measurer = new LineBreakMeasurer(aci, frc);
            int maxWidth = 0, offset = 0;
            TextLayout layout = measurer.nextLayout(currWidth);
            while (layout != null) {
                textList.add(text.substring(offset, offset + layout.getCharacterCount()));
                int width = layout.getOutline(affine).getBounds().width;
                if (maxWidth < width) {
                    maxWidth = width;
                }
                offset += layout.getCharacterCount();
                layout = measurer.nextLayout(currWidth);
            }
            if (maxWidth > areaWidth) {
                currWidth -= maxWidth - currWidth;
                if (currWidth > 0) {
                    textList.clear();
                    continue;
                }
            }
            break;
        }

        String[] texts = new String[textList.size()];
        return (String[]) textList.toArray(texts);

    }

}
