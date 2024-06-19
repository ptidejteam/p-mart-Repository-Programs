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
 * HorizontalNumberAxis3D.java
 * ---------------------------
 * (C) Copyright 2002, 2003, by Tin Luu and Contributors.
 *
 * Original Author:  Tin Luu;
 * Contributor(s):   David Gilbert (for Simba Management Limited);
 *
 * $Id: HorizontalNumberAxis3D.java,v 1.1 2007/10/10 19:54:25 vauchers Exp $
 *
 * Changes
 * -------
 * 15-May-2002 : Version 1, contributed by Tin Luu, based on VerticalNumberAxis3D (DG);
 * 18-Jun-2002 : Removed unnecessary verticalTickLabels definition (bug ID 570436) (DG);
 * 25-Jun-2002 : Removed redundant import (DG);
 * 25-Jul-2002 : Changed order of parameters in ValueAxis constructor (DG);
 * 05-Sep-2002 : Updated constructor to reflect changes in the Axis class, and changed draw
 *               method to observe tickMarkPaint (DG);
 * 18-Sep-2002 : Fixed errors reported by Checkstyle (DG);
 * 05-Nov-2002 : Removed the effect3D attribute, this class now refers to the plot's renderer
 *               for the current setting (DG);
 * 08-Nov-2002 : Moved to new package com.jrefinery.chart.axis (DG);
 * 17-Jan-2003 : Moved plot classes to separate package (DG);
 * 20-Jan-2003 : Removed unnecessary constructors (DG);
 *
 */

package com.jrefinery.chart.axis;

import java.awt.Graphics2D;
import java.awt.Font;
import java.awt.Insets;
import java.awt.font.FontRenderContext;
import java.awt.font.LineMetrics;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Line2D;
import java.util.Iterator;
import com.jrefinery.chart.Effect3D;
import com.jrefinery.chart.plot.Plot;
import com.jrefinery.chart.plot.CategoryPlot;
import com.jrefinery.chart.plot.HorizontalValuePlot;
import com.jrefinery.chart.renderer.CategoryItemRenderer;
import com.jrefinery.ui.RefineryUtilities;

/**
 * A horizontal axis that displays numerical values, and has a 3D-effect.
 *
 * @author Tin Luu
 */
public class HorizontalNumberAxis3D extends HorizontalNumberAxis {

    /**
     * Constructs a horizontal number axis, using default attribute values where necessary.
     *
     * @param label The axis label (null permitted).
     */
    public HorizontalNumberAxis3D(String label) {

        super(label);

    }

    /**
     * Draws the axis on a Java 2D graphics device (such as the screen or a printer).
     *
     * @param g2  the graphics device.
     * @param plotArea  the area for drawing the axes and data.
     * @param dataArea  the area for drawing the data (a subset of the plotArea).
     * @param location  the axis location.
     */
    public void draw(Graphics2D g2, Rectangle2D plotArea, Rectangle2D dataArea, int location) {

        // draw the axis label...
        String label = getLabel();
        if (label != null) {
            Font labelFont = getLabelFont();
            g2.setFont(labelFont);
            g2.setPaint(getLabelPaint());
            FontRenderContext frc = g2.getFontRenderContext();
            Rectangle2D labelBounds = labelFont.getStringBounds(label, frc);
            LineMetrics lm = labelFont.getLineMetrics(label, frc);
            float labelx = (float) (dataArea.getX() + plotArea.getWidth() / 2
                                                    - labelBounds.getWidth() / 2);
            float labely = (float) (plotArea.getMaxY() - getLabelInsets().bottom
                                                       - lm.getDescent()
                                                       - lm.getLeading());
            g2.drawString(label, labelx, labely);
        }

        // draw the tick labels and marks
        // calculate the adjusted data area taking into account the 3D effect...
        CategoryPlot plot = (CategoryPlot) getPlot();
        Effect3D e3D = (Effect3D) plot.getRenderer();
        Rectangle2D adjustedDataArea = new Rectangle2D.Double(dataArea.getMinX(),
            dataArea.getMinY() + e3D.getYOffset(), dataArea.getWidth() - e3D.getXOffset(),
            dataArea.getHeight() - e3D.getYOffset());
        refreshTicks(g2, plotArea, adjustedDataArea, location);

        float maxY = (float) plotArea.getMaxY();
        g2.setFont(getTickLabelFont());

        Iterator iterator = getTicks().iterator();
        while (iterator.hasNext()) {
            Tick tick = (Tick) iterator.next();
            float xx = (float) translateValueToJava2D(tick.getNumericalValue(), plotArea);
            if (isTickLabelsVisible()) {
                g2.setPaint(getTickLabelPaint());
                if (isVerticalTickLabels()) {
                    RefineryUtilities.drawRotatedString(tick.getText(), g2,
                                                        tick.getX(), tick.getY(), -Math.PI / 2);
                }
                else {
                    g2.drawString(tick.getText(), tick.getX(), tick.getY());
                }
            }

            if (isTickMarksVisible()) {
                g2.setStroke(getTickMarkStroke());
                g2.setPaint(getTickMarkPaint());
                Line2D mark = new Line2D.Float(xx, maxY - getTickMarkInsideLength(),
                                               xx, maxY + getTickMarkOutsideLength());
                g2.draw(mark);
            }

        }

    }

