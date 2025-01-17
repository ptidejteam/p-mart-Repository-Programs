/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 2001, 2002 The Apache Software Foundation.  All rights 
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer. 
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:  
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "Xerces" and "Apache Software Foundation" must
 *    not be used to endorse or promote products derived from this
 *    software without prior written permission. For written 
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    nor may "Apache" appear in their name, without prior written
 *    permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation and was
 * originally based on software copyright (c) 1999, International
 * Business Machines, Inc., http://www.apache.org.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */

package xni.parser;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.Reader;
import java.util.StringTokenizer;

import org.apache.xerces.util.AugmentationsImpl;
import org.apache.xerces.util.XMLAttributesImpl;
import org.apache.xerces.util.XMLStringBuffer;

import org.apache.xerces.xni.Augmentations;
import org.apache.xerces.xni.QName;
import org.apache.xerces.xni.XMLAttributes;
import org.apache.xerces.xni.XMLDTDContentModelHandler;
import org.apache.xerces.xni.XMLString;
import org.apache.xerces.xni.XNIException;

import org.apache.xerces.xni.parser.XMLInputSource;

/**
 * This example is a very simple parser configuration that can 
 * parse files with comma-separated values (CSV) to generate XML
 * events. For example, the following CSV document:
 * <pre>
 * Andy Clark,16 Jan 1973,Cincinnati
 * </pre>
 * produces the following XML "document" as represented by the 
 * XNI streaming document information: 
 * <pre>
 * &lt;?xml version='1.0' encoding='UTF-8' standalone='true'?&gt;
 * &lt;!DOCTYPE csv [
 * &lt;!ELEMENT csv (row)*&gt;
 * &lt;!ELEMENT row (col)*&gt;
 * &lt;!ELEMENT col (#PCDATA)&gt;
 * ]&gt;
 * &lt;csv&gt;
 *  &lt;row&gt;
 *   &lt;col&gt;Andy Clark&lt;/col&gt;
 *   &lt;col&gt;16 Jan 1973&lt;/col&gt;
 *   &lt;col&gt;Cincinnati&lt;/col&gt;
 *  &lt;/row&gt;
 * &lt;/csv&gt;
 * </pre>
 * 
 * @author Andy Clark, IBM
 *
 * @version $Id: CSVConfiguration.java,v 1.5 2002/01/29 01:15:06 lehors Exp $
 */
