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
 * --------------------
 * ObjectTableTests.java
 * --------------------
 * (C) Copyright 2003 by Object Refinery Limited and Contributors.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited);
 * Contributor(s):   -;
 *
 * $Id: ObjectTableTests.java,v 1.1 2007/10/10 20:07:43 vauchers Exp $
 *
 * Changes
 * -------
 * 29-Apr-2003 : Version 1 (DG);
 *
 */

package org.jfree.chart.renderer.junit;

import java.awt.Color;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.jfree.chart.renderer.ObjectTable;
import org.jfree.chart.renderer.PaintTable;

/**
 * Tests for the {@link ObjectTable} class.
 *
 * @author David Gilbert
 */
public class ObjectTableTests extends TestCase {

    /**
     * Basic object table.
     */
    public class TObjectTable extends ObjectTable {

        /**
         * Constructor.
         */
        public TObjectTable() {
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
        public Object getObject(int row, int column) {
            return super.getObject(row, column);
        }

        /**
         * Sets the object for a cell in the table.  The table is expanded if necessary.
         *
         * @param row  the row index (zero-based).
         * @param column  the column index (zero-based).
         * @param object  the object.
         */
        public void setObject(int row, int column, Object object) {
            super.setObject(row, column, object);
        }
    }

    /**
     * Returns the tests as a test suite.
     *
     * @return the test suite.
     */
    public static Test suite() {
        return new TestSuite(ObjectTableTests.class);
    }

    /**
     * Constructs a new set of tests.
     *
     * @param  name the name of the tests.
     */
    public ObjectTableTests(String name) {
        super(name);
    }

    /**
     * When an ObjectTable is created, it should be empty and return null for all lookups.
     *
     */
    public void testCreate() {

        TObjectTable t = new TObjectTable();

        // the new table should have zero rows and zero columns...
        assertEquals(t.getColumnCount(), 0);
        assertEquals(t.getRowCount(), 0);

        // ...and should return null for any lookup
        assertNull(t.getObject(0, 0));
        assertNull(t.getObject(12, 12));

    }

    /**
     * When an object is added to the table outside the current bounds, the table
     * should resize automatically.
     *
     */
    public void testSetObject1() {

        TObjectTable t = new TObjectTable();
        t.setObject(8, 5, Color.red);
        int columns = t.getColumnCount();
        assertEquals(6, t.getColumnCount());
        assertEquals(9, t.getRowCount());
        assertNull(t.getObject(7, 4));
        assertEquals(Color.red, t.getObject(8, 5));

    }

    /**
     * Serialize an instance, restore it, and check for equality.
     */
    public void testSerialization() {

        PaintTable t1 = new PaintTable();
        t1.setPaint(0, 0, Color.blue);
        t1.setPaint(0, 1, Color.red);
        t1.setPaint(1, 0, Color.yellow);
        t1.setPaint(1, 1, Color.green);

        ObjectTable t2 = null;

        try {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            ObjectOutput out = new ObjectOutputStream(buffer);
            out.writeObject(t1);
            out.close();

            ObjectInput in = new ObjectInputStream(new ByteArrayInputStream(buffer.toByteArray()));
            t2 = (ObjectTable) in.readObject();
            in.close();
        }
        catch (Exception e) {
            System.out.println(e.toString());
        }
        assertEquals(t1, t2);

    }

}
