package com.jrefinery.chart.demo;

import com.jrefinery.data.IntervalXYDataset;
import com.jrefinery.data.DatasetChangeListener;

/**
 * A quick and dirty implementation.
 */
public class SimpleIntervalXYDataset implements IntervalXYDataset {

    Double[] xStart = new Double[3];
    Double[] xEnd = new Double[3];

    Double[] y = new Double[3];

    public SimpleIntervalXYDataset() {

        xStart[0] = new Double(0.0);
        xStart[1] = new Double(2.0);
        xStart[2] = new Double(3.5);

        xEnd[0] = new Double(2.0);
        xEnd[1] = new Double(3.5);
        xEnd[2] = new Double(4.0);

        y[0] = new Double(3.0);
        y[1] = new Double(4.5);
        y[2] = new Double(2.5);
    }

    /**
     * Returns the number of series in the dataset.
     * @return The number of series in the dataset.
     */
    public int getSeriesCount() {
        return 1;
    }

    /**
     * Returns the name of a series.
     * @param series The series (zero-based index).
     */
    public String getSeriesName(int series) {
        return "Series 1";
    }

    /**
     * Returns the number of items in a series.
     * @param series The series (zero-based index).
     * @return The number of items within a series.
     */
    public int getItemCount(int series) {
        return 3;
    }

    /**
     * Returns the x-value for an item within a series.
     * <P>
     * The implementation is responsible for ensuring that the x-values are presented in ascending
     * order.
     * @param series The series (zero-based index).
     * @param item The item (zero-based index).
     * @return The x-value for an item within a series.
     */
    public Number getXValue(int series, int item) {
        return xStart[item];
    }

    /**
     * Returns the y-value for an item within a series.
     * @param series The series (zero-based index).
     * @param item The item (zero-based index).
     * @return The y-value for an item within a series.
     */
    public Number getYValue(int series, int item) {
        return y[item];
    }

    /**
     * Returns the starting X value for the specified series and item.
     * @param series The series (zero-based index);
     * @param item The item within a series (zero-based index).
     */
    public Number getStartXValue(int series, int item) {
        return xStart[item];
    }

    /**
     * Returns the ending X value for the specified series and item.
     * @param series The series (zero-based index);
     * @param item The item within a series (zero-based index).
     */
    public Number getEndXValue(int series, int item) {
        return xEnd[item];
    }

    /**
     * Returns the starting Y value for the specified series and item.
     * @param series The series (zero-based index);
     * @param item The item within a series (zero-based index).
     */
    public Number getStartYValue(int series, int item) {
        return y[item];
    }

    /**
     * Returns the ending Y value for the specified series and item.
     * @param series The series (zero-based index);
     * @param item The item within a series (zero-based index).
     */
    public Number getEndYValue(int series, int item) {
        return y[item];
    }

    /**
     * Registers an object for notification of changes to the dataset.
     * @param listener The object to register.
     */
    public void addChangeListener(DatasetChangeListener listener) {}

    /**
     * Deregisters an object for notification of changes to the dataset.
     * @param listener The object to deregister.
     */
    public void removeChangeListener(DatasetChangeListener listener) {}

}