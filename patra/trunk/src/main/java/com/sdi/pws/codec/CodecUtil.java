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

import com.sdi.crypto.blowfish.*;
import com.sdi.crypto.sha.SHA;
import com.sdi.crypto.sha.SHAModified;
import com.sdi.pws.db.*;
import com.sdi.pws.gui.compo.db.change.ChangeViewDatabase;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Random;
import java.util.Iterator;

public class CodecUtil
{
    public static boolean checkPassphrase(PwsFileHeader aHeader, String aPassphrase)
    {
        // First calculate the check bytes.
        final byte[] lCheck = calculatePassphraseHash(aHeader.getRndData(), aPassphrase);

        // Finally do the comparison.
        return Arrays.equals(lCheck, aHeader.getRndCheck());
    }

    public static byte[] calculatePassphraseHash(long aData, String aPassphrase)
    {
        // Calculate temp password to check password.
        final byte[] lPassphrasebytes = aPassphrase.getBytes();

        // Calculate a key that will be used to calculate the digest that contains the
        // password check. lCalcKey = SHA1(RND|{0x00,0x00}|password);
        final SHA lDigester = new SHA();
        final byte[] lRndDataBytes = new byte[8];
        BlowfishUtil.unpackLE(aData, lRndDataBytes, 0);
        lDigester.update(lRndDataBytes, 0, 8);
        lDigester.update((byte) 0);
        lDigester.update((byte) 0);
        lDigester.update(lPassphrasebytes, 0, lPassphrasebytes.length);
        final byte[] lCalcKey = lDigester.digest();

        // Encrypt the random data 1000 times using the calculated key.
        // Cipher(RND) is 1000 encryptions of RND using lCalcKey.
        final Blowfish lFish = new BlowfishLE(new BlowfishBasic(lCalcKey));
        long lBlock = aData;
        for (int i = 0; i < 1000; i++) lBlock = lFish.encipher(lBlock);

        // Unpack the result in bytes and calculate the digest.
        // H(RND) is SHA1_init_state_zero(Cipher(RND)|{0x00,0x00});
        final byte[] lBlockBytes = new byte[8];
        BlowfishUtil.unpackLE(lBlock, lBlockBytes, 0);
        final SHAModified lSpecialDigester = new SHAModified();
        lSpecialDigester.update(lBlockBytes, 0, 8);
        lSpecialDigester.update((byte) 0);
        lSpecialDigester.update((byte) 0);
        return lSpecialDigester.digest();
    }

    public static Blowfish initBlowfish(PwsFileHeader aHeader, String aPassphrase)
    {
        // Calculate the operational password.
        SHA lDigester = new SHA();
        lDigester.update(aPassphrase.getBytes(), 0, aPassphrase.getBytes().length);
        lDigester.update(aHeader.getPwSalt(), 0, aHeader.getPwSalt().length);
        final byte[] lOperationalPwd = lDigester.digest();

        // Create an appropriate cipher.

        // Return the result.
        return new BlowfishCBC(new BlowfishLE(new BlowfishBasic(lOperationalPwd)), aHeader.getCbc());
    }

    public static PwsFileHeader initPwsHeader(String aPassphrase)
    {
        // Create a new fresh header.
        PwsFileHeader lPwsHeader = new PwsFileHeader();

        // Generate random password checking data.
        Random lRnd = new Random();
        lRnd.setSeed(System.currentTimeMillis());
        lPwsHeader.setRndData(lRnd.nextLong());

        // Calculate the hash of the random.
        final byte[] lCheck = calculatePassphraseHash(lPwsHeader.getRndData(), aPassphrase);
        lPwsHeader.setRndCheck(lCheck);

        // Generate random salt.
        lRnd.nextBytes(lPwsHeader.getPwSalt());

        // Generate random cbc vector.
        lPwsHeader.setCbc(lRnd.nextLong());

        return lPwsHeader;
    }

    public static PwsFieldImpl readValue(FileInputStream aStream, ByteBuffer aBuf, Blowfish aFish)
    throws IOException, CodecException
    {
        checkReadSpace(aStream, aBuf);

        // Read and decipher the value length.
        // After deciphering, we only use 2 (unsigned) bytes of the 8 as length. This is stated in the
        // file specifications. Some applications put 0 in the most significant bytes, others fill
        // it with garbage.
        long lValueByteLength = aBuf.getLong();
        long lDeciphered = aFish.decipher(lValueByteLength);
        lValueByteLength = lDeciphered & 0xffffffffL;
        final byte lType = (byte) ((lDeciphered & 0xff00000000L) >> 32);

        // Security rope.
        if (lValueByteLength > 2000)
        {
            // codec03
            final String lMsg = "codec03";
            throw new CodecException(lMsg);
        }

        // Calculate the number of blocks to read. These will be padded.
        // Note that when the lValueByteLength is 0 there is a block with 0 contents; this block should be read from the file.
        final long lRestLength = (lValueByteLength == 0) ? 8 : (lValueByteLength % 8);
        final long lValueBlockLen = lRestLength > 0 ? ((lValueByteLength + (8 - lValueByteLength % 8)) / 8) : (lValueByteLength / 8);
        final byte[] lBlockBuf = new byte[8];
        final byte[] lValueBuf = new byte[(int) lValueByteLength];
        int lBufPtr = 0;

        // Now that we calculated the block length and the byte length of the value, we
        // can start reading the blocks and decoding them.
        for (int i = 0; i < lValueBlockLen; i++)
        {
            checkReadSpace(aStream, aBuf);

            // These two lines are in fact the core of the codec.
            long lBlock = aBuf.getLong();
            lBlock = aFish.decipher(lBlock);

            // Unpack the block into single bytes.
            BlowfishUtil.unpackLE(lBlock, lBlockBuf, 0);

            // Append the bytes from the block to the bytes of previous blocks.
            // Note that in the last block it is possible that there are filler bytes which  we do not need.
            // We should skip these trailer bytes.
            int lBytesToCopy = lValueBuf.length - lBufPtr;
            if (lBytesToCopy > lBlockBuf.length) lBytesToCopy = lBlockBuf.length;
            System.arraycopy(lBlockBuf, 0, lValueBuf, lBufPtr, lBytesToCopy);
            lBufPtr += lBytesToCopy;
        }
        return new PwsFieldImpl(new Byte(lType), lValueBuf);
    }

