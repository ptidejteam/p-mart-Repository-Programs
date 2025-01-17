/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 2001, 2002 The Apache Software Foundation.  
 * All rights reserved.
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

import java.util.Vector;

import org.w3c.dom.DOMException;
import org.apache.xerces.dom3.as.*;
import org.apache.xerces.impl.xs.SchemaGrammar;

/**
 *  To begin with, an abstract schema is a generic structure that could 
 * contain both internal and external subsets. An <code>ASModel</code> is an 
 * abstract object that could map to a DTD , an XML Schema , a database 
 * schema, etc. An <code>ASModel</code> could represent either an internal 
 * or an external subset; hence an abstract schema could be composed of an 
 * <code>ASModel</code> representing the internal subset and an 
 * <code>ASModel</code> representing the external subset. Note that the 
 * <code>ASModel</code> representing the external subset could consult the 
 * <code>ASModel</code> representing the internal subset. Furthermore, the 
 * <code>ASModel</code> representing the internal subset could be set to 
 * null by the <code>setInternalAS</code> method as a mechanism for 
 * "removal". In addition, only one <code>ASModel</code> representing the 
 * external subset can be specified as "active" and it is possible that none 
 * are "active". Finally, the <code>ASModel</code> contains the factory 
 * methods needed to create a various types of ASObjects like 
 * <code>ASElementDeclaration</code>, <code>ASAttributeDeclaration</code>, 
 * etc. 
 * <p>See also the <a href='http://www.w3.org/TR/2001/WD-DOM-Level-3-ASLS-20011025'>
 * Document Object Model (DOM) Level 3 Abstract Schemas and Load and Save Specification</a>.
 *
 * @author Pavani Mukthipudi
 * @author Neil Graham
 * @version $Id: ASModelImpl.java,v 1.1 2006/02/02 01:45:02 vauchers Exp $
 */
public class ASModelImpl implements ASModel {

    //
    // Data
    //
    boolean fNamespaceAware = true;

    // conceptually, an ASModel may contain grammar information and/or
    // other ASModels.  These two fields divide that function.
    protected Vector fASModels;
    protected SchemaGrammar fGrammar = null;
    
    //
    // Constructors
    //
    
    public ASModelImpl() {
    	fASModels = new Vector();
    }

    public ASModelImpl(boolean isNamespaceAware) {
    	fASModels = new Vector();
        fNamespaceAware = isNamespaceAware;
    }
    
    //
    // ASObject methods
    //
    
    /**
     * A code representing the underlying object as defined above.
     */
    public short getAsNodeType() {
    	throw new DOMException(DOMException.NOT_SUPPORTED_ERR, "Not supported");
    }

    /**
     * The <code>ASModel</code> object associated with this 
     * <code>ASObject</code>. For a node of type <code>AS_MODEL</code>, this 
     * is <code>null</code>. 
     */
    public ASModel getOwnerASModel() {
    	throw new DOMException(DOMException.NOT_SUPPORTED_ERR, "Not supported");
    }
    
    /**
     * The <code>ASModel</code> object associated with this 
     * <code>ASObject</code>. For a node of type <code>AS_MODEL</code>, this 
     * is <code>null</code>. 
     */
    public void setOwnerASModel(ASModel ownerASModel) {
    	throw new DOMException(DOMException.NOT_SUPPORTED_ERR, "Not supported");
    }

    /**
     * The <code>name</code> of this <code>ASObject</code> depending on the 
     * <code>ASObject</code> type.
     */
    public String getNodeName() {
    	throw new DOMException(DOMException.NOT_SUPPORTED_ERR, "Not supported");
    }
    
    /**
     * The <code>name</code> of this <code>ASObject</code> depending on the 
     * <code>ASObject</code> type.
     */
    public void setNodeName(String nodeName) {
    	throw new DOMException(DOMException.NOT_SUPPORTED_ERR, "Not supported");
    }

    /**
     * The namespace prefix of this node, or <code>null</code> if it is 
     * unspecified.
     */
    public String getPrefix() {
    	throw new DOMException(DOMException.NOT_SUPPORTED_ERR, "Not supported");
    }
    
