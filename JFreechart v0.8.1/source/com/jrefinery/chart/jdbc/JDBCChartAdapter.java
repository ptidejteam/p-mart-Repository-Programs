/* =======================================
 * JFreeChart : a Java Chart Class Library
 * =======================================
 *
 * Project Info:  http://www.object-refinery.com/jfreechart/index.html
 * Project Lead:  David Gilbert (david.gilbert@object-refinery.com);
 *
 * (C) Copyright 2000-2002, by Simba Management Limited and Contributors.
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
 * ------------------
 * JDBCChartAdapter.java
 * ------------------
 * (C) Copyright 2002, by Bryan Scott and Contributors.
 *
 * Original Author:  Bryan Scott;
 * Contributor(s):   -;
 *
 *
 * Changes
 * -------
 * 14-Mar-2002 : Version 1 contributed by Bryan Scott (DG);
 *
 */

package com.jrefinery.chart.jdbc;

import java.sql.*;
import java.util.*;
import com.jrefinery.chart.*;
import com.jrefinery.chart.data.*;
import com.jrefinery.data.*;

/**
 *  Description of the Class
 *  This class provides an chart XYDataset implementation over a database JDBC result set.
 *  The dataset is populated via a call to executeQuery with the string sql query.
 *  The sql query must return at least two columns.  The first column will be
 *  the x-axis and remaining columns y-axis values.
 *  executeQuery can be called a number of times.
 *
 *  The database connection is read-only and no write back facility exists.
 */

public class JDBCChartAdapter implements XYDataset, RangeInfo {

    Connection connection;
    Statement statement;
    ResultSet resultSet;
    ResultSetMetaData metaData;

    String[] columnNames = {};
    Vector rows = new Vector(0);

    /**  The maximum y value of the returned result set */
    protected double maxValue = 0.0;

    /**  The minimum y value of the returned result set */
    protected double minValue = 0.0;

    /** Storage for registered change listeners. */
    protected List listeners = new ArrayList();

    /**
     * Constructor
     * Create a new JDBCChartAdapter and establish a new database connection.
     *
     * @param  url         URL of the database connection
     * @param  driverName  The database driver class name
     * @param  user        The database user
     * @param  passwd      The database users password.
     */
    public JDBCChartAdapter(String url,
                            String driverName,
                            String user,
                            String passwd) {
        try {
            Class.forName(driverName);
            System.out.println("Opening db connection");
            connection = DriverManager.getConnection(url, user, passwd);
            statement = connection.createStatement();
        } catch (ClassNotFoundException ex) {
            System.err.println("Cannot find the database driver classes.");
            System.err.println(ex);
        } catch (SQLException ex) {
            System.err.println("Cannot connect to this database.");
            System.err.println(ex);
        }
    }

