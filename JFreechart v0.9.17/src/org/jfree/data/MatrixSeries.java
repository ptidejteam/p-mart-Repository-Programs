/* ===========================================================
 * JFreeChart : a free chart library for the Java(tm) platform
 * ===========================================================
 *
 * (C) Copyright 2000-2004, by Object Refinery Limited and Contributors.
 *
 * Project Info:  http://www.jfree.org/jfreechart/index.html
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
 * [Java is a trademark or registered trademark of Sun Microsystems, Inc. 
 * in the United States and other countries.]
 *
 * -----------------
 * MatrixSeries.java
 * -----------------
 * (C) Copyright 2003, 2004, by Barak Naveh and Contributors.
 *
 * Original Author:  Barak Naveh;;
 * Contributor(s):   David Gilbert (for Object Refinery Limited);
 *
 * $Id: MatrixSeries.java,v 1.1 2007/10/10 19:29:13 vauchers Exp $
 *
 * Changes
 * -------
 * 10-Jul-2003 : Version 1 contributed by Barak Naveh (DG);
 * 10-Feb-2004 : Fixed Checkstyle complaints (DG);
 *
 */

package org.jfree.data;

import java.io.Serializable;

/**
 * Represents a dense matrix M[i,j] where each Mij item of the matrix has a
 * value (default is 0).
 *
 * @author Barak Naveh
 */
public class MatrixSeries extends Series implements Serializable {
    
    /** Series matrix values */
    protected double[][] data;

    /**
     * Constructs a new matrix series.
     *
     * <p>
     * By default, all matrix items are initialzed to 0.
     * </p>
     *
     * @param name  series name.
     * @param rows  the number of rows.
     * @param columns  the number of columns.
     */
    public MatrixSeries(String name, int rows, int columns) {
        super(name);
        this.data = new double[rows][columns];
        zeroAll();
    }

    /**
     * Returns the number of columns in this matrix series.
     *
     * @return the number of columns in this matrix series.
     */
    public int getColumnsCount() {
        return this.data[0].length;
    }


    /**
     * Return the matrix item at the specified index.
     *
     * @param itemIndex item index.
     *
     * @return matrix item at the specified index.
     */
    public Number getItem(int itemIndex) {
        int    i = getItemRow(itemIndex);
        int    j = getItemColumn(itemIndex);

        Number n = new Double(get(i, j));

        return n;
    }


    /**
     * Returns the column of the specified item.
     *
     * @param itemIndex the index of the item.
     *
     * @return the column of the specified item.
     */
    public int getItemColumn(int itemIndex) {
        //assert itemIndex >= 0 && itemIndex < getItemCount();

        return itemIndex % getRowCount();
    }


    /**
     * Returns the number of items in the series.
     *
     * @return The item count.
     */
    public int getItemCount() {
        return getRowCount() * getColumnsCount();
    }


    /**
     * Returns the row of the specified item.
     *
     * @param itemIndex the index of the item.
     *
     * @return the row of the specified item.
     */
    public int getItemRow(int itemIndex) {
        //assert itemIndex >= 0 && itemIndex < getItemCount();

        return itemIndex / getRowCount();
    }


    /**
     * Returns the number of rows in this matrix series.
     *
     * @return the number of rows in this matrix series.
     */
    public int getRowCount() {
        return this.data.length;
    }


    /**
     * Returns the value of the specified item in this matrix series.
     *
     * @param i the row of the item.
     * @param j the column of the item.
     *
     * @return the value of the specified item in this matrix series.
     */
    public double get(int i, int j) {
        return this.data[i][j];
    }


    /**
     * Updates the value of the specified item in this matrix series.
     *
     * @param i the row of the item.
     * @param j the column of the item.
     * @param mij the new value for the item.
     */
    public void update(int i, int j, double mij) {
        this.data[i][j] = mij;

        fireSeriesChanged();
    }


    /**
     * Sets all matrix values to zero.
     */
    public void zeroAll() {
        double[][] data    = this.data;
        int        rows    = getRowCount();
        int        columns = getColumnsCount();

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                data[i][j] = 0.0;
            }
        }

        fireSeriesChanged();
    }
}
