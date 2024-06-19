package net.suberic.util;
import java.sql.*;
import java.util.*;
import java.io.*;

/**
 * VariableBundle is a combination of a Properties object, a ResourceBundle
 * object, and (optionally) a second Properties object to act as the 'parent'
 * properties.  This allows both for a single point of reference for
 * variables, as well as the ability to do hierarchical lookups with the
 * parent (see getProperty() for an example).
 *
 * The order of lookup is as follows:  Local properties are checked first,
 * then parent properties, and then finally (if the value is not found in
 * any properties) the ResourceBundle is checked.
 */

public class JDBCVariableBundle extends VariableBundle {
  private String mConnectionString;

  public JDBCVariableBundle(String jdbcConnectionString, VariableBundle newParentProperties) throws SQLException {
    mConnectionString = jdbcConnectionString;
    Connection connection = openConnection();

    configure(connection, newParentProperties);
  }

  /**
   * Configures the VariableBundle.
   */
  protected void configure(Connection pConnection, VariableBundle newParentProperties) throws SQLException {

    writableProperties = new Properties();

    resources=null;

    properties = loadProperties(pConnection);

    parentProperties = newParentProperties;
  }

  /**
   * Opens the connection.
   */
  public Connection openConnection() throws SQLException {
    return DriverManager.getConnection(mConnectionString);
  }

  /**
   * Loads the properties from the given DB.
   */
  public Properties loadProperties(Connection pConnection) {
    Properties returnValue = new Properties();

    return returnValue;
  }

  /**
   * Saves the current properties in the VariableBundle to a file.  Note
   * that this only saves the writableProperties of this particular
   * VariableBundle--underlying defaults are not written.
   */
  public void saveProperties() {
    synchronized(this) {
      if (writableProperties.size() > 0) {
        String key = null;
        if (!propertyIsRemoved(key)) {
          if (writableProperties.getProperty(key, "").equals("")) {

            //writeProperty(key, writableProperties.getProperty(key, ""));
          } else {
            properties.setProperty(key, writableProperties.getProperty(key, ""));
            writableProperties.remove(key);
          }
        } else {
          properties.remove(key);
        }
      } else {
      }
      // write out the rest of the writableProperties

      Set<String> propsLeft = writableProperties.stringPropertyNames();
      List<String> propsLeftList = new ArrayList<String>(propsLeft);
      Collections.sort(propsLeftList);
      for (String nextKey: propsLeftList) {
        //writeSaveFile.write(nextKeyEscaped + "=" + nextValueEscaped);
        properties.setProperty(nextKey, writableProperties.getProperty(nextKey, ""));
        writableProperties.remove(nextKey);
      }

      clearRemoveList();

    }
  }

}


