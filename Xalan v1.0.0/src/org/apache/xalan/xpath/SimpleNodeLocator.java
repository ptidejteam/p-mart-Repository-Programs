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

import java.util.*;
import java.io.*;
import org.w3c.dom.*;
import org.apache.xalan.xpath.res.XPATHErrorResources;

/**
 * <meta name="usage" content="advanced"/>
 * SimpleNodeLocator implements a search of one or more DOM trees.
 * By using the connect function as an extension, the user may 
 * specify a directory and a filter specification for XML files 
 * that will be searched.
 * This is a singleton class.
 */
public class SimpleNodeLocator implements XLocator, Serializable
{  
  /**
   * Create a SimpleNodeLocator object.
   */
  public SimpleNodeLocator()
  {
  }
  
  /**
   * The singleton instance of this class.
   */
  private static SimpleNodeLocator m_locater = null;
  
  /**
   * Empty node list for failed results.
   */
  private static MutableNodeList emptyQueryResults = new EmptyNodeListImpl();
  
  /**
   * Empty node list for failed results.
   */
  private static XNodeSet emptyNodeList = new XNodeSet(emptyQueryResults);

  /**
   * The the default locator.
   */
  public static XLocator getDefaultLocator()
  {
    m_locater = (null == m_locater) ? new SimpleNodeLocator() : m_locater;
    return m_locater;
  }

  /**
   * Execute the proprietary connect() function, which returns an 
   * instance of XLocator.  When the XPath object sees a return type 
   * of XLocator, it will call the locationPath function that passes 
   * in the connectArgs.  The opPos and args params are not used 
   * by this function.  This really is just a factory function 
   * for the XLocator instance, but this fact is hidden from the 
   * user.
   * @param opPos The current position in the xpath.m_opMap array.
   * @param args The function args.
   * @returns A node set of Document nodes.
   */
  public static XLocator query(String path, String fileSpec) 
  {    
    m_locater = (null == m_locater) ? new SimpleNodeLocator() : m_locater;
    return m_locater;
  }
  
  /**
   * (Same as query for the moment).
   * @param opPos The current position in the xpath.m_opMap array.
   * @param args The function args.
   * @returns A node set of Document nodes.
   */
  public static XLocator connect(String path, String fileSpec) 
  {    
    m_locater = (null == m_locater) ? new SimpleNodeLocator() : m_locater;
    return m_locater;
  }
  
  /**
   * Execute a connection (if it was not executed by the static 
   * connect method) and process the following LocationPath, 
   * if it is present.  Normally, the connection functionality 
   * should be executed by this function and not the static connect 
   * function, which is really a factory method for the XLocator 
   * instance.  The arguments to the static connect function
   * are re-passed to this function.
   * @param xpath The xpath that is executing.
   * @param context The current source tree context node.
   * @param opPos The current position in the xpath.m_opMap array.
   * @param connectArgs The same arguments that were passed to the 
   * static connect function.
   * @returns the result of the query in an XNodeSet object.
   */
  public XNodeSet connectToNodes(XPath xpath, XPathSupport execContext, Node context, 
                                 int opPos, Vector connectArgs) 
    throws org.xml.sax.SAXException
  {    
    String fileSpec = ((XObject)connectArgs.elementAt(0)).str();
    FileFilter filter = null;
    String filterSpec = null;
    if(connectArgs.size() > 1)
    {
      filterSpec = ((XObject)connectArgs.elementAt(1)).str();
      filter = new FileFilter(filterSpec);
    }
    
    File dir = new File(fileSpec);
    
    XNodeSet results = new XNodeSet();
    MutableNodeList mnl = results.mutableNodeset();
    
    if(null != dir)
    {
      String filenames[] = (filter != null) ? dir.list(filter) : dir.list();
      if(null != filenames)
      {
        int nFiles = filenames.length;
        for(int i = 0; i < nFiles; i++)
        {
          try
          {
            String urlString = "file:"+dir.getAbsolutePath()+File.separatorChar+filenames[i];
            // java.net.URL url = execContext.getURLFromString(filenames[i], null);
            java.net.URL url = new java.net.URL(urlString);
            Document doc = execContext.parseXML(url, null, null);
            if(null != doc)
            {
              int op = xpath.m_opMap[opPos];
              if(XPath.OP_LOCATIONPATH == (op & XPath.LOCATIONPATHEX_MASK))
              {
                XNodeSet xnl = xpath.locationPath(execContext, doc, opPos, null, null, false);
                if(null != xnl)
                {
                  mnl.addNodes(xnl.nodeset());
                  execContext.associateXLocatorToNode(doc, this);
                }
              }
              else
              {
                mnl.addNode(doc);
                execContext.associateXLocatorToNode(doc, this);
              }
            }
          }
          catch(Exception e)
          {
            System.out.println("Couldn't parse file: "+e.getMessage());
          }
        }
      }
      else
      {
        System.out.println("Couldn't get a file list from filespec");
      }
    }
    else
    {
      System.out.println("Filespec was bad in connect");
    }
    
    return results;
  }
  
  /**
   * Computes the union of its operands which must be node-sets.
   * @param context The current source tree context node.
   * @param opPos The current position in the m_opMap array.
   * @returns the union of node-set operands, or an empty set if 
   * callback methods are used.
   */
  public XNodeSet union(XPath xpath, XPathSupport execContext, 
                        Node context, int opPos,
                        NodeCallback callback,
                        Object callbackInfo) 
    throws org.xml.sax.SAXException
  {
    XNodeSet resultNodeSet = null;
    
    opPos = xpath.getFirstChildPos(opPos); 

    while((xpath.m_opMap[opPos] & XPath.LOCATIONPATHEX_MASK) == XPath.OP_LOCATIONPATH)
    {
      int nextOpPos = xpath.getNextOpPos(opPos);

      // XNodeSet expr = (XNodeSet)xpath.execute(execContext, context, opPos);
      opPos = xpath.getFirstChildPos(opPos);
      MutableNodeList mnl = step(xpath, execContext, context, opPos,
                                 callback, callbackInfo, false, false);
      XNodeSet expr = (null != mnl) ? new XNodeSet(mnl) : emptyNodeList;

      if(null == resultNodeSet)
      {
        resultNodeSet = expr;
      }
      else if(expr != emptyNodeList)
      {
        if(resultNodeSet != emptyNodeList)
        {
          MutableNodeList nl = resultNodeSet.mutableNodeset();
          nl.addNodesInDocOrder(expr.nodeset(), execContext);
        }
        else
        {
          resultNodeSet = expr;
        }
      }
      opPos = nextOpPos;
    }
    
    return resultNodeSet;
  }

  /**
   * Execute a location path.  Normally, this method simply 
   * moves past the OP_LOCATIONPATH and it's length member, 
   * and calls the Step function, which will recursivly process 
   * the rest of the LocationPath, and then wraps the NodeList result
   * in an XNodeSet object.
   * @param xpath The xpath that is executing.
   * @param execContext The execution context.
   * @param context The current source tree context node.
   * @param opPos The current position in the xpath.m_opMap array.
   * @param callback Interface that implements the processLocatedNode method.
   * @param callbackInfo Object that will be passed to the processLocatedNode method.
   * @returns the result of the query in an XNodeSet object.
   */
  public XNodeSet locationPath(XPath xpath, XPathSupport execContext, 
                               Node context, int opPos,
                               NodeCallback callback,
                               Object callbackInfo, boolean stopAtFirst) 
    throws org.xml.sax.SAXException
  {    
    XNodeSet results;
    boolean isSimpleFollowing = (0 != (xpath.m_opMap[opPos] & XPath.LOCATIONPATHEX_ISSIMPLE));
    opPos = xpath.getFirstChildPos(opPos);
    MutableNodeList mnl;
    try
    {
      mnl = step(xpath, execContext, context, opPos,
                 callback, callbackInfo, isSimpleFollowing, false);
    }
    catch(FoundIndex fi)
    {
      mnl = step(xpath, execContext, context, opPos,
                 null, null, false, false);
    }

    results = (null != mnl) ? new XNodeSet(mnl) : emptyNodeList;
    return results;
  }  
    
