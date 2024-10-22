/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 2001 The Apache Software Foundation.  All rights 
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

package xni;                    
                    
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

/***
import sax.helpers.AttributesImpl;

import org.xml.sax.Attributes;
import org.xml.sax.Parser;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;
import org.xml.sax.ext.LexicalHandler;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.ParserAdapter;
import org.xml.sax.helpers.ParserFactory;
import org.xml.sax.helpers.XMLReaderFactory;
/***/
import org.apache.xerces.parsers.XMLDocumentParser;
import org.apache.xerces.util.XMLAttributesImpl;
import org.apache.xerces.xni.QName;
import org.apache.xerces.xni.XMLAttributes;
import org.apache.xerces.xni.XMLLocator;
import org.apache.xerces.xni.XMLString;
import org.apache.xerces.xni.XNIException;
import org.apache.xerces.xni.parser.XMLConfigurationException;
import org.apache.xerces.xni.parser.XMLErrorHandler;
import org.apache.xerces.xni.parser.XMLInputSource;
import org.apache.xerces.xni.parser.XMLParseException;
import org.apache.xerces.xni.parser.XMLParserConfiguration;
/***/

/**
 * A sample XNI writer. This sample program illustrates how to
 * use the XNI XMLDocumentHandler callbacks in order to print a
 * document that is parsed.
 *
 * @author Andy Clark, IBM
 *
 * @version $Id: Writer.java,v 1.2 2001/08/23 00:35:19 lehors Exp $
 */
