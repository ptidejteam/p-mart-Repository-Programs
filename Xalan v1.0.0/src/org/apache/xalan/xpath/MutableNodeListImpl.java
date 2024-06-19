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
import org.apache.xalan.xpath.xml.NodeVector;

/**
 * <meta name="usage" content="advanced"/>
 * Implementation of MutableNodeList.
 */
public class MutableNodeListImpl extends NodeVector implements MutableNodeList
{  
  /**
   * Create an empty nodelist.
   */
  public MutableNodeListImpl() 
  {
    super();
  }

  /**
   * Create an empty nodelist.
   */
  public MutableNodeListImpl(int blocksize) 
  {
    super(blocksize);
  }

  /**
   * Create a MutableNodeListImpl, and copy the members of the 
   * given nodelist into it.
   */
  public MutableNodeListImpl(NodeList nodelist) 
  {
    super();
    addNodes(nodelist);
  }

  /**
   * Create a MutableNodeListImpl, and copy the members of the 
   * given nodelist into it.
   */
  public MutableNodeListImpl(Node node) 
  {
    super();
    addNode(node);
  }
  
  /**
   * Returns the <code>index</code>th item in the collection. If 
   * <code>index</code> is greater than or equal to the number of nodes in 
   * the list, this returns <code>null</code>.
   * @param index Index into the collection.
   * @return The node at the <code>index</code>th position in the 
   *   <code>NodeList</code>, or <code>null</code> if that is not a valid 
   *   index.
   */
  public Node item(int index)
  {
    return (Node)this.elementAt(index);
  }
  
  /**
   * The number of nodes in the list. The range of valid child node indices is 
   * 0 to <code>length-1</code> inclusive. 
   */
  public int getLength()
  {
    return this.size();
  }
  
  /**
   * Check for duplicates, for diagnostics.
   */
  public boolean checkDups(Node node)
  {
    boolean foundit = false;
    int n = this.getLength();
    for(int i = 0; i < n; i++)
    {
      if(this.item(i).equals(node))
      {
        foundit = true;
        System.out.println("Found dup");
        break;
      }
    }
    return foundit;
  }
  
  public boolean checkDups()
  {
    boolean foundDup = false;
    int n = this.getLength();
    for(int i = 0; i < n; i++)
    {
      Node child = this.item(i);
      for(int k = 0; k < n; k++)
      {
        if(k != i)
        {
          if(this.item(k).equals(child))
          {
            foundDup = true;
            System.out.println("Found dup");
            break;
          }
        }
      }
    }
    return foundDup;
  }

  
  /**
   * Add a node.
   */
  public void addNode(Node n)
  {
    // checkDups(n);
    this.addElement(n);

  }

  /**
   * Insert a node at a given position.
   */
  public void insertNode(Node n, int pos)
  {
    // checkDups(n);
    insertElementAt(n, pos);
  }

  /**
   * Remove a node.
   */
  public void removeNode(Node n)
  {
    this.removeElement(n);
  }

  /**
   * Set a item to null, so the list doesn't
   * have to keep being compressed.
   */
  public void setItemNull(int pos)
  {
    this.setElementAt(null, pos);
  }
  
  /**
   * Copy NodeList members into this nodelist, adding in 
   * document order.  If a node is null, don't add it.
   */
  public void addNodes(NodeList nodelist)
  {
    if(null != nodelist) // defensive to fix a bug that Sanjiva reported.
    {
      int nChildren = nodelist.getLength();
      for(int i = 0; i < nChildren; i++)
      {
        Node obj = nodelist.item(i);
        if(null != obj)
        {
          addElement(obj);
        }
      }
    }
    // checkDups();
  }

