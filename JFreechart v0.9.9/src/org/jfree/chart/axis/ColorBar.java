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
 * -------------
 * ColorBar.java
 * -------------
 * (C) Copyright 2002, 2003, by David M. O'Donnell and Contributors.
 *
 * Original Author:  David M. O'Donnell;
 * Contributor(s):   David Gilbert (for Object Refinery Limited);
 *
 * $Id: ColorBar.java,v 1.1 2007/10/10 20:07:40 vauchers Exp $
 *
 * Changes
 * -------
 * 26-Nov-2002 : Version 1 contributed by David M. O'Donnell (DG);
 * 14-Jan-2003 : Changed autoRangeMinimumSize from Number --> double (DG);
 * 17-Jan-2003 : Moved plot classes to separate package (DG);
 * 20-Jan-2003 : Removed unnecessary constructors (DG);
 * 26-Mar-2003 : Implemented Serializable (DG);
 * 09-Jul-2003 : Changed ColorBar from extending axis classes to enclosing them (DG);
 *
 */

package org.jfree.chart.axis;

import java.awt.BasicStroke;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.io.Serializable;

import org.jfree.chart.plot.ContourPlot;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.ui.ColorPalette;
import org.jfree.chart.ui.RainbowPalette;

/**
 * A color bar.
 *
 * @author David M. O'Donnell
 */
public class ColorBar implements Serializable {

    /** The default color bar thickness. */
    public static final int DEFAULT_COLORBAR_THICKNESS = 0;

    /** The default color bar thickness percentage. */
    public static final double DEFAULT_COLORBAR_THICKNESS_PERCENT = 0.10;

    /** The default outer gap. */
    public static final int DEFAULT_OUTERGAP = 2;

    /** The axis. */
    private ValueAxis axis;
    
    /** The color bar thickness. */
    private int colorBarThickness = DEFAULT_COLORBAR_THICKNESS;

    /** The color bar thickness as a percentage of the height of the data area. */
    private double colorBarThicknessPercent = DEFAULT_COLORBAR_THICKNESS_PERCENT;

    /** The color palette. */
    private ColorPalette colorPalette = null;

    /** The color bar length. */
    private int colorBarLength = 0; // default make height of plotArea

    /** The amount of blank space around the colorbar. */
    private int outerGap;

    /**
     * Tests this object for equality with another.
     * 
     * @param obj  the object to test against.
     * 
     * @return A boolean.
     */
    public boolean equals(Object obj) {

        if (obj == null) {
            return false;
        }

        if (obj == this) {
            return true;
        }

        if (obj instanceof ColorBar) {
            ColorBar cb = (ColorBar) obj;
            boolean b0 = this.axis.equals(cb.axis);
            boolean b1 = this.colorBarThickness == cb.colorBarThickness;
            boolean b2 = this.colorBarThicknessPercent == cb.colorBarThicknessPercent;
            boolean b3 = this.colorPalette.equals(cb.colorPalette);
            boolean b4 = this.colorBarLength == cb.colorBarLength;
            boolean b5 = this.outerGap == cb.outerGap;
            return b0 && b1 && b2 && b3 && b4 && b5;
        }
        
        return false;
        
    }
    /**
     * Constructs a horizontal colorbar axis, using default values where necessary.
     *
     * @param label  the axis label.
     */
    public ColorBar(String label) {
   
        NumberAxis a = new NumberAxis(label);
        a.setAutoRangeIncludesZero(false);
        this.axis = a;
        this.axis.setLowerMargin(0.0);
        this.axis.setUpperMargin(0.0);

        this.colorPalette = new RainbowPalette();
        this.colorBarThickness = DEFAULT_COLORBAR_THICKNESS;
        this.colorBarThicknessPercent = DEFAULT_COLORBAR_THICKNESS_PERCENT;
        this.outerGap = DEFAULT_OUTERGAP;
        this.colorPalette.setMinZ(this.axis.getRange().getLowerBound());
        this.colorPalette.setMaxZ(this.axis.getRange().getUpperBound());

    }

    public void configure(ContourPlot plot) {
        double minZ = plot.getContourDataset().getMinZValue();
        double maxZ = plot.getContourDataset().getMaxZValue();
        setMinimumValue(minZ);
        setMaximumValue(maxZ);
    }
    
    /**
     * Returns the axis.
     * 
     * @return The axis.
     */
    public ValueAxis getAxis() {
        return this.axis;
    }
    
    /**
     * Sets the axis.
     * 
     * @param axis  the axis.
     */
    public void setAxis(ValueAxis axis) {
        this.axis = axis;
    }
    
    /**
     * Rescales the axis to ensure that all data are visible.
     */
    public void autoAdjustRange() {
        this.axis.autoAdjustRange();
        this.colorPalette.setMinZ(this.axis.getMinimumAxisValue());
        this.colorPalette.setMaxZ(this.axis.getMaximumAxisValue());
    }

