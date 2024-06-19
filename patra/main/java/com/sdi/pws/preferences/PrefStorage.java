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

import java.util.prefs.Preferences;
import java.util.prefs.BackingStoreException;
import java.util.Iterator;

public class PrefStorage
{
    private static final String PREFS_BASE = "com/sdi/pws/1.0";

    public static void savePreferences(com.sdi.pws.preferences.Preferences aPrefs)
    {
        final Preferences lUserPrefs = Preferences.userRoot();
        final Preferences lAppPrefs = lUserPrefs.node(PREFS_BASE);
        final Iterator lIter = aPrefs.iterator();
        while(lIter.hasNext())
        {
            final String lKey = (String) lIter.next();
            final String lVal = aPrefs.getPref(lKey);
            lAppPrefs.put(lKey, lVal);
        }
    }

    public static void loadPreferences(com.sdi.pws.preferences.Preferences aPrefs)
    {
        try
        {
            final Preferences lUserPrefs = Preferences.userRoot();
            final Preferences lAppPrefs = lUserPrefs.node(PREFS_BASE);
            final String[] lKeys = lAppPrefs.keys();
            for(int i = 0; i < lKeys.length; i++)
            {
                final String lStoredPref = lAppPrefs.get(lKeys[i], "<UNKNOWN>");
                if(!"<UNKNOWN>".equals(lStoredPref))
                    aPrefs.setPreference(lKeys[i], lStoredPref);
            }
        }
        catch(BackingStoreException e){}
    }
}
