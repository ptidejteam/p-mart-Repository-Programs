/*
 * @(#)$Id: TemplatesImpl.java,v 1.1 2006/03/01 20:58:15 vauchers Exp $
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
 * @author G. Todd Millerj
 * @author Jochen Cordes <Jochen.Cordes@t-online.de>
 *
 */

package org.apache.xalan.xsltc.trax;

import java.io.Serializable;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.IOException;
import java.util.Properties;
import java.security.AccessController;
import java.security.PrivilegedAction;

import javax.xml.transform.*;

import org.apache.xalan.xsltc.Translet;
import org.apache.xalan.xsltc.compiler.*;
import org.apache.xalan.xsltc.runtime.*;
import org.apache.xalan.xsltc.compiler.util.ErrorMsg;

public final class TemplatesImpl implements Templates, Serializable {

    // Contains the name of the main translet class
    private String   _name = null;

    // Contains the actual class definition for the translet class and
    // any auxiliary classes (representing node sort records, predicates, etc.)
    private byte[][] _bytecodes = null;

    // Contains the translet class definition(s). These are created when this
    // Templates is first instanciated or read back from disk (see readObject())
    private Class[]  _class = null;

    // This tells us which index the main translet class has in the _class
    // and _bytecodes arrays (above).
    private int _transletIndex = -1;
    
    // Our own private class loader - builds Class definitions from bytecodes
    private class TransletClassLoader extends ClassLoader {

	protected TransletClassLoader(ClassLoader parent){
	    super(parent);
	}

	public Class defineClass(byte[] b) {
	    return super.defineClass(null, b, 0, b.length);
	}
    }

    public void writeExternal(ObjectOutput out) throws IOException {
	out.writeObject(_name);
	out.writeObject(_bytecodes);
	out.flush();
    }

    public void readExternal(ObjectInput in)
	throws IOException, ClassNotFoundException {
	_name      = (String)in.readObject();
	_bytecodes = (byte[][])in.readObject();
	_class     = null; // must be created again...
    }

    /**
     * The only way to create an XSLTC emplate object
     * The bytecodes for the translet and auxiliary classes, plus the name of
     * the main translet class, must be supplied
     */
    protected TemplatesImpl(byte[][] bytecodes, String transletName) {
	_bytecodes = bytecodes;
	_name      = transletName;
    }

    /**
     * The TransformerFactory must pass us the translet bytecodes using this
     * method before we can create any translet instances
     */
    protected void setTransletBytecodes(byte[][] bytecodes) {
	_bytecodes = bytecodes;
    }

    /**
     * Returns the translet bytecodes stored in this template
     */
    protected byte[][] getTransletBytecodes() {
	return(_bytecodes);
    }

    /**
     * The TransformerFactory should call this method to set the translet name
     */
    protected void setTransletName(String name) {
	_name = name;
    }

    /**
     * Returns the name of the main translet class stored in this template
     */
    protected String getTransletName() {
	return _name;
    }

    /**
     * Defines the translet class and auxiliary classes.
     * Returns a reference to the Class object that defines the main class
     */
    private void defineTransletClasses()
	throws TransformerConfigurationException {

	if (_bytecodes == null) {
	    ErrorMsg err = new ErrorMsg(ErrorMsg.NO_TRANSLET_CLASS_ERR);
	    throw new TransformerConfigurationException(err.toString());
	}

	TransletClassLoader loader = 
	    (TransletClassLoader) AccessController.doPrivileged(
		new PrivilegedAction() {
			public Object run() {
			    ClassLoader current = getClass().getClassLoader();
			    return new TransletClassLoader(current);
			}
		    }
		);

	try {
	    final int classCount = _bytecodes.length;
	    _class = new Class[classCount];

	    for (int i = 0; i < classCount; i++) {
		_class[i] = loader.defineClass(_bytecodes[i]);
		if (_class[i].getName().equals(_name))
		    _transletIndex = i;
	    }

	    if (_transletIndex < 0) {
		ErrorMsg err= new ErrorMsg(ErrorMsg.NO_MAIN_TRANSLET_ERR,_name);
		throw new TransformerConfigurationException(err.toString());
	    }
	}

	catch (ClassFormatError e) {
	    ErrorMsg err = new ErrorMsg(ErrorMsg.TRANSLET_CLASS_ERR+_name);
	    throw new TransformerConfigurationException(err.toString());
	}
	catch (LinkageError e) {
	    ErrorMsg err = new ErrorMsg(ErrorMsg.TRANSLET_OBJECT_ERR+_name);
	    throw new TransformerConfigurationException(err.toString());
	}
    }

    /**
     * This method generates an instance of the translet class that is
     * wrapped inside this Template. The translet instance will later
     * be wrapped inside a Transformer object.
     */
    private Translet getTransletInstance()
	throws TransformerConfigurationException {
	try {
	    if (_name == null) return null;

	    if (_class == null) defineTransletClasses();

	    // The translet needs a reference to all its auxiliary class
	    // definitions so that it can instanciate them on the fly. You
	    // wouldn't think this is necessary, but it seems like the JVM
	    // quickly forgets the classes we define here and the translet
	    // needs to know them as long as it exists.
	    Translet translet = (Translet)_class[_transletIndex].newInstance();
	    final int classCount = _bytecodes.length;
	    for (int i = 0; i < classCount; i++) {
		if (i != _transletIndex)
		    translet.addAuxiliaryClass(_class[i]);
	    }
	    return translet;
	}
	catch (InstantiationException e) {
	    ErrorMsg err = new ErrorMsg(ErrorMsg.TRANSLET_OBJECT_ERR+_name);
	    throw new TransformerConfigurationException(err.toString());
	}
	catch (IllegalAccessException e) {
	    ErrorMsg err = new ErrorMsg(ErrorMsg.TRANSLET_OBJECT_ERR+_name);
	    throw new TransformerConfigurationException(err.toString());
	}
    }

    /**
     * Implements JAXP's Templates.newTransformer()
     *
     * @throws TransformerConfigurationException
     */
    public Transformer newTransformer()
	throws TransformerConfigurationException {
        return(new TransformerImpl(getTransletInstance()));
    }

    /**
     * Implements JAXP's Templates.getOutputProperties()
     */
    public Properties getOutputProperties() { 
	// We need to instanciate a translet to get the output settings, so
	// we might as well just instanciate a Transformer and use its
	// implementation of this method
	try {
	    Transformer transformer = newTransformer();
	    return transformer.getOutputProperties();
	}
	catch (TransformerConfigurationException e) {
	    return null;
	}
    }

}

