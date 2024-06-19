package com.taursys.examples.library;

import javax.sql.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Title:
 * Description:
 * Copyright:    Copyright (c) 2002
 * Company:
 * @author
 * @version 1.0
 */

public class GenericDAO {
  protected DataSource dataSource;
  protected Connection conn = null;
  protected PreparedStatement stmt = null;
  protected ResultSet results = null;

  /**
   * Creates a new GenericDAO
   */
  public GenericDAO() {
  }

  /**
   * Creates a new GenericDAO with the given DataSource
   */
  public GenericDAO(DataSource dataSource) {
    this.dataSource = dataSource;
  }

  /**
   * Gets the current DataSource
   */
  public DataSource getDataSource() {
    return dataSource;
  }

  /**
   * Sets the DataSource that this DAO will use
   */
  public void setDataSource(DataSource newDataSource) {
    dataSource = newDataSource;
  }

  /**
   * Converts a java.util.Date to a java.sql.Date
   */
  protected java.sql.Date dateToSQLDate(java.util.Date d) {
    if (d == null)
      return null;
    else
      return new java.sql.Date(d.getTime());
  }

  /**
   * Closes resultset, statement and connection.
   * Makes progressive attempts to close everything.
   * May result in multiple exceptions
   */
  protected void closeAll() throws DAOException {
    if (results != null) {
      try {
        results.close();
      } catch (SQLException ex) {
        throw new DAOSQLException(DAOSQLException.SQL_EXCEPTION_DURING_CLOSE,
          "closeResults", "n/a", ex);
      } finally {
        results = null;
        if (stmt != null) {
          try {
            stmt.close();
          } catch (SQLException ex) {
            throw new DAOSQLException(DAOSQLException.SQL_EXCEPTION_DURING_CLOSE,
              "closeStmt", "n/a", ex);
          } finally {
            stmt = null;
            if (conn != null) {
              try {
                conn.close();
              } catch (SQLException ex) {
                throw new DAOSQLException(DAOSQLException.SQL_EXCEPTION_DURING_CLOSE,
                  "closeConn", "n/a", ex);
              } finally {
                conn = null;
              }
            }
          }
        }
      }
    }
  }
}