  /**
   * Execute a step and predicates in a location path.  This recursivly 
   * executes each of the steps and predicates.
   * The step can be oneof XPath.OP_VARIABLE, OP_EXTFUNCTION,
   * OP_FUNCTION, OP_GROUP, FROM_ROOT, FROM_PARENT, FROM_SELF,
   * FROM_ANCESTORS, FROM_ANCESTORS_OR_SELF, MATCH_ATTRIBUTE,
   * FROM_ATTRIBUTES, MATCH_ANY_ANCESTOR, MATCH_IMMEDIATE_ANCESTOR,
   * FROM_CHILDREN, FROM_DESCENDANTS,
   * FROM_DESCENDANTS_OR_SELF, FROM_FOLLOWING, FROM_FOLLOWING_SIBLINGS,
   * FROM_PRECEDING, FROM_PRECEDING_SIBLINGS, or FROM_NAMESPACE.
   * Normally, this function should recurse to process the next 
   * step.  However, it should not continue to process the location 
   * path if the step is oneof MATCH_ATTRIBUTE, MATCH_ANY_ANCESTOR, or 
   * match MATCH_IMMEDIATE_ANCESTOR.
   * This method may be overridden to process the LocationPath as 
   * a whole, or the fromXXX methods may be overridden to process  
   * the steps as individual units.
   * @param xpath The xpath that is executing.
   * @param context The current source tree context node.
   * @param opPos The current position in the xpath.m_opMap array.
   * @returns a node-set.
   */
  protected MutableNodeList step(XPath xpath, XPathSupport execContext, 
                                 Node context, int opPos,
                                 NodeCallback callback, Object callbackInfo,
                                 boolean isSimpleFollowing, boolean stopAtFirst) 
    throws org.xml.sax.SAXException
  {    
    // int endStep = xpath.getNextOpPos(opPos);
    int stepType = xpath.m_opMap[opPos];
    int argLen = 200; // dummy val to shut up compiler
    MutableNodeList subQueryResults = null;
    MutableNodeList queryResults = null;
    boolean shouldReorder = false;
    boolean continueStepRecursion = true;
    boolean mightHaveToChangeLocators = false;
    boolean variableArgLen = false;
    
    if(isSimpleFollowing && (null != callback))
      execContext.pushContextNodePosition();
        
    // If the step has an index predicate, which we won't know until after the 
    // predicate is evaluated, and isSimpleFollowing is true, 
    // then a FoundIndex exception will be thrown, and we'll try again with 
    // a full blown vector search.  This will not be good at all for a few 
    // cases, but for the majority cases, the isSimpleFollowing optimization 
    // is worth it.
    for(int i = 0; i < 2; i++)
    {
      try
      {
        switch(stepType)
        {
        case XPath.OP_VARIABLE:
        case XPath.OP_EXTFUNCTION:
        case XPath.OP_FUNCTION:
        case XPath.OP_GROUP:
          subQueryResults = new MutableNodeListImpl();
          argLen = findNodeSet(xpath, execContext, context, opPos, 
                               stepType, subQueryResults, isSimpleFollowing, stopAtFirst);
          mightHaveToChangeLocators = true;
          variableArgLen = true;
          break;
        case XPath.FROM_ROOT:
          subQueryResults = findRoot(xpath, execContext, context, opPos, 
                                     stepType, subQueryResults, isSimpleFollowing, stopAtFirst);
          break;
        case XPath.FROM_PARENT:
          subQueryResults = findParent(xpath, execContext, context, opPos, 
                                       stepType, subQueryResults, isSimpleFollowing, stopAtFirst);
          break;
        case XPath.FROM_SELF:
          subQueryResults = findSelf(xpath, execContext, context, opPos, stepType, subQueryResults,
                                     callback, callbackInfo, isSimpleFollowing, stopAtFirst);
          break;
        case XPath.FROM_ANCESTORS:
          subQueryResults = findAncestors(xpath, execContext, context, opPos,  stepType, subQueryResults, isSimpleFollowing, stopAtFirst);
          shouldReorder = true;
          break;
        case XPath.FROM_ANCESTORS_OR_SELF:
          subQueryResults = findAncestorsOrSelf(xpath, execContext, context, opPos,  stepType, subQueryResults, isSimpleFollowing, stopAtFirst);
          shouldReorder = true;
          break;
        case XPath.MATCH_ATTRIBUTE:
          continueStepRecursion = false;
          // fall-through on purpose.
        case XPath.FROM_ATTRIBUTES:
          subQueryResults = findAttributes(xpath, execContext, context, opPos,  stepType, subQueryResults, isSimpleFollowing, stopAtFirst);
          break;
        case XPath.MATCH_ANY_ANCESTOR:
        case XPath.MATCH_IMMEDIATE_ANCESTOR:
          continueStepRecursion = false;
          // fall-through on purpose.
        case XPath.FROM_CHILDREN:
          subQueryResults = findChildren(xpath, execContext, context, opPos,  stepType, subQueryResults,
                                         callback, callbackInfo, isSimpleFollowing, stopAtFirst);
          break;
        case XPath.FROM_DESCENDANTS:
        case XPath.FROM_DESCENDANTS_OR_SELF:
          subQueryResults = findDescendants(xpath, execContext, context, opPos,  stepType, subQueryResults,
                                            callback, callbackInfo, isSimpleFollowing, stopAtFirst);
          break;
        case XPath.FROM_FOLLOWING:
          subQueryResults = findFollowing(xpath, execContext, context, opPos,  stepType, subQueryResults, isSimpleFollowing, stopAtFirst);
          break;
        case XPath.FROM_FOLLOWING_SIBLINGS:
          subQueryResults = findFollowingSiblings(xpath, execContext, context, opPos,  stepType, subQueryResults, isSimpleFollowing, stopAtFirst);
          break;
        case XPath.FROM_PRECEDING:
          subQueryResults = findPreceding(xpath, execContext, context, opPos,  stepType, subQueryResults, isSimpleFollowing, stopAtFirst);
          shouldReorder = true;
          break;
        case XPath.FROM_PRECEDING_SIBLINGS:
          subQueryResults = findPrecedingSiblings(xpath, execContext, context, opPos,  stepType, subQueryResults, isSimpleFollowing, stopAtFirst);
          shouldReorder = true;
          break;
        case XPath.FROM_NAMESPACE:
          subQueryResults = findNamespace(xpath, execContext, context, opPos,  stepType, subQueryResults, isSimpleFollowing, stopAtFirst);
          break;
        default:
          subQueryResults = findNodesOnUnknownAxis(xpath, execContext, context, opPos,  stepType, subQueryResults, isSimpleFollowing, stopAtFirst);
          break;
        }

        break; // from loop of 2
      }
      catch(FoundIndex fi)
      {
        if(isSimpleFollowing && (null != callback))
        {
          execContext.popContextNodePosition();
          subQueryResults = new MutableNodeListImpl();
          isSimpleFollowing = false;
          stopAtFirst = false;
        }
        else
        {
          throw fi;
        }
      }
      finally
      {
        if(isSimpleFollowing && (null != callback))
          execContext.popContextNodePosition();
      }
    }
    
    if(null == subQueryResults)
      return null;

    if(!variableArgLen)
      argLen = xpath.getArgLengthOfStep(opPos)+3;

    opPos += argLen;
    int nextStepType = xpath.m_opMap[opPos];
    
    try
    {
      execContext.pushContextNodeList(subQueryResults);
      
      if(XPath.OP_PREDICATE == nextStepType)
      {
        int[] endPredicatesPos = 
        {
          -42};
        subQueryResults = predicates(xpath, execContext, context, opPos, 
                                     subQueryResults, endPredicatesPos);
        opPos = endPredicatesPos[0];
        nextStepType = xpath.m_opMap[opPos];
      }
      
      if(null == subQueryResults)
        return null;
      
      XLocator xlocator = this;
      if((XPath.ENDOP != nextStepType) && continueStepRecursion)
      {
        int nContexts = subQueryResults.getLength();
        for(int i = 0; i < nContexts; i++)
        {
          Node node = subQueryResults.item(i);
          if(null != node)
          {
            if(mightHaveToChangeLocators)
            {
              xlocator = execContext.getXLocatorFromNode(node);
            }
            MutableNodeList mnl = step(xpath, execContext, node, opPos, null, null, false, false);
            if((null != mnl) && mnl != this.emptyNodeList)
            {
              if(queryResults == null)
              {
                queryResults = mnl;
              }
              else
              {
                queryResults.addNodesInDocOrder(mnl, execContext);
              }
            }
          }
        }
      }
      else
      {
        if(shouldReorder)
        {
          queryResults = new MutableNodeListImpl();
          queryResults.addNodesInDocOrder(subQueryResults, execContext);
        }
        else
        {
          queryResults = subQueryResults;
        }
      }
    }
    finally
    {
      execContext.popContextNodeList();
    }

    return queryResults;
  }
  