    /**
     * The namespace prefix of this node, or <code>null</code> if it is 
     * unspecified.
     */
    public void setPrefix(String prefix) {
    	throw new DOMException(DOMException.NOT_SUPPORTED_ERR, "Not supported");
    }

    /**
     * Returns the local part of the qualified name of this 
     * <code>ASObject</code>.
     */
    public String getLocalName() {
    	throw new DOMException(DOMException.NOT_SUPPORTED_ERR, "Not supported");
    }
    
    /**
     * Returns the local part of the qualified name of this 
     * <code>ASObject</code>.
     */
    public void setLocalName(String localName) {
    	throw new DOMException(DOMException.NOT_SUPPORTED_ERR, "Not supported");
    }

    /**
     * The namespace URI of this node, or <code>null</code> if it is 
     * unspecified.  defines how a namespace URI is attached to schema 
     * components.
     */
    public String getNamespaceURI() {
    	throw new DOMException(DOMException.NOT_SUPPORTED_ERR, "Not supported");
    }
    
    /**
     * The namespace URI of this node, or <code>null</code> if it is 
     * unspecified.  defines how a namespace URI is attached to schema 
     * components.
     */
    public void setNamespaceURI(String namespaceURI) {
   	throw new DOMException(DOMException.NOT_SUPPORTED_ERR, "Not supported");
    }

    /**
     * Creates a copy of this <code>ASObject</code>. See text for 
     * <code>cloneNode</code> off of <code>Node</code> but substitute AS 
     * functionality.
     * @param deep Setting the <code>deep</code> flag on, causes the whole 
     *   subtree to be duplicated. Setting it to <code>false</code> only 
     *   duplicates its immediate child nodes.
     * @return Cloned <code>ASObject</code>.
     */
    public ASObject cloneASObject(boolean deep) {
    	throw new DOMException(DOMException.NOT_SUPPORTED_ERR, "Not supported");
    }
    
    //
    // ASModel methods
    //
    
    /**
     * <code>true</code> if this <code>ASModel</code> defines the document 
     * structure in terms of namespaces and local names ; <code>false</code> 
     * if the document structure is defined only in terms of 
     * <code>QNames</code>.
     */
    public boolean getIsNamespaceAware() {
    	return fNamespaceAware;
    }

    /**
     *  0 if used internally, 1 if used externally, 2 if not all. An exception 
     * will be raised if it is incompatibly shared or in use as an internal 
     * subset. 
     */
    public short getUsageLocation() {
    	throw new DOMException(DOMException.NOT_SUPPORTED_ERR, "Not Supported");
    }

    /**
     *  The URI reference. 
     */
    public String getAsLocation() {
    	throw new DOMException(DOMException.NOT_SUPPORTED_ERR, "Not Supported");
    }
    
    /**
     *  The URI reference. 
     */
    public void setAsLocation(String asLocation) {
    	throw new DOMException(DOMException.NOT_SUPPORTED_ERR, "Not Supported");
    }

    /**
     *  The hint to locating an ASModel. 
     */
    public String getAsHint() {
    	throw new DOMException(DOMException.NOT_SUPPORTED_ERR, "Not Supported");
    }
    
    /**
     *  The hint to locating an ASModel. 
     */
    public void setAsHint(String asHint) {
    	throw new DOMException(DOMException.NOT_SUPPORTED_ERR, "Not Supported");
    }
    
    /**
     * If <code>usage</code> is EXTERNAL_SUBSET or NOT_USED, and the 
     * <code>ASModel</code> is simply a container of other ASModels. 
     */
    public boolean getContainer() {
    	return (fGrammar != null);
    }

    /**
     * Instead of returning an all-in-one <code>ASObject</code> with 
     * <code>ASModel</code> methods, have discernible top-level/"global" 
     * element declarations. If one attempts to add, set, or remove a node 
     * type other than the intended one, a hierarchy exception (or 
     * equivalent is thrown). 
     */
    public ASNamedObjectMap getElementDeclarations() {
    	throw new DOMException(DOMException.NOT_SUPPORTED_ERR, "Not Supported");
    }

