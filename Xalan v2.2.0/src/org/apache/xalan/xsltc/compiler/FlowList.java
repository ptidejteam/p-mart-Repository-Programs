/*
 * @(#)$Id: FlowList.java,v 1.1 2006/03/01 20:54:02 vauchers Exp $
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
 *
 */

package org.apache.xalan.xsltc.compiler;

import java.util.Vector;
import de.fub.bytecode.generic.*;

public final class FlowList {
    private Vector _elements;

    public FlowList() {
	_elements = null;
    }

    public FlowList(InstructionHandle bh) {
	_elements = new Vector();
	_elements.addElement(bh);
    }

    public FlowList(FlowList list) {
	_elements = list._elements;
    }

    public FlowList add(InstructionHandle bh) {
	if (_elements == null) {
	    _elements = new Vector();
	}
	_elements.addElement(bh);
	return this;
    }

    public FlowList append(FlowList right) {
	if (_elements == null) {
	    _elements = right._elements;
	}
	else {
	    final Vector temp = right._elements;
	    if (temp != null) {
		final int n = temp.size();
		for (int i = 0; i < n; i++) {
		    _elements.addElement(temp.elementAt(i));
		}
	    }
	}
	return this;
    }

    /**
     * Back patch a flow list. All instruction handles must be branch handles.
     */
    public void backPatch(InstructionHandle target) {
	if (_elements != null) {
	    final int n = _elements.size();
	    for (int i = 0; i < n; i++) {
		BranchHandle bh = (BranchHandle)_elements.elementAt(i);
		bh.setTarget(target);
	    }
	    _elements.clear();		// avoid backpatching more than once
	}
    }
}
