/* ======================================
 * JFreeChart : a free Java chart library
 * ======================================
 *
 * Project Info:  http://www.jfree.org/jfreechart/index.html
 * Project Lead:  David Gilbert (david.gilbert@object-refinery.com);
 *
 * (C) Copyright 2000-2003, by Object Refinery Limited and Contributors.
 *
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation;
 * either version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this
 * library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330,
 * Boston, MA 02111-1307, USA.
 *
 * ---------------------
 * DatasetUtilities.java
 * ---------------------
 * (C) Copyright 2000-2003, by Object Refinery Limited and Contributors.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited);
 * Contributor(s):   Andrzej Porebski (bug fix);
 *                   Jonathan Nash (bug fix);
 *                   Richard Atkinson;
 *
 * $Id: DatasetUtilities.java,v 1.1 2007/10/10 19:15:28 vauchers Exp $
 *
 * Changes (from 18-Sep-2001)
 * --------------------------
 * 18-Sep-2001 : Added standard header and fixed DOS encoding problem (DG);
 * 22-Oct-2001 : Renamed DataSource.java --> Dataset.java etc. (DG);
 * 15-Nov-2001 : Moved to package com.jrefinery.data.* in the JCommon class library (DG);
 *               Changed to handle null values from datasets (DG);
 *               Bug fix (thanks to Andrzej Porebski) - initial value now set to positive or
 *               negative infinity when iterating (DG);
 * 22-Nov-2001 : Datasets with containing no data now return null for min and max calculations (DG);
 * 13-Dec-2001 : Extended to handle HighLowDataset and IntervalXYDataset (DG);
 * 15-Feb-2002 : Added getMinimumStackedRangeValue() and getMaximumStackedRangeValue() (DG);
 * 28-Feb-2002 : Renamed Datasets.java --> DatasetUtilities.java (DG);
 * 18-Mar-2002 : Fixed bug in min/max domain calculation for datasets that implement the
 *               CategoryDataset interface AND the XYDataset interface at the same time.  Thanks
 *               to Jonathan Nash for the fix (DG);
 * 23-Apr-2002 : Added getDomainExtent() and getRangeExtent() methods (DG);
 * 13-Jun-2002 : Modified range measurements to handle IntervalCategoryDataset (DG);
 * 12-Jul-2002 : Method name change in DomainInfo interface (DG);
 * 30-Jul-2002 : Added pie dataset summation method (DG);
 * 01-Oct-2002 : Added a method for constructing an XYDataset from a Function2D instance (DG);
 * 24-Oct-2002 : Amendments required following changes to the CategoryDataset interface (DG);
 * 18-Nov-2002 : Changed CategoryDataset to TableDataset (DG);
 * 04-Mar-2003 : Added isEmpty(XYDataset) method (DG);
 * 05-Mar-2003 : Added a method for creating a CategoryDataset from a KeyedValues instance (DG);
 * 15-May-2003 : Renamed isEmpty --> isEmptyOrNull (DG);
 * 25-Jun-2003 : Added limitPieDataset methods (RA);
 * 26-Jun-2003 : Modified getDomainExtent(...) method to accept null datasets (DG);
 * 27-Jul-2003 : Added getStackedRangeExtent(TableXYDataset data) (RA);
 * 18-Aug-2003 : getStackedRangeExtent(TableXYDataset data) now handles null values (RA);
 * 02-Sep-2003 : Added method to check for null or empty PieDataset (DG);
 * 18-Sep-2003 : Fix for bug 803660 (getMaximumRangeValue for CategoryDataset) (DG);
 *
 */

package org.jfree.data;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * A collection of useful static methods relating to datasets.
 *
 * @author David Gilbert
 */
public class DatasetUtilities {

    /**
     * To prevent instantiation.
     */
    protected DatasetUtilities() {
    }

    /**
     * Constructs an array of <code>Number</code> objects from an array of <code>double</code>
     * primitives.
     *
     * @param data  the data.
     *
     * @return an array of <code>Double</code>.
     */
    public static Number[] createNumberArray(double[] data) {

        Number[] result = new Number[data.length];

        for (int i = 0; i < data.length; i++) {
            result[i] = new Double(data[i]);
        }

        return result;

    }

    /**
     * Constructs an array of arrays of <code>Number</code> objects from a corresponding
     * structure containing <code>double</code> primitives.
     *
     * @param data  the data.
     *
     * @return an array of <code>Double</code>.
     */
    public static Number[][] createNumberArray2D(double[][] data) {

        int l1 = data.length;
        int l2 = data[0].length;

        Number[][] result = new Number[l1][l2];

        for (int i = 0; i < l1; i++) {
            result[i] = createNumberArray(data[i]);
        }

        return result;

    }

