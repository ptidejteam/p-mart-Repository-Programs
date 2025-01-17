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
 *     the documentation and/or other materials provided with the
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
 * originally based on software copyright (c) 1999, Lotus
 * Development Corporation., http://www.lotus.com.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */
package org.apache.xalan.xpath.res;
import org.apache.xalan.xpath.xml.XSLResourceBundle;
import java.util.*;
import java.text.DecimalFormat;

/**
 * <meta name="usage" content="advanced"/>
 * Set up error messages.
 * We build a two dimensional array of message keys and 
 * message strings. In order to add a new message here,
 * you need to first update the count of messages(MAX_CODE)or 
 * the count of warnings(MAX_WARNING). The array will be 
 * automatically filled in with the keys, but you need to 
 * fill in the actual message string. Follow the instructions
 * below.   
 */
public class XPATHErrorResources extends XSLResourceBundle 
{

  public static final String ERROR_SUFFIX = "ER";  
  public static final String WARNING_SUFFIX = "WR";

  public static final int MAX_CODE = 68;                // this is needed to keep track of the number of messages          
  public static final int MAX_WARNING = 10;             // this is needed to keep track of the number of warnings
  public static final int MAX_OTHERS = 20;
  public static final int MAX_MESSAGES = MAX_CODE + MAX_WARNING +1;

  static final Object[][] contents = new Object[MAX_MESSAGES + MAX_OTHERS +1][2];

  /* 
  * Now fill in the message keys.
  * This does not need to be updated. If MAX_CODE and MAX_WARNING
  * are correct, the keys will get filled in automatically with
  * the value ERxxxx (WRxxxx for warnings) where xxxx is a 
  * formatted number corresponding to the error code (i.e. ER0001).
  */ 
  static 
  {
    for(int i = 0; i < MAX_CODE+1; i++)
    {
      contents[i][0] = getMKey(i);
    }
    for(int i = 1; i < MAX_WARNING+1; i++)
    {
      contents[i+ MAX_CODE][0] = getWKey(i);
    }
  }

  /*
  * Now fill in the message text.
  * First create an int for the message code. Make sure you 
  * update MAX_CODE for error messages and MAX_WARNING for warnings
  * Then fill in the message text for that message code in the
  * array. Use the new error code as the index into the array.
  */ 

  // Error messages...

  public static final int ERROR0000 = 0;
  static 
  {
    contents[ERROR0000][1] 
    = "{0}";
  }

  public static final int ER_CURRENT_NOT_ALLOWED_IN_MATCH = 1;
  static 
  {
    contents[ER_CURRENT_NOT_ALLOWED_IN_MATCH][1] 
    = "The current() function is not allowed in a match pattern!";
  }

  public static final int ER_CURRENT_TAKES_NO_ARGS = 2;
  static 
  {
    contents[ER_CURRENT_TAKES_NO_ARGS][1] 
    = "The current() function does not accept arguments!";
  }

  public static final int ER_DOCUMENT_REPLACED = 3;
  static 
  {
    contents[ER_DOCUMENT_REPLACED][1] 
    = "document() function implementation has been replaced by org.apache.xalan.xslt.FuncDocument!";
  }

  public static final int ER_CONTEXT_HAS_NO_OWNERDOC = 4;
  static 
  {
    contents[ER_CONTEXT_HAS_NO_OWNERDOC][1] 
    = "context does not have an owner document!";
  }

  public static final int ER_LOCALNAME_HAS_TOO_MANY_ARGS  = 5;
  static 
  {
    contents[ER_LOCALNAME_HAS_TOO_MANY_ARGS ][1] 
    = "local-name() has too many arguments.";
  }

  public static final int ER_NAMESPACEURI_HAS_TOO_MANY_ARGS  = 6;
  static 
  {
    contents[ER_NAMESPACEURI_HAS_TOO_MANY_ARGS ][1] 
    = "namespace-uri() has too many arguments.";
  }

  public static final int ER_NORMALIZESPACE_HAS_TOO_MANY_ARGS  = 7;
  static 
  {
    contents[ER_NORMALIZESPACE_HAS_TOO_MANY_ARGS ][1] 
    = "normalize-space() has too many arguments.";
  }

