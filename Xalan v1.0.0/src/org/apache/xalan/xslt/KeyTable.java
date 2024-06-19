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
package org.apache.xalan.xslt;

import org.w3c.dom.*;
import java.util.*;
import org.apache.xalan.xpath.NodeListImpl;
import org.apache.xalan.xpath.XObject;
import org.apache.xalan.xpath.xml.XMLParserLiaison; 
import org.apache.xalan.xpath.XPathSupport; 
import org.apache.xalan.xpath.KeyDeclaration;
import org.apache.xalan.xpath.xml.XMLParserLiaisonDefault;

// import org.apache.xalan.xpath.dtm.*;

/**
 * <meta name="usage" content="advanced"/>
 * Table of element keys, keyed by document node.  An instance of this 
 * class is keyed by a Document node that should be matched with the 
 * root of the current context.  It contains a table of name mappings 
 * to tables that contain mappings of identifier values to nodes.
 */
public class KeyTable
{
  /**
   * The document key.  This table should only be used with contexts
   * whose Document roots match this key.
   */
  Node m_docKey;
   
  /**
   * Hashtable of keys.
   * The table is:
   * a) keyed by name,
   * b) each with a value of a hashtable, keyed by the value returned by 
   *    the use attribute,
   * c) each with a value that is a nodelist.
   * Thus, for a given key or keyref, look up hashtable by name, 
   * look up the nodelist by the given reference.
   */
  Hashtable m_keys = new Hashtable();