    /**
     * Returns the range of values in the domain for the dataset.
     * <P>
     * If the supplied dataset is <code>null</code>, the range returned is <code>null</code>.
     *
     * @param data  the dataset (<code>null</code> permitted).
     *
     * @return The range of values (possibly <code>null</code>).
     */
    public static Range getDomainExtent(Dataset data) {

        // check parameters...
        if (data == null) {
            return null;
        }

        if ((data instanceof CategoryDataset) && !(data instanceof XYDataset)) {
            throw new IllegalArgumentException("Datasets.getDomainExtent(...): "
                +  "CategoryDataset does not have a numerical domain.");
        }

        // work out the minimum value...
        if (data instanceof DomainInfo) {
            DomainInfo info = (DomainInfo) data;
            return info.getDomainRange();
        }

        // hasn't implemented DomainInfo, so iterate...
        else if (data instanceof XYDataset) {
            double minimum = Double.POSITIVE_INFINITY;
            double maximum = Double.NEGATIVE_INFINITY;
            XYDataset xyData = (XYDataset) data;
            int seriesCount = xyData.getSeriesCount();
            for (int series = 0; series < seriesCount; series++) {
                int itemCount = xyData.getItemCount(series);
                for (int item = 0; item < itemCount; item++) {

                    Number lvalue = null;
                    Number uvalue = null;
                    if (data instanceof IntervalXYDataset) {
                        IntervalXYDataset intervalXYData = (IntervalXYDataset) data;
                        lvalue = intervalXYData.getStartXValue(series, item);
                        uvalue = intervalXYData.getEndXValue(series, item);
                    }
                    else {
                        lvalue = xyData.getXValue(series, item);
                        uvalue = lvalue;
                    }
                    if (lvalue != null) {
                        minimum = Math.min(minimum, lvalue.doubleValue());
                    }
                    if (uvalue != null) {
                        maximum = Math.max(maximum, uvalue.doubleValue());
                    }

                }
            }
            if (minimum == Double.POSITIVE_INFINITY) {
                return null;
            }
            else {
                return new Range(minimum, maximum);
            }
        }
        else {
            return null; // unrecognised dataset...how should this be handled?
        }

    }

    /**
     * Returns the range of values in the range for the dataset.  This method
     * is the partner for the getDomainExtent method.
     *
     * @param data  the dataset.
     *
     * @return the range of values in the range for the dataset.
     */
    public static Range getRangeExtent(Dataset data) {

        // check parameters...
        if (data == null) {
            return null;
        }

        // work out the minimum value...
        if (data instanceof RangeInfo) {
            RangeInfo info = (RangeInfo) data;
            return info.getValueRange();
        }

        // hasn't implemented RangeInfo, so we'll have to iterate...
        else if (data instanceof CategoryDataset) {

            CategoryDataset tableData = (CategoryDataset) data;
            double minimum = Double.POSITIVE_INFINITY;
            double maximum = Double.NEGATIVE_INFINITY;
            int rowCount = tableData.getRowCount();
            int columnCount = tableData.getColumnCount();
            for (int row = 0; row < rowCount; row++) {
                for (int column = 0; column < columnCount; column++) {
                    Number lvalue = null;
                    Number uvalue = null;
                    if (data instanceof IntervalCategoryDataset) {
                        IntervalCategoryDataset icd = (IntervalCategoryDataset) data;
                        lvalue = icd.getStartValue(row, column);
                        uvalue = icd.getEndValue(row, column);
                    }
                    else {
                        lvalue = tableData.getValue(row, column);
                        uvalue = lvalue;
                    }
                    if (lvalue != null) {
                        minimum = Math.min(minimum, lvalue.doubleValue());
                    }
                    if (uvalue != null) {
                        maximum = Math.max(maximum, uvalue.doubleValue());
                    }
                }
            }
            if (minimum == Double.POSITIVE_INFINITY) {
                return null;
            }
            else {
                return new Range(minimum, maximum);
            }

        }
        else if (data instanceof XYDataset) {

            // hasn't implemented RangeInfo, so we'll have to iterate...
            XYDataset xyData = (XYDataset) data;
            double minimum = Double.POSITIVE_INFINITY;
            double maximum = Double.NEGATIVE_INFINITY;
            int seriesCount = xyData.getSeriesCount();
            for (int series = 0; series < seriesCount; series++) {
                int itemCount = xyData.getItemCount(series);
                for (int item = 0; item < itemCount; item++) {

                    Number lvalue = null;
                    Number uvalue = null;
                    if (data instanceof IntervalXYDataset) {
                        IntervalXYDataset intervalXYData = (IntervalXYDataset) data;
                        lvalue = intervalXYData.getStartYValue(series, item);
                        uvalue = intervalXYData.getEndYValue(series, item);
                    }
                    else if (data instanceof HighLowDataset) {
                        HighLowDataset highLowData = (HighLowDataset) data;
                        lvalue = highLowData.getLowValue(series, item);
                        uvalue = highLowData.getHighValue(series, item);
                    }
                    else {
                        lvalue = xyData.getYValue(series, item);
                        uvalue = lvalue;
                    }
                    if (lvalue != null) {
                        minimum = Math.min(minimum, lvalue.doubleValue());
                    }
                    if (uvalue != null) {
                        maximum = Math.max(maximum, uvalue.doubleValue());
                    }

                }
            }
            if (minimum == Double.POSITIVE_INFINITY) {
                return null;
            }
            else {
                return new Range(minimum, maximum);
            }

        }
        else {
            return null;
        }

    }

