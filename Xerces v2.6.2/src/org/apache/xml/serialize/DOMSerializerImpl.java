/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 1999-2004 The Apache Software Foundation.  All rights
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
 * originally based on software copyright (c) 2002, International
 * Business Machines, Inc., http://www.apache.org.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */



package org.apache.xml.serialize;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.Vector;

import org.apache.xerces.dom.AttrImpl;
import org.apache.xerces.dom.AttributeMap;
import org.apache.xerces.dom.CoreDocumentImpl;
import org.apache.xerces.dom.DOMErrorImpl;
import org.apache.xerces.dom.DOMLocatorImpl;
import org.apache.xerces.dom.DOMMessageFormatter;
import org.apache.xerces.dom.DOMNormalizer;
import org.apache.xerces.dom.DOMStringListImpl;
import org.apache.xerces.dom3.DOMConfiguration;
import org.apache.xerces.dom3.DOMError;
import org.apache.xerces.dom3.DOMErrorHandler;
import org.apache.xerces.dom3.DOMStringList;
import org.apache.xerces.impl.Constants;
import org.apache.xerces.impl.XMLEntityManager;
import org.apache.xerces.util.NamespaceSupport;
import org.apache.xerces.util.SymbolTable;
import org.apache.xerces.util.XML11Char;
import org.apache.xerces.util.XMLChar;
import org.w3c.dom.Attr;
import org.w3c.dom.Comment;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.ProcessingInstruction;
import org.w3c.dom.ls.LSException;
import org.w3c.dom.ls.LSOutput;
import org.w3c.dom.ls.LSSerializer;
import org.w3c.dom.ls.LSSerializerFilter;


/**
 * EXPERIMENTAL: Implemenatation of DOM Level 3 org.w3c.ls.LSSerializer  by delegating serialization
 * calls to <CODE>XMLSerializer</CODE>.
 * LSSerializer provides an API for serializing (writing) a DOM document out in an
 * XML document. The XML data is written to an output stream.
 * During serialization of XML data, namespace fixup is done when possible as
 * defined in DOM Level 3 Core, Appendix B.
 *
 * @author Elena Litani, IBM
 * @author Gopal Sharma, Sun Microsystems
 * @author Arun Yadav, Sun Microsystems
 * @version $Id: DOMSerializerImpl.java,v 1.16 2004/02/10 17:25:26 elena Exp $
 */
public class DOMSerializerImpl implements LSSerializer, DOMConfiguration {

    // data
    // serializer
    private XMLSerializer serializer;

    // XML 1.1 serializer
    private XML11Serializer xml11Serializer;
    
    //Recognized parameters
    private DOMStringList fRecognizedParameters;
    
    /** REVISIT: Currently we handle 3 different configurations, would be nice just have one configuration
     * that has different recognized parameters depending if it is used in Core/LS. 
     */
    protected short features = 0;

    protected final static short NAMESPACES          = 0x1<<0;
    protected final static short WELLFORMED          = 0x1<<1;
    protected final static short ENTITIES            = 0x1<<2;
    protected final static short CDATA               = 0x1<<3;
    protected final static short SPLITCDATA          = 0x1<<4;
    protected final static short COMMENTS            = 0x1<<5;
    protected final static short DISCARDDEFAULT      = 0x1<<6;
    protected final static short INFOSET             = 0x1<<7;
    protected final static short XMLDECL             = 0x1<<8;
    protected final static short NSDECL              = 0x1<<9;
    protected final static short DOM_ELEMENT_CONTENT_WHITESPACE = 0x1<<10;
    
    // well-formness checking
    private DOMErrorHandler fErrorHandler = null;    
    private final DOMErrorImpl fError = new DOMErrorImpl();
    private final DOMLocatorImpl fLocator = new DOMLocatorImpl();
    private static final RuntimeException abort = new RuntimeException();

    /**
     * Constructs a new LSSerializer.
     * The constructor turns on the namespace support in <code>XMLSerializer</code> and
     * initializes the following fields: fNSBinder, fLocalNSBinder, fSymbolTable,
     * fEmptySymbol, fXmlSymbol, fXmlnsSymbol, fNamespaceCounter, fFeatures.
     */
    public DOMSerializerImpl() {
        // set default features
        features |= NAMESPACES;
        features |= ENTITIES;
        features |= COMMENTS;
        features |= CDATA;
        features |= SPLITCDATA;
        features |= WELLFORMED;
        features |= NSDECL;
        features |= DOM_ELEMENT_CONTENT_WHITESPACE;
        features |= DISCARDDEFAULT;
        features |= XMLDECL;
             
        serializer = new XMLSerializer();
        initSerializer(serializer);
    }



    //
    // LSSerializer methods
    //

    public DOMConfiguration getDomConfig(){
        return this;
    }

