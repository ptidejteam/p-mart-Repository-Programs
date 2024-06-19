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
 * ---------------------------
 * HorizontalNumberAxis3D.java
 * ---------------------------
 * (C) Copyright 2002, by Tin Luu and Contributors.
 *
 * Original Author:  Tin Luu;
 * Contributor(s):   David Gilbert (for Simba Management Limited);
 *
 * $Id: HorizontalNumberAxis3D.java,v 1.1 2007/10/10 19:02:26 vauchers Exp $
 *
 * Changes
 * -------
 * 15-May-2002 : Version 1, contributed by Tin Luu, based on VerticalNumberAxis3D (DG);
 *
 */

package com.jrefinery.chart;

import java.awt.Graphics2D;
import java.awt.Font;
import java.awt.Paint;
import java.awt.Insets;
import java.awt.Stroke;
import java.awt.font.FontRenderContext;
import java.awt.font.LineMetrics;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Line2D;
import java.util.Iterator;
import com.jrefinery.ui.RefineryUtilities;
import com.jrefinery.chart.event.AxisChangeEvent;

/**
 * A horizontal axis that displays numerical values.
 * @see XYPlot
 */
public class HorizontalNumberAxis3D extends HorizontalNumberAxis {

    /** A flag indicating whether or not tick labels are drawn vertically. */
    protected boolean verticalTickLabels;

    /** The default 3D-effect (in pixels). */
    public static final double DEFAULT_EFFECT_3D = 10.00;

    /** The 3D-effect (in pixels). */
    private double effect3d = DEFAULT_EFFECT_3D;

    /**
     * Constructs a HorizontalNumberAxis3D, with no label and default attributes.
     */
    public HorizontalNumberAxis3D() {
        this(null);
    }

    /**
     * Constructs a horizontal number axis, using default values where necessary.
     *
     * @param label The axis label.
     */
    public HorizontalNumberAxis3D(String label) {

        this(label,
             Axis.DEFAULT_AXIS_LABEL_FONT,
             ValueAxis.DEFAULT_MINIMUM_AXIS_VALUE,
             ValueAxis.DEFAULT_MAXIMUM_AXIS_VALUE);

        this.autoRange = true;

    }

    /**
     * Constructs a horizontal number axis.
     *
     * @param label The axis label.
     * @param labelFont The font for displaying the axis label.
     * @param minimumAxisValue The lowest value shown on the axis.
     * @param maximumAxisValue The highest value shown on the axis.
     */
    public HorizontalNumberAxis3D(String label, Font labelFont,
                                double minimumAxisValue, double maximumAxisValue) {

        this(label,
             labelFont,
             Axis.DEFAULT_AXIS_LABEL_PAINT,
             Axis.DEFAULT_AXIS_LABEL_INSETS,
             true,  // tick labels visible
             Axis.DEFAULT_TICK_LABEL_FONT,
             Axis.DEFAULT_TICK_LABEL_PAINT,
             Axis.DEFAULT_TICK_LABEL_INSETS,
             false,  // tick labels drawn vertically
             true,  // tick marks visible
             Axis.DEFAULT_TICK_STROKE,
             false, // no auto range selection, since the caller specified a range in the arguments
             NumberAxis.DEFAULT_AUTO_RANGE_INCLUDES_ZERO,
             NumberAxis.DEFAULT_AUTO_RANGE_STICKY_ZERO,
             NumberAxis.DEFAULT_MINIMUM_AUTO_RANGE,
             minimumAxisValue, maximumAxisValue,
             false, // inverted
             true,  // auto tick unit selection
             NumberAxis.DEFAULT_TICK_UNIT,
             true,  // grid lines visible
             ValueAxis.DEFAULT_GRID_LINE_STROKE,
             ValueAxis.DEFAULT_GRID_LINE_PAINT,
             ValueAxis.DEFAULT_CROSSHAIR_VISIBLE,
             0.0,
             ValueAxis.DEFAULT_CROSSHAIR_STROKE,
             ValueAxis.DEFAULT_CROSSHAIR_PAINT,
             DEFAULT_EFFECT_3D);
    }

