/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 2000,2001 The Apache Software Foundation.  All rights 
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

package socket;

import java.io.FileInputStream;
import java.io.FilterInputStream;
import java.io.InputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Random;

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.Parser;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.ParserAdapter;
import org.xml.sax.helpers.ParserFactory;
import org.xml.sax.helpers.XMLReaderFactory;

/**
 * This sample delays the input to the SAX parser to simulate reading data
 * from a socket where data is not always immediately available. An XML
 * parser should be able to parse the input and perform the necessary
 * callbacks as data becomes available. So this is a good way to test
 * any parser that implements the SAX2 <code>XMLReader</code> interface
 * to see if it can parse data as it arrives.
 * <p>
 * <strong>Note:</strong> This sample uses NSGMLS-like output of elements
 * and attributes interspersed with information about how many bytes are
 * being read at a time.
 *
 * @author Andy Clark, IBM
 *
 * @version $Id: DelayedInput.java,v 1.2 2001/08/23 00:35:18 lehors Exp $
 */
public class DelayedInput
    extends DefaultHandler {

    //
    // Constants
    //

    // feature ids

    /** Namespaces feature id (http://xml.org/sax/features/namespaces). */
    protected static final String NAMESPACES_FEATURE_ID = "http://xml.org/sax/features/namespaces";
    
    /** Validation feature id (http://xml.org/sax/features/validation). */
    protected static final String VALIDATION_FEATURE_ID = "http://xml.org/sax/features/validation";

    /** Schema validation feature id (http://apache.org/xml/features/validation/schema). */
    protected static final String SCHEMA_VALIDATION_FEATURE_ID = "http://apache.org/xml/features/validation/schema";

    // default settings

    /** Default parser name. */
    protected static final String DEFAULT_PARSER_NAME = "org.apache.xerces.parsers.SAXParser";

    /** Default namespaces support (true). */
    protected static final boolean DEFAULT_NAMESPACES = true;

    /** Default validation support (false). */
    protected static final boolean DEFAULT_VALIDATION = false;
    
    /** Default Schema validation support (true). */
    protected static final boolean DEFAULT_SCHEMA_VALIDATION = true;

    //
    // Data
    //

    /** Print writer. */
    protected PrintWriter fOut;

    //
    // Constructors
    //

    /** Default constructor. */
    public DelayedInput() {
    } // <init>()

    //
    // ContentHandler methods
    //

    /** Start element. */
    public void startElement(String uri, String localpart, String rawname,
                             Attributes attrs) throws SAXException {

        System.out.println("("+rawname);
        int length = attrs != null ? attrs.getLength() : 0;
        for (int i = 0; i < length; i++) {
            System.out.println("A"+attrs.getQName(i)+' '+attrs.getValue(i));
        }

    } // startElement(String,String,String,Attributes)

    /** End element. */
    public void endElement(String uri, String localpart, String rawname) 
        throws SAXException {
        System.out.println(")"+rawname);
    } // endElement(String,String,String)

    //
    // ErrorHandler methods
    //

    /** Warning. */
    public void warning(SAXParseException ex) throws SAXException {
        printError("Warning", ex);
    } // warning(SAXParseException)

    /** Error. */
    public void error(SAXParseException ex) throws SAXException {
        printError("Error", ex);
    } // error(SAXParseException)

    /** Fatal error. */
    public void fatalError(SAXParseException ex) throws SAXException {
        printError("Fatal Error", ex);
        throw ex;
    } // fatalError(SAXParseException)

    //
    // Protected methods
    //

    /** Prints the error message. */
    protected void printError(String type, SAXParseException ex) {

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

    } // printError(String,SAXParseException)

    //
    // Classes
    //

    /**
     * Delayed input stream filter. This class will limit block reads to a small
     * number of bytes (suitable for display on a standard 80 column terminal)
     * pausing in small increments, randomly. This lets you can verify that the
     * parser can parse the input and make the appropriate callbacks as the
     * data arrives.
     *
     * @author Andy Clark, IBM
     */
    static class DelayedInputStream
        extends FilterInputStream {

        //
        // Data
        //

        /** Random number generator. */
        private Random fRandom = new Random(System.currentTimeMillis());

        //
        // Constructors
        //

        /** Constructs a delayed input stream from the specified input stream. */
        public DelayedInputStream(InputStream in) {
            super(in);
        } // <init>(InputStream)

        //
        // InputStream methods
        //

        /** Performs a delayed block read. */
        public int read(byte[] buffer, int offset, int length) throws IOException {

            // keep read small enough for display
            if (length > 48) {
                length = 48;
            }
            int count = 0;

            // read bytes and pause
            long before = System.currentTimeMillis();
            count = in.read(buffer, offset, length);
            try {
                Thread.currentThread().sleep(Math.abs(fRandom.nextInt()) % 2000);
            }
            catch (InterruptedException e) {
                e.printStackTrace(System.err);
            }
            long after = System.currentTimeMillis();

            // print output
            System.out.print("read "+count+" bytes in "+(after-before)+" ms: ");
            printBuffer(buffer, offset, count);
            System.out.println();

            // return number of characters read
            return count;

        } // read(byte[],int,int):int

        //
        // Private methods
        //

        /** Prints the specified buffer. */
        private void printBuffer(byte[] buffer, int offset, int length) {

            // is there anything to do?
            if (length <= 0) {
                System.out.print("no data read");
                return;
            }

            // print buffer
            System.out.print('[');
            for (int i = 0; i < length; i++) {
                switch ((char)buffer[offset + i]) {
                    case '\r': {
                        System.out.print("\\r");
                        break;
                    }
                    case '\n': {
                        System.out.print("\\n");
                        break;
                    }
                    default: {
                        System.out.print((char)buffer[offset + i]);
                    }
                }
            }
            System.out.print(']');

        } // printBuffer(byte[],int,int)

    } // class DelayedInputStream

    //
    // MAIN
    //

    /** Main program entry point. */
    public static void main(String argv[]) {
        
        // is there anything to do?
        if (argv.length == 0) {
            printUsage();
            System.exit(1);
        }

        // variables
        DefaultHandler handler = new DelayedInput();
        PrintWriter out = new PrintWriter(System.out);
        XMLReader parser = null;
        boolean namespaces = DEFAULT_NAMESPACES;
        boolean validation = DEFAULT_VALIDATION;
        boolean schemaValidation = DEFAULT_SCHEMA_VALIDATION;
        
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
                        parser = XMLReaderFactory.createXMLReader(parserName);
                    }
                    catch (Exception e) {
                        try {
                            Parser sax1Parser = ParserFactory.makeParser(parserName);
                            parser = new ParserAdapter(sax1Parser);
                            System.err.println("warning: Features and properties not supported on SAX1 parsers.");
                        }
                        catch (Exception ex) {
                            parser = null;
                            System.err.println("error: Unable to instantiate parser ("+DEFAULT_PARSER_NAME+")");
                        }
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
                if (option.equals("h")) {
                    printUsage();
                    continue;
                }
            }

            // use default parser?
            if (parser == null) {

                // create parser
                try {
                    parser = XMLReaderFactory.createXMLReader(DEFAULT_PARSER_NAME);
                }
                catch (Exception e) {
                    System.err.println("error: Unable to instantiate parser ("+DEFAULT_PARSER_NAME+")");
                    continue;
                }
            }
        
            // set parser features
            try {
                parser.setFeature(NAMESPACES_FEATURE_ID, namespaces);
            }
            catch (SAXException e) {
                System.err.println("warning: Parser does not support feature ("+NAMESPACES_FEATURE_ID+")");
            }
            try {
                parser.setFeature(VALIDATION_FEATURE_ID, validation);
            }
            catch (SAXException e) {
                System.err.println("warning: Parser does not support feature ("+VALIDATION_FEATURE_ID+")");
            }
            try {
                parser.setFeature(SCHEMA_VALIDATION_FEATURE_ID, schemaValidation);
            }
            catch (SAXNotRecognizedException e) {
                // ignore
            }
            catch (SAXNotSupportedException e) {
                System.err.println("warning: Parser does not support feature ("+SCHEMA_VALIDATION_FEATURE_ID+")");
            }

            // parse file
            parser.setContentHandler(handler);
            parser.setErrorHandler(handler);
            try {
                System.out.println("# filename: "+arg);
                InputStream stream = new DelayedInputStream(new FileInputStream(arg));
                InputSource source = new InputSource(stream);
                source.setSystemId(arg);
                parser.parse(source);
            }
            catch (SAXParseException e) {
                // ignore
            }
            catch (Exception e) {
                System.err.println("error: Parse error occurred - "+e.getMessage());
                if (e instanceof SAXException) {
                    e = ((SAXException)e).getException();
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

        System.err.println("usage: java socket.DelayedInput (options) filename ...");
        System.err.println();
        
        System.err.println("options:");
        System.err.println("  -p name  Select parser by name.");
        System.err.println("  -n | -N  Turn on/off namespace processing.");
        System.err.println("  -v | -V  Turn on/off validation.");
        System.err.println("  -s | -S  Turn on/off Schema validation support.");
        System.err.println("           NOTE: Not supported by all parsers.");
        System.err.println("  -h       This help screen.");
        System.err.println();

        System.err.println("defaults:");
        System.err.println("  Parser:     "+DEFAULT_PARSER_NAME);
        System.err.print("  Namespaces: ");
        System.err.println(DEFAULT_NAMESPACES ? "on" : "off");
        System.err.print("  Validation: ");
        System.err.println(DEFAULT_VALIDATION ? "on" : "off");
        System.err.print("  Schema:     ");
        System.err.println(DEFAULT_SCHEMA_VALIDATION ? "on" : "off");

    } // printUsage()

} // class DelayedInput
