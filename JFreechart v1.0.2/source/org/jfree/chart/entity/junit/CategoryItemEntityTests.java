/* ===========================================================
 * JFreeChart : a free chart library for the Java(tm) platform
 * ===========================================================
 *
 * (C) Copyright 2000-2005, by Object Refinery Limited and Contributors.
 *
 * Project Info:  http://www.jfree.org/jfreechart/index.html
 *
 * This library is free software; you can redistribute it and/or modify it 
 * under the terms of the GNU Lesser General Public License as published by 
 * the Free Software Foundation; either version 2.1 of the License, or 
 * (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but 
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY 
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public 
 * License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, 
 * USA.  
 *
 * [Java is a trademark or registered trademark of Sun Microsystems, Inc. 
 * in the United States and other countries.]
 *
 * ----------------------------
 * CategoryItemEntityTests.java
 * ----------------------------
 * (C) Copyright 2004, by Object Refinery Limited and Contributors.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited);
 * Contributor(s):   -;
 *
 * $Id: CategoryItemEntityTests.java,v 1.1 2007/10/10 20:22:47 vauchers Exp $
 *
 * Changes
 * -------
 * 20-May-2004 : Version 1 (DG);
 *
 */

package org.jfree.chart.entity.junit;

import java.awt.geom.Rectangle2D;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.jfree.chart.entity.CategoryItemEntity;
import org.jfree.data.category.DefaultCategoryDataset;

/**
 * Tests for the {@link CategoryItemEntity} class.
 */
public class CategoryItemEntityTests extends TestCase {

    /**
     * Returns the tests as a test suite.
     *
     * @return The test suite.
     */
    public static Test suite() {
        return new TestSuite(CategoryItemEntityTests.class);
    }

    /**
     * Constructs a new set of tests.
     *
     * @param name  the name of the tests.
     */
    public CategoryItemEntityTests(String name) {
        super(name);
    }

    /**
     * Confirm that the equals method can distinguish all the required fields.
     */
    public void testEquals() {
        CategoryItemEntity e1 = new CategoryItemEntity(
            new Rectangle2D.Double(1.0, 2.0, 3.0, 4.0), "ToolTip", "URL",
            new DefaultCategoryDataset(), 1, "Category", 9
        ); 
        CategoryItemEntity e2 = new CategoryItemEntity(
            new Rectangle2D.Double(1.0, 2.0, 3.0, 4.0), "ToolTip", "URL",
            new DefaultCategoryDataset(), 1, "Category", 9
        ); 
        assertTrue(e1.equals(e2));  
        
        e1.setArea(new Rectangle2D.Double(4.0, 3.0, 2.0, 1.0));
        assertFalse(e1.equals(e2));
        e2.setArea(new Rectangle2D.Double(4.0, 3.0, 2.0, 1.0));
        assertTrue(e1.equals(e2));  

        e1.setToolTipText("New ToolTip");
        assertFalse(e1.equals(e2));
        e2.setToolTipText("New ToolTip");
        assertTrue(e1.equals(e2));  

        e1.setURLText("New URL");
        assertFalse(e1.equals(e2));
        e2.setURLText("New URL");
        assertTrue(e1.equals(e2));  
        
        e1.setCategory("Category 20");
        assertFalse(e1.equals(e2));
        e2.setCategory("Category 20");
        assertTrue(e1.equals(e2)); 
        
        e1.setCategoryIndex(20);
        assertFalse(e1.equals(e2));
        e2.setCategoryIndex(20);
        assertTrue(e1.equals(e2)); 
        
        e1.setSeries(88);
        assertFalse(e1.equals(e2));
        e2.setSeries(88);
        assertTrue(e1.equals(e2)); 
        
    }

    /**
     * Confirm that cloning works.
     */
    public void testCloning() {
        CategoryItemEntity e1 = new CategoryItemEntity(
            new Rectangle2D.Double(1.0, 2.0, 3.0, 4.0), "ToolTip", "URL",
            new DefaultCategoryDataset(), 1, "Category", 9
        ); 
        CategoryItemEntity e2 = null;
        
        try {
            e2 = (CategoryItemEntity) e1.clone();
        }
        catch (CloneNotSupportedException e) {
            System.err.println("Failed to clone.");
        }
        assertTrue(e1 != e2);
        assertTrue(e1.getClass() == e2.getClass());
        assertTrue(e1.equals(e2));
    }

    /**
     * Serialize an instance, restore it, and check for equality.
     */
    public void testSerialization() {
        CategoryItemEntity e1 = new CategoryItemEntity(
            new Rectangle2D.Double(1.0, 2.0, 3.0, 4.0), "ToolTip", "URL",
            new DefaultCategoryDataset(), 1, "Category", 9
        ); 
        CategoryItemEntity e2 = null;
        try {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            ObjectOutput out = new ObjectOutputStream(buffer);
            out.writeObject(e1);
            out.close();

            ObjectInput in = new ObjectInputStream(
                new ByteArrayInputStream(buffer.toByteArray())
            );
            e2 = (CategoryItemEntity) in.readObject();
            in.close();
        }
        catch (Exception e) {
            System.out.println(e.toString());
        }
        assertEquals(e1, e2);
    }

}