    /**
     * Constructor
     * Create a new JDBCChartAdapter using the specificied database connection.
     *
     * @param  con  The database connection to use
     */
    public JDBCChartAdapter(Connection con) {
        try {
            connection = con;
            statement = connection.createStatement();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     *  ExecuteQuery will attempt execute the query passed to it against the existing database
     *  connection.  If no connection exists then no action is taken.
     *  The results from the query are extracted and cached locally, thus applying an upper limit
     *  on how many rows can be retrieved successfully.
     *
     * @param  query  The query to be executed
     */
    public void executeQuery(String query) {
        Object xObject = null;
        int column = 0;
        int currentColumn = 0;
        int numberOfColumns = 0;
        int numberOfValidColumns = 0;
        boolean validColumns[] = null;

        if (connection == null || statement == null) {
            System.err.println("There is no database to execute the query.");
            return;
        }

        try {
            resultSet = statement.executeQuery(query);
            metaData = resultSet.getMetaData();

            numberOfColumns = metaData.getColumnCount();
            validColumns = new boolean[numberOfColumns];
            for (column = 0; column < numberOfColumns; column++) {
                try {
                    int type = metaData.getColumnType(column + 1);
                    switch (type) {
                        case Types.NUMERIC:
                        case Types.REAL:
                        case Types.INTEGER:
                        case Types.DOUBLE:
                        case Types.FLOAT:
                        case Types.BIT:
                        case Types.DATE:
                        case Types.TIMESTAMP:
                            ++numberOfValidColumns;
                            validColumns[column] = true;
                            break;
                        default:
                            System.err.println("Unable to load column " + column + "(" + type + ")");
                            validColumns[column] = false;
                            break;
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                    validColumns[column] = false;
                }
            }

            /// First colum is X data
            columnNames = new String[numberOfValidColumns - 1];
            /// Get the column names and cache them.
            //System.out.println("Starting column names");
            currentColumn = 0;
            for (column = 1; column < numberOfColumns; column++) {
                if (validColumns[column]) {
                    columnNames[currentColumn] = metaData.getColumnLabel(column + 1);
                    ++currentColumn;
            }
        }
        //System.out.println("Finished column names");

        /// Might need to add, to free memory from any previous result sets
        /// if ( rows != null) {
        ///   for (column = 0; column < rows.size(); column++) {
        ///       Vector row = rows.get(column);
        ///       row.removeAllElements();
        ///   }
        ///   rows.removeAllElements();
        /// }

        // Get all rows.
        rows = new Vector();
        while (resultSet.next()) {
            Vector newRow = new Vector();
            for (column = 0; column < numberOfColumns; column++) {
                if (validColumns[column]) {
                    xObject = resultSet.getObject(column + 1);
                    if (xObject instanceof Number) {
                        newRow.addElement((Number) xObject);
                    } else if (xObject instanceof java.util.Date) {
                        newRow.addElement(new Long(((java.util.Date) xObject).getTime()));
                    } else {
                        System.out.println("Unknown Data");
                        newRow.addElement(xObject);
                    }
                }
            }
            rows.addElement(newRow);
        }

        /// A Kludge to make everything work when no rows returned
        if (rows.size() == 0) {
            Vector newRow = new Vector();
            for (column = 0; column < numberOfColumns; column++) {
                if (validColumns[column]) {
                    newRow.addElement(new Integer(0));
                }
            }
            rows.addElement(newRow);
        }
        //  close(); Need to copy the metaData, bug in jdbc:odbc driver.

        /// Determine max and min values.
        if (rows.size() < 1) {
            maxValue = 0.0;
            minValue = 0.0;
        } else {
            Vector row = (Vector) rows.elementAt(0);
            double test;
            maxValue = ((Number) row.get(1)).doubleValue();
            minValue = maxValue;
            for (int rowNum = 0; rowNum < rows.size(); ++rowNum) {
                row = (Vector) rows.elementAt(rowNum);
                for (column = 1; column < numberOfColumns; column++) {
                    test = ((Number) row.get(column)).doubleValue();
                    if (test < minValue) {
                        minValue = test;
                    }
                    if (test > maxValue) {
                        maxValue = test;
                    }
                }
            }
        }

        fireDatasetChanged();// Tell the listeners a new table has arrived.
        } catch (SQLException ex) {
            System.err.println(ex);
        }
    }

    /**
     * Returns the x-value for the specified series and item.  The implementation is responsible for
     * ensuring that the x-values are presented in ascending order.
     *
     * @param  seriesIndex  The index of the series of interest (zero-based);
     * @param  itemIndex    The index of the item of interest (zero-based).
     * @return              The xValue value
     * @see                 XYDataSource
     */
    public Number getXValue(int seriesIndex, int itemIndex) {
        Vector row = (Vector) rows.elementAt(itemIndex);
        return (Number) row.elementAt(0);
    }

    /**
     * Returns the y-value for the specified series and item.
     *
     * @param  seriesIndex  The index of the series of interest (zero-based);
     * @param  itemIndex    The index of the item of interest (zero-based).
     * @return              The yValue value
     * @see                 XYDataSource
     */
    public Number getYValue(int seriesIndex, int itemIndex) {
        Vector row = (Vector) rows.elementAt(itemIndex);
        return (Number) row.elementAt(seriesIndex + 1);
    }

    /**
     * Returns the number of items in the specified series.
     *
     * @param  seriesIndex  The index of the series of interest (zero-based).
     * @return              The itemCount value
     * @see                 XYDataSource
     */
    public int getItemCount(int seriesIndex) {
        return rows.size();
    }

    /**
     * Returns the number of series in the data source;
     *
     * @return    The seriesCount value
     * @see       XYDataSource
     * @see       DataSource
     */
    public int getSeriesCount() {
        return columnNames.length;
    }

    /**
     * Returns the name of the specified series.
     *
     * @param  seriesIndex  The index of the required series (zero-based);
     * @return              The seriesName value
     * @see                 XYDataSource
     * @see                 DataSource
     */
    public String getSeriesName(int seriesIndex) {
        if ((seriesIndex < columnNames.length) && (columnNames[seriesIndex] != null)) {
            return columnNames[seriesIndex];
        } else {
            return "";
        }
    }

    /**
     * Returns the number of items that should be displayed in the legend.
     *
     * @return    The legendItemCount value
     */
    public int getLegendItemCount() {
        return getSeriesCount();
    }

    /**
     * Returns the legend item labels.
     *
     * @return    The legend item labels.
     */
    public String[] getLegendItemLabels() {
        return columnNames;
    }

    /**
     * Registers an object for notification of changes to the data source.
     *
     * @param  listener  The object being registered.
     * @see              XYDataset
     * @see              Dataset
     */
    public void addChangeListener(DatasetChangeListener listener) {
        listeners.add(listener);
    }

    /**
     * Unregisters an object for notification of changes to the data source.
     *
     * @param  listener  The object being unregistered.
     * @see              XYDataset
     * @see              Dataset
     */
    public void removeChangeListener(DatasetChangeListener listener) {
        listeners.remove(listener);
    }

    /**
     * Notifies all registered listeners that this data source has changed in some way.
     *
     * @see    XYDataset
     * @see    Dataset
     */
    protected void fireDatasetChanged() {
        notifyListeners(new DatasetChangeEvent(this));
    }

    /**
     * Notifies all registered listeners that the data source has been modified.
     *
     * @param  event  Contains information about the event that triggered the notification.
     * @see           XYDataset
     * @see           Dataset
     */
    protected void notifyListeners(DatasetChangeEvent event) {
        Iterator iterator = listeners.iterator();
        while (iterator.hasNext()) {
            DatasetChangeListener listener = (DatasetChangeListener) iterator.next();
            listener.datasetChanged(event);
        }
    }

    /**
     * Returns the minimum data value.
     *
     * @return    The minimumRangeValue value
     * @see       RangeInfo
     */
    public Number getMinimumRangeValue() {
        return new Double(minValue);
    }

    /**
     * Returns the maximum data value.
     *
     * @return    The maximumRangeValue value
     * @see       RangeInfo
     */
    public Number getMaximumRangeValue() {
        return new Double(maxValue);
    }

    /**  Close the database connection */
    public void close() {
        System.out.println("Closing db connection");
        try {
            resultSet.close();
        }
        catch (Exception e) {
        }
        try {
            statement.close();
        }
        catch (Exception e) {
        }
        try {
            connection.close();
        }
        catch (Exception e) {
        }
    }

}