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
package org.apache.xalan.xpath;

import org.apache.xalan.xpath.xml.XSLMessages;
import org.apache.xalan.xpath.res.XPATHErrorResources;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * <meta name="usage" content="internal"/>
 * Class to protect a MutableNodeListImpl from being written to.  Really a 
 * debugging class.
 */
class EmptyNodeListImpl extends MutableNodeListImpl
{
  EmptyNodeListImpl()
  {
  }
  
  public void addNode(Node n)
  {
    // checkDups(n);
    throw new RuntimeException(XSLMessages.createXPATHMessage(XPATHErrorResources.ER_CANNOT_WRITE_TO_EMPTYNODELISTIMPL, null)); //"Programmer's error: EmptyNodeListImpl can not be written to.");
  }

  /**
   * Insert a node at a given position.
   */
  public void insertNode(Node n, int pos)
  {
    // checkDups(n);
    throw new RuntimeException(XSLMessages.createXPATHMessage(XPATHErrorResources.ER_CANNOT_WRITE_TO_EMPTYNODELISTIMPL, null)); //"Programmer's error: EmptyNodeListImpl can not be written to.");
  }


  /**
   * Remove a node.
   */
  public void removeNode(Node n)
  {
    throw new RuntimeException(XSLMessages.createXPATHMessage(XPATHErrorResources.ER_CANNOT_WRITE_TO_EMPTYNODELISTIMPL, null)); //"Programmer's error: EmptyNodeListImpl can not be written to.");
  }

  /**
   * Set a item to null, so the list doesn't
   * have to keep being compressed.
   */
  public void setItemNull(int pos)
  {
    throw new RuntimeException(XSLMessages.createXPATHMessage(XPATHErrorResources.ER_CANNOT_WRITE_TO_EMPTYNODELISTIMPL, null)); //"Programmer's error: EmptyNodeListImpl can not be written to.");
  }
  
  /**
   * Copy NodeList members into this nodelist, adding in 
   * document order.  If a node is null, don't add it.
   */
  public void addNodes(NodeList nodelist)
  {
    throw new RuntimeException(XSLMessages.createXPATHMessage(XPATHErrorResources.ER_CANNOT_WRITE_TO_EMPTYNODELISTIMPL, null)); //"Programmer's error: EmptyNodeListImpl can not be written to.");
  }

  /**
   * Copy NodeList members into this nodelist, adding in 
   * document order.  If a node is null, don't add it.
   */
  public void addNodesInDocOrder(NodeList nodelist, XPathSupport support)
  {
    throw new RuntimeException(XSLMessages.createXPATHMessage(XPATHErrorResources.ER_CANNOT_WRITE_TO_EMPTYNODELISTIMPL, null)); //"Programmer's error: EmptyNodeListImpl can not be written to.");
  }
  
  /**
   * Add the node into a vector of nodes where it should occur in 
   * document order.
   * @param v Vector of nodes, presumably containing Nodes
   * @param obj Node object.
   * @param test true if we should test for doc order
   * @return insertIndex.
   */
  public int addNodeInDocOrder(Node node, boolean test, XPathSupport support)
  {
    throw new RuntimeException(XSLMessages.createXPATHMessage(XPATHErrorResources.ER_CANNOT_WRITE_TO_EMPTYNODELISTIMPL, null)); //"Programmer's error: EmptyNodeListImpl can not be written to.");

  } // end addNodeInDocOrder(Vector v, Object obj)

  /**
   * Add the node into a vector of nodes where it should occur in 
   * document order.
   * @param v Vector of nodes, presumably containing Nodes
   * @param obj Node object.
   */
  public int addNodeInDocOrder(Node node, XPathSupport support)
  {
    throw new RuntimeException(XSLMessages.createXPATHMessage(XPATHErrorResources.ER_CANNOT_WRITE_TO_EMPTYNODELISTIMPL, null)); //"Programmer's error: EmptyNodeListImpl can not be written to.");
  } // end addNodeInDocOrder(Vector v, Object obj)
}
