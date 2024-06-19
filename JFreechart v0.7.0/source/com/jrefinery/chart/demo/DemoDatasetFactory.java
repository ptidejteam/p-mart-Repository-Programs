/* ===============
 * JFreeChart Demo
 * ===============
 *
 * Project Info:  http://www.jrefinery.com/jfreechart;
 * Project Lead:  David Gilbert (david.gilbert@jrefinery.com);
 *
 * (C) Copyright 2000, 2001 by Simba Management Limited and Contributors.
 *
 * This program is free software; you can redistribute it and/or modify it under the terms
 * of the GNU General Public License as published by the Free Software Foundation;
 * either version 2 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program;
 * if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston,
 * MA 02111-1307, USA.
 *
 * -----------------------
 * DemoDatasetFactory.java
 * -----------------------
 * (C) Copyright 2001 by Simba Management Limited.
 *
 * Original Author:  David Gilbert (for Simba Management Limited);
 * Contributor(s):   -;
 *
 * $Id: DemoDatasetFactory.java,v 1.1 2007/10/10 18:54:39 vauchers Exp $
 *
 * Changes
 * -------
 * 10-Dec-2001 : Version 1 (DG);
 *
 */

package com.jrefinery.chart.demo;

import com.jrefinery.data.*;
import com.jrefinery.date.*;

public class DemoDatasetFactory {

    /**
     * Returns a dataset consisting of one annual series.
     */
    public static TimeSeriesCollection createTimeSeriesCollection1() {

        BasicTimeSeries t1 = new BasicTimeSeries("Annual", "Year", "Value", Year.class);
        try {
            t1.add(new Year(1990), new Double(50.1));
            t1.add(new Year(1991), new Double(12.3));
            t1.add(new Year(1992), new Double(23.9));
            t1.add(new Year(1993), new Double(83.4));
            t1.add(new Year(1994), new Double(-34.7));
            t1.add(new Year(1995), new Double(76.5));
            t1.add(new Year(1996), new Double(10.0));
            t1.add(new Year(1997), new Double(-14.7));
            t1.add(new Year(1998), new Double(43.9));
            t1.add(new Year(1999), new Double(49.6));
            t1.add(new Year(2000), new Double(37.2));
            t1.add(new Year(2001), new Double(17.1));
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
        }

        return new TimeSeriesCollection(t1);

    }

    /**
     * Creates a time series collection containing JPY/GBP exchange rates.
     */
    public static TimeSeriesCollection createTimeSeriesCollection2() {

        TimeSeriesCollection data = new TimeSeriesCollection();
        data.addSeries(createJPYTimeSeries());
        return data;

    }

    /**
     * Creates a time series collection containing USD/GBP and EUR/GBP exchange rates.
     */
    public static TimeSeriesCollection createTimeSeriesCollection3() {

        TimeSeriesCollection collection = new TimeSeriesCollection();
        collection.addSeries(createUSDTimeSeries());
        collection.addSeries(createEURTimeSeries());
        return collection;

    }

