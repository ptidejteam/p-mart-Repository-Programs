package com.jrefinery.data.junit;

import junit.framework.*;
import com.jrefinery.data.*;
import com.jrefinery.date.*;
import java.util.Date;
import java.util.TimeZone;

public class MinuteTests extends TestCase {

    /**
     * Returns the tests as a test suite.
     */
    public static Test suite() {
        return new TestSuite(MinuteTests.class);
    }

    /**
     * Constructs a new set of tests.
     * @param The name of the tests.
     */
    public MinuteTests(String name) {
        super(name);
    }

    /**
     * Common test setup.
     */
    protected void setUp() {

    }

    /**
     * Test that a Minute instance is equal to itself.
     *
     * SourceForge Bug ID: 558850.
     */
    public void testEqualsSelf() {
        Minute minute = new Minute();
        this.assertTrue(minute.equals(minute));
    }

    public void testEquals() {
        Day day1 = new Day(29, SerialDate.MARCH, 2002);
        Hour hour1 = new Hour(15, day1);
        Minute minute1 = new Minute(15, hour1);
        Day day2 = new Day(29, SerialDate.MARCH, 2002);
        Hour hour2 = new Hour(15, day2);
        Minute minute2 = new Minute(15, hour2);
        this.assertTrue(minute1.equals(minute2));
    }

    /**
     * In GMT, the 4.55pm on 21 Mar 2002 is java.util.Date(1016729700000L).  Use this to check the
     * Minute constructor.
     */
    public void testDateConstructor1() {

        TimeZone zone = TimeZone.getTimeZone("GMT");
        Minute m1 = new Minute(new Date(1016729699999L), zone);
        Minute m2 = new Minute(new Date(1016729700000L), zone);

        this.assertEquals(54, m1.getMinute());
        this.assertEquals(1016729699999L, m1.getEnd(zone));

        this.assertEquals(55, m2.getMinute());
        this.assertEquals(1016729700000L, m2.getStart(zone));

    }

    /**
     * In Singapore, the 4.55pm on 21 Mar 2002 is java.util.Date(1,014,281,700,000L).  Use this to check the
     * Minute constructor.
     */
    public void testDateConstructor2() {

        TimeZone zone = TimeZone.getTimeZone("Asia/Singapore");
        Minute m1 = new Minute(new Date(1016700899999L), zone);
        Minute m2 = new Minute(new Date(1016700900000L), zone);

        this.assertEquals(54, m1.getMinute());
        this.assertEquals(1016700899999L, m1.getEnd(zone));

        this.assertEquals(55, m2.getMinute());
        this.assertEquals(1016700900000L, m2.getStart(zone));

    }

}
