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

import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.io.File;

public class PwsDatabaseImpl
implements PwsDatabase
{
    private List<PwsRecord> records = new ArrayList<PwsRecord>(20);
    private String passphrase;
    private Codec codec;
    private File file;
    private String parameters;
    private boolean changed = false;

    private class PwsDatabaseIterator
    implements Iterator<PwsRecord>
    {
        private int currentIndex = 0;

        public boolean hasNext()
        {
            return (currentIndex < records.size());
        }

        public PwsRecord next()
        {
            return getRecord(currentIndex++);
        }

        public void remove()
        {
            PwsDatabaseImpl.this.remove(currentIndex);
        }
    }

    public int getNrRecords()
    {
        return records.size();
    }

    public PwsRecord getRecord(int aIndex)
    {
        return records.get(aIndex);
    }

    public void add(PwsRecord aRecord)
    {
        records.add(aRecord);
    }

    public void remove(int aIndex)
    {
        records.remove(aIndex);
    }

    public Iterator<PwsRecord> iterator()
    {
        return new PwsDatabaseIterator();
    }

    public String getVersion()
    {
        return codec.getVersion();
    }

    public String getPassphrase()
    {
        return passphrase;
    }

    public void setPassphrase(String passphrase)
    {
        this.passphrase = passphrase;
    }

    public Codec getCodec()
    {
        return codec;
    }

    public void setCodec(Codec codec)
    {
        this.codec = codec;
    }

    public File getFile()
    {
        return file;
    }

    public void setFile(File file)
    {
        this.file = file;
    }

    public String getParameters()
    {
        return parameters;
    }

    public void setParameters(String parameters)
    {
        this.parameters = parameters;
    }

    public boolean isChanged()
    {
        return changed;
    }

    public void setChanged(boolean aChanged)
    {
        this.changed = aChanged;
    }
}