    /** DOM L3-EXPERIMENTAL:
     * Setter for boolean and object parameters
     */
    public void setParameter(String name, Object value) throws DOMException {
        if (value instanceof Boolean) {
            boolean state = ((Boolean) value).booleanValue();
            if (name.equals(Constants.DOM_INFOSET)){
                if (state){
                    features &=~ENTITIES;
                    features &=~CDATA;
                    features |=NAMESPACES;
                    features |=WELLFORMED;
                    features |=COMMENTS;                 
                }
                // false does not have any effect
            } else if (name.equals(Constants.DOM_XMLDECL)) {
                features =
                    (short) (state ? features | XMLDECL : features & ~XMLDECL);                
            } else if (name.equals(Constants.DOM_NAMESPACES)) {
                features =
                    (short) (state
                        ? features | NAMESPACES
                        : features & ~NAMESPACES);
                serializer.fNamespaces = state;
            } else if (name.equals(Constants.DOM_SPLIT_CDATA)) {
                features =
                    (short) (state
                        ? features | SPLITCDATA
                        : features & ~SPLITCDATA);
            } else if (name.equals(Constants.DOM_DISCARD_DEFAULT_CONTENT)) {
                features =
                    (short) (state
                        ? features | DISCARDDEFAULT
                        : features & ~DISCARDDEFAULT);
            } else if (name.equals(Constants.DOM_WELLFORMED)) {
                features =
                    (short) (state
                        ? features | WELLFORMED
                        : features & ~WELLFORMED);
            } else if (name.equals(Constants.DOM_ENTITIES)){
                features =
                    (short) (state
                        ? features | ENTITIES
                        : features & ~ENTITIES);
            }
            else if (name.equals(Constants.DOM_CDATA_SECTIONS)){
                features =
                    (short) (state
                        ? features | CDATA
                        : features & ~CDATA);
                        }
            else if (name.equals(Constants.DOM_COMMENTS)){
                features =
                     (short) (state
                         ? features | COMMENTS
                         : features & ~COMMENTS);
            }
                else if (name.equals(Constants.DOM_CANONICAL_FORM)
                    || name.equals(Constants.DOM_VALIDATE_IF_SCHEMA)
                    || name.equals(Constants.DOM_VALIDATE)
                    || name.equals(Constants.DOM_CHECK_CHAR_NORMALIZATION)
                    || name.equals(Constants.DOM_DATATYPE_NORMALIZATION)
                    || name.equals(Constants.DOM_FORMAT_PRETTY_PRINT)
                    || name.equals(Constants.DOM_NORMALIZE_CHARACTERS)) {
                // true is not supported
                if (state) {
                    String msg =
                        DOMMessageFormatter.formatMessage(
                            DOMMessageFormatter.DOM_DOMAIN,
                            "FEATURE_NOT_SUPPORTED",
                            new Object[] { name });
                    throw new DOMException(DOMException.NOT_SUPPORTED_ERR, msg);
                }
            } else if (
                name.equals(Constants.DOM_NAMESPACE_DECLARATIONS)
                    || name.equals(Constants.DOM_ELEMENT_CONTENT_WHITESPACE)
                    || name.equals(Constants.DOM_IGNORE_UNKNOWN_CHARACTER_DENORMALIZATIONS)) {
                // false is not supported
                if (!state) {
                    String msg =
                        DOMMessageFormatter.formatMessage(
                            DOMMessageFormatter.DOM_DOMAIN,
                            "FEATURE_NOT_SUPPORTED",
                            new Object[] { name });
                    throw new DOMException(DOMException.NOT_SUPPORTED_ERR, msg);
                }
            } else {
                String msg =
                    DOMMessageFormatter.formatMessage(
                        DOMMessageFormatter.DOM_DOMAIN,
                        "FEATURE_NOT_FOUND",
                        new Object[] { name });
                throw new DOMException(DOMException.NOT_SUPPORTED_ERR, msg);
            }
        } else if (name.equals(Constants.DOM_ERROR_HANDLER)) {
            if (value == null || value instanceof DOMErrorHandler) {
                fErrorHandler = (DOMErrorHandler)value;
            } else {
                // REVISIT: modify error exception to TYPE_MISMATCH
                String msg =
                    DOMMessageFormatter.formatMessage(
                        DOMMessageFormatter.DOM_DOMAIN,
                        "TYPE_MISMATCH_ERR",
                        new Object[] { name });
                throw new DOMException(DOMException.NOT_FOUND_ERR, msg);
            }
        } else if (
            name.equals(Constants.DOM_RESOURCE_RESOLVER)
                || name.equals(Constants.DOM_SCHEMA_LOCATION)
                || name.equals(Constants.DOM_SCHEMA_TYPE)
                && value != null) {
            String msg =
                DOMMessageFormatter.formatMessage(
                    DOMMessageFormatter.DOM_DOMAIN,
                    "FEATURE_NOT_SUPPORTED",
                    new Object[] { name });
            throw new DOMException(DOMException.NOT_SUPPORTED_ERR, msg);
        } else {
            String msg =
                DOMMessageFormatter.formatMessage(
                    DOMMessageFormatter.DOM_DOMAIN,
                    "FEATURE_NOT_FOUND",
                    new Object[] { name });
            throw new DOMException(DOMException.NOT_FOUND_ERR, msg);
        }
    }