    /**
     * Returns the height required to draw the axis in the specified draw area.
     *
     * @param g2  the graphics device.
     * @param plot  the plot that the axis belongs to.
     * @param drawArea  the area within which the plot should be drawn.
     * @param location  the axis location (top or bottom).
     *
     * @return the height required to draw the axis in the specified draw area.
     */
    public double reserveHeight(Graphics2D g2, Plot plot, Rectangle2D drawArea, int location) {

        // calculate the height of the axis label...
        double labelHeight = 0.0;
        String label = getLabel();
        if (label != null) {
            LineMetrics metrics = getLabelFont().getLineMetrics(label, g2.getFontRenderContext());
            Insets labelInsets = getLabelInsets();
            labelHeight = labelInsets.top + metrics.getHeight() + labelInsets.bottom;
        }

        // calculate the height required for the tick labels (if visible)
        Insets tickLabelInsets = getTickLabelInsets();
        double tickLabelHeight = tickLabelInsets.top + tickLabelInsets.bottom;
        if (isTickLabelsVisible()) {
            g2.setFont(getTickLabelFont());
            refreshTicks(g2, drawArea, drawArea, location);
            tickLabelHeight = tickLabelHeight
                + getMaxTickLabelHeight(g2, drawArea, isVerticalTickLabels());
        }
        return labelHeight + tickLabelHeight;

    }

    /**
     * Returns area in which the axis will be displayed.
     *
     * @param g2  the graphics device.
     * @param plot  a reference to the plot.
     * @param drawArea  the area within which the plot and axes should be drawn.
     * @param location  the axis location.
     * @param reservedWidth  the space already reserved for the vertical axis.
     * @param verticalAxisLocation  the location of the vertical axis.
     *
     * @return the axis height.
     */
    public double reserveHeight(Graphics2D g2, Plot plot, Rectangle2D drawArea, int location,
                                double reservedWidth, int verticalAxisLocation) {

        // calculate the height of the axis label...
        double labelHeight = 0.0;
        String label = getLabel();
        if (label != null) {
            LineMetrics metrics = getLabelFont().getLineMetrics(label, g2.getFontRenderContext());
            Insets labelInsets = getLabelInsets();
            labelHeight = labelInsets.top + metrics.getHeight() + labelInsets.bottom;
        }

        // calculate the height required for the tick labels (if visible)
        Insets tickLabelInsets = getTickLabelInsets();
        double tickLabelHeight = tickLabelInsets.top + tickLabelInsets.bottom;
        if (isTickLabelsVisible()) {
            g2.setFont(getTickLabelFont());
            refreshTicks(g2, drawArea, drawArea, location);
            tickLabelHeight = tickLabelHeight
                + getMaxTickLabelHeight(g2, drawArea, isVerticalTickLabels());
        }
        return labelHeight + tickLabelHeight;

    }

    /**
     * A utility method for determining the height of the tallest tick label.
     *
     * @param g2  the graphics device.
     * @param drawArea  the area within which the plot and axes should be drawn.
     * @param vertical  a flag that indicates whether or not the tick labels are 'vertical'.
     *
     * @return the height of the tallest tick label.
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
            maxHeight = metrics.getHeight();
        }
        return maxHeight;
    }

    /**
     * Returns true if a plot is compatible with the axis, and false otherwise.
     * <P>
     * For this axis, the requirement is that the plot implements the
     * HorizontalValuePlot interface.
     *
     * @param plot  the plot.
     *
     * @return <code>true</code> if a plot is compatible with the axis.
     */
    protected boolean isCompatiblePlot(Plot plot) {
        if (plot instanceof HorizontalValuePlot) {
            return true;
        }
        else {
            return false;
        }
    }

    /**
     * Returns the x-offset for the 3D effect (obtained from the renderer).
     *
     * @return the offset.
     */
    private double get3DXOffset() {

        double result = 0.0;
        Plot plot = getPlot();
        if (plot != null) {
            if (plot instanceof CategoryPlot) {
                CategoryPlot cp = (CategoryPlot) plot;
                CategoryItemRenderer renderer = cp.getRenderer();
                if (renderer instanceof Effect3D) {
                    Effect3D r = (Effect3D) renderer;
                    result = r.getXOffset();
                }
            }
        }
        return result;

    }

    /**
     * Returns the y-offset for the 3D effect (obtained from the renderer).
     *
     * @return the offset.
     */
    private double get3DYOffset() {

        double result = 0.0;
        Plot plot = getPlot();
        if (plot != null) {
            if (plot instanceof CategoryPlot) {
                CategoryPlot cp = (CategoryPlot) plot;
                CategoryItemRenderer renderer = cp.getRenderer();
                if (renderer instanceof Effect3D) {
                    Effect3D r = (Effect3D) renderer;
                    result = r.getYOffset();
                }
            }
        }
        return result;

    }

}