  public static final int ER_NUMBER_HAS_TOO_MANY_ARGS  = 8;
  static 
  {
    contents[ER_NUMBER_HAS_TOO_MANY_ARGS ][1] 
    = "number() has too many arguments.";
  }

  public static final int ER_NAME_HAS_TOO_MANY_ARGS  = 9;
  static 
  {
    contents[ER_NAME_HAS_TOO_MANY_ARGS ][1] 
    = "name() has too many arguments.";
  }

  public static final int ER_STRING_HAS_TOO_MANY_ARGS  = 10;
  static 
  {
    contents[ER_STRING_HAS_TOO_MANY_ARGS ][1] 
    = "string() has too many arguments.";
  }

  public static final int ER_STRINGLENGTH_HAS_TOO_MANY_ARGS  = 11;
  static 
  {
    contents[ER_STRINGLENGTH_HAS_TOO_MANY_ARGS ][1] 
    = "string-length() has too many arguments.";
  }

  public static final int ER_TRANSLATE_TAKES_3_ARGS  = 12;
  static 
  {
    contents[ER_TRANSLATE_TAKES_3_ARGS ][1] 
    = "The translate() function takes three arguments!";
  }

  public static final int ER_UNPARSEDENTITYURI_TAKES_1_ARG  = 13;
  static 
  {
    contents[ER_UNPARSEDENTITYURI_TAKES_1_ARG ][1] 
    = "The unparsed-entity-uri function should take one argument!";
  }

  public static final int ER_NAMESPACEAXIS_NOT_IMPLEMENTED  = 14;
  static 
  {
    contents[ER_NAMESPACEAXIS_NOT_IMPLEMENTED ][1] 
    = "namespace axis not implemented yet!";
  }

  public static final int ER_UNKNOWN_AXIS  = 15;
  static 
  {
    contents[ER_UNKNOWN_AXIS ][1] 
    = "unknown axis: {0}";
  }

  public static final int ER_UNKNOWN_MATCH_OPERATION  = 16;
  static 
  {
    contents[ER_UNKNOWN_MATCH_OPERATION ][1] 
    = "unknown match operation!";
  }

  public static final int ER_INCORRECT_ARG_LENGTH  = 17;
  static 
  {
    contents[ER_INCORRECT_ARG_LENGTH ][1] 
    = "Arg length of processing-instruction() node test is incorrect!";
  }

  public static final int ER_CANT_CONVERT_TO_NUMBER  = 18;
  static 
  {
    contents[ER_CANT_CONVERT_TO_NUMBER ][1] 
    = "Can not convert {0} to a number";
  }

  public static final int ER_CANT_CONVERT_TO_NODELIST  = 19;
  static 
  {
    contents[ER_CANT_CONVERT_TO_NODELIST ][1] 
    = "Can not convert {0} to a NodeList!";
  }

  public static final int ER_CANT_CONVERT_TO_MUTABLENODELIST  = 20;
  static 
  {
    contents[ER_CANT_CONVERT_TO_MUTABLENODELIST ][1] 
    = "Can not convert {0} to a MutableNodeList!";
  }

  public static final int ER_CANT_CONVERT_TO_TYPE  = 21;
  static 
  {
    contents[ER_CANT_CONVERT_TO_TYPE ][1] 
    = "Can not convert {0} to a type#{1}";
  }

  public static final int ER_EXPECTED_MATCH_PATTERN  = 22;
  static 
  {
    contents[ER_EXPECTED_MATCH_PATTERN ][1] 
    = "Expected match pattern in getMatchScore!";
  }

  public static final int ER_COULDNOT_GET_VAR_NAMED  = 23;
  static 
  {
    contents[ER_COULDNOT_GET_VAR_NAMED ][1] 
    = "Could not get variable named {0}";
  }

  public static final int ER_UNKNOWN_OPCODE  = 24;
  static 
  {
    contents[ER_UNKNOWN_OPCODE ][1] 
    = "ERROR! Unknown op code: {0}";
  }

  public static final int ER_EXTRA_ILLEGAL_TOKENS  = 25;
  static 
  {
    contents[ER_EXTRA_ILLEGAL_TOKENS ][1] 
    = "Extra illegal tokens: {0}";
  }

