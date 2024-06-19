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

import java.util.*;
import org.xml.sax.*;
import org.apache.xalan.xpath.xml.*;
import org.w3c.dom.*;
import java.util.Vector;
import java.io.*;
import java.net.*;
import org.apache.xalan.xpath.XPath;
import org.apache.xalan.xslt.res.XSLTErrorResources;
import org.apache.xalan.xpath.xml.XSLMessages;

/**
 * <meta name="usage" content="advanced"/>
 * Initializes and processes a stylesheet via SAX events.
 * If you need to alter the code in here,
 * it is not for the faint-of-heart, due to the state tracking
 * that has to be done due to the SAX event model.
 */
public class StylesheetHandler implements DocumentHandler
{
  /**
   * The XSLT Processor for needed services.
   */
  XSLTEngineImpl m_processor;

  /**
   * The owning stylesheet.
   */
  Stylesheet m_stylesheet;

  /**
   * The stack of elements, pushed and popped as events occur.
   */
  Stack m_elems = new Stack();

  /**
   * Need to keep a stack of found whitespace elements so that
   * whitespace elements next to non-whitespace elements can
   * be merged.  For instance: &lt;out> &lt;![CDATA[test]]> &lt;/out>
   */
  Stack m_whiteSpaceElems = new Stack();

  /**
   * The current template.
   */
  ElemTemplate m_template = null;

  /**
   * The last element popped from the stack.  I'm not totally clear
   * anymore as to why this is needed.
   */
  ElemTemplateElement m_lastPopped = null;

  /**
   * True if the process is in a template context.
   */
  boolean m_inTemplate = false;

  /**
   * True if the stylesheet element was found, or if it was determined that
   * the stylesheet is wrapperless.
   */
  boolean m_foundStylesheet = false;

  /**
   * Flag to let us know when we've found an element inside the
   * stylesheet that is not an xsl:import, so we can restrict imports
   * to being the first elements.
   */
  boolean m_foundNotImport = false;

  // BEGIN SANJIVA CODE
  boolean m_inLXSLTScript = false;
  StringBuffer m_LXSLTScriptBody;
  String m_LXSLTScriptLang;
  String m_LXSLTScriptSrcURL;
  ExtensionNSHandler m_LXSLTExtensionNSH;
  // boolean m_bsfIsInited = false;
  // END SANJIVA CODE

  /**
   * This will act as a stack of sorts to keep track of the
   * current include base.
   */
  public String m_includeBase;

  /**
   * FormatterToText instance constructor... it will add the DOM nodes
   * to the document fragment.
   */
  public StylesheetHandler(XSLTEngineImpl processor, Stylesheet stylesheetTree)
  {
    m_processor = processor;
    m_stylesheet = stylesheetTree;
    m_includeBase = m_stylesheet.m_baseIdent;
  }

  /**
   * Receive an object for locating the origin of SAX document events.
   *
   * <p>SAX parsers are strongly encouraged (though not absolutely
   * required) to supply a locator: if it does so, it must supply
   * the locator to the application by invoking this method before
   * invoking any of the other methods in the DocumentHandler
   * interface.</p>
   *
   * <p>The locator allows the application to determine the end
   * position of any document-related event, even if the parser is
   * not reporting an error.  Typically, the application will
   * use this information for reporting its own errors (such as
   * character content that does not match an application's
   * business rules).  The information returned by the locator
   * is probably not sufficient for use with a search engine.</p>
   *
   * <p>Note that the locator will return correct information only
   * during the invocation of the events in this interface.  The
   * application should not attempt to use it at any other time.</p>
   *
   * @param locator An object that can return the location of
   *                any SAX document event.
   * @see org.xml.sax.Locator
   */
  public void setDocumentLocator (Locator locator)
  {
    // System.out.println("pushing locator for: "+locator.getPublicId());
    m_processor.m_stylesheetLocatorStack.push(locator);
  }

  /**
   * Receive notification of the beginning of a document.
   *
   * <p>The SAX parser will invoke this method only once, before any
   * other methods in this interface or in DTDHandler (except for
   * setDocumentLocator).</p>
   *
   * @exception org.xml.sax.SAXException Any SAX exception, possibly
   *            wrapping another exception.
   */
  public void startDocument ()
    throws SAXException
  {
    // No action for the moment.
  }


  /**
   * Receive notification of the end of a document.
   *
   * <p>The SAX parser will invoke this method only once, and it will
   * be the last method invoked during the parse.  The parser shall
   * not invoke this method until it has either abandoned parsing
   * (because of an unrecoverable error) or reached the end of
   * input.</p>
   *
   * @exception org.xml.sax.SAXException Any SAX exception, possibly
   *            wrapping another exception.
   */
  public void endDocument ()
    throws SAXException
  {
    if(!m_processor.m_stylesheetLocatorStack.empty())
    {
      Locator locator = (Locator)m_processor.m_stylesheetLocatorStack.pop();
      // System.out.println("popping locator for: "+locator.getPublicId());
    }
    // m_stylesheet.m_extensionNamespaces.clear();
  }

  /**
   * See if this is a xmlns attribute, and, if so, process it.
   *
   * @param attrName Qualified name of attribute.
   * @param atts The attribute list where the element comes from (not used at
   *      this time).
   * @param which The index into the attribute list (not used at this time).
   * @return True if this is a namespace name.
   */
  private boolean isAttrOK(String attrName, AttributeList atts, int which)
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
  private boolean processSpaceAttr(String aname, AttributeList atts, int which)
    throws SAXException
  {
    boolean isSpaceAttr = aname.equals("xml:space");
    if(isSpaceAttr)
    {
      String spaceVal = atts.getValue(which);
      if(spaceVal.equals("default"))
      {
        m_stylesheet.m_defaultSpace = true;
      }
      else if(spaceVal.equals("preserve"))
      {
        m_stylesheet.m_defaultSpace = false;
      }
      else
      {
        throw new SAXException(XSLMessages.createMessage(XSLTErrorResources.ER_XMLSPACE_ILLEGAL_VAL, new Object[]{spaceVal})); //"(StylesheetHandler) "+"xml:space has an illegal value: "+spaceVal);
      }
    }
    return isSpaceAttr;
  }

  /**
   * If this is set to true, we've already warned about using the
   * older XSLT namespace URL.
   */
  private boolean warnedAboutOldXSLTNamespace = false;

  /**
   * The old namespace that we must match as a minumum for XSLT.
   */
  static final String m_oldXSLNameSpaceURL = "http://www.w3.org/XSL/Transform/1.0";

  /**
   * Tell if the given namespace is XSLT
   */
  private boolean isXSLTNameSpace(String ns)
    throws SAXException
  {
    boolean isOld = false;
    boolean isXSLT = (null != ns) && (ns.equals( m_processor.m_XSLNameSpaceURL ) ||
                        (isOld = ns.equals( m_oldXSLNameSpaceURL )));
    if(isOld && !warnedAboutOldXSLTNamespace)
    {
      warnedAboutOldXSLTNamespace = true;
      m_processor.warn(XSLTErrorResources.WG_OLD_XSLT_NS, new Object[] {ns});
    }
    return isXSLT;
  }

  /**
   * Get the local name, without the prefix, if there is one.
   */
  private String getLocalName(String name)
  {
    int index = name.indexOf(':');
    return (index < 0) ? name : name.substring(index+1);
  }

