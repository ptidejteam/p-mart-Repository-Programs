/* ================================================================
 * JCommon : a general purpose, open source, class library for Java
 * ================================================================
 *
 * Project Info:  http://www.object-refinery.com/jcommon/index.html
 * Project Lead:  David Gilbert (david.gilbert@object-refinery.com);
 *
 * (C) Copyright 2000-2002, by Simba Management Limited.
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
 * ---------------------
 * DataPackageTests.java
 * ---------------------
 * (C) Copyright 2001, 2002, by Simba Management Limited.
 *
 * Original Author:  David Gilbert (for Simba Management Limited);
 * Contributor(s):   -;
 *
 * $Id: DataPackageTests.java,v 1.1 2007/10/10 19:52:22 vauchers Exp $
 *
 * Changes
 * -------
 * 16-Nov-2001 : Version 1 (DG);
 *
 */

package com.jrefinery.data.junit;

import junit.framework.*;
import com.jrefinery.data.junit.*;

/**
 * An incomplete collection of tests for the com.jrefinery.data package.
 * <P>
 * These tests can be run using JUnit (http://www.junit.org).
 */
public class DataPackageTests extends TestCase {

    /**
     * Returns a test suite to the JUnit test runner.
     */
    public static Test suite() {
        TestSuite suite = new TestSuite("com.jrefinery.data");
        suite.addTestSuite(BasicTimeSeriesTests.class);
        suite.addTestSuite(YearTests.class);
        suite.addTestSuite(QuarterTests.class);
        suite.addTestSuite(MonthTests.class);
        suite.addTestSuite(WeekTests.class);
        suite.addTestSuite(DayTests.class);
        suite.addTestSuite(HourTests.class);
        suite.addTestSuite(MinuteTests.class);
        suite.addTestSuite(SecondTests.class);
        suite.addTestSuite(MillisecondTests.class);
        return suite;
    }

    /**
     * Constructs the test suite.
     */
    public DataPackageTests(String name) {
        super(name);
    }

}
