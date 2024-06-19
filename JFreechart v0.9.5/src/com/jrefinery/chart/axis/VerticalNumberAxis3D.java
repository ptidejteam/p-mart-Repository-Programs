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
 * VerticalNumberAxis3D.java
 * -------------------------
 * (C) Copyright 2001-2003, by Serge V. Grachov and Contributors.
 *
 * Original Author:  Serge V. Grachov;
 * Contributor(s):   David Gilbert (for Simba Management Limited);
 *                   Jonathan Nash;
 *                   Richard Atkinson;
 *
 * $Id: VerticalNumberAxis3D.java,v 1.1 2007/10/10 19:54:25 vauchers Exp $
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
 * 25-Jul-2002 : Changed order of parameters in ValueAxis constructor (DG);
 * 06-Aug-2002 : Modified draw method to not draw axis label if label is empty String (RA);
 * 05-Sep-2002 : Updated constructor for changes in the Axis class, and changed draw method to
 *               observe tickMarkPaint (DG);
 * 22-Sep-2002 : Fixed errors reported by Checkstyle (DG);
 * 08-Nov-2002 : Moved to new package com.jrefinery.chart.axis (DG);
 * 20-Jan-2003 : Removed unnecessary constructors (DG);
 *
 */

package com.jrefinery.chart.axis;

import java.awt.Graphics2D;
import java.awt.Font;
import java.awt.Insets;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.util.Iterator;
import com.jrefinery.chart.Effect3D;
import com.jrefinery.chart.plot.Plot;
import com.jrefinery.chart.plot.CategoryPlot;
import com.jrefinery.chart.renderer.CategoryItemRenderer;
import com.jrefinery.ui.RefineryUtilities;

/**
 * A standard linear value axis, for values displayed vertically.
 *
 * @author Serge V. Grachov
 */
public class VerticalNumberAxis3D extends VerticalNumberAxis {

    /**
     * Constructs a VerticalNumberAxis3D, with the specified label and default attributes.
     *
     * @param label  the axis label (null permitted).
     */
    public VerticalNumberAxis3D(String label) {

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

        if (!isVisible()) {
            return;
        }

        // draw the axis label
        String label = getLabel();
        if (label == null ? false : !label.equals("")) {
            Font labelFont = getLabelFont();
            g2.setFont(labelFont);
            g2.setPaint(getLabelPaint());

            Rectangle2D labelBounds = labelFont.getStringBounds(label, g2.getFontRenderContext());
            Insets labelInsets = getLabelInsets();
            if (isVerticalLabel()) {
                double xx = plotArea.getX() + labelInsets.left
                                            + labelBounds.getHeight();
                double yy = dataArea.getY() + dataArea.getHeight() / 2
                                            + (labelBounds.getWidth() / 2);
                RefineryUtilities.drawRotatedString(label, g2, (float) xx,
                                                               (float) yy, -Math.PI / 2);
            }
            else {
                double xx = plotArea.getX() + labelInsets.left;
                double yy = plotArea.getY() + plotArea.getHeight() / 2
                                            - labelBounds.getHeight() / 2;
                g2.drawString(label, (float) xx, (float) yy);
            }
        }

        // draw the tick labels and marks and gridlines

        // calculate the adjusted data area taking into account the 3D effect...
        CategoryPlot plot = (CategoryPlot) getPlot();
        Effect3D e3D = (Effect3D) plot.getRenderer();
        Rectangle2D adjustedDataArea = new Rectangle2D.Double(dataArea.getMinX(),
            dataArea.getMinY() + e3D.getYOffset(), dataArea.getWidth() - e3D.getXOffset(),
            dataArea.getHeight() - e3D.getYOffset());
        refreshTicks(g2, plotArea, adjustedDataArea, location);
        g2.setFont(getTickLabelFont());

        Iterator iterator = getTicks().iterator();
        while (iterator.hasNext()) {
            Tick tick = (Tick) iterator.next();
            float yy = (float) translateValueToJava2D(tick.getNumericalValue(), adjustedDataArea);
            if (isTickLabelsVisible()) {
                g2.setPaint(getTickLabelPaint());
                g2.drawString(tick.getText(), tick.getX(), tick.getY());
            }

            if (isTickMarksVisible()) {
                g2.setStroke(getTickMarkStroke());
                g2.setPaint(getTickMarkPaint());
                Line2D mark = new Line2D.Double(dataArea.getX() - getTickMarkOutsideLength(), yy,
                                                dataArea.getX(), yy);
                g2.draw(mark);
            }

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
