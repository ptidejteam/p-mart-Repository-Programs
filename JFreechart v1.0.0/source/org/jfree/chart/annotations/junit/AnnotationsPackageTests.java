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
 * ----------------------------
 * AnnotationsPackageTests.java
 * ----------------------------
 * (C) Copyright 2003-2005, by Object Refinery Limited.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited);
 * Contributor(s):   -;
 *
 * $Id: AnnotationsPackageTests.java,v 1.1 2007/10/10 20:11:33 vauchers Exp $
 *
 * Changes:
 * --------
 * 19-Aug-2003 : Version 1 (DG);
 * 29-Sep-2004 : Added tests for XYShapeAnnotation class (DG);
 * 19-Jan-2005 : Added tests for XYBoxAnnotation class (DG);
 * 29-Jul-2005 : Added tests for new CategoryLineAnnotation class (DG);
 *
 */

package org.jfree.chart.annotations.junit;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * A collection of tests for the <code>org.jfree.chart.annotations</code> 
 * package.  These tests can be run using JUnit (http://www.junit.org).
 */
public class AnnotationsPackageTests extends TestCase {

    /**
     * Returns a test suite to the JUnit test runner.
     *
     * @return The test suite.
     */
    public static Test suite() {
        TestSuite suite = new TestSuite("org.jfree.chart.annotations");
        suite.addTestSuite(CategoryLineAnnotationTests.class);
        suite.addTestSuite(CategoryTextAnnotationTests.class);
        suite.addTestSuite(TextAnnotationTests.class);
        suite.addTestSuite(XYBoxAnnotationTests.class);
        suite.addTestSuite(XYDrawableAnnotationTests.class);
        suite.addTestSuite(XYImageAnnotationTests.class);
        suite.addTestSuite(XYLineAnnotationTests.class);
        suite.addTestSuite(XYPointerAnnotationTests.class);
        suite.addTestSuite(XYShapeAnnotationTests.class);
        suite.addTestSuite(XYTextAnnotationTests.class);
        return suite;
    }

    /**
     * Constructs the test suite.
     *
     * @param name  the suite name.
     */
    public AnnotationsPackageTests(String name) {
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