  /**
   * Copy NodeList members into this nodelist, adding in 
   * document order.  If a node is null, don't add it.
   */
  public void addNodesInDocOrder(NodeList nodelist, XPathSupport support)
  {
    int nChildren = nodelist.getLength();
    for(int i = 0; i < nChildren; i++)
    {
      Node node = nodelist.item(i);
      if(null != node)
      {
        addNodeInDocOrder(node, support);
      }
    }
    // checkDups();
    /*
    int len = nodelist.getLength();
    if(len > 0)
    {
      addNodesInDocOrder(0, size()-1, len-1, nodelist);
    }
    */
  }
  
  /**
   * Not yet ready for prime time.
   * I can't use recursion in this.
   */
  private boolean addNodesInDocOrder(int start, int end, int testIndex, 
                                     NodeList nodelist, XPathSupport support)
  {
    boolean foundit = false;
    int i;
    try
    {
      Node nodeObj = nodelist.item(testIndex);
      int index1 = ((org.apache.xalan.xpath.dtm.DTMProxy)nodeObj).getDTMNodeNumber();
      for(i = end; i >= start; i--)
      {
        int index2 = ((org.apache.xalan.xpath.dtm.DTMProxy)elementAt(i)).getDTMNodeNumber();
        if(index2 == index1)
        {
          i = -2; // Duplicate, suppress insert
          break; 
        }
        if(index2 < index1)
        {
          insertElementAt(nodeObj, i+1);
          testIndex--;
          if(testIndex > 0)
          {
            boolean foundPrev 
              = addNodesInDocOrder(0, i, testIndex, nodelist, support);
            if(!foundPrev)
            {
              addNodesInDocOrder(i, size()-1, testIndex, nodelist, support);
            }
          }
          break;
        }
      }
      if(i == -1)
      {
        insertElementAt(nodeObj, 0);
      }
    }
    catch(ClassCastException cce)
    {
      Node node = nodelist.item(testIndex);
      for(i = end; i >= start; i--)
      {
        Node child = (Node)elementAt(i);
        if(child == node)
        {
          i = -2; // Duplicate, suppress insert
          break; 
        }
        if(!isNodeAfter(node, child, support))
        {
          insertElementAt(node, i+1);
          testIndex--;
          if(testIndex > 0)
          {
            boolean foundPrev 
              = addNodesInDocOrder(0, i, testIndex, nodelist, support);
            if(!foundPrev)
            {
              addNodesInDocOrder(i, size()-1, testIndex, nodelist, support);
            }
          }
          break;
        }
      }
      if(i == -1)
      {
        insertElementAt(node, 0);
      }
   }
    return foundit;
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
    int insertIndex = -1;
    if(test)
    {
      try
      {
        int index1 = ((org.apache.xalan.xpath.dtm.DTMProxy)node).getDTMNodeNumber();
        
        // This needs to do a binary search, but a binary search 
        // is somewhat tough because the sequence test involves 
        // two nodes.
        int size = size(), i;
        for(i = size-1; i >= 0; i--)
        {
          Node node2 = (Node)elementAt(i);
          // Check to see if the nodes share the same parent.  If they 
          // do not, then don't try to sort with it.
          if(!((((1 == index1) && (node == node2)) ||
             (node.getOwnerDocument() == node2.getOwnerDocument()))))
          {
            continue;
          }
          int index2 = ((org.apache.xalan.xpath.dtm.DTMProxy)node2).getDTMNodeNumber();
          if((index2 == index1))
          {
            i = -2; // Duplicate, suppress insert
            break; 
          }
          if(index2 < index1)
          {
            break;
          }
        }
        if(i != -2)
        {
          insertIndex = i+1;
          insertElementAt(node, insertIndex);
        }
        
      }
      catch(ClassCastException cce)
      {
        // This needs to do a binary search, but a binary search 
        // is somewhat tough because the sequence test involves 
        // two nodes.
        int size = size(), i;
        for(i = size-1; i >= 0; i--)
        {
          Node child = (Node)elementAt(i);
          if(child == node)
          {
            i = -2; // Duplicate, suppress insert
            break; 
          }
          if(!isNodeAfter(node, child, support))
          {
            break;
          }
        }
        if(i != -2)
        {
          insertIndex = i+1;
          insertElementAt(node, insertIndex);
        }
      }
    }
    else
    {
      insertIndex = this.size();
      boolean foundit = false;
      for(int i = 0; i < insertIndex; i++)
      {
        if(this.item(i).equals(node))
        {
          foundit = true;
          break;
        }
      }
      if(!foundit)
        addElement(node);
    }
    // checkDups();
    return insertIndex;
  } // end addNodeInDocOrder(Vector v, Object obj)

