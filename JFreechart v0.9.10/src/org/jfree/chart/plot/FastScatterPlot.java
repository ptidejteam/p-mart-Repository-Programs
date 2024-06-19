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
 * --------------------
 * FastScatterPlot.java
 * --------------------
 * (C) Copyright 2002, 2003, by Object Refinery Limited.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited);
 * Contributor(s):   -;
 *
 * $Id: FastScatterPlot.java,v 1.1 2007/10/10 19:05:14 vauchers Exp $
 *
 * Changes (from 29-Oct-2002)
 * --------------------------
 * 29-Oct-2002 : Added standard header (DG);
 * 07-Nov-2002 : Fixed errors reported by Checkstyle (DG);
 * 26-Mar-2003 : Implemented Serializable (DG);
 *
 */

package org.jfree.chart.plot;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;
import java.io.Serializable;

import org.jfree.chart.ChartRenderingInfo;
import org.jfree.chart.CrosshairInfo;
import org.jfree.chart.axis.AxisSpace;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.event.PlotChangeEvent;
import org.jfree.data.Range;
import org.jfree.ui.RectangleEdge;

/**
 * A fast scatter plot.
 *
 * @author David Gilbert
 */
public class FastScatterPlot extends Plot implements ValueAxisPlot, Serializable {

    /** The data. */
    private float[][] data;

    /** The x data range. */
    private Range xDataRange;

    /** The y data range. */
    private Range yDataRange;

    /** The domain axis (used for the x-values). */
    private ValueAxis domainAxis;

    /** The range axis (used for the y-values). */
    private ValueAxis rangeAxis;

    /** The paint used to plot data points. */
    private Paint paint;

    /**
     * Creates a new fast scatter plot.
     * <P>
     * The data is an array of x, y values:  data[0][i] = x, data[1][i] = y.
     *
     * @param data  the data.
     * @param domainAxis  the domain (x) axis.
     * @param rangeAxis  the range (y) axis.
     */
    public FastScatterPlot(float[][] data, ValueAxis domainAxis, ValueAxis rangeAxis) {

        super();

        this.data = data;
        this.xDataRange = calculateXDataRange(data);
        this.yDataRange = calculateYDataRange(data);
        this.domainAxis = domainAxis;
        if (domainAxis != null) {
            domainAxis.setPlot(this);
            domainAxis.addChangeListener(this);
        }

        this.rangeAxis = rangeAxis;
        if (rangeAxis != null) {
            rangeAxis.setPlot(this);
            rangeAxis.addChangeListener(this);
        }

        this.paint = Color.red;

    }

    /**
     * Returns a short string describing the plot type.
     *
     * @return a short string describing the plot type.
     */
    public String getPlotType() {
        return "Fast Scatter Plot";
    }

    /**
     * Returns the domain axis for the plot.  If the domain axis for this plot
     * is null, then the method will return the parent plot's domain axis (if
     * there is a parent plot).
     *
     * @return the domain axis.
     */
    public ValueAxis getDomainAxis() {

        return this.domainAxis;

    }

    /**
     * Returns the range axis for the plot.  If the range axis for this plot is
     * null, then the method will return the parent plot's range axis (if
     * there is a parent plot).
     *
     * @return the range axis.
     */
    public ValueAxis getRangeAxis() {

        return this.rangeAxis;

    }

    /**
     * Returns the paint used to plot data points.
     *
     * @return The paint.
     */
    public Paint getPaint() {
        return this.paint;
    }

    /**
     * Sets the color for the data points.
     *
     * @param paint  the paint.
     */
    public void setPaint(Paint paint) {
        this.paint = paint;
        this.notifyListeners(new PlotChangeEvent(this));
    }

