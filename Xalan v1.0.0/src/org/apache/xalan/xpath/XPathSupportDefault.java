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
import org.w3c.dom.*;
import org.apache.xalan.xpath.xml.QName;
import org.apache.xalan.xpath.xml.PrefixResolver;
import java.net.URL;
import org.xml.sax.SAXException;
import org.xml.sax.DocumentHandler;
import org.apache.xalan.xpath.xml.IntStack;
import org.apache.xalan.xpath.xml.XSLMessages;
import org.apache.xalan.xpath.res.XPATHErrorResources;


/**
 * <meta name="usage" content="advanced"/>
 * Default class for execution context when XPath is used by itself. Many 
 * of the functions in this class need to be overridden in order to 
 * perform correct execution of the XPath (for instance, variable 
 * execution).  This class will likely eventually replace XMLParserLiaisons.
 */
public class XPathSupportDefault implements XPathSupport
{
  /**
   * Keep stack of positions within the current context list.
   */
  private IntStack m_contextCounts = new IntStack();
  
  /**
   * The current context node list.
   */
  private Stack m_contextNodeLists = new Stack();

  /**
   * The current xpath state.  Note... it would be 
   * a good optimization to make our own class and 
   * pre-allocate the XPathContext objects.
   */
  private Stack m_xpathContextStates = new Stack();
  
  /**
   * Class to stort the state of the XPath for re-execution.
   */
  class XPathContext
  {
    XPath xpath;
    XPathSupport execContext;
    Node contextNode;
    PrefixResolver namespaceContext;
    int contextNodePosition;
    NodeList contextNodeList;
    XObject result = null;
                                                              
    XPathContext(XPath xpath, XPathSupport execContext, Node contextNode, 
                         PrefixResolver namespaceContext)
    {
      this.xpath = xpath;
      this.execContext = execContext;
      this.namespaceContext = namespaceContext;
      this.contextNode = contextNode;
      if(m_contextCounts.empty())
      {
        contextNodePosition = -200;
        contextNodeList = null;
      }
      else
      {
        contextNodePosition = getContextNodePosition();
        contextNodeList = getContextNodeList();
      }
    }
    
    XObject execute()
      throws SAXException
    {
      if(null != result)
        return result;
      
      m_contextNodeLists.push(contextNodeList);
      m_contextCounts.push(contextNodePosition);
      try
      {
        result = xpath.execute(execContext, contextNode, 
                              namespaceContext);
      }
      finally
      {
        m_contextNodeLists.pop();
        m_contextCounts.pop();
      }
      return result;
      
    }
    
  }
  
  private XPathContext m_dummyXPathContext = new XPathContext(null, null, null, null);

  /**
   * <meta name="usage" content="experimental"/>
   * Push the current XPath selection,
   * needed for support of the last() function in depth-first 
   * execution.  If the last() function is called, the originating 
   * XPath will be executed to get a count.  This is ugly, and later 
   * some sort of system can be done where the XPath can finish evaluating 
   * from the current point.
   */
  public void pushXPathContext(XPath xpath, XPathSupport execContext, Node contextNode, 
                         PrefixResolver namespaceContext)
  {
    XPathContext xpathContext 
      = new XPathContext(xpath, execContext, contextNode, 
                         namespaceContext);
    m_xpathContextStates.push(xpathContext);
  }
  
  /**
   * <meta name="usage" content="experimental"/>
   * Pop the current XPathContext.
   */
  public void popXPathContext()
  {
    m_xpathContextStates.pop();
  }
  
  /**
   * Push a dummy XPathContext so we can tell that the top-level xpath isn't 
   * in effect.
   */
  public void pushDummyXPathContext()
  {
    m_xpathContextStates.push(m_dummyXPathContext);
  }

  
  /**
   * <meta name="usage" content="experimental"/>
   * Reexecute the last xpath context after the specified one.
   */
  public XObject reExecuteXPathContext(XPath path, XPathSupport execContext, Node context)
    throws SAXException
  {
    int size = m_xpathContextStates.size();
    for(int i = (size-1); i >= 0; i--)
    {
      XPathContext xpathContext = (XPathContext)m_xpathContextStates.elementAt(i);
      if(null == xpathContext.xpath)
        return null;
      
      if((xpathContext.contextNode == context) 
         && (xpathContext.xpath == path)
         && (xpathContext.execContext == context))
        continue;
      
      return xpathContext.execute();
    }
    
    return null;
  }
  
