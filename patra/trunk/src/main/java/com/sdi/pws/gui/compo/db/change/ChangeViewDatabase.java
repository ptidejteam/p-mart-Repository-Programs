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

import com.sdi.pws.codec.Codec;
import com.sdi.pws.db.PwsDatabase;
import com.sdi.pws.db.PwsRecord;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.File;
import java.util.Iterator;
import java.util.List;
import java.util.LinkedList;

public class ChangeViewDatabase
implements PwsDatabase
{
    public final static String CHANGED =  "changed";
    public final static String VERSION = "version";
    public static final String PASSPHRASE = "passphrase";
    public static final String CODEC = "codec";
    public static final String PARAMETERS = "parameters";
    public static final String FILE =  "file";

    private PwsDatabase db;
    private PropertyChangeSupport support = new PropertyChangeSupport(this);
    private List listeners = new LinkedList();

    public ChangeViewDatabase(PwsDatabase aDb)
    {
        db = aDb;
    }

    public PwsDatabase getInternal()
    {
        return db;
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

    // Change view listener support.

    public void addChangeViewDatabaseListener(ChangeViewDatabaseListener aListener)
    {
        if(!listeners.contains(aListener)) listeners.add(aListener);

    }

    public void removeChangeViewDatabaseListener(ChangeViewDatabaseListener aListener)
    {
        listeners.remove(aListener);
    }

    void fireChangeDatabaseEvent(ChangeViewDatabaseEvent aEvt)
    {
        final Iterator lIter = listeners.iterator();
        while(lIter.hasNext())
            ((ChangeViewDatabaseListener) lIter.next()).dbChange(aEvt);
    }

    void fireChangeFieldEvent(ChangeViewFieldEvent aEvt)
    {
        final Iterator lIter = listeners.iterator();
        while(lIter.hasNext())
            ((ChangeViewDatabaseListener) lIter.next()).fieldChange(aEvt);
    }

    void fireChangeRecordEvent(ChangeViewRecordEvent aEvt)
    {
        final Iterator lIter = listeners.iterator();
        while(lIter.hasNext())
            ((ChangeViewDatabaseListener) lIter.next()).recordChange(aEvt);
    }

    // Delegation.

    public int getNrRecords()
    {
        return db.getNrRecords();
    }

    public PwsRecord getRecord(int aIndex)
    {
        return new ChangeViewRecord(this, db.getRecord(aIndex));
    }

    public void add(PwsRecord aRecord)
    {
        db.add(aRecord);
        final ChangeViewDatabaseEvent lEvent = new ChangeViewDatabaseEvent(db, aRecord, db.getNrRecords()-1, ChangeViewDatabaseEvent.INSERT);
        fireChangeDatabaseEvent(lEvent);
        this.setChanged(true);
    }

    public void remove(int aIndex)
    {
        final Object lOldValue = db.getRecord(aIndex);
        db.remove(aIndex);
        final ChangeViewDatabaseEvent lEvent = new ChangeViewDatabaseEvent(db, (PwsRecord) lOldValue, aIndex, ChangeViewDatabaseEvent.DELETE);
        fireChangeDatabaseEvent(lEvent);
        this.setChanged(true);
    }

    private class ChangeViewDatabaseIterator
    implements Iterator
    {
        private Iterator iter;
        public ChangeViewDatabaseIterator(Iterator aIter)
        {
            iter = aIter;
        }

        public void remove()
        {
            iter.remove();
        }

        public boolean hasNext()
        {
            return iter.hasNext();
        }

        public Object next()
        {
            return new ChangeViewRecord(ChangeViewDatabase.this, (PwsRecord) iter.next());
        }
    }

    public Iterator iterator()
    {
        return new ChangeViewDatabaseIterator(db.iterator());
    }

    public String getVersion()
    {
        return db.getVersion();
    }

    public String getPassphrase()
    {
        return db.getPassphrase();
    }

    public void setPassphrase(String passphrase)
    {
        final Object lOldValue = db.getPassphrase();

        db.setPassphrase(passphrase);
        db.setChanged(true);

        support.firePropertyChange(PASSPHRASE, lOldValue, passphrase);
    }

    public Codec getCodec()
    {
        return db.getCodec();
    }

    public void setCodec(Codec codec)
    {
        final Object lOldValue = db.getCodec();

        db.setCodec(codec);
        db.setChanged(true);

        support.firePropertyChange(CODEC, lOldValue, codec);
    }

    public File getFile()
    {
        return db.getFile();
    }

    public void setFile(File file)
    {
        final Object lOldValue = db.getFile();

        db.setFile(file);
        db.setChanged(true);

        support.firePropertyChange(FILE, lOldValue, file);
    }

    public String getParameters()
    {
        return db.getParameters();
    }

    public void setParameters(String parameters)
    {
        final Object lOldValue = db.getParameters();

        db.setParameters(parameters);
        db.setChanged(true);

        support.firePropertyChange(PARAMETERS, lOldValue, parameters);
    }

    public boolean isChanged()
    {
        return db.isChanged();
    }

    public void setChanged(boolean aChanged)
    {
        final Boolean lOldValue = Boolean.valueOf(db.isChanged());
        final Boolean lNewValue = Boolean.valueOf(aChanged);

        db.setChanged(aChanged);
        support.firePropertyChange(CHANGED, lOldValue, lNewValue);
    }
}