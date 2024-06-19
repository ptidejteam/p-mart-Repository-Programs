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

package com.sdi.pws.gui.compo.db.table;

import com.sdi.pws.codec.Codec;
import com.sdi.pws.db.*;
import com.sdi.pws.gui.compo.db.change.*;

import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.io.File;
import java.util.Iterator;

/** Adapter + decorator for PWS databases.
 * Map internal PWS database to a structure that is suitable for displaying inside a table.
 * Take up the db responsabilities (event firing) in a MVC construction.
 */
public class TableViewDatabase
extends AbstractTableModel
implements PwsDatabase, ChangeViewDatabaseListener
{
    private  ChangeViewDatabase db;

    public TableViewDatabase(ChangeViewDatabase aDb)
    {
        if(aDb == null) throw new IllegalArgumentException("Internal db should not be null.");
        db = aDb;
        db.addChangeViewDatabaseListener(this);
    }

    public void setDatabase(ChangeViewDatabase aDb)
    {
        if(aDb == null) throw new IllegalArgumentException("Internal db should not be null.");
        db.removeChangeViewDatabaseListener(this);
        db = aDb;
        db.addChangeViewDatabaseListener(this);
        this.fireTableDataChanged();
    }

    public void fieldChange(ChangeViewFieldEvent aEvt)
    {
        // Lookup the field and fire a change event.
        final Point lTableLocation = fieldToTable((PwsField) aEvt.getSource());
        // Fire the event.
        if(lTableLocation != null) fireTableCellUpdated(lTableLocation.y, lTableLocation.x);
    }

    public void recordChange(ChangeViewRecordEvent aEvt)
    {
        PwsField lField = aEvt.getField();
        // Lookup the location so we can fire an event later on.
        Point lTableLocation = fieldToTable(lField);
        // Fire the event.
        if(lTableLocation != null) fireTableCellUpdated(lTableLocation.y, lTableLocation.x);
    }

    public void dbChange(ChangeViewDatabaseEvent aEvt)
    {
        if(aEvt.getOp() == ChangeViewDatabaseEvent.INSERT)
            fireTableRowsInserted(aEvt.getIndex(), aEvt.getIndex());
        else if(aEvt.getOp() == ChangeViewDatabaseEvent.DELETE)
            fireTableRowsDeleted(aEvt.getIndex(), aEvt.getIndex());
    }

    // Mapping logic.
    //////////////////////////////////////////////////////////////////////////////////////

    /**
     * Map an internal field to table coordinates.
     * @param aField This is the internal field as it is received in the events. It should not be
     *               a ChangeViewField. It is an internal field because these can be uniquely identified
     *               whereas the ChangeViewFields can be different wrappers for the same internal field.
     */
    Point fieldToTable(PwsField aField)
    {
        for(int i=0; i < db.getNrRecords(); i++)
        {
            try
            {
                final ChangeViewRecord lRec = (ChangeViewRecord) db.getRecord(i);

                if(lRec.hasType(PwsField.FIELD_TITLE) && ((ChangeViewField) lRec.get(PwsField.FIELD_TITLE)).getInternal() == aField) return new Point(0,i);
                else if(lRec.hasType(PwsField.FIELD_UID) && ((ChangeViewField) lRec.get(PwsField.FIELD_UID)).getInternal() == aField) return new Point(1,i);
                else if(lRec.hasType(PwsField.FIELD_TITLE) && ((ChangeViewField) lRec.get(PwsField.FIELD_NOTES)).getInternal() == aField) return new Point(2,i);
            }
            catch(ModelException e)
            {
                return new Point(-1, -1);
            }
        }
        return new Point(0,0);
    }

    /**
     *
     * @param aPoint
     * @return Returns a ChangeViewField, so it is the "outer" version.
     */
    PwsField tableToField(Point aPoint)
    {
        // Mapping logic should go in here.
        try
        {
            final PwsRecord lRecord = db.getRecord(aPoint.y);
            switch(aPoint.x)
            {
                case 0:
                    if(lRecord.hasType(PwsField.FIELD_TITLE)) return lRecord.get(PwsField.FIELD_TITLE);
                    else return null;
                case 1:
                    if(lRecord.hasType(PwsField.FIELD_UID)) return lRecord.get(PwsField.FIELD_UID);
                    else return null;
                case 2:
                    if(lRecord.hasType(PwsField.FIELD_NOTES)) return lRecord.get(PwsField.FIELD_NOTES);
                    else return null;
            }
        }
        catch(ModelException eIgnore){;}
        return null;
    }

    // TabelModel implementation.
    //////////////////////////////////////////////////////////////////////////////////////

    public int getRowCount()
    {
       return db.getNrRecords();
    }

    public int getColumnCount()
    {
        return 3;
    }

    public String getColumnName(int column)
    {
        switch(column)
        {
            case 0: return "Title";
            case 1: return "Uid";
            case 2: return "Notes";
            default: return "";
        }
    }

    public Object getValueAt(int rowIndex, int columnIndex)
    {
        final PwsField lField = tableToField(new Point(columnIndex, rowIndex));
        if(lField != null) try{return lField;}catch(Exception eIgnore){return "";}
        else return "";
    }

    // PwsDatabase delegation.
    //////////////////////////////////////////////////////////////////////////////////////

    public int getNrRecords()
    {
        return db.getNrRecords();
    }

    public PwsRecord getRecord(int aIndex)
    {
        return db.getRecord(aIndex);
    }

    public void add(PwsRecord aRecord)
    {
        db.add(aRecord);
    }

    public void remove(int aIndex)
    {
        db.remove(aIndex);
    }

    public Iterator iterator()
    {
        return db.iterator();
    }

    public String getVersion()
    {
        return db.getVersion();
    }

    public String getPassphrase()
    {
        return db.getPassphrase();
    }

    public void setPassphrase(String passphrase)
    {
        db.setPassphrase(passphrase);
    }

    public Codec getCodec()
    {
        return db.getCodec();
    }

    public void setCodec(Codec codec)
    {
        db.setCodec(codec);
    }

    public File getFile()
    {
        return db.getFile();
    }

    public void setFile(File file)
    {
        db.setFile(file);
    }

    public String getParameters()
    {
        return db.getParameters();
    }

    public void setParameters(String parameters)
    {
        db.setParameters(parameters);
    }

    public boolean isChanged()
    {
        return db.isChanged();
    }

    public void setChanged(boolean aChanged)
    {
        db.setChanged(aChanged);
    }
}