    /**
     * Returns the minimum domain value for the specified dataset.
     * <P>
     * This is easy if the dataset implements the DomainInfo interface (a good
     * idea if there is an efficient way to determine the minimum value).
     * Otherwise, it involves iterating over the entire data-set.
     * <p>
     * Returns null if all the data values in the dataset are null.
     *
     * @param data  the dataset.
     *
     * @return the minimum domain value in the dataset (or null).
     */
    public static Number getMinimumDomainValue(Dataset data) {

        // check parameters...
        if (data == null) {
            throw new IllegalArgumentException(
                "DatasetUtilities.getMinimumDomainValue: null dataset not allowed.");
        }

        if ((data instanceof CategoryDataset) && !(data instanceof XYDataset)) {
            throw new IllegalArgumentException("DatasetUtilities.getMinimumDomainValue(...): "
                + "TableDataset does not have numerical domain.");
        }

        // work out the minimum value...
        if (data instanceof DomainInfo) {
            DomainInfo info = (DomainInfo) data;
            return info.getMinimumDomainValue();
        }

        // hasn't implemented DomainInfo, so iterate...
        else if (data instanceof XYDataset) {
            double minimum = Double.POSITIVE_INFINITY;
            XYDataset xyData = (XYDataset) data;
            int seriesCount = xyData.getSeriesCount();
            for (int series = 0; series < seriesCount; series++) {
                int itemCount = xyData.getItemCount(series);
                for (int item = 0; item < itemCount; item++) {

                    Number value = null;
                    if (data instanceof IntervalXYDataset) {
                        IntervalXYDataset intervalXYData = (IntervalXYDataset) data;
                        value = intervalXYData.getStartXValue(series, item);
                    }
                    else {
                        value = xyData.getXValue(series, item);
                    }
                    if (value != null) {
                        minimum = Math.min(minimum, value.doubleValue());
                    }

                }
            }
            if (minimum == Double.POSITIVE_INFINITY) {
                return null;
            }
            else {
                return new Double(minimum);
            }
        }

        else {
            return null; // unrecognised dataset...how should this be handled?
        }

    }

    /**
     * Returns the maximum domain value for the specified dataset.
     * <P>
     * This is easy if the dataset implements the DomainInfo interface (a good
     * idea if there is an efficient way to determine the maximum value).
     * Otherwise, it involves iterating over the entire data-set.
     * <p>
     * Returns null if all the data values in the dataset are null.
     *
     * @param data  the dataset.
     *
     * @return the maximum domain value in the dataset (or null).
     */
    public static Number getMaximumDomainValue(Dataset data) {

        // check parameters...
        if (data == null) {
            throw new IllegalArgumentException(
                "Datasets.getMaximumDomainValue: null dataset not allowed.");
        }

        if ((data instanceof CategoryDataset) && !(data instanceof XYDataset)) {
            throw new IllegalArgumentException("Datasets.getMaximumDomainValue(...): "
                + "CategoryDataset does not have numerical domain.");
        }

        // work out the maximum value...
        if (data instanceof DomainInfo) {
            DomainInfo info = (DomainInfo) data;
            return info.getMaximumDomainValue();
        }

        // hasn't implemented DomainInfo, so iterate...
        else if (data instanceof XYDataset) {
            XYDataset xyData = (XYDataset) data;
            double maximum = Double.NEGATIVE_INFINITY;
            int seriesCount = xyData.getSeriesCount();
            for (int series = 0; series < seriesCount; series++) {
                int itemCount = xyData.getItemCount(series);
                for (int item = 0; item < itemCount; item++) {

                    Number value = null;
                    if (data instanceof IntervalXYDataset) {
                        IntervalXYDataset intervalXYData = (IntervalXYDataset) data;
                        value = intervalXYData.getEndXValue(series, item);
                    }
                    else {
                        value = xyData.getXValue(series, item);
                    }
                    if (value != null) {
                        maximum = Math.max(maximum, value.doubleValue());
                    }
                }
            }
            if (maximum == Double.NEGATIVE_INFINITY) {
                return null;
            }
            else {
                return new Double(maximum);
            }

        }
        else {
            return null; // unrecognised dataset...how should this be handled?
        }

    }