  /**
   * <meta name="usage" content="experimental"/>
   * Get the current position in the context node list.  Used for 
   * depth-first searching.
   */
  public int getContextNodePosition()
  {
    if(!m_contextCounts.empty())
      return m_contextCounts.peek();
    else
      return -200;
  }
  
  /**
   * <meta name="usage" content="experimental"/>
   * Increment the current context node position.
   */
  public void incrementContextNodePosition(Node node)
  {
    int newNodePos = getContextNodePosition()+1;
    m_contextCounts.setTop(newNodePos);
  }
 
  /**
   * <meta name="usage" content="experimental"/>
   * Decrement the current context node position.
   */
  public void decrementContextNodePosition()
  {
    m_contextCounts.setTop(getContextNodePosition()-1);
  }

  /**
   * Push the current context node position.
   */
  public void pushContextNodePosition()
  {
    m_contextNodeLists.push(null);
    m_contextCounts.push(0);
  }

  /**
   * <meta name="usage" content="experimental"/>
   * Pop the current context node position.
   */
  public void popContextNodePosition()
  {
    m_contextNodeLists.pop();
    m_contextCounts.pop();
  }
  
  /**
   * Get the current context node list.
   */
  public NodeList getContextNodeList()
  {
    if (m_contextNodeLists.size()>0)
      return (NodeList)m_contextNodeLists.peek();
    else 
      return null;
  }
  
  /**
   * <meta name="usage" content="internal"/>
   * Set the current context node list.
   * @param A nodelist that represents the current context 
   * list as defined by XPath.
   */
  public void pushContextNodeList(NodeList nl)
  {
    m_contextCounts.push(-200);
    m_contextNodeLists.push(nl);
  }

  /**
   * <meta name="usage" content="internal"/>
   * Pop the current context node list.
   */
  public void popContextNodeList()
  {
    m_contextCounts.pop();
    m_contextNodeLists.pop();
  }

  /**
   * Tells if FoundIndex should be thrown if index is found.
   * This is an optimization for match patterns.
   */
  private boolean m_throwFoundIndex = false;
  
  /**
   * <meta name="usage" content="internal"/>
   * ThrowFoundIndex tells if FoundIndex should be thrown
   * if index is found.
   * This is an optimization for match patterns, and
   * is used internally by the XPath engine.
   */
  public boolean getThrowFoundIndex()
  {
    return m_throwFoundIndex;
  }

  /**
   * <meta name="usage" content="internal"/>
   * ThrowFoundIndex tells if FoundIndex should be thrown
   * if index is found.
   * This is an optimization for match patterns, and
   * is used internally by the XPath engine.
   */
  public void setThrowFoundIndex(boolean b)
  {
    m_throwFoundIndex = b;
  }
  
  /**
   * The current node.
   */
  private Node m_currentNode = null;
  
  /**
   * The current prefixResolver for the execution context (not
   * the source tree context).
   * (Is this really needed?)
   */
  private PrefixResolver m_currentPrefixResolver = null;
  
  /**
   * Get the current context node.
   */
  public Node getCurrentNode()
  {
    return m_currentNode;
  }

  /**
   * Set the current context node.
   */
  public void setCurrentNode(Node n)
  {
    m_currentNode = n;
  }
  
  /**
   * Get the current namespace context for the xpath.
   */
  public PrefixResolver getNamespaceContext()
  {
    return m_currentPrefixResolver;
  }

  /**
   * Get the current namespace context for the xpath.
   */
  public void setNamespaceContext(PrefixResolver pr)
  {
    m_currentPrefixResolver = pr;
  }
  
  /**
   * Given a namespace, get the corresponding prefix.
   * @param prefix A namespace prefix that is valid in the namespaceContext.
   * @param namespaceContext An element from which to evaluate the prefix resolution.
   * @return A namespace, or null if it can't be resolved.
   */
  public String getNamespaceForPrefix(String prefix, 
                                      Element namespaceContext)
  {
    return (null != m_currentPrefixResolver) 
           ? m_currentPrefixResolver.getNamespaceForPrefix(prefix, namespaceContext) 
             : prefix;
  }
  
