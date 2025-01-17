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
package org.apache.xalan.templates;

import java.lang.InstantiationException;

import java.io.Serializable;

import java.util.Enumeration;
import java.util.Vector;

// Xalan imports
import org.apache.xml.utils.UnImplNode;
import org.apache.xml.utils.NameSpace;
import org.apache.xml.utils.PrefixResolver;
import org.apache.xml.utils.QName;
import org.apache.xml.utils.StringToStringTable;
import org.apache.xalan.res.XSLTErrorResources;
import org.apache.xalan.res.XSLMessages;
import org.apache.xalan.transformer.TransformerImpl;
import org.apache.xalan.transformer.ResultNameSpace;
import org.apache.xalan.transformer.ResultTreeHandler;
import org.apache.xpath.VariableStack;
import org.apache.xpath.WhitespaceStrippingElementMatcher;

// TRaX imports
import javax.xml.transform.Templates;
import javax.xml.transform.SourceLocator;

// DOM Imports
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;

// SAX Imports
import org.xml.sax.Locator;
import javax.xml.transform.TransformerException;
import org.xml.sax.helpers.NamespaceSupport;

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
        implements PrefixResolver, Serializable, SourceLocator, 
                   WhitespaceStrippingElementMatcher
{

  /**
   * Construct a template element instance.
   *
   * @param transformer The XSLT TransformerFactory.
   * @param stylesheetTree The owning stylesheet.
   * @param name The name of the element.
   * @param atts The element attributes.
   * @param lineNumber The line in the XSLT file that the element occurs on.
   * @param columnNumber The column index in the XSLT file that the element occurs on.
   */
  public ElemTemplateElement(){}

  /**
   * Tell if this template is a compiled template.
   *
   * @return Boolean flag indicating whether this is a compiled template   
   */
  public boolean isCompiledTemplate()
  {
    return false;
  }

  /**
   * Get an integer representation of the element type.
   *
   * @return An integer representation of the element, defined in the
   *     Constants class.
   * @see org.apache.xalan.templates.Constants
   */
  public int getXSLToken()
  {
    return Constants.ELEMNAME_UNDEFINED;
  }

  /**
   * Return the node name.
   *
   * @return An invalid node name
   */
  public String getNodeName()
  {
    return "Unknown XSLT Element";
  }

  /**
   * This function will be called on top-level elements
   * only, just before the transform begins.
   *
   * @param transformer The XSLT TransformerFactory.
   *
   * @throws TransformerException
   */
  public void runtimeInit(TransformerImpl transformer) throws TransformerException{}

  /**
   * Execute the element's primary function.  Subclasses of this
   * function may recursivly execute down the element tree.
   *
   * @param transformer The XSLT TransformerFactory.
   * @param sourceNode The current context node.
   * @param mode The current mode.
   * 
   * @throws TransformerException if any checked exception occurs.
   */
  public void execute(
          TransformerImpl transformer, Node sourceNode, QName mode)
            throws TransformerException{}

  /**
   * Get the owning "composed" stylesheet.  This looks up the
   * inheritance chain until it calls getStylesheetComposed
   * on a Stylesheet object, which will Get the owning
   * aggregated stylesheet, or that stylesheet if it is aggregated.
   *
   * @return the owning "composed" stylesheet.
   */
  public StylesheetComposed getStylesheetComposed()
  {
    return m_parentNode.getStylesheetComposed();
  }

  /**
   * Get the owning stylesheet.  This looks up the
   * inheritance chain until it calls getStylesheet
   * on a Stylesheet object, which will return itself.
   *
   * @return the owning stylesheet
   */
  public Stylesheet getStylesheet()
  {
    return m_parentNode.getStylesheet();
  }

  /**
   * Get the owning root stylesheet.  This looks up the
   * inheritance chain until it calls StylesheetRoot
   * on a Stylesheet object, which will return a reference
   * to the root stylesheet.
   *
   * @return the owning root stylesheet
   */
  public StylesheetRoot getStylesheetRoot()
  {
    return m_parentNode.getStylesheetRoot();
  }

  /**
   * This function is called during recomposition to
   * control how this element is composed.
   */
  public void recompose(StylesheetRoot root) throws TransformerException
  {
  }

  /**
   * This function is called after everything else has been
   * recomposed, and allows the template to set remaining
   * values that may be based on some other property that
   * depends on recomposition.
   */
  public void compose(){}

  /**
   * Validate that the string is an NCName.
   *
   * @param s The name in question.
   * @return True if the string is a valid NCName according to XML rules.
   * @see <a href="http://www.w3.org/TR/REC-xml-names#NT-NCName">XXX in XSLT Specification</a>
   */
  protected boolean isValidNCName(String s)
  {

    int len = s.length();
    char c = s.charAt(0);

    if (!(Character.isLetter(c) || (c == '_')))
      return false;

    if (len > 0)
    {
      for (int i = 1; i < len; i++)
      {
        c = s.charAt(i);

        if (!(Character.isLetterOrDigit(c) || (c == '_') || (c == '-')
              || (c == '.')))
          return false;
      }
    }

    return true;
  }

  /**
   * Throw a template element runtime error.  (Note: should we throw a TransformerException instead?)
   *
   * @param msg Description of the error that occured.
   * @param args Arguments to be used in the message
   */
  public void error(int msg, Object[] args)
  {

    String themsg = XSLMessages.createMessage(msg, args);

    throw new RuntimeException(
      XSLMessages.createMessage(
        XSLTErrorResources.ER_ELEMTEMPLATEELEM_ERR, new Object[]{ themsg }));  //"ElemTemplateElement error: "+msg);
  }

  // Implemented DOM Element methods.

  /**
   * Add a child to the child list.
   * NOTE: This presumes the child did not previously have a parent.
   * Making that assumption makes this a less expensive operation -- but
   * requires that if you *do* want to reparent a node, you use removeChild()
   * first to remove it from its previous context. Failing to do so will
   * damage the tree.
   *
   * @param newChild Child to be added to child list
   *
   * @return Child just added to the child list
   * @throws DOMException
   */
  public Node appendChild(Node newChild) throws DOMException
  {

    if (null == newChild)
    {
      error(XSLTErrorResources.ER_NULL_CHILD, null);  //"Trying to add a null child!");
    }

    ElemTemplateElement elem = (ElemTemplateElement) newChild;

    if (null == m_firstChild)
    {
      m_firstChild = elem;
    }
    else
    {
      ElemTemplateElement last = (ElemTemplateElement) getLastChild();

      last.m_nextSibling = elem;
    }

    elem.m_parentNode = this;

    return newChild;
  }

  /**
   * Tell if there are child nodes.
   *
   * @return True if there are child nodes
   */
  public boolean hasChildNodes()
  {
    return (null != m_firstChild);
  }

  /**
   * Get the type of the node.
   *
   * @return Constant for this node type
   */
  public short getNodeType()
  {
    return Node.ELEMENT_NODE;
  }

  /**
   * Return the nodelist (same reference).
   *
   * @return The nodelist containing the child nodes (this)
   */
  public NodeList getChildNodes()
  {
    return this;
  }

  /**
   * Remove a child.
   * ADDED 9/8/200 to support compilation.
   * TODO: ***** Alternative is "removeMe() from my parent if any"
   * ... which is less well checked, but more convenient in some cases.
   * Given that we assume only experts are calling this class, it might
   * be preferable. It's less DOMish, though.
   * 
   * @param childETE The child to remove. This operation is a no-op
   * if oldChild is not a child of this node.
   *
   * @return the removed child, or null if the specified
   * node was not a child of this element.
   *
   * @throws DOMException
   */
  public Node removeChild(ElemTemplateElement childETE) throws DOMException
  {

    if (childETE == null || childETE.m_parentNode != this)
      return null;

    // Pointers to the child
    if (childETE == m_firstChild)
      m_firstChild = childETE.m_nextSibling;
    else
    {
      ElemTemplateElement prev =
        (ElemTemplateElement) (childETE.getPreviousSibling());

      prev.m_nextSibling = childETE.m_nextSibling;
    }

    // Pointers from the child
    childETE.m_parentNode = null;
    childETE.m_nextSibling = null;

    return childETE;
  }

  /**
   * Replace the old child with a new child.
   *
   * @param newChild New child to replace with
   * @param oldChild Old child to be replaced
   *
   * @return The new child
   *
   * @throws DOMException
   */
  public Node replaceChild(Node newChild, Node oldChild) throws DOMException
  {

    if (oldChild == null || oldChild.getParentNode() != this)
      return null;

    ElemTemplateElement newChildElem = ((ElemTemplateElement) newChild);
    ElemTemplateElement oldChildElem = ((ElemTemplateElement) oldChild);

    // Fix up previous sibling.
    ElemTemplateElement prev =
      (ElemTemplateElement) oldChildElem.getPreviousSibling();

    if (null != prev)
      prev.m_nextSibling = newChildElem;

    // Fix up parent (this)
    if (m_firstChild == oldChildElem)
      m_firstChild = newChildElem;

    newChildElem.m_parentNode = this;
    oldChildElem.m_parentNode = null;
    newChildElem.m_nextSibling = oldChildElem.m_nextSibling;
    oldChildElem.m_nextSibling = null;

    // newChildElem.m_stylesheet = oldChildElem.m_stylesheet;
    // oldChildElem.m_stylesheet = null;
    return newChildElem;
  }

  /**
   * NodeList method: Count the immediate children of this node
   *
   * @return The count of children of this node
   */
  public int getLength()
  {

    // It is assumed that the getChildNodes call synchronized
    // the children. Therefore, we can access the first child
    // reference directly.
    int count = 0;

    for (ElemTemplateElement node = m_firstChild; node != null;
            node = node.m_nextSibling)
    {
      count++;
    }

    return count;
  }  // getLength():int

  /**
   * NodeList method: Return the Nth immediate child of this node, or
   * null if the index is out of bounds.
   *
   * @param index Index of child to find
   * @return org.w3c.dom.Node: the child node at given index
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
  }  // item(int):Node

  /**
   * Get the stylesheet owner.
   *
   * @return The stylesheet owner
   */
  public Document getOwnerDocument()
  {
    return getStylesheet();
  }

  /**
   * Return the element name.
   *
   * @return The element name
   */
  public String getTagName()
  {
    return getNodeName();
  }

  /**
   * Return the base identifier.
   *
   * @return The base identifier 
   */
  public String getBaseIdentifier()
  {

    // Should this always be absolute?
    return this.getSystemId();
  }

  /** line number where the current document event ends.
   *  @serial         */
  private int m_lineNumber;

  /**
   * Return the line number where the current document event ends.
   * Note that this is the line position of the first character
   * after the text associated with the document event.
   * @return The line number, or -1 if none is available.
   * @see #getColumnNumber
   */
  public int getLineNumber()
  {
    return m_lineNumber;
  }

  /** the column number where the current document event ends.
   *  @serial        */
  private int m_columnNumber;

  /**
   * Return the column number where the current document event ends.
   * Note that this is the column number of the first
   * character after the text associated with the document
   * event.  The first column in a line is position 1.
   * @return The column number, or -1 if none is available.
   * @see #getLineNumber
   */
  public int getColumnNumber()
  {
    return m_columnNumber;
  }

  /**
   * Return the public identifier for the current document event.
   * <p>This will be the public identifier
   * @return A string containing the public identifier, or
   *         null if none is available.
   * @see #getSystemId
   */
  public String getPublicId()
  {
    return (null != m_parentNode) ? m_parentNode.getPublicId() : null;
  }

  /**
   * Return the system identifier for the current document event.
   *
   * <p>If the system identifier is a URL, the parser must resolve it
   * fully before passing it to the application.</p>
   *
   * @return A string containing the system identifier, or null
   *         if none is available.
   * @see #getPublicId
   */
  public String getSystemId()
  {
    return this.getStylesheet().getHref();
  }

  /**
   * Set the location information for this element.
   *
   * @param locator Source Locator with location information for this element
   */
  public void setLocaterInfo(SourceLocator locator)
  {
    m_lineNumber = locator.getLineNumber();
    m_columnNumber = locator.getColumnNumber();
  }

  /**
   * Tell if this element has the default space handling
   * turned off or on according to the xml:space attribute.
   * @serial
   */
  private boolean m_defaultSpace = true;

  /**
   * Set the "xml:space" attribute.
   * A text node is preserved if an ancestor element of the text node
   * has an xml:space attribute with a value of preserve, and
   * no closer ancestor element has xml:space with a value of default.
   * @see <a href="http://www.w3.org/TR/xslt#strip">strip in XSLT Specification</a>
   * @see <a href="http://www.w3.org/TR/xslt#section-Creating-Text">section-Creating-Text in XSLT Specification</a>
   *
   * @param v  Enumerated value, either Constants.ATTRVAL_PRESERVE 
   * or Constants.ATTRVAL_STRIP.
   */
  public void setXmlSpace(int v)
  {
    m_defaultSpace = ((Constants.ATTRVAL_STRIP == v) ? true : false);
  }

  /**
   * Get the "xml:space" attribute.
   * A text node is preserved if an ancestor element of the text node
   * has an xml:space attribute with a value of preserve, and
   * no closer ancestor element has xml:space with a value of default.
   * @see <a href="http://www.w3.org/TR/xslt#strip">strip in XSLT Specification</a>
   * @see <a href="http://www.w3.org/TR/xslt#section-Creating-Text">section-Creating-Text in XSLT Specification</a>
   *
   * @return The value of the xml:space attribute
   */
  public boolean getXmlSpace()
  {
    return m_defaultSpace;
  }

  /**
   * The list of namespace declarations for this element only.
   * @serial
   */
  private Vector m_declaredPrefixes;

  /**
   * Return a table that contains all prefixes available
   * within this element context.
   *
   * @return Vector containing the prefixes available within this
   * element context 
   */
  public Vector getDeclaredPrefixes()
  {
    return m_declaredPrefixes;
  }

  /**
   * From the SAX2 helper class, set the namespace table for
   * this element.  Take care to call resolveInheritedNamespaceDecls.
   * after all namespace declarations have been added.
   *
   * @param nsSupport non-null reference to NamespaceSupport from 
   * the ContentHandler.
   *
   * @throws TransformerException
   */
  public void setPrefixes(NamespaceSupport nsSupport) throws TransformerException
  {
    setPrefixes(nsSupport, false);
  }

  /**
   * Copy the namespace declarations from the NamespaceSupport object.  
   * Take care to call resolveInheritedNamespaceDecls.
   * after all namespace declarations have been added.
   *
   * @param nsSupport non-null reference to NamespaceSupport from 
   * the ContentHandler.
   * @param excludeXSLDecl true if XSLT namespaces should be ignored.
   *
   * @throws TransformerException
   */
  public void setPrefixes(NamespaceSupport nsSupport, boolean excludeXSLDecl)
          throws TransformerException
  {

    Enumeration decls = nsSupport.getDeclaredPrefixes();

    while (decls.hasMoreElements())
    {
      String prefix = (String) decls.nextElement();

      if (null == m_declaredPrefixes)
        m_declaredPrefixes = new Vector();

      String uri = nsSupport.getURI(prefix);

      if (excludeXSLDecl && uri.equals(Constants.S_XSLNAMESPACEURL))
        continue;

      // System.out.println("setPrefixes - "+prefix+", "+uri);
      XMLNSDecl decl = new XMLNSDecl(prefix, uri, false);

      m_declaredPrefixes.addElement(decl);
    }
  }

  /**
   * Fullfill the PrefixResolver interface.  Calling this for this class 
   * will throw an error.
   *
   * @param prefix The prefix to look up, which may be an empty string ("") 
   *               for the default Namespace.
   * @param context The node context from which to look up the URI.
   *
   * @return null if the error listener does not choose to throw an exception.
   */
  public String getNamespaceForPrefix(String prefix, org.w3c.dom.Node context)
  {

    this.error(XSLTErrorResources.ER_CANT_RESOLVE_NSPREFIX, null);

    return null;
  }

  /**
   * Given a namespace, get the corrisponding prefix.
   * 9/15/00: This had been iteratively examining the m_declaredPrefixes
   * field for this node and its parents. That makes life difficult for
   * the compilation experiment, which doesn't have a static vector of
   * local declarations. Replaced a recursive solution, which permits
   * easier subclassing/overriding.
   *
   * @param prefix non-null reference to prefix string, which should map 
   *               to a namespace URL.
   *
   * @return The namespace URL that the prefix maps to, or null if no 
   *         mapping can be found.
   */
  public String getNamespaceForPrefix(String prefix)
  {

    Vector nsDecls = m_declaredPrefixes;

    if (null != nsDecls)
    {
      int n = nsDecls.size();

      for (int i = 0; i < n; i++)
      {
        XMLNSDecl decl = (XMLNSDecl) nsDecls.elementAt(i);

        if (prefix.equals(decl.getPrefix()))
          return decl.getURI();
      }
    }

    // Not found; ask our ancestors
    if (null != m_parentNode)
      return m_parentNode.getNamespaceForPrefix(prefix);

    // No parent, so no definition
    return null;
  }

  /**
   * The table of {@link XMLNSDecl}s for this element
   * and all parent elements, screened for excluded prefixes.
   * @serial
   */
  Vector m_prefixTable;

  /**
   * Return a table that contains all prefixes available
   * within this element context.
   *
   * @return reference to vector of {@link XMLNSDecl}s, which may be null.
   */
  public Vector getPrefixes()
  {
    return m_prefixTable;
  }
  
  /**
   * Get whether or not the passed URL is contained flagged by
   * the "extension-element-prefixes" property.  This method is overridden 
   * by {@link ElemLiteralResult#containsExcludeResultPrefix}.
   * @see <a href="http://www.w3.org/TR/xslt#extension-element">extension-element in XSLT Specification</a>
   *
   * @param prefix non-null reference to prefix that might be excluded.
   *
   * @return true if the prefix should normally be excluded.
   */
  public boolean containsExcludeResultPrefix(String prefix)
  {
    ElemTemplateElement parent = this.getParentElem();
    if(null != parent)
      return parent.containsExcludeResultPrefix(prefix);
      
    return false;
  }

  /**
   * Tell if the result namespace decl should be excluded.  Should be called before
   * namespace aliasing (I think).
   *
   * @param prefix non-null reference to prefix.
   * @param uri reference to namespace that prefix maps to, which is protected 
   *            for null, but should really never be passed as null.
   *
   * @return true if the given namespace should be excluded.
   *
   * @throws TransformerException
   */
  private boolean excludeResultNSDecl(String prefix, String uri)
          throws TransformerException
  {

    if (uri != null)
    {
      if (uri.equals(Constants.S_XSLNAMESPACEURL)
              || getStylesheet().containsExtensionElementURI(uri)
              || uri.equals(Constants.S_BUILTIN_EXTENSIONS_URL))
        return true;

      if (containsExcludeResultPrefix(prefix))
        return true;
    }

    return false;
  }
  
  /**
   * Combine the parent's namespaces with this namespace
   * for fast processing, taking care to reference the
   * parent's namespace if this namespace adds nothing new.
   * (Recursive method, walking the elements depth-first,
   * processing parents before children).
   * Note that this method builds m_prefixTable with aliased 
   * namespaces, *not* the original namespaces.
   *
   * @throws TransformerException
   */
  public void resolvePrefixTables() throws TransformerException
  {
    // Always start with a fresh prefix table!
    m_prefixTable = null;

    // If we have declared declarations, then we look for 
    // a parent that has namespace decls, and add them 
    // to this element's decls.  Otherwise we just point 
    // to the parent that has decls.
    if (null != this.m_declaredPrefixes)
    {
      StylesheetRoot stylesheet = this.getStylesheetRoot();
      
      // Add this element's declared prefixes to the 
      // prefix table.
      int n = m_declaredPrefixes.size();

      for (int i = 0; i < n; i++)
      {
        XMLNSDecl decl = (XMLNSDecl) m_declaredPrefixes.elementAt(i);
        String prefix = decl.getPrefix();
        String uri = decl.getURI();
        if(null == uri)
          uri = "";
        boolean shouldExclude = excludeResultNSDecl(prefix, uri);

        // Create a new prefix table if one has not already been created.
        if (null == m_prefixTable)
          m_prefixTable = new Vector();

        NamespaceAlias nsAlias = stylesheet.getNamespaceAliasComposed(uri);
        if(null != nsAlias)
        {
          // Should I leave the non-aliased element in the table as 
          // an excluded element?
          
          // The exclusion should apply to the non-aliased prefix, so 
          // we don't calculate it here.  -sb
          // Use stylesheet prefix, as per xsl WG
          decl = new XMLNSDecl(nsAlias.getStylesheetPrefix(), 
                              nsAlias.getResultNamespace(), shouldExclude);
        }
        else
          decl = new XMLNSDecl(prefix, uri, shouldExclude);

        m_prefixTable.addElement(decl);
      }
    }

    ElemTemplateElement parent = (ElemTemplateElement) this.getParentNode();

    if (null != parent)
    {

      // The prefix table of the parent should never be null!
      Vector prefixes = parent.m_prefixTable;

      if (null == m_prefixTable && !needToCheckExclude())
      {

        // Nothing to combine, so just use parent's table!
        this.m_prefixTable = parent.m_prefixTable;
      }
      else
      {

        // Add the prefixes from the parent's prefix table.
        int n = prefixes.size();
        
        for (int i = 0; i < n; i++)
        {
          XMLNSDecl decl = (XMLNSDecl) prefixes.elementAt(i);
          boolean shouldExclude = excludeResultNSDecl(decl.getPrefix(),
                                                      decl.getURI());

          if (shouldExclude != decl.getIsExcluded())
          {
            decl = new XMLNSDecl(decl.getPrefix(), decl.getURI(),
                                 shouldExclude);
          }
          
          m_prefixTable.addElement(decl);
        }
      }
    }
    else if (null == m_prefixTable)
    {

      // Must be stylesheet element without any result prefixes!
      m_prefixTable = new Vector();
    }

    // Resolve the children's prefix tables.
    for (ElemTemplateElement child = m_firstChild; child != null;
            child = child.m_nextSibling)
    {
      child.resolvePrefixTables();
    }
  }
  
  /**
   * Return whether we need to check namespace prefixes 
   * against and exclude result prefixes list.
   */
  boolean needToCheckExclude()
  {
    return false;    
  } 

  /**
   * Send startPrefixMapping events to the result tree handler
   * for all declared prefix mappings in the stylesheet.
   *
   * @param transformer non-null reference to the the current transform-time state.
   *
   * @throws TransformerException
   */
  void executeNSDecls(TransformerImpl transformer) throws TransformerException
  {

    try
    {
      if (null != m_prefixTable)
      {
        ResultTreeHandler rhandler = transformer.getResultTreeHandler();
        int n = m_prefixTable.size();

        for (int i = n - 1; i >= 0; i--)
        {
          XMLNSDecl decl = (XMLNSDecl) m_prefixTable.elementAt(i);

          if (!decl.getIsExcluded())
          {
            rhandler.startPrefixMapping(decl.getPrefix(), decl.getURI(), true);
          }
        }
      }
    }
    catch(org.xml.sax.SAXException se)
    {
      throw new TransformerException(se);
    }
  }

  /**
   * Send startPrefixMapping events to the result tree handler
   * for all declared prefix mappings in the stylesheet.
   *
   * @param transformer non-null reference to the the current transform-time state.
   *
   * @throws TransformerException
   */
  void unexecuteNSDecls(TransformerImpl transformer) throws TransformerException
  {

    try
    {
      if (null != m_prefixTable)
      {
        ResultTreeHandler rhandler = transformer.getResultTreeHandler();
        int n = m_prefixTable.size();

        for (int i = 0; i < n; i++)
        {
          XMLNSDecl decl = (XMLNSDecl) m_prefixTable.elementAt(i);

          if (!decl.getIsExcluded())
          {
            rhandler.endPrefixMapping(decl.getPrefix());
          }
        }
      }
    }
    catch(org.xml.sax.SAXException se)
    {
      throw new TransformerException(se);
    }
  }
  
  /** The *relative* document order number of this element.
   *  @serial */
  protected int m_docOrderNumber = -1;
  
  /**
   * Set the UID (document order index).
   *
   * @param kIndex Index of this child.
   */
  public void setUid(int i)
  {
    m_docOrderNumber = i;
  }

  /**
   * Get the UID (document order index).
   *
   * @return Index of this child
   */
  public int getUid()
  {
    return m_docOrderNumber;
  }


  /**
   * Parent node.
   * @serial
   */
  protected ElemTemplateElement m_parentNode;

  /**
   * Get the parent as a Node.
   *
   * @return This node's parent node
   */
  public Node getParentNode()
  {
    return m_parentNode;
  }

  /**
   * Get the parent as an ElemTemplateElement.
   *
   * @return This node's parent as an ElemTemplateElement
   */
  public ElemTemplateElement getParentElem()
  {
    return m_parentNode;
  }

  /**
   * Next sibling.
   * @serial
   */
  ElemTemplateElement m_nextSibling;

  /**
   * Get the next sibling (as a Node) or return null.
   *
   * @return this node's next sibling or null
   */
  public Node getNextSibling()
  {
    return m_nextSibling;
  }

  /**
   * Get the previous sibling (as a Node) or return null.
   * Note that this may be expensive if the parent has many kids;
   * we accept that price in exchange for avoiding the prev pointer
   * TODO: If we were sure parents and sibs are always ElemTemplateElements,
   * we could hit the fields directly rather than thru accessors.
   *
   * @return This node's previous sibling or null
   */
  public Node getPreviousSibling()
  {

    Node walker = getParentNode(), prev = null;

    if (walker != null)
      for (walker = walker.getFirstChild(); walker != null;
              prev = walker, walker = walker.getNextSibling())
      {
        if (walker == this)
          return prev;
      }

    return null;
  }

  /**
   * Get the next sibling (as a ElemTemplateElement) or return null.
   *
   * @return This node's next sibling (as a ElemTemplateElement) or null 
   */
  public ElemTemplateElement getNextSiblingElem()
  {
    return m_nextSibling;
  }

  /**
   * First child.
   * @serial
   */
  ElemTemplateElement m_firstChild;

  /**
   * Get the first child as a Node.
   *
   * @return This node's first child or null
   */
  public Node getFirstChild()
  {
    return m_firstChild;
  }

  /**
   * Get the first child as a ElemTemplateElement.
   *
   * @return This node's first child (as a ElemTemplateElement) or null
   */
  public ElemTemplateElement getFirstChildElem()
  {
    return m_firstChild;
  }

  /**
   * Get the last child.
   *
   * @return This node's last child
   */
  public Node getLastChild()
  {

    ElemTemplateElement lastChild = null;

    for (ElemTemplateElement node = m_firstChild; node != null;
            node = node.m_nextSibling)
    {
      lastChild = node;
    }

    return lastChild;
  }

  /** DOM backpointer that this element originated from.          */
  transient private Node m_DOMBackPointer;

  /**
   * If this stylesheet was created from a DOM, get the
   * DOM backpointer that this element originated from.
   * For tooling use.
   *
   * @return DOM backpointer that this element originated from or null.
   */
  public Node getDOMBackPointer()
  {
    return m_DOMBackPointer;
  }

  /**
   * If this stylesheet was created from a DOM, set the
   * DOM backpointer that this element originated from.
   * For tooling use.
   *
   * @param n DOM backpointer that this element originated from.
   */
  public void setDOMBackPointer(Node n)
  {
    m_DOMBackPointer = n;
  }

  /**
   * Compares this object with the specified object for precedence order.
   * The order is determined by the getImportCountComposed() of the containing
   * composed stylesheet and the getUid() of this element.
   * Returns a negative integer, zero, or a positive integer as this
   * object is less than, equal to, or greater than the specified object.
   * 
   * @param o The object to be compared to this object
   * @returns a negative integer, zero, or a positive integer as this object is
   *          less than, equal to, or greater than the specified object.
   * @throws ClassCastException if the specified object's
   *         type prevents it from being compared to this Object.
   */
  public int compareTo(Object o) throws ClassCastException {
    
    ElemTemplateElement ro = (ElemTemplateElement) o;
    int roPrecedence = ro.getStylesheetComposed().getImportCountComposed();
    int myPrecedence = this.getStylesheetComposed().getImportCountComposed();

    if (myPrecedence < roPrecedence)
      return -1;
    else if (myPrecedence > roPrecedence)
      return 1;
    else
      return this.getUid() - ro.getUid();
  }
  
  /**
   * Get information about whether or not an element should strip whitespace.
   * @see <a href="http://www.w3.org/TR/xslt#strip">strip in XSLT Specification</a>
   *
   * @param support The XPath runtime state.
   * @param targetElement Element to check
   *
   * @return true if the whitespace should be stripped.
   *
   * @throws TransformerException
   */
  public boolean shouldStripWhiteSpace(
          org.apache.xpath.XPathContext support, 
          org.w3c.dom.Element targetElement) throws TransformerException
  {
    StylesheetRoot sroot = this.getStylesheetRoot();
    return (null != sroot) ? sroot.shouldStripWhiteSpace(support, targetElement) :false;
  }
  
  /**
   * Get information about whether or not whitespace can be stripped.
   * @see <a href="http://www.w3.org/TR/xslt#strip">strip in XSLT Specification</a>
   *
   * @return true if the whitespace can be stripped.
   */
  public boolean canStripWhiteSpace()
  {
    StylesheetRoot sroot = this.getStylesheetRoot();
    return (null != sroot) ? sroot.canStripWhiteSpace() : false;
  }

}
