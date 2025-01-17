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
 * ---------------
 * TitleTests.java
 * ---------------
 * (C) Copyright 2004, by Object Refinery Limited and Contributors.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited);
 * Contributor(s):   -;
 *
 * $Id: TitleTests.java,v 1.1 2007/10/10 19:50:31 vauchers Exp $
 *
 * Changes
 * -------
 * 17-Feb-2004 : Version 1 (DG);
 *
 */

package org.jfree.chart.title.junit;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.jfree.chart.title.TextTitle;
import org.jfree.chart.title.Title;
import org.jfree.ui.HorizontalAlignment;
import org.jfree.ui.RectangleEdge;
import org.jfree.ui.Spacer;
import org.jfree.ui.VerticalAlignment;

/**
 * Tests for the abstract {@link Title} class.
 *
 */
public class TitleTests extends TestCase {

    /**
     * Returns the tests as a test suite.
     *
     * @return the test suite.
     */
    public static Test suite() {
        return new TestSuite(TitleTests.class);
    }

    /**
     * Constructs a new set of tests.
     *
     * @param name  the name of the tests.
     */
    public TitleTests(String name) {
        super(name);
    }

    /**
     * Problem that the equals(...) method distinguishes all fields.
     */
    public void testEquals() {
        
        // use the TextTitle class because it is a concrete subclass
        Title t1 = new TextTitle();
        Title t2 = new TextTitle();
        assertEquals(t1, t2);
        
        t1.setPosition(RectangleEdge.LEFT);
        assertFalse(t1.equals(t2));
        t2.setPosition(RectangleEdge.LEFT);
        assertTrue(t1.equals(t2));
        
        t1.setHorizontalAlignment(HorizontalAlignment.RIGHT);
        assertFalse(t1.equals(t2));
        t2.setHorizontalAlignment(HorizontalAlignment.RIGHT);
        assertTrue(t1.equals(t2));
        
        t1.setVerticalAlignment(VerticalAlignment.BOTTOM);
        assertFalse(t1.equals(t2));
        t2.setVerticalAlignment(VerticalAlignment.BOTTOM);
        assertTrue(t1.equals(t2));
        
        t1.setSpacer(new Spacer(Spacer.ABSOLUTE, 5.0, 10.0, 15.0, 20.0));
        assertFalse(t1.equals(t2));
        t2.setSpacer(new Spacer(Spacer.ABSOLUTE, 5.0, 10.0, 15.0, 20.0));
        assertTrue(t1.equals(t2));
        
    }

    /**
     * Two objects that are equal are required to return the same hashCode. 
     */
    public void testHashcode() {
        TextTitle t1 = new TextTitle();
        TextTitle t2 = new TextTitle();
        assertTrue(t1.equals(t2));
        int h1 = t1.hashCode();
        int h2 = t2.hashCode();
        assertEquals(h1, h2);
    }

}