    /** DOM L3-EXPERIMENTAL:
     * Check if parameter can be set
     */
	public boolean canSetParameter(String name, Object state) {
		if (state instanceof Boolean){
			boolean value = ((Boolean)state).booleanValue();
			if (name.equals(Constants.DOM_NAMESPACES)
			|| name.equals(Constants.DOM_SPLIT_CDATA)
			|| name.equals(Constants.DOM_DISCARD_DEFAULT_CONTENT)
			|| name.equals(Constants.DOM_XMLDECL)
            || name.equals(Constants.DOM_WELLFORMED)
            || name.equals(Constants.DOM_INFOSET)
            || name.equals(Constants.DOM_ENTITIES)
            || name.equals(Constants.DOM_CDATA_SECTIONS)
            || name.equals(Constants.DOM_COMMENTS)){
	            // both values supported
				return true;
			}
			else if (name.equals(Constants.DOM_CANONICAL_FORM)
			    || name.equals(Constants.DOM_VALIDATE_IF_SCHEMA)
			    || name.equals(Constants.DOM_VALIDATE)
			    || name.equals(Constants.DOM_CHECK_CHAR_NORMALIZATION)
			    || name.equals(Constants.DOM_DATATYPE_NORMALIZATION)
			    || name.equals(Constants.DOM_FORMAT_PRETTY_PRINT)
			    || name.equals(Constants.DOM_NORMALIZE_CHARACTERS)) {
				// true is not supported
				return !value;
			}
			else if (name.equals(Constants.DOM_NAMESPACE_DECLARATIONS)
			        || name.equals(Constants.DOM_ELEMENT_CONTENT_WHITESPACE)
			        || name.equals(Constants.DOM_IGNORE_UNKNOWN_CHARACTER_DENORMALIZATIONS)) {
				// false is not supported
				return value;
			        }
		}
		else if (name.equals(Constants.DOM_ERROR_HANDLER) &&
				state == null || state instanceof DOMErrorHandler){
			return true;
		}
	    return false;
    }

    /**
     *  DOM Level 3 Core CR - Experimental.
     * 
     *  The list of the parameters supported by this 
     * <code>DOMConfiguration</code> object and for which at least one value 
     * can be set by the application. Note that this list can also contain 
     * parameter names defined outside this specification. 
     */
    public DOMStringList getParameterNames() {
    	
     	if (fRecognizedParameters == null){
			Vector parameters = new Vector();

			//Add DOM recognized parameters
			//REVISIT: Would have been nice to have a list of 
			//recognized parameters.
			parameters.add(Constants.DOM_NAMESPACES);
			parameters.add(Constants.DOM_SPLIT_CDATA);
			parameters.add(Constants.DOM_DISCARD_DEFAULT_CONTENT);
			parameters.add(Constants.DOM_XMLDECL);
			parameters.add(Constants.DOM_CANONICAL_FORM);
			parameters.add(Constants.DOM_VALIDATE_IF_SCHEMA);
			parameters.add(Constants.DOM_VALIDATE);
			parameters.add(Constants.DOM_CHECK_CHAR_NORMALIZATION);
			parameters.add(Constants.DOM_DATATYPE_NORMALIZATION);
			parameters.add(Constants.DOM_FORMAT_PRETTY_PRINT);
			parameters.add(Constants.DOM_NORMALIZE_CHARACTERS);
			parameters.add(Constants.DOM_WELLFORMED);
			parameters.add(Constants.DOM_INFOSET);
			parameters.add(Constants.DOM_NAMESPACE_DECLARATIONS);
			parameters.add(Constants.DOM_ELEMENT_CONTENT_WHITESPACE);
			parameters.add(Constants.DOM_ENTITIES);
			parameters.add(Constants.DOM_CDATA_SECTIONS);
			parameters.add(Constants.DOM_COMMENTS);
			parameters.add(Constants.DOM_IGNORE_UNKNOWN_CHARACTER_DENORMALIZATIONS);
			parameters.add(Constants.DOM_ERROR_HANDLER);
			//parameters.add(Constants.DOM_SCHEMA_LOCATION);
			//parameters.add(Constants.DOM_SCHEMA_TYPE);
			
			//Add recognized xerces features and properties
			
			fRecognizedParameters = new DOMStringListImpl(parameters);		
    		
    	}

    	return fRecognizedParameters; 	
    }	
    