    /**
     * Returns the minimum range value for the specified dataset.
     * <P>
     * This is easy if the dataset implements the RangeInfo interface (a good
     * idea if there is an efficient way to determine the minimum value).
     * Otherwise, it involves iterating over the entire data-set.
     * <p>
     * Returns null if all the data values in the dataset are null.
     *
     * @param data  the dataset.
     *
     * @return the minimum range value in the dataset (or null).
     */
    public static Number getMinimumRangeValue(Dataset data) {

        // check parameters...
        if (data == null) {
            throw new IllegalArgumentException(
                "Datasets.getMinimumRangeValue: null dataset not allowed.");
        }

        // work out the minimum value...
        if (data instanceof RangeInfo) {
            RangeInfo info = (RangeInfo) data;
            return info.getMinimumRangeValue();
        }

        // hasn't implemented RangeInfo, so we'll have to iterate...
        else if (data instanceof CategoryDataset) {

            CategoryDataset categoryData = (CategoryDataset) data;
            double minimum = Double.POSITIVE_INFINITY;
            int seriesCount = categoryData.getRowCount();
            int itemCount = categoryData.getColumnCount();
            for (int series = 0; series < seriesCount; series++) {
                for (int item = 0; item < itemCount; item++) {
                    Number value = null;
                    if (data instanceof IntervalCategoryDataset) {
                        IntervalCategoryDataset icd = (IntervalCategoryDataset) data;
                        value = icd.getStartValue(series, item);
                    }
                    else {
                        value = categoryData.getValue(series, item);
                    }
                    if (value != null) {
                        minimum = Math.min(minimum, value.doubleValue());
                    }
                }
            }
            if (minimum == Double.POSITIVE_INFINITY) {
                return null;
            }
            else {
                return new Double(minimum);
            }

        }
        else if (data instanceof XYDataset) {

            // hasn't implemented RangeInfo, so we'll have to iterate...
            XYDataset xyData = (XYDataset) data;
            double minimum = Double.POSITIVE_INFINITY;
            int seriesCount = xyData.getSeriesCount();
            for (int series = 0; series < seriesCount; series++) {
                int itemCount = xyData.getItemCount(series);
                for (int item = 0; item < itemCount; item++) {

                    Number value = null;
                    if (data instanceof IntervalXYDataset) {
                        IntervalXYDataset intervalXYData = (IntervalXYDataset) data;
                        value = intervalXYData.getStartYValue(series, item);
                    }
                    else if (data instanceof HighLowDataset) {
                        HighLowDataset highLowData = (HighLowDataset) data;
                        value = highLowData.getLowValue(series, item);
                    }
                    else {
                        value = xyData.getYValue(series, item);
                    }
                    if (value != null) {
                        minimum = Math.min(minimum, value.doubleValue());
                    }

                }
            }
            if (minimum == Double.POSITIVE_INFINITY) {
                return null;
            }
            else {
                return new Double(minimum);
            }

        }
        else {
            return null;
        }

    }

