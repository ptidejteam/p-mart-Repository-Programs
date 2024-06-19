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

public class PwsFieldImpl implements PwsField
{
    private Byte type;
    private byte[] value;

    public PwsFieldImpl(Byte aType, byte[] aValue)
    {
        type = aType;
        value = aValue;
    }

    public PwsFieldImpl(Byte aType, String aValue)
    {
        type = aType;
        setAsString(aValue);
    }

    public Byte getType()
    {
        return type;
    }

    public void setType(Byte aType)
    {
        this.type = aType;
    }

    public byte[] getValue()
    {
        return value;
    }

    public void setValue(byte[] value)
    {
        this.value = value;
    }

    public String getAsString()
    throws ModelException
    {
        try
        {
            return new String(value, "ISO-8859-1");
        }
        catch(Exception e)
        {
            throw new ModelException("Error while converting bytes to String", e);
        }
    }

    public void setAsString(String aValue)
    {
        try
        {
            value = aValue.getBytes("ISO-8859-1");
        }
        catch(Exception e){}
    }
}