/* ======================================
 * JFreeChart : a free Java chart library
 * ======================================
 *
 * Project Info:  http://www.object-refinery.com/jfreechart/index.html
 * Project Lead:  David Gilbert (david.gilbert@object-refinery.com);
 *
 * (C) Copyright 2000-2003, by Simba Management Limited and Contributors.
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
 * -------------------
 * JDBCPieDataset.java
 * -------------------
 * (C) Copyright 2002, 2003, by Bryan Scott and Contributors.
 *
 * Original Author:  Bryan Scott; Andy
 * Contributor(s):   David Gilbert (for Simba Management Limited);
 *
 * Changes
 * -------
 * 26-Apr-2002 : Creation based on JdbcXYDataSet, but extending DefaultPieDataset (BS);
 * 24-Jun-2002 : Removed unnecessary import and local variable (DG);
 * 13-Aug-2002 : Updated Javadoc comments and imports, removed default constructor (DG);
 * 18-Sep-2002 : Updated to support BIGINT (BS);
 * 21-Jan-2003 : Renamed JdbcPieDataset --> JDBCPieDataset (DG);
 * 03-Feb-2003 : Added Types.DECIMAL (see bug report 677814) (DG);
 *
 */

package com.jrefinery.data;

import java.sql.Connection;
import java.sql.Date;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.DriverManager;
import java.sql.Types;

/**
 * A pie dataset that reads data from a database via JDBC.
 * <P>
 * A query should be supplied that returns data in two columns, the first containing
 * VARCHAR data, and the second containing numerical data.  The data is cached in-memory
 * and can be refreshed at any time.
 * 
 * @author Bryan Scott.
 */
public class JDBCPieDataset extends DefaultPieDataset {

    /** The database connection. */
    private Connection connection;

    /** A statement. */
    private Statement statement;

    /** The query result set. */
    private ResultSet resultSet;

    /** Meta data about the result set. */
    private ResultSetMetaData metaData;

    /**
     * Creates a new JDBCPieDataset and establishes a new database connection.
     *
     * @param url  the URL of the database connection.
     * @param driverName  The database driver class name.
     * @param user        The database user.
     * @param passwd      The database users password.
     */
    public JDBCPieDataset(String url,
                          String driverName,
                          String user,
                          String passwd) {

        try {
            Class.forName(driverName);
            this.connection = DriverManager.getConnection(url, user, passwd);
            this.statement = connection.createStatement();
        }
        catch (ClassNotFoundException ex) {
            System.err.println("JDBCPieDataset: cannot find the database driver classes.");
            System.err.println(ex);
        }
        catch (SQLException ex) {
            System.err.println("JDBCPieDataset: cannot connect to this database.");
            System.err.println(ex);
        }
    }

    /**
     * Creates a new JDBCPieDataset using a pre-existing database connection.
     * <P>
     * The dataset is initially empty, since no query has been supplied yet.
     *
     * @param con  the database connection.
     */
    public JDBCPieDataset(Connection con) {

        this.connection = con;
        
    }

    /**
     * Creates a new JDBCPieDataset using a pre-existing database connection.
     * <P>
     * The dataset is initialised with the supplied query.
     *
     * @param con  the database connection.
     * @param query  the database connection.
     */
    public JDBCPieDataset(Connection con, String query) {
        this(con);
        executeQuery(query);
    }

    /**
     *  ExecuteQuery will attempt execute the query passed to it against the
     *  existing database connection.  If no connection exists then no action
     *  is taken.
     *  The results from the query are extracted and cached locally, thus
     *  applying an upper limit on how many rows can be retrieved successfully.
     *
     * @param  query  The query to be executed
     */
    public void executeQuery(String query) {

        if (connection == null) {
            System.err.println("There is no database to execute the query.");
            return;
        }

        try {
            statement = connection.createStatement();
            resultSet = statement.executeQuery(query);
            metaData = resultSet.getMetaData();

            int columnCount = metaData.getColumnCount();
            if (columnCount != 2) {
                System.err.println("Invalid sql generated.  PieDataSet requires 2 columns only");
            }

            while (resultSet.next()) {
                Comparable key = resultSet.getString(1);
                Number value = null;
                int columnType = metaData.getColumnType(2);
                switch (columnType) {
                    case Types.NUMERIC:
                    case Types.REAL:
                    case Types.INTEGER:
                    case Types.DOUBLE:
                    case Types.FLOAT:
                    case Types.DECIMAL:
                    case Types.BIGINT:
                        value = (Number) resultSet.getObject(2);
                        setValue(key, value);
                        break;

                    case Types.DATE:
                    case Types.TIMESTAMP:
                        Date date = (Date) resultSet.getObject(2);
                        value = new Long(date.getTime());
                        break;
                    default:
                        System.err.println("JDBCPieDataset - unknown data type");
                        break;
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
                    System.err.println("JDBCPieDataset: swallowing exception.");
                }
            }
            if (statement != null) {
                try {
                    statement.close();
                }
                catch (Exception e) {
                    System.err.println("JDBCPieDataset: swallowing exception.");
                }
            }
        }
    }

}
