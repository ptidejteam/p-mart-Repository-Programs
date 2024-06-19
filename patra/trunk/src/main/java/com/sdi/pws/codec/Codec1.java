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

import com.sdi.crypto.blowfish.Blowfish;
import com.sdi.pws.db.*;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.BufferUnderflowException;
import java.util.Iterator;

public class Codec1
implements Codec
{
    public static final String VERSION = "1.0";

    public PwsDatabase read(File aFile, String aPassphrase)
    throws CodecException
    {
        // Guarded resource.
        FileInputStream lStream = null;

        // Initialize the database structure.
        final PwsDatabaseImpl lDb = new PwsDatabaseImpl();
        lDb.setCodec(this);
        lDb.setPassphrase(aPassphrase);
        lDb.setFile(aFile);

        // Shared resource.
        PwsRecordImpl lCurrentPwsRec = null;

        try
        {
            lStream = new FileInputStream(aFile);
            final ByteBuffer lDataBuf = ByteBuffer.allocateDirect(56);
            lDataBuf.order(ByteOrder.LITTLE_ENDIAN);
            lStream.getChannel().read(lDataBuf);
            lDataBuf.flip();

            // Read the passwordsafe-file header.
            final PwsFileHeader lPwsHeader = new PwsFileHeader();
            lPwsHeader.readFromBuffer(lDataBuf);

            // Check if the passphrase is consistent with the hash that is encoded
            // in the header.
            if (CodecUtil.checkPassphrase(lPwsHeader, aPassphrase))
            {
                // Yes, the passphrase is correct. We can initialize a blowfish cipher to decipher
                // the rest of the data.
                final Blowfish lFish = CodecUtil.initBlowfish(lPwsHeader, aPassphrase);

                // This counter will keep track of all the values we encountered in the file.
                // A password record consists  of 3 consecutive values. The counter is needed
                // to keep track of which value of which password entry we are reading.
                int lValueCounter = 0;

                // We keep reading values as long as there is data left in the file.
                while(lDataBuf.remaining() + lStream.available() >= 8)
                {
                    final byte[] lValueBuf = CodecUtil.readValue(lStream, lDataBuf, lFish).getValue();
                    lValueCounter++;

                    // This is how we see which part of the password record we just read.
                    int lSelection = lValueCounter % 3;

                    // We convert the data into the internal representation.
                    if (lSelection == 0)
                    {
                        // NOTES
                        final PwsFieldImpl lNotesField = new PwsFieldImpl(PwsField.FIELD_NOTES, lValueBuf);
                        lCurrentPwsRec.put(lNotesField);
                    }
                    else if (lSelection == 1)
                    {
                        // TITLE + UID
                        // Create a new record.
                        lCurrentPwsRec = new PwsRecordImpl();
                        lDb.add(lCurrentPwsRec);

                        // Split the first field into title/uid.
                        int lSplitBoundary = 0;
                        for (; (lSplitBoundary < lValueBuf.length) && (lValueBuf[lSplitBoundary] != (byte) 0xAD); lSplitBoundary++) ;
                        if (lSplitBoundary < lValueBuf.length)
                        {
                            final int lTitleLen = lSplitBoundary;
                            final int lLoginLen = lValueBuf.length - lSplitBoundary - 1;

                            if(lTitleLen >=2 && lLoginLen >=2 )
                            {
                                // v1.0 anomaly, spaces used for presentation were added to the data ...
                                // We remove these spaces here.
                                final byte[] lTitle = new byte[lTitleLen - 2];
                                System.arraycopy(lValueBuf, 0, lTitle, 0, lTitleLen - 2);
                                final PwsFieldImpl lTitleField = new PwsFieldImpl(PwsField.FIELD_TITLE, lTitle);
                                lCurrentPwsRec.put(lTitleField);

                                final byte[] lLogin = new byte[lLoginLen - 2];
                                System.arraycopy(lValueBuf, lSplitBoundary + 3, lLogin, 0, lLoginLen - 2);
                                final PwsFieldImpl lLoginField = new PwsFieldImpl(PwsField.FIELD_UID, lLogin);
                                lCurrentPwsRec.put(lLoginField);
                            }
                            else
                            {
                                // Normal case ...
                                // This is the way it should be if it weren't for v 1.0 files.
                                final byte[] lTitle = new byte[lTitleLen];
                                System.arraycopy(lValueBuf, 0, lTitle, 0, lTitleLen);
                                final PwsFieldImpl lTitleField = new PwsFieldImpl(PwsField.FIELD_TITLE, lTitle);
                                lCurrentPwsRec.put(lTitleField);

                                final byte[] lLogin = new byte[lLoginLen];
                                System.arraycopy(lValueBuf, lSplitBoundary + 1, lLogin, 0, lLoginLen);
                                final PwsFieldImpl lLoginField = new PwsFieldImpl(PwsField.FIELD_UID, lLogin);
                                lCurrentPwsRec.put(lLoginField);
                            }
                        }
                        else
                        {
                            // Check if default UID is used.
                            // The default UID has always been an obscure part of the spec.
                            // I could never find a real world example, it must originate in the
                            // earliest versions of the application.
                            // So all in all this piece of code is not used anymore.
                            int lDefaultBoundary = 0;
                            for (; (lDefaultBoundary < lValueBuf.length) && (lValueBuf[lDefaultBoundary] != (byte) 0xA0); lDefaultBoundary++) ;
                            if(lDefaultBoundary < lValueBuf.length)
                            {
                                byte[] lTitle = new byte[lDefaultBoundary];
                                System.arraycopy(lValueBuf, 0, lTitle, 0, lDefaultBoundary);
                                final PwsFieldImpl lTitleField = new PwsFieldImpl(PwsField.FIELD_TITLE, lTitle);
                                lCurrentPwsRec.put(lTitleField);

                                final PwsFieldImpl lLoginField = new PwsFieldImpl(PwsField.FIELD_DEFAULT_UID, new byte[0]);
                                lCurrentPwsRec.put(lLoginField);
                            }
                            else
                            {
                                // Simplest case: only a title, no login.
                                final PwsFieldImpl lTitleField = new PwsFieldImpl(PwsField.FIELD_TITLE, lValueBuf);
                                lCurrentPwsRec.put(lTitleField);
                            }
                        }
                    }
                    else if (lSelection == 2)
                    {
                        // PWD
                        final PwsFieldImpl lPwdField = new PwsFieldImpl(PwsField.FIELD_PWD, lValueBuf);
                        lCurrentPwsRec.put(lPwdField);
                    }
                }
            }
            else
            {
                // No, the passphrase is not correct.
                final String lMsg = "codec02";
                throw new CodecException(lMsg);
            }
        }
        catch (FileNotFoundException eEx)
        {
            // codec00 <filename>
            final String lMsg = "codec00";
            throw new FileCodecException(lMsg, aFile.getAbsolutePath(), eEx);
        }
        catch (IOException eEx)
        {
            // codec01 <filename>
            final String lMsg = "codec01";
            throw new FileCodecException(lMsg, aFile.getAbsolutePath(), eEx);
        }
        catch(BufferUnderflowException eEx)
        {
            // codec01 <filename>
            final String lMsg = "codec01";
            throw new FileCodecException(lMsg, aFile.getAbsolutePath(), eEx);
        }
        finally
        {
            if (lStream != null) try { lStream.close(); } catch (Exception e) { }
        }

        return lDb;
    }

    public void write(PwsDatabase aDb)
    throws CodecException
    {
        // Guarded resource.
        FileOutputStream lStream = null;

        // Get the file where we should write the database.
        // Check the validity.
        File lFile = aDb.getFile();
        if(lFile == null)
        {
            // codec05.
            final String lMsg = "codec05";
            throw new CodecException(lMsg);
        }

        // Generate a new, fresh header.
        final PwsFileHeader lPwsHeader = CodecUtil.initPwsHeader(aDb.getPassphrase());

        // Create the appropriate cipher.
        final Blowfish lFish = CodecUtil.initBlowfish(lPwsHeader, aDb.getPassphrase());

        try
        {
            // Open the file for writing.
            lStream = new FileOutputStream(lFile);

            // Write the header of the file.
            final ByteBuffer lDataBuf = ByteBuffer.allocateDirect(56);
            lDataBuf.order(ByteOrder.LITTLE_ENDIAN);
            lPwsHeader.writeToBuffer(lDataBuf);
            lStream.getChannel().write(lDataBuf);

            // Write all the database records.
            final Iterator lIter = aDb.iterator();
            while(lIter.hasNext())
            {
                final PwsRecord lRecord = (PwsRecord) lIter.next();

                // Write TITLE + LOGIN (UID)
                {
                    byte[] lTitleBuf = null;
                    byte[] lUidBuf = null;
                    byte[] lValueBuf = null;

                    if(lRecord.hasType(PwsField.FIELD_DEFAULT_UID))
                    {
                        // Default UID.
                        if(lRecord.hasType(PwsField.FIELD_TITLE)) lTitleBuf = lRecord.get(PwsField.FIELD_TITLE).getValue();
                        else lTitleBuf = new byte[0];
                        lUidBuf = new byte[]{(byte) 0x20, (byte) 0x20, (byte)0xA0};

                        // Fill the value buffer.
                        lValueBuf = new byte[lTitleBuf.length + lUidBuf.length];
                        System.arraycopy(lTitleBuf, 0, lValueBuf, 0,  lTitleBuf.length);
                        System.arraycopy(lUidBuf, 0, lValueBuf, lTitleBuf.length, lUidBuf.length);
                    }
                    else
                    {
                        // Get the fields.
                        if(lRecord.hasType(PwsField.FIELD_TITLE)) lTitleBuf = lRecord.get(PwsField.FIELD_TITLE).getValue();
                        else lTitleBuf = new byte[0];
                        if(lRecord.hasType(PwsField.FIELD_UID)) lUidBuf = lRecord.get(PwsField.FIELD_UID).getValue();
                        else lUidBuf = new byte[0];

                        // Fill the value buffer.
                        if((lTitleBuf.length > 0) && (lUidBuf.length > 0))
                        {
                            // V 1.0 anomaly. Presentation spaces were added to the title and login data.
                            // We have to reconstruct this here in order to remain compatible.
                            lValueBuf = new byte[lTitleBuf.length + lUidBuf.length + 5];
                            System.arraycopy(lTitleBuf, 0, lValueBuf, 0, lTitleBuf.length);
                            lValueBuf[lTitleBuf.length + 0] = 32;
                            lValueBuf[lTitleBuf.length + 1] = 32;
                            lValueBuf[lTitleBuf.length + 2] = (byte) 0xAD;
                            lValueBuf[lTitleBuf.length + 3] = 32;
                            lValueBuf[lTitleBuf.length + 4] = 32;
                            System.arraycopy(lUidBuf, 0, lValueBuf, lTitleBuf.length + 5,lUidBuf.length);
                        }
                        else if((lTitleBuf.length > 0) && (lUidBuf.length == 0))
                        {
                            // Only title, no uid.
                            lValueBuf = new byte[lTitleBuf.length];
                            System.arraycopy(lTitleBuf, 0, lValueBuf, 0, lTitleBuf.length);
                        }
                        else
                        {
                            // Normal case.
                            // This is the way it should be if it weren't for v 1.0 files.
                            // This case is normally not invoked, I leave it here as a general fallback routine
                            // to handle applications that don't behave as password safe.
                            lValueBuf = new byte[lTitleBuf.length + lUidBuf.length + 1];
                            System.arraycopy(lTitleBuf, 0, lValueBuf, 0, lTitleBuf.length);
                            lValueBuf[lTitleBuf.length + 0] = (byte) 0xAD;
                            System.arraycopy(lUidBuf, 0, lValueBuf, lTitleBuf.length + 1,lUidBuf.length);
                        }
                    }
                    CodecUtil.writeValue(lStream, lDataBuf, lFish, new PwsFieldImpl(PwsField.FIELD_HEADER, lValueBuf));
                }

                // Write PASSWORD
                {
                    byte[] lPwdbuf = null;
                    if(lRecord.hasType(PwsField.FIELD_PWD)) lPwdbuf = lRecord.get(PwsField.FIELD_PWD).getValue();
                    else lPwdbuf = new byte[0];
                    CodecUtil.writeValue(lStream, lDataBuf, lFish, new PwsFieldImpl(PwsField.FIELD_HEADER, lPwdbuf));
                }

                // Write NOTES
                {
                    byte[] lNoteBuf = null;
                    if(lRecord.hasType(PwsField.FIELD_NOTES)) lNoteBuf = lRecord.get(PwsField.FIELD_NOTES).getValue();
                    else lNoteBuf = new byte[0];
                    CodecUtil.writeValue(lStream, lDataBuf, lFish, new PwsFieldImpl(PwsField.FIELD_HEADER, lNoteBuf));
                }
            }

            // Write the data to the file.
            lDataBuf.flip();
            lStream.getChannel().write(lDataBuf);
            lDataBuf.flip();
        }
        catch(ModelException e)
        {
            // codec07 <filename>
            final String lMsg = "codec07";
            throw new CodecException(lMsg, e);
        }
        catch(FileNotFoundException e)
        {
            // codec06 <filename>
            final String lMsg = "codec06";
            throw new FileCodecException(lMsg, lFile.getAbsolutePath(), e);
        }
        catch(IOException e)
        {
            // codec04 <filename>
            final String lMsg = "codec04";
            throw new FileCodecException(lMsg, lFile.getAbsolutePath(), e);
        }
        finally
        {
            if(lStream != null)try{lStream.flush(); lStream.close();}catch(Exception eIgnore){};
        }
    }

    public String getVersion()
    {
        return VERSION;
    }
}