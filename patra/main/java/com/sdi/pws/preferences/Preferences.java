/*
Password Tracker (PATRA). An application to safely store your passwords.
Copyright (C) 2006-2009  Bruno Ranschaert, S.D.I.-Consulting BVBA.

For more information contact: nospam@sdi-consulting.com
Visit our website: http://www.sdi-consulting.com

This program is free software; you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation; either version 2 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
*/

package com.sdi.pws.preferences;

import java.util.Iterator;

public interface Preferences
{
    // Preference constants.
    ////////////////////////////////////////////////////////////////////////////

    // General preferences.
    static final String PREF_DEFAULT_DB = "PREF_DEFAULT_DB";
    static final String PREF_DEFAULT_VIEW = "PREF_DEFAULT_VIEW";
    static final String PREF_ACTIVEDEFAULTUID = "PREF_ACTIVEDEFAULTUID";
    static final String PREF_DEFAULT_UID = "PREF_DEFAULT_UID";
    static final String PREF_ACTIVEDEFAULTGROUP = "PREF_ACTIVEDEFAULTGROUP";
    static final String PREF_DEFAULT_GROUP = "PREF_DEFAULT_GROUP";
    static final String PREF_CONVERT_MODE = "PREF_CONVERT_MODE";
    static final String PREF_BUTTONS_LEFT = "PREF_BUTTONS_LEFT";
    static final String PREF_BUTTONS_RIGHT = "PREF_BUTTONS_RIGHT";
    static final String PREF_VISIBLE_PWD = "PREF_VISIBLE_PWD";
    static final String PREF_SUGGEST_DEFAULT_DB = "PREF_SUGGEST_DEFAULT_DB";
    static final String PREF_NR_BACKUPS = "PREF_NR_BACKUPS";
    static final String PREF_ADD_EXTENSION = "PREF_ADD_EXTENSION";
    static final String PREF_WARN_ON_EXPORT = "PREF_WARN_ON_EXPORT";
    static final String PREF_STAY_ON_TOP = "PREF_STAY_ON_TOP";

    // Generator preferences.
    static final String PREF_GEN_LENGTH = "PREF_GEN_LENGTH";
    static final String PREF_GEN_READABLE = "PREF_GEN_READABLE";
    static final String PREF_GEN_MIXEDCASE = "PREF_GEN_MIXEDCASE";
    static final String PREF_GEN_PUNCT = "PREF_GEN_PUNCT";
    static final String PREF_GEN_DIGITS = "PREF_GEN_DIGITS";

    // Value constants.
    ////////////////////////////////////////////////////////////////////////////

    public static final String VIEW_TREE = "tree";
    public static final String VIEW_TABLE = "table";

    public static final String CONVERT_NONE = "none";
    public static final String CONVERT_AUTO = "automatically";
    public static final String CONVERT_ASK =  "askforit";

    public static final String TRUE = "true";
    public static final String FALSE = "false";

    boolean hasPreference(String aName);
    Iterator iterator();

    String getPref(String aName);
    boolean getBoolPref(String aName) throws PreferencesException;
    int getIntPref(String aName) throws PreferencesException;

    void setPreference(String aName, String aValue);
}