    /**
     * Instead of returning an all-in-one <code>ASObject</code> with 
     * <code>ASModel</code> methods, have discernible top-level/"global" 
     * attribute declarations. If one attempts to add, set, or remove a node 
     * type other than the intended one, a hierarchy exception (or 
     * equivalent is thrown). 
     */
    public ASNamedObjectMap getAttributeDeclarations() {
    	throw new DOMException(DOMException.NOT_SUPPORTED_ERR, "Not Supported");
    }

    /**
     * Instead of returning an all-in-one <code>ASObject</code> with 
     * <code>ASModel</code> methods, have discernible top-level/"global" 
     * notation declarations. If one attempts to add, set, or remove a node 
     * type other than the intended one, a hierarchy exception (or 
     * equivalent is thrown). 
     */
    public ASNamedObjectMap getNotationDeclarations() {
    	throw new DOMException(DOMException.NOT_SUPPORTED_ERR, "Not Supported");
    }

    /**
     * Instead of returning an all-in-one <code>ASObject</code> with 
     * <code>ASModel</code> methods, have discernible top-level/"global" 
     * entity declarations. If one attempts to add, set, or remove a node 
     * type other than the intended one, a hierarchy exception (or 
     * equivalent is thrown). 
     */
    public ASNamedObjectMap getEntityDeclarations() {
    	throw new DOMException(DOMException.NOT_SUPPORTED_ERR, "Not Supported");
    }

    /**
     * Instead of returning an all-in-one <code>ASObject</code> with 
     * <code>ASModel</code> methods, have discernible top-level/"global 
     * content model declarations. If one attempts to add, set, or remove a 
     * node type other than the intended one, a hierarchy exception (or 
     * equivalent is thrown). 
     */
    public ASNamedObjectMap getContentModelDeclarations() {
    	throw new DOMException(DOMException.NOT_SUPPORTED_ERR, "Not Supported");
    }

    /**
     * This method will allow the nesting or "importation" of ASModels. 
     * @param abstractSchema ASModel to be set. Subsequent calls will nest 
     *   the ASModels within the specified <code>ownerASModel</code>. 
     */
    public void addASModel(ASModel abstractSchema) {
    	fASModels.addElement(abstractSchema);
    }

    /**
     * To retrieve a list of nested ASModels without reference to names. 
     * @return A list of ASModels. 
     */
    public ASObjectList getASModels() {
    	throw new DOMException(DOMException.NOT_SUPPORTED_ERR, "Not Supported");
    }

    /**
     * Removes only the specified <code>ASModel</code> from the list of 
     * <code>ASModel</code>s.
     * @param as AS to be removed.
     */
    public void removeAS(ASModel as) {
    	fASModels.removeElement(as);
    }

    /**
     * Determines if an <code>ASModel</code> itself is valid, i.e., confirming 
     * that it's well-formed and valid per its own formal grammar. 
     * @return <code>true</code> if the <code>ASModel</code> is valid, 
     *   <code>false</code> otherwise.
     */
    public boolean validate() {
    	throw new DOMException(DOMException.NOT_SUPPORTED_ERR, "Not Supported");
    }

    /**
     * Imports <code>ASObject</code> into ASModel. 
     * @param asobject  <code>ASObject</code> to be imported. 
     */
    public void importASObject(ASObject asobject) {
    	throw new DOMException(DOMException.NOT_SUPPORTED_ERR, "Not Supported");
    }

    /**
     * Inserts <code>ASObject</code> into ASModel. 
     * @param asobject  <code>ASObject</code> to be inserted. 
     */
    public void insertASObject(ASObject asobject) {
    	throw new DOMException(DOMException.NOT_SUPPORTED_ERR, "Not Supported");
    }
    