  /**
   * Add a node to a node list, but lazily create the node list if 
   * it doesn't already exist.
   */
  private final MutableNodeList addNode(MutableNodeList subQueryResults, Node node)
  {
    if(null == subQueryResults)
      subQueryResults = new MutableNodeListImpl(node);
    else
      subQueryResults.addNode(node);
    return subQueryResults;
  }
  
  /**
   * Add a node to a node list, but lazily create the node list if 
   * it doesn't already exist.
   */
  private final MutableNodeList addNodeInDocOrder(XPathSupport execContext, MutableNodeList subQueryResults, Node node)
  {
    if(null == subQueryResults)
      subQueryResults = new MutableNodeListImpl(node);
    else
      subQueryResults.addNodeInDocOrder(node, execContext);
    return subQueryResults;
  }
  
  
  /**
   * Add the context to the list if it meets the NodeTest qualification.
   * @param xpath The xpath that is executing.
   * @param context The current source tree context node.
   * @param opPos The current position in the xpath.m_opMap array.
   * @param stepType Value of XPath.FROM_SELF.
   * @param subQueryResults Should be an empty node list where the 
   * results of the step will be put.
   * @returns the length of the argument (i.e. amount to add to predicate pos 
   * or end of step).
   */
  protected MutableNodeList findSelf(XPath xpath, XPathSupport execContext, Node context, int opPos, 
                                     int stepType, MutableNodeList subQueryResults,
                                     NodeCallback callback, Object callbackInfo,
                                     boolean isSimpleFollowing, boolean stopAtFirst)
    throws org.xml.sax.SAXException
  {
    int argLen = xpath.getArgLengthOfStep(opPos);
    opPos = xpath.getFirstChildPosOfStep(opPos);
    if(argLen > 0)
    {
      if(XPath.MATCH_SCORE_NONE != nodeTest(xpath, execContext, context, opPos, argLen, stepType))
      {
        // subQueryResults.addNode(context);
        if(isSimpleFollowing && (null != callback))
        {
          execContext.incrementContextNodePosition(context);
          if(predicate(xpath, execContext, context, opPos+argLen))
          {
            callback.processLocatedNode(execContext, context, callbackInfo);
          }
        }
        else
        {
          subQueryResults = addNode(subQueryResults, context);
        }
      }
    }
    else
    {
      // subQueryResults.addNode(context);
      if(isSimpleFollowing && (null != callback))
      {
        execContext.incrementContextNodePosition(context);
        if(predicate(xpath, execContext, context, opPos+argLen))
        {
          callback.processLocatedNode(execContext, context, callbackInfo);
        }
      }
      else
      {
        subQueryResults = addNode(subQueryResults, context);
      }
    }
    return subQueryResults;
  }
  
  /**
   * Add attributes to the list if they meet 
   * the NodeTest qualification.
   * @param xpath The xpath that is executing.
   * @param context The current source tree context node.
   * @param opPos The current position in the xpath.m_opMap array.
   * @param stepType Value of XPath.FROM_ATTRIBUTES.
   * @param subQueryResults Should be an empty node list where the 
   * results of the step will be put.
   * @returns the length of the argument (i.e. amount to add to predicate pos 
   * or end of step).
   */
  protected MutableNodeList findAttributes(XPath xpath, XPathSupport execContext, Node context, int opPos, 
                                           int stepType, MutableNodeList subQueryResults,
                                           boolean isSimpleFollowing, boolean stopAtFirst)
    throws org.xml.sax.SAXException
  {
    int argLen = xpath.getArgLengthOfStep(opPos);
    opPos = xpath.getFirstChildPosOfStep(opPos);
    if( (null != context) && (context.getNodeType()==Node.ELEMENT_NODE) )
    {
      NamedNodeMap attributeList = context.getAttributes();
      if( attributeList != null ) 
      {
        int nAttrs = attributeList.getLength();
        for( int j=0; j < nAttrs; j++ )
        {
          Attr attr = (Attr)attributeList.item(j);
          if(XPath.MATCH_SCORE_NONE != nodeTest(xpath, execContext, attr, opPos, argLen, stepType))
          {
            subQueryResults = addNode(subQueryResults, attr);
            // If we have an attribute name here, we can quit.
          }
        }
      }
    }
    return subQueryResults;
  }
  
  /**
   * Add the namespace node of the context.
   * @param xpath The xpath that is executing.
   * @param context The current source tree context node.
   * @param opPos The current position in the xpath.m_opMap array.
   * @param stepType Value of XPath.FROM_NAMESPACE.
   * @param subQueryResults Should be an empty node list where the 
   * results of the step will be put.
   * @returns the length of the argument (i.e. amount to add to predicate pos 
   * or end of step).
   */
  protected MutableNodeList findNamespace(XPath xpath, XPathSupport execContext, Node context, int opPos, 
                                          int stepType, MutableNodeList subQueryResults,
                                          boolean isSimpleFollowing, boolean stopAtFirst)
    throws org.xml.sax.SAXException
  {
    int argLen = xpath.getArgLengthOfStep(opPos);
    opPos = xpath.getFirstChildPosOfStep(opPos);
    if( (null != context) && (context.getNodeType()==Node.ELEMENT_NODE) )
    {
      NamedNodeMap attributeList = context.getAttributes();
      if( attributeList != null ) 
      {
        int nAttrs = attributeList.getLength();
        for( int j=0; j < nAttrs; j++ )
        {
          Attr attr = (Attr)attributeList.item(j);
          if(XPath.MATCH_SCORE_NONE != nodeTest(xpath, execContext, attr, opPos, argLen, stepType))
          {
            subQueryResults = addNode(subQueryResults, attr);
            // If we have an attribute name here, we can quit.
          }
        }
      }
    }
    return subQueryResults;
  }

