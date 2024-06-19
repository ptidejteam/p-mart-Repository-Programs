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
 * ------------------------
 * JdbcCategoryDataset.java
 * ------------------------
 * (C) Copyright 2002, 2003, by Bryan Scott and Contributors.
 *
 * Original Author:  Bryan Scott; Andy;
 * Contributor(s):   David Gilbert (for Object Refinery Limited);
 *
 * Changes
 * -------
 * 26-Apr-2002 : Creation based on JdbcXYDataSet, using code contributed from Andy;
 * 13-Aug-2002 : Updated Javadocs, import statements and formatting (DG);
 * 03-Sep-2002 : Added fix for bug 591385 (DG);
 * 18-Sep-2002 : Updated to support BIGINT (BS);
 * 16-Oct-2002 : Added fix for bug 586667 (DG);
 * 03-Feb-2003 : Added Types.DECIMAL (see bug report 677814) (DG);
 * 13-Jun-2003 : Added Types.TIME as suggest by Bryan Scott in the forum (DG);
 * 30-Jun-2003 : CVS Write test (BS);
 */

package org.jfree.data;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;

/**
 * A {@link CategoryDataset} implementation over a database JDBC result set.
 * The dataset is populated via a call to executeQuery with the string sql
 * query.
 * The sql query must return at least two columns.  The first column will be
 * the catagory name and remaining columns values.
 * executeQuery can be called a number of times.
 *
 * The database connection is read-only and no write back facility exists.
 *
 * @author Bryan Scott
 */
public class JDBCCategoryDataset extends DefaultCategoryDataset {

    /** The database connection. */
    private Connection connection;

    /** The statement. */
    private Statement statement;

    /** The result set. */
    private ResultSet resultSet;

    /** The result set meta data. */
    private ResultSetMetaData metaData;

    /**
     * Creates a new dataset with a database connection.
     *
     * @param  url  the URL of the database connection.
     * @param  driverName  the database driver class name.
     * @param  user  the database user.
     * @param  passwd  the database user's password.
     */
    public JDBCCategoryDataset(String url,
                               String driverName,
                               String user,
                               String passwd) {

        super();
        try {
            Class.forName(driverName);
            connection = DriverManager.getConnection(url, user, passwd);
            statement = connection.createStatement();
        }
        catch (ClassNotFoundException ex) {
            System.err.println("JDBCCategoryDataset: cannot find the database driver classes.");
            System.err.println(ex);
        }
        catch (SQLException ex) {
            System.err.println("JDBCCategoryDataset: cannot connect to the database.");
            System.err.println(ex);
        }
    }

    /**
     * Create a new dataset with the given database connection.
     *
     * @param connection  the database connection.
     */
    public JDBCCategoryDataset(Connection connection) {
        super();
        this.connection = connection;
    }

    /**
     * Creates a new dataset with the given database connection, and executes the supplied
     * query to populate the dataset.
     *
     * @param connection  the connection.
     * @param query  the query.
     */
    public JDBCCategoryDataset(Connection connection, String query) {
        this(connection);
        executeQuery(query);
    }

    /**
     * Populates the dataset by executing the supplied query against the existing database
     * connection.  If no connection exists then no action is taken.
     * <p>
     * The results from the query are extracted and cached locally, thus applying an upper
     * limit on how many rows can be retrieved successfully.
     *
     * @param query  the query.
     */
    public void executeQuery(String query) {

        // if there is no connection, just return
        if (connection == null) {
            System.err.println("JDBCCategoryDataset.executeQuery(...) : there is no connection.");
            return;
        }

        try {
            statement = connection.createStatement();
            resultSet = statement.executeQuery(query);
            metaData = resultSet.getMetaData();

            int columnCount = metaData.getColumnCount();

            if (columnCount < 2) {
                System.err.println("JDBCCategoryDataset.executeQuery(...) : insufficient columns "
                                   + "returned from the database.");
                return;
            }

            while (resultSet.next()) {
                // first column contains the row key...
                Comparable rowKey = resultSet.getString(1);
                for (int column = 2; column <= columnCount; column++) {

                    Comparable columnKey = metaData.getColumnName(column);
                    Number value = null;
                    int columnType = metaData.getColumnType(column);

                    switch (columnType) {
                        case Types.NUMERIC:
                        case Types.REAL:
                        case Types.INTEGER:
                        case Types.DOUBLE:
                        case Types.FLOAT:
                        case Types.DECIMAL:
                        case Types.BIGINT:
                            value = (Number) resultSet.getObject(column);
                            setValue(value, rowKey, columnKey);
                            break;

                        case Types.DATE:
                        case Types.TIME:
                        case Types.TIMESTAMP:
                            Date date = (Date) resultSet.getObject(column);
                            value = new Long(date.getTime());
                            setValue(value, rowKey, columnKey);
                            break;

                        default:
                            // not a value, can't use it
                            break;
                    }
                }
            }

            fireDatasetChanged();
        }
        catch (SQLException ex) {
            System.err.println(ex);
        }
        finally {
            if (resultSet != null) {
                try {
                    resultSet.close();
                }
                catch (Exception e) {
                    // report this?
                }
            }
            if (statement != null) {
                try {
                    statement.close();
                }
                catch (Exception e) {
                    // report this?
                }
            }
        }
    }

}
