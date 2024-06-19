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
 * ---------------
 * PaintTable.java
 * ---------------
 * (C) Copyright 2002, 2003 by Object Refinery Limited and Contributors.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited);
 * Contributor(s):   Thomas Morgner;
 *
 * $Id: PaintTable.java,v 1.1 2007/10/10 20:07:35 vauchers Exp $
 *
 * Changes (since 8-Jan-2003)
 * --------------------------
 * 08-Jan-2002 : Added standard header and Javadocs (DG);
 * 16-Jan-2003 : Changed to class, and moved to com.jrefinery.chart.renderer (DG);
 * 14-Feb-2003 : Fixed bug in setPaint() method (DG);
 * 25-Mar-2003 : Implemented Serializable (DG);
 * 21-May-2003 : Implementation now uses ObjectTable as backend (TM).
 */

package org.jfree.chart.renderer;

import java.awt.Paint;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OptionalDataException;
import java.io.Serializable;

import org.jfree.io.SerialUtilities;

/**
 * A lookup table for <code>Paint</code> objects.
 *
 * @author David Gilbert
 */
public class PaintTable extends ObjectTable implements Serializable {

    /**
     * Creates a new paint table.
     */
    public PaintTable() {
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

        return (Paint) getObject(row, column);

    }

    /**
     * Sets the paint for a cell in the table.  The table is expanded if necessary.
     *
     * @param row  the row index (zero-based).
     * @param column  the column index (zero-based).
     * @param paint  the paint.
     */
    public void setPaint(int row, int column, Paint paint) {

        setObject(row, column, paint);

    }

    /**
     * Tests this paint table for equality with another object (typically also a paint table).
     *
     * @param o  the other object.
     *
     * @return A boolean.
     */
    public boolean equals(Object o) {

        if (o instanceof PaintTable) {
            return super.equals(o);
        }

        return false;

    }

    /**
     * Handles the serialization of an single element of this table.
     *
     * @param stream the stream which should write the object
     * @param o the object that should be serialized
     * @throws IOException if an IO error occured
     */
    protected void writeSerializedData(ObjectOutputStream stream, Object o) throws IOException {
        SerialUtilities.writePaint((Paint) o, stream);
    }

    /**
     * Handles the deserialization of a single element of the table.
     *
     * @param stream the object input stream from which to read the object.
     * @return the deserialized object
     * @exception ClassNotFoundException Class of a serialized object
     *      cannot be found.
     * @exception OptionalDataException Primitive data was found in the
     * stream instead of objects.
     * @exception IOException Any of the usual Input/Output related exceptions.
     */
    protected Object readSerializedData(ObjectInputStream stream)
        throws OptionalDataException, ClassNotFoundException, IOException {
            
        return SerialUtilities.readPaint(stream);
    }
    
}
