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

package com.sdi.pws.gui;

import java.util.*;

import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.*;

public class FilteredTableModel
extends AbstractTableModel
{
    private TableModel decorated;
    private TableModelListener tableModelListener;
    private List<Integer> rowToModelIndex = new LinkedList<Integer>();
    private String searchString = null;

    public FilteredTableModel(TableModel aTableModel)
    {
        tableModelListener = new TableModelHandler();
        setTableModel(aTableModel);
        aTableModel.addTableModelListener(tableModelListener);
        reindex();
    }

    public TableModel getTableModel()
    {
        return decorated;
    }

    public void setTableModel(TableModel aTableModel)
    {
        //remove listeners if there...
        if (this.decorated != null)
        {
            this.decorated.removeTableModelListener(tableModelListener);
        }

        this.decorated = aTableModel;
        if (this.decorated != null)
        {
            this.decorated.addTableModelListener(tableModelListener);
        }
        reindex();
        fireTableStructureChanged();
    }

    private void reindex()
    {
        rowToModelIndex.clear();
        if (searchString == null || searchString.equals(""))
        {
            // Create an identity mapping.
            for (int t = 0; t < decorated.getRowCount(); t++)
            {
                rowToModelIndex.add(t);
            }
        }
        else
        {
            // Filter out the rows containing the search string.
            final String lRealSearch = searchString.toLowerCase();
            for (int lRowIndex = 0; lRowIndex < decorated.getRowCount(); lRowIndex++)
            {
                for (int column = 0; column < decorated.getColumnCount(); column++)
                {
                    final String lValue = decorated.getValueAt(lRowIndex, column).toString();
                    String columnValue = lValue.toLowerCase();
                    if (columnValue.indexOf(lRealSearch) >= 0)
                    {
                        rowToModelIndex.add(lRowIndex);
                        break;
                    }
                }
            }
        }
    }

    public void search(String aSearchString)
    {
        if(aSearchString != null && !aSearchString.equals(searchString))
        {
            searchString  = aSearchString;
            reindex();
            fireTableDataChanged();
        }
    }

    private int getModelRow(int aRow)
    {
        return rowToModelIndex.get(aRow);
    }

    public int getRowCount()
    {
        return (decorated == null) ? 0 : rowToModelIndex.size();
    }

    public int getColumnCount()
    {
        return (decorated == null) ? 0 : decorated.getColumnCount();
    }

    public String getColumnName(int aColumn)
    {
        return decorated.getColumnName(aColumn);
    }

    public Class getColumnClass(int aColumn)
    {
        return decorated.getColumnClass(aColumn);
    }

    public boolean isCellEditable(int aRow, int aCol)
    {
        return decorated.isCellEditable(getModelRow(aRow), aCol);
    }

    public Object getValueAt(int aRow, int aCol)
    {
        return decorated.getValueAt(getModelRow(aRow), aCol);
        //return toHtml(decorated.getValueAt(getModelRow(aRow), aCol).toString(), searchString);
    }

    public void setValueAt(Object aValue, int aRow, int aCol)
    {
        decorated.setValueAt(aValue, getModelRow(aRow), aCol);
    }

    private boolean isSearching()
    {
        return searchString != null;
    }

    private class TableModelHandler
    implements TableModelListener
    {
        public void tableChanged(TableModelEvent e)
        {
            if (!isSearching())
            {
                reindex();
                fireTableChanged(e);
                return;
            }

            reindex();
            search(searchString);
            fireTableDataChanged();
        }
    }
}