  /**
   * Add children to the list if they meet 
   * the NodeTest qualification.
   * @param xpath The xpath that is executing.
   * @param context The current source tree context node.
   * @param opPos The current position in the xpath.m_opMap array.
   * @param stepType Value of XPath.FROM_CHILDREN.
   * @param subQueryResults Should be an empty node list where the 
   * results of the step will be put.
   * @returns the length of the argument (i.e. amount to add to predicate pos 
   * or end of step).
   */
  protected MutableNodeList findChildren(XPath xpath, XPathSupport execContext, Node context, 
                                         int opPos, 
                                         int stepType, MutableNodeList subQueryResults,
                                         NodeCallback callback, Object callbackInfo,
                                         boolean isSimpleFollowing, boolean stopAtFirst)
    throws org.xml.sax.SAXException
  {
    int argLen = xpath.getArgLengthOfStep(opPos);
    opPos = xpath.getFirstChildPosOfStep(opPos);
    // If using a document fragment with references (non-standard) 
    // we can not use the next-sibling business at the top level.
    if(Node.DOCUMENT_FRAGMENT_NODE != context.getNodeType())
    {
      Node c = context.getFirstChild();
      while( null != c )
      {
        if(XPath.MATCH_SCORE_NONE != nodeTest(xpath, execContext, c, opPos, argLen, stepType))
        {
          // or else call execute method.  If no execute method,
          // add the node.
          // subQueryResults.addNode(c);
          if(isSimpleFollowing && (null != callback))
          {
            execContext.incrementContextNodePosition(c);
            if(predicate(xpath, execContext, c, opPos+argLen))
            {
              callback.processLocatedNode(execContext, c, callbackInfo);
              if(stopAtFirst)
                break;
            }
          }
          else
          {
            subQueryResults = addNode(subQueryResults, c);
          }
        }
        c = c.getNextSibling();
      }
    }
    else
    {
      NodeList children = context.getChildNodes();
      int n = children.getLength();
      for(int i = 0; i < n; i++)
      {
        Node c = children.item(i);
        if(XPath.MATCH_SCORE_NONE != nodeTest(xpath, execContext, c, opPos, argLen, stepType))
        {
          // subQueryResults.addNode(c);
          if(isSimpleFollowing && (null != callback))
          {
            execContext.incrementContextNodePosition(c);
            if(predicate(xpath, execContext, c, opPos+argLen))
            {
              callback.processLocatedNode(execContext, c, callbackInfo);
              if(stopAtFirst)
                break;
            }
          }
          else
          {
            subQueryResults = addNode(subQueryResults, c);
          }
        }
        c = c.getNextSibling();
      }
    }
    return subQueryResults;
  }
  
  /**
   * Add the descendants (and the context if the stepType is 
   * FROM_DESCENDANTS_OR_SELF) to the list if they meet 
   * the NodeTest qualification.
   * @param xpath The xpath that is executing.
   * @param context The current source tree context node.
   * @param opPos The current position in the xpath.m_opMap array.
   * @param stepType Value of XPath.FROM_DESCENDANTS or XPath.FROM_DESCENDANTS_OR_SELF.
   * @param subQueryResults Should be an empty node list where the 
   * results of the step will be put.
   * @returns the length of the argument (i.e. amount to add to predicate pos 
   * or end of step).
   */
  protected MutableNodeList findDescendants(XPath xpath, XPathSupport execContext, 
                                            Node context, int opPos, 
                                            int stepType, MutableNodeList subQueryResults,
                                            NodeCallback callback, Object callbackInfo,
                                            boolean isSimpleFollowing, boolean stopAtFirst)
    throws org.xml.sax.SAXException
  {
    int argLen = xpath.getArgLengthOfStep(opPos);
    opPos = xpath.getFirstChildPosOfStep(opPos);

    // Perform a pre-order traversal of descendents.
    // Note that I would like to be able to do optimization here 
    // where if I have a simple tag name node test, I would 
    // like to be able to call 
    // ((Element)context).getElementsByTagName(m_token).
    // One problem is that it would return a NodeList, not a 
    // NodeListImpl.
    /*
    if(lookahead('[', 1) || lookahead('(', 1) 
    || (Node.ELEMENT_NODE != context.getNodeType()))
    {
    */
    Node pos = context;
    
    // If using a document fragment with references (non-standard) 
    // we can not use the next-sibling business at the top level.
    if(Node.DOCUMENT_FRAGMENT_NODE != context.getNodeType())
    {
      while(null != pos)
      {                   
        if((stepType == XPath.FROM_DESCENDANTS_OR_SELF) || (context != pos))
        {
          if(XPath.MATCH_SCORE_NONE != nodeTest(xpath, execContext, pos, opPos, argLen, stepType))
          {
            // subQueryResults.addNode(pos);
            if(isSimpleFollowing && (null != callback))
            {
              execContext.incrementContextNodePosition(pos);
              if(predicate(xpath, execContext, pos, opPos+argLen))
              {
                callback.processLocatedNode(execContext, pos, callbackInfo);
                if(stopAtFirst)
                  break;
              }
            }
            else
            {
              subQueryResults = addNode(subQueryResults, pos);
            }
          }
        }
        Node nextNode = pos.getFirstChild();
        while(null == nextNode)
        {
          if(context.equals( pos ))
            break;
          nextNode = pos.getNextSibling();
          if(null == nextNode)
          {
            pos = pos.getParentNode();
            if((pos == null) || (context.equals( pos )))
            {
              nextNode = null;
              break;
            }
          }
        }
        pos = nextNode;
      }
    }
    else
    {
      NodeList children = context.getChildNodes();
      int n = children.getLength();
      for(int i = 0; i < n; i++)
      {
        pos = children.item(i);
        context = pos;
        while(null != pos)
        {                   
          if((stepType == XPath.FROM_DESCENDANTS_OR_SELF) || (!context.equals( pos )))
          {
            if(XPath.MATCH_SCORE_NONE != nodeTest(xpath, execContext, pos, opPos, argLen, stepType))
            {
              if(isSimpleFollowing && (null != callback))
              {
                execContext.incrementContextNodePosition(pos);
                if(predicate(xpath, execContext, pos, opPos+argLen))
                {
                  callback.processLocatedNode(execContext, pos, callbackInfo);
                  if(stopAtFirst)
                    break;
                }
              }
              else
              {
                subQueryResults = addNode(subQueryResults, pos);
              }
            }
          }
          Node nextNode = pos.getFirstChild();
          while(null == nextNode)
          {
            if(context.equals( pos ))
              break;
            nextNode = pos.getNextSibling();
            if(null == nextNode)
            {
              pos = pos.getParentNode();
              if((context.equals( pos )) || (pos == null))
              {
                nextNode = null;
                break;
              }
            }
          }
          pos = nextNode;
        }
      }
    }
    return subQueryResults;
  }

  /**
   * Add the nodes following the context to the list if they meet 
   * the NodeTest qualification.
   * The following axis contains all nodes in the same document as 
   * the context node that are after the context node in document 
   * order, excluding any descendants and excluding attribute nodes 
   * and namespace nodes; the nodes are ordered in document order.
   * Note that the ancestor, descendant, following, preceding and 
   * self axes partition a document (ignoring attribute and namespace 
   * nodes): they do not overlap and together they contain all the 
   * nodes in the document.
   * @param xpath The xpath that is executing.
   * @param context The current source tree context node.
   * @param opPos The current position in the xpath.m_opMap array.
   * @param stepType Value of XPath.FROM_FOLLOWING.
   * @param subQueryResults Should be an empty node list where the 
   * results of the step will be put.
   * @returns the length of the argument (i.e. amount to add to predicate pos 
   * or end of step).
   */
  protected MutableNodeList findFollowing(XPath xpath, XPathSupport execContext, Node context, int opPos, 
                                          int stepType, MutableNodeList subQueryResults,
                                          boolean isSimpleFollowing, boolean stopAtFirst)
    throws org.xml.sax.SAXException
  {
    int argLen = xpath.getArgLengthOfStep(opPos);
    opPos = xpath.getFirstChildPosOfStep(opPos);

    // What fun...
    Document doc = context.getOwnerDocument();
    Node pos = context;
    while(null != pos)
    {  
      Node nextNode;
      if(pos != context)
      {
        if(XPath.MATCH_SCORE_NONE != nodeTest(xpath, execContext, pos, opPos, argLen, stepType))
        {
          subQueryResults = addNodeInDocOrder(execContext, subQueryResults, pos);
        }
        nextNode = pos.getFirstChild();
      }
      else
      {
        nextNode = null;
      }
      while(null == nextNode)
      {
        nextNode = pos.getNextSibling();
        if(null == nextNode)
        {
          pos = pos.getParentNode();
          if((doc == pos) || (null == pos))
          {
            nextNode = null;
            break;
          }
        }
      }
      pos = nextNode;
    }
    return subQueryResults;
  }
  


