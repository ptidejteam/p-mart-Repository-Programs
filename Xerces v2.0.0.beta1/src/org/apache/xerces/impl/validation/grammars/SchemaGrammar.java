/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 1999,2000 The Apache Software Foundation.  All rights 
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

package org.apache.xerces.impl.validation.grammars;

import org.apache.xerces.impl.validation.Grammar;
import org.apache.xerces.impl.validation.GrammarPool;
import org.apache.xerces.impl.validation.XMLContentSpec;
import org.apache.xerces.impl.validation.DatatypeValidator;
import org.apache.xerces.impl.validation.datatypes.DatatypeValidatorFactoryImpl;
import org.apache.xerces.impl.validation.XMLAttributeDecl;
import org.apache.xerces.impl.validation.ContentModelValidator;
import org.apache.xerces.impl.validation.XMLElementDecl;
import org.apache.xerces.util.SymbolTable;

import org.apache.xerces.xni.QName;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import org.xml.sax.helpers.NamespaceSupport;

import java.util.Hashtable;

/**
 *  SchemaGrammar holds all the extra information needed for schema validation
 *  on top of the basic information in the Grammar. 
 *
 * @author ericye, IBM
 * @author Stubs generated by DesignDoc on Mon Sep 11 11:10:57 PDT 2000
 * @version $Id: SchemaGrammar.java,v 1.1.2.6 2001/01/30 04:17:02 andyc Exp $
 *
 */
