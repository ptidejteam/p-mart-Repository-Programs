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

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;

public class PwsFileHeader
{
    private long rndData = 0;
    private byte[] rndCheck = new byte[20];
    private byte[] pwSalt = new byte[20];
    private long cbc = 0;

    public void readFromBuffer(ByteBuffer aBuf)
    throws IOException
    {
        rndData = aBuf.getLong();
        aBuf.get(rndCheck);
        aBuf.get(pwSalt);
        cbc = aBuf.getLong();
    }

    public void writeToBuffer(ByteBuffer aBuf)
    {
        aBuf.putLong(rndData);
        aBuf.put(rndCheck);
        aBuf.put(pwSalt);
        aBuf.putLong(cbc);
    }

    public PwsFileHeader()
    {
        Arrays.fill(rndCheck, (byte) 0);
        Arrays.fill(pwSalt, (byte) 0);
    }

    public void setRndData(long rndData)
    {
        this.rndData = rndData;
    }

    public void setRndCheck(byte[] rndCheck)
    {
        this.rndCheck = rndCheck;
    }

    public void setPwSalt(byte[] pwSalt)
    {
        this.pwSalt = pwSalt;
    }

    public void setCbc(long cbc)
    {
        this.cbc = cbc;
    }

    public long getRndData()
    {
        return rndData;
    }

    public byte[] getRndCheck()
    {
        return rndCheck;
    }

    public byte[] getPwSalt()
    {
        return pwSalt;
    }

    public long getCbc()
    {
        return cbc;
    }
}