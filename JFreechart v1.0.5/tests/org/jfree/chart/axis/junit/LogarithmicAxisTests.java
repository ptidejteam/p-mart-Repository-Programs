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
 * -------------------------
 * LogarithmicAxisTests.java
 * -------------------------
 * (C) Copyright 2003-2005, by Object Refinery Limited and Contributors.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited);
 * Contributor(s):   -;
 *
 * $Id: LogarithmicAxisTests.java,v 1.1 2007/10/10 20:38:51 vauchers Exp $
 *
 * Changes
 * -------
 * 26-Mar-2003 : Version 1 (DG);
 * 02-Mar-2007 : Added tests from bug report 880597 (DG);
 *
 */

package org.jfree.chart.axis.junit;

import java.awt.geom.Rectangle2D;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.jfree.chart.axis.LogarithmicAxis;
import org.jfree.ui.RectangleEdge;

/**
 * Tests for the {@link LogarithmicAxis} class.
 */
public class LogarithmicAxisTests extends TestCase {

    static class MyLogarithmicAxis extends LogarithmicAxis {

        public MyLogarithmicAxis(String label) {
            super(label);
        }
        
        /* (non-Javadoc)
         * @see org.jfree.chart.axis.LogarithmicAxis#switchedLog10(double)
         */
        protected double switchedLog10(double val) {
            return super.switchedLog10(val);
        }
        
    }
    
    /** Tolerance for floating point comparisons */
    public static double EPSILON = 0.000001;
    
    MyLogarithmicAxis axis = null;

    /**
     * Returns the tests as a test suite.
     *
     * @return The test suite.
     */
    public static Test suite() {
        return new TestSuite(LogarithmicAxisTests.class);
    }

    /**
     * Constructs a new set of tests.
     *
     * @param name  the name of the tests.
     */
    public LogarithmicAxisTests(String name) {
        super(name);
    }

    /**
     * Sets up a new axis.
     * 
     * @throws Exception
     */
    protected void setUp() throws Exception {
        this.axis = new MyLogarithmicAxis("Value (log)");
        this.axis.setAllowNegativesFlag(false);
        this.axis.setLog10TickLabelsFlag(false);
        this.axis.setLowerMargin(0.0);
        this.axis.setUpperMargin(0.0);

        this.axis.setLowerBound(0.2);
        this.axis.setUpperBound(100.0);
    }

