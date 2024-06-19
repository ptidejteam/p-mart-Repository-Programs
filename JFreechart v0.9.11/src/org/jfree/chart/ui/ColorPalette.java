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
 * -----------------
 * ColorPalette.java
 * -----------------
 * (C) Copyright 2002, 2003, by David M. O'Donnell and Contributors.
 *
 * Original Author:  David M. O'Donnell;
 * Contributor(s):   David Gilbert (for Object Refinery Limited);
 *
 * $Id: ColorPalette.java,v 1.1 2007/10/10 19:09:23 vauchers Exp $
 *
 * Changes
 * -------
 * 26-Nov-2002 : Version 1 contributed by David M. O'Donnell (DG);
 * 26-Mar-2003 : Implemented Serializable (DG);
 *
 */

package org.jfree.chart.ui;

import java.awt.Color;
import java.awt.Paint;
import java.io.Serializable;
import java.util.Arrays;

import org.jfree.chart.axis.Tick;

/**
 * Defines palette used in Contour Plots.
 *
 * @author David M. O'Donnell.
 */
public abstract class ColorPalette implements Serializable {

    /** The min z-axis value. */
    protected double minZ = -1;

    /** The max z-axis value. */
    protected double maxZ = -1;

    /** Red components. */
    protected int[] r;

    /** Green components. */
    protected int[] g;

    /** Blue components. */
    protected int[] b;

    /** Tick values are stored for use with stepped palette. */
    protected double[] tickValues = null;

    /** Logscale? */
    protected boolean logscale = false;

    /** Inverse palette (ie, min and max colors are reversed). */
    protected boolean inverse = false;

    /** The palette name. */
    protected String paletteName = null;

    /** Controls whether palette colors are stepped (not continuous). */
    protected boolean stepped = false;

    /** Constant for converting loge to log10. */
    protected static final double log10 = Math.log(10);

    /**
     * Default contructor.
     */
    public ColorPalette() {
    }

    /**
     * Returns the color associated with a value.
     *
     * @param value  the value.
     *
     * @return the color.
     */
    public Paint getColor(double value) {
        int izV = (int) (253 * (value - minZ) / (maxZ - minZ)) + 2;
        return new Color(r[izV], g[izV], b[izV]);
    }

    /**
     * Returns a color.
     *
     * @param izV  ??.
     *
     * @return the color.
     */
    public Color getColor(int izV) {
        return new Color(r[izV], g[izV], b[izV]);
    }

    /**
     * Returns Color by mapping a given value to a linear palette.
     *
     * @param value  the value.
     *
     * @return The color.
     */
    public Color getColorLinear(double value) {
        int izV = 0;
        if (stepped) {
            int index = Arrays.binarySearch(tickValues, value);
            if (index < 0) {
                index = -1 * index - 2;
            }

            if (index < 0) { // For the case were the first tick is greater than minZ
                value = minZ;
            }
            else {
                value = tickValues[index];
            }
        }
        izV = (int) (253 * (value - minZ) / (maxZ - minZ)) + 2;
        izV = Math.min(izV, 255);
        izV = Math.max(izV, 2);
        return getColor(izV);
    }

    /**
     * Returns Color by mapping a given value to a common log palette.
     *
     * @param value  the value.
     *
     * @return The color.
     */
    public Color getColorLog(double value) {
        int izV = 0;
        double minZtmp = minZ;
        double maxZtmp = maxZ;
        if (minZ <= 0.0) {
//          negatives = true;
            maxZ = maxZtmp - minZtmp + 1;
            minZ = 1;
            value = value - minZtmp + 1;
        }
        double minZlog = Math.log(minZ) / log10;
        double maxZlog = Math.log(maxZ) / log10;
        value = Math.log(value) / log10;
        //  value = Math.pow(10,value);
        if (stepped) {
            int numSteps = tickValues.length;
            int steps = 256 / (numSteps - 1);
            izV = steps * (int) (numSteps * (value - minZlog) / (maxZlog - minZlog)) + 2;
            //  izV = steps*numSteps*(int)((value/minZ)/(maxZlog-minZlog)) + 2;
        }
        else {
            izV = (int) (253 * (value - minZlog) / (maxZlog - minZlog)) + 2;
        }
        izV = Math.min(izV, 255);
        izV = Math.max(izV, 2);

        minZ = minZtmp;
        maxZ = maxZtmp;

        return getColor(izV);
    }

