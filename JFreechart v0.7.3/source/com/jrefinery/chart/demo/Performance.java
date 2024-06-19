/* ===============
 * JFreeChart Demo
 * ===============
 *
 * Project Info:  http://www.jrefinery.com/jfreechart;
 * Project Lead:  David Gilbert (david.gilbert@jrefinery.com);
 *
 * (C) Copyright 2000-2002, by Simba Management Limited and Contributors.
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
 */

package com.jrefinery.chart.demo;

import java.awt.geom.*;
import java.util.*;

public class Performance {

    protected double value = 2.0;

    protected Double number = new Double(value);

    public Performance() {
    }

    public void createLines(int count) {

        for (int i=0; i<count; i++) {
            Line2D line = new Line2D.Double(1.0, 1.0, 1.0, 1.0);
        }

    }

    public void setLines(int count) {

        Line2D line = new Line2D.Double(0.0, 0.0, 0.0, 0.0);
        for (int i=0; i<count; i++) {
            line.setLine(1.0, 1.0, 1.0, 1.0);
        }

    }

    public void getNumber(int count) {

        for (int i=0; i<count; i++) {
            double d = this.number.doubleValue();
        }

    }

    public void getValue(int count) {

        for (int i=0; i<count; i++) {
            double d = this.value;
        }

    }

    public void writeTime(String text, Date time) {

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(time);
        System.out.println(text+" : "+time.getTime());

    }

    public static void main(String[] args) {

        Performance p = new Performance();
        System.out.println("JFreeChart and Java2D Performance:");

        Date start1 = new Date();
        p.createLines(100000);
        Date end1 = new Date();

        Date start2 = new Date();
        p.setLines(100000);
        Date end2 = new Date();

        p.writeTime("Start create lines", start1);
        p.writeTime("Finish create lines", end1);
        p.writeTime("Start set lines", start2);
        p.writeTime("Finish set lines", end2);

        Date start3 = new Date();
        p.getNumber(1000000);
        Date end3 = new Date();

        Date start4 = new Date();
        p.getValue(1000000);
        Date end4 = new Date();

        p.writeTime("Start get number", start3);
        p.writeTime("Finish get number", end3);
        p.writeTime("Start get value", start4);
        p.writeTime("Finish get value", end4);


    }

}