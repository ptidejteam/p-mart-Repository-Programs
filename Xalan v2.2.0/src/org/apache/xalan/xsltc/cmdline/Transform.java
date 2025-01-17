/*
 * @(#)$Id: Transform.java,v 1.1 2006/03/01 20:54:19 vauchers Exp $
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

package org.apache.xalan.xsltc.cmdline;

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

import org.apache.xalan.xsltc.DOM;
import org.apache.xalan.xsltc.Translet;
import org.apache.xalan.xsltc.TransletException;
import org.apache.xalan.xsltc.TransletOutputHandler;

import org.apache.xalan.xsltc.runtime.*;
import org.apache.xalan.xsltc.dom.DOMImpl;
import org.apache.xalan.xsltc.dom.DOMBuilder;
import org.apache.xalan.xsltc.dom.Axis;
import org.apache.xalan.xsltc.dom.DTDMonitor;
import org.apache.xalan.xsltc.compiler.util.ErrorMsg;

final public class Transform {

    private TransletOutputHandler _handler;

    private String  _fileName;
    private String  _className;
    private String  _jarFileSrc;
    private boolean _isJarFileSpecified = false;
    private Vector  _params = null;
    private boolean _uri, _debug;

    private static boolean _allowExit = true;

    public Transform(String className, String fileName,
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

    private Class loadTranslet(String name) throws ClassNotFoundException {
	// First try to load the class using the default class loader
	try {
	    return Class.forName(name);
	}
	catch (ClassNotFoundException e) {
	    // ignore
	}

	// Then try to load the class using the bootstrap class loader
	TransletLoader loader = new TransletLoader();
	return loader.loadTranslet(name);
    }

    private void doTransform() {
	try {
	    
	    final Class clazz = loadTranslet(_className);
	    final Translet translet = (Translet)clazz.newInstance();

	    // Create a SAX parser and get the XMLReader object it uses
	    final SAXParserFactory factory = SAXParserFactory.newInstance();
	    try {
		factory.setFeature(Constants.NAMESPACE_FEATURE,true);
	    }
	    catch (Exception e) {
		factory.setNamespaceAware(true);
	    }
	    final SAXParser parser = factory.newSAXParser();
	    final XMLReader reader = parser.getXMLReader();

	    // Set the DOM's DOM builder as the XMLReader's SAX2 content handler
	    final DOMImpl dom = new DOMImpl();
	    DOMBuilder builder = dom.getBuilder();
	    reader.setContentHandler(builder);

	    try {
		String prop = "http://xml.org/sax/properties/lexical-handler";
		reader.setProperty(prop, builder);
	    }
	    catch (SAXException e) {
		// quitely ignored
	    }
	    
	    // Create a DTD monitor and pass it to the XMLReader object
	    final DTDMonitor dtdMonitor = new DTDMonitor(reader);
	    AbstractTranslet _translet = (AbstractTranslet)translet;
	    dom.setDocumentURI(_fileName);
	    if (_uri)
		reader.parse(_fileName);
	    else
		reader.parse(new File(_fileName).toURL().toExternalForm());

	    builder = null;

	    // If there are any elements with ID attributes, build an index
	    dtdMonitor.buildIdIndex(dom, 0, _translet);
	    // Pass unparsed entity descriptions to the translet
	    _translet.setDTDMonitor(dtdMonitor);

	    // Pass global parameters
	    int n = _params.size();
	    for (int i = 0; i < n; i++) {
		Parameter param = (Parameter) _params.elementAt(i);
		translet.addParameter(param._name, param._value);
	    }

	    // Transform the document
	    String encoding = _translet._encoding;

	    // Create our default SAX/DTD handler
	    DefaultSAXOutputHandler saxHandler =
		new DefaultSAXOutputHandler(System.out, encoding);
	    // Create a translet output handler and plug in the SAX/DTD handler
	    TextOutput textOutput =
		new TextOutput((ContentHandler)saxHandler,
			       (LexicalHandler)saxHandler, encoding);

	    // Transform and pass output to the translet output handler
	    translet.transform(dom, textOutput);
	}
	catch (TransletException e) {
	    if (_debug) e.printStackTrace();
	    System.err.println(ErrorMsg.getTransletErrorMessage()+
			       e.getMessage());
	    if (_allowExit) System.exit(-1);	    
	}
	catch (RuntimeException e) {
	    if (_debug) e.printStackTrace();
	    System.err.println(ErrorMsg.getTransletErrorMessage()+
			       e.getMessage());
	    if (_allowExit) System.exit(-1);
	}
	catch (FileNotFoundException e) {
	    if (_debug) e.printStackTrace();
	    ErrorMsg err = new ErrorMsg(ErrorMsg.FILE_NOT_FOUND_ERR, _fileName);
	    System.err.println(ErrorMsg.getTransletErrorMessage()+
			       err.toString());
	    if (_allowExit) System.exit(-1);
	}
	catch (MalformedURLException e) {
	    if (_debug) e.printStackTrace();
	    ErrorMsg err = new ErrorMsg(ErrorMsg.INVALID_URI_ERR, _fileName);
	    System.err.println(ErrorMsg.getTransletErrorMessage()+
			       err.toString());
	    if (_allowExit) System.exit(-1);
	}
	catch (ClassNotFoundException e) {
	    if (_debug) e.printStackTrace();
	    ErrorMsg err= new ErrorMsg(ErrorMsg.CLASS_NOT_FOUND_ERR,_className);
	    System.err.println(ErrorMsg.getTransletErrorMessage()+
			       err.toString());
	    if (_allowExit) System.exit(-1);
	}
        catch (UnknownHostException e) {
	    if (_debug) e.printStackTrace();
	    ErrorMsg err = new ErrorMsg(ErrorMsg.INVALID_URI_ERR, _fileName);
	    System.err.println(ErrorMsg.getTransletErrorMessage()+
			       err.toString());
	    if (_allowExit) System.exit(-1);
        }
	catch (SAXException e) {
	    Exception ex = e.getException();
	    if (_debug) {
		if (ex != null) ex.printStackTrace();
		e.printStackTrace();
	    }
	    System.err.print(ErrorMsg.getTransletErrorMessage());
	    if (ex != null)
		System.err.println(ex.getMessage());
	    else
		System.err.println(e.getMessage());
	    if (_allowExit) System.exit(-1);
	}
	catch (Exception e) {
	    if (_debug) e.printStackTrace();
	    System.err.println(ErrorMsg.getTransletErrorMessage()+
			       e.getMessage());
	    if (_allowExit) System.exit(-1);
	}
    }

    public static void printUsage() {
	System.err.println(new ErrorMsg(ErrorMsg.TRANSFORM_USAGE_STR));
	if (_allowExit) System.exit(-1);
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
		    else if (args[i].equals("-x")) {
			debug = true;
		    }
		    else if (args[i].equals("-s")) {
			_allowExit = false;
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
		if (args.length - i < 2) printUsage();

		// Get document file and class name
		Transform handler = new Transform(args[i+1],args[i],uri,debug);
		handler.setJarFileInputSrc(isJarFileSpecified,	jarFile);

		// Parse stylesheet parameters
		Vector params = new Vector();
		for (i += 2; i < args.length; i++) {
		    final int equal = args[i].indexOf('=');
		    if (equal > 0) {
			final String name  = args[i].substring(0, equal);
			final String value = args[i].substring(equal+1);
			params.addElement(new Parameter(name, value));
		    }
		    else {
			printUsage();
		    }
		}

		if (i == args.length) {
		    handler.setParameters(params);
		    handler.doTransform();
		    if (_allowExit) System.exit(0);
		}
	    } else {
		printUsage();
	    }
	}
	catch (Exception e) {
	    e.printStackTrace();
	}
    }
}
