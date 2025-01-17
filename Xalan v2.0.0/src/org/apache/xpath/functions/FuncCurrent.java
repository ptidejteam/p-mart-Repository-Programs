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
package org.apache.xpath.functions;

import org.apache.xpath.res.XPATHErrorResources;

import org.w3c.dom.Node;
import org.w3c.dom.traversal.NodeIterator;

import java.util.Vector;

import org.apache.xpath.XPathContext;
import org.apache.xpath.XPath;
import org.apache.xpath.objects.XObject;
import org.apache.xpath.objects.XNodeSet;
import org.apache.xpath.axes.PredicatedNodeTest;
import org.apache.xpath.axes.LocPathIterator;
import org.apache.xpath.axes.ContextNodeList;

/**
 * <meta name="usage" content="advanced"/>
 * Execute the current() function.
 */
public class FuncCurrent extends Function
{

  /**
   * Diagnostics to show string output from a node.
   *
   * @param n The input node, which may be null.
   *
   * @return A diagnostics string representing the node.
   */
  protected String nodeToString(Node n)
  {

    return (null != n)
           ? n.getNodeName() + "{" + ((org.apache.xalan.stree.Child) n).getUid() + "}"
           : "null";
  }

  /**
   * Execute the function.  The function must return
   * a valid object.
   * @param xctxt The current execution context.
   * @return A valid XObject.
   *
   * @throws javax.xml.transform.TransformerException
   */
  public XObject execute(XPathContext xctxt) throws javax.xml.transform.TransformerException
  {

    // If we're in a predicate, then this will return non-null.
    PredicatedNodeTest iter = (PredicatedNodeTest) xctxt.getSubContextList();
    Node currentNode;

    if (null != iter)
    {
      LocPathIterator lpi = iter.getLocPathIterator();

      currentNode = lpi.getCurrentContextNode();
    }
    else
    {
      ContextNodeList cnl = xctxt.getContextNodeList();

      if (null != cnl)
      {
        currentNode = cnl.getCurrentNode();
      }
      else
        currentNode = null;
    }

    return new XNodeSet(currentNode);
  }
}
