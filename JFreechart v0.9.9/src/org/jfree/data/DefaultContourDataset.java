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
 * --------------------------
 * DefaultContourDataset.java
 * --------------------------
 * (C) Copyright 2002, 2003, by David M. O'Donnell and Contributors.
 *
 * Original Author:  David M. O'Donnell;
 * Contributor(s):   David Gilbert (for Object Refinery Limited);
 *
 * $Id: DefaultContourDataset.java,v 1.1 2007/10/10 20:07:41 vauchers Exp $
 *
 * Changes (from 23-Jan-2003)
 * --------------------------
 * 23-Jan-2003 : Added standard header (DG);
 * 20-May-2003 : removed member vars numX and numY, which were never used (TM)
 */

package org.jfree.data;

import java.util.Arrays;
import java.util.Date;
import java.util.Vector;

/**
 * A convenience class that provides a default implementation of the {@link ContourDataset}
 * interface.
 *
 * @author David M. O'Donnell
 */
public class DefaultContourDataset extends AbstractDataset implements ContourDataset {

    /** The series name (this dataset supports only one series). */
    protected String seriesName = null;

    /** Storage for the x values. */
    protected Number[] xValues = null;

    /** Storage for the y values. */
    protected Number[] yValues = null;

    /** Storage for the z values. */
    protected Number[] zValues = null;

    /** The index for the start of each column in the data. */
    protected int xIndex[] = null;

    /** Flags that track whether x, y and z are dates. */
    boolean[] dateAxis = new boolean[3];

    /**
     * Creates a new dataset, initially empty.
     */
    public DefaultContourDataset() {
    }

    /**
     * Constructs a new dataset with the given data.
     *
     * @param seriesName  the series name.
     * @param xData  the x values.
     * @param yData  the y values.
     * @param zData  the z values.
     */
    public DefaultContourDataset(String seriesName,
                                 Object[] xData,
                                 Object[] yData,
                                 Object[] zData) {

        this.seriesName = seriesName;
        initialize(xData, yData, zData);
    }

    public void initialize(Object[] xData,
                            Object[] yData,
                            Object[] zData) {

        xValues = new Double[xData.length];
        yValues = new Double[yData.length];
        zValues = new Double[zData.length];

       // We organise the data with the following assumption:
       // 1) the data are sorted by x then y
       // 2) that the data will be represented by a rectangle formed by
       //    using x[i+1], x, y[j+1], and y.
       // 3) we march along the y-axis at the same value of x until a new value x is
       //    found at which point we will flag the index where x[i+1]<>x[i]

        Vector tmpVector = new Vector(); //create a temporary vector
        double x = 1.123452e31; // set x to some arbitary value (used below)
        for (int k = 0; k < xValues.length; k++) {
            if (xData[k] != null) {
                Number xNumber = null;
                if (xData[k] instanceof Number) {
                    xNumber = (Number) xData[k];
                }
                else if (xData[k] instanceof Date) {
                    dateAxis[0] = true;
                    Date xDate = (Date) xData[k];
                    xNumber = new Long(xDate.getTime()); //store data as Long
                }
                else {
                    xNumber = new Integer(0);
                }
                xValues[k] = new Double(xNumber.doubleValue()); // store Number as Double

                // check if starting new column
                if (x != xValues[k].doubleValue()) {
                    tmpVector.add(new Integer(k)); //store index where new column starts
                    x = xValues[k].doubleValue(); // set x to most recent value
                }
            }
        }

        Object[] inttmp = tmpVector.toArray();
        xIndex = new int[inttmp.length];  // create array xIndex to hold new column indices

        for (int i = 0; i < inttmp.length; i++) {
            xIndex[i] = ((Integer) inttmp[i]).intValue();
        }
        for (int k = 0; k < yValues.length; k++) { // store y and z axes as Doubles
            yValues[k] = (Double) yData[k];
            if (zData[k] != null) {
                zValues[k] = (Double) zData[k];
            }
        }
    }

    /**
     * Creates an object array from an array of doubles.
     *
     * @param data  the data.
     *
     * @return An array of <code>Double</code> objects.
     */
    public static Object[][] formObjectArray(double[][] data) {
        Object[][] object = new Double[data.length][data[0].length];

        for (int i = 0; i < object.length; i++) {
            for (int j = 0; j < object[i].length; j++) {
                object[i][j] = new Double(data[i][j]);
            }
        }
        return object;
    }

    /**
     * Creates an object array from an array of doubles.
     *
     * @param data  the data.
     *
     * @return An array of <code>Double</code> objects.
     */
    public static Object[] formObjectArray(double[] data) {

        Object[] object = new Double[data.length];
        for (int i = 0; i < object.length; i++) {
            object[i] = new Double(data[i]);
        }
        return object;
    }

    /**
     * Returns the number of items in the specified series.
     * <P>
     * Method provided to satisfy the {@link XYDataset} interface implementation.
     *
     * @param series  must be zero, as this dataset only supports one series.
     *
     * @return the item count.
     */
    public int getItemCount(int series) {
        if (series > 0) {
            System.out.println("Only one series for contour");
        }
        return zValues.length;
    }

    /**
     * Returns the maximum z-value.
     *
     * @return The maximum z-value.
     */
    public double getMaxZValue() {
        double zMax = -1.e20;
        for (int k = 0; k < zValues.length; k++) {
            if (zValues[k] != null) {
                zMax = Math.max(zMax, zValues[k].doubleValue());
            }
        }
        return zMax;
    }

    /**
     * Returns the minimum z-value.
     *
     * @return The minimum z-value.
     */
    public double getMinZValue() {

        double zMin = 1.e20;
        for (int k = 0; k < zValues.length; k++) {
            if (zValues[k] != null) {
                zMin = Math.min(zMin, zValues[k].doubleValue());
            }
        }
        return zMin;
    }