    /**
     * Returns a time series of the daily USD/GBP exchange rates in 2001 (to date), for use in
     * the JFreeChart demonstration application.
     * <P>
     * You wouldn't normally create a time series in this way.  Typically, values would
     * be read from a database.
     *
     */
    public static BasicTimeSeries createUSDTimeSeries() {

        BasicTimeSeries t1 = new BasicTimeSeries("USD/GBP");
        try {
            t1.add(new Day(2, SerialDate.JANUARY, 2001), new Double(1.4956));
            t1.add(new Day(3, SerialDate.JANUARY, 2001), new Double(1.5047));
            t1.add(new Day(4, SerialDate.JANUARY, 2001), new Double(1.4931));
            t1.add(new Day(5, SerialDate.JANUARY, 2001), new Double(1.4955));
            t1.add(new Day(8, SerialDate.JANUARY, 2001), new Double(1.4994));
            t1.add(new Day(9, SerialDate.JANUARY, 2001), new Double(1.4911));
            t1.add(new Day(10, SerialDate.JANUARY, 2001), new Double(1.4903));
            t1.add(new Day(11, SerialDate.JANUARY, 2001), new Double(1.4947));
            t1.add(new Day(12, SerialDate.JANUARY, 2001), new Double(1.4784));
            t1.add(new Day(15, SerialDate.JANUARY, 2001), new Double(1.4787));
            t1.add(new Day(16, SerialDate.JANUARY, 2001), new Double(1.4702));
            t1.add(new Day(17, SerialDate.JANUARY, 2001), new Double(1.4729));
            t1.add(new Day(18, SerialDate.JANUARY, 2001), new Double(1.4760));
            t1.add(new Day(19, SerialDate.JANUARY, 2001), new Double(1.4685));
            t1.add(new Day(22, SerialDate.JANUARY, 2001), new Double(1.4609));
            t1.add(new Day(23, SerialDate.JANUARY, 2001), new Double(1.4709));
            t1.add(new Day(24, SerialDate.JANUARY, 2001), new Double(1.4576));
            t1.add(new Day(25, SerialDate.JANUARY, 2001), new Double(1.4589));
            t1.add(new Day(26, SerialDate.JANUARY, 2001), new Double(1.4568));
            t1.add(new Day(29, SerialDate.JANUARY, 2001), new Double(1.4566));
            t1.add(new Day(30, SerialDate.JANUARY, 2001), new Double(1.4604));
            t1.add(new Day(31, SerialDate.JANUARY, 2001), new Double(1.4616));
            t1.add(new Day(1, SerialDate.FEBRUARY, 2001), new Double(1.4777));
            t1.add(new Day(2, SerialDate.FEBRUARY, 2001), new Double(1.4687));
            t1.add(new Day(5, SerialDate.FEBRUARY, 2001), new Double(1.4753));
            t1.add(new Day(6, SerialDate.FEBRUARY, 2001), new Double(1.4605));
            t1.add(new Day(7, SerialDate.FEBRUARY, 2001), new Double(1.4619));
            t1.add(new Day(8, SerialDate.FEBRUARY, 2001), new Double(1.4453));
            t1.add(new Day(9, SerialDate.FEBRUARY, 2001), new Double(1.4463));
            t1.add(new Day(12, SerialDate.FEBRUARY, 2001), new Double(1.4521));
            t1.add(new Day(13, SerialDate.FEBRUARY, 2001), new Double(1.4517));
            t1.add(new Day(14, SerialDate.FEBRUARY, 2001), new Double(1.4601));
            t1.add(new Day(15, SerialDate.FEBRUARY, 2001), new Double(1.4500));
            t1.add(new Day(16, SerialDate.FEBRUARY, 2001), new Double(1.4517));
            t1.add(new Day(19, SerialDate.FEBRUARY, 2001), new Double(1.4459));
            t1.add(new Day(20, SerialDate.FEBRUARY, 2001), new Double(1.4449));
            t1.add(new Day(21, SerialDate.FEBRUARY, 2001), new Double(1.4447));
            t1.add(new Day(22, SerialDate.FEBRUARY, 2001), new Double(1.4465));
            t1.add(new Day(23, SerialDate.FEBRUARY, 2001), new Double(1.4487));
            t1.add(new Day(26, SerialDate.FEBRUARY, 2001), new Double(1.4417));
            t1.add(new Day(27, SerialDate.FEBRUARY, 2001), new Double(1.4420));
            t1.add(new Day(28, SerialDate.FEBRUARY, 2001), new Double(1.4421));
            t1.add(new Day(1, SerialDate.MARCH, 2001), new Double(1.4547));
            t1.add(new Day(2, SerialDate.MARCH, 2001), new Double(1.4741));
            t1.add(new Day(5, SerialDate.MARCH, 2001), new Double(1.4686));
            t1.add(new Day(6, SerialDate.MARCH, 2001), new Double(1.4667));
            t1.add(new Day(7, SerialDate.MARCH, 2001), new Double(1.4618));
            t1.add(new Day(8, SerialDate.MARCH, 2001), new Double(1.4685));
            t1.add(new Day(9, SerialDate.MARCH, 2001), new Double(1.4677));
            t1.add(new Day(12, SerialDate.MARCH, 2001), new Double(1.4660));
            t1.add(new Day(13, SerialDate.MARCH, 2001), new Double(1.4526));
            t1.add(new Day(14, SerialDate.MARCH, 2001), new Double(1.4483));
            t1.add(new Day(15, SerialDate.MARCH, 2001), new Double(1.4441));
            t1.add(new Day(16, SerialDate.MARCH, 2001), new Double(1.4303));
            t1.add(new Day(19, SerialDate.MARCH, 2001), new Double(1.4259));
            t1.add(new Day(20, SerialDate.MARCH, 2001), new Double(1.4283));
            t1.add(new Day(21, SerialDate.MARCH, 2001), new Double(1.4293));
            t1.add(new Day(22, SerialDate.MARCH, 2001), new Double(1.4192));
            t1.add(new Day(23, SerialDate.MARCH, 2001), new Double(1.4293));
            t1.add(new Day(26, SerialDate.MARCH, 2001), new Double(1.4334));
            t1.add(new Day(27, SerialDate.MARCH, 2001), new Double(1.4371));
            t1.add(new Day(28, SerialDate.MARCH, 2001), new Double(1.4347));
            t1.add(new Day(29, SerialDate.MARCH, 2001), new Double(1.4362));
            t1.add(new Day(30, SerialDate.MARCH, 2001), new Double(1.4217));
            t1.add(new Day(2, SerialDate.APRIL, 2001), new Double(1.4205));
            t1.add(new Day(3, SerialDate.APRIL, 2001), new Double(1.4270));
            t1.add(new Day(4, SerialDate.APRIL, 2001), new Double(1.4333));
            t1.add(new Day(5, SerialDate.APRIL, 2001), new Double(1.4287));
            t1.add(new Day(6, SerialDate.APRIL, 2001), new Double(1.4395));
            t1.add(new Day(9, SerialDate.APRIL, 2001), new Double(1.4494));
            t1.add(new Day(10, SerialDate.APRIL, 2001), new Double(1.4385));
            t1.add(new Day(11, SerialDate.APRIL, 2001), new Double(1.4348));
            t1.add(new Day(12, SerialDate.APRIL, 2001), new Double(1.4402));
            t1.add(new Day(17, SerialDate.APRIL, 2001), new Double(1.4314));
            t1.add(new Day(18, SerialDate.APRIL, 2001), new Double(1.4197));
            t1.add(new Day(19, SerialDate.APRIL, 2001), new Double(1.4365));
            t1.add(new Day(20, SerialDate.APRIL, 2001), new Double(1.4416));
            t1.add(new Day(23, SerialDate.APRIL, 2001), new Double(1.4396));
            t1.add(new Day(24, SerialDate.APRIL, 2001), new Double(1.4360));
            t1.add(new Day(25, SerialDate.APRIL, 2001), new Double(1.4397));
            t1.add(new Day(26, SerialDate.APRIL, 2001), new Double(1.4402));
            t1.add(new Day(27, SerialDate.APRIL, 2001), new Double(1.4366));
            t1.add(new Day(30, SerialDate.APRIL, 2001), new Double(1.4309));
            t1.add(new Day(1, SerialDate.MAY, 2001), new Double(1.4324));
            t1.add(new Day(2, SerialDate.MAY, 2001), new Double(1.4336));
            t1.add(new Day(3, SerialDate.MAY, 2001), new Double(1.4329));
            t1.add(new Day(4, SerialDate.MAY, 2001), new Double(1.4375));
            t1.add(new Day(8, SerialDate.MAY, 2001), new Double(1.4321));
            t1.add(new Day(9, SerialDate.MAY, 2001), new Double(1.4219));
            t1.add(new Day(10, SerialDate.MAY, 2001), new Double(1.4226));
            t1.add(new Day(11, SerialDate.MAY, 2001), new Double(1.4199));
            t1.add(new Day(14, SerialDate.MAY, 2001), new Double(1.4183));
            t1.add(new Day(15, SerialDate.MAY, 2001), new Double(1.4218));
            t1.add(new Day(16, SerialDate.MAY, 2001), new Double(1.4295));
            t1.add(new Day(17, SerialDate.MAY, 2001), new Double(1.4296));
            t1.add(new Day(18, SerialDate.MAY, 2001), new Double(1.4296));
            t1.add(new Day(21, SerialDate.MAY, 2001), new Double(1.4366));
            t1.add(new Day(22, SerialDate.MAY, 2001), new Double(1.4283));
            t1.add(new Day(23, SerialDate.MAY, 2001), new Double(1.4244));
            t1.add(new Day(24, SerialDate.MAY, 2001), new Double(1.4102));
            t1.add(new Day(25, SerialDate.MAY, 2001), new Double(1.4205));
            t1.add(new Day(29, SerialDate.MAY, 2001), new Double(1.4183));
            t1.add(new Day(30, SerialDate.MAY, 2001), new Double(1.4230));
            t1.add(new Day(31, SerialDate.MAY, 2001), new Double(1.4201));
            t1.add(new Day(1, SerialDate.JUNE, 2001), new Double(1.4148));
            t1.add(new Day(4, SerialDate.JUNE, 2001), new Double(1.4142));
            t1.add(new Day(5, SerialDate.JUNE, 2001), new Double(1.4095));
            t1.add(new Day(6, SerialDate.JUNE, 2001), new Double(1.3938));
            t1.add(new Day(7, SerialDate.JUNE, 2001), new Double(1.3886));
            t1.add(new Day(8, SerialDate.JUNE, 2001), new Double(1.3798));
            t1.add(new Day(11, SerialDate.JUNE, 2001), new Double(1.3726));
            t1.add(new Day(12, SerialDate.JUNE, 2001), new Double(1.3788));
            t1.add(new Day(13, SerialDate.JUNE, 2001), new Double(1.3878));
            t1.add(new Day(14, SerialDate.JUNE, 2001), new Double(1.4002));
            t1.add(new Day(15, SerialDate.JUNE, 2001), new Double(1.4033));
            t1.add(new Day(18, SerialDate.JUNE, 2001), new Double(1.4038));
            t1.add(new Day(19, SerialDate.JUNE, 2001), new Double(1.4023));
            t1.add(new Day(20, SerialDate.JUNE, 2001), new Double(1.3952));
            t1.add(new Day(21, SerialDate.JUNE, 2001), new Double(1.4142));
            t1.add(new Day(22, SerialDate.JUNE, 2001), new Double(1.4114));
            t1.add(new Day(25, SerialDate.JUNE, 2001), new Double(1.4141));
            t1.add(new Day(26, SerialDate.JUNE, 2001), new Double(1.4157));
            t1.add(new Day(27, SerialDate.JUNE, 2001), new Double(1.4136));
            t1.add(new Day(28, SerialDate.JUNE, 2001), new Double(1.4089));
            t1.add(new Day(29, SerialDate.JUNE, 2001), new Double(1.4066));
            t1.add(new Day(2, SerialDate.JULY, 2001), new Double(1.4154));
            t1.add(new Day(3, SerialDate.JULY, 2001), new Double(1.4072));
            t1.add(new Day(4, SerialDate.JULY, 2001), new Double(1.4064));
            t1.add(new Day(5, SerialDate.JULY, 2001), new Double(1.3995));
            t1.add(new Day(6, SerialDate.JULY, 2001), new Double(1.4070));
            t1.add(new Day(9, SerialDate.JULY, 2001), new Double(1.4094));
            t1.add(new Day(10, SerialDate.JULY, 2001), new Double(1.4113));
            t1.add(new Day(11, SerialDate.JULY, 2001), new Double(1.4143));
            t1.add(new Day(12, SerialDate.JULY, 2001), new Double(1.4061));
            t1.add(new Day(13, SerialDate.JULY, 2001), new Double(1.4008));
            t1.add(new Day(16, SerialDate.JULY, 2001), new Double(1.3999));
            t1.add(new Day(17, SerialDate.JULY, 2001), new Double(1.4003));
            t1.add(new Day(18, SerialDate.JULY, 2001), new Double(1.4155));
            t1.add(new Day(19, SerialDate.JULY, 2001), new Double(1.4165));
            t1.add(new Day(20, SerialDate.JULY, 2001), new Double(1.4282));
            t1.add(new Day(23, SerialDate.JULY, 2001), new Double(1.4190));
            t1.add(new Day(24, SerialDate.JULY, 2001), new Double(1.4200));
            t1.add(new Day(25, SerialDate.JULY, 2001), new Double(1.4276));
            t1.add(new Day(26, SerialDate.JULY, 2001), new Double(1.4275));
            t1.add(new Day(27, SerialDate.JULY, 2001), new Double(1.4233));
            t1.add(new Day(30, SerialDate.JULY, 2001), new Double(1.4246));
            t1.add(new Day(31, SerialDate.JULY, 2001), new Double(1.4254));
            t1.add(new Day(1, SerialDate.AUGUST, 2001), new Double(1.4319));
            t1.add(new Day(2, SerialDate.AUGUST, 2001), new Double(1.4321));
            t1.add(new Day(3, SerialDate.AUGUST, 2001), new Double(1.4293));
            t1.add(new Day(6, SerialDate.AUGUST, 2001), new Double(1.4190));
            t1.add(new Day(7, SerialDate.AUGUST, 2001), new Double(1.4176));
            t1.add(new Day(8, SerialDate.AUGUST, 2001), new Double(1.4139));
            t1.add(new Day(9, SerialDate.AUGUST, 2001), new Double(1.4214));
            t1.add(new Day(10, SerialDate.AUGUST, 2001), new Double(1.4266));
            t1.add(new Day(11, SerialDate.AUGUST, 2001), new Double(1.4220));
            t1.add(new Day(12, SerialDate.AUGUST, 2001), new Double(1.4210));
            t1.add(new Day(15, SerialDate.AUGUST, 2001), new Double(1.4383));
            t1.add(new Day(16, SerialDate.AUGUST, 2001), new Double(1.4431));
            t1.add(new Day(17, SerialDate.AUGUST, 2001), new Double(1.4445));
            t1.add(new Day(20, SerialDate.AUGUST, 2001), new Double(1.4444));
            t1.add(new Day(21, SerialDate.AUGUST, 2001), new Double(1.4483));
            t1.add(new Day(22, SerialDate.AUGUST, 2001), new Double(1.4556));
            t1.add(new Day(23, SerialDate.AUGUST, 2001), new Double(1.4468));
            t1.add(new Day(24, SerialDate.AUGUST, 2001), new Double(1.4464));
            t1.add(new Day(28, SerialDate.AUGUST, 2001), new Double(1.4483));
            t1.add(new Day(29, SerialDate.AUGUST, 2001), new Double(1.4519));
            t1.add(new Day(30, SerialDate.AUGUST, 2001), new Double(1.4494));
            t1.add(new Day(31, SerialDate.AUGUST, 2001), new Double(1.4505));
            t1.add(new Day(3, SerialDate.SEPTEMBER, 2001), new Double(1.4519));
            t1.add(new Day(4, SerialDate.SEPTEMBER, 2001), new Double(1.4460));
            t1.add(new Day(5, SerialDate.SEPTEMBER, 2001), new Double(1.4526));
            t1.add(new Day(6, SerialDate.SEPTEMBER, 2001), new Double(1.4527));
            t1.add(new Day(7, SerialDate.SEPTEMBER, 2001), new Double(1.4617));
            t1.add(new Day(10, SerialDate.SEPTEMBER, 2001), new Double(1.4583));
            t1.add(new Day(11, SerialDate.SEPTEMBER, 2001), new Double(1.4693));
            t1.add(new Day(12, SerialDate.SEPTEMBER, 2001), new Double(1.4633));
            t1.add(new Day(13, SerialDate.SEPTEMBER, 2001), new Double(1.4690));
            t1.add(new Day(14, SerialDate.SEPTEMBER, 2001), new Double(1.4691));
            t1.add(new Day(17, SerialDate.SEPTEMBER, 2001), new Double(1.4668));
            t1.add(new Day(18, SerialDate.SEPTEMBER, 2001), new Double(1.4624));
            t1.add(new Day(19, SerialDate.SEPTEMBER, 2001), new Double(1.4678));
            t1.add(new Day(20, SerialDate.SEPTEMBER, 2001), new Double(1.4657));
            t1.add(new Day(21, SerialDate.SEPTEMBER, 2001), new Double(1.4575));
            t1.add(new Day(24, SerialDate.SEPTEMBER, 2001), new Double(1.4646));
            t1.add(new Day(25, SerialDate.SEPTEMBER, 2001), new Double(1.4699));
            t1.add(new Day(26, SerialDate.SEPTEMBER, 2001), new Double(1.4749));
            t1.add(new Day(27, SerialDate.SEPTEMBER, 2001), new Double(1.4756));
            t1.add(new Day(28, SerialDate.SEPTEMBER, 2001), new Double(1.4699));
            t1.add(new Day(1, SerialDate.OCTOBER, 2001), new Double(1.4784));
            t1.add(new Day(2, SerialDate.OCTOBER, 2001), new Double(1.4661));
            t1.add(new Day(3, SerialDate.OCTOBER, 2001), new Double(1.4767));
            t1.add(new Day(4, SerialDate.OCTOBER, 2001), new Double(1.4770));
            t1.add(new Day(5, SerialDate.OCTOBER, 2001), new Double(1.4810));
            t1.add(new Day(8, SerialDate.OCTOBER, 2001), new Double(1.4743));
            t1.add(new Day(9, SerialDate.OCTOBER, 2001), new Double(1.4667));
            t1.add(new Day(10, SerialDate.OCTOBER, 2001), new Double(1.4505));
            t1.add(new Day(11, SerialDate.OCTOBER, 2001), new Double(1.4434));
            t1.add(new Day(12, SerialDate.OCTOBER, 2001), new Double(1.4504));
            t1.add(new Day(15, SerialDate.OCTOBER, 2001), new Double(1.4471));
            t1.add(new Day(16, SerialDate.OCTOBER, 2001), new Double(1.4474));
            t1.add(new Day(17, SerialDate.OCTOBER, 2001), new Double(1.4512));
            t1.add(new Day(18, SerialDate.OCTOBER, 2001), new Double(1.4445));
            t1.add(new Day(19, SerialDate.OCTOBER, 2001), new Double(1.4384));
            t1.add(new Day(22, SerialDate.OCTOBER, 2001), new Double(1.4275));
            t1.add(new Day(23, SerialDate.OCTOBER, 2001), new Double(1.4212));
            t1.add(new Day(24, SerialDate.OCTOBER, 2001), new Double(1.4233));
            t1.add(new Day(25, SerialDate.OCTOBER, 2001), new Double(1.4297));
            t1.add(new Day(26, SerialDate.OCTOBER, 2001), new Double(1.4328));
            t1.add(new Day(29, SerialDate.OCTOBER, 2001), new Double(1.4515));
            t1.add(new Day(30, SerialDate.OCTOBER, 2001), new Double(1.4564));
            t1.add(new Day(31, SerialDate.OCTOBER, 2001), new Double(1.4541));
            t1.add(new Day(1, SerialDate.NOVEMBER, 2001), new Double(1.4624));
            t1.add(new Day(2, SerialDate.NOVEMBER, 2001), new Double(1.4632));
            t1.add(new Day(5, SerialDate.NOVEMBER, 2001), new Double(1.4570));
            t1.add(new Day(6, SerialDate.NOVEMBER, 2001), new Double(1.4588));
            t1.add(new Day(7, SerialDate.NOVEMBER, 2001), new Double(1.4646));
            t1.add(new Day(8, SerialDate.NOVEMBER, 2001), new Double(1.4552));
            t1.add(new Day(9, SerialDate.NOVEMBER, 2001), new Double(1.4579));
            t1.add(new Day(12, SerialDate.NOVEMBER, 2001), new Double(1.4575));
            t1.add(new Day(13, SerialDate.NOVEMBER, 2001), new Double(1.4429));
            t1.add(new Day(14, SerialDate.NOVEMBER, 2001), new Double(1.4425));
            t1.add(new Day(15, SerialDate.NOVEMBER, 2001), new Double(1.4318));
            t1.add(new Day(16, SerialDate.NOVEMBER, 2001), new Double(1.4291));
            t1.add(new Day(19, SerialDate.NOVEMBER, 2001), new Double(1.4140));
            t1.add(new Day(20, SerialDate.NOVEMBER, 2001), new Double(1.4173));
            t1.add(new Day(21, SerialDate.NOVEMBER, 2001), new Double(1.4132));
            t1.add(new Day(22, SerialDate.NOVEMBER, 2001), new Double(1.4131));
            t1.add(new Day(23, SerialDate.NOVEMBER, 2001), new Double(1.4083));
            t1.add(new Day(26, SerialDate.NOVEMBER, 2001), new Double(1.4122));
            t1.add(new Day(27, SerialDate.NOVEMBER, 2001), new Double(1.4136));
            t1.add(new Day(28, SerialDate.NOVEMBER, 2001), new Double(1.4239));
            t1.add(new Day(29, SerialDate.NOVEMBER, 2001), new Double(1.4225));
            t1.add(new Day(30, SerialDate.NOVEMBER, 2001), new Double(1.4260));
        }
        catch (Exception e) {
            System.err.println(e.getMessage());
        }
        return t1;
    }

