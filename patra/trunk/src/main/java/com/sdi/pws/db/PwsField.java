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

public interface PwsField
{
    // Field types defined by the standard.
    ///////////////////////////////////////
    final Byte FIELD_UUID                     = new Byte((byte)0x01);
    final Byte FIELD_GROUP                    = new Byte((byte)0x02);
    final Byte FIELD_TITLE                    = new Byte((byte)0x03);
    final Byte FIELD_UID                      = new Byte((byte)0x04);
    final Byte FIELD_NOTES                    = new Byte((byte)0x05);
    final Byte FIELD_PWD                      = new Byte((byte)0x06);
    final Byte FIELD_CREATION_TIME            = new Byte((byte)0x07);
    final Byte FIELD_PWD_MODIFICATION_TIME    = new Byte((byte)0x08);
    final Byte FIELD_ACCESS_TIME              = new Byte((byte)0x09);
    final Byte FIELD_LIFETIME                 = new Byte((byte)0x0a);
    final Byte FIELD_POLICY                   = new Byte((byte)0x0b);
    final Byte FIELD_MODIFICATION_TIME        = new Byte((byte)0x0c);
    // Field types for internal use.
    ////////////////////////////////
    final Byte FIELD_HEADER                   = new Byte((byte) 0x45);
    final Byte FIELD_DEFAULT_UID              = new Byte((byte) 0xf0); // Represents the default uid.
    final Byte FIELD_EOR                      = new Byte((byte) 0xff);

    Byte getType();
    void setType(Byte aType);

    byte[] getValue();
    void setValue(byte[] value);

    String getAsString()
    throws ModelException;

    void setAsString(String aValue);
}