    /**
     * Serialize an instance, restore it, and check for equality.
     */
    public void testSerialization() {

        LogarithmicAxis a1 = new LogarithmicAxis("Test Axis");
        LogarithmicAxis a2 = null;

        try {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            ObjectOutput out = new ObjectOutputStream(buffer);
            out.writeObject(a1);
            out.close();

            ObjectInput in = new ObjectInputStream(new ByteArrayInputStream(
                    buffer.toByteArray()));
            a2 = (LogarithmicAxis) in.readObject();
            in.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        assertEquals(a1, a2);

    }
        
    /** 
     * Test if adjustedLog10 and adjustedPow10 are inverses of each other 
     */
     public void testAdjustedLog10() {
         checkLogPowRoundTrip(20);
         checkLogPowRoundTrip(10);
         checkLogPowRoundTrip(5);
         checkLogPowRoundTrip(2);
         checkLogPowRoundTrip(1);
         checkLogPowRoundTrip(0.5);
         checkLogPowRoundTrip(0.2);
         checkLogPowRoundTrip(0.0001);
     }

     private void checkLogPowRoundTrip(double value) {
         assertEquals("log(pow(x)) = x", value, this.axis.adjustedLog10(
                 this.axis.adjustedPow10(value)), EPSILON);
         assertEquals("pow(log(x)) = x", value, this.axis.adjustedPow10(
                 this.axis.adjustedLog10(value)), EPSILON);
     }

     /** 
      * Test if switchedLog10 and switchedPow10 are inverses of each other 
      */
      public void testSwitchedLog10() {
          assertFalse("Axis should not allow negative values",
                  this.axis.getAllowNegativesFlag());
                
          assertEquals(Math.log(0.5) / LogarithmicAxis.LOG10_VALUE,
                  this.axis.switchedLog10(0.5), EPSILON);

          checkSwitchedLogPowRoundTrip(20);
          checkSwitchedLogPowRoundTrip(10);
          checkSwitchedLogPowRoundTrip(5);
          checkSwitchedLogPowRoundTrip(2);
          checkSwitchedLogPowRoundTrip(1);
          checkSwitchedLogPowRoundTrip(0.5);
          checkSwitchedLogPowRoundTrip(0.2);
          checkSwitchedLogPowRoundTrip(0.0001);
      }

      private void checkSwitchedLogPowRoundTrip(double value) {
          assertEquals("log(pow(x)) = x", value, this.axis.switchedLog10(
                  this.axis.switchedPow10(value)), EPSILON);
          assertEquals("pow(log(x)) = x", value, this.axis.switchedPow10(
                  this.axis.switchedLog10(value)), EPSILON);
      }

      /**
       * Test of java2DToValue method.
       */
      public void testJava2DToValue() {
          Rectangle2D plotArea = new Rectangle2D.Double(22, 33, 500, 500);
          RectangleEdge edge = RectangleEdge.BOTTOM;

          // set axis bounds to be both greater than 1
          this.axis.setRange(10, 20);
          checkPointsToValue(edge, plotArea);

          // check for bounds interval that includes 1
          this.axis.setRange(0.5, 10);
          checkPointsToValue(edge, plotArea);

          // check for bounds interval that includes 1
          this.axis.setRange(0.2, 20);
          checkPointsToValue(edge, plotArea);

          // check for both bounds smaller than 1
          this.axis.setRange(0.2, 0.7);
          checkPointsToValue(edge, plotArea);
      }

      /**
       * Test of valueToJava2D method.
       */
      public void testValueToJava2D() {
          Rectangle2D plotArea = new Rectangle2D.Double(22, 33, 500, 500);
          RectangleEdge edge = RectangleEdge.BOTTOM;

          // set axis bounds to be both greater than 1
          this.axis.setRange(10, 20);
          checkPointsToJava2D(edge, plotArea);

          // check for bounds interval that includes 1
          this.axis.setRange(0.5, 10);
          checkPointsToJava2D(edge, plotArea);

          // check for bounds interval that includes 1
          this.axis.setRange(0.2, 20);
          checkPointsToJava2D(edge, plotArea);

          // check for both bounds smaller than 1
          this.axis.setRange(0.2, 0.7);
          checkPointsToJava2D(edge, plotArea);
      }

      private void checkPointsToJava2D(RectangleEdge edge, 
              Rectangle2D plotArea) {
          assertEquals("Left most point on the axis should be beginning of "
                  + "range.", plotArea.getX(), this.axis.valueToJava2D(
                  this.axis.getLowerBound(), plotArea, edge), EPSILON);
          assertEquals("Right most point on the axis should be end of range.", 
                  plotArea.getX() + plotArea.getWidth(), 
                  this.axis.valueToJava2D(this.axis.getUpperBound(), 
                  plotArea, edge), EPSILON);
          assertEquals("Center point on the axis should geometric mean of the bounds.", 
                  plotArea.getX() + (plotArea.getWidth() / 2), 
                  this.axis.valueToJava2D(Math.sqrt(this.axis.getLowerBound() 
                  * this.axis.getUpperBound()), plotArea, edge), EPSILON);
        }

    /** 
     * Check the translation java2D to value for left, right, and center point.
     */
     private void checkPointsToValue(RectangleEdge edge, Rectangle2D plotArea) {
         assertEquals("Right most point on the axis should be end of range.",
                 this.axis.getUpperBound(), this.axis.java2DToValue(
                 plotArea.getX() + plotArea.getWidth(), plotArea, edge), 
                 EPSILON);

         assertEquals("Left most point on the axis should be beginning of "
                 + "range.", this.axis.getLowerBound(), 
                 this.axis.java2DToValue(plotArea.getX(), plotArea, edge), 
                 EPSILON);

         assertEquals("Center point on the axis should geometric mean of the "
                 + "bounds.", Math.sqrt(this.axis.getUpperBound() 
                 * this.axis.getLowerBound()), this.axis.java2DToValue(
                 plotArea.getX() + (plotArea.getWidth() / 2), plotArea, edge), 
                 EPSILON);
    }

    /**
     * Runs all tests in this class.
     * 
     * @param args  ignored.
     */
    public static void main(String[] args) {
        junit.textui.TestRunner.run(LogarithmicAxisTests.class);
    }

}