  /**
   * Build a keys table.
   * @param doc The owner document key (normally the same as startNode).
   * @param startNode The node to start itterating from to build the keys index.
   * @param nscontext The stylesheet's namespace context.
   * @param keyDeclarations The stylesheet's xsl:key declarations.
   * @param xmlLiaison The parser liaison for support of getNodeData(useNode).
   */
  KeyTable(Node doc, Node startNode, org.apache.xalan.xpath.xml.PrefixResolver nscontext, 
           String name, Vector keyDeclarations, XMLParserLiaison xmlLiaison)
   throws org.xml.sax.SAXException
  {    
    m_docKey = doc;
    XPathSupport execContext = xmlLiaison;
    
    try
    {
      org.apache.xalan.xpath.dtm.DTMProxy docp = (org.apache.xalan.xpath.dtm.DTMProxy)doc;
      
      org.apache.xalan.xpath.dtm.DTMProxy dtmp = (org.apache.xalan.xpath.dtm.DTMProxy)startNode;
      int dtmpPos = dtmp.getDTMNodeNumber();
      org.apache.xalan.xpath.dtm.DTM dtm = dtmp.getDTM();
      boolean breakout = false;
      
      // Walk across the kids until all have been accounted for
      for (int kid = dtmpPos; 
           kid != -1; 
           kid = dtm.getNextNode(dtmpPos, kid))
      {
        org.apache.xalan.xpath.dtm.DTMProxy testNode = dtm.getNode(kid);
        // Walk through each of the declarations made with xsl:key
        int nDeclarations = keyDeclarations.size();
        for(int i = 0; i < nDeclarations; i++)
        {
          KeyDeclaration kd = (KeyDeclaration)keyDeclarations.elementAt(i);
          if(!kd.m_name.equals(name)) 
            continue;
          // We are currently processing this key declaration.
          // More than likely, the key function was called in the 
          // xsl:key declaration using the same key name.
          if( kd.m_buildState == KeyDeclaration.BUILDING)
          {
           // Need to break out of nodes loop!
            breakout = true;
            break;
          }  
          kd.m_buildState = KeyDeclaration.BUILDING;
          
          // See if our node matches the given key declaration according to 
          // the match attribute on xsl:key.
          double score = kd.m_match.getMatchScore(execContext, testNode);
          if(score == kd.m_match.MATCH_SCORE_NONE)
          {
            kd.m_buildState = KeyDeclaration.BUILT;
            continue;
          }  

          // Query from the node, according the the select pattern in the
          // use attribute in xsl:key.
          XObject xuse = kd.m_use.execute(xmlLiaison, testNode, nscontext);
          
          NodeList nl = null;
          int nUseValues;
          String exprResult = null;
          if(xuse.getType() != xuse.CLASS_NODESET)
          {
            nUseValues = 1;
            exprResult = xuse.str();
          }
          else
          {
            nl = xuse.nodeset();
            if(0 == nl.getLength())
            {  
              kd.m_buildState = KeyDeclaration.BUILT;
              continue;
            }  
            
            // Use each node in the node list as a key value that we'll be 
            // able to use to look up the given node.
            nUseValues = nl.getLength();
          }
          for(int k = 0; k < nUseValues; k++)
          {            
            // Use getExpr to get the string value of the given node. I hope 
            // the string assumption is the right thing... I can't see how 
            // it could work any other way.
            if(null != nl)
            {
              Node useNode = nl.item(k);
              exprResult = XMLParserLiaisonDefault.getNodeData(useNode);
            }
            
            if(null == exprResult)
              continue;
            
            // Look to see if there's already a table indexed by the 
            // name attribute on xsl:key.  If there's not, create one.
            Hashtable namedKeyTable;
            {
              Object keyTableObj = m_keys.get(kd.m_name);
              if(null == keyTableObj)
              {
                namedKeyTable = new Hashtable();
                m_keys.put(kd.m_name, namedKeyTable);
              }
              else
              {
                namedKeyTable = (Hashtable)keyTableObj;
              }
            }
            
            // Look to see if we already have row indexed by 
            // the node value, which is one of the nodes found via
            // the use attribute of xsl:key.  If there's not a row, 
            // create one.
            NodeListImpl keyNodes;
            {
              Object nodeListObj = namedKeyTable.get(exprResult);
              if(null == nodeListObj)
              {
                keyNodes = new NodeListImpl();
                namedKeyTable.put(exprResult, keyNodes);
              }
              else
              {
                keyNodes = (NodeListImpl)nodeListObj;
              }
            }
            
            // See if the matched node is already in the 
            // table set.  If it is there, we're done, otherwise 
            // add it.
            boolean foundit = false;
            int nKeyNodes = keyNodes.size();
            for(int j = 0; j < nKeyNodes; j++)
            {
              if(testNode == keyNodes.item(j))
              {
                foundit = true;
                break;
              }
            } // end for j
            if(!foundit)
            {
              keyNodes.addElement(testNode);
            }
          } // end for(int k = 0; k < nUseValues; k++)
          kd.m_buildState = KeyDeclaration.BUILT;
          break;
        } // end for(int i = 0; i < nDeclarations; i++)
        
        // Need to break out of main loop?
        if (breakout)
          break;
      }

    }
    catch(ClassCastException cce)
    {
      Node pos = startNode;

      // Do a non-recursive pre-walk over the tree.
      while(null != pos)
      {     
        int nDeclarations = keyDeclarations.size();
        
        // We're going to have to walk the attribute list 
        // if it's an element, so get the attributes.
        NamedNodeMap attrs = null;
        int nNodes;
        if(Node.ELEMENT_NODE == pos.getNodeType())
        {
          attrs = ((Element)pos).getAttributes();
          nNodes = attrs.getLength();
          if(0 == nNodes)
            attrs = null;
        }
        else
        {
          nNodes = 0;
        }
        
        // Walk the primary node, and each of the attributes.
        // This loop is a little strange... it is meant to always 
        // execute once, then execute for each of the attributes.
        Node testNode = pos;
        for(int nodeIndex = -1; nodeIndex < nNodes;)
        {
          // Walk through each of the declarations made with xsl:key
          for(int i = 0; i < nDeclarations; i++)
          {
            KeyDeclaration kd = (KeyDeclaration)keyDeclarations.elementAt(i);
            
            if(!kd.m_name.equals(name)) 
              continue;
            // We are currently processing this key declaration.
            // More than likely, the key function was called in the 
            // xsl:key declaration using the same key name.
            if( kd.m_buildState == KeyDeclaration.BUILDING)
            {
              return;
              // break;
            }  
            kd.m_buildState = KeyDeclaration.BUILDING;
            
            // See if our node matches the given key declaration according to 
            // the match attribute on xsl:key.
            double score = kd.m_match.getMatchScore(execContext, testNode);
            if(score == kd.m_match.MATCH_SCORE_NONE)
            {
              kd.m_buildState = KeyDeclaration.BUILT;
              continue;
            } 
            // Query from the node, according the the select pattern in the
            // use attribute in xsl:key.
            XObject xuse = kd.m_use.execute(execContext, testNode, nscontext);

            NodeList nl = null;
            int nUseValues;
            String exprResult = null;
            if(xuse.getType() != xuse.CLASS_NODESET)
            {
              nUseValues = 1;
              exprResult = xuse.str();
            }
            else
            {
              nl = xuse.nodeset();
              if(0 == nl.getLength())
              {  
                kd.m_buildState = KeyDeclaration.BUILT;
                continue;
              }  
              
              // Use each node in the node list as a key value that we'll be 
              // able to use to look up the given node.
              nUseValues = nl.getLength();
            }
            
            for(int k = 0; k < nUseValues; k++)
            {
              if(null != nl)
              {
                Node useNode = nl.item(k);
                exprResult = XMLParserLiaisonDefault.getNodeData(useNode);
              }
              
              if(null == exprResult)
                continue;
              
              // Look to see if there's already a table indexed by the 
              // name attribute on xsl:key.  If there's not, create one.
              Hashtable namedKeyTable;
              {
                Object keyTableObj = m_keys.get(kd.m_name);
                if(null == keyTableObj)
                {
                  namedKeyTable = new Hashtable();
                  m_keys.put(kd.m_name, namedKeyTable);
                }
                else
                {
                  namedKeyTable = (Hashtable)keyTableObj;
                }
              }
              
              // Look to see if we already have row indexed by 
              // the node value, which is one of the nodes found via
              // the use attribute of xsl:key.  If there's not a row, 
              // create one.
              NodeListImpl keyNodes;
              {
                Object nodeListObj = namedKeyTable.get(exprResult);
                if(null == nodeListObj)
                {
                  keyNodes = new NodeListImpl();
                  namedKeyTable.put(exprResult, keyNodes);
                }
                else
                {
                  keyNodes = (NodeListImpl)nodeListObj;
                }
              }
              
              // See if the matched node is already in the 
              // table set.  If it is there, we're done, otherwise 
              // add it.
              boolean foundit = false;
              int nKeyNodes = keyNodes.size();
              for(int j = 0; j < nKeyNodes; j++)
              {
                if(testNode == keyNodes.item(j))
                {
                  foundit = true;
                  break;
                }
              } // end for j
              if(!foundit)
              {
                keyNodes.addElement(testNode);
              }
            } // end for(int k = 0; k < nUseValues; k++)
            
            kd.m_buildState = KeyDeclaration.BUILT;
            break;

          } // end for(int i = 0; i < nDeclarations; i++)
          nodeIndex++;
          if(null != attrs)
          {
            testNode = attrs.item(nodeIndex);
          }
        } // for(int nodeIndex = -1; nodeIndex < nNodes; nodeIndex++)
        
        // The rest of this is getting the next prewalk position in 
        // the tree.
        Node nextNode = ((Node.ELEMENT_NODE == pos.getNodeType()) 
                         || (Node.DOCUMENT_NODE == pos.getNodeType())) ? pos.getFirstChild() : null;
        while(null == nextNode)
        {
          if(startNode == pos)
            break;
          nextNode = pos.getNextSibling();
          if(null == nextNode)
          {
            pos = pos.getParentNode();
            if((startNode == pos) || (null == pos))
            {
              nextNode = null;
              break;
            }
          }
        }
        pos = nextNode;
      }
    }
    
    // Add null entries for any not found...
    int nDeclarations = keyDeclarations.size();
    for(int i = 0; i < nDeclarations; i++)
    {
      KeyDeclaration kd = (KeyDeclaration)keyDeclarations.elementAt(i);
      Object keyTableObj = m_keys.get(kd.m_name);
      if(null == keyTableObj)
      {
        Hashtable namedKeyTable = new Hashtable();
        m_keys.put(kd.m_name, namedKeyTable);
      }
    }
              
  } // end buildKeysTable method
    
  /**
   * Given a valid element key, return the corresponding node list. 
   * @param The name of the key, which must match the 'name' attribute on xsl:key.
   * @param ref The value that must match the value found by the 'match' attribute on xsl:key.
   * @return If the name was not declared with xsl:key, this will return null, 
   * if the identifier is not found, it will return an empty node set, 
   * otherwise it will return a nodeset of nodes.
   */
  public NodeList getNodeSetByKey(String name, String ref)
  {
    NodeList nl = null;
    Object keyTable = m_keys.get(name);
    if(null != keyTable)
    {
      Object keyNodes = ((Hashtable)keyTable).get(ref);
      if(null != keyNodes)
      {
        nl = (NodeList)keyNodes;
      }
      else
      {
        nl = new NodeListImpl();
      }
    }
    return nl;
  }

    
}