  /**
   * Process xsl:strip-space and xsl:preserve-space.
   */
  private void processStripAndPreserve(String name, AttributeList atts,
                                       int xslToken, int lineNumber, int columnNumber)
    throws SAXException
  {
    ElemTemplateElement nsNode = new ElemEmpty(m_processor,
                                               m_stylesheet,
                                               name, atts, lineNumber, columnNumber);

    int nAttrs = atts.getLength();
    boolean foundIt = false;
    for(int i = 0; i < nAttrs; i++)
    {
      String aname = atts.getName(i);
      if(aname.equals(Constants.ATTRNAME_ELEMENTS))
      {
        foundIt = true;
        StringTokenizer tokenizer = new StringTokenizer(atts.getValue(i), " \t\n\r");
        while(tokenizer.hasMoreTokens())
        {
          // Use only the root, at least for right now.
          String wildcardName = tokenizer.nextToken();

          /**
          * Creating a match pattern is too much overhead, but it's a reasonably
          * easy and safe way to do this right now.  TODO: Validate the pattern
          * to make sure it's a WildcardName.
          */
          XPath matchPat = m_stylesheet.createMatchPattern(wildcardName, nsNode);

          if(Constants.ELEMNAME_PRESERVESPACE == xslToken)
          {
            if(null == m_stylesheet.m_stylesheetRoot.m_whitespacePreservingElements)
            {
              m_stylesheet.m_stylesheetRoot.m_whitespacePreservingElements
                = new Vector();
            }
            m_stylesheet.m_stylesheetRoot.m_whitespacePreservingElements.addElement(matchPat);
          }
          else
          {
            if(null == m_stylesheet.m_stylesheetRoot.m_whitespaceStrippingElements)
            {
              m_stylesheet.m_stylesheetRoot.m_whitespaceStrippingElements
                = new Vector();
            }
            m_stylesheet.m_stylesheetRoot.m_whitespaceStrippingElements.addElement(matchPat);
          }
        }
      }
      else if(!isAttrOK(aname, atts, i))
      {
        m_stylesheet.error(XSLTErrorResources.ER_ILLEGAL_ATTRIBUTE, new Object[] {name, aname}); //name+" has an illegal attribute: "+aname);
      }
    }
    if(!foundIt)
    {
      throw new SAXException(XSLMessages.createMessage(XSLTErrorResources.ER_REQUIRES_ELEMENTS_ATTRIB, new Object[]{name, Constants.ATTRNAME_ELEMENTS})); //"(StylesheetHandler) "+name+" requires a "+Constants.ATTRNAME_ELEMENTS+" attribute!");
    }
  }