    /**
     * Returns the maximum range value for the specified dataset.
     * <P>
     * This is easy if the dataset implements the RangeInfo interface (a good
     * idea if there is an efficient way to determine the maximum value).
     * Otherwise, it involves iterating over the entire data-set.
     * <p>
     * Returns null if all the data values are null.
     *
     * @param data  the dataset.
     *
     * @return the maximum range value in the dataset (or null).
     */
    public static Number getMaximumRangeValue(Dataset data) {

        // check parameters...
        if (data == null) {
            throw new IllegalArgumentException(
                "Datasets.getMinimumRangeValue: null dataset not allowed.");
        }

        // work out the minimum value...
        if (data instanceof RangeInfo) {
            RangeInfo info = (RangeInfo) data;
            return info.getMaximumRangeValue();
        }

        // hasn't implemented RangeInfo, so we'll have to iterate...
        else if (data instanceof CategoryDataset) {

            CategoryDataset categoryData = (CategoryDataset) data;
            double maximum = Double.NEGATIVE_INFINITY;
            int seriesCount = categoryData.getRowCount();
            int itemCount = categoryData.getColumnCount();
            for (int series = 0; series < seriesCount; series++) {
                for (int item = 0; item < itemCount; item++) {
                    Number value = null;
                    if (data instanceof IntervalCategoryDataset) {
                        IntervalCategoryDataset icd = (IntervalCategoryDataset) data;
                        value = icd.getEndValue(series, item);
                    }
                    else {
                        value = categoryData.getValue(series, item);
                    }
                    if (value != null) {
                        maximum = Math.max(maximum, value.doubleValue());
                    }
                }
            }
            if (maximum == Double.NEGATIVE_INFINITY) {
                return null;
            }
            else {
                return new Double(maximum);
            }

        }
        else if (data instanceof XYDataset) {

            XYDataset xyData = (XYDataset) data;
            double maximum = Double.NEGATIVE_INFINITY;
            int seriesCount = xyData.getSeriesCount();
            for (int series = 0; series < seriesCount; series++) {
                int itemCount = xyData.getItemCount(series);
                for (int item = 0; item < itemCount; item++) {
                    Number value = null;
                    if (data instanceof IntervalXYDataset) {
                        IntervalXYDataset intervalXYData = (IntervalXYDataset) data;
                        value = intervalXYData.getEndYValue(series, item);
                    }
                    else if (data instanceof HighLowDataset) {
                        HighLowDataset highLowData = (HighLowDataset) data;
                        value = highLowData.getHighValue(series, item);
                    }
                    else {
                        value = xyData.getYValue(series, item);
                    }
                    if (value != null) {
                        maximum = Math.max(maximum, value.doubleValue());
                    }
                }
            }
            if (maximum == Double.NEGATIVE_INFINITY) {
                return null;
            }
            else {
                return new Double(maximum);
            }

        }
        else {
            return null;
        }

    }

    /**
     * Creates a pie dataset from a table dataset by taking all the values
     * for a single row.
     *
     * @param data  the data.
     * @param rowKey  the row key.
     *
     * @return a pie dataset.
     */
    public static PieDataset createPieDatasetForRow(CategoryDataset data, Comparable rowKey) {

        int row = data.getRowIndex(rowKey);
        return createPieDatasetForRow(data, row);

    }

    /**
     * Creates a pie dataset from a table dataset by taking all the values
     * for a single row.
     *
     * @param data  the data.
     * @param row  the row (zero-based index).
     *
     * @return a pie dataset.
     */
    public static PieDataset createPieDatasetForRow(CategoryDataset data, int row) {

        DefaultPieDataset result = new DefaultPieDataset();
        int columnCount = data.getColumnCount();
        for (int current = 0; current < columnCount; current++) {
            Comparable columnKey = data.getColumnKey(current);
            result.setValue(columnKey, data.getValue(row, current));
        }
        return result;

    }

    /**
     * Creates a pie dataset from a table dataset by taking all the values
     * for a single column.
     *
     * @param data  the data.
     * @param columnKey  the column key.
     *
     * @return a pie dataset.
     */
    public static PieDataset createPieDatasetForColumn(CategoryDataset data,
                                                       Comparable columnKey) {

        int column = data.getColumnIndex(columnKey);
        return createPieDatasetForColumn(data, column);

    }

    /**
     * Creates a pie dataset from a table dataset by taking all the values
     * for a single column.
     *
     * @param data  the data.
     * @param column  the column (zero-based index).
     *
     * @return a pie dataset.
     */
    public static PieDataset createPieDatasetForColumn(CategoryDataset data, int column) {

        DefaultPieDataset result = new DefaultPieDataset();
        int rowCount = data.getRowCount();
        for (int i = 0; i < rowCount; i++) {
            Comparable rowKey = data.getRowKey(i);
            result.setValue(rowKey, data.getValue(i, column));
        }
        return result;

    }

