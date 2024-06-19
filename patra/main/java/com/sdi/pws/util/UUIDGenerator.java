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

package com.sdi.pws.util;

import java.security.SecureRandom;

/**
 * util class to generate a unique ID in hexadecimal using IP address, time and randomnumber
 */
public class UUIDGenerator
{
    private static UUIDGenerator singleton;
    private SecureRandom seeder = new SecureRandom();
    private byte[] midValue = new byte[8];

    /** Factory method.*/
    public static UUIDGenerator getUUIDGenerator()
    {
        if(singleton == null) singleton = new UUIDGenerator();
        return singleton;
    }

    /*Prevent public construction.*/
    private UUIDGenerator()
    {
        byte[] lBytes;
        try
        {
            java.net.InetAddress lInet;
            lInet = java.net.InetAddress.getLocalHost();
            lBytes = lInet.getAddress();
        }
        catch(Exception e)
        {
            lBytes = new byte[] {(byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff};
        }

        System.arraycopy(lBytes, 0, midValue, 0, 4);
        final byte[] lHashBytes = fromInt(System.identityHashCode(this));
        System.arraycopy(lHashBytes, 0, midValue, 4, 4);
    }

    private byte[] fromInt(int aVal)
    {
        return new byte[] {(byte) (aVal >> 24), (byte) ((aVal >> 16) & 0xff), (byte) ((aVal >> 8) & 0xff), (byte) (aVal & 0xff)};
    }

    /**
     * Get a new unique id.
     * @return new unique id.
     */
    public synchronized byte[] getUUID()
    {
        // Generate some data.
        seeder.nextInt();
        long lTimeNow = System.currentTimeMillis();
        int lTimeLow = (int) lTimeNow & 0xFFFFFFFF;
        int lNode = seeder.nextInt();

        // Convert the data into byte[].
        byte[] lTimeLowBytes = fromInt(lTimeLow);
        byte[] lNodeBytes = fromInt(lNode);

        // Construct the result.
        byte[] lResult = new byte[16];
        System.arraycopy(lTimeLowBytes, 0, lResult, 0,  4);
        System.arraycopy(midValue,      0, lResult, 4,  8);
        System.arraycopy(lNodeBytes,    0, lResult, 12, 4);

        return lResult;
    }

    public String hexFormat(byte[] aVal)
    {
        return (new java.math.BigInteger(aVal)).toString(16);
    }
}