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
 * -------------------------
 * VerticalNumberAxis3D.java
 * -------------------------
 * (C) Copyright 2001, 2002, by Serge V. Grachov and Contributors.
 *
 * Original Author:  Serge V. Grachov;
 * Contributor(s):   David Gilbert (for Simba Management Limited);
 *                   Jonathan Nash;
 *
 * $Id: VerticalNumberAxis3D.java,v 1.1 2007/10/10 19:41:59 vauchers Exp $
 *
 * Changes
 * -------
 * 31-Oct-2001 : Version 1 contributed by Serge V. Grachov (DG);
 * 23-Nov-2001 : Overhauled auto tick unit code for all axes (DG);
 * 12-Dec-2001 : Minor change due to grid lines bug fix (DG);
 * 08-Jan-2002 : Added flag allowing the axis to be 'inverted'.  That is, run from positive to
 *               negative.  Added default values to constructors (DG);
 * 16-Jan-2002 : Added an optional crosshair, based on the implementation by Jonathan Nash (DG);
 * 25-Feb-2002 : Updated constructors for new autoRangeStickyZero flag (DG);
 * 19-Apr-2002 : drawVerticalString(...) is now drawRotatedString(...) in RefineryUtilities (DG);
 * 25-Jun-2002 : Removed redundant import (DG);
 *
 */

package com.jrefinery.chart;

import com.jrefinery.ui.RefineryUtilities;
import java.awt.Graphics2D;
import java.awt.Font;
import java.awt.Paint;
import java.awt.Stroke;
import java.awt.Insets;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.util.Iterator;

/**
 * A standard linear value axis, for values displayed vertically.
 */
public class VerticalNumberAxis3D extends VerticalNumberAxis {

    /** The default 3D-effect (in pixels). */
    public static final double DEFAULT_EFFECT_3D = 10.00;

    /** The 3D-effect (in pixels). */
    private double effect3d = DEFAULT_EFFECT_3D;

    /**
     * Constructs a VerticalNumberAxis3D, with no label and default attributes.
     */
    public VerticalNumberAxis3D() {
        this(null);
    }

    /**
     * Constructs a VerticalNumberAxis3D, with the specified label and default attributes.
     *
     * @param label The axis label.
     */
    public VerticalNumberAxis3D(String label) {

        this(label,
             Axis.DEFAULT_AXIS_LABEL_FONT,
             ValueAxis.DEFAULT_MINIMUM_AXIS_VALUE,
             ValueAxis.DEFAULT_MAXIMUM_AXIS_VALUE);

        this.autoRange = true;

    }

    /**
     * Constructs a VerticalNumberAxis3D, using default attributes where necessary.
     *
     * @param label The axis label.
     * @param labelFont The font for displaying the axis label.
     * @param minimumAxisValue The lowest value shown on the axis.
     * @param maximumAxisValue The highest value shown on the axis.
     */
    public VerticalNumberAxis3D(String label, Font labelFont,
                                double minimumAxisValue, double maximumAxisValue) {

        this(label, labelFont,
             Axis.DEFAULT_AXIS_LABEL_PAINT,
             Axis.DEFAULT_AXIS_LABEL_INSETS,
             true,  // vertical label
             true,  // show tick labels
             Axis.DEFAULT_TICK_LABEL_FONT,
             Axis.DEFAULT_TICK_LABEL_PAINT,
             Axis.DEFAULT_TICK_LABEL_INSETS,
             true,  // show tick marks
             Axis.DEFAULT_TICK_STROKE,
             false,  // no autorange since caller specified a range
             NumberAxis.DEFAULT_AUTO_RANGE_INCLUDES_ZERO,
             NumberAxis.DEFAULT_AUTO_RANGE_STICKY_ZERO,
             NumberAxis.DEFAULT_MINIMUM_AUTO_RANGE,
             minimumAxisValue, maximumAxisValue,
             false, // inverted
             true,
             NumberAxis.DEFAULT_TICK_UNIT,
             true,  // show grid lines
             ValueAxis.DEFAULT_GRID_LINE_STROKE,
             ValueAxis.DEFAULT_GRID_LINE_PAINT,
             ValueAxis.DEFAULT_CROSSHAIR_VISIBLE,
             0.0,
             ValueAxis.DEFAULT_CROSSHAIR_STROKE,
             ValueAxis.DEFAULT_CROSSHAIR_PAINT,
             DEFAULT_EFFECT_3D);

    }