  /**
   * Receive notification of the beginning of an element.
   *
   * <p>The Parser will invoke this method at the beginning of every
   * element in the XML document; there will be a corresponding
   * endElement() event for every startElement() event (even when the
   * element is empty). All of the element's content will be
   * reported, in order, before the corresponding endElement()
   * event.</p>
   *
   * <p>If the element name has a namespace prefix, the prefix will
   * still be attached.  Note that the attribute list provided will
   * contain only attributes with explicit values (specified or
   * defaulted): #IMPLIED attributes will be omitted.</p>
   *
   * @param name The element type name.
   * @param atts The attributes attached to the element, if any.
   * @exception org.xml.sax.SAXException Any SAX exception, possibly
   *            wrapping another exception.
   * @see #endElement
   * @see org.xml.sax.AttributeList
   */
  public void startElement (String name, AttributeList atts)
    throws SAXException
  {
    // XSLTErrorResources resbundle = XSLTErrorResources.loadResourceBundle(Constants.ERROR_RESOURCES);
    m_whiteSpaceElems.removeAllElements();
    Locator locator = m_processor.m_stylesheetLocatorStack.isEmpty()
                      ? null :
                        ((Locator)m_processor.m_stylesheetLocatorStack.peek());
    int lineNumber = (null != locator) ? locator.getLineNumber() : 0;
    int columnNumber = (null != locator) ? locator.getColumnNumber() : 0;

    // First push namespaces
    m_stylesheet.pushNamespaces(atts);
    String ns = m_stylesheet.getNamespaceFromStack(name);
    String localName = getLocalName(name);
    ElemTemplateElement elem = null;
    int origStackSize = m_elems.size();
    // Issue a warning for unresolved prefixes
    if (null == ns && name.indexOf(":")>0)
      m_processor.warn(XSLTErrorResources.WG_COULD_NOT_RESOLVE_PREFIX, new Object[] {name});
    else if(isXSLTNameSpace(ns))
    {
      if(null != m_stylesheet.m_XSLNameSpaceURL)
        m_stylesheet.m_XSLNameSpaceURL = ns;

      if(false == m_foundStylesheet)
      {
        m_stylesheet.initXSLTKeys();
        m_stylesheet.m_stylesheetRoot.initDefaultRule();
        m_stylesheet.getTemplateList().setIsWrapperless(false);
      }

      Object obj = m_stylesheet.m_elementKeys.get(localName);
      int xslToken = (null != obj) ? ((Integer)obj).intValue() : -2;
      if(!m_inTemplate)
      {
        if(m_foundStylesheet && (Constants.ELEMNAME_IMPORT != xslToken))
        {
          m_foundNotImport = true;
        }

        switch(xslToken)
        {
        case Constants.ELEMNAME_TEMPLATE:
          m_template = new ElemTemplate(m_processor,
                                        m_stylesheet,
                                        name, atts, lineNumber, columnNumber);
          m_elems.push(m_template);
          m_inTemplate = true;
          m_stylesheet.getTemplateList().addTemplate(m_template);
          break;

        case Constants.ELEMNAME_CSSSTYLECONVERSION:
          m_processor.m_translateCSS = true;
          break;

        case Constants.ELEMNAME_EXTENSION:
          {
            if((null != ns) && !(ns.equals("http://xml.apache.org/xslt") ||
                                 ns.equals("http://xsl.lotus.com/") ||
                                 ns.equals("http://xsl.lotus.com")))
            {
              m_processor.warn(XSLTErrorResources.WG_FUNCTIONS_SHOULD_USE_URL, new Object[] {m_processor.m_XSLT4JNameSpaceURL}); //"Old syntax: the functions instruction should use a url of "+m_processor.m_XSLT4JNameSpaceURL);
            }
            // m_processor.handleFunctionsInstruction((Element)child);
          }
          break;

        case Constants.ELEMNAME_VARIABLE:
        case Constants.ELEMNAME_PARAMVARIABLE:
          {
            ElemVariable varelem = (Constants.ELEMNAME_PARAMVARIABLE == xslToken)
                                   ? new ElemParam(m_processor,
                                                   m_stylesheet,
                                                   name, atts,
                                                   lineNumber, columnNumber)
                                     : new ElemVariable(m_processor,
                                                        m_stylesheet,
                                                        name, atts,
                                                        lineNumber, columnNumber);
            m_elems.push(varelem);
            m_inTemplate = true; // fake it out
            m_stylesheet.setTopLevelVariable(varelem);
            varelem.m_isTopLevel = true;
          }
          break;
          // TODO: Remove this??
        case Constants.ELEMNAME_LOCALE:
          m_processor.warn(XSLTErrorResources.ER_FUNCTION_NOT_SUPPORTED); //"xsl:locale not yet supported!");
          break;

        case Constants.ELEMNAME_PRESERVESPACE:
        case Constants.ELEMNAME_STRIPSPACE:
          processStripAndPreserve(name, atts, xslToken,
                                  lineNumber, columnNumber);
          break;

        case Constants.ELEMNAME_KEY:
          {
            ElemEmpty nsContext = new ElemEmpty(m_processor,
                                                m_stylesheet,
                                                name, atts,
                                                lineNumber, columnNumber);
            m_stylesheet.processKeyElement(nsContext, atts);
          }
          break;

        case Constants.ELEMNAME_DEFINEATTRIBUTESET:
          m_inTemplate = true; // fake it out
          ElemAttributeSet attrSet = new ElemAttributeSet(m_processor,
                                                          m_stylesheet,
                                                          name, atts,
                                                          lineNumber,
                                                          columnNumber);
          m_elems.push(attrSet);
          break;

        case Constants.ELEMNAME_INCLUDE:
          processInclude(name, atts);
          break;

        case Constants.ELEMNAME_IMPORT:
          processImport(name, atts);
          break;

        case Constants.ELEMNAME_OUTPUT:
          m_stylesheet.m_stylesheetRoot.processOutputSpec(name, atts);
          break;

        case Constants.ELEMNAME_DECIMALFORMAT:
          ElemDecimalFormat edf = new ElemDecimalFormat(m_processor,
                                                        m_stylesheet,
                                                        name, atts,
                                                        lineNumber, columnNumber);
          m_stylesheet.processDecimalFormatElement(edf, atts);
          break;

        case Constants.ELEMNAME_NSALIAS:
          m_stylesheet.processNSAliasElement(name, atts);
          break;

        case Constants.ELEMNAME_WITHPARAM:
        case Constants.ELEMNAME_ATTRIBUTE:
        case Constants.ELEMNAME_APPLY_TEMPLATES:
        case Constants.ELEMNAME_USE:
        case Constants.ELEMNAME_CHILDREN:
        case Constants.ELEMNAME_CHOOSE:
        case Constants.ELEMNAME_COMMENT:
        case Constants.ELEMNAME_CONSTRUCT:
        case Constants.ELEMNAME_CONTENTS:
        case Constants.ELEMNAME_COPY:
        case Constants.ELEMNAME_COPY_OF:
        case Constants.ELEMNAME_DISPLAYIF:
        case Constants.ELEMNAME_EVAL:
        case Constants.ELEMNAME_EXPECTEDCHILDREN:
        case Constants.ELEMNAME_FOREACH:
        case Constants.ELEMNAME_IF:
        case Constants.ELEMNAME_CALLTEMPLATE:
        case Constants.ELEMNAME_MESSAGE:
        case Constants.ELEMNAME_NUMBER:
        case Constants.ELEMNAME_OTHERWISE:
        case Constants.ELEMNAME_PI:
        case Constants.ELEMNAME_REMOVEATTRIBUTE:
        case Constants.ELEMNAME_SORT:
        case Constants.ELEMNAME_TEXT:
        case Constants.ELEMNAME_VALUEOF:
        case Constants.ELEMNAME_WHEN:
        case Constants.ELEMNAME_ELEMENT:
        case Constants.ELEMNAME_COUNTER:
        case Constants.ELEMNAME_COUNTERS:
        case Constants.ELEMNAME_COUNTERINCREMENT:
        case Constants.ELEMNAME_COUNTERRESET:
        case Constants.ELEMNAME_COUNTERSCOPE:
        case Constants.ELEMNAME_APPLY_IMPORTS:
          throw new SAXException(XSLMessages.createMessage(XSLTErrorResources.ER_NOT_ALLOWED_INSIDE_STYLESHEET, new Object[]{name})); //"(StylesheetHandler) "+name+" not allowed inside a stylesheet!");
          // break;

        case Constants.ELEMNAME_STYLESHEET:
          m_stylesheet.getTemplateList().setIsWrapperless(false);
          m_foundStylesheet = true;
          int nAttrs = atts.getLength();
          // Not currently used - boolean didSpecifyIndent = false;
          boolean didSpecifyVersion = false; // Sean Timm, STimm@mailgo.com
          for(int i = 0; i < nAttrs; i++)
          {
            String aname = atts.getName(i);
            if(aname.equals("result-ns"))
            {
              throw new SAXException(XSLMessages.createMessage(XSLTErrorResources.ER_RESULTNS_NOT_SUPPORTED, null)); //"result-ns no longer supported!  Use xsl:output instead.");
            }
            else if(aname.equals(Constants.ATTRNAME_DEFAULTSPACE))
            {
              throw new SAXException(XSLMessages.createMessage(XSLTErrorResources.ER_DEFAULTSPACE_NOT_SUPPORTED, null)); //"default-space no longer supported!  Use xsl:strip-space or xsl:preserve-space instead.");
            }
            else if(aname.equals(Constants.ATTRNAME_EXCLUDE_RESULT_PREFIXES))
            {
              StringToStringTable excluded = m_stylesheet.getExcludeResultPrefixes();
              excluded = m_stylesheet.processExcludeResultPrefixes(atts.getValue(i), excluded);
              m_stylesheet.setExcludeResultPrefixes(excluded);
            }
            else if(aname.equals(Constants.ATTRNAME_EXTENSIONELEMENTPREFIXES))
            {
              // BEGIN SANJIVA CODE
              StringTokenizer tokenizer =
                                         new StringTokenizer (atts.getValue (i), " \t\n\r", false);
              while(tokenizer.hasMoreTokens ())
              {
                String prefix = tokenizer.nextToken ();
                // SANJIVA: ask Scott: is the line below correct?
                String extns = m_stylesheet.getNamespaceForPrefixFromStack (prefix);
                if (null == extns)
                  extns = "";
                ExtensionNSHandler nsh = new ExtensionNSHandler (m_processor, extns);
                m_stylesheet.addExtensionNamespace (extns, nsh);
              }
              // END SANJIVA CODE
            }
            else if(aname.equals("id"))
            {
              //
            }
            else if(aname.equals("indent-result"))
            {
              throw new SAXException(XSLMessages.createMessage(XSLTErrorResources.ER_INDENTRESULT_NOT_SUPPORTED,null)); //"indent-result no longer supported!  Use xsl:output instead.");
            }
            else if(aname.equals("version"))
            {
              String versionStr = atts.getValue(i);
              m_stylesheet.m_XSLTVerDeclared = Double.valueOf(versionStr).doubleValue();
              didSpecifyVersion = true; // Sean Timm, STimm@mailgo.com
            }
            else if(!(isAttrOK(aname, atts, i) || processSpaceAttr(aname, atts, i)))
            {
              if(false == m_stylesheet.getTemplateList().getIsWrapperless())
              {
                throw new SAXException(XSLMessages.createMessage(XSLTErrorResources.ER_ILLEGAL_ATTRIB, new Object[]{name, aname})); //"(StylesheetHandler) "+name+" has an illegal attribute: "+aname);
              }
            }

            if(!m_stylesheet.m_namespaces.empty())
            {
              m_stylesheet.m_namespaceDecls = (org.apache.xalan.xpath.xml.NameSpace)m_stylesheet.m_namespaces.peek();
            }

            /*
            default:
            if((null != ns) && (ns.equalsIgnoreCase(m_processor.m_XSLNameSpaceURL) ||
            ns.equalsIgnoreCase(m_processor.m_XML4JNameSpaceURL)))
            {
            m_processor.warn(name
            +" unknown XSL instruction inside context of the stylesheet element!");
            }
            // be tolerant of other namespaces
            break;
            */
          }
          // Thanks to "Timm, Sean" <STimm@mailgo.com>
          if(!didSpecifyVersion)
          {
            m_processor.warn(XSLTErrorResources.WG_STYLESHEET_REQUIRES_VERSION_ATTRIB, null);
            // throw new SAXException(XSLMessages.createMessage(XSLTErrorResources.ER_STYLESHEET_REQUIRES_VERSION_ATTRIB, null)); // "xsl:stylesheet requires a 'version' attribute!");
          }

          break;
        default:
          throw new SAXException(XSLMessages.createMessage(XSLTErrorResources.ER_UNKNOWN_XSL_ELEM, new Object[]{localName})); //"Unknown XSL element: "+localName);
        }
      }
      else
      {
        switch(xslToken)
        {
        case Constants.ELEMNAME_APPLY_TEMPLATES:
          elem = new ElemApplyTemplates(m_processor,
                                        m_stylesheet,
                                        name, atts, lineNumber, columnNumber);
          break;

        case Constants.ELEMNAME_CALLTEMPLATE:
          elem = new ElemCallTemplate(m_processor,
                                      m_stylesheet,
                                      name, atts, lineNumber, columnNumber);
          break;

        case Constants.ELEMNAME_WITHPARAM:
          elem = new ElemWithParam(m_processor,
                                   m_stylesheet,
                                   name, atts, lineNumber, columnNumber);
          break;

        case Constants.ELEMNAME_FOREACH:
          elem = new ElemForEach(m_processor,
                                 m_stylesheet,
                                 name, atts, lineNumber, columnNumber);
          break;

        case Constants.ELEMNAME_SORT:
          try
          {
            ElemForEach foreach = (ElemForEach)m_elems.peek();

            ElemSort sortElem = new ElemSort(m_processor,
                                             m_stylesheet,
                                             name, atts, lineNumber, columnNumber);

            if(null == foreach.m_sortElems)
            {
              foreach.m_sortElems = new Vector();
            }
            foreach.m_sortElems.addElement(sortElem);
            sortElem.m_parentNode = foreach;
          }
          catch(ClassCastException cce)
          {
            throw new SAXException(XSLMessages.createMessage(XSLTErrorResources.ER_BAD_XSLSORT_USE, null)); //"(StylesheetHandler) "+"xsl:sort can only be used with xsl:apply-templates or xsl:for-each.");
          }
          break;

        case Constants.ELEMNAME_APPLY_IMPORTS:
          elem = new ElemApplyImport(m_processor,
                                     m_stylesheet,
                                     name, atts, lineNumber, columnNumber);
          break;

        case Constants.ELEMNAME_VALUEOF:
          elem = new ElemValueOf(m_processor,
                                 m_stylesheet,
                                 name, atts, lineNumber, columnNumber);
          break;

        case Constants.ELEMNAME_NUMBER:
          elem = new ElemNumber(m_processor,
                                m_stylesheet,
                                name, atts, lineNumber, columnNumber);
          break;

        case Constants.ELEMNAME_VARIABLE:
          elem = new ElemVariable(m_processor,
                                  m_stylesheet,
                                  name, atts, lineNumber, columnNumber);
          break;

        case Constants.ELEMNAME_PARAMVARIABLE:
          elem = new ElemParam(m_processor,
                               m_stylesheet,
                               name, atts, lineNumber, columnNumber);
          break;

        case Constants.ELEMNAME_IF:
          elem = new ElemIf(m_processor,
                            m_stylesheet,
                            name, atts, lineNumber, columnNumber);
          break;

        case Constants.ELEMNAME_FALLBACK:
          elem = new ElemFallback(m_processor,
                            m_stylesheet,
                            name, atts, lineNumber, columnNumber);
          break;

        case Constants.ELEMNAME_CHOOSE:
          elem = new ElemChoose(m_processor,
                                m_stylesheet,
                                name, atts, lineNumber, columnNumber);
          break;

        case Constants.ELEMNAME_WHEN:
          {
            ElemTemplateElement parent = (ElemTemplateElement)m_elems.peek();
            if(Constants.ELEMNAME_CHOOSE == parent.getXSLToken())
            {
              ElemTemplateElement lastChild = (ElemTemplateElement)parent.getLastChild();
              if((null == lastChild) ||
                 (Constants.ELEMNAME_WHEN == lastChild.getXSLToken()))
              {
                elem = new ElemWhen(m_processor,
                                    m_stylesheet,
                                    name, atts, lineNumber, columnNumber);
              }
              else
              {
                throw new SAXException(XSLMessages.createMessage(XSLTErrorResources.ER_MISPLACED_XSLWHEN, null)); //"(StylesheetHandler) "+"misplaced xsl:when!");
              }
            }
            else
            {
              throw new SAXException(XSLMessages.createMessage(XSLTErrorResources.ER_XSLWHEN_NOT_PARENTED_BY_XSLCHOOSE, null)); //"(StylesheetHandler) "+"xsl:when not parented by xsl:choose!");
            }
          }
          break;

        case Constants.ELEMNAME_OTHERWISE:
          {
            ElemTemplateElement parent = (ElemTemplateElement)m_elems.peek();
            if(Constants.ELEMNAME_CHOOSE == parent.getXSLToken())
            {
              ElemTemplateElement lastChild = (ElemTemplateElement)parent.getLastChild();
              if((null == lastChild) ||
                 (Constants.ELEMNAME_WHEN == lastChild.getXSLToken()))
              {
                elem = new ElemOtherwise(m_processor,
                                         m_stylesheet,
                                         name, atts, lineNumber, columnNumber);
              }
              else
              {
                throw new SAXException(XSLMessages.createMessage(XSLTErrorResources.ER_MISPLACED_XSLOTHERWISE, null)); //"(StylesheetHandler) "+"misplaced xsl:otherwise!");
              }
            }
            else
            {
              throw new SAXException(XSLMessages.createMessage(XSLTErrorResources.ER_XSLOTHERWISE_NOT_PARENTED_BY_XSLCHOOSE, null)); //"(StylesheetHandler) "+"xsl:otherwise not parented by xsl:choose!");
            }
          }
          break;

        case Constants.ELEMNAME_COPY_OF:
          elem = new ElemCopyOf(m_processor,
                                m_stylesheet,
                                name, atts, lineNumber, columnNumber);
          break;

        case Constants.ELEMNAME_COPY:
          elem = new ElemCopy(m_processor,
                              m_stylesheet,
                              name, atts, lineNumber, columnNumber);
          break;

        case Constants.ELEMNAME_TEXT:
          // Just push the element on the stack to signal
          // that space should be preserved.
          m_elems.push(new ElemText(m_processor,
                                    m_stylesheet,
                                    name, atts, lineNumber, columnNumber));
          break;

        case Constants.ELEMNAME_USE:
          elem = new ElemUse(m_processor,
                             m_stylesheet,
                             name, atts, lineNumber, columnNumber);
          break;

        case Constants.ELEMNAME_ATTRIBUTE:
          elem = new ElemAttribute(m_processor,
                                   m_stylesheet,
                                   name, atts, lineNumber, columnNumber);
          break;

        case Constants.ELEMNAME_ELEMENT:
          elem = new ElemElement(m_processor,
                                 m_stylesheet,
                                 name, atts, lineNumber, columnNumber);
          break;

        case Constants.ELEMNAME_PI:
          elem = new ElemPI(m_processor,
                            m_stylesheet,
                            name, atts, lineNumber, columnNumber);
          break;

        case Constants.ELEMNAME_COMMENT:
          elem = new ElemComment(m_processor,
                                 m_stylesheet,
                                 name, atts, lineNumber, columnNumber);
          break;

        case Constants.ELEMNAME_MESSAGE:
          elem = new ElemMessage(m_processor,
                                 m_stylesheet,
                                 name, atts, lineNumber, columnNumber);

          break;

        case Constants.ELEMNAME_DECIMALFORMAT:
          elem = new ElemDecimalFormat(m_processor,
                                       m_stylesheet,
                                       name, atts, lineNumber, columnNumber);
          break;

        case Constants.ELEMNAME_TEMPLATE:
        case Constants.ELEMNAME_LOCALE:
        case Constants.ELEMNAME_DEFINEATTRIBUTESET:
        case Constants.ELEMNAME_DEFINESCRIPT:
        case Constants.ELEMNAME_EXTENSION:
        case Constants.ELEMNAME_EXTENSIONHANDLER:
        case Constants.ELEMNAME_KEY:
        case Constants.ELEMNAME_IMPORT:
        case Constants.ELEMNAME_INCLUDE:
        case Constants.ELEMNAME_PRESERVESPACE:
        case Constants.ELEMNAME_STRIPSPACE:
          throw new SAXException(XSLMessages.createMessage(XSLTErrorResources.ER_NOT_ALLOWED_INSIDE_TEMPLATE, new Object[]{name})); //"(StylesheetHandler) "+name+" is not allowed inside a template!");
          // break;
        default:
          // If this stylesheet is declared to be of a higher version than the one
          // supported, don't flag an error.
          if(XSLTEngineImpl.m_XSLTVerSupported >= m_stylesheet.m_XSLTVerDeclared)
          {
            throw new SAXException(XSLMessages.createMessage(XSLTErrorResources.ER_UNKNOWN_XSL_ELEM, new Object[]{localName})); //"Unknown XSL element: "+localName);
          }
        }
      }
    }
    // BEGIN SANJIVA CODE
    else if (!m_inTemplate && (null != ns) &&
             (ns.equals ("http://xml.apache.org/xslt")
              || ns.equals("http://xsl.lotus.com/")
              || ns.equals("http://xsl.lotus.com")))
    {
      if (localName.equals ("component"))
      {
        String prefix = null;
        String elements = null;
        String functions = null;
        int nAttrs = atts.getLength ();
        for (int i = 0; i < nAttrs; i++)
        {
          String aname = atts.getName (i);
          if (aname.equals ("prefix"))
          {
            prefix = atts.getValue (i);
          }
          else if (aname.equals ("elements"))
          {
            elements = atts.getValue (i);
          }
          else if (aname.equals ("functions"))
          {
            functions = atts.getValue (i);
          }
          else if(!isAttrOK(aname, atts, i))
          {
            m_stylesheet.error(XSLTErrorResources.ER_ILLEGAL_ATTRIBUTE, new Object[] {name, aname}); //name+" has an illegal attribute: "+aname);
          }
        }
        if (prefix == null)
        {
          throw new SAXException (XSLMessages.createMessage(XSLTErrorResources.ER_MISSING_PREFIX_ATTRIB, new Object[]{name})); //"(StylesheetHandler) " + name +
          //" attribute 'prefix' is missing");
        }
        // SCOTT: is the line below correct?
        String extns = m_stylesheet.getNamespaceForPrefixFromStack (prefix);
        if (null == extns)
        {
          extns = "";
        }
        ExtensionNSHandler nsh = m_stylesheet.lookupExtensionNSHandler (extns);
        if (nsh == null)
        {
          // xsl:extension-element-prefixes might not be known yet,
          // see extend17.xsl.
          // if (elements != null)
          //  throw new SAXException (XSLMessages.createMessage(XSLTErrorResources.ER_UNKNOWN_EXT_NS_PREFIX, new Object[]{name, prefix})); //"(StylesheetHandler) " + name +

          if (null == extns)
            extns = "";
          nsh = new ExtensionNSHandler (m_processor, extns);
          m_stylesheet.addExtensionNamespace (extns, nsh);
        }

        nsh.setScript("javaclass", extns, null); // as default
        if (elements != null)
        {
          nsh.setElements (elements);
        }
        if (functions != null)
        {
          nsh.setFunctions (functions);
        }
        m_LXSLTExtensionNSH = nsh; // hang on to it for processing
        // endElement on lxslt:script
      }
      else if (localName.equals ("script"))
      {
        // process this in end element so that I can see whether I had
        // a body as well. The default pushing logic will save the
        // attributes for me. The body will be accumulated into the
        // following string buffer
        m_inLXSLTScript = true;
        m_LXSLTScriptBody = new StringBuffer ();
        int nAttrs = atts.getLength ();
        for (int i = 0; i < nAttrs; i++)
        {
          String aname = atts.getName (i);
          if (aname.equals ("lang"))
          {
            m_LXSLTScriptLang = atts.getValue (i);
          }
          else if (aname.equals ("src"))
          {
            m_LXSLTScriptSrcURL = atts.getValue (i);
          }
          else if(!isAttrOK(aname, atts, i))
          {
            m_stylesheet.error(XSLTErrorResources.ER_ILLEGAL_ATTRIBUTE, new Object[] {name, aname}); //name+" has an illegal attribute: "+aname);
          }
        }
      }
      else
      {
        // other xalan: element. Not my business.
      }
    }
    // END SANJIVA CODE
    else
    {
      if(!m_inTemplate && !m_foundStylesheet)
      {
        int nAttrs = atts.getLength();
        // Not currently used - boolean didSpecifyIndent = false;
        boolean didSpecifyVersion = false; // Sean Timm, STimm@mailgo.com
        for(int i = 0; i < nAttrs; i++)
        {
          String aname = atts.getName(i);

          String ans = m_stylesheet.getNamespaceFromStack(aname);

          if(isXSLTNameSpace(ans))
          {
            String lname = getLocalName(aname);
            if(lname.equals("version"))
            {
              String versionStr = atts.getValue(i);
              m_stylesheet.m_XSLTVerDeclared = Double.valueOf(versionStr).doubleValue();
              didSpecifyVersion = true; // Sean Timm, STimm@mailgo.com
            }
          }
        }
        if(!didSpecifyVersion)
        {
           m_processor.warn(XSLTErrorResources.WG_STYLESHEET_REQUIRES_VERSION_ATTRIB, null);
          //throw new SAXException(XSLMessages.createMessage(XSLTErrorResources.ER_STYLESHEET_REQUIRES_VERSION_ATTRIB, null)); // "xsl:stylesheet requires a 'version' attribute!");
        }

        elem = initWrapperless(name, atts, lineNumber, columnNumber);
      }
      else
      {
        // BEGIN SANJIVA CODE
        // is this an extension element call?
        ExtensionNSHandler nsh = null;
        String theExtns = null;

        if(null != ns)
        {
          // Yuck, first we have to look at the current attributes for
          // a xsl:extension-element-prefixes attribute.
          int nAtts = atts.getLength();
          for(int i = 0; i < nAtts; i++)
          {
            String aname = atts.getName(i);
            int indexOfNSSep = aname.indexOf(':');
            String prefix;
            if(indexOfNSSep > 0)
            {
              prefix = aname.substring(0, indexOfNSSep);
              if(!prefix.equals("xmlns"))
              {
                String attrNS = m_stylesheet.getNamespaceForPrefixFromStack (prefix);
                if((null != attrNS)
                   && attrNS.equals( m_stylesheet.m_XSLNameSpaceURL ))
                {

                  String nm = aname.substring(indexOfNSSep+1);
                  if(nm.equals(Constants.ATTRNAME_EXTENSIONELEMENTPREFIXES))
                  {
                    String qnames = atts.getValue(i);
                    StringTokenizer tokenizer = new StringTokenizer(qnames, " \t\n\r", false);
                    for(int k = 0; tokenizer.hasMoreTokens(); k++)
                    {
                      String extnsPrefix = tokenizer.nextToken();
                      String extns = m_stylesheet.getNamespaceForPrefixFromStack (extnsPrefix);
                      if((ns != null) && ns.equals(extns))
                      {
                        nsh = new ExtensionNSHandler (m_processor, extns);
                        theExtns = extns;
                        break;
                      }
                    }
                  }
                }
              }
            }
          }
          // now we have to look up the stack...
          if(null == nsh)
          {
            ElemTemplateElement parent = (ElemTemplateElement)m_elems.peek();
            while(null != parent)
            {
              int tok = parent.getXSLToken();
              String[] extensionElementPrefixes = null;
              if(Constants.ELEMNAME_EXTENSIONCALL == tok)
              {
                ElemExtensionCall extcall = (ElemExtensionCall)parent;
                extensionElementPrefixes = extcall.m_extensionElementPrefixes;

              }
              else if(Constants.ELEMNAME_LITERALRESULT == tok)
              {
                ElemLiteralResult extcall = (ElemLiteralResult)parent;
                extensionElementPrefixes = extcall.m_extensionElementPrefixes;
              }
              if(null != extensionElementPrefixes)
              {
                int n = extensionElementPrefixes.length;
                for(int i = 0; i < n; i++)
                {
                  String extnsPrefix = extensionElementPrefixes[i];
                  String extns = m_stylesheet.getNamespaceForPrefixFromStack (extnsPrefix);
                  if((ns != null) && ns.equals(extns))
                  {
                    nsh = new ExtensionNSHandler (m_processor, extns);
                    theExtns = extns;
                    break;
                  }
                }
              }
              parent = parent.m_parentNode;
            }
          }
        }

        if ((ns != null) && (null == nsh))
        {
          nsh = m_stylesheet.lookupExtensionNSHandler(ns);
          theExtns = ns;
        }

        if (nsh != null)
        {
          if(null == theExtns)
            theExtns = "";

          elem = new ElemExtensionCall (m_processor,
                                        m_stylesheet,
                                        // nsh,
                                        theExtns,
                                        nsh.scriptLang,
                                        nsh.scriptSrcURL,
                                        nsh.scriptSrc,
                                        name,
                                        localName,
                                        atts, lineNumber, columnNumber);
        }
        else
        {
          elem = new ElemLiteralResult(m_processor,
                                       m_stylesheet,
                                       name,
                                       atts, lineNumber, columnNumber);
        }
        // BEGIN SANJIVA CODE
      }
      // END SANJIVA CODE
    }
    if(m_inTemplate && (null != elem))
    {
      if(!m_elems.empty())
      {
        ElemTemplateElement parent = (ElemTemplateElement)m_elems.peek();
        parent.appendChild(elem);
      }
      m_elems.push(elem);
    }
    // If for some reason something didn't get pushed, push an empty
    // object.
    if(origStackSize == m_elems.size())
    {
      m_elems.push(new ElemEmpty(m_processor,
                                 m_stylesheet,
                                 name, atts, lineNumber, columnNumber));
    }
  }

