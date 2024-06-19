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

import com.sdi.pws.gui.RecordSelector;
import com.sdi.pws.gui.compo.db.change.ChangeViewField;
import com.sdi.pws.db.PwsRecord;

import javax.swing.*;
import javax.swing.table.TableModel;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.ListSelectionEvent;
import java.beans.PropertyChangeSupport;
import java.beans.PropertyChangeListener;

/** Keeps track of the current selected password entry in the table view.
 *
 */
public class TableViewSelector
implements RecordSelector, ListSelectionListener
{
    // Implementation Notes.
    /////////////////////////////////////////////////////////////////////////////////////////////////////
    // We have to keep a reference to the database. The tabel events contain inidces, we need
    // the db. to lay our hands on the data involved.
    /////////////////////////////////////////////////////////////////////////////////////////////////////

    private PwsRecord selectedRecord;
    private TableModel db;
    private PropertyChangeSupport support = new PropertyChangeSupport(this);

    public TableViewSelector(TableModel aDb, JTable aTable)
    {
        if(aDb == null) throw new IllegalArgumentException("Db should not be null.");
        if(aTable == null) throw new IllegalArgumentException("JTable should not be null.");

        db = aDb;
        selectedRecord = null;
        aTable.getSelectionModel().addListSelectionListener(this);
    }

    // RecordSelector.
    /////////////////////////////////////////////////////////////////////////////////////////////////////

    public boolean isInfoAvailable()
    {
        return selectedRecord != null;
    }

    public PwsRecord getSelectedRecord()
    {
        return selectedRecord;
    }

    // List selection listener.
    ///////////////////////////////////////////////////////////////////////////////////////////////////////

    public void valueChanged(ListSelectionEvent e)
    {
        //Ignore extra messages.
        if (e.getValueIsAdjusting()) return;

        final PwsRecord lOldValue = selectedRecord;

        final ListSelectionModel lSelectionModel = (ListSelectionModel)e.getSource();
        if (lSelectionModel.isSelectionEmpty())
        {
            selectedRecord = null;
        }
        else
        {
            int index = lSelectionModel.getMinSelectionIndex();
            Object lField = db.getValueAt(index, 0);
            if(lField instanceof ChangeViewField)
            {
                selectedRecord = ((ChangeViewField) lField).getRecord();
            }
        }

        final PwsRecord lNewValue = selectedRecord;
        support.firePropertyChange("selectedRecord", lOldValue, lNewValue);
    }

    // Property change support.
    ///////////////////////////////////////////////////////////////////////////////////////////////////////

    public void addPropertyChangeListener(PropertyChangeListener aListener)
    {
        support.addPropertyChangeListener(aListener);
    }

    public void removePropertyChangeListener(PropertyChangeListener aListener)
    {
        support.removePropertyChangeListener(aListener);
    }
}