    /** DOM L3-EXPERIMENTAL:
     * Getter for boolean and object parameters
     */
    public Object getParameter(String name) throws DOMException {
        if (name.equals(Constants.DOM_COMMENTS)) {
            return ((features & COMMENTS) != 0) ? Boolean.TRUE : Boolean.FALSE;
        } else if (name.equals(Constants.DOM_NAMESPACES)) {
            return (features & NAMESPACES) != 0 ? Boolean.TRUE : Boolean.FALSE;
        } else if (name.equals(Constants.DOM_XMLDECL)) {
            return (features & XMLDECL) != 0 ? Boolean.TRUE : Boolean.FALSE;
        } else if (name.equals(Constants.DOM_CDATA_SECTIONS)) {
            return (features & CDATA) != 0 ? Boolean.TRUE : Boolean.FALSE;
        } else if (name.equals(Constants.DOM_ENTITIES)) {
            return (features & ENTITIES) != 0 ? Boolean.TRUE : Boolean.FALSE;
        } else if (name.equals(Constants.DOM_SPLIT_CDATA)) {
            return (features & SPLITCDATA) != 0 ? Boolean.TRUE : Boolean.FALSE;
        } else if (name.equals(Constants.DOM_WELLFORMED)) {
            return (features & WELLFORMED) != 0 ? Boolean.TRUE : Boolean.FALSE;
        } else if (name.equals(Constants.DOM_ELEMENT_CONTENT_WHITESPACE) ||
                   name.equals(Constants.DOM_IGNORE_UNKNOWN_CHARACTER_DENORMALIZATIONS)
                    || name.equals(Constants.DOM_NAMESPACE_DECLARATIONS)) {
            return Boolean.TRUE;
        }else if (name.equals(Constants.DOM_DISCARD_DEFAULT_CONTENT)){
            return ((features & DISCARDDEFAULT)!=0)?Boolean.TRUE:Boolean.FALSE;
        }else if (name.equals(Constants.DOM_INFOSET)){
            if ((features & ENTITIES) == 0 &&
                 (features & CDATA) ==0 &&
                 (features & NAMESPACES) !=0 &&
                 (features & WELLFORMED) !=0 &&
                 (features & COMMENTS) !=0){
                     return Boolean.TRUE;
                 }                 
                 return Boolean.FALSE;
        } else if (name.equals (Constants.DOM_FORMAT_PRETTY_PRINT)
                || name.equals(Constants.DOM_NORMALIZE_CHARACTERS)
                || name.equals(Constants.DOM_CANONICAL_FORM)
                || name.equals(Constants.DOM_VALIDATE_IF_SCHEMA)
                || name.equals(Constants.DOM_CHECK_CHAR_NORMALIZATION)
                || name.equals(Constants.DOM_VALIDATE)
                || name.equals(Constants.DOM_VALIDATE_IF_SCHEMA)
                || name.equals(Constants.DOM_DATATYPE_NORMALIZATION)) {
            return Boolean.FALSE;
        } else if (name.equals(Constants.DOM_ERROR_HANDLER)) {
            return fErrorHandler;
        } else if (
            name.equals(Constants.DOM_RESOURCE_RESOLVER)
                || name.equals(Constants.DOM_SCHEMA_LOCATION)
                || name.equals(Constants.DOM_SCHEMA_TYPE)) {
            String msg =
                DOMMessageFormatter.formatMessage(
                    DOMMessageFormatter.DOM_DOMAIN,
                    "FEATURE_NOT_SUPPORTED",
                    new Object[] { name });
            throw new DOMException(DOMException.NOT_SUPPORTED_ERR, msg);
        } else {
            String msg =
                DOMMessageFormatter.formatMessage(
                    DOMMessageFormatter.DOM_DOMAIN,
                    "FEATURE_NOT_FOUND",
                    new Object[] { name });
            throw new DOMException(DOMException.NOT_FOUND_ERR, msg);
        }
    }


    /**
     * DOM L3 EXPERIMENTAL:
     *  Serialize the specified node as described above in the description of
     * <code>LSSerializer</code>. The result of serializing the node is
     * returned as a string. Writing a Document or Entity node produces a
     * serialized form that is well formed XML. Writing other node types
     * produces a fragment of text in a form that is not fully defined by
     * this document, but that should be useful to a human for debugging or
     * diagnostic purposes.
     * @param wnode  The node to be written.
     * @return  Returns the serialized data, or <code>null</code> in case a
     *   failure occured and the failure wasn't canceled by the error
     *   handler.
     * @exception DOMException
     *    DOMSTRING_SIZE_ERR: The resulting string is too long to fit in a
     *   <code>DOMString</code>.
     */
    public String writeToString(Node wnode) throws DOMException, LSException {
        // determine which serializer to use:
        Document doc = (wnode.getNodeType() == Node.DOCUMENT_NODE)?(Document)wnode:wnode.getOwnerDocument();
        Method getVersion = null;
        XMLSerializer ser = null;
        String ver = null;
        // this should run under JDK 1.1.8...
        try {
            getVersion = doc.getClass().getMethod("getXmlVersion", new Class[]{});
            if(getVersion != null ) {
                ver = (String)getVersion.invoke(doc, null);
            }
        } catch (Exception e) {
            // no way to test the version...
            // ignore the exception
        }
        if(ver != null && ver.equals("1.1")) {
            if(xml11Serializer == null) {
                xml11Serializer = new XML11Serializer();
                initSerializer(xml11Serializer);
            }
            // copy setting from "main" serializer to XML 1.1 serializer
            copySettings(serializer, xml11Serializer);
            ser = xml11Serializer;
        } else {
            ser = serializer;
        }

        StringWriter destination = new StringWriter();
        try {
            prepareForSerialization(ser, wnode);           
            ser.setOutputCharStream(destination);
            if (wnode == null)
                return null;
            else if (wnode.getNodeType() == Node.DOCUMENT_NODE)
                ser.serialize((Document)wnode);
            else if (wnode.getNodeType() == Node.DOCUMENT_FRAGMENT_NODE)
                ser.serialize((DocumentFragment)wnode);
            else if (wnode.getNodeType() == Node.ELEMENT_NODE)
                ser.serialize((Element)wnode);
            else
                return null;
        } catch (RuntimeException e) {
            if (e == DOMNormalizer.abort){
                // stopped at user request
                return null;
            }
            throw new LSException(LSException.SERIALIZE_ERR, e.toString());            
        } catch (IOException ioe) {
	        String msg = DOMMessageFormatter.formatMessage(
			    DOMMessageFormatter.DOM_DOMAIN,
				"STRING_TOO_LONG",
				new Object[] { ioe.getMessage()});
            throw new DOMException(DOMException.DOMSTRING_SIZE_ERR,msg);
        }
        return destination.toString();
    }

