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
 * ObjectTable.java
 * ----------------
 * (C) Copyright 2003 by Object Refinery Limited.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited);
 * Contributor(s):   -;
 *
 * $Id: ObjectTable.java,v 1.1 2007/10/10 20:07:35 vauchers Exp $
 *
 * Changes
 * -------
 * 29-Apr-2003 : Version 1, based on PaintTable class (DG);
 * 21-May-2003 : Copied the array based implementation of StrokeTable and
 *               fixed the serialisation behaviour (TM).
 */

package org.jfree.chart.renderer;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OptionalDataException;
import java.io.Serializable;

import org.jfree.util.ObjectUtils;

/**
 * A lookup table for objects.
 *
 * @author David Gilbert
 */
public class ObjectTable implements Serializable {

  /** The number of rows. */
  private int rows;

  /** The number of columns. */
  private int columns;

  /** An array of <code>Stroke</code> objects.  The array may contain <code>null</code> values. */
  private transient Object[][] data;
    /**
     * Creates a new table.
     */
    public ObjectTable() {
      this.rows = 0;
      this.columns = 0;
      this.data = new Object[0][];
    }

    /**
     * Returns the number of rows in the table.
     *
     * @return The row count.
     */
    public int getRowCount() {
        return rows;
    }

    /**
     * Returns the number of columns in the table.
     *
     * @return The column count.
     */
    public int getColumnCount() {
        return columns;
    }

    /**
     * Returns the object from a particular cell in the table.
     * Returns null, if there is no object at the given position.
     *
     * @param row  the row index (zero-based).
     * @param column  the column index (zero-based).
     *
     * @return The object.
     * @throws IndexOutOfBoundsException if row or column is negative.
     */
    protected Object getObject(int row, int column) {

      Object result = null;
      if (row < this.data.length) {
          Object[] current = this.data[row];
          if (column < current.length) {
              result = current[column];
          }
      }
      return result;

    }

    /**
     * Sets the object for a cell in the table.  The table is expanded if necessary.
     *
     * @param row  the row index (zero-based).
     * @param column  the column index (zero-based).
     * @param object  the object.
     */
    protected void setObject(int row, int column, Object object) {

      // does this increase the number of rows?  if yes, create new storage
      if (row >= this.data.length) {
          Object[][] enlarged = new Object[row + 1][];
          System.arraycopy(this.data, 0, enlarged, 0, this.data.length);
          for (int j = this.data.length; j <= row; j++) {
              enlarged[j] = new Object[0];
          }
          this.data = enlarged;
          this.rows = row + 1;
      }

      // does this increase the current row?
      Object[] current = this.data[row];
      if (column >= current.length) {
          Object[] enlarged = new Object[column + 1];
          System.arraycopy(current, 0, enlarged, 0, current.length);
          enlarged[column] = object;
          this.data[row] = enlarged;
          this.columns = column + 1;
      }
      else {
          current[column] = object;
      }

    }

    /**
     * Tests this paint table for equality with another object (typically also an
     * <code>ObjectTable</code>).
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

        if (o instanceof ObjectTable) {
            ObjectTable ot = (ObjectTable) o;
            boolean result = (getRowCount() == ot.getRowCount());
            result = result && (getColumnCount() == ot.getColumnCount());
            if (result) {
                for (int r = 0; r < getRowCount(); r++) {
                    for (int c = 0; c < getColumnCount(); c++) {
                        result = result && ObjectUtils.equalOrBothNull(getObject(r, c),
                                                            ot.getObject(r, c));
                    }
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
   * @throws java.io.IOException if there is an I/O problem.
   */
  private void writeObject(ObjectOutputStream stream) throws IOException {
      stream.defaultWriteObject();
      int rowCount = this.data.length;
      stream.writeInt(rowCount);
      for (int r = 0; r < rowCount; r++) {
          Object[] column = this.data[r];
          int columnCount = column.length;
          stream.writeInt(columnCount);
          for (int c = 0; c < columnCount; c++) {
              writeSerializedData(stream, column[c]);
          }
      }
  }

  /**
   * Handles the serialization of an single element of this table.
   *
   * @param stream the stream which should write the object
   * @param o the object that should be serialized
   * @throws IOException if an IO error occured
   */
  protected void writeSerializedData (ObjectOutputStream stream, Object o)
    throws IOException
  {
    stream.writeObject(o);
  }

  /**
   * Restores a serialized object.
   *
   * @param stream  the input stream.
   *
   * @throws java.io.IOException if there is an I/O problem.
   * @throws ClassNotFoundException if there is a problem loading a class.
   */
  private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
      stream.defaultReadObject();
      int rowCount = stream.readInt();
      this.data = new Object[rowCount][];
      for (int r = 0; r < rowCount; r++) {
          int columnCount = stream.readInt();
          Object[] column = new Object[columnCount];
          this.data[r] = column;
          for (int c = 0; c < columnCount; c++) {
            column[c] = readSerializedData(stream);
          }
      }
  }

  /**
   * Handles the deserialization of a single element of the table.
   *
   * @param stream the object input stream from which to read the object.
   * @return the deserialized object
   * @exception java.lang.ClassNotFoundException Class of a serialized object
   *      cannot be found.
   * @exception OptionalDataException Primitive data was found in the
   * stream instead of objects.
   * @exception IOException Any of the usual Input/Output related exceptions.
   */
  protected Object readSerializedData (ObjectInputStream stream)
    throws OptionalDataException, ClassNotFoundException, IOException
  {
    return stream.readObject();
  }
}

