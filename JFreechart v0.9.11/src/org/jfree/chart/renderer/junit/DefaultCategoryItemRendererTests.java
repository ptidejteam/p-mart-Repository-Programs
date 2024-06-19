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
 * -------------------------------------
 * DefaultCategoryItemRendererTests.java
 * -------------------------------------
 * (C) Copyright 2003 by Object Refinery Limited and Contributors.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited);
 * Contributor(s):   -;
 *
 * $Id: DefaultCategoryItemRendererTests.java,v 1.1 2007/10/10 19:09:21 vauchers Exp $
 *
 * Changes
 * -------
 * 29-Apr-2003 : Version 1 (DG);
 *
 */

package org.jfree.chart.renderer.junit;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.jfree.chart.renderer.DefaultCategoryItemRenderer;

/**
 * Tests for the {@link DefaultCategoryItemRenderer} class.
 *
 * @author David Gilbert
 */
public class DefaultCategoryItemRendererTests extends TestCase {

    /**
     * Returns the tests as a test suite.
     *
     * @return the test suite.
     */
    public static Test suite() {
        return new TestSuite(DefaultCategoryItemRendererTests.class);
    }

    /**
     * Constructs a new set of tests.
     *
     * @param  name the name of the tests.
     */
    public DefaultCategoryItemRendererTests(String name) {
        super(name);
    }

    /**
     * Test that the equals(...) method distinguishes all fields.
     */
    public void testEquals() {
        DefaultCategoryItemRenderer r1 = new DefaultCategoryItemRenderer();
        DefaultCategoryItemRenderer r2 = new DefaultCategoryItemRenderer();
        assertEquals(r1, r2);

        // test that the value label font is distinguished
//        r2.setValueLabelFont(new Font("Serif", Font.BOLD, 20));
//        assertTrue("Value label font different...", !r1.equals(r2));
//        assertNotSame(r1, r2);
//        r2.setValueLabelFont(AbstractRenderer.DEFAULT_VALUE_LABEL_FONT);
//        assertEquals("Value label font same again...", r1, r2);
//
//        // test that the value label paint is distinguished
//        r2.setValueLabelPaint(Color.red);
//        assertTrue("Value label paint different...", !r1.equals(r2));
//        assertNotSame(r1, r2);
//        r2.setValueLabelPaint(AbstractRenderer.DEFAULT_VALUE_LABEL_PAINT);
//        assertEquals("Value label paint same again...", r1, r2);

    }

    /**
     * Serialize an instance, restore it, and check for equality.
     */
    public void testSerialization() {

        DefaultCategoryItemRenderer r1 = new DefaultCategoryItemRenderer();
        DefaultCategoryItemRenderer r2 = null;

        try {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            ObjectOutput out = new ObjectOutputStream(buffer);
            out.writeObject(r1);
            out.close();

            ObjectInput in = new ObjectInputStream(new ByteArrayInputStream(buffer.toByteArray()));
            r2 = (DefaultCategoryItemRenderer) in.readObject();
            in.close();
        }
        catch (Exception e) {
            System.out.println(e.toString());
        }
        assertEquals(r1, r2);

    }

}
