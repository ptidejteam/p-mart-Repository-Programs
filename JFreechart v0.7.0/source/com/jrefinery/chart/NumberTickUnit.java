package com.jrefinery.chart;

import java.text.*;

/**
 * A numerical tick unit.
 */
public class NumberTickUnit extends TickUnit {

    protected NumberFormat formatter;

    public NumberTickUnit(Number value, NumberFormat formatter) {
        super(value);
        this.formatter = formatter;
    }

    public int compareTo(Object o) {

        NumberTickUnit other = (NumberTickUnit)o;
        if (this.value.doubleValue()>other.value.doubleValue()) {
            return 1;
        }
        else if (this.value.doubleValue()<other.value.doubleValue()) {
            return -1;
        }
        else return 0;

    }

}