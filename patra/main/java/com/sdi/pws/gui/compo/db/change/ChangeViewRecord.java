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

import com.sdi.pws.db.PwsRecord;
import com.sdi.pws.db.PwsField;
import com.sdi.pws.db.ModelException;

import java.util.Iterator;

public class ChangeViewRecord
implements PwsRecord, Comparable
{
    private ChangeViewDatabase db;
    private PwsRecord record;

    public ChangeViewRecord(ChangeViewDatabase aDb, PwsRecord aRecord)
    {
        db = aDb;
        record = aRecord;
    }

    public PwsRecord getInternal()
    {
        return record;
    }

    // Delegate.

    public PwsField get(Byte aFieldType)
    throws ModelException
    {
        return new ChangeViewField(db, this, record.get(aFieldType));
    }

    public void put(PwsField aField)
    {
        // First we modify the record by adding the field.
        // This is the obvious thing to do.
        final boolean lChange = (record.hasType(aField.getType()));
        record.put(aField);
        final ChangeViewRecordEvent lEvent = new ChangeViewRecordEvent(db, record, aField, lChange?ChangeViewRecordEvent.MODIFY:ChangeViewRecordEvent.INSERT);
        db.fireChangeRecordEvent(lEvent);

        // Secondly we also fire a field event.
        // This is less obvious. The reason is that some models might be interested in
        // the value of new/modified fields if the structure is dependent on it.
        // E.g. the tree view is dependent on the group field. If we add a group field to a record
        // And there was no group field before, we are intersted in the group field in order
        // to calculate the structure of the tree.
        try
        {
            // Re-query to be sure to get the wrapper and not the internal one.
            final ChangeViewField lExternalField = (ChangeViewField) this.get(aField.getType());
            db.fireChangeFieldEvent(new ChangeViewFieldEvent(db, lExternalField.getInternal(), lExternalField.getType(), lExternalField.getValue()));
        }
        catch(ModelException eIgnore){;}

        // Finally we mark the database as being changed.
        db.setChanged(true);
    }

    public void remove(Byte aFieldType)
    throws ModelException
    {
        Object lOldValue = null;
        if(record.hasType(aFieldType)) lOldValue = record.get(aFieldType);

        record.remove(aFieldType);

        final ChangeViewRecordEvent lEvent = new ChangeViewRecordEvent(db, this, (PwsField) lOldValue, ChangeViewRecordEvent.DELETE);
        db.fireChangeRecordEvent(lEvent);
        db.setChanged(true);
    }

    public boolean hasType(Byte aFieldType)
    {
        return record.hasType(aFieldType);
    }

    public Iterator typeIterator()
    {
        return record.typeIterator();
    }

    public String toString()
    {
        String lPrintName = "";
        if(this.hasType(PwsField.FIELD_TITLE))
        {
            try
            {
                lPrintName = this.get(PwsField.FIELD_TITLE).getAsString();
                if(this.hasType(PwsField.FIELD_UID))
                {
                    lPrintName += " - " + this.get(PwsField.FIELD_UID).getAsString();
                }
            }
            catch(ModelException eIgnore){;}
        }
        return lPrintName;
    }

    public String toString2()
    {
        final String lPrintName = this.toString();
        String lGroupName = "default";
        if(this.hasType(PwsField.FIELD_GROUP))
        {
            try
            {
                lGroupName = this.get(PwsField.FIELD_GROUP).getAsString();
            }
            catch(ModelException eIgnore){;}
        }
        return lGroupName + " - "  +lPrintName;
    }

    public int compareTo(Object o)
    {
        return (String.CASE_INSENSITIVE_ORDER.compare(this.toString2(), ((ChangeViewRecord)o).toString2()));
    }
}
