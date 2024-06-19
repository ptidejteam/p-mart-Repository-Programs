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
 * ------------------------------------
 * VerticalLogarithmicColorBarAxis.java
 * ------------------------------------
 * (C) Copyright 2002, 2003, by David M. O'Donnell and Contributors.
 *
 * Original Author:  David M. O'Donnell;
 * Contributor(s):   David Gilbert (for Simba Management Limited);
 *
 * $Id: VerticalLogarithmicColorBarAxis.java,v 1.1 2007/10/10 20:03:22 vauchers Exp $
 *
 * Changes
 * -------
 * 26-Nov-2002 : Version 1 contributed by David M. O'Donnell (DG);
 * 26-Mar-2003 : Implemented Serializable (DG);
 *
 */

package org.jfree.chart.axis;

import java.awt.BasicStroke;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.font.FontRenderContext;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.io.Serializable;
import java.util.Iterator;

import org.jfree.chart.plot.ContourValuePlot;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.plot.VerticalValuePlot;
import org.jfree.chart.ui.ColorPalette;
import org.jfree.chart.ui.RainbowPalette;
import org.jfree.data.Range;
import org.jfree.ui.RefineryUtilities;

/**
 * A vertical ColorBar.  This class extends the VerticalNumberAxis to provide
 * a) tickmarks, b) ticklabels, c) axis label, d) mapping between java2D and plot units.
 *
 * @author David M. O'Donnell
 */
