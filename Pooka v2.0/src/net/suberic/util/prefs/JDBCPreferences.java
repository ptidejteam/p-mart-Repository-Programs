package net.suberic.util.prefs;
import java.util.*;
import java.util.prefs.*;
import java.sql.*;

/**
 * A Preference implementation that stores it values in a database via JDBC.
 */
public class JDBCPreferences extends AbstractPreferences {

  // the connection info.
  static final char sSeparator = '/';
  static final String sTableName = "jdbcpreferences";
  static final String sPathColumn = "jpath";
  static final String sKeyColumn = "jkey";
  static final String sValueColumn = "jvalue";

  String mPath;
  Properties mBackingProperties = new Properties();
  boolean mRemoved = false;

  /**
   * Creaes a new JDBCPreferences instance.
   */
  public JDBCPreferences(AbstractPreferences pParent, String pName) {
    super(pParent, pName);
    mBackingProperties = new Properties();
    try {
      syncSpi();
    } catch (BackingStoreException bse) {
      bse.printStackTrace();
    }
  }

  protected String getSpi(String pKey) {
    return mBackingProperties.getProperty(pKey);
  }

  protected void putSpi(String pKey, String pValue) {
    mBackingProperties.setProperty(pKey, pValue);
  }

  protected void removeSpi(String pKey) {
    mBackingProperties.remove(pKey);
  }

  protected void removeNodeSpi() throws BackingStoreException {
    Connection connection=null;
    try {
      connection = getConnection();
      PreparedStatement statement=connection.prepareStatement("delete from " + sTableName + " where " + sPathColumn + " = ?");
      statement.setString(1, absolutePath());
      statement.executeUpdate();

      //connection.commit();

      mBackingProperties.clear();
    } catch (SQLException se) {
      /*
      try {
        connection.rollback();
      } catch (SQLException rollbackEx) {
        // ignore.
      }
      */
      throw new BackingStoreException(se);
    } finally {
      if (connection != null) {
        try {
          connection.close();
        } catch (SQLException se) {
          // ignore.
        }
      }
    }
  }

  protected String[] keysSpi() throws BackingStoreException {
    Connection connection=null;
    try {
      connection = getConnection();
      PreparedStatement statement = connection.prepareStatement("select " + sKeyColumn + " from " + sTableName + " where " + sPathColumn + " = ?");

      statement.setString(1, absolutePath());
      ResultSet resultSet = statement.executeQuery();

      List<String> results = new ArrayList<String>();
      int prefix = absolutePath().length();
      while (resultSet.next()) {
        String keyName = resultSet.getString(sKeyColumn);
        results.add(keyName);
      }

      return results.toArray(new String[0]);

    } catch (SQLException se) {
      throw new BackingStoreException(se);
    } finally {
      if (connection != null) {
        try {
          connection.close();
        } catch (SQLException se) {
          // ignore.
        }
      }
    }
  }

  protected String[] childrenNamesSpi() throws BackingStoreException {
    Connection connection=null;
    try {
      connection = getConnection();
      PreparedStatement statement = connection.prepareStatement("select " + sPathColumn + " from " + sTableName + " where " + sPathColumn + " like ? and " + sPathColumn + " not like ? and not " + sPathColumn + " = ? group by " + sPathColumn);

      statement.setString(1, absolutePath() + '%');
      statement.setString(2, absolutePath() + '%' + sSeparator + '%');
      statement.setString(3, absolutePath());
      ResultSet resultSet = statement.executeQuery();

      List<String> results = new ArrayList<String>();
      int prefix = absolutePath().length();
      while (resultSet.next()) {
        String childName = resultSet.getString(sPathColumn);
        childName = childName.substring(prefix);
        results.add(childName);
      }

      return results.toArray(new String[0]);

    } catch (SQLException se) {
      throw new BackingStoreException(se);
    } finally {
      if (connection != null) {
        try {
          connection.close();
        } catch (SQLException se) {
          // ignore.
        }
      }
    }
  }


  protected AbstractPreferences childSpi(String pName) {
    return new JDBCPreferences(this, pName);
  }

  protected void syncSpi() throws BackingStoreException {
    Connection connection=null;
    try {
      connection = getConnection();
      PreparedStatement statement = connection.prepareStatement("select " + sKeyColumn + ", " + sValueColumn + " from " + sTableName + " where " + sPathColumn + "= ?");

      statement.setString(1, absolutePath());

      ResultSet resultSet = statement.executeQuery();

      Properties newProps = new Properties();
      while (resultSet.next()) {
        String key = resultSet.getString(sKeyColumn);
        String value = resultSet.getString(sValueColumn);
        newProps.setProperty(key, value);
      }

      mBackingProperties = newProps;

    } catch (SQLException se) {
      throw new BackingStoreException(se);
    } finally {
      if (connection != null) {
        try {
          connection.close();
        } catch (SQLException se) {
          // ignore.
        }
      }
    }
  }

  protected void flushSpi() throws BackingStoreException {
    Connection connection=null;
    try {
      connection = getConnection();

      PreparedStatement removeStatement = connection.prepareStatement("delete from " + sTableName + " where " + sPathColumn + "= ?");
      removeStatement.setString(1, absolutePath());
      removeStatement.executeUpdate();

      PreparedStatement addStatement = connection.prepareStatement("insert into " + sTableName + " (" + sPathColumn + ", " + sKeyColumn + ", "+ sValueColumn + ") values (?, ?, ?);");

      Enumeration keyNames = mBackingProperties.propertyNames();
      while (keyNames.hasMoreElements()) {

        String key = (String) keyNames.nextElement();
        String value = mBackingProperties.getProperty(key);

        addStatement.setString(1, absolutePath());
        addStatement.setString(2, key);
        addStatement.setString(3, value);
        addStatement.executeUpdate();

        //connection.commit();
      }
    } catch (SQLException se) {
      /*
      try {
        connection.rollback();
      } catch (SQLException rollbackEx) {
        // ignore.
      }
      */
      throw new BackingStoreException(se);
    } finally {
      if (connection != null) {
        try {
          connection.close();
        } catch (SQLException se) {
          // ignore.
        }
      }
    }
  }

  /**
   * Returns the Connection for this Preferences object.
   */
  private Connection getConnection() throws SQLException {
    try {
      String className = System.getProperty("JDBCPreferences.driverName");
      String url = System.getProperty("JDBCPreferences.url");
      String user = System.getProperty("JDBCPreferences.user");
      String password = System.getProperty("JDBCPreferences.password");
      //System.out.println("className='" + className + "'; url='" + url + "', user=" + user + ", password = " + password);
      Class.forName(className).newInstance();
      Connection returnValue = DriverManager.getConnection(url, user, password);

      return returnValue;
    } catch (ClassNotFoundException cnfe) {
      throw new SQLException(cnfe);
    } catch (InstantiationException ie) {
      throw new SQLException(ie);
    } catch (IllegalAccessException iae) {
      throw new SQLException(iae);
    }
  }

}
