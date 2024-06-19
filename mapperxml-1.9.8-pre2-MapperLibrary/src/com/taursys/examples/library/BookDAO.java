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
 * This is a simple data access object for the books in the libary.
 * @author Marty Phelan
 * @version 1.0
 */
public class BookDAO extends GenericDAO {
  public static final String COLUMNS =
      "CATALOG_NO, TITLE, KEYWORDS, DATE_ADDED, COST, LOCATION_ID";
  public static final String VALUES_CLAUSE =
      "?,          ?,     ?,        ?,          ?,    ? ";
  public static final String UPDATE_COLUMNS =
      "TITLE = ?, KEYWORDS = ?, DATE_ADDED = ?, COST = ?, LOCATION_ID = ?";
  public static final String TABLENAME = "BOOK";
  public static final String PKCLAUSE = "CATALOG_NO = ?";
  public static final String SEARCH_CLAUSE =
      "UPPER(TITLE) like \'%\' || UPPER(?) || \'%\' " +
      "or UPPER(KEYWORDS) like \'%\' || UPPER(?) || \'%\'";

  /**
   * Creates a new BookDAO
   */
  public BookDAO() {
  }

  /**
   * Creates a new BookDAO with the given DataSource
   */
  public BookDAO(DataSource dataSource) {
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
        BookVO vo = new BookVO();
        vo.setCatalogNo(results.getString("CATALOG_NO"));
        vo.setTitle(results.getString("TITLE"));
        vo.setKeywords(results.getString("KEYWORDS"));
        vo.setDateAdded(results.getDate("DATE_ADDED"));
        vo.setLocationId(results.getInt("LOCATION_ID"));
        vo.setCost(results.getBigDecimal("COST"));
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

  /**
   * Get all books in the library collection.
   * @return Collection of BookVO's
   */
  public Collection getAllMatching(String searchKey) throws DAOException {
    ArrayList list = new ArrayList(100);
    String sql = "select " + COLUMNS + " from " + TABLENAME + " where " +
        SEARCH_CLAUSE;
    try {
      conn = dataSource.getConnection();
      stmt = conn.prepareStatement(sql);
      stmt.setString(1, searchKey);
      stmt.setString(2, searchKey);
      results = stmt.executeQuery();
      while (results.next()) {
        BookVO vo = new BookVO();
        vo.setCatalogNo(results.getString("CATALOG_NO"));
        vo.setTitle(results.getString("TITLE"));
        vo.setKeywords(results.getString("KEYWORDS"));
        vo.setDateAdded(results.getDate("DATE_ADDED"));
        vo.setLocationId(results.getInt("LOCATION_ID"));
        vo.setCost(results.getBigDecimal("COST"));
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

  /**
   * Get all books in the library collection.
   * @return Collection of BookVO's
   */
  public BookVO getByPrimaryKey(String catalogNo) throws DAOException {
    String sql = "select " + COLUMNS + " from " + TABLENAME +
        " where " + PKCLAUSE;
    BookVO vo;
    try {
      conn = dataSource.getConnection();
      stmt = conn.prepareStatement(sql);
      stmt.setString(1, catalogNo);
      results = stmt.executeQuery();
      if (!results.next()) {
        throw new DAONotFoundException(DAONotFoundException.NOT_FOUND_EXCEPTION,
            "getByPrimaryKey", sql, catalogNo);
      }
      vo = new BookVO();
      vo.setCatalogNo(results.getString("CATALOG_NO"));
      vo.setTitle(results.getString("TITLE"));
      vo.setKeywords(results.getString("KEYWORDS"));
      vo.setDateAdded(results.getDate("DATE_ADDED"));
      vo.setLocationId(results.getInt("LOCATION_ID"));
      vo.setCost(results.getBigDecimal("COST"));
    } catch (SQLException ex) {
      throw new DAOSQLException(DAOSQLException.SQL_EXCEPTION_DURING_QUERY,
          "getByPrimaryKey", sql, ex);
    } finally {
      closeAll();
    }
    return vo;
  }

  /**
   * Updates the given book
   * @return Collection of BookVO's
   */
  public void updateByPrimaryKey(BookVO vo) throws DAOException {
    String sql = "update " + TABLENAME + " set " + UPDATE_COLUMNS +
        " where " + PKCLAUSE;
    try {
      conn = dataSource.getConnection();
      stmt = conn.prepareStatement(sql);
      stmt.setString(1, vo.getTitle());
      stmt.setString(2, vo.getKeywords());
      stmt.setDate(3, dateToSQLDate(vo.getDateAdded()));
      stmt.setBigDecimal(4, vo.getCost());
      stmt.setInt(5, vo.getLocationId());
      stmt.setString(6, vo.getCatalogNo());
      int rowCount = stmt.executeUpdate();
      // Make sure 1 row updated
      if (rowCount == 0)
        throw new DAONotFoundException(DAONotFoundException.NOT_FOUND_EXCEPTION,
            "updateByPrimaryKey", sql, vo.getCatalogNo());
      // Make sure no more than 1 row updated
      if (rowCount > 1) {
        conn.rollback();
        throw new DAOMultiRowException(DAOMultiRowException.MULTI_ROW_AFFECTED_EXCEPTION,
            "updateByPrimaryKey", sql, vo.getCatalogNo());
      }
    } catch (SQLException ex) {
      throw new DAOSQLException(DAOSQLException.SQL_EXCEPTION_DURING_QUERY,
          "updateByPrimaryKey", sql, ex);
    } finally {
      closeAll();
    }
  }

  /**
   * Deletes the book for the given key
   * @return Collection of BookVO's
   */
  public void deleteByPrimaryKey(String catalogNo) throws DAOException {
    String sql = "delete from " + TABLENAME + " where " + PKCLAUSE;
    try {
      conn = dataSource.getConnection();
      stmt = conn.prepareStatement(sql);
      stmt.setString(1, catalogNo);
      int rowCount = stmt.executeUpdate();
      // Make sure 1 row deleted
      if (rowCount == 0)
        throw new DAONotFoundException(DAONotFoundException.NOT_FOUND_EXCEPTION,
            "deleteByPrimaryKey", sql, catalogNo);
      // Make sure no more than 1 row deleted
      if (rowCount > 1) {
        conn.rollback();
        throw new DAOMultiRowException(DAOMultiRowException.MULTI_ROW_AFFECTED_EXCEPTION,
            "deleteByPrimaryKey", sql, catalogNo);
      }
    } catch (SQLException ex) {
      throw new DAOSQLException(DAOSQLException.SQL_EXCEPTION_DURING_QUERY,
          "deleteByPrimaryKey", sql, ex);
    } finally {
      closeAll();
    }
  }

  /**
   * Creates a new book
   * @return Collection of BookVO's
   */
  public void create(BookVO vo) throws DAOException {
    String sql = "insert into " + TABLENAME + " (" + COLUMNS + " ) " +
        " values (" + VALUES_CLAUSE + " ) ";
    try {
      conn = dataSource.getConnection();
      stmt = conn.prepareStatement(sql);
      stmt.setString(1, vo.getCatalogNo());
      stmt.setString(2, vo.getTitle());
      stmt.setString(3, vo.getKeywords());
      java.util.Date dateAdded = vo.getDateAdded();
      stmt.setDate(4, dateToSQLDate(vo.getDateAdded()));
      stmt.setBigDecimal(5, vo.getCost());
      stmt.setInt(6, vo.getLocationId());
      int rowCount = stmt.executeUpdate();
    } catch (SQLException ex) {
      throw new DAOSQLException(DAOSQLException.SQL_EXCEPTION_DURING_INSERT,
          "insert", sql, ex);
    } finally {
      closeAll();
    }
  }
}