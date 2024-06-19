/* =======================================
 * JFreeChart : a Java Chart Class Library
 * =======================================
 *
 * Project Info:  http://www.jrefinery.com/jfreechart
 * Project Lead:  David Gilbert (david.gilbert@jrefinery.com);
 *
 * This file...
 * $Id: HighLow.java,v 1.1 2007/10/10 18:54:39 vauchers Exp $
 *
 * Original Author:  Andrzej Porebski;
 * Contributor(s):   David Gilbert;
 *
 * (C) Copyright 2000, 2001 by Andrzej Porebski;
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
 * Changes (from 18-Sep-2001)
 * --------------------------
 * 18-Sep-2001 : Added standard header and fixed DOS encoding problem (DG);
 * 17-Nov-2001 : Renamed HiLow --> HighLow (DG);
 *
 */

package com.jrefinery.chart;

import java.awt.*;
import java.awt.geom.*;

/**
 * Represents one point in the HighLow plot.
 */
public class HighLow {

    /** Useful constant for open/close value types. */
    public static final int OPEN = 0;

    /** Useful constant for open/close value types. */
    public static final int CLOSE = 1;

    /** The position of the line. */
    private Line2D line;

    /** The open value. */
    private double open;

    /** The close value. */
    private double close;

    /** The pen/brush used to draw the lines. */
    private Stroke stroke;

    /** The color used to draw the lines. */
    private Paint paint;

    /** The tick size. */
    private double tickSize = 2;

    /**
     * Constructs a high-low item, with default values for the open/close and colors.
     * @param x
     * @param high
     * @param low
     */
    public HighLow(double x, double high, double low) {
	this(x, high, low, high, low, new BasicStroke(), Color.blue);
    }

    /**
     * Constructs a high-low item, with default values for the colors.
     * @param x
     * @param high
     * @param low
     * @param open
     * @param close
     */
    public HighLow(double x, double high, double low, double open, double close) {
	this(x, high, low, open, close, new BasicStroke(), Color.blue);
    }

    /**
     * Constructs a high-low item.
     * @param x
     * @param high
     * @param low
     * @param open
     * @param close
     * @param stroke
     * @param paint
     */
    public HighLow(double x, double high, double low, double open, double close,
		   Stroke stroke, Paint paint) {

        this.line = new Line2D.Double(x, high, x, low);
	this.open = open;
	this.close = close;
	this.stroke = stroke;
	this.paint = paint;

    }

    /**
     * Sets the width of the open/close tick.
     * @param newSize
     */
    public void setTickSize(double newSize) {
	tickSize = newSize;
    }

    /**
     * Returns the width of the open/close tick.
     */
    public double getTickSize() {
	return tickSize;
    }

    /**
     * Returns the line.
     */
    public Line2D getLine() {
	return line;
    }

    /**
     * Returns either OPEN or Close value depending on the valueType.
     * @param valueType
     */
    public double getValue(int valueType) {
	if (valueType == OPEN)
	    return open;
	else
	    return close;
    }

    /**
     * Sets either OPEN or Close value depending on the valueType.
     * @param valueType
     * @param newValue
     */
    public void setValue(int valueType, double newValue) {
	if (valueType == OPEN)
	    open = newValue;
	else
	    close = newValue;
    }

    /**
     * Returns the line for open tick.
     */
    public Line2D getOpenTickLine() {
	return getTickLine(getLine().getX1(), getValue(OPEN), (-1) * getTickSize());
    }

    /**
     * Returns the line. for close tick
     */
    public Line2D getCloseTickLine() {
	return getTickLine(getLine().getX1(), getValue(CLOSE), getTickSize());
    }

    /**
     * ??.
     */
    private Line2D getTickLine(double x, double value, double width) {
	return new Line2D.Double(x,value,x + width,value);
    }

    /**
     * Returns the Stroke object used to draw the line.
     */
    public Stroke getStroke() {
	return stroke;
    }

    /**
     * Returns the Paint object used to color the line.
     */
    public Paint getPaint() {
	return paint;
    }

}