    /**
     * Calculates the total of all the values in a {@link PieDataset}.
     *
     * @param data  the dataset.
     *
     * @return the total.
     */
    public static double getPieDatasetTotal(PieDataset data) {

        // get a list of categories...
        List categories = data.getKeys();

        // compute the total value of the data series skipping over the negative values
        double totalValue = 0;
        Iterator iterator = categories.iterator();
        while (iterator.hasNext()) {
            Comparable current = (Comparable) iterator.next();
            if (current != null) {
                Number value = data.getValue(current);
                double v = 0.0;
                if (value != null) {
                    v = value.doubleValue();
                }
                if (v > 0) {
                    totalValue = totalValue + v;
                }
            }
        }
        return totalValue;
    }

    /**
     * Returns the minimum and maximum values for the dataset's range (as in domain/range),
     * assuming that the series in one category are stacked.
     *
     * @param data  the dataset.
     *
     * @return the value range.
     */
    public static Range getStackedRangeExtent(CategoryDataset data) {

        Range result = null;

        if (data != null) {

            double minimum = 0.0;
            double maximum = 0.0;

            int categoryCount = data.getColumnCount();
            for (int item = 0; item < categoryCount; item++) {
                double positive = 0.0;
                double negative = 0.0;
                int seriesCount = data.getRowCount();
                for (int series = 0; series < seriesCount; series++) {
                    Number number = data.getValue(series, item);
                    if (number != null) {
                        double value = number.doubleValue();
                        if (value > 0.0) {
                            positive = positive + value;
                        }
                        if (value < 0.0) {
                            negative = negative + value;  // '+', remember value is negative
                        }
                    }
                }
                minimum = Math.min(minimum, negative);
                maximum = Math.max(maximum, positive);

            }

            result = new Range(minimum, maximum);

        }

        return result;

    }

    /**
     * Returns the minimum value in the dataset range, assuming that values in
     * each category are "stacked".
     *
     * @param data  the dataset.
     *
     * @return the minimum value.
     */
    public static Number getMinimumStackedRangeValue(CategoryDataset data) {

        Number result = null;

        if (data != null) {

            double minimum = 0.0;

            int categoryCount = data.getRowCount();
            for (int item = 0; item < categoryCount; item++) {
                double total = 0.0;

                int seriesCount = data.getColumnCount();
                for (int series = 0; series < seriesCount; series++) {
                    Number number = data.getValue(series, item);
                    if (number != null) {
                        double value = number.doubleValue();
                        if (value < 0.0) {
                            total = total + value;  // '+', remember value is negative
                        }
                    }
                }
                minimum = Math.min(minimum, total);

            }

            result = new Double(minimum);

        }

        return result;

    }

    /**
     * Returns the maximum value in the dataset range, assuming that values in
     * each category are "stacked".
     *
     * @param data  the dataset.
     *
     * @return the maximum value.
     */
    public static Number getMaximumStackedRangeValue(CategoryDataset data) {

        Number result = null;

        if (data != null) {

            double maximum = 0.0;

            int categoryCount = data.getRowCount();
            for (int item = 0; item < categoryCount; item++) {
                double total = 0.0;

                int seriesCount = data.getColumnCount();
                for (int series = 0; series < seriesCount; series++) {
                    Number number = data.getValue(series, item);
                    if (number != null) {
                        double value = number.doubleValue();
                        if (value > 0.0) {
                            total = total + value;
                        }
                    }
                }
                maximum = Math.max(maximum, total);

            }

            result = new Double(maximum);

        }

        return result;

    }

    /**
     * Creates an {@link XYDataset} by sampling the specified function over a fixed range.
     *
     * @param f  the function.
     * @param start  the start value for the range.
     * @param end  the end value for the range.
     * @param samples  the number of samples (must be > 1).
     * @param seriesName  the name to give the resulting series.
     *
     * @return  the dataset.
     */
    public static XYDataset sampleFunction2D(Function2D f,
                                             double start, double end, int samples,
                                             String seriesName) {

        if (start >= end) {
            throw new IllegalArgumentException("DatasetUtilities.createXYDataset(...): "
                + "start must be before end.");
        }

        if (samples < 2) {
            throw new IllegalArgumentException("DatasetUtilities.createXYDataset(...): "
                + "samples must be at least 2.");
        }

        XYSeries series = new XYSeries(seriesName);

        double step = (end - start) / samples;
        for (int i = 0; i <= samples; i++) {
            double x = start + (step * i);
            series.add(x, f.getValue(x));
        }

        XYSeriesCollection collection = new XYSeriesCollection(series);
        return collection;

    }

