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
import org.apache.xalan.xpath.xml.QName;

/**
 * <meta name="usage" content="advanced"/>
 * This class provides services that must be implemented by 
 * the hosting environment, in other words, stuff that is 
 * external to XPath.  At this point, it is likely that 
 * this interface should be folded into XPathSupport.
 */
public interface XPathEnvSupport
{
  /**
   * Given a valid element key, return the corresponding node list.
   */
  NodeList getNodeSetByKey(Node doc, String name,
                           String ref, org.apache.xalan.xpath.xml.PrefixResolver nscontext)
    throws org.xml.sax.SAXException;

  /**
   * Given a name, locate a variable in the current context, and return
   * the Object.
   */
  XObject getVariable(QName name)
    throws org.xml.sax.SAXException;

  /**
   * Get table of source tree documents.
   * Document objects are keyed by URL string.
   */
  Hashtable getSourceDocsTable();

  /**
   * Given a DOM Document, tell what URI was used to parse it.
   */
  String findURIFromDoc(Document owner);  // Needed for relative resolution

  /**
   * Get the factory object required to create DOM nodes
   * in the result tree.
   */
  public void setDOMFactory(Document domFactory);

  /**
   * Get a DOM document, primarily for creating result
   * tree fragments.
   */
  Document getDOMFactory();

  /**
   * Execute the function-available() function.
   */
  boolean functionAvailable(String namespace,
                            String extensionName);

  /**
   * Execute the element-available() function.
   */
  boolean elementAvailable(String namespace,
                            String extensionName);


  /**
   * Handle an extension function.
   */
  Object extFunction(String namespace, String extensionName,
                                Vector argVec, Object methodKey)
    throws org.xml.sax.SAXException;

  /**
   * Get an XLocator provider keyed by node.  This get's
   * the association based on the root of the tree that the
   * node is parented by.
   */
  XLocator getXLocatorFromNode(Node node);

  /**
   * Associate an XLocator provider to a node based on the root of the tree that the
   * node is parented by.
   */
  void associateXLocatorToNode(Node node, XLocator xlocator);

  /**
   * Determine whether extra whitespace should be stripped from the node. The determination is based
   * on the combination of the default-space attribute on xsl:stylesheet, xsl:strip-space, xsl:preserve-space,
   * and the xml:space attribute.
   * Literal elements from template elements should
   * <em>not</em> be tested with this function.
   * @param textNode A text node from the source tree.
   * @return true if the text node should be stripped of extra whitespace.
   */
  boolean shouldStripSourceNode(Node textNode)
    throws org.xml.sax.SAXException;

}
