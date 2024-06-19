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
 * PaintTable.java
 * ---------------
 * (C) Copyright 2002, 2003 by Simba Management Limited.
 *
 * Original Author:  David Gilbert (for Simba Management Limited);
 * Contributor(s):   -;
 *
 * $Id: PaintTable.java,v 1.1 2007/10/10 19:57:50 vauchers Exp $
 *
 * Changes (since 8-Jan-2003)
 * --------------------------
 * 08-Jan-2002 : Added standard header and Javadocs (DG);
 * 16-Jan-2003 : Changed to class, and moved to com.jrefinery.chart.renderer (DG);
 * 14-Feb-2003 : Fixed bug in setPaint() method (DG);
 *
 */

package com.jrefinery.chart.renderer;

import java.awt.Paint;

/**
 * A lookup table for <code>Paint</code> objects.
 * 
 * @author David Gilbert
 */
public class PaintTable {
       
    /** The number of rows. */
    private int rows;
    
    /** The number of columns. */
    private int columns;
    
    /** An array of <code>Paint</code> objects.  The array may contain <code>null</code> values. */
    private Paint[][] data;
         
    /**
     * Creates a new paint table.
     */
    public PaintTable() {
        this.rows = 0;
        this.columns = 0;
        this.data = new Paint[0][0];
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
     * Returns the paint object from a particular cell in the table.
     * 
     * @param row  the row index (zero-based).
     * @param column  the column index (zero-based).
     * 
     * @return The paint.
     */
    public Paint getPaint(int row, int column) {
        
        Paint result = null;
        if (row < this.data.length) {
            Paint[] current = this.data[row];
            if (column < current.length) {
                result = current[column];
            }
        }
        return result;
        
    }

    /**
     * Sets the paint for a cell in the table.  The table is expanded if necessary.
     * 
     * @param row  the row index (zero-based).
     * @param column  the column index (zero-based).
     * @param paint  the paint.
     */    
    public void setPaint(int row, int column, Paint paint) {
        // does this increase the number of rows?  if yes, create new storage
        if (row >= this.data.length) {
            Paint[][] enlarged = new Paint[row + 1][];
            for (int i = 0; i < this.data.length; i++) {
                enlarged[i] = this.data[i];
            }
            for (int j = this.data.length; j <= row; j++) {
                enlarged[j] = new Paint[0];
            }
            this.data = enlarged;
        }

        // does this increase the current row?
        Paint[] current = this.data[row];
        if (column >= current.length) {
            Paint[] enlarged = new Paint[column + 1];
            for (int i = 0; i < current.length; i++) {
                enlarged[i] = current[i];
            }
            enlarged[column] = paint;
            this.data[row] = enlarged;
        }
        else {
            current[column] = paint;
        }
             
    }
    
}