  /**
   * Add the sibling nodes following the context to the list if they meet 
   * the NodeTest qualification.
   * @param xpath The xpath that is executing.
   * @param context The current source tree context node.
   * @param opPos The current position in the xpath.m_opMap array.
   * @param stepType Value of XPath.FROM_FOLLOWING_SIBLINGS.
   * @param subQueryResults Should be an empty node list where the 
   * results of the step will be put.
   * @returns the length of the argument (i.e. amount to add to predicate pos 
   * or end of step).
   */
  protected MutableNodeList findFollowingSiblings(XPath xpath, XPathSupport execContext, Node context, int opPos, 
                                                  int stepType, MutableNodeList subQueryResults,
                                                  boolean isSimpleFollowing, boolean stopAtFirst)
    throws org.xml.sax.SAXException
  {
    int argLen = xpath.getArgLengthOfStep(opPos);
    opPos = xpath.getFirstChildPosOfStep(opPos);

    Node pos = context.getNextSibling();
    while(null != pos)
    {                   
      if(XPath.MATCH_SCORE_NONE != nodeTest(xpath, execContext, pos, opPos, argLen, stepType))
      {
        subQueryResults = addNode(subQueryResults, pos);
      }
      pos = pos.getNextSibling();
    }
    return subQueryResults;
  }
  
  
  /**
   * Execute a step that performs an OP_VARIABLE, OP_EXTFUNCTION,
   * OP_FUNCTION, or OP_GROUP function.
   * @param xpath The xpath that is executing.
   * @param context The current source tree context node.
   * @param opPos The current position in the xpath.m_opMap array.
   * @param stepType One of OP_VARIABLE, OP_EXTFUNCTION,
   * OP_FUNCTION, or OP_GROUP.
   * @param subQueryResults Should be an empty node list where the 
   * results of the step will be put.
   * @returns the length of the argument (i.e. amount to add to predicate pos 
   * or end of step).
   */
  protected int findNodeSet(XPath xpath, XPathSupport execContext, Node context, int opPos, 
                            int stepType, MutableNodeList subQueryResults,
                            boolean isSimpleFollowing, boolean stopAtFirst)
    throws org.xml.sax.SAXException
  {
    XObject obj = xpath.execute(execContext, context, opPos);
    NodeList nl = obj.nodeset();
    
    // Should this be adding in doc order?
    // We can not simply assign the nl value to 
    // subQueryResults, because nl may be a ref to 
    // a variable or the like, and we may mutate 
    // below... which results in a hard-to-find bug!
    subQueryResults.addNodes(nl);
    return xpath.m_opMap[opPos+XPath.MAPINDEX_LENGTH];
  }
  
  /**
   * Execute a step to the root.
   * @param xpath The xpath that is executing.
   * @param context The current source tree context node.
   * @param opPos The current position in the xpath.m_opMap array.
   * @param stepType Value of XPath.FROM_ROOT.
   * @param subQueryResults Should be an empty node list where the 
   * results of the step will be put.
   * @returns the length of the argument (i.e. amount to add to predicate pos 
   * or end of step).
   */
  protected MutableNodeList findRoot(XPath xpath, XPathSupport execContext, Node context, int opPos, 
                                     int stepType, MutableNodeList subQueryResults,
                                     boolean isSimpleFollowing, boolean stopAtFirst)
    throws org.xml.sax.SAXException
  {
    int argLen = xpath.getArgLengthOfStep(opPos);
    opPos = xpath.getFirstChildPosOfStep(opPos);

    Document docContext = (Node.DOCUMENT_NODE == context.getNodeType()) 
                          ? (Document)context : context.getOwnerDocument();
    subQueryResults = addNode(subQueryResults, docContext);
    return subQueryResults;
  }
  
  /**
   * Add the parent to the list if it meets the NodeTest qualification.
   * @param xpath The xpath that is executing.
   * @param context The current source tree context node.
   * @param opPos The current position in the xpath.m_opMap array.
   * @param stepType Value of XPath.FROM_PARENT.
   * @param subQueryResults Should be an empty node list where the 
   * results of the step will be put.
   * @returns the length of the argument (i.e. amount to add to predicate pos 
   * or end of step).
   */
  protected MutableNodeList findParent(XPath xpath, XPathSupport execContext, Node context, int opPos, 
                                       int stepType, MutableNodeList subQueryResults,
                                       boolean isSimpleFollowing, boolean stopAtFirst)
    throws org.xml.sax.SAXException
  {
    context = execContext.getParentOfNode(context);
    int argLen = xpath.getArgLengthOfStep(opPos);
    if(null != context)
    {
      opPos = xpath.getFirstChildPosOfStep(opPos);
      if(argLen > 0)
      {
        if(XPath.MATCH_SCORE_NONE != nodeTest(xpath, execContext, context, opPos, argLen, stepType))
        {
          subQueryResults = addNode(subQueryResults, context);
        }
      }
      else
      {
        subQueryResults = addNode(subQueryResults, context);
      }
    }
    return subQueryResults;
  }
  
  /**
   * Add ancestors to the list if they meet the NodeTest qualification.
   * @param xpath The xpath that is executing.
   * @param context The current source tree context node.
   * @param opPos The current position in the xpath.m_opMap array.
   * @param stepType Value of XPath.FROM_ANCESTORS.
   * @param subQueryResults Should be an empty node list where the 
   * results of the step will be put.
   * @returns the length of the argument (i.e. amount to add to predicate pos 
   * or end of step).
   */
  protected MutableNodeList findAncestors(XPath xpath, XPathSupport execContext, Node context, int opPos, 
                                          int stepType, MutableNodeList subQueryResults,
                                          boolean isSimpleFollowing, boolean stopAtFirst)
    throws org.xml.sax.SAXException
  {
    context = execContext.getParentOfNode(context);
    int argLen = xpath.getArgLengthOfStep(opPos);
    opPos = xpath.getFirstChildPosOfStep(opPos);
    while(null != context)
    {
      if(XPath.MATCH_SCORE_NONE != nodeTest(xpath, execContext, context, opPos, argLen, stepType))
      {
        subQueryResults = addNode(subQueryResults, context);
      }
      context = execContext.getParentOfNode(context);
    }
    return subQueryResults;
  }
  
  /**
   * Add ancestors or the context to the list if they meet 
   * the NodeTest qualification.
   * @param xpath The xpath that is executing.
   * @param context The current source tree context node.
   * @param opPos The current position in the xpath.m_opMap array.
   * @param stepType Value of XPath.FROM_ANCESTORS_OR_SELF.
   * @param subQueryResults Should be an empty node list where the 
   * results of the step will be put.
   * @returns the length of the argument (i.e. amount to add to predicate pos 
   * or end of step).
   */
  protected MutableNodeList findAncestorsOrSelf(XPath xpath, XPathSupport execContext, Node context, int opPos, 
                                                int stepType, MutableNodeList subQueryResults,
                                                boolean isSimpleFollowing, boolean stopAtFirst)
    throws org.xml.sax.SAXException
  {
    int argLen = xpath.getArgLengthOfStep(opPos);
    opPos = xpath.getFirstChildPosOfStep(opPos);
    while(null != context)
    {
      if(XPath.MATCH_SCORE_NONE != nodeTest(xpath, execContext, context, opPos, argLen, stepType))
      {
        subQueryResults = addNode(subQueryResults, context);
      }
      context = execContext.getParentOfNode(context);
    }
    return subQueryResults;
  }