  /**
   * Init the wrapperless template
   */
  private ElemTemplateElement initWrapperless (String name, AttributeList atts,
                                               int lineNumber, int columnNumber)
    throws SAXException
  {
    m_stylesheet.initXSLTKeys();
    m_stylesheet.m_stylesheetRoot.initDefaultRule();
    MutableAttrListImpl templateAttrs
      = new MutableAttrListImpl();
    templateAttrs.addAttribute("name", "CDATA", "simple");
    m_template = new ElemTemplate(m_processor,
                                  m_stylesheet,
                                  "xsl:template",
                                  templateAttrs, lineNumber, columnNumber);
    ElemTemplateElement elem = new ElemLiteralResult(m_processor,
                                                     m_stylesheet,
                                                     name,
                                                     atts, lineNumber, columnNumber);
    m_template.appendChild(elem);
    m_inTemplate = true;

    m_stylesheet.getTemplateList().setWrapperlessTemplate(m_template);
    m_stylesheet.getTemplateList().setIsWrapperless(true);
    m_foundStylesheet = true;
    if(name.equals("HTML"))
    {
      m_stylesheet.m_stylesheetRoot.m_indentResult = true;
      m_stylesheet.m_stylesheetRoot.setOutputMethod("html");
    }
    return elem;
  }