  public static final int ER_EXPECTED_DOUBLE_QUOTE  = 26;
  static 
  {
    contents[ER_EXPECTED_DOUBLE_QUOTE ][1] 
    = "misquoted literal... expected double quote!";
  }

  public static final int ER_EXPECTED_SINGLE_QUOTE  = 27;
  static 
  {
    contents[ER_EXPECTED_SINGLE_QUOTE ][1] 
    = "misquoted literal... expected single quote!";
  }

  public static final int ER_EMPTY_EXPRESSION  = 28;
  static 
  {
    contents[ER_EMPTY_EXPRESSION ][1] 
    = "Empty expression!";
  }

  public static final int ER_EXPECTED_BUT_FOUND  = 29;
  static 
  {
    contents[ER_EXPECTED_BUT_FOUND ][1] 
    = "Expected {0}, but found: {1}";
  }

  public static final int ER_INCORRECT_PROGRAMMER_ASSERTION  = 30;
  static 
  {
    contents[ER_INCORRECT_PROGRAMMER_ASSERTION ][1] 
    = "Programmer assertion is incorrect! - {0}";
  }

  public static final int ER_BOOLEAN_ARG_NO_LONGER_OPTIONAL  = 31;
  static 
  {
    contents[ER_BOOLEAN_ARG_NO_LONGER_OPTIONAL ][1] 
    = "boolean(...) argument is no longer optional with 19990709 XPath draft.";
  }

  public static final int ER_FOUND_COMMA_BUT_NO_PRECEDING_ARG  = 32;
  static 
  {
    contents[ER_FOUND_COMMA_BUT_NO_PRECEDING_ARG ][1] 
    = "Found ',' but no preceding argument!";
  }

  public static final int ER_FOUND_COMMA_BUT_NO_FOLLOWING_ARG  = 33;
  static 
  {
    contents[ER_FOUND_COMMA_BUT_NO_FOLLOWING_ARG ][1] 
    = "Found ',' but no following argument!";
  }

  public static final int ER_PREDICATE_ILLEGAL_SYNTAX  = 34;
  static 
  {
    contents[ER_PREDICATE_ILLEGAL_SYNTAX ][1] 
    = "'..[predicate]' or '.[predicate]' is illegal syntax.  Use 'self::node()[predicate]' instead.";
  }

  public static final int ER_ILLEGAL_AXIS_NAME  = 35;
  static 
  {
    contents[ER_ILLEGAL_AXIS_NAME ][1] 
    = "illegal axis name: {0}";
  }

  public static final int ER_UNKNOWN_NODETYPE  = 36;
  static 
  {
    contents[ER_UNKNOWN_NODETYPE ][1] 
    = "Unknown nodetype: {0}";
  }

  public static final int ER_PATTERN_LITERAL_NEEDS_BE_QUOTED  = 37;
  static 
  {
    contents[ER_PATTERN_LITERAL_NEEDS_BE_QUOTED ][1] 
    = "Pattern literal ({0}) needs to be quoted!";
  }

  public static final int ER_COULDNOT_BE_FORMATTED_TO_NUMBER  = 38;
  static 
  {
    contents[ER_COULDNOT_BE_FORMATTED_TO_NUMBER ][1] 
    = "{0} could not be formatted to a number!";
  }

  public static final int ER_COULDNOT_CREATE_XMLPROCESSORLIAISON  = 39;
  static 
  {
    contents[ER_COULDNOT_CREATE_XMLPROCESSORLIAISON ][1] 
    = "Could not create XML Processor Liaison: {0}";
  }

  public static final int ER_DIDNOT_FIND_XPATH_SELECT_EXP  = 40;
  static 
  {
    contents[ER_DIDNOT_FIND_XPATH_SELECT_EXP ][1] 
    = "Error! Did not find xpath select expression (-select).";
  }

  public static final int ER_COULDNOT_FIND_ENDOP_AFTER_OPLOCATIONPATH  = 41;
  static 
  {
    contents[ER_COULDNOT_FIND_ENDOP_AFTER_OPLOCATIONPATH ][1] 
    = "ERROR! Could not find ENDOP after OP_LOCATIONPATH";
  }

