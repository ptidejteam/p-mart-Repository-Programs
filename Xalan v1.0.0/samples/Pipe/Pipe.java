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

import org.apache.xalan.xslt.XSLTProcessor;
import org.apache.xalan.xslt.XSLTInputSource;
import org.apache.xalan.xslt.XSLTResultTarget;
import org.apache.xalan.xslt.XSLTProcessorFactory;
import org.apache.xalan.xslt.StylesheetRoot;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * Simple sample code to show how to pipe one transform
 * into another processor.
 */
public class Pipe
{
	public static void main(String[] args)
    throws java.io.IOException,
           java.net.MalformedURLException,
           org.xml.sax.SAXException
	{
    // Use the XSLTProcessorFactory to create a processor.
    XSLTProcessor processor = XSLTProcessorFactory.getProcessor();

    // Compile the two stylesheets.
    StylesheetRoot stylesheet = processor.processStylesheet("foo.xsl");
    StylesheetRoot stylesheet2 = processor.processStylesheet("foo2.xsl");

    // Don't really need to set the processor Stylesheet property, since it's
    // still set from the 2nd processStylesheet, but it's good form....
    processor.setStylesheet(stylesheet2);

    // Get and set a DocumentHandler for final output.
    processor.setDocumentHandler(stylesheet2.getSAXSerializer(System.out));

    // Use the processor (which extends DocumentHandler) to instantiate the
    // XSLTResultTarget object for the first transform.
    XSLTResultTarget firstResult = new XSLTResultTarget(processor);
    // firstResult now functions as a SAX DocumentHandler.

    // The first transform (uses foo.xsl to transform foo.xml) produces a sequence of SAX
    // events (firstResult) that are in turn processed by the processor DocumentHandler
    // (using foo2.xsl), sending the ouput of the second transform to System.out.
    stylesheet.process(new XSLTInputSource("foo.xml"),
                       firstResult);
	}
}