    /**
     * Draws the fast scatter plot on a Java 2D graphics device (such as the screen or
     * a printer).
     *
     * @param g2  the graphics device.
     * @param plotArea   the area within which the plot (including axis labels) should be drawn.
     * @param info  collects chart drawing information (<code>null</code> permitted).
     */
    public void draw(Graphics2D g2, Rectangle2D plotArea, ChartRenderingInfo info) {

        // set up info collection...
        if (info != null) {
            info.setPlotArea(plotArea);

        }

        // adjust the drawing area for plot insets (if any)...
        Insets insets = getInsets();
        if (insets != null) {
            plotArea.setRect(plotArea.getX() + insets.left,
                             plotArea.getY() + insets.top,
                             plotArea.getWidth() - insets.left - insets.right,
                             plotArea.getHeight() - insets.top - insets.bottom);
        }

        AxisSpace space = new AxisSpace();
        space = this.domainAxis.reserveSpace(g2, this, plotArea, RectangleEdge.BOTTOM, space);
        space = this.rangeAxis.reserveSpace(g2, this, plotArea, RectangleEdge.LEFT, space);
        Rectangle2D dataArea = space.shrink(plotArea, null);

        if (info != null) {
            info.setDataArea(dataArea);
        }

        // draw the plot background and axes...
        drawBackground(g2, dataArea);

        double cursor;
        if (this.domainAxis != null) {
            cursor = dataArea.getMaxY();
            cursor = this.domainAxis.draw(g2, cursor, plotArea, dataArea, RectangleEdge.BOTTOM);
        }
        if (this.rangeAxis != null) {
            cursor = dataArea.getMinX();
            cursor = this.rangeAxis.draw(g2, cursor, plotArea, dataArea, RectangleEdge.LEFT);
        }

        Shape originalClip = g2.getClip();
        Composite originalComposite = g2.getComposite();

        g2.clip(dataArea);
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,
                                                   getForegroundAlpha()));

        render(g2, dataArea, info, null);

        g2.setClip(originalClip);
        g2.setComposite(originalComposite);
        drawOutline(g2, dataArea);

    }

    /**
     * Draws a representation of the data within the dataArea region.
     * <P>
     * The <code>info</code> and <code>crosshairInfo</code> arguments may be <code>null</code>.
     *
     * @param g2  the graphics device.
     * @param dataArea  the region in which the data is to be drawn.
     * @param info  an optional object for collection dimension information.
     * @param crosshairInfo  an optional object for collecting crosshair info.
     */
    public void render(Graphics2D g2, Rectangle2D dataArea,
                       ChartRenderingInfo info, CrosshairInfo crosshairInfo) {

        g2.setPaint(Color.red);
        if (this.data != null) {
            for (int i = 0; i < data[0].length; i++) {
                float x = data[0][i];
                float y = data[1][i];
                int transX = (int) this.domainAxis.translateValueToJava2D(x, dataArea,
                                                                          RectangleEdge.BOTTOM);
                int transY = (int) this.rangeAxis.translateValueToJava2D(y, dataArea,
                                                                         RectangleEdge.LEFT);
                g2.fillRect(transX, transY, 1, 1);
            }
        }

    }

    /**
     * Returns the range of data values to be plotted along the axis.
     *
     * @param axis  the axis.
     *
     * @return  the range.
     */
    public Range getDataRange(ValueAxis axis) {

        Range result = null;
        if (axis == this.domainAxis) {
            result = this.xDataRange;
        }
        else if (axis == this.rangeAxis) {
            result = this.yDataRange;
        }
        return result;
    }

    /**
     * Calculates the X data range.
     *
     * @param data  the data.
     *
     * @return the range.
     */
    private Range calculateXDataRange(float[][] data) {
        float lowest = Float.POSITIVE_INFINITY;
        float highest = Float.NEGATIVE_INFINITY;
        for (int i = 0; i < data[0].length; i++) {
            float v = data[0][i];
            if (v < lowest) {
                lowest = v;
            }
            if (v > highest) {
                highest = v;
            }
        }
        return new Range(lowest, highest);
    }

    /**
     * Calculates the Y data range.
     *
     * @param data  the data.
     *
     * @return the range.
     */
    private Range calculateYDataRange(float[][] data) {
        float lowest = Float.POSITIVE_INFINITY;
        float highest = Float.NEGATIVE_INFINITY;
        for (int i = 0; i < data[0].length; i++) {
            float v = data[1][i];
            if (v < lowest) {
                lowest = v;
            }
            if (v > highest) {
                highest = v;
            }
        }
        return new Range(lowest, highest);
    }

    /**
     * Multiplies the range on the horizontal axis/axes by the specified factor (not yet 
     * implemented).
     *
     * @param factor  the zoom factor.
     */
    public void zoomHorizontalAxes(double factor) {
        // zoom the domain axis
    }

    /**
     * Zooms in on the horizontal axes (not yet implemented).
     * 
     * @param lowerPercent  the new lower bound as a percentage of the current range.
     * @param upperPercent  the new upper bound as a percentage of the current range.
     */
    public void zoomHorizontalAxes(double lowerPercent, double upperPercent) {
        // zoom the domain axis
    }

    /**
     * Multiplies the range on the vertical axis/axes by the specified factor (not yet implemented).
     *
     * @param factor  the zoom factor.
     */
    public void zoomVerticalAxes(double factor) {
            // zoom the range axis
            // zoom all the secondary axes
    }

    /**
     * Zooms in on the vertical axes (not yet implemented).
     * 
     * @param lowerPercent  the new lower bound as a percentage of the current range.
     * @param upperPercent  the new upper bound as a percentage of the current range.
     */
    public void zoomVerticalAxes(double lowerPercent, double upperPercent) {
        // zoom the domain axis
    }

}