    /**
     * Creates a {@link CategoryDataset} that contains a copy of the data in an array
     * (instances of <code>Double</code> are created to represent the data items).
     * <p>
     * Row and column keys are created by appending 0, 1, 2, ... to the supplied prefixes.
     *
     * @param rowKeyPrefix  the row key prefix.
     * @param columnKeyPrefix  the column key prefix.
     * @param data  the data.
     *
     * @return the dataset.
     */
    public static CategoryDataset createCategoryDataset(String rowKeyPrefix,
                                                        String columnKeyPrefix,
                                                        double[][] data) {

        DefaultCategoryDataset result = new DefaultCategoryDataset();
        for (int r = 0; r < data.length; r++) {
            String rowKey = rowKeyPrefix + (r + 1);
            for (int c = 0; c < data[r].length; c++) {
                String columnKey = columnKeyPrefix + (c + 1);
                result.addValue(new Double(data[r][c]), rowKey, columnKey);
            }
        }
        return result;

    }

    /**
     * Creates a {@link CategoryDataset} that contains a copy of the data in an array.
     * <p>
     * Row and column keys are created by appending 0, 1, 2, ... to the supplied prefixes.
     *
     * @param rowKeyPrefix  the row key prefix.
     * @param columnKeyPrefix  the column key prefix.
     * @param data  the data.
     *
     * @return the dataset.
     */
    public static CategoryDataset createCategoryDataset(String rowKeyPrefix,
                                                        String columnKeyPrefix,
                                                        Number[][] data) {

        DefaultCategoryDataset result = new DefaultCategoryDataset();
        for (int r = 0; r < data.length; r++) {
            String rowKey = rowKeyPrefix + (r + 1);
            for (int c = 0; c < data[r].length; c++) {
                String columnKey = columnKeyPrefix + (c + 1);
                result.addValue(data[r][c], rowKey, columnKey);
            }
        }
        return result;

    }

    /**
     * Creates a {@link CategoryDataset} that contains a copy of the data in an array
     * (instances of <code>Double</code> are created to represent the data items).
     * <p>
     * Row and column keys are taken from the supplied arrays.
     *
     * @param rowKeys  the row keys.
     * @param columnKeys  the column keys.
     * @param data  the data.
     *
     * @return The dataset.
     */
    public static CategoryDataset createCategoryDataset(String[] rowKeys,
                                                        String[] columnKeys,
                                                        double[][] data) {

        DefaultCategoryDataset result = new DefaultCategoryDataset();
        for (int r = 0; r < data.length; r++) {
            String rowKey = rowKeys[r];
            for (int c = 0; c < data[r].length; c++) {
                String columnKey = columnKeys[c];
                result.addValue(new Double(data[r][c]), rowKey, columnKey);
            }
        }
        return result;

    }

    /**
     * Creates a {@link CategoryDataset} by copying the data from the supplied {@link KeyedValues}
     * instance.
     *
     * @param rowKey  the row key.
     * @param rowData  the row data.
     *
     * @return A dataset.
     */
    public static CategoryDataset createCategoryDataset(String rowKey, KeyedValues rowData) {

        DefaultCategoryDataset result = new DefaultCategoryDataset();
        for (int i = 0; i < rowData.getItemCount(); i++) {
            result.addValue(rowData.getValue(i), rowKey, rowData.getKey(i));
        }
        return result;

    }

    /**
     * Returns <code>true</code> if the dataset is empty (or <code>null</code>), and
     * <code>false</code> otherwise.
     *
     * @param data  the dataset (<code>null</code> permitted).
     *
     * @return A boolean.
     */
    public static boolean isEmptyOrNull(XYDataset data) {

        boolean result = true;

        if (data != null) {
            for (int s = 0; s < data.getSeriesCount(); s++) {
                if (data.getItemCount(s) > 0) {
                    result = false;
                    continue;
                }
            }
        }

        return result;

    }

    /**
     * Returns <code>true</code> if the dataset is empty (or <code>null</code>), and
     * <code>false</code> otherwise.
     *
     * @param dataset  the dataset (<code>null</code> permitted).
     *
     * @return A boolean.
     */
    public static boolean isEmptyOrNull(PieDataset dataset) {

        if (dataset == null) {
            return true;
        }

        int itemCount = dataset.getItemCount();
        if (itemCount == 0) {
            return true;
        }

        for (int item = 0; item < itemCount; item++) {
            Number y = dataset.getValue(item);
            if (y != null) {
                double yy = y.doubleValue();
                if (yy > 0.0) {
                    return false;
                }
            }
        }

        return true;

    }

