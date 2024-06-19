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

package com.sdi.pws.gui.action;

import com.sdi.pws.gui.RecordSelector;
import com.sdi.pws.gui.GuiUtil;
import com.sdi.pws.gui.compo.db.change.ChangeViewRecord;
import com.sdi.pws.db.PwsDatabase;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Iterator;

public class EntryDelete
extends AbstractAction
{
    private PwsDatabase db;
    private JComponent application;
    private RecordSelector selector;

    public EntryDelete(JComponent aAppFrame, PwsDatabase aDb, RecordSelector aSelector)
    {
        super(GuiUtil.getText("action.entrydelete"));
        db = aDb;
        application = aAppFrame;
        selector = aSelector;

        // Install the icon.
        final ImageIcon lUIDIcon = new ImageIcon(EntryDelete.class.getClassLoader().getResource("assets/delete.png"));
        putValue(SMALL_ICON,lUIDIcon);

        // Decide if the action is enabled or not right now.
        setEnabled(selector.isInfoAvailable());

        // Add dynamic change of being enabled.
        selector.addPropertyChangeListener(new PropertyChangeListener(){
            public void propertyChange(PropertyChangeEvent evt)
            {
                EntryDelete.this.setEnabled(selector.isInfoAvailable());
            }
        });
    }

    public void actionPerformed(ActionEvent e)
    {
        if(selector.isInfoAvailable())
        {
            ChangeViewRecord lRecord = (ChangeViewRecord) selector.getSelectedRecord();
            final Iterator lIter = db.iterator();
            int lRecIndex = -1;
            int i = 0;
            while(lIter.hasNext())
            {
                final ChangeViewRecord lRec = (ChangeViewRecord) lIter.next();
                if(lRec.getInternal() == lRecord.getInternal())
                {
                    lRecIndex = i;
                    break;
                }
                i++;
            }

            if(lRecIndex >=0)
            {   final int lAnswer = JOptionPane.showConfirmDialog(application, GuiUtil.getText("action.entrydelete.confirm"), GuiUtil.getText("action.entrydelete"), JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
                if(lAnswer == JOptionPane.YES_OPTION) db.remove(lRecIndex);
            }
        }
    }
}