/*
 * @(#)$Id: ErrorMessages.java,v 1.1 2006/03/01 20:54:09 vauchers Exp $
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

package org.apache.xalan.xsltc.compiler.util;

import java.util.Vector;
import java.util.Enumeration;
import java.util.ResourceBundle;

public class ErrorMessages extends ResourceBundle {

    // These message should be read from a locale-specific resource bundle
    private static final String errorMessages[] = { 
	// MULTIPLE_STYLESHEET_ERR
	"More than one stylesheet defined in the same file.",
	// TEMPLATE_REDEF_ERR	
	"Template ''{0}'' already defined in this stylesheet.",
	// TEMPLATE_UNDEF_ERR
	"Template ''{0}'' not defined in this stylesheet.",
	// VARIABLE_REDEF_ERR	
	"Variable ''{0}'' is multiply defined in the same scope.",
	// VARIABLE_UNDEF_ERR
	"Variable or parameter ''{0}'' is undefined.",
	// CLASS_NOT_FOUND_ERR
	"Cannot find class ''{0}''.",
	// METHOD_NOT_FOUND_ERR
	"Cannot find external method ''{0}'' (must be static and public).",
	// ARGUMENT_CONVERSION_ERR
	"Cannot convert argument/return type in call to method ''{0}''",
	// FILE_NOT_FOUND_ERR
	"File or URI ''{0}'' not found.",
	// INVALID_URI_ERR
	"Invalid URI ''{0}''.",
	// FILE_ACCESS_ERR
	"Cannot open file or URI ''{0}''.",
	// MISSING_ROOT_ERR
	"<xsl:stylesheet> or <xsl:transform> element expected.",
	// NAMESPACE_UNDEF_ERR
	"Namespace prefix ''{0}'' is undeclared.",
	// FUNCTION_RESOLVE_ERR
	"Unable to resolve call to function ''{0}''.",
	// NEED_LITERAL_ERR
	"Argument to ''{0}'' must be a literal string.",
	// XPATH_PARSER_ERR
	"Error parsing XPath expression ''{0}''.",
	// REQUIRED_ATTR_ERR
	"Required attribute ''{0}'' is missing.",
	// ILLEGAL_CHAR_ERR
	"Illegal character ''{0}'' in XPath expression.",
	// ILLEGAL_PI_ERR
	"Illegal name ''{0}'' for processing instruction.",
	// STRAY_ATTRIBUTE_ERR
	"Attribute ''{0}'' outside of element.",
	// ILLEGAL_ATTRIBUTE_ERR
	"Illegal attribute ''{0}''.",
	// CIRCULAR_INCLUDE_ERR
	"Circular import/include. Stylesheet ''{0}'' already loaded.",
	// RESULT_TREE_SORT_ERR
	"Result-tree fragments cannot be sorted (<xsl:sort> elements are "+
	"ignored). You must sort the nodes when creating the result tree.",
	// SYMBOLS_REDEF_ERR
	"Decimal formatting ''{0}'' is already defined.",
	// XSL_VERSION_ERR
	"XSL version ''{0}'' is not supported by XSLTC.",
	// CIRCULAR_VARIABLE_ERR
	"Circular variable/parameter reference in ''{0}''.",
	// ILLEGAL_BINARY_OP_ERR
	"Unknown operator for binary expression.",
	// ILLEGAL_ARG_ERR
	"Illegal argument(s) for function call.",
	// DOCUMENT_ARG_ERR
	"Second argument to document() function must be a node-set.",
	// MISSING_WHEN_ERR
	"At least one <xsl:when> element required in <xsl:choose>.",
	// MULTIPLE_OTHERWISE_ERR
	"Only one <xsl:otherwise> element allowed in <xsl:choose>.",
	// STRAY_OTHERWISE_ERR
	"<xsl:otherwise> can only be used within <xsl:choose>.",
	// STRAY_WHEN_ERR
	"<xsl:when> can only be used within <xsl:choose>.",
	// WHEN_ELEMENT_ERR	
	"Only <xsl:when> and <xsl:otherwise> elements allowed in <xsl:choose>.",
	// UNNAMED_ATTRIBSET_ERR
	"<xsl:attribute-set> is missing the 'name' attribute.",
	// ILLEGAL_CHILD_ERR
	"Illegal child element.",
	// ILLEGAL_ELEM_NAME_ERR
	"You cannot call an element ''{0}''",
	// ILLEGAL_ATTR_NAME_ERR
	"You cannot call an attribute ''{0}''",
	// ILLEGAL_TEXT_NODE_ERR
	"Text data outside of top-level <xsl:stylesheet> element.",
	// SAX_PARSER_CONFIG_ERR
	"JAXP parser not configured correctly",
	// INTERNAL_ERR
	"Unrecoverable XSLTC-internal error: ''{0}''",
	// UNSUPPORTED_XSL_ERR
	"Unsupported XSL element ''{0}''.",
	// UNSUPPORTED_EXT_ERR
	"Unrecognised XSLTC extension ''{0}''.",
	// MISSING_XSLT_URI_ERR
	"The input document is not a stylesheet "+
	"(the XSL namespace is not declared in the root element).",
	// MISSING_XSLT_TARGET_ERR
	"Could not find stylesheet target ''{0}''.",
	// NOT_IMPLEMENTED_ERR
	"Not implemented: ''{0}''.",
	// NOT_STYLESHEET_ERR
	"The input document does not contain an XSL stylesheet.",
	// ELEMENT_PARSE_ERR
	"Could not parse element ''{0}''",
	// KEY_USE_ATTR_ERR
	"The use-attribute of <key> must be node, node-set, string or number.",
	// OUTPUT_VERSION_ERR
	"Output XML document version should be 1.0",
	// ILLEGAL_RELAT_OP_ERR
	"Unknown operator for relational expression",
	// ATTRIBSET_UNDEF_ERR
	"Attempting to use non-existing attribute set ''{0}''.",
	// ATTR_VAL_TEMPLATE_ERR
	"Cannot parse attribute value template ''{0}''.",
	// UNKNOWN_SIG_TYPE_ERR
	"Unknown data-type in signature for class ''{0}''.",
	// DATA_CONVERSION_ERR
	"Cannot convert data-type ''{0}'' to ''{1}''.",

	// NO_TRANSLET_CLASS_ERR
	"This Templates does not contain a valid translet class definition.",
	// NO_MAIN_TRANSLET_ERR
	"This Templates does not contain a class with the name ''{0}''.",
	// TRANSLET_CLASS_ERR
	"Could not load the translet class ''{0}''.",
	// TRANSLET_OBJECT_ERR
	"Translet class loaded, but unable to create translet instance.",
	// ERROR_LISTENER_NULL_ERR
	"Attempting to set ErrorListener for ''{0}'' to null",
	// JAXP_UNKNOWN_SOURCE_ERR
	"Only StreamSource, SAXSource and DOMSOurce are supported by XSLTC",
	// JAXP_NO_SOURCE_ERR
	"Source object passed to ''{0}'' has no contents.",
	// JAXP_COMPILE_ERR
	"Could not compile stylesheet",
	// JAXP_INVALID_ATTR_ERR
	"TransformerFactory does not recognise attribute ''{0}''.",
	// JAXP_SET_RESULT_ERROR
	"setResult() must be called prior to startDocument().",
	// JAXP_NO_TRANSLET_ERR
	"The transformer has no encapsulated translet object.",
	// JAXP_NO_HANDLER_ERR
	"No defined output handler for transformation result.",
	// JAXP_NO_RESULT_ERR
	"Result object passed to ''{0}'' is invalid.",
	// JAXP_UNKNOWN_PROP_ERR
	"Attempting to access invalid Transformer property ''{0}''.",
	// SAX2DOM_ADAPTER_ERR
	"Could not crate SAX2DOM adapter: ''{0}''.",
	// XSLTC_SOURCE_ERR
	"XSLTCSource.build() called without systemId being set.",

	// COMPILE_STDIN_ERR
	"The -i option must be used with the -o option.",
	// COMPILE_USAGE_STR
	"Usage:\n" + 
	"   xsltc [-o <output>] [-d <directory>] [-j <jarfile>]\n"+
	"         [-p <package name>] [-x] [-s] [-u] <stylesheet>|-i\n\n"+
	"   Where <output> is the name to give the the generated translet.\n"+
	"         <stylesheet> is one or more stylesheet file names, or if\n"+
	"         the -u options is specified, one or more stylesheet URLs.\n"+
	"         <directory> is the output directory.\n"+
	"         <jarfile> is the name of a JAR-file to put all classes in.\n"+
	"         <package-name> is used to prefix all class names.\n\n"+
	"   Notes:\n"+
	"         The -i options forces the compiler to read from stdin\n"+
	"         The -o option is ignored if compiling multiple stylesheets\n"+
	"         The -x option switches on debug messages.\n"+
	"         The -s option disables calling System.exit.",
	// TRANSFORM_USAGE_STR
	"Usage: \n" +
	"   xslt  [-j <jarfile>] {-u <document_url> | <document>} <class>\n"+
	"         [<name1>=<value1> ...]\n\n" +
	"   Where <document> is the xml document to be transformed, or\n" +
	"         <document_url> is a url for the xml document,\n" +
	"         <class> is the translet class which is either in\n" +
	"         user's CLASSPATH or in the <jarfile> specified \n" +
	"         with the -j option.\n" +
	"   Notes:\n"+
	"         The -x option switches on debug messages.\n"+
	"         The -s option disables calling System.exit.",

	// STRAY_SORT_ERR
	"<xsl:sort> can only be used within <xsl:for-each> or <xsl:apply-templates>.",
	// UNSUPPORTED_ENCODING
	"Output encoding ''{0}'' is not supported on this JVM."
    };

    private static Vector _keys;

    static {
	_keys = new Vector();
	_keys.addElement(ErrorMsg.ERROR_MESSAGES_KEY);
	_keys.addElement(ErrorMsg.COMPILER_ERROR_KEY);
	_keys.addElement(ErrorMsg.COMPILER_WARNING_KEY);
	_keys.addElement(ErrorMsg.RUNTIME_ERROR_KEY);
    }

    public Enumeration getKeys() {
	return _keys.elements();
    }

    public Object handleGetObject(String key) {
	if (key == null) return null;
	if (key.equals(ErrorMsg.ERROR_MESSAGES_KEY))
	    return errorMessages;
 	else if (key.equals(ErrorMsg.COMPILER_ERROR_KEY))
	    return "Compiler error(s): ";
	else if (key.equals(ErrorMsg.COMPILER_WARNING_KEY))
	    return "Compiler warning(s): ";	    
 	else if (key.equals(ErrorMsg.RUNTIME_ERROR_KEY))
	    return "Translet error(s): ";
	return(null);
    }

}
