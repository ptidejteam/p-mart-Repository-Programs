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
 * ----------------
 * MeterNeedle.java
 * ----------------
 * (C) Copyright 2002, 2003, by the Australian Antarctic Division and Contributors.
 *
 * Original Author:  Bryan Scott (for the Australian Antarctic Division);
 * Contributor(s):   David Gilbert (for Object Refinery Limited);
 *
 * $Id: MeterNeedle.java,v 1.1 2007/10/10 20:07:40 vauchers Exp $
 *
 * Changes:
 * --------
 * 25-Sep-2002 : Version 1, contributed by Bryan Scott (DG);
 * 07-Nov-2002 : Fixed errors reported by Checkstyle (DG);
 *
 */

package org.jfree.chart.needle;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

/**
 * The base class used to represent the needle on a {@link org.jfree.chart.plot.CompassPlot}.
 *
 * @author Bryan Scott
 */
public abstract class MeterNeedle {

    /** The outline paint. */
    private Paint outlinePaint = Color.black;

    /** The outline stroke. */
    private Stroke outlineStroke = new BasicStroke(2);

    /** The fill paint. */
    private Paint fillPaint = null;

    /** The highlight paint. */
    private Paint highlightPaint = null;

    /** The size. */
    private int size = 5;

    /** Scalar to aply to locate the rotation x point. */
    private double rotateX = 0.5;

    /** Scalar to aply to locate the rotation y point. */
    private double rotateY = 0.5;

    /** A transform. */
    private static AffineTransform transform = new AffineTransform();

//    /** 180 degrees in radians. */
//    private static final double ANGLE180 = Math.toRadians(180);

    /**
     * Creates a new needle.
     */
    public MeterNeedle() {
        this(null, null, null);
    }

    /**
     * Creates a new needle.
     *
     * @param outline  the outline paint.
     * @param fill  the fill paint.
     * @param highlight  the highlight paint.
     */
    public MeterNeedle(Paint outline, Paint fill, Paint highlight) {
        fillPaint = fill;
        highlightPaint = highlight;
        outlinePaint = outline;
    }

    /**
     * Returns the outline paint.
     *
     * @return the outline paint.
     */
    public Paint getOutlinePaint() {
        return this.outlinePaint;
    }

    /**
     * Sets the outline paint.
     *
     * @param p  the new paint.
     */
    public void setOutlinePaint(Paint p) {
        if (p != null) {
            outlinePaint = p;
        }
    }

    /**
     * Returns the outline stroke.
     *
     * @return the outline stroke.
     */
    public Stroke getOutlineStroke() {
        return this.outlineStroke;
    }

    /**
     * Sets the outline stroke.
     *
     * @param s  the new stroke.
     */
    public void setOutlineStroke(Stroke s) {
        if (s != null) {
            outlineStroke = s;
        }
    }

    /**
     * Returns the fill paint.
     *
     * @return the fill paint.
     */
    public Paint getFillPaint() {
        return this.fillPaint;
    }

    /**
     * Sets the fill paint.
     *
     * @param p  the fill paint.
     */
    public void setFillPaint(Paint p) {
        if (p != null) {
            fillPaint = p;
        }
    }

    /**
     * Returns the highlight paint.
     *
     * @return the highlight paint.
     */
    public Paint getHighlightPaint() {
        return this.highlightPaint;
    }

    /**
     * Sets the highlight paint.
     *
     * @param p  the highlight paint.
     */
    public void setHighlightPaint(Paint p) {
        if (p != null) {
            highlightPaint = p;
        }
    }

    /**
     * Returns the scalar used for determining the rotation x value.
     *
     * @return the x rotate scalar.
     */
    public double getRotateX() {
        return this.rotateX;
    }

    /**
     * Sets the rotateX value.
     *
     * @param x  the new value.
     */
    public void setRotateX(double x) {
        this.rotateX = x;
    }

    /**
     * Sets the rotateY value.
     *
     * @param y  the new value.
     */
    public void setRotateY(double y) {
        this.rotateY = y;
    }

    /**
     * Returns the scalar used for determining the rotation y value.
     *
     * @return the y rotate scalar.
     */
    public double getRotateY() {
        return this.rotateY;
    }

    /**
     * Draws the needle.
     *
     * @param g2  the graphics device.
     * @param plotArea  the plot area.
     */
    public void draw(Graphics2D g2, Rectangle2D plotArea) {
        draw(g2, plotArea, 0);
    }

    /**
     * Draws the needle.
     *
     * @param g2  the graphics device.
     * @param plotArea  the plot area.
     * @param angle  the angle.
     */
    public void draw(Graphics2D g2, Rectangle2D plotArea, double angle) {

        Point2D.Double pt = new Point2D.Double();
        pt.setLocation(plotArea.getMinX() + rotateX * plotArea.getWidth(),
                       plotArea.getMinY() + rotateY * plotArea.getHeight());
        draw(g2, plotArea, pt, angle);

    }

    /**
     * Draws the needle.
     *
     * @param g2  the graphics device.
     * @param plotArea  the plot area.
     * @param rotate  the rotation point.
     * @param angle  the angle.
     */
    public void draw(Graphics2D g2, Rectangle2D plotArea, Point2D rotate, double angle) {

        Paint savePaint = g2.getColor();
        Stroke saveStroke = g2.getStroke();

        drawNeedle(g2, plotArea, rotate, Math.toRadians(angle));

        g2.setStroke(saveStroke);
        g2.setPaint(savePaint);

    }

    /**
     * Draws the needle.
     *
     * @param g2  the graphics device.
     * @param plotArea  the plot area.
     * @param rotate  the rotation point.
     * @param angle  the angle.
     */
    protected abstract void drawNeedle(Graphics2D g2,
                                       Rectangle2D plotArea, Point2D rotate, double angle);

    /**
     * Displays a shape.
     *
     * @param g2  the graphics device.
     * @param shape  the shape.
     */
    protected void defaultDisplay(Graphics2D g2, Shape shape) {

        if (fillPaint != null) {
            g2.setPaint(fillPaint);
            g2.fill(shape);
        }

        if (outlinePaint != null) {
            g2.setStroke(outlineStroke);
            g2.setPaint(outlinePaint);
            g2.draw(shape);
        }

    }

    /**
     * Returns the size.
     *
     * @return the size.
     */
    public int getSize() {
        return this.size;
    }

    /**
     * Sets the size.
     *
     * @param pixels  the new size.
     */
    public void setSize(int pixels) {
        size = pixels;
    }

    /**
     * Returns the transform.
     *
     * @return the transform.
     */
    public AffineTransform getTransform() {
        return MeterNeedle.transform;
    }

}