  /**
   * Add the nodes preceding the context to the list if they meet 
   * the NodeTest qualification.
   * The preceding axis contains all nodes in the same document 
   * as the context node that are before the context node in document 
   * order, excluding any ancestors and excluding attribute nodes 
   * and namespace nodes; the nodes are ordered in reverse document order.
   * Note that the ancestor, descendant, following, preceding and 
   * self axes partition a document (ignoring attribute and namespace 
   * nodes): they do not overlap and together they contain all the 
   * nodes in the document.
   * @param xpath The xpath that is executing.
   * @param context The current source tree context node.
   * @param opPos The current position in the xpath.m_opMap array.
   * @param stepType Value of XPath.FROM_PRECEDING.
   * @param subQueryResults Should be an empty node list where the 
   * results of the step will be put.
   * @returns the length of the argument (i.e. amount to add to predicate pos 
   * or end of step).
   */
  protected MutableNodeList findPreceding(XPath xpath, XPathSupport execContext, Node context, int opPos, 
                                          int stepType, MutableNodeList subQueryResults,
                                          boolean isSimpleFollowing, boolean stopAtFirst)
    throws org.xml.sax.SAXException
  {
    int argLen = xpath.getArgLengthOfStep(opPos);
    opPos = xpath.getFirstChildPosOfStep(opPos);

    // Ugh.  Reverse document order, no parents, I guess.
    Document doc = context.getOwnerDocument();
    Node pos = doc;
    while(null != pos)
    {       
      if(context.equals( pos ))
        break;

      if(XPath.MATCH_SCORE_NONE != nodeTest(xpath, execContext, pos, opPos, argLen, stepType))
      {
        // Ugh. If I could think a little better tonight, I'm
        // sure there's a better way to check for the parent.
        boolean isParent = false;
        Node parent = execContext.getParentOfNode(context);
        while(null != parent)
        {
          if(parent.equals( pos ))
          {
            isParent = true;
            break;
          }
          parent = execContext.getParentOfNode(parent);
        }
        
        if(!isParent)
        {
          if(null == subQueryResults)
            subQueryResults = new MutableNodeListImpl(pos);
          else
            subQueryResults.insertNode(pos, 0);
        }
      }
      Node nextNode = pos.getFirstChild();
      while(null == nextNode)
      {
        nextNode = pos.getNextSibling();
        if(null == nextNode)
        {
          pos = pos.getParentNode();
          
          // %%bug?
          if((null == pos) || (doc.equals( pos )))
          {
            nextNode = null;
            break;
          }
        }
      }
      pos = nextNode;
    }
    return subQueryResults;
  }
  
  /**
   * Add the sibling nodes preceding the context to the list if they meet 
   * the NodeTest qualification.
   * @param xpath The xpath that is executing.
   * @param context The current source tree context node.
   * @param opPos The current position in the xpath.m_opMap array.
   * @param stepType Value of XPath.FROM_PRECEDING_SIBLINGS.
   * @param subQueryResults Should be an empty node list where the 
   * results of the step will be put.
   * @returns the length of the argument (i.e. amount to add to predicate pos 
   * or end of step).
   */
  protected MutableNodeList findPrecedingSiblings(XPath xpath, XPathSupport execContext, Node context, int opPos, 
                                                  int stepType, MutableNodeList subQueryResults,
                                                  boolean isSimpleFollowing, boolean stopAtFirst)
    throws org.xml.sax.SAXException
  {
    int argLen = xpath.getArgLengthOfStep(opPos);
    opPos = xpath.getFirstChildPosOfStep(opPos);

    Node pos = context.getPreviousSibling();
    while(null != pos)
    {                   
      if(XPath.MATCH_SCORE_NONE != nodeTest(xpath, execContext, pos, opPos, argLen, stepType))
      {
        subQueryResults = addNode(subQueryResults, pos);
      }
      pos = pos.getPreviousSibling();
    }
    return subQueryResults;
  }
    
  /**
   * Add the namespace node of the context.
   * @param xpath The xpath that is executing.
   * @param context The current source tree context node.
   * @param opPos The current position in the xpath.m_opMap array.
   * @param stepType Value of XPath.FROM_NAMESPACE.
   * @param subQueryResults Should be an empty node list where the 
   * results of the step will be put.
   * @returns the length of the argument (i.e. amount to add to predicate pos 
   * or end of step).
   */
  protected MutableNodeList findNodesOnUnknownAxis(XPath xpath, XPathSupport execContext, Node context, int opPos, 
                                                   int stepType, MutableNodeList subQueryResults,
                                                   boolean isSimpleFollowing, boolean stopAtFirst)
    throws org.xml.sax.SAXException
  {
    int argLen = xpath.getArgLengthOfStep(opPos);
    opPos = xpath.getFirstChildPosOfStep(opPos);
    xpath.error(context, XPATHErrorResources.ER_UNKNOWN_AXIS, new Object[] {Integer.toString(stepType)}); //"unknown axis: "+stepType);
    return subQueryResults;
  }

  /**
   * Execute a single predicate for a single node.
   * @returns True if the node should not be filtered.
   */
  protected boolean predicate(XPath xpath, XPathSupport execContext, Node context, int opPos) 
    throws org.xml.sax.SAXException
  {
    boolean shouldNotFilter = true;

    int nextStepType = xpath.m_opMap[opPos];
    if(XPath.OP_PREDICATE == nextStepType)
    {
      execContext.pushDummyXPathContext();
      try
      {
        XObject pred = xpath.predicate(execContext, context, opPos);
        if(XObject.CLASS_NUMBER == pred.getType())
        {
          throw new FoundIndex(); // Ugly, but... see comment in the Step function.
        }
        else if(!pred.bool())
        {
          shouldNotFilter = false;
        }
      }
      finally
      {
        execContext.popXPathContext();
      }
    }
    return shouldNotFilter;
  }


  /**
   * Qualify a node list by it's predicates.
   * @param xpath The xpath that is executing.
   * @param context The current source tree context node.
   * @param opPos The current position in the xpath.m_opMap array.
   * @param subQueryResults The list of nodes that need to be qualified.
   * @param endPredicatesPos The end position in the xpath.m_opMap array will be filled in.
   * @returns the qualified subset of subQueryResults.
   */
  protected MutableNodeList predicates(XPath xpath, XPathSupport execContext, Node context, int opPos, 
                                       MutableNodeList subQueryResults,
                                       int[] endPredicatesPos) 
    throws org.xml.sax.SAXException
  {
    boolean hasNulls = false;
    int nextStepType = xpath.m_opMap[opPos];
    while(XPath.OP_PREDICATE == nextStepType)
    {
      int nContexts = subQueryResults.getLength();
      for(int i = 0; i < nContexts; i++)
      {
        XObject pred = xpath.predicate(execContext, subQueryResults.item(i), opPos);
        if(XObject.CLASS_NUMBER == pred.getType())
        {
          if((i+1) != (int)pred.num())
          {
            hasNulls = true;
            subQueryResults.setItemNull(i);
          }
        }
        else if(!pred.bool())
        {
          hasNulls = true;
          subQueryResults.setItemNull(i);
        }
      }
      opPos = xpath.getNextOpPos(opPos);
      nextStepType = xpath.m_opMap[opPos];
      if(XPath.OP_PREDICATE == nextStepType)
      {
        // This will reconstruct the node list without the nulls.
        subQueryResults = new MutableNodeListImpl(subQueryResults);
        
        execContext.popContextNodeList();
        execContext.pushContextNodeList(subQueryResults);
        // Don't break, loop 'till end so that opPos will be set to end.
        // if(0 == subQueryResults.getLength())
        //  break;
      }
    }
    if(null != endPredicatesPos)
      endPredicatesPos[0] = opPos;
    if(hasNulls)
    {
      subQueryResults = new MutableNodeListImpl(subQueryResults);
    }
    return subQueryResults;
  }
  
  /**
   * Execute a a location path pattern.  This will return a score
   * of MATCH_SCORE_NONE, MATCH_SCORE_NODETEST, 
   * MATCH_SCORE_OTHER, MATCH_SCORE_QNAME.
   * @param xpath The xpath that is executing.
   * @param context The current source tree context node.
   * @param opPos The current position in the xpath.m_opMap array.
   * @returns score, one of MATCH_SCORE_NODETEST, 
   * MATCH_SCORE_NONE, MATCH_SCORE_OTHER, MATCH_SCORE_QNAME.
   */
  public double locationPathPattern(XPath xpath, XPathSupport execContext, Node context, int opPos) 
    throws org.xml.sax.SAXException
  {    
    opPos = xpath.getFirstChildPos(opPos);
    double[] scoreHolder = 
    {
      XPath.MATCH_SCORE_NONE};
    stepPattern(xpath, execContext, context, opPos, scoreHolder);
    return scoreHolder[0];
  }