public class VerticalLogarithmicColorBarAxis extends VerticalLogarithmicAxis
                                             implements ColorBarAxis, Serializable {


    /** The default color bar thickness. */
    public static final int DEFAULT_COLORBAR_THICKNESS = 0;

    /** The default color bar thickness as a percentage. */
    public static final double DEFAULT_COLORBAR_THICKNESS_PCT = 0.05;

    /** The default outer gap. */
    public static final int DEFAULT_OUTERGAP = 20;

    /** The palette. */
    private ColorPalette colorPalette = null;

    /** The color bar length. */
    private int colorBarLength = 0; // default make height of plotArea

    /** The color bar thickness. */
    private int colorBarThickness = DEFAULT_COLORBAR_THICKNESS;

    /** The color bar thickness percentage. */
    private double colorBarThicknessPercent = DEFAULT_COLORBAR_THICKNESS_PCT;

    /** The amount of blank space around the colorbar. */
    private int outerGap;

    /**
     * Constructor for VerticalLogarithmicColorBarAxis.
     *
     * @param label  the axis label.
     */
    public VerticalLogarithmicColorBarAxis(String label) {

        super(label);
        this.colorPalette = new RainbowPalette();
        this.colorBarLength = 0;
        this.colorBarThickness = DEFAULT_COLORBAR_THICKNESS;
        this.colorBarThicknessPercent = DEFAULT_COLORBAR_THICKNESS_PCT;
        this.outerGap = DEFAULT_OUTERGAP;
        this.colorPalette.setMinZ(getRange().getLowerBound());
        this.colorPalette.setMaxZ(getRange().getUpperBound());
        this.setLowerMargin(0.0);
        this.setUpperMargin(0.0);
        this.colorPalette.setLogscale(true);

    }

    /**
     * Returns the current ColorPalette.
     *
     * @return the palette.
     */
    public ColorPalette getColorPalette() {
        return colorPalette;
    }

    /**
     * Returns the Paint associated with the value.
     *
     * @param value  the value.
     *
     * @return the paint.
     */
    public Paint getPaint(double value) {
        return colorPalette.getPaint(value);
    }

    /**
     * Sets the current ColorPalette.
     *
     * @param palette  the palette.
     */
    public void setColorPalette(ColorPalette palette) {
        this.colorPalette = palette;
    }

    /**
     * Rescales the axis to ensure that all data is visible.
     */
    public void autoAdjustRange() {

        Plot plot = getPlot();
        if (plot == null) {
            return;  // no plot, no data.
        }

        if (plot instanceof VerticalValuePlot) {
            ContourValuePlot cvp = (ContourValuePlot) plot;
            Range r = cvp.getContourDataRange();

            if (r == null) {
                r = new Range(DEFAULT_LOWER_BOUND, DEFAULT_UPPER_BOUND);
            }

            double lower = computeLogFloor(r.getLowerBound());

            if (!getAllowNegativesFlag() && lower >= 0.0 && lower < SMALL_LOG_VALUE) {
                //negatives not allowed and lower range bound is zero
                lower = r.getLowerBound();    //use data range bound instead
            }

            double upper = r.getUpperBound();
            if (!getAllowNegativesFlag() && upper < 1.0 && upper > 0.0 && lower > 0.0) {
                //negatives not allowed and upper bound between 0 & 1
                //round up to nearest significant digit for bound:
                //get negative exponent:
                double expVal = Math.log(upper) / LOG10_VALUE;
                expVal = Math.ceil(-expVal + 0.001); //get positive exponent
                expVal = Math.pow(10, expVal);      //create multiplier value
                //multiply, round up, and divide for bound value:
                upper = (expVal > 0.0) ? Math.ceil(upper * expVal) / expVal : Math.ceil(upper);
            }
            else {
                //negatives allowed or upper bound not between 0 & 1
                upper = computeLogCeil(upper);  //use nearest log value
            }
            // ensure the autorange is at least <minRange> in size...
            double minRange = getAutoRangeMinimumSize();
            if (upper - lower < minRange) {
              upper = (upper + lower + minRange) / 2;
              lower = (upper + lower - minRange) / 2;
            }

            setRangeAttribute(new Range(lower, upper));

            setupSmallLogFlag();       //setup flag based on bounds values

            this.colorPalette.setMinZ(lower); //dmo: added this to copied parent method
            this.colorPalette.setMaxZ(upper); //dmo: added this to copied parent method
       }
    }


    /**
     * Draws the plot on a Java 2D graphics device (such as the screen or a printer).
     *
     * @param g2  the graphics device.
     * @param drawArea  the area within which the chart should be drawn.
     * @param plotArea  the area within which the plot should be drawn (a subset of the drawArea).
     * @param location  the axis location.
     */
    public void draw(Graphics2D g2, Rectangle2D drawArea, Rectangle2D plotArea, int location) {

        // draw colorBar rectangle
        double length = plotArea.getHeight();
        if (colorBarLength > 0) {
            length = this.colorBarLength;
        }

        double thickness = colorBarThicknessPercent * plotArea.getWidth(); // plot width
        if (this.colorBarThickness > 0) {
            thickness = colorBarThickness;  //allow fixed thickness
        }

        Rectangle2D colorBarArea = new Rectangle2D.Double(plotArea.getMaxX() + outerGap,
                                                          plotArea.getY(), thickness, length);

        // draw the tick labels and marks and gridlines
        refreshTicks(g2, drawArea, colorBarArea, location);

        drawColorBar(g2, colorBarArea);

        double xx = colorBarArea.getX();
        g2.setFont(getTickLabelFont());

        double xLabelPosition = -1.e20;
        Iterator iterator = getTicks().iterator();
        while (iterator.hasNext()) {
            Tick tick = (Tick) iterator.next();
            float yy = (float) this.translateValueToJava2D(tick.getNumericalValue(), colorBarArea);
            if (isTickLabelsVisible()) {
                xLabelPosition = Math.max(xLabelPosition,
                                          tick.getX() + tickLabelWidth(g2, tick.getText()));
                g2.setPaint(getTickLabelPaint());
                g2.drawString(tick.getText(), tick.getX(), tick.getY());
            }
            if (isTickMarksVisible()) {
                g2.setStroke(this.getTickMarkStroke());
                Line2D mark = new Line2D.Double(colorBarArea.getX() + colorBarArea.getWidth() - 2,
                                                yy,
                                                colorBarArea.getX() + colorBarArea.getWidth() + 2,
                                                yy);
                g2.draw(mark);
            }
        }
        // draw the axis label
        if (getLabel() != null) {
            g2.setFont(getLabelFont());
            g2.setPaint(getLabelPaint());

            Rectangle2D labelBounds
                = getLabelFont().getStringBounds(getLabel(), g2.getFontRenderContext());
            if (isVerticalLabel()) {
                xx = xLabelPosition + getLabelInsets().right + labelBounds.getHeight();
                double yy = colorBarArea.getY() + colorBarArea.getHeight() / 2
                                                + labelBounds.getWidth() / 2;
                RefineryUtilities.drawRotatedString(getLabel(), g2,
                                                    (float) xx, (float) yy, -Math.PI / 2);
            }
            else {
                xx = colorBarArea.getX() + getLabelInsets().left;
                double yy = drawArea.getY() + drawArea.getHeight() / 2
                                            - labelBounds.getHeight() / 2;
                g2.drawString(getLabel(), (float) xx, (float) yy);
            }
        }

    }

    /**
     * Draws the plot on a Java 2D graphics device (such as the screen or a printer).
     *
     * @param g2  the graphics device.
     * @param colorBarArea  the area within which the axis should be drawn.
     */
    public void drawColorBar(Graphics2D g2, Rectangle2D colorBarArea) {

        Object antiAlias = g2.getRenderingHint(RenderingHints.KEY_ANTIALIASING);
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                            RenderingHints.VALUE_ANTIALIAS_OFF);

        colorPalette.setTickValues(getTicks());

        Stroke strokeSaved = g2.getStroke();
        g2.setStroke(new BasicStroke(1.0f));

        double x1 = colorBarArea.getX();
        double x2 = colorBarArea.getMaxX();
        double yy = colorBarArea.getY();
        while (yy <= colorBarArea.getY() + colorBarArea.getHeight()) {
            double value = translateJava2DtoValue((float) yy, colorBarArea);
            System.out.println(value);
            Line2D line = new Line2D.Double(x1, yy, x2, yy);
            g2.setPaint(this.getPaint(value));
            g2.draw(line);
            yy += 1;
        }
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, antiAlias);
        g2.setStroke(strokeSaved);
    }

    /**
     * Returns the tick label width.
     *
     * @param g2  the graphics device.
     * @param tickLabel  the tick label.
     *
     * @return the width.
     */
    public double tickLabelWidth(Graphics2D g2, String tickLabel) {
        Rectangle2D tickLabelBounds
            = getTickLabelFont().getStringBounds(tickLabel, g2.getFontRenderContext());
        return tickLabelBounds.getWidth();
    }

    /**
     * Returns true if the specified plot is compatible with the axis, and false otherwise.
     * <P>
     * This class (ContourValuePlot) requires that the plot implements the ContourValuePlot
     * interface.
     *
     * @param plot  the plot.
     *
     * @return  True if the specified plot is compatible with the axis, and false otherwise.
     */
    protected boolean isCompatiblePlot(Plot plot) {

        return (plot instanceof ContourValuePlot);

    }

    /**
     * Sets the maximum axis value.
     *
     * @param value  the value.
     */
    public void setMaximumAxisValue(double value) {
        this.colorPalette.setMaxZ(value);
        super.setMaximumAxisValue(value);
    }

    /**
     * Sets the minimum axis value.
     *
     * @param value  the value.
     */
    public void setMinimumAxisValue(double value) {
        this.colorPalette.setMinZ(value);
        super.setMinimumAxisValue(value);
    }

    /**
     * Calculates the positions of the tick labels for the axis, storing the
     * results in the tick label list (ready for drawing).
     *
     * @param g2  the graphics device.
     * @param drawArea  the area in which the plot and the axes should be drawn.
     * @param plotArea  the area in which the plot should be drawn.
     * @param location  the axis location.
     */
    public void refreshTicks(Graphics2D g2, Rectangle2D drawArea, Rectangle2D plotArea,
                             int location) {

        // Here I let the parent do the calculation, then modify the results, translating
        // the ticks position to colorbar location.

        super.refreshTicks(g2, drawArea, plotArea, location);  //have parent calcuate ticks
        Iterator iterator = getTicks().iterator();

        java.util.List ticksNew = new java.util.ArrayList();



        while (iterator.hasNext()) {
            Tick tick = (Tick) iterator.next();
            float x = tick.getX();
            String tickLabel = tick.getText();

            FontRenderContext frc = g2.getFontRenderContext();
            Rectangle2D tickLabelBounds = getTickLabelFont().getStringBounds(tickLabel, frc);

            x += (float) (tickLabelBounds.getWidth() + getTickLabelInsets().right);
            x += plotArea.getWidth() + this.colorBarThickness
                                    + getTickLabelInsets().right;
            x += getTickLabelInsets().left;

            float y = tick.getY();
            double currentTickValue = tick.getNumericalValue();

            Tick tickNew = new Tick(new Double(currentTickValue), tickLabel, x, y);
            ticksNew.add(tickNew);
        }
        getTicks().clear();
        getTicks().addAll(ticksNew);

    }

    /**
     * Returns the width of the axis.
     *
     * @param g2  the graphics device.
     * @param plot  the plot.
     * @param drawArea  the area for drawing.
     * @param location  the axis location.
     *
     * @return the width.
     */
    public double reserveWidth(Graphics2D g2, Plot plot, Rectangle2D drawArea, int location) {
        double w = super.reserveWidth(g2, plot, drawArea, location);
        return w + drawArea.getWidth() * colorBarThicknessPercent + outerGap
                 + getTickLabelInsets().left + getTickLabelInsets().right;
    }

    /**
     * This is cheat to make autoAdjustRange public.
     */
	public void doAutoRange() {
		autoAdjustRange();
	}

}
