/* ======================================
 * JFreeChart : a free Java chart library
 * ======================================
 *
 * Project Info:  http://www.jfree.org/jfreechart/index.html
 * Project Lead:  David Gilbert (david.gilbert@object-refinery.com);
 *
 * (C) Copyright 2000-2003, by Simba Management Limited.
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
 * SimpleTimePeriodTests.java
 * --------------------------
 * (C) Copyright 2003 by Simba Management Limited.
 *
 * Original Author:  David Gilbert (for Simba Management Limited);
 * Contributor(s):   -;
 *
 * $Id: SimpleTimePeriodTests.java,v 1.1 2007/10/10 20:03:24 vauchers Exp $
 *
 * Changes
 * -------
 * 13-Mar-2003 : Version 1 (DG);
 *
 */
 
package org.jfree.data.time.junit;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.util.Date;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.jfree.data.time.SimpleTimePeriod;

/**
 * Tests for the {@link SimpleTimePeriod} class.
 *
 * @author David Gilbert
 */
public class SimpleTimePeriodTests extends TestCase {

    /**
     * Returns the tests as a test suite.
     *
     * @return the test suite.
     */
    public static Test suite() {
        return new TestSuite(SimpleTimePeriodTests.class);
    }

    /**
     * Constructs a new set of tests.
     *
     * @param name  the name of the tests.
     */
    public SimpleTimePeriodTests(String name) {
        super(name);
    }

    /**
     * Common test setup.
     */
    protected void setUp() {

    }

    /**
     * Test that an instance is equal to itself.
     *
     * SourceForge Bug ID: 558850.
     */
    public void testEqualsSelf() {
        SimpleTimePeriod p = new SimpleTimePeriod(new Date(1000L), new Date(1001L));
        assertTrue(p.equals(p));
    }

    /**
     * Test the equals method.
     */
    public void testEquals() {
        SimpleTimePeriod p1 = new SimpleTimePeriod(new Date(1000L), new Date(1001L));
        SimpleTimePeriod p2 = new SimpleTimePeriod(new Date(1000L), new Date(1001L));
        assertTrue(p1.equals(p2));
        assertTrue(p2.equals(p1));
    }
    
    /**
     * Serialize an instance, restore it, and check for equality.
     */
    public void testSerialization() {
        
        SimpleTimePeriod p1 = new SimpleTimePeriod(new Date(1000L), new Date(1001L));
        SimpleTimePeriod p2 = null;
        
        try {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            ObjectOutput out = new ObjectOutputStream(buffer);
            out.writeObject(p1);
            out.close();
        
            ObjectInput in = new ObjectInputStream(new ByteArrayInputStream(buffer.toByteArray()));
            p2 = (SimpleTimePeriod) in.readObject();
            in.close();
        }
        catch (Exception e) {
            System.out.println(e.toString());
        }
        assertEquals(p1, p2); 
        
    }

}