    public static void writeValue(FileOutputStream aStream, ByteBuffer aBuf, Blowfish aFish, PwsField aField)
            throws IOException
    {
        // Length in bytes.
        final byte[] lValue = aField.getValue();
        final byte lType = aField.getType().byteValue();
        final int lValueByteLength = lValue.length;

        // Calculate the number of blocks to write. These will be padded.
        // Note that when the lValueByteLength is 0 there is a block with 0 contents; this block should be written to the file.
        final long lRestLength = (lValueByteLength == 0) ? 8 : (lValueByteLength % 8);
        final long lValueBlockLen = lRestLength > 0 ? ((lValueByteLength + (8 - lValueByteLength % 8)) / 8) : (lValueByteLength / 8);
        final byte[] lBlockBuf = new byte[8];
        int lBufPtr = 0;

        // Write the length to the file.
        checkWriteSpace(aStream, aBuf);

        final long lLengthBlock = (lValueByteLength & 0xffffffffL) | (((long) lType & 0xff) << 32);
        aBuf.putLong(aFish.encipher(lLengthBlock));

        // Now that we calculated the block length and the byte length of the value, we
        // can start encodingthe blocks and writing them.
        for (int i = 0; i < lValueBlockLen; i++)
        {
            checkWriteSpace(aStream, aBuf);

            // Append the bytes from the block to the bytes of previous blocks.
            // Note that in the last block it is possible that there are filler bytes which  we do not need.
            // We should skip these trailer bytes.
            int lBytesToCopy = lValue.length - lBufPtr;
            if (lBytesToCopy > lBlockBuf.length) lBytesToCopy = lBlockBuf.length;
            System.arraycopy(lValue, lBufPtr, lBlockBuf, 0, lBytesToCopy);
            lBufPtr += lBytesToCopy;

            // These two lines are in fact the core of the codec.
            long lBlock = BlowfishUtil.packLE(lBlockBuf, 0);
            lBlock = aFish.encipher(lBlock);
            aBuf.putLong(lBlock);
        }
    }

    public static void checkWriteSpace(FileOutputStream aStream, ByteBuffer aBuf)
            throws IOException
    {
        if (aBuf.remaining() < 8)
        {
            aBuf.flip();
            aStream.getChannel().write(aBuf);
            aBuf.flip();
        }
    }

    public static void checkReadSpace(FileInputStream aStream, ByteBuffer aBuf)
            throws IOException
    {
        if (aBuf.remaining() < 8)
        {
            aBuf.flip();
            aStream.getChannel().read(aBuf);
            aBuf.flip();
        }
    }

    public static void upgradeVersion(PwsDatabase aDb)
    {
        final Codec lCodec = aDb.getCodec();
        if(Codec1.VERSION.equals(lCodec.getVersion()))
        {
            aDb.setCodec(new Codec2());
        }
    }

    public static void upgradeVersion(ChangeViewDatabase aDb, String aGroup)
    {
        // Prevent the ChangeViewDatabase from firing change events for each
        // record change by converting the encapsulated database and setting the changed flag once.
        // Setting the changed flag will generate a single changed event.
        // If we don't do this, the GUI will slow down due to the enormous nr of events fired.
        final PwsDatabase lInternal = aDb.getInternal();
        upgradeVersion(lInternal, aGroup);
        aDb.setChanged(true);                
    }

    public static void upgradeVersion(PwsDatabase aDb, String aGroup)
    {
        upgradeVersion(aDb);
        final Iterator lIter = aDb.iterator();
        while(lIter.hasNext())
        {
            final PwsRecord lRec = (PwsRecord) lIter.next();
            final PwsField lField = new PwsFieldImpl(PwsField.FIELD_GROUP, aGroup);
            lRec.put(lField);
        }
    }

    public static void downgradeVersion(PwsDatabase aDb)
    {
        final Codec lCodec = aDb.getCodec();
        if(Codec2.VERSION.equals(lCodec.getVersion()))
        {
            aDb.setCodec(new Codec1());
        }
    }
}
