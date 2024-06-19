package com.jrefinery.chart;

/**
 * Base class representing a tick unit.  This determines the spacing of the tick marks on an
 * axis.
 * <P>
 * This class (and subclasses) should be immutable.
 */
public abstract class TickUnit implements Comparable {

    protected Number value;

    public TickUnit(Number value) {
        this.value = value;
    }

    public Number getValue() {
        return this.value;
    }

    public abstract int compareTo(Object o);

}