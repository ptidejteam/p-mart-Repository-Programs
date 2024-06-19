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

package com.sdi.pws.codec;

import com.sdi.pws.db.PwsDatabase;

import java.io.File;

public class AutoCodec
implements Codec
{
    private Codec fstCodec = new Codec1();
    private Codec scndcodec = new Codec2();
    private Codec selected;

    public PwsDatabase read(File aFile, String aPassphrase) throws CodecException
    {
        PwsDatabase lNewDb;

        try
        {
            lNewDb = scndcodec.read(aFile, aPassphrase);
            selected = scndcodec;
        }
        catch(CodecException eCodec)
        {
            lNewDb = fstCodec.read(aFile, aPassphrase);
            selected = fstCodec;
        }

        if(lNewDb == null)
        {
            throw new CodecException("Could not open the file.");
        }
        else
        {
            lNewDb.setCodec(this);
            return lNewDb;
        }
    }

    public void write(PwsDatabase aDb) throws CodecException
    {
        if(selected != null) selected.write(aDb);
        else throw new CodecException("AutoCodec did not contain a selected codec.");
    }

    public String getVersion()
    {
        if(selected != null) return selected.getVersion();
        else return "UNKNOWN VERSION";
    }

    public Codec getSelected()
    {
        return selected;
    }
}