  /**
   * Returns the namespace of the given node.
   * @param n The node in question.
   * @return A namespace, or null if there is none.
   */
  public String getNamespaceOfNode(Node n)
  {
    String nodeName = n.getNodeName();
    int indexOfNSSep = nodeName.indexOf(':');
    String prefix;
    int ntype = n.getNodeType();

    if(Node.ATTRIBUTE_NODE == ntype)
    {
      if(indexOfNSSep > 0)
      {
        prefix = nodeName.substring(0, indexOfNSSep);
      }
      else
      {
        // Attributes don't use the default namespace, so if 
        // there isn't a prefix, we're done.
        return null;
      }
    }
    else
    {
      prefix = (indexOfNSSep >= 0) ? nodeName.substring(0, indexOfNSSep) : "";
    }
    return (null != m_currentPrefixResolver) 
           ? m_currentPrefixResolver.getNamespaceForPrefix(prefix, n) 
             : prefix;
  }
  
  /**
   * Returns the local name of the given node.
   * @param n The node in question.
   * @return The local name of the node, or null if the node doesn't have a name.
   */
  public String getLocalNameOfNode(Node n)
  {
    String qname = n.getNodeName();
    int index = qname.indexOf(':');
    return (index < 0) ? qname : qname.substring(index+1);
  }
  
  /**
   * This function has to be implemented,
   * because the DOM WG decided that attributes don't
   * have parents.
   * @param n The node in question.
   * @return The "owner" of the node.
   */
  public Node getParentOfNode(Node node)
  {
    return node.getParentNode();
  }
  
  /**
   * Variables don't work when executing an XPath by itself.
   * @return XString that is an error message.
   */
  public XObject getVariable(QName name)
    throws org.xml.sax.SAXException
  {
    return new XString( "Unknown variable: name");
  }
  
  /**
   * Tell if the node is ignorable whitespace.
   * This should be in the DOM.  Return false if the
   * parser doesn't handle this.
   * @deprecated
   */
  public boolean isIgnorableWhitespace(Text node)
  {
    return false;
  }
  
  /**
   * Given a valid element key, return the corresponding node list.
   * @return null, derived element must override.
   */
  public NodeList getNodeSetByKey(Node doc, String name, 
                                  String ref, 
                                  org.apache.xalan.xpath.xml.PrefixResolver nscontext)
    throws org.xml.sax.SAXException
  {
    return null;
  }
  
  /**
   * Get table of source tree documents.
   * Document objects are keyed by URL string.
   * @return null, derived element must override.
   */
  public Hashtable getSourceDocsTable()
  {
    return null;
  }
  
  /**
   * Given a DOM Document, tell what URI was used to parse it.
   * @return string "unknown", derived element must override.
   */
  public String findURIFromDoc(Document owner)
  {
    return "unknown";
  }
  
  /**
   * Take given a URL, try and parse XML.
   * the error condition is severe enough to halt processing.
   * @deprecated
   */
  public Document parseXML(String urlString, String base)
  {
    return null;
  }
  
  /**
   * Get an element from an ID.
   * @return null, derived element must override.
   */
  public Element getElementByID(String id, Document doc)
  {
    return null;
  }

  /**
   * The getUnparsedEntityURI function returns the URI of the unparsed 
   * entity with the specified name in the same document as the context 
   * node (see [3.3 Unparsed Entities]). It returns the empty string if 
   * there is no such entity.
   * @return empty string, derived element must override.
   */
  public String getUnparsedEntityURI(String name, Document doc)
  {
    return "";
  }
  
  /**
   * Get the factory object required to create DOM nodes 
   * in the result tree.
   */
  public void setDOMFactory(Document domFactory)
  {
    throw new RuntimeException(XSLMessages.createXPATHMessage(XPATHErrorResources.ER_SETDOMFACTORY_NOT_SUPPORTED, null)); //"setDOMFactory is not supported by XPathSupportDefault!");
  }

  /**
   * Get a DOM document, primarily for creating result
   * tree fragments.
   */
  public Document getDOMFactory()
  {
    return null;
  }

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
  public boolean problem(short where, short classification, 
                         Node styleNode, Node sourceNode,
                         String msg, int lineNo, int charOffset)
  {
    System.out.println(msg);
    return true;
  }
  
  /**
   * Execute the function-available() function.
   */
  public boolean functionAvailable(String namespace, 
                            String extensionName)
  {
    return false;
  }

  /**
   * Execute the element-available() function.
   */
  public boolean elementAvailable(String namespace, 
                            String extensionName)
  {
    return false;
  }

  /**
   * Handle an extension function.
   */
  public Object extFunction(String namespace, String extensionName, 
                                Vector argVec, Object methodKey)
    throws org.xml.sax.SAXException
  {
    return null;
  }
  
