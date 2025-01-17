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
 * -------------------------
 * TimeSeriesTableModel.java
 * -------------------------
 * (C) Copyright 2001-2003, by Object Refinery Limited.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited);
 * Contributor(s):   -;
 *
 * $Id: TimeSeriesTableModel.java,v 1.1 2007/10/10 19:09:12 vauchers Exp $
 *
 * Changes
 * -------
 * 14-Nov-2001 : Version 1 (DG);
 * 05-Apr-2002 : Removed redundant first column (DG);
 * 24-Jun-2002 : Removed unnecessary local variable (DG);
 * 07-Oct-2002 : Fixed errors reported by Checkstyle (DG);
 *
 */

package org.jfree.data;

import javax.swing.table.AbstractTableModel;

import org.jfree.data.time.RegularTimePeriod;
import org.jfree.data.time.TimeSeries;

/**
 * Wrapper around a time series to convert it to a table model for use in a JTable.
 *
 * @author David Gilbert
 */
public class TimeSeriesTableModel extends AbstractTableModel implements SeriesChangeListener {

    /** The series. */
    private TimeSeries series;

    /** A flag that controls whether the series is editable. */
    private boolean editable;

    /** The new time period. */
    private RegularTimePeriod newTimePeriod;

    /** The new value. */
    private Number newValue;

    /**
     * Default constructor.
     */
    public TimeSeriesTableModel() {
        this(new TimeSeries("Untitled"));
    }

    /**
     * Constructs a table model for a time series.
     *
     * @param series  the time series.
     */
    public TimeSeriesTableModel(TimeSeries series) {
        this(series, false);
    }

    /**
     * Creates a table model based on a time series.
     *
     * @param series  the time series.
     * @param editable  if <ocde>true</code>, the table is editable.
     */
    public TimeSeriesTableModel(TimeSeries series, boolean editable) {

        this.series = series;
        this.series.addChangeListener(this);
        this.editable = editable;
    }

    /**
     * Returns the number of columns in the table model.  For this particular
     * model, the column count is fixed at 2.
     *
     * @return The column count.
     */
    public int getColumnCount() {
        return 2;
    }

    /**
     * Returns the column class in the table model.
     *
     * @param column    The column index.
     * @return the column class in the table model.
     */
    public Class getColumnClass(int column) {

        if (column == 0) {
            return String.class;
        }
        else {
            if (column == 1) {
                return Double.class;
            }
            else {
                return null;
            }
        }

    }

    /**
     * Returns the name of a column
     *
     * @param column  the column index.
     *
     * @return the name of a column.
     */
    public String getColumnName(int column) {

        if (column == 0) {
            return "Period:";
        }
        else {
            if (column == 1) {
                return "Value:";
            }
            else {
                return null;
            }
        }

    }

    /**
     * Returns the number of rows in the table model.
     *
     * @return The row count.
     */
    public int getRowCount() {
        return this.series.getItemCount();
    }

    /**
     * Returns the data value for a cell in the table model.
     *
     * @param row       The row number.
     * @param column    The column number.
     * @return the data value for a cell in the table model.
     */
    public Object getValueAt(int row, int column) {

        if (row < this.series.getItemCount()) {
            if (column == 0) {
                return this.series.getTimePeriod(row);
            }
            else {
                if (column == 1) {
                    return this.series.getValue(row);
                }
                else {
                    return null;
                }
            }
        }
        else {
            if (column == 0) {
                return newTimePeriod;
            }
            else {
                if (column == 1) {
                    return newValue;
                }
                else {
                    return null;
                }
            }
        }

    }

    /**
     * Returns a flag indicating whether or not the specified cell is editable.
     *
     * @param row  the row number.
     * @param column  the column number.
     *
     * @return <code>true</code> if the specified cell is editable.
     */
    public boolean isCellEditable(int row, int column) {

        if (this.editable) {
            if ((column == 0) || (column == 1)) {
                return true;
            }
            else {
                return false;
            }
        }
        else {
            return false;
        }

    }

    /**
     * Updates the time series.
     *
     * @param value  the new value.
     * @param row  the row.
     * @param column  the column.
     */
    public void setValueAt(Object value, int row, int column) {

        if (row < this.series.getItemCount()) {

            // update the time series appropriately
            if (column == 1) {
                try {
                    Double v = Double.valueOf(value.toString());
                    this.series.update(row, v);

                }
                catch (NumberFormatException nfe) {
                    System.err.println("Number format exception");
                }
            }
        }
        else {
            if (column == 0) {
                // this.series.getClass().valueOf(value.toString());
                newTimePeriod = null;
            }
            else if (column == 1) {
                newValue = Double.valueOf(value.toString());
            }
        }
    }

    /**
     * Receives notification that the time series has been changed.  Responds
     * by firing a table data change event.
     *
     * @param event  the event.
     */
    public void seriesChanged(SeriesChangeEvent event) {
        fireTableDataChanged();
    }

}
