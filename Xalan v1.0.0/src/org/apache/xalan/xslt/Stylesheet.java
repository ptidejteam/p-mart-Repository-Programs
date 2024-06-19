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

import org.apache.xalan.xpath.xml.*;
import org.apache.xalan.xpath.*;
import org.w3c.dom.*;
import java.util.*;
import java.text.DecimalFormatSymbols;
import java.net.*;
import java.io.*;
import org.xml.sax.*;
import org.xml.sax.helpers.*;
import org.apache.xalan.xslt.res.XSLTErrorResources;

/**
 * Represents the base stylesheet or an
 * "import" stylesheet; "include" stylesheets are
 * combined with the including stylesheet.
 * At the moment these stylesheets can not be reused within the
 * stylesheet tree or between trees.  This needs to be fixed
 * in the future.
 */
public class Stylesheet  extends UnImplNode
  implements java.io.Serializable, Document, PrefixResolver
{
  /**
   * The full XSLT Namespace URL.  To be replaced by the one actually
   * found.
   */
  String m_XSLNameSpaceURL = "http://www.w3.org/1999/XSL/Transform";

  /**
   * A vector of the -imported- XSL Stylesheets.
   * @serial
   */
  Vector m_imports = new Vector();

  /**
   * Get vector of the -imported- XSL Stylesheets.
   */
  public Vector getImports()
  {
    return m_imports;
  }

  /**
   * Set vector of the -imported- XSL Stylesheets.
   */
  public void setImports(Vector v)
  {
    m_imports = v;
  }

  /**
   * Table of attribute sets, keyed by set name.
   * @serial
   */
  private Vector m_attributeSets = null;

  /**
   * Set table of attribute sets, keyed by set name.
   */
  public Vector getAttributeSets()
  {
    return m_attributeSets;
  }

  /**
   * Get table of attribute sets, keyed by set name.
   */
  public void setAttributeSets(Vector v)
  {
    m_attributeSets = v;
  }

  /**
   * The base URL of the XSL document.
   * @serial
   */
  String m_baseIdent = null;

  /**
   * Get the base identifier with which this stylesheet is associated.
   */
  public String getBaseIdentifier()
  {
    return m_baseIdent;
  }

  /**
   * Get the base identifier with which this stylesheet is associated.
   */
  public void setBaseIdentifier(String baseIdent)
  {
    m_baseIdent = baseIdent;
  }

  /**
   * Tells if the stylesheet tables need to be rebuilt.
   * @serial
   */
  boolean m_tablesAreInvalid = true;

  /**
   * A dummy value for use in hash tables that have no use for the value.
   * @serial
   */
  static Boolean dummyVal = new Boolean(true);

  /**
   * The root of the stylesheet, where all the tables common
   * to all stylesheets are kept.
   * @serial
   */
  public StylesheetRoot m_stylesheetRoot;

  /**
   * Get the root of the stylesheet, where all the tables common
   * to all stylesheets are kept.
   */
  public StylesheetRoot getStylesheetRoot()
  {
    return m_stylesheetRoot;
  }

  /**
   * Set the root of the stylesheet, where all the tables common
   * to all stylesheets are kept.
   */
  public void setStylesheetRoot(StylesheetRoot v)
  {
    m_stylesheetRoot = v;
  }

  /**
   * The parent of the stylesheet.  This will be null if this
   * is the root stylesheet.
   * @serial
   */
  public Stylesheet m_stylesheetParent;

  /**
   * Get the parent of the stylesheet.  This will be null if this
   * is the root stylesheet.
   */
  public Stylesheet getStylesheetParent()
  {
    return m_stylesheetParent;
  }

  /**
   * Set the parent of the stylesheet.  This should be null if this
   * is the root stylesheet.
   */
  public void setStylesheetParent(Stylesheet v)
  {
    m_stylesheetParent = v;
  }

  /**
   * The table of extension namespaces.
   * @serial
   */
  Hashtable m_extensionNamespaces = new Hashtable();

  /**
   * The list of templates, and findTemplate support.
   */
  private TemplateList m_templateList;

  /**
   * Get the list of templates.
   */
  public TemplateList getTemplateList()
  {
    return m_templateList;
  }

  /**
   * Set the list of templates.
   */
  public void setTemplateList(TemplateList v)
  {
    m_templateList = v;
  }

  /**
   * A stack of who's including who is needed in order to support
   * "It is an error if a stylesheet directly or indirectly
   * includes itself."
   * @serial
   */
  transient Stack m_includeStack;

  /**
   * Tell if this stylesheet has the default space handling
   * turned off or on according to the xml:space attribute.
   * @serial
   */
  boolean m_defaultSpace = true;

  /**
   * Get if this stylesheet has the default space handling
   * turned off or on according to the xml:space attribute.
   */
  public boolean getDefaultSpace()
  {
    return m_defaultSpace;
  }

  /**
   * Set if this stylesheet has the default space handling
   * turned off or on according to the xml:space attribute.
   */
  public void setDefaultSpace(boolean v)
  {
    m_defaultSpace = v;
  }

  /**
   * A lookup table of all space preserving elements.
   * @serial
   */
  Vector m_whitespacePreservingElements = null;

  /**
   * Get lookup table of all space preserving elements.
   */
  public Vector getWhitespacePreservingElements()
  {
    return m_whitespacePreservingElements;
  }

  /**
   * Set lookup table of all space preserving elements.
   */
  public void setWhitespacePreservingElements(Vector v)
  {
    m_whitespacePreservingElements = v;
  }

  /**
   * A lookup table of all space stripping elements.
   * @serial
   */
  Vector m_whitespaceStrippingElements = null;

  /**
   * Set lookup table of all space stripping elements.
   */
  public Vector getWhitespaceStrippingElements()
  {
    return m_whitespaceStrippingElements;
  }

  /**
   * Set lookup table of all space stripping elements.
   */
  public void setWhitespaceStrippingElements(Vector v)
  {
    m_whitespaceStrippingElements = v;
  }

  /**
   * A lookup table of exclude-result-prefixes.
   */
  StringToStringTable m_excludeResultPrefixes = null;

  /**
   * Set lookup table of exclude-result-prefixes.
   */
  public StringToStringTable getExcludeResultPrefixes()
  {
    return m_excludeResultPrefixes;
  }

  /**
   * Get lookup table of exclude-result-prefixes.
   */
  public void setExcludeResultPrefixes(StringToStringTable v)
  {
    m_excludeResultPrefixes = v;
  }

  /**
   * Process the exclude-result-prefixes attribute or xsl:exclude-result-prefixes
   * attribute.
   */
  StringToStringTable processExcludeResultPrefixes(String val,
                                                   StringToStringTable excludeResultPrefixes)
    throws SAXException
  {
    if(null == excludeResultPrefixes)
    {
      excludeResultPrefixes = new StringToStringTable();
    }
    StringTokenizer tokenizer = new StringTokenizer(val, " \t\n\r", false);
    while(tokenizer.hasMoreTokens ())
    {
      String prefix = tokenizer.nextToken ();
      if(prefix.equalsIgnoreCase("#default"))
        prefix="";
      String ns = getNamespaceForPrefixFromStack (prefix);
      if(null == ns)
        throw new SAXException(XSLMessages.createMessage(XSLTErrorResources.ER_INVALID_PREFIX_IN_EXCLUDERESULTPREFIX, new Object[]{prefix}));
      excludeResultPrefixes.put(prefix, ns);
    }
    return excludeResultPrefixes;
  }

  /**
   * Table for defined constants, keyed on the names.
   * @serial
   */
  Vector m_topLevelVariables = new Vector();

  /**
   * Get table for defined constants, keyed on the names.
   */
  public Vector getTopLevelVariables()
  {
    return m_topLevelVariables;
  }

  /**
   * Set table for defined constants, keyed on the names.
   */
  public void setTopLevelVariables(Vector v)
  {
    m_topLevelVariables = v;
  }

  /**
   * Stack used for namespaces as the stylesheet is being parsed.
   * @serial
   */
  transient Stack m_namespaces = new Stack();

  /**
   * The start of a linked list of namespace declarations,
   * for mapping from prefix to namespace URI.
   * @serial
   */
  NameSpace m_namespaceDecls = null;

  /* A table of aliases, where the key is the stylesheet uri and the
   * value is the result uri
   */
  public StringToStringTable m_prefix_aliases = null;

  /**
   * This is pushed on the m_resultNameSpaces stack 'till a
   * xmlns attribute is found.
   * @serial
   */
  final org.apache.xalan.xpath.xml.NameSpace m_emptyNamespace
    = new org.apache.xalan.xpath.xml.NameSpace(null, null);

  /**
   * Table of KeyDeclaration objects, which are set by the
   * xsl:key element.
   * @serial
   */
  Vector m_keyDeclarations = new Vector();

  /**
   * Get table of KeyDeclaration objects, which are set by the
   * xsl:key element.
   */
  public Vector getKeyDeclarations()
  {
    return m_keyDeclarations;
  }

  /**
   * Set table of KeyDeclaration objects, which are set by the
   * xsl:key element.
   */
  public void setKeyDeclarations(Vector v)
  {
    m_keyDeclarations = v;
  }

  /**
   * This is set to true if an xsl:key directive is found.
   * Mainly for use by the XMLParserLiaison classes for
   * optimized processing of ids.
   * @serial
   */
  boolean m_needToBuildKeysTable = false;

  /**
   * Get if an xsl:key directive is found.
   * Mainly for use by the XMLParserLiaison classes for
   * optimized processing of ids.
   */
  public boolean getNeedToBuildKeysTable()
  {
    return m_needToBuildKeysTable;
  }

  /**
   * Set if an xsl:key directive is found.
   * Mainly for use by the XMLParserLiaison classes for
   * optimized processing of ids.
   */
  public void setNeedToBuildKeysTable(boolean v)
  {
    m_needToBuildKeysTable = v;
  }

  /**
   * Extension to be used when serializing to disk.
   */
  public static final String STYLESHEET_EXT = ".lxc";

  /**
   * The default template to use for xsl:apply-templates when
   * a select attribute is not found.
   */
  XPath m_defaultATXpath = null;

  /**
   * The version of XSL that was declared.
   */
  double m_XSLTVerDeclared = 1.0;

  /**
   * Table of tables of element decimal-format.
   * @see ElemDecimalFormat.
   */
  Stack m_DecimalFormatDeclarations = new Stack();

  /**
   * Get table of tables of element decimal-format.
   * @see ElemDecimalFormat.
   */
  public Stack getDecimalFormatDeclarations()
  {
    return m_DecimalFormatDeclarations;
  }

  /**
   * Set table of tables of element decimal-format.
   * @see ElemDecimalFormat.
   */
  public void setDecimalFormatDeclarations(Stack v)
  {
    m_DecimalFormatDeclarations = v;
  }

  /**
   * Read the stylesheet from a serialization stream.
   */
  private void readObject(ObjectInputStream stream)
    throws IOException, SAXException
  {
    // System.out.println("Reading Stylesheet");
    try
    {
      stream.defaultReadObject();
    }
    catch(ClassNotFoundException cnfe)
    {
      throw new XSLProcessorException(cnfe);
    }
    m_includeStack = null;
    // System.out.println("Done reading Stylesheet");
  }

  private void writeObject(ObjectOutputStream stream)
    throws IOException
  {
    // System.out.println("Writing Stylesheet");
    stream.defaultWriteObject();
    // System.out.println("Done writing Stylesheet");
  }


  /**
   * Constructor for a Stylesheet needs a Document.
   * @exception XSLProcessorException thrown if the active ProblemListener and XMLParserLiaison decide
   * the error condition is severe enough to halt processing.
   */
  public Stylesheet(StylesheetRoot root,
                    XSLTEngineImpl processor,
                    String baseIdentifier)
    throws XSLProcessorException,
           MalformedURLException,
           FileNotFoundException,
           IOException,
           SAXException
  {
    m_stylesheetRoot = root;
    m_baseIdent = baseIdentifier;
    init(processor);
  }

  /**
   * Tell if this is the root of the stylesheet tree.
   */
  boolean isRoot()
  {
    return false;
  }

  /**
   * Do common initialization.
   */
  protected void init(XSLTEngineImpl processor)
    throws XSLProcessorException,
    MalformedURLException,
    FileNotFoundException,
    IOException,
    SAXException
  {
    m_templateList = new TemplateList(this);
    m_includeStack = new Stack();
    m_includeStack.push(processor.m_parserLiaison.getURLFromString(this.m_baseIdent, null));
    initXPath(processor, null);
  }

  /**
   * This recursive function is called starting from the
   * stylesheet root, and tries to find a match for the
   * passed stylesheet, and then will return the previous
   * sibling, or, it will return which if it found the stylesheet
   * but there was no previous sibling.
   */
  Stylesheet getPreviousImport(Stylesheet which)
  {
    Stylesheet prev = null;
    if(null != m_imports)
    {
      int n = m_imports.size();
      for(int i = 0; i < n; i++)
      {
        if(m_imports.elementAt(i) == which)
        {
          prev = ((i+1) < n) ? (Stylesheet)m_imports.elementAt(i+1) : null;
          break;
        }
      }
    }
    return prev;
  }

  /**
   * Process the xsl:decimal-format element.
   */
  void processDecimalFormatElement(ElemTemplateElement edf, AttributeList atts)
  {
    m_DecimalFormatDeclarations.push(edf);
  }

  /**
   * Process the xsl:namespace-alias element.
   */
  void processNSAliasElement(String name, AttributeList atts)
  {
    int nAttrs = atts.getLength();
    String ns = null;
    String ns2 = null;
    for(int i = 0; i < nAttrs; i++)
    {
      String aname = atts.getName(i);
      String prefix;
      if(aname.equals(Constants.ATTRNAME_STYLESHEET_PREFIX))
      {
        prefix = atts.getValue(i).equals(Constants.ATTRVAL_DEFAULT_PREFIX)? "" : atts.getValue(i);
        ns = getNamespaceForPrefix(prefix);
      }
      else if(aname.equals(Constants.ATTRNAME_RESULT_PREFIX))
      {
        prefix = atts.getValue(i).equals(Constants.ATTRVAL_DEFAULT_PREFIX)? "" : atts.getValue(i);
        ns2 = getNamespaceForPrefix(prefix);
      }
      else if(!isAttrOK(aname, atts, i))
      {
        error(XSLTErrorResources.ER_ILLEGAL_ATTRIBUTE, new Object[] {name, aname}); //name+" has an illegal attribute: "+aname);
      }
    }
    // Build a table of aliases, the key is the stylesheet uri and the
    // value is the result uri
    if (null != ns && null != ns2)
    {
      if (null == m_prefix_aliases)
        m_prefix_aliases = new StringToStringTable();
      m_prefix_aliases.put(ns, ns2);
    }
    else
      error(XSLTErrorResources.ER_MISSING_NS_URI);
  }

  /**
   * Return the alias namespace uri for a given namespace uri if one is found.
   *
   * @param uri the URI of the namespace.
   */
  public String lookForAlias (String uri)
  {
    String s = null;
    if (null != m_prefix_aliases)
      s = (String) m_prefix_aliases.get (uri);
    if(null == s)
    {
      int nImports = m_imports.size();
      for(int i = 0; i < nImports; i++)
      {
        Stylesheet stylesheet = (Stylesheet)m_imports.elementAt(i);
        s = stylesheet.lookForAlias (uri);
        if(null != s)
        {
          break;
        }
      }
      if((null == s) && (null != m_stylesheetParent))
      {
        Stylesheet stylesheet = m_stylesheetParent.getPreviousImport(this);
        if(null != stylesheet)
        {
          s = stylesheet.lookForAlias (uri);
        }
      }
    }
    // If no alias was found, return the original uri
    return s == null ? uri : s;
  }

  /**
   * Process the xsl:key element.
   *
   * (Notes to myself)
   * What we need to do is:
   * 1) As this function is called, build a table of KeyDeclarations.
   * 2) During either XML processing, or upon request, walk the XML
   * document tree, and build a hash table:
   * a) keyed by name,
   * b) each with a value of a hashtable, keyed by the value returned by
   *    the use attribute,
   * c) each with a value that is a nodelist.
   * Thus, for a given key or keyref, look up hashtable by name,
   * look up the nodelist by the given reference.
   */
  void processKeyElement(ElemTemplateElement nsContext, AttributeList atts)
    throws SAXException
  {
    String nameAttr = null;
    XPath matchAttr = null;
    XPath useAttr = null;

    int nAttrs = atts.getLength();
    for(int i = 0; i < nAttrs; i++)
    {
      String aname = atts.getName(i);
      if(aname.equals(Constants.ATTRNAME_NAME))
      {
        nameAttr = atts.getValue(i);
      }
      else if(aname.equals(Constants.ATTRNAME_MATCH))
      {
        matchAttr = createMatchPattern(atts.getValue(i), nsContext);
      }
      else if(aname.equals(Constants.ATTRNAME_USE))
      {
        useAttr = createXPath(atts.getValue(i), nsContext);
      }
      else if(!isAttrOK(aname, atts, i))
      { 
        error(XSLTErrorResources.ER_ILLEGAL_ATTRIBUTE, new Object[] {"xsl:key", aname}); //name+" has an illegal attribute: "+aname);
      }
    }
    if(null == nameAttr)
      error(XSLTErrorResources.ER_KEY_REQUIRES_NAME_ATTRIB, new Object[] {Constants.ATTRNAME_NAME}); //"xsl:key requires a "+Constants.ATTRNAME_NAME+" attribute!");

    if(null == matchAttr)
      error(XSLTErrorResources.ER_KEY_REQUIRES_MATCH_ATTRIB, new Object[] {Constants.ATTRNAME_MATCH}); //"xsl:key requires a "+Constants.ATTRNAME_MATCH+" attribute!");

    if(null == useAttr)
      error(XSLTErrorResources.ER_KEY_REQUIRES_USE_ATTRIB, new Object[] {Constants.ATTRNAME_USE}); //"xsl:key requires a "+Constants.ATTRNAME_USE+" attribute!");

    m_keyDeclarations.addElement(new KeyDeclaration(nameAttr, matchAttr, useAttr));
    m_needToBuildKeysTable = true;
  }


  /**
   * Push the namespace declarations from the current attribute
   * list onto the namespace stack.
   */
  void pushNamespaces(AttributeList atts)
  {
    int nAttrs = atts.getLength();
    org.apache.xalan.xpath.xml.NameSpace nslist = m_emptyNamespace;
    for(int i = (nAttrs - 1); i >= 0; i--)
    {
      String aname = atts.getName(i);
      String value = atts.getValue(i);
      boolean isPrefix = aname.startsWith("xmlns:");
      if (isPrefix || aname.equals("xmlns"))
      {
        String p = isPrefix ? aname.substring(6) : "";
        NameSpace ns = new NameSpace(p, value);
        if(m_emptyNamespace == nslist)
        {
          nslist = ns;
        }
        else
        {
          ns.m_next = nslist;
          nslist = ns;
        }
      }
    }
    m_namespaces.push(nslist);
  }

  void popNamespaces()
  {
    m_namespaces.pop();
  }

  /**
   * Assuming legal attributes have been processed, check if the 
   * given attribute is legal on an xslt element.
   *
   * @param attrName Qualified name of attribute.
   * @param atts The attribute list where the element comes from (not used at
   *      this time).
   * @param which The index into the attribute list (not used at this time).
   * @return True if this attribute should not be flagged as an error.
   */
  boolean isAttrOK(String attrName, AttributeList atts, int which)
  {
    boolean isAttrOK = attrName.equals("xmlns") ||
                       attrName.startsWith("xmlns:");
    if(!isAttrOK)
    {
      int indexOfNSSep = attrName.indexOf(':');
      if(indexOfNSSep >= 0)
      {
        String prefix = attrName.substring(0, indexOfNSSep);
        String ns = getNamespaceForPrefixFromStack(prefix);
        isAttrOK = (!ns.equals(XSLTEngineImpl.m_XSLNameSpaceURL));
      }
      else
      {
        isAttrOK = false; // null namespace, flag it as normally not OK.
      }
    }

    // TODO: Well, process it...
    return isAttrOK;
  }

  /**
   * Get the namespace from a qualified name.
   */
  String getNamespaceFromStack(String nodeName)
  {
    int indexOfNSSep = nodeName.indexOf(':');
    String prefix = (indexOfNSSep >= 0) ? nodeName.substring(0, indexOfNSSep) : "";
    return getNamespaceForPrefixFromStack(prefix);
  }


  /**
   * Given a namespace, get the corrisponding prefix.
   */
  public String getNamespaceForPrefix(String prefix, org.w3c.dom.Node context)
  {
    if(context == this)
      return getNamespaceForPrefix(prefix);
    try
    {
      PrefixResolver pr = (PrefixResolver)context;
      return pr.getNamespaceForPrefix(prefix, context);
    }
    catch(ClassCastException cce)
    {
      error(XSLTErrorResources.ER_CANT_RESOLVE_PREFIX_OF_NON_PREFIX_RESOLVER); //"Can't resolve prefix of non-Prefix resolver!");
      return null;
    }
  }

  /**
   * Get the namespace from a prefix.
   */
  public String getNamespaceForPrefix(String prefix)
  {
    String namespace = null;
    if(prefix.equals("xml"))
    {
      namespace = Constants.S_XMLNAMESPACEURI;
    }
    else
    {
      NameSpace ns = m_namespaceDecls;
      while(null != ns)
      {
        if((null != ns.m_prefix) && prefix.equals(ns.m_prefix))
        {
          namespace = ns.m_uri;
          break;
        }
        ns = ns.m_next;
      }
    }
    return namespace;
  }

  /**
   * Get the namespace from a prefix.  Returns null if not found.
   */
  String getNamespaceForPrefixFromStack(String prefix)
  {
    String namespace = null;
    if(prefix.equals("xml"))
    {
      namespace = Constants.S_XMLNAMESPACEURI;
    }
    else if(prefix.equals("xmlns"))  // But I should really know if this is an attribute
    {
      namespace = null;
    }
    else
    {
      int depth = m_namespaces.size();
      for(int i = depth-1; i >= 0; i--)
      {
        NameSpace ns = (NameSpace)m_namespaces.elementAt(i);
        while(null != ns)
        {
          if((null != ns.m_prefix) && prefix.equals(ns.m_prefix))
          {
            namespace = ns.m_uri;
            i = -1;
            break;
          }
          ns = ns.m_next;
        }
      }
    }
    return namespace;
  }

  /**
   * Process an attribute that has the value of 'yes' or 'no'.
   */
  boolean getYesOrNo(String aname, String val)
    throws org.xml.sax.SAXException
  {
    if(val.equals(Constants.ATTRVAL_YES))
    {
      return true;
    }
    else if(val.equals(Constants.ATTRVAL_NO))
    {
      return false;
    }
    else
    {
      throw new XSLProcessorException(val+" is unknown value for "+aname);
    }
  }

  /**
   * Set a top level variable, to be serialized with the rest of
   * the stylesheet.
   * @param var A top-level variable declared with xsl:variable or xsl:param-variable.
   */
  void setTopLevelVariable(ElemVariable var)
  {
    m_topLevelVariables.addElement(var);
  }

  /**
   * Push top-level variables onto the variable stack.
   */
  void pushTopLevelVariables(Vector topLevelParams, XSLTEngineImpl transformContext)
    throws org.xml.sax.SAXException
  {
    try
    {
      int nImports = m_imports.size();
      for(int i = (nImports-1); i >= 0; i--)
      {
        Stylesheet stylesheet = (Stylesheet)m_imports.elementAt(i);
        stylesheet.pushTopLevelVariables(topLevelParams, transformContext);
      }
      int nVars = m_topLevelVariables.size();
      for(int i = 0; i < nVars; i++)
      {
        ElemVariable var = (ElemVariable)m_topLevelVariables.elementAt(i);
        boolean isParam = (Constants.ELEMNAME_PARAMVARIABLE == var.getXSLToken());
        if(isParam)
        {
          isParam = false;
          int n = topLevelParams.size();
          for(int k = 0; k < n; k++)
          {
            Arg a = (Arg)topLevelParams.elementAt(k);
            if(a.m_qname.equals(var.m_qname))
            {
              isParam = true;
              if(null != a.m_expression)
              {
                XPath selectPattern = createXPath(a.m_expression, this);
                a.m_val = selectPattern.execute(transformContext.getExecContext(),
                                                transformContext.m_rootDoc, this);
                a.m_expression = null;
              }
              transformContext.getVarStack().pushVariable(a.m_qname, a.m_val);
              break;
            }
          }
        }
        if(!isParam)
        {
          var.execute(transformContext, transformContext.m_rootDoc, transformContext.m_rootDoc, null);
        }
      }
    }
    catch(Exception e)
    {
      // Turn it into a runtime exception.
      throw new XSLProcessorException(e);
    }

  }


  /**
   * Given a target element, find the template that best
   * matches in the given XSL document, according
   * to the rules specified in the xsl draft.
   * @param stylesheetTree Where the XSL rules are to be found.
   * @param sourceTree Where the targetElem is to be found.
   * @param targetElem The element that needs a rule.
   * @return Rule that best matches targetElem.
   * @exception XSLProcessorException thrown if the active ProblemListener and XMLParserLiaison decide
   * the error condition is severe enough to halt processing.
   */
  ElemTemplate findTemplate(XSLTEngineImpl transformContext,
                            Node sourceTree,
                            Node targetNode)
    throws SAXException,
           java.net.MalformedURLException,
           java.io.FileNotFoundException,
           java.io.IOException
  {
    return getTemplateList().findTemplate(transformContext, sourceTree, targetNode, null, false);
  }

  /**
   * Given a target element, find the template that best
   * matches in the given XSL document, according
   * to the rules specified in the xsl draft.
   * @param stylesheetTree Where the XSL rules are to be found.
   * @param sourceTree Where the targetElem is to be found.
   * @param targetElem The element that needs a rule.
   * @param mode A string indicating the display mode.
   * @param useImports means that this is an xsl:apply-imports commend.
   * @param foundStylesheet If non-null, the Stylesheet that the found template
   * belongs to will be returned in the foundStylesheet[0].
   * @return Rule that best matches targetElem.
   * @exception XSLProcessorException thrown if the active ProblemListener and XMLParserLiaison decide
   * the error condition is severe enough to halt processing.
   */
  public ElemTemplate findTemplate(XSLTEngineImpl transformContext,
                                   Node sourceTree,
                                   Node targetNode,
                                   QName mode,
                                   boolean useImports)
    throws SAXException
  {
    return getTemplateList().findTemplate(transformContext,
                                   sourceTree, targetNode,
                                   mode, useImports);
  }

  /**
   * Locate a macro via the "name" attribute.
   * @exception XSLProcessorException thrown if the active ProblemListener and XMLParserLiaison decide
   * the error condition is severe enough to halt processing.
   */
  ElemTemplateElement findNamedTemplate(QName qname)
    throws XSLProcessorException
  {
    return getTemplateList().findNamedTemplate(qname);
  }

  /**
   * Add an attribute set to the list.
   */
  void addAttributeSet(QName qname, ElemAttributeSet attrSet)
  {
    if(null == m_attributeSets)
    {
      m_attributeSets = new Vector();
    }
    m_attributeSets.addElement(attrSet);
  }

  /**
   * Add the attributes from the named attribute sets to the attribute list.
   * TODO: Error handling for: "It is an error if there are two attribute sets
   * with the same expanded-name and with equal import precedence and that both
   * contain the same attribute unless there is a definition of the attribute
   * set with higher import precedence that also contains the attribute."
   */
  void applyAttrSets(QName attributeSetsNames[],
                     XSLTEngineImpl processor,
                     Node sourceTree,
                     Node sourceNode,
                     QName mode)
    throws XSLProcessorException,
           java.net.MalformedURLException,
           java.io.FileNotFoundException,
           java.io.IOException,
           SAXException
  {
    if(null != attributeSetsNames)
    {
      // Process up the import chain...
      int nImports = m_imports.size();
      for(int i = 0; i < nImports; i++)
      {
        Stylesheet stylesheet = (Stylesheet)m_imports.elementAt(i);
        stylesheet.applyAttrSets(attributeSetsNames,
                                 processor, sourceTree, sourceNode, mode);
      }
      int nNames = attributeSetsNames.length;
      for(int i = 0; i < nNames; i++)
      {
        QName qname = attributeSetsNames[i];
        int nSets = (null != m_attributeSets) ? m_attributeSets.size() : 0;
        for(int k = 0; k < nSets; k++)
        {
          ElemAttributeSet attrSet = (ElemAttributeSet)m_attributeSets.elementAt(k);
          if(qname.equals(attrSet.m_qname))
          {
            if(null != attrSet)
            {
              attrSet.execute(processor, sourceTree, sourceNode, mode);
            }
          }
        }
      }
    }
  }

  /**
   * Given a valid element key, return the corresponding node list.
   */
  public NodeList getNodeSetByKey(XSLTEngineImpl tcontext, Node doc, String name,
                                  String ref, PrefixResolver nscontext)
     throws org.xml.sax.SAXException
 {
    NodeList nl = null;
    if(null != m_keyDeclarations)
    {
      boolean foundDoc = false;
      if(null == tcontext.m_key_tables)
      {
        tcontext.m_key_tables = new Vector(4);
      }
      else
      {
        int nKeyTables = tcontext.m_key_tables.size();
        for(int i = 0; i < nKeyTables; i++)
        {
          KeyTable kt = (KeyTable)tcontext.m_key_tables.elementAt(i);
          if(doc == kt.m_docKey)
          {
            nl = kt.getNodeSetByKey(name, ref);
            if (nl != null && nl.getLength() > 0)
            {
              foundDoc = true;
              break;
            }
          }
        }
      }
      if((null == nl || nl.getLength() == 0) && !foundDoc && m_needToBuildKeysTable)
      {
        KeyTable kt = new KeyTable(doc, doc, nscontext, name, m_keyDeclarations,
                                   tcontext.m_parserLiaison);
        tcontext.m_key_tables.addElement(kt);
        if(doc == kt.m_docKey)
        {
          foundDoc = true;
          nl = kt.getNodeSetByKey(name, ref);
        }
      }
    }

    // If the nodelist is null at this point, it should
    // mean there wasn't an xsl:key declared with the
    // given name.  So go up the import heiarchy and
    // see if one of the imported stylesheets declared it.
    if(null == nl)
    {
      int nImports = m_imports.size();
      for(int i = 0; i < nImports; i++)
      {
        Stylesheet stylesheet = (Stylesheet)m_imports.elementAt(i);
        nl = stylesheet.getNodeSetByKey(tcontext, doc, name, ref, nscontext);
        if(null != nl)
        {
          break;
        }
      }
    }
    return nl;
  }

  /**
   * Given a valid element decimal-format name, return the decimalFormatSymbols with that name.
   */
  public DecimalFormatSymbols getDecimalFormatElem( String name)
  {
    DecimalFormatSymbols dfs = null;
    if(null != m_DecimalFormatDeclarations)
    {
      // Start from the top of the stack
      for (int i=m_DecimalFormatDeclarations.size()-1; i>=0; i--)
      {
        ElemDecimalFormat edf = (ElemDecimalFormat)m_DecimalFormatDeclarations.elementAt(i);
        if (edf.getName().equals(name))
        {
          dfs = edf.getDecimalFormatSymbols();
          break;
        }
      }
    }

    // If dfs is null at this point, it should
    // mean there wasn't an xsl:decimal-format declared with the
    // given name.  So go up the import heirarchy and
    // see if one of the imported stylesheets declared it.
    if(null == dfs)
    {
      int nImports = m_imports.size();
      for(int i = 0; i < nImports; i++)
      {
        Stylesheet stylesheet = (Stylesheet)m_imports.elementAt(i);
        dfs = stylesheet.getDecimalFormatElem(name);
        if(null != dfs)
        {
          break;
        }
      }
    }
    return dfs;
  }

  // BEGIN SANJIVA CODE
  /**
   * Add an extension namespace handler. This provides methods for calling
   * an element extension as well as for function stuff (which is passed
   * on to XPath).
   *
   * @param uri the URI of the extension namespace.
   * @param nsh handler
   */
  public void addExtensionNamespace (String uri, ExtensionNSHandler nsh)
  {
    m_extensionNamespaces.put (uri, nsh);
  }

  /**
   * Return the handler for a given extension namespace.
   *
   * @param uri the URI of the extension namespace.
   */
  public ExtensionNSHandler lookupExtensionNSHandler (String uri)
  {
    return (ExtensionNSHandler) m_extensionNamespaces.get (uri);
  }

  /**
   * XPath object to use for short evaluations, so we don't have to
   * create one every time.
   */
  private XPath m_xpath = null;

  /**
   * Factory for creating xpaths.
   */
  transient private XPathFactory m_xpathFactory = null;

  /**
   * Statically init anything to do with XPath.
   */
  void initXPath(XSLTEngineImpl processor)
  {
    m_xpathFactory = processor.getXMLProcessorLiaison().getDefaultXPathFactory();

    // m_parserLiaison.setProcessorOwner(this);
    m_xpathProcessor = new XPathProcessorImpl(processor.getXMLProcessorLiaison());
  }

  /**
   * Init anything to do with XPath.
   */
  protected void initXPath(XSLTEngineImpl processor, XPathFactory xpathFactory)
  {
    if(null != xpathFactory)
      m_xpathFactory = xpathFactory;
    else
      m_xpathFactory = processor.getXMLProcessorLiaison().getDefaultXPathFactory();

    // m_parserLiaison.setProcessorOwner(this);
    m_xpathProcessor = new XPathProcessorImpl(processor.getXMLProcessorLiaison());

    m_xpath = m_xpathFactory.create();
    m_xpath.installFunction("document", new FuncDocument());
    m_xpath.installFunction("format-number", new FuncFormatNumb());
  }


  /**
   * The query/pattern-matcher object.
   */
  XPathProcessor m_xpathProcessor = null;

  /**
   * Evaluate an xpath string and return the result.
   */
  XPath createXPath(String str, PrefixResolver nsNode)
    throws org.xml.sax.SAXException
  {
    XPath xpath = m_xpathFactory.create();

    m_xpathProcessor.initXPath(xpath, str, nsNode);
    // xpath.shrink();
    return xpath;
  }

  /**
   * Evaluate an xpath string and return the result.
   */
  double evalMatchPatternStr(XPathSupport execContext, String str, Node context, PrefixResolver nsNode)
    throws org.xml.sax.SAXException
  {
    // This needs to use a factory method of some sort.
    m_xpathProcessor.initMatchPattern(m_xpath, str, nsNode);

    return m_xpath.getMatchScore(execContext, context);
  }

  /**
   * Evaluate an xpath string and return the result.
   */
  XPath createMatchPattern(String str, PrefixResolver nsNode)
    throws org.xml.sax.SAXException
  {
    XPath xpath = m_xpathFactory.create();

    m_xpathProcessor.initMatchPattern(xpath, str, nsNode);
    // xpath.shrink();
    return xpath;
  }

  /**
   * Evaluate an xpath string and return the result.
   */
  public XObject evalXPathStr(XPathSupport execContext, String str,
                              Node context, PrefixResolver nsNode)
    throws org.xml.sax.SAXException
  {
    // System.out.println(str+" : "+ nsNode.getNodeName() + " : "+nsNode.getNodeName());
    XPath xpath = m_xpathFactory.create();
    m_xpathProcessor.initXPath(xpath, str, nsNode);
    return xpath.execute(execContext, context, nsNode);
  }

  /**
   * Get the type of the node.  We'll pretend we're a Document.
   */
  public short              getNodeType()
  {
    return Node.DOCUMENT_NODE;
  }

  /** Unimplemented. */
  public DocumentType       getDoctype()
  {
    error(XSLTErrorResources.ER_DOCUMENT_FUNCTION_INVALID_IN_STYLESHEET_DOM);
    return null;
  }

  /** Unimplemented. */
  public DOMImplementation  getImplementation()
  {
    error(XSLTErrorResources.ER_DOCUMENT_FUNCTION_INVALID_IN_STYLESHEET_DOM);
    return null;
  }

  /** Unimplemented. */
  public Element            getDocumentElement()
  {
    error(XSLTErrorResources.ER_DOCUMENT_FUNCTION_INVALID_IN_STYLESHEET_DOM);
    return null;
  }

  /** Unimplemented. */
  public Element            createElement(String tagName)
    throws DOMException
  {
    error(XSLTErrorResources.ER_DOCUMENT_FUNCTION_INVALID_IN_STYLESHEET_DOM);
    return null;
  }

  /** Unimplemented. */
  public DocumentFragment   createDocumentFragment()
  {
    error(XSLTErrorResources.ER_DOCUMENT_FUNCTION_INVALID_IN_STYLESHEET_DOM);
    return null;
  }

  /** Unimplemented. */
  public Text               createTextNode(String data)
  {
    error(XSLTErrorResources.ER_DOCUMENT_FUNCTION_INVALID_IN_STYLESHEET_DOM);
    return null;
  }

  /** Unimplemented. */
  public Comment            createComment(String data)
  {
    error(XSLTErrorResources.ER_DOCUMENT_FUNCTION_INVALID_IN_STYLESHEET_DOM);
    return null;
  }

  /** Unimplemented. */
  public CDATASection       createCDATASection(String data)
    throws DOMException

  {
    error(XSLTErrorResources.ER_DOCUMENT_FUNCTION_INVALID_IN_STYLESHEET_DOM);
    return null;
  }

  /** Unimplemented. */
  public ProcessingInstruction createProcessingInstruction(String target,
                                                           String data)
    throws DOMException

  {
    error(XSLTErrorResources.ER_DOCUMENT_FUNCTION_INVALID_IN_STYLESHEET_DOM);
    return null;
  }

  /** Unimplemented. */
  public Attr               createAttribute(String name)
    throws DOMException
  {
    error(XSLTErrorResources.ER_DOCUMENT_FUNCTION_INVALID_IN_STYLESHEET_DOM);
    return null;
  }

  /** Unimplemented. */
  public EntityReference    createEntityReference(String name)
    throws DOMException
  {
    error(XSLTErrorResources.ER_DOCUMENT_FUNCTION_INVALID_IN_STYLESHEET_DOM);
    return null;
  }

  /** Unimplemented. */
  public NodeList           getElementsByTagName(String tagname)
  {
    error(XSLTErrorResources.ER_DOCUMENT_FUNCTION_INVALID_IN_STYLESHEET_DOM);
    return null;
  }

  /** Unimplemented. */
  public Node               importNode(Node importedNode,
                                       boolean deep)
    throws DOMException
  {
    error(XSLTErrorResources.ER_DOCUMENT_FUNCTION_INVALID_IN_STYLESHEET_DOM);
    return null;
  }

  /** Unimplemented. */
  public Element            createElementNS(String namespaceURI,
                                            String qualifiedName)
    throws DOMException
  {
    error(XSLTErrorResources.ER_DOCUMENT_FUNCTION_INVALID_IN_STYLESHEET_DOM);
    return null;
  }

  /** Unimplemented. */
  public Attr               createAttributeNS(String namespaceURI,
                                              String qualifiedName)
    throws DOMException
  {
    error(XSLTErrorResources.ER_DOCUMENT_FUNCTION_INVALID_IN_STYLESHEET_DOM);
    return null;
  }

  /** Unimplemented. */
  public NodeList           getElementsByTagNameNS(String namespaceURI,
                                                   String localName)
  {
    error(XSLTErrorResources.ER_DOCUMENT_FUNCTION_INVALID_IN_STYLESHEET_DOM);
    return null;
  }

  /** Unimplemented. */
  public Element            getElementById(String elementId)
  {
    error(XSLTErrorResources.ER_DOCUMENT_FUNCTION_INVALID_IN_STYLESHEET_DOM);
    return null;
  }

  //==========================================================
  // SECTION: XSL directive handling functions
  //==========================================================

  /**
   *  Hash table that can look up xslt element IDs via name.
   */
  static Hashtable m_elementKeys = null;

  /**
   *  Hash table that can look up xslt element IDs via name.
   */
  static Hashtable m_attributeKeys = null;

  /**
   * Hash table that can look up Xalan extensions element IDs via name.
   */
  static Hashtable m_XSLT4JElementKeys = new Hashtable();

  /**
   * Init the XSLT hashtable.
   */
  void initXSLTKeys()
  {
    if(null == m_elementKeys)
    {
      synchronized(XSLTEngineImpl.m_XSLNameSpaceURL)
      {
        Hashtable elementKeys = new Hashtable();
        Hashtable attributeKeys = new Hashtable();

        // String pre = m_XSLNameSpaceURL+":";
        elementKeys.put(new StringKey(Constants.ELEMNAME_APPLY_TEMPLATES_STRING), new Integer(Constants.ELEMNAME_APPLY_TEMPLATES));
        elementKeys.put(new StringKey(Constants.ELEMNAME_WITHPARAM_STRING), new Integer(Constants.ELEMNAME_WITHPARAM));
        elementKeys.put(new StringKey(Constants.ELEMNAME_CONSTRUCT_STRING), new Integer(Constants.ELEMNAME_CONSTRUCT));
        elementKeys.put(new StringKey(Constants.ELEMNAME_CONTENTS_STRING), new Integer(Constants.ELEMNAME_CONTENTS));
        elementKeys.put(new StringKey(Constants.ELEMNAME_COPY_STRING), new Integer(Constants.ELEMNAME_COPY));
        elementKeys.put(new StringKey(Constants.ELEMNAME_COPY_OF_STRING), new Integer(Constants.ELEMNAME_COPY_OF));

        elementKeys.put(new StringKey(Constants.ELEMNAME_ATTRIBUTESET_STRING), new Integer(Constants.ELEMNAME_DEFINEATTRIBUTESET));

        elementKeys.put(new StringKey(Constants.ELEMNAME_USE_STRING), new Integer(Constants.ELEMNAME_USE));

        elementKeys.put(new StringKey(Constants.ELEMNAME_VARIABLE_STRING), new Integer(Constants.ELEMNAME_VARIABLE));
        elementKeys.put(new StringKey(Constants.ELEMNAME_PARAMVARIABLE_STRING), new Integer(Constants.ELEMNAME_PARAMVARIABLE));

        elementKeys.put(new StringKey(Constants.ELEMNAME_DISPLAYIF_STRING), new Integer(Constants.ELEMNAME_DISPLAYIF));
        elementKeys.put(new StringKey(Constants.ELEMNAME_EMPTY_STRING), new Integer(Constants.ELEMNAME_EMPTY));
        elementKeys.put(new StringKey(Constants.ELEMNAME_EVAL_STRING), new Integer(Constants.ELEMNAME_EVAL));
        elementKeys.put(new StringKey(Constants.ELEMNAME_CALLTEMPLATE_STRING), new Integer(Constants.ELEMNAME_CALLTEMPLATE));
        elementKeys.put(new StringKey(Constants.ELEMNAME_TEMPLATE_STRING), new Integer(Constants.ELEMNAME_TEMPLATE));
        elementKeys.put(new StringKey(Constants.ELEMNAME_STYLESHEET_STRING), new Integer(Constants.ELEMNAME_STYLESHEET));
        elementKeys.put(new StringKey(Constants.ELEMNAME_TRANSFORM_STRING), new Integer(Constants.ELEMNAME_STYLESHEET));
        elementKeys.put(new StringKey(Constants.ELEMNAME_IMPORT_STRING), new Integer(Constants.ELEMNAME_IMPORT));
        elementKeys.put(new StringKey(Constants.ELEMNAME_INCLUDE_STRING), new Integer(Constants.ELEMNAME_INCLUDE));
        elementKeys.put(new StringKey(Constants.ELEMNAME_FOREACH_STRING), new Integer(Constants.ELEMNAME_FOREACH));
        elementKeys.put(new StringKey(Constants.ELEMNAME_VALUEOF_STRING), new Integer(Constants.ELEMNAME_VALUEOF));
        elementKeys.put(new StringKey(Constants.ELEMNAME_KEY_STRING), new Integer(Constants.ELEMNAME_KEY));
        elementKeys.put(new StringKey(Constants.ELEMNAME_STRIPSPACE_STRING), new Integer(Constants.ELEMNAME_STRIPSPACE));
        elementKeys.put(new StringKey(Constants.ELEMNAME_PRESERVESPACE_STRING), new Integer(Constants.ELEMNAME_PRESERVESPACE));
        elementKeys.put(new StringKey(Constants.ELEMNAME_NUMBER_STRING), new Integer(Constants.ELEMNAME_NUMBER));
        elementKeys.put(new StringKey(Constants.ELEMNAME_IF_STRING), new Integer(Constants.ELEMNAME_IF));
        elementKeys.put(new StringKey(Constants.ELEMNAME_CHOOSE_STRING), new Integer(Constants.ELEMNAME_CHOOSE));
        elementKeys.put(new StringKey(Constants.ELEMNAME_WHEN_STRING), new Integer(Constants.ELEMNAME_WHEN));
        elementKeys.put(new StringKey(Constants.ELEMNAME_OTHERWISE_STRING), new Integer(Constants.ELEMNAME_OTHERWISE));
        elementKeys.put(new StringKey(Constants.ELEMNAME_TEXT_STRING), new Integer(Constants.ELEMNAME_TEXT));
        elementKeys.put(new StringKey(Constants.ELEMNAME_ELEMENT_STRING), new Integer(Constants.ELEMNAME_ELEMENT));
        elementKeys.put(new StringKey(Constants.ELEMNAME_ATTRIBUTE_STRING), new Integer(Constants.ELEMNAME_ATTRIBUTE));
        elementKeys.put(new StringKey(Constants.ELEMNAME_SORT_STRING), new Integer(Constants.ELEMNAME_SORT));
        elementKeys.put(new StringKey(Constants.ELEMNAME_PI_STRING), new Integer(Constants.ELEMNAME_PI));
        elementKeys.put(new StringKey(Constants.ELEMNAME_COMMENT_STRING), new Integer(Constants.ELEMNAME_COMMENT));

        elementKeys.put(new StringKey(Constants.ELEMNAME_COUNTER_STRING), new Integer(Constants.ELEMNAME_COUNTER));
        elementKeys.put(new StringKey(Constants.ELEMNAME_COUNTERS_STRING), new Integer(Constants.ELEMNAME_COUNTERS));
        elementKeys.put(new StringKey(Constants.ELEMNAME_COUNTERINCREMENT_STRING), new Integer(Constants.ELEMNAME_COUNTERINCREMENT));
        elementKeys.put(new StringKey(Constants.ELEMNAME_COUNTERRESET_STRING), new Integer(Constants.ELEMNAME_COUNTERRESET ));
        elementKeys.put(new StringKey(Constants.ELEMNAME_COUNTERSCOPE_STRING), new Integer(Constants.ELEMNAME_COUNTERSCOPE ));

        elementKeys.put(new StringKey(Constants.ELEMNAME_APPLY_IMPORTS_STRING), new Integer(Constants.ELEMNAME_APPLY_IMPORTS ));

        elementKeys.put(new StringKey(Constants.ELEMNAME_EXTENSION_STRING), new Integer(Constants.ELEMNAME_EXTENSION));

        elementKeys.put(new StringKey(Constants.ELEMNAME_MESSAGE_STRING), new Integer(Constants.ELEMNAME_MESSAGE));
        elementKeys.put(new StringKey(Constants.ELEMNAME_LOCALE_STRING), new Integer(Constants.ELEMNAME_LOCALE));
        elementKeys.put(new StringKey(Constants.ELEMNAME_FALLBACK_STRING), new Integer(Constants.ELEMNAME_FALLBACK));

        elementKeys.put(new StringKey(Constants.ELEMNAME_DECIMALFORMAT_STRING), new Integer(Constants.ELEMNAME_DECIMALFORMAT));

        elementKeys.put(new StringKey(Constants.ELEMNAME_OUTPUT_STRING), new Integer(Constants.ELEMNAME_OUTPUT));
        elementKeys.put(new StringKey(Constants.ELEMNAME_NSALIAS_STRING), new Integer(Constants.ELEMNAME_NSALIAS));

        // elementKeys.put(m_XSLT4JNameSpaceURL+":"+Constants.ELEMNAME_EXTENSIONHANDLER_STRING, new Integer(Constants.ELEMNAME_EXTENSIONHANDLER));

        // m_XSLT4JElementKeys.put(new StringKey(Constants.ELEMNAME_EXTENSION_STRING).intern(), new Integer(Constants.ELEMNAME_EXTENSION));
        m_XSLT4JElementKeys.put(new StringKey(Constants.ELEMNAME_CSSSTYLECONVERSION_STRING), new Integer(Constants.ELEMNAME_CSSSTYLECONVERSION));

        m_XSLT4JElementKeys.put(new StringKey(Constants.ELEMNAME_COMPONENT_STRING), new Integer(Constants.ELEMNAME_COMPONENT));
        m_XSLT4JElementKeys.put(Constants.ELEMNAME_SCRIPT_STRING, new Integer(Constants.ELEMNAME_SCRIPT));

        attributeKeys.put(new StringKey(Constants.ATTRNAME_OUTPUT_METHOD), new Integer(Constants.TATTRNAME_OUTPUT_METHOD));
        attributeKeys.put(new StringKey(Constants.ATTRNAME_AMOUNT), new Integer(Constants.TATTRNAME_AMOUNT));
        attributeKeys.put(new StringKey(Constants.ATTRNAME_ANCESTOR), new Integer(Constants.TATTRNAME_ANCESTOR));
        attributeKeys.put(new StringKey(Constants.ATTRNAME_ARCHIVE), new Integer(Constants.TATTRNAME_ARCHIVE));
        attributeKeys.put(new StringKey(Constants.ATTRNAME_ATTRIBUTE), new Integer(Constants.TATTRNAME_ATTRIBUTE));
        attributeKeys.put(new StringKey(Constants.ATTRNAME_ATTRIBUTE_SET), new Integer(Constants.TATTRNAME_ATTRIBUTE_SET));
        attributeKeys.put(new StringKey(Constants.ATTRNAME_CASEORDER), new Integer(Constants.TATTRNAME_CASEORDER));
        attributeKeys.put(new StringKey(Constants.ATTRNAME_CLASS), new Integer(Constants.TATTRNAME_CLASS));
        attributeKeys.put(new StringKey(Constants.ATTRNAME_CLASSID), new Integer(Constants.TATTRNAME_CLASSID));
        attributeKeys.put(new StringKey(Constants.ATTRNAME_CODEBASE), new Integer(Constants.TATTRNAME_CODEBASE));
        attributeKeys.put(new StringKey(Constants.ATTRNAME_CODETYPE), new Integer(Constants.TATTRNAME_CODETYPE));
        attributeKeys.put(new StringKey(Constants.ATTRNAME_CONDITION), new Integer(Constants.TATTRNAME_CONDITION));
        attributeKeys.put(new StringKey(Constants.ATTRNAME_COPYTYPE), new Integer(Constants.TATTRNAME_COPYTYPE));
        attributeKeys.put(new StringKey(Constants.ATTRNAME_COUNT), new Integer(Constants.TATTRNAME_COUNT));
        attributeKeys.put(new StringKey(Constants.ATTRNAME_DATATYPE), new Integer(Constants.TATTRNAME_DATATYPE));
        attributeKeys.put(new StringKey(Constants.ATTRNAME_DEFAULT), new Integer(Constants.TATTRNAME_DEFAULT));
        attributeKeys.put(new StringKey(Constants.ATTRNAME_DEFAULTSPACE), new Integer(Constants.TATTRNAME_DEFAULTSPACE));
        attributeKeys.put(new StringKey(Constants.ATTRNAME_DEPTH), new Integer(Constants.TATTRNAME_DEPTH));
        attributeKeys.put(new StringKey(Constants.ATTRNAME_DIGITGROUPSEP), new Integer(Constants.TATTRNAME_DIGITGROUPSEP));
        attributeKeys.put(new StringKey(Constants.ATTRNAME_DISABLE_OUTPUT_ESCAPING), new Integer(Constants.TATTRNAME_DISABLE_OUTPUT_ESCAPING));
        attributeKeys.put(new StringKey(Constants.ATTRNAME_ELEMENT), new Integer(Constants.TATTRNAME_ELEMENT));
        attributeKeys.put(new StringKey(Constants.ATTRNAME_ELEMENTS), new Integer(Constants.TATTRNAME_ELEMENTS));
        attributeKeys.put(new StringKey(Constants.ATTRNAME_EXPR), new Integer(Constants.TATTRNAME_EXPR));
        attributeKeys.put(new StringKey(Constants.ATTRNAME_EXTENSIONELEMENTPREFIXES), new Integer(Constants.TATTRNAME_EXTENSIONELEMENTPREFIXES));
        attributeKeys.put(new StringKey(Constants.ATTRNAME_FORMAT), new Integer(Constants.TATTRNAME_FORMAT));
        attributeKeys.put(new StringKey(Constants.ATTRNAME_FROM), new Integer(Constants.TATTRNAME_FROM));
        attributeKeys.put(new StringKey(Constants.ATTRNAME_GROUPINGSEPARATOR), new Integer(Constants.TATTRNAME_GROUPINGSEPARATOR));
        attributeKeys.put(new StringKey(Constants.ATTRNAME_GROUPINGSIZE), new Integer(Constants.TATTRNAME_GROUPINGSIZE));
        attributeKeys.put(new StringKey(Constants.ATTRNAME_HREF), new Integer(Constants.TATTRNAME_HREF));
        attributeKeys.put(new StringKey(Constants.ATTRNAME_ID), new Integer(Constants.TATTRNAME_ID));
        attributeKeys.put(new StringKey(Constants.ATTRNAME_IMPORTANCE), new Integer(Constants.TATTRNAME_IMPORTANCE));
        attributeKeys.put(new StringKey(Constants.ATTRNAME_INDENTRESULT), new Integer(Constants.TATTRNAME_INDENTRESULT));
        attributeKeys.put(new StringKey(Constants.ATTRNAME_LANG), new Integer(Constants.TATTRNAME_LANG));
        attributeKeys.put(new StringKey(Constants.ATTRNAME_LETTERVALUE), new Integer(Constants.TATTRNAME_LETTERVALUE));
        attributeKeys.put(new StringKey(Constants.ATTRNAME_LEVEL), new Integer(Constants.TATTRNAME_LEVEL));
        attributeKeys.put(new StringKey(Constants.ATTRNAME_MATCH), new Integer(Constants.TATTRNAME_MATCH));
        attributeKeys.put(new StringKey(Constants.ATTRNAME_METHOD), new Integer(Constants.TATTRNAME_METHOD));
        attributeKeys.put(new StringKey(Constants.ATTRNAME_MODE), new Integer(Constants.TATTRNAME_MODE));
        attributeKeys.put(new StringKey(Constants.ATTRNAME_NAME), new Integer(Constants.TATTRNAME_NAME));
        attributeKeys.put(new StringKey(Constants.ATTRNAME_NAMESPACE), new Integer(Constants.TATTRNAME_NAMESPACE));
        attributeKeys.put(new StringKey(Constants.ATTRNAME_NDIGITSPERGROUP), new Integer(Constants.TATTRNAME_NDIGITSPERGROUP));
        attributeKeys.put(new StringKey(Constants.ATTRNAME_NS), new Integer(Constants.TATTRNAME_NS));
        attributeKeys.put(new StringKey(Constants.ATTRNAME_ONLY), new Integer(Constants.TATTRNAME_ONLY));
        attributeKeys.put(new StringKey(Constants.ATTRNAME_ORDER), new Integer(Constants.TATTRNAME_ORDER));
        attributeKeys.put(new StringKey(Constants.ATTRNAME_OUTPUT_CDATA_SECTION_ELEMENTS), new Integer(Constants.TATTRNAME_OUTPUT_CDATA_SECTION_ELEMENTS));
        attributeKeys.put(new StringKey(Constants.ATTRNAME_OUTPUT_DOCTYPE_PUBLIC), new Integer(Constants.TATTRNAME_OUTPUT_DOCTYPE_PUBLIC));
        attributeKeys.put(new StringKey(Constants.ATTRNAME_OUTPUT_DOCTYPE_SYSTEM), new Integer(Constants.TATTRNAME_OUTPUT_DOCTYPE_SYSTEM));
        attributeKeys.put(new StringKey(Constants.ATTRNAME_OUTPUT_ENCODING), new Integer(Constants.TATTRNAME_OUTPUT_ENCODING));
        attributeKeys.put(new StringKey(Constants.ATTRNAME_OUTPUT_INDENT), new Integer(Constants.TATTRNAME_OUTPUT_INDENT));
        attributeKeys.put(new StringKey(Constants.ATTRNAME_OUTPUT_MEDIATYPE), new Integer(Constants.TATTRNAME_OUTPUT_MEDIATYPE));
        attributeKeys.put(new StringKey(Constants.ATTRNAME_OUTPUT_STANDALONE), new Integer(Constants.TATTRNAME_OUTPUT_STANDALONE));
        attributeKeys.put(new StringKey(Constants.ATTRNAME_OUTPUT_VERSION), new Integer(Constants.TATTRNAME_OUTPUT_VERSION));
        attributeKeys.put(new StringKey(Constants.ATTRNAME_OUTPUT_OMITXMLDECL), new Integer(Constants.TATTRNAME_OUTPUT_OMITXMLDECL));
        attributeKeys.put(new StringKey(Constants.ATTRNAME_PRIORITY), new Integer(Constants.TATTRNAME_PRIORITY));
        attributeKeys.put(new StringKey(Constants.ATTRNAME_REFID), new Integer(Constants.TATTRNAME_REFID));
        attributeKeys.put(new StringKey(Constants.ATTRNAME_RESULTNS), new Integer(Constants.TATTRNAME_RESULTNS));
        attributeKeys.put(new StringKey(Constants.ATTRNAME_SELECT), new Integer(Constants.TATTRNAME_SELECT));
        attributeKeys.put(new StringKey(Constants.ATTRNAME_SEQUENCESRC), new Integer(Constants.TATTRNAME_SEQUENCESRC));
        attributeKeys.put(new StringKey(Constants.ATTRNAME_STYLE), new Integer(Constants.TATTRNAME_STYLE));
        attributeKeys.put(new StringKey(Constants.ATTRNAME_TEST), new Integer(Constants.TATTRNAME_TEST));
        attributeKeys.put(new StringKey(Constants.ATTRNAME_TOSTRING), new Integer(Constants.TATTRNAME_TOSTRING));
        attributeKeys.put(new StringKey(Constants.ATTRNAME_TYPE), new Integer(Constants.TATTRNAME_TYPE));
        attributeKeys.put(new StringKey(Constants.ATTRNAME_USE), new Integer(Constants.TATTRNAME_USE));
        attributeKeys.put(new StringKey(Constants.ATTRNAME_USEATTRIBUTESETS), new Integer(Constants.TATTRNAME_USEATTRIBUTESETS));
        attributeKeys.put(new StringKey(Constants.ATTRNAME_VALUE), new Integer(Constants.TATTRNAME_VALUE));

        attributeKeys.put(new StringKey(Constants.ATTRNAME_XMLNSDEF), new Integer(Constants.TATTRNAME_XMLNSDEF));
        attributeKeys.put(new StringKey(Constants.ATTRNAME_XMLNS), new Integer(Constants.TATTRNAME_XMLNS));
        attributeKeys.put(new StringKey(Constants.ATTRNAME_XMLSPACE), new Integer(Constants.TATTRNAME_XMLSPACE));

        m_attributeKeys = attributeKeys;
        m_elementKeys = elementKeys;
      }
    }
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
   * or Xalan namespace, -2 if not in known namespace.
   */
  /*
  final int getXSLToken(Node node)
    throws XSLProcessorException
  {
    int tok;
    String ns = m_parserLiaison.getNamespaceOfNode(node);
    if(null != ns)
    {
      // ns = ns.toLowerCase();
      if(ns.equals(m_stylesheetRoot.m_XSLNameSpaceURL))
      {
        String localName = m_parserLiaison.getLocalNameOfNode(node);
        Integer i = (Integer)m_elementKeys.get(localName);
        if(null == i)
        {
          tok = -2;
        }
        else
        {
          tok = i.intValue();
        }
      }
      else if(ns.equals(m_XSLT4JNameSpaceURL))
      {
        String localName = m_parserLiaison.getLocalNameOfNode(node);
        Integer i = (Integer)m_XSLT4JElementKeys.get(localName);
        if(null == i)
        {
          tok = -2;
        }
        else
        {
          tok = i.intValue();
        }
      }
      else
      {
        tok = -2;
      }
    }
    else
    {
      tok = -2;
    }
    return tok;
  }
  */

  /**
   * Find the type of an element using this method. This is slower
   * than it ought to be... I'll find a way to optimize down the
   * line if need be.
   * @param node a probable xsl:xxx element.
   * @param tagType Constants.ELEMNAME_XXX token.
   * @return true if node is of tagType.
   */
  /*
  final boolean isXSLTagOfType(Node node, int tagType)
    throws XSLProcessorException
  {
    return (getXSLToken(node) == tagType);
  }
  */

  /**
   * Set the factory for making XPaths.
   */
  public void setXPathFactory(XPathFactory factory)
  {
    m_xpathFactory = factory;
  }

  /**
   * Get the factory for making xpaths.
   */
  public XPathFactory getXPathFactory()
  {
    return m_xpathFactory;
  }

  /**
   * Set the XPath processor object.
   * @param processor A XPathProcessor interface.
   */
  public void setXPathProcessor(XPathProcessor processor)
  {
    m_xpathProcessor = processor;
  }

  /**
   * Get the XPath processor object.
   * @return The XPathProcessor interface being used.
   */
  public XPathProcessor getXPathProcessor()
  {
    return m_xpathProcessor;
  }

}
