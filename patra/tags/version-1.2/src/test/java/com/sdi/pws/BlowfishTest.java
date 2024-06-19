/*
Password Tracker (PATRA). An application to safely store your passwords.
Copyright (C) 2006  Bruno Ranschaert, S.D.I.-Consulting BVBA.

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

package com.sdi.pws;

import com.sdi.pws.codec.Codec;
import com.sdi.pws.codec.Codec2;
import com.sdi.pws.db.*;

import java.io.File;
import java.util.Iterator;

public class BlowfishTest
{
    public static void main(String[] aArgs)
    {
        try
        {
            Codec lCodec = new Codec2();

            // READ
            PwsDatabase lDb = lCodec.read(new File("src/testdata/testbis.dat"), "leve sdi consulting");
            Iterator lIter = lDb.iterator();
            while (lIter.hasNext())
            {
                PwsRecord lRec = (PwsRecord) lIter.next();

                if(lRec.hasType(PwsField.FIELD_DEFAULT_UID)) System.out.println("DEFAULT UID");

                if (lRec.hasType(PwsField.FIELD_TITLE))
                {
                    PwsField lField = lRec.get(PwsField.FIELD_TITLE);
                    System.out.println(">>>>>" + lField.getAsString() + "<<<<<");
                }
                else
                {
                    System.out.println("!!!!! No UID for " + lRec.get(PwsField.FIELD_TITLE).getAsString());
                }
            }

            // MODIFY
            {
                PwsRecordImpl lNewRec = new PwsRecordImpl();
                lNewRec.put(new PwsFieldImpl(PwsField.FIELD_TITLE, "Test - 3".getBytes()));
                lNewRec.put(new PwsFieldImpl(PwsField.FIELD_UID, "branscha".getBytes()));
                lNewRec.put(new PwsFieldImpl(PwsField.FIELD_PWD, "oeleboele".getBytes()));
                lNewRec.put(new PwsFieldImpl(PwsField.FIELD_NOTES, "Some stupid notes...".getBytes()));
                lNewRec.put(new PwsFieldImpl(PwsField.FIELD_GROUP, "The Ultimate Group".getBytes()));
                lDb.add(lNewRec);
            }

            // WRITE
            lDb.setFile(new File("src/testdata/test2.dat"));
            lDb.setPassphrase("lcm");
            lCodec.write(lDb);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