public class Writer 
    extends XMLDocumentParser 
    implements XMLErrorHandler {

    //
    // Constants
    //

    // feature ids

    /** Namespaces feature id (http://xml.org/sax/features/namespaces). */
    protected static final String NAMESPACES_FEATURE_ID = 
        "http://xml.org/sax/features/namespaces";
    
    /** Validation feature id (http://xml.org/sax/features/validation). */
    protected static final String VALIDATION_FEATURE_ID = 
        "http://xml.org/sax/features/validation";

    /** Schema validation feature id (http://apache.org/xml/features/validation/schema). */
    protected static final String SCHEMA_VALIDATION_FEATURE_ID = 
        "http://apache.org/xml/features/validation/schema";

    // default settings

    /** Default parser configuration (org.apache.xerces.parsers.StandardParserConfiguration). */
    protected static final String DEFAULT_PARSER_CONFIG = 
        "org.apache.xerces.parsers.StandardParserConfiguration";

    /** Default namespaces support (true). */
    protected static final boolean DEFAULT_NAMESPACES = true;

    /** Default validation support (false). */
    protected static final boolean DEFAULT_VALIDATION = false;
    
    /** Default Schema validation support (true). */
    protected static final boolean DEFAULT_SCHEMA_VALIDATION = true;

    /** Default canonical output (false). */
    protected static final boolean DEFAULT_CANONICAL = false;

    //
    // Data
    //

    /** Print writer. */
    protected PrintWriter fOut;

    /** Canonical output. */
    protected boolean fCanonical;

    /** Element depth. */
    protected int fElementDepth;

    /** In DTD. */
    protected boolean fInDTD;

    //
    // Constructors
    //

    /** Default constructor. */
    public Writer(XMLParserConfiguration configuration) {
        super(configuration);
        fConfiguration.setErrorHandler(this);
    } // <init>(XMLParserConfiguration)

    //
    // Public methods
    //

    /** Sets whether output is canonical. */
    public void setCanonical(boolean canonical) {
        fCanonical = canonical;
    } // setCanonical(boolean)

    /** Sets the output stream for printing. */
    public void setOutput(OutputStream stream, String encoding)
        throws UnsupportedEncodingException {

        if (encoding == null) {
            encoding = "UTF8";
        }

        java.io.Writer writer = new OutputStreamWriter(stream, encoding);
        fOut = new PrintWriter(writer);

    } // setOutput(OutputStream,String)

    /** Sets the output writer. */
    public void setOutput(java.io.Writer writer) {
            
        fOut = writer instanceof PrintWriter
             ? (PrintWriter)writer : new PrintWriter(writer);

    } // setOutput(java.io.Writer)

    //
    // XMLDocumentHandler methods
    //

    /** Start document. */
    public void startDocument(XMLLocator locator, String encoding) 
        throws XNIException {

        fElementDepth = 0;
        fInDTD = false;

        if (!fCanonical) {
            fOut.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
            fOut.flush();
        }

    } // startDocument(XMLLocator,String)

    /** Start element. */
    public void startElement(QName element, XMLAttributes attrs) 
        throws XNIException {

        fElementDepth++;
        fOut.print('<');
        fOut.print(element.rawname);
        if (attrs != null) {
            /***
            attrs = sortAttributes(attrs);
            /***/
            int len = attrs.getLength();
            for (int i = 0; i < len; i++) {
                fOut.print(' ');
                fOut.print(attrs.getQName(i));
                fOut.print("=\"");
                normalizeAndPrint(attrs.getValue(i));
                fOut.print('"');
            }
        }
        fOut.print('>');
        fOut.flush();

    } // startElement(QName,XMLAttributes)

    /** Empty element. */
    public void emptyElement(QName element, XMLAttributes attrs) 
        throws XNIException {

        fElementDepth++;
        fOut.print('<');
        fOut.print(element.rawname);
        if (attrs != null) {
            /***
            attrs = sortAttributes(attrs);
            /***/
            int len = attrs.getLength();
            for (int i = 0; i < len; i++) {
                fOut.print(' ');
                fOut.print(attrs.getQName(i));
                fOut.print("=\"");
                normalizeAndPrint(attrs.getValue(i));
                fOut.print('"');
            }
        }
        fOut.print("/>");
        fOut.flush();

    } // emptyElement(QName,XMLAttributes)

    /** Characters. */
    public void characters(XMLString text) throws XNIException {

        normalizeAndPrint(text);
        fOut.flush();

    } // characters(XMLString);

    /** Ignorable whitespace. */
    public void ignorableWhitespace(XMLString text) throws XNIException {

        characters(text);
        fOut.flush();

    } // ignorableWhitespace(XMLString);

    /** End element. */
    public void endElement(QName element) throws XNIException {

        fElementDepth--;
        fOut.print("</");
        fOut.print(element.rawname);
        fOut.print('>');
        fOut.flush();

    } // endElement(QName)

    /** Start CDATA section. */
    public void startCDATA() throws XNIException {
    } // startCDATA()

    /** End CDATA section. */
    public void endCDATA() throws XNIException {
    } // endCDATA()

    //
    // Shared XMLDocumentHandler and XMLDTDHandler methods
    //

    /** Processing instruction. */
    public void processingInstruction(String target, XMLString data) 
        throws XNIException {

        if (fElementDepth > 0) {
            fOut.print("<?");
            fOut.print(target);
            if (data != null && data.length > 0) {
                fOut.print(' ');
                fOut.print(data.toString());
            }
            fOut.print("?>");
            fOut.flush();
        }

    } // processingInstruction(String,XMLString)

    /** Comment. */
    public void comment(XMLString text) throws XNIException {
        if (!fCanonical && fElementDepth > 0) {
            fOut.print("<!--");
            normalizeAndPrint(text);
            fOut.print("-->");
            fOut.flush();
        }
    } // comment(XMLString)

    /** Start entity. */
    public void startEntity(String name, 
                            String publicId, String systemId,
                            String baseSystemId) throws XNIException {
    } // startEntity(String,String,String,String)

    /** End entity. */
    public void endEntity(String name) throws XNIException {
    } // endEntity(String)

    //
    // XMLDTDHandler methods
    //

    /** Start DTD. */
    public void startDTD(XMLLocator locator) throws XNIException {
        fInDTD = true;
    } // startDTD(XMLLocator)

    /** End DTD. */
    public void endDTD() throws XNIException {
        fInDTD = false;
    } // endDTD()

    //
    // XMLErrorHandler methods
    //

    /** Warning. */
    public void warning(String domain, String key, XMLParseException ex) 
        throws XNIException {
        printError("Warning", ex);
    } // warning(String,String,XMLParseException)

    /** Error. */
    public void error(String domain, String key, XMLParseException ex) 
        throws XNIException {
        printError("Error", ex);
    } // error(String,String,XMLParseException)

    /** Fatal error. */
    public void fatalError(String domain, String key, XMLParseException ex) 
        throws XNIException {
        printError("Fatal Error", ex);
        throw ex;
    } // fatalError(String,String,XMLParseException)

    //
    // Protected methods
    //

    /** Returns a sorted list of attributes. */
    /***
    protected XMLAttributes sortAttributes(XMLAttributes attrs) {

        XMLAttributesImpl attributes = new XMLAttributesImpl();

        int len = (attrs != null) ? attrs.getLength() : 0;
        for (int i = 0; i < len; i++) {
            String name = attrs.getQName(i);
            int count = attributes.getLength();
            int j = 0;
            while (j < count) {
                if (name.compareTo(attributes.getQName(j)) < 0) {
                    break;
                }
                j++;
            }
            attributes.insertAttributeAt(j, name, attrs.getType(i), 
                                         attrs.getValue(i));
        }

        return attributes;

    } // sortAttributes(XMLAttributeList):XMLAttributeList
    /***/

    /** Normalizes and prints the given string. */
    protected void normalizeAndPrint(String s) {

        int len = (s != null) ? s.length() : 0;
        for (int i = 0; i < len; i++) {
            char c = s.charAt(i);
            normalizeAndPrint(c);
        }

    } // normalizeAndPrint(String)

    /** Normalizes and prints the given array of characters. */
    protected void normalizeAndPrint(XMLString text) {
        for (int i = 0; i < text.length; i++) {
            normalizeAndPrint(text.ch[text.offset + i]);
        }
    } // normalizeAndPrint(XMLString)
    
    /** Normalizes and print the given character. */
    protected void normalizeAndPrint(char c) {

        switch (c) {
            case '<': {
                fOut.print("&lt;");
                break;
            }
            case '>': {
                fOut.print("&gt;");
                break;
            }
            case '&': {
                fOut.print("&amp;");
                break;
            }
            case '"': {
                fOut.print("&quot;");
                break;
            }
            case '\r':
            case '\n': {
                if (fCanonical) {
                    fOut.print("&#");
                    fOut.print(Integer.toString(c));
                    fOut.print(';');
                    break;
                }
                // else, default print char
            }
            default: {
                fOut.print(c);
            }
        }

    } // normalizeAndPrint(char)

    /** Prints the error message. */
    protected void printError(String type, XMLParseException ex) {

        System.err.print("[");
        System.err.print(type);
        System.err.print("] ");
        String systemId = ex.getSystemId();
        if (systemId != null) {
            int index = systemId.lastIndexOf('/');
            if (index != -1)
                systemId = systemId.substring(index + 1);
            System.err.print(systemId);
        }
        System.err.print(':');
        System.err.print(ex.getLineNumber());
        System.err.print(':');
        System.err.print(ex.getColumnNumber());
        System.err.print(": ");
        System.err.print(ex.getMessage());
        System.err.println();
        System.err.flush();

    } // printError(String,XMLParseException)

    //
    // Main
    //

    /** Main program entry point. */
    public static void main(String argv[]) {
        
        // is there anything to do?
        if (argv.length == 0) {
            printUsage();
            System.exit(1);
        }

        // variables
        Writer writer = null;
        XMLParserConfiguration parserConfig = null;
        boolean namespaces = DEFAULT_NAMESPACES;
        boolean validation = DEFAULT_VALIDATION;
        boolean schemaValidation = DEFAULT_SCHEMA_VALIDATION;
        boolean canonical = DEFAULT_CANONICAL;
        
        // process arguments
        for (int i = 0; i < argv.length; i++) {
            String arg = argv[i];
            if (arg.startsWith("-")) {
                String option = arg.substring(1);
                if (option.equals("p")) {
                    // get parser name
                    if (++i == argv.length) {
                        System.err.println("error: Missing argument to -p option.");
                    }
                    String parserName = argv[i];

                    // create parser
                    try {
                        parserConfig = (XMLParserConfiguration)Class.forName(parserName).newInstance();
                        /***
                        parserConfig.addRecognizedFeatures(new String[] {
                            NAMESPACE_PREFIXES_FEATURE_ID,
                        });
                        /***/
                        writer = null;
                    }
                    catch (Exception e) {
                        parserConfig = null;
                        System.err.println("error: Unable to instantiate parser configuration ("+parserName+")");
                    }
                    continue;
                }
                if (option.equalsIgnoreCase("n")) {
                    namespaces = option.equals("n");
                    continue;
                }
                if (option.equalsIgnoreCase("v")) {
                    validation = option.equals("v");
                    continue;
                }
                if (option.equalsIgnoreCase("s")) {
                    schemaValidation = option.equals("s");
                    continue;
                }
                if (option.equalsIgnoreCase("c")) {
                    canonical = option.equals("c");
                    continue;
                }
                if (option.equals("h")) {
                    printUsage();
                    continue;
                }
            }

            // use default parser?
            if (parserConfig == null) {

                // create parser
                try {
                    parserConfig = (XMLParserConfiguration)Class.forName(DEFAULT_PARSER_CONFIG).newInstance();
                    /***
                    parserConfig.addRecognizedFeatures(new String[] {
                        NAMESPACE_PREFIXES_FEATURE_ID,
                    });
                    /***/
                }
                catch (Exception e) {
                    System.err.println("error: Unable to instantiate parser configuration ("+DEFAULT_PARSER_CONFIG+")");
                    continue;
                }
            }
        
            // set parser features
            if (writer == null) {
                writer = new Writer(parserConfig);
            }
            try {
                parserConfig.setFeature(NAMESPACES_FEATURE_ID, namespaces);
            }
            catch (XMLConfigurationException e) {
                System.err.println("warning: Parser does not support feature ("+NAMESPACES_FEATURE_ID+")");
            }
            try {
                parserConfig.setFeature(VALIDATION_FEATURE_ID, validation);
            }
            catch (XMLConfigurationException e) {
                System.err.println("warning: Parser does not support feature ("+VALIDATION_FEATURE_ID+")");
            }
            try {
                parserConfig.setFeature(SCHEMA_VALIDATION_FEATURE_ID, schemaValidation);
            }
            catch (XMLConfigurationException e) {
                if (e.getType() == XMLConfigurationException.NOT_SUPPORTED) {
                    System.err.println("warning: Parser does not support feature ("+SCHEMA_VALIDATION_FEATURE_ID+")");
                }
            }

            // parse file
            try {
                writer.setOutput(System.out, "UTF8");
            }
            catch (UnsupportedEncodingException e) {
                System.err.println("error: Unable to set output. Exiting.");
                System.exit(1);
            }
            writer.setCanonical(canonical);
            try {
                writer.parse(new XMLInputSource(null, arg, null));
            }
            catch (XMLParseException e) {
                // ignore
            }
            catch (Exception e) {
                System.err.println("error: Parse error occurred - "+e.getMessage());
                if (e instanceof XNIException) {
                    e = ((XNIException)e).getException();
                }
                e.printStackTrace(System.err);
            }
        }

    } // main(String[])

    //
    // Private static methods
    //

    /** Prints the usage. */
    private static void printUsage() {

        System.err.println("usage: java sax.Writer (options) uri ...");
        System.err.println();
        
        System.err.println("options:");
        System.err.println("  -p name  Select parser configuration by name.");
        System.err.println("  -n | -N  Turn on/off namespace processing.");
        System.err.println("  -v | -V  Turn on/off validation.");
        System.err.println("  -s | -S  Turn on/off Schema validation support.");
        System.err.println("           NOTE: Not supported by all parsers.");
        /***
        System.err.println("  -c | -C  Turn on/off Canonical XML output.");
        System.err.println("           NOTE: This is not W3C canonical output.");
        /***/
        System.err.println("  -h       This help screen.");
        System.err.println();

        System.err.println("defaults:");
        System.err.println("  Config:     "+DEFAULT_PARSER_CONFIG);
        System.err.print("  Namespaces: ");
        System.err.println(DEFAULT_NAMESPACES ? "on" : "off");
        System.err.print("  Validation: ");
        System.err.println(DEFAULT_VALIDATION ? "on" : "off");
        System.err.print("  Schema:     ");
        System.err.println(DEFAULT_SCHEMA_VALIDATION ? "on" : "off");
        /***
        System.err.print("  Canonical:  ");
        System.err.println(DEFAULT_CANONICAL ? "on" : "off");
        /***/

    } // printUsage()

} // class Writer