  /**
   * Execute a step in a location path.
   * @param xpath The xpath that is executing.
   * @param context The current source tree context node.
   * @param opPos The current position in the xpath.m_opMap array.
   * @returns the last matched context node.
   */
  protected Node stepPattern(XPath xpath, XPathSupport execContext, Node context, int opPos, double scoreHolder[]) 
    throws org.xml.sax.SAXException
  {    
    int startOpPos = opPos;
    int stepType = xpath.m_opMap[opPos];
    
    int endStep = xpath.getNextOpPos(opPos);
    int nextStepType = xpath.m_opMap[endStep];
    double score;
    
    if(XPath.ENDOP != nextStepType)
    {
      // Continue step via recursion...
      context = stepPattern(xpath, execContext, context, endStep, scoreHolder);
      if(null == context)
        scoreHolder[0] = XPath.MATCH_SCORE_NONE;
      if(scoreHolder[0] == XPath.MATCH_SCORE_NONE)
        return null;
      
      scoreHolder[0] = XPath.MATCH_SCORE_OTHER;
      context = execContext.getParentOfNode(context);
      if(null == context)
        return null;
    }
    
    int argLen;

    switch(stepType)
    {
    case XPath.OP_FUNCTION:
      {
        argLen = xpath.m_opMap[opPos+XPath.MAPINDEX_LENGTH];
        XObject obj = xpath.execute(execContext, context, opPos);
        NodeList nl = obj.nodeset();
        int len = nl.getLength();
        score = XPath.MATCH_SCORE_NONE;
        for(int i = 0; i < len; i++)
        {
          Node n = nl.item(i);
          score = (n.equals(context)) ? XPath.MATCH_SCORE_OTHER : XPath.MATCH_SCORE_NONE;
          if(score == XPath.MATCH_SCORE_OTHER)
          {
            context = n;
            break;
          }
        }
      }
      break;
    case XPath.FROM_ROOT:
      {
        argLen = xpath.getArgLengthOfStep(opPos);
        opPos = xpath.getFirstChildPosOfStep(opPos);
        Document docContext = (Node.DOCUMENT_NODE == context.getNodeType()) 
                              ? (Document)context : context.getOwnerDocument();
        score = (docContext.equals( context )) ? XPath.MATCH_SCORE_OTHER : XPath.MATCH_SCORE_NONE;
        if(score == XPath.MATCH_SCORE_OTHER)
        {
          context = docContext;
        }
      }
      break;
    case XPath.MATCH_ATTRIBUTE:
      {
        argLen = xpath.getArgLengthOfStep(opPos);
        opPos = xpath.getFirstChildPosOfStep(opPos);
        score = nodeTest(xpath, execContext, context, opPos, argLen, XPath.FROM_ATTRIBUTES);
        break;
      }
    case XPath.MATCH_ANY_ANCESTOR:
      argLen = xpath.getArgLengthOfStep(opPos);
      if(context.getNodeType() != Node.ATTRIBUTE_NODE)
      {
        opPos = xpath.getFirstChildPosOfStep(opPos);
        score = XPath.MATCH_SCORE_NONE;
        while(null != context)
        {
          score = nodeTest(xpath, execContext, context, opPos, argLen, stepType);
          if(XPath.MATCH_SCORE_NONE != score)
            break;
          // context = execContext.getParentOfNode(context);
          context = context.getParentNode();
        }
      }
      else
      {
        score = XPath.MATCH_SCORE_NONE;
      }
      break;
    case XPath.MATCH_IMMEDIATE_ANCESTOR:
      argLen = xpath.getArgLengthOfStep(opPos);
      if(context.getNodeType() != Node.ATTRIBUTE_NODE)
      {
        opPos = xpath.getFirstChildPosOfStep(opPos);
        score = nodeTest(xpath, execContext, context, opPos, argLen, stepType);
      }
      else
      {
        score = XPath.MATCH_SCORE_NONE;
      }
      break;
    default:
      argLen = xpath.getArgLengthOfStep(opPos);
      opPos = xpath.getFirstChildPosOfStep(opPos);
      score = XPath.MATCH_SCORE_NONE;
      xpath.error(context, XPATHErrorResources.ER_UNKNOWN_MATCH_OPERATION); //"unknown match operation!");
      break;
    }
    opPos += argLen;
    nextStepType = xpath.m_opMap[opPos];
    
    if(((score != XPath.MATCH_SCORE_NONE)) && (XPath.OP_PREDICATE == nextStepType))
    {
      score = XPath.MATCH_SCORE_OTHER;
      // Execute the xpath.predicates, but if we have an index, then we have 
      // to start over and do a search from the parent.  It would be nice 
      // if I could sense this condition earlier...
      try
      {
        // BUG: m_throwFoundIndex is not threadsafe
        execContext.setThrowFoundIndex(true);
        int startPredicates = opPos;
        opPos = startPredicates;
        nextStepType = xpath.m_opMap[opPos];
        while(XPath.OP_PREDICATE == nextStepType)
        {
          XObject pred = xpath.predicate(execContext, context, opPos);
          if(XObject.CLASS_NUMBER == pred.getType())
          {
            throw new FoundIndex();
          }
          else if(!pred.bool())
          {
            score = XPath.MATCH_SCORE_NONE;
            break; // from while(XPath.OP_PREDICATE == nextStepType)
          }
          opPos = xpath.getNextOpPos(opPos);
          nextStepType = xpath.m_opMap[opPos];
        }
        execContext.setThrowFoundIndex(false);
      }
      catch(FoundIndex fi)
      {
        // We have an index somewhere in our pattern.  So, we have 
        // to do a full search for our step, using the parent as 
        // context, then see if the current context is found in the 
        // node set.  Seems crazy, but, so far, it seems like the 
        // easiest way.
        execContext.setThrowFoundIndex(false);
        Node parentContext = execContext.getParentOfNode(context);
        MutableNodeList mnl = step(xpath, execContext, parentContext, startOpPos, null, null, false, false);
        int nNodes = mnl.getLength();
        score = XPath.MATCH_SCORE_NONE;
        for(int i = 0; i < nNodes; i++)
        {
          Node child = mnl.item(i);
          if((null != child) && child.equals( context ))
          {
            score = XPath.MATCH_SCORE_OTHER;
            break;
          }
        }
      }
    }
    // If we haven't found a score yet, or the test was 
    // negative, assign the score.
    if((scoreHolder[0] == XPath.MATCH_SCORE_NONE) || 
       (score == XPath.MATCH_SCORE_NONE))
      scoreHolder[0] = score;
    
    return (score == XPath.MATCH_SCORE_NONE) ? null : context;
  }

  
  /**
   * Test a node to see if it matches the given node test.
   * @param xpath The xpath that is executing.
   * @param context The current source tree context node.
   * @param opPos The current position in the xpath.m_opMap array.
   * @param len The length of the argument.
   * @param len The type of the step.
   * @returns score in an XNumber, one of MATCH_SCORE_NODETEST, 
   * MATCH_SCORE_NONE, MATCH_SCORE_OTHER, MATCH_SCORE_QNAME.
   */
  public double nodeTest(XPath xpath, XPathSupport execContext, Node context, int opPos, int argLen, int stepType)
    throws org.xml.sax.SAXException
  {
    double score;
    int testType = xpath.m_opMap[opPos];
    int nodeType = context.getNodeType();
    opPos++;
    switch(testType)
    {
    case XPath.NODETYPE_COMMENT:
      score = (Node.COMMENT_NODE == nodeType)
              ? XPath.MATCH_SCORE_NODETEST : XPath.MATCH_SCORE_NONE;
      break;
    case XPath.NODETYPE_TEXT:
      score = (((Node.CDATA_SECTION_NODE == nodeType) 
                || (Node.TEXT_NODE == nodeType)) &&
               (!execContext.shouldStripSourceNode(context)))
              ? XPath.MATCH_SCORE_NODETEST : XPath.MATCH_SCORE_NONE;
      break;
    case XPath.NODETYPE_PI:
      if( (Node.PROCESSING_INSTRUCTION_NODE == nodeType) )
      {
        if(argLen == 2)
        {
          XString name = (XString)xpath.m_tokenQueue[xpath.m_opMap[opPos]];
          score = ((ProcessingInstruction)context).getNodeName().equals(name.str())
                  ? XPath.MATCH_SCORE_QNAME : XPath.MATCH_SCORE_NONE;
        }
        else if(argLen == 1)
        {
          score = XPath.MATCH_SCORE_NODETEST;
        }
        else
        {
          score = XPath.MATCH_SCORE_NONE;
          xpath.error(XPATHErrorResources.ER_INCORRECT_ARG_LENGTH); //"Arg length of processing-instruction() node test is incorrect!");
        }
      }
      else
      {
        score = XPath.MATCH_SCORE_NONE;
      }
      break;
    case XPath.NODETYPE_NODE:
      if((Node.CDATA_SECTION_NODE == nodeType) 
         || (Node.TEXT_NODE == nodeType))
      {
        score = (!execContext.shouldStripSourceNode(context))
                ? XPath.MATCH_SCORE_NODETEST : XPath.MATCH_SCORE_NONE;
      }
      else
      {
        score = XPath.MATCH_SCORE_NODETEST;
      }
      break;
    case XPath.NODETYPE_ROOT:
      score = ( (Node.DOCUMENT_FRAGMENT_NODE == nodeType) 
                || (Node.DOCUMENT_NODE == nodeType))
              ? XPath.MATCH_SCORE_OTHER : XPath.MATCH_SCORE_NONE;
      break;
      
    case XPath.NODENAME:
      {
        if(!((Node.ATTRIBUTE_NODE == nodeType) || (Node.ELEMENT_NODE == nodeType)))
          return XPath.MATCH_SCORE_NONE;
                                        
        boolean test;
        int queueIndex = xpath.m_opMap[opPos];
        String targetNS = (queueIndex >= 0) ? (String)xpath.m_tokenQueue[xpath.m_opMap[opPos]]
                                              : null;
        opPos++;
        
        // From the draft: "Two expanded names are equal if they 
        // have the same local part, and either both have no URI or 
        // both have the same URI."
        // "A node test * is true for any node of the principal node type. 
        // For example, child::* will select all element children of the 
        // context node, and attribute::* will select all attributes of 
        // the context node."
        // "A node test can have the form NCName:*. In this case, the prefix 
        // is expanded in the same way as with a QName using the context 
        // namespace declarations. The node test will be true for any node 
        // of the principal type whose expanded name has the URI to which 
        // the prefix expands, regardless of the local part of the name."
        boolean isTotallyWild = (null == targetNS) && (xpath.m_opMap[opPos] == XPath.ELEMWILDCARD);
        boolean processNamespaces = execContext.getProcessNamespaces();
        boolean didMatchNS = false;
        if(!isTotallyWild && processNamespaces)
        {
          String contextNS = execContext.getNamespaceOfNode(context);
          if((null != targetNS) && (null != contextNS))
          {
            test = contextNS.equals(targetNS);
            didMatchNS = true;
          }
          else
          {
            test = (XPath.ELEMWILDCARD == queueIndex) || 
                   (((null == contextNS) || (contextNS.length() == 0)) &&
                    ((null == targetNS) || (targetNS.length() == 0)));
          }
        }
        else 
          test = true;
        
        queueIndex = xpath.m_opMap[opPos];
        String targetLocalName = (queueIndex >= 0) ? (String)xpath.m_tokenQueue[xpath.m_opMap[opPos]]
                                                     : null;
        
        if(!test)
        {
          score = XPath.MATCH_SCORE_NONE;
        }
        else
        {
          switch(nodeType)
          {
          case Node.ATTRIBUTE_NODE:
            if((stepType == XPath.FROM_ATTRIBUTES) || (stepType == XPath.FROM_NAMESPACE))
            {            
              String attrName = ((Attr)context).getNodeName();
              boolean isNamespace = (attrName.startsWith("xmlns:") || attrName.equals("xmlns"));
              if(XPath.ELEMWILDCARD == queueIndex)
              {
                if(stepType == XPath.FROM_ATTRIBUTES)
                {
                  score = !isNamespace ? XPath.MATCH_SCORE_NODETEST : XPath.MATCH_SCORE_NONE;
                }
                else
                {
                  score = isNamespace ? XPath.MATCH_SCORE_NODETEST : XPath.MATCH_SCORE_NONE;
                }
              }
              else
              {
                if(stepType == XPath.FROM_ATTRIBUTES)
                {
                  if(!isNamespace)
                  {
                    String localAttrName 
                      = execContext.getLocalNameOfNode(context);
                    score = localAttrName.equals(targetLocalName)
                            ? XPath.MATCH_SCORE_QNAME : XPath.MATCH_SCORE_NONE;
                  }
                  else
                  {
                    score = XPath.MATCH_SCORE_NONE;
                  }
                }
                else
                {
                  if(isNamespace)
                  {
                    String namespace = ((Attr)context).getValue();
                    
                    score = namespace.equals(targetLocalName)
                            ? XPath.MATCH_SCORE_QNAME : XPath.MATCH_SCORE_NONE;
                  }
                  else
                  {
                    score = XPath.MATCH_SCORE_NONE;
                  }
                }
              }
            }
            else
            {
              score  = XPath.MATCH_SCORE_NONE;
            }
            break;

          case Node.ELEMENT_NODE:
            if(stepType != XPath.FROM_ATTRIBUTES)
            {
              if(XPath.ELEMWILDCARD == queueIndex)
              {
                score = (didMatchNS ? 
                         XPath.MATCH_SCORE_NSWILD : XPath.MATCH_SCORE_NODETEST);
              }
              else
              {
                
                score = (execContext.getLocalNameOfNode(context).equals(targetLocalName))
                        ? XPath.MATCH_SCORE_QNAME : XPath.MATCH_SCORE_NONE;
              }
            }
            else
            {
              score  = XPath.MATCH_SCORE_NONE;
            }
            break;
            
          default:
            // Trying to match on anything else causes nasty bugs.
            score  = XPath.MATCH_SCORE_NONE;
            break;

          } // end switch(nodeType)
        } // end if(test)
      } // end case xpath.NODENAME
      break;
    default:
      score  = XPath.MATCH_SCORE_NONE;
    } // end switch(testType)
    
    return score;    
  }
  
