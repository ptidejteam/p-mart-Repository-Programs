/* ======================================
 * JFreeChart : a free Java chart library
 * ======================================
 *
 * Project Info:  http://www.jfree.org/jfreechart/index.html
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
 * HorizontalCategoryAxis.java
 * ---------------------------
 * (C) Copyright 2000-2003, by Simba Management Limited.
 *
 * Original Author:  David Gilbert (for Simba Management Limited);
 * Contributor(s):   Jean-Luc SCHWAB;
 *                   Jon Iles;
 *                   Rich Unger;
 *
 * $Id: HorizontalCategoryAxis.java,v 1.1 2007/10/10 20:03:22 vauchers Exp $
 *
 * Changes (from 23-Jun-2001)
 * --------------------------
 * 23-Jun-2001 : Modified to work with null data source (DG);
 * 18-Sep-2001 : Updated header and fixed DOS encoding problem (DG);
 * 16-Oct-2001 : Moved data source classes to com.jrefinery.data.* (DG);
 * 22-Oct-2001 : Renamed DataSource.java --> Dataset.java etc. (DG);
 * 07-Nov-2001 : Updated configure() method (DG);
 * 23-Jan-2002 : Fixed bugs causing null pointer exceptions when axis label is null (DG);
 * 20-Feb-2002 : Adjusted x-coordinate for vertical category labels (DG);
 * 06-Mar-2002 : Updated import statements (DG);
 * 19-Apr-2002 : Added facility to set axis visibility on or off.  Also drawVerticalString(...) is
 *               now drawRotatedString(...) in RefineryUtilities (DG);
 * 30-Apr-2002 : Category labels now wrap to multiple lines if necessary, thanks to
 *               Jean-Luc SCHWAB (DG);
 * 12-Jul-2002 : Added code to (optionally) hide some category labels to avoid overlapping.
 *               Submitted by Jon Iles (DG)
 * 05-Sep-2002 : Updated constructor for changes in Axis class (DG);
 * 25-Sep-2002 : Fixed vertical category labels to observe skipping, as suggested by Rich
 *               Unger, and fixed errors reported by Checkstyle (DG);
 * 04-Oct-2002 : Added setVerticalTickLabels(boolean) method (DG);
 * 06-Nov-2002 : Added margins to the CategoryAxis class (DG);
 * 08-Nov-2002 : Moved to new package com.jrefinery.chart.axis (DG);
 * 19-Nov-2002 : Amended for drawing at the top or the bottom of the plot (DG);
 * 17-Jan-2003 : Moved plot classes to separate package (DG);
 * 22-Jan-2003 : Removed monolithic constructor (DG);
 * 26-Mar-2003 : Implemented Serializable (DG);
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

import org.jfree.chart.event.AxisChangeEvent;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.plot.VerticalCategoryPlot;
import org.jfree.data.CategoryDataset;
import org.jfree.ui.RefineryUtilities;

/**
 * A horizontal axis that displays categories.  Used for bar charts and line charts.
 * <P>
 * Note: the axis needs to rely on the plot for assistance with the placement
 * of category labels, since the plot controls how the categories are distributed.
 *
 * @author David Gilbert
 */