  /**
   * Process xsl:import.
   */
  private void processImport(String name, AttributeList atts)
    throws SAXException
  {
    int nAttrs = atts.getLength();
    boolean foundIt = false;
    for(int i = 0; i < nAttrs; i++)
    {
      String aname = atts.getName(i);
      if(aname.equals(Constants.ATTRNAME_HREF))
      {
        foundIt = true;

        if(m_foundNotImport)
        {
          throw new SAXException(XSLMessages.createMessage(XSLTErrorResources.ER_IMPORTS_AS_FIRST_ELEM, null)); //"(StylesheetHandler) "+"Imports can only occur as the first elements in the stylesheet!");
        }

        String saved_XSLNameSpaceURL = m_stylesheet.m_XSLNameSpaceURL;

        String href = atts.getValue(i);
        URL hrefUrl = m_processor.getURLFromString(href, m_stylesheet.m_baseIdent);
        if(stackContains(m_stylesheet.m_stylesheetRoot.m_importStack, hrefUrl))
        {
          throw new SAXException(XSLMessages.createMessage(XSLTErrorResources.ER_IMPORTING_ITSELF, new Object[] {hrefUrl})); //"(StylesheetHandler) "+hrefUrl+" is directly or indirectly importing itself!");
        }

        m_stylesheet.m_stylesheetRoot.m_importStack.push(hrefUrl);

        try
        {
          String[] stringHolder =
          {
            null};

          Stylesheet importedStylesheet
            = new Stylesheet(m_stylesheet.m_stylesheetRoot,
                             m_processor, m_stylesheet.m_baseIdent);
          StylesheetHandler tp = new StylesheetHandler(m_processor, importedStylesheet);

          importedStylesheet.m_baseIdent = hrefUrl.toExternalForm();

          m_processor.parseXML(hrefUrl,
                               tp, importedStylesheet);

          // I'm going to insert the elements in backwards order,
          // so I can walk them 0 to n.
          m_stylesheet.m_imports.insertElementAt(importedStylesheet, 0);
          importedStylesheet.m_stylesheetParent = m_stylesheet;
        }
        catch(MalformedURLException mue)
        {
          throw new SAXException(XSLMessages.createMessage(XSLTErrorResources.ER_BAD_STYLESHEET_URL, new Object[] {atts.getValue(i)}), mue); //"Stylesheet URL is bad: "+atts.getValue(i), mue);
        }
        catch(FileNotFoundException fnfe)
        {
          throw new SAXException(XSLMessages.createMessage(XSLTErrorResources.ER_FILE_NOT_FOUND, new Object[] {atts.getValue(i)}), fnfe); //"Stylesheet file was not found: "+atts.getValue(i), fnfe);
        }
        catch(IOException ioe)
        {
          throw new SAXException(XSLMessages.createMessage(XSLTErrorResources.ER_IOEXCEPTION, new Object[] {atts.getValue(i)}), ioe); //"Had IO Exception with stylesheet file: "+atts.getValue(i), ioe);
        }

        m_stylesheet.m_stylesheetRoot.m_importStack.pop();

        m_stylesheet.m_XSLNameSpaceURL = saved_XSLNameSpaceURL;
      }
      else if(!isAttrOK(aname, atts, i))
      {
        m_stylesheet.error(XSLTErrorResources.ER_ILLEGAL_ATTRIBUTE, new Object[] {name, aname}); //name+" has an illegal attribute: "+aname);
      }
    }
    if(!foundIt)
    {
      throw new SAXException(XSLMessages.createMessage(XSLTErrorResources.ER_NO_HREF_ATTRIB, new Object[] {name})); //"(StylesheetHandler) "+"Could not find href attribute for "+name);
    }
  }