    /**
     * Draws the plot on a Java 2D graphics device (such as the screen or a printer).
     *
     * @param g2  the graphics device.
     * @param drawArea  the area within which the chart should be drawn.
     * @param dataArea  the area within which the plot should be drawn (a
     *                  subset of the drawArea).
     * @param location  the axis location.
     */
    public void draw(Graphics2D g2, Rectangle2D plotArea, Rectangle2D dataArea, 
                     Rectangle2D reservedArea, AxisLocation location) {


        Rectangle2D colorBarArea = null;
        
        double thickness = calculateBarThickness(dataArea, location);
        if (this.colorBarThickness > 0) {
            thickness = this.colorBarThickness;  // allow fixed thickness
        }

        double length = 0.0;
        if (AxisLocation.isLeftOrRight(location)) {
            length = dataArea.getHeight();
        }
        else {
            length = dataArea.getWidth();
        }
        
        if (this.colorBarLength > 0) {
            length = this.colorBarLength;
        }

        if (location == AxisLocation.BOTTOM) {
            colorBarArea = new Rectangle2D.Double(dataArea.getX(),
                                                  plotArea.getMaxY() + this.outerGap,
                                                  length, thickness);
        }
        else if (location == AxisLocation.TOP) {
            colorBarArea = new Rectangle2D.Double(dataArea.getX(),
                                                  reservedArea.getMinY() + this.outerGap,
                                                  length, thickness);            
        }
        else if (location == AxisLocation.LEFT) {
            colorBarArea = new Rectangle2D.Double(plotArea.getX() - thickness - this.outerGap ,
                                                  dataArea.getMinY(),
                                                  thickness, length);            
        }
        else if (location == AxisLocation.RIGHT) {
            colorBarArea = new Rectangle2D.Double(plotArea.getMaxX() + this.outerGap,
                                                  dataArea.getMinY(),
                                                  thickness, length);            
        }
        
        // update, but dont draw tick marks (needed for stepped colors)
        this.axis.refreshTicks(g2, plotArea, colorBarArea, location);

        drawColorBar(g2, colorBarArea, location);

        if (AxisLocation.isTopOrBottom(location)) {
            this.axis.draw(g2, reservedArea, colorBarArea, AxisLocation.BOTTOM);            
        }
        else {
            this.axis.draw(g2, reservedArea, colorBarArea, location);
        }
        
    }

    /**
     * Draws the plot on a Java 2D graphics device (such as the screen or a printer).
     *
     * @param g2  the graphics device.
     * @param colorBarArea  the area within which the axis should be drawn.
     */
    public void drawColorBar(Graphics2D g2, Rectangle2D colorBarArea, AxisLocation location) {

        Object antiAlias = g2.getRenderingHint(RenderingHints.KEY_ANTIALIASING);
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                            RenderingHints.VALUE_ANTIALIAS_OFF);

        // setTickValues was missing from ColorPalette v. 0.96
        colorPalette.setTickValues(this.axis.getTicks());

        Stroke strokeSaved = g2.getStroke();
        g2.setStroke(new BasicStroke(1.0f));

        if (AxisLocation.isTopOrBottom(location)) {
            double y1 = colorBarArea.getY();
            double y2 = colorBarArea.getMaxY();
            double xx = colorBarArea.getX();
            Line2D line = new Line2D.Double();
            while (xx <= colorBarArea.getMaxX()) {
                double value = this.axis.translateJava2DtoValue((float) xx, colorBarArea,
                                                                location);
                line.setLine(xx, y1, xx, y2);
                g2.setPaint(getPaint(value));
                g2.draw(line);
                xx += 1;
            }
        }
        else {
            double y1 = colorBarArea.getX();
            double y2 = colorBarArea.getMaxX();
            double xx = colorBarArea.getY();
            Line2D line = new Line2D.Double();
            while (xx <= colorBarArea.getMaxY()) {
                double value = this.axis.translateJava2DtoValue((float) xx, colorBarArea,
                                                                location);
                line.setLine(y1, xx, y2, xx);
                g2.setPaint(getPaint(value));
                g2.draw(line);
                xx += 1;
            }            
        }

        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, antiAlias);
        g2.setStroke(strokeSaved);

    }

    /**
     * Returns the color palette.
     *
     * @return the color palette.
     */
    public ColorPalette getColorPalette() {
        return colorPalette;
    }

    /**
     * Returns the Paint associated with a value.
     *
     * @param value  the value.
     *
     * @return the paint.
     */
    public Paint getPaint(double value) {
        return colorPalette.getPaint(value);
    }

    /**
     * Sets the color palette.
     *
     * @param palette  the new palette.
     */
    public void setColorPalette(ColorPalette palette) {
        this.colorPalette = palette;
    }

    /**
     * Sets the maximum value.
     *
     * @param value  the maximum value.
     */
    public void setMaximumValue(double value) {
        this.colorPalette.setMaxZ(value);
        this.axis.setMaximumAxisValue(value);
    }

    /**
     * Sets the minimum value.
     *
     * @param value  the minimum value.
     */
    public void setMinimumValue(double value) {
        this.colorPalette.setMinZ(value);
        this.axis.setMinimumAxisValue(value);
    }

    /**
     * Reserves the space required to draw the color bar.
     *
     * @param g2  the graphics device.
     * @param plot  the plot that the axis belongs to.
     * @param plotArea  the area within which the plot should be drawn.
     * @param location  the axis location.
     *
     * @return the height required to draw the axis in the specified draw area.
     */
    public AxisSpace reserveSpace(Graphics2D g2, Plot plot, Rectangle2D plotArea,
                                  Rectangle2D dataArea, AxisLocation location, AxisSpace space) {

        AxisSpace result = this.axis.reserveSpace(g2, plot, plotArea, location, space);

        double thickness = calculateBarThickness(dataArea, location);
        result.add(thickness + 2 * this.outerGap, location);
        return result;

    }
    
    private double calculateBarThickness(Rectangle2D plotArea, AxisLocation location) {
        double result = 0.0;
        if (AxisLocation.isLeftOrRight(location)) {
            result = plotArea.getWidth() * colorBarThicknessPercent;
        }
        else {
            result = plotArea.getHeight() * colorBarThicknessPercent;
        }
        return result;  
    }

    
}
