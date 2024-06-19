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

package com.sdi.pws.gui.compo.db.change;

import com.sdi.pws.db.PwsField;
import com.sdi.pws.db.ModelException;

public class ChangeViewField
implements PwsField, Comparable
{
    private PwsField field;
    private ChangeViewRecord record;
    private ChangeViewDatabase db;

    public ChangeViewField(ChangeViewDatabase aDb, ChangeViewRecord aRec, PwsField afield)
    {
        this.field = afield;
        this.db = aDb;
        this.record = aRec;
    }

    public PwsField getInternal()
    {
        return field;
    }

    // Delegate.

    public Byte getType()
    {
        return field.getType();
    }

    public void setType(Byte aType)
    {
        field.setType(aType);
        final ChangeViewFieldEvent lEvent = new ChangeViewFieldEvent(db, field, aType, null);
        db.fireChangeFieldEvent(lEvent);
        db.setChanged(true);
    }

    public byte[] getValue()
    {
        return field.getValue();
    }

    public void setValue(byte[] value)
    {
        field.setValue(value);
        final ChangeViewFieldEvent lEvent = new ChangeViewFieldEvent(db, field, null, value);
        db.fireChangeFieldEvent(lEvent);
        db.setChanged(true);
    }

    public String getAsString()
    throws ModelException
    {
        return field.getAsString();
    }

    public void setAsString(String aValue)
    {
        // Use internal field for conversion logic.
        field.setAsString(aValue);
        // Force triggering events.
        this.setValue(field.getValue());
    }

    public String toString()
    {
        try
        {
            return getAsString();
        }
        catch(ModelException e)
        {
            return "";
        }
    }

    public ChangeViewRecord getRecord()
    {
        return record;
    }

    public int compareTo(Object o)
    {
        return String.CASE_INSENSITIVE_ORDER.compare(toString(),(o.toString()));
    }
}