  public static final int ER_ERROR_OCCURED  = 42;
  static 
  {
    contents[ER_ERROR_OCCURED ][1] 
    = "Error occured!";
  }

  public static final int ER_ILLEGAL_VARIABLE_REFERENCE  = 43;
  static 
  {
    contents[ER_ILLEGAL_VARIABLE_REFERENCE][1] 
    = "VariableReference given for variable out of context or without definition!  Name = {0}";
  }

  public static final int ER_AXES_NOT_ALLOWED  = 44;
  static 
  {
    contents[ER_AXES_NOT_ALLOWED][1] 
    = "Only child:: and attribute:: axes are allowed in match patterns!  Offending axes = {0}";
  }

  public static final int ER_KEY_HAS_TOO_MANY_ARGS  = 45;
  static 
  {
    contents[ER_KEY_HAS_TOO_MANY_ARGS ][1] 
    = "key() has an incorrect number of arguments.";
  }

  public static final int ER_COUNT_TAKES_1_ARG  = 46;
  static 
  {
    contents[ER_COUNT_TAKES_1_ARG ][1] 
    = "The count function should take one argument!";
  }

  public static final int ER_COULDNOT_FIND_FUNCTION  = 47;
  static 
  {
    contents[ER_COULDNOT_FIND_FUNCTION ][1] 
    = "Could not find function: {0}";
  }
  
  public static final int ER_UNSUPPORTED_ENCODING  = 48;
  static 
  {
    contents[ER_UNSUPPORTED_ENCODING ][1] 
    = "Unsupported encoding: {0}";
  }
  
  public static final int ER_PROBLEM_IN_DTM_NEXTSIBLING  = 49;
  static 
  {
    contents[ER_PROBLEM_IN_DTM_NEXTSIBLING ][1] 
    = "Problem occured in DTM in getNextSibling... trying to recover";
  }
  
  public static final int ER_CANNOT_WRITE_TO_EMPTYNODELISTIMPL  = 50;
  static 
  {
    contents[ER_CANNOT_WRITE_TO_EMPTYNODELISTIMPL ][1] 
    = "Programmer error: EmptyNodeListImpl can not be written to.";
  }
  
  public static final int ER_SETDOMFACTORY_NOT_SUPPORTED  = 51;
  static 
  {
    contents[ER_SETDOMFACTORY_NOT_SUPPORTED ][1] 
    = "setDOMFactory is not supported by XPathSupportDefault!";
  }
  
  public static final int ER_PREFIX_MUST_RESOLVE  = 52;
  static 
  {
    contents[ER_PREFIX_MUST_RESOLVE ][1] 
      = "Prefix must resolve to a namespace: {0}";
  }
  
  public static final int ER_PARSE_NOT_SUPPORTED  = 53;
  static 
  {
    contents[ER_PARSE_NOT_SUPPORTED ][1] 
      = "parse (InputSource source) not supported in XMLParserLiaisonDefault! Can not open {0}";
  }
  
  public static final int ER_CREATEDOCUMENT_NOT_SUPPORTED  = 54;
  static 
  {
    contents[ER_CREATEDOCUMENT_NOT_SUPPORTED ][1] 
      = "createDocument() not supported in XMLParserLiaisonDefault!";
  }
  
  public static final int ER_CHILD_HAS_NO_OWNER_DOCUMENT  = 55;
  static 
  {
    contents[ER_CHILD_HAS_NO_OWNER_DOCUMENT ][1] 
      = "Attribute child does not have an owner document!";
  }

  public static final int ER_CHILD_HAS_NO_OWNER_DOCUMENT_ELEMENT  = 56;
  static 
  {
    contents[ER_CHILD_HAS_NO_OWNER_DOCUMENT_ELEMENT ][1] 
      = "Attribute child does not have an owner document element!";
  }
  
  public static final int ER_SAX_API_NOT_HANDLED  = 57;
  static 
  {
    contents[ER_SAX_API_NOT_HANDLED ][1] 
      = "SAX API characters(char ch[]... not handled by the DTM!";
  }
  