    /**
     * DOM L3 EXPERIMENTAL:
     * The end-of-line sequence of characters to be used in the XML being
     * written out. The only permitted values are these:
     * <dl>
     * <dt><code>null</code></dt>
     * <dd>
     * Use a default end-of-line sequence. DOM implementations should choose
     * the default to match the usual convention for text files in the
     * environment being used. Implementations must choose a default
     * sequence that matches one of those allowed by  2.11 "End-of-Line
     * Handling". </dd>
     * <dt>CR</dt>
     * <dd>The carriage-return character (#xD).</dd>
     * <dt>CR-LF</dt>
     * <dd> The
     * carriage-return and line-feed characters (#xD #xA). </dd>
     * <dt>LF</dt>
     * <dd> The line-feed
     * character (#xA). </dd>
     * </dl>
     * <br>The default value for this attribute is <code>null</code>.
     */
    public void setNewLine(String newLine) {
        serializer._format.setLineSeparator(newLine);
    }


    /**
     * DOM L3 EXPERIMENTAL:
     * The end-of-line sequence of characters to be used in the XML being
     * written out. The only permitted values are these:
     * <dl>
     * <dt><code>null</code></dt>
     * <dd>
     * Use a default end-of-line sequence. DOM implementations should choose
     * the default to match the usual convention for text files in the
     * environment being used. Implementations must choose a default
     * sequence that matches one of those allowed by  2.11 "End-of-Line
     * Handling". </dd>
     * <dt>CR</dt>
     * <dd>The carriage-return character (#xD).</dd>
     * <dt>CR-LF</dt>
     * <dd> The
     * carriage-return and line-feed characters (#xD #xA). </dd>
     * <dt>LF</dt>
     * <dd> The line-feed
     * character (#xA). </dd>
     * </dl>
     * <br>The default value for this attribute is <code>null</code>.
     */
    public String getNewLine() {
        return serializer._format.getLineSeparator();
    }


    /**
     *  When the application provides a filter, the serializer will call out
     * to the filter before serializing each Node. Attribute nodes are never
     * passed to the filter. The filter implementation can choose to remove
     * the node from the stream or to terminate the serialization early.
     */
    public LSSerializerFilter getFilter(){
        return serializer.fDOMFilter;
    }
    /**
     *  When the application provides a filter, the serializer will call out
     * to the filter before serializing each Node. Attribute nodes are never
     * passed to the filter. The filter implementation can choose to remove
     * the node from the stream or to terminate the serialization early.
     */
    public void setFilter(LSSerializerFilter filter){
        serializer.fDOMFilter = filter;
    }

    // this initializes a newly-created serializer
    private void initSerializer(XMLSerializer ser) {
        ser.fNSBinder = new NamespaceSupport();
        ser.fLocalNSBinder = new NamespaceSupport();
        ser.fSymbolTable = new SymbolTable();	
    }

    // copies all settings that could have been modified
    // by calls to LSSerializer methods from one serializer to another.
    // IMPORTANT:  if new methods are implemented or more settings of
    // the serializer are made alterable, this must be
    // reflected in this method!
    private void copySettings(XMLSerializer src, XMLSerializer dest) {
        dest.fDOMErrorHandler = fErrorHandler;
        dest._format.setEncoding(src._format.getEncoding());
        dest._format.setLineSeparator(src._format.getLineSeparator());
        dest.fDOMFilter = src.fDOMFilter;
    }//copysettings

