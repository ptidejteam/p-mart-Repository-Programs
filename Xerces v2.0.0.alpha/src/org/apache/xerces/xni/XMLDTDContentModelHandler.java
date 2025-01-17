/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 2000 The Apache Software Foundation.  All rights 
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
 * originally based on software copyright (c) 1999, International
 * Business Machines, Inc., http://www.apache.org.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */

package org.apache.xerces.xni;

import org.xml.sax.SAXException;

/**
 * The DTD content model handler interface defines callback methods 
 * to report information items in DTD content models of an element
 * declaration. Parser components interested in DTD content model
 * information implement this interface and are registered as the DTD
 * content model handler on the DTD content model source.
 * <p>
 * A pipeline of DTD content model components starts with a DTD content
 * model source; is followed by zero or more DTD content model filters; 
 * and ends with a DTD content model handler.
 *
 * @see XMLDTDContentModelSource
 * @see XMLDTDContentModelFilter
 * @see XMLDTDHandler
 *
 * @author Stubs generated by DesignDoc on Mon Sep 11 11:10:57 PDT 2000
 * @author Andy Clark, IBM
 *
 * @version $Id: XMLDTDContentModelHandler.java,v 1.1.2.4 2000/10/10 08:04:58 andyc Exp $
 */
public interface XMLDTDContentModelHandler {

    //
    // Constants
    //

    // content model types

    /** 
     * Empty content model type. The element is not allowed to contain
     * any content except for ignorable whitespace.
     * <p>
     * For example:
     * <pre>
     * &lt;!ELEMENT elem EMPTY&gt;
     * </pre>
     *
     * @see TYPE_ANY
     * @see TYPE_MIXED
     * @see TYPE_CHILDREN
     */
    public static final short TYPE_EMPTY = 0;

    /** 
     * Any content model type. The element may contain any declared elements
     * or text content.
     * <p>
     * For example:
     * <pre>
     * &lt;!ELEMENT elem ANY&gt;
     * </pre>
     *
     * @see TYPE_EMPTY
     * @see TYPE_MIXED
     * @see TYPE_CHILDREN
     */
    public static final short TYPE_ANY = 1;

    /**
     * A mixed content model. The element can contain specified elements or
     * text content.
     * <p>
     * For example:
     * <pre>
     * &lt;!ELEMENT elem1 (#PCDATA)&gt;
     * &lt;!ELEMENT elem2 (#PCDATA|foo|bar)*&gt;
     * </pre>
     *
     * @see TYPE_EMPTY
     * @see TYPE_ANY
     * @see TYPE_CHILDREN
     */
    public static final short TYPE_MIXED = 2;

    /**
     * A children content model. The element can contain specified elements
     * as sequences or choices and with specified occurrence counts.
     * <p>
     * For example:
     * <pre>
     * &lt;!ELEMENT elem1 (foo)&gt;
     * &lt;!ELEMENT elem2 (foo?)&gt;
     * &lt;!ELEMENT elem3 (foo*)&gt;
     * &lt;!ELEMENT elem4 (foo+)&gt;
     * &lt;!ELEMENT elem5 (foo,bar)&gt;
     * &lt;!ELEMENT elem6 (foo|bar)&gt;
     * &lt;!ELEMENT elem7 (foo|(bar,baz)+)&gt;
     * </pre>
     *
     * @see TYPE_EMPTY
     * @see TYPE_ANY
     * @see TYPE_MIXED
     * @see SEPARATOR_CHOICE
     * @see SEPARATOR_SEQUENCE
     * @see OCCURS_ZERO_OR_ONE
     * @see OCCURS_ZERO_OR_MORE
     * @see OCCURS_ONE_OR_MORE
     */
    public static final short TYPE_CHILDREN = 3;

    // children model separators

    /** 
     * A choice separator for children content models. This separator is used
     * to specify that the allowed child is one of a collection.
     * <p>
     * For example:
     * <pre>
     * &lt;!ELEMENT elem (foo|bar)&gt;
     * &lt;!ELEMENT elem (foo|bar+)&gt;
     * &lt;!ELEMENT elem (foo|bar|baz)&gt;
     * </pre>
     *
     * @see SEPARATOR_SEQUENCE
     * @see TYPE_CHILDREN
     */
    public static final short SEPARATOR_CHOICE = 4;

    /**
     * A sequence separator for children content models. This separator is
     * used to specify that the allowed children must follow in the 
     * specified sequence.
     * <p>
     * <pre>
     * &lt;!ELEMENT elem (foo,bar)&gt;
     * &lt;!ELEMENT elem (foo,bar*)&gt;
     * &lt;!ELEMENT elem (foo,bar,baz)&gt;
     * </pre>
     *
     * @see SEPARATOR_SEQUENCE
     * @see TYPE_CHILDREN
     */
    public static final short SEPARATOR_SEQUENCE = 5;

    // children model occurrence counts