  public static final int ER_IGNORABLE_WHITESPACE_NOT_HANDLED  = 58;
  static 
  {
    contents[ER_IGNORABLE_WHITESPACE_NOT_HANDLED ][1] 
      = "ignorableWhitespace(char ch[]... not handled by the DTM!";
  }
  
  public static final int ER_DTM_CANNOT_HANDLE_NODES  = 59;
  static 
  {
    contents[ER_DTM_CANNOT_HANDLE_NODES ][1] 
      = "DTMLiaison can not handle nodes of type {0}";
  }
  
  public static final int ER_XERCES_CANNOT_HANDLE_NODES  = 60;
  static 
  {
    contents[ER_XERCES_CANNOT_HANDLE_NODES ][1] 
      = "XercesLiaison can not handle nodes of type {0}";
  }
  
  public static final int ER_XERCES_PARSE_ERROR_DETAILS  = 61;
  static 
  {
    contents[ER_XERCES_PARSE_ERROR_DETAILS ][1] 
      = "XercesLiaison.parse error: SystemID - {0} line - {1}";
  }
  
  public static final int ER_XERCES_PARSE_ERROR  = 62;
  static 
  {
    contents[ER_XERCES_PARSE_ERROR ][1] 
      = "XercesLiaison.parse error";
  }
  
  public static final int ER_CANT_OUTPUT_TEXT_BEFORE_DOC  = 63;
  static 
  {
    contents[ER_CANT_OUTPUT_TEXT_BEFORE_DOC ][1] 
      = "Warning: can't output text before document element!  Ignoring...";
  }
  
  public static final int ER_CANT_HAVE_MORE_THAN_ONE_ROOT  = 64;
  static 
  {
    contents[ER_CANT_HAVE_MORE_THAN_ONE_ROOT ][1] 
      = "Can't have more than one root on a DOM!";
  }
  
  public static final int ER_INVALID_UTF16_SURROGATE  = 65;
  static 
  {
    contents[ER_INVALID_UTF16_SURROGATE ][1] 
      = "Invalid UTF-16 surrogate detected: {0} ?";
  }
  
  public static final int ER_OIERROR  = 66;
  static 
  {
    contents[ER_OIERROR ][1] 
      = "IO error";
  }
  
  public static final int ER_CANNOT_CREATE_URL  = 67;
  static 
  {
    contents[ER_CANNOT_CREATE_URL ][1] 
      = "Cannot create url for: {0}";
  }
  
  public static final int ER_XPATH_READOBJECT  = 68;
  static 
  {
    contents[ER_XPATH_READOBJECT ][1] 
      = "In XPath.readObject: {0}";
  }

  // Warnings...

  public static final int WG_LOCALE_NAME_NOT_HANDLED  = 1;
  static 
  {
    contents[WG_LOCALE_NAME_NOT_HANDLED  + MAX_CODE][1] 
    = "locale name in the format-number function not yet handled!";
  }

  public static final int WG_PROPERTY_NOT_SUPPORTED  = 2;
  static 
  {
    contents[WG_PROPERTY_NOT_SUPPORTED  + MAX_CODE][1] 
    = "XSL Property not supported: {0}";
  }

  public static final int WG_DONT_DO_ANYTHING_WITH_NS  = 3;
  static 
  {
    contents[WG_DONT_DO_ANYTHING_WITH_NS  + MAX_CODE][1] 
    = "Do not currently do anything with namespace {0} in property: {1}";
  }

  public static final int WG_SECURITY_EXCEPTION  = 4;
  static 
  {
    contents[WG_SECURITY_EXCEPTION  + MAX_CODE][1] 
    = "SecurityException when trying to access XSL system property: {0}";
  }

  public static final int WG_QUO_NO_LONGER_DEFINED  = 5;
  static 
  {
    contents[WG_QUO_NO_LONGER_DEFINED  + MAX_CODE][1] 
    = "Old syntax: quo(...) is no longer defined in XPath.";
  }


  public static final int WG_NEED_DERIVED_OBJECT_TO_IMPLEMENT_NODETEST  = 6;
  static 
  {
    contents[WG_NEED_DERIVED_OBJECT_TO_IMPLEMENT_NODETEST  + MAX_CODE][1] 
    = "XPath needs a derived object to implement nodeTest!";
  }

