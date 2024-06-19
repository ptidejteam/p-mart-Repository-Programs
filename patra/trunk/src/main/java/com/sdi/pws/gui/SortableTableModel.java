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

import javax.swing.table.*;
import javax.swing.event.*;
import java.util.*;

public class SortableTableModel
implements TableModel, TableModelListener
{
    EventListenerList listenerList = new EventListenerList();
    TableModel delegatedModel;
    int[] sortedIndicies;
    int sortColumn;
    Comparator comparator;
    Comparator[] comparators;

    public SortableTableModel(TableModel tm)
    {
        delegatedModel = tm;
        delegatedModel.addTableModelListener(this);
        comparators = new Comparator [tm.getColumnCount()];
        sortedIndicies = new int [0];
        setSortColumn(0);
    }

    // listener stuff
    public void addTableModelListener(TableModelListener l)
    {
        listenerList.add(TableModelListener.class, l);
    }

    public void removeTableModelListener(TableModelListener l)
    {
        listenerList.remove(TableModelListener.class, l);
    }

    public void fireTableModelEvent(TableModelEvent e)
    {
        Object[] listeners = listenerList.getListenerList();
        for (int i = listeners.length - 2; i >= 0; i -= 2)
        {
            if (listeners[i] == TableModelListener.class)
            {
                ((TableModelListener) listeners[i + 1]).tableChanged(e);
            }
        }
    }

    // contents stuff

    public Class getColumnClass(int columnIndex)
    {
        if (delegatedModel.getRowCount() > 0)
            return delegatedModel.getValueAt(0, columnIndex).getClass();
        else
            return Object.class;
    }

    public int getColumnCount()
    {
        return delegatedModel.getColumnCount();
    }

    public String getColumnName(int index)
    {
        return delegatedModel.getColumnName(index);
    }

    public int getRowCount()
    {
        return delegatedModel.getRowCount();
    }

    private int getDelegatedRow(int row)
    {
        return sortedIndicies[row];
    }

    public Object getValueAt(int rowIndex, int columnIndex)
    {
        return delegatedModel.getValueAt(getDelegatedRow(rowIndex),
                columnIndex);
    }

    public boolean isCellEditable(int rowIndex, int columnIndex)
    {
        return delegatedModel.isCellEditable(rowIndex, columnIndex);
    }

    public void setValueAt(Object aValue, int rowIndex, int columnIndex)
    {
        delegatedModel.setValueAt(aValue, rowIndex, columnIndex);
    }

    // internal helpers
    public void setComparatorForColumn(Comparator c, int i)
    {
        // range check
        if (i > comparators.length)
        {
            Comparator[] newComparators = new Comparator[i + 1];
            System.arraycopy(comparators, 0,
                    newComparators, 0,
                    comparators.length);
            comparators = newComparators;
        }
        // add the comparator
        comparators[i] = c;
    }

    public void setSortColumn(int i)
    {
        sortColumn = i;

        // reset current comparator, possibly to null, which
        // will make us use "natural ordering" for those values
        comparator = null;
        if ((comparators != null) && (comparators.length > 0))
            // is there one in the list of comparators?
            comparator = comparators[sortColumn];

        // now do the sort
        resort();
    }

    public int getSortColumn()
    {
        return sortColumn;
    }


    /**
     * called to rebuild the delegate-to-sortable mapping
     */
    protected void resort()
    {
        // does sortedIndicies need to grow or shrink?
        if (sortedIndicies.length != delegatedModel.getRowCount())
        {
            sortedIndicies = new int [delegatedModel.getRowCount()];
        }
        // build up a list of SortingDelegates
        ArrayList sortMe = new ArrayList();
        for (int i = 0; i < delegatedModel.getRowCount(); i++)
        {
            SortingDelegate sd =
                    new SortingDelegate(delegatedModel.getValueAt(i, getSortColumn()),
                            i);
            sortMe.add(sd);
        }
        // now sort him with the SortingDelegateComparator
        SortingDelegateComparator sdc =
                new SortingDelegateComparator(comparator);
        Collections.sort(sortMe, sdc);

        // fill sortedIndicies array
        // index -> value represents mapping from original
        // row to sorted row
        for (int i = 0; i < sortMe.size(); i++)
        {
            sortedIndicies[i] =
                    ((SortingDelegate) sortMe.get(i)).row;
        }

        // fire change event
        fireAllChanged();
    }


    public class SortingDelegate extends Object
    {
        public Object value;
        public int row;

        public SortingDelegate(Object v, int r)
        {
            value = v;
            row = r;
        }
    }

    // "pointer object" int 1 is current value and int 2 is
    // current row in table

    // comparator which applies current comparator's compare rule
    // to value 2 in
    class SortingDelegateComparator extends Object implements Comparator
    {
        Comparator comp;

        public SortingDelegateComparator(Comparator c)
        {
            comp = c;
        }

        public int compare(Object o1, Object o2)
        {
            Object v1 = ((SortingDelegate) o1).value;
            Object v2 = ((SortingDelegate) o2).value;
            if (comp != null)
                return comp.compare(v1, v2);
            else if (v1 instanceof Comparable)
                return ((Comparable) v1).compareTo(v2);
            else
                throw new IllegalArgumentException("Can't compare objects for sorting");

        }
    }


    public void tableChanged(TableModelEvent e)
    {
        switch (e.getType())
        {
            case TableModelEvent.DELETE:
            {
                resort();
                fireAllChanged();
                break;
            }
            case TableModelEvent.INSERT:
            {
                resort();
                fireAllChanged();
                break;
            }
            case TableModelEvent.UPDATE:
            {
                resort();
                fireAllChanged();
                break;
            }

        }
    }

    protected void fireAllChanged()
    {
        TableModelEvent e = new TableModelEvent(this);
        fireTableModelEvent(e);
    }


}

