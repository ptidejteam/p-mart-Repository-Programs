/* ======================================
 * JFreeChart : a free Java chart library
 * ======================================
 *
 * Project Info:  http://www.object-refinery.com/jfreechart/index.html
 * Project Lead:  David Gilbert (david.gilbert@object-refinery.com);
 *
 * (C) Copyright 2000-2002, by Simba Management Limited and Contributors.
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
 * $Id: DataPackageTests.java,v 1.1 2007/10/10 21:48:09 vauchers Exp $
 *
 * Changes
 * -------
 * 16-Nov-2001 : Version 1 (DG);
 * 30-Sep-2002 : Added tests for the Regression class (DG);
 * 17-Oct-2002 : Fixed errors reported by Checkstyle (DG);
 *
 */

package com.jrefinery.data.junit;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Some tests for the com.jrefinery.data package that can be run using JUnit.
 * You can find more information about JUnit at http://www.junit.org.
 *
 * @author DG
 */
public class DataPackageTests extends TestCase {

    /**
     * Returns a test suite to the JUnit test runner.
     *
     * @return the test suite.
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
        suite.addTestSuite(RegressionTests.class);
        return suite;
    }

    /**
     * Constructs the test suite.
     *
     * @param name  the test suite name.
     */
    public DataPackageTests(String name) {
        super(name);
    }

}