    /**
     * Returns <code>true</code> if the dataset is empty (or <code>null</code>), and
     * <code>false</code> otherwise.
     *
     * @param data  the dataset (<code>null</code> permitted).
     *
     * @return A boolean.
     */
    public static boolean isEmptyOrNull(CategoryDataset data) {

        if (data == null) {
            return true;
        }

        int rowCount = data.getRowCount();
        int columnCount = data.getColumnCount();
        if (rowCount == 0 || columnCount == 0) {
            return true;
        }

        for (int r = 0; r < rowCount; r++) {
            for (int c = 0; c < columnCount; c++) {
                if (data.getValue(r, c) != null) {
                    return false;
                }

            }
        }

        return true;

    }

    /**
     * Creates an "Other" slice for percentages below the percent threshold.
     *
     * @param data  the PieDataset.
     * @param percentThreshold  the percent threshold.
     * @return A PieDataset.
     */
    public static PieDataset limitPieDataset(PieDataset data, double percentThreshold) {
        return DatasetUtilities.limitPieDataset(data, percentThreshold, 1, "Other");
    }

    /**
     * Create an "Other" slice for percentages below the percent threshold providing there
     * are more slices below the percent threshold than specified in the slice threshold.
     *
     * @param data  the PieDataset.
     * @param percentThreshold  the percent threshold.
     * @param sliceThreshold  the slice threshold.
     * @return A PieDataset.
     */
    public static PieDataset limitPieDataset(PieDataset data,
                                             double percentThreshold, int sliceThreshold) {
        return DatasetUtilities.limitPieDataset(data, percentThreshold, sliceThreshold, "Other");
    }

    /**
     * Create an Other slice with a given label for percentages below the percent threshold
     * providing there are more slices below the percent threshold than specified in the slice
     * threshold.
     *
     * @param data  the PieDataset.
     * @param percentThreshold  the percent threshold.
     * @param sliceThreshold  the slice threshold.
     * @param label  the label to give the "Other" slice.
     * @return A PieDataset.
     */
    public static PieDataset limitPieDataset(PieDataset data,
                                             double percentThreshold, int sliceThreshold,
                                             String label) {
        DefaultPieDataset newDataset = new DefaultPieDataset();
        double total = DatasetUtilities.getPieDatasetTotal(data);

        //  Iterate and find all keys below threshold percentThreshold
        List keys = data.getKeys();
        ArrayList otherKeys = new ArrayList();
        Iterator kIter = keys.iterator();
        while (kIter.hasNext()) {
            Comparable key = (Comparable) kIter.next();
            Number dataValue = data.getValue(key);
            if (dataValue != null) {
                double value = dataValue.doubleValue();
                if (value / total < percentThreshold / 100) {
                    otherKeys.add(key);
                }
            }
        }

        //  Create new dataset with keys above threshold percentThreshold
        kIter = keys.iterator();
        double otherValue = 0;
        while (kIter.hasNext()) {
            Comparable key = (Comparable) kIter.next();
            Number dataValue = data.getValue(key);
            if (dataValue != null) {
                if (otherKeys.contains(key) && otherKeys.size() > sliceThreshold) {
                    //  Do not add key to dataset
                    otherValue += dataValue.doubleValue();
                }
                else {
                    //  Add key to dataset
                    newDataset.setValue(key, dataValue);
                }
            }
            //  Add other category if applicable
            if (otherKeys.size() > sliceThreshold) {
                newDataset.setValue(label, otherValue);
            }
        }
        return newDataset;
    }

    /**
     * Returns the minimum and maximum values for the dataset's range,
     * assuming that the series are stacked.
     *
     * @param data  the dataset.
     * @return  the value range.
     */
    public static Range getStackedRangeExtent(TableXYDataset data) {
        // check parameters...
        if (data == null) {
            return null;
        }
        double minimum = Double.POSITIVE_INFINITY;
        double maximum = Double.NEGATIVE_INFINITY;
        for (int itemNo = 0; itemNo < data.getItemCount(); itemNo++) {
            double value = 0;
            for (int seriesNo = 0; seriesNo < data.getSeriesCount(); seriesNo++) {
                if (data.getYValue(seriesNo, itemNo) != null) {
                    value += (data.getYValue(seriesNo, itemNo).doubleValue());
                }
            }
            if (value > maximum) {
                maximum = value;
            } 
            if (value < minimum) {
                minimum = value;
            } 
        }
        if (minimum == Double.POSITIVE_INFINITY) {
            return null;
        } 
        else {
            return new Range(minimum, maximum);
        }
    }

}