    /**
      *  Serialize the specified node as described above in the general
      * description of the <code>LSSerializer</code> interface. The output
      * is written to the supplied <code>LSOutput</code>.
      * <br> When writing to a <code>LSOutput</code>, the encoding is found by
      * looking at the encoding information that is reachable through the
      * <code>LSOutput</code> and the item to be written (or its owner
      * document) in this order:
      * <ol>
      * <li> <code>LSOutput.encoding</code>,
      * </li>
      * <li>
      * <code>Document.actualEncoding</code>,
      * </li>
      * <li>
      * <code>Document.xmlEncoding</code>.
      * </li>
      * </ol>
      * <br> If no encoding is reachable through the above properties, a
      * default encoding of "UTF-8" will be used.
      * <br> If the specified encoding is not supported an
      * "unsupported-encoding" error is raised.
      * <br> If no output is specified in the <code>LSOutput</code>, a
      * "no-output-specified" error is raised.
      * @param node  The node to serialize.
      * @param destination The destination for the serialized DOM.
      * @return  Returns <code>true</code> if <code>node</code> was
      *   successfully serialized and <code>false</code> in case the node
      *   couldn't be serialized.
      */
    public boolean write(Node node, LSOutput destination) throws LSException{

        if (node == null)
            return false;
            
        Method getVersion = null;
        XMLSerializer ser = null;
        String ver = null;
        Document fDocument =(node.getNodeType() == Node.DOCUMENT_NODE)
                ? (Document) node
                : node.getOwnerDocument();
        // this should run under JDK 1.1.8...
        try {
            getVersion = fDocument.getClass().getMethod("getXmlVersion", new Class[] {});
            if (getVersion != null) {
                ver = (String) getVersion.invoke(fDocument, null);
            }
        } catch (Exception e) {
            //no way to test the version...
            //ignore the exception
        }
        //determine which serializer to use:
        if (ver != null && ver.equals("1.1")) {
            if (xml11Serializer == null) {
                xml11Serializer = new XML11Serializer();
                initSerializer(xml11Serializer);
            }
            //copy setting from "main" serializer to XML 1.1 serializer
            copySettings(serializer, xml11Serializer);
            ser = xml11Serializer;
        } else {
            ser = serializer;
        }

        String encoding = null;
        if ((encoding = destination.getEncoding()) == null) {
            try {
                Method getEncoding =
                    fDocument.getClass().getMethod("getActualEncoding", new Class[] {});
                if (getEncoding != null) {
                    encoding = (String) getEncoding.invoke(fDocument, null);
                }
            } catch (Exception e) {
                // ignore the exception
            }
            if (encoding == null) {
                try {
                    Method getEncoding =
                        fDocument.getClass().getMethod("getXmlEncoding", new Class[] {});
                    if (getEncoding != null) {
                        encoding = (String) getEncoding.invoke(fDocument, null);
                    }
                } catch (Exception e) {
                    // ignore the exception
                }
                if (encoding == null) {
                    encoding = "UTF-8";
                }
            }
        }
        try {
            prepareForSerialization(ser, node);
            ser._format.setEncoding(encoding);
            OutputStream outputStream = destination.getByteStream();
            Writer writer = destination.getCharacterStream();
            String uri =  destination.getSystemId();
            if (writer == null) {
                if (outputStream == null) {
                    if (uri == null) {
                        if (ser.fDOMErrorHandler != null) {
                            DOMErrorImpl error = new DOMErrorImpl();
                            error.fType = "no-output-specified";
                            error.fMessage = "no-output-specified";
                            error.fSeverity = DOMError.SEVERITY_FATAL_ERROR;
                            ser.fDOMErrorHandler.handleError(error);
                        }
                        throw new LSException(LSException.SERIALIZE_ERR, 
                            DOMMessageFormatter.formatMessage(
                                DOMMessageFormatter.SERIALIZER_DOMAIN, 
                                "no-output-specified", null));
                    }
                    else {
                        // URI was specified. Handle relative URIs.
                        String expanded = XMLEntityManager.expandSystemId(uri, null, true);
                        URL url = new URL(expanded != null ? expanded : uri);
                        OutputStream out = null;
                        String protocol = url.getProtocol();
                        String host = url.getHost();
                        // Use FileOutputStream if this URI is for a local file.
                        if (protocol.equals("file") 
                            && (host == null || host.length() == 0 || host.equals("localhost"))) {
                            // REVISIT: We have to decode %nn sequences. For
                            // now files containing spaces and other characters
                            // which were escaped in the URI will fail. -- mrglavas
                            out = new FileOutputStream(new File(url.getPath()));
                        }
                        // Try to write to some other kind of URI. Some protocols
                        // won't support this, though HTTP should work.
                        else {
                            URLConnection urlCon = url.openConnection();
                            urlCon.setDoInput(false);
                            urlCon.setDoOutput(true);
                            urlCon.setUseCaches(false); // Enable tunneling.
                            if (urlCon instanceof HttpURLConnection) {
                                // The DOM L3 LS CR says if we are writing to an HTTP URI
                                // it is to be done with an HTTP PUT. 
                                HttpURLConnection httpCon = (HttpURLConnection) urlCon;
                                httpCon.setRequestMethod("PUT");
                            }
                            out = urlCon.getOutputStream();
                        }
                        ser.setOutputByteStream(out);
                    }
                }
                else {
                    // byte stream was specified
                    ser.setOutputByteStream(outputStream);
                }
            }
            else {
                // character stream is specified
                ser.setOutputCharStream(writer); 
            }

            if (node.getNodeType() == Node.DOCUMENT_NODE)
                ser.serialize((Document) node);
            else if (node.getNodeType() == Node.DOCUMENT_FRAGMENT_NODE)
                ser.serialize((DocumentFragment) node);
            else if (node.getNodeType() == Node.ELEMENT_NODE)
                ser.serialize((Element) node);
            else
                return false;
        } catch( UnsupportedEncodingException ue) {
            if (ser.fDOMErrorHandler != null) {
                DOMErrorImpl error = new DOMErrorImpl();
                error.fException = ue;
				error.fType = "unsupported-encoding";
                error.fMessage = ue.getMessage();
				error.fSeverity = DOMError.SEVERITY_FATAL_ERROR;
                ser.fDOMErrorHandler.handleError(error);
			}
            throw new LSException(LSException.SERIALIZE_ERR, 
                DOMMessageFormatter.formatMessage(
                    DOMMessageFormatter.SERIALIZER_DOMAIN, 
                    "unsupported-encoding", null));			
			//return false;
        } catch (RuntimeException e) {
            if (e == DOMNormalizer.abort){
                // stopped at user request
                return false;
            }
            throw new LSException(LSException.SERIALIZE_ERR, e.toString());            
        } catch (Exception e) {
            if (ser.fDOMErrorHandler != null) {
                DOMErrorImpl error = new DOMErrorImpl();
                error.fException = e;
                error.fMessage = e.getMessage();
                error.fSeverity = DOMError.SEVERITY_ERROR;
                ser.fDOMErrorHandler.handleError(error);

            }   
            e.printStackTrace();       
            throw new LSException(LSException.SERIALIZE_ERR, e.toString());
        }
        return true;

    } //write

