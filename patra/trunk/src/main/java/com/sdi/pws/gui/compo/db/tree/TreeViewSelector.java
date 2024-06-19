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

package com.sdi.pws.gui.compo.db.tree;

import com.sdi.pws.gui.RecordSelector;
import com.sdi.pws.gui.compo.db.change.ChangeViewRecord;
import com.sdi.pws.db.PwsRecord;

import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

public class TreeViewSelector
implements RecordSelector, TreeSelectionListener
{
    // Implementation Notes.
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // A tree node can contain a user object. We store references to LeafNode objects which contain references
    // to the PwsDatabase objects (records). This is easy for us, we do not have to map the nodes to records
    // ourselves, the mapping is kept in memory in the tree structure itself.
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private PropertyChangeSupport support = new PropertyChangeSupport(this);
    private PwsRecord record;

    public TreeViewSelector(JTree aTree)
    {
        aTree.addTreeSelectionListener(this);
    }

    public boolean isInfoAvailable()
    {
        return !(record == null);
    }

    public PwsRecord getSelectedRecord()
    {
        return record;
    }

    public int getSelectedIndex()
    {
        return -1;
    }

    // Property change support.

    public void addPropertyChangeListener(PropertyChangeListener listener)
    {
        support.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener)
    {
        support.removePropertyChangeListener(listener);
    }

    // Selection Listener.

    public void valueChanged(TreeSelectionEvent aEvt)
    {
        final TreePath lPath = aEvt.getPath();
        final DefaultMutableTreeNode lNode = (DefaultMutableTreeNode) lPath.getLastPathComponent();

        // Only act when a record node is selected. Ignore other parts of the tree.
        if(lNode.getUserObject() instanceof TreeViewDatabase.LeafNode )
        {
            TreeViewDatabase.LeafNode lLeaf = (TreeViewDatabase.LeafNode) lNode.getUserObject();
            ChangeViewRecord lRec = lLeaf.getRecord();

            final Object lOldValue = record;
            record = lRec;
            final Object lNewValue = lRec;
            support.firePropertyChange("selectedRecord", lOldValue, lNewValue);
        }
        else
        {
            // Ignore node for the time being.
            final Object lOldValue = record;
            record = null;
            support.firePropertyChange("selectedRecord", lOldValue, null);
        }
    }
}
