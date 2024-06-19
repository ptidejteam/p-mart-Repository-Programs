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
package org.apache.xalan.xpath;

import org.w3c.dom.*;
import java.util.*;
import java.net.URL;
import org.xml.sax.*;
import org.apache.xalan.xpath.xml.PrefixResolver;

/**
 * <meta name="usage" content="advanced"/>
 * Provides basic support for independent use of XPath.  This interface 
 * is used in order that there may be loose coupling between the 
 * XPath package and the support services, such as the variable store, 
 * parser services, DOM implementation, etc.  Most if all implementations
 * of this should derive from XPathSupportDefault (hence, it should 
 * probably be an abstract class instead of an interface).
 */
public interface XPathSupport extends XPathEnvSupport
{
  /**
   * Get the current context node list.
   */
  NodeList getContextNodeList();

  /**
   * <meta name="usage" content="internal"/>
   * Set the current context node list.
   * @param A nodelist that represents the current context 
   * list as defined by XPath.
   */
  void pushContextNodeList(NodeList nl);

  /**
   * <meta name="usage" content="internal"/>
   * Pop the current context node list.
   */
  void popContextNodeList();

  /**
   * <meta name="usage" content="experimental"/>
   * Get the current position in the context node list.  Used for 
   * depth-first searching.
   */
  int getContextNodePosition();

  /**
   * <meta name="usage" content="experimental"/>
   * Increment the current context node position.
   */
  void incrementContextNodePosition(Node node);

  /**
   * <meta name="usage" content="experimental"/>
   * Decrement the current context node position.
   */
  void decrementContextNodePosition();

  /**
   * <meta name="usage" content="experimental"/>
   * Push the current context node position.
   */
  void pushContextNodePosition();

  /**
   * <meta name="usage" content="experimental"/>
   * Pop the current context node position.
   */
  void popContextNodePosition();

  /**
   * Get the current context node.
   * @return The current context node as defined by the XPath recommendation.
   */
  Node getCurrentNode();

  /**
   * <meta name="usage" content="internal"/>
   * Set the current context node.
   * @param n The current context node as defined by the XPath recommendation.
   */
  void setCurrentNode(Node n);

  /**
   * <meta name="usage" content="experimental"/>
   * Push the current XPath selection,
   * needed for support of the last() function in depth-first 
   * execution.  If the last() function is called, the originating 
   * XPath will be executed to get a count.  This is ugly, and later 
   * some sort of system can be done where the XPath can finish evaluating 
   * from the current point.
   */
  void pushXPathContext(XPath xpath, XPathSupport execContext, Node contextNode,
                         PrefixResolver namespaceContext);

  /**
   * <meta name="usage" content="experimental"/>
   * Pop the current XPathContext.
   */
  void popXPathContext();

  /**
   * <meta name="usage" content="experimental"/>
   * Reexecute the last xpath context after the specified one.
   */
  XObject reExecuteXPathContext(XPath path, XPathSupport execContext, Node context)
    throws SAXException;

  /**
   * <meta name="usage" content="experimental"/>
   * Push a dummy XPathContext so we can tell that the top-level xpath isn't
   * in effect.
   */
  public void pushDummyXPathContext();

  /**
   * Get the current namespace context for the xpath.
   * @return An object that can resolve XPath prefixes to namespaces.
   */
  PrefixResolver getNamespaceContext();

  /**
   * Get the current namespace context for the xpath.
   * @param pr An object that can resolve XPath prefixes to namespaces.
   */
  void setNamespaceContext(PrefixResolver pr);

  /**
   * Given a namespace, get the corresponding prefix.
   * @param prefix A namespace prefix that is valid in the namespaceContext.
   * @param namespaceContext An element from which to evaluate the prefix resolution.
   * @return A namespace, or null if it can't be resolved.
   */
  String getNamespaceForPrefix(String prefix,
                               Element namespaceContext);

  /**
   * Returns the namespace of the given node.
   * @param n The node in question.
   * @return A namespace, or null if there is none.
   */
  String getNamespaceOfNode(Node n);