    /**
      *  Serialize the specified node as described above in the general
      * description of the <code>LSSerializer</code> interface. The output
      * is written to the supplied URI.
      * <br> When writing to a URI, the encoding is found by looking at the
      * encoding information that is reachable through the item to be written
      * (or its owner document) in this order:
      * <ol>
      * <li>
      * <code>Document.actualEncoding</code>,
      * </li>
      * <li>
      * <code>Document.xmlEncoding</code>.
      * </li>
      * </ol>
      * <br> If no encoding is reachable through the above properties, a
      * default encoding of "UTF-8" will be used.
      * <br> If the specified encoding is not supported an
      * "unsupported-encoding" error is raised.
      * @param node  The node to serialize.
      * @param URI The URI to write to.
      * @return  Returns <code>true</code> if <code>node</code> was
      *   successfully serialized and <code>false</code> in case the node
      *   couldn't be serialized.
      */
    public boolean writeToURI(Node node, String URI) throws LSException{
        if (node == null){
            return false;
        }

        Method getXmlVersion = null;
        XMLSerializer ser = null;
        String ver = null;
        String encoding = null;

        Document fDocument =(node.getNodeType() == Node.DOCUMENT_NODE)
                ? (Document) node
                : node.getOwnerDocument();
        // this should run under JDK 1.1.8...
        try {
            getXmlVersion =
                fDocument.getClass().getMethod("getXmlVersion", new Class[] {});
            if (getXmlVersion != null) {
                ver = (String) getXmlVersion.invoke(fDocument, null);
            }
        } catch (Exception e) {
            // no way to test the version...
            // ignore the exception
        }
        if (ver != null && ver.equals("1.1")) {
            if (xml11Serializer == null) {
                xml11Serializer = new XML11Serializer();
                initSerializer(xml11Serializer);
            }
            // copy setting from "main" serializer to XML 1.1 serializer
            copySettings(serializer, xml11Serializer);
            ser = xml11Serializer;
        } else {
            ser = serializer;
        }        

        try {
            Method getEncoding =
                fDocument.getClass().getMethod("getActualEncoding", new Class[] {});
            if (getEncoding != null) {
                encoding = (String) getEncoding.invoke(fDocument, null);
            }
        } catch (Exception e) {
            // ignore the exception
        }
        if (encoding == null) {
            try {
                Method getEncoding =
                    fDocument.getClass().getMethod("getXmlEncoding", new Class[] {});
                if (getEncoding != null) {
                    encoding = (String) getEncoding.invoke(fDocument, null);
                }
            } catch (Exception e) {
                // ignore the exception
            }
            if (encoding == null) {
                encoding = "UTF-8";
            }
        }

        try {
            prepareForSerialization(ser, node);
            ser._format.setEncoding(encoding);
            
            // URI was specified. Handle relative URIs.
            String expanded = XMLEntityManager.expandSystemId(URI, null, true);
            URL url = new URL(expanded != null ? expanded : URI);
            OutputStream out = null;
            String protocol = url.getProtocol();
            String host = url.getHost();
            // Use FileOutputStream if this URI is for a local file.
            if (protocol.equals("file") 
                && (host == null || host.length() == 0 || host.equals("localhost"))) {
                // REVISIT: We have to decode %nn sequences. For
                // now files containing spaces and other characters
                // which were escaped in the URI will fail. -- mrglavas
                out = new FileOutputStream(new File(url.getPath()));
            }
            // Try to write to some other kind of URI. Some protocols
            // won't support this, though HTTP should work.
            else {
                URLConnection urlCon = url.openConnection();
                urlCon.setDoInput(false);
                urlCon.setDoOutput(true);
                urlCon.setUseCaches(false); // Enable tunneling.
                if (urlCon instanceof HttpURLConnection) {
                    // The DOM L3 LS CR says if we are writing to an HTTP URI
                    // it is to be done with an HTTP PUT. 
                    HttpURLConnection httpCon = (HttpURLConnection) urlCon;
                    httpCon.setRequestMethod("PUT");
                }
                out = urlCon.getOutputStream();
            }
            ser.setOutputByteStream(out);

            if (node.getNodeType() == Node.DOCUMENT_NODE)
                ser.serialize((Document) node);
            else if (node.getNodeType() == Node.DOCUMENT_FRAGMENT_NODE)
                ser.serialize((DocumentFragment) node);
            else if (node.getNodeType() == Node.ELEMENT_NODE)
                ser.serialize((Element) node);
            else
                return false;
        } catch (RuntimeException e) {
            if (e == DOMNormalizer.abort){
                // stopped at user request
                return false;
            }
            throw new LSException(LSException.SERIALIZE_ERR, e.toString());            
        } catch (Exception e) {
            if (ser.fDOMErrorHandler != null) {
                DOMErrorImpl error = new DOMErrorImpl();
                error.fException = e;
                error.fMessage = e.getMessage();
                error.fSeverity = DOMError.SEVERITY_ERROR;
                ser.fDOMErrorHandler.handleError(error);
            }
            throw new LSException(LSException.SERIALIZE_ERR, e.toString());
        }
        return true;
    } //writeURI
    
    
    //
    //  Private methods
    //

