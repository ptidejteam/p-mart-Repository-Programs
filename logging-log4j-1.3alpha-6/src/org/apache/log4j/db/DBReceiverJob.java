/*
 * Copyright 1999,2004 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.log4j.db;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.helpers.Constants;
import org.apache.log4j.scheduler.Job;
import org.apache.log4j.spi.ComponentBase;
import org.apache.log4j.spi.LoggerRepository;
import org.apache.log4j.spi.LoggingEvent;
import org.apache.log4j.spi.ThrowableInformation;
import org.apache.log4j.spi.location.LocationInfo;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Vector;

/**
 * Actual retrieval of data is made by the instance of DBReceiverJob associated
 * with DBReceiver.
 * 
 * @author Ceki G&uuml;lc&uuml;
 */
class DBReceiverJob extends ComponentBase implements Job {

  long lastId = 0;

  DBReceiver parentDBReceiver;

  DBReceiverJob(DBReceiver parent) {
    parentDBReceiver = parent;
  }

  public void execute() {
    getLogger().debug("DBReceiverJob.execute() called");

    Connection connection = null;

    try {
      Logger logger;
      LoggerRepository loggerRepository = parentDBReceiver
          .getLoggerRepository();
      connection = parentDBReceiver.connectionSource.getConnection();

      StringBuffer sql = new StringBuffer();
      sql.append("SELECT ");
      sql.append("sequence_number, ");
      sql.append("timestamp, ");
      sql.append("rendered_message, ");
      sql.append("logger_name, ");
      sql.append("level_string, ");
      sql.append("ndc, ");
      sql.append("thread_name, ");
      sql.append("reference_flag, ");
      sql.append("caller_filename, ");
      sql.append("caller_class, ");
      sql.append("caller_method, ");
      sql.append("caller_line, ");
      sql.append("event_id ");
      sql.append("FROM logging_event ");

      // have subsequent SELECTs start from we left off last time
      sql.append(" WHERE event_id > " + lastId);
      sql.append(" ORDER BY event_id ASC");

      Statement statement = connection.createStatement();
      ResultSet rs = statement.executeQuery(sql.toString());
      //rs.beforeFirst();

      while (rs.next()) {
        LoggingEvent event = new LoggingEvent();

        event.setSequenceNumber(rs.getLong(1));
        event.setTimeStamp(rs.getLong(2));
        event.setRenderedMessage(rs.getString(3));
        event.setLoggerName(rs.getString(4));
        String levelStr = rs.getString(5);

        // TODO CG The conversion of levelStr should be more general
        Level level = Level.toLevel(levelStr);
        event.setLevel(level);
        event.setNDC(rs.getString(6));
        event.setThreadName(rs.getString(7));

        short mask = rs.getShort(8);

        String fileName = rs.getString(9);
        String className = rs.getString(10);
        String methodName = rs.getString(11);
        String lineNumber = rs.getString(12).trim();

        if (fileName.equals(LocationInfo.NA)) {
          event.setLocationInformation(LocationInfo.NA_LOCATION_INFO);
        } else {
          event.setLocationInformation(new LocationInfo(fileName, className,
              methodName, lineNumber));
        }

        long id = rs.getLong(13);
        //LogLog.info("Received event with id=" + id);
        lastId = id;

        // Scott asked for this info to be
        event.setProperty(Constants.LOG4J_ID_KEY, Long.toString(id));

        if ((mask & DBHelper.PROPERTIES_EXIST) != 0) {
          getProperties(connection, id, event);
        }

        if ((mask & DBHelper.EXCEPTION_EXISTS) != 0) {
          getException(connection, id, event);
        }

        if (!parentDBReceiver.isPaused()) {
          parentDBReceiver.doPost(event);
        }
      } // while
      statement.close();
      statement = null;
    } catch (SQLException sqle) {
      getLogger().error("Problem receiving events", sqle);
    } finally {
      closeConnection(connection);
    }
  }

  void closeConnection(Connection connection) {
    if (connection != null) {
      try {
        //LogLog.warn("closing the connection. ", new Exception("x"));
        connection.close();
      } catch (SQLException sqle) {
        // nothing we can do here
      }
    }
  }

  /**
   * Retrieve the event properties from the logging_event_property table.
   * 
   * @param connection
   * @param id
   * @param event
   * @throws SQLException
   */
  void getProperties(Connection connection, long id, LoggingEvent event)
      throws SQLException {
    String sql = "SELECT mapped_key, mapped_value FROM logging_event_property WHERE event_id='"
        + id + "'";

    Statement statement = null;

    try {
      statement = connection.createStatement();

      ResultSet rs = statement.executeQuery(sql);
      //rs.beforeFirst();

      while (rs.next()) {
        String key = rs.getString(1);
        String value = rs.getString(2);
        event.setProperty(key, value);
      }
    } finally {
      if (statement != null) {
        statement.close();
      }
    }
  }

  /**
   * Retrieve the exception string representation from the
   * logging_event_exception table.
   * 
   * @param connection
   * @param id
   * @param event
   * @throws SQLException
   */
  void getException(Connection connection, long id, LoggingEvent event)
      throws SQLException {
    String sql = "SELECT trace_line FROM logging_event_exception where event_id='"
        + id + "' ORDER by i ASC";

    Statement statement = null;

    try {
      statement = connection.createStatement();

      ResultSet rs = statement.executeQuery(sql);

      Vector v = new Vector();

      while (rs.next()) {
        //int i = rs.getShort(1);
        v.add(rs.getString(1));
      }

      int len = v.size();
      String[] strRep = new String[len];
      for (int i = 0; i < len; i++) {
        strRep[i] = (String) v.get(i);
      }
      // we've filled strRep, we now attach it to the event
      event.setThrowableInformation(new ThrowableInformation(strRep));
    } finally {
      if (statement != null) {
        statement.close();
      }
    }
  }
}