    /**
     * Constructs a horizontal number axis.
     *
     * @param label The axis label.
     * @param labelFont The font for displaying the axis label.
     * @param labelPaint The color used to draw the axis label.
     * @param labelInsets The blank space around the axis label.
     * @param tickLabelsVisible Flag indicating whether or not the tick labels are visible.
     * @param tickLabelFont Font for displaying the tick labels.
     * @param tickLabelPaint The color used to display the tick labels.
     * @param tickLabelInsets The blank space around the tick labels.
     * @param verticalTickLabels A flag indicating whether or not tick labels are drawn vertically.
     * @param tickMarksVisible Flag indicating whether or not tick marks are visible.
     * @param tickMarkStroke The stroke used to draw tick marks (if visible).
     * @param autoRange Flag indicating whether or not the axis range is automatically determined to
     *                  fit the data.
     * @param autoRangeIncludesZero A flag indicating whether or not the axis range *must* include
     *                              zero.
     * @param autoRangeMinimum The smallest axis range permitted (avoids problems with a 'zero'
     *                         range).
     * @param minimumAxisValue The lowest value shown on the axis.
     * @param maximumAxisValue The highest value shown on the axis.
     * @param inverted A flag indicating whether the axis is normal or inverted (inverted means
     *                 running from positive to negative).
     * @param autoTickUnit A flag indicating whether or not the tick value is automatically
     *                     selected from the range of standard tick units.
     * @param tickUnit The tick unit.
     * @param gridLinesVisible Flag indicating whether or not grid lines are visible for this axis;
     * @param gridStroke The Stroke used to display grid lines (if visible);
     * @param gridPaint The Paint used to display grid lines (if visible).
     * @param crosshairValue The value at which to draw an optional crosshair (null permitted).
     * @param crosshairStroke The pen/brush used to draw the crosshair.
     * @param crosshairPaint The color used to draw the crosshair.
     */
    public HorizontalNumberAxis3D(String label,
                                Font labelFont, Paint labelPaint, Insets labelInsets,
                                boolean tickLabelsVisible,
                                Font tickLabelFont, Paint tickLabelPaint, Insets tickLabelInsets,
                                boolean verticalTickLabels,
                                boolean tickMarksVisible, Stroke tickMarkStroke,
                                boolean autoRange,
                                boolean autoRangeIncludesZero, boolean autoRangeStickyZero,
                                Number autoRangeMinimumSize,
                                double minimumAxisValue, double maximumAxisValue,
                                boolean inverted,
                                boolean autoTickUnitSelection, NumberTickUnit tickUnit,
                                boolean gridLinesVisible, Stroke gridStroke, Paint gridPaint,
                                boolean crosshairVisible, double crosshairValue,
                                Stroke crosshairStroke, Paint crosshairPaint,
                                                                double effect3d) {

        super(label, labelFont, labelPaint, labelInsets,
              tickLabelsVisible,
              tickLabelFont, tickLabelPaint, tickLabelInsets,
              verticalTickLabels,
              tickMarksVisible, tickMarkStroke,
              autoRange,
              autoRangeIncludesZero, autoRangeStickyZero,
              autoRangeMinimumSize,
              minimumAxisValue, maximumAxisValue,
              inverted,
              autoTickUnitSelection, tickUnit,
              gridLinesVisible,
              gridStroke, gridPaint,
              crosshairVisible, crosshairValue, crosshairStroke, crosshairPaint);

        this.effect3d = effect3d;

    }

    /**
     * Return axis 3d deep along 'Z' axis.
     */
    public double getEffect3d() {
        return effect3d;
    }

    /**
     * Draws the plot on a Java 2D graphics device (such as the screen or a printer).
     *
     * @param g2 The graphics device.
     * @param drawArea The area within which the chart should be drawn.
     * @param plotArea The area within which the plot should be drawn (a subset of the drawArea).
     */
    public void draw(Graphics2D g2, Rectangle2D drawArea, Rectangle2D plotArea) {

        // draw the axis label...
        if (label!=null) {
            g2.setFont(labelFont);
            g2.setPaint(labelPaint);
            FontRenderContext frc = g2.getFontRenderContext();
            Rectangle2D labelBounds = labelFont.getStringBounds(label, frc);
            LineMetrics lm = labelFont.getLineMetrics(label, frc);
            float labelx = (float)(plotArea.getX()+plotArea.getWidth()/2-labelBounds.getWidth()/2);
            float labely = (float)(drawArea.getMaxY()-labelInsets.bottom
                                                     -lm.getDescent()
                                                     -lm.getLeading());
            g2.drawString(label, labelx, labely);
        }

        // draw the tick labels and marks
        this.refreshTicks(g2, drawArea, plotArea);

        float maxY = (float)plotArea.getMaxY();
        g2.setFont(getTickLabelFont());

        Iterator iterator = ticks.iterator();
        while (iterator.hasNext()) {
            Tick tick = (Tick)iterator.next();
            float xx = (float)this.translateValueToJava2D(tick.getNumericalValue(), plotArea);
            if (tickLabelsVisible) {
                g2.setPaint(this.tickLabelPaint);
                if (this.verticalTickLabels) {
                    RefineryUtilities.drawRotatedString(tick.getText(), g2,
                                                        tick.getX(), tick.getY(), -Math.PI/2);
                }
                else {
                    g2.drawString(tick.getText(), tick.getX(), tick.getY());
                }
            }
            if (tickMarksVisible) {
                g2.setStroke(this.getTickMarkStroke());
                Line2D mark = new Line2D.Float(xx, maxY-2, xx, maxY+2);
                g2.draw(mark);
            }
            if (this.gridLinesVisible) {
                g2.setStroke(gridStroke);
                g2.setPaint(gridPaint);
                Line2D gridline = new Line2D.Double(xx+effect3d,
                                                    plotArea.getMaxY()-effect3d,
                                                    xx+effect3d,
                                                    plotArea.getMinY()-effect3d);
                g2.draw(gridline);

                Line2D grid3Dline = new Line2D.Double(xx,
                plotArea.getMaxY(), xx+effect3d, plotArea.getMaxY()-effect3d);
                g2.draw(grid3Dline);


            }
        }

    }

