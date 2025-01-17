/*
 * @(#)$Id: TemplatesHandlerImpl.java,v 1.1 2006/03/01 20:54:19 vauchers Exp $
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
 * @author Morten Jorgensen
 *
 */

package org.apache.xalan.xsltc.trax;

import javax.xml.transform.*;
import javax.xml.transform.sax.*;

import org.apache.xalan.xsltc.Translet;
import org.apache.xalan.xsltc.runtime.AbstractTranslet;
import org.apache.xalan.xsltc.compiler.*;
import org.apache.xalan.xsltc.compiler.util.Util;

/**
 * Implementation of a JAXP1.1 TemplatesHandler
 */
public class TemplatesHandlerImpl extends Parser implements TemplatesHandler {

    private String _systemId;

    /**
     * Default constructor
     */
    protected TemplatesHandlerImpl() {
	super(null);
    }

    /**
     * Internal initialization
     */
    public void init() {
	// Create and initialize a stylesheet compiler
	final XSLTC xsltc = new XSLTC();
	super.setXSLTC(xsltc);
	xsltc.setParser(this);
	xsltc.init();
	xsltc.setOutputType(XSLTC.BYTEARRAY_OUTPUT);
    }

    /**
     * Implements javax.xml.transform.sax.TemplatesHandler.getSystemId()
     * Get the base ID (URI or system ID) from where relative URLs will be
     * resolved.
     * @return The systemID that was set with setSystemId(String id)
     */
    public String getSystemId() {
	return _systemId;
    }

    /**
     * Implements javax.xml.transform.sax.TemplatesHandler.setSystemId()
     * Get the base ID (URI or system ID) from where relative URLs will be
     * resolved.
     * @param id Base URI for this stylesheet
     */
    public void setSystemId(String id) {
	_systemId = id;
    }

    /**
     * Implements javax.xml.transform.sax.TemplatesHandler.getTemplates()
     * When a TemplatesHandler object is used as a ContentHandler or
     * DocumentHandler for the parsing of transformation instructions, it
     * creates a Templates object, which the caller can get once the SAX
     * events have been completed.
     * @return The Templates object that was created during the SAX event
     *         process, or null if no Templates object has been created.
     */
    public Templates getTemplates() {

	try {
	    // Create a placeholder for the translet bytecodes
	    byte[][] bytecodes = null;

	    final XSLTC xsltc = getXSLTC();

	    // Set the translet class name if not already set
	    String transletName = TransformerFactoryImpl._defaultTransletName;
	    if (_systemId != null) transletName = Util.baseName(_systemId);
	    xsltc.setClassName(transletName);

	    Stylesheet stylesheet = null;
	    SyntaxTreeNode root = getDocumentRoot();

	    // Compile the translet - this is where the work is done!
	    if ((!errorsFound()) && (root != null)) {
		// Create a Stylesheet element from the root node
		stylesheet = makeStylesheet(root);
		stylesheet.setSystemId(_systemId);
		stylesheet.setParentStylesheet(null);
		setCurrentStylesheet(stylesheet);
		// Create AST under the Stylesheet element (parse & type-check)
		createAST(stylesheet);
	    }

	    // Generate the bytecodes and output the translet class(es)
	    if ((!errorsFound()) && (stylesheet != null)) {
		stylesheet.setMultiDocument(xsltc.isMultiDocument());
		stylesheet.translate();
	    }

	    xsltc.printWarnings();

	    // Check that the transformation went well before returning
	    if (bytecodes == null) {
		xsltc.printErrors();
		return null;
	    }

	    return(new TemplatesImpl(bytecodes, transletName));
	}
	catch (CompilerException e) {
	    return null;
	}
    }
}


