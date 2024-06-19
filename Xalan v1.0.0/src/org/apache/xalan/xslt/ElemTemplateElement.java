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
import org.xml.sax.*;
import org.xml.sax.helpers.*;
import org.apache.xalan.xpath.*;
import org.apache.xalan.xpath.xml.*;
import java.util.*;
import java.io.*;
import java.net.*;
import org.apache.xalan.xslt.trace.*;
import org.apache.xalan.xslt.res.XSLTErrorResources;
import org.apache.xml.serialize.OutputFormat;
import org.apache.xml.serialize.Serializer;
import org.apache.xalan.xpath.xml.NameSpace;

/** 
 * <meta name="usage" content="advanced"/>
 * An instance of this class represents an element inside
 * an xsl:template class.  It has a single "execute" method
 * which is expected to perform the given action on the
 * result tree.
 * This class acts like a Element node, and implements the
 * Element interface, but is not a full implementation
 * of that interface... it only implements enough for
 * basic traversal of the tree.
 * 
 * @see Stylesheet
 */
public class ElemTemplateElement extends UnImplNode 
  implements PrefixResolver, Serializable, NodeCallback
{
  /** 
   * The owning stylesheet.
   * (Should this only be put on the template element, to
   * conserve space?)
   * @serial
   */
  public Stylesheet m_stylesheet;
  
  /**
   * The line number where the element occurs in the 
   * xsl file.
   * @serial
   */
  public int m_lineNumber;
  
  /**
   * The columnOffset where the element occurs in the 
   * xsl file.
   * @serial
   */
  public int m_columnNumber;
  
  /** 
   * Tell if this element has the default space handling
   * turned off or on according to the xml:space attribute.
   * @serial
   */
  public boolean m_defaultSpace = true;
  
  /** 
   * The table of namespaces that can be used in the result
   * tree.
   * @serial
   */
  protected StringToStringTable m_namespaces = null;

  /** 
   * The table of namespaces that are excluded from being 
   * used in the result tree but which need to be used 
   * in to resolve prefixes.
   * @serial
   */
  protected StringToStringTable m_excludedNamespaces = null;

  /** 
   * Tell if we've finished construction.  This is set to
   * false until the endElement is encountered.  It's mainly
   * used to tell us when we need to use the element tree
   * to resolve namespace prefixes, instead of the dynamic
   * namespace stack that is used from the stylesheet.
   * @serial
   */
  public boolean m_finishedConstruction = false;
  
  /** 
   * The name of the element.
   * @serial
   */
  public String m_elemName;
  
  /** 
   * Parent node.
   * @serial
   */
  public ElemTemplateElement m_parentNode;

  /** 
   * Next sibling.
   * @serial
   */
  ElemTemplateElement m_nextSibling;
  
  /** 
   * First child.
   * @serial
   */
  protected ElemTemplateElement m_firstChild;
  
  public String m_baseident;
  
  /** Construct a template element instance.
   * 
   * @param processor The XSLT Processor.
   * @param stylesheetTree The owning stylesheet.
   * @param name The name of the element.
   * @param atts The element attributes.
   * @param lineNumber The line in the XSLT file that the element occurs on.
   * @param columnNumber The column index in the XSLT file that the element occurs on.
   * @exception SAXException Never.
   */
  public ElemTemplateElement (XSLTEngineImpl processor,
                              Stylesheet stylesheetTree,
                              String name, 
                              AttributeList atts,
                              int lineNumber, int columnNumber)
    throws SAXException
  {
    m_lineNumber = lineNumber;
    m_columnNumber = columnNumber;
    m_stylesheet = stylesheetTree;
    if(!m_stylesheet.m_namespaces.empty())
    {
      m_namespaces = new StringToStringTable();
      int n = m_stylesheet.m_namespaces.size();
      for(int i = (n-1); i >= 0; i--)
      {
        NameSpace ns = (NameSpace)m_stylesheet.m_namespaces.elementAt(i);
        for(;null != ns; ns = ns.m_next)
        {
          if(ns == m_stylesheet.m_emptyNamespace)
            continue;
          
          if(!m_namespaces.containsValue(ns.m_uri))
          {
            if(!shouldExcludeResultNamespaceNode(this, ns.m_prefix, ns.m_uri))
            {
              m_namespaces.put(ns.m_prefix, ns.m_uri);
            }
            else
            {
              if(null == m_excludedNamespaces)
                m_excludedNamespaces = new StringToStringTable();
              m_excludedNamespaces.put(ns.m_prefix, ns.m_uri);
            }
          }
        }
      }
    }
    m_baseident = ((URL)m_stylesheet.m_includeStack.peek()).toExternalForm();
    //System.out.println("base " + m_baseident);
    
    m_elemName = name;
  }
  
  /** Read the object from the serialization stream.
   * 
   * @param stream The serialization stream.
   * @exception IOException Thrown from defaultReadObject
   */
  private void readObject(ObjectInputStream stream)
    throws IOException
  {
    // System.out.println("Reading ElemTemplateElement");
    try
    {
      stream.defaultReadObject();
    }
    catch(ClassNotFoundException cnfe)
    {
      throw new RuntimeException(XSLMessages.createMessage(XSLTErrorResources.ER_IN_ELEMTEMPLATEELEM_READOBJECT, new Object[]{cnfe.getMessage()}));//"In ElemTemplateElement.readObject: "+cnfe.getMessage());
    }
    // System.out.println("Done reading ElemTemplateElement: "+m_elemName);
  }
  
  /** Write to the serialization stream.
   * 
   * @param stream The serialization stream.
   * @exception IOException Thrown from defaultWriteObject.
   */
  private void writeObject(ObjectOutputStream stream)
    throws IOException
  {
    // System.out.println("Writing element: "+m_elemName);
    stream.defaultWriteObject();
    // System.out.println("Done writing element: "+m_elemName);
  }

  /** 
   * Given a namespace, get the corrisponding prefix.
   */
  public String getNamespaceForPrefix(String prefix, org.w3c.dom.Node context)
  {
    return getNamespaceForPrefix(prefix, this);
  }
  
  /** 
   * Given a namespace, get the corrisponding prefix.
   */
  public String getNamespaceForPrefix(String prefix)
  {
    String namespace = null;
    if(null == prefix)
      return null;
    
    if(m_finishedConstruction)
    {
      if(null != prefix)
      {
        if(prefix.equals("xml"))
        {
          namespace = Constants.S_XMLNAMESPACEURI;
        }
        else if(prefix.equals("xmlns")) // But I should really know if this is an attribute
        {
          return null;
        }
        else
        {
          namespace = m_namespaces.get(prefix);
          if((null == namespace) && (null != m_excludedNamespaces))
          {
            namespace = m_excludedNamespaces.get(prefix);
          }
            
        }
      }
    }
    else
    {
      // if(prefix.equals("xmlns")) // But I should really know if this is an attribute
      //  return null;
      
      namespace = m_stylesheet.getNamespaceForPrefixFromStack(prefix);
    }
    if(null == namespace)
	{
      error(XSLTErrorResources.ER_CANT_RESOLVE_NSPREFIX, new Object[] {prefix}); //"Can not resolve namespace prefix: "+prefix);
    }
    return namespace;
  }
  
  /**
   * Given an XSL tag name, return an integer token
   * that corresponds to ELEMNAME_XXX constants defined 
   * in Constants.java.
   * Note: I tried to optimize this by caching the node to 
   * id lookups in a hash table, but it helped not a bit.
   * I'm not sure that it's spending too much time here anyway.
   * @param node a probable xsl:xxx element.
   * @return Constants.ELEMNAME_XXX token, or -1 if in xsl 
   * or the Xalan namespace, -2 if not in known namespace.
   */
  final int getAttrTok(String name)
    throws XSLProcessorException
  {
    Integer i = (Integer)m_stylesheet.m_attributeKeys.get(name);
    return (null == i) ? -2 : i.intValue();
  }
  
  /**
   * Process the exclude-result-prefixes or the extension-element-prefixes
   * attributes, for the purpose of prefix exclusion.
   */
  protected StringToStringTable processPrefixControl(String localName, 
                                                     String attrValue, 
                                                     StringToStringTable excludeResultPrefixes)
    throws SAXException
  {                                                                                                                   
    if(localName.equals(Constants.ATTRNAME_EXTENSIONELEMENTPREFIXES))
    {
      String qnames = attrValue;
      StringTokenizer tokenizer = new StringTokenizer(qnames, " \t\n\r", false);
      String extensionElementPrefixes[] = new String[tokenizer.countTokens()];
      for(int k = 0; tokenizer.hasMoreTokens(); k++)
      {
        String eprefix = tokenizer.nextToken();
        excludeResultPrefixes 
          = m_stylesheet.processExcludeResultPrefixes(eprefix, 
                                                      excludeResultPrefixes);
      }
    }
    // process xsl:exclude-result-prefixes - Stripped from result tree
    else if(localName.equals(Constants.ATTRNAME_EXCLUDE_RESULT_PREFIXES))
    {
      excludeResultPrefixes 
        = m_stylesheet.processExcludeResultPrefixes(attrValue, 
                                                    excludeResultPrefixes);
    }
    return excludeResultPrefixes;
  }

  /** 
   * See if this is a xmlns attribute or in a non-XSLT.
   * 
   * @param attrName Qualified name of attribute.
   * @param atts The attribute list where the element comes from (not used at 
   *      this time).
   * @param which The index into the attribute list (not used at this time).
   * @return True if this attribute should not be flagged as an error.
   */
  boolean isAttrOK(String attrName, AttributeList atts, int which)
    throws SAXException
  {
    return m_stylesheet.isAttrOK(attrName, atts, which);
  }
  
  /** 
   * Tell whether or not this is a xml:space attribute and, if so, process it.
   * 
   * @param aname The name of the attribute in question.
   * @param atts The attribute list that owns the attribute.
   * @param which The index of the attribute into the attribute list.
   * @return True if this is a xml:space attribute.
   */
  void processSpaceAttr(AttributeList atts, int which)
  {
    String spaceVal = atts.getValue(which);
    if(spaceVal.equals("default"))
    {
      m_defaultSpace = true;
    }
    else if(spaceVal.equals("preserve"))
    {
      m_defaultSpace = false;
    }
    else
    {
      error(XSLTErrorResources.ER_ILLEGAL_VALUE, new Object[] {spaceVal}); //"xml:space has an illegal value: "+spaceVal);
    }
  }


  /** 
   * Tell whether or not this is a xml:space attribute and, if so, process it.
   * 
   * @param aname The name of the attribute in question.
   * @param atts The attribute list that owns the attribute.
   * @param which The index of the attribute into the attribute list.
   * @return True if this is a xml:space attribute.
   */
  boolean processSpaceAttr(String aname, AttributeList atts, int which)
  {
    boolean isSpaceAttr = aname.equals("xml:space");
    if(isSpaceAttr)
    {
      String spaceVal = atts.getValue(which);
      if(spaceVal.equals("default"))
      {
        m_defaultSpace = true;
      }
      else if(spaceVal.equals("preserve"))
      {
        m_defaultSpace = false;
      }
      else
      {
        error(XSLTErrorResources.ER_ILLEGAL_VALUE, new Object[] {spaceVal}); //"xml:space has an illegal value: "+spaceVal);
      }
    }
    return isSpaceAttr;
  }
  
  /**
   * Bottleneck addition of literal result tree attributes, so I can keep
   * track of namespaces.  This will check namespace decls for aliasing.
   */
  void addResultAttribute(Stack resultNameSpaces, 
                          MutableAttrListImpl attList, 
                          String aname, 
                          String value)
  {
    String newValue = value;              // make a copy of the value passed
    boolean isPrefix = aname.startsWith("xmlns:");
    if (isPrefix || aname.equals("xmlns"))
    {
      String p = isPrefix ? aname.substring(6) : "";
      
      // Look up alias table for an alias for this URI and return it if found 
      newValue = m_stylesheet.lookForAlias(value);
      
      ResultNameSpace ns = new ResultNameSpace(p, newValue);
      if(!resultNameSpaces.isEmpty())
      {
        ResultNameSpace nsOnStack = (ResultNameSpace)resultNameSpaces.peek();
        if(XSLTEngineImpl.m_emptyNamespace == nsOnStack)
        {
          resultNameSpaces.setElementAt(ns,
                                        resultNameSpaces.size() - 1);
        }
        else
        {
          while(nsOnStack.m_next != null)
          {
            nsOnStack = nsOnStack.m_next;
          }
          nsOnStack.m_next = ns;
        }
      }
    }

    //attList.removeAttribute(aname);
    attList.addAttribute(aname, "CDATA", newValue);
  }
  
  /** 
   * Validate that the string is an NCName.
   * 
   * @param s The name in question.
   * @return True if the string is a valid NCName according to XML rules.
   * @see http://www.w3.org/TR/REC-xml-names#NT-NCName
   */
  boolean isValidNCName(String s)
  {
    int len = s.length();
    char c = s.charAt(0);
    if(!(Character.isLetter(c) || (c == '_')))
      return false;
    if(len > 0)
    {
      for(int i = 1; i < len; i++)
      {
        c = s.charAt(i);
        if(!(Character.isLetterOrDigit(c) || (c == '_') || (c == '-') || (c == '.')))
          return false;
      }
    }
    return true;
  }
  
  /**
   * Remove any excluded prefixes from the current namespaces.
   */
  void removeExcludedPrefixes(StringToStringTable excludeResultPrefixes)
  {
    if((null != excludeResultPrefixes) && (null != m_namespaces))
    {
      int n = excludeResultPrefixes.getLength();
      for(int k = 0; k < n; k+=2)
      {
        String p = excludeResultPrefixes.elementAt(k);
        String url = m_namespaces.get(p);
        if(null != url)
        {
          if(null == m_excludedNamespaces)
            m_excludedNamespaces = new StringToStringTable();
          m_excludedNamespaces.put(p, url);
          m_namespaces.remove(p);
        }
      }
    }
  }
  
  /**
   * Tell if the result namespace decl should be excluded.  Should be called before 
   * namespace aliasing (I think).
   * TODO: I believe this contains a bug, in that included elements will check with with 
   * their including stylesheet, since in this implementation right now the included 
   * templates are merged with the including stylesheet.  The XSLT Recommendation says: "The 
   * designation of a namespace as an excluded namespace is effective within 
   * the subtree of the stylesheet rooted at the element bearing the 
   * <code>exclude-result-prefixes</code> or <code>xsl:exclude-result-prefixes</code> 
   * attribute; a subtree rooted at an <code>xsl:stylesheet</code> element
   * does not include any stylesheets imported or included by children
   * of that <code>xsl:stylesheet</code> element."
   */
  protected boolean shouldExcludeResultNamespaceNode(ElemTemplateElement elem, String prefix, String uri)
    throws SAXException
  {
    if(uri.equals(m_stylesheet.m_XSLNameSpaceURL)
       || (null != m_stylesheet.lookupExtensionNSHandler(uri))
       || uri.equals("http://xml.apache.org/xslt")
       || uri.equals("http://xsl.lotus.com/")
       || uri.equals("http://xsl.lotus.com"))
      return true; 
        
    while(null != elem)
    {
      elem = elem.m_parentNode;
      if(null == elem)
      {
        if(null != m_stylesheet.m_excludeResultPrefixes)
        {
          if(m_stylesheet.m_excludeResultPrefixes.contains(prefix))
            return true;
        }
      }
    }
    return false;
  }
    
  /*
  * Decide which namespace declarations to output
  */
  void processResultNS( XSLTEngineImpl processor) throws SAXException
  {  
    if(null == m_namespaces)
      return;
    
    int n = m_namespaces.getLength();
    for(int i = 0; i < n; i+=2)
    {
      String prefix = m_namespaces.elementAt(i);
      String srcURI = m_namespaces.elementAt(i+1);
      boolean hasPrefix = (prefix != null) && (prefix.length() > 0);
      if(!hasPrefix)
        prefix = "";
      String desturi = processor.getResultNamespaceForPrefix(prefix);
      String attrName = hasPrefix ? ("xmlns:"+prefix) : "xmlns";
      // Look for an alias for this URI. If one is found, use it as the result URI   
      String aliasURI = m_stylesheet.lookForAlias(srcURI);
      if(!aliasURI.equals(desturi)) // TODO: Check for extension namespaces
      {
        addResultAttribute(processor.m_resultNameSpaces, 
                           processor.m_pendingAttributes, 
                           attrName, srcURI);
      }
    } // end while

  }   
  
  /** Execute the element's primary function.  Subclasses of this
   * function may recursivly execute down the element tree.
   * 
   * @exception XSLProcessorException 
   * @exception java.net.MalformedURLException 
   * @exception java.io.FileNotFoundException 
   * @exception java.io.IOException 
   * @exception SAXException 
   * @param processor The XSLT Processor.
   * @param sourceTree The input source tree.
   * @param sourceNode The current context node.
   * @param mode The current mode.
   */
  public void execute(XSLTEngineImpl processor, 
                      Node sourceTree, 
                      Node sourceNode,
                      QName mode)
    throws XSLProcessorException, 
           java.net.MalformedURLException, 
           java.io.FileNotFoundException, 
           java.io.IOException,
           SAXException
  {
    if(null != m_stylesheet.m_stylesheetRoot.m_traceListeners)
    {
      m_stylesheet.m_stylesheetRoot.fireTraceEvent(new TracerEvent(processor, 
                                                                   sourceTree,
                                                                   sourceNode,
                                                                   mode,
                                                                   this));
    }
  }

  /** 
   * Process the children of a template.
   * 
   * @param processor The XSLT processor instance.
   * @param sourceTree The input source tree.
   * @param sourceNode The current context node.
   * @param mode The current mode.
   * @exception XSLProcessorException Thrown from one of the child execute 
   *     methods.
   * @exception java.net.MalformedURLException Might be thrown from the       
   *      document() function, or from xsl:include or xsl:import.
   * @exception java.io.FileNotFoundException Might be thrown from the        
   *      document() function, or from xsl:include or xsl:import.
   * @exception java.io.IOException Might be thrown from the document()       
   *      function, or from xsl:include or xsl:import.
   * @exception SAXException Might be thrown from the  document() function, or
   *      from xsl:include or xsl:import.
   */
  public void executeChildren(XSLTEngineImpl processor, 
                              Node sourceTree, 
                              Node sourceNode,
                              QName mode)
    throws XSLProcessorException, 
           java.net.MalformedURLException, 
           java.io.FileNotFoundException, 
           java.io.IOException,
           SAXException
  {
    // Check for infinite loops if we have to
    if (XSLTEngineImpl.m_recursionLimit > -1)
      processor.getStackGuard().push(sourceNode, sourceTree);
    if(null != m_firstChild)
      processor.getVarStack().pushElemFrame(this);
    try
    {
      for (ElemTemplateElement node = m_firstChild; node != null; node = node.m_nextSibling) 
      {
        node.execute(processor, sourceTree, sourceNode, mode);
      }
    }
    finally
    {
      if(null != m_firstChild)
        processor.getVarStack().popElemFrame(this);
    }
    // Check for infinite loops if we have to
    if (XSLTEngineImpl.m_recursionLimit > -1)
      processor.getStackGuard().pop();
  }
  
  /** 
   * Take the contents of a template element, process it, and
   * convert it to a string.
   * 
   * @exception XSLProcessorException Thrown from one of the child execute  
   *     methods.
   * @exception java.net.MalformedURLException Might be thrown from the       
   *      document() function, or from xsl:include or xsl:import.
   * @exception java.io.FileNotFoundException Might be thrown from the        
   *      document() function, or from xsl:include or xsl:import.
   * @exception java.io.IOException Might be thrown from the  document()      
   *      function, or from xsl:include or xsl:import.
   * @exception SAXException Might be thrown from the  document() function, or
   *      from xsl:include or xsl:import.
   * @param processor The XSLT processor instance.
   * @param sourceTree The primary source tree.
   * @param sourceNode The current source node context.
   * @param mode The current mode.
   * @return The stringized result of executing the elements children.
   */
  public String childrenToString(XSLTEngineImpl processor, 
                                 Node sourceTree, 
                                 Node sourceNode,
                                 QName mode)
    throws XSLProcessorException, 
           java.net.MalformedURLException, 
           java.io.FileNotFoundException, 
           java.io.IOException,
           SAXException
  {
    processor.m_mustFlushStartDoc = true;
    // processor.flushPending();
    DocumentHandler savedFListener = processor.m_flistener;
    StringWriter sw = new StringWriter();
    OutputFormat formatter = new OutputFormat("text", 
                                              m_stylesheet.m_stylesheetRoot.m_encoding,
                                              false);

    processor.m_flistener = m_stylesheet.m_stylesheetRoot.makeSAXSerializer(sw, formatter);
    
    boolean savedMustFlushStartDoc = processor.m_mustFlushStartDoc;
    boolean savedPendingStartDoc = processor.m_pendingStartDoc;
    String savedPendingName = processor.m_pendingElementName;
    processor.m_pendingElementName = null;
    MutableAttrListImpl savedPendingAttributes = processor.m_pendingAttributes;
    processor.m_pendingAttributes = new MutableAttrListImpl();
    
    executeChildren(processor, sourceTree, sourceNode, mode);
    
    processor.m_pendingElementName = savedPendingName;
    processor.m_pendingAttributes = savedPendingAttributes;
    processor.m_flistener = savedFListener;
    processor.m_mustFlushStartDoc = savedMustFlushStartDoc;
    processor.m_pendingStartDoc = savedPendingStartDoc;
    
    return sw.toString();
  }
  
  /** 
   * Get an integer representation of the element type.
   * 
   * @return An integer representation of the element, defined in the 
   *     Constants class.
   * @see Constants.java
   */
  public int getXSLToken()
  {
    return Constants.ELEMNAME_UNDEFINED;
  }
  
  /**
   * Get the keys for the xsl:sort elements.
   */
  private Vector processSortKeys(ElemTemplateElement xslInstruction,
                                 XSLTEngineImpl tcontext, Node sourceNodeContext)
    throws SAXException
  {
    Vector keys = null;
    int tok = xslInstruction.getXSLToken();
    if((Constants.ELEMNAME_APPLY_TEMPLATES == tok) ||
       (Constants.ELEMNAME_FOREACH == tok))
    {
      XPathSupport execContext = tcontext.getExecContext();
      ElemForEach foreach = (ElemForEach)xslInstruction;
      if(null != foreach.m_sortElems)
      {
        int nChildren = foreach.m_sortElems.size();
        keys = new Vector();
        
        // March backwards, collecting the sort keys.
        for(int i = 0; i < nChildren; i++)
        {
          ElemSort sort = (ElemSort)foreach.m_sortElems.elementAt(i);
          String langString = (null != sort.lang_avt)
                              ? sort.lang_avt.evaluate(execContext, sourceNodeContext, this, 
                                                       new StringBuffer())
                                : null;
          String dataTypeString = sort.dataType_avt.evaluate(execContext, sourceNodeContext, this, 
                                                             new StringBuffer());
          boolean treatAsNumbers = ((null != dataTypeString)&& 
                                    dataTypeString.equals(Constants.ATTRVAL_DATATYPE_NUMBER)) ? 
                                   true : false;
          String orderString = sort.order_avt.evaluate(execContext, sourceNodeContext, this, 
                                                       new StringBuffer());
          boolean descending = ((null != orderString) &&  
                                orderString.equals(Constants.ATTRVAL_ORDER_DESCENDING))? 
                               true : false;

          String caseOrderString = sort.caseOrder_avt.evaluate(execContext, sourceNodeContext, this, 
                                                               new StringBuffer());
          boolean caseOrderUpper = ((null != caseOrderString)&& 
                                    caseOrderString.equals(Constants.ATTRVAL_CASEORDER_UPPER)) ? 
                                   true : false;

          keys.addElement(new NodeSortKey(tcontext, sort.m_selectPattern, 
                                          treatAsNumbers, 
                                          descending, langString, 
                                          caseOrderUpper,xslInstruction));
        }
      }
    }
    return keys;
  }
  
  /** 
   * Perform a query if needed, and call transformChild for each child.
   * 
   * @exception XSLProcessorException Thrown if the active ProblemListener and
   *      XMLParserLiaison decide the error condition is severe enough to halt    
   *      processing.
   * @exception java.net.MalformedURLException Might be thrown from the       
   *      document() function, or from xsl:include or xsl:import.
   * @exception java.io.FileNotFoundException Might be thrown from the        
   *      document() function, or from xsl:include or xsl:import.
   * @exception java.io.IOException Might be thrown from the   document()     
   *      function, or from xsl:include or xsl:import.
   * @exception SAXException Thrown in a variety of circumstances.
   * @param stylesheetTree The owning stylesheet tree.
   * @param xslInstruction The stylesheet element context (depricated -- I do 
   *      not think we need this).
   * @param template The owning template context.
   * @param sourceTree The input source tree.
   * @param sourceNodeContext The current source node context.
   * @param mode The current mode.
   * @param selectPattern The XPath with which to perform the selection.
   * @param xslToken The current XSLT instruction (depricated -- I do not     
   *     think we want this).
   * @param tcontext The XSLTEngineImpl context.
   * @param selectStackFrameIndex The stack frame context for executing the
   *                              select statement.
   */
  protected void transformSelectedChildren(Stylesheet stylesheetTree, 
                                           ElemTemplateElement xslInstruction, // xsl:apply-templates or xsl:for-each
                                           ElemTemplateElement template, // The template to copy to the result tree
                                           Node sourceTree, 
                                           Node sourceNodeContext, QName mode, 
                                           XPath selectPattern, 
                                           int xslToken,
                                           XSLTEngineImpl tcontext,
                                           int selectStackFrameIndex)
    throws XSLProcessorException, 
           java.net.MalformedURLException, 
           java.io.FileNotFoundException, 
           java.io.IOException,
           SAXException
  {
    // Sort the nodes according to the xsl:sort method
    int tok = xslInstruction.getXSLToken();
    Vector keys = processSortKeys(xslInstruction,
                                  tcontext, sourceNodeContext);
    
    // We can only do callbacks if the node list isn't sorted.
    NodeCallback callback = (null == keys) ? this : null;

    NodeList sourceNodes = null;
    if(null != selectPattern)
    {
      XPathSupport execContext = tcontext.getXMLProcessorLiaison();
      
      int savedCurrentStackFrameIndex =tcontext.getVarStack().getCurrentStackFrameIndex();
      tcontext.getVarStack().setCurrentStackFrameIndex(selectStackFrameIndex);

      // Optimization note: is there a way we can keep from creating 
      // a new callback context every time?
      TemplateElementContext callbackContext 
        = (null != callback) 
          ? new TemplateElementContext(stylesheetTree, 
                                       xslInstruction, 
                                       template, 
                                       sourceNodeContext,
                                       mode, 
                                       xslToken, 
                                       tcontext,
                                       savedCurrentStackFrameIndex) : null;
      
      try
      {
        XObject result = selectPattern.execute(execContext, sourceNodeContext, 
                                               xslInstruction, callback, 
                                               callbackContext, false);
        sourceNodes = result.nodeset();
        if(null != m_stylesheet.m_stylesheetRoot.m_traceListeners)
        {
          m_stylesheet.m_stylesheetRoot.fireSelectedEvent(new SelectionEvent(tcontext, 
                                                                             sourceNodeContext,
                                                                             this, 
                                                                             "select",
                                                                             selectPattern,
                                                                             result));
        }
      }
      finally
      {
        tcontext.getVarStack().setCurrentStackFrameIndex(savedCurrentStackFrameIndex);
      }

      // System.out.println(sourceNodes.getLength());
    }
    else if(null != keys)
    {
      // In this case just add the children to a nodelist for sorting, as if 
      // a selection took place.
      MutableNodeListImpl msourceNodes = new MutableNodeListImpl();
      for(Node child=sourceNodeContext.getFirstChild(); null != child; child=child.getNextSibling()) 
      {
        msourceNodes.addElement(child);
      }
      sourceNodes = msourceNodes;
    }

    if(null != sourceNodes)
    {
      int nNodes = sourceNodes.getLength();
      
      if(nNodes > 0)
      {
        if(null != keys)
        {
          NodeSorter sorter = new NodeSorter(tcontext.getXMLProcessorLiaison());
          sorter.sort((NodeVector)sourceNodes, keys, tcontext.getExecContext());
        }
        
        // NodeList children = sourceNodeContext.getChildNodes(); 
        tcontext.getExecContext().pushContextNodeList( sourceNodes );
        try
        {
          if(tcontext.m_traceSelects)
            tcontext.traceSelect(xslInstruction, sourceNodes);
          
          for(int i = 0; i < nNodes; i++) 
          {
            transformChild(
                           stylesheetTree, xslInstruction, template, 
                           sourceNodeContext, sourceNodes.item(i),
                           mode, xslToken, tcontext);
          }
        }
        finally
        {
          tcontext.getExecContext().popContextNodeList();
        }
      }
    }
    else if(null == selectPattern)
    {
      if(tcontext.m_traceSelects)
        tcontext.traceSelect(xslInstruction, sourceNodes);
      Document ownerDoc = sourceNodeContext.getOwnerDocument();
      if((Node.DOCUMENT_NODE != sourceNodeContext.getNodeType()) && (null == ownerDoc))
      {
        error(XSLTErrorResources.ER_NO_OWNERDOC, null); //"Child node does not have an owner document!");
      }
      try
      {
        MutableNodeList contextNodeList = new MutableNodeListImpl();
        tcontext.getExecContext().pushContextNodeList( contextNodeList );
        for(Node childNode = sourceNodeContext.getFirstChild(); 
            null != childNode; childNode = childNode.getNextSibling()) 
        {
          contextNodeList.addNode(childNode);
          transformChild(
                         stylesheetTree, xslInstruction, template, 
                         sourceNodeContext, childNode,
                         mode, xslToken, tcontext);
        }
      }
      finally
      {
        tcontext.getExecContext().popContextNodeList();
      }
    }
  }
  
  /** 
   * Returns whether the specified <var>ch</var> conforms to the XML 1.0 definition
   * of whitespace.  Refer to <A href="http://www.w3.org/TR/1998/REC-xml-19980210#NT-S">
   * the definition of <CODE>S</CODE></A> for details.
   * 
   * @param ch Character to check as XML whitespace.
   * @return =true if <var>ch</var> is XML whitespace; otherwise =false.
   * @see http://www.w3.org/TR/1998/REC-xml-19980210#NT-S
   */
  public static boolean isSpace(char ch) 
  {
    return (ch == 0x20) || (ch == 0x09) || (ch == 0xD) || (ch == 0xA);
  }
  
  /** 
   * Tell if the string is whitespace.
   * 
   * @param string The string in question.
   * @return True if the string is pure whitespace.
   */
  public boolean isWhiteSpace(String string) 
  {
    char[] buf = string.toCharArray();
    int len = buf.length;
    
    boolean isWhiteSpace = true;
    for(int s = 0;  s < len;  s++) 
    {
      if (!isSpace(buf[s]))  
      {
        isWhiteSpace = false;
        break;
      }
    }
    return isWhiteSpace;
  }
  
  /**
   * Implementation of NodeCallback interface.  Process the 
   * node as soon as it is located by the XLocator.
   * @param execContext Execution context.
   * @param sourceNode The source node that was located.
   * @param callbackInfo Opaque info for the caller's benefit.
   */
  public void processLocatedNode(XPathSupport execContext, 
                                 Node sourceNode,
                                 Object callbackInfo)
    throws SAXException
  {
    TemplateElementContext templateContext = (TemplateElementContext)callbackInfo;
    
    VariableStack stack = templateContext.m_transformContext.getVarStack();
    int savedCurrentStackFrameIndex =stack.getCurrentStackFrameIndex();
    stack.setCurrentStackFrameIndex(templateContext.m_stackFrameIndex);
    
    try
    {      
      transformChild(templateContext.m_stylesheetTree, 
                     templateContext.m_xslInstruction, // xsl:apply-templates or xsl:for-each
                     templateContext.m_template, // may be null
                     templateContext.m_sourceNodeContext,
                     sourceNode,
                     templateContext.m_mode, 
                     templateContext.m_xslToken,
                     templateContext.m_transformContext
                     );
    }
    catch(java.net.MalformedURLException mue)
    {
      throw new XSLProcessorException(mue);
    }
    catch(java.io.FileNotFoundException fnfe)
    {
      throw new XSLProcessorException(fnfe);
    }
    catch(java.io.IOException ioe)
    {
      throw new XSLProcessorException(ioe);
    }
    finally
    {
      stack.setCurrentStackFrameIndex(savedCurrentStackFrameIndex);
    }
    /*
    catch(Exception mue)
    {
    throw new XSLProcessorException(mue);
    }
    */
  }
  
  /** 
   * Given an element and mode, find the corresponding
   * template and process the contents.
   * 
   * @param stylesheetTree The current Stylesheet object.
   * @param xslInstruction The calling element (depricated -- I dont think we 
   *      need this).
   * @param template The template to use if xsl:for-each, or null.
   * @param sourceTree The source DOM tree.
   * @param selectContext The selection context.
   * @param child The source context node.
   * @param mode The current mode, may be null.
   * @param xslToken ELEMNAME_APPLY_TEMPLATES, ELEMNAME_APPLY_IMPORTS, or     
   *      ELEMNAME_FOREACH.
   * @exception XSLProcessorException thrown if the active ProblemListener and
   *      XMLParserLiaison decide  the error condition is severe enough to halt   
   *      processing.
   * @exception java.net.MalformedURLException 
   * @exception java.io.FileNotFoundException 
   * @exception java.io.IOException 
   * @exception SAXException 
   * @return true if applied a template, false if not.
   */
  boolean transformChild(Stylesheet stylesheetTree, 
                         ElemTemplateElement xslInstruction, // xsl:apply-templates or xsl:for-each
                         ElemTemplateElement template, // may be null
                         Node selectContext,
                         Node child,
                         QName mode, int xslToken,
                         XSLTEngineImpl transformContext
                         )
    throws XSLProcessorException, 
           java.net.MalformedURLException, 
           java.io.FileNotFoundException, 
           java.io.IOException,
           SAXException
  {    
    int nodeType = child.getNodeType();
    Node sourceTree = (Node.DOCUMENT_NODE == nodeType) ? child : child.getOwnerDocument();
    
    if(null == template)
    {
      boolean isApplyImports = (xslToken == Constants.ELEMNAME_APPLY_IMPORTS);
      if(!isApplyImports)
        stylesheetTree = m_stylesheet.m_stylesheetRoot;

      // Find the XSL template that is the best match for the 
      // element.        
      template = stylesheetTree.findTemplate(transformContext, sourceTree, child, mode,
                                             isApplyImports);
      if(null == template)
      {
        switch(nodeType)
        {
        case Node.DOCUMENT_FRAGMENT_NODE:
        case Node.ELEMENT_NODE:
          template = m_stylesheet.m_stylesheetRoot.m_defaultRule;
          break;
        case Node.CDATA_SECTION_NODE:
        case Node.TEXT_NODE:
        case Node.ATTRIBUTE_NODE:
          template = m_stylesheet.m_stylesheetRoot.m_defaultTextRule;
          break;
        case Node.DOCUMENT_NODE:
          template = m_stylesheet.m_stylesheetRoot.m_defaultRootRule;
          break;
        }   
      }
    }
    
    try
    {
      if(null != template)
      {
        transformContext.resetCurrentState(child);
        
        if(template == m_stylesheet.m_stylesheetRoot.m_defaultTextRule)
        {
          switch(nodeType)
          {
          case Node.CDATA_SECTION_NODE:
          case Node.TEXT_NODE:
            transformContext.cloneToResultTree(stylesheetTree, child, false, false, false);
            break;
          case Node.ATTRIBUTE_NODE:
            {
              Attr attr = (Attr)child;
              String val = attr.getValue();
              transformContext.m_resultTreeHandler.characters(val.toCharArray(), 0, val.length());
            }
            break;
          }
        }
        else
        {
          if(null != m_stylesheet.m_stylesheetRoot.m_traceListeners)
          {
            TracerEvent te = new TracerEvent(transformContext, 
                                             sourceTree,
                                             child,
                                             mode,
                                             template);
            m_stylesheet.m_stylesheetRoot.fireTraceEvent(te);
          }
          template.executeChildren(transformContext, sourceTree, child, mode);
        }
      }
    }
    finally
    {
      transformContext.resetCurrentState(selectContext);
    }
    return true;
  }
  
  /** 
   * Throw a template element runtime error.  (Note: should we throw a SAXException instead?)
   * 
   * @param msg Description of the error that occured.
   */
  public void error(int msg, Object[] args)
  {
    String themsg = XSLMessages.createMessage(msg, args);  
    throw new RuntimeException(XSLMessages.createMessage(XSLTErrorResources.ER_ELEMTEMPLATEELEM_ERR, new Object[] {themsg})); //"ElemTemplateElement error: "+msg);
  }
  
  // Implemented DOM Element methods.
  
  /** 
   * Add a child to the child list.
   * 
   * @exception DOMException 
   * @param newChild 
   */
  public Node               appendChild(Node newChild)
    throws DOMException
  {
    if(null == newChild)
    {
      error(XSLTErrorResources.ER_NULL_CHILD, null); //"Trying to add a null child!");
    }
    ElemTemplateElement elem = (ElemTemplateElement)newChild;
    if(null == m_firstChild)
    {
      m_firstChild = elem;
    }
    else
    {
      ElemTemplateElement last = (ElemTemplateElement)getLastChild();
      last.m_nextSibling = elem;
    }
    elem.m_parentNode = this;
    
    // Do exclusion of result attributes
    for(ElemTemplateElement parent = this; parent != null; parent = parent.m_parentNode)
    {
      int tok = parent.getXSLToken();
      if((tok == Constants.ELEMNAME_LITERALRESULT) || (tok == Constants.ELEMNAME_EXTENSIONCALL))
      {
        elem.removeExcludedPrefixes(((ElemLiteralResult)parent).m_excludeResultPrefixes);
      }
    }

    return newChild;
  }
  
  /** 
   * Tell if there are child nodes.
   */
  public boolean            hasChildNodes()
  {
    return (null != m_firstChild);
  }
  
  /** 
   * Get the type of the node.
   */
  public short              getNodeType()
  {
    return Node.ELEMENT_NODE;
  }
  
  /** Get the parent.
   */
  public Node               getParentNode()
  {
    return m_parentNode;
  }
  
  /** Return the nodelist (same reference).
   */
  public NodeList           getChildNodes()
  {
    return this;
  }
  
  /** Get the first child
   */
  public Node               getFirstChild()
  {
    return m_firstChild;
  }
  
  /** Get the last child.
   */
  public Node               getLastChild()
  {
    ElemTemplateElement lastChild = null;
    for (ElemTemplateElement node = m_firstChild; 
         node != null; node = node.m_nextSibling) 
    {
      lastChild = node;
    }
    return lastChild;
  }
  
  /** Get the next sibling or return null.
   */
  public Node               getNextSibling()
  {
    return m_nextSibling;
  }
  
  /** 
   * NodeList method: Count the immediate children of this node
   * 
   * @return int
   */
  public int getLength() 
  {

    // It is assumed that the getChildNodes call synchronized
    // the children. Therefore, we can access the first child
    // reference directly.
    int count = 0;
    for (ElemTemplateElement node = m_firstChild; node != null; node = node.m_nextSibling) 
    {
      count++;
    }
    return count;

  } // getLength():int
  
  /** 
   * NodeList method: Return the Nth immediate child of this node, or
   * null if the index is out of bounds.
   * 
   * @param index 
   * @return org.w3c.dom.Node
   */
  public Node item(int index) 
  {
    // It is assumed that the getChildNodes call synchronized
    // the children. Therefore, we can access the first child
    // reference directly.
    ElemTemplateElement node = m_firstChild;
    for (int i = 0; i < index && node != null; i++) 
    {
      node = node.m_nextSibling;
    }
    return node;

  } // item(int):Node
  
  /** Get the stylesheet owner.
   */
  public Document           getOwnerDocument()
  {
    return m_stylesheet;
  }
  
  /** Return the element name.
   */
  public String getTagName()
  {
    return m_elemName;
  }
  
  /** Return the node name.
   */
  public String getNodeName()
  {
    return m_elemName;
  }
  
  /** Return the base identifier.
   */
  public String getBaseIdentifier()
  {
    return m_baseident;
  }
    
}