    /**
     * Returns a time series of the daily EUR/GBP exchange rates in 2001 (to date), for use in
     * the JFreeChart demonstration application.
     * <P>
     * You wouldn't normally create a time series in this way.  Typically, values would
     * be read from a database.
     *
     */
    public static BasicTimeSeries createEURTimeSeries() {

        BasicTimeSeries t1 = new BasicTimeSeries("EUR/GBP");
        try {
            t1.add(new Day(2, SerialDate.JANUARY, 2001), new Double(1.5788));
            t1.add(new Day(3, SerialDate.JANUARY, 2001), new Double(1.5913));
            t1.add(new Day(4, SerialDate.JANUARY, 2001), new Double(1.5807));
            t1.add(new Day(5, SerialDate.JANUARY, 2001), new Double(1.5711));
            t1.add(new Day(8, SerialDate.JANUARY, 2001), new Double(1.5778));
            t1.add(new Day(9, SerialDate.JANUARY, 2001), new Double(1.5851));
            t1.add(new Day(10, SerialDate.JANUARY, 2001), new Double(1.5846));
            t1.add(new Day(11, SerialDate.JANUARY, 2001), new Double(1.5727));
            t1.add(new Day(12, SerialDate.JANUARY, 2001), new Double(1.5585));
            t1.add(new Day(15, SerialDate.JANUARY, 2001), new Double(1.5694));
            t1.add(new Day(16, SerialDate.JANUARY, 2001), new Double(1.5629));
            t1.add(new Day(17, SerialDate.JANUARY, 2001), new Double(1.5831));
            t1.add(new Day(18, SerialDate.JANUARY, 2001), new Double(1.5624));
            t1.add(new Day(19, SerialDate.JANUARY, 2001), new Double(1.5694));
            t1.add(new Day(22, SerialDate.JANUARY, 2001), new Double(1.5615));
            t1.add(new Day(23, SerialDate.JANUARY, 2001), new Double(1.5656));
            t1.add(new Day(24, SerialDate.JANUARY, 2001), new Double(1.5795));
            t1.add(new Day(25, SerialDate.JANUARY, 2001), new Double(1.5852));
            t1.add(new Day(26, SerialDate.JANUARY, 2001), new Double(1.5797));
            t1.add(new Day(29, SerialDate.JANUARY, 2001), new Double(1.5862));
            t1.add(new Day(30, SerialDate.JANUARY, 2001), new Double(1.5803));
            t1.add(new Day(31, SerialDate.JANUARY, 2001), new Double(1.5714));
            t1.add(new Day(1, SerialDate.FEBRUARY, 2001), new Double(1.5717));
            t1.add(new Day(2, SerialDate.FEBRUARY, 2001), new Double(1.5735));
            t1.add(new Day(5, SerialDate.FEBRUARY, 2001), new Double(1.5691));
            t1.add(new Day(6, SerialDate.FEBRUARY, 2001), new Double(1.5676));
            t1.add(new Day(7, SerialDate.FEBRUARY, 2001), new Double(1.5677));
            t1.add(new Day(8, SerialDate.FEBRUARY, 2001), new Double(1.5737));
            t1.add(new Day(9, SerialDate.FEBRUARY, 2001), new Double(1.5654));
            t1.add(new Day(12, SerialDate.FEBRUARY, 2001), new Double(1.5621));
            t1.add(new Day(13, SerialDate.FEBRUARY, 2001), new Double(1.5761));
            t1.add(new Day(14, SerialDate.FEBRUARY, 2001), new Double(1.5898));
            t1.add(new Day(15, SerialDate.FEBRUARY, 2001), new Double(1.6045));
            t1.add(new Day(16, SerialDate.FEBRUARY, 2001), new Double(1.5852));
            t1.add(new Day(19, SerialDate.FEBRUARY, 2001), new Double(1.5704));
            t1.add(new Day(20, SerialDate.FEBRUARY, 2001), new Double(1.5892));
            t1.add(new Day(21, SerialDate.FEBRUARY, 2001), new Double(1.5844));
            t1.add(new Day(22, SerialDate.FEBRUARY, 2001), new Double(1.5934));
            t1.add(new Day(23, SerialDate.FEBRUARY, 2001), new Double(1.5951));
            t1.add(new Day(26, SerialDate.FEBRUARY, 2001), new Double(1.5848));
            t1.add(new Day(27, SerialDate.FEBRUARY, 2001), new Double(1.5706));
            t1.add(new Day(28, SerialDate.FEBRUARY, 2001), new Double(1.5680));
            t1.add(new Day(1, SerialDate.MARCH, 2001), new Double(1.5645));
            t1.add(new Day(2, SerialDate.MARCH, 2001), new Double(1.5754));
            t1.add(new Day(5, SerialDate.MARCH, 2001), new Double(1.5808));
            t1.add(new Day(6, SerialDate.MARCH, 2001), new Double(1.5766));
            t1.add(new Day(7, SerialDate.MARCH, 2001), new Double(1.5756));
            t1.add(new Day(8, SerialDate.MARCH, 2001), new Double(1.5760));
            t1.add(new Day(9, SerialDate.MARCH, 2001), new Double(1.5748));
            t1.add(new Day(12, SerialDate.MARCH, 2001), new Double(1.5779));
            t1.add(new Day(13, SerialDate.MARCH, 2001), new Double(1.5837));
            t1.add(new Day(14, SerialDate.MARCH, 2001), new Double(1.5886));
            t1.add(new Day(15, SerialDate.MARCH, 2001), new Double(1.5931));
            t1.add(new Day(16, SerialDate.MARCH, 2001), new Double(1.5945));
            t1.add(new Day(19, SerialDate.MARCH, 2001), new Double(1.5880));
            t1.add(new Day(20, SerialDate.MARCH, 2001), new Double(1.5817));
            t1.add(new Day(21, SerialDate.MARCH, 2001), new Double(1.5927));
            t1.add(new Day(22, SerialDate.MARCH, 2001), new Double(1.6065));
            t1.add(new Day(23, SerialDate.MARCH, 2001), new Double(1.6006));
            t1.add(new Day(26, SerialDate.MARCH, 2001), new Double(1.6007));
            t1.add(new Day(27, SerialDate.MARCH, 2001), new Double(1.5989));
            t1.add(new Day(28, SerialDate.MARCH, 2001), new Double(1.6135));
            t1.add(new Day(29, SerialDate.MARCH, 2001), new Double(1.6282));
            t1.add(new Day(30, SerialDate.MARCH, 2001), new Double(1.6090));
            t1.add(new Day(2, SerialDate.APRIL, 2001), new Double(1.6107));
            t1.add(new Day(3, SerialDate.APRIL, 2001), new Double(1.6093));
            t1.add(new Day(4, SerialDate.APRIL, 2001), new Double(1.5880));
            t1.add(new Day(5, SerialDate.APRIL, 2001), new Double(1.5931));
            t1.add(new Day(6, SerialDate.APRIL, 2001), new Double(1.5968));
            t1.add(new Day(9, SerialDate.APRIL, 2001), new Double(1.6072));
            t1.add(new Day(10, SerialDate.APRIL, 2001), new Double(1.6167));
            t1.add(new Day(11, SerialDate.APRIL, 2001), new Double(1.6214));
            t1.add(new Day(12, SerialDate.APRIL, 2001), new Double(1.6120));
            t1.add(new Day(17, SerialDate.APRIL, 2001), new Double(1.6229));
            t1.add(new Day(18, SerialDate.APRIL, 2001), new Double(1.6298));
            t1.add(new Day(19, SerialDate.APRIL, 2001), new Double(1.6159));
            t1.add(new Day(20, SerialDate.APRIL, 2001), new Double(1.5996));
            t1.add(new Day(23, SerialDate.APRIL, 2001), new Double(1.6042));
            t1.add(new Day(24, SerialDate.APRIL, 2001), new Double(1.6061));
            t1.add(new Day(25, SerialDate.APRIL, 2001), new Double(1.6045));
            t1.add(new Day(26, SerialDate.APRIL, 2001), new Double(1.5970));
            t1.add(new Day(27, SerialDate.APRIL, 2001), new Double(1.6095));
            t1.add(new Day(30, SerialDate.APRIL, 2001), new Double(1.6141));
            t1.add(new Day(1, SerialDate.MAY, 2001), new Double(1.6076));
            t1.add(new Day(2, SerialDate.MAY, 2001), new Double(1.6077));
            t1.add(new Day(3, SerialDate.MAY, 2001), new Double(1.6035));
            t1.add(new Day(4, SerialDate.MAY, 2001), new Double(1.6060));
            t1.add(new Day(8, SerialDate.MAY, 2001), new Double(1.6178));
            t1.add(new Day(9, SerialDate.MAY, 2001), new Double(1.6083));
            t1.add(new Day(10, SerialDate.MAY, 2001), new Double(1.6107));
            t1.add(new Day(11, SerialDate.MAY, 2001), new Double(1.6209));
            t1.add(new Day(14, SerialDate.MAY, 2001), new Double(1.6228));
            t1.add(new Day(15, SerialDate.MAY, 2001), new Double(1.6184));
            t1.add(new Day(16, SerialDate.MAY, 2001), new Double(1.6167));
            t1.add(new Day(17, SerialDate.MAY, 2001), new Double(1.6223));
            t1.add(new Day(18, SerialDate.MAY, 2001), new Double(1.6305));
            t1.add(new Day(21, SerialDate.MAY, 2001), new Double(1.6420));
            t1.add(new Day(22, SerialDate.MAY, 2001), new Double(1.6484));
            t1.add(new Day(23, SerialDate.MAY, 2001), new Double(1.6547));
            t1.add(new Day(24, SerialDate.MAY, 2001), new Double(1.6444));
            t1.add(new Day(25, SerialDate.MAY, 2001), new Double(1.6577));
            t1.add(new Day(29, SerialDate.MAY, 2001), new Double(1.6606));
            t1.add(new Day(30, SerialDate.MAY, 2001), new Double(1.6604));
            t1.add(new Day(31, SerialDate.MAY, 2001), new Double(1.6772));
            t1.add(new Day(1, SerialDate.JUNE, 2001), new Double(1.6717));
            t1.add(new Day(4, SerialDate.JUNE, 2001), new Double(1.6685));
            t1.add(new Day(5, SerialDate.JUNE, 2001), new Double(1.6621));
            t1.add(new Day(6, SerialDate.JUNE, 2001), new Double(1.6460));
            t1.add(new Day(7, SerialDate.JUNE, 2001), new Double(1.6333));
            t1.add(new Day(8, SerialDate.JUNE, 2001), new Double(1.6265));
            t1.add(new Day(11, SerialDate.JUNE, 2001), new Double(1.6311));
            t1.add(new Day(12, SerialDate.JUNE, 2001), new Double(1.6238));
            t1.add(new Day(13, SerialDate.JUNE, 2001), new Double(1.6300));
            t1.add(new Day(14, SerialDate.JUNE, 2001), new Double(1.6289));
            t1.add(new Day(15, SerialDate.JUNE, 2001), new Double(1.6276));
            t1.add(new Day(18, SerialDate.JUNE, 2001), new Double(1.6299));
            t1.add(new Day(19, SerialDate.JUNE, 2001), new Double(1.6353));
            t1.add(new Day(20, SerialDate.JUNE, 2001), new Double(1.6378));
            t1.add(new Day(21, SerialDate.JUNE, 2001), new Double(1.6567));
            t1.add(new Day(22, SerialDate.JUNE, 2001), new Double(1.6523));
            t1.add(new Day(25, SerialDate.JUNE, 2001), new Double(1.6418));
            t1.add(new Day(26, SerialDate.JUNE, 2001), new Double(1.6429));
            t1.add(new Day(27, SerialDate.JUNE, 2001), new Double(1.6439));
            t1.add(new Day(28, SerialDate.JUNE, 2001), new Double(1.6605));
            t1.add(new Day(29, SerialDate.JUNE, 2001), new Double(1.6599));
            t1.add(new Day(2, SerialDate.JULY, 2001), new Double(1.6727));
            t1.add(new Day(3, SerialDate.JULY, 2001), new Double(1.6620));
            t1.add(new Day(4, SerialDate.JULY, 2001), new Double(1.6628));
            t1.add(new Day(5, SerialDate.JULY, 2001), new Double(1.6730));
            t1.add(new Day(6, SerialDate.JULY, 2001), new Double(1.6649));
            t1.add(new Day(9, SerialDate.JULY, 2001), new Double(1.6603));
            t1.add(new Day(10, SerialDate.JULY, 2001), new Double(1.6489));
            t1.add(new Day(11, SerialDate.JULY, 2001), new Double(1.6421));
            t1.add(new Day(12, SerialDate.JULY, 2001), new Double(1.6498));
            t1.add(new Day(13, SerialDate.JULY, 2001), new Double(1.6447));
            t1.add(new Day(16, SerialDate.JULY, 2001), new Double(1.6373));
            t1.add(new Day(17, SerialDate.JULY, 2001), new Double(1.6443));
            t1.add(new Day(18, SerialDate.JULY, 2001), new Double(1.6246));
            t1.add(new Day(19, SerialDate.JULY, 2001), new Double(1.6295));
            t1.add(new Day(20, SerialDate.JULY, 2001), new Double(1.6362));
            t1.add(new Day(23, SerialDate.JULY, 2001), new Double(1.6348));
            t1.add(new Day(24, SerialDate.JULY, 2001), new Double(1.6242));
            t1.add(new Day(25, SerialDate.JULY, 2001), new Double(1.6241));
            t1.add(new Day(26, SerialDate.JULY, 2001), new Double(1.6281));
            t1.add(new Day(27, SerialDate.JULY, 2001), new Double(1.6296));
            t1.add(new Day(30, SerialDate.JULY, 2001), new Double(1.6279));
            t1.add(new Day(31, SerialDate.JULY, 2001), new Double(1.6300));
            t1.add(new Day(1, SerialDate.AUGUST, 2001), new Double(1.6290));
            t1.add(new Day(2, SerialDate.AUGUST, 2001), new Double(1.6237));
            t1.add(new Day(3, SerialDate.AUGUST, 2001), new Double(1.6138));
            t1.add(new Day(6, SerialDate.AUGUST, 2001), new Double(1.6121));
            t1.add(new Day(7, SerialDate.AUGUST, 2001), new Double(1.6170));
            t1.add(new Day(8, SerialDate.AUGUST, 2001), new Double(1.6135));
            t1.add(new Day(9, SerialDate.AUGUST, 2001), new Double(1.5996));
            t1.add(new Day(10, SerialDate.AUGUST, 2001), new Double(1.5931));
            t1.add(new Day(13, SerialDate.AUGUST, 2001), new Double(1.5828));
            t1.add(new Day(14, SerialDate.AUGUST, 2001), new Double(1.5824));
            t1.add(new Day(15, SerialDate.AUGUST, 2001), new Double(1.5783));
            t1.add(new Day(16, SerialDate.AUGUST, 2001), new Double(1.5810));
            t1.add(new Day(17, SerialDate.AUGUST, 2001), new Double(1.5761));
            t1.add(new Day(20, SerialDate.AUGUST, 2001), new Double(1.5831));
            t1.add(new Day(21, SerialDate.AUGUST, 2001), new Double(1.5870));
            t1.add(new Day(22, SerialDate.AUGUST, 2001), new Double(1.5808));
            t1.add(new Day(23, SerialDate.AUGUST, 2001), new Double(1.5845));
            t1.add(new Day(24, SerialDate.AUGUST, 2001), new Double(1.5844));
            t1.add(new Day(28, SerialDate.AUGUST, 2001), new Double(1.5924));
            t1.add(new Day(29, SerialDate.AUGUST, 2001), new Double(1.5950));
            t1.add(new Day(30, SerialDate.AUGUST, 2001), new Double(1.5941));
            t1.add(new Day(31, SerialDate.AUGUST, 2001), new Double(1.5968));
            t1.add(new Day(3, SerialDate.SEPTEMBER, 2001), new Double(1.6020));
            t1.add(new Day(4, SerialDate.SEPTEMBER, 2001), new Double(1.6236));
            t1.add(new Day(5, SerialDate.SEPTEMBER, 2001), new Double(1.6352));
            t1.add(new Day(6, SerialDate.SEPTEMBER, 2001), new Double(1.6302));
            t1.add(new Day(7, SerialDate.SEPTEMBER, 2001), new Double(1.6180));
            t1.add(new Day(10, SerialDate.SEPTEMBER, 2001), new Double(1.6218));
            t1.add(new Day(11, SerialDate.SEPTEMBER, 2001), new Double(1.6182));
            t1.add(new Day(12, SerialDate.SEPTEMBER, 2001), new Double(1.6157));
            t1.add(new Day(13, SerialDate.SEPTEMBER, 2001), new Double(1.6171));
            t1.add(new Day(14, SerialDate.SEPTEMBER, 2001), new Double(1.5960));
            t1.add(new Day(17, SerialDate.SEPTEMBER, 2001), new Double(1.5952));
            t1.add(new Day(18, SerialDate.SEPTEMBER, 2001), new Double(1.5863));
            t1.add(new Day(19, SerialDate.SEPTEMBER, 2001), new Double(1.5790));
            t1.add(new Day(20, SerialDate.SEPTEMBER, 2001), new Double(1.5811));
            t1.add(new Day(21, SerialDate.SEPTEMBER, 2001), new Double(1.5917));
            t1.add(new Day(24, SerialDate.SEPTEMBER, 2001), new Double(1.6005));
            t1.add(new Day(25, SerialDate.SEPTEMBER, 2001), new Double(1.5915));
            t1.add(new Day(26, SerialDate.SEPTEMBER, 2001), new Double(1.6012));
            t1.add(new Day(27, SerialDate.SEPTEMBER, 2001), new Double(1.6032));
            t1.add(new Day(28, SerialDate.SEPTEMBER, 2001), new Double(1.6133));
            t1.add(new Day(1, SerialDate.OCTOBER, 2001), new Double(1.6147));
            t1.add(new Day(2, SerialDate.OCTOBER, 2001), new Double(1.6002));
            t1.add(new Day(3, SerialDate.OCTOBER, 2001), new Double(1.6041));
            t1.add(new Day(4, SerialDate.OCTOBER, 2001), new Double(1.6172));
            t1.add(new Day(5, SerialDate.OCTOBER, 2001), new Double(1.6121));
            t1.add(new Day(8, SerialDate.OCTOBER, 2001), new Double(1.6044));
            t1.add(new Day(9, SerialDate.OCTOBER, 2001), new Double(1.5974));
            t1.add(new Day(10, SerialDate.OCTOBER, 2001), new Double(1.5915));
            t1.add(new Day(11, SerialDate.OCTOBER, 2001), new Double(1.6022));
            t1.add(new Day(12, SerialDate.OCTOBER, 2001), new Double(1.6014));
            t1.add(new Day(15, SerialDate.OCTOBER, 2001), new Double(1.5942));
            t1.add(new Day(16, SerialDate.OCTOBER, 2001), new Double(1.5925));
            t1.add(new Day(17, SerialDate.OCTOBER, 2001), new Double(1.6007));
            t1.add(new Day(18, SerialDate.OCTOBER, 2001), new Double(1.6000));
            t1.add(new Day(19, SerialDate.OCTOBER, 2001), new Double(1.6030));
            t1.add(new Day(22, SerialDate.OCTOBER, 2001), new Double(1.6014));
            t1.add(new Day(23, SerialDate.OCTOBER, 2001), new Double(1.5995));
            t1.add(new Day(24, SerialDate.OCTOBER, 2001), new Double(1.5951));
            t1.add(new Day(25, SerialDate.OCTOBER, 2001), new Double(1.5953));
            t1.add(new Day(26, SerialDate.OCTOBER, 2001), new Double(1.6057));
            t1.add(new Day(29, SerialDate.OCTOBER, 2001), new Double(1.6051));
            t1.add(new Day(30, SerialDate.OCTOBER, 2001), new Double(1.6027));
            t1.add(new Day(31, SerialDate.OCTOBER, 2001), new Double(1.6144));
            t1.add(new Day(1, SerialDate.NOVEMBER, 2001), new Double(1.6139));
            t1.add(new Day(2, SerialDate.NOVEMBER, 2001), new Double(1.6189));
            t1.add(new Day(5, SerialDate.NOVEMBER, 2001), new Double(1.6248));
            t1.add(new Day(6, SerialDate.NOVEMBER, 2001), new Double(1.6267));
            t1.add(new Day(7, SerialDate.NOVEMBER, 2001), new Double(1.6281));
            t1.add(new Day(8, SerialDate.NOVEMBER, 2001), new Double(1.6310));
            t1.add(new Day(9, SerialDate.NOVEMBER, 2001), new Double(1.6313));
            t1.add(new Day(12, SerialDate.NOVEMBER, 2001), new Double(1.6272));
            t1.add(new Day(13, SerialDate.NOVEMBER, 2001), new Double(1.6361));
            t1.add(new Day(14, SerialDate.NOVEMBER, 2001), new Double(1.6323));
            t1.add(new Day(15, SerialDate.NOVEMBER, 2001), new Double(1.6252));
            t1.add(new Day(16, SerialDate.NOVEMBER, 2001), new Double(1.6141));
            t1.add(new Day(19, SerialDate.NOVEMBER, 2001), new Double(1.6086));
            t1.add(new Day(20, SerialDate.NOVEMBER, 2001), new Double(1.6055));
            t1.add(new Day(21, SerialDate.NOVEMBER, 2001), new Double(1.6132));
            t1.add(new Day(22, SerialDate.NOVEMBER, 2001), new Double(1.6074));
            t1.add(new Day(23, SerialDate.NOVEMBER, 2001), new Double(1.6065));
            t1.add(new Day(26, SerialDate.NOVEMBER, 2001), new Double(1.6061));
            t1.add(new Day(27, SerialDate.NOVEMBER, 2001), new Double(1.6039));
            t1.add(new Day(28, SerialDate.NOVEMBER, 2001), new Double(1.6069));
            t1.add(new Day(29, SerialDate.NOVEMBER, 2001), new Double(1.6044));
            t1.add(new Day(30, SerialDate.NOVEMBER, 2001), new Double(1.5928));
        }
        catch (Exception e) {
            System.err.println(e.getMessage());
        }
        return t1;
    }

