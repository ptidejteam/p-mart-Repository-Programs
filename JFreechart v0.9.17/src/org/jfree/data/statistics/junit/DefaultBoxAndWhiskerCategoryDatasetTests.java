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
 * ---------------------------------------------
 * DefaultBoxAndWhiskerCategoryDatasetTests.java
 * ---------------------------------------------
 * (C) Copyright 2004 by Object Refinery Limited and Contributors.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited);
 * Contributor(s):   -;
 *
 * $Id: DefaultBoxAndWhiskerCategoryDatasetTests.java,v 1.1 2007/10/10 19:29:21 vauchers Exp $
 *
 * Changes
 * -------
 * 01-Mar-2004 : Version 1 (DG);
 *
 */

package org.jfree.data.statistics.junit;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.jfree.data.statistics.BoxAndWhiskerCategoryDataset;
import org.jfree.data.statistics.BoxAndWhiskerItem;
import org.jfree.data.statistics.DefaultBoxAndWhiskerCategoryDataset;

/**
 * Tests for the {@link DefaultBoxAndWhiskerCategoryDataset} class.
 *
 * @author David Gilbert
 */
public class DefaultBoxAndWhiskerCategoryDatasetTests extends TestCase {

    /**
     * Returns the tests as a test suite.
     *
     * @return the test suite.
     */
    public static Test suite() {
        return new TestSuite(DefaultBoxAndWhiskerCategoryDatasetTests.class);
    }

    /**
     * Constructs a new set of tests.
     *
     * @param  name the name of the tests.
     */
    public DefaultBoxAndWhiskerCategoryDatasetTests(String name) {
        super(name);
    }

    /**
     * Confirm that the equals method can distinguish all the required fields.
     */
    public void testEquals() {
        
        DefaultBoxAndWhiskerCategoryDataset d1 = new DefaultBoxAndWhiskerCategoryDataset();
        d1.add(
            new BoxAndWhiskerItem(
                new Double(1.0), new Double(2.0), new Double(3.0), new Double(4.0),
                new Double(5.0), new Double(6.0), new Double(7.0), new Double(8.0),
                new ArrayList()
            ), "ROW1", "COLUMN1"
        );
        DefaultBoxAndWhiskerCategoryDataset d2 = new DefaultBoxAndWhiskerCategoryDataset();
        d2.add(
            new BoxAndWhiskerItem(
                new Double(1.0), new Double(2.0), new Double(3.0), new Double(4.0),
                new Double(5.0), new Double(6.0), new Double(7.0), new Double(8.0),
                new ArrayList()
            ), "ROW1", "COLUMN1"
        );
        assertTrue(d1.equals(d2));
        assertTrue(d2.equals(d1));

    }

    /**
     * Serialize an instance, restore it, and check for equality.
     */
    public void testSerialization() {

        DefaultBoxAndWhiskerCategoryDataset d1 = new DefaultBoxAndWhiskerCategoryDataset();
        d1.add(
            new BoxAndWhiskerItem(
                new Double(1.0), new Double(2.0), new Double(3.0), new Double(4.0),
                new Double(5.0), new Double(6.0), new Double(7.0), new Double(8.0),
                new ArrayList()
            ), "ROW1", "COLUMN1"
        );
        DefaultBoxAndWhiskerCategoryDataset d2 = null;
        
        try {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            ObjectOutput out = new ObjectOutputStream(buffer);
            out.writeObject(d1);
            out.close();

            ObjectInput in = new ObjectInputStream(new ByteArrayInputStream(buffer.toByteArray()));
            d2 = (DefaultBoxAndWhiskerCategoryDataset) in.readObject();
            in.close();
        }
        catch (Exception e) {
            System.out.println(e.toString());
        }
        assertEquals(d1, d2);

    }
    
    /**
     * Creates a sample dataset.
     * 
     * @return a sample dataset.
     */
    private BoxAndWhiskerCategoryDataset createSampleDataset() {
        
        final int seriesCount = 3;
        final int categoryCount = 4;
        final int entityCount = 22;
        
        DefaultBoxAndWhiskerCategoryDataset dataset = new DefaultBoxAndWhiskerCategoryDataset();
        for (int i = 0; i < seriesCount; i++) {
            for (int j = 0; j < categoryCount; j++) {
                List list = new ArrayList();
                // add some values...
                for (int k = 0; k < entityCount; k++) {
                    double value1 = 10.0 + Math.random() * 3;
                    list.add(new Double(value1));
                    double value2 = 11.25 + Math.random();  // concentrate values in the middle
                    list.add(new Double(value2));
                }
                dataset.add(list, "Series " + i, " Type " + j);
            }
        }

        return dataset;
    }


}
