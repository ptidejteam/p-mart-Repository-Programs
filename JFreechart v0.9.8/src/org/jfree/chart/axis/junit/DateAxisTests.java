/* ======================================
 * JFreeChart : a free Java chart library
 * ======================================
 *
 * Project Info:  http://www.jfree.org/jfreechart/index.html
 * Project Lead:  David Gilbert (david.gilbert@object-refinery.com);
 *
 * (C) Copyright 2000-2003, by Simba Management Limited and Contributors.
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
 * ------------------
 * DateAxisTests.java
 * ------------------
 * (C) Copyright 2003 by Simba Management Limited and Contributors.
 *
 * Original Author:  David Gilbert (for Simba Management Limited);
 * Contributor(s):   -;
 *
 * $Id: DateAxisTests.java,v 1.1 2007/10/10 20:03:25 vauchers Exp $
 *
 * Changes
 * -------
 * 22-Apr-2003 : Version 1 (DG);
 *
 */

package org.jfree.chart.axis.junit;

import java.util.Calendar;
import java.util.Date;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.jfree.chart.axis.HorizontalDateAxis;
import org.jfree.data.DateRange;

/**
 * Tests for the {@link DateAxis} class.
 *
 * @author David Gilbert
 */
public class DateAxisTests extends TestCase {

    /**
     * Returns the tests as a test suite.
     *
     * @return the test suite.
     */
    public static Test suite() {
        return new TestSuite(DateAxisTests.class);
    }

    /**
     * Constructs a new set of tests.
     *
     * @param  name the name of the tests.
     */
    public DateAxisTests(String name) {
        super(name);
    }

	/**
	 * Test that the setRange(...) method works.
	 */
    public void testSetRange() {
    	
    	// use subclass because DateAxis is abstract
    	HorizontalDateAxis axis = new HorizontalDateAxis("Test Axis");
        Calendar calendar = Calendar.getInstance();
        calendar.set(1999, Calendar.JANUARY, 3);
    	Date d1 = calendar.getTime();
		calendar.set(1999, Calendar.JANUARY, 31);
    	Date d2 = calendar.getTime();;
    	axis.setRange(d1, d2);
    	
        DateRange range = (DateRange) axis.getRange();
        assertEquals(d1, range.getLowerDate());
        assertEquals(d2, range.getUpperDate());
    	
    }

	/**
	 * Test that the setMaximumDate(...) method works.
	 */
	public void testSetMaximumDate() {
    	
		// use subclass because DateAxis is abstract
		HorizontalDateAxis axis = new HorizontalDateAxis("Test Axis");
		Date date = new Date();
		axis.setMaximumDate(date);
    	assertEquals(date, axis.getMaximumDate());
    	
	}

	/**
	 * Test that the setMinimumDate(...) method works.
	 */
	public void testSetMinimumDate() {
    	
		// use subclass because DateAxis is abstract
		HorizontalDateAxis axis = new HorizontalDateAxis("Test Axis");
		Date d1 = new Date();
		Date d2 = new Date(d1.getTime() + 1);
		axis.setMaximumDate(d2);
		axis.setMinimumDate(d1);
		assertEquals(d1, axis.getMinimumDate());
    	
	}

}
