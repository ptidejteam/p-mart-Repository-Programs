/*
 * @(#)$Id: TransformerFactoryImpl.java,v 1.1 2006/03/01 20:52:27 vauchers Exp $
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
 * @author G. Todd Miller 
 *
 */


package org.apache.xalan.xsltc.runtime;

import javax.xml.transform.Templates; 
import javax.xml.transform.Transformer; 
import javax.xml.transform.ErrorListener; 
import javax.xml.transform.Source; 
import javax.xml.transform.stream.StreamSource; 
import javax.xml.transform.stream.StreamResult; 
import javax.xml.transform.URIResolver; 
import javax.xml.transform.TransformerConfigurationException; 
import javax.xml.transform.sax.SAXTransformerFactory; 
import javax.xml.transform.sax.TemplatesHandler;
import javax.xml.transform.sax.TransformerHandler;

import org.xml.sax.XMLFilter;

import org.apache.xalan.xsltc.Translet;
import org.apache.xalan.xsltc.compiler.XSLTC;
import org.apache.xalan.xsltc.runtime.AbstractTranslet;

import java.io.File;
import java.net.URL;
import java.net.MalformedURLException;


/**
 * Implementation of a JAXP1.1 SAXTransformerFactory for Translets.
 */
public class TransformerFactoryImpl extends SAXTransformerFactory {
    public TransformerFactoryImpl() { /* nothing yet */ }

    ////////////////////////////////////////////////////// 
    // SAXTransformerFactory (subclass of TransformerFactory)
    //
    public TemplatesHandler newTemplatesHandler() 
	throws TransformerConfigurationException 
    { 
	/*TBD*/
	throw new TransformerConfigurationException(
	    "TransformerFactoryImpl:newTemplatesHandler() " +
	    "not implemented yet."); 
	//return null; 
    }
    public TransformerHandler newTransformerHandler() 
	throws TransformerConfigurationException 
    {
	/*TBD*/ 
        throw new TransformerConfigurationException(
            "TransformerFactoryImpl:newTransformerHandler() " +
            "not implemented yet."); 
	// return null; 
    }
    public TransformerHandler newTransformerHandler(Source src) 
	throws TransformerConfigurationException 
    { 
        /*TBD*/ 
        throw new TransformerConfigurationException(
            "TransformerFactoryImpl:newTransformerHandler(Source) " +
            "not implemented yet."); 
	// return null; 
    }
    public TransformerHandler newTransformerHandler(Templates templates) 
	throws TransformerConfigurationException 
    { 
        /*TBD*/ 
        throw new TransformerConfigurationException(
            "TransformerFactoryImpl:newTransformerHandler(Templates) " +
            "not implemented yet."); 
	//return null; 
    }
    public XMLFilter newXMLFilter(Source src) 
	throws TransformerConfigurationException 
    { 
	/*TBD*/ 
        throw new TransformerConfigurationException(
            "TransformerFactoryImpl:newXMLFilter(Source) " +
            "not implemented yet."); 
	//return null; 
    }
    public XMLFilter newXMLFilter(Templates templates) 
	throws TransformerConfigurationException 
    { 
	/*TBD*/ 
        throw new TransformerConfigurationException(
            "TransformerFactoryImpl:newXMLFilter(Templates) " +
            "not implemented yet."); 
	//return null; 
    }
    //
    // End SAXTransformerFactory methods 
    ////////////////////////////////////////////////////// 

    ////////////////////////////////////////////////////// 
    // TransformerFactory
    //
    public ErrorListener getErrorListener() { /*TBD*/ return null; }
    public void setErrorListener(ErrorListener listener) 
	throws IllegalArgumentException
    {
	/*TBD*/   
        throw new IllegalArgumentException(
            "TransformerFactoryImpl:setErrorListener(ErrorListener) " +
            "not implemented yet.");
    }
    public Object getAttribute(String name) 
	throws IllegalArgumentException
    { 
	/*TBD*/ 
        throw new IllegalArgumentException(
            "TransformerFactoryImpl:getAttribute(String) " +
            "not implemented yet.");
	//return null; 
    }
    public void setAttribute(String name, Object value) 
	throws IllegalArgumentException
    { 
	/*TBD*/  
        throw new IllegalArgumentException(
            "TransformerFactoryImpl:getAttribute(String) " +
            "not implemented yet.");
    }
    public boolean getFeature(String name) { 
	if ((StreamSource.FEATURE == name) ||
	    (StreamResult.FEATURE == name) ||
	    (SAXTransformerFactory.FEATURE == name)) {
	    return true;
	} else if ((StreamSource.FEATURE.equals(name))
		|| (StreamResult.FEATURE.equals(name))
		|| (SAXTransformerFactory.FEATURE.equals(name))) {
	    return true;
	} else {
 	    return false; 
	}
    } 
    public URIResolver getURIResolver() { /*TBD*/ return null; } 
    public void setURIResolver(URIResolver resolver) {/*TBD*/   } 
    public Source getAssociatedStylesheet(Source src, String media,
	String title, String charset)  throws TransformerConfigurationException
    { 
	/*TBD*/ 
        throw new TransformerConfigurationException(
            "TransformerFactoryImpl:getAssociatedStylesheet(Source,String," +
            "String, String) not implemented yet.");
	//return null; 
    }
    public Transformer newTransformer() throws
	TransformerConfigurationException 
    { 
	/*TBD*/ 
        throw new TransformerConfigurationException(
            "TransformerFactoryImpl:newTransformer() " +
            " not implemented yet.");
	//return null; 
    }
    //
    // End TransformerFactory methods 
    ////////////////////////////////////////////////////// 


    public Transformer newTransformer(Source stylesheet) throws
	TransformerConfigurationException
    {
	XSLTC xsltc = new XSLTC();
	xsltc.init();
	String stylesheetName = stylesheet.getSystemId();
	int index = stylesheetName.indexOf('.');
	String transletName = stylesheetName.substring(0,index);
	boolean isSuccessful = true;
	try {
	    File file = new File(stylesheetName);
	    URL url = file.toURL();
	    isSuccessful = xsltc.compile(url);
	} catch (MalformedURLException e) {
	    throw new TransformerConfigurationException(
		"URL for stylesheet '" + stylesheetName + 
		"' can not be formed."); 
	}

	if (!isSuccessful) {
	    throw new TransformerConfigurationException(
		"Compilation of stylesheet '" + stylesheetName + "' failed.");
	}

	Translet translet = null;
	try {
	    Class clazz = Class.forName(transletName);
	    translet = (Translet)clazz.newInstance();
	    ((AbstractTranslet)translet).setTransletName(transletName);
	} catch (ClassNotFoundException e) {
	    throw new TransformerConfigurationException(
		"Translet class '" + transletName + "' not found.");
	} catch (InstantiationException e) {
	    throw new TransformerConfigurationException(
		"Translet class '" + transletName + 
		"' could not be instantiated");
	} catch (IllegalAccessException  e) {
	    throw new TransformerConfigurationException(
		"Translet class '" + transletName + "' could not be accessed.");
	}
	return (AbstractTranslet)translet;
    }

    public Templates newTemplates(Source stylesheet) throws
       TransformerConfigurationException 
    {
	return new TransletTemplates(stylesheet);
    } 
}
