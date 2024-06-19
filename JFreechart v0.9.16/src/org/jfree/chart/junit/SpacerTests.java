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
 * ----------------
 * SpacerTests.java
 * ----------------
 * (C) Copyright 2003, 2004, by Object Refinery Limited and Contributors.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited);
 * Contributor(s):   -;
 *
 * $Id: SpacerTests.java,v 1.1 2007/10/10 19:25:40 vauchers Exp $
 *
 * Changes
 * -------
 * 18-Aug-2003 : Version 1 (DG);
 *
 */

package org.jfree.chart.junit;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.jfree.chart.Spacer;

/**
 * Tests for the {@link Spacer} class.
 *
 * @author David Gilbert
 */
public class SpacerTests extends TestCase {

    /**
     * Returns the tests as a test suite.
     *
     * @return the test suite.
     */
    public static Test suite() {
        return new TestSuite(SpacerTests.class);
    }

    /**
     * Constructs a new set of tests.
     *
     * @param  name the name of the tests.
     */
    public SpacerTests(String name) {
        super(name);
    }

    /**
     * Confirm that the equals method can distinguish all the required fields.
     */
    public void testEquals() {
        
        Spacer s1 = new Spacer(Spacer.ABSOLUTE, 0.05, 0.05, 0.05, 0.05);
        Spacer s2 = new Spacer(Spacer.ABSOLUTE, 0.05, 0.05, 0.05, 0.05);
        assertTrue(s1.equals(s2));
        
        s1 = new Spacer(Spacer.ABSOLUTE, 0.05, 0.05, 0.05, 0.05);
        s2 = new Spacer(Spacer.RELATIVE, 0.05, 0.05, 0.05, 0.05);
        assertFalse(s1.equals(s2));

        s1 = new Spacer(Spacer.ABSOLUTE, 0.05, 0.05, 0.05, 0.05);
        s2 = new Spacer(Spacer.ABSOLUTE, 0.06, 0.05, 0.05, 0.05);
        assertFalse(s1.equals(s2));

        s1 = new Spacer(Spacer.ABSOLUTE, 0.05, 0.05, 0.05, 0.05);
        s2 = new Spacer(Spacer.ABSOLUTE, 0.05, 0.06, 0.05, 0.05);
        assertFalse(s1.equals(s2));

        s1 = new Spacer(Spacer.ABSOLUTE, 0.05, 0.05, 0.05, 0.05);
        s2 = new Spacer(Spacer.ABSOLUTE, 0.05, 0.05, 0.06, 0.05);
        assertFalse(s1.equals(s2));

        s1 = new Spacer(Spacer.ABSOLUTE, 0.05, 0.05, 0.05, 0.05);
        s2 = new Spacer(Spacer.ABSOLUTE, 0.05, 0.05, 0.05, 0.06);
        assertFalse(s1.equals(s2));
        
    }
        
    /**
     * Serialize an instance, restore it, and check for equality.
     */
    public void testSerialization() {

        Spacer s1 = new Spacer(Spacer.ABSOLUTE, 0.05, 0.05, 0.05, 0.05);
        Spacer s2 = null;

        try {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            ObjectOutput out = new ObjectOutputStream(buffer);
            out.writeObject(s1);
            out.close();

            ObjectInput in = new ObjectInputStream(new ByteArrayInputStream(buffer.toByteArray()));
            s2 = (Spacer) in.readObject();
            in.close();
        }
        catch (Exception e) {
            System.out.println(e.toString());
        }
        boolean b = s1.equals(s2);
        assertTrue(b);

    }

}
