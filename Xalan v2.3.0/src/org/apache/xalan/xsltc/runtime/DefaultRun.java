/*
 * @(#)$Id: DefaultRun.java,v 1.1 2006/03/01 20:58:18 vauchers Exp $
 *
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
 * originally based on software copyright (c) 2001, Sun
 * Microsystems., http://www.sun.com.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 *
 * @author Jacek Ambroziak
 * @author Santiago Pericas-Geertsen
 * @author G. Todd Miller
 * @author Morten Jorgensen
 *
 */

package org.apache.xalan.xsltc.runtime;

import java.io.*;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.util.Vector;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.XMLReader;
import org.xml.sax.SAXException;
import org.xml.sax.ContentHandler;
import org.xml.sax.ext.LexicalHandler;

import org.apache.xalan.xsltc.*;
import org.apache.xalan.xsltc.dom.DOMImpl;
import org.apache.xalan.xsltc.dom.Axis;
import org.apache.xalan.xsltc.dom.DTDMonitor;
import org.apache.xalan.xsltc.runtime.Constants;

final public class DefaultRun {

    private TransletOutputHandler _handler;

    private String  _fileName;
    private String  _className;
    private String  _jarFileSrc;
    private boolean _isJarFileSpecified = false;
    private Vector  _params = null;
    private boolean _uri, _debug;

    AbstractTranslet _translet;

    public DefaultRun(String className, String fileName,
		      boolean uri, boolean debug) {
	_fileName = fileName;
	_className = className;
	_uri = uri;
	_debug = debug;
    }

    public void setParameters(Vector params) {
	_params = params;
    }

    private void setJarFileInputSrc(boolean flag,  String jarFile) {
	// TODO: at this time we do not do anything with this
	// information, attempts to add the jarfile to the CLASSPATH
	// were successful via System.setProperty, but the effects
	// were not visible to the running JVM. For now we add jarfile
	// to CLASSPATH in the wrapper script that calls this program. 
	_isJarFileSpecified = flag;
	// TODO verify jarFile exists...
	_jarFileSrc = jarFile;	
    }

    private void doTransform() {
	try {
	    final Class clazz = Class.forName(_className);
	    final Translet translet = (Translet)clazz.newInstance();

	    // Create a SAX parser and get the XMLReader object it uses
	    final SAXParserFactory factory = SAXParserFactory.newInstance();
	    try {
		factory.setFeature(Constants.NAMESPACE_FEATURE, true);
	    }
	    catch (Exception e) {
		factory.setNamespaceAware(true);
	    }
	    final SAXParser parser = factory.newSAXParser();
	    final XMLReader reader = parser.getXMLReader();

	    // Set the DOM's DOM builder as the XMLReader's SAX2 content handler
	    final DOMImpl dom = new DOMImpl();
	    reader.setContentHandler(dom.getBuilder());
	    // Create a DTD monitor and pass it to the XMLReader object
	    final DTDMonitor dtdMonitor = new DTDMonitor();
	    dtdMonitor.handleDTD(reader);

	    _translet = (AbstractTranslet)translet;
	    dom.setDocumentURI(_fileName);
	    if (_uri)
		reader.parse(_fileName);
	    else {
	        reader.parse(new File(_fileName).toURL().toExternalForm());
	    }
	    
	    // Set size of key/id indices
	    _translet.setIndexSize(dom.getSize());
	    // If there are any elements with ID attributes, build an index
	    dtdMonitor.buildIdIndex(dom, 0, _translet);

	    _translet.setDTDMonitor(dtdMonitor);

	    // Pass global parameters
	    int n = _params.size();
	    for (int i = 0; i < n; i++) {
		Parameter param = (Parameter) _params.elementAt(i);
		translet.addParameter(param._name, param._value);
	    }

	    // Transform the document
	    String encoding = _translet._encoding;

	    DefaultSAXOutputHandler saxHandler =
		new DefaultSAXOutputHandler(System.out, encoding);
	    TextOutput textOutput =
		new TextOutput((ContentHandler)saxHandler,
			       (LexicalHandler)saxHandler, encoding);
	    translet.transform(dom, textOutput);

	    if (_debug) {
		TransletOutputBase handler = new TransletOutputBase();
		long start = System.currentTimeMillis();
		final int nTimes = 100;
		for (int i = 0; i < nTimes; i++)
		    translet.transform(dom, dom.getIterator(), handler);
		long end = System.currentTimeMillis();
		System.out.println("total " + (end - start) + " msec for " 
				   + nTimes + " transformations");
		System.out.println(((double)end - start)/nTimes + " msec avg");
	    }
	}
	catch (TransletException e) {
	    System.err.println("\nTranslet Error: " + e.getMessage());
	    if (_debug) {
		System.err.println(e.toString());
		e.printStackTrace();
	    }
	    doSystemExit(1);	    
	}
	catch (RuntimeException e) {
	    System.err.println("\nRuntime Error: " + e.getMessage());
	    if (_debug) {
		System.err.println(e.toString());
		e.printStackTrace();
	    }
	    doSystemExit(1);
	}
	catch (FileNotFoundException e) {
	    System.err.println("Error: File or URI '"+_fileName+"' not found.");
	    doSystemExit(1);
	}
	catch (MalformedURLException e) {
	    System.err.println("Error: Invalid URI '"+_fileName+"'.");
	    doSystemExit(1);
	}
	catch (ClassNotFoundException e) {
	    System.err.println("Error: Cannot find class '"+_className+"'.");
	    doSystemExit(1);
	}
        catch (UnknownHostException e) {
	    System.err.println("Error: Can't resolve URI specification '"+ 
			       _fileName+"'.");
	    doSystemExit(1);
        }
	catch (Exception e) {
	    e.printStackTrace();
	    System.err.println("Error: internal error.");
	    doSystemExit(1);
	}
    }

