/*
Password Tracker (PATRA). An application to safely store your passwords.
Copyright (C) 2006  Bruno Ranschaert, S.D.I.-Consulting BVBA.

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
import java.util.Map;
import java.util.HashMap;

public class PreferencesImpl
implements Preferences
{
    private Map prefs = new HashMap();

    public PreferencesImpl()
    {
        setPreference(PREF_DEFAULT_DB, "password database");
        setPreference(PREF_DEFAULT_VIEW, VIEW_TABLE);
        setPreference(PREF_ACTIVEDEFAULTUID, FALSE);
        setPreference(PREF_DEFAULT_UID, "default uid");
        setPreference(PREF_ACTIVEDEFAULTGROUP, FALSE);
        setPreference(PREF_DEFAULT_GROUP, "default group");          
        setPreference(PREF_CONVERT_MODE, CONVERT_NONE);
        setPreference(PREF_BUTTONS_LEFT, TRUE);
        setPreference(PREF_BUTTONS_RIGHT, TRUE);
        setPreference(PREF_VISIBLE_PWD, FALSE);
        setPreference(PREF_SUGGEST_DEFAULT_DB, TRUE);
        setPreference(PREF_NR_BACKUPS, "5");
        setPreference(PREF_ADD_EXTENSION, TRUE);
        setPreference(PREF_WARN_ON_EXPORT, TRUE);

        setPreference(PREF_GEN_LENGTH, "8");
        setPreference(PREF_GEN_READABLE, TRUE);
        setPreference(PREF_GEN_MIXEDCASE, TRUE);
        setPreference(PREF_GEN_PUNCT, FALSE);
        setPreference(PREF_GEN_DIGITS, TRUE);      
    }

    public boolean hasPreference(String aName)
    {
        return prefs.containsKey(aName);
    }

    public Iterator iterator()
    {
        return prefs.keySet().iterator();
    }

    public String getPref(String aName)
    {
        if(hasPreference(aName)) return (String) prefs.get(aName);
        else return null;
    }

    public boolean getBoolPref(String aName) throws PreferencesException
    {
        final String lRepr = getPref(aName);
        return Boolean.valueOf(lRepr).booleanValue();
    }

    public int getIntPref(String aName) throws PreferencesException
    {
        final String lRepr = getPref(aName);
        try
        {
            return Integer.parseInt(lRepr);
        }
        catch(NumberFormatException e)
        {
            throw new PreferencesException("Preference is not an integer: " + lRepr);
        }
    }

    public void setPreference(String aName, String aValue)
    {
        prefs.put(aName, aValue);
    }
}