  /**
   * Returns the local name of the given node.
   * @param n The node in question.
   * @return The local name of the node, or null if the node doesn't have a name.
   */
  String getLocalNameOfNode(Node n);

  /**
   * This function has to be implemented,
   * because the DOM WG decided that attributes don't
   * have parents.
   * @param n The node in question.
   * @return The "owner" of the node.
   */
  Node getParentOfNode(Node node);

  /**
   * Tell if the node is ignorable whitespace.
   * This should be in the DOM.  Return false if the
   * parser doesn't handle this.
   * @deprecated
   */
  boolean isIgnorableWhitespace(Text node);

  /**
   * Take given a URL, try and parse XML.
   * the error condition is severe enough to halt processing.
   * @deprecated
   */
  Document parseXML(URL url,
                           DocumentHandler docHandler,
                           Document styleDoc)
    throws SAXException;

  /**
   * Take a user string and try and parse XML, and also return
   * the url.
   * @exception XSLProcessorException thrown if the active ProblemListener and XMLParserLiaison decide
   * the error condition is severe enough to halt processing.
   */
  URL getURLFromString(String urlString, String base)
    throws SAXException ;

  /**
   * Get an element from an ID.
   */
  Element getElementByID(String id, Document doc);

  /**
   * The getUnparsedEntityURI function returns the URI of the unparsed
   * entity with the specified name in the same document as the context
   * node (see [3.3 Unparsed Entities]). It returns the empty string if
   * there is no such entity.
   */
  String getUnparsedEntityURI(String name, Document doc);

  /**
   * Set whether or not the liaison attempts to expand namespaces.  Used
   * for optimization.  No longer used.
   * @deprecated
   */
  void setProcessNamespaces(boolean processNamespaces);

  /**
   * Tells if namespaces should be supported.  For optimization purposes.
   * @deprecated
   */
  boolean getProcessNamespaces();

  /**
   * Register an extension namespace handler. This handler provides
   * functions for testing whether a function is known within the
   * namespace and also for invoking the functions.
   *
   * @param uri the URI for the extension.
   * @param extNS the extension handler.
   */
  void addExtensionNamespace (String uri,
         ExtensionFunctionHandler extNS);

  /**
   * <meta name="usage" content="internal"/>
   * ThrowFoundIndex tells if FoundIndex should be thrown
   * if index is found.
   * This is an optimization for match patterns, and
   * is used internally by the XPath engine.
   */
  boolean getThrowFoundIndex();

  /**
   * <meta name="usage" content="internal"/>
   * ThrowFoundIndex tells if FoundIndex should be thrown
   * if index is found.
   * This is an optimization for match patterns, and
   * is used internally by the XPath engine.
   */
  void setThrowFoundIndex(boolean b);

  /**
   * Get the current error handler, if there is one.
   */
  org.xml.sax.ErrorHandler getErrorHandler();

  /**
   * getXLocatorHandler.
   */
  XLocator createXLocatorHandler();


  /**
   * Function that is called when a problem event occurs.
   *
   * @param   where             Either and XMLPARSER, XSLPROCESSOR, or QUERYENGINE.
   * @param   classification    Either ERROR or WARNING.
   * @param   styleNode         The style tree node where the problem
   *                            occurred.  May be null.
   * @param   sourceNode        The source tree node where the problem
   *                            occurred.  May be null.
   * @param   msg               A string message explaining the problem.
   * @param   lineNo            The line number where the problem occurred,
   *                            if it is known. May be zero.
   * @param   charOffset        The character offset where the problem,
   *                            occurred if it is known. May be zero.
   *
   * @return  true if the return is an ERROR, in which case
   *          exception will be thrown.  Otherwise the processor will
   *          continue to process.
   */
  boolean problem(short where, short classification,
                       Node styleNode, Node sourceNode,
                       String msg, int lineNo, int charOffset);

  public static final short    WARNING         = 1;
  public static final short    ERROR           = 2;

  public static final short    XMLPARSER       = 1;
  public static final short    XSLTPROCESSOR    = 2;
  public static final short    XPATHPARSER     = 3;
  public static final short    XPATHPROCESSOR     = 4;
  public static final short    DATASOURCE     = 5;
}
