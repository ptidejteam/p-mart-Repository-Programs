/*
 * Created on 04-Jun-2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package org.jfree.chart.demo;

import org.jfree.data.time.Day;
import org.jfree.data.time.Hour;
import org.jfree.data.time.Millisecond;
import org.jfree.data.time.Minute;
import org.jfree.data.time.Month;
import org.jfree.data.time.Quarter;
import org.jfree.data.time.Second;
import org.jfree.data.time.Week;
import org.jfree.data.time.Year;

/**
 * @author dgilbert
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class TimePeriodToStringTest {

    public static void main(String[] args) {

        Millisecond millisecond = new Millisecond();
        System.out.println(millisecond.toString());
        Second second = new org.jfree.data.time.Second();
        System.out.println(second.toString());
        Minute minute = new Minute();
        System.out.println(minute.toString());
        Hour hour = new Hour();
        System.out.println(hour.toString());
        Day day = new Day();
        System.out.println(day.toString());
        Week week = new Week();
        System.out.println(week.toString());
        Month month = new Month();
        System.out.println(month.toString());
        Quarter quarter = new Quarter();
        System.out.println(quarter.toString());
        Year year = new Year();
        System.out.println(year.toString());

    }

}