public class HorizontalCategoryAxis extends CategoryAxis 
                                    implements HorizontalAxis, Serializable {

    /** The default setting for vertical category labels. */
    public static final boolean DEFAULT_VERTICAL_CATEGORY_LABELS = false;
    
    /** A flag that indicates whether the category labels should be drawn vertically. */
    private boolean verticalCategoryLabels;

    /** A flag that controls whether to skip category labels to avoid overlapping. */
    private boolean skipCategoryLabelsToFit;

    /** Tick height */
    private int tickHeight;

    /**
     * Creates a new axis using default attribute values.
     *
     * @param label  the axis label (<code>null</code> permitted).
     */
    public HorizontalCategoryAxis(String label) {

        super(label);
        this.verticalCategoryLabels = DEFAULT_VERTICAL_CATEGORY_LABELS;

    }

    /**
     * Returns a flag indicating whether the category labels are drawn 'vertically'.
     *
     * @return the flag.
     */
    public boolean getVerticalCategoryLabels() {
        return this.verticalCategoryLabels;
    }

    /**
     * Sets the flag that determines whether the category labels are drawn 'vertically'.
     *
     * @param flag  the new value of the flag.
     */
    public void setVerticalCategoryLabels(boolean flag) {

        if (this.verticalCategoryLabels != flag) {
            this.verticalCategoryLabels = flag;
            notifyListeners(new AxisChangeEvent(this));
        }

    }

    /**
     * Sets the flag that determines whether the category labels are drawn 'vertically'.
     * <P>
     * You should use the setVerticalCategoryLabels method - this method just passed over to
     * it anyway.
     *
     * @param flag  the new value of the flag.
     */
    public void setVerticalTickLabels(boolean flag) {
        setVerticalCategoryLabels(flag);
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
     * Returns the starting coordinate for the specified category.
     *
     * @param category  the category.
     * @param categoryCount  the number of categories.
     * @param area  the data area.
     *
     * @return the coordinate.
     */
    public double getCategoryStart(int category, int categoryCount, Rectangle2D area) {

        double result = area.getX() + area.getWidth() * getLowerMargin();

        double categoryWidth = calculateCategoryWidth(categoryCount, area);
        double categoryGapWidth = calculateCategoryGapWidth(categoryCount, area);

        result = result + category * (categoryWidth + categoryGapWidth);

        return result;
    }

    /**
     * Returns the middle coordinate for the specified category.
     *
     * @param category  the category.
     * @param categoryCount  the number of categories.
     * @param area  the data area.
     *
     * @return the coordinate.
     */
    public double getCategoryMiddle(int category, int categoryCount, Rectangle2D area) {

        return getCategoryStart(category, categoryCount, area)
               + calculateCategoryWidth(categoryCount, area) / 2;

    }

    /**
     * Returns the end coordinate for the specified category.
     *
     * @param category  the category.
     * @param categoryCount  the number of categories.
     * @param area  the data area.
     *
     * @return the coordinate.
     */
    public double getCategoryEnd(int category, int categoryCount, Rectangle2D area) {
        return getCategoryStart(category, categoryCount, area)
               + calculateCategoryWidth(categoryCount, area);
    }

    /**
     * Draws the axis on a Java 2D graphics device (such as the screen or a printer).
     *
     * @param g2  the graphics device.
     * @param plotArea  the area within which the axis should be drawn.
     * @param dataArea  the area within which the plot is being drawn.
     * @param location  the location of the axis (TOP or BOTTOM).
     */
    public void draw(Graphics2D g2, Rectangle2D plotArea, Rectangle2D dataArea, int location) {

        // if the axis is not visible, don't draw it...
        if (!isVisible()) {
            return;
        }

        // use a cursor to track the vertical level of the items that need drawing...
        double cursorY = 0.0;
        if (location == TOP) {
            cursorY = plotArea.getMinY();
        }
        else {
            cursorY = plotArea.getMaxY();
        }

        // draw the axis label...
        cursorY = drawHorizontalLabel(getLabel(), g2, plotArea, dataArea, location, cursorY);

        // draw the category labels
        if (isTickLabelsVisible()) {
            Font tickLabelFont = getTickLabelFont();
            g2.setFont(tickLabelFont);
            g2.setPaint(getTickLabelPaint());
            refreshTicks(g2, plotArea, dataArea, location);
            Iterator iterator = getTicks().iterator();
            while (iterator.hasNext()) {
                Object obj = iterator.next();
                if (obj instanceof Tick) {
                    Tick tick = (Tick) obj;
                    if (this.verticalCategoryLabels) {
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

    }

    /**
     * Creates a temporary list of ticks that can be used when drawing the axis.
     *
     * @param g2  the graphics device (used to get font measurements).
     * @param plotArea  the area where the plot and axes will be drawn.
     * @param dataArea  the area inside the axes.
     * @param location  the location of the axis.
     */
    public void refreshTicks(Graphics2D g2,
                             Rectangle2D plotArea, Rectangle2D dataArea,
                             int location) {

        this.tickHeight = 1;
        getTicks().clear();
        CategoryPlot categoryPlot = (CategoryPlot) getPlot();
        CategoryDataset data = categoryPlot.getCategoryDataset();
        if (data != null) {
            FontRenderContext frc = g2.getFontRenderContext();
            Font font = getTickLabelFont();
            g2.setFont(font);
            int categorySkip = 0;
            int categoryIndex = 0;
            float maxWidth = (float) ((dataArea.getWidth() + dataArea.getX())
                                       / data.getColumnCount()) * 0.9f;
            float xx = 0.0f;
            float yy = 0.0f;
            Iterator iterator = data.getColumnKeys().iterator();
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
                                                       data.getColumnCount(),
                                                       dataArea);
                if (this.verticalCategoryLabels) {
                    xx = (float) (catX + labelBounds.getHeight() / 2 - metrics.getDescent());
                    if (location == TOP) {
                        yy = (float) (dataArea.getMinY() - getTickLabelInsets().bottom
                                                         - labelBounds.getWidth());
                    }
                    else {
                        yy = (float) (dataArea.getMaxY() + getTickLabelInsets().top
                                                         + labelBounds.getWidth());
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
                        if (location == TOP) {
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
                            if (location == TOP) {
                                yy = (float) (dataArea.getMinY() - getTickLabelInsets().bottom
                                                                 - metrics.getDescent()
                                                                 - metrics.getLeading());
                            }
                            else {
                                yy = (float) (dataArea.getMaxY() + getTickLabelInsets().top
                                                                 + (i + 1) * (metrics.getHeight()
                                                                 - metrics.getDescent()));
                            }
                            ts[i] = new Tick(category, labels[i], xx, yy);
                        }
                        if (labels.length > tickHeight) {
                            tickHeight = labels.length;
                        }
                        getTicks().add(ts);
                    }
                }
                else {
                    xx = (float) (catX - labelBounds.getWidth() / 2);
                    if (location == TOP) {
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
     * Estimates the height required for the axis, given a specific drawing
     * area, without any information about the width of the vertical axis.
     * <P>
     * Supports the HorizontalAxis interface.
     *
     * @param g2  the graphics device (used to obtain font information).
     * @param plot  the plot that the axis belongs to.
     * @param drawArea  the area within which the axis should be drawn.
     * @param location  the axis location (top or bottom).
     *
     * @return the estimated height required for the axis.
     */
    public double reserveHeight(Graphics2D g2, Plot plot, Rectangle2D drawArea, int location) {

        if (!isVisible()) {
            return 0.0;
        }

        // calculate the height of the axis label...
        double labelHeight = 0.0;
        String label = getLabel();
        if (label != null) {
            Rectangle2D labelBounds
                = getLabelFont().getStringBounds(label, g2.getFontRenderContext());
            Insets labelInsets = getLabelInsets();
            labelHeight = labelInsets.top + labelInsets.bottom + labelBounds.getHeight();
        }

        // calculate the height required for the tick labels (if visible);
        double tickLabelHeight = 0.0;
        if (isTickLabelsVisible()) {
            g2.setFont(getTickLabelFont());
            refreshTicks(g2, drawArea, drawArea, location);
            Insets tickLabelInsets = getTickLabelInsets();
            tickLabelHeight = tickLabelInsets.top + tickLabelInsets.bottom
                              + getMaxTickLabelHeight(g2, drawArea, this.verticalCategoryLabels);
        }
        return labelHeight + tickLabelHeight;

    }

    /**
     * Returns the area required to draw the axis in the specified draw area.
     *
     * @param g2  the graphics device.
     * @param plot  the plot that the axis belongs to.
     * @param drawArea  the area within which the plot should be drawn.
     * @param location  the location of the axis.
     * @param reservedWidth  the width reserved by the vertical axis.
     * @param verticalAxisLocation  the location of the vertical axis.
     *
     * @return the area required to draw the axis in the specified draw area.
     */
    public double reserveHeight(Graphics2D g2, Plot plot, Rectangle2D drawArea, int location,
                                double reservedWidth, int verticalAxisLocation) {

        if (!isVisible()) {
            return 0.0;
        }

        // calculate the height of the axis label...
        double labelHeight = 0.0;
        String label = getLabel();
        if (label != null) {
            Rectangle2D labelBounds
                = getLabelFont().getStringBounds(label, g2.getFontRenderContext());
            Insets labelInsets = getLabelInsets();
            labelHeight = labelInsets.top + labelInsets.bottom + labelBounds.getHeight();
        }

        // calculate the height required for the tick labels (if visible);
        double tickLabelHeight = 0.0;
        if (isTickLabelsVisible()) {
            g2.setFont(getTickLabelFont());
            refreshTicks(g2, drawArea, drawArea, location);
            Insets tickLabelInsets = getTickLabelInsets();
            tickLabelHeight = tickLabelInsets.top + tickLabelInsets.bottom
                              + getMaxTickLabelHeight(g2, drawArea, this.verticalCategoryLabels);
        }
        return labelHeight + tickLabelHeight;

    }

    /**
     * A utility method for determining the height of the tallest tick label.
     *
     * @param g2  the graphics device.
     * @param drawArea  the drawing area.
     * @param vertical  a flag indicating whether the tick labels are drawn vertically.
     *
     * @return the maximum tick label height.
     */
    private double getMaxTickLabelHeight(Graphics2D g2, Rectangle2D drawArea, boolean vertical) {
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
            maxHeight = (metrics.getHeight() * tickHeight)
                        - (metrics.getDescent() * (tickHeight - 1));
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
     * Returns true if the specified plot is compatible with the axis.
     *
     * @param plot The plot.
     *
     * @return <code>true</code> if the specified plot is compatible with the axis.
     */
    protected boolean isCompatiblePlot(Plot plot) {

        if (plot instanceof VerticalCategoryPlot) {
            return true;
        }
        else {
            return false;
        }

    }

    /**
     * Configures the axis against the current plot.  Nothing required in this class.
     */
    public void configure() {
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
        for (; ;) {
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

    /**
     * Calculates the width of a category.
     *
     * @param categoryCount  the number of categories.
     * @param area  the area within which the categories will be drawn.
     *
     * @return the category width.
     */
    private double calculateCategoryWidth(int categoryCount, Rectangle2D area) {

        double result = 0.0;

        if (categoryCount > 1) {
            result = area.getWidth()
                     * (1 - getLowerMargin() - getUpperMargin() - getCategoryMargin());
            result = result / categoryCount;
        }
        else {
            result = area.getWidth() * (1 - getLowerMargin() - getUpperMargin());
        }
        return result;

    }

    /**
     * Calculates the width of a category gap.
     *
     * @param categoryCount  the number of categories.
     * @param area  the area within which the categories will be drawn.
     *
     * @return the category gap width.
     */
    private double calculateCategoryGapWidth(int categoryCount, Rectangle2D area) {

        double result = 0.0;
        if (categoryCount > 1) {
            result = area.getWidth() * getCategoryMargin() / (categoryCount - 1);
        }
        return result;

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
 
        if (obj instanceof HorizontalCategoryAxis) {
            HorizontalCategoryAxis hca = (HorizontalCategoryAxis) obj;
            if (super.equals(obj)) {
                boolean b0 = (this.verticalCategoryLabels == hca.verticalCategoryLabels);
                boolean b1 = (this.skipCategoryLabelsToFit == hca.skipCategoryLabelsToFit);
                return b0 && b1;
            }
        }
        
        return false;
               
    }
    
}
