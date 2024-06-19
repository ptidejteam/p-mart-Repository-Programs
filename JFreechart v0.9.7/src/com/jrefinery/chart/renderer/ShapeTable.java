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
 * ---------------
 * ShapeTable.java
 * ---------------
 * (C) Copyright 2003, by Simba Management Limited.
 *
 * Original Author:  David Gilbert (for Simba Management Limited);
 * Contributor(s):   -;
 *
 * $Id: ShapeTable.java,v 1.1 2007/10/10 20:00:08 vauchers Exp $
 *
 * Changes
 * -------
 * 27-Jan-2002 : Version 1 (DG);
 * 14-Feb-2003 : Fixed bug in setShape() method (DG);
 *
 */

package com.jrefinery.chart.renderer;

import java.awt.Shape;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import com.jrefinery.io.SerialUtilities;

/**
 * A lookup table for <code>Shape</code> objects.
 * 
 * @author David Gilbert
 */
public class ShapeTable implements Serializable {
       
    /** The number of rows. */
    private int rows;
    
    /** The number of columns. */
    private int columns;
    
    /** An array of <code>Shape</code> objects.  The array may contain <code>null</code> values. */
    private transient Shape[][] data;
         
    /**
     * Creates a new shape table.
     */
    public ShapeTable() {
        this.rows = 0;
        this.columns = 0;
        this.data = new Shape[0][0];
    }
        
    /**
     * Returns the number of rows in the table.
     * 
     * @return The row count.
     */
    public int getRowCount() {
        return this.rows;
    }

    /**
     * Returns the number of columns in the table.
     * 
     * @return The column count.
     */
    public int getColumnCount() {
        return this.columns;
    }

    /**
     * Returns the shape object from a particular cell in the table.
     * 
     * @param row  the row index (zero-based).
     * @param column  the column index (zero-based).
     * 
     * @return The shape.
     */
    public Shape getShape(int row, int column) {
        
        Shape result = null;
        if (row < this.data.length) {
            Shape[] current = this.data[row];
            if (column < current.length) {
                result = current[column];
            }
        }
        return result;
        
    }
    
    /**
     * Sets the shape for an item.
     * 
     * @param row  the row index (zero-based).
     * @param column  the column index (zero-based).
     * @param shape  the shape.
     */
    public void setShape(int row, int column, Shape shape) {
        // does this increase the number of rows?  if yes, create new storage
        if (row >= this.data.length) {
            Shape[][] enlarged = new Shape[row + 1][];
            for (int i = 0; i < this.data.length; i++) {
                enlarged[i] = this.data[i];
            }
            for (int j = this.data.length; j <= row; j++) {
                enlarged[j] = new Shape[0];
            }
            this.data = enlarged;
        }

        // does this increase the current row?
        Shape[] current = this.data[row];
        if (column >= current.length) {
            Shape[] enlarged = new Shape[column + 1];
            for (int i = 0; i < current.length; i++) {
                enlarged[i] = current[i];
            }
            enlarged[column] = shape;
            this.data[row] = enlarged;
        }
        else {
            current[column] = shape;
        }
             
    }
    
    /**
     * Tests this shape table for equality with another object (typically also a shape table).
     * 
     * @param o  the other object.
     * 
     * @return A boolean.
     */
    public boolean equals(Object o) {
        
        if (o == null) {
            return false;
        }
        
        if (this == o) {
            return true;
        }
    
        if (o instanceof ShapeTable) {
            ShapeTable pt = (ShapeTable) o;
            boolean result = (this.rows == pt.getRowCount()) 
                              && (this.columns == pt.getColumnCount());
            for (int r = 0; r < this.rows; r++) {
                int columnCount = this.data[r].length;
                for (int c = 0; c < columnCount; c++) {
                    result = result && this.data[r][c].equals(pt.getShape(r, c));
                }
            }
            return result;
        }    
        
        return false;
        
    }
    
    /**
     * Handles serialization.
     * 
     * @param stream  the output stream.
     * 
     * @throws IOException if there is an I/O error.
     */
    private void writeObject(ObjectOutputStream stream) throws IOException {
        stream.defaultWriteObject();
        for (int r = 0; r < this.rows; r++) {
            int columnCount = this.data[r].length;
            stream.writeInt(columnCount);
            for (int c = 0; c < columnCount; c++) {
                SerialUtilities.writeShape(this.data[r][c], stream);
            }
        }
    }

    /**
     * Restores a serialized object.
     * 
     * @param stream  the input stream.
     * 
     * @throws IOException if there is an I/O error.
     * @throws ClassNotFoundException if there is a problem loading a class.
     */    
    private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
        stream.defaultReadObject();
        this.data = new Shape[this.rows][this.columns];
        for (int r = 0; r < this.rows; r++) {
            int columnCount = stream.readInt();
            for (int c = 0; c < columnCount; c++) {
                Shape shape = SerialUtilities.readShape(stream);
                this.data[r][c] = shape;
            }
        }
    }
    
}