  /**
   * Test to see if the stack contains the given URL.
   */
  private boolean stackContains(Stack stack, URL url)
  {
    int n = stack.size();
    boolean contains = false;
    for(int i = 0; i < n; i++)
    {
      URL url2 = (URL)stack.elementAt(i);
      if(url2.toString().equals(url.toString()))
      {
        contains = true;
        break;
      }
    }
    return contains;
  }

  /**
   * Process xsl:include.
   */
  private void processInclude(String name, AttributeList atts)
    throws SAXException
  {
    int nAttrs = atts.getLength();
    boolean foundIt = false;
    for(int i = 0; i < nAttrs; i++)
    {
      String aname = atts.getName(i);
      if(aname.equals(Constants.ATTRNAME_HREF))
      {
        foundIt = true;

        // Save state, so this class can be reused.
        Stack saved_elems = m_elems;
        m_elems = new Stack();
        ElemTemplate saved_template = m_template;
        m_template = null;
        ElemTemplateElement saved_lastPopped = m_lastPopped;
        m_lastPopped = null;
        boolean saved_inTemplate = m_inTemplate;
        m_inTemplate = false;
        boolean saved_foundStylesheet = m_foundStylesheet;
        m_foundStylesheet = false;
        String saved_XSLNameSpaceURL = m_stylesheet.m_XSLNameSpaceURL;
        boolean saved_foundNotImport = m_foundNotImport;
        m_foundNotImport = false;
        NameSpace saved_namespaceDecls = m_stylesheet.m_namespaceDecls;
        m_stylesheet.m_namespaceDecls = null;
        Stack saved_namespaces = m_stylesheet.m_namespaces;
        m_stylesheet.m_namespaces = new Stack();


        String href = atts.getValue(i);
        URL hrefUrl
          = m_processor.getURLFromString(href,
                                         ((URL)m_stylesheet.m_includeStack.peek()).toString());

        if(stackContains(m_stylesheet.m_includeStack, hrefUrl))
        {
          throw new SAXException(XSLMessages.createMessage(XSLTErrorResources.ER_IMPORTING_ITSELF, new Object[] {hrefUrl})); //"(StylesheetHandler) "+hrefUrl+" is directly or indirectly including itself!");
        }

        m_stylesheet.m_includeStack.push(hrefUrl);
        // m_includeBase =

        try
        {
          m_processor.parseXML(hrefUrl,
                               this, m_stylesheet);
        }
        catch(IOException ioe)
        {
          throw new SAXException(XSLMessages.createMessage(XSLTErrorResources.ER_PROCESSINCLUDE_ERROR, null), ioe);//"StylesheetHandler.processInclude error", ioe);
        }

        m_stylesheet.m_includeStack.pop();

        m_elems = saved_elems;
        m_template = saved_template;
        m_lastPopped = saved_lastPopped;
        m_inTemplate = saved_inTemplate;
        m_foundStylesheet = saved_foundStylesheet;
        m_stylesheet.m_XSLNameSpaceURL = saved_XSLNameSpaceURL;
        m_foundNotImport = saved_foundNotImport;
        m_stylesheet.m_namespaceDecls = saved_namespaceDecls;
        m_stylesheet.m_namespaces = saved_namespaces;
      }
      else if(!isAttrOK(aname, atts, i))
      {
        m_stylesheet.error(XSLTErrorResources.ER_ILLEGAL_ATTRIBUTE, new Object[] {name, aname}); //name+" has an illegal attribute: "+aname);
      }
    }
    if(!foundIt)
    {
      throw new SAXException(XSLMessages.createMessage(XSLTErrorResources.ER_NO_HREF_ATTRIB, new Object[] {name})); //"(StylesheetHandler) "+"Could not find href attribute for "+name);
    }
  }