  public static final int WG_FUNCTION_TOKEN_NOT_FOUND  = 7;
  static 
  {
    contents[WG_FUNCTION_TOKEN_NOT_FOUND  + MAX_CODE][1] 
    = "function token not found.";
  }

  public static final int WG_COULDNOT_FIND_FUNCTION  = 8;
  static 
  {
    contents[WG_COULDNOT_FIND_FUNCTION  + MAX_CODE][1] 
    = "Could not find function: {0}";
  }

  public static final int WG_CANNOT_MAKE_URL_FROM  = 9;
  static 
  {
    contents[WG_CANNOT_MAKE_URL_FROM  + MAX_CODE][1] 
    = "Can not make URL from: {0}";
  }

  public static final int WG_EXPAND_ENTITIES_NOT_SUPPORTED  = 10;
  static 
  {
    contents[WG_EXPAND_ENTITIES_NOT_SUPPORTED  + MAX_CODE][1] 
    = "-E option not supported for DTM parser";
  }

  // Other miscellaneous text used inside the code...

  static  
  {
    
    contents[MAX_MESSAGES][0] = "ui_language";
    contents[MAX_MESSAGES][1] = "en";
    
    contents[MAX_MESSAGES + 1][0] = "help_language";
    contents[MAX_MESSAGES + 1][1] = "en";
    
    contents[MAX_MESSAGES + 2][0] = "language";
    contents[MAX_MESSAGES + 2][1] = "en";
    
    contents[MAX_MESSAGES + 3][0] = "BAD_CODE";
    contents[MAX_MESSAGES + 3][1] = "Parameter to createMessage was out of bounds";
    
    contents[MAX_MESSAGES + 4][0] = "FORMAT_FAILED";
    contents[MAX_MESSAGES + 4][1] = "Exception thrown during messageFormat call";
    
    contents[MAX_MESSAGES + 5][0] = "version";
    contents[MAX_MESSAGES + 5][1] = ">>>>>>> Xalan Version ";
    
    contents[MAX_MESSAGES + 6][0] = "version2";
    contents[MAX_MESSAGES + 6][1] = "<<<<<<<";
    
    contents[MAX_MESSAGES + 7][0] = "yes";
    contents[MAX_MESSAGES + 7][1] = "yes";
    
    contents[MAX_MESSAGES + 8][0] = "line";
    contents[MAX_MESSAGES + 8][1] = "Line #";
    
    contents[MAX_MESSAGES + 9][0] = "column";
    contents[MAX_MESSAGES + 9][1] = "Column #";
    
    contents[MAX_MESSAGES + 10][0] = "xsldone";
    contents[MAX_MESSAGES + 10][1] = "XSLProcessor: done";
    
    contents[MAX_MESSAGES + 11][0] = "xpath_option";
    contents[MAX_MESSAGES + 11][1] = "xpath options: ";
    
    contents[MAX_MESSAGES + 12][0] = "optionIN";
    contents[MAX_MESSAGES + 12][1] = "   [-in inputXMLURL]";
    
    contents[MAX_MESSAGES + 13][0] = "optionSelect";
    contents[MAX_MESSAGES + 13][1] = "   [-select xpath expression]";
    
    contents[MAX_MESSAGES + 14][0] = "optionMatch";
    contents[MAX_MESSAGES + 14][1] = "   [-match match pattern (for match diagnostics)]";
    
    contents[MAX_MESSAGES + 15][0] = "optionAnyExpr";
    contents[MAX_MESSAGES + 15][1] = "Or just an xpath expression will do a diagnostic dump";
    

    contents[MAX_MESSAGES + 16][0] = "noParsermsg1";
    contents[MAX_MESSAGES + 16][1] = "XSL Process was not successful.";
    
    contents[MAX_MESSAGES + 17][0] = "noParsermsg2";
    contents[MAX_MESSAGES + 17][1] = "** Could not find parser **";

    contents[MAX_MESSAGES + 18][0] = "noParsermsg3";
    contents[MAX_MESSAGES + 18][1] = "Please check your classpath.";

    contents[MAX_MESSAGES + 19][0] = "noParsermsg4"; 
    contents[MAX_MESSAGES + 19][1] = "If you don't have IBM's XML Parser for Java, you can download it from";
    
    contents[MAX_MESSAGES + 20][0] = "noParsermsg5";
    contents[MAX_MESSAGES + 20][1] = "IBM's AlphaWorks: http://www.alphaworks.ibm.com/formula/xml";
  }