public class SchemaGrammar
    extends Grammar {

    // Constants
    //

    private static final int CHUNK_SHIFT = 8; // 2^8 = 256
    private static final int CHUNK_SIZE = (1 << CHUNK_SHIFT);
    private static final int CHUNK_MASK = CHUNK_SIZE - 1;
    private static final int INITIAL_CHUNK_COUNT = (1 << (10 - CHUNK_SHIFT)); // 2^10 = 1k

    //Temp objects for decls structs.
    private XMLContentSpec fTempContentSpecNode = new XMLContentSpec();
    private XMLElementDecl fTempElementDecl = new XMLElementDecl();
    private XMLAttributeDecl fTempAttributeDecl = new XMLAttributeDecl();

    //
    // Data
    //

    // basic information

    // private Element fGrammarDocument;

    // element decl tables that used only by Schemas
    // these arrays are indexed by elementdeclindex.

    private int fScopeDefinedByElement[][] = new int[INITIAL_CHUNK_COUNT][];
    private String fFromAnotherSchemaURI[][] = new String[INITIAL_CHUNK_COUNT][];
    //private TraverseSchema.ComplexTypeInfo fComplexTypeInfo[][] = 
        //new TraverseSchema.ComplexTypeInfo[INITIAL_CHUNK_COUNT][];
    private int fElementDeclDefaultType[][] = new int[INITIAL_CHUNK_COUNT][];
    private String fElementDeclDefaultValue[][] = new String[INITIAL_CHUNK_COUNT][];
    private String fElementDeclSubstituteGrpFullName[][] = new String[INITIAL_CHUNK_COUNT][];
    private int fElementDeclBlockSet[][] = new int[INITIAL_CHUNK_COUNT][];
    private int fElementDeclFinalSet[][] = new int[INITIAL_CHUNK_COUNT][];
    private int fElementDeclMiscFlags[][] = new int[INITIAL_CHUNK_COUNT][];

    //ComplexType and SimpleTypeRegistries
    private Hashtable fComplexTypeRegistry = null;
    private Hashtable fAttributeDeclRegistry = null;
    private DatatypeValidatorFactoryImpl fDatatypeRegistry = null;

    // Hashes top level group , attributegroup, and attribute components (dom elements) by their name
    Hashtable topLevelGroupDecls = new Hashtable();
    Hashtable topLevelAttrDecls  = new Hashtable();
    Hashtable topLevelAttrGrpDecls = new Hashtable();

    private NamespaceSupport fNamespacesMap = null;
    private String fTargetNamespaceURI = "";

    //
    // Constructors
    //

    /** Default constructor. */
    public SchemaGrammar(SymbolTable symbolTable) {
        super(symbolTable);
    } // <init>(SymbolTable)

    //
    // Public methods
    //

    public NamespaceSupport getNamespacesScope(){
        return fNamespacesMap;
    }

    public String getTargetNamespaceURI(){
        return fTargetNamespaceURI;
    }

    public Hashtable getAttirubteDeclRegistry() {
        return fAttributeDeclRegistry;
    }

    public Hashtable getComplexTypeRegistry(){
        return fComplexTypeRegistry;
    }

    public DatatypeValidatorFactoryImpl getDatatypeRegistry(){
        return fDatatypeRegistry;
    }

    public int getElementDefinedScope(int elementDeclIndex) {
        
        if (elementDeclIndex < -1) {
            return -1;
        }
        int chunk = elementDeclIndex >> CHUNK_SHIFT;
        int index = elementDeclIndex & CHUNK_MASK;
        return fScopeDefinedByElement[chunk][index];

    }
    
    public int getAttributeDeclIndex(int elementIndex, QName attribute) {
        if (elementIndex == -1) {
            return -1;
        }
        int attDefIndex = getFirstAttributeDeclIndex(elementIndex);
        while (attDefIndex != -1) {
            getAttributeDecl(attDefIndex, fTempAttributeDecl);

            if (fTempAttributeDecl.name.localpart == attribute.localpart &&
                fTempAttributeDecl.name.uri == attribute.uri ) {
                return attDefIndex;
            }
            attDefIndex = getNextAttributeDeclIndex(attDefIndex);
        }
        return -1;
    } // getAttributeDeclIndex (int,QName)
    
    public int getElementDefaultTYpe(int elementDeclIndex) {
        
        if (elementDeclIndex < -1) {
            return -1;
        }
        int chunk = elementDeclIndex >> CHUNK_SHIFT;
        int index = elementDeclIndex & CHUNK_MASK;
        return fElementDeclDefaultType[chunk][index];

    }

    public int getElementDeclBlockSet(int elementDeclIndex) {

        if (elementDeclIndex < -1) {
            return -1;
        }
        int chunk = elementDeclIndex >> CHUNK_SHIFT;
        int index = elementDeclIndex & CHUNK_MASK;
        return fElementDeclBlockSet[chunk][index];
    }

    public int getElementDeclFinalSet(int elementDeclIndex) {

        if (elementDeclIndex < -1) {
            return -1;
        }
        int chunk = elementDeclIndex >> CHUNK_SHIFT;
        int index = elementDeclIndex & CHUNK_MASK;
        return fElementDeclFinalSet[chunk][index];
    }

    public int getElementDeclMiscFlags(int elementDeclIndex) {

        if (elementDeclIndex < -1) {
            return -1;
        }
        int chunk = elementDeclIndex >> CHUNK_SHIFT;
        int index = elementDeclIndex & CHUNK_MASK;
        return fElementDeclMiscFlags[chunk][index];
    }

    public String getElementFromAnotherSchemaURI(int elementDeclIndex) {
        
        if (elementDeclIndex < 0 ) {
            return null;
        }
        int chunk = elementDeclIndex >> CHUNK_SHIFT;
        int index = elementDeclIndex & CHUNK_MASK;
        return fFromAnotherSchemaURI[chunk][index];

    }

    public String getElementDefaultValue(int elementDeclIndex) {
        
        if (elementDeclIndex < 0 ) {
            return null;
        }
        int chunk = elementDeclIndex >> CHUNK_SHIFT;
        int index = elementDeclIndex & CHUNK_MASK;
        return fElementDeclDefaultValue[chunk][index];

    }
    public String getElementDeclSubstituteGrpElementFullName( int elementDeclIndex){
        
        if (elementDeclIndex < 0 ) {
            return null;
        }
        int chunk = elementDeclIndex >> CHUNK_SHIFT;
        int index = elementDeclIndex & CHUNK_MASK;
        return fElementDeclSubstituteGrpFullName[chunk][index];

    }

    /****

    public TraverseSchema.ComplexTypeInfo getElementComplexTypeInfo(int elementDeclIndex){

        if (elementDeclIndex <- 1) {
            return null;
        }
        int chunk = elementDeclIndex >> CHUNK_SHIFT;
        int index = elementDeclIndex & CHUNK_MASK;
        return fComplexTypeInfo[chunk][index];
    }
    /****/


    public boolean isDTD() {
        return false;
    }

    //
    // Grammar methods
    //

    /** Returns true if this grammar is namespace aware. */
    public boolean isNamespaceAware() {
        return true;
    } // isNamespaceAware():boolean

    //
    // Protected methods
    //
    protected void  setAttributeDeclRegistry(Hashtable attrReg){
        fAttributeDeclRegistry = attrReg;
    }

    protected void  setComplexTypeRegistry(Hashtable cTypeReg){
        fComplexTypeRegistry = cTypeReg;
    }

    protected void setDatatypeRegistry(DatatypeValidatorFactoryImpl dTypeReg){
        fDatatypeRegistry = dTypeReg;
    }

    protected void setNamespacesScope(NamespaceSupport nsSupport) {
        fNamespacesMap = nsSupport;
    }

    protected void setTargetNamespaceURI(String targetNSUri) {
        fTargetNamespaceURI = targetNSUri;
    }

    protected void setElementDefinedScope(int elementDeclIndex, int scopeDefined) {
        int chunk = elementDeclIndex >> CHUNK_SHIFT;
        int index = elementDeclIndex & CHUNK_MASK;
        ensureElementDeclCapacity(chunk);
        if (elementDeclIndex > -1 ) {
            fScopeDefinedByElement[chunk][index] = scopeDefined;
        }
    }

    protected  void setElementFromAnotherSchemaURI(int elementDeclIndex, String anotherSchemaURI) {
        int chunk = elementDeclIndex >> CHUNK_SHIFT;
        int index = elementDeclIndex & CHUNK_MASK;
        ensureElementDeclCapacity(chunk);
        if (elementDeclIndex > -1 ) {
            fFromAnotherSchemaURI[chunk][index] = anotherSchemaURI;
        }
    }

    /****
    protected void setElementComplexTypeInfo(int elementDeclIndex, TraverseSchema.ComplexTypeInfo typeInfo){
        int chunk = elementDeclIndex >> CHUNK_SHIFT;
        int index = elementDeclIndex & CHUNK_MASK;
        ensureElementDeclCapacity(chunk);
        if (elementDeclIndex > -1 ) {
            fComplexTypeInfo[chunk][index] = typeInfo;
        }
    }
    /****/

    protected void setElementDefault(int elementDeclIndex, int defaultType, String defaultValue) {
        int chunk = elementDeclIndex >> CHUNK_SHIFT;
        int index = elementDeclIndex & CHUNK_MASK;
        ensureElementDeclCapacity(chunk);
        if (elementDeclIndex > -1 ) {
            fElementDeclDefaultType[chunk][index] = defaultType;
            fElementDeclDefaultValue[chunk][index] = defaultValue;
        }
    }
    
    protected void setElementDeclBlockSet(int elementDeclIndex, int blockSet) {
        int chunk = elementDeclIndex >> CHUNK_SHIFT;
        int index = elementDeclIndex & CHUNK_MASK;
        ensureElementDeclCapacity(chunk);
        if (elementDeclIndex > -1 ) {
            fElementDeclBlockSet[chunk][index] = blockSet;
        }
    }

    protected void setElementDeclFinalSet(int elementDeclIndex, int finalSet) {
        int chunk = elementDeclIndex >> CHUNK_SHIFT;
        int index = elementDeclIndex & CHUNK_MASK;
        ensureElementDeclCapacity(chunk);
        if (elementDeclIndex > -1 ) {
            fElementDeclFinalSet[chunk][index] = finalSet;
        }
    }

    protected void setElementDeclMiscFlags(int elementDeclIndex, int miscFlags) {
        int chunk = elementDeclIndex >> CHUNK_SHIFT;
        int index = elementDeclIndex & CHUNK_MASK;
        ensureElementDeclCapacity(chunk);
        if (elementDeclIndex > -1 ) {
            fElementDeclMiscFlags[chunk][index] = miscFlags;
        }
    }

    protected void setElementDeclSubstituteGrpElementFullName( int elementDeclIndex, String SubstituteGrpFullName){
        int chunk = elementDeclIndex >> CHUNK_SHIFT;
        int index = elementDeclIndex & CHUNK_MASK;
        ensureElementDeclCapacity(chunk);
        if (elementDeclIndex > -1 ) {
            fElementDeclSubstituteGrpFullName[chunk][index] = SubstituteGrpFullName;
        }
    }

    //add methods for TraverseSchema
    /**
     *@return elementDecl Index, 
     */

    protected int addElementDecl(QName eltQName, int enclosingScope, int scopeDefined, 
                                 int contentSpecType, int contentSpecIndex, 
                                 int attrListHead, DatatypeValidator dv, short defaultType, String defaultValue){
        int elementDeclIndex = getElementDeclIndex(eltQName, enclosingScope);
        if (elementDeclIndex == -1) {
            if (enclosingScope<-1 || scopeDefined < -1 ) {
                //TO DO: report error here;
            }
            fTempElementDecl.name.setValues(eltQName);
            fTempElementDecl.scope = enclosingScope;
            fTempElementDecl.type = (short)contentSpecType;
            fTempElementDecl.simpleType.datatypeValidator = dv;
            fTempElementDecl.simpleType.defaultType = defaultType;
            fTempElementDecl.simpleType.defaultValue = defaultValue;
            elementDeclIndex = createElementDecl();
            setElementDecl(elementDeclIndex,fTempElementDecl);
            setContentSpecIndex(elementDeclIndex, contentSpecIndex);
            setFirstAttributeDeclIndex(elementDeclIndex, attrListHead);
            //note, this is the scope defined by the element, not its enclosing scope
            setElementDefinedScope(elementDeclIndex, scopeDefined);
        }

        return elementDeclIndex;

    }

    /**
     *@return the new attribute List Head
     */
    protected void addAttDef(  int templateElementIndex, 
                      QName attQName, int attType, 
                      String[] enumeration, int attDefaultType, 
                      String attDefaultValue, DatatypeValidator dv, boolean isList){
        int attrDeclIndex = createAttributeDecl();
        fTempAttributeDecl.name.setValues(attQName);
        fTempAttributeDecl.simpleType.datatypeValidator = dv;
        fTempAttributeDecl.simpleType.type = (short)attType;
        fTempAttributeDecl.simpleType.defaultType = (short)attDefaultType;
        fTempAttributeDecl.simpleType.defaultValue = attDefaultValue;
        fTempAttributeDecl.simpleType.list = isList;
        fTempAttributeDecl.simpleType.enumeration = enumeration;

        super.setAttributeDecl(templateElementIndex, attrDeclIndex, fTempAttributeDecl);
    }


    /**
     * use this method for none Leaf type node
     *@return the new contentSpec Index
     */
    protected int addContentSpecNode(short contentSpecType, int value, int otherValue) {
        fTempContentSpecNode.type = contentSpecType;
        fTempContentSpecNode.value = new int[]{value};
        fTempContentSpecNode.otherValue = new int[]{otherValue};
        
        int contentSpecIndex = createContentSpec();
        setContentSpec(contentSpecIndex, fTempContentSpecNode);
        return contentSpecIndex;
    }
    

    /**
     * use this method for Leaf type node
     *@return the new contentSpec Index
     */
    protected int addContentSpecLeafNode(short contentSpecType, String value, String otherValue) {
        fTempContentSpecNode.type = contentSpecType;
        fTempContentSpecNode.value = value;
        fTempContentSpecNode.otherValue = otherValue;
        
        int contentSpecIndex = createContentSpec();
        setContentSpec(contentSpecIndex, fTempContentSpecNode);
        return contentSpecIndex;
    }

    //
    // Private methods
    //

    // ensure capacity

    private boolean ensureElementDeclCapacity(int chunk) {
        try {
            return  fScopeDefinedByElement[chunk][0] == -2;
        } 
        catch (ArrayIndexOutOfBoundsException ex) {
             fScopeDefinedByElement= resize(fScopeDefinedByElement, fScopeDefinedByElement.length * 2);
             fFromAnotherSchemaURI = resize(fFromAnotherSchemaURI, fFromAnotherSchemaURI.length *2);
             //fComplexTypeInfo =      resize(fComplexTypeInfo, fComplexTypeInfo.length *2);
             fElementDeclDefaultType = resize(fElementDeclDefaultType,fElementDeclDefaultType.length*2);
             fElementDeclDefaultValue = resize(fElementDeclDefaultValue,fElementDeclDefaultValue.length*2);
             fElementDeclBlockSet = resize(fElementDeclBlockSet,fElementDeclBlockSet.length*2);
             fElementDeclFinalSet = resize(fElementDeclFinalSet,fElementDeclFinalSet.length*2);
             fElementDeclMiscFlags = resize(fElementDeclMiscFlags,fElementDeclMiscFlags.length*2);
             fElementDeclSubstituteGrpFullName = resize(fElementDeclSubstituteGrpFullName,fElementDeclSubstituteGrpFullName.length*2);
        }
        catch (NullPointerException ex) {
            // ignore
        }
        fScopeDefinedByElement[chunk] = new int[CHUNK_SIZE];
        for (int i=0; i<CHUNK_SIZE; i++) {
            fScopeDefinedByElement[chunk][i] = -2;  //-1, 0 are all valid scope value.
        }
        fFromAnotherSchemaURI[chunk] = new String[CHUNK_SIZE];
        //fComplexTypeInfo[chunk] = new TraverseSchema.ComplexTypeInfo[CHUNK_SIZE];
        fElementDeclDefaultType[chunk] = new int[CHUNK_SIZE];
        fElementDeclDefaultValue[chunk] = new String[CHUNK_SIZE];
        fElementDeclSubstituteGrpFullName[chunk] = new String[CHUNK_SIZE];
        fElementDeclBlockSet[chunk] = new int[CHUNK_SIZE]; // initialized to 0
        fElementDeclFinalSet[chunk] = new int[CHUNK_SIZE]; // initialized to 0
        fElementDeclMiscFlags[chunk] = new int[CHUNK_SIZE]; // initialized to 0
        return true;
    }


    // resize initial chunk

    private int[][] resize(int array[][], int newsize) {
        int newarray[][] = new int[newsize][];
        System.arraycopy(array, 0, newarray, 0, array.length);
        return newarray;
    }


    private String[][] resize(String array[][], int newsize) {
        String newarray[][] = new String[newsize][];
        System.arraycopy(array, 0, newarray, 0, array.length);
        return newarray;
    }

    /***
    private TraverseSchema.ComplexTypeInfo[][] resize(TraverseSchema.ComplexTypeInfo array[][], int newsize) {
        TraverseSchema.ComplexTypeInfo newarray[][] = new TraverseSchema.ComplexTypeInfo[newsize][];
        System.arraycopy(array, 0, newarray, 0, array.length);
        return newarray;
    }
    /****/


} // class SchemaGrammar
