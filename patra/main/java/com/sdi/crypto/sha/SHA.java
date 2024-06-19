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

package com.sdi.crypto.sha;

import java.util.Arrays;

public class SHA
{
    private static final int BLOCK_SIZE = 64;
    public static final int HASH_SIZE = 20;

    private long bytesDigested;
    private byte[] buffer = new byte[BLOCK_SIZE];
    private final int[] w = new int[80];

    protected int h0, h1, h2, h3, h4;

    public SHA()
    {
        reset();
    }

    private void transform(byte[] aDataToDigest, int aOffset)
    {
        final int[] lResult = sha(h0, h1, h2, h3, h4, aDataToDigest, aOffset);
        h0 = lResult[0];
        h1 = lResult[1];
        h2 = lResult[2];
        h3 = lResult[3];
        h4 = lResult[4];
    }

    private byte[] padBuffer()
    {
        final int lCandidatePaddingLength = (int) (bytesDigested % BLOCK_SIZE);
        final int lEffectivePaddingLength = (lCandidatePaddingLength < 56) ? (56 - lCandidatePaddingLength) : (120 - lCandidatePaddingLength);
        final byte[] lResult = new byte[lEffectivePaddingLength + 8];

        lResult[0] = (byte) 0x80;
        final long lBits = bytesDigested << 3;
        lResult[lEffectivePaddingLength + 0] = (byte) (lBits >>> 56);
        lResult[lEffectivePaddingLength + 1] = (byte) (lBits >>> 48);
        lResult[lEffectivePaddingLength + 2] = (byte) (lBits >>> 40);
        lResult[lEffectivePaddingLength + 3] = (byte) (lBits >>> 32);
        lResult[lEffectivePaddingLength + 4] = (byte) (lBits >>> 24);
        lResult[lEffectivePaddingLength + 5] = (byte) (lBits >>> 16);
        lResult[lEffectivePaddingLength + 6] = (byte) (lBits >>> 8);
        lResult[lEffectivePaddingLength + 7] = (byte) lBits;

        return lResult;
    }

    private byte[] getResult()
    {
        return new byte[]{
            (byte) (h0 >>> 24), (byte) (h0 >>> 16), (byte) (h0 >>> 8), (byte) h0,
            (byte) (h1 >>> 24), (byte) (h1 >>> 16), (byte) (h1 >>> 8), (byte) h1,
            (byte) (h2 >>> 24), (byte) (h2 >>> 16), (byte) (h2 >>> 8), (byte) h2,
            (byte) (h3 >>> 24), (byte) (h3 >>> 16), (byte) (h3 >>> 8), (byte) h3,
            (byte) (h4 >>> 24), (byte) (h4 >>> 16), (byte) (h4 >>> 8), (byte) h4
        };
    }

    private final int[]
    sha(int hh0, int hh1, int hh2, int hh3, int hh4, byte[] in, int aOffset)
    {
        int A = hh0;
        int B = hh1;
        int C = hh2;
        int D = hh3;
        int E = hh4;
        int r, T;

        for (r = 0; r < 16; r++)
        {
            w[r] = in[aOffset++] << 24 |
                    (in[aOffset++] & 0xFF) << 16 |
                    (in[aOffset++] & 0xFF) << 8 |
                    (in[aOffset++] & 0xFF);
        }
        for (r = 16; r < 80; r++)
        {
            T = w[r - 3] ^ w[r - 8] ^ w[r - 14] ^ w[r - 16];
            w[r] = T << 1 | T >>> 31;
        }

        // rounds 0-19
        for (r = 0; r < 20; r++)
        {
            T = (A << 5 | A >>> 27) + ((B & C) | (~B & D)) + E + w[r] + 0x5A827999;
            E = D;
            D = C;
            C = B << 30 | B >>> 2;
            B = A;
            A = T;
        }

        // rounds 20-39
        for (r = 20; r < 40; r++)
        {
            T = (A << 5 | A >>> 27) + (B ^ C ^ D) + E + w[r] + 0x6ED9EBA1;
            E = D;
            D = C;
            C = B << 30 | B >>> 2;
            B = A;
            A = T;
        }

        // rounds 40-59
        for (r = 40; r < 60; r++)
        {
            T = (A << 5 | A >>> 27) + (B & C | B & D | C & D) + E + w[r] + 0x8F1BBCDC;
            E = D;
            D = C;
            C = B << 30 | B >>> 2;
            B = A;
            A = T;
        }

        // rounds 60-79
        for (r = 60; r < 80; r++)
        {
            T = (A << 5 | A >>> 27) + (B ^ C ^ D) + E + w[r] + 0xCA62C1D6;
            E = D;
            D = C;
            C = B << 30 | B >>> 2;
            B = A;
            A = T;
        }

        return new int[]{hh0 + A, hh1 + B, hh2 + C, hh3 + D, hh4 + E};
    }

    public void update(byte aDataByte)
    {
        final int lNrUnusedBytesInBlock = (int) (bytesDigested % BLOCK_SIZE);
        bytesDigested++;
        buffer[lNrUnusedBytesInBlock] = aDataByte;
        if (lNrUnusedBytesInBlock == (BLOCK_SIZE - 1))
        {
            transform(buffer, 0);
        }
    }

    public void update(byte[] aBufferToDigest, int aOffset, int aLen)
    {
        int lNrUnusedBytesInBlock = (int) (bytesDigested % BLOCK_SIZE);
        bytesDigested += aLen;
        int lNrUsedbytesInBlock = BLOCK_SIZE - lNrUnusedBytesInBlock;

        int i = 0;
        if (aLen >= lNrUsedbytesInBlock)
        {
            System.arraycopy(aBufferToDigest, aOffset, buffer, lNrUnusedBytesInBlock, lNrUsedbytesInBlock);
            transform(buffer, 0);
            for (i = lNrUsedbytesInBlock; i + BLOCK_SIZE - 1 < aLen; i += BLOCK_SIZE)
            {
                transform(aBufferToDigest, aOffset + i);
            }
            lNrUnusedBytesInBlock = 0;
        }

        if (i < aLen)
        {
            System.arraycopy(aBufferToDigest, aOffset + i, buffer, lNrUnusedBytesInBlock, aLen - i);
        }
    }

    public byte[] digest()
    {
        final byte[] lTail = padBuffer();
        update(lTail, 0, lTail.length);
        final byte[] lResult = getResult();
        reset();
        return lResult;
    }

    public void reset()
    {
        bytesDigested = 0L;
        Arrays.fill(buffer, (byte) 0);
        Arrays.fill(w, 0);

        h0 = 0x67452301;
        h1 = 0xEFCDAB89;
        h2 = 0x98BADCFE;
        h3 = 0x10325476;
        h4 = 0xC3D2E1F0;
    }
}