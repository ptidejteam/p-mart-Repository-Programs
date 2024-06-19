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
import com.sdi.pws.db.PwsDatabase;

import java.util.EventObject;

public class ChangeViewDatabaseEvent
extends EventObject
{
    public static final int INSERT = 0;
    public static final int DELETE = 1;
    public static final int MODIFY = 2;

    private PwsRecord record;
    private int index;
    private int op;

    public ChangeViewDatabaseEvent(PwsDatabase aSource, PwsRecord aRecord, int aIndex, int aOp)
    {
        super(aSource);
        record = aRecord;
        index = aIndex;
        op = aOp;
    }

    public PwsRecord getRecord()
    {
        return record;
    }

    public int getIndex()
    {
        return index;
    }

    public int getOp()
    {
        return op;
    }
}