  /**
   * Create an XPathFactory for this XLocator.
   */
  public static XPathFactory factory() 
  {
    return new SimpleNodeLocatorFactory();
  }

  /**
   * Very crude file filter.
   */
  class FileFilter implements FilenameFilter
  {
    private String m_filterSpec;
    
    public FileFilter(String filter)
    {
      m_filterSpec = filter;
    }
    
    /**
     * Tests if a specified file should be included in a file list.
     *
     * @param   dir    the directory in which the file was found.
     * @param   name   the name of the file.
     * @return  <code>true</code> if the name should be included in the file
     *          list; <code>false</code> otherwise.
     * @since   JDK1.0
     */
    public boolean accept(File dir, String name)
    {
      return name.endsWith(m_filterSpec);
    }
  }
  
}

/**
 * Override the createXLocatorHandler method.
 */
class DOMXPath extends XPath
{
  public DOMXPath()
  {
    super(new org.apache.xalan.xpath.xml.ProblemListenerDefault());
  }
  
  /**
   * getXLocatorHandler.
   */
  public XLocator createXLocatorHandler(XPath xpath)
  {
    return new SimpleNodeLocator();
  }
}

/**
 * Implement an XPath factory.
 */
class SimpleNodeLocatorFactory implements XPathFactory
{
  public XPath create()
  {
    return new DOMXPath();
  }
}
