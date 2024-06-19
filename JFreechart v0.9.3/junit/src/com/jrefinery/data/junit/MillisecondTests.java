package com.jrefinery.data.junit;

import junit.framework.*;
import com.jrefinery.data.*;
import com.jrefinery.date.*;
import java.util.Date;
import java.util.TimeZone;

public class MillisecondTests extends TestCase {

    /**
     * Returns the tests as a test suite.
     */
    public static Test suite() {
        return new TestSuite(MillisecondTests.class);
    }

    /**
     * Constructs a new set of tests.
     * @param The name of the tests.
     */
    public MillisecondTests(String name) {
        super(name);
    }

    /**
     * Common test setup.
     */
    protected void setUp() {

    }

    /**
     * Test that a Day instance is equal to itself.
     *
     * SourceForge Bug ID: 558850.
     */
    public void testEqualsSelf() {
        Millisecond millisecond = new Millisecond();
        this.assertTrue(millisecond.equals(millisecond));
    }

    public void testEquals() {
        Day day1 = new Day(29, SerialDate.MARCH, 2002);
        Hour hour1 = new Hour(15, day1);
        Minute minute1 = new Minute(15, hour1);
        Second second1 = new Second(34, minute1);
        Millisecond milli1 = new Millisecond(999, second1);
        Day day2 = new Day(29, SerialDate.MARCH, 2002);
        Hour hour2 = new Hour(15, day2);
        Minute minute2 = new Minute(15, hour2);
        Second second2 = new Second(34, minute2);
        Millisecond milli2 = new Millisecond(999, second2);
        this.assertTrue(milli1.equals(milli2));
    }

    /**
     * In GMT, the 4.55:59.123pm on 21 Mar 2002 is java.util.Date(1016729759123L).  Use this to check the
     * Second constructor.
     */
    public void testDateConstructor1() {

        TimeZone zone = TimeZone.getTimeZone("GMT");
        Millisecond m1 = new Millisecond(new Date(1016729759122L), zone);
        Millisecond m2 = new Millisecond(new Date(1016729759123L), zone);

        this.assertEquals(122, m1.getMillisecond());
        this.assertEquals(1016729759122L, m1.getEnd(zone));

        this.assertEquals(123, m2.getMillisecond());
        this.assertEquals(1016729759123L, m2.getStart(zone));

    }

    /**
     * In Tallinn, the 4.55:59.123pm on 21 Mar 2002 is java.util.Date(1016722559123L).  Use this to check the
     * Second constructor.
     */
    public void testDateConstructor2() {

        TimeZone zone = TimeZone.getTimeZone("Europe/Tallinn");
        Millisecond m1 = new Millisecond(new Date(1016722559122L), zone);
        Millisecond m2 = new Millisecond(new Date(1016722559123L), zone);

        this.assertEquals(122, m1.getMillisecond());
        this.assertEquals(1016722559122L, m1.getEnd(zone));

        this.assertEquals(123, m2.getMillisecond());
        this.assertEquals(1016722559123L, m2.getStart(zone));

    }

}