  /**
   * Receive notification of the end of an element.
   *
   * <p>The SAX parser will invoke this method at the end of every
   * element in the XML document; there will be a corresponding
   * startElement() event for every endElement() event (even when the
   * element is empty).</p>
   *
   * <p>If the element name has a namespace prefix, the prefix will
   * still be attached to the name.</p>
   *
   * @param name The element type name
   * @exception org.xml.sax.SAXException Any SAX exception, possibly
   *            wrapping another exception.
   */
  public void endElement (String name)
    throws SAXException
  {
    m_stylesheet.popNamespaces();
    m_lastPopped = (ElemTemplateElement)m_elems.pop();
    m_lastPopped.m_finishedConstruction = true;
    int tok = m_lastPopped.getXSLToken();
    if(Constants.ELEMNAME_TEMPLATE == tok)
    {
      m_inTemplate = false;
    }
    else if((Constants.ELEMNAME_PARAMVARIABLE == tok) ||
            Constants.ELEMNAME_VARIABLE == tok)
    {
      ElemVariable var = (ElemVariable)m_lastPopped;
      if(var.m_isTopLevel)
      {
        // Top-level param or variable
        m_inTemplate = false;
      }
    }
    else if(Constants.ELEMNAME_DEFINEATTRIBUTESET == tok)
    {
      m_inTemplate = false;
    }
    // BEGIN SANJIVA CODE
    if (m_inLXSLTScript)
    {
      if (m_LXSLTScriptLang == null)
      {
        throw new SAXException(XSLMessages.createMessage(XSLTErrorResources.ER_MISSING_LANG_ATTRIB, new Object[] {name})); //"(StylesheetHandler) " + name +
      }
      if (m_LXSLTExtensionNSH == null)
      {
        throw new SAXException(XSLMessages.createMessage(XSLTErrorResources.ER_MISSING_CONTAINER_ELEMENT_COMPONENT, new Object[] {name})); //"(StylesheetHandler) misplaced " + name +
        // " element?? Missing container element " +
        // "'component'");
      }
      m_LXSLTExtensionNSH.setScript (m_LXSLTScriptLang, m_LXSLTScriptSrcURL,
                                     m_LXSLTScriptBody.toString ());

      // reset state
      m_inLXSLTScript = false;
      m_LXSLTScriptLang = null;
      m_LXSLTScriptSrcURL = null;
      m_LXSLTScriptBody = null;
      m_LXSLTExtensionNSH = null;
    }
    else if(null != m_LXSLTExtensionNSH)
    {
      // reset state
      m_inLXSLTScript = false;
      m_LXSLTScriptLang = null;
      m_LXSLTScriptSrcURL = null;
      m_LXSLTScriptBody = null;
      m_LXSLTExtensionNSH = null;
    }
    // END SANJIVA CODE
  }

  /**
   * Returns whether the specified <var>ch</var> conforms to the XML 1.0 definition
   * of whitespace.  Refer to <A href="http://www.w3.org/TR/1998/REC-xml-19980210#NT-S">
   * the definition of <CODE>S</CODE></A> for details.
   * @param   ch      Character to check as XML whitespace.
   * @return          =true if <var>ch</var> is XML whitespace; otherwise =false.
   */
  public static boolean isSpace(char ch)
  {
    return (ch == 0x20) || (ch == 0x09) || (ch == 0xD) || (ch == 0xA);
  }

  /**
   * Tell if the string is whitespace.
   * @param   string      String to be trimmed.
   * @return              The trimmed string.
   */
  public boolean isWhiteSpace(char ch[], int start, int length)
  {
    boolean isWhiteSpace = true;
    int end = start+length;
    for(int s = start;  s < end;  s++)
    {
      if (!isSpace(ch[s]))
      {
        isWhiteSpace = false;
        break;
      }
    }
    return isWhiteSpace;
  }


