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
 * -------------------------
 * VerticalCategoryAxis.java
 * -------------------------
 * (C) Copyright 2000-2003, by Simba Management Limited.
 *
 * Original Author:  David Gilbert (for Simba Management Limited);
 * Contributor(s):   Richard Atkinson;
 *
 * $Id: VerticalCategoryAxis.java,v 1.1 2007/10/10 19:54:25 vauchers Exp $
 *
 * Changes (from 23-Jun-2001)
 * --------------------------
 * 23-Jun-2001 : Modified to work with null data source (DG);
 * 18-Sep-2001 : Updated header and fixed DOS encoding problem (DG);
 * 16-Oct-2001 : Moved data source classes to com.jrefinery.data.* (DG);
 * 22-Oct-2001 : Renamed DataSource.java --> Dataset.java etc. (DG);
 * 23-Jan-2002 : Changed the positioning of category labels to improve centering on bars (DG);
 *               Fixed bugs causing exceptions when axis label is null (DG);
 * 06-Mar-2002 : Added accessor methods for verticalLabel attribute. Updated import statements (DG);
 * 19-Apr-2002 : Added facility to set axis visibility on or off.  Also drawVerticalString(...) is
 *               now drawRotatedString(...) in RefineryUtilities (DG);
 * 06-Aug-2002 : Modified draw method to not draw axis label if label is empty String (RA);
 * 05-Sep-2002 : Updated constructor reflecting changes in the Axis class (DG);
 * 01-Oct-2002 : Fixed errors reported by Checkstyle (DG);
 * 06-Nov-2002 : Added category axis margins (DG);
 * 08-Nov-2002 : Moved to new package com.jrefinery.chart.axis (DG);
 * 17-Jan-2003 : Moved plot classes to a separate package (DG);
 * 22-Jan-2003 : Removed monolithic constructor (DG);
 *
 */

package com.jrefinery.chart.axis;

import java.awt.Graphics2D;
import java.awt.Font;
import java.awt.Insets;
import java.awt.font.FontRenderContext;
import java.awt.font.LineMetrics;
import java.awt.geom.Rectangle2D;
import java.util.Iterator;
import com.jrefinery.chart.event.AxisChangeEvent;
import com.jrefinery.chart.plot.Plot;
import com.jrefinery.chart.plot.CategoryPlot;
import com.jrefinery.chart.plot.HorizontalCategoryPlot;
import com.jrefinery.data.CategoryDataset;

/**
 * A vertical axis that displays categories, used for horizontal bar charts.
 *
 * @author David Gilbert
 */
public class VerticalCategoryAxis extends CategoryAxis implements VerticalAxis {

    /** The default setting for vertical axis label. */
    public static final boolean DEFAULT_VERTICAL_LABEL = true;

    /** A flag that indicates whether or not the axis label should be drawn vertically. */
    private boolean verticalLabel;

    /**
     * Constructs a new axis, using default attributes where necessary.
     *
     * @param label  the axis label (<code>null</code> permitted).
     */
    public VerticalCategoryAxis(String label) {

        super(label);
        this.verticalLabel = DEFAULT_VERTICAL_LABEL;

    }

    /**
     * Returns a flag indicating whether or not the axis label is drawn vertically.
     *
     * @return The flag.
     */
    public boolean isVerticalLabel() {
        return this.verticalLabel;
    }

