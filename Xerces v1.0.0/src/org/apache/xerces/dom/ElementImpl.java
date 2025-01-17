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

package org.apache.xerces.dom;

import java.io.*;
import java.util.Enumeration;
import java.util.Vector;

import org.w3c.dom.*;

/**
 * Elements represent most of the "markup" and structure of the
 * document.  They contain both the data for the element itself
 * (element name and attributes), and any contained nodes, including
 * document text (as children).
 * <P>
 * Elements may have Attributes associated with them; the API for this is
 * defined in Node, but the function is implemented here. In general, XML
 * applications should retrive Attributes as Nodes, since they may contain
 * entity references and hence be a fairly complex sub-tree. HTML users will
 * be dealing with simple string values, and convenience methods are provided
 * to work in terms of Strings.
 * <P>
 * @version
 * @since  PR-DOM-Level-1-19980818.
 */
public class ElementImpl
    extends NodeImpl
    implements Element {

    //
    // Constants
    //

    /** Serialization version. */
    static final long serialVersionUID = -7202454486126245907L;

    //
    // Data
    //

    /** Attributes. */
    protected NamedNodeMapImpl attributes;

    /** DOM2: Namespace URI. */
	protected String namespaceURI;
  
    /** DOM2: Prefix */
	protected String prefix;

    /** DOM2: localName. */
	protected String localName;
	
    /** DOM2: support. 
     * Is this element created with ownerDocument.createElementNS()? 
     */
	protected boolean enableNamespace = false;
  
    //
    // Constructors
    //

    /** Factory constructor. */
    public ElementImpl(DocumentImpl ownerDoc, String name) {
    	super(ownerDoc, name, null);
        //setupDefaultAttributes(ownerDoc);
        this.localName = name;
        syncData = true;
    }
    
    /**
     * DOM2: Constructor for Namespace implementation.
     */
    protected ElementImpl(DocumentImpl ownerDocument, 
                          String namespaceURI,
                          String qualifiedName) {

        this.ownerDocument = ownerDocument;
        this.namespaceURI = namespaceURI;
        this.name = qualifiedName;
        int index = qualifiedName.indexOf(':');
        if (index < 0) {
            this.prefix = null;
            this.localName = qualifiedName;
        } 
        else {
            this.prefix = qualifiedName.substring(0, index); 
            this.localName = qualifiedName.substring(index+1);
        }
        this.enableNamespace = true;
        syncData = true;
        
    } // <init>(DocumentImpl,String,short,boolean,String)
    
    //
    // Node methods
    //

    
    /**
     * A short integer indicating what type of node this is. The named
     * constants for this value are defined in the org.w3c.dom.Node interface.
     */
    public short getNodeType() {
        return Node.ELEMENT_NODE;
    }

    /** Returns the node value. */
    public String getNodeValue() {
        return null;
    }

    /**
     * Elements never have a nodeValue.
     * @throws DOMException(NO_MODIFICATION_ALLOWED_ERR), unconditionally.
     */
    public void setNodeValue(String value) throws DOMException {
    	throw new DOMExceptionImpl(DOMException.NO_MODIFICATION_ALLOWED_ERR, 
    	                           "NO_MODIFICATION_ALLOWED_ERR");
    }

    /**
     * Retrieve all the Attributes as a set. Note that this API is inherited
     * from Node rather than specified on Element; in fact only Elements will
     * ever have Attributes, but they want to allow folks to "blindly" operate
     * on the tree as a set of Nodes.
     */
    public NamedNodeMap getAttributes() {

        if (syncData) {
            synchronizeData();
        }

        return attributes;

    } // getAttributes():NamedNodeMap

    /**
     * Return a duplicate copy of this Element. Note that its children
     * will not be copied unless the "deep" flag is true, but Attributes
     * are <i>always</i> replicated.
     *
     * @see org.w3c.dom.Node#cloneNode(boolean)
     */
    public Node cloneNode(boolean deep) {

        if (syncData) {
            synchronizeData();
        }

    	ElementImpl newnode = (ElementImpl) super.cloneNode(deep);
    	// Replicate NamedNodeMap rather than sharing it.
    	newnode.attributes = attributes.cloneMap();
    	return newnode;

    } // cloneNode(boolean):Node

    //
    // Element methods
    //

    /** Returns the element value. */
    public String getValue() {
        return null;
    }

    /**
     * Look up a single Attribute by name. Returns the Attribute's
     * string value, or an empty string (NOT null!) to indicate that the
     * name did not map to a currently defined attribute.
     * <p>
     * Note: Attributes may contain complex node trees. This method
     * returns the "flattened" string obtained from Attribute.getValue().
     * If you need the structure information, see getAttributeNode().
     */
    public String getAttribute(String name) {

        if (syncData) {
            synchronizeData();
        }

        Attr attr = (Attr)(attributes.getNamedItem(name));
        return (attr == null) ? "" : attr.getValue();

    } // getAttribute(String):String


    /**
     * Look up a single Attribute by name. Returns the Attribute Node,
     * so its complete child tree is available. This could be important in
     * XML, where the string rendering may not be sufficient information.
     * <p>
     * If no matching attribute is available, returns null.
     */
    public Attr getAttributeNode(String name) {

        if (syncData) {
            synchronizeData();
        }

        return (Attr)attributes.getNamedItem(name);

    } // getAttributeNode(String):Attr
    

    /**
     * Returns a NodeList of all descendent nodes (children,
     * grandchildren, and so on) which are Elements and which have the
     * specified tag name.
     * <p>
     * Note: NodeList is a "live" view of the DOM. Its contents will
     * change as the DOM changes, and alterations made to the NodeList
     * will be reflected in the DOM.
     *
     * @param tagname The type of element to gather. To obtain a list of
     * all elements no matter what their names, use the wild-card tag
     * name "*".
     *
     * @see DeepNodeListImpl
     */
    public NodeList getElementsByTagName(String tagname) {
    	return new DeepNodeListImpl(this,tagname);
    }
    
    //
    //DOM2: Namespace methods.
    //
    
    /** 
     * Introduced in DOM Level 2. <p>
     *
     * The namespace URI of this node, or null if it is unspecified.<p>
     *
     * This is not a computed value that is the result of a namespace lookup based on
     * an examination of the namespace declarations in scope. It is merely the
     * namespace URI given at creation time.<p>
     *
     * For nodes created with a DOM Level 1 method, such as createElement
     * from the Document interface, this is null.     
     * @since WD-DOM-Level-2-19990923
     */
    public String getNamespaceURI()
    {
        if (syncData) {
            synchronizeData();
        }
        return namespaceURI;
    }
    
    /** 
     * Introduced in DOM Level 2. <p>
     *
     * The namespace prefix of this node, or null if it is unspecified. <p>
     *
     * For nodes created with a DOM Level 1 method, such as createElement
     * from the Document interface, this is null. <p>
     *
     * @since WD-DOM-Level-2-19990923
     */
    public String getPrefix()
    {
        if (syncData) {
            synchronizeData();
        }
        return prefix;
    }
    
    /** 
     * Introduced in DOM Level 2. <p>
     *
     * Note that setting this attribute changes the nodeName attribute, which holds the
     * qualified name, as well as the tagName and name attributes of the Element
     * and Attr interfaces, when applicable.<p>
     *
     * @throws INVALID_CHARACTER_ERR Raised if the specified
     * prefix contains an invalid character.     
     *
     * @since WD-DOM-Level-2-19990923
     */
    public void setPrefix(String prefix)
        throws DOMException
    {
        if (syncData) {
            synchronizeData();
        }
    	if (!DocumentImpl.isXMLName(prefix)) {
    	    throw new DOMExceptionImpl(DOMException.INVALID_CHARACTER_ERR, 
    	                               "INVALID_CHARACTER_ERR");
        }
            
        this.prefix = prefix;
        this.name = prefix+":"+localName;
    }
                                        
    /** 
     * Introduced in DOM Level 2. <p>
     *
     * Returns the local part of the qualified name of this node.
     * @since WD-DOM-Level-2-19990923
     */
    public String             getLocalName()
    {
        if (syncData) {
            synchronizeData();
        }
        return localName;
    }
    
    /**
     * Returns the name of the Element. Note that Element.nodeName() is
     * defined to also return the tag name.
     * <p>
     * This is case-preserving in XML. HTML should uppercasify it on the
     * way in.
     */
    public String getTagName() {
        if (syncData) {
            synchronizeData();
            }
    	return name;
    }

    /**
     * In "normal form" (as read from a source file), there will never be two
     * Text children in succession. But DOM users may create successive Text
     * nodes in the course of manipulating the document. Normalize walks the
     * sub-tree and merges adjacent Texts, as if the DOM had been written out
     * and read back in again. This simplifies implementation of higher-level
     * functions that may want to assume that the document is in standard form.
     * <p>
     * To normalize a Document, normalize its top-level Element child.
     * <p>
     * As of PR-DOM-Level-1-19980818, CDATA -- despite being a subclass of
     * Text -- is considered "markup" and will _not_ be merged either with
     * normal Text or with other CDATASections.
     */
    public void normalize() {

    	Node kid, next;
    	for (kid = getFirstChild(); kid != null; kid = next) {
    		next = kid.getNextSibling();

    		// If kid and next are both Text nodes (but _not_ CDATASection,
    		// which is a subclass of Text), they can be merged.
    		if (next != null
			 && kid.getNodeType() == Node.TEXT_NODE
			 && next.getNodeType() == Node.TEXT_NODE)
    	    {
    			((Text)kid).appendData(next.getNodeValue());
    			removeChild(next);
    			next = kid; // Don't advance; there might be another.
    		}

    		// Otherwise it might be an Element, which is handled recursively
    		else if (kid.getNodeType() ==  Node.ELEMENT_NODE) {
                ((Element)kid).normalize();
            }
        }

    	// changed() will have occurred when the removeChild() was done,
    	// so does not have to be reissued.

    } // normalize()

    /**
     * Remove the named attribute from this Element. If the removed
     * Attribute has a default value, it is immediately replaced thereby.
     * <P>
     * The default logic is actually implemented in NamedNodeMapImpl.
     * PR-DOM-Level-1-19980818 doesn't fully address the DTD, so some
     * of this behavior is likely to change in future versions. ?????
     * <P>
     * Note that this call "succeeds" even if no attribute by this name
     * existed -- unlike removeAttributeNode, which will throw a not-found
     * exception in that case.
     *	
     * @throws DOMException(NO_MODIFICATION_ALLOWED_ERR) if the node is
     * readonly.
     */
    public void removeAttribute(String name) {

    	if (readOnly) {
    		throw new DOMExceptionImpl(
    			DOMException.NO_MODIFICATION_ALLOWED_ERR, 
    			"NO_MODIFICATION_ALLOWED_ERR");
        }
    		
        if (syncData) {
            synchronizeData();
        }

    	AttrImpl att = (AttrImpl) attributes.getNamedItem(name);
    	// Remove it (and let the NamedNodeMap recreate the default, if any)
    	if (att != null) {
    		att.owned = false;
    		attributes.removeNamedItem(name);
    	}

    } // removeAttribute(String)

  
    /**
     * Remove the specified attribute/value pair. If the removed
     * Attribute has a default value, it is immediately replaced.
     * <p>
     * NOTE: Specifically removes THIS NODE -- not the node with this
     * name, nor the node with these contents. If the specific Attribute
     * object passed in is not stored in this Element, we throw a
     * DOMException.  If you really want to remove an attribute by name,
     * use removeAttribute().
     *
     * @return the Attribute object that was removed.
     * @throws DOMException(NOT_FOUND_ERR) if oldattr is not an attribute of
     * this Element.
     * @throws DOMException(NO_MODIFICATION_ALLOWED_ERR) if the node is
     * readonly.
     */
    public Attr removeAttributeNode(Attr oldAttr)
        throws DOMException
        {

    	if (readOnly) {
    		throw new DOMExceptionImpl(
    			DOMException.NO_MODIFICATION_ALLOWED_ERR, 
    			"NO_MODIFICATION_ALLOWED_ERR");
        }
    		
        if (syncData) {
            synchronizeData();
        }

    	AttrImpl found = (AttrImpl) attributes.getNamedItem(oldAttr.getName());

    	// If it is in fact the right object, remove it (and let the
    	// NamedNodeMap recreate the default, if any)

    	if (found == oldAttr) {
    		attributes.removeNamedItem(oldAttr.getName());
    		found.owned = false;
    		return found;
    	}

        throw new DOMExceptionImpl(DOMException.NOT_FOUND_ERR, 
                                   "NOT_FOUND_ERR");

    } // removeAttributeNode(Attr):Attr

   
    /**
     * Add a new name/value pair, or replace the value of the existing
     * attribute having that name.
     *
     * Note: this method supports only the simplest kind of Attribute,
     * one whose value is a string contained in a single Text node.
     * If you want to assert a more complex value (which XML permits,
     * though HTML doesn't), see setAttributeNode().
     *
     * The attribute is created with specified=true, meaning it's an
     * explicit value rather than inherited from the DTD as a default.
     * Again, setAttributeNode can be used to achieve other results.
     *
     * @throws DOMException(INVALID_NAME_ERR) if the name is not acceptable.
     * (Attribute factory will do that test for us.)
     *
     * @throws DOMException(NO_MODIFICATION_ALLOWED_ERR) if the node is
     * readonly.
     */
    public void setAttribute(String name, String value) {

    	if (readOnly) {
    		throw new DOMExceptionImpl(
    			DOMException.NO_MODIFICATION_ALLOWED_ERR, 
    			"NO_MODIFICATION_ALLOWED_ERR");
        }

        if (syncData) {
            synchronizeData();
        }

    	AttrImpl newAttr = (AttrImpl)getOwnerDocument().createAttribute(name);
    	newAttr.setNodeValue(value);
    	attributes.setNamedItem(newAttr);
    	newAttr.owned = true; // Set true AFTER adding -- or move in?????

    } // setAttribute(String,String)
 
    /**
     * Add a new attribute/value pair, or replace the value of the
     * existing attribute with that name.
     * <P>
     * This method allows you to add an Attribute that has already been
     * constructed, and hence avoids the limitations of the simple
     * setAttribute() call. It can handle attribute values that have
     * arbitrarily complex tree structure -- in particular, those which
     * had entity references mixed into their text.
     *
     * @throws DOMException(INUSE_ATTRIBUTE_ERR) if the Attribute object
     * has already been assigned to another Element.
     */
    public Attr setAttributeNode(Attr newAttr)
        throws DOMException
        {

    	if (readOnly) {
    		throw new DOMExceptionImpl(
    			DOMException.NO_MODIFICATION_ALLOWED_ERR, 
    			"NO_MODIFICATION_ALLOWED_ERR");
        }
    	
        if (syncData) {
            synchronizeData();
        }

    	if (!(newAttr instanceof AttrImpl)) {
    		throw new DOMExceptionImpl(DOMException.WRONG_DOCUMENT_ERR, 
    		                           "WRONG_DOCUMENT_ERR");
        }

    	AttrImpl na = (AttrImpl) newAttr;
    	AttrImpl oldAttr = (AttrImpl) attributes.getNamedItem(newAttr.getName());

    	// This will throw INUSE if necessary
    	attributes.setNamedItem(na);
    	na.owned = true; // Must set after adding ... or within?

    	return oldAttr;

    } // setAttributeNode(Attr):Attr
    
    //
    // DOM2: Namespace methods
    //

    /**
     * Introduced in DOM Level 2. <p>
     *
     * Retrieves an attribute value by local name and namespace URI. 
     *
     * @param namespaceURI
     *                      The namespace URI of the attribute to
     *                      retrieve. When it is null or an empty string,
     *                      this method behaves like getAttribute.
     * @param localName     The local name of the attribute to retrieve.
     * @return String       The Attr value as a string, or an empty string 
     *                      if that attribute
     *                      does not have a specified or default value.
     * @since WD-DOM-Level-2-19990923
     */
    public String getAttributeNS(String namespaceURI, String localName) {

        if (syncData) {
            synchronizeData();
        }

        Attr attr = (Attr)(attributes.getNamedItemNS(namespaceURI, localName));
        return (attr == null) ? "" : attr.getValue();

    } // getAttribute(String):String
    
    /**
     * Introduced in DOM Level 2. <p>
     *
     * Adds a new attribute. If an attribute with that local name and namespace URI is
     * already present in the element, its value is changed to be that of the value
     * parameter. This value is a simple string, it is not parsed as it is being set. So any
     * markup (such as syntax to be recognized as an entity reference) is treated as
     * literal text, and needs to be appropriately escaped by the implementation when
     * it is written out. In order to assign an attribute value that contains entity
     * references, the user must create an Attr node plus any Text and
     * EntityReference nodes, build the appropriate subtree, and use
     * setAttributeNodeNS or setAttributeNode to assign it as the value of an
     * attribute. HTML-only DOM implementations do not need to implement this
     * method.
     * @param namespaceURI
     *                          The namespace URI of the attribute to create
     *                          or alter. When it is null or an empty string,
     *                          this method behaves like getAttribute.
     * @param localName         The local name of the attribute to create or
     *                          alter.
     * @param value             The value to set in string form.
     * @throws                  INVALID_CHARACTER_ERR: Raised if the specified
     *                          name contains an invalid character.
     *
     * @throws                  NO_MODIFICATION_ALLOWED_ERR: Raised if this
     *                          node is readonly.
     * @since WD-DOM-Level-2-19990923
     */
    public void setAttributeNS(String namespaceURI, String localName, String value) {

    	if (readOnly) {
    		throw new DOMExceptionImpl(
    			DOMException.NO_MODIFICATION_ALLOWED_ERR, 
    			"NO_MODIFICATION_ALLOWED_ERR");
        }

        if (syncData) {
            synchronizeData();
        }
        //REVISTNS: THe createAttributeNS input params is qualifiedName, 
        // but we only have localName. What are implications?
    	AttrImpl newAttr = (AttrImpl) 
    	    ((DocumentImpl)getOwnerDocument()).
    	        createAttributeNS(namespaceURI, localName);
    	newAttr.setNodeValue(value);
    	attributes.setNamedItem(newAttr);
    	newAttr.owned = true; // Set true AFTER adding -- or move in?????

    } // setAttributeNS(String,String,String)
    
    /**
     * Introduced in DOM Level 2. <p>
     *
     * Removes an attribute by local name and namespace URI. If the removed
     * attribute has a default value it is immediately replaced.<p>
     *
     * @param namespaceURI  The namespace URI of the attribute to remove.
     *                      When it is null or an empty string, this method
     *                      behaves like removeAttribute.
     * @param localName     The local name of the attribute to remove.
     * @since WD-DOM-Level-2-19990923
     */
    public void removeAttributeNS(String namespaceURI, String localName) {

    	if (readOnly) {
    		throw new DOMExceptionImpl(
    			DOMException.NO_MODIFICATION_ALLOWED_ERR, 
    			"NO_MODIFICATION_ALLOWED_ERR");
        }
    		
        if (syncData) {
            synchronizeData();
        }

    	AttrImpl att = (AttrImpl) attributes.getNamedItemNS(namespaceURI, localName);
    	// Remove it (and let the NamedNodeMap recreate the default, if any)
    	if (att != null) {
    		att.owned = false;
    		attributes.removeNamedItemNS(namespaceURI, localName);
    	}

    } // removeAttributeNS(String,String)
    
    /**
     * Retrieves an Attr node by local name and namespace URI. 
     *
     * @param namespaceURI  The namespace URI of the attribute to
     *                      retrieve. When it is null or an empty string,
     *                      this method behaves like getAttribute.
     * @param localName     The local name of the attribute to retrieve.
     * @return Attr         The Attr node with the specified attribute 
     *                      local name and namespace
     *                      URI or null if there is no such attribute.
     * @since WD-DOM-Level-2-19990923
     */
    public Attr getAttributeNodeNS(String namespaceURI, String localName){

        if (syncData) {
            synchronizeData();
        }

        return (Attr)attributes.getNamedItemNS( namespaceURI, localName);

    } // getAttributeNodeNS(String,String):Attr
 
    /**
     * Introduced in DOM Level 2. <p>
     *
     * Adds a new attribute. If an attribute with that local name and 
     * namespace URI is already present in the element, it is replaced 
     * by the new one.
     *
     * @param Attr      The Attr node to add to the attribute list. When 
     *                  the Node has no namespaceURI, this method behaves 
     *                  like setAttributeNode.
     * @return Attr     If the newAttr attribute replaces an existing attribute with the same
     *                  local name and namespace URI, the previously existing Attr node is
     *                  returned, otherwise null is returned.
     * @since WD-DOM-Level-2-19990923
     */
    public Attr setAttributeNodeNS(Attr newAttr)
        throws DOMException
        {

    	if (readOnly) {
    		throw new DOMExceptionImpl(
    			DOMException.NO_MODIFICATION_ALLOWED_ERR, 
    			"NO_MODIFICATION_ALLOWED_ERR");
        }
    	
        if (syncData) {
            synchronizeData();
        }

    	if (!(newAttr instanceof AttrImpl)) {
    		throw new DOMExceptionImpl(DOMException.WRONG_DOCUMENT_ERR, 
    		"WRONG_DOCUMENT_ERR");
        }

    	AttrImpl na = (AttrImpl) newAttr;
    	AttrImpl oldAttr = (AttrImpl) attributes.getNamedItemNS(na.getNamespaceURI(), na.getLocalName());

    	// This will throw INUSE if necessary
    	attributes.setNamedItem(na);
    	na.owned = true; // Must set after adding ... or within?

    	return oldAttr;

    } // setAttributeNodeNS(Attr):Attr
    
    /**
     * Introduced in DOM Level 2. <p>
     *
     * Returns a NodeList of all the Elements with a given local name and
     * namespace URI in the order in which they would be encountered in a preorder
     * traversal of the Document tree, starting from this node.
     *
     * @param namespaceURI The namespace URI of the elements to match
     *                     on. The special value "*" matches all
     *                     namespaces. When it is null or an empty
     *                     string, this method behaves like
     *                     getElementsByTagName.
     * @param localName    The local name of the elements to match on.
     *                     The special value "*" matches all local names.
     * @return NodeList    A new NodeList object containing all the matched Elements.
     * @since WD-DOM-Level-2-19990923
     */
    public NodeList getElementsByTagNameNS(String namespaceURI, String localName) {
    	return new DeepNodeListImpl(this, namespaceURI, localName);
    }

    //
    // Public methods
    //

    /**
     * NON-DOM: Subclassed to flip the attributes' readonly switch as well.
     * @see NodeImpl#setReadOnly
     */
    public void setReadOnly(boolean readOnly, boolean deep) {

    	super.setReadOnly(readOnly,deep);
        if (syncChildren) {
            synchronizeChildren();
        }
    	attributes.setReadOnly(readOnly,true);

    } // setReadOnly(boolean,boolean)

    //
    // Protected methods
    //

    /** Synchronizes the data (name and value) for fast nodes. */
    protected void synchronizeData() {

        // no need to sync in the future
        syncData = false;

        // attributes
        setupDefaultAttributes();

    } // synchronizeData()

    /** Setup the default attributes. */
    protected void setupDefaultAttributes() {

    	// If there is an ElementDefintion, set its Attributes up as
    	// shadows behind our own.
    	NamedNodeMapImpl defaultAttrs = null;
    	DocumentTypeImpl doctype = (DocumentTypeImpl)ownerDocument.getDoctype();
    	if (doctype != null) {
    		ElementDefinitionImpl eldef =
                (ElementDefinitionImpl)doctype.getElements()
                                              .getNamedItem(getNodeName());
    		if (eldef != null) {
    			defaultAttrs = (NamedNodeMapImpl)eldef.getAttributes();
            }
        }

        // create attributes
    	attributes = new NamedNodeMapImpl(this, defaultAttrs);

    } // setupAttributes(DocumentImpl)

} // class ElementImpl