  /**
   * Get the first unparented node in the ancestor chain.
   */
  public Node getRoot(Node node)
  {
    Node root = null;
    while(node != null)
    {
      root = node;
      node = getParentOfNode(node);
    }
    return root;
  }
  
  /**
   * Associate an XLocator provider to a node.  This makes
   * the association based on the root of the tree that the 
   * node is parented by.
   */
  public void associateXLocatorToNode(Node node, XLocator xlocator)
  {
  }

  /**
   * Get an XLocator provider keyed by node.  This get's
   * the association based on the root of the tree that the 
   * node is parented by.
   */
  public XLocator getXLocatorFromNode(Node node)
  {
    return SimpleNodeLocator.getDefaultLocator();
  }
  
  /**
   * Tells if namespaces should be supported.  For optimization purposes.
   */
  boolean m_processNamespaces;
  
  /**
   * Set whether or not the liaison attempts to expand namespaces.  Used 
   * for optimization.
   */
  public void setProcessNamespaces(boolean processNamespaces)
  {
    m_processNamespaces = processNamespaces;
  }
  
  /**
   * Tells if namespaces should be supported.  For optimization purposes.
   */
  public boolean getProcessNamespaces()
  {
    return m_processNamespaces;
  }

  /**
   * Tells, through the combination of the default-space attribute 
   * on xsl:stylesheet, xsl:strip-space, xsl:preserve-space, and the
   * xml:space attribute, whether or not extra whitespace should be stripped 
   * from the node.  Literal elements from template elements should 
   * <em>not</em> be tested with this function.
   * @param textNode A text node from the source tree.
   * @return true if the text node should be stripped of extra whitespace.
   */
  public boolean shouldStripSourceNode(Node textNode)
    throws org.xml.sax.SAXException
  {
    return false;
  }

  /**
   * Take a user string and try and parse XML, and also return 
   * the url.
   * the error condition is severe enough to halt processing.
   */
  public Document parseXML(URL url, 
                           DocumentHandler docHandler, 
                           Document styleDoc)
    throws SAXException
  {
    return null;
  }
  
  /**
   * Take a user string and try and parse XML, and also return 
   * the url.
   * @exception XSLProcessorException thrown if the active ProblemListener and XMLParserLiaison decide 
   * the error condition is severe enough to halt processing.
   */
  public URL getURLFromString(String urlString, String base)
    throws SAXException
  {
    return null;
  }
  
  /**
   * Register an extension namespace handler. This handler provides
   * functions for testing whether a function is known within the 
   * namespace and also for invoking the functions.
   *
   * @param uri the URI for the extension.
   * @param extNS the extension handler.
   */
  public void addExtensionNamespace (String uri,
         ExtensionFunctionHandler extNS)
  {
  }
  
  /**
   * Callback that may be executed when a node is found, if the 
   * XPath query can be done in document order.
   * The callback will be set to null after the next LocationPath or 
   * Union is processed.
   */
  private NodeCallback m_callback = null;
  
  /**
   * Object that will be passed to the processLocatedNode method.
   * The object will be set to null after the next LocationPath or 
   * Union is processed.
   */
  private Object m_callbackInfo = null;

  /**
   * Set a callback that may be called by XPath as nodes are located.
   * The callback will only be called if the XLocator determines that 
   * the location path can process the nodes in document order.
   * If the callback is called, the nodes will not be put into the 
   * node list, and the LocationPath will return an empty node list.
   * The callback will be set to null after the next LocationPath or 
   * Union is processed.
   * @param callback Interface that implements the processLocatedNode method.
   * @param callbackInfo Object that will be passed to the processLocatedNode method.
   */
  public void setCallback(NodeCallback callback, Object callbackInfo)
  {
    m_callback = callback;
    m_callbackInfo = callbackInfo;
  }
  
  /**
   * Get the callback that may be called by XPath as nodes are located.
   * @return the current callback method.
   */
  public NodeCallback getCallback()
  {
    return m_callback;
  }

  /**
   * Get the object that will be passed to the processLocatedNode method.
   * @return object that will be passed to the processLocatedNode method.
   */
  public Object getCallbackInfo()
  {
    return m_callbackInfo;
  }
  
  /**
   * Get the current error handler, if there is one.
   */
  public org.xml.sax.ErrorHandler getErrorHandler()
  {
    return null;
  }
  
  /**
   * getXLocatorHandler.
   */
  public XLocator createXLocatorHandler()
  {
    return new SimpleNodeLocator();
  }

}