    /**
     * Returns the height required to draw the axis in the specified draw area.
     * @param g2 The graphics device;
     * @param plot The plot that the axis belongs to;
     * @param drawArea The area within which the plot should be drawn;
     */
    public double reserveHeight(Graphics2D g2, Plot plot, Rectangle2D drawArea) {

        // calculate the height of the axis label...
        double labelHeight = 0.0;
        if (label!=null) {
            LineMetrics metrics = labelFont.getLineMetrics(label, g2.getFontRenderContext());
            labelHeight = this.labelInsets.top+metrics.getHeight()+this.labelInsets.bottom;
        }

        // calculate the height required for the tick labels (if visible);
        double tickLabelHeight = tickLabelInsets.top+tickLabelInsets.bottom;
        if (tickLabelsVisible) {
            g2.setFont(tickLabelFont);
            this.refreshTicks(g2, drawArea, drawArea);
            tickLabelHeight = tickLabelHeight+getMaxTickLabelHeight(g2, drawArea,
                                                                    this.verticalTickLabels);
        }
        return labelHeight+tickLabelHeight;

    }

    /**
     * Returns area in which the axis will be displayed.
     * @param g2 The graphics device;
     * @param plot A reference to the plot;
     * @param drawArea The area within which the plot and axes should be drawn;
     * @param reservedWidth The space already reserved for the vertical axis;
     */
    public Rectangle2D reserveAxisArea(Graphics2D g2, Plot plot, Rectangle2D drawArea,
                                       double reservedWidth) {

        // calculate the height of the axis label...
        double labelHeight = 0.0;
        if (label!=null) {
            LineMetrics metrics = labelFont.getLineMetrics(label, g2.getFontRenderContext());
            labelHeight = this.labelInsets.top+metrics.getHeight()+this.labelInsets.bottom;
        }

        // calculate the height required for the tick labels (if visible);
        double tickLabelHeight = tickLabelInsets.top+tickLabelInsets.bottom;
        if (tickLabelsVisible) {
            g2.setFont(tickLabelFont);
            this.refreshTicks(g2, drawArea, drawArea);
            tickLabelHeight = tickLabelHeight+getMaxTickLabelHeight(g2, drawArea,
                                                                    this.verticalTickLabels);
        }
        return new Rectangle2D.Double(drawArea.getX(), drawArea.getMaxY(),
                                      drawArea.getWidth()-reservedWidth,
                                      labelHeight+tickLabelHeight);

    }

    /**
     * A utility method for determining the height of the tallest tick label.
     * @param g2 The graphics device;
     * @param drawArea The area within which the plot and axes should be drawn;
     * @param vertical A flag that indicates whether or not the tick labels are 'vertical';
     */
    private double getMaxTickLabelHeight(Graphics2D g2, Rectangle2D drawArea, boolean vertical) {
        Font font = getTickLabelFont();
        g2.setFont(font);
        FontRenderContext frc = g2.getFontRenderContext();
        double maxHeight = 0.0;
        if (vertical) {
            Iterator iterator = this.ticks.iterator();
            while (iterator.hasNext()) {
                Tick tick = (Tick)iterator.next();
                Rectangle2D labelBounds = font.getStringBounds(tick.getText(), frc);
                if (labelBounds.getWidth()>maxHeight) {
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
     * For this axis, the requirement is that the plot implements the HorizontalValuePlot interface.
     * @param plot The plot.
     */
    protected boolean isCompatiblePlot(Plot plot) {
        if (plot instanceof HorizontalValuePlot) return true;
        else return false;
    }

}
