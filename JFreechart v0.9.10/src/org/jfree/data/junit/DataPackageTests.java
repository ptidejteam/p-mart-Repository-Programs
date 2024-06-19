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
 * ---------------------
 * DataPackageTests.java
 * ---------------------
 * (C) Copyright 2001-2003, by Object Refinery Limited.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited);
 * Contributor(s):   -;
 *
 * $Id: DataPackageTests.java,v 1.1 2007/10/10 19:05:08 vauchers Exp $
 *
 * Changes
 * -------
 * 16-Nov-2001 : Version 1 (DG);
 * 30-Sep-2002 : Added tests for the Regression class (DG);
 * 17-Oct-2002 : Fixed errors reported by Checkstyle (DG);
 * 05-Mar-2003 : Added tests for the DefaultKeyedValues class (DG);
 * 13-Mar-2003 : Added tests for the DefaultKeyedValue class (DG);
 *
 */

package org.jfree.data.junit;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Some tests for the <code>org.jfree.data</code> package that can be run using JUnit.
 * You can find more information about JUnit at {@link http://www.junit.org}.
 *
 * @author David Gilbert
 */
public class DataPackageTests extends TestCase {

    /**
     * Returns a test suite to the JUnit test runner.
     *
     * @return the test suite.
     */
    public static Test suite() {
        TestSuite suite = new TestSuite("org.jfree.data");
        suite.addTestSuite(RegressionTests.class);
        suite.addTestSuite(DefaultKeyedValueTests.class);
        suite.addTestSuite(DefaultKeyedValuesTests.class);
        suite.addTestSuite(DefaultKeyedValuesDatasetTests.class);
        suite.addTestSuite(DefaultKeyedValues2DTests.class);
        suite.addTestSuite(DefaultKeyedValues2DDatasetTests.class);
        suite.addTestSuite(TaskSeriesCollectionTests.class);
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
