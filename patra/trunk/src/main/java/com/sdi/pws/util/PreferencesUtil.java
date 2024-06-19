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

package com.sdi.pws.util;

import com.sdi.pws.generator.Generator;
import com.sdi.pws.generator.GeneratorImpl;
import com.sdi.pws.preferences.Preferences;
import com.sdi.pws.preferences.PreferencesException;

import java.util.Date;

public class PreferencesUtil
{
    public static Generator getDefaultGenerator(Preferences aPrefs)
    {
        try
        {
            final GeneratorImpl lGen = new GeneratorImpl();
            lGen.setEntropy(new Date().toString().getBytes());
            lGen.setLength(aPrefs.getIntPref(Preferences.PREF_GEN_LENGTH));
            lGen.setMixedCase(aPrefs.getBoolPref(Preferences.PREF_GEN_MIXEDCASE));
            lGen.setNumbersIncluded(aPrefs.getBoolPref(Preferences.PREF_GEN_DIGITS));
            lGen.setPunctuationIncluded(aPrefs.getBoolPref(Preferences.PREF_GEN_PUNCT));
            lGen.setReadable(aPrefs.getBoolPref(Preferences.PREF_GEN_READABLE));
            return lGen;
        }
        catch(PreferencesException eIgnore)
        {
            return null;
        }
    }

    public static void setPrefsFromGenerator(Preferences aPrefs, Generator aGen)
    {

        aPrefs.setPreference(Preferences.PREF_GEN_LENGTH,  "" + aGen.getLength());
        aPrefs.setPreference(Preferences.PREF_GEN_MIXEDCASE, "" + aGen.isMixedCase());
        aPrefs.setPreference(Preferences.PREF_GEN_DIGITS, "" + aGen.isNumbersIncluded());
        aPrefs.setPreference(Preferences.PREF_GEN_PUNCT, "" + aGen.isPunctuationIncluded());
        aPrefs.setPreference(Preferences.PREF_GEN_READABLE, "" + aGen.isReadable());        
    }
}