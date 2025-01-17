/*
 * @(#)$Id: NodeSortRecordFactory.java,v 1.1 2006/03/01 21:12:28 vauchers Exp $
 *
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 2001-2003 The Apache Software Foundation.  All rights
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
 * @author Morten Jorgensen
 *
 */

package org.apache.xalan.xsltc.dom;

import org.apache.xalan.xsltc.DOM;
import org.apache.xalan.xsltc.Translet;
import org.apache.xalan.xsltc.TransletException;
import org.apache.xalan.xsltc.runtime.AbstractTranslet;
import org.apache.xalan.xsltc.runtime.TransletLoader;

public class NodeSortRecordFactory {

    private static int DESCENDING = "descending".length();
    private static int NUMBER     = "number".length();

    private final DOM      _dom;
    private final String   _className;
    private Class _class;
    private int   _order[];
    private int   _type[];
    private final AbstractTranslet _translet;

    public Class loadTranslet(String name) throws ClassNotFoundException {
	// First try to load the class using the default class loader
	try {
	    return Class.forName(name);
	}
	catch (ClassNotFoundException e) {
	    // ignore
	}

	// Then try to load the class using the bootstrap class loader
	return new TransletLoader().loadTranslet(name);
    }

    /**
     * Creates a NodeSortRecord producing object. The DOM specifies which tree
     * to get the nodes to sort from, the class name specifies what auxillary
     * class to use to sort the nodes (this class is generated by the Sort
     * class), and the translet parameter is needed for methods called by
     * this object.
     */
    public NodeSortRecordFactory(DOM dom, String className, Translet translet,
				 String order[], String type[])
	throws TransletException {
	try {
	    _dom = dom;
	    _className = className;
	    // This should return a Class definition if using TrAX
	    _class = translet.getAuxiliaryClass(className);
	    // This code is only run when the native API is used
	    if (_class == null) _class = loadTranslet(className);
	    _translet = (AbstractTranslet)translet;

	    int levels = order.length;
	    _order = new int[levels];
	    _type = new int[levels];
	    for (int i = 0; i < levels; i++) {
		if (order[i].length() == DESCENDING)
		    _order[i] = NodeSortRecord.COMPARE_DESCENDING;
		if (type[i].length() == NUMBER)
		    _type[i] = NodeSortRecord.COMPARE_NUMERIC;
	    }
	}
	catch (ClassNotFoundException e) {
	    throw new TransletException(e);
	}
    }

    /**
     * Create an instance of a sub-class of NodeSortRecord. The name of this
     * sub-class is passed to us in the constructor.
     */
    public NodeSortRecord makeNodeSortRecord(int node, int last)
	throws ExceptionInInitializerError,
	       LinkageError,
	       IllegalAccessException,
	       InstantiationException,
	       SecurityException,
	       TransletException {

	final NodeSortRecord sortRecord =
	    (NodeSortRecord)_class.newInstance();
	sortRecord.initialize(node, last, _dom, _translet, _order, _type, this);
	return sortRecord;
    }

    public String getClassName() {
	return _className;
    }
}
