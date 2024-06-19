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
 * --------------------
 * StatisticsTests.java
 * --------------------
 * (C) Copyright 2004 by Object Refinery Limited and Contributors.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited);
 * Contributor(s):   -;
 *
 * $Id: StatisticsTests.java,v 1.1 2007/10/10 19:39:21 vauchers Exp $
 *
 * Changes
 * -------
 * 25-Mar-2004 : Version 1 (DG);
 *
 */

package org.jfree.data.statistics.junit;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.jfree.data.statistics.Statistics;
import org.jfree.util.NumberUtils;

/**
 * Tests for the {@link Statistics} class.
 */
public class StatisticsTests extends TestCase {
    
    /**
     * Returns the tests as a test suite.
     *
     * @return the test suite.
     */
    public static Test suite() {
        return new TestSuite(StatisticsTests.class);
    }

    /**
     * Constructs a new set of tests.
     *
     * @param  name the name of the tests.
     */
    public StatisticsTests(String name) {
        super(name);
    }

    /**
     * A simple test for the average calculation.
     */
    public void testAverage() {
        Number[] data = new Number[3];
        data[0] = new Double(1);
        data[1] = new Double(2);
        data[2] = new Double(3);
        double average = Statistics.getAverage(data);
        assertTrue(NumberUtils.equal(average, 2.0));
    }
    
    /**
     * A simple test for the correlation calculation.
     */
    public void testCorrelation1() {
        Number[] data1 = new Number[3];
        data1[0] = new Double(1);
        data1[1] = new Double(2);
        data1[2] = new Double(3);        
        Number[] data2 = new Number[3];
        data2[0] = new Double(1);
        data2[1] = new Double(2);
        data2[2] = new Double(3);    
        double r = Statistics.getCorrelation(data1, data2);
        assertTrue(NumberUtils.equal(r, 1.0));        
    }

    /**
     * A simple test for the correlation calculation.
     * 
     * http://trochim.human.cornell.edu/kb/statcorr.htm
     */
    public void testCorrelation2() {
        Number[] data1 = new Number[20];
        data1[0] = new Double(68);
        data1[1] = new Double(71);
        data1[2] = new Double(62);        
        data1[3] = new Double(75);        
        data1[4] = new Double(58);        
        data1[5] = new Double(60);        
        data1[6] = new Double(67);        
        data1[7] = new Double(68);        
        data1[8] = new Double(71);        
        data1[9] = new Double(69);        
        data1[10] = new Double(68);        
        data1[11] = new Double(67);        
        data1[12] = new Double(63);        
        data1[13] = new Double(62);        
        data1[14] = new Double(60);        
        data1[15] = new Double(63);        
        data1[16] = new Double(65);        
        data1[17] = new Double(67);        
        data1[18] = new Double(63);        
        data1[19] = new Double(61);        
        Number[] data2 = new Number[20];
        data2[0] = new Double(4.1);
        data2[1] = new Double(4.6);
        data2[2] = new Double(3.8);    
        data2[3] = new Double(4.4);    
        data2[4] = new Double(3.2);    
        data2[5] = new Double(3.1);    
        data2[6] = new Double(3.8);    
        data2[7] = new Double(4.1);    
        data2[8] = new Double(4.3);    
        data2[9] = new Double(3.7);    
        data2[10] = new Double(3.5);    
        data2[11] = new Double(3.2);    
        data2[12] = new Double(3.7);    
        data2[13] = new Double(3.3);    
        data2[14] = new Double(3.4);    
        data2[15] = new Double(4.0);    
        data2[16] = new Double(4.1);    
        data2[17] = new Double(3.8);    
        data2[18] = new Double(3.4);    
        data2[19] = new Double(3.6);    
        double r = Statistics.getCorrelation(data1, data2);
        assertTrue(NumberUtils.equal(r, 0.7306356862792885));        
    }

}