    /**
     * Sets a flag indicating whether or not the axis label is drawn vertically.
     * If the setting is changed, registered listeners are notified that the
     * axis has changed.
     *
     * @param flag  the flag.
     */
    public void setVerticalLabel(boolean flag) {

        if (this.verticalLabel != flag) {
            this.verticalLabel = flag;
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

        double result = area.getMinY() + area.getHeight() * getLowerMargin();

        double categoryHeight = calculateCategoryHeight(categoryCount, area);
        double categoryGapHeight = calculateCategoryGapHeight(categoryCount, area);

        result = result + category * (categoryHeight + categoryGapHeight);

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
               + calculateCategoryHeight(categoryCount, area) / 2;
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
               + calculateCategoryHeight(categoryCount, area);
    }

    /**
     * Draws the axis on a Java 2D graphics device (such as the screen or a printer).
     *
     * @param g2  the graphics device.
     * @param plotArea  the area within which the axis should be drawn.
     * @param dataArea  the area within which the plot is being drawn.
     * @param location  the location of the axis (LEFT or RIGHT).
     */
    public void draw(Graphics2D g2, Rectangle2D plotArea, Rectangle2D dataArea, int location) {

        // if the axis is not visible, don't draw it...
        if (!isVisible()) {
            return;
        }

        // draw the axis label...
        drawVerticalLabel(getLabel(), isVerticalLabel(), g2, plotArea, dataArea, location);

        // draw the category labels
        if (isTickLabelsVisible()) {
            g2.setFont(getTickLabelFont());
            g2.setPaint(getTickLabelPaint());
            refreshTicks(g2, plotArea, dataArea, location);
            Iterator iterator = getTicks().iterator();
            while (iterator.hasNext()) {
                Tick tick = (Tick) iterator.next();
                g2.drawString(tick.getText(), tick.getX(), tick.getY());
            }
        }

    }

    /**
     * Creates a temporary list of ticks that can be used when drawing the axis.
     *
     * @param g2  the graphics device (used to get font measurements).
     * @param drawArea  the area where the plot and axes will be drawn.
     * @param plotArea  the area inside the axes.
     * @param location  the location of the axis.
     */
    public void refreshTicks(Graphics2D g2,
                             Rectangle2D drawArea, Rectangle2D plotArea,
                             int location) {

        getTicks().clear();
        CategoryPlot plot = (CategoryPlot) getPlot();
        CategoryDataset data = plot.getCategoryDataset();
        if (data != null) {
            Font font = getTickLabelFont();
            g2.setFont(font);
            FontRenderContext frc = g2.getFontRenderContext();
            int categoryIndex = 0;
            float xx = 0.0f;
            float yy = 0.0f;
            Iterator iterator = data.getColumnKeys().iterator();
            while (iterator.hasNext()) {
                Object category = iterator.next();
                String label = category.toString();
                Rectangle2D labelBounds = font.getStringBounds(label, frc);
                LineMetrics metrics = font.getLineMetrics(label, frc);

                if (location == LEFT) {
                    xx = (float) (plotArea.getX() - getTickLabelInsets().right
                                                  - labelBounds.getWidth());
                }
                else {
                    xx = (float) (plotArea.getMaxX() + getTickLabelInsets().left);
                }
                yy = (float) (getCategoryMiddle(categoryIndex,
                                                data.getColumnCount(),
                                                plotArea)
                                                - metrics.getStrikethroughOffset() + 0.5f);
                Tick tick = new Tick(category, label, xx, yy);
                getTicks().add(tick);
                categoryIndex = categoryIndex + 1;
            }
        }
    }

    /**
     * Estimates the height required for the axis, given a specific drawing
     * area, without any information about the width of the vertical axis.
     * <P>
     * Supports the VerticalAxis interface.
     *
     * @param g2  the graphics device (used to obtain font information).
     * @param drawArea  the area within which the axis should be drawn.
     * @param plot  the plot that the axis belongs to.
     * @param location  the axis location.
     *
     * @return the estimated height required for the axis.
     */
    public double reserveWidth(Graphics2D g2, Plot plot, Rectangle2D drawArea, int location) {

        if (!isVisible()) {
            return 0.0;
        }

        // calculate the width of the axis label...
        double labelWidth = 0.0;
        String label = getLabel();
        if (label != null) {
            Rectangle2D labelBounds
                = getLabelFont().getStringBounds(label, g2.getFontRenderContext());
            Insets labelInsets = getLabelInsets();
            labelWidth = labelInsets.left + labelInsets.right;
            if (this.verticalLabel) {
                // assume width == height before rotation
                labelWidth = labelWidth + labelBounds.getHeight();
            }
            else {
                labelWidth = labelWidth + labelBounds.getWidth();
            }
        }

        // calculate the width required for the tick labels (if visible);
        Insets tickLabelInsets = getTickLabelInsets();
        double tickLabelWidth = tickLabelInsets.left + tickLabelInsets.right;
        if (isTickLabelsVisible()) {
            refreshTicks(g2, drawArea, drawArea, location);
            tickLabelWidth = tickLabelWidth + getMaxTickLabelWidth(g2, drawArea);
        }
        return labelWidth + tickLabelWidth;

    }

    /**
     * Returns the area required to draw the axis in the specified draw area.
     *
     * @param g2  the graphics device.
     * @param plot  the plot that the axis belongs to.
     * @param drawArea  the area within which the plot should be drawn.
     * @param location  the axis location.
     * @param reservedHeight  the height reserved by the horizontal axis.
     * @param horizontalAxisLocation  the horizontal axis location.
     *
     * @return  the area to reserve for the axis.
     */
    public double reserveWidth(Graphics2D g2, Plot plot, Rectangle2D drawArea, int location,
                               double reservedHeight, int horizontalAxisLocation) {

        if (!isVisible()) {
            return 0.0;
        }

        // calculate the width of the axis label...
        double labelWidth = 0.0;
        String label = getLabel();
        if (label != null) {
            Rectangle2D labelBounds
                = getLabelFont().getStringBounds(label, g2.getFontRenderContext());
            Insets labelInsets = getLabelInsets();
            labelWidth = labelInsets.left + labelInsets.right;
            if (this.verticalLabel) {
                // assume width == height before rotation
                labelWidth = labelWidth + labelBounds.getHeight();
            }
            else {
                labelWidth = labelWidth + labelBounds.getWidth();
            }
        }

        // calculate the width required for the tick labels (if visible);
        Insets tickLabelInsets = getTickLabelInsets();
        double tickLabelWidth = tickLabelInsets.left + tickLabelInsets.right;
        if (isTickLabelsVisible()) {
            refreshTicks(g2, drawArea, drawArea, location);
            tickLabelWidth = tickLabelWidth + getMaxTickLabelWidth(g2, drawArea);
        }

        return labelWidth + tickLabelWidth;

    }

    /**
     * Returns true if the specified plot is compatible with the axis, and false otherwise.
     *
     * @param plot  the plot.
     *
     * @return a boolean indicating whether or not the axis considers the plot is compatible.
     */
    protected boolean isCompatiblePlot(Plot plot) {

        if (plot instanceof HorizontalCategoryPlot) {
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
     * Calculates the height of a category.
     *
     * @param categoryCount  the number of categories.
     * @param area  the area in which the categories will be drawn.
     *
     * @return the category height.
     */
    private double calculateCategoryHeight(int categoryCount, Rectangle2D area) {

        double result = 0.0;

        if (categoryCount > 1) {
            result = area.getHeight()
                     * (1 - getLowerMargin() - getUpperMargin() - getCategoryMargin());
            result = result / categoryCount;
        }
        else {
            result = area.getHeight() * (1 - getLowerMargin() - getUpperMargin());
        }
        return result;

    }

    /**
     * Calculates the height of a category gap.
     *
     * @param categoryCount  the number of categories.
     * @param area  the area in which the categories will be drawn.
     *
     * @return the category gap height.
     */
    private double calculateCategoryGapHeight(int categoryCount, Rectangle2D area) {

        double result = 0.0;
        if (categoryCount > 1) {
            result = area.getHeight() * getCategoryMargin() / (categoryCount - 1);
        }
        return result;

    }


}
