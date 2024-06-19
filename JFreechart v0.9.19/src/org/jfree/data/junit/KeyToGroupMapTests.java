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
 * --------------------------
 * CategoryGroupMapTests.java
 * --------------------------
 * (C) Copyright 2004 by Object Refinery Limited and Contributors.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited);
 * Contributor(s):   -;
 *
 * $Id: KeyToGroupMapTests.java,v 1.1 2007/10/10 19:34:52 vauchers Exp $
 *
 * Changes
 * -------
 * 29-Apr-2004 : Version 1 (DG);
 *
 */

package org.jfree.data.junit;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.jfree.data.KeyToGroupMap;

/**
 * Tests for the {@link KeyToGroupMap} class.
 */
public class KeyToGroupMapTests extends TestCase {

    /**
     * Returns the tests as a test suite.
     *
     * @return The test suite.
     */
    public static Test suite() {
        return new TestSuite(KeyToGroupMapTests.class);
    }

    /**
     * Constructs a new set of tests.
     *
     * @param  name the name of the tests.
     */
    public KeyToGroupMapTests(final String name) {
        super(name);
    }

    /**
     * Tests that the getGroupCount() method returns the correct values under various
     * circumstances.
     */
    public void testGroupCount() {
        KeyToGroupMap m1 = new KeyToGroupMap("Default Group");
        // if the default group is not mapped to, it should still count towards the
        // group count...
        m1.mapKeyToGroup("C1", "G1");
        assertEquals(2, m1.getGroupCount());
        
        // now when the default group is mapped to, it shouldn't increase the group
        // count...
        m1.mapKeyToGroup("C2", "Default Group");
        assertEquals(2, m1.getGroupCount());
    
        // complicate things a little...
        m1.mapKeyToGroup("C3", "Default Group");
        m1.mapKeyToGroup("C4", "G2");
        m1.mapKeyToGroup("C5", "G2");
        m1.mapKeyToGroup("C6", "Default Group");
        assertEquals(3, m1.getGroupCount());
        
        // now overwrite group "G2"...
        m1.mapKeyToGroup("C4", "G1");
        m1.mapKeyToGroup("C5", "G1");
        assertEquals(2, m1.getGroupCount()); 
    }
    
    /**
     * Confirm that the equals method can distinguish all the required fields.
     */
    public void testEquals() {
        
        KeyToGroupMap m1 = new KeyToGroupMap("Default Group");
        KeyToGroupMap m2 = new KeyToGroupMap("Default Group");
        assertTrue(m1.equals(m2));
        assertTrue(m2.equals(m1));

    }

    /**
     * Confirm that cloning works.
     */
    public void testCloning() {
        final KeyToGroupMap m1 = new KeyToGroupMap("Test");
        KeyToGroupMap m2 = null;
        try {
            m2 = (KeyToGroupMap) m1.clone();
        }
        catch (CloneNotSupportedException e) {
            System.err.println("CategoryGroupMapTests.testCloning: failed to clone.");
        }
        assertTrue(m1 != m2);
        assertTrue(m1.getClass() == m2.getClass());
        assertTrue(m1.equals(m2));
    }

    /**
     * Serialize an instance, restore it, and check for equality.
     */
    public void testSerialization() {

        final KeyToGroupMap m1 = new KeyToGroupMap("Test");
        KeyToGroupMap m2 = null;

        try {
            final ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            final ObjectOutput out = new ObjectOutputStream(buffer);
            out.writeObject(m1);
            out.close();

            final ObjectInput in = new ObjectInputStream(
                new ByteArrayInputStream(buffer.toByteArray())
            );
            m2 = (KeyToGroupMap) in.readObject();
            in.close();
        }
        catch (Exception e) {
            System.out.println(e.toString());
        }
        assertEquals(m1, m2);

    }

}