    /**
     * Creates an element declaration for the element type specified.
     * @param namespaceURI The <code>namespace URI</code> of the element type 
     *   being declared. 
     * @param name The name of the element. The format of the name could be 
     *   an NCName as defined by XML Namespaces or a Name as defined by XML 
     *   1.0; it's ASModel-dependent. 
     * @return A new <code>ASElementDeclaration</code> object with 
     *   <code>name</code> attribute set to <code>tagname</code> and 
     *   <code>namespaceURI</code> set to <code>systemId</code>. Other 
     *   attributes of the element declaration are set through 
     *   <code>ASElementDeclaration</code> interface methods.
     * @exception DOMException
     *   INVALID_CHARACTER_ERR: Raised if the specified name contains an 
     *   illegal character.
     */
    public ASElementDeclaration createASElementDeclaration(String namespaceURI, 
                                                           String name)
                                                           throws DOMException {
    	throw new DOMException(DOMException.NOT_SUPPORTED_ERR, "Not Supported");
    }

    /**
     * Creates an attribute declaration.
     * @param namespaceURI The namespace URI of the attribute being declared.
     * @param name The name of the attribute. The format of the name could be 
     *   an NCName as defined by XML Namespaces or a Name as defined by XML 
     *   1.0; it's ASModel-dependent. 
     * @return A new <code>ASAttributeDeclaration</code> object with 
     *   appropriate attributes set by input parameters.
     * @exception DOMException
     *   INVALID_CHARACTER_ERR: Raised if the input <code>name</code> 
     *   parameter contains an illegal character.
     */
    public ASAttributeDeclaration createASAttributeDeclaration(String namespaceURI, 
                                                               String name)
                                                               throws DOMException {
    	throw new DOMException(DOMException.NOT_SUPPORTED_ERR, "Not Supported");
    }

    /**
     * Creates a new notation declaration. 
     * @param namespaceURI The namespace URI of the notation being declared.
     * @param name The name of the notation. The format of the name could be 
     *   an NCName as defined by XML Namespaces or a Name as defined by XML 
     *   1.0; it's ASModel-dependent. 
     * @param systemId The system identifier for the notation declaration.
     * @param publicId The public identifier for the notation declaration.
     * @return A new <code>ASNotationDeclaration</code> object with 
     *   <code>notationName</code> attribute set to <code>name</code> and 
     *   <code>publicId</code> and <code>systemId</code> set to the 
     *   corresponding fields.
     * @exception DOMException
     *   INVALID_CHARACTER_ERR: Raised if the specified name contains an 
     *   illegal character.
     */
    public ASNotationDeclaration createASNotationDeclaration(String namespaceURI, String name, 
                                                             String systemId, String publicId)
                                                             throws DOMException {
    	throw new DOMException(DOMException.NOT_SUPPORTED_ERR, "Not Supported");
    }
    
    /**
     * Creates an ASEntityDeclaration. 
     * @param name The name of the entity being declared.
     * @return A new <code>ASEntityDeclaration</code> object with 
     *   <code>entityName</code> attribute set to name.
     * @exception DOMException
     *   INVALID_CHARACTER_ERR: Raised if the specified name contains an 
     *   illegal character.
     */
    public ASEntityDeclaration createASEntityDeclaration(String name)
                                                         throws DOMException {
	throw new DOMException(DOMException.NOT_SUPPORTED_ERR, "Not Supported");
    }

    /**
     * Creates an object which describes part of an 
     * <code>ASElementDeclaration</code>'s content model. 
     * @param minOccurs The minimum occurrence for the subModels of this 
     *   <code>ASContentModel</code>.
     * @param maxOccurs The maximum occurrence for the subModels of this 
     *   <code>ASContentModel</code>.
     * @param operator operator of type <code>AS_CHOICE</code>, 
     *   <code>AS_SEQUENCE</code>, <code>AS_ALL</code> or 
     *   <code>AS_NONE</code>.
     * @return A new <code>ASContentModel</code> object.
     * @exception DOMASException
     *   A DOMASException, e.g., <code>minOccurs &gt; maxOccurs</code>.
     */
    public ASContentModel createASContentModel(int minOccurs, int maxOccurs, 
                                               short operator) throws DOMASException {
        throw new DOMException(DOMException.NOT_SUPPORTED_ERR, "Not Supported");
    }


    // convenience methods
    public SchemaGrammar getGrammar() {
        return fGrammar;
    }
    public void setGrammar(SchemaGrammar grammar) {
        fGrammar = grammar;
    }

    public Vector getInternalASModels() {
        return fASModels;
    }

}