  // ================= INFRASTRUCTURE ======================

  public static final String BAD_CODE = "BAD_CODE";
  public static final String FORMAT_FAILED = "FORMAT_FAILED";

  public static final String ERROR_RESOURCES = "org.apache.xalan.xpath.res.XPATHErrorResources";
  
  public static final String ERROR_STRING = "#error";
  public static final String ERROR_HEADER = "Error: ";
  public static final String WARNING_HEADER = "Warning: ";
  public static final String XSL_HEADER = "XSL ";
  public static final String XML_HEADER = "XML ";
  public static final String QUERY_HEADER = "PATTERN ";

  public Object[][] getContents()
  {
    return contents;
  }

  /**
   * Return a named ResourceBundle for a particular locale.  This method mimics the behavior
   * of ResourceBundle.getBundle(). 
   *
   * @param res the name of the resource to load. 
   * @param locale the locale to prefer when searching for the bundle
   * @return the ResourceBundle
   * @throws MissingResourceException  
   */
  public static final XPATHErrorResources loadResourceBundle (String className) 
    throws MissingResourceException
  {
    Locale locale = Locale.getDefault();
    String suffix = getResourceSuffix(locale);  
    try
    {		
      // first try with the given locale
      return (XPATHErrorResources)ResourceBundle.getBundle (className + suffix, locale);
    }
    catch (MissingResourceException e)
    {
      try                                                  // try to fall back to en_US if we can't load
      {
        // Since we can't find the localized property file,
        // fall back to en_US.
        return (XPATHErrorResources)ResourceBundle.getBundle (className, new Locale ("en", "US"));
      }
      catch (MissingResourceException e2)
      {
        // Now we are really in trouble.
        // very bad, definitely very bad...not going to get very far
        throw new MissingResourceException ("Could not load any resource bundles.", className, "");
      }
    }
  }
  
  /**
   * Return the resource file suffic for the indicated locale
   * For most locales, this will be based the language code.  However
   * for Chinese, we do distinguish between Taiwan and PRC
   *
   * @param locale the locale
   * @return an String suffix which canbe appended to a resource name
   */        
  private static final String getResourceSuffix(Locale locale)
  {
    String suffix = "_" + locale.getLanguage();
    
    String country = locale.getCountry();        
    
    if (country.equals("TW"))
      suffix += "_" + country;

    return suffix;
  }
  
  /**
   * Get the error string associated with the error code
   */ 
  public String getMessageKey(int errorCode)
  {
    if (errorCode > MAX_CODE)
      return null;
    else
    {
      DecimalFormat df = new DecimalFormat("0000");
      return ERROR_SUFFIX + df.format(errorCode);	
    }  
  }
  
  /**
   * Get the error string associated with the error code
   */ 
  public String getWarningKey(int errorCode)
  {
    if (errorCode > MAX_WARNING)
      return null;
    else 
    {
      DecimalFormat df = new DecimalFormat("0000");
      return WARNING_SUFFIX + df.format(errorCode);	
    }
  }
  
  /**
   * Get the error string associated with the error code
   */ 
  public static String getMKey(int errorCode)
  {
    if (errorCode > MAX_CODE)
      return null;
    else
    {
      DecimalFormat df = new DecimalFormat("0000");
      return ERROR_SUFFIX + df.format(errorCode);	
    }  
  }
  
  /**
   * Get the error string associated with the error code
   */ 
  public static String getWKey(int errorCode)
  {
    if (errorCode > MAX_WARNING)
      return null;
    else 
    {
      DecimalFormat df = new DecimalFormat("0000");
      return WARNING_SUFFIX + df.format(errorCode);	
    }
  }
}
