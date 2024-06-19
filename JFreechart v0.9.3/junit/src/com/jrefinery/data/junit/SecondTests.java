package com.jrefinery.data.junit;

import junit.framework.*;
import com.jrefinery.data.*;
import com.jrefinery.date.*;
import java.util.Date;
import java.util.TimeZone;

public class SecondTests extends TestCase {

    /**
     * Returns the tests as a test suite.
     */
    public static Test suite() {
        return new TestSuite(SecondTests.class);
    }

    /**
     * Constructs a new set of tests.
     * @param The name of the tests.
     */
    public SecondTests(String name) {
        super(name);
    }

    /**
     * Common test setup.
     */
    protected void setUp() {

    }

    /**
     * Test that a Second instance is equal to itself.
     *
     * SourceForge Bug ID: 558850.
     */
    public void testEqualsSelf() {
        Second second = new Second();
        this.assertTrue(second.equals(second));
    }

    public void testEquals() {
        Day day1 = new Day(29, SerialDate.MARCH, 2002);
        Hour hour1 = new Hour(15, day1);
        Minute minute1 = new Minute(15, hour1);
        Second second1 = new Second(34, minute1);
        Day day2 = new Day(29, SerialDate.MARCH, 2002);
        Hour hour2 = new Hour(15, day2);
        Minute minute2 = new Minute(15, hour2);
        Second second2 = new Second(34, minute2);
        this.assertTrue(second1.equals(second2));
    }

    /**
     * In GMT, the 4.55:59pm on 21 Mar 2002 is java.util.Date(1016729759000L).  Use this to check the
     * Second constructor.
     */
    public void testDateConstructor1() {

        TimeZone zone = TimeZone.getTimeZone("GMT");
        Second s1 = new Second(new Date(1016729758999L), zone);
        Second s2 = new Second(new Date(1016729759000L), zone);

        this.assertEquals(58, s1.getSecond());
        this.assertEquals(1016729758999L, s1.getEnd(zone));

        this.assertEquals(59, s2.getSecond());
        this.assertEquals(1016729759000L, s2.getStart(zone));

    }

    /**
     * In Chicago, the 4.55:59pm on 21 Mar 2002 is java.util.Date(1016751359000L).  Use this to check the
     * Second constructor.
     */
    public void testDateConstructor2() {

        TimeZone zone = TimeZone.getTimeZone("America/Chicago");
        Second s1 = new Second(new Date(1016751358999L), zone);
        Second s2 = new Second(new Date(1016751359000L), zone);

        this.assertEquals(58, s1.getSecond());
        this.assertEquals(1016751358999L, s1.getEnd(zone));

        this.assertEquals(59, s2.getSecond());
        this.assertEquals(1016751359000L, s2.getStart(zone));

    }

}