public class CSVConfiguration
    extends AbstractConfiguration {

    //
    // Constants
    //

    /** A QName for the &lt;csv&gt; element name. */
    protected static final QName CSV = new QName(null, null, "csv", null);

    /** A QName for the &lt;row&gt; element name. */
    protected static final QName ROW = new QName(null, null, "row", null);

    /** A QName for the &lt;col&gt; element name. */
    protected static final QName COL = new QName(null, null, "col", null);
    
    /** An empty list of attributes. */
    protected static final XMLAttributes EMPTY_ATTRS = new XMLAttributesImpl();

    /** A newline XMLString. */
    private final XMLString NEWLINE = new XMLStringBuffer("\n");

    /** A newline + one space XMLString. */
    private final XMLString NEWLINE_ONE_SPACE = new XMLStringBuffer("\n ");

    /** A newline + two spaces XMLString. */
    private final XMLString NEWLINE_TWO_SPACES = new XMLStringBuffer("\n  ");

    //
    // Data
    //

    /** 
     * A string buffer for use in copying string into an XMLString
     * object for passing to the characters method.
     */
    private final XMLStringBuffer fStringBuffer = new XMLStringBuffer();

    //
    // XMLParserConfiguration methods
    //

    /**
     * Parse an XML document.
     * <p>
     * The parser can use this method to instruct this configuration
     * to begin parsing an XML document from any valid input source
     * (a character stream, a byte stream, or a URI).
     * <p>
     * Parsers may not invoke this method while a parse is in progress.
     * Once a parse is complete, the parser may then parse another XML
     * document.
     * <p>
     * This method is synchronous: it will not return until parsing
     * has ended.  If a client application wants to terminate 
     * parsing early, it should throw an exception.
     *
     * @param source The input source for the top-level of the
     *               XML document.
     *
     * @exception XNIException Any XNI exception, possibly wrapping 
     *                         another exception.
     * @exception IOException  An IO exception from the parser, possibly
     *                         from a byte stream or character stream
     *                         supplied by the parser.
     */
    public void parse(XMLInputSource source) 
        throws IOException, XNIException {

        // get reader
        openInputSourceStream(source);
        Reader reader = source.getCharacterStream();
        if (reader == null) {
            InputStream stream = source.getByteStream();
            reader = new InputStreamReader(stream);
        }
        BufferedReader bufferedReader = new BufferedReader(reader);

        // start document
        if (fDocumentHandler != null) {
            fDocumentHandler.startDocument(null, "UTF-8", null);
            fDocumentHandler.xmlDecl("1.0", "UTF-8", "true", null);
            fDocumentHandler.doctypeDecl("csv", null, null, null);
        }
        if (fDTDHandler != null) {
            fDTDHandler.startDTD(null, null);
            fDTDHandler.elementDecl("csv", "(row)*", null);
            fDTDHandler.elementDecl("row", "(col)*", null);
            fDTDHandler.elementDecl("col", "(#PCDATA)", null);
        }
        if (fDTDContentModelHandler != null) {
            fDTDContentModelHandler.startContentModel("csv", null);
            fDTDContentModelHandler.startGroup(null);
            fDTDContentModelHandler.element("row", null);
            fDTDContentModelHandler.endGroup(null);
            short csvOccurs = XMLDTDContentModelHandler.OCCURS_ZERO_OR_MORE;
            fDTDContentModelHandler.occurrence(csvOccurs, null);
            fDTDContentModelHandler.endContentModel(null);
            
            fDTDContentModelHandler.startContentModel("row", null);
            fDTDContentModelHandler.startGroup(null);
            fDTDContentModelHandler.element("col", null);
            fDTDContentModelHandler.endGroup(null);
            short rowOccurs = XMLDTDContentModelHandler.OCCURS_ZERO_OR_MORE;
            fDTDContentModelHandler.occurrence(rowOccurs, null);
            fDTDContentModelHandler.endContentModel(null);
        
            fDTDContentModelHandler.startContentModel("col", null);
            fDTDContentModelHandler.startGroup(null);
            fDTDContentModelHandler.pcdata(null);
            fDTDContentModelHandler.endGroup(null);
            fDTDContentModelHandler.endContentModel(null);
        }
        if (fDTDHandler != null) {
            fDTDHandler.endDTD(null);
        }
        if (fDocumentHandler != null) {
            fDocumentHandler.startElement(CSV, EMPTY_ATTRS, null);
        }

        // read lines
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            if (fDocumentHandler != null) {
                fDocumentHandler.ignorableWhitespace(NEWLINE_ONE_SPACE, null);
                fDocumentHandler.startElement(ROW, EMPTY_ATTRS, null);
                StringTokenizer tokenizer = new StringTokenizer(line, ",");
                while (tokenizer.hasMoreTokens()) {
                    fDocumentHandler.ignorableWhitespace(NEWLINE_TWO_SPACES, null);
                    fDocumentHandler.startElement(COL, EMPTY_ATTRS, null);
                    String token = tokenizer.nextToken();
                    fStringBuffer.clear();
                    fStringBuffer.append(token);
                    fDocumentHandler.characters(fStringBuffer, null);
                    fDocumentHandler.endElement(COL, null);
                }
                fDocumentHandler.ignorableWhitespace(NEWLINE_ONE_SPACE, null);
                fDocumentHandler.endElement(ROW, null);
            }
        }
        bufferedReader.close();

        // end document
        if (fDocumentHandler != null) {
            fDocumentHandler.ignorableWhitespace(NEWLINE, null);
            fDocumentHandler.endElement(CSV, null);
            fDocumentHandler.endDocument(null);
        }

    } // parse(XMLInputSource)
    
    // NOTE: The following methods are overloaded to ignore setting
    //       of parser state so that this configuration does not
    //       throw configuration exceptions for features and properties
    //       that it doesn't care about.

    public void setFeature(String featureId, boolean state) {}
    public boolean getFeature(String featureId) { return false; }
    public void setProperty(String propertyId, Object value) {}
    public Object getProperty(String propertyId) { return null; }

} // class CSVConfiguration
