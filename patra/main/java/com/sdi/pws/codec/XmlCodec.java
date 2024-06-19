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

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.util.Iterator;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

public class XmlCodec
implements Codec
{
    public static final String VERSION="XML 1.0";

    public PwsDatabase read(File aFile, String aPassphrase)
    throws CodecException
    {
        try
        {
            final PwsDatabase lDb = new PwsDatabaseImpl();
            lDb.setCodec(new Codec2());

            final DocumentBuilder lBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            final Document lDoc = lBuilder.parse(aFile);
            final NodeList lRecNodes = lDoc.getDocumentElement().getElementsByTagName("record");

            for(int i = 0; i < lRecNodes.getLength(); i++)
            {
                 final PwsRecord lRec = new PwsRecordImpl();
                 lDb.add(lRec);

                final Node lRecNode = lRecNodes.item(i);
                final NodeList lFieldNodes = lRecNode.getChildNodes();

                for(int j = 0; j < lFieldNodes.getLength(); j++)
                {
                    final Node lFieldNode = lFieldNodes.item(j);
                    final String lElName = lFieldNode.getNodeName();

                    String lVal = "";
                    if(lFieldNode.getFirstChild() != null) lVal = lFieldNode.getFirstChild().toString();

                    if("title".equals(lElName)) lRec.put(new PwsFieldImpl(PwsField.FIELD_TITLE, lVal));
                    else if("group".equals(lElName)) lRec.put(new PwsFieldImpl(PwsField.FIELD_GROUP, lVal));
                    else if("uid".equals(lElName)) lRec.put(new PwsFieldImpl(PwsField.FIELD_UID, lVal));
                    else if("pwd".equals(lElName)) lRec.put(new PwsFieldImpl(PwsField.FIELD_PWD, lVal));
                    else if("notes".equals(lElName)) lRec.put(new PwsFieldImpl(PwsField.FIELD_NOTES, lVal));
                }
            }
            return lDb;
        }
        catch(ParserConfigurationException e)
        {
            final String lMsg = "codec01";
            throw new FileCodecException(lMsg, aFile.getAbsolutePath(), e);
        }
        catch(SAXException e)
        {
           final String lMsg = "codec01-c";
           throw new ExplainedCodecException(lMsg, aFile.getAbsolutePath(), e.getMessage(), e);
        }
        catch(IOException e)
        {
            final String lMsg = "codec01";
            throw new FileCodecException(lMsg, aFile.getAbsolutePath(), e);
        }
    }

    public void write(PwsDatabase aDb)
    throws CodecException
    {
        BufferedWriter lWriter = null;
        final File lFile = aDb.getFile();

        try
        {
            lWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(lFile), "UTF-8"));
            lWriter.write("<safe>");
            lWriter.newLine();

            final Iterator lIter = aDb.iterator();
            while (lIter.hasNext())
            {
                final PwsRecord lRec = (PwsRecord) lIter.next();

                try
                {
                    final StringBuilder lBuf = new StringBuilder();
                    lBuf.append("   <record>");

                    if (lRec.hasType(PwsField.FIELD_TITLE))
                        lBuf.append("<title>").append(xmlEncode(lRec.get(PwsField.FIELD_TITLE).getAsString())).append("</title>");
                    if (lRec.hasType(PwsField.FIELD_GROUP))
                        lBuf.append("<group>").append(xmlEncode(lRec.get(PwsField.FIELD_GROUP).getAsString())).append("</group>");
                    if (lRec.hasType(PwsField.FIELD_UID))
                        lBuf.append("<uid>").append(xmlEncode(lRec.get(PwsField.FIELD_UID).getAsString())).append("</uid>");
                    if (lRec.hasType(PwsField.FIELD_NOTES))
                        lBuf.append("<notes>").append(xmlEncode(lRec.get(PwsField.FIELD_NOTES).getAsString())).append("</notes>");
                    if (lRec.hasType(PwsField.FIELD_PWD))
                        lBuf.append("<pwd>").append(xmlEncode(lRec.get(PwsField.FIELD_PWD).getAsString())).append("</pwd>");

                    lBuf.append("</record>");
                    lWriter.write(lBuf.toString());
                    lWriter.newLine();
                }
                catch (ModelException eIgnore) { ; }
            }

            lWriter.write("</safe>");
            lWriter.newLine();
        }
        catch(IOException e)
        {
           final String lMsg = "codec04";
           throw new FileCodecException(lMsg, lFile.getAbsolutePath(), e);
        }
        finally
        {
            if(lWriter != null) try{lWriter.close();}catch(Exception e){;}
        }
    }

    public String getVersion()
    {
        return VERSION;
    }

    /**The escaped lower than symbol. */
    private static final String LT = "&lt;";
    /**The escaped greater than symbol. */
    private static final String GT = "&gt;";
    /**The escaped ampersand symbol. */
    private static final String AMP = "&amp;";

    private String xmlEncode(String str)
    {
        if (str == null) return null;
        int l = str.length();
        if (l == 0) return str;
        StringBuilder result = new StringBuilder((int)( l * 1.1));
        for (int i = 0; i<l; i++)
        {
            char c = str.charAt(i);
            switch(c)
            {
                case '<' :
                    result.append(LT);
                    break;
                case '>' :
                    result.append(GT);
                    break;
                case '&' :
                    result.append(AMP);
                    break;
                default:
                    result.append(c);
                    break;
            }
        }
        return result.toString();
    }
}
