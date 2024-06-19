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

package com.sdi.pws.gui.dialog.edit;

import com.sdi.pws.db.PwsRecord;
import com.sdi.pws.db.PwsFieldImpl;
import com.sdi.pws.db.ModelException;

import java.util.List;
import java.util.ArrayList;

/**
 * Class that contains utility methods to handle the input from the GUI and to
 * transform it into a real value.
 */
public class EditorUtil
{
    private static final String EMPTY = "";

    /**
     * Change or add the value of a field if it is new or if it changed.
     * We explicitly do nothing if the value is the same.
     * @param aRecord The record for which we want to change a field.
     * @param lOrig The exisiting value (or null).
     * @param lNew The new value (or null).
     * @param aFieldType The field indication.
     */
    static void  handleDifference(PwsRecord aRecord, String lOrig, String lNew, Byte aFieldType)
    {
        if(lOrig == null && lNew != null)
        {
            // Add the new field.
            aRecord.put(new PwsFieldImpl(aFieldType, lNew.getBytes()));
        }
        else if(lOrig != null && lNew == null)
        {
            // Remove the value.
            try{aRecord.remove(aFieldType);}catch(Exception eIgnore){};
        }
        else if(lOrig != null && lNew != null && !lOrig.equals(lNew))
        {
            // Change the existing field value.
            try{aRecord.get(aFieldType).setAsString(lNew);}catch(ModelException eIgnore){};
        }
    }

    /**
     * Trim whitespace and reduce to null if there is no content.
     * The empty string is for displaying and the null is for storing.
     * @param aSource
     * @return The trimmed string or null if it was an empty string.
     */
    static String trimAndReduceToNull(String aSource)
    {
        if(aSource == null) return aSource;
        final String lTrimmed = aSource.trim();

        if("".equals(lTrimmed)) return null;
        else return lTrimmed;
    }

    /**
     * Turn null into the empty string for display.
     * @param aSource The string under investigation.
     * @return The same string or the empty string if null wass passed.
     */
    static String expandNull(String aSource)
    {
        if(aSource == null) return EMPTY;
        else return aSource;
    }
}
