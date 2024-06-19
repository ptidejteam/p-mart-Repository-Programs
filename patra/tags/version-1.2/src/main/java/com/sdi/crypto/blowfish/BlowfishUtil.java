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

package com.sdi.crypto.blowfish;

public class BlowfishUtil
{
    public static long packLE(byte[] aBuf, int aOffset)
    {
        long lResult = 0;
        lResult |= aBuf[aOffset+7] & 0xff; lResult <<= 8;
        lResult |= aBuf[aOffset+6] & 0xff; lResult <<= 8;
        lResult |= aBuf[aOffset+5] & 0xff; lResult <<= 8;
        lResult |= aBuf[aOffset+4] & 0xff; lResult <<= 8;
        lResult |= aBuf[aOffset+3] & 0xff; lResult <<= 8;
        lResult |= aBuf[aOffset+2] & 0xff; lResult <<= 8;
        lResult |= aBuf[aOffset+1] & 0xff; lResult <<= 8;
        lResult |= aBuf[aOffset+0] & 0xff;
        return lResult;
    }

    public static void unpackLE(long aValue, byte[] aBuf, int aOffset)
    {
        aBuf[aOffset+0] = (byte) aValue; aValue >>= 8;
        aBuf[aOffset+1] = (byte) aValue; aValue >>= 8;
        aBuf[aOffset+2] = (byte) aValue; aValue >>= 8;
        aBuf[aOffset+3] = (byte) aValue; aValue >>= 8;
        aBuf[aOffset+4] = (byte) aValue; aValue >>= 8;
        aBuf[aOffset+5] = (byte) aValue; aValue >>= 8;
        aBuf[aOffset+6] = (byte) aValue; aValue >>= 8;
        aBuf[aOffset+7] = (byte) aValue;
    }

    public static long cvt2(long aValue)
    {
        long lResult = 0;
        lResult |= (aValue >>> 24) & 0xff; lResult <<= 8;
        lResult |= (aValue >>> 16) & 0xff; lResult <<= 8;
        lResult |= (aValue >>> 8)  & 0xff; lResult <<= 8;
        lResult |= (aValue >>> 0)  & 0xff; lResult <<= 8;
        lResult |= (aValue >>> 56) & 0xff; lResult <<= 8;
        lResult |= (aValue >>> 48) & 0xff; lResult <<= 8;
        lResult |= (aValue >>> 40) & 0xff; lResult <<= 8;
        lResult |= (aValue >>> 32) & 0xff;
        return lResult;
    }
}