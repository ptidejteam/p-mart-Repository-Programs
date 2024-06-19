/**
 * Example Mapper Application
 * by: Marty Phelan
 *
 * This example is free software; you can redistribute it and/or
 * modify it as you wish.  It is released to the public domain.
 *
 * This example is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 */
package com.taursys.examples.library;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;

import javax.sql.DataSource;

/**
 * This is a simple data access object for the all libary locations.
 * @author Marty Phelan
 * @version 1.0
 */
public class LocationDAO extends GenericDAO {
  public static final String COLUMNS = "LOCATION_ID, DESCRIPTION";
  public static final String TABLENAME = "LOCATION";

  /**
   * Creates a new LocationDAO
   */
  public LocationDAO() {
  }

  /**
   * Creates a new LocationDAO with the given DataSource
   */
  public LocationDAO(DataSource dataSource) {
    super(dataSource);
  }

  /**
   * Get all books in the library collection.
   * @return Collection of BookVO's
   */
  public Collection getAll() throws DAOException {
    ArrayList list = new ArrayList(100);
    String sql = "select " + COLUMNS + " from " + TABLENAME;
    try {
      conn = dataSource.getConnection();
      stmt = conn.prepareStatement(sql);
      results = stmt.executeQuery();
      while (results.next()) {
        LocationVO vo = new LocationVO();
        vo.setLocationId(results.getInt("LOCATION_ID"));
        vo.setDescription(results.getString("DESCRIPTION"));
        list.add(vo);
      }
    } catch (SQLException ex) {
      throw new DAOSQLException(DAOSQLException.SQL_EXCEPTION_DURING_QUERY,
          "getAll", sql, ex);
    } finally {
      closeAll();
    }
    return list;
  }
}