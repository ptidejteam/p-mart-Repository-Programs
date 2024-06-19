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

import java.util.*;

public class Performance2 {

    double primitive = 42.0;

    Number object = new Double(42.0);

    public Performance2() {
    }

    /**
     * Just use double value - should be fast.
     */
    public double getPrimitive() {
        return primitive;
    }

    /**
     * Creates a Number object every time the primitive is accessed - should be really slow.
     */
    public Number getPrimitiveAsObject() {
        return new Double(primitive);
    }

    /**
     * Returns the object - caller has to use doubleValue() method.
     */
    public Number getObject() {
        return object;
    }

    /**
     * Returns a double value generated from the Object - should be similar to previous method,
     * but is not!
     */
    public double getObjectAsPrimitive() {
        return object.doubleValue();
    }


    public void getPrimitiveLoop(int count) {

        double d;
        for (int i=0; i<count; i++) {
            d = this.getPrimitive();
        }

    }

    public void getPrimitiveAsObjectLoop(int count) {

        double d;
        for (int i=0; i<count; i++) {
            d = this.getPrimitiveAsObject().doubleValue();
        }

    }

    public void getObjectAsPrimitiveLoop(int count) {

        double d;
        for (int i=0; i<count; i++) {
            d = this.getObjectAsPrimitive();
        }

    }

    public void getObjectLoop(int count) {

        double d;
        for (int i=0; i<count; i++) {
            d = this.getObject().doubleValue();
        }

    }

    public void status(String label, Date start, Date end) {
        long elapsed = end.getTime()-start.getTime();
        System.out.println(label+start.getTime()+"-->"+end.getTime()+" = "+elapsed);
    }

    public static void main(String[] args) {

        Performance2 performance = new Performance2();
        int count = 10000000;

        for (int repeat=0; repeat<3; repeat++) {  // repeat a few times just to make sure times are consistent
            Date s1 = new Date();
            performance.getPrimitiveLoop(count);
            Date e1 = new Date();
            performance.status("getPrimitive() : ", s1, e1);

            Date s2 = new Date();
            performance.getPrimitiveAsObjectLoop(count);
            Date e2 = new Date();
            performance.status("getPrimitiveAsObject() : ", s2, e2);

            Date s3 = new Date();
            performance.getObjectLoop(count);
            Date e3 = new Date();
            performance.status("getObject() : ", s3, e3);

            Date s4 = new Date();
            performance.getObjectAsPrimitiveLoop(count);
            Date e4 = new Date();
            performance.status("getObjectAsPrimitive() : ", s4, e4);

            System.out.println("-------------------");
        }
    }

}