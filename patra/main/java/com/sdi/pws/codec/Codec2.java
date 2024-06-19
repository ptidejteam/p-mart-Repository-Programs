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

import com.sdi.pws.db.*;
import com.sdi.crypto.blowfish.*;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.BufferUnderflowException;
import java.util.*;

public class Codec2
implements Codec
{
    public static final String VERSION = "2.0";

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

                // We keep reading values as long as there is data left in the file.
                lCurrentPwsRec = new PwsRecordImpl();

                // Process Format Description Block.
                {
                    PwsField lPwsField = CodecUtil.readValue(lStream, lDataBuf, lFish);
                    if(lPwsField.getAsString().startsWith(" !!!Version 2 File Format!!!"))
                    {
                        // File version information.
                        lPwsField = CodecUtil.readValue(lStream, lDataBuf, lFish);
                        if(!VERSION.equals(lPwsField.getAsString()))
                        {
                            final String lMsg = "codec09";
                            throw new CodecException(lMsg);
                        }
                        // File parameter information.
                        lPwsField = CodecUtil.readValue(lStream, lDataBuf, lFish);
                        lDb.setParameters(lPwsField.getAsString());
                    }
                    else
                    {
                        final String lMsg =  "codec08";
                        throw new CodecException(lMsg);
                    }
                }

                while(lDataBuf.remaining() + lStream.available() >= 8)
                {
                    PwsFieldImpl lPwsField = CodecUtil.readValue(lStream, lDataBuf, lFish);

                    if (lPwsField.getType().equals(PwsField.FIELD_EOR))
                    {
                        // End of record encountered. We don't keep the record delimiter,
                        // we just forget about it.
                        lDb.add(lCurrentPwsRec);
                        lCurrentPwsRec = new PwsRecordImpl();
                    }
                    else
                    {
                        // Value of the current record.
                        lCurrentPwsRec.put(lPwsField);
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
        catch(ModelException e)
        {
            // codec03
            final String lMsg = "codec03";
            throw new CodecException(lMsg);
        }
        catch (FileNotFoundException eEx)
        {
            // codec00 <filename>
            final String lMsg = "codec00";
            throw new FileCodecException(lMsg, aFile.getAbsolutePath(), eEx);
        }
        catch (IOException eEx)
        {
            // codec00 <filename>
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

            // Write Format Description Block.
            CodecUtil.writeValue(lStream, lDataBuf, lFish, new PwsFieldImpl(PwsField.FIELD_HEADER, " !!!Version 2 File Format!!! Please upgrade to PasswordSafe 2.0 or later".getBytes()));
            CodecUtil.writeValue(lStream, lDataBuf, lFish, new PwsFieldImpl(PwsField.FIELD_HEADER, getVersion().getBytes()));
            CodecUtil.writeValue(lStream, lDataBuf, lFish, new PwsFieldImpl(PwsField.FIELD_HEADER, aDb.getParameters()!=null?aDb.getParameters().getBytes():new byte[0]));

            // Write all the database records.
            final Iterator lIter = aDb.iterator();
            while(lIter.hasNext())
            {
                final PwsRecord lRecord = (PwsRecord) lIter.next();
                final Iterator lTypeIter = lRecord.typeIterator();
                while(lTypeIter.hasNext())
                {
                    final Byte lFieldType = (Byte) lTypeIter.next();
                    CodecUtil.writeValue(lStream, lDataBuf, lFish, lRecord.get(lFieldType));
                }
                CodecUtil.writeValue(lStream, lDataBuf, lFish, new PwsFieldImpl(PwsField.FIELD_EOR, new byte[0]));
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