    /** If we should call System.exit or not */
    protected static boolean allowSystemExit = true;

    /** Worker method to call System.exit or not */
    protected static void doSystemExit(int retVal) {
        if (allowSystemExit)
            System.exit(retVal);
    }

    private final static String USAGE_STRING =
	"Usage: \n" +
	"     xslt [-j <jarfile>] {-u <document_url> | <document>} <class>\n"+
	"          [<name1>=<value1> ...]\n\n" +
	"           <document> is the xml document to be transformed, or\n" +
	"           <document_url> is a url for the xml document,\n" +
	"           <class> is the translet class which is either in\n" +
	"           user's CLASSPATH or in the <jarfile> specified \n" +
	"           with the -j option.\n" +
	"          also: [-x] (debug), [-s] (don't allow System.exit)";	

    public static void printUsage() {
	System.err.println(USAGE_STRING);
	System.exit(1);
    }

    public static void main(String[] args) {
	try {
	    if (args.length > 0) {
		int i;
		boolean uri = false, debug = false;
		boolean isJarFileSpecified = false;
		String  jarFile = null;

		// Parse options starting with '-'
		for (i = 0; i < args.length && args[i].charAt(0) == '-'; i++) {
		    if (args[i].equals("-u")) {
			uri = true;
		    }
		    else if (args[i].equals("-s")){
			allowSystemExit = false;
		    }
		    else if (args[i].equals("-x")) {
			debug = true;
		    }
		    else if (args[i].equals("-j")) {
			isJarFileSpecified = true;	
			jarFile = args[++i];
		    }
		    else {
			printUsage();
		    }
		}

		// Enough arguments left ?
		if (args.length - i < 2) {
		    printUsage();
		}

		// Get document file and class name
		DefaultRun handler = new DefaultRun(args[i+1], args[i], 
						    uri, debug);
		handler.setJarFileInputSrc(isJarFileSpecified,	jarFile);

		// Parse stylesheet parameters
		Vector params = new Vector();
		for (i += 2; i < args.length; i++) {
		    int equal = args[i].indexOf('=');
		    if (equal > 0) {
			final Parameter param =
			    new Parameter(args[i].substring(0, equal), 
					  args[i].substring(equal + 1,
							    args[i].length()));
			params.addElement(param);
		    }
		    else {
			printUsage();
		    }
		}

		if (i == args.length) {
		    handler.setParameters(params);
		    handler.doTransform();
		    doSystemExit(0);
		}
	    }else{
	       printUsage();
	    }
	}
	catch (Exception e) {
	    e.printStackTrace();
	}
    }
}
