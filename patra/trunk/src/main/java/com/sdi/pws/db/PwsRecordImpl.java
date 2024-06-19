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

package com.sdi.pws.db;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.SortedSet;
import java.util.TreeSet;

public class PwsRecordImpl
implements PwsRecord
{
    private Hashtable<Byte, PwsField> fields = new Hashtable<Byte, PwsField>();

    public PwsField get(Byte aFieldType)
    throws ModelException
    {
        if(fields.containsKey(aFieldType))
        {
            return fields.get(aFieldType);
        }
        else
        {
            final String lMsg = "No field of that type available.";
            throw new ModelException(lMsg);
        }
    }

    public void put(PwsField aField)
    {
        fields.put(aField.getType(), aField);
    }

    public boolean hasType(Byte aFieldType)
    {
        return  fields.containsKey(aFieldType);
    }

    public Iterator<Byte> typeIterator()
    {
        SortedSet<Byte> lKeys = new TreeSet<Byte>(fields.keySet());
        return lKeys.iterator();
    }

    public void remove(Byte aFieldType)
    throws ModelException
    {
        if(this.hasType(aFieldType))
        {
            fields.remove(aFieldType);
        }
        else
        {
            final String lMsg = "No field of that type available.";
            throw new ModelException(lMsg);
        }
    }
}
