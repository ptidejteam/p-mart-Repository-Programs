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

import com.sdi.pws.codec.Codec;

import java.util.Iterator;
import java.io.File;

/**
 * Temporarily mask the real file and  lets you write the database
 * to another file without changing the contents. This is used
 * to export the database to another file without changing the original file associated
 * with the database. 
 */
public class PwsDatabaseFileWrapper
implements PwsDatabase
{
    private PwsDatabase target;
    private File file;

    public PwsDatabaseFileWrapper(PwsDatabase aDb, File aFile)
    {
        target = aDb;
        file = aFile;
    }

    public int getNrRecords()
    {
        return target.getNrRecords();
    }

    public PwsRecord getRecord(int aIndex)
    {
        return target.getRecord(aIndex);
    }

    public void add(PwsRecord aRecord)
    {
        target.add(aRecord);
    }

    public void remove(int aIndex)
    {
        target.remove(aIndex);
    }

    public Iterator<PwsRecord> iterator()
    {
        return target.iterator();
    }

    public String getVersion()
    {
        return target.getVersion();
    }

    public String getPassphrase()
    {
        return target.getPassphrase();
    }

    public void setPassphrase(String aPassphrase)
    {
        target.setPassphrase(aPassphrase);
    }

    public Codec getCodec()
    {
        return target.getCodec();
    }

    public void setCodec(Codec aCodec)
    {
        target.setCodec(aCodec);
    }

    public File getFile()
    {
        return file;
    }

    public void setFile(File aFile)
    {
        this.file = aFile;
    }

    public String getParameters()
    {
        return target.getParameters();
    }

    public void setParameters(String aParams)
    {
        target.setParameters(aParams);
    }

    public boolean isChanged()
    {
        return target.isChanged();
    }

    public void setChanged(boolean aChanged)
    {
        target.setChanged(aChanged);
    }
}