/* =======================================
 * JFreeChart : a Java Chart Class Library
 * =======================================
 *
 * Project Info:  http://www.jrefinery.com/jfreechart;
 * Project Lead:  David Gilbert (david.gilbert@jrefinery.com);
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
 * -----------
 * Spacer.java
 * -----------
 * (C) Copyright 2002, by Simba Management Limited.
 *
 * Original Author:  David Gilbert (for Simba Management Limited);
 * Contributor(s):   -;
 *
 * $Id: Spacer.java,v 1.1 2007/10/10 18:57:06 vauchers Exp $
 *
 * Changes
 * -------
 * 07-Feb-2002 : Version 1 (DG);
 *
 */

package com.jrefinery.chart;

/**
 * Represents an amount of blank space inside (or sometimes outside) a rectangle.  This class is
 * similar in function to the Insets class, but allows for the space to be specified in relative
 * terms as well as absolute terms.
 * <P>
 * Immutable.
 */
public class Spacer implements Cloneable {

    public static final int RELATIVE = 0;

    public static final int ABSOLUTE = 1;

    protected int type;

    protected double left;

    protected double top;

    protected double right;

    protected double bottom;

    public Spacer(int type, double left, double top, double right, double bottom) {

        this.type = type;
        this.left = left;
        this.top = top;
        this.right = right;
        this.bottom = bottom;

    }

    public double getLeftSpace(double width) {

        double result = 0.0;

        if (type==ABSOLUTE) {
            result = left;
        }
        else if (type==RELATIVE) {
            result = left*width;
        }

        return result;

    }

    public double getRightSpace(double width) {

        double result = 0.0;

        if (type==ABSOLUTE) {
            result = right;
        }
        else if (type==RELATIVE) {
            result = right*width;
        }

        return result;

    }

    public double getTopSpace(double height) {

        double result = 0.0;

        if (type==ABSOLUTE) {
            result = top;
        }
        else if (type==RELATIVE) {
            result = top*height;
        }

        return result;

    }

    public double getBottomSpace(double height) {

        double result = 0.0;

        if (type==ABSOLUTE) {
            result = bottom;
        }
        else if (type==RELATIVE) {
            result = bottom*height;
        }

        return result;

    }

    /**
     * Returns the width after adding the left and right spacing amounts.
     */
    public double getAdjustedWidth(double width) {

        double result = width;

        if (type==ABSOLUTE) {
            result = result + left + right;
        }
        else if (type==RELATIVE) {
            result = result + (left*width) + (right*width);
        }

        return result;

    }

    /**
     * Returns the height after adding the top and bottom spacing amounts.
     */
    public double getAdjustedHeight(double height) {

        double result = height;

        if (type==ABSOLUTE) {
            result = result + top + bottom;
        }
        else if (type==RELATIVE) {
            result = result + (top*height) + (bottom*height);
        }

        return result;

    }

}