    /** 
     * This occurrence count limits the element, choice, or sequence in a
     * children content model to zero or one. In other words, the child
     * is optional.
     * <p>
     * For example:
     * <pre>
     * &lt;!ELEMENT elem (foo?)&gt;
     * </pre>
     *
     * @see OCCURS_ZERO_OR_MORE
     * @see OCCURS_ONE_OR_MORE
     * @see TYPE_CHILDREN
     */
    public static final short OCCURS_ZERO_OR_ONE = 6;

    /** 
     * This occurrence count limits the element, choice, or sequence in a
     * children content model to zero or more. In other words, the child
     * may appear an arbitrary number of times, or not at all.
     * <p>
     * For example:
     * <pre>
     * &lt;!ELEMENT elem (foo*)&gt;
     * </pre>
     *
     * @see OCCURS_ZERO_OR_ONE
     * @see OCCURS_ONE_OR_MORE
     * @see TYPE_CHILDREN
     */
    public static final short OCCURS_ZERO_OR_MORE = 7;

    /** 
     * This occurrence count limits the element, choice, or sequence in a
     * children content model to one or more. In other words, the child
     * may appear an arbitrary number of times, but must appear at least
     * once.
     * <p>
     * For example:
     * <pre>
     * &lt;!ELEMENT elem (foo+)&gt;
     * </pre>
     *
     * @see OCCURS_ZERO_OR_ONE
     * @see OCCURS_ZERO_OR_MORE
     * @see TYPE_CHILDREN
     */
    public static final short OCCURS_ONE_OR_MORE = 8;

    //
    // XMLDTDContentModelHandler methods
    //

    /**
     * The start of a content model. Depending on the type of the content
     * model, specific methods may be called between the call to the
     * startContentModel method and the call to the endContentModel method.
     * 
     * @param elementName The name of the element.
     * @param type        The content model type.
     *
     * @throws SAXException Thrown by handler to signal an error.
     *
     * @see TYPE_EMPTY
     * @see TYPE_ANY
     * @see TYPE_MIXED
     * @see TYPE_CHILDREN
     */
    public void startContentModel(String elementName, short type)
        throws SAXException;

    /**
     * A referenced element in a mixed content model. If the mixed content 
     * model only allows text content, then this method will not be called
     * for that model. However, if this method is called for a mixed
     * content model, then the zero or more occurrence count is implied.
     * <p>
     * <strong>Note:</strong> This method is only called after a call to 
     * the startContentModel method where the type is TYPE_MIXED.
     * 
     * @param elementName The name of the referenced element. 
     *
     * @throws SAXException Thrown by handler to signal an error.
     *
     * @see TYPE_MIXED
     */
    public void mixedElement(String elementName) throws SAXException;

    /**
     * The start of a children group.
     * <p>
     * <strong>Note:</strong> This method is only called after a call to
     * the startContentModel method where the type is TYPE_CHILDREN.
     * <p>
     * <strong>Note:</strong> Children groups can be nested and have
     * associated occurrence counts.
     *
     * @throws SAXException Thrown by handler to signal an error.
     *
     * @see TYPE_CHILDREN
     */
    public void childrenStartGroup() throws SAXException;

    /**
     * A referenced element in a children content model.
     * 
     * @param elementName The name of the referenced element.
     *
     * @throws SAXException Thrown by handler to signal an error.
     *
     * @see TYPE_CHILDREN
     */
    public void childrenElement(String elementName) throws SAXException;

    /**
     * The separator between choices or sequences of a children content
     * model.
     * <p>
     * <strong>Note:</strong> This method is only called after a call to
     * the startContentModel method where the type is TYPE_CHILDREN.
     * 
     * @param separator The type of children separator.
     *
     * @throws SAXException Thrown by handler to signal an error.
     *
     * @see SEPARATOR_CHOICE
     * @see SEPARATOR_SEQUENCE
     * @see TYPE_CHILDREN
     */
    public void childrenSeparator(short separator) throws SAXException;

    /**
     * The occurrence count for a child in a children content model.
     * <p>
     * <strong>Note:</strong> This method is only called after a call to
     * the startContentModel method where the type is TYPE_CHILDREN.
     * 
     * @param occurrence The occurrence count for the last children element
     *                   or children group.
     *
     * @throws SAXException Thrown by handler to signal an error.
     *
     * @see OCCURS_ZERO_OR_ONE
     * @see OCCURS_ZERO_OR_MORE
     * @see OCCURS_ONE_OR_MORE
     * @see TYPE_CHILDREN
     */
    public void childrenOccurrence(short occurrence) throws SAXException;

    /**
     * The end of a children group.
     * <p>
     * <strong>Note:</strong> This method is only called after a call to
     * the startContentModel method where the type is TYPE_CHILDREN.
     *
     * @see TYPE_CHILDREN
     */
    public void childrenEndGroup() throws SAXException;

    /**
     * The end of a content model.
     *
     * @throws SAXException Thrown by handler to signal an error.
     */
    public void endContentModel() throws SAXException;

} // interface XMLDTDContentModelHandler