    /**
     * Returns the maximum z-value within visible region of plot.
     *
     * @param x  the x range.
     * @param y  the y range.
     *
     * @return The z range.
     */
    public Range getZValueRange(Range x, Range y) {

        double minX = x.getLowerBound();
        double minY = y.getLowerBound();
        double maxX = x.getUpperBound();
        double maxY = y.getUpperBound();

        double zMin = 1.e20;
        double zMax = -1.e20;
        for (int k = 0; k < zValues.length; k++) {
            if (xValues[k].doubleValue() >= minX
                && xValues[k].doubleValue() <= maxX
                && yValues[k].doubleValue() >= minY
                && yValues[k].doubleValue() <= maxY) {
                if (zValues[k] != null) {
                    zMin = Math.min(zMin, zValues[k].doubleValue());
                    zMax = Math.max(zMax, zValues[k].doubleValue());
                }
            }
        }

        return new Range(zMin, zMax);
    }

    /**
     * Returns the minimum z-value.
     *
     * @param minX  the minimum x value.
     * @param minY  the minimum y value.
     * @param maxX  the maximum x value.
     * @param maxY  the maximum y value.
     *
     * @return the minimum z-value.
     */
    public double getMinZValue(double minX, double minY, double maxX, double maxY) {

        double zMin = 1.e20;
        for (int k = 0; k < zValues.length; k++) {
            if (zValues[k] != null)
            zMin = Math.min(zMin, zValues[k].doubleValue());
        }
        return zMin;

    }

    /**
     * Returns the number of series.
     * <P>
     * Required by XYDataset interface (this will always return 1)
     *
     * @return 1.
     */
    public int getSeriesCount() {
        return 1;
    }

    /**
     * Returns the name of the specified series.
     *
     * Method provided to satisfy the XYDataset interface implementation
     *
     * @param series must be zero.
     *
     * @return the series name.
     */
    public String getSeriesName(int series) {
        if (series > 0) {
            System.out.println("Only one series for contour");
        }
        return seriesName;
    }

    /**
     * Returns the index of the xvalues.
     *
     * @return The x values.
     */
    public int[] getXIndices() {
        return xIndex;
    }

    /**
     * Returns the x values.
     *
     * @return The x values.
     */
    public Number[] getXValues() {
        return xValues;
    }

    /**
     * Returns the x value for the specified series and index (zero-based indices).
     * Required by the XYDataset
     *
     * @param series  must be zero;
     * @param item  the item index (zero-based).
     *
     * @return The x value.
     */
    public Number getXValue(int series, int item) {
        if (series > 0) {
            System.out.println("Only one series for contour");
        }
        return xValues[item];
    }

    /**
     * Returns an x value.
     *
     * @param item  the item index (zero-based).
     *
     * @return The X value.
     */
    public Number getXValue(int item) {
        return xValues[item];
    }

    /**
     * Returns a Number array containing all y values.
     *
     * @return The Y values.
     */
    public Number[] getYValues() {
        return yValues;
    }

    /**
     * Returns the y value for the specified series and index (zero-based indices).
     * Required by the XYDataset
     *
     * @param series  the series index (must be zero for this dataset).
     * @param item  the item index (zero-based).
     *
     * @return The Y value.
     */
    public Number getYValue(int series, int item) {
        if (series > 0) {
            System.out.println("Only one series for contour");
        }
        return yValues[item];
    }

    /**
     * Returns a Number array containing all z values.
     *
     * @return The Z values.
     */
    public Number[] getZValues() {
        return zValues;
    }

    /**
     * Returns the z value for the specified series and index (zero-based indices).
     * Required by the XYDataset
     *
     * @param series  the series index (must be zero for this dataset).
     * @param item  the item index (zero-based).
     *
     * @return The Z value.
     */
    public Number getZValue(int series, int item) {
        if (series > 0) {
            System.out.println("Only one series for contour");
        }
        return zValues[item];
    }

    /**
     * Returns an int array contain the index into the x values.
     *
     * @return The X values.
     */
    public int[] indexX() {
        int[] index = new int[xValues.length];
        for (int k = 0; k < index.length; k++) {
            index[k] = indexX(k);
        }
        return index;
    }

    /**
     * Given index k, returns the column index containing k.
     *
     * @param k index of interest.
     *
     * @return The column index.
     */
    public int indexX(int k) {
        int i = Arrays.binarySearch(xIndex, k);
        if (i >= 0) {
            return i;
        } else {
            return -1 * i - 2;
        }
    }


    /**
     * Given index k, return the row index containing k.
     *
     * @param k index of interest.
     *
     * @return The row index.
     */
    public int indexY(int k) { // this may be obsolete (not used anywhere)
        return (k / xValues.length);
    }

    /**
     * Given column and row indices, returns the k index.
     *
     * @param i index of along x-axis.
     * @param j index of along y-axis.
     *
     * @return The Z index.
     */
    public int indexZ(int i, int j) {
        return xValues.length * j + i;
    }

    /**
     * Returns true if axis are dates
     * @param axisNumber The axis where 0-x, 1-y, and 2-z.
     */
    public boolean isDateAxis(int axisNumber) {
        if (axisNumber < 0 || axisNumber > 2) {
            return false; // bad axisNumber
        }
        return dateAxis[axisNumber];
    }

    /**
     * Sets the names of the series in the data source.
     *
     * @param seriesNames The names of the series in the data source.
     */
    public void setSeriesNames(String[] seriesNames) {
        if (seriesNames.length > 1) {
            System.out.println("Contours only support one series");
        }
        seriesName = seriesNames[0];
        fireDatasetChanged();
    }

}