    /**
     * Returns a time series of the daily EUR/GBP exchange rates in 2001 (to date), for use in
     * the JFreeChart demonstration application.
     * <P>
     * You wouldn't normally create a time series in this way.  Typically, values would
     * be read from a database.
     *
     */
    public static BasicTimeSeries createJPYTimeSeries() {

        BasicTimeSeries t1 = new BasicTimeSeries("JPY/GBP Exchange Rate");
        try {
            t1.add(new Day(2, SerialDate.JANUARY, 2001), new Double(171.2612));
            t1.add(new Day(3, SerialDate.JANUARY, 2001), new Double(172.1076));
            t1.add(new Day(4, SerialDate.JANUARY, 2001), new Double(172.3485));
            t1.add(new Day(5, SerialDate.JANUARY, 2001), new Double(173.7023));
            t1.add(new Day(8, SerialDate.JANUARY, 2001), new Double(174.1253));
            t1.add(new Day(9, SerialDate.JANUARY, 2001), new Double(173.6386));
            t1.add(new Day(10, SerialDate.JANUARY, 2001), new Double(173.2623));
            t1.add(new Day(11, SerialDate.JANUARY, 2001), new Double(175.7319));
            t1.add(new Day(12, SerialDate.JANUARY, 2001), new Double(174.2442));
            t1.add(new Day(15, SerialDate.JANUARY, 2001), new Double(175.7583));
            t1.add(new Day(16, SerialDate.JANUARY, 2001), new Double(173.0719));
            t1.add(new Day(17, SerialDate.JANUARY, 2001), new Double(173.0805));
            t1.add(new Day(18, SerialDate.JANUARY, 2001), new Double(174.1975));
            t1.add(new Day(19, SerialDate.JANUARY, 2001), new Double(172.3138));
            t1.add(new Day(22, SerialDate.JANUARY, 2001), new Double(170.5016));
            t1.add(new Day(23, SerialDate.JANUARY, 2001), new Double(172.1836));
            t1.add(new Day(24, SerialDate.JANUARY, 2001), new Double(172.2154));
            t1.add(new Day(25, SerialDate.JANUARY, 2001), new Double(170.1515));
            t1.add(new Day(26, SerialDate.JANUARY, 2001), new Double(170.3728));
            t1.add(new Day(29, SerialDate.JANUARY, 2001), new Double(170.2911));
            t1.add(new Day(30, SerialDate.JANUARY, 2001), new Double(170.3995));
            t1.add(new Day(31, SerialDate.JANUARY, 2001), new Double(169.9110));
            t1.add(new Day(1, SerialDate.FEBRUARY, 2001), new Double(170.4084));
            t1.add(new Day(2, SerialDate.FEBRUARY, 2001), new Double(169.8845));
            t1.add(new Day(5, SerialDate.FEBRUARY, 2001), new Double(169.5120));
            t1.add(new Day(6, SerialDate.FEBRUARY, 2001), new Double(167.9429));
            t1.add(new Day(7, SerialDate.FEBRUARY, 2001), new Double(169.6096));
            t1.add(new Day(8, SerialDate.FEBRUARY, 2001), new Double(167.8282));
            t1.add(new Day(9, SerialDate.FEBRUARY, 2001), new Double(170.1427));
            t1.add(new Day(12, SerialDate.FEBRUARY, 2001), new Double(170.8250));
            t1.add(new Day(13, SerialDate.FEBRUARY, 2001), new Double(170.4005));
            t1.add(new Day(14, SerialDate.FEBRUARY, 2001), new Double(170.1455));
            t1.add(new Day(15, SerialDate.FEBRUARY, 2001), new Double(167.6925));
            t1.add(new Day(16, SerialDate.FEBRUARY, 2001), new Double(167.6133));
            t1.add(new Day(19, SerialDate.FEBRUARY, 2001), new Double(167.7099));
            t1.add(new Day(20, SerialDate.FEBRUARY, 2001), new Double(166.9004));
            t1.add(new Day(21, SerialDate.FEBRUARY, 2001), new Double(168.4231));
            t1.add(new Day(22, SerialDate.FEBRUARY, 2001), new Double(168.3292));
            t1.add(new Day(23, SerialDate.FEBRUARY, 2001), new Double(168.6142));
            t1.add(new Day(26, SerialDate.FEBRUARY, 2001), new Double(168.2608));
            t1.add(new Day(27, SerialDate.FEBRUARY, 2001), new Double(167.6325));
            t1.add(new Day(28, SerialDate.FEBRUARY, 2001), new Double(169.1728));
            t1.add(new Day(1, SerialDate.MARCH, 2001), new Double(170.5199));
            t1.add(new Day(2, SerialDate.MARCH, 2001), new Double(175.5211));
            t1.add(new Day(5, SerialDate.MARCH, 2001), new Double(174.9543));
            t1.add(new Day(6, SerialDate.MARCH, 2001), new Double(174.4053));
            t1.add(new Day(7, SerialDate.MARCH, 2001), new Double(175.1675));
            t1.add(new Day(8, SerialDate.MARCH, 2001), new Double(175.7501));
            t1.add(new Day(9, SerialDate.MARCH, 2001), new Double(175.5956));
            t1.add(new Day(12, SerialDate.MARCH, 2001), new Double(176.6677));
            t1.add(new Day(13, SerialDate.MARCH, 2001), new Double(174.4282));
            t1.add(new Day(14, SerialDate.MARCH, 2001), new Double(175.1140));
            t1.add(new Day(15, SerialDate.MARCH, 2001), new Double(175.8914));
            t1.add(new Day(16, SerialDate.MARCH, 2001), new Double(175.7124));
            t1.add(new Day(19, SerialDate.MARCH, 2001), new Double(174.2307));
            t1.add(new Day(20, SerialDate.MARCH, 2001), new Double(175.0382));
            t1.add(new Day(21, SerialDate.MARCH, 2001), new Double(176.1183));
            t1.add(new Day(22, SerialDate.MARCH, 2001), new Double(176.2646));
            t1.add(new Day(23, SerialDate.MARCH, 2001), new Double(175.3608));
            t1.add(new Day(26, SerialDate.MARCH, 2001), new Double(176.5805));
            t1.add(new Day(27, SerialDate.MARCH, 2001), new Double(176.8495));
            t1.add(new Day(28, SerialDate.MARCH, 2001), new Double(174.7895));
            t1.add(new Day(29, SerialDate.MARCH, 2001), new Double(176.6957));
            t1.add(new Day(30, SerialDate.MARCH, 2001), new Double(178.1106));
            t1.add(new Day(2, SerialDate.APRIL, 2001), new Double(179.5654));
            t1.add(new Day(3, SerialDate.APRIL, 2001), new Double(179.7021));
            t1.add(new Day(4, SerialDate.APRIL, 2001), new Double(179.5065));
            t1.add(new Day(5, SerialDate.APRIL, 2001), new Double(177.9874));
            t1.add(new Day(6, SerialDate.APRIL, 2001), new Double(178.3541));
            t1.add(new Day(9, SerialDate.APRIL, 2001), new Double(181.0301));
            t1.add(new Day(10, SerialDate.APRIL, 2001), new Double(179.0357));
            t1.add(new Day(11, SerialDate.APRIL, 2001), new Double(178.8478));
            t1.add(new Day(12, SerialDate.APRIL, 2001), new Double(177.7927));
            t1.add(new Day(17, SerialDate.APRIL, 2001), new Double(177.1644));
            t1.add(new Day(18, SerialDate.APRIL, 2001), new Double(174.1972));
            t1.add(new Day(19, SerialDate.APRIL, 2001), new Double(174.9370));
            t1.add(new Day(20, SerialDate.APRIL, 2001), new Double(176.8555));
            t1.add(new Day(23, SerialDate.APRIL, 2001), new Double(175.3433));
            t1.add(new Day(24, SerialDate.APRIL, 2001), new Double(175.4792));
            t1.add(new Day(25, SerialDate.APRIL, 2001), new Double(175.7154));
            t1.add(new Day(26, SerialDate.APRIL, 2001), new Double(176.1797));
            t1.add(new Day(27, SerialDate.APRIL, 2001), new Double(177.7074));
            t1.add(new Day(30, SerialDate.APRIL, 2001), new Double(176.8592));
            t1.add(new Day(1, SerialDate.MAY, 2001), new Double(174.9104));
            t1.add(new Day(2, SerialDate.MAY, 2001), new Double(174.8992));
            t1.add(new Day(3, SerialDate.MAY, 2001), new Double(173.4239));
            t1.add(new Day(4, SerialDate.MAY, 2001), new Double(173.9663));
            t1.add(new Day(8, SerialDate.MAY, 2001), new Double(174.4871));
            t1.add(new Day(9, SerialDate.MAY, 2001), new Double(173.6851));
            t1.add(new Day(10, SerialDate.MAY, 2001), new Double(174.5957));
            t1.add(new Day(11, SerialDate.MAY, 2001), new Double(173.6254));
            t1.add(new Day(14, SerialDate.MAY, 2001), new Double(174.7913));
            t1.add(new Day(15, SerialDate.MAY, 2001), new Double(175.3932));
            t1.add(new Day(16, SerialDate.MAY, 2001), new Double(176.7291));
            t1.add(new Day(17, SerialDate.MAY, 2001), new Double(175.8551));
            t1.add(new Day(18, SerialDate.MAY, 2001), new Double(176.8558));
            t1.add(new Day(21, SerialDate.MAY, 2001), new Double(176.6443));
            t1.add(new Day(22, SerialDate.MAY, 2001), new Double(175.1953));
            t1.add(new Day(23, SerialDate.MAY, 2001), new Double(171.6117));
            t1.add(new Day(24, SerialDate.MAY, 2001), new Double(169.0407));
            t1.add(new Day(25, SerialDate.MAY, 2001), new Double(171.3975));
            t1.add(new Day(29, SerialDate.MAY, 2001), new Double(170.2811));
            t1.add(new Day(30, SerialDate.MAY, 2001), new Double(171.2154));
            t1.add(new Day(31, SerialDate.MAY, 2001), new Double(168.6795));
            t1.add(new Day(1, SerialDate.JUNE, 2001), new Double(168.2339));
            t1.add(new Day(4, SerialDate.JUNE, 2001), new Double(169.2090));
            t1.add(new Day(5, SerialDate.JUNE, 2001), new Double(169.4501));
            t1.add(new Day(6, SerialDate.JUNE, 2001), new Double(167.8414));
            t1.add(new Day(7, SerialDate.JUNE, 2001), new Double(166.6042));
            t1.add(new Day(8, SerialDate.JUNE, 2001), new Double(166.5005));
            t1.add(new Day(11, SerialDate.JUNE, 2001), new Double(167.2925));
            t1.add(new Day(12, SerialDate.JUNE, 2001), new Double(168.1171));
            t1.add(new Day(13, SerialDate.JUNE, 2001), new Double(168.9091));
            t1.add(new Day(14, SerialDate.JUNE, 2001), new Double(169.8863));
            t1.add(new Day(15, SerialDate.JUNE, 2001), new Double(171.5254));
            t1.add(new Day(18, SerialDate.JUNE, 2001), new Double(172.6955));
            t1.add(new Day(19, SerialDate.JUNE, 2001), new Double(172.3427));
            t1.add(new Day(20, SerialDate.JUNE, 2001), new Double(172.0421));
            t1.add(new Day(21, SerialDate.JUNE, 2001), new Double(175.9830));
            t1.add(new Day(22, SerialDate.JUNE, 2001), new Double(175.5076));
            t1.add(new Day(25, SerialDate.JUNE, 2001), new Double(175.1080));
            t1.add(new Day(26, SerialDate.JUNE, 2001), new Double(175.5043));
            t1.add(new Day(27, SerialDate.JUNE, 2001), new Double(175.3712));
            t1.add(new Day(28, SerialDate.JUNE, 2001), new Double(175.9575));
            t1.add(new Day(29, SerialDate.JUNE, 2001), new Double(175.4734));
            t1.add(new Day(2, SerialDate.JULY, 2001), new Double(175.9908));
            t1.add(new Day(3, SerialDate.JULY, 2001), new Double(175.2386));
            t1.add(new Day(4, SerialDate.JULY, 2001), new Double(175.0405));
            t1.add(new Day(5, SerialDate.JULY, 2001), new Double(175.9451));
            t1.add(new Day(6, SerialDate.JULY, 2001), new Double(177.3383));
            t1.add(new Day(9, SerialDate.JULY, 2001), new Double(176.6965));
            t1.add(new Day(10, SerialDate.JULY, 2001), new Double(177.0476));
            t1.add(new Day(11, SerialDate.JULY, 2001), new Double(175.6136));
            t1.add(new Day(12, SerialDate.JULY, 2001), new Double(174.1736));
            t1.add(new Day(13, SerialDate.JULY, 2001), new Double(174.8619));
            t1.add(new Day(16, SerialDate.JULY, 2001), new Double(175.4915));
            t1.add(new Day(17, SerialDate.JULY, 2001), new Double(175.1916));
            t1.add(new Day(18, SerialDate.JULY, 2001), new Double(176.0599));
            t1.add(new Day(19, SerialDate.JULY, 2001), new Double(174.8244));
            t1.add(new Day(20, SerialDate.JULY, 2001), new Double(175.8257));
            t1.add(new Day(23, SerialDate.JULY, 2001), new Double(176.2682));
            t1.add(new Day(24, SerialDate.JULY, 2001), new Double(176.1794));
            t1.add(new Day(25, SerialDate.JULY, 2001), new Double(176.4514));
            t1.add(new Day(26, SerialDate.JULY, 2001), new Double(176.7673));
            t1.add(new Day(27, SerialDate.JULY, 2001), new Double(176.1476));
            t1.add(new Day(30, SerialDate.JULY, 2001), new Double(178.3029));
            t1.add(new Day(31, SerialDate.JULY, 2001), new Double(178.0895));
            t1.add(new Day(1, SerialDate.AUGUST, 2001), new Double(178.6438));
            t1.add(new Day(2, SerialDate.AUGUST, 2001), new Double(177.1364));
            t1.add(new Day(3, SerialDate.AUGUST, 2001), new Double(176.4042));
            t1.add(new Day(6, SerialDate.AUGUST, 2001), new Double(175.7999));
            t1.add(new Day(7, SerialDate.AUGUST, 2001), new Double(175.5131));
            t1.add(new Day(8, SerialDate.AUGUST, 2001), new Double(173.9804));
            t1.add(new Day(9, SerialDate.AUGUST, 2001), new Double(174.9459));
            t1.add(new Day(10, SerialDate.AUGUST, 2001), new Double(173.8883));
            t1.add(new Day(13, SerialDate.AUGUST, 2001), new Double(173.8253));
            t1.add(new Day(14, SerialDate.AUGUST, 2001), new Double(173.0352));
            t1.add(new Day(15, SerialDate.AUGUST, 2001), new Double(172.4666));
            t1.add(new Day(16, SerialDate.AUGUST, 2001), new Double(173.4173));
            t1.add(new Day(17, SerialDate.AUGUST, 2001), new Double(173.6289));
            t1.add(new Day(20, SerialDate.AUGUST, 2001), new Double(174.3824));
            t1.add(new Day(21, SerialDate.AUGUST, 2001), new Double(173.5063));
            t1.add(new Day(22, SerialDate.AUGUST, 2001), new Double(174.3372));
            t1.add(new Day(23, SerialDate.AUGUST, 2001), new Double(173.8620));
            t1.add(new Day(24, SerialDate.AUGUST, 2001), new Double(173.5825));
            t1.add(new Day(28, SerialDate.AUGUST, 2001), new Double(174.7664));
            t1.add(new Day(29, SerialDate.AUGUST, 2001), new Double(173.5166));
            t1.add(new Day(30, SerialDate.AUGUST, 2001), new Double(173.8555));
            t1.add(new Day(31, SerialDate.AUGUST, 2001), new Double(172.6675));
            t1.add(new Day(3, SerialDate.SEPTEMBER, 2001), new Double(172.3986));
            t1.add(new Day(4, SerialDate.SEPTEMBER, 2001), new Double(171.8860));
            t1.add(new Day(5, SerialDate.SEPTEMBER, 2001), new Double(174.8640));
            t1.add(new Day(6, SerialDate.SEPTEMBER, 2001), new Double(176.1399));
            t1.add(new Day(7, SerialDate.SEPTEMBER, 2001), new Double(175.7110));
            t1.add(new Day(10, SerialDate.SEPTEMBER, 2001), new Double(176.3085));
            t1.add(new Day(11, SerialDate.SEPTEMBER, 2001), new Double(174.6263));
            t1.add(new Day(12, SerialDate.SEPTEMBER, 2001), new Double(174.8058));
            t1.add(new Day(13, SerialDate.SEPTEMBER, 2001), new Double(174.8257));
            t1.add(new Day(14, SerialDate.SEPTEMBER, 2001), new Double(172.3107));
            t1.add(new Day(17, SerialDate.SEPTEMBER, 2001), new Double(172.5397));
            t1.add(new Day(18, SerialDate.SEPTEMBER, 2001), new Double(171.7004));
            t1.add(new Day(19, SerialDate.SEPTEMBER, 2001), new Double(172.1289));
            t1.add(new Day(20, SerialDate.SEPTEMBER, 2001), new Double(170.3143));
            t1.add(new Day(21, SerialDate.SEPTEMBER, 2001), new Double(169.9737));
            t1.add(new Day(24, SerialDate.SEPTEMBER, 2001), new Double(172.0319));
            t1.add(new Day(25, SerialDate.SEPTEMBER, 2001), new Double(172.5516));
            t1.add(new Day(26, SerialDate.SEPTEMBER, 2001), new Double(173.8612));
            t1.add(new Day(27, SerialDate.SEPTEMBER, 2001), new Double(176.5408));
            t1.add(new Day(28, SerialDate.SEPTEMBER, 2001), new Double(175.1092));
            t1.add(new Day(1, SerialDate.OCTOBER, 2001), new Double(177.6150));
            t1.add(new Day(2, SerialDate.OCTOBER, 2001), new Double(177.1049));
            t1.add(new Day(3, SerialDate.OCTOBER, 2001), new Double(178.2525));
            t1.add(new Day(4, SerialDate.OCTOBER, 2001), new Double(178.0819));
            t1.add(new Day(5, SerialDate.OCTOBER, 2001), new Double(178.1643));
            t1.add(new Day(8, SerialDate.OCTOBER, 2001), new Double(176.6654));
            t1.add(new Day(9, SerialDate.OCTOBER, 2001), new Double(176.0773));
            t1.add(new Day(10, SerialDate.OCTOBER, 2001), new Double(174.4806));
            t1.add(new Day(11, SerialDate.OCTOBER, 2001), new Double(175.1855));
            t1.add(new Day(12, SerialDate.OCTOBER, 2001), new Double(176.1221));
            t1.add(new Day(15, SerialDate.OCTOBER, 2001), new Double(175.1425));
            t1.add(new Day(16, SerialDate.OCTOBER, 2001), new Double(175.4683));
            t1.add(new Day(17, SerialDate.OCTOBER, 2001), new Double(175.4936));
            t1.add(new Day(18, SerialDate.OCTOBER, 2001), new Double(174.8134));
            t1.add(new Day(19, SerialDate.OCTOBER, 2001), new Double(174.4492));
            t1.add(new Day(22, SerialDate.OCTOBER, 2001), new Double(174.1978));
            t1.add(new Day(23, SerialDate.OCTOBER, 2001), new Double(174.8360));
            t1.add(new Day(24, SerialDate.OCTOBER, 2001), new Double(174.9378));
            t1.add(new Day(25, SerialDate.OCTOBER, 2001), new Double(175.4385));
            t1.add(new Day(26, SerialDate.OCTOBER, 2001), new Double(176.4207));
            t1.add(new Day(29, SerialDate.OCTOBER, 2001), new Double(177.0540));
            t1.add(new Day(30, SerialDate.OCTOBER, 2001), new Double(177.1128));
            t1.add(new Day(31, SerialDate.OCTOBER, 2001), new Double(177.9818));
            t1.add(new Day(1, SerialDate.NOVEMBER, 2001), new Double(177.9595));
            t1.add(new Day(2, SerialDate.NOVEMBER, 2001), new Double(177.9251));
            t1.add(new Day(5, SerialDate.NOVEMBER, 2001), new Double(177.2003));
            t1.add(new Day(6, SerialDate.NOVEMBER, 2001), new Double(176.6169));
            t1.add(new Day(7, SerialDate.NOVEMBER, 2001), new Double(177.3191));
            t1.add(new Day(8, SerialDate.NOVEMBER, 2001), new Double(175.7736));
            t1.add(new Day(9, SerialDate.NOVEMBER, 2001), new Double(175.2104));
            t1.add(new Day(12, SerialDate.NOVEMBER, 2001), new Double(175.0749));
            t1.add(new Day(13, SerialDate.NOVEMBER, 2001), new Double(175.2402));
            t1.add(new Day(14, SerialDate.NOVEMBER, 2001), new Double(175.3503));
            t1.add(new Day(15, SerialDate.NOVEMBER, 2001), new Double(175.2810));
            t1.add(new Day(16, SerialDate.NOVEMBER, 2001), new Double(175.4077));
            t1.add(new Day(19, SerialDate.NOVEMBER, 2001), new Double(174.3462));
            t1.add(new Day(20, SerialDate.NOVEMBER, 2001), new Double(173.8177));
            t1.add(new Day(21, SerialDate.NOVEMBER, 2001), new Double(174.0356));
            t1.add(new Day(22, SerialDate.NOVEMBER, 2001), new Double(175.0548));
            t1.add(new Day(23, SerialDate.NOVEMBER, 2001), new Double(175.2207));
            t1.add(new Day(26, SerialDate.NOVEMBER, 2001), new Double(175.4978));
            t1.add(new Day(27, SerialDate.NOVEMBER, 2001), new Double(175.2191));
            t1.add(new Day(28, SerialDate.NOVEMBER, 2001), new Double(175.4236));
            t1.add(new Day(29, SerialDate.NOVEMBER, 2001), new Double(176.2304));
            t1.add(new Day(30, SerialDate.NOVEMBER, 2001), new Double(175.6119));        }
        catch (Exception e) {
            System.err.println(e.getMessage());
        }
        return t1;
    }

}