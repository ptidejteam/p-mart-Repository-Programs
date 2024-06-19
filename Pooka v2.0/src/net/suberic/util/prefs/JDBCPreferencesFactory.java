package net.suberic.util.prefs;
import java.util.prefs.*;

/**
 * A PreferenceFactory that stores it values in a database via JDBC.
 */
public class JDBCPreferencesFactory implements PreferencesFactory {
  // we're going to assume that we only have one system table and one user
  // table for now.
  public static Preferences mSystemPrefs;
  public static Preferences mUserPrefs;

  public synchronized Preferences systemRoot() {
    if (mSystemPrefs == null) {
      mSystemPrefs = new JDBCPreferences(null, "/");
    }

    return mSystemPrefs;
  }

  public synchronized Preferences userRoot() {
    if (mUserPrefs == null) {
      mUserPrefs = new JDBCPreferences(null, "");
    }

    return mUserPrefs;
  }
}
