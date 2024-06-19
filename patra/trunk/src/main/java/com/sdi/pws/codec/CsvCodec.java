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

import java.io.*;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;

public class CsvCodec
implements Codec
{
    public static final String VERSION="CSV 1.0";
    private static final String EMPTY="\"\"";

    public PwsDatabase read(File aFile, String aPassphrase) throws CodecException
    {
        // Create new, empty database.
        // Initialize to the latest codec we know.
        PwsDatabase lDb = new PwsDatabaseImpl();
        lDb.setCodec(new Codec2());

        LineNumberReader lReader = null;
        try
        {
            lReader = new LineNumberReader(new FileReader(aFile));
            String lLine = lReader.readLine();
            while(lLine != null)
            {
                // Prepare an empty record.
                final PwsRecord lRec = new PwsRecordImpl();
                lDb.add(lRec);

                // Split the line into its fields.
                StreamTokenizer lTokenizer = new StreamTokenizer(new StringReader(lLine));
                List lTokens = new ArrayList(5);
                while(lTokenizer.nextToken() != StreamTokenizer.TT_EOF)
                {
                    if(lTokenizer.sval != null)
                    {
                        final String lToken = lTokenizer.sval.trim();
                        if(",".equals(lToken)){;}
                        else lTokens.add(lToken);
                    }
                }

                if(lTokens.size() != 5)
                {
                    final String lMsg = "codec01-b";
                    throw new LineCodecException(lMsg, aFile.getAbsolutePath(), "" + lReader.getLineNumber());
                }


                // Parse each field.
                final String lTitle = this.csvDecode((String)lTokens.get(0));
                if(lTitle != null) lRec.put(new PwsFieldImpl(PwsField.FIELD_TITLE, lTitle));

                final String lGroup = this.csvDecode((String)lTokens.get(1));
                if(lGroup != null) lRec.put(new PwsFieldImpl(PwsField.FIELD_GROUP, lGroup));

                final String lUid = this.csvDecode((String)lTokens.get(2));
                if(lUid != null) lRec.put(new PwsFieldImpl(PwsField.FIELD_UID, lUid));

                final String lNotes = this.csvDecode((String)lTokens.get(3));
                if(lNotes != null) lRec.put(new PwsFieldImpl(PwsField.FIELD_NOTES, lNotes));

                final String lPwd = this.csvDecode((String)lTokens.get(4));
                if(lPwd != null) lRec.put(new PwsFieldImpl(PwsField.FIELD_PWD, lPwd));

                // Finally we can read the next line.
                lLine = lReader.readLine();
            }
        }
        catch(FileNotFoundException e)
        {
            final String lMsg = "codec00";
            throw new FileCodecException(lMsg, aFile.getAbsolutePath(), e);
        }
        catch(IOException e)
        {
            final String lMsg = "codec01";
            throw new FileCodecException(lMsg, aFile.getAbsolutePath(), e);
        }
        finally
        {
            if(lReader != null) try{lReader.close();}catch(IOException e){;}
        }
        return lDb;
    }

    public void write(PwsDatabase aDb)
    throws CodecException
    {
        BufferedWriter lWriter = null;
        final File lFile = aDb.getFile();

        try
        {
            lWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(lFile)));

            final Iterator lIter = aDb.iterator();
            while(lIter.hasNext())
            {
                try
                {
                    final StringBuilder lBuf = new StringBuilder();
                    final PwsRecord lRec = (PwsRecord) lIter.next();

                    if(lRec.hasType(PwsField.FIELD_TITLE))
                        lBuf.append(csvEncode(lRec.get(PwsField.FIELD_TITLE).getAsString()));
                    else lBuf.append(EMPTY);

                    lBuf.append(", \t");

                    if(lRec.hasType(PwsField.FIELD_GROUP))
                        lBuf.append(csvEncode(lRec.get(PwsField.FIELD_GROUP).getAsString()));
                    else lBuf.append(EMPTY);

                    lBuf.append(", \t");

                    if(lRec.hasType(PwsField.FIELD_UID))
                        lBuf.append(csvEncode(lRec.get(PwsField.FIELD_UID).getAsString()));
                    else lBuf.append(EMPTY);

                    lBuf.append(", \t");

                    if(lRec.hasType(PwsField.FIELD_NOTES))
                        lBuf.append(csvEncode(lRec.get(PwsField.FIELD_NOTES).getAsString()));
                    else lBuf.append(EMPTY);

                    lBuf.append(", \t");

                    if(lRec.hasType(PwsField.FIELD_PWD))
                        lBuf.append(csvEncode(lRec.get(PwsField.FIELD_PWD).getAsString()));
                    else lBuf.append(EMPTY);

                    lWriter.write(lBuf.toString());
                    lWriter.newLine();
                }
                catch(ModelException eIgnore)
                {;}
            }
        }
        catch(FileNotFoundException e)
        {
            final String lMsg = "codec06";
            throw new FileCodecException(lMsg, lFile.getAbsolutePath(), e);
        }
        catch(IOException e)
        {
            final String lMsg = "codec04";
            throw new FileCodecException(lMsg, lFile.getAbsolutePath(), e);
        }
        finally
        {
            if(lWriter != null) try { lWriter.close(); } catch (Exception eIgnore){;}
        }
    }

    // Replace '\n' with characters.
    // Replace '"' with '\"'
    // Surround with quotes.
    public String csvEncode(String aString)
    {
        final StringBuilder lBuf = new StringBuilder();
        for(int i = 0; i < aString.length(); i++)
        {
            char lChar = aString.charAt(i);
            if(lChar == '\\') lBuf.append("\\\\");
            else if(lChar == '\r'){;} // Ignore carriage returns.
            else if(lChar == '\n') lBuf.append('\\').append('n');
            else if(lChar == '"') lBuf.append("\\\"");
            else lBuf.append(lChar);
        }
        lBuf.insert(0, '\"');
        lBuf.append('\"');
        return lBuf.toString();
    }

    // Trim.
    // Delete quotes on both ends.
    public String csvDecode(String aString)
    {
        aString = aString + "@";
        StringBuilder lBuf = new StringBuilder();
        for(int i = 0; i < aString.length() - 1; i++)
        {
            char lChar = aString.charAt(i);
            char lNext = aString.charAt(i+1);
            if(lChar == '\\')
            {
                if(lNext == '\\')
                {
                    lBuf.append('\\');
                    i++;
                }
                else if(lNext == 'n')
                {
                    lBuf.append('\n');
                    i++;
                }
                else if(lNext == '\"')
                {
                    lBuf.append('\"');
                    i++;
                }
                else lBuf.append('\\');
            }
            else lBuf.append(lChar);
        }
        return lBuf.toString();
    }

    public String getVersion()
    {
        return VERSION;
    }
}