  /**
   * Add the node into a vector of nodes where it should occur in 
   * document order.
   * @param v Vector of nodes, presumably containing Nodes
   * @param obj Node object.
   */
  public int addNodeInDocOrder(Node node, XPathSupport support)
  {
    return addNodeInDocOrder(node, true, support);
  } // end addNodeInDocOrder(Vector v, Object obj)

  /**
   * Figure out if node2 should be placed after node1 when 
   * placing nodes in a list that is to be sorted in 
   * document order.  Assumes that node1 and node2 are not
   * equal.
   * NOTE: Make sure this does the right thing with attribute nodes!!!
   * @return true if node2 should be placed 
   * after node1, and false if node2 should be placed 
   * before node1.
   */
  public static boolean isNodeAfter(Node node1, Node node2, XPathSupport support)
  {
    // Assume first that the nodes are DTM nodes, since discovering node 
    // order is massivly faster for the DTM.
    try
    {
      int index1 = ((org.apache.xalan.xpath.dtm.DTMProxy)node1).getDTMNodeNumber();
      int index2 = ((org.apache.xalan.xpath.dtm.DTMProxy)node2).getDTMNodeNumber();
      return index1 <= index2;
    }
    catch(ClassCastException cce)
    {
      // isNodeAfter will return true if node is after countedNode 
      // in document order. isDOMNodeAfter is sloooow (relativly).
      return isDOMNodeAfter(node1, node2, support);
    }
  }  
  
  /**
   * Figure out if node2 should be placed after node1 when 
   * placing nodes in a list that is to be sorted in 
   * document order.  Assumes that node1 and node2 are not
   * equal.
   * NOTE: Make sure this does the right thing with attribute nodes!!!
   * @return true if node2 should be placed 
   * after node1, and false if node2 should be placed 
   * before node1.
   */
  static boolean isDOMNodeAfter(Node node1, Node node2, XPathSupport support)
  {
    boolean isNodeAfter = false; // return value.
    
    Node parent1 = support.getParentOfNode(node1);
    Node parent2 = support.getParentOfNode(node2);
    
    // Optimize for most common case
    if(parent1 == parent2) // then we know they are siblings
    {
      if (null != parent1)
        isNodeAfter = isNodeAfterSibling(
                                       parent1, node1, node2);
      else
      {
        if(node1 == node2) // Same document?
          return false;
        else
          return true;
      }
    }
    else
    {
      // General strategy: Figure out the lengths of the two 
      // ancestor chains, and walk up them looking for the 
      // first common ancestor, at which point we can do a 
      // sibling compare.  Edge condition where one is the 
      // ancestor of the other.
      
      // Count parents, so we can see if one of the chains 
      // needs to be equalized.
      int nParents1 = 2, nParents2 = 2; // count node & parent obtained above
      while(parent1 != null)
      {
        nParents1++;
        parent1 = support.getParentOfNode(parent1);
      }
      
      while(parent2 != null)
      {
        nParents2++;
        parent2 = support.getParentOfNode(parent2);
      }
      
      Node startNode1 = node1, startNode2 = node2; // adjustable starting points
      
      // Do I have to adjust the start point in one of 
      // the ancesor chains?
      if(nParents1 < nParents2)
      {
        // adjust startNode2
        int adjust = nParents2 - nParents1;
        for(int i = 0; i < adjust; i++)
        {
          startNode2 = support.getParentOfNode(startNode2);
        }
      }
      else if(nParents1 > nParents2)
      {
        // adjust startNode1
        int adjust = nParents1 - nParents2;
        for(int i = 0; i < adjust; i++)
        {
          startNode1 = support.getParentOfNode(startNode1);
        }
      }
      
      Node prevChild1 = null, prevChild2 = null; // so we can "back up"
      
      // Loop up the ancestor chain looking for common parent.
      while(null != startNode1)
      {
        if(startNode1 == startNode2) // common parent?
        {
          if(null == prevChild1) // first time in loop?
          {
            // Edge condition: one is the ancestor of the other.
            isNodeAfter = (nParents1 < nParents2) ? true : false;
            break; // from while loop
          }
          else
          {
            isNodeAfter = isNodeAfterSibling(
                                             startNode1, prevChild1, prevChild2);
            break; // from while loop
          }
        } // end if(startNode1 == startNode2)
        prevChild1 = startNode1;
        startNode1 = support.getParentOfNode(startNode1);
        prevChild2 = startNode2;
        startNode2 = support.getParentOfNode(startNode2);
      } // end while
    } // end big else
    
    /* -- please do not remove... very useful for diagnostics --
    System.out.println("node1 = "+node1.getNodeName()+"("+node1.getNodeType()+")"+
    ", node2 = "+node2.getNodeName()
    +"("+node2.getNodeType()+")"+
    ", isNodeAfter = "+isNodeAfter); */
    
    return isNodeAfter;
  } // end isNodeAfter(Node node1, Node node2)
  
