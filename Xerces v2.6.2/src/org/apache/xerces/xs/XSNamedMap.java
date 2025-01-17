/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 2003 The Apache Software Foundation.  All rights
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
 * originally based on software copyright (c) 2003, International
 * Business Machines, Inc., http://www.apache.org.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */

package org.apache.xerces.xs;

/**
 * Objects implementing the <code>XSNamedMap</code> interface are used to 
 * represent immutable collections of XML Schema components that can be 
 * accessed by name. Note that <code>XSNamedMap</code> does not inherit from 
 * <code>XSObjectList</code>. The <code>XSObject</code>s in 
 * <code>XSNamedMap</code>s are not maintained in any particular order. 
 */
public interface XSNamedMap {
    /**
     * The number of <code>XSObjects</code> in the <code>XSObjectList</code>. 
     * The range of valid child object indices is 0 to <code>length-1</code> 
     * inclusive. 
     */
    public int getLength();

    /**
     *  Returns the <code>index</code>th item in the collection or 
     * <code>null</code> if <code>index</code> is greater than or equal to 
     * the number of objects in the list. The index starts at 0. 
     * @param index  index into the collection. 
     * @return  The <code>XSObject</code> at the <code>index</code>th 
     *   position in the <code>XSObjectList</code>, or <code>null</code> if 
     *   the index specified is not valid. 
     */
    public XSObject item(int index);

    /**
     * Retrieves an <code>XSObject</code> specified by local name and 
     * namespace URI.
     * <br>Per XML Namespaces, applications must use the value <code>null</code> as the 
     * <code>namespace</code> parameter for methods if they wish to specify 
     * no namespace.
     * @param namespace The namespace URI of the <code>XSObject</code> to 
     *   retrieve, or <code>null</code> if the <code>XSObject</code> has no 
     *   namespace. 
     * @param localName The local name of the <code>XSObject</code> to 
     *   retrieve.
     * @return A <code>XSObject</code> (of any type) with the specified local 
     *   name and namespace URI, or <code>null</code> if they do not 
     *   identify any object in this map.
     */
    public XSObject itemByName(String namespace, 
                               String localName);

}
