/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 1999 The Apache Software Foundation.  All rights
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
 * 4. The names "Xalan" and "Apache Software Foundation" must
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
 * originally based on software copyright (c) 1999, Lotus
 * Development Corporation., http://www.lotus.com.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */

import org.xml.sax.SAXException;
import org.apache.xalan.xslt.XSLTProcessorFactory;
import org.apache.xalan.xslt.XSLTInputSource;
import org.apache.xalan.xslt.XSLTResultTarget;
import org.apache.xalan.xslt.XSLTProcessor;
import org.apache.xalan.xslt.XSLTEngineImpl;
import org.apache.xalan.xslt.StylesheetRoot;
import org.apache.xalan.xslt.StylesheetHandler;
import org.apache.xerces.parsers.SAXParser;

/**
 * Run the XSLT processor with SAX input for both the XML input and the stylesheet.
 * SAX DocumentHandlers are used to produce the stylesheet tree, the XML input tree,
 * and the transformation result tree.
 *
 * Note: This sample uses some functionality in the processor object (XSLTEngineImpl)
 * that is not exposed through the XSLTProcessor interface.
 */
public class PureSAX
{
	public static void main(String[] args)
    throws Exception
	{
    // Create an XSLT processor, returning an XSLTProcessor interface.
    XSLTProcessor processor = XSLTProcessorFactory.getProcessor();

    // Create a stylesheet using SAX.

    // Instantiate a Xerces SAX parser.
    SAXParser saxparser = new SAXParser();

    // Create an empty StylesheetRoot. The createStylesheetRoot(String baseURI) method is not
    // part of the XSLTProcessor interface, so must use the underlying XSLTEngineImpl object.
    // The baseURI is for resolving relative URIs. If null is sent, defaults to the current
    // directory.
    StylesheetRoot stylesheet = ((XSLTEngineImpl)processor).createStylesheetRoot(null);

    // Set up a StylesheetHandler (a SAX DocumentHandler) to receive events
    // as the Stylesheet is parsed.
    StylesheetHandler stylesheetHandler
      = new StylesheetHandler((XSLTEngineImpl)processor, stylesheet);

    // Set the StylesheetHandler to listen to SAX events from the SAX parser.
    saxparser.setDocumentHandler(stylesheetHandler);

    // Parse foo.xsl, sending SAX events to stylesheetHandler, which fills in the
    // StylesheetRoot object.
    saxparser.parse("foo.xsl");

    // Do a SAX-driven transform.

    // Reset the parser for a new parse.
    saxparser.reset();
    // Set the processor Stylesheet property, telling the processor which stylesheet to use.
    processor.setStylesheet(stylesheet);
    // Set the processor to act as a DocumentHandler, receiving SAX events from the
    // SAX parser.
    saxparser.setDocumentHandler(processor);
    // Set the SAX Parser lexical handler to the XSLTProcessor, so the SAX parser
    // can handle lexical events in the XML source, such as the occurrence of comment nodes.
    saxparser.setProperty("http://xml.org/sax/properties/lexical-handler", processor);
    // Set the processor to output the result to the screen.
    processor.setOutputStream(System.out);
    // Parse foo.xml, sending SAX events to the processor, which sends output
    // to a stream.
    saxparser.parse("foo.xml");
	}
}