  /**
   * Figure out if child2 is after child1 in document order.
   * @param parent Must be the parent of child1 and child2.
   * @param child1 Must be the child of parent and not equal to child2.
   * @param child2 Must be the child of parent and not equal to child1.
   * @returns true if child 2 is after child1 in document order.
   */
  private static boolean isNodeAfterSibling(
                                     Node parent, Node child1, Node child2)
  {
    boolean isNodeAfterSibling = false;
    int child1type = child1.getNodeType();
    int child2type = child2.getNodeType();
    if((Node.ATTRIBUTE_NODE != child1type) && (Node.ATTRIBUTE_NODE == child2type))
    {
      // always sort attributes before non-attributes.
      isNodeAfterSibling = false;
    }
    else if((Node.ATTRIBUTE_NODE == child1type) && (Node.ATTRIBUTE_NODE != child2type))
    {
      // always sort attributes before non-attributes.
      isNodeAfterSibling = true;
    }
    else if(Node.ATTRIBUTE_NODE == child1type)
    {
      NamedNodeMap children = parent.getAttributes();
      int nNodes = children.getLength();
      boolean found1 = false, found2 = false;
      for(int i = 0; i < nNodes; i++)
      {
        Node child = children.item(i);
        if(child1 == child)
        {
          if(found2)
          {
            isNodeAfterSibling = false;
            break;
          }
          found1 = true;
        }
        else if(child2 == child)
        {
          if(found1)
          {
            isNodeAfterSibling = true;
            break;
          }
          found2 = true;
        }
      }
    }
    else
    {
      // NodeList children = parent.getChildNodes();
      // int nNodes = children.getLength();
      Node child = parent.getFirstChild();
      boolean found1 = false, found2 = false;
      while(null != child)
      {
        // Node child = children.item(i);
        if(child1 == child)
        {
          if(found2)
          {
            isNodeAfterSibling = false;
            break;
          }
          found1 = true;
        }
        else if(child2 == child)
        {
          if(found1)
          {
            isNodeAfterSibling = true;
            break;
          }
          found2 = true;
        }
        child = child.getNextSibling();
      }
    }
    return isNodeAfterSibling;
  } // end isNodeAfterSibling(Node parent, Node child1, Node child2)
  
}

