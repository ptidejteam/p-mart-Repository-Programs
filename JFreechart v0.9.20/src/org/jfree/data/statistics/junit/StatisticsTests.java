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
 * $Id: StatisticsTests.java,v 1.1 2007/10/10 19:46:30 vauchers Exp $
 *
 * Changes
 * -------
 * 25-Mar-2004 : Version 1 (DG);
 *
 */

package org.jfree.data.statistics.junit;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
     * @return The test suite.
     */
    public static Test suite() {
        return new TestSuite(StatisticsTests.class);
    }

    /**
     * Constructs a new set of tests.
     *
     * @param name  the name of the tests.
     */
    public StatisticsTests(final String name) {
        super(name);
    }

    /**
     * A simple test for the calculateMean(Collection) method.
     */
    public void testCalculateMean1() {
        List values = new ArrayList();
        values.add(new Double(9.0));
        values.add(new Double(3.0));
        values.add(new Double(2.0));
        values.add(new Double(2.0));
        double mean = Statistics.calculateMean(values);
        assertEquals(4.0, mean, 0.0000001);
    }
    
    /**
     * A simple test for the calculateMean(Number[]) method.
     */
    public void testCalculateMean2() {
        final Number[] values = new Number[3];
        values[0] = new Double(1);
        values[1] = new Double(2);
        values[2] = new Double(3);
        final double mean = Statistics.calculateMean(values);
        assertEquals(2.0, mean, 0.0000001);
    }
    
    /**
     * A test for the calculateMedian() method.
     */
    public void testCalculateMedian1() {
        final List values = new ArrayList();
        values.add(new Double(1.0));
        final double median = Statistics.calculateMedian(values);
        assertEquals(1.0, median, 0.0000001);
    }

    /**
     * A test for the calculateMedian() method.
     */
    public void testCalculateMedian2() {
        final List values = new ArrayList();
        values.add(new Double(2.0));
        values.add(new Double(1.0));
        final double median = Statistics.calculateMedian(values);
        assertEquals(1.5, median, 0.0000001);
    }

    /**
     * Problem the median calculation.
     */
    public void testCalculateMedian3() {
        final List values = new ArrayList();
        values.add(new Double(1.0));
        values.add(new Double(2.0));
        values.add(new Double(3.0));
        values.add(new Double(6.0));
        values.add(new Double(5.0));
        values.add(new Double(4.0));
        final double median = Statistics.calculateMedian(values);
        assertEquals(3.5, median, 0.0000001);
    }

    /**
     * A test for the calculateMedian() method.
     */
    public void testCalculateMedian4() {
        final List values = new ArrayList();
        values.add(new Double(7.0));
        values.add(new Double(2.0));
        values.add(new Double(3.0));
        values.add(new Double(5.0));
        values.add(new Double(4.0));
        values.add(new Double(6.0));
        values.add(new Double(1.0));
        final double median = Statistics.calculateMedian(values);
        assertEquals(4.0, median, 0.0000001);
    }
    
    /**
     * A test using some real data that caused a problem at one point.
     */
    public void testCalculateMedian5() {
        final List values = new ArrayList();
        values.add(new Double(11.228692993861783));
        values.add(new Double(11.30823353859889));
        values.add(new Double(11.75312904769314));
        values.add(new Double(11.825102897465314));
        values.add(new Double(10.184252778401783));
        values.add(new Double(12.207951828057766));
        values.add(new Double(10.68841994040566));
        values.add(new Double(12.099522004479438));
        values.add(new Double(11.508874945056881));
        values.add(new Double(12.052517729558513));
        values.add(new Double(12.401481645578734));
        values.add(new Double(12.185377793028543));
        values.add(new Double(10.666372951930315));
        values.add(new Double(11.680978041499548));
        values.add(new Double(11.06528277406718));
        values.add(new Double(11.36876492904596));
        values.add(new Double(11.927565516175939));
        values.add(new Double(11.39307785978655));
        values.add(new Double(11.989603679523857));
        values.add(new Double(12.009834360354864));
        values.add(new Double(10.653351822461559));
        values.add(new Double(11.851776254376754));
        values.add(new Double(11.045441544755946));
        values.add(new Double(11.993674040560624));
        values.add(new Double(12.898219965238944));
        values.add(new Double(11.97095782819647));
        values.add(new Double(11.73234406745488));
        values.add(new Double(11.649006017243991));
        values.add(new Double(12.20549704915365));
        values.add(new Double(11.799723639384919));
        values.add(new Double(11.896208658005628));
        values.add(new Double(12.164149111823424));
        values.add(new Double(12.042795103513766));
        values.add(new Double(12.114839532596426));
        values.add(new Double(12.166609097075824));
        values.add(new Double(12.183017546225935));
        values.add(new Double(11.622009125845342));
        values.add(new Double(11.289365786738633));
        values.add(new Double(12.462984323671568));
        values.add(new Double(11.573494921030598));
        values.add(new Double(10.862867940485804));
        values.add(new Double(12.018186939664872));
        values.add(new Double(10.418046849313018));
        values.add(new Double(11.326344465881341));
        double median = Statistics.calculateMedian(values, true);
        assertEquals(11.812413268425116, median, 0.000001);
        Collections.sort(values);
        double median2 = Statistics.calculateMedian(values, false);
        assertEquals(11.812413268425116, median2, 0.000001);
    }

    /**
     * A test for the calculateMedian() method.
     */
    public void testCalculateMedian6() {
        final List values = new ArrayList();
        values.add(new Double(7.0));
        values.add(new Double(2.0));
        values.add(new Double(3.0));
        values.add(new Double(5.0));
        values.add(new Double(4.0));
        values.add(new Double(6.0));
        values.add(new Double(1.0));
        final double median = Statistics.calculateMedian(values, 0, 2);
        assertEquals(3.0, median, 0.0000001);
    }
    
    /**
     * A simple test for the correlation calculation.
     */
    public void testCorrelation1() {
        final Number[] data1 = new Number[3];
        data1[0] = new Double(1);
        data1[1] = new Double(2);
        data1[2] = new Double(3);        
        final Number[] data2 = new Number[3];
        data2[0] = new Double(1);
        data2[1] = new Double(2);
        data2[2] = new Double(3);    
        final double r = Statistics.getCorrelation(data1, data2);
        assertTrue(NumberUtils.equal(r, 1.0));        
    }

    /**
     * A simple test for the correlation calculation.
     * 
     * http://trochim.human.cornell.edu/kb/statcorr.htm
     */
    public void testCorrelation2() {
        final Number[] data1 = new Number[20];
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
        final Number[] data2 = new Number[20];
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
        final double r = Statistics.getCorrelation(data1, data2);
        assertTrue(NumberUtils.equal(r, 0.7306356862792885));        
    }

}