    private void prepareForSerialization(XMLSerializer ser, Node node) {
        ser.reset();
        ser.features = features;
        ser.fDOMErrorHandler = fErrorHandler;
        ser.fNamespaces = (features & NAMESPACES) !=0;
        ser._format.setOmitComments((features & COMMENTS)==0);
        ser._format.setOmitXMLDeclaration((features & XMLDECL) == 0);   
 
        if ((features & WELLFORMED) != 0) {
            // REVISIT: this is inefficient implementation of well-formness. Instead, we should check
            // well-formness as we serialize the tree
            Node next, root;
            root = node;
            Method versionChanged;
            boolean verifyNames = true;
            Document document =(node.getNodeType() == Node.DOCUMENT_NODE)
                    ? (Document) node
                    : node.getOwnerDocument();
            try {
                versionChanged = document.getClass().getMethod("isXMLVersionChanged()", new Class[] {});
                if (versionChanged != null) {
                    verifyNames = ((Boolean)versionChanged.invoke(document, null)).booleanValue();
                }
            } catch (Exception e) {
                //no way to test the version...
                //ignore the exception
            }
            while (node != null) {
              verify(node, verifyNames, false);
              // Move down to first child
              next = node.getFirstChild();
              // No child nodes, so walk tree
              while (next == null) {
                // Move to sibling if possible.
                next = node.getNextSibling();
                if (next == null){
                    node = node.getParentNode();
                    if (root == node){
                        next = null;
                        break;                   
                    }
                    next = node.getNextSibling();
                }
              }
              node = next;
          }
 

        }
    }
    
    
    private void verify (Node node, boolean verifyNames, boolean xml11Version){

        int type = node.getNodeType();
        boolean wellformed;
        switch (type) { 
            case Node.DOCUMENT_NODE:{
                break;
            }
            case Node.DOCUMENT_TYPE_NODE:{
                break;
            }
            case Node.ELEMENT_NODE:{               
                if (verifyNames){
                    if((features & NAMESPACES) != 0){
                        wellformed = CoreDocumentImpl.isValidQName(node.getPrefix() , node.getLocalName(), xml11Version) ;
                    }
                    else{
                        wellformed = CoreDocumentImpl.isXMLName(node.getNodeName() , xml11Version);
                    }
                    if (!wellformed){
                            if (!wellformed){
                                if (fErrorHandler != null) {
                                    String msg = DOMMessageFormatter.formatMessage(
                                        DOMMessageFormatter.DOM_DOMAIN, 
                                        "wf-invalid-character-in-node-name", 
                                        new Object[]{"Element", node.getNodeName()});
                                        DOMNormalizer.reportDOMError(fErrorHandler, fError, fLocator, msg, DOMError.SEVERITY_FATAL_ERROR, null, 
                                        "wf-invalid-character-in-node-name");
                                }
                        
                            }                       
                    }
                }
                
                AttributeMap attributes = (node.hasAttributes()) ? (AttributeMap) node.getAttributes() : null; 
                if (attributes != null) {
                    for (int i = 0; i < attributes.getLength(); ++i) {
                        Attr attr = (Attr) attributes.item(i);
                        DOMNormalizer.isAttrValueWF( fErrorHandler, fError, fLocator, 
                                      attributes,(AttrImpl) attr, attr.getValue(), xml11Version);
                        if (verifyNames) {
                            wellformed = CoreDocumentImpl.isXMLName( node.getNodeName(), xml11Version);
                            if (!wellformed) {
                                    String msg =
                                        DOMMessageFormatter.formatMessage(
                                            DOMMessageFormatter.DOM_DOMAIN,
                                            "wf-invalid-character-in-node-name",
                                            new Object[] { "Attr", node.getNodeName()});
                                    DOMNormalizer.reportDOMError( fErrorHandler, fError, fLocator, msg, DOMError.SEVERITY_FATAL_ERROR, null,
                                        "wf-invalid-character-in-node-name");
                            }
                        }
                    }

                }
                
                break;
            }
        
        case Node.COMMENT_NODE: {
            // only verify well-formness if comments included in the tree
            if ((features & COMMENTS) != 0)
                DOMNormalizer.isCommentWF(fErrorHandler, fError, fLocator, ((Comment)node).getData(), xml11Version);
            break;
        }
        case Node.ENTITY_REFERENCE_NODE: {
            // only if entity is preserved in the tree
            if (verifyNames && (features & ENTITIES) != 0){
                CoreDocumentImpl.isXMLName(node.getNodeName() , xml11Version);
            }
            break;
            
        }
        case Node.CDATA_SECTION_NODE: {
            // verify content
            DOMNormalizer.isXMLCharWF(fErrorHandler, fError, fLocator, node.getNodeValue(), xml11Version);
            // the ]]> string will be checked during serialization
            break;
        }
        case Node.TEXT_NODE:{
            DOMNormalizer.isXMLCharWF(fErrorHandler, fError, fLocator, node.getNodeValue(), xml11Version);
            break;
        }
        case Node.PROCESSING_INSTRUCTION_NODE:{
            ProcessingInstruction pinode = (ProcessingInstruction)node ;
            String target = pinode.getTarget();
            if (verifyNames) {
                if (xml11Version) {
                    wellformed = XML11Char.isXML11ValidName(target);
                } else {
                    wellformed = XMLChar.isValidName(target);
                }

                if (!wellformed) {
                    String msg =
                        DOMMessageFormatter.formatMessage(
                            DOMMessageFormatter.DOM_DOMAIN,
                            "wf-invalid-character-in-node-name",
                            new Object[] { "Element", node.getNodeName()});
                    DOMNormalizer.reportDOMError(
                        fErrorHandler,
                        fError,
                        fLocator,
                        msg,
                        DOMError.SEVERITY_FATAL_ERROR,
                        null,
                        "wf-invalid-character-in-node-name");
                }
            }              
            DOMNormalizer.isXMLCharWF(fErrorHandler, fError, fLocator, pinode.getData(), xml11Version);
            break;
        }        
        }
               
    }

}//DOMSerializerImpl




