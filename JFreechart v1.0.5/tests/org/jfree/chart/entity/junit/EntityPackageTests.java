/* ===========================================================
 * JFreeChart : a free chart library for the Java(tm) platform
 * ===========================================================
 *
 * (C) Copyright 2000-2005, by Object Refinery Limited and Contributors.
 *
 * Project Info:  http://www.jfree.org/jfreechart/index.html
 *
 * This library is free software; you can redistribute it and/or modify it 
 * under the terms of the GNU Lesser General Public License as published by 
 * the Free Software Foundation; either version 2.1 of the License, or 
 * (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but 
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY 
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public 
 * License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, 
 * USA.  
 *
 * [Java is a trademark or registered trademark of Sun Microsystems, Inc. 
 * in the United States and other countries.]
 *
 * -----------------------
 * EntityPackageTests.java
 * -----------------------
 * (C) Copyright 2004, 2005, by Object Refinery Limited.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited);
 * Contributor(s):   -;
 *
 * $Id: EntityPackageTests.java,v 1.1 2007/10/10 20:38:52 vauchers Exp $
 *
 * Changes:
 * --------
 * 19-May-2004 : Version 1 (DG);
 * 19-Jan-2005 : Added main() method to run JUnit in text mode (DG);
 *
 */

package org.jfree.chart.entity.junit;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * A collection of tests for the org.jfree.chart.entity package.
 * <P>
 * These tests can be run using JUnit (http://www.junit.org).
 */
public class EntityPackageTests extends TestCase {

    /**
     * Returns a test suite to the JUnit test runner.
     *
     * @return The test suite.
     */
    public static Test suite() {
        TestSuite suite = new TestSuite("org.jfree.chart.entity");
        suite.addTestSuite(CategoryItemEntityTests.class);
        suite.addTestSuite(ContourEntityTests.class);
        suite.addTestSuite(LegendItemEntityTests.class);
        suite.addTestSuite(PieSectionEntityTests.class);
        suite.addTestSuite(StandardEntityCollectionTests.class);
        suite.addTestSuite(TickLabelEntityTests.class);
        suite.addTestSuite(XYItemEntityTests.class);
        return suite;
    }

    /**
     * Constructs the test suite.
     *
     * @param name  the suite name.
     */
    public EntityPackageTests(String name) {
        super(name);
    }
    
    /**
     * Runs the test suite using JUnit's text-based runner.
     * 
     * @param args  ignored.
     */
    public static void main(String[] args) {
        junit.textui.TestRunner.run(suite());
    }

}