    /**
     * Returns the maximum Z value.
     *
     * @return the value.
     */
    public double getMaxZ() {
        return maxZ;
    }

    /**
     * Returns the minimum Z value.
     *
     * @return the value.
     */
    public double getMinZ() {
        return minZ;
    }

    /**
     * Returns Paint by mapping a given value to a either a linear or common log palette
     * as controlled by the value logscale.
     *
     * @param value  the value.
     *
     * @return The paint.
     */
    public Paint getPaint(double value) {
        if (isLogscale()) {
            return getColorLog(value);
        }
        else {
            return getColorLinear(value);
        }
    }

    /**
     * Returns the palette name.
     *
     * @return the palette name.
     */
    public String getPaletteName () {
        return paletteName;
    }

    /**
     * Returns the tick values.
     *
     * @return the tick values.
     */
    public double[] getTickValues() {
        return tickValues;
    }

    /**
     * Called to initialize the palette's color indexes
     */
    public abstract void initialize();

    /**
     * Inverts Palette
     */
    public void invertPalette() {

        int[] red = new int[256];
        int[] green = new int[256];
        int[] blue = new int[256];
        for (int i = 0; i < 256; i++) {
            red[i] = r[i];
            green[i] = g[i];
            blue[i] = b[i];
        }

        for (int i = 2; i < 256; i++) {
            r[i] = red[257 - i];
            g[i] = green[257 - i];
            b[i] = blue[257 - i];
        }
    }

    /**
     * Returns the inverse flag.
     *
     * @return the flag.
     */
    public boolean isInverse () {
        return inverse;
    }

    /**
     * Returns the log-scale flag.
     *
     * @return the flag.
     */
    public boolean isLogscale() {
        return logscale;
    }

    /**
     * Returns the 'is-stepped' flag.
     *
     * @return the flag.
     */
    public boolean isStepped () {
        return stepped;
    }

    /**
     * Sets the inverse flag.
     *
     * @param inverse  the new value.
     */
    public void setInverse (boolean inverse) {
        this.inverse = inverse;
        initialize();
        if (inverse) {
            invertPalette();
        }
        return;
    }

    /**
     * Sets the 'log-scale' flag.
     *
     * @param logscale  the new value.
     */
    public void setLogscale(boolean logscale) {
        this.logscale = logscale;
    }

    /**
     * Sets the maximum Z value.
     *
     * @param newMaxZ  the new value.
     */
    public void setMaxZ(double newMaxZ) {
        maxZ = newMaxZ;
    }

    /**
     * Sets the minimum Z value.
     *
     * @param newMinZ  the new value.
     */
    public void setMinZ(double newMinZ) {
        minZ = newMinZ;
    }

    /**
     * Sets the palette name.
     *
     * @param paletteName  the name.
     */
    public void setPaletteName (String paletteName) {
        //String oldValue = this.paletteName;
        this.paletteName = paletteName;
        return;
    }

    /**
     * Sets the stepped flag.
     *
     * @param stepped  the flag.
     */
    public void setStepped (boolean stepped) {
        this.stepped = stepped;
        return;
    }

    /**
     * Sets the tick values.
     *
     * @param newTickValues  the tick values.
     */
    public void setTickValues(double[] newTickValues) {
        tickValues = newTickValues;
    }

    /**
     * Store ticks. Required when doing stepped axis
     *
     * @param ticks  the ticks.
     */
    public void setTickValues(java.util.List ticks) {
        tickValues = new double[ticks.size()];
        for (int i = 0;i < tickValues.length; i++) {
            tickValues[i] = ((Tick) ticks.get(i)).getNumericalValue();
        }
    }

}