  /**
   * Receive notification of character data.
   *
   * <p>The Parser will call this method to report each chunk of
   * character data.  SAX parsers may return all contiguous character
   * data in a single chunk, or they may split it into several
   * chunks; however, all of the characters in any single event
   * must come from the same external entity, so that the Locator
   * provides useful information.</p>
   *
   * <p>The application must not attempt to read from the array
   * outside of the specified range.</p>
   *
   * <p>Note that some parsers will report whitespace using the
   * ignorableWhitespace() method rather than this one (validating
   * parsers must do so).</p>
   *
   * @param ch The characters from the XML document.
   * @param start The start position in the array.
   * @param length The number of characters to read from the array.
   * @exception org.xml.sax.SAXException Any SAX exception, possibly
   *            wrapping another exception.
   * @see #ignorableWhitespace
   * @see org.xml.sax.Locator
   */
  public void characters (char ch[], int start, int length)
    throws SAXException
  {
    if(m_inTemplate)
    {
      ElemTemplateElement parent = (ElemTemplateElement)m_elems.peek();
      boolean preserveSpace = false;
      boolean disableOutputEscaping = false;
      if(Constants.ELEMNAME_TEXT == parent.getXSLToken())
      {
        disableOutputEscaping = ((ElemText)parent).m_disableOutputEscaping;
        parent = (ElemTemplateElement)m_elems.elementAt(m_elems.size()-2);
        preserveSpace = true;
      }
      Locator locator = m_processor.m_stylesheetLocatorStack.isEmpty()
                        ? null :
                          ((Locator)m_processor.m_stylesheetLocatorStack.peek());
      int lineNumber = (null != locator) ? locator.getLineNumber() : 0;
      int columnNumber = (null != locator) ? locator.getColumnNumber() : 0;
      ElemTextLiteral elem = new ElemTextLiteral(m_processor,
                                                 m_stylesheet,
                                                 ch, start, length,
                                                 false, preserveSpace,
                                                 disableOutputEscaping,
                                                 lineNumber, columnNumber);
      boolean isWhite = isWhiteSpace(ch, start, length);
      if(preserveSpace || (!preserveSpace && !isWhite))
      {
        while(!m_whiteSpaceElems.isEmpty())
        {
          ElemTextLiteral whiteElem = (ElemTextLiteral)m_whiteSpaceElems.pop();
          parent.appendChild(whiteElem);
        }
        parent.appendChild(elem);
      }
      else if(isWhite)
      {
        boolean shouldPush = true;

        // Check out the last child added to the stylesheet tree to
        // see if it is a literal text result element.
        Node last = parent.getLastChild();

        if(null != last)
        {
          ElemTemplateElement lastElem = (ElemTemplateElement)last;
          // If it was surrounded by xsl:text, it will count as an element.
          boolean isPrevCharData
            = Constants.ELEMNAME_TEXTLITERALRESULT == lastElem.getXSLToken();

          boolean isLastPoppedXSLText = (m_lastPopped != null) &&
              (Constants.ELEMNAME_TEXT == m_lastPopped.getXSLToken());

          if(isPrevCharData && !isLastPoppedXSLText)
          {
            parent.appendChild(elem);
            shouldPush = false;
          }
        }
        if(shouldPush)
          m_whiteSpaceElems.push(elem);
      }
    }
    // BEGIN SANJIVA CODE
    else if (m_inLXSLTScript)
    {
      m_LXSLTScriptBody.append (ch, start, length);
    }
    // END SANJIVA CODE
    // TODO: Flag error if text inside of stylesheet
//    m_lastPopped = null;            // Reset this so that it does not affect future elements
  }

  /**
   * Receive notification of cdata.
   *
   * <p>The Parser will call this method to report each chunk of
   * character data.  SAX parsers may return all contiguous character
   * data in a single chunk, or they may split it into several
   * chunks; however, all of the characters in any single event
   * must come from the same external entity, so that the Locator
   * provides useful information.</p>
   *
   * <p>The application must not attempt to read from the array
   * outside of the specified range.</p>
   *
   * <p>Note that some parsers will report whitespace using the
   * ignorableWhitespace() method rather than this one (validating
   * parsers must do so).</p>
   *
   * @param ch The characters from the XML document.
   * @param start The start position in the array.
   * @param length The number of characters to read from the array.
   * @exception org.xml.sax.SAXException Any SAX exception, possibly
   *            wrapping another exception.
   * @see #ignorableWhitespace
   * @see org.xml.sax.Locator
   */
  public void cdata (char ch[], int start, int length)
    throws SAXException
  {
    if(m_inTemplate)
    {
      ElemTemplateElement parent = (ElemTemplateElement)m_elems.peek();
      boolean preserveSpace = false;
      boolean disableOutputEscaping = false;
      if(Constants.ELEMNAME_TEXT == parent.getXSLToken())
      {
        disableOutputEscaping = ((ElemText)parent).m_disableOutputEscaping;
        parent = (ElemTemplateElement)m_elems.elementAt(m_elems.size()-2);
        preserveSpace = true;
      }
      Locator locator = m_processor.m_stylesheetLocatorStack.isEmpty()
                        ? null :
                          ((Locator)m_processor.m_stylesheetLocatorStack.peek());
      int lineNumber = (null != locator) ? locator.getLineNumber() : 0;
      int columnNumber = (null != locator) ? locator.getColumnNumber() : 0;
      ElemTextLiteral elem = new ElemTextLiteral(m_processor,
                                                 m_stylesheet,
                                                 ch, start, length,
                                                 true, preserveSpace,
                                                 disableOutputEscaping,
                                                 lineNumber, columnNumber);
      boolean isWhite = isWhiteSpace(ch, start, length);
      if(preserveSpace || (!preserveSpace && !isWhite))
      {
        while(!m_whiteSpaceElems.isEmpty())
        {
          ElemTextLiteral whiteElem = (ElemTextLiteral)m_whiteSpaceElems.pop();
          parent.appendChild(whiteElem);
        }
        parent.appendChild(elem);
      }
      else if(isWhite)
      {
        boolean shouldPush = true;

        // Check out the previous element that was added to the
        // stylesheet tree to see if it was a literal text
        // element.
        Node last = parent.getLastChild();
        if(null != last)
        {
          ElemTemplateElement lastElem = (ElemTemplateElement)last;
          // If it was surrounded by xsl:text, it will count as an element.
          boolean isPrevCharData
            = Constants.ELEMNAME_TEXTLITERALRESULT == lastElem.getXSLToken();
          boolean isLastPoppedXSLText = (m_lastPopped != null) &&
              (Constants.ELEMNAME_TEXT == m_lastPopped.getXSLToken());
          if(isPrevCharData && !isLastPoppedXSLText)
          {
            parent.appendChild(elem);
            shouldPush = false;
          }
        }
        if(shouldPush)
          m_whiteSpaceElems.push(elem);
      }
    }
    // BEGIN SANJIVA CODE
    else if (m_inLXSLTScript)
    {
      m_LXSLTScriptBody.append (ch, start, length);
    }
    // END SANJIVA CODE
    // TODO: Flag error if text inside of stylesheet
    m_lastPopped = null;         // Reset this so that it does not affect future elements
  }

  /**
   * Receive notification of ignorable whitespace in element content.
   *
   * <p>Validating Parsers must use this method to report each chunk
   * of ignorable whitespace (see the W3C XML 1.0 recommendation,
   * section 2.10): non-validating parsers may also use this method
   * if they are capable of parsing and using content models.</p>
   *
   * <p>SAX parsers may return all contiguous whitespace in a single
   * chunk, or they may split it into several chunks; however, all of
   * the characters in any single event must come from the same
   * external entity, so that the Locator provides useful
   * information.</p>
   *
   * <p>The application must not attempt to read from the array
   * outside of the specified range.</p>
   *
   * @param ch The characters from the XML document.
   * @param start The start position in the array.
   * @param length The number of characters to read from the array.
   * @exception org.xml.sax.SAXException Any SAX exception, possibly
   *            wrapping another exception.
   * @see #characters
   */
  public void ignorableWhitespace (char ch[], int start, int length)
    throws SAXException
  {
    // Ignore!
    m_lastPopped = null;   // Reset this so that it does not affect future elements
  }


  /**
   * Receive notification of a processing instruction.
   *
   * <p>The Parser will invoke this method once for each processing
   * instruction found: note that processing instructions may occur
   * before or after the main document element.</p>
   *
   * <p>A SAX parser should never report an XML declaration (XML 1.0,
   * section 2.8) or a text declaration (XML 1.0, section 4.3.1)
   * using this method.</p>
   *
   * @param target The processing instruction target.
   * @param data The processing instruction data, or null if
   *        none was supplied.
   * @exception org.xml.sax.SAXException Any SAX exception, possibly
   *            wrapping another exception.
   */
  public void processingInstruction (String target, String data)
    throws SAXException
  {
    // No action for the moment.
//    m_lastPopped = null;    // Reset this so that it does not affect future elements
  }


  /**
   * Called when a Comment is to be constructed.
   * @param   data  The comment data.
   * @exception org.xml.sax.SAXException Any SAX exception, possibly
   *            wrapping another exception.
   */
  public void comment(String data) throws SAXException
  {
    // No action for the moment.
  }

  /**
   * Receive notivication of a entityReference.
   */
  public void entityReference(String name)
    throws SAXException
  {
    // No action for the moment.
  }

}