    /**
     * Constructs a new VerticalNumberAxis3D.
     *
     * @param label The axis label.
     * @param labelFont The font for displaying the axis label.
     * @param labelPaint The paint used to draw the axis label.
     * @param labelInsets Determines the amount of blank space around the label.
     * @param labelDrawnVertical Flag indicating whether or not the label is drawn vertically.
     * @param tickLabelsVisible Flag indicating whether or not tick labels are visible.
     * @param tickLabelFont The font used to display tick labels.
     * @param tickLabelPaint The paint used to draw tick labels.
     * @param tickLabelInsets Determines the amount of blank space around tick labels.
     * @param tickMarksVisible Flag indicating whether or not tick marks are visible.
     * @param tickMarkStroke The stroke used to draw tick marks (if visible).
     * @param autoRange Flag indicating whether or not the axis is automatically scaled to fit the
     *                  data.
     * @param autoRangeIncludesZero A flag indicating whether or not zero *must* be displayed on
     *                              axis.
     * @param autoRangeMinimum The smallest automatic range allowed.
     * @param minimumAxisValue The lowest value shown on the axis.
     * @param maximumAxisValue The highest value shown on the axis.
     * @param inverted A flag indicating whether the axis is normal or inverted (inverted means
     *                 running from positive to negative).
     * @param autoTickUnitSelection A flag indicating whether or not the tick units are
     *                              selected automatically.
     * @param tickUnit The tick unit.
     * @param gridLinesVisible Flag indicating whether or not grid lines are visible for this axis.
     * @param gridStroke The pen/brush used to display grid lines (if visible).
     * @param gridPaint The color used to display grid lines (if visible).
     * @param crosshairValue The value at which to draw an optional crosshair (null permitted).
     * @param crosshairStroke The pen/brush used to draw the crosshair.
     * @param crosshairPaint The color used to draw the crosshair.
     * @param effect3d 'Z' axis deep.
     */
    public VerticalNumberAxis3D(String label,
                                Font labelFont, Paint labelPaint, Insets labelInsets,
                                boolean labelDrawnVertical,
                                boolean tickLabelsVisible, Font tickLabelFont, Paint tickLabelPaint,
                                Insets tickLabelInsets,
                                boolean tickMarksVisible, Stroke tickMarkStroke,
                                boolean autoRange,
                                boolean autoRangeIncludesZero, boolean autoRangeStickyZero,
                                Number autoRangeMinimum,
                                double minimumAxisValue, double maximumAxisValue,
                                boolean inverted,
                                boolean autoTickUnitSelection, NumberTickUnit tickUnit,
                                boolean gridLinesVisible, Stroke gridStroke, Paint gridPaint,
                                boolean crosshairVisible, double crosshairValue,
                                Stroke crosshairStroke, Paint crosshairPaint,
                                double effect3d) {

        super(label, labelFont, labelPaint, labelInsets, labelDrawnVertical, tickLabelsVisible,
              tickLabelFont, tickLabelPaint,
              tickLabelInsets, tickMarksVisible, tickMarkStroke,
              autoRange,
              autoRangeIncludesZero, autoRangeStickyZero,
              autoRangeMinimum,
              minimumAxisValue, maximumAxisValue,
              inverted,
              autoTickUnitSelection, tickUnit,
              gridLinesVisible, gridStroke, gridPaint,
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
     * @param dataArea The area within which the plot should be drawn (a subset of the drawArea).
     */
    public void draw(Graphics2D g2, Rectangle2D drawArea, Rectangle2D dataArea) {

        if (!visible) return;

        // draw the axis label
        if (this.label!=null) {
            g2.setFont(labelFont);
            g2.setPaint(labelPaint);

            Rectangle2D labelBounds = labelFont.getStringBounds(label, g2.getFontRenderContext());
            if (verticalLabel) {
                double xx = drawArea.getX()+labelInsets.left+labelBounds.getHeight();
                double yy = dataArea.getY()+dataArea.getHeight()/2+(labelBounds.getWidth()/2);
                RefineryUtilities.drawRotatedString(label, g2, (float)xx, (float)yy, -Math.PI/2);
            }
            else {
                double xx = drawArea.getX()+labelInsets.left;
                double yy = drawArea.getY()+drawArea.getHeight()/2-labelBounds.getHeight()/2;
                g2.drawString(label, (float)xx, (float)yy);
            }
        }

        // draw the tick labels and marks and gridlines
        this.refreshTicks(g2, drawArea, dataArea);
        double xx = dataArea.getX();
        g2.setFont(tickLabelFont);

        Iterator iterator = ticks.iterator();
        while (iterator.hasNext()) {
            Tick tick = (Tick)iterator.next();
            float yy = (float)this.translateValueToJava2D(tick.getNumericalValue(), dataArea);
            if (tickLabelsVisible) {
                g2.setPaint(this.tickLabelPaint);
                g2.drawString(tick.getText(), tick.getX(), tick.getY());
            }
            if (tickMarksVisible) {
                g2.setStroke(this.getTickMarkStroke());
                Line2D mark = new Line2D.Double(dataArea.getX()-2, yy,
                                                dataArea.getX(), yy);
                g2.draw(mark);
            }
            if (gridLinesVisible) {
                g2.setStroke(gridStroke);
                g2.setPaint(gridPaint);
                Line2D gridline = new Line2D.Double(xx+effect3d, yy-effect3d,
                                                    dataArea.getMaxX()+effect3d, yy-effect3d);
                g2.draw(gridline);
                Line2D grid3Dline = new Line2D.Double(xx, yy,
                                                      xx+effect3d, yy-effect3d);
                g2.draw(grid3Dline);
            }
        }

    }

}