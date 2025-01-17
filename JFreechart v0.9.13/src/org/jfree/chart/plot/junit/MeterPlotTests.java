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
 * -------------------
 * MeterPlotTests.java
 * -------------------
 * (C) Copyright 2003 by Object Refinery Limited and Contributors.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited);
 * Contributor(s):   -;
 *
 * $Id: MeterPlotTests.java,v 1.1 2007/10/10 19:15:36 vauchers Exp $
 *
 * Changes
 * -------
 * 27-Mar-2003 : Version 1 (DG);
 *
 */

package org.jfree.chart.plot.junit;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.jfree.chart.plot.MeterPlot;

/**
 * Tests for the {@link MeterPlot} class.
 *
 * @author David Gilbert
 */
public class MeterPlotTests extends TestCase {

    /**
     * Returns the tests as a test suite.
     *
     * @return the test suite.
     */
    public static Test suite() {
        return new TestSuite(MeterPlotTests.class);
    }

    /**
     * Constructs a new set of tests.
     *
     * @param  name the name of the tests.
     */
    public MeterPlotTests(String name) {
        super(name);
    }

//    /**
//     * Test the equals method.
//     */
//    public void testEquals() {
//        MeterPlot plot1 = new MeterPlot();
//        MeterPlot plot2 = new MeterPlot();
//        assertTrue(plot1.equals(plot2));    
//        
//        // labelType...
//        plot1.setLabelType(CompassPlot.VALUE_LABELS);
//        assertFalse(plot1.equals(plot2));
//        plot2.setLabelType(CompassPlot.VALUE_LABELS);
//        assertTrue(plot1.equals(plot2));
//        
//    }

    /**
     * Serialize an instance, restore it, and check for equality.
     */
    public void testSerialization() {

        MeterPlot p1 = new MeterPlot(null);
        MeterPlot p2 = null;

        try {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            ObjectOutput out = new ObjectOutputStream(buffer);
            out.writeObject(p1);
            out.close();

            ObjectInput in = new ObjectInputStream(new ByteArrayInputStream(buffer.toByteArray()));
            p2 = (MeterPlot) in.readObject();
            in.close();
        }
        catch (Exception e) {
            System.out.println(e.toString());
        }
        assertEquals(p1, p2);

    }

}
